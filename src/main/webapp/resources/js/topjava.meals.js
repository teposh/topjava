const mealAjaxUrl = "profile/meals/";

const ctx = {
    ajaxUrl: mealAjaxUrl
};

$(function () {
    makeEditable(
        $("#datatable").DataTable({
            "paging": false,
            "info": true,
            "columns": [
                {
                    "data": "dateTime"
                },
                {
                    "data": "description"
                },
                {
                    "data": "calories"
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

function getFilter() {
    return "?" + [
        "startDate=" + $("#startDate").val(),
        "startTime=" + $("#startTime").val(),
        "endDate=" + $("#endDate").val(),
        "endTime=" + $("#endTime").val()
    ].join("&");
}

function clearFilter() {
    $("#startDate").val("");
    $("#startTime").val("");
    $("#endDate").val("");
    $("#endTime").val("");
    updateTable();
}

function updateTable() {
    $.get(ctx.ajaxUrl + getFilter(), function (data) {
        ctx.datatableApi.clear().rows.add(data).draw();
    });
}
