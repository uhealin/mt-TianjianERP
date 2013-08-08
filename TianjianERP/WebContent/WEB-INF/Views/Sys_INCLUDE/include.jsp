<%@ taglib prefix="mt"   uri="http://www.matech.cn/tag" %>
<%@ taglib prefix="jodd" uri="jodd" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%@page import="com.matech.framework.pub.sys.UTILSysProperty"%>
<%@page import="com.matech.framework.pub.util.ASFuntion"%>

<%	
	ASFuntion include_asf = new ASFuntion() ;
	String useSelect = include_asf.showNull(UTILSysProperty.SysProperty.getProperty("useSelect")).trim();
    boolean isDebug=false;

%>
<meta http-equiv="pragma" content="no-cach" /> 
<meta http-equiv="cache-control" content="no-cache" /> 
<meta http-equiv="expires" content="0" /> 
<script>
	var MATECH_SYSTEM_WEB_ROOT = "${pageContext.request.contextPath}/";
	var btnDenyRight = "${btnDenyRight}" ;
</script>

<link rel="Shortcut Icon" href="${pageContext.request.contextPath}/AS_SYSTEM/favicon.ico">
<link href="${pageContext.request.contextPath}/AS_CSS/style.css" rel="stylesheet" type="text/css"  />

<!-- other -->
<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/protect.js" charset="GBK"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/prototype.js"  charset="GBK"></script>


<!-- ============================EXT JS====================== -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/EXT_CSS/ext-all.css"/>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/AS_CSS/fileuploadfield.css"/>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/AS_CSS/GridSummary.css"/>

<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/jquery.min.js"></script> 
<!-- base -->
<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/ext-base.js"></script>

<script>
	Ext.BLANK_IMAGE_URL = "${pageContext.request.contextPath}/images/default/s.gif" ;
</script>

<!-- pkgs -->
<% if(isDebug){ %>
<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/pkgs/ext-foundation.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/pkgs/cmp-foundation.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/pkgs/ext-dd.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/pkgs/data-foundation.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/pkgs/data-json.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/pkgs/data-grouping.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/pkgs/resizable.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/pkgs/window.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/pkgs/state.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/pkgs/data-list-views.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/pkgs/pkg-tabs.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/pkgs/pkg-buttons.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/pkgs/pkg-toolbars.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/pkgs/pkg-tips.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/pkgs/pkg-tree.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/pkgs/pkg-menu.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/pkgs/pkg-forms.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/pkgs/pkg-grid-foundation.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/pkgs/pkg-grid-editor.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/pkgs/pkg-grid-property.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/pkgs/pkg-grid-grouping.js"></script>

<%}else{ %>
<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/pkgs/pkgs.package.min.js"></script>

<%} %>

<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/ext-lang-zh_CN.js"></script>
<!-- EXT plug -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/EXT_CSS/GroupHeaderPlugin.css" charset="GBK"/>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/EXT_CSS/TreeGrid.css" />  
<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/GroupHeaderPlugin.js" charset="GBK"></script>
<script type='text/javascript' src='${pageContext.request.contextPath}/AS_INCLUDE/RowExpander.js'></script>
<script type='text/javascript' src='${pageContext.request.contextPath}/AS_INCLUDE/TreeGrid.js'></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/MultiCellSelectionModel.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/BufferView.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/Ext.ButtonPanel.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/FileUploadField.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/NewTime.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/GridSummary.js"></script>

<!-- matech -->

<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/form.js" ></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/validation.js" ></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/common.js" charset="GBK"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/mt_validation.js" charset="GBK"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/copy_paste.js" charset="GBK"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/columns.js" charset="GBK"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/grid.js" charset="GBK"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/cytx.js"  charset="GBK"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/process.js"  ></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/mt_combox.js" charset="GBK"></script>


