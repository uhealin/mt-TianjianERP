package com.matech.framework.service.schedule.model;

import com.matech.framework.pub.db.Table;

@Table(name="s_schedule",pk="pk1",insertPk=false)
public class ScheduleVO {

	protected Integer pk1 ;
	 protected String cnname ;
	 protected String task ;
	 protected String stime ;
	 protected String etime ;
	 protected Integer period ;
	 protected Integer counter ;
	 protected String param ;
	 protected String exc_hours ;
	 protected String exc_minutes ;


	 public Integer getPk1(){ return this.pk1; }
	 public void setPk1(Integer pk1){ this.pk1=pk1; }
	 public String getCnname(){ return this.cnname; }
	 public void setCnname(String cnname){ this.cnname=cnname; }
	 public String getTask(){ return this.task; }
	 public void setTask(String task){ this.task=task; }
	 public String getStime(){ return this.stime; }
	 public void setStime(String stime){ this.stime=stime; }
	 public String getEtime(){ return this.etime; }
	 public void setEtime(String etime){ this.etime=etime; }
	 public Integer getPeriod(){ return this.period; }
	 public void setPeriod(Integer period){ this.period=period; }
	 public Integer getCounter(){ return this.counter; }
	 public void setCounter(Integer counter){ this.counter=counter; }
	 public String getParam(){ return this.param; }
	 public void setParam(String param){ this.param=param; }
	 public String getExc_hours(){ return this.exc_hours; }
	 public void setExc_hours(String exc_hours){ this.exc_hours=exc_hours; }
	 public String getExc_minutes(){ return this.exc_minutes; }
	 public void setExc_minutes(String exc_minutes){ this.exc_minutes=exc_minutes; }
}
