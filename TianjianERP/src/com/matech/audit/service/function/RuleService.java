package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.project.ProjectService;
import com.matech.audit.service.project.model.Project;
import com.matech.audit.service.usersubject.SubjectAssitemService;
import com.matech.framework.pub.autocode.DELUnid;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class RuleService {
	
	private Connection myConn;
	
	public RuleService(){
		
	}
	
	public RuleService(Connection conn){
		this.myConn = conn;
	}
	
	public static Object getProject(String projectID,String result,String subjectFullName,String direction,String dataName) throws Exception{
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			new DBConnect().changeDataBaseByProjectid(conn, projectID);
			
			String acc = new ProjectService(conn).getProjectById(projectID).getAccPackageId();
			
			if("借".equals(direction) || "1".equals(direction) ){
				direction = "1";
			}else if("贷".equals(direction) || "-1".equals(direction)){
				direction = "-1";
			}else  if("双向".equals(direction) || "0".equals(direction)){
				direction = "0";
			}
			
			if("0".equals(dataName) || "本位币".equals(dataName) ){
				dataName = "0";
			}
			
			FunctionService fs = new FunctionService(conn,projectID, acc, "", "","username", null);
			String string = fs.getProject(subjectFullName, result, direction, dataName);
			
			return string;
		} catch (Exception e) {
			e.printStackTrace();
			return "0.00";
		} finally {
			DbUtil.close(conn);
		}
		
	}
	
	/**
	 * 
	 * @param Type	取数方式:取项目/取帐套
	 * @param ProjectOrCustomer	 项目或客户编号
	 * @param SubjectFullName 标准科目全路径名称
	 * @param RuleProperty 指标属性:期初数/期末数/借发生/贷发生/净发生
	 * @param Year 年
	 * @param Month 月
	 * @param DataName 币种,‘0’为本位币
	 * @return
	 * @throws Exception
	 */
	public static Object getValue(String Type,String ProjectOrCustomer,String SubjectFullName,String RuleProperty,String Year,String Month,String DataName) throws Exception{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			FunctionService fs = null;
			
			if(DataName == null){
				DataName = "0";
			}
			
			if("取项目".equals(Type) || "1".equals(Type)){
				conn = new DBConnect().getConnect("");
				new DBConnect().changeDataBaseByProjectid(conn, ProjectOrCustomer);
				
				/**
				 * 项目区间
				 */
				String AuditTimeBegin = "";
				String AuditTimeEnd = "";
				String sql = "select * from z_project where projectid='"+ProjectOrCustomer+"'";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				if(rs.next()){
					AuditTimeBegin = rs.getString("AuditTimeBegin");
					AuditTimeEnd = rs.getString("AuditTimeEnd");
				}
				DbUtil.close(rs);
				DbUtil.close(ps);
				int bYear = Integer.parseInt(AuditTimeBegin.substring(0, 4));
				int bMonth = Integer.parseInt(AuditTimeBegin.substring(5, 7));
				int eYear = Integer.parseInt(AuditTimeEnd.substring(0, 4));
				int eMonth = Integer.parseInt(AuditTimeEnd.substring(5, 7));
				
				fs = new FunctionService( conn,ProjectOrCustomer, null, "getProject",null, null, null);
				String fx = "";
				
				if("结转数".equals(RuleProperty)){
					return fs.getProject6(SubjectFullName,"结转数",fx,DataName,Year,Month,bYear,bMonth,eYear,eMonth);
				}else{
					//走老科目取数逻辑
					
					if("期初数".equals(RuleProperty)){
						RuleProperty = "项目期初";
					}else if("借发生".equals(RuleProperty)){
						RuleProperty = "项目发生";
						fx = "1";
					}else if("贷发生".equals(RuleProperty)){
						RuleProperty = "项目发生";
						fx = "-1";
					}else if("净发生".equals(RuleProperty)){
						RuleProperty = "项目发生";
					}else if("期末数".equals(RuleProperty)){
						RuleProperty = "项目期末";
					}else {
						throw new Exception("指标属性设置错误!");
					}
					
					return fs.getProject2(SubjectFullName,RuleProperty,fx,DataName,Year,Month,bYear,bMonth,eYear,eMonth);
				}
				
				
				
				
			} else if("取帐套".equals(Type) || "0".equals(Type)){
				conn = new DBConnect().getConnect(ProjectOrCustomer);
				
				fs = new FunctionService( conn,null, ProjectOrCustomer + Year, "getProject",null, null, null);
				
				int bYear = Integer.parseInt(Year);
				int bMonth = 1;
				int eYear = Integer.parseInt(Year);
				int eMonth = 12;
				
				
				
				String fx = "";
				
				if("结转数".equals(RuleProperty)){
					return fs.getProject6(SubjectFullName,"结转数",fx,DataName,"0",Month,bYear,bMonth,eYear,eMonth);
				}else{
					//走老科目取数逻辑
					
					if("期初数".equals(RuleProperty)){
						RuleProperty = "项目期初";
					}else if("借发生".equals(RuleProperty)){
						RuleProperty = "项目发生";
						fx = "1";
					}else if("贷发生".equals(RuleProperty)){
						RuleProperty = "项目发生";
						fx = "-1";
					}else if("净发生".equals(RuleProperty)){
						RuleProperty = "项目发生";
					}else if("期末数".equals(RuleProperty)){
						RuleProperty = "项目期末";
					}else {
						throw new Exception("指标属性设置错误!");
					}
					
					return fs.getProject2(SubjectFullName,RuleProperty,fx,DataName,"0",Month,bYear,bMonth,eYear,eMonth);
				}
				
				
				
			} else{
				throw new Exception("取数方式设置错误!");
			}
			
			
//			RuleService ruleService = new RuleService();			
//			if("取项目".equals(Type) || "1".equals(Type)){
//				string = ruleService.getProject( ProjectOrCustomer, SubjectFullName, RuleProperty, Year,Month,DataName);
//			} else if("取帐套".equals(Type) || "0".equals(Type)){
//				string = ruleService.getAccPackage( ProjectOrCustomer, SubjectFullName, RuleProperty, Year,Month,DataName);
//			} else{
//				throw new Exception("取数方式设置错误!");
//			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return "0.00";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(conn);
		}
		
	}
	
	public String getProject(String projectID, String SubjectFullName,String RuleProperty,String Year,String Month,String DataName) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = new DBConnect().getConnect("");
			new DBConnect().changeDataBaseByProjectid(conn, projectID);
			String result = "0.00";
			
			String bYearMonth = new ProjectService(conn).getProjectById(projectID).getAuditTimeBegin();
			String eYearMonth = new ProjectService(conn).getProjectById(projectID).getAuditTimeEnd();
			
			int Begin = Integer.parseInt(bYearMonth.substring(0, 4)) * 12 + Integer.parseInt(bYearMonth.substring(5, 7));
			int End = Integer.parseInt(eYearMonth.substring(0, 4)) * 12 + Integer.parseInt(eYearMonth.substring(5, 7));
			
			int bYear = Integer.parseInt(Year) * 12 + Integer.parseInt(Month);
			int eYear = Integer.parseInt(Year) * 12 + Integer.parseInt(Month);
			
			if(Month == null || "0".equals(Month) || "".equals(Month) || "00".equals(Month)){
				 bYear = Begin;
				 eYear = End;
			}
			
			String sql = "";
			
			String sql1 = "";
			String sql2 = "";
			if("0".equals(DataName) || "本位币".equals(DataName)){
				sql1 = "c_account";
				sql2 = "z_accountrectify";
			} else {
				sql1 = "c_accountall";
				sql2 = "z_accountallrectify";
			}
			
