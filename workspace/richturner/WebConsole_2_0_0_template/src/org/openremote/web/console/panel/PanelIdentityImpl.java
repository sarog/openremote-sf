package org.openremote.web.console.panel;

public class PanelIdentityImpl implements PanelIdentity {
	private int id;
	private String name;
	
	public PanelIdentityImpl(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	@Override
	public int getId() {
		return id;
	}
	
	@Override
	public void setId(int id) {
		this.id = id;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
	}	
}
