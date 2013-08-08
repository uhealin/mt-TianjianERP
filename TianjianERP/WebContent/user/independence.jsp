<%@page import="com.matech.audit.pub.db.DBConnect"%>
<%@page import="com.matech.framework.listener.UserSession"%>
<%@page import="com.matech.framework.pub.util.WebUtil"%>
<%@page import="com.matech.audit.service.user.model.IndependenceVO"%>
<%@page import="com.matech.framework.pub.util.StringUtil"%>
<%@page import="net.sf.json.JSONObject"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="com.matech.audit.service.user.model.IndependenceEnum"%>
<%@page import="com.matech.framework.pub.db.DbUtil"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.util.*"%>

<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
 <%!
    public static List<IndependenceEnum> getSubIndepenceEnums(String paraent_code){
	 List<IndependenceEnum> subIndependenceEnums=new ArrayList<IndependenceEnum>();
	 for(IndependenceEnum independenceEnum:IndependenceEnum.values()){
		 if(independenceEnum.name().startsWith(paraent_code)){
			 subIndependenceEnums.add(independenceEnum);
		 }
	 }
	 return subIndependenceEnums;
 }
 %>

<%
	java.util.Calendar c=java.util.Calendar.getInstance();    
	java.text.SimpleDateFormat f=new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	String modify_date = f.format(c.getTime());
	java.text.SimpleDateFormat ff=new java.text.SimpleDateFormat("yyyy");
	String current_year = ff.format(c.getTime());
 %>
 
 <% 
   Connection conn=null;
   DbUtil dbUtil=null;
   WebUtil webUtil=new WebUtil(request,response);
   UserSession userSession=webUtil.getUserSession();
   String uuid=request.getParameter("uuid");
   IndependenceVO independenceVO=null;
   JSONObject jsonIndMap=new JSONObject();
   JSONArray jarr=new JSONArray();
   try{
    conn=new DBConnect().getConnect();
    dbUtil=new DbUtil(conn);
   if(StringUtil.isBlank(uuid)){
	   independenceVO=dbUtil.load(IndependenceVO.class, "userid",userSession.getUserId());
	   //independenceVO=new IndependenceVO();
	   if(StringUtil.isBlank(independenceVO.getUuid())){
	   uuid=UUID.randomUUID().toString();
	   independenceVO.setUuid(uuid);
	   independenceVO.setUserid(userSession.getUserId());
	   independenceVO.setDepartmentid(userSession.getUserAuditDepartmentId());
	   independenceVO.setModify_date(modify_date);
	   independenceVO.setYear(current_year);
	   //dbUtil.insert(independenceVO);
	   }
   }else{
       independenceVO=dbUtil.load(IndependenceVO.class, uuid);        
   }
   jarr=JSONArray.fromObject(StringUtil.isBlank(independenceVO.getJarr())?"[]":independenceVO.getJarr());
   for(IndependenceEnum independenceEnum:IndependenceEnum.values()){
	   JSONObject json=new JSONObject();
	   json.put("code", independenceEnum.name());
	   json.put("state", "是");
	   json.put("remark", "仅供测试");
	   jsonIndMap.put(independenceEnum.name(),json);
   }
   }catch(Exception ex){
	   
   }
 %>
 <%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>独立性调查问卷</title>

<style type="text/css">

   .formTable td{
   padding: 5px 5px 5px 5px !important;
   }
    tr,td {
     border-bottom-width: 0px !important;
     border-top-width: 0px !important;
     
  }
  
  input{
    border-width: 1px
  }

   .trInd td{
     text-indent: 2em;
     line-height: 2em;
   }
   
   p,li{
     line-height: 2em;
   }
   
  

</style>

