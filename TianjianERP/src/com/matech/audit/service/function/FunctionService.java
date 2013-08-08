package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import com.matech.audit.service.customer.CustomerService;
import com.matech.audit.service.project.ProjectService;
import com.matech.audit.service.project.model.Project;
import com.matech.audit.service.report.ReportService;
import com.matech.framework.pub.autocode.DELUnid;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.sys.UTILSysProperty;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.UTILString;

public class FunctionService {

	private String proid;
	private String pkgid;
	private String fname;
	private String userid;
	private String username;
	private HttpServletRequest request;
	private Connection conn;

	
//	private String km;
//	private String subname;
	
	ASFuntion CHF = new ASFuntion();
	SubjectResultService SRS = null;
	
	public FunctionService(Connection conn,String proid, String pkgid, String fname,
			String userid, String username, HttpServletRequest request){
		this.conn=conn;
		this.proid = proid; // 项目ID
		this.pkgid = pkgid; // 帐套编号
		this.fname = fname; // 函数名称
		this.userid = userid; // 客户ID
		this.username = username; // 客户名称
		this.request = request; // 网页信息
		SRS = new SubjectResultService(conn,pkgid);
	}
	
	private String getYearRectify() throws Exception{
		String result = "0.00";
		String sql = "";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String km = CHF.showNull(request.getParameter("km"));
			if (km.equals("")) {
				return "科目不能为空!";
			}
			
			/**
			 *科目对照  
			 */
			String skm = changeSubjectName(km);
			if(!"".equals(skm)){
				km = skm;
			}
			
			String tzlx = CHF.showNull(request.getParameter("tzlx"));	//1,2,3,4
			if (tzlx.equals("")) {
				return "返回值类型不能为空！";
			}
			String fx = CHF.showNull(request.getParameter("fx"));//1,-1,0
			if (fx.equals("")) {
				return "返回值方向不能为空！";
			}
			String qu = CHF.showNull(request.getParameter("qu"));	//0,1
			if (qu.equals("")) {
				return "返回区间类型不能为空！";
			}
			String year = CHF.showNull(request.getParameter("year"));//0 -1 -2 -3 ...
			if (year.equals("")) {
				return "年度不能为空！";
			}
			String bz = CHF.showNull(request.getParameter("bz")); //0,~
			
			String tableName = "";
			if("0".equals(year)){
				if("0".equals(bz)){
					tableName = "z_accountrectify";
				}else{
					tableName = "z_accountallrectify";
				}
			}else{
				if("0".equals(bz)){
					tableName = "z_accountyearrectify";
				}else{
					tableName = "z_accountallyearrectify";
				}
			}
			
			String dir = "1";
			sql = "select distinct direction2 from c_account where AccPackageID = '"+pkgid+"' and  (SubjectID='"+km+"' or AccName='"+km+"' or SubjectFullName1='"+km+"') ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				dir = rs.getString(1);
			}
			
			String returnValue = "";
			
			String endyear = SRS.getProjectEndYear(proid);			
			
			if("1".equals(tzlx)){	//调整
				if("0".equals(qu)){	//年末
					if("1".equals(fx)){
						returnValue = "sum(DebitTotalOcc1)";
					}else if("-1".equals(fx)){
						returnValue = "sum(CreditTotalOcc1)";
					}else{
						returnValue = dir + "*sum(DebitTotalOcc1 - CreditTotalOcc1)";
					}
				}else{	//年初
					if("1".equals(fx)){
						returnValue = "sum(DebitTotalOcc4)";
					}else if("-1".equals(fx)){
						returnValue = "sum(CreditTotalOcc4)";
					}else{
						returnValue = dir + "*sum(DebitTotalOcc4 - CreditTotalOcc4)";
					}
				}
			}else if("2".equals(tzlx)){		//重分类
				if("0".equals(qu)){	//年末
					if("1".equals(fx)){	//借
						returnValue = "sum(DebitTotalOcc2)";
					}else if("-1".equals(fx)){	//贷 
						returnValue = "sum(CreditTotalOcc2)";
					}else{	//借-贷 
						returnValue = dir + "*sum(DebitTotalOcc2 - CreditTotalOcc2)";
					}
				}else{	//年初
					if("1".equals(fx)){ //借
						returnValue = "sum(DebitTotalOcc5)";
					}else if("-1".equals(fx)){ //贷 
						returnValue = "sum(CreditTotalOcc5)";
					}else{	//借-贷 
						returnValue = dir + "*sum(DebitTotalOcc5 - CreditTotalOcc5)";
					}
				}
			}else if("3".equals(tzlx)){		//不符未调
				if("0".equals(qu)){	//年末
					if("1".equals(fx)){	//借
						returnValue = "sum(DebitTotalOcc3)";
					}else if("-1".equals(fx)){	//贷 
						returnValue = "sum(CreditTotalOcc3)";
					}else{	//借-贷 
						returnValue = dir + "*sum(DebitTotalOcc3 - CreditTotalOcc3)";
					}
				}else{	//年初
					if("1".equals(fx)){ //借
						returnValue = "sum(DebitTotalOcc0)";
					}else if("-1".equals(fx)){ //贷 
						returnValue = "sum(CreditTotalOcc0)";
					}else{	//借-贷 
						returnValue = dir + "*sum(DebitTotalOcc0 - CreditTotalOcc0)";
					}
				}
			}else if("4".equals(tzlx) && "0".equals(bz)){		//账表不符
				if("0".equals(qu)){	//年末
					returnValue = " 0 ";
				}else{	//年初
					if("1".equals(fx)){ //借
						returnValue = "sum(DebitTotalOcc6)";
					}else if("-1".equals(fx)){ //贷 
						returnValue = "sum(CreditTotalOcc6)";
					}else{	//借-贷 
						returnValue = dir + "*sum(DebitTotalOcc6 - CreditTotalOcc6)";
					}
				}
			}else {
				returnValue = " 0 ";
			}
			
			String whereValue = "";
			String stropt = SRS.getTextKeyAll(km,proid);
			String [] ss = stropt.split("\\|");
			String str1 = ss.length<=1 ?  km + "','" : ss[1].replaceAll("`","','");
			km = "'"+str1.substring(0,str1.length()-2);
			whereValue =" and subjectid in ("+km+") ";
			
			if(!"0".equals(year)){
				whereValue += " and yearrectify = '"+String.valueOf((Integer.parseInt(endyear) + Integer.parseInt(year)))+"' ";
			}
				
			if(!"0".equals(bz)){
				whereValue += " and DataName='"+bz+"' ";
			}
			
			
			sql = "select "+ returnValue+ " from "+tableName+" where AccPackageID="+pkgid+" and projectid="+proid+" " + whereValue ;
			
			//org.util.Debug.prtOut("取项目调整数 = " + sql);
			ps = conn.prepareStatement(sql);
			int i = 1;
			rs = ps.executeQuery();
			i = 0;
			while (rs.next()) {
				i++;
				if (i > 1) {
					result = "科目调整数出错！";
					break;
				}
				result = CHF.showNull(rs.getString(1));
			}

			if("".equals(result)){
				result = "0";//result = "该科目既不是标准科目，也不是用户科目，请修改科目名";
			}
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
	}
	
	public String getResult() throws Exception {
		String result = "";
		// 取科目名称
		try {
			if (fname.equals("getNcs")) {
				result = getNcs(); // 取年初数
			} else if (fname.equals("getNcsGo")) {
				result = getNcsGo(); // 取年初数(上年)
			} else if (fname.equals("getHsNcs")) {
				result = getHsNcs(); // 取核算年初数
			} else if (fname.equals("getHsNcsGo")) {
				result = getHsNcsGo(); // 取核算年初数(上年)
			} else if (fname.equals("getNms")) {
				result = getNms(); // 取年末数
			} else if (fname.equals("getNmsGo")) {
				result = getNmsGo(); // 取年末数(上年)
			} else if (fname.equals("getHsNms")) {
				result = getHsNms(); // //取核算年末数
			} else if (fname.equals("getHsNmsGo")) {
				result = getHsNmsGo(); // //取核算年末数(上年)
			}

			else if (fname.equals("getNlfse")) {
				result = getNlfse(); // 科目余额表年累计发生额
			} else if (fname.equals("getNlfseGo")) {
				result = getNlfseGo(); // 科目余额表年累计发生额(上年)
			} else if (fname.equals("getHsNlfse")) {
				result = getHsNlfse(); // 取核算年累计发生额
			} else if (fname.equals("getHsNlfseGo")) {
				result = getHsNlfseGo(); // 取核算年累计发生额(上年)
			}

			else if (fname.equals("getBqfse")) {
				result = getBqfse(); // 科目余额表本期发生额
			} else if (fname.equals("getBqfseGo")) {
				result = getBqfseGo(); // 科目余额表本期发生额(上年)
			} else if (fname.equals("getHsBqfse")) {
				result = getHsBqfse(); // 取核算本期发生额
			} else if (fname.equals("getHsBqfseGo")) {
				result = getHsBqfseGo(); // 取核算本期发生额(上年)
			}

			else if (fname.equals("getBqqms")) {
				result = getBqqms(); // 科目余额表本期期末数
			} else if (fname.equals("getBqqmsGo")) {
				result = getBqqmsGo(); // 科目余额表本期期末数(上年)
			} else if (fname.equals("getHsBqqms")) {
				result = getHsBqqms(); // 核算本期期末数
			} else if (fname.equals("getHsBqqmsGo")) {
				result = getHsBqqmsGo(); // 核算本期期末数(上年)
			}

			else if (fname.equals("getBqqcs")) {
				result = getBqqcs(); // 科目余额表本期期初数
			} else if (fname.equals("getBqqcsGo")) {
				result = getBqqcsGo(); // 科目余额表本期期初数(上年)
			} else if (fname.equals("getHsBqqcs")) {
				result = getHsBqqcs(); // 核算本期期初数
			} else if (fname.equals("getHsBqqcsGo")) {
				result = getHsBqqcsGo(); // 核算本期期初数(上年)
			}

			else if (fname.equals("getRwbxx")) {
				result = getRwbxx(); // 任务表信息
			} else if (fname.equals("getKmdzs")) {
				result = getKmdzs(); // 取科目调整数
			} else if (fname.equals("getKmdzsGo")) {
				result = getKmdzsGo(); // 取科目调整数(上年)
			}
			// else if (fname.equals("getKmbqdzs")) {
			// result = getKmbqdzs(); //取科目本期调整数
			// }
			else if (fname.equals("getSjdwxx")) {
				result = getSjdwxx(); // 取审计单位信息
			} else if (fname.equals("getSjdwzdyxx")) {
				result = getSjdwzdyxx(); // 取审计单位自定义信息
			} else if (fname.equals("getBsjdwxx")) {
				result = getBsjdwxx(); // 取被审单位信息
			} else if (fname.equals("getBsjdwzdyxx")) {
				result = getBsjdwzdyxx(); // 取被审单位自定义信息
			} else if (fname.equals("getZdyxx")) {
				result = getZdyxx();
			} else if (fname.equals("getDgxx")) {
				result = getDgxx(); // 底稿信息
			} else if (fname.equals("getKjzc")) {
				result = getKjzc(); // 会计政策
			} else if (fname.equals("getZdyHs")) {
				result = getZdyHs(); // 自定义函数
			} else if (fname.equals("getXmxx")) {
				result = getXmxx();
			} else if (fname.equals("getXmzdyxx")){
				result = getXmzdyxx();//取审计项目自定义信息
			}
			
			else if (fname.equals("getBbNcs")) {
				result = getBbNcs(); // 取年初数（报表值）(2007-2-8)
			} else if (fname.equals("getBbNms")) {
				result = getBbNms(); // 取年末数（报表值）(2007-2-8)
			} else if (fname.equals("getBbNlfse")) {
				result = getBbNlfse(); // 科目余额表年累计发生额 （报表值）(2007-2-8)
			} else if (fname.equals("getBbKmdzs")) {
				result = getBbKmdzs(); // 取科目调整数 （报表值）(2007-2-12)
			}
			
			else if (fname.equals("getBbBqqcs")) {
				result = getBbBqqcs(); // 科目余额表本期期初数 （报表值）(2007-2-12)
			}else if (fname.equals("getBbBqqms")) {
				result = getBbBqqms(); // 科目余额表本期期末数 （报表值）(2007-2-12)
			}else if (fname.equals("getBbBqfse")) {
				result = getBbBqfse(); // 科目余额表本期发生额 （报表值）(2007-2-12)
			}
			
			else if (fname.equals("getBbNcsGo")) {
				result = getBbNcsGo(); // 取年初数(上年)（报表值）(2007-2-8)
			}else if (fname.equals("getBbNmsGo")) {
				result = getBbNmsGo(); // 取年末数(上年)（报表值）(2007-2-8)
			}else if (fname.equals("getBbNlfseGo")) {
				result = getBbNlfseGo(); // 科目余额表年累计发生额(上年)（报表值）(2007-2-8)
			}
			
			else if (fname.equals("getBbBqqcsGo")) {
				result = getBbBqqcsGo(); // 科目余额表本期期初数 (上年)（报表值）(2007-2-13)
			}else if (fname.equals("getBbBqqmsGo")) {
				result = getBbBqqmsGo(); // 科目余额表本期期末数 (上年)（报表值）(2007-2-13)
			}else if (fname.equals("getBbBqfseGo")) {
				result = getBbBqfseGo(); // 科目余额表本期发生额 (上年)（报表值）(2007-2-13)
			}
			
			else if (fname.equals("getRelation")) {
				result = getRelation(); // 得到合并报表的控股或比率　控股为0；比率为1
			}else if (fname.equals("getRectify")) {
				result = getRectify(); // getRectify(报表类型,报表项目,汇总方向) jb,lx,xm,fx
			}
			
			else if(fname.equals("getProject")){
				result = getProject();	//取项目数
			}
			else if(fname.equals("getExchangerate")){
				result = getExchangerate();	//取汇率
			}
			
			else if(fname.equals("getYearRectify")){
				result = getYearRectify();	//取项目年度调整
			}
			else if(fname.equals("getAccpackage")){
				result = getAccpackage();	//取帐套数
			}
			else if(fname.equals("getAccpackageGuide")){
				result = getAccpackageGuide();	//取帐套数指导
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "";

		}
	}
	
	
//	 任务表信息
	private String getRwbxx() throws Exception {
		String result = "";
		String sql = "";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			
			String taskid = CHF.showNull(request.getParameter("dg")); // 底稿id
			// proid
			if (taskid.equals("")) {
				return "底稿编号不能为空!";
			}
			String xm = CHF.showNull(request.getParameter("xm"));
			if (xm.equals("")) {
				return "底稿返回值参数不能为空!";
			}

//			taskid = new String(taskid.getBytes("ISO8859-1"));
//			xm = new String(xm.getBytes("ISO8859-1"));

			sql = "select * from z_task where taskid=? and ProjectID =?";
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, taskid);
			ps.setString(i++, proid);
			rs = ps.executeQuery();
			if (rs.next()) {
				if (xm.equals("编制人")) {
					result = CHF.showNull(rs.getString("User1"));
				} else if (xm.equals("复核人")) {
					result = CHF.showNull(rs.getString("User2"));
				} else if (xm.equals("审核人")) {
					result = CHF.showNull(rs.getString("User3"));
				} else if (xm.equals("编制时间")) {
					result = CHF.showNull(rs.getString("date1"));
				} else if (xm.equals("复核时间")) {
					result = CHF.showNull(rs.getString("date2"));
				} else if (xm.equals("审核时间")) {
					result = CHF.showNull(rs.getString("date3"));
				} else if (xm.equals("底稿名称")) {
					result = CHF.showNull(rs.getString("TaskName"));
				} else if (xm.equals("底稿编号")) {
					result = CHF.showNull(rs.getString("TaskID"));
				} else {
					result = "底稿返回值参数输入错误!";
				}
			} else {
				result = "没有此底稿!";
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
	}

//	 取科目调整数
	private String getKmdzs() throws Exception {
		String result = "0.00";
		String sql = "";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String km = CHF.showNull(request.getParameter("km"));
			if (km.equals("")) {
				return "科目不能为空!";
			}
			
			/**
			 *科目对照  
			 */
			String skm = changeSubjectName(km);
			if(!"".equals(skm)){
				km = skm;
			}
			
			String type = CHF.showNull(request.getParameter("type")); //1:科目名称 2：科目全路径 3：科目ID

			String tzlx = CHF.showNull(request.getParameter("tzlx"));
			if (tzlx.equals("")) {
				return "调整类型不能为空！";
			}

			String fx = CHF.showNull(request.getParameter("fx"));
			if (fx.equals("")) {
				return "方向不能为空！";
			}
			
			String sTab = "";
			String bz = CHF.showNull(request.getParameter("bz"));
			if (bz.equals("0") || bz.equals("") || bz.equals("本位币")) {
				bz = "0";
				sTab = " z_accountrectify ";
			}else{
				sTab = " z_accountallrectify ";
			}
	
			
			if (type.equals("") || type.equals("1")) {
				if (!SRS.isNull("c_accpkgsubject", "SubjectName", km, "")) {
					km = SRS.SubjectResult(km);
				} else {
					km = "('" + km + "')";
				}
				sql = " select ifnull(direction2,if(SUBSTRING(Property,2,1)=2,-1,1) ) direction2 " +
				" from c_accpkgsubject a left join c_account b " +
				" on a.subjectid = b.subjectid and b.submonth = 1 " +
				" where a.accpackageid='"+pkgid+"' and b.accpackageid='"+pkgid+"' and a.SubjectName in "+km + "" +
				" union " +
				" select if(SUBSTRING(Property,2,1)=2,-1,1) from z_usesubject" +
				" where projectid = '"+this.proid+"' " +
				" and SubjectName in "+km + "";
				
			}else if (type.equals("2")) {
				if (!SRS.isNull("c_accpkgsubject", "SubjectFullName", km, "")) {
					km = SRS.SubjectResult(km);
				} else {
					km = "('" + km + "')";
				}
				
				sql = " select ifnull(direction2,if(SUBSTRING(Property,2,1)=2,-1,1) ) direction2 " +
				" from c_accpkgsubject a left join c_account b " +
				" on a.subjectid = b.subjectid and b.submonth = 1 " +
				" where a.accpackageid='"+pkgid+"' and b.accpackageid='"+pkgid+"' and a.SubjectFullName in "+km + "" +
				" union " +
				" select if(SUBSTRING(Property,2,1)=2,-1,1) from z_usesubject" +
				" where projectid = '"+this.proid+"' " +
				" and SubjectFullName in "+km + "";
			}else{
				sql = " select ifnull(direction2,if(SUBSTRING(Property,2,1)=2,-1,1) ) direction2 " +
				" from c_accpkgsubject a left join c_account b " +
				" on a.subjectid = b.subjectid and b.submonth = 1 " +
				" where a.accpackageid='"+pkgid+"' and b.accpackageid='"+pkgid+"' and a.subjectid = '"+km+"'" +
				" union " +
				" select if(SUBSTRING(Property,2,1)=2,-1,1) from z_usesubject" +
				" where projectid = '"+this.proid+"' " +
				" and subjectid = '"+km+"'";
			}
			
			String direction2 = "";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				direction2 = rs.getString(1);
			}
			DbUtil.close(rs);
            DbUtil.close(ps);
			
			
			String str = "";
			if (fx.equals("1")) {
				str = "debittotalocc" + tzlx;
				if (tzlx.equals("7")) {
					str = "debittotalocc1 + debittotalocc2";
				}
				if (tzlx.equals("8")) {
					str = "debittotalocc4 + debittotalocc5";
				}
			} else if (fx.equals("-1")) {
				str = "credittotalocc" + tzlx;
				if (tzlx.equals("7")) {
					str = "credittotalocc1 + credittotalocc2";
				}
				if (tzlx.equals("8")) {
					str = "credittotalocc4 + credittotalocc5";
				}
			} else if (fx.equals("0")) {
				str = direction2 + " * (debittotalocc" + tzlx + " - credittotalocc" + tzlx + ") ";
				if (tzlx.equals("7")) {
					str = direction2 + " * (debittotalocc1 - credittotalocc1 + debittotalocc2 - credittotalocc2) occ ";
				}
				if (tzlx.equals("8")) {
					str = direction2 + " * (debittotalocc4 - credittotalocc4 + debittotalocc5 - credittotalocc5) occ ";
				}

			}  else {
				return "方向输入错误！";
			}
			
			sql = "select " + str + " from "+sTab+" where  projectid='"+proid+"'  ";
			if(!"0".equals(bz)){
				sql += " and DataName='"+bz+"' ";
			}
			
			if (type.equals("") || type.equals("1")) {

				sql += " and subjectname in " + km;
			} else if (type.equals("2")) {

				sql += " and SubjectID in (select SubjectID from c_accpkgsubject where AccPackageID='"
						+ pkgid + "' and SubjectFullName in " + km + " union " +
						"select SubjectID from z_usesubject where AccPackageID='"+pkgid+"' and projectID='"+proid+"'  and SubjectFullName in " + km + ")";
				
			} else if (type.equals("3")) {
				sql += "and subjectid = " + km;
			}
			
			//org.util.Debug.prtOut("getKmdzs:" + sql);
			ps = conn.prepareStatement(sql);
			int i = 1;
			rs = ps.executeQuery();
			i = 0;
			while (rs.next()) {
				i++;
				if (i > 1) {
					result = "科目调整数出错！";
					break;
				}
				result = CHF.showNull(rs.getString(1));
			}

			if("".equals(result)){
				result = "0";//result = "该科目既不是标准科目，也不是用户科目，请修改科目名";
			}
			
			return result;
			
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
	}
	
	
	// 取科目调整数
	
//	private String getKmdzs() throws Exception {
//		String result = "0.00";
//		String sql = "";
//		
//		PreparedStatement ps = null;
//		ResultSet rs = null;
//		try {
//			
//
//			String km = CHF.showNull(request.getParameter("km"));
////			km = new String(km.getBytes("ISO8859-1"));
//			if (km.equals("")) {
//				return "科目不能为空!";
//			}
//			String type = CHF.showNull(request.getParameter("type"));
////			type = new String(type.getBytes("ISO8859-1"));
//
//			String tzlx = CHF.showNull(request.getParameter("tzlx"));
////			tzlx = new String(tzlx.getBytes("ISO8859-1"));
//			if (tzlx.equals("")) {
//				return "调整类型不能为空！";
//			}
//
//			String fx = CHF.showNull(request.getParameter("fx"));
////			fx = new String(fx.getBytes("ISO8859-1"));
//			if (fx.equals("")) {
//				return "方向不能为空！";
//			}
//
//			/**
//			 * 兼容外币
//			 */
//			String sTab = "";
//			String bz = CHF.showNull(request.getParameter("bz"));
////			bz = new String(bz.getBytes("ISO8859-1"));
//			if (bz.equals("0") || bz.equals("") || bz.equals("本位币")) {
//				sTab = " z_accountrectify ";
//				bz = "0";
//			} else {
//				sTab = " z_accountallrectify ";
//			}
//			
//			String str = "";
//			
//			if (type.equals("") || type.equals("1")) {
//				if (!SRS.isNull("c_accpkgsubject", "SubjectName", km, "")) {
//					km = SRS.SubjectResult(km);
//				} else {
//					km = "('" + km + "')";
//				}
//				sql = "select if(SUBSTRING(Property,2,1)=2,-1,1) from c_accpkgsubject where AccPackageID='"+pkgid+"' and SubjectName in "+km;
//			}else if (type.equals("2")) {
//				if (!SRS.isNull("c_accpkgsubject", "SubjectFullName", km, "")) {
//					km = SRS.SubjectResult(km);
//				} else {
//					km = "('" + km + "')";
//				}
//				sql = "select if(SUBSTRING(Property,2,1)=2,-1,1) from c_accpkgsubject where AccPackageID='"+pkgid+"' and SubjectFullName in "+km;
//			}else{
//				sql = "select if(SUBSTRING(Property,2,1)=2,-1,1) from c_accpkgsubject where AccPackageID='"+pkgid+"' and subjectid = '"+km+"'";
//			}
//			String dir = "";
//			ps = conn.prepareStatement(sql);
//			rs = ps.executeQuery();
//			if(rs.next()){
//				dir = rs.getString(1);
//			}
//			//org.util.Debug.prtOut("dir getKmdzs:" + dir);
//			sql = "";
//			
//			if (fx.equals("1")) {
//				str = "debittotalocc" + tzlx;
//				if (tzlx.equals("7")) {
//					str = "debittotalocc1 + debittotalocc2";
//				}
//				if (tzlx.equals("8")) {
//					str = "debittotalocc4 + debittotalocc5";
//				}
//
//				sql += "select "
//						+ str
//						+ " from "+sTab+" where AccPackageID=? and projectid=? ";
//				if(!"0".equals(bz)){
//					sql += " and DataName='"+bz+"' ";
//				}
//			} else if (fx.equals("-1")) {
//				str = "credittotalocc" + tzlx;
//				if (tzlx.equals("7")) {
//					str = "credittotalocc1 + credittotalocc2";
//				}
//				if (tzlx.equals("8")) {
//					str = "credittotalocc4 + credittotalocc5";
//				}
//
//				sql += "select "
//						+ str
//						+ " from "+sTab+" where AccPackageID=? and projectid=? ";
//				if(!"0".equals(bz)){
//					sql += " and DataName='"+bz+"' ";
//				}
//			} else if (fx.equals("0")) {
//				str = "debittotalocc" + tzlx + " - credittotalocc" + tzlx;
//				if (tzlx.equals("7")) {
//					str = dir + "*(debittotalocc1 - credittotalocc1 + debittotalocc2 - credittotalocc2) occ ";
//				}
//				if (tzlx.equals("8")) {
//					str = dir + "*(debittotalocc4 - credittotalocc4 + debittotalocc5 - credittotalocc5) occ ";
//				}
//
//				sql += "select "
//						+ str
//						+ " from "+sTab+" where AccPackageID=?  and projectid=? ";
//				if(!"0".equals(bz)){
//					sql += " and DataName='"+bz+"' ";
//				}
//			} else {
//				return "方向输入错误！";
//			}
//
//			if (type.equals("") || type.equals("1")) {
////				if (!SRS.isNull("c_accpkgsubject", "SubjectName", km, "")) {
////					km = AST.TextKey(km);
////				} else {
////					km = "('" + km + "')";
////				}
//
//				sql += " and subjectname in " + km;
//			} else if (type.equals("2")) {
//				// ///////////////////////////////////////
////				if (!SRS.isNull("c_accpkgsubject", "SubjectFullName", km, "")) {
////					km = AST.TextKey(km);
////				} else {
////					km = "('" + km + "')";
////				}
//
//				sql += " and SubjectID in (select SubjectID from c_accpkgsubject where AccPackageID='"
//						+ pkgid + "' and SubjectFullName in " + km + " union " +
//						"select SubjectID from z_usesubject where AccPackageID='"+pkgid+"' and projectID='"+proid+"'  and SubjectFullName in " + km + ")";
//				// ///////////////////////////////////////
//			} else if (type.equals("3")) {
//				sql += "and subjectid = " + km;
//			}
//
//			//org.util.Debug.prtOut("getKmdzs:" + sql);
//			ps = conn.prepareStatement(sql);
//			int i = 1;
//			ps.setString(i++, pkgid);
//			ps.setString(i++, proid);
//			rs = ps.executeQuery();
//			i = 0;
//			while (rs.next()) {
//				i++;
//				if (i > 1) {
//					result = "科目调整数出错！";
//					break;
//				}
//				result = CHF.showNull(rs.getString(1));
//			}
//
//			if("".equals(result)){
//				result = "0";//result = "该科目既不是标准科目，也不是用户科目，请修改科目名";
//			}
//			//org.util.Debug.prtOut("getNcs result:" + result);
//			
//			return result;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return "0";
//		} finally {
//			DbUtil.close(rs);
//            DbUtil.close(ps);
//		}
//
//	}

	
	// 取科目调整数(上年)
	private String getKmdzsGo() throws Exception {
		String result = "0.00";
		String sql = "";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		pkgid = SRS.getNewPackageID(pkgid);
		try {
			if("该单位没有去年的帐套!".equals(pkgid)){
				return "0";
			}
			

			String km = CHF.showNull(request.getParameter("km"));
//			km = new String(km.getBytes("ISO8859-1"));
			if (km.equals("")) {
				return "科目不能为空!";
			}
			
			/**
			 *科目对照  
			 */
			String skm = changeSubjectName(km);
			if(!"".equals(skm)){
				km = skm;
			}
			
			String type = CHF.showNull(request.getParameter("type"));
//			type = new String(type.getBytes("ISO8859-1"));

			String tzlx = CHF.showNull(request.getParameter("tzlx"));
//			tzlx = new String(tzlx.getBytes("ISO8859-1"));
			if (tzlx.equals("")) {
				return "调整类型不能为空！";
			}

			String fx = CHF.showNull(request.getParameter("fx"));
//			fx = new String(fx.getBytes("ISO8859-1"));
			if (fx.equals("")) {
				return "方向不能为空！";
			}

			String str = "";
			
			if (type.equals("") || type.equals("1")) {
				if (!SRS.isNull("c_accpkgsubject", "SubjectName", km, "")) {
					km = SRS.SubjectResult(km);
				} else {
					km = "('" + km + "')";
				}
			}else if (type.equals("2")) {
				if (!SRS.isNull("c_accpkgsubject", "SubjectFullName", km, "")) {
					km = SRS.SubjectResult(km);
				} else {
					km = "('" + km + "')";
				}
			}
			
			if (fx.equals("1")) {
				str = "debittotalocc" + tzlx;
				if (tzlx.equals("7")) {
					str = "debittotalocc1 + debittotalocc2";
				}
				if (tzlx.equals("8")) {
					str = "debittotalocc4 + debittotalocc5";
				}

				sql += "select "
						+ str
						+ " from z_accountrectify where AccPackageID=? and projectid=?";
			} else if (fx.equals("-1")) {
				str = "credittotalocc" + tzlx;
				if (tzlx.equals("7")) {
					str = "credittotalocc1 + credittotalocc2";
				}
				if (tzlx.equals("8")) {
					str = "credittotalocc4 + credittotalocc5";
				}

				sql += "select "
						+ str
						+ " from z_accountrectify where AccPackageID=? and projectid=?";
			} else if (fx.equals("0")) {
				str = "debittotalocc" + tzlx + " - credittotalocc" + tzlx;
				if (tzlx.equals("7")) {
					str = "(select if(SUBSTRING(Property,2,1)=2,-1,1) from c_accpkgsubject where AccPackageID='"+pkgid+"' and (SubjectName ='"+km+"' or SubjectFullName='"+km+"' or subjectid='"+km+"') )*(debittotalocc1 - credittotalocc1 + debittotalocc2 - credittotalocc2) occ ";
				}
				if (tzlx.equals("8")) {
					str = "(select if(SUBSTRING(Property,2,1)=2,-1,1) from c_accpkgsubject where AccPackageID='"+pkgid+"' and (SubjectName ='"+km+"' or SubjectFullName='"+km+"' or subjectid='"+km+"') )*(debittotalocc4 - credittotalocc4 + debittotalocc5 - credittotalocc5) occ ";
				}

				sql += "select "
						+ str
						+ " from z_accountrectify where AccPackageID=? and projectid=?";
			} else {
				return "方向输入错误！";
			}

			if (type.equals("") || type.equals("1")) {
//				if (!SRS.isNull("c_accpkgsubject", "SubjectName", km, "")) {
//					km = AST.TextKey(km);
//				} else {
//					km = "('" + km + "')";
//				}

				sql += " and subjectname in " + km;
			} else if (type.equals("2")) {
				// ///////////////////////////////////////
//				if (!SRS.isNull("c_accpkgsubject", "SubjectFullName", km, "")) {
//					km = AST.TextKey(km);
//				} else {
//					km = "('" + km + "')";
//				}

				sql += " and SubjectID in (select SubjectID from c_accpkgsubject where AccPackageID='"
						+ pkgid + "' and SubjectFullName in " + km + ")";
				// ///////////////////////////////////////
			} else if (type.equals("3")) {
				sql += "and subjectid = " + km;
			}

			//org.util.Debug.prtOut("getKmdzs:" + sql);
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, pkgid);
			ps.setString(i++, proid);
			rs = ps.executeQuery();
			i = 0;
			while (rs.next()) {
				i++;
				if (i > 1) {
					result = "上年科目调整数出错！";
					break;
				}
				result = CHF.showNull(rs.getString(1));
			}

			if("".equals(result)){
				result = "0";//result = "该科目既不是标准科目，也不是用户科目，请修改科目名";
			}
			//org.util.Debug.prtOut("getNcs result:" + result);
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}

	}

	// 取审计单位信息
	private String getSjdwxx() throws Exception {
		String result = "";
		String sql = "";
		String sql1 = "";
		
		PreparedStatement ps = null;
		PreparedStatement ps1 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		java.text.SimpleDateFormat dateformat = new java.text.SimpleDateFormat(
		"yyyy年MM月dd日");
		java.util.Date currentdate = new java.util.Date();
		/*
		 * currentdate = rs.getDate("date1");
					if (currentdate != null)
						result = CHF.showNull(dateformat.format(currentdate));
		 * */
		try {
			
			String xm = CHF.showNull(request.getParameter("xm"));
//			xm = new String(xm.getBytes("ISO8859-1"));
	//		sql = "select * from k_customer where DepartID = (select AuditDept from z_project where ProjectID = ?)";
			sql = "select a.*,b.standbyname stby from k_customer a, z_project b where DepartID=AuditDept and ProjectID = ? ";
			//添加取函证联系人
			sql1 = "select value from k_userdef where contrastid = 'common' and name='函证联系人' and property = '"+proid+"'";
			ps = conn.prepareStatement(sql);
			ps1 = conn.prepareStatement(sql1);
			int i = 1;
			ps.setString(i++, proid);
			rs = ps.executeQuery();
			rs1 = ps1.executeQuery();
			if (rs.next()) {
				if (xm.equals("单位名称")) {
					if(CHF.showNull(rs.getString("stby")).equals("")){
						result = CHF.showNull(rs.getString("DepartName"));
					}else{
						result = CHF.showNull(rs.getString("stby"));
					}
				} else if (xm.equals("单位英文名称")) {
					result = CHF.showNull(rs.getString("DepartEnName"));
				} else if (xm.equals("单位地址")) {
					result = CHF.showNull(rs.getString("Address"));
				} else if (xm.equals("单位电话")) {
					result = CHF.showNull(rs.getString("Phone"));
				} else if (xm.equals("法人代表")) {
					result = CHF.showNull(rs.getString("Corporate"));
				} else if (xm.equals("国税号")) {
					result = CHF.showNull(rs.getString("CountryCess"));
				} else if (xm.equals("地税号")) {
					result = CHF.showNull(rs.getString("TerraCess"));
				} else if (xm.equals("企业代码")) {
					result = CHF.showNull(rs.getString("EnterpriseCode"));
				} else if (xm.equals("成立日期")) {
					if(!"".equals(rs.getString("DepartDate"))){
					currentdate = rs.getDate("DepartDate");
					if (currentdate != null)
						result = CHF.showNull(dateformat.format(currentdate));
					}
				//	result = CHF.showNull(rs.getString("DepartDate"));
				} else if (xm.equals("注册地址")) {
					result = CHF.showNull(rs.getString("LoginAddress"));
				} else if (xm.equals("单位联系人")) {
					result = CHF.showNull(rs.getString("LinkMan"));
				} else if (xm.equals("经营期限起")) {
					if(!"".equals(rs.getString("BusinessBegin"))){
					currentdate = rs.getDate("BusinessBegin");
					if (currentdate != null)
						result = CHF.showNull(dateformat.format(currentdate));
					}
				//	result = CHF.showNull(rs.getString("BusinessBegin"));
				} else if (xm.equals("经营期限至")) {
					if(!"".equals(rs.getString("BusinessEnd"))){
					currentdate = rs.getDate("BusinessEnd");
					if (currentdate != null)
						result = CHF.showNull(dateformat.format(currentdate));
					}
				//	result = CHF.showNull(rs.getString("BusinessEnd"));
				} else if (xm.equals("经营范围")) {
					result = CHF.showNull(rs.getString("BusinessBound"));
				} else if (xm.equals("备注")) {
					result = CHF.showNull(rs.getString("Remark"));
				} else if (xm.equals("电子邮件")) {
					result = CHF.showNull(rs.getString("Email"));
				} else if (xm.equals("传真号码")) {
					result = CHF.showNull(rs.getString("fax"));
				} else if (xm.equals("邮政编码")) {
					result = CHF.showNull(rs.getString("postalcode"));
				} else if (xm.equals("函证联系人")) {
					if(rs1.next()) {
						result = CHF.showNull(rs1.getString("value"));
					} else {
						result = CHF.showNull(rs.getString("LinkMan"));	
					}
				
				}//传真号码 邮政编码
				// else if (xm.equals("行业类型")) {
				// result = CHF.showNull(rs.getString("VocationID"));
				// }

				else {
					result = "审计单位返回值参数输入错误!";
				}
			} else {
				result = "没有此审计单位!";
			}

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(rs1);
			DbUtil.close(ps);
			DbUtil.close(ps1);
		}

	}

	// 取审计单位自定义信息
	private String getSjdwzdyxx() throws Exception {
		String result = "";
		String sql = "";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			
			String xm = CHF.showNull(request.getParameter("xm"));
			xm = new String(xm.getBytes("ISO8859-1"));
			if (xm.equals("")) {
				return "自定义信息名称不能为空!";
			}
			sql = "select value from k_userdef where  ContrastID = (select AuditDept from z_project where ProjectID = ?) and name = ?";
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, proid);
			ps.setString(i++, xm);
			rs = ps.executeQuery();
			if (rs.next()) {
				result = CHF.showNull(rs.getString(1));
			} else {
				result = "没有此自定义信息!";
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}

	}

	// 取被审单位信息
	private String getBsjdwxx() throws Exception {
		String result = "";
		String sql = "";
		String sql1 = "";
		String sql2 = "";
		
		PreparedStatement ps = null;
		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		java.text.SimpleDateFormat dateformat = new java.text.SimpleDateFormat(
		"yyyy年MM月dd日");
		java.util.Date currentdate = new java.util.Date();
		try {
			
			String xm = CHF.showNull(request.getParameter("xm"));
//			//System.out.println("xm:"+xm);
//			xm = new String(xm.getBytes("ISO8859-1"));			
//			//System.out.println("xm:"+xm);
			String taskcode = CHF.showNull(request.getParameter("taskcode"));
			if (request.getParameter("curTaskCode") != null) {
				taskcode = CHF.showNull(request.getParameter("curTaskCode"));
			}

//			taskcode = new String(taskcode.getBytes("ISO8859-1"));
			//org.util.Debug.prtOut("getBsjdwxx taskcode :="+taskcode);
			
			ReportService report=new ReportService(conn);
			String customerid="";
			if (report.isReportProject(proid)>0){
				//是合并报表项目，则得到
				customerid=report.getCustomeridByTaskid(proid, "taskcode", taskcode);
			}else{
				//不是
				customerid=report.getCustomeridByProjectid(proid);
			}
			
			
			sql = "select * from k_customer where DepartID = "+customerid;
			sql1 = "select customerid,position,group_concat(name) as name  from asdb.k_manager where customerid="+customerid+" and position=? group by position ";
			//org.util.Debug.prtOut("getBsjdwxx sql :="+sql);
			ps = conn.prepareStatement(sql);
			ps1 = conn.prepareStatement(sql1);
//			int i = 1;
//			ps.setString(i++, proid);
			rs = ps.executeQuery();
			if (rs.next()) {
				if (xm.equals("被审单位名称")) {
					String shortName = String.valueOf(UTILSysProperty.SysProperty.get("底稿中是否使用被审单位简称"));
					if("是".equals(shortName)) {
						sql2 = "select Value from k_userdef where Name='单位简称' and Property='com_cust' and contrastid='" + customerid + "' and Value is not null and Value <> ''";
						System.out.println("sql2="+sql2);
						
						ps2 = conn.prepareStatement(sql2);
						rs2 = ps2.executeQuery();
						if(rs2.next()) {
							result = CHF.showNull(rs2.getString("Value"));
							if ("".equals(result)){
								//如果为空，还是用全称替代简称
								result = CHF.showNull(rs.getString("DepartName"));
							}
						} else {
							result = CHF.showNull(rs.getString("DepartName"));
						}
					} else {
						result = CHF.showNull(rs.getString("DepartName"));
					}
				} else if (xm.equals("被审单位全称")) {
					result = CHF.showNull(rs.getString("DepartName"));
				}else if (xm.equals("被审单位简称")) {
					sql2 = "select Value from k_userdef where Name='单位简称' and Property='com_cust' and contrastid='" + customerid + "' and Value is not null and Value <> ''";
					ps2 = conn.prepareStatement(sql2);
					rs2 = ps2.executeQuery();
					if(rs2.next()) {
						result = CHF.showNull(rs2.getString("Value"));
					} else {
						result = "";
					}
					
				} else if (xm.equals("被审单位编号")) {
					if ("".equals(CHF.showNull(rs.getString("departcode")))){
						result = CHF.showNull(rs.getString("departid"));
					}else{
						result = CHF.showNull(rs.getString("departcode"));
					}
				}else if (xm.equals("被审单位英文名称")) {
					result = CHF.showNull(rs.getString("DepartEnName"));
				}else if (xm.equals("被审单位地址")) {
					result = CHF.showNull(rs.getString("Address"));
				} else if (xm.equals("被审单位电话")) {
					result = CHF.showNull(rs.getString("Phone"));
				} else if (xm.equals("法人代表")) {
					result = CHF.showNull(rs.getString("Corporate"));
				} else if (xm.equals("国税号")) {
					result = CHF.showNull(rs.getString("CountryCess"));
				} else if (xm.equals("地税号")) {
					result = CHF.showNull(rs.getString("TerraCess"));
				} else if (xm.equals("企业代码")) {
					result = CHF.showNull(rs.getString("EnterpriseCode"));
				} else if (xm.equals("成立日期")) {
					if(!"".equals(rs.getString("DepartDate"))){
					currentdate = rs.getDate("DepartDate");
					if (currentdate != null)
						result = CHF.showNull(dateformat.format(currentdate));
					}
				//	result = CHF.showNull(rs.getString("DepartDate"));
				} else if (xm.equals("注册地址")) {
					result = CHF.showNull(rs.getString("LoginAddress"));
				} else if (xm.equals("单位联系人")) {
					result = CHF.showNull(rs.getString("LinkMan"));
				} else if (xm.equals("经营期限起")) {
					if(!"".equals(rs.getString("BusinessBegin"))){
					currentdate = rs.getDate("BusinessBegin");
					if (currentdate != null)
						result = CHF.showNull(dateformat.format(currentdate));
					}
				//	result = CHF.showNull(rs.getString("BusinessBegin"));
				} else if (xm.equals("经营期限至")) {
					if(!"".equals(rs.getString("BusinessEnd"))){
					currentdate = rs.getDate("BusinessEnd");
					if (currentdate != null)
						result = CHF.showNull(dateformat.format(currentdate));
					}
				//	result = CHF.showNull(rs.getString("BusinessEnd"));
				} else if (xm.equals("经营范围")) {
					result = CHF.showNull(rs.getString("BusinessBound"));
				} else if (xm.equals("备注")) {
					result = CHF.showNull(rs.getString("Remark"));
				} else if (xm.equals("电子邮件")) {
					result = CHF.showNull(rs.getString("Email"));
				} else if (xm.equals("行业名称")) {
//					String str = CHF.showNull(rs.getString("VocationID")); // 要中文
//					sql = "select industryname from k_industry where industryid='"
//							+ str + "'";
//					ps = conn.prepareStatement(sql);
//					rs = ps.executeQuery();
//					if (rs.next()) {
						result = CHF.showNull(rs.getString("hylx"));
//					}
				} else if (xm.equals("传真号码")) {
					result = CHF.showNull(rs.getString("fax"));
				} else if (xm.equals("邮政编码")) {
					result = CHF.showNull(rs.getString("postalcode"));
				}//传真号码 邮政编码
				else if (xm.equals("营业执照号")) {
					result = CHF.showNull(rs.getString("BPR"));
				} else if (xm.equals("纳税人识别号")) {
					result = CHF.showNull(rs.getString("taxpayer"));
				} else if (xm.equals("注册资本")) {
					result = CHF.showNull(rs.getString("register"));
				} else if (xm.equals("货币类型")) {
					result = CHF.showNull(rs.getString("curname"));
				} else if (xm.equals("股东成员")) {
					result = CHF.showNull(rs.getString("stockowner"));
				} 
				 else if (xm.equals("经营期限")) {
						if("长期经营".equals(rs.getString("BusinessBegin"))) {
							result = "长期经营";
						} else {
							result = CHF.showNull(rs.getString("BusinessBegin"))+" 至 "+CHF.showNull(rs.getString("BusinessEnd"));
						}
					 }
				 else if(xm.equals("董事长")) {
					 ps1.setString(1, "董事长");
					 rs1=ps1.executeQuery();
					 if(rs1.next()) {
						 result = CHF.showNull(rs1.getString("name"));
					 }
				 } 
				 else if(xm.equals("总经理")) {
					 ps1.setString(1, "总经理");
					 rs1=ps1.executeQuery();
					 if(rs1.next()) {
						 result = CHF.showNull(rs1.getString("name"));
					 }
				 }
//				 else if(xm.equals("法人代表")) {
//					 ps1.setString(1, "法人代表");
//					 rs1=ps1.executeQuery();
//					 if(rs1.next()) {
//						 result = CHF.showNull(rs1.getString("name"));
//					 }
//				 }
				 else if(xm.equals("副董事长")) {
					 ps1.setString(1, "副董事长");
					 rs1=ps1.executeQuery();
					 if(rs1.next()) {
						 result = CHF.showNull(rs1.getString("name"));
					 }
				 }
				 else if(xm.equals("副总经理")) {
					 ps1.setString(1, "副总经理");
					 rs1=ps1.executeQuery();
					 if(rs1.next()) {
						 result = CHF.showNull(rs1.getString("name"));
					 }
				 }
				 else if(xm.equals("总会计师")) {
					 ps1.setString(1, "总会计师");
					 rs1=ps1.executeQuery();
					 if(rs1.next()) {
						 result = CHF.showNull(rs1.getString("name"));
					 }
				 }
				 else if(xm.equals("财务经理")) {
					 ps1.setString(1, "财务经理");
					 rs1=ps1.executeQuery();
					 if(rs1.next()) {
						 result = CHF.showNull(rs1.getString("name"));
					 }
				 }
				else {
					result = "被审单位返回值参数输入错误!";
				}
			} else {
				result = "没有此审计单位!";
			}

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(rs1);
            DbUtil.close(ps);
            DbUtil.close(ps1);
		}

	}

	// 取被审单位自定义信息
	private String getBsjdwzdyxx() throws Exception {
		String result = "";
		String sql = "";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			
			String xm = CHF.showNull(request.getParameter("xm"));
//			xm = new String(xm.getByte1"));
			if (xm.equals("")) {
				return "自定义信息名称不能为空!";
			}
			
			
			String taskcode = CHF.showNull(request.getParameter("taskcode"));
			if (request.getParameter("curTaskCode") != null) {
				taskcode = CHF.showNull(request.getParameter("curTaskCode"));
			}

//			taskcode = new String(taskcode.getByte1"));
			
			String opt = CHF.showNull(request.getParameter("opt"));
			if("1".equals(opt)){
				//合并报表当前客户
				sql = "select value from asdb.k_userdef where ContrastID =(select CustomerId from z_task a,asdb.k_customerrelation b where projectid='"+proid+"' and systemid=(select systemid from asdb.z_project where projectid='"+proid+"') and taskcode='"+taskcode+"' and Description=b.autoid) and name ='"+xm+"' ";
			}else if("2".equals(opt)){
				//合并报表当前客户的母公司
				sql = "select value from asdb.k_userdef where ContrastID =(select CustomerId from asdb.k_customerrelation where parentid=(select if(b.property like '2%',autoid,parentid) pid from z_task a,asdb.k_customerrelation b where projectid='"+proid+"' and systemid=(select systemid from asdb.z_project where projectid='"+proid+"') and taskcode='"+taskcode+"' and Description=b.autoid) and property like '0%')  and name ='"+xm+"' ";
			}else{
				//原来
				sql = "select value from k_userdef where  ContrastID = (select CustomerId from z_project where ProjectID = '"+proid+"') and name = '"+xm+"'";	
			}

			ps = conn.prepareStatement(sql);
//			int i = 1;
//			ps.setString(i++, proid);
//			ps.setString(i++, xm);
			rs = ps.executeQuery();
			if (rs.next()) {
				result = CHF.showNull(rs.getString(1));
			} else {
				result = "没有此自定义信息!";
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}

	}

	// 会计政策
	private String getKjzc() throws Exception {
		String result = "";
		String sql = "";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			
			String xm = CHF.showNull(request.getParameter("xm"));
//			xm = new String(xm.getByte1"));
			if ("".equals(xm)) {
				return "会计政策参数不能为空！";
			}
			sql = "select GROUP_CONCAT(policyvalue) policyvalue  from z_accountantpolicy where DepartID = (select CustomerId from z_project where ProjectID=?) "
					+ " and PolicyName like CONCAT('%',?,'%') group by policyname ";
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, proid);
			ps.setString(i++, xm);
			i = 1;
			rs = ps.executeQuery();
			if (rs.next()) {
				result = CHF.showNull(rs.getString(1));
			}
			if ("".equals(result))
				result = "会计政策参数不存在或输入错误！";
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
	}

	// 底稿信息
	private String getDgxx() throws Exception {
		String result = "";
		String sql = "";
		String sql1 = "";
		
		PreparedStatement ps = null;
		PreparedStatement ps1 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		java.text.SimpleDateFormat dateformat = new java.text.SimpleDateFormat(
				"yyyy-MM-dd");
		java.util.Date currentdate = new java.util.Date();
		try {
			

			String taskcode = CHF.showNull(request.getParameter("taskcode"));
			if (request.getParameter("curTaskCode") != null) {
				taskcode = CHF.showNull(request.getParameter("curTaskCode"));
			}

//			taskcode = new String(taskcode.getByte1"));
			if (taskcode.equals("")) {
//				return "索引号不能为空!";
				return "";
			}
			
			String xm = CHF.showNull(request.getParameter("xm"));
//			xm = new String(xm.getByte1"));
			sql = "select a.*,b.advice badvice,c.advice cadvice,d.advice dadvice,e.advice eadvice,name,year(CURRENT_DATE) Tyear, month(CURRENT_DATE) Tmonth,day(CURRENT_DATE) Tday from z_task a left join  k_user ON id=user0 left join (select * from (select * from z_auditadvice order by advicedate desc) a group by adviceType,projectid,taskid) b on b.projectid='"
					+ proid
					+ "' and a.taskid=b.taskid and b.adviceType='编制' left join (select * from (select * from z_auditadvice order by advicedate desc) a group by adviceType,projectid,taskid) c on c.projectid='"
					+ proid
					+ "' and a.taskid=c.taskid and c.adviceType='一级复核' left join (select * from (select * from z_auditadvice order by advicedate desc) a group by adviceType,projectid,taskid) d on d.projectid='"
					+ proid
					+ "' and a.taskid=d.taskid and d.adviceType='二级复核' left join (select * from (select * from z_auditadvice order by advicedate desc) a group by adviceType,projectid,taskid) e on e.projectid='"
					+ proid
					+ "' and a.taskid=e.taskid and e.adviceType='三级复核' where IsLeaf =1 and a.ProjectID='"
					+ proid + "' and TaskCode='" + taskcode + "' ";

			ps = conn.prepareStatement(sql);
		//	int i = 1;
			// ps.setString(i++, proid);
			// ps.setString(i++, taskcode);
			rs = ps.executeQuery();

			if (rs.next()) { 
				if (xm.equals("责任人")) {
					result = CHF.showNull(rs.getString("name"));
				} else if (xm.equals("编制人")) {
					result = getUserNameByUserID(CHF.showNull(rs.getString("User1")));
				} else if (xm.equals("编制日期")) {
					currentdate = rs.getDate("date1");
					if (currentdate != null)
						result = CHF.showNull(dateformat.format(currentdate));
					// result = CHF.showNull(rs.getString("date1"));
				} else if (xm.equals("编制意见")) {
					result = CHF.showNull(rs.getString("badvice"));
				} else if (xm.equals("一级复核人")) {
					
					result = getUserNameByUserID(CHF.showNull(rs.getString("User5")));
					if(result == null || "".equals(result))  //一审取不到就取二审，兼容以前的
						result = getUserNameByUserID(CHF.showNull(rs.getString("User2")));
					
				} else if (xm.equals("一级复核日期")) {
					currentdate = rs.getDate("date5");
					if(currentdate == null || "".equals(currentdate))  //一审取不到就取二审，兼容以前的
						currentdate = rs.getDate("date2");
					
					if (currentdate != null) 
						result = CHF.showNull(dateformat.format(currentdate));
				} else if (xm.equals("一级复核意见")) {
					result = CHF.showNull(rs.getString("cadvice"));
					if(result == null || "".equals(result))  //一审取不到就取二审，兼容以前的
						result = CHF.showNull(rs.getString("dadvice"));
				} else if (xm.equals("二级复核人")) {
					result = getUserNameByUserID(CHF.showNull(rs.getString("User2")));
				} else if (xm.equals("二级复核日期")) {
					currentdate = rs.getDate("date2");
					if (currentdate != null)
						result = CHF.showNull(dateformat.format(currentdate));
					// result = CHF.showNull(rs.getString("date2"));
				} else if (xm.equals("二级复核意见")) {
					result = CHF.showNull(rs.getString("dadvice"));
				} else if (xm.equals("三级复核人")) {
					result = getUserNameByUserID(CHF.showNull(rs.getString("User3")));
				} else if (xm.equals("三级复核日期")) {
					currentdate = rs.getDate("date3");
					if (currentdate != null)
						result = CHF.showNull(dateformat.format(currentdate));
					// result = CHF.showNull(rs.getString("date3"));
				} else if (xm.equals("三级复核意见")) {
					result = CHF.showNull(rs.getString("eadvice"));
				} else if (xm.equals("索引号")) {
					result = CHF.showNull(rs.getString("taskcode"));
				} else if (xm.equals("当前年")) {
					result = CHF.showNull(rs.getString("Tyear"));
				} else if (xm.equals("当前月")) {
					result = CHF.showNull(rs.getString("Tmonth"));
				} else if (xm.equals("当前日")) {
					result = CHF.showNull(rs.getString("Tday"));
				}  else if (xm.equals("责任一级复核人")) {
					//企审通的修改要用到责任一级复核人，E审通还没有修改，所以暂时返回空字符串
					result = "";
				} else if (xm.equals("责任二级复核人")) {
					//企审通的修改要用到责任一级复核人，E审通还没有修改，所以暂时返回空字符串
					result = "";
				} else if (xm.equals("责任三级复核人")) {
					//企审通的修改要用到责任一级复核人，E审通还没有修改，所以暂时返回空字符串
					result = "";
				} 
				
				else if (xm.equals("编制说明")) {
					result = CHF.showNull(rs.getString("taskContent"));
				}
				
				else {
					result = "底稿返回值参数输入错误!";
				}

			} else {
				result = "没有此底稿单位!";
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(rs1);
            DbUtil.close(ps);
            DbUtil.close(ps1);
		}
	}


	private String getUserNameByUserID(String userID)  throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String result = "";
			if(!"".equals(userID)){
				String sql = "select * from asdb.k_user where id = "+userID;
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				if(rs.next()){
					result = rs.getString("name");
				}
			}			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
	}
//取项目信息
	private String getXmxx() throws Exception {
		String result = "";
		String sql = "";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		java.text.SimpleDateFormat dateformat = new java.text.SimpleDateFormat("yyyy-MM-dd");
		java.text.SimpleDateFormat dateformat1 = new java.text.SimpleDateFormat("yyyy年MM月dd日");
		Locale localeEN = new Locale("en", "US" );
		java.text.DateFormat fullDateFormatEN =java.text.DateFormat.getDateInstance(java.text.DateFormat.LONG,localeEN);
		java.util.Date currentdate = new java.util.Date();
		try {
			
			String xm = CHF.showNull(request.getParameter("xm"));
//			xm = new String(xm.getByte1"));

			sql = "select *,substring(accpackageid,7,4) accpyear,ADDDATE(audittimebegin,-1) olddate,CONCAT(substring(accpackageid,7,4)-1) oldyear \n"
				+", userrole \n"
				+"from z_project \n"
				
				+", \n"
				+"( \n"
				+"select group_concat(userrole) as userrole \n"
				+" from  \n"
				+"	( \n"
				+"select concat(b.name,\"(\",a.role,\")\") as userrole,a.autoid  \n"
				+"from z_auditpeople a,k_user b \n"
				+"where projectid="	+ proid + " \n"
				+"and a.userid=b.id \n"
				+"order by autoid \n"
				+"	)b \n"
				+")b \n"
				+"where projectid="	+ proid + "";
			
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				if (xm.equals("审计项目")) {
					result = CHF.showNull(rs.getString("ProjectName"));
				} else if (xm.equals("审计类型")) {
					result = CHF.showNull(rs.getString("AuditPara"));
				} else if (xm.equals("立项日期")) {
					currentdate = rs.getDate("ProjectCreated");
					if (currentdate != null)
						result = CHF.showNull(dateformat.format(currentdate));
				//	result = CHF.showNull(rs.getString("ProjectCreated"));
				} else if (xm.equals("审计区间起")) {
					currentdate = rs.getDate("AuditTimeBegin");
					if (currentdate != null)
						result = CHF.showNull(dateformat.format(currentdate));
				//	result = CHF.showNull(rs.getString("AuditTimeBegin"));
				} else if (xm.equals("审计区间至")) {
					currentdate = rs.getDate("AuditTimeEnd");
					if (currentdate != null)
						result = CHF.showNull(dateformat.format(currentdate));
				//	result = CHF.showNull(rs.getString("AuditTimeEnd"));
				} else if (xm.equals("截止日期")) {
					currentdate = rs.getDate("AuditTimeEnd");
					if (currentdate != null)
						result = CHF.showNull(dateformat1.format(currentdate));
				//	result = CHF.showNull(rs.getString("AuditTimeEnd"));
				} else if (xm.equals("英文截止日期")) {
					currentdate = rs.getDate("AuditTimeEnd");
					if (currentdate != null)
						result = CHF.showNull(fullDateFormatEN.format(currentdate));
				//	result = CHF.showNull(rs.getString("AuditTimeEnd"));
				} else if (xm.equals("项目年度")) {
					result = CHF.showNull(rs.getString("accpyear"));
				} else if (xm.equals("上年截止日")) {
					result = CHF.showNull(rs.getString("olddate"));
				} else if (xm.equals("上年年份")) {
					result = CHF.showNull(rs.getString("oldyear"));
				} else if (xm.equals("计划结束日期")) {
					result = CHF.showNull(rs.getString("projectend"));
				}  else if (xm.equals("项目成员")) {
					result = CHF.showNull(rs.getString("userrole"));
				} else if (xm.equals("审计发现")) {
					DbUtil.close(rs);
					DbUtil.close(ps);
					
					//取审计发现吧
					
					sql="select area,memo,standant,advice,confirm from z_discovery \n"
							+"where projectid="+proid+" \n"
							+"and upload='是'";
					
					//System.out.println("sql="+sql);
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					int i=0;
					String advice,confirm;
					while (rs.next()){
						result += (i++) +"、在对["+ CHF.showNull(rs.getString("area"))+"]的审计过程中，遇到审计情况为["
							+ CHF.showNull(rs.getString("memo"))+"]，对照["
							+ CHF.showNull(rs.getString("standant"))+"]审计标准";
						
						advice=CHF.showNull(rs.getString("advice"));
						confirm=CHF.showNull(rs.getString("confirm"));
						
						if (!"".equals(advice)){
							result +=",经过讨论，我们得出如下审计意见："+advice;
						}
						if (!"".equals(confirm)){
							result +=",最终，我们得出如下审计结论："+confirm;
						}
						result+="\n";
						
					}
					if (i==0){
						result="无需要上报的审计发现";
					}
					
				//IPO项目专用	
				}else if (xm.equals("上年年末")) {
					result = CHF.showNull(rs.getString("oldyear"))+"-12-31";
				}
				else if (xm.equals("上年年初")) {
					result = CHF.showNull(rs.getString("oldyear"))+"-01-01";
				}
				else if (xm.equals("上2年年末")) {
					result = (Integer.parseInt(CHF.showNull(rs.getString("oldyear")))-1)+"-12-31";
				}
				else if (xm.equals("上2年年份")) {
					result = (Integer.parseInt(CHF.showNull(rs.getString("oldyear")))-1)+"";
				}
				else if (xm.equals("上2年年初")) {
					result = (Integer.parseInt(CHF.showNull(rs.getString("oldyear")))-1)+"-01-01";
				}
				else if (xm.equals("上3年年末")) {
					result = (Integer.parseInt(CHF.showNull(rs.getString("oldyear")))-2)+"-12-31";
				}
				else if (xm.equals("上3年年份")) {
					result = (Integer.parseInt(CHF.showNull(rs.getString("oldyear")))-2)+"";
				}
				else if (xm.equals("上3年年初")) {
					result = (Integer.parseInt(CHF.showNull(rs.getString("oldyear")))-2)+"-01-01";
				}
				else {
					result = "项目返回值参数输入错误!";
				}
			} else {
				result = "没有此项目信息!";
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
	}

//	取被审单位自定义信息
	private String getXmzdyxx() throws Exception {
		String result = "";
		String sql = "";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			
			String xm = CHF.showNull(request.getParameter("xm"));
//			xm = new String(xm.getBytes("ISO8859-1"));
			if (xm.equals("")) {
				return "自定义信息名称不能为空!";
			}
			sql = "select value from k_userdef where  ContrastID = 'common' and Property = ? and name = ? ";
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, proid);
			ps.setString(i++, xm);
			rs = ps.executeQuery();
			if (rs.next()) {
				result = CHF.showNull(rs.getString(1));
			} else {
				result = "没有此自定义信息!";
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
	}
	
	
	/**
	 * 自定义函数，有点象取列公式覆盖的效果，差别有2： 1、无论返回多行都只取一行； 2、覆盖当前公式
	 * =取自定义函数(9999,"期末调整借.帐表差异＋期末重分类借.帐表差异","&科目名称=原材料")
	 * @return String
	 * @throws Exception
	 */
	private String getZdyHs() throws Exception {
		String s = request.getParameter("areaid");
		String field = request.getParameter("field");
		String strsql = "";
		String strLimit = request.getParameter("limit");

		String departID=this.pkgid.substring(0,6);
		String projectID=this.proid;
		
		String allfield = CHF.showNull(request.getParameter("allfield"));
		System.out.println(field+"|allfield=|"+allfield);
		
		
		Map args=new TreeMap();//保存当前参数
		
		Enumeration enum1 = request.getParameterNames();
		while (enum1.hasMoreElements()) {
			String paramName = (String) enum1.nextElement();
			String paramValue = request.getParameter(paramName);
			args.put(paramName, paramValue);
		
		}
		
		args.put("curDepartId",departID);
		args.put("curProjectid",projectID);
		args.put("curAccPackageID",this.pkgid);
		args.put("curPackageid",this.pkgid);
		args.put("curProjectid",projectID);
		
		int iLimit = 0, j = 0;
		try {
			iLimit = Integer.parseInt(strLimit);
		} catch (Exception e) {
		}

		if (iLimit <= 0)
			iLimit = 1; // 1表示第一行

		// 缓存返回的数据
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		String strResult = "0.00";

		if (s != null && field != null) {
			try {
				
				String mysql = "select ifnull(evalue,fieldvalue) from k_areafunctionfields \n"
					+" where areaid='"+s+"' and fieldvalue=? \n";
				ps = conn.prepareStatement(mysql);
				ps.setString(1,allfield);
				rs = ps.executeQuery();
				if(rs.next()) {//有对应列取对应列
					allfield = rs.getString(1);
				}
				DbUtil.close(rs);
	            DbUtil.close(ps);	
	            System.out.println(field+":"+allfield);
				
				
//				查找出当前底稿类型
				  String auditTypeID="";
				  String s4="select audittype from z_project where projectid = ?";
				  ps = conn.prepareStatement(s4);
				  ps.setString(1,projectID);
				  rs = ps.executeQuery();
				  
				  if(rs.next()){
					  auditTypeID=rs.getString(1);
				  }else{
					  throw new Exception("系统找不到项目［"+projectID+"］！");
				  }
				
				
				String s3 = "select strsql,classPath from k_areafunction where id= ? and typeid in( 0,?) order by typeid desc";
				ps = conn.prepareStatement(s3);
			    ps.setString(1,s);
			    ps.setString(2,auditTypeID);
				rs = ps.executeQuery();
				
			    String classPathStr="";
			    args.put("_id",s);
			    args.put("_typeid",auditTypeID);
				
				if (rs.next()) {
					strsql = rs.getString("strsql");
//					处理类
				    classPathStr=rs.getString("classPath");
					
					// //org.util.Debug.prtOut("strsqlinit="+strsql);
					String t1[] = UTILString.getVaribles(strsql);
					String strParam = "";
					
					if (t1!=null){
					
						for (int i = 0; i < t1.length; i++) {
							strParam = (String) request.getParameter(t1[i]);
							// //org.util.Debug.prtOut("t1[i]="+t1[i]);
							if (strParam == null || strParam.equals("")) {
								// 取不到就到SESSION中去取，只支持：
								// String proid = "";String pkgid = "";String userid
								// = "";String username = "";
								if (t1[i].equals("curProjectid")) {
									strParam = this.proid;
								} else if (t1[i].equals("curPackageid")
										|| t1[i].equals("curAccPackageID")) {
									strParam = this.pkgid;
								} else if (t1[i].equals("user")) {
									strParam = this.userid;
								} else if (t1[i].equals("username")) {
									strParam = this.username;
								}
								if (strParam == null || strParam.equals("")) {
									// 还是取不到，那就说明公式访问参数设置有误
//									throw new Exception("公式访问参数设置有误，出现" + t1[i]+ "未赋值!");
									strParam = "";
								}
							} else {
								// 转换中文
	//							strParam = new String(strParam.getBytes("ISO8859-1");	
							}
							// //org.util.Debug.prtOut("strParam="+strParam);
							strsql = strsql.replaceAll("\\$\\{" + t1[i] + "\\}",
									strParam);
							args.put(t1[i],strParam);
							// //org.util.Debug.prtOut("strsql="+strsql);
						} // FOR
					}
					//org.util.Debug.prtOut("strsql=" + strsql);
				} else {
					throw new Exception("未找到areaid=" + String.valueOf(s)
							+ "设置的列公式");
				}

				String tempTable = "";
				
				 //执行指定的SQL
			    //如果没有处理类，就直接执行SQL，否则，SQL就是处理类的参数。
			    if("".equals(classPathStr)){
			    	ps = conn.prepareStatement(strsql);
			        rs = ps.executeQuery();	
			    }else{
			    	
			    	//参数是中文时，追加英文参数
			    	Enumeration enum2 = request.getParameterNames();
			    	while (enum2.hasMoreElements()) {
						String paramName = (String) enum2.nextElement();
						String paramValue = request.getParameter(paramName);
						
						String sql = "select ifnull(evalue,fieldvalue) from k_areafunctionparams where areaid='"+s+"' and fieldvalue='"+paramName+"'";
						ps = conn.prepareStatement(sql);
						rs = ps.executeQuery();
						if(rs.next()) {//有对应列取对应列
							paramName = rs.getString(1);
						}
						DbUtil.close(rs);
			            DbUtil.close(ps);	
			            args.put(paramName, paramValue);
					}
			    	
			    	if("9999".equals(s)){
			    		String type = CHF.showNull((String)args.get("科目类型"));
			    		if("".equals(type)){
			    			//自定义取数：9999公式的科目类型默认为一级
			    			//args.put("科目类型", "一级");
			    		}
			    	}
			    	
			    	AreaFunction af=(AreaFunction)Class.forName("com.matech.audit.service.function."+classPathStr).newInstance();
			    	rs=af.process(null,request,null,conn,args);
			    	tempTable = af.getTempTable();
			    	
			    }
				
			    //有多条时加总
			    double result = 0.00;
				while (rs.next()) { //j++ < iLimit && 
					// 只取一行，不管其他
					try {
						if(allfield.indexOf("＋")>-1 || allfield.indexOf("+")>-1
							|| allfield.indexOf("－")>-1 || allfield.indexOf("-")>-1
							|| allfield.indexOf("＊")>-1 || allfield.indexOf("*")>-1
							|| allfield.indexOf("／")>-1 || allfield.indexOf("/")>-1
							|| allfield.indexOf("（")>-1 || allfield.indexOf("(")>-1
							|| allfield.indexOf("）")>-1 || allfield.indexOf(")")>-1
							|| field.indexOf("＋")>-1 || field.indexOf("+")>-1
							|| field.indexOf("－")>-1 || field.indexOf("-")>-1
							|| field.indexOf("＊")>-1 || field.indexOf("*")>-1
							|| field.indexOf("／")>-1 || field.indexOf("/")>-1
							|| field.indexOf("（")>-1 || field.indexOf("(")>-1
							|| field.indexOf("）")>-1 || field.indexOf(")")>-1
						){ //有运算符，取d0
							result += rs.getDouble("d0");	
						}else{
							if(!"".equals(allfield)){
								if(allfield.equals(field)){
									result += rs.getDouble(field);	
								}else{
									result += rs.getDouble(allfield);
								}
							}else{
								result += rs.getDouble(field);
							}
						}
					} catch (Exception e) {
						if(j++ < iLimit){
							if(!"".equals(allfield)){
								if(allfield.equals(field)){
									strResult = CHF.showNull(rs.getString(field));	
								}else{
									strResult = CHF.showNull(rs.getString(allfield));
								}
							}else{
								strResult = CHF.showNull(rs.getString(field));
							}
						}else{
							break;
						}
					}
					
				}
				if(result != 0.00){
					strResult = CHF.showMoney3(String.valueOf(result));
				}
				if(!"".equals(tempTable)){
		    		System.out.println("临时表 tempTable:"+tempTable);
		    		new com.matech.audit.work.subjectentry.SubjectEntry(conn).DelTempTable(tempTable);
		    	}
				
			} catch (Exception e) {
				e.printStackTrace();
				strResult = e.getMessage();
			} finally {
				DbUtil.close(rs);
	            DbUtil.close(ps);				
			}
		}

		return strResult;
	}

	
	private String getZdyxx() throws Exception {
		return "";
	}
	
//	 核算本期期末数
	private String getHsBqqms() throws Exception {
		DbUtil.checkConn(conn);
		String result = "";
		String sql = "";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String km = CHF.showNull(request.getParameter("km"));
			if (km.equals("")) {
				return "科目不能为空!";
			}
			
			/**
			 *科目对照  
			 */
			String skm = changeSubjectName(km);
			if(!"".equals(skm)){
				km = skm;
			}
			
//			km = new String(km.getByte1"));

			String hs = CHF.showNull(request.getParameter("hs"));
			if (hs.equals("")) {
				return "核算项目不能为空!";
			}
//			hs = new String(hs.getByte1"));

			String kmtype = CHF.showNull(request.getParameter("type"));
//			kmtype = new String(kmtype.getByte1"));
			String hstype = CHF.showNull(request.getParameter("hstype"));
//			hstype = new String(hstype.getByte1"));

			String submonth = CHF.showNull(request.getParameter("qs"));
			if (submonth.equals("")) {
				return "期数不能为空!";
			}
//			submonth = new String(submonth.getByte1"));

			String fx = CHF.showNull(request.getParameter("fx"));
			if (fx.equals("")) {
				return "方向不能为空!";
			}
//			fx = new String(fx.getByte1"));

			
			if (fx.equals("0")) {
				sql = "select if(direction=0,(Balance),direction*Balance) from c_assitementryacc where SubMonth=? and AccPackageID=? ";
			} else if (fx.equals("1")) {
				sql = "select DebitBalance from c_assitementryacc where SubMonth=? and AccPackageID=? ";
			} else if (fx.equals("-1")) {
				sql = "select (-1)*CreditBalance from c_assitementryacc where SubMonth=? and AccPackageID=? ";
			} else {
				return "方向输入错误！";
			}

			if (kmtype.equals("") || kmtype.equals("1")) {
				if (!SRS.isNull("c_accpkgsubject", "SubjectName", km, "")) {
					km = SRS.SubjectResult(km);
				} else {
					km = "('" + km + "')";
				}

				sql += " and AccID =(select DISTINCT subjectid from c_accpkgsubject where  subjectname in "
						+ km + " and AccPackageID='" + pkgid + "') ";
			} else if (kmtype.equals("2")) {
				if (!SRS.isNull("c_accpkgsubject", "SubjectFullName", km, "")) {
					km = SRS.SubjectResult(km);
				} else {
					km = "('" + km + "')";
				}

				sql += " and AccID =(select DISTINCT subjectid from c_accpkgsubject where  subjectfullname in "
						+ km + "  and AccPackageID='" + pkgid + "')";
			} else if (kmtype.equals("3")) {
				sql += " and AccID=" + km;
			} else {
				return "科目类型输入错误！";
			}
			if (hstype.equals("") || hstype.equals("1")) {
				if (!SRS.isNull("c_assitem", "000", hs, km)) {
					hs = SRS.TextKey(hs);
				} else {
					hs = "('" + hs + "')";
				}

				sql += " and AssItemName in " + hs;
			} else if (hstype.equals("2")) {
				if (!SRS.isNull("c_assitem", "111", hs, km)) {
					hs = SRS.TextKey(hs);
				} else {
					hs = "('" + hs + "')";
				}

				sql += " and AssItemID = (select DISTINCT AssItemID from c_assitem where  asstotalname in "
						+ hs + " and AccPackageID = '" + pkgid + "') ";
			} else if (hstype.equals("3")) {
				sql += " and assitemid= '" + hs + "'";
			} else {
				return "核算项目类型输入错误！";
			}

			//org.util.Debug.prtOut("getHsBqqms:" + sql);
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, submonth);
			ps.setString(i++, pkgid);
			// ps.setString(i++, km);
			// ps.setString(i++, hs);
			// ps.setString(i++, subname);
			rs = ps.executeQuery();

			i = 0;
			while (rs.next()) {
				i++;
				if (i > 1) {
					result = "核算本期期末数出错！";
					break;
				}
				result = CHF.showNull(rs.getString(1));
			}
			
			if("".equals(result)){
				result="0";//result = "科目既不是标准科目，也不是用户科目或核算不是用户核算，请修改";
			}
			//org.util.Debug.prtOut("getNcs result:" + result);
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
	}

	// 核算本期期末数(上年)
	private String getHsBqqmsGo() throws Exception {
		DbUtil.checkConn(conn);
		String result = "";
		String sql = "";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		pkgid = SRS.getNewPackageID(pkgid);
		try {
			if("该单位没有去年的帐套!".equals(pkgid)){
				return "0";
			}
			String km = CHF.showNull(request.getParameter("km"));
			if (km.equals("")) {
				return "科目不能为空!";
			}
			/**
			 *科目对照  
			 */
			String skm = changeSubjectName(km);
			if(!"".equals(skm)){
				km = skm;
			}
			
//			km = new String(km.getByte1"));

			String hs = CHF.showNull(request.getParameter("hs"));
			if (hs.equals("")) {
				return "核算项目不能为空!";
			}
//			hs = new String(hs.getByte1"));

			String kmtype = CHF.showNull(request.getParameter("type"));
//			kmtype = new String(kmtype.getByte1"));
			String hstype = CHF.showNull(request.getParameter("hstype"));
//			hstype = new String(hstype.getByte1"));

			String submonth = CHF.showNull(request.getParameter("qs"));
			if (submonth.equals("")) {
				return "期数不能为空!";
			}
//			submonth = new String(submonth.getByte1"));

			String fx = CHF.showNull(request.getParameter("fx"));
			if (fx.equals("")) {
				return "方向不能为空!";
			}
//			fx = new String(fx.getByte1"));

			
			if (fx.equals("0")) {
				sql = "select if(direction=0,(Balance),direction*Balance) from c_assitementryacc where SubMonth=? and AccPackageID=? ";
			} else if (fx.equals("1")) {
				sql = "select DebitBalance from c_assitementryacc where SubMonth=? and AccPackageID=? ";
			} else if (fx.equals("-1")) {
				sql = "select (-1)*CreditBalance from c_assitementryacc where SubMonth=? and AccPackageID=? ";
			} else {
				return "方向输入错误！";
			}

			if (kmtype.equals("") || kmtype.equals("1")) {
				if (!SRS.isNull("c_accpkgsubject", "SubjectName", km, "")) {
					km = SRS.SubjectResult(km);
				} else {
					km = "('" + km + "')";
				}

				sql += " and AccID =(select DISTINCT subjectid from c_accpkgsubject where  subjectname in "
						+ km + "and AccPackageID='" + pkgid + "')  ";
			} else if (kmtype.equals("2")) {
				if (!SRS.isNull("c_accpkgsubject", "SubjectFullName", km, "")) {
					km = SRS.SubjectResult(km);
				} else {
					km = "('" + km + "')";
				}

				sql += " and AccID =(select DISTINCT subjectid from c_accpkgsubject where  subjectfullname in "
						+ km + "and AccPackageID='" + pkgid + "')  ";
			} else if (kmtype.equals("3")) {
				sql += " and AccID=　" + km;
			} else {
				return "科目类型输入错误！";
			}
			if (hstype.equals("") || hstype.equals("1")) {
				if (!SRS.isNull("c_assitem", "000", hs, km)) {
					hs = SRS.TextKey(hs);
				} else {
					hs = "('" + hs + "')";
				}

				sql += " and AssItemName in  " + hs;
			} else if (hstype.equals("2")) {
				if (!SRS.isNull("c_assitem", "111", hs, km)) {
					hs = SRS.TextKey(hs);
				} else {
					hs = "('" + hs + "')";
				}

				sql += " and AssItemID = (select DISTINCT AssItemID from c_assitem where  asstotalname in "
						+ hs + " and AccPackageID = '" + pkgid + "') ";
			} else if (hstype.equals("3")) {
				sql += " and assitemid = '" + hs + "'";
			} else {
				return "核算项目类型输入错误！";
			}

			//org.util.Debug.prtOut("getHsBqqms:" + sql);
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, submonth);
			ps.setString(i++, pkgid);
			// ps.setString(i++, km);
			// ps.setString(i++, hs);
			// ps.setString(i++, subname);
			rs = ps.executeQuery();

			i = 0;
			while (rs.next()) {
				i++;
				if (i > 1) {
					result = "核算本期期末数出错！";
					break;
				}
				result = CHF.showNull(rs.getString(1));
			}
			
			if("".equals(result)){
				result="0";//result = "科目既不是标准科目，也不是用户科目或核算不是用户核算，请修改";
			}
			//org.util.Debug.prtOut("getNcs result:" + result);
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
	}

	// 科目余额表本期期初数
	private String getBqqcs() throws Exception {
		DbUtil.checkConn(conn);
		String result = "";
		String sql = "";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String subname = CHF.showNull(request.getParameter("km"));
			if (subname.equals("")) {
				return "科目不能为空!";
			}
			
			/**
			 *科目对照  
			 */
			String skm = changeSubjectName(subname);
			if(!"".equals(skm)){
				subname = skm;
			}
			
			String subtype = CHF.showNull(request.getParameter("type"));
			String submonth = CHF.showNull(request.getParameter("qs"));
			if (submonth.equals("")) {
				return "期数不能为空!";
			}
			String fx = CHF.showNull(request.getParameter("fx"));
			if (fx.equals("")) {
				return "方向不能为空!";
			}
//			subname = new String(subname.getByte1"));
//			subtype = new String(subtype.getBytes());
//			submonth = new String(submonth.getBytes());
//			fx = new String(fx.getBytes());

			String sTab = "";
			String bz = CHF.showNull(request.getParameter("bz"));
//			bz = new String(bz.getByte1"));
			if (bz.equals("0") || bz.equals("") || bz.equals("本位币")) {
				sTab = " c_Account ";
				bz = "0";
			} else {
				sTab = " c_AccountAll ";
			}

			
			if (fx.equals("0")) {
				sql = "select if(direction=0,(DebitRemain + CreditRemain),direction*(DebitRemain + CreditRemain))  from "
						+ sTab
						+ " where SubMonth=? and DataName='"
						+ bz
						+ "' and AccPackageID=? ";
			} else if (fx.equals("1")) {
				sql = "select DebitRemain  from " + sTab
						+ " where SubMonth=? and DataName='" + bz
						+ "' and AccPackageID=? ";
			} else if (fx.equals("-1")) {
				sql = "select (-1)*(CreditRemain)  from " + sTab
						+ " where SubMonth=? and DataName='" + bz
						+ "' and AccPackageID=? ";
			} else {
				return "方向输入错误！";
			}

			if (subtype.equals("") || subtype.equals("1")) {
				if (!SRS.isNull("c_accpkgsubject", "SubjectName", subname, "")) {
					subname = SRS.SubjectResult(subname);
				} else {
					subname = "('" + subname + "')";
				}

				sql += "and AccName in " + subname;

			} else if (subtype.equals("2")) {
				if (!SRS.isNull("c_accpkgsubject", "SubjectFullName", subname, "")) {
					subname = SRS.SubjectResult(subname);
				} else {
					subname = "('" + subname + "')";
				}

				sql += " and SubjectID in (select SubjectID from c_accpkgsubject where AccPackageID='"
						+ pkgid + "' and SubjectFullName in " + subname + ")";

			} else if (subtype.equals("3")) {
				sql += " and SubjectID = " + subname;
			} else {
				return "科目类型输入错误！";
			}
			//org.util.Debug.prtOut("getBqfse:" + sql);
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, submonth);
			ps.setString(i++, pkgid);
			// ps.setString(i++, subname);
			rs = ps.executeQuery();

			i = 0;
			while (rs.next()) {
				i++;
				if (i > 1) {
					result = "科目余额表本期期初数出错！";
					break;
				}
				result = CHF.showNull(rs.getString(1));
			}
			
			if("".equals(result)){
				result = "0";//result = "该科目既不是标准科目，也不是用户科目，请修改科目名";
			}
			//org.util.Debug.prtOut("getNcs result:" + result);
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
	}

	// 科目余额表本期期初数(上年)
	private String getBqqcsGo() throws Exception {
		DbUtil.checkConn(conn);
		String result = "";
		String sql = "";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		pkgid = SRS.getNewPackageID(pkgid);
		try {
			if("该单位没有去年的帐套!".equals(pkgid)){
				return "0";
			}
			String subname = CHF.showNull(request.getParameter("km"));
			if (subname.equals("")) {
				return "科目不能为空!";
			}
			
			/**
			 *科目对照  
			 */
			String skm = changeSubjectName(subname);
			if(!"".equals(skm)){
				subname = skm;
			}
			
			String subtype = CHF.showNull(request.getParameter("type"));
			String submonth = CHF.showNull(request.getParameter("qs"));
			if (submonth.equals("")) {
				return "期数不能为空!";
			}
			String fx = CHF.showNull(request.getParameter("fx"));
			if (fx.equals("")) {
				return "方向不能为空!";
			}
//			subname = new String(subname.getBytes("ISO8859-1"));
//			subtype = new String(subtype.getBytes("ISO8859-1"));
//			submonth = new String(submonth.getBytes("ISO8859-1"));
//			fx = new String(fx.getBytes("ISO8859-1"));

			String sTab = "";
			String bz = CHF.showNull(request.getParameter("bz"));
//			bz = new String(bz.getBytes("ISO8859-1"));
			if (bz.equals("0") || bz.equals("") || bz.equals("本位币")) {
				sTab = " c_Account ";
				bz = "0";
			} else {
				sTab = " c_AccountAll ";
			}

			
			if (fx.equals("0")) {
				sql = "select if(direction=0,(DebitRemain + CreditRemain),direction*(DebitRemain + CreditRemain))  from "
						+ sTab
						+ " where SubMonth=? and DataName='"
						+ bz
						+ "' and AccPackageID=? ";
			} else if (fx.equals("1")) {
				sql = "select DebitRemain  from " + sTab
						+ " where SubMonth=? and DataName='" + bz
						+ "' and AccPackageID=? ";
			} else if (fx.equals("-1")) {
				sql = "select (-1)*(CreditRemain) from " + sTab
						+ " where SubMonth=? and DataName='" + bz
						+ "' and AccPackageID=? ";
			} else {
				return "方向输入错误！";
			}

			if (subtype.equals("") || subtype.equals("1")) {
				if (!SRS.isNull("c_accpkgsubject", "SubjectName", subname, "")) {
					subname = SRS.SubjectResult(subname);
				} else {
					subname = "('" + subname + "')";
				}

				sql += "and AccName in " + subname;

			} else if (subtype.equals("2")) {
				if (!SRS.isNull("c_accpkgsubject", "SubjectFullName", subname, "")) {
					subname = SRS.SubjectResult(subname);
				} else {
					subname = "('" + subname + "')";
				}

				sql += " and SubjectID in (select SubjectID from c_accpkgsubject where AccPackageID='"
						+ pkgid + "' and SubjectFullName in " + subname + ")";

			} else if (subtype.equals("3")) {
				sql += " and SubjectID = " + subname;
			} else {
				return "科目类型输入错误！";
			}
			//org.util.Debug.prtOut("getBqfse:" + sql);
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, submonth);
			ps.setString(i++, pkgid);
			// ps.setString(i++, subname);
			rs = ps.executeQuery();

			i = 0;
			while (rs.next()) {
				i++;
				if (i > 1) {
					result = "上年科目余额表本期期初数出错！";
					break;
				}
				result = CHF.showNull(rs.getString(1));
			}
			
			if("".equals(result)){
				result = "0";//result = "该科目既不是标准科目，也不是用户科目，请修改科目名";
			}
			//org.util.Debug.prtOut("getNcs result:" + result);
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
	}

	// //核算本期期初数
	private String getHsBqqcs() throws Exception {
		DbUtil.checkConn(conn);
		String result = "";
		String sql = "";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String km = CHF.showNull(request.getParameter("km"));
			if (km.equals("")) {
				return "科目不能为空!";
			}
			/**
			 *科目对照  
			 */
			String skm = changeSubjectName(km);
			if(!"".equals(skm)){
				km = skm;
			}
			
//			km = new String(km.getBytes("ISO8859-1"));

			String hs = CHF.showNull(request.getParameter("hs"));
			if (hs.equals("")) {
				return "核算项目不能为空!";
			}
//			hs = new String(hs.getBytes());

			String kmtype = CHF.showNull(request.getParameter("type"));
//			kmtype = new String(kmtype.getBytes("ISO8859-1"));
			String hstype = CHF.showNull(request.getParameter("hstype"));
//			hstype = new String(hstype.getBytes("ISO8859-1"));

			String submonth = CHF.showNull(request.getParameter("qs"));
			if (submonth.equals("")) {
				return "期数不能为空!";
			}
//			submonth = new String(submonth.getBytes("ISO8859-1"));

			String fx = CHF.showNull(request.getParameter("fx"));
			if (fx.equals("")) {
				return "方向不能为空!";
			}
//			fx = new String(fx.getBytes("ISO8859-1"));

			
			if (fx.equals("0")) {
				sql = "select if(direction=0,(DebitRemain + CreditRemain),direction*(DebitRemain + CreditRemain))  from c_assitementryacc where SubMonth=? and AccPackageID=? ";
			} else if (fx.equals("1")) {
				sql = "select DebitRemain  from c_assitementryacc where SubMonth=? and AccPackageID=? ";
			} else if (fx.equals("-1")) {
				sql = "select (-1)*CreditRemain c from c_assitementryacc where SubMonth=? and AccPackageID=? ";
			} else {
				return "方向输入错误！";
			}

			if (kmtype.equals("") || kmtype.equals("1")) {
				if (!SRS.isNull("c_accpkgsubject", "SubjectName", km, "")) {
					km = SRS.SubjectResult(km);
				} else {
					km = "('" + km + "')";
				}

				sql += " and AccID =(select DISTINCT subjectid from c_accpkgsubject where  subjectname in "
						+ km + "and AccPackageID='" + pkgid + "')  ";
			} else if (kmtype.equals("2")) {
				if (!SRS.isNull("c_accpkgsubject", "SubjectFullName", km, "")) {
					km = SRS.SubjectResult(km);
				} else {
					km = "('" + km + "')";
				}

				sql += " and AccID =(select DISTINCT subjectid from c_accpkgsubject where  subjectfullname in "
						+ km + "and AccPackageID='" + pkgid + "')  ";
			} else if (kmtype.equals("3")) {
				sql += " and AccID=" + km;
			} else {
				return "科目类型输入错误！";
			}
			if (hstype.equals("") || hstype.equals("1")) {
				if (!SRS.isNull("c_assitem", "000", hs, km)) {
					hs = SRS.TextKey(hs);
				} else {
					hs = "('" + hs + "')";
				}

				sql += " and AssItemName in " + hs;
			} else if (hstype.equals("2")) {
				if (!SRS.isNull("c_assitem", "111", hs, km)) {
					hs = SRS.TextKey(hs);
				} else {
					hs = "('" + hs + "')";
				}

				sql += " and AssItemID = (select DISTINCT AssItemID from c_assitem where  asstotalname in "
						+ hs + " and AccPackageID = '" + pkgid + "') ";
			} else if (hstype.equals("3")) {
				sql += " and assitemid='" + hs + "'";
			} else {
				return "核算项目类型输入错误！";
			}

			//org.util.Debug.prtOut("getHsBqqcs:" + sql);
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, submonth);
			ps.setString(i++, pkgid);
			// ps.setString(i++, km);
			// ps.setString(i++, hs);
			// ps.setString(i++, subname);
			rs = ps.executeQuery();

			i = 0;
			while (rs.next()) {
				i++;
				if (i > 1) {
					result = "核算本期期初数出错！";
					break;
				}
				result = CHF.showNull(rs.getString(1));
			}
			
			if("".equals(result)){
				result="0";//result = "科目既不是标准科目，也不是用户科目或核算不是用户核算，请修改";
			}
			//org.util.Debug.prtOut("getNcs result:" + result);
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
	}


	// //核算本期期初数(上年)
	private String getHsBqqcsGo() throws Exception {
		DbUtil.checkConn(conn);
		String result = "";
		String sql = "";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		pkgid = SRS.getNewPackageID(pkgid);
		try {
			if("该单位没有去年的帐套!".equals(pkgid)){
				return "0";
			}
			String km = CHF.showNull(request.getParameter("km"));
			if (km.equals("")) {
				return "科目不能为空!";
			}
			/**
			 *科目对照  
			 */
			String skm = changeSubjectName(km);
			if(!"".equals(skm)){
				km = skm;
			}
			
//			km = new String(km.getBytes("ISO8859-1"));

			String hs = CHF.showNull(request.getParameter("hs"));
			if (hs.equals("")) {
				return "核算项目不能为空!";
			}
//			hs = new String(hs.getBytes("ISO8859-1"));

			String kmtype = CHF.showNull(request.getParameter("type"));
//			kmtype = new String(kmtype.getBytes("ISO8859-1"));
			String hstype = CHF.showNull(request.getParameter("hstype"));
//			hstype = new String(hstype.getBytes("ISO8859-1"));

			String submonth = CHF.showNull(request.getParameter("qs"));
			if (submonth.equals("")) {
				return "期数不能为空!";
			}
//			submonth = new String(submonth.getBytes("ISO8859-1"));

			String fx = CHF.showNull(request.getParameter("fx"));
			if (fx.equals("")) {
				return "方向不能为空!";
			}
//			fx = new String(fx.getBytes("ISO8859-1"));

			
			if (fx.equals("0")) {
				sql = "select if(direction=0,(DebitRemain + CreditRemain),direction*(DebitRemain + CreditRemain))  from c_assitementryacc where SubMonth=? and AccPackageID=? ";
			} else if (fx.equals("1")) {
				sql = "select DebitRemain  from c_assitementryacc where SubMonth=? and AccPackageID=? ";
			} else if (fx.equals("-1")) {
				sql = "select (-1)*CreditRemain c from c_assitementryacc where SubMonth=? and AccPackageID=? ";
			} else {
				return "方向输入错误！";
			}

			if (kmtype.equals("") || kmtype.equals("1")) {
				if (!SRS.isNull("c_accpkgsubject", "SubjectName", km, "")) {
					km = SRS.SubjectResult(km);
				} else {
					km = "('" + km + "')";
				}

				sql += " and AccID =(select DISTINCT subjectid from c_accpkgsubject where  subjectname in "
						+ km + "and AccPackageID='" + pkgid + "')  ";
			} else if (kmtype.equals("2")) {
				if (!SRS.isNull("c_accpkgsubject", "SubjectFullName", km, "")) {
					km = SRS.SubjectResult(km);
				} else {
					km = "('" + km + "')";
				}

				sql += " and AccID =(select DISTINCT subjectid from c_accpkgsubject where  subjectfullname in "
						+ km + "and AccPackageID='" + pkgid + "')  ";
			} else if (kmtype.equals("3")) {
				sql += " and AccID=　" + km;
			} else {
				return "科目类型输入错误！";
			}
			if (hstype.equals("") || hstype.equals("1")) {
				if (!SRS.isNull("c_assitem", "000", hs, km)) {
					hs = SRS.TextKey(hs);
				} else {
					hs = "('" + hs + "')";
				}

				sql += " and AssItemName in " + hs;
			} else if (hstype.equals("2")) {
				if (!SRS.isNull("c_assitem", "111", hs, km)) {
					hs = SRS.TextKey(hs);
				} else {
					hs = "('" + hs + "')";
				}

				sql += " and AssItemID = (select DISTINCT AssItemID from c_assitem where  asstotalname in "
						+ hs + " and AccPackageID = '" + pkgid + "') ";
			} else if (hstype.equals("3")) {
				sql += " and assitemid='" + hs + "'";
			} else {
				return "核算项目类型输入错误！";
			}

			//org.util.Debug.prtOut("getHsBqqcs:" + sql);
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, submonth);
			ps.setString(i++, pkgid);
			// ps.setString(i++, km);
			// ps.setString(i++, hs);
			// ps.setString(i++, subname);
			rs = ps.executeQuery();

			i = 0;
			while (rs.next()) {
				i++;
				if (i > 1) {
					result = "核算本期期初数出错！";
					break;
				}
				result = CHF.showNull(rs.getString(1));
			}
			
			if("".equals(result)){
				result="0";//result = "科目既不是标准科目，也不是用户科目或核算不是用户核算，请修改";
			}
			//org.util.Debug.prtOut("getNcs result:" + result);
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
	}


	
	
	private String getBqqmsGo() throws Exception{
		DbUtil.checkConn(conn);
		String result = "";
		String sql = "";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		pkgid = SRS.getNewPackageID(pkgid);
		try {
			if("该单位没有去年的帐套!".equals(pkgid)){
				return "0";
			}
			String subname = CHF.showNull(request.getParameter("km"));
			if (subname.equals("")) {
				return "科目不能为空!";
			}
			/**
			 *科目对照  
			 */
			String skm = changeSubjectName(subname);
			if(!"".equals(skm)){
				subname = skm;
			}
			
			String subtype = CHF.showNull(request.getParameter("type"));
			String submonth = CHF.showNull(request.getParameter("qs"));
			if (submonth.equals("")) {
				return "期数不能为空!";
			}
			String fx = CHF.showNull(request.getParameter("fx"));
			if (fx.equals("")) {
				return "方向不能为空!";
			}
//			subname = new String(subname.getBytes("ISO8859-1"));
//			subtype = new String(subtype.getBytes("ISO8859-1"));
//			submonth = new String(submonth.getBytes("ISO8859-1"));
//			fx = new String(fx.getBytes("ISO8859-1"));

			String sTab = "";
			String bz = CHF.showNull(request.getParameter("bz"));
//			bz = new String(bz.getBytes("ISO8859-1"));
			if (bz.equals("0") || bz.equals("") || bz.equals("本位币")) {
				sTab = " c_Account ";
				bz = "0";
			} else {
				sTab = " c_AccountAll ";
			}

			
			if (fx.equals("0")) {
				sql = "select if(direction=0,(Balance),direction*Balance) from "
						+ sTab
						+ " where SubMonth=? and DataName='"
						+ bz
						+ "' and AccPackageID=? ";
			} else if (fx.equals("1")) {
				sql = "select DebitBalance from " + sTab
						+ " where SubMonth=? and DataName='" + bz
						+ "' and AccPackageID=? ";
			} else if (fx.equals("-1")) {
				sql = "select (-1)*CreditBalance from " + sTab
						+ " where SubMonth=? and DataName='" + bz
						+ "' and AccPackageID=? ";
			} else {
				return "方向输入错误！";
			}

			if (subtype.equals("") || subtype.equals("1")) {
				if (!SRS.isNull("c_accpkgsubject", "SubjectName", subname, "")) {
					subname = SRS.SubjectResult(subname);
				} else {
					subname = "('" + subname + "')";
				}

				sql += "and AccName in " + subname;

			} else if (subtype.equals("2")) {
				if (!SRS.isNull("c_accpkgsubject", "SubjectFullName", subname, "")) {
					subname = SRS.SubjectResult(subname);
				} else {
					subname = "('" + subname + "')";
				}

				sql += " and SubjectID in (select SubjectID from c_accpkgsubject where AccPackageID='"
						+ pkgid + "' and SubjectFullName in " + subname + ")";

			} else if (subtype.equals("3")) {
				sql += " and SubjectID = " + subname;
			} else {
				return "科目类型输入错误！";
			}
			//org.util.Debug.prtOut("getBqfse:" + sql);
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, submonth);
			ps.setString(i++, pkgid);
			// ps.setString(i++, subname);
			rs = ps.executeQuery();

			i = 0;
			while (rs.next()) {
				i++;
				if (i > 1) {
					result = "上年科目余额表本期期末数出错！";
					break;
				}
				result = CHF.showNull(rs.getString(1));
			}
			
			if("".equals(result)){
				result = "0";//result = "该科目既不是标准科目，也不是用户科目，请修改科目名";
			}
			//org.util.Debug.prtOut("getNcs result:" + result);
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
	}

	private String getBqqms() throws Exception{
		DbUtil.checkConn(conn);
		String result = "";
		String sql = "";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String subname = CHF.showNull(request.getParameter("km"));
			if (subname.equals("")) {
				return "科目不能为空!";
			}
			/**
			 *科目对照  
			 */
			String skm = changeSubjectName(subname);
			if(!"".equals(skm)){
				subname = skm;
			}
			
			String subtype = CHF.showNull(request.getParameter("type"));
			String submonth = CHF.showNull(request.getParameter("qs"));
			if (submonth.equals("")) {
				return "期数不能为空!";
			}
			String fx = CHF.showNull(request.getParameter("fx"));
			if (fx.equals("")) {
				return "方向不能为空!";
			}
//			subname = new String(subname.getBytes("ISO8859-1"));
//			subtype = new String(subtype.getBytes("ISO8859-1"));
//			submonth = new String(submonth.getBytes("ISO8859-1"));
//			fx = new String(fx.getBytes("ISO8859-1"));

			String sTab = "";
			String bz = CHF.showNull(request.getParameter("bz"));
//			bz = new String(bz.getBytes("ISO8859-1"));
			if (bz.equals("0") || bz.equals("") || bz.equals("本位币")) {
				sTab = " c_Account ";
				bz = "0";
			} else {
				sTab = " c_AccountAll ";
			}

			
			if (fx.equals("0")) {
				sql = "select if(direction=0,(Balance),direction*Balance) from "
						+ sTab
						+ " where SubMonth=? and DataName='"
						+ bz
						+ "' and AccPackageID=? ";
			} else if (fx.equals("1")) {
				sql = "select DebitBalance from " + sTab
						+ " where SubMonth=? and DataName='" + bz
						+ "' and AccPackageID=? ";
			} else if (fx.equals("-1")) {
				sql = "select (-1)*CreditBalance from " + sTab
						+ " where SubMonth=? and DataName='" + bz
						+ "' and AccPackageID=? ";
			} else {
				return "方向输入错误！";
			}

			if (subtype.equals("") || subtype.equals("1")) {
				if (!SRS.isNull("c_accpkgsubject", "SubjectName", subname, "")) {
					subname = SRS.SubjectResult(subname);
				} else {
					subname = "('" + subname + "')";
				}

				sql += "and AccName in " + subname;

			} else if (subtype.equals("2")) {
				if (!SRS.isNull("c_accpkgsubject", "SubjectFullName", subname, "")) {
					subname = SRS.SubjectResult(subname);
				} else {
					subname = "('" + subname + "')";
				}

				sql += " and SubjectID in (select SubjectID from c_accpkgsubject where AccPackageID='"
						+ pkgid + "' and SubjectFullName in " + subname + ")";

			} else if (subtype.equals("3")) {
				sql += " and SubjectID = " + subname;
			} else {
				return "科目类型输入错误！";
			}
			//org.util.Debug.prtOut("getBqfse:" + sql);
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, submonth);
			ps.setString(i++, pkgid);
			// ps.setString(i++, subname);
			rs = ps.executeQuery();

			i = 0;
			while (rs.next()) {
				i++;
				if (i > 1) {
					result = "科目余额表本期期末数出错！";
					break;
				}
				result = CHF.showNull(rs.getString(1));
			}
			
			if("".equals(result)){
				result = "0";//result = "该科目既不是标准科目，也不是用户科目，请修改科目名";
			}
			//org.util.Debug.prtOut("getNcs result:" + result);
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
	}

//	 取核算本期发生额(上年)
	private String getHsBqfseGo() throws Exception {
		DbUtil.checkConn(conn);
		String result = "";
		String sql = "";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		pkgid = SRS.getNewPackageID(pkgid);
		try {
			if("该单位没有去年的帐套!".equals(pkgid)){
				return "0";
			}
			String km = CHF.showNull(request.getParameter("km"));
			if (km.equals("")) {
				return "科目不能为空!";
			}
			/**
			 *科目对照  
			 */
			String skm = changeSubjectName(km);
			if(!"".equals(skm)){
				km = skm;
			}
//			km = new String(km.getBytes("ISO8859-1"));

			String hs = CHF.showNull(request.getParameter("hs"));
			if (hs.equals("")) {
				return "核算项目不能为空!";
			}
//			hs = new String(hs.getBytes("ISO8859-1"));

			String kmtype = CHF.showNull(request.getParameter("type"));
//			kmtype = new String(kmtype.getBytes("ISO8859-1"));
			String hstype = CHF.showNull(request.getParameter("hstype"));
//			hstype = new String(hstype.getBytes("ISO8859-1"));

			String submonth = CHF.showNull(request.getParameter("qs"));
			if (submonth.equals("")) {
				return "期数不能为空!";
			}
//			submonth = new String(submonth.getBytes("ISO8859-1"));

			String fx = CHF.showNull(request.getParameter("fx"));
			if (fx.equals("")) {
				return "方向不能为空!";
			}
//			fx = new String(fx.getBytes("ISO8859-1"));

			
			if (fx.equals("0")) {
				sql = "select if(direction=0,(DebitOcc - CreditOcc),direction*(DebitOcc - CreditOcc)) from c_assitementryacc where SubMonth=? and AccPackageID=? ";
			} else if (fx.equals("1")) {
				sql = "select DebitOcc  from c_assitementryacc where SubMonth=? and AccPackageID=? ";
			} else if (fx.equals("-1")) {
				sql = "select CreditOcc from c_assitementryacc where SubMonth=? and AccPackageID=? ";
			} else {
				return "方向输入错误！";
			}

			if (kmtype.equals("") || kmtype.equals("1")) {
				if (!SRS.isNull("c_accpkgsubject", "SubjectName", km, "")) {
					km = SRS.SubjectResult(km);
				} else {
					km = "('" + km + "')";
				}

				sql += " and AccID =(select DISTINCT subjectid from c_accpkgsubject where  subjectname in "
						+ km + "and AccPackageID='" + pkgid + "')  ";
			} else if (kmtype.equals("2")) {
				if (!SRS.isNull("c_accpkgsubject", "SubjectFullName", km, "")) {
					km = SRS.SubjectResult(km);
				} else {
					km = "('" + km + "')";
				}

				sql += " and AccID =(select DISTINCT subjectid from c_accpkgsubject where  subjectfullname in "
						+ km + "and AccPackageID='" + pkgid + "')  ";
			} else if (kmtype.equals("3")) {
				sql += " and AccID=" + km;
			} else {
				return "科目类型输入错误！";
			}
			if (hstype.equals("") || hstype.equals("1")) {
				if (!SRS.isNull("c_assitem", "000", hs, km)) {
					hs = SRS.TextKey(hs);
				} else {
					hs = "('" + hs + "')";
				}

				sql += " and AssItemName in " + hs;
			} else if (hstype.equals("2")) {
				if (!SRS.isNull("c_assitem", "111", hs, km)) {
					hs = SRS.TextKey(hs);
				} else {
					hs = "('" + hs + "')";
				}

				sql += " and AssItemID = (select DISTINCT AssItemID from c_assitem where  asstotalname in "
						+ hs + " and AccPackageID = '" + pkgid + "') ";
			} else if (hstype.equals("3")) {
				sql += " and assitemid='" + hs + "'";
			} else {
				return "核算项目类型输入错误！";
			}

			//org.util.Debug.prtOut("getHsBqfse:" + sql);
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, submonth);
			ps.setString(i++, pkgid);
			// ps.setString(i++, km);
			// ps.setString(i++, hs);

			rs = ps.executeQuery();

			i = 0;
			while (rs.next()) {
				i++;
				if (i > 1) {
					result = "上年核算本期发生额出错！";
					break;
				}
				result = CHF.showNull(rs.getString(1));
			}
			
			if("".equals(result)){
				result="0";//result = "科目既不是标准科目，也不是用户科目或核算不是用户核算，请修改";
			}
			//org.util.Debug.prtOut("getNcs result:" + result);
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
	}

//	 取核算本期发生额
	private String getHsBqfse() throws Exception {
		DbUtil.checkConn(conn);
		String result = "";
		String sql = "";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String km = CHF.showNull(request.getParameter("km"));
			if (km.equals("")) {
				return "科目不能为空!";
			}
			/**
			 *科目对照  
			 */
			String skm = changeSubjectName(km);
			if(!"".equals(skm)){
				km = skm;
			}
//			km = new String(km.getBytes("ISO8859-1"));

			String hs = CHF.showNull(request.getParameter("hs"));
			if (hs.equals("")) {
				return "核算项目不能为空!";
			}
//			hs = new String(hs.getBytes("ISO8859-1"));

			String kmtype = CHF.showNull(request.getParameter("type"));
//			kmtype = new String(kmtype.getBytes("ISO8859-1"));
			String hstype = CHF.showNull(request.getParameter("hstype"));
//			hstype = new String(hstype.getBytes("ISO8859-1"));

			String submonth = CHF.showNull(request.getParameter("qs"));
			if (submonth.equals("")) {
				return "期数不能为空!";
			}
//			submonth = new String(submonth.getBytes("ISO8859-1"));

			String fx = CHF.showNull(request.getParameter("fx"));
			if (fx.equals("")) {
				return "方向不能为空!";
			}
//			fx = new String(fx.getBytes("ISO8859-1"));

			
			if (fx.equals("0")) {
				sql = "select if(direction=0,(DebitOcc - CreditOcc),direction*(DebitOcc - CreditOcc)) from c_assitementryacc where SubMonth=? and AccPackageID=? ";
			} else if (fx.equals("1")) {
				sql = "select DebitOcc  from c_assitementryacc where SubMonth=? and AccPackageID=? ";
			} else if (fx.equals("-1")) {
				sql = "select CreditOcc from c_assitementryacc where SubMonth=? and AccPackageID=? ";
			} else {
				return "方向输入错误！";
			}

			if (kmtype.equals("") || kmtype.equals("1")) {
				if (!SRS.isNull("c_accpkgsubject", "SubjectName", km, "")) {
					km = SRS.SubjectResult(km);
				} else {
					km = "('" + km + "')";
				}
				sql += " and AccID =(select DISTINCT subjectid from c_accpkgsubject where  subjectname in "
						+ km + "and AccPackageID='" + pkgid + "') ";
			} else if (kmtype.equals("2")) {
				if (!SRS.isNull("c_accpkgsubject", "SubjectFullName", km, "")) {
					km = SRS.SubjectResult(km);
				} else {
					km = "('" + km + "')";
				}

				sql += " and AccID =(select DISTINCT subjectid from c_accpkgsubject where  subjectfullname in "
						+ km + "and AccPackageID='" + pkgid + "')  ";
			} else if (kmtype.equals("3")) {
				sql += " and AccID=" + km;
			} else {
				return "科目类型输入错误！";
			}
			if (hstype.equals("") || hstype.equals("1")) {
				if (!SRS.isNull("c_assitem", "000", hs, km)) {
					hs = SRS.TextKey(hs);
				} else {
					hs = "('" + hs + "')";
				}

				sql += " and AssItemName in " + hs;
			} else if (hstype.equals("2")) {
				if (!SRS.isNull("c_assitem", "111", hs, km)) {
					hs = SRS.TextKey(hs);
				} else {
					hs = "('" + hs + "')";
				}

				sql += " and AssItemID = (select DISTINCT AssItemID from c_assitem where  asstotalname in "
						+ hs + " and AccPackageID = '" + pkgid + "') ";
			} else if (hstype.equals("3")) {
				sql += " and assitemid='" + hs + "'";
			} else {
				return "核算项目类型输入错误！";
			}

			//org.util.Debug.prtOut("getHsBqfse:" + sql);
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, submonth);
			ps.setString(i++, pkgid);
			// ps.setString(i++, km);
			// ps.setString(i++, hs);

			rs = ps.executeQuery();

			i = 0;
			while (rs.next()) {
				i++;
				if (i > 1) {
					result = "核算本期发生额出错！";
					break;
				}
				result = CHF.showNull(rs.getString(1));
			}
			
			if("".equals(result)){
				result="0";//result = "科目既不是标准科目，也不是用户科目或核算不是用户核算，请修改";
			}
			//org.util.Debug.prtOut("getNcs result:" + result);
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
	}

//	 科目余额表本期发生额(上年)
	private String getBqfseGo() throws Exception {
		DbUtil.checkConn(conn);
		String result = "";
		String sql = "";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		pkgid = SRS.getNewPackageID(pkgid);
		try {
			if("该单位没有去年的帐套!".equals(pkgid)){
				return "0";
			}
			String subname = CHF.showNull(request.getParameter("km"));
			if (subname.equals("")) {
				return "科目不能为空!";
			}
			/**
			 *科目对照  
			 */
			String skm = changeSubjectName(subname);
			if(!"".equals(skm)){
				subname = skm;
			}
			String subtype = CHF.showNull(request.getParameter("type"));
			String submonth = CHF.showNull(request.getParameter("qs"));
			if (submonth.equals("")) {
				return "期数不能为空!";
			}
			String fx = CHF.showNull(request.getParameter("fx"));
			if (fx.equals("")) {
				return "方向不能为空!";
			}
//			subname = new String(subname.getBytes("ISO8859-1"));
//			subtype = new String(subtype.getBytes("ISO8859-1"));
//			submonth = new String(submonth.getBytes("ISO8859-1"));
//			fx = new String(fx.getBytes("ISO8859-1"));

			String sTab = "";
			String bz = CHF.showNull(request.getParameter("bz"));
//			bz = new String(bz.getBytes("ISO8859-1"));
			if (bz.equals("0") || bz.equals("") || bz.equals("本位币")) {
				sTab = " c_Account ";
				bz = "0";
			} else {
				sTab = " c_AccountAll ";
			}
			
			if (fx.equals("0")) {
				sql = "select if(direction=0,(DebitOcc - CreditOcc),direction*(DebitOcc - CreditOcc)) from "
						+ sTab
						+ " where SubMonth=? and DataName='"
						+ bz
						+ "' and AccPackageID=? ";
			} else if (fx.equals("1")) {
				sql = "select DebitOcc  from " + sTab
						+ " where SubMonth=? and DataName='" + bz
						+ "' and AccPackageID=? ";
			} else if (fx.equals("-1")) {
				sql = "select CreditOcc from " + sTab
						+ " where SubMonth=? and DataName='" + bz
						+ "' and AccPackageID=? ";
			} else {
				return "方向输入错误！";
			}

			if (subtype.equals("") || subtype.equals("1")) {
				if (!SRS.isNull("c_accpkgsubject", "SubjectName", subname, "")) {
					subname = SRS.SubjectResult(subname);
				} else {
					subname = "('" + subname + "')";
				}

				sql += "and AccName in " + subname;

			} else if (subtype.equals("2")) {
				if (!SRS.isNull("c_accpkgsubject", "SubjectFullName", subname, "")) {
					subname = SRS.SubjectResult(subname);
				} else {
					subname = "('" + subname + "')";
				}

				sql += " and SubjectID in (select SubjectID from c_accpkgsubject where AccPackageID='"
						+ pkgid + "' and SubjectFullName in " + subname + ")";

			} else if (subtype.equals("3")) {
				sql += " and SubjectID = " + subname;
			} else {
				return "科目类型输入错误！";
			}
			//org.util.Debug.prtOut("getBqfse:" + sql);
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, submonth);
			ps.setString(i++, pkgid);
			// ps.setString(i++, subname);
			rs = ps.executeQuery();

			i = 0;
			while (rs.next()) {
				i++;
				if (i > 1) {
					result = "上年科目余额表年累计发生额出错！";
					break;
				}
				result = CHF.showNull(rs.getString(1));
			}
			
			if("".equals(result)){
				result = "0";//result = "该科目既不是标准科目，也不是用户科目，请修改科目名";
			}
			//org.util.Debug.prtOut("getNcs result:" + result);
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
	}

//	 科目余额表本期发生额
	private String getBqfse() throws Exception {
		DbUtil.checkConn(conn);
		String result = "";
		String sql = "";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String subname = CHF.showNull(request.getParameter("km"));
			if (subname.equals("")) {
				return "科目不能为空!";
			}
			/**
			 *科目对照  
			 */
			String skm = changeSubjectName(subname);
			if(!"".equals(skm)){
				subname = skm;
			}
			String subtype = CHF.showNull(request.getParameter("type"));
			String submonth = CHF.showNull(request.getParameter("qs"));
			if (submonth.equals("")) {
				return "期数不能为空!";
			}
			String fx = CHF.showNull(request.getParameter("fx"));
			if (fx.equals("")) {
				return "方向不能为空!";
			}
//			subname = new String(subname.getBytes("ISO8859-1"));
//			subtype = new String(subtype.getBytes("ISO8859-1"));
//			submonth = new String(submonth.getBytes("ISO8859-1"));
//			fx = new String(fx.getBytes("ISO8859-1"));

			String sTab = "";
			String bz = CHF.showNull(request.getParameter("bz"));
//			bz = new String(bz.getBytes("ISO8859-1"));
			if (bz.equals("0") || bz.equals("") || bz.equals("本位币")) {
				sTab = " c_Account ";
				bz = "0";
			} else {
				sTab = " c_AccountAll ";
			}

			
			if (fx.equals("0")) {
				sql = "select if(direction=0,(DebitOcc - CreditOcc),direction*(DebitOcc - CreditOcc)) from "
						+ sTab
						+ " where SubMonth=? and DataName='"
						+ bz
						+ "' and AccPackageID=? ";
			} else if (fx.equals("1")) {
				sql = "select DebitOcc  from " + sTab
						+ " where SubMonth=? and DataName='" + bz
						+ "' and AccPackageID=? ";
			} else if (fx.equals("-1")) {
				sql = "select CreditOcc from " + sTab
						+ " where SubMonth=? and DataName='" + bz
						+ "' and AccPackageID=? ";
			} else {
				return "方向输入错误！";
			}

			if (subtype.equals("") || subtype.equals("1")) {
				if (!SRS.isNull("c_accpkgsubject", "SubjectName", subname, "")) {
					subname = SRS.SubjectResult(subname);
				} else {
					subname = "('" + subname + "')";
				}

				sql += "and AccName in " + subname;

			} else if (subtype.equals("2")) {
				if (!SRS.isNull("c_accpkgsubject", "SubjectFullName", subname, "")) {
					subname = SRS.SubjectResult(subname);
				} else {
					subname = "('" + subname + "')";
				}

				sql += " and SubjectID in (select SubjectID from c_accpkgsubject where AccPackageID='"
						+ pkgid + "' and SubjectFullName in " + subname + ")";

			} else if (subtype.equals("3")) {
				sql += " and SubjectID = " + subname;
			} else {
				return "科目类型输入错误！";
			}
			//org.util.Debug.prtOut("getBqfse:" + sql);
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, submonth);
			ps.setString(i++, pkgid);
			// ps.setString(i++, subname);
			rs = ps.executeQuery();

			i = 0;
			while (rs.next()) {
				i++;
				if (i > 1) {
					result = "科目余额表年累计发生额出错！";
					break;
				}
				result = CHF.showNull(rs.getString(1));
			}
			
			if("".equals(result)){
				result = "0";//result = "该科目既不是标准科目，也不是用户科目，请修改科目名";
			}
			//org.util.Debug.prtOut("getNcs result:" + result);
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
	}

//	 取核算年累计发生额(上年)
	private String getHsNlfseGo()  throws Exception {
		DbUtil.checkConn(conn);
		String result = "";
		String sql = "";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		pkgid = SRS.getNewPackageID(pkgid);
		try {
			if("该单位没有去年的帐套!".equals(pkgid)){
				return "0";
			}
			String km = CHF.showNull(request.getParameter("km"));
			if (km.equals("")) {
				return "科目不能为空!";
			}
			/**
			 *科目对照  
			 */
			String skm = changeSubjectName(km);
			if(!"".equals(skm)){
				km = skm;
			}
//			km = new String(km.getBytes("ISO8859-1"));

			String hs = CHF.showNull(request.getParameter("hs"));
			if (hs.equals("")) {
				return "核算项目不能为空!";
			}
//			hs = new String(hs.getBytes("ISO8859-1"));

			String kmtype = CHF.showNull(request.getParameter("type"));
//			kmtype = new String(kmtype.getBytes("ISO8859-1"));
			String hstype = CHF.showNull(request.getParameter("hstype"));
//			hstype = new String(hstype.getBytes("ISO8859-1"));

			String fx = CHF.showNull(request.getParameter("fx"));
			if (fx.equals("")) {
				return "方向不能为空!";
			}
//			fx = new String(fx.getBytes("ISO8859-1"));

			
			if (fx.equals("0")) {
				sql = "select if(direction=0,(DebitTotalOcc-CreditTotalOcc),direction*(DebitTotalOcc-CreditTotalOcc))  from c_assitementryacc where SubMonth=12 and AccPackageID=? ";
			} else if (fx.equals("1")) {
				sql = "select DebitTotalOcc  from c_assitementryacc where SubMonth=12 and AccPackageID=? ";
			} else if (fx.equals("-1")) {
				sql = "select CreditTotalOcc  from c_assitementryacc where SubMonth=12 and AccPackageID=? ";
			} else {
				return "方向输入错误！";
			}

			if (kmtype.equals("") || kmtype.equals("1")) {
				if (!SRS.isNull("c_accpkgsubject", "SubjectName", km, "")) {
					km = SRS.SubjectResult(km);
				} else {
					km = "('" + km + "')";
				}

				sql += " and AccID =(select DISTINCT subjectid from c_accpkgsubject where  subjectname in "
						+ km + "and AccPackageID='" + pkgid + "') ";
			} else if (kmtype.equals("2")) {
				if (!SRS.isNull("c_accpkgsubject", "SubjectName", km, "")) {
					km = SRS.SubjectResult(km);
				} else {
					km = "('" + km + "')";
				}

				sql += " and AccID =(select DISTINCT subjectid from c_accpkgsubject where  subjectfullname in "
						+ km + "and AccPackageID='" + pkgid + "')  ";
			} else if (kmtype.equals("3")) {
				sql += " and AccID=　" + km;
			} else {
				return "科目类型输入错误！";
			}
			if (hstype.equals("") || hstype.equals("1")) {
				if (!SRS.isNull("c_assitem", "000", hs, km)) {
					hs = SRS.TextKey(hs);
				} else {
					hs = "('" + hs + "')";
				}

				sql += " and AssItemName in " + hs;
			} else if (hstype.equals("2")) {
				if (!SRS.isNull("c_assitem", "111", hs, km)) {
					hs = SRS.TextKey(hs);
				} else {
					hs = "('" + hs + "')";
				}

				sql += " and AssItemID = (select DISTINCT AssItemID from c_assitem where  asstotalname in "
						+ hs + " and AccPackageID = '" + pkgid + "') ";
			} else if (hstype.equals("3")) {
				sql += " and assitemid='" + hs + "'";
			} else {
				return "核算项目类型输入错误！";
			}

			//org.util.Debug.prtOut("getHsNlfse:" + sql);

			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, pkgid);
			// ps.setString(i++, km);
			// ps.setString(i++, hs);

			rs = ps.executeQuery();
			i = 0;
			while (rs.next()) {
				i++;
				if (i > 1) {
					result = "上年核算年累计发生额出错！";
					break;
				}
				result = CHF.showNull(rs.getString(1));
			}
			
			if("".equals(result)){
				result="0";//result = "科目既不是标准科目，也不是用户科目或核算不是用户核算，请修改";
			}
			//org.util.Debug.prtOut("getNcs result:" + result);
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
	}

//	 取核算年累计发生额
	private String getHsNlfse() throws Exception {
		DbUtil.checkConn(conn);
		String result = "";
		String sql = "";		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String km = CHF.showNull(request.getParameter("km"));
			if (km.equals("")) {
				return "科目不能为空!";
			}
			/**
			 *科目对照  
			 */
			String skm = changeSubjectName(km);
			if(!"".equals(skm)){
				km = skm;
			}
//			km = new String(km.getBytes("ISO8859-1"));

			String hs = CHF.showNull(request.getParameter("hs"));
			if (hs.equals("")) {
				return "核算项目不能为空!";
			}
//			hs = new String(hs.getBytes("ISO8859-1"));

			String kmtype = CHF.showNull(request.getParameter("type"));
//			kmtype = new String(kmtype.getBytes("ISO8859-1"));
			String hstype = CHF.showNull(request.getParameter("hstype"));
//			hstype = new String(hstype.getBytes("ISO8859-1"));

			String fx = CHF.showNull(request.getParameter("fx"));
			if (fx.equals("")) {
				return "方向不能为空!";
			}
//			fx = new String(fx.getBytes("ISO8859-1"));

		
			if (fx.equals("0")) {
				sql = "select if(direction=0,(DebitTotalOcc-CreditTotalOcc),direction*(DebitTotalOcc-CreditTotalOcc))  from c_assitementryacc where SubMonth=12 and AccPackageID=? ";
			} else if (fx.equals("1")) {
				sql = "select DebitTotalOcc  from c_assitementryacc where SubMonth=12 and AccPackageID=? ";
			} else if (fx.equals("-1")) {
				sql = "select CreditTotalOcc  from c_assitementryacc where SubMonth=12 and AccPackageID=? ";
			} else {
				return "方向输入错误！";
			}

			if (kmtype.equals("") || kmtype.equals("1")) {
				if (!SRS.isNull("c_accpkgsubject", "SubjectName", km, "")) {
					km = SRS.SubjectResult(km);
				} else {
					km = "('" + km + "')";
				}

				sql += " and AccID =(select DISTINCT subjectid from c_accpkgsubject where  subjectname in "
						+ km + "and AccPackageID='" + pkgid + "') ";
			} else if (kmtype.equals("2")) {
				if (!SRS.isNull("c_accpkgsubject", "SubjectFullName", km, "")) {
					km = SRS.SubjectResult(km);
				} else {
					km = "('" + km + "')";
				}

				sql += " and AccID =(select DISTINCT subjectid from c_accpkgsubject where  subjectfullname in "
						+ km + "and AccPackageID='" + pkgid + "') ";
			} else if (kmtype.equals("3")) {
				sql += " and AccID=" + km;
			} else {
				return "科目类型输入错误！";
			}
			if (hstype.equals("") || hstype.equals("1")) {
				if (!SRS.isNull("c_assitem", "000", hs, km)) {
					hs = SRS.TextKey(hs);
				} else {
					hs = "('" + hs + "')";
				}
				sql += " and AssItemName in " + hs;
			} else if (hstype.equals("2")) {
				if (!SRS.isNull("c_assitem", "111", hs, km)) {
					hs = SRS.TextKey(hs);
				} else {
					hs = "('" + hs + "')";
				}

				sql += " and AssItemID = (select DISTINCT AssItemID from c_assitem where  asstotalname in "
						+ hs + " and AccPackageID = '" + pkgid + "') ";
			} else if (hstype.equals("3")) {
				sql += " and assitemid='" + hs + "'";
			} else {
				return "核算项目类型输入错误！";
			}

			//org.util.Debug.prtOut("getHsNlfse:" + sql);
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, pkgid);
			// ps.setString(i++, km);
			// ps.setString(i++, hs);

			rs = ps.executeQuery();
			i = 0;
			while (rs.next()) {
				i++;
				if (i > 1) {
					result = "核算年累计发生额出错！";
					break;
				}
				result = CHF.showNull(rs.getString(1));
			}
			
			if("".equals(result)){
				result="0";//result = "科目既不是标准科目，也不是用户科目或核算不是用户核算，请修改";
			}
			//org.util.Debug.prtOut("getNcs result:" + result);
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
	}

//	 科目余额表年累计发生额(上年)
	private String getNlfseGo() throws Exception {
		DbUtil.checkConn(conn);
		String result = "";
		String sql = "";		
		PreparedStatement ps = null;
		ResultSet rs = null;
		pkgid = SRS.getNewPackageID(pkgid);
		try {
			if("该单位没有去年的帐套!".equals(pkgid)){
				return "0";
			}
			String subname = CHF.showNull(request.getParameter("km"));
			if (subname.equals("")) {
				return "科目不能为空!";
			}
			/**
			 *科目对照  
			 */
			String skm = changeSubjectName(subname);
			if(!"".equals(skm)){
				subname = skm;
			}
			String subtype = CHF.showNull(request.getParameter("type"));
//			subname = new String(subname.getBytes("ISO8859-1"));
//			subtype = new String(subtype.getBytes("ISO8859-1"));

			String fx = CHF.showNull(request.getParameter("fx"));
			if (fx.equals("")) {
				return "方向不能为空!";
			}

			String sTab = "";
			String bz = CHF.showNull(request.getParameter("bz"));
//			bz = new String(bz.getBytes("ISO8859-1"));
			if (bz.equals("0") || bz.equals("") || bz.equals("本位币")) {
				sTab = " c_Account ";
				bz = "0";
			} else {
				sTab = " c_AccountAll ";
			}
			
			if (fx.equals("0")) {
				sql = "select if(direction=0,(DebitTotalOcc-CreditTotalOcc),direction*(DebitTotalOcc-CreditTotalOcc))  from "
						+ sTab
						+ " where SubMonth=12 and DataName='"
						+ bz
						+ "' and AccPackageID=? ";
			} else if (fx.equals("1")) {
				sql = "select DebitTotalOcc  from " + sTab
						+ " where SubMonth=12 and DataName='" + bz
						+ "' and AccPackageID=? ";
			} else if (fx.equals("-1")) {
				sql = "select CreditTotalOcc  from " + sTab
						+ " where SubMonth=12 and DataName='" + bz
						+ "' and AccPackageID=? ";
			} else {
				return "方向输入错误！";
			}

			if (subtype.equals("") || subtype.equals("1")) {
				if (!SRS.isNull("c_accpkgsubject", "SubjectName", subname, "")) {
					subname = SRS.SubjectResult(subname);
				} else {
					subname = "('" + subname + "')";
				}

				sql += "and AccName in " + subname;

			} else if (subtype.equals("2")) {
				if (!SRS.isNull("c_accpkgsubject", "SubjectFullName", subname, "")) {
					subname = SRS.SubjectResult(subname);
				} else {
					subname = "('" + subname + "')";
				}

				sql += " and SubjectID in (select SubjectID from c_accpkgsubject where AccPackageID='"
						+ pkgid + "' and SubjectFullName in " + subname + ")";

			} else if (subtype.equals("3")) {
				sql += " and SubjectID = " + subname;
			} else {
				return "科目类型输入错误！";
			}

			//org.util.Debug.prtOut("getNlfse:" + sql);
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, pkgid);
			// ps.setString(i++, subname);

			rs = ps.executeQuery();
			i = 0;
			while (rs.next()) {
				i++;
				if (i > 1) {
					result = "上年科目余额表年累计发生额出错！";
					break;
				}
				result = CHF.showNull(rs.getString(1));
			}
			
			if("".equals(result)){
				result = "0";//result = "该科目既不是标准科目，也不是用户科目，请修改科目名";
			}
			//org.util.Debug.prtOut("getNcs result:" + result);
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
	}

//	 科目余额表年累计发生额
	private String getNlfse() throws Exception {
		DbUtil.checkConn(conn);
		String result = "";
		String sql = "";		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String subname = CHF.showNull(request.getParameter("km"));
			if (subname.equals("")) {
				return "科目不能为空!";
			}
			/**
			 *科目对照  
			 */
			String skm = changeSubjectName(subname);
			if(!"".equals(skm)){
				subname = skm;
			}
			
			String subtype = CHF.showNull(request.getParameter("type"));
//			subname = new String(subname.getBytes("ISO8859-1"));
//			subtype = new String(subtype.getBytes("ISO8859-1"));

			String fx = CHF.showNull(request.getParameter("fx"));
			if (fx.equals("")) {
				return "方向不能为空!";
			}

			String sTab = "";
			String bz = CHF.showNull(request.getParameter("bz"));
//			bz = new String(bz.getBytes("ISO8859-1"));
			if (bz.equals("0") || bz.equals("") || bz.equals("本位币")) {
				sTab = " c_Account ";
				bz = "0";
			} else {
				sTab = " c_AccountAll ";
			}
			
			if (fx.equals("0")) {
				sql = "select if(direction=0,(DebitTotalOcc-CreditTotalOcc),direction*(DebitTotalOcc-CreditTotalOcc))  from "
						+ sTab
						+ " where SubMonth=12 and DataName='"
						+ bz
						+ "' and AccPackageID=? ";
			} else if (fx.equals("1")) {
				sql = "select DebitTotalOcc  from " + sTab
						+ " where SubMonth=12 and DataName='" + bz
						+ "' and AccPackageID=? ";
			} else if (fx.equals("-1")) {
				sql = "select CreditTotalOcc  from " + sTab
						+ " where SubMonth=12 and DataName='" + bz
						+ "' and AccPackageID=? ";
			} else {
				return "方向输入错误！";
			}

			if (subtype.equals("") || subtype.equals("1")) {
				if (!SRS.isNull("c_accpkgsubject", "SubjectName", subname, "")) {
					subname = SRS.SubjectResult(subname);
				} else {
					subname = "('" + subname + "')";
				}

				sql += "and AccName in " + subname;

			} else if (subtype.equals("2")) {
				if (!SRS.isNull("c_accpkgsubject", "SubjectFullName", subname, "")) {
					subname = SRS.SubjectResult(subname);
				} else {
					subname = "('" + subname + "')";
				}

				sql += " and SubjectID in (select SubjectID from c_accpkgsubject where AccPackageID='"
						+ pkgid + "' and SubjectFullName in " + subname + ")";

			} else if (subtype.equals("3")) {
				sql += " and SubjectID = " + subname;
			} else {
				return "科目类型输入错误！";
			}

			//org.util.Debug.prtOut("getNlfse:" + sql);
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, pkgid);
			// ps.setString(i++, subname);

			rs = ps.executeQuery();
			i = 0;
			while (rs.next()) {
				i++;
				if (i > 1) {
					result = "科目余额表年累计发生额出错！";
					break;
				}
				result = CHF.showNull(rs.getString(1));
			}
			
			if("".equals(result)){
				result = "0";//result = "该科目既不是标准科目，也不是用户科目，请修改科目名";
			}
			//org.util.Debug.prtOut("getNcs result:" + result);
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
	}

//	 取核算年末数（上年）
	private String getHsNmsGo() throws Exception {
		DbUtil.checkConn(conn);
		String result = "";
		String sql = "";		
		PreparedStatement ps = null;
		ResultSet rs = null;
		pkgid = SRS.getNewPackageID(pkgid);
		try {
			if("该单位没有去年的帐套!".equals(pkgid)){
				return "0";
			}
			String km = CHF.showNull(request.getParameter("km"));
			if (km.equals("")) {
				return "科目不能为空!";
			}
			/**
			 *科目对照  
			 */
			String skm = changeSubjectName(km);
			if(!"".equals(skm)){
				km = skm;
			}
//			km = new String(km.getBytes("ISO8859-1"));

			String hs = CHF.showNull(request.getParameter("hs"));
			if (hs.equals("")) {
				return "核算项目不能为空!";
			}
//			hs = new String(hs.getBytes("ISO8859-1"));
			String kmtype = CHF.showNull(request.getParameter("type"));
//			kmtype = new String(kmtype.getBytes("ISO8859-1"));
			String hstype = CHF.showNull(request.getParameter("hstype"));
//			hstype = new String(hstype.getBytes("ISO8859-1"));
			String fx = CHF.showNull(request.getParameter("fx"));
			if (fx.equals("")) {
				return "方向不能为空!";
			}
			if (fx.equals("0")) {
				sql = "select if(direction=0,(Balance),direction*(Balance)) from c_assitementryacc where SubMonth=12 and AccPackageID=? ";
			} else if (fx.equals("1")) {
				sql = "select DebitBalance from c_assitementryacc where SubMonth=12 and AccPackageID=? ";
			} else if (fx.equals("-1")) {
				sql = "select (-1)*CreditBalance from c_assitementryacc where SubMonth=12 and AccPackageID=? ";
			} else {
				return "方向输入错误！";
			}
			// sql = "select abs(Balance) from c_assitementryacc where
			// SubMonth=12 and AccPackageID=? ";
			if (kmtype.equals("") || kmtype.equals("1")) {
				if (!SRS.isNull("c_accpkgsubject", "SubjectName", km, "")) {
					km = SRS.SubjectResult(km);
				} else {
					km = "('" + km + "')";
				}

				sql += " and AccID =(select DISTINCT subjectid from c_accpkgsubject where  subjectname in "
						+ km + "and AccPackageID = '" + pkgid + "') ";
			} else if (kmtype.equals("2")) {
				if (!SRS.isNull("c_accpkgsubject", "SubjectFullName", km, "")) {
					km = SRS.SubjectResult(km);
				} else {
					km = "('" + km + "')";
				}

				sql += " and AccID =(select DISTINCT subjectid from c_accpkgsubject where  subjectfullname in "
						+ km + "and AccPackageID = '" + pkgid + "') ";
			} else if (kmtype.equals("3")) {
				sql += " and AccID=" + km;
			} else {
				return "科目类型输入错误！";
			}
			if (hstype.equals("") || hstype.equals("1")) {
				if (!SRS.isNull("c_assitem", "000", hs, km)) {
					hs = SRS.TextKey(hs);
				} else {
					hs = "('" + hs + "')";
				}

				sql += " and AssItemName in " + hs;
			} else if (hstype.equals("2")) {
				if (!SRS.isNull("c_assitem", "111", hs, km)) {
					hs = SRS.TextKey(hs);
				} else {
					hs = "('" + hs + "')";
				}

				sql += " and AssItemID = (select DISTINCT AssItemID from c_assitem where  asstotalname in "
						+ hs + " and AccPackageID = '" + pkgid + "') ";
			} else if (hstype.equals("3")) {
				sql += " and assitemid='" + hs + "'";
			} else {
				return "核算项目类型输入错误！";
			}

			//org.util.Debug.prtOut("getHsNms:" + sql);			
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, pkgid);
			// ps.setString(i++, km);
			// ps.setString(i++, hs);
			rs = ps.executeQuery();
			i = 0;
			while (rs.next()) {
				i++;
				if (i > 1) {
					result = "上年核算年末数出错！";
					break;
				}
				result = CHF.showNull(rs.getString(1));
			}
			
			if("".equals(result)){
				result="0";//result = "科目既不是标准科目，也不是用户科目或核算不是用户核算，请修改";
			}
			//org.util.Debug.prtOut("getNcs result:" + result);
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
	}

//	 取核算年末数
	private String getHsNms() throws Exception {
		DbUtil.checkConn(conn);
		String result = "";
		String sql = "";		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String km = CHF.showNull(request.getParameter("km"));
			if (km.equals("")) {
				return "科目不能为空!";
			}
			/**
			 *科目对照  
			 */
			String skm = changeSubjectName(km);
			if(!"".equals(skm)){
				km = skm;
			}
//			km = new String(km.getBytes("ISO8859-1"));

			String hs = CHF.showNull(request.getParameter("hs"));
			if (hs.equals("")) {
				return "核算项目不能为空!";
			}
//			hs = new String(hs.getBytes("ISO8859-1"));
			String kmtype = CHF.showNull(request.getParameter("type"));
//			kmtype = new String(kmtype.getBytes("ISO8859-1"));
			String hstype = CHF.showNull(request.getParameter("hstype"));
//			hstype = new String(hstype.getBytes("ISO8859-1"));

			String fx = CHF.showNull(request.getParameter("fx"));
			if (fx.equals("")) {
				return "方向不能为空!";
			}
			if (fx.equals("0")) {
				sql = "select if(direction=0,(Balance),direction*(Balance)) from c_assitementryacc where SubMonth=12 and AccPackageID=? ";
			} else if (fx.equals("1")) {
				sql = "select DebitBalance from c_assitementryacc where SubMonth=12 and AccPackageID=? ";
			} else if (fx.equals("-1")) {
				sql = "select (-1)*CreditBalance from c_assitementryacc where SubMonth=12 and AccPackageID=? ";
			} else {
				return "方向输入错误！";
			}

			// sql = "select if(direction=0,(Balance),direction*(Balance)) from
			// c_assitementryacc where SubMonth=12 and AccPackageID=? ";

			if (kmtype.equals("") || kmtype.equals("1")) {

				if (!SRS.isNull("c_accpkgsubject", "SubjectName", km, "")) {
					km = SRS.SubjectResult(km);
				} else {
					km = "('" + km + "')";
				}

				sql += " and AccID =(select  subjectid from c_accpkgsubject where  subjectname in "
						+ km + "and AccPackageID = '" + pkgid + "')  ";
			} else if (kmtype.equals("2")) {

				if (!SRS.isNull("c_accpkgsubject", "SubjectFullName", km, "")) {
					km = SRS.SubjectResult(km);
				} else {
					km = "('" + km + "')";
				}

				sql += " and AccID =(select  subjectid from c_accpkgsubject where  subjectfullname in "
						+ km + "and AccPackageID = '" + pkgid + "')  ";
			} else if (kmtype.equals("3")) {
				sql += " and AccID='" + km + "'";
			} else {
				return "科目类型输入错误！";
			}
			if (hstype.equals("") || hstype.equals("1")) {
				if (!SRS.isNull("c_assitem", "000", hs, km)) {
					hs = SRS.TextKey(hs);
				} else {
					hs = "('" + hs + "')";
				}

				sql += " and AssItemName in " + hs;
			} else if (hstype.equals("2")) {
				if (!SRS.isNull("c_assitem", "111", hs, km)) {
					hs = SRS.TextKey(hs);
				} else {
					hs = "('" + hs + "')";
				}

				sql += " and AssItemID = (select DISTINCT AssItemID from c_assitem where  asstotalname in "
						+ hs + " and AccPackageID = '" + pkgid + "') ";
			} else if (hstype.equals("3")) {
				sql += " and assitemid= '" + hs + "'";
			} else {
				return "核算项目类型输入错误！";
			}

			//org.util.Debug.prtOut("getHsNms:" + sql);			
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, pkgid);
			// ps.setString(i++, km);
			// ps.setString(i++, hs);
			rs = ps.executeQuery();
			i = 0;
			while (rs.next()) {
				i++;
				if (i > 1) {
					result = "核算年末数出错！";
					break;
				}
				result = CHF.showNull(rs.getString(1));
			}
			
			if("".equals(result)){
				result="0";//result = "科目既不是标准科目，也不是用户科目或核算不是用户核算，请修改";
			}
			//org.util.Debug.prtOut("getNcs result:" + result);
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
	}

	//	 取年末数(上年)
	private String getNmsGo() throws Exception {
		DbUtil.checkConn(conn);
		String result = "";
		String sql = "";		
		PreparedStatement ps = null;
		ResultSet rs = null;
		pkgid = SRS.getNewPackageID(pkgid);
		try {
			if("该单位没有去年的帐套!".equals(pkgid)){
				return "0";
			}
			String subname = CHF.showNull(request.getParameter("km"));
			if (subname.equals("")) {
				return "科目不能为空!";
			}
			/**
			 *科目对照  
			 */
			String skm = changeSubjectName(subname);
			if(!"".equals(skm)){
				subname = skm;
			}
			
			String subtype = CHF.showNull(request.getParameter("type"));
//			subname = new String(subname.getBytes("ISO8859-1"));
//			subtype = new String(subtype.getBytes("ISO8859-1"));

			String sTab = "";
			String bz = CHF.showNull(request.getParameter("bz"));
//			bz = new String(bz.getBytes("ISO8859-1"));
			if (bz.equals("0") || bz.equals("") || bz.equals("本位币")) {
				sTab = " c_Account ";
				bz = "0";
			} else {
				sTab = " c_AccountAll ";
			}
			
			String fx = CHF.showNull(request.getParameter("fx"));
			if (fx.equals("")) {
				return "方向不能为空!";
			}
			if (fx.equals("0")) {
				sql = "select if(direction=0,(Balance),direction*Balance) from "
						+ sTab
						+ " where SubMonth=12 and DataName='"
						+ bz
						+ "' and AccPackageID=? ";
			} else if (fx.equals("1")) {
				sql = "select DebitBalance from " + sTab
						+ " where SubMonth=12 and DataName='" + bz
						+ "' and AccPackageID=? ";
			} else if (fx.equals("-1")) {
				sql = "select (-1)*CreditBalance from " + sTab
						+ " where SubMonth=12 and DataName='" + bz
						+ "' and AccPackageID=? ";
			} else {
				return "方向输入错误！";
			}

			// sql =
			// "select if(direction=0,(Balance),direction*Balance) from
			// c_Account where SubMonth=12 and AccPackageID=? ";
			if (subtype.equals("") || subtype.equals("1")) {
				if (!SRS.isNull("c_accpkgsubject", "SubjectName", subname, "")) {
					subname = SRS.SubjectResult(subname);
				} else {
					subname = "('" + subname + "')";
				}

				sql += "and AccName in " + subname;

			} else if (subtype.equals("2")) {
				if (!SRS.isNull("c_accpkgsubject", "SubjectFullName", subname, "")) {
					subname = SRS.SubjectResult(subname);
				} else {
					subname = "('" + subname + "')";
				}

				sql += " and SubjectID in (select SubjectID from c_accpkgsubject where AccPackageID='"
						+ pkgid + "' and SubjectFullName in " + subname + ")";

			} else if (subtype.equals("3")) {
				sql += " and SubjectID = " + subname;
			} else {
				return "科目类型输入错误！";
			}

			//org.util.Debug.prtOut("getNms:" + sql);
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, pkgid);
			// ps.setString(i++, subname);

			rs = ps.executeQuery();
			i = 0;
			while (rs.next()) {
				i++;
				if (i > 1) {
					result = "上年科目年末数出错！";
					break;
				}
				result = CHF.showNull(rs.getString(1));
			}
			
			if("".equals(result)){
				result = "0";//result = "该科目既不是标准科目，也不是用户科目，请修改科目名";
			}
			//org.util.Debug.prtOut("getNcs result:" + result);
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
	}

//	 取年末数
	private String getNms() throws Exception {
		DbUtil.checkConn(conn);
		String result = "";
		String sql = "";		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String subname = CHF.showNull(request.getParameter("km"));
			if (subname.equals("")) {
				return "科目不能为空!";
			}
			/**
			 *科目对照  
			 */
			String skm = changeSubjectName(subname);
			if(!"".equals(skm)){
				subname = skm;
			}
			String subtype = CHF.showNull(request.getParameter("type"));
//			subname = new String(subname.getBytes("ISO8859-1"));
//			subtype = new String(subtype.getBytes("ISO8859-1"));

			String sTab = "";
			String bz = CHF.showNull(request.getParameter("bz"));
//			bz = new String(bz.getBytes("ISO8859-1"));
			if (bz.equals("0") || bz.equals("") || bz.equals("本位币")) {
				sTab = " c_Account ";
				bz = "0";
			} else {
				sTab = " c_AccountAll ";
			}
			
			String fx = CHF.showNull(request.getParameter("fx"));
			if (fx.equals("")) {
				return "方向不能为空!";
			}
			if (fx.equals("0")) {
				sql = "select if(direction=0,(Balance),direction*Balance) from "
						+ sTab
						+ " where SubMonth=12 and DataName='"
						+ bz
						+ "' and AccPackageID=? ";
			} else if (fx.equals("1")) {
				sql = "select DebitBalance from " + sTab
						+ " where SubMonth=12 and DataName='" + bz
						+ "' and AccPackageID=? ";
			} else if (fx.equals("-1")) {
				sql = "select (-1)*CreditBalance from " + sTab
						+ " where SubMonth=12  and DataName='" + bz
						+ "' and AccPackageID=? ";
			} else {
				return "方向输入错误！";
			}
			if (subtype.equals("") || subtype.equals("1")) {
				if (!SRS.isNull("c_accpkgsubject", "SubjectName", subname, "")) {
					subname = SRS.SubjectResult(subname);
				} else {
					subname = "('" + subname + "')";
				}
				sql += "and AccName in " + subname;

			} else if (subtype.equals("2")) {
				if (!SRS.isNull("c_accpkgsubject", "SubjectFullName", subname, "")) {
					subname = SRS.SubjectResult(subname);
				} else {
					subname = "('" + subname + "')";
				}

				sql += " and SubjectID in (select SubjectID from c_accpkgsubject where AccPackageID='"
						+ pkgid + "' and SubjectFullName in " + subname + ")";

			} else if (subtype.equals("3")) {
				sql += " and SubjectID = " + subname;
			} else {
				return "科目类型输入错误！";
			}

			//org.util.Debug.prtOut("getNms:" + sql);
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, pkgid);
			// ps.setString(i++, subname);

			rs = ps.executeQuery();
			i = 0;
			while (rs.next()) {
				i++;
				if (i > 1) {
					result = "科目年末数出错！";
					break;
				}
				result = CHF.showNull(rs.getString(1));
			}
			
			if("".equals(result)){
				result = "0";//result = "该科目既不是标准科目，也不是用户科目，请修改科目名";
			}
			//org.util.Debug.prtOut("getNcs result:" + result);
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
	}

//	 取核算年初数(上年)
	private String getHsNcsGo()  throws Exception {
		DbUtil.checkConn(conn);
		String result = "";
		String sql = "";		
		PreparedStatement ps = null;
		ResultSet rs = null;
		pkgid = SRS.getNewPackageID(pkgid);
		try {
			if("该单位没有去年的帐套!".equals(pkgid)){
				return "0";
			}
			String km = CHF.showNull(request.getParameter("km"));
			if (km.equals("")) {
				return "科目不能为空!";
			}
			/**
			 *科目对照  
			 */
			String skm = changeSubjectName(km);
			if(!"".equals(skm)){
				km = skm;
			}
//			km = new String(km.getBytes("ISO8859-1"));

			String hs = CHF.showNull(request.getParameter("hs"));
			if (hs.equals("")) {
				return "核算项目不能为空!";
			}
//			hs = new String(hs.getBytes("ISO8859-1"));

			String kmtype = CHF.showNull(request.getParameter("type"));
//			kmtype = new String(kmtype.getBytes("ISO8859-1"));
			String hstype = CHF.showNull(request.getParameter("hstype"));
//			hstype = new String(hstype.getBytes("ISO8859-1"));

			String fx = CHF.showNull(request.getParameter("fx"));
			if (fx.equals("")) {
				return "方向不能为空!";
			}
//			fx = new String(fx.getBytes("ISO8859-1"));
			
			if (fx.equals("0")) {
				sql = "select if(direction=0,(DebitRemain+CreditRemain),direction*(DebitRemain+CreditRemain)) from c_assitementryacc where SubMonth=1 and AccPackageID=? ";
			} else if (fx.equals("1")) {
				sql = "select DebitRemain from c_assitementryacc where SubMonth=1 and AccPackageID=? ";
			} else if (fx.equals("-1")) {
				sql = "select (-1)*CreditRemain from c_assitementryacc where SubMonth=1 and AccPackageID=? ";
			} else {
				return "方向输入错误！";
			}

			if (kmtype.equals("") || kmtype.equals("1")) {
				if (!SRS.isNull("c_accpkgsubject", "SubjectName", km, "")) {
					km = SRS.SubjectResult(km);
				} else {
					km = "('" + km + "')";
				}

				sql += " and AccID =(select  subjectid from c_accpkgsubject where  subjectname in "
						+ km + "and AccPackageID = '" + pkgid + "') ";
			} else if (kmtype.equals("2")) {
				if (!SRS.isNull("c_accpkgsubject", "SubjectFullName", km, "")) {
					km = SRS.SubjectResult(km);
				} else {
					km = "('" + km + "')";
				}

				sql += " and AccID =(select  subjectid from c_accpkgsubject where  subjectfullname in "
						+ km + "and AccPackageID = '" + pkgid + "') ";
			} else if (kmtype.equals("3")) {
				sql += " and AccID='" + km + "' ";
			} else {
				return "科目类型输入错误！";
			}
			if (hstype.equals("") || hstype.equals("1")) {
				if (!SRS.isNull("c_assitem", "000", hs, km)) {
					hs = SRS.TextKey(hs);
				} else {
					hs = "('" + hs + "')";
				}

				sql += " and AssItemName in " + hs;
			} else if (hstype.equals("2")) {
				if (!SRS.isNull("c_assitem", "111", hs, km)) {
					hs = SRS.TextKey(hs);
				} else {
					hs = "('" + hs + "')";
				}

				sql += " and AssItemID = (select DISTINCT AssItemID from c_assitem where  asstotalname in "
						+ hs + " and AccPackageID = '" + pkgid + "') ";
			} else if (hstype.equals("3")) {
				sql += " and assitemid= '" + hs + "'";
			} else {
				return "核算项目类型输入错误！";
			}

			//org.util.Debug.prtOut("getHsNcs:" + sql);
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, pkgid);
			// ps.setString(i++, km);
			// ps.setString(i++, hs);
			// ps.setString(i++, subname);

			rs = ps.executeQuery();
			i = 0;
			while (rs.next()) {
				i++;
				if (i > 1) {
					result = "上年核算年初数出错！";
					break;
				}
				result = CHF.showNull(rs.getString(1));
			}
			
			if("".equals(result)){
				result="0";//result = "科目既不是标准科目，也不是用户科目或核算不是用户核算，请修改";
			}
			//org.util.Debug.prtOut("getNcs result:" + result);
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
	}

	//	 取核算年初数
	private String getHsNcs()  throws Exception {
		DbUtil.checkConn(conn);
		String result = "";
		String sql = "";		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String km = CHF.showNull(request.getParameter("km"));
			if (km.equals("")) {
				return "科目不能为空!";
			}
			/**
			 *科目对照  
			 */
			String skm = changeSubjectName(km);
			if(!"".equals(skm)){
				km = skm;
			}
//			km = new String(km.getBytes("ISO8859-1"));

			String hs = CHF.showNull(request.getParameter("hs"));
			if (hs.equals("")) {
				return "核算项目不能为空!";
			}
//			hs = new String(hs.getBytes("ISO8859-1"));

			String kmtype = CHF.showNull(request.getParameter("type"));
//			kmtype = new String(kmtype.getBytes("ISO8859-1"));
			String hstype = CHF.showNull(request.getParameter("hstype"));
//			hstype = new String(hstype.getBytes("ISO8859-1"));

			String fx = CHF.showNull(request.getParameter("fx"));
			if (fx.equals("")) {
				return "方向不能为空!";
			}
//			fx = new String(fx.getBytes("ISO8859-1"));

			
			if (fx.equals("0")) {
				sql = "select if(direction=0,(DebitRemain+CreditRemain),direction*(DebitRemain+CreditRemain)) from c_assitementryacc where SubMonth=1 and AccPackageID=? ";
			} else if (fx.equals("1")) {
				sql = "select DebitRemain from c_assitementryacc where SubMonth=1 and AccPackageID=? ";
			} else if (fx.equals("-1")) {
				sql = "select (-1)*CreditRemain from c_assitementryacc where SubMonth=1 and AccPackageID=? ";
			} else {
				return "方向输入错误！";
			}

			if (kmtype.equals("") || kmtype.equals("1")) {
				if (!SRS.isNull("c_accpkgsubject", "SubjectName", km, "")) {
					km = SRS.SubjectResult(km);
					
				} else {
					km = "('" + km + "')";
				}
				sql += " and AccID =(select  subjectid from c_accpkgsubject where  subjectname in "
						+ km + " and AccPackageID = '" + pkgid + "') ";
			} else if (kmtype.equals("2")) {
				if (!SRS.isNull("c_accpkgsubject", "SubjectFullName", km, "")) {
					km = SRS.SubjectResult(km);
				} else {
					km = "('" + km + "')";
				}

				sql += " and AccID =(select  subjectid from c_accpkgsubject where  subjectfullname in "
						+ km + " and AccPackageID = '" + pkgid + "') ";
			} else if (kmtype.equals("3")) {
				sql += " and AccID='" + km + "'";
			} else {
				return "科目类型输入错误！";
			}
			if (hstype.equals("") || hstype.equals("1")) {
				if (!SRS.isNull("c_assitem", "000", hs, km)) {
					hs = SRS.TextKey(hs);
				} else {
					hs = "('" + hs + "')";
				}
				sql += " and AssItemName in " + hs;
			} else if (hstype.equals("2")) {
				if (!SRS.isNull("c_assitem", "111", hs, km)) {
					hs = SRS.TextKey(hs);
				} else {
					hs = "('" + hs + "')";
				}
				sql += " and AssItemid = (select DISTINCT AssItemid from c_assitem where  asstotalname in "
						+ hs + " and AccPackageID = '" + pkgid + "') ";
			} else if (hstype.equals("3")) {
				sql += " and assitemid='" + hs + "'";
			} else {
				return "核算项目类型输入错误！";
			}

			//org.util.Debug.prtOut("getHsNcs:" + sql);
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, pkgid);
			// ps.setString(i++, km);
			// ps.setString(i++, hs);
			// ps.setString(i++, subname);

			rs = ps.executeQuery();
			i = 0;
			while (rs.next()) {
				i++;
				if (i > 1) {
					result = "核算年初数出错！";
					break;
				}
				result = CHF.showNull(rs.getString(1));
			}
			
			if("".equals(result)){
				result="0";//result = "科目既不是标准科目，也不是用户科目或核算不是用户核算，请修改";
			}
			//org.util.Debug.prtOut("getNcs result:" + result);
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
	}
	
	
//	 取年初数(上年)
	private String getNcsGo() throws Exception {
		DbUtil.checkConn(conn);
		String result = "";
		String sql = "";		
		PreparedStatement ps = null;
		ResultSet rs = null;
		pkgid = SRS.getNewPackageID(pkgid);
		try {
			if("该单位没有去年的帐套!".equals(pkgid)){
				return "0";
			}
			String subname = CHF.showNull(request.getParameter("km"));
			if (subname.equals("")) {
				return "科目不能为空!";
			}
			/**
			 *科目对照  
			 */
			String skm = changeSubjectName(subname);
			if(!"".equals(skm)){
				subname = skm;
			}
			String subtype = CHF.showNull(request.getParameter("type"));
//			subname = new String(subname.getBytes("ISO8859-1"));
//			subtype = new String(subtype.getBytes("ISO8859-1"));

			String fx = CHF.showNull(request.getParameter("fx"));
			if (fx.equals("")) {
				return "方向不能为空!";
			}

			String sTab = "";
			String bz = CHF.showNull(request.getParameter("bz"));
//			bz = new String(bz.getBytes("ISO8859-1"));
			if (bz.equals("0") || bz.equals("") || bz.equals("本位币")) {
				sTab = " c_Account ";
				bz = "0";
			} else {
				sTab = " c_AccountAll ";
			}
			
			if (fx.equals("0")) {
				sql = "select if(direction=0,(DebitRemain+CreditRemain),direction*(DebitRemain+CreditRemain)) from "
						+ sTab
						+ " where SubMonth=1 and DataName='"
						+ bz
						+ "' and AccPackageID=? ";
			} else if (fx.equals("1")) {
				sql = "select DebitRemain from " + sTab
						+ " where SubMonth=1 and DataName='" + bz
						+ "' and AccPackageID=? ";
			} else if (fx.equals("-1")) {
				sql = "select (-1)*(CreditRemain) from " + sTab
						+ " where SubMonth=1 and DataName='" + bz
						+ "' and AccPackageID=? ";
			} else {
				return "方向输入错误！";
			}

			if (subtype.equals("") || subtype.equals("1")) {
				if (!SRS.isNull("c_accpkgsubject", "SubjectName", subname, "")) {
					subname = SRS.SubjectResult(subname);
				} else {
					subname = "('" + subname + "')";
				}

				sql += "and AccName in " + subname;

			} else if (subtype.equals("2")) {
				if (!SRS.isNull("c_accpkgsubject", "SubjectFullName", subname, "")) {
					subname = SRS.SubjectResult(subname);
				} else {
					subname = "('" + subname + "')";
				}

				sql += " and SubjectID in (select SubjectID from c_accpkgsubject where AccPackageID='"
						+ pkgid + "' and SubjectFullName in " + subname + " )";
			} else if (subtype.equals("3")) {
				sql += " and SubjectID = " + subname;
			} else {
				return "科目类型输入错误！";
			}

			//org.util.Debug.prtOut("getNcs:" + sql);
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, pkgid);
			// ps.setString(i++, subname);

			rs = ps.executeQuery();
			i = 0;
			while (rs.next()) {
				i++;
				if (i > 1) {
					result = "上年科目年初数出错！";
					break;
				}
				result = CHF.showNull(rs.getString(1));
			}
			
			if("".equals(result)){
				result = "0";//result = "该科目既不是标准科目，也不是用户科目，请修改科目名";
			}
			//org.util.Debug.prtOut("getNcs result:" + result);
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
	}

//	 取年初数
	private String getNcs() throws Exception {
		DbUtil.checkConn(conn);
		String result = "";
		String sql = "";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String subname = CHF.showNull(request.getParameter("km"));
			if (subname.equals("")) {
				return "科目不能为空!";
			}
			/**
			 *科目对照  
			 */
			String skm = changeSubjectName(subname);
			if(!"".equals(skm)){
				subname = skm;
			}
			String subtype = CHF.showNull(request.getParameter("type"));
//			subname = new String(subname.getBytes("UTF-8"));
//			subname = new String(subname.getBytes("ISO8859-1"));
//			subname = new String(subname.getBytes("GBK"));
			//System.out.println("subname "+subname);
//			subtype = new String(subtype.getBytes("ISO8859-1"));

			String fx = CHF.showNull(request.getParameter("fx"));
			if (fx.equals("")) {
				return "方向不能为空!";
			}
			/**
			 * 兼容外币
			 */
			String sTab = "";
			String bz = CHF.showNull(request.getParameter("bz"));
//			bz = new String(bz.getBytes("ISO8859-1"));
			if (bz.equals("0") || bz.equals("") || bz.equals("本位币")) {
				sTab = " c_Account ";
				bz = "0";
			} else {
				sTab = " c_AccountAll ";
			}
			
			if (fx.equals("0")) {
				sql = "select if(direction=0,(DebitRemain+CreditRemain),direction*(DebitRemain+CreditRemain)) from "
						+ sTab
						+ " where SubMonth=1 and DataName='"
						+ bz
						+ "' and AccPackageID='"+this.pkgid+"'  ";
			} else if (fx.equals("1")) {
				sql = "select DebitRemain from " + sTab
						+ " where SubMonth=1 and DataName='" + bz
						+ "' and AccPackageID='"+this.pkgid+"' ";
			} else if (fx.equals("-1")) {
				sql = "select (-1)*(CreditRemain) from " + sTab
						+ " where SubMonth=1 and DataName='" + bz
						+ "' and AccPackageID='"+this.pkgid+"'  ";
			} else {
				return "方向输入错误！";
			}
			
			if (subtype.equals("") || subtype.equals("1")) {
				if (!SRS.isNull("c_accpkgsubject", "SubjectName", subname, "")) {
					subname = SRS.SubjectResult(subname);
				} else {
					subname = "('" + subname + "')";
				}
				sql += "and AccName in " + subname;
			} else if (subtype.equals("2")) {
				if (!SRS.isNull("c_accpkgsubject", "SubjectFullName", subname, "")) {
					subname = SRS.SubjectResult(subname);
				} else {
					subname = "('" + subname + "')";
				}

				sql += " and SubjectID in (select SubjectID from c_accpkgsubject where AccPackageID='"
						+ pkgid + "' and SubjectFullName in " + subname + " )";
			} else if (subtype.equals("3")) {
				sql += " and SubjectID = '" + subname + "' ";
			} else {
				return "科目类型输入错误！";
			}
			
			//org.util.Debug.prtOut("getNcs:" + sql);
			ps = conn.prepareStatement(sql);
			
			rs = ps.executeQuery();
			int i = 0;
			while (rs.next()) {
				i++;
				if (i > 1) {
					result = "科目年初数出错！";
					break;
				}
				result = CHF.showNull(rs.getString(1));
			}
			
			if("".equals(result)){
				result = "0";//result = "该科目既不是标准科目，也不是用户科目，请修改科目名";
			}
			 
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
	}

	//-----------------------------------------------------------------------------------
	//报表值
	//-----------------------------------------------------------------------------------
	
//	 取年初数（报表值）
	private String getBbNcs() throws Exception {
		String result = "";
		String sql = "";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			
			String AuditTimeBegin = "";
			String AuditTimeEnd = "";
			
			
			sql = "select * from z_project where projectid='"+this.proid+"'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				AuditTimeBegin = rs.getString("AuditTimeBegin");
				AuditTimeEnd = rs.getString("AuditTimeEnd");
			}
			int bNumber = Integer.parseInt(AuditTimeBegin.substring(0,4)) * 12 + Integer.parseInt(AuditTimeBegin.substring(5,7));
			int eNumber = Integer.parseInt(AuditTimeEnd.substring(0,4)) * 12 + Integer.parseInt(AuditTimeEnd.substring(5,7));
			
			String subname = CHF.showNull(request.getParameter("km"));
			if (subname.equals("")) {
				return "科目不能为空!";
			}
			/**
			 *科目对照  
			 */
			String skm = changeSubjectName(subname);
			if(!"".equals(skm)){
				subname = skm;
			}
			String subtype = CHF.showNull(request.getParameter("type"));
//			subname = new String(subname.getBytes("ISO8859-1"));
//			subtype = new String(subtype.getBytes("ISO8859-1"));

			String fx = CHF.showNull(request.getParameter("fx"));
			if (fx.equals("")) {
				return "方向不能为空!";
			}
			/**
			 * 兼容外币
			 */
			String sTab = "";
			String bz = CHF.showNull(request.getParameter("bz"));
//			bz = new String(bz.getBytes("ISO8859-1"));
			if (bz.equals("0") || bz.equals("") || bz.equals("本位币")) {
				sTab = " c_Account ";
				bz = "0";
			} else {
				sTab = " c_AccountAll ";
			}

			
			if (fx.equals("0")) {
				sql = "select sum(if(direction2=0,(DebitRemain+CreditRemain),direction2*(DebitRemain+CreditRemain))) from "
						+ sTab
						+ " where SubYearMonth*12+SubMonth="+bNumber+" and DataName='"
						+ bz
						+ "' ";
			} else if (fx.equals("1")) {
				sql = "select sum(DebitRemain) from " + sTab
						+ " where SubYearMonth*12+SubMonth="+bNumber+" and DataName='" + bz
						+ "'  ";
			} else if (fx.equals("-1")) {
				sql = "select sum((-1)*(CreditRemain)) from " + sTab
						+ " where SubYearMonth*12+SubMonth="+bNumber+" and DataName='" + bz
						+ "' ";
			} else {
				return "方向输入错误！";
			}

			String stropt = SRS.getTextKeyAll(subname);
			String [] ss = stropt.split("\\|");
			String str = ss.length<=1?subname+"','":ss[1].replaceAll("`","','");
			subname = "'"+str.substring(0,str.length()-2);
			//and subjectid in("+str+")
			sql +=" and subjectid in ("+subname+") ";
			/*
			if (subtype.equals("") || subtype.equals("1")) {				
				sql +=" and subjectid in("+subname+") ";
			} else if (subtype.equals("2")) {
				sql +=" and subjectid in("+subname+") ";
			} else if (subtype.equals("3")) {
				sql +=" and subjectid in("+subname+") ";
			} else {
				return "科目类型输入错误！";
			}
			*/
			//org.util.Debug.prtOut("getBbNcs:" + sql);
			ps = conn.prepareStatement(sql);
			int i = 1;
//			ps.setString(i++, pkgid);
			// ps.setString(i++, subname);

			rs = ps.executeQuery();
			i = 0;
			while (rs.next()) {
				i++;
				if (i > 1) {
					result = "科目年初数出错！";
					break;
				}
				result = CHF.showNull(rs.getString(1));
			}
			
			if("".equals(result)){
				result="0";//result = "该科目既不是标准科目，也不是用户科目，请修改科目名";
			}
			//org.util.Debug.prtOut("getNcs result:" + result);
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
	}


//	 取年末数 （报表值）
	private String getBbNms() throws Exception {
		String result = "";
		String sql = "";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String AuditTimeBegin = "";
			String AuditTimeEnd = "";
			
			sql = "select * from z_project where projectid='"+this.proid+"'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				AuditTimeBegin = rs.getString("AuditTimeBegin");
				AuditTimeEnd = rs.getString("AuditTimeEnd");
			}
			int bNumber = Integer.parseInt(AuditTimeBegin.substring(0,4)) * 12 + Integer.parseInt(AuditTimeBegin.substring(5,7));
			int eNumber = Integer.parseInt(AuditTimeEnd.substring(0,4)) * 12 + Integer.parseInt(AuditTimeEnd.substring(5,7));

			
			String subname = CHF.showNull(request.getParameter("km"));
			if (subname.equals("")) {
				return "科目不能为空!";
			}
			/**
			 *科目对照  
			 */
			String skm = changeSubjectName(subname);
			if(!"".equals(skm)){
				subname = skm;
			}
			String subtype = CHF.showNull(request.getParameter("type"));
//			subname = new String(subname.getBytes("ISO8859-1"));
//			subtype = new String(subtype.getBytes("ISO8859-1"));

			String sTab = "";
			String bz = CHF.showNull(request.getParameter("bz"));
//			bz = new String(bz.getBytes("ISO8859-1"));
			if (bz.equals("0") || bz.equals("") || bz.equals("本位币")) {
				sTab = " c_Account ";
				bz = "0";
			} else {
				sTab = " c_AccountAll ";
			}

			
			String fx = CHF.showNull(request.getParameter("fx"));
			if (fx.equals("")) {
				return "方向不能为空!";
			}
			if (fx.equals("0")) {
				sql = "select sum(if(direction2=0,(Balance),direction2*Balance)) from "
						+ sTab
						+ " where SubYearMonth*12+SubMonth="+eNumber+" and DataName='"
						+ bz
						+ "'  ";
			} else if (fx.equals("1")) {
				sql = "select sum(DebitBalance) from " + sTab
						+ " where SubYearMonth*12+SubMonth="+eNumber+" and DataName='" + bz
						+ "'  ";
			} else if (fx.equals("-1")) {
				sql = "select sum((-1)*CreditBalance) from " + sTab
						+ " where SubYearMonth*12+SubMonth="+eNumber+"  and DataName='" + bz
						+ "' ";
			} else {
				return "方向输入错误！";
			}
			
			String stropt = SRS.getTextKeyAll(subname);
			//org.util.Debug.prtOut("stropt:" + stropt);
			String [] ss = stropt.split("\\|");
			String str = ss.length<=1?subname+"','":ss[1].replaceAll("`","','");
			subname = "'"+str.substring(0,str.length()-2);
			//and subjectid in("+str+")
			sql +=" and subjectid in ("+subname+") ";
			/*
			if (subtype.equals("") || subtype.equals("1")) {
				if (!isNull("c_accpkgsubject", "SubjectName", subname, "")) {
					subname = AST.TextKey(subname);
				} else {
					subname = "('" + subname + "')";
				}
				sql += "and AccName in " + subname;

			} else if (subtype.equals("2")) {
				if (!isNull("c_accpkgsubject", "SubjectFullName", subname, "")) {
					subname = AST.TextKey(subname);
				} else {
					subname = "('" + subname + "')";
				}

				sql += " and SubjectID in (select SubjectID from c_accpkgsubject where AccPackageID='"
						+ pkgid + "' and SubjectFullName in " + subname + ")";

			} else if (subtype.equals("3")) {
				sql += " and SubjectID = " + subname;
			} else {
				return "科目类型输入错误！";
			}
			 */
			//org.util.Debug.prtOut("getBbNms:" + sql);
			ps = conn.prepareStatement(sql);
			int i = 1;
//			ps.setString(i++, pkgid);
			// ps.setString(i++, subname);

			rs = ps.executeQuery();
			i = 0;
			while (rs.next()) {
				i++;
				if (i > 1) {
					result = "科目年末数出错！";
					break;
				}
				result = CHF.showNull(rs.getString(1));
			}
			
			if("".equals(result)){
				result="0";//result = "该科目既不是标准科目，也不是用户科目，请修改科目名";
			}
			//org.util.Debug.prtOut("getNcs result:" + result);
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
	}
	
//	 科目余额表年累计发生额 （报表值）
	private String getBbNlfse() throws Exception {
		String result = "";
		String sql = "";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String subname = CHF.showNull(request.getParameter("km"));
			if (subname.equals("")) {
				return "科目不能为空!";
			}
			/**
			 *科目对照  
			 */
			String skm = changeSubjectName(subname);
			if(!"".equals(skm)){
				subname = skm;
			}
			String subtype = CHF.showNull(request.getParameter("type"));
//			subname = new String(subname.getBytes("ISO8859-1"));
//			subtype = new String(subtype.getBytes("ISO8859-1"));

			String fx = CHF.showNull(request.getParameter("fx"));
			if (fx.equals("")) {
				return "方向不能为空!";
			}

			String sTab = "";
			String bz = CHF.showNull(request.getParameter("bz"));
//			bz = new String(bz.getBytes("ISO8859-1"));
			if (bz.equals("0") || bz.equals("") || bz.equals("本位币")) {
				sTab = " c_Account ";
				bz = "0";
			} else {
				sTab = " c_AccountAll ";
			}

			
			if (fx.equals("0")) {
				sql = "select sum(if(direction2=0,(DebitTotalOcc-CreditTotalOcc),direction2*(DebitTotalOcc-CreditTotalOcc)))  from "
						+ sTab
						+ " where SubMonth=12 and DataName='"
						+ bz
						+ "' and AccPackageID=? ";
			} else if (fx.equals("1")) {
				sql = "select sum(DebitTotalOcc)  from " + sTab
						+ " where SubMonth=12 and DataName='" + bz
						+ "' and AccPackageID=? ";
			} else if (fx.equals("-1")) {
				sql = "select sum(CreditTotalOcc)  from " + sTab
						+ " where SubMonth=12 and DataName='" + bz
						+ "' and AccPackageID=? ";
			} else {
				return "方向输入错误！";
			}

			String stropt = SRS.getTextKeyAll(subname);
			String [] ss = stropt.split("\\|");
			String str = ss.length<=1?subname+"','":ss[1].replaceAll("`","','");
			subname = "'"+str.substring(0,str.length()-2);
			//and subjectid in("+str+")
			sql +=" and subjectid in ("+subname+") ";
			
			/*
			if (subtype.equals("") || subtype.equals("1")) {
				if (!isNull("c_accpkgsubject", "SubjectName", subname, "")) {
					subname = AST.TextKey(subname);
				} else {
					subname = "('" + subname + "')";
				}

				sql += "and AccName in " + subname;

			} else if (subtype.equals("2")) {
				if (!isNull("c_accpkgsubject", "SubjectFullName", subname, "")) {
					subname = AST.TextKey(subname);
				} else {
					subname = "('" + subname + "')";
				}

				sql += " and SubjectID in (select SubjectID from c_accpkgsubject where AccPackageID='"
						+ pkgid + "' and SubjectFullName in " + subname + ")";

			} else if (subtype.equals("3")) {
				sql += " and SubjectID = " + subname;
			} else {
				return "科目类型输入错误！";
			}
			*/
			//org.util.Debug.prtOut("getBbNlfse:" + sql);
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, pkgid);
			// ps.setString(i++, subname);

			rs = ps.executeQuery();
			i = 0;
			while (rs.next()) {
				i++;
				if (i > 1) {
					result = "科目余额表年累计发生额出错！";
					break;
				}
				result = CHF.showNull(rs.getString(1));
			}
			
			if("".equals(result)){
				result = "0";//result = "该科目既不是标准科目，也不是用户科目，请修改科目名";
			}
			//org.util.Debug.prtOut("getNcs result:" + result);
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
	}
	
//	 取科目调整数 （报表值）
	private String getBbKmdzs() throws Exception {
		String result = "0.00";
		String sql = "";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			

			String km = CHF.showNull(request.getParameter("km"));
			if (km.equals("")) {
				return "科目不能为空!";
			}
			/**
			 *科目对照  
			 */
			String skm = changeSubjectName(km);
			if(!"".equals(skm)){
				km = skm;
			}
			String type = CHF.showNull(request.getParameter("type"));

			String tzlx = CHF.showNull(request.getParameter("tzlx"));
			if (tzlx.equals("")) {
				return "调整类型不能为空！";
			}

			String fx = CHF.showNull(request.getParameter("fx"));
			if (fx.equals("")) {
				return "方向不能为空！";
			}

			sql = " select ifnull(direction2,if(SUBSTRING(Property,2,1)=2,-1,1) ) direction2 " +
			" from c_accpkgsubject a left join c_account b " +
			" on a.subjectid = b.subjectid and b.submonth = 1 " +
			" where a.accpackageid='"+pkgid+"' and b.accpackageid='"+pkgid+"' " +
			" and  (a.SubjectID='"+km+"' or a.subjectname='"+km+"' or a.SubjectFullName='"+km+"') " +
			" union " +
			" select if(SUBSTRING(Property,2,1)=2,-1,1) from z_usesubject a" +
			" where projectid = '"+this.proid+"' " +
			" and  (a.SubjectID='"+km+"' or a.subjectname='"+km+"' or a.SubjectFullName='"+km+"') " ;
			String direction2 = "";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				direction2 = rs.getString(1);
			}
			DbUtil.close(rs);
            DbUtil.close(ps);
			
			sql = "";
			
			/**
			 * 兼容外币
			 */
			String sTab = "";
			String bz = CHF.showNull(request.getParameter("bz"));
//			bz = new String(bz.getBytes("ISO8859-1"));
			if (bz.equals("0") || bz.equals("") || bz.equals("本位币")) {
				sTab = " z_accountrectify ";
				bz = "0";
			} else {
				sTab = " z_accountallrectify ";
			}
			
			String str = "";
			if (fx.equals("1")) {
				str = "sum(debittotalocc" + tzlx+")";
				if (tzlx.equals("7")) {
					str = "sum(debittotalocc1 + debittotalocc2)";
				}
				if (tzlx.equals("8")) {
					str = "sum(debittotalocc4 + debittotalocc5)";
				}
			} else if (fx.equals("-1")) {
				str = "sum(credittotalocc" + tzlx+")";
				if (tzlx.equals("7")) {
					str = "sum(credittotalocc1 + credittotalocc2)";
				}
				if (tzlx.equals("8")) {
					str = "sum(credittotalocc4 + credittotalocc5)";
				}

			} else if (fx.equals("0")) {
				str = "sum("+direction2+"*(debittotalocc" + tzlx + " - credittotalocc" + tzlx+"))";
				if (tzlx.equals("7")) {
					str ="sum("+direction2+"*(debittotalocc1 - credittotalocc1 + debittotalocc2 - credittotalocc2)) occ ";
				}
				if (tzlx.equals("8")) {
					str ="sum("+direction2+"*(debittotalocc4 - credittotalocc4 + debittotalocc5 - credittotalocc5)) occ ";
				}

			} else {
				return "方向输入错误！";
			}

			String stropt = SRS.getTextKeyAll(km,proid);
			String [] ss = stropt.split("\\|");
			String str1 = ss.length<=1?km+"','":ss[1].replaceAll("`","','");
			km = "'"+str1.substring(0,str1.length()-2);
			//and subjectid in("+str+")
			
			sql = "select " + str + " from "+sTab+" where  projectid='"+proid+"'  ";
			sql +=" and subjectid in ("+km+") ";
			if(!"0".equals(bz)){
				sql += " and DataName='"+bz+"' ";
			}
			//org.util.Debug.prtOut("getBbKmdzs:" + sql);
			ps = conn.prepareStatement(sql);
			int i = 1;
			rs = ps.executeQuery();
			i = 0;
			while (rs.next()) {
				i++;
				if (i > 1) {
					result = "科目调整数出错！";
					break;
				}
				result = CHF.showNull(rs.getString(1));
			}

			if("".equals(result)){
				result = "0";//result = "该科目既不是标准科目，也不是用户科目，请修改科目名";
			}
			//org.util.Debug.prtOut("getNcs result:" + result);
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}

	}
	
//	 科目余额表本期期初数 （报表值）
	private String getBbBqqcs() throws Exception {
		String result = "";
		String sql = "";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String subname = CHF.showNull(request.getParameter("km"));
			if (subname.equals("")) {
				return "科目不能为空!";
			}
			/**
			 *科目对照  
			 */
			String skm = changeSubjectName(subname);
			if(!"".equals(skm)){
				subname = skm;
			}
			String subtype = CHF.showNull(request.getParameter("type"));
			String submonth = CHF.showNull(request.getParameter("qs"));
			if (submonth.equals("")) {
				return "期数不能为空!";
			}
			String fx = CHF.showNull(request.getParameter("fx"));
			if (fx.equals("")) {
				return "方向不能为空!";
			}
//			subname = new String(subname.getBytes("ISO8859-1"));
//			subtype = new String(subtype.getBytes("ISO8859-1"));
//			submonth = new String(submonth.getBytes("ISO8859-1"));
//			fx = new String(fx.getBytes("ISO8859-1"));

			String sTab = "";
			String bz = CHF.showNull(request.getParameter("bz"));
//			bz = new String(bz.getBytes("ISO8859-1"));
			if (bz.equals("0") || bz.equals("") || bz.equals("本位币")) {
				sTab = " c_Account ";
				bz = "0";
			} else {
				sTab = " c_AccountAll ";
			}

			
			if (fx.equals("0")) {
				sql = "select sum(if(direction2=0,(DebitRemain + CreditRemain),direction2*(DebitRemain + CreditRemain)))  from "
						+ sTab
						+ " where SubMonth=? and DataName='"
						+ bz
						+ "' and AccPackageID=? ";
			} else if (fx.equals("1")) {
				sql = "select sum(DebitRemain)  from " + sTab
						+ " where SubMonth=? and DataName='" + bz
						+ "' and AccPackageID=? ";
			} else if (fx.equals("-1")) {
				sql = "select sum((-1)*(CreditRemain))  from " + sTab
						+ " where SubMonth=? and DataName='" + bz
						+ "' and AccPackageID=? ";
			} else {
				return "方向输入错误！";
			}

			String stropt = SRS.getTextKeyAll(subname);
			String [] ss = stropt.split("\\|");
			String str1 = ss.length<=1?subname+"','":ss[1].replaceAll("`","','");
			subname = "'"+str1.substring(0,str1.length()-2);
			//and subjectid in("+str+")
			sql +=" and subjectid in ("+subname+") ";
			/*
			if (subtype.equals("") || subtype.equals("1")) {
				if (!isNull("c_accpkgsubject", "SubjectName", subname, "")) {
					subname = AST.TextKey(subname);
				} else {
					subname = "('" + subname + "')";
				}

				sql += "and AccName in " + subname;

			} else if (subtype.equals("2")) {
				if (!isNull("c_accpkgsubject", "SubjectFullName", subname, "")) {
					subname = AST.TextKey(subname);
				} else {
					subname = "('" + subname + "')";
				}

				sql += " and SubjectID in (select SubjectID from c_accpkgsubject where AccPackageID='"
						+ pkgid + "' and SubjectFullName in " + subname + ")";

			} else if (subtype.equals("3")) {
				sql += " and SubjectID = " + subname;
			} else {
				return "科目类型输入错误！";
			}
			*/
			//org.util.Debug.prtOut("getBbBqqcs:" + sql);
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, submonth);
			ps.setString(i++, pkgid);
			// ps.setString(i++, subname);
			rs = ps.executeQuery();

			i = 0;
			while (rs.next()) {
				i++;
				if (i > 1) {
					result = "科目余额表本期期初数出错！";
					break;
				}
				result = CHF.showNull(rs.getString(1));
			}
			
			if("".equals(result)){
				result = "0";//result = "该科目既不是标准科目，也不是用户科目，请修改科目名";
			}
			//org.util.Debug.prtOut("getNcs result:" + result);
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
	}
	
//	 科目余额表本期期末数（报表值）
	private String getBbBqqms() throws Exception {
		String result = "";
		String sql = "";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String subname = CHF.showNull(request.getParameter("km"));
			if (subname.equals("")) {
				return "科目不能为空!";
			}
			/**
			 *科目对照  
			 */
			String skm = changeSubjectName(subname);
			if(!"".equals(skm)){
				subname = skm;
			}
//			String subtype = CHF.showNull(request.getParameter("type"));
			String submonth = CHF.showNull(request.getParameter("qs"));
			if (submonth.equals("")) {
				return "期数不能为空!";
			}
			String fx = CHF.showNull(request.getParameter("fx"));
			if (fx.equals("")) {
				return "方向不能为空!";
			}
//			subname = new String(subname.getBytes("ISO8859-1"));
////			subtype = new String(subtype.getBytes("ISO8859-1"));
//			submonth = new String(submonth.getBytes("ISO8859-1"));
//			fx = new String(fx.getBytes("ISO8859-1"));

			String sTab = "";
			String bz = CHF.showNull(request.getParameter("bz"));
//			bz = new String(bz.getBytes("ISO8859-1"));
			if (bz.equals("0") || bz.equals("") || bz.equals("本位币")) {
				sTab = " c_Account ";
				bz = "0";
			} else {
				sTab = " c_AccountAll ";
			}

			
			if (fx.equals("0")) {
				sql = "select sum(if(direction2=0,(Balance),direction2*Balance)) from "
						+ sTab
						+ " where SubMonth=? and DataName='"
						+ bz
						+ "' and AccPackageID=? ";
			} else if (fx.equals("1")) {
				sql = "select sum(DebitBalance) from " + sTab
						+ " where SubMonth=? and DataName='" + bz
						+ "' and AccPackageID=? ";
			} else if (fx.equals("-1")) {
				sql = "select sum((-1)*CreditBalance) from " + sTab
						+ " where SubMonth=? and DataName='" + bz
						+ "' and AccPackageID=? ";
			} else {
				return "方向输入错误！";
			}

			String stropt = SRS.getTextKeyAll(subname);
			String [] ss = stropt.split("\\|");
			String str1 = ss.length<=1?subname+"','":ss[1].replaceAll("`","','");
			subname = "'"+str1.substring(0,str1.length()-2);
			//and subjectid in("+str+")
			sql +=" and subjectid in ("+subname+") ";
			/*
			if (subtype.equals("") || subtype.equals("1")) {
				if (!isNull("c_accpkgsubject", "SubjectName", subname, "")) {
					subname = AST.TextKey(subname);
				} else {
					subname = "('" + subname + "')";
				}

				sql += "and AccName in " + subname;

			} else if (subtype.equals("2")) {
				if (!isNull("c_accpkgsubject", "SubjectFullName", subname, "")) {
					subname = AST.TextKey(subname);
				} else {
					subname = "('" + subname + "')";
				}

				sql += " and SubjectID in (select SubjectID from c_accpkgsubject where AccPackageID='"
						+ pkgid + "' and SubjectFullName in " + subname + ")";

			} else if (subtype.equals("3")) {
				sql += " and SubjectID = " + subname;
			} else {
				return "科目类型输入错误！";
			}
			*/
			//org.util.Debug.prtOut("getBbBqqms:" + sql);
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, submonth);
			ps.setString(i++, pkgid);
			// ps.setString(i++, subname);
			rs = ps.executeQuery();

			i = 0;
			while (rs.next()) {
				i++;
				if (i > 1) {
					result = "科目余额表本期期末数出错！";
					break;
				}
				result = CHF.showNull(rs.getString(1));
			}
			
			if("".equals(result)){
				result = "0";//result = "该科目既不是标准科目，也不是用户科目，请修改科目名";
			}
			//org.util.Debug.prtOut("getNcs result:" + result);
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
	}
	
//	 科目余额表本期发生额（报表值）
	private String getBbBqfse() throws Exception {
		String result = "";
		String sql = "";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String subname = CHF.showNull(request.getParameter("km"));
			if (subname.equals("")) {
				return "科目不能为空!";
			}
			/**
			 *科目对照  
			 */
			String skm = changeSubjectName(subname);
			if(!"".equals(skm)){
				subname = skm;
			}
			String subtype = CHF.showNull(request.getParameter("type"));
			String submonth = CHF.showNull(request.getParameter("qs"));
			if (submonth.equals("")) {
				return "期数不能为空!";
			}
			String fx = CHF.showNull(request.getParameter("fx"));
			if (fx.equals("")) {
				return "方向不能为空!";
			}
//			subname = new String(subname.getBytes("ISO8859-1"));
//			subtype = new String(subtype.getBytes("ISO8859-1"));
//			submonth = new String(submonth.getBytes("ISO8859-1"));
//			fx = new String(fx.getBytes("ISO8859-1"));

			String sTab = "";
			String bz = CHF.showNull(request.getParameter("bz"));
//			bz = new String(bz.getBytes("ISO8859-1"));
			if (bz.equals("0") || bz.equals("") || bz.equals("本位币")) {
				sTab = " c_Account ";
				bz = "0";
			} else {
				sTab = " c_AccountAll ";
			}

			
			if (fx.equals("0")) {
				sql = "select sum(if(direction2=0,(DebitOcc - CreditOcc),direction2*(DebitOcc - CreditOcc))) from "
						+ sTab
						+ " where SubMonth=? and DataName='"
						+ bz
						+ "' and AccPackageID=? ";
			} else if (fx.equals("1")) {
				sql = "select sum(DebitOcc)  from " + sTab
						+ " where SubMonth=? and DataName='" + bz
						+ "' and AccPackageID=? ";
			} else if (fx.equals("-1")) {
				sql = "select sum(CreditOcc) from " + sTab
						+ " where SubMonth=? and DataName='" + bz
						+ "' and AccPackageID=? ";
			} else {
				return "方向输入错误！";
			}

			String stropt = SRS.getTextKeyAll(subname);
			String [] ss = stropt.split("\\|");
			String str1 = ss.length<=1?subname+"','":ss[1].replaceAll("`","','");
			subname = "'"+str1.substring(0,str1.length()-2);
			//and subjectid in("+str+")
			sql +=" and subjectid in ("+subname+") ";
			
			/*
			if (subtype.equals("") || subtype.equals("1")) {
				if (!isNull("c_accpkgsubject", "SubjectName", subname, "")) {
					subname = AST.TextKey(subname);
				} else {
					subname = "('" + subname + "')";
				}

				sql += "and AccName in " + subname;

			} else if (subtype.equals("2")) {
				if (!isNull("c_accpkgsubject", "SubjectFullName", subname, "")) {
					subname = AST.TextKey(subname);
				} else {
					subname = "('" + subname + "')";
				}

				sql += " and SubjectID in (select SubjectID from c_accpkgsubject where AccPackageID='"
						+ pkgid + "' and SubjectFullName in " + subname + ")";

			} else if (subtype.equals("3")) {
				sql += " and SubjectID = " + subname;
			} else {
				return "科目类型输入错误！";
			}
			*/
			//org.util.Debug.prtOut("getBbBqfse:" + sql);
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, submonth);
			ps.setString(i++, pkgid);
			// ps.setString(i++, subname);
			rs = ps.executeQuery();

			i = 0;
			while (rs.next()) {
				i++;
				if (i > 1) {
					result = "科目余额表年累计发生额出错！";
					break;
				}
				result = CHF.showNull(rs.getString(1));
			}
			
			if("".equals(result)){
				result = "0";//result = "该科目既不是标准科目，也不是用户科目，请修改科目名";
			}
			//org.util.Debug.prtOut("getNcs result:" + result);
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
	}
	
	// 取年初数(上年)（报表值）
	private String getBbNcsGo() throws Exception {
		String result = "";
		String sql = "";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		pkgid = SRS.getNewPackageID(pkgid);
		try {
			if("该单位没有去年的帐套!".equals(pkgid)){
				return "0";
			}
			
			String subname = CHF.showNull(request.getParameter("km"));
			if (subname.equals("")) {
				return "科目不能为空!";
			}
			/**
			 *科目对照  
			 */
			String skm = changeSubjectName(subname);
			if(!"".equals(skm)){
				subname = skm;
			}
//			String subtype = CHF.showNull(request.getParameter("type"));
//			subname = new String(subname.getBytes("ISO8859-1"));
//			subtype = new String(subtype.getBytes("ISO8859-1"));

			String fx = CHF.showNull(request.getParameter("fx"));
			if (fx.equals("")) {
				return "方向不能为空!";
			}

			String sTab = "";
			String bz = CHF.showNull(request.getParameter("bz"));
//			bz = new String(bz.getBytes("ISO8859-1"));
			if (bz.equals("0") || bz.equals("") || bz.equals("本位币")) {
				sTab = " c_Account ";
				bz = "0";
			} else {
				sTab = " c_AccountAll ";
			}

			
			if (fx.equals("0")) {
				sql = "select sum(if(direction2=0,(DebitRemain+CreditRemain),direction2*(DebitRemain+CreditRemain))) from "
						+ sTab
						+ " where SubMonth=1 and DataName='"
						+ bz
						+ "' and AccPackageID=? ";
			} else if (fx.equals("1")) {
				sql = "select sum(DebitRemain) from " + sTab
						+ " where SubMonth=1 and DataName='" + bz
						+ "' and AccPackageID=? ";
			} else if (fx.equals("-1")) {
				sql = "select sum((-1)*(CreditRemain)) from " + sTab
						+ " where SubMonth=1 and DataName='" + bz
						+ "' and AccPackageID=? ";
			} else {
				return "方向输入错误！";
			}
			String stropt = SRS.getTextKeyAll(subname);
			String [] ss = stropt.split("\\|");
			String str = ss.length<=1?subname+"','":ss[1].replaceAll("`","','");
			subname = "'"+str.substring(0,str.length()-2);
			//and subjectid in("+str+")
			sql +=" and subjectid in ("+subname+") ";
			/*
			if (subtype.equals("") || subtype.equals("1")) {
				if (!isNull("c_accpkgsubject", "SubjectName", subname, "")) {
					subname = AST.TextKey(subname);
				} else {
					subname = "('" + subname + "')";
				}

				sql += "and AccName in " + subname;

			} else if (subtype.equals("2")) {
				if (!isNull("c_accpkgsubject", "SubjectFullName", subname, "")) {
					subname = AST.TextKey(subname);
				} else {
					subname = "('" + subname + "')";
				}

				sql += " and SubjectID in (select SubjectID from c_accpkgsubject where AccPackageID='"
						+ pkgid + "' and SubjectFullName in " + subname + " )";
			} else if (subtype.equals("3")) {
				sql += " and SubjectID = " + subname;
			} else {
				return "科目类型输入错误！";
			}
			*/
			//org.util.Debug.prtOut("getBbNcsGo:" + sql);
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, pkgid);
			// ps.setString(i++, subname);

			rs = ps.executeQuery();
			i = 0;
			while (rs.next()) {
				i++;
				if (i > 1) {
					result = "上年科目年初数出错！";
					break;
				}
				result = CHF.showNull(rs.getString(1));
			}
			
			if("".equals(result)){
				result = "0";//result = "该科目既不是标准科目，也不是用户科目，请修改科目名";
			}
			//org.util.Debug.prtOut("getNcs result:" + result);
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
	}	
	
//	 取年末数 (上年)（报表值）
	private String getBbNmsGo() throws Exception {
		String result = "";
		String sql = "";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		pkgid = SRS.getNewPackageID(pkgid);
		try {
			if("该单位没有去年的帐套!".equals(pkgid)){
				return "0";
			}
			String subname = CHF.showNull(request.getParameter("km"));
			if (subname.equals("")) {
				return "科目不能为空!";
			}
			/**
			 *科目对照  
			 */
			String skm = changeSubjectName(subname);
			if(!"".equals(skm)){
				subname = skm;
			}
//			String subtype = CHF.showNull(request.getParameter("type"));
//			subname = new String(subname.getBytes("ISO8859-1"));
//			subtype = new String(subtype.getBytes("ISO8859-1"));

			String sTab = "";
			String bz = CHF.showNull(request.getParameter("bz"));
//			bz = new String(bz.getBytes("ISO8859-1"));
			if (bz.equals("0") || bz.equals("") || bz.equals("本位币")) {
				sTab = " c_Account ";
				bz = "0";
			} else {
				sTab = " c_AccountAll ";
			}

			
			String fx = CHF.showNull(request.getParameter("fx"));
			if (fx.equals("")) {
				return "方向不能为空!";
			}
			if (fx.equals("0")) {
				sql = "select sum(if(direction2=0,(Balance),direction2*Balance)) from "
						+ sTab
						+ " where SubMonth=12 and DataName='"
						+ bz
						+ "' and AccPackageID=? ";
			} else if (fx.equals("1")) {
				sql = "select sum(DebitBalance) from " + sTab
						+ " where SubMonth=12 and DataName='" + bz
						+ "' and AccPackageID=? ";
			} else if (fx.equals("-1")) {
				sql = "select sum((-1)*CreditBalance) from " + sTab
						+ " where SubMonth=12  and DataName='" + bz
						+ "' and AccPackageID=? ";
			} else {
				return "方向输入错误！";
			}
			
			String stropt = SRS.getTextKeyAll(subname);
			String [] ss = stropt.split("\\|");
			String str = ss.length<=1?subname+"','":ss[1].replaceAll("`","','");
			subname = "'"+str.substring(0,str.length()-2);
			//and subjectid in("+str+")
			sql +=" and subjectid in ("+subname+") ";
			/*
			if (subtype.equals("") || subtype.equals("1")) {
				if (!isNull("c_accpkgsubject", "SubjectName", subname, "")) {
					subname = AST.TextKey(subname);
				} else {
					subname = "('" + subname + "')";
				}
				sql += "and AccName in " + subname;

			} else if (subtype.equals("2")) {
				if (!isNull("c_accpkgsubject", "SubjectFullName", subname, "")) {
					subname = AST.TextKey(subname);
				} else {
					subname = "('" + subname + "')";
				}

				sql += " and SubjectID in (select SubjectID from c_accpkgsubject where AccPackageID='"
						+ pkgid + "' and SubjectFullName in " + subname + ")";

			} else if (subtype.equals("3")) {
				sql += " and SubjectID = " + subname;
			} else {
				return "科目类型输入错误！";
			}
			 */
			//org.util.Debug.prtOut("getBbNms:" + sql);
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, pkgid);
			// ps.setString(i++, subname);

			rs = ps.executeQuery();
			i = 0;
			while (rs.next()) {
				i++;
				if (i > 1) {
					result = "科目年末数出错！";
					break;
				}
				result = CHF.showNull(rs.getString(1));
			}
			
			if("".equals(result)){
				result = "0";//result = "该科目既不是标准科目，也不是用户科目，请修改科目名";
			}
			//org.util.Debug.prtOut("getNcs result:" + result);
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
	}
	
//	 科目余额表年累计发生额 (上年)（报表值）
	private String getBbNlfseGo() throws Exception {
		String result = "";
		String sql = "";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		pkgid = SRS.getNewPackageID(pkgid);
		try {
			if("该单位没有去年的帐套!".equals(pkgid)){
				return "0";
			}
			String subname = CHF.showNull(request.getParameter("km"));
			if (subname.equals("")) {
				return "科目不能为空!";
			}
			/**
			 *科目对照  
			 */
			String skm = changeSubjectName(subname);
			if(!"".equals(skm)){
				subname = skm;
			}
//			String subtype = CHF.showNull(request.getParameter("type"));
//			subname = new String(subname.getBytes("ISO8859-1"));
//			subtype = new String(subtype.getBytes("ISO8859-1"));

			String fx = CHF.showNull(request.getParameter("fx"));
			if (fx.equals("")) {
				return "方向不能为空!";
			}

			String sTab = "";
			String bz = CHF.showNull(request.getParameter("bz"));
//			bz = new String(bz.getBytes("ISO8859-1"));
			if (bz.equals("0") || bz.equals("") || bz.equals("本位币")) {
				sTab = " c_Account ";
				bz = "0";
			} else {
				sTab = " c_AccountAll ";
			}

			
			if (fx.equals("0")) {
				sql = "select sum(if(direction2=0,(DebitTotalOcc-CreditTotalOcc),direction2*(DebitTotalOcc-CreditTotalOcc)))  from "
						+ sTab
						+ " where SubMonth=12 and DataName='"
						+ bz
						+ "' and AccPackageID=? ";
			} else if (fx.equals("1")) {
				sql = "select sum(DebitTotalOcc)  from " + sTab
						+ " where SubMonth=12 and DataName='" + bz
						+ "' and AccPackageID=? ";
			} else if (fx.equals("-1")) {
				sql = "select sum(CreditTotalOcc)  from " + sTab
						+ " where SubMonth=12 and DataName='" + bz
						+ "' and AccPackageID=? ";
			} else {
				return "方向输入错误！";
			}

			String stropt = SRS.getTextKeyAll(subname);
			String [] ss = stropt.split("\\|");
			String str = ss.length<=1?subname+"','":ss[1].replaceAll("`","','");
			subname = "'"+str.substring(0,str.length()-2);
			//and subjectid in("+str+")
			sql +=" and subjectid in ("+subname+") ";
			
			/*
			if (subtype.equals("") || subtype.equals("1")) {
				if (!isNull("c_accpkgsubject", "SubjectName", subname, "")) {
					subname = AST.TextKey(subname);
				} else {
					subname = "('" + subname + "')";
				}

				sql += "and AccName in " + subname;

			} else if (subtype.equals("2")) {
				if (!isNull("c_accpkgsubject", "SubjectFullName", subname, "")) {
					subname = AST.TextKey(subname);
				} else {
					subname = "('" + subname + "')";
				}

				sql += " and SubjectID in (select SubjectID from c_accpkgsubject where AccPackageID='"
						+ pkgid + "' and SubjectFullName in " + subname + ")";

			} else if (subtype.equals("3")) {
				sql += " and SubjectID = " + subname;
			} else {
				return "科目类型输入错误！";
			}
			*/
			//org.util.Debug.prtOut("getBbNlfse:" + sql);
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, pkgid);
			// ps.setString(i++, subname);

			rs = ps.executeQuery();
			i = 0;
			while (rs.next()) {
				i++;
				if (i > 1) {
					result = "科目余额表年累计发生额出错！";
					break;
				}
				result = CHF.showNull(rs.getString(1));
			}
			
			if("".equals(result)){
				result = "0";//result = "该科目既不是标准科目，也不是用户科目，请修改科目名";
			}
			//org.util.Debug.prtOut("getNcs result:" + result);
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
	}
	
//	 科目余额表本期期初数 （报表值）(上年)
	private String getBbBqqcsGo() throws Exception {
		String result = "";
		String sql = "";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		pkgid = SRS.getNewPackageID(pkgid);
		try {
			if("该单位没有去年的帐套!".equals(pkgid)){
				return "0";
			}
			String subname = CHF.showNull(request.getParameter("km"));
			if (subname.equals("")) {
				return "科目不能为空!";
			}
			/**
			 *科目对照  
			 */
			String skm = changeSubjectName(subname);
			if(!"".equals(skm)){
				subname = skm;
			}
			String subtype = CHF.showNull(request.getParameter("type"));
			String submonth = CHF.showNull(request.getParameter("qs"));
			if (submonth.equals("")) {
				return "期数不能为空!";
			}
			String fx = CHF.showNull(request.getParameter("fx"));
			if (fx.equals("")) {
				return "方向不能为空!";
			}
//			subname = new String(subname.getBytes("ISO8859-1"));
//			subtype = new String(subtype.getBytes("ISO8859-1"));
//			submonth = new String(submonth.getBytes("ISO8859-1"));
//			fx = new String(fx.getBytes("ISO8859-1"));

			String sTab = "";
			String bz = CHF.showNull(request.getParameter("bz"));
//			bz = new String(bz.getBytes("ISO8859-1"));
			if (bz.equals("0") || bz.equals("") || bz.equals("本位币")) {
				sTab = " c_Account ";
				bz = "0";
			} else {
				sTab = " c_AccountAll ";
			}

			
			if (fx.equals("0")) {
				sql = "select sum(if(direction2=0,(DebitRemain + CreditRemain),direction2*(DebitRemain + CreditRemain)))  from "
						+ sTab
						+ " where SubMonth=? and DataName='"
						+ bz
						+ "' and AccPackageID=? ";
			} else if (fx.equals("1")) {
				sql = "select sum(DebitRemain)  from " + sTab
						+ " where SubMonth=? and DataName='" + bz
						+ "' and AccPackageID=? ";
			} else if (fx.equals("-1")) {
				sql = "select sum((-1)*(CreditRemain))  from " + sTab
						+ " where SubMonth=? and DataName='" + bz
						+ "' and AccPackageID=? ";
			} else {
				return "方向输入错误！";
			}

			String stropt = SRS.getTextKeyAll(subname);
			String [] ss = stropt.split("\\|");
			String str1 = ss.length<=1?subname+"','":ss[1].replaceAll("`","','");
			subname = "'"+str1.substring(0,str1.length()-2);
			//and subjectid in("+str+")
			sql +=" and subjectid in ("+subname+") ";
			/*
			if (subtype.equals("") || subtype.equals("1")) {
				if (!isNull("c_accpkgsubject", "SubjectName", subname, "")) {
					subname = AST.TextKey(subname);
				} else {
					subname = "('" + subname + "')";
				}

				sql += "and AccName in " + subname;

			} else if (subtype.equals("2")) {
				if (!isNull("c_accpkgsubject", "SubjectFullName", subname, "")) {
					subname = AST.TextKey(subname);
				} else {
					subname = "('" + subname + "')";
				}

				sql += " and SubjectID in (select SubjectID from c_accpkgsubject where AccPackageID='"
						+ pkgid + "' and SubjectFullName in " + subname + ")";

			} else if (subtype.equals("3")) {
				sql += " and SubjectID = " + subname;
			} else {
				return "科目类型输入错误！";
			}
			*/
			//org.util.Debug.prtOut("getBbBqqcs:" + sql);
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, submonth);
			ps.setString(i++, pkgid);
			// ps.setString(i++, subname);
			rs = ps.executeQuery();

			i = 0;
			while (rs.next()) {
				i++;
				if (i > 1) {
					result = "科目余额表本期期初数出错！";
					break;
				}
				result = CHF.showNull(rs.getString(1));
			}
			
			if("".equals(result)){
				result = "0";//result = "该科目既不是标准科目，也不是用户科目，请修改科目名";
			}
			//org.util.Debug.prtOut("getNcs result:" + result);
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
	}
	
//	 科目余额表本期期末数（报表值）(上年)
	private String getBbBqqmsGo() throws Exception {
		String result = "";
		String sql = "";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		pkgid = SRS.getNewPackageID(pkgid);
		try {
			if("该单位没有去年的帐套!".equals(pkgid)){
				return "0";
			}
			String subname = CHF.showNull(request.getParameter("km"));
			if (subname.equals("")) {
				return "科目不能为空!";
			}
			/**
			 *科目对照  
			 */
			String skm = changeSubjectName(subname);
			if(!"".equals(skm)){
				subname = skm;
			}
//			String subtype = CHF.showNull(request.getParameter("type"));
			String submonth = CHF.showNull(request.getParameter("qs"));
			if (submonth.equals("")) {
				return "期数不能为空!";
			}
			String fx = CHF.showNull(request.getParameter("fx"));
			if (fx.equals("")) {
				return "方向不能为空!";
			}
//			subname = new String(subname.getBytes("ISO8859-1"));
////			subtype = new String(subtype.getBytes("ISO8859-1"));
//			submonth = new String(submonth.getBytes("ISO8859-1"));
//			fx = new String(fx.getBytes("ISO8859-1"));

			String sTab = "";
			String bz = CHF.showNull(request.getParameter("bz"));
//			bz = new String(bz.getBytes("ISO8859-1"));
			if (bz.equals("0") || bz.equals("") || bz.equals("本位币")) {
				sTab = " c_Account ";
				bz = "0";
			} else {
				sTab = " c_AccountAll ";
			}

			
			if (fx.equals("0")) {
				sql = "select sum(if(direction2=0,(Balance),direction2*Balance)) from "
						+ sTab
						+ " where SubMonth=? and DataName='"
						+ bz
						+ "' and AccPackageID=? ";
			} else if (fx.equals("1")) {
				sql = "select sum(DebitBalance) from " + sTab
						+ " where SubMonth=? and DataName='" + bz
						+ "' and AccPackageID=? ";
			} else if (fx.equals("-1")) {
				sql = "select sum((-1)*CreditBalance) from " + sTab
						+ " where SubMonth=? and DataName='" + bz
						+ "' and AccPackageID=? ";
			} else {
				return "方向输入错误！";
			}

			String stropt = SRS.getTextKeyAll(subname);
			String [] ss = stropt.split("\\|");
			String str1 = ss.length<=1?subname+"','":ss[1].replaceAll("`","','");
			subname = "'"+str1.substring(0,str1.length()-2);
			//and subjectid in("+str+")
			sql +=" and subjectid in ("+subname+") ";
			/*
			if (subtype.equals("") || subtype.equals("1")) {
				if (!isNull("c_accpkgsubject", "SubjectName", subname, "")) {
					subname = AST.TextKey(subname);
				} else {
					subname = "('" + subname + "')";
				}

				sql += "and AccName in " + subname;

			} else if (subtype.equals("2")) {
				if (!isNull("c_accpkgsubject", "SubjectFullName", subname, "")) {
					subname = AST.TextKey(subname);
				} else {
					subname = "('" + subname + "')";
				}

				sql += " and SubjectID in (select SubjectID from c_accpkgsubject where AccPackageID='"
						+ pkgid + "' and SubjectFullName in " + subname + ")";

			} else if (subtype.equals("3")) {
				sql += " and SubjectID = " + subname;
			} else {
				return "科目类型输入错误！";
			}
			*/
			//org.util.Debug.prtOut("getBbBqqms:" + sql);
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, submonth);
			ps.setString(i++, pkgid);
			// ps.setString(i++, subname);
			rs = ps.executeQuery();

			i = 0;
			while (rs.next()) {
				i++;
				if (i > 1) {
					result = "科目余额表本期期末数出错！";
					break;
				}
				result = CHF.showNull(rs.getString(1));
			}
			
			if("".equals(result)){
				result = "0";//result = "该科目既不是标准科目，也不是用户科目，请修改科目名";
			}
			//org.util.Debug.prtOut("getNcs result:" + result);
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
	}
	
//	 科目余额表本期发生额（报表值）(上年)
	private String getBbBqfseGo() throws Exception {
		String result = "";
		String sql = "";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		pkgid = SRS.getNewPackageID(pkgid);
		try {
			if("该单位没有去年的帐套!".equals(pkgid)){
				return "0";
			}
			String subname = CHF.showNull(request.getParameter("km"));
			if (subname.equals("")) {
				return "科目不能为空!";
			}
			/**
			 *科目对照  
			 */
			String skm = changeSubjectName(subname);
			if(!"".equals(skm)){
				subname = skm;
			}
			String subtype = CHF.showNull(request.getParameter("type"));
			String submonth = CHF.showNull(request.getParameter("qs"));
			if (submonth.equals("")) {
				return "期数不能为空!";
			}
			String fx = CHF.showNull(request.getParameter("fx"));
			if (fx.equals("")) {
				return "方向不能为空!";
			}
//			subname = new String(subname.getBytes("ISO8859-1"));
//			subtype = new String(subtype.getBytes("ISO8859-1"));
//			submonth = new String(submonth.getBytes("ISO8859-1"));
//			fx = new String(fx.getBytes("ISO8859-1"));

			String sTab = "";
			String bz = CHF.showNull(request.getParameter("bz"));
//			bz = new String(bz.getBytes("ISO8859-1"));
			if (bz.equals("0") || bz.equals("") || bz.equals("本位币")) {
				sTab = " c_Account ";
				bz = "0";
			} else {
				sTab = " c_AccountAll ";
			}

			
			if (fx.equals("0")) {
				sql = "select sum(if(direction2=0,(DebitOcc - CreditOcc),direction2*(DebitOcc - CreditOcc))) from "
						+ sTab
						+ " where SubMonth=? and DataName='"
						+ bz
						+ "' and AccPackageID=? ";
			} else if (fx.equals("1")) {
				sql = "select sum(DebitOcc)  from " + sTab
						+ " where SubMonth=? and DataName='" + bz
						+ "' and AccPackageID=? ";
			} else if (fx.equals("-1")) {
				sql = "select sum(CreditOcc) from " + sTab
						+ " where SubMonth=? and DataName='" + bz
						+ "' and AccPackageID=? ";
			} else {
				return "方向输入错误！";
			}

			String stropt = SRS.getTextKeyAll(subname);
			String [] ss = stropt.split("\\|");
			String str1 = ss.length<=1?subname+"','":ss[1].replaceAll("`","','");
			subname = "'"+str1.substring(0,str1.length()-2);
			//and subjectid in("+str+")
			sql +=" and subjectid in ("+subname+") ";
			
			/*
			if (subtype.equals("") || subtype.equals("1")) {
				if (!isNull("c_accpkgsubject", "SubjectName", subname, "")) {
					subname = AST.TextKey(subname);
				} else {
					subname = "('" + subname + "')";
				}

				sql += "and AccName in " + subname;

			} else if (subtype.equals("2")) {
				if (!isNull("c_accpkgsubject", "SubjectFullName", subname, "")) {
					subname = AST.TextKey(subname);
				} else {
					subname = "('" + subname + "')";
				}

				sql += " and SubjectID in (select SubjectID from c_accpkgsubject where AccPackageID='"
						+ pkgid + "' and SubjectFullName in " + subname + ")";

			} else if (subtype.equals("3")) {
				sql += " and SubjectID = " + subname;
			} else {
				return "科目类型输入错误！";
			}
			*/
			//org.util.Debug.prtOut("getBbBqfse:" + sql);
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, submonth);
			ps.setString(i++, pkgid);
			// ps.setString(i++, subname);
			rs = ps.executeQuery();

			i = 0;
			while (rs.next()) {
				i++;
				if (i > 1) {
					result = "科目余额表年累计发生额出错！";
					break;
				}
				result = CHF.showNull(rs.getString(1));
			}
			
			if("".equals(result)){
				result = "0";//result = "该科目既不是标准科目，也不是用户科目，请修改科目名";
			}
			//org.util.Debug.prtOut("getNcs result:" + result);
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
	}
	
	//----------------------------------------------------------------------------------------
	//合并报表
	//----------------------------------------------------------------------------------------
	
	/**
	 * 得到合并报表的控股或比率　控股为0；比率为1
	 * @return
	 * @throws Exception
	 */
	//getRelation("单位名称","控股或比率")
	private String getRelation() throws Exception{
		String result = "";
		String sql = "";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			
			String dw = CHF.showNull(request.getParameter("dw"));//单位名称
//			dw = new String(dw.getBytes("ISO8859-1"));
			if (dw.equals("")) {
				return "单位名称不能为空!";
			}
			
			String bl = CHF.showNull(request.getParameter("bl"));//控股或比率
//			bl = new String(bl.getBytes("ISO8859-1"));
			if (bl.equals("")) {
				return "控股或比率不能为空!";
			}
			
			sql = "select * from k_customerrelation where systemid = (select systemid from z_project where projectid='"+this.proid+"') and customerid in (select DepartID from k_customer where property=1 and departname='"+dw+"') ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				if("0".equals(bl)){
					result = CHF.showNull(rs.getString("isControl")); // 是否控股 result = CHF.showNull(rs.getString(1));
				}else if("1".equals(bl)){
					result = CHF.showNull(rs.getString("controlRate")); //控股比例
				}else{
					return "项目返回值参数输入错误!";
				}
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
		
	}
	//getRectify(报表级别,报表类型,报表项目,汇总方向) jb,lx,xm,fx
	private String getRectify()throws Exception{
		String result = "";
		String sql = "";		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			
			String taskcode = CHF.showNull(request.getParameter("taskcode"));
			if (request.getParameter("curTaskCode") != null) {
				taskcode = CHF.showNull(request.getParameter("curTaskCode"));
			}

//			taskcode = new String(taskcode.getBytes("ISO8859-1"));
			if (taskcode.equals("")) {
				return "索引号不能为空!";
			}
			
//			String jb = CHF.showNull(request.getParameter("jb"));//报表级别
//			jb = new String(jb.getBytes("ISO8859-1"));
//			if (jb.equals("")) {
//				return "报表级别不能为空!";
//			}
			
			String lx = CHF.showNull(request.getParameter("lx"));//报表类型
//			lx = new String(lx.getBytes("ISO8859-1"));
			if (lx.equals("")) {
				return "报表类型不能为空!";
			}
			
			String xm = CHF.showNull(request.getParameter("xm"));//报表项目
//			xm = new String(xm.getBytes("ISO8859-1"));
			if (xm.equals("")) {
				return "报表项目不能为空!";
			}
			
			String fx = CHF.showNull(request.getParameter("fx"));//汇总方向 1为借；-1为贷
//			fx = new String(fx.getBytes("ISO8859-1"));
			if (fx.equals("")) {
				return "汇总方向不能为空!";
			}
			
			sql = "select * from z_accountrectifyaccount where ProjectId='"+this.proid+"' and ReportLevel=(select level0 from z_task where projectid='"+this.proid+"' and taskcode='"+taskcode+"') and ReportEntry='"+lx+"' and ReportEntry='"+xm+"'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				if("1".equals(fx)){
					result = CHF.showNull(rs.getString("Debitocc"));
				}else if("-1".equals(fx)){
					result = CHF.showNull(rs.getString("Crditocc"));
				}else if("0".equals(fx)){
					result = String.valueOf(Double.parseDouble(rs.getString("Debitocc")) - Double.parseDouble(rs.getString("Crditocc")));
				}else{
					return "项目返回值参数输入错误!";
				}
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}	
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
			String sql = "select subjectname from z_task where projectid="+ projectid + " and taskcode='" + taskcode+ "' and isleaf=1";
			rs = st.executeQuery(sql);
			if (rs.next()) {
				subjectname = rs.getString(1);
			}
		} finally {
			DbUtil.close(rs);
            DbUtil.close(st);
		}
		return subjectname;
	}
	
	public  String getProject() throws Exception{
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String subname = CHF.showNull(request.getParameter("km"));
			if ("".equals(subname)) {
				String curTaskCode = (String)request.getParameter("curTaskCode");
				subname=getTaskSubjectNameByTaskCode(conn,this.proid,curTaskCode);
				if ("".equals(subname)) {
					throw new  Exception("科目不能为空!");
				}
			}
			String res = CHF.showNull(request.getParameter("res"));
			if ("".equals(res)) {
				throw new  Exception("结果返回参数不能为空!");
			}
			
			String fx = CHF.showNull(request.getParameter("fx"));
			if (fx.equals("借") ) {
				fx = "1";
			} else if(fx.equals("贷")){
				fx = "-1";
			}
			/**
			 * 兼容外币
			 */
			String bz = CHF.showNull(request.getParameter("bz"));
			if (bz.equals("0") || bz.equals("") || bz.equals("本位币")) {
				bz = "0";
			} 
			String year = CHF.showNull(request.getParameter("year"));		//年度
			if("".equals(year)){
				year = "0";
			}
			
			String month = CHF.showNull(request.getParameter("month")).trim();		//年度
			if("0".equals(month)){
				month = "";
			}
			/**
			 * 项目区间
			 */
			String AuditTimeBegin = "";
			String AuditTimeEnd = "";
			String sql = "select * from z_project where projectid='"+this.proid+"'";
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
			
			if("未结转损益".equals(subname)){
				return getProject4(subname,res,fx,bz);
			}else{
				if("凭证张数".equals(res)){
					return getProject5(subname,res,fx,bz,year,month,"1",bYear,bMonth,eYear,eMonth);
				}
				
				if(!"".equals(fx) && !"0".equals(fx) &&( 
					"期末未审".equals(res) 
					|| "期末审定".equals(res) 
					|| "项目期末".equals(res) 
					|| "期初审定".equals(res) 
					|| "项目期初".equals(res)
					)
				){
					//走9999逻辑
					return getProject1(subname,res,fx,bz,year,month,bYear,bMonth,eYear,eMonth);
					
				}else{
					if("结转数".equals(res)){
						return getProject6(subname,res,fx,bz,year,month,bYear,bMonth,eYear,eMonth);
					}else{
						//走老科目取数逻辑
						return getProject2(subname,res,fx,bz,year,month,bYear,bMonth,eYear,eMonth);
					}
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		
		
	}
	
	/**
	 * =取项目数("主营业务收入","结转数","贷","本位币","-2")
	 * @param subname
	 * @param res
	 * @param fx
	 * @param bz
	 * @param year
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public String getProject6(String subname,String res,String fx,String bz,String year,String month,
			int bYear,int bMonth,int eYear,int eMonth) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "";	
			String result = "";
			
			/**
			 * 上年调整影响年末 否：上海立信
			 */
			String svalue = "";
			sql = "select svalue from s_config where sname='上年调整影响年末'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				svalue = rs.getString(1);
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			/**
			 *科目对照  
			 */
			String skm = changeSubjectName(subname);
			if(!"".equals(skm)){
				subname = skm;
			}
			
			String sTab = "";
			String sTab1 = "";
			String table1 = "";
			String table2 = "";
			String sql1 = "";
			
			String strBz = "";
			if (bz.equals("0") || bz.equals("") || bz.equals("本位币")) {
				sTab = " c_Account a ";
				sTab1 = " left join z_accountrectify b  on b.projectid='"+this.proid+"' and a.rsubjectid=b.subjectid";
				bz = "0";
				table1 = "\n sum(ifnull(DebitTotalOcc0,0)) DebitTotalOcc0,sum(ifnull(CreditTotalOcc0,0)) CreditTotalOcc0,sum(if(direction2=0,ifnull((DebitTotalOcc0-CreditTotalOcc0),0),ifnull(direction2*(DebitTotalOcc0-CreditTotalOcc0),0))) rectify0," ;
				table2 = ""; 
			} 
			else {
				sTab = " c_AccountAll a ";
				sTab1 = " left join z_accountallrectify b  on b.projectid='"+this.proid+"' and a.rsubjectid=b.subjectid  and a.rDataName=b.DataName ";
				table2 = " and a.accsign=1 ";
				
				strBz = "		and Currency = if('"+bz+"' = '0','','"+bz+"')  \n" ;
			}
			
			
//			String AuditTimeBegin = "";
//			String AuditTimeEnd = "";
//			sql = "select * from z_project where projectid='"+this.proid+"'";
//			ps = conn.prepareStatement(sql);
//			rs = ps.executeQuery();
//			if(rs.next()){
//				AuditTimeBegin = rs.getString("AuditTimeBegin");
//				AuditTimeEnd = rs.getString("AuditTimeEnd");
//			}
//			DbUtil.close(rs);
//			DbUtil.close(ps);
//			
//			int bYear = Integer.parseInt(AuditTimeBegin.substring(0, 4));
//			int bMonth = Integer.parseInt(AuditTimeBegin.substring(5, 7));
//			int eYear = Integer.parseInt(AuditTimeEnd.substring(0, 4));
//			int eMonth = Integer.parseInt(AuditTimeEnd.substring(5, 7));
//			
			int StartYearMonth = bYear * 12 + bMonth;
			int EndYearMonth = 	eYear * 12 + eMonth;
			
			/**
			 * 年度为0，不用改变区间
			 * 年度为-1,就是取上年
			 */
			if(!"0".equals(year)){
				//System.out.println(AuditTimeBegin + "|" + AuditTimeEnd + "|" + StartYearMonth + "|" + EndYearMonth);
				
				int iYearMonthArea = EndYearMonth - StartYearMonth + 1;
				
				if(iYearMonthArea >= 12 ){
					StartYearMonth = StartYearMonth + iYearMonthArea * Integer.parseInt(year);
					EndYearMonth = EndYearMonth + iYearMonthArea * Integer.parseInt(year);
				}else{
					StartYearMonth = (bYear + Integer.parseInt(year))*12+ bMonth;
					EndYearMonth = StartYearMonth + 11;
				}
				
//				//System.out.println(AuditTimeBegin + "|" + AuditTimeEnd + "|" + StartYearMonth + "|" + EndYearMonth);
				
				eYear += Integer.parseInt(year);				
				if (bz.equals("0") || bz.equals("") || bz.equals("本位币")) {
					sTab = " c_Account a ";
					sTab1 = " left join z_accountyearrectify b  on b.yearrectify = '"+eYear+"' and b.projectid='"+this.proid+"' and a.rsubjectid=b.subjectid";
					bz = "0";
					table1 = "\n sum(ifnull(DebitTotalOcc0,0)) DebitTotalOcc0,sum(ifnull(CreditTotalOcc0,0)) CreditTotalOcc0,sum(if(direction2=0,ifnull((DebitTotalOcc0-CreditTotalOcc0),0),ifnull(direction2*(DebitTotalOcc0-CreditTotalOcc0),0))) rectify0," ;
					table2 = ""; 
				} 
				else {
					sTab = " c_AccountAll a ";
					sTab1 = " left join z_accountallyearrectify b  on b.yearrectify = '"+eYear+"' and b.projectid='"+this.proid+"' and a.rsubjectid=b.subjectid  and a.rDataName=b.DataName ";
					table2 = " and a.accsign=1 ";
				}
				
			}
			
			//单月
			if(!"".equals(month)){
				
				int EndYearMonth1 = eYear * 12 + Integer.parseInt(month);
				
				if(EndYearMonth1>=StartYearMonth && EndYearMonth1 <= EndYearMonth){
					StartYearMonth = EndYearMonth1;
					EndYearMonth = EndYearMonth1;
				}else{
					return "0";
				}
			}
			
			
			//System.out.println(AuditTimeBegin + "|" + AuditTimeEnd + "|" + StartYearMonth + "|" + EndYearMonth);
			
//			String stropt = SRS.getTextKeyAll(subname, proid);
//			String [] ss = stropt.split("\\|");
//			String str = ss.length<=1?subname+"','":ss[1].replaceAll("`","','");
//			subname = "'"+str.substring(0,str.length()-2);			
//			sql1 +=" and a.subjectid in ("+subname+") ";
			
//			找出参数科目在客户中的所有科目编号（包括底层和非底层）
			String myds1="";
			int mylevel1=1;
			sql = " select  group_concat(distinct subjectid SEPARATOR '\\',\\'') as subjectid,min(level1) \n"
					+ " from ( select distinct subjectid,level1 from  c_account a \n"
					+ " where 1=1 \n"
					+"	and SubYearMonth *12 +SubMonth >= "+StartYearMonth+" \n" 
					+"	and SubYearMonth *12 +SubMonth <= "+EndYearMonth+" \n" 
					+ " and (subjectfullname2 = '" + subname + "' or subjectfullname2 like '" + subname + "/%') \n" 
					+ " ) a";
			//System.out.println(sql);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				myds1 = rs.getString(1);
				mylevel1=rs.getInt(2);
				
				myds1 = "'"+myds1+"'";
			} else {
				myds1 = "''";
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
//			sql = "select a.*,b.subjectfullname2 from ( \n" + 
//			"	select (ifnull( occ1,0) - ifnull(occ2,0))  as CarryOver   , \n" + 
//			"	ifnull(occ11,0)-ifnull(occ21,0) occ1,ifnull(occ12,0)-ifnull(occ22,0) occ2, \n" + 
//			"	a.tokenid,a.dataname   \n" +
//			"	from (      \n" +
//			"	select A.tokenid,a.direction2, dataname, \n" + 
//			"			sum(a.debitocc-a.creditocc) as occ1,sum(a.debitocc) occ11,sum(a.creditocc) as occ12 \n" +    
//			"			from "+sTab+"   \n" +
//			"			where a.SubYearMonth * 12  + a.SubMonth >=  '"+StartYearMonth+"' \n" +
//			"			and a.SubYearMonth * 12  + a.SubMonth <= '"+EndYearMonth+"' \n" +
//			"			and isleaf1=1  \n" +
//			"			and a.dataname='"+bz+"'   \n" +
//			"			group by a.tokenid      \n" +
//			"		) a inner join (      \n" +
//			"			select a.tokenid,   \n" +
//			"			if(Currency='','0',Currency) as dataname,   \n" +
//			"			sum(a.occurvalue* a.dirction) as occ2 , \n" + 
//			"			sum(if(a.property like '%2%' and Dirction = 1,OccurValue,0)) as occ21, \n" + 
//			"			sum(if(a.property like '%2%' and Dirction = -1,OccurValue,0)) as occ22   \n" +
//			"			from (      \n" +
//			"				select  distinct a.* \n" + 
//			"				from c_subjectentry a    \n" +
//			"				where substring(a.VchDate,1,4) * 12 + substring(a.VchDate,6,2) >=  '"+StartYearMonth+"' \n" +  
//			"				and substring(a.VchDate,1,4) * 12 + substring(a.VchDate,6,2) <=  '"+EndYearMonth+"'       \n" +
//			"				and a.property like '%2%'     \n" +
//			"				and Currency = if('"+bz+"' = '0','','"+bz+"')  \n" +
//			
//			"				and a.subjectid in ( \n" +
//			"					select b.subjectid \n" +
//			"					from c_Account a,c_Account b  \n" +
//			"					where 1=1 \n" +
//			"					and a.SubYearMonth * 12 + a.SubMonth = '24107'  \n" +
//			"					and b.SubYearMonth * 12 + b.SubMonth = '24107'  \n" +
//			sql1  +
//			"					and (b.subjectfullname1 = a.subjectfullname1 or b.subjectfullname1 like concat(a.subjectfullname1,'/%')) \n" +
//			"				) " +
//			"			) a      \n" +
//			"			group by a.tokenid,Currency \n" + 
//			"		) b on a.tokenid = b.tokenid and a.dataname=b.dataname \n" + 
//			
//			"			) a ,c_account b  \n" +
//			"		where 1=1  \n" +
//			"		and b.SubYearMonth * 12 + b.SubMonth = '"+EndYearMonth+"' \n" +
//			"		and a.tokenid = b.tokenid   \n" ;
			
			sql = " select (if(b.occ1 is null,0,b.occ1)-if(c.occ2 is null,0,c.occ2))* (direction2) as CarryOver , \n" +
			"	ifnull(occ11,0)-ifnull(occ21,0) occ1,ifnull(occ12,0)-ifnull(occ22,0) occ2 \n" +
			" 	from (  \n" +
			" 	select direction2,sum(DebitOcc)-sum(CreditOcc) as occ1,sum(DebitOcc) as occ11,sum(CreditOcc) as occ12 \n" + 
			" 	from "+sTab+"  \n" +
			" 	where 1=1  \n" +
			" 	and SubYearMonth *12 +SubMonth >= '"+StartYearMonth+"' \n" + 
			" 	and SubYearMonth *12 +SubMonth <= '"+EndYearMonth+"'  \n" +
			" 	and level1="+mylevel1+"  \n" +
			"	and a.dataname='"+bz+"'   \n" +
			"	and a.subjectid in ( \n" +
			myds1 + 
			"	) group by direction2 " +
			" )b, \n" + 
			" (  \n" +
			" 		select sum(a.occurvalue* a.dirction) as occ2, \n" + 
			"		sum(if(a.property like '%2%' and Dirction = 1,OccurValue,0)) as occ21, \n" + 
			"		sum(if(a.property like '%2%' and Dirction = -1,OccurValue,0)) as occ22   \n" +
			" 		from c_subjectentry a  \n" +
			" 		where 1=1 " +
			"		and property like '%2%' \n" + 
			strBz +
			"		and a.subjectid in ( \n" +
			myds1 +
			"		) " +
			"       and substring(a.VchDate,1,4) * 12 + substring(a.VchDate,6,2) >= '"+StartYearMonth+"' \n" + 
			"       and substring(a.VchDate,1,4) * 12 + substring(a.VchDate,6,2) <= '"+EndYearMonth+"'  \n" +
			" )c";
			//System.out.println(sql);
			
			sql = "select sum(CarryOver) as CarryOver,sum(occ1) as occ1,sum(occ2) as occ2 " +
				" from (" +
				sql + 
				") a ";
			
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			int i = 0;
			while (rs.next()) {
				i++;
				if (i > 1) {
					throw new  Exception("取项目数出错！");					
				}				
				if("结转数".equals(res)){
					if("1".equals(fx)){
						result = CHF.showNull(rs.getString("occ1"));
					}else if("-1".equals(fx)){
						result = CHF.showNull(rs.getString("occ2"));
					}else{
						result = CHF.showNull(rs.getString("CarryOver"));
					}
				}
				
			}
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
	}
	
	/**
	 * 借贷凭证数 opt 是用来区分取项目还是用户
	 * @param subname
	 * @param res
	 * @param fx
	 * @param bz
	 * @param year
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public String getProject5(String subname,String res,String fx,String bz,String year,String month,String opt,
			int bYear,int bMonth,int eYear,int eMonth) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "";	
			String result = "";
			
			String Currency = "";
			if (!(bz.equals("0") || bz.equals("") || bz.equals("本位币"))) {
				Currency = " and Currency = '"+bz+"' ";
			}
			
//			String AuditTimeBegin = "";
//			String AuditTimeEnd = "";
//			sql = "select * from z_project where projectid='"+this.proid+"'";
//			ps = conn.prepareStatement(sql);
//			rs = ps.executeQuery();
//			if(rs.next()){
//				AuditTimeBegin = rs.getString("AuditTimeBegin");
//				AuditTimeEnd = rs.getString("AuditTimeEnd");
//			}
//			DbUtil.close(rs);
//			DbUtil.close(ps);
//			
//			int bYear = Integer.parseInt(AuditTimeBegin.substring(0, 4));
//			int bMonth = Integer.parseInt(AuditTimeBegin.substring(5, 7));
//			int eYear = Integer.parseInt(AuditTimeEnd.substring(0, 4));
//			int eMonth = Integer.parseInt(AuditTimeEnd.substring(5, 7));
			
			int StartYearMonth = bYear * 12 + bMonth;
			int EndYearMonth = 	eYear * 12 + eMonth;
			
			/**
			 * 年度为0，不用改变区间
			 * 年度为-1,就是取上年
			 */
			if(!"0".equals(year)){
				//System.out.println(AuditTimeBegin + "|" + AuditTimeEnd + "|" + StartYearMonth + "|" + EndYearMonth);
				
				int iYearMonthArea = EndYearMonth - StartYearMonth + 1;
				
				if(iYearMonthArea >= 12 ){
					StartYearMonth = StartYearMonth + iYearMonthArea * Integer.parseInt(year);
					EndYearMonth = EndYearMonth + iYearMonthArea * Integer.parseInt(year);
				}else{
					StartYearMonth = (bYear + Integer.parseInt(year))*12+ bMonth;
					EndYearMonth = StartYearMonth + 11;
				}
				
				//System.out.println(AuditTimeBegin + "|" + AuditTimeEnd + "|" + StartYearMonth + "|" + EndYearMonth);
				
				eYear += Integer.parseInt(year);				
				
			}
			
//			单月
			if(!"".equals(month)){
				
				int EndYearMonth1 = eYear * 12 + Integer.parseInt(month);
				
				if(EndYearMonth1>=StartYearMonth && EndYearMonth1 <= EndYearMonth){
					StartYearMonth = EndYearMonth1;
					EndYearMonth = EndYearMonth1;
				}else{
					return "0";
				}
			}
			
			sql = "select sum(VoucherCount) as VoucherCount, " +
			" sum(DebitVoucherCount) as DebitVoucherCount, " +
			" sum(CreditVoucherCount) as CreditVoucherCount " +
			" from( " +
			" select a.AccPackageID, a.subjectid, \n" +
			" count(distinct voucherid) VoucherCount, \n" +
			" count(distinct if(Dirction=1,voucherid,0) ) -if(sum(distinct if(Dirction=-1,voucherid,0))=0,0,1) as DebitVoucherCount, \n" +
			" count(distinct if(Dirction=-1,voucherid,0) ) -if(sum(distinct if(Dirction=1,voucherid,0))=0,0,1) as CreditVoucherCount  \n" +
			" from c_subjectentry a,( \n" +
			" 	select distinct  AccPackageID, subjectid \n" +
			" 	from c_account a \n" +
			" 	where 1=1 \n" +
			" 	and SubYearMonth*12+SubMonth>= "+StartYearMonth+" \n" +
			" 	and SubYearMonth*12+SubMonth<= "+EndYearMonth+"  \n" +
			" 	and (a.subjectfullname2 = '"+subname+"' or a.subjectfullname2 like '"+subname+"/%') \n" +
			" ) b \n" +
			" where 1=1 \n" +  
			" and a.subjectid = b.subjectid \n" +
			" and a.AccPackageID = b.AccPackageID \n" + Currency + 
			" and substring(VchDate,1,4) * 12 + substring(VchDate,6,2) >= "+StartYearMonth+" \n" +
			" and substring(VchDate,1,4) * 12 + substring(VchDate,6,2) <= "+EndYearMonth+"  \n" +
			" group by a.AccPackageID,a.subjectid  " +
			") a ";
			
			//System.out.println(sql);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			int i = 0;
			while (rs.next()) {
				i++;
				if (i > 1) {
					throw new  Exception("取项目数出错！");					
				}				
				if("凭证张数".equals(res)){
					if("1".equals(fx)){
						result = CHF.showNull(rs.getString("DebitVoucherCount"));
					}else if("-1".equals(fx)){
						result = CHF.showNull(rs.getString("CreditVoucherCount"));
					}else{
						result = CHF.showNull(rs.getString("VoucherCount"));
					}
				}
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	
	
	/**
	 * 未结转损益
	 * @param subname
	 * @param res
	 * @param fx
	 * @param bz
	 * @return
	 * @throws Exception
	 * 取项目数("未结转损益","期末已审")  :本期未结转的数据+本期调整损益类科目的合计数  ：     本年损益类科目的余额合计值 ＋  本年损益类科目的净调整数
	 * 取项目数("未结转损益","期末未审") :本期未结转的数据            ：  本年损益类科目的余额合计值  
	 * 取项目数("未结转损益","本期调整") :本期调整损益类科目的合计数  ：    本年损益类科目的净调整数
	 * 
	 * 取项目数("未结转损益","期初审定") :上期未结转的数据+以前年度调整损益类科目的合计数             上年损益类科目的余额合计值 ＋ 所有5年的损益类科目的净调整数
	 * 取项目数("未结转损益","以前年度调整") :以前年度调整损益类科目的合计数   ：          所有5年的损益类科目的净调整数
	 * 
	 * 取项目数("未结转损益","上期调整") :上期调整损益类科目的合计数   ：   上1年的损益类科目的净调整数
	 */
	public String getProject4(String subname,String res,String fx,String bz) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "";	
			String result = "";
			String strSql = "";
			
			String vocationid = new CustomerService(conn).getCustomer(this.pkgid.substring(0,6)).getVocationId();
			
			/**
			 * 上年调整影响年末 否：上海立信
			 */
			String svalue = "";
			sql = "select svalue from s_config where sname='上年调整影响年末'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				svalue = rs.getString(1);
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			sql = "select exists( \n"
				+"	select 1  \n"
				+"		from asdb.k_standsubject \n" 
				+"		where vocationid="+vocationid+" \n" 
				+"		and subjectid like '4%'  \n"
				+"		and (subjectname like '%本年利润%' or subjectname like '%利润分配%') \n"
				+"	) and exists( \n"
				+"		select 1  \n"
				+"		from asdb.k_standsubject \n" 
				+"		where vocationid="+vocationid+" \n" 
				+"		and subjectid like '6%'  \n"
				+"		and (subjectname like '%支出%' or subjectname like '%收入%'  or subjectname like '%费用%') \n"
				+"	) as myresult";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			strSql="5";
			if(rs.next()) {
				if (rs.getBoolean(1)){
					strSql="6";
				}
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			/**
			 * 通过标准科目的损益类型，求所有的用户科目的损益科目编号
			 */
			sql = "	select group_concat(\"'\",subjectid,\"'\") as subjectid  \n" +
			"	from ( \n" +
			"		select b.subjectid \n" +
			"		from k_standsubject a,c_account b \n" + 
			"		where a.subjectid like '"+strSql+"%'   \n" +
			"		and a.vocationid = "+vocationid+" \n" +
			"		and b.AccPackageID ="+this.pkgid+" \n" +
			"		and b.submonth = 1 \n" +
			"		and (b.subjectfullname2 = a.subjectfullname or b.subjectfullname2 like concat(a.subjectfullname,'/%')) \n" +
			"		union  \n" +
			"		select b.subjectid from k_standsubject a,z_usesubject b \n" + 
			"		where a.subjectid like '"+strSql+"%'   \n" +
			"		and a.vocationid = "+vocationid+" \n" +
			"		and b.projectID ="+this.proid+" \n" +
			"		and (b.subjectfullname = a.subjectfullname or b.subjectfullname like concat(a.subjectfullname,'/%')) \n" +
			"	) a ";
			String sqlSubjects = "";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				sqlSubjects = rs.getString("subjectid");
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			if(!"".equals(sqlSubjects)){
				sqlSubjects = "\n	and a.subjectid in ("+sqlSubjects+") ";
			}
			
			
			
			String AuditTimeBegin = "";
			String AuditTimeEnd = "";
			sql = "select * from z_project where projectid='"+this.proid+"'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				AuditTimeBegin = rs.getString("AuditTimeBegin");
				AuditTimeEnd = rs.getString("AuditTimeEnd");
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			String sTab = "",strTab1="";
			if (bz.equals("0") || bz.equals("") || bz.equals("本位币")) {
				sTab = " c_Account a " +
				" left  join z_accountrectify b on b.projectid='"+this.proid+"' and a.subjectid=b.subjectid " +
				" left  join z_accountyearrectify c on c.projectid='"+this.proid+"' and c.yearrectify = (substring('"+AuditTimeEnd+"',1,4) -1) and a.subjectid=c.subjectid ";
				
				strTab1 = " z_usesubject a " +
				" left  join z_accountrectify b on b.projectid='"+this.proid+"' and a.subjectid=b.subjectid " +
				" left  join z_accountyearrectify c on c.projectid='"+this.proid+"' and c.yearrectify = (substring('"+AuditTimeEnd+"',1,4) -1) and a.subjectid=c.subjectid ";
				
			}else{
				sTab = " c_AccountAll a " +
				" left  join z_accountallrectify b on b.projectid='"+this.proid+"' and a.subjectid=b.subjectid and a.DataName=b.DataName" +
				" left  join z_accountallyearrectify c on c.projectid='"+this.proid+"' and c.yearrectify = (substring('"+AuditTimeEnd+"',1,4) -1) and a.subjectid=c.subjectid and a.DataName=c.DataName";
				
				strTab1 = " z_usesubject a " +
				" left  join z_accountallrectify b on b.projectid='"+this.proid+"' and a.subjectid=b.subjectid and b.DataName = '"+bz+"'" +
				" left  join z_accountallyearrectify c on c.projectid='"+this.proid+"' and c.yearrectify = (substring('"+AuditTimeEnd+"',1,4) -1) and a.subjectid=c.subjectid and c.DataName = '"+bz+"'";
				
			}
			
			if("否".equals(svalue)){
				sql =
					
					"\n select  " +
					"\n sum(DebitRemain) as DebitRemain, " +
					"\n sum(CreditRemain) as CreditRemain, " +
					"\n sum(initbalance) as initbalance, " +
					"\n sum(DebitBalance) as DebitBalance, " +
					"\n sum(CreditBalance) as CreditBalance, " +
					"\n sum(Balance) as Balance, " +
					"\n sum(qmrectify) as qmrectify, " +
					"\n sum(qmrectify1) as qmrectify1, " +
					"\n sum(qmrectify2) as qmrectify2, " +
					"\n sum(qcrectify) as qcrectify, " +
					"\n sum(qcrectify1) as qcrectify1, " +
					"\n sum(qcrectify2) as qcrectify2, " +
					"\n sum(qrectify) as qrectify, " +
					"\n sum(qrectify1) as qrectify1, " +
					"\n sum(qrectify2) as qrectify2 " +
					"\n from (  " +
					
					"\n select " +
					"\n	sum(if(concat(SubYearMonth,'-',LPAD(SubMonth,2,'0'))=substring('"+AuditTimeBegin+"',1,7),DebitRemain,0)) DebitRemain," +
					"\n	sum(if(concat(SubYearMonth,'-',LPAD(SubMonth,2,'0'))=substring('"+AuditTimeBegin+"',1,7),(-1)*CreditRemain,0)) CreditRemain," +
					"\n	(-1) * sum(if(concat(SubYearMonth,'-',LPAD(SubMonth,2,'0'))=substring('"+AuditTimeBegin+"',1,7),(DebitRemain+CreditRemain),0)) initbalance," +
					"\n	sum(if(concat(SubYearMonth,'-',LPAD(SubMonth,2,'0'))=substring('"+AuditTimeEnd+"',1,7),DebitBalance,0)) DebitBalance," +
					"\n	sum(if(concat(SubYearMonth,'-',LPAD(SubMonth,2,'0'))=substring('"+AuditTimeEnd+"',1,7),(-1)*CreditBalance,0)) CreditBalance," +
					"\n	(-1) * sum(if(concat(SubYearMonth,'-',LPAD(SubMonth,2,'0'))=substring('"+AuditTimeEnd+"',1,7),Balance,0)) Balance," +
					//期末调整
					"\n sum(if(concat(SubYearMonth,'-',LPAD(SubMonth,2,'0'))=substring('"+AuditTimeBegin+"',1,7),(-1)*(ifnull(b.DebitTotalOcc1,0) - ifnull(b.CreditTotalOcc1,0)) + (-1)*(ifnull(b.DebitTotalOcc2,0) - ifnull(b.CreditTotalOcc2,0)) ,0)) as qmrectify," +
					"\n sum(if(concat(SubYearMonth,'-',LPAD(SubMonth,2,'0'))=substring('"+AuditTimeBegin+"',1,7),ifnull(b.DebitTotalOcc1,0) + ifnull(b.DebitTotalOcc2,0) ,0)) as qmrectify1," +
					"\n sum(if(concat(SubYearMonth,'-',LPAD(SubMonth,2,'0'))=substring('"+AuditTimeBegin+"',1,7),ifnull(b.CreditTotalOcc1,0) + ifnull(b.CreditTotalOcc2,0) ,0)) as qmrectify2," +
					//期初调整(以前年度调整)
					"\n (-1) * sum(if(concat(SubYearMonth,'-',LPAD(SubMonth,2,'0'))=substring('"+AuditTimeBegin+"',1,7),ifnull(c.DebitTotalOcc1,0) - ifnull(c.CreditTotalOcc1,0) + ifnull(c.DebitTotalOcc2,0) - ifnull(c.CreditTotalOcc2,0) ,0)) as qcrectify," +
					"\n sum(if(concat(SubYearMonth,'-',LPAD(SubMonth,2,'0'))=substring('"+AuditTimeBegin+"',1,7),ifnull(c.DebitTotalOcc1,0) + ifnull(c.DebitTotalOcc2,0) ,0)) as qcrectify1," +
					"\n sum(if(concat(SubYearMonth,'-',LPAD(SubMonth,2,'0'))=substring('"+AuditTimeBegin+"',1,7),ifnull(c.CreditTotalOcc1,0) + ifnull(c.CreditTotalOcc2,0),0) ) as qcrectify2, " +

					//上期调整
					"\n (-1) * sum(if(concat(SubYearMonth,'-',LPAD(SubMonth,2,'0'))=substring('"+AuditTimeBegin+"',1,7),ifnull(c.DebitTotalOcc1,0) - ifnull(c.CreditTotalOcc1,0) + ifnull(c.DebitTotalOcc2,0) - ifnull(c.CreditTotalOcc2,0) ,0)) as qrectify," +
					"\n sum(if(concat(SubYearMonth,'-',LPAD(SubMonth,2,'0'))=substring('"+AuditTimeBegin+"',1,7),ifnull(c.DebitTotalOcc1,0) + ifnull(c.DebitTotalOcc2,0) ,0)) as qrectify1," +
					"\n sum(if(concat(SubYearMonth,'-',LPAD(SubMonth,2,'0'))=substring('"+AuditTimeBegin+"',1,7),ifnull(c.CreditTotalOcc1,0) + ifnull(c.CreditTotalOcc2,0),0) ) as qrectify2 " +
					
					"\n	from "+sTab+" " +
					"\n	where substring(a.AccPackageID,1,6) = '"+this.pkgid.substring(0,6)+"'" +
					"\n	and concat(SubYearMonth,'-',LPAD(SubMonth,2,'0')) >= substring('"+AuditTimeBegin+"',1,7) " +
					"\n	and concat(SubYearMonth,'-',LPAD(SubMonth,2,'0')) <= substring('"+AuditTimeEnd+"',1,7)" +
					"\n	and a.DataName='"+bz+"' " +
//					"\n	and a.subjectid like '"+strSql+"%' " +
					sqlSubjects + 
					"\n	and a.level1 = 1 " +
				
				
					"\n	union " +


					"\n	select  " +
					"\n		0 as DebitRemain, " +
					"\n		0 as  CreditRemain, " +
					"\n		0 as  initbalance, " +
					"\n		0 as  DebitBalance, " +
					"\n		0 as  CreditBalance, " +
					"\n		0 as  Balance, " +
					"\n	(-1)*(ifnull(b.DebitTotalOcc1,0) - ifnull(b.CreditTotalOcc1,0)) + (-1)*(ifnull(b.DebitTotalOcc2,0) - ifnull(b.CreditTotalOcc2,0))  as qmrectify, " +
					"\n	ifnull(b.DebitTotalOcc1,0) + ifnull(b.DebitTotalOcc2,0)  as qmrectify1, " +
					"\n	ifnull(b.CreditTotalOcc1,0) + ifnull(b.CreditTotalOcc2,0) as qmrectify2, " +
					"\n	(-1) * ifnull(c.DebitTotalOcc1,0) - ifnull(c.CreditTotalOcc1,0) + ifnull(c.DebitTotalOcc2,0) - ifnull(c.CreditTotalOcc2,0) as qcrectify, " +
					"\n	ifnull(c.DebitTotalOcc1,0) + ifnull(c.DebitTotalOcc2,0)  as qcrectify1, " +
					"\n	ifnull(c.CreditTotalOcc1,0) + ifnull(c.CreditTotalOcc2,0) as qcrectify2,  " +
					"\n	(-1) * ifnull(c.DebitTotalOcc1,0) - ifnull(c.CreditTotalOcc1,0) + ifnull(c.DebitTotalOcc2,0) - ifnull(c.CreditTotalOcc2,0)  as qrectify, " +
					"\n	ifnull(c.DebitTotalOcc1,0) + ifnull(c.DebitTotalOcc2,0)  as qrectify1, " +
					"\n	ifnull(c.CreditTotalOcc1,0) + ifnull(c.CreditTotalOcc2,0) as qrectify2  " +
				 "\n	from "+strTab1+" " +  
				 "\n	where a.projectid="+this.proid+" " +
//				 "\n	and a.subjectid like '"+strSql+"%'  " +
				 sqlSubjects + 
				 "\n	and a.level0 = 1  " +
				 "\n	and a.subjectname <>'年初未分配利润' " +
				 "\n ) a";
				
				
			}else{
				sql =
					
					"\n select  " +
					"\n sum(DebitRemain) as DebitRemain, " +
					"\n sum(CreditRemain) as CreditRemain, " +
					"\n sum(initbalance) as initbalance, " +
					"\n sum(DebitBalance) as DebitBalance, " +
					"\n sum(CreditBalance) as CreditBalance, " +
					"\n sum(Balance) as Balance, " +
					"\n sum(qmrectify) as qmrectify, " +
					"\n sum(qmrectify1) as qmrectify1, " +
					"\n sum(qmrectify2) as qmrectify2, " +
					"\n sum(qcrectify) as qcrectify, " +
					"\n sum(qcrectify1) as qcrectify1, " +
					"\n sum(qcrectify2) as qcrectify2, " +
					"\n sum(qrectify) as qrectify, " +
					"\n sum(qrectify1) as qrectify1, " +
					"\n sum(qrectify2) as qrectify2 " +
					"\n from (  " +
					
					
					"\n select " +
					"\n	sum(if(concat(SubYearMonth,'-',LPAD(SubMonth,2,'0'))=substring('"+AuditTimeBegin+"',1,7),DebitRemain,0)) DebitRemain," +
					"\n	sum(if(concat(SubYearMonth,'-',LPAD(SubMonth,2,'0'))=substring('"+AuditTimeBegin+"',1,7),(-1)*CreditRemain,0)) CreditRemain," +
					"\n	(-1) * sum(if(concat(SubYearMonth,'-',LPAD(SubMonth,2,'0'))=substring('"+AuditTimeBegin+"',1,7),(DebitRemain+CreditRemain),0)) initbalance," +
					"\n	sum(if(concat(SubYearMonth,'-',LPAD(SubMonth,2,'0'))=substring('"+AuditTimeEnd+"',1,7),DebitBalance,0)) DebitBalance," +
					"\n	sum(if(concat(SubYearMonth,'-',LPAD(SubMonth,2,'0'))=substring('"+AuditTimeEnd+"',1,7),(-1)*CreditBalance,0)) CreditBalance," +
					"\n	(-1) * sum(if(concat(SubYearMonth,'-',LPAD(SubMonth,2,'0'))=substring('"+AuditTimeEnd+"',1,7),Balance,0)) Balance," +
					//期末调整
					"\n (-1) * sum(if(concat(SubYearMonth,'-',LPAD(SubMonth,2,'0'))=substring('"+AuditTimeBegin+"',1,7),ifnull(b.DebitTotalOcc1,0) - ifnull(b.CreditTotalOcc1,0) + ifnull(b.DebitTotalOcc2,0) - ifnull(b.CreditTotalOcc2,0) ,0)) as qmrectify," +
					"\n sum(if(concat(SubYearMonth,'-',LPAD(SubMonth,2,'0'))=substring('"+AuditTimeBegin+"',1,7),ifnull(b.DebitTotalOcc1,0) + ifnull(b.DebitTotalOcc2,0) ,0)) as qmrectify1," +
					"\n sum(if(concat(SubYearMonth,'-',LPAD(SubMonth,2,'0'))=substring('"+AuditTimeBegin+"',1,7),ifnull(b.CreditTotalOcc1,0) + ifnull(b.CreditTotalOcc2,0) ,0)) as qmrectify2," +
					//期初调整(以前年度调整)
					"\n (-1) * sum(if(concat(SubYearMonth,'-',LPAD(SubMonth,2,'0'))=substring('"+AuditTimeBegin+"',1,7),ifnull(b.DebitTotalOcc4,0) - ifnull(b.CreditTotalOcc4,0) + ifnull(b.DebitTotalOcc5,0) - ifnull(b.CreditTotalOcc5,0) + ifnull(b.DebitTotalOcc6,0) - ifnull(b.CreditTotalOcc6,0) ,0)) as qcrectify," +
					"\n sum(if(concat(SubYearMonth,'-',LPAD(SubMonth,2,'0'))=substring('"+AuditTimeBegin+"',1,7),ifnull(b.DebitTotalOcc4,0) + ifnull(b.DebitTotalOcc5,0) + ifnull(b.DebitTotalOcc6,0) ,0)) as qcrectify1," +
					"\n sum(if(concat(SubYearMonth,'-',LPAD(SubMonth,2,'0'))=substring('"+AuditTimeBegin+"',1,7),ifnull(b.CreditTotalOcc4,0) + ifnull(b.CreditTotalOcc5,0) + ifnull(b.CreditTotalOcc6,0) ,0)) as qcrectify2," +
					
					//上期调整
					"\n (-1) * sum(if(concat(SubYearMonth,'-',LPAD(SubMonth,2,'0'))=substring('"+AuditTimeBegin+"',1,7),ifnull(c.DebitTotalOcc1,0) - ifnull(c.CreditTotalOcc1,0) + ifnull(c.DebitTotalOcc2,0) - ifnull(c.CreditTotalOcc2,0) ,0)) as qrectify," +
					"\n sum(if(concat(SubYearMonth,'-',LPAD(SubMonth,2,'0'))=substring('"+AuditTimeBegin+"',1,7),ifnull(c.DebitTotalOcc1,0) + ifnull(c.DebitTotalOcc2,0) ,0)) as qrectify1," +
					"\n sum(if(concat(SubYearMonth,'-',LPAD(SubMonth,2,'0'))=substring('"+AuditTimeBegin+"',1,7),ifnull(c.CreditTotalOcc1,0) + ifnull(c.CreditTotalOcc2,0),0) ) as qrectify2 " +
					
					"\n	from "+sTab+" " +
					"\n	where substring(a.AccPackageID,1,6) = '"+this.pkgid.substring(0,6)+"'" +
					"\n	and concat(SubYearMonth,'-',LPAD(SubMonth,2,'0')) >= substring('"+AuditTimeBegin+"',1,7) " +
					"\n	and concat(SubYearMonth,'-',LPAD(SubMonth,2,'0')) <= substring('"+AuditTimeEnd+"',1,7)" +
					"\n	and a.DataName='"+bz+"' " +
//					"\n	and a.subjectid like '"+strSql+"%' " +
					sqlSubjects + 
					"\n	and a.level1 = 1 "  +
					
					
					"\n	union " +


					"\n	select  " +
					"\n		0 as DebitRemain, " +
					"\n		0 as  CreditRemain, " +
					"\n		0 as  initbalance, " +
					"\n		0 as  DebitBalance, " +
					"\n		0 as  CreditBalance, " +
					"\n		0 as  Balance, " +
					"\n	(-1)*(ifnull(b.DebitTotalOcc1,0) - ifnull(b.CreditTotalOcc1,0)) + (-1)*(ifnull(b.DebitTotalOcc2,0) - ifnull(b.CreditTotalOcc2,0))  as qmrectify, " +
					"\n	ifnull(b.DebitTotalOcc1,0) + ifnull(b.DebitTotalOcc2,0)  as qmrectify1, " +
					"\n	ifnull(b.CreditTotalOcc1,0) + ifnull(b.CreditTotalOcc2,0) as qmrectify2, " +
					"\n	(-1) * ifnull(c.DebitTotalOcc1,0) - ifnull(c.CreditTotalOcc1,0) + ifnull(c.DebitTotalOcc2,0) - ifnull(c.CreditTotalOcc2,0) as qcrectify, " +
					"\n	ifnull(c.DebitTotalOcc1,0) + ifnull(c.DebitTotalOcc2,0)  as qcrectify1, " +
					"\n	ifnull(c.CreditTotalOcc1,0) + ifnull(c.CreditTotalOcc2,0) as qcrectify2,  " +
					"\n	(-1) * ifnull(c.DebitTotalOcc1,0) - ifnull(c.CreditTotalOcc1,0) + ifnull(c.DebitTotalOcc2,0) - ifnull(c.CreditTotalOcc2,0)  as qrectify, " +
					"\n	ifnull(c.DebitTotalOcc1,0) + ifnull(c.DebitTotalOcc2,0)  as qrectify1, " +
					"\n	ifnull(c.CreditTotalOcc1,0) + ifnull(c.CreditTotalOcc2,0) as qrectify2  " +
				 "\n	from "+strTab1+" " +  
				 "\n	where a.projectid="+this.proid+" " +
//				 "\n	and a.subjectid like '"+strSql+"%'  " +
				 sqlSubjects + 
				 "\n	and a.level0 = 1  " +
				 "\n	and a.subjectname <>'年初未分配利润' " +
				 "\n ) a";
					
					
					
			}
			
			System.out.println(sql);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			int i = 0;
			
			NumberFormat formatter = new DecimalFormat("######0.00");
			
			while (rs.next()) {
				i++;
				if (i > 1) {
					throw new  Exception("取项目数出错！");					
				}
				if("期末未审".equals(res)){
					if("1".equals(fx)){
						result = CHF.showNull(rs.getString("DebitBalance"));
					}else if("-1".equals(fx)){
						result = CHF.showNull(rs.getString("CreditBalance"));
					}else{
						result = CHF.showNull(rs.getString("Balance"));
					}
				} else if("期末已审".equals(res)){
					if("1".equals(fx)){
						result = formatter.format(rs.getDouble("DebitBalance") + rs.getDouble("qmrectify1") );
					}else if("-1".equals(fx)){
						result = formatter.format(rs.getDouble("CreditBalance") + rs.getDouble("qmrectify2") );
					}else{
						result = formatter.format(rs.getDouble("Balance") + rs.getDouble("qmrectify") );
					}
				} else if("本期调整".equals(res)){
					if("1".equals(fx)){
						result = CHF.showNull(rs.getString("qmrectify1"));
					}else if("-1".equals(fx)){
						result = CHF.showNull(rs.getString("qmrectify2"));
					}else{
						result = CHF.showNull(rs.getString("qmrectify"));
					}
				} else if("以前年度调整".equals(res)){
					if("1".equals(fx)){
						result = CHF.showNull(rs.getString("qcrectify1"));
					}else if("-1".equals(fx)){
						result = CHF.showNull(rs.getString("qcrectify2"));
					}else{
						result = CHF.showNull(rs.getString("qcrectify"));
					}
				} else if("上期调整".equals(res)){
					if("1".equals(fx)){
						result = CHF.showNull(rs.getString("qrectify1"));
					}else if("-1".equals(fx)){
						result = CHF.showNull(rs.getString("qrectify2"));
					}else{
						result = CHF.showNull(rs.getString("qrectify"));
					}
				} else if("期初审定".equals(res)){
					if("1".equals(fx)){
						result = formatter.format(rs.getDouble("DebitRemain") + rs.getDouble("qcrectify1") );
					}else if("-1".equals(fx)){
						result = formatter.format(rs.getDouble("CreditRemain") + rs.getDouble("qcrectify2") );
					}else{
						result = formatter.format(rs.getDouble("initbalance") + rs.getDouble("qcrectify") );
					}
				}
				else if("期初未审".equals(res)){
					if("1".equals(fx)){
						result = formatter.format(rs.getDouble("DebitRemain") );
					}else if("-1".equals(fx)){
						result = formatter.format(rs.getDouble("CreditRemain")  );
					}else{
						result = formatter.format(rs.getDouble("initbalance")  );
					}
				}
			}
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	
	/**
	 * 指标的项目取数
	 * @param subname
	 * @param res
	 * @param fx
	 * @param bz
	 * @return
	 * @throws Exception
	 */
	public String getProject1(String subname,String res,String fx,String bz,String year,String month,
			int bYear,int bMonth,int eYear,int eMonth) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			Map args = new HashMap();
			
			Project project = new ProjectService(conn).getProjectById(this.proid);
			args.put("project", project);
			
			String tempTable = "tt_"+DELUnid.getCharUnid();
			String sql = "",strResult = "",allfield = "";
			
			/**
			 * 上年调整影响年末 否：上海立信
			 */
			String svalue = "";
			sql = "select svalue from s_config where sname='上年调整影响年末'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				svalue = rs.getString(1);
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			
			sql = "create table " + tempTable + " (  \n" + 
			"`mycol` varchar(20) default NULL,          \n" +		//列号
	        "  `myrow` varchar(20) default NULL,          \n" +		//行号
	        "  `myproperty` varchar(40) default NULL,     \n" +		//属性
	        "  `myname` varchar(60) default NULL,         \n" +		//名称
	        "  `myparam` varchar(100) default NULL,         \n" +	//参数
	        "  `myvalue` decimal(15,2) default NULL,        \n" +		//最终的值
	        " `pdirection` int(11) default NULL,            \n" +	//属性方向
	        " `ndirection` int(11) default NULL,            \n" +	//名称方向
	        "  `autoid` int(11) NOT NULL auto_increment,  \n" +
	        "  PRIMARY KEY  (`autoid`)                    \n" +
	        ") ENGINE=MyISAM DEFAULT CHARSET=gbk        \n";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);			
			
//			String AuditTimeBegin = project.getAuditTimeBegin();
//			String AuditTimeEnd = project.getAuditTimeEnd();
//			
//			int bYear = Integer.parseInt(AuditTimeBegin.substring(0, 4));
//			int bMonth = Integer.parseInt(AuditTimeBegin.substring(5, 7));
//			int eYear = Integer.parseInt(AuditTimeEnd.substring(0, 4));
//			int eMonth = Integer.parseInt(AuditTimeEnd.substring(5, 7));
			
			int StartYearMonth = bYear * 12 + bMonth;
			int EndYearMonth = 	eYear * 12 + eMonth;
			
			/**
			 * 年度为0，不用改变区间
			 * 年度为-1,就是取上年
			 */
			if(!"0".equals(year)){
				int iYearMonthArea = EndYearMonth - StartYearMonth + 1;
				
				if(iYearMonthArea >= 12 ){
					StartYearMonth = StartYearMonth + iYearMonthArea * Integer.parseInt(year);
					EndYearMonth = EndYearMonth + iYearMonthArea * Integer.parseInt(year);
				}else{
					StartYearMonth = (bYear + Integer.parseInt(year))*12+ bMonth;
					EndYearMonth = StartYearMonth + 11;
				}
				
				eYear += Integer.parseInt(year);				
				
			}
			
			//单月
			
			if(!"".equals(month)){
				
				int EndYearMonth1 = eYear * 12 + Integer.parseInt(month);
				
				if(!(EndYearMonth1>=StartYearMonth && EndYearMonth1 <= EndYearMonth)){
					return "0";
				}
			}
			
			/**
			 * 增加外币折合本位币 : 去除“折合”　例：美元折合（表示美元折合为人民币）
			 */
			String strName = "";
			if(bz.indexOf("折合")>-1){
				strName = "F";
				bz = CHF.replaceStr(bz, "折合", "");
			}
			
			if("人民币".equals(bz)){
				sql = "select 1 from c_accpackage where AccPackageID = " + this.pkgid + " and currName = '"+bz+"'";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				if(rs.next()){
					bz = "0";
				}
			}
			
			
			sql="insert into "+tempTable+"(mycol,myrow,myproperty,myname,myparam,pdirection,ndirection) values ( ?,?,?,?,?,?,?)";
			ps = conn.prepareStatement(sql);
			ps.setString(1, "1");
			ps.setString(2, "1");
			
			ps.setString(4, subname);
			ps.setString(5, "``"+year+"`"+month+"`"+bz+"``年度="+year);
			
			ps.setString(7, "1");
			
			if("项目期初".equals(res)){
				if("0".equals(bz)){		//本位币
					if("1".equals(fx)){
						allfield = "借方期初数";
						ps.setString(3, "借方期初数");
					}else if("-1".equals(fx)){
						allfield = "贷方期初数";
						ps.setString(3, "贷方期初数");
					}else{
						allfield = "期初数";
						ps.setString(3, "期初数");
					}
				}else{	//外币
					if("F".equals(strName)){ //外币折合本位币
						if("1".equals(fx)){
							allfield = "借方期初数";
							ps.setString(3, "借方期初数");
						}else if("-1".equals(fx)){
							allfield = "贷方期初数";
							ps.setString(3, "贷方期初数");
						}else{
							allfield = "期初数";
							ps.setString(3, "期初数");
						}
					}else{ 	//外币(原币)
						if("1".equals(fx)){
							allfield = "原币借方期初数";
							ps.setString(3, "原币借方期初数");
						}else if("-1".equals(fx)){
							allfield = "原币贷方期初数";
							ps.setString(3, "原币贷方期初数");
						}else{
							allfield = "原币期初数";
							ps.setString(3, "原币期初数");
						}
					}//exit //外币(原币)
				}//exit //外币
				ps.setString(6, "1");
				ps.addBatch();
				
			}else if("项目期末".equals(res)){
				if("0".equals(bz)){
					if("1".equals(fx)){
						allfield = "借方期末数";
						ps.setString(3, "借方期末数");
					}else if("-1".equals(fx)){
						allfield = "贷方期末数";
						ps.setString(3, "贷方期末数");
					}else{
						allfield = "期末数";
						ps.setString(3, "期末数");
					}
				}else{		//外币
					if("F".equals(strName)){ //外币折合本位币
						if("1".equals(fx)){
							allfield = "借方期末数";
							ps.setString(3, "借方期末数");
						}else if("-1".equals(fx)){
							allfield = "贷方期末数";
							ps.setString(3, "贷方期末数");
						}else{
							allfield = "期末数";
							ps.setString(3, "期末数");
						}
					}else{//外币(原币)
						if("1".equals(fx)){
							allfield = "原币借方期末数";
							ps.setString(3, "原币借方期末数");
						}else if("-1".equals(fx)){
							allfield = "原币贷方期末数";
							ps.setString(3, "原币贷方期末数");
						}else{
							allfield = "原币期末数";
							ps.setString(3, "原币期末数");
						}
					}//exit //外币(原币)
				}//exit //外币
				
				ps.setString(6, "1");
				ps.addBatch();
			}else if("项目发生".equals(res)){
				if("0".equals(bz)){
					if("1".equals(fx)){
						allfield = "借发生";
						ps.setString(3, "借发生");
					}else if("-1".equals(fx)){
						allfield = "贷发生";
						ps.setString(3, "贷发生");
					}else{
						allfield = "净发生";
						ps.setString(3, "净发生");
					}
				}else{	//外币
					if("F".equals(strName)){ //外币折合本位币
						if("1".equals(fx)){
							allfield = "借发生";
							ps.setString(3, "借发生");
						}else if("-1".equals(fx)){
							allfield = "贷发生";
							ps.setString(3, "贷发生");
						}else{
							allfield = "净发生";
							ps.setString(3, "净发生");
						}
					}else{ 	//外币(原币)
						if("1".equals(fx)){
							allfield = "原币借发生";
							ps.setString(3, "原币借发生");
						}else if("-1".equals(fx)){
							allfield = "原币贷发生";
							ps.setString(3, "原币贷发生");
						}else{
							allfield = "原币净发生";
							ps.setString(3, "原币净发生");
						}
					} //exit //外币(原币)
					
				} //exit 外币
				
				ps.setString(6, "1");
				ps.addBatch();
			}else if("账表不符".equals(res)){
				if("0".equals(bz)){
					if("1".equals(fx)){
						allfield = "账表不符借";
						ps.setString(3, "账表不符借");
						ps.setString(6, "1");
						ps.addBatch();
					}else if("-1".equals(fx)){
						allfield = "账表不符贷";
						ps.setString(3, "账表不符贷");
						ps.setString(6, "1");
						ps.addBatch();
					}else{
						allfield = "账表不符借`账表不符贷";
						ps.setString(3, "账表不符借");
						ps.setString(6, "1");
						ps.addBatch();
						ps.setString(3, "账表不符贷");
						ps.setString(6, "-1");
						ps.addBatch();
					}
				}else{//外币
					if("F".equals(strName)){ //外币折合本位币
						if("1".equals(fx)){
							allfield = "账表不符借";
							ps.setString(3, "账表不符借");
							ps.setString(6, "1");
							ps.addBatch();
						}else if("-1".equals(fx)){
							allfield = "账表不符贷";
							ps.setString(3, "账表不符贷");
							ps.setString(6, "1");
							ps.addBatch();
						}else{
							allfield = "账表不符借`账表不符贷";
							ps.setString(3, "账表不符借");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "账表不符贷");
							ps.setString(6, "-1");
							ps.addBatch();
						}
					}else{ 	//外币(原币)
						if("1".equals(fx)){
							allfield = "原币账表不符借";
							ps.setString(3, "原币账表不符借");
							ps.setString(6, "1");
							ps.addBatch();
						}else if("-1".equals(fx)){
							allfield = "原币账表不符贷";
							ps.setString(3, "原币账表不符贷");
							ps.setString(6, "1");
							ps.addBatch();
						}else{
							allfield = "原币账表不符借`原币账表不符贷";
							ps.setString(3, "原币账表不符借");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "原币账表不符贷");
							ps.setString(6, "-1");
							ps.addBatch();
						}
					}//exit //外币(原币)
				}//exit //外币
				
			}else if("期初调整".equals(res)){
				if("0".equals(bz)){
					if("1".equals(fx)){
						allfield = "期初调整借";
						ps.setString(3, "期初调整借");
						ps.setString(6, "1");
						ps.addBatch();
					}else if("-1".equals(fx)){
						allfield = "期初调整贷";
						ps.setString(3, "期初调整贷");
						ps.setString(6, "1");
						ps.addBatch();
					}else{
						allfield = "期初调整借`期初调整贷";
						ps.setString(3, "期初调整借");
						ps.setString(6, "1");
						ps.addBatch();
						ps.setString(3, "期初调整贷");
						ps.setString(6, "-1");
						ps.addBatch();
					}
				}else{//外币
					if("F".equals(strName)){ //外币折合本位币
						if("1".equals(fx)){
							allfield = "期初调整借";
							ps.setString(3, "期初调整借");
							ps.setString(6, "1");
							ps.addBatch();
						}else if("-1".equals(fx)){
							allfield = "期初调整贷";
							ps.setString(3, "期初调整贷");
							ps.setString(6, "1");
							ps.addBatch();
						}else{
							allfield = "期初调整借`期初调整贷";
							ps.setString(3, "期初调整借");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "期初调整贷");
							ps.setString(6, "-1");
							ps.addBatch();
						}
					}else{ 	//外币(原币)
						if("1".equals(fx)){
							allfield = "原币期初调整借";
							ps.setString(3, "原币期初调整借");
							ps.setString(6, "1");
							ps.addBatch();
						}else if("-1".equals(fx)){
							allfield = "原币期初调整贷";
							ps.setString(3, "原币期初调整贷");
							ps.setString(6, "1");
							ps.addBatch();
						}else{
							allfield = "原币期初调整借`原币期初调整贷";
							ps.setString(3, "原币期初调整借");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "原币期初调整贷");
							ps.setString(6, "-1");
							ps.addBatch();
						}
					} //exit //外币(原币)
				}//exit //外币 
			}else if("期初重分类".equals(res)){
				if("0".equals(bz)){
					if("1".equals(fx)){
						allfield = "期初重分类借";
						ps.setString(3, "期初重分类借");
						ps.setString(6, "1");
						ps.addBatch();
					}else if("-1".equals(fx)){
						allfield = "期初重分类贷";
						ps.setString(3, "期初重分类贷");
						ps.setString(6, "1");
						ps.addBatch();
					}else{
						allfield = "期初重分类借`期初重分类贷";
						ps.setString(3, "期初重分类借");
						ps.setString(6, "1");
						ps.addBatch();
						ps.setString(3, "期初重分类贷");
						ps.setString(6, "-1");
						ps.addBatch();
					}
				}else{//外币
					if("F".equals(strName)){ //外币折合本位币
						if("1".equals(fx)){
							allfield = "期初重分类借";
							ps.setString(3, "期初重分类借");
							ps.setString(6, "1");
							ps.addBatch();
						}else if("-1".equals(fx)){
							allfield = "期初重分类贷";
							ps.setString(3, "期初重分类贷");
							ps.setString(6, "1");
							ps.addBatch();
						}else{
							allfield = "期初重分类借`期初重分类贷";
							ps.setString(3, "期初重分类借");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "期初重分类贷");
							ps.setString(6, "-1");
							ps.addBatch();
						}
					}else{ 	//外币(原币)
						if("1".equals(fx)){
							allfield = "原币期初重分类借";
							ps.setString(3, "原币期初重分类借");
							ps.setString(6, "1");
							ps.addBatch();
						}else if("-1".equals(fx)){
							allfield = "原币期初重分类贷";
							ps.setString(3, "原币期初重分类贷");
							ps.setString(6, "1");
							ps.addBatch();
						}else{
							allfield = "原币期初重分类借`原币期初重分类贷";
							ps.setString(3, "原币期初重分类借");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "原币期初重分类贷");
							ps.setString(6, "-1");
							ps.addBatch();
						}
					}//exit //外币(原币)
				}//exit //外币
			}else if("期初不符未调".equals(res)){
				if("0".equals(bz)){
					if("1".equals(fx)){
						allfield = "期初不符未调借";
						ps.setString(3, "期初不符未调借");
						ps.setString(6, "1");
						ps.addBatch();
					}else if("-1".equals(fx)){
						allfield = "期初不符未调贷";
						ps.setString(3, "期初不符未调贷");
						ps.setString(6, "1");
						ps.addBatch();
					}else{
						allfield = "期初不符未调借`期初不符未调贷";
						ps.setString(3, "期初不符未调借");
						ps.setString(6, "1");
						ps.addBatch();
						ps.setString(3, "期初不符未调贷");
						ps.setString(6, "-1");
						ps.addBatch();
					}
				}else{//外币
					if("F".equals(strName)){ //外币折合本位币
						if("1".equals(fx)){
							allfield = "期初不符未调借";
							ps.setString(3, "期初不符未调借");
							ps.setString(6, "1");
							ps.addBatch();
						}else if("-1".equals(fx)){
							allfield = "期初不符未调贷";
							ps.setString(3, "期初不符未调贷");
							ps.setString(6, "1");
							ps.addBatch();
						}else{
							allfield = "期初不符未调借`期初不符未调贷";
							ps.setString(3, "期初不符未调借");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "期初不符未调贷");
							ps.setString(6, "-1");
							ps.addBatch();
						}
					}else{ 	//外币(原币)
						if("1".equals(fx)){
							allfield = "原币期初不符未调借";
							ps.setString(3, "原币期初不符未调借");
							ps.setString(6, "1");
							ps.addBatch();
						}else if("-1".equals(fx)){
							allfield = "原币期初不符未调贷";
							ps.setString(3, "原币期初不符未调贷");
							ps.setString(6, "1");
							ps.addBatch();
						}else{
							allfield = "原币期初不符未调借`原币期初不符未调贷";
							ps.setString(3, "原币期初不符未调借");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "原币期初不符未调贷");
							ps.setString(6, "-1");
							ps.addBatch();
						}
					} //exit //外币(原币)
				}//exit //外币
				
			}else if("期末调整".equals(res)){
				if("0".equals(bz)){
					if("1".equals(fx)){
						allfield = "期末调整借";
						ps.setString(3, "期末调整借");
						ps.setString(6, "1");
						ps.addBatch();
					}else if("-1".equals(fx)){
						allfield = "期末调整贷";
						ps.setString(3, "期末调整贷");
						ps.setString(6, "1");
						ps.addBatch();
					}else{
						allfield = "期末调整借`期末调整贷";
						ps.setString(3, "期末调整借");
						ps.setString(6, "1");
						ps.addBatch();
						ps.setString(3, "期末调整贷");
						ps.setString(6, "-1");
						ps.addBatch();
					}
				}else{//外币
					if("F".equals(strName)){ //外币折合本位币
						if("1".equals(fx)){
							allfield = "期末调整借";
							ps.setString(3, "期末调整借");
							ps.setString(6, "1");
							ps.addBatch();
						}else if("-1".equals(fx)){
							allfield = "期末调整贷";
							ps.setString(3, "期末调整贷");
							ps.setString(6, "1");
							ps.addBatch();
						}else{
							allfield = "期末调整借`期末调整贷";
							ps.setString(3, "期末调整借");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "期末调整贷");
							ps.setString(6, "-1");
							ps.addBatch();
						}
					}else{ 	//外币(原币)
						if("1".equals(fx)){
							allfield = "原币期末调整借";
							ps.setString(3, "原币期末调整借");
							ps.setString(6, "1");
							ps.addBatch();
						}else if("-1".equals(fx)){
							allfield = "原币期末调整贷";
							ps.setString(3, "原币期末调整贷");
							ps.setString(6, "1");
							ps.addBatch();
						}else{
							allfield = "原币期末调整借`原币期末调整贷";
							ps.setString(3, "原币期末调整借");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "原币期末调整贷");
							ps.setString(6, "-1");
							ps.addBatch();
						}
					}//exit //外币(原币)
				}//exit //外币
				
			}else if("期末重分类".equals(res)){
				if("0".equals(bz)){
					if("1".equals(fx)){
						allfield = "期末重分类借";
						ps.setString(3, "期末重分类借");
						ps.setString(6, "1");
						ps.addBatch();
					}else if("-1".equals(fx)){
						allfield = "期末重分类贷";
						ps.setString(3, "期末重分类贷");
						ps.setString(6, "1");
						ps.addBatch();
					}else{
						allfield = "期末重分类借`期末重分类贷";
						ps.setString(3, "期末重分类借");
						ps.setString(6, "1");
						ps.addBatch();
						ps.setString(3, "期末重分类贷");
						ps.setString(6, "-1");
						ps.addBatch();
					}
				}else{//外币
					if("F".equals(strName)){ //外币折合本位币
						if("1".equals(fx)){
							allfield = "期末重分类借";
							ps.setString(3, "期末重分类借");
							ps.setString(6, "1");
							ps.addBatch();
						}else if("-1".equals(fx)){
							allfield = "期末重分类贷";
							ps.setString(3, "期末重分类贷");
							ps.setString(6, "1");
							ps.addBatch();
						}else{
							allfield = "期末重分类借`期末重分类贷";
							ps.setString(3, "期末重分类借");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "期末重分类贷");
							ps.setString(6, "-1");
							ps.addBatch();
						}
					}else{ 	//外币(原币)
						if("1".equals(fx)){
							allfield = "原币期末重分类借";
							ps.setString(3, "原币期末重分类借");
							ps.setString(6, "1");
							ps.addBatch();
						}else if("-1".equals(fx)){
							allfield = "原币期末重分类贷";
							ps.setString(3, "原币期末重分类贷");
							ps.setString(6, "1");
							ps.addBatch();
						}else{
							allfield = "原币期末重分类借`原币期末重分类贷";
							ps.setString(3, "原币期末重分类借");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "原币期末重分类贷");
							ps.setString(6, "-1");
							ps.addBatch();
						}
					}//exit //外币(原币)
				}//exit //外币
				
			}else if("期末不符未调".equals(res)){
				if("0".equals(bz)){
					if("1".equals(fx)){
						allfield = "期末不符未调借";
						ps.setString(3, "期末不符未调借");
						ps.setString(6, "1");
						ps.addBatch();
					}else if("-1".equals(fx)){
						allfield = "期末不符未调贷";
						ps.setString(3, "期末不符未调贷");
						ps.setString(6, "1");
						ps.addBatch();
					}else{
						allfield = "期末不符未调借`期末不符未调贷";
						ps.setString(3, "期末不符未调借");
						ps.setString(6, "1");
						ps.addBatch();
						ps.setString(3, "期末不符未调贷");
						ps.setString(6, "-1");
						ps.addBatch();
					}
				}else{//外币
					if("F".equals(strName)){ //外币折合本位币
						if("1".equals(fx)){
							allfield = "期末不符未调借";
							ps.setString(3, "期末不符未调借");
							ps.setString(6, "1");
							ps.addBatch();
						}else if("-1".equals(fx)){
							allfield = "期末不符未调贷";
							ps.setString(3, "期末不符未调贷");
							ps.setString(6, "1");
							ps.addBatch();
						}else{
							allfield = "期末不符未调借`期末不符未调贷";
							ps.setString(3, "期末不符未调借");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "期末不符未调贷");
							ps.setString(6, "-1");
							ps.addBatch();
						}
					}else{ 	//外币(原币)
						if("1".equals(fx)){
							allfield = "原币期末不符未调借";
							ps.setString(3, "原币期末不符未调借");
							ps.setString(6, "1");
							ps.addBatch();
						}else if("-1".equals(fx)){
							allfield = "原币期末不符未调贷";
							ps.setString(3, "原币期末不符未调贷");
							ps.setString(6, "1");
							ps.addBatch();
						}else{
							allfield = "原币期末不符未调借`原币期末不符未调贷";
							ps.setString(3, "原币期末不符未调借");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "原币期末不符未调贷");
							ps.setString(6, "-1");
							ps.addBatch();
						}
					}//exit //外币(原币)
				}//exit //外币
				
			}else if("期初审定".equals(res)){
				if("0".equals(bz)){
					if("1".equals(fx)){
						allfield = "借方期初数`期初调整借`期初重分类借`账表不符借";
						ps.setString(3, "借方期初数");
						ps.setString(6, "1");
						ps.addBatch();
						ps.setString(3, "期初调整借");
						ps.setString(6, "1");
						ps.addBatch();
						ps.setString(3, "期初重分类借");
						ps.setString(6, "1");
						ps.addBatch();
						ps.setString(3, "账表不符借");
						ps.setString(6, "1");
						ps.addBatch();
					}else if("-1".equals(fx)){
						allfield = "贷方期初数`期初调整贷`期初重分类贷`账表不符贷";
						ps.setString(3, "贷方期初数");
						ps.setString(6, "1");
						ps.addBatch();
						ps.setString(3, "期初调整贷");
						ps.setString(6, "1");
						ps.addBatch();
						ps.setString(3, "期初重分类贷");
						ps.setString(6, "1");
						ps.addBatch();
						ps.setString(3, "账表不符贷");
						ps.setString(6, "1");
						ps.addBatch();
					}else{
						allfield = "审定期初";
						ps.setString(3, "审定期初");
						ps.setString(6, "1");
						ps.addBatch();
					}
				}else{//外币
					if("F".equals(strName)){ //外币折合本位币
						if("1".equals(fx)){
							allfield = "借方期初数`期初调整借`期初重分类借`账表不符借";
							ps.setString(3, "借方期初数");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "期初调整借");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "期初重分类借");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "账表不符借");
							ps.setString(6, "1");
							ps.addBatch();
						}else if("-1".equals(fx)){
							allfield = "贷方期初数`期初调整贷`期初重分类贷`账表不符贷";
							ps.setString(3, "贷方期初数");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "期初调整贷");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "期初重分类贷");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "账表不符贷");
							ps.setString(6, "1");
							ps.addBatch();
						}else{
							allfield = "审定期初";
							ps.setString(3, "审定期初");
							ps.setString(6, "1");
							ps.addBatch();
						}
					}else{ 	//外币(原币)
						if("1".equals(fx)){
							allfield = "原币借方期初数`原币期初调整借`原币期初重分类借`原币账表不符借";
							ps.setString(3, "原币借方期初数");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "原币期初调整借");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "原币期初重分类借");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "原币账表不符借");
							ps.setString(6, "1");
							ps.addBatch();
						}else if("-1".equals(fx)){
							allfield = "原币贷方期初数`原币期初调整贷`原币期初重分类贷`原币账表不符贷";
							ps.setString(3, "原币贷方期初数");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "原币期初调整贷");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "原币期初重分类贷");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "原币账表不符贷");
							ps.setString(6, "1");
							ps.addBatch();
						}else{
							allfield = "原币审定期初";
							ps.setString(3, "原币审定期初");
							ps.setString(6, "1");
							ps.addBatch();
						}
					}//exit //外币(原币)
				}//exit //外币
			}else if("期末审定".equals(res)){
				if("否".equals(svalue)){  //上年调整影响年末 否：上海立信
					if("0".equals(bz)){
						if("1".equals(fx)){
							allfield = "借方期末数`期末调整借`期末重分类借";
							ps.setString(3, "借方期末数");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "期末调整借");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "期末重分类借");
							ps.setString(6, "1");
							ps.addBatch();
						}else if("-1".equals(fx)){
							allfield = "贷方期末数`期末调整贷`期末重分类贷";
							ps.setString(3, "贷方期末数");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "期末调整贷");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "期末重分类贷");
							ps.setString(6, "1");
							ps.addBatch();
						}else{
							allfield = "审定期末";
							ps.setString(3, "审定期末");
							ps.setString(6, "1");
							ps.addBatch();
						}
					}else{//外币
						if("F".equals(strName)){ //外币折合本位币
							if("1".equals(fx)){
								allfield = "借方期末数`期末调整借`期末重分类借";
								ps.setString(3, "借方期末数");
								ps.setString(6, "1");
								ps.addBatch();
								ps.setString(3, "期末调整借");
								ps.setString(6, "1");
								ps.addBatch();
								ps.setString(3, "期末重分类借");
								ps.setString(6, "1");
								ps.addBatch();
							}else if("-1".equals(fx)){
								allfield = "贷方期末数`期末调整贷`期末重分类贷";
								ps.setString(3, "贷方期末数");
								ps.setString(6, "1");
								ps.addBatch();
								ps.setString(3, "期末调整贷");
								ps.setString(6, "1");
								ps.addBatch();
								ps.setString(3, "期末重分类贷");
								ps.setString(6, "1");
								ps.addBatch();
							}else{
								allfield = "审定期末";
								ps.setString(3, "审定期末");
								ps.setString(6, "1");
								ps.addBatch();
							}
						}else{ 	//外币(原币)
							if("1".equals(fx)){
								allfield = "原币借方期末数`原币期末调整借`原币期末重分类借";
								ps.setString(3, "原币借方期末数");
								ps.setString(6, "1");
								ps.addBatch();
								ps.setString(3, "原币期末调整借");
								ps.setString(6, "1");
								ps.addBatch();
								ps.setString(3, "原币期末重分类借");
								ps.setString(6, "1");
								ps.addBatch();
							}else if("-1".equals(fx)){
								allfield = "原币贷方期末数`原币期末调整贷`原币期末重分类贷";
								ps.setString(3, "原币贷方期末数");
								ps.setString(6, "1");
								ps.addBatch();
								ps.setString(3, "原币期末调整贷");
								ps.setString(6, "1");
								ps.addBatch();
								ps.setString(3, "原币期末重分类贷");
								ps.setString(6, "1");
								ps.addBatch();
							}else{
								allfield = "原币审定期末";
								ps.setString(3, "原币审定期末");
								ps.setString(6, "1");
								ps.addBatch();
							}
						}//exit //外币(原币)
					}//exit //外币

					
				}else{			//svalue 为　“是”
					if("0".equals(bz)){
						if("1".equals(fx)){
							allfield = "借方期末数`期初调整借`期初重分类借`账表不符借`期末调整借`期末重分类借";
							ps.setString(3, "借方期末数");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "期初调整借");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "期初重分类借");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "账表不符借");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "期末调整借");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "期末重分类借");
							ps.setString(6, "1");
							ps.addBatch();
						}else if("-1".equals(fx)){
							allfield = "贷方期末数`期初调整贷`期初重分类贷`账表不符贷`期末调整贷`期末重分类贷";
							ps.setString(3, "贷方期末数");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "期初调整贷");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "期初重分类贷");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "账表不符贷");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "期末调整贷");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "期末重分类贷");
							ps.setString(6, "1");
							ps.addBatch();
						}else{
							allfield = "审定期末";
							ps.setString(3, "审定期末");
							ps.setString(6, "1");
							ps.addBatch();
						}
					}else{//外币
						if("F".equals(strName)){ //外币折合本位币
							if("1".equals(fx)){
								allfield = "借方期末数`期初调整借`期初重分类借`账表不符借`期末调整借`期末重分类借";
								ps.setString(3, "借方期末数");
								ps.setString(6, "1");
								ps.addBatch();
								ps.setString(3, "期初调整借");
								ps.setString(6, "1");
								ps.addBatch();
								ps.setString(3, "期初重分类借");
								ps.setString(6, "1");
								ps.addBatch();
								ps.setString(3, "账表不符借");
								ps.setString(6, "1");
								ps.addBatch();
								ps.setString(3, "期末调整借");
								ps.setString(6, "1");
								ps.addBatch();
								ps.setString(3, "期末重分类借");
								ps.setString(6, "1");
								ps.addBatch();
							}else if("-1".equals(fx)){
								allfield = "贷方期末数`期初调整贷`期初重分类贷`账表不符贷`期末调整贷`期末重分类贷";
								ps.setString(3, "贷方期末数");
								ps.setString(6, "1");
								ps.addBatch();
								ps.setString(3, "期初调整贷");
								ps.setString(6, "1");
								ps.addBatch();
								ps.setString(3, "期初重分类贷");
								ps.setString(6, "1");
								ps.addBatch();
								ps.setString(3, "账表不符贷");
								ps.setString(6, "1");
								ps.addBatch();
								ps.setString(3, "期末调整贷");
								ps.setString(6, "1");
								ps.addBatch();
								ps.setString(3, "期末重分类贷");
								ps.setString(6, "1");
								ps.addBatch();
							}else{
								allfield = "审定期末";
								ps.setString(3, "审定期末");
								ps.setString(6, "1");
								ps.addBatch();
							}
						}else{ 	//外币(原币)
							if("1".equals(fx)){
								allfield = "原币借方期末数`原币期初调整借`原币期初重分类借`原币账表不符借`原币期末调整借`原币期末重分类借";
								ps.setString(3, "原币借方期末数");
								ps.setString(6, "1");
								ps.addBatch();
								ps.setString(3, "原币期初调整借");
								ps.setString(6, "1");
								ps.addBatch();
								ps.setString(3, "原币期初重分类借");
								ps.setString(6, "1");
								ps.addBatch();
								ps.setString(3, "原币账表不符借");
								ps.setString(6, "1");
								ps.addBatch();
								ps.setString(3, "原币期末调整借");
								ps.setString(6, "1");
								ps.addBatch();
								ps.setString(3, "原币期末重分类借");
								ps.setString(6, "1");
								ps.addBatch();
							}else if("-1".equals(fx)){
								allfield = "原币贷方期末数`原币期初调整贷`原币期初重分类贷`原币账表不符贷`原币期末调整贷`原币期末重分类贷";
								ps.setString(3, "原币贷方期末数");
								ps.setString(6, "1");
								ps.addBatch();
								ps.setString(3, "原币期初调整贷");
								ps.setString(6, "1");
								ps.addBatch();
								ps.setString(3, "原币期初重分类贷");
								ps.setString(6, "1");
								ps.addBatch();
								ps.setString(3, "原币账表不符贷");
								ps.setString(6, "1");
								ps.addBatch();
								ps.setString(3, "原币期末调整贷");
								ps.setString(6, "1");
								ps.addBatch();
								ps.setString(3, "原币期末重分类贷");
								ps.setString(6, "1");
								ps.addBatch();
							}else{
								allfield = "原币审定期末";
								ps.setString(3, "原币审定期末");
								ps.setString(6, "1");
								ps.addBatch();
							}
						}//exit //外币(原币)
					}//exit //外币
				}
				
				
				
				
			}
			
			else if("期初总调整".equals(res)){
				if("0".equals(bz)){
					if("1".equals(fx)){
						allfield = "期初调整借`期初重分类借`账表不符借";
						ps.setString(3, "期初调整借");
						ps.setString(6, "1");
						ps.addBatch();
						ps.setString(3, "期初重分类借");
						ps.setString(6, "1");
						ps.addBatch();
						ps.setString(3, "账表不符借");
						ps.setString(6, "1");
						ps.addBatch();
					}else if("-1".equals(fx)){
						allfield = "期初调整贷`期初重分类贷`账表不符贷";
						ps.setString(3, "期初调整贷");
						ps.setString(6, "1");
						ps.addBatch();
						ps.setString(3, "期初重分类贷");
						ps.setString(6, "1");
						ps.addBatch();
						ps.setString(3, "账表不符贷");
						ps.setString(6, "1");
						ps.addBatch();
					}else{
						allfield = "期初调整借`期初重分类借`账表不符借`期初调整贷`期初重分类贷`账表不符贷";
						ps.setString(3, "期初调整借");
						ps.setString(6, "1");
						ps.addBatch();
						ps.setString(3, "期初重分类借");
						ps.setString(6, "1");
						ps.addBatch();
						ps.setString(3, "账表不符借");
						ps.setString(6, "1");
						ps.addBatch();
						ps.setString(3, "期初调整贷");
						ps.setString(6, "-1");
						ps.addBatch();
						ps.setString(3, "期初重分类贷");
						ps.setString(6, "-1");
						ps.addBatch();
						ps.setString(3, "账表不符贷");
						ps.setString(6, "-1");
						ps.addBatch();
					}
				}else{//外币
					if("F".equals(strName)){ //外币折合本位币
						if("1".equals(fx)){
							allfield = "期初调整借`期初重分类借`账表不符借";
							ps.setString(3, "期初调整借");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "期初重分类借");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "账表不符借");
							ps.setString(6, "1");
							ps.addBatch();
						}else if("-1".equals(fx)){
							allfield = "期初调整贷`期初重分类贷`账表不符贷";
							ps.setString(3, "期初调整贷");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "期初重分类贷");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "账表不符贷");
							ps.setString(6, "1");
							ps.addBatch();
						}else{
							allfield = "期初调整借`期初重分类借`账表不符借`期初调整贷`期初重分类贷`账表不符贷";
							ps.setString(3, "期初调整借");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "期初重分类借");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "账表不符借");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "期初调整贷");
							ps.setString(6, "-1");
							ps.addBatch();
							ps.setString(3, "期初重分类贷");
							ps.setString(6, "-1");
							ps.addBatch();
							ps.setString(3, "账表不符贷");
							ps.setString(6, "-1");
							ps.addBatch();
						}
					}else{//外币(原币)
						if("1".equals(fx)){
							allfield = "原币期初调整借`原币期初重分类借`原币账表不符借";
							ps.setString(3, "原币期初调整借");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "原币期初重分类借");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "原币账表不符借");
							ps.setString(6, "1");
							ps.addBatch();
						}else if("-1".equals(fx)){
							allfield = "原币期初调整贷`原币期初重分类贷`原币账表不符贷";
							ps.setString(3, "原币期初调整贷");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "原币期初重分类贷");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "原币账表不符贷");
							ps.setString(6, "1");
							ps.addBatch();
						}else{
							allfield = "原币期初调整借`原币期初重分类借`原币账表不符借`原币期初调整贷`原币期初重分类贷`原币账表不符贷";
							ps.setString(3, "原币期初调整借");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "原币期初重分类借");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "原币账表不符借");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "原币期初调整贷");
							ps.setString(6, "-1");
							ps.addBatch();
							ps.setString(3, "原币期初重分类贷");
							ps.setString(6, "-1");
							ps.addBatch();
							ps.setString(3, "原币账表不符贷");
							ps.setString(6, "-1");
							ps.addBatch();
						}
					}//exit //外币(原币)
				}//exit //外币
				
			}else if("期末总调整".equals(res)){
				if("0".equals(bz)){
					if("1".equals(fx)){
						allfield = "期末调整借`期末重分类借";
						ps.setString(3, "期末调整借");
						ps.setString(6, "1");
						ps.addBatch();
						ps.setString(3, "期末重分类借");
						ps.setString(6, "1");
						ps.addBatch();
					}else if("-1".equals(fx)){
						allfield = "期末调整贷`期末重分类贷";
						ps.setString(3, "期末调整贷");
						ps.setString(6, "1");
						ps.addBatch();
						ps.setString(3, "期末重分类贷");
						ps.setString(6, "1");
						ps.addBatch();
					}else{
						allfield = "期末调整借`期末重分类借`期末调整贷`期末重分类贷";
						ps.setString(3, "期末调整借");
						ps.setString(6, "1");
						ps.addBatch();
						ps.setString(3, "期末重分类借");
						ps.setString(6, "1");
						ps.addBatch();
						ps.setString(3, "期末调整贷");
						ps.setString(6, "-1");
						ps.addBatch();
						ps.setString(3, "期末重分类贷");
						ps.setString(6, "-1");
						ps.addBatch();
					}
				}else{//外币
					if("F".equals(strName)){ //外币折合本位币
						if("1".equals(fx)){
							allfield = "期末调整借`期末重分类借";
							ps.setString(3, "期末调整借");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "期末重分类借");
							ps.setString(6, "1");
							ps.addBatch();
						}else if("-1".equals(fx)){
							allfield = "期末调整贷`期末重分类贷";
							ps.setString(3, "期末调整贷");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "期末重分类贷");
							ps.setString(6, "1");
							ps.addBatch();
						}else{
							allfield = "期末调整借`期末重分类借`期末调整贷`期末重分类贷";
							ps.setString(3, "期末调整借");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "期末重分类借");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "期末调整贷");
							ps.setString(6, "-1");
							ps.addBatch();
							ps.setString(3, "期末重分类贷");
							ps.setString(6, "-1");
							ps.addBatch();
						}
					}else{//外币(原币)
						if("1".equals(fx)){
							allfield = "原币期末调整借`原币期末重分类借";
							ps.setString(3, "原币期末调整借");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "原币期末重分类借");
							ps.setString(6, "1");
							ps.addBatch();
						}else if("-1".equals(fx)){
							allfield = "原币期末调整贷`原币期末重分类贷";
							ps.setString(3, "原币期末调整贷");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "原币期末重分类贷");
							ps.setString(6, "1");
							ps.addBatch();
						}else{
							allfield = "原币期末调整借`原币期末重分类借`原币期末调整贷`原币期末重分类贷";
							ps.setString(3, "原币期末调整借");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "原币期末重分类借");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "原币期末调整贷");
							ps.setString(6, "-1");
							ps.addBatch();
							ps.setString(3, "原币期末重分类贷");
							ps.setString(6, "-1");
							ps.addBatch();
						}
					}//exit //外币(原币)
				}//exit //外币
				
			}
			
			else if("期末未审".equals(res)){
				
				if("否".equals(svalue)){  //上年调整影响年末 否：上海立信
					if("0".equals(bz)){
						if("1".equals(fx)){
							allfield = "借方期末数";
							ps.setString(3, "借方期末数");
							ps.setString(6, "1");
							ps.addBatch();
						}else if("-1".equals(fx)){
							allfield = "贷方期末数";
							ps.setString(3, "贷方期末数");
							ps.setString(6, "1");
							ps.addBatch();
						}else{
							allfield = "期末数";
							ps.setString(3, "期末数");
							ps.setString(6, "1");
							ps.addBatch();
						}
					}else{
						if("F".equals(strName)){ //外币折合本位币
							if("1".equals(fx)){
								allfield = "借方期末数";
								ps.setString(3, "借方期末数");
								ps.setString(6, "1");
								ps.addBatch();
							}else if("-1".equals(fx)){
								allfield = "贷方期末数";
								ps.setString(3, "贷方期末数");
								ps.setString(6, "1");
								ps.addBatch();
							}else{
								allfield = "期末数";
								ps.setString(3, "期末数");
								ps.setString(6, "1");
								ps.addBatch();
							}
						}else{//外币(原币)
							if("1".equals(fx)){
								allfield = "原币借方期末数";
								ps.setString(3, "原币借方期末数");
								ps.setString(6, "1");
								ps.addBatch();
							}else if("-1".equals(fx)){
								allfield = "原币贷方期末数";
								ps.setString(3, "原币贷方期末数");
								ps.setString(6, "1");
								ps.addBatch();
							}else{
								allfield = "原币期末数";
								ps.setString(3, "原币期末数");
								ps.setString(6, "1");
								ps.addBatch();
							}
						}//exit //外币(原币)
					}//exit //外币
				}else{//svalue 为“是”
					if("0".equals(bz)){
						if("1".equals(fx)){
							allfield = "借方期末数`期初调整借`期初重分类借`账表不符借";
							ps.setString(3, "借方期末数");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "期初调整借");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "期初重分类借");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "账表不符借");
							ps.setString(6, "1");
							ps.addBatch();
						}else if("-1".equals(fx)){
							allfield = "贷方期末数`期初调整贷`期初重分类贷`账表不符贷";
							ps.setString(3, "贷方期末数");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "期初调整贷");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "期初重分类贷");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "账表不符贷");
							ps.setString(6, "1");
							ps.addBatch();
						}else{
							allfield = "期末数`期初调整借`期初重分类借`账表不符借`期初调整贷`期初重分类贷`账表不符贷";
							ps.setString(3, "期末数");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "期初调整借");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "期初重分类借");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "账表不符借");
							ps.setString(6, "1");
							ps.addBatch();
							ps.setString(3, "期初调整贷");
							ps.setString(6, "-1");
							ps.addBatch();
							ps.setString(3, "期初重分类贷");
							ps.setString(6, "-1");
							ps.addBatch();
							ps.setString(3, "账表不符贷");
							ps.setString(6, "-1");
							ps.addBatch();
						}
					}else{ //外币
						if("F".equals(strName)){ //外币折合本位币
							if("1".equals(fx)){
								allfield = "借方期末数`期初调整借`期初重分类借`账表不符借";
								ps.setString(3, "借方期末数");
								ps.setString(6, "1");
								ps.addBatch();
								ps.setString(3, "期初调整借");
								ps.setString(6, "1");
								ps.addBatch();
								ps.setString(3, "期初重分类借");
								ps.setString(6, "1");
								ps.addBatch();
								ps.setString(3, "账表不符借");
								ps.setString(6, "1");
								ps.addBatch();
							}else if("-1".equals(fx)){
								allfield = "贷方期末数`期初调整贷`期初重分类贷`账表不符贷";
								ps.setString(3, "贷方期末数");
								ps.setString(6, "1");
								ps.addBatch();
								ps.setString(3, "期初调整贷");
								ps.setString(6, "1");
								ps.addBatch();
								ps.setString(3, "期初重分类贷");
								ps.setString(6, "1");
								ps.addBatch();
								ps.setString(3, "账表不符贷");
								ps.setString(6, "1");
								ps.addBatch();
							}else{
								allfield = "期末数`期初调整借`期初重分类借`账表不符借`期初调整贷`期初重分类贷`账表不符贷";
								ps.setString(3, "期末数");
								ps.setString(6, "1");
								ps.addBatch();
								ps.setString(3, "期初调整借");
								ps.setString(6, "1");
								ps.addBatch();
								ps.setString(3, "期初重分类借");
								ps.setString(6, "1");
								ps.addBatch();
								ps.setString(3, "账表不符借");
								ps.setString(6, "1");
								ps.addBatch();
								ps.setString(3, "期初调整贷");
								ps.setString(6, "-1");
								ps.addBatch();
								ps.setString(3, "期初重分类贷");
								ps.setString(6, "-1");
								ps.addBatch();
								ps.setString(3, "账表不符贷");
								ps.setString(6, "-1");
								ps.addBatch();
							}
						}else{//外币(原币)
							if("1".equals(fx)){
								allfield = "原币借方期末数`原币期初调整借`原币期初重分类借`原币账表不符借";
								ps.setString(3, "原币借方期末数");
								ps.setString(6, "1");
								ps.addBatch();
								ps.setString(3, "原币期初调整借");
								ps.setString(6, "1");
								ps.addBatch();
								ps.setString(3, "原币期初重分类借");
								ps.setString(6, "1");
								ps.addBatch();
								ps.setString(3, "原币账表不符借");
								ps.setString(6, "1");
								ps.addBatch();
							}else if("-1".equals(fx)){
								allfield = "原币贷方期末数`原币期初调整贷`原币期初重分类贷`原币账表不符贷";
								ps.setString(3, "原币贷方期末数");
								ps.setString(6, "1");
								ps.addBatch();
								ps.setString(3, "原币期初调整贷");
								ps.setString(6, "1");
								ps.addBatch();
								ps.setString(3, "原币期初重分类贷");
								ps.setString(6, "1");
								ps.addBatch();
								ps.setString(3, "原币账表不符贷");
								ps.setString(6, "1");
								ps.addBatch();
							}else{
								allfield = "原币期末数`原币期初调整借`原币期初重分类借`原币账表不符借`原币期初调整贷`原币期初重分类贷`原币账表不符贷";
								ps.setString(3, "原币期末数");
								ps.setString(6, "1");
								ps.addBatch();
								ps.setString(3, "原币期初调整借");
								ps.setString(6, "1");
								ps.addBatch();
								ps.setString(3, "原币期初重分类借");
								ps.setString(6, "1");
								ps.addBatch();
								ps.setString(3, "原币账表不符借");
								ps.setString(6, "1");
								ps.addBatch();
								ps.setString(3, "原币期初调整贷");
								ps.setString(6, "-1");
								ps.addBatch();
								ps.setString(3, "原币期初重分类贷");
								ps.setString(6, "-1");
								ps.addBatch();
								ps.setString(3, "原币账表不符贷");
								ps.setString(6, "-1");
								ps.addBatch();
							}
						}//exit //外币(原币)
					}//exit //外币
				}
				
				
			}
			
			ps.executeBatch();
			DbUtil.close(ps);

			new RuleService(conn).getObject(this.proid, tempTable ,project); 
			
			sql="select myrow,mycol,sum(pdirection * pdirection * myvalue) as myvalue from "+tempTable +" group by myrow,mycol";
			ps = conn.prepareStatement(sql);
			rs=ps.executeQuery();
			if(rs.next()){
				strResult = new java.text.DecimalFormat("0.00").format(rs.getDouble("myvalue"));
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			sql = "drop table if EXISTS " + tempTable;
			ps = conn.prepareStatement(sql);
			ps.execute();
			
			return strResult;
			
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 帐上的项目取数　不用z_manuaccount表
	 * @param subname
	 * @param res
	 * @param fx
	 * @param bz
	 * @return
	 * @throws Exception
	 */
	public String getProject2(String subname,String res,String fx,String bz,String year,String month,
			int bYear,int bMonth,int eYear,int eMonth) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "";	
			String result = "";
			
			/**
			 * 上年调整影响年末 否：上海立信
			 */
			String svalue = "";
			sql = "select svalue from s_config where sname='上年调整影响年末'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				svalue = rs.getString(1);
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			/**
			 *科目对照  
			 */
			String skm = changeSubjectName(subname);
			if(!"".equals(skm)){
				subname = skm;
			}
			
			String sTab = "";
			String sTab1 = "";
			String table1 = "";
//			String table2 = "";
			String sql1 = "";
			
			
//			String AuditTimeBegin = "";
//			String AuditTimeEnd = "";
//			sql = "select * from z_project where projectid='"+this.proid+"'";
//			ps = conn.prepareStatement(sql);
//			rs = ps.executeQuery();
//			if(rs.next()){
//				AuditTimeBegin = rs.getString("AuditTimeBegin");
//				AuditTimeEnd = rs.getString("AuditTimeEnd");
//			}
//			DbUtil.close(rs);
//			DbUtil.close(ps);
//			
//			int bYear = Integer.parseInt(AuditTimeBegin.substring(0, 4));
//			int bMonth = Integer.parseInt(AuditTimeBegin.substring(5, 7));
//			int eYear = Integer.parseInt(AuditTimeEnd.substring(0, 4));
//			int eMonth = Integer.parseInt(AuditTimeEnd.substring(5, 7));
			
			int StartYearMonth = bYear * 12 + bMonth;
			int EndYearMonth = 	eYear * 12 + eMonth;
			
			int sqlEndYearMonth = EndYearMonth;
			
			/**
			 * 科目方向
			 */
			sql = "select distinct direction2 from c_Account a where 1=1 " +
			" and SubYearMonth * 12 + SubMonth >= "+StartYearMonth+" " +
			" and SubYearMonth * 12 + SubMonth <= "+EndYearMonth+" " +
			" and a.subjectfullname2 = '"+subname+"' " +
			" union " +
			" select if(substring(property,2,1)='2',-1,1) " +
			" from z_usesubject where projectid='"+this.proid+"' " +
			" and subjectfullname = '"+subname+"' " ;
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			int direction2 = 1;
			if(rs.next()){
				direction2 = rs.getInt(1);
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			

			/**
			 * 增加外币折合本位币 : 去除“折合”　例：美元折合（表示美元折合为人民币）
			 */
			String strName = "";
			if(bz.indexOf("折合")>-1){
				strName = "F";
				bz = CHF.replaceStr(bz, "折合", "");
			}
			
			if("人民币".equals(bz)){
				sql = "select 1 from c_accpackage where AccPackageID = " + this.pkgid + " and currName = '"+bz+"'";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				if(rs.next()){
					bz = "0";
				}
			}
			
			
			String isleaf = "";
			if (bz.equals("0") || bz.equals("") || bz.equals("本位币")) {
				sTab = " c_Account a ";
				isleaf = "isleaf";
				sTab1 = " z_accountrectify a  where a.projectid='"+this.proid+"' ";
				bz = "0";
				table1 = "\n ifnull(sum(ifnull(DebitTotalOcc0,0)),0) DebitTotalOcc0,ifnull(sum(ifnull(CreditTotalOcc0,0)),0) CreditTotalOcc0,ifnull(sum(if("+direction2+"=0,ifnull((DebitTotalOcc0-CreditTotalOcc0),0),ifnull("+direction2+"*(DebitTotalOcc0-CreditTotalOcc0),0))),0) rectify0," ;
//				table2 = ""; 
				
			} 
			else {
				isleaf = "isleaf1";
				sTab = " c_AccountAll a ";
				sTab1 = "  z_accountallrectify a  where a.projectid='"+this.proid+"' and a.DataName='"+bz+"' ";
//				table2 = " and a.accsign=1 ";
			}
			
			/**
			 * 年度为0，不用改变区间
			 * 年度为-1,就是取上年
			 */
			if(!"0".equals(year)){
				//System.out.println(AuditTimeBegin + "|" + AuditTimeEnd + "|" + StartYearMonth + "|" + EndYearMonth);
				
				int iYearMonthArea = EndYearMonth - StartYearMonth + 1;
				
				if(iYearMonthArea >= 12 ){
					StartYearMonth = StartYearMonth + iYearMonthArea * Integer.parseInt(year);
					EndYearMonth = EndYearMonth + iYearMonthArea * Integer.parseInt(year);
				}else{
					StartYearMonth = (bYear + Integer.parseInt(year))*12+ bMonth;
					EndYearMonth = StartYearMonth + 11;
				}
				
				//System.out.println(AuditTimeBegin + "|" + AuditTimeEnd + "|" + StartYearMonth + "|" + EndYearMonth);
				bYear += Integer.parseInt(year);	//修改期初年
				eYear += Integer.parseInt(year);	//修改期末年			
				if (bz.equals("0") || bz.equals("") || bz.equals("本位币")) {
					isleaf = "isleaf1";
					sTab = " c_Account a ";
					sTab1 = " z_accountyearrectify a  where a.yearrectify = '"+eYear+"' and a.projectid='"+this.proid+"' ";
					bz = "0";
					table1 = "\n ifnull(sum(ifnull(DebitTotalOcc0,0)),0) DebitTotalOcc0,ifnull(sum(ifnull(CreditTotalOcc0,0)),0) CreditTotalOcc0,ifnull(sum(if("+direction2+"=0,ifnull((DebitTotalOcc0-CreditTotalOcc0),0),ifnull("+direction2+"*(DebitTotalOcc0-CreditTotalOcc0),0))),0) rectify0," ;
//					table2 = ""; 
				} 
				else {
					isleaf = "isleaf1";
					sTab = " c_AccountAll a ";
					sTab1 = " z_accountallyearrectify a  where a.yearrectify = '"+eYear+"' and a.projectid='"+this.proid+"' and a.DataName='"+bz+"' ";
//					table2 = " and a.accsign=1 ";
				}
				
			}
			
			//单月
			if(!"".equals(month)){
				
				int EndYearMonth1 = eYear * 12 + Integer.parseInt(month);
				
				if(EndYearMonth1>=StartYearMonth && EndYearMonth1 <= EndYearMonth){
					StartYearMonth = EndYearMonth1;
					EndYearMonth = EndYearMonth1;
				}else{
					return "0";
				}
			}
			
			double DebitRemain = 0.00;
			double CreditRemain = 0.00;
			double initbalance = 0.00;
			
			double DebitBalance = 0.00;
			double CreditBalance = 0.00;
			double Balance = 0.00;
			
			double DebitOcc = 0.00;
			double CreditOcc = 0.00;
			double Occ = 0.00;
			
			int i = 0;
			
			for(int iMonth = bYear; iMonth <= eYear;iMonth ++){
				
				/**
				 * 所有下级
				 */
				sql = "select group_concat(distinct \"'\",subjectid,\"'\") subjects from (" +
					" select distinct subjectid from c_Account a where 1=1 " +
					" and SubYearMonth * 12 + SubMonth >= "+StartYearMonth+" " +
					" and SubYearMonth * 12 + SubMonth <= "+sqlEndYearMonth+" " +
					" and SubYearMonth = "+iMonth+" " +
//					" and a.subjectfullname2 = '"+subname+"'" ;
					" and isleaf1 = 1 " +
					" and (a.subjectfullname2 = '"+subname+"' or a.subjectfullname2 like '"+subname+"/%' )" +
					" union \n" +
					" select subjectid \n" +
					" from z_usesubject \n" +
					" where projectid="+this.proid+" \n" +
					" and tipsubjectid in (" +
					"	select distinct subjectid from c_account a " +
					" 	where 1=1 " + 
					" 	and a.subyearmonth*12+a.submonth>='"+StartYearMonth+"' and a.subyearmonth*12+a.submonth<='"+sqlEndYearMonth+"' " +
					" 	and a.subyearmonth = '"+iMonth+"'  " +
					"	and (a.subjectfullname2 like '"+subname+"/%'  or a.subjectfullname2 = '"+subname+"' ) and level1 = 1 " +
					" ) and isleaf = 1 \n" +
					" union " +
					" select subjectid \n" +
					" from z_usesubject a \n" +
					" where projectid="+this.proid+" \n" +
					" and (a.subjectfullname like '"+subname+"/%'  or a.subjectfullname = '"+subname+"' ) and isleaf = 1 " +
					" ) a" ;

				org.util.Debug.prtOut("所有下级SQL:"+sql);
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				String subjects = "''";
				if(rs.next()){
					subjects = rs.getString(1);
				}
				DbUtil.close(rs);
				DbUtil.close(ps);
				
				sql1 =" and a.subjectid in ("+subjects+") ";
				
				
				/**
				 * 得到期初、发生、期末
				 */
				sql = "select sum(DebitRemain) DebitRemain,sum(CreditRemain) CreditRemain," +
				"\n sum(if(direction2=0,initbalance,direction2*initbalance)) initbalance," +
				"\n sum(DebitTotalOcc) DebitOcc,sum(CreditTotalOcc) CreditOcc,sum(if(direction2=0,(DebitTotalOcc-CreditTotalOcc),direction2*(DebitTotalOcc - CreditTotalOcc))) Occ," +
				"\n sum(DebitBalance) DebitBalance,sum(CreditBalance) CreditBalance," +
				"\n sum(if(direction2=0,Balance,direction2*Balance)) Balance " +
				
				"\n from (" +
				
				"\n 	select subjectid as rsubjectid ,DataName as rDataName,direction2," +
				"\n		sum(if(SubYearMonth * 12 + SubMonth="+StartYearMonth+",DebitRemain"+strName+",0)) DebitRemain," +
				"\n		sum(if(SubYearMonth * 12 + SubMonth="+StartYearMonth+",(-1)*CreditRemain"+strName+",0)) CreditRemain," +
				"\n		sum(if(SubYearMonth * 12 + SubMonth="+StartYearMonth+",(DebitRemain"+strName+" + CreditRemain"+strName+"),0)) initbalance," +
				"\n		sum(DebitOcc"+strName+") DebitTotalOcc, " +
				"\n		sum(CreditOcc"+strName+") CreditTotalOcc, " +
				"\n		(sum(DebitOcc"+strName+") - sum(CreditOcc"+strName+")) Occ," +
				"\n		sum(if(SubYearMonth * 12 + SubMonth="+EndYearMonth+",DebitBalance"+strName+",0)) DebitBalance," +
				"\n		sum(if(SubYearMonth * 12 + SubMonth="+EndYearMonth+",(-1)*CreditBalance"+strName+",0)) CreditBalance," +
				"\n		sum(if(SubYearMonth * 12 + SubMonth="+EndYearMonth+",Balance"+strName+",0)) Balance " +
				"\n		from "+sTab+
				"\n		where substring(a.AccPackageID,1,6) = '"+this.pkgid.substring(0,6)+"'" +
				"\n		and SubYearMonth * 12 + SubMonth >= "+StartYearMonth+" " +
				"\n		and SubYearMonth * 12 + SubMonth <= "+EndYearMonth+"" +
				"\n		and SubYearMonth = "+iMonth+" " +
				"\n		and DataName='"+bz+"' " + sql1 +
				"\n		group by a.subjectid,DataName" +
				
				"\n		union " +
				
				"\n		select subjectid as rsubjectid ,0 as rDataName,if(substring(property,2,1) =2,-1,1) as direction2, " +
				"\n		0,0,0, 0,0,0, 0,0,0" +
				"\n		from z_usesubject a where projectid = "+this.proid+" " +sql1 +
			
				"\n ) a where 1=1 ";
				
//				org.util.Debug.prtOut("getProject 科目SQL: " + sql);
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while (rs.next()) {
					
					DebitRemain += rs.getDouble("DebitRemain");
					CreditRemain += rs.getDouble("CreditRemain");
					initbalance += rs.getDouble("initbalance");
					
					DebitBalance += rs.getDouble("DebitBalance");
					CreditBalance += rs.getDouble("CreditBalance");
					Balance += rs.getDouble("Balance");
					
					DebitOcc += rs.getDouble("DebitOcc");
					CreditOcc += rs.getDouble("CreditOcc");
					Occ += rs.getDouble("Occ");
					
				}
				DbUtil.close(rs);
				DbUtil.close(ps);
				
			}
			
			if("项目期初".equals(res)){
				if("1".equals(fx)){
					return CHF.showNull(String.valueOf(DebitRemain));
				}else if("-1".equals(fx)){
					return CHF.showNull(String.valueOf(CreditRemain));
				}else{
					return CHF.showNull(String.valueOf(initbalance));
				}
			}else if("项目期末".equals(res)){
			if("1".equals(fx)){
					return CHF.showNull(String.valueOf(DebitBalance));
				}else if("-1".equals(fx)){
					return CHF.showNull(String.valueOf(CreditBalance));
				}else{
					return CHF.showNull(String.valueOf(Balance));
				}
			}else if("项目发生".equals(res)){
				if("1".equals(fx)){
					return CHF.showNull(String.valueOf(DebitOcc));
				}else if("-1".equals(fx)){
					return CHF.showNull(String.valueOf(CreditOcc));
				}else{
					return CHF.showNull(String.valueOf(Occ));
				}
			}
//			/**
//			 * 所有下级
//			 */
//			sql = "select group_concat(distinct \"'\",subjectid,\"'\") subjects from (" +
//				" select distinct subjectid from c_Account a where 1=1 " +
//				" and SubYearMonth * 12 + SubMonth >= "+StartYearMonth+" " +
//				" and SubYearMonth * 12 + SubMonth <= "+sqlEndYearMonth+" " +
////				" and a.subjectfullname2 = '"+subname+"'" ;
//				" and isleaf1 = 1 " +
//				" and (a.subjectfullname2 = '"+subname+"' or a.subjectfullname2 like '"+subname+"/%' )" +
//				" union \n" +
//				" select subjectid \n" +
//				" from z_usesubject \n" +
//				" where projectid="+this.proid+" \n" +
//				" and tipsubjectid in (" +
//				"	select distinct subjectid from c_account a " +
//				" 	where 1=1 " + 
//				" 	and a.subyearmonth*12+a.submonth>='"+StartYearMonth+"' and a.subyearmonth*12+a.submonth<='"+sqlEndYearMonth+"' " +
//				"	and (a.subjectfullname2 like '"+subname+"/%'  or a.subjectfullname2 = '"+subname+"' ) and level1 = 1 " +
//				" ) and isleaf = 1 \n" +
//				" union " +
//				" select subjectid \n" +
//				" from z_usesubject a \n" +
//				" where projectid="+this.proid+" \n" +
//				" and (a.subjectfullname like '"+subname+"/%'  or a.subjectfullname = '"+subname+"' ) and isleaf = 1 " +
//				" ) a" ;
//
//			//System.out.println("所有下级SQL:"+sql);
//			ps = conn.prepareStatement(sql);
//			rs = ps.executeQuery();
//			String subjects = "''";
//			if(rs.next()){
//				subjects = rs.getString(1);
//			}
//			DbUtil.close(rs);
//			DbUtil.close(ps);
//			
//			sql1 +=" and a.subjectid in ("+subjects+") ";
//			
//			/**
//			 * 得到期初、发生、期末
//			 */
//			sql = "select sum(DebitRemain) DebitRemain,sum(CreditRemain) CreditRemain," +
//			"\n sum(if(direction2=0,initbalance,direction2*initbalance)) initbalance," +
//			"\n sum(DebitTotalOcc) DebitOcc,sum(CreditTotalOcc) CreditOcc,sum(if(direction2=0,(DebitTotalOcc-CreditTotalOcc),direction2*(DebitTotalOcc - CreditTotalOcc))) Occ," +
//			"\n sum(DebitBalance) DebitBalance,sum(CreditBalance) CreditBalance," +
//			"\n sum(if(direction2=0,Balance,direction2*Balance)) Balance " +
//			
//			"\n from (" +
//			
//			"\n 	select subjectid as rsubjectid ,DataName as rDataName,direction2," +
//			"\n		sum(if(SubYearMonth * 12 + SubMonth="+StartYearMonth+",DebitRemain"+strName+",0)) DebitRemain," +
//			"\n		sum(if(SubYearMonth * 12 + SubMonth="+StartYearMonth+",(-1)*CreditRemain"+strName+",0)) CreditRemain," +
//			"\n		sum(if(SubYearMonth * 12 + SubMonth="+StartYearMonth+",(DebitRemain"+strName+" + CreditRemain"+strName+"),0)) initbalance," +
//			"\n		sum(DebitOcc"+strName+") DebitTotalOcc, " +
//			"\n		sum(CreditOcc"+strName+") CreditTotalOcc, " +
//			"\n		(sum(DebitOcc"+strName+") - sum(CreditOcc"+strName+")) Occ," +
//			"\n		sum(if(SubYearMonth * 12 + SubMonth="+EndYearMonth+",DebitBalance"+strName+",0)) DebitBalance," +
//			"\n		sum(if(SubYearMonth * 12 + SubMonth="+EndYearMonth+",(-1)*CreditBalance"+strName+",0)) CreditBalance," +
//			"\n		sum(if(SubYearMonth * 12 + SubMonth="+EndYearMonth+",Balance"+strName+",0)) Balance " +
//			"\n		from "+sTab+
//			"\n		where substring(a.AccPackageID,1,6) = '"+this.pkgid.substring(0,6)+"'" +
//			"\n		and SubYearMonth * 12 + SubMonth >= "+StartYearMonth+" " +
//			"\n		and SubYearMonth * 12 + SubMonth <= "+EndYearMonth+"" +
//			"\n		and DataName='"+bz+"' " + table2 + sql1 +
//			"\n		group by a.subjectid,DataName" +
//			
//			"\n		union " +
//			
//			"\n		select subjectid as rsubjectid ,0 as rDataName,if(substring(property,2,1) =2,-1,1) as direction2, " +
//			"\n		0,0,0, 0,0,0, 0,0,0" +
//			"\n		from z_usesubject a where projectid = "+this.proid+" " +sql1 +
//		
//			"\n ) a where 1=1 ";
//			
//			//org.util.Debug.prtOut("getProject 科目SQL: " + sql);
//			ps = conn.prepareStatement(sql);
//			rs = ps.executeQuery();
//			int i = 0;
//			
//			while (rs.next()) {
//				i++;
//				if (i > 1) {
//					throw new  Exception("取项目数出错！");					
//				}	
//				
//				DebitRemain = rs.getDouble("DebitRemain");
//				CreditRemain = rs.getDouble("CreditRemain");
//				initbalance = rs.getDouble("initbalance");
//				
//				DebitBalance = rs.getDouble("DebitBalance");
//				CreditBalance = rs.getDouble("CreditBalance");
//				Balance = rs.getDouble("Balance");
//				
//				DebitOcc = rs.getDouble("DebitOcc");
//				CreditOcc = rs.getDouble("CreditOcc");
//				Occ = rs.getDouble("Occ");
//				
//				result = CHF.showNull(rs.getString(1));
//				if("项目期初".equals(res)){
//					if("1".equals(fx)){
//						return CHF.showNull(String.valueOf(DebitRemain));
//					}else if("-1".equals(fx)){
//						return CHF.showNull(String.valueOf(CreditRemain));
//					}else{
//						return CHF.showNull(String.valueOf(initbalance));
//					}
//				}else if("项目期末".equals(res)){
//				if("1".equals(fx)){
//						return CHF.showNull(String.valueOf(DebitBalance));
//					}else if("-1".equals(fx)){
//						return CHF.showNull(String.valueOf(CreditBalance));
//					}else{
//						return CHF.showNull(String.valueOf(Balance));
//					}
//				}else if("项目发生".equals(res)){
//					if("1".equals(fx)){
//						return CHF.showNull(String.valueOf(DebitOcc));
//					}else if("-1".equals(fx)){
//						return CHF.showNull(String.valueOf(CreditOcc));
//					}else{
//						return CHF.showNull(String.valueOf(Occ));
//					}
//				}
//			}	
//			
//			DbUtil.close(rs);
//			DbUtil.close(ps);
			
			/**
			 * 得到调整
			 */
			sql = "select  " +
			"\n ifnull(sum(ifnull("+isleaf+" * DebitTotalOcc6"+strName+",0)),0) DebitTotalOcc6,ifnull(sum(ifnull("+isleaf+" * CreditTotalOcc6"+strName+",0)),0) CreditTotalOcc6,ifnull(sum(if("+direction2+"=0,ifnull("+isleaf+" * (DebitTotalOcc6"+strName+"-CreditTotalOcc6"+strName+"),0),ifnull("+direction2+"*"+isleaf+" * (DebitTotalOcc6"+strName+"-CreditTotalOcc6"+strName+"),0))),0) rectify6," + 
			"\n ifnull(sum(ifnull("+isleaf+" * DebitTotalOcc4"+strName+",0)),0) DebitTotalOcc4,ifnull(sum(ifnull("+isleaf+" * CreditTotalOcc4"+strName+",0)),0) CreditTotalOcc4,ifnull(sum(if("+direction2+"=0,ifnull("+isleaf+" * (DebitTotalOcc4"+strName+"-CreditTotalOcc4"+strName+"),0),ifnull("+direction2+"*"+isleaf+" * (DebitTotalOcc4"+strName+"-CreditTotalOcc4"+strName+"),0))),0) rectify4," +
			"\n ifnull(sum(ifnull("+isleaf+" * DebitTotalOcc5"+strName+",0)),0) DebitTotalOcc5,ifnull(sum(ifnull("+isleaf+" * CreditTotalOcc5"+strName+",0)),0) CreditTotalOcc5,ifnull(sum(if("+direction2+"=0,ifnull("+isleaf+" * (DebitTotalOcc5"+strName+"-CreditTotalOcc5"+strName+"),0),ifnull("+direction2+"*"+isleaf+" * (DebitTotalOcc5"+strName+"-CreditTotalOcc5"+strName+"),0))),0) rectify5," +
			
			table1 +
			
			"\n ifnull(sum(ifnull("+isleaf+" * DebitTotalOcc1"+strName+",0)),0) DebitTotalOcc1,ifnull(sum(ifnull("+isleaf+" * CreditTotalOcc1"+strName+",0)),0) CreditTotalOcc1,ifnull(sum(if("+direction2+"=0,ifnull("+isleaf+" * (DebitTotalOcc1"+strName+"-CreditTotalOcc1"+strName+"),0),ifnull("+direction2+"*"+isleaf+" * (DebitTotalOcc1"+strName+"-CreditTotalOcc1"+strName+"),0))),0) rectify1," +
			"\n ifnull(sum(ifnull("+isleaf+" * DebitTotalOcc2"+strName+",0)),0) DebitTotalOcc2,ifnull(sum(ifnull("+isleaf+" * CreditTotalOcc2"+strName+",0)),0) CreditTotalOcc2,ifnull(sum(if("+direction2+"=0,ifnull("+isleaf+" * (DebitTotalOcc2"+strName+"-CreditTotalOcc2"+strName+"),0),ifnull("+direction2+"*"+isleaf+" * (DebitTotalOcc2"+strName+"-CreditTotalOcc2"+strName+"),0))),0) rectify2," +
			"\n ifnull(sum(ifnull("+isleaf+" * DebitTotalOcc3"+strName+",0)),0) DebitTotalOcc3,ifnull(sum(ifnull("+isleaf+" * CreditTotalOcc3"+strName+",0)),0) CreditTotalOcc3,ifnull(sum(if("+direction2+"=0,ifnull("+isleaf+" * (DebitTotalOcc3"+strName+"-CreditTotalOcc3"+strName+"),0),ifnull("+direction2+"*"+isleaf+" * (DebitTotalOcc3"+strName+"-CreditTotalOcc3"+strName+"),0))),0) rectify3," +

			"\n ifnull(sum(if("+direction2+"=0,ifnull("+isleaf+" * (DebitTotalOcc6"+strName+"-CreditTotalOcc6"+strName+"),0) + ifnull("+isleaf+" * (DebitTotalOcc4"+strName+"-CreditTotalOcc4"+strName+"),0) + ifnull("+isleaf+" * (DebitTotalOcc5"+strName+"-CreditTotalOcc5"+strName+"),0),ifnull("+direction2+"*"+isleaf+" * (DebitTotalOcc6"+strName+"-CreditTotalOcc6"+strName+"),0) + ifnull("+direction2+"*"+isleaf+" * (DebitTotalOcc4"+strName+"-CreditTotalOcc4"+strName+"),0) + ifnull("+direction2+"*"+isleaf+" * (DebitTotalOcc5"+strName+"-CreditTotalOcc5"+strName+"),0) )),0) qcrectify," +
			"\n ifnull(sum(ifnull("+isleaf+" * DebitTotalOcc6"+strName+",0) + ifnull("+isleaf+" * DebitTotalOcc4"+strName+",0) + ifnull("+isleaf+" * DebitTotalOcc5"+strName+",0)),0) qcrectify1," +
			"\n ifnull(sum(ifnull("+isleaf+" * CreditTotalOcc6"+strName+",0) + ifnull("+isleaf+" * CreditTotalOcc4"+strName+",0) + ifnull("+isleaf+" * CreditTotalOcc5"+strName+",0)),0) qcrectify2, \n " +

			"\n ifnull(sum(if("+direction2+"=0,ifnull("+isleaf+" * (DebitTotalOcc1"+strName+"-CreditTotalOcc1"+strName+"),0) + ifnull("+isleaf+" * (DebitTotalOcc2"+strName+"-CreditTotalOcc2"+strName+"),0),ifnull("+direction2+"*"+isleaf+" * (DebitTotalOcc1"+strName+"-CreditTotalOcc1"+strName+"),0) + ifnull("+direction2+"*"+isleaf+" * (DebitTotalOcc2"+strName+"-CreditTotalOcc2"+strName+"),0) )),0) qmrectify, "+
			"\n ifnull(sum(ifnull("+isleaf+" * DebitTotalOcc1"+strName+",0) + ifnull("+isleaf+" * DebitTotalOcc2"+strName+",0)),0) qmrectify1, " +
			"\n ifnull(sum(ifnull("+isleaf+" * CreditTotalOcc1"+strName+",0) + ifnull("+isleaf+" * CreditTotalOcc2"+strName+",0)),0) qmrectify2 " +

			"\n from " + sTab1 + sql1; 
			
			//org.util.Debug.prtOut("getProject 调整SQL: " + sql);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			i = 0;
			while (rs.next()) {
				i++;
				if (i > 1) {
					throw new  Exception("取项目数出错！");					
				}
				
				double DebitTotalOcc6 = rs.getDouble("DebitTotalOcc6");
				double CreditTotalOcc6 = rs.getDouble("CreditTotalOcc6");
				double rectify6 = rs.getDouble("rectify6");
				
				double DebitTotalOcc4 = rs.getDouble("DebitTotalOcc4");
				double CreditTotalOcc4 = rs.getDouble("CreditTotalOcc4");
				double rectify4 = rs.getDouble("rectify4");
				
				double DebitTotalOcc5 = rs.getDouble("DebitTotalOcc5");
				double CreditTotalOcc5 = rs.getDouble("CreditTotalOcc5");
				double rectify5 = rs.getDouble("rectify5");
				
				double DebitTotalOcc1 = rs.getDouble("DebitTotalOcc1");
				double CreditTotalOcc1 = rs.getDouble("CreditTotalOcc1");
				double rectify1 = rs.getDouble("rectify1");
				
				double DebitTotalOcc2 = rs.getDouble("DebitTotalOcc2");
				double CreditTotalOcc2 = rs.getDouble("CreditTotalOcc2");
				double rectify2 = rs.getDouble("rectify2");
				
				double DebitTotalOcc3 = rs.getDouble("DebitTotalOcc3");
				double CreditTotalOcc3 = rs.getDouble("CreditTotalOcc3");
				double rectify3 = rs.getDouble("rectify3");
				
				double qcrectify = rs.getDouble("qcrectify");
				double qcrectify1 = rs.getDouble("qcrectify1");
				double qcrectify2 = rs.getDouble("qcrectify2");
				
				double qmrectify = rs.getDouble("qmrectify");
				double qmrectify1 = rs.getDouble("qmrectify1");
				double qmrectify2 = rs.getDouble("qmrectify2");
				
				double DebitTotalOcc0 = 0.00;
				double CreditTotalOcc0 = 0.00;
				double rectify0 = 0.00;
				
				if(!"".equals(table1.trim())){
					DebitTotalOcc0 = rs.getDouble("DebitTotalOcc0");
					CreditTotalOcc0 = rs.getDouble("CreditTotalOcc0");
					rectify0 = rs.getDouble("rectify0");
				}
				
				double sumNumber = 0.00;
				
				if("账表不符".equals(res)){
					if("1".equals(fx)){
						return CHF.showNull(String.valueOf(DebitTotalOcc6));
					}else if("-1".equals(fx)){
						return CHF.showNull(String.valueOf(CreditTotalOcc6));
					}else{
						return CHF.showNull(String.valueOf(rectify6));
					}
				}else if("期初调整".equals(res)){
					if("1".equals(fx)){
						return CHF.showNull(String.valueOf(DebitTotalOcc4));
					}else if("-1".equals(fx)){
						return CHF.showNull(String.valueOf(CreditTotalOcc4));
					}else{
						return CHF.showNull(String.valueOf(rectify4));
					}
				}else if("期初重分类".equals(res)){
					if("1".equals(fx)){
						return CHF.showNull(String.valueOf(DebitTotalOcc5));
					}else if("-1".equals(fx)){
						return CHF.showNull(String.valueOf(CreditTotalOcc5));
					}else{
						return CHF.showNull(String.valueOf(rectify5));
					}
				}else if("期初不符未调".equals(res)){
					if("1".equals(fx)){
						return CHF.showNull(String.valueOf(DebitTotalOcc0));
					}else if("-1".equals(fx)){
						return CHF.showNull(String.valueOf(CreditTotalOcc0));
					}else{
						return CHF.showNull(String.valueOf(rectify0));
					}
				}else if("期末调整".equals(res)){
					if("1".equals(fx)){
						return CHF.showNull(String.valueOf(DebitTotalOcc1));
					}else if("-1".equals(fx)){
						return CHF.showNull(String.valueOf(CreditTotalOcc1));
					}else{
						return CHF.showNull(String.valueOf(rectify1));
					}
				}else if("期末重分类".equals(res)){
					if("1".equals(fx)){
						return CHF.showNull(String.valueOf(DebitTotalOcc2));
					}else if("-1".equals(fx)){
						return CHF.showNull(String.valueOf(CreditTotalOcc2));
					}else{
						return CHF.showNull(String.valueOf(rectify2));
					}
				}else if("期末不符未调".equals(res)){
					if("1".equals(fx)){
						return CHF.showNull(String.valueOf(DebitTotalOcc3));
					}else if("-1".equals(fx)){
						return CHF.showNull(String.valueOf(CreditTotalOcc3));
					}else{
						return CHF.showNull(String.valueOf(rectify3));
					}
					
				}else if("期初总调整".equals(res)){
					if("1".equals(fx)){
						return CHF.showNull(String.valueOf(qcrectify1));
					}else if("-1".equals(fx)){
						return CHF.showNull(String.valueOf(qcrectify2));
					}else{
						return CHF.showNull(String.valueOf(qcrectify));
					}
					
				}else if("期末总调整".equals(res)){
					if("1".equals(fx)){
						return CHF.showNull(String.valueOf(qmrectify1));
					}else if("-1".equals(fx)){
						return CHF.showNull(String.valueOf(qmrectify2));
					}else{
						return CHF.showNull(String.valueOf(qmrectify));
					}
					
				}else if("期初审定".equals(res)){
					if("1".equals(fx)){
						sumNumber = DebitRemain + DebitTotalOcc6 + DebitTotalOcc4 + DebitTotalOcc5;
						return  CHF.showNull(String.valueOf(sumNumber));
					}else if("-1".equals(fx)){
						sumNumber = CreditRemain + CreditTotalOcc6 + CreditTotalOcc4 + CreditTotalOcc5;
						return  CHF.showNull(String.valueOf(sumNumber));
					}else{
						sumNumber = initbalance + rectify4 + rectify5 + rectify6;
						return  CHF.showNull(String.valueOf(sumNumber));
					}
					
				}
				
				
				if("否".equals(svalue)){ //期初调整不影响期末
					
					if("期末审定".equals(res)){
						
						
						if("1".equals(fx)){
							sumNumber = DebitBalance + DebitTotalOcc1 + DebitTotalOcc2;
							return  CHF.showNull(String.valueOf(sumNumber));
						}else if("-1".equals(fx)){
							sumNumber = CreditBalance + CreditTotalOcc1 + CreditTotalOcc2;
							return  CHF.showNull(String.valueOf(sumNumber));
						}else{
							sumNumber = Balance + rectify1 + rectify2 ;
							return  CHF.showNull(String.valueOf(sumNumber));
						}
						
					}else if("期末未审".equals(res)){
						
						if("1".equals(fx)){
							sumNumber = DebitBalance ;
							return  CHF.showNull(String.valueOf(sumNumber));
						}else if("-1".equals(fx)){
							sumNumber = CreditBalance ;
							return  CHF.showNull(String.valueOf(sumNumber));
						}else{
							sumNumber = Balance;
							return  CHF.showNull(String.valueOf(sumNumber));
						}

					}
					
				}else{		//期初调整影响期末
					
					if("期末审定".equals(res)){
						
						if("1".equals(fx)){
							sumNumber = DebitBalance + DebitTotalOcc1 + DebitTotalOcc2 + DebitTotalOcc4 + DebitTotalOcc5 + DebitTotalOcc6;
							return  CHF.showNull(String.valueOf(sumNumber));
						}else if("-1".equals(fx)){
							sumNumber = CreditBalance + CreditTotalOcc1 + CreditTotalOcc2 + CreditTotalOcc4 + CreditTotalOcc5 + CreditTotalOcc6;
							return  CHF.showNull(String.valueOf(sumNumber));
						}else{
							sumNumber = Balance + rectify1 + rectify2 + rectify4 + rectify5 + rectify6;
							return  CHF.showNull(String.valueOf(sumNumber));
						}
						
					}else if("期末未审".equals(res)){
						
						if("1".equals(fx)){
							sumNumber = DebitBalance + DebitTotalOcc4 + DebitTotalOcc5 + DebitTotalOcc6;
							return  CHF.showNull(String.valueOf(sumNumber));
						}else if("-1".equals(fx)){
							sumNumber = CreditBalance + CreditTotalOcc4 + CreditTotalOcc5 + CreditTotalOcc6;
							return  CHF.showNull(String.valueOf(sumNumber));
						}else{
							sumNumber = Balance + rectify4 + rectify5 + rectify6;
							return  CHF.showNull(String.valueOf(sumNumber));
						}

						
					}
					
				}
				
				
			}
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 帐上的项目取数，用z_manuaccount表
	 * @param subname
	 * @param res
	 * @param fx
	 * @param bz
	 * @return
	 * @throws Exception
	 */
	public String getProject(String subname,String res,String fx,String bz) throws Exception{
		String result = "";
		String sql = "";		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			/**
			 *科目对照  
			 */
			String skm = changeSubjectName(subname);
			if(!"".equals(skm)){
				subname = skm;
			}
//			String res = CHF.showNull(request.getParameter("res"));
//			res = new String(res.);
//			if ("".equals(res)) {
//				throw new  Exception("结果返回参数不能为空!");
//			}
			
//			String fx = CHF.showNull(request.getParameter("fx"));
			/**
			 * 兼容外币
			 */
//			String sTab = "";
//			String bz = CHF.showNull(request.getParameter("bz"));
//			String table1 = "";
//			String table2 = "";
//			bz = new String(bz.);
//			if (bz.equals("0") || bz.equals("") || bz.equals("本位币")) {
//				sTab = " c_Account a right join z_accountrectify b  on b.projectid='"+this.proid+"' and a.subjectid=b.subjectid";
//				bz = "0";
//				table1 = "sum(ifnull(DebitTotalOcc6,0)) DebitTotalOcc6,sum(ifnull(CreditTotalOcc6,0)) CreditTotalOcc6,sum(if(direction2=0,ifnull((DebitTotalOcc6-CreditTotalOcc6),0),ifnull(direction2*(DebitTotalOcc6-CreditTotalOcc6),0))) rectify6,";
//				table2 = "+ ifnull((DebitTotalOcc6-CreditTotalOcc6),0) "; 
//			} 
//			else {
//				sTab = " c_AccountAll a right join z_accountallrectify b  on b.projectid='"+this.proid+"' and a.subjectid=b.subjectid  and a.accsign=1 and a.DataName=b.DataName ";
			
//			}
			
//			String AuditTimeBegin = "";
//			String AuditTimeEnd = "";
			
			
//			sql = "select * from z_project where projectid='"+this.proid+"'";
//			ps = conn.prepareStatement(sql);
//			rs = ps.executeQuery();
//			if(rs.next()){
//				AuditTimeBegin = rs.getString("AuditTimeBegin");
//				AuditTimeEnd = rs.getString("AuditTimeEnd");
//			}
			
			sql = "select sum(DebitRemain) DebitRemain,sum((-1)*CreditRemain) CreditRemain,\n" +
				" sum(if(direction2=0,initbalance,direction2*initbalance)) remain,\n" +
				" sum(DebitTotalOcc) DebitOcc,sum(CreditTotalOcc) CreditOcc,sum(if(direction2=0,(DebitTotalOcc-CreditTotalOcc),direction2*(DebitTotalOcc - CreditTotalOcc))) Occ,\n" +
				" sum(DebitBalance) DebitBalance,sum((-1)*CreditBalance) CreditBalance,\n" +
				" sum(if(direction2=0,Balance,direction2*Balance)) Balance, \n" +
				
				" sum(ifnull(DebitTotalOcc6,0)) DebitTotalOcc6,sum(ifnull(CreditTotalOcc6,0)) CreditTotalOcc6,sum(if(direction2=0,ifnull((DebitTotalOcc6-CreditTotalOcc6),0),ifnull(direction2*(DebitTotalOcc6-CreditTotalOcc6),0))) rectify6,\n" + 
				" sum(ifnull(DebitTotalOcc4,0)) DebitTotalOcc4,sum(ifnull(CreditTotalOcc4,0)) CreditTotalOcc4,sum(if(direction2=0,ifnull((DebitTotalOcc4-CreditTotalOcc4),0),ifnull(direction2*(DebitTotalOcc4-CreditTotalOcc4),0))) rectify4,\n" +
				" sum(ifnull(DebitTotalOcc5,0)) DebitTotalOcc5,sum(ifnull(CreditTotalOcc5,0)) CreditTotalOcc5,sum(if(direction2=0,ifnull((DebitTotalOcc5-CreditTotalOcc5),0),ifnull(direction2*(DebitTotalOcc5-CreditTotalOcc5),0))) rectify5,\n" +
				" sum(ifnull(DebitTotalOcc0,0)) DebitTotalOcc0,sum(ifnull(CreditTotalOcc0,0)) CreditTotalOcc0,sum(if(direction2=0,ifnull((DebitTotalOcc0-CreditTotalOcc0),0),ifnull(direction2*(DebitTotalOcc0-CreditTotalOcc0),0))) rectify0,\n" +
				
				" sum(ifnull(DebitTotalOcc1,0)) DebitTotalOcc1,sum(ifnull(CreditTotalOcc1,0)) CreditTotalOcc1,sum(if(direction2=0,ifnull((DebitTotalOcc1-CreditTotalOcc1),0),ifnull(direction2*(DebitTotalOcc1-CreditTotalOcc1),0))) rectify1,\n" +
				" sum(ifnull(DebitTotalOcc2,0)) DebitTotalOcc2,sum(ifnull(CreditTotalOcc2,0)) CreditTotalOcc2,sum(if(direction2=0,ifnull((DebitTotalOcc2-CreditTotalOcc2),0),ifnull(direction2*(DebitTotalOcc2-CreditTotalOcc2),0))) rectify2,\n" +
				" sum(ifnull(DebitTotalOcc3,0)) DebitTotalOcc3,sum(ifnull(CreditTotalOcc3,0)) CreditTotalOcc3,sum(if(direction2=0,ifnull((DebitTotalOcc3-CreditTotalOcc3),0),ifnull(direction2*(DebitTotalOcc3-CreditTotalOcc3),0))) rectify3,\n" +
				
				" sum(if(direction2=0,ifnull((DebitTotalOcc6-CreditTotalOcc6),0) + ifnull((DebitTotalOcc4-CreditTotalOcc4),0) + ifnull((DebitTotalOcc5-CreditTotalOcc5),0),ifnull(direction2*(DebitTotalOcc6-CreditTotalOcc6),0) + ifnull(direction2*(DebitTotalOcc4-CreditTotalOcc4),0) + ifnull(direction2*(DebitTotalOcc5-CreditTotalOcc5),0) )) qcrectify,\n" +
				" sum(ifnull(DebitTotalOcc6,0) + ifnull(DebitTotalOcc4,0) + ifnull(DebitTotalOcc5,0)) qcrectify1,\n" +
				" sum(ifnull(CreditTotalOcc6,0) + ifnull(CreditTotalOcc4,0) + ifnull(CreditTotalOcc5,0)) qcrectify2, \n " +

				" sum(if(direction2=0,ifnull((DebitTotalOcc1-CreditTotalOcc1),0) + ifnull((DebitTotalOcc2-CreditTotalOcc2),0),ifnull(direction2*(DebitTotalOcc1-CreditTotalOcc1),0) + ifnull(direction2*(DebitTotalOcc2-CreditTotalOcc2),0) )) qmrectify, \n"+
				" sum(ifnull(DebitTotalOcc1,0) + ifnull(DebitTotalOcc2,0)) qmrectify1, \n" +
				" sum(ifnull(CreditTotalOcc1,0) + ifnull(CreditTotalOcc2,0)) qmrectify2, \n" +
				
				" sum(if(direction2=0,initbalance + ifnull((DebitTotalOcc4-CreditTotalOcc4),0) + ifnull((DebitTotalOcc5-CreditTotalOcc5),0) + ifnull((DebitTotalOcc6-CreditTotalOcc6),0)  ,direction2 * (initbalance + ifnull((DebitTotalOcc4-CreditTotalOcc4),0) + ifnull((DebitTotalOcc5-CreditTotalOcc5),0) + ifnull((DebitTotalOcc6-CreditTotalOcc6),0) ))) examine1,\n" +
				" sum(debitremain + ifnull(DebitTotalOcc6,0) + ifnull(DebitTotalOcc4,0) + ifnull(DebitTotalOcc5,0)) examine11, \n" +
				" sum((-1)*creditremain + ifnull(CreditTotalOcc6,0) + ifnull(CreditTotalOcc4,0) + ifnull(CreditTotalOcc5,0)) examine12,\n" +
				
				" sum(if(direction2=0,ifnull((DebitTotalOcc4-CreditTotalOcc4),0) + ifnull((DebitTotalOcc5-CreditTotalOcc5),0) + ifnull((DebitTotalOcc6-CreditTotalOcc6),0)  + Balance + ifnull((DebitTotalOcc1-CreditTotalOcc1),0) + ifnull((DebitTotalOcc2-CreditTotalOcc2),0) ,direction2 * ( ifnull((DebitTotalOcc4-CreditTotalOcc4),0) + ifnull((DebitTotalOcc5-CreditTotalOcc5),0) + ifnull((DebitTotalOcc6-CreditTotalOcc6),0)  + Balance + ifnull((DebitTotalOcc1-CreditTotalOcc1),0) + ifnull((DebitTotalOcc2-CreditTotalOcc2),0)))) examine2, \n" +
				" sum(ifnull(DebitTotalOcc6,0) + ifnull(DebitTotalOcc4,0) + ifnull(DebitTotalOcc5,0) + DebitBalance + ifnull(DebitTotalOcc1,0) + ifnull(DebitTotalOcc2,0)) examine21, \n" +
				" sum(ifnull(CreditTotalOcc6,0) + ifnull(CreditTotalOcc4,0) + ifnull(CreditTotalOcc5,0)  + (-1)*CreditBalance + ifnull(CreditTotalOcc1,0) + ifnull(CreditTotalOcc2,0)) examine22, \n" +
				
				" sum(if(direction2=0,ifnull((DebitTotalOcc4-CreditTotalOcc4),0) + ifnull((DebitTotalOcc5-CreditTotalOcc5),0) + ifnull((DebitTotalOcc6-CreditTotalOcc6),0)  + Balance  ,direction2 * ( ifnull((DebitTotalOcc4-CreditTotalOcc4),0) + ifnull((DebitTotalOcc5-CreditTotalOcc5),0) + ifnull((DebitTotalOcc6-CreditTotalOcc6),0)  + Balance ))) examine3,  \n" +
				" sum(ifnull(DebitTotalOcc6,0) + ifnull(DebitTotalOcc4,0) + ifnull(DebitTotalOcc5,0)  + DebitBalance  ) examine31,  \n" +
				" sum(ifnull(CreditTotalOcc6,0) + ifnull(CreditTotalOcc4,0) + ifnull(CreditTotalOcc5,0)  + (-1)*CreditBalance  ) examine32  \n" +
				
				" from z_manuaccount where  assitemid = '' and projectID ='"+proid+"' and DataName='"+bz+"'  " ;
				
//				sql += " group by subjectid"; 
			
//				" from (" +
//				" 	select direction2," +
//				"	sum(if(concat(SubYearMonth,'-',LPAD(SubMonth,2,'0'))=substring('"+AuditTimeBegin+"',1,7),DebitRemain,0)) DebitRemain," +
//				"	sum(if(concat(SubYearMonth,'-',LPAD(SubMonth,2,'0'))=substring('"+AuditTimeBegin+"',1,7),(-1)*CreditRemain,0)) CreditRemain," +
//				"	sum(if(concat(SubYearMonth,'-',LPAD(SubMonth,2,'0'))=substring('"+AuditTimeBegin+"',1,7),(DebitRemain+CreditRemain),0)) remain," +
//				"	sum(DebitOcc) DebitOcc, sum(CreditOcc) CreditOcc, (sum(DebitOcc) - sum(CreditOcc)) Occ," +
//				"	sum(if(concat(SubYearMonth,'-',LPAD(SubMonth,2,'0'))=substring('"+AuditTimeEnd+"',1,7),DebitBalance,0)) DebitBalance," +
//				"	sum(if(concat(SubYearMonth,'-',LPAD(SubMonth,2,'0'))=substring('"+AuditTimeEnd+"',1,7),(-1)*CreditBalance,0)) CreditBalance," +
//				"	sum(if(concat(SubYearMonth,'-',LPAD(SubMonth,2,'0'))=substring('"+AuditTimeEnd+"',1,7),Balance,0)) Balance," +
//				"	b.* " +
//				"	from "+sTab+" where substring(a.AccPackageID,1,6) = '"+this.pkgid.substring(0,6)+"'" +
//				"	and concat(SubYearMonth,'-',LPAD(SubMonth,2,'0')) >= substring('"+AuditTimeBegin+"',1,7) " +
//				"	and concat(SubYearMonth,'-',LPAD(SubMonth,2,'0')) <= substring('"+AuditTimeEnd+"',1,7)" +
//				"	and DataName='"+bz+"'" +
//				"	group by b.subjectid" +
//				") a where 1=1 ";
//			
			String stropt = SRS.getTextKeyAll(subname, proid);
			String [] ss = stropt.split("\\|");
			String str = ss.length<=1?subname+"','":ss[1].replaceAll("`","','");
			subname = "'"+str.substring(0,str.length()-2);			
			sql +=" and subjectid in ("+subname+") ";
			
			
			//org.util.Debug.prtOut("getProject:=| "+sql);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			int i = 0;
			while (rs.next()) {
				i++;
				if (i > 1) {
					throw new  Exception("取项目数出错！");					
				}				
//				result = CHF.showNull(rs.getString(1));
				if("项目期初".equals(res)){
					if("1".equals(fx)){
						result = CHF.showNull(rs.getString("DebitRemain"));
					}else if("-1".equals(fx)){
						result = CHF.showNull(rs.getString("CreditRemain"));
					}else{
						result = CHF.showNull(rs.getString("remain"));
					}
				}else if("项目期末".equals(res)){
					if("1".equals(fx)){
						result = CHF.showNull(rs.getString("DebitBalance"));
					}else if("-1".equals(fx)){
						result = CHF.showNull(rs.getString("CreditBalance"));
					}else{
						result = CHF.showNull(rs.getString("Balance"));
					}
				}else if("项目发生".equals(res)){
					if("1".equals(fx)){
						result = CHF.showNull(rs.getString("DebitOcc"));
					}else if("-1".equals(fx)){
						result = CHF.showNull(rs.getString("CreditOcc"));
					}else{
						result = CHF.showNull(rs.getString("Occ"));
					}
				}
				
				else if("账表不符".equals(res)){
					if("1".equals(fx)){
						result = CHF.showNull(rs.getString("DebitTotalOcc6"));
					}else if("-1".equals(fx)){
						result = CHF.showNull(rs.getString("CreditTotalOcc6"));
					}else{
						result = CHF.showNull(rs.getString("rectify6"));
					}
				}else if("期初调整".equals(res)){
					if("1".equals(fx)){
						result = CHF.showNull(rs.getString("DebitTotalOcc4"));
					}else if("-1".equals(fx)){
						result = CHF.showNull(rs.getString("CreditTotalOcc4"));
					}else{
						result = CHF.showNull(rs.getString("rectify4"));
					}
				}else if("期初重分类".equals(res)){
					if("1".equals(fx)){
						result = CHF.showNull(rs.getString("DebitTotalOcc5"));
					}else if("-1".equals(fx)){
						result = CHF.showNull(rs.getString("CreditTotalOcc5"));
					}else{
						result = CHF.showNull(rs.getString("rectify5"));
					}
				}else if("期初不符未调".equals(res)){
					if("1".equals(fx)){
						result = CHF.showNull(rs.getString("DebitTotalOcc0"));
					}else if("-1".equals(fx)){
						result = CHF.showNull(rs.getString("CreditTotalOcc0"));
					}else{
						result = CHF.showNull(rs.getString("rectify0"));
					}
				}else if("期末调整".equals(res)){
					if("1".equals(fx)){
						result = CHF.showNull(rs.getString("DebitTotalOcc1"));
					}else if("-1".equals(fx)){
						result = CHF.showNull(rs.getString("CreditTotalOcc1"));
					}else{
						result = CHF.showNull(rs.getString("rectify1"));
					}
				}else if("期末重分类".equals(res)){
					if("1".equals(fx)){
						result = CHF.showNull(rs.getString("DebitTotalOcc2"));
					}else if("-1".equals(fx)){
						result = CHF.showNull(rs.getString("CreditTotalOcc2"));
					}else{
						result = CHF.showNull(rs.getString("rectify2"));
					}
				}else if("期末不符未调".equals(res)){
					if("1".equals(fx)){
						result = CHF.showNull(rs.getString("DebitTotalOcc3"));
					}else if("-1".equals(fx)){
						result = CHF.showNull(rs.getString("CreditTotalOcc3"));
					}else{
						result = CHF.showNull(rs.getString("rectify3"));
					}
					
				}else if("期初审定".equals(res)){
					if("1".equals(fx)){
						result = CHF.showNull(rs.getString("examine11"));
					}else if("-1".equals(fx)){
						result = CHF.showNull(rs.getString("examine12"));
					}else{
						result = CHF.showNull(rs.getString("examine1"));
					}
					
				}else if("期末审定".equals(res)){
					if("1".equals(fx)){
						result = CHF.showNull(rs.getString("examine21"));
					}else if("-1".equals(fx)){
						result = CHF.showNull(rs.getString("examine22"));
					}else{
						result = CHF.showNull(rs.getString("examine2"));
					}
					
				}
				
				else if("期初总调整".equals(res)){
					if("1".equals(fx)){
						result = CHF.showNull(rs.getString("qcrectify1"));
					}else if("-1".equals(fx)){
						result = CHF.showNull(rs.getString("qcrectify2"));
					}else{
						result = CHF.showNull(rs.getString("qcrectify"));
					}
					
				}else if("期末总调整".equals(res)){
					if("1".equals(fx)){
						result = CHF.showNull(rs.getString("qmrectify1"));
					}else if("-1".equals(fx)){
						result = CHF.showNull(rs.getString("qmrectify2"));
					}else{
						result = CHF.showNull(rs.getString("qmrectify"));
					}
					
				}else if("期末未审".equals(res)){
					if("1".equals(fx)){
						result = CHF.showNull(rs.getString("examine31"));
					}else if("-1".equals(fx)){
						result = CHF.showNull(rs.getString("examine32"));
					}else{
						result = CHF.showNull(rs.getString("examine3"));
					}
					
				}
				
			}
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
		
	}
	
	/**
	 * 根据外币名称取得汇率值
	 * @return
	 * @throws Exception
	 */
	private String getExchangerate() throws Exception{
		String result = "";
		String sql = "";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			
			String currname = CHF.showNull(request.getParameter("currname"));//外币名称
//			dw = new String(dw.);
			if (currname.equals("")) {
				return "外币名称不能为空!";
			}
			
			sql = "select exchangerate from z_exchangerate where projectid="+proid+" and currname='"+currname+"' ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				
					result = CHF.showNull(rs.getString(1)); //汇率
				
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "1";
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
		
	}

	 
    public String changeSubjectName(String subjectName)throws Exception{
    	Statement st = null;
        ResultSet rs = null;
        String sql = "";
        try {
        	ASFuntion CHF = new ASFuntion();
        	subjectName = CHF.replaceStr(subjectName, " ", "");		
    		
        	st = conn.createStatement();
        	
        	String subsql = " and projectid='"+proid+"' and b.DepartID=a.customerid";
        	if(proid==null){
        		subsql = " and DepartID='"+pkgid.substring(0,6)+"' and b.DepartID=a.customerid";
        	}
        	sql = "select * from z_project a,k_customer b where 1=1"+subsql;
			
			rs = st.executeQuery(sql);
			
			String dpID =""; 
			String VocationID = "";
			if(rs.next()){
				dpID = rs.getString("customerid");
				VocationID = rs.getString("VocationID");
			}
			
			sql = "select 1 from k_standsubject where VocationID='"+VocationID+"' and subjectfullname='"+subjectName+"'";
			System.out.println("sql2:"+sql);
			rs = st.executeQuery(sql);
			if(rs.next()){
				return subjectName;
			}
			
        	sql = "select a.* from k_standsubject a ,(" +
        	" 	select a.subjectname,replace(CONCAT(a.subjectname,'                                     '),b.key1,b.key2) exSubjectName" +
			" 	from (    " +
			" 		select '"+subjectName+"' as subjectName " +
			" 	) a,k_key b" +
			" 	where  b.departid in ('0','"+dpID+"') " +
			"	and a.subjectname like concat('%',b.key1,'%') " +
			
			" 	union" +
			
			"	select distinct a.subjectname,TRIM(replace(replace(CONCAT(a.subjectname,'                                     '),b.key1,b.key2),c.key1,c.key2))  exSubjectName" +
			" 	from (    " +
			" 		select '"+subjectName+"' as subjectName " +
			"	) a,k_key b,k_key c" +
			"	where  b.departid in ('0','"+dpID+"') " +
			" 	and  c.departid in ('0','"+dpID+"') " +
			"	and a.subjectname like concat('%',b.key1,'%')  " +
			"	and a.subjectname like concat('%',c.key1,'%') " +
			
			"	union " +
			
			"	select distinct a.subjectname,TRIM(replace(replace(replace(CONCAT(a.subjectname,'                                     '),b.key1,b.key2),c.key1,c.key2),d.key1,d.key2))  exSubjectName" +
			" 	from (    " + 
			" 		select '"+subjectName+"' as subjectName " +
			"	) a,k_key b,k_key c,k_key d  " +
			"	where  b.departid in ('0','"+dpID+"') " + 
			" 	and  c.departid in ('0','"+dpID+"') " +
			" 	and  d.departid in ('0','"+dpID+"') " +
			"	and a.subjectname like concat('%',b.key1,'%')  " +
			"	and a.subjectname like concat('%',c.key1,'%')  " +
			"	and a.subjectname like concat('%',d.key1,'%') " +
			" ) b where VocationID="+VocationID+" and  a.subjectname = b.exSubjectName";
        	
        	rs = st.executeQuery(sql);
        	if(rs.next()){
        		return rs.getString("subjectname");
        	}else{
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
 * 取帐套数
 * 根据类型和月份,取得各类型的余额.月份为空则取年末月的.
 * lx:类型 yefen:月份
 * 如:=取帐套数("资产类","4")
 * @return
 * @throws Exception
 */    
	public String  getAccpackage() throws Exception {
		DbUtil.checkConn(conn);
		String result = "";
		String sqlResult = "";	
		String sql = "";
		String customerID = "";
		String year = "";
		PreparedStatement ps = null;
		ResultSet rs = null;
		int i = 0;
		try {
			String type = CHF.showNull(request.getParameter("lx"));
			String month =  CHF.showNull(request.getParameter("yefen"));
			//org.util.Debug.prtOut("yefen result:" + month);
			if (type.equals("") || type == null || "null".equals(type)) {
				return "类型不能为空!";
			}
			//根据指写的类型在c_subjecttype表中找出对应的科目编号
			sql = "select distinct subid from c_subjecttype where accpackageid = "+pkgid+" and ctype = '"+type+"'";
			i = new DbUtil(conn).queryForInt(sql);
			customerID = pkgid.substring(0, 6);
			year = pkgid.substring(6);
			if(month!=null&&!"".equals(month)){
		         sqlResult = "select Sum(balance) as Remain ";
		         sqlResult = sqlResult + " from c_account a,c_accpkgsubject b,c_accpackage c ";
		         sqlResult = sqlResult + " where a.SubjectID like '" + i +"%' ";

		         sqlResult = sqlResult + " and a.SubMonth=" + month;

		         sqlResult = sqlResult + " and b.level0=1 and c.CustomerID=" + customerID;
		         sqlResult = sqlResult + " and c.AccpackageYear=" + year;
		         sqlResult = sqlResult + " and a.SubjectID=b.SubjectID and a.AccPackageID=b.AccPackageID and b.AccPackageID=c.AccPackageID ";
		       }else{
		         sqlResult = "select sum(a.balance) as Remain ";
		         sqlResult = sqlResult + " from c_account a,c_accpkgsubject b,c_accpackage c ,c_account d";
		         sqlResult = sqlResult + " where a.SubjectID like '" + i + "%' ";

		         sqlResult = sqlResult + " and a.SubMonth=12";
		         sqlResult = sqlResult + " and d.SubMonth=1";
		         sqlResult = sqlResult + " and b.level0=1 and c.CustomerID=" + customerID;
		         sqlResult = sqlResult + " and c.AccpackageYear=" + year;
		         sqlResult = sqlResult + " and a.subjectid=d.subjectid and a.accpackageid=d.accpackageid ";
		         sqlResult = sqlResult + " and a.SubjectID=b.SubjectID and a.AccPackageID=b.AccPackageID and b.AccPackageID=c.AccPackageID ";
		       }
			ps = conn.prepareStatement(sqlResult);
			rs = ps.executeQuery();
			//org.util.Debug.prtOut("sqlResult :" + sqlResult);
			if(rs.next()) {
				result = CHF.showNull(rs.getString(1));
			}
			
			if("".equals(result)){
				result="0";
			}
			//org.util.Debug.prtOut("getAccpackage result:" + result);
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
	}
/**
 * 取帐套数指导
 * 根据金额、类型、期初/期末返回对就的科目编号和科目名称.
 * je:金额 lx:类型(资产、负债等) qc:期初/期末(不填则为期初或期末)
 * 如:=取帐套数指导("68477.00","", "资产类")
 * @return
 * @throws Exception
 */	
	public String  getAccpackageGuide() throws Exception {
		DbUtil.checkConn(conn);
		String result = "";
		String sqlResult = "";	
		String sql = "";
		PreparedStatement ps = null;
		ResultSet rs = null;
		int i = 0;
		String chaxun = "";
		try {
			String je = CHF.showNull(request.getParameter("je"));//金额
			String lx =  CHF.showNull(request.getParameter("lx"));//类型:资产类、负债类等
			String qc =  CHF.showNull(request.getParameter("qc"));//期初、期末
			//把金额中的逗号去掉
			je.replaceAll(",","");
			if(qc.equals("期初")) {
				chaxun = " (creditRemain+debitremain="+je+") ";
			} else if(qc.equals("期末")) {
				chaxun = " balance = "+je+" ";
			} else {
				chaxun = " (creditRemain+debitremain="+je+" or balance = "+je+") ";
			}
			
			if (je.equals("") || je == null || "null".equals(je)) {
				return "金额不能为空!";
			}
			//从c_subjecttype表中获得指定类型的科目编号
			sql = "select distinct subid from c_subjecttype where accpackageid = "+pkgid+" and ctype = '"+lx+"'";
			i = new DbUtil(conn).queryForInt(sql);
			sqlResult = "select group_concat(subjectid,accname) \n"
					+" from ( \n"
					+" 	select  distinct subjectid,accname \n"
					+" 	from c_account \n"
					+" 	where  \n"
					+" 	accpackageid="+pkgid+" \n"
					+" 	and "+chaxun+" \n"
					+" 	and (substring(subjectid,1,1) in ('"+i+"')) \n"
					+" ) a";
    			ps = conn.prepareStatement(sqlResult);
			rs = ps.executeQuery();
			while (rs.next()) {
				result = CHF.showNull(rs.getString(1));
			}
			
			if("".equals(result)){
				result="";
			}
			//org.util.Debug.prtOut("getAccpackageGuide result:" + result);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
	}
	public static void main(String[] args) {
		String AuditTimeBegin = "   预付   账  款  ";
		ASFuntion CHF = new ASFuntion();
		AuditTimeBegin = CHF.replaceStr(AuditTimeBegin, " ", "");		
		//System.out.println("|"+AuditTimeBegin+"|");
	}
}
