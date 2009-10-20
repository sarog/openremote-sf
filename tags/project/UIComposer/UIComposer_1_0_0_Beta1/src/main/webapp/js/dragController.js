function initDraggableAndDroppable() {
    makeCommandBtnDraggable();
    makeTableCellDrappable();
    prepareMacroBtn();
    makeIphoneBtnDraggable();
    makeMacroPanelDraggabe();
}

function makeMacroPanelDraggabe() {
    $("#macro").draggable({
        cursor: 'move',
        handle: ".item_title"
    });
}

function makeCommandBtnDraggable(items) {
    var btns;
    if (typeof(items) == 'undefined') {
        btns = $(".blue_btn");
    } else {
        btns = items;
    }
    btns.draggable({
        zIndex: 2700,
        cursor: 'move',
        helper: 'clone',
        start: function(event, ui) {
            $(this).draggable('option', 'revert', false);
        }
    });
}

function makeIphoneBtnDraggable(items) {
    var btns;
    if (typeof(items) == 'undefined') {
        btns = $(".iphone_btn");
    } else {
        btns = $(items);
    }

    btns.draggable({
        cursor: 'hand',
        opacity: 0.75,
        start: function(event, ui) {
            $(this).draggable('option', 'revert', false);
        },
        containment: $("#dropable_table")
    });
}


function makeTableCellDrappable() {
    $("#dropable_table td").droppable({
        hoverClass: 'hiLight',
        accept: function(draggable) {
            if (draggable.hasClass("blue_btn") || draggable.hasClass("iphone_btn") || draggable.hasClass("knx_btn") || draggable.hasClass("x10_btn")) {
                return true;
            }
            return false;
        },
        drop: function(event, ui) {

            if ($(this).find(".iphone_btn").length > 0) {
                ui.draggable.draggable('option', 'revert', true);
            } else {
                if (ui.draggable.hasClass("command_btn")) {
                    if (typeof(ui.draggable.attr("eventId")) == "undefined") {
                        ui.draggable.attr("eventId", BUTTONID++);
                    }
                }
                var btn = HTMLBuilder.iphoneBtnBuilder(ui.draggable);
                btn.click(function() {
                    selectIphoneBtn(btn);
                    HTMLBuilder.iphoneBtnDeleteIconBuilder().appendTo($(this));
                });
                btn.appendTo(this);
                makeIphoneBtnDraggable(btn);
                if (ui.draggable.hasClass(".iphone_btn")) {
                    ui.draggable.hide("fast",
                    function() {
                        $(this).remove();
                    });
                }
            }
        }
    });
}




function selectIphoneBtn(btn) {
    $(".iphone_btn").removeClass("selected");
    $(".iphone_btn .delete_icon").remove();
    btn.addClass("selected");

}


