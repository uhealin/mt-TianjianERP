<%@page import="com.matech.audit.service.department.model.KDepartmentVO"%>
<%@page import="com.matech.audit.service.department.model.KAreaVO"%>
<%@page import="com.matech.audit.service.department.model.DepartmentVO"%>
<%@page import="com.matech.audit.service.user.model.UserVO"%>
<%@page import="com.matech.audit.pub.db.DBConnect"%>
<%@page import="com.matech.framework.pub.db.DbUtil"%>
<%@page import="java.sql.Connection"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

    
<%
   Connection conn=null;
   DbUtil dbUtil=null;
   String id=request.getParameter("id");
   UserVO userVO=null;
   KDepartmentVO departmentVO=null;
   KAreaVO kAreaVO=null;
   try{
	   conn=new DBConnect().getConnect();
	   dbUtil=new DbUtil(conn);
	   userVO=dbUtil.load(UserVO.class, Integer.parseInt(id));
	   departmentVO=dbUtil.load(KDepartmentVO.class, Integer.parseInt(userVO.getDepartmentid()));
	   kAreaVO=dbUtil.load(KAreaVO.class, Integer.parseInt(departmentVO.getAreaid()));
   }catch(Exception ex){
	   
   }finally{
	   DbUtil.close(conn);
   }
%>


<table>
  <tbody>
    <tr>
    <th>姓名</th>
    <td><%=userVO.getName() %></td>
    <td rowspan="7">
      <img alt="" src="${pageContext.request.contextPath}/userPhoto/<%=userVO.getIsTips()%>.jpg">
    </td>
    </tr>
    <tr>
     <th>所属机构</th>
     <td><%=kAreaVO.getName() %></td>
    </tr>
    
    <tr>
      <th>所属部门</th>
      <td><%=departmentVO.getDepartname() %></td>
    </tr>
    
    <tr>
      <th>所内电话</th>
      <td><%=userVO.getPhone() %></td>
    </tr>
    
    <tr>
      <th>手机号码</th>
      <td><%=userVO.getMobilePhone() %></td>
    </tr>
    
    <tr>
      <th>电子邮箱</th>
      <td><%=userVO.getEmail() %></td>
    </tr>
    
    <tr>
      <th>职级</th>
      <td><%=userVO.getRank() %></td>
    </tr>
  </tbody>
</table>