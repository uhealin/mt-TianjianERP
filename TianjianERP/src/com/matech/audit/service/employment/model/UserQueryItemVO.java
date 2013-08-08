package com.matech.audit.service.employment.model;

import com.matech.framework.pub.db.Table;

@Table(name="oa_user_query_item", pk="uuid")
public class UserQueryItemVO {
	 protected String uuid ;
	 protected String mainformid ;
	 protected String table_name ;
	 protected String column_name ;
	 protected String logic ;
	 protected String condition_name ;
	 protected String operator ;


	 public String getUuid(){ return this.uuid; }
	 public void setUuid(String uuid){ this.uuid=uuid; }
	 public String getMainformid(){ return this.mainformid; }
	 public void setMainformid(String mainformid){ this.mainformid=mainformid; }
	 public String getTable_name(){ return this.table_name; }
	 public void setTable_name(String table_name){ this.table_name=table_name; }
	 public String getColumn_name(){ return this.column_name; }
	 public void setColumn_name(String column_name){ this.column_name=column_name; }
	 public String getLogic(){ return this.logic; }
	 public void setLogic(String logic){ this.logic=logic; }
	 public String getCondition_name(){ return this.condition_name; }
	 public void setCondition_name(String condition_name){ this.condition_name=condition_name; }
	 public String getOperator(){ return this.operator; }
	 public void setOperator(String operator){ this.operator=operator; }
}
