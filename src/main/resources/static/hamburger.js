document.querySelectorAll('.header').forEach((header) => {
    const toggle = header.querySelector('.menu-toggle');
    const nav = header.querySelector('nav');
    if (!toggle || !nav) return;

    const mobile = window.matchMedia('(max-width: 1024px)');
    const setOpen = (open, restoreFocus = false) => {
        header.classList.toggle('is-menu-open', open);
        toggle.setAttribute('aria-expanded', String(open));
        toggle.setAttribute('aria-label', open ? 'メニューを閉じる' : 'メニューを開く');
        if (restoreFocus) toggle.focus();
    };

    header.classList.add('menu-ready');

    toggle.addEventListener('click', () => {
        setOpen(toggle.getAttribute('aria-expanded') !== 'true');
    });

    nav.addEventListener('click', (event) => {
        if (mobile.matches && event.target.closest('a')) setOpen(false, true);
    });

    document.addEventListener('click', (event) => {
        if (!header.contains(event.target)) setOpen(false);
    });

    header.addEventListener('keydown', (event) => {
        if (event.key === 'Escape' && toggle.getAttribute('aria-expanded') === 'true') {
            setOpen(false, true);
        }
    });

    header.addEventListener('focusout', (event) => {
        if (!header.contains(event.relatedTarget)) setOpen(false);
    });

    // 画面幅を切り替えた後に、非表示の要素へフォーカスを残さない。
    mobile.addEventListener('change', () => {
        const active = document.activeElement;
        setOpen(false);
        if (mobile.matches && nav.contains(active)) toggle.focus();
        if (!mobile.matches && active === toggle) nav.querySelector('a')?.focus();
    });
});
