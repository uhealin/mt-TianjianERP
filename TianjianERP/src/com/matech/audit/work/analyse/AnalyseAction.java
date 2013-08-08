package com.matech.audit.work.analyse;

import java.io.PrintWriter;
import java.sql.Connection;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.pub.func.ASTextKey;
import com.matech.audit.service.analyse.AnalyseService;
import com.matech.audit.work.analyse.ECStatisticsAction.Jsp;

import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.datagrid.FormatType;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.service.print.PrintSetup;

public class AnalyseAction extends MultiActionController{
	
	
	protected enum Jsp {
		ecStatist;
		
		public String getPath(){
			return MessageFormat.format("/analyse/{0}.jsp", this.name());
		}
	}
	
	private final String _strSubject = "/analyse/SubjectList.jsp";
	private final String _strIList = "/analyse/IList.jsp";
	private final String _strAssItem = "/analyse/AssItemList.jsp";
	private final String _strAList = "/analyse/AList.jsp";
	private final String _strDay = "/analyse/Day.jsp";
	
	
	private final String _strList = "/analyse/List.jsp";
	
	public ModelAndView day(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		ModelAndView modelAndView = new ModelAndView(_strDay);
		Connection conn=null;
		try {
			ASFuntion CHF=new ASFuntion();
			String sDate = CHF.showNull(request.getParameter("sDate"));
			String acc1 = CHF.showNull(request.getParameter("AccPackageID"));
			if("".equals(sDate)){
				DataGridProperty pp = new DataGridProperty();
				pp.setTableID("day");
				pp.setCustomerId("");
				pp.setPageSize_CH(500);
				pp.setWhichFieldIsValue(1);
				pp.setCancelOrderby(true);
				pp.setCancelPage(true);
				pp.setInputType("checkbox");
				
				String sql = "select concat(name,'`',value) as value1, name,value from k_dic where `ctype`='新账龄区间' order by Autoid ";
				
				pp.addColumn("区间说明", "name");
				pp.addColumn("相隔月份", "value");
				
				pp.setSQL(sql);
				request.getSession().setAttribute(DataGrid.sessionPre+pp.getTableID(),pp);
				
				modelAndView.addObject("AccPackageID", acc1);
				
			}else{
				response.setContentType("text/html;charset=utf-8");
				PrintWriter out = response.getWriter();
				
				UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
				String acc = userSession.getCurAccPackageId();
				if(acc == null) acc = "";
				
				
				if("".equals(acc1)){
					acc1 = userSession.getCurChoiceAccPackageId();
				}
				
				if("".equals(acc1))acc1 = acc;
				if(acc.equals(acc1)){
					String Projectid =  userSession.getCurProjectId();					
					if(Projectid!=null && !"".equals(Projectid) && !"null".equals(Projectid)){
						conn = new DBConnect().getConnect(acc.substring(0,6));
						new AnalyseService(conn).saveDay(sDate, Projectid);
						out.print("区间设置成功！");
					}else{
						out.print("注意：因为您没有选择项目,区间设置只对当前分析有效！");
					}
				}else{
					out.print("注意：因为您选择帐套编号与当前项目的帐套编号不同,区间设置只对当前分析有效！");
				}
				out.close();
				return null;	
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}		
		return modelAndView;		
	}
	
	public ModelAndView subjectlist1(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		ModelAndView modelAndView = new ModelAndView(_strSubject);
		Connection conn=null;
		try {
			ASFuntion CHF=new ASFuntion();
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
						
			String acc = "";
			String CustomerID = CHF.showNull(request.getParameter("CustomerID"));
			String EndYear = CHF.showNull(request.getParameter("EndYear"));
			String EndMonth = CHF.showNull(request.getParameter("EndMonth"));
			
			if(!"".equals(CustomerID) && !"".equals(EndYear)){
				acc = CustomerID + EndYear;
			}
			
			if("".equals(acc)){
				acc = CHF.showNull(userSession.getCurChoiceAccPackageId());
				if("".equals(acc)){
					acc = CHF.showNull(userSession.getCurAccPackageId());
				}
			}
			
			if(!"".equals(acc)){
				if("".equals(CustomerID)){
					CustomerID=acc.substring(0,6);
				}
				EndYear = acc.substring(6);
			}

			if("".equals(EndYear)){
				EndYear = userSession.getCurChoiceEndYear();
			}
			
			if("".equals(EndMonth)){
				EndMonth = userSession.getCurChoiceEndMonth();
			}
			
			if("".equals(EndMonth)){
				EndMonth = "12";
			}
			
			conn = new DBConnect().getConnect(CustomerID);
			
			String SubjectID = CHF.showNull(request.getParameter("SubjectID"));

			modelAndView.addObject("CustomerID", CustomerID);
			modelAndView.addObject("EndYear", EndYear);
			modelAndView.addObject("EndMonth", EndMonth);
			modelAndView.addObject("SubjectID", SubjectID);
			String TName = "";
			if(!"".equals(CustomerID)){
				TName = new ASTextKey(conn).TextCustomerName(CustomerID);
			}			
			modelAndView.addObject("TName", TName);
			
			DataGridProperty pp = new DataGridProperty(){
				public void onSearch (
	    				javax.servlet.http.HttpSession session,
	    				javax.servlet.http.HttpServletRequest request,
	    				javax.servlet.http.HttpServletResponse response) throws Exception{
					ASFuntion CHF=new ASFuntion();
					
					String acc = this.getRequestValue("AccPackageID");
					String SubjectID = this.getRequestValue("SubjectID");
					String CustomerID = this.getRequestValue("CustomerID");
					String EndYear = this.getRequestValue("EndYear");
					String EndMonth = this.getRequestValue("EndMonth");
					
					String contrast = this.getRequestValue("contrast");
					if("".equals(contrast.trim())){
						contrast = "1";
					}
					
					if(!"".equals(CustomerID) && !"".equals(EndYear)){
						acc = CustomerID + EndYear;						
					}
					
					UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
					userSession.setCurChoiceAccPackageId(acc);
					userSession.setCurChoiceBeginYear(EndYear);
					userSession.setCurChoiceBeginMonth(EndMonth);
					userSession.setCurChoiceEndYear(EndYear);
					userSession.setCurChoiceEndMonth(EndMonth);	
					
					String Table = this.getRequestValue("Table");

					this.cleanColumn();
					if(!"1".equals(contrast.trim())){
						this.addColumn("科目", "Subjectid");
						this.addColumn("科目名称", "Subjectname");
						this.addColumn("信用期", "ConfigValue",null,"com.matech.audit.work.analyse.AnalyseFieldProcess",null);
					}else{
						this.addColumn("科目", "Subjectid");
						this.addColumn("科目名称", "Subjectname");
//						this.addColumn("信用期", "AccPackageID",null,"com.matech.audit.work.analyse.AnalyseFieldProcess",null);
					}
					
					if("".equals(CustomerID)){
						this.setCustomerId("");
						this.setOrAddRequestValue("Table"," select 1 Subjectid,2 Subjectname,2 ConfigValue ");	
					}else{
						this.setCustomerId(CustomerID);	
						
						String projectID = CHF.showNull(userSession.getCurProjectId());
						String AccPackageID = CHF.showNull(userSession.getCurAccPackageId());
						
						if(projectID!=null && !"".equals(projectID) && !"null".equals(projectID)){
							if(acc.equals(AccPackageID)){

								this.setOrAddRequestValue("Table"," select * from c_accpkgsubject a left join z_accyearconfig b on b.projectid = '"+projectID+"' and a.subjectid = configname ");
								
							}else{
								this.setOrAddRequestValue("Table"," select *,'' as ConfigValue from c_accpkgsubject ");
							}
						}else{
							this.setOrAddRequestValue("Table"," select *,'' as ConfigValue from c_accpkgsubject ");	
						}
							
					}
										
					if(!"".equals(SubjectID)){
						SubjectID = " and isleaf=1 and SubjectID like CONCAT('"+SubjectID+"','%') ";
					}else{
						SubjectID = " and 1=2 ";
					}
					if(!"".equals(acc)){
						acc = " and AccPackageID = '"+acc+"' ";
					}
					this.setOrAddRequestValue("AccPackageID",acc);	
					this.setOrAddRequestValue("SubjectID",SubjectID);
					
					
										
				}
			};
			
			pp.setTableID("slist");			
			pp.setPageSize_CH(500);
			pp.setWhichFieldIsValue(1);
			pp.setCancelOrderby(true);
			pp.setCancelPage(true);
			pp.setInputType("checkbox");
			
			//String sql = "select Subjectid,Subjectname from c_accpkgsubject where accpackageid='${AccPackageID}' and isleaf=1 ${SubjectID} ";
			
			String sql = "select Subjectid,Subjectname,ConfigValue from ( ${Table} ) a where 1=1 ${AccPackageID}  ${SubjectID} ${COLUMN_QUERY} order by Subjectid";
			
			pp.addColumn("科目", "Subjectid");
			pp.addColumn("科目名称", "Subjectname");
//			pp.addColumn("信用期", "AccPackageID",null,"com.matech.audit.work.analyse.AnalyseFieldProcess",null);
			
//			pp.setHiddenCol(new String[]{"AccPackageID"});
			
			pp.addSqlWhere("SubjectID", "${SubjectID}");	
			pp.addSqlWhere("AccPackageID", "${AccPackageID}");	
			pp.addSqlWhere("Table","${Table}");
			
			pp.addInputValue("Table");
			pp.addInputValue("CustomerID");
			pp.addInputValue("EndYear");
			pp.addInputValue("EndMonth");
						
			pp.addInputValue("contrast");
			
			pp.setSQL(sql);
			request.getSession().setAttribute(DataGrid.sessionPre+pp.getTableID(),pp);
		
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}		
		return modelAndView;
	}
	
	public ModelAndView subject(HttpServletRequest request, HttpServletResponse response,Analyse analyse)  throws Exception {
		ModelAndView modelAndView = new ModelAndView(_strIList);
		Connection conn=null;
		try {
			ASFuntion CHF=new ASFuntion();
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
			
			String acc = analyse.getCustomerID() + analyse.getEndYear();
			
			conn = new DBConnect().getDirectConnect(analyse.getCustomerID());
			AnalyseService analyseService = new AnalyseService(conn);
			
			if("".equals(analyse.getSdate())){
				if(userSession.getCurAccPackageId() !=null && userSession.getCurAccPackageId().equals(acc)){
					if(userSession.getCurProjectId()!=null && !"".equals(userSession.getCurProjectId())){
						analyse.setSdate(analyseService.getDay(userSession.getCurProjectId()));
					}
				}
				if("".equals(analyse.getSdate())) {
					analyse.setSdate("1个月`1|2个月`2|3个月`3|半年`6|1年`12|2年`24|3年`36|");
				}
			}
			
			String TableName = CHF.showNull(request.getParameter("TableName"));
			
			/**
			 * 定义一个String数组为 analyse[n][3],
			 *  analyse[n][0] 字
			 *  analyse[n][1] 显示　
			 *  analyse[n][2] 年月
			 */
			
			String [][] an = analyseService.getAnaly(analyse.getSdate(),acc.substring(6),analyse.getEndMonth());
					
			String dataGrid = CHF.showNull(request.getParameter("txtTable"));
			org.util.Debug.prtOut("deltable 删除11="+dataGrid);
			if(!"".equals(dataGrid))
				request.getSession().removeAttribute(DataGrid.sessionPre + dataGrid);
			
//			analyse.getCreditPeriod()
			if("".equals(analyse.getContrast().trim()) || "0".equals(analyse.getContrast().trim())){
				analyse.setCreditPeriod(null);
			}
			
			TableName = analyseService.createTable(an,TableName);
			analyseService.dataSubjectTable(an,analyse,TableName);
			
			
			
			DataGridProperty pp = new DataGridProperty();
			pp.setTableID("alist"+analyseService.getRandom());			
			pp.setPageSize_CH(500);
			pp.setWhichFieldIsValue(1);
			pp.setCancelOrderby(true);
			pp.setCancelPage(true);
			pp.setCustomerId(analyse.getCustomerID());
			
			String sql = "select CID,CName,Year,if(Contrast=0,'',Contrast) as Contrast,Dirction,Remain,Balance";
			
			pp.setTrActionProperty(true);
			pp.setTrAction(" AccPackageID='"+acc+"' subjectID=${CID} style='cursor:hand;' onDBLclick='goSort();' ");
			
			pp.addColumn("科目编号", "CID");
			pp.addColumn("科目名称", "CName");
			pp.addColumn("连续性至", "Year");
			
			pp.addColumn("信用期(月)", "Contrast","showCenter");	//信用期
			
			pp.addColumn("科目方向", "Dirction","showCenter");
			pp.addColumn("期初余额", "Remain","showMoney");
			pp.addColumn("余额", "Balance","showMoney");
			
			String sTitle = "科目编号`科目名称`连续性至`信用期(月)`科目方向`期初余额`余额";
			
			String s1 = "select '','>合计','','','',sum(remain),sum(Balance)";			
			
			for(int i=0;i < an.length; i++){
				sql += ","+ an[i][0] + ", "+ an[i][0] +"/balance*100 sDay"+i;
				pp.addColumn(an[i][1], an[i][0],"showMoney");
				pp.addColumn("占比", "sDay"+i,"showMoney");
				s1 += ",sum("+an[i][0]+"), sum("+an[i][0]+")/sum(balance)*100 ";
				sTitle += "`"+an[i][1]+"`占比";
			}
			sql += " from " +TableName +" where 1=1 ${COLUMN_QUERY}";
			s1 += " from " +TableName;
			
			pp.setCountsql(s1);
						
			pp.setFixedHeader(true) ;
			pp.setFixedCol(true) ;
			pp.setFixedColNum(5) ;
			pp.setSQL(sql);
			request.getSession().setAttribute(DataGrid.sessionPre+pp.getTableID(),pp);
			
			String TName = new ASTextKey(conn).TextCustomerName(analyse.getCustomerID());
			
			modelAndView.addObject("DataGrid", pp.getTableID());
			
			modelAndView.addObject("TableName", TableName);
			
			modelAndView.addObject("TName", TName);
			modelAndView.addObject("SubjectID", analyse.getSubjectID());
			modelAndView.addObject("Dirction", ("1".equals(analyseService.getDirection(acc, analyse.getSubjectID()))?"借":"贷"));
			modelAndView.addObject("Day",analyse.getEndYear() +" - " +analyse.getEndMonth() );
			
			/**
			 * 打印
			 */
			PrintSetup printSetup = new PrintSetup(conn);	
			printSetup.setStrTitles(new String[]{TName+" 科目账龄分析"});
			printSetup.setStrChineseTitles(new String[]{sTitle});
			printSetup.setStrQuerySqls(new String[]{sql.replaceAll("\\$\\{COLUMN_QUERY\\}","") + " union "+ s1});
//			printSetup.setIColumnWidths(new int[]{8,9,3,9,11,12,9,12,9,12,9,12,9,12,9,12,9,12,9,12,9});
			printSetup.setCharColumn(new String[]{"1`2`3`4"});
			
			List[] lists = new List[1];
			lists[0] = new ArrayList();  
			lists[0].add(0, "1");
			lists[0].add(1, "分析单位："+TName+"       分析科目："+analyse.getSubjectID()+"             分析方向："+("1".equals(analyse.getDirection())?"借":"贷")+ "                截止年月："+analyse.getEndYear() +" - " +analyse.getEndMonth());
			lists[0].add(2, "12");
			lists[0].add(3, null);
			
			printSetup.setPoms(lists);
			String filename = printSetup.getExcelFile();

			modelAndView.addObject("filename", filename);
			
			userSession.setCurChoiceAccPackageId(acc);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}		
		
		return modelAndView;
	}
	
	
	public ModelAndView print(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		HashMap mapResult = new HashMap();
		ASFuntion CHF=new ASFuntion();
		
		String filename = CHF.showNull(request.getParameter("filename"));
		
		mapResult.put("refresh","");
		
		mapResult.put("saveasfilename","账龄分析");			
		mapResult.put("bVpage","false");
		mapResult.put("strPrintTitleRows","$2:$4");
		mapResult.put("filename", filename);
		return new ModelAndView("/Excel/tempdata/PrintandSave.jsp",mapResult);
	}
	
	
	
	public ModelAndView assitemlist(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		ModelAndView modelAndView = new ModelAndView(_strAssItem);
		Connection conn=null;
		try {
			ASFuntion CHF=new ASFuntion();
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
						
			String acc = "";
			String CustomerID = CHF.showNull(request.getParameter("CustomerID"));
			String EndYear = CHF.showNull(request.getParameter("EndYear"));
			String EndMonth = CHF.showNull(request.getParameter("EndMonth"));
			
			if(!"".equals(CustomerID) && !"".equals(EndYear)){
				acc = CustomerID + EndYear;
			}
			
			if("".equals(acc)){
				acc = CHF.showNull(userSession.getCurChoiceAccPackageId());
				if("".equals(acc)){
					acc = CHF.showNull(userSession.getCurAccPackageId());
				}
			}
			
			if(!"".equals(acc)){
				if("".equals(CustomerID)){
					CustomerID=acc.substring(0,6);
				}
				EndYear = acc.substring(6);
			}

			if("".equals(EndYear)){
				EndYear = userSession.getCurChoiceEndYear();
			}
			
			if("".equals(EndMonth)){
				EndMonth = userSession.getCurChoiceEndMonth();
			}
			
			if("".equals(EndMonth)){
				EndMonth = "12";
			}
			
			conn = new DBConnect().getConnect(CustomerID);
			
			String SubjectID = CHF.showNull(request.getParameter("SubjectID"));
			
			modelAndView.addObject("CustomerID", CustomerID);
			modelAndView.addObject("EndYear", EndYear);
			modelAndView.addObject("EndMonth", EndMonth);
			modelAndView.addObject("SubjectID", SubjectID);
			String TName = "";
			if(!"".equals(CustomerID)){
				TName = new ASTextKey(conn).TextCustomerName(CustomerID);
			}
			modelAndView.addObject("TName", TName);
			
			DataGridProperty pp = new DataGridProperty(){
				public void onSearch (
	    				javax.servlet.http.HttpSession session,
	    				javax.servlet.http.HttpServletRequest request,
	    				javax.servlet.http.HttpServletResponse response) throws Exception{
					String acc = this.getRequestValue("AccPackageID");
					String SubjectID = this.getRequestValue("subjectID");
					String CustomerID = this.getRequestValue("customerID");
					String EndYear = this.getRequestValue("endYear");
					String EndMonth = this.getRequestValue("endMonth");
					
					String contrast = this.getRequestValue("contrast");
					if("".equals(contrast.trim())){
						contrast = "1";
					}
					
					this.cleanColumn();
					if(!"1".equals(contrast.trim())){
						this.addColumn("核算编号", "AssItemID");
						this.addColumn("核算名称", "AssItemName");
						this.addColumn("信用期", "ConfigValue",null,"com.matech.audit.work.analyse.AnalyseFieldProcess",null);
					}else{
						this.addColumn("核算编号", "AssItemID");
						this.addColumn("核算名称", "AssItemName");
					}
					
					String AssItemID = this.getRequestValue("AssItemID");
					
					if(!"".equals(CustomerID) && !"".equals(EndYear)){
						acc = CustomerID + EndYear;						
					}
					
					UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
					userSession.setCurChoiceAccPackageId(acc);
					userSession.setCurChoiceBeginYear(EndYear);
					userSession.setCurChoiceBeginMonth(EndMonth);
					userSession.setCurChoiceEndYear(EndYear);
					userSession.setCurChoiceEndMonth(EndMonth);	
					
					String Table = this.getRequestValue("Table");
					
					String sql = "select distinct AssItemID,AssItemName,ConfigValue,subjectID from  ${Table}  a where 1=1 ${AccPackageID}  ${AssItemID} ${subjectID}  ${COLUMN_QUERY}";
					
					if("".equals(CustomerID)){
						this.setCustomerId("");
						this.setOrAddRequestValue("Table"," (select 1 AssItemID,2 AssItemName,'' as ConfigValue,'' as subjectID) ");	
					}else{
						this.setCustomerId(CustomerID);
						String projectID = CHF.showNull(userSession.getCurProjectId());
						String AccPackageID = CHF.showNull(userSession.getCurAccPackageId());
						
						String string = " select distinct a.*,concat(accid,'`|`',assitemid) as subjectID " +
						" 	from  c_assitementryacc  a ,( " +
						" 		select b.subjectid  " +
						" 		from c_accpkgsubject a, c_accpkgsubject b " + 
						" 		where a.subjectid = '"+SubjectID+"'   " +
						" 		and a.AccPackageID = '"+acc+"' " + 
						" 		and b.AccPackageID = '"+acc+"'   " +
						" 		and (b.subjectfullname like concat(a.subjectfullname ,'/%') or b.subjectfullname = a.subjectfullname) " + 
						" 		and b.isleaf=1  " +
						" 	) b " +
						" 	where 1=1 " + 
						" 	and AccPackageID = '"+acc+"' " +   
						" 	and IsLeaf1=1  " +
						" 	and SubMonth=1  " +
						" 	and accid =subjectid " + 
						" 	order by AssItemID ";
						
						if(projectID!=null && !"".equals(projectID) && !"null".equals(projectID)){
							if(acc.equals(AccPackageID)){
								string = "(select * from ("+string+") a left join z_accyearconfig b on b.projectid = '200815932' and concat(accid,'`|`',assitemid) = configname )";
							}else{
								string = "(select *,'' as ConfigValue from ("+string+") a )";
							}
						}else{
							string = "(select *,'' as ConfigValue from ("+string+") a )";
						}
						
						this.setOrAddRequestValue("Table",string);	
					}
					
					if(!"".equals(AssItemID)){
						AssItemID = " and AssItemID like '"+AssItemID+"%' ";
					}else{
						AssItemID = "";
					}
					
					if(!"".equals(SubjectID)){
						SubjectID = " and 1=1 ";
					}else{
						SubjectID = " and 1=2 ";
					}
					
					if(!"".equals(acc)){
						acc = " and AccPackageID = '"+acc+"' ";
					}
					this.setOrAddRequestValue("AccPackageID",acc);
					this.setOrAddRequestValue("AssItemID",AssItemID);
					this.setOrAddRequestValue("subjectID",SubjectID);
					
					this.setSQL(sql);
							
				}
			};
			
			pp.setTableID("alist");			
			pp.setPageSize_CH(500);
			pp.setWhichFieldIsValue(1);
			pp.setCancelOrderby(true);
			pp.setCancelPage(true);
			pp.setInputType("checkbox");
			
			
			
			pp.addColumn("核算编号", "AssItemID");
			pp.addColumn("核算名称", "AssItemName");
			
			pp.addSqlWhere("subjectID", "${subjectID}");
			pp.addSqlWhere("AssItemID", "${AssItemID}");	
			pp.addSqlWhere("AccPackageID", "${AccPackageID}");	
			pp.addSqlWhere("Table","${Table}");
			
			pp.addInputValue("Table");
			pp.addInputValue("customerID");
			pp.addInputValue("endYear");
			pp.addInputValue("endMonth");
			
			pp.addInputValue("contrast");
									
			request.getSession().setAttribute(DataGrid.sessionPre+pp.getTableID(),pp);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}		
		return modelAndView;
	}
	
	
	public ModelAndView assitem(HttpServletRequest request, HttpServletResponse response,Analyse analyse)  throws Exception {
		ModelAndView modelAndView = new ModelAndView(_strAList);
		Connection conn=null;
		try {
			ASFuntion CHF=new ASFuntion();
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
			
			String acc = analyse.getCustomerID() + analyse.getEndYear();
			
			conn = new DBConnect().getDirectConnect(analyse.getCustomerID()); 
			AnalyseService analyseService = new AnalyseService(conn);
			
			if("".equals(analyse.getSdate())){
				if(userSession.getCurAccPackageId() !=null && userSession.getCurAccPackageId().equals(acc)){
					if(userSession.getCurProjectId()!=null && !"".equals(userSession.getCurProjectId())){
						analyse.setSdate(analyseService.getDay(userSession.getCurProjectId()));
					}
				}
				if("".equals(analyse.getSdate())) {
					analyse.setSdate("1个月`1|2个月`2|3个月`3|半年`6|1年`12|2年`24|3年`36|");
				}
			}
			
			/**
			 * 定义一个String数组为 analyse[n][3],
			 *  analyse[n][0] 字
			 *  analyse[n][1] 显示　
			 *  analyse[n][2] 年月
			 */
			
			String [][] an = analyseService.getAnaly(analyse.getSdate(),acc.substring(6),analyse.getEndMonth());
			
			String TableName = CHF.showNull(request.getParameter("TableName"));
			
			String dataGrid = CHF.showNull(request.getParameter("txtTable"));
			org.util.Debug.prtOut("deltable 删除11="+dataGrid);
			if(!"".equals(dataGrid))
				request.getSession().removeAttribute(DataGrid.sessionPre + dataGrid);
			
//			analyse.getCreditPeriod()
			if("".equals(analyse.getContrast().trim()) || "0".equals(analyse.getContrast().trim())){
				analyse.setCreditPeriod(null);
			}
			
			TableName = analyseService.createTable(an,TableName);
			analyseService.dataAssItemTable(an,analyse,TableName);
			
			DataGridProperty pp = new DataGridProperty();
			pp.setTableID("alist"+analyseService.getRandom());			
			pp.setPageSize_CH(500);
			pp.setWhichFieldIsValue(1);
			pp.setCancelOrderby(true);
			pp.setCancelPage(true);
			pp.setCustomerId(analyse.getCustomerID());
			
			String sql = "select CID,CName,Year,if(Contrast=0,'',Contrast) as Contrast,Dirction,Remain,Balance";
			
			pp.setTrActionProperty(true);
			pp.setTrAction(" AccPackageID='"+acc+"' SubjectID2='"+analyse.getSubjectID()+"' AssItemID2='${CID}' style='cursor:hand;' onDBLclick='goSort();' ");
			
			pp.addColumn("核算编号", "CID");
			pp.addColumn("核算名称", "CName");
			pp.addColumn("连续性至", "Year");
			pp.addColumn("信用期(月)", "Contrast","showCenter");	//信用期
			pp.addColumn("核算方向", "Dirction","showCenter");
			pp.addColumn("期初余额", "Remain","showMoney");
			pp.addColumn("余额", "Balance","showMoney");
			
			pp.setFixedHeader(true) ;
			pp.setFixedCol(true) ;
			pp.setFixedColNum(5) ;
			
			String sTitle = "核算编号`核算名称`连续性至`信用期(月)`核算方向`期初余额`余额";
			
			String s1 = "select '','>合计','','','',sum(remain),sum(Balance)";			
			
			for(int i=0;i < an.length; i++){
				sql += ","+ an[i][0] + ", "+ an[i][0] +"/balance*100 sDay"+i;
				pp.addColumn(an[i][1], an[i][0],"showMoney");
				pp.addColumn("占比", "sDay"+i,"showMoney");
				s1 += ",sum("+an[i][0]+"), sum("+an[i][0]+")/sum(balance)*100 ";
				sTitle += "`"+an[i][1]+"`占比";
			}
		/*	sql += " from " +TableName;
			s1 += " from " +TableName;*/
			
			sql += " from " +TableName +" where 1=1 ${COLUMN_QUERY}";
			s1 += " from " +TableName;
			
			pp.setCountsql(s1);
						
			
			pp.setSQL(sql);
			request.getSession().setAttribute(DataGrid.sessionPre+pp.getTableID(),pp);
			
			String TName = new ASTextKey(conn).TextCustomerName(analyse.getCustomerID());
			
			modelAndView.addObject("DataGrid", pp.getTableID());
			
			modelAndView.addObject("TableName", TableName);
			
			modelAndView.addObject("TName", TName);
			modelAndView.addObject("SubjectID", analyse.getSubjectID());
			modelAndView.addObject("Dirction", ("1".equals(analyseService.getDirection(acc, analyse.getSubjectID()))?"借":"贷"));
			modelAndView.addObject("Day",analyse.getEndYear() +" - " +analyse.getEndMonth() );
			
			/**
			 * 打印
			 */
			PrintSetup printSetup = new PrintSetup(conn);	
			printSetup.setStrTitles(new String[]{TName+" 核算账龄分析"});
			printSetup.setStrChineseTitles(new String[]{sTitle});
			printSetup.setStrQuerySqls(new String[]{sql.replaceAll("\\$\\{COLUMN_QUERY\\}","") + " union "+ s1});
			printSetup.setCharColumn(new String[]{"1`2`3"});
//			printSetup.setIColumnWidths(new int[]{8,9,3,13,13,11,9,11,9,11,9,11,9,11,9,12,9,11,9,11,9});
			printSetup.setVertical(false);
			
			List[] lists = new List[1];
			lists[0] = new ArrayList();  
			lists[0].add(0, "1");
			lists[0].add(1, "分析单位："+TName+"       分析科目："+analyse.getSubjectID()+"             分析方向："+("1".equals(analyse.getDirection())?"借":"贷")+ "                截止年月："+analyse.getEndYear() +" - " +analyse.getEndMonth());
			lists[0].add(2, "14");
			lists[0].add(3, null);
			
			printSetup.setPoms(lists);
			String filename = printSetup.getExcelFile();

			modelAndView.addObject("filename", filename);
			
			userSession.setCurChoiceAccPackageId(acc);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}		
		
		return modelAndView;
	}
	
	
	public ModelAndView assitemAndSubjectWaring(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		ASFuntion CHF = new ASFuntion();
		String moneysql=CHF.showNull(request.getParameter("moneysql"));
		String AccPackageID=CHF.showNull(request.getParameter("AccPackageID"));
		
		moneysql = moneysql.replaceAll("％", "%").replaceAll("&gt;",">").replaceAll("&lt;","<").replaceAll("<br>","");
		
		DataGridProperty pp = new DataGridProperty() {
		};
		

		// 必要设置
		pp.setTableID("assitemAndSubjectWaring");
		// 基本设置

		pp.setCustomerId(AccPackageID.substring(0, 6));

		pp.setOrderBy_CH("accpackageid,1,value1");
		pp.setDirection_CH("asc,asc,asc");
		
		pp.setPageSize_CH(500);
		
		// 查询设置
		pp.setSQL(moneysql);
		
		System.out.println("yzm:aaaa="+moneysql);
	
		pp.setWhichFieldIsValue(1);
	
		pp.addColumn("帐套编号", "accpackageid",FormatType.showCenter);
		pp.addColumn("类型", "1",FormatType.showCenter); 
		pp.addColumn("编号", "value1",FormatType.showCenter);
		pp.addColumn("名称", "value2" ,FormatType.showCenter);
		pp.addColumn("方向", "direction2",FormatType.showCenter);
		pp.addColumn("账期", "zhangqi",FormatType.showCenter);
		pp.addColumn("原余额", "occ","showMoney");
		pp.addColumn("借方发生", "Debit","showMoney");
		pp.addColumn("贷方发生", "Credit","showMoney");
		pp.addColumn("余额", "occ1","showMoney");
		

		// 打印设置
		pp.setPrintEnable(true);
		pp.setPrintTitle("账期监控");

		request.getSession()
				.setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);

		return new ModelAndView("lookoutsetup/assitemAndSubjectWaring.jsp");
	}
	
