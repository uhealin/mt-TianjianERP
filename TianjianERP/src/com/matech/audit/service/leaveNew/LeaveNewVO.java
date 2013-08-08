package com.matech.audit.service.leaveNew;

import com.matech.framework.pub.db.Table;

@Table(name="k_leave_new",pk="uuid")
public class LeaveNewVO {
	
	 protected String uuid ;
	 protected String formid ;
	 protected String userid ;
	 protected String departmentid ;
	 protected String leave_type ;
	 protected String leave_begin_time ;
	 protected String leave_end_time ;
	 protected String leave_reason ;
	 protected String apply_time ;
	 protected String state ;
	 protected String remark ;
	 protected String leave_cancel_time ;
	 protected String leave_cancel_reason ;


	 public String getUuid(){ return this.uuid; }
	 public void setUuid(String uuid){ this.uuid=uuid; }
	 public String getFormid(){ return this.formid; }
	 public void setFormid(String formid){ this.formid=formid; }
	 public String getUserid(){ return this.userid; }
	 public void setUserid(String userid){ this.userid=userid; }
	 public String getDepartmentid(){ return this.departmentid; }
	 public void setDepartmentid(String departmentid){ this.departmentid=departmentid; }
	 public String getLeave_type(){ return this.leave_type; }
	 public void setLeave_type(String leave_type){ this.leave_type=leave_type; }
	 public String getLeave_begin_time(){ return this.leave_begin_time; }
	 public void setLeave_begin_time(String leave_begin_time){ this.leave_begin_time=leave_begin_time; }
	 public String getLeave_end_time(){ return this.leave_end_time; }
	 public void setLeave_end_time(String leave_end_time){ this.leave_end_time=leave_end_time; }
	 public String getLeave_reason(){ return this.leave_reason; }
	 public void setLeave_reason(String leave_reason){ this.leave_reason=leave_reason; }
	 public String getApply_time(){ return this.apply_time; }
	 public void setApply_time(String apply_time){ this.apply_time=apply_time; }
	 public String getState(){ return this.state; }
	 public void setState(String state){ this.state=state; }
	 public String getRemark(){ return this.remark; }
	 public void setRemark(String remark){ this.remark=remark; }
	 public String getLeave_cancel_time(){ return this.leave_cancel_time; }
	 public void setLeave_cancel_time(String leave_cancel_time){ this.leave_cancel_time=leave_cancel_time; }
	 public String getLeave_cancel_reason(){ return this.leave_cancel_reason; }
	 public void setLeave_cancel_reason(String leave_cancel_reason){ this.leave_cancel_reason=leave_cancel_reason; }
	
}
