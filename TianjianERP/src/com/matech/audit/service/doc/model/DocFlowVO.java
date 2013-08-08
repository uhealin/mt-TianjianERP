package com.matech.audit.service.doc.model;

import com.matech.framework.pub.db.Table;

@Table(name="oa_doc_flow",pk="uuid")
public class DocFlowVO {

	 protected String uuid ;
	 protected String name ;
	 protected String code ;
	 protected String next_node_code ;
	 protected String is_end_ind ;
	 protected String is_start_ind ;
	 protected String ctype ;


	 public String getUuid(){ return this.uuid; }
	 public void setUuid(String uuid){ this.uuid=uuid; }
	 public String getName(){ return this.name; }
	 public void setName(String name){ this.name=name; }
	 public String getCode(){ return this.code; }
	 public void setCode(String code){ this.code=code; }
	 public String getNext_node_code(){ return this.next_node_code; }
	 public void setNext_node_code(String next_node_code){ this.next_node_code=next_node_code; }
	 public String getIs_end_ind(){ return this.is_end_ind; }
	 public void setIs_end_ind(String is_end_ind){ this.is_end_ind=is_end_ind; }
	 public String getIs_start_ind(){ return this.is_start_ind; }
	 public void setIs_start_ind(String is_start_ind){ this.is_start_ind=is_start_ind; }
	 public String getCtype(){ return this.ctype; }
	 public void setCtype(String ctype){ this.ctype=ctype; }

}
