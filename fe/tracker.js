function showTracker() {
    const dailyTracker = document.getElementById('daily-tracker');
    dailyTracker.style.display = 'flex';
    const statsContainer = document.getElementById('stats-container');
    statsContainer.style.display = 'flex';
}

function hideTracker() {
    const dailyTracker = document.getElementById('daily-tracker');
    dailyTracker.style.display = 'none';
    const statsContainer = document.getElementById('stats-container');
    statsContainer.style.display = 'none';
}

function updateLeetCodeStats() {
    if (!hasToken() || !hasUsername()) {
        changeToLogin()
        hideTracker();
        return;
    }
    showTracker()
    hideLoginOrSignup();

    getLeetCodeStats()
    .then(data => {
        // Update UI
        updateStats(data);
        updateDailyTracker(data);
        renderHeatmapMonth(data);
        })
    .catch(error => {
        console.error("Fetch error:", error);
    });
}

function updateStats(data) {
    // Set solved values
    document.getElementById('totalSolved').textContent = data.progress.all.solvedNum;
    document.getElementById('easySolved').textContent = data.progress.easy.solvedNum;
    document.getElementById('mediumSolved').textContent = data.progress.medium.solvedNum;
    document.getElementById('hardSolved').textContent = data.progress.hard.solvedNum;
    // Set slashes
    document.getElementById('slash1').textContent = "/"
    document.getElementById('slash2').textContent = "/"
    document.getElementById('slash3').textContent = "/"
    document.getElementById('slash4').textContent = "/"
    // Set total values
    document.getElementById('totalProblems').textContent = data.progress.all.totalNum;
    document.getElementById('totalEasy').textContent = data.progress.easy.totalNum;
    document.getElementById('totalMedium').textContent = data.progress.medium.totalNum;
    document.getElementById('totalHard').textContent = data.progress.hard.totalNum;
    // Update Progress Bar
    const progressPercent = Math.min((data.progress.all.solvedNum / data.progress.all.totalNum) * 100, 100);
    document.getElementById("progressBar").style.width = `${progressPercent}%`;
    document.getElementById("progressBar").title = `${progressPercent.toFixed(1)}%`;
}

function updateDailyTracker(data) {
    updateVisitStatus(data);
    updateSolvedStatus(data);
}

function updateVisitStatus(data) {
    if (data === undefined) {
        return;
    }
    const today = new Date().toISOString().split('T')[0];
    const todayEntry = data.heatmap.find(entry => entry.date === today);
    const visitedToday = todayEntry ? todayEntry.visited : false;

    const icon = document.getElementById("visitedIconCheck");

    if (visitedToday) {
        icon.className = "far fa-check-circle";
        icon.style.color = "#a6da95";
    } else {
        icon.className = "far fa-times-circle";
        icon.style.color = "#ed8796";
    }
}

function updateSolvedStatus(data) {
    if (data === undefined) {
        return;
    }
    const today = new Date().toISOString().split('T')[0];
    const todayEntry = data.heatmap.find(entry => entry.date === today);
    const solvedToday = todayEntry ? todayEntry.solved : false;

    const icon = document.getElementById("solvedIconCheck");

    if (solvedToday) {
        icon.className = "far fa-check-circle";
        icon.style.color = "#a6da95";
    } else {
        icon.className = "far fa-times-circle";
        icon.style.color = "#ed8796";
    }
}