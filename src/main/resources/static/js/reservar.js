(function () {
    'use strict';

    const MESES = [
        'Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio',
        'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'
    ];
    const DIAS = ['Lu', 'Ma', 'Mi', 'Ju', 'Vi', 'Sa', 'Do'];

    let checkin = null;
    let checkout = null;
    let numHuespedes = 1;
    let calendarMonth = new Date().getMonth();
    let calendarYear = new Date().getFullYear();
    let habitacionesDisponibles = [];
    let seleccion = {};

    const checkinInput = document.getElementById('checkinInput');
    const checkoutInput = document.getElementById('checkoutInput');
    const numHuespedesEl = document.getElementById('numHuespedes');
    const roomsList = document.getElementById('roomsList');
    const reservaSummary = document.getElementById('reservaSummary');
    const totalAmount = document.getElementById('totalAmount');
    const totalDetail = document.getElementById('totalDetail');
    const btnSiguiente1 = document.getElementById('btnSiguiente1');
    const calendarTitle = document.getElementById('calendarTitle');
    const calendarGrid = document.getElementById('calendarGrid');
    function getCsrfHeaders() {
        const token = document.querySelector('meta[name="_csrf"]')?.content;
        const header = document.querySelector('meta[name="_csrf_header"]')?.content;
        const headers = { 'Content-Type': 'application/json' };
        if (token && header) {
            headers[header] = token;
        }
        return headers;
    }

    function formatMoney(penAmount) {
        if (window.HostalCurrency) {
            return HostalCurrency.format(penAmount);
        }
        return `S/ ${Number(penAmount).toFixed(0)}`;
    }

    function formatDate(date) {
        const d = String(date.getDate()).padStart(2, '0');
        const m = String(date.getMonth() + 1).padStart(2, '0');
        const y = date.getFullYear();
        return `${d}/${m}/${y}`;
    }

    function toIsoDate(date) {
        const d = String(date.getDate()).padStart(2, '0');
        const m = String(date.getMonth() + 1).padStart(2, '0');
        return `${date.getFullYear()}-${m}-${d}`;
    }

    function sameDay(a, b) {
        return a && b &&
            a.getFullYear() === b.getFullYear() &&
            a.getMonth() === b.getMonth() &&
            a.getDate() === b.getDate();
    }

    function isInRange(date) {
        if (!checkin || !checkout) return false;
        return date > checkin && date < checkout;
    }

    function getNoches() {
        if (!checkin || !checkout) return 0;
        return Math.round((checkout - checkin) / (1000 * 60 * 60 * 24));
    }

    function tieneRangoCompleto() {
        return checkin && checkout && checkout > checkin;
    }

    function updateDateInputs() {
        checkinInput.value = checkin ? formatDate(checkin) : '';
        checkoutInput.value = checkout ? formatDate(checkout) : '';
        checkinInput.placeholder = checkin ? '' : 'Seleccionar';
        checkoutInput.placeholder = checkout ? '' : 'Seleccionar';
    }

    function renderCalendar() {
        calendarTitle.textContent = `${MESES[calendarMonth]} ${calendarYear}`;
        calendarGrid.innerHTML = '';

        DIAS.forEach(d => {
            const el = document.createElement('div');
            el.className = 'calendar-day-name';
            el.textContent = d;
            calendarGrid.appendChild(el);
        });

        const firstDay = new Date(calendarYear, calendarMonth, 1);
        let startWeekday = firstDay.getDay() - 1;
        if (startWeekday < 0) startWeekday = 6;

        for (let i = 0; i < startWeekday; i++) {
            const empty = document.createElement('div');
            empty.className = 'calendar-day empty';
            calendarGrid.appendChild(empty);
        }

        const daysInMonth = new Date(calendarYear, calendarMonth + 1, 0).getDate();
        const today = new Date();
        today.setHours(0, 0, 0, 0);

        for (let day = 1; day <= daysInMonth; day++) {
            const date = new Date(calendarYear, calendarMonth, day);
            const btn = document.createElement('button');
            btn.type = 'button';
            btn.className = 'calendar-day';
            btn.textContent = day;

            if (date < today) {
                btn.classList.add('disabled');
                btn.disabled = true;
            } else {
                if (sameDay(date, checkin)) btn.classList.add('range-start');
                if (sameDay(date, checkout)) btn.classList.add('range-end');
                if (isInRange(date)) btn.classList.add('in-range');
                if (sameDay(date, checkin) && sameDay(date, checkout)) {
                    btn.classList.add('range-start', 'range-end');
                }
                btn.addEventListener('click', () => onDayClick(date));
            }
            calendarGrid.appendChild(btn);
        }
    }

    function onDayClick(date) {
        if (!checkin || (checkin && checkout)) {
            checkin = new Date(date);
            checkout = null;
        } else if (date <= checkin) {
            checkin = new Date(date);
            checkout = null;
        } else {
            checkout = new Date(date);
        }

        updateDateInputs();
        renderCalendar();
        resetSeleccion();

        if (tieneRangoCompleto()) {
            buscarHabitaciones();
        } else {
            habitacionesDisponibles = [];
            mostrarMensajeInicial();
        }
    }

    function resetSeleccion() {
        seleccion = {};
        actualizarResumen();
    }

    function mostrarMensajeInicial() {
        if (!checkin) {
            roomsList.innerHTML = `
                <div class="empty-rooms-msg">
                    <i class="bi bi-calendar-range"></i>
                    <p>Selecciona las fechas de tu estadía para ver las habitaciones disponibles.</p>
                </div>`;
        } else {
            roomsList.innerHTML = `
                <div class="empty-rooms-msg">
                    <i class="bi bi-calendar-plus"></i>
                    <p>Selecciona la fecha de salida (check-out) para completar tu rango de estadía.</p>
                </div>`;
        }
    }

    async function buscarHabitaciones() {
        if (!tieneRangoCompleto()) {
            habitacionesDisponibles = [];
            mostrarMensajeInicial();
            return;
        }

        roomsList.innerHTML = `
            <div class="text-center py-4 text-muted">
                <div class="spinner-border spinner-border-sm text-primary"></div>
                <p class="mt-2 mb-0 small">Buscando habitaciones...</p>
            </div>`;

        try {
            const params = new URLSearchParams({
                checkin: toIsoDate(checkin),
                checkout: toIsoDate(checkout),
                huespedes: numHuespedes
            });
            const response = await fetch(`/api/disponibilidad?${params}`);
            if (!response.ok) throw new Error('Error en la consulta');
            habitacionesDisponibles = await response.json();
            ajustarSeleccion();
            renderRooms();
            actualizarResumen();
        } catch (e) {
            roomsList.innerHTML = `
                <div class="empty-rooms-msg">
                    <i class="bi bi-exclamation-circle"></i>
                    <p>Error al cargar habitaciones. Intenta de nuevo.</p>
                </div>`;
        }
    }

    function ajustarSeleccion() {
        Object.keys(seleccion).forEach(id => {
            const tipo = habitacionesDisponibles.find(h => h.idTipo === parseInt(id));
            if (!tipo) {
                delete seleccion[id];
            } else {
                seleccion[id] = Math.min(seleccion[id], tipo.habitacionesDisponibles);
                if (seleccion[id] <= 0) delete seleccion[id];
            }
        });
    }

    function renderRooms() {
        if (!tieneRangoCompleto()) {
            mostrarMensajeInicial();
            return;
        }

        if (habitacionesDisponibles.length === 0) {
            roomsList.innerHTML = `
                <div class="empty-rooms-msg">
                    <i class="bi bi-door-closed"></i>
                    <p>No hay habitaciones disponibles para las fechas y huéspedes seleccionados.</p>
                </div>`;
            return;
        }

        const noches = getNoches();
        roomsList.innerHTML = habitacionesDisponibles.map(tipo => {
            const cantidad = seleccion[tipo.idTipo] || 0;
            const precioNoche = parseFloat(tipo.precioBase);
            const subtotal = precioNoche * noches * cantidad;
            const maxDisp = tipo.habitacionesDisponibles;
            const selectedClass = cantidad > 0 ? 'selected' : '';

            return `
                <div class="room-select-card ${selectedClass}" data-tipo-id="${tipo.idTipo}">
                    <img src="${tipo.imagenUrl || ''}" alt="${tipo.nombreTipo}">
                    <div class="room-select-info">
                        <h6>${tipo.nombreTipo}</h6>
                        <p class="desc">${tipo.descripcion || ''}</p>
                        <div class="room-capacity">
                            <i class="bi bi-people"></i> Hasta ${tipo.capacidad} personas
                            <span class="ms-2 text-muted">(${maxDisp} disponible${maxDisp !== 1 ? 's' : ''})</span>
                        </div>
                        <div class="room-price-line">
                            ${formatMoney(precioNoche)} x ${noches} noches
                            ${cantidad > 0 ? `= <strong>${formatMoney(subtotal)}</strong>` : ''}
                        </div>
                    </div>
                    <div class="room-select-actions">
                        ${cantidad > 0 ? `<div class="room-total">Total: ${formatMoney(subtotal)}</div>` : '<div></div>'}
                        <div class="guest-stepper">
                            <button type="button" class="btn-menos" data-tipo="${tipo.idTipo}" ${cantidad <= 0 ? 'disabled' : ''}>−</button>
                            <span>${cantidad}</span>
                            <button type="button" class="btn-mas" data-tipo="${tipo.idTipo}" data-max="${maxDisp}" ${cantidad >= maxDisp ? 'disabled' : ''}>+</button>
                        </div>
                    </div>
                </div>`;
        }).join('');

        roomsList.querySelectorAll('.btn-mas').forEach(btn => {
            btn.addEventListener('click', () => {
                const id = parseInt(btn.dataset.tipo);
                const max = parseInt(btn.dataset.max);
                seleccion[id] = (seleccion[id] || 0) + 1;
                if (seleccion[id] > max) seleccion[id] = max;
                renderRooms();
                actualizarResumen();
            });
        });

        roomsList.querySelectorAll('.btn-menos').forEach(btn => {
            btn.addEventListener('click', () => {
                const id = parseInt(btn.dataset.tipo);
                seleccion[id] = (seleccion[id] || 0) - 1;
                if (seleccion[id] <= 0) delete seleccion[id];
                renderRooms();
                actualizarResumen();
            });
        });
    }

    function actualizarResumen() {
        const noches = getNoches();
        let totalHabitaciones = 0;
        let totalPagar = 0;

        Object.entries(seleccion).forEach(([id, cant]) => {
            const tipo = habitacionesDisponibles.find(h => h.idTipo === parseInt(id));
            if (tipo && cant > 0) {
                totalHabitaciones += cant;
                totalPagar += parseFloat(tipo.precioBase) * noches * cant;
            }
        });

        if (totalHabitaciones > 0 && noches > 0) {
            reservaSummary.style.display = 'block';
            totalAmount.textContent = formatMoney(totalPagar);
            totalDetail.textContent = `${noches} noche${noches !== 1 ? 's' : ''} • ${totalHabitaciones} habitación${totalHabitaciones !== 1 ? 'es' : ''}`;
            btnSiguiente1.disabled = false;
        } else {
            reservaSummary.style.display = 'none';
            btnSiguiente1.disabled = true;
        }
    }

    function goToStep(step) {
        [1, 2, 3, 4].forEach(s => {
            const panel = document.getElementById(`step${s}`);
            if (panel) panel.style.display = s === step ? 'block' : 'none';
        });

        document.querySelectorAll('.wizard-step').forEach(el => {
            const s = parseInt(el.dataset.step);
            el.classList.remove('active', 'completed');
            if (s === step) el.classList.add('active');
            else if (s < step) el.classList.add('completed');
        });

        window.scrollTo({ top: 0, behavior: 'smooth' });
    }

    function mostrarConfirmacion(codigoReserva, total) {
        document.getElementById('codigoReserva').textContent = codigoReserva;
        document.getElementById('confirmDetail').textContent =
            totalDetail.textContent + ' • Total: ' + formatMoney(total);
        goToStep(4);
    }

    function validarDatosPersonales() {
        const nombre = document.getElementById('clienteNombre').value.trim();
        const documento = document.getElementById('clienteDocumento').value.trim();
        const email = document.getElementById('clienteEmail').value.trim();
        const errorEl = document.getElementById('step2Error');
        errorEl.style.display = 'none';

        if (nombre.length < 3) {
            errorEl.textContent = 'Ingresa tu nombre completo.';
            errorEl.style.display = 'block';
            return false;
        }
        if (documento.length < 6) {
            errorEl.textContent = 'Ingresa un documento de identidad válido.';
            errorEl.style.display = 'block';
            return false;
        }
        if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
            errorEl.textContent = 'Ingresa un correo electrónico válido.';
            errorEl.style.display = 'block';
            return false;
        }
        return true;
    }

    function construirPayloadReserva() {
        const habitaciones = Object.entries(seleccion)
            .filter(([, cant]) => cant > 0)
            .map(([idTipo, cantidad]) => ({
                idTipo: parseInt(idTipo),
                cantidad
            }));

        const cardNumber = document.getElementById('cardNumber').value.replace(/\s/g, '');

        return {
            fechaCheckin: toIsoDate(checkin),
            fechaCheckout: toIsoDate(checkout),
            numHuespedes: numHuespedes,
            nombre: document.getElementById('clienteNombre').value.trim(),
            email: document.getElementById('clienteEmail').value.trim(),
            documento: document.getElementById('clienteDocumento').value.trim(),
            telefono: document.getElementById('clienteTelefono').value.trim() || null,
            tarjetaReferencia: cardNumber.slice(-4),
            habitaciones
        };
    }

    async function confirmarReserva() {
        const btn = document.getElementById('btnSiguiente3');
        const errorEl = document.getElementById('pagoError');
        errorEl.style.display = 'none';

        btn.disabled = true;
        btn.innerHTML = '<span class="spinner-border spinner-border-sm"></span> Procesando...';

        try {
            const response = await fetch('/api/reservas', {
                method: 'POST',
                headers: getCsrfHeaders(),
                body: JSON.stringify(construirPayloadReserva())
            });

            const data = await response.json();

            if (!response.ok) {
                throw new Error(data.error || 'No se pudo registrar la reserva.');
            }

            mostrarConfirmacion(data.codigoReserva, data.total);
        } catch (e) {
            mostrarErrorPago(e.message);
        } finally {
            btn.disabled = false;
            btn.innerHTML = 'Confirmar reserva <i class="bi bi-chevron-right"></i>';
        }
    }

    function validarTarjeta() {
        const nombre = document.getElementById('cardName').value.trim();
        const numero = document.getElementById('cardNumber').value.replace(/\s/g, '');
        const expiry = document.getElementById('cardExpiry').value.trim();
        const cvv = document.getElementById('cardCvv').value.trim();
        const errorEl = document.getElementById('pagoError');
        errorEl.textContent = '';
        errorEl.style.display = 'none';

        if (nombre.length < 3) {
            mostrarErrorPago('Ingresa el nombre completo como aparece en la tarjeta.');
            return false;
        }

        if (!/^\d{16}$/.test(numero)) {
            mostrarErrorPago('El número de tarjeta debe tener 16 dígitos.');
            return false;
        }

        if (!validarLuhn(numero)) {
            mostrarErrorPago('El número de tarjeta no es válido.');
            return false;
        }

        const expiryMatch = expiry.match(/^(\d{2})\/(\d{2})$/);
        if (!expiryMatch) {
            mostrarErrorPago('La fecha de vencimiento debe tener el formato MM/AA.');
            return false;
        }

        const month = parseInt(expiryMatch[1], 10);
        const year = 2000 + parseInt(expiryMatch[2], 10);
        if (month < 1 || month > 12) {
            mostrarErrorPago('El mes de vencimiento no es válido.');
            return false;
        }

        const now = new Date();
        const expiryDate = new Date(year, month, 0);
        if (expiryDate < now) {
            mostrarErrorPago('La tarjeta está vencida.');
            return false;
        }

        if (!/^\d{3,4}$/.test(cvv)) {
            mostrarErrorPago('El CVV debe tener 3 o 4 dígitos.');
            return false;
        }

        return true;
    }

    function mostrarErrorPago(msg) {
        const errorEl = document.getElementById('pagoError');
        errorEl.textContent = msg;
        errorEl.style.display = 'block';
    }

    function validarLuhn(numero) {
        let sum = 0;
        let alternate = false;
        for (let i = numero.length - 1; i >= 0; i--) {
            let n = parseInt(numero.charAt(i), 10);
            if (alternate) {
                n *= 2;
                if (n > 9) n -= 9;
            }
            sum += n;
            alternate = !alternate;
        }
        return sum % 10 === 0;
    }

    function formatCardNumber(input) {
        let value = input.value.replace(/\D/g, '').substring(0, 16);
        input.value = value.replace(/(\d{4})(?=\d)/g, '$1 ').trim();
    }

    function formatExpiry(input) {
        let value = input.value.replace(/\D/g, '').substring(0, 4);
        if (value.length >= 3) {
            input.value = value.substring(0, 2) + '/' + value.substring(2);
        } else {
            input.value = value;
        }
    }

    document.getElementById('btnMesAnterior').addEventListener('click', () => {
        calendarMonth--;
        if (calendarMonth < 0) { calendarMonth = 11; calendarYear--; }
        renderCalendar();
    });

    document.getElementById('btnMesSiguiente').addEventListener('click', () => {
        calendarMonth++;
        if (calendarMonth > 11) { calendarMonth = 0; calendarYear++; }
        renderCalendar();
    });

    document.getElementById('btnMenosHuespedes').addEventListener('click', () => {
        if (numHuespedes > 1) {
            numHuespedes--;
            numHuespedesEl.textContent = numHuespedes;
            if (tieneRangoCompleto()) buscarHabitaciones();
        }
    });

    document.getElementById('btnMasHuespedes').addEventListener('click', () => {
        if (numHuespedes < 20) {
            numHuespedes++;
            numHuespedesEl.textContent = numHuespedes;
            if (tieneRangoCompleto()) buscarHabitaciones();
        }
    });

    document.getElementById('btnBuscar').addEventListener('click', buscarHabitaciones);
    btnSiguiente1.addEventListener('click', () => goToStep(2));

    document.querySelectorAll('.btn-anterior').forEach(btn => {
        btn.addEventListener('click', () => goToStep(parseInt(btn.dataset.target)));
    });

    document.getElementById('btnSiguiente2').addEventListener('click', () => {
        if (validarDatosPersonales()) goToStep(3);
    });

    document.getElementById('btnSiguiente3').addEventListener('click', (e) => {
        e.preventDefault();
        if (validarTarjeta()) confirmarReserva();
    });

    document.getElementById('cardNumber').addEventListener('input', function () {
        formatCardNumber(this);
    });

    document.getElementById('cardExpiry').addEventListener('input', function () {
        formatExpiry(this);
    });

    document.getElementById('cardCvv').addEventListener('input', function () {
        this.value = this.value.replace(/\D/g, '').substring(0, 4);
    });

    document.addEventListener('hostal:currency-change', () => {
        if (habitacionesDisponibles.length > 0) {
            renderRooms();
        }
        actualizarResumen();
    });

    updateDateInputs();
    renderCalendar();
    mostrarMensajeInicial();
})();
