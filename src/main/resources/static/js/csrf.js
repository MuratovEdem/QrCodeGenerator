// Утилиты для работы с CSRF
class CsrfUtils {
    constructor() {
        this.token = null;
        this.headerName = null;
        this.init();
    }

    init() {
        // Получаем CSRF токен из мета-тегов
        const tokenMeta = document.querySelector('meta[name="_csrf"]');
        const headerMeta = document.querySelector('meta[name="_csrf_header"]');

        if (tokenMeta && headerMeta) {
            this.token = tokenMeta.content;
            this.headerName = headerMeta.content;
        }

        // Настраиваем все формы
        this.setupForms();

        // Настраиваем AJAX запросы
        this.setupAjax();
    }

    setupForms() {
        // Все формы с методом POST, PUT, PATCH, DELETE должны иметь CSRF токен
        document.querySelectorAll('form').forEach(form => {
            const method = (form.getAttribute('method') || 'get').toLowerCase();

            if (['post', 'put', 'patch', 'delete'].includes(method)) {
                // Проверяем, есть ли уже CSRF токен
                const hasCsrfToken = Array.from(form.elements).some(
                    element => element.name === '_csrf'
                );

                if (!hasCsrfToken && this.token) {
                    const csrfInput = document.createElement('input');
                    csrfInput.type = 'hidden';
                    csrfInput.name = '_csrf';
                    csrfInput.value = this.token;
                    form.appendChild(csrfInput);
                }
            }
        });
    }

    setupAjax() {
        // Настройка для XMLHttpRequest
        const originalSend = XMLHttpRequest.prototype.send;
        const self = this;

        XMLHttpRequest.prototype.send = function(body) {
            if (self.token && self.headerName) {
                this.setRequestHeader(self.headerName, self.token);
            }
            return originalSend.call(this, body);
        };

        // Настройка для fetch API
        const originalFetch = window.fetch;
        window.fetch = function(url, options = {}) {
            options.headers = options.headers || {};
            if (self.token && self.headerName) {
                options.headers[self.headerName] = self.token;
            }
            return originalFetch(url, options);
        };

        // Настройка для jQuery (если используется)
        if (window.jQuery) {
            $.ajaxSetup({
                beforeSend: function(xhr) {
                    if (self.token && self.headerName) {
                        xhr.setRequestHeader(self.headerName, self.token);
                    }
                }
            });
        }
    }

    // Получить CSRF токен
    getToken() {
        return this.token;
    }

    // Получить заголовок для CSRF
    getHeader() {
        return this.headerName;
    }

    // Добавить CSRF токен к данным формы
    addCsrfToFormData(formData) {
        if (this.token) {
            formData.append('_csrf', this.token);
        }
        return formData;
    }

    // Добавить CSRF токен к объекту данных
    addCsrfToObject(data) {
        if (this.token && data && typeof data === 'object') {
            data._csrf = this.token;
        }
        return data;
    }
}

// Создаем глобальный экземпляр
window.csrfUtils = new CsrfUtils();