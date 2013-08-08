<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>个人工作台</title>

<style>

	body{
		height: 100% ;
		width: 100% ;
	}

	.outContent {
		border: 1px solid #acc1dc ;
		width: 100%;
		height:99%;
		margin: 1px;
		width: expression(document.body.clientWidth/3-10) ; 
	}
	 
	.inContent {
		border: 2px solid #e1ebf8 ;
		margin: 1px;
		height:100%;
		padding: 0px;
	}
	
	.title {
		padding:3px;
		height: 6px;
		background-color: #e1ebf8;
		color: #133db6;
		font-size: 14px;
		font-weight: bold;
		line-height: 9px;
		padding-bottom: 6px;
	}

	
	ul {
		font-size: 14px ;
		font-family: "宋体" ;
		color: #003399;
		padding:0px;
		font-size: 12px;
	}
	
	a {
		text-decoration: none;
	}
	
	.special {
		font-weight: bold;
		color: red;
	}
</style>
</head>
<body>
	<table width="100%" border="0" style="height:100%;">
	  <tr>
	    <td rowspan="1" style="vertical-align: top;height:50%;">
		    <div class="outContent" style="height:98%;">
				<div class="inContent">
					<div class="title">
						<div style="float:left;padding: 3px;">公告通知 </div><div style="float: right;padding: 3px;"><a href="${pageContext.request.contextPath}/placard/View.jsp">更多</a></div>
					</div>
					<ul>
						<c:forEach items="${placardList}" var="placard" varStatus="var">
							<c:choose>
								<c:when test="${placard.ctype == '重要公告'}">
									<li class="special">
										<a href="${pageContext.request.contextPath}/placard/View.jsp" target="_blank" class="special">
											[${placard.ctype}]&nbsp;${placard.caption}
										</a>
									</li>
								</c:when>
								<c:otherwise>
									<li>
										<a href="${pageContext.request.contextPath}/placard/View.jsp" target="_blank">
											[${placard.ctype}]&nbsp;${placard.caption}
										</a>
									</li>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</ul>
				</div>
			</div>
	    </td>
	    
	    <td style="height:50%;vertical-align: top;">
		    <div class="outContent" style="height:98%;" >
				<div class="inContent" >
			    	<div class="title">
			    		<div style="float:left;padding: 3px;">新闻中心</div><div style="float: right;padding: 3px;"><a href="${pageContext.request.contextPath}/news.do?method=more" target="_blank" class="more" >更多</a></div>
			    		</div>
					    	<ul>
								<c:forEach items="${newsList}" var="news" varStatus="var">
									<li>
										<a href="${pageContext.request.contextPath}/news.do?method=viewNews&autoId=${news.autoId }" target="_blank">${news.title}</a>
									</li>
								</c:forEach>
							</ul>
				</div>
			</div>
	    </td>
	    
	    <td rowspan="2" style="vertical-align: top;">
	    	<div class="outContent"> 
				<div class="inContent">
			    	<div class="title">
			    		<div style="float:left;padding: 3px;">待处理工作</div><div style="float: right;padding: 3px;"></div>
			    		</div>
			    	<ul>
							<c:forEach items="${waitingList}" var="waitingTask" varStatus="var">
								<c:choose>
									<c:when test="${waitingTask.processKey == 'BusinessTakeKey'}">
										<li><a href="${pageContext.request.contextPath}/businessTake.do?method=audit&taskId=${waitingTask.taskId}" target="_blank">
											${waitingTask.processname}_${waitingTask.projectname}</a>
										</li>
									</c:when>
									<c:when test="${waitingTask.processKey == 'TaskAuditKey'}">
										<li><a href="${pageContext.request.contextPath}/taskAudit.do?method=audit&taskId=${waitingTask.taskId}" target="_blank">
											${waitingTask.processname}_${waitingTask.projectname}</a>
										</li>
									</c:when>
									<c:when test="${waitingTask.processKey == 'TaskStoreKey'}">
										<li><a href="${pageContext.request.contextPath}/taskStore.do?method=audit&taskId=${waitingTask.taskId}" target="_blank">
											${waitingTask.processname}_${waitingTask.projectname}</a>
										</li>
									</c:when>
									<c:when test="${waitingTask.processKey == 'LendFileKey'}">
										<li><a href="${pageContext.request.contextPath}/fileLend.do?method=audit&taskId=${waitingTask.taskId}" target="_blank">
											${waitingTask.processname}_${waitingTask.projectname}</a>
										</li>  
									</c:when>
								</c:choose>
							</c:forEach>
					</ul>
				</div>
			</div>
	    </td>
	  </tr>
	  
	  <tr>
	    <td rowspan="1" style="vertical-align: top;">
		    <div class="outContent" style="height:98%;">
				<div class="inContent">
					<div class="title">
						<div style="float:left;padding: 3px;">法律法规 </div><div style="float: right;padding: 3px;"><a href="${pageContext.request.contextPath}/Policy/manager.jsp">更多</a></div>
					</div>
					<ul>
						<c:forEach items="${policyList}" var="policy" varStatus="var">
							<li><a href="${pageContext.request.contextPath}/policy.do?method=saveParentId&parentId=${policy.id}" target="_blank">${policy.id}</a></li>
						</c:forEach>
					</ul>
				</div>
			</div>
	    </td>
	    
	  	<td style="vertical-align: top;">
	  		<div class="outContent" style="height:98%;">
				<div class="inContent">
			    	<div class="title">
			    		<div style="float:left;padding: 3px;">规章制度</div><div style="float: right;padding: 3px;">
			    			<a href="${pageContext.request.contextPath}/regulations.do?method=more" target="_blank" class="more" >更多</a></div>
			    	</div>
			    	<ul>
						<c:forEach items="${regulationsList}" var="regulations" varStatus="var">
							<li><a href="${pageContext.request.contextPath}/regulations.do?method=viewRegulations&autoId=${regulations.autoId }" target="_blank">${regulations.title }</a></li>
						</c:forEach>
					</ul>
				</div>
			</div>
	  	</td>
	  </tr>
	</table>
	
</body>
</html>