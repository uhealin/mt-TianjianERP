package com.matech.audit.service.form.model;

import com.matech.framework.pub.db.Table;

@Table(name="mt_com_form",pk="UUID")
public class FormVO {	
protected String UUID ;
protected String NAME ;
protected String ENNAME ;
protected String TABLENAME ;
protected String TABLETYPE ;
protected String DEFINESTR ;
protected String EXTCLASS ;
protected String UDATE ;
protected String UNAME ;
protected String SELECTTYPE ;
protected String PROPERTY ;
protected String PARENTFORMID ;
protected String LISTSQL ;
protected String FORM_TYPE ;
protected String LISTHTML ;
protected String reftables ;
protected String thead ;
protected String where_00_name ;
protected String where_00_sql ;
protected String where_00_hiddenbtn ;
protected String where_00_hiddencol ;
protected String where_01_name ;
protected String where_01_sql ;
protected String where_01_hiddenbtn ;
protected String where_01_hiddencol ;
protected String where_02_name ;
protected String where_02_sql ;
protected String where_02_hiddenbtn ;
protected String where_02_hiddencol ;
protected String where_03_name ;
protected String where_03_sql ;
protected String where_03_hiddenbtn ;
protected String where_03_hiddencol ;
protected String where_04_name ;
protected String where_04_sql ;
protected String where_04_hiddenbtn ;
protected String where_04_hiddencol ;
protected String where_05_name ;
protected String where_05_sql ;
protected String where_05_hiddenbtn ;
protected String where_05_hiddencol ;
protected String where_06_name ;
protected String where_06_sql ;
protected String where_06_hiddenbtn ;
protected String where_06_hiddencol ;
protected String where_07_name ;
protected String where_07_sql ;
protected String where_07_hiddenbtn ;
protected String where_07_hiddencol ;
protected String where_08_name ;
protected String where_08_sql ;
protected String where_08_hiddenbtn ;
protected String where_08_hiddencol ;
protected String where_09_name ;
protected String where_09_sql ;
protected String where_09_hiddenbtn ;
protected String where_09_hiddencol ;


public String getUUID(){ return this.UUID; }
public void setUUID(String UUID){ this.UUID=UUID; }
public String getNAME(){ return this.NAME; }
public void setNAME(String NAME){ this.NAME=NAME; }
public String getENNAME(){ return this.ENNAME; }
public void setENNAME(String ENNAME){ this.ENNAME=ENNAME; }
public String getTABLENAME(){ return this.TABLENAME; }
public void setTABLENAME(String TABLENAME){ this.TABLENAME=TABLENAME; }
public String getTABLETYPE(){ return this.TABLETYPE; }
public void setTABLETYPE(String TABLETYPE){ this.TABLETYPE=TABLETYPE; }
public String getDEFINESTR(){ return this.DEFINESTR; }
public void setDEFINESTR(String DEFINESTR){ this.DEFINESTR=DEFINESTR; }
public String getEXTCLASS(){ return this.EXTCLASS; }
public void setEXTCLASS(String EXTCLASS){ this.EXTCLASS=EXTCLASS; }
public String getUDATE(){ return this.UDATE; }
public void setUDATE(String UDATE){ this.UDATE=UDATE; }
public String getUNAME(){ return this.UNAME; }
public void setUNAME(String UNAME){ this.UNAME=UNAME; }
public String getSELECTTYPE(){ return this.SELECTTYPE; }
public void setSELECTTYPE(String SELECTTYPE){ this.SELECTTYPE=SELECTTYPE; }
public String getPROPERTY(){ return this.PROPERTY; }
public void setPROPERTY(String PROPERTY){ this.PROPERTY=PROPERTY; }
public String getPARENTFORMID(){ return this.PARENTFORMID; }
public void setPARENTFORMID(String PARENTFORMID){ this.PARENTFORMID=PARENTFORMID; }
public String getLISTSQL(){ return this.LISTSQL; }
public void setLISTSQL(String LISTSQL){ this.LISTSQL=LISTSQL; }
public String getFORM_TYPE(){ return this.FORM_TYPE; }
public void setFORM_TYPE(String FORM_TYPE){ this.FORM_TYPE=FORM_TYPE; }
public String getLISTHTML(){ return this.LISTHTML; }
public void setLISTHTML(String LISTHTML){ this.LISTHTML=LISTHTML; }
public String getReftables(){ return this.reftables; }
public void setReftables(String reftables){ this.reftables=reftables; }
public String getThead(){ return this.thead; }
public void setThead(String thead){ this.thead=thead; }
public String getWhere_00_name(){ return this.where_00_name; }
public void setWhere_00_name(String where_00_name){ this.where_00_name=where_00_name; }
public String getWhere_00_sql(){ return this.where_00_sql; }
public void setWhere_00_sql(String where_00_sql){ this.where_00_sql=where_00_sql; }
public String getWhere_00_hiddenbtn(){ return this.where_00_hiddenbtn; }
public void setWhere_00_hiddenbtn(String where_00_hiddenbtn){ this.where_00_hiddenbtn=where_00_hiddenbtn; }
public String getWhere_00_hiddencol(){ return this.where_00_hiddencol; }
public void setWhere_00_hiddencol(String where_00_hiddencol){ this.where_00_hiddencol=where_00_hiddencol; }
public String getWhere_01_name(){ return this.where_01_name; }
public void setWhere_01_name(String where_01_name){ this.where_01_name=where_01_name; }
public String getWhere_01_sql(){ return this.where_01_sql; }
public void setWhere_01_sql(String where_01_sql){ this.where_01_sql=where_01_sql; }
public String getWhere_01_hiddenbtn(){ return this.where_01_hiddenbtn; }
public void setWhere_01_hiddenbtn(String where_01_hiddenbtn){ this.where_01_hiddenbtn=where_01_hiddenbtn; }
public String getWhere_01_hiddencol(){ return this.where_01_hiddencol; }
public void setWhere_01_hiddencol(String where_01_hiddencol){ this.where_01_hiddencol=where_01_hiddencol; }
public String getWhere_02_name(){ return this.where_02_name; }
public void setWhere_02_name(String where_02_name){ this.where_02_name=where_02_name; }
public String getWhere_02_sql(){ return this.where_02_sql; }
public void setWhere_02_sql(String where_02_sql){ this.where_02_sql=where_02_sql; }
public String getWhere_02_hiddenbtn(){ return this.where_02_hiddenbtn; }
public void setWhere_02_hiddenbtn(String where_02_hiddenbtn){ this.where_02_hiddenbtn=where_02_hiddenbtn; }
public String getWhere_02_hiddencol(){ return this.where_02_hiddencol; }
public void setWhere_02_hiddencol(String where_02_hiddencol){ this.where_02_hiddencol=where_02_hiddencol; }
public String getWhere_03_name(){ return this.where_03_name; }
public void setWhere_03_name(String where_03_name){ this.where_03_name=where_03_name; }
public String getWhere_03_sql(){ return this.where_03_sql; }
public void setWhere_03_sql(String where_03_sql){ this.where_03_sql=where_03_sql; }
public String getWhere_03_hiddenbtn(){ return this.where_03_hiddenbtn; }
public void setWhere_03_hiddenbtn(String where_03_hiddenbtn){ this.where_03_hiddenbtn=where_03_hiddenbtn; }
public String getWhere_03_hiddencol(){ return this.where_03_hiddencol; }
public void setWhere_03_hiddencol(String where_03_hiddencol){ this.where_03_hiddencol=where_03_hiddencol; }
public String getWhere_04_name(){ return this.where_04_name; }
public void setWhere_04_name(String where_04_name){ this.where_04_name=where_04_name; }
public String getWhere_04_sql(){ return this.where_04_sql; }
public void setWhere_04_sql(String where_04_sql){ this.where_04_sql=where_04_sql; }
public String getWhere_04_hiddenbtn(){ return this.where_04_hiddenbtn; }
public void setWhere_04_hiddenbtn(String where_04_hiddenbtn){ this.where_04_hiddenbtn=where_04_hiddenbtn; }
public String getWhere_04_hiddencol(){ return this.where_04_hiddencol; }
public void setWhere_04_hiddencol(String where_04_hiddencol){ this.where_04_hiddencol=where_04_hiddencol; }
public String getWhere_05_name(){ return this.where_05_name; }
public void setWhere_05_name(String where_05_name){ this.where_05_name=where_05_name; }
public String getWhere_05_sql(){ return this.where_05_sql; }
public void setWhere_05_sql(String where_05_sql){ this.where_05_sql=where_05_sql; }
public String getWhere_05_hiddenbtn(){ return this.where_05_hiddenbtn; }
public void setWhere_05_hiddenbtn(String where_05_hiddenbtn){ this.where_05_hiddenbtn=where_05_hiddenbtn; }
public String getWhere_05_hiddencol(){ return this.where_05_hiddencol; }
public void setWhere_05_hiddencol(String where_05_hiddencol){ this.where_05_hiddencol=where_05_hiddencol; }
public String getWhere_06_name(){ return this.where_06_name; }
public void setWhere_06_name(String where_06_name){ this.where_06_name=where_06_name; }
public String getWhere_06_sql(){ return this.where_06_sql; }
public void setWhere_06_sql(String where_06_sql){ this.where_06_sql=where_06_sql; }
public String getWhere_06_hiddenbtn(){ return this.where_06_hiddenbtn; }
public void setWhere_06_hiddenbtn(String where_06_hiddenbtn){ this.where_06_hiddenbtn=where_06_hiddenbtn; }
public String getWhere_06_hiddencol(){ return this.where_06_hiddencol; }
public void setWhere_06_hiddencol(String where_06_hiddencol){ this.where_06_hiddencol=where_06_hiddencol; }
public String getWhere_07_name(){ return this.where_07_name; }
public void setWhere_07_name(String where_07_name){ this.where_07_name=where_07_name; }
public String getWhere_07_sql(){ return this.where_07_sql; }
public void setWhere_07_sql(String where_07_sql){ this.where_07_sql=where_07_sql; }
public String getWhere_07_hiddenbtn(){ return this.where_07_hiddenbtn; }
public void setWhere_07_hiddenbtn(String where_07_hiddenbtn){ this.where_07_hiddenbtn=where_07_hiddenbtn; }
public String getWhere_07_hiddencol(){ return this.where_07_hiddencol; }
public void setWhere_07_hiddencol(String where_07_hiddencol){ this.where_07_hiddencol=where_07_hiddencol; }
public String getWhere_08_name(){ return this.where_08_name; }
public void setWhere_08_name(String where_08_name){ this.where_08_name=where_08_name; }
public String getWhere_08_sql(){ return this.where_08_sql; }
public void setWhere_08_sql(String where_08_sql){ this.where_08_sql=where_08_sql; }
public String getWhere_08_hiddenbtn(){ return this.where_08_hiddenbtn; }
public void setWhere_08_hiddenbtn(String where_08_hiddenbtn){ this.where_08_hiddenbtn=where_08_hiddenbtn; }
public String getWhere_08_hiddencol(){ return this.where_08_hiddencol; }
public void setWhere_08_hiddencol(String where_08_hiddencol){ this.where_08_hiddencol=where_08_hiddencol; }
public String getWhere_09_name(){ return this.where_09_name; }
public void setWhere_09_name(String where_09_name){ this.where_09_name=where_09_name; }
public String getWhere_09_sql(){ return this.where_09_sql; }
public void setWhere_09_sql(String where_09_sql){ this.where_09_sql=where_09_sql; }
public String getWhere_09_hiddenbtn(){ return this.where_09_hiddenbtn; }
public void setWhere_09_hiddenbtn(String where_09_hiddenbtn){ this.where_09_hiddenbtn=where_09_hiddenbtn; }
public String getWhere_09_hiddencol(){ return this.where_09_hiddencol; }
public void setWhere_09_hiddencol(String where_09_hiddencol){ this.where_09_hiddencol=where_09_hiddencol; }

}