package com.matech.audit.service.pccpa.model;

import com.matech.framework.pub.db.Table;

@Table(name="sr_department",pk="code")
public class SrDepartmentVO {

	
	
		 protected String CodeId ;
		 protected Integer codelevel ;
		 protected String code ;
		 protected String Description ;
		 protected String PPtr ;
		 protected String CPtr ;
		 protected String Spell ;
		 protected String Dtype ;
		 protected String flag ;
		 protected String Status ;
		 protected String shortname ;
		 protected String addcode ;
		 protected String UnitFlag ;
		 protected String alias1 ;
		 protected String alias2 ;
		 protected String alias3 ;
		 protected String alias4 ;
		 protected String alias5 ;


		 public String getCodeId(){ return this.CodeId; }
		 public void setCodeId(String CodeId){ this.CodeId=CodeId; }
		 public Integer getCodelevel(){ return this.codelevel; }
		 public void setCodelevel(Integer codelevel){ this.codelevel=codelevel; }
		 public String getCode(){ return this.code; }
		 public void setCode(String code){ this.code=code; }
		 public String getDescription(){ return this.Description; }
		 public void setDescription(String Description){ this.Description=Description; }
		 public String getPPtr(){ return this.PPtr; }
		 public void setPPtr(String PPtr){ this.PPtr=PPtr; }
		 public String getCPtr(){ return this.CPtr; }
		 public void setCPtr(String CPtr){ this.CPtr=CPtr; }
		 public String getSpell(){ return this.Spell; }
		 public void setSpell(String Spell){ this.Spell=Spell; }
		 public String getDtype(){ return this.Dtype; }
		 public void setDtype(String Dtype){ this.Dtype=Dtype; }
		 public String getFlag(){ return this.flag; }
		 public void setFlag(String flag){ this.flag=flag; }
		 public String getStatus(){ return this.Status; }
		 public void setStatus(String Status){ this.Status=Status; }
		 public String getShortname(){ return this.shortname; }
		 public void setShortname(String shortname){ this.shortname=shortname; }
		 public String getAddcode(){ return this.addcode; }
		 public void setAddcode(String addcode){ this.addcode=addcode; }
		 public String getUnitFlag(){ return this.UnitFlag; }
		 public void setUnitFlag(String UnitFlag){ this.UnitFlag=UnitFlag; }
		 public String getAlias1(){ return this.alias1; }
		 public void setAlias1(String alias1){ this.alias1=alias1; }
		 public String getAlias2(){ return this.alias2; }
		 public void setAlias2(String alias2){ this.alias2=alias2; }
		 public String getAlias3(){ return this.alias3; }
		 public void setAlias3(String alias3){ this.alias3=alias3; }
		 public String getAlias4(){ return this.alias4; }
		 public void setAlias4(String alias4){ this.alias4=alias4; }
		 public String getAlias5(){ return this.alias5; }
		 public void setAlias5(String alias5){ this.alias5=alias5; }


}
