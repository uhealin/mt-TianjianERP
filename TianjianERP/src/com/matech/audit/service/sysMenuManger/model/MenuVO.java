package com.matech.audit.service.sysMenuManger.model;

import com.matech.framework.pub.db.Table;

@Table(name="s_sysmenu",pk="id")
public class MenuVO {
	
		 protected Integer id ;
		 protected String menu_id ;
		 protected String parentid ;
		 protected Integer depth ;
		 protected String ctype ;
		 protected String name ;
		 protected String act ;
		 protected String target ;
		 protected String helpact ;
		 protected String isvalidate ;
		 protected String ActiveX_method ;
		 protected String POWER ;


		 public Integer getId(){ return this.id; }
		 public void setId(Integer id){ this.id=id; }
		 public String getMenu_id(){ return this.menu_id; }
		 public void setMenu_id(String menu_id){ this.menu_id=menu_id; }
		 public String getParentid(){ return this.parentid; }
		 public void setParentid(String parentid){ this.parentid=parentid; }
		 public Integer getDepth(){ return this.depth; }
		 public void setDepth(Integer depth){ this.depth=depth; }
		 public String getCtype(){ return this.ctype; }
		 public void setCtype(String ctype){ this.ctype=ctype; }
		 public String getName(){ return this.name; }
		 public void setName(String name){ this.name=name; }
		 public String getAct(){ return this.act; }
		 public void setAct(String act){ this.act=act; }
		 public String getTarget(){ return this.target; }
		 public void setTarget(String target){ this.target=target; }
		 public String getHelpact(){ return this.helpact; }
		 public void setHelpact(String helpact){ this.helpact=helpact; }
		 public String getIsvalidate(){ return this.isvalidate; }
		 public void setIsvalidate(String isvalidate){ this.isvalidate=isvalidate; }
		 public String getActiveX_method(){ return this.ActiveX_method; }
		 public void setActiveX_method(String ActiveX_method){ this.ActiveX_method=ActiveX_method; }
		 public String getPOWER(){ return this.POWER; }
		 public void setPOWER(String POWER){ this.POWER=POWER; }


}
