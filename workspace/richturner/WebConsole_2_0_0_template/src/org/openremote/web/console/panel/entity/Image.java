package org.openremote.web.console.panel.entity;

public interface Image {
	Integer getId();
	String getSrc();
	Boolean getSystemImage();
	Link getLink();
	Include getInclude();
	
	void setId(Integer id);
	void setSrc(String src);
	void setSystemImage(Boolean systemImage);
	void setLink(Link link);
	void setInclude(Include include);
}
