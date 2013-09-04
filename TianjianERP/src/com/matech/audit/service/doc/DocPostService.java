package com.matech.audit.service.doc;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.department.model.KAreaVO;
import com.matech.audit.service.department.model.KDepartmentVO;
import com.matech.audit.service.doc.model.AutoCodeUsedVO;
import com.matech.audit.service.doc.model.AutoCodeVO;
import com.matech.audit.service.doc.model.DocFlowVO;
import com.matech.audit.service.doc.model.DocLogVO;
import com.matech.audit.service.doc.model.DocPostFileVO;
import com.matech.audit.service.doc.model.DocPostVO;
import com.matech.audit.service.placard.PlacardService;
import com.matech.audit.service.placard.model.PlacardTable;
import com.matech.audit.service.user.model.UserVO;
import com.matech.audit.work.form.FormDefineAction;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.autocode.DELAutocode;
import com.matech.framework.pub.autocode.model.AutocodeTable;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.StringUtil;
import com.matech.sms.SmsOpt;
import com.matech.sms.SmsService;
import com.mysql.jdbc.Driver;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class DocPostService
{
  private Connection conn;
  private DbUtil dbUtil;
  private PlacardService placardService;
  private DELAutocode delAutocode;
  private SmsService smsService;

  public DocPostService(Connection conn)
  {
    this.conn = conn;
    try {
      this.dbUtil = new DbUtil(conn);
      this.placardService = new PlacardService(conn);
      this.delAutocode = new DELAutocode();
      this.smsService = new SmsService(conn);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public List<String> doSignIssue(DocPostVO docPostVO, UserSession userSession, String remark)
  {
    List result = new ArrayList();

    int i = 0;
    DocLogVO log = new DocLogVO();
    try
    {
      log.setDoc_no(docPostVO.getDoc_no());
      log.setHandle_time(StringUtil.getCurDateTime());
      log.setHandler_id(userSession.getUserId());
      log.setHandler_name(userSession.getUserName());
      log.setNode(Node.qf);
      log.setUuid(UUID.randomUUID().toString());
      log.setRemark(remark);
      log.setCtype("docpost");
      log.setDoc_id(docPostVO.getUuid());
      i += this.dbUtil.insert(new Object[] { log });
      if (i == 1)
        result.add(MessageFormat.format("文件 {0} 签发成功", new Object[] { docPostVO.getTitle() }));
      else {
        result.add(MessageFormat.format("文件 {0} 签发失败", new Object[] { docPostVO.getTitle() }));
      }
      String handler_ids = StringUtil.trim(docPostVO.getSignissuer_ids(), new String[] { "," });
      String[] arr_countsigner_ids = handler_ids.split(",");
      int log_count = this.dbUtil.queryForInt("select count(uuid) from oa_doc_log where ctype=? and doc_id=? and node_code=? and handler_id in (" + handler_ids + ")", 
        new Object[] { "docpost", docPostVO.getUuid(), Node.qf.name() });

      if (log_count == arr_countsigner_ids.length) {
        if (StringUtil.isBlank(docPostVO.getChecker_ids())) {
          docPostVO.setNode_code(Node.end.name());
          docPostVO.setNode_remark(MessageFormat.format("{0} 没有核阅人，发文办结", new Object[] { StringUtil.getCurDateTime() }));
          docPostVO.setDoc_no(genDocNo(docPostVO, userSession));

          DocPostFileVO docPostFileVO = createDocPostFile(docPostVO);
          this.dbUtil.insert(new Object[] { docPostFileVO });

          remaindEnd(docPostVO, userSession, false);
        } else {
          docPostVO.setNode_code(Node.hy.name());
          docPostVO.setNode_remark(MessageFormat.format("{0} 完成签发,进入核阅", new Object[] { StringUtil.getCurDateTime() }));
          remaindCheck(docPostVO, userSession, false);
        }
      }

      docPostVO = updateSignInfoContext(docPostVO, Node.qf);
      this.dbUtil.update(new Object[] { docPostVO });
    }
    catch (Exception e) {
      e.printStackTrace();
      result.add(MessageFormat.format("操作异常:{0}", new Object[] { e.getLocalizedMessage() }));
    }

    return result;
  }

  public List<String> doCounterSign(DocPostVO docPostVO, UserSession userSession, String remark)
  {
    List result = new ArrayList();

    int i = 0;
    DocLogVO log = new DocLogVO();
    try {
      if ((!"a".equals(docPostVO.getApply_type())) && (!StringUtil.isBlank(docPostVO.getCur_hq_id())) && (!userSession.getUserId().equals(docPostVO.getCur_hq_id()))) {
        UserVO userVO = (UserVO)this.dbUtil.load(UserVO.class, Integer.valueOf(Integer.parseInt(docPostVO.getCur_hq_id())));
        result.add(MessageFormat.format("你不属于当前会签人，不能进行会签 . 请联系 {0} 手机号码:{1}", new Object[] { userVO.getName(), userVO.getMobilePhone() }));
        return result;
      }

      log.setDoc_no(docPostVO.getDoc_no());
      log.setHandle_time(StringUtil.getCurDateTime());
      log.setHandler_id(userSession.getUserId());
      log.setHandler_name(userSession.getUserName());
      log.setNode(Node.hq);
      log.setUuid(UUID.randomUUID().toString());
      log.setRemark(remark);
      log.setCtype("docpost");
      log.setDoc_id(docPostVO.getUuid());
      i += this.dbUtil.insert(new Object[] { log });
      if (i == 1) {
        result.add(MessageFormat.format("文件 {0} 会签成功", new Object[] { docPostVO.getTitle() }));
        String handler_ids = StringUtil.trim(docPostVO.getCountersigner_ids(), new String[] { "," });
        handler_ids = StringUtil.trim(handler_ids, new String[] { "," });
        String[] arr_countsigner_ids = handler_ids.split(",");
        for (int j = 0; j < arr_countsigner_ids.length; j++) {
          if (userSession.getUserId().equals(arr_countsigner_ids[j])) {
            if (j == arr_countsigner_ids.length - 1)
              docPostVO.setCur_hq_id("");
            else {
              docPostVO.setCur_hq_id(arr_countsigner_ids[(j + 1)]);
            }
          }
        }
        int log_count = this.dbUtil.queryForInt("select count(uuid) from oa_doc_log where ctype=? and doc_id=? and node_code=? and handler_id in (" + handler_ids + ")", 
          new Object[] { "docpost", docPostVO.getUuid(), Node.hq.name() });
        if (log_count == arr_countsigner_ids.length) {
          if (!StringUtil.isBlank(docPostVO.getSignissuer_ids())) {
            docPostVO.setNode_code(Node.qf.name());

            docPostVO.setNode_remark(MessageFormat.format("{0} 完成会签,进入签发", new Object[] { StringUtil.getCurDateTime() }));
            remaindSignissuer(docPostVO, userSession, false);
          } else if (!StringUtil.isBlank(docPostVO.getChecker_ids())) {
            docPostVO.setNode_code(Node.hy.name());
            docPostVO.setNode_remark(MessageFormat.format("{0} 完成会签，没有签发人，进入核阅", new Object[] { StringUtil.getCurDateTime() }));
            remaindCheck(docPostVO, userSession, false);
          } else {
            docPostVO.setNode_code(Node.end.name());
            docPostVO.setNode_remark(MessageFormat.format("{0} 完成会签，没有签发人和核阅人，文件完结", new Object[] { StringUtil.getCurDateTime() }));
            remaindEnd(docPostVO, userSession, false);
          }
        }

        docPostVO = updateSignInfoContext(docPostVO, Node.hq);
        this.dbUtil.update(new Object[] { docPostVO });
        remaindCounterSigner(docPostVO, userSession, false);
      } else {
        result.add(MessageFormat.format("文号{0} 会签失败", new Object[] { docPostVO.getDoc_no() }));
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
      result.add(MessageFormat.format("操作异常:{0}", new Object[] { e.getLocalizedMessage() }));
    }

    return result;
  }

  public List<String> doCheck(DocPostVO docPostVO, UserSession userSession, String remark, boolean ischeck)
  {
    List result = new ArrayList();
    DbUtil dbUtil = null;

    int i = 0;
    DocLogVO log = new DocLogVO();
    try {
      this.conn = new DBConnect().getConnect();
      dbUtil = new DbUtil(this.conn);

      log.setDoc_no(docPostVO.getDoc_no());
      log.setHandle_time(StringUtil.getCurDateTime());
      log.setHandler_id(userSession.getUserId());
      log.setHandler_name(userSession.getUserName());
      log.setNode(Node.hy);
      log.setUuid(UUID.randomUUID().toString());
      log.setRemark(remark);
      log.setCtype("docpost");
      log.setDoc_id(docPostVO.getUuid());
      i += dbUtil.insert(new Object[] { log });
      if (i == 1) {
        if (ischeck)
          result.add(MessageFormat.format("文件  {0} 核阅并签字成功", new Object[] { docPostVO.getTitle() }));
        else
          result.add(MessageFormat.format("文件  {0} 确认不核阅", new Object[] { docPostVO.getTitle() }));
      }
      else {
        result.add(MessageFormat.format("文件  {0} 核阅失败", new Object[] { docPostVO.getTitle() }));
      }
      String handler_ids = StringUtil.trim(docPostVO.getChecker_ids(), new String[] { "," });
      String[] arr_countsigner_ids = handler_ids.split(",");
      int log_count = dbUtil.queryForInt("select count(uuid) from oa_doc_log where ctype=? and doc_id=? and node_code=? and handler_id in (" + handler_ids + ")", 
        new Object[] { "docpost", docPostVO.getUuid(), Node.hy.name() });
      if (log_count == arr_countsigner_ids.length) {
        docPostVO.setNode_code(Node.end.name());
        docPostVO.setNode_remark(MessageFormat.format("{0} 完成{1}", new Object[] { StringUtil.getCurDateTime(), ischeck ? "核阅并签字" : "确认不核阅" }));
        docPostVO.setDoc_no(genDocNo(docPostVO, userSession));
        remaindEnd(docPostVO, userSession, false);

        DocPostFileVO docPostFileVO = createDocPostFile(docPostVO);
        dbUtil.insert(new Object[] { docPostFileVO });
      }

      if (ischeck)
        docPostVO = updateSignInfoContext(docPostVO, Node.hy);
      else {
        docPostVO = updateSignInfoContext(docPostVO, Node.hy, "不核阅", "", true);
      }
      dbUtil.update(new Object[] { docPostVO });
    }
    catch (Exception e) {
      e.printStackTrace();
      result.add(MessageFormat.format("操作异常:{0}", new Object[] { e.getLocalizedMessage() }));
    }

    return result;
  }

  public static void main(String[] args)
  {
    Connection conn = null;
    DbUtil dbUtil = null;
    try {
      Class.forName(Driver.class.getName());

      System.out.println(StringUtil.getCurDateTime("yyyy-MM-dd HH:mm:ss", 900));
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  public List<String> doHandle(DocPostVO docPostVO, UserSession userSession, String remark)
  {
    StringBuffer sqlPattern = new StringBuffer();
    String sql = "";
    sqlPattern.append("UPDATE oa_doc_post SET ")
      .append("handler_ids=REPLACE(handler_ids,?,''), ")
      .append("handler_names=REPLACE(handler_names,?,'') ")
      .append(" where  doc_no=? and handler_ids like ?");

    sql = sqlPattern.toString();
    DbUtil dbUtil = null;

    DocFlowVO flow = null; DocFlowVO nextFlow = null;
    DocLogVO log = new DocLogVO();
    List results = new ArrayList();
    int i = 0;
    try {
      this.conn.setAutoCommit(false);
      dbUtil = new DbUtil(this.conn);
      PreparedStatement ps = this.conn.prepareStatement(sql);

      ps.setString(1, userSession.getUserId());
      ps.setString(2, userSession.getUserName());
      ps.setString(3, docPostVO.getDoc_no());
      ps.setString(4, "%" + userSession.getUserId() + "%");
      i += ps.executeUpdate();
      if (i == 1)
      {
        docPostVO = (DocPostVO)dbUtil.load(docPostVO, docPostVO.getUuid());
        flow = (DocFlowVO)dbUtil.load(DocFlowVO.class, "code", docPostVO.getNode_code());
        log.setDoc_no(docPostVO.getDoc_no());
        log.setHandle_time(StringUtil.getCurDateTime());
        log.setHandler_id(userSession.getUserId());
        log.setHandler_name(userSession.getUserName());
        log.setNode_code(flow.getCode());
        log.setNode_name(flow.getName());
        log.setUuid(UUID.randomUUID().toString());
        log.setRemark(remark);
        log.setCtype("发文");
        dbUtil.insert(new Object[] { log });
        if (docPostVO.getHandler_ids().replaceAll(",", "").trim().length() == 0) {
          flow = (DocFlowVO)dbUtil.load(DocFlowVO.class, "code", docPostVO.getNode_code());
          nextFlow = getNextNode(flow);
          if (nextFlow == null) {
            results.add("没有下一个节点");
            return results;
          }
          docPostVO.setNode_code(nextFlow.getCode());
          if ("hq".equals(nextFlow.getCode())) {
            docPostVO.setHandler_ids(docPostVO.getCountersigner_ids());
            docPostVO.setHandler_names(docPostVO.getCountersigner_names());
          } else if ("hy".equals(nextFlow.getCode())) {
            docPostVO.setHandler_ids(docPostVO.getCountersigner_ids());
            docPostVO.setHandler_names(docPostVO.getCountersigner_names());
          } else if ("hy".equals(nextFlow.getCode())) {
            docPostVO.setHandler_ids(docPostVO.getCountersigner_ids());
            docPostVO.setHandler_names(docPostVO.getCountersigner_names());
          }

          i += dbUtil.update(new Object[] { docPostVO });

          results.add("操作成功");
          this.conn.commit();
        }
      } else {
        results.add("你并不属于处理人");
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      results.add("操作异常:" + e.getLocalizedMessage());
    }
    return results;
  }

  public List<String> doFallBack(DocPostVO docPostVO, UserSession userSession, String remark) {
    StringBuffer sqlPattern = new StringBuffer();
    String sql = "";
    sqlPattern.append("UPDATE oa_doc_post SET ")
      .append("handler_ids=REPLACE(handler_ids,?,''), ")
      .append("handler_names=REPLACE(handler_names,?,'') ")
      .append(" where  doc_no=? and handler_ids like ?");

    sql = sqlPattern.toString();
    DbUtil dbUtil = null;

    DocFlowVO flow = null; DocFlowVO nextFlow = null;
    DocLogVO log = new DocLogVO();
    List results = new ArrayList();
    int i = 0;
    try {
      this.conn.setAutoCommit(false);
      dbUtil = new DbUtil(this.conn);
      PreparedStatement ps = this.conn.prepareStatement(sql);

      ps.setString(1, userSession.getUserId());
      ps.setString(2, userSession.getUserName());
      ps.setString(3, docPostVO.getDoc_no());
      ps.setString(4, "%" + userSession.getUserId() + "%");
      i += ps.executeUpdate();
      if (i == 1)
      {
        docPostVO = (DocPostVO)dbUtil.load(docPostVO, docPostVO.getUuid());
        flow = (DocFlowVO)dbUtil.load(DocFlowVO.class, "code", docPostVO.getNode_code());
        log.setDoc_no(docPostVO.getDoc_no());
        log.setHandle_time(StringUtil.getCurDateTime());
        log.setHandler_id(userSession.getUserId());
        log.setHandler_name(userSession.getUserName());
        log.setNode_code(flow.getCode());
        log.setNode_name(flow.getName());
        log.setUuid(UUID.randomUUID().toString());
        log.setRemark("");
        log.setCtype("发文");
        dbUtil.insert(new Object[] { log });
        if (docPostVO.getHandler_ids().replaceAll(",", "").trim().length() == 0) {
          flow = (DocFlowVO)dbUtil.load(DocFlowVO.class, "code", docPostVO.getNode_code());
          nextFlow = getNextNode(flow);
          if (nextFlow == null) {
            results.add("没有下一个节点");
            return results;
          }
          docPostVO.setNode_code(nextFlow.getCode());
          if ("hq".equals(nextFlow.getCode())) {
            docPostVO.setHandler_ids(docPostVO.getCountersigner_ids());
            docPostVO.setHandler_names(docPostVO.getCountersigner_names());
          } else if ("hy".equals(nextFlow.getCode())) {
            docPostVO.setHandler_ids(docPostVO.getCountersigner_ids());
            docPostVO.setHandler_names(docPostVO.getCountersigner_names());
          } else if ("hy".equals(nextFlow.getCode())) {
            docPostVO.setHandler_ids(docPostVO.getCountersigner_ids());
            docPostVO.setHandler_names(docPostVO.getCountersigner_names());
          }

          i += dbUtil.update(new Object[] { docPostVO });

          results.add("操作成功");
          this.conn.commit();
        }
      } else {
        results.add("你并不属于处理人");
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      results.add("操作异常:" + e.getLocalizedMessage());
    }
    return results;
  }

  public DocFlowVO getNextNode(DocFlowVO docFlowVO)
  {
    String sql = "SELECT a.* FROM oa_doc_flow a,(SELECT next_node_code FROM oa_doc_flow WHERE CODE=? and ctype=? )b WHERE a.CODE=b.next_node_code";

    PreparedStatement ps = null;
    ResultSet rs = null;
    DocFlowVO nextVo = null;
    try {
      ps = this.conn.prepareStatement(sql);
      ps.setString(1, docFlowVO.getCode());
      ps.setString(2, docFlowVO.getCtype());
      rs = ps.executeQuery();
      while (rs.next())
        nextVo = (DocFlowVO)DbUtil.evalObject(DocFlowVO.class, rs);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    } finally {
      DbUtil.close(null, ps, rs);
    }
    return nextVo;
  }

  public int getNextSeqNo() {
    int seqno = 0;
    String sql = "select doc_seq from oa_doc_post order by doc_seq asc";
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    try
    {
      preparedStatement = this.conn.prepareStatement(sql);
      resultSet = preparedStatement.executeQuery();
      while (resultSet.next()) {
        int i = resultSet.getInt("doc_seq");
        if (seqno != i)
          break;
        seqno++;
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    } finally {
      DbUtil.close(null, preparedStatement, resultSet);
    }

    return seqno;
  }

  public static AutoCodeVO toVO(AutocodeTable autocodeTable) {
    if (autocodeTable == null) return null;
    AutoCodeVO autoCodeVO = new AutoCodeVO();
    autoCodeVO.setAowner(autocodeTable.getAowner());
    autoCodeVO.setAtype(autocodeTable.getAtype());
    autoCodeVO.setCurNum1(Integer.valueOf(autocodeTable.getCurnum1()));
    autoCodeVO.setCurNum2(Integer.valueOf(autocodeTable.getCurnum2()));
    autoCodeVO.setCurNum3(Integer.valueOf(autocodeTable.getCurnum3()));
    autoCodeVO.setFormat(autocodeTable.getFormat());
    autoCodeVO.setId(Integer.valueOf(autocodeTable.getId()));
    autoCodeVO.setShowlen1(Integer.valueOf(autocodeTable.getShowlen1()));
    autoCodeVO.setShowlen2(Integer.valueOf(autocodeTable.getShowlen2()));
    autoCodeVO.setShowlen3(Integer.valueOf(autocodeTable.getShowlen3()));
    return autoCodeVO;
  }

  public AutoCodeUsedVO doBookCode(UserSession userSession, AutoCodeVO autoCodeVO) {
    AutoCodeUsedVO autoCodeUsedVO = null;
    DELAutocode delAutocode = DELAutocode.getInstant();
    try {
      List autoCodeUsedVOs = null;

      autoCodeUsedVOs = this.dbUtil.select(AutoCodeUsedVO.class, 
        "select * from {0} where atype=? and year=? and state=? order by number asc", new Object[] { 
        autoCodeVO.getAtype(), 
        StringUtil.getCurYear(), 
        Integer.valueOf(0) });

      if (autoCodeUsedVOs.size() == 1) {
        autoCodeUsedVO = (AutoCodeUsedVO)autoCodeUsedVOs.get(0);
        autoCodeUsedVO.setAbandonuser(userSession.getUserId());
        autoCodeUsedVO.setState(Integer.valueOf(1));
        autoCodeUsedVO.setAbandondate(StringUtil.getCurDateTime());
        this.dbUtil.update(new Object[] { autoCodeUsedVO });
        return autoCodeUsedVO;
      }
      String fullnum = delAutocode.getAutoCode(autoCodeVO.getAtype(), "all");
      autoCodeUsedVOs = this.dbUtil.select(AutoCodeUsedVO.class, 
        "select * from {0} where atype=? and year=? and state=? order by number asc", new Object[] { 
        autoCodeVO.getAtype(), 
        StringUtil.getCurYear(), 
        Integer.valueOf(0) });

      if (autoCodeUsedVOs.size() == 1) {
        autoCodeUsedVO = (AutoCodeUsedVO)autoCodeUsedVOs.get(0);
        autoCodeUsedVO.setAbandonuser(userSession.getUserId());
        autoCodeUsedVO.setState(Integer.valueOf(1));
        autoCodeUsedVO.setAbandondate(StringUtil.getCurDateTime());
        this.dbUtil.update(new Object[] { autoCodeUsedVO });
      } else {
        throw new Exception(MessageFormat.format("文号 类型 {0} 无法预定", new Object[] { autoCodeVO.getAtype() }));
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
    return autoCodeUsedVO;
  }

  public int doEnd(DocPostVO docPostVO)
  {
    return 0;
  }

  public List<String> doProjectSign(DocPostVO docPostVO, UserSession userSession, String remark)
  {
    List result = new ArrayList();

    int i = 0;
    DocLogVO log = new DocLogVO();
    try
    {
      log.setDoc_no(docPostVO.getDoc_no());
      log.setHandle_time(StringUtil.getCurDateTime());
      log.setHandler_id(userSession.getUserId());
      log.setHandler_name(userSession.getUserName());
      log.setNode(Node.xm);
      log.setUuid(UUID.randomUUID().toString());
      log.setRemark(remark);
      log.setCtype("docpost");
      log.setDoc_id(docPostVO.getUuid());
      i += this.dbUtil.insert(new Object[] { log });
      if (i == 1)
        result.add(MessageFormat.format("文号{0} 项目人员确认成功", new Object[] { docPostVO.getDoc_no() }));
      else {
        result.add(MessageFormat.format("文号{0} 项目人员确认失败", new Object[] { docPostVO.getDoc_no() }));
      }
      String handler_ids = StringUtil.trim(docPostVO.getProject_member_ids(), new String[] { "," });
      String[] arr_project_ids = handler_ids.split(",");
      int log_count = this.dbUtil.queryForInt("select count(uuid) from oa_doc_log where ctype=? and doc_id=? and node_code=? and handler_id in (" + handler_ids + ")", 
        new Object[] { "docpost", docPostVO.getUuid(), Node.xm.name() });
      if (log_count == arr_project_ids.length) {
        docPostVO.setNode_code(Node.hq.name());
        docPostVO.setNode_remark(MessageFormat.format("{0} 完成项目相关签字,进入会签", new Object[] { StringUtil.getCurDateTime() }));
        remaindCounterSigner(docPostVO, userSession, false);
      }
      if (StringUtil.isBlank(docPostVO.getCountersigner_ids())) {
        docPostVO.setNode_code(Node.qf.name());
        docPostVO.setNode_remark(MessageFormat.format("{0} 无会签人，直接进入签发", new Object[] { StringUtil.getCurDateTime() }));
        remaindSignissuer(docPostVO, userSession, false);
      }
      docPostVO = updateSignInfoContext(docPostVO, Node.xm);
      this.dbUtil.update(new Object[] { docPostVO });
    }
    catch (Exception e) {
      e.printStackTrace();
      result.add(MessageFormat.format("操作异常:{0}", new Object[] { e.getLocalizedMessage() }));
    }

    return result;
  }

  public DocPostVO updateSignInfoContext(DocPostVO docPostVO, Node node) {
    return updateSignInfoContext(docPostVO, node, "已签", "未签", false);
  }

  public DocPostVO updateSignInfoContext(DocPostVO docPostVO, Node node, String signName, String unsignName, boolean isForce) {
    int eff = 0;
    String context = "";
    String contextName = "";
    String names = ""; String ids = "";

    List<DocLogVO> docLogVOs = this.dbUtil.select(DocLogVO.class, 
      "select * from {0} where node_code=? and doc_id=?", new Object[] { node.name(), docPostVO.getUuid() });

    switch (node) {
    case hq:
      names = docPostVO.getCountersigner_names();
      ids = docPostVO.getCountersigner_ids();

      break;
    case fq:
      names = docPostVO.getProject_member_names();
      ids = docPostVO.getProject_member_ids();
      break;
    case hy:
      names = docPostVO.getSignissuer_names();
      ids = docPostVO.getSignissuer_ids();
      break;
    case qf:
      names = docPostVO.getChecker_names();
      ids = docPostVO.getChecker_ids();
      break;
    }

    String[] arr_ids = ids.split(",");
    if ((Node.hq == node) && (arr_ids.length > 0)) {
      docPostVO.setCur_hq_id(arr_ids[0]);
    }
    boolean isFirstSign = false;

    for (int i = 0; i < arr_ids.length; i++)
    {
      String id = arr_ids[i];
      if (!StringUtil.isBlank(id)) {
        UserVO userVO = null;
        try {
          userVO = (UserVO)this.dbUtil.load(UserVO.class, Integer.valueOf(Integer.parseInt(id))); } catch (Exception ex) {
          continue;
        }boolean isSigned = false;
        for (DocLogVO docLogVO : docLogVOs)
        {
          if (id.equals(docLogVO.getHandler_id())) {
            isSigned = true;
            break;
          }

        }

        if ((!isFirstSign) && (!isSigned) && (node == Node.hq)) {
          docPostVO.setCur_hq_id(id);
          isFirstSign = true;
        }
        if (isForce) {
          context = context + userVO.getName() + "(" + signName + "),";
          contextName = contextName + userVO.getName() + ",";
        } else {
          context = context + userVO.getName() + "(" + (isSigned ? signName : unsignName) + "),";
          contextName = contextName + userVO.getName() + ",";
        }
      }
    }
    if ((!isFirstSign) && (node == Node.hq)) {
      docPostVO.setCur_hq_id("");
    }
    switch (node) {
    case hq:
      docPostVO.setCountersign_info(context);
      docPostVO.setCountersigner_names(contextName);
      break;
    case fq:
      docPostVO.setProject_member_sign_info(context);
      break;
    case hy:
      docPostVO.setSignissuer_info(context);
      break;
    case qf:
      docPostVO.setCheck_info(context);
      break;
    }

    return docPostVO;
  }

  public String createDocNo(DocPostVO docPostVO, UserSession userSession) {
    String docno = "";
    KDepartmentVO kDepartmentVO = null;
    KAreaVO kAreaVO = null;
    try {
      kDepartmentVO = (KDepartmentVO)this.dbUtil.load(KDepartmentVO.class, docPostVO.getDepartmentid());
      kAreaVO = (KAreaVO)this.dbUtil.load(KAreaVO.class, kDepartmentVO.getAreaid());
      kAreaVO.getAutoid().intValue();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return docno;
  }

  public int remaindStart(DocPostVO docPostVO, UserSession userSession)
  {
    int eff = 0;

    if (eff == 0) {
      eff += remaindCounterSigner(docPostVO, userSession, false);
    }
    if (eff == 0) {
      eff += remaindSignissuer(docPostVO, userSession, false);
    }
    if (eff == 0) {
      eff += remaindCheck(docPostVO, userSession, false);
    }

    return eff;
  }

  public int remaindEnd(DocPostVO docPostVO, UserSession userSession, boolean isTimeout) {
    int eff = 0;

    if ((eff == 0) && 
      (isTimeout)) {
      eff += remaindCounterSigner(docPostVO, userSession, isTimeout);
    }

    if ((eff == 0) && 
      (isTimeout)) {
      eff += remaindSignissuer(docPostVO, userSession, isTimeout);
    }

    remaindCreater(docPostVO, userSession, isTimeout);
    return eff;
  }

  public int remaindPrjectMember(DocPostVO docPostVO, UserSession userSession, boolean isTimeout)
  {
    int eff = 0;
    String ids = docPostVO.getProject_member_ids();
    if (StringUtil.isBlank(ids)) {
      return eff;
    }

    if (docPostVO.getDel_ind().intValue() == 1)
    {
      return eff;
    }

    ids = StringUtil.trim(ids, new String[] { "," });
    List<UserVO> userVOs = this.dbUtil.select(UserVO.class, 
      "select a.* from {0} a where a.id in (" + ids + ") and (select count(uuid) from oa_doc_log where doc_id=? and node_code=? and handler_id=a.id)<1 ", new Object[] { 
      docPostVO.getUuid(), Node.xm.name() });

    String contextPattern = "{0} 您好，{1} 于 {2} 发起了 {3}发文 {4}，请相关项目人员及时进行确认";
    if (isTimeout) {
      contextPattern = "{0} 您好，{1} 于 {2} 发起了 {3}发文 {4}，你没有及时进行确认";
    }
    String ctype_name = "a".equals(docPostVO.getCtype()) ? "行政" : "业务";
    for (UserVO userVO : userVOs) {
      String context = MessageFormat.format(contextPattern, new Object[] { 
        userVO.getName(), 
        docPostVO.getCreater_names(), 
        docPostVO.getCreate_date(), 
        ctype_name, 
        docPostVO.getTitle() });
      try
      {
        PlacardTable placardTable = new PlacardTable();
        placardTable.setIsReversion(0);
        placardTable.setAddresserTime(StringUtil.getCurDateTime());
        placardTable.setCaption(ctype_name + "发文 相关项目人员提醒");
        placardTable.setMatter(context);
        placardTable.setIsRead(0);
        placardTable.setIsNotReversion(0);
        placardTable.setUuid(UUID.randomUUID().toString());
        placardTable.setUrl(FormDefineAction.pathFormListExtView("0bad72f7-636f-42cb-bf72-1eda086b3836", "06"));
        placardTable.setUuidName("uuid");
        placardTable.setModel("相关项目人员确认");
        placardTable.setAddresser(docPostVO.getCreater_ids());
        placardTable.setAddressee(String.valueOf(userVO.getId()));
        this.placardService.AddPlacard(placardTable);
        eff++; } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    return eff;
  }

  public int remaindCreater(DocPostVO docPostVO, UserSession userSession, boolean isTimeout)
  {
    int eff = 0;

    if (docPostVO.getDel_ind().intValue() == 1)
    {
      return eff;
    }

    String contextPattern = "您于 {0} 提交的《{1}》文件签转流程结束，请尽快查阅。";
    String subfix = "end";
    String time = StringUtil.getCurDateTime();
    if (isTimeout) {
      contextPattern = "您于 {0} 提交的《{1}》文件讲于今天到期，但签转流程尚未结束，请处理。";
      subfix = "timeout";
      time = StringUtil.getCurDate() + " 08:30:00";
    }
    String cdate = StringUtil.showDate(docPostVO.getCreate_date(), "showDate:yyyy年MM月dd日");
    UserVO createrVO = null;
    String context = "";
    String applyName = "a".equals(docPostVO.getApply_type()) ? "行政" : "业务";
    try {
      createrVO = (UserVO)this.dbUtil.load(UserVO.class, docPostVO.getCreater_ids());
      context = MessageFormat.format(contextPattern, new Object[] { cdate, docPostVO.getTitle() });
      PlacardTable placardTable = new PlacardTable();
      placardTable.setIsReversion(0);
      placardTable.setAddresserTime(StringUtil.getCurDateTime());
      placardTable.setCaption(applyName + "文件 " + docPostVO.getTitle());
      placardTable.setMatter(context);
      placardTable.setIsRead(0);
      placardTable.setIsNotReversion(0);
      placardTable.setUuid(UUID.randomUUID().toString());
      placardTable.setUrl(FormDefineAction.pathFormListExtView("0bad72f7-636f-42cb-bf72-1eda086b3836", "00") + "&ctype=" + docPostVO.getApply_type());
      placardTable.setUuidName("uuid");
      placardTable.setModel(isTimeout ? "文件过期" : "文件结束");
      placardTable.setAddresser(docPostVO.getCreater_ids());
      placardTable.setAddressee(docPostVO.getCreater_ids());
      this.placardService.AddPlacard(placardTable);
      SmsOpt.sendSm(createrVO.getMobilePhone(), context, docPostVO.getUuid() + ":" + subfix, time);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return eff;
  }

  public int remaindCounterSigner(DocPostVO docPostVO, UserSession userSession, boolean isTimeout)
  {
    int eff = 0;
    String ids = docPostVO.getCountersigner_ids();

    if (StringUtil.isBlank(ids)) {
      System.out.println("====================qwh:ids is null");
      return eff;
    }
    System.out.println("====================qwh:ids is not null");
    ids = StringUtil.trim(ids, new String[] { "," });
    List<UserVO> userVOs = this.dbUtil.select(UserVO.class, 
      "select a.* from {0} a where a.id in (" + ids + ") and (select count(uuid) from oa_doc_log where doc_id=? and node_code=? and handler_id=a.id)<1 ", new Object[] { 
      docPostVO.getUuid(), Node.hq.name() });

    String contextPattern = "{0}{4} {1} 于{2} 提交的《{3}》文件需要您签字。请尽快处理，谢谢!";
    String subfix = "hq";
    String time = StringUtil.getCurDateTime("yyyy-MM-dd HH:mm:ss", 900);
    if (isTimeout)
    {
      contextPattern = "{0}{4} {1} 于{2} 提交的 《{3}》 文件将于今日到期。文件需要您签字，请尽快 办理，谢谢!";
      subfix = "hqtimeout";

      time = StringUtil.getCurDate() + " 08:30:00";
    }
    String ctype_name = "a".equals(docPostVO.getCtype()) ? "行政" : "业务";
    UserVO createrVO = null;
    KDepartmentVO departmentVO = null;
    KAreaVO kAreaVO = null;
    String cdate = "";
    try {
      createrVO = (UserVO)this.dbUtil.load(UserVO.class, Integer.valueOf(Integer.parseInt(docPostVO.getCreater_ids())));
      departmentVO = (KDepartmentVO)this.dbUtil.load(KDepartmentVO.class, Integer.valueOf(Integer.parseInt(createrVO.getDepartmentid())));
      kAreaVO = (KAreaVO)this.dbUtil.load(KAreaVO.class, Integer.valueOf(Integer.parseInt(departmentVO.getAreaid())));
      cdate = StringUtil.showDate(docPostVO.getCreate_date(), "showDate:yyyy年MM月dd日");
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    for (UserVO userVO : userVOs)
      if (StringUtil.isIn(docPostVO.getApply_type(), new String[] { "b" })) { if (!StringUtil.isIn(docPostVO.getCur_hq_id(), new String[] { String.valueOf(userVO.getId()) })) continue; } else {
        String context = MessageFormat.format(contextPattern, new Object[] { 
          kAreaVO.getName(), 
          createrVO.getName(), 
          cdate, 
          docPostVO.getTitle(), 
          departmentVO.getDepartname() });
        try
        {
          PlacardTable placardTable = new PlacardTable();
          placardTable.setIsReversion(0);
          placardTable.setAddresserTime(StringUtil.getCurDateTime());
          placardTable.setCaption(ctype_name + "发文  会签人员提醒");
          placardTable.setMatter(context);
          placardTable.setIsRead(0);
          placardTable.setIsNotReversion(0);
          placardTable.setUuid(UUID.randomUUID().toString());
          placardTable.setUrl(FormDefineAction.pathFormListExtView("0bad72f7-636f-42cb-bf72-1eda086b3836", "01"));
          placardTable.setUuidName("uuid");
          placardTable.setModel("发文会签");
          placardTable.setAddresser(docPostVO.getCreater_ids());
          placardTable.setAddressee(String.valueOf(userVO.getId()));

          eff++;

          System.out.println("==================time=" + time);
          SmsOpt.sendSm(userVO.getMobilePhone(), context, docPostVO.getUuid() + ":" + subfix, time);
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    return eff;
  }

  public int remaindSignissuer(DocPostVO docPostVO, UserSession userSession, boolean isTimeout)
  {
    int eff = 0;
    String ids = docPostVO.getSignissuer_ids();
    if (StringUtil.isBlank(ids)) {
      return eff;
    }

    if (docPostVO.getDel_ind().intValue() == 1)
    {
      return eff;
    }

    ids = StringUtil.trim(ids, new String[] { "," });
    List<UserVO> userVOs = this.dbUtil.select(UserVO.class, 
      "select a.* from {0} a where a.id in (" + ids + ") and (select count(uuid) from oa_doc_log where doc_id=? and node_code=? and handler_id=a.id)<1 ", new Object[] { 
      docPostVO.getUuid(), Node.qf.name() });

    String contextPattern = "{0}{4} {1} 于{2} 提交的《{3}》文件需要您签字。请尽快处理，谢谢!";
    String subfix = "qf";
    String time = StringUtil.getCurDateTime();
    if (isTimeout) {
      contextPattern = "{0}{4} {1} 于{2} 提交的 《{3}》 文件将于今日到期。文件需要您签字，请尽快 办理。谢谢!";
      subfix = "qftimeout";

      time = StringUtil.getCurDate() + " 09:00:00";
    }
    UserVO createrVO = null;
    KDepartmentVO departmentVO = null;
    KAreaVO kAreaVO = null;
    String cdate = "";
    String ctype_name = "a".equals(docPostVO.getCtype()) ? "行政" : "业务";
    for (UserVO userVO : userVOs)
      try
      {
        createrVO = (UserVO)this.dbUtil.load(UserVO.class, Integer.valueOf(Integer.parseInt(docPostVO.getCreater_ids())));
        departmentVO = (KDepartmentVO)this.dbUtil.load(KDepartmentVO.class, Integer.valueOf(Integer.parseInt(createrVO.getDepartmentid())));
        kAreaVO = (KAreaVO)this.dbUtil.load(KAreaVO.class, Integer.valueOf(Integer.parseInt(departmentVO.getAreaid())));
        cdate = StringUtil.showDate(docPostVO.getCreate_date(), "showDate:yyyy年MM月dd日");
        String context = MessageFormat.format(contextPattern, new Object[] { 
          kAreaVO.getName(), 
          createrVO.getName(), 
          cdate, 
          docPostVO.getTitle(), 
          departmentVO.getDepartname() });

        PlacardTable placardTable = new PlacardTable();
        placardTable.setIsReversion(0);
        placardTable.setAddresserTime(StringUtil.getCurDateTime());
        placardTable.setCaption(ctype_name + "发文  签发人员提醒");
        placardTable.setMatter(context);
        placardTable.setIsRead(0);
        placardTable.setIsNotReversion(0);
        placardTable.setUuid(UUID.randomUUID().toString());
        placardTable.setUrl(FormDefineAction.pathFormListExtView("0bad72f7-636f-42cb-bf72-1eda086b3836", "02"));
        placardTable.setUuidName("uuid");
        placardTable.setModel("发文签发");
        placardTable.setAddresser(docPostVO.getCreater_ids());
        placardTable.setAddressee(String.valueOf(userVO.getId()));

        eff++;

        SmsOpt.sendSm(userVO.getMobilePhone(), context, docPostVO.getUuid() + ":" + subfix, time); } catch (Exception ex) {
        ex.printStackTrace();
      }
    return eff;
  }

  public int remaindCheck(DocPostVO docPostVO, UserSession userSession, boolean isTimeout)
  {
    int eff = 0;
    String ids = docPostVO.getChecker_ids();
    if (StringUtil.isBlank(ids)) {
      return eff;
    }

    if (docPostVO.getDel_ind().intValue() == 1)
    {
      return eff;
    }

    ids = StringUtil.trim(ids, new String[] { "," });
    List<UserVO> userVOs = this.dbUtil.select(UserVO.class, 
      "select a.* from {0} a where a.id in (" + ids + ") and (select count(uuid) from oa_doc_log where doc_id=? and node_code=? and handler_id=a.id)<1 ", new Object[] { 
      docPostVO.getUuid(), Node.hy.name() });

    String contextPattern = "{0}{4} {1} 于{2} 提交的《{3}》文件需要您签字。请尽快处理，谢谢!";
    String subfix = "hy";
    String time = StringUtil.getCurDateTime();
    if (isTimeout) {
      contextPattern = "{0}{4} {1} 于{2} 提交的 《{3}》 文件将于今日到期。文件需要您签字，请尽快 办理，谢谢!";
      subfix = "timeouthy";
      time = StringUtil.getCurDate() + " 09:30:00";
    }
    UserVO createrVO = null;
    KDepartmentVO departmentVO = null;
    KAreaVO kAreaVO = null;
    String cdate = "";
    String ctype_name = "a".equals(docPostVO.getCtype()) ? "行政" : "业务";
    for (UserVO userVO : userVOs)
      try
      {
        createrVO = (UserVO)this.dbUtil.load(UserVO.class, Integer.valueOf(Integer.parseInt(docPostVO.getCreater_ids())));
        departmentVO = (KDepartmentVO)this.dbUtil.load(KDepartmentVO.class, Integer.valueOf(Integer.parseInt(createrVO.getDepartmentid())));
        kAreaVO = (KAreaVO)this.dbUtil.load(KAreaVO.class, Integer.valueOf(Integer.parseInt(departmentVO.getAreaid())));
        cdate = StringUtil.showDate(docPostVO.getCreate_date(), "showDate:yyyy年MM月dd日");
        String context = MessageFormat.format(contextPattern, new Object[] { 
          kAreaVO.getName(), 
          createrVO.getName(), 
          cdate, 
          docPostVO.getTitle(), 
          departmentVO.getDepartname() });

        PlacardTable placardTable = new PlacardTable();
        placardTable.setIsReversion(0);
        placardTable.setAddresserTime(StringUtil.getCurDateTime());
        placardTable.setCaption(ctype_name + "发文  核阅人员提醒");
        placardTable.setMatter(context);
        placardTable.setIsRead(0);
        placardTable.setIsNotReversion(0);
        placardTable.setUuid(UUID.randomUUID().toString());
        placardTable.setUrl(FormDefineAction.pathFormListExtView("0bad72f7-636f-42cb-bf72-1eda086b3836", "03"));
        placardTable.setUuidName("uuid");
        placardTable.setModel("发文核阅");
        placardTable.setAddresser(docPostVO.getCreater_ids());
        placardTable.setAddressee(String.valueOf(userVO.getId()));

        eff++;

        SmsOpt.sendSm(userVO.getMobilePhone(), context, docPostVO.getUuid() + ":" + subfix, time); } catch (Exception ex) {
        ex.printStackTrace();
      }
    return eff;
  }

  public String genDocNo(DocPostVO docPostVO, UserSession userSession) {
    String doc_no = "";
    KDepartmentVO kDepartmentVO = null;
    KAreaVO kAreaVO = null;
    String autoCode_name = "";
    try
    {
      doc_no = this.delAutocode.getAutoCode(docPostVO.getDoc_type(), "all", new String[0], this.conn, true);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return doc_no;
  }

  public DocPostFileVO createDocPostFile(DocPostVO docPostVO)
  {
    DocPostFileVO docPostFileVO = new DocPostFileVO();

    docPostFileVO.setModify_date(StringUtil.getCurDate());
    docPostFileVO.setCreater_id(docPostVO.getUserid());
    docPostFileVO.setDel_ind(Integer.valueOf(0));
    docPostFileVO.setDepartmentid(docPostVO.getDepartmentid());
    docPostFileVO.setDoc_id(docPostVO.getUuid());
    docPostFileVO.setDoc_no(docPostVO.getDoc_no());
    docPostFileVO.setDoc_type(docPostVO.getDoc_type());
    docPostFileVO.setFile_count(docPostVO.getFile_total_count());
    docPostFileVO.setBeaccount_addr(docPostVO.getBeaccount_addr());
    if (!StringUtil.isBlank(docPostVO.getCountersigner_ids())) {
      String[] arr_hq_ids = docPostVO.getCountersigner_ids().split(",");
      if (arr_hq_ids.length >= 1) {
        docPostFileVO.setHq_id_1(arr_hq_ids[0]);
      }
      if (arr_hq_ids.length >= 2) {
        docPostFileVO.setHq_id_2(arr_hq_ids[1]);
      }
      if (arr_hq_ids.length >= 3) {
        docPostFileVO.setHq_id_3(arr_hq_ids[2]);
      }
      if (arr_hq_ids.length >= 4) {
        docPostFileVO.setHq_id_4(arr_hq_ids[3]);
      }
    }
    docPostFileVO.setHy_id(docPostVO.getChecker_ids());
    docPostFileVO.setPost_addr(docPostVO.getPost_addr_names());
    docPostFileVO.setQf_id(docPostVO.getSignissuer_ids());
    docPostFileVO.setTitle(docPostVO.getTitle());
    docPostFileVO.setUserid(docPostVO.getUserid());
    docPostFileVO.setUuid(UUID.randomUUID().toString());
    docPostFileVO.setRemark(docPostVO.getNode_remark());
    try {
      AutoCodeUsedVO autoCodeUsedVO = (AutoCodeUsedVO)this.dbUtil.select(AutoCodeUsedVO.class, "select * from {0} where atype=? and fullnumber=? ", new Object[] { docPostVO.getDoc_type(), docPostVO.getDoc_no() }).get(0);
      docPostFileVO.setDoc_year(autoCodeUsedVO.getYear().toString());
      docPostFileVO.setDoc_seqno(autoCodeUsedVO.getNumber());
    }
    catch (Exception localException) {
    }
    return docPostFileVO;
  }

  public int remaindTimeoutCreater(DocPostVO docPostVO) {
    int eff = 0;
    SimpleDateFormat smdf = new SimpleDateFormat("yyyy-MM-dd");
    Date sdate = null;
    Date tdate = null;
    Date ndate = null;
    if (StringUtil.isIn(docPostVO.getNode_code(), new String[] { Node.end.name() })) {
      return eff;
    }
    try
    {
      sdate = smdf.parse(docPostVO.getSignissue_date());

      tdate = smdf.parse(docPostVO.getTimeout_date());
      ndate = smdf.parse(StringUtil.getCurDate());
      if (tdate.compareTo(ndate) == 0)
      {
        remaindCreater(docPostVO, null, true);
      } else sdate.compareTo(ndate);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }

    return eff;
  }

  public int remaindTimeoutSigner(DocPostVO docPostVO)
  {
    int eff = 0;
    SimpleDateFormat smdf = new SimpleDateFormat("yyyy-MM-dd");
    Date sdate = null;
    Date tdate = null;
    Date ndate = null,hdate=null;
    if (StringUtil.isIn(docPostVO.getNode_code(), new String[] { Node.end.name() })) {
      return eff;
    }
    try
    {
      sdate = smdf.parse(docPostVO.getSignissue_date());
      hdate = smdf.parse(docPostVO.getHq_date());
      tdate = smdf.parse(docPostVO.getTimeout_date());
      ndate = smdf.parse(StringUtil.getCurDate());
      if (hdate.compareTo(ndate) == 0) {
    	  remaindCounterSigner(docPostVO, null, true);
      }
      else if (tdate.compareTo(ndate) == 0) {
        remaindCounterSigner(docPostVO, null, true);
        remaindSignissuer(docPostVO, null, true);
        remaindCheck(docPostVO, null, true);
      }
      else if (sdate.compareTo(ndate) == 0) {
        remaindCounterSigner(docPostVO, null, true);
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    return eff;
  }

  public static enum Node
  {
    fq("发起"), xm("项目相关签字"), hq("会签"), qf("签发"), hy("核阅"), end("完结");

    private String name_cn;

    private Node(String name_cn) { this.name_cn = name_cn; }

    public String getName_cn()
    {
      return this.name_cn;
    }

    public Node next() {
      switch (this) { case end:
        return hq;
      case hq:
        return qf;
      case hy:
        return hy;
      case fq:
      }
      return null;
    }
  }
}