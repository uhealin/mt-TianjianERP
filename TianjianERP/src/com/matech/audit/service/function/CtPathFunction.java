package com.matech.audit.service.function;

import java.sql.Connection;
import java.util.Map;

public interface CtPathFunction {
	public String process(
			javax.servlet.http.HttpSession session,
			javax.servlet.http.HttpServletRequest request,
			javax.servlet.http.HttpServletResponse response,
			Connection conn,Map args) throws Exception ;
}
