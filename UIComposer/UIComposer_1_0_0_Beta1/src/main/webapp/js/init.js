$(document).ready(function() {
    $("#tabs").tabs();
    bindEvent();
    initDraggableAndDroppable();
    $("#saveBtn").unbind().bind("click", postData);
});