<script type="text/javascript">

  var jsonInds=<%=jsonIndMap.toString()%>;
  var uuid="<%=independenceVO.getUuid()%>",userid="<%=independenceVO.getUserid()%>",departmentid="<%=independenceVO.getDepartmentid()%>";
  var jarr=<%=jarr.toString()%>; 
  var pageIndex=0,pageCount=0;  
  Ext.onReady(function(){
	  
	  $(".trInd").each(function(index){
		  var td1=$(this).find("td:eq(1)"), 
		      td2=$(this).find("td:eq(2)"),
		      td3=$(this).find("td:eq(3)");
		      td4=$(this).find("td:eq(4)");
		  var radio1=$("<input type='radio' />"),
		  radio2=$("<input type='radio' />"),
		  radio3=$("<input type='radio' />"),
		  input=$("<input type='text' />")
		  ;
		  var code=$(this).attr("id");
		  code=code.substring(2);
		  
		  radio1.attr("name","state"+code).val("是");//.attr("checked","checked");
		  radio2.attr("name","state"+code).val("否");
		  radio3.attr("name","state"+code).val("适用");
		  input.attr("name","remark"+code).val("");
		  td1.append(radio1);
		  td2.append(radio2);
		  td3.append(radio3);
		  td4.append(input);
	  });
	  
	  $.each(jarr,function(index,item){
		  var name=item["name"];
		  if(name.indexOf("state")==0){
			  $("input[name="+name+"]").each(function(index){
				 if($(this).val()==item["value"]){
					 $(this).attr("checked","checked");
				 } 
			  });
			  
		  }else if(name.indexOf("remark")==0){
			  $("input[name="+name+"]").val(item["value"]);
		  }
	  });
	  
	  var tbar = new Ext.Toolbar({
	   		renderTo: "divBtn"
	   		
	   		//items:[saveBtn]
	});

	pageCount=$(".divPage").size(); //计算共几页
	$(".divPage:gt(0)").hide();  //开始值显示第一页面
	 
	var saveBtn = {
		text:'保存',
		icon:'${pageContext.request.contextPath}/img/save.gif',
		handler:function(){
			
		  var jarr=$("#thisForm").serializeArray();
		  jarr=Ext.util.JSON.encode(jarr);
		  var year=$("#year").val();
		  var modify_date=$("#modify_date").val();
		  var url="${pageContext.request.contextPath}/user.do";
		  //var param={method:"doUpdateIndependence",jarr:jarr,year:year,uuid:uuid,modify_date:modify_date};
		  var param={method:"doSaveIndependence",jarr:jarr,year:year,uuid:uuid};
		  $.post(url,param,function(str){
			  alert(str);
		  });
		}};
	
	var backBtn = {
			text:'返回',
            icon:'${pageContext.request.contextPath}/img/back.gif',
            handler:function(){
				window.history.back();
			}
	}
		
	var btnPrePage={
			text:"上一页",
			handler:function(){
				if(pageIndex==0)return;
				//pageIndex=pageIndex-1;
				$(".divPage:eq("+(pageIndex--)+")").fadeOut(500); 
				$(".divPage:eq("+(pageIndex)+")").fadeIn(500); 
				$("#divPageInfo").html((pageIndex+1)+"/"+pageCount);
			}
		}
	
	var btnNextPage={
		text:"下一页",
		handler:function(){
			
			if(pageIndex==pageCount-1)return;
			//pageIndex=pageIndex-1;
			$(".divPage:eq("+(pageIndex++)+")").fadeOut(500); 
			$(".divPage:eq("+(pageIndex)+")").fadeIn(500); 
			$("#divPageInfo").html((pageIndex+1)+"/"+pageCount);
		}
	}
	
	tbar.add(btnPrePage);
	tbar.add(btnNextPage);
	
if("view"=="${param.mode}") {
	$("input").attr("disabled","disabled");
	$("radio").attr("disabled","disabled");
} else {
	tbar.add(saveBtn);
	tbar.add(backBtn);
}

	tbar.doLayout();

  });
  
  
  
</script>
</head>
<body>

<div id="divBtn"></div>
<div id="divPageInfo"></div>
<form id="thisForm" >
<input type="hidden" id="modify_date" value="<%=modify_date %>">
    <div class="divPage">
 <table class="formTable"><tbody><tr><td style="padding: 10px 10px 10px 10px">
   <div style="text-align: center; font-size: 3em;font-weight: bolder;">独立性调查问卷</div>
        <div style="text-align: center;">（适用普通员工）</div>
     <div style="text-align: center;"><input id="year" readOnly="readonly" size="5" value="<%=StringUtil.showNull(independenceVO.getYear())%>">年度</div>
填表说明
<ol type="1" style="list-style-type: decimal;list-style-position: inside;">
   <li> 本所每位执业人员均应在每年年初填写本问卷，并由部门主管复核。</li>
