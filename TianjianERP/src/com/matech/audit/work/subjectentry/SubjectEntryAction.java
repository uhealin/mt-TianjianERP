package com.matech.audit.work.subjectentry;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.del.JRockey2Opp;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.pub.db.GetResult;
import com.matech.audit.pub.func.ASTextKey;
import com.matech.audit.service.autotoken.AutoTokenService;
import com.matech.audit.service.checkInfo.PrintModel;
import com.matech.audit.service.project.ProjectService;
import com.matech.audit.service.search.takeOutVoucher;
import com.matech.audit.work.assitem.Assitem;
import com.matech.audit.work.system.CommonSecurity;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.autocode.DELUnid;
import com.matech.framework.pub.datagrid.FormatType;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.sys.UTILSysProperty;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;
import com.matech.framework.pub.util.UTILString;
import com.matech.framework.service.print.PrintSetup;
import com.matech.framework.service.sysmenu.sysMenuMan;

public class SubjectEntryAction extends MultiActionController{
	private final String _strSuccess = "/subjectentry/List.jsp";
	private final String _strBList = "/subjectentry/BList.jsp";
	private final String _strMList = "/subjectentry/MList.jsp";
	private final String _strGList = "/subjectentry/GList.jsp";
	private final String _strFList = "/subjectentry/FList.jsp";
	private final String _strVList = "/subjectentry/VList.jsp";

	private final String _strAList = "/subjectentry/AList.jsp";
	
	private final String _sortList = "/subjectentry/SortList.jsp";		//详细凭证
	
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		Connection conn=null;
		HashMap mapResult = new HashMap();
		try {
			ASFuntion CHF=new ASFuntion();
			System.out.println("明细账1："+CHF.getCurrentTime());
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");

			String T1 = CHF.showNull(request.getParameter("T1"));
			String acc = CHF.showNull(request.getParameter("AccPackageID"));
			
			String html = CHF.showNull(request.getParameter("html"));	//用于标志要不要用setTdtoValue　1为要　
			String noSubject = request.getParameter("noSubject"); //只显示有核算的科目
			String addAssItem = request.getParameter("addAssItem"); //是否显示核算为下级
			
			String projectid = CHF.showNull(userSession.getCurProjectId());
			if("".equals(acc)){
				acc = CHF.showNull(userSession.getCurChoiceAccPackageId());
				if("".equals(acc)){
					acc = CHF.showNull(userSession.getCurAccPackageId());
				}
			}else{
				userSession.setCurChoiceAccPackageId(acc);
			}
			if(!"".equals(acc)){
				if("".equals(T1)){
					T1=acc.substring(0,6);
				}
			}
			
			if(!"".equals(acc)){
				conn = new DBConnect().getConnect(T1);

				ASTextKey tkey = new ASTextKey(conn);
				String TName = "";
				if(!"".equals(T1)) TName = tkey.TextCustomerName(T1);

				String BeginYear = CHF.showNull(request.getParameter("BeginYear"));
				if("".equals(BeginYear) && !"".equals(acc)){
					BeginYear = acc.substring(6);
				}
				
				if("".equals(BeginYear)){
					BeginYear = CHF.showNull(userSession.getCurChoiceBeginYear());
					if("".equals(BeginYear)){
						BeginYear = CHF.showNull(userSession.getCurProjectBeginYear());
					}
				}else{
					userSession.setCurChoiceBeginYear(BeginYear);
				}
				

				String EndYear = CHF.showNull(request.getParameter("EndYear"));
				if("".equals(EndYear) && !"".equals(acc)){
					EndYear = acc.substring(6);
				}
				if("".equals(EndYear)){
					EndYear = CHF.showNull(userSession.getCurChoiceEndYear());
					if("".equals(EndYear)){ 
						EndYear = CHF.showNull(userSession.getCurProjectEndYear());
					}
				}else{
					userSession.setCurChoiceEndYear(EndYear);
				}
				

				String BeginDate = CHF.showNull(request.getParameter("BeginDate"));
				if("".equals(BeginDate)){
					BeginDate = CHF.showNull(userSession.getCurChoiceBeginMonth());
					if("".equals(BeginDate)){
						BeginDate = CHF.showNull(userSession.getCurProjectBeginMonth());
					}
				}else{
					userSession.setCurChoiceBeginMonth(BeginDate);
				}
				if("".equals(BeginDate)) BeginDate = "01";

				String EndDate = CHF.showNull(request.getParameter("EndDate"));
				if("".equals(EndDate)){
					EndDate = CHF.showNull(userSession.getCurChoiceEndMonth());
					if("".equals(EndDate)){
						EndDate = CHF.showNull(userSession.getCurProjectEndMonth());
					}
				}else{
					userSession.setCurChoiceEndMonth(EndDate);
				}
				if("".equals(EndDate)) EndDate = "12";

				String SubjectID = CHF.showNull(request.getParameter("SubjectID"));
				String AssItemID = CHF.showNull(request.getParameter("AssItemID"));
				
				
				SubjectEntry subjectEntry = new SubjectEntry(conn);
				String SubjectId1 = "";
				if(!"".equals(SubjectID)) {
					if("".equals(subjectEntry.getSName1(acc, SubjectID))){
						SubjectID = "";
					} else {
						SubjectId1 = subjectEntry.getSName1(acc, SubjectID);
					}
				}

				/**
				 * 没有狗不能看2005年后的帐套
				 */
				int accAllowYear = Integer.parseInt(UTILSysProperty.SysProperty.getProperty("accAllowYear")); 
				if (Integer.parseInt(EndYear) > accAllowYear && JRockey2Opp.getUserLic() <= 0) {
				    final String TRY_URL = "/AuditSystem/AS_SYSTEM/error_page.jsp?tip=999";
				    response.sendRedirect(TRY_URL);
				    return null;
				}

//				String Subjects = subjectEntry.getSubjectss(acc, SubjectID);
//				String Subjects1 = subjectEntry.getSubTree(acc, SubjectID);
				SubjectID = SubjectId1;

				String currency = CHF.showNull(request.getParameter("currency"));
				if("".equals(currency) && !"2".equals(noSubject)) currency = "1";
				
				String toSubjects = CHF.showNull(request.getParameter("toSubjects"));
				if("".equals(toSubjects)) toSubjects = "1";
				String toLevel1 = CHF.showNull(request.getParameter("level0"));
				if("".equals(toLevel1)) toLevel1 = "1";
				String information = CHF.showNull(request.getParameter("information"));

				//是控件搞出来的
				String inOcx = CHF.showNull((String)request.getParameter("inOcx"));
				String taskId = CHF.showNull((String)request.getParameter("taskId"));
				if("".equals(SubjectID)){
					String strSql = "select b.subjectid From z_task a,c_account b " +
					"	where 1=1 " +
					"	and a.projectID = '"+projectid+"' " +
					"	and a.taskid = '"+taskId+"' " +
					"	and b.AccPackageID = '"+acc+"' " +
					"	and b.submonth = 1 " +
					"	and a.subjectname = b.subjectfullname2 ";
					SubjectID = CHF.showNull(new DbUtil(conn).queryForString(strSql));
				}
				mapResult.put("inOcx", inOcx);
				System.out.println("qwh:"+inOcx + "|SubjectID="+SubjectID);

				mapResult.put("T1", T1);
				mapResult.put("TName", TName);
				mapResult.put("BeginYear", BeginYear);
				mapResult.put("BeginDate", BeginDate);
				mapResult.put("EndYear", EndYear);
				mapResult.put("EndDate", EndDate);
				mapResult.put("SubjectID", SubjectID);
//				mapResult.put("Subjects", Subjects);
//				mapResult.put("Subjects1", Subjects1);
				mapResult.put("currency", currency);
				mapResult.put("toSubjects", toSubjects);
				mapResult.put("level0", toLevel1);
				mapResult.put("information", information);
				
				mapResult.put("AssItemID", AssItemID);
				
				
				if(!"1".equals(toSubjects)){
					mapResult.put("disabled", "disabled");	
				}
				
			}else{
				mapResult.put("SubjectID", "");
				mapResult.put("BeginDate", "01");
				mapResult.put("EndDate", "12");
			}

			mapResult.put("AccPackageID", acc);
			mapResult.put("projectid", projectid);

			mapResult.put("html", html);
			
			mapResult.put("noSubject", noSubject);
			mapResult.put("addAssItem", addAssItem);
			System.out.println("明细账2："+CHF.getCurrentTime());
			
		} catch (Exception e) {
			Debug.print(Debug.iError, "科目明细账查询失败！", e);
            throw e;
		} finally {
			DbUtil.close(conn);
		}
		return new ModelAndView(_strSuccess,mapResult);
	}

	public ModelAndView centerpage(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		Connection conn=null;
//		HashMap mapResult = new HashMap();
		try {
			ASFuntion CHF=new ASFuntion();
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
			
			String html = CHF.showNull(request.getParameter("html"));	//用于标志要不要用setTdtoValue　1为要　
			
			String TabName = CHF.showNull(request.getParameter("tab"));

			String T1 = CHF.showNull(request.getParameter("T1"));

			String Currency = CHF.showNull(request.getParameter("Currency"));//只显示本位币

			String toSubjects = CHF.showNull(request.getParameter("toSubjects"));//显示对方科目
			
			String toLevel1 = CHF.showNull(request.getParameter("toLevel1"));//对方科目显示的级数

			String moneyChao = CHF.showNull(request.getParameter("moneyChao"));//显示对方科目
			String allOpt = CHF.showNull(request.getParameter("allOpt"));//显示对方科目


			String BeginYear = CHF.showNull(request.getParameter("BeginYear"));
			if("".equals(BeginYear)){
				BeginYear = CHF.showNull(userSession.getCurChoiceBeginYear());
				if("".equals(BeginYear)){
					BeginYear = CHF.showNull(userSession.getCurProjectBeginYear());
				}
			}else{
				userSession.setCurChoiceBeginYear(BeginYear);
			}

			String EndYear = CHF.showNull(request.getParameter("EndYear"));
			if("".equals(EndYear)){
				EndYear = CHF.showNull(userSession.getCurChoiceEndYear());
				if("".equals(EndYear)){
					EndYear = CHF.showNull(userSession.getCurProjectEndYear());
				}
			}else{
				userSession.setCurChoiceEndYear(EndYear);
			}

			String BeginDate = CHF.showNull(request.getParameter("BeginDate"));
			if("".equals(BeginDate)){
				BeginDate = CHF.showNull(userSession.getCurChoiceBeginMonth());
				if("".equals(BeginDate)){
					BeginDate = CHF.showNull(userSession.getCurProjectBeginMonth());
				}
			}else{
				userSession.setCurChoiceBeginMonth(BeginDate);
			}
			if("".equals(BeginDate)) BeginDate = "01";

			String EndDate = CHF.showNull(request.getParameter("EndDate"));
			if("".equals(EndDate)){
				EndDate = CHF.showNull(userSession.getCurChoiceEndMonth());
				if("".equals(EndDate)){
					EndDate = CHF.showNull(userSession.getCurProjectEndMonth());
				}
			}else{
				userSession.setCurChoiceEndMonth(EndDate);
			}
			if("".equals(EndDate)) EndDate = "12";

			String SubjectID = CHF.showNull(request.getParameter("SubjectID"));

			String acc = T1+EndYear;
			userSession.setCurChoiceAccPackageId(acc);

			conn = new DBConnect().getConnect(T1);
			
			int result = new SubjectEntry(conn).SubjectProperty(acc,SubjectID);
		
			if("1".equals(Currency)){
				result = 0;
			}

			
			/*
			0:非叶子与无外币和无数量的叶子
			1:叶子有外币无数量
			2:叶子无外币有数量
			3:叶子有外币有数量
			4:复币
			*/

			switch(result){
				case 0://bonelist
					response.sendRedirect("subjectentry.do?method=bonelist&Currency="+Currency+"&TabName="+TabName+"&T1="+T1+"&SubjectID="+SubjectID+"&toLevel1="+toLevel1+"&toSubjects="+toSubjects+"&moneyChao="+moneyChao+"&allOpt="+allOpt+"&BeginYear="+BeginYear+"&EndYear="+EndYear+"&BeginDate="+BeginDate+"&EndDate="+EndDate+"&AccPackageID="+acc+"&html="+html);
					break;
				case 1://btwolist
					response.sendRedirect("subjectentry.do?method=btwolist&TabName="+TabName+"&T1="+T1+"&SubjectID="+SubjectID+"&toSubjects="+toSubjects+"&toLevel1="+toLevel1+"&moneyChao="+moneyChao+"&allOpt="+allOpt+"&BeginYear="+BeginYear+"&EndYear="+EndYear+"&BeginDate="+BeginDate+"&EndDate="+EndDate+"&AccPackageID="+acc+"&html="+html);
					break;
				case 2://bthreelist
					response.sendRedirect("subjectentry.do?method=bthreelist&TabName="+TabName+"&T1="+T1+"&SubjectID="+SubjectID+"&toSubjects="+toSubjects+"&toLevel1="+toLevel1+"&moneyChao="+moneyChao+"&allOpt="+allOpt+"&BeginYear="+BeginYear+"&EndYear="+EndYear+"&BeginDate="+BeginDate+"&EndDate="+EndDate+"&AccPackageID="+acc+"&html="+html);
					break;
				case 3://bfourlist
					response.sendRedirect("subjectentry.do?method=bfourlist&TabName="+TabName+"&T1="+T1+"&SubjectID="+SubjectID+"&toSubjects="+toSubjects+"&toLevel1="+toLevel1+"&moneyChao="+moneyChao+"&allOpt="+allOpt+"&BeginYear="+BeginYear+"&EndYear="+EndYear+"&BeginDate="+BeginDate+"&EndDate="+EndDate+"&AccPackageID="+acc+"&html="+html);
					break;
			}


		} catch (Exception e) {
			Debug.print(Debug.iError, "科目明细账查询失败！", e);
            throw e;
		} finally {
			DbUtil.close(conn);
		}
		return null;
	}

	private HashMap onelist(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		response.setContentType("text/html;charset=utf-8");
		Connection conn=null;
		HashMap mapResult = new HashMap();
		try {
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
			ASFuntion CHF=new ASFuntion();

			String user=CHF.showNull(userSession.getUserId());
			String projectid=CHF.showNull(userSession.getCurProjectId());
			if("".equals(projectid)) projectid = "-1";

			String T1 = CHF.showNull(request.getParameter("T1"));
			String TabName = CHF.showNull(request.getParameter("TabName"));

			String acc = CHF.showNull(request.getParameter("AccPackageID"));
			if("".equals(acc)){
				acc = CHF.showNull(userSession.getCurChoiceAccPackageId());
				if("".equals(acc)){
					acc = CHF.showNull(userSession.getCurAccPackageId());
				}
			}

			String BeginYear = CHF.showNull(request.getParameter("BeginYear"));
			if("".equals(BeginYear)){
				BeginYear = CHF.showNull(userSession.getCurChoiceBeginYear());
				if("".equals(BeginYear)){
					BeginYear = CHF.showNull(userSession.getCurProjectBeginYear());
				}
			}
			String EndYear = CHF.showNull(request.getParameter("EndYear"));
			if("".equals(EndYear)){
				EndYear = CHF.showNull(userSession.getCurChoiceEndYear());
				if("".equals(EndYear)){
					EndYear = CHF.showNull(userSession.getCurProjectEndYear());
				}
			}
			String BeginDate = CHF.showNull(request.getParameter("BeginDate"));
			if("".equals(BeginDate)){
				BeginDate = CHF.showNull(userSession.getCurChoiceBeginMonth());
				if("".equals(BeginDate)){
					BeginDate = CHF.showNull(userSession.getCurProjectBeginMonth());
				}
			}
			String EndDate = CHF.showNull(request.getParameter("EndDate"));
			if("".equals(EndDate)){
				EndDate = CHF.showNull(userSession.getCurChoiceEndMonth());
				if("".equals(EndDate)){
					EndDate = CHF.showNull(userSession.getCurProjectEndMonth());
				}
			}

			String SubjectID = CHF.showNull(request.getParameter("SubjectID"));
			String Currency = CHF.showNull(request.getParameter("Currency"));

			conn = new DBConnect().getConnect(T1);
			SubjectEntry subjectEntry = new SubjectEntry(conn);

//			PrintWriter out = response.getWriter();

			if(!subjectEntry.ExistsTable(TabName)){
				int result = subjectEntry.LowSubjectProperty(acc,SubjectID);
				mapResult.put("result", new Integer(result));

//				if("0".equals(Currency)){
//					switch(result){
//						case 0:
//							break;
//						case 1:
//							out.println("<script>alert(\"该科目["+SubjectID+"]的下级科目存在外币帐。如要查看外币账,请选择下级科目；本科目只显示本币金额! \");</script>");
//							break;
//						case 2:
//							out.println("<script>alert(\"该科目["+SubjectID+"]的下级科目存在数量帐。如要查看数量账,请选择下级科目；本科目只显示本币金额! \");</script>");
//							break;
//						case 3:
//							out.println("<script>alert(\"该科目["+SubjectID+"]的下级科目存在外币帐和数量帐。如要查看外币和数量账,请选择下级科目；本科目只显示本币金额! \");</script>");
//							break;
//					}
//				}

				subjectEntry.CreateTable(TabName);
				subjectEntry.DataToTable(TabName,user,projectid,T1,SubjectID,BeginYear,BeginDate,EndYear,EndDate);
			}

			String sTable = "";
			if(!"".equals(subjectEntry.getSubFullName(acc,SubjectID))){
				sTable = "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tr><td>科目名称：<font color=\"blue\">"+subjectEntry.getSubFullName(acc,SubjectID)+"</font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;期间：<font color=\"blue\">"+BeginYear+"年"+BeginDate+"月－"+EndYear+"年"+EndDate+"月</font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;本位币名称：<font color=\"blue\">"+new ASTextKey(conn).getACurrRate(acc)+"</font></td></tr></table>";
			}
			mapResult.put("SubjectID", SubjectID);
			mapResult.put("TabName", TabName);
			mapResult.put("T1", T1);
			mapResult.put("sTable", sTable);
			mapResult.put("Currency", Currency);

			return mapResult;

		} catch (Exception e) {
			Debug.print(Debug.iError, "科目明细账查询失败 非叶子与无外币和无数量的叶子！", e);
            throw e;
		} finally {
			DbUtil.close(conn);
		}
	}

	public ModelAndView bonelist(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		Connection conn=null;
		HashMap mapResult = new HashMap();
		try {
			mapResult = onelist(request,response);

			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");

			ASFuntion CHF=new ASFuntion();
			
			String T1 = CHF.showNull(request.getParameter("T1"));
			String TabName = CHF.showNull(request.getParameter("TabName"));
			String SubjectID = CHF.showNull(request.getParameter("SubjectID"));

			String acc = CHF.showNull(request.getParameter("AccPackageID"));
			if("".equals(acc)){
				acc = CHF.showNull(userSession.getCurChoiceAccPackageId());
				if("".equals(acc)){
					acc = CHF.showNull(userSession.getCurAccPackageId());
				}
			}

			String user=CHF.showNull(userSession.getUserId());
			String projectid=CHF.showNull(userSession.getCurProjectId());
			if("".equals(projectid)) projectid = "-1";

			String toSubjects = CHF.showNull(request.getParameter("toSubjects"));//显示对方科目
			String sqlwhere = "";
			if(!toSubjects.equals("1")){
				sqlwhere = " and  1=2 ";
			}
			String toLevel1 = CHF.showNull(request.getParameter("toLevel1"));//对方科目显示的级数
			String subjectnameSql = "";
			if(toLevel1.equals("1")){
				subjectnameSql = "if(locate('/',b.subjectfullname1)=0,b.subjectfullname1,substr(b.subjectfullname1,1,locate('/',b.subjectfullname1)-1))";
			}else{
//				subjectnameSql =  "if(b.subjectname1!=b.subjectfullname1 && concat(if(locate('/',b.subjectfullname1)=0,b.subjectfullname1,substr(b.subjectfullname1,1,locate('/',b.subjectfullname1)-1)),'/',b.subjectname1)!=b.subjectfullname1 \n"
//					 +" ,concat(if(locate('/',b.subjectfullname1)=0,b.subjectfullname1,substr(b.subjectfullname1,1,locate('/',b.subjectfullname1)-1)),'/../',b.subjectname1) \n"
//					 +" ,b.subjectfullname1) ";
				subjectnameSql =  " b.subjectfullname1 ";
			}
//			String moneyChao = CHF.showNull(request.getParameter("moneyChao"));//显示对方科目
//			String allOpt = CHF.showNull(request.getParameter("allOpt"));//显示对方科目

			conn = new DBConnect().getConnect(T1);
			SubjectEntry subjectEntry = new SubjectEntry(conn);
			
			String BeginYear = CHF.showNull(request.getParameter("BeginYear"));
			String EndYear = CHF.showNull(request.getParameter("EndYear"));
			String BeginDate = CHF.showNull(request.getParameter("BeginDate"));
			String EndDate = CHF.showNull(request.getParameter("EndDate"));
			
			/**
			 * DataGrid
			 */
			if(!"".equals(SubjectID)){
				DataGridProperty pp = new DataGridProperty(){
					public void onSearch (
		    				javax.servlet.http.HttpSession session,
		    				javax.servlet.http.HttpServletRequest request,
		    				javax.servlet.http.HttpServletResponse response) throws Exception{

						String TabName = this.getRequestValue("TabName");

						String allOpt = this.getRequestValue("allOpt");
						String moneyChao = this.getRequestValue("moneyChao");
						
						if(moneyChao == null || "".equals(moneyChao.trim())){
							allOpt = "0";
						}
						
						String strWhere = "";
						String allType = this.getRequestValue("allType");
						if("".equals(allType)) allType = "1";
						if("1".equals(allType)){
							strWhere = " ABS(dateRemain) ";
						}else if("2".equals(allType)){
							strWhere = " ABS(debit) ";
						}else if("3".equals(allType)){
							strWhere = " ABS(credit) ";
						}
						
						String tempAllOptC = "";
						String where = "";
						if("1".equals(allOpt)){
							where = " and substring(vchdate,9) not in ('00','97','98') " +
							" and " + strWhere + " >" + moneyChao ;

							tempAllOptC = " 根据 每条 的凭证来过滤 ";
							//==小杨的轨迹
							String tempWhere = "仅显示余额 > "+moneyChao+" 限额明细,"+tempAllOptC;
							this.setRepalcedSQL("${tempWhere}");
							this.setOrAddRequestValue("tempWhere",tempWhere);
							//===
						}else if("2".equals(allOpt)){
							where = " and substring(vchdate,9) not in ('00','97','98') " +
							" and " + strWhere + " >" + moneyChao +
							" and id in (" +
							" 	select max(id) from `"+TabName+"` " +
							"	where 1=1 and substring(vchdate,9) not in ('00','97','98')" +
							"	group by vchdate" +
							" ) ";
							tempAllOptC = " 根据 每天 的凭证来过滤 ";
							//==小杨的轨迹
							String tempWhere = "仅显示余额 > "+moneyChao+" 限额明细,"+tempAllOptC;
							this.setRepalcedSQL("${tempWhere}");
							this.setOrAddRequestValue("tempWhere",tempWhere);
							//===
						}else if("3".equals(allOpt)){
							where = " and substring(vchdate,9) not in ('00','97','98') " +
							" and " + strWhere + " >" + moneyChao +
							" and id in (" +
							" 	select max(id) from `"+TabName+"` " +
							"	where 1=1 and substring(vchdate,9) not in ('00','97','98')" +
							"	group by vchmonth" +
							" ) ";
							tempAllOptC = " 根据 每月 的凭证来过滤 ";
							//==小杨的轨迹
							String tempWhere = "仅显示余额 > "+moneyChao+" 限额明细,"+tempAllOptC;
							this.setRepalcedSQL("${tempWhere}");
							this.setOrAddRequestValue("tempWhere",tempWhere);
							//===
						}



						this.setOrAddRequestValue("where",where);
					}
				};

				String html = CHF.showNull(request.getParameter("html"));	//用于标志要不要用setTdtoValue　1为要　
				if("1".equals(html)){
					pp.setTdtoValue("<a href='St://${value}'>${tdvalue}</a>");	
				}
				
				pp.setTableID("SubjectEntry"+subjectEntry.getRandom());
				pp.setCustomerId(T1);
				pp.setPageSize_CH(500);
				pp.setWhichFieldIsValue(3);

//				String sql = "select id,vchmonth vchmonth1,autoid,voucherid,p1,p2,p3," +
//						"IF(substring(vchdate,9)='00',vchyear,'') vchyear,IF(substring(vchdate,9)='00',vchmonth,'') vchmonth," +
//						"IF(substring(vchdate,9)='00' or substring(vchdate,9)='97' or substring(vchdate,9)='98','',vchdate) vchdate," +
//						"typeid,oldvoucherid,summary,if(substring(vchdate,9)='00','',debit)  debit,if(substring(vchdate,9)='00','',credit) credit,IF(dateremain>0,'借',if(dateremain<0,'贷','平')) rec,ABS(dateremain) dateremain from `"+TabName+"`";
 
				String sql = "select id,a.vchmonth vchmonth1,a.autoid,a.voucherid," +
						"case substring(a.vchdate,9) when '00' then 0 when '97' then 0 when '98' then 0 else y.vchid end p1," +
						"case substring(a.vchdate,9) when '00' then 0 when '97' then 0 when '98' then 0 else z.vchid end p2," +
						"case substring(a.vchdate,9) when '00' then 0 when '97' then 0 when '98' then 0 else t.vchid end p3," +
						"case substring(a.vchdate,9) when '00' then 0 when '97' then 0 when '98' then 0 else t1.attachCode end p5," +
						"IF(substring(a.vchdate,9)='00',vchyear,'') vchyear," +
						"IF(substring(a.vchdate,9)='00',vchmonth,'') vchmonth," +
						"case substring(a.vchdate,9) when '00' then '' when '97' then '' when '98' then '' else a.vchdate end vchdate," +
						"a.typeid,a.oldvoucherid,replace(substring(a.subjects,2),',','<br>') as subjects,a.summary," +
						"if(substring(a.vchdate,9)='00','',debit)  debit,if(substring(a.vchdate,9)='00','',credit) credit,IF(dateremain>0,'借',if(dateremain<0,'贷','平')) rec,ABS(dateremain) dateremain,'"+acc+"' as acc,subjectnames " +
						"from (" +
						"	select a.*,group_concat(distinct "+subjectnameSql+") subjectnames   from (" +
						"		select *,IF(credit>0,1,-1) AS opt from `"+TabName+"` where 1=1 ${where} order by id ${LIMIT}" +
						"	) a left join c_subjectentry b " +
						"	on 1=1 " +
						"	AND SUBSTRING(b.VchDate,1,4)*12 + SUBSTRING(b.VchDate,6,2) >= " + (Integer.parseInt(BeginYear) * 12 + Integer.parseInt(BeginDate)) +  
						"	AND SUBSTRING(b.VchDate,1,4)*12 + SUBSTRING(b.VchDate,6,2) <= " + (Integer.parseInt(EndYear) * 12 + Integer.parseInt(EndDate)) + 
						"	AND a.voucherid=b.voucherid " +
						"	AND a.opt = IF(b.Dirction*b.occurvalue<0,-1,1) " +
						"	AND a.autoid <>b.autoid " + sqlwhere + 
						"	group by a.id  " +
						") a " +
						"left join (select distinct vchid from z_voucherspotcheck  where projectid='" +projectid + "' and createor='" + user +"'   ) y on a.voucherid=y.vchid  " +
						"left join (select distinct vchid from z_question  where projectid='" +projectid + "' and createor='" + user +"'   )  z on a.voucherid=z.vchid  " +
						"left join (select distinct vchid from z_taxcheck  where projectid='" +projectid + "' and createor='" + user +"'   )  t on a.autoid=t.vchid  " +
						"left join (select distinct attachCode from z_attach  where projectid='" +projectid + "' ) t1 on a.autoid=t1.attachCode  " +
						"where 1=1 AND NOT (SUBSTRING(a.vchdate,9) = '00' AND vchmonth <> '01' AND id <> 1) ";

				pp.setOrderBy_CH("id");
				pp.setDirection("asc");
				pp.setCancelOrderby(true);
//				pp.setCancelPage(true);

				pp.setLimitByOwnEnable(true);
				pp.setCountsql("select count(*) as datagrid_count from `" + TabName + "` where 1=1 ${where}");
				pp.setEnableCountTr(false);
				
				pp.setFixedHeader(true) ;

				pp.setTrActionProperty(true);
				pp.setTrAction("  AccPackageID='"+acc+"' vchdate='${vchdate}' voucherid='${voucherid}' style='cursor:hand;' onDBLclick='goSort();'");

				String setColumnWidth = "";
				if(acc.equals(userSession.getCurAccPackageId()) && userSession.getCurProjectId()!=null && !"1".equals(html)){
					pp.addColumn("抽","p1",FormatType.showTakeOut );
//					pp.addColumn("附","p5",FormatType.showAttach );
					setColumnWidth = "4,";
					ProjectService projectService = new ProjectService(conn);
					if("税务审计".equals(projectService.getProjectById(projectid).getAuditPara()))
						pp.addColumn("税", "p3", "showTaskTax");
				}
				String printSqlColumn = "",printColumn = "";
				printSqlColumn = "vchdate,typeid,oldvoucherid,";
				printColumn = "凭证日期`字`号`摘要`";
				
				setColumnWidth += "8,8,5,25";
//				pp.addColumn("年", "vchyear","showCenter");
//				pp.addColumn("月", "vchmonth","showCenter");
				pp.addColumn("凭证日期", "vchdate","showCenter");
				pp.addColumn("字", "typeid","showCenter");
				pp.addColumn("号", "oldvoucherid","showCenter");
				pp.addColumn("摘要", "summary");
				
				if(toSubjects.equals("1")){
					printSqlColumn += "subjectnames,";
					printColumn +="对方科目`";
					pp.addColumn("对方科目", "subjectnames");
					setColumnWidth += ",15";
				}
				setColumnWidth += ",15,15,5,15";
				printSqlColumn += "summary,debit,credit,rec,dateremain";
				printColumn +="借方发生额`贷方发生额`方向`余额";
				
				pp.setColumnWidth(setColumnWidth);
				
//				pp.addColumn("摘要", "summary",null,null,"<td style=\"word-break: keep-all;\">${value}</td>");
				pp.addColumn("借方发生额", "debit","showMoney");
				pp.addColumn("贷方发生额", "credit","showMoney");
				pp.addColumn("方向", "rec","showCenter");
				pp.addColumn("余额", "dateremain","showMoney");

				pp.addSqlWhere("where", "${where}");
				pp.addSqlWhere("tempWhere", "${tempWhere}");

				pp.addInputValue("TabName");
				pp.addInputValue("allOpt");
				pp.addInputValue("allType");
				pp.addInputValue("moneyChao");

				String TName = "";
				if(!"".equals(T1)) TName = new ASTextKey(conn).TextCustomerName(T1);
				
				pp.setPrintEnable(true);
				pp.setPrintTitle(TName + "      科目明细账查询");
				pp.setPrintCharColumn("1`2`3`4") ;
				
				pp.setPrintPoms("科目名称："+subjectEntry.getSubFullName(acc,SubjectID) +"    期间："+BeginYear+"年"+BeginDate+"月－"+EndYear+"年"+EndDate+"月     贷币单位："+new ASTextKey(conn).getACurrRate(acc));
				
//				pp.setPrintSqlColumn(printSqlColumn);
//				pp.setPrintColumn(printColumn);
				pp.setPrintTableHead(null);
				
				
				pp.setSQL(sql);
				request.getSession().setAttribute(DataGrid.sessionPre+pp.getTableID(),pp);

				mapResult.put("html", html);
				
				mapResult.put("TabName", TabName);

				mapResult.put("DataGrid", pp.getTableID());
			}

		} catch (Exception e) {
			Debug.print(Debug.iError, "科目明细账查询失败 非叶子与无外币和无数量的叶子！", e);
            throw e;
		} finally {
			DbUtil.close(conn);
		}

		return new ModelAndView(_strBList,mapResult);
	}

	public ModelAndView btwolist(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		Connection conn=null;
		HashMap mapResult = new HashMap();
		try {
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
			ASFuntion CHF=new ASFuntion();

			String user=CHF.showNull(userSession.getUserId());
			String projectid=CHF.showNull(userSession.getCurProjectId());
			if("".equals(projectid)) projectid = "-1";

			String T1 = CHF.showNull(request.getParameter("T1"));
			String TabName = CHF.showNull(request.getParameter("TabName"));

			String acc = CHF.showNull(request.getParameter("AccPackageID"));
			if("".equals(acc)){
				acc = CHF.showNull(userSession.getCurChoiceAccPackageId());
				if("".equals(acc)){
					acc = CHF.showNull(userSession.getCurAccPackageId());
				}
			}

			String BeginYear = CHF.showNull(request.getParameter("BeginYear"));
			if("".equals(BeginYear)){
				BeginYear = CHF.showNull(userSession.getCurChoiceBeginYear());
				if("".equals(BeginYear)){
					BeginYear = CHF.showNull(userSession.getCurProjectBeginYear());
				}
			}
			String EndYear = CHF.showNull(request.getParameter("EndYear"));
			if("".equals(EndYear)){
				EndYear = CHF.showNull(userSession.getCurChoiceEndYear());
				if("".equals(EndYear)){
					EndYear = CHF.showNull(userSession.getCurProjectEndYear());
				}
			}
			String BeginDate = CHF.showNull(request.getParameter("BeginDate"));
			if("".equals(BeginDate)){
				BeginDate = CHF.showNull(userSession.getCurChoiceBeginMonth());
				if("".equals(BeginDate)){
					BeginDate = CHF.showNull(userSession.getCurProjectBeginMonth());
				}
			}
			String EndDate = CHF.showNull(request.getParameter("EndDate"));
			if("".equals(EndDate)){
				EndDate = CHF.showNull(userSession.getCurChoiceEndMonth());
				if("".equals(EndDate)){
					EndDate = CHF.showNull(userSession.getCurProjectEndMonth());
				}
			}

			String SubjectID = CHF.showNull(request.getParameter("SubjectID"));

			String toSubjects = CHF.showNull(request.getParameter("toSubjects"));//显示对方科目
			String sqlwhere = "";
			if(!toSubjects.equals("1")){
				sqlwhere = " and  1=2 ";
			}
			String toLevel1 = CHF.showNull(request.getParameter("toLevel1")); //对方科目显示的级数
			String subjectnameSql = "";
			if(toLevel1.equals("1")){
				subjectnameSql = "if(locate('/',b.subjectfullname1)=0,b.subjectfullname1,substr(b.subjectfullname1,1,locate('/',b.subjectfullname1)-1))";
			}else{
//				subjectnameSql =  "if(b.subjectname1!=b.subjectfullname1 && concat(if(locate('/',b.subjectfullname1)=0,b.subjectfullname1,substr(b.subjectfullname1,1,locate('/',b.subjectfullname1)-1)),'/',b.subjectname1)!=b.subjectfullname1 \n"
//					 +" ,concat(if(locate('/',b.subjectfullname1)=0,b.subjectfullname1,substr(b.subjectfullname1,1,locate('/',b.subjectfullname1)-1)),'/../',b.subjectname1) \n"
//					 +" ,b.subjectfullname1) ";
				subjectnameSql =  " b.subjectfullname1 ";
			}
			conn = new DBConnect().getConnect(T1);
			SubjectEntry subjectEntry = new SubjectEntry(conn);
			Map map = subjectEntry.SubjectCurrency(acc,SubjectID);
			if(!subjectEntry.ExistsTable(TabName)){
				subjectEntry.CreateTable(TabName,map);
				subjectEntry.DataToTable(map,TabName,user,projectid,T1,SubjectID,BeginYear,BeginDate,EndYear,EndDate);
			}


			String sTable = "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tr><td>科目名称：<font color=\"blue\">"+subjectEntry.getSubFullName(acc,SubjectID)+"</font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;期间：<font color=\"blue\">"+BeginYear+"年"+BeginDate+"月－"+EndYear+"年"+EndDate+"月</font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;本位币名称：<font color=\"blue\">"+new ASTextKey(conn).getACurrRate(acc)+"</font></td></tr></table>";

			mapResult.put("SubjectID", SubjectID);
			mapResult.put("TabName", TabName);
			mapResult.put("T1", T1);
			mapResult.put("sTable", sTable);


			/**
			 * DataGrid
			 */
			if(!"".equals(SubjectID)){
				DataGridProperty pp = new DataGridProperty(){
					public void onSearch (
		    				javax.servlet.http.HttpSession session,
		    				javax.servlet.http.HttpServletRequest request,
		    				javax.servlet.http.HttpServletResponse response) throws Exception{

						String TabName = this.getRequestValue("TabName");

						String allOpt = this.getRequestValue("allOpt");
						String moneyChao = this.getRequestValue("moneyChao");
						
						if(moneyChao == null || "".equals(moneyChao.trim())){
							allOpt = "0";
						}
						
						String strWhere = "";
						String allType = this.getRequestValue("allType");
						if("".equals(allType)) allType = "1";
						if("1".equals(allType)){
							strWhere = " ABS(dateRemain) ";
						}else if("2".equals(allType)){
							strWhere = " ABS(debit) ";
						}else if("3".equals(allType)){
							strWhere = " ABS(credit) ";
						}
						
						String where = "";
						if("1".equals(allOpt)){
							where = " and substring(vchdate,9) not in ('00','97','98') " +
							" and " + strWhere + " >" + moneyChao ;
						}else if("2".equals(allOpt)){
							where = " and substring(vchdate,9) not in ('00','97','98') " +
							" and " + strWhere + " >" + moneyChao +
							" and id in (" +
							" 	select max(id) from `"+TabName+"` " +
							"	where 1=1 and substring(vchdate,9) not in ('00','97','98')" +
							"	group by vchdate" +
							" ) ";
						}else if("3".equals(allOpt)){
							where = " and substring(vchdate,9) not in ('00','97','98') " +
							" and " + strWhere + " >" + moneyChao +
							" and id in (" +
							" 	select max(id) from `"+TabName+"` " +
							"	where 1=1 and substring(vchdate,9) not in ('00','97','98')" +
							"	group by vchmonth" +
							" ) ";
						}


						this.setOrAddRequestValue("where",where);
					}
				};
				
				String html = CHF.showNull(request.getParameter("html"));	//用于标志要不要用setTdtoValue　1为要　
				if("1".equals(html)){
					pp.setTdtoValue("<a href='St://${value}'>${tdvalue}</a>");	
				}
				
				pp.setTableID("SubjectEntry"+subjectEntry.getRandom());
				pp.setCustomerId(T1);
				pp.setPageSize_CH(100);
				pp.setWhichFieldIsValue(3);
				pp.setOrderBy_CH("id");
				pp.setDirection("asc");
				pp.setCancelOrderby(true);

				pp.setTrActionProperty(true);
				pp.setTrAction("  AccPackageID='"+acc+"' vchdate='${vchdate}' voucherid='${voucherid}' style='cursor:hand;' onDBLclick='goSort();'");

				String TableHead = "",TableHead1 = "";
				String sql = "";
				
				TableHead = "凭证日期,字,号,";

				sql = "select id,a.vchmonth vchmonth1,autoid,a.voucherid," +
					"case substring(a.vchdate,9) when '00' then 0 when '97' then 0 when '98' then 0 else y.vchid end p1," +
					"case substring(a.vchdate,9) when '00' then 0 when '97' then 0 when '98' then 0 else z.vchid end p2," +
					"case substring(a.vchdate,9) when '00' then 0 when '97' then 0 when '98' then 0 else t.vchid end p3," +
					"IF(substring(a.vchdate,9)='00',vchyear,'') vchyear,IF(substring(a.vchdate,9)='00',vchmonth,'') vchmonth," +
					"case substring(a.vchdate,9) when '00' then '' when '97' then '' when '98' then '' else a.vchdate end vchdate," +
					"a.typeid,a.oldvoucherid,replace(substring(a.subjects,2),',','<br>') as subjects,a.summary, ";

				String setColumnWidth = "";
				if(acc.equals(userSession.getCurAccPackageId()) && userSession.getCurProjectId()!=null && !"1".equals(html)){
//					TableHead = "抽,年,月,凭证日期,字,号,";
					TableHead1 = "抽,";
					pp.addColumn("抽","p1",FormatType.showTakeOut );
//					pp.addColumn("疑","p2",FormatType.showDoubt );
					setColumnWidth = "4,";
					ProjectService projectService = new ProjectService(conn);
					if("税务审计".equals(projectService.getProjectById(projectid).getAuditPara())){
						pp.addColumn("税", "p3", "showTaskTax");
//						TableHead = "抽,疑,税,年,月,凭证日期,字,号,";
						TableHead1 = "抽,税,";
					}
				}

				
//				pp.addColumn("年", "vchyear","showCenter");
//				pp.addColumn("月", "vchmonth","showCenter");
				pp.addColumn("凭证日期", "vchdate","showCenter");
				pp.addColumn("字", "typeid","showCenter");
				pp.addColumn("号", "oldvoucherid","showCenter");
				pp.addColumn("摘要", "summary");
				TableHead += "摘要,";
				setColumnWidth += "8,8,5,25";
				if("1".equals(toSubjects)){
					TableHead += "对方科目,";
					pp.addColumn("对方科目", "subjectnames");
					setColumnWidth += ",15";
				}
				
				pp.setColumnWidth(setColumnWidth);
				

				Set coll = map.keySet();
				int ii =1;

				String sqlHead1 = "";
				String sqlHead2 = "";
				String sqlHead3 = "";

				for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
					String key = (String) iter.next();
					String value = (String) map.get(key);
					sql += "if(substring(a.vchdate,9)='00','',debitrate"+ii+") debitrate"+ii+",if(substring(a.vchdate,9)='00','',debit"+key+") debit"+key+",";
					sqlHead1 += "汇率,"+value+",";

					pp.addColumn("汇率", "debitrate"+ii,"showMoney");
					pp.addColumn(value, "debit"+key,"showMoney");
					ii ++;

				}
				sql += "if(substring(a.vchdate,9)='00','',debit) debit,";
				sqlHead1 += "借方本位币";
				TableHead += "借方{"+sqlHead1+"},";

				pp.addColumn("借方本位币", "debit","showMoney");

				ii=1;
				for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
					String key = (String) iter.next();
					String value = (String) map.get(key);
					sql += "if(substring(a.vchdate,9)='00','',creditrate"+ii+") creditrate"+ii+",if(substring(a.vchdate,9)='00','',credit"+key+") credit"+key+",";
					sqlHead2 += "汇率,"+value+",";

					pp.addColumn("汇率", "creditrate"+ii,"showMoney");
					pp.addColumn(value, "credit"+key,"showMoney");
					ii ++;

				}
				sql += "if(substring(a.vchdate,9)='00','',credit) credit,IF(dateremain>0,'借',if(dateremain<0,'贷','平')) rec,";
				sqlHead2 += "贷方本位币";
				TableHead += "贷方{"+sqlHead2+"},方向,";

				pp.addColumn("贷方本位币", "credit","showMoney");
				pp.addColumn("方向", "rec","showCenter");

				ii=1;
				for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
					String key = (String) iter.next();
					String value = (String) map.get(key);
//					sql += "abs(dRemain"+key+") dRemain"+key+",";
					sql += "if(dateremain<0,(-1)*dRemain"+key+",dRemain"+key+") as dRemain"+key+",";
					sqlHead3 += value+",";

					pp.addColumn(value, "dRemain"+key,"showMoney");
					ii ++;
				}
				sql +="ABS(dateRemain) dateRemain,subjectnames  " +
				"from (" +
				"	select a.*,group_concat(distinct "+subjectnameSql+") subjectnames   from (" +
				"		select *,IF(credit>0,1,-1) AS opt from `"+TabName+"` where 1=1 ${where} order by id ${LIMIT}" +
				"	) a left join c_subjectentry b " +
				"	on 1=1 " +
				"	AND SUBSTRING(b.VchDate,1,4)*12 + SUBSTRING(b.VchDate,6,2) >= " + (Integer.parseInt(BeginYear) * 12 + Integer.parseInt(BeginDate)) +  
				"	AND SUBSTRING(b.VchDate,1,4)*12 + SUBSTRING(b.VchDate,6,2) <= " + (Integer.parseInt(EndYear) * 12 + Integer.parseInt(EndDate)) + 
				"	AND a.voucherid=b.voucherid " +
				"	AND a.opt = IF(b.Dirction*b.occurvalue<0,-1,1) " +
				"	AND a.autoid <>b.autoid " + sqlwhere + 
				"	group by a.id  " +
				") a " +

				"left join (select distinct vchid from z_voucherspotcheck  where projectid='" +projectid + "' and createor='" + user +"'   ) y on a.voucherid=y.vchid  " +
				"left join (select distinct vchid from z_question  where projectid='" +projectid + "' and createor='" + user +"'   )  z on a.voucherid=z.vchid  " +
				"left join (select distinct vchid from z_taxcheck  where projectid='" +projectid + "' and createor='" + user +"'   )  t on a.autoid=t.vchid  " +
				"where 1=1 AND NOT (SUBSTRING(a.vchdate,9) = '00' AND vchmonth <> '01' AND id <> 1) ";

				sqlHead3 +="本位币余额";
				TableHead +="余额{"+sqlHead3+"}";

				pp.addColumn("本位币余额", "dateRemain","showMoney");


				pp.setLimitByOwnEnable(true);
				pp.setCountsql("select count(*) as datagrid_count from `" + TabName + "` where 1=1 ${where} ");
				pp.setEnableCountTr(false);

				pp.setTableHead(TableHead1 + TableHead);

				pp.addSqlWhere("where", "${where}");

				pp.addInputValue("TabName");
				pp.addInputValue("allOpt");
				pp.addInputValue("allType");
				pp.addInputValue("moneyChao");
				
				pp.setFixedHeader(true) ;
 
				String TName = "";
				if(!"".equals(T1)) TName = new ASTextKey(conn).TextCustomerName(T1);
				
				pp.setPrintEnable(true);
				pp.setPrintTitle(TName + "      科目明细账查询");
				pp.setPrintCharColumn("1`2`3`4") ;
				
				pp.setPrintPoms("科目名称："+subjectEntry.getSubFullName(acc,SubjectID) +"    期间："+BeginYear+"年"+BeginDate+"月－"+EndYear+"年"+EndDate+"月     贷币单位："+new ASTextKey(conn).getACurrRate(acc));

				
//				pp.setPrintSqlColumn(printSqlColumn);
//				pp.setPrintColumn(printColumn);
				pp.setPrintTableHead(TableHead);
				
				pp.setSQL(sql);
				request.getSession().setAttribute(DataGrid.sessionPre+pp.getTableID(),pp);
				
				mapResult.put("html", html);
				mapResult.put("TabName", TabName);
				mapResult.put("DataGrid", pp.getTableID());
			}

		} catch (Exception e) {
			Debug.print(Debug.iError, "科目明细账查询失败 1:叶子有外币无数量！", e);
            throw e;
		} finally {
			DbUtil.close(conn);
		}
		return new ModelAndView(_strBList,mapResult);
	}

	public ModelAndView bthreelist(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		Connection conn=null;
		HashMap mapResult = new HashMap();
		try {
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
			ASFuntion CHF=new ASFuntion();

			String user=CHF.showNull(userSession.getUserId());
			String projectid=CHF.showNull(userSession.getCurProjectId());
			if("".equals(projectid)) projectid = "-1";

			String T1 = CHF.showNull(request.getParameter("T1"));
			String TabName = CHF.showNull(request.getParameter("TabName"));

			String acc = CHF.showNull(request.getParameter("AccPackageID"));
			if("".equals(acc)){
				acc = CHF.showNull(userSession.getCurChoiceAccPackageId());
				if("".equals(acc)){
					acc = CHF.showNull(userSession.getCurAccPackageId());
				}
			}

			String BeginYear = CHF.showNull(request.getParameter("BeginYear"));
			if("".equals(BeginYear)){
				BeginYear = CHF.showNull(userSession.getCurChoiceBeginYear());
				if("".equals(BeginYear)){
					BeginYear = CHF.showNull(userSession.getCurProjectBeginYear());
				}
			}
			String EndYear = CHF.showNull(request.getParameter("EndYear"));
			if("".equals(EndYear)){
				EndYear = CHF.showNull(userSession.getCurChoiceEndYear());
				if("".equals(EndYear)){
					EndYear = CHF.showNull(userSession.getCurProjectEndYear());
				}
			}
			String BeginDate = CHF.showNull(request.getParameter("BeginDate"));
			if("".equals(BeginDate)){
				BeginDate = CHF.showNull(userSession.getCurChoiceBeginMonth());
				if("".equals(BeginDate)){
					BeginDate = CHF.showNull(userSession.getCurProjectBeginMonth());
				}
			}
			String EndDate = CHF.showNull(request.getParameter("EndDate"));
			if("".equals(EndDate)){
				EndDate = CHF.showNull(userSession.getCurChoiceEndMonth());
				if("".equals(EndDate)){
					EndDate = CHF.showNull(userSession.getCurProjectEndMonth());
				}
			}


			String toSubjects = CHF.showNull(request.getParameter("toSubjects"));//显示对方科目
			String sqlwhere = "";
			if(!toSubjects.equals("1")){
				sqlwhere = " and  1=2 ";
			}
			String toLevel1 = CHF.showNull(request.getParameter("toLevel1")); //对方科目显示的级数
			String subjectnameSql = "";
			if(toLevel1.equals("1")){
				subjectnameSql = "if(locate('/',b.subjectfullname1)=0,b.subjectfullname1,substr(b.subjectfullname1,1,locate('/',b.subjectfullname1)-1))";
			}else{
//				subjectnameSql =  "if(b.subjectname1!=b.subjectfullname1 && concat(if(locate('/',b.subjectfullname1)=0,b.subjectfullname1,substr(b.subjectfullname1,1,locate('/',b.subjectfullname1)-1)),'/',b.subjectname1)!=b.subjectfullname1 \n"
//					 +" ,concat(if(locate('/',b.subjectfullname1)=0,b.subjectfullname1,substr(b.subjectfullname1,1,locate('/',b.subjectfullname1)-1)),'/../',b.subjectname1) \n"
//					 +" ,b.subjectfullname1) ";
				subjectnameSql =  " b.subjectfullname1 ";
			}

			String SubjectID = CHF.showNull(request.getParameter("SubjectID"));
			conn = new DBConnect().getConnect(T1);
			SubjectEntry subjectEntry = new SubjectEntry(conn);

			Map map = subjectEntry.SubjectUnitName(acc,SubjectID);
			if(!subjectEntry.ExistsTable(TabName)){
				subjectEntry.CreateUnitTable(TabName,map);
				subjectEntry.DataToUnitTable(map,TabName,user,projectid,T1,SubjectID,BeginYear,BeginDate,EndYear,EndDate);
			}

			String sTable = "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tr><td>科目名称：<font color=\"blue\">"+subjectEntry.getSubFullName(acc,SubjectID)+"</font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;期间：<font color=\"blue\">"+BeginYear+"年"+BeginDate+"月－"+EndYear+"年"+EndDate+"月</font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;本位币名称：<font color=\"blue\">"+new ASTextKey(conn).getACurrRate(acc)+"</font></td></tr></table>";

			mapResult.put("SubjectID", SubjectID);
			mapResult.put("TabName", TabName);
			mapResult.put("T1", T1);
			mapResult.put("sTable", sTable);

			/**
			 * DataGrid
			 */
			if(!"".equals(SubjectID)){
				DataGridProperty pp = new DataGridProperty(){
					public void onSearch (
		    				javax.servlet.http.HttpSession session,
		    				javax.servlet.http.HttpServletRequest request,
		    				javax.servlet.http.HttpServletResponse response) throws Exception{

						String TabName = this.getRequestValue("TabName");

						String allOpt = this.getRequestValue("allOpt");
						String moneyChao = this.getRequestValue("moneyChao");
						if(moneyChao == null || "".equals(moneyChao.trim())){
							allOpt = "0";
						}
						
						String strWhere = "";
						String allType = this.getRequestValue("allType");
						if("".equals(allType)) allType = "1";
						if("1".equals(allType)){
							strWhere = " ABS(dateRemain) ";
						}else if("2".equals(allType)){
							strWhere = " ABS(debit) ";
						}else if("3".equals(allType)){
							strWhere = " ABS(credit) ";
						}
						
						String where = "";
						if("1".equals(allOpt)){
							where = " and substring(vchdate,9) not in ('00','97','98') " +
							" and " + strWhere + " >" + moneyChao ;
						}else if("2".equals(allOpt)){
							where = " and substring(vchdate,9) not in ('00','97','98') " +
							" and " + strWhere + " >" + moneyChao +
							" and id in (" +
							" 	select max(id) from `"+TabName+"` " +
							"	where 1=1 and substring(vchdate,9) not in ('00','97','98')" +
							"	group by vchdate" +
							" ) ";
						}else if("3".equals(allOpt)){
							where = " and substring(vchdate,9) not in ('00','97','98') " +
							" and " + strWhere + " >" + moneyChao +
							" and id in (" +
							" 	select max(id) from `"+TabName+"` " +
							"	where 1=1 and substring(vchdate,9) not in ('00','97','98')" +
							"	group by vchmonth" +
							" ) ";
						}

						this.setOrAddRequestValue("where",where);
					}
				};
				
				String html = CHF.showNull(request.getParameter("html"));	//用于标志要不要用setTdtoValue　1为要　
				if("1".equals(html)){
					pp.setTdtoValue("<a href='St://${value}'>${tdvalue}</a>");	
				}
				
				pp.setTableID("SubjectEntry"+subjectEntry.getRandom());
				pp.setCustomerId(T1);
				pp.setPageSize_CH(100);
				pp.setWhichFieldIsValue(3);
				pp.setOrderBy_CH("id");
				pp.setDirection("asc");
				pp.setCancelOrderby(true);
				pp.setFixedHeader(true) ;
				pp.setTrActionProperty(true);
				pp.setTrAction("  AccPackageID='"+acc+"' vchdate='${vchdate}' voucherid='${voucherid}' style='cursor:hand;' onDBLclick='goSort();'");

				String TableHead = "",TableHead1 = "";
				String sql = "";

				TableHead = "凭证日期,字,号,";

				sql = "select id,a.vchmonth vchmonth1,autoid,a.voucherid," +
					"case substring(a.vchdate,9) when '00' then 0 when '97' then 0 when '98' then 0 else y.vchid end p1," +
					"case substring(a.vchdate,9) when '00' then 0 when '97' then 0 when '98' then 0 else z.vchid end p2," +
					"case substring(a.vchdate,9) when '00' then 0 when '97' then 0 when '98' then 0 else t.vchid end p3," +
					"IF(substring(a.vchdate,9)='00',vchyear,'') vchyear,IF(substring(a.vchdate,9)='00',vchmonth,'') vchmonth," +
					"case substring(a.vchdate,9) when '00' then '' when '97' then '' when '98' then '' else a.vchdate end vchdate," +
					"a.typeid,a.oldvoucherid,replace(substring(a.subjects,2),',','<br>') as subjects,a.summary, ";
				String setColumnWidth = "";
				if(acc.equals(userSession.getCurAccPackageId()) && userSession.getCurProjectId()!=null && !"1".equals(html)){
//					TableHead = "抽,年,月,凭证日期,字,号,";
					TableHead1 = "抽,";
					pp.addColumn("抽","p1",FormatType.showTakeOut );
//					pp.addColumn("疑","p2",FormatType.showDoubt );
					setColumnWidth = "4,";
					ProjectService projectService = new ProjectService(conn);
					if("税务审计".equals(projectService.getProjectById(projectid).getAuditPara())){
						pp.addColumn("税", "p3", "showTaskTax");
//						TableHead = "抽,疑,税,年,月,凭证日期,字,号,";
						TableHead1 = "抽,税,";
					}
				}


//				pp.addColumn("年", "vchyear","showCenter");
//				pp.addColumn("月", "vchmonth","showCenter");
				pp.addColumn("凭证日期", "vchdate","showCenter");
				pp.addColumn("字", "typeid","showCenter");
				pp.addColumn("号", "oldvoucherid","showCenter");
				pp.addColumn("摘要", "summary");
				TableHead += "摘要,";
				setColumnWidth += "8,8,5,25";
				if("1".equals(toSubjects)){
					TableHead += "对方科目,";
					pp.addColumn("对方科目", "subjectnames");
					setColumnWidth += ",15";
				}
				pp.setColumnWidth(setColumnWidth);
				

				Set coll = map.keySet();
				int ii =1;

				String sqlHead1 = "";
				String sqlHead2 = "";
				String sqlHead3 = "";

				for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
					String key = (String) iter.next();
					String value = (String) map.get(key);
					sql += "if(substring(a.vchdate,9)='00','',debitPrice"+ii+") debitPrice"+ii+",if(substring(a.vchdate,9)='00','',debit"+key+") debit"+key+",";
					sqlHead1 += "单价,"+value+",";

					pp.addColumn("单价", "debitPrice"+ii,"showMoney");
					pp.addColumn(value, "debit"+key,"showMoney");
					ii ++;
				}
				sql += "if(substring(a.vchdate,9)='00','',debit) debit,";
				sqlHead1 += "借方本位币";
				TableHead += "借方{"+sqlHead1+"},";

				pp.addColumn("借方本位币", "debit","showMoney");

				ii=1;
				for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
					String key = (String) iter.next();
					String value = (String) map.get(key);
					sql += "if(substring(a.vchdate,9)='00','',creditPrice"+ii+") creditPrice"+ii+",if(substring(a.vchdate,9)='00','',credit"+key+") credit"+key+",";
					sqlHead2 += "单价,"+value+",";

					pp.addColumn("单价", "creditPrice"+ii,"showMoney");
					pp.addColumn(value, "credit"+key,"showMoney");
					ii ++;
				}
				sql += "if(substring(a.vchdate,9)='00','',credit) credit,IF(dateremain>0,'借',if(dateremain<0,'贷','平')) rec,";
				sqlHead2 += "贷方本位币";
				TableHead += "贷方{"+sqlHead2+"},方向,";

				pp.addColumn("贷方本位币", "credit","showMoney");
				pp.addColumn("方向", "rec","showCenter");

				ii=1;
				for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
					String key = (String) iter.next();
					String value = (String) map.get(key);
//					sql += "abs(dRemain"+key+") dRemain"+key+",";
					sql += "if(dateremain<0,(-1)*dRemain"+key+",dRemain"+key+") as dRemain"+key+",";
					sqlHead3 += value+",";

					pp.addColumn(value, "dRemain"+key,"showMoney");
					ii ++;
				}
				sql +="ABS(dateRemain) dateRemain,subjectnames  	" +
				"from (" +
				"	select a.*,group_concat(distinct "+subjectnameSql+") subjectnames   from (" +
				"		select *,IF(credit>0,1,-1) AS opt from `"+TabName+"` where 1=1 ${where} order by id ${LIMIT}" +
				"	) a left join c_subjectentry b " +
				"	on 1=1 " +
				"	AND SUBSTRING(b.VchDate,1,4)*12 + SUBSTRING(b.VchDate,6,2) >= " + (Integer.parseInt(BeginYear) * 12 + Integer.parseInt(BeginDate)) +  
				"	AND SUBSTRING(b.VchDate,1,4)*12 + SUBSTRING(b.VchDate,6,2) <= " + (Integer.parseInt(EndYear) * 12 + Integer.parseInt(EndDate)) + 
				"	AND a.voucherid=b.voucherid " +
				"	AND a.opt = IF(b.Dirction*b.occurvalue<0,-1,1) " +
				"	AND a.autoid <>b.autoid " + sqlwhere + 
				"	group by a.id  " +
				") a " +
				"left join (select distinct vchid from z_voucherspotcheck  where projectid='" +projectid + "' and createor='" + user +"'   ) y on a.voucherid=y.vchid  " +
				"left join (select distinct vchid from z_question  where projectid='" +projectid + "' and createor='" + user +"'   )  z on a.voucherid=z.vchid  " +
				"left join (select distinct vchid from z_taxcheck  where projectid='" +projectid + "' and createor='" + user +"'   )  t on a.autoid=t.vchid " +
				"where 1=1 AND NOT (SUBSTRING(a.vchdate,9) = '00' AND vchmonth <> '01' AND id <> 1) ";

				sqlHead3 +="本位币余额";
				TableHead +="余额{"+sqlHead3+"}";

				pp.addColumn("本位币余额", "dateRemain","showMoney");

				pp.setTableHead(TableHead1 + TableHead);

				pp.setLimitByOwnEnable(true);
				pp.setCountsql("select count(*) as datagrid_count from `" + TabName + "` where 1=1 ${where}");
				pp.setEnableCountTr(false);

				pp.addSqlWhere("where", "${where}");

				pp.addInputValue("TabName");
				pp.addInputValue("allOpt");
				pp.addInputValue("allType");
				pp.addInputValue("moneyChao");

				String TName = "";
				if(!"".equals(T1)) TName = new ASTextKey(conn).TextCustomerName(T1);
				
				pp.setPrintEnable(true);
				pp.setPrintTitle(TName + "      科目明细账查询");
				pp.setPrintCharColumn("1`2`3`4") ;
				
				pp.setPrintPoms("科目名称："+subjectEntry.getSubFullName(acc,SubjectID) +"    期间："+BeginYear+"年"+BeginDate+"月－"+EndYear+"年"+EndDate+"月     贷币单位："+new ASTextKey(conn).getACurrRate(acc));

				
				pp.setPrintTableHead(TableHead);
				
				pp.setSQL(sql);
				request.getSession().setAttribute(DataGrid.sessionPre+pp.getTableID(),pp);
				mapResult.put("html", html);
				mapResult.put("TabName", TabName);
				mapResult.put("DataGrid", pp.getTableID());

			}

		} catch (Exception e) {
			Debug.print(Debug.iError, "科目明细账查询失败 2:叶子无外币有数量！", e);
            throw e;
		} finally {
			DbUtil.close(conn);
		}
		return new ModelAndView(_strBList,mapResult);
	}

	
	//复币
	public ModelAndView bfubilist(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		Connection conn=null;
		HashMap mapResult = new HashMap();
		try {
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
			ASFuntion CHF=new ASFuntion();

			String user=CHF.showNull(userSession.getUserId());
			String projectid=CHF.showNull(userSession.getCurProjectId());
			if("".equals(projectid)) projectid = "-1";

			String T1 = CHF.showNull(request.getParameter("T1"));
			String TabName = CHF.showNull(request.getParameter("TabName"));


			

			String BeginYear = CHF.showNull(request.getParameter("BeginYear"));
			if("".equals(BeginYear)){
				BeginYear = CHF.showNull(userSession.getCurChoiceBeginYear());
				if("".equals(BeginYear)){
					BeginYear = CHF.showNull(userSession.getCurProjectBeginYear());
				}
			}
			String EndYear = CHF.showNull(request.getParameter("EndYear"));
			if("".equals(EndYear)){
				EndYear = CHF.showNull(userSession.getCurChoiceEndYear());
				if("".equals(EndYear)){
					EndYear = CHF.showNull(userSession.getCurProjectEndYear());
				}
			}
			String BeginDate = CHF.showNull(request.getParameter("BeginDate"));
			if("".equals(BeginDate)){
				BeginDate = CHF.showNull(userSession.getCurChoiceBeginMonth());
				if("".equals(BeginDate)){
					BeginDate = CHF.showNull(userSession.getCurProjectBeginMonth());
				}
			}
			String EndDate = CHF.showNull(request.getParameter("EndDate"));
			if("".equals(EndDate)){
				EndDate = CHF.showNull(userSession.getCurChoiceEndMonth());
				if("".equals(EndDate)){
					EndDate = CHF.showNull(userSession.getCurProjectEndMonth());
				}
			}

			String acc = T1+EndYear;
			userSession.setCurChoiceAccPackageId(acc);
			if("".equals(acc)){
				acc = CHF.showNull(userSession.getCurChoiceAccPackageId());
				if("".equals(acc)){
					acc = CHF.showNull(userSession.getCurAccPackageId());
				}
			}
			
			String SubjectID = CHF.showNull(request.getParameter("SubjectID"));

			String toSubjects = CHF.showNull(request.getParameter("toSubjects"));//显示对方科目
			String sqlwhere = "";
			if(!toSubjects.equals("1")){
				sqlwhere = " and  1=2 ";
			}
			
			String crt = CHF.showNull(request.getParameter("crt"));//复币
			
			String toLevel1 = CHF.showNull(request.getParameter("toLevel1")); //对方科目显示的级数
			String subjectnameSql = "";
			if(toLevel1.equals("1")){
				subjectnameSql = "if(locate('/',b.subjectfullname)=0,b.subjectfullname,substr(b.subjectfullname,1,locate('/',b.subjectfullname)-1))";
			}else{
				subjectnameSql =  "if(subjectname!=b.subjectfullname&&concat(if(locate('/',b.subjectfullname)=0,b.subjectfullname,substr(b.subjectfullname,1,locate('/',b.subjectfullname)-1)),'/',subjectname)!=b.subjectfullname \n"
					 +" ,concat(if(locate('/',b.subjectfullname)=0,b.subjectfullname,substr(b.subjectfullname,1,locate('/',b.subjectfullname)-1)),'/../',subjectname) \n"
					 +" ,b.subjectfullname) ";
			}
			conn = new DBConnect().getConnect(T1);
			SubjectEntry subjectEntry = new SubjectEntry(conn);
			Map map = subjectEntry.SubjectCurrency(acc,SubjectID);
			if(!subjectEntry.ExistsTable(TabName)){
				subjectEntry.CreateTable(TabName,map);
				subjectEntry.FubiDataToTable(map,TabName,user,projectid,T1,SubjectID,BeginYear,BeginDate,EndYear,EndDate,crt);
			}


			String sTable = "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tr><td>科目名称：<font color=\"blue\">"+subjectEntry.getSubFullName(acc,SubjectID)+"</font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;期间：<font color=\"blue\">"+BeginYear+"年"+BeginDate+"月－"+EndYear+"年"+EndDate+"月</font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;本位币名称：<font color=\"blue\">"+new ASTextKey(conn).getACurrRate(acc)+"</font></td></tr></table>";

			mapResult.put("SubjectID", SubjectID);
			mapResult.put("TabName", TabName);
			mapResult.put("T1", T1);
			mapResult.put("sTable", sTable);


			/**
			 * DataGrid
			 */
			if(!"".equals(SubjectID)){
				DataGridProperty pp = new DataGridProperty(){
					public void onSearch (
		    				javax.servlet.http.HttpSession session,
		    				javax.servlet.http.HttpServletRequest request,
		    				javax.servlet.http.HttpServletResponse response) throws Exception{

						String TabName = this.getRequestValue("TabName");

						String allOpt = this.getRequestValue("allOpt");
						String moneyChao = this.getRequestValue("moneyChao");
						if(moneyChao == null || "".equals(moneyChao.trim())){
							allOpt = "0";
						}
						
						String strWhere = "";
						String allType = this.getRequestValue("allType");
						if("".equals(allType)) allType = "1";
						if("1".equals(allType)){
							strWhere = " ABS(dateRemain) ";
						}else if("2".equals(allType)){
							strWhere = " ABS(debit) ";
						}else if("3".equals(allType)){
							strWhere = " ABS(credit) ";
						}
						
						String where = "";
						if("1".equals(allOpt)){
							where = " and substring(vchdate,9) not in ('00','97','98') " +
							" and " + strWhere + " >" + moneyChao ;
						}else if("2".equals(allOpt)){
							where = " and substring(vchdate,9) not in ('00','97','98') " +
							" and " + strWhere + " >" + moneyChao +
							" and id in (" +
							" 	select max(id) from `"+TabName+"` " +
							"	where 1=1 and substring(vchdate,9) not in ('00','97','98')" +
							"	group by vchdate" +
							" ) ";
						}else if("3".equals(allOpt)){
							where = " and substring(vchdate,9) not in ('00','97','98') " +
							" and " + strWhere + " >" + moneyChao +
							" and id in (" +
							" 	select max(id) from `"+TabName+"` " +
							"	where 1=1 and substring(vchdate,9) not in ('00','97','98')" +
							"	group by vchmonth" +
							" ) ";
						}


						this.setOrAddRequestValue("where",where);
					}
				};
				
				String html = CHF.showNull(request.getParameter("html"));	//用于标志要不要用setTdtoValue　1为要　
				if("1".equals(html)){
					pp.setTdtoValue("<a href='St://${value}'>${tdvalue}</a>");	
				}
				
				pp.setTableID("SubjectEntry"+subjectEntry.getRandom());
				pp.setCustomerId(T1);
				pp.setPageSize_CH(100);
				pp.setWhichFieldIsValue(3);
				pp.setOrderBy_CH("id");
				pp.setDirection("asc");
				pp.setCancelOrderby(true);
				pp.setFixedHeader(true) ;
				pp.setTrActionProperty(true);
				pp.setTrAction("  AccPackageID='"+acc+"' vchdate='${vchdate}' voucherid='${voucherid}' style='cursor:hand;' onDBLclick='goSort();'");

				String TableHead = "",TableHead1 = "";
				String sql = "";

				TableHead = "年,月,凭证日期,字,号,";

				sql = "select id,a.vchmonth vchmonth1,autoid,a.voucherid," +
					"case substring(a.vchdate,9) when '00' then 0 when '97' then 0 when '98' then 0 else y.vchid end p1," +
					"case substring(a.vchdate,9) when '00' then 0 when '97' then 0 when '98' then 0 else z.vchid end p2," +
					"case substring(a.vchdate,9) when '00' then 0 when '97' then 0 when '98' then 0 else t.vchid end p3," +
					"IF(substring(a.vchdate,9)='00',vchyear,'') vchyear,IF(substring(a.vchdate,9)='00',vchmonth,'') vchmonth," +
					"case substring(a.vchdate,9) when '00' then '' when '97' then '' when '98' then '' else a.vchdate end vchdate," +
					"a.typeid,a.oldvoucherid,replace(substring(a.subjects,2),',','<br>') as subjects,a.summary, ";
				String setColumnWidth = "";
				if(acc.equals(userSession.getCurAccPackageId()) && userSession.getCurProjectId()!=null && !"1".equals(html)){
//					TableHead = "抽,年,月,凭证日期,字,号,";
					TableHead1 = "抽,";
					pp.addColumn("抽","p1",FormatType.showTakeOut );
//					pp.addColumn("疑","p2",FormatType.showDoubt );
					setColumnWidth = "4,";
					ProjectService projectService = new ProjectService(conn);
					if("税务审计".equals(projectService.getProjectById(projectid).getAuditPara())){
						pp.addColumn("税", "p3", "showTaskTax");
//						TableHead = "抽,疑,税,年,月,凭证日期,字,号,";
						TableHead1 = "抽,税,";
					}
				}


				pp.addColumn("年", "vchyear","showCenter");
				pp.addColumn("月", "vchmonth","showCenter");
				pp.addColumn("凭证日期", "vchdate","showCenter");
				pp.addColumn("字", "typeid","showCenter");
				pp.addColumn("号", "oldvoucherid","showCenter");

				setColumnWidth += "5,5,8,8,5";
				if("1".equals(toSubjects)){
					TableHead += "对方科目,";
					pp.addColumn("对方科目", "subjectnames");
					setColumnWidth += ",15";
				}
				setColumnWidth += ",25";
				pp.setColumnWidth(setColumnWidth);
				
				TableHead += "摘要,";

				pp.addColumn("摘要", "summary");
//				pp.addColumn("摘要", "summary",null,null,"<td style=\"word-break: keep-all;\">${value}</td>");

				Set coll = map.keySet();
				int ii =1;

				String sqlHead1 = "";
				String sqlHead2 = "";
				String sqlHead3 = "";

				for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
					String key = (String) iter.next();
					String value = (String) map.get(key);
					sql += "if(substring(a.vchdate,9)='00','',debitrate"+ii+") debitrate"+ii+",if(substring(a.vchdate,9)='00','',debit"+key+") debit"+key+",";
					sqlHead1 += "汇率,"+value+",";

					pp.addColumn("汇率", "debitrate"+ii,"showMoney");
					pp.addColumn(value, "debit"+key,"showMoney");
					ii ++;

				}
				sql += "if(substring(a.vchdate,9)='00','',debit) debit,";
				sqlHead1 += "借方本位币";
				TableHead += "借方{"+sqlHead1+"},";

				pp.addColumn("借方本位币", "debit","showMoney");

				ii=1;
				for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
					String key = (String) iter.next();
					String value = (String) map.get(key);
					sql += "if(substring(a.vchdate,9)='00','',creditrate"+ii+") creditrate"+ii+",if(substring(a.vchdate,9)='00','',credit"+key+") credit"+key+",";
					sqlHead2 += "汇率,"+value+",";

					pp.addColumn("汇率", "creditrate"+ii,"showMoney");
					pp.addColumn(value, "credit"+key,"showMoney");
					ii ++;

				}
				sql += "if(substring(a.vchdate,9)='00','',credit) credit,IF(dateremain>0,'借',if(dateremain<0,'贷','平')) rec,";
				sqlHead2 += "贷方本位币";
				TableHead += "贷方{"+sqlHead2+"},方向,";

				pp.addColumn("贷方本位币", "credit","showMoney");
				pp.addColumn("方向", "rec","showCenter");

				ii=1;
				for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
					String key = (String) iter.next();
					String value = (String) map.get(key);
//					sql += "abs(dRemain"+key+") dRemain"+key+",";
					sql += "if(dateremain<0,(-1)*dRemain"+key+",dRemain"+key+") as dRemain"+key+",";
					sqlHead3 += value+",";

					pp.addColumn(value, "dRemain"+key,"showMoney");
					ii ++;
				}
				sql +="ABS(dateRemain) dateRemain,subjectnames  from (select *,group_concat(distinct "+subjectnameSql+") subjectnames   from (select * from `"+TabName+"` where 1=1 ${where}  order by id ${LIMIT}) a left join (select * from c_accpkgsubject where  AccPackageID="+acc+" "+sqlwhere+" ) b  on 1=1 and a.subjects like concat('%,',b.subjectid,',%')  group by a.id  ) a " +

				"left join (select distinct vchid from z_voucherspotcheck  where projectid='" +projectid + "' and createor='" + user +"'   ) y on a.voucherid=y.vchid  " +
				"left join (select distinct vchid from z_question  where projectid='" +projectid + "' and createor='" + user +"'   )  z on a.voucherid=z.vchid  " +
				"left join (select distinct vchid from z_taxcheck  where projectid='" +projectid + "' and createor='" + user +"'   )  t on a.autoid=t.vchid  " +
				"where 1=1 AND NOT (SUBSTRING(a.vchdate,9) = '00' AND vchmonth <> '01' AND id <> 1) ";
				
				sqlHead3 +="本位币余额";
				TableHead +="余额{"+sqlHead3+"}";

				pp.addColumn("本位币余额", "dateRemain","showMoney");


				pp.setLimitByOwnEnable(true);
				pp.setCountsql("select count(*) as datagrid_count from `" + TabName + "` where 1=1 ${where} ");
				pp.setEnableCountTr(false);

				pp.setTableHead(TableHead1 + TableHead);

				pp.addSqlWhere("where", "${where}");

				pp.addInputValue("TabName");
				pp.addInputValue("allOpt");
				pp.addInputValue("allType");
				pp.addInputValue("moneyChao");

				String TName = "";
				if(!"".equals(T1)) TName = new ASTextKey(conn).TextCustomerName(T1);
				
				pp.setPrintEnable(true);
				pp.setPrintTitle(TName + "      科目明细账查询");
				pp.setPrintCharColumn("1`2`3`4") ;
				
				pp.setPrintPoms("科目名称："+subjectEntry.getSubFullName(acc,SubjectID) +"    期间："+BeginYear+"年"+BeginDate+"月－"+EndYear+"年"+EndDate+"月     贷币单位："+new ASTextKey(conn).getACurrRate(acc));

				
				pp.setPrintTableHead(TableHead);
				
				pp.setSQL(sql);
				request.getSession().setAttribute(DataGrid.sessionPre+pp.getTableID(),pp);
				mapResult.put("html", html);
				mapResult.put("TabName", TabName);
				mapResult.put("DataGrid", pp.getTableID());
			}

		} catch (Exception e) {
			Debug.print(Debug.iError, "科目明细账查询失败 1:叶子有外币无数量！", e);
            throw e;
		} finally {
			DbUtil.close(conn);
		}
		return new ModelAndView(_strBList,mapResult);
	}
	public ModelAndView bfourlist(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		Connection conn=null;
		HashMap mapResult = new HashMap();
		try {
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
			ASFuntion CHF=new ASFuntion();

			String user=CHF.showNull(userSession.getUserId());
			String projectid=CHF.showNull(userSession.getCurProjectId());
			if("".equals(projectid)) projectid = "-1";

			String T1 = CHF.showNull(request.getParameter("T1"));
			String TabName = CHF.showNull(request.getParameter("TabName"));

			String acc = CHF.showNull(request.getParameter("AccPackageID"));


			if("".equals(acc)){
				acc = CHF.showNull(userSession.getCurChoiceAccPackageId());
				if("".equals(acc)){
					acc = CHF.showNull(userSession.getCurAccPackageId());
				}
			}

			String BeginYear = CHF.showNull(request.getParameter("BeginYear"));
			if("".equals(BeginYear)){
				BeginYear = CHF.showNull(userSession.getCurChoiceBeginYear());
				if("".equals(BeginYear)){
					BeginYear = CHF.showNull(userSession.getCurProjectBeginYear());
				}
			}
			String EndYear = CHF.showNull(request.getParameter("EndYear"));
			if("".equals(EndYear)){
				EndYear = CHF.showNull(userSession.getCurChoiceEndYear());
				if("".equals(EndYear)){
					EndYear = CHF.showNull(userSession.getCurProjectEndYear());
				}
			}
			String BeginDate = CHF.showNull(request.getParameter("BeginDate"));
			if("".equals(BeginDate)){
				BeginDate = CHF.showNull(userSession.getCurChoiceBeginMonth());
				if("".equals(BeginDate)){
					BeginDate = CHF.showNull(userSession.getCurProjectBeginMonth());
				}
			}
			String EndDate = CHF.showNull(request.getParameter("EndDate"));
			if("".equals(EndDate)){
				EndDate = CHF.showNull(userSession.getCurChoiceEndMonth());
				if("".equals(EndDate)){
					EndDate = CHF.showNull(userSession.getCurProjectEndMonth());
				}
			}

			String toSubjects = CHF.showNull(request.getParameter("toSubjects"));//显示对方科目
			String sqlwhere = "";
			if(!toSubjects.equals("1")){
				sqlwhere = " and  1=2 ";
			}
			String toLevel1 = CHF.showNull(request.getParameter("toLevel1")); //对方科目显示的级数
			String subjectnameSql = "";
			if(toLevel1.equals("1")){
				subjectnameSql = "if(locate('/',b.subjectfullname1)=0,b.subjectfullname1,substr(b.subjectfullname1,1,locate('/',b.subjectfullname1)-1))";
			}else{
//				subjectnameSql =  "if(b.subjectname1!=b.subjectfullname1 && concat(if(locate('/',b.subjectfullname1)=0,b.subjectfullname1,substr(b.subjectfullname1,1,locate('/',b.subjectfullname1)-1)),'/',b.subjectname1)!=b.subjectfullname1 \n"
//					 +" ,concat(if(locate('/',b.subjectfullname1)=0,b.subjectfullname1,substr(b.subjectfullname1,1,locate('/',b.subjectfullname1)-1)),'/../',b.subjectname1) \n"
//					 +" ,b.subjectfullname1) ";
				subjectnameSql =  " b.subjectfullname1 ";
			}
			String SubjectID = CHF.showNull(request.getParameter("SubjectID"));
			conn = new DBConnect().getConnect(T1);
			SubjectEntry subjectEntry = new SubjectEntry(conn);
			Map Cmap = subjectEntry.SubjectCurrency(acc,SubjectID); //汇率
			Map Umap = subjectEntry.SubjectUnitName(acc,SubjectID); //单价

			if(!subjectEntry.ExistsTable(TabName)){
				subjectEntry.CreateTable(TabName,Cmap,Umap);
				subjectEntry.DataToTable(Cmap,Umap,TabName,user,projectid,T1,SubjectID,BeginYear,BeginDate,EndYear,EndDate);
			}

			String sTable = "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tr><td>科目名称：<font color=\"blue\">"+subjectEntry.getSubFullName(acc,SubjectID)+"</font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;期间：<font color=\"blue\">"+BeginYear+"年"+BeginDate+"月－"+EndYear+"年"+EndDate+"月</font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;本位币名称：<font color=\"blue\">"+new ASTextKey(conn).getACurrRate(acc)+"</font></td></tr></table>";

			mapResult.put("SubjectID", SubjectID);
			mapResult.put("TabName", TabName);
			mapResult.put("T1", T1);
			mapResult.put("sTable", sTable);

			/**
			 * DataGrid
			 */
			if(!"".equals(SubjectID)){
				DataGridProperty pp = new DataGridProperty(){
					public void onSearch (
		    				javax.servlet.http.HttpSession session,
		    				javax.servlet.http.HttpServletRequest request,
		    				javax.servlet.http.HttpServletResponse response) throws Exception{

						String TabName = this.getRequestValue("TabName");

						String allOpt = this.getRequestValue("allOpt");
						String moneyChao = this.getRequestValue("moneyChao");
						if(moneyChao == null || "".equals(moneyChao.trim())){
							allOpt = "0";
						}
						
						String strWhere = "";
						String allType = this.getRequestValue("allType");
						if("".equals(allType)) allType = "1";
						if("1".equals(allType)){
							strWhere = " ABS(dateRemain) ";
						}else if("2".equals(allType)){
							strWhere = " ABS(debit) ";
						}else if("3".equals(allType)){
							strWhere = " ABS(credit) ";
						}
						
						String where = "";
						if("1".equals(allOpt)){
							where = " and substring(vchdate,9) not in ('00','97','98') " +
							" and " + strWhere + " >" + moneyChao ;
						}else if("2".equals(allOpt)){
							where = " and substring(vchdate,9) not in ('00','97','98') " +
							" and " + strWhere + " >" + moneyChao +
							" and id in (" +
							" 	select max(id) from `"+TabName+"` " +
							"	where 1=1 and substring(vchdate,9) not in ('00','97','98')" +
							"	group by vchdate" +
							" ) ";
						}else if("3".equals(allOpt)){
							where = " and substring(vchdate,9) not in ('00','97','98') " +
							" and " + strWhere + " >" + moneyChao +
							" and id in (" +
							" 	select max(id) from `"+TabName+"` " +
							"	where 1=1 and substring(vchdate,9) not in ('00','97','98')" +
							"	group by vchmonth" +
							" ) ";
						}


						this.setOrAddRequestValue("where",where);
					}
				};
				
				String html = CHF.showNull(request.getParameter("html"));	//用于标志要不要用setTdtoValue　1为要　
				if("1".equals(html)){
					pp.setTdtoValue("<a href='St://${value}'>${tdvalue}</a>");	
				}
				
				pp.setTableID("SubjectEntry"+subjectEntry.getRandom());
				pp.setCustomerId(T1);
				pp.setPageSize_CH(100);
				pp.setWhichFieldIsValue(3);
				pp.setOrderBy_CH("id");
				pp.setDirection("asc");
				pp.setCancelOrderby(true);

				pp.setTrActionProperty(true);
				pp.setTrAction("  AccPackageID='"+acc+"' vchdate='${vchdate}' voucherid='${voucherid}' style='cursor:hand;' onDBLclick='goSort();'");

				String TableHead = "",TableHead1 = "";
				String sql = "";

				TableHead = "凭证日期,字,号,";

				sql = "select id,a.vchmonth vchmonth1,autoid,a.voucherid," +
					"case substring(a.vchdate,9) when '00' then 0 when '97' then 0 when '98' then 0 else y.vchid end p1," +
					"case substring(a.vchdate,9) when '00' then 0 when '97' then 0 when '98' then 0 else z.vchid end p2," +
					"case substring(a.vchdate,9) when '00' then 0 when '97' then 0 when '98' then 0 else t.vchid end p3," +
					"IF(substring(a.vchdate,9)='00',vchyear,'') vchyear,IF(substring(a.vchdate,9)='00',vchmonth,'') vchmonth," +
					"case substring(a.vchdate,9) when '00' then '' when '97' then '' when '98' then '' else a.vchdate end vchdate," +
					"a.typeid,a.oldvoucherid,replace(substring(a.subjects,2),',','<br>') as subjects,a.summary, ";
				String setColumnWidth = "";
				if(acc.equals(userSession.getCurAccPackageId()) && userSession.getCurProjectId()!=null && !"1".equals(html)){
//					TableHead = "抽,年,月,凭证日期,字,号,";
					TableHead1 = "抽,";
					pp.addColumn("抽","p1",FormatType.showTakeOut );
//					pp.addColumn("疑","p2",FormatType.showDoubt );
					setColumnWidth = "4,";
					ProjectService projectService = new ProjectService(conn);
					if("税务审计".equals(projectService.getProjectById(projectid).getAuditPara())){
						pp.addColumn("税", "p3", "showTaskTax");
//						TableHead = "抽,疑,税,年,月,凭证日期,字,号,";
						TableHead1 = "抽,税,";
					}
				}


//				pp.addColumn("年", "vchyear","showCenter");
//				pp.addColumn("月", "vchmonth","showCenter");
				pp.addColumn("凭证日期", "vchdate","showCenter");
				pp.addColumn("字", "typeid","showCenter");
				pp.addColumn("号", "oldvoucherid","showCenter");
				pp.addColumn("摘要", "summary");
				TableHead += "摘要,";
				
				setColumnWidth += "8,8,5,25";
				if("1".equals(toSubjects)){
					TableHead += "对方科目,";
					pp.addColumn("对方科目", "subjectnames");
					setColumnWidth += ",15";
				}
				pp.setColumnWidth(setColumnWidth);
				

				Set Ucoll = Umap.keySet();
				Set Ccoll = Cmap.keySet();
				int ii =1;

				String sqlHead1 = "";
				String sqlHead2 = "";
				String sqlHead3 = "";

				for (Iterator iter = Ucoll.iterator(); iter.hasNext(); ) {
					String key = (String) iter.next();
					String value = (String) Umap.get(key);
					sql += "if(substring(a.vchdate,9)='00','',debitPrice"+ii+") debitPrice"+ii+",if(substring(a.vchdate,9)='00','',debit"+key+") debit"+key+",";
					sqlHead1 += "单价,"+value+",";

					pp.addColumn("单价", "debitPrice"+ii,"showMoney");
					pp.addColumn(value, "debit"+key,"showMoney");
					ii ++;
				}

				ii =1;
				for (Iterator iter = Ccoll.iterator(); iter.hasNext(); ) {
					String key = (String) iter.next();
					String value = (String) Cmap.get(key);
					sql += "if(substring(a.vchdate,9)='00','',debitrate"+ii+") debitrate"+ii+",if(substring(a.vchdate,9)='00','',debit"+key+") debit"+key+",";
					sqlHead1 += "汇率,"+value+",";

					pp.addColumn("汇率", "debitrate"+ii,"showMoney");
					pp.addColumn(value, "debit"+key,"showMoney");
					ii ++;
				}
				sql += "if(substring(a.vchdate,9)='00','',debit) debit,";
				sqlHead1 += "借方本位币";
				TableHead += "借方{"+sqlHead1+"},";

				pp.addColumn("借方本位币", "debit","showMoney");

				ii=1;
				for (Iterator iter = Ucoll.iterator(); iter.hasNext(); ) {
					String key = (String) iter.next();
					String value = (String) Umap.get(key);
					sql += "if(substring(a.vchdate,9)='00','',creditPrice"+ii+") creditPrice"+ii+",if(substring(a.vchdate,9)='00','',credit"+key+") credit"+key+",";
					sqlHead2 += "单价,"+value+",";

					pp.addColumn("单价", "creditPrice"+ii,"showMoney");
					pp.addColumn(value, "credit"+key,"showMoney");
					ii ++;
				}

				ii=1;
				for (Iterator iter = Ccoll.iterator(); iter.hasNext(); ) {
					String key = (String) iter.next();
					String value = (String) Cmap.get(key);
					sql += "if(substring(a.vchdate,9)='00','',creditrate"+ii+") creditrate"+ii+",if(substring(a.vchdate,9)='00','',credit"+key+") credit"+key+",";
					sqlHead2 += "汇率,"+value+",";

					pp.addColumn("汇率", "creditrate"+ii,"showMoney");
					pp.addColumn(value, "credit"+key,"showMoney");
					ii ++;

				}

				sql += "if(substring(a.vchdate,9)='00','',credit) credit,IF(dateremain>0,'借',if(dateremain<0,'贷','平')) rec,";
				sqlHead2 += "贷方本位币";
				TableHead += "贷方{"+sqlHead2+"},方向,";

				pp.addColumn("贷方本位币", "credit","showMoney");
				pp.addColumn("方向", "rec","showCenter");

				ii=1;
				for (Iterator iter = Ucoll.iterator(); iter.hasNext(); ) {
					String key = (String) iter.next();
					String value = (String) Umap.get(key);
//					sql += "abs(dRemain"+key+") dRemain"+key+",";
					sql += "if(dateremain<0,(-1)*dRemain"+key+",dRemain"+key+") as dRemain"+key+",";
					sqlHead3 += value+",";

					pp.addColumn(value, "dRemain"+key,"showMoney");
					ii ++;
				}

				ii=1;
				for (Iterator iter = Ccoll.iterator(); iter.hasNext(); ) {
					String key = (String) iter.next();
					String value = (String) Cmap.get(key);
//					sql += "abs(dRemain"+key+") dRemain"+key+",";
					sql += "if(dateremain<0,(-1)*dRemain"+key+",dRemain"+key+") as dRemain"+key+",";
					sqlHead3 += value+",";

					pp.addColumn(value, "dRemain"+key,"showMoney");
					ii ++;
				}

				sql +="ABS(dateRemain) dateRemain,subjectnames  " +
				"from (" +
				"	select a.*,group_concat(distinct "+subjectnameSql+") subjectnames   from (" +
				"		select *,IF(credit>0,1,-1) AS opt from `"+TabName+"` where 1=1 ${where} order by id ${LIMIT}" +
				"	) a left join c_subjectentry b " +
				"	on 1=1 " +
				"	AND SUBSTRING(b.VchDate,1,4)*12 + SUBSTRING(b.VchDate,6,2) >= " + (Integer.parseInt(BeginYear) * 12 + Integer.parseInt(BeginDate)) +  
				"	AND SUBSTRING(b.VchDate,1,4)*12 + SUBSTRING(b.VchDate,6,2) <= " + (Integer.parseInt(EndYear) * 12 + Integer.parseInt(EndDate)) + 
				"	AND a.voucherid=b.voucherid " +
				"	AND a.opt = IF(b.Dirction*b.occurvalue<0,-1,1) " +
				"	AND a.autoid <>b.autoid " + sqlwhere + 
				"	group by a.id  " +
				") a " +
				"left join (select distinct vchid from z_voucherspotcheck  where projectid='" +projectid + "' and createor='" + user +"'   ) y on a.voucherid=y.vchid  " +
				"left join (select distinct vchid from z_question  where projectid='" +projectid + "' and createor='" + user +"'   )  z on a.voucherid=z.vchid  " +
				"left join (select distinct vchid from z_taxcheck  where projectid='" +projectid + "' and createor='" + user +"'   )  t on a.autoid=t.vchid  where 1=1 ";


				sqlHead3 +="本位币余额";
				TableHead +="余额{"+sqlHead3+"}";

				pp.addColumn("本位币余额", "dateRemain","showMoney");

				pp.setTableHead(TableHead1 + TableHead);

				pp.setLimitByOwnEnable(true);
				pp.setCountsql("select count(*) as datagrid_count from `" + TabName + "` where 1=1 ${where} ");
				pp.setEnableCountTr(false);

				pp.addSqlWhere("where", "${where}");

				pp.addInputValue("TabName");
				pp.addInputValue("allOpt");
				pp.addInputValue("allType");
				pp.addInputValue("moneyChao");
				pp.setFixedHeader(true) ;
				pp.setSQL(sql);
				
				String TName = "";
				if(!"".equals(T1)) TName = new ASTextKey(conn).TextCustomerName(T1);
				
				pp.setPrintEnable(true);
				pp.setPrintTitle(TName + "      科目明细账查询");
				pp.setPrintCharColumn("1`2`3`4") ;
				
				pp.setPrintPoms("科目名称："+subjectEntry.getSubFullName(acc,SubjectID) +"    期间："+BeginYear+"年"+BeginDate+"月－"+EndYear+"年"+EndDate+"月     贷币单位："+new ASTextKey(conn).getACurrRate(acc));

				
				pp.setPrintTableHead(TableHead);
				
				request.getSession().setAttribute(DataGrid.sessionPre+pp.getTableID(),pp);
				mapResult.put("html", html);
				mapResult.put("TabName", TabName);
				mapResult.put("DataGrid", pp.getTableID());


			}

		} catch (Exception e) {
			Debug.print(Debug.iError, "科目明细账查询失败 3:叶子有外币有数量！", e);
            throw e;
		} finally {
			DbUtil.close(conn);
		}
		return new ModelAndView(_strBList,mapResult);
	}
	
	
	
		

	public ModelAndView deltable(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		Connection conn=null;
		try {
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
			ASFuntion CHF=new ASFuntion();
			String acc = CHF.showNull(request.getParameter("AccPackageID"));
			if("".equals(acc)){
				acc = CHF.showNull(userSession.getCurChoiceAccPackageId());
				if("".equals(acc)){
					acc = CHF.showNull(userSession.getCurAccPackageId());
				}
			}

			String dataGrid =   CHF.showNull(request.getParameter("dataGrid"));
			org.util.Debug.prtOut("deltable 删除="+dataGrid);
			if(!"".equals(dataGrid))
				request.getSession().removeAttribute(DataGrid.sessionPre + dataGrid);

			conn = new DBConnect().getConnect(acc.substring(0,6));

			String TabName = CHF.showNull(request.getParameter("TableName"));
			if(!"".equals(TabName)){
				new SubjectEntry(conn).DelTempTable(TabName);
			}

		} catch (Exception e) {
			Debug.print(Debug.iError, "删除临时表失败！", e);
            throw e;
		} finally {
			DbUtil.close(conn);
		}
		return null;
	}

	/**
	 * 多栏帐
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView mlist(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		HashMap mapResult = new HashMap();
		ASFuntion CHF=new ASFuntion();
		String acc = CHF.showNull(request.getParameter("AccPackageID"));
		String SubjectID = CHF.showNull(request.getParameter("SubjectID"));

		DataGridProperty pp = new DataGridProperty(){
			public void onSearch (
    				javax.servlet.http.HttpSession session,
    				javax.servlet.http.HttpServletRequest request,
    				javax.servlet.http.HttpServletResponse response) throws Exception{
				Connection conn=null;
				try {
					String acc = this.getRequestValue("AccPackageID");
					String SubjectID = this.getRequestValue("SubjectID");

					conn = new DBConnect().getConnect(acc.substring(0, 6));
//					this.setOrAddRequestValue("SubjectID",SubjectID);
					if(!"".equals(SubjectID)){
						if(new SubjectEntry(conn).isLeaf(acc,SubjectID)){
							this.setOrAddRequestValue("SubjectID",SubjectID);
							this.setOrAddRequestValue("ParentSubjectId","fail");
						}else{
							this.setOrAddRequestValue("ParentSubjectId",SubjectID);
							this.setOrAddRequestValue("SubjectID","");
						}
					}else{
						this.setOrAddRequestValue("SubjectID","");
						this.setOrAddRequestValue("ParentSubjectId","fail");
					}


					this.setNotRepalce("SubjectID,ParentSubjectId");

				} catch (Exception e) {
					e.printStackTrace();

				} finally {
					DbUtil.close(conn);
				}


			}
		};
		pp.setTableID("mlist");
		pp.setCustomerId(acc.substring(0, 6));;
		pp.setPageSize_CH(100);
		pp.setWhichFieldIsValue(1);
		pp.setCancelOrderby(true);
		pp.setCancelPage(true);
		pp.setInputType("checkbox");
//		pp.setInputType("button");

		String sql = "select Subjectid,Subjectname from c_accpkgsubject where accpackageid='"+acc+"' and (   SubjectId='${SubjectID}'  or  ParentSubjectId='${ParentSubjectId}'  ) ";

		pp.addColumn("科目", "Subjectid");
		pp.addColumn("科目名称", "Subjectname");

		pp.addSqlWhere("SubjectID", "${SubjectID}");
		pp.addSqlWhere("ParentSubjectId", "${ParentSubjectId}");


		pp.addInputValue("SubjectID");
		pp.addInputValue("AccPackageID");

		pp.setCancelAjaxSynchronization(false); //异步开关
		pp.setSQL(sql);
		request.getSession().setAttribute(DataGrid.sessionPre+pp.getTableID(),pp);


		mapResult.put("AccPackageID", acc);
		return new ModelAndView(_strMList,mapResult);
	}

	public ModelAndView bfivelist(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		Connection conn=null;
		HashMap mapResult = new HashMap();
		try {
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
			ASFuntion CHF=new ASFuntion();

			String user=CHF.showNull(userSession.getUserId());
			String projectid=CHF.showNull(userSession.getCurProjectId());
			if("".equals(projectid)) projectid = "-1";

			String T1 = CHF.showNull(request.getParameter("T1"));
			String TabName = CHF.showNull(request.getParameter("TabName"));


			String acc = CHF.showNull(request.getParameter("AccPackageID"));
			if("".equals(acc)){
				acc = CHF.showNull(userSession.getCurChoiceAccPackageId());
				if("".equals(acc)){
					acc = CHF.showNull(userSession.getCurAccPackageId());
				}
			}

			String BeginYear = CHF.showNull(request.getParameter("BeginYear"));
			if("".equals(BeginYear)){
				BeginYear = CHF.showNull(userSession.getCurChoiceBeginYear());
				if("".equals(BeginYear)){
					BeginYear = CHF.showNull(userSession.getCurProjectBeginYear());
				}
			}
			String EndYear = CHF.showNull(request.getParameter("EndYear"));
			if("".equals(EndYear)){
				EndYear = CHF.showNull(userSession.getCurChoiceEndYear());
				if("".equals(EndYear)){
					EndYear = CHF.showNull(userSession.getCurProjectEndYear());
				}
			}
			String BeginDate = CHF.showNull(request.getParameter("BeginDate"));
			if("".equals(BeginDate)){
				BeginDate = CHF.showNull(userSession.getCurChoiceBeginMonth());
				if("".equals(BeginDate)){
					BeginDate = CHF.showNull(userSession.getCurProjectBeginMonth());
				}
			}
			String EndDate = CHF.showNull(request.getParameter("EndDate"));
			if("".equals(EndDate)){
				EndDate = CHF.showNull(userSession.getCurChoiceEndMonth());
				if("".equals(EndDate)){
					EndDate = CHF.showNull(userSession.getCurProjectEndMonth());
				}
			}

			String SubjectID = CHF.showNull(request.getParameter("SubjectID"));
			String sData = CHF.showNull(request.getParameter("sData"));

			conn = new DBConnect().getConnect(T1);
			SubjectEntry subjectEntry = new SubjectEntry(conn);

			if("".equals(sData)){
				sData = subjectEntry.getSubject(acc,SubjectID);
			}
			
			System.out.println("sData="+sData);

			if(!subjectEntry.ExistsTable(TabName)){
				subjectEntry.CreateTable(TabName,sData);
				subjectEntry.DataToTable(sData,TabName,user,projectid,T1,SubjectID,BeginYear,BeginDate,EndYear,EndDate);
			}

			/**
			 * DataGrid
			 */
			if(!"".equals(SubjectID)){
				DataGridProperty pp = new DataGridProperty(){
					public void onSearch (
		    				javax.servlet.http.HttpSession session,
		    				javax.servlet.http.HttpServletRequest request,
		    				javax.servlet.http.HttpServletResponse response) throws Exception{

						String TabName = this.getRequestValue("TabName");

						String allOpt = this.getRequestValue("allOpt");
						String moneyChao = this.getRequestValue("moneyChao");

						String strWhere = "";
						String allType = this.getRequestValue("allType");
						if("".equals(allType)) allType = "1";
						if("1".equals(allType)){
							strWhere = " ABS(dateRemain) ";
						}else if("2".equals(allType)){
							strWhere = " ABS(debit) ";
						}else if("3".equals(allType)){
							strWhere = " ABS(credit) ";
						}
						
						String where = "";
						if("1".equals(allOpt)){
							where = " and substring(vchdate,9) not in ('00','97','98') " +
							" and " + strWhere + " >" + moneyChao ;
						}else if("2".equals(allOpt)){
							where = " and substring(vchdate,9) not in ('00','97','98') " +
							" and " + strWhere + " >" + moneyChao +
							" and id in (" +
							" 	select max(id) from `"+TabName+"` " +
							"	where 1=1 and substring(vchdate,9) not in ('00','97','98')" +
							"	group by vchdate" +
							" ) ";
						}else if("3".equals(allOpt)){
							where = " and substring(vchdate,9) not in ('00','97','98') " +
							" and " + strWhere + " >" + moneyChao +
							" and id in (" +
							" 	select max(id) from `"+TabName+"` " +
							"	where 1=1 and substring(vchdate,9) not in ('00','97','98')" +
							"	group by vchmonth" +
							" ) ";
						}


						this.setOrAddRequestValue("where",where);
					}
				};
				
				String html = CHF.showNull(request.getParameter("html"));	//用于标志要不要用setTdtoValue　1为要　
				if("1".equals(html)){
					pp.setTdtoValue("<a href='St://${value}'>${tdvalue}</a>");	
				}
				
				pp.setTableID("SubjectEntry"+subjectEntry.getRandom());
				pp.setCustomerId(T1);
				pp.setPageSize_CH(100);
				pp.setWhichFieldIsValue(3);
				pp.setOrderBy_CH("id");
				pp.setDirection("asc");
				pp.setCancelOrderby(true);

				pp.setTrActionProperty(true);
				pp.setTrAction("  AccPackageID='"+acc+"' vchdate='${vchdate}' voucherid='${voucherid}' style='cursor:hand;' onDBLclick='goSort();'");
				String TableHead = "",TableHead1 = "";
				String sql = "";

				TableHead = "年,月,凭证日期,字,号,摘要,";

				sql = "select id,a.vchmonth vchmonth1,autoid,a.voucherid," +
					"case substring(a.vchdate,9) when '00' then 0 when '97' then 0 when '98' then 0 else y.vchid end p1," +
					"case substring(a.vchdate,9) when '00' then 0 when '97' then 0 when '98' then 0 else z.vchid end p2," +
					"case substring(a.vchdate,9) when '00' then 0 when '97' then 0 when '98' then 0 else t.vchid end p3," +
					"IF(substring(a.vchdate,9)='00',vchyear,'') vchyear,IF(substring(a.vchdate,9)='00',vchmonth,'') vchmonth," +
					"case substring(a.vchdate,9) when '00' then '' when '97' then '' when '98' then '' else a.vchdate end vchdate," +
					"a.typeid,a.oldvoucherid,a.summary,";
				String setColumnWidth = "";
				if(acc.equals(userSession.getCurAccPackageId()) && userSession.getCurProjectId()!=null && !"1".equals(html)){
//					TableHead = "抽,年,月,凭证日期,字,号,摘要,";
					TableHead1 = "抽,";
					pp.addColumn("抽","p1",FormatType.showTakeOut );
//					pp.addColumn("疑","p2",FormatType.showDoubt );
					setColumnWidth = "4,";
					ProjectService projectService = new ProjectService(conn);
					if("税务审计".equals(projectService.getProjectById(projectid).getAuditPara())){
						pp.addColumn("税", "p3", "showTaskTax");
//						TableHead = "抽,疑,税,年,月,凭证日期,字,号,摘要,";
						TableHead1 = "抽,税,";
					}
				}

				setColumnWidth += "5,5,8,8,5,25";
				pp.setColumnWidth(setColumnWidth);
				pp.addColumn("年", "vchyear","showCenter");
				pp.addColumn("月", "vchmonth","showCenter");
				pp.addColumn("凭证日期", "vchdate","showCenter");
				pp.addColumn("字", "typeid","showCenter");
				pp.addColumn("号", "oldvoucherid","showCenter");
				pp.addColumn("摘要", "summary");
//				pp.addColumn("摘要", "summary",null,null,"<td style=\"word-break: keep-all;\">${value}</td>");

				String [] sAll = sData.split("`");
				String ss = "";
				for(int i=1;i < sAll.length; i++) {
					String sName = subjectEntry.getSName(acc,sAll[i]);
					sql += "if(debit"+i+"=0.00 && credit"+i+"=0.00,'',debit"+i+") debit"+i+",";
					pp.addColumn(sName, "debit"+i,"showMoney");
					ss += sName+",";
				}
				for(int i=1;i < sAll.length; i++) {
					String sName = subjectEntry.getSName(acc,sAll[i]);
					sql += "if(debit"+i+"=0.00 && credit"+i+"=0.00,'',credit"+i+") credit"+i+",";
					pp.addColumn(sName, "credit"+i,"showMoney");
				}

				sql += " IF(dateremain>0,'借',if(dateremain<0,'贷','平')) rec,ABS(dateremain) dateremain from (select * from `"+TabName+"`  where 1=1 ${where}  order by id ${LIMIT} ) a " +
				"left join (select distinct vchid from z_voucherspotcheck  where projectid='" +projectid + "' and createor='" + user +"'   ) y on a.voucherid=y.vchid  " +
				"left join (select distinct vchid from z_question  where projectid='" +projectid + "' and createor='" + user +"'   )  z on a.voucherid=z.vchid  " +
				"left join (select distinct vchid from z_taxcheck  where projectid='" +projectid + "' and createor='" + user +"'   )  t on a.autoid=t.vchid  where 1=1 ";
;

				pp.addColumn("方向", "rec","showCenter");
				pp.addColumn("余额", "dateRemain","showMoney");

				TableHead += "借方{"+ss.substring(0, ss.length()-1)+"},贷方{"+ss.substring(0, ss.length()-1)+"},方向,余额";

				pp.setTableHead(TableHead1 + TableHead);

				pp.setLimitByOwnEnable(true);
				pp.setCountsql("select count(*) as datagrid_count from `" + TabName + "`  where 1=1 ${where} ");
				pp.setEnableCountTr(false);

				pp.addSqlWhere("where", "${where}");

				pp.addInputValue("TabName");
				pp.addInputValue("allOpt");
				pp.addInputValue("allType");
				pp.addInputValue("moneyChao");
				
				String TName = "";
				if(!"".equals(T1)) TName = new ASTextKey(conn).TextCustomerName(T1);
				
				pp.setPrintEnable(true);
				pp.setPrintTitle(TName + "      科目明细账查询");
				pp.setPrintCharColumn("1`2`3`4") ;
				
				pp.setPrintPoms("科目名称："+subjectEntry.getSubFullName(acc,SubjectID) +"    期间："+BeginYear+"年"+BeginDate+"月－"+EndYear+"年"+EndDate+"月     贷币单位："+new ASTextKey(conn).getACurrRate(acc));
				pp.setPrintTableHead(TableHead);
				
				pp.setFixedHeader(true) ;
				pp.setSQL(sql);
				request.getSession().setAttribute(DataGrid.sessionPre+pp.getTableID(),pp);
				mapResult.put("html", html);
				mapResult.put("TabName", TabName);
				mapResult.put("DataGrid", pp.getTableID());
			}

		} catch (Exception e) {
			Debug.print(Debug.iError, "科目明细账查询失败 4:多栏帐！", e);
            throw e;
		} finally {
			DbUtil.close(conn);
		}
		return new ModelAndView(_strBList,mapResult);
	}

	//打印设置
	public ModelAndView print(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		ASFuntion CHF=new ASFuntion();
		HashMap mapResult = new HashMap();

		String countPage = CHF.showNull(request.getParameter("countPage"));
		String colcount = CHF.showNull(request.getParameter("colcount"));
		String selectPage = CHF.showNull(request.getParameter("selectPage"));
		String selectOption = CHF.showNull(request.getParameter("selectOption"));
		String monthbegin = CHF.showNull(request.getParameter("monthbegin"));
		String monthend = CHF.showNull(request.getParameter("monthend"));

		mapResult.put("countPage", countPage);
		mapResult.put("colcount", colcount);
		mapResult.put("selectPage", selectPage);
		mapResult.put("selectOption", selectOption);
		mapResult.put("monthbegin", monthbegin);
		mapResult.put("monthend", monthend);

		return new ModelAndView("/Excel/tempdata/PrintSetup.jsp",mapResult);
	}

	public ModelAndView btext(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		Connection conn=null;
		HashMap mapResult = new HashMap();
		try {
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
			ASFuntion CHF=new ASFuntion();

			String user=CHF.showNull(userSession.getUserId());
			String projectid=CHF.showNull(userSession.getCurProjectId());
			if("".equals(projectid)) projectid = "-1";

			String DGrid =(String)CHF.showNull(request.getParameter("DataGrid"));

			String T1 =(String)CHF.showNull(request.getParameter("T1"));
			String SubjectID = (String)CHF.showNull(request.getParameter("SubjectID"));
			String BeginYear =(String)CHF.showNull(request.getParameter("BeginYear"));
			String BeginDate = (String)CHF.showNull(request.getParameter("BeginDate"));
			String EndYear =(String)CHF.showNull(request.getParameter("EndYear"));
			String EndDate = (String)CHF.showNull(request.getParameter("EndDate"));

			String TabName = (String)CHF.showNull(request.getParameter("TabName"));

			String acc = T1 + EndYear;

			String txtType = (String)CHF.showNull(request.getParameter("txtType"));
			String txtBegin =(String)CHF.showNull(request.getParameter("txtBegin"));
			String txtEnd = (String)CHF.showNull(request.getParameter("txtEnd"));
			String txtSQL1 = "";
			String txtSQL2 = "";
			txtType = "".equals(txtType)?"0":txtType;

			String sData = (String)CHF.showNull(request.getParameter("sData"));
			String Currency = (String)CHF.showNull(request.getParameter("Currency"));

			String toSubjects = (String)CHF.showNull(request.getParameter("toSubjects"));

			switch(Integer.parseInt(txtType)){
			case 0:
				break;
			case 1:
			case 2:
				txtSQL1 = " limit "+txtBegin+","+txtEnd;
				break;
			case 3:
				txtSQL2 = " and vchmonth1>="+txtBegin+" and vchmonth1<="+txtEnd+" ";
				break;
			}

			conn = new DBConnect().getConnect(T1);
			SubjectEntry subjectEntry = new SubjectEntry(conn);

			int result = subjectEntry.SubjectProperty(acc,SubjectID);
			if("1".equals(Currency) && "".equals(sData)){
				result = 0;
			}

			Map map = subjectEntry.SubjectCurrency(acc,SubjectID);
			Map Umap = subjectEntry.SubjectUnitName(acc,SubjectID);

			if(!"".equals(sData)){
				result = 4;
			}
			/*
			if("".equals(sData)){
				if(!subjectEntry.ExistsTable(TabName)){
					switch(result){
					case 0:
						subjectEntry.CreateTable(TabName);
						subjectEntry.DataToTable(TabName,user,projectid,T1,SubjectID,BeginYear,BeginDate,EndYear,EndDate);
					break;
					case 1:
						subjectEntry.CreateTable(TabName,map);
						subjectEntry.DataToTable(map,TabName,user,projectid,T1,SubjectID,BeginYear,BeginDate,EndYear,EndDate);
						break;
					case 2:
						subjectEntry.CreateUnitTable(TabName,Umap);
						subjectEntry.DataToUnitTable(Umap,TabName,user,projectid,T1,SubjectID,BeginYear,BeginDate,EndYear,EndDate);
						break;
					case 3:
						subjectEntry.CreateTable(TabName,map,Umap);
						subjectEntry.DataToTable(map,Umap,TabName,user,projectid,T1,SubjectID,BeginYear,BeginDate,EndYear,EndDate);
						break;
					}
				}
			}else{
				if(!subjectEntry.ExistsTable(TabName)){
					subjectEntry.CreateTable(TabName,sData);
					subjectEntry.DataToTable(sData,TabName,user,projectid,T1,SubjectID,BeginYear,BeginDate,EndYear,EndDate);
				}
				result = 4;
			}
			*/
			ASTextKey tkey = new ASTextKey(conn);
			String TName = "";
			String ss = "";
			String full = subjectEntry.getSubFullName(acc,SubjectID);
			if(!"".equals(T1)) {
				TName = tkey.TextCustomerName(T1);
				ss = tkey.getACurrRate(acc);
			}

			DataGridProperty pp = (DataGridProperty)request.getSession().getAttribute(DataGrid.sessionPre+DGrid); //科目明细账查询

			PrintSetup printSetup = new PrintSetup(conn);
			printSetup.setStrTitles(new String[]{TName+" 科目明细账查询"});

			List[] lists = new List[1];
			lists[0] = new ArrayList();
			lists[0].add(0, "1");
			lists[0].add(1, "科目名称："+full +"    期间："+BeginYear+"年"+BeginDate+"月－"+EndYear+"年"+EndDate+"月     贷币单位："+ss);
			lists[0].add(2, "9");
			lists[0].add(3, null);

			printSetup.setPoms(lists);
			printSetup.setCharColumn(new String[]{"1`2`3`4`5`6"});
			String sRows = "";

			String [][] CTName = new String[1][2];
			String [][] ColName = new String[1][2];


			String sql = "select vchyear,vchmonth,vchdate,typeid,oldvoucherid,";

			if("1".equals(toSubjects) && result!=4){
				sql += "replace(subjectnames,'<br>',','),";
			}

			sql += "summary,";
			String tempsql = sql;
			
			String txtSQL = " limit ";
			int iSheet = 10000;

			int TCount = 0;
			TCount = Integer.parseInt(pp.getAllCount());

			int TFor = TCount / iSheet;
			ArrayList filename = new ArrayList();
			for(int t = 0; t<= TFor; t++ ){
				sql = tempsql;
				txtSQL += String.valueOf(t * iSheet ) + ",10000" ;

				switch(result){
					case 0:		//本位币
						if("1".equals(toSubjects)){
							printSetup.setStrChineseTitles(new String[]{"年`月`凭证日期`字`号`对方科目`摘要`借方发生额`贷方发生额`方向`余　额"});
							printSetup.setIColumnWidths(new int[]{6,4,13,6,6,16,30,25,25,4,25});
						}else{
							printSetup.setStrChineseTitles(new String[]{"年`月`凭证日期`字`号`摘要`借方发生额`贷方发生额`方向`余　额"});
							printSetup.setIColumnWidths(new int[]{6,4,13,6,6,46,25,25,4,25});
						}
						printSetup.setStrQuerySqls(new String[]{"select * from ("+sql + "debit,credit,rec,dateremain from ("+pp.getFinishSQLDeleteLimit()+") a where 1=1 "+txtSQL2+" order by id "+txtSQL1+")a "+txtSQL });
						sRows = "$2:$4";
						break;
					case 1:			//外币
						CTName[0][1] = new String();
						CTName[0][1] = ",";
						ColName[0][1] = new String();
						ColName[0][1]= ",";
						CTName[0][0] = new String();
						ColName[0][0] = new String();
	
						CTName[0][0] = "年,月,凭证日期,字,号,";
						ColName[0][0] = "年,月,凭证日期,字,号,";
	
						if("1".equals(toSubjects)){
							CTName[0][0] += "对方科目,";
							ColName[0][0] += "对方科目,";
						}
						CTName[0][0] += "摘要,";
						ColName[0][0] += "摘要,";
	
	
						Set coll = map.keySet();
						int ii =1;
						for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
							String key = (String) iter.next();
							String value = (String) map.get(key);
							sql += "debitrate"+ii+",debit"+key+",";
							CTName[0][0] += "借方,借方,";
							ColName[0][0] += "汇率,"+value+",";
							ii ++;
						}
						sql += "debit,";
						CTName[0][0] += "借方,";
						ColName[0][0] += "借方本位币,";
	
						ii=1;
						for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
							String key = (String) iter.next();
							String value = (String) map.get(key);
							sql += "creditrate"+ii+",credit"+key+",";
							CTName[0][0] += "贷方,贷方,";
							ColName[0][0] += "汇率,"+value+",";
							ii ++;
						}
						sql += "credit,rec,";
						CTName[0][0] += "贷方,方向,";
						ColName[0][0] += "贷方本位币,方向,";
	
						ii=1;
						for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
							String key = (String) iter.next();
							String value = (String) map.get(key);
							sql += "dRemain"+key+",";
							CTName[0][0] += "余额,";
							ColName[0][0] += value+",";
							ii ++;
						}
						sql += "dateRemain";
						CTName[0][0] += "余额";
						ColName[0][0] += "本位币余额";
	
						printSetup.setStrQuerySqls(new String[]{"select * from ("+sql + " from ("+pp.getFinishSQLDeleteLimit()+") a where 1=1 "+txtSQL2+"  order by id "+txtSQL1+")a "+txtSQL });
						printSetup.setHeaders(CTName, ColName);
	
						sRows="$2:$5";
						break;
					case 2:		//数量
						CTName[0][1] = ",";
						ColName[0][1]= ",";
	
						CTName[0][0] = "年,月,凭证日期,字,号,";
						ColName[0][0] = "年,月,凭证日期,字,号,";
	
						if("1".equals(toSubjects)){
							CTName[0][0] += "对方科目,";
							ColName[0][0] += "对方科目,";
						}
						CTName[0][0] += "摘要,";
						ColName[0][0] += "摘要,";
	
						coll = Umap.keySet();
						ii =1;
						for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
							String key = (String) iter.next();
							String value = (String) Umap.get(key);
							sql += "debitPrice"+ii+",debit"+key+",";
							CTName[0][0] += "借方,借方,";
							ColName[0][0] += "单价,"+value+",";
							ii ++;
						}
						sql += "debit,";
						CTName[0][0] += "借方,";
						ColName[0][0] += "借方本位币,";
	
						ii=1;
						for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
							String key = (String) iter.next();
							String value = (String) Umap.get(key);
							sql += "creditPrice"+ii+",credit"+key+",";
							CTName[0][0] += "贷方,贷方,";
							ColName[0][0] += "单价,"+value+",";
							ii ++;
						}
						sql += "credit,rec,";
						CTName[0][0] += "贷方,方向,";
						ColName[0][0] += "贷方本位币,方向,";
	
						ii=1;
						for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
							String key = (String) iter.next();
							String value = (String) Umap.get(key);
							sql += "dRemain"+key+",";
							CTName[0][0] += "余额,";
							ColName[0][0] += value+",";
							ii ++;
						}
						sql += "dateRemain";
						CTName[0][0] += "余额";
						ColName[0][0] += "本位币余额";
	
						printSetup.setStrQuerySqls(new String[]{"select * from ("+sql + " from ("+pp.getFinishSQLDeleteLimit()+") a where 1=1 "+txtSQL2+"  order by id "+txtSQL1+")a "+txtSQL });
						printSetup.setHeaders(CTName, ColName);
	
						sRows="$2:$5";
						break;
					case 3:		//外币与数量
						CTName[0][1] = ",";
						ColName[0][1]= ",";
	
	
						CTName[0][0] = "年,月,凭证日期,字,号,";
						ColName[0][0] = "年,月,凭证日期,字,号,";
	
						if("1".equals(toSubjects)){
							CTName[0][0] += "对方科目,";
							ColName[0][0] += "对方科目,";
						}
						CTName[0][0] += "摘要,";
						ColName[0][0] += "摘要,";
	
	
						Set Ucoll = Umap.keySet();
						Set Ccoll = map.keySet();
	
						ii =1;
						for (Iterator iter = Ucoll.iterator(); iter.hasNext(); ) {
							String key = (String) iter.next();
							String value = (String) Umap.get(key);
							sql += "debitPrice"+ii+",debit"+key+",";
							CTName[0][0] += "借方,借方,";
							ColName[0][0] += "单价,"+value+",";
							ii ++;
						}
						ii =1;
						for (Iterator iter = Ccoll.iterator(); iter.hasNext(); ) {
							String key = (String) iter.next();
							String value = (String) map.get(key);
							sql += "debitrate"+ii+",debit"+key+",";
							CTName[0][0] += "借方,借方,";
							ColName[0][0] += "汇率,"+value+",";
							ii ++;
						}
						sql += "debit,";
						CTName[0][0] += "借方,";
						ColName[0][0] += "借方本位币,";
	
						ii=1;
						for (Iterator iter = Ucoll.iterator(); iter.hasNext(); ) {
							String key = (String) iter.next();
							String value = (String) Umap.get(key);
							sql += "creditPrice"+ii+",credit"+key+",";
							CTName[0][0] += "贷方,贷方,";
							ColName[0][0] += "单价,"+value+",";
							ii ++;
						}
						ii=1;
						for (Iterator iter = Ccoll.iterator(); iter.hasNext(); ) {
							String key = (String) iter.next();
							String value = (String) map.get(key);
							sql += "creditrate"+ii+",credit"+key+",";
							CTName[0][0] += "贷方,贷方,";
							ColName[0][0] += "汇率,"+value+",";
							ii ++;
						}
						sql += "credit,rec,";
						CTName[0][0] += "贷方,方向,";
						ColName[0][0] += "贷方本位币,方向,";
	
						ii=1;
						for (Iterator iter = Ucoll.iterator(); iter.hasNext(); ) {
							String key = (String) iter.next();
							String value = (String) Umap.get(key);
							sql += "dRemain"+key+",";
							CTName[0][0] += "余额,";
							ColName[0][0] += value+",";
							ii ++;
						}
						ii=1;
						for (Iterator iter = Ccoll.iterator(); iter.hasNext(); ) {
							String key = (String) iter.next();
							String value = (String) map.get(key);
							sql += "dRemain"+key+",";
							CTName[0][0] += "余额,";
							ColName[0][0] += value+",";
							ii ++;
						}
						sql += "dateRemain";
						CTName[0][0] += "余额";
						ColName[0][0] += "本位币余额";
	
						printSetup.setStrQuerySqls(new String[]{"select * from ("+sql + " from ("+pp.getFinishSQLDeleteLimit()+") a where 1=1 "+txtSQL2+"  order by id "+txtSQL1+")a "+txtSQL });
						printSetup.setHeaders(CTName, ColName);
	
						sRows="$2:$5";
						break;
					case 4:		//多栏帐
						CTName[0][1] = ",";
						ColName[0][1]= ",";
	
						CTName[0][0] = "年,月,凭证日期,字,号,";
						ColName[0][0] = "年,月,凭证日期,字,号,";
	
	//					if("1".equals(toSubjects)){
	//						CTName[0][0] += "对方科目,";
	//						ColName[0][0] += "对方科目,";
	//					}
						CTName[0][0] += "摘要,";
						ColName[0][0] += "摘要,";
	
						String [] sAll = sData.split("`");
						String s1="";
						String s2="";
						String t1="";
						String t2="";
						String tt="";
	
						for(int i=1;i < sAll.length; i++) {
							String sName = subjectEntry.getSName(acc,sAll[i]);
							s1 += " debit"+i+",";
							s2 += " credit"+i+",";
							t1 +="借方,";
							t2 +="贷方,";
							tt +=sName+",";
						}
	
						CTName[0][0] += t1 + t2 +"方向,余额";
						ColName[0][0] += tt + tt +"方向,余额";
	
						printSetup.setStrQuerySqls(new String[]{"select * from ("+sql + s1 +s2 + "rec,dateremain from ("+pp.getFinishSQLDeleteLimit()+") a where 1=1 "+txtSQL2+"  order by id "+txtSQL1+")a "+txtSQL });
						printSetup.setHeaders(CTName, ColName);
	
						sRows="$2:$5";
						break;
				}

				printSetup.setStrSheetName("科目明细账-"+(t+1));

				filename.add( printSetup.getExcelFile());

				txtSQL = " limit ";
			}
			printSetup = null;
			//vpage strPrintTitleRows
			mapResult.put("refresh","");

			mapResult.put("saveasfilename","科目明细账");
			mapResult.put("bVpage","false");
			mapResult.put("strPrintTitleRows",sRows);
			mapResult.put("filenameList", filename);

		} catch (Exception e) {
			Debug.print(Debug.iError, "科目明细账打印失败!", e);
            throw e;
		} finally {
			DbUtil.close(conn);
		}
		return new ModelAndView("/Excel/tempdata/PrintandSave.jsp",mapResult);
	}

	/**
	 * 科目总帐
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView glist(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		response.setContentType("text/html;charset=utf-8");
		Connection conn=null;
		HashMap mapResult = new HashMap();
		String pid= request.getParameter("pid");
		try {
			ASFuntion CHF=new ASFuntion();
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
			String T1 = CHF.showNull(request.getParameter("T1"));
			String acc = CHF.showNull(request.getParameter("AccPackageID"));


			if("".equals(acc)){
				acc = CHF.showNull(userSession.getCurChoiceAccPackageId());
				if("".equals(acc)){
					acc = CHF.showNull(userSession.getCurAccPackageId());
				}
			}else{
				userSession.setCurChoiceAccPackageId(acc);
			}
			if(!"".equals(acc)){
				if("".equals(T1)){
					T1=acc.substring(0,6);
				}
			}
			if(!"".equals(acc)){
				conn = new DBConnect().getConnect(T1);

				ASTextKey tkey = new ASTextKey(conn);
				String TName = "";
				if(!"".equals(T1)) TName = tkey.TextCustomerName(T1);

				String BeginYear = CHF.showNull(request.getParameter("BeginYear"));
				if("".equals(BeginYear)){
					BeginYear = CHF.showNull(userSession.getCurChoiceBeginYear());
					if("".equals(BeginYear)){
						BeginYear = CHF.showNull(userSession.getCurProjectBeginYear());
					}
				}else{
					userSession.setCurChoiceBeginYear(BeginYear);
				}
				if("".equals(BeginYear) ){
					BeginYear = acc.substring(6);
				}
				

				String EndYear = CHF.showNull(request.getParameter("EndYear"));
				if("".equals(EndYear)){
					EndYear = CHF.showNull(userSession.getCurChoiceEndYear());
					if("".equals(EndYear)){
						EndYear = CHF.showNull(userSession.getCurProjectEndYear());
					}
				}else{
					userSession.setCurChoiceEndYear(EndYear);
				}
				if("".equals(EndYear) ){
					EndYear = acc.substring(6);
				}
				

				String BeginDate = CHF.showNull(request.getParameter("BeginDate"));
				if("".equals(BeginDate)){
					BeginDate = CHF.showNull(userSession.getCurChoiceBeginMonth());
					if("".equals(BeginDate)){
						BeginDate = CHF.showNull(userSession.getCurProjectBeginMonth());
					}
				}else{
					userSession.setCurChoiceBeginMonth(BeginDate);
				}
				if("".equals(BeginDate)) BeginDate = "01";

				String EndDate = CHF.showNull(request.getParameter("EndDate"));
				if("".equals(EndDate)){
					EndDate = CHF.showNull(userSession.getCurChoiceEndMonth());
					if("".equals(EndDate)){
						EndDate = CHF.showNull(userSession.getCurProjectEndMonth());
					}
				}else{
					userSession.setCurChoiceEndMonth(EndDate);
				}
				if("".equals(EndDate)) EndDate = "12";

				/**
				 * 没有狗不能看2005年后的帐套
				 */
				int accAllowYear = Integer.parseInt(UTILSysProperty.SysProperty.getProperty("accAllowYear")); 
				if (Integer.parseInt(EndYear) > accAllowYear && JRockey2Opp.getUserLic() <= 0) {
				    final String TRY_URL = "/AuditSystem/AS_SYSTEM/error_page.jsp?tip=999";
				    response.sendRedirect(TRY_URL);
				    return null;
				}

				String SubjectID = CHF.showNull(request.getParameter("SubjectID"));

				SubjectEntry subjectEntry = new SubjectEntry(conn);

				if("".equals(subjectEntry.getSName(acc, SubjectID))){
					SubjectID = "";
				}

				String Subjects = subjectEntry.getSubjects(acc, SubjectID);

				String Currency = CHF.showNull(request.getParameter("Currency"));

				PrintWriter out = response.getWriter();

				if(!"".equals(SubjectID)){

					Map Smap = subjectEntry.SubjectCurrency(acc,SubjectID); //外币
					Map Umap = subjectEntry.SubjectUnitName(acc,SubjectID); //数量

					int result = 0;
					int r1 = subjectEntry.SubjectProperty(acc,SubjectID);
					int r2 = subjectEntry.LowSubjectProperty(acc,SubjectID);

					if("1".equals(Currency)){
						result = 0;
					}else{
						switch(r1){
							case 0:
//								switch(r2){
//									case 0:
//										break;
//									case 1:
//										out.println("<script>alert(\"该科目["+SubjectID+"]的下级科目存在外币帐。如要查看外币账,请选择下级科目；本科目只显示本币金额! \");</script>");
//										break;
//									case 2:
//										out.println("<script>alert(\"该科目["+SubjectID+"]的下级科目存在数量帐。如要查看数量账,请选择下级科目；本科目只显示本币金额! \");</script>");
//										break;
//									case 3:
//										out.println("<script>alert(\"该科目["+SubjectID+"]的下级科目存在外币帐和数量帐。如要查看外币和数量账,请选择下级科目；本科目只显示本币金额! \");</script>");
//										break;
//								}
								result = 0;
								break;
							case 1:
								result = 1;
								break;
							case 2:
								result = 2;
								break;
							case 3:
								result = 3;
								break;
						}
					}
					mapResult.put("Currency", Currency);
					mapResult.put("r2", new Integer(r2));
					mapResult.put("r1", new Integer(r1));

					String acc1 = T1+BeginYear;
					String sql = "";
					String sName = "";
					String tName1 = "";
					String tName2 = "";

					DataGridProperty pp = new DataGridProperty();

					pp.setTableID("GLIST"+subjectEntry.getRandom());
					pp.setCustomerId(T1);
					pp.setPageSize_CH(100);
					pp.setWhichFieldIsValue(1);
					pp.setCancelOrderby(true);
					pp.setCancelPage(true);
					pp.setTrActionProperty(true);
					pp.setTrAction(" AccPackageID='${accpackageid}'  Month='${sm}' SubjectID='${subjectid}' style='cursor:hand;' onDBLclick='goSort();'");

					/**
					 * 科目连续性
					 */
					AutoTokenService ats = new AutoTokenService(conn);
					String tokenid = ats.getTokenid(acc, SubjectID);
					String tokenid2 = ats.getTokenidLeaf(acc, SubjectID);

					String setColumnWidth = "5,5,10";
					
					switch(result){
					case 0:

						sql = "select * from (" +
						" select accpackageid,subjectid,LPAD(SubMonth,2,'0') sm,subyearMonth,LPAD(SubMonth,2,'0') submonth,if(a.SubMonth=1,'>年初余额','>期初余额') Summary,'' DebitOcc,'' CreditOcc, "+
						" if(DebitRemain+CreditRemain>0,'借',if(DebitRemain+CreditRemain<0,'贷','平')) dir, "+
						" direction * (DebitRemain+CreditRemain) remain,concat(subyearMonth,LPAD(SubMonth,2,'0'),'0') ss from c_account a "+
						" where accpackageid>='"+acc1+"' and accpackageid<='"+acc+"' "+
						" and concat(subyearMonth,LPAD(SubMonth,2,'0'))>=concat('"+BeginYear+"','"+BeginDate+"')"+
						" and concat(subyearMonth,LPAD(SubMonth,2,'0'))<=concat('"+EndYear+"','"+EndDate+"')"+
						" and tokenid='"+tokenid+"' " +
						" union " +
						" select accpackageid,subjectid,LPAD(SubMonth,2,'0') sm,'','','>本月合计',DebitOcc,CreditOcc, "+
						" if(Balance>0,'借',if(Balance<0,'贷','平')) dir,direction * (Balance) remain,concat(subyearMonth,LPAD(SubMonth,2,'0'),'1') ss "+
						" from c_account where accpackageid>='"+acc1+"' and accpackageid<='"+acc+"' "+
						" and concat(subyearMonth,LPAD(SubMonth,2,'0'))>=concat('"+BeginYear+"','"+BeginDate+"')"+
						" and concat(subyearMonth,LPAD(SubMonth,2,'0'))<=concat('"+EndYear+"','"+EndDate+"')"+
						" and tokenid='"+tokenid+"' " +
						" union " +
						" select accpackageid,subjectid,LPAD(SubMonth,2,'0') sm,'','','>本年累计',DebitTotalOcc,CreditTotalOcc, "+
						" if(Balance>0,'借',if(Balance<0,'贷','平')) dir,direction * (Balance) remain,concat(subyearMonth,LPAD(SubMonth,2,'0'),'2') ss "+
						" from c_account where  accpackageid>='"+acc1+"' and accpackageid<='"+acc+"' "+
						" and concat(subyearMonth,LPAD(SubMonth,2,'0'))>=concat('"+BeginYear+"','"+BeginDate+"')"+
						" and concat(subyearMonth,LPAD(SubMonth,2,'0'))<=concat('"+EndYear+"','"+EndDate+"')"+
						" and tokenid='"+tokenid+"' " +
						" ) a where 1=1  order by abs(ss)";

						pp.addColumn("年", "subyearMonth","showCenter");
						pp.addColumn("月", "submonth","showCenter");
						pp.addColumn("摘要", "summary",null,null,"<td style=\"word-break: keep-all;\">${value}</td>");
						pp.addColumn("借方发生额", "DebitOcc","showMoney");
						pp.addColumn("贷方发生额", "CreditOcc","showMoney");
						pp.addColumn("方向", "dir","showCenter");
						pp.addColumn("余　额", "remain","showMoney");

						sName = " subyearMonth,submonth,Summary,DebitOcc,CreditOcc,dir,remain ";
						tName1 = "年`月`摘要`借方发生额`贷方发生额`方向`余　额";

						break;
					case 1:
						pp.addColumn("年", "subyearMonth","showCenter");
						pp.addColumn("月", "submonth","showCenter");
						pp.addColumn("摘要", "summary",null,null,"<td style=\"word-break: keep-all;\">${value}</td>");

						Set Scoll = Smap.keySet();
						int ii =1;
						sql = "";

						String sql1 = "",sql2 = "",sql3 = "",sql4 = "",sql5 = "" ;
						String sql6 = "",sql7 = "",sql8 = "",sql9 = "",sql0 = "";
						String sN1 = "";
						String sN2 = "";
						String sN3 = "";
						String tN1 = "";
						String tN2 = "";
						String tName = "";
						for (Iterator iter = Scoll.iterator(); iter.hasNext(); ) {
							String key = (String) iter.next();
							String value = (String) Smap.get(key);

							sql1 += " '' Debit"+ii+",";
							sql2 += " '' Credit"+ii+",";
							sql3 += " a"+ii+".direction * (a"+ii+".DebitRemain+a"+ii+".CreditRemain) reocc"+ii+",";

							sql6 += " a"+ii+".DebitOcc Debit"+ii+",";
							sql7 += " a"+ii+".CreditOcc Credit"+ii+",";
							sql8 += " a"+ii+".DebitTotalOcc Debit"+ii+",";
							sql9 += " a"+ii+".CreditTotalOcc Credit"+ii+",";
							sql0 += " a"+ii+".direction * (a"+ii+".Balance) reocc"+ii+",";

							sql4 += " c_accountall a"+ii+",";
							sql5 += " and a"+ii+".tokenid = '"+tokenid+"' and a"+ii+".DataName ='"+value+"' ";
							sql5 += " and a.SubYearMonth = a"+ii+".SubYearMonth and a.SubMonth = a"+ii+".SubMonth and a.AccPackageID= a"+ii+".AccPackageID ";

							sN1 += " Debit"+ii+", ";
							sN2 += " Credit"+ii+", ";
							sN3 += " reocc"+ii+", ";

							tN1 += value+",";
							tN2 += "借方,";
							ii ++;
						}
						ii = 1;
						for (Iterator iter = Scoll.iterator(); iter.hasNext(); ) {
							String key = (String) iter.next();
							String value = (String) Smap.get(key);
							pp.addColumn(value, "Debit"+ii,"showMoney");
							ii ++;
							setColumnWidth += ",10";
						}
						pp.addColumn("借方本位币", "DebitOcc","showMoney");
						setColumnWidth += ",10";
						
						ii = 1;
						for (Iterator iter = Scoll.iterator(); iter.hasNext(); ) {
							String key = (String) iter.next();
							String value = (String) Smap.get(key);
							pp.addColumn(value, "Credit"+ii,"showMoney");
							ii ++;
							setColumnWidth += ",10";
						}
						pp.addColumn("贷方本位币", "CreditOcc","showMoney");
						pp.addColumn("方向", "dir","showCenter");
						setColumnWidth += ",10,5";
						ii = 1;
						for (Iterator iter = Scoll.iterator(); iter.hasNext(); ) {
							String key = (String) iter.next();
							String value = (String) Smap.get(key);
							pp.addColumn(value, "reocc"+ii,"showMoney");
							ii ++;
							setColumnWidth += ",10";
						}
						pp.addColumn("本位币余额", "remain","showMoney");
						setColumnWidth += ",10";
						
						tName = "年,月,摘要,借方{"+tN1+"借方本位币},贷方{"+tN1+"贷方本位币},方向,余额{"+tN1+"本位币余额}";
						pp.setTableHead(tName);

						String s1 = " a.AccPackageID>='"+acc1+"' and a.AccPackageID<='"+acc+"'  and concat(a.subyearMonth,LPAD(a.SubMonth,2,'0'))>=concat('"+BeginYear+"','"+BeginDate+"') and concat(a.subyearMonth,LPAD(a.SubMonth,2,'0'))<=concat('"+EndYear+"','"+EndDate+"') ";
						s1 += "  and a.tokenid = '"+tokenid+"' "+ sql5;

						sql = "select *  from(select a.accpackageid,a.subjectid,LPAD(a.SubMonth,2,'0')  sm,a.subyearMonth,LPAD(a.SubMonth,2,'0') submonth,if(a.SubMonth=1,'>年初余额','>期初余额') Summary,"+
							sql1 + " '' DebitOcc," + sql2 + " '' CreditOcc, if(a.DebitRemain+a.CreditRemain>0,'借',if(a.DebitRemain+a.CreditRemain<0,'贷','平')) dir,"+
							sql3 + " a.direction * (a.DebitRemain+a.CreditRemain) remain,concat(a.subyearMonth,LPAD(a.SubMonth,2,'0'),'0') ss from "+
							sql4 + " c_account a where "+ s1 + " union select a.accpackageid,a.subjectid, LPAD(a.SubMonth,2,'0')  sm,'','','>本月合计' Summary,"+
							sql6 + " a.DebitOcc," + sql7 + " a.CreditOcc, if(a.Balance>0,'借',if(a.Balance<0,'贷','平')) dir,"+
							sql0 + " a.direction * (a.Balance) remain,concat(a.subyearMonth,LPAD(a.SubMonth,2,'0'),'1') ss from "+sql4+" c_account a where "+
							s1 + " union select  a.accpackageid,a.subjectid,LPAD(a.SubMonth,2,'0')  sm,'','','>本年累计' Summary, "+
							sql8 + " a.DebitTotalOcc, "+ sql9 + " a.CreditTotalOcc,if(a.Balance>0,'借',if(a.Balance<0,'贷','平')) dir,"+
							sql0 + " a.direction * (a.Balance) remain,concat(a.subyearMonth,LPAD(a.SubMonth,2,'0'),'2') ss from "+sql4+" c_account a where "+
							s1 + ") a where 1=1  order by abs(ss)";

						sName = " subyearMonth,submonth,Summary,"+sN1+" DebitOcc,"+sN2+" CreditOcc,dir,"+sN3+" remain ";
						tName1 = "年,月,摘要,"+tN1+"借方本位币,"+tN1+"贷方本位币,方向,"+tN1+"本位币余额";
						tName2 = "年,月,摘要,"+tN2+"借方,"+tN2.replaceAll("借方","贷方")+"贷方,方向,"+tN2.replaceAll("借方","余额")+"余额";

						break;
					case 2:
						pp.addColumn("年", "subyearMonth","showCenter");
						pp.addColumn("月", "submonth","showCenter");
						pp.addColumn("摘要", "summary",null,null,"<td style=\"word-break: keep-all;\">${value}</td>");
						
						Set Ucoll = Umap.keySet();
						ii =1;
						sql = "";

						sql1 = "";
						sql2 = "";
						sql3 = "";
						sql4 = "";
						sql5 = "";
						sql6 = "";
						sql7 = "";
						sql8 = "";
						sql9 = "";
						sql0 = "";
						sN1 = "";
						sN2 = "";
						sN3 = "";
						tN1 = "";
						tN2 = "";
						for (Iterator iter = Ucoll.iterator(); iter.hasNext(); ) {
					    	String key = (String) iter.next();
							String value = (String) Umap.get(key);

							sql1 += " '' Debit"+ii+",";
							sql2 += " '' Credit"+ii+",";
							sql3 += " a"+ii+".direction * (a"+ii+".DebitRemain+a"+ii+".CreditRemain) reocc"+ii+",";

							sql6 += " a"+ii+".DebitOcc Debit"+ii+",";
							sql7 += " a"+ii+".CreditOcc Credit"+ii+",";
							sql8 += " a"+ii+".DebitTotalOcc Debit"+ii+",";
							sql9 += " a"+ii+".CreditTotalOcc Credit"+ii+",";
							sql0 += " a"+ii+".direction * (a"+ii+".Balance) reocc"+ii+",";

							sql4 += " c_accountall a"+ii+",";
							sql5 += " and a"+ii+".tokenid = '"+tokenid+"' and a"+ii+".DataName ='"+value+"' ";
							sql5 += " and a.SubYearMonth = a"+ii+".SubYearMonth and a.SubMonth = a"+ii+".SubMonth and a.AccPackageID= a"+ii+".AccPackageID ";

							sN1 += " Debit"+ii+", ";
							sN2 += " Credit"+ii+", ";
							sN3 += " reocc"+ii+", ";

							tN1 += value+",";
							tN2 += "借方,";
							ii ++;
						}
						ii = 1;
						for (Iterator iter = Ucoll.iterator(); iter.hasNext(); ) {
							String key = (String) iter.next();
							String value = (String) Umap.get(key);
							pp.addColumn(value, "Debit"+ii,"showMoney");
							ii ++;
							setColumnWidth += ",10";
						}
						pp.addColumn("借方本位币", "DebitOcc","showMoney");
						setColumnWidth += ",10";
						ii = 1;
						for (Iterator iter = Ucoll.iterator(); iter.hasNext(); ) {
							String key = (String) iter.next();
							String value = (String) Umap.get(key);
							pp.addColumn(value, "Credit"+ii,"showMoney");
							ii ++;
							setColumnWidth += ",10";
						}
						pp.addColumn("贷方本位币", "CreditOcc","showMoney");
						pp.addColumn("方向", "dir","showCenter");
						setColumnWidth += ",10,5";
						ii = 1;
						for (Iterator iter = Ucoll.iterator(); iter.hasNext(); ) {
							String key = (String) iter.next();
							String value = (String) Umap.get(key);
							pp.addColumn(value, "reocc"+ii,"showMoney");
							ii ++;
							setColumnWidth += ",10";
						}
						pp.addColumn("本位币余额", "remain","showMoney");
						setColumnWidth += ",10";
						
						tName = "年,月,摘要,借方{"+tN1+"借方本位币},贷方{"+tN1+"贷方本位币},方向,余额{"+tN1+"本位币余额}";
						pp.setTableHead(tName);

						s1 = " a.AccPackageID>='"+acc1+"' and a.AccPackageID<='"+acc+"'  and concat(a.subyearMonth,LPAD(a.SubMonth,2,'0'))>=concat('"+BeginYear+"','"+BeginDate+"') and concat(a.subyearMonth,LPAD(a.SubMonth,2,'0'))<=concat('"+EndYear+"','"+EndDate+"') ";
						s1 += "  and a.tokenid = '"+tokenid+"' "+ sql5;

						sql = "select *  from(select a.accpackageid,a.subjectid,LPAD(a.SubMonth,2,'0')  sm,a.subyearMonth,LPAD(a.SubMonth,2,'0') submonth,if(a.SubMonth=1,'>年初余额','>期初余额') Summary,"+
							sql1 + " '' DebitOcc," + sql2 + " '' CreditOcc, if(a.DebitRemain+a.CreditRemain>0,'借',if(a.DebitRemain+a.CreditRemain<0,'贷','平')) dir,"+
							sql3 + " a.direction * (a.DebitRemain+a.CreditRemain) remain,concat(a.subyearMonth,LPAD(a.SubMonth,2,'0'),'0') ss from "+
							sql4 + " c_account a where "+ s1 + " union select a.accpackageid,a.subjectid, LPAD(a.SubMonth,2,'0')  sm,'','','>本月合计' Summary,"+
							sql6 + " a.DebitOcc," + sql7 + " a.CreditOcc, if(a.Balance>0,'借',if(a.Balance<0,'贷','平')) dir,"+
							sql0 + " a.direction * (a.Balance) remain,concat(a.subyearMonth,LPAD(a.SubMonth,2,'0'),'1') ss from "+sql4+" c_account a where "+
							s1 + " union select a.accpackageid, a.subjectid,LPAD(a.SubMonth,2,'0')  sm,'','','>本年累计' Summary, "+
							sql8 + " a.DebitTotalOcc, "+ sql9 + " a.CreditTotalOcc,if(a.Balance>0,'借',if(a.Balance<0,'贷','平')) dir,"+
							sql0 + " a.direction * (a.Balance) remain,concat(a.subyearMonth,LPAD(a.SubMonth,2,'0'),'2') ss from "+sql4+" c_account a where "+
							s1 + ") a where 1=1  order by abs(ss)";

						sName = " subyearMonth,submonth,Summary,"+sN1+" DebitOcc,"+sN2+" CreditOcc,dir,"+sN3+" remain ";
						tName1 = "年,月,摘要,"+tN1+"借方本位币,"+tN1+"贷方本位币,方向,"+tN1+"本位币余额";
						tName2 = "年,月,摘要,"+tN2+"借方,"+tN2.replaceAll("借方","贷方")+"贷方,方向,"+tN2.replaceAll("借方","余额")+"余额";

						break;
					case 3:
						pp.addColumn("年", "subyearMonth","showCenter");
						pp.addColumn("月", "submonth","showCenter");
						pp.addColumn("摘要", "summary",null,null,"<td style=\"word-break: keep-all;\">${value}</td>");

						Scoll = Smap.keySet(); //外币
						Ucoll = Umap.keySet(); //数量
						ii =1;
						sql = "";

						sql1 = "";
						sql2 = "";
						sql3 = "";
						sql4 = "";
						sql5 = "";
						sql6 = "";
						sql7 = "";
						sql8 = "";
						sql9 = "";
						sql0 = "";
						sN1 = "";
						sN2 = "";
						sN3 = "";
						tN1 = "";
						tN2 = "";
						for (Iterator iter = Ucoll.iterator(); iter.hasNext(); ) {
							String key = (String) iter.next();
							String value = (String) Umap.get(key);
							sql1 += " '' UDebit"+ii+",";
							sql2 += " '' UCredit"+ii+",";
							sql3 += " b"+ii+".direction * (b"+ii+".DebitRemain+b"+ii+".CreditRemain) Ureocc"+ii+",";

							sql6 += " b"+ii+".DebitOcc UDebit"+ii+",";
							sql7 += " b"+ii+".CreditOcc UCredit"+ii+",";
							sql8 += " b"+ii+".DebitTotalOcc UDebit"+ii+",";
							sql9 += " b"+ii+".CreditTotalOcc UCredit"+ii+",";
							sql0 += " b"+ii+".direction * (b"+ii+".Balance) Ureocc"+ii+",";

							sql4 += " c_accountall b"+ii+",";
							sql5 += " and b"+ii+".tokenid = '"+tokenid+"' and b"+ii+".DataName ='"+value+"' ";
							sql5 += " and a.SubYearMonth = b"+ii+".SubYearMonth and a.SubMonth = b"+ii+".SubMonth and a.AccPackageID= b"+ii+".AccPackageID ";

							sN1 += " UDebit"+ii+", ";
							sN2 += " UCredit"+ii+", ";
							sN3 += " Ureocc"+ii+", ";

							tN1 += value+",";
							tN2 += "借方,";
							ii ++;
						}
						ii = 1;
						for (Iterator iter = Scoll.iterator(); iter.hasNext(); ) {
					    	String key = (String) iter.next();
							String value = (String) Smap.get(key);
							sql1 += " '' Debit"+ii+",";
							sql2 += " '' Credit"+ii+",";
							sql3 += " a"+ii+".direction * (a"+ii+".DebitRemain+a"+ii+".CreditRemain) reocc"+ii+",";

							sql6 += " a"+ii+".DebitOcc Debit"+ii+",";
							sql7 += " a"+ii+".CreditOcc Credit"+ii+",";
							sql8 += " a"+ii+".DebitTotalOcc Debit"+ii+",";
							sql9 += " a"+ii+".CreditTotalOcc Credit"+ii+",";
							sql0 += " a"+ii+".direction * (a"+ii+".Balance) reocc"+ii+",";

							sql4 += " c_accountall a"+ii+",";
							sql5 += " and a"+ii+".tokenid = '"+tokenid+"' and a"+ii+".DataName ='"+value+"' ";
							sql5 += " and a.SubYearMonth = a"+ii+".SubYearMonth and a.SubMonth = a"+ii+".SubMonth and a.AccPackageID= a"+ii+".AccPackageID ";

							sN1 += " Debit"+ii+", ";
							sN2 += " Credit"+ii+", ";
							sN3 += " reocc"+ii+", ";

							tN1 += value+",";
							tN2 += "借方,";
							ii ++;
						}

						ii = 1;
						for (Iterator iter = Ucoll.iterator(); iter.hasNext(); ) {
							String key = (String) iter.next();
							String value = (String) Umap.get(key);
							pp.addColumn(value, "UDebit"+ii,"showMoney");
							ii ++;
							setColumnWidth += ",10";
						}
						ii = 1;
						for (Iterator iter = Scoll.iterator(); iter.hasNext(); ) {
							String key = (String) iter.next();
							String value = (String) Smap.get(key);
							pp.addColumn(value, "Debit"+ii,"showMoney");
							ii ++;
							setColumnWidth += ",10";
						}
						pp.addColumn("借方本位币", "DebitOcc","showMoney");
						setColumnWidth += ",10";
						
						ii = 1;
						for (Iterator iter = Ucoll.iterator(); iter.hasNext(); ) {
							String key = (String) iter.next();
							String value = (String) Umap.get(key);
							pp.addColumn(value, "UCredit"+ii,"showMoney");
							ii ++;
							setColumnWidth += ",10";
						}
						ii = 1;
						for (Iterator iter = Scoll.iterator(); iter.hasNext(); ) {
							String key = (String) iter.next();
							String value = (String) Smap.get(key);
							pp.addColumn(value, "Credit"+ii,"showMoney");
							ii ++;
							setColumnWidth += ",10";
						}
						pp.addColumn("贷方本位币", "CreditOcc","showMoney");
						pp.addColumn("方向", "dir","showCenter");
						setColumnWidth += ",10,5";
						ii = 1;
						for (Iterator iter = Ucoll.iterator(); iter.hasNext(); ) {
							String key = (String) iter.next();
							String value = (String) Umap.get(key);
							pp.addColumn(value, "Ureocc"+ii,"showMoney");
							ii ++;
							setColumnWidth += ",10";
						}
						ii = 1;
						for (Iterator iter = Scoll.iterator(); iter.hasNext(); ) {
							String key = (String) iter.next();
							String value = (String) Smap.get(key);
							pp.addColumn(value, "reocc"+ii,"showMoney");
							ii ++;
							setColumnWidth += ",10";
						}
						pp.addColumn("本位币余额", "remain","showMoney");
						setColumnWidth += ",10";

						tName = "年,月,摘要,借方{"+tN1+"借方本位币},贷方{"+tN1+"贷方本位币},方向,余额{"+tN1+"本位币余额}";
						pp.setTableHead(tName);

						s1 = " a.AccPackageID>='"+acc1+"' and a.AccPackageID<='"+acc+"'  and concat(a.subyearMonth,LPAD(a.SubMonth,2,'0'))>=concat('"+BeginYear+"','"+BeginDate+"') and concat(a.subyearMonth,LPAD(a.SubMonth,2,'0'))<=concat('"+EndYear+"','"+EndDate+"') ";
						s1 += "  and a.tokenid = '"+tokenid+"' "+ sql5;

						sql = "select *  from(select a.accpackageid,a.subjectid, LPAD(a.SubMonth,2,'0')  sm,a.subyearMonth,LPAD(a.SubMonth,2,'0') submonth,if(a.SubMonth=1,'>年初余额','>期初余额') Summary,"+
							sql1 + " '' DebitOcc," + sql2 + " '' CreditOcc, if(a.DebitRemain+a.CreditRemain>0,'借',if(a.DebitRemain+a.CreditRemain<0,'贷','平')) dir,"+
							sql3 + " a.direction * (a.DebitRemain+a.CreditRemain) remain,concat(a.subyearMonth,LPAD(a.SubMonth,2,'0'),'0') ss from "+
							sql4 + " c_account a where "+ s1 + " union select a.accpackageid,a.subjectid, LPAD(a.SubMonth,2,'0')  sm,'','','>本月合计' Summary,"+
							sql6 + " a.DebitOcc," + sql7 + " a.CreditOcc, if(a.Balance>0,'借',if(a.Balance<0,'贷','平')) dir,"+
							sql0 + " a.direction * (a.Balance) remain,concat(a.subyearMonth,LPAD(a.SubMonth,2,'0'),'1') ss from "+sql4+" c_account a where "+
							s1 + " union select a.accpackageid,a.subjectid, LPAD(a.SubMonth,2,'0')  sm,'','','>本年累计' Summary, "+
							sql8 + " a.DebitTotalOcc, "+ sql9 + " a.CreditTotalOcc,if(a.Balance>0,'借',if(a.Balance<0,'贷','平')) dir,"+
							sql0 + " a.direction * (a.Balance) remain,concat(a.subyearMonth,LPAD(a.SubMonth,2,'0'),'2') ss from "+sql4+" c_account a where "+
							s1 + ") a where 1=1  order by abs(ss)";


						sName = " subyearMonth,submonth,Summary,"+sN1+" DebitOcc,"+sN2+" CreditOcc,dir,"+sN3+" remain ";
						tName1 = "年,月,摘要,"+tN1+"借方本位币,"+tN1+"贷方本位币,方向,"+tN1+"本位币余额";
						tName2 = "年,月,摘要,"+tN2+"借方,"+tN2.replaceAll("借方","贷方")+"贷方,方向,"+tN2.replaceAll("借方","余额")+"余额";

						break;
					}
					
					pp.setColumnWidth(setColumnWidth);
					
					String StrName = "科目名称："+subjectEntry.getSubFullName(acc,SubjectID)+"       期间："+BeginYear+"年"+BeginDate+"月－"+EndYear+"年"+EndDate+"月        本位币名称："+new ASTextKey(conn).getACurrRate(acc);

					String sTable = "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tr><td>科目名称：<font color=\"blue\">"+subjectEntry.getSubFullName(acc,SubjectID)+"</font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;期间：<font color=\"blue\">"+BeginYear+"年"+BeginDate+"月－"+EndYear+"年"+EndDate+"月</font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;本位币名称：<font color=\"blue\">"+new ASTextKey(conn).getACurrRate(acc)+"</font></td></tr></table>";
					mapResult.put("sTable", sTable);

					request.getSession().setAttribute("StrName",null);
					request.getSession().setAttribute("sName",null);
					request.getSession().setAttribute("tName1",null);
					request.getSession().setAttribute("tName2",null);

					request.getSession().setAttribute("StrName",StrName);
					request.getSession().setAttribute("sName",sName);
					request.getSession().setAttribute("tName1",tName1);
					request.getSession().setAttribute("tName2",tName2);
					
					pp.setFixedHeader(true) ;

					mapResult.put("DataGrid", pp.getTableID());

					pp.setSQL(sql);
					request.getSession().setAttribute(DataGrid.sessionPre+pp.getTableID(),pp);

				}

				mapResult.put("T1", T1);
				mapResult.put("TName", TName);
				mapResult.put("BeginYear", BeginYear);
				mapResult.put("BeginDate", BeginDate);
				mapResult.put("EndYear", EndYear);
				mapResult.put("EndDate", EndDate);
				mapResult.put("SubjectID", SubjectID);
				mapResult.put("Subjects", Subjects);
			}else{
				mapResult.put("SubjectID", "");
				mapResult.put("BeginDate", "01");
				mapResult.put("EndDate", "12");
			}

			mapResult.put("AccPackageID", acc);

		} catch (Exception e) {
			Debug.print(Debug.iError, "科目总帐查询失败！", e);
            throw e;
		} finally {
			DbUtil.close(conn);
		}
		if(pid==null)
			return new ModelAndView(_strGList,mapResult);
		else
			return new ModelAndView(_strFList,mapResult);
	}


	public ModelAndView gtext(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		Connection conn=null;
		HashMap mapResult = new HashMap();
		try {
			ASFuntion CHF=new ASFuntion();

			String StrName = (String)request.getSession().getAttribute("StrName");
			String sName = (String)request.getSession().getAttribute("sName");
			String tName1 = (String)request.getSession().getAttribute("tName1");
			String tName2 = (String)request.getSession().getAttribute("tName2");

			String acc =(String)CHF.showNull(request.getParameter("AccPackageID"));
			String SubjectID = (String)CHF.showNull(request.getParameter("SubjectID"));
			String Currency = (String)CHF.showNull(request.getParameter("Currency"));

			String DGrid = (String)CHF.showNull(request.getParameter("DataGrid"));

			conn = new DBConnect().getConnect(acc.substring(0,6));

			SubjectEntry subjectEntry = new SubjectEntry(conn);

			DataGridProperty pp = (DataGridProperty)request.getSession().getAttribute(DataGrid.sessionPre+DGrid);

			PrintSetup printSetup = new PrintSetup(conn);

			List[] lists = new List[1];
			lists[0] = new ArrayList();
			lists[0].add(0, "1");
			lists[0].add(1, StrName);
			lists[0].add(2, "9");
			lists[0].add(3, null);

			printSetup.setPoms(lists);

			printSetup.setCharColumn(new String[]{"1`2`3`6"});
			printSetup.setIColumnWidths(new int[]{7,3,29,28,27,6,27});
			printSetup.setStrTitles(new String[]{new ASTextKey(conn).TextCustomerName(acc.substring(0,6))+" 科目总帐查询"});
			printSetup.setStrQuerySqls(new String[]{"select "+sName+" from ("+pp.getFinishSQLDeleteLimit()+" ) a"});
			String sRows = "";

			int result = subjectEntry.SubjectProperty(acc,SubjectID);
			if("1".equals(Currency)){
				result = 0;
			}

			switch(result){
				case 0:
					printSetup.setStrChineseTitles(new String[]{tName1});
					printSetup.setIColumnWidths(new int[]{7,4,49,19,19,6,19});
					sRows = "$2:$4";
					break;
				case 1:
				case 2:
				case 3:
					String [][] CTName = new String[][]{new String[]{tName2,","}};
					String [][] ColName = new String[][]{new String[]{tName1,","}};
					printSetup.setHeaders(CTName, ColName);
//					printSetup.setIColumnWidths(new int[]{6,4,28,15,15,4,15});
					sRows="$2:$5";
					break;
			}
			String filename = printSetup.getExcelFile();

			//vpage strPrintTitleRows
			mapResult.put("refresh","");

			mapResult.put("saveasfilename","科目总账");
			mapResult.put("bVpage","true");
			mapResult.put("strPrintTitleRows",sRows);
			mapResult.put("filename", filename);

		}catch (Exception e) {
			Debug.print(Debug.iError, "科目总帐打印失败！", e);
            throw e;
		} finally {
			DbUtil.close(conn);
		}
		return new ModelAndView("/Excel/tempdata/PrintandSave.jsp",mapResult);
	}

	/**
	 * 日记帐
	 */

	public ModelAndView vlist(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		ModelAndView modelAndView = new ModelAndView(_strVList);
		Connection conn=null;
		try {
			ASFuntion CHF=new ASFuntion();
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");

			String T1 = CHF.showNull(request.getParameter("T1"));
			String acc = CHF.showNull(request.getParameter("AccPackageID"));

			if("".equals(acc)){
				acc = CHF.showNull(userSession.getCurChoiceAccPackageId());
				if("".equals(acc)){
					acc = CHF.showNull(userSession.getCurAccPackageId());
				}
			}else{
				userSession.setCurChoiceAccPackageId(acc);
			}
			if(!"".equals(acc)){
				if("".equals(T1)){
					T1=acc.substring(0,6);
				}
			}

			if(!"".equals(acc)){
				conn = new DBConnect().getConnect(T1);

				ASTextKey tkey = new ASTextKey(conn);
				String TName = "";
				if(!"".equals(T1)) TName = tkey.TextCustomerName(T1);

				String Begin = CHF.showNull(request.getParameter("Begin"));
				String End = CHF.showNull(request.getParameter("End"));


				if("".equals(Begin)){
					
					Begin = acc.substring(6) + "-01-01";
				}

				if("".equals(End)){
					End = acc.substring(6) + "-12-31";
				}

				if(!"".equals(End)){
					acc = T1 + End.substring(0, 4);
				}

				/**
				 * 没有狗不能看2005年后的帐套
				 */
				int accAllowYear = Integer.parseInt(UTILSysProperty.SysProperty.getProperty("accAllowYear")); 
				if (Integer.parseInt( End.substring(0, 4)) > accAllowYear && JRockey2Opp.getUserLic() <= 0) {
				    final String TRY_URL = "/AuditSystem/AS_SYSTEM/error_page.jsp?tip=999";
				    response.sendRedirect(TRY_URL);
				    return null;
				}

				String SubjectID = CHF.showNull(request.getParameter("SubjectID"));

				SubjectEntry subjectEntry = new SubjectEntry(conn);

				if(subjectEntry.isExistAccPackage(acc)){
					String SubjectId1 = "";
					if("".equals(subjectEntry.getSName1(acc, SubjectID))){
						SubjectID = "";
					} else {
						SubjectId1 = subjectEntry.getSName1(acc, SubjectID);
					}

					String Subjects = subjectEntry.getSubjects(acc, SubjectID);
					SubjectID = SubjectId1;

					String currency = CHF.showNull(request.getParameter("currency"));
					String toSubjects = CHF.showNull(request.getParameter("toSubjects"));


					modelAndView.addObject("SubjectID", SubjectID);
					modelAndView.addObject("Subjects", Subjects);
					modelAndView.addObject("currency", currency);
					modelAndView.addObject("toSubjects", toSubjects);

					modelAndView.addObject("BeginDate", Begin.substring(5, 7));
					modelAndView.addObject("EndDate", End.substring(5, 7));
					modelAndView.addObject("isExistAccPackage", "0");
				}else{
					modelAndView.addObject("SubjectID", "");
					modelAndView.addObject("BeginDate", "01");
					modelAndView.addObject("EndDate", "12");
					modelAndView.addObject("isExistAccPackage", "1");
				}

				modelAndView.addObject("T1", T1);
				modelAndView.addObject("TName", TName);
				modelAndView.addObject("Begin", Begin);
				modelAndView.addObject("End", End);
			}else{
				modelAndView.addObject("SubjectID", "");
				modelAndView.addObject("BeginDate", "01");
				modelAndView.addObject("EndDate", "12");
			}

			modelAndView.addObject("AccPackageID", acc);


		} catch (Exception e) {
			e.printStackTrace();
			 throw e;
		} finally {
			DbUtil.close(conn);
		}
		return modelAndView;
	}

	public ModelAndView vpage(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		Connection conn=null;
		try {
			ASFuntion CHF=new ASFuntion();
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");

			String TabName = CHF.showNull(request.getParameter("TabName"));

			String T1 = CHF.showNull(request.getParameter("T1"));

			String Currency = CHF.showNull(request.getParameter("currency"));//只显示本位币

			String toSubjects = CHF.showNull(request.getParameter("toSubjects"));//显示对方科目

			String Begin = CHF.showNull(request.getParameter("Begin"));
			String End = CHF.showNull(request.getParameter("End"));

			String SubjectID = CHF.showNull(request.getParameter("SubjectID"));

			String acc = T1+End.substring(0, 4);

			userSession.setCurChoiceAccPackageId(acc);
			if(!"".equals(Begin)){
				userSession.setCurChoiceBeginYear(Begin.substring(0, 4));
				userSession.setCurChoiceBeginMonth(Begin.substring(5,7));
			}
			if(!"".equals(End)){
				userSession.setCurChoiceEndYear(End.substring(0, 4));
				userSession.setCurChoiceEndMonth(End.substring(5,7));
			}

			conn = new DBConnect().getConnect(T1);
			int result = new SubjectEntry(conn).SubjectProperty(acc,SubjectID);

			if("1".equals(Currency)){
				result = 0;
			}

			/*
			0:非叶子与无外币和无数量的叶子
			1:叶子有外币无数量
			2:叶子无外币有数量
			3:叶子有外币有数量
			*/

			switch(result){
				case 0://bonelist
					request.getRequestDispatcher("subjectentry.do?method=vonelist").forward(request, response);
//					response.sendRedirect("subjectentry.do?method=vonelist&Currency="+Currency+"&TabName="+TabName+"&T1="+T1+"&SubjectID="+SubjectID+"&toSubjects="+toSubjects);
					break;
				case 1://btwolist
					request.getRequestDispatcher("subjectentry.do?method=vtwolist").forward(request, response);
//					response.sendRedirect("subjectentry.do?method=vtwolist&TabName="+TabName+"&T1="+T1+"&SubjectID="+SubjectID+"&toSubjects="+toSubjects);
					break;
				case 2://bthreelist
					request.getRequestDispatcher("subjectentry.do?method=vthreelist").forward(request, response);
//					response.sendRedirect("subjectentry.do?method=vthreelist&TabName="+TabName+"&T1="+T1+"&SubjectID="+SubjectID+"&toSubjects="+toSubjects);
					break;
				case 3://bfourlist
					request.getRequestDispatcher("subjectentry.do?method=vfourlist").forward(request, response);
//					response.sendRedirect("subjectentry.do?method=vfourlist&TabName="+TabName+"&T1="+T1+"&SubjectID="+SubjectID+"&toSubjects="+toSubjects);
					break;
			}

		}catch (Exception e) {
			e.printStackTrace();
			 throw e;
		} finally {
			DbUtil.close(conn);
		}
		return null;
	}

	public ModelAndView vonelist(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		ModelAndView modelAndView = new ModelAndView(_strBList);
		Connection conn=null;
		try {
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");

			ASFuntion CHF=new ASFuntion();
			String T1 = CHF.showNull(request.getParameter("T1"));
			String TabName = CHF.showNull(request.getParameter("TabName"));
			String SubjectID = CHF.showNull(request.getParameter("SubjectID"));

			String acc = CHF.showNull(request.getParameter("AccPackageID"));
			if("".equals(acc)){
				acc = CHF.showNull(userSession.getCurChoiceAccPackageId());
				if("".equals(acc)){
					acc = CHF.showNull(userSession.getCurAccPackageId());
				}
			}

			String user=CHF.showNull(userSession.getUserId());
			String projectid=CHF.showNull(userSession.getCurProjectId());
			if("".equals(projectid)) projectid = "-1";

			String toSubjects = CHF.showNull(request.getParameter("toSubjects"));//显示对方科目
			String sqlwhere = "";
			if(!toSubjects.equals("1")){
				sqlwhere = " and  1=2 ";
			}
			
			String Currency = CHF.showNull(request.getParameter("currency"));

			String Begin = CHF.showNull(request.getParameter("Begin"));
			String End = CHF.showNull(request.getParameter("End"));

			conn = new DBConnect().getConnect(T1);
			SubjectEntry subjectEntry = new SubjectEntry(conn);

			if(!subjectEntry.ExistsTable(TabName)){
				int result = subjectEntry.LowSubjectProperty(acc,SubjectID);
				modelAndView.addObject("result", new Integer(result));

				subjectEntry.CreateTable(TabName);
				subjectEntry.DataToTable( TabName,  user,  projectid, T1,  SubjectID,  Begin, End);

			}

			String sTable = "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tr><td>科目名称：<font color=\"blue\">"+subjectEntry.getSubFullName(acc,SubjectID)+"</font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;期间：<font color=\"blue\">"+Begin+"－"+End+"</font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;本位币名称：<font color=\"blue\">"+new ASTextKey(conn).getACurrRate(acc)+"</font></td></tr></table>";

			modelAndView.addObject("SubjectID", SubjectID);
			modelAndView.addObject("TabName", TabName);
			modelAndView.addObject("T1", T1);
			modelAndView.addObject("sTable", sTable);

			modelAndView.addObject("Currency", Currency);

			if(!"".equals(SubjectID)){
				DataGridProperty pp = new DataGridProperty();

				pp.setTableID("SubjectEntry"+subjectEntry.getRandom());
				pp.setCustomerId(T1);
				pp.setPageSize_CH(100);
				pp.setWhichFieldIsValue(4);

				String sql = "select id,vchdate as vchdate1,substring(a.vchdate,6,2) vchmonth1 ,a.autoid,a.voucherid," +
						"case vchmonth when 1 then y.vchid else 0 end p1," +
						"case vchmonth when 1 then z.vchid else 0 end p2," +
						"case vchmonth when 1 then t.vchid else 0 end p3," +
						"case vchmonth when 0 then a.vchdate else '' end vchdate," +
						"a.typeid,a.oldvoucherid,replace(substring(a.subjects,2),',','<br>') as subjects,a.summary," +
						"case vchmonth when 0 then '' else a.debit end debit," +
						"case vchmonth when 0 then '' else a.credit end credit," +
						"case when dateremain>0 then '借' when dateremain<0 then '贷' else '平' end rec," +
						"ABS(dateremain) dateremain,subjectnames " +
//						"from (select * from `"+TabName+"` order by id ${LIMIT} ) a " +
						"from (select *,group_concat(distinct b.subjectfullname) subjectnames   from (select * from `"+TabName+"` where 1=1  order by id ${LIMIT}) a left join (select * from c_accpkgsubject where  AccPackageID="+acc+" "+sqlwhere+" )   b  on 1=1 and a.subjects like concat('%,',b.subjectid,',%')  group by a.id  ) a " +
						"left join (select distinct vchid from z_voucherspotcheck  where projectid='" +projectid + "' and createor='" + user +"'   ) y on a.voucherid=y.vchid  " +
						"left join (select distinct vchid from z_question  where projectid='" +projectid + "' and createor='" + user +"'   )  z on a.voucherid=z.vchid  " +
						"left join (select distinct vchid from z_taxcheck  where projectid='" +projectid + "' and createor='" + user +"'   )  t on a.autoid=t.vchid  where 1=1 ";

				
				String html = CHF.showNull(request.getParameter("html"));	//用于标志要不要用setTdtoValue　1为要　
				if("1".equals(html)){
					pp.setTdtoValue("<a href='St://${value}'>${tdvalue}</a>");	
				}
				
				pp.setOrderBy_CH("id");
				pp.setDirection("asc");
				pp.setCancelOrderby(true);
//				pp.setCancelPage(true);

				pp.setTrActionProperty(true);
				pp.setTrAction("  AccPackageID='"+acc+"' vchdate='${vchdate1}' voucherid='${voucherid}' style='cursor:hand;' onDBLclick='goSort();'");
				String setColumnWidth = "";
				if(acc.equals(userSession.getCurAccPackageId()) && userSession.getCurProjectId()!=null && !"1".equals(html)){
					pp.addColumn("抽","p1",FormatType.showTakeOut );
//					pp.addColumn("疑","p2",FormatType.showDoubt );
					setColumnWidth = "4,";
					ProjectService projectService = new ProjectService(conn);
					if("税务审计".equals(projectService.getProjectById(projectid).getAuditPara())){
						pp.addColumn("税", "p3", "showTaskTax");
					}
				}

				pp.addColumn("凭证日期", "vchdate","showCenter");
				pp.addColumn("字", "typeid","showCenter");
				pp.addColumn("号", "oldvoucherid","showCenter");

				setColumnWidth += "8,8,5";
				if("1".equals(toSubjects)){
//					pp.addColumn("对方科目", "subjects",null,"com.matech.audit.work.subjectentry.BatchValueProcess",null);
					pp.addColumn("对方科目", "subjectnames");
					setColumnWidth += ",15";
				}
				setColumnWidth += ",25";
				pp.setColumnWidth(setColumnWidth);
				
				pp.addColumn("摘要", "summary");
//				pp.addColumn("摘要", "summary",null,null,"<td style=\"word-break: keep-all;\">${value}</td>");
				pp.addColumn("借方发生额", "debit","showMoney");
				pp.addColumn("贷方发生额", "credit","showMoney");
				pp.addColumn("方向", "rec","showCenter");
				pp.addColumn("余　额", "dateremain","showMoney");


				pp.setLimitByOwnEnable(true);
				pp.setCountsql("select count(*) as datagrid_count from `" + TabName + "`");
				pp.setEnableCountTr(false);
				pp.setFixedHeader(true) ;
				pp.setSQL(sql);
				request.getSession().setAttribute(DataGrid.sessionPre+pp.getTableID(),pp);
				modelAndView.addObject("html", html);
				modelAndView.addObject("DataGrid", pp.getTableID());
			}



		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		return modelAndView;
	}

	public ModelAndView vtwolist(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		ModelAndView modelAndView = new ModelAndView(_strBList);
		Connection conn=null;
		try {
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");

			ASFuntion CHF=new ASFuntion();
			String T1 = CHF.showNull(request.getParameter("T1"));
			String TabName = CHF.showNull(request.getParameter("TabName"));
			String SubjectID = CHF.showNull(request.getParameter("SubjectID"));

			String acc = CHF.showNull(request.getParameter("AccPackageID"));
			if("".equals(acc)){
				acc = CHF.showNull(userSession.getCurChoiceAccPackageId());
				if("".equals(acc)){
					acc = CHF.showNull(userSession.getCurAccPackageId());
				}
			}

			String user=CHF.showNull(userSession.getUserId());
			String projectid=CHF.showNull(userSession.getCurProjectId());
			if("".equals(projectid)) projectid = "-1";

			String toSubjects = CHF.showNull(request.getParameter("toSubjects"));//显示对方科目
			String sqlwhere = "";
			if(!toSubjects.equals("1")){
				sqlwhere = " and  1=2 ";
			}
			String Begin = CHF.showNull(request.getParameter("Begin"));
			String End = CHF.showNull(request.getParameter("End"));

			conn = new DBConnect().getConnect(T1);
			SubjectEntry subjectEntry = new SubjectEntry(conn);

			Map map = subjectEntry.SubjectCurrency(acc,SubjectID);
			if(!subjectEntry.ExistsTable(TabName)){
				subjectEntry.CreateTable(TabName,map);
				subjectEntry.DataToTable(map,TabName,user,projectid,T1,SubjectID,Begin,End,"CurrValue");
			}

			String sTable = "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tr><td>科目名称：<font color=\"blue\">"+subjectEntry.getSubFullName(acc,SubjectID)+"</font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;期间：<font color=\"blue\">"+Begin+"－"+End+"</font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;本位币名称：<font color=\"blue\">"+new ASTextKey(conn).getACurrRate(acc)+"</font></td></tr></table>";

			modelAndView.addObject("SubjectID", SubjectID);
			modelAndView.addObject("TabName", TabName);
			modelAndView.addObject("T1", T1);
			modelAndView.addObject("sTable", sTable);

			/**
			 * DataGrid
			 */
			if(!"".equals(SubjectID)){
				DataGridProperty pp = new DataGridProperty();
				pp.setTableID("SubjectEntry"+subjectEntry.getRandom());
				pp.setCustomerId(T1);
				pp.setPageSize_CH(100);
				pp.setWhichFieldIsValue(4);
				pp.setOrderBy_CH("id");
				pp.setDirection("asc");
				pp.setCancelOrderby(true);

				String html = CHF.showNull(request.getParameter("html"));	//用于标志要不要用setTdtoValue　1为要　
				if("1".equals(html)){
					pp.setTdtoValue("<a href='St://${value}'>${tdvalue}</a>");	
				}
				
				pp.setTrActionProperty(true);
				pp.setTrAction("  AccPackageID='"+acc+"' vchdate='${vchdate1}' voucherid='${voucherid}' style='cursor:hand;' onDBLclick='goSort();'");

				String TableHead = "",TableHead1 = "";
				String sql = "";

				TableHead = "凭证日期,字,号,";

				sql = "select id,vchdate as vchdate1,substring(a.vchdate,6,2) vchmonth1, autoid,a.voucherid," +
					"case vchmonth when 1 then y.vchid else 0 end p1," +
					"case vchmonth when 1 then z.vchid else 0 end p2," +
					"case vchmonth when 1 then t.vchid else 0 end p3," +
					"case vchmonth when 0 then a.vchdate else '' end vchdate," +
					"a.typeid,a.oldvoucherid,replace(substring(a.subjects,2),',','<br>') as subjects,a.summary," ;
				String setColumnWidth = "";
				if(acc.equals(userSession.getCurAccPackageId()) && userSession.getCurProjectId()!=null && !"1".equals(html)){
//					TableHead = "抽,凭证日期,字,号,";
					TableHead1 = "抽,";
					pp.addColumn("抽","p1",FormatType.showTakeOut );
//					pp.addColumn("疑","p2",FormatType.showDoubt );
					setColumnWidth = "4,";
					ProjectService projectService = new ProjectService(conn);
					if("税务审计".equals(projectService.getProjectById(projectid).getAuditPara())){
						pp.addColumn("税", "p3", "showTaskTax");
//						TableHead = "抽,疑,税,凭证日期,字,号,";
						TableHead1 = "抽,税,";
					}
				}


				pp.addColumn("凭证日期", "vchdate","showCenter");
				pp.addColumn("字", "typeid","showCenter");
				pp.addColumn("号", "oldvoucherid","showCenter");

				setColumnWidth += "8,8,5";
				if("1".equals(toSubjects)){
//					pp.addColumn("对方科目", "subjects",null,"com.matech.audit.work.subjectentry.BatchValueProcess",null);
					pp.addColumn("对方科目", "subjectnames");
					setColumnWidth += ",15";
				}
				setColumnWidth += ",25";
				pp.setColumnWidth(setColumnWidth);
				
				TableHead += "摘要,";

				pp.addColumn("摘要", "summary");
//				pp.addColumn("摘要", "summary",null,null,"<td style=\"word-break: keep-all;\">${value}</td>");

				Set coll = map.keySet();
				int ii =1;

				String sqlHead1 = "";
				String sqlHead2 = "";
				String sqlHead3 = "";

				for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
					String key = (String) iter.next();
					String value = (String) map.get(key);
					sql += "case vchmonth when 0 then '' else a.debitrate"+ii+" end debitrate"+ii+",case vchmonth when 0 then '' else a.debit"+key+" end  debit"+key+",";
					sqlHead1 += "汇率,"+value+",";

					pp.addColumn("汇率", "debitrate"+ii,"showMoney");
					pp.addColumn(value, "debit"+key,"showMoney");
					ii ++;

				}
				sql += "case vchmonth when 0 then '' else a.debit end debit,";
				sqlHead1 += "借方本位币";
				TableHead += "借方{"+sqlHead1+"},";

				pp.addColumn("借方本位币", "debit","showMoney");

				ii=1;
				for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
					String key = (String) iter.next();
					String value = (String) map.get(key);
					sql += "case vchmonth when 0 then '' else a.creditrate"+ii+" end creditrate"+ii+",case vchmonth when 0 then '' else a.credit"+key+" end credit"+key+",";
					sqlHead2 += "汇率,"+value+",";

					pp.addColumn("汇率", "creditrate"+ii,"showMoney");
					pp.addColumn(value, "credit"+key,"showMoney");
					ii ++;

				}
				sql += "case vchmonth when 0 then '' else a.credit end credit,case when dateremain>0 then '借' when dateremain<0 then '贷' else '平' end rec,";
				sqlHead2 += "贷方本位币";
				TableHead += "贷方{"+sqlHead2+"},方向,";

				pp.addColumn("贷方本位币", "credit","showMoney");
				pp.addColumn("方向", "rec","showCenter");

				ii=1;
				for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
					String key = (String) iter.next();
					String value = (String) map.get(key);
//					sql += "abs(dRemain"+key+") dRemain"+key+",";
					sql += "if(dateremain<0,(-1)*dRemain"+key+",dRemain"+key+") as dRemain"+key+",";
					sqlHead3 += value+",";

					pp.addColumn(value, "dRemain"+key,"showMoney");
					ii ++;
				}
				sql +="ABS(dateRemain) dateRemain,subjectnames  " +
//				"from (select * from `"+TabName+"` order by id ${LIMIT} ) a " +
				"from (select *,group_concat(distinct b.subjectfullname) subjectnames   from (select * from `"+TabName+"` where 1=1  order by id ${LIMIT}) a left join (select * from c_accpkgsubject where  AccPackageID="+acc+" "+sqlwhere+" )   b  on 1=1 and a.subjects like concat('%,',b.subjectid,',%')  group by a.id  ) a " +

				"left join (select distinct vchid from z_voucherspotcheck  where projectid='" +projectid + "' and createor='" + user +"'   ) y on a.voucherid=y.vchid  " +
				"left join (select distinct vchid from z_question  where projectid='" +projectid + "' and createor='" + user +"'   )  z on a.voucherid=z.vchid  " +
				"left join (select distinct vchid from z_taxcheck  where projectid='" +projectid + "' and createor='" + user +"'   )  t on a.autoid=t.vchid  where 1=1 ";

				sqlHead3 +="本位币余额";
				TableHead +="余额{"+sqlHead3+"}";

				pp.addColumn("本位币余额", "dateRemain","showMoney");

				pp.setTableHead(TableHead1 + TableHead);

				pp.setLimitByOwnEnable(true);
				pp.setCountsql("select count(*) as datagrid_count from `" + TabName + "`");
				pp.setEnableCountTr(false);
				pp.setFixedHeader(true) ;
				pp.setSQL(sql);
				request.getSession().setAttribute(DataGrid.sessionPre+pp.getTableID(),pp);
				modelAndView.addObject("html", html);
				modelAndView.addObject("DataGrid", pp.getTableID());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		return modelAndView;
	}

	public ModelAndView vthreelist(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		ModelAndView modelAndView = new ModelAndView(_strBList);
		Connection conn=null;
		try {
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");

			ASFuntion CHF=new ASFuntion();
			String T1 = CHF.showNull(request.getParameter("T1"));
			String TabName = CHF.showNull(request.getParameter("TabName"));
			String SubjectID = CHF.showNull(request.getParameter("SubjectID"));

			String acc = CHF.showNull(request.getParameter("AccPackageID"));
			if("".equals(acc)){
				acc = CHF.showNull(userSession.getCurChoiceAccPackageId());
				if("".equals(acc)){
					acc = CHF.showNull(userSession.getCurAccPackageId());
				}
			}

			String user=CHF.showNull(userSession.getUserId());
			String projectid=CHF.showNull(userSession.getCurProjectId());
			if("".equals(projectid)) projectid = "-1";

			String Begin = CHF.showNull(request.getParameter("Begin"));
			String End = CHF.showNull(request.getParameter("End"));

			String toSubjects = CHF.showNull(request.getParameter("toSubjects"));//显示对方科目
			String sqlwhere = "";
			if(!toSubjects.equals("1")){
				sqlwhere = " and  1=2 ";
			}
			
			conn = new DBConnect().getConnect(T1);
			SubjectEntry subjectEntry = new SubjectEntry(conn);

			Map map = subjectEntry.SubjectUnitName(acc,SubjectID);
			if(!subjectEntry.ExistsTable(TabName)){
				subjectEntry.CreateTable(TabName,map);
				subjectEntry.DataToTable(map,TabName,user,projectid,T1,SubjectID,Begin,End,"Quantity");
			}

			String sTable = "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tr><td>科目名称：<font color=\"blue\">"+subjectEntry.getSubFullName(acc,SubjectID)+"</font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;期间：<font color=\"blue\">"+Begin+"－"+End+"</font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;本位币名称：<font color=\"blue\">"+new ASTextKey(conn).getACurrRate(acc)+"</font></td></tr></table>";

			modelAndView.addObject("SubjectID", SubjectID);
			modelAndView.addObject("TabName", TabName);
			modelAndView.addObject("T1", T1);
			modelAndView.addObject("sTable", sTable);
			/**
			 * DataGrid
			 */
			if(!"".equals(SubjectID)){
				DataGridProperty pp = new DataGridProperty();
				
				String html = CHF.showNull(request.getParameter("html"));	//用于标志要不要用setTdtoValue　1为要　
				if("1".equals(html)){
					pp.setTdtoValue("<a href='St://${value}'>${tdvalue}</a>");	
				}
				
				pp.setTableID("SubjectEntry"+subjectEntry.getRandom());
				pp.setCustomerId(T1);
				pp.setPageSize_CH(100);
				pp.setWhichFieldIsValue(4);
				pp.setOrderBy_CH("id");
				pp.setDirection("asc");
				pp.setCancelOrderby(true);

				pp.setTrActionProperty(true);
				pp.setTrAction("  AccPackageID='"+acc+"' vchdate='${vchdate1}' voucherid='${voucherid}' style='cursor:hand;' onDBLclick='goSort();'");

				String TableHead = "",TableHead1 = "";
				String sql = "";

				TableHead = "凭证日期,字,号,";

				sql = "select id,vchdate as vchdate1,substring(a.vchdate,6,2) vchmonth1,autoid,a.voucherid," +
					"case vchmonth when 1 then y.vchid else 0 end p1," +
					"case vchmonth when 1 then z.vchid else 0 end p2," +
					"case vchmonth when 1 then t.vchid else 0 end p3," +
					"case vchmonth when 0 then a.vchdate else '' end vchdate," +
					"a.typeid,a.oldvoucherid,replace(substring(a.subjects,2),',','<br>') as subjects,a.summary," ;
				String setColumnWidth = "";
				if(acc.equals(userSession.getCurAccPackageId()) && userSession.getCurProjectId()!=null && !"1".equals(html)){
//					TableHead = "抽,凭证日期,字,号,";
					TableHead1 = "抽,";
					pp.addColumn("抽","p1",FormatType.showTakeOut );
//					pp.addColumn("疑","p2",FormatType.showDoubt );
					setColumnWidth = "4,";
					ProjectService projectService = new ProjectService(conn);
					if("税务审计".equals(projectService.getProjectById(projectid).getAuditPara())){
						pp.addColumn("税", "p3", "showTaskTax");
//						TableHead = "抽,疑,税,凭证日期,字,号,";
						TableHead1 = "抽,税,";
					}
				}


				pp.addColumn("凭证日期", "vchdate","showCenter");
				pp.addColumn("字", "typeid","showCenter");
				pp.addColumn("号", "oldvoucherid","showCenter");

				setColumnWidth += "8,8,5";
				if("1".equals(toSubjects)){
//					pp.addColumn("对方科目", "subjects",null,"com.matech.audit.work.subjectentry.BatchValueProcess",null);
					pp.addColumn("对方科目", "subjectnames");
					setColumnWidth += ",15";
				}
				setColumnWidth += ",25";
				pp.setColumnWidth(setColumnWidth);
				
				TableHead += "摘要,";

				pp.addColumn("摘要", "summary");
//				pp.addColumn("摘要", "summary",null,null,"<td style=\"word-break: keep-all;\">${value}</td>");

				Set coll = map.keySet();
				int ii =1;

				String sqlHead1 = "";
				String sqlHead2 = "";
				String sqlHead3 = "";

				for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
					String key = (String) iter.next();
					String value = (String) map.get(key);
					sql += "case vchmonth when 0 then '' else a.debitrate"+ii+" end debitrate"+ii+",case vchmonth when 0 then '' else a.debit"+key+" end  debit"+key+",";
					sqlHead1 += "单价,"+value+",";

					pp.addColumn("单价", "debitrate"+ii,"showMoney");
					pp.addColumn(value, "debit"+key,"showMoney");
					ii ++;

				}
				sql += "case vchmonth when 0 then '' else a.debit end debit,";
				sqlHead1 += "借方本位币";
				TableHead += "借方{"+sqlHead1+"},";

				pp.addColumn("借方本位币", "debit","showMoney");

				ii=1;
				for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
					String key = (String) iter.next();
					String value = (String) map.get(key);
					sql += "case vchmonth when 0 then '' else a.creditrate"+ii+" end creditrate"+ii+",case vchmonth when 0 then '' else a.credit"+key+" end credit"+key+",";
					sqlHead2 += "单价,"+value+",";

					pp.addColumn("单价", "creditrate"+ii,"showMoney");
					pp.addColumn(value, "credit"+key,"showMoney");
					ii ++;

				}
				sql += "case vchmonth when 0 then '' else a.credit end credit,case when dateremain>0 then '借' when dateremain<0 then '贷' else '平' end rec,";
				sqlHead2 += "贷方本位币";
				TableHead += "贷方{"+sqlHead2+"},方向,";

				pp.addColumn("贷方本位币", "credit","showMoney");
				pp.addColumn("方向", "rec","showCenter");

				ii=1;
				for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
					String key = (String) iter.next();
					String value = (String) map.get(key);
//					sql += "abs(dRemain"+key+") dRemain"+key+",";
					sql += "if(dateremain<0,(-1)*dRemain"+key+",dRemain"+key+") as dRemain"+key+",";
					sqlHead3 += value+",";

					pp.addColumn(value, "dRemain"+key,"showMoney");
					ii ++;
				}
				sql +="ABS(dateRemain) dateRemain,subjectnames  " +
//				"from (select * from `"+TabName+"` order by id ${LIMIT} ) a " +
				"from (select *,group_concat(distinct b.subjectfullname) subjectnames   from (select * from `"+TabName+"` where 1=1  order by id ${LIMIT}) a left join (select * from c_accpkgsubject where  AccPackageID="+acc+" "+sqlwhere+" )   b  on 1=1 and a.subjects like concat('%,',b.subjectid,',%')  group by a.id  ) a " +
				"left join (select distinct vchid from z_voucherspotcheck  where projectid='" +projectid + "' and createor='" + user +"'   ) y on a.voucherid=y.vchid  " +
				"left join (select distinct vchid from z_question  where projectid='" +projectid + "' and createor='" + user +"'   )  z on a.voucherid=z.vchid  " +
				"left join (select distinct vchid from z_taxcheck  where projectid='" +projectid + "' and createor='" + user +"'   )  t on a.autoid=t.vchid  where 1=1 ";

				sqlHead3 +="本位币余额";
				TableHead +="余额{"+sqlHead3+"}";

				pp.addColumn("本位币余额", "dateRemain","showMoney");

				pp.setTableHead(TableHead1 + TableHead);
				pp.setFixedHeader(true) ;
				pp.setLimitByOwnEnable(true);
				pp.setCountsql("select count(*) as datagrid_count from `" + TabName + "`");
				pp.setEnableCountTr(false);

				pp.setSQL(sql);
				request.getSession().setAttribute(DataGrid.sessionPre+pp.getTableID(),pp);
				modelAndView.addObject("html", html);
				modelAndView.addObject("DataGrid", pp.getTableID());
			}


		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		return modelAndView;
	}

	public ModelAndView vfourlist(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		ModelAndView modelAndView = new ModelAndView(_strBList);
		Connection conn=null;
		try {
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");

			ASFuntion CHF=new ASFuntion();
			String T1 = CHF.showNull(request.getParameter("T1"));
			String TabName = CHF.showNull(request.getParameter("TabName"));
			String SubjectID = CHF.showNull(request.getParameter("SubjectID"));

			String acc = CHF.showNull(request.getParameter("AccPackageID"));
			if("".equals(acc)){
				acc = CHF.showNull(userSession.getCurChoiceAccPackageId());
				if("".equals(acc)){
					acc = CHF.showNull(userSession.getCurAccPackageId());
				}
			}

			String user=CHF.showNull(userSession.getUserId());
			String projectid=CHF.showNull(userSession.getCurProjectId());
			if("".equals(projectid)) projectid = "-1";

			String toSubjects = CHF.showNull(request.getParameter("toSubjects"));//显示对方科目
			String sqlwhere = "";
			if(!toSubjects.equals("1")){
				sqlwhere = " and  1=2 ";
			}
			String Begin = CHF.showNull(request.getParameter("Begin"));
			String End = CHF.showNull(request.getParameter("End"));

			conn = new DBConnect().getConnect(T1);
			SubjectEntry subjectEntry = new SubjectEntry(conn);

			Map Cmap = subjectEntry.SubjectCurrency(acc,SubjectID); //汇率
			Map Umap = subjectEntry.SubjectUnitName(acc,SubjectID); //单价

			if(!subjectEntry.ExistsTable(TabName)){
				subjectEntry.CreateTable(TabName,Cmap,Umap);
				subjectEntry.DataToTable(Cmap,Umap,TabName,user,projectid,T1,SubjectID,Begin,End);
			}

			String sTable = "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tr><td>科目名称：<font color=\"blue\">"+subjectEntry.getSubFullName(acc,SubjectID)+"</font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;期间：<font color=\"blue\">"+Begin+"－"+End+"</font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;本位币名称：<font color=\"blue\">"+new ASTextKey(conn).getACurrRate(acc)+"</font></td></tr></table>";

			modelAndView.addObject("SubjectID", SubjectID);
			modelAndView.addObject("TabName", TabName);
			modelAndView.addObject("T1", T1);
			modelAndView.addObject("sTable", sTable);

			/**
			 * DataGrid
			 */
			if(!"".equals(SubjectID)){
				DataGridProperty pp = new DataGridProperty();
				String html = CHF.showNull(request.getParameter("html"));	//用于标志要不要用setTdtoValue　1为要　
				if("1".equals(html)){
					pp.setTdtoValue("<a href='St://${value}'>${tdvalue}</a>");	
				}
				
				pp.setTableID("SubjectEntry"+subjectEntry.getRandom());
				pp.setCustomerId(T1);
				pp.setPageSize_CH(100);
				pp.setWhichFieldIsValue(4);
				pp.setOrderBy_CH("id");
				pp.setDirection("asc");
				pp.setCancelOrderby(true);

				pp.setTrActionProperty(true);
				pp.setTrAction("  AccPackageID='"+acc+"' vchdate='${vchdate1}' voucherid='${voucherid}' style='cursor:hand;' onDBLclick='goSort();'");

				String TableHead = "",TableHead1 = "";
				String sql = "";

				TableHead = "凭证日期,字,号,";

				sql = "select id,vchdate as vchdate1,substring(a.vchdate,6,2) vchmonth1,autoid,a.voucherid," +
				"case vchmonth when 1 then y.vchid else 0 end p1," +
				"case vchmonth when 1 then z.vchid else 0 end p2," +
				"case vchmonth when 1 then t.vchid else 0 end p3," +
				"case vchmonth when 0 then a.vchdate else '' end vchdate," +
				"a.typeid,a.oldvoucherid,replace(substring(a.subjects,2),',','<br>') as subjects,a.summary," ;
				String setColumnWidth = "";
				if(acc.equals(userSession.getCurAccPackageId()) && userSession.getCurProjectId()!=null && !"1".equals(html)){
//					TableHead = "抽,凭证日期,字,号,";
					TableHead1 = "抽,";
					pp.addColumn("抽","p1",FormatType.showTakeOut );
//					pp.addColumn("疑","p2",FormatType.showDoubt );
					setColumnWidth = "4,";
					ProjectService projectService = new ProjectService(conn);
					if("税务审计".equals(projectService.getProjectById(projectid).getAuditPara())){
						pp.addColumn("税", "p3", "showTaskTax");
//						TableHead = "抽,疑,税,凭证日期,字,号,";
						TableHead1 = "抽,税,";
					}
				}


				pp.addColumn("凭证日期", "vchdate","showCenter");
				pp.addColumn("字", "typeid","showCenter");
				pp.addColumn("号", "oldvoucherid","showCenter");

				setColumnWidth += "8,8,5";
				if("1".equals(toSubjects)){
//					pp.addColumn("对方科目", "subjects",null,"com.matech.audit.work.subjectentry.BatchValueProcess",null);
					pp.addColumn("对方科目", "subjectnames");
					setColumnWidth += ",15";
				}
				setColumnWidth += ",25";
				pp.setColumnWidth(setColumnWidth);
				
				TableHead += "摘要,";

				pp.addColumn("摘要", "summary");
//				pp.addColumn("摘要", "summary",null,null,"<td style=\"word-break: keep-all;\">${value}</td>");

				Set Ucoll = Umap.keySet();
				Set Ccoll = Cmap.keySet();
				int ii =1;

				String sqlHead1 = "";
				String sqlHead2 = "";
				String sqlHead3 = "";

				for (Iterator iter = Ucoll.iterator(); iter.hasNext(); ) {
					String key = (String) iter.next();
					String value = (String) Umap.get(key);
					sql += "case vchmonth when 0 then '' else a.debitPrice"+ii+" end debitPrice"+ii+",case vchmonth when 0 then '' else a.debit"+key+" end  debit"+key+",";
					sqlHead1 += "单价,"+value+",";

					pp.addColumn("单价", "debitPrice"+ii,"showMoney");
					pp.addColumn(value, "debit"+key,"showMoney");
					ii ++;
				}

				ii =1;
				for (Iterator iter = Ccoll.iterator(); iter.hasNext(); ) {
					String key = (String) iter.next();
					String value = (String) Cmap.get(key);
					sql += "case vchmonth when 0 then '' else a.debitrate"+ii+" end debitrate"+ii+",case vchmonth when 0 then '' else a.debit"+key+" end  debit"+key+",";
					sqlHead1 += "汇率,"+value+",";

					pp.addColumn("汇率", "debitrate"+ii,"showMoney");
					pp.addColumn(value, "debit"+key,"showMoney");
					ii ++;
				}
				sql += "case vchmonth when 0 then '' else a.debit end debit,";
				sqlHead1 += "借方本位币";
				TableHead += "借方{"+sqlHead1+"},";

				pp.addColumn("借方本位币", "debit","showMoney");

				ii=1;
				for (Iterator iter = Ucoll.iterator(); iter.hasNext(); ) {
					String key = (String) iter.next();
					String value = (String) Umap.get(key);
					sql += "case vchmonth when 0 then '' else a.creditPrice"+ii+" end creditPrice"+ii+",case vchmonth when 0 then '' else a.credit"+key+" end  credit"+key+",";
					sqlHead2 += "单价,"+value+",";

					pp.addColumn("单价", "creditPrice"+ii,"showMoney");
					pp.addColumn(value, "credit"+key,"showMoney");
					ii ++;
				}

				ii=1;
				for (Iterator iter = Ccoll.iterator(); iter.hasNext(); ) {
					String key = (String) iter.next();
					String value = (String) Cmap.get(key);
					sql += "case vchmonth when 0 then '' else a.creditrate"+ii+" end creditrate"+ii+",case vchmonth when 0 then '' else a.credit"+key+" end credit"+key+",";
					sqlHead2 += "汇率,"+value+",";

					pp.addColumn("汇率", "creditrate"+ii,"showMoney");
					pp.addColumn(value, "credit"+key,"showMoney");
					ii ++;

				}

				sql += "case vchmonth when 0 then '' else a.credit end credit,case when dateremain>0 then '借' when dateremain<0 then '贷' else '平' end rec,";
				sqlHead2 += "贷方本位币";
				TableHead += "贷方{"+sqlHead2+"},方向,";

				pp.addColumn("贷方本位币", "credit","showMoney");
				pp.addColumn("方向", "rec","showCenter");

				ii=1;
				for (Iterator iter = Ucoll.iterator(); iter.hasNext(); ) {
					String key = (String) iter.next();
					String value = (String) Umap.get(key);
//					sql += "abs(dRemain"+key+") dRemain"+key+",";
					sql += "if(dateremain<0,(-1)*dRemain"+key+",dRemain"+key+") as dRemain"+key+",";
					sqlHead3 += value+",";

					pp.addColumn(value, "dRemain"+key,"showMoney");
					ii ++;
				}

				ii=1;
				for (Iterator iter = Ccoll.iterator(); iter.hasNext(); ) {
					String key = (String) iter.next();
					String value = (String) Cmap.get(key);
//					sql += "abs(dRemain"+key+") dRemain"+key+",";
					sql += "if(dateremain<0,(-1)*dRemain"+key+",dRemain"+key+") as dRemain"+key+",";
					sqlHead3 += value+",";

					pp.addColumn(value, "dRemain"+key,"showMoney");
					ii ++;
				}

				sql +="ABS(dateRemain) dateRemain,subjectnames  " +
//				"from (select * from `"+TabName+"` order by id ${LIMIT} ) a " +
				"from (select *,group_concat(distinct b.subjectfullname) subjectnames   from (select * from `"+TabName+"` where 1=1  order by id ${LIMIT}) a left join (select * from c_accpkgsubject where  AccPackageID="+acc+" "+sqlwhere+" )   b  on 1=1 and a.subjects like concat('%,',b.subjectid,',%')  group by a.id  ) a " +
				"left join (select distinct vchid from z_voucherspotcheck  where projectid='" +projectid + "' and createor='" + user +"'   ) y on a.voucherid=y.vchid  " +
				"left join (select distinct vchid from z_question  where projectid='" +projectid + "' and createor='" + user +"'   )  z on a.voucherid=z.vchid  " +
				"left join (select distinct vchid from z_taxcheck  where projectid='" +projectid + "' and createor='" + user +"'   )  t on a.autoid=t.vchid  where 1=1 ";


				sqlHead3 +="本位币余额";
				TableHead +="余额{"+sqlHead3+"}";

				pp.addColumn("本位币余额", "dateRemain","showMoney");

				pp.setTableHead(TableHead1 + TableHead);

				pp.setLimitByOwnEnable(true);
				pp.setCountsql("select count(*) as datagrid_count from `" + TabName + "`");
				pp.setEnableCountTr(false);
				pp.setFixedHeader(true) ;
				pp.setSQL(sql);
				request.getSession().setAttribute(DataGrid.sessionPre+pp.getTableID(),pp);
				modelAndView.addObject("html", html);
				modelAndView.addObject("DataGrid", pp.getTableID());

			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		return modelAndView;
	}

	public ModelAndView vtext(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		Connection conn=null;
		HashMap mapResult = new HashMap();
		try {
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
			ASFuntion CHF=new ASFuntion();

			String user=CHF.showNull(userSession.getUserId());
			String projectid=CHF.showNull(userSession.getCurProjectId());
			if("".equals(projectid)) projectid = "-1";

			String DGrid =(String)CHF.showNull(request.getParameter("DataGrid"));

			String T1 =(String)CHF.showNull(request.getParameter("T1"));
			String SubjectID = (String)CHF.showNull(request.getParameter("SubjectID"));

			String Begin =(String)CHF.showNull(request.getParameter("Begin"));
			String End =(String)CHF.showNull(request.getParameter("End"));

			String TabName = (String)CHF.showNull(request.getParameter("TabName"));

			String acc = T1 + End.subSequence(0, 4);

			String txtType = (String)CHF.showNull(request.getParameter("txtType"));
			String txtBegin =(String)CHF.showNull(request.getParameter("txtBegin"));
			String txtEnd = (String)CHF.showNull(request.getParameter("txtEnd"));
			String txtSQL1 = "";
			String txtSQL2 = "";
			txtType = "".equals(txtType)?"0":txtType;

			String Currency = (String)CHF.showNull(request.getParameter("Currency"));

			String toSubjects = (String)CHF.showNull(request.getParameter("toSubjects"));

			switch(Integer.parseInt(txtType)){
			case 0:
				break;
			case 1:
			case 2:
				txtSQL1 = " limit "+txtBegin+","+txtEnd;
				break;
			case 3:
				txtSQL2 = " and vchmonth1>="+txtBegin+" and vchmonth1<="+txtEnd+" ";
				break;
			}

			conn = new DBConnect().getConnect(T1);
			SubjectEntry subjectEntry = new SubjectEntry(conn);

			int result = subjectEntry.SubjectProperty(acc,SubjectID);
			if("1".equals(Currency) ){
				result = 0;
			}

			Map map = subjectEntry.SubjectCurrency(acc,SubjectID);
			Map Umap = subjectEntry.SubjectUnitName(acc,SubjectID);


			ASTextKey tkey = new ASTextKey(conn);
			String TName = "";
			String ss = "";
			String full = subjectEntry.getSubFullName(acc,SubjectID);
			if(!"".equals(T1)) {
				TName = tkey.TextCustomerName(T1);
				ss = tkey.getACurrRate(acc);
			}

			DataGridProperty pp = (DataGridProperty)request.getSession().getAttribute(DataGrid.sessionPre+DGrid); //科目明细账查询

			PrintSetup printSetup = new PrintSetup(conn);
			printSetup.setStrTitles(new String[]{TName+" 科目明细账查询"});

			List[] lists = new List[1];
			lists[0] = new ArrayList();
			lists[0].add(0, "1");
			lists[0].add(1, "科目名称："+full +"    期间："+ Begin +"－"+ End +"     贷币单位："+ss);
			lists[0].add(2, "9");
			lists[0].add(3, null);

			printSetup.setPoms(lists);
			printSetup.setCharColumn(new String[]{"1`2`3`4`5`6"});
			String sRows = "";

			String [][] CTName = new String[1][2];
			String [][] ColName = new String[1][2];


			String sql = "select vchdate,typeid,oldvoucherid,";

			if("1".equals(toSubjects) ){
				sql += "replace(subjectnames,'<br>',','),";
			}

			sql += "summary,";

			switch(result){
				case 0:		//本位币
					if("1".equals(toSubjects)){
						printSetup.setStrChineseTitles(new String[]{"凭证日期`字`号`对方科目`摘要`借方发生额`贷方发生额`方向`余　额"});
						printSetup.setIColumnWidths(new int[]{13,6,6,16,30,25,25,4,25});
					}else{
						printSetup.setStrChineseTitles(new String[]{"凭证日期`字`号`摘要`借方发生额`贷方发生额`方向`余　额"});
						printSetup.setIColumnWidths(new int[]{13,6,6,46,25,25,4,25});
					}
					printSetup.setStrQuerySqls(new String[]{sql + "debit,credit,rec,dateremain from ("+pp.getFinishSQLDeleteLimit()+") a where 1=1 "+txtSQL2+" order by id "+txtSQL1 });
					
					sRows = "$2:$4";
					break;
				case 1:			//外币
					CTName[0][1] = new String();
					CTName[0][1] = ",";
					ColName[0][1] = new String();
					ColName[0][1]= ",";
					CTName[0][0] = new String();
					ColName[0][0] = new String();

					CTName[0][0] = "凭证日期,字,号,";
					ColName[0][0] = "凭证日期,字,号,";

					if("1".equals(toSubjects)){
						CTName[0][0] += "对方科目,";
						ColName[0][0] += "对方科目,";
					}
					CTName[0][0] += "摘要,";
					ColName[0][0] += "摘要,";


					Set coll = map.keySet();
					int ii =1;
					for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
						String key = (String) iter.next();
						String value = (String) map.get(key);
						sql += "debitrate"+ii+",debit"+key+",";
						CTName[0][0] += "借方,借方,";
						ColName[0][0] += "汇率,"+value+",";
						ii ++;
					}
					sql += "debit,";
					CTName[0][0] += "借方,";
					ColName[0][0] += "借方本位币,";

					ii=1;
					for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
						String key = (String) iter.next();
						String value = (String) map.get(key);
						sql += "creditrate"+ii+",credit"+key+",";
						CTName[0][0] += "贷方,贷方,";
						ColName[0][0] += "汇率,"+value+",";
						ii ++;
					}
					sql += "credit,rec,";
					CTName[0][0] += "贷方,方向,";
					ColName[0][0] += "贷方本位币,方向,";

					ii=1;
					for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
						String key = (String) iter.next();
						String value = (String) map.get(key);
						sql += "dRemain"+key+",";
						CTName[0][0] += "余额,";
						ColName[0][0] += value+",";
						ii ++;
					}
					sql += "dateRemain";
					CTName[0][0] += "余额";
					ColName[0][0] += "本位币余额";

					printSetup.setStrQuerySqls(new String[]{sql + " from ("+pp.getFinishSQLDeleteLimit()+") a where 1=1 "+txtSQL2+"  order by id "+txtSQL1 });
					printSetup.setHeaders(CTName, ColName);

					sRows="$2:$5";
					break;
				case 2:		//数量
					CTName[0][1] = ",";
					ColName[0][1]= ",";

					CTName[0][0] = "凭证日期,字,号,";
					ColName[0][0] = "凭证日期,字,号,";

					if("1".equals(toSubjects)){
						CTName[0][0] += "对方科目,";
						ColName[0][0] += "对方科目,";
					}
					CTName[0][0] += "摘要,";
					ColName[0][0] += "摘要,";

					coll = Umap.keySet();
					ii =1;
					for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
						String key = (String) iter.next();
						String value = (String) Umap.get(key);
						sql += "debitrate"+ii+",debit"+key+",";
						CTName[0][0] += "借方,借方,";
						ColName[0][0] += "单价,"+value+",";
						ii ++;
					}
					sql += "debit,";
					CTName[0][0] += "借方,";
					ColName[0][0] += "借方本位币,";

					ii=1;
					for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
						String key = (String) iter.next();
						String value = (String) Umap.get(key);
						sql += "creditrate"+ii+",credit"+key+",";
						CTName[0][0] += "贷方,贷方,";
						ColName[0][0] += "单价,"+value+",";
						ii ++;
					}
					sql += "credit,rec,";
					CTName[0][0] += "贷方,方向,";
					ColName[0][0] += "贷方本位币,方向,";

					ii=1;
					for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
						String key = (String) iter.next();
						String value = (String) Umap.get(key);
						sql += "dRemain"+key+",";
						CTName[0][0] += "余额,";
						ColName[0][0] += value+",";
						ii ++;
					}
					sql += "dateRemain";
					CTName[0][0] += "余额";
					ColName[0][0] += "本位币余额";

					printSetup.setStrQuerySqls(new String[]{sql + " from ("+pp.getFinishSQLDeleteLimit()+") a where 1=1 "+txtSQL2+"  order by id "+txtSQL1 });
					printSetup.setHeaders(CTName, ColName);

					sRows="$2:$5";
					break;
				case 3:		//外币与数量
					CTName[0][1] = ",";
					ColName[0][1]= ",";


					CTName[0][0] = "凭证日期,字,号,";
					ColName[0][0] = "凭证日期,字,号,";

					if("1".equals(toSubjects)){
						CTName[0][0] += "对方科目,";
						ColName[0][0] += "对方科目,";
					}
					CTName[0][0] += "摘要,";
					ColName[0][0] += "摘要,";


					Set Ucoll = Umap.keySet();
					Set Ccoll = map.keySet();

					ii =1;
					for (Iterator iter = Ucoll.iterator(); iter.hasNext(); ) {
						String key = (String) iter.next();
						String value = (String) Umap.get(key);
						sql += "debitPrice"+ii+",debit"+key+",";
						CTName[0][0] += "借方,借方,";
						ColName[0][0] += "单价,"+value+",";
						ii ++;
					}
					ii =1;
					for (Iterator iter = Ccoll.iterator(); iter.hasNext(); ) {
						String key = (String) iter.next();
						String value = (String) map.get(key);
						sql += "debitrate"+ii+",debit"+key+",";
						CTName[0][0] += "借方,借方,";
						ColName[0][0] += "汇率,"+value+",";
						ii ++;
					}
					sql += "debit,";
					CTName[0][0] += "借方,";
					ColName[0][0] += "借方本位币,";

					ii=1;
					for (Iterator iter = Ucoll.iterator(); iter.hasNext(); ) {
						String key = (String) iter.next();
						String value = (String) Umap.get(key);
						sql += "creditPrice"+ii+",credit"+key+",";
						CTName[0][0] += "贷方,贷方,";
						ColName[0][0] += "单价,"+value+",";
						ii ++;
					}
					ii=1;
					for (Iterator iter = Ccoll.iterator(); iter.hasNext(); ) {
						String key = (String) iter.next();
						String value = (String) map.get(key);
						sql += "creditrate"+ii+",credit"+key+",";
						CTName[0][0] += "贷方,贷方,";
						ColName[0][0] += "汇率,"+value+",";
						ii ++;
					}
					sql += "credit,rec,";
					CTName[0][0] += "贷方,方向,";
					ColName[0][0] += "贷方本位币,方向,";

					ii=1;
					for (Iterator iter = Ucoll.iterator(); iter.hasNext(); ) {
						String key = (String) iter.next();
						String value = (String) Umap.get(key);
						sql += "dRemain"+key+",";
						CTName[0][0] += "余额,";
						ColName[0][0] += value+",";
						ii ++;
					}
					ii=1;
					for (Iterator iter = Ccoll.iterator(); iter.hasNext(); ) {
						String key = (String) iter.next();
						String value = (String) map.get(key);
						sql += "dRemain"+key+",";
						CTName[0][0] += "余额,";
						ColName[0][0] += value+",";
						ii ++;
					}
					sql += "dateRemain";
					CTName[0][0] += "余额";
					ColName[0][0] += "本位币余额";

					printSetup.setStrQuerySqls(new String[]{sql + " from ("+pp.getFinishSQLDeleteLimit()+") a where 1=1 "+txtSQL2+"  order by id "+txtSQL1 });
					printSetup.setHeaders(CTName, ColName);

					sRows="$2:$5";
					break;

			}




			String filename = printSetup.getExcelFile();

			//vpage strPrintTitleRows
			mapResult.put("refresh","");

			mapResult.put("saveasfilename","科目明细账");
			mapResult.put("bVpage","false");
			mapResult.put("strPrintTitleRows",sRows);
			mapResult.put("filename", filename);

		} catch (Exception e) {
			Debug.print(Debug.iError, "科目明细账打印失败!", e);
            throw e;
		} finally {
			DbUtil.close(conn);
		}
		return new ModelAndView("/Excel/tempdata/PrintandSave.jsp",mapResult);
	}


	/**
	 * 按照模版打印
	 *
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ModelAndView printtemplate(HttpServletRequest req,
			HttpServletResponse res) throws Exception {
		String strResult = "CheckoutInfo/PrintModel.jsp?modelName=coeq_print";

		HashMap mapResult = new HashMap();

		HttpSession session = req.getSession();
		ASFuntion CHF=new ASFuntion();
		// 系统公共变量
		UserSession us = (UserSession) session.getAttribute("userSession");

		//检查是否登陆项目
		if(!new CommonSecurity(req,res).checkProjectLogin()) {
			return null;
		}

		String projectid = us.getCurProjectId();
		String accpackageid = us.getCurAccPackageId();
		String CustomerId =  us.getCurCustomerId();
		String AuditBeginYear = us.getCurChoiceBeginYear();
		String AuditEndYear = us.getCurChoiceEndYear();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = new DBConnect().getConnect(CustomerId);
			SubjectEntry subjectEntry = new SubjectEntry(conn);
			String subjectName = subjectEntry.getSubFullName(accpackageid,CHF.showNull(req.getParameter("SubjectID")));

			// 另存为底稿的参数
			StringBuffer pmSql = new StringBuffer();
			String sql = " select group_concat(\"'\",subjectid,\"'\") as gs "
						+" from c_account  "
						+"  where accpackageid ='"+accpackageid+"' "
						+" and submonth=1  "
						+" and (subjectfullname1='"+subjectName+"' or subjectfullname1 like '"+subjectName+"/%') ";
			String strSubjectid = "";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()) {
				strSubjectid = rs.getString(1);
			}

			pmSql.append(" select vchdate,tv,summary,subjectname,correspondingSubjectname,DebitValue,CrebitValue,'','','','','','','','',b.name as Createor,'' FROM (  \n");
			pmSql.append(" select a.entryvchdate vchdate,a.entrytypeid typeid,a.entryoldvoucherid oldvoucherid,concat(a.entrytypeid,conv(a.entryoldvoucherid,10,10)) as tv,a.entrysummary summary,\n");
			pmSql.append(" a.subjectid,a.entrysubjectid,a.entrysubjectfullname1 as subjectname,if(a.entryDirction=1,a.entryOccurValue,0.00) DebitValue,if(a.entryDirction=-1,a.entryOccurValue,0.00) CrebitValue, \n");
			pmSql.append(" substring(replace(b.allsubjectname,concat(',',a.entrysubjectfullname1),''),2,length(replace(b.allsubjectname,concat(',',a.entrysubjectfullname1),''))) as correspondingSubjectname, \n");
			pmSql.append(" a.entrydirction,a.entryoccurvalue,a.createor,a.entryQuantity,a.entrycurrency,a.entryvchdate as odvchdate,a.entrytypeid as odtypeid,a.entryoldvoucherid as odvoucherid,0 as orderid  \n");
			pmSql.append(" from z_voucherspotcheck a  \n");
			pmSql.append(" 	left join  \n");
			pmSql.append(" ( \n");
			pmSql.append(" 	select distinct a.vchid ,concat(',',group_concat(distinct entrysubjectfullname1)) as allsubjectname \n");
			pmSql.append(" from `z_voucherspotcheck` a  \n");
			pmSql.append(" where a.subjectid in ("+strSubjectid+")  \n");
			pmSql.append(" group by vchid   \n");
			pmSql.append(" ) b \n");
			pmSql.append(" on a.vchid = b.vchid \n");
			pmSql.append(" where a.entryaccpackageid>='"+CustomerId+AuditBeginYear+"' \n");
			pmSql.append(" and a.entryaccpackageid<='"+CustomerId+AuditEndYear+"' \n");
			pmSql.append(" and a.projectid="+projectid+"   \n");

			takeOutVoucher vs = new takeOutVoucher();
			vs.setCSubjectid(req.getParameter("SubjectID"));

			if (!vs.getCSubjectid().equals(""))
				pmSql.append(" and a.subjectid like '" + vs.getCSubjectid() + "%'  ");

//			pmSql.append("      group by a.autoid  \n");
			pmSql.append("     union  \n");
			pmSql.append("     select '','','','','','','','','','','','','','','','',b.entryvchdate as odvchdate,b.entrytypeid as odtypeid,b.entryoldvoucherid as odvoucherid,1 as orderid \n");
//			pmSql.append("     from c_voucher a \n");
//			pmSql.append("     inner join \n");
			pmSql.append("     from z_voucherspotcheck b \n");
//			pmSql.append("     on a.autoid=b.vchid \n");
//			pmSql.append("     where a.accpackageid=" + accpackageid + "   \n");
			pmSql.append("     where b.projectid=" + projectid + "  \n");
			pmSql.append("     and b.subjectid like '" + vs.getCSubjectid() + "%' \n");

			pmSql.append("  ) a   \n");
			pmSql.append("left join k_user b on a.Createor = b.id \n");
			pmSql.append("  where 1=1  \n");
			pmSql.append("    \n");

			// //外币条件
			// if(!accCurrency.equals(currency)){
			// if("数量帐".equals(currency)){
			// sql=sql+" and a.Unitname !='' ";
			// }else{
			// sql=sql+" and a.currency = '"+currency+"'";
			// }
			// }

			pmSql.append("  order by odvchdate,odtypeid,odvoucherid,orderid \n");

			System.out.println("print sql:::\n\n\n" + pmSql.toString());

			PrintModel pm = new PrintModel();

			pm.setExcelTemplateFileName("抽凭检查表.xls");
			pm.setStrQuerySqls(new String[] { pmSql.toString() });
			pm.setCharColumn(new String[] { "2`3`4`5" });

			pm.putVarMap("depaname", us.getCurCustomerName());
			pm.putVarMap("auditpeople", us.getUserName());
			pm.putVarMap("audittime", CHF.getCurrentDate());

			if(!"".equals(subjectName) && subjectName!=null) {
				subjectName = "("+subjectName+")";
			} else {
				subjectName = "";
			}
			pm.putVarMap("subjectName", subjectName);

			pm.setVertical(false);
			pm.putVarMap("particular",getManuScriptAcc(vs, accpackageid, projectid));

			session.setAttribute("coeq_print", pm);

			strResult += "&departid=" + accpackageid.substring(0, 6);
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(conn);
		}
		return new ModelAndView(strResult, mapResult);
	}

	private String getManuScriptAcc(takeOutVoucher vs, String apkID,
			String prjID) {

		StringBuffer sb = new StringBuffer();
		String[][] data = null;
		StringBuffer result = new StringBuffer();

		sb
				.append("  select a.subjectid,a.subjectname,a.debitocc as cdebitocc,b.debitocc,format((a.debitocc/b.debitocc)*100,2) as debitrate ,a.creditOcc as ccreditOcc,b.creditocc,format((a.creditOcc/b.creditocc)*100,2) as creditrate FROM  \n");
		sb.append("  (  \n");
		sb.append("      /*  抽凭按一级科目汇总  */  \n");
		sb.append("      select   \n");
		sb.append("        c.subjectid,  \n");
		sb.append("        c.subjectname,  \n");
		sb
				.append("        SUM(IF(dirction=1,a.OccurValue,0.00)) as debitOcc,  \n");
		sb
				.append("        SUM(IF(dirction=-1,a.OccurValue,0.00)) as creditOcc  \n");
		sb.append("      from   \n");
		sb.append("        c_subjectentry a  \n");
		sb.append("      inner join  \n");
		sb.append("        z_voucherspotcheck b  \n");
		sb.append("      on a.voucherid=b.vchid  \n");
		sb.append("      inner join  \n");
		sb.append("      (  \n");
		sb.append("         /**/  \n");
		sb
				.append("          select distinct LENGTH(subjectid) as lt from c_accpkgsubject   \n");
		sb.append("          where accpackageid='" + apkID + "'  \n");
		sb.append("          and Level0=1  \n");
		sb.append("        \n");
		sb.append("      ) d  \n");
		sb.append("      inner join  \n");
		sb.append("        c_accpkgsubject c  \n");
		sb
				.append("      on a.accpackageid=c.accpackageid and substring(a.subjectid,1,d.lt) = c.subjectid  \n");
		sb.append("      where a.accpackageid='" + apkID + "'    \n");
		sb.append("      and b.projectid='" + prjID + "'  \n");
		sb.append("      and c.accpackageid='" + apkID + "'  \n");
		sb.append("      and c.Level0=1  \n");

		if (!vs.getSubject().equals(""))
			sb.append("  and a.SubjectID like '" + vs.getSubject() + "%'   \n");
		if (!vs.getEndDate().equals(""))
			sb.append("  and a.VchDate <='" + vs.getEndDate() + "'    \n");
		if (!vs.getStartDate().equals(""))
			sb.append("  and a.VchDate >='" + vs.getStartDate() + "'    \n");
		if (!vs.getMoneyStr().equals(""))
			sb.append("  and " + vs.getMoneyStr() + "    \n");
		if (!vs.getSummary().equals(""))
			sb.append("  and a.Summary like '%" + vs.getSummary() + "%'   \n");
		if (!vs.getVoucherNumber().equals(""))
			sb.append("  and a.oldvoucherid ='" + vs.getVoucherNumber()
					+ "'    \n");
		if (!vs.getTypeID().equals(""))
			sb.append("  and a.typeid ='" + vs.getTypeID() + "'   \n");
		if (!vs.getCreateor().equals(""))
			sb.append("  and Createor ='" + vs.getCreateor() + "'   \n");

		sb.append("      group by c.subjectid  \n");
		sb.append("  ) a  \n");
		sb.append("   inner join  \n");
		sb.append("  (   \n");
		sb.append("      /*  一级科目汇总 */  \n");
		sb
				.append("      select a.subjectid,a.accname,debittotalocc as debitocc,credittotalocc as creditocc  \n");
		sb.append("      from c_account a, c_accpkgsubject b  \n");
		sb.append("      where a.accpackageid='" + apkID + "'   \n");
		sb.append("      and b.accpackageid='" + apkID + "'   \n");
		sb.append("      and submonth=12  \n");
		sb.append("      and b.Level0=1  \n");
		sb.append("      and a.subjectid=b.subjectid  \n");
		sb.append("        \n");
		sb.append("  ) b  \n");
		sb.append("  on a.subjectid=b.subjectid  \n");
		sb.append("  order by a.subjectid \n");

		Connection conn = null;
		try {
			conn = new DBConnect().getConnect(apkID.substring(0, 6));
			GetResult gr = new GetResult(conn);
			data = gr.getStringArrayBySQL(conn, sb.toString());

			for (int i = 0; i < data.length; i++) {
				result.append(data[i][1] + "(" + data[i][0] + "):\n  借方总发生额: "
						+ data[i][3] + ";样品借方总发生额: " + data[i][2] + ";占比: "
						+ data[i][4] + "%;贷方总发生额: " + data[i][6]
						+ ";样品贷方总发生额: " + data[i][5] + ";占比: " + data[i][7]
						+ "% \n");// data[i][]
			}

		} catch (Exception e) {
			e.printStackTrace();
			result.append("执行抽凭合计出错:" + e.getMessage());
			// throw new Exception("执行抽凭合计出错",e);
		} finally {
			DbUtil.close(conn);
		}
		return result.toString();
	}


	/**
	 * 批量打印
	 */

	public ModelAndView create(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		ASFuntion CHF=new ASFuntion();
		HashMap mapResult = new HashMap();

		String T1 =(String)CHF.showNull(request.getParameter("T1"));
		String BeginYear =(String)CHF.showNull(request.getParameter("BeginYear"));
		String BeginDate = (String)CHF.showNull(request.getParameter("BeginDate"));
		String EndYear =(String)CHF.showNull(request.getParameter("EndYear"));
		String EndDate = (String)CHF.showNull(request.getParameter("EndDate"));
		
		String toSubjects = CHF.showNull(request.getParameter("toSubjects"));//显示对方科目
		String sqlwhere = "";
		if(!toSubjects.equals("1")){
			sqlwhere = " and  1=2 ";
		}
		String toLevel1 = CHF.showNull(request.getParameter("toLevel1"));//对方科目显示的级数
		String subjectnameSql = "";
		if(toLevel1.equals("1")){
			subjectnameSql = "if(locate('/',b.subjectfullname)=0,b.subjectfullname,substr(b.subjectfullname,1,locate('/',b.subjectfullname)-1))";
		}else{
			subjectnameSql =  "if(subjectname!=b.subjectfullname&&concat(if(locate('/',b.subjectfullname)=0,b.subjectfullname,substr(b.subjectfullname,1,locate('/',b.subjectfullname)-1)),'/',subjectname)!=b.subjectfullname \n"
				 +" ,concat(if(locate('/',b.subjectfullname)=0,b.subjectfullname,substr(b.subjectfullname,1,locate('/',b.subjectfullname)-1)),'/../',subjectname) \n"
				 +" ,b.subjectfullname) ";
		}
		mapResult.put("T1", T1);
		mapResult.put("BeginYear", BeginYear);
		mapResult.put("BeginDate", BeginDate);
		mapResult.put("EndYear", EndYear);
		mapResult.put("EndDate", EndDate);
		mapResult.put("sqlwhere", sqlwhere);
		mapResult.put("subjectnameSql", subjectnameSql);


		return new ModelAndView("/subjectentry/create.jsp",mapResult);
	}

	public ModelAndView createprintfile(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		ModelAndView modelAndView = new ModelAndView("/Excel/tempdata/PrintandSave.jsp");
		Connection conn=null;
		try {
			ASFuntion CHF=new ASFuntion();

			int iSheet = 10000;

			String T1 =(String)CHF.showNull(request.getParameter("T1"));
			String BeginYear =(String)CHF.showNull(request.getParameter("BeginYear"));
			String BeginDate = (String)CHF.showNull(request.getParameter("BeginDate"));
			String EndYear =(String)CHF.showNull(request.getParameter("EndYear"));
			String EndDate = (String)CHF.showNull(request.getParameter("EndDate"));

			String SubjectType = (String)CHF.showNull(request.getParameter("SubjectType"));	//0为1级，1为末级
			String bSubjectID = (String)CHF.showNull(request.getParameter("bSubjectID"));
			String eSubjectID = (String)CHF.showNull(request.getParameter("eSubjectID"));
			String sqlwhere = (String)CHF.showNull(request.getParameter("sqlwhere"));
			String subjectnameSql = (String)CHF.showNull(request.getParameter("subjectnameSql"));

			conn = new DBConnect().getConnect(T1);
			SubjectEntry subjectEntry = new SubjectEntry(conn);

			ArrayList args = subjectEntry.getSubject(T1,EndYear, EndDate, bSubjectID, eSubjectID, SubjectType);
			ArrayList filename = new ArrayList();
			String [] temptable = new String [args.size()];

			String acc = T1 + EndYear;
			String [][] CTName = new String[1][2];
			String [][] ColName = new String[1][2];
			CTName[0][1] = new String();
			CTName[0][1] = ",";
			ColName[0][1] = new String();
			ColName[0][1]= ",";
			CTName[0][0] = new String();
			ColName[0][0] = new String();

			String sql = "";
			String txtSQL = "";
			int i = 0;
//			String sRows = "";

			for (Iterator aiter = args.iterator(); aiter.hasNext(); ) {
				SubjectInfo si=(SubjectInfo)aiter.next();
				String SubjectID =si.subjectid;		//科目ID
				
				String avalue = si.accName+"("+SubjectID+")";	//科目名称
			
				if(avalue.length()>=31){
					avalue = avalue.substring(0,30);
				}
//				System.out.println(avalue);
//				String SubjectID = (String) args.get(i);
				temptable[i] = new String();
				temptable[i] = "tt_" + DELUnid.getCharUnid();

				PrintSetup printSetup = new PrintSetup(conn);
				printSetup.setStrTitles(new String[]{"科目明细账查询"});
				printSetup.setStrSheetName(avalue);
				printSetup.setCharColumn(new String[]{"1`2`3`4`5`6"});

				int result = subjectEntry.SubjectProperty(acc,SubjectID);
				Map map = subjectEntry.SubjectCurrency(acc,SubjectID);
				Map Umap = subjectEntry.SubjectUnitName(acc,SubjectID);
				Set coll = null;
				int TCount = 0;
				int TFor = 0;

				txtSQL = " limit ";

				switch(result){
				case 0:		//本位币
					subjectEntry.CreateTable(temptable[i]);
					subjectEntry.DataToTable(temptable[i],"","",T1,SubjectID,BeginYear,BeginDate,EndYear,EndDate);
					TCount = subjectEntry.getTempTableCount(temptable[i]);
					TFor = TCount / iSheet;

					for(int t = 0; t<= TFor; t++ ){

						txtSQL += String.valueOf(t * iSheet) + ",10000" ;
						if("".equals(sqlwhere)){
							printSetup.setStrChineseTitles(new String[]{"年`月`凭证日期`字`号`对方科目`摘要`借方发生额`贷方发生额`方向`余　额"});
							printSetup.setIColumnWidths(new int[]{6,4,13,6,6,30,46,25,25,4,25});
							printSetup.setCharColumn(new String[]{"1`2`3`4`5`6`7"});
							printSetup.setStrQuerySqls(new String[]{
									"select " +
									"IF(substring(a.vchdate,9)='00',vchyear,'') vchyear," +
									"IF(substring(a.vchdate,9)='00',vchmonth,'') vchmonth," +
									"case substring(a.vchdate,9) when '00' then '' when '97' then '' when '98' then '' else a.vchdate end vchdate," +
									"a.typeid,a.oldvoucherid,subjectnames,a.summary," +
									"if(substring(a.vchdate,9)='00','',debit)  debit,if(substring(a.vchdate,9)='00','',credit) credit,IF(dateremain>0,'借',if(dateremain<0,'贷','平')) rec,ABS(dateremain) dateremain " +
									"from (select *,group_concat(distinct "+subjectnameSql+") subjectnames   from (select * from `"+temptable[i]+"` order by id "+txtSQL+") a left join (select * from c_accpkgsubject where  AccPackageID="+acc+" "+sqlwhere+" ) b  on 1=1 and a.subjects like concat('%,',b.subjectid,',%')  group by a.id  ) a   " 
								});
							
						}else{
							printSetup.setStrChineseTitles(new String[]{"年`月`凭证日期`字`号`摘要`借方发生额`贷方发生额`方向`余　额"});
							printSetup.setIColumnWidths(new int[]{6,4,13,6,6,46,25,25,4,25});
							printSetup.setStrQuerySqls(new String[]{
									"select " +
									"IF(substring(a.vchdate,9)='00',vchyear,'') vchyear," +
									"IF(substring(a.vchdate,9)='00',vchmonth,'') vchmonth," +
									"case substring(a.vchdate,9) when '00' then '' when '97' then '' when '98' then '' else a.vchdate end vchdate," +
									"a.typeid,a.oldvoucherid,a.summary," +
									"if(substring(a.vchdate,9)='00','',debit)  debit,if(substring(a.vchdate,9)='00','',credit) credit,IF(dateremain>0,'借',if(dateremain<0,'贷','平')) rec,ABS(dateremain) dateremain " +
									"from (select *,group_concat(distinct "+subjectnameSql+") subjectnames   from (select * from `"+temptable[i]+"` order by id "+txtSQL+") a left join (select * from c_accpkgsubject where  AccPackageID="+acc+" "+sqlwhere+" ) b  on 1=1 and a.subjects like concat('%,',b.subjectid,',%')  group by a.id  ) a   " 
								});
						}
					
					
						
//						sRows = "$2:$4";

						filename.add(printSetup.getExcelFile());
						txtSQL = " limit ";
					}

					break;
				case 1:			//外币
					subjectEntry.CreateTable(temptable[i],map);
					subjectEntry.DataToTable(map,temptable[i],"","",T1,SubjectID,BeginYear,BeginDate,EndYear,EndDate);
					TCount = subjectEntry.getTempTableCount(temptable[i]);
					TFor = TCount / iSheet;

					for(int t = 0; t<= TFor; t++ ){
						txtSQL += String.valueOf(t * iSheet + 1) + "," +  String.valueOf((t+1) * iSheet) ;

						CTName[0][0] = "年,月,凭证日期,字,号,";
						ColName[0][0] = "年,月,凭证日期,字,号,";
						
						if("".equals(sqlwhere)){
							CTName[0][0] += "对方科目,";
							ColName[0][0] += "对方科目,";
						}
						CTName[0][0] += "摘要,";
						ColName[0][0] += "摘要,";
						
						sql = "select " +
						"IF(substring(a.vchdate,9)='00',vchyear,'') vchyear,IF(substring(a.vchdate,9)='00',vchmonth,'') vchmonth," +
						"case substring(a.vchdate,9) when '00' then '' when '97' then '' when '98' then '' else a.vchdate end vchdate," +
						"a.typeid,a.oldvoucherid, ";

						if("".equals(sqlwhere)){
							sql += "a.subjectnames,";
						}
						sql += "a.summary,";
						coll = map.keySet();
						int ii =1;
						for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
							String key = (String) iter.next();
							String value = (String) map.get(key);
							sql += "if(substring(a.vchdate,9)='00','',debitrate"+ii+") debitrate"+ii+",if(substring(a.vchdate,9)='00','',debit"+key+") debit"+key+",";
							CTName[0][0] += "借方,借方,";
							ColName[0][0] += "汇率,"+value+",";
							ii ++;
						}
						sql += "if(substring(a.vchdate,9)='00','',debit) debit,";
						CTName[0][0] += "借方,";
						ColName[0][0] += "借方本位币,";

						ii=1;
						for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
							String key = (String) iter.next();
							String value = (String) map.get(key);
							sql += "if(substring(a.vchdate,9)='00','',creditrate"+ii+") creditrate"+ii+",if(substring(a.vchdate,9)='00','',credit"+key+") credit"+key+",";
							CTName[0][0] += "贷方,贷方,";
							ColName[0][0] += "汇率,"+value+",";
							ii ++;
						}
						sql += "if(substring(a.vchdate,9)='00','',credit) credit,IF(dateremain>0,'借',if(dateremain<0,'贷','平')) rec,";
						CTName[0][0] += "贷方,方向,";
						ColName[0][0] += "贷方本位币,方向,";

						ii=1;
						for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
							String key = (String) iter.next();
							String value = (String) map.get(key);
							sql += "abs(dRemain"+key+") dRemain"+key+",";
							CTName[0][0] += "余额,";
							ColName[0][0] += value+",";
							ii ++;
						}
						sql +="ABS(dateRemain) dateRemain from (select *,group_concat(distinct "+subjectnameSql+") subjectnames   from (select * from `"+temptable[i]+"` order by id "+txtSQL+") a left join (select * from c_accpkgsubject where  AccPackageID="+acc+" "+sqlwhere+" ) b  on 1=1 and a.subjects like concat('%,',b.subjectid,',%')  group by a.id  ) a   "; 
						CTName[0][0] += "余额";
						ColName[0][0] += "本位币余额";

						printSetup.setStrQuerySqls(new String[]{sql});
						printSetup.setHeaders(CTName, ColName);

//						sRows = "$2:$5";
						filename.add(printSetup.getExcelFile());
						txtSQL = " limit ";

					}


					break;
				case 2:		//数量
					subjectEntry.CreateUnitTable(temptable[i],map);
					subjectEntry.DataToUnitTable(Umap,temptable[i],"","",T1,SubjectID,BeginYear,BeginDate,EndYear,EndDate);
					TCount = subjectEntry.getTempTableCount(temptable[i]);
					TFor = TCount / iSheet;

					for(int t = 0; t<= TFor; t++ ){
						txtSQL += String.valueOf(t * iSheet + 1) + "," +  String.valueOf((t+1) * iSheet) ;
						
						CTName[0][0] = "年,月,凭证日期,字,号,";
						ColName[0][0] = "年,月,凭证日期,字,号,";
						
						if("".equals(sqlwhere)){
							CTName[0][0] += "对方科目,";
							ColName[0][0] += "对方科目,";
						}
						CTName[0][0] += "摘要,";
						ColName[0][0] += "摘要,";

						sql = "select " +
						"IF(substring(a.vchdate,9)='00',vchyear,'') vchyear,IF(substring(a.vchdate,9)='00',vchmonth,'') vchmonth," +
						"case substring(a.vchdate,9) when '00' then '' when '97' then '' when '98' then '' else a.vchdate end vchdate," +
						"a.typeid,a.oldvoucherid, ";

						if("".equals(sqlwhere)){
							sql += "a.subjectnames,";
						}
						sql += "a.summary,";
						coll = Umap.keySet();
						int ii =1;
						for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
							String key = (String) iter.next();
							String value = (String) Umap.get(key);
							sql += "if(substring(a.vchdate,9)='00','',debitPrice"+ii+") debitPrice"+ii+",if(substring(a.vchdate,9)='00','',debit"+key+") debit"+key+",";
							CTName[0][0] += "借方,借方,";
							ColName[0][0] += "单价,"+value+",";
							ii ++;
						}
						sql += "if(substring(a.vchdate,9)='00','',debit) debit,";
						CTName[0][0] += "借方,";
						ColName[0][0] += "借方本位币,";

						ii=1;
						for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
							String key = (String) iter.next();
							String value = (String) Umap.get(key);
							sql += "if(substring(a.vchdate,9)='00','',creditPrice"+ii+") creditPrice"+ii+",if(substring(a.vchdate,9)='00','',credit"+key+") credit"+key+",";
							CTName[0][0] += "贷方,贷方,";
							ColName[0][0] += "单价,"+value+",";
							ii ++;
						}
						sql += "if(substring(a.vchdate,9)='00','',credit) credit,IF(dateremain>0,'借',if(dateremain<0,'贷','平')) rec,";
						CTName[0][0] += "贷方,方向,";
						ColName[0][0] += "贷方本位币,方向,";

						ii=1;
						for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
							String key = (String) iter.next();
							String value = (String) Umap.get(key);
							sql += "abs(dRemain"+key+") dRemain"+key+",";
							CTName[0][0] += "余额,";
							ColName[0][0] += value+",";
							ii ++;
						}
						sql +="ABS(dateRemain) dateRemain  from (select *,group_concat(distinct "+subjectnameSql+") subjectnames   from (select * from `"+temptable[i]+"` order by id "+txtSQL+") a left join (select * from c_accpkgsubject where  AccPackageID="+acc+" "+sqlwhere+" ) b  on 1=1 and a.subjects like concat('%,',b.subjectid,',%')  group by a.id  ) a   "; 
						CTName[0][0] += "余额";
						ColName[0][0] += "本位币余额";

						printSetup.setStrQuerySqls(new String[]{sql});
						printSetup.setHeaders(CTName, ColName);

//						sRows = "$2:$5";
						filename.add(printSetup.getExcelFile());
						txtSQL = " limit ";
					}


					break;
				case 3:		//外币与数量
					subjectEntry.CreateTable(temptable[i],map,Umap);
					subjectEntry.DataToTable(map,Umap,temptable[i],"","",T1,SubjectID,BeginYear,BeginDate,EndYear,EndDate);
					TCount = subjectEntry.getTempTableCount(temptable[i]);
					TFor = TCount / iSheet;

					for(int t = 0; t<= TFor; t++ ){
						txtSQL += String.valueOf(t * iSheet + 1) + "," +  String.valueOf((t+1) * iSheet) ;
						CTName[0][0] = "年,月,凭证日期,字,号,";
						ColName[0][0] = "年,月,凭证日期,字,号,";
						
						if("".equals(sqlwhere)){
							CTName[0][0] += "对方科目,";
							ColName[0][0] += "对方科目,";
						}
						CTName[0][0] += "摘要,";
						ColName[0][0] += "摘要,";
						sql = "select " +
						"IF(substring(a.vchdate,9)='00',vchyear,'') vchyear,IF(substring(a.vchdate,9)='00',vchmonth,'') vchmonth," +
						"case substring(a.vchdate,9) when '00' then '' when '97' then '' when '98' then '' else a.vchdate end vchdate," +
						"a.typeid,a.oldvoucherid, ";

						if("".equals(sqlwhere)){
							sql += "a.subjectnames,";
						}
						sql += "a.summary,";

						Set Ucoll = Umap.keySet();
						Set Ccoll = map.keySet();

						int ii =1;
						for (Iterator iter = Ucoll.iterator(); iter.hasNext(); ) {
							String key = (String) iter.next();
							String value = (String) Umap.get(key);
							sql += "if(substring(a.vchdate,9)='00','',debitPrice"+ii+") debitPrice"+ii+",if(substring(a.vchdate,9)='00','',debit"+key+") debit"+key+",";
							CTName[0][0] += "借方,借方,";
							ColName[0][0] += "单价,"+value+",";
							ii ++;
						}
						ii =1;
						for (Iterator iter = Ccoll.iterator(); iter.hasNext(); ) {
							String key = (String) iter.next();
							String value = (String) map.get(key);
							sql += "if(substring(a.vchdate,9)='00','',debitrate"+ii+") debitrate"+ii+",if(substring(a.vchdate,9)='00','',debit"+key+") debit"+key+",";
							CTName[0][0] += "借方,借方,";
							ColName[0][0] += "汇率,"+value+",";
							ii ++;
						}
						sql += "if(substring(a.vchdate,9)='00','',debit) debit,";
						CTName[0][0] += "借方,";
						ColName[0][0] += "借方本位币,";

						ii=1;
						for (Iterator iter = Ucoll.iterator(); iter.hasNext(); ) {
							String key = (String) iter.next();
							String value = (String) Umap.get(key);
							sql += "if(substring(a.vchdate,9)='00','',creditPrice"+ii+") creditPrice"+ii+",if(substring(a.vchdate,9)='00','',credit"+key+") credit"+key+",";
							CTName[0][0] += "贷方,贷方,";
							ColName[0][0] += "单价,"+value+",";
							ii ++;
						}
						ii=1;
						for (Iterator iter = Ccoll.iterator(); iter.hasNext(); ) {
							String key = (String) iter.next();
							String value = (String) map.get(key);
							sql += "if(substring(a.vchdate,9)='00','',creditrate"+ii+") creditrate"+ii+",if(substring(a.vchdate,9)='00','',credit"+key+") credit"+key+",";
							CTName[0][0] += "贷方,贷方,";
							ColName[0][0] += "汇率,"+value+",";
							ii ++;
						}
						sql += "if(substring(a.vchdate,9)='00','',credit) credit,IF(dateremain>0,'借',if(dateremain<0,'贷','平')) rec,";
						CTName[0][0] += "贷方,方向,";
						ColName[0][0] += "贷方本位币,方向,";

						ii=1;
						for (Iterator iter = Ucoll.iterator(); iter.hasNext(); ) {
							String key = (String) iter.next();
							String value = (String) Umap.get(key);
							sql += "abs(dRemain"+key+") dRemain"+key+",";
							CTName[0][0] += "余额,";
							ColName[0][0] += value+",";
							ii ++;
						}
						ii=1;
						for (Iterator iter = Ccoll.iterator(); iter.hasNext(); ) {
							String key = (String) iter.next();
							String value = (String) map.get(key);
							sql += "abs(dRemain"+key+") dRemain"+key+",";
							CTName[0][0] += "余额,";
							ColName[0][0] += value+",";
							ii ++;
						}
						sql +="ABS(dateRemain) dateRemain  from (select *,group_concat(distinct "+subjectnameSql+") subjectnames   from (select * from `"+temptable[i]+"` order by id "+txtSQL+") a left join (select * from c_accpkgsubject where  AccPackageID="+acc+" "+sqlwhere+" ) b  on 1=1 and a.subjects like concat('%,',b.subjectid,',%')  group by a.id  ) a   "; 
						CTName[0][0] += "余额";
						ColName[0][0] += "本位币余额";

						printSetup.setStrQuerySqls(new String[]{sql });
						printSetup.setHeaders(CTName, ColName);

//						sRows = "$2:$5";
						filename.add(printSetup.getExcelFile());
						txtSQL = " limit ";
					}


					break;

				}

				i ++;

			} //exit for

			for(int ii=0;i<temptable.length;i++){
				String TabName = temptable[ii];
				if(!"".equals(TabName)){
					subjectEntry.DelTempTable(TabName);
				}
			}

//			String [] filenames = new String[filename.size()];
//			for(int j=0;j<filename.size();j++){
//				filenames[i] = new String();
//				filenames[i] = (String)filename.get(i);
//			}

//			modelAndView.addObject("temptable", temptable);
//			modelAndView.addObject("filename", filename);
//			modelAndView.addObject("AccPackageID", acc);

//			modelAndView.addObject("T1", T1);
//			modelAndView.addObject("BeginYear", BeginYear);
//			modelAndView.addObject("BeginDate", BeginDate);
//			modelAndView.addObject("EndYear", EndYear);
//			modelAndView.addObject("EndDate", EndDate);

//			modelAndView.addObject("SubjectType", SubjectType);
//			modelAndView.addObject("bSubjectID", bSubjectID);
//			modelAndView.addObject("eSubjectID", eSubjectID);

			modelAndView.addObject("filenameList", filename);
			modelAndView.addObject("refresh","");

			modelAndView.addObject("saveasfilename","科目明细账");
			modelAndView.addObject("bVpage","false");
//			modelAndView.addObject("strPrintTitleRows",sRows);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		return modelAndView;
	}


	public ModelAndView delalltable(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		Connection conn=null;
		try {
			ASFuntion CHF=new ASFuntion();
			String acc = CHF.showNull(request.getParameter("AccPackageID"));

			conn = new DBConnect().getConnect(acc.substring(0,6));

			String [] temptable = request.getParameterValues("temptable");
			SubjectEntry se = new SubjectEntry(conn);
			for(int i=0;i<temptable.length;i++){
				String TabName = temptable[i];
				if(!"".equals(TabName)){
					se.DelTempTable(TabName);
				}
			}


		} catch (Exception e) {
			Debug.print(Debug.iError, "删除临时表失败！", e);
            throw e;
		} finally {
			DbUtil.close(conn);
		}
		return null;
	}

	public void getSubjectNames(HttpServletRequest request, HttpServletResponse response) {

System.out.println("getSubjectNames");

		PreparedStatement ps = null;
	    ResultSet rs = null;
	    ASFuntion CHF = new ASFuntion() ;
	    Connection conn = null ;

	    String subjectIDs = CHF.showNull(request.getParameter("subjectIDs")) ;

	    String[] subjectIDsArr = subjectIDs.split("<BR>") ;
	    String T1 = CHF.showNull(request.getParameter("T1")) ;

System.out.println("SubjectIds:"+CHF.showNull(request.getParameter("subjectIDs")));


	    String subjectNames = "" ;
	    try {
	    	conn = new DBConnect().getConnect(T1);
	    	PrintWriter out = response.getWriter() ;
	    	for(int i=0;i<subjectIDsArr.length;i++) {

		    	String sql = "select SubjectName from c_accpkgsubject where SubjectID = '" +subjectIDsArr[i] + "'";
		    	ps = conn.prepareStatement(sql);
		        rs = ps.executeQuery();

		        if(rs.next()) {
		        	subjectNames += rs.getString("SubjectName") + "<br>" ;
		        }
	    	}
	    		subjectNames = UTILString.killEndToken(subjectNames, "<br>") ;

	    	out.write(subjectNames) ;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(conn) ;
		}

	}

	
	/**
	 * 只分析本位币
	 * 多栏明细帐：借方多栏账（贷方不分析）、借方多栏账（贷方以负数分析）、贷方多栏账（借方不分析）、贷方多栏账（借方以负数分析）。
	 * 1、借方多栏账（贷方不分析），通常用于费用类科目，支出记借方，结转支出时记贷方，贷方发生额不记入明细专栏，合计和累计体现的是实际支出数。
	 * 2、借方多栏账（贷方以负数分析），通常用于不需月末结转的科目，借方发生额以正数记入明细专栏，贷方发生额以负数记入明细专栏，合计时借贷相抵。如果月末需结转的科目用这种账页，借贷相抵后，合计为零。
	 * 3、贷方多栏账（借方不分析），通常用于收入类科目，收入记贷方，结转收入时记借方，借方发生额不记入明细专栏，合计和累计体现的是实际收入数。
	 * 4、贷方多栏账（借方以负数分析），通常用于不需月末结转的科目，贷方发生额以正数记入明细专栏，借方发生额以负数记入明细专栏，合计时借贷相抵。如果月末需结转的科目用这种账页，借贷相抵后，合计为零。
	 * 5、普通多栏账
	 */
	
	public ModelAndView alist(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		ModelAndView modelAndView = new ModelAndView(_strAList);
		Connection conn=null;
		try {
			ASFuntion CHF=new ASFuntion();
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");

			String T1 = CHF.showNull(request.getParameter("T1"));
			String acc = CHF.showNull(request.getParameter("AccPackageID"));
			
			String html = CHF.showNull(request.getParameter("html"));	//用于标志要不要用setTdtoValue　1为要　
			
			String projectid = CHF.showNull(userSession.getCurProjectId());
			if("".equals(acc)){
				acc = CHF.showNull(userSession.getCurChoiceAccPackageId());
				if("".equals(acc)){
					acc = CHF.showNull(userSession.getCurAccPackageId());
				}
			}else{
				userSession.setCurChoiceAccPackageId(acc);
			}
			if(!"".equals(acc)){
				if("".equals(T1)){
					T1=acc.substring(0,6);
				}
			}
			if(!"".equals(acc)){
				conn = new DBConnect().getConnect(T1);

				ASTextKey tkey = new ASTextKey(conn);
				String TName = "";
				if(!"".equals(T1)) TName = tkey.TextCustomerName(T1);

				String BeginYear = CHF.showNull(request.getParameter("BeginYear"));
				if("".equals(BeginYear) ){
					BeginYear = acc.substring(6);
				}
				if("".equals(BeginYear)){
					BeginYear = CHF.showNull(userSession.getCurChoiceBeginYear());
					if("".equals(BeginYear)){
						BeginYear = CHF.showNull(userSession.getCurProjectBeginYear());
					}
				}else{
					userSession.setCurChoiceBeginYear(BeginYear);
				}

				String EndYear = CHF.showNull(request.getParameter("EndYear"));
				if("".equals(EndYear) ){
					EndYear = acc.substring(6);
				}
				if("".equals(EndYear)){
					EndYear = CHF.showNull(userSession.getCurChoiceEndYear());
					if("".equals(EndYear)){
						EndYear = CHF.showNull(userSession.getCurProjectEndYear());
					}
				}else{
					userSession.setCurChoiceEndYear(EndYear);
				}

				String BeginDate = CHF.showNull(request.getParameter("BeginDate"));
				if("".equals(BeginDate)){
					BeginDate = CHF.showNull(userSession.getCurChoiceBeginMonth());
					if("".equals(BeginDate)){
						BeginDate = CHF.showNull(userSession.getCurProjectBeginMonth());
					}
				}else{
					userSession.setCurChoiceBeginMonth(BeginDate);
				}
				if("".equals(BeginDate)) BeginDate = "01";

				String EndDate = CHF.showNull(request.getParameter("EndDate"));
				if("".equals(EndDate)){
					EndDate = CHF.showNull(userSession.getCurChoiceEndMonth());
					if("".equals(EndDate)){
						EndDate = CHF.showNull(userSession.getCurProjectEndMonth());
					}
				}else{
					userSession.setCurChoiceEndMonth(EndDate);
				}
				if("".equals(EndDate)) EndDate = "12";

				String SubjectID = CHF.showNull(request.getParameter("SubjectID"));

				SubjectEntry subjectEntry = new SubjectEntry(conn);
				String SubjectId1 = "";
				if(!"".equals(SubjectID)) {
					if("".equals(subjectEntry.getSName1(acc, SubjectID))){
						SubjectID = "";
					} else {
						SubjectId1 = subjectEntry.getSName1(acc, SubjectID);
					}
				}


				/**
				 * 没有狗不能看2005年后的帐套
				 */
				int accAllowYear = Integer.parseInt(UTILSysProperty.SysProperty.getProperty("accAllowYear")); 
				if (Integer.parseInt(EndYear) > accAllowYear && JRockey2Opp.getUserLic() <= 0) {
				    final String TRY_URL = "/AuditSystem/AS_SYSTEM/error_page.jsp?tip=999";
				    response.sendRedirect(TRY_URL);
				    return null;
				}

				String Subjects = subjectEntry.getSubjects(acc, SubjectID);
				SubjectID = SubjectId1;

				String currency = CHF.showNull(request.getParameter("currency"));
				String toSubjects = CHF.showNull(request.getParameter("toSubjects"));
				String toLevel1 = CHF.showNull(request.getParameter("level0"));

				modelAndView.addObject("T1", T1);
				modelAndView.addObject("TName", TName);
				modelAndView.addObject("BeginYear", BeginYear);
				modelAndView.addObject("BeginDate", BeginDate);
				modelAndView.addObject("EndYear", EndYear);
				modelAndView.addObject("EndDate", EndDate);
				modelAndView.addObject("SubjectID", SubjectID);
				modelAndView.addObject("Subjects", Subjects);
				modelAndView.addObject("currency", currency);
				modelAndView.addObject("toSubjects", toSubjects);
				modelAndView.addObject("level0", toLevel1);
			}else{
				modelAndView.addObject("SubjectID", "");
				modelAndView.addObject("BeginDate", "01");
				modelAndView.addObject("EndDate", "12");
			}

			modelAndView.addObject("AccPackageID", acc);
			modelAndView.addObject("projectid", projectid);

			modelAndView.addObject("html", html);
			
			return modelAndView;
		} catch (Exception e) {
			Debug.print(Debug.iError, "科目明细账查询失败！", e);
            throw e;
		} finally {
			DbUtil.close(conn);
		}
		
	}
	
	//多栏明细账查询
	public ModelAndView fivelist(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		Connection conn=null;
		HashMap mapResult = new HashMap();
		try {
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
			ASFuntion CHF=new ASFuntion();

			String user=CHF.showNull(userSession.getUserId());
			String projectid=CHF.showNull(userSession.getCurProjectId());
			if("".equals(projectid)) projectid = "-1";

			String T1 = CHF.showNull(request.getParameter("T1"));
			String TabName = CHF.showNull(request.getParameter("TabName"));


			String acc = CHF.showNull(request.getParameter("AccPackageID"));
			if("".equals(acc)){
				acc = CHF.showNull(userSession.getCurChoiceAccPackageId());
				if("".equals(acc)){
					acc = CHF.showNull(userSession.getCurAccPackageId());
				}
			}

			String BeginYear = CHF.showNull(request.getParameter("BeginYear"));
			if("".equals(BeginYear)){
				BeginYear = CHF.showNull(userSession.getCurChoiceBeginYear());
				if("".equals(BeginYear)){
					BeginYear = CHF.showNull(userSession.getCurProjectBeginYear());
				}
			}
			String EndYear = CHF.showNull(request.getParameter("EndYear"));
			if("".equals(EndYear)){
				EndYear = CHF.showNull(userSession.getCurChoiceEndYear());
				if("".equals(EndYear)){
					EndYear = CHF.showNull(userSession.getCurProjectEndYear());
				}
			}
			String BeginDate = CHF.showNull(request.getParameter("BeginDate"));
			if("".equals(BeginDate)){
				BeginDate = CHF.showNull(userSession.getCurChoiceBeginMonth());
				if("".equals(BeginDate)){
					BeginDate = CHF.showNull(userSession.getCurProjectBeginMonth());
				}
			}
			String EndDate = CHF.showNull(request.getParameter("EndDate"));
			if("".equals(EndDate)){
				EndDate = CHF.showNull(userSession.getCurChoiceEndMonth());
				if("".equals(EndDate)){
					EndDate = CHF.showNull(userSession.getCurProjectEndMonth());
				}
			}

			String SubjectID = CHF.showNull(request.getParameter("SubjectID"));
			String sData = CHF.showNull(request.getParameter("sData"));
			
			String atype = CHF.showNull(request.getParameter("atype"));
			int type = 0;
			if(!"".equals(atype)){
				type = Integer.parseInt(atype);
			}
			
			conn = new DBConnect().getConnect(T1);
			SubjectEntry subjectEntry = new SubjectEntry(conn);

			if("".equals(sData)){
				sData = subjectEntry.getSubject(acc,SubjectID);
				
				if(sData.split("`").length == 1){
					sData += SubjectID +"`";
				}
			}

			System.out.println("sData="+sData);
			
			if(!subjectEntry.ExistsTable(TabName)){
				subjectEntry.CreateTable(TabName,sData);
				subjectEntry.DataToTable(sData,TabName,user,projectid,T1,SubjectID,BeginYear,BeginDate,EndYear,EndDate);
			}

			/**
			 * DataGrid
			 */
			if(!"".equals(SubjectID)){
				DataGridProperty pp = new DataGridProperty(){
					public void onSearch (
		    				javax.servlet.http.HttpSession session,
		    				javax.servlet.http.HttpServletRequest request,
		    				javax.servlet.http.HttpServletResponse response) throws Exception{

						String TabName = this.getRequestValue("TabName");

						String allOpt = this.getRequestValue("allOpt");
						String moneyChao = this.getRequestValue("moneyChao");
 						String sLength = this.getRequestValue("sLength"); 
 						
						if(moneyChao == null || "".equals(moneyChao.trim())){
							allOpt = "0";
						}
						
						String strWhere = "";
						String allType = this.getRequestValue("allType");
						if("".equals(allType)) allType = "1";
						if("1".equals(allType)){
							strWhere = " ABS(dateRemain) ";
						}else if("2".equals(allType)){
							if("".equals(sLength)){
								strWhere = " ABS(debit) ";	
							}else{
								int length = Integer.parseInt(sLength);
								for(int i=1;i < length; i++) {
									strWhere += "debit"+i+" + ";
								}
								strWhere = " ABS("+strWhere+"0) ";
							}
							
						}else if("3".equals(allType)){
							if("".equals(sLength)){
								strWhere = " ABS(credit) ";	
							}else{
								int length = Integer.parseInt(sLength);
								for(int i=1;i < length; i++) {
									strWhere += "credit"+i+" + ";
								}
								strWhere = " ABS("+strWhere+"0) ";
							}
						}
						
						String where = "";
						if("1".equals(allOpt)){
							where = " and substring(vchdate,9) not in ('00','97','98') " +
							" and " + strWhere + " >" + moneyChao ;
						}else if("2".equals(allOpt)){
							where = " and substring(vchdate,9) not in ('00','97','98') " +
							" and " + strWhere + " >" + moneyChao +
							" and id in (" +
							" 	select max(id) from `"+TabName+"` " +
							"	where 1=1 and substring(vchdate,9) not in ('00','97','98')" +
							"	group by vchdate" +
							" ) ";
						}else if("3".equals(allOpt)){
							where = " and substring(vchdate,9) not in ('00','97','98') " +
							" and " + strWhere + " >" + moneyChao +
							" and id in (" +
							" 	select max(id) from `"+TabName+"` " +
							"	where 1=1 and substring(vchdate,9) not in ('00','97','98')" +
							"	group by vchmonth" +
							" ) ";
						}


						this.setOrAddRequestValue("where",where);
					}
				};
				
				String toSubjects = CHF.showNull(request.getParameter("toSubjects"));//显示对方科目
				String sqlwhere = "";
				if(!toSubjects.equals("1")){
					sqlwhere = " and  1=2 ";
				}
				String level0 = CHF.showNull(request.getParameter("level0")); //对方科目显示的级数
				String subjectnameSql = "";
				if(level0.equals("1")){
					subjectnameSql = "if(locate('/',b.subjectfullname1)=0,b.subjectfullname1,substr(b.subjectfullname1,1,locate('/',b.subjectfullname1)-1))";
				}else{
//					subjectnameSql =  "if(b.subjectname1!=b.subjectfullname1 && concat(if(locate('/',b.subjectfullname1)=0,b.subjectfullname1,substr(b.subjectfullname1,1,locate('/',b.subjectfullname1)-1)),'/',b.subjectname1)!=b.subjectfullname1 \n"
//						 +" ,concat(if(locate('/',b.subjectfullname1)=0,b.subjectfullname1,substr(b.subjectfullname1,1,locate('/',b.subjectfullname1)-1)),'/../',b.subjectname1) \n"
//						 +" ,b.subjectfullname1) ";
					subjectnameSql =  " b.subjectfullname1 ";
				}
				
				String html = CHF.showNull(request.getParameter("html"));	//用于标志要不要用setTdtoValue　1为要　
				if("1".equals(html)){
					pp.setTdtoValue("<a href='St://${value}'>${tdvalue}</a>");	
				}
				
				pp.setTableID("SubjectEntry"+subjectEntry.getRandom());
				pp.setCustomerId(T1);
				pp.setPageSize_CH(100);
				pp.setWhichFieldIsValue(3);
				pp.setOrderBy_CH("id");
				pp.setDirection("asc");
				pp.setCancelOrderby(true);

				pp.setTrActionProperty(true);
				pp.setTrAction("  AccPackageID='"+acc+"' vchdate='${vchdate}' voucherid='${voucherid}' style='cursor:hand;' onDBLclick='goSort();'");

				String TableHead = "",TableHead1 = "";
				String sql = "";

				TableHead = "凭证日期,字,号,";

				sql = "select id,a.vchmonth vchmonth1,autoid,a.voucherid," +
					"case substring(a.vchdate,9) when '00' then 0 when '97' then 0 when '98' then 0 else y.vchid end p1," +
					"case substring(a.vchdate,9) when '00' then 0 when '97' then 0 when '98' then 0 else z.vchid end p2," +
					"case substring(a.vchdate,9) when '00' then 0 when '97' then 0 when '98' then 0 else t.vchid end p3," +
					"IF(substring(a.vchdate,9)='00',vchyear,'') vchyear,IF(substring(a.vchdate,9)='00',vchmonth,'') vchmonth," +
					"case substring(a.vchdate,9) when '00' then '' when '97' then '' when '98' then '' else a.vchdate end vchdate," +
					"a.typeid,a.oldvoucherid,";

				String setColumnWidth = "";
				if(acc.equals(userSession.getCurAccPackageId()) && userSession.getCurProjectId()!=null && !"1".equals(html)){
//					TableHead = "抽,年,月,凭证日期,字,号,";
					pp.addColumn("抽","p1",FormatType.showTakeOut );
					TableHead1 = "抽,";
//					pp.addColumn("疑","p2",FormatType.showDoubt );
					setColumnWidth = "4,";
					ProjectService projectService = new ProjectService(conn);
					if("税务审计".equals(projectService.getProjectById(projectid).getAuditPara())){
						pp.addColumn("税", "p3", "showTaskTax");
//						TableHead = "抽,疑,税,年,月,凭证日期,字,号,";
						TableHead1 = "抽,税,";
					}
				}

				setColumnWidth += "8,8,5,25";
				
//				pp.addColumn("年", "vchyear","showCenter");
//				pp.addColumn("月", "vchmonth","showCenter");
				pp.addColumn("凭证日期", "vchdate","showCenter");
				pp.addColumn("字", "typeid","showCenter");
				pp.addColumn("号", "oldvoucherid","showCenter");
				pp.addColumn("摘要", "summary");
				TableHead += "摘要,";
				if("1".equals(toSubjects)){
					sql += "a.subjectnames,";
					TableHead += "对方科目,";
					pp.addColumn("对方科目", "subjectnames");
					setColumnWidth += ",15";
				}
				pp.setColumnWidth(setColumnWidth);
				
				sql += "a.summary,";
				

				String [] sAll = sData.split("`");
				String ss = "",s1 = "",s2 = "";
				
				for(int i=1;i < sAll.length; i++) {
					s1 += "debit"+i+" + ";
					s2 += "credit"+i+" + ";
				}
				sql += "if(substring(a.vchdate,9)='00','',("+s1+" 0)) as debit,if(substring(a.vchdate,9)='00','',("+s2+" 0)) as credit,";
				pp.addColumn("借方", "debit","showMoney");
				pp.addColumn("贷方", "credit","showMoney");
				TableHead += "借方,贷方,";
				
				sql += " IF(dateremain>0,'借',if(dateremain<0,'贷','平')) rec,ABS(dateremain) dateremain, " ;
				TableHead += "方向,余额,";
				pp.addColumn("方向", "rec","showCenter");
				pp.addColumn("余额", "dateRemain","showMoney");
				
				switch(type){
				case 0 :	//普通多栏帐
					for(int i=1;i < sAll.length; i++) {
						String sName = subjectEntry.getSName(acc,sAll[i]);
//						sql += "if(substring(a.vchdate,9)='00' || substring(a.vchdate,9)='97' || substring(a.vchdate,9)='98','',if(debit"+i+"=0.00 ,'',debit"+i+")) debit"+i+",";
						sql += "if(debit"+i+"=0.00 ,'',debit"+i+") debit"+i+",";
						pp.addColumn(sName, "debit"+i,"showMoney");
						ss += sName+",";
					}
					for(int i=1;i < sAll.length; i++) {
						String sName = subjectEntry.getSName(acc,sAll[i]);
//						sql += "if(substring(a.vchdate,9)='00' || substring(a.vchdate,9)='97' || substring(a.vchdate,9)='98','',if(credit"+i+"=0.00,'',credit"+i+")) credit"+i+",";
						sql += "if(credit"+i+"=0.00,'',credit"+i+") credit"+i+",";
						pp.addColumn(sName, "credit"+i,"showMoney");
					}
					TableHead += "借方{"+ss.substring(0, ss.length()-1)+"},贷方{"+ss.substring(0, ss.length()-1)+"}";
					sql = sql.substring(0, sql.length()-1);
					break;
				case 1 :	//借方（贷方不分析）
					for(int i=1;i < sAll.length; i++) {
						String sName = subjectEntry.getSName(acc,sAll[i]);
//						sql += "if(substring(a.vchdate,9)='00' || substring(a.vchdate,9)='97' || substring(a.vchdate,9)='98','',if(debit"+i+"=0.00 ,'',debit"+i+")) debit"+i+",";
						sql += "if(debit"+i+"=0.00 ,'',debit"+i+") debit"+i+",";
						pp.addColumn(sName, "debit"+i,"showMoney");
						ss += sName+",";
					}
					TableHead += "借方{"+ss.substring(0, ss.length()-1)+"}";
					sql = sql.substring(0, sql.length()-1);
					break;
				case 2 :	//借方（贷方以负数分析）
					for(int i=1;i < sAll.length; i++) {
						String sName = subjectEntry.getSName(acc,sAll[i]);
//						sql += "if(substring(a.vchdate,9)='00' || substring(a.vchdate,9)='97' || substring(a.vchdate,9)='98','',if(credit"+i+"<>0.00 ,(-1)*credit"+i+",if(debit"+i+"<>0.00,debit"+i+",''))) debit"+i+",";
						sql += "if(substring(a.vchdate,9)='00' || substring(a.vchdate,9)='97' || substring(a.vchdate,9)='98',if(substring(a.vchdate,9)='00' ,'',(debit"+i+"-credit"+i+")),if(credit"+i+"<>0.00 ,(-1)*credit"+i+",if(debit"+i+"<>0.00,debit"+i+",''))) debit"+i+",";
						pp.addColumn(sName, "debit"+i,"showMoney");
						ss += sName+",";
					}
					TableHead += "借方{"+ss.substring(0, ss.length()-1)+"}";
					sql = sql.substring(0, sql.length()-1);
					break;
				case 3 :	//贷方（借方不分析）
					for(int i=1;i < sAll.length; i++) {
						String sName = subjectEntry.getSName(acc,sAll[i]);
//						sql += "if(substring(a.vchdate,9)='00' || substring(a.vchdate,9)='97' || substring(a.vchdate,9)='98','',if(credit"+i+"=0.00,'',credit"+i+")) credit"+i+",";
						sql += "if(credit"+i+"=0.00,'',credit"+i+") credit"+i+",";
						pp.addColumn(sName, "credit"+i,"showMoney");
						ss += sName+",";
					}
					TableHead += "贷方{"+ss.substring(0, ss.length()-1)+"}";
					sql = sql.substring(0, sql.length()-1);
					break;
				case 4 :	//贷方（借方以负数分析）
					for(int i=1;i < sAll.length; i++) {
						String sName = subjectEntry.getSName(acc,sAll[i]);
//						sql += "if(substring(a.vchdate,9)='00' || substring(a.vchdate,9)='97' || substring(a.vchdate,9)='98','',if(debit"+i+"<>0.00 ,(-1)*debit"+i+",if(credit"+i+"<>0.00,credit"+i+",''))) credit"+i+",";
						sql += "if(substring(a.vchdate,9)='00' || substring(a.vchdate,9)='97' || substring(a.vchdate,9)='98',if(substring(a.vchdate,9)='00' ,'',(-1)*(debit"+i+"-credit"+i+")),if(debit"+i+"<>0.00 ,(-1)*debit"+i+",if(credit"+i+"<>0.00,credit"+i+",''))) credit"+i+",";
						pp.addColumn(sName, "credit"+i,"showMoney");
						ss += sName+",";
					}
					TableHead += "贷方{"+ss.substring(0, ss.length()-1)+"}";
					sql = sql.substring(0, sql.length()-1);
					break;
				}
				
				sql += " from (" +
				"	select a.*,group_concat(distinct "+subjectnameSql+") subjectnames   from (" +
				"		select *,IF(("+s2+"+0)>0,1,-1) AS opt from `"+TabName+"` where 1=1 ${where} order by id ${LIMIT}" +
				"	) a left join c_subjectentry b " +
				"	on 1=1 " +
				"	AND SUBSTRING(b.VchDate,1,4)*12 + SUBSTRING(b.VchDate,6,2) >= " + (Integer.parseInt(BeginYear) * 12 + Integer.parseInt(BeginDate)) +  
				"	AND SUBSTRING(b.VchDate,1,4)*12 + SUBSTRING(b.VchDate,6,2) <= " + (Integer.parseInt(EndYear) * 12 + Integer.parseInt(EndDate)) + 
				"	AND a.voucherid=b.voucherid " +
				"	AND a.opt = IF(b.Dirction*b.occurvalue<0,-1,1) " +
				"	AND a.autoid <>b.autoid " + sqlwhere + 
				"	group by a.id  " +
				" ) a " +
				" left join (select distinct vchid from z_voucherspotcheck  where projectid='" +projectid + "' and createor='" + user +"'   ) y on a.voucherid=y.vchid  " +
				" left join (select distinct vchid from z_question  where projectid='" +projectid + "' and createor='" + user +"'   )  z on a.voucherid=z.vchid  " +
				" left join (select distinct vchid from z_taxcheck  where projectid='" +projectid + "' and createor='" + user +"'   )  t on a.autoid=t.vchid  where 1=1 ";

				pp.setTableHead(TableHead1 + TableHead);

				pp.setLimitByOwnEnable(true);
				pp.setCountsql("select count(*) as datagrid_count from `" + TabName + "`  where 1=1 ${where} ");
				pp.setEnableCountTr(false);

				pp.addSqlWhere("where", "${where}");

				pp.addInputValue("TabName");
				pp.addInputValue("allOpt");
				pp.addInputValue("allType");
				pp.addInputValue("sLength");
				pp.addInputValue("moneyChao");

				String TName = "";
				if(!"".equals(T1)) TName = new ASTextKey(conn).TextCustomerName(T1);
				
				pp.setPrintEnable(true);
				pp.setPrintTitle(TName + "      科目明细账查询");
				pp.setPrintCharColumn("1`2`3`4") ;
				
				pp.setPrintPoms("科目名称："+subjectEntry.getSubFullName(acc,SubjectID) +"    期间："+BeginYear+"年"+BeginDate+"月－"+EndYear+"年"+EndDate+"月     贷币单位："+new ASTextKey(conn).getACurrRate(acc));

				
				pp.setPrintTableHead(TableHead);
				
//				String TName = "";
//				if(!"".equals(T1)) TName = new ASTextKey(conn).TextCustomerName(T1);
//				pp.setPrintTitle(TName + "　　多栏明细账查询");
				pp.setFixedHeader(true) ;
				pp.setSQL(sql);
				request.getSession().setAttribute(DataGrid.sessionPre+pp.getTableID(),pp);

				String sTable = "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tr><td>科目名称：<font color=\"blue\">"+subjectEntry.getSubFullName(acc,SubjectID)+"</font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;期间：<font color=\"blue\">"+BeginYear+"年"+BeginDate+"月－"+EndYear+"年"+EndDate+"月</font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;本位币名称：<font color=\"blue\">"+new ASTextKey(conn).getACurrRate(acc)+"</font></td></tr></table>";

				mapResult.put("SubjectID", SubjectID);
				mapResult.put("T1", T1);
				mapResult.put("sTable", sTable);
				
				mapResult.put("TabName", TabName);
				mapResult.put("DataGrid", pp.getTableID());
				mapResult.put("html", html);
				mapResult.put("allOpt", String.valueOf(type));	//用于保存多栏帐类型
				mapResult.put("sLength",String.valueOf(sAll.length));
				
			}

		} catch (Exception e) {
			Debug.print(Debug.iError, "科目明细账查询失败 4:多栏帐！", e);
            throw e;
		} finally {
			DbUtil.close(conn);
		}
		return new ModelAndView(_strBList,mapResult);
	}
	
	/**
	 * 打印
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView xtext(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		//
		Connection conn=null;
		HashMap mapResult = new HashMap();
		try {
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
			ASFuntion CHF=new ASFuntion();
/*
			//var url = "subjectentry.do?method=text
			&DataGrid="+txtTable+"
			&type="+type+"
			&Currency="+cr+"
			&sData="+sd+"
			&TabName="+t+"
			&SubjectID="+s+"
			&BeginYear=${BeginYear}
			&BeginDate=${BeginDate}
			&EndYear=${EndYear}
			&EndDate=${EndDate}
			&T1=${T1}
			&txtType="+txtType+"
			&txtBegin="+txtBegin+"
			&txtEnd="+txtEnd+"
			&toSubjects="+to+"
			&random="+Math.random();
*/			
			String user=CHF.showNull(userSession.getUserId());
			String projectid=CHF.showNull(userSession.getCurProjectId());
			if("".equals(projectid)) projectid = "-1";

			String DGrid =(String)CHF.showNull(request.getParameter("DataGrid"));

			String type =(String)CHF.showNull(request.getParameter("type"));
			
			String T1 =(String)CHF.showNull(request.getParameter("T1"));
			String SubjectID = (String)CHF.showNull(request.getParameter("SubjectID"));
			String BeginYear =(String)CHF.showNull(request.getParameter("BeginYear"));
			String BeginDate = (String)CHF.showNull(request.getParameter("BeginDate"));
			String EndYear =(String)CHF.showNull(request.getParameter("EndYear"));
			String EndDate = (String)CHF.showNull(request.getParameter("EndDate"));

			String TabName = (String)CHF.showNull(request.getParameter("TabName"));

			String acc = T1 + EndYear;

			String txtType = (String)CHF.showNull(request.getParameter("txtType"));
			String txtBegin =(String)CHF.showNull(request.getParameter("txtBegin"));
			String txtEnd = (String)CHF.showNull(request.getParameter("txtEnd"));
			String txtSQL1 = "";
			String txtSQL2 = "";
			txtType = "".equals(txtType)?"0":txtType;

			String sData = (String)CHF.showNull(request.getParameter("sData"));
			String Currency = (String)CHF.showNull(request.getParameter("Currency"));

			String toSubjects = (String)CHF.showNull(request.getParameter("toSubjects"));

			switch(Integer.parseInt(txtType)){
			case 0:
				break;
			case 1:
			case 2:
				txtSQL1 = " limit "+txtBegin+","+txtEnd;
				break;
			case 3:
				txtSQL2 = " and vchmonth1>="+txtBegin+" and vchmonth1<="+txtEnd+" ";
				break;
			}

			conn = new DBConnect().getConnect(T1);
			SubjectEntry subjectEntry = new SubjectEntry(conn);

			ASTextKey tkey = new ASTextKey(conn);
			String TName = "";
			String ss = "";
			String full = subjectEntry.getSubFullName(acc,SubjectID);
			if(!"".equals(T1)) {
				TName = tkey.TextCustomerName(T1);
				ss = tkey.getACurrRate(acc);
			}

			DataGridProperty pp = (DataGridProperty)request.getSession().getAttribute(DataGrid.sessionPre+DGrid); //科目明细账查询

			PrintSetup printSetup = new PrintSetup(conn);
			printSetup.setStrTitles(new String[]{TName+"　　多栏明细账查询"});

			List[] lists = new List[1];
			lists[0] = new ArrayList();
			lists[0].add(0, "1");
			lists[0].add(1, "科目名称："+full +"    期间："+BeginYear+"年"+BeginDate+"月－"+EndYear+"年"+EndDate+"月     贷币单位："+ss);
			lists[0].add(2, "9");
			lists[0].add(3, null);

			printSetup.setPoms(lists);
			printSetup.setCharColumn(new String[]{"1`2`3`4`5`6"});
			String sRows = "";

			String [][] CTName = new String[1][2];
			String [][] ColName = new String[1][2];


			String sql = "select vchyear,vchmonth,vchdate,typeid,oldvoucherid,";

			if("1".equals(toSubjects) ){
				sql += "replace(subjectnames,'<br>',','),";
			}

			sql += "summary,debit,credit,rec,dateremain";
			String tempsql = sql;
			
			String txtSQL = " limit ";
			int iSheet = 10000;

			int TCount = 0;
			TCount = Integer.parseInt(pp.getAllCount());

			int TFor = TCount / iSheet;
			ArrayList filename = new ArrayList();
			
			String [] sAll = sData.split("`");
			
			for(int t = 0; t<= TFor; t++ ){
				sql = tempsql;
				txtSQL += String.valueOf(t * iSheet ) + ",10000" ;
				String s1="";
				String s2="";
				switch(Integer.parseInt(type)){
					case 0:		//普通多栏帐
						CTName[0][1] = new String();
						CTName[0][1] = ",";
						ColName[0][1] = new String();
						ColName[0][1]= ",";
						CTName[0][0] = new String();
						ColName[0][0] = new String();
						
						CTName[0][0] = "年,月,凭证日期,字,号,";
						ColName[0][0] = "年,月,凭证日期,字,号,";
	
						if("1".equals(toSubjects)){
							CTName[0][0] += "对方科目,";
							ColName[0][0] += "对方科目,";
						}
						
						CTName[0][0] += "摘要,借方,贷方,方向,余额";
						ColName[0][0] += "摘要,借方,贷方,方向,余额";
	
						String t1="";
						String t2="";
						String tt="";
	
						for(int i=1;i < sAll.length; i++) {
							String sName = subjectEntry.getSName(acc,sAll[i]);
							s1 += ", debit"+i+" ";
							s2 += ", credit"+i+" ";
							t1 +=",借方";
							t2 +=",贷方";
							tt +="," + sName;
						}
	
						CTName[0][0] += t1 + t2 ;
						ColName[0][0] += tt + tt ;
	
						sRows="$2:$5";
						
						break;
					case 1:		//借方（贷方不分析）
						CTName[0][1] = ",";
						ColName[0][1]= ",";
	
						CTName[0][0] = "年,月,凭证日期,字,号,";
						ColName[0][0] = "年,月,凭证日期,字,号,";
	
						if("1".equals(toSubjects)){
							CTName[0][0] += "对方科目,";
							ColName[0][0] += "对方科目,";
						}
						
						CTName[0][0] += "摘要,借方,贷方,方向,余额";
						ColName[0][0] += "摘要,借方,贷方,方向,余额";
	
						t1="";
						t2="";
						tt="";
	
						for(int i=1;i < sAll.length; i++) {
							String sName = subjectEntry.getSName(acc,sAll[i]);
							s1 += ", debit"+i+" ";
//							s2 += ", credit"+i+" ";
							t1 +=",借方";
//							t2 +=",贷方";
							tt +="," + sName;
						}
	
						CTName[0][0] += t1  ;
						ColName[0][0] += tt ;
	
						sRows="$2:$5";
						
						break;
					case 2:		//借方（贷方以负数分析）
						CTName[0][1] = ",";
						ColName[0][1]= ",";
	
						CTName[0][0] = "年,月,凭证日期,字,号,";
						ColName[0][0] = "年,月,凭证日期,字,号,";
	
						if("1".equals(toSubjects)){
							CTName[0][0] += "对方科目,";
							ColName[0][0] += "对方科目,";
						}
						
						CTName[0][0] += "摘要,借方,贷方,方向,余额";
						ColName[0][0] += "摘要,借方,贷方,方向,余额";
	
						t1="";
						t2="";
						tt="";
	
						for(int i=1;i < sAll.length; i++) {
							String sName = subjectEntry.getSName(acc,sAll[i]);
							s1 += ", debit"+i+" ";
//							s2 += ", credit"+i+" ";
							t1 +=",借方";
//							t2 +=",贷方";
							tt +="," + sName;
						}
	
						CTName[0][0] += t1  ;
						ColName[0][0] += tt ;
	
						sRows="$2:$5";
	
						break;
					case 3:		//贷方（借方不分析）
						CTName[0][1] = ",";
						ColName[0][1]= ",";
	
						CTName[0][0] = "年,月,凭证日期,字,号,";
						ColName[0][0] = "年,月,凭证日期,字,号,";
	
						if("1".equals(toSubjects)){
							CTName[0][0] += "对方科目,";
							ColName[0][0] += "对方科目,";
						}
						
						CTName[0][0] += "摘要,借方,贷方,方向,余额";
						ColName[0][0] += "摘要,借方,贷方,方向,余额";
	
						t1="";
						t2="";
						tt="";
	
						for(int i=1;i < sAll.length; i++) {
							String sName = subjectEntry.getSName(acc,sAll[i]);
//							s1 += ", debit"+i+" ";
							s2 += ", credit"+i+" ";
//							t1 +=",借方";
							t2 +=",贷方";
							tt +="," + sName;
						}
	
						CTName[0][0] += t2  ;
						ColName[0][0] += tt ;
	
						sRows="$2:$5";
	
						break;
					case 4:		//贷方（借方以负数分析）
						CTName[0][1] = ",";
						ColName[0][1]= ",";
	
						CTName[0][0] = "年,月,凭证日期,字,号,";
						ColName[0][0] = "年,月,凭证日期,字,号,";
	
						if("1".equals(toSubjects)){
							CTName[0][0] += "对方科目,";
							ColName[0][0] += "对方科目,";
						}
						
						CTName[0][0] += "摘要,借方,贷方,方向,余额";
						ColName[0][0] += "摘要,借方,贷方,方向,余额";
	
						t1="";
						t2="";
						tt="";
	
						for(int i=1;i < sAll.length; i++) {
							String sName = subjectEntry.getSName(acc,sAll[i]);
//							s1 += ", debit"+i+" ";
							s2 += ", credit"+i+" ";
//							t1 +=",借方";
							t2 +=",贷方";
							tt +="," + sName;
						}
	
						CTName[0][0] += t2  ;
						ColName[0][0] += tt ;
	
						sRows="$2:$5";
						
						break;
				}
				
				printSetup.setStrQuerySqls(new String[]{"select * from ("+sql + s1 +s2 + " from ("+pp.getFinishSQLDeleteLimit()+") a where 1=1 "+txtSQL2+"  order by id "+txtSQL1+")a "+txtSQL });
				printSetup.setHeaders(CTName, ColName);
				printSetup.setStrSheetName("科目明细账-"+(t+1));

				filename.add( printSetup.getExcelFile());

				txtSQL = " limit ";
			}
			printSetup = null;
			//vpage strPrintTitleRows
			mapResult.put("refresh","");

			mapResult.put("saveasfilename","科目明细账");
			mapResult.put("bVpage","false");
			mapResult.put("strPrintTitleRows",sRows);
			mapResult.put("filenameList", filename);

		} catch (Exception e) {
			Debug.print(Debug.iError, "科目明细账打印失败!", e);
            throw e;
		} finally {
			DbUtil.close(conn);
		}
		return new ModelAndView("/Excel/tempdata/PrintandSave.jsp",mapResult);
	}
	
	/**
	 * 科目树(可以含核算)
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView getSubjectJSON(HttpServletRequest request,HttpServletResponse response) throws Exception {
		
		response.setContentType("text/html;charset=utf-8");
		
		ASFuntion CHF=new ASFuntion();
		
		Connection conn = null;

		try { 
			
			String accpackageId = CHF.showNull(request.getParameter("accpackageId"));
			String subjectFullName = CHF.showNull(request.getParameter("subjectFullName"));
			String level = CHF.showNull(request.getParameter("level"));
			
			String addAssItem = CHF.showNull(request.getParameter("addAssItem")); 	//增加核算为科目的下级
			if("".equals(addAssItem)) addAssItem = "1";
			
			String checked = CHF.showNull(request.getParameter("checked"));
			
			String isSubject = CHF.showNull(request.getParameter("isSubject"));	//判断这个是不是科目
			String accid = CHF.showNull(request.getParameter("accid"));	//判断这个是不是科目
			
			String noSubject = CHF.showNull(request.getParameter("noSubject")); //只显示有核算的科目
			
			conn = new DBConnect().getConnect("");
			new DBConnect().changeDataBaseByAccPackageId(accpackageId, conn);
			
			SubjectEntry subjectEntry =  new SubjectEntry(conn);
			String result;
			
			if(subjectFullName == null || "".equals(subjectFullName) || "undefined".equals(subjectFullName)) {
				//一级科目
				result = subjectEntry.getSubjectJSON(accpackageId,addAssItem,checked,noSubject);
			} else {
				//根据科目全路径和层次获得下级科目
				
				int iLevel = 0;
				try {
					iLevel = Integer.parseInt(level);
				} catch (Exception e) {
//					e.printStackTrace();
				}
				
				result = subjectEntry.getSubjectJSON(accpackageId, subjectFullName, iLevel,addAssItem,isSubject,accid,checked);
			}
			System.out.println("#####"+result);
			response.getWriter().write(result);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		
		
		return null;
	}
	
	/**
	 * 详细凭证
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView sortList(HttpServletRequest request,HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(_sortList);
		Connection conn = null;
		String sql = "";
		try {
			ASFuntion CHF = new ASFuntion();
			
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
			String user=CHF.showNull(userSession.getUserId());
			String projectid=CHF.showNull(userSession.getCurProjectId());
			
			String html = CHF.showNull(request.getParameter("html"));	//用于标志要不要用setTdtoValue　1为要
			
			String money = CHF.showNull(request.getParameter("money")); //	增加高亮显示金额
			String samCount = CHF.showNull(request.getParameter("samCount")); //分页用的（没有用了）

			String acc = CHF.showNull(request.getParameter("AccPackageID"));
			String VoucherID = CHF.showNull(request.getParameter("VoucherID"));			
			
			String VchDate = CHF.showNull(request.getParameter("VchDate")); 
			String TypeId = CHF.showNull(request.getParameter("TypeId")); 
			String OldVoucherId = CHF.showNull(request.getParameter("OldVoucherId"));
	
			String where = "";
			if(VoucherID==null || "".equals(VoucherID) || "undefined".equals(VoucherID)){
				if ("".equals(OldVoucherId)) {
					//临时增加，让人修改！
					throw new Exception("请在穿透中增加:[OldVoucherId] 参数!");
				}
				if ("".equals(VchDate)) {
					//临时增加，让人修改！
					throw new Exception("请在穿透中增加:[VchDate] 参数!");
				} 
				if ("".equals(TypeId)) {
					//临时增加，让人修改！
					throw new Exception("请在穿透中增加:[TypeId] 参数!");
				}
				
				where = "		and vchdate = '" + VchDate + "' " +
				"		and TypeID='" + TypeId + "' " +
				"		and oldvoucherid='" + OldVoucherId + "' " ;
				
			}else{
				where = "	and voucherid = '"+VoucherID+"' ";
			}
			
			sql = "select d.vchid as autoid1,a.*,SubjectID as SubjectID1," +
			"	if(Dirction=1,OccurValue,0) debit," +
			"	if(Dirction=-1,OccurValue,0) credit," +
			"	if(b.vid is null,'true','false') as _is_leaf," +
			"	'null' as _parent," +
			"	autoid as _id " +
			"	from c_subjectentry a " +
			"	left join ( " +
			"		select voucherid as vid,serail from c_assitementry " + 
			"		where 1=1 " +  
			where + 
			"		group by voucherid,serail " +
			"	) b on a.voucherid = b.vid and a.serail= b.serail " +
			
			" left join (select distinct vchid from z_voucherspotcheck  where projectid='" +projectid + "' and createor='" + user +"'   ) d on a.voucherid=d.vchid " + 
			
			
			
			"	where 1=1 " +   
			where + 
			"	order by a.serail ";
			
			conn = new DBConnect().getConnect(acc.substring(0,6));
			
			if(VoucherID==null || "".equals(VoucherID) || "undefined".equals(VoucherID)){
				
				where = "		and vchdate = '" + VchDate + "' " +
				"		and TypeID='" + TypeId + "' " +
				"		and voucherid='" + OldVoucherId + "' " ;
				
			}else{
				where = "	and autoid = '"+VoucherID+"' ";
			}
			
			Map map = new SubjectEntry(conn).getVoucher( acc, where);
			
//			map.put("VchDate", VchDate);
//			map.put("TypeID", TypeID);
//			map.put("VoucherID", VoucherID);
//			map.put("Director", Director);
//			map.put("FillUser", FillUser);
//			map.put("AuditUser", AuditUser);
//			map.put("KeepUser", KeepUser);
//			map.put("Property", property);	//是否结转
//			map.put("Currency", "1");	//有外币
//			map.put("UnitName", "1");	//有数量
			
			DataGridProperty pp = new DataGridProperty(){
				public ResultSet onExpand(HttpSession session, HttpServletRequest request, HttpServletResponse response, Connection conn) throws Exception {
					try {
						PreparedStatement ps = null;
						ResultSet rs = null;
						
						ASFuntion CHF=new ASFuntion();
						UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
						String user=CHF.showNull(userSession.getUserId());
						String projectid=CHF.showNull(userSession.getCurProjectId());
						
						String html = CHF.showNull(request.getParameter("html"));
						String tableId = CHF.showNull(request.getParameter("tableId"));
						
						String AccPackageID = CHF.showNull(request.getParameter("AccPackageID"));
						String VoucherID = CHF.showNull(request.getParameter("VoucherID"));
						String VchDate = CHF.showNull(request.getParameter("VchDate"));
						String TypeID = CHF.showNull(request.getParameter("TypeID"));
						String oldvoucherid = CHF.showNull(request.getParameter("oldvoucherid"));
						String Serail = CHF.showNull(request.getParameter("Serail"));
						
						String autoid = CHF.showNull(request.getParameter("autoid"));
//						[serail, summary, subjectid, subjectname1, debit, credit]
						String sql = "select vchid as autoid1,'"+autoid+"' as autoid2,a.*," +
						"	a.assitemid as subjectid1,AssTotalName as SubjectFullName1," +
						"	if(a.Dirction=1,a.AssItemSum,0) debit," +
						"	if(a.Dirction=-1,a.AssItemSum,0) credit," +
						"	'true' as _is_leaf," +
						"	'"+autoid+"' as _parent," +
						"	concat(autoid,'_0') as _id " +
						" from c_assitementry a " +
						
						" left join (select distinct vchid from z_voucherspotcheck  where projectid='" +projectid + "' and createor='" + user +"'   ) d on a.voucherid=d.vchid " + 
						
						
						" ,c_assitem b " +
						" where 1=1 " +
						" and VoucherID = '"+VoucherID+"' " +
						" and Serail = '"+Serail+"' " +
						
						" and a.AccPackageID = b.AccPackageID " +
						" and a.subjectid = b.AccID" +
						" and a.assitemid = b.assitemid " +
						" order by a.AutoId";
						System.out.println(sql);
						ps = conn.prepareStatement(sql);
						rs = ps.executeQuery();
						return rs;
					} catch (Exception e) {
						throw e;
					}
					
				}
			};
			pp.setTableID("sortList"+DELUnid.getNumUnid());
			pp.setCustomerId(acc.substring(0, 6));
			
			pp.setPageSize_CH(100);
			pp.setWhichFieldIsValue(2);
			pp.setCancelOrderby(true);
			pp.setUseBufferGrid(false) ;
			pp.setTrActionProperty(true);
			pp.setTrAction(" SubjectID='${SubjectID}' AssitemID = '${AssitemID}' autoid='${autoid}' AccPackageID='"+(String)map.get("AccPackageID")+"' VoucherID='${VoucherID}' VchDate='${VchDate}'  TypeID = '${TypeID}' oldvoucherid='${oldvoucherid}' Serail= '${Serail}' ");
		
			pp.setGridTreeId("serail");
			pp.setGridType("treeGrid");
			
			String setColumnWidth = "";
			
			System.out.println(acc+"|"+userSession.getCurAccPackageId()+"|"+userSession.getCurProjectId());
			try {
				String getCurAccPackageId = CHF.showNull(userSession.getCurAccPackageId());
				String getCurProjectBeginYear = CHF.showNull(userSession.getCurProjectBeginYear());
				String getCurProjectEndYear = CHF.showNull(userSession.getCurProjectEndYear());
				
				if(!"".equals(getCurAccPackageId)){
					getCurAccPackageId = getCurAccPackageId.substring(0, 6);
					int ProjectBeginYear = Integer.parseInt(getCurAccPackageId + getCurProjectBeginYear);
					int ProjectEndYear = Integer.parseInt(getCurAccPackageId + getCurProjectEndYear);
					/*
					if(ProjectBeginYear <= Integer.parseInt(acc) && Integer.parseInt(acc) <= ProjectEndYear && userSession.getCurProjectId()!=null && !"1".equals(html)){
						pp.addColumn("抽","autoid1",FormatType.showTakeOut );
						setColumnWidth = "3,";
					}
					*/
				}
			} catch (Exception e) {
				
			}
			
			setColumnWidth += "4,4,20,6,15"; 
			pp.setColumnWidth(setColumnWidth);
			
			if("1".equals(html)){
				pp.setTdtoValue("<a href='St://${value}'>${tdvalue}</a>");	
			}
			pp.addColumn("结", "autoid","","com.matech.audit.work.subjectentry.CarryProcess","");
			pp.addColumn("序号", "Serail");
			pp.addColumn("摘要", "Summary");
			pp.addColumn("科目编号", "SubjectID1");
			pp.addColumn("科目名称", "SubjectFullName1");
			
			pp.addColumn("借方金额", "debit","showMoney");
			pp.addColumn("贷方金额", "credit","showMoney");
			
			 //    type=1 : 没有外币，没有数量。
			 //    type=2 : 有外币，没有数量。
			 //    type=3 : 没有外币，有数量。
			 //    type=4 : 有外币，有数量。
			int type = 1;  //type=1 : 没有外币，没有数量。
			if("1".equals((String)map.get("Currency"))){
				type += 1; //    type=2 : 有外币，没有数量。
			 	pp.addColumn("汇率", "CurrRate","showMoney");
			 	pp.addColumn("外币金额", "CurrValue","showMoney");
			 	pp.addColumn("外币名称", "Currency");
			 	
			}
			
			if("1".equals((String)map.get("UnitName"))){
			 	type += 2;  //    type=3 : 没有外币，有数量。
			 	pp.addColumn("数量额", "Quantity","showMoney");
			 	pp.addColumn("单价", "UnitPrice","showMoney");
			 	pp.addColumn("计量单位", "UnitName");
			 	
			}
//		    type=4 : 有外币，有数量。
			
	//		pp.addColumn("结","autoid","showCarry" );
			
			pp.addInputValue("html");
			pp.addInputValue("money");
			pp.addInputValue("samCount");
			pp.addInputValue("AccPackageID");
			pp.addInputValue("VoucherID");
			pp.addInputValue("VchDate");
			pp.addInputValue("TypeId");
			pp.addInputValue("OldVoucherId");
			
			pp.setSQL(sql);
			
			pp.setPrintEnable(true);
			pp.setPrintCharColumn("1`2`3`4") ;
			
			request.getSession().setAttribute(DataGrid.sessionPre+pp.getTableID(),pp);
			
			modelAndView.addObject("html", html);
			modelAndView.addObject("money", money);
			modelAndView.addObject("samCount", samCount);
			
			modelAndView.addObject("AccPackageID", (String)map.get("AccPackageID"));
			modelAndView.addObject("DataGrid", pp.getTableID());
			
			if(VoucherID==null || "".equals(VoucherID) || "undefined".equals(VoucherID)){
				modelAndView.addObject("VoucherID", (String)map.get("AutoId"));
				modelAndView.addObject("VchDate", VchDate); 
				modelAndView.addObject("TypeId", TypeId);
				modelAndView.addObject("OldVoucherId", OldVoucherId);
			}else{
				modelAndView.addObject("VoucherID", VoucherID);
				
				modelAndView.addObject("VchDate", (String)map.get("VchDate")); 
				modelAndView.addObject("TypeId", (String)map.get("TypeID"));
				modelAndView.addObject("OldVoucherId", (String)map.get("VoucherID"));
				
			}
			
			modelAndView.addObject("Director", (String)map.get("Director"));
			modelAndView.addObject("FillUser", (String)map.get("FillUser"));
			modelAndView.addObject("AuditUser", (String)map.get("AuditUser"));
			modelAndView.addObject("KeepUser", (String)map.get("KeepUser"));
			modelAndView.addObject("Property", (String)map.get("Property"));//0为非结转，1为结转
			
			String isForwardButton = "设为结转凭证";
			if("1".equals((String)map.get("Property"))){
				isForwardButton = "设为非结转凭证";
			}
			modelAndView.addObject("isForwardButton", isForwardButton);
			
			modelAndView.addObject("CurrencyName", CHF.showNull(userSession.getCurChoiceCurrencyName()));
			
			modelAndView.addObject("projectID", CHF.showNull(userSession.getCurProjectId()));
			modelAndView.addObject("user", CHF.showNull(userSession.getUserId()));
			modelAndView.addObject("type", String.valueOf(type));
			
			return modelAndView;
			
		} catch (Exception e) {
			System.out.println("详细凭证 SQL:"+sql);
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		
	}
	
	
	/**
	 * 批量打印（重写4.0）,包括【核算】
	 */
	public ModelAndView printfile(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		ModelAndView modelAndView = new ModelAndView("/Excel/tempdata/PrintandSave.jsp");
		Connection conn=null;
		try {
			ASFuntion CHF=new ASFuntion();

			int iSheet = 10000;

			String T1 =(String)CHF.showNull(request.getParameter("T1"));
			String BeginYear =(String)CHF.showNull(request.getParameter("BeginYear"));
			String BeginDate = (String)CHF.showNull(request.getParameter("BeginDate"));
			String EndYear =(String)CHF.showNull(request.getParameter("EndYear"));
			String EndDate = (String)CHF.showNull(request.getParameter("EndDate"));

			String SubjectType = (String)CHF.showNull(request.getParameter("SubjectType"));	//0为1级，1为末级,2为末级 相同一级在同一个SHEET
			String bSubjectID = (String)CHF.showNull(request.getParameter("bSubjectID"));
			String eSubjectID = (String)CHF.showNull(request.getParameter("eSubjectID"));
//			String sqlwhere = (String)CHF.showNull(request.getParameter("sqlwhere"));
//			String subjectnameSql = (String)CHF.showNull(request.getParameter("subjectnameSql"));

			String Subjects = (String)CHF.showNull(request.getParameter("Subjects"));//科目编号
			String currency = (String)CHF.showNull(request.getParameter("currency"));//只显示本位币
			String toSubjects = (String)CHF.showNull(request.getParameter("toSubjects"));//显示对方科目
			String level0 = (String)CHF.showNull(request.getParameter("level0"));//显示对方科目的类型
			
			String sqlwhere = "";
			if(!toSubjects.equals("1")){
				sqlwhere = " and  1=2 ";
			}
			String subjectnameSql = "";
			if(level0.equals("1")){
				subjectnameSql = "if(locate('/',b.subjectfullname)=0,b.subjectfullname,substr(b.subjectfullname,1,locate('/',b.subjectfullname)-1))";
			}else{
				subjectnameSql =  "if(subjectname!=b.subjectfullname&&concat(if(locate('/',b.subjectfullname)=0,b.subjectfullname,substr(b.subjectfullname,1,locate('/',b.subjectfullname)-1)),'/',subjectname)!=b.subjectfullname \n"
					 +" ,concat(if(locate('/',b.subjectfullname)=0,b.subjectfullname,substr(b.subjectfullname,1,locate('/',b.subjectfullname)-1)),'/../',subjectname) \n"
					 +" ,b.subjectfullname) ";
			
			}
			
			conn = new DBConnect().getDirectConnect(T1);
			SubjectEntry subjectEntry = new SubjectEntry(conn);
			Assitem assitem = new Assitem(conn);
			
//			ArrayList args = subjectEntry.getSubjectList(T1,EndYear, EndDate, bSubjectID, eSubjectID, SubjectType);
			ArrayList args = subjectEntry.getSubjectList(T1,EndYear, EndDate, Subjects, SubjectType);
			ArrayList filename = new ArrayList();
			String [] temptable = null;
			
			String acc = T1 + EndYear;
			
//			String [] StrTitles = new String[iLevelCount]; //标题
//			String [] CharColumn = new String[iLevelCount];//字符列
//			String [] StrChineseTitles = new String[iLevelCount];//列名
//			String [] StrQuerySqls = new String[iLevelCount];//Sql
			
			String [][] CTName = new String[1][2];//复杂表头
			String [][] ColName = new String[1][2];//复杂表头
			
			CTName[0][1] = new String();
			CTName[0][1] = ",";
			ColName[0][1] = new String();
			ColName[0][1]= ",";
			CTName[0][0] = new String();
			ColName[0][0] = new String();

			String sql = "";
			String txtSQL = "";
//			int i = 0;
			if("2".equals(SubjectType)){
				/**
				 * 末级(包括核算)相同一级在同一个SHEET 
				 * 有三个可能，1、只有本位币，2、有外币，3、有外币有数量
				 */
				subjectEntry.setSplitBool(true);//用来标志：明细账多外币时，每个外币分开显示
				assitem.setSplitBool(true);
				
				//求出一级的个数
				ArrayList levelList = new ArrayList();
//				int iLevelCount = 0; //有多少个sheet
				String levelSid = "";
				for(int iSubject = 0; iSubject < args.size(); iSubject++){
					SubjectInfo si=(SubjectInfo)args.get(iSubject);
					if(!levelSid.equals(si.levelSubjectID)){
						levelList.add(si.levelSubjectID);
					}
					levelSid = si.levelSubjectID;
				}
				
				temptable = new String [levelList.size()]; //相同一级在同一个SHEET 
				String [] temp = new String [args.size()]; //打印科目 
				
				for(int i = 0; i< levelList.size();i++){
					temptable[i] = new String();
					temptable[i] = "tt_" + DELUnid.getCharUnid();
					
					PrintSetup printSetup = new PrintSetup(conn);
					printSetup.setStrTitles(new String[]{"明细账查询"});
					printSetup.setCharColumn(new String[]{"1`2`3`4`5`6`7"});
					
					levelSid = (String)levelList.get(i);
					boolean bool = true;
					
					//判断本sheet的3种情况
					int levelResult = 0;
					
					for(int iSubject = 0; iSubject < args.size(); iSubject++){
						SubjectInfo si=(SubjectInfo)args.get(iSubject);
						
						String opt = si.opt;//1为核算，其它为科目
						String AssItemID = si.assitemid; //核算
						String SubjectID =si.subjectid;		//科目ID
						String accName = si.accName;//名称
						String levelSubjectID = si.levelSubjectID;//一级科目
						String levelSubjectName = si.levelSubjectName;//一级科目
						
						temp[iSubject] = new String();
						temp[iSubject] = temptable[i] + "_" + iSubject;
						
						if(levelSid.equals(si.levelSubjectID)){
							if(bool){
								subjectEntry.CreateTableSheet(temptable[i]); //不相等时，加一个sheet
								String avalue = levelSubjectName + "("+levelSubjectID+")";
								printSetup.setStrSheetName(avalue);
								bool = false;
							}
						}else{
							continue;
						}
						
						//求出临时表
						if("1".equals(opt)){
							//核算
							Map Cmap = assitem.AssitemCurrency(acc, SubjectID, AssItemID);
							Map Umap = assitem.AssitemUnitName(acc, SubjectID, AssItemID);
							
							int result = assitem.AssitemProperty(acc, SubjectID, AssItemID);
							if("1".equals(currency)){//只显示本位币
								result = 0;
							}
							if(result >= levelResult){ //取最大值
								levelResult = result;
							}
							
							switch(result){
							case 0:
								assitem.CreateTable(temp[iSubject]);
								assitem.DataToTable(temp[iSubject],"", "", T1,SubjectID, AssItemID, BeginYear, BeginDate,EndYear, EndDate);
								
								break;
							case 1:	
								assitem.CreateTable(temp[iSubject],Cmap);
								assitem.DataToTable(Cmap,temp[iSubject],"", "", T1,SubjectID, AssItemID, BeginYear, BeginDate,EndYear, EndDate,1);
								break;
								
							case 2:
								assitem.CreateTable(temp[iSubject],Umap);				
								assitem.DataToTable(Umap,temp[iSubject],"", "", T1,SubjectID, AssItemID, BeginYear, BeginDate,EndYear, EndDate,2);
								break;
								
							case 3:
								assitem.CreateTable(temp[iSubject],Cmap,Umap);
								assitem.DataToTable(Cmap,temp[iSubject],"", "", T1,SubjectID, AssItemID, BeginYear, BeginDate,EndYear, EndDate,1);
								assitem.DataToTable(Umap,temp[iSubject],"", "", T1,SubjectID, AssItemID, BeginYear, BeginDate,EndYear, EndDate,2);
								break;
							}
							subjectEntry.DataToTable(temptable[i], temp[iSubject], result, opt);
							subjectEntry.DelTempTable(temp[iSubject]);//删除表
						}else{
							//科目
							
							int result = subjectEntry.SubjectProperty(acc,SubjectID);
							if("1".equals(currency)){//只显示本位币
								result = 0;
							}
							if(result >= levelResult){ //取最大值
								levelResult = result;
							}
							
							Map map = subjectEntry.SubjectCurrency(acc,SubjectID);
							Map Umap = subjectEntry.SubjectUnitName(acc,SubjectID);

							switch(result){
							case 0:		//本位币
								subjectEntry.CreateTable(temp[iSubject]);
								subjectEntry.DataToTable(temp[iSubject],"","",T1,SubjectID,BeginYear,BeginDate,EndYear,EndDate);
								break;
							case 1:			//外币
								subjectEntry.CreateTable(temp[iSubject],map);
								subjectEntry.DataToTable(map,temp[iSubject],"","",T1,SubjectID,BeginYear,BeginDate,EndYear,EndDate);
								break;
							case 2:		//数量
								
								subjectEntry.CreateUnitTable(temp[iSubject],Umap);
								subjectEntry.DataToUnitTable(Umap,temp[iSubject],"","",T1,SubjectID,BeginYear,BeginDate,EndYear,EndDate);
								break;
							case 3:		//外币与数量
								subjectEntry.CreateTable(temp[iSubject],map,Umap);
//								subjectEntry.DataToTable(map,Umap,temp[iSubject],"","",T1,SubjectID,BeginYear,BeginDate,EndYear,EndDate);
								subjectEntry.DataToTable(map,temp[iSubject],"","",T1,SubjectID,BeginYear,BeginDate,EndYear,EndDate);
								subjectEntry.DataToUnitTable(Umap,temp[iSubject],"","",T1,SubjectID,BeginYear,BeginDate,EndYear,EndDate);
								break;
							}
							subjectEntry.DataToTable(temptable[i], temp[iSubject], result, opt);
							subjectEntry.DelTempTable(temp[iSubject]);//删除表
						}
					}
					
					/**
					 * 打印(3种情况)
					 * 1、本位币
					 * 2、有外币
					 * 3、有数量
					 * 4、有外币和数量
					 */
					int TCount = 0;
					int TFor = 0;
					txtSQL = " limit ";
					switch(levelResult){
					case 0:		//本位币
						TCount = subjectEntry.getTempTableCount(temptable[i]);
						TFor = TCount / iSheet;

						for(int t = 0; t<= TFor; t++ ){

							txtSQL += String.valueOf(t * iSheet) + "," + iSheet ;
							
							String ChineseTitles = "年`月`凭证日期`字`号`发生科目`";
							sql = "select " +
							"IF(substring(a.vchdate,9)='00',vchyear,'') vchyear,IF(substring(a.vchdate,9)='00',vchmonth,'') vchmonth," +
							"case substring(a.vchdate,9) when '00' then '' when '97' then '' when '98' then '' else a.vchdate end vchdate," +
							"a.typeid,a.oldvoucherid,a.rsubject, ";

							if("".equals(sqlwhere)){
								ChineseTitles += "对方科目`";
								sql += "a.subjectnames,";
							}
							ChineseTitles += "摘要`借方发生额`贷方发生额`方向`余　额";
							sql += "a.summary," +
							"if(substring(a.vchdate,9)='00','',debit)  debit,if(substring(a.vchdate,9)='00','',credit) credit,IF(remain>0,'借',if(remain<0,'贷','平')) rec,ABS(remain) dateremain " +
							"from (select *,group_concat(distinct "+subjectnameSql+") subjectnames   from (select * from `"+temptable[i]+"` order by id "+txtSQL+") a left join (select * from c_accpkgsubject where  AccPackageID="+acc+" "+sqlwhere+" ) b  on 1=1 and a.subjects like concat('%,',b.subjectid,',%')  group by a.id  ) a   ";
							
							printSetup.setStrChineseTitles(new String[]{ChineseTitles});
							printSetup.setStrQuerySqls(new String[]{sql});

							filename.add(printSetup.getExcelFile());
							txtSQL = " limit ";
						}
						break;
					case 1:			//外币
					case 2:			//数量
					case 3:			//外币 + 数量
						TCount = subjectEntry.getTempTableCount(temptable[i]);
						TFor = TCount / iSheet;

						for(int t = 0; t<= TFor; t++ ){

							txtSQL += String.valueOf(t * iSheet) + "," + iSheet ;
							
							CTName[0][0] = "年,月,凭证日期,字,号,发生科目,";
							ColName[0][0] = "年,月,凭证日期,字,号,发生科目,";
							
							if("".equals(sqlwhere)){
								CTName[0][0] += "对方科目,";
								ColName[0][0] += "对方科目,";
							}
							CTName[0][0] += "摘要,";
							ColName[0][0] += "摘要,";
							
							sql = "select " +
							"IF(substring(a.vchdate,9)='00',vchyear,'') vchyear,IF(substring(a.vchdate,9)='00',vchmonth,'') vchmonth," +
							"case substring(a.vchdate,9) when '00' then '' when '97' then '' when '98' then '' else a.vchdate end vchdate," +
							"a.typeid,a.oldvoucherid,a.rsubject, ";

							if("".equals(sqlwhere)){
								sql += "a.subjectnames,";
							}
							sql += "a.summary,";
							
							if(levelResult == 2){
								CTName[0][0] += "借方,借方,借方,借方,";
								ColName[0][0] += "单位,单价,数量借发生,借方本位币,";
							}else if(levelResult == 3){
								CTName[0][0] += "借方,借方,借方,借方,";
								ColName[0][0] += "币种与数量,汇率与单价,借发生,借方本位币,";
							}else{
								CTName[0][0] += "借方,借方,借方,借方,";
								ColName[0][0] += "币种,汇率,原币借发生,借方本位币,";
							}
							
							sql += "a.rCurrency as debitRCurrency,if(substring(a.vchdate,9)='00','',debitRate) as  debitRate,if(substring(a.vchdate,9)='00','',debitCurr) as debitCurr ,if(substring(a.vchdate,9)='00','',debit) as debit,";
							
							if(levelResult == 2){
								CTName[0][0] += "贷方,贷方,贷方,贷方,";
								ColName[0][0] += "单位,单价,数量贷发生,贷方本位币,";
							}else if(levelResult == 3){
								CTName[0][0] += "贷方,贷方,贷方,贷方,";
								ColName[0][0] += "币种与数量,汇率与单价,贷发生,贷方本位币,";
							}else{
								CTName[0][0] += "贷方,贷方,贷方,贷方,";
								ColName[0][0] += "币种,汇率,原币贷发生,贷方本位币,";
							}
							sql += "a.rCurrency as creditRCurrency ,if(substring(a.vchdate,9)='00','',creditRate) as  creditRate,if(substring(a.vchdate,9)='00','',creditCurr) as creditCurr ,if(substring(a.vchdate,9)='00','',credit) as credit,";
							
							CTName[0][0] += "方向,";
							ColName[0][0] += "方向,";
							sql += "IF(a.remain>0,'借',if(a.remain<0,'贷','平')) rec,";

							if(levelResult == 2){
								CTName[0][0] += "余额,余额,余额";
								ColName[0][0] += "单位,数量余额,本位币余额";
							}else if(levelResult == 3){
								CTName[0][0] += "余额,余额,余额";
								ColName[0][0] += "币种与数量,币种与数量余额,本位币余额,";
							}else{
								CTName[0][0] += "余额,余额,余额";
								ColName[0][0] += "币种,原币余额,本位币余额";
							}
							sql += "a.rCurrency as currRCurrency,abs(a.currRemain) as currRemain,ABS(remain) dateRemain ";
							
							sql +="from (select *,group_concat(distinct "+subjectnameSql+") subjectnames   from (select * from `"+temptable[i]+"` order by id "+txtSQL+") a left join (select * from c_accpkgsubject where  AccPackageID="+acc+" "+sqlwhere+" ) b  on 1=1 and a.subjects like concat('%,',b.subjectid,',%')  group by a.id  ) a   "; 

							printSetup.setStrQuerySqls(new String[]{sql});
							printSetup.setHeaders(CTName, ColName);

							filename.add(printSetup.getExcelFile());
							txtSQL = " limit ";
						}
						
						break;
						
					}
					
					
				}
				
				
					
				
			}else{
				temptable = new String [args.size()];
				
				//1级或末级(包括核算) 单独一个SHEET
				for(int i = 0; i < args.size(); i++){
					SubjectInfo si=(SubjectInfo)args.get(i);
					
					String opt = si.opt;//1为核算，其它为科目
					String AssItemID = si.assitemid; //核算
					String SubjectID =si.subjectid;		//科目ID
					String accName = si.accName;//名称
					String levelSubjectID = si.levelSubjectID;//一级科目
					String levelSubjectName = si.levelSubjectName;//一级科目
					
					
					int TCount = 0;
					int TFor = 0;
					
					temptable[i] = new String();
					temptable[i] = "tt_" + DELUnid.getCharUnid();
					
					PrintSetup printSetup = new PrintSetup(conn);
					printSetup.setStrTitles(new String[]{"明细账查询"});
					printSetup.setCharColumn(new String[]{"1`2`3`4`5`6"});
					
					String avalue = "";
					if("1".equals(opt)){
						//核算
						avalue = SubjectID +"(核算："+accName+")";	//核算名称
						if(avalue.length()>=31){
							avalue = avalue.substring(0,30);
						}
						printSetup.setStrSheetName(avalue);
						
						sql = "select IF(substring(a.vchdate,9)='00',vchmonth,'') vchmonth," +
						"case substring(a.vchdate,9) when '00' then '' when '97' then '' when '98' then '' else a.vchdate end vchdate," +
						"typeid,oldvoucherid,assitemid,";
						if("".equals(sqlwhere)){
							sql +="subjectnames,";
						}
						sql += " summary,";
						
						Map Cmap = assitem.AssitemCurrency(acc, SubjectID, AssItemID);
						Map Umap = assitem.AssitemUnitName(acc, SubjectID, AssItemID);
						
						int result = assitem.AssitemProperty(acc, SubjectID, AssItemID);
						if("1".equals(currency)){//只显示本位币
							result = 0;
						}
						
						txtSQL = " limit ";
						
						switch(result){
						case 0:
							assitem.CreateTable(temptable[i]);
							assitem.DataToTable(temptable[i],"", "", T1,SubjectID, AssItemID, BeginYear, BeginDate,EndYear, EndDate);
							TCount = subjectEntry.getTempTableCount(temptable[i]);
							TFor = TCount / iSheet;

							for(int t = 0; t<= TFor; t++ ){	//分页
//									txtSQL += String.valueOf(t * iSheet + 1) + "," +  String.valueOf((t+1) * iSheet) ;
								txtSQL += String.valueOf(t * iSheet) + "," + iSheet ;
								
								if("".equals(sqlwhere)){
									printSetup.setStrChineseTitles(new String[]{"月`凭证日期`字`号`核算编号`对方科目`摘要`借方发生额`贷方发生额`方向`余　额"});
									printSetup.setIColumnWidths(new int[]{4,13,18,5,24,30,72,22,20,4,23});
								}else{
									printSetup.setStrChineseTitles(new String[]{"月`凭证日期`字`号`核算编号`摘要`借方发生额`贷方发生额`方向`余　额"});
									printSetup.setIColumnWidths(new int[]{4,13,18,5,24,72,22,20,4,23});
								}
								printSetup.setStrQuerySqls(new String[]{
										sql + 
										"debit,credit,if(dateremain<0,'贷','平') rec,dateremain " +
										"from (select *,group_concat(distinct "+subjectnameSql+") subjectnames   " +
										"from (select * from `"+temptable[i]+"` order by id "+txtSQL+") a " +
										"left join (select * from c_accpkgsubject where  AccPackageID="+acc+" "+sqlwhere+" ) b  " +
										"on 1=1 and a.subjects like concat('%,',b.subjectid,',%')  group by a.id  ) a "
									}
								);	
								
								filename.add(printSetup.getExcelFile());
								txtSQL = " limit ";
							}
							
							break;
						case 1:	
							assitem.CreateTable(temptable[i],Cmap);
							assitem.DataToTable(Cmap,temptable[i],"", "", T1,SubjectID, AssItemID, BeginYear, BeginDate,EndYear, EndDate,1);
							TCount = subjectEntry.getTempTableCount(temptable[i]);
							TFor = TCount / iSheet;

							for(int t = 0; t<= TFor; t++ ){	//分页
//									txtSQL += String.valueOf(t * iSheet + 1) + "," +  String.valueOf((t+1) * iSheet) ;
								txtSQL += String.valueOf(t * iSheet) + "," + iSheet ;
								
								CTName[0][0] = "月,凭证日期,字,号,核算编号,";
								ColName[0][0] = "月,凭证日期,字,号,核算编号,";
								if("".equals(sqlwhere)){
									CTName[0][0] += "对方科目,";
									ColName[0][0] += "对方科目,";
								}
								CTName[0][0] += "摘要,";
								ColName[0][0] += "摘要,";
								
								Set coll = Cmap.keySet();
								int ii =1;
								for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
									String key = (String) iter.next();
									String value = (String) Cmap.get(key);
									sql += "debitrate"+ii+",debit"+key+",";
									CTName[0][0] += "借方,借方,";
									ColName[0][0] += "汇率,"+value+",";
									ii ++;
								}
								sql += "debit,";
								CTName[0][0] += "借方,";
								ColName[0][0] += "借方本位币,";
								
								ii=1;
								for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
									String key = (String) iter.next();
									String value = (String) Cmap.get(key);
									sql += "creditrate"+ii+",credit"+key+",";
									CTName[0][0] += "贷方,贷方,";
									ColName[0][0] += "汇率,"+value+",";
									ii ++;
								}
								sql += "credit,if(dateremain<0,'贷','平') rec,";
								CTName[0][0] += "贷方,方向,";
								ColName[0][0] += "贷方本位币,方向,";
								
								ii=1;
								for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
									String key = (String) iter.next();
									String value = (String) Cmap.get(key);
									sql += "dRemain"+key+",";
									CTName[0][0] += "余额,";
									ColName[0][0] += value+",";
									ii ++;
								}
								sql += "dateRemain";
								CTName[0][0] += "余额";
								ColName[0][0] += "本位币余额";
								printSetup.setStrQuerySqls(new String[]{
										sql + 
										"	from (select *,group_concat(distinct "+subjectnameSql+") subjectnames   " +
										"	from (select * from `"+temptable[i]+"` order by id "+txtSQL+") a " +
										"	left join (select * from c_accpkgsubject where  AccPackageID="+acc+" "+sqlwhere+" ) b  " +
										"	on 1=1 and a.subjects like concat('%,',b.subjectid,',%')  group by a.id  ) a " 
									}
								);
								printSetup.setHeaders(CTName, ColName);
								
								filename.add(printSetup.getExcelFile());
								txtSQL = " limit ";
							}

							break;
							
						case 2:
							assitem.CreateTable(temptable[i],Umap);				
							assitem.DataToTable(Umap,temptable[i],"", "", T1,SubjectID, AssItemID, BeginYear, BeginDate,EndYear, EndDate,2);
							TCount = subjectEntry.getTempTableCount(temptable[i]);
							TFor = TCount / iSheet;

							for(int t = 0; t<= TFor; t++ ){	//分页
//									txtSQL += String.valueOf(t * iSheet + 1) + "," +  String.valueOf((t+1) * iSheet) ;
								txtSQL += String.valueOf(t * iSheet) + "," + iSheet ;
								
								CTName[0][1] = ",";
								ColName[0][1]= ",";
								
								CTName[0][0] = "月,凭证日期,字,号,核算编号,";
								ColName[0][0] = "月,凭证日期,字,号,核算编号,";
								if("".equals(sqlwhere)){
									CTName[0][0] += "对方科目,";
									ColName[0][0] += "对方科目,";
								}
								CTName[0][0] += "摘要,";
								ColName[0][0] += "摘要,";
								
								Set coll = Umap.keySet();
								int ii =1;
								for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
									String key = (String) iter.next();
									String value = (String) Umap.get(key);
									sql += "debitrate"+ii+",debit"+key+",";
									CTName[0][0] += "借方,借方,";
									ColName[0][0] += "单价,"+value+",";
									ii ++;
								}
								sql += "debit,";
								CTName[0][0] += "借方,";
								ColName[0][0] += "借方本位币,";
								
								ii=1;
								for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
									String key = (String) iter.next();
									String value = (String) Umap.get(key);
									sql += "creditrate"+ii+",credit"+key+",";
									CTName[0][0] += "贷方,贷方,";
									ColName[0][0] += "单价,"+value+",";
									ii ++;
								}
								sql += "credit,if(dateremain<0,'贷','平') rec,";
								CTName[0][0] += "贷方,方向,";
								ColName[0][0] += "贷方本位币,方向,";
								
								ii=1;
								for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
									String key = (String) iter.next();
									String value = (String) Umap.get(key);
									sql += "dRemain"+key+",";
									CTName[0][0] += "余额,";
									ColName[0][0] += value+",";
									ii ++;
								}
								sql += "dateRemain";
								CTName[0][0] += "余额";
								ColName[0][0] += "本位币余额";
//									System.out.println("sk:打印sql 2 \n"+sql + " from "+temptable[i]+" a where 1=1   order by id " );
								printSetup.setStrQuerySqls(new String[]{
										sql + 
										"	from (select *,group_concat(distinct "+subjectnameSql+") subjectnames   " +
										"	from (select * from `"+temptable[i]+"` order by id "+txtSQL+") a " +
										"	left join (select * from c_accpkgsubject where  AccPackageID="+acc+" "+sqlwhere+" ) b  " +
										"	on 1=1 and a.subjects like concat('%,',b.subjectid,',%')  group by a.id  ) a " 
									}
								);
								printSetup.setHeaders(CTName, ColName);
								
								filename.add(printSetup.getExcelFile());
								txtSQL = " limit ";
							}
							
							break;
							
						case 3:
							assitem.CreateTable(temptable[i],Cmap,Umap);
							assitem.DataToTable(Cmap,Umap,temptable[i],"", "", T1,SubjectID, AssItemID, BeginYear, BeginDate,EndYear, EndDate);
							TCount = subjectEntry.getTempTableCount(temptable[i]);
							TFor = TCount / iSheet;

							for(int t = 0; t<= TFor; t++ ){	//分页
//									txtSQL += String.valueOf(t * iSheet + 1) + "," +  String.valueOf((t+1) * iSheet) ;
								txtSQL += String.valueOf(t * iSheet) + "," + iSheet ;
								
								CTName[0][1] = ",";
								ColName[0][1]= ",";
								
								CTName[0][0] = "月,凭证日期,字,号,核算编号,";
								ColName[0][0] = "月,凭证日期,字,号,核算编号,";
								if("".equals(sqlwhere)){
									CTName[0][0] += "对方科目,";
									ColName[0][0] += "对方科目,";
								}
								CTName[0][0] += "摘要,";
								ColName[0][0] += "摘要,";
								
								Set Ucoll = Umap.keySet();
								Set Ccoll = Cmap.keySet();
								
								int ii =1;
								for (Iterator iter = Ucoll.iterator(); iter.hasNext(); ) {
									String key = (String) iter.next();
									String value = (String) Umap.get(key);
									sql += "debitPrice"+ii+",debit"+key+",";
									CTName[0][0] += "借方,借方,";
									ColName[0][0] += "单价,"+value+",";
									ii ++;						
								}
								ii =1;
								for (Iterator iter = Ccoll.iterator(); iter.hasNext(); ) {
									String key = (String) iter.next();
									String value = (String) Cmap.get(key);
									sql += "debitrate"+ii+",debit"+key+",";
									CTName[0][0] += "借方,借方,";
									ColName[0][0] += "汇率,"+value+",";
									ii ++;
								}
								sql += "debit,";
								CTName[0][0] += "借方,";
								ColName[0][0] += "借方本位币,";
								
								ii=1;
								for (Iterator iter = Ucoll.iterator(); iter.hasNext(); ) {
									String key = (String) iter.next();
									String value = (String) Umap.get(key);
									sql += "creditPrice"+ii+",credit"+key+",";
									CTName[0][0] += "贷方,贷方,";
									ColName[0][0] += "单价,"+value+",";
									ii ++;
								}
								ii=1;
								for (Iterator iter = Ccoll.iterator(); iter.hasNext(); ) {
									String key = (String) iter.next();
									String value = (String) Cmap.get(key);
									sql += "creditrate"+ii+",credit"+key+",";
									CTName[0][0] += "贷方,贷方,";
									ColName[0][0] += "汇率,"+value+",";
									ii ++;
								}
								sql += "credit,if(dateremain<0,'贷','平') rec,";
								CTName[0][0] += "贷方,方向,";
								ColName[0][0] += "贷方本位币,方向,";
								
								ii=1;
								for (Iterator iter = Ucoll.iterator(); iter.hasNext(); ) {
									String key = (String) iter.next();
									String value = (String) Umap.get(key);
									sql += "dRemain"+key+",";
									CTName[0][0] += "余额,";
									ColName[0][0] += value+",";
									ii ++;
								}
								ii=1;
								for (Iterator iter = Ccoll.iterator(); iter.hasNext(); ) {
									String key = (String) iter.next();
									String value = (String) Cmap.get(key);
									sql += "dRemain"+key+",";
									CTName[0][0] += "余额,";
									ColName[0][0] += value+",";
									ii ++;
								}
								sql += "dateRemain";
								CTName[0][0] += "余额";
								ColName[0][0] += "本位币余额";
								printSetup.setStrQuerySqls(new String[]{
										sql + 
										"	from (select *,group_concat(distinct "+subjectnameSql+") subjectnames   " +
										"	from (select * from `"+temptable[i]+"` order by id "+txtSQL+") a " +
										"	left join (select * from c_accpkgsubject where  AccPackageID="+acc+" "+sqlwhere+" ) b  " +
										"on 1=1 and a.subjects like concat('%,',b.subjectid,',%')  group by a.id  ) a " 
									}
								);
								printSetup.setHeaders(CTName, ColName);
								
								filename.add(printSetup.getExcelFile());
								txtSQL = " limit ";
								
							}
							
							break;
						}
						
					}else{
						//科目 
						avalue = accName+"("+SubjectID+")";	//科目名称
						if(avalue.length()>=31){
							avalue = avalue.substring(0,30);
						}
						printSetup.setStrSheetName(avalue);
						
						int result = subjectEntry.SubjectProperty(acc,SubjectID);
						if("1".equals(currency)){//只显示本位币
							result = 0;
						}
						
						Map map = subjectEntry.SubjectCurrency(acc,SubjectID);
						Map Umap = subjectEntry.SubjectUnitName(acc,SubjectID);
						Set coll = null;

						txtSQL = " limit ";
						switch(result){
						case 0:		//本位币
							subjectEntry.CreateTable(temptable[i]);
							subjectEntry.DataToTable(temptable[i],"","",T1,SubjectID,BeginYear,BeginDate,EndYear,EndDate);
							TCount = subjectEntry.getTempTableCount(temptable[i]);
							TFor = TCount / iSheet;

							for(int t = 0; t<= TFor; t++ ){

//									txtSQL += String.valueOf(t * iSheet + 1) + "," +  String.valueOf((t+1) * iSheet) ;
								txtSQL += String.valueOf(t * iSheet) + "," + iSheet ;
								if("".equals(sqlwhere)){
									printSetup.setStrChineseTitles(new String[]{"年`月`凭证日期`字`号`对方科目`摘要`借方发生额`贷方发生额`方向`余　额"});
									printSetup.setIColumnWidths(new int[]{6,4,13,6,6,30,46,25,25,4,25});
									printSetup.setCharColumn(new String[]{"1`2`3`4`5`6`7"});
									printSetup.setStrQuerySqls(new String[]{
											"select " +
											"IF(substring(a.vchdate,9)='00',vchyear,'') vchyear," +
											"IF(substring(a.vchdate,9)='00',vchmonth,'') vchmonth," +
											"case substring(a.vchdate,9) when '00' then '' when '97' then '' when '98' then '' else a.vchdate end vchdate," +
											"a.typeid,a.oldvoucherid,subjectnames,a.summary," +
											"if(substring(a.vchdate,9)='00','',debit)  debit,if(substring(a.vchdate,9)='00','',credit) credit,IF(dateremain>0,'借',if(dateremain<0,'贷','平')) rec,ABS(dateremain) dateremain " +
											"from (select *,group_concat(distinct "+subjectnameSql+") subjectnames   from (select * from `"+temptable[i]+"` order by id "+txtSQL+") a left join (select * from c_accpkgsubject where  AccPackageID="+acc+" "+sqlwhere+" ) b  on 1=1 and a.subjects like concat('%,',b.subjectid,',%')  group by a.id  ) a   " 
										});
									
								}else{
									printSetup.setStrChineseTitles(new String[]{"年`月`凭证日期`字`号`摘要`借方发生额`贷方发生额`方向`余　额"});
									printSetup.setIColumnWidths(new int[]{6,4,13,6,6,46,25,25,4,25});
									printSetup.setStrQuerySqls(new String[]{
											"select " +
											"IF(substring(a.vchdate,9)='00',vchyear,'') vchyear," +
											"IF(substring(a.vchdate,9)='00',vchmonth,'') vchmonth," +
											"case substring(a.vchdate,9) when '00' then '' when '97' then '' when '98' then '' else a.vchdate end vchdate," +
											"a.typeid,a.oldvoucherid,a.summary," +
											"if(substring(a.vchdate,9)='00','',debit)  debit,if(substring(a.vchdate,9)='00','',credit) credit,IF(dateremain>0,'借',if(dateremain<0,'贷','平')) rec,ABS(dateremain) dateremain " +
											"from (select *,group_concat(distinct "+subjectnameSql+") subjectnames   from (select * from `"+temptable[i]+"` order by id "+txtSQL+") a left join (select * from c_accpkgsubject where  AccPackageID="+acc+" "+sqlwhere+" ) b  on 1=1 and a.subjects like concat('%,',b.subjectid,',%')  group by a.id  ) a   " 
										});
								}
							
							
								
//									sRows = "$2:$4";

								filename.add(printSetup.getExcelFile());
								txtSQL = " limit ";
							}

							break;
						case 1:			//外币
							subjectEntry.CreateTable(temptable[i],map);
							subjectEntry.DataToTable(map,temptable[i],"","",T1,SubjectID,BeginYear,BeginDate,EndYear,EndDate);
							TCount = subjectEntry.getTempTableCount(temptable[i]);
							TFor = TCount / iSheet;

							for(int t = 0; t<= TFor; t++ ){
//									txtSQL += String.valueOf(t * iSheet + 1) + "," +  String.valueOf((t+1) * iSheet) ;
								txtSQL += String.valueOf(t * iSheet) + "," + iSheet ;
								
								CTName[0][0] = "年,月,凭证日期,字,号,";
								ColName[0][0] = "年,月,凭证日期,字,号,";
								
								if("".equals(sqlwhere)){
									CTName[0][0] += "对方科目,";
									ColName[0][0] += "对方科目,";
								}
								CTName[0][0] += "摘要,";
								ColName[0][0] += "摘要,";
								
								sql = "select " +
								"IF(substring(a.vchdate,9)='00',vchyear,'') vchyear,IF(substring(a.vchdate,9)='00',vchmonth,'') vchmonth," +
								"case substring(a.vchdate,9) when '00' then '' when '97' then '' when '98' then '' else a.vchdate end vchdate," +
								"a.typeid,a.oldvoucherid, ";

								if("".equals(sqlwhere)){
									sql += "a.subjectnames,";
								}
								sql += "a.summary,";
								coll = map.keySet();
								int ii =1;
								for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
									String key = (String) iter.next();
									String value = (String) map.get(key);
									sql += "if(substring(a.vchdate,9)='00','',debitrate"+ii+") debitrate"+ii+",if(substring(a.vchdate,9)='00','',debit"+key+") debit"+key+",";
									CTName[0][0] += "借方,借方,";
									ColName[0][0] += "汇率,"+value+",";
									ii ++;
								}
								sql += "if(substring(a.vchdate,9)='00','',debit) debit,";
								CTName[0][0] += "借方,";
								ColName[0][0] += "借方本位币,";

								ii=1;
								for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
									String key = (String) iter.next();
									String value = (String) map.get(key);
									sql += "if(substring(a.vchdate,9)='00','',creditrate"+ii+") creditrate"+ii+",if(substring(a.vchdate,9)='00','',credit"+key+") credit"+key+",";
									CTName[0][0] += "贷方,贷方,";
									ColName[0][0] += "汇率,"+value+",";
									ii ++;
								}
								sql += "if(substring(a.vchdate,9)='00','',credit) credit,IF(dateremain>0,'借',if(dateremain<0,'贷','平')) rec,";
								CTName[0][0] += "贷方,方向,";
								ColName[0][0] += "贷方本位币,方向,";

								ii=1;
								for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
									String key = (String) iter.next();
									String value = (String) map.get(key);
									sql += "abs(dRemain"+key+") dRemain"+key+",";
									CTName[0][0] += "余额,";
									ColName[0][0] += value+",";
									ii ++;
								}
								sql +="ABS(dateRemain) dateRemain from (select *,group_concat(distinct "+subjectnameSql+") subjectnames   from (select * from `"+temptable[i]+"` order by id "+txtSQL+") a left join (select * from c_accpkgsubject where  AccPackageID="+acc+" "+sqlwhere+" ) b  on 1=1 and a.subjects like concat('%,',b.subjectid,',%')  group by a.id  ) a   "; 
								CTName[0][0] += "余额";
								ColName[0][0] += "本位币余额";

								printSetup.setStrQuerySqls(new String[]{sql});
								printSetup.setHeaders(CTName, ColName);

//									sRows = "$2:$5";
								filename.add(printSetup.getExcelFile());
								txtSQL = " limit ";

							}


							break;
						case 2:		//数量
							subjectEntry.CreateUnitTable(temptable[i],Umap);
							subjectEntry.DataToUnitTable(Umap,temptable[i],"","",T1,SubjectID,BeginYear,BeginDate,EndYear,EndDate);
							TCount = subjectEntry.getTempTableCount(temptable[i]);
							TFor = TCount / iSheet;

							for(int t = 0; t<= TFor; t++ ){
//									txtSQL += String.valueOf(t * iSheet + 1) + "," +  String.valueOf((t+1) * iSheet) ;
								txtSQL += String.valueOf(t * iSheet) + "," + iSheet ;
								
								CTName[0][0] = "年,月,凭证日期,字,号,";
								ColName[0][0] = "年,月,凭证日期,字,号,";
								
								if("".equals(sqlwhere)){
									CTName[0][0] += "对方科目,";
									ColName[0][0] += "对方科目,";
								}
								CTName[0][0] += "摘要,";
								ColName[0][0] += "摘要,";

								sql = "select " +
								"IF(substring(a.vchdate,9)='00',vchyear,'') vchyear,IF(substring(a.vchdate,9)='00',vchmonth,'') vchmonth," +
								"case substring(a.vchdate,9) when '00' then '' when '97' then '' when '98' then '' else a.vchdate end vchdate," +
								"a.typeid,a.oldvoucherid, ";

								if("".equals(sqlwhere)){
									sql += "a.subjectnames,";
								}
								sql += "a.summary,";
								coll = Umap.keySet();
								int ii =1;
								for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
									String key = (String) iter.next();
									String value = (String) Umap.get(key);
									sql += "if(substring(a.vchdate,9)='00','',debitPrice"+ii+") debitPrice"+ii+",if(substring(a.vchdate,9)='00','',debit"+key+") debit"+key+",";
									CTName[0][0] += "借方,借方,";
									ColName[0][0] += "单价,"+value+",";
									ii ++;
								}
								sql += "if(substring(a.vchdate,9)='00','',debit) debit,";
								CTName[0][0] += "借方,";
								ColName[0][0] += "借方本位币,";

								ii=1;
								for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
									String key = (String) iter.next();
									String value = (String) Umap.get(key);
									sql += "if(substring(a.vchdate,9)='00','',creditPrice"+ii+") creditPrice"+ii+",if(substring(a.vchdate,9)='00','',credit"+key+") credit"+key+",";
									CTName[0][0] += "贷方,贷方,";
									ColName[0][0] += "单价,"+value+",";
									ii ++;
								}
								sql += "if(substring(a.vchdate,9)='00','',credit) credit,IF(dateremain>0,'借',if(dateremain<0,'贷','平')) rec,";
								CTName[0][0] += "贷方,方向,";
								ColName[0][0] += "贷方本位币,方向,";

								ii=1;
								for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
									String key = (String) iter.next();
									String value = (String) Umap.get(key);
									sql += "abs(dRemain"+key+") dRemain"+key+",";
									CTName[0][0] += "余额,";
									ColName[0][0] += value+",";
									ii ++;
								}
								sql +="ABS(dateRemain) dateRemain  from (select *,group_concat(distinct "+subjectnameSql+") subjectnames   from (select * from `"+temptable[i]+"` order by id "+txtSQL+") a left join (select * from c_accpkgsubject where  AccPackageID="+acc+" "+sqlwhere+" ) b  on 1=1 and a.subjects like concat('%,',b.subjectid,',%')  group by a.id  ) a   "; 
								CTName[0][0] += "余额";
								ColName[0][0] += "本位币余额";

								printSetup.setStrQuerySqls(new String[]{sql});
								printSetup.setHeaders(CTName, ColName);

//									sRows = "$2:$5";
								filename.add(printSetup.getExcelFile());
								txtSQL = " limit ";
							}


							break;
						case 3:		//外币与数量
							subjectEntry.CreateTable(temptable[i],map,Umap);
							subjectEntry.DataToTable(map,Umap,temptable[i],"","",T1,SubjectID,BeginYear,BeginDate,EndYear,EndDate);
							TCount = subjectEntry.getTempTableCount(temptable[i]);
							TFor = TCount / iSheet;

							for(int t = 0; t<= TFor; t++ ){
//									txtSQL += String.valueOf(t * iSheet + 1) + "," +  String.valueOf((t+1) * iSheet) ;
								txtSQL += String.valueOf(t * iSheet) + "," + iSheet ;
								
								CTName[0][0] = "年,月,凭证日期,字,号,";
								ColName[0][0] = "年,月,凭证日期,字,号,";
								
								if("".equals(sqlwhere)){
									CTName[0][0] += "对方科目,";
									ColName[0][0] += "对方科目,";
								}
								CTName[0][0] += "摘要,";
								ColName[0][0] += "摘要,";
								sql = "select " +
								"IF(substring(a.vchdate,9)='00',vchyear,'') vchyear,IF(substring(a.vchdate,9)='00',vchmonth,'') vchmonth," +
								"case substring(a.vchdate,9) when '00' then '' when '97' then '' when '98' then '' else a.vchdate end vchdate," +
								"a.typeid,a.oldvoucherid, ";

								if("".equals(sqlwhere)){
									sql += "a.subjectnames,";
								}
								sql += "a.summary,";

								Set Ucoll = Umap.keySet();
								Set Ccoll = map.keySet();

								int ii =1;
								for (Iterator iter = Ucoll.iterator(); iter.hasNext(); ) {
									String key = (String) iter.next();
									String value = (String) Umap.get(key);
									sql += "if(substring(a.vchdate,9)='00','',debitPrice"+ii+") debitPrice"+ii+",if(substring(a.vchdate,9)='00','',debit"+key+") debit"+key+",";
									CTName[0][0] += "借方,借方,";
									ColName[0][0] += "单价,"+value+",";
									ii ++;
								}
								ii =1;
								for (Iterator iter = Ccoll.iterator(); iter.hasNext(); ) {
									String key = (String) iter.next();
									String value = (String) map.get(key);
									sql += "if(substring(a.vchdate,9)='00','',debitrate"+ii+") debitrate"+ii+",if(substring(a.vchdate,9)='00','',debit"+key+") debit"+key+",";
									CTName[0][0] += "借方,借方,";
									ColName[0][0] += "汇率,"+value+",";
									ii ++;
								}
								sql += "if(substring(a.vchdate,9)='00','',debit) debit,";
								CTName[0][0] += "借方,";
								ColName[0][0] += "借方本位币,";

								ii=1;
								for (Iterator iter = Ucoll.iterator(); iter.hasNext(); ) {
									String key = (String) iter.next();
									String value = (String) Umap.get(key);
									sql += "if(substring(a.vchdate,9)='00','',creditPrice"+ii+") creditPrice"+ii+",if(substring(a.vchdate,9)='00','',credit"+key+") credit"+key+",";
									CTName[0][0] += "贷方,贷方,";
									ColName[0][0] += "单价,"+value+",";
									ii ++;
								}
								ii=1;
								for (Iterator iter = Ccoll.iterator(); iter.hasNext(); ) {
									String key = (String) iter.next();
									String value = (String) map.get(key);
									sql += "if(substring(a.vchdate,9)='00','',creditrate"+ii+") creditrate"+ii+",if(substring(a.vchdate,9)='00','',credit"+key+") credit"+key+",";
									CTName[0][0] += "贷方,贷方,";
									ColName[0][0] += "汇率,"+value+",";
									ii ++;
								}
								sql += "if(substring(a.vchdate,9)='00','',credit) credit,IF(dateremain>0,'借',if(dateremain<0,'贷','平')) rec,";
								CTName[0][0] += "贷方,方向,";
								ColName[0][0] += "贷方本位币,方向,";

								ii=1;
								for (Iterator iter = Ucoll.iterator(); iter.hasNext(); ) {
									String key = (String) iter.next();
									String value = (String) Umap.get(key);
									sql += "abs(dRemain"+key+") dRemain"+key+",";
									CTName[0][0] += "余额,";
									ColName[0][0] += value+",";
									ii ++;
								}
								ii=1;
								for (Iterator iter = Ccoll.iterator(); iter.hasNext(); ) {
									String key = (String) iter.next();
									String value = (String) map.get(key);
									sql += "abs(dRemain"+key+") dRemain"+key+",";
									CTName[0][0] += "余额,";
									ColName[0][0] += value+",";
									ii ++;
								}
								sql +="ABS(dateRemain) dateRemain  from (select *,group_concat(distinct "+subjectnameSql+") subjectnames   from (select * from `"+temptable[i]+"` order by id "+txtSQL+") a left join (select * from c_accpkgsubject where  AccPackageID="+acc+" "+sqlwhere+" ) b  on 1=1 and a.subjects like concat('%,',b.subjectid,',%')  group by a.id  ) a   "; 
								CTName[0][0] += "余额";
								ColName[0][0] += "本位币余额";

								printSetup.setStrQuerySqls(new String[]{sql });
								printSetup.setHeaders(CTName, ColName);

//									sRows = "$2:$5";
								filename.add(printSetup.getExcelFile());
								txtSQL = " limit ";
							}


							break;

						}

						i ++;
						
					} //exit 科目
						
				} //exit for
				
			}
			
			//删除临时表
			for(int ii=0;ii<temptable.length;ii++){
				String TabName = temptable[ii];
				if(!"".equals(TabName)){
					subjectEntry.DelTempTable(TabName);
				}
			}

			modelAndView.addObject("filenameList", filename);
			modelAndView.addObject("refresh","");

			modelAndView.addObject("saveasfilename","科目明细账");
			modelAndView.addObject("bVpage","false");

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		return modelAndView;
	}
	
	
}

