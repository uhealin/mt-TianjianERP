package com.matech.framework.servlet.extmenu;


import java.util.List;

public class Menu {
    private String id;
    private String text;
    private boolean leaf;
    private String cls;
    private List children;
    private String href;
    private String activeXMethod ;
    private String parentid;
    private String menuid;
    private String dogid ;
    
    
	public String getMenuid() {
		return menuid;
	}
	public void setMenuid(String menuid) {
		this.menuid = menuid;
	}
	public String getDogid() {
		return dogid;
	}
	public void setDogid(String dogid) {
		this.dogid = dogid;
	}
	public String getActiveXMethod() {
		return activeXMethod;
	}
	public void setActiveXMethod(String activeXMethod) {
		this.activeXMethod = activeXMethod;
	}
	public List getChildren() {
		return children;
	}
	public void setChildren(List children) {
		this.children = children;
	}
	public String getCls() {
		return cls;
	}
	public void setCls(String cls) {
		this.cls = cls;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public boolean isLeaf() {
		return leaf;
	}
	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
	public String getParentid() {
		return parentid;
	}
	public void setParentid(String parentid) {
		this.parentid = parentid;
	}

}