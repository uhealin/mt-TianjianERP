package com.matech.audit.service.question.model;

import java.util.List;

public class TreeNode {
	
	private String id;
    private String text;
    private String value ;
    private boolean hasChildren;
    private List ChildNodes ;
    private String fullPath ;
    private String checkstate ;
    private String parentId ;
    private boolean showcheck ;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public boolean isHasChildren() {
		return hasChildren;
	}
	public void setHasChildren(boolean hasChildren) {
		this.hasChildren = hasChildren;
	}
	
	
	public String getFullPath() {
		return fullPath;
	}
	public void setFullPath(String fullPath) {
		this.fullPath = fullPath;
	}
	public String getCheckstate() {
		return checkstate;
	}
	public void setCheckstate(String checkstate) {
		this.checkstate = checkstate;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public boolean isShowcheck() {
		return showcheck;
	}
	public void setShowcheck(boolean showcheck) {
		this.showcheck = showcheck;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public List getChildNodes() {
		return ChildNodes;
	}
	public void setChildNodes(List childNodes) {
		ChildNodes = childNodes;
	}
    


}
