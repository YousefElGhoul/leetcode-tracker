function hasUsername() {
    return localStorage.getItem('username') !== null;
}

function hideLoginOrSignup() {
    hideEmptyDisclaimer()
    const loginForm = document.getElementById('login');
    const signupForm = document.getElementById('register');
    signupForm.style.display = 'none';
    loginForm.style.display = 'none';
}

function hideEmptyDisclaimer() {
    const loginDisclaimer = document.getElementById('login-empty-disclaimer')
    const registerDisclaimer = document.getElementById('register-empty-disclaimer')
    loginDisclaimer.style.display = 'none'
    registerDisclaimer.style.display = 'none'
}

function changeToSignUp() {
    hideEmptyDisclaimer()
    const loginForm = document.getElementById('login');
    const signupForm = document.getElementById('register');
    signupForm.style.display = 'flex';
    loginForm.style.display = 'none';
    return signupForm;
}

function changeToLogin() {
    hideEmptyDisclaimer()
    const loginForm = document.getElementById('login');
    const signupForm = document.getElementById('register');
    loginForm.style.display = 'flex';
    signupForm.style.display = 'none';
    return loginForm;
}

async function handleUsernameLogin() {
    const username = document.getElementById('login-username-input').value.trim();
    const password = document.getElementById('login-password-input').value;
    const disclaimer = document.getElementById('login-empty-disclaimer')
    if (!username && !password) {
        disclaimer.style.display = 'block'
        disclaimer.innerText = "Empty Credentials"
        return
    }
    if (!username) {
        disclaimer.style.display = 'block'
        disclaimer.innerText = "Please insert a username."
        return
    }
    if (!password) {
        disclaimer.style.display = 'block'
        disclaimer.innerText = "Please insert a password."
        return
    }
    disclaimer.style.display = 'none'

    try {
        await login(username, password);
        localStorage.setItem('username', username);
        updateLeetCodeStats();
    } catch (error) {
        disclaimer.style.display = 'block'
        disclaimer.innerText = error.message || "Login failed"
    }
}

async function handleUsernameRegister() {
    const username = document.getElementById('register-username-input').value.trim();
    const password = document.getElementById('register-password-input').value;
    const disclaimer = document.getElementById('register-empty-disclaimer')
    if (!username && !password) {
        disclaimer.style.display = 'block'
        disclaimer.innerText = "Empty Credentials"
        return
    }
    if (!username) {
        disclaimer.style.display = 'block'
        disclaimer.innerText = "Please insert a username."
        return
    }
    if (!password) {
        disclaimer.style.display = 'block'
        disclaimer.innerText = "Please insert a password."
        return
    }
    disclaimer.style.display = 'none'

    try {
        await register(username, password);
        localStorage.setItem('username', username);
        updateLeetCodeStats();
    } catch (error) {
        disclaimer.style.display = 'block'
        disclaimer.innerText = error.message || "Registration failed"
    }
}

function logout() {
    localStorage.removeItem('username');
    localStorage.removeItem('token');
    updateLeetCodeStats();
}
