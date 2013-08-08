package com.matech.audit.service.user.model;

import java.util.ArrayList;
import java.util.List;

import com.matech.framework.pub.db.Table;

@Table(name="s_user_menugroup",pk="uuid",excludeColumns={"childGroups"})
public class UserMenuGroupVO {
	 protected String uuid ;
	 protected String name ;
	 protected String parent_id ;
	 protected String menu_ids ;
	 protected String userid ;
	 protected Integer orderid;

     public List<UserMenuGroupVO> getChildGroups() {
		return childGroups;
	}
	public void setChildGroups(List<UserMenuGroupVO> childGroups) {
		this.childGroups = childGroups;
	}
	protected List<UserMenuGroupVO> childGroups=new ArrayList<UserMenuGroupVO>();
	 
	 public Integer getOrderid() {
		return orderid;
	}
	public void setOrderid(Integer orderid) {
		this.orderid = orderid;
	}
	public String getUuid(){ return this.uuid; }
	 public void setUuid(String uuid){ this.uuid=uuid; }
	 public String getName(){ return this.name; }
	 public void setName(String name){ this.name=name; }
	 public String getParent_id(){ return this.parent_id; }
	 public void setParent_id(String parent_id){ this.parent_id=parent_id; }
	 public String getMenu_ids(){ return this.menu_ids; }
	 public void setMenu_ids(String menu_ids){ this.menu_ids=menu_ids; }
	 public String getUserid(){ return this.userid; }
	 public void setUserid(String userid){ this.userid=userid; }
}