<li>本问卷列示的问题分为三类：第一类是禁止事项，即一旦存在该种情况，将导致没有防范措施消除对独立性的损害；第二类是可能对独立性产生损害的事项；第三类是报告事项。</li>
<li>根据《中国注册会计师职业道德守则》的规定，注册会计师应当在业务期间和财务报表涵盖的期间独立于鉴证客户。在填写本问卷时，如发现有本问卷所列第一类事项存在于上述期间，或本问卷所列第二类事项存在于上述期间并经评估对独立性产生损害，部门主管不得分派该执业人员参与该客户的鉴证业务；如发现存在第三类事项，主管该名员工的合伙人应告知主管该客户的合伙人，由主管该客户的合伙人对独立性作出评价，并采取相应的防范措施以消除不利影响或将其降低至可接受水平，防范措施主要包括：（1）要求主管该名员工的合伙人合理安排该员工职责，以减少对项目组可能产生的影响；（2）由项目组以外的注册会计师复核已执行的相关鉴证工作。</li>
<li>执业人员在对本调查问卷所列的问题进行回答后，应对自己保持独立性情况作出评价。</li>
<li>“经济利益”是指因持有某一实体的股权、债券和其他证券以及其他债务性工具而拥有的利益，包括为取得这种利益享有的权利和承担的义务。</li>
<li>“直接经济利益”是指直接拥有并控制的经济利益（包括授权他人管理的经济利益），或者通过投资工具（如基金、理财产品、年金等）拥有的经济利益，并且有能力控制这些投资工具或影响其决策。</li>
<li>“间接经济利益”是指通过投资工具（如基金、理财产品、年金等）拥有的经济利益，但没有能力控制这些投资工具，或影响其投资决策。</li>
<li>“重大间接经济利益”或“重大经济利益”是指投资额占本人上一年度收入总额5%以上（含5%）。</li>
<li>主要近亲属：是指配偶、父母及子女。</li>
<li>其他近亲属：是指兄弟姐妹、祖父母、外祖父母、孙子女、外孙子女。</li>
</ol>

 </td></tr></tbody></table>
    
      
    </div>
    
    <div class="divPage">
      <table class="formTable" border="0">
        <thead>
          <tr>
            <th>评价内容</th>
            <th>是</th>
            <th>否</th>
            <th>适用</th>
            <th>备注</th>
          </tr>
        </thead>
        <tbody>
        <!-- 1.禁止事项：
1.1经济利益
1.1.1本人及主要近亲属在本部门的鉴证客户中是否拥有直接经济利益或重大间接利益？
1.1.2本人及主要近亲属是否拥有本所上市公司客户的股票、债券等？
1.1.3当本部门鉴证客户是某一实体的重要子公司（子公司资产总额占合并资产总额5%或者利润总额占合并利润总额的10%，下同），本人及主要近亲属在该实体中是否拥有直接经济利益或重大间接利益？
1.1.4本人及主要近亲属是否在某一实体拥有重大经济利益，并且本部门鉴证客户也在该实体拥有经济利益且能对该实体施加重大影响？
1.1.5与本人存在密切私人关系的本所其他员工在本部门的鉴证客户中是否拥有已知的经济利益？
1.2 贷款和担保
1.2.1本人及主要近亲属是否从本部门属于银行或类似金融机构等的鉴证客户取得未按照正常的程序、条款和条件的贷款或担保？
1.2.2本人及主要近亲属是否从本部门不属于银行或类似金融机构的鉴证客户取得贷款，或由鉴证客户提供贷款担保？
1.2.3本人及主要近亲属是否向本部门鉴证客户提供贷款或为其提供担保？
1.2.4本人及主要近亲属是否在本部门的银行或类似金融机构等鉴证客户按照非正常的商业条件开立存款或交易账户？
1.3 商业关系
1.3.1本人及主要近亲属是否与本部门鉴证客户或其控股股东、董事、高级管理人员共同开办企业？
1.3.2 本人及主要近亲属是否从本部门鉴证客户购买商品或服务，且未按照正常的商业程序公平交易？
-->
          <tr>
            <td>1.1经济利益</td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            
          </tr>
          <% List<IndependenceEnum> ide_1_1s=getSubIndepenceEnums("_1_1_");
            for(IndependenceEnum inEnum:ide_1_1s){
          %>
          
          <tr class="trInd" id="tr<%=inEnum.name()%>">
            <td><%=inEnum.getPrefix() %>&nbsp;<%=inEnum.getDesc() %></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
          </tr>
          <%} %>
          
          <tr>
            <td>1.2 贷款和担保</td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            
          </tr>
          <% List<IndependenceEnum> ide_1_2s=getSubIndepenceEnums("_1_2_");
            for(IndependenceEnum inEnum:ide_1_2s){
          %>
          
          <tr class="trInd" id="tr<%=inEnum.name()%>">
            <td><%=inEnum.getPrefix() %>&nbsp;<%=inEnum.getDesc() %></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
          </tr>
          <%} %>
           <tr>
            <td>1.3 商业关系</td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            
          </tr>
          <% List<IndependenceEnum> ide_1_3s=getSubIndepenceEnums("_1_3_");
            for(IndependenceEnum inEnum:ide_1_3s){
          %>
          
          <tr class="trInd" id="tr<%=inEnum.name()%>">
             <td><%=inEnum.getPrefix() %>&nbsp;<%=inEnum.getDesc() %></td>
         
            <td></td>
            <td></td>
            <td></td>
            <td></td>
          </tr>
          <%} %>
        </tbody>
      </table>
    </div>
    
    
    
        <div class="divPage">
      <table class="formTable" border="0">
        <thead>
          <tr>
            <th>评价内容</th>
            <th>是</th>
            <th>否</th>
            <th>适用</th>
            <th>备注</th>
          </tr>
        </thead>
        <tbody>
        <!-- 
  1.4 家庭和私人关系
