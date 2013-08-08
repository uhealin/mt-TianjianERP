<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>程序和目标</title>
</head>
<body style=" margin: 0px; padding: 0px;">

	<div id="divBlock" style="position:absolute;width:100%;height:100%; top:expression(this.offsetParent.scrollTop); z-index:1; padding:10px; background:#ffffff;filter:alpha(opacity=50); text-align:center; display:none;">
	</div>
	<div id="divProduce" style="position:absolute;width:400px;height:200px; z-index:2;left:expression((document.body.clientWidth-400)/2);top:expression(this.offsetParent.scrollTop + 130); border:1px solid #6595d6; padding:10px; background:#ffffff;text-align:center; display: none;">
		<fieldset>
			<legend><font size="2">设置执行程序</font></legend>
			<textarea name="textProduce" cols="40" rows="7" id="textProduce"></textarea><br/><br/>
			<input type="hidden" name="autoid" id="autoid" value="">
			<input type="button" onclick="saveProcedure();" class="flyBT" value="确定" >
			<input type="button" onclick="document.getElementById('textProduce').value='';" class="flyBT" value="清空" >
			<input type="button" class="flyBT" value="关闭" onclick="hiddenProDiv();">
		</fieldset>
	</div>

	<div id="divProduceRemark" style="position:absolute;width:400px;height:200px; z-index:2;left:expression((document.body.clientWidth-400)/2);top:expression(this.offsetParent.scrollTop + 130); border:1px solid #6595d6; padding:10px; background:#ffffff;text-align:center; display: none;">
		<fieldset>
			<legend><font size="2">设置程序备注</font></legend>
			<textarea name="textProduceRemark" cols="40" rows="7" id="textProduceRemark"></textarea><br/><br/>
			<input type="hidden" name="autoid" id="autoid" value="">
			<input type="button" onclick="saveProcedureRemark();" class="flyBT" value="确定" >
			<input type="button" onclick="document.getElementById('textProduceRemark').value='';" class="flyBT" value="清空" >
			<input type="button" class="flyBT" value="关闭" onclick="hiddenProRemarkDiv();">
		</fieldset>
	</div>

	<div id="divTarget" style="position:absolute;width:400px;height:200px; z-index:2;left:expression((document.body.clientWidth-400)/2);top:expression(this.offsetParent.scrollTop + 130); border:1px solid #6595d6; padding:10px; background:#ffffff;text-align:center; display: none;">
		<fieldset>
			<legend><font size="2">设置目标</font></legend>
			<textarea name="textTarget" cols="40" rows="7" id="textTarget"></textarea><br/><br/>
			<input type="hidden" name="autoid" id="autoid" value="">
			<input type="button" onclick="saveTarget();" class="flyBT" value="确定" >
			<input type="button" onclick="document.getElementById('textTarget').value='';" class="flyBT" value="清空" >
			<input type="button" class="flyBT" value="关闭" onclick="hiddenTargetDiv();">
		</fieldset>
	</div>
<!--
	<font color="#FF0000" size="2" >
	<strong>您现在所在位置: </strong>
		<font color="#0000CC">工作底稿-&gt;${taskCode} ${taskName}-&gt;程序和目标</font>
	</font><br>
  <table width="500"  border="0" cellpadding="0" cellspacing="0">
    <tr>
      <td height="1" bgcolor="#0000FF"></td>
  </tr>
  </table>
  <br/>

  <input name="" type="button" class="flyBT" value="返  回" onclick="history.back();">&nbsp;
  <input name="" type="button" class="flyBT" value="打  印" onclick="goPrint();">&nbsp;
 -->
<input name="projectId" id="projectId" value="${param.projectId}" type="hidden">

<c:set var="ondblclick" value="ondblclick"></c:set>
<c:if test="${isManager == false}">
	<c:set var="disabled" value=" disabled='disabled' style='background-color: #eeeeee;' "></c:set>
	<c:set var="ondblclick" value="sdsadsad"></c:set>
</c:if>

