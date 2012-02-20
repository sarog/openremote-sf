package org.openremote.web.console.panel;

import org.openremote.web.console.service.AutoBeanService;

public class SystemPanel {
	private static Panel instance = null;
	private static final String jsonStr = "{\"screens\":{\"screen\":[{\"absolute\":[{\"height\":\"50\",\"width\":\"100%\",\"label\":{\"id\":108,\"text\":\"Controller List\",\"color\":\"#FFFFFF\",\"fontSize\":20},\"left\":\"0\",\"top\":\"0\"}],\"id\":50,\"name\":\"controllerlist\",\"list\":[{\"dataSource\":\"controllerCredentialsList\",\"height\":\"360\",\"width\":\"100%\",\"itemBindingObject\":\"controllerCredentials\",\"left\":\"0\",\"itemtemplate\":{\"absolute\":[{\"height\":\"50\",\"width\":\"270\",\"label\":{\"id\":110,\"text\":\"${url}\",\"color\":\"#FFFFFF\",\"fontSize\":13},\"left\":\"0\",\"top\":\"0\"},{\"height\":\"20\",\"button\":{\"id\":130,\"name\":\">\",\"navigate\":{\"to\":\"editcontroller\",\"data\":[{\"dataValuePair\":{\"name\":\"url\",\"value\":\"${url}\"}}]}},\"width\":\"20\",\"left\":\"285\",\"top\":\"15\"}],\"itemHeight\":\"50\"},\"top\":\"50\"}]},{\"absolute\":[{\"height\":\"50\",\"width\":\"100%\",\"label\":{\"id\":108,\"text\":\"Settings\",\"color\":\"#FFFFFF\",\"fontSize\":20},\"left\":\"0\",\"top\":\"0\"}],\"id\":51,\"form\":[{\"field\":[{\"name\":\"url\",\"inputType\":\"textbox\",\"optional\":false,\"validationString\":\"^(http|https)\\\\://.+/.+$\",\"label\":\"Controller URL:\"},{\"name\":\"defaultPanel\",\"inputType\":\"textbox\",\"optional\":true,\"label\":\"Default Panel Name:\"},{\"name\":\"username\",\"inputType\":\"textbox\",\"optional\":true,\"label\":\"Username:\"},{\"name\":\"password\",\"inputType\":\"password\",\"optional\":true,\"validationString\":\"^(\\\\w|\\\\d)+$\",\"label\":\"Password:\"}],\"dataSource\":\"defaultControllerCredentials\",\"height\":\"300\",\"button\":[{\"name\":\"SAVE\",\"type\":\"submit\"},{\"navigate\":{\"to\":\"controllerlist\"},\"type\":\"cancel\"}],\"width\":\"95%\",\"left\":\"2%\",\"top\":\"55\"}],\"name\":\"settings\"},{\"absolute\":[{\"height\":\"50\",\"width\":\"100%\",\"label\":{\"id\":115,\"text\":\"Login\",\"color\":\"#FFFFFF\",\"fontSize\":20},\"left\":\"0\",\"top\":\"0\"}],\"id\":52,\"name\":\"login\"},{\"absolute\":[{\"height\":\"50\",\"width\":\"100%\",\"label\":{\"id\":119,\"text\":\"Logout\",\"color\":\"#FFFFFF\",\"fontSize\":20},\"left\":\"0\",\"top\":\"0\"}],\"id\":53,\"name\":\"logout\"},{\"absolute\":[{\"height\":\"50\",\"width\":\"100%\",\"label\":{\"id\":100,\"text\":\"Edit/Add Controller\",\"color\":\"#FFFFFF\",\"fontSize\":20},\"left\":\"0\",\"top\":\"0\"}],\"id\":54,\"form\":[{\"field\":[{\"name\":\"url\",\"inputType\":\"textbox\",\"optional\":false,\"validationString\":\"^(http|https)\\\\://.+/.+$\",\"label\":\"Controller URL:\"},{\"name\":\"defaultPanel\",\"inputType\":\"textbox\",\"optional\":true,\"label\":\"Default Panel Name:\"},{\"name\":\"username\",\"inputType\":\"textbox\",\"optional\":true,\"label\":\"Username:\"},{\"name\":\"password\",\"inputType\":\"password\",\"optional\":true,\"validationString\":\"^(\\\\w|\\\\d)+$\",\"label\":\"Password:\"}],\"dataSource\":\"controllerCredentialsByUrl\",\"height\":\"300\",\"button\":[{\"name\":\"SAVE\",\"type\":\"submit\"},{\"navigate\":{\"to\":\"controllerlist\"},\"type\":\"cancel\"}],\"width\":\"95%\",\"left\":\"2%\",\"top\":\"55\"}],\"name\":\"editcontroller\"},{\"absolute\":[{\"height\":\"50\",\"width\":\"100%\",\"label\":{\"id\":120,\"text\":\"Select Panel\",\"color\":\"#FFFFFF\",\"fontSize\":20},\"left\":\"0\",\"top\":\"0\"},{\"height\":\"300\",\"width\":\"100%\",\"label\":{\"id\":121,\"text\":\"PANEL LIST HERE\",\"color\":\"#FFFFFF\",\"fontSize\":14},\"left\":\"0\",\"top\":\"55\"}],\"id\":55,\"name\":\"selectpanel\"}]},\"groups\":{\"group\":[{\"id\":2,\"tabbar\":{\"item\":[{\"name\":\"SEARCH\",\"navigate\":{\"action\":\"search\"}},{\"name\":\"+\",\"navigate\":{\"to\":\"editcontroller\"}},{\"name\":\"SETTINGS\",\"navigate\":{\"to\":\"settings\"}}]},\"name\":\"Default\",\"include\":[{\"ref\":50,\"type\":\"screen\"}]},{\"id\":3,\"name\":\"Edit Settings\",\"include\":[{\"ref\":51,\"type\":\"screen\"}]},{\"id\":4,\"name\":\"Login Logout\",\"include\":[{\"ref\":52,\"type\":\"screen\"},{\"ref\":53,\"type\":\"screen\"}]},{\"id\":5,\"name\":\"Edit Controller\",\"include\":[{\"ref\":54,\"type\":\"screen\"}]},{\"id\":6,\"tabbar\":{\"item\":[{\"name\":\"CONTROLLER LIST\",\"navigate\":{\"to\":\"controllerlist\"}},{\"name\":\"SETTINGS\",\"navigate\":{\"to\":\"settings\"}}]},\"name\":\"Panel List\",\"include\":[{\"ref\":55,\"type\":\"screen\"}]}]}}";
	
	private SystemPanel() {}
	
	public static Panel get() {
		if (instance == null) {
			  instance = AutoBeanService.getInstance().fromJsonString(Panel.class, jsonStr).as();
		}
		return instance;
		
//		if (instance == null && !isRunning) {
//		try {
//				new RequestBuilder(RequestBuilder.GET, "resources/systempanel.def").sendRequest("", new RequestCallback() {
//					  @Override
//					  public void onResponseReceived(Request req, Response resp) {
//						  instance = AutoBeanService.getInstance().fromJsonString(Panel.class, resp.getText()).as();
//						  isRunning = false;
//					  }
//		
//					  @Override
//					  public void onError(Request res, Throwable throwable) {
//					    String msg = throwable.getMessage();
//					  }
//					});
//			} catch (Exception e) {
//				String msg = e.getMessage();
//			}
//		}
//		return instance;
	}
}
