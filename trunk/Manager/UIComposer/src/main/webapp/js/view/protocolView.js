var KNXView = function() {
    function KNXView(knx) {
        self = this;
        var _model = knx;

        self.getModel = function() {
            return _model;
        };

        self.getElement = function() {
            return $("#" + self.getModel().getElementId());
        };

        var init = function() {
            var btn = HTMLBuilder.KNXBtnBuilder(self.getModel());
            var info = $("#knx_tab p");
            if (info.size() != 0) {
                info.remove();
            }
            btn.prependTo($("#knx_tab .item_container"));
        };
        init();
    }

    return KNXView;
} ();

var X10View = function() {
    function X10View(x10) {
        self = this;
        var _model = x10;

        self.getModel = function() {
            return _model;
        };

        self.getElement = function() {
            return $("#" + self.getModel().getElementId());
        };

        var init = function() {
            var btn = HTMLBuilder.X10BtnBuilder(self.getModel());
	        var info = $("#x10_tab p");
	        if(info.size()!=0){
	        	info.remove();
	        }
	        btn.prependTo($("#x10_tab .item_container"));
        };
        init();
    }

    return X10View;
} ();