<c:if test="${!empty tacheList}">
<fieldset>
	<legend><font size="2">环节</font></legend>
	<table id="targetTable" cellSpacing="1" cellPadding="2" width="100%" bgColor="#6595d6" border="0">
	  <tr>
	      <td noWrap align="center" bgColor="#b9c4d5">环节编号</td>
	      <td noWrap align="center" bgColor="#b9c4d5">环节名称</td>
	      <td noWrap align="center" bgColor="#b9c4d5">已分工执行人</td>
	      <td noWrap align="center" bgColor="#b9c4d5">分工</td>
	  </tr>

		<c:forEach items="${tacheList}" var="tache" varStatus="var">
			<tr tacheTaskId="${tache.taskId}">
				<td align="center" bgcolor="#f6f6f6">
					${tache.taskCode}
				</td>
				<td bgcolor="#f6f6f6">
					${tache.taskName}
				</td>
				<td bgcolor="#f6f6f6">
					${tache.executor}
				</td>
				<td align="center" bgcolor="#f6f6f6">
						<input type="text" autoid="176" hideresult=true multiselect=true onclick="onPopDivClick(this);"
						 ${disabled} onchange="updateTacheExecutor(this);" taskId="${tache.taskId}" id="tacheExecutor" name="tacheExecutor" refer="projectId"/>
				</td>
			</tr>
		</c:forEach>
	</table>
</fieldset>
</c:if>

<c:if test="${empty tacheList}">

<!--
<input name="" type="button" class="flyBT" value="打印目标与程序" onclick="goPrint();">&nbsp;
<input name="" type="button" class="flyBT" value="批量导入" onclick="batchUpload();">&nbsp;

 -->
<fieldset>
	<legend><font size="2">目标</font></legend>
	<input type="button" class="flyBT" value="增  加" ${disabled} onclick="addTarget();"/>
	<input type="button" class="flyBT" value="删  除" ${disabled} onclick="delTargets();"/>

	<table id="targetTable" cellSpacing="1" cellPadding="2" width="100%" bgColor="#6595d6" border="0" style="margin-top: 5px;">
	    <tr class="targetTr">
	      <td width="2px;" align="center" bgColor="#b9c4d5">选</td>
	      <td align="center" bgColor="#b9c4d5">状态</td>
	      <td align="center" bgColor="#b9c4d5">编号</td>
	      <td noWrap align="center" bgColor="#b9c4d5">目标</td>
	      <td noWrap align="center" bgColor="#b9c4d5">相关执行程序</td>
	      <td noWrap align="center" bgColor="#b9c4d5">认定</td>
	      <td noWrap align="center" bgColor="#b9c4d5">备注</td>
	      <td noWrap align="center" bgColor="#b9c4d5">是否执行</td>
	      <c:if test="${param.isLeaf == 'false'}">
	      	<td noWrap align="center" bgColor="#b9c4d5">执行人</td>
	      </c:if>
	    </tr>

	<c:set var="prevTarStateId" value=""></c:set>
	<c:forEach items="${targetList}" var="target" varStatus="var">
		<tr tarAutoId="${target.autoID}">
	   		<td align="center" bgcolor="#f6f6f6">
	   			<input type="checkbox" tarAutoId="${target.autoID}" id="tarAutoId" name="tarAutoId" value="${target.autoID}"/>
	        </td>
			<td align="center" bgcolor="#f6f6f6">
				<select tarAutoId="${target.autoID}" prevValue="${target.state}" onchange="checkPrevTarState('${target.autoID}');" id="tarState${target.autoID}" name="state">
					<option value="未完成">未完成</option>
					<option value="已完成">已完成</option>
					<option value="不适用">不适用</option>
				</select>
				<script language="javascript">
					selectObj = document.getElementById("tarState${target.autoID}");
					for(var i=0 ; i < selectObj.options.length; i++) {
						if(selectObj.options[i].value == "${target.state}") {
							selectObj.options[i].selected = 'selected';
							break;
						}
					}
				</script>
				<input type="hidden" id="prevTarState${target.autoID}" value="${prevTarStateId }">
			</td>
			<td align="center" bgcolor="#f6f6f6">
				<input type="text" tarAutoId="${target.autoID}" id="tarDefineId${target.autoID}" ${disabled}  onblur="updateTarget(this);" name="defineId" title="${target.defineId}" value="${target.defineId }" size="1" />
			</td>
			<td align="left" tarAutoId="${target.autoID}" bgcolor="#f6f6f6" title="双击编辑目标" style="cursor:hand;" ${ondblclick}="showTargetDiv(this);" >
				${target.auditTarget }
			</td>
			<td align="center" bgcolor="#f6f6f6">
				<input type="text" tarAutoId="${target.autoID}" id="correlationExeProcedure" ${disabled}  onblur="updateTarget(this);" name="correlationExeProcedure"  title="${target.correlationExeProcedure }" value="${target.correlationExeProcedure }" size="12" />
			</td>
			<td align="center" bgColor="#f6f6f6">
		        	<input type="text" "
							tarAutoId="${target.autoID}"
							id="cognizance" "
							name="cognizance" "
							title="${target.cognizance}"
							value="${target.cognizance}"
							autoid="175"
							autoWidth="320"
							hideresult=true
							multiselect=true
							onclick="onPopDivClick(this);"
							onchange="updateTarget(this);"
							${disabled}
		        			size="12" />
		    </td>
			<td align="center" bgcolor="#f6f6f6">
				<input type="text" tarAutoId="${target.autoID}" id="remark" ${disabled}  onblur="updateTarget(this);" name="remark" title="${target.remark }"  value="${target.remark }" />
			</td>
			<td align="center" bgcolor="#f6f6f6">
				<c:choose>
					<c:when test="${target.executeIt == 0}">
						<input type="checkbox" tarAutoId="${target.autoID}" id="executeIt" onclick="updateTarget(this);" name="executeIt" value="1"  />
					</c:when>
					<c:otherwise>
						<input type="checkbox" tarAutoId="${target.autoID}" id="executeIt" onclick="updateTarget(this);" name="executeIt" value="0" checked="checked"  />
					</c:otherwise>
				</c:choose>
			</td>

			<c:if test="${param.isLeaf == 'false'}">
				<td align="center" bgcolor="#f6f6f6">
					<input type="text"
		        			autoid="176"
							hideresult=true
							multiselect=true
							onclick="onPopDivClick(this);"
							onchange="updateExecutor(this);"
							cognizance="${target.cognizance}"
							id="targetExecutor"
							name="targetExecutor"
							refer="projectId"
							${disabled}
							size="12" />
				</td>
			</c:if>
		</tr>
		<c:set var="prevTarStateId" value="${target.autoID}"></c:set>

	</c:forEach>
	</table>