	/**
	 * 保存信用期
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView saveCreditPeriod(HttpServletRequest request, HttpServletResponse response,Analyse analyse)  throws Exception {
		Connection conn=null;
		try {
			ASFuntion CHF=new ASFuntion();
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
			String acc = CHF.showNull(userSession.getCurAccPackageId());
			
			String CustomerID = CHF.showNull(analyse.getCustomerID());
			String EndYear = CHF.showNull(analyse.getEndYear());
			
			String acc1 = CustomerID + EndYear;
			
			response.setContentType("text/html;charset=utf-8");
			PrintWriter out = response.getWriter();
			
			if(!"".equals(acc1)){
				userSession.setCurChoiceAccPackageId(acc1);
			}
			
			if(acc.equals(acc1)){	//保存信用期
				String projectID =  userSession.getCurProjectId();
				if(projectID!=null && !"".equals(projectID) && !"null".equals(projectID)){
					conn = new DBConnect().getConnect(acc.substring(0,6));
					AnalyseService as = new AnalyseService(conn);
					for(int i = 0;i<analyse.getCreditPeriod().length;i++){
						if(analyse.getCreditPeriod() != null){
							as.save(projectID, analyse.getSubjects()[i], analyse.getCreditPeriod()[i]);
						}
					}
					
					out.print("<script>alert(\"信用期设置成功！\");</script>");
				}else{
					out.print("<script>alert(\"注意：因为您没有选择项目,信用期设置只对当前分析有效！\");</script>");
				}
				
			}else{					//信用期只有一次有效
				out.print("<script>alert(\"注意：因为您选择帐套编号与当前项目的帐套编号不同,信用期设置只对当前分析有效！\");</script>");
			}
			
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		
	}

	
	/**
	 * 新模式
	 */
	public ModelAndView subjectlist(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		ModelAndView modelAndView = new ModelAndView(_strList);
		Connection conn=null;
		try {
			
			ASFuntion CHF=new ASFuntion();
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
						
			String acc = "";
			String CustomerID = CHF.showNull(request.getParameter("CustomerID")).trim();
			String EndYear = CHF.showNull(request.getParameter("EndYear")).trim();	//结束年
			String EndDate = CHF.showNull(request.getParameter("EndDate")).trim();	//结束月
			
			String SubjectID = CHF.showNull(request.getParameter("SubjectID")).trim();	//科目编号
			String sDate = CHF.showNull(request.getParameter("sDate")).trim();	//账龄区间
			
			String TableName = CHF.showNull(request.getParameter("TableName")).trim();	//临时表名
			
			
			if(!"".equals(CustomerID) && !"".equals(EndYear)){
				acc = CustomerID + EndYear;
			}
			
			if("".equals(acc)){
				acc = CHF.showNull(userSession.getCurChoiceAccPackageId());
				if("".equals(acc)){
					acc = CHF.showNull(userSession.getCurAccPackageId());
				}
			}
			
			if(!"".equals(acc)){
				if("".equals(CustomerID)){
					CustomerID=acc.substring(0,6);
				}
				EndYear = acc.substring(6);
			}

			if("".equals(EndYear)){
				EndYear = userSession.getCurChoiceEndYear();
			}
			
			if("".equals(EndDate)){
				EndDate = userSession.getCurChoiceEndMonth();
			}
			
			if("".equals(EndDate)){
				EndDate = "12";
			}
			
			if(!"".equals(SubjectID)){
				
				conn = new DBConnect().getDirectConnect(CustomerID); 
				AnalyseService analyseService = new AnalyseService(conn);
				
				if("".equals(sDate)){
					if(userSession.getCurAccPackageId() !=null && userSession.getCurAccPackageId().equals(acc)){
						if(userSession.getCurProjectId()!=null && !"".equals(userSession.getCurProjectId())){
							sDate = analyseService.getDay(userSession.getCurProjectId());
						}
					}
					if("".equals(sDate)) {
						sDate = "1个月`1|2个月`2|3个月`3|半年`6|1年`12|2年`24|3年`36|";
					}
				}
				
				String [][] dayArray = analyseService.getAnaly(sDate);
				
				TableName = analyseService.createTable(TableName,dayArray);
				
				analyseService.saveAnaly( TableName,CustomerID, EndYear, EndDate, SubjectID, dayArray);
				System.out.println(TableName);
				
				DataGridProperty pp = new DataGridProperty();
				pp.setCustomerId(CustomerID);
				
				pp.setTableID(TableName);			
				pp.setPageSize_CH(500);
				pp.setWhichFieldIsValue(1);
//				pp.setCancelOrderby(true);
//				pp.setCancelPage(true);
				pp.setOrderBy_CH("subjectid,assitemid");
				pp.setDirection_CH("asc,asc");
				
				String sql = "select a.*,if(direction2 = -1,'贷','借') as direction  ";
				
//				pp.setTrActionProperty(true);
//				pp.setTrAction(" AccPackageID='"+acc+"' subjectID=${CID} style='cursor:hand;' onDBLclick='goSort();' ");
				
				pp.addColumn("科目编号", "Subjectid");
				pp.addColumn("核算编号", "AssitemID");
				pp.addColumn("科目名称", "subjectname");
				pp.addColumn("连续性至", "Year");
				
//				pp.addColumn("信用期(月)", "Contrast","showCenter");	//信用期
				
				pp.addColumn("方向", "direction","showCenter");
				pp.addColumn("期初余额", "Remain","showMoney");
				pp.addColumn("余额", "Balance","showMoney");
				
				String sTitle = "科目编号`科目名称`连续性至`信用期(月)`科目方向`期初余额`余额";
				
				String s1 = "select '','>合计','','','',sum(remain),sum(Balance)";			
				
				for(int i=0;i < dayArray.length; i++){
					sql += ", "+ dayArray[i][0] +"/balance*100 sDay"+i;
					pp.addColumn(dayArray[i][1], dayArray[i][0],"showMoney");
					pp.addColumn("占比", "sDay"+i,"showMoney");
					s1 += ",sum("+dayArray[i][0]+"), sum("+dayArray[i][0]+")/sum(balance)*100 ";
					sTitle += "`"+dayArray[i][1]+"`占比";
				}
				sql += " from " +TableName +" a where 1=1 ";
				s1 += " from " +TableName;
				
//				pp.setCountsql(s1);
							
				pp.setSQL(sql);
				
				pp.setPrintEnable(true);
				pp.setPrintTitle("账龄分析");
				pp.setPrintCharColumn("1`2`3`4");
				
				request.getSession().setAttribute(DataGrid.sessionPre+pp.getTableID(),pp);
				
				modelAndView.addObject("DataGrid", TableName);
				
				
				
			}
			
			modelAndView.addObject("CustomerID", CustomerID);
			modelAndView.addObject("EndYear", EndYear);
			modelAndView.addObject("EndDate", EndDate);
			modelAndView.addObject("SubjectID", SubjectID);
			modelAndView.addObject("sDate", sDate);
			
			
			return modelAndView;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		
	}
	
	
	public ModelAndView view_user_goabordList(HttpServletRequest request,HttpServletResponse response) throws Exception{
		DataGridProperty dp = new DataGridProperty(){};
		
		dp.setTableID("statisticsList");
		dp.setCustomerId("");
		dp.setPageSize_CH(20);
		
		
		
		ModelAndView modelandview =new ModelAndView(Jsp.ecStatist.getPath());
		
		modelandview.addObject("outputData","true");
		
		String sql = 
				"select * from view_user_goabord"  ;
		
		dp.setSQL(sql);
		dp.setOrderBy_CH("id");
		dp.setDirection("asc");
		
		dp.setColumnWidth("8,60");
		
		
		String strHead="姓名,出国考察地点登记{";
		//dp.addColumn("部门", "部门");
		dp.addColumn("姓名", "姓名");
		//dp.addColumn("性别", "sex");
		//dp.addColumn("办公区域", "residence");
		//dp.addColumn("楼层", "floor");
		//dp.addColumn("人员类别", "rank");
		
		
		//Connection conn = new DBConnect().getConnect();
		
		Calendar cal=Calendar.getInstance();
		for (int i = 0; i <5 ; i++){
			
			dp.addColumn((cal.get(Calendar.YEAR)-i)+"年", i+"年");
			strHead += i + "年,";
		}
		strHead = strHead.substring(0, strHead.length()-1) + "}";
		System.out.println("=================================");
		System.out.println("strHead ="+strHead);
		System.out.println("=================================");
		dp.setTableHead(strHead);
		
		//dp.addColumn("是否已领体检表", "examination_get");
		//dp.addColumn("是否已领体检结果表", "results_get");
		
		dp.setTrActionProperty(false);
		
		dp.setWhichFieldIsValue(1);
		dp.setPrintEnable(true);
		dp.setPrintVerTical(false);
		dp.setPrintTitle("出国考察地点登记");

		request.getSession().setAttribute(DataGrid.sessionPre + dp.tableID, dp);
		
		return modelandview;
	}
	
}