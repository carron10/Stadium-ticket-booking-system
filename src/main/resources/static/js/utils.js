
feather.replace();
const initialize = () => {
    initializeSimplebar();
    initializeSidebarCollapse();
}

const initializeSimplebar = () => {
    const simplebarElement = document.getElementsByClassName("js-simplebar")[0];

    if (simplebarElement) {
        const simplebarInstance = new SimpleBar(document.getElementsByClassName("js-simplebar")[0]);

        /* Recalculate simplebar on sidebar dropdown toggle */
        const sidebarDropdowns = document.querySelectorAll(".js-sidebar [data-bs-parent]");

        sidebarDropdowns.forEach(link => {
            link.addEventListener("shown.bs.collapse", () => {
                simplebarInstance.recalculate();
            });
            link.addEventListener("hidden.bs.collapse", () => {
                simplebarInstance.recalculate();
            });
        });
    }
}

const initializeSidebarCollapse = () => {
    const sidebarElement = document.getElementsByClassName("js-sidebar")[0];
    const sidebarToggleElement = document.getElementsByClassName("js-sidebar-toggle")[0];

    if (sidebarElement && sidebarToggleElement) {
        sidebarToggleElement.addEventListener("click", () => {
            sidebarElement.classList.toggle("collapsed");

            sidebarElement.addEventListener("transitionend", () => {
                window.dispatchEvent(new Event("resize"));
            });
        });
    }
}

// Wait until page is loaded
document.addEventListener("DOMContentLoaded", () => initialize());




function reloadPage(delay = 0) {
    setTimeout(() => {
        window.location.reload();
    }, delay)
}
function send(url, data, method) {
    return $.ajax({
        url: url,
        method: method,
        data: data
    });
}
function error(str, title) {
    Swal.fire({
        title:
            (title == undefined ? "Error" : title)
        , text: str,
        icon: 'error'
    })
    // $("#alert").modal("show").find('.modal-content').addClass(
    //     "bg-danger").removeClass("bg-primary").find(
    //         '.modal-body').html(
    //             "<b>" + str + "</b>");
    // $('#alert').find('.title').html('<b>' + (title == undefined ? "Error" : title) + '</b>').addClass('text-white');
}
function notify(str, title) {
    Success(str, title);
}
function success(str, title) {
    Success(str, title);
}
function Success(str, title) {
    Swal.fire({
        title:
            (title == undefined ? "Success!!" : title)
        , text: str,
        icon: 'success'
    })
    // $("#alert").modal("show").find('.modal-content').addClass(
    //     "bg-danger").removeClass("bg-primary").find(
    //         '.modal-body').html(
    //             "<b>" + str + "</b>");
    // $('#alert').find('.title').html('<b>' + (title == undefined ? "Error" : title) + '</b>').addClass('text-white');
}