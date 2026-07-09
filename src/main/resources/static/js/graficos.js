window.Graficos = (function () {
    function prepararCanvas(id) {
        const canvas = document.getElementById(id);
        if (!canvas) return null;

        const contenedor = canvas.parentElement;
        const ancho = contenedor.clientWidth || 600;
        const alto = contenedor.clientHeight || 280;
        canvas.width = ancho;
        canvas.height = alto;

        const ctx = canvas.getContext('2d');
        ctx.clearRect(0, 0, ancho, alto);
        ctx.font = '12px Arial';
        ctx.fillStyle = '#475569';
        return { canvas, ctx, ancho, alto };
    }

    function mayorValor(datos, minimo) {
        return Math.max(minimo || 1, ...datos);
    }

    function barras(id, etiquetas, datos, opciones) {
        const grafico = prepararCanvas(id);
        if (!grafico) return;

        const ctx = grafico.ctx;
        const ancho = grafico.ancho;
        const alto = grafico.alto;
        const margen = { arriba: 20, derecha: 18, abajo: 38, izquierda: 44 };
        opciones = opciones || {};
        const color = opciones.color || '#2563eb';
        const maximo = mayorValor(datos, opciones.maximo || 1);
        const areaAncho = ancho - margen.izquierda - margen.derecha;
        const areaAlto = alto - margen.arriba - margen.abajo;
        const paso = areaAncho / datos.length;
        const barraAncho = Math.max(12, paso * 0.55);

        ctx.strokeStyle = '#edf2f7';
        ctx.lineWidth = 1;
        for (let linea = 0; linea <= 4; linea++) {
            const y = margen.arriba + (areaAlto / 4) * linea;
            ctx.beginPath();
            ctx.moveTo(margen.izquierda, y);
            ctx.lineTo(ancho - margen.derecha, y);
            ctx.stroke();
        }

        ctx.strokeStyle = '#dbe3ee';
        ctx.beginPath();
        ctx.moveTo(margen.izquierda, margen.arriba);
        ctx.lineTo(margen.izquierda, alto - margen.abajo);
        ctx.lineTo(ancho - margen.derecha, alto - margen.abajo);
        ctx.stroke();

        datos.forEach((valor, i) => {
            const x = margen.izquierda + paso * i + (paso - barraAncho) / 2;
            const h = (valor / maximo) * areaAlto;
            const y = alto - margen.abajo - h;
            ctx.fillStyle = valor === 0 ? '#e2e8f0' : color;
            ctx.fillRect(x, y, barraAncho, h);

            ctx.fillStyle = '#64748b';
            ctx.textAlign = 'center';
            ctx.fillText(etiquetas[i], x + barraAncho / 2, alto - 14);

            if (valor > 0) {
                ctx.fillStyle = '#334155';
                ctx.font = '11px Arial';
                ctx.fillText(valor, x + barraAncho / 2, y - 6);
                ctx.font = '12px Arial';
            }
        });
    }

    function linea(id, etiquetas, datos, opciones) {
        const grafico = prepararCanvas(id);
        if (!grafico) return;

        const ctx = grafico.ctx;
        const ancho = grafico.ancho;
        const alto = grafico.alto;
        const margen = { arriba: 20, derecha: 18, abajo: 38, izquierda: 44 };
        opciones = opciones || {};
        const color = opciones.color || '#10b981';
        const maximo = mayorValor(datos, opciones.maximo || 1);
        const areaAncho = ancho - margen.izquierda - margen.derecha;
        const areaAlto = alto - margen.arriba - margen.abajo;

        ctx.strokeStyle = '#edf2f7';
        ctx.lineWidth = 1;
        for (let linea = 0; linea <= 4; linea++) {
            const y = margen.arriba + (areaAlto / 4) * linea;
            ctx.beginPath();
            ctx.moveTo(margen.izquierda, y);
            ctx.lineTo(ancho - margen.derecha, y);
            ctx.stroke();
        }

        ctx.strokeStyle = '#dbe3ee';
        ctx.beginPath();
        ctx.moveTo(margen.izquierda, margen.arriba);
        ctx.lineTo(margen.izquierda, alto - margen.abajo);
        ctx.lineTo(ancho - margen.derecha, alto - margen.abajo);
        ctx.stroke();

        const puntos = datos.map((valor, i) => {
            const x = margen.izquierda + (areaAncho / Math.max(1, datos.length - 1)) * i;
            const y = alto - margen.abajo - (valor / maximo) * areaAlto;
            return { x, y };
        });

        ctx.strokeStyle = color;
        ctx.lineWidth = 3;
        ctx.beginPath();
        puntos.forEach((p, i) => {
            if (i === 0) ctx.moveTo(p.x, p.y);
            else ctx.lineTo(p.x, p.y);
        });
        ctx.stroke();

        if (puntos.length > 1) {
            ctx.lineTo(puntos[puntos.length - 1].x, alto - margen.abajo);
            ctx.lineTo(puntos[0].x, alto - margen.abajo);
            ctx.closePath();
            ctx.fillStyle = 'rgba(16,185,129,0.08)';
            ctx.fill();
        }

        puntos.forEach((p, i) => {
            ctx.fillStyle = datos[i] === 0 ? '#cbd5e1' : color;
            ctx.beginPath();
            ctx.arc(p.x, p.y, 4, 0, Math.PI * 2);
            ctx.fill();
            ctx.fillStyle = '#64748b';
            ctx.textAlign = 'center';
            ctx.fillText(etiquetas[i], p.x, alto - 14);
        });
    }

    function circular(id, etiquetas, datos, opciones) {
        const grafico = prepararCanvas(id);
        if (!grafico) return;

        const ctx = grafico.ctx;
        const ancho = grafico.ancho;
        const alto = grafico.alto;
        opciones = opciones || {};
        const colores = opciones.colores || ['#2563eb', '#10b981', '#f59e0b', '#8b5cf6', '#ef4444'];
        const total = datos.reduce((suma, valor) => suma + valor, 0);
        const radio = Math.min(ancho, alto) * 0.28;
        const centroX = ancho * 0.38;
        const centroY = alto * 0.48;
        let inicio = -Math.PI / 2;

        if (total === 0) {
            ctx.fillStyle = '#e2e8f0';
            ctx.beginPath();
            ctx.arc(centroX, centroY, radio, 0, Math.PI * 2);
            ctx.fill();
            return;
        }

        datos.forEach((valor, i) => {
            const angulo = (valor / total) * Math.PI * 2;
            ctx.fillStyle = colores[i % colores.length];
            ctx.beginPath();
            ctx.moveTo(centroX, centroY);
            ctx.arc(centroX, centroY, radio, inicio, inicio + angulo);
            ctx.closePath();
            ctx.fill();
            inicio += angulo;
        });

        const leyendaX = ancho * 0.68;
        let leyendaY = alto * 0.25;
        etiquetas.forEach((texto, i) => {
            ctx.fillStyle = colores[i % colores.length];
            ctx.fillRect(leyendaX, leyendaY - 10, 12, 12);
            ctx.fillStyle = '#475569';
            ctx.textAlign = 'left';
            ctx.fillText(texto + ' (' + datos[i] + ')', leyendaX + 18, leyendaY);
            leyendaY += 24;
        });
    }

    return { barras, linea, circular };
})();
