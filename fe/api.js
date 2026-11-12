async function getLeetCodeStats() {
    const username = localStorage.getItem('username');
    const response = await fetch(`https://leetcode-tracker-api.yousefelghoul.me/api/v1/tracker?username=${username}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });
    if (!response.ok) {
        throw new Error(`LeetCode API error! Status: ${response.status}`);
    }
    return response.json();
}

async function recordVisit() {
    const username = localStorage.getItem('username');
    const response = await fetch(`https://leetcode-tracker-api.yousefelghoul.me/api/v1/visit?username=${username}`,  {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        });
    if (!response.ok) {
        throw new Error(`LeetCode API error! Status: ${response.status}`);
    }
    updateLeetCodeStats();
    return response.json();
}

async function clearHeatmap() {
    const username = localStorage.getItem('username');
    const token = getToken();
    const response = await fetch(`https://leetcode-tracker-api.yousefelghoul.me/api/v1/clear?username=${username}`,  {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
    if (!response.ok) {
        throw new Error(`LeetCode API error! Status: ${response.status}`);
    }
    updateLeetCodeStats();
    return response.json();
}
