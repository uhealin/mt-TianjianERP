package com.matech.sms;

public class Sms {
	private String autoId ;//        INT(11)       (NULL)          NO      PRI     (NULL)   AUTO_INCREMENT  SELECT,INSERT,UPDATE,REFERENCES                    
	private String fsUserId ;//      VARCHAR(50)   gbk_chinese_ci  YES     MUL     (NULL)                   SELECT,INSERT,UPDATE,REFERENCES  发送人         
	private String content   ;//     MEDIUMTEXT    gbk_chinese_ci  YES             (NULL)                   SELECT,INSERT,UPDATE,REFERENCES  发送内容      
	private String jsUserId  ;//     VARCHAR(50)   gbk_chinese_ci  YES     MUL     (NULL)                   SELECT,INSERT,UPDATE,REFERENCES  接收人         
	private String state     ;//     VARCHAR(50)   gbk_chinese_ci  YES             (NULL)                   SELECT,INSERT,UPDATE,REFERENCES  发送状态      
	private String jsMobilePhone;//  VARCHAR(50)   gbk_chinese_ci  YES             (NULL)                   SELECT,INSERT,UPDATE,REFERENCES  接收人手机号
	private String smsPriority ;//   VARCHAR(100)  gbk_chinese_ci  YES             (NULL)                   SELECT,INSERT,UPDATE,REFERENCES  优先级         
	private String perproty ;//      MEDIUMTEXT    gbk_chinese_ci  YES             (NULL)                   SELECT,INSERT,UPDATE,REFERENCES  备用        
	
	public String getAutoId() {
		return autoId;
	}
	public void setAutoId(String autoId) {
		this.autoId = autoId;
	}
	public String getFsUserId() {
		return fsUserId;
	}
	public void setFsUserId(String fsUserId) {
		this.fsUserId = fsUserId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getJsUserId() {
		return jsUserId;
	}
	public void setJsUserId(String jsUserId) {
		this.jsUserId = jsUserId;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getJsMobilePhone() {
		return jsMobilePhone;
	}
	public void setJsMobilePhone(String jsMobilePhone) {
		this.jsMobilePhone = jsMobilePhone;
	}
	public String getSmsPriority() {
		return smsPriority;
	}
	public void setSmsPriority(String smsPriority) {
		this.smsPriority = smsPriority;
	}
	public String getPerproty() {
		return perproty;
	}
	public void setPerproty(String perproty) {
		this.perproty = perproty;
	}
	
	
}