1.4.1本人是否与本部门鉴证客户的董事、高级管理人员，或所处职位能够对客户会计记录或被审计财务报表的编制施加重大影响的员工      
        （以下简称特定员工）存在家庭（含主要近亲属和其他近亲属）和私人关系？
1.4.2 本人主要近亲属在本部门鉴证客户中所处职位是否能够对该客户的财务状况、经营成果和现金流量施加重大影响？
1.5 与鉴证客户发生雇佣关系
 1.5.1 本人是否在未来某一时间将要或有可能加入本部门鉴证客户？
1.6 临时借出员工
 1.6.1 本部门鉴证客户是否向本所借用过本人？
1.7 曾担任董事、高级管理人员或特定员工
 1.7.1 最近1年内，本人是否曾担任本部门鉴证客户的董事、高级管理人员或特定人员？
1.8 兼任董事或高级管理人员
 1.8.1 本人是否兼任本所鉴证客户董事或高级管理人员？
 1.8.2 本人是否兼任本所鉴证客户的公司秘书？
1.9 薪酬和业绩评价政策
 1.9.1 本人薪酬或业绩评价是否与向本部门鉴证客户推销的非鉴证服务挂钩？
1.10 礼品和款待
1.10.1 本人是否接受本部门鉴证客户的礼品？
1.10.2 本人是否接受本部门鉴证客户的款待超出业务活动中的正常往来？

2.可能对独立性造成损害的事项
2.1 经济利益
2.1.1. 本人其他近亲属在本部门鉴证客户中是否拥有直接经济利益或重大经济利益？（如填列“是”，请回答下列问题，并评价对独立性的影响，如填列“否”，则无需回答下列问题。）
  2.1.1.1 本人与该亲属之间的关系是否密切？
  2.1.1.2 经济利益对该亲属是否重要？

