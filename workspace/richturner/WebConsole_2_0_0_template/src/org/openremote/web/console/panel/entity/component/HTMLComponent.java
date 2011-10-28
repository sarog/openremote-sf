package org.openremote.web.console.panel.entity.component;

import org.openremote.web.console.panel.entity.Navigate;

public interface HTMLComponent {
	Integer getId();
	String getHtml();
	
	void setId(Integer id);
	void setHtml(String html);
}
