var EJSHelper = function() {
    return {
        updateView: function(url, containerId, model) {
            try {
                new EJS({
                    url: url
                }).update(containerId, model);
            } catch(e) {
                if (e instanceof Error) {
                    $.showErrorMsg(e.description);
                } else {
                    $.showErrorMsg("Can't Connect to server.");
                }
            }
        },
        render: function(url, model) {
            var html = "";
            try {
                if (model === undefined) {
                    html = new EJS({
                        url: url
                    }).render();
                } else {
                    html = new EJS({
                        url: url
                    }).render(model);
					
                }
            } catch(e) {
                if (e instanceof Error) {
                    $.showErrorMsg(e.description);
                } else {
                    $.showErrorMsg("Can't Connect to server.");
                }
            }

            return html;
        }
    };
} ();