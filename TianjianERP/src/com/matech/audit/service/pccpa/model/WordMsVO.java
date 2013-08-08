package com.matech.audit.service.pccpa.model;

import com.matech.framework.pub.db.Table;

@Table(name="tj_wordms",pk="id")
public class WordMsVO {
	 protected Integer id ;
	 protected String DocID ;
	 protected String HY ;
	 protected String QF ;
	 protected String HQ ;
	 protected String XMFZR ;
	 protected String BMFZR ;
	 protected String JSB ;
	 protected String DLHHR ;


	 public Integer getId(){ return this.id; }
	 public void setId(Integer id){ this.id=id; }
	 public String getDocID(){ return this.DocID; }
	 public void setDocID(String DocID){ this.DocID=DocID; }
	 public String getHY(){ return this.HY; }
	 public void setHY(String HY){ this.HY=HY; }
	 public String getQF(){ return this.QF; }
	 public void setQF(String QF){ this.QF=QF; }
	 public String getHQ(){ return this.HQ; }
	 public void setHQ(String HQ){ this.HQ=HQ; }
	 public String getXMFZR(){ return this.XMFZR; }
	 public void setXMFZR(String XMFZR){ this.XMFZR=XMFZR; }
	 public String getBMFZR(){ return this.BMFZR; }
	 public void setBMFZR(String BMFZR){ this.BMFZR=BMFZR; }
	 public String getJSB(){ return this.JSB; }
	 public void setJSB(String JSB){ this.JSB=JSB; }
	 public String getDLHHR(){ return this.DLHHR; }
	 public void setDLHHR(String DLHHR){ this.DLHHR=DLHHR; }
}