</fieldset>

<fieldset>
	<legend><font size="2">程序</font></legend>
	<input type="button" class="flyBT" value="增  加" ${disabled} onclick="addProcedure();"/>

	<c:if test="${isMust == 'yes'}">
		<input type="button" class="flyBT" value="增加下级" ${disabled} onclick="addChildProcedure();"/>
	</c:if>

	<input type="button" class="flyBT" value="删  除" ${disabled} onclick="delProcedures();"/>

	<c:if test="${isMust == 'yes'}">
		<input type="button" class="flyBT" value="设置必做底稿" onclick="setMustTask();"/>
		<input type="button" class="flyBT" value="程序库" onclick="selectProcedure();" />
	</c:if>

	<table id="procedureTable" cellSpacing="1" style="margin-top: 5px;" cellPadding="2" width="100%" bgColor="#6595d6" border="0" align="center">
	    <tr class="DGtd">
			<td noWrap align="center" bgColor="#b9c4d5">选</td>
			<td noWrap align="center" bgColor="#b9c4d5">状态</td>
			<td noWrap align="center" bgColor="#b9c4d5">编号</td>
			<td noWrap align="center" bgColor="#b9c4d5">实质性程序</td>
			<td noWrap align="center" bgColor="#b9c4d5">相关底稿</td>
			<td noWrap align="center" bgColor="#b9c4d5">执行人</td>
			<td noWrap align="center" bgColor="#b9c4d5">认定</td>
			<td noWrap align="center" bgColor="#b9c4d5">备注</td>
	    </tr>

		<c:forEach items="${procedureList}" var="procedure" varStatus="var">
			<tr proAutoId="${procedure.autoId}" valign="top">
				<c:if test="${procedure.level0 == 0}">
					<c:set var="tdColor" value="#E4E8EF"></c:set>
				</c:if>

				<c:if test="${procedure.level0 != 0}">
					<c:set var="tdColor" value="#FFFFFF"></c:set>
				</c:if>
				<td bgcolor="${tdColor }" nowrap="nowrap">
					<c:forEach begin="0" end="${procedure.level0}" step="1">
						&nbsp;&nbsp;&nbsp;&nbsp;
					</c:forEach>
					<img style="cursor: hand;" src="${pageContext.request.contextPath}/images/nofollow.jpg" proAutoId="${procedure.autoId}"  onclick="showOrHiddenChild(this);"  border="0" />

					<input type="checkbox" proAutoId="${procedure.autoId}" parentId="${procedure.parentId}" id="proAutoId" name="proAutoId" title="${procedure.autoId}" value="${procedure.autoId}" />
				</td>
				<td bgcolor="${tdColor }">
				<select onchange="checkFinish(this);" prevValue="${procedure.state}" proAutoId="${procedure.autoId}" id="proState${procedure.autoId}" name="state">
				    <option value="未完成">未完成</option>
				    <option value="已完成">已完成</option>
				    <option value="不适用">不适用</option>
				</select>
				<script language="javascript">
					selectObj = document.getElementById("proState${procedure.autoId}");
					for(var i=0 ; i < selectObj.options.length; i++) {
						if(selectObj.options[i].value == "${procedure.state}") {
							selectObj.options[i].selected = 'selected';
							break;
						}
					}
				</script>
				</td>
				<td bgcolor="${tdColor }">
					<input type="text" proAutoId="${procedure.autoId}" ${disabled}  onblur="updateProcedure(this);"  id="defineId" name="defineId" title="${procedure.defineId}"  value="${procedure.defineId}" size="3" />
				</td>

				<td align="left" proAutoId="${procedure.autoId}" bgcolor="${tdColor }" title="双击编辑程序" style="cursor:hand;" ${ondblclick}="showProDiv(this);" >
					${procedure.auditProcedure}
				</td>

				<td align="left" bgcolor="${tdColor }" nowrap="nowrap">
					${procedure.manuLinks}
					<input type="text" proAutoId="${procedure.autoId}" ${disabled}  onblur="updateProcedure(this);" id="manuScript${procedure.autoId}" name="manuScript" title="${procedure.manuScript}" value="${procedure.manuScript}" readonly="readOnly" size="12" />
				    <input type="button" class="flyBT" onclick="goSelectTask('${procedure.autoId}');" value="选择底稿" ${disabled} />
				</td>
				<td bgcolor="${tdColor }">
					<input type="text"
		        			proAutoId="${procedure.autoId}"
		        			autoid="176"
							hideresult=true
							multiselect=true
							onclick="onPopDivClick(this);"
							onchange="updateProcedure(this);updateChild(this);"
							id="executor"
							name="executor"
							title="${procedure.executor}"
							value="${procedure.executor}"
							${disabled}
							refer="projectId"
							size="12" />
				</td>

				<td bgColor="${tdColor }">
		        	<input type="text" "
							proAutoId="${procedure.autoId}"
							id="cognizance" "
							name="cognizance" "
							title="${procedure.cognizance}"
							value="${procedure.cognizance}"
							autoid="175"
							autoWidth="320"
							hideresult=true
							multiselect=true
							onclick="onPopDivClick(this);"
							onchange="updateProcedure(this);"
							${disabled}
		        			size="12" />
		        </td>

				<td align="left" proAutoId="${procedure.autoId}" bgcolor="${tdColor }" title="双击编辑程序备注" style="cursor:hand;" ${ondblclick}="showProRemarkDiv(this);" >
					${procedure.remark}
				</td>
			</tr>
		</c:forEach>
	</table>
