package com.matech.audit.service.placard.model;

import java.util.List;

public class TreeNode {

	private boolean leaf;
	private List children;
	private String text;
	private String id;
	private boolean checked;

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public boolean isLeaf() {
		return leaf;
	}

	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}

	public List getChildren() {
		return children;
	}

	public void setChildren(List children) {
		this.children = children;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
