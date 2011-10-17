package org.openremote.modeler.irfileparser;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.user.client.rpc.IsSerializable;

//TODO not sure if this class will remain in this package. maybe move
// to the model package or a new one

public class IRCommandInfo extends BaseModel implements IsSerializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IRCommandInfo() {
	}

	public IRCommandInfo(String name, String code, String originalCode, String comment, CodeSetInfo codeSet) {
		setName(name);
		setCode(code);
		setOriginalCodeString(originalCode);
		setComment(comment);
		setCodeSet(codeSet);
	}

	public CodeSetInfo getCodeSet(){
		return get("codeSet");
	}
	
	public void setCodeSet(CodeSetInfo codeSet) {
		set("codeSet",codeSet);
		
	}

	public String getOriginalCodeString(){
		return get("originalCode");
	}
	
	public void setOriginalCodeString(String originalCode) {
		set("originalCode",originalCode);
		
	}

	public String getComment(){
		return get("comment");
	}
	
	public void setComment(String comment) {
		set("comment",comment);
		
	}
	
	public String getName() {
		return get("name");
	}
	
	public void setName(String name) {
		set("name", name);
	}

	public String getCode() {
		return get("code");
	}

	public void setCode(String code) {
		set("code", code);
	}

	

}
