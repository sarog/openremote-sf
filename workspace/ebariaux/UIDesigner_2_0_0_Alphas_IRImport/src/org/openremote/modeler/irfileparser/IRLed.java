package org.openremote.modeler.irfileparser;

import com.extjs.gxt.ui.client.data.BaseModel;

public class IRLed extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String code;
	private String value;

	public IRLed() {
	}

	public IRLed(String code, String value) {
		this.code = code;
		this.value = value;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