-->
          <tr>
            <td>1.4 家庭和私人关系</td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            
          </tr>
          <% List<IndependenceEnum> ide_1_4s=getSubIndepenceEnums("_1_4_");
            for(IndependenceEnum inEnum:ide_1_4s){
          %>
          
          <tr class="trInd" id="tr<%=inEnum.name()%>">
            <td><%=inEnum.getPrefix() %>&nbsp;<%=inEnum.getDesc() %></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
          </tr>
          <%} %>
          
          <tr>
            <td>1.5 与鉴证客户发生雇佣关系</td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            
          </tr>
          <% List<IndependenceEnum> ide_1_5s=getSubIndepenceEnums("_1_5_");
            for(IndependenceEnum inEnum:ide_1_5s){
          %>
          
          <tr class="trInd" id="tr<%=inEnum.name()%>">
            <td><%=inEnum.getPrefix() %>&nbsp;<%=inEnum.getDesc() %></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
          </tr>
          <%} %>
           <tr>
            <td>1.6 临时借出员工</td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            
          </tr>
          <% List<IndependenceEnum> ide_1_6s=getSubIndepenceEnums("_1_6_");
            for(IndependenceEnum inEnum:ide_1_6s){
          %>
          
          <tr class="trInd" id="tr<%=inEnum.name()%>">
             <td><%=inEnum.getPrefix() %>&nbsp;<%=inEnum.getDesc() %></td>
         
            <td></td>
            <td></td>
            <td></td>
            <td></td>
          </tr>
          <%} %>
 

          <tr>
            <td>1.7 曾担任董事、高级管理人员或特定员工</td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            
          </tr>
          <% List<IndependenceEnum> ide_1_7s=getSubIndepenceEnums("_1_7_");
            for(IndependenceEnum inEnum:ide_1_7s){
          %>
          
          <tr class="trInd" id="tr<%=inEnum.name()%>">
             <td><%=inEnum.getPrefix() %>&nbsp;<%=inEnum.getDesc() %></td>
         
            <td></td>
            <td></td>
            <td></td>
            <td></td>
          </tr>
          <%} %>
          
          <tr>
            <td>1.8 兼任董事或高级管理人员</td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            
          </tr>
          <% List<IndependenceEnum> ide_1_8s=getSubIndepenceEnums("_1_8_");
            for(IndependenceEnum inEnum:ide_1_8s){
          %>
          
          <tr class="trInd" id="tr<%=inEnum.name()%>">
             <td><%=inEnum.getPrefix() %>&nbsp;<%=inEnum.getDesc() %></td>
         
            <td></td>
            <td></td>
            <td></td>
            <td></td>
          </tr>
          <%} %>
          
          <tr>
            <td>1.9 薪酬和业绩评价政策</td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            
          </tr>
          <% List<IndependenceEnum> ide_1_9s=getSubIndepenceEnums("_1_9_");
            for(IndependenceEnum inEnum:ide_1_9s){
          %>
          
          <tr class="trInd" id="tr<%=inEnum.name()%>">
             <td><%=inEnum.getPrefix() %>&nbsp;<%=inEnum.getDesc() %></td>
         
            <td></td>
            <td></td>
            <td></td>
            <td></td>
          </tr>
          <%} %>
          
          <tr>
            <td>1.10 礼品和款待</td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            
          </tr>
          <% List<IndependenceEnum> ide_1_10s=getSubIndepenceEnums("_1_10_");
            for(IndependenceEnum inEnum:ide_1_10s){
          %>
          
          <tr class="trInd" id="tr<%=inEnum.name()%>">
             <td><%=inEnum.getPrefix() %>&nbsp;<%=inEnum.getDesc() %></td>
         
            <td></td>
            <td></td>
            <td></td>
            <td></td>
          </tr>
          <%} %>
          
  
  <tr>
            <td>          2.可能对独立性造成损害的事项<p>
&nbsp;2.1 经济利益<p>
&nbsp;&nbsp;2.1.1. 本人其他近亲属在本部门鉴证客户中是否拥有直接经济利益或重大经济利益？（如填列“是”，请回答下列问题，并评价对独立性的影响，如填列“否”，则无需回答下列问题。）
</td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            
          </tr>
          <% List<IndependenceEnum> ide_2_1_1s=getSubIndepenceEnums("_2_1_1_");
            for(IndependenceEnum inEnum:ide_2_1_1s){
          %>
          
          <tr class="trInd" id="tr<%=inEnum.name()%>">
             <td><%=inEnum.getPrefix() %>&nbsp;<%=inEnum.getDesc() %></td>
         
            <td></td>
            <td></td>
            <td></td>
            <td></td>
          </tr>
          <%} %>
        </tbody>
      </table>
    </div>
    
            <div class="divPage">
      <table class="formTable" border="0">
        <thead>
          <tr>
            <th>评价内容</th>
            <th>是</th>
            <th>否</th>
            <th>适用</th>
            <th>备注</th>
          </tr>
        </thead>
        <tbody>
        <!-- 
 2.1.2 本人及主要近亲属是否在某一实体拥有经济利益，并且知悉本部门鉴证客户的董事、高级管理人员或具有控制权的所有者也在该实体拥有经济利益？（如填列“是”，请回答下列问题，并评价对独立性的影响，如填列“否”，则无需回答下列问题。）
 2.1.2.1 实体的所有权是否由少数人持有？
 2.1.2.2 经济利益对于本人及主要近亲属是否重大？
 2.1.2.3 鉴证客户的董事、高级管理人员或具有控制权的所有者是否能够控制该经济实体，或对其施加重大影响？
