package com.matech.audit.service.doc.model;

import com.matech.framework.pub.db.Table;

@Table(name="k_autocode",pk="id",insertPk=false)
public class AutoCodeVO {
	
	
		 protected Integer id ;
		 protected String atype ;
		 protected String aowner ;
		 protected Integer CurNum1 ;
		 protected Integer CurNum2 ;
		 protected Integer CurNum3 ;
		 protected Integer showlen1 ;
		 protected Integer showlen2 ;
		 protected Integer showlen3 ;
		 protected String format ;
		 protected String areaid;
		 protected String aname;


		 public Integer getId(){ return this.id; }
		 public void setId(Integer id){ this.id=id; }
		 public String getAtype(){ return this.atype; }
		 public void setAtype(String atype){ this.atype=atype; }
		 public String getAowner(){ return this.aowner; }
		 public void setAowner(String aowner){ this.aowner=aowner; }
		 public Integer getCurNum1(){ return this.CurNum1; }
		 public void setCurNum1(Integer CurNum1){ this.CurNum1=CurNum1; }
		 public Integer getCurNum2(){ return this.CurNum2; }
		 public void setCurNum2(Integer CurNum2){ this.CurNum2=CurNum2; }
		 public Integer getCurNum3(){ return this.CurNum3; }
		 public void setCurNum3(Integer CurNum3){ this.CurNum3=CurNum3; }
		 public Integer getShowlen1(){ return this.showlen1; }
		 public void setShowlen1(Integer showlen1){ this.showlen1=showlen1; }
		 public Integer getShowlen2(){ return this.showlen2; }
		 public void setShowlen2(Integer showlen2){ this.showlen2=showlen2; }
		 public Integer getShowlen3(){ return this.showlen3; }
		 public void setShowlen3(Integer showlen3){ this.showlen3=showlen3; }
		 public String getFormat(){ return this.format; }
		 public void setFormat(String format){ this.format=format; }
		public String getAreaid() {
			return areaid;
		}
		public void setAreaid(String areaid) {
			this.areaid = areaid;
		}
		public String getAname() {
			return aname;
		}
		public void setAname(String aname) {
			this.aname = aname;
		}
		 
		 
}
