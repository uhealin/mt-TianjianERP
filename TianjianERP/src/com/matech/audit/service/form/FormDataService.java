package com.matech.audit.service.form;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

public class FormDataService {
	private static final String[] CHARS_FROM = { "&", "\"", "<", ">" };
	private static final String[] CHARS_TO = { "&amp;", "&quot;", "&lt;",
			"&gt;" };
	private HttpServletRequest request;

	public FormDataService(HttpServletRequest request) {
		this.request = request;
	}

	public String getAllFormFieldsAndValues() {
		StringBuffer sb = new StringBuffer();

		Enumeration e = this.request.getParameterNames();
		while (e.hasMoreElements()) {
			String field = (String) e.nextElement();
			String fieldValue = this.request.getParameter(field);
			sb.append("<tr>");
			sb.append("<th style=\"vertical-align: top\">");
			sb.append(parse(field));
			sb.append("</th>");
			sb.append("<td><pre class=\"samples\">");
			sb.append(parse(fieldValue));
			sb.append("</pre></td>");
			sb.append("</tr>");
		}
		return sb.toString();
	}

	private Object parse(String fieldValue) {
		String fv = fieldValue;
		for (int i = 0; i < CHARS_FROM.length; i++) {
			fv = fv.replaceAll(CHARS_FROM[i], CHARS_TO[i]);
		}
		return fv;
	}
}
