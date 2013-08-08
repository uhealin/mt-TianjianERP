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
   
    <script type="text/javascript">
        function doCheck(){
        	document.forms[0].action="docrec.do?method=doCheckEnd";
        	document.forms[0].submit();
        }
        
        function doTodo(){
        	document.forms[0].action="docrec.do?method=doTodo";
        	document.forms[0].submit();
        }
        
        Ext.onReady(function(){
        	attachInit("fu");
        	
        	$("#divLog").load("docrec.do",{method:"viewLog",uuid:"<%=vo.getUuid() %>"});
/*
        	new ExtButtonPanel({
        		desc:'',
        		renderTo:'sbtBtn',
        		items:[
        		{
                    text: '保存',
                    id:'appSubmit23', 
                    icon:'${pageContext.request.contextPath}/img/receive.png' ,
                    scale: 'large',
                    handler:doCheck
                   },{
                    text: '返回',
                    id:'appSubmit25', 
                    icon:'${pageContext.request.contextPath}/img/back_32.png' ,
                    scale: 'large',
                       handler:doTodo
                   }
                ]  
        	}); 
 */       	
        var viewport = new Ext.Viewport({
                layout: 'border',
                items: [
                // create instance immediately
               {
                    // lazily created panel (xtype:'panel' is default)
                    region: 'south',
                    contentEl: 'south',
                    split: true,
                    height: 200,
                    minSize: 100,
                    maxSize: 200,
                    collapsible: true,
                    title: '审阅处理',
                    margins: '0 0 0 0'
                },
                // in this instance the TabPanel is not wrapped by another panel
                // since no title is needed, this Panel is added directly
                // as a Container
                {
    			
    			  region: 'center',
                    contentEl: 'center',
                    autoScroll: true
    			}
    		   ]
            });
        });
   </script>
   
</head>

<body>

   <div id="center" class="x-hide-display">
   
     
      <table class="formTable">
      <thead>
        <tr>
          <th colspan="4" style="text-align: center;"><h2>收文审阅: <%=vo.getRec_doc_no() %></h2></th>
        </tr>
        </thead>
        <tbody>
        <tr>
          <th>文件名</th>
          <td><%=vo.getFile_name()==null?"":vo.getFile_name() %></td>
          <th class="required">附件下载</th>
          <td>
            <input type="hidden" id="fu" value="<%=vo.getFile_upload()==null?"":vo.getFile_upload()%>" readonly="readonly" />
          </td>
        </tr>
                <tr>
       
          <th class="required">截止日期</th>
          <td>
          <%=vo.getTimeout_date()==null?"":vo.getTimeout_date() %>
          </td>
          <th>签名</th>
          <td><%=vo.getCreater_name() %></td>
        </tr>
        
        
        
     
         
         </tbody>
       </table>
       <div id="divLog"></div>
       
   </div>
   
   <div id="south" class="x-hide-display">
     <form method="post">  <input  type="hidden" name="uuid" value="<%=vo.getUuid()%>"/>
      <table class="formTable">
        <tbody>
          <tr>
                      <th>处理意见</th>
          <td><textarea name="handle_remark" rows="5" cols="15"></textarea> </td>
         <th >意见是否对其他审阅人公开</th>
    <td style="width: 200px" ><input   value="true" property=""  ext_id=public_ind  id="public_ind"  ext_name=public_ind  name="public_ind"  ext_type=singleSelect  type="text" valuemustexist=true      ext_select=700|truefalse  autoid="700"  refer="truefalse"  />
	
	</td>
        

          </tr>
          
          <tr>
          <th >指派他人</th>
          <td >
            <input id="assigner_id"  name="assigner_id"    type="hidden"         />
            <input  style="width:350px"  id="assigner_name"      type="text"         />
             <a onclick="show_selectUser('assigner_name','assigner_id');">选择</a>
            </td>
            
            
  <td colspan="2" >
  
  
  <button type="button" onclick="doCheck()">签字</button>
               &nbsp;   <button type="button" onclick="doTodo()">返回</button>
               
               </td>  
        </tr>
          
        </tbody>
      </table>
       </form>
   </div>
  
</body>
</html>