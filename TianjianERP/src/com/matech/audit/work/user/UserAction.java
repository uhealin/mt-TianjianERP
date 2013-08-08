package com.matech.audit.work.user;

import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.department.DepartmentService;
import com.matech.audit.service.department.model.DepartmentVO;
import com.matech.audit.service.fileupload.MyFileUpload;
import com.matech.audit.service.form.model.QueryVO;
import com.matech.audit.service.kdic.model.KDicVO;
import com.matech.audit.service.manuscript.ManuFileService;
import com.matech.audit.service.popedom.PopedomService;
import com.matech.audit.service.role.RoleService;
import com.matech.audit.service.setdef.SetdefObjectService;
import com.matech.audit.service.user.Foder;
import com.matech.audit.service.user.UserService;
import com.matech.audit.service.user.model.IndependenceVO;
import com.matech.audit.service.user.model.User;
import com.matech.audit.service.user.model.UserDetailsTree;
import com.matech.audit.service.user.model.UserFavVO;
import com.matech.audit.service.user.model.UserVO;
import com.matech.audit.service.userdef.Userdef;
import com.matech.audit.service.userdef.UserdefService;
import com.matech.audit.service.userpopedom.UserPopedomService;
import com.matech.audit.work.oa.interiorEmail.InteriorEmailAction;
import com.matech.framework.listener.UserSession;
import com.matech.framework.multidb.MultiDbIF;
import com.matech.framework.pub.autocode.DELUnid;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.log.Log;
import com.matech.framework.pub.single.Single;
import com.matech.framework.pub.sys.UTILSysProperty;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.MD5;
import com.matech.framework.pub.util.StringUtil;
import com.matech.framework.pub.util.WebUtil;
import com.matech.framework.service.excelupload.ExcelUploadService;
import com.matech.framework.service.print.PrintSetup;
import java.io.File;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.del.JRockey2Opp;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

public class UserAction extends MultiActionController
{
  private Log log = new Log(UserAction.class);

  private final String _strSuccess = "/user/List.jsp";
  private final String _strSuccess3 = "/user/userManagerList.jsp";
  private final String _strSuccess4 = "/user/userManagerList2.jsp";
  private final String _strSuccess2 = "/user/ExamineList.jsp";
  private final String _strInt = "/user/AddandEdit.jsp";
  private final String _strInt2 = "/user/AddandEdit2.jsp";
  private final String _strItem = "/user/ItemList.jsp";
  private final String _strTask = "/user/ItemTask.jsp";
  private final String _strProject = "/user/ProjectTree.jsp";
  private final String _strUser = "/user/UserTree.jsp";
  private final String _strMessagerList = "/user/userMessagerList.jsp";
  private final String _strError = "../hasNoRight.jsp";
  private final String _strSms = "user/smsList.jsp";

  private final String SET_ORDER_BY_VIEW = "user/setOrderBy.jsp";
  private final String _changePassword = "/user/changePassword.jsp";

  private final String address = "user/address.jsp";
  private final String addressAdd = "user/addressAdd.jsp";

  private static String userEditDepartmentList = "user/userEditDepartmentList.jsp";

  private static String mobileMessageList = "user/mobileMessageList.jsp";

  private static String roleUserList = "user/roleUserList.jsp";

  private static String userDetailsTreeList = "user/userDetailsTreeList.jsp";

  private static String updateUserDetailTree = "user/userDetailsTree.jsp";

  public ModelAndView address(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    ModelAndView modelAndView = new ModelAndView("user/address.jsp");
    Connection conn = new DBConnect().getConnect("");
    DataGridProperty pp = new DataGridProperty();

    UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");

    String myId = userSession.getUserId();
    UserService us = new UserService(conn);
    String property = us.getPropertyById(myId);
    DbUtil.close(conn);
    String tableid = "tt_" + DELUnid.getNumUnid();
    String sql = "select distinct b.departname,a.*,case when " + property + ">=max(r.property) then CONVERT(a.mobilephone using utf8) else '无权查看' end as Jurisdiction  " + 
      "\tfrom k_user a " + 
      "\tleft join k_department b on a.departmentid = b.autoid " + 
      "\tleft join k_department c on b.fullpath like concat(c.fullpath,'%') " + 
      " left join k_userrole ur on ur.userid=a.id " + 
      " left join k_role r on r.id=ur.rid " + 
      "\twhere a.state = 0 and a.id <>'19' " + 
      "\t${parentid} ${areaid} ${departmentid} ${floor} ${house} ${name}${phone} " + 
      " group by a.id ";

    System.out.println(sql);
    pp.addColumn("所属部门", "departname");
    pp.addColumn("工位号", "station");
    pp.addColumn("姓名", "name");
    pp.addColumn("手机", "Jurisdiction");
    pp.addColumn("办公电话", "phone");
    pp.addColumn("EMail", "email");
    pp.addColumn("楼层", "floor");
    pp.addColumn("房间号", "house");

    pp.addSqlWhere("parentid", " and ifnull(c.parentid,'') = '${parentid}' ");
    pp.addSqlWhere("areaid", " and ifnull(c.areaid,'') = '${areaid}' ");
    pp.addSqlWhere("departmentid", " and ifnull(c.autoid,'') = '${departmentid}' ");
    pp.addSqlWhere("phone", " and a.phone like '%${phone}%' ");

    pp.addSqlWhere("floor", " and a.floor like '%${floor}%' ");
    pp.addSqlWhere("house", " and a.house like '%${house}%' ");
    pp.addSqlWhere("name", " and a.name like '%${name}%' ");

    pp.setOrderBy_CH("a.departmentid,a.istips,a.name ");
    pp.setTableID(tableid);
    pp.setCustomerId("");

    pp.setPageSize_CH(50);
    pp.setWhichFieldIsValue(1);

    pp.setPrintEnable(true);
    pp.setPrintTitle("通讯录");

    pp.setSQL(sql);

    request.getSession().setAttribute("DGProperty_" + pp.getTableID(), pp);

    modelAndView.addObject("tableid", pp.getTableID());
    return modelAndView;
  }

  public ModelAndView addressAdd(HttpServletRequest request, HttpServletResponse response) throws Exception {
    ModelAndView modelAndView = new ModelAndView("user/addressAdd.jsp");
    Connection conn = null;
    try {
      ASFuntion CHF = new ASFuntion();

      conn = new DBConnect().getConnect("");

      String flag = CHF.showNull(request.getParameter("flag"));

      String loginid = CHF.showNull(request.getParameter("loginid"));
      UserService userService = new UserService(conn);
      User user = userService.getUser(loginid, "loginid");

      modelAndView.addObject("user", user);
      modelAndView.addObject("flag", flag);
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    } finally {
      DbUtil.close(conn);
    }

    return modelAndView;
  }

  public void addressSave(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection conn = null;
    try {
      ASFuntion CHF = new ASFuntion();

      conn = new DBConnect().getConnect("");

      String flag = CHF.showNull(request.getParameter("flag"));

      String loginid = CHF.showNull(request.getParameter("loginid"));

      UserService userService = new UserService(conn);
      User user = userService.getUser(loginid, "loginid");

      user.setMobilePhone(CHF.showNull(request.getParameter("mobilePhone")));
      user.setPhone(CHF.showNull(request.getParameter("phone")));
      user.setFloor(CHF.showNull(request.getParameter("floor")));
      user.setHouse(CHF.showNull(request.getParameter("house")));

      user.setStation(CHF.showNull(request.getParameter("station")));
      user.setPassword(CHF.showNull(request.getParameter("password")));
      user.setPaperstype(CHF.showNull(request.getParameter("paperstype")));
      user.setPapersnumber(CHF.showNull(request.getParameter("papersnumber")));
      user.setIdentityCard(CHF.showNull(request.getParameter("identityCard")));
      user.setSex(CHF.showNull(request.getParameter("sex")));

      user.setBorndate(CHF.showNull(request.getParameter("borndate")));
      user.setNation(CHF.showNull(request.getParameter("nation")));
      user.setMarriage(CHF.showNull(request.getParameter("marriage")));
      user.setPlace(CHF.showNull(request.getParameter("place")));
      user.setResidence(CHF.showNull(request.getParameter("residence")));

      user.setPolitics(CHF.showNull(request.getParameter("politics")));
      user.setPartytime(CHF.showNull(request.getParameter("partytime")));
      user.setRelationships(CHF.showNull(request.getParameter("relationships")));
      user.setDiploma(CHF.showNull(request.getParameter("diploma")));
      user.setDiplomatime(CHF.showNull(request.getParameter("diplomatime")));

      user.setEducational(CHF.showNull(request.getParameter("educational")));
      user.setProfession(CHF.showNull(request.getParameter("profession")));
      user.setEnglish(CHF.showNull(request.getParameter("english")));
      user.setCpano(CHF.showNull(request.getParameter("cpano")));

      userService.updateUser(user, loginid);

      if ("login".equals(flag))
        response.sendRedirect(request.getContextPath() + "/login.do");
      else
        response.sendRedirect(request.getContextPath() + "/user.do?method=addressAdd&loginid=" + loginid);
    }
    catch (Exception e) {
      e.printStackTrace();
      throw e;
    } finally {
      DbUtil.close(conn);
    }
  }

  public ModelAndView Save(HttpServletRequest request, HttpServletResponse response, User uservo)
    throws Exception
  {
    System.out.println("============User AddOrUpdateSave=================");

    Connection conn = null;
    String strResult = "";
    ASFuntion CHF = new ASFuntion();

    UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");

    String departmentidNList = request.getParameter("departmentidNList");
    String areaid = request.getParameter("areaid");
    String emtype = request.getParameter("emtypelist");
    HashMap mapResult = new HashMap();

    String id = request.getParameter("id");
    try {
      conn = new DBConnect().getConnect("");
      DbUtil dbUtil = new DbUtil(conn);

      WebUtil webUtil = new WebUtil(request, response);

      User uservoOld = (User)dbUtil.load(User.class, uservo.getId());
      if (StringUtil.isBlank(uservo.getLoginid())) {
        uservo.setLoginid(uservoOld.getLoginid());
      }
      if (StringUtil.isBlank(uservo.getDepartid())) {
        if (StringUtil.isBlank(uservoOld.getDepartid()))
          uservo.setDepartid(areaid);
        else {
          uservo.setDepartid(uservoOld.getDepartid());
        }
      }
      if (StringUtil.isBlank(uservo.getDepartmentid())) {
        if (StringUtil.isBlank(uservoOld.getDepartmentid()))
          uservo.setDepartmentid(departmentidNList);
        else {
          uservo.setDepartmentid(uservoOld.getDepartmentid());
        }
      }
      if (StringUtil.isBlank(uservo.getIdentityCard())) {
        uservo.setIdentityCard(uservoOld.getIdentityCard());
      }
      String[] userDefName = request.getParameterValues("UserDefName");
      String[] userDefValue = request.getParameterValues("UserDefValue");
      uservo.setIdentityCard(CHF.showNull(request.getParameter("identityCard")));

      int length = userDefName == null ? 0 : userDefName.length;

      Userdef[] userdefs = new Userdef[length];
      Userdef userdef = null;
      for (int i = 0; i < userdefs.length; i++) {
        userdef = new Userdef();
        userdef.setName(userDefName[i]);
        userdef.setValue(userDefValue[i]);
        userdefs[i] = userdef;
      }

      uservo.setUserdefs(userdefs);
      uservo.setEmtype(request.getParameter("emtype"));
      UserService userService = new UserService(conn);

      String UserOpt = request.getParameter("UserOpt");
      if ("1".equals(UserOpt))
      {
        String FileRondomName = CHF.showNull(request.getParameter("fileRondomName"));
        String uploadFileName = getFileName(CHF.showNull(request.getParameter("uploadFileName")));
        String emptype = CHF.showNull(request.getParameter("emtype1"));
        System.out.println("--------------emptype1---------" + emptype);
        if ((!"".equals(uploadFileName)) && (!"".equals(FileRondomName)))
        {
          uservo.setUserPhoto(uploadFileName);
          uservo.setUserPhotoTemp(FileRondomName);
        } else {
          uservo.setUserPhoto("");
          uservo.setUserPhotoTemp("");
        }

        userService.addUser(uservo);
        try
        {
          User usernew = (User)dbUtil.load(User.class, "identitycard", uservo.getIdentityCard());
          String rolevalue = CHF.showNull(request.getParameter("roles"));
          if ((!StringUtil.isBlank(usernew.getId())) && (!StringUtil.isBlank(rolevalue)))
            dbUtil.execute("insert into k_userrole(userid,rid) values(?,?)", new Object[] { usernew.getId(), rolevalue });
        } catch (Exception e) {
          e.printStackTrace();
        }
        String commondefNames = CHF.showNull(request.getParameter("commondefNames"));
        String commondefValues = CHF.showNull(request.getParameter("commondefValues"));

        if ((!commondefNames.equals("")) && (!commondefValues.equals("")))
        {
          System.out.println(commondefNames.substring(0, commondefNames.length() - 1));
          System.out.println(commondefValues.substring(0, commondefValues.length() - 1));

          String[] commondefnames = commondefNames.substring(0, commondefNames.length() - 1).split("-");
          String[] commondefvalues = commondefValues.substring(0, commondefValues.length() - 1).split("-");

          Userdef[] commonsetdef = new Userdef[commondefnames.length];

          for (int i = 0; i < commondefnames.length; i++) {
            if (commondefvalues[i].equals("NaN")) {
              commondefvalues[i] = "";
            }
            String name = commondefnames[i];
            String value = commondefvalues[i];

            commonsetdef[i] = new Userdef();
            commonsetdef[i].setName(name);
            commonsetdef[i].setValue(value);
            commonsetdef[i].setProperty("com_user");
          }

          try
          {
            PreparedStatement ps = null;
            ResultSet rs = null;
            String sql = "select id from k_User where loginid = '" + uservo.getLoginid() + "'";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
              String contrastid = rs.getString(1);
              UserdefService udm = new UserdefService(conn);
              udm.addOrupdateUserdef(commonsetdef, contrastid, "com_user");
            }

            rs.close();
            ps.close();
          }
          catch (Exception e)
          {
            com.matech.framework.pub.util.Debug.print(4, "用户信息保存失败！", e);
          }

        }

        this.log.log("新增用户[" + uservo.getLoginid() + ":" + uservo.getName() + "]");

        strResult = "/user/List.jsp";
        if (!StringUtil.isBlank(departmentidNList))
          response.sendRedirect("user.do?method=userListByEmtype&departmentidNList=" + departmentidNList + "&areaid=" + areaid + "&emtype=" + emtype);
        else
          response.sendRedirect("user.do?method=List");
        return null;
      }
      if ("2".equals(UserOpt)) {
        System.out.println("hzh:id=" + uservo.getId());

        String emtypeMust = request.getParameter("emtype");

        String FileRondomName = CHF.showNull(request.getParameter("fileRondomName"));
        String uploadFileName = getFileName(CHF.showNull(request.getParameter("uploadFileName")));
        if (!"".equals(uploadFileName)) "".equals(FileRondomName);

        if (!"".equals(uploadFileName)) {
          uservo.setUserPhoto(uploadFileName);
          uservo.setUserPhotoTemp(FileRondomName);
        } else {
          User userTemp = userService.getUser(uservo.getId(), "id");
          uservo.setUserPhoto(userTemp.getUserPhoto());
          uservo.setUserPhotoTemp(uservo.getUserPhotoTemp());
        }

        String commondefNames = CHF.showNull(request.getParameter("commondefNames"));
        String commondefValues = CHF.showNull(request.getParameter("commondefValues"));

        if ((!commondefNames.equals("")) && (!commondefValues.equals("")))
        {
          String[] commondefnames = commondefNames.substring(0, commondefNames.length() - 1).split("-");
          String[] commondefvalues = commondefValues.substring(0, commondefValues.length() - 1).split("-");

          Userdef[] commonsetdef = new Userdef[commondefnames.length];

          for (int i = 0; i < commondefnames.length; i++) {
            if (commondefvalues[i].equals("NaN")) {
              commondefvalues[i] = "";
            }
            String name = commondefnames[i];
            String value = commondefvalues[i];

            commonsetdef[i] = new Userdef();
            commonsetdef[i].setName(name);
            commonsetdef[i].setValue(value);
            commonsetdef[i].setProperty("com_user");
          }
          try
          {
            String contrastid = uservo.getId();
            PreparedStatement ps = null;

            String sql = "delete from k_userdef where contrastid='" + contrastid + "' and property='com_user'";
            ps = conn.prepareStatement(sql);

            UserdefService udm = new UserdefService(conn);
            udm.addOrupdateUserdef(commonsetdef, contrastid, "com_user");

            ps.close();
          }
          catch (Exception e)
          {
            com.matech.framework.pub.util.Debug.print(4, "用户信息保存失败！", e);
          }

        }

        String department = "";
        if (!"".equals(uservo.getDepartmentid())) {
          department = new DepartmentService(conn).getVo(Integer.parseInt(uservo.getDepartmentid())).getDepartmentName();
        }

        String roles = CHF.showNull(uservo.getRoles());
        if ("".equals(roles)) roles = "''";
        String sql = "SELECT CONCAT('权限增加了【',GROUP_CONCAT(DISTINCT a.rolename),'】') FROM k_role a \tLEFT JOIN k_userrole b ON b.userid = ? \tAND a.id = b.rid \tWHERE a.id IN (" + 
          roles + ") " + 
          "\tAND b.userid IS NULL";

        String[] params = { uservo.getId() };
        String result = CHF.showNull(dbUtil.queryForString(sql, params));
        if (!"".equals(result)) result = department + "【" + uservo.getName() + "】" + result;

        sql = "SELECT CONCAT('权限删除了【',GROUP_CONCAT(DISTINCT a.rolename),'】') \tFROM k_role a,(\t\tSELECT b.* FROM k_role a \t\tRIGHT JOIN k_userrole b ON a.id IN (" + 
          roles + ") AND a.id = b.rid " + 
          "\t\tWHERE b.userid = ?\t" + 
          "\t\tAND a.id IS NULL" + 
          "\t) b WHERE a.id = b.rid";
        String result1 = CHF.showNull(dbUtil.queryForString(sql, params));
        if (!"".equals(result)) {
          if (!"".equals(result1)) result = result + "，" + result1;
        }
        else if (!"".equals(result1)) result = department + "【" + uservo.getName() + "】" + result1;

        if (!"".equals(result)) {
          this.log.log("修改用户权限：" + result);
        }
        String userid = request.getParameter("userid");
        uservo.setId(userid);

        userService.updateUser(uservo);
        strResult = "/user/List.jsp";

        if ("myControl".equals(request.getParameter("myControl")))
        {
          response.sendRedirect("user.do?method=Edit&UserOpt=2&id=" + userid + "&myControl=myControl&view=" + CHF.showNull(request.getParameter("view")));
        }
        else if (!StringUtil.isBlank(departmentidNList))
          response.sendRedirect("user.do?method=userListByEmtype&departmentidNList=" + departmentidNList + "&areaid=" + areaid + "&emtype=" + emtype);
        else
          response.sendRedirect("user.do?method=List");
        return null;
      }

      if ("3".equals(UserOpt))
      {
        String condition = null;
        String getSql = "select * from k_userdef where ContrastID = ? and Property = ?";
        PreparedStatement pss = conn.prepareStatement(getSql);

        String userId = userSession.getUserId();

        pss.setInt(1, Integer.parseInt(userId));
        pss.setString(2, "workpath");

        ResultSet rs = pss.executeQuery();
        if (rs.next())
          condition = "update";
        else {
          condition = "add";
        }

        String pd = request.getParameter("pd");
        System.out.println("pd=" + pd);

        String pathName = "E审通工作目录";
        String pathValue = "E审通工作目录";

        if (pd.equalsIgnoreCase("E")) {
          userService.addOrUpdate(Integer.parseInt(userId), pathName, pathValue, condition);
        } else if (pd.equalsIgnoreCase("selfDefine")) {
          pathName = "自定义工作目录";
          pathValue = request.getParameter("workingPath");
          userService.addOrUpdate(Integer.parseInt(userId), pathName, pathValue, condition);
        }

        String userCanSaveAsFile = (String)request.getSession().getAttribute("_UserCanSaveAsFile");

        String workPath = "E审通工作目录";

        if ("自定义工作目录".equals(pathName)) {
          workPath = pathValue.replaceAll("\\\\", "\\\\\\\\");
        }

        if ("FALSE".equals(userCanSaveAsFile)) {
          workPath = "无权";
        }

        System.out.println("workPath:" + workPath);

        userSession.setUserWorkPath(workPath);

        String FileRondomName = CHF.showNull(request.getParameter("fileRondomName"));
        String uploadFileName = getFileName(CHF.showNull(request.getParameter("uploadFileName")));
        if (!"".equals(uploadFileName)) "".equals(FileRondomName);

        if (!"".equals(uploadFileName)) {
          uservo.setUserPhoto(uploadFileName);
          uservo.setUserPhotoTemp(FileRondomName);
        } else {
          User userTemp = userService.getUser(uservo.getId(), "id");
          uservo.setUserPhoto(userTemp.getUserPhoto());
          uservo.setUserPhotoTemp(uservo.getUserPhotoTemp());
        }

        userService.updateUser(uservo);

        mapResult.put("uservo", uservo);
        Userdef[] userdefVo = (Userdef[])null;
        if ((uservo != null) && (uservo.getUserdefs() != null)) {
          userdefVo = uservo.getUserdefs();
          mapResult.put("UserDefTbodyCount", new Integer(userdefVo.length));
        }
        mapResult.put("userdef", userdefVo);
        mapResult.put("UserOpt", UserOpt);

        String commondefNames = CHF.showNull(request.getParameter("commondefNames"));
        String commondefValues = CHF.showNull(request.getParameter("commondefValues"));

        if ((!commondefNames.equals("")) && (!commondefValues.equals("")))
        {
          System.out.println(commondefNames.substring(0, commondefNames.length() - 1));
          System.out.println(commondefValues.substring(0, commondefValues.length() - 1));

          String[] commondefnames = commondefNames.substring(0, commondefNames.length() - 1).split("-");
          String[] commondefvalues = commondefValues.substring(0, commondefValues.length() - 1).split("-");

          Userdef[] commonsetdef = new Userdef[commondefnames.length];

          for (int i = 0; i < commondefnames.length; i++) {
            if (commondefvalues[i].equals("NaN")) {
              commondefvalues[i] = "";
            }
            String name = commondefnames[i];
            String value = commondefvalues[i];

            commonsetdef[i] = new Userdef();
            commonsetdef[i].setName(name);
            commonsetdef[i].setValue(value);
            commonsetdef[i].setProperty("com_user");
          }
          try
          {
            String contrastid = uservo.getId();
            PreparedStatement ps = null;

            String sql = "delete from k_userdef where contrastid='" + contrastid + "' and property='com_user'";
            ps = conn.prepareStatement(sql);

            UserdefService udm = new UserdefService(conn);
            udm.addOrupdateUserdef(commonsetdef, contrastid, "com_user");

            ps.close();
          }
          catch (Exception e) {
            com.matech.framework.pub.util.Debug.print(4, "用户信息保存失败！", e);
          }

        }

        String close = CHF.showNull(request.getParameter("close"));
        response.sendRedirect("/AuditSystem/user.do?method=Edit&UserOpt=3&close=" + close);
        return null;
      }
      strResult = "../hasNoRight.jsp";
    }
    catch (Exception e)
    {
      com.matech.framework.pub.util.Debug.print(4, "用户信息保存失败！", e);
      strResult = "../hasNoRight.jsp";
      throw e;
    }
    finally {
      DbUtil.close(conn); } DbUtil.close(conn);