2.1.3本人及主要近亲属是否作为受托管理人在本部门鉴证客户中拥有直接经济利益或重大间接经济利益？（如填列“是”，请回答下列问题，并评价对独立性的影响，如填列“否”，则无需回答下列问题。）
 2.1.3.1 本人及主要近亲属是否为受托财产的受益人？
 2.1.3.2 委托人在鉴证客户中拥有的经济利益对委托人是否重大？
 2.1.3.3 委托人是否能对鉴证客户施加重大影响？
 2.1.3.4 针对委托人在鉴证客户拥有的经济利益，本人及主要近亲属是否对其任何投资决策都能施加重大影响？
2.2 商业关系
2.2.1本人及主要近亲属是否在股东人数有限的实体中拥有经济利益，而本部门的鉴证客户或其董事、高级管理人员也在该实体拥有经济利益？（如填列“是”，请回答下列问题，并评价对独立性的影响，如填列“否”，则无需回答下列问题。）
2.2.1.1这种商业关系对于本人及主要近亲属是否重要？
2.2.1.2该经济利益对本人及主要近亲属是否重要？
2.2.1.3该鉴证客户或其董事、高级管理人员是否因该经济利益而控制该实体？
2.2.2在按照正常商业程序购买的前提下，本人及主要近亲属是否从本部门鉴证客户购买商品或服务？（如填列“是”，请回答下列问题，并评价对独立性的影响，如填列“否”，则无需回答下列问题。）

 2.2.2.1交易性质是否特殊？
2.2.2.2交易金额是否较大？
-->
          <tr>
            <td> 2.1.2 本人及主要近亲属是否在某一实体拥有经济利益，并且知悉本部门鉴证客户的董事、高级管理人员或具有控制权的所有者也在该实体拥有经济利益？（如填列“是”，请回答下列问题，并评价对独立性的影响，如填列“否”，则无需回答下列问题。）</td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            
          </tr>
          <% List<IndependenceEnum> ide_2_1_2s=getSubIndepenceEnums("_2_1_2_");
            for(IndependenceEnum inEnum:ide_2_1_2s){
          %>
          
          <tr class="trInd" id="tr<%=inEnum.name()%>">
            <td><%=inEnum.getPrefix() %>&nbsp;<%=inEnum.getDesc() %></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
          </tr>
          <%} %>
          
          <tr>
            <td>2.1.3本人及主要近亲属是否作为受托管理人在本部门鉴证客户中拥有直接经济利益或重大间接经济利益？（如填列“是”，请回答下列问题，并评价对独立性的影响，如填列“否”，则无需回答下列问题。）</td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            
          </tr>
          <% List<IndependenceEnum> ide_2_1_3s=getSubIndepenceEnums("_2_1_3_");
            for(IndependenceEnum inEnum:ide_2_1_3s){
          %>
          
          <tr class="trInd" id="tr<%=inEnum.name()%>">
            <td><%=inEnum.getPrefix() %>&nbsp;<%=inEnum.getDesc() %></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
          </tr>
          <%} %>
           <tr>
            <td>2.2 商业关系 <p>
2.2.1本人及主要近亲属是否在股东人数有限的实体中拥有经济利益，而本部门的鉴证客户或其董事、高级管理人员也在该实体拥有经济利益？（如填列“是”，请回答下列问题，并评价对独立性的影响，如填列“否”，则无需回答下列问题。）
</td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            
          </tr>
          <% List<IndependenceEnum> ide_2_2_1s=getSubIndepenceEnums("_2_2_1_");
            for(IndependenceEnum inEnum:ide_2_1_1s){
          %>
          
          <tr class="trInd" id="tr<%=inEnum.name()%>">
             <td><%=inEnum.getPrefix() %>&nbsp;<%=inEnum.getDesc() %></td>
         
            <td></td>
            <td></td>
            <td></td>
            <td></td>
          </tr>
          <%} %>
 

          <tr>
            <td>2.2.2在按照正常商业程序购买的前提下，本人及主要近亲属是否从本部门鉴证客户购买商品或服务？（如填列“是”，请回答下列问题，并评价对独立性的影响，如填列“否”，则无需回答下列问题。）
