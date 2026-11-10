/* MovieTicket Admin — hành vi dùng chung cho các trang quản trị */
(function () {
    'use strict';

    function store(key, value) {
        try {
            if (value === undefined) return localStorage.getItem(key);
            localStorage.setItem(key, value);
        } catch (e) { return null; }
    }

    // ---- Sidebar trên mobile ----
    document.querySelectorAll('[data-sidebar-toggle]').forEach(function (btn) {
        btn.addEventListener('click', function () {
            document.body.classList.toggle('sidebar-open');
        });
    });
    var backdrop = document.querySelector('.sidebar-backdrop');
    if (backdrop) {
        backdrop.addEventListener('click', function () {
            document.body.classList.remove('sidebar-open');
        });
    }

    // ---- Xác nhận trước khi xóa / khóa ----
    document.addEventListener('submit', function (e) {
        var msg = e.target.getAttribute('data-confirm');
        if (msg && !window.confirm(msg)) e.preventDefault();
    }, true);
    document.addEventListener('click', function (e) {
        var link = e.target.closest('a[data-confirm]');
        if (link && !window.confirm(link.getAttribute('data-confirm'))) e.preventDefault();
    });

    // ---- Chuyển chế độ xem thẻ / bảng (ghi nhớ theo trang) ----
    document.querySelectorAll('[data-view-toggle]').forEach(function (group) {
        var key = 'admin-view:' + location.pathname;
        var buttons = group.querySelectorAll('[data-view]');
        var panels = document.querySelectorAll('[data-view-panel]');

        function show(view) {
            buttons.forEach(function (b) { b.classList.toggle('active', b.dataset.view === view); });
            panels.forEach(function (p) { p.hidden = p.dataset.viewPanel !== view; });
            store(key, view);
        }

        buttons.forEach(function (b) {
            b.addEventListener('click', function () { show(b.dataset.view); });
        });
        show(store(key) || group.dataset.viewToggle || 'table');
    });

    // ---- Tìm nhanh trong bảng / lưới thẻ ----
    document.querySelectorAll('[data-search-target]').forEach(function (input) {
        var selector = input.getAttribute('data-search-target');
        input.addEventListener('input', function () {
            var q = input.value.trim().toLowerCase();
            document.querySelectorAll(selector).forEach(function (row) {
                var hit = !q || row.textContent.toLowerCase().indexOf(q) !== -1;
                row.classList.toggle('hidden-by-search', !hit);
            });
        });
    });

    // ---- Lọc theo trạng thái phía trình duyệt ----
    document.querySelectorAll('[data-status-filter]').forEach(function (group) {
        var selector = group.getAttribute('data-status-filter');
        var buttons = group.querySelectorAll('[data-value]');
        buttons.forEach(function (btn) {
            btn.addEventListener('click', function () {
                var v = btn.dataset.value;
                buttons.forEach(function (b) { b.classList.toggle('active', b === btn); });
                document.querySelectorAll(selector).forEach(function (el) {
                    el.classList.toggle('hidden-by-filter', v !== '' && el.dataset.status !== v);
                });
            });
        });
    });

    // ---- Ô tải ảnh có xem trước ----
    document.querySelectorAll('[data-upload]').forEach(function (zone) {
        var input = zone.querySelector('input[type=file]');
        var thumb = zone.querySelector('.upload-thumb');
        var name = zone.querySelector('[data-upload-name]');
        var hint = zone.querySelector('[data-upload-hint]');
        var maxMb = parseFloat(zone.getAttribute('data-max-mb') || '0');
        if (!input) return;

        ['dragenter', 'dragover'].forEach(function (ev) {
            zone.addEventListener(ev, function () { zone.classList.add('dragover'); });
        });
        ['dragleave', 'drop'].forEach(function (ev) {
            zone.addEventListener(ev, function () { zone.classList.remove('dragover'); });
        });

        input.addEventListener('change', function () {
            var file = input.files && input.files[0];
            if (!file) return;
            if (!file.type.startsWith('image/')) {
                alert('Vui lòng chọn file ảnh!');
                input.value = '';
                return;
            }
            if (maxMb && file.size > maxMb * 1024 * 1024) {
                alert('Ảnh không được vượt quá ' + maxMb + 'MB!');
                input.value = '';
                return;
            }
            if (name) name.textContent = file.name;
            if (hint) hint.textContent = (file.size / 1024 / 1024).toFixed(2) + ' MB';
            if (thumb) {
                var reader = new FileReader();
                reader.onload = function (ev) {
                    thumb.innerHTML = '';
                    var img = document.createElement('img');
                    img.src = ev.target.result;
                    img.alt = 'Xem trước';
                    thumb.appendChild(img);
                };
                reader.readAsDataURL(file);
            }
        });
    });

    // ---- Tự ẩn thông báo thành công ----
    document.querySelectorAll('.alert-success[data-autohide]').forEach(function (el) {
        setTimeout(function () {
            if (window.bootstrap && bootstrap.Alert) bootstrap.Alert.getOrCreateInstance(el).close();
        }, 5000);
    });
})();
