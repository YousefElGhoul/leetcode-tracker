const API_BASE = 'https://leetcode-tracker-api.yousefelghoul.me/';

async function register(username, password) {
    const response = await fetch(`${API_BASE}/api/v1/auth/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password })
    });
    const data = await response.json();
    if (!response.ok) throw new Error(data.message || `Registration failed (${response.status})`);
    localStorage.setItem('token', data.token);
    return data;
}

async function login(username, password) {
    const response = await fetch(`${API_BASE}/api/v1/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password })
    });
    const data = await response.json();
    if (!response.ok) throw new Error(data.message || `Login failed (${response.status})`);
    localStorage.setItem('token', data.token);
    return data;
}

function getToken() {
    return localStorage.getItem('token');
}

function hasToken() {
    return !!localStorage.getItem('token');
}

function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
}