</td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            
          </tr>
          <% List<IndependenceEnum> ide_2_2_2s=getSubIndepenceEnums("_2_2_2_");
            for(IndependenceEnum inEnum:ide_2_2_2s){
          %>
          
          <tr class="trInd" id="tr<%=inEnum.name()%>">
             <td><%=inEnum.getPrefix() %>&nbsp;<%=inEnum.getDesc() %></td>
         
            <td></td>
            <td></td>
            <td></td>
            <td></td>
          </tr>
          <%} %>
       
        </tbody>
      </table>
    </div>
    
    
    
     <div class="divPage">
      <table class="formTable" border="0">
        <thead>
          <tr>
            <th>评价内容</th>
            <th>是</th>
            <th>否</th>
            <th>适用</th>
            <th>备注</th>
          </tr>
        </thead>
        <tbody>
        <!-- 
 2.3 家庭和私人关系
 2.3.1 本人其他近亲属是否是本部门鉴证客户的董事、高级管理人员或特定员工？（如填列“是”，请回答下列问题，并评价对独立性的影响，如填列“否”，则无需回答下列问题。）
  2.3.1.1 本人与该亲属关系是否密切？
  2.3.1.2 该亲属在鉴证客户中的职位是否重要？
  2.3.1.3 本人在该鉴证客户项目组的角色是否重要？
 2.3.2 与本人关系密切的本部门鉴证客户员工是否是该客户的董事、高级管理人员或特定员工？（如填列“是”，请回答下列问题，并评价对独立性的影响，如填列“否”，则无需回答下列问题。）
  2.3.2.1 该员工在鉴证客户中的职位是否重要？
  2.3.2.2 本人在该鉴证客户项目组的角色是否重要？
2.4 担任董事、高级管理人员或特定员工
 2.4.1 在本年度之前是否担任本部门鉴证客户的董事、高级管理人员或特定员工？（如填列“是”，请回答下列问题，并评价对独立性的影响，如填列“否”，则无需回答下列问题。）
  2.4.1.1 在鉴证客户中曾担任的职务是否重要？
  2.4.1.2 离开客户时间是否不够长（12个月以上）？
2.5 为鉴证客户提供非鉴证服务
 2.5.1 本人是否为本部门的鉴证客户提供非鉴证服务？
2.6 薪酬和业绩评价政策
 2.6.1本人薪酬或业绩评价是否与向本部门鉴证客户推销的非鉴证服务挂钩？（如填列“是”，请回答下列问题，并评价对独立性的影响，如填列“否”，则无需回答下列问题。）
  2.6.1.1 推销非鉴证服务的因素在本人薪酬或业绩评价中所占比例是否较大？
  2.6.1.2 本人在该客户项目组的角色是否重要？
  2.6.1.3 推销非鉴证服务的业绩是否影响本人的晋升？

-->
          <tr>
            <td>  2.3 家庭和私人关系<p>
 2.3.1 本人其他近亲属是否是本部门鉴证客户的董事、高级管理人员或特定员工？（如填列“是”，请回答下列问题，并评价对独立性的影响，如填列“否”，则无需回答下列问题。）
</td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            
          </tr>
          <% List<IndependenceEnum> ide_2_3_1s=getSubIndepenceEnums("_2_3_1_");
            for(IndependenceEnum inEnum:ide_2_3_1s){
          %>
          
          <tr class="trInd" id="tr<%=inEnum.name()%>">
            <td><%=inEnum.getPrefix() %>&nbsp;<%=inEnum.getDesc() %></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
          </tr>
          <%} %>
          
          <tr>
            <td> 2.3.2 与本人关系密切的本部门鉴证客户员工是否是该客户的董事、高级管理人员或特定员工？（如填列“是”，请回答下列问题，并评价对独立性的影响，如填列“否”，则无需回答下列问题。）
            </td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            
          </tr>
          <% List<IndependenceEnum> ide_2_3_2s=getSubIndepenceEnums("_2_3_2_");
            for(IndependenceEnum inEnum:ide_2_3_2s){
          %>
          
          <tr class="trInd" id="tr<%=inEnum.name()%>">
            <td><%=inEnum.getPrefix() %>&nbsp;<%=inEnum.getDesc() %></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
          </tr>
          <%} %>
           <tr>
            <td>2.4 担任董事、高级管理人员或特定员工<p>
 2.4.1 在本年度之前是否担任本部门鉴证客户的董事、高级管理人员或特定员工？（如填列“是”，请回答下列问题，并评价对独立性的影响，如填列“否”，则无需回答下列问题。）
