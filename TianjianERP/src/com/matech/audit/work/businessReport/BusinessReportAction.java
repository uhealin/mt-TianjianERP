package com.matech.audit.work.businessReport;

import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.businessReport.BusinessReportSugesstionVO;
import com.matech.audit.service.businessReport.BusinessReportVO;
import com.matech.audit.service.groupVindicate.GroupVindicateVO;
import com.matech.audit.service.placard.PlacardService;
import com.matech.audit.service.placard.model.PlacardTable;
import com.matech.audit.service.userpopedom.UserPopedomService;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.StringUtil;
import com.matech.framework.pub.util.WebUtil;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

public class BusinessReportAction extends MultiActionController
{
  private static final String list = "/businessReport/addbusinesApplySkip.jsp";
  private static final String list1 = "/businessReport/list.jsp";
  private static final String addAndEdit = "/businessReport/addAndEdit.jsp";
  private static final String audit = "/businessReport/addAndEdit.jsp";
  private static final String view = "/businessReport/view.jsp";
  private static final String vindicateUpdate = "/businessReport/vindicateUpdate.jsp";

  public ModelAndView list(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    ModelAndView modelAndView = new ModelAndView("/businessReport/list.jsp");

    DataGridProperty pp = new DataGridProperty();
    Connection conn = new DBConnect().getConnect();
    UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
    String userid = userSession.getUserId();
    ASFuntion CHF = new ASFuntion();
    String menuid = CHF.showNull(request.getParameter("menuid"));
    String popedom = "";

    UserPopedomService userPopedomService = new UserPopedomService(conn);
    popedom = userPopedomService.getUserIdPopedom(userid, menuid);

    String ppSql = "";
    ppSql = 
     //CASE kb.state WHEN '复核通过' THEN  CONCAT ('<span style=\'color:green\'>',kb.state,'<span>')WHEN '不复核' THEN  CONCAT ('<span style=\'color:green\'>',kb.state,'<span>')WHEN '暂缓通过' THEN  CONCAT ('<span style=\'color:red\'>',kb.state,'<span>')ELSE CONCAT ('<span style=\'color:blue\'>由',kb.state ,'受理<span>')END
    "SELECT kb.uuid,kb.report_level,kv.group_name as kvgroup_name,kb.property,kb.company_name as kbcompany_name,kb.report_type as kbreport_type,ku.Name AS sname,ka.name as kaname ,kd.departname AS sqdepartment,kb.reaudit_time,kb.except_complete_time,kd2.departname AS ssdeparment, kb.property as maxtime,kb.cancelstate, kb.audit_departmentid,kb.apply_userid,kb.audit_groupname as kbgroupname,  ku2.name as kbsuser,   kb.last_audit_time as kbstime,  IF(kb.appoint_ind='1',CASE kb.state WHEN '复核通过' THEN  CONCAT ('<span style=\\'color:green\\'>',kb.state,'<span>')WHEN '不复核' THEN  CONCAT ('<span style=\\'color:green\\'>',kb.state,'<span>')WHEN '暂缓通过' THEN  CONCAT ('<span style=\\'color:red\\'>',kb.state,'<span>')ELSE CONCAT ('<span style=\\'color:blue\\'>由',ku1.Name,'受理<span>')END,'<span style=\\'color:blue\\'>此项目尚未分配</span>')  AS aastate,kb.state,kb.appoint_human  FROM K_BUSINESS_REPORT kb LEFT JOIN k_user ku ON kb.apply_userid=ku.id  LEFT JOIN k_department kd ON kd.autoid=ku.departmentid  left join k_area ka on ka.autoid=ku.departid  LEFT JOIN k_user ku1 ON ku1.id=kb.appoint_human  LEFT JOIN k_user ku2 ON ku2.id=kb.last_audit_uid  LEFT JOIN k_department kd2 ON kd2.autoid=kb.audit_departmentid  LEFT JOIN k_Vindicate_group kv ON kb.audit_groupname=kv.uuid  where 1=1  and kb.cancelstate!='作废' and (kb.audit_departmentid IN (" +  
     // "SELECT kb.uuid,kb.report_level,kv.group_name as kvgroup_name,kb.property,kb.company_name as kbcompany_name,kb.report_type as kbreport_type,ku.Name AS sname,ka.name as kaname ,kd.departname AS sqdepartment,kb.reaudit_time,kb.except_complete_time,kd2.departname AS ssdeparment, kb.property as maxtime,kb.cancelstate, kb.audit_departmentid,kb.apply_userid,kb.audit_groupname as kbgroupname,  ku2.name as kbsuser,   kb.last_audit_time as kbstime,  IF(kb.appoint_ind='1',IF(kb.state!='复核通过' ,IF(kb.state!='不复核' ,IF(kb.state!='暂缓通过',CONCAT('由',ku1.Name,'受理'),kb.state),kb.state) ,kb.state),'此项目尚未分配')  AS aastate,kb.state,kb.appoint_human  FROM K_BUSINESS_REPORT kb LEFT JOIN k_user ku ON kb.apply_userid=ku.id  LEFT JOIN k_department kd ON kd.autoid=ku.departmentid  left join k_area ka on ka.autoid=ku.departid  LEFT JOIN k_user ku1 ON ku1.id=kb.appoint_human  LEFT JOIN k_user ku2 ON ku2.id=kb.last_audit_uid  LEFT JOIN k_department kd2 ON kd2.autoid=kb.audit_departmentid  LEFT JOIN k_Vindicate_group kv ON kb.audit_groupname=kv.uuid  where 1=1  and kb.cancelstate!='作废' and (kb.audit_departmentid IN (" + 
      popedom + ") OR kb.apply_userid='" + userid + "' or kb.uuid IN (SELECT kr_uuid FROM k_business_report_suggestions WHERE  suggestion_id ='" + userid + "') ) " + 
      " ${group_name} ${sname} ${property} ${sqdepartment} ${reaudit_time} ${except_complete_time} " + 
      " union " + 
      " SELECT '','',' ','2999-12-12'  as property,'已复核比例',CONCAT((SELECT COUNT(UUID) FROM k_business_report kb WHERE  state='复核通过'  and kb.cancelstate!='作废'  and (kb.audit_departmentid IN (" + popedom + ") OR kb.apply_userid='" + userid + "'  or kb.uuid IN (SELECT kr_uuid FROM k_business_report_suggestions WHERE  suggestion_id ='" + userid + "') )  ${group_name} ${sname} ${property} ${sqdepartment} ${reaudit_time} ${except_complete_time} )," + 
      " '/'," + 
      " (SELECT COUNT(UUID) FROM k_business_report kb where 1=1 and kb.cancelstate!='作废'  and (kb.audit_departmentid IN (" + popedom + ") OR kb.apply_userid='" + userid + "'  or kb.uuid IN (SELECT kr_uuid FROM k_business_report_suggestions WHERE  suggestion_id ='" + userid + "' ) ) ${group_name} ${sname} ${property} ${sqdepartment} ${reaudit_time} ${except_complete_time})),'','','','','','','','','','-','','','','-','','-'";

    pp.setTableID("BusinessReportAction_tableid");
    pp.setPageSize_CH(50);
    pp.setCustomerId("");
    pp.setWhichFieldIsValue(1);
    pp.setInputType("radio");
    pp.setOrderBy_CH("property");
    pp.setDirection("desc");
    pp.setPrintEnable(true);
    pp.setPrintVerTical(false);
    pp.setTrActionProperty(true);
    pp.setTrAction("uuid=${uuid}");

    pp.setSQL(ppSql);
    pp.addColumn("处理状态", "aastate");
    pp.addColumn("是否加急", "report_level");
    pp.addColumn("组别", "kvgroup_name");
    pp.addColumn("公司名称", "kbcompany_name");
    pp.addColumn("报告类型", "kbreport_type");
    pp.addColumn("申请时间", "property");
    pp.addColumn("送审时间", "reaudit_time");
    pp.addColumn("到期时间", "except_complete_time");
    pp.addColumn("送审区域", "kaname");
    pp.addColumn("送审部门", "sqdepartment");

    pp.addColumn("送审人", "sname");
    pp.addColumn("最后处理人", "kbsuser");

    pp.addColumn("最后处理时间", "kbstime");

    pp.addSqlWhere("group_name", " and  kb.audit_groupname like '%${group_name}%'  ");
    pp.addSqlWhere("company_name", " and  kb.company_name like '%${company_name}%'  ");
    pp.addSqlWhere("report_type", " and  kb.report_type like '%${report_type}%'  ");
    pp.addSqlWhere("sname", " and  kb.apply_userid like '%${sname}%'  ");
    pp.addSqlWhere("property", " and  kb.property like '%${property}%'");
    pp.addSqlWhere("sqdepartment", " and  kb.audit_departmentid like '%${sqdepartment}%'");
    pp.addSqlWhere("reaudit_time", " and  kb.reaudit_time like '%${reaudit_time}%'");
    pp.addSqlWhere("except_complete_time", " and  kb.except_complete_time like  '%${except_complete_time}%'");

    request.getSession().setAttribute("DGProperty_" + pp.getTableID(), pp);
    return modelAndView;
  }

