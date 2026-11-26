(() => {
    const dialog = document.getElementById('avatar-editor');
    if (!dialog || !window.createImageBitmap || !dialog.showModal) return;
    const form = document.getElementById('avatar-form');
    const fileInput = document.getElementById('avatar-file');
    const choose = document.getElementById('choose-avatar');
    const remove = document.getElementById('remove-avatar');
    const canvas = document.getElementById('avatar-canvas');
    const context = canvas.getContext('2d');
    if (!context) return;
    context.imageSmoothingQuality = 'high';
    const zoomInput = document.getElementById('avatar-zoom');
    const zoomValue = document.getElementById('avatar-zoom-value');
    const status = document.getElementById('avatar-status');
    const error = document.getElementById('avatar-editor-error');
    const save = document.getElementById('save-avatar');
    const cancel = document.getElementById('cancel-avatar');
    let image = null, zoom = 1, rotation = 0, offsetX = 0, offsetY = 0, busy = false;
    let selection = 0, pinch = null;
    const pointers = new Map();
    const clamp = (value, min, max) => Math.min(max, Math.max(min, value));
    document.querySelector('.avatar-controls').hidden = false;

    function message(element, text, isError = false) {
        element.textContent = text;
        element.classList.toggle('is-error', isError);
    }

    function render() {
        if (!image) return;
        const sideways = rotation % 180 !== 0;
        const width = sideways ? image.height : image.width;
        const height = sideways ? image.width : image.height;
        const scale = Math.max(512 / width, 512 / height) * zoom;
        offsetX = clamp(offsetX, -(width * scale - 512) / 2, (width * scale - 512) / 2);
        offsetY = clamp(offsetY, -(height * scale - 512) / 2, (height * scale - 512) / 2);
        context.clearRect(0, 0, 512, 512);
        context.save();
        context.translate(256 + offsetX, 256 + offsetY);
        context.rotate(rotation * Math.PI / 180);
        context.scale(scale, scale);
        context.drawImage(image, -image.width / 2, -image.height / 2);
        context.restore();
        zoomInput.value = zoom;
        zoomValue.value = `${Math.round(zoom * 100)}%`;
    }

    function reset() {
        zoom = 1; rotation = 0; offsetX = 0; offsetY = 0;
        render();
    }

    choose.addEventListener('click', () => { if (!busy) fileInput.click(); });
    fileInput.addEventListener('change', async () => {
        const file = fileInput.files[0];
        fileInput.value = '';
        if (!file) return;
        const current = ++selection;
        if (!['image/jpeg', 'image/png', 'image/webp'].includes(file.type)) {
            message(status, 'JPEG・PNG・WebPの画像を選んでください。', true); return;
        }
        if (file.size > 10 * 1024 * 1024) {
            message(status, '元の画像は10MB以内で選んでください。', true); return;
        }
        try {
            const next = await createImageBitmap(file, { imageOrientation: 'from-image' });
            if (current !== selection) { next.close(); return; }
            if (next.width * next.height > 40_000_000) {
                next.close(); throw new Error('画像が大きすぎます。小さくした画像を選んでください。');
            }
            image?.close(); image = next;
            message(error, ''); message(status, '');
            reset(); dialog.showModal();
        } catch (cause) {
            message(status, cause.message.includes('大きすぎ') ? cause.message : '画像を読み込めません。別の画像を選んでください。', true);
        }
    });

    zoomInput.addEventListener('input', () => { zoom = Number(zoomInput.value); render(); });
    document.getElementById('rotate-avatar').addEventListener('click', () => {
        rotation = (rotation + 90) % 360; offsetX = 0; offsetY = 0; render();
    });
    document.getElementById('reset-avatar').addEventListener('click', reset);
    cancel.addEventListener('click', () => { if (!busy) dialog.close(); });
    dialog.addEventListener('cancel', event => { if (busy) event.preventDefault(); });
    dialog.addEventListener('close', () => {
        // closeイベントより先に新しい画像で開き直されていたら、その画像は解放しない。
        if (dialog.open) return;
        pointers.clear(); pinch = null; image?.close(); image = null;
        choose.focus();
    });

    function point(event) { return { x: event.clientX, y: event.clientY }; }
    function pinchState() {
        const [a, b] = [...pointers.values()];
        return { distance: Math.hypot(a.x - b.x, a.y - b.y), zoom };
    }
    canvas.addEventListener('pointerdown', event => {
        if (busy || (event.pointerType === 'mouse' && event.button !== 0)) return;
        canvas.setPointerCapture(event.pointerId);
        pointers.set(event.pointerId, point(event));
        if (pointers.size === 2) pinch = pinchState();
    });
    canvas.addEventListener('pointermove', event => {
        const previous = pointers.get(event.pointerId);
        if (!previous || busy) return;
        pointers.set(event.pointerId, point(event));
        if (pointers.size === 1) {
            const ratio = 512 / canvas.getBoundingClientRect().width;
            offsetX += (event.clientX - previous.x) * ratio;
            offsetY += (event.clientY - previous.y) * ratio;
        } else if (pointers.size === 2 && pinch?.distance > 0) {
            zoom = clamp(pinch.zoom * pinchState().distance / pinch.distance, 1, 4);
        }
        render();
    });
    const release = event => {
        pointers.delete(event.pointerId);
        pinch = pointers.size === 2 ? pinchState() : null;
    };
    canvas.addEventListener('pointerup', release);
    canvas.addEventListener('pointercancel', release);
    canvas.addEventListener('lostpointercapture', release);
    canvas.addEventListener('keydown', event => {
        if (busy) return;
        const step = event.shiftKey ? 32 : 8;
        switch (event.key) {
            case 'ArrowLeft': offsetX -= step; break;
            case 'ArrowRight': offsetX += step; break;
            case 'ArrowUp': offsetY -= step; break;
            case 'ArrowDown': offsetY += step; break;
            case '+': case '=': zoom = clamp(zoom + 0.1, 1, 4); break;
            case '-': zoom = clamp(zoom - 0.1, 1, 4); break;
            default: return;
        }
        event.preventDefault(); render();
    });

    function setBusy(value) {
        busy = value;
        [choose, remove, save, cancel, zoomInput, document.getElementById('rotate-avatar'), document.getElementById('reset-avatar')]
            .forEach(element => { element.disabled = value; });
        save.textContent = value ? '保存中…' : 'この画像で保存';
    }

    async function send(url, data) {
        const csrf = form.querySelector('input[name="_csrf"]');
        if (!csrf) throw new Error('画面を再読み込みして、もう一度操作してください。');
        const response = await fetch(url, {
            method: 'POST', body: data, credentials: 'same-origin',
            headers: { [form.dataset.csrfHeader || 'X-CSRF-TOKEN']: csrf.value, Accept: 'application/json' }
        });
        if (response.status === 413) throw new Error('保存する画像は2MB以内にしてください。');
        if (response.redirected || response.status === 403 || response.status === 401) {
            throw new Error('画面の有効期限が切れました。再読み込みして、もう一度操作してください。');
        }
        if (!response.headers.get('content-type')?.includes('application/json')) {
            throw new Error('画像を保存できませんでした。時間をおいてもう一度お試しください。');
        }
        const result = await response.json();
        if (!response.ok) throw new Error(result.message || '画像を保存できませんでした。');
        return result;
    }

    function updateAvatars(url) {
        document.querySelectorAll('[data-avatar-container]').forEach(container => {
            const fallback = container.querySelector('[data-avatar-fallback]');
            let img = container.querySelector('img');
            if (url) {
                if (!img) { img = document.createElement('img'); img.alt = ''; container.prepend(img); }
                img.src = url;
            } else { img?.remove(); }
            fallback.hidden = Boolean(url);
        });
        remove.hidden = !url;
    }

    form.addEventListener('submit', async event => {
        event.preventDefault();
        if (busy || !image) return;
        setBusy(true); message(error, '');
        try {
            const blob = await new Promise(resolve => canvas.toBlob(resolve, 'image/png'));
            if (!blob) throw new Error('画像を作成できませんでした。もう一度お試しください。');
            const data = new FormData(form); data.append('avatar', blob, 'avatar.png');
            const result = await send(form.action, data);
            updateAvatars(result.avatarUrl); message(status, result.message); dialog.close();
        } catch (cause) { message(error, cause.message || '通信に失敗しました。もう一度お試しください。', true); }
        finally { setBusy(false); if (!dialog.open) choose.focus(); }
    });
    remove.addEventListener('click', async () => {
        if (busy) return;
        setBusy(true); message(status, '');
        try {
            const result = await send(remove.dataset.url, new FormData(form));
            updateAvatars(null); message(status, result.message);
        } catch (cause) { message(status, cause.message || '通信に失敗しました。もう一度お試しください。', true); }
        finally { setBusy(false); if (remove.hidden) choose.focus(); }
    });
})();
