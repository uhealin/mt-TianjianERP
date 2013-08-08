package com.matech.audit.service.info;

import com.matech.audit.service.info.model.TimesReport;
import com.matech.audit.service.process.ProcessService;
import com.matech.audit.service.question.model.Question;
import com.matech.audit.service.regulations.model.Regulations;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.UTILString;
import com.matech.framework.pub.util.WebUtil;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class InfoService
{
  private Connection conn = null;

  public InfoService(Connection conn) {
    this.conn = conn;
  }

  public List<String> getUserBirth(String userId)
    throws Exception
  {
    PreparedStatement ps = null;
    ResultSet rs = null;
    List birthUserList = new ArrayList();
    ASFuntion CHF = new ASFuntion();
    try
    {
      String sql = " SELECT GROUP_CONCAT(autoid) AS departmentids \n FROM ( \n  \tSELECT a.autoid \n  \tFROM k_department a,k_user b \n  \tWHERE b.id=? AND (a.autoid=b.departmentid OR b.ProjectPopedom LIKE CONCAT('%.',a.autoid,'.%')) \n  \tUNION \n  \tSELECT c.autoid \n  \tFROM k_department a,k_user b ,k_department c \n  \tWHERE b.id =? AND a.autoid=b.departmentid \n  \tAND a.ProjectPopedom LIKE CONCAT('%.',c.autoid,'.%') \n  )t\n ";

      String departmentIds = "";
      ps = this.conn.prepareStatement(sql);
      ps.setString(1, userId);
      ps.setString(2, userId);
      rs = ps.executeQuery();
      if (rs.next()) {
        departmentIds = CHF.showNull(rs.getString(1));
      }

      if ("".equals(departmentIds)) {
        departmentIds = "-1";
      }

      sql = " SELECT * FROM ( \n \tSELECT SUBSTR(IFNULL(bornDate,''),6,10) AS birthday,NAME,DATE_SUB(CURDATE(),INTERVAL WEEKDAY(CURDATE()) + 1 DAY) AS weekBeginDate, \n\t\tDATE_SUB(CURDATE(),INTERVAL WEEKDAY(CURDATE()) - 5 DAY) AS weekEndDate,departmentid \n\t\tFROM k_user \n\t) a WHERE birthday BETWEEN SUBSTR(weekBeginDate,6,10) AND SUBSTR(weekEndDate,6,10) \tand  departmentid in(" + 
        departmentIds + ")";

      ps = this.conn.prepareStatement(sql);
      rs = ps.executeQuery();
      while (rs.next())
        birthUserList.add(rs.getString("name"));
    }
    catch (Exception e)
    {
      e.printStackTrace();
      throw e;
    } finally {
      DbUtil.close(rs);
      DbUtil.close(ps);
    }

    return birthUserList;
  }

  public List<String> getCustomerBirth(String userId)
    throws Exception
  {
    PreparedStatement ps = null;
    ResultSet rs = null;
    List birthCustomerList = new ArrayList();
    ASFuntion CHF = new ASFuntion();
    try
    {
      String sql = " SELECT GROUP_CONCAT(customerid) FROM  \n k_customermanager \n  WHERE (user1 = ? OR user2 = ?) \n ";

      String customerIds = "";
      ps = this.conn.prepareStatement(sql);
      ps.setString(1, userId);
      ps.setString(2, userId);
      rs = ps.executeQuery();
      if (rs.next()) {
        customerIds = CHF.showNull(rs.getString(1));
      }

      if ("".equals(customerIds)) {
        customerIds = "-1";
      }

      sql = " SELECT * FROM ( \n \tSELECT SUBSTR(IFNULL(birthday,''),6,10) AS birthday,NAME,DATE_SUB(CURDATE(),INTERVAL WEEKDAY(CURDATE()) + 1 DAY) AS weekBeginDate, \n\t\tDATE_SUB(CURDATE(),INTERVAL WEEKDAY(CURDATE()) - 5 DAY) AS weekEndDate,customerid \n\t\tFROM k_manager \n\t) a WHERE birthday BETWEEN SUBSTR(weekBeginDate,6,10) AND SUBSTR(weekEndDate,6,10) \tand  customerid in(" + 
        customerIds + ")";

      ps = this.conn.prepareStatement(sql);
      rs = ps.executeQuery();
      while (rs.next())
        birthCustomerList.add(rs.getString("name"));
    }
    catch (Exception e)
    {
      e.printStackTrace();
      throw e;
    } finally {
      DbUtil.close(rs);
      DbUtil.close(ps);
    }

    return birthCustomerList;
  }

  public List<Map<String, String>> getRemind(String userId, String loginId, String departmentId)
    throws Exception
  {
    PreparedStatement ps = null;
    ResultSet rs = null;
    List remindList = new ArrayList();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    try
    {
      Calendar calendar = new GregorianCalendar();

      calendar.set(5, 1);
      String monthBeginDate = sdf.format(calendar.getTime());

      calendar.set(5, 1);
      calendar.roll(5, -1);
      String monthEndDate = sdf.format(calendar.getTime());

      String sql = " SELECT COUNT(DISTINCT a.projectid) AS remindcount, '项目预安排' AS remindtype,'项' as unit \n FROM z_projectpre a, z_auditpeople b \n WHERE a.state = 1 \n AND a.projectid = b.projectid\n AND b.userid = '" + 
        userId + "' \n" + 
        " \n" + 
        " UNION ALL \n" + 
        " \n" + 
        " SELECT COUNT(DISTINCT a.projectid) AS remindcount,'预安排答复' AS remindtype,'项' as unit \n" + 
        " FROM z_projectpre a \n" + 
        " WHERE a.state = 2 \n" + 
        " AND a.createuser = '" + loginId + "' \n" + 
        " \n" + 
        " UNION ALL \n" + 
        " \n" + 
        " SELECT SUM(IF(b.holiday IS NULL AND c.workdate IS NULL, 1, 0)) AS remindcount,'当月工时未申报' AS remindtype,'天' as unit \n" + 
        " FROM k_day a \n" + 
        " LEFT JOIN k_holiday b ON a.workdate = b.holiday\n" + 
        " LEFT JOIN (\n" + 
        " \t\tSELECT DISTINCT workdate \n" + 
        " \t\tFROM oa_timesreport \n" + 
        " \t\tWHERE userid = '" + loginId + "' \n" + 
        " \t\tAND workdate >= '" + monthBeginDate + "' \n" + 
        " \t\tAND workdate <= '" + monthEndDate + "'\n" + 
        " ) c ON a.workdate = c.workdate \n" + 
        " WHERE a.workdate >= '" + monthBeginDate + "' \n" + 
        " AND a.workdate <= '" + monthEndDate + "' \n" + 
        " \n" + 
        " UNION ALL \n" + 
        " \n" + 
        " SELECT COUNT(DISTINCT a.projectid) AS remindcount,'工时未审批' AS remindtype,'人天' as unit \n" + 
        " FROM oa_timesreport a,( \n" + 
        " \t\tSELECT DISTINCT projectid \n" + 
        " \t\tFROM z_auditpeople \n" + 
        " \t\tWHERE userid = '" + userId + "' \n" + 
        " \t\tAND role = '项目负责人'\n" + 
        " ) b  \n" + 
        " WHERE a.departmentid = '" + departmentId + "' \n" + 
        " AND a.audituser IS NULL \n" + 
        " AND (a.projectid = b.projectid OR '' LIKE '%合伙人%') \n" + 
        " \n" + 
        " UNION ALL \n" + 
        " \n" + 
        " SELECT COUNT(*) AS remindcount, '项目未评价' AS remindtype,'项' as unit \n" + 
        "  FROM (\n" + 
        " \t\tSELECT h.State AS StateEx \n" + 
        " \t\tFROM ( \n" + 
        " \t\t\tSELECT DISTINCT projectid \n" + 
        " \t\t\tFROM oa_timesreport \n" + 
        " \t\t\tWHERE userid = '" + userId + "' \n" + 
        " \t\t\tAND totalTime > 0 \n" + 
        " \t\t\tAND projectid > 0 \n" + 
        " \t\t) a \n" + 
        " \t\tINNER JOIN z_project t2 ON a.projectid = t2.projectid \n" + 
        " \t\tLEFT JOIN oa_ratingmain h ON h.projectid = a.projectid) t1 \n" + 
        " \t\tWHERE (t1.StateEx = 0 OR t1.StateEx IS NULL) \n" + 
        " \n" + 
        " UNION ALL \n" + 
        " \n" + 
        " SELECT COUNT(*) AS remindcount, '评价未审批' AS remindtype,'项' as unit \n" + 
        " FROM ( \n" + 
        " \t\tSELECT h.State AS StateEx \n" + 
        " \t\tFROM (\n" + 
        " \t\t\t\tSELECT DISTINCT projectid \n" + 
        " \t\t\t\tFROM oa_timesreport a,( \n" + 
        " \t\t\t\t\tSELECT a.autoid AS departmentid \n" + 
        " \t\t\t\t\tFROM k_department a, k_user b \n" + 
        " \t\t\t\t\tWHERE b.loginid = '" + userId + "' \n" + 
        " \t\t\t\t\tAND (a.autoid = b.departmentid OR  b.ProjectPopedom LIKE CONCAT('%.', a.autoid, '.%'))  \n" + 
        " \t\t\t\t) b\n" + 
        " \t\t\t\tWHERE a.departmentid = b.departmentid AND a.totalTime > 0 \n" + 
        " \t\t) a \n" + 
        " \t\tINNER JOIN z_project t2 ON t2.projectid = a.projectid \n" + 
        " \t\tLEFT JOIN oa_ratingmain h ON h.projectid = a.projectid \n" + 
        " \t\tGROUP BY a.projectid \n" + 
        " ) AS t1\n" + 
        " WHERE 1 = 1 AND t1.stateEx = 2 \n" + 
        " \n" + 
        " UNION ALL \n" + 
        " \n" + 
        " SELECT COUNT(1) AS remindcount, '主任室协调排班回复' AS remindtype,'项' as unit \n" + 
        " FROM z_ProjectPreDetailView a \n" + 
        " WHERE a.Accept = 0 \n" + 
        " AND a.departid = 2 \n" + 
        " AND ('合伙人,质控部,员工' LIKE '%合伙人%') \n" + 
        " \n";

      ps = this.conn.prepareStatement(sql);
      rs = ps.executeQuery();
      while (rs.next()) {
        Map map = new HashMap();
        map.put("remindtype", rs.getString("remindtype"));
        map.put("remindcount", rs.getString("remindcount"));
        map.put("unit", rs.getString("unit"));
        remindList.add(map);
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      throw e;
    } finally {
      DbUtil.close(rs);
      DbUtil.close(ps);
    }

    return remindList;
  }

  public List<Regulations> getListRegulations() {
    List list = null;
    String sql = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try
    {
      sql = " select autoId,title,contents,updateTime,publishUserId,attachmentId,memo,property  from oa_regulations order by updateTime desc limit 10";

      ps = this.conn.prepareStatement(sql);
      rs = ps.executeQuery();
      list = new ArrayList();
      while (rs.next()) {
        Regulations regulations = new Regulations();
        regulations.setAutoId(rs.getString("autoId"));
        regulations.setTitle(rs.getString("title"));
        regulations.setContents(rs.getString("contents"));
        regulations.setUpdateTime(rs.getString("updateTime"));
        regulations.setPublishUserId(rs.getString("publishUserId"));
        regulations.setAttachmentId(rs.getString("attachmentId"));
        regulations.setMemo(rs.getString("memo"));
        regulations.setProperty(rs.getString("property"));
        list.add(regulations);
      }
      List localList1 = list;
      return localList1;
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      DbUtil.close(rs);
      DbUtil.close(ps);
    }
    return null;
  }

  public List<Map<String, String>> getFileCenterMenu(String userId)
  {
    List list = null;
    String sql = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    ASFuntion CHF = new ASFuntion();
    try
    {
      sql = " SELECT distinct menu_id,name,act \n FROM(\n \tSELECT * \n \tFROM (  \n \t\tSELECT a.id,a.menu_id,a.name,act \n \t\tFROM s_sysmenu a,k_menuversion c \n \t\tWHERE 1=1 \n \t\tAND c.menuversion='档案管理中心' \n \t\tAND a.id=c.menuid \n \t) a LEFT JOIN ( \n \t\tSELECT id AS userid,popedom  \n \t\tFROM k_user \n \t\tWHERE id = ? \n \t\tUNION  \n \t\tSELECT userid,popedom \n \t\tFROM k_userrole a,k_role b \n \t\tWHERE userid = ? AND a.rid = b.id \n \t) b ON b.popedom LIKE CONCAT('%.', a.id, '.%') \n )t WHERE userid IS NOT NULL  \n order by menu_id  \n";

      ps = this.conn.prepareStatement(sql);
      ps.setString(1, userId);
      ps.setString(2, userId);
      rs = ps.executeQuery();
      list = new ArrayList();
      while (rs.next()) {
        Map map = new HashMap();
        map.put("id", CHF.showNull(rs.getString(1)));
        map.put("name", CHF.showNull(rs.getString(2)));
        map.put("url", CHF.showNull(rs.getString(3)));

        list.add(map);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      DbUtil.close(rs);
      DbUtil.close(ps);
    }
    return list;
  }

  public List getTitleHint(UserSession userSession)
    throws Exception
  {
    PreparedStatement ps = null;
    PreparedStatement ps1 = null;
    ResultSet rs = null;
    ResultSet rs1 = null;
    try {
      List list = loadPccpaTitleHint(userSession);

      String userid = userSession.getUserId();
      String departmentid = userSession.getUserAuditDepartmentId();

      String sql = "";

      sql = "select distinct a.* from s_titlehint a, k_userrole b  \twhere b.userid = '" + 
        userid + "' " + 
        "\tand (a.roleids='all' or concat(',',a.roleids,',') like concat('%,',b.rid,',%')) " + 
        "\torder by a.property,a.id";
      ps = this.conn.prepareStatement(sql);
      rs = ps.executeQuery();
      String actname;
      while (rs.next())
      {
        String hintsql = rs.getString("hintsql");
        String hinttxt = rs.getString("hinttxt");
        String acturl = rs.getString("acturl");
        actname = rs.getString("actname");

        hintsql = hintsql.replaceAll("\\$CURUSER", userid);
        hintsql = hintsql.replaceAll("\\$curDepartmentid", departmentid);
        hintsql = hintsql.replaceAll("\\$USERNAME", userSession.getUserName());

        String[] t1 = UTILString.getVaribles(hinttxt);
        String[] t2 = UTILString.getVaribles(acturl);

        ps1 = this.conn.prepareStatement(hintsql);
        rs1 = ps1.executeQuery();
        String strParam = "";
        int count = 1;
        while (rs1.next()) {
          try {
            count = rs1.getInt(1);
          } catch (Exception localException1) {
          }
          for (int j = 0; j < t1.length; j++) {
            try {
              strParam = rs1.getString(t1[j]);
              if (strParam == null)
                strParam = "";
            } catch (Exception localException2) {
            }
            hinttxt = hinttxt.replaceAll("\\$\\{" + t1[j] + "\\}", strParam);
          }

          if (t2 != null) {
            for (int j = 0; j < t2.length; j++) {
              try {
                strParam = rs1.getString(t2[j]);
                if (strParam == null)
                  strParam = "";
              } catch (Exception localException3) {
              }
              acturl = acturl.replaceAll("\\$\\{" + t2[j] + "\\}", strParam);
            }
          }
        }
        DbUtil.close(rs1);
        DbUtil.close(ps1);

        if (count != 0) {
          Map map = new HashMap();
          map.put("hinttxt", hinttxt);
          map.put("acturl", acturl);
          map.put("actname", actname);
          list.add(map);
        }
      }
      DbUtil dbUtil = new DbUtil(this.conn);
      List<Map> processList = dbUtil.getList("select pkey,pname from MT_JBPM_PROCESSDEPLOY");
      for (Map process : processList) {
        Map titleHint = new HashMap();
        String pkey = process.get("pkey").toString();
        String pname = process.get("pname").toString();
        String psql = ProcessService.getDealingSql(pkey, userSession.getUserId());

        int taskCount = dbUtil.queryForInt("select count(a.taskId) from (" + psql + ") a");
        if (taskCount >= 1) {
          titleHint.put("hinttxt", MessageFormat.format("我有 {0}个  {1}任务未处理", new Object[] { Integer.valueOf(taskCount), pname }));
          titleHint.put("actname", MessageFormat.format("未处理 {0}", new Object[] { pname }));
          titleHint.put("acturl", MessageFormat.format("process.do?method=auditList&pkey={0}", new Object[] { pkey }));
          list.add(titleHint);
        }
      }
      System.out.println(list);
      List localList1 = list;
      return localList1;
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    } finally {
      DbUtil.close(rs);
      DbUtil.close(ps);
    }
  }

  public List getCtype()
    throws Exception
  {
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      List list = new ArrayList();
      String sql = "select * from k_dic where ctype = '部门规章类型' order by abs(property)";
      ps = this.conn.prepareStatement(sql);
      rs = ps.executeQuery();
      while (rs.next()) {
        Map map = new HashMap();
        map.put("title", rs.getString("value"));
        map.put("id", "mytab" + rs.getString("property"));
        map.put("ctype", rs.getString("value"));
        map.put("listeners", "{activate:function(){document.getElementById('placardListDiv').style.display='block';document.getElementById('newsListDiv').style.display='none'}}");
        list.add(map);
      }
      List localList1 = list;
      return localList1;
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    } finally {
      DbUtil.close(rs);
      DbUtil.close(ps);
    }
  }

  public List<Map<String, String>> getEMail(String userId)
    throws Exception
  {
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      List emailList = new ArrayList();
      String sql = "select distinct a.uuid,title,senddate \tfrom oa_email a,oa_emailuser b \twhere 1=1 \tand b.isread = '否' \tand b.userid ='" + 
        userId + "' " + 
        "\tand a.uuid = b.uuid " + 
        "\torder by senddate desc limit 10";
      ps = this.conn.prepareStatement(sql);
      rs = ps.executeQuery();
      while (rs.next()) {
        Map map = new HashMap();
        map.put("uuid", rs.getString("uuid"));
        map.put("title", rs.getString("title"));
        map.put("senddate", rs.getString("senddate"));
        emailList.add(map);
      }

      List localList1 = emailList;
      return localList1;
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    } finally {
      DbUtil.close(rs);
      DbUtil.close(ps);
    }
  }

  public List<Map<String, String>> getProclamation(String userId)
    throws Exception
  {
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      List emailList = new ArrayList();
      String sql = " SELECT a.`uuid`,a.`title`,c.departname AS departmentId,b.name AS `userId`, \n a.publishDate,CONCAT(SUBSTR(REPLACE(REPLACE(a.`content`,'\n',''),'\r',''),1,30),'...') as content, \na.`readUserId`,a.`property`,a.status \n FROM `k_proclamation` a  \n LEFT JOIN k_user b ON a.userId=b.Id \n LEFT JOIN k_department c ON a.departmentId = c.autoId \n WHERE 1=1  and concat(',',a.readuserid,',') like '%," + 
        userId + ",%' " + 
        " and status = '已审批' ";

      ps = this.conn.prepareStatement(sql);
      rs = ps.executeQuery();
      while (rs.next()) {
        Map map = new HashMap();
        map.put("uuid", rs.getString("uuid"));
        map.put("title", rs.getString("title"));
        map.put("publishdate", rs.getString("publishdate"));
        emailList.add(map);
      }

      List localList1 = emailList;
      return localList1;
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    } finally {
      DbUtil.close(rs);
      DbUtil.close(ps);
    }
  }

  public List<Question> getQuestion(String sqlWhere, String orderby)
    throws Exception
  {
    PreparedStatement ps = null;
    ResultSet rs = null;

    List questionList = new ArrayList();
    try
    {
      String sql = " SELECT a.id,a.GreateDate,TITLE,Typename,a.createUserId \n FROM asdb.p_Question a,asdb.p_Questiontype b \n WHERE a.QuestionType=b.id \n" + 
        sqlWhere + 
        orderby;

      ps = this.conn.prepareStatement(sql);
      rs = ps.executeQuery();
      while (rs.next()) {
        Question question = new Question();
        question.setId(rs.getString(1));
        question.setCreateDate(rs.getString(2));
        question.setTitle(rs.getString(3));
        question.setTypeId(rs.getString(4));
        question.setUserId(rs.getString(5));

        questionList.add(question);
      }

      List localList1 = questionList;
      return localList1;
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    } finally {
      DbUtil.close(rs);
      DbUtil.close(ps);
    }
  }

  public List<Map<String, String>> getSchedule(String loginId, String startDate, String endDate)
    throws Exception
  {
    PreparedStatement ps = null;
    ResultSet rs = null;
    ASFuntion CHF = new ASFuntion();
    List scheduleList = new ArrayList();
    try
    {
      String sql = " SELECT workdate,worktype,memo \n FROM ( \n \tSELECT workdate,worktype,CONCAT(worktype,' ',projectname) AS memo \n \tFROM oa_timesreport a \n \tLEFT JOIN z_project b ON a.projectid=b.projectid \n \tWHERE userid='" + 
        loginId + "' AND workdate>='" + startDate + "' \n" + 
        " \tAND workdate<=CURDATE() \n" + 
        " \tUNION \n" + 
        " \tSELECT workdate,'项目工作 ',CONCAT('项目工作 ',projectname) AS memo \n" + 
        " \tFROM oa_timesschedular a \n" + 
        " \tLEFT JOIN z_project b ON a.projectid=b.projectid \n" + 
        " \tWHERE userid='" + loginId + "' AND workdate>CURDATE() \n" + 
        " \tAND workdate<='" + endDate + "' \n" + 
        " \tGROUP BY workdate \n" + 
        " )t ORDER BY workdate \n";

      ps = this.conn.prepareStatement(sql);
      rs = ps.executeQuery();
      while (rs.next()) {
        Map map = new HashMap();
        map.put("workdate", CHF.showNull(rs.getString(1)));
        map.put("worktype", CHF.showNull(rs.getString(2)));
        map.put("task", CHF.showNull(rs.getString(3)));

        scheduleList.add(map);
      }

      List localList1 = scheduleList;
      return localList1;
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    } finally {
      DbUtil.close(rs);
      DbUtil.close(ps);
    }
  }

  public List<Map<String, String>> getRank()
    throws Exception
  {
    PreparedStatement ps = null;
    ResultSet rs = null;
    ASFuntion CHF = new ASFuntion();
    List rankList = new ArrayList();
    try
    {
      String sql = " SELECT answerCount,userName FROM ( \n \tSELECT COUNT(*) AS answerCount,b.name AS userName \n  \tFROM p_answer a \n  \tLEFT JOIN k_user b ON a.userid = b.id  \n  \tGROUP BY a.userid  \n ) a \n ORDER BY answerCount DESC  \n LIMIT 5 \n";

      ps = this.conn.prepareStatement(sql);
      rs = ps.executeQuery();
      while (rs.next()) {
        Map map = new HashMap();
        map.put("count", CHF.showNull(rs.getString(1)));
        map.put("userName", CHF.showNull(rs.getString(2)));

        rankList.add(map);
      }

      List localList1 = rankList;
      return localList1;
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    } finally {
      DbUtil.close(rs);
      DbUtil.close(ps);
    }
  }

  public List<TimesReport> getTimesReport(String loginId, String workDate)
    throws Exception
  {
    PreparedStatement ps = null;
    ResultSet rs = null;
    ASFuntion CHF = new ASFuntion();
    List timesReportList = new ArrayList();
    try
    {
      String sql = " SELECT projectId,userId,departmentId,workDate,WEEK,workType,remark,sTime,eTime,\n totalTime,updateUser,updateTime \n FROM oa_timesreport  \n WHERE loginid = ? AND workdate = ? \n";

      ps = this.conn.prepareStatement(sql);
      rs = ps.executeQuery();
      while (rs.next())
      {
        TimesReport trt = new TimesReport();
        trt.setProjectId(CHF.showNull(rs.getString(1)));
        trt.setUserId(CHF.showNull(rs.getString(2)));
        trt.setDepartmentId(CHF.showNull(rs.getString(3)));
        trt.setWorkDate(CHF.showNull(rs.getString(4)));
        trt.setWEEK(CHF.showNull(rs.getString(5)));
        trt.setWorkType(CHF.showNull(rs.getString(6)));
        trt.setRemark(CHF.showNull(rs.getString(7)));
        trt.setsTime(CHF.showNull(rs.getString(8)));
        trt.seteTime(CHF.showNull(rs.getString(9)));
        trt.setTotalTime(CHF.showNull(rs.getString(10)));
        trt.setUpdateUser(CHF.showNull(rs.getString(11)));
        trt.setUpdateTime(CHF.showNull(rs.getString(12)));

        timesReportList.add(trt);
      }

      List localList1 = timesReportList;
      return localList1;
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    } finally {
      DbUtil.close(rs);
      DbUtil.close(ps);
    }
  }

  private List<Map<String, String>> loadPccpaTitleHint(UserSession userSession) {
    String url = "http://www.pccpa.com.cn/Panchina_TX/remind.action";
    Map params = new HashMap();
    params.put("userName", userSession.getUserName());
    String context = WebUtil.DoPost(url, params, new HashMap());

    JSONArray jsonArray = new JSONArray();
    List list = new ArrayList();
    try {
      jsonArray = JSONArray.fromObject(context);

      for (int i = 0; i < jsonArray.size(); i++)
      {
        JSONObject jsonObject = jsonArray.getJSONObject(i);
        Map titleHint = new HashMap();
        titleHint.put("hinttxt", jsonObject.getString("remindStr"));
        titleHint.put("actname", jsonObject.getString("titleStr"));
        titleHint.put("acturl", jsonObject.getString("jumptoUrl"));
        list.add(titleHint);
      }
    } catch (Exception ex) {
      Map titleHint = new HashMap();
      titleHint.put("hinttxt", "http://www.pccpa.com.cn 无法访问");
      titleHint.put("actname", "http://www.pccpa.com.cn 无法访问");
      titleHint.put("acturl", "javascript:void()");
      //list.add(titleHint);
    }
    return list;
  }
}