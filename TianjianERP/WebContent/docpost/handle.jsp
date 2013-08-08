<%@page import="com.matech.audit.service.doc.model.DocPostVO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
   
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
 <%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
 <% DocPostVO docPostVO=null; %>
</head>
<body>

  <div>
     <form action="">
       <table class="formTable">
          <thead>
            <tr><th colspan="2">发文处理</th></tr>
          </thead>
          
          <tbody>
             <tr>
               <th>处理意见</th>
               <td><textarea></textarea></td>
             </tr>
          </tbody>
          
       </table>
     </form>
   </div>

</body>
</html>