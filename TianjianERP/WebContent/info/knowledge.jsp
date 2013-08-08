<%@ page language="java" contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8">


<title>知识首页</title>
</head>

<style>

.datetime {
	color: #cccccc;
	font-size: 12px;
	padding-left: 5px;
}

.linkdiv{border-bottom:1px #DDDDEE dotted;padding:5px;}


</style>
<script type="text/javascript">
	
	Ext.onReady(function(){

	 	var layout = new Ext.Viewport({
			layout:'border',
			items:[{
					region:'center',
		    		id:'center-panel',
		    		layout: 'vbox',
		    		layoutConfig: {  
		    		    align : 'stretch',  
		    		    pack  : 'start'
		    		}, 
		    		split:true,
		    		margins:'0 0 0 0',
		    		lines:false,
		    		collapsible :false,
		    		items:[{
		    		    title: '我提的问题',
		    		    flex:2,
		    		    collapsible :false,
		    		    contentEl:'myQuestion',
		    		    autoScroll:true
		    		},
		    		/*
		    		{
		    		    title: '我关注的问题',
		    		    flex:2,
		    		    collapsible :false,
		    		    contentEl:'myfocus',
		    		    autoScroll:true
		    		},
		    		*/  
		    		{
		    		    title: '最新的问题',
		    		    flex:2,
		    		    collapsible :false,
		    		    contentEl:'newQuestion',
		    		    autoScroll:true
		    		},{
		    		    title: '最热门的问题',
		    		    flex:2,
		    		    collapsible :false,
		    		    contentEl:'hotQuestion',
		    		    autoScroll:true
		    		}]

				},{
					region:'east',
		    		id:'east-panel',
		    		split:true,
		    		margins:'0 0 0 0',
		    		width:250,
		    		layout:'vbox',
		    		layoutConfig: {  
		    		    align : 'stretch',  
		    		    pack  : 'start'
		    		}, 
		    		lines:false,
		    		items:[{
		    		    title: '我的知识贡献度',
		    		    flex:2,
		    		    collapsible :false,
		    		    autoScroll:true,
		    		    contentEl:'myKnowledge'
		    		},{
		    		    title: '知识排名',
		    		    flex:2,
		    		    collapsible :false,
		    		    autoScroll:true,
		    		    contentEl:'myRank'
		    		}
		    		]
				}
			 ]
		});
	 	
	 	layout.doLayout();
	 	
	}) ;

</script>

<body>	

	<div id=center>
		<div id=myQuestion>
			<c:forEach items="${myQuestionList}" var="myQuestion">
				<div class="linkdiv">
					<img border="0" alt="" align="absMiddle" src="${pageContext.request.contextPath}/images/news_bullet.gif">
					<a style="color:#344456; text-decoration: none" href="javascript:;" onclick="showQuestion('${myQuestion.id}');" >
						<span style="font-size: 12px">${myQuestion.title}</span>
						<span class="datetime">(${myQuestion.createDate})</span>
					</a>
				</div>
			</c:forEach>
		</div>
		<div id=myfocus>
			
		</div>
		<div id=newQuestion>
			<c:forEach items="${newQuestionList}" var="newQuestion">
				<div class="linkdiv">
					<img border="0" alt="" align="absMiddle" src="${pageContext.request.contextPath}/images/news_bullet.gif">
					<a style="color:#344456; text-decoration: none" href="javascript:void(0);" onclick="showQuestion('${newQuestion.id}');" >
						<span style="font-size: 12px">${newQuestion.title}</span>
						<span class="datetime">(${newQuestion.createDate})</span>
					</a>
				</div>
			</c:forEach>
		</div>
		<div id=hotQuestion>
			<c:forEach items="${hotQuestionList}" var="hotQuestion">
				<div class="linkdiv">
					<img border="0" alt="" align="absMiddle" src="${pageContext.request.contextPath}/images/news_bullet.gif">
					<a style="color:#344456; text-decoration: none" href="javascript:void(0);" onclick="showQuestion('${hotQuestion.id}');" >
						<span style="font-size: 12px">${hotQuestion.title}</span>
						<span class="datetime">(${hotQuestion.createDate})</span>
					</a>
				</div>
			</c:forEach>		
		</div>
	</div>
	
	<div id=east>
		<div id=myKnowledge>
			
			
			<div class="linkdiv">
				<img border="0" alt="" align="absMiddle" src="${pageContext.request.contextPath}/images/news_bullet.gif">
				1.总计提问：${myQuestionCount}
			</div>
			
			<div class="linkdiv">
				<img border="0" alt="" align="absMiddle" src="${pageContext.request.contextPath}/images/news_bullet.gif">
				2.累计回答：${myAnswerCount}
			</div>
			
			
		</div>
		<div id=myRank>
		
			<c:forEach items="${rankList}" var="rank" varStatus="status">
				<div class="linkdiv">
					<img border="0" alt="" align="absMiddle" src="${pageContext.request.contextPath}/images/news_bullet.gif">
					${status.index+1}、${rank.userName}  回答问题数：${rank.count}
				</div>
			</c:forEach>	
		</div>
	</div>
 
</body>
</html>
<script>

function showQuestion(id) {
	var tab = parent.tab ;
	var url = "${pageContext.request.contextPath}/question/View.jsp?chooseValue="+id ;
	if(tab && tab.id == "mainFrameTab"){
		try{
			n = parent.parent.tab.add({    
				'title':"问题库",  
				 closable:true,  //通过html载入目标页    
				 html:'<iframe scrolling="no" frameborder="0" width="100%" height="100%" src="' + url + '"></iframe>'   
			});    
			parent.parent.tab.setActiveTab(n);
		}catch(e){
			window.location = url ;
		}
	}else {
		window.location = url ;
	}
	
}

</script>