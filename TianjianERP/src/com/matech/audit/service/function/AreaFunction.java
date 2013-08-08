package com.matech.audit.service.function;

import java.util.Map;
import java.sql.ResultSet;
import java.sql.Connection;
public interface AreaFunction {
		public ResultSet process(
				javax.servlet.http.HttpSession session,
				javax.servlet.http.HttpServletRequest request,
				javax.servlet.http.HttpServletResponse response,
				Connection conn,Map args) throws Exception ;
		
		public String getTempTable();
}