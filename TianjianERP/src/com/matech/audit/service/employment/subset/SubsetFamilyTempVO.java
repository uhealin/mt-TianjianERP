package com.matech.audit.service.employment.subset;

import com.matech.framework.pub.db.Table;

@Table(name="oa_subset_family_temp",pk="uuid")
public class SubsetFamilyTempVO {
	
		
			 protected String uuid ;
			 protected String userid ;
			 protected String departmentid ;
			 protected String mainformid ;
			 protected String B0110 ;
			 protected String A0100 ;
			 protected String STATUS ;
			 protected String STATUS2 ;
			 protected Integer ID ;
			 protected String A7905 ;
			 protected String A7910 ;
			 protected String A7920 ;
			 protected Double A7940 ;


			 public String getUuid(){ return this.uuid; }
			 public void setUuid(String uuid){ this.uuid=uuid; }
			 public String getUserid(){ return this.userid; }
			 public void setUserid(String userid){ this.userid=userid; }
			 public String getDepartmentid(){ return this.departmentid; }
			 public void setDepartmentid(String departmentid){ this.departmentid=departmentid; }
			 public String getMainformid(){ return this.mainformid; }
			 public void setMainformid(String mainformid){ this.mainformid=mainformid; }
			 public String getB0110(){ return this.B0110; }
			 public void setB0110(String B0110){ this.B0110=B0110; }
			 public String getA0100(){ return this.A0100; }
			 public void setA0100(String A0100){ this.A0100=A0100; }
			 public String getSTATUS(){ return this.STATUS; }
			 public void setSTATUS(String STATUS){ this.STATUS=STATUS; }
			 public String getSTATUS2(){ return this.STATUS2; }
			 public void setSTATUS2(String STATUS2){ this.STATUS2=STATUS2; }
			 public Integer getID(){ return this.ID; }
			 public void setID(Integer ID){ this.ID=ID; }
			 public String getA7905(){ return this.A7905; }
			 public void setA7905(String A7905){ this.A7905=A7905; }
			 public String getA7910(){ return this.A7910; }
			 public void setA7910(String A7910){ this.A7910=A7910; }
			 public String getA7920(){ return this.A7920; }
			 public void setA7920(String A7920){ this.A7920=A7920; }
			 public Double getA7940(){ return this.A7940; }
			 public void setA7940(Double A7940){ this.A7940=A7940; }


}
