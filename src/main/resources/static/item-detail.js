document.querySelectorAll('[data-quantity-selector]').forEach(form => {
    const input = form.querySelector('input[name="quantity"]');
    const decrease = form.querySelector('[data-step="-1"]');
    const increase = form.querySelector('[data-step="1"]');
    const submit = form.querySelector('button[type="submit"]');
    const total = form.querySelector('#detail-subtotal');
    const price = Number(form.dataset.price);
    const min = Number(input.min), max = Number(input.max);
    const format = new Intl.NumberFormat('ja-JP');

    function update() {
        const value = input.valueAsNumber;
        const valid = Number.isInteger(value) && value >= min && value <= max;
        decrease.disabled = input.disabled || !valid || value <= min;
        increase.disabled = input.disabled || !valid || value >= max;
        submit.disabled = input.disabled || !valid;
        if (total) total.value = valid ? format.format(price * value) : '—';
    }

    form.querySelectorAll('[data-step]').forEach(button => {
        button.addEventListener('click', () => {
            const current = Number.isFinite(input.valueAsNumber) ? input.valueAsNumber : min;
            input.value = Math.max(min, Math.min(max, current + Number(button.dataset.step)));
            update();
        });
    });
    input.addEventListener('input', update);
    input.addEventListener('change', () => {
        input.value = Math.max(min, Math.min(max, Math.trunc(input.valueAsNumber) || min));
        update();
    });
    update();
});