//			sql = "select '"+SubjectFullName+"' subjectname,  \n" + 
//			" ifnull(sum(case ifnull(subyearmonth*12+submonth,"+bYear+") when "+bYear+" then ifnull(direction2 * (DebitRemain+CreditRemain),0)  else 0 end + case ifnull(subyearmonth*12+submonth,"+Begin+") when "+Begin+" then BeginOcc else 0 end),0) remain,  \n" + 
//			" ifnull(sum(ifnull(DebitOcc,0)),0) DebitOcc,ifnull(sum(ifnull(CreditOcc,0)),0) CreditOcc,  \n" + 
//			" ifnull(sum(ifnull(direction2 * (DebitOcc - CreditOcc),0)),0) Occ,   \n" + 
//			" ifnull(sum(case ifnull(subyearmonth*12+submonth,"+eYear+") when "+eYear+" then ifnull(direction2 * (Balance),0) else 0 end + case ifnull(subyearmonth*12+submonth,"+End+") when "+End+" then EndOcc else 0 end  ),0) Balance  \n" + 
//			" from (   \n" +
//			"	select subjectid,  \n" +
//			"	case when a.accname1='' then SubjectName else accname1 end  SubjectName,  \n" +
//			"	case when a.accfullname1='' then subjectfullname2 else accfullname1 end  subjectfullname, \n" +
//			"	sum(direction2 * ((DebitTotalOcc1 - CreditTotalOcc1) + (DebitTotalOcc2 - CreditTotalOcc2 ) + (DebitTotalOcc4 - CreditTotalOcc4) + (DebitTotalOcc5 - CreditTotalOcc5) + (DebitTotalOcc6 - CreditTotalOcc6)) * rectifySign) EndOcc, \n" +
//			"	sum(direction2 * ((DebitTotalOcc4 - CreditTotalOcc4) + (DebitTotalOcc5 - CreditTotalOcc5) + (DebitTotalOcc6 - CreditTotalOcc6)) * rectifySign) BeginOcc \n" +
//			"	from z_manuaccount a \n" +
//			"	where projectid='"+projectID+"'  \n" +
//			"	and (case when a.accfullname1='' then subjectfullname2 else accfullname1 end like '"+SubjectFullName+"/%' or case when a.accfullname1='' then subjectfullname2 else accfullname1 end = '"+SubjectFullName+"')   \n" +
//			"	and isleaf1=1	 \n" +
//			"	and dataname='"+DataName+"' \n" +
//			"	group by subjectid \n" +
//			" ) a left join "+sql1+" b \n" +
//			" on 1=1   \n" +
//			" and subyearmonth*12+submonth>="+bYear+"  \n" + 
//			" and subyearmonth*12+submonth<="+eYear+"  \n" +
//			" and isleaf1=1 and dataname='"+DataName+"'  \n" +
//			" and a.subjectid=b.subjectid \n" +
//			" where 1=1 "; 
		
			sql = "select '"+SubjectFullName+"' subjectname,  \n" + 
			" sum(if(subyearmonth*12+submonth = "+(eYearMonth.substring(0, 4))+"*12+1,direction2 * ((DebitRemain+CreditRemain) + (DebitTotalOcc4 - CreditTotalOcc4) + (DebitTotalOcc5 - CreditTotalOcc5) + (DebitTotalOcc6 - CreditTotalOcc6)),0)) remain,     \n" + 
			" sum(DebitOcc) DebitOcc,sum(CreditOcc) CreditOcc,   \n" + 
			" sum(direction2 * (DebitOcc - CreditOcc)) Occ ,  \n" + 
			" sum(if(subyearmonth*12+submonth = "+(eYearMonth.substring(0, 4))+"*12+12,direction2 * (Balance + (DebitTotalOcc1 - CreditTotalOcc1) + (DebitTotalOcc2 - CreditTotalOcc2 ) + (DebitTotalOcc4 - CreditTotalOcc4) + (DebitTotalOcc5 - CreditTotalOcc5) + (DebitTotalOcc6 - CreditTotalOcc6)),0)) Balance    \n" + 
			" from "+sql1+" a left join "+sql2+" b \n" +
			" on 1=1   \n" +
			" and b.projectid = '"+projectID+"' " +
			" and a.subjectid=b.subjectid " +
			" and isleaf1=1" +
			" where 1=1 " +
			" and subyearmonth*12+submonth>="+bYear+"  \n" + 
			" and subyearmonth*12+submonth<="+eYear+"  \n" +
			
			" and isleaf1=1 and dataname='"+DataName+"'  \n" +
			" and (subjectfullname2 = '"+SubjectFullName+"' or subjectfullname2 like '"+SubjectFullName+"%') \n" ;
			
			////System.out.println("getProject:=|"+sql);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				if("期初数".equals(RuleProperty)){
					result = rs.getString("remain");
				}else if("借发生".equals(RuleProperty)){
					result = rs.getString("DebitOcc");
				}else if("贷发生".equals(RuleProperty)){
					result = rs.getString("CreditOcc");
				}else if("净发生".equals(RuleProperty)){
					result = rs.getString("Occ");
				}else if("期末数".equals(RuleProperty)){
					result = rs.getString("Balance");
				}else {
					throw new Exception("指标属性设置错误!");
				}
			}
		
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
//			return "0.00";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(conn);
		}
	}
	
	public String getAccPackage(String customerID,String SubjectFullName,String RuleProperty,String Year,String Month,String DataName) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = new DBConnect().getConnect(customerID);
			String result = "0.00";
			
			String sql = "";
			
			String sql1 = "";
			if("0".equals(DataName) || "本位币".equals(DataName)){
				sql1 = "c_account";
			} else {
				sql1 = "c_accountall";
			}
			
			int bYear = Integer.parseInt(Year) * 12 + Integer.parseInt(Month);
			int eYear = Integer.parseInt(Year) * 12 + Integer.parseInt(Month);
			if(Month == null || "0".equals(Month) || "".equals(Month) || "00".equals(Month)){
				bYear = Integer.parseInt(Year) * 12 + Integer.parseInt("01");
				eYear = Integer.parseInt(Year) * 12 + Integer.parseInt("12");				
			}
			
			sql = "select '"+SubjectFullName+"' subjectname,  \n" + 
				" ifnull(sum(case subyearmonth*12+submonth when "+bYear+" then direction2 * (DebitRemain+CreditRemain) else 0 end),0) remain,  \n" + 
				" ifnull(sum(DebitOcc),0) DebitOcc,ifnull(sum(CreditOcc),0) CreditOcc,  \n" + 
				" ifnull(sum(direction2 * (DebitOcc - CreditOcc)),0) Occ,  \n" + 
				" ifnull(sum(case subyearmonth*12+submonth when "+eYear+" then direction2 * (Balance) else 0 end),0) Balance  \n" + 
				" from "+sql1+"   \n" + 
				" where 1=1  \n" + 
				" and subyearmonth*12+submonth>="+bYear+"  \n" + 
				" and subyearmonth*12+submonth<="+eYear+"  \n" +
				" and (subjectfullname2 like '"+SubjectFullName+"/%' or subjectfullname2 = '"+SubjectFullName+"')  \n" + 
				" and isleaf1=1 and dataname='"+DataName+"' ";
			
			////System.out.println("getAccPackage:=|"+sql);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				if("期初数".equals(RuleProperty)){
					result = rs.getString("remain");
				}else if("借发生".equals(RuleProperty)){
					result = rs.getString("DebitOcc");
				}else if("贷发生".equals(RuleProperty)){
					result = rs.getString("CreditOcc");
				}else if("净发生".equals(RuleProperty)){
					result = rs.getString("Occ");
				}else if("期末数".equals(RuleProperty)){
					result = rs.getString("Balance");
				}else {
					throw new Exception("指标属性设置错误!");
				}
			}
			
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
//			return "0.00";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(conn);
		}
	}
	
	
	/**
	 * 重写指标取数 
	 */
	
	/**
	 * 
	 * @param projectID		项目编号
	 * @param Year			年度		内容：0(本年)，-1(1年前)，-2(2年前)，-3(3年前)，-4(4年前)，-5(5年前)，
	 * @param Month			月份		内容：0(日期从项目区间得到)
	 * @param DataName		币种		
	 * @param guideLine		指标对象
	 * @param args			辅助Map 已有Project对象 名：project
	 * @return
	 * @throws Exception
	 */
	public String[] getProjectValue(String projectID,String Year,String Month,String DataName,GuideLine guideLine,Map args) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try {
			ASFuntion CHF=new ASFuntion();
			
			String [] tableName = new String []{"","",""}; 	//选择表名
			
			String strName = "";
//			核算为空，表示是科目；不为空，表示为核算
			if("".equals(guideLine.getAssitemID())){	
				//本位币或外币
				if("".equals(DataName) || "0".equals(DataName) || "本位币".equals(DataName)){
					//本年或历年
					if("0".equals(Year) || "0".equals(Month)){
						tableName[0] = "c_account";
						tableName[1] = "z_accountrectify";
						tableName[2] = "z_accountyearrectify";
					}else{
						tableName[0] = "c_account";
						tableName[1] = "z_accountyearrectify";
						tableName[2] = "z_accountyearrectify";
					}
					DataName = "0";
				}else{
					if("0".equals(Year) || "0".equals(Month)){
						tableName[0] = "c_accountall";
						tableName[1] = "z_accountallrectify";
						tableName[2] = "z_accountallyearrectify";
					}else{
						tableName[0] = "c_accountall";
						tableName[1] = "z_accountallyearrectify";
						tableName[2] = "z_accountallyearrectify";
					}
					strName = "F";
				}
			}else{
				if("".equals(DataName) || "0".equals(DataName) || "本位币".equals(DataName)){
					if("0".equals(Year) || "0".equals(Month)){
						tableName[0] = "c_assitementryacc";
						tableName[1] = "z_assitemaccrectify";
						tableName[2] = "";
					}else{
						tableName[0] = "c_assitementryacc";
						tableName[1] = "z_assitemaccrectify";
						tableName[2] = "";
					}
					DataName = "0";
				}else{
					if("0".equals(Year) || "0".equals(Month)){
						tableName[0] = "c_assitementryaccall";
						tableName[1] = "z_assitemaccallrectify";
						tableName[2] = "";
					}else{
						tableName[0] = "c_assitementryaccall";
						tableName[1] = "z_assitemaccallrectify";
						tableName[2] = "";
					}
					strName = "F";
				}
			}
			
			Project project = (Project) args.get("project");
			
			String strStartYear=(String)args.get("起始年");
			String strStartMonth=(String)args.get("起始月");
			String strEndYear=(String)args.get("结束年");
			String strEndMonth=(String)args.get("结束月");
			
			String begin = "".equals(project.getAuditTimeBegin()) ? "0" : project.getAuditTimeBegin().substring(0,4);
			String end = "".equals(project.getAuditTimeEnd()) ? "0" : project.getAuditTimeEnd().substring(0,4);
			String bMonth = "".equals(project.getAuditTimeBegin()) ? "0" : project.getAuditTimeBegin().substring(5,7);
			String eMonth = "".equals(project.getAuditTimeEnd()) ? "0" : project.getAuditTimeEnd().substring(5,7);
			
			int iCheckParam=0;
			if (strStartYear!=null && !strStartYear.equals("")){
				iCheckParam++;
			}
			if (strEndYear!=null && !strEndYear.equals("")){
				iCheckParam++;
			}
			if (strStartMonth!=null && !strStartMonth.equals("")){
				iCheckParam++;
			}
			if (strEndMonth!=null && !strEndMonth.equals("")){
				iCheckParam++;
			}
			
			String strStartYearMonth="",strEndYearMonth="";
			
			String[] name = guideLine.getProperty();
			
			
			boolean bYear=false;
			int iResult=0;
			for (int i=0;i<name.length;i++){
				
				if (name[i] ==null || name[i].equals("")){
					
				}else{
					if (name[i].indexOf("year")>-1){
						bYear=true;
					}
					
					//去掉year
					name[i] = CHF.replaceStr(name[i], "year", "");
					iResult++;
				}
			}
			
			if (iCheckParam==4){
				//如果完整提供起始年、起始月、结束年、结束月4个参数
				strStartYearMonth=String.valueOf((Integer.parseInt(begin)+Integer.parseInt(strStartYear))*12+Integer.parseInt(strStartMonth));
				strEndYearMonth	 =String.valueOf((Integer.parseInt(end)+Integer.parseInt(strEndYear))*12+Integer.parseInt(strEndMonth));
				
			}else if (iCheckParam!=0){
				throw new Exception("请完整提供起始年、起始月、结束年、结束月4个参数！");
			}else{
				//如果一个参数都没提供
				
				
				if (bYear){
					//是按年取数
					
					if("".equals(Month) || "0".equals(Month) || "00".equals(Month)){
						//没有给月份
						if("".equals(Year)){
							//没有给年份
							strStartYearMonth = String.valueOf(Integer.parseInt(end)*12+1);
							strEndYearMonth = String.valueOf(Integer.parseInt(end)*12+12);
							Year = "0";
						}else{
							//给乐年份
							int pyear = Integer.parseInt(end) + Integer.parseInt(Year);
							strStartYearMonth = String.valueOf((pyear) * 12 + 1);
							strEndYearMonth = String.valueOf((pyear) * 12 + 12);
						}
						
					}else{
						if("".equals(Year)){
							strStartYearMonth = String.valueOf(Integer.parseInt(end)*12+1);
							strEndYearMonth = String.valueOf(Integer.parseInt(end)*12+Integer.parseInt(Month));
						}else{
							strStartYearMonth = String.valueOf((Integer.parseInt(end) + Integer.parseInt(Year) )*12+Integer.parseInt(Month));
							strEndYearMonth = String.valueOf((Integer.parseInt(end) + Integer.parseInt(Year) )*12+Integer.parseInt(Month));
						}
						 
					}
					
				}else{
					//是按项目取数
					if("".equals(Month) || "0".equals(Month) || "00".equals(Month)){
						if("".equals(Year)){
							strStartYearMonth = String.valueOf(Integer.parseInt(begin)*12+Integer.parseInt(bMonth));
							strEndYearMonth = String.valueOf(Integer.parseInt(end)*12+Integer.parseInt(eMonth));
							Year = "0";
						}else{
							int pyear = Integer.parseInt(end) + Integer.parseInt(Year);
							int beginMonth =  (pyear) * 12 + 1;
							int endMonth =  (pyear) * 12 + 12;
							
							int projectbegin = Integer.parseInt(begin)*12+Integer.parseInt(bMonth);
							int projectend = Integer.parseInt(end)*12+Integer.parseInt(eMonth);
							
							if( Integer.parseInt(begin) > pyear || Integer.parseInt(end) < pyear) throw new Exception("查询年度超出审计年度，请重新输入!!");
							
							for(int i= Integer.parseInt(begin); i<=Integer.parseInt(end) ;i++ ){
								if(pyear == i){
									if(projectend < endMonth){
										strEndYearMonth = String.valueOf(projectend);
									}else{
										strEndYearMonth = String.valueOf(endMonth);
									}
									if(projectbegin < beginMonth){
										strStartYearMonth = String.valueOf(beginMonth);
									}else{
										strStartYearMonth = String.valueOf(projectbegin);
									}
								}
							}
							
						}
						
					}else{
						if("".equals(Year)) throw new Exception("请提供“年度”!");
						strStartYearMonth = String.valueOf((Integer.parseInt(end) + Integer.parseInt(Year) )*12+Integer.parseInt(Month));
						strEndYearMonth = String.valueOf((Integer.parseInt(end) + Integer.parseInt(Year) )*12+Integer.parseInt(Month)); 
					}
				}
			}
			
//			////System.out.println("strStartYearMonth2="+strStartYearMonth);
//			////System.out.println("strEndYearMonth2="+strEndYearMonth);


			String tokenid =  "";
			String sql1 = "";
			String sql2 = "";
			int direction = 0;
			
			sql = "select distinct tokenid,subjectfullname2,direction2 from c_account where SubYearMonth * 12 + SubMonth = '"+strEndYearMonth+"' and subjectid = '"+guideLine.getSubjectID()+"'  " +
				" union " +
				" select distinct a.subjectfullname as tokenid,subjectfullname,if(substring(Property,2,1) = 2 , -1 ,1 ) as direction2 from z_usesubject a where a.projectid=" + projectID + " and subjectid = '"+guideLine.getSubjectID()+"' ";
			////System.out.println(sql);
			ps = this.myConn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				tokenid = rs.getString("tokenid");
				direction = rs.getInt("direction2");
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			
			String strResult[]=new String[iResult];
			for(int i=0;i<iResult;i++){
				strResult[i]="0.00";
			}
			
			double [] balance = new double[]{0.00,0.00}; 
			double [] rectify = new double[]{0.00,0.00}; 
			
			/**
			 *	当年余额:满足分组：1、2、3、12、4、5,balance在此赋值
			 */
			if (guideLine.getGroup().indexOf("1")>-1 
					|| guideLine.getGroup().indexOf("2")>-1 
					|| guideLine.getGroup().indexOf("3")>-1
					|| guideLine.getGroup().indexOf("4")>-1
					|| guideLine.getGroup().indexOf("5")>-1
					){
				if("".equals(guideLine.getAssitemID())){	
					sql1 = " and tokenid = '" + tokenid + "' " ;
					sql2 = "group by tokenid ";
					
				}else{
					sql1 = " and accid = '" + guideLine.getSubjectID() + "' and assitemid = '" + guideLine.getAssitemID() + "' " ;
					sql2 = " group by accid,assitemid ";
				}
				
				sql = "select " + 
				" ifnull(sum(if( subyearmonth*12+submonth = '"+strStartYearMonth+"' , direction2 * (DebitRemain+CreditRemain) , 0 )),0) remain,  " + 
				" ifnull(sum(if( subyearmonth*12+submonth = '"+strStartYearMonth+"' , DebitRemain , 0 )),0) DebitRemain,  " +
				" ifnull(sum(if( subyearmonth*12+submonth = '"+strStartYearMonth+"' , (-1) * CreditRemain , 0 )),0) CreditRemain,  " +
				" ifnull(sum(DebitOcc),0) DebitOcc," +
				" ifnull(sum(CreditOcc),0) CreditOcc,  " + 
				" ifnull(sum(direction2 * (DebitOcc - CreditOcc)),0) Occ,  " + 
				" ifnull(sum(if( subyearmonth*12+submonth = '"+strEndYearMonth+"' , direction2 * (Balance) , 0 )),0) Balance,  " + 
				" ifnull(sum(if( subyearmonth*12+submonth = '"+strEndYearMonth+"' , DebitBalance , 0 )),0) DebitBalance,  " + 
				" ifnull(sum(if( subyearmonth*12+submonth = '"+strEndYearMonth+"' , (-1) * CreditBalance , 0 )),0) CreditBalance,  " + 
				
				" ifnull(sum(if( subyearmonth*12+submonth = '"+strStartYearMonth+"' , direction2 * (DebitRemain"+strName+"+CreditRemain"+strName+") , 0 )),0) remainF,  " + 
				" ifnull(sum(if( subyearmonth*12+submonth = '"+strStartYearMonth+"' , DebitRemain"+strName+" , 0 )),0) DebitRemainF,  " +
				" ifnull(sum(if( subyearmonth*12+submonth = '"+strStartYearMonth+"' , (-1) * CreditRemain"+strName+" , 0 )),0) CreditRemainF,  " +
				" ifnull(sum(DebitOcc"+strName+"),0) DebitOccF," +
				" ifnull(sum(CreditOcc"+strName+"),0) CreditOccF,  " + 
				" ifnull(sum(direction2 * (DebitOcc"+strName+" - CreditOcc"+strName+")),0) OccF,  " + 
				" ifnull(sum(if( subyearmonth*12+submonth = '"+strEndYearMonth+"' , direction2 * (Balance"+strName+") , 0 )),0) BalanceF,  " + 
				" ifnull(sum(if( subyearmonth*12+submonth = '"+strEndYearMonth+"' , DebitBalance"+strName+" , 0 )),0) DebitBalanceF,  " + 
				" ifnull(sum(if( subyearmonth*12+submonth = '"+strEndYearMonth+"' , (-1) * CreditBalance"+strName+" , 0 )),0) CreditBalanceF  " + 
				
				" from "+tableName[0]+"   " + 
				" where 1=1  " + 
				" and subyearmonth*12+submonth>='"+strStartYearMonth+"'  " + 
				" and subyearmonth*12+submonth<='"+strEndYearMonth+"'  " +
				" and DataName = '"+DataName+"' " + sql1 + sql2;
				
	//			////System.out.println(sql);
				ps = this.myConn.prepareStatement(sql);
				rs = ps.executeQuery();
				
				if(rs.next()){
					balance[0] = rs.getDouble("remain");
					balance[1] = rs.getDouble("Balance");
					
					if (guideLine.getGroup().indexOf("1")>-1 
							|| guideLine.getGroup().indexOf("2")>-1 
							){
						for (int i=0;i<iResult;i++){
							strResult[i] = new java.text.DecimalFormat("0.00").format(rs.getDouble(name[i])); 
						}
						return strResult;
					}
				}
				if (guideLine.getGroup().indexOf("1")>-1 
					|| guideLine.getGroup().indexOf("2")>-1 
					){
					return strResult;
				}
				DbUtil.close(rs);
				DbUtil.close(ps);
			}
			
			/**
			 *	当年结转数:满足分组：c
			 */
			if (guideLine.getGroup().indexOf("c")>-1 
					){
				
				/**
				 * 结转数
				 * 例:
				 * Year  Month
				 *	0		0 		表示本年全年结转数
				 *	0		1		表示本年1月结转数
				 *	-1		0		表示1年前全年结转数
				 *	-1		1		表示1年前1月结转数
				 */
				String mycs = "";
				String csSubjectID = (String)args.get("对应科目");	//结转科目；多个，以“;”分隔
				sql=" select  \n" 
					+" 	group_concat(subjectid SEPARATOR '\\',\\'') as subjectid \n"
					+" from ( select distinct subjectid from  c_account \n" 
					+" where isleaf1=1 \n" 
					+" and SubYearMonth *12 +SubMonth >= '"+strStartYearMonth+"' \n" 
					+" and SubYearMonth *12 +SubMonth <= '"+strEndYearMonth+"' \n" 
					+" and substr(subjectfullname2,1, \n" 
					+" 			if(locate('/',subjectfullname2) = 0, \n" 
					+" 					length(subjectfullname2), \n" 
					+" 					locate('/',subjectfullname2) -1 ) \n" 
					+"             ) in ('"+CHF.replaceStr(csSubjectID, ";", "','")+"')  \n" 
					+" ) a";
				
				ps = this.myConn.prepareStatement(sql);
				rs = ps.executeQuery();
				if(rs.next()){
					mycs=rs.getString(1);
				}else{
					mycs="";
				}
				DbUtil.close(rs);
				DbUtil.close(ps);
				
				sql = "select (ifnull( occ1,0) - ifnull(occ2,0)) * a.direction2  as yearOcc  " +
				" from (  " +
				" 	select a.direction2,A.subjectid,sum(debitocc-creditocc) as occ1   " +
				" 	from c_account a  " +
				" 	where SubYearMonth * 12 + SubMonth >= '"+strStartYearMonth+"'  " +
				" 	and SubYearMonth * 12 + SubMonth <= '"+strEndYearMonth+"'  " +
				" 	and a.subjectid = '" + guideLine.getSubjectID() + "'  " +
				" 	group by subjectid  " +
				" ) a left join (  " +
				" 	select a.subjectid,sum(a.occurvalue* a.dirction) as occ2 from (  " +
				" 		select  distinct a.*   " +
				" 		from c_subjectentry a ,c_subjectentry b   " +
				" 		where substring(a.VchDate,1,4) * 12 + substring(a.VchDate,6,2) >= '"+strStartYearMonth+"'  " +
				" 		and substring(a.VchDate,1,4) * 12 + substring(a.VchDate,6,2) <= '"+strEndYearMonth+"'  " +
				" 		and a.vchdate = b.vchdate  " +
				" 		and a.voucherid=b.voucherid  " +
				" 		and a.subjectid = '" + guideLine.getSubjectID() + "'  " +
				" 		and b.subjectid in ('"+mycs+"')  " +
				" 	) a  " +
				" 	group by a.subjectid  " +
				" ) b on a.subjectid = b.subjectid";
				
	//			////System.out.println(guideLine.getSubjectID() + "||"+sql);
				ps = this.myConn.prepareStatement(sql);
				rs = ps.executeQuery();
				if(rs.next()){
					for (int i=0;i<iResult;i++){
						strResult[i] = new java.text.DecimalFormat("0.00").format(rs.getDouble(name[i]));
					}
				}
				return strResult;
			}
			
			/**
			 * 确定调整数是否需要上下级抵销
			 */
			String sopt = "1";
			sql = ""
                + " select 1 from  "
                + " c_account a "
                + " inner join "
                + " z_usesubject b "
                + " on a.subjectid=b.tipsubjectid "
                + " where a.accpackageid='" + project.getAccPackageId() + "' "
                + "   and a.subjectid='" + guideLine.getSubjectID() + "' "
                + "   and a.submonth=1 "
                + "   and a.isleaf1=1 "
                + "   and b.accpackageid='" + project.getAccPackageId() + "' "
                + "   and b.projectid='" + projectID + "' ";
			ps = this.myConn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				sopt = "0";
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			String string = "";
			String string1 = "";
			if(!"".equals(guideLine.getAssitemID())){	
				string += " and assitemid = '"+guideLine.getAssitemID()+"' ";
			}
			if(!"0".equals(DataName)){
				string += " and DataName = '"+DataName+"' ";
			}
			if(Integer.parseInt(Year) != 0 && !"0".equals(Month) ){
				string1 = " and yearrectify = '"+String.valueOf(Integer.parseInt(end) + Integer.parseInt(Year))+"' ";
			}
			
			/**
			 *	当年调整 ，rectify在此赋值
			 */
			if (guideLine.getGroup().indexOf("6")>-1 
				||guideLine.getGroup().indexOf("3")>-1 
				||guideLine.getGroup().indexOf("5")>-1
				){

				sql = "select * from " + tableName[1] + " where projectid ='"+projectID+"' and subjectid = '"+guideLine.getSubjectID()+"' " + string + string1 ;
				////System.out.println(sql);
				ps = this.myConn.prepareStatement(sql);
				rs = ps.executeQuery();
				if(rs.next()){
					if (guideLine.getGroup().indexOf("6")>-1){
						//调整
						for (int i=0;i<iResult;i++){
							strResult[i] = new java.text.DecimalFormat("0.00").format(Integer.parseInt(sopt) * rs.getDouble(name[i]));
						}
						return strResult;
					}
					rectify[0] = Integer.parseInt(sopt) * direction * ((rs.getDouble("debittotalocc4") - rs.getDouble("credittotalocc4")) 
							+ (rs.getDouble("debittotalocc5") - rs.getDouble("credittotalocc5")) 
							+ (rs.getDouble("debittotalocc6") - rs.getDouble("credittotalocc6")));
					rectify[1] = Integer.parseInt(sopt) * direction * ((rs.getDouble("debittotalocc1") - rs.getDouble("credittotalocc1")) 
							+ (rs.getDouble("debittotalocc2") - rs.getDouble("credittotalocc2")));
				}
				if (guideLine.getGroup().indexOf("6")>-1){
					return strResult;
				}
				DbUtil.close(rs);
				DbUtil.close(ps);
			}
			
			String sdbalance = new java.text.DecimalFormat("0.00").format(balance[1] + rectify[0] + rectify[1]);
			
			if (guideLine.getGroup().indexOf("3")>-1){
				
				for (int i=0;i<iResult;i++){
					if("sdremain".equals(name[i])){
						strResult[i] = new java.text.DecimalFormat("0.00").format(balance[0] + rectify[0]);
					}
					if("sdbalance".equals(name[i])){
						strResult[i] = sdbalance;
					}
				}
				return strResult;
			}
			
			/**
			 * 历年调整:
			 */
			if (guideLine.getGroup().indexOf("7")>-1 
					||guideLine.getGroup().indexOf("8")>-1 
					||guideLine.getGroup().indexOf("9")>-1
					||guideLine.getGroup().indexOf("a")>-1 
					||guideLine.getGroup().indexOf("b")>-1
				){
				if(!"".equals(tableName[2])){
					int iYear=0;
					
					if (guideLine.getGroup().indexOf("7")>-1){
						iYear=0;
					}
					if (guideLine.getGroup().indexOf("8")>-1){
						iYear=1;
					}
					if (guideLine.getGroup().indexOf("9")>-1){
						iYear=2;
					}
					if (guideLine.getGroup().indexOf("a")>-1){
						iYear=3;
					}
					if (guideLine.getGroup().indexOf("b")>-1){
						iYear=4;
					}
						
					string1 = " and yearrectify = '"+String.valueOf(Integer.parseInt(end) + Integer.parseInt(Year) - (iYear + 1))+"' ";
					sql = "select * from " + tableName[2] + " where projectid ='"+projectID+"' and subjectid = '"+guideLine.getSubjectID()+"' " + string + string1 ;
					////System.out.println(sql);
					ps = this.myConn.prepareStatement(sql);
					rs = ps.executeQuery();
					if(rs.next()){
						
						for (int i=0;i<iResult;i++){
							strResult[i] = new java.text.DecimalFormat("0.00").format(Integer.parseInt(sopt) * rs.getDouble(CHF.replaceStr(name[i], guideLine.split[iYear], "")));
						}
					}
					return strResult;
				}
				DbUtil.close(rs);
				DbUtil.close(ps);
			}
			
			/**
			 * 账龄
			 */
			sql1 = "";
			Map audit = new HashMap();
			
			double auditbal = balance[1];
			
			String vchdate = "";
			String sqlRectify = "";
			if(guideLine.getGroup().indexOf("5")>-1 ){		
				//审定账龄，需要添加调整
				auditbal = Double.parseDouble(sdbalance);
				if("".equals(guideLine.getAssitemID())){	
					sqlRectify = " select '"+String.valueOf(Integer.parseInt(end) + Integer.parseInt(Year))+"' as yearrectify,'" + tokenid +"' as tid,DebitTotalOcc1,CreditTotalOcc1,DebitTotalOcc2,CreditTotalOcc2 " + 
					" from "+tableName[1]+"  " +
					" where ProjectID = "+projectID+" and SubjectID ='" + guideLine.getSubjectID() + "'  " +
					" union  " +
					" select yearrectify,'" + tokenid +"' as tid,DebitTotalOcc1,CreditTotalOcc1,DebitTotalOcc2,CreditTotalOcc2 " +
					" from "+tableName[2]+" " + 
					" where ProjectID = "+projectID+" and SubjectID = '" + guideLine.getSubjectID() + "' ";
				}else{
					sqlRectify = "select '"+String.valueOf(Integer.parseInt(end) + Integer.parseInt(Year))+"' as yearrectify,subjectid as sid,AssItemID as aid,AssItemName,DebitTotalOcc1,CreditTotalOcc1,DebitTotalOcc2,CreditTotalOcc2 " + 
					" from "+tableName[1]+" " + 
					" where ProjectID = '" + projectID + "' and SubjectID = '"+ guideLine.getSubjectID() + "' " ;

				}
			}
			
			for(int i = 0 ;i <=guideLine.numYear; i++){
				String yearmonth1 = String.valueOf((Integer.parseInt(end) + Integer.parseInt(Year) - i )*12+Integer.parseInt(Month)); 
				String yearmonth2 = String.valueOf((Integer.parseInt(end) + Integer.parseInt(Year) - (i + 1))*12+Integer.parseInt(Month)); 
				String s = " sum(case when ifnull(DebitOcc,0)<0 then 0 else ifnull(DebitOcc,0) end)  Occ, ";
				if(direction == -1){
					s = " sum(case when ifnull(CreditOcc,0)<0 then 0 else ifnull(CreditOcc,0) end) Occ, ";
				}
				
				String s1 = "";
				if(guideLine.getGroup().indexOf("5")>-1){		//审定调整
					s = " sum(case when ifnull(DebitOcc,0)<0 then 0 else ifnull(DebitOcc,0) end) + (ifnull(DebitTotalOcc1,0) + ifnull(DebitTotalOcc2,0)) * " + sopt + " as  Occ, ";
					if(direction == -1){
						s = " sum(case when ifnull(CreditOcc,0)<0 then 0 else ifnull(CreditOcc,0) end) + (ifnull(DebitTotalOcc1,0) + ifnull(DebitTotalOcc2,0)) * " + sopt + " as Occ, ";
					}
					if("".equals(guideLine.getAssitemID())){
						s1 = " left join (" +
							sqlRectify +
							") b on a.tokenid = b.tid and yearrectify = "+String.valueOf(Integer.parseInt(end) + Integer.parseInt(Year) - i )+" ";
					} else{
						s1 = " left join (" +
							sqlRectify +
						") b on a.accid = b.sid and a.assitemid = b.aid and yearrectify = "+String.valueOf(Integer.parseInt(end) + Integer.parseInt(Year) - i )+" ";

					}
				}
				
				sql1 += " select " + s + " 'Day" + i + "' as vchdate " +
					" from "+tableName[0]+" a " + s1 + 
					" where 1=1  " ;
				
				if(i == guideLine.numYear){
					sql1 += " and subyearmonth*12+submonth <=" + yearmonth1 ;
				} else{
					sql1 += " and subyearmonth*12+submonth>"+yearmonth2+"  and subyearmonth*12+submonth <=" + yearmonth1 ;
				}
				
				if("".equals(guideLine.getAssitemID())){	
					sql1 +=" and DataName = '"+DataName+"' and tokenid = '" + tokenid +"' union";
				}else{
					sql1 += " and DataName = '"+DataName+"' and accid = '" + guideLine.getSubjectID() + "' and assitemid = '" + guideLine.getAssitemID() + "' union" ;
				}
	
				
			}
			sql = "select * from (" + sql1.substring(0,sql1.length()-5) + ") a order by abs(vchdate)";
			////System.out.println(sql);
			ps = this.myConn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				String Occ = rs.getString("Occ");
				vchdate = rs.getString("vchdate");
				if(Occ == null){
					audit.put(vchdate, new java.text.DecimalFormat("0.00").format(auditbal));
					auditbal = 0;
				}else{
					if(auditbal >= 0 ){
						if(auditbal-Double.parseDouble(Occ)>0){
							audit.put(vchdate, new java.text.DecimalFormat("0.00").format(Double.parseDouble(Occ)));
							auditbal = auditbal-Double.parseDouble(Occ);
						}else{
							audit.put(vchdate, new java.text.DecimalFormat("0.00").format(auditbal));
							auditbal = 0;
						}
						
					}else{
						if(Double.parseDouble(Occ)>0){
							audit.put(vchdate, new java.text.DecimalFormat("0.00").format(auditbal));
							auditbal = 0;
						}else{
							audit.put(vchdate,"0");
						}
						
					}
				}
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			if(guideLine.getGroup().indexOf("5")>-1 ){
				for (int i=0;i<iResult;i++){
					
					strResult[i] =  (String)audit.get(CHF.replaceStr(name[i], "sd", ""));
				}
			}else{
				for (int i=0;i<iResult;i++){
					strResult[i] =  (String)audit.get(name[i]);
				}
			}
			return strResult;
			
		} catch (Exception e) {
			e.printStackTrace();
			//System.out.println("error SQL : " + sql);
			throw e;
		} finally {
//			//System.out.println("finally SQL : " +sql);
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
	}
	
	public void setBatchProjectValue(String projectID,String Year,String Month,String tempTable,String strGroupFields[][],String strGroup[],Map args) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		String sql = "";
		try {
			
			ASFuntion CHF=new ASFuntion();
			
			System.out.println("年度："+Year+" | 月份："+Month);
			
			/**
			 * 上年调整影响年末 否：上海立信
			 */
			String svalue = "";
			sql = "select svalue from s_config where sname='上年调整影响年末'";
			ps = myConn.prepareStatement(sql);
			rs = ps.executeQuery(); 
			while(rs.next()){
				svalue = rs.getString(1);
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			/**
			 * 取上年数恒定为12月 是：就12月 上海立信		
			 */
//			String smonth = "";
//			sql = "select svalue from s_config where sname='取上年数恒定为12月'";
//			ps = myConn.prepareStatement(sql);
//			rs = ps.executeQuery();
//			while(rs.next()){
//				smonth = rs.getString(1);
//			}
//			DbUtil.close(rs);
//			DbUtil.close(ps);
			
			Project project = (Project) args.get("project");
		
			String projectBeginYear = "".equals(project.getAuditTimeBegin()) ? "0" : project.getAuditTimeBegin().substring(0,4);
			String projectEndYear = "".equals(project.getAuditTimeEnd()) ? "0" : project.getAuditTimeEnd().substring(0,4);
			String projectBeginMonth = "".equals(project.getAuditTimeBegin()) ? "0" : project.getAuditTimeBegin().substring(5,7);
			String projectEndMonth = "".equals(project.getAuditTimeEnd()) ? "0" : project.getAuditTimeEnd().substring(5,7);
			
			String YearMonthArea = CHF.showNull((String)args.get("审计区间"));	//用来表示一个审计区间有多少个月
			
			String strStartMonth=CHF.showNull((String)args.get("起始月"));
			String strEndMonth=CHF.showNull((String)args.get("结束月"));
			
//			String strStartMonth1=CHF.showNull((String)args.get("起始月"));
//			String strEndMonth1=CHF.showNull((String)args.get("结束月"));
			
			if("0".equals(strStartMonth)){
				strStartMonth = "";
			}
			if("0".equals(strEndMonth)){
				strEndMonth = "";
			}
			
			String strStartYearMonth="",strEndYearMonth="",strEndYearMonth1 = "";
			
			String allYear = CHF.showNull((String)args.get("比较年份"));

			String subejctType = CHF.showNull((String)args.get("科目类型"));// 末级(一级、二级)
//			列的科目范围
			String SubjectName = (String)args.get("科目名称");
			
			for(int iGroup=0;iGroup<strGroup.length;iGroup++){
				//遍历列
				
				String[] name = strGroupFields[iGroup];
				
				
				
				String strUpdate="";
				
				String preToken="",joinToken="dataname",mulToken="";
				
				////System.out.println(strGroup.length+"|"+iGroup);
				
				if (name==null)continue;
				
				if (name[0].indexOf("cur")==0){
					preToken="cur";
				}
				if (name[0].indexOf("unit")==0){
					preToken="unit";
					
					mulToken=" unitsign * ";
					
					//如果又要刷外币，又要刷数量，那么dataname里面，放的只是外币值，数量名称将放到unitname
					String bCurAndUnit = (String)args.get("外币数量");
					if (bCurAndUnit!=null && "都要".equals(bCurAndUnit)){
						joinToken="unitname";
					}
				}
				boolean bYear=false;
				if (name[0].indexOf("year")>-1){
					preToken+="year";
					bYear=true;
				}
				
				String strRecSign="";
				if (strGroup[iGroup].indexOf("6")>-1 
					||strGroup[iGroup].indexOf("d")>-1 
					//审定数（本位币/原币），需要作余额分析，取得期末调整＋期末重分类等
					||strGroup[iGroup].indexOf("3")>-1
					||strGroup[iGroup].indexOf("a")>-1
					
					//审定账龄（本位币/原币）都需要作余额分析，取得期末调整＋期末重分类等
					||strGroup[iGroup].indexOf("5")>-1
					||strGroup[iGroup].indexOf("c")>-1
					
					//m: 审定结转数；n：原币审定结转数
					||strGroup[iGroup].indexOf("m")>-1 
					||strGroup[iGroup].indexOf("n")>-1
					
					||strGroup[iGroup].indexOf("r")>-1
					){
					strRecSign=" a.recsign * ";
				}
				
				//构造最后的update语句
				int iNameLength;
				for (iNameLength=0;iNameLength<name.length;iNameLength++){
					if (name[iNameLength]==null) break;
					
					if(name[iNameLength].indexOf("TotalOcc0")>-1){
						strUpdate+=", a."+name[iNameLength]+" = "+strRecSign+mulToken+" 0";
					}else{
						strUpdate+=", a."+name[iNameLength]+" = "+strRecSign+mulToken+" b." + name[iNameLength];	
					}
					
				}
				if (strGroupFields[iGroup].length>0) strUpdate = strUpdate.substring(1);
				
				
				
				//判断取数区间 年 Year、月 Month、起始月 strStartMonth、结束月 strEndMonth
				if (bYear){
					int intYear = Integer.parseInt(projectEndYear); 
					//账套取数
					if (!"".equals(Year)){
						intYear+=Integer.parseInt(Year);
					}
					
					int intStartMonth=1;
					int intEndMonth=12;
					if("".equals(Month) || "0".equals(Month) || "00".equals(Month)){
					}else{
						intStartMonth=Integer.parseInt(Month);
						intEndMonth=Integer.parseInt(Month);
					}
					
					strStartYearMonth = String.valueOf(intYear*12 + intStartMonth);
					strEndYearMonth = String.valueOf(intYear*12 + intEndMonth);
					
				}else{
					//项目取数
					if(("".equals(Year) || "0".equals(Year)) && ("".equals(strStartMonth) &&"".equals(strEndMonth) && "".equals(YearMonthArea))){	
						//本年
						if("".equals(Month) || "0".equals(Month) || "00".equals(Month)){
							//如果没有提供年、月，则无条件取 项目起始和结束区间，这种情况适用于 年审、年审预审、外资年审、外资年审预审的项目取数；
							strStartYearMonth = String.valueOf(Integer.parseInt(projectBeginYear)*12+Integer.parseInt(projectBeginMonth));
							strEndYearMonth = String.valueOf(Integer.parseInt(projectEndYear)*12+Integer.parseInt(projectEndMonth));
							
						}else{
							
							int iYearMonthArea=Integer.parseInt(projectEndYear)*12+Integer.parseInt(projectEndMonth)-(Integer.parseInt(projectBeginYear)*12+Integer.parseInt(projectBeginMonth))+1;
							if (iYearMonthArea>12){
								throw new Exception("跨多年项目，不支持按指定月份取数");
							}
							
							//只提供月，没有提供年，适用于适用于 年审、年审预审、外资年审、外资年审预审的损益类科目，披露每月发生数；
							int intStart = Integer.parseInt(projectBeginYear)*12+Integer.parseInt(Month); 
							int intEnd = Integer.parseInt(projectEndYear)*12+Integer.parseInt(Month); 
							
							int projectbegin = Integer.parseInt(projectBeginYear)*12+Integer.parseInt(projectBeginMonth);
							int projectend = Integer.parseInt(projectEndYear)*12+Integer.parseInt(projectEndMonth);
						
							//项目取数
							/*
							 * 
							 * 1.	指定月损益彭勇会采用比较特殊的算法，
							 * 因此虽然设定的还是1到12月，但是刷出来的数据，会是：      1到5月，是07年的；6到12月份，会是06年的，请特别注意！
							 * 
							 */
							if(projectbegin <= intStart && intStart <= projectend){
								strStartYearMonth = String.valueOf(intStart);
								strEndYearMonth = String.valueOf(intStart);
							}else if(projectbegin <= intEnd && intEnd <= projectend){
								strStartYearMonth = String.valueOf(intEnd);
								strEndYearMonth = String.valueOf(intEnd);
							}
							
							
						}
					
					}else{
						
						//指定年份的情况，适用于 IPO审计逐年披露,还有 前面4种审计（ 年审、年审预审、外资年审、外资年审预审）的项目取上年同期数；
						
						if ( !"".equals(strStartMonth) ||  !"".equals(strEndMonth) ){
							//提供起始月、结束月，就是IPO,IPO的取数要求每年取，而不是按照项目区间合并；
							if (strStartMonth!=null && !"".equals(strStartMonth)){
								projectBeginMonth=strStartMonth;
							}
							
							if (strEndMonth!=null && !"".equals(strEndMonth)){
								projectEndMonth=strEndMonth;
							}
							
							int pyear = Integer.parseInt(projectEndYear) + Integer.parseInt(Year);
							
							if("".equals(Month) || "0".equals(Month) || "00".equals(Month)){
								//没有提供月份的时候就是这样
								//按照修整月份完成，每月取数
								strStartYearMonth = String.valueOf((pyear) * 12 + Integer.parseInt(projectBeginMonth));
								strEndYearMonth = String.valueOf((pyear) * 12 + Integer.parseInt(projectEndMonth));
							}else{
								//提供月份的时候
								if(!(Integer.parseInt(projectBeginMonth)<=Integer.parseInt(Month))){
									strStartYearMonth = "-1";
									strEndYearMonth = "-1";
								}else if(!(Integer.parseInt(Month)<=Integer.parseInt(projectEndMonth))){
//									项目取数，按照修整月份完成，每月取数
									strStartYearMonth = "-1";
									strEndYearMonth = "-1";
								}else{
//									项目取数，按照修整月份完成，每月取数
									strStartYearMonth = String.valueOf((pyear) * 12 + Integer.parseInt(Month));
									strEndYearMonth = String.valueOf((pyear) * 12 + Integer.parseInt(Month));
								}
								
							}
							
						}else{
							//没有提供，就是非IPO。也就是前面4种审计（ 年审、年审预审、外资年审、外资年审预审）的项目取上年同期数；
							strStartYearMonth = String.valueOf(Integer.parseInt(projectBeginYear)*12+Integer.parseInt(projectBeginMonth));
							strEndYearMonth = String.valueOf(Integer.parseInt(projectEndYear)*12+Integer.parseInt(projectEndMonth));
							
							int iYearMonthArea=Integer.parseInt(projectEndYear)*12+Integer.parseInt(projectEndMonth)
								-(Integer.parseInt(projectBeginYear)*12+Integer.parseInt(projectBeginMonth)) + 1;
							
							if(!"".equals(YearMonthArea)){
								try {
									strStartYearMonth = String.valueOf(Integer.parseInt(strEndYearMonth) - Integer.parseInt(YearMonthArea) + 1);
									iYearMonthArea = Integer.parseInt(YearMonthArea);	//按审计区间来算上期	
								} catch (Exception e) {}
							}
							
							if("".equals(Month) || "0".equals(Month) || "00".equals(Month)){
								
								if (iYearMonthArea>=12){
									//正式审计 年审
									strStartYearMonth = String.valueOf(Integer.parseInt(strStartYearMonth) + iYearMonthArea * Integer.parseInt(Year));
									strEndYearMonth = String.valueOf(Integer.parseInt(strEndYearMonth) + iYearMonthArea * Integer.parseInt(Year));
								}else {
									//预审，不足12个月
									strStartYearMonth = String.valueOf((Integer.parseInt(projectBeginYear)+Integer.parseInt(Year))*12+ Integer.parseInt(projectBeginMonth) );
									strEndYearMonth = String.valueOf(Integer.parseInt(strStartYearMonth) + 11);
								}
							}else{
								if (iYearMonthArea>12){
									throw new Exception("跨多年项目，不支持按指定月份取数");
								}
								
								int intStart = (Integer.parseInt(projectBeginYear) + Integer.parseInt(Year) )*12+Integer.parseInt(Month); 
								int intEnd = (Integer.parseInt(projectEndYear) + Integer.parseInt(Year) )*12+Integer.parseInt(Month); 
								
								strStartYearMonth = String.valueOf(Integer.parseInt(strStartYearMonth) + iYearMonthArea * Integer.parseInt(Year));
								strEndYearMonth = String.valueOf(Integer.parseInt(strEndYearMonth) + iYearMonthArea * Integer.parseInt(Year));
								
								//项目取数 
								/**
								 * 
								 * 1.	指定月损益彭勇会采用比较特殊的算法，
								 * 因此虽然设定的还是1到12月，但是刷出来的数据，会是：      1到5月，是07年的；6到12月份，会是06年的，请特别注意！
								 * 
								 */
								if(Integer.parseInt(strStartYearMonth) <= intStart && intStart <= Integer.parseInt(strEndYearMonth)){
									strStartYearMonth = String.valueOf(intStart);
									strEndYearMonth = String.valueOf(intStart);
								}else if(Integer.parseInt(strStartYearMonth) <= intEnd && intEnd <= Integer.parseInt(strEndYearMonth)){
									strStartYearMonth = String.valueOf(intEnd);
									strEndYearMonth = String.valueOf(intEnd);
								}else if(!"".equals(YearMonthArea)){
									/**
									 * 通过审计区间来取项目的月份取数	
									 */
									strStartYearMonth = String.valueOf((Integer.parseInt(projectEndYear) - iYearMonthArea/12 )*12+Integer.parseInt(Month) + iYearMonthArea * Integer.parseInt(Year));  
									strEndYearMonth = strStartYearMonth;
								}
								
							}
						}
						
					}
				}
				
				if(allYear != null && !"".equals(allYear)){
					strEndYearMonth1 = String.valueOf(Integer.parseInt(projectEndYear)*12+Integer.parseInt(projectEndMonth));
				}else{
					strEndYearMonth1 = strEndYearMonth;
				}
				
				/**
				 * 设置最小的层次
				 */
				sql = "select level1 " +
				" from c_account a" +
				" where 1=1" +
				" and a.subyearmonth*12+a.submonth>='"+strStartYearMonth+"' and a.subyearmonth*12+a.submonth<='"+strEndYearMonth+"'  \n" +
				" and (a.subjectfullname2 like '"+SubjectName+"/%'  or a.subjectfullname2 = '"+SubjectName+"' )  \n" + 
				" order by level1 limit 1 ";
				ps = myConn.prepareStatement(sql);
				rs = ps.executeQuery();
				int level1 = 1;
				if(rs.next()){
					level1 = rs.getInt(1);
				}
				DbUtil.close(rs);
				DbUtil.close(ps);
				sql = "";
				
				String sqlType = "";
				String sqlType1 = "";
				if(subejctType == null || "".equals(subejctType.trim()) || "末级".equals(subejctType)){
					sqlType = " and a.isleaf1 = 1  \n";
					sqlType1 = " and isleaf = 1  \n";
				}else if("一级".equals(subejctType)) {
					sqlType = " and a.level1 = "+level1+"  \n";
					sqlType1 = " and level0 = "+level1+"  \n";
				}else if("二级".equals(subejctType)) {
					sqlType = " and ((a.level1="+level1+" and a.isleaf1=1) " +
							" or (a.level1="+level1+" +1) " +
							" or (a.subjectfullname2 = '"+SubjectName+"' and a.isleaf1=1) )  \n";
					sqlType1 = " and ( (level0="+level1+" and isleaf=1) or  level0="+level1+" +1 )  \n";
				}
				
//				strEndYearMonth1 = String.valueOf(Integer.parseInt(projectEndYear)*12+Integer.parseInt(projectEndMonth));
				
//				System.out.println("区间："+strStartYearMonth + " | " + strEndYearMonth);
				
				//根据 科目还是核算、是本位币还是数量或外币，最多循环4次
				sql = "select distinct issubject  as d1,IF(dataname='本位币',0,1) d2 from " + tempTable;
				ps = this.myConn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					//遍历行
				
					String DataName=rs.getString(2);
					String isSubject=rs.getString(1);
				
					//System.out.println("DataName="+DataName+"|isSubject="+isSubject+"|strGroup[iGroup]"+strGroup[iGroup]);
					
					String [] tableName = new String []{"","",""}; 	//选择表名
				
					String strName = "";
	
					//核算为空，表示是科目；不为空，表示为核算
					if("科目".equals(isSubject)){	
						//本位币或外币
						if("0".equals(DataName)){
							//本位币
							if("".equals(Year) || "0".equals(Year) || ("".equals(Year) && "0".equals(Month))){
								//本年或历年
								tableName[0] = "c_account";
								tableName[1] = "z_accountrectify";
								tableName[2] = "z_accountyearrectify";
							}else{
								//上年或历年
								tableName[0] = "c_account";
								tableName[1] = "z_accountyearrectify";
								tableName[2] = "z_accountyearrectify";
							}
							DataName = "0";
							strName = "";
						}else{
							if("".equals(Year) || "0".equals(Year) || ("".equals(Year) && "0".equals(Month))){
								tableName[0] = "c_accountall";
								tableName[1] = "z_accountallrectify";
								tableName[2] = "z_accountallyearrectify";
							}else{
								tableName[0] = "c_accountall";
								tableName[1] = "z_accountallyearrectify";
								tableName[2] = "z_accountallyearrectify";
							}
							
							//仅仅在 外币账（DataName<>0），且取本位币账的时候，才需要"f"
							if (preToken.indexOf("cur")<0  && preToken.indexOf("unit")<0 )
								strName = "F";
						}
					}else{
						//核算
						if("0".equals(DataName)){
							//本位币
							if("".equals(Year) || "0".equals(Year) || ("".equals(Year) && "0".equals(Month))){
								tableName[0] = "c_assitementryacc";
								tableName[1] = "z_assitemaccrectify";
								tableName[2] = "z_assitemaccyearrectify";
							}else{
								tableName[0] = "c_assitementryacc";
								tableName[1] = "z_assitemaccyearrectify";
								tableName[2] = "z_assitemaccyearrectify";
							}
							DataName = "0";
							strName = "";
						}else{
							//外币
							if("".equals(Year) || "0".equals(Year) || ("".equals(Year) && "0".equals(Month))){
								tableName[0] = "c_assitementryaccall";
								tableName[1] = "z_assitemaccallrectify";
								tableName[2] = "z_assitemaccallyearrectify";
							}else{
								tableName[0] = "c_assitementryaccall";
								tableName[1] = "z_assitemaccallyearrectify";
								tableName[2] = "z_assitemaccallyearrectify";
							}
							//仅仅在 外币账（DataName<>0），且取本位币账的时候，才需要"f"
							if (preToken.indexOf("cur")<0  && preToken.indexOf("unit")<0 )
								strName = "F";
						}
					}
			

					String sql1 = "";
					String sql2 = "";
		
					/**
					 * 2 9 f
					 *	当年余额:满足分组：1、8、e 是数量、外币、本位币
					 */
					if (strGroup[iGroup].indexOf("1")>-1 
						||strGroup[iGroup].indexOf("8")>-1 
						||strGroup[iGroup].indexOf("e")>-1 
						
						//年期初、发生、期末
						||strGroup[iGroup].indexOf("2")>-1 
						||strGroup[iGroup].indexOf("9")>-1 
						||strGroup[iGroup].indexOf("f")>-1 
						
						//审定数（本位币/原币），需要作余额分析，取得未审余额
						||strGroup[iGroup].indexOf("3")>-1
						||strGroup[iGroup].indexOf("a")>-1
						
						//账龄（本位币/原币、未审/审定）都需要作余额分析，取得未审余额
						||strGroup[iGroup].indexOf("4")>-1
						||strGroup[iGroup].indexOf("5")>-1
						||strGroup[iGroup].indexOf("b")>-1
						||strGroup[iGroup].indexOf("c")>-1
						){
						
						////System.out.println("进入DataName="+DataName+"|isSubject="+isSubject+"|strGroup[iGroup]"+strGroup[iGroup]);
						
						if("科目".equals(isSubject)){	
//							sql1 = " and a.tokenid = b.tokenid and b.sid like concat('%',subyearmonth,'`',a.subjectid,'%') " ;
							sql1 = " and a.tokenid = b.tokenid  " ;
							sql2 = " group by sname,b.tokenid,b."+joinToken+" ";
							
						}else{
							sql1 = " and a.AssTotalName1=b.tokenid  and b.sid like concat('%',a.subyearmonth,'`',a.accid,'`',a.assitemid,'%') " ;
							sql2 = " group by sname,b.tokenid,b."+joinToken+" ";
						}

						if (strGroup[iGroup].indexOf("3")>-1
							||strGroup[iGroup].indexOf("a")>-1
							||strGroup[iGroup].indexOf("4")>-1
							||strGroup[iGroup].indexOf("5")>-1
							||strGroup[iGroup].indexOf("b")>-1
							||strGroup[iGroup].indexOf("c")>-1
								){
							strUpdate=" a."+preToken+"remain=b."+preToken+"remain," +
							" a."+preToken+"Balance=b."+preToken+"Balance" ;
						}
						
						sql = "select sname,b.tokenid,b."+joinToken+"," + 
						"\n ifnull(sum(if( a.subyearmonth*12+a.submonth = '"+strStartYearMonth+"' , a.direction2 * (a.DebitRemain"+strName+"+a.CreditRemain"+strName+") , 0 )),0) "+preToken+"remain,  " + 
						"\n ifnull(sum(if( a.subyearmonth*12+a.submonth = '"+strStartYearMonth+"' , if((a.DebitRemain"+strName+"+a.CreditRemain"+strName+")>0,abs(a.DebitRemain"+strName+"+a.CreditRemain"+strName+"),0),0 )),0)  "+preToken+"DebitRemain,  " +
						"\n ifnull(sum(if( a.subyearmonth*12+a.submonth = '"+strStartYearMonth+"' , if((a.DebitRemain"+strName+"+a.CreditRemain"+strName+")<0,abs(a.DebitRemain"+strName+"+a.CreditRemain"+strName+"),0),0 )),0)  "+preToken+"CreditRemain,  " +
						"\n ifnull(sum(a.DebitOcc"+strName+"),0)  "+preToken+"DebitOcc," +
						"\n ifnull(sum(a.CreditOcc"+strName+"),0)  "+preToken+"CreditOcc,  " + 
						"\n ifnull(sum(a.direction2 * (a.DebitOcc"+strName+" - a.CreditOcc"+strName+")),0)  "+preToken+"Occ,  " + 
						"\n ifnull(sum(if( a.subyearmonth*12+a.submonth = '"+strEndYearMonth+"' , a.direction2 * (a.Balance"+strName+") , 0 )),0)  "+preToken+"Balance,  " + 
						"\n ifnull(sum(if( a.subyearmonth*12+a.submonth = '"+strEndYearMonth+"' , if(a.Balance"+strName+">0,abs(a.Balance"+strName+"),0), 0 )),0)  "+preToken+"DebitBalance,  " + 
						"\n ifnull(sum(if( a.subyearmonth*12+a.submonth = '"+strEndYearMonth+"' , if(a.Balance"+strName+"<0,abs(a.Balance"+strName+"),0), 0 )),0)  "+preToken+"CreditBalance  " + 
						
						"\n from "+tableName[0]+"   a,"+tempTable+" b " + 
						"\n where 1=1  " + sqlType + 
						"\n and a.subyearmonth*12+a.submonth>='"+strStartYearMonth+"'  " + 
						"\n and a.subyearmonth*12+a.submonth<='"+strEndYearMonth+"'  " +
						"\n and a.DataName = if(b."+joinToken+"='本位币',0,b."+joinToken+") " +
						"\n and b.issubject = '"+isSubject+"' " +
						"\n and if(b.dataname='本位币',0,1) = '"+DataName+"' " + sql1 + sql2;
						
//						System.out.println(sql);
						
						sql = "update "+tempTable+" a  join ( " + sql + " ) b " +
						 	" on a.issubject = '"+isSubject+"' and if(a.dataname='本位币',0,1) = '"+DataName+"' " +
						 	" and a.sname = b.sname and a.tokenid = b.tokenid and a."+joinToken+" = b."+joinToken +" \n" +
							"set " + strUpdate; 
						ps = this.myConn.prepareStatement(sql);
						
						ps.execute();
						DbUtil.close(ps);
						
						if(preToken.indexOf("unit") >-1){	//显示数量时，科目只有本位币，数量值显示为0
							sql = "update "+tempTable+" a set " +
									""+preToken+"remain = 0," +
									""+preToken+"DebitRemain = 0," +
									""+preToken+"CreditRemain = 0," +
									""+preToken+"DebitOcc = 0," + 
									""+preToken+"CreditOcc = 0," +
									""+preToken+"Occ = 0," +
									""+preToken+"Balance = 0," +
									""+preToken+"DebitBalance = 0," +
									""+preToken+"CreditBalance = 0" +
									" where "+joinToken+" = '本位币'";
							ps = this.myConn.prepareStatement(sql);
							ps.execute();
							DbUtil.close(ps);
						}
						
						if (strGroup[iGroup].indexOf("1")>-1 
								|| strGroup[iGroup].indexOf("8")>-1
								|| strGroup[iGroup].indexOf("e")>-1
//								年期初、发生、期末
								||strGroup[iGroup].indexOf("2")>-1 
								||strGroup[iGroup].indexOf("9")>-1 
								||strGroup[iGroup].indexOf("f")>-1 
							){
							continue ;
						}
					}//取余额表
					
					
					/**
					 *	当年结转数:满足分组：7:结转数；k是原币接转数
					 */
					if (strGroup[iGroup].indexOf("7")>-1 
						||strGroup[iGroup].indexOf("k")>-1 
						
						//m: 审定结转数；n：原币审定结转数
						||strGroup[iGroup].indexOf("m")>-1 
						||strGroup[iGroup].indexOf("n")>-1 
						){
						
						/**
						 * 结转数
						 * 例:
						 * Year  Month
						 *	0		0 		表示本年全年结转数
						 *	0		1		表示本年1月结转数
						 *	-1		0		表示1年前全年结转数
						 *	-1		1		表示1年前1月结转数
						 */
						if("科目".equals(isSubject)){	
							//科目结转数	
							
							sql = "select group_concat(distinct \"'\",a.subjectid,\"'\") sids  " +
							"	from c_account  a " +
							"	where 1=1 " +
							"	and a.SubYearMonth * 12  + a.SubMonth >=  '"+strStartYearMonth+"' " +
							"	and a.SubYearMonth * 12  + a.SubMonth <=  '"+strEndYearMonth+"' " +
							" 	and (a.subjectfullname2 = '"+SubjectName+"' or a.subjectfullname2 like '"+SubjectName+"/%' )  \n" ; 
							
							ps = myConn.prepareStatement(sql);
							rs1 = ps.executeQuery();
							String carrySubjectID = "";
							if(rs1.next()){
								carrySubjectID = rs1.getString(1);
							}
							DbUtil.close(rs1);
							DbUtil.close(ps);
							if(!"".equals(carrySubjectID)){
								carrySubjectID = " and a.subjectid in ("+carrySubjectID+") ";
							}
							
							if (preToken.indexOf("cur")>-1){
								//外币
								sql="update "+tempTable+" a, \n"  +
								"	( \n"+
								"	select (ifnull( occ1,0) - ifnull(occ2,0))*a.direction2  as "+preToken+"CarryOver   ,a.tokenid,a.dataname \n"+
								"	from (    \n"+
								"		select A.tokenid,a.direction2,b.dataname,sum(a.debitocc-a.creditocc) as occ1 \n"+    
								"		from c_accountall a   ,"+tempTable+" b \n"+ 
								"		where a.SubYearMonth * 12  + a.SubMonth >=  '"+strStartYearMonth+"'      \n"+
								"		and a.SubYearMonth * 12  + a.SubMonth <=  '"+strEndYearMonth+"'     \n"+
								sqlType + 
								"		and a.tokenid = b.tokenid \n"+
								"		and a.dataname=if(b.dataname='本位币','0',b.dataname) \n" +
								"		group by a.tokenid , dataname   \n" +
								"	) a left join (    \n" +
								"		select a.tid as tokenid, Currency as dataname, sum(a.CurrValue* a.dirction) as occ2 " +
								"			from (    \n"+
								"			select  distinct a.* , b.tokenid as tid    \n"+
								"			from c_subjectentry a  ,"+tempTable+" b,(" +
								"				select distinct subjectid,accname,SubjectFullName1,SubjectFullName2,tokenid  " +
								"				from c_account c " +
								"				where 1=1 \n" +
								" 				and (c.subjectfullname2 = '"+SubjectName+"' or c.subjectfullname2 like '"+SubjectName+"/%' )  \n" +
								"				and c.SubYearMonth * 12  + c.SubMonth >=  '"+strStartYearMonth+"'  \n" +
								"				and c.SubYearMonth * 12  + c.SubMonth <=  '"+strEndYearMonth+"'  \n" +
								"				" +
								"			) c \n"+
								"			where substring(a.VchDate,1,4) * 12  +substring(a.VchDate,6,2) >=  '"+strStartYearMonth+"'      \n"+
								"			and substring(a.VchDate,1,4) * 12  +substring(a.VchDate,6,2) <=  '"+strEndYearMonth+"'     \n"+
								carrySubjectID + 
								"			and (a.SubjectFullName1 = c.SubjectFullName1 or a.SubjectFullName1 like concat(c.SubjectFullName1,'/%')) \n" +
								"			and a.property like '%2%'   \n"+
								"			and b.tokenid = c.tokenid \n"+
								"		) a    \n"+
								"		group by a.tid,a.Currency  \n"+
								"	) b on a.tokenid = b.tokenid and a.dataname=b.dataname \n"+
								")b \n"+
								"set a."+preToken+"CarryOver=b."+preToken+"CarryOver \n"+
								"where a.tokenid = b.tokenid and a.dataname=b.dataname \n";
							}else{
								//本位币
								sql="update "+tempTable+" a, \n"  +
								"	( \n"+
								"	select (ifnull( occ1,0) - ifnull(occ2,0))*a.direction2  as CarryOver   ,a.tokenid,a.dataname \n"+
								"	from (    \n"+
								"		select A.tokenid,a.direction2,'本位币' as dataname,sum(a.debitocc-a.creditocc) as occ1 \n"+    
								"		from c_account a   ,"+tempTable+" b \n"+
								"		where a.SubYearMonth * 12  + a.SubMonth >=  '"+strStartYearMonth+"'      \n"+
								"		and a.SubYearMonth * 12  + a.SubMonth <=  '"+strEndYearMonth+"'     \n"+
								sqlType + 
								"		and a.tokenid = b.tokenid \n"+
//								"		and a.dataname=if(b.dataname='本位币','0',b.dataname) \n" +
								"		and a.dataname=0 \n" +
								"		group by a.tokenid    \n" +
								"	) a left join (    \n" +
								"		select a.tid as tokenid, \n"+
								"			'本位币' as dataname, \n"+
								"			sum(a.occurvalue* a.dirction) as occ2 from (    \n"+
								"			select  distinct a.*  , b.tokenid as tid    \n"+
								"			from c_subjectentry a  ,"+tempTable+" b,(" +
								"				select distinct subjectid,accname,SubjectFullName1,SubjectFullName2,tokenid  " +
								"				from c_account c " +
								"				where 1=1 \n" +
								" 				and (c.subjectfullname2 = '"+SubjectName+"' or c.subjectfullname2 like '"+SubjectName+"/%' )  \n" +
								"				and c.SubYearMonth * 12  + c.SubMonth >=  '"+strStartYearMonth+"'  \n" +
								"				and c.SubYearMonth * 12  + c.SubMonth <=  '"+strEndYearMonth+"'  \n" +
								"				" +
								"			) c \n"+
								"			where substring(a.VchDate,1,4) * 12  +substring(a.VchDate,6,2) >=  '"+strStartYearMonth+"'      \n"+
								"			and substring(a.VchDate,1,4) * 12  +substring(a.VchDate,6,2) <=  '"+strEndYearMonth+"'     \n"+
								carrySubjectID + 
								"			and (a.SubjectFullName1 = c.SubjectFullName1 or a.SubjectFullName1 like concat(c.SubjectFullName1,'/%')) \n" +
								"			and a.property like '%2%'   \n"+
								"			and b.tokenid = c.tokenid \n"+
								"		) a    \n"+
								"		group by a.tid  \n"+
								"	) b on a.tokenid = b.tokenid and a.dataname=b.dataname \n"+
								")b \n"+
								"set a.CarryOver=b.CarryOver \n"+ 
								"where a.tokenid = b.tokenid  \n";
							}
						}else{
							//核算结转数
							if (preToken.indexOf("cur")>-1){
								//外币
								sql="update "+tempTable+" a, \n"  +
								"	( \n"+
								"	select (ifnull( occ1,0) - ifnull(occ2,0))*a.direction2  as "+preToken+"CarryOver   ,a.accid,a.assitemid ,a.tokenid,a.dataname,a.sid \n"+
								"	from (    \n"+
								"		select b.sid,A.AssTotalName1 as tokenid,a.accid,a.assitemid ,a.direction2,b.dataname,SUM(occ1) AS occ1 \n"+    
								"		from ( \n" +
								"			select A.AssTotalName1 ,a.accid,a.assitemid ,a.direction2,a.dataname,sum(a.debitocc-a.creditocc) as occ1 \n" +
								"			from c_assitementryaccall a \n" +
								"			where a.SubYearMonth * 12  + a.SubMonth >=  '"+strStartYearMonth+"'      \n"+
								"			and a.SubYearMonth * 12  + a.SubMonth <=  '"+strEndYearMonth+"'     \n"+
								"			group by a.accid,a.assitemid , dataname   \n" +
								"		)  a   ,"+tempTable+" b \n"+
								"		where 1=1      \n"+
								"		and a.AssTotalName1 = b.tokenid \n"+
								" 		and b.sid like concat('%`',a.accid,'`',a.assitemid,'%')" +
								"		and a.dataname=if(b.dataname='本位币','0',b.dataname) \n" +
								"		GROUP BY b.sid " +
								
								"	) a left join (    \n" +
								"		select  sid,a.subjectid as accid ,a.assitemid ,Currency as dataname, sum(a.CurrValue* a.dirction) as occ2 " +
								"			from (    \n"+
								"			select  distinct a.*  ,b.sid    \n"+
								"			from c_assitementry a  ,"+tempTable+" b \n"+
								"			where substring(a.VchDate,1,4) * 12  +substring(a.VchDate,6,2) >=  '"+strStartYearMonth+"'      \n"+
								"			and substring(a.VchDate,1,4) * 12  +substring(a.VchDate,6,2) <=  '"+strEndYearMonth+"'     \n"+
								"			and a.property like '%2%'   \n"+
								" 			and b.sid like concat('%`',a.subjectid,'`',a.assitemid,'%')" +
								"		) a    \n"+
								"		group by a.sid,a.Currency  \n"+
								"	) b on a.sid = b.sid  and a.dataname=b.dataname \n"+
								")b \n"+
								"set a."+preToken+"CarryOver=b."+preToken+"CarryOver \n"+
								"where a.tokenid = b.tokenid and a.sid =b.sid and a.dataname=b.dataname \n";
								
								
							}else{
								//本位币
								sql="update "+tempTable+" a, \n"  +
								"	( \n"+
								"	select (ifnull( occ1,0) - ifnull(occ2,0))*a.direction2  as CarryOver   ,a.accid,a.assitemid ,a.tokenid,a.dataname,a.sid \n"+
								"	from (    \n"+
								
								"		select b.sid,A.AssTotalName1 as tokenid,a.accid,a.assitemid ,a.direction2, a.dataname, SUM(occ1) AS occ1   \n"+    
								"		from (" +
								"			select A.AssTotalName1 ,a.accid,a.assitemid ,a.direction2,'本位币' as dataname,sum(a.debitocc-a.creditocc) as occ1 " +
								"			from c_assitementryacc a " +
								"			where a.SubYearMonth * 12  + a.SubMonth >=  '"+strStartYearMonth+"'      \n"+
								"			and a.SubYearMonth * 12  + a.SubMonth <=  '"+strEndYearMonth+"'     \n" +
								"			and a.dataname=0 \n" +
								"			group by a.accid,a.assitemid , dataname   \n" +
								
								"		)  a   ,"+tempTable+" b \n"+
								"		where 1=1     \n"+
								"		and a.AssTotalName1 = b.tokenid \n"+
								" 		and b.sid like concat('%`',a.accid,'`',a.assitemid,'%')" +
								"		GROUP BY b.sid " +
								
								"	) a left join (    \n" +
								"		select sid,a.subjectid as accid ,a.assitemid ,'本位币' as dataname, sum(a.AssItemSum* a.dirction) as occ2 " +
								"			from (    \n"+
								"			select  distinct a.* ,b.sid    \n"+
								"			from c_assitementry a  ,"+tempTable+" b \n"+
								"			where substring(a.VchDate,1,4) * 12  +substring(a.VchDate,6,2) >=  '"+strStartYearMonth+"'      \n"+
								"			and substring(a.VchDate,1,4) * 12  +substring(a.VchDate,6,2) <=  '"+strEndYearMonth+"'     \n"+
								"			and a.property like '%2%'   \n"+
								" 			and b.sid like concat('%`',a.subjectid,'`',a.assitemid,'%')" +
								"		) a    \n"+
								"		group by a.sid    \n"+
								"	) b on a.sid = b.sid and a.dataname=b.dataname \n"+
								")b \n"+
								"set a.CarryOver=b.CarryOver \n"+
								"where a.tokenid = b.tokenid and a.sid =b.sid  \n";
								
							}
							
						}
						
						
//						System.out.println("结转数:"+sql);
						ps = this.myConn.prepareStatement(sql);
						
						ps.execute();
						DbUtil.close(ps);
						if (strGroup[iGroup].indexOf("7")>-1 
							||strGroup[iGroup].indexOf("k")>-1 
							){
							continue ;
						}
					}
					
					
					/**
					 *	当年调整 ，rectify在此赋值
					 */
					if (strGroup[iGroup].indexOf("6")>-1 
						||strGroup[iGroup].indexOf("d")>-1 
						
						//审定数（本位币/原币），需要作余额分析，取得期末调整＋期末重分类等
						||strGroup[iGroup].indexOf("3")>-1
						||strGroup[iGroup].indexOf("a")>-1
						
						//审定账龄（本位币/原币）都需要作余额分析，取得期末调整＋期末重分类等
						||strGroup[iGroup].indexOf("5")>-1
						||strGroup[iGroup].indexOf("c")>-1
						
						//m: 审定结转数；n：原币审定结转数
						||strGroup[iGroup].indexOf("m")>-1 
						||strGroup[iGroup].indexOf("n")>-1 
						){
						
						String strSql = "";
						String strSql1 = "";
						if (!"0".equals(DataName)){
							if("科目".equals(isSubject)){	
								strSql = "concat(substring(accpackageid,7),'`',subjectid) as sid,dataname,";
							}else{
								strSql = "concat(substring(accpackageid,7),'`',subjectid,'`',assitemid) as sid,dataname,";
							}
						}else{
							if("科目".equals(isSubject)){	
								strSql = "concat(substring(accpackageid,7),'`',subjectid) as sid,'本位币' as dataname,";
								strSql1 = " sum(a.DebitTotalOcc0"+strName+") as "+preToken+"DebitTotalOcc0,   \n" +
										" sum(a.CreditTotalOcc0"+strName+") as "+preToken+"CreditTotalOcc0 ,   \n" ;
							}else{
								strSql = "concat(substring(accpackageid,7),'`',subjectid,'`',assitemid) as sid,'本位币' as dataname,";
							}
						}
						
						strSql = "b.tokenid,b.sName,b.dataname,b.sid,";
						
						if (strGroup[iGroup].indexOf("3")>-1
							||strGroup[iGroup].indexOf("a")>-1
							||strGroup[iGroup].indexOf("5")>-1
							||strGroup[iGroup].indexOf("c")>-1
							||strGroup[iGroup].indexOf("m")>-1 
							||strGroup[iGroup].indexOf("n")>-1 
							){
							strUpdate=" a."+preToken+"debittotalocc1="+strRecSign+"b."+preToken+"debittotalocc1," +
							" a."+preToken+"debittotalocc2="+strRecSign+"b."+preToken+"debittotalocc2," +
							" a."+preToken+"debittotalocc4="+strRecSign+"b."+preToken+"debittotalocc4," +
							" a."+preToken+"debittotalocc5="+strRecSign+"b."+preToken+"debittotalocc5," +
							" a."+preToken+"debittotalocc6="+strRecSign+"b."+preToken+"debittotalocc6," +
							" a."+preToken+"credittotalocc1="+strRecSign+"b."+preToken+"credittotalocc1," +
							" a."+preToken+"credittotalocc2="+strRecSign+"b."+preToken+"credittotalocc2," +
							" a."+preToken+"credittotalocc4="+strRecSign+"b."+preToken+"credittotalocc4," +
							" a."+preToken+"credittotalocc5="+strRecSign+"b."+preToken+"credittotalocc5," +
							" a."+preToken+"credittotalocc6="+strRecSign+"b."+preToken+"credittotalocc6" ;
						}
						
						sql = " select  " + strSql + strSql1 +
							" sum(a.DebitTotalOcc1"+strName+") as "+preToken+"DebitTotalOcc1,   \n" +
							" sum(a.CreditTotalOcc1"+strName+") as "+preToken+"CreditTotalOcc1,  \n" +
							" sum(a.DebitTotalOcc2"+strName+") as "+preToken+"DebitTotalOcc2,   \n" +
							" sum(a.CreditTotalOcc2"+strName+") as "+preToken+"CreditTotalOcc2,  \n" +
							" sum(a.DebitTotalOcc3"+strName+") as "+preToken+"DebitTotalOcc3,   \n" +
							" sum(a.CreditTotalOcc3"+strName+") as "+preToken+"CreditTotalOcc3,  \n" +
							" sum(a.DebitTotalOcc4"+strName+") as "+preToken+"DebitTotalOcc4,   \n" +
							" sum(a.CreditTotalOcc4"+strName+") as "+preToken+"CreditTotalOcc4,  \n" +
							" sum(a.DebitTotalOcc5"+strName+") as "+preToken+"DebitTotalOcc5,   \n" +
							" sum(a.CreditTotalOcc5"+strName+") as "+preToken+"CreditTotalOcc5,  \n" +
							" sum(a.DebitTotalOcc6"+strName+") as "+preToken+"DebitTotalOcc6,   \n" +
							" sum(a.CreditTotalOcc6"+strName+") as "+preToken+"CreditTotalOcc6  \n" + 
							" from " + tableName[1] + " a ,"+tempTable+" b " +
							" where projectid ='"+projectID+"' \n" +
							//加过滤，避免太多
							" and (  \n" +
							//" 		abs(DebitTotalOcc0)+abs(CreditTotalOcc0) \n" +
							" 		abs(a.DebitTotalOcc1"+strName+")+abs(a.CreditTotalOcc1"+strName+") \n" +
							" 		+abs(a.DebitTotalOcc2"+strName+")+abs(a.CreditTotalOcc2"+strName+") \n" +
							" 		+abs(a.DebitTotalOcc3"+strName+")+abs(a.CreditTotalOcc3"+strName+") \n" +
							" 		+abs(a.DebitTotalOcc4"+strName+")+abs(a.CreditTotalOcc4"+strName+") \n" +
							" 		+abs(a.DebitTotalOcc5"+strName+")+abs(a.CreditTotalOcc5"+strName+") \n" +
							" 		+abs(a.DebitTotalOcc6"+strName+")+abs(a.CreditTotalOcc6"+strName+") \n" +
							" 		)>0 \n" ;
							//历年调整
						if(!"".equals(Year) && !"0".equals(Year) ){ 
							int pyear = Integer.parseInt(projectEndYear) + Integer.parseInt(Year);
							sql += " and a.yearrectify = '"+pyear +"' ";
						}		
						if("科目".equals(isSubject)){		
							sql += " AND CONCAT(',',b.sid,',') LIKE CONCAT('%,',SUBSTRING(a.accpackageid,7),'`',a.subjectid,',%')  " ;
						}else{
							sql += " AND CONCAT(',',b.sid,',') LIKE CONCAT('%,',SUBSTRING(a.accpackageid,7),'`',a.subjectid,'`',a.assitemid,',%')  " ;
						}
						sql += " GROUP BY b.tokenid,b.sName,b.dataname " ; 
						
						sql = "update "+tempTable+" a join (" +	sql + " ) b \n" +
							" ON a.tokenid = b.tokenid AND a.sName= b.sName AND a.dataname = b.dataname   \n" +
							" set " + strUpdate ;

//						System.out.println(sql);
						ps = this.myConn.prepareStatement(sql);
						
						ps.execute();
						DbUtil.close(ps);
						if (strGroup[iGroup].indexOf("6")>-1 
							||strGroup[iGroup].indexOf("d")>-1 
							){
						continue ;//返回
						}
						
					}
					
					/**
					 * 调整加类型："+preToken1+"recxm:调整细目
					 */
					if (strGroup[iGroup].indexOf("r")>-1
						||strGroup[iGroup].indexOf("u")>-1
						){
						
						String strSql = "",strSql2 = "",strSql3 = "",strSql4 = "",preToken1 = "";
						
						if(strGroup[iGroup].indexOf("u")==-1){

						}else{
							preToken1 = "cur";
							strSql3 = " a.dataname, ";
							strSql4 = " and if(a.dataname='0','本位币',a.dataname) = b.dataname ";
						}
						
//						String vchdate0 = projectEndYear + "-12-31"; //本年 
//						String vchdate1 = String.valueOf(Integer.parseInt(projectEndYear) - 1) + "-12-31"; //上年
						strUpdate = 
						//#调整
						"a."+preToken1+"recxm1DebitTotalOcc1 = "+strRecSign+"b."+preToken1+"recxm1DebitTotalOcc1,a."+preToken1+"recxm1CreditTotalOcc1 = "+strRecSign+"b."+preToken1+"recxm1CreditTotalOcc1," +
						"a."+preToken1+"recxm1DebitTotalOcc4 = "+strRecSign+"b."+preToken1+"recxm1DebitTotalOcc4,a."+preToken1+"recxm1CreditTotalOcc4 = "+strRecSign+"b."+preToken1+"recxm1CreditTotalOcc4," +
						"a."+preToken1+"recxm2DebitTotalOcc1 = "+strRecSign+"b."+preToken1+"recxm2DebitTotalOcc1,a."+preToken1+"recxm2CreditTotalOcc1 = "+strRecSign+"b."+preToken1+"recxm2CreditTotalOcc1," +
						"a."+preToken1+"recxm2DebitTotalOcc4 = "+strRecSign+"b."+preToken1+"recxm2DebitTotalOcc4,a."+preToken1+"recxm2CreditTotalOcc4 = "+strRecSign+"b."+preToken1+"recxm2CreditTotalOcc4," +
						"a."+preToken1+"recxm3DebitTotalOcc1 = "+strRecSign+"b."+preToken1+"recxm3DebitTotalOcc1,a."+preToken1+"recxm3CreditTotalOcc1 = "+strRecSign+"b."+preToken1+"recxm3CreditTotalOcc1," +
						"a."+preToken1+"recxm3DebitTotalOcc4 = "+strRecSign+"b."+preToken1+"recxm3DebitTotalOcc4,a."+preToken1+"recxm3CreditTotalOcc4 = "+strRecSign+"b."+preToken1+"recxm3CreditTotalOcc4," +
						"a."+preToken1+"recxm4DebitTotalOcc1 = "+strRecSign+"b."+preToken1+"recxm4DebitTotalOcc1,a."+preToken1+"recxm4CreditTotalOcc1 = "+strRecSign+"b."+preToken1+"recxm4CreditTotalOcc1," +
						"a."+preToken1+"recxm4DebitTotalOcc4 = "+strRecSign+"b."+preToken1+"recxm4DebitTotalOcc4,a."+preToken1+"recxm4CreditTotalOcc4 = "+strRecSign+"b."+preToken1+"recxm4CreditTotalOcc4," +
						"a."+preToken1+"recxm5DebitTotalOcc1 = "+strRecSign+"b."+preToken1+"recxm5DebitTotalOcc1,a."+preToken1+"recxm5CreditTotalOcc1 = "+strRecSign+"b."+preToken1+"recxm5CreditTotalOcc1," +
						"a."+preToken1+"recxm5DebitTotalOcc4 = "+strRecSign+"b."+preToken1+"recxm5DebitTotalOcc4,a."+preToken1+"recxm5CreditTotalOcc4 = "+strRecSign+"b."+preToken1+"recxm5CreditTotalOcc4," +
						"a."+preToken1+"recxm6DebitTotalOcc1 = "+strRecSign+"b."+preToken1+"recxm6DebitTotalOcc1,a."+preToken1+"recxm6CreditTotalOcc1 = "+strRecSign+"b."+preToken1+"recxm6CreditTotalOcc1," +
						"a."+preToken1+"recxm6DebitTotalOcc4 = "+strRecSign+"b."+preToken1+"recxm6DebitTotalOcc4,a."+preToken1+"recxm6CreditTotalOcc4 = "+strRecSign+"b."+preToken1+"recxm6CreditTotalOcc4," +
						"a."+preToken1+"recxm7DebitTotalOcc1 = "+strRecSign+"b."+preToken1+"recxm7DebitTotalOcc1,a."+preToken1+"recxm7CreditTotalOcc1 = "+strRecSign+"b."+preToken1+"recxm7CreditTotalOcc1," +
						"a."+preToken1+"recxm7DebitTotalOcc4 = "+strRecSign+"b."+preToken1+"recxm7DebitTotalOcc4,a."+preToken1+"recxm7CreditTotalOcc4 = "+strRecSign+"b."+preToken1+"recxm7CreditTotalOcc4," +

						//重分类
						"a."+preToken1+"recxm1DebitTotalOcc2 = "+strRecSign+"b."+preToken1+"recxm1DebitTotalOcc2,a."+preToken1+"recxm1CreditTotalOcc2 = "+strRecSign+"b."+preToken1+"recxm1CreditTotalOcc2," +
						"a."+preToken1+"recxm1DebitTotalOcc5 = "+strRecSign+"b."+preToken1+"recxm1DebitTotalOcc5,a."+preToken1+"recxm1CreditTotalOcc5 = "+strRecSign+"b."+preToken1+"recxm1CreditTotalOcc5," +
						"a."+preToken1+"recxm2DebitTotalOcc2 = "+strRecSign+"b."+preToken1+"recxm2DebitTotalOcc2,a."+preToken1+"recxm2CreditTotalOcc2 = "+strRecSign+"b."+preToken1+"recxm2CreditTotalOcc2," +
						"a."+preToken1+"recxm2DebitTotalOcc5 = "+strRecSign+"b."+preToken1+"recxm2DebitTotalOcc5,a."+preToken1+"recxm2CreditTotalOcc5 = "+strRecSign+"b."+preToken1+"recxm2CreditTotalOcc5," +
						"a."+preToken1+"recxm3DebitTotalOcc2 = "+strRecSign+"b."+preToken1+"recxm3DebitTotalOcc2,a."+preToken1+"recxm3CreditTotalOcc2 = "+strRecSign+"b."+preToken1+"recxm3CreditTotalOcc2," +
						"a."+preToken1+"recxm3DebitTotalOcc5 = "+strRecSign+"b."+preToken1+"recxm3DebitTotalOcc5,a."+preToken1+"recxm3CreditTotalOcc5 = "+strRecSign+"b."+preToken1+"recxm3CreditTotalOcc5," +
						"a."+preToken1+"recxm4DebitTotalOcc2 = "+strRecSign+"b."+preToken1+"recxm4DebitTotalOcc2,a."+preToken1+"recxm4CreditTotalOcc2 = "+strRecSign+"b."+preToken1+"recxm4CreditTotalOcc2," +
						"a."+preToken1+"recxm4DebitTotalOcc5 = "+strRecSign+"b."+preToken1+"recxm4DebitTotalOcc5,a."+preToken1+"recxm4CreditTotalOcc5 = "+strRecSign+"b."+preToken1+"recxm4CreditTotalOcc5," +
						"a."+preToken1+"recxm5DebitTotalOcc2 = "+strRecSign+"b."+preToken1+"recxm5DebitTotalOcc2,a."+preToken1+"recxm5CreditTotalOcc2 = "+strRecSign+"b."+preToken1+"recxm5CreditTotalOcc2," +
						"a."+preToken1+"recxm5DebitTotalOcc5 = "+strRecSign+"b."+preToken1+"recxm5DebitTotalOcc5,a."+preToken1+"recxm5CreditTotalOcc5 = "+strRecSign+"b."+preToken1+"recxm5CreditTotalOcc5," +
						"a."+preToken1+"recxm6DebitTotalOcc2 = "+strRecSign+"b."+preToken1+"recxm6DebitTotalOcc2,a."+preToken1+"recxm6CreditTotalOcc2 = "+strRecSign+"b."+preToken1+"recxm6CreditTotalOcc2," +
						"a."+preToken1+"recxm6DebitTotalOcc5 = "+strRecSign+"b."+preToken1+"recxm6DebitTotalOcc5,a."+preToken1+"recxm6CreditTotalOcc5 = "+strRecSign+"b."+preToken1+"recxm6CreditTotalOcc5," +
						"a."+preToken1+"recxm7DebitTotalOcc2 = "+strRecSign+"b."+preToken1+"recxm7DebitTotalOcc2,a."+preToken1+"recxm7CreditTotalOcc2 = "+strRecSign+"b."+preToken1+"recxm7CreditTotalOcc2," +
						"a."+preToken1+"recxm7DebitTotalOcc5 = "+strRecSign+"b."+preToken1+"recxm7DebitTotalOcc5,a."+preToken1+"recxm7CreditTotalOcc5 = "+strRecSign+"b."+preToken1+"recxm7CreditTotalOcc5," +

						//不符未调
						"a."+preToken1+"recxm2DebitTotalOcc3 = "+strRecSign+"b."+preToken1+"recxm2DebitTotalOcc3,a."+preToken1+"recxm2CreditTotalOcc3 = "+strRecSign+"b."+preToken1+"recxm2CreditTotalOcc3," +
						"a."+preToken1+"recxm2DebitTotalOcc0 = "+strRecSign+"b."+preToken1+"recxm2DebitTotalOcc0,a."+preToken1+"recxm2CreditTotalOcc0 = "+strRecSign+"b."+preToken1+"recxm2CreditTotalOcc0, " +
						"a."+preToken1+"recxm5DebitTotalOcc3 = "+strRecSign+"b."+preToken1+"recxm5DebitTotalOcc3,a."+preToken1+"recxm5CreditTotalOcc3 = "+strRecSign+"b."+preToken1+"recxm5CreditTotalOcc3," +
						"a."+preToken1+"recxm5DebitTotalOcc0 = "+strRecSign+"b."+preToken1+"recxm5DebitTotalOcc0,a."+preToken1+"recxm5CreditTotalOcc0 = "+strRecSign+"b."+preToken1+"recxm5CreditTotalOcc0 ";
						
						strSql = "b.tokenid,b.sName,b.dataname,b.sid," +
						//调整
						"sum(a."+preToken1+"recxm1DebitTotalOcc1) "+preToken1+"recxm1DebitTotalOcc1,sum(a."+preToken1+"recxm1CreditTotalOcc1) "+preToken1+"recxm1CreditTotalOcc1," +
						"sum(a."+preToken1+"recxm1DebitTotalOcc4) "+preToken1+"recxm1DebitTotalOcc4,sum(a."+preToken1+"recxm1CreditTotalOcc4) "+preToken1+"recxm1CreditTotalOcc4," +
						"sum(a."+preToken1+"recxm2DebitTotalOcc1) "+preToken1+"recxm2DebitTotalOcc1,sum(a."+preToken1+"recxm2CreditTotalOcc1) "+preToken1+"recxm2CreditTotalOcc1," +
						"sum(a."+preToken1+"recxm2DebitTotalOcc4) "+preToken1+"recxm2DebitTotalOcc4,sum(a."+preToken1+"recxm2CreditTotalOcc4) "+preToken1+"recxm2CreditTotalOcc4," +
						"sum(a."+preToken1+"recxm3DebitTotalOcc1) "+preToken1+"recxm3DebitTotalOcc1,sum(a."+preToken1+"recxm3CreditTotalOcc1) "+preToken1+"recxm3CreditTotalOcc1," +
						"sum(a."+preToken1+"recxm3DebitTotalOcc4) "+preToken1+"recxm3DebitTotalOcc4,sum(a."+preToken1+"recxm3CreditTotalOcc4) "+preToken1+"recxm3CreditTotalOcc4," +
						"sum(a."+preToken1+"recxm4DebitTotalOcc1) "+preToken1+"recxm4DebitTotalOcc1,sum(a."+preToken1+"recxm4CreditTotalOcc1) "+preToken1+"recxm4CreditTotalOcc1," +
						"sum(a."+preToken1+"recxm4DebitTotalOcc4) "+preToken1+"recxm4DebitTotalOcc4,sum(a."+preToken1+"recxm4CreditTotalOcc4) "+preToken1+"recxm4CreditTotalOcc4," +
						"sum(a."+preToken1+"recxm5DebitTotalOcc1) "+preToken1+"recxm5DebitTotalOcc1,sum(a."+preToken1+"recxm5CreditTotalOcc1) "+preToken1+"recxm5CreditTotalOcc1," +
						"sum(a."+preToken1+"recxm5DebitTotalOcc4) "+preToken1+"recxm5DebitTotalOcc4,sum(a."+preToken1+"recxm5CreditTotalOcc4) "+preToken1+"recxm5CreditTotalOcc4," +
						"sum(a."+preToken1+"recxm6DebitTotalOcc1) "+preToken1+"recxm6DebitTotalOcc1,sum(a."+preToken1+"recxm6CreditTotalOcc1) "+preToken1+"recxm6CreditTotalOcc1," +
						"sum(a."+preToken1+"recxm6DebitTotalOcc4) "+preToken1+"recxm6DebitTotalOcc4,sum(a."+preToken1+"recxm6CreditTotalOcc4) "+preToken1+"recxm6CreditTotalOcc4," +
						"sum(a."+preToken1+"recxm7DebitTotalOcc1) "+preToken1+"recxm7DebitTotalOcc1,sum(a."+preToken1+"recxm7CreditTotalOcc1) "+preToken1+"recxm7CreditTotalOcc1," +
						"sum(a."+preToken1+"recxm7DebitTotalOcc4) "+preToken1+"recxm7DebitTotalOcc4,sum(a."+preToken1+"recxm7CreditTotalOcc4) "+preToken1+"recxm7CreditTotalOcc4," +

						//重分类
						"sum(a."+preToken1+"recxm1DebitTotalOcc2) "+preToken1+"recxm1DebitTotalOcc2,sum(a."+preToken1+"recxm1CreditTotalOcc2) "+preToken1+"recxm1CreditTotalOcc2," +
						"sum(a."+preToken1+"recxm1DebitTotalOcc5) "+preToken1+"recxm1DebitTotalOcc5,sum(a."+preToken1+"recxm1CreditTotalOcc5) "+preToken1+"recxm1CreditTotalOcc5," +
						"sum(a."+preToken1+"recxm2DebitTotalOcc2) "+preToken1+"recxm2DebitTotalOcc2,sum(a."+preToken1+"recxm2CreditTotalOcc2) "+preToken1+"recxm2CreditTotalOcc2," +
						"sum(a."+preToken1+"recxm2DebitTotalOcc5) "+preToken1+"recxm2DebitTotalOcc5,sum(a."+preToken1+"recxm2CreditTotalOcc5) "+preToken1+"recxm2CreditTotalOcc5," +
						"sum(a."+preToken1+"recxm3DebitTotalOcc2) "+preToken1+"recxm3DebitTotalOcc2,sum(a."+preToken1+"recxm3CreditTotalOcc2) "+preToken1+"recxm3CreditTotalOcc2," +
						"sum(a."+preToken1+"recxm3DebitTotalOcc5) "+preToken1+"recxm3DebitTotalOcc5,sum(a."+preToken1+"recxm3CreditTotalOcc5) "+preToken1+"recxm3CreditTotalOcc5," +
						"sum(a."+preToken1+"recxm4DebitTotalOcc2) "+preToken1+"recxm4DebitTotalOcc2,sum(a."+preToken1+"recxm4CreditTotalOcc2) "+preToken1+"recxm4CreditTotalOcc2," +
						"sum(a."+preToken1+"recxm4DebitTotalOcc5) "+preToken1+"recxm4DebitTotalOcc5,sum(a."+preToken1+"recxm4CreditTotalOcc5) "+preToken1+"recxm4CreditTotalOcc5," +
						"sum(a."+preToken1+"recxm5DebitTotalOcc2) "+preToken1+"recxm5DebitTotalOcc2,sum(a."+preToken1+"recxm5CreditTotalOcc2) "+preToken1+"recxm5CreditTotalOcc2," +
						"sum(a."+preToken1+"recxm5DebitTotalOcc5) "+preToken1+"recxm5DebitTotalOcc5,sum(a."+preToken1+"recxm5CreditTotalOcc5) "+preToken1+"recxm5CreditTotalOcc5," +
						"sum(a."+preToken1+"recxm6DebitTotalOcc2) "+preToken1+"recxm6DebitTotalOcc2,sum(a."+preToken1+"recxm6CreditTotalOcc2) "+preToken1+"recxm6CreditTotalOcc2," +
						"sum(a."+preToken1+"recxm6DebitTotalOcc5) "+preToken1+"recxm6DebitTotalOcc5,sum(a."+preToken1+"recxm6CreditTotalOcc5) "+preToken1+"recxm6CreditTotalOcc5," +
						"sum(a."+preToken1+"recxm7DebitTotalOcc2) "+preToken1+"recxm7DebitTotalOcc2,sum(a."+preToken1+"recxm7CreditTotalOcc2) "+preToken1+"recxm7CreditTotalOcc2," +
						"sum(a."+preToken1+"recxm7DebitTotalOcc5) "+preToken1+"recxm7DebitTotalOcc5,sum(a."+preToken1+"recxm7CreditTotalOcc5) "+preToken1+"recxm7CreditTotalOcc5," +

						//不符未调
						"sum(a."+preToken1+"recxm2DebitTotalOcc3) "+preToken1+"recxm2DebitTotalOcc3,sum(a."+preToken1+"recxm2CreditTotalOcc3) "+preToken1+"recxm2CreditTotalOcc3," +
						"sum(a."+preToken1+"recxm2DebitTotalOcc0) "+preToken1+"recxm2DebitTotalOcc0,sum(a."+preToken1+"recxm2CreditTotalOcc0) "+preToken1+"recxm2CreditTotalOcc0," +
						"sum(a."+preToken1+"recxm5DebitTotalOcc3) "+preToken1+"recxm5DebitTotalOcc3,sum(a."+preToken1+"recxm5CreditTotalOcc3) "+preToken1+"recxm5CreditTotalOcc3," +
						"sum(a."+preToken1+"recxm5DebitTotalOcc0) "+preToken1+"recxm5DebitTotalOcc0,sum(a."+preToken1+"recxm5CreditTotalOcc0) "+preToken1+"recxm5CreditTotalOcc0 " ;

						
						strSql2 = 
						//调整
						"	sum(case when a.itemtype='客户已调' and a.yearrectify=0  then DebitTotalOcc1 else 0 end) "+preToken1+"recxm1DebitTotalOcc1, " +
						"	sum(case when a.itemtype='客户已调' and a.yearrectify=0  then CreditTotalOcc1 else 0 end) "+preToken1+"recxm1CreditTotalOcc1, " +
						"	sum(case when a.itemtype='客户已调' and a.yearrectify=-1  then DebitTotalOcc1 else 0 end) "+preToken1+"recxm1DebitTotalOcc4, " +
						"	sum(case when a.itemtype='客户已调' and a.yearrectify=-1  then CreditTotalOcc1 else 0 end) "+preToken1+"recxm1CreditTotalOcc4,  " +
						
						"	sum(case when a.itemtype='客户未调' and a.yearrectify=0  then DebitTotalOcc1 else 0 end) "+preToken1+"recxm2DebitTotalOcc1, " +
						"	sum(case when a.itemtype='客户未调' and a.yearrectify=0  then CreditTotalOcc1 else 0 end) "+preToken1+"recxm2CreditTotalOcc1, " +
						"	sum(case when a.itemtype='客户未调' and a.yearrectify=-1  then DebitTotalOcc1 else 0 end) "+preToken1+"recxm2DebitTotalOcc4, " +
						"	sum(case when a.itemtype='客户未调' and a.yearrectify=-1  then CreditTotalOcc1 else 0 end) "+preToken1+"recxm2CreditTotalOcc4,  " +
						
						"	sum(case when a.itemtype='帐表差异' and a.yearrectify=0  then DebitTotalOcc1 else 0 end) "+preToken1+"recxm3DebitTotalOcc1, " +
						"	sum(case when a.itemtype='帐表差异' and a.yearrectify=0  then CreditTotalOcc1 else 0 end) "+preToken1+"recxm3CreditTotalOcc1, " +
						"	sum(case when a.itemtype='帐表差异' and a.yearrectify=-1  then DebitTotalOcc1 else 0 end) "+preToken1+"recxm3DebitTotalOcc4, " +
						"	sum(case when a.itemtype='帐表差异' and a.yearrectify=-1  then CreditTotalOcc1 else 0 end) "+preToken1+"recxm3CreditTotalOcc4,  " +
						
						"	sum(case when a.itemtype='追溯调整' and a.yearrectify=0  then DebitTotalOcc1 else 0 end) "+preToken1+"recxm4DebitTotalOcc1, " +
						"	sum(case when a.itemtype='追溯调整' and a.yearrectify=0  then CreditTotalOcc1 else 0 end) "+preToken1+"recxm4CreditTotalOcc1, " +
						"	sum(case when a.itemtype='追溯调整' and a.yearrectify=-1  then DebitTotalOcc1 else 0 end) "+preToken1+"recxm4DebitTotalOcc4, " +
						"	sum(case when a.itemtype='追溯调整' and a.yearrectify=-1  then CreditTotalOcc1 else 0 end) "+preToken1+"recxm4CreditTotalOcc4,  " +

						"	sum(case when a.itemtype='明显微小错报' and a.yearrectify=0  then DebitTotalOcc1 else 0 end) "+preToken1+"recxm5DebitTotalOcc1, " +
						"	sum(case when a.itemtype='明显微小错报' and a.yearrectify=0  then CreditTotalOcc1 else 0 end) "+preToken1+"recxm5CreditTotalOcc1, " +
						"	sum(case when a.itemtype='明显微小错报' and a.yearrectify=-1  then DebitTotalOcc1 else 0 end) "+preToken1+"recxm5DebitTotalOcc4, " +
						"	sum(case when a.itemtype='明显微小错报' and a.yearrectify=-1  then CreditTotalOcc1 else 0 end) "+preToken1+"recxm5CreditTotalOcc4,  " +
						
						"	sum(case when a.itemtype='客户同意' and a.yearrectify=0  then DebitTotalOcc1 else 0 end) "+preToken1+"recxm6DebitTotalOcc1, " +
						"	sum(case when a.itemtype='客户同意' and a.yearrectify=0  then CreditTotalOcc1 else 0 end) "+preToken1+"recxm6CreditTotalOcc1, " +
						"	sum(case when a.itemtype='客户同意' and a.yearrectify=-1  then DebitTotalOcc1 else 0 end) "+preToken1+"recxm6DebitTotalOcc4, " +
						"	sum(case when a.itemtype='客户同意' and a.yearrectify=-1  then CreditTotalOcc1 else 0 end) "+preToken1+"recxm6CreditTotalOcc4,  " +
						
						"	sum(case when a.itemtype='客户不同意' and a.yearrectify=0  then DebitTotalOcc1 else 0 end) "+preToken1+"recxm7DebitTotalOcc1, " +
						"	sum(case when a.itemtype='客户不同意' and a.yearrectify=0  then CreditTotalOcc1 else 0 end) "+preToken1+"recxm7CreditTotalOcc1, " +
						"	sum(case when a.itemtype='客户不同意' and a.yearrectify=-1  then DebitTotalOcc1 else 0 end) "+preToken1+"recxm7DebitTotalOcc4, " +
						"	sum(case when a.itemtype='客户不同意' and a.yearrectify=-1  then CreditTotalOcc1 else 0 end) "+preToken1+"recxm7CreditTotalOcc4,  " +

						//重分类
						"	sum(case when a.itemtype='客户已调' and a.yearrectify=0  then DebitTotalOcc2 else 0 end) "+preToken1+"recxm1DebitTotalOcc2, " +
						"	sum(case when a.itemtype='客户已调' and a.yearrectify=0  then CreditTotalOcc2 else 0 end) "+preToken1+"recxm1CreditTotalOcc2, " +
						"	sum(case when a.itemtype='客户已调' and a.yearrectify=-1  then DebitTotalOcc2 else 0 end) "+preToken1+"recxm1DebitTotalOcc5,  " +
						"	sum(case when a.itemtype='客户已调' and a.yearrectify=-1  then CreditTotalOcc2 else 0 end) "+preToken1+"recxm1CreditTotalOcc5,  " +
						
						"	sum(case when a.itemtype='客户未调' and a.yearrectify=0  then DebitTotalOcc2 else 0 end) "+preToken1+"recxm2DebitTotalOcc2, " +
						"	sum(case when a.itemtype='客户未调' and a.yearrectify=0  then CreditTotalOcc2 else 0 end) "+preToken1+"recxm2CreditTotalOcc2, " +
						"	sum(case when a.itemtype='客户未调' and a.yearrectify=-1  then DebitTotalOcc2 else 0 end) "+preToken1+"recxm2DebitTotalOcc5,  " +
						"	sum(case when a.itemtype='客户未调' and a.yearrectify=-1  then CreditTotalOcc2 else 0 end) "+preToken1+"recxm2CreditTotalOcc5,  " +
						
						"	sum(case when a.itemtype='帐表差异' and a.yearrectify=0  then DebitTotalOcc2 else 0 end) "+preToken1+"recxm3DebitTotalOcc2, " +
						"	sum(case when a.itemtype='帐表差异' and a.yearrectify=0  then CreditTotalOcc2 else 0 end) "+preToken1+"recxm3CreditTotalOcc2, " +
						"	sum(case when a.itemtype='帐表差异' and a.yearrectify=-1  then DebitTotalOcc2 else 0 end) "+preToken1+"recxm3DebitTotalOcc5,  " +
						"	sum(case when a.itemtype='帐表差异' and a.yearrectify=-1  then CreditTotalOcc2 else 0 end) "+preToken1+"recxm3CreditTotalOcc5,  " +
						
						"	sum(case when a.itemtype='追溯调整' and a.yearrectify=0  then DebitTotalOcc2 else 0 end) "+preToken1+"recxm4DebitTotalOcc2, " +
						"	sum(case when a.itemtype='追溯调整' and a.yearrectify=0  then CreditTotalOcc2 else 0 end) "+preToken1+"recxm4CreditTotalOcc2, " +
						"	sum(case when a.itemtype='追溯调整' and a.yearrectify=-1  then DebitTotalOcc2 else 0 end) "+preToken1+"recxm4DebitTotalOcc5,  " +
						"	sum(case when a.itemtype='追溯调整' and a.yearrectify=-1  then CreditTotalOcc2 else 0 end) "+preToken1+"recxm4CreditTotalOcc5,  " +

						"	sum(case when a.itemtype='明显微小错报' and a.yearrectify=0  then DebitTotalOcc2 else 0 end) "+preToken1+"recxm5DebitTotalOcc2, " +
						"	sum(case when a.itemtype='明显微小错报' and a.yearrectify=0  then CreditTotalOcc2 else 0 end) "+preToken1+"recxm5CreditTotalOcc2, " +
						"	sum(case when a.itemtype='明显微小错报' and a.yearrectify=-1  then DebitTotalOcc2 else 0 end) "+preToken1+"recxm5DebitTotalOcc5, " +
						"	sum(case when a.itemtype='明显微小错报' and a.yearrectify=-1  then CreditTotalOcc2 else 0 end) "+preToken1+"recxm5CreditTotalOcc5,  " +
						
						"	sum(case when a.itemtype='客户同意' and a.yearrectify=0  then DebitTotalOcc2 else 0 end) "+preToken1+"recxm6DebitTotalOcc2, " +
						"	sum(case when a.itemtype='客户同意' and a.yearrectify=0  then CreditTotalOcc2 else 0 end) "+preToken1+"recxm6CreditTotalOcc2, " +
						"	sum(case when a.itemtype='客户同意' and a.yearrectify=-1  then DebitTotalOcc2 else 0 end) "+preToken1+"recxm6DebitTotalOcc5, " +
						"	sum(case when a.itemtype='客户同意' and a.yearrectify=-1  then CreditTotalOcc2 else 0 end) "+preToken1+"recxm6CreditTotalOcc5,  " +
						
						"	sum(case when a.itemtype='客户不同意' and a.yearrectify=0  then DebitTotalOcc2 else 0 end) "+preToken1+"recxm7DebitTotalOcc2, " +
						"	sum(case when a.itemtype='客户不同意' and a.yearrectify=0  then CreditTotalOcc2 else 0 end) "+preToken1+"recxm7CreditTotalOcc2, " +
						"	sum(case when a.itemtype='客户不同意' and a.yearrectify=-1  then DebitTotalOcc2 else 0 end) "+preToken1+"recxm7DebitTotalOcc5, " +
						"	sum(case when a.itemtype='客户不同意' and a.yearrectify=-1  then CreditTotalOcc2 else 0 end) "+preToken1+"recxm7CreditTotalOcc5,  " +

						//不符未调
						"	sum(case when a.itemtype='客户未调' and a.yearrectify=0  then DebitTotalOcc3 else 0 end) "+preToken1+"recxm2DebitTotalOcc3, " + 
						"	sum(case when a.itemtype='客户未调' and a.yearrectify=0  then CreditTotalOcc3 else 0 end) "+preToken1+"recxm2CreditTotalOcc3,  " +
						"	sum(case when a.itemtype='客户未调' and a.yearrectify=-1  then DebitTotalOcc3 else 0 end) "+preToken1+"recxm2DebitTotalOcc0,  " +
						"	sum(case when a.itemtype='客户未调' and a.yearrectify=-1  then CreditTotalOcc3 else 0 end) "+preToken1+"recxm2CreditTotalOcc0 , " +
						
						"	sum(case when a.itemtype='明显微小错报' and a.yearrectify=0  then DebitTotalOcc3 else 0 end) "+preToken1+"recxm5DebitTotalOcc3, " + 
						"	sum(case when a.itemtype='明显微小错报' and a.yearrectify=0  then CreditTotalOcc3 else 0 end) "+preToken1+"recxm5CreditTotalOcc3,  " +
						"	sum(case when a.itemtype='明显微小错报' and a.yearrectify=-1  then DebitTotalOcc3 else 0 end) "+preToken1+"recxm5DebitTotalOcc0,  " +
						"	sum(case when a.itemtype='明显微小错报' and a.yearrectify=-1  then CreditTotalOcc3 else 0 end) "+preToken1+"recxm5CreditTotalOcc0  " ;
						
						if("科目".equals(isSubject)){	
							//本位币或外币
							
							sql = "select a.accpackageid,a.subjectid,a.subjectname,a.subjectfullname1,a.subjectfullname2,a.isleaf,a.level0, " + 
							strSql3 + 
							//调整
							strSql2 + 
							"	from z_rectifysubject a " +
							"	where 1=1 " +
							"	and a.projectid='"+projectID+"'  " +
							"	and (yearrectify = 0 or yearrectify = -1) " +
							"	and (a.subjectfullname2 like '"+SubjectName+"/%' or a.subjectfullname2 = '"+SubjectName+"')  	 " +
							"	group by "+strSql3+" a.subjectid  " ;
							
							System.out.println(sql);
							
						}else{
							//核算
							//sql = "select a.accpackageid,a.projectid,a.subjectid,b.SubjectName,b.subjectfullname,b.tokenid,c.assitemid," + 
							sql = "select a.accpackageid,a.subjectid,a.AssItemID,a.AssItemName,a.subjectfullname1,a.subjectfullname2,a.isleaf,a.level0, " +
							strSql3 + 
							strSql2 + 
							"	from z_rectifyassitem a  " +
							"	where 1=1 " +
							"	and a.projectid='"+projectID+"'  " +
							"	and (yearrectify = 0 or yearrectify = -1) " +
							"	group by "+strSql3+" a.subjectid ,a.assitemid  " ;
							
						}
						
						
						sql = " select  " + strSql + " from ("+sql+") a,"+tempTable+" b " +
							" where 1=1 \n";
						if("科目".equals(isSubject)){		
							sql += strSql4 + " AND CONCAT(',',b.sid,',') LIKE CONCAT('%,',SUBSTRING(a.accpackageid,7),'`',a.subjectid,',%')  " ;
						}else{
							sql += strSql4 + " AND CONCAT(',',b.sid,',') LIKE CONCAT('%,',SUBSTRING(a.accpackageid,7),'`',a.subjectid,'`',a.assitemid,',%')  " ;
						}
						sql += " GROUP BY b.tokenid,b.sName,b.dataname " ; 
						
						
						sql = "update "+tempTable+" a join (" +	sql + " ) b \n" +
							" ON a.tokenid = b.tokenid AND a.sName= b.sName AND a.dataname = b.dataname   \n" +
							" set " + strUpdate ;
						System.out.println(sql);
						ps = this.myConn.prepareStatement(sql);
						ps.execute();
						DbUtil.close(ps);
					}
							
					
					/**
					 *	审定数
					 */
					if (strGroup[iGroup].indexOf("3")>-1
						||strGroup[iGroup].indexOf("a")>-1
						
						//审定账龄（本位币/原币）都需要作余额分析，取得审定数
						||strGroup[iGroup].indexOf("5")>-1
						||strGroup[iGroup].indexOf("c")>-1						
						
						){
						
						if("否".equals(svalue)){
							sql = "update "+tempTable+" a set " +
							" "+preToken+"sdremain = " +
							" "+preToken+"remain + direction2 * (" +
							" ("+preToken+"debittotalocc4 - "+preToken+"credittotalocc4) + " +
							" ("+preToken+"debittotalocc5 - "+preToken+"credittotalocc5) + " +
							" ("+preToken+"debittotalocc6 - "+preToken+"credittotalocc6))," +
							" "+preToken+"sdbalance = " +
							" "+preToken+"Balance + direction2 * (" +
							" ("+preToken+"debittotalocc1 - "+preToken+"credittotalocc1) + " +
							" ("+preToken+"debittotalocc2 - "+preToken+"credittotalocc2)) " +
							"";
						}else{
							sql = "update "+tempTable+" a set " +
							" "+preToken+"sdremain = " +
							" "+preToken+"remain + direction2 * (" +
							" ("+preToken+"debittotalocc4 - "+preToken+"credittotalocc4) + " +
							" ("+preToken+"debittotalocc5 - "+preToken+"credittotalocc5) + " +
							" ("+preToken+"debittotalocc6 - "+preToken+"credittotalocc6))," +
							" "+preToken+"sdbalance = " +
							" "+preToken+"Balance + direction2 * (" +
							" ("+preToken+"debittotalocc1 - "+preToken+"credittotalocc1) + " +
							" ("+preToken+"debittotalocc2 - "+preToken+"credittotalocc2) + " +
							" ("+preToken+"debittotalocc4 - "+preToken+"credittotalocc4) + " +
							" ("+preToken+"debittotalocc5 - "+preToken+"credittotalocc5) + " +
							" ("+preToken+"debittotalocc6 - "+preToken+"credittotalocc6))" +
							"";
						}
						
						////System.out.println(sql);
						ps = this.myConn.prepareStatement(sql);
						ps.execute();
						DbUtil.close(ps);
						if (strGroup[iGroup].indexOf("3")>-1
								||strGroup[iGroup].indexOf("a")>-1
							){
							continue ;//返回
						}
					}

					/**
					 * //m: 审定结转数；n：原币审定结转数
					 */
					if (strGroup[iGroup].indexOf("m")>-1
						||strGroup[iGroup].indexOf("n")>-1
						){
						sql = "update "+tempTable+" a set " +
						" "+preToken+"sdCarryOver = " +
						" "+preToken+"CarryOver + direction2 * (" +
						" ("+preToken+"debittotalocc1 - "+preToken+"credittotalocc1) + " +
						" ("+preToken+"debittotalocc2 - "+preToken+"credittotalocc2))";
						//System.out.println(sql);
						ps = this.myConn.prepareStatement(sql);
						ps.execute();
						DbUtil.close(ps);
						continue ;//返回
					}
					
					/**
					 * 账龄:4:本位币未审账龄、5是本位币审定账龄、b是原币未审账龄、c是原币审定账龄
					 * s:调整的账龄、t:重分类的账龄　
					 */
					if (strGroup[iGroup].indexOf("4")>-1
						||strGroup[iGroup].indexOf("5")>-1
						||strGroup[iGroup].indexOf("b")>-1
						||strGroup[iGroup].indexOf("c")>-1
						
						||strGroup[iGroup].indexOf("s")>-1
						||strGroup[iGroup].indexOf("t")>-1
						
						){
						String zlStartMonth="0",zlEndMonth="0";
						String strDayUpdate="";
						
						boolean bool = false;	//账龄分支
						
						//insert into `s_config` (`sname`, `svalue`, `sautoid`, `multiselect`, `smemo`, `upuser`, `uptime`, `property`, `control`) values('调整的账龄按自定义区间输出','是','399','','','系统管理员','2009-07-23','user','');
						
						sql = "select 1 from s_config where sname = '调整的账龄按自定义区间输出' and svalue = '是'";
						ps = this.myConn.prepareStatement(sql);
						ResultSet rs2 = ps.executeQuery();
						if(rs2.next()){
							bool = true;
						}
						
						for (int i=0;i<iNameLength;i++){
							
							if (i==0){
								strDayUpdate="0";
							}else{
								strDayUpdate+="+" + name[i-1] ;
							}
								
							boolean bEndMonth=false;
							
							if ("Day11".equals(name[i]) 
								|| "sdDay11".equals(name[i]) 
								|| "curDay11".equals(name[i])
								|| "cursdDay11".equals(name[i])
								){
								//半年以内
								zlStartMonth="0";
								zlEndMonth="6";
							}else if ("Day10".equals(name[i])
								||"sdDay10".equals(name[i])
								||"curDay10".equals(name[i])
								||"cursdDay10".equals(name[i])
								){
								//3月以内
								zlStartMonth="0";
								zlEndMonth="3";
							}else if ("Day6".equals(name[i])
								||"sdDay6".equals(name[i])
								||"curDay6".equals(name[i])
								||"cursdDay6".equals(name[i])
								){
								//1月以内
								zlStartMonth="0";
								zlEndMonth="1";
							}else if ("Day7".equals(name[i])
								||"sdDay7".equals(name[i])
								||"curDay7".equals(name[i])
								||"cursdDay7".equals(name[i])
								){
								//1月到3月
								zlStartMonth="1";
								zlEndMonth="3";
							}else if ("Day8".equals(name[i])
								||"sdDay8".equals(name[i])
								||"curDay8".equals(name[i])
								||"cursdDay8".equals(name[i])
								){
								//3月到6月
								zlStartMonth="3";
								zlEndMonth="6";
							}else if ("Day13".equals(name[i])
								||"sdDay13".equals(name[i])
								||"curDay13".equals(name[i])
								||"cursdDay13".equals(name[i])
								){
								//6月到9月
								zlStartMonth="6";
								zlEndMonth="9";
							}else if ("Day14".equals(name[i])
								||"sdDay14".equals(name[i])
								||"curDay14".equals(name[i])
								||"cursdDay14".equals(name[i])
								){
								//9月到12月
								zlStartMonth="9";
								zlEndMonth="12";
							}else if ("Day0".equals(name[i])
								||"sdDay0".equals(name[i])
								||"curDay0".equals(name[i])
								||"cursdDay0".equals(name[i])
								){
								//1年以内
								zlStartMonth="0";
								zlEndMonth="12";
							}else if ("Day9".equals(name[i])
								||"sdDay9".equals(name[i])
								||"curDay9".equals(name[i])
								||"cursdDay9".equals(name[i])
								){
								//半年到1年
								zlStartMonth="6";
								zlEndMonth="12";
							}else if ("Day1".equals(name[i])
								||"sdDay1".equals(name[i])
								||"curDay1".equals(name[i])
								||"cursdDay1".equals(name[i])
								){
								//1年到2年
								zlStartMonth="12";
								zlEndMonth="24";
							}else if ("Day2".equals(name[i])
								||"sdDay2".equals(name[i])
								||"curDay2".equals(name[i])
								||"cursdDay2".equals(name[i])
								){
								//2年到3年
								zlStartMonth="24";
								zlEndMonth="36";
							}else if ("Day3".equals(name[i])
								||"sdDay3".equals(name[i])
								||"curDay3".equals(name[i])
								||"cursdDay3".equals(name[i])
								){
								//3年到4年
								zlStartMonth="36";
								zlEndMonth="48";
							}else if ("Day4".equals(name[i])
								||"sdDay4".equals(name[i])
								||"curDay4".equals(name[i])
								||"cursdDay4".equals(name[i])
								){
								//4年到5年
								zlStartMonth="48";
								zlEndMonth="60";
							}else if ("Day5".equals(name[i])
								||"sdDay5".equals(name[i])
								||"curDay5".equals(name[i])
								||"cursdDay5".equals(name[i])
								){
								//5年以上
								zlStartMonth="60";
								zlEndMonth="1000";
								bEndMonth=true;
							}else if ("Day12".equals(name[i])
								||"sdDay12".equals(name[i])
								||"curDay12".equals(name[i])
								||"cursdDay12".equals(name[i])
								){
								//3年以上
								zlStartMonth="36";
								zlEndMonth="1000";
								bEndMonth=true;
							}
			
							if (strGroup[iGroup].indexOf("4")>-1
								||strGroup[iGroup].indexOf("b")>-1	
								|| bool
								){
								//未审本位币账龄
								//SubjectName
								
								if("科目".equals(isSubject)){	
									
									sql="update "+tempTable+" a left join \n" +
									"( \n" +
									"	select tokenid,dataname,occ1,if(inarea=1,occ2,null) occ2 " +
									"	,occ3,if(inarea=1,occ4,null) occ4 " +
									"	from ( \n" +
									"		select tokenid,if(dataname='0','本位币',dataname) as dataname, \n" +
									
									"		sum(if( a.subyearmonth*12+a.submonth>('"+strEndYearMonth+"'-"+zlStartMonth+") and a.subyearmonth*12+a.submonth<=('"+strEndYearMonth+"') ,  if(direction2 * DebitOcc"+strName+" >0,direction2 * DebitOcc"+strName+",0) + if(direction2 * CreditOcc"+strName+" <0,(-1) * direction2 * CreditOcc"+strName+",0) , 0 )) Occ1, \n" +
									"		sum(if( a.subyearmonth*12+a.submonth>('"+strEndYearMonth+"'-"+zlEndMonth+") and a.subyearmonth*12+a.submonth<=('"+strEndYearMonth+"' -"+zlStartMonth+") ,if(direction2 * DebitOcc"+strName+" >0,direction2 * DebitOcc"+strName+",0) + if(direction2 * CreditOcc"+strName+" <0,(-1) * direction2 * CreditOcc"+strName+",0), 0 )) Occ2, \n" +
									
									"		sum(if( a.subyearmonth*12+a.submonth>('"+strEndYearMonth+"'-"+zlStartMonth+") and a.subyearmonth*12+a.submonth<=('"+strEndYearMonth+"') , if(direction2 * CreditOcc"+strName+" >0,direction2 * CreditOcc"+strName+",0) + if(direction2 * DebitOcc"+strName+" <0,(-1) * direction2 * DebitOcc"+strName+",0) , 0 )) Occ3, \n" +
									"		sum(if( a.subyearmonth*12+a.submonth>('"+strEndYearMonth+"'-"+zlEndMonth+") and a.subyearmonth*12+a.submonth<=('"+strEndYearMonth+"' -"+zlStartMonth+") ,if(direction2 * CreditOcc"+strName+" >0,direction2 * CreditOcc"+strName+",0) + if(direction2 * DebitOcc"+strName+" <0,(-1) * direction2 * DebitOcc"+strName+",0) , 0 )) Occ4, \n" +

									"        \n";
									
									if (bEndMonth){
										sql+=" 0 as inarea \n";
									}else{
										sql+="       if (('"+strEndYearMonth+"'>=('"+strEndYearMonth+"'-"+zlStartMonth+")  && ('"+strEndYearMonth+"'-"+zlStartMonth+") >=min(subyearmonth)*12+1) \n" +
										"       	||('"+strEndYearMonth+"'>= ('"+strEndYearMonth+"'-"+zlEndMonth+") && ('"+strEndYearMonth+"'-"+zlEndMonth+") >=min(subyearmonth)*12+1), \n" +
										"       	1,0) as inarea \n" ;
									}
									
									
									sql+="		from "+tableName[0]+" a \n" + 
									"		where 1=1 \n" + 
									"		and subyearmonth*12+submonth<= '"+strEndYearMonth+"' \n" +  
//									" 		and ((a.direction2=1 and  a.DebitOcc>=0) or (a.direction2=-1 and a.CreditOcc>=0))" +  
									" 		and (a.subjectfullname2 = '"+SubjectName+"' or a.subjectfullname2 like '"+SubjectName+"/%')" +
									"		group by tokenid,dataname	 \n" +
									"  )a \n" +
									")b \n" +
									" on a.tokenid=b.tokenid and a.dataname=b.dataname \n"+
//									" set "+name[i]+"=if(abs("+strDayUpdate+")>=abs(a."+preToken+"balance),0,if("+preToken+"balance>0,if(("+preToken+"balance -occ1) <0,0,if(occ2 is null,"+preToken+"balance-occ1,if( ("+preToken+"balance-occ1-occ2)<=0,"+preToken+"balance-occ1,occ2))),if(occ2 is null,"+preToken+"balance,if(occ2>0,"+preToken+"balance,0)))) \n" +
									" set "+name[i]+"=if(abs("+strDayUpdate+")>=abs(a."+preToken+"balance),0,if("+preToken+"balance>0,if(("+preToken+"balance -occ1) <0,0,if(occ2 is null,"+preToken+"balance-occ1,if( ("+preToken+"balance-occ1-occ2)<=0,"+preToken+"balance-occ1,occ2))),if(("+preToken+"balance +occ3) >=0,0,if(occ4 is null,"+preToken+"balance+occ3,if( ("+preToken+"balance+occ3+occ4)>=0,"+preToken+"balance+occ3,(-1)*occ4))))) \n" +
									" where a.issubject = '"+isSubject+"' " +
									" and if(a.dataname='本位币',0,1) = '"+DataName+"' " +
									" and b.tokenid is not null ";
									
								}else{
									
									sql="update "+tempTable+" a left join \n" +
									"( \n" +
									"	select tokenid,AssTotalName1,dataname,occ1,if(inarea=1,occ2,null) occ2" +
									"	,occ3,if(inarea=1,occ4,null) occ4 " +
									"	from ( \n" +
									"		select tokenid,AssTotalName1,if(dataname='0','本位币',dataname) as dataname, \n" +	
									
									"		sum(if( a.subyearmonth*12+a.submonth>('"+strEndYearMonth+"'-"+zlStartMonth+") and a.subyearmonth*12+a.submonth<=('"+strEndYearMonth+"') ,  if(direction2 * DebitOcc"+strName+" >0,direction2 * DebitOcc"+strName+",0) + if(direction2 * CreditOcc"+strName+" <0,(-1) * direction2 * CreditOcc"+strName+",0) , 0 )) Occ1, \n" +
									"		sum(if( a.subyearmonth*12+a.submonth>('"+strEndYearMonth+"'-"+zlEndMonth+") and a.subyearmonth*12+a.submonth<=('"+strEndYearMonth+"' -"+zlStartMonth+") ,if(direction2 * DebitOcc"+strName+" >0,direction2 * DebitOcc"+strName+",0) + if(direction2 * CreditOcc"+strName+" <0,(-1) * direction2 * CreditOcc"+strName+",0), 0 )) Occ2, \n" +
									"		sum(if( a.subyearmonth*12+a.submonth>('"+strEndYearMonth+"'-"+zlStartMonth+") and a.subyearmonth*12+a.submonth<=('"+strEndYearMonth+"') , if(direction2 * CreditOcc"+strName+" >0,direction2 * CreditOcc"+strName+",0) + if(direction2 * DebitOcc"+strName+" <0,(-1) * direction2 * DebitOcc"+strName+",0) , 0 )) Occ3, \n" +
									"		sum(if( a.subyearmonth*12+a.submonth>('"+strEndYearMonth+"'-"+zlEndMonth+") and a.subyearmonth*12+a.submonth<=('"+strEndYearMonth+"' -"+zlStartMonth+") ,if(direction2 * CreditOcc"+strName+" >0,direction2 * CreditOcc"+strName+",0) + if(direction2 * DebitOcc"+strName+" <0,(-1) * direction2 * DebitOcc"+strName+",0) , 0 )) Occ4, \n" +
									
									"        \n" ;
									if (bEndMonth){
										sql+=" 0 as inarea \n";
									}else{
										sql+="       if (('"+strEndYearMonth+"'>=('"+strEndYearMonth+"'-"+zlStartMonth+")  && ('"+strEndYearMonth+"'-"+zlStartMonth+") >=min(subyearmonth)*12+1) \n" +
										"       	||('"+strEndYearMonth+"'>= ('"+strEndYearMonth+"'-"+zlEndMonth+") && ('"+strEndYearMonth+"'-"+zlEndMonth+") >=min(subyearmonth)*12+1), \n" +
										"       	1,0) as inarea \n";
									}
									
									sql+="		from "+tableName[0]+" a,(" +
									"				select distinct accpackageid,subjectid,AccName,tokenid,subjectfullname1 \n" +
									"				from c_account \n" + 
									"				where 1=1 \n" +
									"				and subyearmonth*12+submonth<= '"+strEndYearMonth+"' \n" +  
									"				and subyearmonth*12+submonth>('"+strEndYearMonth+"'-"+zlEndMonth+") \n" +  
									"				and (subjectfullname2 = '"+SubjectName+"' or subjectfullname2 like '"+SubjectName+"/%') \n" +
									"		) b \n" + 
									"		where 1=1 \n" +
									"		and subyearmonth*12+submonth<= '"+strEndYearMonth+"' \n" +
//									" 		and ((a.direction2=1 and  a.DebitOcc>=0) or (a.direction2=-1 and a.CreditOcc>=0))" +  
									"		and a.accpackageid = b.accpackageid \n" +
									"		and a.accid=b.subjectid   \n" +
//									"		group by tokenid,AssItemName,dataname	 \n" +
									"		group by tokenid,AssTotalName1,dataname	 \n" +
//									"		group by tokenid,accid,assitemid,dataname	 \n" +
									" ) a \n" +
									")b \n" +
									"on a.sname=b.tokenid and a.tokenid=b.AssTotalName1 and a.dataname=b.dataname \n" +
//									"set "+name[i]+"=if(abs("+strDayUpdate+")>=abs(a."+preToken+"balance),0,if("+preToken+"balance>0,if(("+preToken+"balance -occ1) <0,0,if(occ2 is null,"+preToken+"balance-occ1,if( ("+preToken+"balance-occ1-occ2)<=0,"+preToken+"balance-occ1,occ2))),if(occ2 is null,"+preToken+"balance,if(occ2>0,"+preToken+"balance,0)))) \n" +
									"set "+name[i]+"=if(abs("+strDayUpdate+")>=abs(a."+preToken+"balance),0,if("+preToken+"balance>0,if(("+preToken+"balance -occ1) <0,0,if(occ2 is null,"+preToken+"balance-occ1,if( ("+preToken+"balance-occ1-occ2)<=0,"+preToken+"balance-occ1,occ2))),if(("+preToken+"balance +occ3) >=0,0,if(occ4 is null,"+preToken+"balance+occ3,if( ("+preToken+"balance+occ3+occ4)>=0,"+preToken+"balance+occ3,(-1)*occ4))))) \n" +
									"  where a.issubject = '"+isSubject+"' " +
									" and if(a.dataname='本位币',0,1) = '"+DataName+"' " +
									" and b.tokenid is not null ";
									
									
								}
								
//								System.out.println(sql);
							}
							
							if (strGroup[iGroup].indexOf("5")>-1
								||strGroup[iGroup].indexOf("c")>-1	
								){
								//审定本位币账龄
								if(bool && preToken.indexOf("cur")<0 
//									&& ("".equals(Year) || "0".equals(Year)) 
									){	//只有在本位币和年度=0时，才能显示新账龄
									
									
								}else{

									String strDataNameRec="dataname";
									if (preToken.indexOf("cur")<0){
										strDataNameRec="'0' as dataname";
									}
									
									String EndMonth = project.getAuditTimeEnd().substring(5, 7);
									
									String EndYear = project.getAuditTimeEnd().substring(0, 4);
									
									if("科目".equals(isSubject)){	
										sql="update "+tempTable+" a left join \n" + 
										"( \n" +
										
										//把ACCOUNT提出来，避免先UNION在GROUP BY
										"	select tokenid,dataname,occ1,if(inarea=1,occ2,null) occ2,occ3,if(inarea=1,occ4,null) occ4 from ( \n\n" +
										
										"	select a.tokenid,a.dataname, inarea, \n" +
										"	occ1 + if(a.direction2=1,ifnull(b.recdebitOcc1,0),ifnull(b.reccreditOcc1,0)) + (-1) * if(a.direction2=1,ifnull(b.reccreditOcc11,0),ifnull(b.recdebitOcc11,0))  as occ1, \n"+
										"	occ2 + if(a.direction2=1,ifnull(b.recdebitOcc2,0),ifnull(b.reccreditOcc2,0)) + (-1) * if(a.direction2=1,ifnull(b.reccreditOcc21,0),ifnull(b.recdebitOcc21,0)) as occ2, \n" +
										
										"	occ3 + if(a.direction2=-1,ifnull(b.recdebitOcc1,0),ifnull(b.reccreditOcc1,0)) + (-1) * if(a.direction2=-1,ifnull(b.reccreditOcc11,0),ifnull(b.recdebitOcc11,0))  as occ3, \n"+
										"	occ4 + if(a.direction2=-1,ifnull(b.recdebitOcc2,0),ifnull(b.reccreditOcc2,0)) + (-1) * if(a.direction2=-1,ifnull(b.reccreditOcc21,0),ifnull(b.recdebitOcc21,0)) as occ4 \n" +
										
										"	\n\n"+
										"	from(  \n"+
										"		select tokenid,dataname,a.direction2, \n"+ 
										
										"		sum(if( a.subyearmonth*12+a.submonth>('"+strEndYearMonth+"'-"+zlStartMonth+") and a.subyearmonth*12+a.submonth<=('"+strEndYearMonth+"') ,  if(direction2 * DebitOcc"+strName+" >0,direction2 * DebitOcc"+strName+",0) + if(direction2 * CreditOcc"+strName+" <0,(-1) * direction2 * CreditOcc"+strName+",0) , 0 )) Occ1, \n" +
										"		sum(if( a.subyearmonth*12+a.submonth>('"+strEndYearMonth+"'-"+zlEndMonth+") and a.subyearmonth*12+a.submonth<=('"+strEndYearMonth+"' -"+zlStartMonth+") ,if(direction2 * DebitOcc"+strName+" >0,direction2 * DebitOcc"+strName+",0) + if(direction2 * CreditOcc"+strName+" <0,(-1) * direction2 * CreditOcc"+strName+",0), 0 )) Occ2, \n" +
										"		sum(if( a.subyearmonth*12+a.submonth>('"+strEndYearMonth+"'-"+zlStartMonth+") and a.subyearmonth*12+a.submonth<=('"+strEndYearMonth+"') , if(direction2 * CreditOcc"+strName+" >0,direction2 * CreditOcc"+strName+",0) + if(direction2 * DebitOcc"+strName+" <0,(-1) * direction2 * DebitOcc"+strName+",0) , 0 )) Occ3, \n" +
										"		sum(if( a.subyearmonth*12+a.submonth>('"+strEndYearMonth+"'-"+zlEndMonth+") and a.subyearmonth*12+a.submonth<=('"+strEndYearMonth+"' -"+zlStartMonth+") ,if(direction2 * CreditOcc"+strName+" >0,direction2 * CreditOcc"+strName+",0) + if(direction2 * DebitOcc"+strName+" <0,(-1) * direction2 * DebitOcc"+strName+",0) , 0 )) Occ4, \n" ;
	
										
										if (bEndMonth){
											sql+=" 0 as inarea \n";
										}else{
											sql+="       if (('"+strEndYearMonth+"'>=('"+strEndYearMonth+"'-"+zlStartMonth+")  && ('"+strEndYearMonth+"'-"+zlStartMonth+") >=min(subyearmonth)*12+1) \n" +
											"       	||('"+strEndYearMonth+"'>= ('"+strEndYearMonth+"'-"+zlEndMonth+") && ('"+strEndYearMonth+"'-"+zlEndMonth+") >=min(subyearmonth)*12+1), \n" +
											"       	1,0) as inarea \n";
										}
										
										sql +=" from "+tableName[0]+" a \n"+
										"			where 1=1  \n"+
										"			and subyearmonth*12+submonth<= '"+strEndYearMonth1+"' \n"+ 
										"			and (subjectfullname2 = '"+SubjectName+"' or subjectfullname2 like '"+SubjectName+"/%') \n"+ 
	//									"			and ((a.direction2=1 and  a.DebitOcc>=0) or (a.direction2=-1 and a.CreditOcc>=0))	 \n" +
										"		group by tokenid,dataname,a.direction2 \n";
										
										sql+="	union \n		" +
										"		select tokenid,dataname,a.direction2, \n"+ 
										"		0 as  Occ1, \n"+ 
										"		0 as  Occ2, \n"+ 
										"		0 as  Occ3, \n"+ 
										"		0 as  Occ4, \n";
											
										if (bEndMonth){
											sql+=" 0 as inarea \n";
										}else{
											sql+="       if (('"+strEndYearMonth+"'>=('"+strEndYearMonth+"'-"+zlStartMonth+")  && ('"+strEndYearMonth+"'-"+zlStartMonth+") >=min(subyearmonth)*12+1) \n" +
												"       	||('"+strEndYearMonth+"'>= ('"+strEndYearMonth+"'-"+zlEndMonth+") && ('"+strEndYearMonth+"'-"+zlEndMonth+") >=min(subyearmonth)*12+1), \n" +
												"       	1,0) as inarea \n";
										}	
											
										sql+=" 		from (\n" +
										
										"			select subjectfullname as tokenid,0 as dataname,subyearmonth,submonth,if(substring(property,2,1)=2,-1,1) as direction2 \n" +
										"			from z_usesubject a ,c_account b \n" +
										"			where a.projectid = "+project.getProjectId()+"  \n" +
										"			and isleaf=1 \n" +
										"			and b.subjectfullname2 = '"+SubjectName+"' \n" +
										"			and subyearmonth*12+submonth= '"+strEndYearMonth+"' \n" +
										"			and a.tipsubjectid = b.subjectid \n" +
										
										"			union" +
										
										"			select subjectfullname,0,"+EndYear+" as subyearmonth,"+EndMonth+" as submonth,if(substring(property,2,1)=2,-1,1) \n" +
										"			from z_usesubject a  \n" +
										"			where a.projectid = "+project.getProjectId()+"  \n" +
										"			and isleaf=1 \n" +
										"			and (a.subjectfullname = '"+SubjectName+"' or a.subjectfullname like '"+SubjectName+"/%') \n" +
										
										"		) a \n"+
										"		group by tokenid,dataname,a.direction2 \n"+
										 
										"	)a   \n"+
										"	left join ( \n"+ 
										"		select a.*,tokenid \n"+ 
										"		from ( \n"+
										"			select accpackageid,dataname,sid, \n"+ 
										
										"			sum(case when (yearrectify*12+"+EndMonth+")>('"+strEndYearMonth+"'-"+zlStartMonth+") and (yearrectify*12+"+EndMonth+")<=('"+strEndYearMonth+"') then if(DebitTotalOcc1"+strName+"<0,0,DebitTotalOcc1"+strName+")+if(DebitTotalOcc2"+strName+"<0,0,DebitTotalOcc2"+strName+") else 0 end) recdebitOcc1, \n"+ 
										"			sum(case when (yearrectify*12+"+EndMonth+")>('"+strEndYearMonth+"'-"+zlStartMonth+") and (yearrectify*12+"+EndMonth+")<=('"+strEndYearMonth+"') then if(CreditTotalOcc1"+strName+"<0,0,CreditTotalOcc1"+strName+")+if(CreditTotalOcc2"+strName+"<0,0,CreditTotalOcc2"+strName+") else 0 end) reccreditOcc1,  \n"+
										"			sum(case when (yearrectify*12+"+EndMonth+")>('"+strEndYearMonth+"'-"+zlEndMonth+") and (yearrectify*12+"+EndMonth+")<=('"+strEndYearMonth+"' -"+zlStartMonth+") then if(DebitTotalOcc1"+strName+"<0,0,DebitTotalOcc1"+strName+")+if(DebitTotalOcc2"+strName+"<0,0,DebitTotalOcc2"+strName+") else 0 end) recdebitOcc2,  \n"+
										"			sum(case when (yearrectify*12+"+EndMonth+")>('"+strEndYearMonth+"'-"+zlEndMonth+") and (yearrectify*12+"+EndMonth+")<=('"+strEndYearMonth+"' -"+zlStartMonth+") then if(CreditTotalOcc1"+strName+"<0,0,CreditTotalOcc1"+strName+")+if(CreditTotalOcc2"+strName+"<0,0,CreditTotalOcc2"+strName+") else 0 end) reccreditOcc2,  \n"+
										
										"			sum(case when (yearrectify*12+"+EndMonth+")>('"+strEndYearMonth+"'-"+zlStartMonth+") and (yearrectify*12+"+EndMonth+")<=('"+strEndYearMonth+"') then if(DebitTotalOcc1"+strName+">0,0,DebitTotalOcc1"+strName+")+if(DebitTotalOcc2"+strName+">0,0,DebitTotalOcc2"+strName+") else 0 end) recdebitOcc11, \n"+ 
										"			sum(case when (yearrectify*12+"+EndMonth+")>('"+strEndYearMonth+"'-"+zlStartMonth+") and (yearrectify*12+"+EndMonth+")<=('"+strEndYearMonth+"') then if(CreditTotalOcc1"+strName+">0,0,CreditTotalOcc1"+strName+")+if(CreditTotalOcc2"+strName+">0,0,CreditTotalOcc2"+strName+") else 0 end) reccreditOcc11,  \n"+
										"			sum(case when (yearrectify*12+"+EndMonth+")>('"+strEndYearMonth+"'-"+zlEndMonth+") and (yearrectify*12+"+EndMonth+")<=('"+strEndYearMonth+"' -"+zlStartMonth+") then if(DebitTotalOcc1"+strName+">0,0,DebitTotalOcc1"+strName+")+if(DebitTotalOcc2"+strName+">0,0,DebitTotalOcc2"+strName+") else 0 end) recdebitOcc21,  \n"+
										"			sum(case when (yearrectify*12+"+EndMonth+")>('"+strEndYearMonth+"'-"+zlEndMonth+") and (yearrectify*12+"+EndMonth+")<=('"+strEndYearMonth+"' -"+zlStartMonth+") then if(CreditTotalOcc1"+strName+">0,0,CreditTotalOcc1"+strName+")+if(CreditTotalOcc2"+strName+">0,0,CreditTotalOcc2"+strName+") else 0 end) reccreditOcc21  \n"+
	
										
										"			from( \n"+ 
										"				select accpackageid,subjectid as sid,"+strDataNameRec+",DebitTotalOcc1"+strName+",DebitTotalOcc2"+strName+",CreditTotalOcc1"+strName+",CreditTotalOcc2"+strName+",substring(accpackageid,7) as yearrectify \n"+ 
										"				from "+tableName[1]+"   \n"+
										"				where projectid = "+project.getProjectId()+"   \n"+
										
										//加过滤，避免太多
										" 				and (  \n" +
										" 					abs(DebitTotalOcc1"+strName+")+abs(CreditTotalOcc1"+strName+") \n" +
										" 					+abs(DebitTotalOcc2"+strName+")+abs(CreditTotalOcc2"+strName+") \n" +
										" 					+abs(DebitTotalOcc3"+strName+")+abs(CreditTotalOcc3"+strName+") \n" +
										" 					+abs(DebitTotalOcc4"+strName+")+abs(CreditTotalOcc4"+strName+") \n" +
										" 					+abs(DebitTotalOcc5"+strName+")+abs(CreditTotalOcc5"+strName+") \n" +
										" 					+abs(DebitTotalOcc6"+strName+")+abs(CreditTotalOcc6"+strName+") \n" +
										" 				)>0 \n " +
										
										"				union  \n"+
										"				select accpackageid,subjectid,"+strDataNameRec+",DebitTotalOcc1"+strName+",DebitTotalOcc2"+strName+",CreditTotalOcc1"+strName+",CreditTotalOcc2"+strName+",yearrectify \n"+ 
										"				from "+tableName[2]+"   \n"+
										"				where projectid = "+project.getProjectId()+"   \n"+
										
										//加过滤，避免太多
										" 				and (  \n" +
										" 					abs(DebitTotalOcc1"+strName+")+abs(CreditTotalOcc1"+strName+") \n" +
										" 					+abs(DebitTotalOcc2"+strName+")+abs(CreditTotalOcc2"+strName+") \n" +
										" 					+abs(DebitTotalOcc3"+strName+")+abs(CreditTotalOcc3"+strName+") \n" +
										" 					+abs(DebitTotalOcc4"+strName+")+abs(CreditTotalOcc4"+strName+") \n" +
										" 					+abs(DebitTotalOcc5"+strName+")+abs(CreditTotalOcc5"+strName+") \n" +
										" 					+abs(DebitTotalOcc6"+strName+")+abs(CreditTotalOcc6"+strName+") \n" +
										" 				)>0 \n " +
										
										
										
										"			)b  \n"+
										"			group by accpackageid,dataname,sid \n"+ 
										"		) a ,(				 \n"+
										"				select distinct accpackageid,subjectid,AccName,tokenid \n"+ 
										"				from c_account  \n"+
										"				where 1=1  \n"+
										"				and subyearmonth*12+submonth<= '"+strEndYearMonth+"' \n"+ 
										"				and subyearmonth*12+submonth>('"+strEndYearMonth+"'-"+zlEndMonth+") \n"+ 
										"				and (subjectfullname2 = '"+SubjectName+"' or subjectfullname2 like '"+SubjectName+"/%') \n"+
										
										"				union \n" +
										
										"				select distinct a.accpackageid,a.subjectid,a.subjectname,subjectfullname as tokenid \n" +
										"				from z_usesubject a ,c_account b \n" +
										"				where a.projectid = "+project.getProjectId()+"  \n" +
										"				and isleaf=1 \n" +
										"				and b.subjectfullname2 = '"+SubjectName+"'\n" + 
										"				and subyearmonth*12+submonth= '"+strEndYearMonth+"' \n" +
										"				and a.tipsubjectid = b.subjectid \n" +	
										
										"				union" +
										
										"				select distinct a.accpackageid,a.subjectid,a.subjectname,subjectfullname as tokenid \n" +
										"				from z_usesubject a  \n" +
										"				where a.projectid = "+project.getProjectId()+"  \n" +
										"				and isleaf=1 \n" +
										"				and (a.subjectfullname = '"+SubjectName+"' or a.subjectfullname like '"+SubjectName+"/%') \n" +
		
										
										"			) b   \n"+
										"			where 1=1  \n"+
										"			and a.accpackageid = b.accpackageid \n"+ 
										"			and a.sid=b.subjectid  \n"+
									
										"		) b on a.dataname = b.dataname and a.tokenid=b.tokenid \n\n"+
										
										"  )a \n" +
										")b \n" +
										" on a.tokenid=b.tokenid and a.dataname=if(b.dataname='0','本位币',b.dataname) \n"+
	//									" set "+name[i]+"=if(abs("+strDayUpdate+")>=abs(a."+preToken+"sdbalance),0,if("+preToken+"sdbalance>0,if(("+preToken+"sdbalance -occ1) <0,0,if(occ2 is null,"+preToken+"sdbalance-occ1,if( ("+preToken+"sdbalance-occ1-occ2)<=0,"+preToken+"sdbalance-occ1,occ2))),if(occ2 is null,"+preToken+"sdbalance,if(occ2>0,"+preToken+"sdbalance,0)))) \n" +
										" set "+name[i]+"=if(abs("+strDayUpdate+")>=abs(a."+preToken+"sdbalance),0,if("+preToken+"sdbalance>0,if(("+preToken+"sdbalance -occ1) <0,0,if(occ2 is null,"+preToken+"sdbalance-occ1,if( ("+preToken+"sdbalance-occ1-occ2)<=0,"+preToken+"sdbalance-occ1,occ2))),if(("+preToken+"sdbalance +occ3) >=0,0,if(occ4 is null,"+preToken+"sdbalance+occ3,if( ("+preToken+"sdbalance+occ3+occ4)>=0,"+preToken+"sdbalance+occ3,(-1)*occ4))))) \n" +
										"  where a.issubject = '"+isSubject+"' " +
										" and if(a.dataname='本位币',0,1) = '"+DataName+"' " +
										" and b.tokenid is not null ";
										
									}else{
										//核算？？
										
										sql="update "+tempTable+" a left join \n" +
										"( \n" +
										"	select tokenid,AssTotalName1,dataname,occ1,if(inarea=1,occ2,null) occ2,occ3,if(inarea=1,occ4,null) occ4 from ( \n\n" +
										
										"	select a.tokenid,a.AssTotalName1,a.dataname, inarea, \n" +
										"	occ1 + if(a.direction2=1,ifnull(b.recdebitOcc1,0),ifnull(b.reccreditOcc1,0)) + (-1) * if(a.direction2=1,ifnull(b.reccreditOcc11,0),ifnull(b.recdebitOcc11,0))  as occ1, \n"+
										"	occ2 + if(a.direction2=1,ifnull(b.recdebitOcc2,0),ifnull(b.reccreditOcc2,0)) + (-1) * if(a.direction2=1,ifnull(b.reccreditOcc21,0),ifnull(b.recdebitOcc21,0)) as occ2, \n" +
										
										"	occ3 + if(a.direction2=-1,ifnull(b.recdebitOcc1,0),ifnull(b.reccreditOcc1,0)) + (-1) * if(a.direction2=-1,ifnull(b.reccreditOcc11,0),ifnull(b.recdebitOcc11,0))  as occ3, \n"+
										"	occ4 + if(a.direction2=-1,ifnull(b.recdebitOcc2,0),ifnull(b.reccreditOcc2,0)) + (-1) * if(a.direction2=-1,ifnull(b.reccreditOcc21,0),ifnull(b.recdebitOcc21,0)) as occ4 \n" +
				
										
										"	\n\n"+
										"	from(  \n"+
										"		select tokenid,AssTotalName1,dataname,a.direction2, \n"+ 
										
										"		sum(if( a.subyearmonth*12+a.submonth>('"+strEndYearMonth+"'-"+zlStartMonth+") and a.subyearmonth*12+a.submonth<=('"+strEndYearMonth+"') ,  if(direction2 * DebitOcc"+strName+" >0,direction2 * DebitOcc"+strName+",0) + if(direction2 * CreditOcc"+strName+" <0,(-1) * direction2 * CreditOcc"+strName+",0) , 0 )) Occ1, \n" +
										"		sum(if( a.subyearmonth*12+a.submonth>('"+strEndYearMonth+"'-"+zlEndMonth+") and a.subyearmonth*12+a.submonth<=('"+strEndYearMonth+"' -"+zlStartMonth+") ,if(direction2 * DebitOcc"+strName+" >0,direction2 * DebitOcc"+strName+",0) + if(direction2 * CreditOcc"+strName+" <0,(-1) * direction2 * CreditOcc"+strName+",0), 0 )) Occ2, \n" +
										"		sum(if( a.subyearmonth*12+a.submonth>('"+strEndYearMonth+"'-"+zlStartMonth+") and a.subyearmonth*12+a.submonth<=('"+strEndYearMonth+"') , if(direction2 * CreditOcc"+strName+" >0,direction2 * CreditOcc"+strName+",0) + if(direction2 * DebitOcc"+strName+" <0,(-1) * direction2 * DebitOcc"+strName+",0) , 0 )) Occ3, \n" +
										"		sum(if( a.subyearmonth*12+a.submonth>('"+strEndYearMonth+"'-"+zlEndMonth+") and a.subyearmonth*12+a.submonth<=('"+strEndYearMonth+"' -"+zlStartMonth+") ,if(direction2 * CreditOcc"+strName+" >0,direction2 * CreditOcc"+strName+",0) + if(direction2 * DebitOcc"+strName+" <0,(-1) * direction2 * DebitOcc"+strName+",0) , 0 )) Occ4, \n" ;
	
										
										if (bEndMonth){
											sql+=" 0 as inarea \n";
										}else{
											sql+="       if (('"+strEndYearMonth+"'>=('"+strEndYearMonth+"'-"+zlStartMonth+")  && ('"+strEndYearMonth+"'-"+zlStartMonth+") >=min(subyearmonth)*12+1) \n" +
											"       	||('"+strEndYearMonth+"'>= ('"+strEndYearMonth+"'-"+zlEndMonth+") && ('"+strEndYearMonth+"'-"+zlEndMonth+") >=min(subyearmonth)*12+1), \n" +
											"       	1,0) as inarea \n";
										}
										
										
										sql+="		from "+tableName[0]+" a ,(" +
										"				select distinct accpackageid,subjectid,AccName,tokenid,subjectfullname1 \n" +
										"				from c_account \n" + 
										"				where 1=1 \n" +
										"				and subyearmonth*12+submonth<= '"+strEndYearMonth1+"' \n" +  
										"				and (subjectfullname2 = '"+SubjectName+"' or subjectfullname2 like '"+SubjectName+"/%') \n" +
										"		) b \n" + 
										"		where 1=1  \n"+
										"		and subyearmonth*12+submonth<= '"+strEndYearMonth1+"' \n"+ 
										"		and a.accpackageid = b.accpackageid \n" +
										"		and a.accid=b.subjectid   \n" +
	//									"		group by tokenid,AssItemName,dataname	 \n" +
										"		group by tokenid,AssTotalName1,dataname	 \n" +
										 
										"	)a   \n"+
										"	left join ( \n"+ 
										"		select a.*,tokenid,AssTotalName1 \n"+ 
										"		from ( \n"+
										"			select accpackageid,dataname,sid,assitemid, \n"+
										
										"			sum(case when (yearrectify*12+"+EndMonth+")>('"+strEndYearMonth+"'-"+zlStartMonth+") and (yearrectify*12+"+EndMonth+")<=('"+strEndYearMonth+"') then if(DebitTotalOcc1"+strName+"<0,0,DebitTotalOcc1"+strName+")+if(DebitTotalOcc2"+strName+"<0,0,DebitTotalOcc2"+strName+") else 0 end) recdebitOcc1, \n"+ 
										"			sum(case when (yearrectify*12+"+EndMonth+")>('"+strEndYearMonth+"'-"+zlStartMonth+") and (yearrectify*12+"+EndMonth+")<=('"+strEndYearMonth+"') then if(CreditTotalOcc1"+strName+"<0,0,CreditTotalOcc1"+strName+")+if(CreditTotalOcc2"+strName+"<0,0,CreditTotalOcc2"+strName+") else 0 end) reccreditOcc1,  \n"+
										"			sum(case when (yearrectify*12+"+EndMonth+")>('"+strEndYearMonth+"'-"+zlEndMonth+") and (yearrectify*12+"+EndMonth+")<=('"+strEndYearMonth+"' -"+zlStartMonth+") then if(DebitTotalOcc1"+strName+"<0,0,DebitTotalOcc1"+strName+")+if(DebitTotalOcc2"+strName+"<0,0,DebitTotalOcc2"+strName+") else 0 end) recdebitOcc2,  \n"+
										"			sum(case when (yearrectify*12+"+EndMonth+")>('"+strEndYearMonth+"'-"+zlEndMonth+") and (yearrectify*12+"+EndMonth+")<=('"+strEndYearMonth+"' -"+zlStartMonth+") then if(CreditTotalOcc1"+strName+"<0,0,CreditTotalOcc1"+strName+")+if(CreditTotalOcc2"+strName+"<0,0,CreditTotalOcc2"+strName+") else 0 end) reccreditOcc2,  \n"+
	
										"			sum(case when (yearrectify*12+"+EndMonth+")>('"+strEndYearMonth+"'-"+zlStartMonth+") and (yearrectify*12+"+EndMonth+")<=('"+strEndYearMonth+"') then if(DebitTotalOcc1"+strName+">0,0,DebitTotalOcc1"+strName+")+if(DebitTotalOcc2"+strName+">0,0,DebitTotalOcc2"+strName+") else 0 end) recdebitOcc11, \n"+ 
										"			sum(case when (yearrectify*12+"+EndMonth+")>('"+strEndYearMonth+"'-"+zlStartMonth+") and (yearrectify*12+"+EndMonth+")<=('"+strEndYearMonth+"') then if(CreditTotalOcc1"+strName+">0,0,CreditTotalOcc1"+strName+")+if(CreditTotalOcc2"+strName+">0,0,CreditTotalOcc2"+strName+") else 0 end) reccreditOcc11,  \n"+
										"			sum(case when (yearrectify*12+"+EndMonth+")>('"+strEndYearMonth+"'-"+zlEndMonth+") and (yearrectify*12+"+EndMonth+")<=('"+strEndYearMonth+"' -"+zlStartMonth+") then if(DebitTotalOcc1"+strName+">0,0,DebitTotalOcc1"+strName+")+if(DebitTotalOcc2"+strName+">0,0,DebitTotalOcc2"+strName+") else 0 end) recdebitOcc21,  \n"+
										"			sum(case when (yearrectify*12+"+EndMonth+")>('"+strEndYearMonth+"'-"+zlEndMonth+") and (yearrectify*12+"+EndMonth+")<=('"+strEndYearMonth+"' -"+zlStartMonth+") then if(CreditTotalOcc1"+strName+">0,0,CreditTotalOcc1"+strName+")+if(CreditTotalOcc2"+strName+">0,0,CreditTotalOcc2"+strName+") else 0 end) reccreditOcc21  \n"+
					
										
										"			from( \n"+ 
										"				select accpackageid,subjectid as sid,assitemid,"+strDataNameRec+",DebitTotalOcc1"+strName+",DebitTotalOcc2"+strName+",CreditTotalOcc1"+strName+",CreditTotalOcc2"+strName+",substring(accpackageid,7) as yearrectify \n"+ 
										"				from "+tableName[1]+"   \n"+
										"				where projectid = "+project.getProjectId()+"   \n"+
										
	//									加过滤，避免太多
										" 				and (  \n" +
										" 					abs(DebitTotalOcc1"+strName+")+abs(CreditTotalOcc1"+strName+") \n" +
										" 					+abs(DebitTotalOcc2"+strName+")+abs(CreditTotalOcc2"+strName+") \n" +
										" 					+abs(DebitTotalOcc3"+strName+")+abs(CreditTotalOcc3"+strName+") \n" +
										" 					+abs(DebitTotalOcc4"+strName+")+abs(CreditTotalOcc4"+strName+") \n" +
										" 					+abs(DebitTotalOcc5"+strName+")+abs(CreditTotalOcc5"+strName+") \n" +
										" 					+abs(DebitTotalOcc6"+strName+")+abs(CreditTotalOcc6"+strName+") \n" +
										" 				)>0 \n " +
										
										
										"			)b  \n"+
										"			group by accpackageid,dataname,sid,assitemid \n"+ 
										"		) a ,(				 \n"+
										"			select distinct accpackageid,subjectid,AccName,tokenid \n"+ 
										"			from c_account  \n"+
										"			where 1=1  \n"+
										"			and subyearmonth*12+submonth<= '"+strEndYearMonth1+"' \n"+ 
	//									"			and subyearmonth*12+submonth>('"+strEndYearMonth+"'-"+zlEndMonth+") \n"+ 
										"			and (subjectfullname2 = '"+SubjectName+"' or subjectfullname2 like '"+SubjectName+"/%') \n"+ 
										"		) b ,( \n" +
										"			select distinct AssItemID,AssTotalName1 \n" +
										"			from c_assitementryacc a  \n" +
										"			where 1=1 \n" +
										"			and subyearmonth*12+submonth<= '"+strEndYearMonth1+"' \n" + 
	//									"			and subyearmonth*12+submonth>('"+strEndYearMonth+"'-"+zlEndMonth+")  \n" +
										"		)  c  \n"+
										"		where 1=1  \n"+
										"		and a.accpackageid = b.accpackageid \n"+ 
										"		and a.sid=b.subjectid  \n" +
										"		and a.assitemid = c.assitemid \n"+
									
										"		) b on a.dataname = b.dataname and a.tokenid=b.tokenid and a.AssTotalName1=b.AssTotalName1\n\n"+
										
										"  )a \n" +
										")b \n" +
										" on a.sname=b.tokenid and a.tokenid=b.AssTotalName1 and a.dataname=if(b.dataname='0','本位币',b.dataname) \n"+
	//									" set "+name[i]+"=if(abs("+strDayUpdate+")>=abs(a."+preToken+"sdbalance),0,if("+preToken+"sdbalance>0,if(("+preToken+"sdbalance -occ1) <0,0,if(occ2 is null,"+preToken+"sdbalance-occ1,if( ("+preToken+"sdbalance-occ1-occ2)<=0,"+preToken+"sdbalance-occ1,occ2))),if(occ2 is null,"+preToken+"sdbalance,if(occ2>0,"+preToken+"sdbalance,0)))) \n" +
										" set "+name[i]+"=if(abs("+strDayUpdate+")>=abs(a."+preToken+"sdbalance),0,if("+preToken+"sdbalance>0,if(("+preToken+"sdbalance -occ1) <0,0,if(occ2 is null,"+preToken+"sdbalance-occ1,if( ("+preToken+"sdbalance-occ1-occ2)<=0,"+preToken+"sdbalance-occ1,occ2))),if(("+preToken+"sdbalance +occ3) >=0,0,if(occ4 is null,"+preToken+"sdbalance+occ3,if( ("+preToken+"sdbalance+occ3+occ4)>=0,"+preToken+"sdbalance+occ3,(-1)*occ4))))) \n" +
										"  where a.issubject = '"+isSubject+"' " +
										" and if(a.dataname='本位币',0,1) = '"+DataName+"' " +
										" and b.tokenid is not null ";
										
									}
								
								}
//								System.out.println(sql);
								
							}
							
//							System.out.println(sql);
							ps = this.myConn.prepareStatement(sql);
							ps.execute();
							DbUtil.close(ps);
							
						}//for (int i=0;i<iNameLength;i++){
						
						if (strGroup[iGroup].indexOf("5")>-1
							||strGroup[iGroup].indexOf("c")>-1	
							
							||strGroup[iGroup].indexOf("s")>-1
							||strGroup[iGroup].indexOf("t")>-1
								){
							
							//更新z_analsyerectify的property
							sql = "update  z_analsyerectify a ,z_subjectentryrectify b " +
							" set a.property = b.property " +
							" where a.ProjectID = "+projectID+" " +
							" and b.ProjectID = "+projectID+" " +
							" and ifnull(a.property,'') = '' " +
							" and a.autoid = b.autoid";
							System.out.println("更新z_analsyerectify的property|SQL:"+sql);
							ps = this.myConn.prepareStatement(sql);
							ps.execute();
							DbUtil.close(ps);
							
							for (int i=0;i<iNameLength;i++){
								
								if (i==0){
									strDayUpdate="0";
								}else{
									strDayUpdate+="+" + name[i-1] ;
								}
									
								boolean bEndMonth=false;
								
								if ("Day11".equals(name[i]) 
									|| "sdDay11".equals(name[i]) 
									|| "curDay11".equals(name[i])
									|| "cursdDay11".equals(name[i])
									
									|| "tzDay11".equals(name[i])
									|| "cflDay11".equals(name[i])
									
									){
									//半年以内
									zlStartMonth="0";
									zlEndMonth="6";
								}else if ("Day10".equals(name[i])
									||"sdDay10".equals(name[i])
									||"curDay10".equals(name[i])
									||"cursdDay10".equals(name[i])
									
									|| "tzDay10".equals(name[i])
									|| "cflDay10".equals(name[i])
									
									){
									//3月以内
									zlStartMonth="0";
									zlEndMonth="3";
								}else if ("Day6".equals(name[i])
									||"sdDay6".equals(name[i])
									||"curDay6".equals(name[i])
									||"cursdDay6".equals(name[i])
									
									|| "tzDay6".equals(name[i])
									|| "cflDay6".equals(name[i])
									
									){
									//1月以内
									zlStartMonth="0";
									zlEndMonth="1";
								}else if ("Day7".equals(name[i])
									||"sdDay7".equals(name[i])
									||"curDay7".equals(name[i])
									||"cursdDay7".equals(name[i])
									
									|| "tzDay7".equals(name[i])
									|| "cflDay7".equals(name[i])
									
									){
									//1月到3月
									zlStartMonth="1";
									zlEndMonth="3";
								}else if ("Day8".equals(name[i])
									||"sdDay8".equals(name[i])
									||"curDay8".equals(name[i])
									||"cursdDay8".equals(name[i])
									
									|| "tzDay8".equals(name[i])
									|| "cflDay8".equals(name[i])
									
									){
									//3月到6月
									zlStartMonth="3";
									zlEndMonth="6";
								}else if ("Day13".equals(name[i])
									||"sdDay13".equals(name[i])
									||"curDay13".equals(name[i])
									||"cursdDay13".equals(name[i])
									
									|| "tzDay13".equals(name[i])
									|| "cflDay13".equals(name[i])
									
									){
									//6月到9月
									zlStartMonth="6";
									zlEndMonth="9";
								}else if ("Day14".equals(name[i])
									||"sdDay14".equals(name[i])
									||"curDay14".equals(name[i])
									||"cursdDay14".equals(name[i])
									
									|| "tzDay14".equals(name[i])
									|| "cflDay14".equals(name[i])
									
									){
									//9月到12月
									zlStartMonth="9";
									zlEndMonth="12";
								}else if ("Day0".equals(name[i])
									||"sdDay0".equals(name[i])
									||"curDay0".equals(name[i])
									||"cursdDay0".equals(name[i])
									
									|| "tzDay0".equals(name[i])
									|| "cflDay0".equals(name[i])
									
									){
									//1年以内
									zlStartMonth="0";
									zlEndMonth="12";
								}else if ("Day9".equals(name[i])
									||"sdDay9".equals(name[i])
									||"curDay9".equals(name[i])
									||"cursdDay9".equals(name[i])
									
									|| "tzDay9".equals(name[i])
									|| "cflDay9".equals(name[i])
									
									){
									//半年到1年
									zlStartMonth="6";
									zlEndMonth="12";
								}else if ("Day1".equals(name[i])
									||"sdDay1".equals(name[i])
									||"curDay1".equals(name[i])
									||"cursdDay1".equals(name[i])
									
									|| "tzDay1".equals(name[i])
									|| "cflDay1".equals(name[i])
									
									){
									//1年到2年
									zlStartMonth="12";
									zlEndMonth="24";
								}else if ("Day2".equals(name[i])
									||"sdDay2".equals(name[i])
									||"curDay2".equals(name[i])
									||"cursdDay2".equals(name[i])
									
									|| "tzDay2".equals(name[i])
									|| "cflDay2".equals(name[i])
									
									){
									//2年到3年
									zlStartMonth="24";
									zlEndMonth="36";
								}else if ("Day3".equals(name[i])
									||"sdDay3".equals(name[i])
									||"curDay3".equals(name[i])
									||"cursdDay3".equals(name[i])
									
									|| "tzDay3".equals(name[i])
									|| "cflDay3".equals(name[i])
									
									){
									//3年到4年
									zlStartMonth="36";
									zlEndMonth="48";
								}else if ("Day4".equals(name[i])
									||"sdDay4".equals(name[i])
									||"curDay4".equals(name[i])
									||"cursdDay4".equals(name[i])
									
									|| "tzDay4".equals(name[i])
									|| "cflDay4".equals(name[i])
									
									){
									//4年到5年
									zlStartMonth="48";
									zlEndMonth="60";
								}else if ("Day5".equals(name[i])
									||"sdDay5".equals(name[i])
									||"curDay5".equals(name[i])
									||"cursdDay5".equals(name[i])
									
									|| "tzDay5".equals(name[i])
									|| "cflDay5".equals(name[i])
									
									){
									//5年以上
									zlStartMonth="60";
									zlEndMonth="1000";
									bEndMonth=true;
								}else if ("Day12".equals(name[i])
									||"sdDay12".equals(name[i])
									||"curDay12".equals(name[i])
									||"cursdDay12".equals(name[i])
									
									|| "tzDay12".equals(name[i])
									|| "cflDay12".equals(name[i])
									
									){
									//3年以上
									zlStartMonth="36";
									zlEndMonth="1000";
									bEndMonth=true;
								}
								
								
								if(bool && preToken.indexOf("cur")<0 
//										&& ("".equals(Year) || "0".equals(Year)) 
									){	//只有在本位币和年度=0时，才能显示新账龄
										
									//账龄调整类型 311、411是同意的，611、711是不同意的
									
									String sqlProperty = " and property in ('311','411') ";
									if(name[i].indexOf("tz") > -1){
										//调整的账龄
										sqlProperty = " and property = '311' ";
									}else if(name[i].indexOf("cfl") > -1){
//										重分类的账龄
										sqlProperty = " and property = '411' ";
									}
									String sqlYear = Year;
									if("".equals(Year)){
										sqlYear = "0";
									}
									
									if("科目".equals(isSubject)){	
										sql="update "+tempTable+" a , \n" + 
										"( \n" +
										"	select tokenid,if(dataname = 0,'本位币',dataname) as dataname,sum(direction * AnalsyeBalance) as AnalsyeBalance \n" +
										"	from z_analsyerectify " +
										"	where projectid="+projectID+" \n" +
										"	and issubject = '"+isSubject+"' \n" +
										"	and submonth >='"+zlStartMonth+"' \n" +
										"	and submonth < '"+zlEndMonth+"' \n" +
										
										sqlProperty + 
										"	and subyearmonth = '"+sqlYear+"' " +
										
										"	group  by tokenid,dataname \n" +
										") b   \n" +
										"set "+name[i]+"= ifnull(direction2 * AnalsyeBalance,0) + "+name[i]+"  " +
										"where a.issubject = '"+isSubject+"' " +
										"and if(a.dataname='本位币',0,1) = '"+DataName+"' " +
										"and a.tokenid=b.tokenid " ;
//										"and a.dataname=b.dataname ";
										
									}else{
										sql="update "+tempTable+" a , \n" + 
										"( \n" +
										"	select tokenid,AssTotalName,if(dataname = 0,'本位币',dataname) as dataname,sum(direction * AnalsyeBalance) as AnalsyeBalance \n" +
										"	from z_analsyerectify " +
										"	where projectid="+projectID+" \n" +
										"	and issubject = '"+isSubject+"' \n" +
										"	and submonth >='"+zlStartMonth+"' \n" +
										"	and submonth < '"+zlEndMonth+"' \n" +
										
										sqlProperty +  
										"	and subyearmonth = '"+sqlYear+"' " +
										
										"	GROUP  BY tokenid,AssTotalName,dataname \n" +
										") b \n" +
										"set "+name[i]+"= ifnull(direction2 * AnalsyeBalance,0) + "+name[i]+" " +
										"where a.issubject = '"+isSubject+"' " +
										"and if(a.dataname='本位币',0,1) = '"+DataName+"' " +
										"and a.sname=b.tokenid and a.tokenid=b.AssTotalName " ;
//										"and a.dataname=b.dataname ";
									}
									
//									System.out.println(sql);
									ps = this.myConn.prepareStatement(sql);
									ps.execute();
									DbUtil.close(ps);	
								}
								
								
							} 
						}
						
						
						if (strGroup[iGroup].indexOf("4")>-1
							||strGroup[iGroup].indexOf("5")>-1
							||strGroup[iGroup].indexOf("b")>-1
							||strGroup[iGroup].indexOf("c")>-1
							
							||strGroup[iGroup].indexOf("s")>-1
							||strGroup[iGroup].indexOf("t")>-1
							){
							continue ;//返回
						}
					}

					/**
					 * 没有本位币和原币之分
					 * 
					 * 借方凭证张数	
					 * 贷方凭证张数
					 * 凭证日期 ：最后的发生日
					 */
					if (strGroup[iGroup].indexOf("l")>-1){
						
						sql = "UPDATE "+tempTable+" set VchDate = '' where VchDate = '0.00' or ifnull(VchDate,'') = ''";
						ps = this.myConn.prepareStatement(sql);
						ps.execute();
						DbUtil.close(ps);
						sql = "UPDATE "+tempTable+" set DebitVchDate = '' where DebitVchDate = '0.00' or ifnull(DebitVchDate,'') = ''";
						ps = this.myConn.prepareStatement(sql);
						ps.execute();
						DbUtil.close(ps);
						sql = "UPDATE "+tempTable+" set CreditVchDate = '' where CreditVchDate = '0.00' or ifnull(CreditVchDate,'') = ''";
						ps = this.myConn.prepareStatement(sql);
						ps.execute();
						DbUtil.close(ps);
						sql = "UPDATE "+tempTable+" set DebitSummary = '' where DebitSummary = '0.00' or ifnull(DebitSummary,'') = ''";
						ps = this.myConn.prepareStatement(sql);
						ps.execute();
						DbUtil.close(ps);
						sql = "UPDATE "+tempTable+" set CreditSummary = '' where CreditSummary = '0.00' or ifnull(CreditSummary,'') = ''";
						ps = this.myConn.prepareStatement(sql);
						ps.execute();
						DbUtil.close(ps);
						
						String Currency1 = "";
						String Currency2 = "";
						String where1 = "";
						String where2 = "";
						String where3 = "";
						if(!"0".equals(DataName)){
							Currency1 = "a.Currency,";
							Currency2 = ",a.Currency";
							where1 = " and a.Currency <> '' ";
							where2 = " and a.dataname = b.Currency ";
							where3 = " and a.Currency = b.Currency ";
						}
						
						if("科目".equals(isSubject)){	
							sql = "update "+tempTable+" a ,( " +
								"\n 	select  tokenid,dataname,b.* " +
								"\n 	from "+tableName[0]+" a,( " +
								"\n			select a.AccPackageID, a.subjectid, "+Currency1+" " +
								"\n			count(distinct voucherid) a1," +
								"\n			count(distinct if(Dirction=1,voucherid,0) ) -if(sum(distinct if(Dirction=-1,voucherid,0))=0,0,1) as DebitVoucherCount," +
								"\n			count(distinct if(Dirction=-1,voucherid,0) ) -if(sum(distinct if(Dirction=1,voucherid,0))=0,0,1) as CreditVoucherCount, " +
								"\n			max(VchDate) as VchDate, " +
								"\n			b.DebitVchDate,b.CreditVchDate, " +
								"\n			GROUP_CONCAT(IF(VchDate = b.DebitVchDate AND Dirction=1,Summary,NULL)) AS DebitSummary, " +
								"\n			GROUP_CONCAT(IF(VchDate = b.CreditVchDate AND Dirction=-1,Summary,NULL)) AS CreditSummary ," +
								
								"\n			max(a.OccurValue) as OccurValue, " +
								"\n			max(if(Dirction=1,a.OccurValue,null)) as DebitOccurValue, " +
								"\n			max(if(Dirction=-1,a.OccurValue,null)) as CreditOccurValue " +
								
								"\n			from c_subjectentry a,(" +
								"\n				SELECT AccPackageID, subjectid, "+Currency1+" " +
								"\n				MAX(IF(Dirction=1,VchDate,'')) AS DebitVchDate," +
								"\n				MAX(IF(Dirction=-1,VchDate,'')) AS CreditVchDate" +
								"\n				from c_subjectentry a" +
								"\n				where 1=1 " + where1 + 
								"\n				and substring(VchDate,1,4) * 12 + substring(VchDate,6,2) >= '"+strStartYearMonth+"' " +
								"\n				and substring(VchDate,1,4) * 12 + substring(VchDate,6,2) <= '"+strEndYearMonth+"' " +
								"\n				group by AccPackageID,subjectid " + Currency2 + 
								"\n			) b " +
								"\n			where 1=1 " + where1 + 
								"\n			and substring(VchDate,1,4) * 12 + substring(VchDate,6,2) >= '"+strStartYearMonth+"' " +
								"\n			and substring(VchDate,1,4) * 12 + substring(VchDate,6,2) <= '"+strEndYearMonth+"' " +
								"\n			AND a.AccPackageID = b.AccPackageID AND a.subjectid = b.subjectid " + where3 + 
								"\n			group by AccPackageID,subjectid " + Currency2 + 
								"\n 	) b" +
								"\n 	where 1=1 " +
								"\n		and subyearmonth * 12 + submonth ='"+strEndYearMonth+"'" +
								"\n		and (a.subjectfullname2 = '"+SubjectName+"' or a.subjectfullname2 like '"+SubjectName+"/%')" +
								"\n		and a.AccPackageID = b.AccPackageID AND a.subjectid = b.subjectid " + 
								"\n		and a.subjectid = b.subjectid " + where2 + 
								"\n ) b " +
								"\n set " +
								
								"\n	a.OccurValue = b.OccurValue," +
								
								"\n	a.DebitVoucherCount = b.DebitVoucherCount," +
								"\n	a.CreditVoucherCount = b.CreditVoucherCount," +
								
								"\n	a.DebitVchDate = b.DebitVchDate," +
								"\n	a.CreditVchDate = b.CreditVchDate," +
								"\n	a.DebitSummary = b.DebitSummary," +
								"\n	a.CreditSummary = b.CreditSummary," +
								"\n	a.VchDate = b.VchDate" +
								
								"\n where 1=1" +
								"\n and a.issubject = '"+isSubject+"'" +
								"\n and a.tokenid=b.tokenid and a.dataname=if(b.dataname='0','本位币',b.dataname) ";
							
						}else{
							
							
							sql = "update "+tempTable+" a ,( " +
							"\n 	select  AssTotalName1,AccID,a.AssItemID,dataname,b.OccurValue,b.DebitVoucherCount,b.CreditVoucherCount,b.VchDate,b.DebitVchDate,b.CreditVchDate,b.DebitSummary,b.CreditSummary " +
							"\n 	from "+tableName[0]+" a,( " +
							"\n			select a.AccPackageID, a.subjectid,a.AssItemID, "+Currency1+" " +
							"\n			count(distinct voucherid) a1," +
							"\n			count(distinct if(Dirction=1,voucherid,0) ) -if(sum(distinct if(Dirction=-1,voucherid,0))=0,0,1) as DebitVoucherCount," +
							"\n			count(distinct if(Dirction=-1,voucherid,0) ) -if(sum(distinct if(Dirction=1,voucherid,0))=0,0,1) as CreditVoucherCount, " +
							"\n			max(VchDate) as VchDate, " +
							"\n			b.DebitVchDate,b.CreditVchDate, " +
							"\n			GROUP_CONCAT(IF(VchDate = b.DebitVchDate AND Dirction=1,Summary,NULL)) AS DebitSummary, " +
							"\n			GROUP_CONCAT(IF(VchDate = b.CreditVchDate AND Dirction=-1,Summary,NULL)) AS CreditSummary, " +

							"\n			max(a.AssItemSum) as OccurValue, " +
							"\n			max(if(Dirction=1,a.AssItemSum,null)) as DebitOccurValue, " +
							"\n			max(if(Dirction=-1,a.AssItemSum,null)) as CreditOccurValue " +
							
							"\n			from c_assitementry a ,(" +
							"\n				SELECT AccPackageID, subjectid,a.AssItemID, "+Currency1+" " +
							"\n				MAX(IF(Dirction=1,VchDate,'')) AS DebitVchDate," +
							"\n				MAX(IF(Dirction=-1,VchDate,'')) AS CreditVchDate" +
							"\n				from c_assitementry a " +
							"\n				where 1=1 " + where1 + 
							"\n				and substring(VchDate,1,4) * 12 + substring(VchDate,6,2) >= '"+strStartYearMonth+"' " +
							"\n				and substring(VchDate,1,4) * 12 + substring(VchDate,6,2) <= '"+strEndYearMonth+"' " +
							"\n				group by AccPackageID,subjectid,AssItemID " + Currency2 + 
							"\n			) b " +
							"\n			where 1=1 " + where1 + 
							"\n			and substring(VchDate,1,4) * 12 + substring(VchDate,6,2) >= '"+strStartYearMonth+"' " +
							"\n			and substring(VchDate,1,4) * 12 + substring(VchDate,6,2) <= '"+strEndYearMonth+"' " +
							"\n			and a.AccPackageID = b.AccPackageID AND a.subjectid = b.subjectid and a.AssItemID = b.AssItemID " + where3 + 
							"\n			group by a.AccPackageID,a.subjectid,a.AssItemID " + Currency2 + 
							"\n 	) b" +
							"\n 	where 1=1 " +
							"\n		and subyearmonth * 12 + submonth ='"+strEndYearMonth+"'" +
//							"\n		and (a.subjectfullname2 = '"+SubjectName+"' or a.subjectfullname2 like '"+SubjectName+"/%')" +
							"\n		and a.AccPackageID = b.AccPackageID" +
							"\n		and a.accid = b.subjectid " +
							"\n		and a.AssItemID = b.AssItemID " + where2 + 
							"\n ) b " +
							"\n set " +
							
							"\n	a.OccurValue = b.OccurValue," +
							
							"\n	a.DebitVoucherCount = b.DebitVoucherCount," +
							"\n	a.CreditVoucherCount = b.CreditVoucherCount," +
							
							"\n	a.DebitVchDate = b.DebitVchDate," +
							"\n	a.CreditVchDate = b.CreditVchDate," +
							"\n	a.DebitSummary = b.DebitSummary," +
							"\n	a.CreditSummary = b.CreditSummary," +
							"\n	a.VchDate = b.VchDate" +
							
							"\n where 1=1" +
							"\n and a.issubject = '"+isSubject+"'" +
							"\n and a.tokenid=b.AssTotalName1 " +
							"\n and a.sid like concat('%`',b.accid,'`',b.assitemid,'%') " +
							"\n and a.dataname=if(b.dataname='0','本位币',b.dataname) ";
						}
						
						System.out.println(sql);
						ps = this.myConn.prepareStatement(sql);
						ps.execute();
						DbUtil.close(ps);

						if (strGroup[iGroup].indexOf("l")>-1){
							continue ;//返回
						}
						
					}
					
					/**
					 * 关联单位
					 */
					if (strGroup[iGroup].indexOf("o")>-1){
						
						sql = "update "+tempTable+" a left join asdb.k_connectcompanys b" +
							" on b.customerid = '"+project.getCustomerId()+"' and connectcompanysname = fullname " +
							" set connect = if(ifnull(connectcompanysname,'') = '','否','是') ";
						ps = this.myConn.prepareStatement(sql);
						ps.execute();
						DbUtil.close(ps);

						if (strGroup[iGroup].indexOf("o")>-1){
							continue ;//返回
						}
					}
					
					/**
					 * 发函单位
					 */
					if (strGroup[iGroup].indexOf("p")>-1){
						
						sql = "update "+tempTable+" a left join z_letters b " +
							" on b.projectid = '"+project.getProjectId()+"' " +
							" and sid like concat('%',subjectid,if(assitemid='','',concat('`',assitemid)),'%') " +
							" set funccase = if(ifnull(projectid,'') = '','否','是') ";
						ps = this.myConn.prepareStatement(sql);
						ps.execute();
						DbUtil.close(ps);

						if (strGroup[iGroup].indexOf("p")>-1){
							continue ;//返回
						}
					}
					
					/**
					 * 标准科目  科目编号与名称  核算编号与名称
					 */
					if (strGroup[iGroup].indexOf("q")>-1){
						
//						标准科目 科目编号与名称
						sql = "update "+tempTable+" a left join ( \n" +
						"	select  distinct a.subjectid,a.AccName,a.tokenid,a.subjectfullname1,a.subjectfullname2,b.subjectfullname2 as standname,concat(a.subjectid,'|',a.AccName) as subjectIDorName \n" +	
						"		from c_account a , ( \n" +
						"			select distinct subjectid,subjectfullname1,subjectfullname2 from c_account a \n" +  				
						"			where 1=1  				 \n" +
						"			and a.subyearmonth*12+a.submonth='"+strEndYearMonth+"' \n" + 	
						"			and (a.subjectfullname2 like '"+SubjectName+"/%'  or a.subjectfullname2 = '"+SubjectName+"' ) \n" + 
						"			and level1 = 1  \n" +
						"		) b \n" +
						"		where a.subyearmonth*12+a.submonth='"+strEndYearMonth+"' \n" + 
						"		and (a.subjectfullname2 like '"+SubjectName+"/%' or a.subjectfullname2 = '"+SubjectName+"') \n" +  
						"		and (a.subjectfullname2 like concat(b.subjectfullname2,'/%') or a.subjectfullname2 = b.subjectfullname2) \n" +  

						"		union \n" + 
						"		select a.subjectid,a.subjectname,a.subjectfullname,a.subjectfullname,a.subjectfullname,b.subjectfullname2,concat(a.subjectid,'|',a.subjectname) as subjectIDorName \n" +	
						"		from z_usesubject a,( \n" +
						"			select distinct subjectid,subjectfullname1,subjectfullname2 from c_account a \n" +  				
						"			where 1=1  				 \n" +
						"			and a.subyearmonth*12+a.submonth='"+strEndYearMonth+"' \n" + 	
						"			and (a.subjectfullname2 like '"+SubjectName+"/%'  or a.subjectfullname2 = '"+SubjectName+"' ) \n" + 
						"			and level1 = 1  \n" +
						"		) b \n" +
						"		where projectid="+projectID+" \n" + 
						"		and tipsubjectid = b.subjectid \n" +

						"		union 			 \n" +
						"		select a.subjectid,a.subjectname,a.subjectfullname,a.subjectfullname,a.subjectfullname,b.subjectfullname,concat(a.subjectid,'|',a.subjectname) as subjectIDorName \n" +	
						"		from z_usesubject a ,( \n" +
						"			select * from z_usesubject a \n" +  
						"			where projectid="+projectID+"  \n" +
						"			and (a.subjectfullname like '"+SubjectName+"/%'  or a.subjectfullname = '"+SubjectName+"' ) \n" + 
						"			and level0 = 1 \n" +
						"		) b \n" +
						"		where a.projectid="+projectID+" \n" + 
						"		and (a.subjectfullname like '"+SubjectName+"/%'  or a.subjectfullname = '"+SubjectName+"' ) \n" + 
						"		and a.tipsubjectid = b.tipsubjectid \n" +
						
						"	) b on a.sName = b.tokenid" +
						"	set a.standname = b.standname," +
						"	a.subjectIDorName = b.subjectIDorName  ";	
						ps = this.myConn.prepareStatement(sql);
						ps.execute();
						DbUtil.close(ps);
						
						if("核算".equals(isSubject)){	//核算编号与名称
							sql = "update "+tempTable+" a left join c_assitementryacc b \n" +
							" on a.tokenid = b.AssTotalName1 and a.sid like concat('%`',b.accid,'`',b.assitemid,'%')" +
							" set a.assitemIDorName = concat(assitemid,'|',assitemname) " +
							" where isSubject = '"+isSubject+"'" ;
							ps = this.myConn.prepareStatement(sql);
							ps.execute();
							DbUtil.close(ps);
							
							sql = "update "+tempTable+" a set assitemIDorName = ''  " +
							" where a.assitemIDorName = 0.00 " ;
							ps = this.myConn.prepareStatement(sql);
							ps.execute();
							DbUtil.close(ps);
							
							sql = "update "+tempTable+" a set assitemIDorName = ''  " +
							" where a.subjectIDorName = '' " ;
							ps = this.myConn.prepareStatement(sql);
							ps.execute();
							DbUtil.close(ps);
						}
						
					}
					
				
				}//行循环
			}//列循环
			
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("error SQL : " + sql);
			throw e;
		} finally {
//			////System.out.println("finally SQL : " +sql);
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
	}
	
	
	/**
	 * 静态指标批量计算
	 */
	public void getObject(String projectID, String tempTable1 ,Project project){
		String sql="";
		
		Statement st = null;
		Statement st1 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		
		
		ASFuntion CHF=new ASFuntion();
		String tempTable = null;
		try {
			
			System.out.println("1:"+CHF.getCurrentTime());
			
			st = myConn.createStatement(); 
			st1 = myConn.createStatement(); 
			
			String begin = "".equals(project.getAuditTimeBegin()) ? "0" : project.getAuditTimeBegin().substring(0,4);
			String end = "".equals(project.getAuditTimeEnd()) ? "0" : project.getAuditTimeEnd().substring(0,4);
			String bMonth = "".equals(project.getAuditTimeBegin()) ? "0" : project.getAuditTimeBegin().substring(5,7);
			String eMonth = "".equals(project.getAuditTimeEnd()) ? "0" : project.getAuditTimeEnd().substring(5,7);

			/**
			 *  代码逻辑
			 *   
			 * 
			  
			  	int i=1;
				while rs.next(){
					
					//分析指标
					
					if (不同){
						if (i>1){
							计算并更新
						}
							
						删除表、建表
				
						old=.
					}
				
					//插入；
					
					i++;
				
				}
				
				计算并更新
				删除表
			  
			  
			 * 
			 */
			
			sql="select autoid,myname,myproperty,myparam,concat(myproperty,'-',myparam) as mygroup from "+tempTable1 +" order by myproperty,myparam";
//			System.out.println(sql);
			rs1=st1.executeQuery(sql);
			String oldgroup="",curgroup="";
			String allfield="",myparm="",SubjectFullName="",Year="",Month="",DataName="",autoid="",AssTotalName="";
			Map args=null;
			String newallfield="",newYear="",newMonth="";
			Map newargs=null;
			int iCount=1;
			
			tempTable = "tt1_"+DELUnid.getCharUnid();
			
			String strSelectSql="";
			
			System.out.println("2:"+CHF.getCurrentTime());
			
			while (rs1.next()){
				
				
				newallfield=""; 
				newYear="";
				newMonth="";
				
				newargs=new HashMap();
				newargs.put("project", project);
				
				/**
				 * 分析指标
				 */
				newallfield=rs1.getString("myproperty");
				SubjectFullName=rs1.getString("myname");
				myparm=rs1.getString("myparam");
				autoid=rs1.getString("autoid");
				
				curgroup=rs1.getString("mygroup");
				
				String[] param=null;
				int iLen=0;
				if (myparm!=null){
					param=myparm.split("`");
					iLen=param.length;
				}
				
				/*
				if (iLen>0){
					sid=param[0].trim();
				}
				*/
				
				if (iLen>1){
					newYear=param[2].trim();
				}
				if ("".equals(newYear)){
					newYear="0";
				}
				
				if (iLen>2){
					newMonth=param[3].trim();
				}
				if ("".equals(newMonth)){
					newMonth="0";
				}
				
				if (iLen>3){
					DataName=param[4].trim();
				}
				if ("".equals(DataName)){
					DataName="0";
				}
				
	
				if (iLen>4){
					AssTotalName=param[5].trim();
				}
	
				if(iLen>5 ){
					for(int j=6;j<param.length;j++){
						if(!"".equals(param[j].trim())){
							String [] si = param[j].trim().split("=");
							newargs.put(si[0],si[1]);
						}
					}
				}
				
				//System.out.println( autoid  + "|" + newallfield + "|" + SubjectFullName + "|" + myparm + "|" + curgroup + "|iCount=" +  iCount+"|Year="+Year+"|newYear="+newYear);
				
				String strStartYearMonth="",strEndYearMonth="";
				
				strStartYearMonth = String.valueOf(Integer.parseInt(begin)*12+Integer.parseInt(bMonth));
				strEndYearMonth = String.valueOf(Integer.parseInt(end)*12+Integer.parseInt(eMonth));
				
				
				int iYearMonthArea=Integer.parseInt(strEndYearMonth)-Integer.parseInt(strStartYearMonth) + 1;
				if (Integer.parseInt(newYear)>0){
					//往明年、后年比较，扩展期末
					strEndYearMonth = String.valueOf(Integer.parseInt(strEndYearMonth) + iYearMonthArea * Integer.parseInt(newYear));
				}else if (Integer.parseInt(newYear)<0){
					//往去年、前年比较，扩展期初
					strStartYearMonth = String.valueOf(Integer.parseInt(strStartYearMonth) + iYearMonthArea * Integer.parseInt(newYear));
				}
				
				String acc = String.valueOf(Integer.parseInt(project.getAccPackageId()) + Integer.parseInt(newYear));
				
				String strW="";
				if("".equals(DataName.trim()) || "0".equals(DataName) || "本位币".equals(DataName)) {
					DataName = "0";
				}else{
					strW += " and DataName = '"+DataName+"'";
				}
				
				
				
				if("".equals(newMonth)){
					newMonth = "0";
				}
					
//				//System.out.println(newYear + "|" + newMonth + "|" + strStartYearMonth + "|" + strEndYearMonth);
				
				String[] result = getClientIDAndDirectionByStandName(myConn,acc, project.getProjectId(),SubjectFullName);
	            String subjectid = result[0];
	            
	            String isCurname = "";
				if (newallfield.indexOf("原币")>-1 || (!"".equals(DataName) && !"0".equals(DataName) && !"本位币".equals(DataName) )){
					isCurname="支持";
					newargs.put("支持外币", "支持");
				}
				String isUnitName = "";
				if (newallfield.indexOf("数量")>-1){
					isUnitName="支持";
					newargs.put("支持数量", "支持");
				}
				
				
				String isAssItem = CHF.showNull((String)newargs.get("包含核算"));
				String AssItem = CHF.showNull((String)newargs.get("核算名称"));
	            
				if("".equals(AssTotalName)){ 	//科目指标
					newargs.put("包含核算","否");
				}else{							//核算指标
//					newargs.put("包含核算","否");
				}
				
//	            isAssItem  	AssItem
//	            ""			""			"客户;供应商;关联;往来;费用";
//	            ""			!""			AssItem
//	            否			""			科目
//	            否			!""			AssItem
//	            是			""			"客户;供应商;关联;往来;费用";
//	            是			!""			AssItem
	            
				if("".equals(isAssItem) && "".equals(AssItem)){
					AssItem="客户;供应商;往来;费用";
					
					/**
					 * 根据科目辅助核算披露，得到默认核算名称。没有值默认为［客户;供应商;关联;往来;费用］
					 */
					String newAssItem = new SubjectAssitemService(myConn).getFunction(acc, SubjectFullName);
					if(!"".equals(newAssItem)){
						AssItem = newAssItem;
					}
					newargs.put("核算名称", AssItem);
				}else if("".equals(isAssItem) && !"".equals(AssItem)){
					//不用
				}else if("否".equals(isAssItem) && "".equals(AssItem)){
					//不用
				}else if("否".equals(isAssItem) && !"".equals(AssItem)){
					//不用
				}else if("是".equals(isAssItem) && "".equals(AssItem)){
					AssItem="客户;供应商;往来;费用";
					
					/**
					 * 根据科目辅助核算披露，得到默认核算名称。没有值默认为［客户;供应商;关联;往来;费用］
					 */
					String newAssItem = new SubjectAssitemService(myConn).getFunction(acc, SubjectFullName);
					if(!"".equals(newAssItem)){
						AssItem = newAssItem;
					}
					newargs.put("核算名称", AssItem);
				}else if("是".equals(isAssItem) && !"".equals(AssItem)){
					//不用
				}
				
//				if("否".equals(isAssItem) && "".equals(AssItem)){
//					
//				}else if("是".equals(isAssItem) && "".equals(AssItem)){
//					
//				}else if("".equals(AssItem)){
//					AssItem="客户;供应商;关联;往来";
//					
//					/**
//					 * 根据科目辅助核算披露，得到默认核算名称。没有值默认为［客户;供应商;关联;往来;费用］
//					 */
//					String newAssItem = new SubjectAssitemService(myConn).getFunction(acc, SubjectFullName);
//					if(!"".equals(newAssItem)){
//						AssItem = newAssItem;
//					}
//					
//					System.out.println("SubjectFullName="+SubjectFullName + "|newAssItem="+newAssItem);
//					
//					newargs.put("核算名称", AssItem);
//				}
				
				//如果前后分组不同
				if (!oldgroup.equals(curgroup)){
					if (iCount>1){
						
						
						System.out.println("21:"+CHF.getCurrentTime());
						
						/**
						 * 计算并更新
						 */
//						开始计算
						String sql1 = "";
						sql="";
						
						String[] fields=allfield.split("`");
						
						GuideLineProperty[] gps=null,gps1=null;
						if (fields.length>0){
							gps1=new GuideLineProperty[fields.length];
						}
						int iGps=0;
						
						String orderby = CHF.showNull((String)args.get("排序"));
						
						
						
						sql="select fieldvalue,evalue,groupid,INSTR(concat('`','"+allfield+"','`'),concat('`',fieldvalue,'`'))>0 as d1 ,orderid from k_areafunctionfields \n"	+
							"where areaid=9999 and typeid=0 \n"	+
							"and ifnull(groupid,'') <> ''  \n"	+
							"order by groupid,orderid";
						rs=st.executeQuery(sql);
						sql="";
						while (rs.next()){
							sql+= ", add column "+rs.getString("evalue")+" decimal (15,2) DEFAULT '0.00' ";
							
							if(allfield.indexOf(rs.getString("fieldvalue"))>-1){
								sql1= " sum(" + rs.getString("evalue")+") ";
							}
							//追加fields 
							if (rs.getInt("d1")>0){
								gps1[iGps++]=new GuideLineProperty(rs.getString("evalue"),rs.getString("groupid"));
							}
							
							if (!"".equals(orderby) && rs.getString("fieldvalue").equals(orderby)){
								orderby=" order by "+rs.getString("evalue")+" desc ";
							}
							
						}
						DbUtil.close(rs);
						if (!sql1.equals("")){
							//增加字段列
							st.execute("alter table " + tempTable + sql.substring(1) + " , change sid sid varchar (300)  NULL ");
							
							st.execute("alter table " + tempTable + " add index sName (sName),add index tokenid (tokenid),add index dataname (dataname),add index sid (sid)");
						}
						
						System.out.println("22:"+CHF.getCurrentTime());
//						去掉多余的null数组单元
						if (iGps>0){
							gps=new GuideLineProperty[iGps];
							for (int i=0;i<iGps;i++){
								gps[i]=gps1[i];
							}
						}
						
//						分组排序
						if (iGps>0){
							java.util.Arrays.sort(gps,new   java.util.Comparator(){
						        public   int   compare(Object   obj1,Object   obj2){
							        String   s1   =   ((GuideLineProperty)obj1).group,
							        		 s2   =   ((GuideLineProperty)obj2).group;
							        return   s1.compareTo(s2);     //不就完了？
						        }
						    });
						}
						String oldGroup="";
						int iGroupFields=0,iInnerFields=0;
						String strGroupFields[][]=new String[iGps][];
						String temp[]=new String[iGps];
						
						GuideLineProperty gp=null;
						String strGroup[]=null;
						
						if (iGps>0){
							strGroup=new String[iGps];
						
							gp=gps[0];
							oldGroup=gp.group;
							temp[iInnerFields++]=gp.field;
							for(int i=1; i<iGps; i++){
								gp=gps[i];
								
								if (gp.group.indexOf(oldGroup)>=0){
									//是一个分组的，继续追加
									temp[iInnerFields++]=gp.field;
								}else{
									//更新分组
									String temp1[]=new String[iInnerFields];
									for (int j=0;j<iInnerFields;j++){
										temp1[j]=temp[j];
									}
									strGroupFields[iGroupFields]=temp1;
									strGroup[iGroupFields++]=oldGroup;
									         
									//重置中间变量
									oldGroup=gp.group;
									iInnerFields=0;
									temp =new String[iGps];
									temp[iInnerFields++]=gp.field;
								}
							}
							if (gp.group.indexOf(oldGroup)>=0){
								//更新分组
								strGroupFields[iGroupFields]=temp;
								strGroup[iGroupFields++]=oldGroup;
							}
						
							String [][] strGroupFields1 = new String [strGroupFields.length][];
							for(int i=0;i<strGroupFields1.length;i++){
								if(strGroupFields[i] != null){
									
									////System.out.println("strGroup="+strGroup[i]);
									
									strGroupFields1[i] = new String [strGroupFields[i].length] ;
									for(int j=0;j<strGroupFields1[i].length;j++){
										if(strGroupFields[i][j]!= null)
											strGroupFields1[i][j] = strGroupFields[i][j];	
										
										
										////System.out.println("strGroupFields1="+strGroupFields[i][j]);
										
										
									}
								}
							}
							
							System.out.println("31:"+CHF.getCurrentTime());
							setBatchProjectValue(project.getProjectId(),Year,Month,tempTable,strGroupFields,strGroup,args);
							System.out.println("32:"+CHF.getCurrentTime());
						}
						
						
						//更新表结果
						sql="update "+tempTable1+" a,(select myautoid,"+sql1+" as myvalue from "+tempTable+" group  by myautoid )b \n"
							+" set a.myvalue=b.myvalue where a.autoid=b.myautoid";
						st.execute(sql);
						
						
						
						//
						allfield=newallfield;
						Year=newYear;
						Month=newMonth;
						
						args=newargs;
						
						
					}else{
						allfield=newallfield;
						Year=newYear;
						Month=newMonth;
						
						args=newargs;
						
					}
					
					/**
					 *  删除表
					 */
					sql = "drop table if EXISTS " + tempTable;
					st.execute(sql);
					
					/**
					 * 建表
					 */
					
					System.out.println("4:"+CHF.getCurrentTime());
					strSelectSql = getRuleSQL(SubjectFullName, strStartYearMonth, strEndYearMonth, subjectid, end, args);
					////System.out.println(strSelectSql);
					
					//这里不一样！！！
					sql = "create table " + tempTable + " select *,"+autoid+" as  myautoid from (" + strSelectSql +") a where 1=2";
					st.execute(sql);
					System.out.println("5:"+CHF.getCurrentTime());
					
					sql="alter table "+tempTable+" add column unitname varchar (20)  , add column unitsign varchar (20)  ";
					st.execute(sql);
					System.out.println("6:"+CHF.getCurrentTime());
					/**
					 * 更新分组值
					 */
					oldgroup=curgroup;
					
				}
			
				
				/**
				 * 插入
				 */
				strSelectSql = getRuleSQL(SubjectFullName, strStartYearMonth, strEndYearMonth, subjectid, end, args);
//				System.out.println(strSelectSql);
				
				sql = "insert into " + tempTable + " select *,"+autoid+" as  myautoid ,'' as unitname,'' as unitsign from (" + strSelectSql +") a where 1=1 " + strW;
				st.execute(sql);
				System.out.println("7:"+CHF.getCurrentTime());
				
				if ("支持".equals(isCurname) && "支持".equals(isUnitName)){
					
					args.put("外币数量", "都要");
					
					strW += " and (DataName = '"+DataName+"' or unitname = '"+DataName+"' )";
					
					strSelectSql = CHF.replaceStr(strSelectSql, "and a.accsign=1", "and a.accsign=2");
					
					sql = "update "+tempTable+" a ,(" +
						strSelectSql +
						" ) b set a.unitname = if(b.dataname='本位币','',b.dataname)  where a.tokenid=b.tokenid and a.issubject=b.issubject and a.sName = b.sName";
					
//					////System.out.println(sql);
					st.execute(sql);
					System.out.println("8:"+CHF.getCurrentTime());
					
					sql = "update "+tempTable+" a, " +
						" ( " +
						" 	select tokenid,sName,max(dataname)as dataname from "+tempTable+" " +
						" 	group by tokenid,sName " +
						" 	having count(*)>1 " +
						" )b " +
						" set unitsign=0 " +
						" where a.tokenid=b.tokenid  and a.sName = b.sName and a.dataname <> b.dataname " ;
					
//					////System.out.println(sql);
					
					st.execute(sql);
					System.out.println("9:"+CHF.getCurrentTime());
					
				}
				sql = "update "+tempTable+" set unitsign=1 where unitsign is null ";
				st.execute(sql);
				
				iCount++;
			}
			
			/**
			 * 计算并更新
			 */
//			开始计算
			String sql1 = "";
			sql="";
			allfield=newallfield;
			Year=newYear;
			Month=newMonth;
			
			//System.out.println("qwh:Year="+Year+"|allfield="+allfield+"|Month="+Month);
			
			args=newargs;
			
			String[] fields=allfield.split("`");
			
			GuideLineProperty[] gps=null,gps1=null;
			if (fields.length>0){
				gps1=new GuideLineProperty[fields.length];
			}
			int iGps=0;
			
			String orderby = CHF.showNull((String)args.get("排序"));
			
			sql="select fieldvalue,evalue,groupid,INSTR(concat('`','"+allfield+"','`'),concat('`',fieldvalue,'`'))>0 as d1 ,orderid from k_areafunctionfields \n"	+
				"where areaid=9999 and typeid=0 \n"	+
				"and ifnull(groupid,'') <> ''  \n"	+
				"order by groupid,orderid";
			rs=st.executeQuery(sql);
			System.out.println("a1:"+CHF.getCurrentTime());
			
			sql="";
			while (rs.next()){
				sql+= ", add column "+rs.getString("evalue")+" decimal (15,2) DEFAULT '0.00' ";
				
				if(allfield.indexOf(rs.getString("fieldvalue"))>-1){
					sql1 = " sum(" + rs.getString("evalue")+") ";
				}
				//追加fields
				if (rs.getInt("d1")>0){
					gps1[iGps++]=new GuideLineProperty(rs.getString("evalue"),rs.getString("groupid"));
				}
				
				if (!"".equals(orderby) && rs.getString("fieldvalue").equals(orderby)){
					orderby=" order by "+rs.getString("evalue")+" desc ";
				}
				
			}
			DbUtil.close(rs);
			if (!sql1.equals("")){
				//增加字段列
				st.execute("alter table " + tempTable + sql.substring(1) + " , change sid sid varchar (300)  NULL ");
				
				st.execute("alter table " + tempTable + " add index sName (sName),add index tokenid (tokenid),add index dataname (dataname),add index sid (sid)");
			}
			System.out.println("a2:"+CHF.getCurrentTime());
			
//			去掉多余的null数组单元
			if (iGps>0){
				gps=new GuideLineProperty[iGps];
				for (int i=0;i<iGps;i++){
					gps[i]=gps1[i];
				}
			}
			
//			分组排序
			if (iGps>0){
				java.util.Arrays.sort(gps,new   java.util.Comparator(){
			        public   int   compare(Object   obj1,Object   obj2){
				        String   s1   =   ((GuideLineProperty)obj1).group,
				        		 s2   =   ((GuideLineProperty)obj2).group;
				        return   s1.compareTo(s2);     //不就完了？
			        }
			    });
			}
			String oldGroup="";
			int iGroupFields=0,iInnerFields=0;
			String strGroupFields[][]=new String[iGps][];
			String temp[]=new String[iGps];
			
			GuideLineProperty gp=null;
			String strGroup[]=null;
			
			if (iGps>0){
				strGroup=new String[iGps];
			
				gp=gps[0];
				oldGroup=gp.group;
				temp[iInnerFields++]=gp.field;
				for(int i=1; i<iGps; i++){
					gp=gps[i];
					
					if (gp.group.indexOf(oldGroup)>=0){
						//是一个分组的，继续追加
						temp[iInnerFields++]=gp.field;
					}else{
						//更新分组
						String temp1[]=new String[iInnerFields];
						for (int j=0;j<iInnerFields;j++){
							temp1[j]=temp[j];
						}
						strGroupFields[iGroupFields]=temp1;
						strGroup[iGroupFields++]=oldGroup;
						         
						//重置中间变量
						oldGroup=gp.group;
						iInnerFields=0;
						temp =new String[iGps];
						temp[iInnerFields++]=gp.field;
					}
				}
				if (gp.group.indexOf(oldGroup)>=0){
					//更新分组
					strGroupFields[iGroupFields]=temp;
					strGroup[iGroupFields++]=oldGroup;
				}
			
				String [][] strGroupFields1 = new String [strGroupFields.length][];
				for(int i=0;i<strGroupFields1.length;i++){
					if(strGroupFields[i] != null){
						
						////System.out.println("strGroup="+strGroup[i]);
						
						strGroupFields1[i] = new String [strGroupFields[i].length] ;
						for(int j=0;j<strGroupFields1[i].length;j++){
							if(strGroupFields[i][j]!= null)
								strGroupFields1[i][j] = strGroupFields[i][j];	
							
							
							////System.out.println("strGroupFields1="+strGroupFields[i][j]);
							
							
						}
					}
				}
				
				System.out.println("a4:"+CHF.getCurrentTime());
				setBatchProjectValue(project.getProjectId(),Year,Month,tempTable,strGroupFields,strGroup,args);
				System.out.println("a5:"+CHF.getCurrentTime());
//				//System.out.println( iCount + "|" + allfield + "|" + SubjectFullName + "|" + myparm + "|" + autoid + "|" + curgroup);
			}
			
			
			//更新表结果
			sql="update "+tempTable1+" a,(select myautoid,"+sql1+" as myvalue from "+tempTable+" group  by myautoid )b \n"
				+" set a.myvalue=b.myvalue where a.autoid=b.myautoid";
			st.execute(sql);
			System.out.println("a7:"+CHF.getCurrentTime());
			
			/**
			 * 删除表 
			 */
			sql = "drop table if EXISTS " + tempTable;
			st.execute(sql);
			
		}catch(Exception e){
			e.printStackTrace();
			//System.out.println(sql);
		}finally{
			DbUtil.close(rs);
			DbUtil.close(st);
		}
		
		
		
		
	}
	
	/**
	 * 静态指标解释
	 * @param projectID
	 * @param Year
	 * @param Month
	 * @param DataName
	 * @param SubjectFullName
	 * @param AssTotalName
	 * @param allfield
	 * @param project
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public String getObject(String projectID, String Year, String Month,
			String DataName, String SubjectFullName, String AssTotalName,
			String allfield,Project project,Map args) throws Exception {
		Statement st = null;
		ResultSet rs = null;
		try {
			st = myConn.createStatement(); 
			String tempTable = "tt_"+DELUnid.getCharUnid();
			String sql = "",strResult = "";
			
			rs = getObject(tempTable, projectID,  Year,  Month,
					 DataName,  SubjectFullName,  AssTotalName,
					 allfield, project, args);
			
			if(rs.next()){
				strResult = new java.text.DecimalFormat("0.00").format(rs.getDouble(1));
			}
			
			sql = "drop table if EXISTS " + tempTable;
			st.execute(sql);
			return strResult;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(st);
		}
		
		
	}
	
	public ResultSet getObject(String tempTable ,String projectID, String Year, String Month,
			String DataName, String SubjectFullName, String AssTotalName,
			String allfield,Project project,Map args) throws Exception {
//		PreparedStatement ps = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			
			String strW = "";
			
			ASFuntion CHF=new ASFuntion();
			
			st = myConn.createStatement(); 
			
			if("".equals(DataName.trim()) || "0".equals(DataName) || "本位币".equals(DataName)) {
				DataName = "0";
			}else{
				strW += " and DataName = '"+DataName+"'";
			}
			
			args.put("project", project);
			
			GuideLine guideLine = new GuideLine();
			guideLine.setProperty(new String[]{allfield});
			
//			String strStartMonth=(String)args.get("起始月");
//			String strEndMonth=(String)args.get("结束月");
			
			if("".equals(Month)){
				Month = "0";
			}
			
			String begin = "".equals(project.getAuditTimeBegin()) ? "0" : project.getAuditTimeBegin().substring(0,4);
			String end = "".equals(project.getAuditTimeEnd()) ? "0" : project.getAuditTimeEnd().substring(0,4);
			String bMonth = "".equals(project.getAuditTimeBegin()) ? "0" : project.getAuditTimeBegin().substring(5,7);
			String eMonth = "".equals(project.getAuditTimeEnd()) ? "0" : project.getAuditTimeEnd().substring(5,7);
			
			String strStartYearMonth="",strEndYearMonth="";
			
			strStartYearMonth = String.valueOf(Integer.parseInt(begin)*12+Integer.parseInt(bMonth));
			strEndYearMonth = String.valueOf(Integer.parseInt(end)*12+Integer.parseInt(eMonth));
			
			
			int iYearMonthArea=Integer.parseInt(strEndYearMonth)-Integer.parseInt(strStartYearMonth) + 1;
			if (Integer.parseInt(Year)>0){
				//往明年、后年比较，扩展期末
				strEndYearMonth = String.valueOf(Integer.parseInt(strEndYearMonth) + iYearMonthArea * Integer.parseInt(Year));
			}else if (Integer.parseInt(Year)<0){
				//往去年、前年比较，扩展期初
				strStartYearMonth = String.valueOf(Integer.parseInt(strStartYearMonth) + iYearMonthArea * Integer.parseInt(Year));
			}
			
			String acc = String.valueOf(Integer.parseInt(project.getAccPackageId()) + Integer.parseInt(Year));
			
			String[] result = getClientIDAndDirectionByStandName(myConn,acc, project.getProjectId(),SubjectFullName);
            String subjectid = result[0];
            
            String isCurname = "";
			if (allfield.indexOf("原币")>-1){
				isCurname="支持";
				args.put("支持外币", "支持");
			}
			String isUnitName = "";
			if (allfield.indexOf("数量")>-1){
				isUnitName="支持";
				args.put("支持数量", "支持");
			}
			
			String isAssItem = CHF.showNull((String)args.get("包含核算"));
			String AssItem = CHF.showNull((String)args.get("核算名称"));
            
			if("".equals(AssTotalName)){ 	//科目指标
				args.put("包含核算","否");
			}else{							//核算指标
//				args.put("包含核算","否");
			}
			
			String strSelectSql = getRuleSQL(SubjectFullName, strStartYearMonth, strEndYearMonth, subjectid, end, args);
			////System.out.println(strSelectSql);
			
			
			String sql = "create table " + tempTable + " " + strSelectSql;
			st.execute(sql);
			
			sql="alter table "+tempTable+" add column unitname varchar (20)  , add column unitsign varchar (20)  ";
			st.execute(sql);
			
			if ("支持".equals(isCurname) && "支持".equals(isUnitName)){
				
				args.put("外币数量", "都要");
				
				strW += " and (DataName = '"+DataName+"' or unitname = '"+DataName+"' )";
				
				strSelectSql = CHF.replaceStr(strSelectSql, "and a.accsign=1", "and a.accsign=2");
				
				sql = "update "+tempTable+" a ,(" +
					strSelectSql +
					" ) b set a.unitname = if(b.dataname='本位币','',b.dataname)  where a.tokenid=b.tokenid and a.issubject=b.issubject and a.sName = b.sName";
				
//				////System.out.println(sql);
				st.execute(sql);
				
				sql = "update "+tempTable+" a, " +
					" ( " +
					" 	select tokenid,sName,max(dataname)as dataname from "+tempTable+" " +
					" 	group by tokenid,sName " +
					" 	having count(*)>1 " +
					" )b " +
					" set unitsign=0 " +
					" where a.tokenid=b.tokenid  and a.sName = b.sName and a.dataname <> b.dataname " ;
				
//				////System.out.println(sql);
				
				st.execute(sql);
				
			}
			sql = "update "+tempTable+" set unitsign=1 where unitsign is null ";
			st.execute(sql);
			
//			临时表追加字段，并完成fields等的翻译；
			String sql1 = "";
			sql="";
			
			String[] fields=allfield.split("`");
			
			GuideLineProperty[] gps=null,gps1=null;
			if (fields.length>0){
				gps1=new GuideLineProperty[fields.length];
			}
			int iGps=0;
			
			String orderby = CHF.showNull((String)args.get("排序"));
			
			sql="select fieldvalue,evalue,groupid,INSTR(concat('`','"+allfield+"','`'),concat('`',fieldvalue,'`'))>0 as d1 ,orderid from k_areafunctionfields \n"	+
				"where areaid=9999 and typeid=0 \n"	+
				"and ifnull(groupid,'') <> ''  \n"	+
				"order by groupid,orderid";
			rs=st.executeQuery(sql);
			sql="";
			while (rs.next()){
				sql+= ", add column "+rs.getString("evalue")+" decimal (15,2) DEFAULT '0.00' ";
				
				if(allfield.indexOf(rs.getString("fieldvalue"))>-1){
					sql1+= " sum(" + rs.getString("evalue")+") as '" +rs.getString("fieldvalue")+"',";
				}
				//追加fields
				if (rs.getInt("d1")>0){
					gps1[iGps++]=new GuideLineProperty(rs.getString("evalue"),rs.getString("groupid"));
				}
				
				if (!"".equals(orderby) && rs.getString("fieldvalue").equals(orderby)){
					orderby=" order by "+rs.getString("evalue")+" desc ";
				}
				
			}
			DbUtil.close(rs);
			if (!sql1.equals("")){
				//增加字段列
				st.execute("alter table " + tempTable + sql.substring(1) + " , change sid sid varchar (300)  NULL ");
				
				st.execute("alter table " + tempTable + " add index sName (sName),add index tokenid (tokenid),add index dataname (dataname),add index sid (sid)");
			}
			
//			去掉多余的null数组单元
			if (iGps>0){
				gps=new GuideLineProperty[iGps];
				for (int i=0;i<iGps;i++){
					gps[i]=gps1[i];
				}
			}
			
//			分组排序
			if (iGps>0){
				java.util.Arrays.sort(gps,new   java.util.Comparator(){
			        public   int   compare(Object   obj1,Object   obj2){
				        String   s1   =   ((GuideLineProperty)obj1).group,
				        		 s2   =   ((GuideLineProperty)obj2).group;
				        return   s1.compareTo(s2);     //不就完了？
			        }
			    });
			}
			String oldGroup="";
			int iGroupFields=0,iInnerFields=0;
			String strGroupFields[][]=new String[iGps][];
			String temp[]=new String[iGps];
			
			GuideLineProperty gp=null;
			String strGroup[]=null;
			
			if (iGps>0){
				strGroup=new String[iGps];
			
				gp=gps[0];
				oldGroup=gp.group;
				temp[iInnerFields++]=gp.field;
				for(int i=1; i<iGps; i++){
					gp=gps[i];
					
					if (gp.group.indexOf(oldGroup)>=0){
						//是一个分组的，继续追加
						temp[iInnerFields++]=gp.field;
					}else{
						//更新分组
						String temp1[]=new String[iInnerFields];
						for (int j=0;j<iInnerFields;j++){
							temp1[j]=temp[j];
						}
						strGroupFields[iGroupFields]=temp1;
						strGroup[iGroupFields++]=oldGroup;
						         
						//重置中间变量
						oldGroup=gp.group;
						iInnerFields=0;
						temp =new String[iGps];
						temp[iInnerFields++]=gp.field;
					}
				}
				if (gp.group.indexOf(oldGroup)>=0){
					//更新分组
					strGroupFields[iGroupFields]=temp;
					strGroup[iGroupFields++]=oldGroup;
				}
			
				String [][] strGroupFields1 = new String [strGroupFields.length][];
				for(int i=0;i<strGroupFields1.length;i++){
					if(strGroupFields[i] != null){
						
						////System.out.println("strGroup="+strGroup[i]);
						
						strGroupFields1[i] = new String [strGroupFields[i].length] ;
						for(int j=0;j<strGroupFields1[i].length;j++){
							if(strGroupFields[i][j]!= null)
								strGroupFields1[i][j] = strGroupFields[i][j];	
							
							
							////System.out.println("strGroupFields1="+strGroupFields[i][j]);
							
							
						}
					}
				}
				
				
				setBatchProjectValue(project.getProjectId(),Year,Month,tempTable,strGroupFields,strGroup,args);
				
			}
			
			String strResult = "0.00";
			
			sql = "select " + sql1 + " 0 from "+ tempTable + " where 1=1 " + strW;
			////System.out.println(sql);
			rs = st.executeQuery(sql);
//			if(rs.next()){
//				strResult = new java.text.DecimalFormat("0.00").format(rs.getDouble(1));
//			}
//			
//			sql = "drop table if EXISTS " + tempTable;
//			st.execute(sql);
			
			return rs;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} 
		
	}
	

	
	/**
	 * 动态指标解释
	 * @param projectID
	 * @param Year
	 * @param Month
	 * @param DataName
	 * @param subjectName	是指定的科目名
	 * @param tokenid		是指定的科目名的下级科目或核算
	 * @param sName			是核算时的科目tokenid		
	 * @param property
	 * @param project
	 * @param args
	 * @param isSubject
	 * @return
	 * @throws Exception
	 */
	public String[] getObject(String projectID, String Year, String Month,
			String DataName,String subjectName, String tokenid,String sName,
			String[] propertys,String propertyGroup, Project project,Map args,String isSubject) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			if("".equals(DataName.trim()) || "0".equals(DataName) || "本位币".equals(DataName)) DataName = "0";
			
			args.put("project", project);
			
			GuideLine guideLine = new GuideLine();
			guideLine.setProperty(propertys); 
			guideLine.setGroup(propertyGroup);
			
			String acc = ""; 
			if("".equals(Year)){
				acc = project.getAccPackageId();			
			}else{
				acc = String.valueOf(Integer.parseInt(project.getAccPackageId()) + Integer.parseInt(Year));
			}
			
			String[] result = getClientIDAndDirectionByStandName(myConn,acc, project.getProjectId(),subjectName);
            String subjectid = result[0];
            
			String sql = "";
			if("科目".equals(isSubject)){
				
				sql = " select distinct subjectid,'' as assitemid " +
				" from c_account a " +   
				" where 1=1    " +
				" and accpackageid ="+acc+" " +
				" and (a.subjectfullname2 like '"+subjectName+"/%'  or a.subjectfullname2 = '"+subjectName+"' ) " +  
				" and a.tokenid = '"+tokenid+"' " +
				" union  " +
				" select subjectid,'' as assitemid " +
				" from z_usesubject  " +
				" where projectid="+project.getProjectId()+" " + 
				" and tipsubjectid='"+subjectid+"'  " +
				" and isleaf=1  ";
				
			}else{
				
				sql = "select distinct accid,assitemid " +
				" from c_assitementryacc a,(  " +
				" 	select distinct subjectid " +
				" 	from c_account a " +
				" 	where 1=1 " +
				" 	and accpackageid ="+acc+" " +
				" 	and (a.subjectfullname2 like '"+subjectName+"/%'  or a.subjectfullname2 = '"+subjectName+"' )  " + 
				" 	and a.tokenid = '"+sName+"' " +
				" ) b  " +
				" where 1= 1 " +
				" and accpackageid ="+acc+" and accid = subjectid  " +
				" and AssTotalName1 = '"+tokenid+"'";
				
			}
			ps = myConn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				guideLine.setSubjectID(rs.getString(1));
				guideLine.setAssitemID(rs.getString(2));
			}else{
				guideLine.setSubjectID("");
				guideLine.setAssitemID("");
			}
			
			return getProjectValue(projectID,Year,Month,DataName,guideLine,args);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
	}

	
	/**
	 * 静态指标行定义
	 * 
	 * 1.指定一个科目名称,取指定科目的所有叶子节点(含新增科目,而且历年科目的话,如果没有做连续性,就每个科目一条,如果连续性了,则设置了连续性的要以最后一年的为准),返回科目名称和科目编号;
	 * 2.指定一个科目名称,取指定科目的二级(没有二级就是一级)的科目名称和科目编号;
	 * 3.指定一个科目名称和辅助核算类型(可以是多个,还可以是扫描所有核算),如果该科目有辅助核算,就取辅助核算下级,没有,就取科目下级(科目下级取法同方式1);
	 *    这个方式3其实就是3007的取数方法.
	 * 4.交叉取,提供一个科目,一个对方科目,取他们的叶子科目节点,交替出现;
	 * 
	 * 前提
	 * 科目名称
	 * 对方科目(可以为空)
	 * 
	 * A、包含核算	包含(有核算显示核算，无核算显示科目)；不包含(只显示科目)
	 * B、核算名称	空(所有核算类型)，不为空(指定核算类型)，要含 A
	 * C、关联客户	所有客户、关联客户、非关联客户
	 * D、支持外币	空、显示外币
	 * E、科目类型	末级(一级、二级)
	 * 
	 * 特殊参数：显示行数(默认全部)
	 * 
	 * 科目名称 - 对方科目，得到 4
	 * 科目名称 - 核算名称(关联客户/支持外币)，得到 3
	 * 科目名称 - 包含核算(科目类型) 得到 1、2
	 * 
	 * 返回：ResultSet
	 *  科目编号 subjectid/辅助核算编号 assitemid /科目或辅助核算名称(带科目名称) cname /币种 : curname
	 */
	public ResultSet getRuleSubjectName(Map args) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try {
			ASFuntion CHF=new ASFuntion();
			Project project = (Project) args.get("project");
			
			String acc = project.getAccPackageId();
			

			/**
			 * 特殊参数：显示行数(默认全部)
			 */
//			String limit = CHF.showNull((String)args.get("显示行数"));
			String sqlLimit = "";
//			if(limit != null && !"".equals(limit.trim())){
//				sqlLimit = "limit 0, " + limit;
//			}
			
			String strStartYear=(String)args.get("起始年");
			String strStartMonth=(String)args.get("起始月");
			String strEndYear=(String)args.get("结束年");
			String strEndMonth=(String)args.get("结束月");
			
			String Year= CHF.showNull((String)args.get("年度"));
			String Month= CHF.showNull((String)args.get("月份"));
			if("".equals(Month)){
				Month = "0";
			}
			
			String begin = "".equals(project.getAuditTimeBegin()) ? "0" : project.getAuditTimeBegin().substring(0,4);
			String end = "".equals(project.getAuditTimeEnd()) ? "0" : project.getAuditTimeEnd().substring(0,4);
			String bMonth = "".equals(project.getAuditTimeBegin()) ? "0" : project.getAuditTimeBegin().substring(5,7);
			String eMonth = "".equals(project.getAuditTimeEnd()) ? "0" : project.getAuditTimeEnd().substring(5,7);
			
			String strStartYearMonth="",strEndYearMonth="";
			
			int iCheckParam=0;
			if (strStartYear!=null && !strStartYear.equals("")){
				iCheckParam++;
			}
			if (strEndYear!=null && !strEndYear.equals("")){
				iCheckParam++;
			}
			if (strStartMonth!=null && !strStartMonth.equals("")){
				iCheckParam++;
			}
			if (strEndMonth!=null && !strEndMonth.equals("")){
				iCheckParam++;
			}
			
			if (iCheckParam==4){
				strStartYearMonth=String.valueOf((Integer.parseInt(begin)+Integer.parseInt(strStartYear))*12+Integer.parseInt(strStartMonth));
				strEndYearMonth	 =String.valueOf((Integer.parseInt(end)+Integer.parseInt(strEndYear))*12+Integer.parseInt(strEndMonth));
				
			}else if (iCheckParam!=0){
				throw new Exception("请完整提供起始年、起始月、结束年、结束月4个参数！");
			}else{
				//是按项目取数
				if("".equals(Month) || "0".equals(Month) || "00".equals(Month)){
					if("".equals(Year)){
						strStartYearMonth = String.valueOf(Integer.parseInt(begin)*12+Integer.parseInt(bMonth));
						strEndYearMonth = String.valueOf(Integer.parseInt(end)*12+Integer.parseInt(eMonth));
						Year = "0";
					}else{
						int pyear = Integer.parseInt(end) + Integer.parseInt(Year);
						int beginMonth =  (pyear) * 12 + 1;
						int endMonth =  (pyear) * 12 + 12;
						
						int projectbegin = Integer.parseInt(begin)*12+Integer.parseInt(bMonth);
						int projectend = Integer.parseInt(end)*12+Integer.parseInt(eMonth);
						
						if( Integer.parseInt(begin) > pyear || Integer.parseInt(end) < pyear) throw new Exception("查询年度超出审计年度，请重新输入!!");
						
						for(int i= Integer.parseInt(begin); i<=Integer.parseInt(end) ;i++ ){
							if(pyear == i){
								if(projectend < endMonth){
									strEndYearMonth = String.valueOf(projectend);
								}else{
									strEndYearMonth = String.valueOf(endMonth);
								}
								if(projectbegin < beginMonth){
									strStartYearMonth = String.valueOf(beginMonth);
								}else{
									strStartYearMonth = String.valueOf(projectbegin);
								}
							}
						}
						
					}
					
				}else{
					if("".equals(Year)) throw new Exception("请提供“年度”!");
					strStartYearMonth = String.valueOf((Integer.parseInt(end) + Integer.parseInt(Year) )*12+Integer.parseInt(Month));
					strEndYearMonth = String.valueOf((Integer.parseInt(end) + Integer.parseInt(Year) )*12+Integer.parseInt(Month)); 
				}
			}
			
			String SubjectName = CHF.showNull((String)args.get("科目名称"));		//科目名称
			if (SubjectName==null || SubjectName.equals("")){
                String manuid = CHF.showNull((String)args.get("manuid"));
                if (manuid==null || manuid.equals("")){
                    SubjectName=getTaskSubjectNameByTaskCode(myConn,project.getProjectId(),(String)args.get("curTaskCode"));
                }else{
                    //如果科目名称为空，则通过前台提交的刷新底稿编号去取得对应任务得科目名称；
                    SubjectName = getTaskSubjectNameByManuID(myConn, manuid);
                }
            }

            String sName = changeSubjectName(myConn,project.getProjectId(),SubjectName);
            if(!"".equals(sName)){
            	SubjectName = sName; 
            }
            if("".equals(SubjectName)) throw new Exception("请输入科目名称！");
			
			String otherSubjectName = CHF.showNull((String)args.get("对方科目"));	//对方科目
			//有对方科目
			if(!"".equals(otherSubjectName)){		
				sql = "select subjectname,tokenid,DataName,isSubject,tokenid as sName, direction2,sid,	 recsign 	 " +
				"\n FROM " +
				"\n ( " +
				"\n select subjectname,tokenid,DataName,isSubject,direction2,group_concat(sid) as sid,min(recsign) as recsign,orderid from (" +
				"\n	select distinct if(a.level1 = 1, a.tokenid,INSERT(a.tokenid,1,char_length(b.tokenid) +1 ,''))  as subjectname,a.tokenid," +
				"\n if(a.DataName='0','本位币',a.DataName) as DataName,'科目' as isSubject ," +
				"\n direction2,concat(subyearmonth,'`',subjectid) sid, " +
				"\n if(exists(select 1 from z_usesubject c where c.projectid='"+project.getProjectId()+"' and a.isleaf1=1 and c.accpackageid=a.accpackageid and (c.subjectfullname = a.subjectfullname1 or c.subjectfullname like concat(a.subjectfullname1,'/%'))),0,1) as recsign ," +
				"\n '1' as orderid " +
				"\n	from c_account a ,(" +
				"\n		select distinct accpackageid,tokenid  from c_account a " +
				"\n		where 1=1" +
				"\n		and SubYearMonth*12+SubMonth>='"+strStartYearMonth+"' and SubYearMonth*12+SubMonth<='"+strEndYearMonth+"' " +
				"\n		and (subjectfullname2 like '"+SubjectName+"/%' or subjectfullname2 = '"+SubjectName+"')" +
				"\n		and level1 = 1" +
				"\n	) b " +
				"\n	where 1=1 " +
				"\n	and SubYearMonth*12+SubMonth>='"+strStartYearMonth+"' and SubYearMonth*12+SubMonth<='"+strEndYearMonth+"' " +
				"\n	and (subjectfullname2 like '"+SubjectName+"/%' or subjectfullname2 = '"+SubjectName+"') and isleaf1=1  " +
				"\n	and a.accpackageid = b.accpackageid and (a.tokenid like concat(b.tokenid,'/%') or a.tokenid = b.tokenid)" +
				"\n ) a group by subjectname,tokenid,DataName,isSubject,direction2 " +
				"\n	union all " +
				"\n select subjectname,tokenid,DataName,isSubject,direction2,group_concat(sid) as sid,min(recsign) as recsign,orderid from (" +
				"\n	select distinct if(a.level1 = 1, a.tokenid,INSERT(a.tokenid,1,char_length(b.tokenid) +1 ,''))  as subjectname, a.tokenid," +
				"\n if(a.DataName='0','本位币',a.DataName) as DataName,'科目' as isSubject ," +
				"\n direction2,concat(subyearmonth,'`',subjectid) sid, " +
				"\n if(exists(select 1 from z_usesubject c where c.projectid='"+project.getProjectId()+"' and a.isleaf1=1 and c.accpackageid=a.accpackageid and (c.subjectfullname = a.subjectfullname1 or c.subjectfullname like concat(a.subjectfullname1,'/%'))),0,1) as recsign ," +
				"\n '2' as orderid" +
				"\n	from c_account a ,(" +
				"\n		select distinct accpackageid,tokenid  from c_account a " +
				"\n		where 1=1" +
				"\n		and SubYearMonth*12+SubMonth>='"+strStartYearMonth+"' and SubYearMonth*12+SubMonth<='"+strEndYearMonth+"' " +
				"\n		and (subjectfullname2 like '"+otherSubjectName+"/%' or subjectfullname2 = '"+otherSubjectName+"')" +
				"\n		and level1 = 1" +
				"\n	) b " +
				"\n	where 1=1 " +
				"\n	and SubYearMonth*12+SubMonth>='"+strStartYearMonth+"' and SubYearMonth*12+SubMonth<='"+strEndYearMonth+"' " +
				"\n	and (subjectfullname2 like '"+otherSubjectName+"/%' or subjectfullname2 = '"+otherSubjectName+"') and isleaf1=1  " +
				"\n	and a.accpackageid = b.accpackageid and (a.tokenid like concat(b.tokenid,'/%') or a.tokenid = b.tokenid)" +
				"\n ) a group by subjectname,tokenid,DataName,isSubject,direction2 " +
				"\n ) a  order by subjectname,orderid " + sqlLimit;
				
//				////System.out.println(sql);
				ps = myConn.prepareStatement(sql);
				rs = ps.executeQuery();
				return rs;
			}
			
			/**
			 * 没有对方科目
			 */
			
			String[] result = getClientIDAndDirectionByStandName(myConn,acc, project.getProjectId(),SubjectName);
            String subjectid = result[0];
            
			sql = this.getRuleSQL(SubjectName, strStartYearMonth, strEndYearMonth, subjectid, end, args);
			////System.out.println("SQL:" + sql);
			ps = myConn.prepareStatement(sql);
			rs = ps.executeQuery();
			return rs;
			
		} catch (Exception e) {
			////System.out.println("ERROR SQL:" + sql);
			e.printStackTrace();
			DbUtil.close(rs);
			DbUtil.close(ps);
			throw e;
		} 
	}
	
	
	/**
	 * 指标列SQL
	 * 本位币、外币、数量的取决依据：
	 * 只要有外币，本行就以外币为准；
	 * 否则，只要有数量，本行就以数量为准；
	 * 否则，本位币；
	 * @param SubjectName
	 * @param strStartYearMonth
	 * @param strEndYearMonth
	 * @param subjectid
	 * @param end
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public String getRuleSQL(String SubjectName,String strStartYearMonth,String strEndYearMonth,String subjectid1,String end,Map args) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "";
			ASFuntion CHF=new ASFuntion();
			
			Project project = (Project) args.get("project");
			
			String isAssItem = CHF.showNull((String)args.get("包含核算"));
			String AssItem = CHF.showNull((String)args.get("核算名称"));
			
			String correlation = CHF.showNull((String)args.get("关联客户")); //所有客户、关联客户、其它客户
			correlation = CHF.replaceStr(correlation, "他",	"它");
			
			String isCurname = CHF.showNull((String)args.get("支持外币"));	//支持
			String isUnitName = CHF.showNull((String)args.get("支持数量"));	//支持
			String strAccsign="";
			if ("支持".equals(isCurname)){
				strAccsign=" and a.accsign=1";
			}else if ("支持".equals(isUnitName)){
				strAccsign=" and a.accsign=2";
			}
			
			/**
			 * 例：
			 * 1、科目类型=末级&显示全部科目=是 【表示显示所有末级(包含所有期初、发生为0)的科目】
			 * 2、科目类型=二级&显示全部科目=是 【表示显示所有二级(包含所有期初、发生为0)的科目】
			 */
			String subejctType = CHF.showNull((String)args.get("科目类型"));// 末级(一级、二级)
			String subejctAll = CHF.showNull((String)args.get("显示全部科目"));//显示本级所有全部科目，包含所有期初、发生为0的科目
			String sqlSubject = "";
			
			/**
			 * 设置最小的层次
			 */
			sql = "select level1 " +
			" from c_account a" +
			" where 1=1" +
			" and a.subyearmonth*12+a.submonth>='"+strStartYearMonth+"' and a.subyearmonth*12+a.submonth<='"+strEndYearMonth+"'  \n" +
			" and (a.subjectfullname2 like '"+SubjectName+"/%'  or a.subjectfullname2 = '"+SubjectName+"' )  \n" + 
			" order by level1 limit 1 ";
			ps = myConn.prepareStatement(sql);
			rs = ps.executeQuery();
			int level1 = 1;
			if(rs.next()){
				level1 = rs.getInt(1);
			}
			DbUtil.close(rs);
			DbUtil.close(ps); 
			sql = "";
			
			String sqlType = ""; 
			String sqlType1 = "";
			if(subejctType == null || "".equals(subejctType.trim()) || "末级".equals(subejctType)){
				sqlType = " and a.isleaf1 = 1  \n";
				sqlType1 = " and isleaf = 1  \n";
			}else if("一级".equals(subejctType)) {
				sqlType = " and a.level1 = "+level1+"  \n";
				sqlType1 = " and level0 = "+level1+"  \n";
			}else if("二级".equals(subejctType)) {
				sqlType = " and ((a.level1="+level1+" and a.isleaf1=1) " +
						" or (a.level1="+level1+" +1) " +
						" or (a.subjectfullname2 = '"+SubjectName+"' and a.isleaf1=1) )  \n";
				sqlType1 = " and ( (level0="+level1+" and isleaf=1) or  level0="+level1+" +1 )  \n";
			}else if("三级".equals(subejctType)) {
				sqlType = " and ((a.level1="+level1+" and a.isleaf1=1) " +
				" or (a.level1="+level1+" +2) " +
				" or (a.subjectfullname2 = '"+SubjectName+"' and a.isleaf1=1) )  \n";
				sqlType1 = " and ( (level0="+level1+" and isleaf=1) or  level0="+level1+" +2 )  \n";
			}else if("全部".equals(subejctType)) {
				sqlType = " ";
				sqlType1 = " ";
			}
			
			//显示本级所有全部科目，包含所有期初、发生为0的科目
			if("是".equals(subejctAll)){
				sqlSubject = "	union " +
				"	SELECT  DISTINCT b.subjectname AS subjectname, \n" +
				"	b.subjectfullname AS subjectfullname1,  \n" +
				"	b.subjectfullname AS tokenid, \n" +
				"	IF(IFNULL(a.DataName,'0')='0','本位币',a.DataName) AS DataName, \n" +
				"	CASE SUBSTRING(property,2,1) WHEN 1 THEN 1 WHEN 2 THEN -1 END  AS direction2, \n" +        
				"	CONCAT(SUBSTRING(b.accpackageid,7),'`',b.subjectid) sid,  \n" +
				"	IF(EXISTS(SELECT 1 FROM z_usesubject c WHERE c.projectid='"+project.getProjectId()+"' AND b.isleaf=1 AND c.accpackageid=b.accpackageid AND (c.subjectfullname = b.subjectfullname OR c.subjectfullname LIKE CONCAT(b.subjectfullname,'/%'))),0,1) AS recsign \n" +  
				"	FROM c_account a   \n" +
				"	RIGHT JOIN c_accpkgsubject b ON a.accpackageid = b.accpackageid AND a.subjectid = b.subjectid \n" +
				"	AND a.subyearmonth*12+a.submonth>='"+strStartYearMonth+"' AND a.subyearmonth*12+a.submonth<='"+strEndYearMonth+"'  \n" +
				"	AND (a.subjectfullname2 LIKE '"+SubjectName+"/%' OR a.subjectfullname2 = '"+SubjectName+"')   \n" +
				"	INNER JOIN ( \n" +
				"		SELECT DISTINCT a.AccPackageID,a.subjectid,a.subjectfullname1,a.subjectfullname2 \n" +
				"		FROM c_account a,( \n" +
				"			SELECT DISTINCT AccPackageID,a.subjectid,a.subjectfullname1,a.subjectfullname2 \n" +
				"			FROM c_account a \n" +
				"			WHERE a.subyearmonth*12+a.submonth>='"+strStartYearMonth+"' AND a.subyearmonth*12+a.submonth<='"+strEndYearMonth+"' \n" + 
				"			AND (a.subjectfullname2 LIKE '"+SubjectName+"/%' OR a.subjectfullname2 = '"+SubjectName+"')   \n" +
				"		) b  \n" +
				"		WHERE a.subyearmonth*12+a.submonth>='"+strStartYearMonth+"' AND a.subyearmonth*12+a.submonth<='"+strEndYearMonth+"' \n" + 
				"		AND a.level1=1 \n" +
				"		AND a.AccPackageID = b.AccPackageID \n" +
				"		AND (b.subjectfullname2 LIKE CONCAT(a.subjectfullname2,'/%') OR b.subjectfullname2 = a.subjectfullname2) \n" +  
				"	) c ON b.accpackageid = c.accpackageid  \n" +
				"	AND (b.subjectfullname = c.subjectfullname1 OR b.subjectfullname LIKE CONCAT(c.subjectfullname1,'/%')) \n" +
				"	WHERE 1=1 \n" +
				"	AND a.accpackageid IS NULL " + sqlType1 ;
			}
			
			
			String sqlCustomer = "";	
			String sqlCustomer1 = "select group_concat(distinct \"'\",connectcompanysname,\"'\") from asdb.k_connectcompanys a where a.customerid="+project.getCustomerId()+"";
			ps = myConn.prepareStatement(sqlCustomer1);
			rs = ps.executeQuery();
			sqlCustomer1 = "";
			if(rs.next()){
				sqlCustomer1 = CHF.showNull(rs.getString(1));
			}
			if("".equals(sqlCustomer1)){
				sqlCustomer1 = "-1";
			}
			
			//所有客户、关联客户、其它客户
			if("关联客户".equals(correlation.trim())){
				sqlCustomer = "and ifnull(assitemname,subjectname) in ("+sqlCustomer1 +") ";
			}else if("其它客户".equals(correlation.trim())){
				sqlCustomer = "and ifnull(assitemname,subjectname) not in ("+sqlCustomer1 +") ";
			}
			
			String sqlCurName1 = "";
			String sqlCurName2 = "";
			if(!"".equals(strAccsign)){	//支持外币
				
				sqlCurName1 = "select  \n" +
				" distinct standname as fullName ,\n" +
				" a.subjectfullname1  as subjectname, \n" +
				" a.tokenid,if(a.DataName='0','本位币',a.DataName) as DataName ,'科目' as isSubject,a.tokenid as sName,direction2,group_concat(distinct a.subyearmonth,'`',a.subjectid) sid,  '1' as recsign  \n" +
				" from c_accountall a  \n" +
				" where 1=1  \n" + 
				" and a.subyearmonth*12+a.submonth>='"+strStartYearMonth+"' and a.subyearmonth*12+a.submonth<='"+strEndYearMonth+"'  \n" +
				" and (a.subjectfullname2 like '"+SubjectName+"/%'  or a.subjectfullname2 = '"+SubjectName+"' )  \n" + 
				sqlType + strAccsign +
				" group by a.tokenid ,DataName \n" ; 
				
				sqlCurName2 = "select   \n" +
				" assitemname as fullName,  \n" +
				" concat(subjectfullname1,'/',assitemname) as subjectname,  \n" +
				" tokenid as tid,AssTotalName1,if(a.DataName='0','本位币',a.DataName) as DataName,\n" +
				" '核算' as isSubject,tokenid as sName,direction2 ,  \n" +
				" group_concat(distinct subyearmonth,'`',accid,'`',assitemid) as sid,1  as recsign " +
				" from c_assitementryaccall a, ( \n" +
				"	select distinct accpackageid,subjectid,AccName,subjectfullname1,tokenid  from c_account a  \n" +  
				" 	where 1=1   \n" +
				" 	and subyearmonth*12+submonth>='"+strStartYearMonth+"' and subyearmonth*12+submonth<='"+strEndYearMonth+"'  \n" +
				" 	and (a.subjectfullname2 like '"+SubjectName+"/%'  or a.subjectfullname2 = '"+SubjectName+"' )  \n" +
				" ) b  \n" +
				" where 1=1  \n" +
				" and a.subyearmonth*12+a.submonth>='"+strStartYearMonth+"' and a.subyearmonth*12+a.submonth<='"+strEndYearMonth+"'   \n" +
				 strAccsign +
				" and a.accpackageid = b.accpackageid  \n" +
				" and a.accid = b.subjectid  \n" +
				" group by tokenid,AssTotalName1,DataName \n";
				
			}
			
			String sqlTable = ""; 
			
			//不包含核算	只显示科目
			if("否".equals(isAssItem) && "".equals(AssItem.trim())){
				
//				所有客户、关联客户、其它客户
				if("关联客户".equals(correlation.trim())){
					sqlCustomer = "and exists (select 1 from asdb.k_connectcompanys a where a.customerid="+project.getCustomerId()+" and a.connectcompanysname = fullName)";
				}else if("其它客户".equals(correlation.trim())){
					sqlCustomer = "and not exists (select 1 from asdb.k_connectcompanys a where a.customerid="+project.getCustomerId()+" and a.connectcompanysname = fullName)";
				}
				
				sqlTable = "select a.subjectname as fullName,a.subjectfullname1 as subjectname,a.tokenid,a.dataname,'科目' as isSubject,a.tokenid as sName,a.direction2,group_concat(sid) as sid,min(recsign) as recsign from( \n" +  
				" select distinct standname  as subjectname,a.subjectfullname1,a.tokenid,\n" +
				" if(a.DataName='0','本位币',a.DataName) as DataName,a.direction2,\n " +
				" concat(a.subyearmonth,'`',a.subjectid) sid,  \n" +
				" if(exists(select 1 from z_usesubject c where c.projectid='"+project.getProjectId()+"' and a.isleaf1=1 and c.accpackageid=a.accpackageid and (c.subjectfullname = a.subjectfullname1 or c.subjectfullname like concat(a.subjectfullname1,'/%'))),0,1) as recsign  \n" +
				" from c_account a \n" +
				" where 1=1 " + 
				" and a.subyearmonth*12+a.submonth>='"+strStartYearMonth+"' and a.subyearmonth*12+a.submonth<='"+strEndYearMonth+"' " +
				" and (a.subjectfullname2 like '"+SubjectName+"/%'  or a.subjectfullname2 = '"+SubjectName+"' ) " + 
				sqlType + 
				sqlSubject + 
				" ) a \n" +
				" group by a.tokenid,a.dataname,a.direction2 \n" +
				
				" union " +
				" select subjectName as fullName,subjectfullname as subjectName,subjectfullname,'本位币' ,'科目' as isSubject,subjectfullname as sName,case substring(property,2,1) when 1 then 1 when 2 then -1 end  direction2,concat("+end+",'`',subjectid) sid, 1 as recsign \n" +
				" from z_usesubject \n" +
				" where projectid="+project.getProjectId()+" \n" +
				" and tipsubjectid in (" +
				" 	select distinct subjectid from c_account a " +
				" 	where 1=1 " + 
				" 	and a.subyearmonth*12+a.submonth>='"+strStartYearMonth+"' and a.subyearmonth*12+a.submonth<='"+strEndYearMonth+"' " +
				"	and (a.subjectfullname2 like '"+SubjectName+"/%'  or a.subjectfullname2 = '"+SubjectName+"' ) and level1 = 1 " +
				" ) \n" + 
				sqlType1 + 
				" union " +
				" select subjectName as fullName,subjectfullname as subjectName,subjectfullname,'本位币' ,'科目' as isSubject,subjectfullname as sName,case substring(property,2,1) when 1 then 1 when 2 then -1 end  direction2,concat("+end+",'`',subjectid) sid, 1 as recsign \n" +
				" from z_usesubject a \n" +
				" where projectid="+project.getProjectId()+" \n" +
				" and (a.subjectfullname like '"+SubjectName+"/%'  or a.subjectfullname = '"+SubjectName+"' )  " +
				
				"  \n" +
				sqlType1  ; 
				
				if(!"".equals(strAccsign)){
					sqlTable = "select distinct " +
					" ifnull(b.fullName,a.fullName) as fullName,\n" +
					" ifnull(b.subjectname,a.subjectname) as subjectname,\n" +
					" ifnull(b.tokenid,a.tokenid) as tokenid,\n" +
					" ifnull(b.dataname,a.dataname) as dataname,\n" +
					" ifnull(b.isSubject,a.isSubject) as isSubject, \n" +
					" ifnull(b.sName,a.sName) as sName,\n" +
					" ifnull(b.direction2,a.direction2) as direction2, \n" +
					" ifnull(b.sid,a.sid) as sid, \n" +
					" ifnull(b.recsign,a.recsign) as recsign \n" +
					" from ( \n" +
					sqlTable +
					" ) a left join (\n" +
					sqlCurName1 + 
					" \n) b on a.tokenid = b.tokenid ";
				}
				
				sql = "select * from ("+sqlTable+") a " +
				" where 1=1 " +  sqlCustomer +
				" order by sid " ;
				return sql; 
			}
			
			//包含核算
			
			String sqlAssItem = "";
			if(!"".equals(AssItem.trim())){		//核算名称不为空,多个以“,”分隔
//				String acc = CHF.showNull((String) args.get("curAccPackageID"));
				String allYear = CHF.showNull((String)args.get("比较年份"));
				
				if("".equals(allYear)) {
					allYear = "0";
				}
				String accpackageid1 = project.getCustomerId() + (Integer.parseInt(project.getAuditTimeBegin().substring(0,4))+Integer.parseInt(allYear));
				String accpackageid2 = project.getCustomerId() + project.getAuditTimeEnd().substring(0,4); 
				
				String sqlStr = "select * from c_subjectassitem " +
						"	where 1=1 " +
						"	and accpackageid >='"+accpackageid1+"' " +
						"	and accpackageid <='"+accpackageid2+"' " +
						"	and property = 1 " +
						"	limit 1";
//				System.out.println("辅助核算111:"+sqlStr);
				ps = myConn.prepareStatement(sqlStr);
				rs = ps.executeQuery();
				boolean bool = true;
				if(rs.next()){
					//新的逻辑：与科目辅助核算披露设置相关联
					DbUtil.close(rs);
					DbUtil.close(ps);
					bool = false;
					
					sqlStr = "select distinct a.ifequal,a.subjectid,a.asstotalname1  " +
					" from c_subjectassitem a,c_account b " +
					" where 1=1 " +
					" and a.accpackageid >='"+accpackageid1+"' " +
					" and a.accpackageid <='"+accpackageid2+"'  " +
					
					" and b.accpackageid >='"+accpackageid1+"' " +
					" and b.accpackageid <='"+accpackageid2+"'  " +
					" and (b.subjectfullname2 = '"+SubjectName+"' or b.subjectfullname2 like '"+SubjectName+"/%') " +
					" and a.subjectid = b.subjectid " ;
//					System.out.println("辅助核算:"+sqlStr); 
					ps = myConn.prepareStatement(sqlStr);
					rs = ps.executeQuery();
					while(rs.next()){
						String ifequal = rs.getString("ifequal");
						String accid = rs.getString("subjectid");
						String asstotalname1 = rs.getString("asstotalname1");
						
						if("0".equals(ifequal)){ //有核算披露
							sqlAssItem += "or (a.accid = '"+accid+"' and a.asstotalname1 like '"+asstotalname1+"/%' ) ";
						}
					
					}
					
					if(!"".equals(sqlAssItem)){
				    	sqlAssItem = " and ( " + sqlAssItem.substring(2)+ ") ";
				    }else{
				    	sqlAssItem = " and 1=2 ";
				    }
					
				}
				DbUtil.close(rs);
				DbUtil.close(ps);
				
				if(bool){ 
					//原来的逻辑
					String [] strAssItem = AssItem.split(";");
					String string = "";
					for(int i = 0;i<strAssItem.length;i++){
						if(strAssItem[i] != null && !"".equals(strAssItem[i])){
							string += " asstotalname1 like '%"+strAssItem[i]+"%' or";
						}
					}
					
					if("".equals(string)) {
						string = " and 1=2 ";
					}else{
						string = " and ("+string.substring(0, string.length()-2)+") ";
					}
					sqlAssItem = "select distinct asstotalname1 from c_assitementryacc " +
					" where 1=1" +
					" and subyearmonth*12+submonth>='"+strStartYearMonth+"' and subyearmonth*12+submonth<='"+strEndYearMonth+"' " +
					" and Level1=1 " +
					string;
					
					ps = myConn.prepareStatement(sqlAssItem);
					rs = ps.executeQuery();
				    sqlAssItem = "";
				    while(rs.next()){
				    	sqlAssItem += " asstotalname1 like '"+rs.getString(1)+"/%' or" ;
				    }
				    if(!"".equals(sqlAssItem)){
				    	sqlAssItem = " and ( " + sqlAssItem.substring(0,sqlAssItem.length()-2)+ ") ";
				    }else{
				    	sqlAssItem = " and 1=2 ";
				    }
				}
				
			}
			
			
			String sql1 = "select a.fullname,a.subjectname,a.tokenid,a.dataname,a.isSubject,a.tid as sName,direction2,sid,recsign  \n" +
					" from ( \n";
			String sql2 = "";
			String sql3 = " order by a.sid ";
			if(!"".equals(strAccsign)){
				sql1 = " select distinct " +
				" if(c.fullname is null,if(b.fullname is null,a.fullname,b.fullname),c.fullname ) fullname,\n" +
				" if(c.subjectname is null,if(b.subjectname is null,a.subjectname,b.subjectname),c.subjectname ) subjectname,\n" +
				" if(c.AssTotalName1 is null,if(b.tokenid is null,a.tokenid,b.tokenid),c.AssTotalName1 ) tokenid,\n" +
				" if(c.dataname is null,if(b.dataname is null,a.dataname,b.dataname),c.dataname ) dataname,\n" +
				" if(c.isSubject is null,if(b.isSubject is null,a.isSubject,b.isSubject),c.isSubject ) isSubject, \n" +
				" if(c.sName is null,if(b.sName is null,a.tid,b.sName),c.sName ) as sName,\n" +
				" if(c.direction2 is null,if(b.direction2 is null,a.direction2,b.direction2),c.direction2 ) as direction2,  \n" +
				" if(c.sid is null,if(b.sid is null,a.sid,b.sid),c.sid ) as sid,  \n" +
				" if(c.recsign is null,if(b.recsign is null,a.recsign,b.recsign),c.recsign ) as recsign  \n" +
				" from ( \n";
				
				sql2 = " left join ( \n" +
				sqlCurName1 + 
				"\n ) b on a.tokenid = b.tokenid \n" +
				" left join ( \n" +
				sqlCurName2 + 
				"\n ) c on a.tid = c.tid and a.AssTotalName1 = c.AssTotalName1 \n";
			}
			
			sql = sql1 + 
			" 	select distinct " +
			"	if(assitemname is not null,assitemname,subjectname) as fullname,\n" +
			"	if(assitemname is not null,concat(subjectfullname1,'/',assitemname),subjectfullname1) as subjectname,\n" +
			" 	ifnull(AssTotalName1,a.tokenid) as tokenid,ifnull(b.dataname,a.dataname) as dataname, \n" +
			" 	a.tokenid as tid, AssTotalName1 ,IF(b.AssTotalName1 is null,'科目','核算') as isSubject," +
			"	ifnull(b.direction2,a.direction2) as direction2 ,ifnull(b.sid,a.sid) as sid,ifnull(b.recsign,a.recsign) as recsign \n" +
			" 	from(	\n" +
			"		select a.* ,ifnull(tids,tokenid) tids  \n" +
			"		from ( \n" +
			"		  select a.subjectname,a.subjectfullname1,a.tokenid,a.dataname,a.direction2,group_concat(sid) as sid,min(recsign) as recsign  from( \n" +
			"			select  distinct standname as subjectname,a.subjectfullname1, \n" +
			"			a.tokenid,if(a.DataName='0','本位币',a.DataName) as DataName,direction2, concat(subyearmonth,'`',subjectid) sid, \n" +
			"			if(exists(select 1 from z_usesubject c where c.projectid='"+project.getProjectId()+"' and a.isleaf1=1 and c.accpackageid=a.accpackageid and (c.subjectfullname = a.subjectfullname1 or c.subjectfullname like concat(a.subjectfullname1,'/%'))),0,1) as recsign  \n" +
			"			from c_account a  \n" +
			"			where a.subyearmonth*12+a.submonth>='"+strStartYearMonth+"' and a.subyearmonth*12+a.submonth<='"+strEndYearMonth+"' \n" +
			sqlType + 
			"			and (a.subjectfullname2 like '"+SubjectName+"/%' or a.subjectfullname2 = '"+SubjectName+"')  \n" +
			sqlSubject + 
			"			union \n" +
			"			select subjectName,subjectfullname,subjectfullname,'本位币' ,case substring(property,2,1) when 1 then 1 when 2 then -1 end  direction2,concat("+end+",'`',subjectid) sid, 1 as recsign \n" +
			"			from z_usesubject \n" +
			"			where projectid="+project.getProjectId()+" \n" +
			"			and tipsubjectid in (" +
			" 				select distinct subjectid from c_account a " +
			" 				where 1=1 " + 
			" 				and a.subyearmonth*12+a.submonth>='"+strStartYearMonth+"' and a.subyearmonth*12+a.submonth<='"+strEndYearMonth+"' " +
			"				and (a.subjectfullname2 like '"+SubjectName+"/%'  or a.subjectfullname2 = '"+SubjectName+"' ) and level1 = 1 " +
			" 			) \n" +
			sqlType1 +  
			" 			union " +
			"			select subjectName,subjectfullname,subjectfullname,'本位币' ,case substring(property,2,1) when 1 then 1 when 2 then -1 end  direction2,concat("+end+",'`',subjectid) sid, 1 as recsign \n" +
			"			from z_usesubject a \n" +
			"			where projectid="+project.getProjectId()+" \n" +
			"			and (a.subjectfullname like '"+SubjectName+"/%'  or a.subjectfullname = '"+SubjectName+"' )  " +
			sqlType1 +
			"		  )a group by a.tokenid,a.dataname,a.direction2" +
			"		) a left join ( \n" +
			"			select distinct accid ,tokenid as tids \n" +
			"			from c_assitementryacc a ,( \n" +
			"				select distinct accpackageid,subjectid,AccName,tokenid  from c_account a \n" +  
			"				where 1=1  \n" +
			"				and subyearmonth*12+submonth>='"+strStartYearMonth+"' and subyearmonth*12+submonth<='"+strEndYearMonth+"' \n" +
			"				and (a.subjectfullname2 like '"+SubjectName+"/%'  or a.subjectfullname2 = '"+SubjectName+"' ) \n" +
			"			) b \n" +
			"			where a.subyearmonth ="+end+"   \n" +
			sqlAssItem +
			"			and a.submonth=1  \n" +
			"			and a.isleaf1=1   \n" +
			"			and a.accpackageid = b.accpackageid \n" +
			"			and a.accid = b.subjectid \n" +
			"		) c on a.tokenid = c.tids	 \n" +
			"	) a \n" +
			"	left join ( \n" +
			"	  select assitemname,AssTotalName1,tokenid,DataName,direction2,group_concat(sid) as sid,1  as recsign from ( \n" +
			"		select distinct assitemname, AssTotalName1,tokenid,if(a.DataName='0','本位币',a.DataName) as DataName,direction2,concat(subyearmonth,'`',accid,'`',assitemid) as sid \n" +
			"		from c_assitementryacc a,(" +
			"			select distinct accpackageid,subjectid,AccName,tokenid  from c_account a \n" +  
			"			where 1=1   \n" +
			"			and subyearmonth*12+submonth>='"+strStartYearMonth+"' and subyearmonth*12+submonth<='"+strEndYearMonth+"'  \n" +
			"			and (a.subjectfullname2 like '"+SubjectName+"/%'  or a.subjectfullname2 = '"+SubjectName+"' ) \n" +
			"		) b \n" +
			"		where a.subyearmonth*12+a.submonth>='"+strStartYearMonth+"' and a.subyearmonth*12+a.submonth<='"+strEndYearMonth+"' \n" +
			"		and a.isleaf1=1 \n" +
			sqlAssItem +
			"		and a.accpackageid =b.accpackageid and a.accid = b.subjectid \n" +
			"	  ) a group by assitemname,AssTotalName1,tokenid,DataName,direction2 \n" +
			"	) b on a.tids = b.tokenid \n" +
			"	where 1=1 \n" +
			"	" + sqlCustomer + " \n" +
			" ) a  \n" +
			sql2 + sql3 ;
			
			if("全部".equals(subejctType)){
				sql = "select * from ( "+sql+") a \n" +
				"	union \n" +
				"		  select a.subjectname,a.subjectfullname1,a.tokenid,a.dataname,'科目' as isSubject,a.tokenid as sName,a.direction2,group_concat(sid) as sid,min(recsign) as recsign  from( \n" +
				"			select  distinct standname as subjectname,a.subjectfullname1, \n" +
				"			a.tokenid,if(a.DataName='0','本位币',a.DataName) as DataName,direction2, concat(subyearmonth,'`',subjectid) sid, \n" +
				"			if(exists(select 1 from z_usesubject c where c.projectid='"+project.getProjectId()+"' and a.isleaf1=1 and c.accpackageid=a.accpackageid and (c.subjectfullname = a.subjectfullname1 or c.subjectfullname like concat(a.subjectfullname1,'/%'))),0,1) as recsign  \n" +
				"			from c_account a  \n" +
				"			where a.subyearmonth*12+a.submonth>='"+strStartYearMonth+"' and a.subyearmonth*12+a.submonth<='"+strEndYearMonth+"' \n" +
				sqlType + 
				"			and (a.subjectfullname2 like '"+SubjectName+"/%' or a.subjectfullname2 = '"+SubjectName+"')  \n" +
				"			union \n" +
				"			select subjectName,subjectfullname,subjectfullname,'本位币' ,case substring(property,2,1) when 1 then 1 when 2 then -1 end  direction2,concat("+end+",'`',subjectid) sid, 1 as recsign \n" +
				"			from z_usesubject \n" +
				"			where projectid="+project.getProjectId()+" \n" +
				"			and tipsubjectid in (" +
				" 				select distinct subjectid from c_account a " +
				" 				where 1=1 " + 
				" 				and a.subyearmonth*12+a.submonth>='"+strStartYearMonth+"' and a.subyearmonth*12+a.submonth<='"+strEndYearMonth+"' " +
				"				and (a.subjectfullname2 like '"+SubjectName+"/%'  or a.subjectfullname2 = '"+SubjectName+"' ) and level1 = 1 " +
				" 			) \n" +
				sqlType1 +  
				" 			union " +
				"			select subjectName,subjectfullname,subjectfullname,'本位币' ,case substring(property,2,1) when 1 then 1 when 2 then -1 end  direction2,concat("+end+",'`',subjectid) sid, 1 as recsign \n" +
				"			from z_usesubject a \n" +
				"			where projectid="+project.getProjectId()+" \n" +
				"			and (a.subjectfullname like '"+SubjectName+"/%'  or a.subjectfullname = '"+SubjectName+"' )  " +
				sqlType1 +
				"		  )a group by a.tokenid,a.dataname,a.direction2" +
				" ";
				
			}
			
			return sql;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
	}
	
	public ArrayList getRuleModels(String strP){
		
		ArrayList mResult=new ArrayList();
	
		strP=strP.trim();
		String c="",result="";
		int iDirection=1;
		
		int i=0;
		//////System.out.println(strP);
		
		for(;i<strP.length();i++){
			
			c=strP.substring(i,i+1);
			
			if (c.equals("+") ||c.equals("＋") ){
				
				if (!result.equals("")){
					RuleModel rm=new RuleModel(); 
					
					rm.iDirection=iDirection;
					if (result!=null)
						rm.strName=result.trim();
					else{
						rm.strName="";
					}

					
					mResult.add(rm);
					
				}
				
				result="";
				iDirection=1;
			}else if (c.equals("-") ||c.equals("－")){
				
				if (!result.equals("")){
					RuleModel rm=new RuleModel();
					
					rm.iDirection=iDirection;
					if (result!=null)
						rm.strName=result.trim();
					else{
						rm.strName="";
					}

					
					mResult.add(rm);
				}
				
				result="";
				iDirection=-1;
			}else{
				result+=c;
			}
			
			
		}
	
		if (!result.equals("")){
			RuleModel rm=new RuleModel();
			
			rm.iDirection=iDirection;
			if (result!=null)
				rm.strName=result.trim();
			else{
				rm.strName="";
			}

			
			mResult.add(rm);
		}
	
		return mResult;
	}
	
	/**
	 * 根据底稿编号获取对应任务的对应标准科目名
	 * @param conn Connection
	 * @param manuid String
	 * @return String
	 * @throws Exception
	 */
	public String getTaskSubjectNameByManuID(Connection conn, String manuid)
			throws Exception {
		String subjectname = "";
		Statement st = null;
		ResultSet rs = null;
		try {
			String sql = "select distinct projectid from z_task where manuid="
					+ manuid;
			st = conn.createStatement();

			sql = "select subjectname from z_task where manuid=" + manuid;

			rs = st.executeQuery(sql);
			if (rs.next()) {
				subjectname = rs.getString(1);
			}
		} finally {
			if (rs != null)
				rs.close();
			if (st != null)
				st.close();
		}
		return subjectname;
	}
	
	/**
	 * 根据底稿任务编号和年项目编号获取对应的标准科目名称
	 * @param conn Connection
	 * @param projectid String
	 * @param taskcode String
	 * @return String
	 * @throws Exception
	 */
	public String getTaskSubjectNameByTaskCode(Connection conn,
			String projectid, String taskcode) throws Exception {
		String subjectname = "";
		Statement st = null;
		ResultSet rs = null;
		try {

			st = conn.createStatement();
			String sql = "select subjectname from z_task where projectid="
					+ projectid + " and taskcode='" + taskcode
					+ "' and isleaf=1";
			rs = st.executeQuery(sql);
			if (rs.next()) {
				subjectname = rs.getString(1);
			}
		} finally {
			if (rs != null)
				rs.close();
			if (st != null)
				st.close();
		}
		return subjectname;
	}
	
	public String changeSubjectName(Connection conn, String projectID,
			String subjectName) throws Exception {
		Statement st = null;
		ResultSet rs = null;
		try {
			st = conn.createStatement();
			String sql = "";
			sql = "select * from z_project a,k_customer b where projectid='"
					+ projectID + "' and b.DepartID=a.customerid";

			rs = st.executeQuery(sql);

			String dpID = "";
			String VocationID = "";
			String acc = "";
			if (rs.next()) {
				dpID = rs.getString("customerid");
				VocationID = rs.getString("VocationID");
				acc = rs.getString("AccPackageID");
			}

			sql = "select * from c_account where AccPackageID='"+acc+"' and subjectfullname2 = '"+subjectName+"' and submonth=1";
			rs = st.executeQuery(sql);
			if(rs.next()){
				return rs.getString("subjectfullname2");
			}
			
			sql = "select a.* from k_standsubject a ,("
					+ " 	select a.subjectname,replace(CONCAT(a.subjectname,'                                     '),b.key1,b.key2) exSubjectName"
					+ " 	from (    "
					+ " 		select '"
					+ subjectName
					+ "' as subjectName "
					+ " 	) a,k_key b"
					+ " 	where  b.departid in ('0','"
					+ dpID
					+ "') "
					+ "	and a.subjectname like concat('%',b.key1,'%') "
					+

					" 	union"
					+

					"	select distinct a.subjectname,TRIM(replace(replace(CONCAT(a.subjectname,'                                     '),b.key1,b.key2),c.key1,c.key2))  exSubjectName"
					+ " 	from (    "
					+ " 		select '"
					+ subjectName
					+ "' as subjectName "
					+ "	) a,k_key b,k_key c"
					+ "	where  b.departid in ('0','"
					+ dpID
					+ "') "
					+ " 	and  c.departid in ('0','"
					+ dpID
					+ "') "
					+ "	and a.subjectname like concat('%',b.key1,'%')  "
					+ "	and a.subjectname like concat('%',c.key1,'%') "
					+

					"	union "
					+

					"	select distinct a.subjectname,TRIM(replace(replace(replace(CONCAT(a.subjectname,'                                     '),b.key1,b.key2),c.key1,c.key2),d.key1,d.key2))  exSubjectName"
					+ " 	from (    " + " 		select '" + subjectName
					+ "' as subjectName " + "	) a,k_key b,k_key c,k_key d  "
					+ "	where  b.departid in ('0','" + dpID + "') "
					+ " 	and  c.departid in ('0','" + dpID + "') "
					+ " 	and  d.departid in ('0','" + dpID + "') "
					+ "	and a.subjectname like concat('%',b.key1,'%')  "
					+ "	and a.subjectname like concat('%',c.key1,'%')  "
					+ "	and a.subjectname like concat('%',d.key1,'%') "
					+ " ) b where VocationID=" + VocationID
					+ " and  a.subjectname = b.exSubjectName";

			rs = st.executeQuery(sql);
			if (rs.next()) {
				return rs.getString("subjectname");
			} else {
				return "";
			}

		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			if (rs != null)
				rs.close();
			if (st != null)
				st.close();
		}

	}
	
	/**
	 *
	 * @param conn Connection
	 * @param apkID String
	 * @param prjID String
	 * @param subjectName String
	 * @return String
	 * @throws Exception
	 */
	public String[] getClientIDAndDirectionByStandName(Connection conn,
			String apkID, String prjID, String subjectName) throws Exception {
		String[] result = { "", "" };
		Statement st = conn.createStatement();
		String sql = "  select subjectid,direction2 from c_account where accpackageid="
				+ apkID
				+ "  \n"
				+ "  and subjectfullname2='"
				+ subjectName
				+ "' \n"
				+ "  union  \n"
				+ "select subjectid,case when property=1 then 1 when property=2 then -1 else 1 end as direction from z_usesubject where  accpackageid="
				+ apkID
				+ " and projectid="
				+ prjID
				+ " and subjectfullname ='"
				+ subjectName + "'  \n";
		ResultSet rs = st.executeQuery(sql);
		if (rs.next()) {
			result[0] = rs.getString(1);
			result[1] = rs.getString(2);
		} else {
			result[0] = "null";
			result[1] = "1";
		}
		return result;
	}
	
	public static void main1(String[] args) throws Exception{
		Connection conn = null;
		try {
			
//			//////System.out.println(new java.text.DecimalFormat("#0.00").format(0.00));
			
//			DBConnect db = new DBConnect();
//			conn = db.getConnect("");
//			db.changeDataBaseByProjectid(conn,"20081394");
//			RuleService ruleService = new RuleService(conn);
//			GuideLine guideLine = new GuideLine();
//			Map map = new HashMap();
//			Project project = new Project();
//			project.setAccPackageId("1000032008");
//			project.setProjectId("20081394");
//			project.setAuditTimeBegin("2007-07-01");
//			project.setAuditTimeEnd("2008-06-30");	
//			project.setCustomerId("100003");
//			map.put("project", project);
			
//			guideLine.setAssitemID("2-00-1191010");
//			guideLine.setSubjectID("113302");//1002-02-02
//			guideLine.setProperty("结转数");
//			guideLine.setProperty("审定1年以内");
			
//			guideLine.setProperty("期末数");
//			guideLine.setProperty("借发生");
//			guideLine.setProperty("折合借发生");
//			guideLine.setProperty("年借发生");
//			guideLine.setProperty("2年到3年");
//			guideLine.setProperty("3年到4年");
//			guideLine.setProperty("1年前期末调整借");
//			guideLine.setProperty("期末调整贷");
//			guideLine.setProperty("借发生");
			
//			String s = ruleService.getProjectValue("20081394", "", "0", "0", guideLine, map);
//			//////System.out.println(String.valueOf(s));
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);

		}	
	}
	
	public static void main(String[] args) throws Exception{
		Connection conn = null;
		try {
//			String [] str = "人有".split("`");
//			//System.out.println(str[0]);
			System.out.println(null + "" );
			return ;
			
//			DBConnect db = new DBConnect();
//			conn = db.getConnect("");
//			db.changeDataBaseByProjectid(conn,"20081399");
//			RuleService ruleService = new RuleService(conn);
//			GuideLine guideLine = new GuideLine();
//			Map map = new HashMap();
//			Project project = new Project();
//			project.setAccPackageId("1000392006");
//			project.setProjectId("20081399");
//			project.setAuditTimeBegin("2006-01-01");
//			project.setAuditTimeEnd("2006-12-31");	
//			project.setCustomerId("100039");
//			map.put("project", project);
////			map.put("显示行数", "10");　//不用
//			
////			map.put("科目名称", "主营业务收入");
////			map.put("对方科目", "主营业务成本");
//			
////			map.put("科目名称", "其他应收款");
////			map.put("科目名称", ""+SubjectName+"");
////			map.put("科目名称", "库存商品");
//			map.put("科目名称", "在库产成品");
////			map.put("核算名称", "客户;供应商;关联;往来");
////			map.put("支持外币", "支持");
//			map.put("支持数量", "支持");
//			map.put("关联客户", "关联客户");
////			map.put("关联客户", "其它客户");
////			map.put("包含核算", "是");
//			map.put("包含核算", "否");
////			map.put("科目类型", "一级");
////			map.put("科目类型", "二级");
//			
////			String isAssItem = CHF.showNull((String)args.get("包含核算"));
////			String AssItem = CHF.showNull((String)args.get("核算名称"));
////			String subejctType = CHF.showNull((String)args.get("科目类型"));// 末级(一级、二级)
////			String correlation = CHF.showNull((String)args.get("关联客户")); //所有客户、关联客户、其它客户
////			String isCurname = CHF.showNull((String)args.get("支持外币"));	//支持
//			
//			ResultSet rs = ruleService.getRuleSubjectName(map);
//			java.sql.ResultSetMetaData rsmd = rs.getMetaData();
//			int iCount = rsmd.getColumnCount();
//			int ii = 1;
//			while(rs.next()){
//				String string = "";
//				for (int i = 1; i <= iCount; i++) {
//					string += rsmd.getColumnLabel(i) +" = "+ rs.getString(i) + " | ";
//				}
//				////System.out.println("输出 " + ii +" 行: " + string);
//				ii ++;
//			}
			
//			RuleService ruleService = new RuleService(conn);
//			GuideLine guideLine = new GuideLine();
//			Map map = new HashMap();
//			Project project = new Project();
//			project.setAccPackageId("1001292007");
//			project.setProjectId("200814164");
//			project.setAuditTimeBegin("2007-01-01");
//			project.setAuditTimeEnd("2007-12-31");
//			map.put("project", project);
////			map.put("对应科目", "本年利润;利润分配");
//			
//			guideLine.setAssitemID("1005.01");
//			guideLine.setSubjectID("503.02.12");//1002-02-02
////			guideLine.setProperty("结转数");
//			guideLine.setProperty("1年以内");
////			guideLine.setProperty("期末数");
////			guideLine.setProperty("2年到3年");
////			guideLine.setProperty("3年到4年");
////			guideLine.setProperty("1年前期末调整借");
////			guideLine.setProperty("期末调整贷");
////			guideLine.setProperty("借发生");
//			
//			String s = ruleService.getProjectValue("200813160", "0", "12", "0", guideLine, map);
//			//////System.out.println(String.valueOf(s));
////			s = ruleService.getObject("200813160", "-1",  "0", "0", "现金", "", "期末数",project);
////			//////System.out.println(String.valueOf(s));
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);

		}
		
	}
	
}


