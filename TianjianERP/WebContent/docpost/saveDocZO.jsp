<%@page import="com.matech.framework.pub.path.Path"%>
<%@ page contentType="text/html; charset=utf-8" language="java" import="java.sql.*,java.awt.*,java.util.*" errorPage="" %>
<%request.setCharacterEncoding("utf-8");%>
<%@page pageEncoding="utf-8"%>
<%@ page import="com.zhuozhengsoft.ZSOfficeX.*"%>
<%
SaveDocObj   SaveObj  = new SaveDocObj(request, response);

try{
	 String phyPath=Path.getWarPath(config)+"\\docpost\\file";
	SaveObj.saveToFile(phyPath+ "\\"  + SaveObj.FileName);  
	SaveObj.returnOK();
}
finally {
	SaveObj.close();
}
%>

<sctipt>
  alert("保存成功");
  window.close();
</sctipt>