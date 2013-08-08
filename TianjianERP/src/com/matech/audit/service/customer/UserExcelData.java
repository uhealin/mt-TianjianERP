package com.matech.audit.service.customer;

import java.io.PrintWriter;
import java.sql.*;
import java.text.*;
import java.util.Date;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.auditPeople.AuditPeopleService;
import com.matech.audit.service.auditPeople.model.AuditPeople;
import com.matech.audit.service.businessTake.BusinessTakeService;
import com.matech.audit.service.group.GroupService;
import com.matech.audit.service.project.BusinessProjectService;
import com.matech.audit.service.project.ProjectService;
import com.matech.audit.service.project.model.BusinessProject;
import com.matech.audit.service.project.model.Project;
import com.matech.framework.pub.autocode.DELAutocode;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.sys.UTILSysProperty;
import com.matech.framework.pub.util.ASFuntion;


public class UserExcelData {

	private Connection conn = null;

	public UserExcelData(Connection conn1) 
			throws Exception {
		this.conn = conn1;
	}

	public void newTable() throws Exception {
		delTable();
		ASFuntion asf = new ASFuntion();
//		String sql = "CREATE TABLE `tt_k_customer` (`DepartID` int(10) NOT NULL auto_increment,`DepartName` varchar(50) NOT NULL default '',`Address` varchar(100) default NULL,`Corporate` varchar(50) default NULL,`CountryCess` varchar(20) default NULL,`Email` varchar(20) default NULL,`TerraCess` varchar(20) default NULL,`EnterpriseCode` varchar(20) default NULL,`DepartDate` varchar(10) default NULL,`LoginAddress` varchar(100) default NULL,`LinkMan` varchar(40) default NULL,`Phone` varchar(20) default NULL,`BusinessBound` varchar(200) default NULL,`Property` varchar(10) NOT NULL default '',`Remark` varchar(200) default NULL,`BPR` varchar(20) default NULL,`register` varchar(200) default NULL,`stockowner` varchar(200) default '',`postalcode` varchar(10) default NULL,`fax` varchar(20) default NULL,`taxpayer` varchar(20) default NULL,PRIMARY KEY  (`DepartID`)) ENGINE=MyISAM DEFAULT CHARSET=gbk";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
//			String sql = "CREATE TABLE tt_k_customer like k_customer";
//			ps = conn.prepareStatement(sql);
//			ps.execute();
			String sql = "show create table k_customer";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				sql = rs.getString(2);
				sql = asf.replaceStr(sql.toLowerCase(), "k_customer".toLowerCase(), "tt_k_customer".toLowerCase());
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			System.out.println(sql);
			ps = conn.prepareStatement(sql);
			ps.execute();
			
			sql = "alter table `asdb`.`tt_k_customer` change `DepartID` `DepartID` int (10)  NOT NULL AUTO_INCREMENT ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			
			//特殊列：用于初始化项目
			sql = "alter table `asdb`.`tt_k_customer` add column `projectname` varchar (500)  NULL ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			sql = "alter table `asdb`.`tt_k_customer` add column `projectyear` varchar (100)  NULL ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			sql = "alter table `asdb`.`tt_k_customer` add column `auditpara` varchar (100)  NULL ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			sql = "alter table `asdb`.`tt_k_customer` add column `manageruser` varchar (100)  NULL ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			sql = "alter table `asdb`.`tt_k_customer` add column `businesscost` varchar (100)  NULL ";
			ps = conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("�½��û���ʱ���SQLִ��ʧ��" + e.getMessage(), e);
		} finally {
			DbUtil.close(ps);
		}

	}

	public void delTable() throws Exception {
		String sql = "DROP TABLE IF EXISTS `tt_k_customer`";
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.execute();
			sql = "DROP TABLE IF EXISTS `tt_k_userdef`";
			ps = conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("ɾ���û���ʱ���SQLִ��ʧ��" + e.getMessage(), e);
		} finally {
			if (ps != null)
				ps.close();
		}

	}

	public String [] getExcelRows(String [] tableRows,String [] excelAllRows ){
//		String []excelRows = new String [excelAllRows.length-1];
		int [] ii = new int [excelAllRows.length];
		for(int i=0;i<ii.length;i++){
			ii[i]=-1;
		}
		int num=0;
		for (int i = 0; i < excelAllRows.length-1; i++) {
			for (int j = 0; j < tableRows.length; j++) {
				if(excelAllRows[i].trim().equals(tableRows[j].trim())){
//					ii[num++]=i;
					break;
				}
				if(j==tableRows.length-1){
//					excelRows[num] = new String();
//					excelRows[num]=excelAllRows[i];
//					num++;
					ii[num]=i;
					num++;
				}
			}
		}
		String []excelRows = new String [num];
		for (int i = 0; i < ii.length; i++) {
			for (int j = 0; j < excelAllRows.length-1; j++) {
				if(ii[i]==j && ii[i]!=-1){
					excelRows[i]= new String();
					excelRows[i]= excelAllRows[j].trim();
					break;
				}
			}
		}
		
		org.util.Debug.prtOut("excelRows:="+excelRows.length);
		return excelRows;
	}
	
	public String [] newUserDef(String [] excelRows)throws Exception{
		PreparedStatement ps = null;
		String sql = "";
		try {
			String [] ud = new String[excelRows.length];
			String str = "";
			
			for (int i = 0; i < ud.length; i++) {
				ud[i] = new String();
				ud[i] = "value"+i;
				str +="`value"+i+"` varchar(100) default NULL,";
			}
			sql = "CREATE TABLE `tt_k_userdef` (`DepartID` int(10) NOT NULL auto_increment,`DepartName` varchar(500) NOT NULL default '',"+str+" PRIMARY KEY  (`DepartID`)) ENGINE=MyISAM DEFAULT CHARSET=gbk";
			ps = conn.prepareStatement(sql);
			ps.execute();
			
			return ud;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (ps != null)
				ps.close();
		}
	}
	
	public void CheckUpData(PrintWriter out,String [] excelRows,String departmentId)throws Exception{
		PreparedStatement ps = null;
		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		String sql = "";
		try {
			//修改客户的所属部门
			//加新部门
			sql = "INSERT INTO k_department (departname,parentid) " +
			"	SELECT DISTINCT a.departmentid AS departname,'555555' AS parentid " +
			"	FROM tt_k_customer a " +
			"	LEFT JOIN k_department b ON a.departmentid = b.departname " +
			"	WHERE b.autoid IS NULL AND ifnull(a.departmentId,'') <> '' ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			//修改部门ID
			sql = "UPDATE tt_k_customer a , k_department b SET a.departmentid = b.autoid WHERE a.departmentid = b.departname";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			//客户的所属部门为空，就以当前人所在部门为主
			sql = "UPDATE tt_k_customer set departmentid = '" + departmentId + "' where ifnull(departmentId,'') = '' ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			sql = "select count(*) from tt_k_customer where DepartName=''";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				if(rs.getInt(1)>0){ 
					out.println("<br>有[<font color=blue>"+rs.getInt(1)+"</font>]条客户资料的客户名称为空，这些记录被忽略!");
					out.flush();
				}
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			String temp = "";
//			if(!"".equals(departmentId)){
//				temp = " and a.departmentId = '"+departmentId+"' ";
//			}
			out.println("<br>检查同名客户...");
			out.flush();
			sql = "select a.DepartName,a.departid from k_customer a ,tt_k_customer b where a.DepartName=b.DepartName and a.Property=1 " + temp;
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			int iCount = 0;
			while(rs.next()){
				iCount ++;
				out.println("<br>[<font color=blue>("+rs.getString(2)+")"+rs.getString(1)+"</font>]的客户名重复，这条记录会更新客户资料!");
				out.flush();
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			if(iCount == 0) return;
			
			out.println("<br>正在更新的同名客户资料...");
			out.flush();
			
			sql = "update k_customer a join (" +
			"	select b.*,a.departid did from k_customer a ,tt_k_customer b " +
			"	where a.DepartName=b.DepartName and a.Property=1 and a.DepartName<>'' " + temp + 
			") b on a.departid = b.did " +
			"set " +
			"a.Property=if(ifnull(b.Property,'')='',a.Property,ifnull(b.Property,'')), " +
			
			"a.departcode=if(ifnull(b.departcode,'')='',a.departid,ifnull(b.departcode,'')) ," +
			"a.groupname=if(ifnull(b.groupname,'')='',a.groupname,ifnull(b.groupname,'')) ," +
			"a.departenname=if(ifnull(b.departenname,'')='',a.departenname,ifnull(b.departenname,'')) ," +
			"a.Corporate=if(ifnull(b.Corporate,'')='',a.Corporate,ifnull(b.Corporate,'')) , " +
			"a.taxpayer=if(ifnull(b.taxpayer,'')='',a.taxpayer,ifnull(b.taxpayer,'')) ," +
			"a.CountryCess=if(ifnull(b.CountryCess,'')='',a.CountryCess,ifnull(b.CountryCess,'')) , " +
			"a.TerraCess=if(ifnull(b.TerraCess,'')='',a.TerraCess,ifnull(b.TerraCess,'')) ," +
			
			"a.BPR=if(ifnull(b.BPR,'')='',a.BPR,ifnull(b.BPR,'')) , " +
			"a.register=if(ifnull(b.register,'')='',a.register,ifnull(b.register,'')) , " +
			"a.EnterpriseCode=if(ifnull(b.EnterpriseCode,'')='',a.EnterpriseCode,ifnull(b.EnterpriseCode,'')) , " +
			"a.DepartDate=if(ifnull(b.DepartDate,'')='',a.DepartDate,ifnull(b.DepartDate,'')) , " +
			"a.LoginAddress=if(ifnull(b.LoginAddress,'')='',a.LoginAddress,ifnull(b.LoginAddress,'')) , " +
			"a.stockowner=if(ifnull(b.stockowner,'')='',a.stockowner,ifnull(b.stockowner,'')) , " +
			
			"a.LinkMan=if(ifnull(b.LinkMan,'')='',a.LinkMan,ifnull(b.LinkMan,'')) , " +
			"a.fax=if(ifnull(b.fax,'')='',a.fax,ifnull(b.fax,'')) , " +
			"a.Phone=if(ifnull(b.Phone,'')='',a.Phone,ifnull(b.Phone,'')) , " +
			"a.Email=if(ifnull(b.Email,'')='',a.Email,ifnull(b.Email,'')) , " +
			"a.Address=if(ifnull(b.Address,'')='',a.Address,ifnull(b.Address,'')) , " +
			"a.postalcode=if(ifnull(b.postalcode,'')='',a.postalcode,ifnull(b.postalcode,'')) , " +
			
			"a.BusinessBound=if(ifnull(b.BusinessBound,'')='',a.BusinessBound,ifnull(b.BusinessBound,'')) , " +
			"a.Remark=if(ifnull(b.Remark,'')='',a.Remark,ifnull(b.Remark,'')) , " +
			"a.departmentid=if(ifnull(b.departmentid,'')='',a.departmentid,ifnull(b.departmentid,'')) ," +
			
			"a.customerShortName=if(ifnull(b.customerShortName,'')='',a.customerShortName,ifnull(b.customerShortName,'')) ," +
			"a.beforeName=if(ifnull(b.beforeName,'')='',a.beforeName,ifnull(b.beforeName,'')) ," +
			"a.hylx=if(ifnull(b.hylx,'')='',a.hylx,ifnull(b.hylx,'')) ," +
			"a.curname=if(ifnull(b.curname,'')='',a.curname,ifnull(b.curname,'')) ," +
			"a.parentName=if(ifnull(b.parentName,'')='',a.parentName,ifnull(b.parentName,'')) ," +
			"a.holding=if(ifnull(b.holding,'')='',a.holding,ifnull(b.holding,'')) ," +
			
			"a.iframework=if(ifnull(b.iframework,'')='',a.iframework,ifnull(b.iframework,'')) ," +
			"a.companyProperty=if(ifnull(b.companyProperty,'')='',a.companyProperty,ifnull(b.companyProperty,'')) ," +
			"a.plate=if(ifnull(b.plate,'')='',a.plate,ifnull(b.plate,'')) ," +
			"a.practitioner=if(ifnull(b.practitioner,'')='',a.practitioner,ifnull(b.practitioner,'')) ," +
			"a.fashion=if(ifnull(b.fashion,'')='',a.fashion,ifnull(b.fashion,'')) ," +
			"a.estate=if(ifnull(b.estate,'')='',a.estate,ifnull(b.estate,'')) ," +
			
			"a.approach=if(ifnull(b.approach,'')='',a.approach,ifnull(b.approach,'')) ," +
			"a.intro=if(ifnull(b.intro,'')='',a.intro,ifnull(b.intro,'')) ," +
			"a.mostly=if(ifnull(b.mostly,'')='',a.mostly,ifnull(b.mostly,'')) ," +
			"a.subordination=if(ifnull(b.subordination,'')='',a.subordination,ifnull(b.subordination,'')) ," +
			"a.businessbegin=if(ifnull(b.businessbegin,'')='',a.businessbegin,ifnull(b.businessbegin,'')) ," +
			"a.businessend=if(ifnull(b.businessend,'')='',a.businessend,ifnull(b.businessend,'')), " +
			"a.groupplate=if(ifnull(b.groupplate,'')='',a.groupplate,ifnull(b.groupplate,'')), " +
			"a.totalassets=if(ifnull(b.totalassets,'')='',a.totalassets,ifnull(b.totalassets,'')), " +
			"a.totalcurname=if(ifnull(b.totalcurname,'')='',a.totalcurname,ifnull(b.totalcurname,'')) " +
			"" ;
			
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			try{
				//检查tt_k_userdef 是否存在
				sql = "select 1 from tt_k_userdef where 1=2";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				DbUtil.close(rs);
				DbUtil.close(ps);
				
				sql = "insert into `k_userdef`  (`ContrastID`, `Name`, `Value`, `Property`) values (?,?,?,'cust')";
				ps2 = conn.prepareStatement(sql);
				
				sql = "select b.*,a.departid did " +
				" from k_customer a ,tt_k_customer b " +
				" where a.DepartName=b.DepartName " +
				" and a.Property=1 " +
				" and a.DepartName<>'' " + temp;
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					
					sql = "delete from `k_userdef` where ContrastID='"+rs.getString("did")+"'";
					ps1 = conn.prepareStatement(sql);
					ps1.execute();
					DbUtil.close(ps1);
					
					sql = "select * from tt_k_userdef where DepartName = '"+rs.getString("DepartName")+"'";
					ps1 = conn.prepareStatement(sql);
					rs1 = ps1.executeQuery();
					if(rs1.next()){
						for (int i = 0; i < excelRows.length; i++) {
							if(!"".equals(rs1.getString("value"+i))){
								ps2.setString(1, rs.getString("did"));
								ps2.setString(2, (excelRows[i]).trim());
								ps2.setString(3, rs1.getString("value"+i).replaceAll("'", "\\\\'"));
								ps2.addBatch();
							}
						}
					}
					DbUtil.close(rs1);
					DbUtil.close(ps1);
				}
				DbUtil.close(rs);
				DbUtil.close(ps);
				
				ps2.executeBatch();
				
			}catch (Exception e) {}
			out.println("更新的同名客户资料完成!");
			out.flush();
			
		} catch (Exception e) {
			System.out.println("SQL=|"+sql);
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public String CheckUpData()throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		String result = "";
		try {
			sql = "select count(*) from tt_k_customer where DepartName=''";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				if(rs.getInt(1)>0){ 
					result+="<br>有[<font color=blue>"+rs.getInt(1)+"</font>]条客户资料的客户名称为空，这些记录被忽略!";
				}
			}
			sql = "select a.DepartName from k_customer a ,tt_k_customer b where a.DepartName=b.DepartName and a.Property=1";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				result+="<br>[<font color=blue>"+rs.getString(1)+"</font>]的客户名重复，这条记录会更新客户资料!";
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			if (rs !=null) rs.close();
			if (ps != null)
				ps.close();
		}
	}
	
	public void updateData()throws Exception{
		PreparedStatement ps = null;
		PreparedStatement pss = null;
		ResultSet rs = null;
		String sql = "";
		try {	
			sql = "select DepartID,DepartDate from tt_k_customer";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			int num =0;
			String str="";
			while(rs.next()){
				try {
					str = rs.getString(1);
					int rint = rs.getInt(2);
					sql = "UPDATE tt_k_customer set DepartDate=DATE_FORMAT(DATE_ADD('1992-12-8',INTERVAL (DepartDate-33946.0) DAY),'%Y-%m-%d') where DepartID="+str+" and DepartDate !='' ";
					pss = conn.prepareStatement(sql);
					pss.execute();
				} catch (Exception e) {
					
				}				
			}
		}catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			if (rs !=null) rs.close();
			if (pss != null)pss.close();
			if (ps != null)ps.close();
		}
	}
	
	public void insertData(PrintWriter out,String [] excelRows,String departmentId)throws Exception{
		PreparedStatement ps = null;
		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		String sql = "";
		try {
			//insert into `s_config` (`sname`, `svalue`, `sautoid`) values('批量导入是否新建业务库','是','399');
			String isCreateDataBase =  UTILSysProperty.SysProperty.getProperty("批量导入是否新建业务库"); 
			System.out.println("批量导入是否新建业务库="+isCreateDataBase);
			
			int num =0;
			 //会计制度
			sql = "select IndustryID from k_industry where IsDefault=1";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				num = rs.getInt(1);
			}else{
				sql = "select IndustryID from k_industry order by IndustryID limit 1";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				if(rs.next()){
					num = rs.getInt(1);
				}
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			sql = "insert into `k_userdef`  (`ContrastID`, `Name`, `Value`, `Property`) values (?,?,?,'cust')";
			ps2 = conn.prepareStatement(sql);
			
			out.println("<br>开始更新新增客户...");
			out.flush();
			
			/**
			 * 修改k_dic参数
			 * "行业名称","hylx",
			 */
			String [] ctypes = new String[]{"组织机构性质","公司性质","所属板块","客户来源方式"};
			String [] values = new String[]{"iframework","companyProperty","plate","approach"};
//			updateKdic(ctypes,values);
			
			String temp = "";
//			if(!"".equals(departmentId)){
//				temp = " and b.departmentId = '"+departmentId+"' ";
//			}
			sql = "select a.* " +
			"from tt_k_customer a " +
			"left join k_customer b " +
			"on a.DepartName=b.DepartName and a.Property=1 " + temp + 
			"where b.DepartName is null and a.DepartName<>'' ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			DELAutocode t=new DELAutocode();
			CustomerService cm = new CustomerService(conn);
			while(rs.next()){
				String ss = t.getAutoCode("KHDH","");
				
				if(isCreateDataBase == null || "是".equals(isCreateDataBase)){
					//默认建库
					System.out.println("默认建库");
					cm.createDataBase(ss);	//新建库
				}
				
				out.println("<br>新增[<font color=blue>("+ss+")"+rs.getString("DepartName")+"</font>]客户资料...");
				out.flush();
				sql = "insert into k_customer (" +
						"DepartID, DepartName, VocationID, Property," +
						
						"departcode,groupname,departenname,corporate,taxpayer,countrycess,terracess," +
						"BPR,register,enterprisecode,departdate,loginaddress,stockowner," +
						"linkman,fax,phone,email,address,postalcode," +
						"businessbound,remark,departmentid," +
						"customerShortName,beforeName,hylx,curname,parentName,holding," +
						"iframework,companyProperty,plate,practitioner,fashion,estate," +
						"approach,intro,mostly,subordination,businessbegin,businessend," +
						"groupplate,totalassets,totalcurname " +
						
						") " +
						"select 	" +
						"'"+ss+"', DepartName, '"+num+"',Property," +
						"IF(IFNULL(departcode,'') = '','"+ss+"',IFNULL(departcode,'')) as departcode,groupname,departenname,corporate,taxpayer,countrycess,terracess," +
						"BPR,register,enterprisecode,departdate,loginaddress,stockowner," +
						"linkman,fax,phone,email,address,postalcode," +
						"businessbound,remark,departmentid," +
						"customerShortName,beforeName,hylx,curname,parentName,holding," +
						"iframework,companyProperty,plate,practitioner,fashion,estate," +
						"approach,intro,mostly,subordination,businessbegin,businessend," +
						"groupplate,totalassets,totalcurname " +
						
						"from tt_k_customer a " +
						"where a.DepartID='"+rs.getString("DepartID")+"' ";
//				System.out.println("while sql="+sql);
				ps1 = conn.prepareStatement(sql);
				ps1.execute();
				DbUtil.close(ps1);
					
				sql = "select * from tt_k_userdef where DepartName = '"+rs.getString("DepartName")+"'";
				ps1 = conn.prepareStatement(sql);
				rs1 = ps1.executeQuery();
				if(rs1.next()){
					for (int i = 0; i < excelRows.length; i++) {
						if(!"".equals(rs1.getString("value"+i))){
							ps2.setString(1, ss);
							ps2.setString(2, (excelRows[i]).trim());
							ps2.setString(3, rs1.getString("value"+i).replaceAll("'", "\\\\'"));
							ps2.addBatch();
						}
					}
				}
				DbUtil.close(rs1);
				DbUtil.close(ps1);
				out.println("客户资料更新完毕!");
				out.flush();
				
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			ps2.executeBatch();
			
			updateGroupName();//批量导入客户:修改k_customer 表的groupName 字段 
			
		} catch (Exception e) {
			System.out.println("SQL=|"+sql);
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public void insertData(String [] excelRows)throws Exception{
		PreparedStatement ps = null;
		PreparedStatement pss = null;
		ResultSet rs = null;
		ResultSet rss = null;
		String sql = "";
		Statement sm = null;
		try {
			int num =0;
			int count = 0;
			sql = "select IndustryID from k_industry where IsDefault=1";
			pss = conn.prepareStatement(sql);
			rs = pss.executeQuery();
			if(rs.next()){
				num = rs.getInt(1);
			}else{
				sql = "select IndustryID from k_industry limit 1";
				pss = conn.prepareStatement(sql);
				rs = pss.executeQuery();
				if(rs.next()){
					num = rs.getInt(1);
				}
			}

			sm = conn.createStatement();
			sql = "update k_customer a join (select a.*,b.departid did from tt_k_customer a left join k_customer b on a.DepartName=b.DepartName and a.Property=1 where b.DepartName is not null and a.DepartName<>'') b on a.departid = b.did set a.Address=b.Address, a.Corporate=b.Corporate, a.CountryCess=b.CountryCess, a.Email=b.Email, a.TerraCess=b.TerraCess,a.EnterpriseCode=b.EnterpriseCode, a.DepartDate=b.DepartDate, a.LoginAddress=b.LoginAddress, a.LinkMan=b.LinkMan, a.Phone=b.Phone, a.BusinessBound=b.BusinessBound, a.Property=b.Property, a.Remark=b.Remark, a.BPR=b.BPR, a.register=b.register, a.stockowner=b.stockowner, a.postalcode=b.postalcode, a.fax=b.fax, a.taxpayer=b.taxpayer";
			
//			sm.addBatch(sql);
//			count++;
			sm.execute(sql);
			sm.execute("Flush tables");
			
			sql = "select a.*,b.departid did from tt_k_customer a left join k_customer b on a.DepartName=b.DepartName and a.Property=1 where b.DepartName is not null and a.DepartName<>''";
			pss = conn.prepareStatement(sql);
			rs = pss.executeQuery();
			while(rs.next()){
				sql = "select * from tt_k_userdef where 1=2 ";
				pss = conn.prepareStatement(sql);
				rss = pss.executeQuery();
				ResultSetMetaData RSMD = rss.getMetaData();	
				if(RSMD.getColumnCount()>2){
					
					sql = "delete from `k_userdef` where ContrastID='"+rs.getString("did")+"'";
					pss = conn.prepareStatement(sql);
					pss.execute();
				
					sql = "select * from tt_k_userdef where DepartName = '"+rs.getString("DepartName")+"'";
					pss = conn.prepareStatement(sql);
					rss = pss.executeQuery();
					if(rss.next()){
						String temp = "insert into `k_userdef`  (`ContrastID`, `Name`, `Value`, `Property`) values ";
						for (int i = 0; i < excelRows.length; i++) {
							
							if(!"".equals(rss.getString("value"+i))){
								temp = temp + "('"+rs.getString("did")+"','"+(excelRows[i]).trim()+"','"+rss.getString("value"+i).replaceAll("'", "\\\\'")+"','cust'),";
							}
						}
						
						sql = temp.substring(0, temp.length()-1);
//						System.out.println("sql:" + sql);
	//					sm.addBatch(sql);
	//					count++;
	//					if (count % 200 == 0) {
	//						sm.executeBatch();
	//		            }
						
						sm.execute(sql);
					}
				}

			}
			sql = "select a.* from tt_k_customer a left join k_customer b on a.DepartName=b.DepartName and a.Property=1 where b.DepartName is null and a.DepartName<>''";
			pss = conn.prepareStatement(sql);
			rs = pss.executeQuery();
			DELAutocode t=new DELAutocode();
			CustomerService cm = new CustomerService(conn);
			while(rs.next()){
				String ss = t.getAutoCode("KHDH","");
				
				cm.createDataBase(ss);
				
				sql = "insert into `k_customer` (`DepartID`, `DepartName`, `VocationID`, `Address`, `Corporate`, `CountryCess`, `Email`, `TerraCess`, `EnterpriseCode`, `DepartDate`, `LoginAddress`, `LinkMan`, `Phone`, `BusinessBound`, `Property`, `Remark`, `BPR`, `register`, `stockowner`, `postalcode`, `fax`, `taxpayer`,groupname,departenname,departcode,departmentid ) " +
						"select 	'"+ss+"', `DepartName`, '"+num+"',`Address`, `Corporate`, `CountryCess`, `Email`, `TerraCess`, `EnterpriseCode`, `DepartDate`, `LoginAddress`, `LinkMan`, `Phone`, `BusinessBound`, `Property`, `Remark`, `BPR`, `register`, `stockowner`, `postalcode`, `fax`, `taxpayer`,groupname,departenname,departcode,departmentid from `tt_k_customer` a where a.DepartID='"+rs.getString("DepartID")+"' ";
//				sm.addBatch(sql);
//				count++;
//				if (count % 200 == 0) {
//					sm.executeBatch();
//	            }
				sm.execute(sql);
				sm.execute("Flush tables");
				
				sql = "select * from tt_k_userdef where 1=2 ";
				pss = conn.prepareStatement(sql);
				rss = pss.executeQuery();
				ResultSetMetaData RSMD = rss.getMetaData();	
				if(RSMD.getColumnCount()>2){
					
					sql = "select * from tt_k_userdef where DepartName = '"+rs.getString("DepartName")+"'";
					pss = conn.prepareStatement(sql);
					rss = pss.executeQuery();
					
					if(rss.next()){
						String temp = "insert into `k_userdef`  (`ContrastID`, `Name`, `Value`, `Property`) values ";
						for (int i = 0; i < excelRows.length; i++) {
							
							if(!"".equals(rss.getString("value"+i))){
								temp = temp + "('"+ss+"','"+(excelRows[i]).trim()+"','"+rss.getString("value"+i).replaceAll("'", "\\\\'")+"','cust'),";
							}
						}
						
						sql = temp.substring(0, temp.length()-1);
	//					System.out.println("sql:" + sql);
						sm.execute(sql);
	//					sm.addBatch(sql);
	//					count++;
	//					if (count % 200 == 0) {
	//						sm.executeBatch();
	//		            }
					}
				}
				
//				if (count % 200 == 0) {
//					sm.executeBatch();
//	            }
				
			}
			
//			if (count % 200 != 0) {
//				sm.executeBatch();
//            }
//			sm.executeBatch();

			
		}catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			delTable();
			if( rs != null)
				rs.close();
			if( rss != null)
				rss.close();
			if (ps != null)
				ps.close();
			if(pss != null)
				pss.close();
		}
	}
	
	/**
	 * 所属集团
	 *	批量导入客户:修改k_customer 表的groupName 字段 
	 *	逻辑：
	 *	1、备份k_group
	 *	2、把集团名称还原到客户表，并清空k_group
	 *	3、重新计算集团表和集团的上下级关系
	 *	4、根据客户的部门信息，得到集团的所属部门；一个集团多部门时，复制集团使部门与集团关系为一对一
	 *	5、根据备份k_group表，还原只有集团没有客户的集团数据
	 *	6、重算k_group的fullpath
	 */
	public void updateGroupName()throws Exception {
		PreparedStatement ps = null;
		String sql = "";
		try {
			//1、备份k_group
			sql = "DROP TABLE IF EXISTS tt_k_group ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			sql = "create table tt_k_group as " +
			"	select a.*,b.groupname as bgroupname from k_group a left join k_group b on a.parentid = b.groupid";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			//2、还原集团名称到客户表
			sql = "update k_customer a,k_group b set a.groupName = b.groupName  where a.groupname = b.groupid";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			//清空k_group
			sql = "TRUNCATE TABLE k_group";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			//3、重新计算集团表和集团的上下级关系
			sql = "insert into k_group(groupname,parentId,level) " +
			"	select distinct a.groupName,'0','1' " +
			"	from k_customer a left join k_group b " +
			"	on a.groupName=b.groupName " +
			"	where b.groupName is null " +
			"	and ifnull(a.groupName,'') <> '' " +
			"	and abs(a.groupname)=0";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			sql = "update k_customer a,k_group b set a.groupName=b.groupId where  a.groupName=b.groupName;";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);	
			
			//求出集团的上级客户
			sql = "UPDATE k_customer a, k_group b SET b.parentid = a.groupname WHERE a.departname = b.groupname AND IFNULL(a.groupname ,'') <> ''";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);	
			
			//求母公司在客户表的集团ID为空
			sql = "UPDATE k_customer a, k_group b  SET a.groupname = b.groupid WHERE a.departname = b.groupname AND IFNULL(a.groupname ,'') = ''";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);	
			
			//集团ID=上级集团ID
			sql = "UPDATE k_group SET parentid = 0 WHERE groupid = parentid";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			//从备份表还原客户表没有的集团
			sql = "insert into k_group(groupname,parentId,level,property) " +
			"	select distinct a.groupname,0,1,group_concat(a.groupid) " +
			"	from tt_k_group a " +
			"	left join k_group b on a.groupname = b.groupname " +
			"	where b.groupname is null " +
			"	group by a.groupname";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			//求出还原的集团parentid
			sql = "update k_group a,(" +
			"	select a.groupid as pid,b.groupid " +
			"	from k_group a,( " +
			"		select a.*,bgroupname " +
			"		from k_group a,tt_k_group b " +
			"		where concat(',',a.property,',') like concat('%,',b.groupid,',%') " +
			"		and b.bgroupname is not null" +
			"	) b where a.groupname = b.bgroupname " +
			") b " +
			"set a.parentid = b.pid " +
			"where a.groupid = b.groupid";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			
			new GroupService(conn).updateFullpath();//修改全路径
			
			//4、根据客户的部门信息，得到集团的所属部门；一个集团多部门时，复制集团使部门与集团关系为一对一
			//修改集团的所属部门
			//1、插入集团的所属部门
			sql = "UPDATE k_group a " +
			"	INNER JOIN ( " +
			"		SELECT groupName,GROUP_CONCAT(IF(IFNULL(departmentid,'')='',null,departmentid) ORDER BY a.departmentid) AS departmentid FROM ( " + 
			"			SELECT DISTINCT groupName,departmentid FROM k_customer WHERE groupName<>'' AND groupName<>'-1' ORDER BY groupName " +
			"		) a  " +
			"		GROUP BY  groupName " +
			"		ORDER BY groupName " +
			"	) b ON  a.groupId = b.groupName " +
			"	SET a.departmentid = b.departmentid";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			//插入备份集团的所属部门
			sql = "UPDATE k_group a " +
			"	INNER JOIN (  " +
			"		select a.groupid,GROUP_CONCAT(IF(IFNULL(b.departmentid,'')='',null,IFNULL(b.departmentid,'')) ORDER BY b.departmentid) as departmentid " +
			"		from k_group a,tt_k_group b  " +
			"		where concat(',',a.property,',') like concat('%,',b.groupid,',%') " +
			"		group by a.groupid " +
			"	) b ON  a.groupId = b.groupid " + 
			"	SET a.departmentid = b.departmentid";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			//2、得到每个集团的部门集合
			sql = "UPDATE k_group a " +
			"	INNER JOIN ( " +
			"		SELECT b.groupid,GROUP_CONCAT(DISTINCT IFNULL(a.departmentid,'') ORDER BY a.departmentid) AS departments " +
			"		FROM k_group a,k_group b " +
			"		WHERE  1=1 " +
			"		AND a.fullpath LIKE CONCAT(b.fullpath,'%') " +
			"		GROUP BY b.groupid	 " +
			"	) b ON a.groupId =b.groupId " +
			"	SET a.departments = b.departments";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			//3、放大部门集合到集团的每个节点上
			sql = "UPDATE k_group a,k_group b SET a.departments =b.departments WHERE b.parentid = 0 AND b.departments LIKE '%,%' AND a.fullpath LIKE CONCAT(b.fullpath ,'%')";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			sql = "UPDATE k_group SET property = NULL ";  //清空辅助字段的内容
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			//4、复制有多部门的集团，使部门与集团关系为一对一
			sql = "INSERT INTO k_group (groupname,LEVEL,property,departmentid,departments) " +
			"	SELECT a.groupname,a.level,a.groupid AS property,b.autoid,a.parentid " +
			"	FROM k_group a,( " +
			"		SELECT autoid,departname FROM k_department " +
			"		UNION  " +
			"		SELECT '-1','无部门' FROM k_department " +
			"	) b  " +
			"	WHERE 1=1 " +
			"	AND a.departments LIKE '%,%' " +
			"	AND CONCAT(',',a.departments,',') LIKE CONCAT('%,',b.autoid,',%')";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			//5、更新有多部门的集团的parentid
			sql = "UPDATE k_group SET parentid = departments WHERE departments = 0 AND property > ''"; 
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			sql = "UPDATE k_group a,k_group b SET a.parentid = b.groupid WHERE a.property > '' AND b.property > '' AND a.departmentid =b.departmentid AND a.departments = b.property";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			//6、更新有多部门的集团的fullpath
			sql = "DELETE FROM k_group WHERE departments LIKE '%,%'";//删除有多部门集团 
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			sql = "UPDATE  k_group SET departmentid = '' WHERE departmentid = '-1'"; //把-1的部门改为空部门
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			sql = "update k_group set parentid = 0 where groupid = parentid";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			sql = "update k_group set parentid = 0 where parentid is null";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			new GroupService(conn).updateFullpath();//修改全路径
			
			//7、更新客户的集团编号
			sql = "UPDATE k_customer a,k_group b  SET a.groupname = b.groupid WHERE a.departmentid = b.departmentid AND a.groupname = b.property";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			sql = "UPDATE k_group SET property = NULL ,departments = NULL";  //清空辅助字段的内容
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			
		} catch (Exception e) {
			System.out.println("SQL:"+sql);
			e.printStackTrace();
		}finally {
			DbUtil.close(ps);	
		}
		
	}
	
	//行业类型=行业名称 
	//组织机构性质
	//公司性质	
	//所属板块
	//客户资料来源途径
	public void updateKdic(String []ctypes,String []values)throws Exception{
		PreparedStatement ps = null;
		String sql = "";
		try{ 
			
			for (int i = 0; i < values.length; i++) {
				sql = "INSERT INTO k_dic(NAME,VALUE,ctype,userdata)  " +
				"	SELECT DISTINCT "+values[i]+" AS NAME,"+values[i]+" AS VALUE, ? AS ctype,0 AS userdata " +
				"	FROM k_dic a " +
				"	RIGHT JOIN (SELECT DISTINCT "+values[i]+" FROM tt_k_customer where ifnull("+values[i]+",'') <> '') b " +
				"	ON a.ctype = ? AND a.value = "+values[i]+" " +
				"	WHERE a.ctype IS NULL ";
				ps = conn.prepareStatement(sql);
				
				int ii = 1;
				ps.setString(ii++, ctypes[i]);
				ps.setString(ii++, ctypes[i]);
				ps.execute();
				DbUtil.close(ps);
			}
			
		} catch (Exception e) {
			System.out.println("SQL:"+sql);
			e.printStackTrace();
		}finally {
			DbUtil.close(ps);	
		}
	}
	
	
	//初始化项目:以【客户编号、项目年度、项目类型】来判断，当前年度是否已建立项目
	//1、判断是否需要初始化项目，【项目名称】不能为空，为空就不需要初始化项目
	//2、初始化项目：判断当年客户是否已建立项目，有就忽略，无就新增
	public boolean isAddProject()throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try {
			boolean bool = false;
			sql = "select 1 from tt_k_customer where ifnull(projectname,'') <> ''";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				bool = true;
			}
			return bool;
		} catch (Exception e) {
			System.out.println("SQL:"+sql);
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public void insertProject(PrintWriter out,String UserId)throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try {
			ASFuntion CHF = new ASFuntion();
			//项目名称不能为空
			//项目年度为空，默认为本年
			//项目类型、项目负责人可以为空
			String year = CHF.getCurrentDate().substring(0, 4);
			String strSql = "select a.*, \n" +
			"	b.projectname,if(ifnull(b.projectyear,'') ='',"+year+",b.projectyear) as projectyear ,b.auditpara,b.manageruser,if(ifnull(b.businesscost,'') = '',0,b.businesscost) as businesscost, \n" +
			"	c.projectid,ifnull(c.projectname,'') as cprojectname, \n" +
			"	ifnull(d.id,'') as userid \n" +
			"	from k_customer a \n" +
			"	inner join tt_k_customer b on a.DepartName=b.DepartName and a.Property=1 \n" +
			"	left join z_project c on a.departid = c.customerid and if(ifnull(b.projectyear,'') ='',"+year+",b.projectyear) = c.projectyear and b.auditpara = c.auditpara \n" +
			"	left join k_user d on b.manageruser = d.name \n" +
			"	where ifnull(b.projectname ,'') <>'' \n";
			
			//1、检查当年项目是否已存在，存在就显示提示信息
			out.println("<br>检查当年已存在的项目....");
			out.flush();
			sql = strSql + "	and c.projectid is not null ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				String DepartName = rs.getString("DepartName");
				String projectyear = rs.getString("projectyear");
				String cprojectname = rs.getString("cprojectname");
				String projectid = rs.getString("projectid");
				out.println("<br>客户[<font color=blue>"+DepartName+"</font>]"+projectyear+"年项目：<font color=blue>"+cprojectname+"("+projectid+")</font>已经存在，本客户的初始化项目被忽略!");
				out.flush();
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			out.println("<br>项目检查完毕");
			out.flush();
			//2、新增项目，并提示
			out.println("<br>开始新增初始化项目....");
			out.flush();
			
			ProjectService projectService  = new ProjectService(conn);
			BusinessProjectService bps = new BusinessProjectService(conn) ;
			BusinessTakeService bts = new BusinessTakeService(conn) ;
			
			Project project = null ;
			BusinessProject bp = null ;
			String projectUserState = CHF.showNull(UTILSysProperty.SysProperty.getProperty("项目承接启用流程控制")).trim();
			
			sql = strSql + "	and c.projectid is null ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				project = new Project();
				bp = new BusinessProject() ;
				
				String departid = rs.getString("departid");
				String departname = rs.getString("departname");
				String projectyear = rs.getString("projectyear");
				String auditpara = rs.getString("auditpara"); 
				String projectName = rs.getString("projectName");
				String managerUserId = rs.getString("userid");
				String departmentid = rs.getString("departmentId");
				String businesscost = rs.getString("businesscost");
				
				project.setCustomerId(departid);
				project.setAccPackageId(departid + projectyear);
				project.setAuditPara(auditpara);
				project.setProjectName(projectName);
				project.setAuditTimeBegin(projectyear + "-01-01");
				project.setAuditTimeEnd(projectyear + "-12-31");
				project.setProjectCreated(CHF.getCurrentDate()) ;
				project.setDepartmentId(departmentid) ;
				project.setProjectYear(projectyear);
				project.setProperty("未填") ;
				project.setAuditType("0");
				
				bp.setPayCustomerId(departid);
				bp.setEntrustCustomerId(departid);
				bp.setCustomerId(departid) ;
				bp.setProjectName(projectName) ;
				bp.setAuditpara(auditpara) ;
				bp.setManagerUserId(managerUserId) ;
				bp.setDepartmentId(departmentid) ;
				bp.setTypeId("0");
				bp.setBusinessCost(businesscost);
				
				if(!"是".equals(projectUserState)){
					// 建项后就生成委托号和报告文号
//					String WTBH = bts.getEntrustNumber(CHF.getCurrentDate().substring(0, 4)) ;
//
//					String BGWH = bts.getReportNumber(CHF.getCurrentDate().substring(0, 4)) ;
//					BGWH = CHF.getCurrentDate().substring(0, 4) + "-" + BGWH ;
					
					bp.setEntrustNumber("0");
					bp.setReportNumeber("0");
					
					bp.setProperty("业务项目接受审批完毕");
					// 设置项目状态 为 1
					project.setState("0") ;
				}else{
					project.setState("-8") ;
				}
				
				String projectId = projectService.save(project);
				
				//在业务项目表插入一条记录
				bp.setProjectID(projectId) ;
				bp.setCreator(UserId) ;
				bp.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())) ;
				bps.save(bp) ;
			
				AuditPeopleService aps = new AuditPeopleService(conn, projectId) ;
				AuditPeople auditPeople = new AuditPeople();
				auditPeople.setUserId(managerUserId);
				auditPeople.setRole("项目负责人");
				auditPeople.setIsAudit("1");
				auditPeople.setIsTarAndPro("1");
				auditPeople.setDepartmentId("0");
				aps.addOrUpdateAuditPeople(auditPeople) ;
				
				out.println("<br>客户[<font color=blue>"+departname+"</font>]"+projectyear.toString()+"年项目：<font color=blue>"+projectName+"("+projectId+")</font>，初始化项目成功!");
				out.flush();
			}
			
			out.println("<br>新增初始化项目完成");
			out.flush();
		} catch (Exception e) {
			System.out.println("SQL:"+sql);
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
	}
	
	public static void main(String[] args) throws Exception{ 
		Connection  conn = new DBConnect().getDirectConnect("");
		UserExcelData uu = new UserExcelData(conn);
		String [] ctypes = new String[]{"行业类型","组织机构性质","公司性质","所属板块","客户来源方式"};
		String [] values = new String[]{"hylx","iframework","companyProperty","plate","approach"};
		uu.updateKdic(ctypes,values);
		DbUtil.close(conn);	
	}
}