  public ModelAndView auditBusinesReportList(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    Connection conn = new DBConnect().getConnect("");
    DbUtil dbUtil = new DbUtil(conn);

    WebUtil webUtil = null;
    return null;
  }

  public ModelAndView auditBusinesReport(HttpServletRequest request, HttpServletResponse response) throws Exception
  {
    Connection conn = new DBConnect().getConnect("");
    DbUtil dbUtil = new DbUtil(conn);

    WebUtil webUtil = new WebUtil(request, response);

    String type = request.getParameter("hiddentype");

    String apply_userid = request.getParameter("apply_userid");
    String uuid = request.getParameter("audituuid");

    String suggestioncontext = request.getParameter("suggestion_context");

    String suggestioncontextChangeInd = request.getParameter("suggestion_help_change_ind");

    String suggestionhelp = request.getParameter("suggestion_help");
    BusinessReportVO vo = (BusinessReportVO)dbUtil.load(BusinessReportVO.class, uuid);
    String sql = "";
    String other = request.getParameter("other");
    UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");

    PlacardTable pt = new PlacardTable();
    ASFuntion CHF = new ASFuntion();

    pt.setIsNotReversion(0);
    pt.setProperty("");
    pt.setIsReversion(Integer.parseInt("0"));
    pt.setAddresserTime(CHF.getCurrentDate() + " " + CHF.getCurrentTime());
    pt.setIsRead(0);
    pt.setCtype("质控预约");
    pt.setImage("");
    pt.setModel("");
    pt.setAddresser(userSession.getUserId());
    BusinessReportSugesstionVO brsv = new BusinessReportSugesstionVO();
    brsv.setKr_uuid(uuid);
    brsv.setSuggestion_user(userSession.getUserName());
    brsv.setSuggestion_time(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    brsv.setSuggestion_context(suggestioncontext);
    brsv.setSuggestion_id(userSession.getUserId());

    if ("0".equals(suggestioncontextChangeInd)) {
      brsv.setSuggestion_help(suggestionhelp);
    }
    brsv.setUuid(StringUtil.getUUID());

    pt.setCaption("预约提醒");

    if (("other".equals(type)) && (other != null) && (!"".equals(other))) {
      sql = "update k_business_report set appoint_ind='1',appoint_human=" + other + " where uuid='" + uuid + "' and appoint_human=" + userSession.getUserId();
    }

    if ("no".equals(type)) {
      sql = "update k_business_report set state='不复核',appoint_ind='1',last_audit_uid='" + userSession.getUserId() + "',last_audit_time='" + StringUtil.getCurDateTime() + "'  where uuid='" + uuid + "'  and appoint_human=" + userSession.getUserId();
      pt.setMatter("您预约的" + vo.getCompany_Name() + "报告项目质量控制复核预约流程已结束！");
      pt.setAddressee(apply_userid);
    }

    if ("yes".equals(type)) {
      sql = "update k_business_report set state='复核通过',appoint_ind='1',last_audit_uid='" + userSession.getUserId() + "',last_audit_time='" + StringUtil.getCurDateTime() + "'   where uuid='" + uuid + "'  and appoint_human=" + userSession.getUserId();

      pt.setMatter("您预约的" + vo.getCompany_Name() + "报告项目质量控制复核预约流程已结束！");
      pt.setAddressee(apply_userid);
    }

    if ("wait".equals(type)) {
      sql = "update k_business_report set state='暂缓通过',appoint_ind='1'  where uuid='" + uuid + "'  and appoint_human=" + userSession.getUserId();

      pt.setMatter("您预约的" + vo.getCompany_Name() + "报告项目质量控制复核预约流程需要暂缓通过！");
      pt.setAddressee(apply_userid);
    }
    try {
      dbUtil.executeUpdate(sql);
      PlacardService pm = new PlacardService(conn);
      pt.setUuid(uuid);
      pm.AddPlacard(pt);
      dbUtil.insert(new Object[] { brsv });
    } catch (Exception localException) {
    }
    response.sendRedirect("formDefine.do?method=formListExtView&uuid=66543ce0-3b15-4243-a4df-108b20d0f9bb&where_id=01");
    return null;
  }

  public ModelAndView auditBusinesReportSkip(HttpServletRequest request, HttpServletResponse response) throws Exception
  {
    String uuid = request.getParameter("uuid");

    String groupVindicateUUID = "";
    Double grouppercent = 0.0D;
    String realpercent = "";
    String percentson = "";
    String percentmom = "";
    String percentsql = "";
    GroupVindicateVO groupVindicateVO = null;
    ModelAndView model = new ModelAndView("/businessReport/addAndEdit.jsp");
    Connection conn = new DBConnect().getConnect("");
    DbUtil dbUtil = new DbUtil(conn);
    BusinessReportVO businessReportVO = (BusinessReportVO)dbUtil.load(BusinessReportVO.class, uuid);
    UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");

    List list = dbUtil.getList("k_business_report_suggestions", "kr_uuid", uuid, "suggestion_time");

    groupVindicateUUID = businessReportVO.getAudit_groupname();

    List grouplist = dbUtil.select(GroupVindicateVO.class, "select * from {0} where uuid=?", new Object[] { groupVindicateUUID });
    if (grouplist.size() > 0) {
      groupVindicateVO = (GroupVindicateVO)grouplist.get(0);
      grouppercent = groupVindicateVO.getPercent();
    } else {
      grouppercent = 100.0D;
    }

    percentsql = "SELECT CONCAT( (SELECT COUNT(*) FROM k_business_report WHERE audit_groupname LIKE '" + 
      groupVindicateUUID + "' AND cancelstate!='作废' AND state='不复核')" + 
      " ,'-', " + 
      " (SELECT COUNT(*) FROM k_business_report WHERE audit_groupname LIKE '%" + groupVindicateUUID + "' AND cancelstate!='作废' ) " + 
      " ) AS percent  FROM DUAL;";

    ResultSet rs = dbUtil.getResultSet(percentsql);
    while (rs.next()) {
      realpercent = rs.getString(1);
    }
    if (rs != null) {
      rs.close();
    }

    boolean b = false;
    if (!StringUtil.isBlank(realpercent)) {
      String[] s = realpercent.split("-");
      percentson = s[0]; percentmom = s[1];
      float son = Float.parseFloat(percentson);

      float mom = 0.0F;
      try
      {
        mom = Float.parseFloat(percentmom);
      } catch (Exception e) {
        e.printStackTrace();
        mom = 1.0F;
      }

      b = son / mom > grouppercent;
    }

    model.addObject("businessReportVO", businessReportVO);
    model.addObject("suggestions", list);

    model.addObject("isaudit", "isaudit");
    model.addObject("percentind", Boolean.valueOf(b));
    model.addObject("grouppercent", Double.valueOf(grouppercent));

    model.addObject("doctype", businessReportVO.getTj_report_type());
    return model;
  }

  public ModelAndView addbusinesApply(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    Connection conn = new DBConnect().getConnect("");
    DbUtil dbUtil = new DbUtil(conn);
    WebUtil webUtil = null;
    try
    {
      webUtil = new WebUtil(request, response);
      BusinessReportVO businessReportVO = (BusinessReportVO)webUtil.evalObject(BusinessReportVO.class);
      businessReportVO.setUuid(StringUtil.getUUID());

      UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");

      String strSql = "select uuid from k_Vindicate_group where group_departmentid like '%" + userSession.getUserAuditDepartmentId() + "%' ";

      String groupuuid = dbUtil.queryForString(strSql);
      GroupVindicateVO group = null;

      if (!StringUtil.isBlank(groupuuid)) {
        group = (GroupVindicateVO)dbUtil.load(GroupVindicateVO.class, groupuuid);
        businessReportVO.setAppoint_human(Integer.valueOf(Integer.parseInt(group.getGroup_headman())));
        businessReportVO.setCancelstate("有效");
      } else {
        businessReportVO.setAppoint_human(Integer.valueOf(19));
        businessReportVO.setCancelstate("无效");
      }
      businessReportVO.setProperty(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
      businessReportVO.setState("未审核");
      String remarks = request.getParameter("remarks");
      businessReportVO.setRemarks(remarks);

      businessReportVO.setAppoint_ind("0");

      businessReportVO.setReport_data_receive_ind("0");

      dbUtil.insert(new Object[] { businessReportVO });
    }
    catch (Exception e) {
      e.printStackTrace();
      response.getWriter().write("保存失败");
    }

    response.sendRedirect("formDefine.do?method=formListExtView&uuid=66543ce0-3b15-4243-a4df-108b20d0f9bb&where_id=00&menuid=10001529");
    return null;
  }

  public ModelAndView addbusinesApplySkip(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    String uuid = request.getParameter("uuid");
    String b_audit = request.getParameter("audit");
    String doctype = request.getParameter("doctype");
    ModelAndView model = new ModelAndView("/businessReport/addAndEdit.jsp");
    Connection conn = new DBConnect().getConnect("");
    DbUtil dbUtil = new DbUtil(conn);
    WebUtil webUtil = new WebUtil(request, response);
    UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");

    String strSql = "select uuid from k_Vindicate_group where group_departmentid like '%" + userSession.getUserAuditDepartmentId() + "%' ";

    String groupuuid = dbUtil.queryForString(strSql);
    GroupVindicateVO group = (GroupVindicateVO)dbUtil.load(GroupVindicateVO.class, groupuuid);
    if (StringUtil.isBlank(groupuuid)) {
      model.addObject("hasgroup", "false");
    }
    else {
      model.addObject("hasgroup", "true");
    }
    Map map = new HashMap();
    map.put("audit_groupname", groupuuid);
    model.addObject("businessReportVO", map);
    model.addObject("grouphead", group);
    model.addObject("doctype", doctype);
    model.addObject("isadd", "true");
    return model;
  }

  public ModelAndView updateBusinesReportSkip(HttpServletRequest request, HttpServletResponse response) throws Exception
  {
    String uuid = request.getParameter("uuid");
    ModelAndView model = new ModelAndView("/businessReport/addAndEdit.jsp");
    Connection conn = new DBConnect().getConnect("");
    DbUtil dbUtil = new DbUtil(conn);
    WebUtil webUtil = new WebUtil(request, response);

    BusinessReportVO b = (BusinessReportVO)dbUtil.load(BusinessReportVO.class, uuid);

    UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");

    Integer.parseInt(userSession.getUserId()); b.getApply_userid().intValue();

    model.addObject("businessReportVO", b);
    model.addObject("doctype", b.getTj_report_type());
    model.addObject("isupdate", "true");
    return model;
  }

  public ModelAndView updateBusinesReportBeforeSkip(HttpServletRequest request, HttpServletResponse response) throws Exception
  {
    String uuid = request.getParameter("uuid");
    String re = "no";
    Connection conn = new DBConnect().getConnect("");
    response.setContentType("text/html;charset=utf-8");
    DbUtil dbUtil = new DbUtil(conn);
    WebUtil webUtil = new WebUtil(request, response);
    BusinessReportVO b = (BusinessReportVO)dbUtil.load(BusinessReportVO.class, uuid);

    UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
    if (Integer.parseInt(userSession.getUserId()) == b.getApply_userid().intValue()) {
      re = "ok";
    }

    response.getWriter().write(re);
    return null;
  }

  public ModelAndView viewBusinesReport(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    String uuid = request.getParameter("uuid");

    ModelAndView model = new ModelAndView("/businessReport/addAndEdit.jsp");
    Connection conn = new DBConnect().getConnect("");
    DbUtil dbUtil = new DbUtil(conn);
    WebUtil webUtil = new WebUtil(request, response);
    BusinessReportVO businessReportVO = (BusinessReportVO)dbUtil.load(BusinessReportVO.class, uuid);
    UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
    List list = dbUtil.getList("k_business_report_suggestions", "kr_uuid", uuid, "suggestion_time");
    model.addObject("businessReportVO", businessReportVO);
    model.addObject("suggestions", list);
    model.addObject("view", "true");

    model.addObject("doctype", businessReportVO.getTj_report_type());
    return model;
  }

  public ModelAndView updateBusinesReport(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    String uuid = request.getParameter("hiddenuuid");

    ModelAndView model = new ModelAndView("/businessReport/addAndEdit.jsp");
    Connection conn = new DBConnect().getConnect("");
    DbUtil dbUtil = new DbUtil(conn);
    WebUtil webUtil = new WebUtil(request, response);
    BusinessReportVO businessReportVO = (BusinessReportVO)webUtil.evalObject(BusinessReportVO.class);
    businessReportVO.setUuid(uuid);
    String remarks = request.getParameter("remarks");
    businessReportVO.setRemarks(remarks);
    dbUtil.update(new Object[] { businessReportVO });
    UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");

    response.sendRedirect("formDefine.do?method=formListExtView&uuid=66543ce0-3b15-4243-a4df-108b20d0f9bb&where_id=00&menuid=10001529");
    return null;
  }

  public ModelAndView updateBusinesReportCancelState(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    String uuid = request.getParameter("uuid");
    String cancelstate = request.getParameter("cancelstate");
    String cancelstate_zh = "作废";
    if (cancelstate == "cancelstate") {
      cancelstate_zh = "有效";
    }
    String result = "状态变为" + cancelstate_zh + "成功";
    try {
      Connection conn = new DBConnect().getConnect("");
      DbUtil dbUtil = new DbUtil(conn);
      String sql = "update k_business_report set cancelstate=? where uuid=? and state!='复核通过' and state!='不复核'";
      List l = new ArrayList();
      l.add(cancelstate_zh);
      l.add(uuid);
      dbUtil.update(sql, l);
      dbUtil.execute("delete from k_placard where uuid='" + uuid + "'");
    }
    catch (Exception e) {
      result = "状态变为" + cancelstate_zh + "失败";
    }

    response.sendRedirect("formDefine.do?method=formListExtView&uuid=66543ce0-3b15-4243-a4df-108b20d0f9bb&where_id=00");
    return null;
  }

  public ModelAndView updateBusinesReportDataReceiveCancelState(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    String uuid = request.getParameter("uuid");
    String re = "no";
    try {
      Connection conn = new DBConnect().getConnect("");
      DbUtil dbUtil = new DbUtil(conn);
      String sql = "update k_business_report set report_data_receive_ind=1 where uuid=?";
      List l = new ArrayList();
      l.add(uuid);
      dbUtil.update(sql, l);
      re = "ok";
    } catch (Exception e) {
      re = "no";
    }
    response.getWriter().write(re);
    return null;
  }

  public ModelAndView updateBusinesReportAppointInd(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    String uuid = request.getParameter("uuid");
    String re = "no";
    try {
      Connection conn = new DBConnect().getConnect("");
      DbUtil dbUtil = new DbUtil(conn);
      String sql = "update k_business_report set appoint_ind='1' where uuid=?";
      List l = new ArrayList();
      l.add(uuid);
      dbUtil.update(sql, l);
      re = "ok";
    } catch (Exception e) {
      re = "no";
    }
    response.getWriter().write(re);
    return null;
  }

  public ModelAndView updateVindicateGroupSkip(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String uuid = request.getParameter("uuid");
    String b_audit = request.getParameter("audit");
    ModelAndView model = new ModelAndView("/businessReport/vindicateUpdate.jsp");
    Connection conn = new DBConnect().getConnect("");
    DbUtil dbUtil = new DbUtil(conn);
    WebUtil webUtil = new WebUtil(request, response);
    GroupVindicateVO groupVindicateVO = (GroupVindicateVO)dbUtil.load(GroupVindicateVO.class, uuid);
    model.addObject("groupVindicateVO", groupVindicateVO);
    model.addObject("old_groupHeanMan", groupVindicateVO.getGroup_headman());
    return model;
  }

  public ModelAndView updateVindicateGroup(HttpServletRequest request, HttpServletResponse response) throws Exception
  {
    String uuid = request.getParameter("uuid");

    String old_groupHeanMan = request.getParameter("old_groupHeanMan");
    ModelAndView model = new ModelAndView("/businessReport/addAndEdit.jsp");
    Connection conn = new DBConnect().getConnect("");
    DbUtil dbUtil = new DbUtil(conn);
    WebUtil webUtil = new WebUtil(request, response);
    GroupVindicateVO groupVindicateVO = (GroupVindicateVO)webUtil.evalObject(GroupVindicateVO.class);
    dbUtil.update(new Object[] { groupVindicateVO });
    if ((groupVindicateVO.getGroup_headman() != "") && (groupVindicateVO.getGroup_headman() != null) && 
      (old_groupHeanMan != groupVindicateVO.getGroup_headman())) {
      String sql = "update k_business_report set appoint_human=? where appoint_human=? and state!='复核通过'";
      dbUtil.executeUpdate(sql, new String[] { groupVindicateVO.getGroup_headman(), old_groupHeanMan });
    }
    response.sendRedirect("formDefine.do?method=formListView&uuid=aa2742a8-042d-4aaf-8469-eee86ae48571&formId=aa2742a8-042d-4aaf-8469-eee86ae48571&formTypeId=838a1d67-462d-4b64-a25b-b49935b85658");
    return null;
  }
}