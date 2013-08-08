package com.matech.audit.service.kdic.model;

import com.matech.framework.pub.db.Table;


@Table(name="k_dic",pk="Autoid",insertPk=false)
public class KDicVO {
	
	
		 protected Integer Autoid ;
		 protected String Name ;
		 protected String Value ;
		 protected String ctype ;
		 protected String userdata ;
		 protected String property ;
		 protected String ext_str1 ;
		 protected String ext_str2 ;
		 protected String ext_str3 ;
		 protected Integer ext_int1 ;
		 protected Integer ext_int2 ;
		 protected Integer ext_int3 ;


		 public Integer getAutoid(){ return this.Autoid; }
		 public void setAutoid(Integer Autoid){ this.Autoid=Autoid; }
		 public String getName(){ return this.Name; }
		 public void setName(String Name){ this.Name=Name; }
		 public String getValue(){ return this.Value; }
		 public void setValue(String Value){ this.Value=Value; }
		 public String getCtype(){ return this.ctype; }
		 public void setCtype(String ctype){ this.ctype=ctype; }
		 public String getUserdata(){ return this.userdata; }
		 public void setUserdata(String userdata){ this.userdata=userdata; }
		 public String getProperty(){ return this.property; }
		 public void setProperty(String property){ this.property=property; }
		 public String getExt_str1(){ return this.ext_str1; }
		 public void setExt_str1(String ext_str1){ this.ext_str1=ext_str1; }
		 public String getExt_str2(){ return this.ext_str2; }
		 public void setExt_str2(String ext_str2){ this.ext_str2=ext_str2; }
		 public String getExt_str3(){ return this.ext_str3; }
		 public void setExt_str3(String ext_str3){ this.ext_str3=ext_str3; }
		 public Integer getExt_int1(){ return this.ext_int1; }
		 public void setExt_int1(Integer ext_int1){ this.ext_int1=ext_int1; }
		 public Integer getExt_int2(){ return this.ext_int2; }
		 public void setExt_int2(Integer ext_int2){ this.ext_int2=ext_int2; }
		 public Integer getExt_int3(){ return this.ext_int3; }
		 public void setExt_int3(Integer ext_int3){ this.ext_int3=ext_int3; }


}