</fieldset>

<c:if test="${correlationTaskList != null }">
	<br/>
	<mt:DataGridPrintByBean name="jpbm_correlationTaskList"/>
</c:if>

</c:if>

</body>
</html>

<script type="text/javascript">

var parentTaskId = "${parentTaskId}";
var projectId = document.getElementById("projectId").value;

if(projectId=="") {
	projectId = "${userSession.curProjectId}";
	document.getElementById("projectId").value = projectId;
}

//---------------------------
// 打印
//---------------------------
function goPrint() {
	var url = "${pageContext.request.contextPath}/oa/task.do?method=printTarAndPro"
			+ "&taskId=" + parentTaskId
			+ "&projectId=" + projectId
			+ "&random=" + Math.random();
	window.open(url, '', 'height=480,width=640,location=no;');
}

//--------------------------
// 打开底稿
//--------------------------
function openTaskFile(objTR){
	var taskId = objTR.taskId;
	var fullPath = objTR.fullPath;

	openFile(taskId);
}

//------------------------------------
//打开文件
//------------------------------------
function openFile(taskId) {
	var url = "${pageContext.request.contextPath}/taskCommon.do?method=fileOpen"
			+ "&taskId=" + taskId
			+ "&projectId=" + projectId
			+ "&random=" + Math.random();
	myOpenUrl(url);
}

