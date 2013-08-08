package com.matech.audit.service.doc.model;

import com.matech.audit.service.doc.DocPostService.Node;
import com.matech.framework.pub.db.Table;
import com.matech.framework.pub.util.property;

@Table(name="oa_doc_log",pk="uuid")
public class DocLogVO {
	 protected String uuid ;
	 protected String doc_no ;
	 protected String handler_id ;
	 protected String handler_name ;
	 protected String node_code ;
	 protected String handle_time ;
	 protected String remark ;
	 protected String node_name ;
	 protected String ctype ;
	 protected String public_ind ;
	 protected String state ;
     protected String doc_id;
     protected String pccpa_docid;
     
	 public String getUuid(){ return this.uuid; }
	 public void setUuid(String uuid){ this.uuid=uuid; }
	 public String getDoc_no(){ return this.doc_no; }
	 public void setDoc_no(String doc_no){ this.doc_no=doc_no; }
	 public String getHandler_id(){ return this.handler_id; }
	 public void setHandler_id(String handler_id){ this.handler_id=handler_id; }
	 public String getHandler_name(){ return this.handler_name; }
	 public void setHandler_name(String handler_name){ this.handler_name=handler_name; }
	 public String getNode_code(){ return this.node_code; }
	 public void setNode_code(String node_code){ this.node_code=node_code; }
	 public String getHandle_time(){ return this.handle_time; }
	 public void setHandle_time(String handle_time){ this.handle_time=handle_time; }
	 public String getRemark(){ return this.remark; }
	 public void setRemark(String remark){ this.remark=remark; }
	 public String getNode_name(){ return this.node_name; }
	 public void setNode_name(String node_name){ this.node_name=node_name; }
	 public String getCtype(){ return this.ctype; }
	 public void setCtype(String ctype){ this.ctype=ctype; }
	 public String getPublic_ind(){ return this.public_ind; }
	 public void setPublic_ind(String public_ind){ this.public_ind=public_ind; }
	 public String getState(){ return this.state; }
	 public void setState(String state){ this.state=state; }
	 
	 
	public String getPccpa_docid() {
		return pccpa_docid;
	}
	public void setPccpa_docid(String pccpa_docid) {
		this.pccpa_docid = pccpa_docid;
	}
	public String getDoc_id() {
		return doc_id;
	}
	public void setDoc_id(String doc_id) {
		this.doc_id = doc_id;
	}
	public void setNode(Node node){
		this.setNode_code(node.name());
		this.setNode_name(node.getName_cn());
	}
	
	
}
