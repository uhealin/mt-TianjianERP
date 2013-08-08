<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<style type="text/css">
*{
	font-size: 22px;
}
</style>
<script type="text/javascript">

Ext.onReady(function (){
	new Ext.Toolbar({
			renderTo: "divBtn",
			height:30,
			defaults: {autoHeight: true,autoWidth:true},
	       items:[ { 
	        text:'返回',
	        icon:'${pageContext.request.contextPath}/img/back.gif', 
	        handler:function(){
				window.history.back();
			}
	  	},'->']
	});
	 
});
</script>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>查看</title>
</head>
<body style="overflow: hidden;">
	<div id="divBtn"> </div>
	<div style="width: 100%;font-size:25px !important;padding-top: 20px;line-height: 25px;height: 95%;overflow: auto; " >
		<pre>
								<b>项目业务接洽事先公示</b><br>
					<font color="red">${newCustomer.customerName}</font>目前正在<font color="blue">${newCustomer.optQuality}</font>（按项目业务性质列举项目描述），
				  我所&nbsp;合伙人：<font color="blue">${partnerUser.name}</font>\高级经理:<font color="blue">${managerUser.name}</font>目前正与对方接洽，并于近日递交了专业服务
				  建议书,  有可能被该公司聘任作为申报会计师[按实际情况进行修改]。按我所“关于
				  实施 对首 次接洽的上市公司、拟上市公司等重大项目业务受理事先公示的通知”的要求，
				  现予以公示。
					 作为本所员工，如您与<font color="red">${newCustomer.customerName}</font>已有过接洽，或在此之前已提供
				  过报价、专业服务建议书或发生过其他可能影响本次已递交的专业服务建议 书的事项，
				  请在<font color="blue">${newCustomer.deadlineDate}</font>前立即联系本项目的负责人：
				  
				  ${oneUser.name}：(办公电话：<font color="blue">${oneUser.phone}</font>，移动电话：<font color="blue">${oneUser.mobilePhone }</font>)
				    ${twoUser.name}：(办公电话：<font color="blue">${twoUser.phone}</font>，移动电话：<font color="blue">${twoUser.mobilePhone }</font>)
	
													    主任会计师 ：
																		
				  ${newCustomer.deadlineDate}
						
		</pre>
	</div>
</body>
</html>