package com.matech.audit.service.nianJian.model;

import com.matech.framework.pub.db.Table;

/**
 * 主要项目清单
 * @author Administrator
 *
 */
@Table(name="z_project_report",pk="uuid")
public class ProjectReport {
	protected String uuid ;
	 protected String mainformId ;
	 protected String prjId ;
	 protected String prjName ;
	 protected String reportId ;
	 protected String reportName ;


	 public String getUuid(){ return this.uuid; }
	 public void setUuid(String uuid){ this.uuid=uuid; }
	 public String getMainformId(){ return this.mainformId; }
	 public void setMainformId(String mainformId){ this.mainformId=mainformId; }
	 public String getPrjId(){ return this.prjId; }
	 public void setPrjId(String prjId){ this.prjId=prjId; }
	 public String getPrjName(){ return this.prjName; }
	 public void setPrjName(String prjName){ this.prjName=prjName; }
	 public String getReportId(){ return this.reportId; }
	 public void setReportId(String reportId){ this.reportId=reportId; }
	 public String getReportName(){ return this.reportName; }
	 public void setReportName(String reportName){ this.reportName=reportName; }
}
