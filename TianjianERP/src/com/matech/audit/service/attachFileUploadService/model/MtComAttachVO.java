package com.matech.audit.service.attachFileUploadService.model;

import com.matech.framework.pub.db.Table;

@Table(name="MT_COM_ATTACH",pk="ATTACHID")
public class MtComAttachVO {
	
	 protected String ATTACHID ;
	 protected String ATTACHNAME ;
	 protected String ATTACHFILE ;
	 protected String ATTACHFILEPATH ;
	 protected String ATTACHTYPE ;
	 protected String UPDATEUSER ;
	 protected String UPDATETIME ;
	 protected String INDEXTABLE ;
	 protected String INDEXMETADATA ;
	 protected String INDEXID ;
	 protected String PROPERTY ;
	 protected String RECORDCONTENT ;
	 protected String FILEID ;
	 protected Integer FILESIZE ;
	 protected String GATHERDATE ;


	 public String getATTACHID(){ return this.ATTACHID; }
	 public void setATTACHID(String ATTACHID){ this.ATTACHID=ATTACHID; }
	 public String getATTACHNAME(){ return this.ATTACHNAME; }
	 public void setATTACHNAME(String ATTACHNAME){ this.ATTACHNAME=ATTACHNAME; }
	 public String getATTACHFILE(){ return this.ATTACHFILE; }
	 public void setATTACHFILE(String ATTACHFILE){ this.ATTACHFILE=ATTACHFILE; }
	 public String getATTACHFILEPATH(){ return this.ATTACHFILEPATH; }
	 public void setATTACHFILEPATH(String ATTACHFILEPATH){ this.ATTACHFILEPATH=ATTACHFILEPATH; }
	 public String getATTACHTYPE(){ return this.ATTACHTYPE; }
	 public void setATTACHTYPE(String ATTACHTYPE){ this.ATTACHTYPE=ATTACHTYPE; }
	 public String getUPDATEUSER(){ return this.UPDATEUSER; }
	 public void setUPDATEUSER(String UPDATEUSER){ this.UPDATEUSER=UPDATEUSER; }
	 public String getUPDATETIME(){ return this.UPDATETIME; }
	 public void setUPDATETIME(String UPDATETIME){ this.UPDATETIME=UPDATETIME; }
	 public String getINDEXTABLE(){ return this.INDEXTABLE; }
	 public void setINDEXTABLE(String INDEXTABLE){ this.INDEXTABLE=INDEXTABLE; }
	 public String getINDEXMETADATA(){ return this.INDEXMETADATA; }
	 public void setINDEXMETADATA(String INDEXMETADATA){ this.INDEXMETADATA=INDEXMETADATA; }
	 public String getINDEXID(){ return this.INDEXID; }
	 public void setINDEXID(String INDEXID){ this.INDEXID=INDEXID; }
	 public String getPROPERTY(){ return this.PROPERTY; }
	 public void setPROPERTY(String PROPERTY){ this.PROPERTY=PROPERTY; }
	 public String getRECORDCONTENT(){ return this.RECORDCONTENT; }
	 public void setRECORDCONTENT(String RECORDCONTENT){ this.RECORDCONTENT=RECORDCONTENT; }
	 public String getFILEID(){ return this.FILEID; }
	 public void setFILEID(String FILEID){ this.FILEID=FILEID; }
	 public Integer getFILESIZE(){ return this.FILESIZE; }
	 public void setFILESIZE(Integer FILESIZE){ this.FILESIZE=FILESIZE; }
	 public String getGATHERDATE(){ return this.GATHERDATE; }
	 public void setGATHERDATE(String GATHERDATE){ this.GATHERDATE=GATHERDATE; }

	 public boolean isImg(){
		 return this.ATTACHNAME.toLowerCase().endsWith(".jpg")||
				 this.ATTACHNAME.toLowerCase().endsWith(".png")||
				 this.ATTACHNAME.toLowerCase().endsWith(".gif");
	 }
	 
	 public boolean isVideo(){
		 return this.ATTACHNAME.toLowerCase().endsWith(".avi")||
				 this.ATTACHNAME.toLowerCase().endsWith(".wmv")||
				 this.ATTACHNAME.toLowerCase().endsWith(".3gp")||
				 this.ATTACHNAME.toLowerCase().endsWith(".mp4");
		 
	 }
	 
	 public boolean isAudio(){
		 return this.ATTACHNAME.toLowerCase().endsWith(".mp3")||
				 this.ATTACHNAME.toLowerCase().endsWith(".ogg")||
				 this.ATTACHNAME.toLowerCase().endsWith(".wma");
	 }
	 
	 public boolean isFalsh(){
		return this.ATTACHNAME.toLowerCase().endsWith(".swf")||this.ATTACHNAME.toLowerCase().endsWith(".flvs");
	 }

}
