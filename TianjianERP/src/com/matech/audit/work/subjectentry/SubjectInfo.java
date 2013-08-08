package com.matech.audit.work.subjectentry;

public class SubjectInfo {
	public String subjectid = "";
	public String accName = "";
	
	public String opt = "";
	public String assitemid = "";
	
	public String levelSubjectID = "";
	public String levelSubjectName = "";
	SubjectInfo(String subjectid,String accName){
		this.subjectid=subjectid;
		this.accName=accName;
	}
	
	SubjectInfo(String opt,String assitemid,String subjectid,String accName){
		this.opt=opt;
		this.assitemid=assitemid;
		this.subjectid=subjectid;
		this.accName=accName;
	}
	
	SubjectInfo(String opt,String assitemid,String subjectid,String accName,String levelSubjectID,String levelSubjectName){
		this.opt=opt;
		this.assitemid=assitemid;
		this.subjectid=subjectid;
		this.accName=accName;
		this.levelSubjectID=levelSubjectID;
		this.levelSubjectName=levelSubjectName;
	}
}
