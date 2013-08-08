package com.matech.audit.service.education.model;

import com.matech.framework.pub.db.Table;

/**
 * 培训费用类
 * @author Administrator
 *
 */
@Table(name="k_train_fee",pk="uuid")
public class TrainVO {
	protected String uuid ;
	 protected String classId ;
	 protected String projectId ;
	 protected String startDate ;
	 protected String endDate ;
	 protected String period ;
	 protected Double placeFee ;
	 protected Double mealFee ;
	 protected Double quarterage ;
	 protected Double carFee ;
	 protected Double teachFee ;
	 protected Double otherFee ;
	 protected Double total ;
	 protected Double trainFee ;
	 protected String state ;


	 public String getUuid(){ return this.uuid; }
	 public void setUuid(String uuid){ this.uuid=uuid; }
	 public String getClassId(){ return this.classId; }
	 public void setClassId(String classId){ this.classId=classId; }
	 public String getProjectId(){ return this.projectId; }
	 public void setProjectId(String projectId){ this.projectId=projectId; }
	 public String getStartDate(){ return this.startDate; }
	 public void setStartDate(String startDate){ this.startDate=startDate; }
	 public String getEndDate(){ return this.endDate; }
	 public void setEndDate(String endDate){ this.endDate=endDate; }
	 public String getPeriod(){ return this.period; }
	 public void setPeriod(String period){ this.period=period; }
	 public Double getPlaceFee(){ return this.placeFee; }
	 public void setPlaceFee(Double placeFee){ this.placeFee=placeFee; }
	 public Double getMealFee(){ return this.mealFee; }
	 public void setMealFee(Double mealFee){ this.mealFee=mealFee; }
	 public Double getQuarterage(){ return this.quarterage; }
	 public void setQuarterage(Double quarterage){ this.quarterage=quarterage; }
	 public Double getCarFee(){ return this.carFee; }
	 public void setCarFee(Double carFee){ this.carFee=carFee; }
	 public Double getTeachFee(){ return this.teachFee; }
	 public void setTeachFee(Double teachFee){ this.teachFee=teachFee; }
	 public Double getOtherFee(){ return this.otherFee; }
	 public void setOtherFee(Double otherFee){ this.otherFee=otherFee; }
	 public Double getTotal(){ return this.total; }
	 public void setTotal(Double total){ this.total=total; }
	 public Double getTrainFee(){ return this.trainFee; }
	 public void setTrainFee(Double trainFee){ this.trainFee=trainFee; }
	 public String getState(){ return this.state; }
	 public void setState(String state){ this.state=state; }

}
