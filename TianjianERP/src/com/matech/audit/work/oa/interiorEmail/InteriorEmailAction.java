package com.matech.audit.work.oa.interiorEmail;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.func.TagsChecker;
import com.matech.audit.service.attachFileUploadService.AttachService;
import com.matech.audit.service.department.DepartmentService;
import com.matech.audit.service.enterpriseQualification.EnterpriseQualificationService;
import com.matech.audit.service.oa.interiorEmail.InteriorEmailService;
import com.matech.audit.service.oa.interiorEmail.model.Email;
import com.matech.audit.service.oa.interiorEmail.model.EmailUser;
import com.matech.audit.service.placard.PlacardService;
import com.matech.audit.service.placard.model.PlacardTable;
import com.matech.audit.service.role.model.RoleTable;
import com.matech.audit.service.user.UserService;
import com.matech.audit.service.user.model.User;
import com.matech.audit.service.userpopedom.UserPopedomService;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DBConnect;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.sms.SmsService;

/**
 * @author ymm
 *内部邮箱
 */
public class InteriorEmailAction extends MultiActionController{
	
	
	//private static final String EPISTOLIZE = "oa/interiorEmail/epistolize.jsp";
	private static final String EPISTOLIZE = "oa/interiorEmail/epistolizeShow.jsp";
	private static final String EPISTOLIZES = "oa/interiorEmail/resumEmail.jsp";
	private static final String ADDRESSEELIST = "oa/interiorEmail/addresseeList.jsp"; //收件箱
	private static final String SENDLIST = "oa/interiorEmail/sendList.jsp"; //发件箱
	private static final String emailMain = "oa/interiorEmail/emailMain.jsp"; //mian
	private static final String REPLYSKIP = "oa/interiorEmail/epistolizeShow.jsp"; //回复跳转
	private static final String READ = "oa/interiorEmail/read.jsp"; //阅读
	private static final String DRAFTLIST = "oa/interiorEmail/draftList.jsp"; //草稿箱list
	private static final String DELETELIST = "oa/interiorEmail/deleteList.jsp"; //已删除
	private static final String DRAFTEDIT = "oa/interiorEmail/draftEdit.jsp"; //草稿跳转
	private static final String INDEXPAGE = "email"; // 附件名
	private static final String READUSER = "oa/interiorEmail/readUser.jsp"; // 附件名
	

	/**
	 * main
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView emailMain(HttpServletRequest request, HttpServletResponse response){

		ModelAndView modelAndView = new ModelAndView(emailMain);
		String isReadOnly=request.getParameter("isReadOnly");
		String back=request.getParameter("back");
		String uuidName=request.getParameter("uuid");
		modelAndView.addObject("isReadOnly", isReadOnly);
		modelAndView.addObject("uuidName", uuidName);
		modelAndView.addObject("back", back);
		Connection conn = null;
		try {
				conn = new DBConnect().getConnect();
				InteriorEmailService interiorEmailService = new InteriorEmailService(conn);
				UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
				
				modelAndView.addObject("notGet",getIsRead(userSession)); //未读
				
				String sql="SELECT COUNT(DISTINCT a.uuid) AS sumCount FROM `oa_emailuser` a " +
							" left join oa_email b on a.uuid = b.uuid "+
							"WHERE a.userId='"+userSession.getUserId()+"' " +
							" AND (a.ctype='收件人' or a.ctype='抄送人' or a.ctype='密送人')" +
							" AND a.dustbin = '否' AND b.`status` <> '已删除' AND b.`status`<>'已撤销' ";
				String addresseeCounts = interiorEmailService.getValueBySql(sql);
				addresseeCounts = this.setStringName(addresseeCounts, "0"); 
				modelAndView.addObject("addresseeCount", addresseeCounts);

				sql="SELECT COUNT(*) AS sumCount FROM `oa_emaildraft` WHERE addresser='"+userSession.getUserId()+"'";
				String draftCounts = interiorEmailService.getValueBySql(sql);
				draftCounts = this.setStringName(draftCounts, "0"); 
				modelAndView.addObject("draftCount", draftCounts);
				
				
				sql="SELECT COUNT(*) AS sumCount FROM `oa_email` WHERE addresser='"+userSession.getUserId()+"'and status !='已撤销' and status !='已删除' ";
				String alreadyCounts = interiorEmailService.getValueBySql(sql);
				alreadyCounts = this.setStringName(alreadyCounts, "0"); 
				modelAndView.addObject("alreadyCount", alreadyCounts);
				
				sql="SELECT COUNT(DISTINCT a.uuid) AS sumCount" +
						" FROM `oa_emailuser` a " +
						" WHERE userId='"+userSession.getUserId()+"' AND dustbin='是' and (a.ctype='收件人' or a.ctype='抄送人' or a.ctype='密送人') ";
				String delCounts = interiorEmailService.getValueBySql(sql);
				delCounts = this.setStringName(delCounts, "0"); 
				modelAndView.addObject("delCount", delCounts);
				
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return modelAndView;
	}
		
	/**
	 * 收件箱LIST
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView addresseeList(HttpServletRequest request, HttpServletResponse response){
		Connection conn = null;
		ModelAndView modelAndView = new ModelAndView(ADDRESSEELIST);
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		DataGridProperty pp = new DataGridProperty(); 
		
		ASFuntion asf = new ASFuntion();
		String ifRead = asf.showNull(request.getParameter("ifRead"));
		try {	
			/*String sql="SELECT a.`uuid`,if(d.readTime='0',Concat('<b>',b.`Name`,'</b>'),b.`Name`) AS addresser,\n" 
							+" if(d.readTime='0',Concat('<b>',a.`title`,'</b>'),a.title) as title, \n" 
							+" if(d.readTime='0',Concat('<b>',a.`sendDate`,'</b>'),a.sendDate) as sendDate, a.`property`, \n" 
							+" if(a.`importance` = '重要邮件',concat('<font color=#FF8C00>',a.`importance`,'</font>'), \n"
							+"if(a.`importance` ='非常重要',concat('<font color=#FF0000><b>',a.`importance`,'</b></font>'),a.`importance`)) AS importance, c.*    \n"
							+"FROM oa_email a \n"
							+"LEFT JOIN k_user b ON a.`addresser` = b.`id` \n"
							+"LEFT JOIN(  \n"
							+"	SELECT `indexid`,REPLACE(GROUP_CONCAT('<a href=\"common.do?method=attachDownload&attachId=',c.`attachfile`,'\">',c.attachname,'</a><br><br>'),',','') AS fileName,  \n"
							+"		CONCAT('约',IF(SUM(c.`filesize`)>1024*1024, CONCAT(ROUND(SUM(c.`filesize`)/1024/1024),'MB'),CONCAT(ROUND(SUM(c.`filesize`/1024)),'KB'))) AS fileSize   \n"
							+"	FROM k_attachext c \n"
							+"	GROUP BY `indexid` \n"
							+")c ON a.`fileId` = c.`indexid`  \n"
							+"inner JOIN oa_emailuser d ON a.`uuid` = d.`uuid` \n"
							+"where 1=1  and d.ctype='收件人' and d.dustbin = '否' and d.userId='"+userSession.getUserId()+"' " 
							+"${title} ${importance} ${sendDate} ${content}";*/
			String sql = "SELECT a.`uuid`,if(d.isRead='否','<font color=red>NEW</font>','') as IFNEW, if(d.isRead='否',Concat('<b>',b.`Name`,'</b>'),b.`Name`) AS addresser, \n"
						+"if(d.isRead='否',Concat('<b>',a.`title`,'</b>'),a.title) as title,  \n"
						+"if(d.isRead='否',Concat('<b>',a.`sendDate`,'</b>'),a.sendDate) as sendDate, a.`property`,f.departname,  \n"
						+" a.`importance`  AS importance, \n"     
						+"REPLACE(GROUP_CONCAT('<a href=\"common.do?method=attachDownload&attachId=',c.`attachfile`,'\">',c.attachname,'</a>&nbsp;| &nbsp;'),',','') AS fileName,count(c.attachname) as cCount \n"
						//+"REPLACE(CONCAT('<a href=\"common.do?method=attachDownload&attachId=',c.`attachfile`,'\">',c.attachname,'</a>.....'),',','') AS fileName,count(c.attachname) as cCount \n"
						//+"CONCAT('约',IF(SUM(c.`filesize`)>1024*1024, CONCAT(ROUND(SUM(c.`filesize`)/1024/1024),'MB'),CONCAT(ROUND(SUM(c.`filesize`/1024)),'KB'))) AS fileSize    \n"
						+"FROM oa_email a \n"
						+"LEFT JOIN k_user b ON a.`addresser` = b.`id`  \n"
						+"LEFT JOIN k_department f ON b.`departmentId` = f.`autoid`  \n"
						+"left JOIN oa_emailuser d ON a.`uuid` = d.`uuid`  \n"
						+"left join k_attachext c on a.`fileId` = c.`indexid`  \n"
						+"where 1=1   AND a.`status` <> '已删除' AND a.`status`<>'已撤销'  \n"
						+" and d.userId='"+userSession.getUserId()+"' and (d.ctype='收件人' or d.ctype='抄送人' or d.ctype='密送人')  and d.dustbin = '否' ${title} ${importance} ${sendDate} ${content} ${addressee}\n";
			
			if(!"".equals(ifRead)){
				sql +=" and d.isRead='否' ";
			}
			sql+=" group by a.`uuid`";
			
			System.out.println("sssssssssss$$$$$$$="+sql);
			
			pp.setTableID("addresseeList");
			pp.setCustomerId("");
			pp.setWhichFieldIsValue(1);
			pp.setInputType("checkbox");
			pp.setOrderBy_CH("a.sendDate,d.isread");
			pp.setDirection("desc,asc");

			pp.setPageSize_CH("20");
			pp.setTrActionProperty(true); // 设置 table可双击
			pp.setPrintEnable(true);	//关闭dg打印 
			pp.setTrAction(" uuid='${uuid}' ") ;
			
			//pp.setPrintSqlColumn("addresser,departname,sendDate,importance");
			//pp.setPrintColumn("主题`发件人`发件人所属部门`发件时间`重要性");
			pp.setPrintTitle("内部邮箱列表");
			
			pp.setColumnWidth("3,30,8,10,15,16,8,15");
			pp.setSQL(sql);
			pp.addColumn("NEW", "IFNEW");
			pp.addColumn("主题", "title",null,null," <a href=# onclick='goRead(\"${uuid}\")'>${value}</a> ");
			pp.addColumn("发件人", "addresser");
			pp.addColumn("发件人所属部门", "departname");
			pp.addColumn("发件时间", "sendDate");
			pp.addColumn("附件", "fileName");
			pp.addColumn("重要性", "importance");
			
			//pp.addColumn("附件大小", "fileSize");
			
			pp.addSqlWhere("addressee", "and b.name like '%${addressee}%'");
			pp.addSqlWhere("title", "and a.title like '%${title}%'");
			pp.addSqlWhere("importance", "and a.importance like '%${importance}%'");
			pp.addSqlWhere("sendDate", "and a.sendDate like '%${sendDate}%'");
			pp.addSqlWhere("content", "and a.content like '%${content}%'");
			
