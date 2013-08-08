package com.matech.audit.service.doc.model;

import com.matech.framework.pub.db.Table;


@Table(name="oa_doc_post_sign",pk="uuid")
public class DocPostSignVO {

	 protected String uuid ;
	 protected String doc_no ;
	 protected String signer_name ;
	 protected String sign_time ;
	 protected String sign_state ;
	 protected String signer_id ;
	 protected String ctype ;


	 public String getUuid(){ return this.uuid; }
	 public void setUuid(String uuid){ this.uuid=uuid; }
	 public String getDoc_no(){ return this.doc_no; }
	 public void setDoc_no(String doc_no){ this.doc_no=doc_no; }
	 public String getSigner_name(){ return this.signer_name; }
	 public void setSigner_name(String signer_name){ this.signer_name=signer_name; }
	 public String getSign_time(){ return this.sign_time; }
	 public void setSign_time(String sign_time){ this.sign_time=sign_time; }
	 public String getSign_state(){ return this.sign_state; }
	 public void setSign_state(String sign_state){ this.sign_state=sign_state; }
	 public String getSigner_id(){ return this.signer_id; }
	 public void setSigner_id(String signer_id){ this.signer_id=signer_id; }
	 public String getCtype(){ return this.ctype; }
	 public void setCtype(String ctype){ this.ctype=ctype; }


}
