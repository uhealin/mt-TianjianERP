<%@page import="com.matech.framework.pub.util.WebUtil"%>
<%@page import="com.matech.framework.pub.net.Web"%>
<%@page import="com.matech.audit.service.user.model.UserVO"%>
<%@page import="com.matech.audit.service.doc.DocPostService.Node"%>
<%@page import="java.util.*"%>
<%@page import="com.matech.audit.service.doc.model.DocLogVO"%>
<%@page import="java.util.List"%>
<%@page import="com.matech.audit.service.doc.model.DocPostVO"%>
<%@page import="com.matech.audit.pub.db.DBConnect"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.matech.framework.pub.db.DbUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>

<%
   Connection conn=null;
   DbUtil dbUtil=null;
   String uuid=request.getParameter("uuid");
   DocPostVO docPostVO=new DocPostVO();
   List docLogVOs=null;
   WebUtil webUtil=new WebUtil(request,response);
   try{
	   conn=new DBConnect().getConnect();
	   dbUtil=new DbUtil(conn);
	   docPostVO=dbUtil.load(DocPostVO.class, uuid);
	   String t="SELECT c.uuid,c.doc_no,b.id AS handler_id,b.name AS  handler_name, \n"+
				" c.node_code, \n"+
				" c.handle_time, \n"+
				" c.remark,c.node_name, "+
				" c.ctype,c.public_ind,c.state,c.doc_id,c.pccpa_docid,b.mobilephone,'会签' AS opt \n"+
				" FROM oa_doc_post a \n"+
				" INNER JOIN  k_user b  \n"+
				" ON CONCAT(',',a.countersigner_ids,',') LIKE CONCAT('%,',b.id,',%')  \n"+
				" LEFT JOIN oa_doc_log c ON a.uuid=c.doc_id AND c.handler_id=b.id \n"+
				" WHERE a.uuid='"+uuid+"' \n"+
				" union all \n" +
				" SELECT c.uuid,c.doc_no,b.id AS handler_id,b.name AS  handler_name, \n"+
				" c.node_code,\n "+
				" c.handle_time, \n"+
				" c.remark,c.node_name, \n"+
				" c.ctype,c.public_ind,c.state,c.doc_id,c.pccpa_docid,b.mobilephone,'签发' AS opt \n"+
				" FROM oa_doc_post a \n"+
				" INNER JOIN  k_user b ON CONCAT(',',a.signissuer_ids,',') LIKE CONCAT('%,',b.id,',%') \n"+
				" LEFT JOIN oa_doc_log c ON a.uuid=c.doc_id AND c.handler_id=b.id \n"+
				" WHERE a.uuid='"+uuid+"' \n" +
				" union all \n" +
				" SELECT c.uuid,c.doc_no,b.id AS handler_id,b.name AS  handler_name, \n"+
				" c.node_code, \n"+
				" c.handle_time, \n"+
				" c.remark,c.node_name, \n"+
				" c.ctype,c.public_ind,c.state,c.doc_id,c.pccpa_docid,b.mobilephone,'核阅' AS opt \n"+
				" FROM oa_doc_post a \n"+
				" INNER JOIN  k_user b ON CONCAT(',',a.checker_ids,',') LIKE CONCAT('%,',b.id,',%')  \n "+
				" LEFT JOIN oa_doc_log c ON a.uuid=c.doc_id AND c.handler_id=b.id \n"+
				" WHERE a.uuid='"+uuid+"' ";
				
		System.out.println("=====================sql"+t);
			//t="select * from {0} where doc_id=? order by handle_time desc";
		docLogVOs=dbUtil.getListBySql(t);
		
		request.setAttribute("docLogVOs",docLogVOs);
		
		
   }catch(Exception ex){
	  ex.printStackTrace();
   }finally{
	  DbUtil.close(conn);
   }
   
   
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>发文记录:<%=docPostVO.getTitle() %></title>
<script type="text/javascript">

  Ext.onReady(function(){
	  var tbar = new Ext.Toolbar({
	   		renderTo: "divBtn",
	   		id:'mt_form_tbar',
	   		items:[{
	   			text:'返回',
	   	        cls:'x-btn-text-icon',
	   	        icon:'${pageContext.request.contextPath}/img/back.gif',
	   	        handler:function(){
	   	        	window.location.href="<%=webUtil.getPreUrl()%>";
	   	        }
	   		}]
	});
  });

</script>
</head>
<body>
<div id="divBtn"></div>
<table class="formTable">
  <tbody>
    <tr>
      <th>标题</th>
      <td><%=docPostVO.getTitle() %></td>
    </tr>
    
    <tr>
      <th>流程备注</th>
      <td><%=docPostVO.getNode_remark() %></td>
    </tr>
    
  </tbody>
  
</table>

<table class="formTable" cellpadding="3">
   <thead>
      <tr>
        <th></th>
        <th>签字类型</th>
        <th>签字日期</th>
        <th>签字人</th>
        <th>签字人手机</th>
      </tr>
   </thead>
   <tbody>
   
	<c:forEach items="${docLogVOs}" var="docLogVO">
		<tr>
         <td>${docLogVO.index}</td>
         <td>${docLogVO.opt}</td>
         <td>${docLogVO.handle_time}</td>
         <td>${docLogVO.handler_name}</td>
         <td>${docLogVO.mobilephone}</td>
       </tr>
	</c:forEach>
	
   </tbody>
</table>

</body>
</html>
