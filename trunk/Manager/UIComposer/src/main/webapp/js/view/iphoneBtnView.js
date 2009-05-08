var IphoneBtnView = function() {
    function IphoneBtnView(iphoneBtn) {
        var self = this;
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

           
            return btn;
        };
        init();

        self.deleteView = function() {
			self.getModel().clearArea();
            self.getElement().remove();
			
        };

        self.updateView = function() {
			var iphoneBtn = self.getModel();
            iphoneBtn.fillArea();
            var btn = self.getElement();
			btn.removeAttr("style");

            var tableCell = ScreenView.findCell(iphoneBtn.x, iphoneBtn.y);

            btn.css("top", $(tableCell).offset().top);
            btn.css("left", $(tableCell).offset().left);
            btn.css("height", $(tableCell).height() * iphoneBtn.height + iphoneBtn.height - 1);
            btn.css("width", $(tableCell).width() * iphoneBtn.width + iphoneBtn.width - 1);

			
            btn.interceptStr({
                text: self.getModel().label,
                max: 5,
                setText: function(str) {
                    $(this).find(".middle span").html(str);
                },
				setTitle:false
            });

            if (iphoneBtn.icon != constant.DEFAULT_IPHONE_BTN_ICON && iphoneBtn.icon != "") {
                btn.find("table").removeClass("iPhone_btn_cont");
                btn.find("table .middle").html("<img src=" + iphoneBtn.icon + ">");
            }

        };
    }
    return IphoneBtnView;
} ();