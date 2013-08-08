package com.matech.audit.service.car.model;

import com.matech.framework.pub.db.Table;

@Table(name="c_motorman",pk="uuid")
public class CarMotormanVO {

		
			 protected String uuid ;
			 protected String id ;
			 protected String name ;
			 protected String time ;
			 protected String phone ;
			 protected String permit ;
			 protected String registrationMark ;
			 protected String fettle ;
			 protected String departmentid ;


			 public String getUuid(){ return this.uuid; }
			 public void setUuid(String uuid){ this.uuid=uuid; }
			 public String getId(){ return this.id; }
			 public void setId(String id){ this.id=id; }
			 public String getName(){ return this.name; }
			 public void setName(String name){ this.name=name; }
			 public String getTime(){ return this.time; }
			 public void setTime(String time){ this.time=time; }
			 public String getPhone(){ return this.phone; }
			 public void setPhone(String phone){ this.phone=phone; }
			 public String getPermit(){ return this.permit; }
			 public void setPermit(String permit){ this.permit=permit; }
			 public String getRegistrationMark(){ return this.registrationMark; }
			 public void setRegistrationMark(String registrationMark){ this.registrationMark=registrationMark; }
			 public String getFettle(){ return this.fettle; }
			 public void setFettle(String fettle){ this.fettle=fettle; }
			 public String getDepartmentid(){ return this.departmentid; }
			 public void setDepartmentid(String departmentid){ this.departmentid=departmentid; }


}
