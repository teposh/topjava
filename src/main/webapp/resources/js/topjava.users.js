const userAjaxUrl = "admin/users/";

// https://stackoverflow.com/a/5064235/548473
const ctx = {
    ajaxUrl: userAjaxUrl
};

// $(document).ready(function () {
$(function () {
    makeEditable(
        $("#datatable").DataTable({
            "paging": false,
            "info": true,
            "columns": [
                {
                    "data": "name"
                },
                {
                    "data": "email"
                },
                {
                    "data": "roles"
                },
                {
                    "data": "enabled"
                },
                {
                    "data": "registered"
                },
                {
                    "defaultContent": "Edit",
                    "orderable": false
                },
                {
                    "defaultContent": "Delete",
                    "orderable": false
                }
            ],
            "order": [
                [
                    0,
                    "asc"
                ]
            ]
        })
    );
});

function setEnabled(e) {
    const checkbox = $(e);
    const closest = checkbox.closest("tr");
    const enabled = checkbox.prop("checked");
    const id = closest.attr("id");
    console.log(id, enabled)
    $.ajax({
        url: userAjaxUrl + id,
        type: "POST",
        data: {
            "enabled": enabled
        }
    }).done(function () {
        closest.toggleClass("disabled");
    });
}
