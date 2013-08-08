package com.matech.audit.service.department.model;

import com.matech.framework.pub.db.Table;

@Table(name="k_department",pk="autoid")
public class KDepartmentVO {
	
		 protected Integer autoid ;
		 protected String departname ;
		 protected Integer parentid ;
		 protected String property ;
		 protected String Popedom ;
		 protected Integer level0 ;
		 protected String fullpath ;
		 protected String url ;
		 protected Integer ltype ;
		 protected String typeid ;
		 protected String address ;
		 protected String postalcode ;
		 protected String ProjectPopedom ;
		 protected String areaid ;
		 protected String isleaf ;
		 protected String rand0 ;
		 protected String enname ;
		 protected String projectapprove ;
		 protected String schedulingapprove ;
		 protected String costapprove ;
		 protected String realityapprove ;


		 public Integer getAutoid(){ return this.autoid; }
		 public void setAutoid(Integer autoid){ this.autoid=autoid; }
		 public String getDepartname(){ return this.departname; }
		 public void setDepartname(String departname){ this.departname=departname; }
		 public Integer getParentid(){ return this.parentid; }
		 public void setParentid(Integer parentid){ this.parentid=parentid; }
		 public String getProperty(){ return this.property; }
		 public void setProperty(String property){ this.property=property; }
		 public String getPopedom(){ return this.Popedom; }
		 public void setPopedom(String Popedom){ this.Popedom=Popedom; }
		 public Integer getLevel0(){ return this.level0; }
		 public void setLevel0(Integer level0){ this.level0=level0; }
		 public String getFullpath(){ return this.fullpath; }
		 public void setFullpath(String fullpath){ this.fullpath=fullpath; }
		 public String getUrl(){ return this.url; }
		 public void setUrl(String url){ this.url=url; }
		 public Integer getLtype(){ return this.ltype; }
		 public void setLtype(Integer ltype){ this.ltype=ltype; }
		 public String getTypeid(){ return this.typeid; }
		 public void setTypeid(String typeid){ this.typeid=typeid; }
		 public String getAddress(){ return this.address; }
		 public void setAddress(String address){ this.address=address; }
		 public String getPostalcode(){ return this.postalcode; }
		 public void setPostalcode(String postalcode){ this.postalcode=postalcode; }
		 public String getProjectPopedom(){ return this.ProjectPopedom; }
		 public void setProjectPopedom(String ProjectPopedom){ this.ProjectPopedom=ProjectPopedom; }
		 public String getAreaid(){ return this.areaid; }
		 public void setAreaid(String areaid){ this.areaid=areaid; }
		 public String getIsleaf(){ return this.isleaf; }
		 public void setIsleaf(String isleaf){ this.isleaf=isleaf; }
		 public String getRand0(){ return this.rand0; }
		 public void setRand0(String rand0){ this.rand0=rand0; }
		 public String getEnname(){ return this.enname; }
		 public void setEnname(String enname){ this.enname=enname; }
		 public String getProjectapprove(){ return this.projectapprove; }
		 public void setProjectapprove(String projectapprove){ this.projectapprove=projectapprove; }
		 public String getSchedulingapprove(){ return this.schedulingapprove; }
		 public void setSchedulingapprove(String schedulingapprove){ this.schedulingapprove=schedulingapprove; }
		 public String getCostapprove(){ return this.costapprove; }
		 public void setCostapprove(String costapprove){ this.costapprove=costapprove; }
		 public String getRealityapprove(){ return this.realityapprove; }
		 public void setRealityapprove(String realityapprove){ this.realityapprove=realityapprove; }


}
