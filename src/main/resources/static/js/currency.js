(function () {
    'use strict';

    const STORAGE_KEY = 'hostal-currency';
    const SUPPORTED = ['PEN', 'USD', 'EUR'];
    const FALLBACK_RATES = { PEN: 1, USD: 0.294, EUR: 0.2573 };

    let rates = { ...FALLBACK_RATES };
    let currency = localStorage.getItem(STORAGE_KEY) || 'PEN';
    let ready = false;

    function convert(penAmount) {
        const amount = Number(penAmount);
        if (Number.isNaN(amount)) {
            return 0;
        }
        const rate = rates[currency] ?? 1;
        return amount * rate;
    }

    function format(penAmount) {
        const value = convert(penAmount);
        if (currency === 'PEN') {
            return `S/ ${Math.round(value)}`;
        }
        if (currency === 'USD') {
            return `$ ${value.toFixed(2)}`;
        }
        if (currency === 'EUR') {
            return `€ ${value.toFixed(2)}`;
        }
        return String(value);
    }

    function formatWithSuffix(penAmount, suffix) {
        return format(penAmount) + (suffix || '');
    }

    function getCurrency() {
        return currency;
    }

    function isReady() {
        return ready;
    }

    function applyToDocument() {
        document.querySelectorAll('[data-pen-amount]').forEach(el => {
            const pen = el.dataset.penAmount;
            const suffix = el.dataset.penSuffix || '';
            el.textContent = formatWithSuffix(pen, suffix);
        });
    }

    function syncSelector() {
        document.querySelectorAll('.currency-select').forEach(select => {
            select.value = currency;
        });
    }

    function setCurrency(code) {
        if (!SUPPORTED.includes(code)) {
            return;
        }
        currency = code;
        localStorage.setItem(STORAGE_KEY, code);
        syncSelector();
        applyToDocument();
        document.dispatchEvent(new CustomEvent('hostal:currency-change', {
            detail: { currency: code, rates: { ...rates } }
        }));
    }

    async function loadRates() {
        try {
            const response = await fetch('/api/tipos-cambio');
            if (!response.ok) {
                throw new Error('Respuesta inválida');
            }
            const data = await response.json();
            if (data.rates) {
                rates = { ...FALLBACK_RATES, ...data.rates };
            }
        } catch (error) {
            console.warn('No se pudieron cargar tasas de cambio. Se usan valores por defecto.', error);
        }
    }

    function bindSelectors() {
        document.querySelectorAll('.currency-select').forEach(select => {
            select.addEventListener('change', () => setCurrency(select.value));
        });
    }

    async function init() {
        syncSelector();
        bindSelectors();
        await loadRates();
        ready = true;
        applyToDocument();
        document.dispatchEvent(new CustomEvent('hostal:currency-ready', {
            detail: { currency, rates: { ...rates } }
        }));
    }

    window.HostalCurrency = {
        convert,
        format,
        formatWithSuffix,
        getCurrency,
        isReady,
        setCurrency,
        applyToDocument
    };

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})();
