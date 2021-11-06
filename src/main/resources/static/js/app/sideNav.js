/* Open the sidenav */

function openNav(id) {
    if (window.innerWidth > 1200)
    {
        document.getElementById(id).style.width = "15%";
        document.getElementById("grid").style.marginLeft = "15%"

    } else if (window.innerWidth > 800) {
        document.getElementById(id).style.width = "25%";
        document.getElementById("grid").style.marginLeft = "25%";

    } else {
        document.getElementById(id).style.width = "100%";
    }
}

/* Close/hide the sidenav */
function closeNav(id) {
    document.getElementById(id).style.width = "0";
    document.getElementById("grid").style.marginLeft="0"
}
