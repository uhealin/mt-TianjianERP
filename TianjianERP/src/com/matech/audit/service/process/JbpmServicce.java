package com.matech.audit.service.process;
 
public class JbpmServicce {
	private static JbpmTemplate template;

	public static JbpmTemplate getJbpmTemplate() {
		return template;
	}

	public void setJbpmTemplate(JbpmTemplate jbpmTemplate) {
		template = jbpmTemplate;
	}
}
