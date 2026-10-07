const API_BASE = '/api/v1';

async function api(path, options = {}) {
    const response = await fetch(API_BASE + path, {
        headers: { 'Content-Type': 'application/json' },
        ...options
    });
    if (response.status === 204) {
        return null;
    }
    const data = await response.json().catch(() => null);
    if (!response.ok) {
        const message = data && data.message ? data.message : 'เกิดข้อผิดพลาด (' + response.status + ')';
        throw new Error(message);
    }
    return data;
}

function getUser() {
    try {
        return JSON.parse(localStorage.getItem('user'));
    } catch (e) {
        return null;
    }
}

function setUser(user) {
    localStorage.setItem('user', JSON.stringify(user));
}

function logout() {
    localStorage.removeItem('user');
    location.href = '/login';
}

// ใช้กับหน้าที่ต้อง login ก่อน ถ้ายังไม่ login จะพาไปหน้า login
function requireLogin() {
    const user = getUser();
    if (!user) {
        location.href = '/login';
    }
    return user;
}

function renderNavUser() {
    const box = document.getElementById('nav-user');
    const user = getUser();
    if (user) {
        const name = user.fullName || user.username;
        const avatar = user.avatarUrl
            ? '<img class="avatar" src="' + escapeHtml(user.avatarUrl) + '" alt="">'
            : '<span class="avatar">' + escapeHtml(name.charAt(0).toUpperCase()) + '</span>';
        box.innerHTML = '<a class="user-chip" href="/profile" title="ดูโปรไฟล์">' + avatar
            + '<span class="user-text">'
            + '<span class="user-name">' + escapeHtml(name) + '</span>'
            + '<span class="user-level">Lv.' + (user.level || 1) + ' · ' + (user.totalScore || 0) + ' คะแนน</span>'
            + '</span></a>'
            + '<button class="nav-logout" onclick="logout()">ออกจากระบบ</button>';
    } else {
        box.innerHTML = '<a class="nav-button" href="/login">เข้าสู่ระบบ</a>'
            + '<a class="nav-button gold" href="/register">สมัครสมาชิก</a>';
    }
    markActiveLink();
}

// ไฮไลต์เมนูของหน้าที่เปิดอยู่
function markActiveLink() {
    document.querySelectorAll('.nav-links a').forEach(link => {
        if (link.getAttribute('href') === location.pathname) {
            link.classList.add('active');
            const dropdown = link.closest('.dropdown');
            if (dropdown) {
                dropdown.querySelector('.dropdown-toggle').classList.add('active');
            }
        }
    });
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text == null ? '' : String(text);
    return div.innerHTML;
}

function showMessage(elementId, text, isError) {
    const box = document.getElementById(elementId);
    box.textContent = text;
    box.className = isError ? 'message error' : 'message success';
}

function getQueryParam(name) {
    return new URLSearchParams(location.search).get(name);
}