//------------------------------------
//相关底稿打开底稿
//------------------------------------
function openFileByLink(taskId) {
	openFile(taskId);
}

//------------------------------------
//相关底稿打开表页
//------------------------------------
function openFileByLink2(taskId,sheetTaskCode) {
	var url = "${pageContext.request.contextPath}/taskCommon.do?method=fileOpen"
			+ "&taskId=" + taskId
			+ "&projectId=" + projectId
			+ "&sheetTaskCode=" + sheetTaskCode
			+ "&random=" + Math.random();
	myOpenUrl(url);
}


//------------------------------------
// 增加目标
//------------------------------------
function addTarget() {
	if(parentTaskId == "" || parentTaskId =="0") {
		alert("非法参数!!parentTaskId=" + parentTaskId);
		return;
	}

	xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
	var url = "${pageContext.request.contextPath}/oa/target.do?method=add&parentTaskId=" + parentTaskId
			+ "&projectId=" + projectId
			+ "&random=" + Math.random();

	xmlHttp.open("POST", url, false);
	xmlHttp.send();
	var strResult = unescape(xmlHttp.responseText);

	if(strResult.indexOf('ok') >= 0 ){
		//alert("添加成功");
		window.location.reload();
	}else {
		alert("添加目标失败：" + strResult);
	}
}


//------------------------------------
// 删除多个目标
//------------------------------------
function delTargets() {
	var checkBoxs = document.getElementsByName("tarAutoId");

	var isChecked = false;
	for(var i=0; i < checkBoxs.length; i++) {
		if(checkBoxs[i].checked == true) {
			isChecked = true;
			break;
		}
	}
	if(!isChecked) {
		alert("请选择要删除的目标");
		return;
	}

	for(var i=0; i < checkBoxs.length; i++) {
		if(checkBoxs[i].checked == true) {
			delTarget(checkBoxs[i].value);
		}
	}
	window.location.reload();
}

//------------------------------------
// 删除目标
//------------------------------------
function delTarget(autoId) {
	xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
	var url = "${pageContext.request.contextPath}/oa/target.do?method=remove&autoId=" + autoId
			+ "&projectId=" + projectId
			+ "random=" + Math.random();

	xmlHttp.open("POST", url, false);
	xmlHttp.send();
}

//------------------------------------
// 更新目标
//------------------------------------
function updateTarget(obj) {
	var val = obj.value;
	var att = obj.name;
	var autoId = obj.tarAutoId;

	var possStr = "method=update"
				+ "&projectId=" + projectId
				+ "&autoId=" + autoId
		        + "&val=" + encodeURI(val)
		        + "&att=" + att
		        + "&random=" + Math.random();

	var url = "${pageContext.request.contextPath}/oa/target.do";

	var xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
	xmlHttp.open("POST",url,false);
	xmlHttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
	xmlHttp.send(possStr);
}


/////////////////////////////////////////////////////////////////////////////////////////////////

//------------------------------------
// 增加程序
//------------------------------------
function addProcedure() {
	if(parentTaskId == "" || parentTaskId =="0") {
		alert("非法参数!!parentTaskId=" + parentTaskId);
		return;
	}

	xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");

	var url = "${pageContext.request.contextPath}/oa/procedure.do?method=add&parentTaskId=" + parentTaskId
			+ "&projectId=" + projectId
			+ "&parentId=0"
			+ "&random=" + Math.random();

	xmlHttp.open("POST", url, false);
	xmlHttp.send();
	var strResult = unescape(xmlHttp.responseText);
	if(strResult.indexOf('ok') >= 0 ){
		//alert("添加程序成功");
		window.location.reload();
	}else {
		alert("添加程序失败：" + strResult);
	}
}

//------------------------------------
// 删除多个程序
//------------------------------------
function delProcedures() {

	var checkBoxs = document.getElementsByName("proAutoId");

	var isChecked = false;

	var childs;

	for(var i=0; i < checkBoxs.length; i++) {
		if(checkBoxs[i].checked == true) {

			//先判断有没有下级
			childs = document.getElementsByName("proAutoId");
			for(var j=0; j < checkBoxs.length; j++) {
				if(!childs[j].checked && childs[j].parentId && childs[j].parentId == checkBoxs[i].value) {
					alert("请先删除下级程序!!");
					return;
				}
			}

			isChecked = true;
			break;
		}
	}

	if(!isChecked) {
		alert("请选择要删除的程序");
		return;
	}

	for(var i=0; i < checkBoxs.length; i++) {
		if(checkBoxs[i].checked == true) {
			delProcedure(checkBoxs[i].value);
		}
	}
	window.location.reload();
}

