<%@page import="com.matech.audit.service.doc.model.DocRecVO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>


  <%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
  <%
      DocRecVO vo=(DocRecVO)request.getAttribute(DocRecVO.class.getName());
   %>
</head>

<body>
 
   <div>
      <table class="formTable">
        <tr>
          <td colspan="4" style="text-align: center;"><h2>审阅</h2></td>
        </tr>
        <tr>
          <th>文件名</th>
          <td><%=vo.getFile_name() %></td>
          <th class="required">附件下载</th>
          <td>
            <a href="<%=vo.getFile_upload()%>"></a>
          </td>
        </tr>
                <tr>
          <th>办理时限</th>
          <td><input name="handle_hour" value="<%=vo.getHandle_hour() %>" />小时</td>
          <th class="required">截止日期</th>
          <td><input name="" value="<%=vo.getTimeout_date() %>" /></td>
        </tr>
        
        <tr>
          <th>处理意见</th>
          <td><textarea name="handle_remark" rows="5" cols="30"><%=vo.getHandle_remark() %></textarea> </td>
          <td colspan="2"><input type="radio" />公开<input type="radio" />非公开</td>
        </tr>
        
       <tr>
          <th>签名</th>
          <td colspan="3"><input  /></td>
        
        </tr>
        
          <tr>
          <th >指派审核人</th>
          <td colspan="3"><select></select></td>
        
        </tr>
         
         <tr>
           <td ></td>  
           <td colspan="3"><button>审毕</button><button>代办</button></td>  
         </tr>
         
         
       </table>
   </div>
   
</body>
</html>