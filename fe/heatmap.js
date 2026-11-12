function buildHistoryFromData(data) {
    if (!data || !data.heatmap) {
        return {};
    }

    const history = {};
    data.heatmap.forEach(entry => {
        history[entry.date] = {
            visited: entry.visited || false,
            solved: entry.solved || false
        };
    });

    return history;
}

function renderHeatmapMonth(data) {
    if (data === undefined) {
        return;
    }
    const calendar = document.getElementById("calendarHeatmap");
    const weekdays = document.getElementById("calendarWeekdays");
    calendar.innerHTML = "";
    weekdays.innerHTML = "<span>Sat</span><span>Sun</span><span>Mon</span><span>Tue</span><span>Wed</span><span>Thu</span><span>Fri</span>"

    const year = now.getFullYear();
    const month = now.getMonth();
    const daysInMonth = new Date(year, month + 1, 0).getDate();

    const history = buildHistoryFromData(data);

    // 1. Change Month
    const title = now.toLocaleString("default", { month: "long", year: "numeric" });
    document.getElementById("calendarMonthTitle").textContent = `Activity – ${title}`;

    // 2. Find what weekday the 1st of the month falls on
    const jsWeekday = new Date(year, month, 1).getDay(); // Sunday = 0
    const offset = (jsWeekday + 1) % 7; // Now Saturday = 0, Sunday = 1, ..., Friday = 6

    // 3. Add leading blank cells for alignment
    for (let i = 0; i < offset; i++) {
        const blank = document.createElement("div");
        blank.classList.add("empty-heatmap-cell");
        calendar.appendChild(blank);
    }

    // 4. Fill in actual days of the month
    let solvedDaysCount = 0;

    for (let day = 1; day <= daysInMonth; day++) {
        const dateStr = `${year}-${String(month + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
        const entry = history[dateStr] || {};

        const cell = document.createElement("div");
        cell.classList.add("heatmap-cell");

        if (entry.solved) {
        cell.classList.add("heatmap-solved");
        solvedDaysCount++;
        } else if (entry.visited) {
        cell.classList.add("heatmap-visited");
        }

        cell.title = `${dateStr} - ${entry.solved ? "Solved" : entry.visited ? "Visited" : "No activity"}`;
        calendar.appendChild(cell);
        
        if (dateStr === today) {
            cell.style.outline = "2.5px solid #8aadf4";
        }
    }
}

