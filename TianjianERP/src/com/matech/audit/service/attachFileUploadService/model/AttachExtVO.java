package com.matech.audit.service.attachFileUploadService.model;

import com.matech.framework.pub.db.Table;

@Table(name="k_attachext",pk="attachid")
public class AttachExtVO {
	protected String attachid ;
	 protected String attachname ;
	 protected String attachfile ;
	 protected String attachfilepath ;
	 protected String attachtype ;
	 protected String updateuser ;
	 protected String updatetime ;
	 protected String indextable ;
	 protected String indexmetadata ;
	 protected String indexid ;
	 protected String property ;
	 protected Integer filesize ;
	 protected String property1 ;


	 public String getAttachid(){ return this.attachid; }
	 public void setAttachid(String attachid){ this.attachid=attachid; }
	 public String getAttachname(){ return this.attachname; }
	 public void setAttachname(String attachname){ this.attachname=attachname; }
	 public String getAttachfile(){ return this.attachfile; }
	 public void setAttachfile(String attachfile){ this.attachfile=attachfile; }
	 public String getAttachfilepath(){ return this.attachfilepath; }
	 public void setAttachfilepath(String attachfilepath){ this.attachfilepath=attachfilepath; }
	 public String getAttachtype(){ return this.attachtype; }
	 public void setAttachtype(String attachtype){ this.attachtype=attachtype; }
	 public String getUpdateuser(){ return this.updateuser; }
	 public void setUpdateuser(String updateuser){ this.updateuser=updateuser; }
	 public String getUpdatetime(){ return this.updatetime; }
	 public void setUpdatetime(String updatetime){ this.updatetime=updatetime; }
	 public String getIndextable(){ return this.indextable; }
	 public void setIndextable(String indextable){ this.indextable=indextable; }
	 public String getIndexmetadata(){ return this.indexmetadata; }
	 public void setIndexmetadata(String indexmetadata){ this.indexmetadata=indexmetadata; }
	 public String getIndexid(){ return this.indexid; }
	 public void setIndexid(String indexid){ this.indexid=indexid; }
	 public String getProperty(){ return this.property; }
	 public void setProperty(String property){ this.property=property; }
	 public Integer getFilesize(){ return this.filesize; }
	 public void setFilesize(Integer filesize){ this.filesize=filesize; }
	 public String getProperty1(){ return this.property1; }
	 public void setProperty1(String property1){ this.property1=property1; }
	 
	 
	 
}
