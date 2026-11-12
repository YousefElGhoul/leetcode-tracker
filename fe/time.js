const now = new Date();
const today = now
    .toISOString()
    .split('T')[0];
const yesterday = new Date(
        new Date(today)
            .setDate(new Date(today)
            .getDate() - 1)
    )
    .toISOString()
    .split('T')[0];


// Get current time and format
getTime = () => {
    const now = new Date();
    let min = now.getMinutes(),
        sec = now.getSeconds(),
        hour = now.getHours()

    return "" + 
        (hour < 10 ? ("0" + hour) : hour) + ":" + 
        (min < 10 ? ("0" + min) : min) + ":" + 
        (sec < 10 ? ("0" + sec) : sec);
}

function updateClock() {
    // Set up the clock
    document.getElementById("clock").innerHTML = getTime();
    // Set clock interval to tick clock
    setInterval( () => {
        document.getElementById("clock").innerHTML = getTime();
    },100);
}