    return new ModelAndView(strResult, mapResult);
  }

  public ModelAndView Edit(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    System.out.println("============User AddOrUpdateSelect=================");
    Connection conn = null;
    String strResult = "";
    String userPhotoSrc = "";
    HashMap mapResult = new HashMap();
    ASFuntion CHF = new ASFuntion();
    DbUtil dbUtil = null;
    try {
      String temp = UTILSysProperty.SysProperty.getProperty("clientDog");
      UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
      String loginDisabled = UTILSysProperty.SysProperty.getProperty("禁止用户自己修改登录名");
      if ((loginDisabled == null) || ("".equals(loginDisabled)) || ("否".equals(loginDisabled))) {
        loginDisabled = "否";
      }

      conn = new DBConnect().getConnect("");
      dbUtil = new DbUtil(conn);
      SetdefObjectService setdefObjectService = new SetdefObjectService(conn);

      List setValueList = setdefObjectService.getSetValueList("user");
      request.setAttribute("setValueList", setValueList);

      String UserOpt = request.getParameter("UserOpt");
      User uservo = null;
      Userdef[] userdefVo = (Userdef[])null;
      UserService userService = new UserService(conn);
      String fileTempName = DELUnid.getNumUnid();
      String passWord = "not";
      String act = CHF.showNull(request.getParameter("act"));

      boolean b = new RoleService(conn).hasRole(userSession.getUserId(), "系统登录名管理");
      boolean berp = new RoleService(conn).hasRole(userSession.getUserId(), "ERP人员管理员");
      mapResult.put("bhasRole", b ? "1" : "0");
      mapResult.put("berprole", berp ? "1" : "0");
      if ("1".equals(UserOpt)) {
        passWord = "can";
      }

      if ("2".equals(UserOpt)) {
        String id = request.getParameter("id");
        if (!act.equals("no")) {
          passWord = "can";
        }

        if ((id != null) && (!"".equals(id))) {
          uservo = userService.getUser(id, "id");

          userPhotoSrc = uservo.getUserPhoto();

          if ((userPhotoSrc == null) || ("".equals(userPhotoSrc))) {
            userPhotoSrc = "/images/noPhoto.gif";
          } else {
            if (userPhotoSrc.indexOf(".") > -1) {
              fileTempName = userPhotoSrc.substring(0, userPhotoSrc.indexOf("."));
            }
            userPhotoSrc = "/userPhoto/" + userPhotoSrc;
          }

          setValueList = setdefObjectService.getSetValueList("user", id, "com_user");
          request.setAttribute("setValueList", setValueList);
          mapResult.put("fn", uservo.getUserPhotoTemp());
        }
      }
      else if ("3".equals(UserOpt)) {
        String loginid = userSession.getUserLoginId();
        String id = request.getParameter("id");
        if ((id == null) || ("".equals(id))) {
          id = userSession.getUserId();
        }
        uservo = userService.getUser(id, "id");

        userPhotoSrc = uservo.getUserPhoto();
        if ((userPhotoSrc == null) || ("".equals(userPhotoSrc)))
        {
          userPhotoSrc = "/images/noPhoto.gif";
        } else {
          fileTempName = userPhotoSrc;
          userPhotoSrc = "/userPhoto/" + userPhotoSrc;
        }

        setValueList = setdefObjectService.getSetValueList("user", id, "com_user");
        request.setAttribute("setValueList", setValueList);
        request.setAttribute("loginid", loginid);
        mapResult.put("loginDisabled", loginDisabled);
        mapResult.put("fn", uservo.getUserPhotoTemp());
      }

      mapResult.put("uservo", uservo);

      if ((uservo != null) && (uservo.getUserdefs() != null)) {
        userdefVo = uservo.getUserdefs();
        mapResult.put("UserDefTbodyCount", new Integer(userdefVo.length));
      }

      String userId = userSession.getUserId();
      String getSql = "select * from k_userdef where ContrastID = ? and Property = ?";
      PreparedStatement ps = conn.prepareStatement(getSql);
      ps.setString(1, userId);
      ps.setString(2, "workpath");
      ResultSet rs = ps.executeQuery();
      if ((rs.next()) && (rs != null)) {
        mapResult.put("workName", rs.getString(3));
        if (rs.getString(4) == null) {
          mapResult.put("workValue", "");
        } else {
          System.out.println(rs.getString(4));
          mapResult.put("workValue", rs.getString(4).replace("\\", "/"));
        }
      } else {
        mapResult.put("workName", "");
        mapResult.put("workValue", "");
      }

      boolean bcheckUserRole = checkUserRole(userSession.getUserId(), "系统登录名管理");
      if (bcheckUserRole)
        mapResult.put("hasRole", "true");
      else {
        mapResult.put("hasRole", "false");
      }

      String close = request.getParameter("close");
      String departmentidNList = CHF.showNull(request.getParameter("departmentidNList"));
      String areaid = CHF.showNull(request.getParameter("areaid"));
      String emtype = CHF.showNull(request.getParameter("emtype"));
      mapResult.put("close", close);
      mapResult.put("departmentidNList", departmentidNList);
      mapResult.put("areaid", areaid);
      mapResult.put("emtype", emtype);

      mapResult.put("userdef", userdefVo);
      mapResult.put("UserOpt", UserOpt);

      mapResult.put("temp", temp);
      mapResult.put("fileTempName", fileTempName);
      mapResult.put("userPhotoSrc", userPhotoSrc);
      String svalue = CHF.showNull(UTILSysProperty.SysProperty.getProperty("系统应用事务所"));
      mapResult.put("svalue", svalue);
      if ((UserOpt.equals("3")) && (close.equals("1")))
        strResult = "/user/AddandEdit2.jsp";
      else {
        strResult = "/user/AddandEdit.jsp";
      }
      request.setAttribute("passWord", passWord);
      request.setAttribute("act", act);
    } catch (Exception e) {
      com.matech.framework.pub.util.Debug.print(4, "用户信息读取失败！", e);
      strResult = "../hasNoRight.jsp";
      throw e;
    } finally {
      DbUtil.close(conn);
    }
    return new ModelAndView(strResult, mapResult);
  }

  public ModelAndView ReadDog(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    Connection conn = null;
    try {
      conn = new DBConnect().getConnect("");
      response.setContentType("text/html;charset=utf-8");

      ASFuntion asf = new ASFuntion();
      String AS_dog = asf.showNull(request.getParameter("AS_dog"));
      String opt = asf.showNull(request.getParameter("opt"));
      String usr = asf.showNull(request.getParameter("usr"));
      String dog = asf.showNull(request.getParameter("dog"));

      UserService us = new UserService(conn);

      PrintWriter out = response.getWriter();
      if ("1".equals(opt)) {
        us.updateDog(usr, dog);
      } else if ("2".equals(opt)) {
        if (us.getDog(dog))
          us.updateDog(usr, dog);
        else {
          out.print("5");
        }
      }
      else if (AS_dog.length() > 100) {
        String result = "0";
        Map map = JRockey2Opp.resolveInfo(AS_dog);

        JRockey2Opp.getDogState();
        Map dogInfo = JRockey2Opp.getInfoFromDog();

        String serverSysCo = (String)dogInfo.get("sysCo");

        String clientSysUi = (String)map.get("sysUi");
        String clientSysCo = (String)map.get("sysCo");

        boolean bool = true;

        if ("".equals(serverSysCo)) {
          bool = false;
          result = "1";
        } else if ("".equals(clientSysCo)) {
          bool = false;
          result = "2";
        } else if (!clientSysCo.equals(serverSysCo)) {
          bool = false;
          result = "3";
        } else {
          bool = new UserService(conn).getDog(clientSysUi);
        }
        if (bool) {
          result = clientSysUi;
        }

        out.print(result);
      }

      out.close();
    } catch (Exception e) {
      com.matech.framework.pub.util.Debug.print(4, "读取狗信息失败！", e);
      e.printStackTrace();
      throw e;
    } finally {
      DbUtil.close(conn);
    }

    return null;
  }

  public ModelAndView CheckUser(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    Connection conn = null;
    try {
      conn = new DBConnect().getConnect("");
      String id = request.getParameter("id");
      String loginid = request.getParameter("loginid");
      boolean temp = false;
      response.setContentType("text/html;charset=utf-8");
      PrintWriter out = response.getWriter();
      UserService us = new UserService(conn);

      if ((!"".equals(id)) && (id != null)) {
        temp = us.SelectUser(Integer.parseInt(id));
      }
      if ((!"".equals(loginid)) && (loginid != null)) {
        temp = us.SelectUser(loginid);
      }

      String goBack = request.getParameter("goBack");

      if ((goBack != null) && (!goBack.equals(""))) {
        String departAndroles = "";
        departAndroles = us.getUser(Integer.parseInt(id));

        if (!temp)
          out.print("no*" + departAndroles);
        else {
          out.print("yes*" + departAndroles);
        }

      }
      else if (!temp) {
        out.print("no");
      } else {
        out.print("yes");
      }

      out.close();
    } catch (Exception e) {
      com.matech.framework.pub.util.Debug.print(4, "读取用户信息失败！", e);
      e.printStackTrace();
      throw e;
    } finally {
      DbUtil.close(conn);
    }
    return null;
  }

  public ModelAndView CheckUserEmail(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    Connection conn = null;
    try {
      conn = new DBConnect().getConnect("");
      String id = request.getParameter("id");
      String email = request.getParameter("email");
      boolean temp = false;
      response.setContentType("text/html;charset=utf-8");
      PrintWriter out = response.getWriter();
      DbUtil dbUtil = new DbUtil(conn);
      List users = dbUtil.select(UserVO.class, "select * from {0} where email=?  ", new Object[] { email });

      if (users.size() > 0)
        out.print("no");
      else
        out.print("yes");
    }
    catch (Exception e)
    {
      com.matech.framework.pub.util.Debug.print(4, "读取用户信息失败！", e);
      e.printStackTrace();
      throw e;
    } finally {
      DbUtil.close(conn);
    }
    return null;
  }

  public ModelAndView Remove(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    System.out.println("============User Delete=================");
    Connection conn = null;
    PrintWriter out = null;
    try {
      UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");

      conn = new DBConnect().getConnect("");
      UserService us = new UserService(conn);
      String id = request.getParameter("id");
      String opt = request.getParameter("opt");
      response.setContentType("text/html;charset=utf-8");
      out = response.getWriter();
      if ((!"".equals(id)) && (id != null))
      {
        User uservo = us.getUser(id, "loginid");
        us.removeUser(id, opt);

        this.log.log("禁用用户[" + uservo.getLoginid() + ":" + uservo.getName() + "]");

        out.print("禁用成功！");
      } else {
        out.print("禁用失败！");
      }
    } catch (Exception e) {
      out.print("删除失败！");
      com.matech.framework.pub.util.Debug.print(4, "删除用户失败！", e);
      e.printStackTrace();
      throw e;
    } finally {
      out.close();
      DbUtil.close(conn);
    }
    return null;
  }

  public static void main(String[] args) {
    String ss = "1266,1272,1287";
    String[] str = ss.split(",");
    for (int i = 0; i < str.length; i++)
    {
      System.out.println(i);
    }
  }

  public ModelAndView Remove3(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    Connection conn = null;
    PrintWriter out = null;
    try {
      conn = new DBConnect().getConnect("");

      String id = request.getParameter("id");
      String[] str = id.split(",");
      UserService us = new UserService(conn);
      for (int i = 0; i < str.length; i++) {
        us.removeUser3(str[i], "0");
      }
      response.setContentType("text/html;charset=utf-8");
      out = response.getWriter();
      out.print("禁用成功！");
    } catch (Exception e) {
      out.print("禁用失败！");
      com.matech.framework.pub.util.Debug.print(4, "删除用户失败！", e);
      e.printStackTrace();
      throw e;
    } finally {
      out.close();
      DbUtil.close(conn);
    }
    return null;
  }

  public ModelAndView Remove4(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    Connection conn = null;
    PrintWriter out = null;
    try {
      conn = new DBConnect().getConnect("");

      String id = request.getParameter("id");
      String[] str = id.split(",");
      UserService us = new UserService(conn);
      for (int i = 0; i < str.length; i++) {
        us.removeUser4(str[i], "1");
      }
      response.setContentType("text/html;charset=utf-8");
      out = response.getWriter();
      out.print("删除成功！");
    } catch (Exception e) {
      out.print("删除失败！");
      com.matech.framework.pub.util.Debug.print(4, "删除用户失败！", e);
      e.printStackTrace();
      throw e;
    } finally {
      out.close();
      DbUtil.close(conn);
    }

    return null;
  }

  public ModelAndView Remove2(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    Connection conn = null;
    PrintWriter out = null;
    try {
      conn = new DBConnect().getConnect("");
      String id = request.getParameter("id");
      String opt = request.getParameter("opt");
      response.setContentType("text/html;charset=utf-8");
      out = response.getWriter();
      if ((!"".equals(id)) && (id != null)) {
        new UserService(conn).removeUser(id, opt);
        out.print("删除成功！");
      } else {
        out.print("删除失败！");
      }
    } catch (Exception e) {
      out.print("删除失败！");
      com.matech.framework.pub.util.Debug.print(4, "删除用户失败！", e);
      e.printStackTrace();
      throw e;
    } finally {
      out.close();
      DbUtil.close(conn);
    }
    return new ModelAndView("/AuditSystem/user.do?method=List2&examineOrnot=true");
  }

  public ModelAndView Revert(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    Connection conn = null;
    PrintWriter out = null;
    try {
      conn = new DBConnect().getConnect("");
      ASFuntion asf = new ASFuntion();
      String id = asf.showNull(request.getParameter("id"));
      String loginid = asf.showNull(request.getParameter("loginid"));
      String departmentid = asf.showNull(request.getParameter("departmentid"));
      String roles = asf.showNull(request.getParameter("roles"));

      System.out.println("hzh: id=" + id);

      response.setContentType("text/html;charset=utf-8");
      out = response.getWriter();

      User user = new User();
      user.setId(id);
      user.setLoginid(loginid);
      user.setDepartmentid(departmentid);

      UserService us = new UserService(conn);
      if (!"".equals(id)) {
        us.revertUser(user);
        us.updateRoles(id, roles);
        out.print("还原成功！");
      } else {
        out.print("还原失败！");
      }
    } catch (Exception e) {
      out.print("还原失败！");
      com.matech.framework.pub.util.Debug.print(4, "还原用户失败！", e);
      e.printStackTrace();
      throw e;
    } finally {
      out.close();
      DbUtil.close(conn);
    }
    return null;
  }

  public ModelAndView UpdatePWD(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    Connection conn = null;
    try
    {
      conn = new DBConnect().getConnect("");
      UserService userService = new UserService(conn);

      response.setContentType("text/html;charset=utf-8");
      boolean bool = false;

      String UserOpt = request.getParameter("UserOpt");

      PrintWriter out = response.getWriter();

      String id = request.getParameter("id");

      String password_old = request.getParameter("password_old");
      String password = request.getParameter("password");

      String password_now = userService.getUser(id, "id").getPassword();

      System.out.println("hzh: id=" + id);
      System.out.println("hzh: password_old=" + password_old);
      System.out.println("hzh: password=" + password);

      System.out.println(password_now + "|" + MD5.getMD5String(password_old));
      if ("3".equals(UserOpt)) {
        if (!password_now.equals(MD5.getMD5String(password_old))) {
          out.print("error");
          return null;
        }

        bool = userService.UpdatePassword(Integer.parseInt(id), password_old, password);
      } else {
        bool = userService.UpdatePassword(Integer.parseInt(id), password);
      }

      if (bool) {
        UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
        String userId = userSession.getUserId();
        if (id.equalsIgnoreCase(userId)) {
          userSession.setUserPwd(password);
        }
        out.print("yes");
      } else {
        out.print("no");
      }
      out.close();
    } catch (Exception e) {
      com.matech.framework.pub.util.Debug.print(4, "修改用户密码失败！", e);
      e.printStackTrace();
      throw e;
    } finally {
      DbUtil.close(conn); } DbUtil.close(conn);

    return null;
  }

  public ModelAndView Help(HttpServletRequest request, HttpServletResponse response)
  {
    return new ModelAndView("/user/PasswordHelp.jsp");
  }

  public ModelAndView userManagerList2(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    HashMap mapResult = new HashMap();
    ASFuntion CHF = new ASFuntion();

    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    String tap = CHF.showNull(request.getParameter("tap"));
    String departmentidNList = CHF.showNull(request.getParameter("departmentidNList"));
    String areaid = CHF.showNull(request.getParameter("areaid"));
    DbUtil dbUtil = null;
    List emtypes = null;
    String temp = UTILSysProperty.SysProperty.getProperty("clientDog");
    try
    {
      conn = new DBConnect().getConnect("");
      dbUtil = new DbUtil(conn);
      emtypes = dbUtil.select(KDicVO.class, "select * from {0} where ctype=?", new Object[] { "人员库" });

      request.setAttribute("emtypes", emtypes);
    }
    catch (Exception e) {
      com.matech.framework.pub.util.Debug.print(4, "查询用户列表失败！", e);
      e.printStackTrace();
      throw e;
    } finally {
      DbUtil.close(rs);
      DbUtil.close(ps);
      DbUtil.close(conn);
    }

    String examineOrnot = CHF.showNull(request.getParameter("examineOrnot"));
    mapResult.put("tap", tap);
    mapResult.put("departmentidNList", departmentidNList);
    mapResult.put("areaid", areaid);
    if (("".equals(examineOrnot)) || (examineOrnot == null))
    {
      return new ModelAndView("/user/userManagerList2.jsp", mapResult);
    }
    return new ModelAndView("/user/ExamineList.jsp", mapResult);
  }

  public ModelAndView userListByEmtype(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    String emtype = request.getParameter("emtype");
    String departmentidNList = request.getParameter("departmentidNList");
    String areaid = request.getParameter("areaid");
    String logSql = " \tLEFT JOIN ( \n\tSELECT b.udate,b.utime,b.loginid,DATEDIFF(CURDATE(),b.udate) AS loginDay FROM (\t \n\tSELECT loginid,MAX(ABS(id))  AS logid \n\tFROM t_log WHERE cmdName ='用户登录'   \n\tGROUP BY loginid  ORDER BY ABS(id) \n\t) a  \n\tleft JOIN t_log b ON a.logid = b.id \n) d ON a.loginid = d.loginid";

    String strSql = "select a.ID,a.name as uname ,ka.name as aname ,a.loginid,if(password='c4ca4238a0b923820dcc509a6f75849b','未修改缺省密码','已修改') as pwd, case when Sex='M' or Sex='男' then '男' else '女' end Sex, Educational,DepartName,Post ,istips,c.roles,a.rank,a.pccpa_seqno,a.departmentid, \n a.BornDate,a.Diploma,a.Specialty,a.mobilePhone,a.tel_shortno,a.email,a.identityCard,\n  a.station,a.nation,a.diplomatime,a.entrytime,a.phone,a.phone_shortno,a.bank_card_no ,kr.autoid as krautoid, CASE  WHEN CONVERT(loginDay,char) ='0' THEN CONCAT('今天登录时间：',CONVERT(utime,char)) WHEN loginDay >0 THEN CONCAT(CONVERT(loginDay,char),'天闲置') ELSE  concat('未曾登录') END  AS printLoginInfo \n from asdb.k_User a left join asdb.k_department b  on a.departmentid = b.autoID \n " + 
      logSql + 
      " left join (select userid,group_concat(distinct rolename) as roles from k_userrole a,k_role b where a.rid=b.id group by userid)c on a.id=c.userid \n" + 
      " left join k_area ka on ka.autoid=a.departid " + 
      
      " left join k_rank kr ON kr.name =a.rank " + 
      " where  a.emtype='" + emtype + "' and b.autoid=" + departmentidNList;

    DataGridProperty pp = new DataGridProperty();
    pp.setTableID("user_" + emtype);
    pp.setCustomerId("");
    pp.setPageSize_CH(50);
    pp.setWhichFieldIsValue(1);

    pp.setPrintEnable(true);
    //pp.setOrderBy_CH("krautoid,pccpa_seqno,id");
    pp.setOrderBy_CH("ka.orderid,b.property,a.pccpa_seqno");
    pp.setDirection("asc,asc,asc");
    pp.setColumnWidth("10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10");

    pp.addColumn("姓名", "uname");
    pp.addColumn("登录名", "loginid");

    pp.addColumn("所属区域", "aname");
    pp.addColumn("所属部门", "DepartName");
    pp.addColumn("职级", "rank");
    pp.addColumn("所内排序", "pccpa_seqno");
    pp.addColumn("手机号码", "mobilePhone");
    pp.addColumn("手机短号", "phone_shortno");
    pp.addColumn("办公电话", "phone");
    pp.addColumn("办公短号", "tel_shortno");
    pp.addColumn("银行卡号", "bank_card_no");

    pp.setInputType("radio");
    pp.setSQL(strSql);
    request.setAttribute("emtype", emtype);
    request.setAttribute("departmentidNList", departmentidNList);
    request.setAttribute("areaid", areaid);
    request.getSession().setAttribute("DGProperty_" + pp.getTableID(), pp);
    return new ModelAndView("user/userManagerListByEmtype.jsp");
  }

  public ModelAndView updatePccpa_seqno(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection conn = null;
    DbUtil dbUtil = null;
    WebUtil webUtil = new WebUtil(request, response);
    UserSession userSession = webUtil.getUserSession();
    String userid = request.getParameter("userid");
    String pccpa_seqno = request.getParameter("pccpa_seqno");
    int eff = 0;
    String sql = "update k_user set pccpa_seqno=? where id=" + userid;
    String re = "";
    response.setContentType("text/html;charset=utf-8");
    try {
      conn = new DBConnect().getConnect();
      dbUtil = new DbUtil(conn);
      eff += dbUtil.executeUpdate(sql, new Object[] { pccpa_seqno });
      if (eff > 0)
        re = "修改成功";
      else
        re = "修改失败";
    }
    catch (Exception ex) {
      re = ex.getLocalizedMessage();
    } finally {
      DbUtil.close(conn);
    }
    response.getWriter().write(re);
    return null;
  }

  public ModelAndView userManagerList(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    HashMap mapResult = new HashMap();
    ASFuntion CHF = new ASFuntion();

    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    String tap = CHF.showNull(request.getParameter("tap"));
    String departmentidNList = CHF.showNull(request.getParameter("departmentidNList"));
    DbUtil dbUtil = null;
    try {
      String temp = UTILSysProperty.SysProperty.getProperty("clientDog");
      conn = new DBConnect().getConnect("");
      String revert = request.getParameter("revert");
      dbUtil = new DbUtil(conn);
      String departName = null;
      String judge = CHF.showNull(request.getParameter("judge"));
      mapResult.put("judge", judge);

      String state = " and state=0 ";

      String tabOpt = "0";

      DataGridProperty pp = new DataGridProperty();
      pp.setTableID("user");
      pp.setCustomerId("");
      pp.setPageSize_CH(50);
      pp.setWhichFieldIsValue(1);

      pp.setPrintEnable(true);

      pp.setInputType("radio");

      UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
      String menuid = CHF.showNull(request.getParameter("menuid"));
      String departments = new UserPopedomService(conn).getUserPopedom(userSession.getUserId(), "user");

      String strSql = ""; String sql = "";
      String departmentid = userSession.getUserAuditDepartmentId();
      String userid = userSession.getUserId();
      UserService us = new UserService(conn);

      String logSql = " \tLEFT JOIN ( \n\tSELECT b.udate,b.utime,b.loginid,DATEDIFF(CURDATE(),b.udate) AS loginDay FROM (\t \n\tSELECT loginid,MAX(ABS(id))  AS logid \n\tFROM t_log WHERE cmdName ='用户登录'   \n\tGROUP BY loginid  ORDER BY ABS(id) \n\t) a  \n\tleft JOIN t_log b ON a.logid = b.id \n) d ON a.loginid = d.loginid";

      String goMsgAhref = " '<img src=" + request.getContextPath() + "/img/goMsg.png onclick=goMsg(',a.id,') alt=手机短信提醒登录>'";

      strSql = "select ID,Name,a.loginid,if(password='c4ca4238a0b923820dcc509a6f75849b','未修改缺省密码','已修改') as pwd, case when Sex='M' or Sex='男' then '男' else '女' end Sex, Educational,DepartName,Post ,istips,c.roles,a.rank,a.departmentid, \n a.BornDate,a.Diploma,a.Specialty,a.mobilePhone,a.email,a.identityCard,\n  a.station,a.nation,a.diplomatime,a.entrytime,a.phone, CASE  WHEN loginDay =0 THEN CONCAT(CONVERT('今天登录时间：',CHAR),utime) WHEN loginDay>0 THEN CONCAT(loginDay,'天闲置'," + 
        goMsgAhref + ") ELSE  concat('未曾登录'," + goMsgAhref + ") END  AS loginInfo, \n" + 
        " CASE  WHEN CONVERT(loginDay,char) ='0' THEN CONCAT('今天登录时间：',CONVERT(utime,char)) WHEN loginDay >0 THEN CONCAT(CONVERT(loginDay,char),'天闲置') ELSE  concat('未曾登录') END  AS printLoginInfo \n" + 
        " from asdb.k_User a left join asdb.k_department b  on a.departmentid = b.autoID \n " + 
        logSql + 
        " left join (select userid,group_concat(distinct rolename) as roles from k_userrole a,k_role b where a.rid=b.id group by userid)c on a.id=c.userid \n" + 
        " where 1=1 ";

      if (!StringUtil.isBlank(departmentidNList)) {
        strSql = "select ID,Name,a.loginid,if(password='c4ca4238a0b923820dcc509a6f75849b','未修改缺省密码','已修改') as pwd, case when Sex='M' or Sex='男' then '男' else '女' end Sex, Educational,DepartName,Post ,istips,c.roles,a.rank,a.departmentid, \n a.BornDate,a.Diploma,a.Specialty,a.mobilePhone,a.email,a.identityCard,\n  a.station,a.nation,a.diplomatime,a.entrytime,a.phone,a.tel_shortno,a.phone_shortno,a.bank_card_no ,  CASE  WHEN loginDay =0 THEN CONCAT(CONVERT('今天登录时间：',CHAR),utime) WHEN loginDay>0 THEN CONCAT(loginDay,'天闲置'," + 
          goMsgAhref + ") ELSE  concat('未曾登录'," + goMsgAhref + ") END  AS loginInfo, \n" + 
          " CASE  WHEN CONVERT(loginDay,char) ='0' THEN CONCAT('今天登录时间：',CONVERT(utime,char)) WHEN loginDay >0 THEN CONCAT(CONVERT(loginDay,char),'天闲置') ELSE  concat('未曾登录') END  AS printLoginInfo \n" + 
          " from asdb.k_User a left join asdb.k_department b  on a.departmentid = b.autoID \n " + 
          logSql + 
          " left join (select userid,group_concat(distinct rolename) as roles from k_userrole a,k_role b where a.rid=b.id group by userid)c on a.id=c.userid \n" + 
          " where 1=1 and b.autoid=" + departmentidNList;
      }
      pp.setOrderBy_CH("state,id,istips,loginid");
      pp.setDirection("asc,asc,asc,asc");
      pp.setColumnWidth("10,10,5,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10");

      pp.addColumn("姓名", "Name");
      pp.addColumn("登录名", "loginid");
      pp.addColumn("性别", "Sex", "showCenter");

      pp.addColumn("所属部门", "DepartName");

      pp.addColumn("职级", "rank");
      pp.addColumn("权限", "roles", "hide");
      pp.addColumn("闲置时间", "loginInfo", "hide");
      pp.addColumn("生日", "BornDate", "hide");
      pp.addColumn("毕业院校", "Diploma", "hide");
      pp.addColumn("专长", "Specialty", "hide");
      pp.addColumn("手机号码", "mobilePhone");

      pp.addColumn("手机短号", "tel_shortno");
      pp.addColumn("办公电话", "phone");
      pp.addColumn("办公短号", "phone_shortno");
      pp.addColumn("银行卡号", "bank_card_no");

      pp.addColumn("电子邮件", "email", "hide");
      pp.addColumn("证件号码", "identityCard", "hide");
      pp.addColumn("工号", "station", "hide");
      pp.addColumn("民族", "nation", "hide");
      pp.addColumn("毕业时间", "diplomatime", "hide");
      pp.addColumn("入职时间", "entrytime", "hide");
      if ("admin".equals(userSession.getUserLoginId())) {
        pp.addColumn("密码状态", "pwd");

        if ("1".equals(temp)) {
          String printSql = "";
          if ((departmentid != null) && (!"".equals(departmentid)))
          {
            strSql = "select DISTINCT ID,Name,loginid,if(password='c4ca4238a0b923820dcc509a6f75849b','未修改缺省密码','已修改') as pwd, \n case when Sex='M' or Sex='男' then '男' else '女' end Sex,Educational,DepartName,Post, clientDogSysUi, \n if(clientDogSysUi='','<input type=\"button\"  value=\"绑定狗\" style=\"width: 100px;\" class=\"flyBT\"  onclick=\"bindDog(this);\"  title=\"绑定狗信息\" />', \n '<input type=\"button\"  value=\"解除狗绑定\" style=\"width: 100px;\" class=\"flyBT\"  onclick=\"bindDog(this);\" title=\"解除狗绑定\" />' ) aButton,a.rank,c.roles,istips,a.departmentid \n\tfrom k_User a  \n\tinner join (  \n\t\tselect a.*  \n\t\tfrom k_department a\t) b  on a.departmentid = b.autoID  \n\tleft join (select userid,group_concat(distinct rolename) as roles from k_userrole a,k_role b where a.rid=b.id group by userid)c on a.id=c.userid \n\twhere 1=1  and (a.departmentid='" + 
              departmentid + "' or a.departmentid in (" + departments + "))   \n";
          }
          else
          {
            strSql = "select ID,Name,loginid,if(password='c4ca4238a0b923820dcc509a6f75849b','未修改缺省密码','已修改') as pwd, case when Sex='M' or Sex='男' then '男' else '女' end Sex,Educational,DepartName,Post, clientDogSysUi, if(clientDogSysUi='','<input type=\"button\"  value=\"绑定狗\" style=\"width: 100px;\" class=\"flyBT\"  onclick=\"bindDog(this);\"  title=\"绑定狗信息\" />',  '<input type=\"button\"  value=\"解除狗绑定\" style=\"width: 100px;\" class=\"flyBT\"  onclick=\"bindDog(this);\" title=\"解除狗绑定\" />' ) aButton,a.rank,c.roles,istips,a.departmentid  from asdb.k_User a left join asdb.k_department b  on a.departmentid = b.autoID  left join (select userid,group_concat(distinct rolename) as roles from k_userrole a,k_role b where a.rid=b.id group by userid)c on a.id=c.userid \n where 1=1  ";

            printSql = "select ID,Name,loginid,case when Sex='M' or Sex='男' then '男' else '女' end Sex,Educational,DepartName,Post,if(password='c4ca4238a0b923820dcc509a6f75849b','未修改缺省密码','已修改') as pwd, clientDogSysUi from asdb.k_User a left join asdb.k_department b  on a.departmentid = b.autoID where 1=1  " + 
              state;
          }
          pp.addColumn("狗信息", "clientDogSysUi");
          pp.addColumn("绑定操作", "aButton", "showCenter");
        }

      }

      sql = sql + strSql + state;
      String subSearchID = CHF.showNull(request.getParameter("loginid"));
      String subSearchName = CHF.showNull(request.getParameter("name"));
      String subSearchDepId = CHF.showNull(request.getParameter("department"));
      String subSearchRole = CHF.showNull(request.getParameter("role"));
      String subSearchRank = CHF.showNull(request.getParameter("rank"));

      String opt = CHF.showNull(request.getParameter("opt"));

      if (!"save".equals(opt)) {
        if (!subSearchID.equals(""))
          sql = sql + " and a.loginId like '%" + subSearchID + "%'";
        if (!subSearchName.equals(""))
          sql = sql + " and a.name like '%" + subSearchName + "%'";
        if (!subSearchDepId.equals(""))
          sql = sql + " and a.departmentid ='" + subSearchDepId + "'";
        if (!subSearchRole.equals(""))
          sql = sql + " and c.roles like '%" + subSearchRole + "%' ";
        if (!subSearchRank.equals(""))
          sql = sql + " and  a.rank like '%" + subSearchRank + "%'";
      }
      pp.setSQL(sql);

      mapResult.put("revert", revert);
      mapResult.put("temp", temp);
      request.getSession().setAttribute("DGProperty_" + pp.getTableID(), pp);

      state = " and state=1 ";

      DataGridProperty pp2 = new DataGridProperty();
      pp2.setTableID("user2");
      pp2.setCustomerId("");
      pp2.setPageSize_CH(50);
      pp2.setWhichFieldIsValue(1);
      pp2.setUseBufferGrid(false);
      pp2.setPrintEnable(true);
      pp2.setPrintTitle("禁用人员列表");

      pp2.setInputType("radio");
      pp2.setTrAction("style='cursor:hand;' onclick='goSetUsrID();'");
      String sql2 = strSql + state;

      pp2.setOrderBy_CH("DepartName,istips,a.loginid");
      pp2.setDirection("asc,asc,asc");

      pp2.addColumn("编号", "id");
      pp2.addColumn("姓名", "Name");
      pp2.addColumn("登录名", "loginid");
      pp2.addColumn("性别", "Sex", "showCenter");
      pp2.addColumn("学历", "Educational");
      pp2.addColumn("所属部门", "DepartName");
      pp2.addColumn("岗位", "Post");
      pp2.addColumn("密码状态", "pwd");

      String subSearchID2 = CHF.showNull(request.getParameter("loginid2"));
      String subSearchName2 = CHF.showNull(request.getParameter("name2"));
      String subSearchDepId2 = CHF.showNull(request.getParameter("department2"));
      String subSearchRole2 = CHF.showNull(request.getParameter("role2"));
      String subSearchRank2 = CHF.showNull(request.getParameter("rank2"));

      if (!subSearchID2.equals(""))
        sql2 = sql2 + " and a.loginid like '%" + subSearchID2 + "%'";
      if (!subSearchName2.equals(""))
        sql2 = sql2 + " and a.name like '%" + subSearchName2 + "%'";
      if (!subSearchDepId2.equals(""))
        sql2 = sql2 + " and a.departmentid ='" + subSearchDepId2 + "'";
      if (!subSearchRole2.equals(""))
        sql2 = sql2 + " and c.roles like '%" + subSearchRole2 + "%' ";
      if (!subSearchRank2.equals("")) {
        sql2 = sql2 + " and  a.rank like '%" + subSearchRank2 + "%'";
      }
      pp2.setSQL(sql2);

      request.getSession().setAttribute("DGProperty_" + pp2.getTableID(), pp2);

      if ((!subSearchID2.equals("")) || (!subSearchName2.equals(""))) {
        tabOpt = "1";
      }
      mapResult.put("tabOpt", tabOpt);

      String svalue = "";

      sql = "select svalue from s_config where sname='新增人员是否审批'";

      ps = conn.prepareStatement(sql);

      rs = ps.executeQuery();
      while (rs.next()) {
        svalue = rs.getString(1);
      }

      request.setAttribute("svalue", svalue);

      DataGridProperty pp3 = new DataGridProperty();
      pp3.setTableID("partTimeList");
      pp3.setCustomerId("");
      pp3.setPageSize_CH(50);
      pp3.setWhichFieldIsValue(1);
      pp3.setInputType("radio");
      pp3.setOrderBy_CH("loginid");
      pp3.setDirection("desc");
      pp3.setPrintEnable(true);
      pp3.setPrintTitle("外聘人员列表");

      String sql3 = "select * from k_parttime where 1=1 ${partTimeName} ${partTimDdepartment}";

      pp3.setSQL(sql3);

      pp3.addColumn("姓名", "Name");
      pp3.addColumn("登录名", "loginid", "hide");
      pp3.addColumn("性别", "Sex", "showCenter");

      pp3.addColumn("所属部门", "DepartName");

      pp3.addColumn("薪酬级别", "rank");
      pp3.addColumn("权限", "roles");
      pp3.addColumn("闲置时间", "loginInfo", "hide");
      pp3.addColumn("生日", "BornDate", "hide");
      pp3.addColumn("毕业院校", "Diploma", "hide");
      pp3.addColumn("专长", "Specialty", "hide");
      pp3.addColumn("手机号码", "mobilePhone", "hide");
      pp3.addColumn("电子邮件", "email", "hide");
      pp3.addColumn("证件号码", "identityCard", "hide");
      pp3.addColumn("工号", "station", "hide");
      pp3.addColumn("民族", "nation", "hide");
      pp3.addColumn("毕业时间", "diplomatime", "hide");
      pp3.addColumn("入职时间", "entrytime", "hide");
      pp3.addColumn("办公电话", "phone", "hide");

      pp3.addSqlWhere("partTimeName", " and name = '${partTimeName}'");
      pp3.addSqlWhere("partTimDdepartment", " and departmentid ='${partTimDdepartment}'");

      request.getSession().setAttribute("DGProperty_" + pp3.getTableID(), pp3);
    }
    catch (Exception e)
    {
      com.matech.framework.pub.util.Debug.print(4, "查询用户列表失败！", e);
      e.printStackTrace();
      throw e;
    } finally {
      DbUtil.close(rs);
      DbUtil.close(ps);
      DbUtil.close(conn);
    }

    String examineOrnot = CHF.showNull(request.getParameter("examineOrnot"));
    mapResult.put("tap", tap);
    if (("".equals(examineOrnot)) || (examineOrnot == null))
    {
      return new ModelAndView("/user/userManagerList.jsp", mapResult);
    }
    return new ModelAndView("/user/ExamineList.jsp", mapResult);
  }

  public void departmentTreeByUseridAndMenuid(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    response.setContentType("text/html;charset=utf-8");
    Connection conn = null;
    DbUtil dbUtil = null;
    WebUtil webUtil = new WebUtil(request, response);
    UserSession userSession = webUtil.getUserSession();
    try {
      ASFuntion CHF = new ASFuntion();

      String checked = CHF.showNull(request.getParameter("checked"));

      String departid = CHF.showNull(request.getParameter("departid"));
      String areaid = CHF.showNull(request.getParameter("areaid"));
      String departname = CHF.showNull(request.getParameter("departname"));
      String isSubject = CHF.showNull(request.getParameter("isSubject"));
      String popedom = "";

      String userpopedom = CHF.showNull(request.getParameter("userpopedom"));

      String loginid = CHF.showNull(request.getParameter("loginid"));
      String menuid = CHF.showNull(request.getParameter("menuid"));

      conn = new DBConnect().getConnect("");
      dbUtil = new DbUtil(conn);

      String addUser = CHF.showNull(request.getParameter("addUser"));
      UserPopedomService userPopedomService = new UserPopedomService(conn);
      popedom = userPopedomService.getUserIdPopedom(userSession.getUserId(), menuid);
      System.out.println(addUser + "|" + checked + "|" + departid + "|" + areaid + "|" + departname + "|" + isSubject);

      DepartmentService ds = new DepartmentService(conn);
      UserPopedomService up = new UserPopedomService(conn);
      String departments = up.getLoginIdPopedom(loginid, menuid);

      ds.setAddUser(addUser);

      List list = null;
      if (("".equals(isSubject)) || ("undefined".equals(isSubject))) {
        list = ds.getOrgan(checked);
        if (list == null)
        {
          if ("userpopedom".equals(userpopedom)) {
            checked = "false";
            ds.setUserpopedom(departments);
          }
          departid = "555555";
          list = ds.getDepartment(departid, areaid, checked);
        }

        Map favRoot = new HashMap();
        favRoot.put("id", UUID.randomUUID().toString());
        favRoot.put("text", "自定义用户");
        favRoot.put("leaf", Boolean.valueOf(false));
        favRoot.put("isSubject", "user_fav_root");
      }
      else if ("user_fav_root".equals(isSubject)) {
        List<UserFavVO> userFavVOs = dbUtil.select(UserFavVO.class, 
          "select * from {0} where userid=? ", new Object[] { userSession.getUserId() });
        list = new ArrayList();
        for (UserFavVO userFavVO : userFavVOs) {
          Map favRoot = new HashMap();
          favRoot.put("id", userFavVO.getUuid());
          favRoot.put("text", userFavVO.getName());
          favRoot.put("leaf", Boolean.valueOf(true));
          favRoot.put("isSubject", "user_fav");
          favRoot.put("fav_user_ids", userFavVO.getFav_user_ids());
          list.add(favRoot);
        }

      }
      else if ("1".equals(isSubject))
      {
        list = ds.getArea(departid, checked);
        if (list == null)
        {
          if ("userpopedom".equals(userpopedom)) {
            checked = "false";
            ds.setUserpopedom(departments);
          }
          list = ds.getDepartment(departid, areaid, checked);
          if ("true".equals(addUser)) {
            List list1 = ds.getUser(departid, checked);
            if (list1 != null) {
              if (list == null) list = new ArrayList();
              for (int i = 0; i < list1.size(); i++)
                list.add(list1.get(i));
            }
          }
        }
      }
      else
      {
        if ("userpopedom".equals(userpopedom)) {
          checked = "false";
          ds.setUserpopedom(departments);
        }
        list = ds.getDepartment(departid, areaid, checked);
        list = departmentPepodemFilter(list, popedom);
        if ("true".equals(addUser)) {
          List list1 = ds.getUser(departid, checked);
          if (list1 != null) {
            if (list == null) list = new ArrayList();
            for (int i = 0; i < list1.size(); i++) {
              System.out.println(list1.get(i));
              list.add(list1.get(i));
            }
          }
        }

      }

      String json = "{}";
      if (list != null) {
        json = JSONArray.fromObject(list).toString();
      }
      System.out.println("json=" + json);
      response.getWriter().write(json);
    }
    catch (Exception e) {
      e.printStackTrace();
    } finally {
      DbUtil.close(conn);
    }
  }

  private List departmentPepodemFilter(List departmentList, String popedom) {
    List newDepartmentList = new ArrayList();
    if (StringUtil.isBlank(popedom)) {
      return newDepartmentList;
    }
    for (Iterator it = departmentList.iterator(); it.hasNext(); ) {
      Map departmentMap = (Map)it.next();
      String departid = (String)departmentMap.get("departid");
      if (popedom.contains(departid)) {
        newDepartmentList.add(departmentMap);
      }
    }
    return newDepartmentList;
  }

  public ModelAndView List(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    HashMap mapResult = new HashMap();
    ASFuntion CHF = new ASFuntion();

    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    String tap = CHF.showNull(request.getParameter("tap"));
    DbUtil dbUtil = null;
    try {
      String temp = UTILSysProperty.SysProperty.getProperty("clientDog");
      conn = new DBConnect().getConnect("");
      String revert = request.getParameter("revert");
      dbUtil = new DbUtil(conn);
      String departName = null;
      String judge = CHF.showNull(request.getParameter("judge"));
      mapResult.put("judge", judge);

      String state = " and state=0 ";

      String tabOpt = "0";

      DataGridProperty pp = new DataGridProperty();
      pp.setTableID("user");
      pp.setCustomerId("");
      pp.setPageSize_CH(50);
      pp.setWhichFieldIsValue(1);

      pp.setPrintEnable(true);

      pp.setInputType("radio");

      UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
      String menuid = CHF.showNull(request.getParameter("menuid"));
      String departments = new UserPopedomService(conn).getUserPopedom(userSession.getUserId(), "user");

      String strSql = ""; String sql = "";
      String departmentid = userSession.getUserAuditDepartmentId();
      String userid = userSession.getUserId();
      UserService us = new UserService(conn);

      String logSql = " \tLEFT JOIN ( \n\tSELECT b.udate,b.utime,b.loginid,DATEDIFF(CURDATE(),b.udate) AS loginDay FROM (\t \n\tSELECT loginid,MAX(ABS(id))  AS logid \n\tFROM t_log WHERE cmdName ='用户登录'   \n\tGROUP BY loginid  ORDER BY ABS(id) \n\t) a  \n\tleft JOIN t_log b ON a.logid = b.id \n) d ON a.loginid = d.loginid";

      String goMsgAhref = " '<img src=" + request.getContextPath() + "/img/goMsg.png onclick=goMsg(',a.id,') alt=手机短信提醒登录>'";

      strSql = "select ID,a.Name,a.loginid,if(password='c4ca4238a0b923820dcc509a6f75849b','未修改缺省密码','已修改') as pwd, case when Sex='M' or Sex='男' then '男' else '女' end Sex, Educational,DepartName,Post ,istips,c.roles,a.rank,a.departmentid, \n a.BornDate,a.Diploma,a.Specialty,a.mobilePhone,a.email,a.identityCard,\n  a.station,a.nation,a.diplomatime,a.entrytime,a.phone, CASE  WHEN loginDay =0 THEN CONCAT(CONVERT('今天登录时间：',CHAR),utime) WHEN loginDay>0 THEN CONCAT(loginDay,'天闲置'," + 
        goMsgAhref + ") ELSE  concat('未曾登录'," + goMsgAhref + ") END  AS loginInfo, \n" + 
        " CASE  WHEN CONVERT(loginDay,char) ='0' THEN CONCAT('今天登录时间：',CONVERT(utime,char)) WHEN loginDay >0 THEN CONCAT(CONVERT(loginDay,char),'天闲置') ELSE  concat('未曾登录') END  AS printLoginInfo \n" + 
        " from asdb.k_User a left join asdb.k_department b  on a.departmentid = b.autoID \n " + 
        " left join k_area ka on b.areaid = ka.autoid " +  //
        logSql + 
        " left join (select userid,group_concat(distinct rolename) as roles from k_userrole a,k_role b where a.rid=b.id group by userid)c on a.id=c.userid \n" + 
        " where 1=1 ";

     // pp.setOrderBy_CH("DepartName,istips,loginid");
      String orderby="ka.orderid,b.property,a.pccpa_seqno";
      pp.setOrderBy_CH(orderby);
      pp.setDirection("asc,asc,asc");
      pp.setColumnWidth("10,10,5,10,10,10,13");
      pp.setUseBufferGrid(false);
      pp.setPrintColumnWidth("30,20,10,20,35,25,35,40,35,20,30,30,30,30,30,30,30,30,30,30");
      pp.setPrintSqlColumn("Name,loginid,Sex,Educational,DepartName,Post,rank,roles,printLoginInfo,BornDate,Diploma,Specialty,mobilePhone,email,identityCard,station,nation,diplomatime,entrytime,phone");
      pp.setPrintColumn("姓名`登录名`性别`学历`所属部门`岗位`职级`操作权限`闲置时间`出生年月`毕业院校`特长`手机号码`电子邮件`身份证号`工号`民族`毕业时间`入职时间`办公电话");
      pp.setPrintCharColumn("10`13`15`16`18`19`20");

      pp.addColumn("姓名", "Name");
      pp.addColumn("登录名", "loginid");
      pp.addColumn("性别", "Sex", "showCenter");

      pp.addColumn("所属部门", "DepartName");

      pp.addColumn("薪酬级别", "rank");
      pp.addColumn("权限", "roles");
      pp.addColumn("闲置时间", "loginInfo");
      pp.addColumn("生日", "BornDate", "hide");
      pp.addColumn("毕业院校", "Diploma", "hide");
      pp.addColumn("专长", "Specialty", "hide");
      pp.addColumn("手机号码", "mobilePhone", "hide");
      pp.addColumn("电子邮件", "email", "hide");
      pp.addColumn("证件号码", "identityCard", "hide");
      pp.addColumn("工号", "station", "hide");
      pp.addColumn("民族", "nation", "hide");
      pp.addColumn("毕业时间", "diplomatime", "hide");
      pp.addColumn("入职时间", "entrytime", "hide");
      pp.addColumn("办公电话", "phone", "hide");
      if ("admin".equals(userSession.getUserLoginId())) {
        pp.addColumn("密码状态", "pwd");

        if ("1".equals(temp)) {
          String printSql = "";
          if ((departmentid != null) && (!"".equals(departmentid)))
          {
            strSql = "select DISTINCT ID,Name,loginid,if(password='c4ca4238a0b923820dcc509a6f75849b','未修改缺省密码','已修改') as pwd, \n case when Sex='M' or Sex='男' then '男' else '女' end Sex,Educational,DepartName,Post, clientDogSysUi, \n if(clientDogSysUi='','<input type=\"button\"  value=\"绑定狗\" style=\"width: 100px;\" class=\"flyBT\"  onclick=\"bindDog(this);\"  title=\"绑定狗信息\" />', \n '<input type=\"button\"  value=\"解除狗绑定\" style=\"width: 100px;\" class=\"flyBT\"  onclick=\"bindDog(this);\" title=\"解除狗绑定\" />' ) aButton,a.rank,c.roles,istips,a.departmentid \n\tfrom k_User a  \n\tinner join (  \n\t\tselect a.*  \n\t\tfrom k_department a\t) b  on a.departmentid = b.autoID  \n\tleft join (select userid,group_concat(distinct rolename) as roles from k_userrole a,k_role b where a.rid=b.id group by userid)c on a.id=c.userid \n\twhere 1=1  and (a.departmentid='" + 
              departmentid + "' or a.departmentid in (" + departments + "))   \n";
          }
          else
          {
            strSql = "select ID,Name,loginid,if(password='c4ca4238a0b923820dcc509a6f75849b','未修改缺省密码','已修改') as pwd, case when Sex='M' or Sex='男' then '男' else '女' end Sex,Educational,DepartName,Post, clientDogSysUi, if(clientDogSysUi='','<input type=\"button\"  value=\"绑定狗\" style=\"width: 100px;\" class=\"flyBT\"  onclick=\"bindDog(this);\"  title=\"绑定狗信息\" />',  '<input type=\"button\"  value=\"解除狗绑定\" style=\"width: 100px;\" class=\"flyBT\"  onclick=\"bindDog(this);\" title=\"解除狗绑定\" />' ) aButton,a.rank,c.roles,istips,a.departmentid  from asdb.k_User a left join asdb.k_department b  on a.departmentid = b.autoID  left join (select userid,group_concat(distinct rolename) as roles from k_userrole a,k_role b where a.rid=b.id group by userid)c on a.id=c.userid \n where 1=1  ";

            printSql = "select ID,Name,loginid,case when Sex='M' or Sex='男' then '男' else '女' end Sex,Educational,DepartName,Post,if(password='c4ca4238a0b923820dcc509a6f75849b','未修改缺省密码','已修改') as pwd, clientDogSysUi from asdb.k_User a left join asdb.k_department b  on a.departmentid = b.autoID where 1=1  " + 
              state;
          }
          pp.addColumn("狗信息", "clientDogSysUi");
          pp.addColumn("绑定操作", "aButton", "showCenter");
        }

      }

      sql = sql + strSql + state;
      String subSearchID = CHF.showNull(request.getParameter("loginid"));
      String subSearchName = CHF.showNull(request.getParameter("name"));
      String subSearchDepId = CHF.showNull(request.getParameter("department"));
      String subSearchRole = CHF.showNull(request.getParameter("role"));
      String subSearchRank = CHF.showNull(request.getParameter("rank"));

      String opt = CHF.showNull(request.getParameter("opt"));

      if (!"save".equals(opt)) {
        if (!subSearchID.equals(""))
          sql = sql + " and a.loginId like '%" + subSearchID + "%'";
        if (!subSearchName.equals(""))
          sql = sql + " and a.name like '%" + subSearchName + "%'";
        if (!subSearchDepId.equals(""))
          sql = sql + " and a.departmentid ='" + subSearchDepId + "'";
        if (!subSearchRole.equals(""))
          sql = sql + " and c.roles like '%" + subSearchRole + "%' ";
        if (!subSearchRank.equals(""))
          sql = sql + " and  a.rank like '%" + subSearchRank + "%'";
      }
      pp.setSQL(sql);

      mapResult.put("revert", revert);
      mapResult.put("temp", temp);
      request.getSession().setAttribute("DGProperty_" + pp.getTableID(), pp);

      state = " and state=1 ";

      DataGridProperty pp2 = new DataGridProperty();
      pp2.setTableID("user2");
      pp2.setCustomerId("");
      pp2.setPageSize_CH(50);
      pp2.setWhichFieldIsValue(1);
      pp2.setUseBufferGrid(false);
      pp2.setPrintEnable(true);
      pp2.setPrintTitle("禁用人员列表");

      pp2.setInputType("radio");
      pp2.setTrAction("style='cursor:hand;' onclick='goSetUsrID();'");
      String sql2 = strSql + state;

      pp2.setOrderBy_CH(orderby);
      //pp2.setOrderBy_CH("DepartName,istips,a.loginid");
      
      pp2.setDirection("asc,asc,asc");

      pp2.addColumn("编号", "id");
      pp2.addColumn("姓名", "Name");
      pp2.addColumn("登录名", "loginid");
      pp2.addColumn("性别", "Sex", "showCenter");
      pp2.addColumn("学历", "Educational");
      pp2.addColumn("所属部门", "DepartName");
      pp2.addColumn("岗位", "Post");
      pp2.addColumn("密码状态", "pwd");

      String subSearchID2 = CHF.showNull(request.getParameter("loginid2"));
      String subSearchName2 = CHF.showNull(request.getParameter("name2"));
      String subSearchDepId2 = CHF.showNull(request.getParameter("department2"));
      String subSearchRole2 = CHF.showNull(request.getParameter("role2"));
      String subSearchRank2 = CHF.showNull(request.getParameter("rank2"));

      if (!subSearchID2.equals(""))
        sql2 = sql2 + " and a.loginid like '%" + subSearchID2 + "%'";
      if (!subSearchName2.equals(""))
        sql2 = sql2 + " and a.name like '%" + subSearchName2 + "%'";
      if (!subSearchDepId2.equals(""))
        sql2 = sql2 + " and a.departmentid ='" + subSearchDepId2 + "'";
      if (!subSearchRole2.equals(""))
        sql2 = sql2 + " and c.roles like '%" + subSearchRole2 + "%' ";
      if (!subSearchRank2.equals("")) {
        sql2 = sql2 + " and  a.rank like '%" + subSearchRank2 + "%'";
      }
      pp2.setSQL(sql2);

      request.getSession().setAttribute("DGProperty_" + pp2.getTableID(), pp2);

      if ((!subSearchID2.equals("")) || (!subSearchName2.equals(""))) {
        tabOpt = "1";
      }
      mapResult.put("tabOpt", tabOpt);

      String svalue = "";

      sql = "select svalue from s_config where sname='新增人员是否审批'";

      ps = conn.prepareStatement(sql);

      rs = ps.executeQuery();
      while (rs.next()) {
        svalue = rs.getString(1);
      }

      request.setAttribute("svalue", svalue);

      DataGridProperty pp3 = new DataGridProperty();
      pp3.setTableID("partTimeList");
      pp3.setCustomerId("");
      pp3.setPageSize_CH(50);
      pp3.setWhichFieldIsValue(1);
      pp3.setInputType("radio");
      pp3.setOrderBy_CH("loginid");
      pp3.setDirection("desc");
      pp3.setPrintEnable(true);
      pp3.setPrintTitle("外聘人员列表");

      String sql3 = "select * from k_parttime where 1=1 ${partTimeName} ${partTimDdepartment}";

      pp3.setSQL(sql3);

      pp3.addColumn("姓名", "Name");
      pp3.addColumn("登录名", "loginid", "hide");
      pp3.addColumn("性别", "Sex", "showCenter");

      pp3.addColumn("所属部门", "DepartName");

      pp3.addColumn("薪酬级别", "rank");
      pp3.addColumn("权限", "roles");
      pp3.addColumn("闲置时间", "loginInfo", "hide");
      pp3.addColumn("生日", "BornDate", "hide");
      pp3.addColumn("毕业院校", "Diploma", "hide");
      pp3.addColumn("专长", "Specialty", "hide");
      pp3.addColumn("手机号码", "mobilePhone", "hide");
      pp3.addColumn("电子邮件", "email", "hide");
      pp3.addColumn("证件号码", "identityCard", "hide");
      pp3.addColumn("工号", "station", "hide");
      pp3.addColumn("民族", "nation", "hide");
      pp3.addColumn("毕业时间", "diplomatime", "hide");
      pp3.addColumn("入职时间", "entrytime", "hide");
      pp3.addColumn("办公电话", "phone", "hide");

      pp3.addSqlWhere("partTimeName", " and name = '${partTimeName}'");
      pp3.addSqlWhere("partTimDdepartment", " and departmentid ='${partTimDdepartment}'");

      request.getSession().setAttribute("DGProperty_" + pp3.getTableID(), pp3);
    }
    catch (Exception e)
    {
      com.matech.framework.pub.util.Debug.print(4, "查询用户列表失败！", e);
      e.printStackTrace();
      throw e;
    } finally {
      DbUtil.close(rs);
      DbUtil.close(ps);
      DbUtil.close(conn);
    }

    String examineOrnot = CHF.showNull(request.getParameter("examineOrnot"));
    mapResult.put("tap", tap);
    if (("".equals(examineOrnot)) || (examineOrnot == null))
    {
      return new ModelAndView("/user/List.jsp", mapResult);
    }
    return new ModelAndView("/user/ExamineList.jsp", mapResult);
  }

  public ModelAndView List2(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    HashMap mapResult = new HashMap();
    ASFuntion CHF = new ASFuntion();

    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try
    {
      System.out.println("============User List=================");
      MultiDbIF db = (MultiDbIF)UTILSysProperty.context.getBean("MultiDbAction");
      String temp = UTILSysProperty.SysProperty.getProperty("clientDog");

      String revert = request.getParameter("revert");

      String state = " and state=0 ";

      String sql = "";

      state = " and state=1 ";

      DataGridProperty pp2 = new DataGridProperty();
      pp2.setTableID("user3");
      pp2.setCustomerId("");
      pp2.setPageSize_CH(50);
      pp2.setWhichFieldIsValue(1);
      pp2.setPrintEnable(true);
      pp2.setPrintTitle("待审批人员列表");

      pp2.setInputType("radio");
      pp2.setTrAction("style='cursor:hand;' onclick='goSetUsrID();'");
      String sql2 = "select ID,Name,round(loginid) loginid,if(password='c4ca4238a0b923820dcc509a6f75849b','未修改缺省密码','已修改') as pwd," + 
        db.mIf("Sex='M' or Sex='男'", "'男'", "'女'") + " Sex,Educational,DepartName,Post from asdb.k_User a left join asdb.k_department b " + 
        " on a.departmentid = b.autoID where 1=1 and (b.url ='' or b.url is null)" + state + " and  loginid >='1' and loginid <= '999999999' " + "union " + 
        " select ID,Name,loginid,if(password='c4ca4238a0b923820dcc509a6f75849b','未修改缺省密码','已修改') as pwd," + db.mIf("Sex='M' or Sex='男'", "'男'", "'女'") + " Sex,Educational,DepartName,Post from asdb.k_User a left join asdb.k_department b  on a.departmentid = b.autoID where 1=1 and (b.url ='' or b.url is null) " + state + " and loginid >='a' and loginid <= 'zzzzzzzzz'";

      pp2.setOrderBy_CH("id");
      pp2.setDirection("asc");
      pp2.setUseBufferGrid(false);

      pp2.addColumn("编号", "id");
      pp2.addColumn("姓名", "Name");
      pp2.addColumn("登录名", "loginid");
      pp2.addColumn("性别", "Sex", "showCenter");
      pp2.addColumn("学历", "Educational");
      pp2.addColumn("所属部门", "DepartName");
      pp2.addColumn("岗位", "Post");
      pp2.addColumn("密码状态", "pwd");

      String subSearchID2 = CHF.showNull(request.getParameter("loginid2"));
      String subSearchName2 = CHF.showNull(request.getParameter("name2"));

      if (!subSearchID2.equals(""))
        sql2 = sql2 + " and loginid like '%" + subSearchID2 + "%'";
      if (!subSearchName2.equals("")) {
        sql2 = sql2 + " and name like '%" + subSearchName2 + "%'";
      }
      pp2.setSQL(sql2);

      request.getSession().setAttribute("DGProperty_" + pp2.getTableID(), pp2);

      String svalue = "";

      sql = "select svalue from s_config where sname='新增人员是否审批'";

      conn = new DBConnect().getConnect("");
      ps = conn.prepareStatement(sql);

      rs = ps.executeQuery();
      while (rs.next()) {
        svalue = rs.getString(1);
      }

      request.setAttribute("svalue", svalue);
    }
    catch (Exception e) {
      com.matech.framework.pub.util.Debug.print(4, "查询用户列表失败！", e);
      e.printStackTrace();
      throw e;
    } finally {
      DbUtil.close(rs);
      DbUtil.close(ps);
      DbUtil.close(conn);
    }

    String examineOrnot = CHF.showNull(request.getParameter("examineOrnot"));
    examineOrnot = "true";

    if (("".equals(examineOrnot)) || (examineOrnot == null)) {
      return new ModelAndView("/user/List.jsp", mapResult);
    }
    return new ModelAndView("/user/ExamineList.jsp", mapResult);
  }

  public ModelAndView ProjectPopedom(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    HashMap mapResult = new HashMap();
    Connection conn = null;
    try {
      conn = new DBConnect().getConnect("");
      PopedomService pps = new PopedomService(conn);
      String opt = request.getParameter("opt");
      String loginid = "";
      if ("save".equals(opt)) {
        loginid = request.getParameter("loginid");
        String stAll = request.getParameter("stAll");
        pps.SaveProjectPopedom(stAll, loginid);
        ModelAndView localModelAndView = List(request, response);
        return localModelAndView;
      }
      String id = request.getParameter("id");
      loginid = new UserService(conn).getUser(id, "id").getLoginid();
      String name = new UserService(conn).getUser(id, "id").getName();

      String sTable = pps.getATreeTable("555555");
      String ppm = pps.getProjectPopedom(loginid);

      mapResult.put("name", name);
      mapResult.put("loginid", loginid);
      mapResult.put("sTable", sTable);
      mapResult.put("ppm", ppm);
    }
    catch (Exception e) {
      com.matech.framework.pub.util.Debug.print(4, "查询个人项目项目权限失败！", e);
      e.printStackTrace();
      throw e;
    } finally {
      DbUtil.close(conn); } DbUtil.close(conn);

    return new ModelAndView("/user/ProjectTree.jsp", mapResult);
  }

  public ModelAndView Item(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    HashMap mapResult = new HashMap();
    Connection conn = null;
    try {
      conn = new DBConnect().getConnect("");

      MultiDbIF db = (MultiDbIF)UTILSysProperty.context.getBean("MultiDbAction");

      String id = request.getParameter("id");
      String name = new UserService(conn).getUser(id, "id").getName();

      DataGridProperty pp = new DataGridProperty();
      pp.setTableID("item");

      pp.setCustomerId("");
      pp.setPageSize_CH(50);
      pp.setWhichFieldIsValue(1);
      pp.setPrintEnable(false);

      String[] args = { 
        "'<a href=\\'user.do?method=Task&projectid=',b.projectid,'&id=" + id + "\\'>',c.departname,'</a>'" };

      String sql = "select b.projectid, b.projectname,a.role,b.ProjectCreated, " + 
        db.mConcat(args) + " departname " + 
        " from k_customer c,z_auditpeople a,z_project b   where a.userid='" + id + "' and a.projectid=b.projectid and b.customerid=c.departid ";

      pp.setOrderBy_CH("ProjectCreated");
      pp.setDirection("desc");
      pp.setUseBufferGrid(false);

      pp.addColumn("项目编号", "projectid");
      pp.addColumn("项目名称", "projectname");
      pp.addColumn("项目中担任角色", "role");
      pp.addColumn("项目起始日期", "ProjectCreated");
      pp.addColumn("客户名称", "departname");

      pp.setSQL(sql);

      request.getSession().setAttribute("DGProperty_" + pp.getTableID(), pp);
      mapResult.put("name", name);
    }
    catch (Exception e) {
      com.matech.framework.pub.util.Debug.print(4, "查询个人项目简历失败！", e);
      e.printStackTrace();
      throw e;
    } finally {
      DbUtil.close(conn);
    }
    return new ModelAndView("/user/ItemList.jsp", mapResult);
  }

  public ModelAndView UserPopedom(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    HashMap mapResult = new HashMap();
    Connection conn = null;
    ASFuntion CHF = new ASFuntion();
    try {
      conn = new DBConnect().getConnect("");
      UserSession us = (UserSession)request.getSession().getAttribute("userSession");
      String SysPpm = us.getUserAuditOfficePopedom();
      PopedomService pps = new PopedomService(conn, SysPpm);
      String opt = request.getParameter("opt");
      String loginid = "";
      if ("save".equals(opt)) {
          loginid = request.getParameter("loginid");
          String stAll = request.getParameter("stAll");
          pps.SavePopedom(stAll, loginid);

          String[] departmentid = request.getParameterValues("departmentid");
          String[] menuid = request.getParameterValues("menuid");
          new UserPopedomService(conn).saveLoginIdPopedom(loginid, menuid, departmentid);

          ModelAndView localModelAndView = List(request, response);
          return localModelAndView;
      }
      String id = request.getParameter("id");
      loginid = new UserService(conn).getUser(id, "id").getLoginid();
      String name = new UserService(conn).getUser(id, "id").getName();

      if ("".equals(loginid))
      {
        loginid = "00";
      }
      try {
        pps.setUserPopedom(true); } catch (Exception localException1) {
      }
      String sTable = pps.getPopedomTree("00", loginid);

      mapResult.put("name", name);
      mapResult.put("loginid", loginid);
      mapResult.put("sTable", sTable);
    }
    catch (Exception e) {
      com.matech.framework.pub.util.Debug.print(4, "查询个人项目项目权限失败！", e);
      e.printStackTrace();
      throw e;
    } finally {
      DbUtil.close(conn); } DbUtil.close(conn);

    return new ModelAndView("/user/UserTree.jsp", mapResult);
  }

  public ModelAndView Upload(HttpServletRequest request, HttpServletResponse response)
  {
    return new ModelAndView("/user/UserUpload.jsp");
  }

  public ModelAndView UploadPeople(HttpServletRequest request, HttpServletResponse response)
  {
    return new ModelAndView("/user/PeopleUpload.jsp");
  }

  public ModelAndView UploadProject(HttpServletRequest request, HttpServletResponse response)
  {
    return new ModelAndView("/user/ProjectUpload.jsp");
  }

  public ModelAndView uploadUpdate(HttpServletRequest request, HttpServletResponse response)
  {
    return new ModelAndView("/user/uUserUpload.jsp");
  }

  public ModelAndView updateUpload(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    PrintWriter out = null;
    Connection conn = null;
    Single sl = new Single();
    UserSession us = (UserSession)request.getSession().getAttribute("userSession");
    String lockmsg = "装载帐套数据";
    try
    {
      response.setContentType("text/html;charset=utf-8");

      out = response.getWriter();

      Map parameters = null;

      String uploadtemppath = "";

      String strFullFileName = "";

      String User = "";

      String popedom = ".22.2205.2210.2214.221405.2215.2216.2217.221701.221702.221703.221704.221725.221730.221735.32.3205.3210.3215.3216.3225.42.4201.4203.4204.4206.4208.4212.4215.421505.421510.4226.4230.45.4505.4510.4515.4520.4525.4530.4535.52.5204.5205.520505.520510.520515.5210.5215.5217.5220.5225.5226.522605.522610.522615.5230.523005.523010.5232.451003.5236.5250.525005.525010.525025.525070.525080.62.6205.620535.620537.620540.620545.620560.620565.6210.6215.6220.622005.622010.622015.622020.6225.622505.622510.72.7205.720505.720530.7207.720705.720710.7209.720905.720910.7215.7220.722005.752001.92.";

      MyFileUpload myfileUpload = new MyFileUpload(request);
      uploadtemppath = myfileUpload.UploadFile(null, null);
      parameters = myfileUpload.getMap();
      System.out.println(parameters);
      User = (String)parameters.get("User");

      uploadtemppath = (String)parameters.get("tempdir");

      strFullFileName = uploadtemppath + 
        (String)parameters.get("filename");
      org.util.Debug.prtOut("strFullFileName=" + strFullFileName);
      uploadtemppath = (String)parameters.get("tempdir");

      if (uploadtemppath.equals(""))
        out.print("Error\n帐套数据上传及预处理失败");
      else {
        out.println("帐套数据上传并分析成功!<br>正在加载，请等待<br><br><br>");
      }
      int error = 0;

      out.println("预处理分析帐套文件<br/>");
      out.flush();

      conn = new DBConnect().getDirectConnect("");

      ExcelUploadService upload = null;
      try {
        upload = new ExcelUploadService(conn, strFullFileName);
      } catch (Exception e) {
        e.printStackTrace();
        out.println("临时路径或者客户编号设置有误,请与系统管理员联系<br>");
        error = 1;
      }

      try
      {
        Single.locked(lockmsg, us.getUserLoginId());
      } catch (Exception e) {
        out.println(e.getMessage() + "<br/>");
        error = 1;
      }

      if (error > 0) {
        out.println("装载活动遇到错误,已经中止!<br>请解决错误后重新装载");
      } else {
        org.util.Debug.prtOut("装载的临时目录为:" + uploadtemppath);
        out.println("继续处理装载<br>");
        out.flush();

        UserService ued = new UserService(conn);
        out.println("正在分析EXCEL文件......");
        out.flush();
        upload.init();
        out.println("分析EXCEL文件完毕!<BR>");

        out.println("正在装载用户内容!......");
        out.flush();

        ued.newTable();

        upload.setExcelNum("");
        upload.setExcelString("身份证号,学历,密码,办公电话,手机号码,工号,出生年月,毕业时间,入职时间,入党时间");
        String[] exexlKmye = { "身份证号", "姓名", "登录名", "所属部门" };
        String[] tableKmye = { "identityCard", "Name", "loginid", "departmentid" };
        String[] exexlPzmxOpt = { "出生年月", "学历", "毕业院校", "职级", 
          "岗位", "特长", "操作权限", "性别", "密码", "手机号码", "电子邮件", "工号", "民族", "毕业时间", "入职时间", "办公电话", 
          "婚姻状态", "籍贯", "户口所在地", "政治面貌", "入党时间", "组织关系所在单位", "专业", "英语能力", "CPA号", "合同类型" };
        String[] tablePzmxOpt = { "borndate", "educational", 
          "diploma", "rank", "post", "specialty", "userrole", "sex", "Password", "mobilePhone", "email", "station", "nation", "diplomatime", "entrytime", "phone", 
          "marriage", "place", "residence", "politics", "partytime", "relationships", "profession", "english", "cpano", "compact" };

        String[] exexlKmyeFixFields = { "DepartID" };
        String[] excelKmyeFixFieldValues = { "555555" };

        String result = "";

        result = upload.LoadFromExcel("人员列表", "tt_k_user", 
          exexlKmye, tableKmye, exexlPzmxOpt, tablePzmxOpt, 
          exexlKmyeFixFields, excelKmyeFixFieldValues);

        out.println("装载用户内容完毕!<BR>");

        out.flush();
        out.println("开始更新用户列表!......");
        out.flush();
        result = ued.CheckUpData2();
        ued.updateData2();
        ued.insertData2();
        out.println("更新用户列表完毕!<BR>");

        if ((result != null) && (result.length() > 0)) {
          out.println("<br><br>装载非正常结果报告：<BR><font color='red'>");
          out.println(result);
          out.println("</font><br>");
        }

        out.println("<hr>数据装载成功 <a href=\"user.do?method=List\">返回查询页面</a>\"</font>");
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
      out.println("<font style=\"color:red\">装载处理出现错误:<br/>" + e.getMessage());
      out.println("<a href=\"user.do?method=uploadUpdate\">返回装载页面</a>\"</font>");

      com.matech.framework.pub.util.Debug.print(4, "查询个人项目项目权限失败！", e);
      e.printStackTrace();
    } finally {
      try {
        Single.unlocked(lockmsg, us.getUserLoginId());
      } catch (Exception e) {
        out.println("撤销并发锁失败：" + e.getMessage() + "<br/>");
      }
      DbUtil.close(conn);
    }

    return null;
  }

  public ModelAndView SaveUpload(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    PrintWriter out = null;
    Connection conn = null;
    Single sl = new Single();
    UserSession us = (UserSession)request.getSession().getAttribute("userSession");
    String lockmsg = "装载帐套数据";
    try
    {
      response.setContentType("text/html;charset=utf-8");

      out = response.getWriter();

      Map parameters = null;

      String uploadtemppath = "";

      String strFullFileName = "";

      String User = "";

      String popedom = ".22.2205.2210.2214.221405.2215.2216.2217.221701.221702.221703.221704.221725.221730.221735.32.3205.3210.3215.3216.3225.42.4201.4203.4204.4206.4208.4212.4215.421505.421510.4226.4230.45.4505.4510.4515.4520.4525.4530.4535.52.5204.5205.520505.520510.520515.5210.5215.5217.5220.5225.5226.522605.522610.522615.5230.523005.523010.5232.451003.5236.5250.525005.525010.525025.525070.525080.62.6205.620535.620537.620540.620545.620560.620565.6210.6215.6220.622005.622010.622015.622020.6225.622505.622510.72.7205.720505.720530.7207.720705.720710.7209.720905.720910.7215.7220.722005.752001.92.";

      MyFileUpload myfileUpload = new MyFileUpload(request);
      uploadtemppath = myfileUpload.UploadFile(null, null);
      parameters = myfileUpload.getMap();
      System.out.println(parameters);
      User = (String)parameters.get("User");

      uploadtemppath = (String)parameters.get("tempdir");

      strFullFileName = uploadtemppath + 
        (String)parameters.get("filename");
      org.util.Debug.prtOut("strFullFileName=" + strFullFileName);
      uploadtemppath = (String)parameters.get("tempdir");

      if (uploadtemppath.equals(""))
        out.print("Error\n帐套数据上传及预处理失败");
      else {
        out.println("帐套数据上传并分析成功!<br>正在加载，请等待<br><br><br>");
      }
      int error = 0;

      out.println("预处理分析帐套文件<br/>");
      out.flush();

      conn = new DBConnect().getDirectConnect("");

      ExcelUploadService upload = null;
      try {
        upload = new ExcelUploadService(conn, strFullFileName);
      } catch (Exception e) {
        e.printStackTrace();
        out.println("临时路径或者客户编号设置有误,请与系统管理员联系<br>");
        error = 1;
      }

      try
      {
        Single.locked(lockmsg, us.getUserLoginId());
      } catch (Exception e) {
        out.println(e.getMessage() + "<br/>");
        error = 1;
      }

      if (error > 0) {
        out.println("装载活动遇到错误,已经中止!<br>请解决错误后重新装载");
      } else {
        org.util.Debug.prtOut("装载的临时目录为:" + uploadtemppath);
        out.println("继续处理装载<br>");
        out.flush();

        UserService ued = new UserService(conn);
        out.println("正在分析EXCEL文件......");
        out.flush();
        upload.init();
        out.println("分析EXCEL文件完毕!<BR>");

        out.println("正在装载用户内容!......");
        out.flush();

        ued.newTable();

        upload.setExcelNum("");
        upload.setExcelString("学历,密码,手机");
        String[] exexlKmye = { "姓名", "登录名", "部门" };
        String[] tableKmye = { "Name", "loginid", "departmentid" };
        String[] exexlPzmxOpt = { "出生年月", "学历", "毕业院校及专业", "职级", 
          "岗位", "特长", "角色", "性别", "密码", "手机", "邮箱", "身份证号", "工号", "民族", "毕业时间", "入职时间", "办公电话", 
          "婚姻状态", "籍贯", "户口所在地", "政治面貌", "入党时间", "组织关系所在单位", "专业", "英语能力", "CPA号", "合同类型" };
        String[] tablePzmxOpt = { "borndate", "educational", 
          "diploma", "rank", "post", "specialty", "userrole", "sex", "Password", "mobilePhone", "email", "identityCard", "station", "nation", "diplomatime", "entrytime", "phone", 
          "marriage", "place", "residence", "politics", "partytime", "relationships", "profession", "english", "cpano", "compact" };

        String[] exexlKmyeFixFields = { "DepartID" };
        String[] excelKmyeFixFieldValues = { "555555" };

        String result = "";

        result = upload.LoadFromExcel("人员列表", "tt_k_user", 
          exexlKmye, tableKmye, exexlPzmxOpt, tablePzmxOpt, 
          exexlKmyeFixFields, excelKmyeFixFieldValues);

        out.println("装载用户内容完毕!<BR>");

        out.flush();
        out.println("开始更新用户列表!......");
        out.flush();
        result = ued.CheckUpData();
        ued.updateData();
        ued.insertData();
        out.println("更新用户列表完毕!<BR>");

        if ((result != null) && (result.length() > 0)) {
          out.println("<br><br>装载非正常结果报告：<BR><font color='red'>");
          out.println(result);
          out.println("</font><br>");
        }

        out.println("<hr>数据装载成功 <a href=\"user.do?method=List\">返回查询页面</a>\"</font>");
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
      out.println("<font style=\"color:red\">装载处理出现错误:<br/>" + e.getMessage());
      out.println("<a href=\"user.do?method=Upload\">返回装载页面</a>\"</font>");

      com.matech.framework.pub.util.Debug.print(4, "查询个人项目项目权限失败！", e);
      e.printStackTrace();
    } finally {
      try {
        Single.unlocked(lockmsg, us.getUserLoginId());
      } catch (Exception e) {
        out.println("撤销并发锁失败：" + e.getMessage() + "<br/>");
      }
      DbUtil.close(conn);
    }

    return null;
  }

  public ModelAndView SaveUploadProject(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    PrintWriter out = null;
    Connection conn = null;
    Single sl = new Single();
    UserSession us = (UserSession)request.getSession().getAttribute("userSession");
    String lockmsg = "装载帐套数据";
    try
    {
      response.setContentType("text/html;charset=utf-8");

      out = response.getWriter();

      Map parameters = null;

      String uploadtemppath = "";

      String strFullFileName = "";

      String User = "";

      String popedom = ".22.2205.2210.2214.221405.2215.2216.2217.221701.221702.221703.221704.221725.221730.221735.32.3205.3210.3215.3216.3225.42.4201.4203.4204.4206.4208.4212.4215.421505.421510.4226.4230.45.4505.4510.4515.4520.4525.4530.4535.52.5204.5205.520505.520510.520515.5210.5215.5217.5220.5225.5226.522605.522610.522615.5230.523005.523010.5232.451003.5236.5250.525005.525010.525025.525070.525080.62.6205.620535.620537.620540.620545.620560.620565.6210.6215.6220.622005.622010.622015.622020.6225.622505.622510.72.7205.720505.720530.7207.720705.720710.7209.720905.720910.7215.7220.722005.752001.92.";

      MyFileUpload myfileUpload = new MyFileUpload(request);
      uploadtemppath = myfileUpload.UploadFile(null, null);
      parameters = myfileUpload.getMap();
      System.out.println(parameters);
      User = (String)parameters.get("User");

      uploadtemppath = (String)parameters.get("tempdir");

      strFullFileName = uploadtemppath + 
        (String)parameters.get("filename");
      org.util.Debug.prtOut("strFullFileName=" + strFullFileName);
      uploadtemppath = (String)parameters.get("tempdir");

      if (uploadtemppath.equals(""))
        out.print("Error\n帐套数据上传及预处理失败");
      else {
        out.println("帐套数据上传并分析成功!<br>正在加载，请等待<br><br><br>");
      }
      int error = 0;

      out.println("预处理分析帐套文件<br/>");
      out.flush();

      conn = new DBConnect().getDirectConnect("");

      ExcelUploadService upload = null;
      try {
        upload = new ExcelUploadService(conn, strFullFileName);
      } catch (Exception e) {
        e.printStackTrace();
        out.println("临时路径或者客户编号设置有误,请与系统管理员联系<br>");
        error = 1;
      }

      try
      {
        Single.locked(lockmsg, us.getUserLoginId());
      } catch (Exception e) {
        out.println(e.getMessage() + "<br/>");
        error = 1;
      }

      if (error > 0) {
        out.println("装载活动遇到错误,已经中止!<br>请解决错误后重新装载");
      } else {
        org.util.Debug.prtOut("装载的临时目录为:" + uploadtemppath);
        out.println("继续处理装载<br>");
        out.flush();

        UserService ued = new UserService(conn);
        out.println("正在分析EXCEL文件......");
        out.flush();
        upload.init();
        out.println("分析EXCEL文件完毕!<BR>");

        out.println("正在装载用户内容!......");
        out.flush();

        ued.newTable();

        upload.setExcelNum("");
        upload.setExcelString("项目编号,项目名称,委托人,审计对象,审计对象所属行业,审计工作类别,\t合同开始时间,\t合同结束时间,合同签约时间,服务时间完成时间,主要审计内容,服务范围,合同金额,投资规模/项目规模(万元),项目联系人,联系方式,合同编号,机构参与人数(高峰),(平均),合同履行情况,证明文件");

        String[] exexlKmye = { "项目编号", "项目名称", "委托人", "审计对象", "审计对象所属行业", "审计工作类别", "合同开始时间", "合同结束时间", 
          "合同签约时间", "服务时间", "合同金额", "项目联系人", "联系方式", "合同编号" };
        String[] tableKmye = { "projectId", "projectName", "principalName", "auditTarget", "auTaIndustry", "auditWorkClass", 
          "contractBegin", "contractEnd", "signTime", "serveTime", "contractMoney", "proUnitPrincipal", "contactInfo", "contractNu" };

        String[] exexlPzmxOpt = { "完成时间", "主要审计内容", "服务范围", "投资规模/项目规模(万元)", "机构参与人数(高峰)", "(平均)", "合同履行情况", "证明文件" };
        String[] tablePzmxOpt = { "finishTime", "auditContent", "serveRange", "investmentScale", "peopleBigNu", "peopleAvgNu", "performCondition", "certificate" };

        String[] exexlKmyeFixFields = { "property" };
        String[] excelKmyeFixFieldValues = { "33333" };

        String result = "";

        result = upload.LoadFromExcel("项目资历列表", "td_project", 
          exexlKmye, tableKmye, exexlPzmxOpt, tablePzmxOpt, exexlKmyeFixFields, excelKmyeFixFieldValues, true);

        out.println("装载用户内容完毕!<BR>");

        out.flush();
        out.println("开始更新用户列表!......");
        out.flush();
        result = ued.CheckUpData();
        ued.updateData();
        ued.insertData();
        out.println("更新用户列表完毕!<BR>");

        if ((result != null) && (result.length() > 0)) {
          out.println("<br><br>装载非正常结果报告：<BR><font color='red'>");
          out.println(result);
          out.println("</font><br>");
        }

        out.println("<hr>数据装载成功 <a href=\"formDefine.do?method=formListView&uuid=30eb6764-2e94-4507-b27e-c44cfd96e5b0\">返回查询页面</a>\"</font>");
      }

    }
    catch (Exception e)
    {
      e.printStackTrace();
      out.println("<font style=\"color:red\">装载处理出现错误:<br/>" + e.getMessage());
      out.println("<a href=\"user.do?method=UploadProject\">返回装载页面</a>\"</font>");

      com.matech.framework.pub.util.Debug.print(4, "查询失败！", e);
      e.printStackTrace();
    } finally {
      try {
        Single.unlocked(lockmsg, us.getUserLoginId());
      } catch (Exception e) {
        out.println("撤销并发锁失败：" + e.getMessage() + "<br/>");
      }
      DbUtil.close(conn);
    }

    return null;
  }

  public ModelAndView SaveUploadPeople(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    PrintWriter out = null;
    Connection conn = null;
    Single sl = new Single();
    UserSession us = (UserSession)request.getSession().getAttribute("userSession");
    String lockmsg = "装载帐套数据";
    try
    {
      response.setContentType("text/html;charset=utf-8");

      out = response.getWriter();

      Map parameters = null;

      String uploadtemppath = "";

      String strFullFileName = "";

      String User = "";

      String popedom = ".22.2205.2210.2214.221405.2215.2216.2217.221701.221702.221703.221704.221725.221730.221735.32.3205.3210.3215.3216.3225.42.4201.4203.4204.4206.4208.4212.4215.421505.421510.4226.4230.45.4505.4510.4515.4520.4525.4530.4535.52.5204.5205.520505.520510.520515.5210.5215.5217.5220.5225.5226.522605.522610.522615.5230.523005.523010.5232.451003.5236.5250.525005.525010.525025.525070.525080.62.6205.620535.620537.620540.620545.620560.620565.6210.6215.6220.622005.622010.622015.622020.6225.622505.622510.72.7205.720505.720530.7207.720705.720710.7209.720905.720910.7215.7220.722005.752001.92.";

      MyFileUpload myfileUpload = new MyFileUpload(request);
      uploadtemppath = myfileUpload.UploadFile(null, null);
      parameters = myfileUpload.getMap();
      System.out.println(parameters);
      User = (String)parameters.get("User");

      uploadtemppath = (String)parameters.get("tempdir");

      strFullFileName = uploadtemppath + 
        (String)parameters.get("filename");
      org.util.Debug.prtOut("strFullFileName=" + strFullFileName);
      uploadtemppath = (String)parameters.get("tempdir");

      if (uploadtemppath.equals(""))
        out.print("Error\n帐套数据上传及预处理失败");
      else {
        out.println("帐套数据上传并分析成功!<br>正在加载，请等待<br><br><br>");
      }
      int error = 0;

      out.println("预处理分析帐套文件<br/>");
      out.flush();

      conn = new DBConnect().getDirectConnect("");

      ExcelUploadService upload = null;
      try {
        upload = new ExcelUploadService(conn, strFullFileName);
      } catch (Exception e) {
        e.printStackTrace();
        out.println("临时路径或者客户编号设置有误,请与系统管理员联系<br>");
        error = 1;
      }

      try
      {
        Single.locked(lockmsg, us.getUserLoginId());
      } catch (Exception e) {
        out.println(e.getMessage() + "<br/>");
        error = 1;
      }

      if (error > 0) {
        out.println("装载活动遇到错误,已经中止!<br>请解决错误后重新装载");
      } else {
        org.util.Debug.prtOut("装载的临时目录为:" + uploadtemppath);
        out.println("继续处理装载<br>");
        out.flush();

        UserService ued = new UserService(conn);
        out.println("正在分析EXCEL文件......");
        out.flush();
        upload.init();
        out.println("分析EXCEL文件完毕!<BR>");

        out.println("正在装载用户内容!......");
        out.flush();

        ued.newTable();

        upload.setExcelNum("");
        upload.setExcelString("单位, 部门, 性别,年龄,文化程度,专业技术资格,职级,资格名称");
        String[] exexlKmye = { "单位", "部门", "性别", "年龄", "文化程度", "专业技术资格", "职级", "资格名称" };
        String[] tableKmye = { "unitName", "deptName", "sex", "age", "degreeEducation", "profTechQualifi", "rank", "qualificationName" };

        String[] exexlKmyeFixFields = { "property" };
        String[] excelKmyeFixFieldValues = { "33333" };

        String result = "";

        result = upload.LoadFromExcel("人员资历列表", "td_personal", 
          exexlKmye, tableKmye, exexlKmyeFixFields, excelKmyeFixFieldValues, true);

        out.println("装载用户内容完毕!<BR>");

        out.flush();
        out.println("开始更新用户列表!......");
        out.flush();
        result = ued.CheckUpData();
        ued.updateData();
        ued.insertData();
        out.println("更新用户列表完毕!<BR>");

        if ((result != null) && (result.length() > 0)) {
          out.println("<br><br>装载非正常结果报告：<BR><font color='red'>");
          out.println(result);
          out.println("</font><br>");
        }

        out.println("<hr>数据装载成功 <a href=\"formDefine.do?method=formListView&uuid=974c4dd6-f1e5-43fd-925c-58bb3e4920a5\">返回查询页面</a>\"</font>");
      }

    }
    catch (Exception e)
    {
      e.printStackTrace();
      out.println("<font style=\"color:red\">装载处理出现错误:<br/>" + e.getMessage());
      out.println("<a href=\"user.do?method=UploadPeople\">返回装载页面</a>\"</font>");

      com.matech.framework.pub.util.Debug.print(4, "查询失败！", e);
      e.printStackTrace();
    } finally {
      try {
        Single.unlocked(lockmsg, us.getUserLoginId());
      } catch (Exception e) {
        out.println("撤销并发锁失败：" + e.getMessage() + "<br/>");
      }
      DbUtil.close(conn);
    }

    return null;
  }

  public ModelAndView SaveUpload1(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    PrintWriter out = null;
    Connection conn = null;
    Single sl = new Single();
    UserSession us = (UserSession)request.getSession().getAttribute("userSession");
    String lockmsg = "装载帐套数据";
    try
    {
      response.setContentType("text/html;charset=utf-8");

      out = response.getWriter();

      Map parameters = null;

      String uploadtemppath = "";

      String strFullFileName = "";

      String User = "";

      String popedom = ".22.2205.2210.2214.221405.2215.2216.2217.221701.221702.221703.221704.221725.221730.221735.32.3205.3210.3215.3216.3225.42.4201.4203.4204.4206.4208.4212.4215.421505.421510.4226.4230.45.4505.4510.4515.4520.4525.4530.4535.52.5204.5205.520505.520510.520515.5210.5215.5217.5220.5225.5226.522605.522610.522615.5230.523005.523010.5232.451003.5236.5250.525005.525010.525025.525070.525080.62.6205.620535.620537.620540.620545.620560.620565.6210.6215.6220.622005.622010.622015.622020.6225.622505.622510.72.7205.720505.720530.7207.720705.720710.7209.720905.720910.7215.7220.722005.752001.92.";

      MyFileUpload myfileUpload = new MyFileUpload(request);
      uploadtemppath = myfileUpload.UploadFile(null, null);
      parameters = myfileUpload.getMap();
      System.out.println(parameters);
      User = (String)parameters.get("User");

      uploadtemppath = (String)parameters.get("tempdir");

      strFullFileName = uploadtemppath + 
        (String)parameters.get("filename");
      org.util.Debug.prtOut("strFullFileName=" + strFullFileName);
      uploadtemppath = (String)parameters.get("tempdir");

      if (uploadtemppath.equals(""))
        out.print("Error\n帐套数据上传及预处理失败");
      else {
        out.println("帐套数据上传并分析成功!<br>正在加载，请等待<br><br><br>");
      }
      int error = 0;

      out.println("预处理分析帐套文件<br/>");
      out.flush();

      conn = new DBConnect().getDirectConnect("");

      ExcelUploadService upload = null;
      try {
        upload = new ExcelUploadService(conn, strFullFileName);
      } catch (Exception e) {
        e.printStackTrace();
        out.println("临时路径或者客户编号设置有误,请与系统管理员联系<br>");
        error = 1;
      }

      try
      {
        Single.locked(lockmsg, us.getUserLoginId());
      } catch (Exception e) {
        out.println(e.getMessage() + "<br/>");
        error = 1;
      }

      if (error > 0) {
        out.println("装载活动遇到错误,已经中止!<br>请解决错误后重新装载");
      } else {
        org.util.Debug.prtOut("装载的临时目录为:" + uploadtemppath);
        out.println("继续处理装载<br>");
        out.flush();

        UserService ued = new UserService(conn);
        out.println("正在分析EXCEL文件......");
        out.flush();
        upload.init();
        out.println("分析EXCEL文件完毕!<BR>");

        out.println("正在装载用户内容!......");
        out.flush();

        ued.newTable();

        upload.setExcelNum("");
        upload.setExcelString("报刊编号, 报刊名称, 价格");
        String[] exexlKmye = { "报刊编号", "报刊名称", "价格" };
        String[] tableKmye = { "periodicalId", "periodicalName", "pricePreMonth" };

        String[] exexlKmyeFixFields = { "number" };
        String[] excelKmyeFixFieldValues = { "1000" };

        String result = "";

        result = upload.LoadFromExcel("报刊订阅列表", "sm_periodicals", 
          exexlKmye, tableKmye, exexlKmyeFixFields, excelKmyeFixFieldValues, true);

        out.println("装载用户内容完毕!<BR>");

        out.flush();
        out.println("开始更新用户列表!......");
        out.flush();
        result = ued.CheckUpData();
        ued.updateData();
        ued.insertData();
        out.println("更新用户列表完毕!<BR>");

        if ((result != null) && (result.length() > 0)) {
          out.println("<br><br>装载非正常结果报告：<BR><font color='red'>");
          out.println(result);
          out.println("</font><br>");
        }

        out.println("<hr>数据装载成功 <a href=\"formDefine.do?method=formListView&uuid=08454169-4073-4971-8bad-c432e907f683\">返回查询页面</a>\"</font>");
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
      out.println("<font style=\"color:red\">装载处理出现错误:<br/>" + e.getMessage());
      out.println("<a href=\"user.do?method=Upload\">返回装载页面</a>\"</font>");

      com.matech.framework.pub.util.Debug.print(4, "查询失败！", e);
      e.printStackTrace();
    } finally {
      try {
        Single.unlocked(lockmsg, us.getUserLoginId());
      } catch (Exception e) {
        out.println("撤销并发锁失败：" + e.getMessage() + "<br/>");
      }
      DbUtil.close(conn);
    }

    return null;
  }

  public ModelAndView ListEm(HttpServletRequest request, HttpServletResponse response) throws Exception
  {
    HashMap mapResult = new HashMap();
    ASFuntion CHF = new ASFuntion();

    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    String tap = CHF.showNull(request.getParameter("tap"));
    DbUtil dbUtil = null;
    try {
      String temp = UTILSysProperty.SysProperty.getProperty("clientDog");
      conn = new DBConnect().getConnect("");
      dbUtil = new DbUtil(conn);
      String revert = request.getParameter("revert");

      String departName = null;
      String judge = CHF.showNull(request.getParameter("judge"));
      mapResult.put("judge", judge);

      String state = " and state=0 ";

      String tabOpt = "0";

      DataGridProperty pp = new DataGridProperty();
      pp.setTableID("user");
      pp.setCustomerId("");
      pp.setPageSize_CH(50);
      pp.setWhichFieldIsValue(1);

      pp.setPrintEnable(true);

      pp.setInputType("radio");

      UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
      String menuid = CHF.showNull(request.getParameter("menuid"));
      String departments = new UserPopedomService(conn).getUserPopedom(userSession.getUserId(), "user");

      String strSql = ""; String sql = "";
      String departmentid = request.getParameter("department_id");
      String emtype = request.getParameter("emtype");
      String userid = userSession.getUserId();
      UserService us = new UserService(conn);

      String logSql = " \tLEFT JOIN ( \n\tSELECT b.udate,b.utime,b.loginid,DATEDIFF(CURDATE(),b.udate) AS loginDay FROM (\t \n\tSELECT loginid,MAX(ABS(id))  AS logid \n\tFROM t_log WHERE cmdName ='用户登录'   \n\tGROUP BY loginid  ORDER BY ABS(id) \n\t) a  \n\tleft JOIN t_log b ON a.logid = b.id \n) d ON a.loginid = d.loginid";

      String goMsgAhref = " '<img src=" + request.getContextPath() + "/img/goMsg.png onclick=goMsg(',a.id,') alt=手机短信提醒登录>'";
      if ((departmentid != null) && (!"".equals(departmentid)))
      {
        strSql = "select DISTINCT ID,Name,a.loginid,if(password=md5(1),'未修改缺省密码','已修改') as pwd,case when Sex='M' or Sex='男' then '男' else '女' end Sex,\n\tEducational,DepartName,Post ,istips,c.roles,a.rank,a.departmentid, \n   a.BornDate,a.Diploma,a.Specialty,a.mobilePhone,a.email,a.identityCard, \na.station,a.nation,a.diplomatime,a.entrytime,a.phone, \n CASE  WHEN loginDay =0 THEN CONCAT(CONVERT('今天登录时间：',CHAR),utime) WHEN loginDay>0 THEN CONCAT(loginDay,'天闲置'," + 
          goMsgAhref + ") ELSE  concat('未曾登录'," + goMsgAhref + ") END  AS loginInfo, \n" + 
          " CASE  WHEN CONVERT(loginDay,char) ='0' THEN CONCAT('今天登录时间：',CONVERT(utime,char)) WHEN loginDay >0 THEN CONCAT(CONVERT(loginDay,char),'天闲置') ELSE  concat('未曾登录') END  AS printLoginInfo \n" + 
          "\tfrom k_User a  \n" + 
          "\tinner join (  \n" + 
          "\t\tselect a.*  \n" + 
          "\t\tfrom k_department a" + 
          "\t) b  on a.departmentid = b.autoID  \n" + 
          "\tleft join (select userid,group_concat(distinct rolename) as roles from k_userrole a,k_role b where a.rid=b.id group by userid)c on a.id=c.userid \n" + 
          logSql + 
          "\twhere 1=1 and a.emtype=" + emtype + " (a.departmentid='" + departmentid + "' or a.departmentid in (" + departments + "))  ";
      }
      else {
        strSql = "select ID,Name,a.loginid,if(password='c4ca4238a0b923820dcc509a6f75849b','未修改缺省密码','已修改') as pwd, case when Sex='M' or Sex='男' then '男' else '女' end Sex, Educational,DepartName,Post ,istips,c.roles,a.rank,a.departmentid, \n a.BornDate,a.Diploma,a.Specialty,a.mobilePhone,a.email,a.identityCard,\n  a.station,a.nation,a.diplomatime,a.entrytime,a.phone, CASE  WHEN loginDay =0 THEN CONCAT(CONVERT('今天登录时间：',CHAR),utime) WHEN loginDay>0 THEN CONCAT(loginDay,'天闲置'," + 
          goMsgAhref + ") ELSE  concat('未曾登录'," + goMsgAhref + ") END  AS loginInfo, \n" + 
          " CASE  WHEN CONVERT(loginDay,char) ='0' THEN CONCAT('今天登录时间：',CONVERT(utime,char)) WHEN loginDay >0 THEN CONCAT(CONVERT(loginDay,char),'天闲置') ELSE  concat('未曾登录') END  AS printLoginInfo \n" + 
          " from asdb.k_User a left join asdb.k_department b  on a.departmentid = b.autoID \n " + 
          logSql + 
          " left join (select userid,group_concat(distinct rolename) as roles from k_userrole a,k_role b where a.rid=b.id group by userid)c on a.id=c.userid \n" + 
          " where 1=1 ";
      }

      pp.setOrderBy_CH("DepartName,istips,loginid");
      pp.setDirection("asc,asc,asc");
      pp.setColumnWidth("10,10,5,10,10,10,13");
      pp.setUseBufferGrid(false);
      pp.setPrintColumnWidth("30,20,10,20,35,25,35,40,35,20,30,30,30,30,30,30,30,30,30,30");
      pp.setPrintSqlColumn("Name,loginid,Sex,Educational,DepartName,Post,rank,roles,printLoginInfo,BornDate,Diploma,Specialty,mobilePhone,email,identityCard,station,nation,diplomatime,entrytime,phone");
      pp.setPrintColumn("姓名`登录名`性别`学历`所属部门`岗位`职级`操作权限`闲置时间`出生年月`毕业院校`特长`手机号码`电子邮件`身份证号`工号`民族`毕业时间`入职时间`办公电话");
      pp.setPrintCharColumn("10`13`15`16`18`19`20");

      pp.addColumn("姓名", "Name");
      pp.addColumn("登录名", "loginid");
      pp.addColumn("性别", "Sex", "showCenter");

      pp.addColumn("所属部门", "DepartName");

      pp.addColumn("薪酬级别", "rank");
      pp.addColumn("权限", "roles");
      pp.addColumn("闲置时间", "loginInfo");
      pp.addColumn("生日", "BornDate", "hide");
      pp.addColumn("毕业院校", "Diploma", "hide");
      pp.addColumn("专长", "Specialty", "hide");
      pp.addColumn("手机号码", "mobilePhone", "hide");
      pp.addColumn("电子邮件", "email", "hide");
      pp.addColumn("证件号码", "identityCard", "hide");
      pp.addColumn("工号", "station", "hide");
      pp.addColumn("民族", "nation", "hide");
      pp.addColumn("毕业时间", "diplomatime", "hide");
      pp.addColumn("入职时间", "entrytime", "hide");
      pp.addColumn("办公电话", "phone", "hide");
      if ("admin".equals(userSession.getUserLoginId())) {
        pp.addColumn("密码状态", "pwd");

        if ("1".equals(temp)) {
          String printSql = "";
          if ((departmentid != null) && (!"".equals(departmentid)))
          {
            strSql = "select DISTINCT ID,Name,loginid,if(password='c4ca4238a0b923820dcc509a6f75849b','未修改缺省密码','已修改') as pwd, \n case when Sex='M' or Sex='男' then '男' else '女' end Sex,Educational,DepartName,Post, clientDogSysUi, \n if(clientDogSysUi='','<input type=\"button\"  value=\"绑定狗\" style=\"width: 100px;\" class=\"flyBT\"  onclick=\"bindDog(this);\"  title=\"绑定狗信息\" />', \n '<input type=\"button\"  value=\"解除狗绑定\" style=\"width: 100px;\" class=\"flyBT\"  onclick=\"bindDog(this);\" title=\"解除狗绑定\" />' ) aButton,a.rank,c.roles,istips,a.departmentid \n\tfrom k_User a  \n\tinner join (  \n\t\tselect a.*  \n\t\tfrom k_department a\t) b  on a.departmentid = b.autoID  \n\tleft join (select userid,group_concat(distinct rolename) as roles from k_userrole a,k_role b where a.rid=b.id group by userid)c on a.id=c.userid \n\twhere 1=1  and (a.departmentid='" + 
              departmentid + "' or a.departmentid in (" + departments + "))   \n";
          }
          else
          {
            strSql = "select ID,Name,loginid,if(password='c4ca4238a0b923820dcc509a6f75849b','未修改缺省密码','已修改') as pwd, case when Sex='M' or Sex='男' then '男' else '女' end Sex,Educational,DepartName,Post, clientDogSysUi, if(clientDogSysUi='','<input type=\"button\"  value=\"绑定狗\" style=\"width: 100px;\" class=\"flyBT\"  onclick=\"bindDog(this);\"  title=\"绑定狗信息\" />',  '<input type=\"button\"  value=\"解除狗绑定\" style=\"width: 100px;\" class=\"flyBT\"  onclick=\"bindDog(this);\" title=\"解除狗绑定\" />' ) aButton,a.rank,c.roles,istips,a.departmentid  from asdb.k_User a left join asdb.k_department b  on a.departmentid = b.autoID  left join (select userid,group_concat(distinct rolename) as roles from k_userrole a,k_role b where a.rid=b.id group by userid)c on a.id=c.userid \n where 1=1  ";

            printSql = "select ID,Name,loginid,case when Sex='M' or Sex='男' then '男' else '女' end Sex,Educational,DepartName,Post,if(password='c4ca4238a0b923820dcc509a6f75849b','未修改缺省密码','已修改') as pwd, clientDogSysUi from asdb.k_User a left join asdb.k_department b  on a.departmentid = b.autoID where 1=1  " + 
              state;
          }
          pp.addColumn("狗信息", "clientDogSysUi");
          pp.addColumn("绑定操作", "aButton", "showCenter");
        }

      }

      sql = sql + strSql + state;
      String subSearchID = CHF.showNull(request.getParameter("loginid"));
      String subSearchName = CHF.showNull(request.getParameter("name"));
      String subSearchDepId = CHF.showNull(request.getParameter("department"));
      String subSearchRole = CHF.showNull(request.getParameter("role"));
      String subSearchRank = CHF.showNull(request.getParameter("rank"));

      String opt = CHF.showNull(request.getParameter("opt"));

      if (!"save".equals(opt)) {
        if (!subSearchID.equals(""))
          sql = sql + " and a.loginId like '%" + subSearchID + "%'";
        if (!subSearchName.equals(""))
          sql = sql + " and a.name like '%" + subSearchName + "%'";
        if (!subSearchDepId.equals(""))
          sql = sql + " and a.departmentid ='" + subSearchDepId + "'";
        if (!subSearchRole.equals(""))
          sql = sql + " and c.roles like '%" + subSearchRole + "%' ";
        if (!subSearchRank.equals(""))
          sql = sql + " and  a.rank like '%" + subSearchRank + "%'";
      }
      pp.setSQL(sql);

      mapResult.put("revert", revert);
      mapResult.put("temp", temp);
      request.getSession().setAttribute("DGProperty_" + pp.getTableID(), pp);

      state = " and state=1 ";

      DataGridProperty pp2 = new DataGridProperty();
      pp2.setTableID("user2");
      pp2.setCustomerId("");
      pp2.setPageSize_CH(50);
      pp2.setWhichFieldIsValue(1);
      pp2.setUseBufferGrid(false);
      pp2.setPrintEnable(true);
      pp2.setPrintTitle("禁用人员列表");

      pp2.setInputType("radio");
      pp2.setTrAction("style='cursor:hand;' onclick='goSetUsrID();'");
      String sql2 = strSql + state;

      pp2.setOrderBy_CH("DepartName,istips,a.loginid");
      pp2.setDirection("asc,asc,asc");

      pp2.addColumn("编号", "id");
      pp2.addColumn("姓名", "Name");
      pp2.addColumn("登录名", "loginid");
      pp2.addColumn("性别", "Sex", "showCenter");
      pp2.addColumn("学历", "Educational");
      pp2.addColumn("所属部门", "DepartName");
      pp2.addColumn("岗位", "Post");
      pp2.addColumn("密码状态", "pwd");

      String subSearchID2 = CHF.showNull(request.getParameter("loginid2"));
      String subSearchName2 = CHF.showNull(request.getParameter("name2"));
      String subSearchDepId2 = CHF.showNull(request.getParameter("department2"));
      String subSearchRole2 = CHF.showNull(request.getParameter("role2"));
      String subSearchRank2 = CHF.showNull(request.getParameter("rank2"));

      if (!subSearchID2.equals(""))
        sql2 = sql2 + " and a.loginid like '%" + subSearchID2 + "%'";
      if (!subSearchName2.equals(""))
        sql2 = sql2 + " and a.name like '%" + subSearchName2 + "%'";
      if (!subSearchDepId2.equals(""))
        sql2 = sql2 + " and a.departmentid ='" + subSearchDepId2 + "'";
      if (!subSearchRole2.equals(""))
        sql2 = sql2 + " and c.roles like '%" + subSearchRole2 + "%' ";
      if (!subSearchRank2.equals("")) {
        sql2 = sql2 + " and  a.rank like '%" + subSearchRank2 + "%'";
      }
      pp2.setSQL(sql2);

      request.getSession().setAttribute("DGProperty_" + pp2.getTableID(), pp2);

      if ((!subSearchID2.equals("")) || (!subSearchName2.equals(""))) {
        tabOpt = "1";
      }
      mapResult.put("tabOpt", tabOpt);

      String svalue = "";

      sql = "select svalue from s_config where sname='新增人员是否审批'";

      ps = conn.prepareStatement(sql);

      rs = ps.executeQuery();
      while (rs.next()) {
        svalue = rs.getString(1);
      }

      request.setAttribute("svalue", svalue);

      DataGridProperty pp3 = new DataGridProperty();
      pp3.setTableID("partTimeList");
      pp3.setCustomerId("");
      pp3.setPageSize_CH(50);
      pp3.setWhichFieldIsValue(1);
      pp3.setInputType("radio");
      pp3.setOrderBy_CH("loginid");
      pp3.setDirection("desc");
      pp3.setPrintEnable(true);
      pp3.setPrintTitle("外聘人员列表");

      String sql3 = "select * from k_parttime where 1=1 ${partTimeName} ${partTimDdepartment}";

      pp3.setSQL(sql3);

      pp3.addColumn("姓名", "Name");
      pp3.addColumn("登录名", "loginid", "hide");
      pp3.addColumn("性别", "Sex", "showCenter");

      pp3.addColumn("所属部门", "DepartName");

      pp3.addColumn("薪酬级别", "rank");
      pp3.addColumn("权限", "roles");
      pp3.addColumn("闲置时间", "loginInfo", "hide");
      pp3.addColumn("生日", "BornDate", "hide");
      pp3.addColumn("毕业院校", "Diploma", "hide");
      pp3.addColumn("专长", "Specialty", "hide");
      pp3.addColumn("手机号码", "mobilePhone", "hide");
      pp3.addColumn("电子邮件", "email", "hide");
      pp3.addColumn("证件号码", "identityCard", "hide");
      pp3.addColumn("工号", "station", "hide");
      pp3.addColumn("民族", "nation", "hide");
      pp3.addColumn("毕业时间", "diplomatime", "hide");
      pp3.addColumn("入职时间", "entrytime", "hide");
      pp3.addColumn("办公电话", "phone", "hide");

      pp3.addSqlWhere("partTimeName", " and name = '${partTimeName}'");
      pp3.addSqlWhere("partTimDdepartment", " and departmentid ='${partTimDdepartment}'");

      request.getSession().setAttribute("DGProperty_" + pp3.getTableID(), pp3);
      List queryVOs = dbUtil.select(QueryVO.class, 
        "select t1.tablename as table_name,t1.name as table_name_cn,t2.* from mt_com_form t1 left join mt_com_form_query   t2 on t2.formid=t1.uuid  where t1.form_type=?", new Object[] { 
        "c4cf9647-e925-4f02-9656-f4d3ab3c75b7" });

      request.setAttribute("jarrEmTable", JSONArray.fromObject(queryVOs).toString());
    }
    catch (Exception e) {
      com.matech.framework.pub.util.Debug.print(4, "查询用户列表失败！", e);
      e.printStackTrace();
      throw e;
    } finally {
      DbUtil.close(rs);
      DbUtil.close(ps);
      DbUtil.close(conn);
    }

    String examineOrnot = CHF.showNull(request.getParameter("examineOrnot"));
    mapResult.put("tap", tap);
    if (("".equals(examineOrnot)) || (examineOrnot == null))
    {
      return new ModelAndView(Jsp.ListEm.getPath(), mapResult);
    }
    return new ModelAndView("/user/ExamineList.jsp", mapResult);
  }

  public ModelAndView Print(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    Connection conn = null;
    HashMap mapResult = new HashMap();
    try {
      String temp = UTILSysProperty.SysProperty.getProperty("clientDog");
      conn = new DBConnect().getConnect("");
      String tableid = request.getParameter("tableid");

      DataGridProperty pp = (DataGridProperty)request.getSession().getAttribute("DGProperty_" + tableid);

      PrintSetup printSetup = new PrintSetup(conn);

      printSetup.setStrTitles(new String[] { "人员列表" });

      printSetup.setStrQuerySqls(new String[] { pp.getFinishSQL() });
      printSetup.setStrChineseTitles(new String[] { "编号`姓名`登录名`性别`学历`所属部门`岗位" });
      printSetup.setCharColumn(new String[] { "1`2`3`4`5`6`7" });

      if ("1".equals(temp)) {
        printSetup.setStrQuerySqls(new String[] { "select ID,Name,loginid,Sex,Educational,DepartName,Post, clientDogSysUi from (" + pp.getFinishSQL() + ") a " });
        printSetup.setStrChineseTitles(new String[] { "编号`姓名`登录名`性别`学历`所属部门`岗位`狗信息" });
        printSetup.setCharColumn(new String[] { "1`2`3`4`5`6`7`8" });
      }
      printSetup.setIColumnWidths(new int[] { 11, 11, 11, 8, 15, 35, 21 });

      String filename = printSetup.getExcelFile();

      mapResult.put("refresh", "");

      mapResult.put("saveasfilename", "人员列表");
      mapResult.put("bVpage", "false");
      mapResult.put("strPrintTitleRows", "$2:$4");
      mapResult.put("filename", filename);
    }
    catch (Exception e) {
      com.matech.framework.pub.util.Debug.print(4, "查询个人项目项目权限失败！", e);
      e.printStackTrace();
      throw e;
    } finally {
      DbUtil.close(conn);
    }

    return new ModelAndView("/Excel/tempdata/PrintandSave.jsp", mapResult);
  }

  public ModelAndView mutiDeleteList(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    ASFuntion CHF = new ASFuntion();
    String menuid = CHF.showNull(request.getParameter("menuid"));
    Connection conn = null;
    try {
      conn = new DBConnect().getConnect("");
      UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
      String role = userSession.getUserName();
      String departmentId = userSession.getUserAuditDepartmentId();
      String addSql = "";
      UserPopedomService userPopedomService = new UserPopedomService(conn);
      String departmentIds = userPopedomService.getUserPopedom(userSession.getUserId(), "user");
      if (!role.equals("系统管理员")) {
        addSql = " and (a.departmentid =" + departmentId + " or a.departmentid in (" + departmentIds + ")) ";
      }
      DataGridProperty pp = new DataGridProperty();
      String state = " and state=0 ";
      pp.setTableID("serMutiDelete");

      pp.setInputType("checkbox");
      pp.setCustomerId("");
      pp.setPageSize_CH(25);
      pp.setWhichFieldIsValue(1);
      pp.setPrintEnable(true);
      pp.setPrintTitle("人员列表");

      String sql = "select * from ( select ID,Name,round(loginid) loginid,if(password='c4ca4238a0b923820dcc509a6f75849b','未修改缺省密码','已修改') as pwd,if(Sex='M' or Sex='男','男','女') Sex,Educational,DepartName,Post,a.departmentid from asdb.k_User a  left join asdb.k_department b  on a.departmentid = b.autoID  where 1=1 and (b.url ='' or b.url is null)" + 
        state + "  and loginid >=1 and loginid <= 999999999 " + 
        addSql + 
        " union " + 
        " select ID,Name,loginid,if(password='c4ca4238a0b923820dcc509a6f75849b','未修改缺省密码','已修改') as pwd,if(Sex='M' or Sex='男','男','女') Sex," + 
        " Educational,DepartName,Post,a.departmentid from asdb.k_User a left join asdb.k_department b  on a.departmentid = b.autoID where 1=1 and (b.url ='' or b.url is null) " + state + " and  id!=19 and loginid >='a' and loginid <= 'zzzzzzzzz'" + addSql + 
        " ) a where 1=1  ${loginid} ${name} ${department}";
      pp.setOrderBy_CH("id");
      pp.setDirection("asc");
      pp.setColumnWidth("8,8,6,6,10,8,8");

      pp.addColumn("姓名", "Name");
      pp.addColumn("登录名", "loginid");
      pp.addColumn("性别", "Sex", "showCenter");
      pp.addColumn("学历", "Educational");
      pp.addColumn("所属部门", "DepartName");
      pp.addColumn("岗位", "Post");
      pp.addColumn("密码状态", "pwd");

      pp.addSqlWhere("loginid", " and loginid like '%${loginid}%'");
      pp.addSqlWhere("name", " and name like '%${name}%'");
      pp.addSqlWhere("department", " and a.departmentid = '${department}'");

      pp.setSQL(sql);

      request.getSession().setAttribute(
        "DGProperty_" + pp.getTableID(), pp);
    }
    catch (Exception e) {
      com.matech.framework.pub.util.Debug.print(4, "查询人员信息失败！", e);
      e.printStackTrace();
      throw e;
    }

    return new ModelAndView("/user/mutiDeleteList.jsp");
  }

  public ModelAndView mutiDeleteList1(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    ModelAndView modelAndView = new ModelAndView("/user/mutiDeleteList1.jsp");
    ASFuntion CHF = new ASFuntion();
    Connection conn = null;
    try {
      conn = new DBConnect().getConnect("");
      UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
      DepartmentService departService = new DepartmentService(conn);
      String departmentid = userSession.getUserAuditDepartmentId();
      StringBuffer sb = new StringBuffer("");
      if ((departmentid != null) && (!"".equals(departmentid))) {
        String projectPopedom = departService.getProjectPopedom(departmentid);
        if (!projectPopedom.equals("")) {
          StringTokenizer st = new StringTokenizer(projectPopedom, ",");
          sb.append(" and departmentid in (");
          while (st.hasMoreTokens()) {
            sb.append(st.nextToken() + ",");
          }
          sb.append(departmentid + ")");
        } else {
          sb.append(" and  departmentid = '" + departmentid + "' ");
        }
      } else {
        sb.append("");
      }

      DataGridProperty pp = new DataGridProperty();
      String state = "";
      String stateflag = request.getParameter("flag");
      if ((stateflag != null) && (stateflag.equals("1")))
        state = " and state=0 ";
      else {
        state = " and state=1 ";
      }
      pp.setTableID("serMutiDelete1");

      pp.setInputType("checkbox");
      pp.setCustomerId("");
      pp.setPageSize_CH(50);
      pp.setWhichFieldIsValue(1);

      String sql = "select ID,Name,round(loginid) loginid,if(password='c4ca4238a0b923820dcc509a6f75849b','未修改缺省密码','已修改') as pwd,if(Sex='M' or Sex='男','男','女') Sex,Educational,DepartName,Post  from asdb.k_User a left join asdb.k_department b  on a.departmentid = b.autoID  where 1=1 and (b.url ='' or b.url is null)" + 
        state + sb.toString() + " AND loginid>0" + 
        " union " + 
        " select ID,Name,loginid,if(password='c4ca4238a0b923820dcc509a6f75849b','未修改缺省密码','已修改') as pwd," + 
        "if(Sex='M' or Sex='男','男','女')" + 
        " Sex,Educational,DepartName,Post " + 
        " from asdb.k_User a left join asdb.k_department b  on a.departmentid = b.autoID " + 
        " where 1=1 and (b.url ='' or b.url is null) " + state + sb.toString();

      System.out.println(sql);
      pp.setOrderBy_CH("id");
      pp.setDirection("asc");
      pp.setUseBufferGrid(false);

      pp.addColumn("姓名", "Name");
      pp.addColumn("登录名", "loginid");
      pp.addColumn("性别", "Sex", "showCenter");
      pp.addColumn("学历", "Educational");
      pp.addColumn("所属部门", "DepartName");
      pp.addColumn("岗位", "Post");
      pp.addColumn("密码状态", "pwd");

      String subSearchID = CHF.showNull(request.getParameter("loginid"));
      String subSearchName = CHF.showNull(request.getParameter("name"));
      String subSearchDepId = CHF.showNull(request.getParameter("department"));

      if (!subSearchID.equals(""))
        sql = sql + " and loginid like '%" + subSearchID + "%'";
      if (!subSearchName.equals(""))
        sql = sql + " and name like '%" + subSearchName + "%'";
      if (!subSearchDepId.equals(""))
        sql = sql + " and departmentid ='" + subSearchDepId + "'";
      System.out.println(sql);
      pp.setSQL(sql);

      request.getSession().setAttribute(
        "DGProperty_" + pp.getTableID(), pp);
    }
    catch (Exception e) {
      com.matech.framework.pub.util.Debug.print(4, "查询人员信息失败！", e);
      e.printStackTrace();
      throw e;
    } finally {
      DbUtil.close(conn);
    }

    return modelAndView;
  }

  public ModelAndView xlsPrint(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    HashMap mapResult = new HashMap();
    try
    {
      DataGridProperty pp = new DataGridProperty();
      pp.setTableID("xlsPrint");
      pp.setCancelPage(true);
      pp.setInputType("checkbox");
      pp.setCustomerId("");
      pp.setPageSize_CH(50);
      pp.setWhichFieldIsValue(1);
      String sql = "select ID,Name,loginid,c.DepartName,Post,''isprint  from k_user a left join k_customer b on a.departid=b.departid left join k_department c on a.departmentid=c.autoid where 1=1 ";
      pp.setUseBufferGrid(false);
      pp.setOrderBy_CH("ID");
      pp.setDirection("asc");

      pp.addColumn("姓名", "Name");
      pp.addColumn("登录名", "loginid");
      pp.addColumn("所属部门", "DepartName");
      pp.addColumn("岗位", "Post");

      pp.setSQL(sql);

      request.getSession().setAttribute("DGProperty_" + pp.getTableID(), pp);
    }
    catch (Exception e) {
      com.matech.framework.pub.util.Debug.print(4, "查询个人项目底稿分工失败！", e);
      e.printStackTrace();
      throw e;
    }

    return new ModelAndView("/user/Prints.jsp", mapResult);
  }

  public ModelAndView test(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    HashMap mapResult = new HashMap();
    ModelAndView modelAndView = new ModelAndView("/Excel/tempdata/PrintandSave.jsp");
    try {
      ASFuntion CHF = new ASFuntion();
      MultiDbIF db = (MultiDbIF)UTILSysProperty.context.getBean("MultiDbAction");
      String id = request.getParameter("id");
      System.out.println("jt:" + id);
      String[] ids = id.split(",");
      ArrayList filename = new ArrayList();
      conn = new DBConnect().getConnect("");
      for (int j = 0; j < ids.length; j++)
      {
        String sql = "select name,loginid," + db.mIf("Sex='M' or Sex='男'", "'男'", "'女'") + " sex,borndate,educational,diploma,b.departname bdname,c.departname cdname,rank,post,specialty from k_user a left join k_customer b on a.departid=b.departid left join k_department c on a.departmentid=c.autoid where 1=1 and id = '" + ids[j] + "'";

        HashMap VMap = new HashMap();
        ps = conn.prepareStatement(sql);
        rs = ps.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        if (rs.next()) {
          for (int i = 0; i < columnCount; i++) {
            VMap.put(rsmd.getColumnLabel(i + 1), CHF.showNull(rs.getString(i + 1)));
          }

        }

        PrintSetup printSetup = new PrintSetup(conn);

        printSetup.setVarMap(VMap);
        printSetup.setStrQuerySqls(new String[] { "select Name,Value from k_UserDef where ContrastID='" + ids[j] + "' and Property ='user' order by id asc" });
        printSetup.setStrExcelTemplateFileName("用户资料.xls");

        printSetup.setStrSheetName(new DbUtil(conn).queryForString("select name from k_user where id='" + ids[j] + "'"));
        String excelName = printSetup.getExcelFile();
        filename.add(excelName);
      }

      modelAndView.addObject("filenameList", filename);
    }
    catch (Exception e) {
      com.matech.framework.pub.util.Debug.print(4, "查询个人项目项目权限失败！", e);
      e.printStackTrace();
      throw e;
    } finally {
      DbUtil.close(rs);
      DbUtil.close(ps);
      DbUtil.close(conn);
    }

    return modelAndView;
  }

  public ModelAndView CheckName(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    Connection conn = null;
    try {
      conn = new DBConnect().getConnect("");

      String id = request.getParameter("id");

      boolean temp = false;

      PrintWriter out = response.getWriter();
      UserService user = new UserService(conn);

      temp = user.SelectName(id);

      if (!temp)
        out.print("no");
      else {
        out.print("yes");
      }
      out.close();
    } catch (Exception e) {
      com.matech.framework.pub.util.Debug.print(4, "读取自定义信息失败！", e);
      e.printStackTrace();
      throw e;
    } finally {
      DbUtil.close(conn);
    }
    return null;
  }

  public void uploadPhoto(HttpServletRequest request, HttpServletResponse response)
  {
    PrintWriter out = null;
    Connection conn = null;
    try {
      conn = new DBConnect().getConnect("");
      response.setContentType("text/html;charset=UTF-8");
      out = response.getWriter();
      MyFileUpload myfileUpload = new MyFileUpload(request);

      Foder foder = new Foder("", request);
      String path = foder.createFoder("temp");

      String uploadtemppath = myfileUpload.UploadFile(null, path);

      Map parameters = myfileUpload.getMap();

      String fileName = (String)parameters.get("filename");
      String fileTempName = (String)parameters.get("fileTempName");
      String userid = (String)parameters.get("userid");
      fileTempName = fileTempName + fileName.substring(fileName.indexOf("."), fileName.length());
      fileTempName = userid + fileName.substring(fileName.indexOf("."), fileName.length());

      if ((fileName != null) && (!"".equals(fileName)))
      {
        String oldfile = uploadtemppath + fileName;
        String newfile = "";
        String newPath = foder.createFoder("userPhoto");
        newfile = newPath + fileTempName;
        ManuFileService mfs = new ManuFileService(conn);
        File file = new File(newfile);

        if (file.exists()) {
          file.delete();
        }
        mfs.copyFile(new File(oldfile), file);
      }

      out.println("<script>window.parent.changePhoto('" + fileTempName + "');alert(\"上传相片成功!\");</script>");
    }
    catch (Exception e) {
      out.println("<script>alert(\"上传照片失败!\")</script>");
      e.printStackTrace();
    } finally {
      out.close();
    }
  }

  public void deletePhoto(HttpServletRequest request, HttpServletResponse response)
  {
    PrintWriter out = null;

    label173: 
    try { out = response.getWriter();

      ASFuntion asf = new ASFuntion();
      Foder foder = new Foder("", request);

      String fileName = asf.showNull(request.getParameter("deleteName"));

      if ((fileName != null) && (!"".equals(fileName)))
      {
        String newPath = foder.createFoder("userPhoto");
        String newfile = newPath + fileName;

        File file = new File(newfile);

        if (file.exists())
        {
          if (file.delete()) {
            out.write("suc"); break label173;
          }
          out.write("fail"); break label173;
        }

        out.write("notExist");
      }

    }
    catch (Exception e)
    {
      e.printStackTrace();
    } finally {
      out.close();
    }
  }

  public void deleteUpdatePhoto(HttpServletRequest request, HttpServletResponse response)
  {
    PrintWriter out = null;
    Connection conn = null;
    PreparedStatement ps = null;

    label274: 
    try { conn = new DBConnect().getConnect("");
      out = response.getWriter();
      ASFuntion asf = new ASFuntion();

      String id = asf.showNull(request.getParameter("id"));

      String sql = "update k_User set userPhoto=?,userPhotoTemp=? where id=?";

      ps = conn.prepareStatement(sql);
      ps.setString(1, "");
      ps.setString(2, "");
      ps.setString(3, id);

      ps.execute();
      ps.execute("Flush tables");

      Foder foder = new Foder("", request);

      String fileName = asf.showNull(request.getParameter("deleteName"));

      if ((fileName != null) && (!"".equals(fileName)))
      {
        String newPath = foder.createFoder("userPhoto");
        String newfile = newPath + fileName;

        File file = new File(newfile);

        if (file.exists())
        {
          if (file.delete()) {
            out.write("suc"); break label274;
          }
          out.write("fail"); break label274;
        }

        out.write("notExist");
      }

    }
    catch (Exception e)
    {
      e.printStackTrace();
    } finally {
      out.close();
    }
  }

  private String getFileName(String filepath)
  {
    String returnstr = filepath;
    int length = filepath.trim().length();

    filepath = filepath.replace('\\', '/');
    if (length > 0) {
      int i = filepath.lastIndexOf("/");
      if (i >= 0) {
        filepath = filepath.substring(i + 1);
        returnstr = filepath;
      }
    }
    return returnstr;
  }

  public ModelAndView setOrderBy(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    Connection conn = null;

    ModelAndView modelAndView = new ModelAndView("user/setOrderBy.jsp");
    try {
      conn = new DBConnect().getConnect("");

      List userList = new UserService(conn).getUserList();

      modelAndView.addObject("userList", userList);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      conn.close();
    }
    return modelAndView;
  }

  public ModelAndView saveOrderBy(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    ASFuntion asf = new ASFuntion();
    Connection conn = null;
    PrintWriter out = null;
    try {
      conn = new DBConnect().getConnect("");
      String values = asf.showNull(request.getParameter("values"));

      int result = new UserService(conn).saveOrderBy(values);

      response.setContentType("text/html;charset=utf-8");
      out = response.getWriter();

      out.print(result);
    }
    catch (Exception e) {
      e.printStackTrace();
    } finally {
      out.close();
      conn.close();
    }
    return null;
  }

  public ModelAndView isRoleOutride(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    ASFuntion asf = new ASFuntion();
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    PrintWriter out = null;
    try
    {
      response.setContentType("text/html;charset=utf-8");
      out = response.getWriter();

      conn = new DBConnect().getConnect("");
      String departmentid = asf.showNull(request.getParameter("departmentid"));
      String roleId = asf.showNull(request.getParameter("roleId"));
      String limitRole = asf.showNull(UTILSysProperty.SysProperty.getProperty("以下角色每个部门不得多于三个"));
      String opt = asf.showNull(request.getParameter("opt"));
      UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");

      if ("19".equals(userSession.getUserId())) {
        out.write("ok");
        return null;
      }

      if (!"1".equals(opt)) {
        String userId = asf.showNull(request.getParameter("userId"));
        String tempSql = "select group_concat(distinct rid) from k_userrole where rid in(" + roleId + ") " + 
          " and rid not in(select rid from k_userrole where userid =?)";
        DbUtil dbUtil = new DbUtil(conn);
        String[] params = { userId };
        roleId = dbUtil.queryForString(tempSql, params);
      }

      String tempRoleName = "";
      if (!"".equals(limitRole))
      {
        String sql = "SELECT a.autoid,a.departname,d.id,d.rolename,COUNT(DISTINCT b.name) as userCount \nFROM k_department a,k_user b,k_userrole c,k_role d \nWHERE a.autoid = b.departmentid \nAND b.id = c.userid AND c.rid = d.id \nAND d.id IN(?) \nAND d.id IN(?) \nAND b.departmentid = ? \nGROUP BY departname,rid \nORDER BY departname,rid \n";

        ps = conn.prepareStatement(sql);
        ps.setString(1, roleId);
        ps.setString(2, limitRole);
        ps.setString(3, departmentid);
        rs = ps.executeQuery();
        while (rs.next()) {
          int count = rs.getInt("userCount");
          String rolename = rs.getString("rolename");
          if (count > 2) {
            tempRoleName = tempRoleName + rolename + ",";
          }
        }
      }

      if ("".equals(tempRoleName))
        out.write("ok");
      else
        out.write(tempRoleName.substring(0, tempRoleName.length() - 1));
    }
    catch (Exception e)
    {
      e.printStackTrace();
    } finally {
      out.close();
      conn.close();
    }
    return null;
  }

  public ModelAndView userMessagerList(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    ModelAndView modelAndView = new ModelAndView("/user/userMessagerList.jsp");

    DataGridProperty pp = new DataGridProperty();
    UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
    String departmentid = new ASFuntion().showNull(request.getParameter("departmentid"));
    modelAndView.addObject("departmentid", userSession.getUserAuditDepartmentId());

    String sql = "select distinct a.*,b.departname,group_concat(distinct e.rolename) as rolename  \n\tfrom k_user a \n\tleft join k_department b on a.departmentid = b.autoid \n\tleft join k_department c on b.fullpath like concat(c.fullpath,'%') \n   left join k_userRole d on a.id = d.userId   \n   left join k_role e on d.rid = e.id \twhere a.state = 0  \n  ${departmentid} ${loginid} ${roles} group by a.id";

    pp.addColumn("姓名", "name");
    pp.addColumn("所属部门", "departname");
    pp.addColumn("手机", "mobilePhone");
    pp.addColumn("工位号", "station");
    pp.addColumn("办公电话", "phone");
    pp.addColumn("EMail", "email");
    pp.addColumn("楼层", "floor");
    pp.addColumn("房间号", "house");
    pp.addColumn("所属角色", "rolename");

    pp.addSqlWhere("departmentid", " and ifnull(c.autoid,'') in (${departmentid}) ");
    pp.addSqlWhere("loginid", " and a.id = '${loginid}' or a.name like '%${loginid}%'  ");
    pp.addSqlWhere("roles", " and e.id in (${roles})  ");

    pp.setOrderBy_CH("a.departmentid,a.istips,a.name ");
    pp.setTableID("userMessagerList");
    pp.setCustomerId("");
    pp.setInputType("checkbox");
    pp.setUseBufferGrid(false);

    pp.setPageSize_CH(500);
    pp.setWhichFieldIsValue(1);
    pp.setColumnWidth("7,10,10,6,8,8,10,10,20");

    pp.setPrintEnable(true);
    pp.setPrintTitle("手机短息");

    pp.setSQL(sql);

    request.getSession().setAttribute("DGProperty_" + pp.getTableID(), pp);

    modelAndView.addObject("tableid", pp.getTableID());
    return modelAndView;
  }

  public void userMessager(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    response.setCharacterEncoding("utf-8");

    Connection conn = null;
    try {
      ASFuntion asf = new ASFuntion();
      InteriorEmailAction emailAction = new InteriorEmailAction();

      PrintWriter out = response.getWriter();
      String msg = "";
      conn = new DBConnect().getConnect("");
      String userIds = asf.showNull(request.getParameter("userIds"));
      String mobilePhone = asf.showNull(request.getParameter("mobilePhone"));
      String conter = asf.showNull(request.getParameter("conter"));

      if ((!"".equals(userIds)) || ((!"".equals(mobilePhone)) && (!"".equals(conter))))
      {
        emailAction.mobilePhoneInfo(request, response, mobilePhone.replaceAll("，", ","), userIds, conter);

        msg = "发送成功";
        out.write(msg);
      }

    }
    catch (Exception e)
    {
      e.printStackTrace();
    } finally {
      DbUtil.close(conn);
    }
  }

  public ModelAndView smsList(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    ModelAndView modelAndView = new ModelAndView("user/smsList.jsp");

    DataGridProperty pp = new DataGridProperty();
    UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");

    String sql = "SELECT DISTINCT a.`autoId`,b.`Name` AS `fsUserId`,`content`,c.`Name` AS `jsUserId`,if(a.state='0','成功',a.`state`) as state,\na.`jsMobilePhone`,a.`createDate`,a.`smsPriority`,`perproty` \nFROM `asdb`.`k_sms` a \nLEFT JOIN k_user b ON a.`fsUserId` = b.`id` \nLEFT JOIN k_user c ON a.`jsUserId` = c.`id` \nLEFT JOIN k_userrole d ON a.`fsUserId` = d.`userid` \nLEFT JOIN k_role e ON d.`rid` = e.`rolename` \nWHERE 1=1 AND (a.`fsUserId` = '" + 
      userSession.getUserId() + "' OR 19 = " + userSession.getUserId() + ")" + 
      " ${jsUserId} ${createDate} ";

    System.out.println(sql);
    if ("19".equals(userSession.getUserId()))
    {
      pp.addColumn("发送人", "fsUserId");
      pp.setColumnWidth("8,40,10,10,10,15");
    } else {
      pp.setColumnWidth("45,10,10,10,15");
    }
    pp.addColumn("内容", "content");
    pp.addColumn("接收人", "jsUserId");
    pp.addColumn("接收人手机号", "jsMobilePhone");
    pp.addColumn("发送状态", "state");
    pp.addColumn("发送时间", "createDate");

    pp.addSqlWhere("jsUserId", " and c.id = '${jsUserId}' ");
    pp.addSqlWhere("createDate", " and a.createDate like '%${createDate}%'  ");
    pp.setUseBufferGrid(false);
    pp.setOrderBy_CH("createDate");
    pp.setDirection("desc");
    pp.setTableID("smsList");
    pp.setCustomerId("");

    pp.setPageSize_CH(50);
    pp.setWhichFieldIsValue(1);

    pp.setSQL(sql);

    request.getSession().setAttribute("DGProperty_" + pp.getTableID(), pp);

    return modelAndView;
  }

  public ModelAndView changePassword(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    ModelAndView modelAndView = new ModelAndView("/user/changePassword.jsp");

    Connection conn = null;
    PrintWriter out = null;
    ASFuntion asf = new ASFuntion();
    response.setContentType("text/html;charset=utf-8");
    String newPassword = asf.showNull(request.getParameter("password"));
    String pwdReset = asf.showNull(request.getParameter("pwdReset"));

    if ((!"".equals(newPassword)) || (!"".equals(pwdReset))) {
      try {
        conn = new DBConnect().getConnect("");

        UserSession userSession = (UserSession)request.getSession()
          .getAttribute("userSession");

        int id = Integer.parseInt(userSession.getUserId());
        UserService userService = new UserService(conn);
        ModelAndView localModelAndView1;
        if (!"".equals(newPassword))
        {
          if (userService.UpdatePassword(id, newPassword)) {
            out = response.getWriter();
            out.println("<script>alert('密码修改成功，请重新登录!')</script>");
            request.getSession().removeAttribute("userSession");
            out.flush();
            localModelAndView1 = modelAndView;
            return localModelAndView1;
          }

        }

        if (("0".equals(pwdReset)) && 
          (userService.UpdatePassword(id, "1"))) {
          out = response.getWriter();
          out.println("<script>alert('密码重新成功，请重新登录!')</script>");
          request.getSession().removeAttribute("userSession");
          out.flush();
          localModelAndView1 = modelAndView;
          return localModelAndView1;
        }
      }
      catch (Exception localException)
      {
      }
      finally {
        DbUtil.close(conn); } DbUtil.close(conn);
    }
    else {
      return modelAndView;
    }

    return null;
  }

  public ModelAndView userEditDepartmentList(HttpServletRequest request, HttpServletResponse response) throws Exception
  {
    ASFuntion CHF = new ASFuntion();
    Connection conn = null;
    ModelAndView modelAndView = new ModelAndView(userEditDepartmentList);
    try
    {
      UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");

      String areaId = CHF.showNull(request.getParameter("areaId"));
      String departmentId = CHF.showNull(request.getParameter("departmentId"));

      DataGridProperty pp = new DataGridProperty();
      pp.setTableID("userBatchEditDepartment");
      pp.setCustomerId("");
      pp.setPageSize_CH(100);
      pp.setWhichFieldIsValue(1);
      pp.setInputType("checkbox");

      String strSql = "SELECT a.id,a.`Name`,a.`loginid`,roles, \n CASE WHEN Sex='M' OR Sex='男' THEN '男' ELSE '女' END Sex,a.rank,b.departName \n FROM k_user a \n LEFT JOIN k_department b ON a.`departmentid` = b.`autoid` \n left join (select userid,group_concat(distinct rolename) as roles from k_userrole a,k_role b where a.rid=b.id group by userid)c on a.id=c.userid  WHERE  1=1 and a.state=0  ";

      if (!"".equals(areaId)) {
        strSql = strSql + " and b.areaid = '" + areaId + "' ";
      }
      if (!"".equals(departmentId)) {
        strSql = strSql + " and (b.autoid='" + departmentId + "'  OR fullpath LIKE '" + departmentId + "|%')  ";
      }
      System.out.println(strSql);
      pp.setSQL(strSql);
      pp.setOrderBy_CH("id");
      pp.setDirection("desc");
      pp.setColumnWidth("8,8,5,10,15");
      pp.setUseBufferGrid(false);

      pp.addColumn("姓名", "Name");
      pp.addColumn("登录名", "loginid");
      pp.addColumn("性别", "Sex", "showCenter");
      pp.addColumn("所属部门", "departName");

      pp.addColumn("薪酬级别", "rank");
      pp.addColumn("权限", "roles");

      modelAndView.addObject("departmentId", departmentId);

      request.getSession().setAttribute("DGProperty_" + pp.getTableID(), pp);
    }
    catch (Exception e) {
      com.matech.framework.pub.util.Debug.print(4, "查询用户列表失败！", e);
      e.printStackTrace();
      throw e;
    } finally {
      DbUtil.close(conn);
    }
    return modelAndView;
  }

  public ModelAndView updateUserDepartment(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    ASFuntion CHF = new ASFuntion();
    Connection conn = null;
    try
    {
      conn = new DBConnect().getConnect("");
      request.setCharacterEncoding("utf-8");
      response.setCharacterEncoding("utf-8");
      UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");

      String ids = CHF.showNull(request.getParameter("userIds"));
      String departmentId = CHF.showNull(request.getParameter("departmentId"));
      String departname = CHF.showNull(request.getParameter("departname"));
      String menuId = CHF.showNull(request.getParameter("menuId"));
      String opt = CHF.showNull(request.getParameter("opt"));
      String msg = "移动失败!";
      if ((!"".equals(ids)) && (!"".equals(departmentId))) {
        ids = ids.substring(0, ids.length() - 1);
        String selectUserSql = "select group_concat(name) from k_user where id in(" + ids + ")";
        String mobUser = new DbUtil(conn).queryForString(selectUserSql);

        String logInfo = "【" + userSession.getUserName() + "】将【" + mobUser + "】移动到【" + departname + "】";
        this.log.log(logInfo);

        String sql = "UPDATE K_USER SET DEPARTMENTID='" + departmentId + "' WHERE ID IN(" + ids + ")";
        int result = new DbUtil(conn).executeUpdate(sql);

        if (result > 0) {
          msg = "移动成功!";
        }
      }
      response.getWriter().write(msg);
    } catch (Exception localException) {
    } finally {
      DbUtil.close(conn);
    }
    return null;
  }

  public ModelAndView mobileMessageList(HttpServletRequest request, HttpServletResponse response) throws Exception
  {
    ASFuntion CHF = new ASFuntion();
    Connection conn = null;
    ModelAndView modelAndView = new ModelAndView(mobileMessageList);
    try {
      conn = new DBConnect().getConnect("");

      UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");

      String userid = userSession.getUserId();

      String departId = CHF.showNull(request.getParameter("departmentId"));
      String areaId = CHF.showNull(request.getParameter("areaId"));
      String menuid = CHF.showNull(request.getParameter("menuid"));

      String departmentIds = new UserPopedomService(conn).getUserIdPopedom(userid, menuid);

      DataGridProperty pp = new DataGridProperty();
      pp.setTableID("mobileMessageList");
      pp.setCustomerId("");
      pp.setPageSize_CH(100);
      pp.setWhichFieldIsValue(1);

      String strSql = "";

      if ("".equals(departId)) {
        if (!"".equals(areaId)) {
          areaId = " and a.areaId = '" + areaId + "'";
        }
        strSql = "SELECT * FROM ( \nSELECT a.autoId,a.`departname`,IFNULL(fsCount,0) AS fsCount,IFNULL(sbCount,0) AS sbCount,a.areaid FROM k_department a \nLEFT JOIN ( \n\tSELECT  autoId,departname,SUM(fsCount) AS fsCount,SUM(sbCount) AS sbCount FROM( \n\t\tSELECT c.autoId,c.departname,IF(a.state ='短信发送成功',COUNT(*),0) AS fsCount,IF(a.state <>'短信发送成功',COUNT(*),0) AS sbCount  FROM k_sms a \n\t\tLEFT JOIN k_user b ON a.fsUserId = b.id \n\t\tLEFT JOIN k_department c ON b.departmentid = c.autoid \n\t\tWHERE b.`departmentid`>'' ${createDate} \n\t\tGROUP BY a.state,c.autoid ORDER BY ABS(c.autoid) ASC \n\t)a GROUP BY autoId \n) b ON a.autoid = b.autoid \n where 1=1 " + 
          areaId + " " + 
          "UNION ALL \n" + 
          "SELECT  0,departname,SUM(fsCount) AS fsCount,SUM(sbCount) AS sbCount,0 FROM ( \n" + 
          "\tSELECT 0,CONCAT('无部门-',IFNULL(b.name,'未知')) AS departname, \n" + 
          "\tIF(a.state ='短信发送成功',COUNT(*),0) AS fsCount,IF(a.state <>'短信发送成功',COUNT(*),0) AS sbCount   FROM k_sms a \n" + 
          "\tLEFT JOIN k_user b ON a.fsUserId = b.id \n" + 
          "\tWHERE 1=1 and (b.departmentid IS NULL OR departmentid ='') ${createDate} GROUP BY a.fsUserId,a.state \n" + 
          ")b GROUP BY departname \n" + 
          ")a  ";

        strSql = strSql + " union all select -1,'合计',sum(fsCount) as fsCount,sum(sbCount) as sbCount,0 from (" + strSql + ") a ";
      }
      else {
        if (!"".equals(departId)) {
          departId = "AND (b.`departmentid`='" + departId + "' OR c.`fullpath` LIKE CONCAT('" + departId + "','|%'))";
        }
        strSql = "SELECT id as autoId,ifnull(`departname`,'无部门') as departname,SUM(fsCount) AS fsCount,SUM( sbCount) AS  sbCount FROM ( \nSELECT b.id,CONCAT(c.`departname`,'-',`name`) AS departname,IF(a.state ='短信发送成功',COUNT(*),0) AS fsCount,IF(a.state <>'短信发送成功',COUNT(*),0) AS sbCount  \nFROM k_sms a \nLEFT JOIN k_user b ON a.fsUserId = b.id \nLEFT JOIN k_department c ON b.departmentid = c.autoid \nWHERE 1=1 " + 
          departId + " ${createDate} \n" + 
          "GROUP BY a.fsUserId,a.state  ORDER BY ABS(c.autoid) ASC \n" + 
          ") a GROUP BY id ";

        strSql = strSql + " union  select -1,'合计' as departname,sum(fsCount) as fsCount,sum(sbCount) as sbCount from (" + strSql + ") a";
      }

      pp.setSQL(strSql);
      pp.setOrderBy_CH("autoid");
      pp.setDirection("asc");
      pp.setColumnWidth("30,20,20");

      pp.setPrintColumnWidth("60,30,30");

      pp.setPrintEnable(true);
      pp.setPrintTitle("手机短信统计列表");

      if (!"".equals(departId))
        pp.addColumn("姓名", "departname", "showCenter");
      else {
        pp.addColumn("部门", "departname", "showCenter");
      }
      pp.addColumn("发送数量", "fsCount", "showCenter");
      pp.addColumn("失败数量", "sbCount", "showCenter");

      pp.addSqlWhere("createDate", " and a.createdate>='${beginDate} 00:00:00' and a.createdate<='${endDate} 24:00:00' ");

      request.getSession().setAttribute("DGProperty_" + pp.getTableID(), pp);
    }
    catch (Exception e) {
      com.matech.framework.pub.util.Debug.print(4, "查询用户列表失败！", e);
      e.printStackTrace();
      throw e;
    } finally {
      DbUtil.close(conn);
    }
    return modelAndView;
  }

  public ModelAndView roleUserList(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    ASFuntion CHF = new ASFuntion();
    ModelAndView modelAndView = new ModelAndView(roleUserList);
    try
    {
      String roleId = CHF.showNull(request.getParameter("roleId"));

      DataGridProperty pp = new DataGridProperty();
      pp.setTableID("roleUserList");
      pp.setCustomerId("");
      pp.setPageSize_CH(100);
      pp.setWhichFieldIsValue(1);
      if (!"".equals(roleId)) {
        roleId = " and a.rid='" + roleId + "' ";
      }
      String strSql = "SELECT a.`userid`,b.`Name`,b.`loginid`,CASE WHEN Sex='M' OR Sex='男' THEN '男' ELSE '女' END Sex,b.rank,c.departName,d.roles FROM k_userRole a \ninner JOIN k_user b ON a.`userid` = b.`id` \nLEFT JOIN k_department c ON b.`departmentid` = c.`autoid` \nLEFT JOIN (SELECT userid,GROUP_CONCAT(DISTINCT rolename) AS roles FROM k_userrole a,k_role b WHERE a.rid=b.id GROUP BY userid)d ON b.id=d.userid \nWHERE 1=1 " + 
        roleId + " ";

      pp.setSQL(strSql);
      pp.setOrderBy_CH("id");
      pp.setDirection("desc");
      pp.setColumnWidth("15,15,10,20,30");

      pp.addColumn("姓名", "Name");

      pp.addColumn("性别", "Sex", "showCenter");
      pp.addColumn("所属部门", "departName");

      pp.addColumn("权限", "roles");

      request.getSession().setAttribute("DGProperty_" + pp.getTableID(), pp);
    }
    catch (Exception e) {
      com.matech.framework.pub.util.Debug.print(4, "查询用户列表失败！", e);
      e.printStackTrace();
      throw e;
    }

    return modelAndView;
  }

  public ModelAndView userDetailsTreeList(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    ModelAndView modelAndView = new ModelAndView(userDetailsTreeList);
    try
    {
      DataGridProperty pp = new DataGridProperty();
      pp.setTableID("userDetailsTreeList");
      pp.setCustomerId("");
      pp.setPageSize_CH(100);
      pp.setWhichFieldIsValue(1);

      String strSql = " SELECT`autoId`,`id`,`text`,`url`,`orderby`,if(`isShow` = 1,'显示','隐藏' ) as isShow,  `createDate`,`createUser`,`property` FROM `asdb`.`k_userdetailstree`  ";

      pp.setSQL(strSql);
      pp.setOrderBy_CH("abs(orderby)");
      pp.setDirection("asc");
      pp.setColumnWidth("15,15,10,20,30");
      pp.setInputType("radio");

      pp.addColumn("id", "id");
      pp.addColumn("标签名", "text");
      pp.addColumn("url", "url");
      pp.addColumn("排序", "orderby");
      pp.addColumn("显示/隐藏", "isShow");

      request.getSession().setAttribute("DGProperty_" + pp.getTableID(), pp);
    }
    catch (Exception e) {
      com.matech.framework.pub.util.Debug.print(4, "查询用户列表失败！", e);
      e.printStackTrace();
      throw e;
    }

    return modelAndView;
  }

  public ModelAndView saveDetailsTree(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    Connection conn = null;
    ASFuntion CHF = new ASFuntion();
    try
    {
      UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");

      String autoId = CHF.showNull(request.getParameter("autoId"));
      String id = CHF.showNull(request.getParameter("id"));
      String text = CHF.showNull(request.getParameter("text"));
      String url = CHF.showNull(request.getParameter("url"));
      String orderby = CHF.showNull(request.getParameter("orderby"));
      String isShow = CHF.showNull(request.getParameter("isShow"));
      String property = CHF.showNull(request.getParameter("property"));

      UserDetailsTree detailsTree = new UserDetailsTree();

      detailsTree.setAutoId(autoId);
      detailsTree.setId(id);
      detailsTree.setText(text);
      detailsTree.setUrl(url);
      detailsTree.setIsShow(isShow);
      detailsTree.setOrderby(orderby);
      detailsTree.setProperty(property);

      conn = new DBConnect().getConnect("");
      UserService us = new UserService(conn);
      if ("".equals(autoId)) {
        detailsTree.setCreateUser(userSession.getUserId());
        detailsTree.setCreateDate(CHF.getCurrentDate() + " " + CHF.getCurrentTime());

        us.saveDetailsTree(detailsTree);
      } else {
        us.updateDetailsTree(detailsTree);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      DbUtil.close(conn);
    }
    response.sendRedirect(request.getContextPath() + "/user.do?method=userDetailsTreeList");

    return null;
  }

  public ModelAndView goUpdateDetailsTree(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    Connection conn = null;
    ASFuntion CHF = new ASFuntion();
    String autoId = CHF.showNull(request.getParameter("autoId"));
    ModelAndView modelAndView = new ModelAndView(updateUserDetailTree);
    try {
      conn = new DBConnect().getConnect("");
      UserService us = new UserService(conn);
      UserDetailsTree detailsTree = us.getUserDetailsTree(autoId);

      modelAndView.addObject("userDetailsTree", detailsTree);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      DbUtil.close(conn);
    }
    return modelAndView;
  }

  public void delDetailsTree(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    Connection conn = null;
    ASFuntion CHF = new ASFuntion();
    try {
      String autoId = CHF.showNull(request.getParameter("autoId"));
      conn = new DBConnect().getConnect("");
      UserService us = new UserService(conn);
      us.delDetailsTree(autoId);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      DbUtil.close(conn);
    }
    response.sendRedirect(request.getContextPath() + "/user.do?method=userDetailsTreeList");
  }

  public ModelAndView ajaxUpdateDetailsTree(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    Connection conn = null;
    try {
      conn = new DBConnect().getConnect("");
      UserService us = new UserService(conn);
      response.setContentType("html/text");
      response.setCharacterEncoding("UTF-8");
      PrintWriter out = response.getWriter();

      List listMap = us.getListUserDetailsTree();
      String json = JSONArray.fromObject(listMap).toString();
      System.out.println("join=" + json);
      out.write(json);
    }
    catch (Exception e) {
      e.printStackTrace();
    } finally {
      DbUtil.close(conn);
    }
    return null;
  }

  public ModelAndView doSaveFav(HttpServletRequest request, HttpServletResponse response) throws Exception
  {
    Connection conn = null;
    DbUtil dbUtil = null;
    WebUtil webUtil = new WebUtil(request, response);
    response.setContentType("text/html;charset=utf-8");
    UserFavVO userFavVO = (UserFavVO)webUtil.evalObject(UserFavVO.class);
    UserSession userSession = webUtil.getUserSession();
    int eff = 0;
    String re = "";
    try {
      conn = new DBConnect().getConnect();
      dbUtil = new DbUtil(conn);
      List userFavVOs = dbUtil.select(UserFavVO.class, 
        "select * from {0} where userid=? and name=?", new Object[] { 
        userSession.getUserId(), 
        userFavVO.getName() });

      if (userFavVOs.size() > 0) {
        userFavVO = (UserFavVO)userFavVOs.get(0);
        userFavVO = (UserFavVO)webUtil.evalObject(userFavVO);
        eff += dbUtil.update(new Object[] { userFavVO });
      } else {
        userFavVO.setUuid(UUID.randomUUID().toString());
        userFavVO.setUserid(userSession.getUserId());
        eff += dbUtil.insert(new Object[] { userFavVO });
      }
      re = eff > 0 ? "保存成功" : "保存失败";
    } catch (Exception ex) {
      re = ex.getLocalizedMessage();
    } finally {
      DbUtil.close(conn);
    }
    response.getWriter().write(re);
    return null;
  }

  public void departmentTree(HttpServletRequest request, HttpServletResponse response) throws Exception
  {
    response.setContentType("text/html;charset=utf-8");
    Connection conn = null;
    DbUtil dbUtil = null;
    WebUtil webUtil = new WebUtil(request, response);
    UserSession userSession = webUtil.getUserSession();
    try {
      ASFuntion CHF = new ASFuntion();

      String checked = CHF.showNull(request.getParameter("checked"));

      String departid = CHF.showNull(request.getParameter("departid"));
      String areaid = CHF.showNull(request.getParameter("areaid"));
      String departname = CHF.showNull(request.getParameter("departname"));
      String isSubject = CHF.showNull(request.getParameter("isSubject"));

      String userpopedom = CHF.showNull(request.getParameter("userpopedom"));

      String loginid = CHF.showNull(request.getParameter("loginid"));
      String menuid = CHF.showNull(request.getParameter("omenuid"));

      String addUser = CHF.showNull(request.getParameter("addUser"));

      System.out.println(addUser + "|" + checked + "|" + departid + "|" + areaid + "|" + departname + "|" + isSubject);

      conn = new DBConnect().getConnect("");
      dbUtil = new DbUtil(conn);
      DepartmentService ds = new DepartmentService(conn);
      UserPopedomService up = new UserPopedomService(conn);
      String departments = up.getLoginIdPopedom(loginid, menuid);

      ds.setAddUser(addUser);

      List list = null;
      if (("".equals(isSubject)) || ("undefined".equals(isSubject))) {
        list = ds.getOrgan(checked);
        if (list == null)
        {
          if ("userpopedom".equals(userpopedom)) {
            checked = "false";
            ds.setUserpopedom(departments);
          }
          departid = "555555";
          list = ds.getDepartment(departid, areaid, checked);
        }

        Map favRoot = new HashMap();
        favRoot.put("id", UUID.randomUUID().toString());
        favRoot.put("text", "自定义用户");
        favRoot.put("leaf", Boolean.valueOf(false));
        favRoot.put("isSubject", "user_fav_root");
        list.add(favRoot);
      } else if ("user_fav_root".equals(isSubject)) {
        List<UserFavVO> userFavVOs = dbUtil.select(UserFavVO.class, 
          "select * from {0} where userid=? ", new Object[] { userSession.getUserId() });
        list = new ArrayList();
        for (UserFavVO userFavVO : userFavVOs) {
          Map favRoot = new HashMap();
          favRoot.put("id", userFavVO.getUuid());
          favRoot.put("text", userFavVO.getName());
          favRoot.put("leaf", Boolean.valueOf(true));
          favRoot.put("isSubject", "user_fav");
          favRoot.put("fav_user_ids", userFavVO.getFav_user_ids());
          list.add(favRoot);
        }

      }
      else if ("1".equals(isSubject))
      {
        list = ds.getArea(departid, checked);
        if (list == null)
        {
          if ("userpopedom".equals(userpopedom)) {
            checked = "false";
            ds.setUserpopedom(departments);
          }
          list = ds.getDepartment(departid, areaid, checked);
          if ("true".equals(addUser)) {
            List list1 = ds.getUser(departid, checked);
            if (list1 != null) {
              if (list == null) list = new ArrayList();
              for (int i = 0; i < list1.size(); i++)
                list.add(list1.get(i));
            }
          }
        }
      }
      else
      {
        if ("userpopedom".equals(userpopedom)) {
          checked = "false";
          ds.setUserpopedom(departments);
        }
        list = ds.getDepartment(departid, areaid, checked);
        if ("true".equals(addUser)) {
          List list1 = ds.getUser(departid, checked);
          if (list1 != null) {
            if (list == null) list = new ArrayList();
            for (int i = 0; i < list1.size(); i++) {
              System.out.println(list1.get(i));
              list.add(list1.get(i));
            }
          }
        }

      }

      String json = "{}";
      if (list != null) {
        json = JSONArray.fromObject(list).toString();
      }
      System.out.println("json=" + json);
      response.getWriter().write(json);
    }
    catch (Exception e) {
      e.printStackTrace();
    } finally {
      DbUtil.close(conn);
    }
  }

  public ModelAndView jsonUser(HttpServletRequest request, HttpServletResponse response) throws Exception
  {
    Connection conn = null;
    DbUtil dbUtil = null;
    WebUtil webUtil = new WebUtil(request, response);
    UserSession userSession = webUtil.getUserSession();
    int eff = 0;
    String re = "";
    String id = request.getParameter("id");
    JSONObject json = new JSONObject();
    response.setContentType("text/html;charset=utf-8");
    try {
      conn = new DBConnect().getConnect();
      dbUtil = new DbUtil(conn);
      UserVO userVO = (UserVO)dbUtil.load(UserVO.class, Integer.valueOf(Integer.parseInt(id)));
      json = JSONObject.fromObject(userVO);
      re = json.toString();
    } catch (Exception ex) {
      re = ex.getLocalizedMessage();
    } finally {
      DbUtil.close(conn);
    }
    response.getWriter().write(re);
    return null;
  }

  public ModelAndView deleteuserFav(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    Connection conn = null;
    DbUtil dbUtil = null;
    WebUtil webUtil = new WebUtil(request, response);
    UserSession userSession = webUtil.getUserSession();

    String id = request.getParameter("uuid");

    response.setContentType("text/html;charset=utf-8");
    try {
      conn = new DBConnect().getConnect();
      dbUtil = new DbUtil(conn);
      dbUtil.del("k_user_fav", "uuid", id);
    } catch (Exception ex) {
      response.getWriter().write("删除失败！");
      ex.getLocalizedMessage();
    } finally {
      DbUtil.close(conn);
    }
    response.getWriter().write("删除成功！");
    return null;
  }

  public ModelAndView nlist(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    return new ModelAndView("user/nlist.jsp");
  }

  public ModelAndView checkUserRole(HttpServletRequest request, HttpServletResponse response) throws Exception
  {
    Connection conn = null;
    conn = new DBConnect().getConnect();
    RoleService roleService = new RoleService(conn);
    String rolename = request.getParameter("rolename");
    UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
    String re = "no";
    boolean b = roleService.hasRole(userSession.getUserId(), rolename);

    if (b) {
      re = "ok";
    }
    response.getWriter().write(re);
    return null;
  }

  public boolean checkUserRole(String userid, String rolename)
  {
    Connection conn = null;
    boolean b = false;
    try {
      conn = new DBConnect().getConnect();
      RoleService roleService = new RoleService(conn);
      b = roleService.hasRole(userid, rolename);
    } catch (Exception e) {
      e.printStackTrace();
    }
    String re = "no";
    return b;
  }
  public ModelAndView changeEmType(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection conn = null;
    conn = new DBConnect().getConnect();

    ASFuntion CHF = new ASFuntion();
    String emtype = CHF.showNull(request.getParameter("emtype"));
    String userid = CHF.showNull(request.getParameter("userid"));
    DbUtil dbUtil = new DbUtil(conn);
    List uidAndemtype = new ArrayList();
    uidAndemtype.add(emtype);
    uidAndemtype.add(userid);
    try
    {
      dbUtil.update("update k_user set emtype=? where id=?", uidAndemtype);
      response.getWriter().write("ok");
    } catch (Exception e) {
      response.getWriter().write("no");
    }
    return null;
  }
  
  
  public ModelAndView doUpdateIndependence(HttpServletRequest request,HttpServletResponse response) throws Exception{
		Connection conn=null;
		DbUtil dbUtil=null;
		WebUtil webUtil=new WebUtil(request, response);
		UserSession userSession=webUtil.getUserSession();
		int eff=0;
		String re="";
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		String uuid=request.getParameter("uuid");
		IndependenceVO independenceVO=null;
		//String year=request.getParameter("year");
		//String jarr=request.getParameter("jarr");
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			independenceVO=dbUtil.load(IndependenceVO.class, uuid);
			independenceVO=webUtil.evalObject(independenceVO);
		    eff+=dbUtil.update(independenceVO);
		    if(eff>0){
		    	re="更新成功";
		    }else{
		    	re="更新失败";
		    }
		}catch(Exception ex){
			re=ex.getLocalizedMessage();
		}finally{
			DbUtil.close(conn);
		}
		response.getWriter().write(re);
		return null;
	}

  public static enum Jsp
  {
    ListEm,independence;

    public String getPath() {
      return MessageFormat.format("/user/{0}.jsp", new Object[] { name() });
    }
  }
  
  
	public ModelAndView addIndependence(HttpServletRequest request, HttpServletResponse response) {
		
		java.util.Calendar c=java.util.Calendar.getInstance();  
		java.text.SimpleDateFormat f=new java.text.SimpleDateFormat("yyyy");
		String current_year = f.format(c.getTime());
		
		WebUtil webUtil=new WebUtil(request, response);
		UserSession userSession=webUtil.getUserSession();
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		String userid = userSession.getUserId();
		
		DbUtil dbUtil = null;
		Connection conn = null;
		IndependenceVO independenceVO = null;
		
		try {
			conn = new DBConnect().getConnect();
			dbUtil = new DbUtil(conn);
			List<IndependenceVO> independenceVOs = dbUtil.select(IndependenceVO.class, "select * from {0} where `userid`=? and `year`=?", userid, current_year);
			if(independenceVOs.size()>0) {
				webUtil.alert("您已完成本年度独立性问卷调查，无需重复新增，如需修改请选择修改功能！");
				//response.getWriter().write("<script>parent.tab.remove(parent.tab.getActiveTab());</script>");
				response.getWriter().write("<script>window.history.back();</script>");
				return null;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new ModelAndView(Jsp.independence.getPath());
	}
	
	  public ModelAndView doSaveIndependence(HttpServletRequest request,HttpServletResponse response) {
		  
			Connection conn=null;
			DbUtil dbUtil=null;
			WebUtil webUtil=new WebUtil(request, response);
			UserSession userSession=webUtil.getUserSession();
			int eff=0;
			String re="";
			response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
			IndependenceVO independenceVO=null;
			//String uuid=request.getParameter("uuid");
			//String year=request.getParameter("year");
			//String jarr=request.getParameter("jarr");
			
			java.util.Calendar c=java.util.Calendar.getInstance();    
			java.text.SimpleDateFormat f=new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String modify_date = f.format(c.getTime());
			java.text.SimpleDateFormat ff=new java.text.SimpleDateFormat("yyyy");
			String current_year = ff.format(c.getTime());
			
			try{
				conn=new DBConnect().getConnect();
				dbUtil=new DbUtil(conn);
				
				List<IndependenceVO> independenceVOs = dbUtil.select(IndependenceVO.class, "select * from {0} where `userid`=? and `year`=?", userSession.getUserId(), current_year);
				if(independenceVOs.size()>0) {
					String uuid = request.getParameter("uuid");
					independenceVO=dbUtil.load(IndependenceVO.class, uuid);
					independenceVO=webUtil.evalObject(independenceVO);
				    eff+=dbUtil.update(independenceVO);
				    if(eff>0){
				    	re="更新成功";
				    }else{
				    	re="更新失败";
				    }
					response.getWriter().write(re);
					return null;
				}

				String uuid=UUID.randomUUID().toString();
				//independenceVO=dbUtil.load(IndependenceVO.class, uuid);
				independenceVO=webUtil.evalObject(IndependenceVO.class);
			    independenceVO.setUserid(userSession.getUserId());
			    independenceVO.setDepartmentid(userSession.getUserAuditDepartmentId());
			    independenceVO.setModify_date(modify_date);
			    independenceVO.setYear(current_year);
			    independenceVO.setUuid(uuid);
			    
			    
			    eff+=dbUtil.insert(independenceVO);
			    if(eff>0){
			    	re="保存成功";
			    }else{
			    	re="保存失败";
			    }
			    response.getWriter().write(re);
			}catch(Exception ex){
				re=ex.getLocalizedMessage();
			}finally{
				DbUtil.close(conn);
			}
			return null;
	  }
  
  
}