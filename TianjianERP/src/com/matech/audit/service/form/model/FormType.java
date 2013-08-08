package com.matech.audit.service.form.model;

public class FormType {
	private String formTypeId;
	private String formTypeName;
	private String parentId;

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	private String property;

	public String getFormTypeId() {
		return formTypeId;
	}

	public void setFormTypeId(String formTypeId) {
		this.formTypeId = formTypeId;
	}

	public String getFormTypeName() {
		return formTypeName;
	}

	public void setFormTypeName(String formTypeName) {
		this.formTypeName = formTypeName;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}
}