//------------------------------------
// 删除程序
//------------------------------------
function delProcedure(autoId) {

	xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
	var url = "${pageContext.request.contextPath}/oa/procedure.do?method=remove&autoId=" + autoId
			+ "&projectId=" + projectId
			+ "&random=" + Math.random();

	xmlHttp.open("POST", url, false);
	xmlHttp.send();
}


//------------------------------------
// 更新程序
//------------------------------------
function updateProcedure(obj) {
	var val = obj.value;
	var att = obj.name;
	var autoId = obj.proAutoId;

	var possStr = "method=update"
				+ "&projectId=" + projectId
				+ "&autoId=" + autoId
		        + "&val=" + encodeURI(val)
		        + "&att=" + att
		        + "&random=" + Math.random();

	var url = "${pageContext.request.contextPath}/oa/procedure.do";

	var xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
	xmlHttp.open("POST",url,false);
	xmlHttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
	xmlHttp.send(possStr);
}

//------------------------------------
function goSelectTask(autoId) {

	var manuScriptTagId = "manuScript" + autoId;

	var manuScriptValue = document.getElementById(manuScriptTagId).value;

	var url = "${pageContext.request.contextPath}/taskCommon.do?method=selectTask"
		+ "&projectId=" + projectId
		+ "&manuScriptTagId=" + manuScriptTagId
		+ "&manuScriptValue=" + manuScriptValue
		+ "&parentTaskId=" + parentTaskId;
	open(url,'','width=500,height=400,toolbar=no,menubar=no,scrollbars=yes,resizable=yes,location=no,status=no');
}

//设置必做底稿
function setMustTask() {
	var url = "${pageContext.request.contextPath}/taskCommon.do?method=selectTask"
			+ "&projectId=" + projectId
			+ "&manuScriptTagId=isMust"
			+ "&parentTaskId=" + parentTaskId;
	open(url,'','width=500,height=400,toolbar=no,menubar=no,scrollbars=yes,resizable=yes,location=no,status=no');
}

//----------------------------------
// 显示修改目标的文本框
//----------------------------------
function showTargetDiv(obj) {
	divObj = document.getElementById("divTarget");
	blockObj =  document.getElementById("divBlock");
	textTargetObj = document.getElementById("textTarget");
	textTargetObj.value = jsTrim(obj.innerHTML);
	divObj.style.display = "";
	blockObj.style.display = "";

	document.getElementById("autoid").value = jsTrim(obj.tarAutoId);
}

function hiddenTargetDiv() {
	divObj = document.getElementById("divTarget");
	blockObj =  document.getElementById("divBlock");
	divObj.style.display = "none";
	blockObj.style.display = "none";
}


//----------------------------------
// 显示修改程序的文本框
//----------------------------------
function showProDiv(obj) {
	divObj = document.getElementById("divProduce");
	blockObj =  document.getElementById("divBlock");
	textProObj = document.getElementById("textProduce");
	textProObj.value = jsTrim(obj.innerHTML);
	divObj.style.display = "";
	blockObj.style.display = "";

	document.getElementById("autoid").value = jsTrim(obj.proAutoId);
}

function hiddenProDiv() {
	divObj = document.getElementById("divProduce");
	blockObj =  document.getElementById("divBlock");
	divObj.style.display = "none";
	blockObj.style.display = "none";
}

function tempObj() {
	var value;
	var name;
	var proAutoId;
}

//----------------------------------
// 更新目标
//----------------------------------
function saveTarget() {
	var val = document.getElementById("textTarget").value;

	if(val.length >200) {
		alert("不能多于200个字!!");
		return false;
	}

	var att = "auditTarget";
	var autoid = document.getElementById("autoid").value;

	updateTarget2(autoid,att,val);
	location.reload();
}


