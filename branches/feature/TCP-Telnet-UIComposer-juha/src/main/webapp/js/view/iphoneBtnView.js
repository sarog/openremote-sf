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
            btn.appendTo($("#iphoneBtn_container"));
			IphoneBtnView.layout(iphoneBtn);
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

            IphoneBtnView.layout(iphoneBtn);

			var maxLenght = IphoneBtnView.getMaxLabelLength(iphoneBtn);
            btn.interceptStr({
                text: self.getModel().label,
                max: maxLenght,
                setText: function(str) {
                    $(this).find(".middle span").html(str);
                },
				setTitle:false
            });

            if (iphoneBtn.icon != null && iphoneBtn.icon.length > 0) {
                btn.find("table").removeClass("iPhone_btn_cont");
                btn.find("table .middle").html("<img src=" + iphoneBtn.icon + ">");
            }

        };
    }

	IphoneBtnView.getMaxLabelLength = function(iphoneBtn) {
		// if we haven't set Screen cell width ,set 5 as it's default value.
		if (ScreenView.cellWidth == -1) {
			return 5;
		}
		return Math.round(ScreenView.cellWidth * iphoneBtn.width/10);
	};
	
	IphoneBtnView.layout = function(iphoneBtn) {
		var btn = $("#"+iphoneBtn.getElementId());
		var tableCell = ScreenView.findCell(iphoneBtn.x, iphoneBtn.y);

        btn.css("top", $(tableCell).offset().top);
        btn.css("left", $(tableCell).offset().left);
        btn.css("height", $(tableCell).innerHeight() * iphoneBtn.height + iphoneBtn.height - 1);
        btn.css("width", $(tableCell).innerWidth() * iphoneBtn.width + iphoneBtn.width - 1);
		btn.find("td.middle").height($(tableCell).innerHeight() * iphoneBtn.height + iphoneBtn.height - 7);
	};
    return IphoneBtnView;
} ();