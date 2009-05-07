var IphoneBtnView = function() {
    function IphoneBtnView(iphoneBtn) {
        self = this;
        var _model = iphoneBtn;
        self.getModel = function() {
            return _model;
        };

        self.getElement = function() {
            return $("#" + self.getModel().getElementId());
        };

        var init = function() {
			var iphoneBtn = self.getModel();
            iphoneBtn.fillArea();
            var btn = HTMLBuilder.iphoneBtnBuilder(iphoneBtn);

  			var tableCell = ScreenView.findCell(iphoneBtn.x, iphoneBtn.y);	

            btn.css("top", $(tableCell).offset().top);
            btn.css("left", $(tableCell).offset().left);
            btn.css("height", $(tableCell).height() * iphoneBtn.height + iphoneBtn.height - 1);
            btn.css("width", $(tableCell).width() * iphoneBtn.width + iphoneBtn.width - 1);
            btn.appendTo($("#iphoneBtn_container"));

            //Hack the JQuery Draggable,use can't draggable the button sidelong
            btn.find("div.ui-resizable-handle.ui-resizable-se").remove();
            return btn;
        };
        init();
    }
    return IphoneBtnView;
} ();