//------------------------------------
// 更新目标
//------------------------------------
function updateTarget2(autoid,att,val) {

	var possStr = "method=update"
				+ "&projectId=" + projectId
				+ "&autoId=" + autoid
		        + "&val=" + encodeURI(val)
		        + "&att=" + att
		        + "&random=" + Math.random();

	var url = "${pageContext.request.contextPath}/oa/target.do";

	var xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
	xmlHttp.open("POST",url,false);
	xmlHttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
	xmlHttp.send(possStr);
}


//----------------------------------
// 更新执行程序
//----------------------------------
function saveProcedure() {
	var val = document.getElementById("textProduce").value;

	if(val.length >1000) {
		alert("不能多于1000个字!!");
		return false;
	}

	var att = "auditprocedure";
	var autoid = document.getElementById("autoid").value;

	updateProcedure2(autoid,att,val);
	location.reload();
}

//------------------------------------
// 更新程序
//------------------------------------
function updateProcedure2(autoid,att,val) {

	var possStr = "method=update"
				+ "&projectId=" + projectId
				+ "&autoId=" + autoid
		        + "&val=" + encodeURI(val)
		        + "&att=" + att
		        + "&random=" + Math.random();

	var url = "${pageContext.request.contextPath}/oa/procedure.do";

	var xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
	xmlHttp.open("POST",url,false);
	xmlHttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
	xmlHttp.send(possStr);
}

//--------------------
// 去空格函数
//--------------------
function jsTrim(str) {
    return str.replace(/^\s* | \s*$/g,"");
}

//检查前面的程序是否完成
function checkFinish(obj) {
	var autoId = obj.proAutoId;
	var val = obj.value;
	var result = "ok";

	if(val == "已完成") {
		var query_String = "autoId=" + autoId

		var url = "${pageContext.request.contextPath}/oa/task.do?method=getNoFinishProcedure"
				+ "&projectId=" + projectId;

		result = ajaxLoadPageSynch(url,query_String);
	}

	if(result.indexOf('ok') < 0) {
		alert("请先完成前面的程序:" + result);

		//还原选择状态
		var selObj = document.getElementById("proState" + autoId);
		for(var i=0 ; i < selObj.options.length; i++) {
			if(selObj.options[i].value == obj.prevValue) {
				selObj.options[i].selected = 'selected';
				break;
			}
		}

		return;
	} else {
		updateProcedure(obj);
	}

}

//根据目标更新程序执行人
function updateExecutor(obj) {
	var targetCognizance = obj.cognizance;
	var co = targetCognizance.split(",");

	var cognizances = document.getElementsByName("cognizance");
	var executors = document.getElementsByName("executor");

	for(var i=0; i < cognizances.length; i++) {

		//找到程序认定
		if(cognizances[i].proAutoId) {

			for(var k=0; k < executors.length; k++) {

				//找到程序的执行人
				if(executors[k].proAutoId == cognizances[i].proAutoId) {

					for(var j=0; j < co.length; j++) {

						//找到与目标对应的程序
						if(cognizances[i].value.indexOf(co[j]) > -1) {

							//如果程序执行人为空,则直接赋值更新
							if(executors[k].value == "") {
								executors[k].value = obj.value;
							} else {
								var ex = obj.value.split(",");
								for(var l=0; l < ex.length; l++) {

									//如果执行人不在程序执行人列表中
									if(executors[k].value.indexOf(ex[l]) < 0) {
										executors[k].value += "," + ex[l];
									}
								}
							}
						}
					}

					//更新程序的执行人
					updateProcedure(executors[k]);
				}
			}
		}
	}
}

//----------------------------------
// 更新目标状态
//----------------------------------
function checkPrevTarState(autoId) {
	var selectObj = document.getElementById("tarState" + autoId);
	var prevTarStateObj = document.getElementById("prevTarState" + autoId);
	var selectValue = selectObj.options[selectObj.selectedIndex].value;

	if(prevTarStateObj) {
		if(prevTarStateObj.value != "") {
			var prevTarStateValue = document.getElementById("tarState" + prevTarStateObj.value).value;

			if(prevTarStateValue == "未完成" && selectValue == "已完成") {
				for(var i=0 ; i < selectObj.options.length; i++) {
					if(selectObj.options[i].value == selectObj.prevValue) {
						selectObj.options[i].selected = 'selected';
						break;
					}
				}

				var prevTarDefineId = document.getElementById("tarDefineId" + prevTarStateObj.value).value;
				alert("请先完成上一个目标:" + prevTarDefineId);
			}
		}
	}

	updateTarget(selectObj);
}

