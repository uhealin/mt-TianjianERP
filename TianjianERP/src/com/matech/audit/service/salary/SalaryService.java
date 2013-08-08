package com.matech.audit.service.salary;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.swing.text.html.HTMLDocument.HTMLReader.PreAction;

import org.apache.taglibs.standard.lang.jpath.expression.NowFunction;
import org.jbpm.test.Db;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.salary.model.Salary;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.exception.MatechException;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;
import com.matech.framework.pub.util.StringUtil;
import com.matech.framework.pub.util.UTILString;
import com.matech.framework.service.print.PrintSetup;

public class SalaryService {
	
	Connection conn = null;
	
	public SalaryService(Connection conn){
		this.conn = conn;
	}
	
	/**
	 * add
	 * @param salary
	 * @return
	 */
	public boolean add(Salary salary){
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "INSERT INTO k_salary \n"
		            +"( userId,rankId,nowYear,nowMonth,departmentId,pch,status) \n"
		            +"VALUES (?,?,?,?,?,?,?);";
		boolean result = false;
		
		try {
			int i = 1;
			ps = conn.prepareStatement(sql);
			ps.setString(i++, salary.getUserId());
			ps.setString(i++, salary.getRankId());
			ps.setString(i++, salary.getNowYear());
			ps.setString(i++, salary.getNowMonth());
			ps.setString(i++, salary.getDepartmentId());
			ps.setString(i++, salary.getPch());
			ps.setString(i++, salary.getStatus());
			ps.execute();
			
			result = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		return result;
		
	}
	public Map wageAllMap(){
		PreparedStatement ps =null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		Map<String, String> wageAll = new HashMap<String, String>();
		String sql = " SELECT wagesetup ,GROUP_CONCAT(rankid) as rankids FROM ( \n"
			+  " SELECT rankid,GROUP_CONCAT(wagesname ORDER BY orderid) AS wagesetup \n"
			+  " FROM `k_rankwages`   WHERE rankid is not null \n"
			+  " GROUP BY rankid )t GROUP BY wagesetup"; 
		try{
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				String wageNames = rs.getString("wagesetup");
				String ranks = rs.getString("rankids");
				String sql1 = "select userId from k_salary where rankId in ("+ranks+")";
				ps2 = conn.prepareStatement(sql1);
				rs2 = ps2.executeQuery();
				if(rs2.next()){
					wageAll.put(ranks, wageNames);
				}
				DbUtil.close(rs2);
				DbUtil.close(ps2);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return wageAll;
	}
	public List wageAllList(){
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		String sql="";
		List<Map<String,String>> wageList = new ArrayList<Map<String,String>>();
		String sql2 = " SELECT wagesetup ,GROUP_CONCAT(rankid) as rankids FROM ( \n"
			+  " SELECT rankid,GROUP_CONCAT(wagesname ORDER BY orderid) AS wagesetup \n"
			+  " FROM `k_rankwages`  WHERE rankid is not null \n"
			+  " GROUP BY rankid )t GROUP BY wagesetup"; 
		try{
		
		ps = conn.prepareStatement(sql2);
		rs = ps.executeQuery();
		int j=1;
		while(rs.next()){
			sql = "select userId from k_salary where rankId in ( "+rs.getString("rankids")+")";
			ps2 = conn.prepareStatement(sql);
			rs2 = ps2.executeQuery();
			if(rs2.next()){
				Map<String, String> wageMap = new HashMap<String, String>();
				wageMap.put("title","第"+j+"职级");
				wageMap.put("id", "listSaraly"+j);
				wageMap.put("contentEl", "listSaraly"+j);
				j++;
		        wageList.add(wageMap);
			}  
			DbUtil.close(rs2);
			DbUtil.close(ps2);
		}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return wageList;
	}
	public Map  detailWageMap(String pch){
		PreparedStatement ps = null;
		ResultSet rs = null;
		String rankIds ="";
        Map rankWages = new HashMap();
     
		try{
			String sql1 = "SELECT GROUP_CONCAT(DISTINCT rankid) FROM k_salary WHERE pch='"+pch+"'";
			ps = conn.prepareStatement(sql1);
			rs = ps.executeQuery();
			if(rs.next()){
				rankIds = rs.getString(1);
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			String sql2 = " SELECT wagesetup ,GROUP_CONCAT(rankid) as rankids FROM ( \n"
	    			+  " SELECT rankid,GROUP_CONCAT(wagesname ORDER BY orderid) AS wagesetup \n"
	    			+  " FROM `k_rankwages` WHERE rankid IN ("+rankIds+") \n"
	    			+  " GROUP BY rankid )t GROUP BY wagesetup"; 
			ps = conn.prepareStatement(sql2);
			rs = ps.executeQuery();
			int j=1;
			while(rs.next()){
				String wages = rs.getString("wagesetup");
				String rankId = rs.getString("rankids");
				rankWages.put(rankId, wages);
		       
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
			DbUtil.close(rs);
		}
		return rankWages;
	}
	public List detailWageList(String pch){
		PreparedStatement ps = null;
		ResultSet rs = null;
		String rankIds ="";
        List<Map<String,String>> wageList = new ArrayList<Map<String,String>>();
		try{
			String sql1 = "SELECT GROUP_CONCAT(DISTINCT rankid) FROM k_salary WHERE pch='"+pch+"'";
			ps = conn.prepareStatement(sql1);
			rs = ps.executeQuery();
			if(rs.next()){
				rankIds = rs.getString(1);
				}
			DbUtil.close(rs);
			DbUtil.close(ps);
			String sql2 = " SELECT wagesetup ,GROUP_CONCAT(rankid) as rankids FROM ( \n"
	    			+  " SELECT rankid,GROUP_CONCAT(wagesname ORDER BY orderid) AS wagesetup \n"
	    			+  " FROM `k_rankwages` WHERE rankid IN ("+rankIds+") \n"
	    			+  " GROUP BY rankid )t GROUP BY wagesetup"; 
			ps = conn.prepareStatement(sql2);
			rs = ps.executeQuery();
			int j=1;
			while(rs.next()){
				Map<String, String> wageMap = new HashMap<String, String>();
				wageMap.put("title","第"+j+"职级");
				wageMap.put("id", "listSaraly"+j);
				wageMap.put("contentEl", "listSaraly"+j);
				j++;
				//String wages = rs.getString("wagesetup");
				//String rankId = rs.getString("rankids");
		        //rankWages.put(rankId,wages);   
		        wageList.add(wageMap);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
			DbUtil.close(rs);
		}
		return wageList;
	}
	
	public String handlePre(String pch,String status){
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		String next ="";
		try{
			//获取下一进度
			String sql = "SELECT c.name,c.value \n"
				+" FROM k_dic c"
				+" where c.ctype='工资进度' and value=? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, status);
			rs = ps.executeQuery();
			if(rs.next()){
				next = rs.getString(1);
			}else{
				throw new Exception("工资发放无法定位:pch="+pch+",status="+status);
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			//更新进度
			sql="update k_salary set status=? where pch=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, next);
			ps.setString(2, pch);
			ps.execute();
			
			next="成功，工资提交至："+next;
		}catch(Exception e){
			e.printStackTrace();
			next="失败："+e.getMessage();
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return next;
	}
	
	public String handleNext(String pch){
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		String next ="";
		try{
			//获取下一进度
			String sql = "SELECT c.name,c.value \n"
				+" FROM k_dic a,( \n"
				+" 		SELECT DISTINCT STATUS AS NAME \n"
				+" 		FROM k_salary a \n"
				+" 		WHERE pch=? \n"
				+" )b,k_dic c \n"
				+" WHERE a.ctype='工资进度' \n"
				+" AND a.name=b.name \n"
				+" AND c.ctype='工资进度' \n"
				+" AND c.value>a.value \n"
				+" ORDER BY VALUE \n";
			ps = conn.prepareStatement(sql);
			ps.setString(1, pch);
			rs = ps.executeQuery();
			if(rs.next()){
				next = rs.getString(1);
			}else{
				throw new Exception("工资发放无法定位下一步:pch="+pch);
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			//更新进度
			sql="update k_salary set status=? where pch=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, next);
			ps.setString(2, pch);
			ps.execute();
			
			next="成功，工资提交至："+next;
		}catch(Exception e){
			e.printStackTrace();
			next="失败："+e.getMessage();
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return next;
	}
	
	/**
	 * 得到文件目录
	 * @param pch
	 * @return
	 */
	public String getExeclDir() {
        String path = null;
        try {
            path = org.del.DelPublic.getWarPath();
        } catch (Exception e) {
            path = org.del.DelPublic.getClassRoot() + "../../";
        }
        if (path.substring(0, 1).equals("/")) {
            path = path.substring(1);
        }
        if (path.substring(path.length() - 1, path.length()).equals("/")) {
            path += "salary/salarydata";
        } else {
            path += "/salary/salarydata";
        }
        File file = new File(path);
        if(!file.exists()) {
        	file.mkdirs();
        }
        return path;
    }
	
	public  String ExploreExcel(String pch){
		PreparedStatement ps = null;
		PreparedStatement ps1 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		String filenames="";
		try {
			ASFuntion asf = new ASFuntion();
			
			PrintSetup printSetup = null;
			String sql1 = "",sql = "",title="",sql2 = "";
			/**
			 * 求已知职级
			 * 每一个职级的n1...等都有可能不一样
			 */
				
				String rankId ="",rankName = "";
				title="";
				sql2 = "";
				rankId ="0";
				rankName ="工资表";
				
				int ii = 1;
				sql1 = "SELECT * FROM k_salary WHERE pch=? and rankId=? limit 1";
				ps1 = conn.prepareStatement(sql1);
				ps1.setString(1, pch);
				ps1.setString(2, rankId);
				rs1 = ps1.executeQuery();
				ResultSetMetaData RSMD = rs1.getMetaData();
				if(rs1.next()){
					for (int i = 1; i <= RSMD.getColumnCount(); i++) {
						if(RSMD.getColumnLabel(i).toLowerCase().equals("n" + ii)){
							//n1....,v1....
							String n = asf.showNull(rs1.getString("n" + ii));
							if(!"".equals(n)){
								title += "`" + rs1.getString("n" + ii);
								sql2 +=  ",v" + ii ;
							}
							ii ++;
						}
					}
				}
				DbUtil.close(rs1);
				DbUtil.close(ps1);
			
				title = "项目`部门`姓名`年份`月份`备注" + title ; 
				sql = "SELECT a.pchname,c.departname,b.name,a.nowYear,a.nowMonth,memo" + sql2 + " " + 
				"	FROM k_salary a,k_user b,k_department c  " +
				"	WHERE a.userid = b.id AND b.departmentId = c.autoId AND pch = '"+pch+"' and rankid = '"+rankId+"' " +
				"	order by c.property,b.pccpa_seqno";
				
				printSetup = new PrintSetup(conn);
				printSetup.setCharColumn(new String[]{"1`2`3`4`5"});
		    	printSetup.setStrChineseTitles(new String[]{title});
		        printSetup.setStrQuerySqls(new String[]{sql});
		        
		        printSetup.setStrSheetName(rankName);
		        printSetup.setStrTitles(new String[]{""});
		        Thread.sleep(1);
		        filenames+=","+printSetup.getExcelFile();
			
		} catch (Exception e) {
			e.printStackTrace();
			e.printStackTrace();
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}    
		
		System.out.println("qwh:result="+filenames.substring(1));
		return "".equals(filenames) ? filenames : filenames.substring(1);
	}
	
	public  String ExploreExcel2(String pch){
		PreparedStatement ps = null;
		ResultSet rs = null;
		String rankIds ="";
		String filenames="";
		try{
			//导出有职级的人
			String sql1 = "SELECT GROUP_CONCAT(DISTINCT rankid) FROM k_salary WHERE pch=?";
			ps = conn.prepareStatement(sql1);
			ps.setString(1, pch);
			rs = ps.executeQuery();
			if(rs.next()){
				rankIds = rs.getString(1);
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			
			
			/*
		    String sql2 = " SELECT wagesetup ,GROUP_CONCAT(rankid) as rankids FROM ( \n"
		    			+  " SELECT rankid,GROUP_CONCAT(wagesname ORDER BY orderid) AS wagesetup \n"
		    			+  " FROM `k_rankwages` WHERE rankid IN ("+rankIds+") \n"
		    			+  " GROUP BY rankid )t GROUP BY wagesetup";
		    */
			String sql2 = "SELECT a.*,b.wagesetup \n"
							+" FROM ( \n"
							+" 	SELECT a.group as groupname,GROUP_CONCAT(a.autoid) AS rankids,MIN(autoid) AS rankid,a.basesalary \n"
							+" 	FROM k_rank a  \n"
							+" 	WHERE a.autoid IN ("+rankIds+") \n"
							+" 	GROUP BY a.group \n"
							+" )a,( \n"
							+" 	SELECT rankid,GROUP_CONCAT(wagesname ORDER BY orderid) AS wagesetup \n" 
							+" 	FROM `k_rankwages` WHERE rankid IN ("+rankIds+")  \n"
							+" 	GROUP BY rankid  \n"
							+" )b  \n"
							+" WHERE a.rankid=b.rankid \n"
							+" ORDER BY a.basesalary DESC";
		    ps = conn.prepareStatement(sql2);
		    PrintSetup printSetup = null;
		    rs = ps.executeQuery();
		    while(rs.next()){
		    	printSetup = new PrintSetup(conn);
		    	
		        String wages = rs.getString("wagesetup");
		        String title = "姓名,身份证号码,职级,部门,年份,月份,"+wages;
		        String[] wageslist = wages.split(",");
		        StringBuffer sb = new StringBuffer("SELECT b.`name`,identityCard,rank,c.departname,a.nowYear,a.nowMonth,");
		        for(int i = 1;i<=wageslist.length;i++){
		            if(i!=wageslist.length){
		            	sb.append("v"+i+",");
		            }else{
		            	sb.append("v"+i);
		            }	
		        }
		        title = title.replaceAll(",", "`");
		        
		        printSetup.setCharColumn(new String[]{"1`2`3`4`5`6"});
		    	printSetup.setStrChineseTitles(new String[]{title});
		        String salaryrank = rs.getString("rankIds");
		        sb.append(" FROM k_salary a,k_user b,k_department c  WHERE a.userid = b.id AND b.departmentId = c.autoId AND pch = '"+pch+"' and rankid in ("+salaryrank+")");
		        
		        System.out.println("sql3="+sb.toString());
		        
		        printSetup.setStrQuerySqls(new String[]{sb.toString()});
		        
		        
		        printSetup.setStrSheetName(rs.getString("groupname"));
		        printSetup.setStrTitles(new String[]{""});
		        Thread.sleep(1);
		        filenames+=","+printSetup.getExcelFile();
		        
		    	
		    	/*
		    	String wages = rs.getString("wagesetup");
		        String title = "姓名,身份证号码,职级,部门,id";
		        
		        title = title.replaceAll(",", "`");
		        
		        printSetup.setCharColumn(new String[]{"1`2`3`4`5"});
		    	printSetup.setStrChineseTitles(new String[]{title});
		    	
		        printSetup.setStrQuerySqls(new String[]{"select name,identityCard,rank,departmentid,loginid from k_user"});
		        printSetup.setStrSheetName("测试");
		        j++;
		        printSetup.setStrTitles(new String[]{"工资"+j});
		        Thread.sleep(1);
		        filenames+=","+printSetup.getExcelFile();
		        */
		    }
		    
		    
		    //导出没职级的人
		    sql1 = " SELECT b.autoid \n"
		    		+" FROM K_SALARY a,(SELECT autoid FROM k_rank ) b \n"
		    		+" WHERE a.rankid IS NULL and pch=? \n"
		    		+" ORDER BY 1";
			ps = conn.prepareStatement(sql1);
			ps.setString(1, pch);
			rs = ps.executeQuery();
			rankIds="";
			if(rs.next()){
				rankIds = rs.getString(1);
			
				DbUtil.close(rs);
				DbUtil.close(ps);
		    
				sql2 = " SELECT wagesetup ,GROUP_CONCAT(rankid) as rankids FROM ( \n"
			    			+  " SELECT rankid,GROUP_CONCAT(wagesname ORDER BY orderid) AS wagesetup \n"
			    			+  " FROM `k_rankwages` WHERE rankid IN ("+rankIds+") \n"
			    			+  " GROUP BY rankid )t GROUP BY wagesetup"; 
				ps = conn.prepareStatement(sql2);
				rs = ps.executeQuery();
			    while(rs.next()){
			    	printSetup = new PrintSetup(conn);
			    	
			        String wages = rs.getString("wagesetup");
			        String title = "姓名,身份证号码,职级,部门,年份,月份,"+wages;
			        String[] wageslist = wages.split(",");
			        StringBuffer sb = new StringBuffer("SELECT b.`name`,identityCard,rank,c.departname,a.nowYear,a.nowMonth,");
			        for(int i = 1;i<=wageslist.length;i++){
			            if(i!=wageslist.length){
			            	sb.append("v"+i+",");
			            }else{
			            	sb.append("v"+i);
			            }	
			        }
			        title = title.replaceAll(",", "`");
			        
			        printSetup.setCharColumn(new String[]{"1`2`3`4`5`6"});
			    	printSetup.setStrChineseTitles(new String[]{title});
			        sb.append(" FROM k_salary a,k_user b,k_department c  WHERE a.userid = b.id AND b.departmentId = c.autoId AND pch = '"+pch+"' and a.rankid is null order by b.station,b.loginid");
			        
			        System.out.println("qwh:空记录="+sb.toString());
			        
			        printSetup.setStrQuerySqls(new String[]{sb.toString()});
			        
			        printSetup.setStrSheetName("未定职级");
			        printSetup.setStrTitles(new String[]{""});
			        Thread.sleep(1);
			        filenames+=","+printSetup.getExcelFile();
			        
			    }
			}
		    
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}    
		
		System.out.println("qwh:result="+filenames.substring(1));
		return filenames.substring(1);
	}
	/**
	 * 初始化 工资
	 * @param userId
	 * @param pch
	 * @return
	 */
	public boolean init(String pch,String departmentid,String paraYear,String paraMonth){
		PreparedStatement ps1 = null;
		//初始化工资，之前没有记录
		boolean result = false;
		String sql = "";
		try {
			sql = "INSERT INTO k_salary \n"
	            +"( userId,rankId,nowYear,nowMonth,departmentId,pch,status,pchname) \n"
	            +" select a.id,b.autoId,'"+paraYear+"','"+paraMonth+"',a.departmentid,'"+pch+"','暂存','月度工资' \n" 
	            +" from k_user  a \n" 
	            +" left join k_rank b on a.rank = b.name \n"
	            +" where a.departmentId='"+departmentid+"' and a.emtype=001";
			ps1 = conn.prepareStatement(sql);
			ps1.execute();
		
			try {
				AddInit1(pch);
				result=true;
			} catch (Exception e) {
				
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps1);
		}
		return result;
		
	}
	
	/**
	 * 录入批次号
	 * @param userId
	 * @param pch
	 * @return
	 */
	public void initPch(String year,String month,String departmentId,String userIds,String pch){
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		PreparedStatement ps2 = null;
		boolean temp = false;
		String sql =" select userid,pch from k_salary" +
				    " WHERE 1=1 ";
		if(!"".equals(year)){
		sql +="  and nowYear="+year;
		}
		
		if(!"".equals(month)){
		sql +=" and nowMonth="+month;
		}
		
		if(!"".equals(departmentId)){
		sql +=" and departmentId="+departmentId;
		}
		
		if(!"".equals(userIds)){
		sql +=" and userId in ("+userIds +")";
		}
			
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				String pchDB = rs.getString("pch");
				String userid = rs.getString("userid");
				if (null == pchDB || pchDB.equals("")) {
					String sql2 = "update k_salary set pch ='"+pch+"' where userid = '"+userid+"' ";
					temp = true;
					try {
						ps = conn.prepareStatement(sql2);
						ps.execute();
					} catch (SQLException e) {
						e.printStackTrace();
					}finally{
						DbUtil.close(ps2);
					}
				}
			}
			if (temp) {
				updateSoftSalary(pch);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
			DbUtil.close(rs);
		}
		
	}

	/**
	 * 初始化 录入弹性工资页面
	 * @param departmentId
	 * @param pch
	 * @return
	 */
	public void initSoftSalary(String departmentId,int paraYear,int paraMonth){
		
		String sql = "";
		PreparedStatement ps = null;
		if (!departmentId.trim().equals("")) {   //有部门,只显示本部门和授权部门
			sql = "INSERT INTO k_salary \n"
	            +"( userId,rankId,nowYear,nowMonth,departmentId,status,n17,v17,n18,v18,n19,v19,n20,v20, \n"  
	            +"n21,v21,n22,v22,n23,v23,n24,v24,n25,v25, \n" 
	            +"n26,v26,n27,v27,n28,v28,n29,v29,n30,v30) \n"
	            +" select a.id,b.autoId,'"+paraYear+"','"+paraMonth+"',a.departmentid,'未录弹性工资','本月工时','0','本月外勤天数','0','绩效工资','0','其他补助','0', \n"
	            +"'缺勤减薪','0','其他减薪','0','收入总和','0','社保计提','0','社保调整','0', \n"
	            +"'计税收入','0','代扣个税','0','财务扣款','0','其他扣款','0','实发工资','0'  \n"
	            +"from k_user a " 
	            +"left join k_rank b on a.rank = b.name \n"
	            +"where a.id in \n"
	            +"(SELECT id FROM k_user WHERE departmentId = '"+departmentId+"' AND id not in ( \n"
                +"SELECT DISTINCT userId from k_salary \n"
                +"WHERE nowYear='"+paraYear+"' AND nowMonth='"+paraMonth+"' )) ";
		} else {                         //无部门,显示全部部门和授权部门
			sql = "INSERT INTO k_salary \n"
	            +"( userId,rankId,nowYear,nowMonth,departmentId,status,n17,v17,n18,v18,n19,v19,n20,v20), \n"
	            +"n21,v21,n22,v22,n23,v23,n24,v24,n25,v25, \n" 
	            +"n26,v26,n27,v27,n28,v28,n29,v29,n30,v30) \n"
	            +" select a.id,b.autoId,'"+paraYear+"','"+paraMonth+"',a.departmentid,'未录弹性工资','本月工时','0','本月外勤天数','0','绩效工资','0','其他补助','0', \n"
	            +"'缺勤减薪','0','其他减薪','0','收入总和','0','社保计提','0','社保调整','0', \n"
	            +"'计税收入','0','代扣个税','0','财务扣款','0','其他扣款','0','实发工资','0'  \n"
	            +"from k_user a "  
	            +"left join k_rank b on a.rank = b.name \n"
	            +"where a.id in \n"
	            +"(SELECT id FROM k_user WHERE id not in ( \n"
                +"SELECT DISTINCT userId from k_salary \n"
                +"WHERE nowYear='"+paraYear+"' AND nowMonth='"+paraMonth+"' )) ";
		}
		
		try {
			ps = conn.prepareStatement(sql);
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		
	}
	//检查改工资记录的状态
	public String checkUpdate(String pch){
		String sql ="select count(*) from k_salary where pch=? and status = '人事部门确认'";
		PreparedStatement ps = null;
		ResultSet rs = null;
		String result = "";
		try{
			ps = conn.prepareStatement(sql);
			ps.setString(1, pch);
			rs = ps.executeQuery();
			if(rs.next()){
				result = rs.getString(1);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return result;
	}
    
	/**
	 * 设置字段名称
	 * 对EXCEL操作时，有可能会删除一列
	 * 所以对于n1....等字段名称只能通过第一列来还原
	 */
	public void updateDataTxt(String pch,String areaid)throws MatechException{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "";
			
			DbUtil db = new DbUtil(conn);
//			sql = "select groupFlag,group_concat(wagesName) as wages,'' as resWages from k_rankwages group by groupFlag";
//			List fList = db.getList(sql);
			
			ASFuntion asf = new ASFuntion();
			List list = new ArrayList();
			int ii = 1;
			String nextAutoid = "";
			String sql1="select a.*,min(b.autoid) as nextAutoid from tt_k_salary  a,tt_k_salary b " +
			"	where trim(a.username)='姓名' and trim(b.username)='姓名'  " +
			"	and a.pch=?  " +
			"	and b.pch=?  " +
			"	and a.autoid < b.autoid " +
			"	group by a.autoid " +
			"	order by a.autoid ";
			ps = conn.prepareStatement(sql1);
			ps.setString(1, pch);
			ps.setString(2, pch);
			rs = ps.executeQuery();
			ResultSetMetaData RSMD = rs.getMetaData();
			while(rs.next()){
				sql = "";
				ii = 1;
				String autoid = rs.getString("autoid");
				nextAutoid = rs.getString("nextAutoid");
				
				for (int i = 1; i <= RSMD.getColumnCount(); i++) {
					if(RSMD.getColumnLabel(i).toLowerCase().equals("v" + ii)){
						//n1....,v1....
						String v = asf.showNull(rs.getString("v" + ii));
						if(!"".equals(v)){
							sql += ",n" + ii + "='"+v+"' ";
						}
						ii ++;
						
					}
				}
				if(!"".equals(sql)){
					list.add("update tt_k_salary set " + sql.substring(1) + " where pch = '"+pch+"' and autoid >'"+autoid+"' and autoid < '"+nextAutoid+"' ");
				}
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			//最大一条：1、有多个表页，2、只有一个表页
			if("".equals(nextAutoid)){ 
				//只有一个表页
				sql = "select a.* from tt_k_salary  a where trim(a.username)='姓名' and a.pch=?  "; 
			}else{
				sql = "select a.* from tt_k_salary  a where trim(a.username)='姓名' and a.pch=? and autoid = '"+nextAutoid+"' ";
			}
			ps = conn.prepareStatement(sql);
			ps.setString(1, pch);
			rs = ps.executeQuery();
			RSMD = rs.getMetaData();
			if(rs.next()){
				sql = "";
				ii = 1;
				String autoid = rs.getString("autoid");
				for (int i = 1; i <= RSMD.getColumnCount(); i++) {
					if(RSMD.getColumnLabel(i).toLowerCase().equals("v" + ii)){
						//n1....,v1....
						String v = asf.showNull(rs.getString("v" + ii));
						if(!"".equals(v)){
							sql += ",n" + ii + "='"+v+"' ";
						}
						ii ++;
						
					}
				}
				if(!"".equals(sql)){
					list.add("update tt_k_salary set " + sql.substring(1) + " where pch = '"+pch+"' and autoid >'"+autoid+"' ");
				}
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			for (int i = 0; i < list.size(); i++) {
				ps = conn.prepareStatement((String)list.get(i));
				ps.execute();
				DbUtil.close(ps);
			}
			
			
			sql="delete from tt_k_salary where pch=? and trim(username)='姓名' ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, pch);
			ps.execute();
			
			sql = "update  tt_k_salary a,(select group_concat(groupflag,'.',wagesname) as groupset from k_rankwages where rankid='0') b" +
			" set a.groupset = b.groupset where pch=? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, pch);
			ps.execute();
			
			updateData(pch,areaid);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	
	/**
	 * 
	 * @param pch
	 * @param areaid : 当前人的所在分所
	 * @throws MatechException
	 */
	public void updateData(String pch,String areaid)throws Exception{
		updateData(pch,areaid,"暂存");
	}
	
    public void updateData(String pch,String areaid,String status)throws Exception{
		PreparedStatement ps = null;

		ResultSet rs = null;
		try {
			//通过身份证还原用户ID
			String sql="update tt_k_salary a,k_department b set a.departmentId = b.autoid where a.departmentname = b.departname and pch=? and b.areaid=? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, pch);
			ps.setString(2, areaid);
			ps.execute();
			DbUtil.close(ps);
			
			sql="UPDATE tt_k_salary a,k_user b SET a.`userId` = b.`id`,a.rankId = b.rank,a.departmentId=b.departmentId WHERE a.`username` = b.`name` and a.departmentId = b.departmentId and pch=? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, pch);
			ps.execute();
			DbUtil.close(ps);
			
			sql = "UPDATE tt_k_salary a  SET a.`rankId` = 0 WHERE a.pch=? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, pch);
			ps.execute();
			DbUtil.close(ps);
			
			sql="delete from  tt_k_salary where (userid is null or userid=0) and pch=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, pch);
			ps.execute();
			DbUtil.close(ps);
			
			sql = "UPDATE tt_k_salary a set a.status = ? where a.pch=?  ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, status);
			ps.setString(2, pch);
			ps.execute();
			DbUtil.close(ps);
			
//			sql="";
//			for(int i=1;i<70;i++){
//				sql+="c"+i+" = v"+i+",";
//			}
//			sql = "UPDATE tt_k_salary a set " +sql+" c70 = v70 where a.pch=? ";
//			ps = conn.prepareStatement(sql);
//			ps.setString(1, pch);
//			ps.execute();
//			DbUtil.close(ps);	
			
			//还原状态
			sql ="UPDATE tt_k_salary a ,(SELECT STATUS,nowyear,nowmonth,groupset FROM k_salary WHERE pch=? LIMIT 1) b \n"
					+" SET a.status = b.status,a.nowyear=b.nowyear,a.nowmonth=b.nowmonth,a.groupset=b.groupset \n"
					+" where a.pch=? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, pch);
			ps.setString(2, pch);
			ps.execute();
			DbUtil.close(ps);
			
			/**
			 * 设置字段名称
			 * 对EXCEL操作时，有可能会删除一列
			 * 所以对于n1....等字段名称只能通过第一列来还原
			 */
			//设置字段名称,还原【工资分组】
//			for(int i =1;i<=70;i++){
//				sql ="update tt_k_salary a join (SELECT c"+i+",n"+i+",rankid FROM k_salary WHERE pch=? group by rankid )b \n"
//					+"on a.rankid=b.rankid \n"
//					+"set a.c"+i+"=b.c"+i+" where a.pch=? and a.n"+i+"=b.n"+i+" ";
//				System.out.println("qwh:恢复字段名称="+sql);
//				ps = conn.prepareStatement(sql);
//				ps.setString(1, pch);
//				ps.setString(2, pch);
//				ps.execute();
//				DbUtil.close(ps);
//			}
			
			/**
			 * 只删除同一年月，同一项目，同一部门，同一人的记录
			
			sql = "delete a from k_salary a ,tt_k_salary b " +
			"	where b.pch = ? " +
			"	and a.userid = b.userid " +
			"	and a.nowyear = b.nowyear and a.nowmonth = b.nowmonth " +
			"	and a.departmentid = b.departmentid " +
			"	and a.pchname = b.pchname";
			ps = conn.prepareStatement(sql);
			ps.setString(1, pch);
			ps.execute();
			DbUtil.close(ps);	
			
			
			//重新插入
			 */
			deleteByPch(pch);
			insertData(pch);
		
		}catch (Exception e) {
			e.printStackTrace();
			Debug.print(Debug.iError,"访问失败",e);
			throw new MatechException("访问失败："+e.getMessage(),e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
    
    public void insertData(String pch)throws MatechException{
		PreparedStatement ps = null;
		try {
			//更新已有用户信息
			String sql = "INSERT INTO k_salary  \n" +
							            "( userId,rankId,nowYear,nowMonth,countValue,departmentId,pch,status,pchname,memo,groupset, \n" +
							            "n1,v1,c1,n2,v2,c2,n3,v3,c3,n4,v4,c4,n5,v5,c5, \n" +
							            "n6,v6,c6,n7,v7,c7,n8,v8,c8,n9,v9,c9,n10,v10,c10, \n" +
							            "n11,v11,c11,n12,v12,c12,n13,v13,c13,n14,v14,c14,n15,v15,c15, \n" +
							            "n16,v16,c16,n17,v17,c17,n18,v18,c18,n19,v19,c19,n20,v20,c20, \n" +
							            "n21,v21,c21,n22,v22,c22,n23,v23,c23,n24,v24,c24,n25,v25,c25, \n" +
							            "n26,v26,c26,n27,v27,c27,n28,v28,c28,n29,v29,c29,n30,v30,c30, \n" +
							            "n31,v31,c31,n32,v32,c32,n33,v33,c33,n34,v34,c34,n35,v35,c35, \n" +
							            "n36,v36,c36,n37,v37,c37,n38,v38,c38,n39,v39,c39,n40,v40,c40, \n" +
							            "n41,v41,c41,n42,v42,c42,n43,v43,c43,n44,v44,c44,n45,v45,c45, \n" +
							            "n46,v46,c46,n47,v47,c47,n48,v48,c48,n49,v49,c49,n50,v50,c50    " +
							            ")\n" +
							            "select userId,rankId,nowYear,nowMonth,countValue,departmentId,pch,status,pchname,memo,groupset, \n" +
							            "n1,v1,c1,n2,v2,c2,n3,v3,c3,n4,v4,c4,n5,v5,c5, \n" +
							            "n6,v6,c6,n7,v7,c7,n8,v8,c8,n9,v9,c9,n10,v10,c10, \n" +
							            "n11,v11,c11,n12,v12,c12,n13,v13,c13,n14,v14,c14,n15,v15,c15, \n" +
							            "n16,v16,c16,n17,v17,c17,n18,v18,c18,n19,v19,c19,n20,v20,c20, \n" +
							            "n21,v21,c21,n22,v22,c22,n23,v23,c23,n24,v24,c24,n25,v25,c25, \n" +
							            "n26,v26,c26,n27,v27,c27,n28,v28,c28,n29,v29,c29,n30,v30,c30, \n" +
							            "n31,v31,c31,n32,v32,c32,n33,v33,c33,n34,v34,c34,n35,v35,c35, \n" +
							            "n36,v36,c36,n37,v37,c37,n38,v38,c38,n39,v39,c39,n40,v40,c40, \n" +
							            "n41,v41,c41,n42,v42,c42,n43,v43,c43,n44,v44,c44,n45,v45,c45, \n" +
							            "n46,v46,c46,n47,v47,c47,n48,v48,c48,n49,v49,c49,n50,v50,c50    " +
							            "from tt_k_salary a \n"+
							            "where a.pch=? \n"+
							            "and a.userId <> ''";
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, pch);
			ps.execute();
			//ps.execute("Flush tables");
			DbUtil.close(ps);
			
		}catch (Exception e) {
			Debug.print(Debug.iError,"访问失败",e);
			throw new MatechException("访问失败："+e.getMessage(),e);
		} finally {
			DbUtil.close(ps);
		}
	}
    
	
	/**
	 * 获取工资项作为excel的表头
	 */
	public List<String> getExcelValue(){
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<String> list = new ArrayList<String>();
		String sql ="select  DISTINCT wagesName from k_rankwages order by orderId";
		try{
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				list.add(rs.getString("wagesName"));
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return list;
		
	}

	
	//显示职级层
	public String showRankDiv(String pch){
		 StringBuffer rankDiv = new StringBuffer("<div style='height:160px;'><table border='0'  style='line-height: 22px;'   class='data_tb' align='center'><tr>"); 
		 String	sql= "select distinct a.rankId,b.name rankName " +
		 "from k_salary a " +
		 "left join k_rank b on a.rankId = b.autoId \n"+
		 "where pch=?  and rankId<>'null' order by b.autoid desc";
		 PreparedStatement ps = null;
		 ResultSet rs = null;
		 try{
			 ps = conn.prepareStatement(sql);
			 ps.setString(1, pch);
			 rs = ps.executeQuery();
			 while(rs.next()){
				 rankDiv.append("<td class='data_tb_alignright' onmouseover= \"style.backgroundColor='#e4f4fe'\" "+ 
						 "onmouseout= \"style.backgroundColor='#d3e1f1'\" onclick='showRankDiv(\"div_"+rs.getString("rankId")+"\")'>"+rs.getString("rankName")+"</td>");
			 }
			 rankDiv.append("</tr></table><div>");
		 }catch(Exception e){
			 e.printStackTrace();
		 }finally{
			 DbUtil.close(rs);
			 DbUtil.close(ps);
		 }
		return rankDiv.toString();
	}
	
	
	public void AddInit1(String pch) throws Exception {
		ASFuntion asf = new ASFuntion();
		PreparedStatement ps = null,ps2 = null,ps3 = null,ps4 = null,ps5 = null;
		ResultSet rs5 = null,rs4 = null, rs2 = null,rs = null;
		
		try {
			
			String sql ="update k_salary set rankid=0 where pch=? and rankid is null";
			ps = conn.prepareStatement(sql);
			ps.setString(1, pch);
			ps.execute();
			ps.close();
			
			
			sql= "select distinct a.rankId,b.name rankName " +
			 "from k_salary a " +
			 "left join k_rank b on a.rankId = b.autoId \n"+
			 "where pch=? and rankId<>'null' order by b.autoid desc";
			ps = conn.prepareStatement(sql);
			ps.setString(1, pch);
			rs = ps.executeQuery();
			// 根据工资年月
			while(rs.next()){
				//工资设定 : 插入基本值
				String rankId = rs.getString("rankId");
				sql = " SELECT rankId,wagesName,getValue,updateTache,valueType,propenty,groupFlag,groupset " +
						"FROM k_rankwages,(select group_concat(groupflag,'.',wagesname) as groupset from k_rankwages where rankid='"+rankId+"') b " +
						"where  rankid='"+rankId+"' order by abs(orderid) asc";
				ps2 = conn.prepareStatement(sql);
				rs2 = ps2.executeQuery();
				int i = 1;
				List<String> list = new ArrayList<String>();
				List<String> ifInputList = new ArrayList<String>();
				List<String> valueTypeList = new ArrayList<String>();
				
				while (rs2.next()) {
					
					String wagesName = asf.showNull(rs2.getString("wagesName"));    //工资项名称
					String propenty = asf.showNull(rs2.getString("propenty"));              //工资项拼音简写
					String valueType = asf.showNull(rs2.getString("valueType"));           //工资项取值类型
				    String gValue = asf.showNull(rs2.getString("getValue"));                 //工资项具体取值
				    String groupset = asf.showNull(rs2.getString("groupset")); //分组集合
				    
				    if (gValue.indexOf("'")>0) {            //如果查到的工资项值为sql语句，就转义
						gValue=gValue.replaceAll("\\'", "\\\\'");
					}
					sql="update k_salary set n"+i+"=? , \n" +
								" v"+i+"=?, \n" +
								"c"+i+"=?, \n" +
								"groupset=? \n" +
								"where rankid=? \n" +
								"and pch=? " ;
					ps3 = conn.prepareStatement(sql);
					ps3.setString(1, wagesName);
					ps3.setString(2, gValue);
					ps3.setString(3, propenty);
					ps3.setString(4, groupset);
					ps3.setString(5, rankId);
					ps3.setString(6, pch);
					ps3.execute();
					DbUtil.close(ps3);
					
					list.add("v"+(i));
					ifInputList.add(rs2.getString("updateTache")); 
					valueTypeList.add(valueType);
					
					i++;	
				}
				DbUtil.close(rs2);
				DbUtil.close(ps2);
				
				//修改[数据库计算][记忆字段]的工资金额
				sql = "select a.* from k_salary as a \n" 
					 + " where a.rankid ='"+rankId+"' and a.pch='"+pch+"' and rankId<>'null'  ";
				ps4 = conn.prepareStatement(sql);
				rs4 = ps4.executeQuery();
				while(rs4.next()){
					String userId = asf.showNull(rs4.getString("userId")); //userId
					
					for(int j=0;j<list.size();j++){
						if(ifInputList.get(j) !=null && !"".equals(ifInputList.get(j))){            //假如“有权修改的环节”有值
							String getValue = "";
							if("".endsWith(asf.showNull(rs4.getString(list.get(j))))){              //如果当前工资项为空，就设为0
								getValue ="0";
							}else{
								getValue=asf.showNull(rs4.getString(list.get(j)));
							}
							if (null!=valueTypeList.get(j)&&!"".equals(valueTypeList.get(j))) {           //如果取值类型不为空
								if (valueTypeList.get(j).equals("数据库计算")) {                                   //如果取值类型是“数据库计算”，执行查询初始化
									String strSql =getValue;
									
									if(!"0".equals(strSql)){
										try{
											// sql的外部变量替换
											String sqlVariableSentence = "";
											String[] sqlVariables = null;
											sqlVariables = UTILString.getVaribles(strSql);
											for (int k = 0; k < sqlVariables.length; k++) {
												sqlVariableSentence = rs4.getString(sqlVariables[k]);
												if (sqlVariableSentence!=null && !"".equals(sqlVariableSentence)) {
													strSql = strSql.replaceAll("\\$\\{" + sqlVariables[k] + "\\}",sqlVariableSentence);
												}
											}
											ps5 = conn.prepareStatement(strSql);
											rs5 = ps5.executeQuery();
											while (rs5.next()) {
												getValue = rs5.getString(1);
												if (getValue==null) {
													getValue = "0";
												}
											}
										}catch(Exception e){
											System.out.println("计算数据库SQL失败："+strSql+"|");
										}finally{
											DbUtil.close(rs5);
											DbUtil.close(ps5);
										}
											
									}
								}
								if(valueTypeList.get(j).equals("记忆字段")){
									 int year = Integer.parseInt(rs4.getString("nowyear"));
									 int month = Integer.parseInt(rs4.getString("nowMonth"));
							
									 String sql2="select "+list.get(j)+" from k_salary where userId='"+userId+"'" +
									 		"  and nowyear *12 + nowmonth='"+(year * 12 + (month-1))+"' and status='已发放'";
   								    PreparedStatement ps7 = conn.prepareStatement(sql2);
									ResultSet rs7 = ps7.executeQuery();
									if(rs7.next()){
										getValue = rs7.getString(1);
									}else{
										getValue = "0";
									}
									
									DbUtil.close(rs7);
									DbUtil.close(ps7);
								
								}
							}
							
							//不为0，修改金额
							if(!"0".equals(getValue)){
								sql = "update k_salary set " + list.get(j) + "=? where pch=? and userid = ? ";
								ps3 = conn.prepareStatement(sql);
								ps3.setString(1, getValue);
								ps3.setString(2, pch);
								ps3.setString(3, userId);
								ps3.execute();
								DbUtil.close(ps3);
							}
							
						} 
					}
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(rs2);
			DbUtil.close(rs4);
			DbUtil.close(rs5);
			
			DbUtil.close(ps);
			DbUtil.close(ps2);
			DbUtil.close(ps3);
			DbUtil.close(ps4);
			DbUtil.close(ps5);
			
		}
		
		
	}
	
	/**
	 * 初始化 添加
	 * @param 
	 * @return
	 */
	public List<String> AddInit(String pch){
		
		ASFuntion asf = new ASFuntion();
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		PreparedStatement ps2 = null;
		ResultSet rs2 = null;
		PreparedStatement ps3 = null;
		
		ResultSet rs4 = null;
		PreparedStatement ps4 = null;
		
		PreparedStatement ps5 = null;
		ResultSet rs5 = null;
		
		String sql ="";
		List<String> tableList = new ArrayList<String>();
	     String divHead= showRankDiv(pch);
	     tableList.add(divHead);
		String strFormulas,strCNames,strENames;
		String preRankid="",strFormula="",newScript=""; 
		try {
			//int sumSalary = 1;
			sql= "select distinct a.rankId,b.name rankName " +
			 "from k_salary a " +
			 "left join k_rank b on a.rankId = b.autoId \n"+
			 "where pch=? and rankId<>'null' order by b.autoid desc";
			ps = conn.prepareStatement(sql);
			ps.setString(1, pch);
			rs = ps.executeQuery();
			// 根据工资年月
			StringBuffer wagesTotal = new StringBuffer("var myvar = new Array();");
			//String wagesTotal = "var myvar = 1;";
			while(rs.next()){
				
				//清空
				strFormulas="";
				strCNames="";
				strENames="";
			
				//工资设定
				String rankId = rs.getString("rankId");
				String rankName = rs.getString("rankName");
				sql = " SELECT rankId,wagesName,getValue,updateTache,valueType,propenty " +
						"FROM k_rankwages " +
						"where  rankid='"+rankId+"' order by orderid asc";
				ps2 = conn.prepareStatement(sql);
				rs2 = ps2.executeQuery();
				int i = 1;
				String sName=""; //工资项名称
				List<String> list = new ArrayList<String>();
				List<String> ifInputList = new ArrayList<String>();
				List<String> valueTypeList = new ArrayList<String>();
				StringBuffer total = new StringBuffer("<tr><td class='FixedDataColumn'>&nbsp;</td><td class='FixedDataColumn'>&nbsp;</td><td class='FixedDataColumn'>小计</td><td class='data_tb_alignright'></td>");
				String propenty ="";
				while (rs2.next()) {
					
					String wagesName = asf.showNull(rs2.getString("wagesName"));    //工资项名称
				    propenty = asf.showNull(rs2.getString("propenty"));              //工资项拼音简写
					String valueType = asf.showNull(rs2.getString("valueType"));           //工资项取值类型
				    String gValue = asf.showNull(rs2.getString("getValue"));                 //工资项具体取值
				    if (gValue.indexOf("'")>0) {            //如果查到的工资项值为sql语句，就转义
						gValue=gValue.replaceAll("\\'", "\\\\'");
					}
					sql="update k_salary set n"+i+"='"+wagesName+"', \n" +
								" v"+i+"='"+gValue+"', \n" +
								"c"+i+"='"+propenty+"' \n" +
//									"countValue= ifnull(countValue,0)+ifnull(v"+i+",0) " +
								"where rankid='"+rankId+"' \n" +
								"and pch=? " ;
					ps3 = conn.prepareStatement(sql);
					ps3.setString(1, pch);
					ps3.execute();
					i++;	
					
					ifInputList.add(rs2.getString("updateTache")); 
					valueTypeList.add(valueType);
					sName+="<td class='data_tb_alignright'  >"+wagesName+"</td>";
					total.append("<td class='data_tb_content'><input type='text' style='border:0px;background-color:transparent;' id='"+propenty+rankId+"_all' readOnly='readOnly' size='8'></td>");
					list.add("v"+(i-1));        //如果放在I++前 需要把-1去掉
					
					strCNames+=",\""+wagesName+"\"";
				    strENames+=",\""+propenty+"\"";
				    strFormula=gValue;
					if (("字段计算").equals(valueType)) {
						strFormulas+=",\""+strFormula+"\"";
					}
					
					DbUtil.close(ps3);
				}
				
				DbUtil.close(rs2);
				DbUtil.close(ps2);
			
		
				sName="<td class='FixedCell' >序号</td><td class='FixedCell'>姓名</td><td class='FixedCell' ><input type='hidden' value='"+rankId+"' name='rank'>职级</td><td class='data_tb_alignright' >部门</td>"+sName;
				//"<td class='data_tb_alignright'>工资总计</td>";
			   
				String newTable = "<div class='FixedHeaderColumnsTableDiv'  style='width: 98%; height: 340px; display: none;' id='div_"+rankId+"' ><table border='0'  style='line-height: 22px;'   class='data_tb' align='center'>";
			
//				if (null==rankId||"".equals(rankId)||"null".equals(rankId)) {         //如果尚未设置薪酬级别
//					newTable+="<tr ><td class='data_tb_alignright' colspan='2'>以下人员尚未设置薪酬级别:</td></tr>"; 
//				}

				newTable+="<tr class='FixedHeaderRow1' >"+sName+"</tr>";
				sql = "select a.*,b.name as userName,c.departName,a.nowYear,a.nowMonth from k_salary as a \n" 
					 + " left join k_user b on a.userId = b.id \n"  
					 + " left join k_department c on a.departmentId = c.autoId \n"
					 + " where a.rankid ='"+rankId+"' and a.pch='"+pch+"' and rankId<>'null' order by a.departmentId,b.loginId  ";
				ps4 = conn.prepareStatement(sql);
				rs4 = ps4.executeQuery();
				String newTt = "";
				int x =1;
				while(rs4.next()){
					String departName = asf.showNull(rs4.getString("departName"));        //部门名称
					String userId = asf.showNull(rs4.getString("userId"));                           //userId
					if("".equals(departName)){
						departName=" 未设置部门";							
					}
					newTt+="<tr>" +
					        "<input type='hidden' value="+userId+" name='userId_"+rankId+"'>"+
					        "<input type='hidden' value="+rs4.getString("rankId")+" name='"+userId+"-rankId'>"+
					        "<input type='hidden' value="+pch+" id='pch_add'>"+
							"<td class='FixedDataColumn'>"+x+"</td>"+
					        "<td class='FixedDataColumn'>"+rs4.getString("userName")+"" +
							"</td><td class='FixedDataColumn'>"+rankName+"</td>"+
							"<td class='data_tb_content' >"+departName+"</td>";
					x++; 
					for(int j=0;j<list.size();j++){
						if(ifInputList.get(j) !=null && !"".equals(ifInputList.get(j))){            //假如“有权修改的环节”有值
							String getValue = "",temp = "n",temp2="c";
//							System.out.println(asf.showNull(rs4.getString("n"+(j+1)))+":"+asf.showNull(rs4.getString(list.get(j))));
							String numOnly = "  onkeyup=\"inputkeyup(this)\" onblur=\"inputblur(this)\" onkeypress=\"inputkeypress(this)\" ";  //只能输入数字和两位小数
							if("".endsWith(asf.showNull(rs4.getString(list.get(j))))){              //如果当前工资项为空，就设为0
								getValue ="0";
							}else{
								getValue=asf.showNull(rs4.getString(list.get(j)));
							}
							if (null!=valueTypeList.get(j)&&!"".equals(valueTypeList.get(j))) {           //如果取值类型不为空
								if (valueTypeList.get(j).equals("数据库计算")) {                                   //如果取值类型是“数据库计算”，执行查询初始化
									String strSql =getValue;
									
									if(!"0".equals(strSql)){
										// sql的外部变量替换
										String sqlVariableSentence = "";
										String[] sqlVariables = null;
										sqlVariables = UTILString.getVaribles(strSql);
										for (int k = 0; k < sqlVariables.length; k++) {
											sqlVariableSentence = rs4.getString(sqlVariables[k]);
											if (sqlVariableSentence!=null && !"".equals(sqlVariableSentence)) {
												strSql = strSql.replaceAll("\\$\\{" + sqlVariables[k] + "\\}",
														sqlVariableSentence);
											}
										}
										ps5 = conn.prepareStatement(strSql);
										rs5 = ps5.executeQuery();
										while (rs5.next()) {
											getValue = rs5.getString(1);
											if (getValue==null) {
												getValue = "0";
											}
										}
									}
								}
								if(valueTypeList.get(j).equals("记忆字段")){
									 int month = Integer.parseInt(rs4.getString("nowMonth"));
							
									 String sql2="select v"+(j+1)+" from k_salary where userId='"+userId+"'" +
									 		"  and nowyear = YEAR(now()) and nowmonth='"+(month-1)+"' and status='已发放'";
   								    PreparedStatement ps7 = conn.prepareStatement(sql2);
									ResultSet rs7 = ps7.executeQuery();
									if(rs7.next()){
										getValue = rs7.getString(1);
									}
									
									DbUtil.close(rs7);
									DbUtil.close(ps7);
								
								}
							}
							
							if("业务部".endsWith(ifInputList.get(j)) || "均可".endsWith(ifInputList.get(j))){     //如果“有权修改的环节”对应当前角色，就打印输入框
								newTt+= "<td class='data_tb_content' >" +
											   " <input type='hidden' value='"+rs4.getString(temp+(j+1))+"' name='"+userId+"-n"+(j+1)+"'>"+
											   " <input type='hidden' value='"+rs4.getString(temp2+(j+1))+"' name='"+userId+"-c"+(j+1)+"'>"+
											   " <input type='text' size='8' value='"+getValue+"' id='"+rs4.getString(temp2+(j+1))+userId+
											   "' name='"+rs4.getString(temp2+(j+1))+rankId+"' " +numOnly+
											   " onchange='m(\""+userId+"\",\"strFormulas"+rankId+"\",\"strCNames"+rankId+"\",\"strENames"+rankId+"\",\""+rankName+"\",\"myvar\"),getSum(),getAllSum()'  onfocus=this.select()>";
								if (j==0) {
									newTt+="<input type='hidden' value='m(\""+userId+"\",\"strFormulas"+rankId+"\",\"strCNames"+rankId+"\",\"strENames"+rankId+"\",\""+rankName+"\",\"myvar\")' name='executeStr'>";
								}
								newTt+=" </td>";
							}else{        //如果“有权修改的环节”不对应当前角色，就打印只读的 输入框
								 
								newTt += "<td class='data_tb_content'>"+
									      			" <input type='hidden' value='"+rs4.getString(temp+(j+1))+"' name='"+userId+"-n"+(j+1)+"'>"+
									      			" <input type='hidden' value='"+rs4.getString(temp2+(j+1))+"' name='"+userId+"-c"+(j+1)+"'>"+
									      			" <input type='text' size='7' style=\"border:0px;background-color:transparent;\" id='"+
									      			rs4.getString(temp2+(j+1))+userId+"' value='"+getValue+"' name='"+
									      			rs4.getString(temp2+(j+1))+rankId+"' readonly='readonly' >";
								if (j==0) {
									newTt+="<input type='hidden' value='m(\""+userId+"\",\"strFormulas"+rankId+"\",\"strCNames"+rankId+"\",\"strENames"+rankId+"\",\""+rankName+"\",\"myvar\")' name='executeStr'>";
								}
								newTt+="</td>" ;
							}
						} 
					}
//					newTt += "<td class='data_tb_content' id='sumSalary"+sumSalary+"'>"+
//									" <input type='hidden' value="+rs4.getString("countValue")+" name='"+rs4.getString("userId")+"-countValue'>"+ 
//									rs4.getString("countValue")+"</td>";
//									sumSalary++;
					newTt+="</tr>";
				}
				//+total.toString()
				newTable+=newTt+total.append("</tr>")+"</table></div>";
			
				DbUtil.close(rs4);
				DbUtil.close(ps4);
				DbUtil.close(ps5);
				DbUtil.close(rs5);
				
				if (!preRankid.equals(rankId)){
					//每遇到一种新的职级，就输出一次
					if ("".equals(strFormulas)){
					}else{
						newScript+="var strFormulas"+rankId+"=new Array("+strFormulas.substring(1)+"); ";
						newScript+="var strCNames"+rankId+"=new Array("+strCNames.substring(1)+"); ";
						newScript+="var strENames"+rankId+"=new Array("+strENames.substring(1)+"); ";
						
					}
					preRankid=rankId;
				}
				wagesTotal.append("myvar['"+rankId+"']=new Array("+strENames.substring(1)+");");
				//newScript+="var myvar =1; ";
				tableList.add(newTable);
			
			}
			/**
			 * 增加合计的功能
			 */
			newScript+=wagesTotal.toString();
			
			String sumTable="<div class='FixedHeaderColumnsTableDiv'  id='sumDiv'  style='width: 99%; height:80px;display:none' ><table border='0' style='line-height: 22px;'   class='data_tb' align='center'><tr ><td class='data_tb_alignright' >部门合计：</td>";
			String sumTd="<tr><td FixedDataColumn>&nbsp;</td>";
			String getAll="";
			String wageList ="";
			String sql6 ="SELECT DISTINCT wagesName,propenty FROM k_rankwages WHERE propenty <>''  ORDER BY orderId";
			PreparedStatement ps6 = conn.prepareStatement(sql6);
			ResultSet rs6 = ps6.executeQuery();
			while(rs6.next()){
				wageList+=",\""+rs6.getString("propenty")+"\"";
				sumTable+="<td class='data_tb_alignright'>"+rs6.getString("wagesName")+"</td>";
				sumTd+="<td class='data_tb_content'><input type='text' style=\"border:0px;background-color:transparent;\" readOnly='readOnly' size='8' id='"+rs6.getString("propenty")+"_allsum' name='"+rs6.getString("propenty")+"_allsum'></td>";
			}
			sumTd+="</tr>";
			sumTable+="</tr>"+sumTd+"</table></div>";
			getAll="var wageList = "+"new Array("+wageList.substring(1)+");";
			tableList.add(sumTable);
			newScript+=getAll;
			tableList.add(newScript);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
			DbUtil.close(ps2);
			DbUtil.close(ps3);
			DbUtil.close(rs2);
			DbUtil.close(rs);
		}
		return tableList;
		
	}
	/**
	 * 根据部门取区域id
	 * @param autoId
	 * @return
	 */
	public String getArea(String autoId){
		PreparedStatement ps = null;
		ResultSet rs = null;
		String areaId = "";
		try{
			String sql ="select areaid from k_department where autoId="+autoId;
			ps= conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				areaId = rs.getString(1);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
			DbUtil.close(rs);
		}
		return areaId;
	}
	/**
	 * 数据库计算
	 * @param 
	 * @return 
	 */
	public String excuteSql(String sql,String id,String year,String month){
		PreparedStatement ps = null;
		ResultSet rs = null;
		String day="";
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, id);
			ps.setString(2, year);
			ps.setString(3, month);
			rs = ps.executeQuery();
			while (rs.next()) {
				day=rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
			DbUtil.close(rs);
		}
		
		return day;
	}
	public String checkStatus(String pch){
		String result = "";
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try{
			  String sql ="select status from k_salary where pch=?  group by status";
			  ps = conn.prepareStatement(sql);
			  ps.setString(1, pch);
			  rs = ps.executeQuery();
			  while(rs.next()){
				  result = rs.getString("status");
			  }
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return result;
	}
	
	/**
	 * 初始化    审批/ 修改 /发放
	 * @param pch,editRight
	 * @return tableList
	 */
	public List<String> updateInit(String pch,String editRight){
		
		ASFuntion asf = new ASFuntion();
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		PreparedStatement ps2 = null;
		ResultSet rs2 = null;
		
		PreparedStatement ps3 = null;
		
		PreparedStatement ps4 = null;
		ResultSet rs4 = null;
		
		PreparedStatement ps5 = null;
		ResultSet rs5 = null;
		
		String sql = "select distinct a.rankId,b.name rankName " + 
					 "from k_salary a " +
					 "left join k_rank b on a.rankId = b.autoId \n"+
					 "where a.pch=? and a.rankId<>'null' order by b.autoid desc";
		List<String> tableList = new ArrayList<String>();
		String rankDiv = showRankDiv(pch);
		tableList.add(rankDiv);
		
		String strFormulas,strCNames,strENames;
		String preRankid="",strFormula="",newScript=""; 
		
		try {
			//int sumSalary = 1;
			ps = conn.prepareStatement(sql);
			ps.setString(1, pch);
			rs = ps.executeQuery();
			// 根据工资年月   
			StringBuffer wagesTotal = new StringBuffer("var myvar = new Array();");
			while(rs.next()){
				
				//清空
				strFormulas="";
				strCNames="";
				strENames="";
				
				//工资设定
				String rankId = rs.getString("rankId");
				String rankName = rs.getString("rankName");
				sql = " SELECT rankId,wagesName,getValue,updateTache,valueType,propenty " +
						"FROM k_rankwages " +
						"where  rankid='"+rankId+"' order by orderid asc";   //职级的工资项
				ps2 = conn.prepareStatement(sql);
				rs2 = ps2.executeQuery();
				int i = 1;
				String sName=""; //工资项名称
				List<String> list = new ArrayList<String>();
				List<String> ifInputList = new ArrayList<String>();
				List<String> valueTypeList = new ArrayList<String>();
				StringBuffer total = new StringBuffer("<tr><td class='FixedDataColumn'>&nbsp;</td><td class='FixedDataColumn'>&nbsp;</td><td class='FixedDataColumn'>小计</td><td class='data_tb_alignright'></td>");
				while (rs2.next()) {
					
					String wagesName = asf.showNull(rs2.getString("wagesName"));    //工资项名称
					String propenty = asf.showNull(rs2.getString("propenty"));              //工资项拼音简写
					String valueType = asf.showNull(rs2.getString("valueType"));           //工资项取值类型
				    String gValue = asf.showNull(rs2.getString("getValue"));                 //工资项具体取值
					i++;	
					ifInputList.add(rs2.getString("updateTache"));      //读取权限
					valueTypeList.add(valueType);           
					sName+="<td class='data_tb_alignright'  >"+wagesName+"</td>";
					total.append("<td class='data_tb_content'><input type='text' style='border:0px;background-color:transparent;' id='"+propenty+rankId+"_all' size='8' readOnly='readOnly'></td>");
					list.add("v"+(i-1));//如果放在I++前 需要把-1去掉
					strCNames+=",\""+wagesName+"\"";
				    strENames+=",\""+propenty+"\"";
				    strFormula=gValue;
					if (("字段计算").equals(valueType)) {
						strFormulas+=",\""+strFormula+"\"";
					}
						
				}
				DbUtil.close(rs2);
				DbUtil.close(ps2);
				
				sName="<td class='FixedCell'>序号</td><td class='FixedCell'>姓名</td><td class='FixedCell'><input type='hidden' name='rank' value='"+rankId+"'>职级</td><td class='data_tb_alignright' >部门</td>"+sName;
				//"<td class='data_tb_alignright'>工资总计</td>";
				String newTable = "<div class='FixedHeaderColumnsTableDiv' style='width: 98%; height: 340px;display:none' id='div_"+rankId+"' ><table border='0'  style='line-height: 22px;'   class='data_tb' align='center'>";
			
				if (null==rankId||"".equals(rankId)||"null".equals(rankId)) {         //如果尚未设置薪酬级别
					newTable+="<tr ><td class='data_tb_alignright' colspan='2'>以下人员尚未设置薪酬级别:</td></tr>"; 
				}
				
				newTable+="<tr class='FixedHeaderRow1'>"+sName+"</tr>";
				
				sql = "select distinct b.name as userName,a.*,c.departName,a.departmentId  from k_salary as a \n" 
					 + " inner join k_user b on a.userId = b.id \n"  
					 + " left join k_department c on a.departmentId = c.autoId  where 1=1 \n";
				if (null==rankId||"".equals(rankId)||"null".equals(rankId)) {         //如果尚未设置薪酬级别
						sql+=" and ( a.rankid IS NULL or a.rankid='null') AND a.pch='"+pch+"' ORDER BY a.departmentId,b.loginId ";
				} else {
						sql+=" and a.rankid ='"+rankId+"' and a.pch='"+pch+"' order by a.departmentId,b.loginId  ";
				}
				//System.out.println("qwh:sql="+sql);
				ps4 = conn.prepareStatement(sql);
				rs4 = ps4.executeQuery();
				String newTt = "";
				int x = 1;
				while(rs4.next()){
					String departName = asf.showNull(rs4.getString("departName"));        //部门名称
					String userId = asf.showNull(rs4.getString("userId"));                           //userId
					if("".equals(departName)||null==departName){
						departName=" 未设置部门";							
					}
					newTt+="<tr>" +
							"<td class='FixedDataColumn'>"+x+"</td>"+
							"<td class='FixedDataColumn'>"+rs4.getString("userName") +"</td>"+
							"<td class='FixedDataColumn'>"+rankName+"</td>"+
							"<input type='hidden' value='"+rs4.getString("nowYear")+"' name='Nian'>"+
							"<input type='hidden' value='"+rs4.getString("nowMonth")+"' name='Yue'>"+
							"<input type='hidden' value="+userId+" name='userId"+rankId+"'>"+
							"<input type='hidden' value='"+rs4.getString("departmentId")+"' id ='departmentId' name='departmentId'>"+
							//"<input type='text' value='"+rs4.getString("rankId")+"' name='rankId-"+userId+"' id='rankId-"+userId+"' >"+
							//"</td>" +
							"<td class='data_tb_content' >"+departName+"</td>";
					x++;
					for(int j=0;j<list.size();j++){
						String getValue = "",temp = "n",temp2 = "c";
						String numOnly = "  onkeyup=\"inputkeyup(this)\" onblur=\"inputblur(this)\" onkeypress=\"inputkeypress(this)\" ";  //只能输入数字和两位小数
						if(ifInputList.get(j) !=null && !"".equals(ifInputList.get(j))){      //假如“有权修改的环节”有值
							if("".endsWith(asf.showNull(rs4.getString(list.get(j))))){        //如果当前工资项为空，就设为0
								getValue ="0";
							}else{
								getValue=asf.showNull(rs4.getString(list.get(j)));
							}
							if(editRight.endsWith(ifInputList.get(j)) || "均可".endsWith(ifInputList.get(j))){     //如果“有权修改的环节”对应当前角色，就打印输入框
								newTt+= "<td class='data_tb_content' >" +
											   " <input type='hidden' value='"+rs4.getString(temp+(j+1))+"' name='"+userId+"-n"+(j+1)+"'>"+
											   " <input type='hidden' value='"+rs4.getString(temp2+(j+1))+"' name='"+userId+"-c"+(j+1)+"'>"+
											   " <input type='text' size='8' value='"+getValue+"' id='"+rs4.getString(temp2+(j+1))+userId+
											   "' name='"+rs4.getString(temp2+(j+1))+rankId+"' " +numOnly+
											   " onchange='m(\""+userId+"\",\"strFormulas"+rankId+"\",\"strCNames"+rankId+"\",\"strENames"+rankId+"\",\""+rankName+"\",\"myvar\" ),getSum(),getAllSum()'  onfocus=this.select()>";
								if (j==0) {
									newTt+="<input type='hidden' value='m(\""+userId+"\",\"strFormulas"+rankId+"\",\"strCNames"+rankId+"\",\"strENames"+rankId+"\",\""+rankName+"\",\"myvar\")' name='executeStr'>";
								}
								newTt+="</td>";
							}else{        //如果“有权修改的环节”不对应当前角色，就打印只读的 输入框
								newTt += "<td class='data_tb_content'>"+
								      			" <input type='hidden' value='"+rs4.getString(temp+(j+1))+"' name='"+userId+"-n"+(j+1)+"'>"+
								      			" <input type='hidden' value='"+rs4.getString(temp2+(j+1))+"' name='"+userId+"-c"+(j+1)+"'>"+
								      			" <input type='text' size='7' style=\"border:0px;background-color:transparent;\" id='"+
								      			rs4.getString(temp2+(j+1))+userId+"' value='"+getValue+"' name='"+
								      			rs4.getString(temp2+(j+1))+rankId+"' readonly='readonly' >";
								if (j==0) {
									newTt+="<input type='hidden' value='m(\""+userId+"\",\"strFormulas"+rankId+"\",\"strCNames"+rankId+"\",\"strENames"+rankId+"\")' name='executeStr'>";
								}
				      			newTt+="</td>" ;
							}
						} 
//						newTt += "<input type='hidden' value='"+rs4.getString(temp+(j+1))+"' name='itemCNames'>";
//						newTt += "<input type='hidden' value='"+rs4.getString(temp2+(j+1))+"' name='itemENames'>";
					}
//					newTt += "<td class='data_tb_content' id='sumSalary"+sumSalary+"'>"+
//									" <input type='hidden' value="+rs4.getString("countValue")+" name='"+rs4.getString("userId")+"-countValue'>"+ 
//									rs4.getString("countValue")+"</td>";
//									sumSalary++;
					newTt+="<input type='hidden' value='"+rs4.getString("rankId")+"' name='rankId-"+userId+"' id='rankId-"+userId+"' >";
					newTt+="</tr>";
				}
				
				newTable+=newTt+total.append("</tr>")+"</table></div>";
				
				DbUtil.close(rs4);
				DbUtil.close(ps4);
				DbUtil.close(rs5);
				DbUtil.close(ps5);
				
				if (!preRankid.equals(rankId)){
					//每遇到一种新的职级，就输出一次
					if ("".equals(strFormulas)){
					}else{
						newScript+="var strFormulas"+rankId+"=new Array("+strFormulas.substring(1)+"); ";
						newScript+="var strCNames"+rankId+"=new Array("+strCNames.substring(1)+"); ";
						newScript+="var strENames"+rankId+"=new Array("+strENames.substring(1)+"); ";
					}
					preRankid=rankId;
				}
				wagesTotal.append("myvar['"+rankId+"']=new Array("+strENames.substring(1)+");");
				tableList.add(newTable);
			}
			newScript+=wagesTotal.toString();
			/**
			 * 增加合计的功能
			 */
			newScript+=wagesTotal.toString();
			
			String sumTable="<div class='FixedHeaderColumnsTableDiv' style='width: 99%; height: 80px;display:none'id='sumDiv' ><table border='0' style='line-height: 22px;'   class='data_tb' align='center'><tr ><td class='data_tb_alignright' >部门合计：</td>";
			String sumTd="<tr><td FixedDataColumn>&nbsp;</td>";
			String getAll="";
			String wageList ="";
			String sql6 ="SELECT DISTINCT wagesName,propenty FROM k_rankwages WHERE propenty <>''  ORDER BY orderId";
			PreparedStatement ps6 = conn.prepareStatement(sql6);
			ResultSet rs6 = ps6.executeQuery();
			while(rs6.next()){
				wageList+=",\""+rs6.getString("propenty")+"\"";
				sumTable+="<td class='data_tb_alignright'>"+rs6.getString("wagesName")+"</td>";
				sumTd+="<td class='data_tb_content'><input type='text' style=\"border:0px;background-color:transparent;\" readOnly='readOnly' size='8' id='"+rs6.getString("propenty")+"_allsum' name='"+rs6.getString("propenty")+"_allsum'></td>";
			}
			sumTd+="</tr>";
			sumTable+="</tr>"+sumTd+"</table></div>";
			getAll="var wageList = "+"new Array("+wageList.substring(1)+");";
			tableList.add(sumTable);
			//tableList.add(getAll);
			newScript+=getAll;
			tableList.add(newScript);
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
			DbUtil.close(rs);
			DbUtil.close(ps2);
			DbUtil.close(rs2);
			DbUtil.close(ps3);
			
		}
		return tableList;
		
	}
	
	/**
	 * 点击弹性工资录入按钮，页面切换，进入录入页面
	 * @param 
	 * @return
	 */
	public void updateSoftSalary(String pch){
		
		ASFuntion asf = new ASFuntion();
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		PreparedStatement ps2 = null;
		ResultSet rs2 = null;
		PreparedStatement ps3 = null;
		
		ResultSet rs4 = null;
		PreparedStatement ps4 = null;
		String sql = "select distinct a.rankId,b.name rankName " +
					 "from k_salary a " +
					 " left join k_rank b on a.rankId = b.autoId \n"+
					 "where a.pch=? ";
		List<String> tableList = new ArrayList<String>();
		try {
			int sumSalary = 1;
			ps = conn.prepareStatement(sql);
			ps.setString(1, pch);
			rs = ps.executeQuery();
			// 根据批次号
			while(rs.next()){
				//工资设定
				String rankId = rs.getString("rankId");
				sql = " SELECT rankId,wagesName,getValue,updateTache " +
						"FROM k_rankwages " +
						"where  rankid='"+rankId+"' order by orderid ";
				ps2 = conn.prepareStatement(sql);
				rs2 = ps2.executeQuery();
				int i = 1;
				String sName=""; //工资项名称
				List<String> list = new ArrayList<String>();
				List<String> ifInputList = new ArrayList<String>();
				while (rs2.next()) {
						sql="update k_salary set n"+i+"='"+rs2.getString("wagesName")+"'," +
									" v"+i+"='"+rs2.getString("getValue")+"'" ;
//						if (i == 1 || i ==3 || i ==4 || i ==5 || i ==6 || i ==7 || i==8) {
//							sql += ", countValue= ifnull(countValue,0)+ifnull(v"+i+",0), " +
//									"v23= ifnull(countValue,0)+ifnull(v"+i+",0) ";
//						}
					    sql += "where rankid='"+rankId+"'";
						ps3 = conn.prepareStatement(sql);
						ps3.execute();
						i++;	
						ifInputList.add(rs2.getString("updateTache")); 
						sName+="<td class='data_tb_alignright'  >"+asf.showNull(rs2.getString("wagesName"))+"</td>";
						list.add("v"+(i-1));//如果放在I++前 需要把-1去掉
						DbUtil.close(ps3);
				}
				DbUtil.close(rs2);
				DbUtil.close(ps2);
				
//				sName="<td class='data_tb_alignright'  >姓名</td><td class='data_tb_alignright' >薪酬级别</td><td class='data_tb_alignright' >性别</td>"+sName;
//				String newTable = "<table border='0'  style='line-height: 22px;'   class='data_tb' align='center'>";
//			
//				newTable+="<tr >"+sName+"</tr>";
//				
//				sql = "select a.*,b.name as userName,b.sex as sex,c.name as rankName  from k_salary as a \n" 
//					 + " left join k_user b on a.userId = b.id \n"  
//					 + " left join k_rank c on a.rankId = c.autoId \n"
//					 + "where rankid ='"+rankId+"' and a.pch='"+pch+"' order by a.departmentId,b.loginId  ";
//				ps4 = conn.prepareStatement(sql);
//				rs4 = ps4.executeQuery();
//				String newTt = "";
//				while(rs4.next()){
//					
//					String rankName = asf.showNull(rs4.getString("rankName"));
//					if("".equals(rankName)){
//						rankName=" 未设置薪酬级别";							
//					}else{
//						rankName =rs4.getString("rankName");
//					}
//					newTt+="<tr>" +
//							"<td class='data_tb_content'>"+rs4.getString("userName")+"" +
//							"</td>" +
//							"<td class='data_tb_content' >"+rankName+"</td> " +
//							"<td class='data_tb_content' >"+rs4.getString("sex")+"</td> ";
//					
//					for(int j=0;j<list.size();j++){
//						if(ifInputList.get(j) !=null && !"".equals(ifInputList.get(j))){
//							String getValue = "";
//							if("人事部".endsWith(ifInputList.get(j))){
//								if("".endsWith(asf.showNull(rs4.getString(list.get(j))))){
//									getValue ="0";
//								}else{
//									getValue=asf.showNull(rs4.getString(list.get(j)));
//								}
//								newTt+="<td class='data_tb_content' >" +
//									   " <input type='hidden' value="+list.get(j)+" name='nowMonth'>"+
//									   " <input type='text' size='8' value="+getValue+" name='v' id='v"+j+"'" +
//									   " onblur=ajaxEditSaray('"+pch+"','"+rs4.getString("userId")+"','"+list.get(j)+"','v"+j+"','sumSalary"+sumSalary+"')></td>";
//							}else{
//								newTt+="<td class='data_tb_content' >"+getValue+"</td>";
//							}
//						} 
//					}
//					newTt+="<td class='data_tb_content' id='sumSalary"+sumSalary+"'>"+rs4.getString("countValue")+"</td>";
//					newTt+="</tr>";
//					sumSalary++;
//				}
				
//				newTable+=newTt+"</table>";
//				DbUtil.close(rs4);
//				DbUtil.close(ps4);
//				tableList.add(newTable);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
			DbUtil.close(ps2);
			DbUtil.close(ps3);
			DbUtil.close(rs2);
			DbUtil.close(rs);
		}
		//return tableList;
		
	}
	
	/**
	 * 审批查看的表格
	 * @param pch
	 * @return
	 */
	public List<String> getTables(String pch,String ctype){
		
		ASFuntion asf = new ASFuntion();
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		PreparedStatement ps2 = null;
		ResultSet rs2 = null;
		
		PreparedStatement ps3 = null;
		
		ResultSet rs4 = null;
		PreparedStatement ps4 = null;
		String sql = "select distinct a.rankId,b.name rankName " +
					 "from k_salary a " +
					 " left join k_rank b on a.rankId = b.autoId \n"+
					 "where a.pch=?";
		List<String> tableList = new ArrayList<String>();
		try {
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, pch);
			rs = ps.executeQuery();
			int sumSalary = 1;
			// 根据批次号
			while(rs.next()){
				//工资设定
				String rankId = rs.getString("rankId");
				sql = " SELECT rankId,wagesName,getValue,updateTache " +
						"FROM k_rankwages " +
						"where  rankid='"+rankId+"'";
				ps2 = conn.prepareStatement(sql);
				rs2 = ps2.executeQuery();
				int i = 1;
				String sName=""; //工资项名称
				List<String> list = new ArrayList<String>();
				List<String> ifInputList = new ArrayList<String>();
				while (rs2.next()) {
						i++;	
						ifInputList.add(rs2.getString("updateTache")); 
						sName+="<td class='data_tb_alignright'  >"+rs2.getString("wagesName")+"</td>\n";
						list.add("v"+(i-1));//如果放在I++前 需要把-1去掉
				}
				DbUtil.close(rs2);
				DbUtil.close(ps2);
				
				sName="<td class='data_tb_alignright'  >姓名</td><td class='data_tb_alignright' >部门</td>"+sName+"<td class='data_tb_alignright'>工资总计</td>";
				String newTable = "<table border='0'  style='line-height: 22px;'   class='data_tb' align='center'>\n";
			
				newTable+="<tr >\n<td class='data_tb_alignright' colspan='"+list.size()+2+"'>职级名称:"+rs.getString("rankName")+"</td>\n</tr>"; //所属职级

				newTable+="<tr >\n"+sName+"\n</tr>";
				
				sql = "select a.*,b.name as userName,c.departName  from k_salary as a \n" 
					 + " left join k_user b on a.userId = b.id \n"  
					 + " left join k_department c on a.departmentId = c.autoId \n"
					 + "where rankid ='"+rankId+"' and a.pch='"+pch+"' order by a.departmentId,b.loginId  ";
				ps4 = conn.prepareStatement(sql);
				rs4 = ps4.executeQuery();
				String newTt = "";
				
				while(rs4.next()){
					newTt+="<tr>\n" +
							"<td class='data_tb_content'>"+rs4.getString("userName")+"" +
							"</td>\n" +
							"<td class='data_tb_content' >\n"+rs4.getString("departName")+"</td>\n";
					
					
					for(int j=0;j<list.size();j++){
						if(ifInputList.get(j) !=null && !"".equals(ifInputList.get(j))){
							String getValue = "";
							if(ctype.indexOf(ifInputList.get(j))>-1){
								if("".endsWith(asf.showNull(rs4.getString(list.get(j))))){
									getValue ="0";
								}else{
									getValue=asf.showNull(rs4.getString(list.get(j)));
								}
								newTt+="<td class='data_tb_content' >\n" +
									   " <input type='hidden' value="+list.get(j)+" name='nowMonth'>"+
									   " <input type='text' value="+getValue+" name='v' id='v"+j+"'" +
									   " onblur=ajaxEditSaray('"+pch+"','"+rs4.getString("userId")+"','"+list.get(j)+"','v"+j+"','sumSalary"+sumSalary+"')></td>";
							}else{
								newTt+="<td class='data_tb_content' >"+rs4.getString(list.get(j))+"\n</td>";
							}
						} 
					}
					newTt+="\n<td class='data_tb_content' id='sumSalary"+sumSalary+"'>"+rs4.getString("countValue")+"</td>\n";
					newTt+="\n</tr>";
					sumSalary++;
				}
				
				newTable+=newTt+"\n</table>";
				DbUtil.close(rs4);
				DbUtil.close(ps4);
				tableList.add(newTable);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
			DbUtil.close(ps2);
			DbUtil.close(ps3);
			DbUtil.close(rs2);
			DbUtil.close(rs);
		}
		return tableList;
		
	}
	
	
	/**
	 * 修改工资
	 * @param pch
	 * @param userId
	 * @param name
	 * @param value
	 * @return
	 */
	public String updateSalary(String pch,String userId,String name,String value){
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "UPDATE k_salary SET countValue=countValue-"+name+"+"+value+","+name+"='"+value+"' WHERE pch='"+pch+"' AND userId='"+userId+"'";
		String result = "";
		
		try {
			ps = conn.prepareStatement(sql);
			
			ps.execute();
			
			sql ="SELECT countValue FROM k_salary WHERE pch='"+pch+"' AND userId='"+userId+"'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				result = rs.getString("countValue");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		return result;
		
	}
	
	/**
	 * 人事部门修改工资
	 * @param pch
	 * @param userId
	 * @param name
	 * @param value
	 * @return
	 */
	public String updateSalaryByRs(String pch,String userId,String name,String value){
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		if (name.equals("v20")) {
			sql = "UPDATE k_salary SET v23=v23-"+name+"+"+value+","+name+"='"+value+"' WHERE pch='"+pch+"' AND userId='"+userId+"'";
		} else if (name.equals("v21") || name.equals("v22")){
			sql = "UPDATE k_salary SET v23=v23+"+name+"-"+value+","+name+"='"+value+"' WHERE pch='"+pch+"' AND userId='"+userId+"'";
		}
		
		String result = "";
		
		try {
			ps = conn.prepareStatement(sql);
			
			ps.execute();
			
			sql ="SELECT v23 FROM k_salary WHERE pch='"+pch+"' AND userId='"+userId+"'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				result = rs.getString("v23");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		return result;
		
	}
	
	/**
	 * 人事部门修改工资、扣税
	 * @param pch
	 * @param userId
	 * @param name
	 * @param value
	 * @return
	 */
	public String updateSalaryKouSui(String pch,String userId,String name,String value,String value2){
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "UPDATE k_salary SET v26=v23-"+value+"-"+value2+","+name+"='"+value+"' WHERE pch='"+pch+"' AND userId='"+userId+"'";
		
		String result = "";
		try {
			ps = conn.prepareStatement(sql);
			
			ps.execute();
			
			sql ="SELECT v26 FROM k_salary WHERE pch='"+pch+"' AND userId='"+userId+"'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				result = rs.getString("v26");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		return result;
		
	}
	
	/**
	 * 人事部门修改工资、计算实发工资
	 * @param pch
	 * @param userId
	 * @param name
	 * @param value
	 * @return
	 */
	public String updateSalaryShiFa(String pch,String userId,String name,String value,String value2,String value3){
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "UPDATE k_salary SET v30=v26-"+value+"-"+value2+"-"+value3+","+name+"='"+value+"' WHERE pch='"+pch+"' AND userId='"+userId+"'";
		
		String result = "";
		try {
			ps = conn.prepareStatement(sql);
			
			ps.execute();
			
			sql ="SELECT v30 FROM k_salary WHERE pch='"+pch+"' AND userId='"+userId+"'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				result = rs.getString("v30");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		return result;
		
	}
	
	/**
	 * 检查当前年月是否新初始化了工资
	 * @return
	 */
	public String getPch(){
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		String result = "";
		
		try {
			
			String sql ="SELECT DISTINCT pch FROM k_salary WHERE nowYear=YEAR(NOW()) AND nowMonth=MONTH(NOW())";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				result = rs.getString("pch");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		return result;
		
	}
	
	/**
	 *修改工资状态
	 * @param statusName
	 * @param pch
	 * @return
	 */
	public boolean updateStatus(String statusName,String pch){
		
		PreparedStatement ps = null;
		String sql = "UPDATE k_salary SET status='"+statusName+"' WHERE pch='"+pch+"'";
		boolean result = false;
		
		try {
			ps = conn.prepareStatement(sql);
			ps.execute();
			
			result = true;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		return result;
		
	}
	
	public static void main(String[] args) {
		String sql = "SELECT * FROM k_salary where nowYear=YEAR(NOW()) AND nowMonth=Month(now()) and userId='6735'";
		ASFuntion asf = new ASFuntion();
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			ResultSetMetaData RSMD = rs.getMetaData(); //得到所有的列
			int j = 1;
			while (rs.next()) {
				
				for (int i = 1; i <= RSMD.getColumnCount(); i++) {
					String lineName = RSMD.getColumnLabel(i).toLowerCase();//得到列明
					if(!"autoid".endsWith(lineName) && !"userid".endsWith(lineName) && 
						!"rankid".endsWith(lineName) && !"nowyear".endsWith(lineName) &&
						!"nowmonth".endsWith(lineName) && !"countvalue".endsWith(lineName)&& 
						!"departmentid".endsWith(lineName) && !"pch".endsWith(lineName) && 
						!"status".endsWith(lineName)){
						
							
						//判断得到的里面
						if(lineName.indexOf("n")>-1){
						//if(lineName.equals(asf.showNull("n"+j))){
							if(!"".equals(asf.showNull(rs.getString(lineName)))){
								
								j ++;
							}
						}
					}
				}
				
				 
				 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
			DbUtil.close(rs);
		}
		
	}
	
	
	/**
	 * 根据sql 得到一列值
	 * @param sql
	 * @return
	 */
	public String getValueBySql(String sql){
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		String value = "";
		
		
		try {
		
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				//value += rs.getString(1);
				value = rs.getString(1);
			}
			
		} catch (SQLException e) {
		
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return value;
	}
	
	/**
	 * 根据sql 得到userids
	 * @param 
	 * @return
	 */
	public String getUserIdsBySql(String sql){
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		PreparedStatement ps2 = null;
		ResultSet rs2 = null;
		String value = "";
		
		
		try {
		
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			ps2 = conn.prepareStatement(sql);
			rs2 = ps2.executeQuery();
			rs2.last(); 
	        int count   =   rs2.getRow();
	        int num = 1;
			while (rs.next()) {
				if  (count>1) {
					value += rs.getString(1) ;
					if (num < count) {
						value += ",";
						num ++;
					}
				} else {
					value = rs.getString(1);
				}
			}
			
		} catch (SQLException e) {
		
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return value;
	}
	
/**
 * 根据条件获取 工资对象(部门初始化工资)
 * @param year
 * @param month
 * @param departmentId
 * @param userIds
 * @return
 */
public List<Map> getSelaryList(String year,String month,String departmentId,String userIds){
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		
		List<Map> listSalary = new ArrayList<Map>();
		
		ASFuntion asf = new ASFuntion();
		
		try {
			
			String sql ="SELECT  a.*,b.name,b.rank,b.sex,b.loginid,c.departname  FROM k_salary a " +
								" left join k_user b on a.userid = b.id  \n" +
								" left join k_department c on a.departmentId = c.autoid \n" +
							    " WHERE 1=1 ";
			if(!"".equals(year)){
				sql +="  and a.nowYear="+year;
			}
			
			if(!"".equals(month)){
				sql +=" and a.nowMonth="+month;
			}
			
			if(!"".equals(departmentId)){
				sql +=" and a.departmentId="+departmentId;
			}
			
			if(!"".equals(userIds)){
				sql +=" and a.userId in ("+userIds +")";
			}
			
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				
				Map<Object, String> map = new HashMap<Object, String>();
                
				String userId = rs.getString("userId");
				String loginId = rs.getString("loginid");
				String autoId = rs.getString("autoid");
				String v20 = rs.getString("v20");    //其他补助
				String countValue = rs.getString("countValue");   //基础工资
				
				String gsSql = "SELECT SUM(totaltime) \n"
								+"FROM oa_timesreport \n"
								+"WHERE userid='"+loginId+"' and workdate like '"+year+"-"+month+"-%' or workdate like '"+year+"-0"+month+"-%' ";
				String gs="";
				try {
					gs = new DbUtil(conn).queryForString(gsSql);
					if (null == gs || gs.equals("")) {
						gs="0";
					}
				}  catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				String wqSql = "SELECT COUNT(DISTINCT workdate) FROM oa_timesreport \n" +
					           "WHERE worktype IN ('市外外勤' ,'室内外勤') \n" +
				               "AND userid='"+loginId+"' and workdate like '"+year+"-"+month+"-%' or workdate like '"+year+"-0"+month+"-%' ";
	            String wqDays="";   //外勤天数
	            try {
	            	wqDays = new DbUtil(conn).queryForString(wqSql);
	                    if (null == wqDays || wqDays.equals("")) {
	                    	wqDays="0";
	                    }
	            }  catch (Exception e) {
	                  // TODO Auto-generated catch block
	                  e.printStackTrace();
	            }
	            int wqbz = 0;
	            int gsgz = 0;
				map.put("gs",gs);
				map.put("wqDays",wqDays);
				if (null!=rs.getString("v9")&&!rs.getString("v9").equals("")) {
					wqbz = Integer.parseInt(rs.getString("v9")) * Integer.parseInt(wqDays);  //外勤补助
				}
				if (null!=rs.getString("v2")&&!rs.getString("v2").equals("")) {
					gsgz = Integer.parseInt(rs.getString("v2")) * Integer.parseInt(gs);  //工时工资
				}
				
				map.put("wqbz",wqbz+"");
				map.put("gsgz", gsgz+"");
				int srTotal = wqbz + gsgz;				
				if (null!=v20 && !v20.equals("") && null!=countValue && !countValue.equals("")) {
					srTotal += Integer.parseInt(v20) + Integer.parseInt(countValue); 
				}
				
				String Sql3 = "update k_salary set v23 = '"+srTotal+"'," +
									"v26 ='"+srTotal+"'," +
									"v30 ='"+srTotal+"'" +
				                    "where autoid = '"+autoId+"'";
				try {
					int rst = new DbUtil(conn).executeUpdate(Sql3);
					if (rst >0 ) {   //更新成功
						
					}
				}  catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}              
				              
				map.put("autoId",rs.getString("autoid"));
				map.put("userId",userId);
				map.put("sex",rs.getString("sex"));
				map.put("name",rs.getString("name"));
				map.put("departname",rs.getString("departname"));
				map.put("rank",rs.getString("rank"));
				map.put("nowMonth",rs.getString("nowMonth"));
				map.put("nowYear",rs.getString("nowYear"));
				map.put("countValue",rs.getString("countValue"));
				map.put("departmentId",rs.getString("departmentId"));
				map.put("pch",rs.getString("pch"));
				map.put("status",rs.getString("status"));
				
				map.put("n1",rs.getString("n1"));
				map.put("v1",rs.getString("v1"));
				map.put("n2",rs.getString("n2"));
				map.put("v2",rs.getString("v2"));
				map.put("n3",rs.getString("n3"));
				map.put("v3",rs.getString("v3"));
				map.put("n4",rs.getString("n4"));
				map.put("v4",rs.getString("v4"));
				map.put("n5",rs.getString("n5"));
				map.put("v5",rs.getString("v5"));
				
				map.put("n6",rs.getString("n6"));
				map.put("v6",rs.getString("v6"));
				map.put("n7",rs.getString("n7"));
				map.put("v7",rs.getString("v7"));
				map.put("n8",rs.getString("n8"));
				map.put("v8",rs.getString("v8"));
				map.put("n9",rs.getString("n9"));
				map.put("v9",rs.getString("v9"));
				map.put("v17",rs.getString("v17"));
				map.put("n17",rs.getString("n17"));
				
				map.put("v18",rs.getString("v18"));
				map.put("n18",rs.getString("n18"));
				map.put("v19",rs.getString("v19"));
				map.put("n19",rs.getString("n19"));
				map.put("v20",rs.getString("v20"));
				map.put("n20",rs.getString("n20"));
				map.put("v21",rs.getString("v21"));
				map.put("n21",rs.getString("n21"));
				map.put("v22",rs.getString("v22"));
				map.put("n22",rs.getString("n22"));
				
				map.put("v23",rs.getString("v23"));
				map.put("n23",rs.getString("n23"));
				map.put("v24",rs.getString("v24"));
				map.put("n24",rs.getString("n24"));
				map.put("v25",rs.getString("v25"));
				map.put("n25",rs.getString("n25"));
				map.put("v26",rs.getString("v26"));
				map.put("n26",rs.getString("n26"));
				map.put("v27",rs.getString("v27"));
				map.put("n27",rs.getString("n27"));
				
				map.put("v28",rs.getString("v28"));
				map.put("n28",rs.getString("n28"));
				map.put("v29",rs.getString("v29"));
				map.put("n29",rs.getString("n29"));
				map.put("v30",rs.getString("v30"));
				map.put("n30",rs.getString("n30"));
				map.put("c20",rs.getString("c20"));
				
				listSalary.add(map);
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
			DbUtil.close(rs);
		}
		return listSalary;
		
	}

		/**
		 * 修改旧月份工资方法
		 * @param year
		 * @param month
		 * @param departmentId
		 * @param userIds
		 * @return
		 */
		public boolean updateSalary(List<Salary> list,String year,String month,String departmentid){
			
			PreparedStatement ps = null;
			
			PreparedStatement ps2 = null;
			
			String sql = " DELETE from k_salary \n" +
	            			   " where nowYear='"+year+"' and nowMonth='"+month+"' \n" +
	            			   " and departmentid='"+ departmentid+"' ";
			boolean result = false;
			try {
				ps = conn.prepareStatement(sql);
				ps.execute();
				
				result = true;
				
			} catch (SQLException e) {
				e.printStackTrace();
			}finally{
				DbUtil.close(ps);
			}
			if (result) {
				for (int i = 0; i < list.size(); i++) {
					String sql2 = "INSERT INTO k_salary \n" +
							            "( userId,rankId,nowYear,nowMonth,countValue,departmentId,pch,status, \n" +
							            "n1,v1,c1,n2,v2,c2,n3,v3,c3,n4,v4,c4,n5,v5,c5, \n" +
							            "n6,v6,c6,n7,v7,c7,n8,v8,c8,n9,v9,c9,n10,v10,c10, \n" +
							            "n11,v11,c11,n12,v12,c12,n13,v13,c13,n14,v14,c14,n15,v15,c15, \n" +
							            "n16,v16,c16,n17,v17,c17,n18,v18,c18,n19,v19,c19,n20,v20,c20, \n" +
							            "n21,v21,c21,n22,v22,c22,n23,v23,c23,n24,v24,c24,n25,v25,c25, \n" +
							            "n26,v26,c26,n27,v27,c27,n28,v28,c28,n29,v29,c29,n30,v30,c30 )\n" +
							            "values('"+list.get(i).getUserId()+"','"+list.get(i).getRankId()+"','"+list.get(i).getNowYear()+"','"+list.get(i).getNowMonth()+"', \n" +
							            "'"+list.get(i).getCountValue()+"','"+list.get(i).getDepartmentId()+"','"+list.get(i).getPch()+"','"+list.get(i).getStatus()+"', \n" +
							            "'"+list.get(i).getN1()+"','"+list.get(i).getV1()+"','"+list.get(i).getC1()+"','"+list.get(i).getN2()+"','"+list.get(i).getV2()+"','"+list.get(i).getC2()+"','"+list.get(i).getN3()+"','"+list.get(i).getV3()+"','"+list.get(i).getC3()+"','"+list.get(i).getN4()+"','"+list.get(i).getV4()+"','"+list.get(i).getC4()+"','"+list.get(i).getN5()+"','"+list.get(i).getV5()+"','"+list.get(i).getC5()+"', \n" +
							            "'"+list.get(i).getN6()+"','"+list.get(i).getV6()+"','"+list.get(i).getC6()+"','"+list.get(i).getN7()+"','"+list.get(i).getV7()+"','"+list.get(i).getC7()+"','"+list.get(i).getN8()+"','"+list.get(i).getV8()+"','"+list.get(i).getC8()+"','"+list.get(i).getN9()+"','"+list.get(i).getV9()+"','"+list.get(i).getC9()+"','"+list.get(i).getN10()+"','"+list.get(i).getV10()+"','"+list.get(i).getC10()+"', \n" +
							            "'"+list.get(i).getN11()+"','"+list.get(i).getV11()+"','"+list.get(i).getC11()+"','"+list.get(i).getN12()+"','"+list.get(i).getV12()+"','"+list.get(i).getC12()+"','"+list.get(i).getN13()+"','"+list.get(i).getV13()+"','"+list.get(i).getC13()+"','"+list.get(i).getN14()+"','"+list.get(i).getV14()+"','"+list.get(i).getC14()+"','"+list.get(i).getN15()+"','"+list.get(i).getV15()+"','"+list.get(i).getC15()+"', \n" +
							            "'"+list.get(i).getN16()+"','"+list.get(i).getV16()+"','"+list.get(i).getC16()+"','"+list.get(i).getN17()+"','"+list.get(i).getV17()+"','"+list.get(i).getC17()+"','"+list.get(i).getN18()+"','"+list.get(i).getV18()+"','"+list.get(i).getC18()+"','"+list.get(i).getN19()+"','"+list.get(i).getV19()+"','"+list.get(i).getC19()+"','"+list.get(i).getN20()+"','"+list.get(i).getV20()+"','"+list.get(i).getC20()+"', \n" +
							            "'"+list.get(i).getN21()+"','"+list.get(i).getV21()+"','"+list.get(i).getC21()+"','"+list.get(i).getN22()+"','"+list.get(i).getV22()+"','"+list.get(i).getC22()+"','"+list.get(i).getN23()+"','"+list.get(i).getV23()+"','"+list.get(i).getC23()+"','"+list.get(i).getN24()+"','"+list.get(i).getV24()+"','"+list.get(i).getC24()+"','"+list.get(i).getN25()+"','"+list.get(i).getV25()+"','"+list.get(i).getC25()+"', \n" +
							            "'"+list.get(i).getN26()+"','"+list.get(i).getV26()+"','"+list.get(i).getC26()+"','"+list.get(i).getN27()+"','"+list.get(i).getV27()+"','"+list.get(i).getC27()+"','"+list.get(i).getN28()+"','"+list.get(i).getV28()+"','"+list.get(i).getC28()+"','"+list.get(i).getN29()+"','"+list.get(i).getV29()+"','"+list.get(i).getC29()+"','"+list.get(i).getN30()+"','"+list.get(i).getV30()+"','"+list.get(i).getC30()+"' )" ;
					result = false;
					
					try {
						ps2 = conn.prepareStatement(sql2);
						ps2.execute();
						
						result = true;
						
					} catch (SQLException e) {
						e.printStackTrace();
					}finally{
						DbUtil.close(ps2);
					}
				}
					
			}
			return result;
		}
		
		/**
		 * 人事部门审批方法
		 * @param year
		 * @param month
		 * @param departmentId
		 * @param userIds
		 * @return
		 */
		public boolean updateAudit(List<Salary> list,String year,String month,String departmentid){
			
			PreparedStatement ps = null;
			
			PreparedStatement ps2 = null;
			
			String sql = " DELETE from k_salary \n" +
	            			   " where nowYear='"+year+"' and nowMonth='"+month+"' \n" +
	            			   " and departmentid='"+ departmentid+"' ";
			boolean result = false;
			try {
				ps = conn.prepareStatement(sql);
				ps.execute();
				
				result = true;
				
			} catch (SQLException e) {
				e.printStackTrace();
			}finally{
				DbUtil.close(ps);
			}
			if (result) {
				for (int i = 0; i < list.size(); i++) {
					String sql2 = "INSERT INTO k_salary \n" +
							            "( userId,rankId,nowYear,nowMonth,countValue,departmentId,pch,status, \n" +
							            "n1,v1,c1,n2,v2,c2,n3,v3,c3,n4,v4,c4,n5,v5,c5, \n" +
							            "n6,v6,c6,n7,v7,c7,n8,v8,c8,n9,v9,c9,n10,v10,c10, \n" +
							            "n11,v11,c11,n12,v12,c12,n13,v13,c13,n14,v14,c14,n15,v15,c15, \n" +
							            "n16,v16,c16,n17,v17,c17,n18,v18,c18,n19,v19,c19,n20,v20,c20, \n" +
							            "n21,v21,c21,n22,v22,c22,n23,v23,c23,n24,v24,c24,n25,v25,c25, \n" +
							            "n26,v26,c26,n27,v27,c27,n28,v28,c28,n29,v29,c29,n30,v30,c30 )\n" +
							            "values('"+list.get(i).getUserId()+"','"+list.get(i).getRankId()+"','"+list.get(i).getNowYear()+"','"+list.get(i).getNowMonth()+"', \n" +
							            "'"+list.get(i).getCountValue()+"','"+list.get(i).getDepartmentId()+"','"+list.get(i).getPch()+"','"+list.get(i).getStatus()+"', \n" +
							            "'"+list.get(i).getN1()+"','"+list.get(i).getV1()+"','"+list.get(i).getC1()+"','"+list.get(i).getN2()+"','"+list.get(i).getV2()+"','"+list.get(i).getC2()+"','"+list.get(i).getN3()+"','"+list.get(i).getV3()+"','"+list.get(i).getC3()+"','"+list.get(i).getN4()+"','"+list.get(i).getV4()+"','"+list.get(i).getC4()+"','"+list.get(i).getN5()+"','"+list.get(i).getV5()+"','"+list.get(i).getC5()+"', \n" +
							            "'"+list.get(i).getN6()+"','"+list.get(i).getV6()+"','"+list.get(i).getC6()+"','"+list.get(i).getN7()+"','"+list.get(i).getV7()+"','"+list.get(i).getC7()+"','"+list.get(i).getN8()+"','"+list.get(i).getV8()+"','"+list.get(i).getC8()+"','"+list.get(i).getN9()+"','"+list.get(i).getV9()+"','"+list.get(i).getC9()+"','"+list.get(i).getN10()+"','"+list.get(i).getV10()+"','"+list.get(i).getC10()+"', \n" +
							            "'"+list.get(i).getN11()+"','"+list.get(i).getV11()+"','"+list.get(i).getC11()+"','"+list.get(i).getN12()+"','"+list.get(i).getV12()+"','"+list.get(i).getC12()+"','"+list.get(i).getN13()+"','"+list.get(i).getV13()+"','"+list.get(i).getC13()+"','"+list.get(i).getN14()+"','"+list.get(i).getV14()+"','"+list.get(i).getC14()+"','"+list.get(i).getN15()+"','"+list.get(i).getV15()+"','"+list.get(i).getC15()+"', \n" +
							            "'"+list.get(i).getN16()+"','"+list.get(i).getV16()+"','"+list.get(i).getC16()+"','"+list.get(i).getN17()+"','"+list.get(i).getV17()+"','"+list.get(i).getC17()+"','"+list.get(i).getN18()+"','"+list.get(i).getV18()+"','"+list.get(i).getC18()+"','"+list.get(i).getN19()+"','"+list.get(i).getV19()+"','"+list.get(i).getC19()+"','"+list.get(i).getN20()+"','"+list.get(i).getV20()+"','"+list.get(i).getC20()+"', \n" +
							            "'"+list.get(i).getN21()+"','"+list.get(i).getV21()+"','"+list.get(i).getC21()+"','"+list.get(i).getN22()+"','"+list.get(i).getV22()+"','"+list.get(i).getC22()+"','"+list.get(i).getN23()+"','"+list.get(i).getV23()+"','"+list.get(i).getC23()+"','"+list.get(i).getN24()+"','"+list.get(i).getV24()+"','"+list.get(i).getC24()+"','"+list.get(i).getN25()+"','"+list.get(i).getV25()+"','"+list.get(i).getC25()+"', \n" +
							            "'"+list.get(i).getN26()+"','"+list.get(i).getV26()+"','"+list.get(i).getC26()+"','"+list.get(i).getN27()+"','"+list.get(i).getV27()+"','"+list.get(i).getC27()+"','"+list.get(i).getN28()+"','"+list.get(i).getV28()+"','"+list.get(i).getC28()+"','"+list.get(i).getN29()+"','"+list.get(i).getV29()+"','"+list.get(i).getC29()+"','"+list.get(i).getN30()+"','"+list.get(i).getV30()+"','"+list.get(i).getC30()+"' )" ;

					result = false;
					
					try {
						ps2 = conn.prepareStatement(sql2);
						ps2.execute();
						
						result = true;
						
					} catch (SQLException e) {
						e.printStackTrace();
					}finally{
						DbUtil.close(ps2);
					}
				}
					
			}
			return result;
		}
	
		/**
		 * 工资发放方法
		 * @param year
		 * @param month
		 * @param departmentId
		 * @param userIds
		 * @return
		 */
		public boolean updateGive(List<Salary> list,String year,String month,String departmentid){
			
			PreparedStatement ps = null;
			
			PreparedStatement ps2 = null;
			
			String sql = " DELETE from k_salary \n" +
	            			   " where nowYear='"+year+"' and nowMonth='"+month+"' \n" +
	            			   " and departmentid='"+ departmentid+"' ";
			boolean result = false;
			try {
				ps = conn.prepareStatement(sql);
				ps.execute();
				
				result = true;
				
			} catch (SQLException e) {
				e.printStackTrace();
			}finally{
				DbUtil.close(ps);
			}
			if (result) {
				for (int i = 0; i < list.size(); i++) {
					String sql2 = "INSERT INTO k_salary \n" +
							            "( userId,rankId,nowYear,nowMonth,countValue,departmentId,pch,status, \n" +
							            "n1,v1,c1,n2,v2,c2,n3,v3,c3,n4,v4,c4,n5,v5,c5, \n" +
							            "n6,v6,c6,n7,v7,c7,n8,v8,c8,n9,v9,c9,n10,v10,c10, \n" +
							            "n11,v11,c11,n12,v12,c12,n13,v13,c13,n14,v14,c14,n15,v15,c15, \n" +
							            "n16,v16,c16,n17,v17,c17,n18,v18,c18,n19,v19,c19,n20,v20,c20, \n" +
							            "n21,v21,c21,n22,v22,c22,n23,v23,c23,n24,v24,c24,n25,v25,c25, \n" +
							            "n26,v26,c26,n27,v27,c27,n28,v28,c28,n29,v29,c29,n30,v30,c30 )\n" +
							            "values('"+list.get(i).getUserId()+"','"+list.get(i).getRankId()+"','"+list.get(i).getNowYear()+"','"+list.get(i).getNowMonth()+"', \n" +
							            "'"+list.get(i).getCountValue()+"','"+list.get(i).getDepartmentId()+"','"+list.get(i).getPch()+"','"+list.get(i).getStatus()+"', \n" +
							            "'"+list.get(i).getN1()+"','"+list.get(i).getV1()+"','"+list.get(i).getC1()+"','"+list.get(i).getN2()+"','"+list.get(i).getV2()+"','"+list.get(i).getC2()+"','"+list.get(i).getN3()+"','"+list.get(i).getV3()+"','"+list.get(i).getC3()+"','"+list.get(i).getN4()+"','"+list.get(i).getV4()+"','"+list.get(i).getC4()+"','"+list.get(i).getN5()+"','"+list.get(i).getV5()+"','"+list.get(i).getC5()+"', \n" +
							            "'"+list.get(i).getN6()+"','"+list.get(i).getV6()+"','"+list.get(i).getC6()+"','"+list.get(i).getN7()+"','"+list.get(i).getV7()+"','"+list.get(i).getC7()+"','"+list.get(i).getN8()+"','"+list.get(i).getV8()+"','"+list.get(i).getC8()+"','"+list.get(i).getN9()+"','"+list.get(i).getV9()+"','"+list.get(i).getC9()+"','"+list.get(i).getN10()+"','"+list.get(i).getV10()+"','"+list.get(i).getC10()+"', \n" +
							            "'"+list.get(i).getN11()+"','"+list.get(i).getV11()+"','"+list.get(i).getC11()+"','"+list.get(i).getN12()+"','"+list.get(i).getV12()+"','"+list.get(i).getC12()+"','"+list.get(i).getN13()+"','"+list.get(i).getV13()+"','"+list.get(i).getC13()+"','"+list.get(i).getN14()+"','"+list.get(i).getV14()+"','"+list.get(i).getC14()+"','"+list.get(i).getN15()+"','"+list.get(i).getV15()+"','"+list.get(i).getC15()+"', \n" +
							            "'"+list.get(i).getN16()+"','"+list.get(i).getV16()+"','"+list.get(i).getC16()+"','"+list.get(i).getN17()+"','"+list.get(i).getV17()+"','"+list.get(i).getC17()+"','"+list.get(i).getN18()+"','"+list.get(i).getV18()+"','"+list.get(i).getC18()+"','"+list.get(i).getN19()+"','"+list.get(i).getV19()+"','"+list.get(i).getC19()+"','"+list.get(i).getN20()+"','"+list.get(i).getV20()+"','"+list.get(i).getC20()+"', \n" +
							            "'"+list.get(i).getN21()+"','"+list.get(i).getV21()+"','"+list.get(i).getC21()+"','"+list.get(i).getN22()+"','"+list.get(i).getV22()+"','"+list.get(i).getC22()+"','"+list.get(i).getN23()+"','"+list.get(i).getV23()+"','"+list.get(i).getC23()+"','"+list.get(i).getN24()+"','"+list.get(i).getV24()+"','"+list.get(i).getC24()+"','"+list.get(i).getN25()+"','"+list.get(i).getV25()+"','"+list.get(i).getC25()+"', \n" +
							            "'"+list.get(i).getN26()+"','"+list.get(i).getV26()+"','"+list.get(i).getC26()+"','"+list.get(i).getN27()+"','"+list.get(i).getV27()+"','"+list.get(i).getC27()+"','"+list.get(i).getN28()+"','"+list.get(i).getV28()+"','"+list.get(i).getC28()+"','"+list.get(i).getN29()+"','"+list.get(i).getV29()+"','"+list.get(i).getC29()+"','"+list.get(i).getN30()+"','"+list.get(i).getV30()+"','"+list.get(i).getC30()+"' )" ;

					result = false;
					
					try {
						ps2 = conn.prepareStatement(sql2);
						ps2.execute();
						
						result = true;
						
					} catch (SQLException e) {
						e.printStackTrace();
					}finally{
						DbUtil.close(ps2);
					}
				}
					
			}
			return result;
		}
		
		/**
		 * 业务部门 删除方法
		 * @param year
		 * @param month
		 * @param departmentId
		 * @param userIds
		 * @return
		 */
		public boolean deleteByPch(String pch){
			
			PreparedStatement ps = null;
			
			String sql = " DELETE from k_salary \n" +
	            			   " where pch='"+pch+"'";
			boolean result = false;
			try {
				ps = conn.prepareStatement(sql);
				ps.execute();
				
				result = true;
				
			} catch (SQLException e) {
				e.printStackTrace();
			}finally{
				DbUtil.close(ps);
			}
			return result;
		}
		
		/**
		 * 删除同一年月。同一部门，同一项目的
		 * @param year
		 * @param month
		 * @param pchname
		 * @param departmentId
		 * @return
		 */
		public boolean deleteByPch(String year,String month,String pchname,String departmentId){
			
			PreparedStatement ps = null;
			
			String sql = " DELETE from k_salary where nowYear =? and nowMonth =? and  pchname = ? and departmentId = ?  ";
			boolean result = false;
			try {
				ps = conn.prepareStatement(sql);
				ps.setString(1, year);
				ps.setString(2, month);
				ps.setString(3, pchname);
				ps.setString(4, departmentId);
				ps.execute();
				
				result = true;
				
			} catch (SQLException e) {
				e.printStackTrace();
			}finally{
				DbUtil.close(ps);
			}
			return result;
		}
		
		/**
		 * 检查当年当月该部门是否已有录入工资的员工
		 * @param year
		 * @param month
		 * @param departmentId
		 * @return
		 */
		public boolean checkByNy(String year,String month,String departmentId){
			PreparedStatement ps = null;
			ResultSet rs = null;
			boolean result = true;
			String sql =" select * from k_salary where nowYear='"+year+"' and nowMonth='"+month+"' and departmentId='"+departmentId
					   +"' and status = '部门暂存'";
			try{
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					result = false;
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				DbUtil.close(rs);
				DbUtil.close(ps);
			}
			return result;
		}
		
		public void initDelete(String departmentId,String year,String month){
			PreparedStatement ps = null;
			String sql="delete from k_salary where nowYear="+year+" and nowMonth='"+month+"' and status='初始化失败'";
			try{
				ps = conn.prepareStatement(sql);
				ps.execute();
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				DbUtil.close(ps);
			}
		}
		/**
		 * 业务部门 删除方法,根据年月
		 * @param year
		 * @param month
		 * @param departmentId
		 * @param userIds
		 * @return
		 */
		public boolean deleteByNY(String year,String month,String departmentId){
			boolean ifdelelte = checkByNy(year,month,departmentId);
			PreparedStatement ps = null;
			
			String sql = " DELETE from k_salary \n" +
	            			   " where status ='初始化失败' and nowYear='"+year+"' and nowMonth='"+month+"' and departmentId="+departmentId;
			boolean result = true;
			if(ifdelelte){
				try {
					ps = conn.prepareStatement(sql);
					ps.execute();
					
					result = true;
					
				} catch (SQLException e) {
					e.printStackTrace();
				}finally{
					DbUtil.close(ps);
				}
			
		 }
			return result;
		}
		
	    
}