</td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            
          </tr>
          <% List<IndependenceEnum> ide_2_4_1s=getSubIndepenceEnums("_2_4_1_");
            for(IndependenceEnum inEnum:ide_2_4_1s){
          %>
          
          <tr class="trInd" id="tr<%=inEnum.name()%>">
             <td><%=inEnum.getPrefix() %>&nbsp;<%=inEnum.getDesc() %></td>
         
            <td></td>
            <td></td>
            <td></td>
            <td></td>
          </tr>
          <%} %>
 

          <tr>
            <td>2.5 为鉴证客户提供非鉴证服务
</td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            
          </tr>
          <% List<IndependenceEnum> ide_2_5s=getSubIndepenceEnums("_2_5_");
            for(IndependenceEnum inEnum:ide_2_5s){
          %>
          
          <tr class="trInd" id="tr<%=inEnum.name()%>">
             <td><%=inEnum.getPrefix() %>&nbsp;<%=inEnum.getDesc() %></td>
         
            <td></td>
            <td></td>
            <td></td>
            <td></td>
          </tr>
          <%} %>
       
       <tr>
            <td>2.6 薪酬和业绩评价政策<p>
 2.6.1本人薪酬或业绩评价是否与向本部门鉴证客户推销的非鉴证服务挂钩？（如填列“是”，请回答下列问题，并评价对独立性的影响，如填列“否”，则无需回答下列问题。）

</td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            
          </tr>
          <% List<IndependenceEnum> ide_2_6_1s=getSubIndepenceEnums("_2_6_1_");
            for(IndependenceEnum inEnum:ide_2_6_1s){
          %>
          
          <tr class="trInd" id="tr<%=inEnum.name()%>">
             <td><%=inEnum.getPrefix() %>&nbsp;<%=inEnum.getDesc() %></td>
         
            <td></td>
            <td></td>
            <td></td>
            <td></td>
          </tr>
         
          <%} %>
       </tbody>
         </table>
         </div>
           <div class="divPage">
      <table class="formTable" border="0">
        <thead>
          <tr>
            <th>评价内容</th>
            <th>是</th>
            <th>否</th>
            <th>适用</th>
            <th>备注</th>
          </tr>
        </thead>
        <tbody>
        <!-- 
2.7 诉讼或诉讼威胁
 2.7.1 本人与本部门鉴证客户是否发生诉讼或很可能发生诉讼？（如填列“是”，请回答下列问题，并评价对独立性的影响，如填列“否”，则无需回答下列问题。）
  2.7.1.1 诉讼是否重要？
  2.7.1.2 诉讼是否与前期鉴证业务相关？

3. 报告事项
3.1 家庭和私人关系
 3.1.1 本人与本所鉴证客户的董事、高级管理人员或特定员工之间是否存在家庭或私人关系？


-->
          <tr>
            <td>  2.7 诉讼或诉讼威胁<p>
 2.7.1 本人与本部门鉴证客户是否发生诉讼或很可能发生诉讼？（如填列“是”，请回答下列问题，并评价对独立性的影响，如填列“否”，则无需回答下列问题。）
</td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            
          </tr>
          <% List<IndependenceEnum> ide_2_7_1s=getSubIndepenceEnums("_2_7_1_");
            for(IndependenceEnum inEnum:ide_2_7_1s){
          %>
          
          <tr class="trInd" id="tr<%=inEnum.name()%>">
            <td><%=inEnum.getPrefix() %>&nbsp;<%=inEnum.getDesc() %></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
          </tr>
          <%} %>
          
          <tr>
            <td> 3. 报告事项<p>
3.1 家庭和私人关系
            </td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            
          </tr>
          <% List<IndependenceEnum> ide_3_1s=getSubIndepenceEnums("_3_1_");
            for(IndependenceEnum inEnum:ide_3_1s){
          %>
          
          <tr class="trInd" id="tr<%=inEnum.name()%>">
            <td><%=inEnum.getPrefix() %>&nbsp;<%=inEnum.getDesc() %></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
          </tr>
          <%} %>
           
           <tr>
            <td colspan="5" style="border-top-width: 1px !important;">
            对保持独立性情况做出评价：<p>

    不存在影响本人独立性的事项（或：除“可能对独立性产生损害的事项评价表”中所列事项，不存在其他影响本人独立性的事项。
 <p>


本人签名：<p>
                                                              日期：

<p>
                                                              部门主管（签名）<p>
                                                              日期：
                                                                     
             
            </td>
           </tr>
        </tbody>
      </table>
    </div>
</form>
</body>
</html>