//--------------------------------
// 为环节分工
//--------------------------------
function updateTacheExecutor(tache) {
	var taskId = tache.taskId;
	var query_String = "taskId=" + taskId + "&executor=" + tache.value;

	var url = "${pageContext.request.contextPath}/oa/task.do?method=setExecutor"
			+ "&projectId=" + projectId;

	var result = ajaxLoadPageSynch(url,query_String);

}

//--------------------------------
// 选择程序
//--------------------------------
function selectProcedure() {
	var url = "${pageContext.request.contextPath}/taskCommon.do?method=procedureLib"
			+ "&projectId=" + projectId
			+ "&parentTaskId=" + parentTaskId;
	open(url,'','width=500,height=400,toolbar=no,menubar=no,scrollbars=yes,resizable=yes,location=no,status=no');
}

//--------------------------------
// 增加下级程序
//--------------------------------
function addChildProcedure() {
	var checkBoxs = document.getElementsByName("proAutoId");

	var isChecked = false;
	var proAutoId = "";
	for(var i=0; i < checkBoxs.length; i++) {
		if(checkBoxs[i].checked == true) {

			isChecked = true;
			proAutoId = checkBoxs[i].value;
			break;
		}
	}

	if(!isChecked) {
		alert("请选择上级程序");
		return;
	}

	if(parentTaskId == "" || parentTaskId =="0") {
		alert("非法参数!!parentTaskId=" + parentTaskId);
		return;
	}

	xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");

	var url = "${pageContext.request.contextPath}/oa/procedure.do?method=add&parentTaskId=" + parentTaskId
			+ "&projectId=" + projectId
			+ "&parentId=" + proAutoId
			+ "&random=" + Math.random();

	xmlHttp.open("POST", url, false);
	xmlHttp.send();
	var strResult = unescape(xmlHttp.responseText);
	if(strResult.indexOf('ok') >= 0 ){
		//alert("添加程序成功");
		window.location.reload();
	}else {
		alert("添加程序失败：" + strResult);
	}
}

function showOrHiddenChild(parent) {
	if(parent.proAutoId) {
		var objParent = document.getElementById("parentId" + parent.proAutoId);
		if(objParent) {
			if(objParent.style.display == "none") {
				objParent.style.display = "";
				parent.src = "/AuditSystem/images/nofollow.jpg";
			} else {
				objParent.style.display = "none";
				parent.src = "/AuditSystem/images/plus.jpg";
			}
		}
	}
}

//---------------------------------
// 更新下级执行人
//---------------------------------
function updateChild(obj) {
	var childs = document.getElementsByName("executor");

	for(var i=0; i < childs.length; i++) {
		if(childs[i].parentId && childs[i].parentId == obj.proAutoId) {
			childs[i].value = obj.value;
			updateProcedure(childs[i]);
		}
	}
}

//----------------------------------
// 更新执行程序
//----------------------------------
function saveProcedureRemark() {
	var val = document.getElementById("textProduceRemark").value;

	if(val.length >100) {
		alert("不能多于100个字!!");
		return false;
	}

	var att = "remark";
	var autoid = document.getElementById("autoid").value;

	updateProcedure2(autoid,att,val);
	location.reload();
}

//----------------------------------
// 显示修改程序的文本框
//----------------------------------
function showProRemarkDiv(obj) {
	divObj = document.getElementById("divProduceRemark");
	blockObj =  document.getElementById("divBlock");
	textProObj = document.getElementById("textProduceRemark");
	textProObj.value = jsTrim(obj.innerHTML);
	divObj.style.display = "";
	blockObj.style.display = "";

	document.getElementById("autoid").value = jsTrim(obj.proAutoId);
}

//----------------------------------
// 更新执行程序
//----------------------------------
function hiddenProRemarkDiv() {
	divObj = document.getElementById("divProduceRemark");
	blockObj =  document.getElementById("divBlock");
	divObj.style.display = "none";
	blockObj.style.display = "none";
}

//----------------------------------
// 批量导入程序
//----------------------------------
function batchUpload() {
	parent.window.location = "${pageContext.request.contextPath}/taskCommon.do?method=upload"
					+ "&projectId=" + projectId
					+ "&parentTaskId=" + parentTaskId
					+ "&random=" + Math.random();
}
</script>