			modelAndView.addObject("notGet",getIsRead(userSession)); //未读
		} catch (Exception e) {
			e.printStackTrace();
		}finally{			
			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		}
		
		return modelAndView;
	}

	/**
	 * 已发送List
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView sendList(HttpServletRequest request, HttpServletResponse response){
		
		ModelAndView modelAndView = new ModelAndView(SENDLIST);
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		DataGridProperty pp = new DataGridProperty(); 
		try {	
			String sql="SELECT DISTINCT a.`uuid`,GROUP_CONCAT( DISTINCT e.name) as userName, a.`title`,a.`sendDate`, a.`property`, \n" 
						+" if(a.`importance` = '重要邮件',concat('<font color=#FF8C00>',a.`importance`,'</font>'), \n"
						+"if(a.`importance` ='非常重要',concat('<font color=#FF0000><b>',a.`importance`,'</b></font>'),a.`importance`)) AS importance,    \n"
						+"	indexid, \n" 
						+"REPLACE(GROUP_CONCAT('<a href=\"common.do?method=attachDownload&attachId=',c.`attachfile`,'\">',c.attachname,'</a>&nbsp;| &nbsp;'),',','') AS fileName \n"
						//+"	CONCAT('约',IF(SUM(c.`filesize`)>1024*1024, CONCAT(ROUND(SUM(c.`filesize`)/1024/1024),'MB'),CONCAT(ROUND(SUM(c.`filesize`/1024)),'KB'))) AS fileSize   \n"
						+"FROM oa_email a \n"
						+"LEFT JOIN k_attachext c  ON a.`fileId` = c.`indexid`  \n"
						+"LEFT JOIN k_user e ON a.`addressee` LIKE CONCAT('%',e.`id`,'%') \n" 
						+"where 1=1  and a.addresser='"+userSession.getUserId()+"' and a.status !='已撤销' and a.status !='发件箱已删除' and a.status !='已删除'" +
						" ${addressee} ${title} ${importance} ${sendDate} ${content} group by a.`uuid`,c.`indexid`   ";
			System.out.println(sql);
			pp.setTableID("sendList");
			pp.setCustomerId("");
			pp.setWhichFieldIsValue(1);
			pp.setInputType("checkbox");
			pp.setOrderBy_CH("sendDate");
			pp.setDirection("desc");
			
			pp.setTrActionProperty(true); // 设置 table可双击
			pp.setTrAction("style=\"cursor:hand;\" uuid=\"${uuid}\"  ");
			
			pp.setColumnWidth("15,25,8,13,15,15");
			pp.setSQL(sql);
			
			pp.addColumn("收件人", "userName");
			pp.addColumn("主题", "title",null,null,"<a href='javascript:void(0);' onclick='goLook(\"${uuid}\")'>${value}</a>");
			pp.addColumn("重要性", "importance");
			pp.addColumn("发件时间", "sendDate");
			pp.addColumn("附件", "fileName");
			//pp.addColumn("附件大小", "fileSize");
			pp.addColumn("操作","uuid",null,null,"<a href='javascript:void(0);' onclick='goSend(\"${value}\")'>再次发送</a> &nbsp;<a href='javascript:void(0);' onclick='goActionLookState(\"${value}\")'>查看阅读状态</a>");

			pp.addSqlWhere("title", "and a.title like '%${title}%'");
			pp.addSqlWhere("addressee", "and e.name like '%${addressee}%'");
			pp.addSqlWhere("importance", "and a.importance like '%${importance}%'");
			pp.addSqlWhere("sendDate", "and a.sendDate like '%${sendDate}%'");
			pp.addSqlWhere("content", "and a.content like '%${content}%'");
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{			
			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		}
		
		return modelAndView;
	}
	
	/**
	 * 发送状态
	 * @param request
	 * @param response
	 * @return
	 */
	private static String sendStateList = "oa/interiorEmail/sendStateList.jsp";
	public ModelAndView sendEmailStateList(HttpServletRequest request, HttpServletResponse response){
		
		ModelAndView modelAndView = new ModelAndView(sendStateList);
		//UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		DataGridProperty pp = new DataGridProperty(); 
		ASFuntion asf = new ASFuntion();
		String uuid = asf.showNull(request.getParameter("uuid"));
		modelAndView.addObject("uuid", uuid);
		try {	
			String sql="SELECT a.autoId,a.userId,b.name,c.departName,IF(a.isRead ='否','未读','已读') AS isRead FROM oa_emailuser a \n" 
					   +" LEFT JOIN k_user b ON a.userId = b.id \n"
					   +" LEFT JOIN k_department c ON b.departmentId = c.autoId \n"
					   +" WHERE 1=1 \n";
			if(!"".equals(uuid)){
				sql +=" and a.uuid = '"+uuid+"'";
			}
			sql +="  ${userName} ${departName} ";
			pp.setTableID("sendEmailStateList");
			pp.setCustomerId("");
			pp.setWhichFieldIsValue(1);
			pp.setInputType("radio");
			pp.setOrderBy_CH("isRead");
			pp.setDirection("desc");
			
			//pp.setTrActionProperty(true); // 设置 table可双击
			//pp.setTrAction("style=\"cursor:hand;\" uuid=\"${uuid}\"  ");
			
			pp.setColumnWidth("15,15,15,30");
			pp.setSQL(sql);
			
			pp.addColumn("状态", "isRead","showCenter");
			pp.addColumn("收件人", "name","showCenter");
			pp.addColumn("所属部门", "departName","showCenter");
			pp.addColumn("操作","uuid","showCenter",null,"<a href='javascript:void(0);' onclick='goSend(\"${userId}\",\"${name}\",\"${isRead}\")'>再次发送</a>" +
														"&nbsp;|&nbsp;<a href='javascript:void(0);' onclick='goRepealSend(\"${autoId}\",\"${userId}\",\"${isRead}\",\"${name}\")'>撤销发送</a>"+
														"&nbsp;|&nbsp;<a href='javascript:void(0);' onclick='goMbMsg(\"${autoId}\",\"${userId}\",\"${isRead}\",\"${name}\")'>手机短信提醒</a>");

			pp.addSqlWhere("userName", "and b.name like '%${userName}%'");
			pp.addSqlWhere("departName", "and d.departname like '%${departName}%'");
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{			
			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		}
		
		return modelAndView;
	}
	
	/**
	 * 草稿箱List
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView draftList(HttpServletRequest request, HttpServletResponse response){
		
		ModelAndView modelAndView = new ModelAndView(DRAFTLIST);
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		DataGridProperty pp = new DataGridProperty(); 
		try {	
			String sql="SELECT a.`uuid`,b.`Name` AS addresser, a.`title`,a.`sendDate`, a.`property`, \n" 
							+" if(a.`importance` = '重要邮件',concat('<font color=#FF8C00>',a.`importance`,'</font>'), \n"
							+"if(a.`importance` ='非常重要',concat('<font color=#FF0000><b>',a.`importance`,'</b></font>'),a.`importance`)) AS importance,    \n"
							//+"REPLACE(GROUP_CONCAT('<a href=\"common.do?method=attachDownload&attachId=',c.`attachfile`,'\">',c.attachname,'</a><br><br>'),',','') AS fileName,  \n"
							+"REPLACE(GROUP_CONCAT('<a href=\"common.do?method=attachDownload&attachId=',c.`attachfile`,'\">',c.attachname,'</a>&nbsp;| &nbsp;'),',','') AS fileName, \n"
							+"CONCAT('约',IF(SUM(c.`filesize`)>1024*1024, CONCAT(ROUND(SUM(c.`filesize`)/1024/1024),'MB'),CONCAT(ROUND(SUM(c.`filesize`/1024)),'KB'))) AS fileSize   \n"
							+"FROM oa_emaildraft a \n"
							+"LEFT JOIN k_user b ON a.`addresser` = b.`id` \n"
							+"LEFT JOIN  k_attachext c  ON a.`fileId` = c.`indexid`    \n"
							+"where 1=1 and a.addresser='"+userSession.getUserId()+"' ${title} ${importance} ${sendDate} ${content} group by a.uuid ";
			
			pp.setTableID("draftList");
			pp.setCustomerId("");
			pp.setWhichFieldIsValue(1);
			pp.setInputType("checkbox");
			pp.setOrderBy_CH("sendDate");
			pp.setDirection("desc");
			
			pp.setColumnWidth("20,8,15,15,10,15");
			pp.setSQL(sql);
			
			pp.setTrActionProperty(true); // 设置 table可双击
			pp.setTrAction("style=\"cursor:hand;\" uuid=\"${uuid}\"  ");
			
			//pp.addColumn("发件人", "addresser");
			pp.addColumn("主题", "title");
			pp.addColumn("重要性", "importance");
			pp.addColumn("发件时间", "sendDate");
			pp.addColumn("附件", "fileName");
			pp.addColumn("附件大小", "fileSize");
			pp.addColumn("操作","uuid",null,null,"<a href='javascript:void(0);' onclick='goEdit(\"${value}\")'><img src='img/edit.gif' alt='编辑'></img>编辑</a> &nbsp;&nbsp;" +
							"<a href='javascript:void(0);' onclick='goSend(\"${value}\")'><img src='img/open.gif' alt='立即发送'></img>立即发送</a>");
			
			pp.addSqlWhere("title", "and a.title like '%${title}%'");
			pp.addSqlWhere("importance", "and a.importance like '%${importance}%'");
			pp.addSqlWhere("sendDate", "and a.sendDate like '%${sendDate}%'");
			pp.addSqlWhere("content", "and a.content like '%${content}%'");
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{			
			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		}
		
		return modelAndView;
	}

	/**
	 * 已删除List
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView deletedList(HttpServletRequest request, HttpServletResponse response){
		
		ModelAndView modelAndView = new ModelAndView(DELETELIST);
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		DataGridProperty pp = new DataGridProperty(); 
		try {	
			String sql="SELECT a.`uuid`,b.`Name` AS addresser, a.`title`,a.`sendDate`, a.`property`, \n" 
							+" if(a.`importance` = '重要邮件',concat('<font color=#FF8C00>',a.`importance`,'</font>'), \n"
							+" if(a.`importance` ='非常重要',concat('<font color=#FF0000><b>',a.`importance`,'</b></font>'),a.`importance`)) AS importance    \n"
							//+" REPLACE(GROUP_CONCAT('<a href=\"common.do?method=attachDownload&attachId=',c.`attachfile`,'\">',c.attachname,'</a><br><br>'),',','') AS fileName  \n"
							//+" CONCAT('约',IF(SUM(c.`filesize`)>1024*1024, CONCAT(ROUND(SUM(c.`filesize`)/1024/1024),'MB'),CONCAT(ROUND(SUM(c.`filesize`/1024)),'KB'))) AS fileSize   \n"
							+" FROM oa_email a \n"
							+" LEFT JOIN k_user b ON a.`addresser` = b.`id` \n"
							//+" LEFT JOIN k_attachext c  ON a.`fileId` = c.`indexid`  \n"
							+" left JOIN oa_emailuser d ON a.`uuid` = d.`uuid` \n"
							+"where 1=1 and d.dustbin = '是' and d.userId='"+userSession.getUserId()+"' and (d.ctype='收件人' or d.ctype='抄送人' or d.ctype='密送人') \n" 
							+" ${title} ${importance} ${sendDate} ${content} group by a.uuid ";
			
			pp.setTableID("deletedList");
			pp.setCustomerId("");
			pp.setWhichFieldIsValue(1);
			pp.setInputType("checkbox");
			pp.setOrderBy_CH("sendDate");
			pp.setDirection("desc");
			
			pp.setColumnWidth("10,20,8,15,20,15");
			pp.setSQL(sql);
			pp.setTrActionProperty(true); // 设置 table可双击
			pp.setTrAction("style=\"cursor:hand;\" uuid=\"${a.uuid}\"  ");
			
			pp.addColumn("发件人", "addresser");
			pp.addColumn("主题", "title");
			pp.addColumn("重要性", "importance");
			pp.addColumn("发件时间", "sendDate");
			//pp.addColumn("附件", "fileName");
			//pp.addColumn("附件大小", "fileSize");
			
			pp.addSqlWhere("title", "and a.title like '%${title}%'");
			pp.addSqlWhere("importance", "and a.importance like '%${importance}%'");
			pp.addSqlWhere("sendDate", "and a.sendDate like '%${sendDate}%'");
			pp.addSqlWhere("content", "and a.content like '%${content}%'");
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{			
			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		}
		
		return modelAndView;
	}
	
	/**
	 * 写信跳转
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView addSkip (HttpServletRequest request, HttpServletResponse response){
		
		ModelAndView modelAndView = new ModelAndView(EPISTOLIZE);
		
	 
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
	
		modelAndView.addObject("notGet",getIsRead(userSession)); //未读
			
		return modelAndView;
	}
	/**
	 * 写信跳转
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView addSkips (HttpServletRequest request, HttpServletResponse response){
		
		ModelAndView modelAndView = new ModelAndView(EPISTOLIZES);
		
	 
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
	
		modelAndView.addObject("notGet",getIsRead(userSession)); //未读
			
		return modelAndView;
	}
	
	/**
	 * 写信保存
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView saveAddressee(HttpServletRequest request, HttpServletResponse response){
		
		ASFuntion  asf = new ASFuntion();
		Connection conn = null;
		try {
				conn = new DBConnect().getConnect();
				InteriorEmailService emailService = new InteriorEmailService(conn);
				PlacardService placardService = new PlacardService(conn);
				String addresseeIds = asf.showNull(request.getParameter("addresseeId"));//收信人
				String copyUserIds = asf.showNull(request.getParameter("copyUserId")); //抄送
				String secretUserIds = asf.showNull(request.getParameter("secretUserId"));//密送
				String title = asf.showNull(request.getParameter("title"));
				String importance = asf.showNull(request.getParameter("importance")); //重要性
				String ctype = asf.showNull(request.getParameter("ctype")); //草稿 还是 立即发送
				//String uuid = asf.showNull(request.getParameter("uuid")); //草稿 还是 立即发送
				
				Email email = new Email(); 
				String content = asf.showNull(request.getParameter("content"));
				String fileId = asf.showNull(request.getParameter("fileId"));  //附件
				String instationRemind = asf.showNull(request.getParameter("instationRemind"));  //内部短信提醒
				String receiveRemind = asf.showNull(request.getParameter("receiveRemind"));  //阅读条
				String mobilePhoneRemind = asf.showNull(request.getParameter("mobilePhoneRemind"));  //手机短信提醒
				
				/*if(!"".equals(content)){
					boolean result = TagsChecker.check(content); //标签闭合性检查
					if(!result){
						content = TagsChecker.fix(content); //修复没有闭合的标签
					}
				}*/
				
				UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
				
				String uuid = UUID.randomUUID().toString(); 
				
				EmailUser emailUser = new EmailUser();
				if(!"".equals(instationRemind)){
					emailUser.setInstationRemind(instationRemind);
				} 
				if(!"".equals(receiveRemind)){
					emailUser.setReceiveRemind(receiveRemind);
				}
				emailUser.setUuid(uuid);
				emailUser.setReadTime("0");
				if(!"".equals(mobilePhoneRemind)){
					emailUser.setMobilePhoneRemind(mobilePhoneRemind);
				}
				
				String todayTime = asf.getCurrentDate()+" "+asf.getCurrentTime();
				String sbString="您有一份新邮件，发信人是："+userSession.getUserName()+" "
									+"发送时间为："+todayTime+" "
									+"邮件标题为："+title+" "
									+"重要性："+importance+" "
									+"请您及时查看！"; 
				
				PlacardTable placardTable=new PlacardTable(); 
				placardTable.setIsReversion(0);
				placardTable.setAddresserTime(todayTime);
				placardTable.setCaption("邮件提醒");
				placardTable.setMatter(sbString);
				placardTable.setIsRead(0);
				placardTable.setIsNotReversion(0);
				placardTable.setUuid(uuid);
				placardTable.setUrl("interiorEmail.do?method=emailMain&isReadOnly=true&back=true");
				placardTable.setUuidName("uuid");
				placardTable.setModel("内部邮件");
				placardTable.setAddresser(userSession.getUserId());//发起
					
				 //收件人
				if(!"".equals(addresseeIds)){
					
					String[] addresseeId = addresseeIds.split(",");
					emailUser.setCtype("收件人");
					//人员
					for (int i = 0; i < addresseeId.length; i++) {
						
						emailUser.setUserId(addresseeId[i]);
						emailService.addEmailUser(emailUser);

						placardTable.setAddressee(addresseeId[i]); //接收的老大UserId
						//发送站内消息
						if("是".equals(instationRemind) && "立即发送".equals(ctype)){
							placardService.AddPlacard(placardTable); //记录人发消息
						}
						
					}
					//手机短信提醒
					if("是".equals(mobilePhoneRemind) && "立即发送".equals(ctype)){
						String msgcontent =  sbString.replace("<br>", "");
						this.mobilePhoneInfo(request,response,"", addresseeIds,msgcontent);
					}
					 
				}
				
				//抄送人
				if(!"".equals(copyUserIds)){ 
					String[] copyUserId = copyUserIds.split(",");
					emailUser.setCtype("抄送人");
					
					for (int i = 0; i < copyUserId.length; i++) {
						emailUser.setUserId(copyUserId[i]);
						emailService.addEmailUser(emailUser);
						
						placardTable.setAddressee(copyUserId[i]); //接收的老大UserId

						if("是".equals(instationRemind) && "立即发送".equals(ctype)){
							placardService.AddPlacard(placardTable); //记录人发消息
						}
					}
					
					if("是".equals(mobilePhoneRemind) && "立即发送".equals(ctype)){
						String msgcontent =  sbString.replace("<br>", "");
						this.mobilePhoneInfo(request,response,"", copyUserIds,msgcontent);
					}
				}
					
				//密送人
				if(!"".equals(secretUserIds)){ 
					String[] secretUserId = secretUserIds.split(",");
					emailUser.setCtype("密送人");
					
					for (int i = 0; i < secretUserId.length; i++) {
						emailUser.setUserId(secretUserId[i]);
						emailService.addEmailUser(emailUser);
						
						placardTable.setAddressee(secretUserId[i]); //接收的老大UserId
						if("是".equals(instationRemind) && "立即发送".equals(ctype)){
							placardService.AddPlacard(placardTable); //记录人发消息
						}
					}
					
					if("是".equals(mobilePhoneRemind) && "立即发送".equals(ctype)){
						String msgcontent =  sbString.replace("<br>", "");
						this.mobilePhoneInfo(request,response,"", secretUserIds,msgcontent);
					}
				}
				
				email.setUuid(uuid);
				email.setContent(content);
				email.setFileId(fileId);
				email.setImportance(importance);
				email.setTitle(title);
				email.setAddresser(userSession.getUserId());
				email.setAddressee(addresseeIds+copyUserIds+secretUserIds); //收件人
				if("草稿".equals(ctype)){
					//新增邮件信息
						emailService.addEmailDraft(email);
				}else if("立即发送".equals(ctype)){
						emailService.addEmail(email);
				}

				
				if("草稿".equals(ctype)){
					response.sendRedirect(request.getContextPath()+"/interiorEmail.do?method=draftList");
				}else{
					response.sendRedirect(request.getContextPath()+"/interiorEmail.do?method=addresseeList");
				}	
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return null;
	}

	/**
	 * 未读数量
	 * @param request
	 * @param response
	 * @return
	 */
	public String getIsRead (UserSession userSession){
		
		Connection connRead = null;
		try {
			connRead = new DBConnect().getConnect();
			
			//未读数量
			String sql = "SELECT COUNT(DISTINCT a.`uuid`) AS sumCount " +
					 "FROM `oa_emailuser` a " +
					 " INNER JOIN oa_email b ON a.`uuid` = b.`uuid` "+
					 "WHERE userId='"+userSession.getUserId()+"' and a.isRead='否'  "  +
					 "  and (a.ctype='收件人' or a.ctype='抄送人' or a.ctype='密送人')";
			String notGets = new DbUtil(connRead).queryForString(sql);
			if("0".equals(notGets) || "".equals(notGets)){
				return  "0";
			}else{
				return notGets;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(connRead);
		}
		
		return null;
	}
	
	/**
	 * 删除收件箱
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView delAddressee(HttpServletRequest request, HttpServletResponse response){
	
		ASFuntion asf = new ASFuntion();
		Connection conn = null;
		try {
				String result = "false";
				response.setCharacterEncoding("utf-8");
				request.setCharacterEncoding("utf-8");
				PrintWriter out = response.getWriter();
				conn = new DBConnect().getConnect();
				InteriorEmailService interiorEmailService = new InteriorEmailService(conn);
				String uuids = asf.showNull(request.getParameter("uuids")); //批量删除到垃圾箱
				String ctype = asf.showNull(request.getParameter("ctype")); //批量删除到垃圾箱
				UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
				
				//放在垃圾箱
				if("1".equals(ctype)){
					if(!"".equalsIgnoreCase(uuids)){
						String[] uuid = uuids.split(",");
						
						for (int i = 0; i < uuid.length; i++) {
							String sql = "UPDATE `oa_emailuser` SET dustbin = '是'" +
									" WHERE UUID='"+uuid[i]+"' AND userId = '"+userSession.getUserId()+"' ";
							interiorEmailService.UpdateValueBySql(sql);
							result = "true";
						}
					}
					//永久删除
				} else if("2".equals(ctype)){
					if(!"".equalsIgnoreCase(uuids)){
					String[] uuid = uuids.split(",");
					
					for (int i = 0; i < uuid.length; i++) {
						
						List<EmailUser> listEmailUser = interiorEmailService.getListEmailUser(uuid[i], userSession.getUserId());
						
						for (int j = 0; j < listEmailUser.size(); j++) {
							EmailUser emailUser = listEmailUser.get(j);
							interiorEmailService.addEmailUserLog(emailUser); //添加日志
						}
						
						//如果是最后一个人删除，就删掉发件人邮件
						String sql ="SELECT COUNT(*) FROM oa_emailuser WHERE `uuid`='"+uuid[i]+"' AND  (ctype='收件人' or ctype='抄送人' or ctype='密送人') ";
						int count = new DbUtil(conn).queryForInt(sql);
						//判断是否是最后一个
						if(count == 1){
							sql = "DELETE FROM `oa_email` WHERE UUID = '"+uuid[i]+"' ";
							int delEm = new DbUtil(conn).executeUpdate(sql);

						}

						//永久删除
						sql = "DELETE FROM `oa_emailuser` WHERE UUID = '"+uuid[i]+"' AND userId='"+userSession.getUserId()+"' and (ctype='收件人' or ctype='抄送人' or ctype='密送人') ";
						int delEmUser = new DbUtil(conn).executeUpdate(sql);
						
						result = "true";
						
					}
				}
				
				//删除所有已读邮件
				}else if("3".equals(ctype)){
					
					if(!"".equalsIgnoreCase(uuids)){
						String[] uuid = uuids.split(",");
						
						for (int i = 0; i < uuid.length; i++) {
							String sql = "UPDATE oa_emailuser a  \n"
								+"SET a.dustbin = '是' \n"
								+"WHERE a.userId = '"+userSession.getUserId()+"' and (a.isread='是' or a.readTime>0)  \n"
								+"AND a.`uuid`='"+uuid[i]+"'";
							
							interiorEmailService.UpdateValueBySql(sql);
							result = "true";
						}
					}else{
						
						String sql = "UPDATE oa_emailuser a \n" 
									+" left join oa_email b on a.uuid = b.uuid \n"
									+"SET a.dustbin = '是' \n"
									+"WHERE  a.userId = '"+userSession.getUserId()+"' and  (a.isread='是' or a.readTime>0)   ";
						interiorEmailService.UpdateValueBySql(sql);
						result = "true";
					}
				}
				out.write(result);
				//response.sendRedirect(request.getContextPath()+"/interiorEmail.do?method=");
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return null;
	}
	
	/**
	 * 删除草稿
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView delDraft(HttpServletRequest request, HttpServletResponse response){
		
		ASFuntion asf = new ASFuntion();
		Connection conn = null;
		try {
				String result = "false";
				response.setCharacterEncoding("utf-8");
				request.setCharacterEncoding("utf-8");
				PrintWriter out = response.getWriter();
				conn = new DBConnect().getConnect();
				InteriorEmailService interiorEmailService = new InteriorEmailService(conn);
				String uuids = asf.showNull(request.getParameter("uuids")); //永远删除
				
				if(!"".equalsIgnoreCase(uuids)){
					String[] uuid = uuids.split(",");
					
					for (int i = 0; i < uuid.length; i++) {
						
						String sql="SELECT fileId FROM `oa_emaildraft` WHERE uuid='"+uuid[i]+"'";
						String fileIds = interiorEmailService.getValueBySql(sql);
						String[] fileId = fileIds.split("@`@"); //已发箱数量
						
						AttachService attachService = new AttachService(conn);
						if(!"".equals(fileId[0])){
							attachService.remove(INDEXPAGE, fileId[0]); // 删除文件
						}
						
						sql = "DELETE FROM `oa_emailuser` WHERE UUID = '"+uuid[i]+"'";
						new DbUtil(conn).executeUpdate(sql);
						
						//永久删除
						sql = "DELETE FROM `oa_emaildraft` WHERE UUID = '"+uuid[i]+"'";
						new DbUtil(conn).executeUpdate(sql);
						
						result = "true";
					}
				}
				
				out.write(result);
				//response.sendRedirect(request.getContextPath()+"/interiorEmail.do?method=addresseeList");
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return null;
	}
	
	/**
	 * 标记已阅读
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView signAlreadyRead(HttpServletRequest request, HttpServletResponse response){
		
		ASFuntion asf = new ASFuntion();
		Connection conn = null;
		try {
				conn = new DBConnect().getConnect();
				InteriorEmailService interiorEmailService = new InteriorEmailService(conn);
				String uuids = asf.showNull(request.getParameter("uuids")); //批量删除到垃圾箱
				UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
				response.setCharacterEncoding("utf-8");
				request.setCharacterEncoding("utf-8");
				PrintWriter out = response.getWriter();
				//标记已阅读
				if(!"".equalsIgnoreCase(uuids)){
					String[] uuid = uuids.split(",");
					
					for (int i = 0; i < uuid.length; i++) {
						String sql = "UPDATE `oa_emailuser` SET isRead='是' ,readTime=ABS(readTime)+1,readDate=now() WHERE UUID='"+uuid[i]+"' and userId= '"+userSession.getUserId()+"'";
						interiorEmailService.UpdateValueBySql(sql);
						out.write("true");
					}
					
				}else{
					out.write("false");
				}
				//response.sendRedirect(request.getContextPath()+"/interiorEmail.do?method=addresseeList");
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return null;
	}

	/**
	 * 回复跳转
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView replySkip(HttpServletRequest request, HttpServletResponse response){
			ModelAndView modelAndView = new ModelAndView(REPLYSKIP);
			Connection conn = null;
			ASFuntion asf = new ASFuntion();
			String uuid = asf.showNull(request.getParameter("uuid"));
			String opt = asf.showNull(request.getParameter("opt"));
		try {
			conn = new DBConnect().getConnect();
			if(!"".equals(uuid)){
				
				InteriorEmailService emailService = new InteriorEmailService(conn);
				Email email = emailService.getEmail(uuid);
				UserService userService = new UserService(conn);
				User user = userService.getUser(email.getAddresser(), "id");
				PlacardService placardService = new PlacardService(conn);
				UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
				
				String sql = "UPDATE `oa_emailuser` SET isRead='是' ,readTime=ABS(readTime)+1,readDate=now() WHERE UUID='"+uuid+"' and userId= '"+userSession.getUserId()+"'";
				emailService.UpdateValueBySql(sql);
				
				sql = "SELECT COUNT(DISTINCT `uuid`) AS sumCount " +
						 "FROM `oa_emailuser` a " +
						 "WHERE userId='"+userSession.getUserId()+"' and a.isRead='否' AND readTime='0' and dustbin='否'" +
						 "  and ctype='收件人'";
				String notGets = emailService.getValueBySql(sql);
				if("0".equals(notGets) || "".equals(notGets)){
					modelAndView.addObject("notGet", "0");
				}else{
					String[] notGet = notGets.split("@`@"); //未读
					modelAndView.addObject("notGet", notGet[0]);
				}
				
				//第一次阅读时发收条提醒
				sql = "SELECT receiveRemind FROM `oa_emailuser` WHERE userId='"+userSession.getUserId()+"' AND readTime = '1' and uuid='"+uuid+"'";
				String ifreceiveRemind = emailService.getValueBySql(sql);
				
				if(!"".equals(ifreceiveRemind)){
					
					String[] receiveRemind = ifreceiveRemind.split("@`@");
				
					if("是".equals(receiveRemind[0])){
						PlacardTable placardTable=new PlacardTable(); 
						String todayTime = asf.getCurrentDate()+" "+asf.getCurrentTime();
						placardTable.setAddresser(userSession.getUserId());//发起
						placardTable.setAddresserTime(todayTime);
						placardTable.setCaption("邮件阅读提醒条");
						String sbString=user.getName()+"已经于“"+todayTime+"”<br>" +
										"阅读了您的邮件标题为："+email.getTitle()+"的邮件！";
						placardTable.setMatter("'"+sbString+"'");
						placardTable.setAddressee(email.getAddresser()); //接收的老大UserId
						placardTable.setIsRead(0);
						placardTable.setIsReversion(0);
						placardTable.setIsNotReversion(0);
					 
						placardService.AddPlacard(placardTable); //记录人发消息
							
					}
				}
				//再次发送
				if("againSend".equals(opt)){
					email.setAddresser("");
					//收件人
					String addresseeId = asf.showNull(new DbUtil(conn).queryForString("SELECT GROUP_CONCAT(userid) FROM oa_emailUser  WHERE `uuid` = '"+uuid+"' and ctype='收件人'"));
					String userName = asf.showNull(new DbUtil(conn).queryForString("SELECT GROUP_CONCAT(name) FROM k_user  WHERE `id` in ("+addresseeId+" )"));
					
					//email.setAddressee(addresseeId);
					modelAndView.addObject("userIdAll",addresseeId+",");
					modelAndView.addObject("userName",userName);
					
					//抄送人
					String copyUserId = asf.showNull(new DbUtil(conn).queryForString("SELECT GROUP_CONCAT(userid) FROM oa_emailUser  WHERE `uuid` = '"+uuid+"' and ctype='抄送人' "));
					if(!"".equals(copyUserId)){
						
						String copyUser = asf.showNull(new DbUtil(conn).queryForString("SELECT GROUP_CONCAT(name) FROM k_user  WHERE `id` in ("+copyUserId+" )"));
						
						modelAndView.addObject("copyUserId",copyUserId+",");
						modelAndView.addObject("copyUser",copyUser);
					}
					
					//密送人
					String secretUserId = asf.showNull(new DbUtil(conn).queryForString("SELECT GROUP_CONCAT(userid) FROM oa_emailUser  WHERE `uuid` = '"+uuid+"' and ctype='密送人' "));
					if(!"".equals(secretUserId)){
						
						String secretUser = asf.showNull(new DbUtil(conn).queryForString("SELECT GROUP_CONCAT(name) FROM k_user  WHERE `id` in ("+copyUserId+" )"));
						
						modelAndView.addObject("secretUserId",secretUserId+",");
						modelAndView.addObject("secretUser",secretUser);
					}
				}else{					
					email.setFileId("");
					email.setTitle("Re："+email.getTitle());
					modelAndView.addObject("reply","回复");
					modelAndView.addObject("userName",user.getName());
					String titleInfo = new DbUtil(conn).queryForString("SELECT CONCAT(b.`Name`,'在',a.`sendDate`,'的来信中写道:') FROM oa_email a LEFT JOIN k_user b ON a.`addresser` = b.id WHERE a.`uuid` = '"+uuid+"'");
					modelAndView.addObject("titleInfo", titleInfo);
				}
				modelAndView.addObject("email",email);
				
				
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return modelAndView;
	}
	
	/**
	 * 回复
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView reply(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelAndView = new ModelAndView(REPLYSKIP);
		Connection conn = null;
		ASFuntion asf = new ASFuntion();
		
		//String uuid = asf.showNull(request.getParameter("uuid"));
		//String instationRemind = asf.showNull(request.getParameter("instationRemind"));  //内部短信提醒
		String ctype = asf.showNull(request.getParameter("ctype")); //草稿 还是 立即发送
		String title = asf.showNull(request.getParameter("title"));
		String importance = asf.showNull(request.getParameter("importance")); //重要性
		String content = asf.showNull(request.getParameter("content"));
		String fileId = asf.showNull(request.getParameter("fileId"));  //附件
		//String addresseeId = asf.showNull(request.getParameter("addresseeId"));  //发件人
		String receiveRemind = asf.showNull(request.getParameter("receiveRemind"));  //阅读条
		String instationRemind = asf.showNull(request.getParameter("instationRemind"));  //提醒下
		String mobilePhoneRemind = asf.showNull(request.getParameter("mobilePhoneRemind"));  //手机短信提醒
		
		String addresseeIds = asf.showNull(request.getParameter("addresseeId"));//收信人
		String copyUserIds = asf.showNull(request.getParameter("copyUserId")); //抄送
		String secretUserIds = asf.showNull(request.getParameter("secretUserId"));//密送
		
	try {
		conn = new DBConnect().getConnect();
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		InteriorEmailService emailService = new InteriorEmailService(conn);
		Email email = new Email(); 
		
		
		
		String uuid = UUID.randomUUID().toString();
		email.setUuid(uuid);
		email.setTitle(title);
		email.setAddresser(userSession.getUserId());
		email.setContent(content);
		email.setFileId(fileId);
		email.setImportance(importance);
		email.setAddressee(addresseeIds+copyUserIds+secretUserIds);
		
		boolean result = emailService.addEmail(email);
		
		if(result){
			EmailUser emailUser = new EmailUser();
			if(!"".equals(receiveRemind)){
				emailUser.setReceiveRemind(receiveRemind);
			}
			emailUser.setUuid(uuid);
			emailUser.setCtype("收件人");
			emailUser.setMobilePhoneRemind(mobilePhoneRemind);
			
			PlacardService placardService = new PlacardService(conn);
			
			String todayTime = asf.getCurrentDate()+" "+asf.getCurrentTime();
			String sbString="您有一份新邮件，发信人是："+userSession.getUserName()+" "
								+"发送时间为："+todayTime+" "
								+"邮件标题为："+title+" "
								+"重要性："+importance+" "
								+"请您及时查看！"; 
			
			PlacardTable placardTable=new PlacardTable(); 
			placardTable.setIsReversion(0);
			placardTable.setAddresserTime(todayTime);
			placardTable.setCaption("邮件提醒");
			placardTable.setMatter(sbString);
			placardTable.setIsRead(0);
			placardTable.setIsNotReversion(0);
			placardTable.setUuid(uuid);
			placardTable.setUrl("interiorEmail.do?method=emailMain&back=true");
			placardTable.setUuidName("uuid");
			placardTable.setModel("内部邮件");
			placardTable.setAddresser(userSession.getUserId());//发起
			
			 //收件人
			if(!"".equals(addresseeIds)){
				
				String[] addresseeId = addresseeIds.split(",");
				emailUser.setCtype("收件人");
				//人员
				for (int i = 0; i < addresseeId.length; i++) {
					
					emailUser.setUserId(addresseeId[i]);
					emailService.addEmailUser(emailUser);

					placardTable.setAddressee(addresseeId[i]); //接收的老大UserId
					//发送站内消息
					if("是".equals(instationRemind) && "立即发送".equals(ctype)){
						placardService.AddPlacard(placardTable); //记录人发消息
					}
					
				}
				//手机短信提醒
				if("是".equals(mobilePhoneRemind) && "立即发送".equals(ctype)){
					String msgcontent =  sbString.replace("<br>", "");
					this.mobilePhoneInfo(request,response,"", addresseeIds,msgcontent);
				}
				 
			}
			
			
			//抄送人
			if(!"".equals(copyUserIds)){ 
				String[] copyUserId = copyUserIds.split(",");
				emailUser.setCtype("抄送人");
				
				for (int i = 0; i < copyUserId.length; i++) {
					emailUser.setUserId(copyUserId[i]);
					emailService.addEmailUser(emailUser);
					
					placardTable.setAddressee(copyUserId[i]); //接收的老大UserId

					if("是".equals(mobilePhoneRemind) && "立即发送".equals(ctype)){
						placardService.AddPlacard(placardTable); //记录人发消息
					}
				}
				
				if("是".equals(mobilePhoneRemind) && "立即发送".equals(ctype)){
					String msgcontent =  sbString.replace("<br>", "");
					this.mobilePhoneInfo(request,response,"", copyUserIds,msgcontent);
				}
			}
				
			//密送人
			if(!"".equals(secretUserIds)){ 
				String[] secretUserId = secretUserIds.split(",");
				emailUser.setCtype("密送人");
				
				for (int i = 0; i < secretUserId.length; i++) {
					emailUser.setUserId(secretUserId[i]);
					emailService.addEmailUser(emailUser);
					
					placardTable.setAddressee(secretUserId[i]); //接收的老大UserId
					if("是".equals(mobilePhoneRemind) && "立即发送".equals(ctype)){
						placardService.AddPlacard(placardTable); //记录人发消息
					}
				}
				
				if("是".equals(mobilePhoneRemind) && "立即发送".equals(ctype)){
					String msgcontent =  sbString.replace("<br>", "");
					this.mobilePhoneInfo(request,response,"", secretUserIds,msgcontent);
				}
			}
			
			
		}
		
		response.sendRedirect(request.getContextPath()+"/interiorEmail.do?method=addresseeList");
		
	} catch (Exception e) {
		e.printStackTrace();
	}finally{
		DbUtil.close(conn);
	}
	return null;
}
	/**
	 * 上一页 sql
	 * @param conn
	 * @param userId
	 * @return
	 */
	private String upPage(Connection conn,String userId,String uuid){
		
		
		try{
			if(!"".equals(uuid)){
				
				/*String sql = "SELECT `uuid` FROM ( "+  
							"SELECT MAX(autoId),a.uuid FROM oa_emailUser a \n"+
							" INNER JOIN oa_email b ON a.uuid = b.uuid \n"+
							" WHERE userId ='"+userId+"' AND b.status<> '已删除' AND b.status<> '已撤销' " +
							" AND autoid<"+autoId+" GROUP BY a.`uuid` ORDER BY ABS(autoid) LIMIT 1 \n"+
							" )a ";*/
				String sql = "SELECT t4.uuid \n"
							+"FROM ( \n"
							+"	SELECT MIN(t2.orderid) AS orderid \n"
							+"	FROM  \n"
							+"	( \n"
							+"	SELECT  a.senddate AS orderid,a.uuid  \n"
							+"	FROM oa_email a  \n"
							+"	LEFT JOIN oa_emailuser d ON a.`uuid` = d.`uuid`   \n"
							+"	WHERE 1=1   AND a.`status` <> '已删除' AND a.`status`<>'已撤销'   \n"
							+"	 AND d.userId='"+userId+"' AND (d.ctype='收件人' OR d.ctype='抄送人' OR d.ctype='密送人')  AND d.dustbin = '否'   \n"
							+"	 \n"
							+"	 ) t1 \n" 
							+"	 ,( \n"
							+"	SELECT  a.senddate AS orderid,a.uuid \n"
							+"	FROM oa_email a  \n"
							+"	LEFT JOIN oa_emailuser d ON a.`uuid` = d.`uuid`   \n"
							+"	WHERE 1=1   AND a.`status` <> '已删除' AND a.`status`<>'已撤销'   \n"
							+"	 AND d.userId='"+userId+"' AND (d.ctype='收件人' OR d.ctype='抄送人' OR d.ctype='密送人')  AND d.dustbin = '否'  \n" 
							+"	 ) t2 \n" 
							+"	WHERE t1.uuid = '"+uuid+"' AND t2.orderid>t1.orderid  \n"
							+")t3 \n"
							+"  ,( \n"
							+"SELECT  a.senddate AS orderid,a.uuid \n"
							+"	FROM oa_email a  \n"
							+"	LEFT JOIN oa_emailuser d ON a.`uuid` = d.`uuid`   \n"
							+"	WHERE 1=1   AND a.`status` <> '已删除' AND a.`status`<>'已撤销'   \n"
							+"	 AND d.userId='"+userId+"' AND (d.ctype='收件人' OR d.ctype='抄送人' OR d.ctype='密送人')  AND d.dustbin = '否'  \n" 
							+" ) t4 \n"
							+" WHERE t3.orderid=t4.orderid";
				uuid = new DbUtil(conn).queryForString(sql);
			}
		}catch (Exception e) {
			System.out.print("查询上一页sql出错："+e.getMessage());
		}finally{
			
		}
		return uuid;
	}
	
	/**
	 * 下一页
	 * @param conn
	 * @param userId
	 * @param autoId
	 * @return
	 */
	private String nextPage(Connection conn,String userId,String uuid){
		
		
		try{
			if(!"".equals(uuid)){
				
				/*String sql = "SELECT `uuid` FROM ( "+ 
							"SELECT Min(autoId),a.uuid FROM oa_emailUser a \n"+
							" INNER JOIN oa_email b ON a.uuid = b.uuid \n"+
							" WHERE userId ='"+userId+"' AND b.status<> '已删除' AND b.status<> '已撤销' \n" +
							" AND autoid>"+autoId+" GROUP BY a.`uuid` ORDER BY ABS(autoid) LIMIT 1 \n"+
							" )a ";*/
				String sql = "SELECT t4.uuid \n"
							+"FROM ( \n"
							+"	SELECT MAX(t2.orderid) AS orderid \n"
							+"	FROM  \n"
							+"	( \n"
							+"	SELECT  a.senddate AS orderid,a.uuid \n"
							+"	FROM oa_email a  \n"
							+"	LEFT JOIN oa_emailuser d ON a.`uuid` = d.`uuid`   \n"
							+"	WHERE 1=1   AND a.`status` <> '已删除' AND a.`status`<>'已撤销'   \n"
							+"	 AND d.userId='"+userId+"' AND (d.ctype='收件人' OR d.ctype='抄送人' OR d.ctype='密送人')  AND d.dustbin = '否' \n" 
							+"	 ) t1 \n"
							+"	 ,( \n"
							+"	SELECT  a.senddate AS orderid,a.uuid \n"
							+"	FROM oa_email a  \n"
							+"	LEFT JOIN oa_emailuser d ON a.`uuid` = d.`uuid`   \n"
							+"	WHERE 1=1   AND a.`status` <> '已删除' AND a.`status`<>'已撤销'   \n"
							+"	 AND d.userId='"+userId+"' AND (d.ctype='收件人' OR d.ctype='抄送人' OR d.ctype='密送人')  AND d.dustbin = '否'   \n"
							+"	 ) t2 \n"
							+"	WHERE t1.uuid = '"+uuid+"' AND t2.orderid<t1.orderid   \n"
							+")t3 \n"
							+"  ,( \n"
							+"SELECT  a.senddate AS orderid,a.uuid \n"
							+"	FROM oa_email a  \n"
							+"	LEFT JOIN oa_emailuser d ON a.`uuid` = d.`uuid`   \n"
							+"	WHERE 1=1   AND a.`status` <> '已删除' AND a.`status`<>'已撤销'   \n"
							+"	 AND d.userId='"+userId+"' AND (d.ctype='收件人' OR d.ctype='抄送人' OR d.ctype='密送人')  AND d.dustbin = '否'   \n"
							+") t4 \n"
							+" WHERE t3.orderid=t4.orderid";
				uuid = new DbUtil(conn).queryForString(sql);
			}
		}catch (Exception e) {
			System.out.print("查询下一页sql出错："+e.getMessage());
		}finally{
			
		}
		return uuid;
	}
	/**
	 * 阅读
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView read(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelAndView = new ModelAndView(READ);
		Connection conn = null;
		ASFuntion asf = new ASFuntion();
		String uuid = asf.showNull(request.getParameter("uuid"));
		String readUser = asf.showNull(request.getParameter("readUser")); //在发件箱  跳转过来传的参数
		String isReadOnly = asf.showNull(request.getParameter("isReadOnly")); //是不是只读，用来是否显示 “回复”
		String back=asf.showNull(request.getParameter("back"));
		//String ctype=asf.showNull(request.getParameter("ctype")); //用来判断是上一封(0) 还是 下一封(1)
		
		modelAndView.addObject("readUser",readUser);
		modelAndView.addObject("back",back);
		
	try {
		if("true".equals(isReadOnly)){
			isReadOnly = "";
		}
		conn = new DBConnect().getConnect();
		if(!"".equals(uuid)){
			
			InteriorEmailService emailService = new InteriorEmailService(conn);
			UserService userService = new UserService(conn);
			PlacardService placardService = new PlacardService(conn);
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			String userId = userSession.getUserId();
			/*//默认值为下一封
			String order = " asc ";
			String sign  =">";
			
			if("0".equals(ctype)){ //上一封
				order = " desc ";
				sign = "<";
			}
			String signUuidSql = "SELECT b.uuid FROM oa_emailuser a \n"+
							    " inner join oa_email b on a.uuid = b.uuid "+
								" WHERE userId='"+userSession.getUserId()+"' and status <>'已撤销' and status <>'已删除'  AND \n"+
								"autoId"+sign+"ABS( \n"+
								"     ( \n"+
								"     SELECT max(a.autoId) FROM `oa_emailuser` a  WHERE  a.UUID='"+uuid+"' AND a.userId='"+userSession.getUserId()+"' \n"+
								"    ) \n"+
								") \n"+
								" group by a.uuid ORDER BY autoid "+order +" limit 1 ";
			String toUuid = asf.showNull(new DbUtil(conn).queryForString(signUuidSql));
			if(!"".equals(toUuid)){
				uuid = toUuid;
			}*/
			
			//String autoSql = " SELECT MAX(autoId) FROM `oa_emailuser` WHERE `uuid`='"+uuid+"' AND userId='"+userId+"' limit 1 ";
			//String autoId = new DbUtil(conn).queryForString(autoSql);
			
			String upPageUuid =upPage(conn, userId, uuid); //上一页 uuid
			
			String nextPageUuid =  nextPage(conn, userId, uuid); //下一页 uuid
			
			modelAndView.addObject("upPageUuid", upPageUuid);
			modelAndView.addObject("nextPageUuid", nextPageUuid);

			//判断邮件是否被删除或者撤销
			String checkUuid = asf.showNull(new DbUtil(conn).queryForString("SELECT uuid FROM oa_email WHERE `uuid`='"+uuid+"' AND `status` <> '已删除' and `status`<>'已撤销' "));
			if(!"".equals(checkUuid)){
				uuid = checkUuid;
			}	
			Email email = emailService.getEmailCheckStatus(uuid);
			if( email !=null){
				User user = userService.getUser(email.getAddresser(), "id");
				
				String sql  = "UPDATE `oa_emailuser` SET isRead = '是',readTime=abs(readTime)+1,readDate=now() WHERE UUID='"+uuid+"' AND userId = '"+userSession.getUserId()+"'";
				emailService.UpdateValueBySql(sql);
				
				//关闭系统提示功能
				new DbUtil(conn).executeUpdate("UPDATE `k_placard` SET isRead = '1' WHERE model ='内部邮件'  AND addressee ='"+userSession.getUserId()+"' AND `uuid`='"+uuid+"'");
				
				//得到未读数量
				String notGets = this.getIsRead(userSession);
				modelAndView.addObject("notGet", notGets);
				
				//收件人
				sql = " SELECT GROUP_CONCAT(b.name) FROM `oa_emailuser` a \n"
					+"LEFT JOIN k_user b ON a.`userId` =b.id \n"
					+"WHERE a.`uuid` = '"+uuid+"' and  ctype = '收件人' GROUP BY a.`uuid`";
				String readUserNames = emailService.getValueBySql(sql);
				readUserNames = this.setStringName(readUserNames,"");
				modelAndView.addObject("readUserNames", readUserNames);
				
				//抄送人
				sql = " SELECT GROUP_CONCAT(b.name) FROM `oa_emailuser` a \n"
					+"LEFT JOIN k_user b ON a.`userId` =b.id \n"
					+"WHERE a.`uuid` = '"+uuid+"' and a.ctype='抄送人' GROUP BY a.`uuid`";
				String copyUserName = emailService.getValueBySql(sql);
				copyUserName = this.setStringName(copyUserName,"");
				modelAndView.addObject("copyUserName", copyUserName);
				
				
				//第一次阅读时发收条提醒
				sql = "SELECT receiveRemind FROM `oa_emailuser` WHERE userId='"+userSession.getUserId()+"' AND readTime=1 and uuid='"+uuid+"'";
				String ifreceiveRemind =  new DbUtil(conn).queryForString(sql);
				if(!"".equals(ifreceiveRemind)){
					
					if("是".equals(ifreceiveRemind)){
						PlacardTable placardTable=new PlacardTable(); 
						String todayTime = asf.getCurrentDate()+" "+asf.getCurrentTime();
						placardTable.setAddresser(userSession.getUserId());//发起
						placardTable.setAddresserTime(todayTime);
						placardTable.setCaption("邮件阅读提醒条");
						String sbString="【"+userSession.getUserName()+"】已经于“"+todayTime+"” " +
						"阅读了您的邮件标题为："+email.getTitle()+"的邮件！";
						placardTable.setMatter(sbString);
						placardTable.setAddressee(email.getAddresser()); //接收的老大UserId
						placardTable.setIsRead(0);
						placardTable.setIsReversion(0);
						placardTable.setIsNotReversion(0);
						
						placardService.AddPlacard(placardTable); //记录人发消息
						
					}
				}
				
				modelAndView.addObject("userName",user.getName());
			}
			modelAndView.addObject("email",email);
				
			modelAndView.addObject("uuid",uuid);
			modelAndView.addObject("isReadOnly",isReadOnly);
			
			
		}
		
	} catch (Exception e) {
		e.printStackTrace();
	}finally{
		DbUtil.close(conn);
	}
	return modelAndView;
}
	
	//私有方法，内部使用
	private String setStringName(String readUserNames,String showString){
		if("0".equals(readUserNames) || "".equals(readUserNames)){
			readUserNames= showString;
		}else{
			readUserNames  = readUserNames.replace("@`@", ",");
			readUserNames = readUserNames.substring(0,readUserNames.length()-1);
			if("".equals(readUserNames)){
				readUserNames= showString;
			}
		}
		return readUserNames;
	}
	/**
	 * 得到阅读人员
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView readUser(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelAndView = new ModelAndView(READUSER);
		Connection conn = null;
		ASFuntion asf = new ASFuntion();
		String uuid = asf.showNull(request.getParameter("uuid"));
		
	try {
		conn = new DBConnect().getConnect();
		if(!"".equals(uuid)){
			
			InteriorEmailService emailService = new InteriorEmailService(conn);
			Email email = emailService.getEmail(uuid);
	 
			
			List<EmailUser> listEmailUser = emailService.getListEmailReadUser(uuid);
			modelAndView.addObject("listEmailUser",listEmailUser);
			modelAndView.addObject("email",email);
		}
		
	} catch (Exception e) {
		e.printStackTrace();
	}finally{
		DbUtil.close(conn);
	}
	return modelAndView;
}

	/**
	 * ajax得到未读人员
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView ajaxReadUser(HttpServletRequest request, HttpServletResponse response){
		Connection conn = null;
		ASFuntion asf = new ASFuntion();
		try {
			String uuid = asf.showNull(request.getParameter("uuid"));
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			conn = new DBConnect().getConnect();
			if(!"".equals(uuid)){
				
				//未读人员
				String weiduUser = new DbUtil(conn).queryForString("SELECT GROUP_CONCAT(b.name) FROM oa_emailuser a LEFT JOIN k_user b ON a.userId = b.id WHERE `uuid` = '"+uuid+"' AND a.isread='否'");
				
				//已读人员
				String yiduUser = new DbUtil(conn).queryForString("SELECT GROUP_CONCAT(b.name) FROM oa_emailuser a LEFT JOIN k_user b ON a.userId = b.id WHERE `uuid` = '"+uuid+"' AND a.isread='是'");
				
				response.getWriter().write(weiduUser+"!"+yiduUser);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return null;
	}
	
	/**
	 * 人员tree
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void getUserJsonTree(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
//		ASFuntion CHF = new ASFuntion();
//		Connection conn = null;
//		PreparedStatement ps = null;
//		ResultSet rs = null;
//		ResultSet rs2 = null ;
//		try {
//			conn=new DBConnect().getConnect() ;
//			response.setContentType("text/html;charset=utf-8") ;
//			PrintWriter out = response.getWriter();
//			
//			UserSession userSession = (UserSession)request.getSession().getAttribute(("userSession"));
//			String userId = userSession.getUserId() ;
//			
//			//求出当前在线的人员
//			List list = new ArrayList();
//			Iterator it = OnlineListListener.getList().iterator();
//			while(it.hasNext()){
//				String id = ((UserSession)it.next()).getUserId();
//				list.add(id);
//			}
//			
//			String sql = "select * from (select autoid,departname from k_department  order by abs(property), autoid) a  union select -1,'无部门人员' from k_department ";
//			ps = conn.prepareStatement(sql);
//			rs = ps.executeQuery();
//			List treeList = new ArrayList() ;
//			while(rs.next()) {
//				String departId = rs.getString(1) ;
//				//TreeNode treeNode = new TreeNode() ;
//				Map map = new HashMap();
//				map.put("id", departId);
//			
//				map.put("text",rs.getString(2));
//				map.put("leaf", false);
//				//treeNode.setId(departId) ;
//				//treeNode.setText(rs.getString(2)) ;
//				//treeNode.setLeaf(false) ;
//				
//				sql = "select id,name ,DepartName from k_user a,k_department b where a.departmentid=b.autoid and a.state=0 and a.departmentid='" + departId + "' order by b.DepartName " ;
//				if("-1".equals(departId)) {
//					//无部门人员
//					sql = "select id,name from k_user a where a.state=0 and (a.departmentid = '' or departmentid is null) " ;
//				}
//				ps = conn.prepareStatement(sql) ;
//				rs2 = ps.executeQuery() ;
//				List childList = new ArrayList() ;
//				while(rs2.next()) {
//					//TreeNode childTreeNode = new TreeNode() ;
//					Map childTreeNode = new HashMap();
//					//childTreeNode.setId(rs2.getString(1)) ;
//					childTreeNode.put("id",rs2.getString(1)) ;
//					childTreeNode.put("userName",rs2.getString(2)) ;
//					
//					if(list.contains(rs2.getString(1))) {
//						//在线用户
//						//childTreeNode.setText(rs2.getString(2)+"&nbsp;<font color='#FF0000'>(在线)</font>") ;
//						childTreeNode.put("text",rs2.getString(2)+"&nbsp;<font color='#FF0000'>(在线)</font>") ;
//					}else {
//						//childTreeNode.setText(rs2.getString(2)+"&nbsp;<font color='#0000FF'>(离线)</font>") ;
//						childTreeNode.put("text",rs2.getString(2)+"&nbsp;<font color='#0000FF'>(离线)</font>") ;
//					}
//					
//					childTreeNode.put("leaf",true) ;
//					childTreeNode.put("checked",false) ;
//					//childTreeNode.setLeaf(true) ;
//					//childTreeNode.setChecked(false) ;
//					childList.add(childTreeNode) ;
//				}
//				map.put("name", departId);
//				//treeNode.setChildren(childList) ;
//				map.put("children",childList) ;
//				//treeNode.setChecked(false) ;
//				map.put("checked", false);
//				//treeList.add(treeNode) ;
//				treeList.add(map) ;
//			}
//			
//			String jsonStr = JSONArray.fromObject(treeList).toString() ;
//			out.write(jsonStr) ;
//		}catch(Exception e){
//			e.printStackTrace();
//			throw e;
//		}finally{
//			DbUtil.close(rs);
//			DbUtil.close(ps);
//			DbUtil.close(conn);
//		}
		response.setContentType("text/html;charset=utf-8");
		Connection conn = null;
		try {
			ASFuntion CHF=new ASFuntion();
			
			String checked = CHF.showNull(request.getParameter("checked"));
			
			String departid = CHF.showNull(request.getParameter("departid"));//单位/区域/部门
			String areaid = CHF.showNull(request.getParameter("areaid"));//单位/区域/部门
			String departname = CHF.showNull(request.getParameter("departname"));	
			String isSubject = CHF.showNull(request.getParameter("isSubject"));	//判断是哪个节目
			
			
			String userpopedom = CHF.showNull(request.getParameter("userpopedom"));	 //用于判断部门是否要加上选择框
			
			String loginid = CHF.showNull(request.getParameter("loginid")); //人员loginid
			String menuid = CHF.showNull(request.getParameter("omenuid")); //菜单ID	
			String joinUser = CHF.showNull(request.getParameter("joinUser")); //菜单ID	
			
			String addUser = CHF.showNull(request.getParameter("addUser")); //用于追加一个人员树
			
			System.out.println(addUser+"|"+checked+"|"+departid+"|"+areaid+"|"+departname+"|"+isSubject);
			
			conn = new DBConnect().getConnect();
			DepartmentService ds = new DepartmentService(conn);
			UserPopedomService up = new UserPopedomService(conn);
			String departments = up.getLoginIdPopedom(loginid, menuid);
			EnterpriseQualificationService eqs = new EnterpriseQualificationService(conn);
			
			ds.setAddUser(addUser); //追加人员树 addUser = "addUser"; 
			
			List list = null;
			if("".equals(isSubject) || "undefined".equals(isSubject)) {
				list = ds.getOrgan(checked);	
				if(list == null){
					//区域表无值，直接展开部门表
					if("userpopedom".equals(userpopedom)){
						checked = "false";
						ds.setUserpopedom(departments);
					}
					departid = "555555";
					list = ds.getDepartment(departid, areaid, checked);
					
				}
			}else{
				if("1".equals(isSubject)){ 
					//如果是1，就表示当前节目是单位，要展开区域
					//1、区域表有值，要展开
					//2、区域表无值，直接展开部门表
					list = ds.getArea(departid,checked,"","");
					if(list == null){
						//区域表无值，直接展开部门表
						if("userpopedom".equals(userpopedom)){
							checked = "false";
							ds.setUserpopedom(departments);
						}
						list = ds.getDepartment(departid, areaid, checked);
						if("true".equals(addUser)){
							List list1 = ds.getUser(departid, checked);
							if(list1 != null){
								if(list == null) list = new ArrayList();
								for(int i = 0;i<list1.size(); i++){
									list.add(list1.get(i));	
								}
							}
						}
					}
				}else{
					//都是展开部门
					if("userpopedom".equals(userpopedom)){
						checked = "false";
						ds.setUserpopedom(departments);
					}
					list = ds.getDepartment(departid, areaid, checked);
					if("true".equals(addUser)){
						List list1 = eqs.getUser(departid, checked,","+joinUser);
						if(list1 != null){
							if(list == null) list = new ArrayList();
							for(int i = 0;i<list1.size(); i++){
								System.out.println(list1.get(i));
								list.add(list1.get(i));
							}
						}
					}
				}
			}
			
			String json = "{}";
			if(list != null){
				json = JSONArray.fromObject(list).toString();
			}
			System.out.println("json="+json);
			response.getWriter().write(json);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
	}

	/**
	 * 草稿跳转
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView draftSkip(HttpServletRequest request, HttpServletResponse response){
			ModelAndView modelAndView = new ModelAndView(REPLYSKIP);
			Connection conn = null;
			ASFuntion asf = new ASFuntion();
			String uuid = asf.showNull(request.getParameter("uuid"));
		try {
			conn = new DBConnect().getConnect();
			if(!"".equals(uuid)){
				
				InteriorEmailService emailService = new InteriorEmailService(conn);
				Email email = emailService.getDraftEmail(uuid);
				//UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
				String tempSql = "SELECT userId FROM `oa_emailuser` WHERE uuid='"+uuid+"' ";
				email.setAddresser("");
				/**
				 * 
				 * 收件人
				 * 
				 * */
				String userIds = emailService.getValueBySql(tempSql+" and ctype='收件人'");
				userIds = this.setStringName(userIds, "");
				
				String userName = "";
				if(!"".equals(userIds)){
					String userNameSql = "SELECT GROUP_CONCAT(name) FROM `k_user` WHERE id in ("+userIds+")";
					userName = new DbUtil(conn).queryForString(userNameSql);
				}
				if(!"".equals(userIds)){
					userIds =userIds+",";
				}
				modelAndView.addObject("userIdAll",userIds);
				modelAndView.addObject("userName",userName);
				
				
				/**
				 * 
				 * 抄送人
				 * 
				 * */
				String copyUserId = emailService.getValueBySql(tempSql+" and ctype='抄送人'");
				copyUserId = this.setStringName(copyUserId, "");
				
				String copyUser = "";
				if(!"".equals(copyUserId)){
					String copySql = "SELECT GROUP_CONCAT(name) FROM `k_user` WHERE id in ("+copyUserId+")";
					copyUser = new DbUtil(conn).queryForString(copySql);
				}
				if(!"".equals(copyUserId)){
					copyUserId =copyUserId+",";
				}
				modelAndView.addObject("copyUser",copyUser);
				modelAndView.addObject("copyUserId",copyUserId);
				
				/**
				 * 
				 * 密送人
				 * 
				 * */
				String secretUserId = emailService.getValueBySql(tempSql+" and ctype='密送人'");
				secretUserId = this.setStringName(secretUserId, "");
				
				String secretUser = "";
				if(!"".equals(secretUserId)){
					String secretSql = "SELECT GROUP_CONCAT(name) FROM `k_user` WHERE id in ("+secretUserId+")";
					secretUser = emailService.getValueBySql(secretSql);
				}
				if(!"".equals(secretUserId)){
					secretUserId =secretUserId+",";
				}
				modelAndView.addObject("secretUser",secretUser);
				modelAndView.addObject("secretUserId",secretUserId);
				
				//收条(第一次阅读提醒)
				String shouSql = "SELECT receiveRemind FROM `oa_emailuser` WHERE uuid='"+uuid+"'";
				String receiveReminds = emailService.getValueBySql(shouSql);
				if(!"".equals(receiveReminds)){
					receiveReminds = this.setStringName(receiveReminds,"否");
					modelAndView.addObject("receiveRemind",receiveReminds);
				}
				
				//手机短信
				String infoSql = "SELECT mobilePhoneRemind FROM `oa_emailuser` WHERE uuid='"+uuid+"' limit 1 ";
				String mobilePhoneReminds = emailService.getValueBySql(infoSql);
				if(!"".equals(receiveReminds)){
					mobilePhoneReminds = this.setStringName(mobilePhoneReminds,"否");
					modelAndView.addObject("mobilePhoneRemind",mobilePhoneReminds);
				}
				
				modelAndView.addObject("email",email);
				//modelAndView.addObject("userName",user.getName());
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return modelAndView;
	}
	
	/**
	 * 草稿 立即发送
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView draftSend(HttpServletRequest request, HttpServletResponse response){
		Connection conn = null;
		ASFuntion asf = new ASFuntion();
		String uuid = asf.showNull(request.getParameter("uuid"));
		String Ifparam = asf.showNull(request.getParameter("Ifparam"));
		String userId = asf.showNull(request.getParameter("userId"));
		String result = "false";
	try {
		//response.setContentType("text/html;charset=utf-8") ;
		response.setCharacterEncoding("utf-8");
		request.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		conn = new DBConnect().getConnect();
		if(!"".equals(uuid)){
			
			InteriorEmailService emailService = new InteriorEmailService(conn);
			Email email = null;
			if(!"".equals(Ifparam)){
				 email = emailService.getDraftEmail(uuid);//在草稿箱点击的 立即发送
			}else{
				email = emailService.getEmail(uuid); //已发送 点击的 再次发送
			}
			
			PlacardService placardService = new PlacardService(conn);
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			String fsName = new DbUtil(conn).queryForString("select name from k_user where id='"+email.getAddresser()+"'");
			
			//得到收件人ID
			String sql = "SELECT group_concat(userId) FROM `oa_emailuser` WHERE uuid='"+uuid+"'";
			if(!"".equals(userId)){
				sql +=" and userId in ("+userId+")";
			}
			String userIds = new DbUtil(conn).queryForString(sql);
		 
			String newUuid = UUID.randomUUID().toString();
			email.setUuid(newUuid);
			
			//查询 是否有短信提示和手机提醒
			String isShowMsgs = new DbUtil(conn).queryForString("SELECT CONCAT(if(instationRemind>'',instationRemind,'否'),',',if(mobilePhoneRemind>'',mobilePhoneRemind,'否')) FROM oa_emailuser WHERE `uuid`='"+uuid+"' LIMIT 1");
			String[] isShowMsg = isShowMsgs.split(",");
			
			//插入邮件人员表
			String innerSql = "INSERT INTO `asdb`.`oa_emailuser`  \n"
							  +" (`uuid`,`userId`,`ctype`,`instationRemind`,`receiveRemind`,`isRead`,`dustbin`,`mobilePhoneRemind`,`readTime`,`readDate`,`property`)\n"
							  +" SELECT '"+newUuid+"',`userId`,`ctype`,`instationRemind`,`receiveRemind`,'否',`dustbin`,`mobilePhoneRemind`,0,`readDate`,`property` \n"
							  +" FROM oa_emailuser WHERE `uuid` = '"+uuid+"'";
			if(!"".equals(userId)){
				innerSql +=" and userId in ("+userId+")";
			}
			int inserUser = new DbUtil(conn).executeUpdate(innerSql);
			
			String todayTime = asf.getCurrentDate()+" "+asf.getCurrentTime();

			if(inserUser>0){
				if(!"".equals(userIds)){
					String[] addresseeId = userIds.split(",");
					
					String sbString=fsName+"您有一份邮件,邮件主题为“"+email.getTitle()+"” " +
									"发件时间为："+todayTime+"，请您及时阅读！";
					
					for (int i = 0; i < addresseeId.length; i++) {
						
						if("是".equals(isShowMsg[0])){
							
							PlacardTable placardTable=new PlacardTable(); 
							
							placardTable.setAddresser(userSession.getUserId());//发起
							placardTable.setAddresserTime(todayTime);
							placardTable.setCaption("邮件提醒");
							placardTable.setMatter(sbString);
							placardTable.setAddressee(addresseeId[i]); //接收的老大UserId
							placardTable.setIsRead(0);
							placardTable.setIsReversion(0);
							placardTable.setIsNotReversion(0);
							placardTable.setUuid(uuid);
							placardTable.setUrl("interiorEmail.do?method=emailMain&isReadOnly=true&back=true");
							placardTable.setUuidName("uuid");
							placardTable.setModel("内部邮件");
							placardService.AddPlacard(placardTable); //记录人发消息
						}

					}
					if("是".equals(isShowMsg[1])){
						//发送手机短信
						String msgcontent =  sbString.replace("<br>", "");
						System.out.println(this.mobilePhoneInfo(request,response,"", userIds, msgcontent)); 
					}
					email.setAddressee(userIds);
					email.setSendDate(todayTime);
					emailService.addEmail(email);
				}
			}
			
			
			result="true";
			out.write(result);
		}else{
			out.write(result);
		}
		
	} catch (Exception e) {
		e.printStackTrace();
	}finally{
		DbUtil.close(conn);
	}
	return null;
}

	/**
	 *得到数量
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView getCount(HttpServletRequest request, HttpServletResponse response){
		Connection conn = null;
		ASFuntion asf = new ASFuntion();
		String sql = asf.showNull(request.getParameter("sql"));
	try {
		conn = new DBConnect().getConnect();
		response.setCharacterEncoding("utf-8");
		request.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		if(!"".equals(sql)){
			
			InteriorEmailService emailService = new InteriorEmailService(conn);
			String counts = emailService.getValueBySql(sql);
			
			if("0".equals(counts) || "".equals(counts)){
				out.write("0");
			}else{
				String[] count = counts.split("@`@"); //未读
				out.write(count[0]);
			}
		}
		
	} catch (Exception e) {
		e.printStackTrace();
	}finally{
		DbUtil.close(conn);
	}
	return null;
}
	
 	/**
	 * 发送手机短信()
	 * @param mobiPhone 手机号码  可以有多个. 如:13560126012,13560126013,13560126014
	 * @param userIds 用户ID，可以有多个. 如:19,1111,2222
	 * @param content 要发送手机短信的内容
	 * @return 返回发送的结果
	 * @throws Exception
	 */
	public String mobilePhoneInfo(HttpServletRequest request, HttpServletResponse response,String mobiPhone,String userIds,String content) throws Exception{
		
		String msg = "userId或手机号码不能为空";
		Connection conn = null;
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			
		try{
			conn = new DBConnect().getConnect();
			SmsService sms=new SmsService(conn);
			msg= sms.sendSms(userSession.getUserId(), userIds, mobiPhone, content);
			
		}finally{
			DbUtil.close(conn);
		}
		return msg;
	}
	 
	//验证是否是手机号
	public boolean getIfmobilePhone(String mobilePhone){
		
		return  Pattern.matches("^(13[0-9]|15[0-9]|18[7|8|9|6|5|3])\\d{4,8}", mobilePhone); //验证手机号
	}
	
	/**
	 * 根据角色ID，得到 用户ID，支持多个角色ID，如：11,22,33
	 * @param roleId
	 * @return
	 */
	public String getUserIdByRoleId(String roleId){
		
		Connection conn = null;
		String userIds = "";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = new DBConnect().getConnect();
			String sql = "SELECT DISTINCT userid FROM k_userRole WHERE rid IN ("+roleId+")";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				userIds = rs.getString(1)+",";
			}
			if(!"".equals(userIds)){
				userIds = userIds.substring(0, userIds.length()-1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return userIds;
	}
	
	/**
	 * 同步加载 角色树
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView getRoleList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		response.setContentType("text/html;charset=utf-8");
		
		ASFuntion af = new ASFuntion();
		
		String roleId = af.showNull(request.getParameter("id"));
		
		Connection conn = null;
		
		StringBuffer sb = new StringBuffer();
		PreparedStatement ps = null;
		ResultSet rs = null;
		//sb.append("[");
		List treeList = new ArrayList() ;
		try {
			
			conn = new DBConnect().getConnect();
		
			List roleList = new EnterpriseQualificationService(conn).getRoleList();

			boolean check = false;
			Map map = null;
			Map childTreeNode = null;
			
						
			for(int i=0; i < roleList.size(); i++) {
				RoleTable roletable = (RoleTable)roleList.get(i);
 
				map = new HashMap();
				map.put("text", roletable.getRolename());
				map.put("id", "roleId_"+roletable.getId());
				map.put("leaf", false);
				map.put("checked", false);
				StringBuffer sql = new StringBuffer();
				sql.append("select b.id,b.name,b.loginid from k_userRole a " );
				sql.append("inner join k_user b on a.userid = b.id \n  " );
				sql.append(" where rid='"+roletable.getId()+"' and b.state=0 ORDER BY b.name ASC ");

				ps = conn.prepareStatement(sql.toString()) ;
				rs = ps.executeQuery() ;
				List childList = new ArrayList() ;
				while(rs.next()) {
					childTreeNode = new HashMap();
					childTreeNode.put("id",rs.getString(1)) ;
					childTreeNode.put("text", rs.getString(2));
					childTreeNode.put("userName",rs.getString(2)) ;
					childTreeNode.put("loginid", rs.getString("loginid"));
					childTreeNode.put("leaf",true) ;
					childTreeNode.put("checked",false) ;
					childList.add(childTreeNode) ;
				}
				map.put("children",childList) ;
				treeList.add(map) ;	
			}
			//sb.append("]");
			
			//System.out.println(" 角色 sb.toString()="+sb.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		
		//response.getWriter().write(sb.toString());
		String jsonStr = JSONArray.fromObject(treeList).toString() ;
		response.getWriter().write(jsonStr) ;
		return null;
	}
	
	/**
	 * 异步加载 角色树
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView getRoleTree(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		response.setContentType("text/html;charset=utf-8");
		
		ASFuntion af = new ASFuntion();
		
		String roleId = af.showNull(request.getParameter("id"));
		roleId = roleId.replaceAll("roleId_", "");
		Connection conn = null;
		
		StringBuffer sb = new StringBuffer();
		PreparedStatement ps = null;
		ResultSet rs = null;
		//sb.append("[");
		List treeList = new ArrayList() ;
		try {
			
			conn = new DBConnect().getConnect();
		
			List roleList = new EnterpriseQualificationService(conn).getRoleList();

			boolean check = false;
			if("0".equals(roleId)){
				for(int i=0; i < roleList.size(); i++) {
					RoleTable roletable = (RoleTable)roleList.get(i);
					Map map = new HashMap();
					map.put("text", roletable.getRolename());
					map.put("id", "roleId_"+roletable.getId());
					map.put("leaf", false);
					map.put("checked", false);
					treeList.add(map) ;	
				}
			}else{
				StringBuffer sql = new StringBuffer();
				sql.append("select DISTINCT b.id,b.name,b.loginid from k_userRole a " );
				sql.append("inner join k_user b on a.userid = b.id \n  " );
				sql.append(" where rid='"+roleId+"' and b.state=0 ORDER BY b.name ASC ");
				
				ps = conn.prepareStatement(sql.toString()) ;
				rs = ps.executeQuery() ;
				while(rs.next()) {
					Map childTreeNode = new HashMap();
					childTreeNode.put("id",rs.getString(1)) ;
					childTreeNode.put("text", rs.getString(2));
					childTreeNode.put("userName",rs.getString(2)) ;
					childTreeNode.put("loginid", rs.getString("loginid"));
					childTreeNode.put("leaf",true) ;
					childTreeNode.put("checked",false) ;

					treeList.add(childTreeNode) ;
				}
			}			
			System.out.println(treeList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		String jsonStr = JSONArray.fromObject(treeList).toString() ;
		response.getWriter().write(jsonStr) ;
		return null;
	}
	//回复全部
	public ModelAndView replyAll(HttpServletRequest request,
			HttpServletResponse response){
		
		ASFuntion asf = new ASFuntion();
		ModelAndView modelAndView = new ModelAndView(EPISTOLIZE);
		String uuid = asf.showNull(request.getParameter("uuid"));
		Connection conn = null;
		try {
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			if(!"".equals(uuid)){
				conn = new DBConnect().getConnect();

				InteriorEmailService emailService = new InteriorEmailService(conn);
				Email email = emailService.getEmail(uuid);
				//发送人
				String addresser = asf.showNull(new DbUtil(conn).queryForString("SELECT addresser FROM oa_email WHERE   `uuid`='"+uuid+"'"));
				
				//接收人
				String addressess = asf.showNull(new DbUtil(conn).queryForString("SELECT GROUP_CONCAT(DISTINCT userId) FROM oa_emailuser WHERE `uuid`='"+uuid+"'and userId<>'"+userSession.getUserId()+"' AND (ctype = '收件人' OR ctype = '抄送人' OR ctype = '密送人')"));
				
				String userIdAll = addressess;
				
				if(!"".equals(addresser)){
					 
					if(!"".equals(addressess)){
						userIdAll = addresser + "," +addressess;
					}else{
						userIdAll = addresser;
					}
				}
				
				if(!"".equals(userIdAll)){
					String   userName = new DbUtil(conn).queryForString("SELECT GROUP_CONCAT(name) FROM k_user WHERE `id` in ("+userIdAll+") ");
					modelAndView.addObject("userName", userName);
				}
				email.setAddresser("");
				String titleInfo = new DbUtil(conn).queryForString("SELECT CONCAT(b.`Name`,'在',a.`sendDate`,'的来信中写道:') FROM oa_email a LEFT JOIN k_user b ON a.`addresser` = b.id WHERE a.`uuid` = '"+uuid+"'");
				modelAndView.addObject("titleInfo", titleInfo);
				modelAndView.addObject("userIdAll", userIdAll+",");
				email.setTitle("Re："+email.getTitle());
				modelAndView.addObject("email", email);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return modelAndView;
	}
	
	//转发
	public ModelAndView transmit(HttpServletRequest request,
			HttpServletResponse response){
		
		ASFuntion asf = new ASFuntion();
		ModelAndView modelAndView = new ModelAndView(EPISTOLIZE);
		String uuid = asf.showNull(request.getParameter("uuid"));
		Connection conn = null;
		try {
			if(!"".equals(uuid)){
				
				conn = new DBConnect().getConnect();
				
				InteriorEmailService ie = new InteriorEmailService(conn);
				Email email  = ie.getEmail(uuid);
				email.setAddressee("");
				email.setAddresser("");
				email.setSendDate("");
				email.setTitle("转发:"+email.getTitle());
				modelAndView.addObject("email", email);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return modelAndView;
	}
	
	/**
	 * 根据一条sql返回一个String
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView getStringSql(HttpServletRequest request, HttpServletResponse response){
		Connection conn = null;
		ASFuntion asf = new ASFuntion();
		String sql = asf.showNull(request.getParameter("sql"));
	try {
		conn = new DBConnect().getConnect();
		response.setCharacterEncoding("utf-8");
		request.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		String ruturnString = "";
		if(!"".equals(sql)){
			ruturnString = asf.showNull(new DbUtil(conn).queryForString(sql));
		}
		out.write(ruturnString);
	} catch (Exception e) {
		e.printStackTrace();
	}finally{
		DbUtil.close(conn);
	}
	return null;
}
	/**
	 * 已发送 撤销 未阅读的人员
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView repealEmail(HttpServletRequest request,
			HttpServletResponse response){
		
		ASFuntion asf = new ASFuntion();
		String uuids = asf.showNull(request.getParameter("uuid"));
		Connection conn = null;
		try {
			if(!"".equals(uuids)){
				
				conn = new DBConnect().getConnect();
				String[] uuid = uuids.split(",");
				
				for (int i = 0; i < uuid.length; i++) {

					if(!"".equals(uuid[i])){
						
						new DbUtil(conn).execute("DELETE FROM `oa_emailuser` WHERE `uuid`='"+uuid[i]+"' AND (isRead='否' OR readTime=0)");
						
						new DbUtil(conn).execute(" update oa_email set status ='已撤销' WHERE `uuid`='"+uuid[i]+"' ");
					}
				}
			}
			
			response.sendRedirect(request.getContextPath()+"/interiorEmail.do?method=sendList");
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return null;
	}
	
	/**
	 * ajax撤销未读邮件
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView ajaxRepealEmail(HttpServletRequest request,
			HttpServletResponse response){
		
		ASFuntion asf = new ASFuntion();
		String uuid = asf.showNull(request.getParameter("uuid"));
		
		String autoId = asf.showNull(request.getParameter("autoId"));
		
		Connection conn = null;
		try {
			String result = "false";
			conn = new DBConnect().getConnect();

			if(!"".equals(uuid)){
				//InteriorEmailService ie = new InteriorEmailService(conn);
				new DbUtil(conn).execute("DELETE FROM `oa_emailuser` WHERE `uuid`='"+uuid+"' AND (isRead='否' OR readTime=0)");
				
				new DbUtil(conn).execute(" update oa_email set status ='已撤销' WHERE `uuid`='"+uuid+"' ");
				result = "true";
			}
			
			if(!"".equals(autoId)){
				new DbUtil(conn).executeUpdate("DELETE FROM `oa_emailuser` WHERE `autoId` = "+autoId+" ");
				result = "true";
			}
			
			response.getWriter().write(result);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return null;
	}
	
	/**
	 * ajax发送手机短信
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView ajaxMbMsg(HttpServletRequest request,
			HttpServletResponse response){
		
		ASFuntion asf = new ASFuntion();
		Connection conn = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");

			String userId = asf.showNull(request.getParameter("userId"));
			String uuid = asf.showNull(request.getParameter("uuid"));
			
			conn = new DBConnect().getConnect();

			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");

			if(!"".equals(userId)){
				InteriorEmailService emailService = new InteriorEmailService(conn);
				Email email = emailService.getEmail(uuid);
				
				String todayTime = asf.getCurrentDate()+" "+asf.getCurrentTime();
				String sbString="您有一份新邮件，发信人是："+userSession.getUserName()+" "
									+"发送时间为："+todayTime+" "
									+"邮件标题为："+email.getTitle()+" "
									+"重要性："+email.getImportance()+" "
									+"请您及时查看！"; 
				String msg = this.mobilePhoneInfo(request, response, "", userId, sbString);
				
				response.getWriter().write(msg);
			}
			 
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return null;
	}
	
	/**
	 * 已发送  删除记录(其实是改变邮件状态)
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView sendDel(HttpServletRequest request,
			HttpServletResponse response){
		
		ASFuntion asf = new ASFuntion();
		String uuids = asf.showNull(request.getParameter("uuid"));
		Connection conn = null;
		try {
			if(!"".equals(uuids)){
				
				conn = new DBConnect().getConnect();
				String[] uuid = uuids.split(",");
				for (int i = 0; i < uuid.length; i++) {
					if(!"".equals(uuid[i])){
						new DbUtil(conn).execute(" update oa_email set status ='发件箱已删除' WHERE `uuid`='"+uuid[i]+"' ");
					}
				}
			}
			
			response.sendRedirect(request.getContextPath()+"/interiorEmail.do?method=sendList");
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return null;
	}
	
	/**
	 * 检查内容
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView checkContent(HttpServletRequest request,
			HttpServletResponse response){
		
		ASFuntion asf = new ASFuntion();
		boolean result = false;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");

			String checkContent = asf.showNull(request.getParameter("checkContent"));
			
				
			if(!"".equals(checkContent)){
				result = TagsChecker.check(checkContent); //标签闭合性检查
				if(!result){
					checkContent = TagsChecker.fix(checkContent); //修复没有闭合的标签
					System.out.println(checkContent);
				}
				result = TagsChecker.check(checkContent); //标签闭合性检查
			}
				
			response.getWriter().write("<script type=\"text/javascript\">window.parent.goCheckContent('"+result+"')</script>");
			 
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
		}
		return null;
	}
	
	/**
	 * 回复邮件
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView recoverEmail(HttpServletRequest request,
			HttpServletResponse response){
		
		ASFuntion asf = new ASFuntion();
		String uuid = asf.showNull(request.getParameter("uuids"));
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		Connection conn = null;
		try {
			if(!"".equals(uuid)){
				
				conn = new DBConnect().getConnect();
				if(!"".equals(uuid)){
					String[] uuids = uuid.split(",");
					for (int i = 0; i < uuids.length; i++) {
						if(!"".equals(uuids[i])){
							new DbUtil(conn).execute(" UPDATE `oa_emailuser` SET dustbin = '否' WHERE `uuid` = '"+uuids[i]+"' AND userId='"+userSession.getUserId()+"'");
						}
					}
				}
			}
			
			response.sendRedirect(request.getContextPath()+"/interiorEmail.do?method=deletedList");
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return null;
	}
	
	public ModelAndView goSendEmailByIds(HttpServletRequest request,
			HttpServletResponse response){
		
		ASFuntion asf = new ASFuntion();
		
		ModelAndView modelAndView = null;
		try {
			String ids = asf.showNull(request.getParameter("ids"));
			if(!"".equals(ids)){
				modelAndView = new ModelAndView(emailMain);
				modelAndView.addObject("userIdAll",ids);
			}else{
				String userIdAll = asf.showNull(request.getParameter("userIdAll"));
				modelAndView = new ModelAndView(EPISTOLIZE);
				modelAndView.addObject("userIdAll",userIdAll);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
		}
		return modelAndView;
	}
}
