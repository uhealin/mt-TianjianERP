package com.matech.audit.service.rectify;

import java.sql.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.pub.func.ASTextKey;
import com.matech.audit.service.accpackageext.AccPackageExtService;
import com.matech.audit.service.customer.CustomerService;
import com.matech.audit.service.dataupload.DisposeTableService;
//import com.matech.audit.service.manuaccount.ManuacCountService;
import com.matech.audit.service.project.ProjectService;
import com.matech.audit.service.project.model.Project;
import com.matech.audit.service.rectify.model.AnalsyeRectify;
import com.matech.audit.work.repair.Repair;
import com.matech.audit.work.uploadProcess.FrontProcessAction;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.autocode.*;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.UTILString;

public class RectifyService {

	private Connection conn = null;
	
	private String proId = "";	//项目编号
	private String tempTable = "";

	public String getTempTable() {
		return tempTable;
	}

	public void setTempTable(String tempTable) {
		this.tempTable = tempTable;
	}

	public String getProId() {
		return proId;
	}

	public void setProId(String proId) {
		this.proId = proId;
	}
	
	public RectifyService(Connection conn) {
		this.conn = conn;
	}
	
	//---------------------------------------------------------------------------------------------
	//以下是［上年审定数过渡］模块 
	//---------------------------------------------------------------------------------------------
	
	public void DelTempTable(String TabName) throws Exception {		
		PreparedStatement ps = null;
		try {			
			String sql = "DROP TABLE IF EXISTS `" + TabName + "`";
			ps = conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);	
		}
	}
	
	/**
	 * 
	 * @param AccPackageID	本年帐套ID
	 * @param ProjectID		本年项目ID
	 * @param oAccPackageID		上年帐套ID
	 * @param oProjectID		上年项目ID
	 */
	public String getExaminSQL(String AccPackageID,String ProjectID,String oAccPackageID,String oProjectID)throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String randomName1 = "tt_"+DELUnid.getNumUnid();
			String sql = "CREATE TABLE "+ randomName1 + 
				" select a.SubjectID,a.SubjectName," +
				" ifnull(b.SubjectfullName2,c.SubjectfullName) SubjectfullName," +
				" ifnull(b.direction2,c.direction1) direction,a.isleaf," +
				" case when a.isleaf=1 then (DebitTotalOcc6-CreditTotalOcc6)+(DebitTotalOcc1-CreditTotalOcc1)+(DebitTotalOcc2-CreditTotalOcc2)+(DebitTotalOcc4-CreditTotalOcc4)+(DebitTotalOcc5-CreditTotalOcc5) else 0 end  TotalOcc," +
				" ifnull(Balance,0) Balance" +
				" from z_accountrectify a " +
				" left join c_account b on a.projectid="+oProjectID+" and b.accpackageid='"+oAccPackageID+"' and b.submonth=12  and a.subjectid=b.subjectid " +
				" left join (" +
				" 	select SubjectID,SubjectName,SubjectfullName,isleaf,level0,case substring(property,2,1) when 2 then '-1' else substring(property,2,1) end direction1 from c_accpkgsubject where accpackageid='"+oAccPackageID+"' " +
				" 	union " +
				" 	select SubjectID,SubjectName,SubjectfullName,isleaf,level0,case substring(property,2,1) when 2 then '-1' else substring(property,2,1) end direction1 from z_usesubject where projectid="+oProjectID+" and accpackageid='"+oAccPackageID+"' " +
				" ) c on a.projectid="+oProjectID+" and c.isleaf=1 and a.SubjectID = c.SubjectID " +
				" where c.SubjectID is not null" ;
			ps = conn.prepareStatement(sql);
			ps.execute();
			
			String randomName2 = "tt_"+DELUnid.getNumUnid();
			
			sql = "CREATE TABLE "+ randomName2 + 
				"		select ifnull(b.subjectid,a.subjectid) oSubjectID," +
				"		ifnull(b.accname,a.subjectname) accname," +
				"		ifnull(b.SubjectfullName2,c.SubjectfullName) SubjectfullName2," +
				"		ifnull(b.direction2,c.direction1) direction1," +
				" 		case when a.isleaf=1 then (DebitTotalOcc6-CreditTotalOcc6) else 0 end  TotalOcc1," +
				"		ifnull(b.DebitRemain+b.CreditRemain,0) remain" +
				"		from z_accountrectify a " +
				"		left join c_account b on a.accpackageid='"+AccPackageID+"' and b.accpackageid='"+AccPackageID+"' and b.submonth=1 and a.subjectId=b.subjectId" +
				"		left join (" +
				"			select SubjectID,SubjectName,SubjectfullName,isleaf,level0,case substring(property,2,1) when 2 then '-1' else substring(property,2,1) end direction1 from c_accpkgsubject where accpackageid='"+AccPackageID+"' " +
				"			union " +
				"			select SubjectID,SubjectName,SubjectfullName,isleaf,level0,case substring(property,2,1) when 2 then '-1' else substring(property,2,1) end direction1 from z_usesubject where projectid="+ProjectID+" and accpackageid='"+AccPackageID+"' " +
				"		) c on a.projectid="+ProjectID+" and c.isleaf=1 and a.SubjectID = c.SubjectID " +
				"		where a.projectid='"+ProjectID+"' and b.isleaf1=1" ;
				
			ps = conn.prepareStatement(sql);
			ps.execute();
			
			String randomName3 = "tt_"+DELUnid.getNumUnid();
			sql = "CREATE TABLE "+ randomName3 + 
				" select a.*, ifnull(TotalOcc,0)+ifnull(Balance,0) bal,ifnull(TotalOcc,0)+ifnull(Balance,0)-(ifnull(remain,0)+ifnull(TotalOcc1,0)) occ" +
				" from (" +
				" select * from "+ randomName1+" a" +
				" left join "+ randomName2+" b on a.SubjectfullName = b.SubjectfullName2" +
				" union " +
				" select * from "+ randomName1+" a" +
				" right join "+ randomName2+" b on a.SubjectfullName = b.SubjectfullName2" +
				" ) a " +
				" where abs(ifnull(TotalOcc,0))+abs(ifnull(Balance,0)) + abs(ifnull(remain,0)) + abs(ifnull(TotalOcc1,0))>0 " +
				" order by subjectid,osubjectid" ;
			ps = conn.prepareStatement(sql);
			ps.execute();
			
			DelTempTable(randomName1);	
			DelTempTable(randomName2);
			
			return randomName3;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public int  isAccpackageID(String AccpackageID)throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select 1 from asdb.z_project where accpackageid = "+AccpackageID+"-1";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				sql = "select 1 from asdb.c_accpackage where accpackageid = "+AccpackageID+"-1";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				if(rs.next()){
					return 0;
				}else{
					return 2;
				}
			}else {
				return 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 3;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	//---------------------------------------------------------------------------------------------
	//以上是［上年审定数过渡］模块
	//---------------------------------------------------------------------------------------------
	

	/**
	 * 
	 * @param acc
	 * @param projectID
	 * @param SubjectID
	 * @return 0:可以新增单笔调整或新增科目； 1:不能新增单笔调整；2：不能新增科目
	 * @throws Exception
	 */
	public int isRestrict(String acc, String projectID, String SubjectID) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		int number = 0;
		try {

			Repair repair = new Repair(conn);
			boolean bool = repair.isConfig();
			if(bool){
				bool = repair.isRepair(acc, projectID);
				if(bool){
					return 0;
				}
			}
		
			
			sql = "select * from z_usesubject where accpackageid='" + acc
					+ "' and projectid='" + projectID + "' and parentsubjectid='"
					+ SubjectID + "'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				number = 1;
			} else {
				sql = "select * from z_subjectentryrectify a left join z_assitementryrectify b on autoid = entryid where a.accpackageid='"
						+ acc
						+ "' and a.projectid='"
						+ projectID
						+ "' and a.subjectid='"
						+ SubjectID + "' and substring(vchdate,5) ='-12-31' and b.accpackageid is null";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				if (rs.next()) {
					number = 2;
				}
			}
			return number;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}

	public String getNewID(String oldId1, String oldId2 , String opt) {
		//oldId1 = 5181Z,oldId2 = 5181
		System.out.println("前："+oldId1 + "|" + oldId2);
		if(oldId1 == null || "".equals(oldId1)){
			oldId1 = oldId2;
		}
		if(oldId1.equals(oldId2)){
			return oldId1 + "A";
		}else {
			String id = oldId1.substring(oldId2.length());
			String id2 = "";
			int length = id.length();
			
			if(length > 1){
				id2 = id.substring(0, id.length() - 1);
				id = id.substring(id.length() - 1);
			}
			boolean bFound =false;
			for(int ch='A';ch<='Z';ch++)  {
				if(String.valueOf((char)ch).equals(id)){
					if((ch+1) > 'Z'){
						return getNewID( oldId1,  oldId1, opt );
					}else{
						id = String.valueOf((char)(ch+1));
					}
					
					bFound=true;
					break;
				}		
			}
			if (!bFound){
				return oldId1 + "A";
			}
			
			System.out.println("后："+oldId1 + "|" + oldId2 + id2 + id);
			return oldId2 + id2 + id;
		}
	}

	public String getNewID1(String OldID, String parent, String opt) {
		String newID = "";
		
		try{
			if ("0".equals(opt)) {
				newID = OldID + "01";
			} else {
				if (!OldID.equals(parent)) {
					String oid = OldID.substring(parent.length());
					String o1 = OldID.substring(parent.length(),parent.length() + 1);
					String o2 = OldID.substring(OldID.length() - 1, OldID.length());
					String oo1 = "";	//前分隔符；
					String oo2 = "";	//后分隔符；
					try {
						Integer.parseInt(o1);
					} catch (Exception e) {
						oo1 = o1;
					}
					try {
						Integer.parseInt(o2);
					} catch (Exception e) {
						oo2 = o2;
					}
					if ("".equals(oo1) && "".equals(oo2)) {
						//没有分隔符的情况下，就是自动加1；
						oid = String.valueOf((Integer.parseInt(oid) + 1));
					} else {
	//						System.out.println("ss+ss...sss".replaceAll("\\+", ""));
	//						System.out.println("ss+ss...s*ss".replaceAll("\\.", ""));
	//						System.out.println("ss+ss...s*ss".replaceAll("\\*", ""));
	//						System.out.println("ss+ss??.s*ss".replaceAll("\\?", ""));
	//						System.out.println("ss+s\\s??.s*ss".replaceAll("\\\\", ""));
						String so1 = oo1;
						String so2 = oo2;
						if(".".equals(oo1) || "+".equals(oo1) || "*".equals(oo1) || "?".equals(oo1) || "-".equals(oo1)){
							so1 = "\\" + oo1;
						}
						if(".".equals(oo2) || "+".equals(oo2) || "*".equals(oo2) || "?".equals(oo2) || "-".equals(oo2)){
							so2 = "\\" + oo2;
						}
	//						 System.out.println("11111 == .99".replace(".","").replace(oo2, ""));
						
						try{
							oid = String.valueOf((Integer.parseInt(oid.replaceAll(so1,"").replaceAll(so2, "")) + 1));
						}catch(Exception e){
							try{
								oid = oid.replaceAll(so1,"").replaceAll(so2, "");
								//2147483648   
								oid=oid.substring(oid.length()-"147483648".length(),oid.length());
								oid=String.valueOf((Integer.parseInt(oid) + 1));
							}catch(Exception e1){
								oid=oid.substring(1,oid.length()-2)+"1";	
							}
							
						}
						
					}
	
					if (oid.length() < 2) {
						oid = "0" + oid;
					}
	
					newID = parent + oo1 + oid + oo2;
				} else {
					newID = OldID + "01";
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		return newID;
	}

	public String insertData(String acc, String proid, String assName,String parentID) throws Exception {
		return insertData( acc,  proid,  assName, parentID, 1);
	}
	/**
	 * 负值重分类
	 * @param acc
	 * @param proid
	 * @param assName
	 * @param parentID
	 * @return
	 * @throws Exception
	 */
	public String insertData(String acc, String proid, String assName,String parentID,int direction) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		PreparedStatement pss = null;
		ResultSet rs = null;
		String sql = "";
		String result = "";
		try {

			String vocationid = new CustomerService(conn).getCustomer(acc.substring(0,6)).getVocationId();		//会计制度
			
			/**
			 * 检查c_accpkgsubject 是否存在 assName 的值
			 */
			sql = "select * from c_accpkgsubject where accpackageid = '"+acc+"' and ParentSubjectId = '"+parentID+"' and  subjectname='"+assName+"'  order by subjectid asc ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				return rs.getString("subjectid");
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			sql = "select * from z_usesubject where projectid='"+proid+"' and subjectname='"+assName+"' and ParentSubjectId = '"+parentID+"' order by subjectid asc  limit 1 ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				return rs.getString("subjectid");
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			sql = "insert into z_usesubject(projectID,accpackageID,subjectID,ParentSubjectId,"
					+ "TipSubjectId,SubjectName,SubjectFullName,`level0`,Property,isleaf) values (?,?,?,?,?,?,?,?,?,?)";
			pss = conn.prepareStatement(sql);

			sql = "update z_usesubject set isleaf=0 where accpackageid='" + acc
					+ "' and projectid='" + proid + "' and SubjectId='"
					+ parentID + "'";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			pss.setString(1, proid);
			pss.setString(2, acc);
			
			pss.setString(4, parentID);
			pss.setString(6, assName);

			pss.setInt(10, 1);

			String s1 = "";
			int level0 = 0;
			String opt = "",IsLeaf = "";
			sql = "select * from (select subjectid,ParentSubjectId,SubjectName,SubjectFullName,IsLeaf,level0,Property,'0' as opt from c_accpkgsubject where accpackageid='"
					+ acc
					+ "' union select subjectid,ParentSubjectId,SubjectName,SubjectFullName,IsLeaf,level0,Property,'1' as opt from z_usesubject where accpackageid='"
					+ acc
					+ "' and projectid='"
					+ proid
					+ "') a where subjectid = '" + parentID + "' ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {//找到上级科目
				level0 = rs.getInt("level0");
				s1 = rs.getString("SubjectFullName");
				opt = rs.getString("opt");
				IsLeaf = rs.getString("IsLeaf"); 
				pss.setString(7, s1 + "/" + assName);
				pss.setInt(8, rs.getInt("level0") + 1);
				pss.setString(9, rs.getString("Property"));

			}else{	//找不到，就是新增一级科目
				
				sql = "select * from asdb.k_standsubject where vocationid = "+vocationid+" and subjectfullname = '"+assName+"' ";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				if (rs.next()) {
					pss.setString(7, rs.getString("SubjectFullName"));
					pss.setInt(8, rs.getInt("level0"));
					pss.setString(9, "0"+rs.getString("Property"));
				}else{
					pss.setString(7, assName);
					pss.setInt(8, 1);
					if(direction == -1){
						pss.setString(9, "02");
					}else{
						pss.setString(9, "01");
					}
				}
				
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			if(!"".equals(s1)){
//				找到上级科目
				if(level0 == 1 && "1".equals(opt) ){
					sql = "select * from z_usesubject where accpackageid='"
						+ acc
						+ "' and projectid='"
						+ proid
						+ "' and tipsubjectID = '"+parentID+"' order by length(subjectid) desc,subjectid desc limit 1";
				}else{
					sql = "select * from z_usesubject where accpackageid='"
						+ acc
						+ "' and projectid='"
						+ proid
						+ "' and parentsubjectID = '"+parentID+"' order by length(subjectid) desc,subjectid desc limit 1";
				}
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				if (rs.next()) {
	
					pss.setString(3, result = getNewID(rs.getString("subjectid"),parentID, "1"));
					pss.setString(5, rs.getString("TipSubjectId"));
	
				} else {
					
					sql = "select * from c_accpkgsubject where accpackageid='"
							+ acc
							+ "' and (select SubjectFullName from c_accpkgsubject where accpackageid='"
							+ acc + "' and subjectid='" + parentID
							+ "' ) like concat(SubjectFullName,'%') and level0=1 ";
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					if (rs.next()) {
						
						pss.setString(5, rs.getString("SubjectId"));
//						sql = "select * from c_accpkgsubject where accpackageid='"+acc+"' and  SubjectFullName like concat('"+s1+"','%') and level0 = "+(level+1)+" order by subjectid desc limit 1";
//						System.out.println("aaaaaaaaa:"+sql);
//						ps = conn.prepareStatement(sql);
//						rs = ps.executeQuery();
//						if(rs.next()){ 
//							System.out.println("aaaaaaaa rs:"+rs.getString("subjectid"));
//							pss.setString(3, result = getNewID(rs.getString("subjectid"), parentID, "1"));
//						}else{
						DbUtil.close(rs);
						DbUtil.close(ps);

							sql = "select * from c_accpkgsubject where accpackageid='"+acc+"' and  SubjectFullName like concat('"+s1+"','%') and isleaf = 1 order by length(subjectid) desc,subjectid desc limit 1";
							ps = conn.prepareStatement(sql);
							rs = ps.executeQuery();
							if(rs.next()){
								pss.setString(3, result = getNewID(rs.getString("subjectid"), parentID, "1"));
							}
							DbUtil.close(rs);
							DbUtil.close(ps);
							
//						}
					}else{
						
						sql = "select * from z_usesubject where accpackageid='"
							+ acc
							+ "' and projectid='"
							+ proid
							+ "' and subjectID = '"+parentID+"' order by length(subjectid) desc,subjectid desc limit 1";
						ps = conn.prepareStatement(sql);
						rs = ps.executeQuery();
						if (rs.next()) {
							pss.setString(3, result = getNewID(rs.getString("subjectid"), parentID, "1"));
							pss.setString(5, rs.getString("tipSubjectId"));
						}
						DbUtil.close(rs);
						DbUtil.close(ps);
					}
				}
				DbUtil.close(rs);
				DbUtil.close(ps);
			}else{
//				找不到，就是新增一级科目
				String oldId1 = "",oldId2 = "";
				
				/**
				 * 先从标准科目中找一级
				 * 1、找到：就去标准科目的编号，比较一下用户账上有没有，有就从新生成，没有就用这个编号
				 * 2、找不到：原来的方式生成科目编号
				 * 如果新增的是一级科目{
				 * 	   如果新增的是损益科目，要按账上的损益所在的实际科目序号来作为1级序号
				 * 	   如果新增的不是损益科目，按标准科目编号+英文序号方式
				 * 	}
				 */
				
				sql = "select * from asdb.k_standsubject where vocationid = "+vocationid+" and subjectfullname = '"+assName+"' ";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				if (rs.next()) {
					//找到一级标准科目
					oldId1 = rs.getString("SubjectID");
					DbUtil.close(rs);
					DbUtil.close(ps);
					
					oldId2 = oldId1;
					//比较是否已经存在
					while(true){
						sql = "select 1 from c_accpkgsubject where accpackageid='"+acc+"' and subjectid = '"+oldId2+"' " +
						"union " +
						"select 1 from z_usesubject where projectid='"+proid+"' and subjectid = '"+oldId2+"' ";
						ps = conn.prepareStatement(sql);
						rs = ps.executeQuery();
						if(rs.next()){
//							101 101,101A,101B 
//							oldId1 = "101",oldId2 = "101";
//							getNewID( oldId1,  oldId2 ,"")=101A;oldId1 = "101",oldId2 = "101A";
//							oldId1 = "101",oldId2 = "101A";
//							getNewID( oldId1,  oldId2 ,"")=101B;oldId1 = "101",oldId2 = "101B";
//							oldId1 = "101",oldId2 = "101B";
//							getNewID( oldId1,  oldId2 ,"")=101C;oldId1 = "101",oldId2 = "101C";
							
							oldId2 = getNewID( oldId1,  oldId2 ,"");
						}else{
							//账上科目不存在
							result = oldId2;
							break;
						}
					}
				}else{
					DbUtil.close(rs);
					DbUtil.close(ps);
					
					//找不到一级标准科目 
					sql = "	select distinct subjectid  " +
					"	from c_accpkgsubject " +
					"	where ParentSubjectId = '"+parentID+"' " +
					"	and accpackageid='"+acc+"' " +
					"	order by length(subjectid) desc,subjectid desc limit 1" ;
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					if(rs.next()){
						oldId2 = rs.getString(1);
					}
					DbUtil.close(rs);
					DbUtil.close(ps);
					
					
					sql = "	select distinct subjectid " +
					"	from z_usesubject " +
					"	where projectid='"+proid+"' " +
					"	and  ParentSubjectId = '"+parentID+"'" +
					"	order by length(subjectid) desc,subjectid desc limit 1 ";
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					if (rs.next()) {
						oldId1 = rs.getString(1);
					}
					DbUtil.close(rs);
					DbUtil.close(ps);
					result = getNewID( oldId1,  oldId2 ,"");
				}
				pss.setString(3, result);
				pss.setString(5, result);
				
				
			}
			
			System.out.println(result+"|"+ acc+"|"+ proid+"|"+ assName+"|"+ parentID+"|"+direction);
			pss.execute();
			
			return result;
		} catch (Exception e) {
			System.out.println("error SQL:"+sql);
			e.printStackTrace();
			return "";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(pss);
		}

	}


	public void insertData(String acc, String proid, String strOpts,
			String strSubs, String strOpps) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		PreparedStatement pss = null;
		ResultSet rs = null;
		try {

			//org.util.Debug.prtOut("insertData :=" + strOpts);
			//org.util.Debug.prtOut("insertData :=" + strSubs);
			//org.util.Debug.prtOut("insertData :=" + strOpps);

			String[] sqlOpts = strOpts.split("\\|");
			String[] sqlSub = strSubs.split("\\|");
			String[] sqlOpp = strOpps.split("\\|");

			String[][] sqlBoth = new String[sqlSub.length][3];
			String[] sqlOpt = new String[sqlSub.length];
			String str = "";
			for (int i = 0; i < sqlSub.length; i++) {
				sqlBoth[i][0] = new String();
				sqlBoth[i][1] = new String();
				sqlBoth[i][2] = new String();
				sqlOpt[i] = new String();
				if ("".equals(sqlSub[i]) || " ".equals(sqlSub[i])) {
					sqlBoth[i][0] = "";
					sqlBoth[i][1] = "";
					sqlBoth[i][2] = "";
					sqlOpt[i] = "1";
					continue;
				}
				sqlBoth[i][0] = sqlSub[i];
				sqlBoth[i][1] = sqlOpp[i];
				sqlBoth[i][2] = sqlOpts[i];
				sqlOpt[i] = "0";
				str += "'" + sqlOpp[i] + "',";
			}
			str = str.substring(0, str.length() - 1);
			//org.util.Debug.prtOut("str :=|" + str);

			String sql = "select * from (select subjectid from c_accpkgsubject where accpackageid='"
					+ acc
					+ "' union select subjectid from z_usesubject where accpackageid='"
					+ acc
					+ "' and projectid='"
					+ proid
					+ "') a  where subjectid in (" + str + ")";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			while (rs.next()) {
				String s = rs.getString("subjectid");
				for (int i = 0; i < sqlSub.length; i++) {
					if (sqlBoth[i][1].equals(s)) {
						sqlOpt[i] = "1";
					}
				}

			}

			sql = "insert into z_usesubject(projectID,accpackageID,subjectID,ParentSubjectId,TipSubjectId,SubjectName,SubjectFullName,`level0`,Property,isleaf) value (?,?,?,?,?,?,?,?,?,?)";
			pss = conn.prepareStatement(sql);
			for (int i = 0; i < sqlSub.length; i++) {

				if ("0".equals(sqlOpt[i])) {

					pss.setString(1, proid);
					pss.setString(2, acc);
					pss.setString(3, sqlBoth[i][1]);

					pss.setString(4, sqlBoth[i][2]);

					sql = "update z_usesubject set isleaf=0 where accpackageid='"
							+ acc
							+ "' and projectid='"
							+ proid
							+ "' and subjectID = '" + sqlBoth[i][2] + "'";
					ps = conn.prepareStatement(sql);
					ps.execute();

					sql = "select * from c_accpkgsubject where accpackageid='"
							+ acc + "' and subjectid = '" + sqlBoth[i][0] + "'";
					//org.util.Debug.prtOut("insertData sql1:=" + sql);
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					String s = "";
					if (rs.next()) {
						s = rs.getString("subjectname");
						pss.setString(6, s);
					}
					sql = "select * from (select SubjectFullName,level0 from c_accpkgsubject where accpackageid='"
							+ acc
							+ "' and subjectid = '"
							+ sqlBoth[i][2]
							+ "' "
							+ "union select SubjectFullName,level0 from z_usesubject where accpackageid='"
							+ acc
							+ "' and projectID='"
							+ proid
							+ "' and subjectid = '" + sqlBoth[i][2] + "' ) a";
					//org.util.Debug.prtOut("insertData sql2:=" + sql);
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();

					if (rs.next()) {
						pss.setString(7, rs.getString("SubjectFullName") + "/"
								+ s);
						pss.setInt(8, rs.getInt("level0") + 1);
					}
					sql = "select a.subjectid,a.property from (select subjectid,parentsubjectid,SubjectFullName,level0,property from c_accpkgsubject where accpackageid='"
							+ acc
							+ "' union select subjectid,parentsubjectid,SubjectFullName,level0,property from z_usesubject where accpackageid='"
							+ acc
							+ "'and projectID='"
							+ proid
							+ "' ) a,(select subjectid,parentsubjectid,SubjectFullName,level0,property from c_accpkgsubject where accpackageid='"
							+ acc
							+ "' union select subjectid,parentsubjectid,SubjectFullName,level0,property from z_usesubject where accpackageid='"
							+ acc
							+ "' and projectID='"
							+ proid
							+ "' )b where b.subjectid = '"
							+ sqlBoth[i][2]
							+ "' and b.SubjectFullName like concat(a.SubjectFullName,'%') and a.level0=1 ";

					//org.util.Debug.prtOut("insertData sql3:=" + sql);
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();

					if (rs.next()) {
						pss.setString(5, rs.getString("subjectid"));
//						 新增科目的方向，与c_accpkgsubject表的property内容一样 值为新增科目的父科目方向一样
						pss.setString(9, rs.getString("property"));
					}
					pss.setInt(10, 1);

					pss.addBatch();

				}
			}
			pss.executeBatch();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	public void insertData(String acc, String proid, String strOpts,
			String strSubs, String strOpps,String strAss) throws Exception {
		
		PreparedStatement ps = null;
		PreparedStatement ps1 = null;
		ResultSet rs = null;
		try {
		
			conn.setAutoCommit(false);

			//org.util.Debug.prtOut("insertData :=" + strOpts);
			//org.util.Debug.prtOut("insertData :=" + strSubs);
			//org.util.Debug.prtOut("insertData :=" + strOpps);
			//org.util.Debug.prtOut("insertData :=" + strAss);

			String[] sqlOpts = strOpts.split("\\|");
			String[] sqlSub = strSubs.split("\\|");
			String[] sqlOpp = strOpps.split("\\|");
			String[] sqlAss = strAss.split("\\|");

			String[][] sqlBoth = new String[sqlSub.length][4];
			String[] sqlOpt = new String[sqlSub.length];
			String str = "";
			for (int i = 0; i < sqlSub.length; i++) {
				sqlBoth[i][0] = new String();
				sqlBoth[i][1] = new String();
				sqlBoth[i][2] = new String();
				sqlBoth[i][3] = new String();
				sqlOpt[i] = new String();
				if ("".equals(sqlSub[i]) || " ".equals(sqlSub[i])) {
					sqlBoth[i][0] = "";
					sqlBoth[i][1] = "";
					sqlBoth[i][2] = "";
					sqlBoth[i][3] = "";
					sqlOpt[i] = "1";
					continue;
				}
				sqlBoth[i][0] = sqlSub[i];
				sqlBoth[i][1] = sqlOpp[i];
				sqlBoth[i][2] = sqlOpts[i];
				sqlBoth[i][3] = sqlAss[i];
				sqlOpt[i] = "0";
				str += "'" + sqlOpp[i] + "',";
			}
			str = str.substring(0, str.length() - 1);
			//org.util.Debug.prtOut("str :=|" + str);

			String sql = "select * from (select subjectid from c_accpkgsubject where accpackageid='"
					+ acc
					+ "' union select subjectid from z_usesubject where accpackageid='"
					+ acc
					+ "' and projectid='"
					+ proid
					+ "') a  where subjectid in (" + str + ")";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			conn.commit();

			while (rs.next()) {
				String s = rs.getString("subjectid");
				for (int i = 0; i < sqlSub.length; i++) {
					if (sqlBoth[i][1].equals(s)) {
						sqlOpt[i] = "1";
					}
				}

			}

			sql = "insert into z_usesubject(projectID,accpackageID,subjectID,ParentSubjectId,TipSubjectId,SubjectName,SubjectFullName,`level0`,Property,isleaf) value (?,?,?,?,?,?,?,?,?,?)";
			ps1 = conn.prepareStatement(sql);
			for (int i = 0; i < sqlSub.length; i++) {

				if ("0".equals(sqlOpt[i])) {

					ps1.setString(1, proid);
					ps1.setString(2, acc);
					ps1.setString(3, sqlBoth[i][1]);

					ps1.setString(4, sqlBoth[i][2]);

					sql = "update z_usesubject set isleaf=0 where accpackageid='"
							+ acc
							+ "' and projectid='"
							+ proid
							+ "' and subjectID = '" + sqlBoth[i][2] + "'";
					ps = conn.prepareStatement(sql);
					ps.execute();
					conn.commit();

					String s = "";
					
//					sql = "select * from c_accpkgsubject where accpackageid='"+ acc + "' and subjectid = '" + sqlBoth[i][0] + "'";
//					//org.util.Debug.prtOut("insertData sql1:=" + sql);
//					ps = conn.prepareStatement(sql);
//					rs = ps.executeQuery();
//					conn.commit();
//					
//					if (rs.next()) {
//						s = rs.getString("subjectname");
//						pss.setString(6, s);
//					}
					s = sqlBoth[i][3];
					ps1.setString(6, s);
					
					sql = "select * from (select SubjectFullName,level0 from c_accpkgsubject where accpackageid='"
							+ acc
							+ "' and subjectid = '"
							+ sqlBoth[i][2]
							+ "' "
							+ "union select SubjectFullName,level0 from z_usesubject where accpackageid='"
							+ acc
							+ "' and projectID='"
							+ proid
							+ "' and subjectid = '" + sqlBoth[i][2] + "' ) a";
					//org.util.Debug.prtOut("insertData sql2:=" + sql);
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					conn.commit();
					if (rs.next()) {
						ps1.setString(7, rs.getString("SubjectFullName") + "/"
								+ s);
						ps1.setInt(8, rs.getInt("level0") + 1);
					}
					sql = "select a.subjectid,a.property from (select subjectid,parentsubjectid,SubjectFullName,level0,property from c_accpkgsubject where accpackageid='"
							+ acc
							+ "' union select subjectid,parentsubjectid,SubjectFullName,level0,property from z_usesubject where accpackageid='"
							+ acc
							+ "'and projectID='"
							+ proid
							+ "' ) a,(select subjectid,parentsubjectid,SubjectFullName,level0,property from c_accpkgsubject where accpackageid='"
							+ acc
							+ "' union select subjectid,parentsubjectid,SubjectFullName,level0,property from z_usesubject where accpackageid='"
							+ acc
							+ "' and projectID='"
							+ proid
							+ "' )b where b.subjectid = '"
							+ sqlBoth[i][2]
							+ "' and b.SubjectFullName like concat(a.SubjectFullName,'%') and a.level0=1 ";

					//org.util.Debug.prtOut("insertData sql3:=" + sql);
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					conn.commit();
					if (rs.next()) {
						ps1.setString(5, rs.getString("subjectid"));
						// 新增科目的方向，与c_accpkgsubject表的property内容一样 值为新增科目的父科目方向一样
						ps1.setString(9, rs.getString("property"));
					}
					ps1.setInt(10, 1);

					ps1.addBatch();

				}
			}
			ps1.executeBatch();
			conn.commit();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(ps1);
		}
	}
	
	public ArrayList selSubject(String aid, String departID) throws Exception {
		ArrayList alrs = null;
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select *  from c_subjectentry where VoucherID = (select VoucherID  from c_subjectentry where AutoId ='"+ aid + "') order by Serail";
			
			ps = conn.prepareStatement(sql);
			alrs = new ArrayList();
			rs = ps.executeQuery();
			if (rs.next()) {
				ArrayList al = new ArrayList();
				al.add(rs.getString("VchDate")); //0
				al.add(rs.getString("TypeID")); //1
				al.add(rs.getString("OldVoucherID")); //2
				
				al.add(rs.getString("Serail")); //3
				al.add(rs.getString("Summary")); //4
				al.add(rs.getString("SubjectID")); //5
				al.add(rs.getString("Dirction")); //6
				al.add(rs.getString("OccurValue")); //7
				al.add(rs.getString("subjectname1")); //8
				alrs.add(al);
			}
			return alrs;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	public ArrayList selectVoucher(String vStr, String departID)
			throws Exception {
		ArrayList al = null;
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			if (vStr != null && !"".equals(vStr)) {

				String sql = "select Property,description,VchDate from z_voucherrectify WHERE AutoID='"
						+ vStr + "' ";
				
				ps = conn.prepareStatement(sql);
				al = new ArrayList();
				rs = ps.executeQuery();
				if (rs.next()) {
					String st = rs.getString(2);
					
					st = st.replaceAll("\n","\\\\n");
					st = st.replaceAll("\r","\\\\r"); 
					
					al.add(rs.getString(1));
					al.add(st);
					al.add(rs.getString(3));
					// al.add(rs.getString(4));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return al;
	}
	
	
	public String getSubject(String acc,String projectID,String vStr)throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String result = "";
			String sql = " select a.*  from ( " +
			" 	select distinct subjectid from c_accpkgsubject where accpackageid = ? " +
			" 	union  " +
			" 	select distinct subjectid from z_usesubject where projectid = ? " +
			" ) a ,( " +
			" 	select distinct subjectid " +
			" 	from z_subjectentryrectify " +
			" 	where voucherid=? " +
			" ) b  " +
			" where b.subjectid like concat(a.subjectid,'%')";
			ps = conn.prepareStatement(sql);
			ps.setString(1, acc);
			ps.setString(2, projectID);
			ps.setString(3, vStr);
			rs = ps.executeQuery();
			while(rs.next()){
				result += "'"+rs.getString(1)+"',";
			}
			if(!"".equals(result)){
				result = result.substring(0,result.length()-1);
			}else{
				result = "''";
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
	 * 外币：核算外币的调整汇总（暂不用）
	 * @param AccPackageID
	 * @param projectID
	 * @throws Exception
	 */

	public void createWbAssitem(String AccPackageID, String projectID) throws Exception{
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try {
			String endYear = this.getProjectEndYear(projectID);
			
			sql = "DELETE from z_assitemaccallrectify where  projectID=" + projectID;
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			String svalue = "";
			sql = "select svalue from s_config where sname='上年调整影响年末'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				svalue = rs.getString(1);
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			if("否".equals(svalue)){
				sql = "insert into z_assitemaccallrectify (AccPackageID, ProjectID, SubjectID, AssItemID, AssItemName, DataName,\n" +
			    " DebitTotalOcc1, CreditTotalOcc1, DebitTotalOcc2, CreditTotalOcc2, DebitTotalOcc3, CreditTotalOcc3, DebitTotalOcc4, CreditTotalOcc4, DebitTotalOcc5, CreditTotalOcc5, DebitTotalOcc6, CreditTotalOcc6, "+
			    " DebitTotalOcc1f, CreditTotalOcc1f, DebitTotalOcc2f, CreditTotalOcc2f, DebitTotalOcc3f, CreditTotalOcc3f, DebitTotalOcc4f, CreditTotalOcc4f, DebitTotalOcc5f, CreditTotalOcc5f, DebitTotalOcc6f, CreditTotalOcc6f "+
			    " )" +
				" select distinct accpackageId,projectID,subjectid,assitemid,assitemname,a.Currency,  " +
				" sum(DebitTotalOcc1) DebitTotalOcc1,sum(CreditTotalOcc1) CreditTotalOcc1,  " +
				" sum(DebitTotalOcc2) DebitTotalOcc2,sum(CreditTotalOcc2) CreditTotalOcc2,  " +
				" sum(DebitTotalOcc3) DebitTotalOcc3,sum(CreditTotalOcc3) CreditTotalOcc3,  " +
				" sum(DebitTotalOcc4) DebitTotalOcc4,sum(CreditTotalOcc4) CreditTotalOcc4,  " +
				" sum(DebitTotalOcc5) DebitTotalOcc5,sum(CreditTotalOcc5) CreditTotalOcc5,  " +
				" sum(DebitTotalOcc6) DebitTotalOcc6,sum(CreditTotalOcc6) CreditTotalOcc6,  \n" +
				
				" sum(DebitTotalOcc1f) DebitTotalOcc1f,sum(CreditTotalOcc1f) CreditTotalOcc1f,  " +
				" sum(DebitTotalOcc2f) DebitTotalOcc2f,sum(CreditTotalOcc2f) CreditTotalOcc2f,  " +
				" sum(DebitTotalOcc3f) DebitTotalOcc3f,sum(CreditTotalOcc3f) CreditTotalOcc3f,  " +
				" sum(DebitTotalOcc4f) DebitTotalOcc4f,sum(CreditTotalOcc4f) CreditTotalOcc4f,  " +
				" sum(DebitTotalOcc5f) DebitTotalOcc5f,sum(CreditTotalOcc5f) CreditTotalOcc5f,  " +
				" sum(DebitTotalOcc6f) DebitTotalOcc6f,sum(CreditTotalOcc6f) CreditTotalOcc6f  \n" +
				
				" FROM (  " +
				" select distinct b.entryid,a.accpackageId,a.projectID,a.subjectid,b.assitemid,c.assitemname,  Currency," +
				
				" case when a.property like '3%' and a.dirction=1 and substring(a.vchdate,1,4) = '"+endYear+"' then CurrValue else 0 end DebitTotalOcc1," +
				" case when a.property like '3%' and a.dirction=-1 and substring(a.vchdate,1,4) = '"+endYear+"' then CurrValue else 0 end CreditTotalOcc1," +
				" case when a.property like '4%' and a.dirction=1 and substring(a.vchdate,1,4) = '"+endYear+"' then CurrValue else 0 end DebitTotalOcc2," +
				" case when a.property like '4%' and a.dirction=-1 and substring(a.vchdate,1,4) = '"+endYear+"' then CurrValue else 0 end CreditTotalOcc2," +
				" case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=1 and substring(a.vchdate,1,4) = '"+endYear+"' then CurrValue else 0 end DebitTotalOcc3, " +
				" case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=-1 and substring(a.vchdate,1,4) = '"+endYear+"' then CurrValue else 0 end CreditTotalOcc3, " +
				" case when a.property like '3%' and a.dirction=1 and substring(a.vchdate,1,4) = "+endYear+"-1 then CurrValue else 0 end DebitTotalOcc4," +
				" case when a.property like '3%' and a.dirction=-1 and substring(a.vchdate,1,4) = "+endYear+"-1 then CurrValue else 0 end CreditTotalOcc4, " +
				" case when a.property like '4%' and a.dirction=1 and substring(a.vchdate,1,4) = "+endYear+"-1 then CurrValue else 0 end DebitTotalOcc5, " +
				" case when a.property like '4%' and a.dirction=-1 and substring(a.vchdate,1,4) = "+endYear+"-1 then CurrValue else 0 end CreditTotalOcc5, " +
				" case when a.property like '5%' and a.dirction=1 and substring(a.vchdate,1,4) = "+endYear+"-1 then CurrValue else 0 end DebitTotalOcc6,   " +
				" case when a.property like '5%' and a.dirction=-1 and substring(a.vchdate,1,4) = "+endYear+"-1 then CurrValue else 0 end CreditTotalOcc6,   " +

				" case when a.property like '3%' and a.dirction=1 and substring(a.vchdate,1,4) = '"+endYear+"' then OccurValue else 0 end DebitTotalOcc1f," +
				" case when a.property like '3%' and a.dirction=-1 and substring(a.vchdate,1,4) = '"+endYear+"' then OccurValue else 0 end CreditTotalOcc1f," +
				" case when a.property like '4%' and a.dirction=1 and substring(a.vchdate,1,4) = '"+endYear+"' then OccurValue else 0 end DebitTotalOcc2f," +
				" case when a.property like '4%' and a.dirction=-1 and substring(a.vchdate,1,4) = '"+endYear+"' then OccurValue else 0 end CreditTotalOcc2f," +
				" case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=1 and substring(a.vchdate,1,4) = '"+endYear+"' then OccurValue else 0 end DebitTotalOcc3f, " +
				" case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=-1 and substring(a.vchdate,1,4) = '"+endYear+"' then OccurValue else 0 end CreditTotalOcc3f, " +
				" case when a.property like '3%' and a.dirction=1 and substring(a.vchdate,1,4) = "+endYear+"-1 then OccurValue else 0 end DebitTotalOcc4f," +
				" case when a.property like '3%' and a.dirction=-1 and substring(a.vchdate,1,4) = "+endYear+"-1 then OccurValue else 0 end CreditTotalOcc4f, " +
				" case when a.property like '4%' and a.dirction=1 and substring(a.vchdate,1,4) = "+endYear+"-1 then OccurValue else 0 end DebitTotalOcc5f, " +
				" case when a.property like '4%' and a.dirction=-1 and substring(a.vchdate,1,4) = "+endYear+"-1 then OccurValue else 0 end CreditTotalOcc5f, " +
				" case when a.property like '5%' and a.dirction=1 and substring(a.vchdate,1,4) = "+endYear+"-1 then OccurValue else 0 end DebitTotalOcc6f,   " +
				" case when a.property like '5%' and a.dirction=-1 and substring(a.vchdate,1,4) = "+endYear+"-1 then OccurValue else 0 end CreditTotalOcc6f   " +

				" from z_subjectentryrectify a,z_assitementryrectify b ,   c_assitementryaccall  c  " +
				" where a.projectID='"+projectID+"' " +
//				" and a.Currency <> '' " +
				" and c.accpackageId= '"+AccPackageID+"' and c.submonth=1 " +
				" and a.accpackageId=b.accpackageId and a.projectID=b.projectID " +
//				" and a.Currency=c.dataName" +
				" and a.accpackageId=c.accpackageId  and a.autoid=b.entryId and a.subjectid=c.accid and b.assitemid=c.assitemid  " +
				" )as a group by Currency,subjectid,assitemid";
			}else{
				sql = "insert into z_assitemaccallrectify (AccPackageID, ProjectID, SubjectID, AssItemID, AssItemName, DataName,\n" +
			    " DebitTotalOcc1, CreditTotalOcc1, DebitTotalOcc2, CreditTotalOcc2, DebitTotalOcc3, CreditTotalOcc3, DebitTotalOcc4, CreditTotalOcc4, DebitTotalOcc5, CreditTotalOcc5, DebitTotalOcc6, CreditTotalOcc6, "+
			    " DebitTotalOcc1f, CreditTotalOcc1f, DebitTotalOcc2f, CreditTotalOcc2f, DebitTotalOcc3f, CreditTotalOcc3f, DebitTotalOcc4f, CreditTotalOcc4f, DebitTotalOcc5f, CreditTotalOcc5f, DebitTotalOcc6f, CreditTotalOcc6f "+
			    " )" +
				" select distinct accpackageId,projectID,subjectid,assitemid,assitemname,a.Currency,  " +
				" sum(DebitTotalOcc1) DebitTotalOcc1,sum(CreditTotalOcc1) CreditTotalOcc1,  " +
				" sum(DebitTotalOcc2) DebitTotalOcc2,sum(CreditTotalOcc2) CreditTotalOcc2,  " +
				" sum(DebitTotalOcc3) DebitTotalOcc3,sum(CreditTotalOcc3) CreditTotalOcc3,  " +
				" sum(DebitTotalOcc4) DebitTotalOcc4,sum(CreditTotalOcc4) CreditTotalOcc4,  " +
				" sum(DebitTotalOcc5) DebitTotalOcc5,sum(CreditTotalOcc5) CreditTotalOcc5,  " +
				" sum(DebitTotalOcc6) DebitTotalOcc6,sum(CreditTotalOcc6) CreditTotalOcc6,  \n" +
				
				" sum(DebitTotalOcc1f) DebitTotalOcc1f,sum(CreditTotalOcc1f) CreditTotalOcc1f,  " +
				" sum(DebitTotalOcc2f) DebitTotalOcc2f,sum(CreditTotalOcc2f) CreditTotalOcc2f,  " +
				" sum(DebitTotalOcc3f) DebitTotalOcc3f,sum(CreditTotalOcc3f) CreditTotalOcc3f,  " +
				" sum(DebitTotalOcc4f) DebitTotalOcc4f,sum(CreditTotalOcc4f) CreditTotalOcc4f,  " +
				" sum(DebitTotalOcc5f) DebitTotalOcc5f,sum(CreditTotalOcc5f) CreditTotalOcc5f,  " +
				" sum(DebitTotalOcc6f) DebitTotalOcc6f,sum(CreditTotalOcc6f) CreditTotalOcc6f  \n" +
				
				" FROM (  " +
				" select distinct b.entryid,a.accpackageId,a.projectID,a.subjectid,b.assitemid,c.assitemname,  Currency," +
				
				" case when a.property like '3%' and a.dirction=1 and substring(a.vchdate,1,4) = '"+endYear+"' then CurrValue else 0 end DebitTotalOcc1," +
				" case when a.property like '3%' and a.dirction=-1 and substring(a.vchdate,1,4) = '"+endYear+"' then CurrValue else 0 end CreditTotalOcc1," +
				" case when a.property like '4%' and a.dirction=1 and substring(a.vchdate,1,4) = '"+endYear+"' then CurrValue else 0 end DebitTotalOcc2," +
				" case when a.property like '4%' and a.dirction=-1 and substring(a.vchdate,1,4) = '"+endYear+"' then CurrValue else 0 end CreditTotalOcc2," +
				" case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=1 and substring(a.vchdate,1,4) = '"+endYear+"' then CurrValue else 0 end DebitTotalOcc3, " +
				" case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=-1 and substring(a.vchdate,1,4) = '"+endYear+"' then CurrValue else 0 end CreditTotalOcc3, " +
				" case when a.property like '3%' and a.dirction=1 and substring(a.vchdate,1,4) < '"+endYear+"' then CurrValue else 0 end DebitTotalOcc4," +
				" case when a.property like '3%' and a.dirction=-1 and substring(a.vchdate,1,4) < '"+endYear+"' then CurrValue else 0 end CreditTotalOcc4, " +
				" case when a.property like '4%' and a.dirction=1 and substring(a.vchdate,1,4) < '"+endYear+"' then CurrValue else 0 end DebitTotalOcc5, " +
				" case when a.property like '4%' and a.dirction=-1 and substring(a.vchdate,1,4) < '"+endYear+"' then CurrValue else 0 end CreditTotalOcc5, " +
				" case when a.property like '5%' and a.dirction=1 and substring(a.vchdate,1,4) < '"+endYear+"' then CurrValue else 0 end DebitTotalOcc6,   " +
				" case when a.property like '5%' and a.dirction=-1 and substring(a.vchdate,1,4) < '"+endYear+"' then CurrValue else 0 end CreditTotalOcc6,   " +

				" case when a.property like '3%' and a.dirction=1 and substring(a.vchdate,1,4) = '"+endYear+"' then OccurValue else 0 end DebitTotalOcc1f," +
				" case when a.property like '3%' and a.dirction=-1 and substring(a.vchdate,1,4) = '"+endYear+"' then OccurValue else 0 end CreditTotalOcc1f," +
				" case when a.property like '4%' and a.dirction=1 and substring(a.vchdate,1,4) = '"+endYear+"' then OccurValue else 0 end DebitTotalOcc2f," +
				" case when a.property like '4%' and a.dirction=-1 and substring(a.vchdate,1,4) = '"+endYear+"' then OccurValue else 0 end CreditTotalOcc2f," +
				" case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=1 and substring(a.vchdate,1,4) = '"+endYear+"' then OccurValue else 0 end DebitTotalOcc3f, " +
				" case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=-1 and substring(a.vchdate,1,4) = '"+endYear+"' then OccurValue else 0 end CreditTotalOcc3f, " +
				" case when a.property like '3%' and a.dirction=1 and substring(a.vchdate,1,4) < '"+endYear+"' then OccurValue else 0 end DebitTotalOcc4f," +
				" case when a.property like '3%' and a.dirction=-1 and substring(a.vchdate,1,4) < '"+endYear+"' then OccurValue else 0 end CreditTotalOcc4f, " +
				" case when a.property like '4%' and a.dirction=1 and substring(a.vchdate,1,4) < '"+endYear+"' then OccurValue else 0 end DebitTotalOcc5f, " +
				" case when a.property like '4%' and a.dirction=-1 and substring(a.vchdate,1,4) < '"+endYear+"' then OccurValue else 0 end CreditTotalOcc5f, " +
				" case when a.property like '5%' and a.dirction=1 and substring(a.vchdate,1,4) < '"+endYear+"' then OccurValue else 0 end DebitTotalOcc6f,   " +
				" case when a.property like '5%' and a.dirction=-1 and substring(a.vchdate,1,4) < '"+endYear+"' then OccurValue else 0 end CreditTotalOcc6f   " +

				" from z_subjectentryrectify a,z_assitementryrectify b ,   c_assitementryaccall  c  " +
				" where a.projectID='"+projectID+"' " +
//				" and a.Currency <> '' " +
				" and c.accpackageId= '"+AccPackageID+"' and c.submonth=1 " +
				" and a.accpackageId=b.accpackageId and a.projectID=b.projectID " +
//				" and a.Currency=c.dataName" +
				" and a.accpackageId=c.accpackageId and a.autoid=b.entryId and a.subjectid=c.accid and b.assitemid=c.assitemid  " +
				" )as a group by Currency,subjectid,assitemid";
			}
			
			org.util.Debug.prtOut("createWbAssitem = |" + sql);
			
			ps = conn.prepareStatement(sql);
			ps.execute();
		
			//修改dataname为空的值
			sql = "update z_assitemaccallrectify a,(" +
			"	select distinct accpackageid,accid as subjectid,assitemid,dataname " +
			"	from c_assitementryaccall " +
			"	where 1=1 " +
			"	and accpackageid='"+ AccPackageID+ "' " +
			"	and submonth = 1 " +
			"	and accsign=1 " +
			") b " +
			"set a.dataname = b.dataname " +
			"where a.projectid='"+ projectID + "' " +
			"and b.accpackageid='"+ AccPackageID+ "' " +
			"and a.dataname = '' " +
			"and a.subjectid = b.subjectid " +
			"and a.assitemid = b.assitemid ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			
			//org.util.Debug.prtOut("createWbAssitem = |" + new ASFuntion().getCurrentTime());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
		}
	}
	/**
	 * 外币：科目外币的调整汇总
	 * @param AccPackageID
	 * @param projectID
	 * @throws Exception
	 */
	
	
	public void createWbTzhz(String AccPackageID, String projectID)throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try {

			String endYear = this.getProjectEndYear(projectID);
			
			sql = "DELETE from z_accountallrectify where  projectID=" + projectID;
			ps = conn.prepareStatement(sql);
			ps.execute();
			
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
			 * 科目外币汇总表增加isleaf1
			 */
			if("否".equals(svalue)){
				sql = "INSERT into z_accountallrectify (accpackageId,projectID,subjectid,SubjectName,DataName,"
					
					+ " DebitTotalOcc1, CreditTotalOcc1, DebitTotalOcc2, CreditTotalOcc2, "
					+ " DebitTotalOcc3, CreditTotalOcc3, DebitTotalOcc4, CreditTotalOcc4, "
					+ " DebitTotalOcc5, CreditTotalOcc5, DebitTotalOcc6, CreditTotalOcc6,"
					
					+ " DebitTotalOcc1f, CreditTotalOcc1f, DebitTotalOcc2f, CreditTotalOcc2f, "
					+ " DebitTotalOcc3f, CreditTotalOcc3f, DebitTotalOcc4f, CreditTotalOcc4f, "
					+ " DebitTotalOcc5f, CreditTotalOcc5f, DebitTotalOcc6f, CreditTotalOcc6f,"
					
					+ "isleaf1) "
					+ " select distinct a.accpackageid,a.projectid,"
					+ " if(b.isleaf1=1,a.subjectid,b.subjectid) subjectid,if(b.isleaf1=1,a.accname,b.accname) accname,a.Currency,"
					+ " sum(DebitTotalOcc1) DebitTotalOcc1,sum(CreditTotalOcc1) CreditTotalOcc1,"
					+ " sum(DebitTotalOcc2) DebitTotalOcc2,sum(CreditTotalOcc2) CreditTotalOcc2,"
					+ " sum(DebitTotalOcc3) DebitTotalOcc3,sum(CreditTotalOcc3) CreditTotalOcc3,"
					+ " sum(DebitTotalOcc4) DebitTotalOcc4,sum(CreditTotalOcc4) CreditTotalOcc4,"
					+ " sum(DebitTotalOcc5) DebitTotalOcc5,sum(CreditTotalOcc5) CreditTotalOcc5,"
					+ " sum(DebitTotalOcc6) DebitTotalOcc6,sum(CreditTotalOcc6) CreditTotalOcc6," 
					
					+ " sum(DebitTotalOcc1f) DebitTotalOcc1f,sum(CreditTotalOcc1f) CreditTotalOcc1f,"
					+ " sum(DebitTotalOcc2f) DebitTotalOcc2f,sum(CreditTotalOcc2f) CreditTotalOcc2f,"
					+ " sum(DebitTotalOcc3f) DebitTotalOcc3f,sum(CreditTotalOcc3f) CreditTotalOcc3f,"
					+ " sum(DebitTotalOcc4f) DebitTotalOcc4f,sum(CreditTotalOcc4f) CreditTotalOcc4f,"
					+ " sum(DebitTotalOcc5f) DebitTotalOcc5f,sum(CreditTotalOcc5f) CreditTotalOcc5f,"
					+ " sum(DebitTotalOcc6f) DebitTotalOcc6f,sum(CreditTotalOcc6f) CreditTotalOcc6f,"
					
					+ "b.isleaf1 "
					+ " from (select distinct a.accpackageid,a.projectid,a.subjectid,b.accname,b.subjectfullname1,a.Currency,"
					+ " sum(case when a.property like '3%' and a.dirction=1 and substring(a.vchdate,1,4) = '"+endYear+"' then CurrValue else 0 end) DebitTotalOcc1,"
					+ " sum(case when a.property like '3%' and a.dirction=-1 and substring(a.vchdate,1,4) = '"+endYear+"' then CurrValue else 0 end) CreditTotalOcc1,"
					+ " sum(case when a.property like '4%' and a.dirction=1 and substring(a.vchdate,1,4) = '"+endYear+"' then CurrValue else 0 end) DebitTotalOcc2,"
					+ " sum(case when a.property like '4%' and a.dirction=-1 and substring(a.vchdate,1,4) = '"+endYear+"' then CurrValue else 0 end) CreditTotalOcc2,"
					+ " sum(case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=1 and substring(a.vchdate,1,4) = '"+endYear+"' then CurrValue else 0 end) DebitTotalOcc3," 
					+ " sum(case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=-1 and substring(a.vchdate,1,4) = '"+endYear+"' then CurrValue else 0 end) CreditTotalOcc3," 
					+ " sum(case when a.property like '3%' and a.dirction=1 and substring(a.vchdate,1,4) = "+endYear+"-1 then CurrValue else 0 end) DebitTotalOcc4,"
					+ " sum(case when a.property like '3%' and a.dirction=-1 and substring(a.vchdate,1,4) = "+endYear+"-1 then CurrValue else 0 end) CreditTotalOcc4,"
					+ " sum(case when a.property like '4%' and a.dirction=1 and substring(a.vchdate,1,4) = "+endYear+"-1 then CurrValue else 0 end) DebitTotalOcc5,"
					+ " sum(case when a.property like '4%' and a.dirction=-1 and substring(a.vchdate,1,4) = "+endYear+"-1 then CurrValue else 0 end) CreditTotalOcc5,"
					+ " sum(case when a.property like '5%' and a.dirction=1 and substring(a.vchdate,1,4) = "+endYear+"-1 then CurrValue else 0 end) DebitTotalOcc6,  "
					+ " sum(case when a.property like '5%' and a.dirction=-1 and substring(a.vchdate,1,4) = "+endYear+"-1 then CurrValue else 0 end) CreditTotalOcc6, "
			
					+ " sum(case when a.property like '3%' and a.dirction=1 and substring(a.vchdate,1,4) = '"+endYear+"' then OccurValue else 0 end) DebitTotalOcc1f,"
					+ " sum(case when a.property like '3%' and a.dirction=-1 and substring(a.vchdate,1,4) = '"+endYear+"' then OccurValue else 0 end) CreditTotalOcc1f,"
					+ " sum(case when a.property like '4%' and a.dirction=1 and substring(a.vchdate,1,4) = '"+endYear+"' then OccurValue else 0 end) DebitTotalOcc2f,"
					+ " sum(case when a.property like '4%' and a.dirction=-1 and substring(a.vchdate,1,4) = '"+endYear+"' then OccurValue else 0 end) CreditTotalOcc2f,"
					+ " sum(case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=1 and substring(a.vchdate,1,4) = '"+endYear+"' then OccurValue else 0 end) DebitTotalOcc3f," 
					+ " sum(case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=-1 and substring(a.vchdate,1,4) = '"+endYear+"' then OccurValue else 0 end) CreditTotalOcc3f," 
					+ " sum(case when a.property like '3%' and a.dirction=1 and substring(a.vchdate,1,4) = "+endYear+"-1 then OccurValue else 0 end) DebitTotalOcc4f,"
					+ " sum(case when a.property like '3%' and a.dirction=-1 and substring(a.vchdate,1,4) = "+endYear+"-1 then OccurValue else 0 end) CreditTotalOcc4f,"
					+ " sum(case when a.property like '4%' and a.dirction=1 and substring(a.vchdate,1,4) = "+endYear+"-1 then OccurValue else 0 end) DebitTotalOcc5f,"
					+ " sum(case when a.property like '4%' and a.dirction=-1 and substring(a.vchdate,1,4) = "+endYear+"-1 then OccurValue else 0 end) CreditTotalOcc5f,"
					+ " sum(case when a.property like '5%' and a.dirction=1 and substring(a.vchdate,1,4) = "+endYear+"-1 then OccurValue else 0 end) DebitTotalOcc6f,  "
					+ " sum(case when a.property like '5%' and a.dirction=-1 and substring(a.vchdate,1,4) = "+endYear+"-1 then OccurValue else 0 end) CreditTotalOcc6f "

					+ " from z_subjectentryrectify a,c_accountall b "
					+ " where a.accpackageid='"+ AccPackageID+ "' and a.projectid='"+ projectID + "' "
//					+ " and a.Currency <>'' "
					+ " and b.accpackageid='"+ AccPackageID
					+ "' and b.submonth=1 and b.accsign=1 "
					+ " and a.subjectid=b.subjectid "
//					+ " and a.Currency=b.dataName "
					+ " group by b.dataName,b.subjectid) a , (select * from c_accountall "
					+ " where accpackageid='"+ AccPackageID+ "' and submonth=1 and accsign=1 ) b "
					+ " where (a.subjectfullname1 like concat(b.subjectfullname1,'/%') or a.subjectfullname1=b.subjectfullname1) "
//					+ " and a.Currency=b.dataName "
					+ " group  by b.dataName,a.Currency,b.subjectid";
			}else{
				sql = "INSERT into z_accountallrectify (accpackageId,projectID,subjectid,SubjectName,DataName,"
					
					+ " DebitTotalOcc1, CreditTotalOcc1, DebitTotalOcc2, CreditTotalOcc2, "
					+ " DebitTotalOcc3, CreditTotalOcc3, DebitTotalOcc4, CreditTotalOcc4, "
					+ " DebitTotalOcc5, CreditTotalOcc5, DebitTotalOcc6, CreditTotalOcc6,"
					
					+ " DebitTotalOcc1f, CreditTotalOcc1f, DebitTotalOcc2f, CreditTotalOcc2f, "
					+ " DebitTotalOcc3f, CreditTotalOcc3f, DebitTotalOcc4f, CreditTotalOcc4f, "
					+ " DebitTotalOcc5f, CreditTotalOcc5f, DebitTotalOcc6f, CreditTotalOcc6f,"
					
					+ "isleaf1) "
					+ " select distinct a.accpackageid,a.projectid,"
					+ " if(b.isleaf1=1,a.subjectid,b.subjectid) subjectid,if(b.isleaf1=1,a.accname,b.accname) accname,a.Currency,"
					+ " sum(DebitTotalOcc1) DebitTotalOcc1,sum(CreditTotalOcc1) CreditTotalOcc1,"
					+ " sum(DebitTotalOcc2) DebitTotalOcc2,sum(CreditTotalOcc2) CreditTotalOcc2,"
					+ " sum(DebitTotalOcc3) DebitTotalOcc3,sum(CreditTotalOcc3) CreditTotalOcc3,"
					+ " sum(DebitTotalOcc4) DebitTotalOcc4,sum(CreditTotalOcc4) CreditTotalOcc4,"
					+ " sum(DebitTotalOcc5) DebitTotalOcc5,sum(CreditTotalOcc5) CreditTotalOcc5,"
					+ " sum(DebitTotalOcc6) DebitTotalOcc6,sum(CreditTotalOcc6) CreditTotalOcc6," 
					
					+ " sum(DebitTotalOcc1f) DebitTotalOcc1f,sum(CreditTotalOcc1f) CreditTotalOcc1f,"
					+ " sum(DebitTotalOcc2f) DebitTotalOcc2f,sum(CreditTotalOcc2f) CreditTotalOcc2f,"
					+ " sum(DebitTotalOcc3f) DebitTotalOcc3f,sum(CreditTotalOcc3f) CreditTotalOcc3f,"
					+ " sum(DebitTotalOcc4f) DebitTotalOcc4f,sum(CreditTotalOcc4f) CreditTotalOcc4f,"
					+ " sum(DebitTotalOcc5f) DebitTotalOcc5f,sum(CreditTotalOcc5f) CreditTotalOcc5f,"
					+ " sum(DebitTotalOcc6f) DebitTotalOcc6f,sum(CreditTotalOcc6f) CreditTotalOcc6f,"
					
					+ "b.isleaf1 "
					+ " from (select distinct a.accpackageid,a.projectid,a.subjectid,b.accname,b.subjectfullname1,a.Currency,"
					+ " sum(case when a.property like '3%' and a.dirction=1 and substring(a.vchdate,1,4) = '"+endYear+"' then CurrValue else 0 end) DebitTotalOcc1,"
					+ " sum(case when a.property like '3%' and a.dirction=-1 and substring(a.vchdate,1,4) = '"+endYear+"' then CurrValue else 0 end) CreditTotalOcc1,"
					+ " sum(case when a.property like '4%' and a.dirction=1 and substring(a.vchdate,1,4) = '"+endYear+"' then CurrValue else 0 end) DebitTotalOcc2,"
					+ " sum(case when a.property like '4%' and a.dirction=-1 and substring(a.vchdate,1,4) = '"+endYear+"' then CurrValue else 0 end) CreditTotalOcc2,"
					+ " sum(case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=1 and substring(a.vchdate,1,4) = '"+endYear+"' then CurrValue else 0 end) DebitTotalOcc3," 
					+ " sum(case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=-1 and substring(a.vchdate,1,4) = '"+endYear+"' then CurrValue else 0 end) CreditTotalOcc3," 
					+ " sum(case when a.property like '3%' and a.dirction=1 and substring(a.vchdate,1,4) < '"+endYear+"' then CurrValue else 0 end) DebitTotalOcc4,"
					+ " sum(case when a.property like '3%' and a.dirction=-1 and substring(a.vchdate,1,4) < '"+endYear+"' then CurrValue else 0 end) CreditTotalOcc4,"
					+ " sum(case when a.property like '4%' and a.dirction=1 and substring(a.vchdate,1,4) < '"+endYear+"' then CurrValue else 0 end) DebitTotalOcc5,"
					+ " sum(case when a.property like '4%' and a.dirction=-1 and substring(a.vchdate,1,4) < '"+endYear+"' then CurrValue else 0 end) CreditTotalOcc5,"
					+ " sum(case when a.property like '5%' and a.dirction=1 and substring(a.vchdate,1,4) < '"+endYear+"' then CurrValue else 0 end) DebitTotalOcc6,  "
					+ " sum(case when a.property like '5%' and a.dirction=-1 and substring(a.vchdate,1,4) < '"+endYear+"' then CurrValue else 0 end) CreditTotalOcc6, "
			
					+ " sum(case when a.property like '3%' and a.dirction=1 and substring(a.vchdate,1,4) = '"+endYear+"' then OccurValue else 0 end) DebitTotalOcc1f,"
					+ " sum(case when a.property like '3%' and a.dirction=-1 and substring(a.vchdate,1,4) = '"+endYear+"' then OccurValue else 0 end) CreditTotalOcc1f,"
					+ " sum(case when a.property like '4%' and a.dirction=1 and substring(a.vchdate,1,4) = '"+endYear+"' then OccurValue else 0 end) DebitTotalOcc2f,"
					+ " sum(case when a.property like '4%' and a.dirction=-1 and substring(a.vchdate,1,4) = '"+endYear+"' then OccurValue else 0 end) CreditTotalOcc2f,"
					+ " sum(case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=1 and substring(a.vchdate,1,4) = '"+endYear+"' then OccurValue else 0 end) DebitTotalOcc3f," 
					+ " sum(case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=-1 and substring(a.vchdate,1,4) = '"+endYear+"' then OccurValue else 0 end) CreditTotalOcc3f," 
					+ " sum(case when a.property like '3%' and a.dirction=1 and substring(a.vchdate,1,4) < '"+endYear+"' then OccurValue else 0 end) DebitTotalOcc4f,"
					+ " sum(case when a.property like '3%' and a.dirction=-1 and substring(a.vchdate,1,4) < '"+endYear+"' then OccurValue else 0 end) CreditTotalOcc4f,"
					+ " sum(case when a.property like '4%' and a.dirction=1 and substring(a.vchdate,1,4) < '"+endYear+"' then OccurValue else 0 end) DebitTotalOcc5f,"
					+ " sum(case when a.property like '4%' and a.dirction=-1 and substring(a.vchdate,1,4) < '"+endYear+"' then OccurValue else 0 end) CreditTotalOcc5f,"
					+ " sum(case when a.property like '5%' and a.dirction=1 and substring(a.vchdate,1,4) < '"+endYear+"' then OccurValue else 0 end) DebitTotalOcc6f,  "
					+ " sum(case when a.property like '5%' and a.dirction=-1 and substring(a.vchdate,1,4) < '"+endYear+"' then OccurValue else 0 end) CreditTotalOcc6f "

					+ " from z_subjectentryrectify a,c_accountall b "
					+ " where a.accpackageid='"+ AccPackageID+ "' and a.projectid='"+ projectID + "' "
//					+ " and a.Currency <>'' "
					+ " and b.accpackageid='"+ AccPackageID
					+ "' and b.submonth=1 and b.accsign=1 "
					+ " and a.subjectid=b.subjectid "
//					+ " and a.Currency=b.dataName "
					+ " group by b.dataName,b.subjectid) a , (select * from c_accountall "
					+ " where accpackageid='"+ AccPackageID+ "' and submonth=1 and accsign=1 ) b "
					+ " where (a.subjectfullname1 like concat(b.subjectfullname1,'/%') or a.subjectfullname1=b.subjectfullname1) "
//					+ " and a.Currency=b.dataName "
					+ " group  by b.dataName,a.Currency,b.subjectid";
			}
			

			org.util.Debug.prtOut("createWbTzhz = |" + sql);
//			//org.util.Debug.prtOut("createAssitem = "+ String.valueOf(new java.util.Date(System.currentTimeMillis())));
			ps = conn.prepareStatement(sql);
			ps.execute();
			
			//修改dataname为空的值
			sql = "update z_accountallrectify a,(" +
			"	select distinct accpackageid,subjectid,dataname " +
			"	from c_accountall " +
			"	where 1=1 " +
			"	and accpackageid='"+ AccPackageID+ "' " +
			"	and submonth = 1 " +
			"	and accsign=1 " +
			") b " +
			"set a.dataname = b.dataname " +
			"where a.projectid='"+ projectID + "' " +
			"and b.accpackageid='"+ AccPackageID+ "' " +
			"and a.dataname = '' " +
			"and a.subjectid = b.subjectid";
			ps = conn.prepareStatement(sql);
			ps.execute();
			
			//org.util.Debug.prtOut("createWbTzhz = |" + new ASFuntion().getCurrentTime());
			
			createWbTzHzYear( AccPackageID, projectID);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 本位币：核算的调整汇总
	 * @param AccPackageID
	 * @param projectID
	 * @throws Exception
	 */
	public void createAssitem(String AccPackageID, String projectID) throws Exception {
		createAssitem1( AccPackageID,  projectID);		
//		new ManuacCountService(conn).updateRectify(projectID);
//		new ManuacCountService(conn).insertOne( projectID);
	}
	
	/**
	 * 本位币：核算的调整汇总
	 * @param AccPackageID
	 * @param projectID
	 * @throws Exception
	 */
	public void createAssitem(String AccPackageID, String projectID,String delete,String insert) throws Exception {
		createAssitem1( AccPackageID,  projectID);		
//		new ManuacCountService(conn).updateRectify(projectID);
		//org.util.Debug.prtOut("进入 insertOne = |" + new ASFuntion().getCurrentTime());
//		new ManuacCountService(conn).insertOne( projectID, delete, insert);
	}
	
	public void createAssitem1(String AccPackageID, String projectID) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		// int i = 0;
		try {

			String endYear = this.getProjectEndYear(projectID);
			
			sql = "DELETE from z_assitemaccrectify where projectID=" + projectID;
			ps = conn.prepareStatement(sql);
			ps.execute();
			
			String svalue = "";
			sql = "select svalue from s_config where sname='上年调整影响年末'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				svalue = rs.getString(1);
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			if("否".equals(svalue)){
				sql = "INSERT into z_assitemaccrectify (accpackageId,projectID,subjectid,assitemid,assitemname, "
					+ " DebitTotalOcc1, CreditTotalOcc1, "
					+ " DebitTotalOcc2, CreditTotalOcc2, "
					+ " DebitTotalOcc3, CreditTotalOcc3, "
					+ " DebitTotalOcc4, CreditTotalOcc4, "
					+ " DebitTotalOcc5, CreditTotalOcc5, "
					+ " DebitTotalOcc6, CreditTotalOcc6) "
					+ " select accpackageId,projectID,subjectid,assitemid,assitemname, "
					+ " sum(DebitTotalOcc1) DebitTotalOcc1,sum(CreditTotalOcc1) CreditTotalOcc1, "
					+ " sum(DebitTotalOcc2) DebitTotalOcc2,sum(CreditTotalOcc2) CreditTotalOcc2, "
					+ " sum(DebitTotalOcc3) DebitTotalOcc3,sum(CreditTotalOcc3) CreditTotalOcc3, "
					+ " sum(DebitTotalOcc4) DebitTotalOcc4,sum(CreditTotalOcc4) CreditTotalOcc4, "
					+ " sum(DebitTotalOcc5) DebitTotalOcc5,sum(CreditTotalOcc5) CreditTotalOcc5, "
					+ " sum(DebitTotalOcc6) DebitTotalOcc6,sum(CreditTotalOcc6) CreditTotalOcc6 "
					+ " FROM (  select a.accpackageId,a.projectID,a.subjectid,b.assitemid,c.assitemname, "
					
					+ " case when a.property like '3%' and a.dirction=1 and substring(a.vchdate,1,4) = '"+endYear+"' then OccurValue else 0 end DebitTotalOcc1,"
					+ " case when a.property like '3%' and a.dirction=-1 and substring(a.vchdate,1,4) = '"+endYear+"' then OccurValue else 0 end CreditTotalOcc1,"
					+ " case when a.property like '4%' and a.dirction=1 and substring(a.vchdate,1,4) = '"+endYear+"' then OccurValue else 0 end DebitTotalOcc2,"
					+ " case when a.property like '4%' and a.dirction=-1 and substring(a.vchdate,1,4) = '"+endYear+"' then OccurValue else 0 end CreditTotalOcc2,"
					+ " case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=1 and substring(a.vchdate,1,4) = '"+endYear+"' then OccurValue else 0 end DebitTotalOcc3, "
					+ " case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=-1 and substring(a.vchdate,1,4) = '"+endYear+"' then OccurValue else 0 end CreditTotalOcc3, "
					
					+ " case when a.property like '3%' and a.dirction=1 and substring(a.vchdate,1,4) = "+endYear+"-1 then OccurValue else 0 end DebitTotalOcc4,"
					+ " case when a.property like '3%' and a.dirction=-1 and substring(a.vchdate,1,4) = "+endYear+"-1 then OccurValue else 0 end CreditTotalOcc4, "
					+ " case when a.property like '4%' and a.dirction=1 and substring(a.vchdate,1,4) = "+endYear+"-1 then OccurValue else 0 end DebitTotalOcc5, "
					+ " case when a.property like '4%' and a.dirction=-1 and substring(a.vchdate,1,4) = "+endYear+"-1 then OccurValue else 0 end CreditTotalOcc5, "
					+ " case when a.property like '5%' and a.dirction=1 and substring(a.vchdate,1,4) = "+endYear+"-1 then OccurValue else 0 end DebitTotalOcc6,   "
					+ " case when a.property like '5%' and a.dirction=-1 and substring(a.vchdate,1,4) = "+endYear+"-1 then OccurValue else 0 end CreditTotalOcc6   "
					
					+ " from z_subjectentryrectify a,z_assitementryrectify b , c_assitementryacc c  "
					+ " where a.projectID='"+ projectID+ "' and c.accpackageId= '"+ AccPackageID+ "' and c.submonth=1 "
					+ " and a.accpackageId=b.accpackageId and a.projectID=b.projectID and a.accpackageId=c.accpackageId "
					+ " and a.autoid=b.entryId and a.subjectid=c.accid and b.assitemid=c.assitemid "
					+ " )as a group by subjectid,assitemid";
			}else{
				sql = "INSERT into z_assitemaccrectify (accpackageId,projectID,subjectid,assitemid,assitemname, "
					+ " DebitTotalOcc1, CreditTotalOcc1, "
					+ " DebitTotalOcc2, CreditTotalOcc2, "
					+ " DebitTotalOcc3, CreditTotalOcc3, "
					+ " DebitTotalOcc4, CreditTotalOcc4, "
					+ " DebitTotalOcc5, CreditTotalOcc5, "
					+ " DebitTotalOcc6, CreditTotalOcc6) "
					+ " select accpackageId,projectID,subjectid,assitemid,assitemname, "
					+ " sum(DebitTotalOcc1) DebitTotalOcc1,sum(CreditTotalOcc1) CreditTotalOcc1, "
					+ " sum(DebitTotalOcc2) DebitTotalOcc2,sum(CreditTotalOcc2) CreditTotalOcc2, "
					+ " sum(DebitTotalOcc3) DebitTotalOcc3,sum(CreditTotalOcc3) CreditTotalOcc3, "
					+ " sum(DebitTotalOcc4) DebitTotalOcc4,sum(CreditTotalOcc4) CreditTotalOcc4, "
					+ " sum(DebitTotalOcc5) DebitTotalOcc5,sum(CreditTotalOcc5) CreditTotalOcc5, "
					+ " sum(DebitTotalOcc6) DebitTotalOcc6,sum(CreditTotalOcc6) CreditTotalOcc6 "
					+ " FROM (  select a.accpackageId,a.projectID,a.subjectid,b.assitemid,c.assitemname, "
					
					+ " case when a.property like '3%' and a.dirction=1 and substring(a.vchdate,1,4) = '"+endYear+"' then OccurValue else 0 end DebitTotalOcc1,"
					+ " case when a.property like '3%' and a.dirction=-1 and substring(a.vchdate,1,4) = '"+endYear+"' then OccurValue else 0 end CreditTotalOcc1,"
					+ " case when a.property like '4%' and a.dirction=1 and substring(a.vchdate,1,4) = '"+endYear+"' then OccurValue else 0 end DebitTotalOcc2,"
					+ " case when a.property like '4%' and a.dirction=-1 and substring(a.vchdate,1,4) = '"+endYear+"' then OccurValue else 0 end CreditTotalOcc2,"
					+ " case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=1 and substring(a.vchdate,1,4) = '"+endYear+"' then OccurValue else 0 end DebitTotalOcc3, "
					+ " case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=-1 and substring(a.vchdate,1,4) = '"+endYear+"' then OccurValue else 0 end CreditTotalOcc3, "
					
					+ " case when a.property like '3%' and a.dirction=1 and substring(a.vchdate,1,4) < '"+endYear+"' then OccurValue else 0 end DebitTotalOcc4,"
					+ " case when a.property like '3%' and a.dirction=-1 and substring(a.vchdate,1,4) < '"+endYear+"' then OccurValue else 0 end CreditTotalOcc4, "
					+ " case when a.property like '4%' and a.dirction=1 and substring(a.vchdate,1,4) < '"+endYear+"' then OccurValue else 0 end DebitTotalOcc5, "
					+ " case when a.property like '4%' and a.dirction=-1 and substring(a.vchdate,1,4) < '"+endYear+"' then OccurValue else 0 end CreditTotalOcc5, "
					+ " case when a.property like '5%' and a.dirction=1 and substring(a.vchdate,1,4) < '"+endYear+"' then OccurValue else 0 end DebitTotalOcc6,   "
					+ " case when a.property like '5%' and a.dirction=-1 and substring(a.vchdate,1,4) < '"+endYear+"' then OccurValue else 0 end CreditTotalOcc6   "
					
					+ " from z_subjectentryrectify a,z_assitementryrectify b , c_assitementryacc c  "
					+ " where a.projectID='"+ projectID+ "' and c.accpackageId= '"+ AccPackageID+ "' and c.submonth=1 "
					+ " and a.accpackageId=b.accpackageId and a.projectID=b.projectID and a.accpackageId=c.accpackageId "
					+ " and a.autoid=b.entryId and a.subjectid=c.accid and b.assitemid=c.assitemid "
					+ " )as a group by subjectid,assitemid";
			}
			
			//org.util.Debug.prtOut("createAssitem = |" + sql);
//			//org.util.Debug.prtOut("createAssitem = "+ String.valueOf(new java.util.Date(System.currentTimeMillis())));
			ps = conn.prepareStatement(sql);
			ps.execute();

			//org.util.Debug.prtOut("createAssitem1 = |" + new ASFuntion().getCurrentTime());
			
			createAssitemYear( AccPackageID,  projectID);
			createWbAssitem( AccPackageID,  projectID);
			createWbAssitemYear( AccPackageID,  projectID);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}

	}
	
	public void createWbAssitemYear(String AccPackageID, String projectID) throws Exception {
		DbUtil.checkConn(conn);
		Statement st = null;
		ResultSet rs = null;
		String sql = "";
		try {
			String endYear = this.getProjectEndYear(projectID);
			
			st = conn.createStatement();
			
			sql = "DELETE from z_assitemaccallyearrectify where  projectID=" + projectID;
			st.execute(sql);
			
			String svalue = "";
			sql = "select svalue from s_config where sname='上年调整影响年末'";
			rs = st.executeQuery(sql);
			while(rs.next()){
				svalue = rs.getString(1);
			}
			DbUtil.close(rs);
			
			sql = "select distinct min(substring(vchdate,1,4)) from z_subjectentryrectify where projectid='"+projectID+"' and substring(vchdate,1,4)<'"+endYear+"' order by vchdate desc ";
			rs = st.executeQuery(sql);
			int min = 0;
			if(rs.next()){
				min = rs.getInt(1);
			}
			DbUtil.close(rs);
			
			if(min == 0) return;
			
			for(int ivyear=Integer.parseInt(endYear)-1;ivyear>=min;ivyear--){
				String vyear = String.valueOf(ivyear);
				if("否".equals(svalue)){
					sql = "insert into z_assitemaccallyearrectify (AccPackageID, ProjectID, SubjectID, AssItemID, AssItemName, DataName,yearrectify,\n" +
				    " DebitTotalOcc1, CreditTotalOcc1, DebitTotalOcc2, CreditTotalOcc2, DebitTotalOcc3, CreditTotalOcc3, DebitTotalOcc4, CreditTotalOcc4, DebitTotalOcc5, CreditTotalOcc5, DebitTotalOcc6, CreditTotalOcc6, "+
				    " DebitTotalOcc1f, CreditTotalOcc1f, DebitTotalOcc2f, CreditTotalOcc2f, DebitTotalOcc3f, CreditTotalOcc3f, DebitTotalOcc4f, CreditTotalOcc4f, DebitTotalOcc5f, CreditTotalOcc5f, DebitTotalOcc6f, CreditTotalOcc6f "+
				    " )" +
					" select distinct accpackageId,projectID,subjectid,assitemid,assitemname,a.Currency, yearrectify, " +
					" sum(DebitTotalOcc1) DebitTotalOcc1,sum(CreditTotalOcc1) CreditTotalOcc1,  " +
					" sum(DebitTotalOcc2) DebitTotalOcc2,sum(CreditTotalOcc2) CreditTotalOcc2,  " +
					" sum(DebitTotalOcc3) DebitTotalOcc3,sum(CreditTotalOcc3) CreditTotalOcc3,  " +
					" sum(DebitTotalOcc4) DebitTotalOcc4,sum(CreditTotalOcc4) CreditTotalOcc4,  " +
					" sum(DebitTotalOcc5) DebitTotalOcc5,sum(CreditTotalOcc5) CreditTotalOcc5,  " +
					" sum(DebitTotalOcc6) DebitTotalOcc6,sum(CreditTotalOcc6) CreditTotalOcc6,  \n" +
					
					" sum(DebitTotalOcc1f) DebitTotalOcc1f,sum(CreditTotalOcc1f) CreditTotalOcc1f,  " +
					" sum(DebitTotalOcc2f) DebitTotalOcc2f,sum(CreditTotalOcc2f) CreditTotalOcc2f,  " +
					" sum(DebitTotalOcc3f) DebitTotalOcc3f,sum(CreditTotalOcc3f) CreditTotalOcc3f,  " +
					" sum(DebitTotalOcc4f) DebitTotalOcc4f,sum(CreditTotalOcc4f) CreditTotalOcc4f,  " +
					" sum(DebitTotalOcc5f) DebitTotalOcc5f,sum(CreditTotalOcc5f) CreditTotalOcc5f,  " +
					" sum(DebitTotalOcc6f) DebitTotalOcc6f,sum(CreditTotalOcc6f) CreditTotalOcc6f  \n" +
					
					" FROM (  " +
					" select distinct b.entryid,a.accpackageId,a.projectID,a.subjectid,b.assitemid,c.assitemname,  Currency,'"+vyear+"' yearrectify," +
					
					" case when a.property like '3%' and a.dirction=1 and substring(a.vchdate,1,4) = '"+vyear+"' then CurrValue else 0 end DebitTotalOcc1," +
					" case when a.property like '3%' and a.dirction=-1 and substring(a.vchdate,1,4) = '"+vyear+"' then CurrValue else 0 end CreditTotalOcc1," +
					" case when a.property like '4%' and a.dirction=1 and substring(a.vchdate,1,4) = '"+vyear+"' then CurrValue else 0 end DebitTotalOcc2," +
					" case when a.property like '4%' and a.dirction=-1 and substring(a.vchdate,1,4) = '"+vyear+"' then CurrValue else 0 end CreditTotalOcc2," +
					" case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=1 and substring(a.vchdate,1,4) = '"+vyear+"' then CurrValue else 0 end DebitTotalOcc3, " +
					" case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=-1 and substring(a.vchdate,1,4) = '"+vyear+"' then CurrValue else 0 end CreditTotalOcc3, " +
					" case when a.property like '3%' and a.dirction=1 and substring(a.vchdate,1,4) = "+vyear+"-1 then CurrValue else 0 end DebitTotalOcc4," +
					" case when a.property like '3%' and a.dirction=-1 and substring(a.vchdate,1,4) = "+vyear+"-1 then CurrValue else 0 end CreditTotalOcc4, " +
					" case when a.property like '4%' and a.dirction=1 and substring(a.vchdate,1,4) = "+vyear+"-1 then CurrValue else 0 end DebitTotalOcc5, " +
					" case when a.property like '4%' and a.dirction=-1 and substring(a.vchdate,1,4) = "+vyear+"-1 then CurrValue else 0 end CreditTotalOcc5, " +
					" case when a.property like '5%' and a.dirction=1 and substring(a.vchdate,1,4) = "+vyear+"-1 then CurrValue else 0 end DebitTotalOcc6,   " +
					" case when a.property like '5%' and a.dirction=-1 and substring(a.vchdate,1,4) = "+vyear+"-1 then CurrValue else 0 end CreditTotalOcc6,   " +

					" case when a.property like '3%' and a.dirction=1 and substring(a.vchdate,1,4) = '"+vyear+"' then OccurValue else 0 end DebitTotalOcc1f," +
					" case when a.property like '3%' and a.dirction=-1 and substring(a.vchdate,1,4) = '"+vyear+"' then OccurValue else 0 end CreditTotalOcc1f," +
					" case when a.property like '4%' and a.dirction=1 and substring(a.vchdate,1,4) = '"+vyear+"' then OccurValue else 0 end DebitTotalOcc2f," +
					" case when a.property like '4%' and a.dirction=-1 and substring(a.vchdate,1,4) = '"+vyear+"' then OccurValue else 0 end CreditTotalOcc2f," +
					" case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=1 and substring(a.vchdate,1,4) = '"+vyear+"' then OccurValue else 0 end DebitTotalOcc3f, " +
					" case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=-1 and substring(a.vchdate,1,4) = '"+vyear+"' then OccurValue else 0 end CreditTotalOcc3f, " +
					" case when a.property like '3%' and a.dirction=1 and substring(a.vchdate,1,4) = "+vyear+"-1 then OccurValue else 0 end DebitTotalOcc4f," +
					" case when a.property like '3%' and a.dirction=-1 and substring(a.vchdate,1,4) = "+vyear+"-1 then OccurValue else 0 end CreditTotalOcc4f, " +
					" case when a.property like '4%' and a.dirction=1 and substring(a.vchdate,1,4) = "+vyear+"-1 then OccurValue else 0 end DebitTotalOcc5f, " +
					" case when a.property like '4%' and a.dirction=-1 and substring(a.vchdate,1,4) = "+vyear+"-1 then OccurValue else 0 end CreditTotalOcc5f, " +
					" case when a.property like '5%' and a.dirction=1 and substring(a.vchdate,1,4) = "+vyear+"-1 then OccurValue else 0 end DebitTotalOcc6f,   " +
					" case when a.property like '5%' and a.dirction=-1 and substring(a.vchdate,1,4) = "+vyear+"-1 then OccurValue else 0 end CreditTotalOcc6f   " +

					" from z_subjectentryrectify a,z_assitementryrectify b ,  c_assitementryaccall c  " +
					" where a.projectID='"+projectID+"' " +
//					" and a.Currency <> '' " +
					" and c.accpackageId= '"+AccPackageID+"' and c.submonth=1" +
					" and a.accpackageId=b.accpackageId and a.projectID=b.projectID " +
//					" and a.Currency=c.dataName" +
					" and a.accpackageId=c.accpackageId  and a.autoid=b.entryId and a.subjectid=c.accid and b.assitemid=c.assitemid  " +
					" )as a group by Currency,subjectid,assitemid";
				}else{
					sql = "insert into z_assitemaccallyearrectify (AccPackageID, ProjectID, SubjectID, AssItemID, AssItemName, DataName,yearrectify,\n" +
				    " DebitTotalOcc1, CreditTotalOcc1, DebitTotalOcc2, CreditTotalOcc2, DebitTotalOcc3, CreditTotalOcc3, DebitTotalOcc4, CreditTotalOcc4, DebitTotalOcc5, CreditTotalOcc5, DebitTotalOcc6, CreditTotalOcc6, "+
				    " DebitTotalOcc1f, CreditTotalOcc1f, DebitTotalOcc2f, CreditTotalOcc2f, DebitTotalOcc3f, CreditTotalOcc3f, DebitTotalOcc4f, CreditTotalOcc4f, DebitTotalOcc5f, CreditTotalOcc5f, DebitTotalOcc6f, CreditTotalOcc6f "+
				    " )" +
					" select distinct accpackageId,projectID,subjectid,assitemid,assitemname,a.Currency, yearrectify, " +
					" sum(DebitTotalOcc1) DebitTotalOcc1,sum(CreditTotalOcc1) CreditTotalOcc1,  " +
					" sum(DebitTotalOcc2) DebitTotalOcc2,sum(CreditTotalOcc2) CreditTotalOcc2,  " +
					" sum(DebitTotalOcc3) DebitTotalOcc3,sum(CreditTotalOcc3) CreditTotalOcc3,  " +
					" sum(DebitTotalOcc4) DebitTotalOcc4,sum(CreditTotalOcc4) CreditTotalOcc4,  " +
					" sum(DebitTotalOcc5) DebitTotalOcc5,sum(CreditTotalOcc5) CreditTotalOcc5,  " +
					" sum(DebitTotalOcc6) DebitTotalOcc6,sum(CreditTotalOcc6) CreditTotalOcc6,  \n" +
					
					" sum(DebitTotalOcc1f) DebitTotalOcc1f,sum(CreditTotalOcc1f) CreditTotalOcc1f,  " +
					" sum(DebitTotalOcc2f) DebitTotalOcc2f,sum(CreditTotalOcc2f) CreditTotalOcc2f,  " +
					" sum(DebitTotalOcc3f) DebitTotalOcc3f,sum(CreditTotalOcc3f) CreditTotalOcc3f,  " +
					" sum(DebitTotalOcc4f) DebitTotalOcc4f,sum(CreditTotalOcc4f) CreditTotalOcc4f,  " +
					" sum(DebitTotalOcc5f) DebitTotalOcc5f,sum(CreditTotalOcc5f) CreditTotalOcc5f,  " +
					" sum(DebitTotalOcc6f) DebitTotalOcc6f,sum(CreditTotalOcc6f) CreditTotalOcc6f  \n" +
					
					" FROM (  " +
					" select distinct b.entryid,a.accpackageId,a.projectID,a.subjectid,b.assitemid,c.assitemname,  Currency,'"+vyear+"' yearrectify," +
					
					" case when a.property like '3%' and a.dirction=1 and substring(a.vchdate,1,4) = '"+vyear+"' then CurrValue else 0 end DebitTotalOcc1," +
					" case when a.property like '3%' and a.dirction=-1 and substring(a.vchdate,1,4) = '"+vyear+"' then CurrValue else 0 end CreditTotalOcc1," +
					" case when a.property like '4%' and a.dirction=1 and substring(a.vchdate,1,4) = '"+vyear+"' then CurrValue else 0 end DebitTotalOcc2," +
					" case when a.property like '4%' and a.dirction=-1 and substring(a.vchdate,1,4) = '"+vyear+"' then CurrValue else 0 end CreditTotalOcc2," +
					" case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=1 and substring(a.vchdate,1,4) = '"+vyear+"' then CurrValue else 0 end DebitTotalOcc3, " +
					" case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=-1 and substring(a.vchdate,1,4) = '"+vyear+"' then CurrValue else 0 end CreditTotalOcc3, " +
					" case when a.property like '3%' and a.dirction=1 and substring(a.vchdate,1,4) < '"+vyear+"' then CurrValue else 0 end DebitTotalOcc4," +
					" case when a.property like '3%' and a.dirction=-1 and substring(a.vchdate,1,4) < '"+vyear+"' then CurrValue else 0 end CreditTotalOcc4, " +
					" case when a.property like '4%' and a.dirction=1 and substring(a.vchdate,1,4) < '"+vyear+"' then CurrValue else 0 end DebitTotalOcc5, " +
					" case when a.property like '4%' and a.dirction=-1 and substring(a.vchdate,1,4) < '"+vyear+"' then CurrValue else 0 end CreditTotalOcc5, " +
					" case when a.property like '5%' and a.dirction=1 and substring(a.vchdate,1,4) < '"+vyear+"' then CurrValue else 0 end DebitTotalOcc6,   " +
					" case when a.property like '5%' and a.dirction=-1 and substring(a.vchdate,1,4) < '"+vyear+"' then CurrValue else 0 end CreditTotalOcc6,   " +

					" case when a.property like '3%' and a.dirction=1 and substring(a.vchdate,1,4) = '"+vyear+"' then OccurValue else 0 end DebitTotalOcc1f," +
					" case when a.property like '3%' and a.dirction=-1 and substring(a.vchdate,1,4) = '"+vyear+"' then OccurValue else 0 end CreditTotalOcc1f," +
					" case when a.property like '4%' and a.dirction=1 and substring(a.vchdate,1,4) = '"+vyear+"' then OccurValue else 0 end DebitTotalOcc2f," +
					" case when a.property like '4%' and a.dirction=-1 and substring(a.vchdate,1,4) = '"+vyear+"' then OccurValue else 0 end CreditTotalOcc2f," +
					" case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=1 and substring(a.vchdate,1,4) = '"+vyear+"' then OccurValue else 0 end DebitTotalOcc3f, " +
					" case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=-1 and substring(a.vchdate,1,4) = '"+vyear+"' then OccurValue else 0 end CreditTotalOcc3f, " +
					" case when a.property like '3%' and a.dirction=1 and substring(a.vchdate,1,4) < '"+vyear+"' then OccurValue else 0 end DebitTotalOcc4f," +
					" case when a.property like '3%' and a.dirction=-1 and substring(a.vchdate,1,4) < '"+vyear+"' then OccurValue else 0 end CreditTotalOcc4f, " +
					" case when a.property like '4%' and a.dirction=1 and substring(a.vchdate,1,4) < '"+vyear+"' then OccurValue else 0 end DebitTotalOcc5f, " +
					" case when a.property like '4%' and a.dirction=-1 and substring(a.vchdate,1,4) < '"+vyear+"' then OccurValue else 0 end CreditTotalOcc5f, " +
					" case when a.property like '5%' and a.dirction=1 and substring(a.vchdate,1,4) < '"+vyear+"' then OccurValue else 0 end DebitTotalOcc6f,   " +
					" case when a.property like '5%' and a.dirction=-1 and substring(a.vchdate,1,4) < '"+vyear+"' then OccurValue else 0 end CreditTotalOcc6f   " +

					" from z_subjectentryrectify a,z_assitementryrectify b ,  c_assitementryaccall c  " +
					" where a.projectID='"+projectID+"' " +
//					" and a.Currency <> '' " +
					" and c.accpackageId= '"+AccPackageID+"' and c.submonth=1" +
					" and a.accpackageId=b.accpackageId and a.projectID=b.projectID " +
//					" and a.Currency=c.dataName" +
					" and a.accpackageId=c.accpackageId  and a.autoid=b.entryId and a.subjectid=c.accid and b.assitemid=c.assitemid  " +
					" )as a group by Currency,subjectid,assitemid";
				}
				
				st.addBatch(sql);
			}
			
			st.executeBatch();
//			//org.util.Debug.prtOut("createWbAssitem = |" + sql);
			
			//修改dataname为空的值
			sql = "update z_assitemaccallyearrectify a,(" +
			"	select distinct accpackageid,accid as subjectid,assitemid,dataname " +
			"	from c_assitementryaccall " +
			"	where 1=1 " +
			"	and accpackageid='"+ AccPackageID+ "' " +
			"	and submonth = 1 " +
			"	and accsign=1 " +
			") b " +
			"set a.dataname = b.dataname " +
			"where a.projectid='"+ projectID + "' " +
			"and b.accpackageid='"+ AccPackageID+ "' " +
			"and a.dataname = '' " +
			"and a.subjectid = b.subjectid " +
			"and a.assitemid = b.assitemid ";
			st.execute(sql);
			
			//org.util.Debug.prtOut("createWbAssitem = |" + new ASFuntion().getCurrentTime());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(st);
		}
	}
	
	public void createAssitemYear(String AccPackageID, String projectID) throws Exception {
		DbUtil.checkConn(conn);
		Statement st = null;
		ResultSet rs = null;
		String sql = "";
		try {
			String endYear = this.getProjectEndYear(projectID);
			
			st = conn.createStatement();
			
			sql = "DELETE from z_assitemaccyearrectify where  projectID=" + projectID;
			st.execute(sql);
			
			String svalue = "";
			sql = "select svalue from s_config where sname='上年调整影响年末'";
			rs = st.executeQuery(sql);
			while(rs.next()){
				svalue = rs.getString(1);
			}
			DbUtil.close(rs);
			
			sql = "select distinct min(substring(vchdate,1,4)) from z_subjectentryrectify where projectid='"+projectID+"' and substring(vchdate,1,4)<'"+endYear+"' order by vchdate desc ";
			rs = st.executeQuery(sql);
			int min = 0;
			if(rs.next()){
				min = rs.getInt(1);
			}
			DbUtil.close(rs);
			
			if(min == 0) return;
			
			for(int ivyear=Integer.parseInt(endYear)-1;ivyear>=min;ivyear--){
				String vyear = String.valueOf(ivyear);
				if("否".equals(svalue)){
					sql = "INSERT into z_assitemaccyearrectify (accpackageId,projectID,subjectid,assitemid,assitemname, "
						+ " DebitTotalOcc1, CreditTotalOcc1, "
						+ " DebitTotalOcc2, CreditTotalOcc2, "
						+ " DebitTotalOcc3, CreditTotalOcc3, "
						+ " DebitTotalOcc4, CreditTotalOcc4, "
						+ " DebitTotalOcc5, CreditTotalOcc5, "
						+ " DebitTotalOcc6, CreditTotalOcc6,yearrectify) "
						+ " select accpackageId,projectID,subjectid,assitemid,assitemname, "
						+ " sum(DebitTotalOcc1) DebitTotalOcc1,sum(CreditTotalOcc1) CreditTotalOcc1, "
						+ " sum(DebitTotalOcc2) DebitTotalOcc2,sum(CreditTotalOcc2) CreditTotalOcc2, "
						+ " sum(DebitTotalOcc3) DebitTotalOcc3,sum(CreditTotalOcc3) CreditTotalOcc3, "
						+ " sum(DebitTotalOcc4) DebitTotalOcc4,sum(CreditTotalOcc4) CreditTotalOcc4, "
						+ " sum(DebitTotalOcc5) DebitTotalOcc5,sum(CreditTotalOcc5) CreditTotalOcc5, "
						+ " sum(DebitTotalOcc6) DebitTotalOcc6,sum(CreditTotalOcc6) CreditTotalOcc6,yearrectify "
						+ " FROM (  select a.accpackageId,a.projectID,a.subjectid,b.assitemid,c.assitemname, "
						
						+ " case when a.property like '3%' and a.dirction=1 and substring(a.vchdate,1,4) = '"+vyear+"' then OccurValue else 0 end DebitTotalOcc1,"
						+ " case when a.property like '3%' and a.dirction=-1 and substring(a.vchdate,1,4) = '"+vyear+"' then OccurValue else 0 end CreditTotalOcc1,"
						+ " case when a.property like '4%' and a.dirction=1 and substring(a.vchdate,1,4) = '"+vyear+"' then OccurValue else 0 end DebitTotalOcc2,"
						+ " case when a.property like '4%' and a.dirction=-1 and substring(a.vchdate,1,4) = '"+vyear+"' then OccurValue else 0 end CreditTotalOcc2,"
						+ " case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=1 and substring(a.vchdate,1,4) = '"+vyear+"' then OccurValue else 0 end DebitTotalOcc3, "
						+ " case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=-1 and substring(a.vchdate,1,4) = '"+vyear+"' then OccurValue else 0 end CreditTotalOcc3, "
						
						+ " case when a.property like '3%' and a.dirction=1 and substring(a.vchdate,1,4) = "+vyear+"-1 then OccurValue else 0 end DebitTotalOcc4,"
						+ " case when a.property like '3%' and a.dirction=-1 and substring(a.vchdate,1,4) = "+vyear+"-1 then OccurValue else 0 end CreditTotalOcc4, "
						+ " case when a.property like '4%' and a.dirction=1 and substring(a.vchdate,1,4) = "+vyear+"-1 then OccurValue else 0 end DebitTotalOcc5, "
						+ " case when a.property like '4%' and a.dirction=-1 and substring(a.vchdate,1,4) = "+vyear+"-1 then OccurValue else 0 end CreditTotalOcc5, "
						+ " case when a.property like '5%' and a.dirction=1 and substring(a.vchdate,1,4) = "+vyear+"-1 then OccurValue else 0 end DebitTotalOcc6,   "
						+ " case when a.property like '5%' and a.dirction=-1 and substring(a.vchdate,1,4) = "+vyear+"-1 then OccurValue else 0 end CreditTotalOcc6,   " 
						+ " '"+vyear+"' yearrectify "
						
						+ " from z_subjectentryrectify a,z_assitementryrectify b ,c_assitementryacc c  "
						+ " where a.projectID='"+ projectID + "' and c.accpackageId= '"+ AccPackageID+ "' and c.submonth=1 "
						+ " and a.accpackageId=b.accpackageId and a.projectID=b.projectID and a.accpackageId=c.accpackageId "
						+ " and a.autoid=b.entryId and a.subjectid=c.accid and b.assitemid=c.assitemid "
						+ " )as a group by subjectid,assitemid";
				}else{
					sql = "INSERT into z_assitemaccyearrectify (accpackageId,projectID,subjectid,assitemid,assitemname, "
						+ " DebitTotalOcc1, CreditTotalOcc1, "
						+ " DebitTotalOcc2, CreditTotalOcc2, "
						+ " DebitTotalOcc3, CreditTotalOcc3, "
						+ " DebitTotalOcc4, CreditTotalOcc4, "
						+ " DebitTotalOcc5, CreditTotalOcc5, "
						+ " DebitTotalOcc6, CreditTotalOcc6,yearrectify) "
						+ " select accpackageId,projectID,subjectid,assitemid,assitemname, "
						+ " sum(DebitTotalOcc1) DebitTotalOcc1,sum(CreditTotalOcc1) CreditTotalOcc1, "
						+ " sum(DebitTotalOcc2) DebitTotalOcc2,sum(CreditTotalOcc2) CreditTotalOcc2, "
						+ " sum(DebitTotalOcc3) DebitTotalOcc3,sum(CreditTotalOcc3) CreditTotalOcc3, "
						+ " sum(DebitTotalOcc4) DebitTotalOcc4,sum(CreditTotalOcc4) CreditTotalOcc4, "
						+ " sum(DebitTotalOcc5) DebitTotalOcc5,sum(CreditTotalOcc5) CreditTotalOcc5, "
						+ " sum(DebitTotalOcc6) DebitTotalOcc6,sum(CreditTotalOcc6) CreditTotalOcc6,yearrectify "
						+ " FROM (  select a.accpackageId,a.projectID,a.subjectid,b.assitemid,c.assitemname, "
						
						+ " case when a.property like '3%' and a.dirction=1 and substring(a.vchdate,1,4) = '"+vyear+"' then OccurValue else 0 end DebitTotalOcc1,"
						+ " case when a.property like '3%' and a.dirction=-1 and substring(a.vchdate,1,4) = '"+vyear+"' then OccurValue else 0 end CreditTotalOcc1,"
						+ " case when a.property like '4%' and a.dirction=1 and substring(a.vchdate,1,4) = '"+vyear+"' then OccurValue else 0 end DebitTotalOcc2,"
						+ " case when a.property like '4%' and a.dirction=-1 and substring(a.vchdate,1,4) = '"+vyear+"' then OccurValue else 0 end CreditTotalOcc2,"
						+ " case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=1 and substring(a.vchdate,1,4) = '"+vyear+"' then OccurValue else 0 end DebitTotalOcc3, "
						+ " case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=-1 and substring(a.vchdate,1,4) = '"+vyear+"' then OccurValue else 0 end CreditTotalOcc3, "
						
						+ " case when a.property like '3%' and a.dirction=1 and substring(a.vchdate,1,4) < '"+vyear+"' then OccurValue else 0 end DebitTotalOcc4,"
						+ " case when a.property like '3%' and a.dirction=-1 and substring(a.vchdate,1,4) < '"+vyear+"' then OccurValue else 0 end CreditTotalOcc4, "
						+ " case when a.property like '4%' and a.dirction=1 and substring(a.vchdate,1,4) < '"+vyear+"' then OccurValue else 0 end DebitTotalOcc5, "
						+ " case when a.property like '4%' and a.dirction=-1 and substring(a.vchdate,1,4) < '"+vyear+"' then OccurValue else 0 end CreditTotalOcc5, "
						+ " case when a.property like '5%' and a.dirction=1 and substring(a.vchdate,1,4) < '"+vyear+"' then OccurValue else 0 end DebitTotalOcc6,   "
						+ " case when a.property like '5%' and a.dirction=-1 and substring(a.vchdate,1,4) < '"+vyear+"' then OccurValue else 0 end CreditTotalOcc6,   " 
						+ " '"+vyear+"' yearrectify "
						
						+ " from z_subjectentryrectify a,z_assitementryrectify b ,c_assitementryacc c  "
						+ " where a.projectID='"+ projectID + "' and c.accpackageId= '"+ AccPackageID+ "' and c.submonth=1 "
						+ " and a.accpackageId=b.accpackageId and a.projectID=b.projectID and a.accpackageId=c.accpackageId "
						+ " and a.autoid=b.entryId and a.subjectid=c.accid and b.assitemid=c.assitemid "
						+ " )as a group by subjectid,assitemid";
				}
				//org.util.Debug.prtOut("createAssitemYear = " + sql);
				st.addBatch(sql);
			}
			
			st.executeBatch();

			//org.util.Debug.prtOut("createAssitem1 = |" + new ASFuntion().getCurrentTime());
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(st);
		}

	}
	
	
	/**
	 * 本位币：科目的调整汇总
	 * @param AccPackageID
	 * @param projectID
	 * @throws Exception
	 */

	public void createTzhz(String AccPackageID, String projectID)throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try {
			//org.util.Debug.prtOut("createTzhz = "+ String.valueOf(new java.util.Date(System.currentTimeMillis())));
			
			String endYear = this.getProjectEndYear(projectID);
			
			//conn = new DBConnect().getConnect(AccPackageID.substring(0, 6));
			sql = "DELETE from z_accountrectify where projectID=" + projectID;
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			String svalue = "";
			sql = "select svalue from s_config where sname='上年调整影响年末'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				svalue = rs.getString(1);
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			if("否".equals(svalue)){
				sql ="INSERT into z_accountrectify (accpackageId,projectID,subjectid,SubjectName,"
					+ " DebitTotalOcc1, CreditTotalOcc1, "
					+ " DebitTotalOcc2, CreditTotalOcc2, "
					+ " DebitTotalOcc3, CreditTotalOcc3, "
					+ " DebitTotalOcc4, CreditTotalOcc4, "
					+ " DebitTotalOcc5, CreditTotalOcc5, "
					+ " DebitTotalOcc6, CreditTotalOcc6, " 
					+ " DebitTotalOcc0, CreditTotalOcc0,isleaf "
					+ " ) "
					+ " select ifnull(a.accpackageid,'"+AccPackageID+"') accpackageid, " 
					+ " ifnull(a.projectid,'"+projectID+"') accpackageid, "
					+ " ifnull(a.subjectid,b.subjectid) subjectid,ifnull(a.SubjectName,b.SubjectName) SubjectName,"
					+ " ifnull(a.DebitTotalOcc1,'0.00') DebitTotalOcc1,ifnull(a.CreditTotalOcc1,'0.00') CreditTotalOcc1,"
					+ " ifnull(a.DebitTotalOcc2,'0.00') DebitTotalOcc2,ifnull(a.CreditTotalOcc2,'0.00') CreditTotalOcc2,"
					+ " ifnull(a.DebitTotalOcc3,'0.00') DebitTotalOcc3,ifnull(a.CreditTotalOcc3,'0.00') CreditTotalOcc3,"
					+ " ifnull(a.DebitTotalOcc4,'0.00') DebitTotalOcc4,ifnull(a.CreditTotalOcc4,'0.00') CreditTotalOcc4,"
					+ " ifnull(a.DebitTotalOcc5,'0.00') DebitTotalOcc5,ifnull(a.CreditTotalOcc5,'0.00') CreditTotalOcc5,"
					+ " ifnull(a.DebitTotalOcc6,'0.00') DebitTotalOcc6,ifnull(a.CreditTotalOcc6,'0.00') CreditTotalOcc6,"
					
					+ " ifnull(a.DebitTotalOcc0,'0.00') DebitTotalOcc0,ifnull(a.CreditTotalOcc0,'0.00') CreditTotalOcc0 ,b.isleaf "
					
					+ " from (select a.accpackageid,a.projectid,"
					+ " b.subjectid,b.SubjectName,"
					+ " sum(DebitTotalOcc1) DebitTotalOcc1,sum(CreditTotalOcc1) CreditTotalOcc1,"
					+ " sum(DebitTotalOcc2) DebitTotalOcc2,sum(CreditTotalOcc2) CreditTotalOcc2,"
					+ " sum(DebitTotalOcc3) DebitTotalOcc3,sum(CreditTotalOcc3) CreditTotalOcc3,"
					+ " sum(DebitTotalOcc4) DebitTotalOcc4,sum(CreditTotalOcc4) CreditTotalOcc4,"
					+ " sum(DebitTotalOcc5) DebitTotalOcc5,sum(CreditTotalOcc5) CreditTotalOcc5,"
					+ " sum(DebitTotalOcc6) DebitTotalOcc6,sum(CreditTotalOcc6) CreditTotalOcc6,"
					
					+ " sum(DebitTotalOcc0) DebitTotalOcc0,sum(CreditTotalOcc0) CreditTotalOcc0 "
					
					+ " from (select a.accpackageid,a.projectid,a.subjectid,b.SubjectName,b.subjectfullname,"
		
					+ " sum(case when a.property like '3%' and a.dirction=1 and substring(a.vchdate,1,4) = '"+endYear+"' then OccurValue else 0 end) DebitTotalOcc1,"
					+ " sum(case when a.property like '3%' and a.dirction=-1 and substring(a.vchdate,1,4) = '"+endYear+"' then OccurValue else 0 end) CreditTotalOcc1,"
					+ " sum(case when a.property like '4%' and a.dirction=1 and substring(a.vchdate,1,4) = '"+endYear+"' then OccurValue else 0 end) DebitTotalOcc2,"
					+ " sum(case when a.property like '4%' and a.dirction=-1 and substring(a.vchdate,1,4) = '"+endYear+"' then OccurValue else 0 end) CreditTotalOcc2,"
					+ " sum(case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=1 and substring(a.vchdate,1,4) = '"+endYear+"' then OccurValue else 0 end) DebitTotalOcc3, "
					+ " sum(case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=-1 and substring(a.vchdate,1,4) = '"+endYear+"' then OccurValue else 0 end) CreditTotalOcc3, "
					
					+ " sum(case when a.property like '3%' and a.dirction=1 and substring(a.vchdate,1,4) = "+endYear+"-1 then OccurValue else 0 end) DebitTotalOcc4,"
					+ " sum(case when a.property like '3%' and a.dirction=-1 and substring(a.vchdate,1,4) = "+endYear+"-1 then OccurValue else 0 end) CreditTotalOcc4, "
					+ " sum(case when a.property like '4%' and a.dirction=1 and substring(a.vchdate,1,4) = "+endYear+"-1 then OccurValue else 0 end) DebitTotalOcc5, "
					+ " sum(case when a.property like '4%' and a.dirction=-1 and substring(a.vchdate,1,4) = "+endYear+"-1 then OccurValue else 0 end) CreditTotalOcc5, "
					+ " sum(case when a.property like '5%' and a.dirction=1 and substring(a.vchdate,1,4) = "+endYear+"-1 then OccurValue else 0 end) DebitTotalOcc6,   "
					+ " sum(case when a.property like '5%' and a.dirction=-1 and substring(a.vchdate,1,4) = "+endYear+"-1 then OccurValue else 0 end) CreditTotalOcc6,   "
					+ " sum(case when (a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=1 and substring(a.vchdate,1,4) = "+endYear+"-1 then OccurValue else 0 end) DebitTotalOcc0, "
					+ " sum(case when (a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=-1 and substring(a.vchdate,1,4) = "+endYear+"-1 then OccurValue else 0 end) CreditTotalOcc0 "
					
					+ " from z_subjectentryrectify a , (" 
					+ " select subjectID,SubjectName,SubjectFullName,level0,isleaf from c_accpkgsubject where accpackageid='"+AccPackageID+"' union "
					+ " select subjectID,SubjectName,SubjectFullName,level0,isleaf from z_usesubject where accpackageid='"+AccPackageID+"'  and projectid='"+projectID+"' ) b"
					+ " where a.accpackageid='"+AccPackageID+"' and a.projectid='"+projectID+"' and a.subjectid=b.subjectid group by a.subjectid ) a ,("
					+ " select subjectID,SubjectName,SubjectFullName,level0,isleaf from c_accpkgsubject where accpackageid='"+AccPackageID+"' union "
					+ " select subjectID,SubjectName,SubjectFullName,level0,isleaf from z_usesubject where accpackageid='"+AccPackageID+"'  and projectid='"+projectID+"' ) b "
					+ " where a.subjectfullname like concat(b.subjectfullname,'/%') or a.subjectfullname=b.subjectfullname  group  by b.subjectid ) a  right join (" 
					+ " select subjectID,SubjectName,SubjectFullName,level0,if(isleaf=1 ,if((select count(*) from z_usesubject where accpackageid='"+AccPackageID+"' and projectid='"+projectID+"' and subjectfullname like concat(a.subjectfullname,'/%'))>0,0,isleaf),isleaf) isleaf from c_accpkgsubject a where accpackageid='"+AccPackageID+"' union "
					+ " select subjectID,SubjectName,SubjectFullName,level0,isleaf from z_usesubject where accpackageid='"+AccPackageID+"'  and projectid='"+projectID+"' ) b "
					+ " on a.subjectid=b.subjectid where 1=1 order by subjectid";
				
			}else{
				sql ="INSERT into z_accountrectify (accpackageId,projectID,subjectid,SubjectName,"
					+ " DebitTotalOcc1, CreditTotalOcc1, "
					+ " DebitTotalOcc2, CreditTotalOcc2, "
					+ " DebitTotalOcc3, CreditTotalOcc3, "
					+ " DebitTotalOcc4, CreditTotalOcc4, "
					+ " DebitTotalOcc5, CreditTotalOcc5, "
					+ " DebitTotalOcc6, CreditTotalOcc6, " 
					+ " DebitTotalOcc0, CreditTotalOcc0,isleaf "
					+ " ) "
					+ " select ifnull(a.accpackageid,'"+AccPackageID+"') accpackageid, " 
					+ " ifnull(a.projectid,'"+projectID+"') accpackageid, "
					+ " ifnull(a.subjectid,b.subjectid) subjectid,ifnull(a.SubjectName,b.SubjectName) SubjectName,"
					+ " ifnull(a.DebitTotalOcc1,'0.00') DebitTotalOcc1,ifnull(a.CreditTotalOcc1,'0.00') CreditTotalOcc1,"
					+ " ifnull(a.DebitTotalOcc2,'0.00') DebitTotalOcc2,ifnull(a.CreditTotalOcc2,'0.00') CreditTotalOcc2,"
					+ " ifnull(a.DebitTotalOcc3,'0.00') DebitTotalOcc3,ifnull(a.CreditTotalOcc3,'0.00') CreditTotalOcc3,"
					+ " ifnull(a.DebitTotalOcc4,'0.00') DebitTotalOcc4,ifnull(a.CreditTotalOcc4,'0.00') CreditTotalOcc4,"
					+ " ifnull(a.DebitTotalOcc5,'0.00') DebitTotalOcc5,ifnull(a.CreditTotalOcc5,'0.00') CreditTotalOcc5,"
					+ " ifnull(a.DebitTotalOcc6,'0.00') DebitTotalOcc6,ifnull(a.CreditTotalOcc6,'0.00') CreditTotalOcc6,"
					
					+ " ifnull(a.DebitTotalOcc0,'0.00') DebitTotalOcc0,ifnull(a.CreditTotalOcc0,'0.00') CreditTotalOcc0 ,b.isleaf "
					
					+ " from (select a.accpackageid,a.projectid,"
					+ " b.subjectid,b.SubjectName,"
					+ " sum(DebitTotalOcc1) DebitTotalOcc1,sum(CreditTotalOcc1) CreditTotalOcc1,"
					+ " sum(DebitTotalOcc2) DebitTotalOcc2,sum(CreditTotalOcc2) CreditTotalOcc2,"
					+ " sum(DebitTotalOcc3) DebitTotalOcc3,sum(CreditTotalOcc3) CreditTotalOcc3,"
					+ " sum(DebitTotalOcc4) DebitTotalOcc4,sum(CreditTotalOcc4) CreditTotalOcc4,"
					+ " sum(DebitTotalOcc5) DebitTotalOcc5,sum(CreditTotalOcc5) CreditTotalOcc5,"
					+ " sum(DebitTotalOcc6) DebitTotalOcc6,sum(CreditTotalOcc6) CreditTotalOcc6,"
					
					+ " sum(DebitTotalOcc0) DebitTotalOcc0,sum(CreditTotalOcc0) CreditTotalOcc0 "
					
					+ " from (select a.accpackageid,a.projectid,a.subjectid,b.SubjectName,b.subjectfullname,"
		
					+ " sum(case when a.property like '3%' and a.dirction=1 and substring(a.vchdate,1,4) = '"+endYear+"' then OccurValue else 0 end) DebitTotalOcc1,"
					+ " sum(case when a.property like '3%' and a.dirction=-1 and substring(a.vchdate,1,4) = '"+endYear+"' then OccurValue else 0 end) CreditTotalOcc1,"
					+ " sum(case when a.property like '4%' and a.dirction=1 and substring(a.vchdate,1,4) = '"+endYear+"' then OccurValue else 0 end) DebitTotalOcc2,"
					+ " sum(case when a.property like '4%' and a.dirction=-1 and substring(a.vchdate,1,4) = '"+endYear+"' then OccurValue else 0 end) CreditTotalOcc2,"
					+ " sum(case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=1 and substring(a.vchdate,1,4) = '"+endYear+"' then OccurValue else 0 end) DebitTotalOcc3, "
					+ " sum(case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=-1 and substring(a.vchdate,1,4) = '"+endYear+"' then OccurValue else 0 end) CreditTotalOcc3, "
					
					+ " sum(case when a.property like '3%' and a.dirction=1 and substring(a.vchdate,1,4) < '"+endYear+"' then OccurValue else 0 end) DebitTotalOcc4,"
					+ " sum(case when a.property like '3%' and a.dirction=-1 and substring(a.vchdate,1,4) < '"+endYear+"' then OccurValue else 0 end) CreditTotalOcc4, "
					+ " sum(case when a.property like '4%' and a.dirction=1 and substring(a.vchdate,1,4) < '"+endYear+"' then OccurValue else 0 end) DebitTotalOcc5, "
					+ " sum(case when a.property like '4%' and a.dirction=-1 and substring(a.vchdate,1,4) < '"+endYear+"' then OccurValue else 0 end) CreditTotalOcc5, "
					+ " sum(case when a.property like '5%' and a.dirction=1 and substring(a.vchdate,1,4) < '"+endYear+"' then OccurValue else 0 end) DebitTotalOcc6,   "
					+ " sum(case when a.property like '5%' and a.dirction=-1 and substring(a.vchdate,1,4) < '"+endYear+"' then OccurValue else 0 end) CreditTotalOcc6,   "
					+ " sum(case when (a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=1 and substring(a.vchdate,1,4) < '"+endYear+"' then OccurValue else 0 end) DebitTotalOcc0, "
					+ " sum(case when (a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=-1 and substring(a.vchdate,1,4) < '"+endYear+"' then OccurValue else 0 end) CreditTotalOcc0 "

					
					+ " from z_subjectentryrectify a , (" 
					+ " select subjectID,SubjectName,SubjectFullName,level0,isleaf from c_accpkgsubject where accpackageid='"+AccPackageID+"' union "
					+ " select subjectID,SubjectName,SubjectFullName,level0,isleaf from z_usesubject where accpackageid='"+AccPackageID+"'  and projectid='"+projectID+"' ) b"
					+ " where a.accpackageid='"+AccPackageID+"' and a.projectid='"+projectID+"' and a.subjectid=b.subjectid group by a.subjectid ) a ,("
					+ " select subjectID,SubjectName,SubjectFullName,level0,isleaf from c_accpkgsubject where accpackageid='"+AccPackageID+"' union "
					+ " select subjectID,SubjectName,SubjectFullName,level0,isleaf from z_usesubject where accpackageid='"+AccPackageID+"'  and projectid='"+projectID+"' ) b "
					+ " where a.subjectfullname like concat(b.subjectfullname,'/%') or a.subjectfullname=b.subjectfullname  group  by b.subjectid ) a  right join (" 
					+ " select subjectID,SubjectName,SubjectFullName,level0,if(isleaf=1 ,if((select count(*) from z_usesubject where accpackageid='"+AccPackageID+"' and projectid='"+projectID+"' and subjectfullname like concat(a.subjectfullname,'/%'))>0,0,isleaf),isleaf) isleaf from c_accpkgsubject a where accpackageid='"+AccPackageID+"' union "
					+ " select subjectID,SubjectName,SubjectFullName,level0,isleaf from z_usesubject where accpackageid='"+AccPackageID+"'  and projectid='"+projectID+"' ) b "
					+ " on a.subjectid=b.subjectid where 1=1 order by subjectid";
			}

			org.util.Debug.prtOut("createTzhz = |" + sql);
			ps = conn.prepareStatement(sql);
			ps.execute();
			ps.close();
			
			org.util.Debug.prtOut("createTzhz 1 = |" + new ASFuntion().getCurrentTime());
			
			/**
			 * 新增c_account科目
			 * 在用户科目体系存在，而余额表中不存在（则期初，发生，期末都为0）
			 * 当发生调整重分类时，在余额表新增12条［期初，发生，期末都为0］的科目
			 */
			int ii = 3;
//			sql = "insert into c_account(accpackageid,subjectid,accname,subjectfullname1,isleaf1,level1,SubYearMonth,SubMonth,DataName,direction, DebitRemain,CreditRemain,DebitOcc,CreditOcc, Balance,DebitTotalOcc,CreditTotalOcc,DebitBalance,CreditBalance,subjectfullname2,  direction2,standname ,tokenid)  " +
			sql = " select distinct accpackageid,subjectid,SubjectName,SubjectFullName,IsLeaf,level0,substring(accpackageid,7) as year,b.submonth,0, case substring(property,2,1) when 2 then '-1' else substring(property,2,1) end as direction, 0,0,0,0,0,0,0,0,0,subjectfullname2,  direction2 ,standname ,tokenid " +
			" from (" +
			" select a.* ," +
			" case when a.level0=1 then a.subjectfullname2 else concat(d.subjectfullname2,'/',a.SubjectName) end as tokenid " +
			" from (" +
			" 	select a.*," +
			" 	case when c.level0=1 then concat(c.standkey,substring(a.subjectfullname,locate('/',a.subjectfullname))) when c.level0=2 then concat(c.standkey,'/',a.subjectfullname) else a.subjectfullname end as subjectfullname2," +
			" 	case ifnull(c.property,substring(a.property,2,1)) when 2 then -1 else ifnull(c.property,substring(a.property,2,1)) end as direction2,SubjectName as standname " +
			" 	from (" +
			
			" 		select b.* " +
			" 		from z_accountrectify a ,c_accpkgsubject b  " +
			" 		where a.AccPackageID='"+AccPackageID+"' " +
			" 		and ProjectID='"+projectID+"' " +
			" 		and b.AccPackageID='"+AccPackageID+"'  " +
			" 		and abs(a.credittotalocc1)+ ABS(a.debittotalocc1)+abs(a.credittotalocc2)+ ABS(a.debittotalocc2)+abs(a.credittotalocc3)+ ABS(a.debittotalocc3)+abs(a.credittotalocc4)+ ABS(a.debittotalocc4)+abs(a.credittotalocc5)+ ABS(a.debittotalocc5) +abs(a.credittotalocc6)+ ABS(a.debittotalocc6)+abs(a.credittotalocc0)+ ABS(a.debittotalocc0)>0  " +
			" 		and a.SubjectID=b.SubjectID " +
			
			" 	) a " +
			" 	left join c_account b on a.accpackageid=b.accpackageid and a.SubjectID=b.SubjectID and SubMonth=1 " +
			" 	left join z_keyresult c on c.standkey not like '%/%' and (a.subjectfullname=c.userkey  or a.subjectfullname like concat(c.userkey,'/','%'))  " +
			" 	where b.SubjectID is null  " +
			" ) a " +
			" left join c_account d on a.accpackageid=d.accpackageid and (a.subjectfullname=d.subjectfullname1 or a.subjectfullname like concat(d.subjectfullname1,'/%')) and d.level1=1 and d.SubMonth=1 " +
			" ) a ,k_month b where b.monthtype=12 ";

			
			for(int i = 0 ;i<ii;i++){
				
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				if(!rs.next()){
					System.out.println(i + "|" + sql);
					i = 4;
				}
				DbUtil.close(rs);
				DbUtil.close(ps);
				
				
				String sql1 = "insert into c_account(accpackageid,subjectid,accname,subjectfullname1,isleaf1,level1,SubYearMonth,SubMonth,DataName,direction, DebitRemain,CreditRemain,DebitOcc,CreditOcc, Balance,DebitTotalOcc,CreditTotalOcc,DebitBalance,CreditBalance,subjectfullname2,  direction2,standname ,tokenid)  " + sql;
				ps = conn.prepareStatement(sql1);			
				ps.execute();
				DbUtil.close(ps);
		
			}
			
//			//org.util.Debug.prtOut("createTzhz 新增科目= |"+sql);			
			org.util.Debug.prtOut("createTzhz 2 = |" + new ASFuntion().getCurrentTime());
			
			
//			String string = "";
//			
//			AccPackageExtService accPackageExtService = new AccPackageExtService(conn);
//			string = accPackageExtService.get(AccPackageID, "allsubjectid");
//			if("".equals(string)){
//				string = accPackageExtService.subjectentry(AccPackageID); 
//				if("".equals(string)){
//					string = "''";
//				}
//			}
//			
//			//org.util.Debug.prtOut("createTzhz 31 = |" + new ASFuntion().getCurrentTime());
//			
//			/**
//			 * 删除c_account科目
//			 * 删除余额表中［期初，发生，期末都为0］和［调整汇总表的汇总都为0］的科目
//			 */
//			
//			sql = "select distinct AccPackageID,a.SubjectID from c_account a " +				
//				" where AccPackageID='"+AccPackageID+"' " +
//				" and a.subjectid not in ("+string+") " +
//				" group by a.SubjectID " + 
//				" having sum(abs(DebitRemain)+abs(CreditRemain)+abs(DebitOcc)+abs(CreditOcc)+abs(DebitTotalOcc)+abs(CreditTotalOcc)+abs(DebitBalance)+abs(CreditBalance)+abs(Balance))=0 " ;
//			ps = conn.prepareStatement(sql);
//			rs = ps.executeQuery();
//			String s1 = "";
//			while(rs.next()){
//				s1 += ",'"+rs.getString("SubjectID")+"'";
//			}
//			rs.close();
//			ps.close();
//			if(!"".equals(s1)){
//				s1 = s1.substring(1);
//			}
//			
//			org.util.Debug.prtOut("createTzhz 32 = |" + new ASFuntion().getCurrentTime());
//			
//			if(!"".equals(s1)){
//				sql = "delete from c_account where AccPackageID='"+AccPackageID+"' and SubjectID in (" +
//				" 	select a.SubjectID from z_accountrectify a " +
//				" where a.AccPackageID='"+AccPackageID+"' and ProjectID='"+projectID+"' " + 
//				" and abs(a.credittotalocc1)+ ABS(a.debittotalocc1)+abs(a.credittotalocc2)+ ABS(a.debittotalocc2)+abs(a.credittotalocc3)+ ABS(a.debittotalocc3)+abs(a.credittotalocc4)+ ABS(a.debittotalocc4)+abs(a.credittotalocc5)+ ABS(a.debittotalocc5) +abs(a.credittotalocc6)+ ABS(a.debittotalocc6)+abs(a.credittotalocc0)+ ABS(a.debittotalocc0)=0 " +
//				" and a.SubjectID in ("+s1+") )";
////				//org.util.Debug.prtOut("createTzhz 删除科目= |"+sql);
//				ps = conn.prepareStatement(sql);
//				ps.execute();
//				ps.close();
//			}
			
			
			
			org.util.Debug.prtOut("createTzhz 3 = |" + new ASFuntion().getCurrentTime());
			
			createTzhzYear( AccPackageID, projectID);	//往年调整
			
			//新调整汇总
			createSubject( AccPackageID, projectID);
			createAssitemNew( AccPackageID, projectID);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	public void createTzhzYear(String AccPackageID,String projectID) throws Exception {
//		PreparedStatement ps = null;
		Statement st = null;
		ResultSet rs = null;
		String sql = "";
		try {
			String endYear = this.getProjectEndYear(projectID);
			
			st = conn.createStatement();
			
			sql = "DELETE from z_accountyearrectify where  projectID=" + projectID;
			st.execute(sql);
		
			String svalue = "";
			sql = "select svalue from s_config where sname='上年调整影响年末'";
			rs = st.executeQuery(sql);
			while(rs.next()){
				svalue = rs.getString(1);
			}
			DbUtil.close(rs);
			
			sql = "select distinct min(substring(vchdate,1,4)) from z_subjectentryrectify where projectid='"+projectID+"' and substring(vchdate,1,4)<'"+endYear+"' order by vchdate desc ";
			rs = st.executeQuery(sql);
			int min = 0;
			if(rs.next()){
				min = rs.getInt(1);
			}
			DbUtil.close(rs);
			
			if(min == 0) return;
			
			for(int ivyear=Integer.parseInt(endYear)-1;ivyear>=min;ivyear--){
				String vyear = String.valueOf(ivyear);
				if("否".equals(svalue)){
					sql = "INSERT into z_accountyearrectify (accpackageId,projectID,yearrectify,subjectid,SubjectName,"
						+ " DebitTotalOcc1, CreditTotalOcc1, "
						+ " DebitTotalOcc2, CreditTotalOcc2, "
						+ " DebitTotalOcc3, CreditTotalOcc3, "
						+ " DebitTotalOcc4, CreditTotalOcc4, "
						+ " DebitTotalOcc5, CreditTotalOcc5, "
						+ " DebitTotalOcc6, CreditTotalOcc6, " 
						+ " DebitTotalOcc0, CreditTotalOcc0,isleaf1 "
						+ " ) "
						+ " select a.accpackageid,a.projectid, a.yearrectify,b.subjectid,b.SubjectName," 
						+ " sum(DebitTotalOcc1) DebitTotalOcc1,sum(CreditTotalOcc1) CreditTotalOcc1, "
						+ " sum(DebitTotalOcc2) DebitTotalOcc2,sum(CreditTotalOcc2) CreditTotalOcc2, "
						+ " sum(DebitTotalOcc3) DebitTotalOcc3,sum(CreditTotalOcc3) CreditTotalOcc3, " 
						+ " sum(DebitTotalOcc4) DebitTotalOcc4,sum(CreditTotalOcc4) CreditTotalOcc4, "
						+ " sum(DebitTotalOcc5) DebitTotalOcc5,sum(CreditTotalOcc5) CreditTotalOcc5, "
						+ " sum(DebitTotalOcc6) DebitTotalOcc6,sum(CreditTotalOcc6) CreditTotalOcc6, "
						+ " sum(DebitTotalOcc0) DebitTotalOcc0,sum(CreditTotalOcc0) CreditTotalOcc0,b.isleaf  from ( "
						+ " select a.accpackageid,a.projectid,'"+vyear+"' yearrectify,a.subjectid,b.SubjectName,b.subjectfullname, "
						+ " sum(case when a.property like '3%' and a.dirction=1 and substring(a.vchdate,1,4) = '"+vyear+"' then OccurValue else 0 end) DebitTotalOcc1,"
						+ " sum(case when a.property like '3%' and a.dirction=-1 and substring(a.vchdate,1,4) = '"+vyear+"' then OccurValue else 0 end) CreditTotalOcc1,"
						+ " sum(case when a.property like '4%' and a.dirction=1 and substring(a.vchdate,1,4) = '"+vyear+"' then OccurValue else 0 end) DebitTotalOcc2,"
						+ " sum(case when a.property like '4%' and a.dirction=-1 and substring(a.vchdate,1,4) = '"+vyear+"' then OccurValue else 0 end) CreditTotalOcc2,"
						+ " sum(case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=1 and substring(a.vchdate,1,4) = '"+vyear+"' then OccurValue else 0 end) DebitTotalOcc3," 
						+ " sum(case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=-1 and substring(a.vchdate,1,4) = '"+vyear+"' then OccurValue else 0 end) CreditTotalOcc3," 

						+ " sum(case when a.property like '3%' and a.dirction=1 and substring(a.vchdate,1,4) = "+vyear+"-1 then OccurValue else 0 end) DebitTotalOcc4,"
						+ " sum(case when a.property like '3%' and a.dirction=-1 and substring(a.vchdate,1,4) = "+vyear+"-1 then OccurValue else 0 end) CreditTotalOcc4,"
						+ " sum(case when a.property like '4%' and a.dirction=1 and substring(a.vchdate,1,4) = "+vyear+"-1 then OccurValue else 0 end) DebitTotalOcc5,"
						+ " sum(case when a.property like '4%' and a.dirction=-1 and substring(a.vchdate,1,4) = "+vyear+"-1 then OccurValue else 0 end) CreditTotalOcc5,"
						+ " sum(case when a.property like '5%' and a.dirction=1 and substring(a.vchdate,1,4) = "+vyear+"-1 then OccurValue else 0 end) DebitTotalOcc6,  "
						+ " sum(case when a.property like '5%' and a.dirction=-1 and substring(a.vchdate,1,4) = "+vyear+"-1 then OccurValue else 0 end) CreditTotalOcc6, " 
						+ " sum(case when (a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=1 and substring(a.vchdate,1,4) = "+vyear+"-1 then OccurValue else 0 end) DebitTotalOcc0," 
						+ " sum(case when (a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=-1 and substring(a.vchdate,1,4) = "+vyear+"-1 then OccurValue else 0 end) CreditTotalOcc0  "
						+ " from z_subjectentryrectify a , ( " 
						+ " select subjectID,SubjectName,SubjectFullName,level0,isleaf from c_accpkgsubject where accpackageid='"+AccPackageID+"' " 
						+ " union  "
						+ " select subjectID,SubjectName,SubjectFullName,level0,isleaf from z_usesubject where accpackageid='"+AccPackageID+"'  and projectid='"+projectID+"' " 
						+ " ) b where a.accpackageid='"+AccPackageID+"' and a.projectid='"+projectID+"' and a.subjectid=b.subjectid "
						+ " group by a.subjectid "
						+ " ) a ,( "
						+ " select subjectID,SubjectName,SubjectFullName,level0,if(isleaf=1 ,if((select count(*) from z_usesubject where accpackageid='"+AccPackageID+"' and projectid='"+projectID+"' and subjectfullname like concat(a.subjectfullname,'/%'))>0,0,isleaf),isleaf) isleaf from c_accpkgsubject a where accpackageid='"+AccPackageID+"' " 
						+ " union  "
						+ " select subjectID,SubjectName,SubjectFullName,level0,isleaf from z_usesubject where accpackageid='"+AccPackageID+"'  and projectid='"+projectID+"' " 
						+ " ) b  where a.subjectfullname like concat(b.subjectfullname,'/%') or a.subjectfullname=b.subjectfullname  group  by b.subjectid ";

				}else{
					sql = "INSERT into z_accountyearrectify (accpackageId,projectID,yearrectify,subjectid,SubjectName,"
						+ " DebitTotalOcc1, CreditTotalOcc1, "
						+ " DebitTotalOcc2, CreditTotalOcc2, "
						+ " DebitTotalOcc3, CreditTotalOcc3, "
						+ " DebitTotalOcc4, CreditTotalOcc4, "
						+ " DebitTotalOcc5, CreditTotalOcc5, "
						+ " DebitTotalOcc6, CreditTotalOcc6, " 
						+ " DebitTotalOcc0, CreditTotalOcc0,isleaf1 "
						+ " ) "
						+ " select a.accpackageid,a.projectid, a.yearrectify,b.subjectid,b.SubjectName," 
						+ " sum(DebitTotalOcc1) DebitTotalOcc1,sum(CreditTotalOcc1) CreditTotalOcc1, "
						+ " sum(DebitTotalOcc2) DebitTotalOcc2,sum(CreditTotalOcc2) CreditTotalOcc2, "
						+ " sum(DebitTotalOcc3) DebitTotalOcc3,sum(CreditTotalOcc3) CreditTotalOcc3, " 
						+ " sum(DebitTotalOcc4) DebitTotalOcc4,sum(CreditTotalOcc4) CreditTotalOcc4, "
						+ " sum(DebitTotalOcc5) DebitTotalOcc5,sum(CreditTotalOcc5) CreditTotalOcc5, "
						+ " sum(DebitTotalOcc6) DebitTotalOcc6,sum(CreditTotalOcc6) CreditTotalOcc6, "
						+ " sum(DebitTotalOcc0) DebitTotalOcc0,sum(CreditTotalOcc0) CreditTotalOcc0,b.isleaf  from ( "
						+ " select a.accpackageid,a.projectid,'"+vyear+"' yearrectify,a.subjectid,b.SubjectName,b.subjectfullname, "
						+ " sum(case when a.property like '3%' and a.dirction=1 and substring(a.vchdate,1,4) = '"+vyear+"' then OccurValue else 0 end) DebitTotalOcc1,"
						+ " sum(case when a.property like '3%' and a.dirction=-1 and substring(a.vchdate,1,4) = '"+vyear+"' then OccurValue else 0 end) CreditTotalOcc1,"
						+ " sum(case when a.property like '4%' and a.dirction=1 and substring(a.vchdate,1,4) = '"+vyear+"' then OccurValue else 0 end) DebitTotalOcc2,"
						+ " sum(case when a.property like '4%' and a.dirction=-1 and substring(a.vchdate,1,4) = '"+vyear+"' then OccurValue else 0 end) CreditTotalOcc2,"
						+ " sum(case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=1 and substring(a.vchdate,1,4) = '"+vyear+"' then OccurValue else 0 end) DebitTotalOcc3," 
						+ " sum(case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=-1 and substring(a.vchdate,1,4) = '"+vyear+"' then OccurValue else 0 end) CreditTotalOcc3," 

						+ " sum(case when a.property like '3%' and a.dirction=1 and substring(a.vchdate,1,4) < '"+vyear+"' then OccurValue else 0 end) DebitTotalOcc4,"
						+ " sum(case when a.property like '3%' and a.dirction=-1 and substring(a.vchdate,1,4) < '"+vyear+"' then OccurValue else 0 end) CreditTotalOcc4,"
						+ " sum(case when a.property like '4%' and a.dirction=1 and substring(a.vchdate,1,4) < '"+vyear+"' then OccurValue else 0 end) DebitTotalOcc5,"
						+ " sum(case when a.property like '4%' and a.dirction=-1 and substring(a.vchdate,1,4) < '"+vyear+"' then OccurValue else 0 end) CreditTotalOcc5,"
						+ " sum(case when a.property like '5%' and a.dirction=1 and substring(a.vchdate,1,4) < '"+vyear+"' then OccurValue else 0 end) DebitTotalOcc6,  "
						+ " sum(case when a.property like '5%' and a.dirction=-1 and substring(a.vchdate,1,4) < '"+vyear+"' then OccurValue else 0 end) CreditTotalOcc6, " 
						+ " sum(case when (a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=1 and substring(a.vchdate,1,4) < '"+vyear+"' then OccurValue else 0 end) DebitTotalOcc0," 
						+ " sum(case when (a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=-1 and substring(a.vchdate,1,4) < '"+vyear+"' then OccurValue else 0 end) CreditTotalOcc0  "
						+ " from z_subjectentryrectify a , ( " 
						+ " select subjectID,SubjectName,SubjectFullName,level0,isleaf from c_accpkgsubject where accpackageid='"+AccPackageID+"' " 
						+ " union  "
						+ " select subjectID,SubjectName,SubjectFullName,level0,isleaf from z_usesubject where accpackageid='"+AccPackageID+"'  and projectid='"+projectID+"' " 
						+ " ) b where a.accpackageid='"+AccPackageID+"' and a.projectid='"+projectID+"' and a.subjectid=b.subjectid "
						+ " group by a.subjectid "
						+ " ) a ,( "
						+ " select subjectID,SubjectName,SubjectFullName,level0,if(isleaf=1 ,if((select count(*) from z_usesubject where accpackageid='"+AccPackageID+"' and projectid='"+projectID+"' and subjectfullname like concat(a.subjectfullname,'/%'))>0,0,isleaf),isleaf) isleaf from c_accpkgsubject a where accpackageid='"+AccPackageID+"' " 
						+ " union  "
						+ " select subjectID,SubjectName,SubjectFullName,level0,isleaf from z_usesubject where accpackageid='"+AccPackageID+"'  and projectid='"+projectID+"' " 
						+ " ) b  where a.subjectfullname like concat(b.subjectfullname,'/%') or a.subjectfullname=b.subjectfullname  group  by b.subjectid ";

				}
				
				org.util.Debug.prtOut("createTzhzYear = |"+sql);
				
				st.addBatch(sql);
				
			}
			st.executeBatch();
			
			//org.util.Debug.prtOut("createTzhzYear 3 = |" + new ASFuntion().getCurrentTime());
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(st);
		}
	}
	
	public void createWbTzHzYear(String AccPackageID,String projectID) throws Exception {
		Statement st = null;
		ResultSet rs = null;
		String sql = "";
		try {
			String endYear = this.getProjectEndYear(projectID);
			
			st = conn.createStatement();
			sql = "DELETE from z_accountallyearrectify where  projectID=" + projectID;
			st.execute(sql);
			
			String svalue = "";
			sql = "select svalue from s_config where sname='上年调整影响年末'";
			rs = st.executeQuery(sql);
			while(rs.next()){
				svalue = rs.getString(1);
			}
			DbUtil.close(rs);
			
			sql = "select distinct min(substring(vchdate,1,4)) from z_subjectentryrectify where projectid='"+projectID+"' and substring(vchdate,1,4)<'"+endYear+"' order by vchdate desc ";
			rs = st.executeQuery(sql);
			int min = 0;
			if(rs.next()){
				min = rs.getInt(1);
			}
			DbUtil.close(rs);
			
			if(min == 0) return;
			
			for(int ivyear=Integer.parseInt(endYear)-1;ivyear>=min;ivyear--){
				String vyear = String.valueOf(ivyear);
				if("否".equals(svalue)){
					sql = "INSERT into z_accountallyearrectify (accpackageId,projectID,yearrectify,subjectid,SubjectName,DataName,"
						+ " DebitTotalOcc1, CreditTotalOcc1, DebitTotalOcc2, CreditTotalOcc2, "
						+ " DebitTotalOcc3, CreditTotalOcc3, DebitTotalOcc4, CreditTotalOcc4, "
						+ " DebitTotalOcc5, CreditTotalOcc5, DebitTotalOcc6, CreditTotalOcc6, "
						
						+ " DebitTotalOcc1f, CreditTotalOcc1f, DebitTotalOcc2f, CreditTotalOcc2f, "
						+ " DebitTotalOcc3f, CreditTotalOcc3f, DebitTotalOcc4f, CreditTotalOcc4f, "
						+ " DebitTotalOcc5f, CreditTotalOcc5f, DebitTotalOcc6f, CreditTotalOcc6f, "
						
						+ " isleaf1) "
						+ " select distinct a.accpackageid,a.projectid,'"+vyear+"' yearrectify,"
						+ " if(b.isleaf1=1,a.subjectid,b.subjectid) subjectid,if(b.isleaf1=1,a.accname,b.accname) accname,a.Currency,"
						
						+ " sum(DebitTotalOcc1) DebitTotalOcc1,sum(CreditTotalOcc1) CreditTotalOcc1,"
						+ " sum(DebitTotalOcc2) DebitTotalOcc2,sum(CreditTotalOcc2) CreditTotalOcc2,"
						+ " sum(DebitTotalOcc3) DebitTotalOcc3,sum(CreditTotalOcc3) CreditTotalOcc3,"
						+ " sum(DebitTotalOcc4) DebitTotalOcc4,sum(CreditTotalOcc4) CreditTotalOcc4,"
						+ " sum(DebitTotalOcc5) DebitTotalOcc5,sum(CreditTotalOcc5) CreditTotalOcc5,"
						+ " sum(DebitTotalOcc6) DebitTotalOcc6,sum(CreditTotalOcc6) CreditTotalOcc6,"
						
						+ " sum(DebitTotalOcc1f) DebitTotalOcc1f,sum(CreditTotalOcc1f) CreditTotalOcc1f,"
						+ " sum(DebitTotalOcc2f) DebitTotalOcc2f,sum(CreditTotalOcc2f) CreditTotalOcc2f,"
						+ " sum(DebitTotalOcc3f) DebitTotalOcc3f,sum(CreditTotalOcc3f) CreditTotalOcc3f,"
						+ " sum(DebitTotalOcc4f) DebitTotalOcc4f,sum(CreditTotalOcc4f) CreditTotalOcc4f,"
						+ " sum(DebitTotalOcc5f) DebitTotalOcc5f,sum(CreditTotalOcc5f) CreditTotalOcc5f,"
						+ " sum(DebitTotalOcc6f) DebitTotalOcc6f,sum(CreditTotalOcc6f) CreditTotalOcc6f,"
						
						
						+ " b.isleaf1 "
						+ " from (select distinct a.accpackageid,a.projectid,a.subjectid,b.accname,b.subjectfullname1,a.Currency,"
						
						+ " sum(case when a.property like '3%' and a.dirction=1 and substring(a.vchdate,1,4) = '"+vyear+"' then CurrValue else 0 end) DebitTotalOcc1,"
						+ " sum(case when a.property like '3%' and a.dirction=-1 and substring(a.vchdate,1,4) = '"+vyear+"' then CurrValue else 0 end) CreditTotalOcc1,"
						+ " sum(case when a.property like '4%' and a.dirction=1 and substring(a.vchdate,1,4) = '"+vyear+"' then CurrValue else 0 end) DebitTotalOcc2,"
						+ " sum(case when a.property like '4%' and a.dirction=-1 and substring(a.vchdate,1,4) = '"+vyear+"' then CurrValue else 0 end) CreditTotalOcc2,"
						+ " sum(case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=1 and substring(a.vchdate,1,4) = '"+vyear+"' then CurrValue else 0 end) DebitTotalOcc3," 
						+ " sum(case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=-1 and substring(a.vchdate,1,4) = '"+vyear+"' then CurrValue else 0 end) CreditTotalOcc3," 
						+ " sum(case when a.property like '3%' and a.dirction=1 and substring(a.vchdate,1,4) = "+vyear+"-1 then CurrValue else 0 end) DebitTotalOcc4,"
						+ " sum(case when a.property like '3%' and a.dirction=-1 and substring(a.vchdate,1,4) = "+vyear+"-1 then CurrValue else 0 end) CreditTotalOcc4,"
						+ " sum(case when a.property like '4%' and a.dirction=1 and substring(a.vchdate,1,4) = "+vyear+"-1 then CurrValue else 0 end) DebitTotalOcc5,"
						+ " sum(case when a.property like '4%' and a.dirction=-1 and substring(a.vchdate,1,4) = "+vyear+"-1 then CurrValue else 0 end) CreditTotalOcc5,"
						+ " sum(case when a.property like '5%' and a.dirction=1 and substring(a.vchdate,1,4) = "+vyear+"-1 then CurrValue else 0 end) DebitTotalOcc6,  "
						+ " sum(case when a.property like '5%' and a.dirction=-1 and substring(a.vchdate,1,4) = "+vyear+"-1 then CurrValue else 0 end) CreditTotalOcc6, "

						+ " sum(case when a.property like '3%' and a.dirction=1 and substring(a.vchdate,1,4) = '"+vyear+"' then OccurValue else 0 end) DebitTotalOcc1f,"
						+ " sum(case when a.property like '3%' and a.dirction=-1 and substring(a.vchdate,1,4) = '"+vyear+"' then OccurValue else 0 end) CreditTotalOcc1f,"
						+ " sum(case when a.property like '4%' and a.dirction=1 and substring(a.vchdate,1,4) = '"+vyear+"' then OccurValue else 0 end) DebitTotalOcc2f,"
						+ " sum(case when a.property like '4%' and a.dirction=-1 and substring(a.vchdate,1,4) = '"+vyear+"' then OccurValue else 0 end) CreditTotalOcc2f,"
						+ " sum(case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=1 and substring(a.vchdate,1,4) = '"+vyear+"' then OccurValue else 0 end) DebitTotalOcc3f," 
						+ " sum(case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=-1 and substring(a.vchdate,1,4) = '"+vyear+"' then OccurValue else 0 end) CreditTotalOcc3f," 
						+ " sum(case when a.property like '3%' and a.dirction=1 and substring(a.vchdate,1,4) = "+vyear+"-1 then OccurValue else 0 end) DebitTotalOcc4f,"
						+ " sum(case when a.property like '3%' and a.dirction=-1 and substring(a.vchdate,1,4) = "+vyear+"-1 then OccurValue else 0 end) CreditTotalOcc4f,"
						+ " sum(case when a.property like '4%' and a.dirction=1 and substring(a.vchdate,1,4) = "+vyear+"-1 then OccurValue else 0 end) DebitTotalOcc5f,"
						+ " sum(case when a.property like '4%' and a.dirction=-1 and substring(a.vchdate,1,4) = "+vyear+"-1 then OccurValue else 0 end) CreditTotalOcc5f,"
						+ " sum(case when a.property like '5%' and a.dirction=1 and substring(a.vchdate,1,4) = "+vyear+"-1 then OccurValue else 0 end) DebitTotalOcc6f,  "
						+ " sum(case when a.property like '5%' and a.dirction=-1 and substring(a.vchdate,1,4) = "+vyear+"-1 then OccurValue else 0 end) CreditTotalOcc6f "

						+ " from z_subjectentryrectify a,c_accountall b "
						+ " where a.accpackageid='"+ AccPackageID+ "' and a.projectid='"+ projectID + "' "
//						+ " and a.Currency <>'' "
						+ " and b.accpackageid='"+ AccPackageID
						+ "' and b.submonth=1 and b.accsign=1 "
						+ " and a.subjectid=b.subjectid "
//						+ " and a.Currency=b.dataName "
						+ " group by b.dataName,b.subjectid" 
						+ " ) a , (" 
						+ " select * from c_accountall "
						+ " where accpackageid='"+ AccPackageID+ "' and submonth=1 and accsign=1 " 
						+ " ) b "
						+ " where (a.subjectfullname1 like concat(b.subjectfullname1,'/%') or a.subjectfullname1=b.subjectfullname1) "
//						+ " and a.Currency=b.dataName "
						+ " group  by b.dataName,a.Currency,b.subjectid";
					
				}else{
					sql = "INSERT into z_accountallyearrectify (accpackageId,projectID,yearrectify,subjectid,SubjectName,DataName,"
						+ " DebitTotalOcc1, CreditTotalOcc1, DebitTotalOcc2, CreditTotalOcc2, "
						+ " DebitTotalOcc3, CreditTotalOcc3, DebitTotalOcc4, CreditTotalOcc4, "
						+ " DebitTotalOcc5, CreditTotalOcc5, DebitTotalOcc6, CreditTotalOcc6, "
						
						+ " DebitTotalOcc1f, CreditTotalOcc1f, DebitTotalOcc2f, CreditTotalOcc2f, "
						+ " DebitTotalOcc3f, CreditTotalOcc3f, DebitTotalOcc4f, CreditTotalOcc4f, "
						+ " DebitTotalOcc5f, CreditTotalOcc5f, DebitTotalOcc6f, CreditTotalOcc6f, "
						
						+ " isleaf1) "
						+ " select distinct a.accpackageid,a.projectid,'"+vyear+"' yearrectify,"
						+ " if(b.isleaf1=1,a.subjectid,b.subjectid) subjectid,if(b.isleaf1=1,a.accname,b.accname) accname,a.Currency,"
						
						+ " sum(DebitTotalOcc1) DebitTotalOcc1,sum(CreditTotalOcc1) CreditTotalOcc1,"
						+ " sum(DebitTotalOcc2) DebitTotalOcc2,sum(CreditTotalOcc2) CreditTotalOcc2,"
						+ " sum(DebitTotalOcc3) DebitTotalOcc3,sum(CreditTotalOcc3) CreditTotalOcc3,"
						+ " sum(DebitTotalOcc4) DebitTotalOcc4,sum(CreditTotalOcc4) CreditTotalOcc4,"
						+ " sum(DebitTotalOcc5) DebitTotalOcc5,sum(CreditTotalOcc5) CreditTotalOcc5,"
						+ " sum(DebitTotalOcc6) DebitTotalOcc6,sum(CreditTotalOcc6) CreditTotalOcc6,"
						
						+ " sum(DebitTotalOcc1f) DebitTotalOcc1f,sum(CreditTotalOcc1f) CreditTotalOcc1f,"
						+ " sum(DebitTotalOcc2f) DebitTotalOcc2f,sum(CreditTotalOcc2f) CreditTotalOcc2f,"
						+ " sum(DebitTotalOcc3f) DebitTotalOcc3f,sum(CreditTotalOcc3f) CreditTotalOcc3f,"
						+ " sum(DebitTotalOcc4f) DebitTotalOcc4f,sum(CreditTotalOcc4f) CreditTotalOcc4f,"
						+ " sum(DebitTotalOcc5f) DebitTotalOcc5f,sum(CreditTotalOcc5f) CreditTotalOcc5f,"
						+ " sum(DebitTotalOcc6f) DebitTotalOcc6f,sum(CreditTotalOcc6f) CreditTotalOcc6f,"
						
						
						+ " b.isleaf1 "
						+ " from (select distinct a.accpackageid,a.projectid,a.subjectid,b.accname,b.subjectfullname1,a.Currency,"
						
						+ " sum(case when a.property like '3%' and a.dirction=1 and substring(a.vchdate,1,4) = '"+vyear+"' then CurrValue else 0 end) DebitTotalOcc1,"
						+ " sum(case when a.property like '3%' and a.dirction=-1 and substring(a.vchdate,1,4) = '"+vyear+"' then CurrValue else 0 end) CreditTotalOcc1,"
						+ " sum(case when a.property like '4%' and a.dirction=1 and substring(a.vchdate,1,4) = '"+vyear+"' then CurrValue else 0 end) DebitTotalOcc2,"
						+ " sum(case when a.property like '4%' and a.dirction=-1 and substring(a.vchdate,1,4) = '"+vyear+"' then CurrValue else 0 end) CreditTotalOcc2,"
						+ " sum(case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=1 and substring(a.vchdate,1,4) = '"+vyear+"' then CurrValue else 0 end) DebitTotalOcc3," 
						+ " sum(case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=-1 and substring(a.vchdate,1,4) = '"+vyear+"' then CurrValue else 0 end) CreditTotalOcc3," 
						+ " sum(case when a.property like '3%' and a.dirction=1 and substring(a.vchdate,1,4) < '"+vyear+"' then CurrValue else 0 end) DebitTotalOcc4,"
						+ " sum(case when a.property like '3%' and a.dirction=-1 and substring(a.vchdate,1,4) < '"+vyear+"' then CurrValue else 0 end) CreditTotalOcc4,"
						+ " sum(case when a.property like '4%' and a.dirction=1 and substring(a.vchdate,1,4) < '"+vyear+"' then CurrValue else 0 end) DebitTotalOcc5,"
						+ " sum(case when a.property like '4%' and a.dirction=-1 and substring(a.vchdate,1,4) < '"+vyear+"' then CurrValue else 0 end) CreditTotalOcc5,"
						+ " sum(case when a.property like '5%' and a.dirction=1 and substring(a.vchdate,1,4) < '"+vyear+"' then CurrValue else 0 end) DebitTotalOcc6,  "
						+ " sum(case when a.property like '5%' and a.dirction=-1 and substring(a.vchdate,1,4) < '"+vyear+"' then CurrValue else 0 end) CreditTotalOcc6, "

						+ " sum(case when a.property like '3%' and a.dirction=1 and substring(a.vchdate,1,4) = '"+vyear+"' then OccurValue else 0 end) DebitTotalOcc1f,"
						+ " sum(case when a.property like '3%' and a.dirction=-1 and substring(a.vchdate,1,4) = '"+vyear+"' then OccurValue else 0 end) CreditTotalOcc1f,"
						+ " sum(case when a.property like '4%' and a.dirction=1 and substring(a.vchdate,1,4) = '"+vyear+"' then OccurValue else 0 end) DebitTotalOcc2f,"
						+ " sum(case when a.property like '4%' and a.dirction=-1 and substring(a.vchdate,1,4) = '"+vyear+"' then OccurValue else 0 end) CreditTotalOcc2f,"
						+ " sum(case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=1 and substring(a.vchdate,1,4) = '"+vyear+"' then OccurValue else 0 end) DebitTotalOcc3f," 
						+ " sum(case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=-1 and substring(a.vchdate,1,4) = '"+vyear+"' then OccurValue else 0 end) CreditTotalOcc3f," 
						+ " sum(case when a.property like '3%' and a.dirction=1 and substring(a.vchdate,1,4) < '"+vyear+"' then OccurValue else 0 end) DebitTotalOcc4f,"
						+ " sum(case when a.property like '3%' and a.dirction=-1 and substring(a.vchdate,1,4) < '"+vyear+"' then OccurValue else 0 end) CreditTotalOcc4f,"
						+ " sum(case when a.property like '4%' and a.dirction=1 and substring(a.vchdate,1,4) < '"+vyear+"' then OccurValue else 0 end) DebitTotalOcc5f,"
						+ " sum(case when a.property like '4%' and a.dirction=-1 and substring(a.vchdate,1,4) < '"+vyear+"' then OccurValue else 0 end) CreditTotalOcc5f,"
						+ " sum(case when a.property like '5%' and a.dirction=1 and substring(a.vchdate,1,4) < '"+vyear+"' then OccurValue else 0 end) DebitTotalOcc6f,  "
						+ " sum(case when a.property like '5%' and a.dirction=-1 and substring(a.vchdate,1,4) < '"+vyear+"' then OccurValue else 0 end) CreditTotalOcc6f "

						+ " from z_subjectentryrectify a,c_accountall b "
						+ " where a.accpackageid='"+ AccPackageID+ "' and a.projectid='"+ projectID+ "' "
//						+ " and a.Currency <>'' "
						+ " and b.accpackageid='"+ AccPackageID
						+ "' and b.submonth=1 and b.accsign=1 "
						+ " and a.subjectid=b.subjectid "
//						+ " and a.Currency=b.dataName "
						+ " group by b.dataName,b.subjectid" 
						+ " ) a , (" 
						+ " select * from c_accountall "
						+ " where accpackageid='"+ AccPackageID+ "' and submonth=1 and accsign=1 " 
						+ " ) b "
						+ " where (a.subjectfullname1 like concat(b.subjectfullname1,'/%') or a.subjectfullname1=b.subjectfullname1) "
//						+ " and a.Currency=b.dataName "
						+ " group  by b.dataName,a.Currency,b.subjectid";
				}
				

//				//org.util.Debug.prtOut("createWbTzHzYear = |"+sql);
				
				st.addBatch(sql);
			}
			st.executeBatch();
			
			//修改dataname为空的值
			sql = "update z_accountallyearrectify a,(" +
			"	select distinct accpackageid,subjectid,dataname " +
			"	from c_accountall " +
			"	where 1=1 " +
			"	and accpackageid='"+ AccPackageID+ "' " +
			"	and submonth = 1 " +
			"	and accsign=1 " +
			") b " +
			"set a.dataname = b.dataname " +
			"where a.projectid='"+ projectID + "' " +
			"and b.accpackageid='"+ AccPackageID+ "' " +
			"and a.dataname = '' " +
			"and a.subjectid = b.subjectid";
			st.execute(sql);
			
			//org.util.Debug.prtOut("createWbTzHzYear  = |" + new ASFuntion().getCurrentTime());
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(st);
		}   
	}
	
	
	public ArrayList selectAssitem(String vStr, String departID)
			throws Exception {
		ArrayList al = new ArrayList();

		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		String str = "";
		String Astr = "";
		try {
			if (vStr != null && !"".equals(vStr)) {

				String sql = "SELECT entryId,assitemId from z_assitementryrectify where entryId in("
						+ " select autoid from `z_subjectentryrectify`  where TypeID='调' and VoucherID='"
						+ vStr + "') order by entryId";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while (rs.next()) {
					ArrayList aAl = new ArrayList();
					if (str.equals(rs.getString(1))) {
						Astr += "," + rs.getString(2);
					} else {
						if (!"".equals(Astr)) {
							aAl.add(str);
							aAl.add(Astr);
							al.add(aAl);
						}
						Astr = rs.getString(2);
					}
					str = rs.getString(1);
				}
				ArrayList aAll = new ArrayList();
				aAll.add(str);
				aAll.add(Astr);
				al.add(aAll);
			}
			return al;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	public ArrayList selectSubject(String vStr, String departID)
			throws Exception {
		ArrayList al = null;
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			if (vStr != null && !"".equals(vStr)) {

//				String sql = "select subjectid,summary,dirction,occurvalue,autoid,CurrValue,Currency,CurrRate,vchdate from z_subjectentryrectify where  TypeID='调' and VoucherID='"
//						+ vStr + "'";
				//添加科目名称
				String sql = "select a.subjectid,summary,dirction,occurvalue,autoid,CurrValue,Currency,CurrRate,vchdate,b.subjectname,itemtype,inventorytype,DebitExpressions,LenderExpressions " 
							+" from z_subjectentryrectify a "
							+" left join "
							+" ( "
							+" select accpackageid,subjectid,subjectname from c_accpkgsubject a "
							+" union "
							+" select  accpackageid,subjectid,subjectname from z_usesubject b where projectid = " + this.proId
							+" ) b "
							+" on a.subjectid = b.subjectid and b.accpackageid=a.accpackageid "
							+" where  TypeID='调' and VoucherID='"+ vStr + "' order by Serail";
					
				ps = conn.prepareStatement(sql);
				al = new ArrayList();
				rs = ps.executeQuery();
				al = new ArrayList();
				while (rs.next()) {
					ArrayList aSub = new ArrayList();
					aSub.add(rs.getString(1));
					aSub.add(rs.getString(2));
					aSub.add(rs.getString(3));
					aSub.add(rs.getString(4));
					aSub.add(rs.getString(5));
					aSub.add(rs.getString(6));
					aSub.add(rs.getString(7));
					aSub.add(rs.getString(8));
					aSub.add(rs.getString(9));
					aSub.add(rs.getString(10));
					aSub.add(rs.getString(11));
					aSub.add(rs.getString(12));
					aSub.add(rs.getString(13));
					aSub.add(rs.getString(14));
					al.add(aSub);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return al;
	}

	public void delVoucher(String vStr, String acc) throws Exception {
		DbUtil.checkConn(conn);
		System.out.println("vStr="+vStr+"  acc="+acc);
		PreparedStatement ps = null;
		try {
			if (vStr != null && !"".equals(vStr)) {

				ps = null;
				String sql = "";
//				sql = "delete a from c_assitementryacc a , (select b.* from  c_assitem a right join ("
//						+ " select * from z_assitementryrectify where entryId in("
//						+ " select autoid from z_subjectentryrectify  where TypeID='调' and VoucherID='"
//						+ vStr
//						+ "')"
//						+ " ) b on a.accid=b.subjectid and a.assitemid=b.assitemid where a.assitemid is null ) b where "
//						+ " a.accid=b.subjectid and a.assitemid=b.assitemid and a.accpackageid='"
//						+ acc + "' ";
//				System.out.println("sql1:"+sql);
//				ps = conn.prepareStatement(sql);
//				ps.execute();

				sql = "delete from z_assitementryrectify where entryId in("
						+ " select autoid from z_subjectentryrectify  where TypeID='调' and VoucherID='"
						+ vStr + "')";
				System.out.println("sql2:"+sql);
				ps = conn.prepareStatement(sql);
				ps.execute();
				DbUtil.close(ps);
				sql = "delete b from z_subjectentryrectify a,z_analsyerectify b where a.AutoId = b.AutoId and a.TypeID='调' and VoucherID='"
					+ vStr + "'";
				System.out.println("sql5:"+sql);
				ps = conn.prepareStatement(sql);
				ps.execute();
				DbUtil.close(ps);
				sql = "delete from z_subjectentryrectify where TypeID='调' and VoucherID='"
						+ vStr + "'";
				System.out.println("sql3:"+sql);
				ps = conn.prepareStatement(sql);
				ps.execute();
				DbUtil.close(ps);
				sql = "delete from z_voucherrectify WHERE AutoID='" + vStr
						+ "'";
				System.out.println("sql4:"+sql);
				ps = conn.prepareStatement(sql);
				ps.execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}

	}

	public void delVoucher(String vStr, String acc, String proid)
			throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		try {
			if (vStr != null && !"".equals(vStr)) {
				ps = null;
				String sql = "delete from z_usesubject where projectID='"
						+ proid
						+ "' and accpackageid='"
						+ acc
						+ "' and Subjectid in(select subjectid from z_subjectentryrectify  where TypeID='调' and VoucherID='"
						+ vStr + "')";
				ps = conn.prepareStatement(sql);
				ps.execute();
				sql = "delete from z_assitementryrectify where entryId in("
						+ " select autoid from z_subjectentryrectify  where TypeID='调' and VoucherID='"
						+ vStr + "')";
				ps = conn.prepareStatement(sql);
				ps.execute();
				sql = "delete from z_subjectentryrectify where TypeID='调' and VoucherID='"
						+ vStr + "'";
				ps = conn.prepareStatement(sql);
				ps.execute();
				sql = "delete from z_voucherrectify WHERE autoid='" + vStr
						+ "'";
				ps = conn.prepareStatement(sql);
				ps.execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}

	}

	public void AddOrModifyVoucher(VoucherTable vt, String act)
			throws Exception {

		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		int i = 1;
		// //org.util.Debug.prtOut("AddOrModifyVoucher111 "+vt.getAutoid());
		try {
			if (act.equals("ad")) {
				String str = "INSERT INTO z_voucherrectify(AutoId,AccPackageID,projectID,VoucherID,TypeID,VchDate,FillUser,AuditUser,KeepUser,Director,AffixCount,Description,DoubtUserId,Property) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				ps = conn.prepareStatement(str);
				ps.setInt(i++, vt.getAutoid());
				ps.setString(i++, vt.getAccpackageid());
				ps.setString(i++, vt.getProjectID());
				ps.setInt(i++, vt.getVoucherid());
				ps.setString(i++, vt.getTypeid());
				ps.setString(i++, vt.getVchdate());
				ps.setString(i++, vt.getFilluser());
				ps.setString(i++, vt.getAudituser());
				ps.setString(i++, vt.getKeepuser());
				ps.setString(i++, vt.getDirector());
				ps.setInt(i++, vt.getAffixcount());
				ps.setString(i++, vt.getDescription());
				ps.setString(i++, vt.getDoubtuserid());
				ps.setString(i++, vt.getProperty());

				ps.execute();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}

	public void AddOrModifySubjectEntry(SubjectEntryTable set, String act)
			throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;

		// //org.util.Debug.prtOut("AddOrModifyVoucher "+set.getSubjectid());
		try {
			if (act.equals("ad")) {
				String str = "INSERT INTO z_subjectentryrectify(Autoid,AccPackageID,projectID,VoucherID,TypeId,VchDate,Serail,Summary,SubjectID,Dirction,OccurValue,CurrRate,CurrValue,Currency,Quantity,UnitPrice,BankID,Property,itemtype,inventorytype,DebitExpressions,LenderExpressions) VALUES "
						+ " ('"
						+ set.getAutoid()
						+ "','"
						+ set.getAccpackageid()
						+ "','"
						+ set.getProjectID()
						+ "','"
						+ set.getVoucherid()
						+ "','"
						+ set.getTypeid()
						+ "','"
						+ set.getVchdate()
						+ "','"
						+ set.getSerail()
						+ "','"
						+ set.getSummary()
						+ "','"
						+ set.getSubjectid()
						+ "','"
						+ set.getDirction()
						+ "','"
						+ set.getOccurvalue()
						+ "','"
						+ set.getCurrrate()
						+ "','"
						+ set.getCurrvalue()
						+ "','"
						+ set.getCurrency()
						+ "','"
						+ set.getQuantity()
						+ "','"
						+ set.getUnitprice()
						+ "','"
						+ set.getBankid()
						+ "','"
						+ set.getProperty()
						+ "','"
						+ set.getItemtype()
						+ "','"
						+ set.getInventorytype()
						+ "','"
						+ set.getDebitExpressions()
						+ "','"
						+ set.getLenderExpressions()
						+ "')";
//				//org.util.Debug.prtOut(str);
				ps = conn.prepareStatement(str);
				
				ps.execute();

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}

	}

	public void AddRectify(RectifyTable rt, String act) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		int i = 1;
		// //org.util.Debug.prtOut("AddOrModifyVoucher "+set.getSubjectid());
		try {
			if (act.equals("ad")) {
				String str = "INSERT INTO z_assitementryrectify(AccPackageID,projectID,entryId,assitemId,subjectId) VALUES(?,?,?,?,?)";
				ps = conn.prepareStatement(str);
				ps.setString(i++, rt.getAccpackageId());
				ps.setString(i++, rt.getProjectID());
				ps.setString(i++, rt.getEntryId());
				ps.setString(i++, rt.getAssitemId());
				ps.setString(i++, rt.getSubjectId());
				ps.execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}

	/**
	 * add by king 2006-06-14
	 */
	public String takeOutVoucher(String AutoID, String departID)
			throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			if (AutoID == null || "".equals(AutoID)) {
				return "false";
			}

			ps = conn
					.prepareStatement("update z_voucherrectify set Property=concat(substring(Property,1,2),2) where autoid='"
							+ AutoID + "'");
			ps.executeUpdate();
			ps = conn
					.prepareStatement("select VoucherID,AccPackageID from z_voucherrectify where autoid='"
							+ AutoID + "'");
			rs = ps.executeQuery();
			if (rs.next()) {
				String VoucherID = rs.getString(1);
				String AccPackageID = rs.getString(2);
				ps = conn
						.prepareStatement("update z_subjectentryrectify set Property=concat(substring(Property,1,2),2) where Serail='"
								+ VoucherID
								+ "' and AccPackageID='"
								+ AccPackageID + "'");
				ps.executeUpdate();

			}

			return "ok";
		} catch (Exception e) {
			e.printStackTrace();
			return "false";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}
	


public String getDown(String acc, String subjectID, String opt)
			throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection con=null;
		StringBuffer sbf = new StringBuffer("<select name=\"Currency" + opt
				+ "\" ><option value=\"00\">&nbsp;</option>");
		try {
			//org.util.Debug.prtOut(acc + " : " + subjectID + " : " + opt);

			String sql = "select * from c_accountall  where accpackageid='"
					+ acc
					+ "' and submonth=1 and isleaf1=1 and AccSign=1 and subjectid='"
					+ subjectID + "'";
			//System.out.println("sqltest:"+sql);
			
		    con= new DBConnect().getConnect(acc.substring(0,6));
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			int num = 0;
			while (rs.next()) {
				num++;
				sbf.append("<option value=\"" + rs.getString("DataName")
						+ "\">" + rs.getString("DataName") + "</option>");
			}
			sbf.append("</select>");
			if (num == 0) {
				return "*";
			}
			return sbf.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(con);
		}

	}

	public String getSCurrency(String acc, String subjectID) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		String result = "";
		try {
			String sql = "select * from c_accountall where accpackageid='"
					+ acc + "' and submonth=1 and subjectid='" + subjectID
					+ "'";

			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				result = "科目［"+subjectID+"］存在外币，请在修改页面中补录［原币金额］和［币种］！\\n";
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
	 * 负值重分类
	 */
	
	/**
	 * 得到负值重分类的核算范围
	 */
	public String getClassAssitem(String acc)  throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			StringBuffer sb = new StringBuffer();
			String string = "";
			String sql = "select  distinct concat(a.accid,'`',a.assitemid) as assitem " +
			"	from c_assitementryacc a,c_subjectassitem b,c_accpkgsubject c " +
			"	where a.accpackageid=?  " +
			"	and b.accpackageid=?  " +
			"	and c.accpackageid=?  " +
			"	and a.submonth = 1 " +
			"	and a.isleaf1 = 1 " +
			"	and b.ifequal = 0 " +
			"	and c.AssistCode = 1 " +
			"	and a.accid = c.subjectid " + 
			"	and a.accid = b.subjectid  " +
			"	and (a.AssTotalName1 = b.AssTotalName1 or a.AssTotalName1 like concat(b.AssTotalName1,'/%')) " +
			"	order by a.accid,a.assitemid ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, acc);
			ps.setString(2, acc);
			ps.setString(3, acc);
			rs = ps.executeQuery();
			while(rs.next()){
				sb.append("'" + rs.getString(1) + "',");
			}
			string = sb.toString();			
			return  "".equals(string) ? "" :string.substring(0,string.length()-1);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	
	/**
	 * 得到负值重分类的科目范围
	 * @param acc	账套ID
	 * @param projectID 项目ID
	 * @param key	科目范围
	 * @return
	 * @throws Exception
	 */
	public String getClassSubject(String acc,String projectID,String [] key) throws Exception{
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;		
		try {
			StringBuffer sb = new StringBuffer();
			String string = "";
			for (int i = 0; i < key.length; i++) {
				string += "'" + key[i] + "',";
			}
			string = string.substring(0,string.length()-1);
			
//			String sql = "select a.subjectid from c_account a left join (select * from asdb.k_standsubject where vocationid = (select vocationid from asdb.k_customer where departid = (select customerid from asdb.z_project where projectid='"+
//				projectID +"' )) and subjectName in(" + string + ")) b on a.subjectfullname2 like concat(b.subjectName,'%') where SubMonth=1 and b.subjectName is not null and isleaf1=1 and accpackageid='" + acc + "' order by a.subjectid";
			
			String sql = "select a.subjectid from c_accpkgsubject a where a.AssistCode=1 and a.accpackageid='"+acc+"' and isleaf=1 order by a.subjectid";
			
			
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				sb.append("'" + rs.getString(1) + "',");
			}
			string = sb.toString();			
			return "".equals(string) ? "" :string.substring(0,string.length()-1);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 得到核算中的科目
	 * @param acc
	 * @return
	 * @throws Exception
	 */
	public String getNotClassSubject(String acc) throws Exception{
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			StringBuffer sb = new StringBuffer();
			String string = "";
//			String sql = "select distinct a.accid from (select distinct accid,assitemid from c_assitementryacc where accpackageid='"+acc+"' and SubMonth=1) a left join c_assitem b on b.accpackageid='"+acc+"' and a.accid=b.accid and a.assitemid=b.assitemid where b.accpackageid is not null order by a.accid";
			
			String sql = " select distinct a.accid " +
			" from ( " +
			" 	select distinct accid,assitemid ,asstotalname1 " +
			" 	from c_assitementryacc " +
			" 	where accpackageid='"+acc+"' and SubMonth=1 " +
			" ) a  " +
			" left join c_assitem b " + 
			" on b.accpackageid='"+acc+"' " + 
			" and a.accid=b.accid  " +
			" and a.assitemid=b.assitemid " + 

			" left join c_subjectassitem c " +
			" on c.accpackageid='"+acc+"'  " +
			" and c.ifequal = 1 " +
			" and c.subjectid = a.accid " +
              
			" where b.accpackageid is not null " + 
			" and c.accpackageid is  null  " +
			" order by a.accid";
			
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				sb.append("'" + rs.getString(1) + "',");
			}
			string = sb.toString();			
			return  "".equals(string) ? "" :string.substring(0,string.length()-1);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 得到挂帐多科目
	 * @param acc
	 * @param opt
	 * @return
	 * @throws Exception
	 */
	public String getUserSubject(String acc,int opt) throws Exception{
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			StringBuffer sb = new StringBuffer();
			String string = "";
			String sql = "";
			if(opt == 0 ){
				sql = "select distinct oriaccid,orisubjectid from c_usersubject where oriDataName=0 and accpackageid='"+acc+"' and oriaccid=''";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){					
					sb.append("'" + rs.getString(2) + "',");
				}
			}else{
				sql = "select distinct oriaccid,orisubjectid from c_usersubject where oriDataName=0 and accpackageid='"+acc+"' and oriaccid>''";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){					
					sb.append("'" + rs.getString(1) + "`" + rs.getString(2) + "',");
				}
			}
			
			string = sb.toString();			
			return  "".equals(string) ? "" :string.substring(0,string.length()-1);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	
	public String getAllSubjectID(String acc,String projectID,String SubjectID,String opt)throws Exception{
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			if (!"".equals(SubjectID)) {
				StringBuffer sb = new StringBuffer("");
				
				String str = "";
				if("0".equals(opt)){
					str = " and a.level0=b.level0 and a.SubjectFullName like CONCAT(b.SubjectFullName,'%') " ;
				}else if("1".equals(opt)){
					str = " and a.level0=b.level0+1 and a.SubjectFullName like CONCAT(b.SubjectName,'%') ";
				}
			
				String sql = "select a.* from (select subjectid,subjectname,ParentSubjectId,level0,SubjectFullName from c_accpkgsubject where accpackageid='"+acc+"' union  select subjectid,subjectname,ParentSubjectId,level0,SubjectFullName from z_usesubject where accpackageid='"+acc+"' and projectID='"+projectID+"' ) a,(select level0,ParentSubjectId,SubjectFullName SubjectName,substring(SubjectFullName,1,INSTR(SubjectFullName,concat('/',SubjectName))-1) SubjectFullName from c_accpkgsubject where accpackageid='"+acc+"' and subjectid = '"+SubjectID+"' union select level0,ParentSubjectId,SubjectFullName SubjectName,substring(SubjectFullName,1,INSTR(SubjectFullName,concat('/',SubjectName))-1) SubjectFullName from z_usesubject where accpackageid='"+acc+"' and projectID='"+projectID+"' and subjectid = '"+SubjectID+"' ) b where 1=1 "+str+" order by length(a.subjectid), a.subjectid";
				
				//org.util.Debug.prtOut("负值重分类1:="+sql);
				ps = conn.prepareStatement(sql);
				
				rs = ps.executeQuery();
				while(rs.next()){
					sb.append(rs.getString("subjectid") + "`");
					sb.append(rs.getString("subjectname") + "`");
					sb.append(rs.getString("ParentSubjectId") + "`");
					sb.append(rs.getString("level0") + "|");
				}
				
				if("".equals(sb.toString())){
					sql = "select * from (select subjectid,subjectname,ParentSubjectId,level0 from c_accpkgsubject where accpackageid='"+acc+"' union  select subjectid,subjectname,ParentSubjectId,level0 from z_usesubject where accpackageid='"+acc+"' and projectID='"+projectID+"') a where  subjectid = '"+SubjectID+"'";
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					if(rs.next()){
						sb.append(rs.getString("subjectid") + "`");
						sb.append(rs.getString("subjectname") + "`");
						sb.append(rs.getString("subjectid") + "`");
						sb.append(rs.getString("level0") + "|");
					}
					//org.util.Debug.prtOut("负值重分类2:="+sql);
				}
			
				return sb.toString();
			}
			return "";
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public String getHaveAssitem(String acc,String SubjectID) throws Exception{
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select 1 from c_accpkgsubject where accpackageid='"+acc+"' and subjectid='"+SubjectID+"' and isleaf=1  limit 1";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				sql = "select 1 from c_assitementryacc where accid='"+SubjectID+"' and accpackageid='"+acc+"' limit 1";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				if(rs.next()){
					return "have";
				}else{
					return "none";
				}
			}else{
				return "have";
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
	 * 判断分组：1、同一组；2、都不在组；3、调整在组，对方不在组；4、调整不在组，对方在组；5、在不同组
	 * @param acc
	 * @param SubjectID
	 * @param AssitemID
	 * @param OrderID
	 * @return
	 * @throws Exception
	 */
	public int isSameOne(String acc,String SubjectID,String AssitemID,String OrderID) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "";
			String opt = "";
			String s1 = SubjectID;
			String s2 = AssitemID;
			if("".equals(AssitemID)){
				s1 = "";
				s2 = SubjectID;
			}

			opt = " and ( oriaccid = '"+OrderID+"'  or orisubjectid = '"+OrderID+"' )";
			
			sql = "select * from c_usersubject where oriDataName=0 and AccPackageID='"+acc+"' and concat(oriaccid ,'`',orisubjectid) = concat('"+s1+"','`','"+s2+"') ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){										
				sql = "select * from c_usersubject where oriDataName=0 and AccPackageID='"+acc+"' and subjectid='"+rs.getString("subjectid")+"' and accid='"+rs.getString("accid")+"' " + opt;
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				if(rs.next()){		
					return 1;	//同一组
				}else{
					sql = "select * from c_usersubject where oriDataName=0 and AccPackageID='"+acc+"' " + opt;
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					if(rs.next()){
						
						if("".equals(AssitemID)){
							return 5; //在不同组
						}else{
							if( AssitemID.equals(rs.getString("orisubjectid"))){
								return 5; //在不同组
							}else{
								return 3; //调整在组，对方不在组
							}
						}
					}else{
						return 3; //调整在组，对方不在组
					}
				}
			}else{
				sql = "select * from c_usersubject where oriDataName=0 and AccPackageID='"+acc+"' " + opt;
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				if(rs.next()){
					
					if("".equals(AssitemID)){
						return 4; //调整不在组，对方在组
					}else{
						if( AssitemID.equals(rs.getString("orisubjectid"))){
							return 4; //调整不在组，对方在组
						}else{
							return 2; //都不在组
						}
					}
					
				}else{
					return 2; //都不在组
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
	
	public String  saveClass(String acc,String projectID,String FillUser,ClassifiCation classifiCation) throws Exception{
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		try {
			String sql = "";
			
			boolean bool = false;
			
			String SDate = "";
			if("0".equals(classifiCation.getVdate())){
				SDate = (Integer.parseInt(acc.substring(6)) - 1)+"-12-01";
			}else{
				SDate = acc.substring(6)+"-12-31";
			}
			
			String [] sqlSubject = classifiCation.getStrSubject().split("\\|");
			String [] sqlAssitem = classifiCation.getStrAssitem().split("\\|");
			String [] sqlDirection = classifiCation.getStrDirection().split("\\|");
			String [] sqlBalance = classifiCation.getStrBalance().split("\\|");
			String [] sqlText = classifiCation.getStrText().split("\\|");
			
			String [] sqlName = classifiCation.getStrName().split("\\|");
			
			String [] sqlNew = classifiCation.getStrNew().split("\\|");
			
			VoucherTable vt = new VoucherTable();
			SubjectEntryTable set = new SubjectEntryTable();
			RectifyTable rt = new RectifyTable();
			
			
			
//			if(!"||".equals(classifiCation.getStrNew())){
//				insertData(acc,projectID,classifiCation.getStrNew(),classifiCation.getStrSubject(),classifiCation.getStrText(),classifiCation.getStrName()); 
//			}
			
			vt.setAccpackageid(acc);
			vt.setFilluser(FillUser);
			vt.setProperty(classifiCation.getProperty());
			vt.setProjectID(projectID);//
			vt.setVchdate(SDate);
			vt.setTypeid("调");
			vt.setAudituser("");
		    vt.setKeepuser("");
		    vt.setDirector("");
		    vt.setAffixcount(1);
		    vt.setDoubtuserid("");

			set.setAccpackageid(acc);
			set.setVchdate(SDate);
			set.setProperty(classifiCation.getProperty());
			set.setProjectID(projectID);//
			set.setTypeid("调");
			set.setCurrrate(0);
		    set.setCurrvalue(0);
		    set.setCurrency("");
		    set.setQuantity(0);
		    set.setUnitprice(0);
		    set.setBankid("");

			rt.setAccpackageId(acc);
			rt.setProjectID(projectID);//
			
			System.out.println("长度:"+sqlSubject.length);
			
			String result = "";
			for(int i=1;i<sqlSubject.length;i++){
				String VKeyId = new DELAutocode().getAutoCode("AUVO","");
				vt.setAutoid(Integer.parseInt(VKeyId));
				
				String vid = new DELAutocode().getAutoCode("VOID","");
				vt.setVoucherid(Integer.parseInt(vid));
				vt.setDescription(""); //备注
				
				AddOrModifyVoucher(vt,"ad"); //增加调整表
				
				//增加分录表
				String SUId = "";
				for(int j=0; j<2; j++){
					SUId = new DELAutocode().getAutoCode("SUAU","");
					set.setAutoid(Integer.parseInt(SUId));
					set.setVoucherid(Integer.parseInt(VKeyId));
					set.setSerail(j+1);
					set.setOccurvalue(Math.abs(Double.parseDouble(sqlBalance[i])));
					if(j==0){	//调整科目分录
						String str = getSCurrency(acc,sqlSubject[i]);
						if(result.indexOf(str) == -1){
							result += str;
						}
						
						set.setSubjectid(sqlSubject[i]);
						set.setSummary("负值重分类:"+sqlSubject[i]+","+sqlAssitem[i]+","+sqlName[i]);//摘要
						if("1".equals(sqlDirection[i])){
							set.setDirction(1);					
						}else{
							set.setDirction(-1);
						}
						if(!"".equals(sqlAssitem[i].trim())){
							rt.setEntryId(SUId);	
							rt.setAssitemId(sqlAssitem[i]);
							rt.setSubjectId(sqlSubject[i]);
//							AddRectify(rt,"ad");	
							bool = true;
						}
						
					}else{		//对方科目分录
						
						if("1".equals(sqlDirection[i])){
							set.setDirction(-1);					
						}else{
							set.setDirction(1);
						}
						
						/**
						 * 
						 */
						
						int opt = isSameOne(acc,sqlSubject[i],sqlAssitem[i].trim(),sqlText[i]);
						switch(opt){
						case 1 :	//同一组
							
							String str = getSCurrency(acc,sqlText[i]);
							if(result.indexOf(str) == -1){
								result += str;
							}
							
							set.setSubjectid(sqlText[i]);
							set.setSummary("负值重分类:"+sqlText[i]+","+sqlAssitem[i]+","+sqlName[i]);//摘要							
							
							if(!"".equals(sqlAssitem[i].trim())){
								sql = "select distinct oriaccid,orisubjectid,orisubjectname from c_usersubject where oriDataName=0 and AccPackageID='"+acc+"' and ( oriaccid = '"+sqlText[i]+"'  or orisubjectid = '"+sqlText[i]+"' ) and concat(accid ,'`',subjectid) = (select distinct concat(accid ,'`',subjectid) from c_usersubject  where oriDataName=0 and AccPackageID='"+acc+"' and oriaccid='"+sqlSubject[i]+"' and orisubjectid ='"+sqlAssitem[i]+"') ";
								ps = conn.prepareStatement(sql);
								rs = ps.executeQuery();
								while(rs.next()){	
									String s1 = rs.getString("oriaccid");
									String s2 = rs.getString("orisubjectid");
									if(!"".equals(s1)){		//A1有核算B2，就调到B2
										rt.setEntryId(SUId);
										rt.setSubjectId(sqlText[i]);
										if(sqlAssitem[i].trim().equals(s2)){
											rt.setAssitemId(sqlAssitem[i]);
											bool = true;											
											break;
										}else{		//A1在组内有核算，但无B2
											rt.setAssitemId(s2);
											
											set.setSummary("负值重分类:"+sqlText[i]+","+s2+","+rs.getString("orisubjectname"));//摘要		
											bool = true;		
										}
									}else{
										set.setSummary("负值重分类:"+sqlText[i]+","+sqlName[i]);//摘要
									}									
								}
								
								DbUtil.close(rs);
								DbUtil.close(ps);
							}else{
								sql = "select distinct oriaccid,orisubjectid,orisubjectname from c_usersubject where oriDataName=0 and AccPackageID='"+acc+"' and ( oriaccid = '"+sqlText[i]+"'  or orisubjectid = '"+sqlText[i]+"' ) and concat(accid ,'`',subjectid) = (select distinct concat(accid ,'`',subjectid) from c_usersubject  where oriDataName=0 and AccPackageID='"+acc+"' and oriaccid='' and orisubjectid ='"+sqlSubject[i]+"') ";
								ps = conn.prepareStatement(sql);
								rs = ps.executeQuery();
								while(rs.next()){	//A1有核算，随便调到一个组内A1的核算上；
									String s1 = rs.getString("oriaccid");
									String s2 = rs.getString("orisubjectid");
									if(!"".equals(s1)){	
										rt.setEntryId(SUId);
										rt.setSubjectId(sqlText[i]);
										rt.setAssitemId(s2);
										
										set.setSummary("负值重分类:"+sqlText[i]+","+s2+","+rs.getString("orisubjectname"));//摘要
										
										bool = true;
										break;
									}
								}
								
								DbUtil.close(rs);
								DbUtil.close(ps);
							}
							break;
							
						case 2 :	//都不在组
						case 3 :	//调整在组，对方不在组	
							
							if(!"".equals(sqlAssitem[i].trim())){
								//新增核算时，只新增同核算类型的核算；不同核算类型的核算以新增科目增加
								sql = "select accid,assitemid from c_assitementryacc where accpackageid='"+acc+"' and accid='"+sqlText[i]+"'  ";
								ps = conn.prepareStatement(sql);
								rs = ps.executeQuery();
								int ii =0;
								int jj = 0;
								while(rs.next()){
									set.setSubjectid(sqlText[i]);
									set.setSummary("负值重分类:"+sqlText[i]+","+sqlAssitem[i]+","+sqlName[i]);	
									ii++;
									
									String s1 = rs.getString("accid");
									String s2 = rs.getString("assitemid");
									if(sqlAssitem[i].trim().equals(s2)){	//A1有核算B2，就调到B2；

										rt.setEntryId(SUId);
										rt.setSubjectId(sqlText[i]);
										rt.setAssitemId(sqlAssitem[i]);
										bool = true;
										jj = 0;
										break;
									}else{	//A1有核算但无B2，就新建核算B2；
										jj = 1;
										rt.setEntryId(SUId);
										rt.setSubjectId(sqlText[i]);
										rt.setAssitemId(sqlAssitem[i]);
										bool = true;
									}
								}
								
								DbUtil.close(rs);
								DbUtil.close(ps);
								
								if(jj == 1){ //新建核算B2；
									insertAssitem(acc, projectID, sqlName[i], rt.getSubjectId(), rt.getAssitemId());
								}
								if(ii == 0){	//A1无核算，就新建下级子科目 B2；
									String newSubjectID= "";
									if("0".equals(sqlNew[i])){
										sql = "select * from (select subjectid,case when ParentSubjectId='' then subjectid else ParentSubjectId end ParentSubjectId from c_accpkgsubject where accpackageid='"+acc+"' union select subjectid,case when ParentSubjectId='' then subjectid else ParentSubjectId end ParentSubjectId from z_usesubject where projectid='"+projectID+"' )a where  subjectid='"+sqlText[i]+"' ";
										ps = conn.prepareStatement(sql);
										rs = ps.executeQuery();
										if(rs.next()){
											newSubjectID = insertData(acc,projectID,sqlName[i],rs.getString("ParentSubjectId"));
										}
										
										DbUtil.close(rs);
										DbUtil.close(ps);
										
									}else{
										newSubjectID = insertData(acc,projectID,sqlName[i],sqlText[i]);
									}
									
									set.setSubjectid(newSubjectID);
									set.setSummary("负值重分类:"+newSubjectID+","+sqlName[i]);	
								}
								
							}else{
								
								sql = "select 1 from c_assitementryacc where accpackageid='"+acc+"' and accid='"+sqlText[i]+"'  limit 1";
								ps = conn.prepareStatement(sql);
								rs = ps.executeQuery();
								if(rs.next()){
									sqlNew[i] = "0";	//如果本科目有核算，就修改调整为同级对照
								}
								
								
								if("0".equals(sqlNew[i])){		//同级调整就新增科目
									
									String newSubjectID= "";
									
									sql = "select * from (select subjectid,ParentSubjectId,subjectname from c_accpkgsubject where accpackageid='"+acc+"' union select subjectid,ParentSubjectId,subjectname from z_usesubject where projectid='"+projectID+"' )a where  subjectid='"+sqlText[i]+"' ";
									ps = conn.prepareStatement(sql);
									rs = ps.executeQuery();
									if(rs.next()){
										if(sqlName[i].equals(rs.getString("subjectname"))){
											newSubjectID = sqlText[i];
										}else{
											if("0".equals(sqlNew[i])){
												newSubjectID = insertData(acc,projectID,sqlName[i],rs.getString("ParentSubjectId"));
											}else{
												newSubjectID = insertData(acc,projectID,sqlName[i],sqlText[i]);
											}	
										}
										
									}
									
									set.setSubjectid(newSubjectID);
									set.setSummary("负值重分类:"+newSubjectID+","+sqlName[i]);	
									
								}else{
//									新增核算时，只新增同核算类型的核算；不同核算类型的核算以新增科目增加
//									sql = "select 1 from c_assitementryacc where accpackageid='"+acc+"' and accid='"+sqlText[i]+"'  limit 1";
									if("".equals(sqlAssitem[i].trim())){
										sql = "select 1 from c_assitementryacc where accpackageid='"+acc+"' and accid='"+sqlText[i]+"'  limit 1";
									}else{
										sql = "select 1 from c_assitementryacc where accpackageid='"+acc+"' and accid='"+sqlText[i]+"' and '"+sqlAssitem[i].trim()+"' like concat(assitemid,'%')  limit 1";									
									}
									
									ps = conn.prepareStatement(sql);
									rs = ps.executeQuery();
									if(rs.next()){	//A1有核算就新建下；
										
										DbUtil.close(rs);
										DbUtil.close(ps);
										
										String newSubjectID = insertData(acc,projectID,sqlName[i],sqlText[i]);
										set.setSubjectid(newSubjectID);
										set.setSummary("负值重分类:"+newSubjectID+","+sqlName[i]);	

									}else{	//A1无核算就新建下级或者同级科目A2；
										
										DbUtil.close(rs);
										DbUtil.close(ps);
										
										String newSubjectID= "";
										
										sql = "select * from (select subjectid,ParentSubjectId,subjectname from c_accpkgsubject where accpackageid='"+acc+"' union select subjectid,ParentSubjectId,subjectname from z_usesubject where projectid='"+projectID+"' )a where  subjectid='"+sqlText[i]+"' ";
										ps = conn.prepareStatement(sql);
										rs = ps.executeQuery();
										if(rs.next()){
											if(sqlName[i].equals(rs.getString("subjectname"))){
												newSubjectID = sqlText[i];
											}else{
												if("0".equals(sqlNew[i])){
													newSubjectID = insertData(acc,projectID,sqlName[i],rs.getString("ParentSubjectId"));
												}else{
													newSubjectID = insertData(acc,projectID,sqlName[i],sqlText[i]);
												}	
											}
											
										}
										
										set.setSubjectid(newSubjectID);
										set.setSummary("负值重分类:"+newSubjectID+","+sqlName[i]);								
									}
								}
								
							}
							
							break;
							
						case 4 :	//调整不在组，对方在组
							
							if(!"".equals(sqlAssitem[i].trim())){
								set.setSubjectid(sqlText[i]);
								set.setSummary("负值重分类:"+sqlText[i]+","+sqlAssitem[i]+","+sqlName[i]);
								
								sql = "select distinct oriaccid,orisubjectid from c_usersubject where oriDataName=0 and AccPackageID='"+acc+"' and ( oriaccid = '"+sqlText[i]+"'  or orisubjectid = '"+sqlText[i]+"' ) ";
								ps = conn.prepareStatement(sql);
								rs = ps.executeQuery();
								int jj = 0;
								
								while(rs.next()){
									String s1 = rs.getString("oriaccid");
									String s2 = rs.getString("orisubjectid");
									if(!"".equals(s1)){
										rt.setEntryId(SUId);
										rt.setSubjectId(sqlText[i]);
										rt.setAssitemId(sqlAssitem[i]);
										if(sqlAssitem[i].trim().equals(s2)){		//A1有核算B2，直接调到B2上；									
											bool = true;
											jj =0;
											break;
										}else{	//A1有核算但无B2，就新建核算B2
											bool = true;
											jj =1;
										}										
									}else{	//A1无核算，就新建同级B2
										String newSubjectID= "";
										sql = "select * from (select subjectid,ParentSubjectId from c_accpkgsubject where accpackageid='"+acc+"' union select subjectid,ParentSubjectId from z_usesubject where projectid='"+projectID+"' )a where  subjectid='"+sqlText[i]+"' ";
										ps = conn.prepareStatement(sql);
										rs = ps.executeQuery();
										if(rs.next()){
											newSubjectID = insertData(acc,projectID,sqlName[i],rs.getString("ParentSubjectId"));
										}
										set.setSubjectid(newSubjectID);
										set.setSummary("负值重分类:"+newSubjectID+","+sqlName[i]);								
									}
									
								}
								
								DbUtil.close(rs);
								DbUtil.close(ps);
								
								if(jj==1){
									insertAssitem(acc, projectID, sqlName[i], rt.getSubjectId(), rt.getAssitemId());
								}
								
							}else{
								String newSubjectID = "";
//								新增核算时，只新增同核算类型的核算；不同核算类型的核算以新增科目增加
//								sql = "select 1 from c_assitementryacc where accpackageid='"+acc+"' and accid='"+sqlText[i]+"'  limit 1";
								if("".equals(sqlAssitem[i].trim())){
									sql = "select 1 from c_assitementryacc where accpackageid='"+acc+"' and accid='"+sqlText[i]+"'  limit 1";
								}else{
									sql = "select 1 from c_assitementryacc where accpackageid='"+acc+"' and accid='"+sqlText[i]+"' and '"+sqlAssitem[i].trim()+"' like concat(assitemid,'%')  limit 1";									
								}
								ps = conn.prepareStatement(sql);
								rs = ps.executeQuery();
								if(rs.next()){	//A1有核算就新建下；
									
									DbUtil.close(rs);
									DbUtil.close(ps);
									
									newSubjectID = insertData(acc,projectID,sqlName[i],sqlText[i]);									
								}else{ //A1无核算就新建同级科目A2
									
									DbUtil.close(rs);
									DbUtil.close(ps);
									
									sql = "select * from (select subjectid,ParentSubjectId from c_accpkgsubject where accpackageid='"+acc+"' union select subjectid,ParentSubjectId from z_usesubject where projectid='"+projectID+"' )a where  subjectid='"+sqlText[i]+"' ";
									ps = conn.prepareStatement(sql);
									rs = ps.executeQuery();
									if(rs.next()){
										newSubjectID = insertData(acc,projectID,sqlName[i],rs.getString("ParentSubjectId"));
									}
								}
								set.setSubjectid(newSubjectID);
								set.setSummary("负值重分类:"+newSubjectID+","+sqlName[i]);	
							}
							break;
							
						case 5 :	//在不同组
							
							if(!"".equals(sqlAssitem[i].trim())){
								/**
								 * 新增核算时，只新增同核算类型的核算；不同核算类型的核算以新增科目增加
								 * 没有例子：暂时不改
								 */
								sql = "select distinct oriaccid,orisubjectid from c_usersubject where oriDataName=0 and AccPackageID='"+acc+"' and ( oriaccid = '"+sqlText[i]+"'  or orisubjectid = '"+sqlText[i]+"' ) and concat(accid ,'`',subjectid) = (select distinct concat(accid ,'`',subjectid) from c_usersubject  where oriDataName=0 and AccPackageID='"+acc+"' and oriaccid='"+sqlSubject[i]+"' and orisubjectid ='"+sqlAssitem[i].trim()+"') ";
								ps = conn.prepareStatement(sql);
								rs = ps.executeQuery();
								int jj =0;
								int ii =0;
								while(rs.next()){
									ii ++ ;
									String s1 = rs.getString("oriaccid");
									String s2 = rs.getString("orisubjectid");
									if(!"".equals(s1)){
										rt.setEntryId(SUId);
										rt.setSubjectId(sqlText[i]);
										rt.setAssitemId(sqlAssitem[i]);									
										if(sqlAssitem[i].trim().equals(s2)){												
											set.setSubjectid(sqlText[i]);
											set.setSummary("负值重分类:"+sqlText[i]+","+sqlAssitem[i]+","+sqlName[i]);	
											bool = true;
											jj = 0;
											break;
										}else{
											jj = 1;
										}
											
									}else{
										
									}
								}
								
								DbUtil.close(rs);
								DbUtil.close(ps);
								
								if(jj == 1){
									insertAssitem(acc, projectID, sqlName[i], rt.getSubjectId(), rt.getAssitemId());
								}
								set.setSubjectid(sqlText[i]);
								set.setSummary("负值重分类:"+sqlText[i]+","+sqlAssitem[i]+","+sqlName[i]);	
								if(ii==0){
									String newSubjectID = "";
									sql = "select * from (select subjectid,ParentSubjectId from c_accpkgsubject where accpackageid='"+acc+"' union select subjectid,ParentSubjectId from z_usesubject where projectid='"+projectID+"' )a where  subjectid='"+sqlText[i]+"' ";
									ps = conn.prepareStatement(sql);
									rs = ps.executeQuery();
									if(rs.next()){
										newSubjectID = insertData(acc,projectID,sqlName[i],rs.getString("ParentSubjectId"));
									}
									
									DbUtil.close(rs);
									DbUtil.close(ps);
									
									set.setSubjectid(newSubjectID);
									set.setSummary("负值重分类:"+newSubjectID+","+sqlName[i]);	
								}
								
							}else{
								sql = "select 1 from c_usersubject where oriDataName=0 and AccPackageID='"+acc+"' and ( oriaccid = '"+sqlText[i]+"' ) and concat(accid ,'`',subjectid) = (select concat(accid ,'`',subjectid) from c_usersubject  where oriDataName=0 and AccPackageID='"+acc+"' and oriaccid='"+sqlSubject[i]+"' and orisubjectid ='"+sqlAssitem[i]+"') limt 1";
								ps = conn.prepareStatement(sql);
								rs = ps.executeQuery();
								String newSubjectID = "";
								if(rs.next()){
									
									DbUtil.close(rs);
									DbUtil.close(ps);
									
									newSubjectID = insertData(acc,projectID,sqlName[i],sqlText[i]);
								}else{
									
									DbUtil.close(rs);
									DbUtil.close(ps);
									
									sql = "select * from (select subjectid,ParentSubjectId from c_accpkgsubject where accpackageid='"+acc+"' union select subjectid,ParentSubjectId from z_usesubject where projectid='"+projectID+"' )a where  subjectid='"+sqlText[i]+"' ";
									ps = conn.prepareStatement(sql);
									rs = ps.executeQuery();
									if(rs.next()){
										newSubjectID = insertData(acc,projectID,sqlName[i],rs.getString("ParentSubjectId"));
									}
								}
								set.setSubjectid(newSubjectID);
								set.setSummary("负值重分类:"+newSubjectID+","+sqlName[i]);	
							}
							
							break;
						}						
					}	//exit if
					
					//插入外币
					ASFuntion CHF = new ASFuntion();
					int iCount = 0;
					NumberFormat formatter = new DecimalFormat("######0.00");
					Project project = new ProjectService(conn).getProjectById(projectID);
					String begin = "".equals(project.getAuditTimeBegin()) ? "0" : project.getAuditTimeBegin().substring(0,4);
					String end = "".equals(project.getAuditTimeEnd()) ? "0" : project.getAuditTimeEnd().substring(0,4);
					String bMonth = "".equals(project.getAuditTimeBegin()) ? "0" : project.getAuditTimeBegin().substring(5,7);
					String eMonth = "".equals(project.getAuditTimeEnd()) ? "0" : project.getAuditTimeEnd().substring(5,7);
					int projectbegin = Integer.parseInt(begin)*12+Integer.parseInt(bMonth);
					int projectend = Integer.parseInt(end)*12+Integer.parseInt(eMonth);
					
					int ii = 1;
					if(rt.getAssitemId() == null || "".equals(rt.getAssitemId().trim())){
						sql = "select 1 from c_account where subjectid=? and SubYearMonth*12+SubMonth = ? and abs(Balance) = ? ";
						ps = conn.prepareStatement(sql);
					}else{
						sql = "select 1 from c_assitementryacc where AssItemID = ? and accid=? and SubYearMonth*12+SubMonth = ? and abs(Balance) = ? ";
						ps = conn.prepareStatement(sql);
						ps.setString(ii++, rt.getAssitemId());
					}
					ps.setString(ii++, set.getSubjectid());
					ps.setInt(ii++, projectend);
					ps.setDouble(ii++, set.getOccurvalue());
					rs = ps.executeQuery();
					if(rs.next()){
						DbUtil.close(rs);
						DbUtil.close(ps);
						
						ii = 1;
						if(rt.getAssitemId() == null || "".equals(rt.getAssitemId().trim())){
							sql = "select *,direction2*Balance as bal from c_accountall where subjectid=? and SubYearMonth*12+SubMonth = ?  ";
							ps = conn.prepareStatement(sql);
						}else{
							sql = "select *,direction2*Balance as bal from c_assitementryaccall where AssItemID = ? and accid=? and SubYearMonth*12+SubMonth = ?  ";
							ps = conn.prepareStatement(sql);
							ps.setString(ii++, rt.getAssitemId());
						}
						ps.setString(ii++, set.getSubjectid());
						ps.setInt(ii++, projectend);
						rs = ps.executeQuery();
						boolean bool1 = true;
						ii = iCount + 1;
						while(rs.next()){
							bool1 = false;
							set.setCurrency(rs.getString("DataName"));
							double Balance = rs.getDouble("bal");
							if(Balance < 0 ){
								set.setCurrvalue(Math.abs(Balance));
								if(ii == iCount + 1){
									set.setCurrrate(Double.parseDouble(formatter.format(set.getOccurvalue()/set.getCurrvalue())));
									
								}else{
									SUId = new DELAutocode().getAutoCode("SUAU","");
									set.setAutoid(Integer.parseInt(SUId));
									set.setOccurvalue(0.00);//多外币时删除本位币调整数
									rt.setEntryId(SUId);
									
									sql = "select * from z_exchangerate where projectid = ? and currname = ? ";
									ps = conn.prepareStatement(sql);
									ps.setString(1, projectID);
									ps.setString(2, rs.getString("DataName"));
									rs1 = ps.executeQuery();
									if(rs1.next()){
										set.setCurrrate(rs.getDouble("exchangerate"));
									}else{
										set.setCurrrate(1.00);
									}
									if(rt.getAssitemId() != null && !"".equals(rt.getAssitemId().trim())){
										bool = true;
									}
								}
								set.setSerail(ii);
								
								AddOrModifySubjectEntry(set,"ad"); //增加调整分录表
								if(bool){
									AddRectify(rt,"ad");
									bool = false;
								}
								ii++ ;
							}else{
								bool1 = true;
							}
						}
						DbUtil.close(rs);
						DbUtil.close(ps);
						
						iCount = ii;
						
						if(bool1){		//没有外币
							AddOrModifySubjectEntry(set,"ad"); //增加调整分录表
							if(bool){
								AddRectify(rt,"ad");
								bool = false;
							}
						}
						
						set.setCurrrate(0.00);
						set.setCurrvalue(0.00);
						set.setCurrency("");
						
						String str = getSCurrency(acc,set.getSubjectid());
						if(result.indexOf(str) > -1){
//							result = result.replaceAll(str, "");
							result = CHF.replaceStr(result, str, "");
						}
						
					}else{
						AddOrModifySubjectEntry(set,"ad"); //增加调整分录表
						if(bool){
							AddRectify(rt,"ad");
							bool = false;
						}
					}
					
					
				} //exit for 1
				
			} //exit for
			
			if("".equals(result)){
				result = "保存成功！";
			}
			createTzhz(acc,projectID);
			createWbTzhz(acc, projectID);
			createAssitem(acc,projectID);//增加核算金额
			//org.util.Debug.prtOut("计算0 = "+String.valueOf(new java.util.Date(System.currentTimeMillis())));
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(rs1);
			DbUtil.close(ps);
		}
	}
	
	
	public void insertAssitem(String acc, String proid, String assName,String SubjectID,String AssitemID)throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			
			String sql = "select * from c_assitem where accpackageid='"+acc+"' and accid='"+SubjectID+"' and assitemid='"+AssitemID+"'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(!rs.next()){
				sql = "INSERT into c_assitem (AccPackageID,accid,assitemid,assitemname,asstotalname,parentassitemid,debitremain,CreditRemain,isleaf,level0,uomunit,curr,property) " +
				" select AccPackageID,'"+SubjectID+"' as accid,assitemid,assitemname,asstotalname,parentassitemid,debitremain,CreditRemain,isleaf,level0,uomunit,curr,property FROM (" +
				" select * from c_assitem where accpackageid='"+acc+"'  and assitemid='"+AssitemID+"' limit 1) a ";
				ps = conn.prepareStatement(sql);
				ps.execute();
				/*删除新插入的核算体系*/
				sql = "delete from c_assitementryacc where accpackageid='"+acc+"' and accid='"+SubjectID+"' and assitemid='"+AssitemID+"'";
				//org.util.Debug.prtOut("DEL sql = "+sql);
				ps = conn.prepareStatement(sql);
				ps.execute();
			}
			
			sql = "select 1 from c_assitementryacc where accpackageid='"+acc+"' and accid='"+SubjectID+"' and assitemid='"+AssitemID+"'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(!rs.next()){
				sql = "";
				sql+=" insert into c_assitementryacc \n"; 
				sql+=" ( \n"; 
				sql+="        accpackageid,accid,assitemid,assitemname,subyearmonth,submonth, \n"; 
				sql+="        debitremain,creditremain,debitocc,creditocc, \n"; 
				sql+="        balance,debittotalocc,credittotalocc, \n"; 
				sql+="        direction,debitbalance,creditbalance,isleaf1,level1,asstotalname1,direction2,dataname \n"; 
				sql+=" ) \n"; 
				sql+=" SELECT accpackageid,accid,assitemid,assitemname,substring(accpackageid,7) as subyearmonth,submonth, \n"; 
				sql+="        0,0,0,0, \n"; 
				sql+="        0,0,0, \n"; 
				sql+="        direction,0,0,isleaf,`level0`,asstotalname,direction2,0 \n"; 
				sql+=" from   c_assitem a,k_month b, \n"; 
				sql+=" ( \n"; 
				sql+="     select  a.subjectid, case substring(property,2,1) when 2 then '-1' else substring(property,2,1) end as direction, \n";
				sql+="     ifnull(direction2,case substring(property,2,1) when 2 then '-1' else substring(property,2,1) end) direction2 \n";
				sql+="     FROM (select AccPackageID,subjectID,property from c_accpkgsubject where AccPackageID='"+acc+"' union select AccPackageID,subjectID,property from z_usesubject where AccPackageID='"+acc+"' and projectID='"+proid+"') a \n";
				sql+="     left join c_account b on  b.accpackageid='"+acc+"'  and a.subjectid=b.subjectid and submonth=1 \n";
				sql+="     where a.accpackageid='"+acc+"'  \n"; 
				sql+="     and a.subjectid='"+SubjectID+"'  \n"; 
				sql+=" ) c \n"; 
				sql+=" where accpackageid='"+acc+"'  \n"; 
				sql+="   and accid='"+SubjectID+"' \n"; 
				sql+="   and assitemid='"+AssitemID+"' \n"; 
				sql+="   and a.accid=c.subjectid \n"; 
				sql+="   and monthtype=12 \n"; 

				//org.util.Debug.prtOut("py insertAssitem: "+sql);
				ps = conn.prepareStatement(sql);
				ps.execute();
				
				/*删除新插入的核算体系*/
				sql = "delete from c_assitem where accpackageid='"+acc+"' and accid='"+SubjectID+"' and assitemid='"+AssitemID+"'";
				ps = conn.prepareStatement(sql);
				ps.execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);			
			DbUtil.close(ps);
		}
	}
	
	
	public String insertAssitem1(String acc, String proid, String ParentAssItemId ,String assName,String SubjectID)throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try {
			String AssitemID = "";
			
			sql = "select AssItemID from c_assitementryacc " +
			" where AccPackageID = ? and submonth = 1 " +
			" and accid = ? " +
			" and assitemid like concat(?,'%') " +
			" and isleaf1 = 1 " +
			" and assitemname = ? " +
			" order by length(AssItemID) desc, AssItemID desc limit 1 ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, acc);
			ps.setString(2, SubjectID);
			ps.setString(3, ParentAssItemId);
			ps.setString(4, assName);
			rs = ps.executeQuery();
			if(rs.next()){
				AssitemID = rs.getString(1);
				
			}else{
				DbUtil.close(rs);			
				DbUtil.close(ps);
				
				sql = "select AssItemID from c_assitementryacc " +
				" where AccPackageID = ? and SubMonth = 1 " +
				" and accid = ? " +
				" and assitemid like concat(?,'%') " +
				" and level1 = 2 order by length(AssItemID) desc, AssItemID desc limit 1";
				ps = conn.prepareStatement(sql);
				ps.setString(1, acc);
				ps.setString(2, SubjectID);
				ps.setString(3, ParentAssItemId);
				
				rs = ps.executeQuery();
				if(rs.next()){
					AssitemID = rs.getString(1);
				}
				DbUtil.close(rs);
				
				AssitemID = UTILString.getNewTaskCode(AssitemID);
				
				insertAssitem( acc,  proid,  ParentAssItemId , assName, SubjectID, AssitemID);
			}
			
			return AssitemID;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);			
			DbUtil.close(ps);
		}

	}
	
	public void insertAssitem(String acc, String proid, String ParentAssItemId ,String assName,String SubjectID,String AssitemID)throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			
			String sql = "select * from c_assitem where accpackageid='"+acc+"' and accid='"+SubjectID+"' and assitemid='"+AssitemID+"'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(!rs.next()){
				
				sql = "INSERT into c_assitem (AccPackageID,accid,assitemid,assitemname,asstotalname,parentassitemid,debitremain,CreditRemain,isleaf,level0,uomunit,curr,property)  " +
				" select AccPackageID,'"+SubjectID+"' as accid,'"+AssitemID+"' as assitemid,'"+assName+"' as assitemname,concat(asstotalname,'/"+assName+"') as asstotalname,assitemid as parentassitemid,debitremain,CreditRemain,1,level0 + 1,uomunit,curr,property FROM (" +
				" select * from c_assitem  where accpackageid='"+acc+"' and accid='"+SubjectID+"' and assitemid='"+ParentAssItemId+"' ) a ";
				ps = conn.prepareStatement(sql);
				ps.execute();
				/*删除新插入的核算体系*/
				sql = "delete from c_assitementryacc where accpackageid='"+acc+"' and accid='"+SubjectID+"' and assitemid='"+AssitemID+"'";
				//org.util.Debug.prtOut("DEL sql = "+sql);
				ps = conn.prepareStatement(sql);
				ps.execute();
			}
			
			sql = "select 1 from c_assitementryacc where accpackageid='"+acc+"' and accid='"+SubjectID+"' and assitemid='"+AssitemID+"'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(!rs.next()){
				sql = "";
				sql+=" insert into c_assitementryacc \n"; 
				sql+=" ( \n"; 
				sql+="        accpackageid,accid,assitemid,assitemname,subyearmonth,submonth, \n"; 
				sql+="        debitremain,creditremain,debitocc,creditocc, \n"; 
				sql+="        balance,debittotalocc,credittotalocc, \n"; 
				sql+="        direction,debitbalance,creditbalance,isleaf1,level1,asstotalname1,direction2,dataname \n"; 
				sql+=" ) \n"; 
				sql+=" SELECT accpackageid,accid,assitemid,assitemname,substring(accpackageid,7) as subyearmonth,submonth, \n"; 
				sql+="        0,0,0,0, \n"; 
				sql+="        0,0,0, \n"; 
				sql+="        direction,0,0,isleaf,`level0`,asstotalname,direction2,0 \n"; 
				sql+=" from   c_assitem a,k_month b, \n"; 
				sql+=" ( \n"; 
				sql+="     select  a.subjectid, case substring(property,2,1) when 2 then '-1' else substring(property,2,1) end as direction, \n";
				sql+="     ifnull(direction2,case substring(property,2,1) when 2 then '-1' else substring(property,2,1) end) direction2 \n";
				sql+="     FROM (select AccPackageID,subjectID,property from c_accpkgsubject where AccPackageID='"+acc+"' union select AccPackageID,subjectID,property from z_usesubject where AccPackageID='"+acc+"' and projectID='"+proid+"') a \n";
				sql+="     left join c_account b on  b.accpackageid='"+acc+"'  and a.subjectid=b.subjectid and submonth=1 \n";
				sql+="     where a.accpackageid='"+acc+"'  \n"; 
				sql+="     and a.subjectid='"+SubjectID+"'  \n"; 
				sql+=" ) c \n"; 
				sql+=" where accpackageid='"+acc+"'  \n"; 
				sql+="   and accid='"+SubjectID+"' \n"; 
				sql+="   and assitemid='"+AssitemID+"' \n"; 
				sql+="   and a.accid=c.subjectid \n"; 
				sql+="   and monthtype=12 \n"; 

				//org.util.Debug.prtOut("py insertAssitem: "+sql);
				ps = conn.prepareStatement(sql);
				ps.execute();
				
				/*删除新插入的核算体系*/
				sql = "delete from c_assitem where accpackageid='"+acc+"' and accid='"+SubjectID+"' and assitemid='"+AssitemID+"'";
				ps = conn.prepareStatement(sql);
				ps.execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);			
			DbUtil.close(ps);
		}
	}
	
	public String getUserSubject(String acc,String projectID,int eYear,String SubjectID,String OrderID)throws Exception{
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			StringBuffer sb = new StringBuffer("");
			String sql1 = "select a.accid,a.subjectid,a.oriparentsubjectid,a.oriparentdirection,a.oriaccid,a.orisubjectid,a.orisubjectname,a.orisubjectfullname,b.balance from(select distinct a.accid,a.subjectid,a.SubjectName,a.subjectfullname,a.oriparentsubjectid,a.oriparentdirection,a.oriaccid,a.orisubjectid,a.orisubjectname,a.orisubjectfullname from  c_usersubject a where a.oriDataName=0 and oriaccid ='' and accpackageid='"+acc+"' )a inner join c_account b on  b.subyearmonth*12+b.submonth="+eYear+" and a.orisubjectid=b.subjectid union select a.accid,a.subjectid,a.oriparentsubjectid,a.oriparentdirection,a.oriaccid,a.orisubjectid,a.orisubjectname,a.orisubjectfullname,b.balance from(select distinct a.accid,a.subjectid,a.SubjectName,a.subjectfullname,a.oriparentsubjectid,a.oriparentdirection,a.oriaccid,a.orisubjectid,a.orisubjectname,a.orisubjectfullname from  c_usersubject a where a.oriDataName=0 and oriaccid >'' and accpackageid='"+acc+"' )a inner join c_assitementryacc b on  b.subyearmonth*12+b.submonth="+eYear+" and a.oriaccid=b.accid and a.orisubjectid=b.assitemid";
			
			String s1 = "";
			String s2 = "";
			if("".equals(OrderID)){
				s1 = "";
				s2 = SubjectID;
			}else{
				s1 = SubjectID;
				s2 = OrderID;
			}
			String sql = "select if(b.oriaccid='',b.orisubjectid,b.oriaccid) SubjectID,if(b.oriaccid='','',b.orisubjectid) assitemid, " +
					" b.orisubjectname,b.orisubjectfullname,if(b.oriparentdirection=1,'借','贷') subDir,b.balance,b.oriparentdirection" +
					" from ("+sql1 + ") a ,("+sql1 + ") b where a.oriaccid = '"+s1+"' and a.orisubjectid = '"+s2+"'" +
					" and a.accid = b.accid and a.subjectid = b.subjectid and a.oriparentsubjectid = b.oriparentsubjectid order by b.oriaccid,b.orisubjectid";
			
			sql = "select a.SubjectID,a.AssItemID,orisubjectname,orisubjectfullname,subDir,oriparentdirection*(balance) qmremain," +
				" case a.oriparentdirection when 1 then ifnull(DebitTotalOcc2,0) when -1 then ifnull(CreditTotalOcc2,0) end occ," +
				" case a.oriparentdirection when 1 then oriparentdirection*(balance)+ifnull(DebitTotalOcc2,0) when -1 then oriparentdirection*(balance)+ifnull(CreditTotalOcc2,0) end bal" +
				" from ( "+sql+") a left join ( " +
				" select SubjectID,'' AssItemID,DebitTotalOcc2,CreditTotalOcc2 from z_accountrectify where ProjectID='"+projectID+"' union select SubjectID,AssItemID,DebitTotalOcc2,CreditTotalOcc2 from z_assitemaccrectify where ProjectID='"+projectID+"' " +
				" ) b on a.SubjectID=b.SubjectID and a.AssItemID=b.AssItemID";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				sb.append(rs.getString("SubjectID") + "`");
				sb.append(rs.getString("assitemid") + "`");
				sb.append(rs.getString("orisubjectname") + "`");
				sb.append(rs.getString("orisubjectfullname") + "`");
				sb.append(rs.getString("subDir") + "`");
				sb.append(rs.getString("qmremain") + "`" );	
				sb.append(" `" );
				sb.append(rs.getString("occ") + "`");
				sb.append(rs.getString("bal") );
				sb.append("|");
			}
			
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);			
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 函证(普通模式)
	 * @param acc
	 * @param projectID
	 * @param eYear
	 * @param SubjectID
	 * @param OrderID
	 * @param dataName
	 * @return
	 * @throws Exception
	 */
	public String getUserSubject(String acc,String projectID,int eYear,String SubjectID,String OrderID,String dataName)throws Exception{
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			StringBuffer sb = new StringBuffer("");
			String s1 = "";
			String s2 = "";
			String s3 = dataName;
			if("".equals(OrderID)){
				s1 = "";
				s2 = SubjectID;
			}else{
				s1 = SubjectID;
				s2 = OrderID;
			}
			
			if("本位币".equals(dataName)){
				s3 = "0";
			}
			
			String sql = "select a.* ,ifnull(exchangerate,1) exchangerate ,remain*ifnull(exchangerate,1) exrate from (select case when a.accid='' then a.subjectid else a.accid end subjectid," +
					"\n case when a.accid='' then '' else a.subjectid end  assitemid,a.accname," +
					"\n case when a.dataname='0' then '本位币' else a.dataname end dataname," +
					"\n case a.direction when 1 then '借' else '贷' end direction," +
					"\n a.direction*Balance Balance,a.direction*a.occ occ,a.direction*(Balance+occ) remain,a.dataname dName " +
					"\n from (" +
//					分组	科目
					"\n		select  a.*,ifnull((DebitTotalOcc2-CreditTotalOcc2),0) occ  from ( " +
					"\n			select '' accid,subjectid,accname,SubjectFullName1, DataName,direction2 direction,Balance  from c_account where accpackageid='"+acc+"' and SubMonth = "+eYear+" and isleaf1=1  " +
					"\n			and exists (select 1 from c_usersubject where 1=1 and accpackageid='"+acc+"' and oriaccid='' and oriDataName=0 and c_account.subjectid=orisubjectid)" +
					"\n		) a left join z_accountrectify b on projectID='"+projectID+"' and b.isleaf=1 and a.SubjectID=b.SubjectID " +
					
					"\n		union " +
					
//					分组	科目外币
					"\n		select a.*,ifnull((DebitTotalOcc2-CreditTotalOcc2),0) occ from ( " +
					"\n			select '' accid,subjectid,accname,SubjectFullName1, DataName,direction2 direction,Balance  from c_accountall where accpackageid='"+acc+"' and SubMonth = "+eYear+" and isleaf1=1 and accsign = 1 " +
					"\n			and not exists (select 1 from c_usersubject where 1=1 and accpackageid='"+acc+"' and oriaccid='' and c_accountall.subjectid=orisubjectid and c_accountall.DataName = oriDataName)" +
					"\n		) a left join z_accountallrectify b on projectID='"+projectID+"' and b.isleaf1=1  and a.SubjectID=b.SubjectID and a.DataName=b.DataName  " +
					
					"\n		union " +
					
//					分组核算
					"\n		select a.*,ifnull((DebitTotalOcc2-CreditTotalOcc2),0) occ from ( " +
					"\n			select  accid subjectid,AssItemID,AssItemName,AssTotalName1, DataName,direction,Balance   from c_assitementryacc where accpackageid='"+acc+"' and SubMonth = "+eYear+" and isleaf1=1  " +
					"\n			and exists (select 1 from c_usersubject where 1=1 and accpackageid='"+acc+"' and  c_assitementryacc.accid = oriaccid and c_assitementryacc.assitemid=orisubjectid and oriDataName=0)" +
					"\n		) a left join z_assitemaccrectify b on projectID='"+projectID+"'  and a.subjectid=b.SubjectID and a.AssItemID=b.AssItemID  " +
					
					"\n		union " +
					
//					分组核算外币
					"\n		select a.*,ifnull((DebitTotalOcc2-CreditTotalOcc2),0) occ from ( " +
					"\n			select  accid subjectid,AssItemID,AssItemName,AssTotalName1, DataName,direction,Balance   from c_assitementryaccall where accpackageid='"+acc+"' and SubMonth = "+eYear+" and isleaf1=1 and accsign = 1  " +
					"\n			and exists (select 1 from c_usersubject where 1=1 and accpackageid='"+acc+"' and  c_assitementryaccall.accid = oriaccid and c_assitementryaccall.assitemid=orisubjectid and c_assitementryaccall.DataName =oriDataName)" +
					"\n		) a left join z_assitemaccallrectify b on projectID='"+projectID+"'  and a.subjectid=b.SubjectID and a.AssItemID=b.AssItemID and a.DataName=b.DataName  " +

					"\n ) a ,(" +
					
					"\n		select a.* from c_usersubject a ,c_usersubject b where a.accpackageid='"+acc+"' and b.accpackageid='"+acc+"' " +
					"\n		and b.oriaccid='"+s1+"' and b.orisubjectid='"+s2+"' and b.oriDataName='"+dataName+"'" +
					"\n		and a.accid=b.accid and a.subjectid = b.subjectid" +
					"\n ) b where a.accid = b.oriaccid and a.subjectid = b.orisubjectid and a.DataName=b.oriDataName " +
					
					"\n having abs(Balance) + abs(occ) + abs(remain) <>0 order by a.accid,a.subjectid" +
					"\n ) a left join z_exchangerate b on a.dName = b.currname and b.projectid='"+projectID+"'";
			
			//org.util.Debug.prtOut("py sql :=|"+ sql);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				sb.append(rs.getString("subjectid") + "`");
				sb.append(rs.getString("assitemid") + "`");
				sb.append(rs.getString("accname") + "`");
				sb.append(rs.getString("dataname") + "`");
				sb.append(rs.getString("occ") + "`");
				sb.append(rs.getString("direction") + "`");
				sb.append(rs.getString("Balance") + "`" );	
				
				sb.append(rs.getString("exchangerate") + "`" );
				sb.append(rs.getString("exrate") + "`");
				sb.append(rs.getString("remain")  );
				
				
				
				sb.append("|");
			}
			
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);			
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 函证(分层模式)
	 * @param acc
	 * @param projectID
	 * @param bDate
	 * @param eDate
	 * @param SubjectID
	 * @param OrderID
	 * @param dataName
	 * @return
	 * @throws Exception
	 */
	public String getUserSubject(String acc,String projectID,int bDate,int eDate,String SubjectID,String OrderID,String dataName)throws Exception{
			DbUtil.checkConn(conn);
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				StringBuffer sb = new StringBuffer("");
				String s1 = "";
				String s2 = "";
				String s3 = dataName;
				
				String tt = "_" + SubjectID + "_" + OrderID;
				if("".equals(OrderID)){
					s1 = "";
					s2 = SubjectID;
				}else{
					s1 = SubjectID;
					s2 = OrderID;
				}
				
				if("本位币".equals(dataName)){
					s3 = "0";
				}
				
				String sql = "select case when a.accid='' then a.subjectid else a.accid end subjectid," +
						"\n case when a.accid='' then '' else a.subjectid end  assitemid,a.accname," +
						"\n case when a.dataname='0' then '本位币' else concat('外币：',a.DataName) end dataname," +
						"\n case a.direction when 1 then '借' else '贷' end direction," +
						"\n a.direction*initBalance initBalance,DebitTotalOcc,CreditTotalOcc," +
						"\n a.direction*Balance Balance,a.direction*a.occ occ,a.direction*(Balance+occ) remain,a.dataname dName ," +
						"\n if((Balance+occ)>=0,abs(Balance+occ),0) ggsq, if((Balance+occ)<0,abs(Balance+occ),0) qggs ,1 as exchangerate ,a.direction * exrate as exrate," +
						"\n	a.SubjectFullName" +
						"\n from (" +
//						分组	科目
						"\n		select  a.*,ifnull((DebitTotalOcc2-CreditTotalOcc2),0) occ,c.SubjectFullName   from ( " +
						"\n			select '' accid,subjectid,accname,SubjectFullName1, DataName,direction2 direction," +
						"\n			sum(case subyearmonth*12+submonth when "+eDate+" then Balance else 0 end) Balance,  " +
						"\n			sum(case subyearmonth*12+submonth when "+eDate+" then Balance else 0 end) exrate,  " +
						"\n			sum(case subyearmonth*12+submonth when "+bDate+" then (DebitRemain+CreditRemain) else 0 end)  initBalance,  " +
						"\n			sum(DebitOcc) DebitTotalOcc,  sum(CreditOcc) CreditTotalOcc    " +
						"\n			from c_account " +
						"\n			where  subyearmonth*12+submonth>="+bDate+" and subyearmonth*12+submonth<="+eDate+"  and isleaf1=1  " +
						"\n			group by subjectid " +
						"\n		) a " +
						"\n		left join (select * from z_accountrectify b where projectID='"+projectID+"' and b.isleaf=1 ) b on a.SubjectID=b.SubjectID " +
						"\n		inner join (select * from c_accpkgsubject where 1=1 and accpackageid='"+acc+"' and level0 = 1) c on (a.SubjectFullName1 = c.SubjectFullName or a.SubjectFullName1 like concat(c.SubjectFullName,'/%'))" +
						"\n 	where 1=1 " +
						"\n		and exists (select 1 from c_usersubject where 1=1 and accpackageid='"+acc+"' and oriaccid='' and oriDataName=0 and a.subjectid=orisubjectid)" +
						"\n		and not exists (select 1 from c_accountall where 1=1 and accpackageid='"+acc+"' and subyearmonth*12+submonth>="+bDate+" and subyearmonth*12+submonth<="+eDate+" and isleaf1=1 and accsign = 1 and a.subjectid=subjectid )" + 
						"\n 	and not exists (select 1 from c_assitementryacc where 1=1 and accpackageid='"+acc+"' and subyearmonth*12+submonth>="+bDate+" and subyearmonth*12+submonth<="+eDate+" and isleaf1=1 and a.subjectid=accid )" + 
						
						
						"\n		union " +
						
//						分组	科目外币
						"\n		select a.*,ifnull((DebitTotalOcc2-CreditTotalOcc2),0) occ,c.SubjectFullName  from ( " +
						"\n			select '' accid,subjectid,accname,SubjectFullName1, DataName,direction2 direction,  " +
						"\n			sum(case subyearmonth*12+submonth when "+eDate+" then Balance else 0 end) Balance,  " +
						"\n			sum(case subyearmonth*12+submonth when "+eDate+" then BalanceF else 0 end) exrate,  " +
						"\n			sum(case subyearmonth*12+submonth when "+bDate+" then (DebitRemain+CreditRemain) else 0 end)  initBalance,  " +
						"\n			sum(DebitOcc) DebitTotalOcc,  sum(CreditOcc) CreditTotalOcc    " +
						"\n			from c_accountall " +
						"\n			where  subyearmonth*12+submonth>="+bDate+" and subyearmonth*12+submonth<="+eDate+"  and isleaf1=1  and accsign = 1  " +
						"\n			group by subjectid,DataName" +
						"\n		) a " +
						"\n		left join z_accountallrectify b on projectID='"+projectID+"' and b.isleaf1=1  and a.SubjectID=b.SubjectID and a.DataName=b.DataName  " +
						"\n		inner join (select * from c_accpkgsubject where 1=1 and accpackageid='"+acc+"' and level0 = 1) c on (a.SubjectFullName1 = c.SubjectFullName or a.SubjectFullName1 like concat(c.SubjectFullName,'/%'))" +
						"\n		where 1=1 " +
						"\n		and exists (select 1 from c_usersubject where 1=1 and accpackageid='"+acc+"' and oriaccid='' and a.subjectid=orisubjectid and a.DataName = oriDataName)" +
						"\n 	and not exists (select 1 from c_assitementryacc where 1=1 and accpackageid='"+acc+"' and subyearmonth*12+submonth>="+bDate+" and subyearmonth*12+submonth<="+eDate+" and isleaf1=1 and a.subjectid=accid)" +
						
						"\n		union " +
						
//						分组核算
						"\n		select a.*,ifnull((DebitTotalOcc2-CreditTotalOcc2),0) occ,c.SubjectFullName  from ( " +
						"\n			select  accid subjectid,AssItemID,AssItemName,AssTotalName1, DataName,direction," +
						"\n			sum(case subyearmonth*12+submonth when "+eDate+" then Balance else 0 end) Balance,  " +
						"\n			sum(case subyearmonth*12+submonth when "+eDate+" then Balance else 0 end) exrate,  " +
						"\n			sum(case subyearmonth*12+submonth when "+bDate+" then (DebitRemain+CreditRemain) else 0 end)  initBalance,  " +
						"\n			sum(DebitOcc) DebitTotalOcc,  sum(CreditOcc) CreditTotalOcc    " +

						"\n			from c_assitementryacc " +
						"\n			where  subyearmonth*12+submonth>="+bDate+" and subyearmonth*12+submonth<="+eDate+"  and isleaf1=1  " +
						"\n			group by accid ,AssItemID" +
						"\n		) a " +
						"\n		left join z_assitemaccrectify b on projectID='"+projectID+"'  and a.subjectid=b.SubjectID and a.AssItemID=b.AssItemID  " +
						"\n		inner join c_accpkgsubject d on d.accpackageid='"+acc+"' and a.subjectid = d.subjectid " +
						"\n		inner join (select * from c_accpkgsubject where 1=1 and accpackageid='"+acc+"' and level0 = 1) c on (d.SubjectFullName = c.SubjectFullName or d.SubjectFullName like concat(c.SubjectFullName,'/%')) " +
						
						"\n		where 1=1 " +
						"\n		and exists (select 1 from c_usersubject where 1=1 and accpackageid='"+acc+"' and  a.subjectid = oriaccid and a.assitemid=orisubjectid and oriDataName=0)" +
						"\n 	and not exists (select 1 from c_assitementryaccall where 1=1 and accpackageid='"+acc+"' and subyearmonth*12+submonth>="+bDate+" and subyearmonth*12+submonth<="+eDate+" and isleaf1=1 and accsign = 1 and a.subjectid=accid and a.assitemid=assitemid)" +
						
						
						"\n		union " +
						
//						分组核算外币
						"\n		select a.*,ifnull((DebitTotalOcc2-CreditTotalOcc2),0) occ,c.SubjectFullName  from ( " +
						"\n			select  accid subjectid,AssItemID,AssItemName,AssTotalName1, DataName,direction," +
						"\n			sum(case subyearmonth*12+submonth when "+eDate+" then Balance else 0 end) Balance,  " +
						"\n			sum(case subyearmonth*12+submonth when "+eDate+" then BalanceF else 0 end) exrate,  " +
						"\n			sum(case subyearmonth*12+submonth when "+bDate+" then (DebitRemain+CreditRemain) else 0 end)  initBalance,  " +
						"\n			sum(DebitOcc) DebitTotalOcc,  sum(CreditOcc) CreditTotalOcc    " +
						"\n			from c_assitementryaccall " +
						"\n			where  subyearmonth*12+submonth>="+bDate+" and subyearmonth*12+submonth<="+eDate+"  and isleaf1=1  and accsign = 1  " +
						"\n			group by accid ,AssItemID,DataName" +
						"\n		) a " +
						"\n		left join z_assitemaccallrectify b on projectID='"+projectID+"'  and a.subjectid=b.SubjectID and a.AssItemID=b.AssItemID and a.DataName=b.DataName  " +
						"\n		inner join c_accpkgsubject d on d.accpackageid='"+acc+"' and a.subjectid = d.subjectid " +
						"\n		inner join (select * from c_accpkgsubject where 1=1 and accpackageid='"+acc+"' and level0 = 1) c on (d.SubjectFullName = c.SubjectFullName or d.SubjectFullName like concat(c.SubjectFullName,'/%')) " +

						"\n		where 1=1" +
						"\n		and exists (select 1 from c_usersubject where 1=1 and accpackageid='"+acc+"' and  a.subjectid = oriaccid and a.assitemid=orisubjectid and a.DataName =oriDataName)" +
						
						
						"\n ) a ,(" +
						
						"\n		select a.* from c_usersubject a ,c_usersubject b where a.accpackageid='"+acc+"' and b.accpackageid='"+acc+"' " +
						"\n		and b.oriaccid='"+s1+"' and b.orisubjectid='"+s2+"' and b.oriDataName='"+dataName+"'" +
						"\n		and a.accid=b.accid and a.subjectid = b.subjectid" +
						"\n ) b where a.accid = b.oriaccid and a.subjectid = b.orisubjectid and a.DataName=b.oriDataName " +
						
						"\n having abs(initBalance) + abs(DebitTotalOcc) + abs(CreditTotalOcc) + abs(Balance) + abs(occ) + abs(remain) <>0 order by a.accid,a.subjectid";
				
				//org.util.Debug.prtOut("py sql :=|"+ sql);
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				int ii=0;
				ASFuntion CHF=new ASFuntion();
				while(rs.next()){
					
					String subjectid = rs.getString("subjectid");
					String assitemid = rs.getString("assitemid");
					String accname = rs.getString("accname");
					String dataname = rs.getString("dataname");
					
					String initBalance = rs.getString("initBalance");
					String DebitTotalOcc = rs.getString("DebitTotalOcc");
					String CreditTotalOcc = rs.getString("CreditTotalOcc");
					
					String occ = rs.getString("occ");
					String direction = rs.getString("direction");
					String Balance = rs.getString("Balance");
					
//					String exchangerate = rs.getString("exchangerate");
					String exrate = rs.getString("exrate");
					String remain = rs.getString("remain");
					
					String ggsq = rs.getString("ggsq");
					String qggs = rs.getString("qggs");
					
					String SubjectFullName = rs.getString("SubjectFullName");
					
					sb.append("<tr id='"+tt+"' bgColor='#b2c2d2' height='18' subName = '"+SubjectFullName+"'>");
					sb.append("<td></td>");
					sb.append("<td noWrap>"+subjectid+"</td>");
				    sb.append("<td noWrap>"+assitemid+"</td>");
				    sb.append("<td noWrap>"+accname+"</td>");
				    sb.append("<td noWrap>"+dataname+"</td>");
				    
				    sb.append(CHF.showMoney(initBalance));
				    sb.append(CHF.showMoney(DebitTotalOcc));
				    sb.append(CHF.showMoney(CreditTotalOcc));
				    
				    sb.append(CHF.showMoney(occ));
				    sb.append("<td style='TEXT-ALIGN: center'>"+direction+"</td>");
				    sb.append(CHF.showMoney(Balance));
				    
//				    sb.append(CHF.showMoney(exchangerate));
				    sb.append(CHF.showMoney(exrate));
				    
				    sb.append("<td noWrap align='middle'><input id='newGgsq"+ii+"' maxLength='15' size='16' value='"+ggsq+"' name='newGgsq"+ii+"'></td>");
				    sb.append("<td noWrap align='middle'><input id='newQggs"+ii+"' maxLength='15' size='16' value='"+qggs+"' name='newQggs"+ii+"'></td>");
				    
//				    sb.append("<td noWrap align='middle'><input id='newTD"+ii+"' maxLength='15' size='16' value='"+remain+"' name='newTD"+ii+"'></td>");
				    
				    sb.append("</tr>");

				}
				
				return sb.toString();
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			} finally {
				DbUtil.close(rs);			
				DbUtil.close(ps);
			}
		}
	
	/**
	 * |subjectid:21210101`assitemid:3-SD-049`accname:冯浩芹`dataname:本位币`direction:贷`initbalance:0.00`debittotalocc:0.34`credittotalocc:1530.34`balance:1530.00`occ:0.00`remain:1530.00`dname:0`ggsq:0.00`qggs:1530.00`exchangerate:1`exrate:1530.00`subjectfullname:应付账款`|subjectid:212102`assitemid:3-SD-049`accname:冯浩芹`dataname:本位币`direction:贷`initbalance:8541584.70`debittotalocc:11956837.17`credittotalocc:24461524.09`balance:21046271.62`occ:0.00`remain:21046271.62`dname:0`ggsq:0.00`qggs:21046271.62`exchangerate:1`exrate:21046271.62`subjectfullname:应付账款`||subjectid:21210101`assitemid:3-SD-049`accname:冯浩芹`dataname:本位币`direction:贷`initbalance:0.00`debittotalocc:0.34`credittotalocc:1530.34`balance:1530.00`occ:0.00`remain:1530.00`dname:0`ggsq:0.00`qggs:1530.00`exchangerate:1`exrate:1530.00`subjectfullname:应付账款`|subjectid:212102`assitemid:3-SD-049`accname:冯浩芹`dataname:本位币`direction:贷`initbalance:8541584.70`debittotalocc:11956837.17`credittotalocc:24461524.09`balance:21046271.62`occ:0.00`remain:21046271.62`dname:0`ggsq:0.00`qggs:21046271.62`exchangerate:1`exrate:21046271.62`subjectfullname:应付账款`|
	 */
	public String getUserSubjectExcel(String acc,String projectID,int bDate,int eDate,String SubjectID,String OrderID,String dataName)throws Exception{
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			StringBuffer sb = new StringBuffer("");
			String s1 = "";
			String s2 = "";
			String s3 = dataName;
			
			String tt = "_" + SubjectID + "_" + OrderID;
			if("".equals(OrderID)){
				s1 = "";
				s2 = SubjectID;
			}else{
				s1 = SubjectID;
				s2 = OrderID;
			}
			
			if("本位币".equals(dataName)){
				s3 = "0";
			}
			
			String sql = "select case when a.accid='' then a.subjectid else a.accid end subjectid," +
					"\n case when a.accid='' then '' else a.subjectid end  assitemid,a.accname," +
					"\n case when a.dataname='0' then '本位币' else concat('外币：',a.DataName) end dataname," +
					"\n case a.direction when 1 then '借' else '贷' end direction," +
					"\n a.direction*initBalance initBalance,DebitTotalOcc,CreditTotalOcc," +
					"\n a.direction*Balance Balance,a.direction*a.occ occ,a.direction*(Balance+occ) remain,a.dataname dName ," +
					"\n if((Balance+occ)>=0,abs(Balance+occ),0) ggsq, if((Balance+occ)<0,abs(Balance+occ),0) qggs ,1 as exchangerate ,a.direction * exrate as exrate," +
					"\n	a.SubjectFullName" +
					"\n from (" +
//					分组	科目
					"\n		select  a.*,ifnull((DebitTotalOcc2-CreditTotalOcc2),0) occ,c.SubjectFullName   from ( " +
					"\n			select '' accid,subjectid,accname,SubjectFullName1, DataName,direction2 direction," +
					"\n			sum(case subyearmonth*12+submonth when "+eDate+" then Balance else 0 end) Balance,  " +
					"\n			sum(case subyearmonth*12+submonth when "+eDate+" then Balance else 0 end) exrate,  " +
					"\n			sum(case subyearmonth*12+submonth when "+bDate+" then (DebitRemain+CreditRemain) else 0 end)  initBalance,  " +
					"\n			sum(DebitOcc) DebitTotalOcc,  sum(CreditOcc) CreditTotalOcc    " +
					"\n			from c_account " +
					"\n			where  subyearmonth*12+submonth>="+bDate+" and subyearmonth*12+submonth<="+eDate+"  and isleaf1=1  " +
					"\n			group by subjectid " +
					"\n		) a " +
					"\n		left join (select * from z_accountrectify b where projectID='"+projectID+"' and b.isleaf=1 ) b on a.SubjectID=b.SubjectID " +
					"\n		inner join (select * from c_accpkgsubject where 1=1 and accpackageid='"+acc+"' and level0 = 1) c on (a.SubjectFullName1 = c.SubjectFullName or a.SubjectFullName1 like concat(c.SubjectFullName,'/%'))" +
					"\n 	where 1=1 " +
					"\n		and exists (select 1 from c_usersubject where 1=1 and accpackageid='"+acc+"' and oriaccid='' and oriDataName=0 and a.subjectid=orisubjectid)" +
					"\n		and not exists (select 1 from c_accountall where 1=1 and accpackageid='"+acc+"' and subyearmonth*12+submonth>="+bDate+" and subyearmonth*12+submonth<="+eDate+" and isleaf1=1 and accsign = 1 and a.subjectid=subjectid )" + 
					"\n 	and not exists (select 1 from c_assitementryacc where 1=1 and accpackageid='"+acc+"' and subyearmonth*12+submonth>="+bDate+" and subyearmonth*12+submonth<="+eDate+" and isleaf1=1 and a.subjectid=accid )" + 
					
					
					"\n		union " +
					
//					分组	科目外币
					"\n		select a.*,ifnull((DebitTotalOcc2-CreditTotalOcc2),0) occ,c.SubjectFullName  from ( " +
					"\n			select '' accid,subjectid,accname,SubjectFullName1, DataName,direction2 direction,  " +
					"\n			sum(case subyearmonth*12+submonth when "+eDate+" then Balance else 0 end) Balance,  " +
					"\n			sum(case subyearmonth*12+submonth when "+eDate+" then BalanceF else 0 end) exrate,  " +
					"\n			sum(case subyearmonth*12+submonth when "+bDate+" then (DebitRemain+CreditRemain) else 0 end)  initBalance,  " +
					"\n			sum(DebitOcc) DebitTotalOcc,  sum(CreditOcc) CreditTotalOcc    " +
					"\n			from c_accountall " +
					"\n			where  subyearmonth*12+submonth>="+bDate+" and subyearmonth*12+submonth<="+eDate+"  and isleaf1=1  and accsign = 1  " +
					"\n			group by subjectid,DataName" +
					"\n		) a " +
					"\n		left join z_accountallrectify b on projectID='"+projectID+"' and b.isleaf1=1  and a.SubjectID=b.SubjectID and a.DataName=b.DataName  " +
					"\n		inner join (select * from c_accpkgsubject where 1=1 and accpackageid='"+acc+"' and level0 = 1) c on (a.SubjectFullName1 = c.SubjectFullName or a.SubjectFullName1 like concat(c.SubjectFullName,'/%'))" +
					"\n		where 1=1 " +
					"\n		and exists (select 1 from c_usersubject where 1=1 and accpackageid='"+acc+"' and oriaccid='' and a.subjectid=orisubjectid and a.DataName = oriDataName)" +
					"\n 	and not exists (select 1 from c_assitementryacc where 1=1 and accpackageid='"+acc+"' and subyearmonth*12+submonth>="+bDate+" and subyearmonth*12+submonth<="+eDate+" and isleaf1=1 and a.subjectid=accid)" +
					
					"\n		union " +
					
//					分组核算
					"\n		select a.*,ifnull((DebitTotalOcc2-CreditTotalOcc2),0) occ,c.SubjectFullName  from ( " +
					"\n			select  accid subjectid,AssItemID,AssItemName,AssTotalName1, DataName,direction," +
					"\n			sum(case subyearmonth*12+submonth when "+eDate+" then Balance else 0 end) Balance,  " +
					"\n			sum(case subyearmonth*12+submonth when "+eDate+" then Balance else 0 end) exrate,  " +
					"\n			sum(case subyearmonth*12+submonth when "+bDate+" then (DebitRemain+CreditRemain) else 0 end)  initBalance,  " +
					"\n			sum(DebitOcc) DebitTotalOcc,  sum(CreditOcc) CreditTotalOcc    " +

					"\n			from c_assitementryacc " +
					"\n			where  subyearmonth*12+submonth>="+bDate+" and subyearmonth*12+submonth<="+eDate+"  and isleaf1=1  " +
					"\n			group by accid ,AssItemID" +
					"\n		) a " +
					"\n		left join z_assitemaccrectify b on projectID='"+projectID+"'  and a.subjectid=b.SubjectID and a.AssItemID=b.AssItemID  " +
					"\n		inner join c_accpkgsubject d on d.accpackageid='"+acc+"' and a.subjectid = d.subjectid " +
					"\n		inner join (select * from c_accpkgsubject where 1=1 and accpackageid='"+acc+"' and level0 = 1) c on (d.SubjectFullName = c.SubjectFullName or d.SubjectFullName like concat(c.SubjectFullName,'/%')) " +
					
					"\n		where 1=1 " +
					"\n		and exists (select 1 from c_usersubject where 1=1 and accpackageid='"+acc+"' and  a.subjectid = oriaccid and a.assitemid=orisubjectid and oriDataName=0)" +
					"\n 	and not exists (select 1 from c_assitementryaccall where 1=1 and accpackageid='"+acc+"' and subyearmonth*12+submonth>="+bDate+" and subyearmonth*12+submonth<="+eDate+" and isleaf1=1 and accsign = 1 and a.subjectid=accid and a.assitemid=assitemid)" +
					
					
					"\n		union " +
					
//					分组核算外币
					"\n		select a.*,ifnull((DebitTotalOcc2-CreditTotalOcc2),0) occ,c.SubjectFullName  from ( " +
					"\n			select  accid subjectid,AssItemID,AssItemName,AssTotalName1, DataName,direction," +
					"\n			sum(case subyearmonth*12+submonth when "+eDate+" then Balance else 0 end) Balance,  " +
					"\n			sum(case subyearmonth*12+submonth when "+eDate+" then BalanceF else 0 end) exrate,  " +
					"\n			sum(case subyearmonth*12+submonth when "+bDate+" then (DebitRemain+CreditRemain) else 0 end)  initBalance,  " +
					"\n			sum(DebitOcc) DebitTotalOcc,  sum(CreditOcc) CreditTotalOcc    " +
					"\n			from c_assitementryaccall " +
					"\n			where  subyearmonth*12+submonth>="+bDate+" and subyearmonth*12+submonth<="+eDate+"  and isleaf1=1  and accsign = 1  " +
					"\n			group by accid ,AssItemID,DataName" +
					"\n		) a " +
					"\n		left join z_assitemaccallrectify b on projectID='"+projectID+"'  and a.subjectid=b.SubjectID and a.AssItemID=b.AssItemID and a.DataName=b.DataName  " +
					"\n		inner join c_accpkgsubject d on d.accpackageid='"+acc+"' and a.subjectid = d.subjectid " +
					"\n		inner join (select * from c_accpkgsubject where 1=1 and accpackageid='"+acc+"' and level0 = 1) c on (d.SubjectFullName = c.SubjectFullName or d.SubjectFullName like concat(c.SubjectFullName,'/%')) " +

					"\n		where 1=1" +
					"\n		and exists (select 1 from c_usersubject where 1=1 and accpackageid='"+acc+"' and  a.subjectid = oriaccid and a.assitemid=orisubjectid and a.DataName =oriDataName)" +
					
					
					"\n ) a ,(" +
					
					"\n		select a.* from c_usersubject a ,c_usersubject b where a.accpackageid='"+acc+"' and b.accpackageid='"+acc+"' " +
					"\n		and b.oriaccid='"+s1+"' and b.orisubjectid='"+s2+"' and b.oriDataName='"+dataName+"'" +
					"\n		and a.accid=b.accid and a.subjectid = b.subjectid" +
					"\n ) b where a.accid = b.oriaccid and a.subjectid = b.orisubjectid and a.DataName=b.oriDataName " +
					
					"\n having abs(initBalance) + abs(DebitTotalOcc) + abs(CreditTotalOcc) + abs(Balance) + abs(occ) + abs(remain) <>0 order by a.accid,a.subjectid";
			
			//org.util.Debug.prtOut("py sql :=|"+ sql);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			int ii=0;
			ASFuntion CHF=new ASFuntion();
			sb.append("|");
			ResultSetMetaData RSMD = rs.getMetaData();			
			while(rs.next()){
				
				for (int i = 1; i <= RSMD.getColumnCount(); i++) {
					sb.append(RSMD.getColumnLabel(i).toLowerCase() + ":" + rs.getString(RSMD.getColumnLabel(i)) + "`");
				}
				
				sb.append("|");
			}
			System.out.println(sb.toString());
			
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);			
			DbUtil.close(ps);
		}
	}
	/**
	 * 对冲重分类
	 */
	
	
	public String getSubjectTable(String acc,String projectID,int bYear,int eYear,String [] key) throws Exception{
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		PreparedStatement ps1 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		try {
			ASFuntion CHF=new ASFuntion();
			
			String ClassSubject = getClassSubject(acc, projectID, key);
			String NotSubject = getNotClassSubject(acc);
			String s1 = "".equals(ClassSubject) ? " and 1=2 " : " and a.subjectid in ("+ClassSubject+") ";
			String s2 = "".equals(NotSubject) ? " and 1=2 " : " and a.subjectid not IN("+NotSubject+") ";
			String s3 = "".equals(ClassSubject) ? " and 1=2 " : " and a.accid in ("+ClassSubject+") ";
			String s4 = " and a.AssTotalName1 not like '现金流量%' ";
	
			StringBuffer sb = new StringBuffer("");
			
			String sql1 = "select '' as accid, a.subjectid,AccName,subjectfullname1," +
					" sum(if (subyearmonth*12+submonth="+bYear+",direction*(a.DebitRemain+a.CreditRemain),0)) Remain," +
					" sum(debitocc) as debitocc,sum(creditocc) as creditocc," +
					" sum(if (subyearmonth*12+submonth="+eYear+",(direction*Balance),0)) Balance,if(a.direction=1,'借','贷') dir" +
					" from c_account a 	where subyearmonth*12+submonth>="+bYear+" and subyearmonth*12+submonth<="+eYear+" " +
					" " + s1 + s2 +
					" and isleaf1=1 group by subjectid HAVING  ABS(Remain) + abs(debitocc) + abs(creditocc) + abs(Balance)>0  " +
					" union select accid,AssItemID,AssItemName,AssTotalName1," +
					" sum(if (subyearmonth*12+submonth="+bYear+",direction*(a.DebitRemain+a.CreditRemain),0)) Remain," +
					" sum(debitocc) as debitocc,sum(creditocc) as creditocc," +
					" sum(if (subyearmonth*12+submonth="+eYear+",(direction*Balance),0)) Balance,if(a.direction=1,'借','贷') dir " +
					" from c_assitementryacc a 	" +
					" where subyearmonth*12+submonth>="+bYear+" and subyearmonth*12+submonth<="+eYear+" " +
					" " + s3 + s4 +
					" and a.isleaf1=1 group by accid,AssItemID  HAVING  ABS(Remain) + abs(debitocc) + abs(creditocc) + abs(Balance)>0 ";
			
			String sql2 = "select ifnull(a.accid,b.accid) accid,ifnull(a.subjectid,b.subjectid) subjectid," +
					" ifnull(a.AccName,b.subjectName) AccName,ifnull(a.subjectfullname1,b.subjectfullname) subjectfullname1," +
					" ifnull(a.dir,b.dir) sumdir,ifnull(a.Remain,0) Remain," +
					" ifnull(a.debitocc,0) debitocc,ifnull(a.creditocc,0) creditocc," +
					" ifnull(a.dir,b.dir) qmdir,ifnull(a.Balance,0) Balance " +
					" from ( " + sql1 + " ) a  right join  (" +
					" select distinct accpackageid,accid,SubjectID,subjectName,subjectfullname,if(direction=1,'借','贷') dir from c_usersubject where oriDataName=0 and accpackageid='"+acc+"' " +
					" ) b on concat(a.accid,a.subjectid) =concat(b.accid, b.SubjectID) order by concat(b.accid,b.subjectid)";
			
			ps = conn.prepareStatement(sql2);
			rs = ps.executeQuery();
			int jj = 1;
			while(rs.next()){
				String accid = rs.getString("accid");
				String subjectId = rs.getString("subjectid");
				String SubjectFullName = rs.getString("subjectfullname1");				
				
				String Balance = rs.getString("Balance");
				String sumdir = rs.getString("sumdir");
				
				sb.append("<tr onMouseOver=\"this.bgColor='#E4E8EF';\" onMouseOut=\"this.bgColor='#CCCCCC';\"  bgColor=\"#CCCCCC\" >");				
				sb.append("<TD colSpan=2 noWrap><IMG id='Img"+accid + subjectId+"' src='/AuditSystem/images/plus.jpg'>");
				if("".equals(accid)){
					sb.append("【<font color=\"#0000FF\">"+subjectId+"</font>】" + SubjectFullName + " <font color=\"#0000FF\">(科目)</font>");
				}else{
					sb.append("【<font color=\"red\">"+subjectId+"</font>】" + SubjectFullName + " <font color=\"red\">(核算)</font>　<font color=\"#0000FF\">科目："+accid + "</font>");
				}				
				sb.append("</TD>");
				sb.append("<TD align='center'>"+sumdir+"</TD>");
				sb.append(CHF.showMoney(rs.getString("Remain")));
				sb.append(CHF.showMoney(rs.getString("debitocc")));
				sb.append(CHF.showMoney(rs.getString("creditocc")));
				sb.append("<TD align='center'>"+rs.getString("qmdir")+"</TD>");
				sb.append(CHF.showMoney(Balance));
				
				sb.append("<TD><input type='button' class='flyBT' name='btn' id='btn' value='对冲' SubjectID='"+subjectId+"' accid='"+accid+"' sumdir='"+sumdir+"' Balance='"+Balance+"' AccName='"+rs.getString("AccName")+"' onclick=\"return getClass(this,'"+jj+"');\"></TD>"); //操作
				
				sb.append("</tr>");
				
				String sql3 = "select ifnull(a.accid,b.oriaccid) accid,ifnull(a.subjectid,b.orisubjectid) subjectid," +
				" ifnull(a.AccName,b.orisubjectname) AccName,ifnull(a.subjectfullname1,b.orisubjectfullname) subjectfullname1," +
				" ifnull(a.dir,b.dir) sumdir,ifnull(a.Remain,0) Remain," +
				" ifnull(a.debitocc,0) debitocc,ifnull(a.creditocc,0) creditocc," +
				" ifnull(a.dir,b.dir) qmdir,ifnull(a.Balance,0) Balance " +
				" from ( " + sql1 + " ) a  right join  (" +
				" select accpackageid,accid,SubjectID,oriaccid,orisubjectid,orisubjectname,orisubjectfullname,if(oridirection=1,'借','贷') dir" +
				" from c_usersubject a where a.oriDataName=0 and accpackageid='"+acc+"' " +
				" and concat(a.accid,a.subjectid) <> concat(a.oriaccid, a.orisubjectid)" +
				" ) b on concat(a.accid,a.subjectid) =concat(b.oriaccid, b.orisubjectid) " +
				" where 1=1  and concat(b.accid,b.subjectid) = concat('"+rs.getString("accid")+"','"+rs.getString("SubjectID")+"') " +
				
				" HAVING  abs(Balance)>0 " + //过滤为0的科目
				
				" order by concat(b.accid,b.subjectid)";
				
				ps1 = conn.prepareStatement(sql3);
				rs1 = ps1.executeQuery();
				
				while(rs1.next()){
					String oriaccid = rs1.getString("accid");
					String orisubjectId = rs1.getString("subjectid");
					String oriSubjectFullName = rs1.getString("subjectfullname1");	
					
					String oriBalance = rs1.getString("Balance");
					String orisumdir = rs1.getString("sumdir");
					
					sb.append("<tr onmouseover=\"this.bgColor='#E4E8EF';\" onmouseout=\"this.bgColor='#F3F3F3';\" bgColor=\"#F3F3F3\" height=\"18\">");				
					sb.append("<TD  width='30px' align='right'><input checked name=\"ori"+jj+"\" id=\"ori"+jj+"\" type=\"checkbox\" value=\""+orisubjectId+"\" accid = \""+oriaccid+"\"  sumdir=\""+orisumdir+"\" Balance=\""+oriBalance+"\"></TD>");
					sb.append("<TD>");
					if("".equals(accid)){
						sb.append("【<font color=\"#0000FF\">"+orisubjectId+"</font>】" + oriSubjectFullName + " <font color=\"#0000FF\">(科目)</font>");
					}else{
						sb.append("【<font color=\"red\">"+orisubjectId+"</font>】" + oriSubjectFullName + " <font color=\"red\">(核算)</font>　<font color=\"#0000FF\">科目："+oriaccid + "</font>");
					}				
					sb.append("</TD>");
					sb.append("<TD align='center'>"+rs1.getString("sumdir")+"</TD>");
					sb.append(CHF.showMoney(rs1.getString("Remain")));
					sb.append(CHF.showMoney(rs1.getString("debitocc")));
					sb.append(CHF.showMoney(rs1.getString("creditocc")));
					sb.append("<TD align='center'>"+rs1.getString("qmdir")+"</TD>");
					sb.append(CHF.showMoney(rs1.getString("Balance")));
					
					sb.append("<TD></TD>"); 
					
					sb.append("</tr>");
				}
				
				jj++;
				
			}
			
			
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(rs1);
			DbUtil.close(ps);
			DbUtil.close(ps1);
		}
	}
	
	public String getResultTable(String acc,String projectID,int bYear,int eYear,String [] key) throws Exception{
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		PreparedStatement ps1 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		try {
			ASFuntion CHF=new ASFuntion();
			
			String ClassSubject = getClassSubject(acc, projectID, key);
			String NotSubject = getNotClassSubject(acc);
			String s1 = "".equals(ClassSubject) ? " and 1=2 " : " and a.subjectid in ("+ClassSubject+") ";
			String s2 = "".equals(NotSubject) ? " and 1=2 " : " and a.subjectid not IN("+NotSubject+") ";
			String s3 = "".equals(ClassSubject) ? " and 1=2 " : " and a.accid in ("+ClassSubject+") ";
			String s4 = " and a.AssTotalName1 not like '现金流量%' ";
	
			StringBuffer sb = new StringBuffer("");
			
			String sql1 = "select '' as accid, a.subjectid,AccName,subjectfullname1," +
					" sum(if (subyearmonth*12+submonth="+bYear+",direction*(a.DebitRemain+a.CreditRemain),0)) Remain," +
					" sum(debitocc) as debitocc,sum(creditocc) as creditocc," +
					" sum(if (subyearmonth*12+submonth="+eYear+",(direction*Balance),0)) Balance,if(a.direction=1,'借','贷') dir" +
					" from c_account a 	where subyearmonth*12+submonth>="+bYear+" and subyearmonth*12+submonth<="+eYear+" " +
					" " + s1 + s2 +
					" and isleaf1=1 group by subjectid HAVING  ABS(Remain) + abs(debitocc) + abs(creditocc) + abs(Balance)>0  " +
					" union select accid,AssItemID,AssItemName,AssTotalName1," +
					" sum(if (subyearmonth*12+submonth="+bYear+",direction*(a.DebitRemain+a.CreditRemain),0)) Remain," +
					" sum(debitocc) as debitocc,sum(creditocc) as creditocc," +
					" sum(if (subyearmonth*12+submonth="+eYear+",(direction*Balance),0)) Balance,if(a.direction=1,'借','贷') dir " +
					" from c_assitementryacc a 	" +
					" where subyearmonth*12+submonth>="+bYear+" and subyearmonth*12+submonth<="+eYear+" " +
					" " + s3 + s4 +
					" and a.isleaf1=1 group by accid,AssItemID  HAVING  ABS(Remain) + abs(debitocc) + abs(creditocc) + abs(Balance)>0 ";
			//org.util.Debug.prtOut("sql1 = "+sql1);
			
			String sql2 = "select a.*,if(substring(b.Property,2,1) =1,(debittotalocc2-credittotalocc2),(-1)*(debittotalocc2-credittotalocc2)) occ" +
					" from (SELECT SubjectID,assitemid,debittotalocc2,credittotalocc2 from z_assitemaccrectify where AccPackageID ='"+acc+"' and projectid='"+projectID+"'  " +
					" union select SubjectID,'' as assitemid,debittotalocc2,credittotalocc2  from z_accountrectify " +
					" where AccPackageID ='"+acc+"' and projectid='"+projectID+"' and (DebitTotalOcc2 <>0 OR CreditTotalOcc2 <>0) " +
					" ) a left join c_accpkgsubject b on b.AccPackageID ='"+acc+"' and a.SubjectID=b.SubjectID	" +
					" where b.AssistCode = 1 and b.isleaf=1 order by a.subjectid,assitemid"; 
			//org.util.Debug.prtOut("sql2 = "+sql2);
			
			String sql3 = "select ifnull(a.accid,b.accid) accid,ifnull(a.subjectid,b.subjectid) subjectid," +
					" ifnull(a.AccName,b.subjectName) AccName,ifnull(a.subjectfullname1,b.subjectfullname) subjectfullname1," +
					" ifnull(a.dir,b.dir) sumdir,ifnull(a.Remain,0) Remain," +
					" ifnull(a.debitocc,0) debitocc,ifnull(a.creditocc,0) creditocc," +
					" ifnull(a.dir,b.dir) qmdir,ifnull(a.Balance,0) Balance " +
					" from ( " + sql1 + " ) a  right join  (" +
					" select distinct accpackageid,accid,SubjectID,subjectName,subjectfullname,if(direction=1,'借','贷') dir from c_usersubject where oriDataName=0 and accpackageid='"+acc+"' " +
					" ) b on concat(a.accid,a.subjectid) =concat(b.accid, b.SubjectID) order by concat(b.accid,b.subjectid)";
			//org.util.Debug.prtOut("sql3 = "+sql3);
			
			sql3 = "select a.*,ifnull(debittotalocc2,0) debittotalocc2,ifnull(credittotalocc2,0) credittotalocc2,ifnull(occ,0) occ from (" +
					" select if(accid='',subjectid,accid) SubjectID1,if(accid='','',subjectid) assitemid,a.* " +
					" from (" + sql3 + ") a ) a left join ("+sql2+") b on a.SubjectID1 = b.SubjectID and  a.assitemid = b.assitemid ";
			//org.util.Debug.prtOut("sql3 =| "+sql3);
			
			ps = conn.prepareStatement(sql3);
			rs = ps.executeQuery();
			int ii = 1;
			while(rs.next()){
				String accid = rs.getString("accid");
				String subjectId = rs.getString("subjectid");
				String SubjectFullName = rs.getString("subjectfullname1");				

				String sumdir = rs.getString("sumdir");
				
				double Balance = rs.getDouble("Balance");				
				double occ = rs.getDouble("occ");
				
				double bal = Balance + occ;
				
				String ss = "";
				if("".equals(accid)){
					ss = " SubjectID='"+subjectId+"' OrderID='"+accid+"' ";
				}else{
					ss = " SubjectID='"+accid+"' OrderID='"+subjectId+"' ";
				}
				sb.append("<tr "+ss+" AccName='"+rs.getString("AccName")+"' Direction='"+sumdir+"' Occ='"+bal+"' onMouseOver=\"this.bgColor='#E4E8EF';\" onMouseOut=\"this.bgColor='#CCCCCC';\"  bgColor=\"#CCCCCC\" >");				
				sb.append("<TD colSpan=2 noWrap><IMG id='Img"+accid + subjectId+"' src='/AuditSystem/images/plus.jpg'>");
				if("".equals(accid)){
					sb.append("【<font color=\"#0000FF\">"+subjectId+"</font>】" + SubjectFullName + " <font color=\"#0000FF\">(科目)</font>");
				}else{
					sb.append("【<font color=\"red\">"+subjectId+"</font>】" + SubjectFullName + " <font color=\"red\">(核算)</font>　<font color=\"#0000FF\">科目："+accid + "</font>");
				}				
				sb.append("</TD>");
				sb.append("<TD align='center'>"+sumdir+"</TD>");
				sb.append(CHF.showMoney(rs.getString("Remain")));
				sb.append(CHF.showMoney(rs.getString("debitocc")));
				sb.append(CHF.showMoney(rs.getString("creditocc")));
				sb.append("<TD align='center'>"+rs.getString("qmdir")+"</TD>");
				sb.append(CHF.showMoney(String.valueOf(Balance)));				
				sb.append(CHF.showMoney(String.valueOf(occ)));
				sb.append(CHF.showMoney(String.valueOf(bal)));
				if(bal<0){
					sb.append("<TD><input type=\"text\" size=\"12\" class=\"checkexist-wheninputed\"  id=\"txtSubject"+ii+"\" name=\"txtSubject"+ii+"\" value=\"\" onkeydown=\"onKeyDownEvent();\" onkeyup=\"onKeyUpEvent();\" onclick=\"onPopDivClick(this);\" valuemustexist=true multilevel=true autoid=6  hideresult=true refer=newID" + ii + "  refer1=projectID valuemustbenumber=true onchange=\"onCollates("+ii+");\"  title=\"科目不能为空\"/><input type=\"checkbox\" id=\"newID" + ii + "\" name=\"newID" + ii + "\" value=\"0\" style=\"display:none\" title=\"新增科目\" onclick=\"isCheck(this);\"><span id=\"txt" + ii + "\" style=\"display:none\">新</span><input type=\"hidden\" name=\"txtOpt"+ii+"\" id=\"txtOpt"+ii+"\"></TD>"); //对方科目
				}else{
					sb.append("<TD></TD>"); //对方科目
				}
				sb.append("</tr>");
				ii ++;
				
				String sql4 = "select ifnull(a.accid,b.oriaccid) accid,ifnull(a.subjectid,b.orisubjectid) subjectid," +
				" ifnull(a.AccName,b.orisubjectname) AccName,ifnull(a.subjectfullname1,b.orisubjectfullname) subjectfullname1," +
				" ifnull(a.dir,b.dir) sumdir,ifnull(a.Remain,0) Remain," +
				" ifnull(a.debitocc,0) debitocc,ifnull(a.creditocc,0) creditocc," +
				" ifnull(a.dir,b.dir) qmdir,ifnull(a.Balance,0) Balance " +
				" from ( " + sql1 + " ) a  right join  (" +
				" select accpackageid,accid,SubjectID,oriaccid,orisubjectid,orisubjectname,orisubjectfullname,if(oridirection=1,'借','贷') dir" +
				" from c_usersubject a where oriDataName=0 and accpackageid='"+acc+"' " +
				" and concat(a.accid,a.subjectid) <> concat(a.oriaccid, a.orisubjectid)" +
				" ) b on concat(a.accid,a.subjectid) =concat(b.oriaccid, b.orisubjectid) " +
				" where 1=1  and concat(b.accid,b.subjectid) = concat('"+rs.getString("accid")+"','"+rs.getString("SubjectID")+"') " +
				
				" HAVING  abs(Balance)>0 " + //过滤为0的科目
				
				" order by concat(b.accid,b.subjectid)";
				
				sql4 = "select a.*,ifnull(debittotalocc2,0) debittotalocc2,ifnull(credittotalocc2,0) credittotalocc2,ifnull(occ,0) occ from (" +
				" select if(accid='',subjectid,accid) SubjectID1,if(accid='','',subjectid) assitemid,a.* " +
				" from (" + sql4 + ") a ) a left join ("+sql2+") b on a.SubjectID1 = b.SubjectID and  a.assitemid = b.assitemid ";
				//org.util.Debug.prtOut("sql4 =| "+sql4);
				
				ps1 = conn.prepareStatement(sql4);
				rs1 = ps1.executeQuery();
				
				
				
				while(rs1.next()){
					String oriaccid = rs1.getString("accid");
					String orisubjectId = rs1.getString("subjectid");
					String oriSubjectFullName = rs1.getString("subjectfullname1");	
					
					
					String orisumdir = rs1.getString("sumdir");
					
					double oriBalance = rs1.getDouble("Balance");
					double oriocc = rs1.getDouble("occ");
					double oribal = oriBalance + oriocc;
					
					sb.append("<tr onmouseover=\"this.bgColor='#E4E8EF';\" onmouseout=\"this.bgColor='#F3F3F3';\" bgColor=\"#F3F3F3\" height=\"18\">");				
					sb.append("<TD  width='15px' align='right'><IMG id='Img"+oriaccid + orisubjectId+"' src='/AuditSystem/images/sjx1.gif'></TD>");
					sb.append("<TD>");
					if("".equals(accid)){
						sb.append("【<font color=\"#0000FF\">"+orisubjectId+"</font>】" + oriSubjectFullName + " <font color=\"#0000FF\">(科目)</font>");
					}else{
						sb.append("【<font color=\"red\">"+orisubjectId+"</font>】" + oriSubjectFullName + " <font color=\"red\">(核算)</font>　<font color=\"#0000FF\">科目："+oriaccid + "</font>");
					}				
					sb.append("</TD>");
					sb.append("<TD align='center'>"+orisumdir+"</TD>");
					sb.append(CHF.showMoney(rs1.getString("Remain")));
					sb.append(CHF.showMoney(rs1.getString("debitocc")));
					sb.append(CHF.showMoney(rs1.getString("creditocc")));
					sb.append("<TD align='center'>"+rs1.getString("qmdir")+"</TD>");
					sb.append(CHF.showMoney(String.valueOf(oriBalance)));					
					sb.append(CHF.showMoney(String.valueOf(oriocc))); 
					sb.append(CHF.showMoney(String.valueOf(oribal)));
					sb.append("<TD></TD>");
					sb.append("</tr>");
					ii ++;
				}
				
				
				
			}
			
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(rs1);
			DbUtil.close(ps);
			DbUtil.close(ps1);
		}
	}
	
	public String saveSubject(String acc,String projectID,String FillUser,ClassifiCation classifiCation) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSet rs1 = null;		
		try {
			String result = "";
			String opt = "";
			
//			String sql = "";
			String SDate = "";
			if("0".equals(classifiCation.getVdate())){
				SDate = acc.substring(6)+"-01-01";
			}else{
				SDate = acc.substring(6)+"-12-31";
			}
			String [] sqlSubject = classifiCation.getStrSubject().split("\\|");
			String [] sqlAssitem = classifiCation.getStrAssitem().split("\\|");
			String [] sqlDirection = classifiCation.getStrDirection().split("\\|");
			String [] sqlBalance = classifiCation.getStrBalance().split("\\|");
						
//			String sqlName = classifiCation.getStrName();
			
			double sum = 0.00;
			String str = "";
			
			for(int i=1; i<sqlSubject.length;i++){
				if("".equals(sqlAssitem[i].trim())){
					int sub = isRestrict(acc,projectID,sqlSubject[i]);
					if(sub == 1){
						result += "注意：科目【"+sqlSubject[i]+"】已存在下级科目，不能对此科目进行对冲重分类！\\n";
						opt = "1";
					}
				}
				
				sum +=  Integer.parseInt(sqlDirection[i]) * Double.parseDouble(sqlBalance[i]);
//				sum +=  Math.abs(Double.parseDouble(sqlBalance[i]));
				if(!"".equals(sqlAssitem[i].trim())){
					str += sqlSubject[i] + "（科目："+sqlAssitem[i]+"）,";
				}else{
					str += sqlSubject[i] + ",";
				}
			}
			
//			sum = sum - Math.abs(Double.parseDouble(sqlBalance[1]));
			sum = sum- Integer.parseInt(sqlDirection[1]) * Double.parseDouble(sqlBalance[1]);
			if("1".equals(opt)){
				return result;
			}
			
			VoucherTable vt = new VoucherTable();
			SubjectEntryTable set = new SubjectEntryTable();
			RectifyTable rt = new RectifyTable();
			
			vt.setAccpackageid(acc);
			vt.setFilluser(FillUser);
			vt.setProperty(classifiCation.getProperty());
			vt.setProjectID(projectID);//
			vt.setVchdate(SDate);
			vt.setTypeid("调");
			vt.setAudituser("");
		    vt.setKeepuser("");
		    vt.setDirector("");
		    vt.setAffixcount(1);
		    vt.setDoubtuserid("");

			set.setAccpackageid(acc);
			set.setVchdate(SDate);
			set.setProperty(classifiCation.getProperty());
			set.setProjectID(projectID);//
			set.setTypeid("调");
			set.setCurrrate(0);
		    set.setCurrvalue(0);
		    set.setCurrency("");
		    set.setQuantity(0);
		    set.setUnitprice(0);
		    set.setBankid("");

			rt.setAccpackageId(acc);
			rt.setProjectID(projectID);//
			
			
			String VKeyId = "";			
			for (int i = 1; i < sqlSubject.length; i++) {
								
				if(i==1){ //增加调整表只录一条
					VKeyId = new DELAutocode().getAutoCode("AUVO","");
					vt.setAutoid(Integer.parseInt(VKeyId));
					String vid = new DELAutocode().getAutoCode("VOID","");
					vt.setVoucherid(Integer.parseInt(vid));
					vt.setDescription(""); //备注
					
					AddOrModifyVoucher(vt,"ad"); //增加调整表				
				}
				
				String SUId = new DELAutocode().getAutoCode("SUAU","");
				set.setAutoid(Integer.parseInt(SUId));
				set.setVoucherid(Integer.parseInt(VKeyId));
				set.setSerail(i);
				
				if(i==1){
//					set.setDirction(Integer.parseInt(sqlDirection[i]));
					
					if(sum>=0){
						set.setDirction(1);
					}else{						
						set.setDirction(-1);
					}
					set.setOccurvalue(Math.abs(sum));
					if(!"".equals(sqlAssitem[i].trim())){
						set.setSummary("对冲重分类：汇总核算为"+str.substring(0,str.length()-1));//摘要
					}else{
						set.setSummary("对冲重分类：汇总科目为"+str.substring(0,str.length()-1));//摘要
					}
				}else{
//					set.setDirction(Integer.parseInt(sqlDirection[i]));
					if(Double.parseDouble(sqlBalance[i])>=0){
						set.setDirction((-1)*Integer.parseInt(sqlDirection[i]));
					}else{
						set.setDirction(Integer.parseInt(sqlDirection[i]));
					}					
					set.setOccurvalue(Math.abs(Double.parseDouble(sqlBalance[i])));
					if(!"".equals(sqlAssitem[i].trim())){
						set.setSummary("对冲重分类：合并到【"+sqlSubject[1]+"】"+classifiCation.getStrName() + "（核算） 科目："+sqlAssitem[1]);//摘要
					}else{
						set.setSummary("对冲重分类：合并到【"+sqlSubject[1]+"】"+classifiCation.getStrName() + "（科目）");//摘要	
					}
					
				}
				
				result += getSCurrency(acc,sqlSubject[i]);
				String str1 = getSCurrency(acc,sqlSubject[i]);
				if(result.indexOf(str1) == -1){
					result += str1;
				}
				
				if(!"".equals(sqlAssitem[i].trim())){
					set.setSubjectid(sqlAssitem[i]);
				}else{
					set.setSubjectid(sqlSubject[i]);	
				}
				
					
			
				AddOrModifySubjectEntry(set,"ad"); //增加调整分录表
				
				if(!"".equals(sqlAssitem[i].trim())){
					rt.setEntryId(SUId);
					rt.setAssitemId(sqlSubject[i]);
					rt.setSubjectId(sqlAssitem[i]);
					AddRectify(rt,"ad");
				}
				
			}
			
			if("".equals(result)){
				result = "保存成功！";
			}
			createTzhz(acc,projectID);
			createWbTzhz(acc, projectID);
			createAssitem(acc,projectID);//增加核算金额
			
			return result;			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(rs1);
			DbUtil.close(ps);
		}		
	}
	
	public String  getProjectEndYear(String projectID) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			 String sql = "select substring(audittimeend,1,4) from asdb.z_project where projectid='"+projectID+"' ";
			 ps = conn.prepareStatement(sql);
			 rs = ps.executeQuery();
			 
			 String year = "";
			 
			 if(rs.next()){
				 year = rs.getString(1); 
			 }
			 return year;
			 
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	
	/**
	 * 损益结转校验	生成账表不符调整
	 */
	
	public void getCarrySave(String acc,String projectID,String isNew,String user ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		try {
			String string = " and subjectid like '5%' ";
			if("1".equals(isNew)){
				string = " and subjectid like '6%' ";
			}
			
			String SDate = String.valueOf(Integer.parseInt(acc.substring(6))-1) + "-12-31"; 
			
			VoucherTable vt = new VoucherTable();
			SubjectEntryTable set = new SubjectEntryTable();
			RectifyTable rt = new RectifyTable();
			
			vt.setAccpackageid(acc);
			vt.setFilluser(user);
			vt.setProperty("511");
			vt.setProjectID(projectID);//
			vt.setVchdate(SDate);
			vt.setTypeid("调");
			vt.setAudituser("");
		    vt.setKeepuser("");
		    vt.setDirector("");
		    vt.setAffixcount(1);
		    vt.setDoubtuserid("");

		    String VKeyId = new DELAutocode().getAutoCode("AUVO","");
			vt.setAutoid(Integer.parseInt(VKeyId));
			
			String vid = new DELAutocode().getAutoCode("VOID","");
			vt.setVoucherid(Integer.parseInt(vid));
			vt.setDescription(""); //备注
			
			AddOrModifyVoucher(vt,"ad"); //增加调整表
			
			set.setAccpackageid(acc);
			set.setVchdate(SDate);
			set.setProperty("511");
			set.setProjectID(projectID);//
			set.setTypeid("调");
			set.setCurrrate(0);
		    set.setCurrvalue(0);
		    set.setCurrency("");
		    set.setQuantity(0);
		    set.setUnitprice(0);
		    set.setBankid("");

			rt.setAccpackageId(acc);
			rt.setProjectID(projectID);//
			
			String sql = "select subjectid,accname, direction,Balance from c_account where accpackageid='"+acc+"' "+string+" and SubMonth='12' and isleaf1=1 and Balance<>0 ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			int ii = 0;
			
			double ss = 0.00;	//差
			double s1 = 0.00;	//借
			double s2 = 0.00;	//贷
			while(rs.next()){
				String SUId = new DELAutocode().getAutoCode("SUAU","");
				set.setAutoid(Integer.parseInt(SUId));
				set.setVoucherid(Integer.parseInt(VKeyId));
				set.setSerail(ii+1);
				
				String subjectid = rs.getString("subjectid");
				String accname = rs.getString("accname");
				String direction = rs.getString("direction");
				double Balance = rs.getDouble("Balance"); 
				
				set.setOccurvalue(Balance);
				set.setSubjectid(subjectid);
				set.setSummary("账表不符调整:"+subjectid+","+accname);//摘要
				
				if("-1".equals(direction)){
					set.setDirction(1);	
					s1 += Balance;
				}else{
					set.setDirction(-1);
					s2 += Balance;
				}
				
				AddOrModifySubjectEntry(set,"ad"); //增加调整分录表
				
				ii ++;
				
				sql = "select accid,assitemid,AssItemName from (" +
					" select * ,0 opt" +
					" from c_assitementryacc where accpackageid='"+acc+"' and accid='"+subjectid+"'" +
					" and submonth=12 and isleaf1=1" +
					" and (asstotalname1 like '%客户%' or asstotalname1 like '%供应商%' or asstotalname1 like '%关联%' or asstotalname1 like '%往来%' )" +
					" union " +
					" select * ,1 opt" +
					" from c_assitementryacc where accpackageid='"+acc+"' and accid='"+subjectid+"'" +
					" and submonth=12 and isleaf1=1" +
					" and not (asstotalname1 like '%客户%' or asstotalname1 like '%供应商%' or asstotalname1 like '%关联%' or asstotalname1 like '%往来%' )" +
					" ) a order by opt,accid,assitemid limit 1 ";
				ps = conn.prepareStatement(sql);
				rs1 = ps.executeQuery();
				if(rs1.next()){
					rt.setEntryId(SUId);	
					rt.setAssitemId(rs1.getString("assitemid"));
					rt.setSubjectId(subjectid);
					AddRectify(rt,"ad");	
				}
				rs1.close();	
				
			}
			rs.close();
			
			//org.util.Debug.prtOut("ss:="+ss + " s1:="+s1 + " s2:="+s2);
			
			if(ii != 0){
				sql = "select subjectid,subjectname,case substring(property,2,1) when  '2' then -1 else 1 end direction from c_accpkgsubject where SubjectFullName like '本年利润%' and isleaf=1 and accpackageid='"+acc+"' order by subjectid limit 1";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				if(rs.next()){
					
					String SUId = new DELAutocode().getAutoCode("SUAU","");
					set.setAutoid(Integer.parseInt(SUId));
					set.setVoucherid(Integer.parseInt(VKeyId));
					set.setSerail(ii+1);
					
					String subjectid = rs.getString("subjectid");
					String accname = rs.getString("subjectname");
					String direction = rs.getString("direction");
					
					set.setOccurvalue(ss);
					set.setSubjectid(subjectid);
					set.setSummary("账表不符调整:"+subjectid+","+accname);//摘要
					
					if(s1 > s2){
						ss = s1 - s2 ;
						set.setOccurvalue(ss);
						set.setDirction(-1);
					}else{
						ss = s2 - s1 ;
						set.setOccurvalue(ss);
						set.setDirction(1);
					}
					
					AddOrModifySubjectEntry(set,"ad"); //增加调整分录表
					
				}
			}
			
			createTzhz(acc,projectID);
			createWbTzhz(acc, projectID);
			createAssitem(acc,projectID);//增加核算金额
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs1);
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
	}
	
	/**
	 * 试算平衡表
	 * @param acc
	 * @param projectID
	 * @param strStartYearMonth
	 * @param strEndYearMonth
	 * @param tempTable
	 * @param SubjectID
	 * @param tokenid
	 * @param level1
	 * @param trid
	 * @return
	 * @throws Exception
	 */
	public String getTrialSubject(String acc, String projectID,String projectEndYear,
			String strStartYearMonth, String strEndYearMonth, String tempTable,
			String SubjectID,String tokenid,String level1, String trid,String year) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ASFuntion CHF=new ASFuntion();
			StringBuffer sb = new StringBuffer("");
			
			String sql = " select distinct subjectfullname2 " +
			" from c_account a " +
			" where 1=1" +
			" and a.SubYearMonth*12 + a.SubMonth >= "+strStartYearMonth+" \n" +
			" and a.SubYearMonth*12 + a.SubMonth <= "+strEndYearMonth+" \n" +
			" and tokenid ='"+tokenid+"' " ;
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			String subjectfullname2 = "";
			if(rs.next()){
				subjectfullname2 = rs.getString("subjectfullname2");
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			if("".equals(subjectfullname2)) return "";
			
			ASTextKey tkey = new ASTextKey(conn);
			String ss = tkey.getACurrRate(acc);
			
			int zYear = Integer.parseInt(projectEndYear) + Integer.parseInt(year);
			String table = "z_accountrectify";
			String where = "";
			if(!String.valueOf(zYear).equals(projectEndYear)){
				table = "z_accountyearrectify";
				where = " and yearrectify = " + zYear ;
			}
			
			/**
			 * 余额表
			 */
			sql = " select SubjectID,'' as AssItemID,AccName,subjectfullname2 as subjectfullname,tokenid,direction2 ," +
			" case a.dataname when '0' then '"+ ss+"' else a.dataname end DataName ," +
			" case a.direction2 when 1 then '借' when -1 then '贷' else '双向' end direction," + 
			" a.direction2 * sum(IF(a.SubYearMonth*12 + a.SubMonth='"+strStartYearMonth+"',(a.DebitRemain+a.CreditRemain),0)) qcremain," + 
			" a.direction2 * sum(IF(a.SubYearMonth*12 + a.SubMonth='"+strEndYearMonth+"',a.Balance,0)) qmremain," +
			" sum(IF(a.SubYearMonth*12 + a.SubMonth='"+strStartYearMonth+"',(a.DebitRemain+a.CreditRemain),0)) qcremain1," + 
			" sum(IF(a.SubYearMonth*12 + a.SubMonth='"+strEndYearMonth+"',a.Balance,0)) qmremain1,isleaf1,level1 " +

			" from c_account a " +
			" where 1=1" +
			" and a.SubYearMonth*12 + a.SubMonth >= "+strStartYearMonth+" \n" +
			" and a.SubYearMonth*12 + a.SubMonth <= "+strEndYearMonth+" \n" +
			" and level1 = " + String.valueOf(Integer.parseInt(level1) + 1) +
			" and subjectfullname2 like '"+subjectfullname2+"/%'" +
			" group by a.SubjectID \n"  ;
			
			sql = "insert into " + tempTable + " (SubjectID,AssItemID,AccName,subjectfullname,tokenid,direction2 ,DataName,direction,qcremain,qmremain,qcremain1,qmremain1,isleaf1,level1) " + sql;
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			/**
			 * 调整情况
			 */
			sql = " update "+tempTable+" a,"+table+" b " +
				" set TotalOcc456 = direction2 * (DebitTotalOcc4 + DebitTotalOcc5 + DebitTotalOcc6 - CreditTotalOcc4 - CreditTotalOcc5 - CreditTotalOcc6 )," +
				" TotalOcc4 = direction2 * (DebitTotalOcc4 - CreditTotalOcc4 ), " +
				" TotalOcc5 = direction2 * (DebitTotalOcc5 - CreditTotalOcc5 ), " +
				" TotalOcc1 = direction2 * (DebitTotalOcc1 - CreditTotalOcc1 ), " +
				" TotalOcc2 = direction2 * (DebitTotalOcc2 - CreditTotalOcc2 ), " +
				" TotalOcc30 = direction2 * (DebitTotalOcc3 + DebitTotalOcc0 - CreditTotalOcc3 - CreditTotalOcc0)," +
				
				" TotalOcc4561 =  (DebitTotalOcc4 + DebitTotalOcc5 + DebitTotalOcc6 - CreditTotalOcc4 - CreditTotalOcc5 - CreditTotalOcc6 )," +
				" TotalOcc41 =  (DebitTotalOcc4 - CreditTotalOcc4 ), " +
				" TotalOcc51 =  (DebitTotalOcc5 - CreditTotalOcc5 ), " +
				" TotalOcc11 =  (DebitTotalOcc1 - CreditTotalOcc1 ), " +
				" TotalOcc21 =  (DebitTotalOcc2 - CreditTotalOcc2 ), " +
				" TotalOcc301 =  (DebitTotalOcc3 + DebitTotalOcc0 - CreditTotalOcc3 - CreditTotalOcc0)" +
				" where b.projectid = "+projectID+" " +
				" and a.DataName='" +ss+"' " + where + 
				
				" and a.SubjectID like '" +SubjectID+"%' " +
				
				" and a.SubjectID = b.SubjectID ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			/**
			 * 显示DataGrid
			 */
			
			sql = "select *," +
			" qcremain + TotalOcc4 + TotalOcc5 as sdqcremain,qmremain + TotalOcc1 + TotalOcc2 as sdqmremain, " +
			" qcremain1 + TotalOcc41 + TotalOcc51 as sdqcremain1,qmremain1 + TotalOcc11 + TotalOcc21 as sdqmremain1 " +
			" from " +tempTable + " a where 1=1  and a.SubjectID like '" +SubjectID+"%'  and level1 = " + String.valueOf(Integer.parseInt(level1) + 1) ;
			
			sql = "select SubjectID,AccName,direction,qcremain1,TotalOcc41,TotalOcc51,sdqcremain1,qmremain1,TotalOcc11,TotalOcc21,sdqmremain1,isleaf1,level1,1 as opt,tokenid,subjectfullname " +
			" from (" + 
			sql +
			" ) a " ;
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				
				String rstokenid = rs.getString("tokenid");
				String rsopt = rs.getString("opt");
				String rsisleaf1 = rs.getString( "isleaf1");
				String rslevel1 = rs.getString("level1");
				
				String subjectfullname = rs.getString("subjectfullname");
				
				
				String rsSubjectID = rs.getString("SubjectID");
				String rsAccName = rs.getString( "AccName");
				String rsdirection = rs.getString("direction");
				
				String rsqcremain = rs.getString( "qcremain1");
				String rsTotalOcc4 = rs.getString( "TotalOcc41");
				String rsTotalOcc5 = rs.getString( "TotalOcc51");
				String rssdqcremain = rs.getString( "sdqcremain1");
				
				String rsqmremain = rs.getString("qmremain1");
				String rsTotalOcc1 = rs.getString( "TotalOcc11");
				String rsTotalOcc2 = rs.getString( "TotalOcc21");
				String rssdqmremain = rs.getString( "sdqmremain1");
				if("1".equals(rsisleaf1)){
					sb.append("<tr id='"+trid+"' bgColor='#b2c2d2' height='18' SubjectFullName = '"+subjectfullname+"' SubjectID = '"+rsSubjectID+"' tokenid = '"+rstokenid+"' style='cursor:hand;' onClick='goClickTR();' onDBLclick='goSort();' opt = "+rsopt+" isleaf1 = '"+rsisleaf1+"' level1='"+rslevel1+"' >");
				}else{
					sb.append("<tr id='"+trid+"' bgColor='#CCCCCC' height='18' SubjectFullName = '"+subjectfullname+"' SubjectID = '"+rsSubjectID+"' tokenid = '"+rstokenid+"' style='cursor:hand;' onClick='goClickTR();' onDBLclick='goSort();' opt = "+rsopt+" isleaf1 = '"+rsisleaf1+"' level1='"+rslevel1+"' >");
				}
				sb.append("<td noWrap>"+rsSubjectID+"</td>");
			    sb.append("<td noWrap>"+rsAccName+"</td>");
			    sb.append(CHF.dealData("showCenter",rsdirection,""));
//			    sb.append("<td noWrap>"+rsdirection+"</td>");
			    
//			    sb.append(CHF.showMoney1(rsqcremain));
//			    sb.append(CHF.showMoney1(rsTotalOcc4));
//			    sb.append(CHF.showMoney1(rsTotalOcc5));
			    sb.append(CHF.showMoney1(rssdqcremain));

			    sb.append(CHF.showMoney1(rsqmremain));
			    sb.append(CHF.showMoney1(rsTotalOcc1));
			    sb.append(CHF.showMoney1(rsTotalOcc2));
			    sb.append(CHF.showMoney1(rssdqmremain));
			    
			    sb.append("</tr>");
				
			}
			
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
	}
	
	
	/**
	 * 新版多科目挂帐
	 * @param acc
	 * @param projectID
	 * @param eYear
	 * @param SubjectID
	 * @param OrderID
	 * @return
	 * @throws Exception
	 */
	public String getNUserSubject(String acc,String projectID,int eYear,String SubjectID,String OrderID,String vdate,String tt)throws Exception{
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			
			String strBalance = "b.balance ";
			String strOcc = "DebitTotalOcc2 as DebitTotalOcc,CreditTotalOcc2 as CreditTotalOcc ";
			if("0".equals(vdate)){
				strBalance = "(b.DebitRemain + b.CreditRemain ) as balance ";
				strOcc = "DebitTotalOcc5 as DebitTotalOcc,CreditTotalOcc5 as CreditTotalOcc ";
			}
			
			ASFuntion CHF=new ASFuntion();
			StringBuffer sb = new StringBuffer("");
			String sql1 = "\n select a.accid,a.subjectid,a.oriparentsubjectid,a.oriparentdirection,a.oriaccid,a.orisubjectid,a.orisubjectname,a.orisubjectfullname, " +strBalance +
					"\n from(" +
					"\n 	select distinct a.accid,a.subjectid,a.SubjectName,a.subjectfullname,a.oriparentsubjectid,a.oriparentdirection,a.oriaccid,a.orisubjectid,a.orisubjectname,a.orisubjectfullname " +
					"\n 	from  c_usersubject a " +
					"\n 	where a.oriDataName=0 and oriaccid ='' and accpackageid='"+acc+"' " +
					"\n )a inner join c_account b " +
					"\n on  b.subyearmonth*12+b.submonth="+eYear+" and a.orisubjectid=b.subjectid " +
					"\n union " +
					"\n select a.accid,a.subjectid,a.oriparentsubjectid,a.oriparentdirection,a.oriaccid,a.orisubjectid,a.orisubjectname,a.orisubjectfullname, " + strBalance + 
					"\n from(" +
					"\n 	select distinct a.accid,a.subjectid,a.SubjectName,a.subjectfullname,a.oriparentsubjectid,a.oriparentdirection,a.oriaccid,a.orisubjectid,a.orisubjectname,a.orisubjectfullname " +
					"\n 	from  c_usersubject a where a.oriDataName=0 and oriaccid >'' and accpackageid='"+acc+"' " +
					"\n )a inner join c_assitementryacc b " +
					"\n on  b.subyearmonth*12+b.submonth="+eYear+" and a.oriaccid=b.accid and a.orisubjectid=b.assitemid";
			
			String s1 = "";
			String s2 = "";
			if("".equals(OrderID)){
				s1 = "";
				s2 = SubjectID;
			}else{
				s1 = SubjectID;
				s2 = OrderID;
			}
			String sql = "\n select if(b.oriaccid='',b.orisubjectid,b.oriaccid) SubjectID,if(b.oriaccid='','',b.orisubjectid) assitemid, " +
					"\n  b.orisubjectname,b.orisubjectfullname,if(b.oriparentdirection=1,'借','贷') subDir,b.balance,b.oriparentdirection" +
					"\n  from ("+sql1 + ") a ,("+sql1 + ") b where a.oriaccid = '"+s1+"' and a.orisubjectid = '"+s2+"'" +
					"\n  and a.accid = b.accid and a.subjectid = b.subjectid and a.oriparentsubjectid = b.oriparentsubjectid order by b.oriaccid,b.orisubjectid";
			
			sql = "\n select a.SubjectID,a.AssItemID,orisubjectname,orisubjectfullname,subDir,oriparentdirection*(balance) qmremain," +
				"\n  case a.oriparentdirection when 1 then ifnull(DebitTotalOcc,0) when -1 then ifnull(CreditTotalOcc,0) end occ," +
				"\n  case a.oriparentdirection when 1 then oriparentdirection*(balance)+ifnull(DebitTotalOcc,0) when -1 then oriparentdirection*(balance)+ifnull(CreditTotalOcc,0) end bal" +
				"\n  from ( "+sql+") a left join ( " +
				"\n  select SubjectID,'' AssItemID,"+strOcc+" from z_accountrectify where ProjectID='"+projectID+"' union select SubjectID,AssItemID,"+strOcc+" from z_assitemaccrectify where ProjectID='"+projectID+"' " +
				"\n  ) b on a.SubjectID=b.SubjectID and a.AssItemID=b.AssItemID";
			System.out.println(sql);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				
				String sID = rs.getString("subjectID"); 
				String aID = rs.getString("assitemid"); 
				String name = rs.getString("orisubjectname"); 
				String fullname = rs.getString("orisubjectfullname"); 
				String direction = rs.getString("subDir") ;
				String balance = rs.getString("qmremain") ;
				String rectify = rs.getString("occ") ;
				String sdbalance = rs.getString("bal");
				
				sb.append("<tr id='"+tt+"' bgColor='#b2c2d2' height='18'>");
				sb.append("<td></td>");
				sb.append("<td></td>");
				sb.append("<td noWrap>"+sID+"</td>");
			    sb.append("<td noWrap>"+aID+"</td>");
			    sb.append("<td noWrap>"+name+"</td>");
			    sb.append("<td noWrap>"+fullname+"</td>");
			    
			    sb.append("<td style='TEXT-ALIGN: center'>"+direction+"</td>");
			    
			    sb.append(CHF.showMoney(balance));
			    sb.append("<td></td>");
			    sb.append("<td></td>");
			    sb.append("<td></td>");
			    sb.append(CHF.showMoney(rectify));
			    sb.append(CHF.showMoney(sdbalance));
			    
//			    sb.append("<td></td>");
			    sb.append("</tr>");
				
			}
			
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);			
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 得到当前的SubjectID 一级有没有核算
	 * 
	 * 显示核算（调整不准新增调整类型）
	 * 条件：对方科目是一级且有多个核算类型
	 * 情况：
	 * 1、调整科目没有核算	要显示
	 * 2、调整科目有对方科目没有的核算类型		要显示
	 * 3、调整科目的核算类型和对方科目一样		不用显示
	 * @param acc
	 * @param SubjectID
	 * @return
	 * @throws Exception
	 */
	public String ifAssitem(String acc,String SubjectID)throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select * from c_accpkgsubject where AccPackageID = ? and SubjectID = ? and level0 =1";
			ps = conn.prepareStatement(sql);
			ps.setString(1, acc);
			ps.setString(2, SubjectID);
			rs = ps.executeQuery();
			if(!rs.next()){
				return "0";
			}
			DbUtil.close(rs);			
			DbUtil.close(ps);
			
			sql = "select * from c_assitementryacc where AccPackageID = ? and AccID = ? and level1 = 1 and submonth = 1";
			ps = conn.prepareStatement(sql);
			ps.setString(1, acc);
			ps.setString(2, SubjectID);
			rs = ps.executeQuery();
			int opt = 0;
			String fullname = "";
			while(rs.next()){
				opt ++  ;
				fullname += "`" + rs.getString("AssTotalName1");
			}
			if(opt > 1 ) {
				return "1" + fullname;
			}
			return "0";
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);			
			DbUtil.close(ps);
		}
		
	}
	
	/**
	 * 把显示的保存的strDataGrid的临时表内
	 */
	public void create(String tablename,String strSql)throws Exception{
		PreparedStatement ps = null;
		try {
			
			String sql = "";
			
			DisposeTableService dts = new DisposeTableService(conn);
			if(!dts.checkTableExist(tablename)){	//判断tablename　是否存在
				sql  = "CREATE TABLE "
					+ tablename
					+ " ("
					+ " id int(10) NOT NULL auto_increment,"
					+ " optif int(1) default 1,"					//是否要调整，1为要，0为不用
					+ " opt int(1) default 0,"						
					+ " subjectid varchar(50) default NULL,"		//调整科目
					+ " assitemid varchar(100) default NULL,"		//调整核算
					+ " name varchar(100) default NULL,"			//名称
					+ " fullname varchar(500) default NULL,"		//全路径
					+ " direction varchar(10) default NULL,"		//方向“借”、“贷”
					+ " rectifyocc varchar(30) default NULL,"		//调整金额　
					+ " totalocc varchar(30) default NULL,"			//实际调整金额，为空就表示用默认的调整金额
					+ " talocc varchar(30) default NULL,"			//已调整金额
					+ " occ varchar(30) default NULL,"				//调整金额　为空或为负值就要调整，为正或为0就不用调整
					+ " pid varchar(50) default NULL,"				
					+ " s2 varchar(100) default NULL,"
					+ " sid varchar(150) default NULL,"				//多科目挂帐的组长列
					
					+ " osubjects varchar(50) default NULL,"	//预设的科目
					+ " oAssitems varchar(50) default NULL,"	//预设的核算类型　
					
					+ " oif varchar(50) default NULL,"			//是否要新增科目或核算	1为要但不用生成编号，2为要且要生成编号，0不用
					+ " osubjectid varchar(50) default NULL,"	//对方科目
					+ " oassitemid varchar(100) default NULL,"	//对方核算
					
					
					+ " PRIMARY KEY  (id)"
					+ ") ENGINE=MyISAM DEFAULT CHARSET=gbk";
				
				ps = conn.prepareStatement(sql);
				ps.execute();
				DbUtil.close(ps);
			}

			sql = "select a.opt,a.SubjectID,a.orderid,a.AccName,a.SubjectFullName1, a.subDir,a.rectifyocc,a.talocc,a.occ,a.pid,a.s2,a.sid from (" +
			strSql + 
			") a left join  " + tablename + " b " +
			" on a.SubjectID = b.SubjectID and a.orderid = b.assitemid " +
			" where b.subjectid is null";
			
			sql = "insert into " + tablename + " (opt,subjectid,assitemid,name,fullname,direction,rectifyocc,talocc,occ,pid,s2,sid) " + sql ;
			ps = conn.prepareStatement(sql);
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 修改调整明细临时表的值
	 * @param tablename
	 * @param subjectid
	 * @param assitemid
	 * @param opt
	 * @param totalocc
	 * @throws Exception
	 */
	public void udpate(String tablename,String subjectid,String assitemid,String opt,String totalocc,String txtSubject)throws Exception{
		PreparedStatement ps = null;
		try {
			int i = 1;
			String sql = "";
			sql = "update " + tablename + " set optif = ? ,totalocc = ?,osubjects = ?  where subjectid = ? and assitemid = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(i++, opt);
			ps.setString(i++, totalocc);
			ps.setString(i++, txtSubject);
			
			ps.setString(i++, subjectid);
			ps.setString(i++, assitemid);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
		}
	}
	/**
	 * 新版负值重分类的保存
	 */
	public String saveClass(String acc, String projectID, String tablename,
			String[] rSubjects, String[] oSubjects,String[] oAssitems,String user, String vdate,
			String property) throws Exception {
		PreparedStatement ps = null;
		PreparedStatement ps1 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		String sql = "";
		try {
			
			ASFuntion CHF=new ASFuntion();
			
			boolean bool = false;
			sql = "select 1 from s_config where sname = '调整的账龄按自定义区间输出' and svalue = '是'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				bool = true;
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			FrontProcessAction.printMessage("classificationID","开始检查负值重分类的合理性...") ;
			
//			sql = "update " + tablename + " set oSubjects = ?,oAssitems = if(assitemid='',?,'') where subjectid like concat(?,'%') ";
			sql = "update " + tablename + " set oSubjects = ?,oAssitems = ? where ifnull(oSubjects,'') = '' and subjectid like concat(?,'%') ";
			ps = conn.prepareStatement(sql);
			for(int i = 0;i<rSubjects.length; i++){
				if(rSubjects[i] != null 
					&& oSubjects[i] != null 
					&& !"".equals(rSubjects[i].trim()) 
					&& !"".equals(oSubjects[i].trim())){
					
//					当科目编号有“标准”，就表示要新增一个标准科目
					if(oSubjects[i].trim().indexOf("标准")>-1){
						
						sql = "select *,if(a.property =2, -1,a.property ) as direction from k_standsubject a " +
						" where a.VocationID = (select VocationID from asdb.k_customer where departid=substring(?,1,6)) " +
						" and level0=1 " +
						" and concat(a.subjectid ,'标准') = ? ";
						ps1 = conn.prepareStatement(sql);
						ps1.setString(1, acc);
						ps1.setString(2, oSubjects[i].trim());
						rs = ps1.executeQuery();
						while(rs.next()){
							String subjectname = rs.getString("subjectname");
							int direction = rs.getInt("direction");
							
							String newSubjectid = insertData( acc,  projectID,  subjectname, "", direction);
							
							oSubjects[i] = CHF.replaceStr(oSubjects[i].trim(), "标准", "已增");
							
							sql = "update z_usesubject set subjectid = ? ,tipsubjectid = ? where projectid = ? and subjectid = ? ";
							ps1 = conn.prepareStatement(sql);
							ps1.setString(1, oSubjects[i].trim());
							ps1.setString(2, oSubjects[i].trim());
							ps1.setString(3, projectID);
							ps1.setString(4, newSubjectid);
							ps1.execute();
							
						}
						DbUtil.close(rs);
						DbUtil.close(ps1);
					}
					
					ps.setString(1, oSubjects[i]);
					ps.setString(2, oAssitems[i]);
					ps.setString(3, rSubjects[i]);
					ps.addBatch();
					
				}
			}
			ps.executeBatch();
			DbUtil.close(ps);
			
			/**
			 * 删除所以没有调整科目的记录
			 */
			sql = "delete from " + tablename + " where ifnull(oSubjects,'') = '' ";
			ps = conn.prepareStatement(sql); 
			ps.execute();
			DbUtil.close(ps);
			
			FrontProcessAction.printMessage("classificationID","正在删除设置对方科目的记录...") ;

			/**
			 * 删除所有occ大于0分录数
			 */
			sql = "delete from " + tablename + " where optif = 0 ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			/**
			 * 删除所有occ大于0分录数
			 */
			sql = "delete from " + tablename + " where occ >= 0 ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			FrontProcessAction.printMessage("classificationID","正在删除负值重分类差值大于0的记录...") ;
			
			
			/**
			 * 调整时，同一组内随机取一个 oif = 0,
			 */
			sql = "update c_usersubject a," + tablename + " b " +
			" set b.oif = 0," +
			" b.osubjectid = if(oriaccid='',a.orisubjectid ,a.oriaccid)," +
			" b.oassitemid = if(oriaccid='','' ,a.orisubjectid) " +
			" where AccPackageId = ? " +
			" and ifnull(b.osubjectid,'') = '' " +
			" and concat(a.accid ,'`',a.subjectid) = b.sid " +
			" and if(oriaccid='',a.orisubjectid ,a.oriaccid) like concat(osubjects,'%')";   
			ps = conn.prepareStatement(sql);
			ps.setString(1, acc);			
			ps.execute();
			DbUtil.close(ps);
			
			/**
			 * 调整，调整名称与对方科目名称一样的科目 oif = 0,
			 */
			sql = "update ( " +
			"	select subjectid,subjectname,subjectfullname from c_accpkgsubject where AccPackageID =? and isleaf = 1 " +
			"	union" +
			" 	select subjectid,subjectname,subjectfullname from z_usesubject where projectid =? and isleaf = 1 " +
			" ) a ," + tablename + " b " +
			" set b.oif = 0," +
			" b.osubjectid = a.subjectid," +
			" b.oassitemid = '' " +
			" where ifnull(b.osubjectid,'') = '' " +
			" and a.subjectid like concat(osubjects,'%') " +
			" and a.subjectname = b.name";
			ps = conn.prepareStatement(sql);
			ps.setString(1, acc);
			ps.setString(2, projectID);
			ps.execute();
			DbUtil.close(ps);
			
			/**
			 * 调整，调整名称与对方核算名称一样的核算 oif = 0
			 */
			sql =  "update (" +
			"	select accid,assitemid,assitemname,AssTotalName1 from c_assitementryacc where AccPackageID = ? and submonth =1 and isleaf1 = 1" +
			" ) a ," + tablename + " b " +
			" set b.oif = 0," +
			" b.osubjectid = a.accid," +
			" b.oassitemid = a.assitemid" +
			" where ifnull(b.osubjectid,'') = ''" +
			" and a.accid like concat(b.osubjects,'%')" +
			" and a.AssItemName = b.name";
			ps = conn.prepareStatement(sql);
			ps.setString(1, acc);
			ps.execute();
			DbUtil.close(ps);
			
			/**
			 * 对冲：调整科目与对方科目进入对冲，金额少了不用调整
			 */
			sql = "UPDATE " + tablename + " a,(	" +
			"		SELECT a.NAME,MAX(a.id) AS id	" +
			"		FROM 	" + tablename + " a ," + tablename + " b 	" +
			"		WHERE a.oif = 0 AND b.oif = 0	" +
			"		AND a.osubjectid = b.subjectid	AND a.oassitemid = b.assitemid	" +
			"		AND a.subjectid = b.osubjectid	AND a.assitemid = b.oassitemid	" +
			"		AND ABS(a.occ) =  ABS(b.occ) 	" +
			"		GROUP BY a.name" +
			"	) b SET a.occ = 0 " +
			"	WHERE a.id = b.id";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps); 
				
			sql = "UPDATE " + tablename + " a ," + tablename + " b " +
			"	SET a.occ = 0 " +
			"	WHERE a.oif = 0 AND b.oif = 0 " +
			"	AND a.osubjectid = b.subjectid AND a.oassitemid = b.assitemid " +
			"	AND a.subjectid = b.osubjectid AND a.assitemid = b.oassitemid " +
			"	AND ABS(a.occ) - ABS(b.occ) <0";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			/**
			 * 删除所有occ大于0分录数
			 */
			sql = "delete from " + tablename + " where occ >= 0 ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			FrontProcessAction.printMessage("classificationID","开始检查科目辅助核算披露设置...") ;
			
			/**
			 * 新增核算　：对方科目是有核算的1级科目　oif=1,
			 * 1、调整科目有核算　：新增与调整科目一样的核算
			 * 2、调整科目没有核算：对方科目的核算就先找［科目辅助核算披露设置］，再找往来核算；否则就随机一类核算
			 * 
			 * 
			 * 显示核算（调整不准新增调整类型）
			 * 条件：对方科目是一级且有多个核算类型
			 * 情况：
			 * 1、调整科目没有核算	要显示
			 * 2、调整科目有对方科目没有的核算类型		要显示
			 * 3、调整科目的核算类型和对方科目一样		不用显示
			 */
			
			/**
			 * 科目辅助核算披露设置 不为空
			 * 条件：对方科目　osubjects　是一级且有核算
			 * 对方科目是有核算，先找［科目辅助核算披露设置］对应的核算类型
			 * 1、有一个披露核算，就调整到此核算
			 * 2、有多个披露核算，就判断osubjects的哪些核算类型和调整核算一样
			 * 3、有多个披露核算，没有相同的核算类型，就随机新增到osubjects　的一个核算类型上
			 * 4、如果不披露核算，就直接调整到对方科目上，不用新增核算和科目
			 * 
			 * 科目辅助核算披露设置 为空
			 * 条件：osubjects　是一级且有核算
			 * 1、oAssitems　有值，有就新增核算到这个类型下
			 * 2、oAssitems　无值、osubjects　有－个核算类型，就判断osubjects这个有多少类核算，只有一个就新增核算到这个类型
			 * 3、oAssitems　无值、osubjects　有多个核算类型，就判断osubjects的哪些核算类型和调整核算一样
			 * 4、oAssitems　无值、osubjects　有多个核算类型，就判断［科目辅助核算披露设置］有没有设置，有就新增核算到这个类型
			 * 5、以上都不是，就随机新增到osubjects　的一个核算类型上
			 */
			sql = "select 1 from c_subjectassitem where accpackageid = ? limit 1";
			ps = conn.prepareStatement(sql);
			ps.setString(1, acc);
			rs = ps.executeQuery();
			if(rs.next()){ //科目辅助核算披露设置 不为空
				DbUtil.close(rs);
				DbUtil.close(ps);
				
				//1、有一个披露核算，就调整到此核算
				sql = " update ( " +
				
				"	select a.assitemid as oAssitems,a.subjectid, max(b.assitemid) as assitemid " + 
				"	from (" +
				"		select ifequal,subjectid,assitemid,AssTotalName1 from c_subjectassitem 	" +
				"		where accpackageid=? group by subjectid having count(*) = 1 " +
				"	) a,c_assitem b " +
				"	where 1=1 " +
				"	and b.accpackageid=? " +
				"	and a.ifequal = 0 " +
				"	and a.subjectid = b.accid " +
				"	and a.assitemid = b.parentassitemid  " + 
				"	group by a.subjectid,a.assitemid " +
				"	order by a.subjectid,a.assitemid " +
				
				" ) a ," + tablename + " b  " +
				" set " +
				" b.oAssitems = a.oAssitems, " +
				" b.osubjectid = a.subjectid, " +
				" b.oassitemid = a.assitemid," +
				" b.oif=2 " +
				" where  ifnull(b.oAssitems,'') = '' " +
				" and ifnull(b.osubjectid,'') = ''" +
				" and ifnull(b.oassitemid,'') = ''" +
				" and a.subjectid = b.osubjects";
				ps = conn.prepareStatement(sql);
				ps.setString(1, acc);
				ps.setString(2, acc);
				ps.execute();
				DbUtil.close(ps);
				
				//2、有多个披露核算，就判断osubjects的哪些核算类型和调整核算一样
				sql =" update ( " +
				"	select a.assitemid as oAssitems,a.AssTotalName1 as AssTotalName,a.subjectid, max(b.assitemid) as assitemid " +
				"	from (" +
				"		select a.* from c_subjectassitem a,(" +
				"			select subjectid from c_subjectassitem where accpackageid=?  group by subjectid having count(*) > 1 " +
				"		) b where accpackageid=? and a.subjectid = b.subjectid" +
				"	)  a,c_assitem b" +
				"	where a.accpackageid=?" +
				"	and a.ifequal = 0" +
				"	and a.subjectid = b.accid" +
				"	and a.assitemid = b.parentassitemid  " +
				"	group by a.subjectid,a.assitemid " +
				"	order by a.subjectid,a.assitemid " +
				
				" ) a ," + tablename + " b " +
				" set " +
				" b.oAssitems = a.oAssitems, " +
				" b.osubjectid = a.subjectid, " +
				" b.oassitemid = a.assitemid," +
				" b.oif=2 " +
				" where  ifnull(b.oAssitems,'') = ''  " +
				" and ifnull(b.osubjectid,'') = ''" +
				" and ifnull(b.oassitemid,'') = ''" +
				" and a.subjectid = b.osubjects " + 
				" and (b.fullname = a.AssTotalName or b.fullname like concat(a.AssTotalName,'/%')) ";
				ps = conn.prepareStatement(sql);
				ps.setString(1, acc);
				ps.setString(2, acc);
				ps.setString(3, acc);
				ps.execute();
				DbUtil.close(ps);
				
				//3、有多个披露核算，没有相同的核算类型，就随机新增到osubjects　的一个核算类型上
				sql =" update ( " +
				"	select a.assitemid as oAssitems,a.AssTotalName1 as AssTotalName,a.subjectid, max(b.assitemid) as assitemid " +
				"	from (" +
				"		select a.* from c_subjectassitem a,(" +
				"			select subjectid from c_subjectassitem where accpackageid=?  group by subjectid having count(*) > 1 " +
				"		) b where accpackageid=? and a.subjectid = b.subjectid" +
				"	)  a,c_assitem b" +
				"	where a.accpackageid=?" +
				"	and a.ifequal = 0" +
				"	and a.subjectid = b.accid" +
				"	and a.assitemid = b.parentassitemid  " +
				"	group by a.subjectid,a.assitemid " +
				"	order by a.subjectid,a.assitemid " +
				
				" ) a ," + tablename + " b " +
				" set " +
				" b.oAssitems = a.oAssitems, " +
				" b.osubjectid = a.subjectid, " +
				" b.oassitemid = a.assitemid," +
				" b.oif=2 " +
				" where  ifnull(b.oAssitems,'') = ''  " +
				" and ifnull(b.osubjectid,'') = ''" +
				" and ifnull(b.oassitemid,'') = ''" +
				" and a.subjectid = b.osubjects " ;
				ps = conn.prepareStatement(sql);
				ps.setString(1, acc);
				ps.setString(2, acc);
				ps.setString(3, acc);
				ps.execute();
				DbUtil.close(ps);
				
				
				//4、如果不披露核算，就直接调整到对方科目上，不用新增核算和科目
				
				sql =" update c_subjectassitem a ," + tablename + " b " +
				" set " +
				" b.oif=0, " +
				" b.osubjectid = a.subjectid," +
				" b.oassitemid = '' " +
				" where  a.accpackageid = ? " +
				" and ifequal = 1 " +
				" and ifnull(b.osubjectid,'') = ''" +
				" and a.subjectid = b.osubjects " ;
				ps = conn.prepareStatement(sql);
				ps.setString(1, acc);
				ps.execute();
				DbUtil.close(ps);
				
				
			}else{	//科目辅助核算披露设置 为空
				DbUtil.close(rs);
				DbUtil.close(ps);
				
				
				//1、oAssitems　有值，有就新增核算到这个类型下
				sql = " update ( " +
				" 	select oAssitems,accid as subjectid,max(assitemid) as assitemid " +
				" 	from ( " +
				" 		select distinct a.assitemid as oAssitems,b.accid,b.assitemid " +
				" 		from ( " +
				" 			select distinct accid,assitemid " +
				"			from c_assitem a " +
				"			where accpackageid=? and a.level0= 1  " +
				" 		) a,c_assitem b   " +
				" 		where 1=1 " +
				" 		and b.accpackageid=? " +  
				" 		and a.accid = b.accid " +
				" 		and a.assitemid = b.parentassitemid " +   
				" 	) a " +
				" 	group by oAssitems,accid " +
				" 	order by accid,assitemid " +
				" ) a ," + tablename + " b  " +
				" set " +
				" b.oAssitems = a.oAssitems, " +
				" b.osubjectid = a.subjectid, " +
				" b.oassitemid = a.assitemid," +
				" b.oif=2 " +
				" where  ifnull(b.oAssitems,'') <> '' " +
				" and ifnull(b.osubjectid,'') = '' " +
				" and ifnull(b.oassitemid,'') = '' " +
				" and a.subjectid = b.osubjects" +
				" and a.oAssitems = b.oAssitems ";
				ps = conn.prepareStatement(sql);
				ps.setString(1, acc);
				ps.setString(2, acc);
				ps.execute();
				DbUtil.close(ps);
				
				
				// 2、oAssitems　无值、osubjects　有－个核算类型，就判断osubjects这个有多少类核算，只有一个就新增核算到这个类型
				sql = " update ( " +
				" 	select oAssitems,accid as subjectid,max(assitemid) as assitemid " +
				" 	from ( " +
				" 		select distinct a.assitemid as oAssitems,b.accid,b.assitemid " +
				" 		from ( " +
				" 			select distinct accid,assitemid " +
				"			from c_assitem a " +
				"			where accpackageid=? and a.level0= 1 group by accid having count(*) = 1 " +
				" 		) a,c_assitem b   " +
				" 		where 1=1 " +
				" 		and b.accpackageid=? " +  
				" 		and a.accid = b.accid " +
				" 		and a.assitemid = b.parentassitemid " +   
				" 	) a " +
				" 	group by oAssitems,accid " +
				" 	order by accid,assitemid " +
				" ) a ," + tablename + " b  " +
				" set " +
				" b.oAssitems = a.oAssitems, " +
				" b.osubjectid = a.subjectid, " +
				" b.oassitemid = a.assitemid," +
				" b.oif=2 " +
				" where  ifnull(b.oAssitems,'') = '' " +
				" and ifnull(b.osubjectid,'') = ''" +
				" and ifnull(b.oassitemid,'') = ''" +
				" and a.subjectid = b.osubjects";
				ps = conn.prepareStatement(sql);
				ps.setString(1, acc);
				ps.setString(2, acc);
				ps.execute();
				DbUtil.close(ps);
				
				//3、oAssitems　无值、osubjects　有多个核算类型，就判断osubjects的哪些核算类型和调整核算一样
				sql =" update ( " +
				" 	select oAssitems,a.AssTotalName,accid as subjectid,max(assitemid) as assitemid " +
				" 	from ( " +
				" 		select distinct a.assitemid as oAssitems,a.AssTotalName,b.accid,b.assitemid " + 
				" 		from ( " +
				" 			select a.* from c_assitem a,( " +
				" 				select distinct accid " +
				" 				from c_assitem a  " +
				" 				where accpackageid=? and a.level0= 1 " + 
				" 				group by accid having count(*) >1 " +
				" 			) b " +
				" 			where accpackageid=? and a.level0= 1 " + 
				" 			and a.accid = b.accid " +
				" 		) a,c_assitem b   " +
				" 		where 1=1 " +
				" 		and b.accpackageid=? " +  
				" 		and a.accid = b.accid " +
				" 		and a.assitemid = b.parentassitemid " +   
				" 	) a " +
				" 	group by oAssitems,accid " +
				" 	order by accid,assitemid " +
				" ) a ," + tablename + " b " +
				" set " +
				" b.oAssitems = a.oAssitems, " +
				" b.osubjectid = a.subjectid, " +
				" b.oassitemid = a.assitemid," +
				" b.oif=2 " +
				" where  ifnull(b.oAssitems,'') = ''  " +
				" and ifnull(b.osubjectid,'') = ''" +
				" and ifnull(b.oassitemid,'') = ''" +
				" and a.subjectid = b.osubjects " + 
				" and (b.fullname = a.AssTotalName or b.fullname like concat(a.AssTotalName,'/%')) ";
				ps = conn.prepareStatement(sql);
				ps.setString(1, acc);
				ps.setString(2, acc);
				ps.setString(3, acc);
				ps.execute();
				DbUtil.close(ps);
				
				//4、oAssitems　无值、osubjects　有多个核算类型，就判断［科目辅助核算披露设置］有没有设置，有就新增核算到这个类型
				sql =" update ( " +
				" 	select oAssitems,a.AssTotalName,accid as subjectid,max(assitemid) as assitemid " +
				" 	from ( " +
				" 		select distinct a.assitemid as oAssitems,a.AssTotalName,b.accid,b.assitemid " + 
				" 		from ( " +
				" 			select a.*  " +
				" 			from c_assitem a,c_subjectassitem b " +
				" 			where a.accpackageid=? " +
				"			and b.accpackageid=? " +
				"			and a.level0= 1  " +		
				" 			and a.accid like concat(b.subjectid,'%') " +
				" 			and instr(b.AssTotalName1,a.AssTotalName) >0 " +
				" 		) a,c_assitem b   " +
				" 		where 1=1 " +
				" 		and b.accpackageid=? " +  
				" 		and a.accid = b.accid " +
				" 		and a.assitemid = b.parentassitemid " +   
				" 	) a " +
				" 	group by oAssitems,accid " +
				" 	order by accid,assitemid " +
				" ) a ," + tablename + " b " +
				" set " +
				" b.oAssitems = a.oAssitems, " +
				" b.osubjectid = a.subjectid, " +
				" b.oassitemid = a.assitemid," +
				" b.oif=2 " +
				" where  ifnull(b.oAssitems,'') = ''  " +
				" and ifnull(b.osubjectid,'') = ''" +
				" and ifnull(b.oassitemid,'') = ''" +
				" and a.subjectid = b.osubjects " ; 
				ps = conn.prepareStatement(sql);
				ps.setString(1, acc);
				ps.setString(2, acc);
				ps.setString(3, acc);
				ps.execute();
				DbUtil.close(ps);
				
				//5、以上都不是，就随机新增到osubjects　的一个核算类型上
				sql = " update ( " +
				" 	select oAssitems,accid as subjectid,max(assitemid) as assitemid " +
				" 	from ( " +
				" 		select distinct a.assitemid as oAssitems,b.accid,b.assitemid " +
				" 		from ( " +
				" 			select distinct accid,assitemid " +
				"			from c_assitem a " +
				"			where accpackageid=? and a.level0= 1  " +
				" 		) a,c_assitem b   " +
				" 		where 1=1 " +
				" 		and b.accpackageid=? " +  
				" 		and a.accid = b.accid " +
				" 		and a.assitemid = b.parentassitemid " +   
				" 	) a " +
				" 	group by oAssitems,accid " +
				" 	order by accid,assitemid " +
				" ) a ," + tablename + " b  " +
				" set " +
				" b.oAssitems = a.oAssitems, " +
				" b.osubjectid = a.subjectid, " +
				" b.oassitemid = a.assitemid," +
				" b.oif=2 " +
				" where  ifnull(b.oAssitems,'') = ''  " +
				" and ifnull(b.osubjectid,'') = ''" +
				" and ifnull(b.oassitemid,'') = ''" +
				" and a.subjectid = b.osubjects";
				ps = conn.prepareStatement(sql);
				ps.setString(1, acc);
				ps.setString(2, acc);
				ps.execute();
				DbUtil.close(ps);
			}
			
			
//			0、如果已经设置oAssitems，就自动设置oassitemid
			sql = " update " + tablename + " b  " +
			" set " +
			" b.osubjectid = b.osubjects, " +
			" b.oassitemid = b.oAssitems," +
			" b.oif=2 " +
			" where b.oAssitems<> '' ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			/**
			 * 新增科目：对方科目一级没有核算，就在一级科目的下级新增科目 新增在一级的下级
			 * 1、对方科目有下级 		osubjects 与　osubjectid　不一样
			 * 2、对方科目没有下级　	osubjects 与　osubjectid　一样
			 * 
			 * 1、非叶子，就一定有下级
			 * aid = 自已
			 * subjectid = 下级最大
			 * 2、叶子，没有下级
			 * aid = 自已
			 * subjectid = 自已
			 */
			sql = "update (" +
			"	select if(a.ParentSubjectId = '',a.subjectid,a.ParentSubjectId) as aid,max(a.subjectid) as subjectid from (" +
			"		select subjectid,ParentSubjectId,SubjectName,SubjectFullName from c_accpkgsubject where AccPackageID = ? " +
			"		union " +
			"		select subjectid,ParentSubjectId,SubjectName,SubjectFullName from z_usesubject where projectID = ? " +
			"	) a " +
			"	group by if(a.ParentSubjectId = '',a.subjectid,a.ParentSubjectId)" +
			
			" ) a," + tablename + " b " +
			" set b.oif=2," +
			" osubjectid = a.subjectid," +
			" oassitemid = '' " +
			" where  ifnull(b.osubjectid,'') = ''" +
			" and a.aid = b.osubjects";
			ps = conn.prepareStatement(sql);
			ps.setString(1, acc);
			ps.setString(2, projectID);
			ps.execute();
			DbUtil.close(ps);
			
			sql = "update (" +
			"	select subjectid as aid,max(a.subjectid) as subjectid from (" +
			"		select subjectid,ParentSubjectId,SubjectName,SubjectFullName from c_accpkgsubject where AccPackageID = ? and isleaf =1" +
			"		union " +
			"		select subjectid,ParentSubjectId,SubjectName,SubjectFullName from z_usesubject where projectID = ? and isleaf =1" +
			"	) a " +
			"	group by subjectid" +
			
			" ) a," + tablename + " b " +
			" set b.oif=2," +
			" osubjectid = a.subjectid," +
			" oassitemid = '' " +
			" where  ifnull(b.osubjectid,'') = ''" +
			" and a.aid = b.osubjects";
			ps = conn.prepareStatement(sql);
			ps.setString(1, acc);
			ps.setString(2, projectID);
			ps.execute();
			DbUtil.close(ps);
			
			
			FrontProcessAction.printMessage("classificationID","开始插入调整分录表...") ;
			
			/**
			 * 插入调整分录表
			 */
			Project project = new ProjectService(conn).getProjectById(projectID);
			
			String SDate = "";
			String SText = "";
			int YearMonth = 0;
			String strBalance = "";
			if("0".equals(vdate)){
				SDate = (Integer.parseInt(acc.substring(6)) - 1)+"-12-31";
				SText = "期初";
				strBalance = "(DebitRemain+CreditRemain)";
				int bYear = Integer.parseInt(project.getAuditTimeBegin().substring(0, 4));
				int bMonth = Integer.parseInt(project.getAuditTimeBegin().substring(5, 7));
				YearMonth = bYear * 12 + bMonth;
			}else{
				SDate = acc.substring(6)+"-12-31";
				SText = "期末";
				strBalance = "Balance";
				int eYear = Integer.parseInt(project.getAuditTimeEnd().substring(0, 4));
				int eMonth = Integer.parseInt(project.getAuditTimeEnd().substring(5, 7));
				YearMonth = eYear * 12 + eMonth;
			} 
			
			VoucherTable vt = new VoucherTable();
			SubjectEntryTable set = new SubjectEntryTable();
			RectifyTable rt = new RectifyTable();
			
			
			
			
			vt.setAccpackageid(acc);
			vt.setFilluser(user);
			vt.setProperty(property);
			vt.setProjectID(projectID);//
			vt.setVchdate(SDate);
			vt.setTypeid("调");
			vt.setAudituser("");
		    vt.setKeepuser("");
		    vt.setDirector("");
		    vt.setAffixcount(1);
		    vt.setDoubtuserid("");

			set.setAccpackageid(acc);
			set.setVchdate(SDate);
			set.setProperty(property);
			set.setProjectID(projectID);//
			set.setTypeid("调");
			set.setCurrrate(0);
		    set.setCurrvalue(0);
		    set.setCurrency("");
		    set.setQuantity(0);
		    set.setUnitprice(0);
		    set.setBankid("");

			rt.setAccpackageId(acc);
			rt.setProjectID(projectID);//
			
			sql = "select * from " + tablename + " a where optif = 1  order by id";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			
			String result = "";
			int opt = 0;		//用于标志是否做过重分类
			while(rs.next()){
				
				opt ++ ;
				
				//rs 的值
				String subjectid = rs.getString("subjectid");
				String assitemid = rs.getString("assitemid");
				String name = rs.getString("name");
				
				String direction = rs.getString("direction");
				
				String rectifyocc = rs.getString("rectifyocc");
				String totalocc = rs.getString("totalocc");
				String talocc = rs.getString("talocc");
				String occ = rs.getString("occ");
				
				String osubjects = rs.getString("osubjects");
				String oassitems = rs.getString("oassitems");
				
				int oif = rs.getInt("oif");
				
				String osubjectid = rs.getString("osubjectid");
				String oassitemid = rs.getString("oassitemid");
				
				if(occ != null && Double.parseDouble(occ) >= 0.00){
					continue;
				}
				
				String sqlBalance = "0.00";
				if(totalocc != null && !"".equals(totalocc) ){
					sqlBalance = totalocc;
				}else{
					if(occ == null){
						sqlBalance = rectifyocc;
					}else{
						sqlBalance = occ;
					}
				}
				
				
				/**
				 * 插入分录
				 */
				String VKeyId = new DELAutocode().getAutoCode("AUVO","");
				vt.setAutoid(Integer.parseInt(VKeyId));
				
				sql = "select max(autoid) from z_voucherrectify";
				ps = conn.prepareStatement(sql);
				rs1 = ps.executeQuery();
				if(rs1.next()){
					VKeyId= String.valueOf((rs1.getInt(1)+1));
					System.out.println("系统找到z_voucherrectify最大值："+VKeyId);
					vt.setAutoid(Integer.parseInt(VKeyId));
				}
				DbUtil.close(rs1);
				DbUtil.close(ps);
				
				String vid = new DELAutocode().getAutoCode("VOID","");
				vt.setVoucherid(Integer.parseInt(vid));
				vt.setDescription(""); //备注
				
				AddOrModifyVoucher(vt,"ad"); //增加调整表
				
				String SUId = "";
				
				set.setVoucherid(vt.getAutoid());
				
				if("借".equals(direction.trim())){
					set.setDirction(1);					
				}else{
					set.setDirction(-1);
				}
				
				int serailID = 1;
				
				String subjectid1 = "",assitemid1 = "", name1 = "",Balance1 = "",str = "";
				String sql1 = "select if(c.oriaccid = '',c.orisubjectid,c.oriaccid) as subjectid ," +
				" if(c.oriaccid = '','',c.orisubjectid) as assitemid " +
				" from c_usersubject a ,c_usersubject c " +
				" where a.accpackageid = '"+acc+"' " +
				" and c.accpackageid = '"+acc+"' " +
				" and if(a.oriaccid = '',a.orisubjectid,a.oriaccid) = '"+subjectid+"' " +
				" and if(a.oriaccid = '','',a.orisubjectid) = '"+assitemid+"' " +
				" and a.accid = c.accid " +
				" and a.subjectid = c.subjectid" +
				" and a.oriParentSubjectID1 = c.oriParentSubjectID1 ";
				
				sql = "select a.subjectid,'' as assitemid,accname,direction * "+strBalance+" as Balance " +
				" from c_account a ,(	" + sql1 +
				" ) b" +
				" where 1=1 " +
				" and a.SubYearMonth * 12 + SubMonth = " + YearMonth + 
				" and a.subjectid = if(assitemid = '',b.subjectid,'') " +
				
				" union " +
				
				" select a.accid, a.assitemid,AssItemName,direction  * "+strBalance+" as Balance " +
				" from 	c_assitementryacc a ,(	" + sql1 +
				" ) b " +
				" where 1=1 " +
				" and a.SubYearMonth * 12 + SubMonth = " + YearMonth + 
				" and a.accid = b.subjectid " +
				" and a.assitemid = b.assitemid ";
				
				int opt1 = 0;	//标志有没有多科目挂账
				ps = conn.prepareStatement(sql);
				rs1 = ps.executeQuery();
				while(rs1.next()){
					opt1 ++; 
					
					SUId = new DELAutocode().getAutoCode("SUAU","");
					set.setAutoid(Integer.parseInt(SUId));
					
					sql = "select max(autoid) from z_subjectentryrectify";
					ps = conn.prepareStatement(sql);
					ResultSet rs2 = ps.executeQuery();
					if(rs2.next()){
						set.setAutoid(rs2.getInt(1)+1);
						System.out.println("系统找到z_subjectentryrectify最大值："+rs.getInt(1));
					}
					DbUtil.close(rs2);
					DbUtil.close(ps);
					
					subjectid1 = rs1.getString("subjectid");
					assitemid1 = rs1.getString("assitemid");
					name1 = rs1.getString("accname");
					Balance1 = rs1.getString("Balance");
					
					set.setOccurvalue((-1)*(Double.parseDouble(Balance1)));
					
					set.setSerail(serailID++);
					str = getSCurrency(acc,subjectid1);
					if(result.indexOf(str) == -1){
						result += str;
					}
					
					set.setSubjectid(subjectid1);
					set.setSummary(SText + "负值重分类:"+subjectid1+","+assitemid1+","+name1);//摘要
					
					if(!"".equals(assitemid1.trim())){
						rt.setEntryId(String.valueOf(set.getAutoid()));	
						rt.setAssitemId(assitemid1);
						rt.setSubjectId(subjectid1);
						AddRectify(rt,"ad");	
					}
					
					FrontProcessAction.printMessage("classificationID","插入调整科目［"+subjectid1+"］核算［"+assitemid1+"］的调整分录表") ;
					
					AddOrModifySubjectEntry(set,"ad"); //增加调整分录表
					if(bool){
						insertAnalsye(acc,projectID,subjectid1,assitemid1,subjectid1,assitemid1,String.valueOf((-1)*(Double.parseDouble(Balance1))),"2",set.getAutoid(),set.getDirction()) ;//调出科目
					}
					
				}
				
				if(opt1 == 0){
					SUId = new DELAutocode().getAutoCode("SUAU","");
					set.setAutoid(Integer.parseInt(SUId));

					sql = "select max(autoid) from z_subjectentryrectify";
					ps = conn.prepareStatement(sql);
					ResultSet rs2 = ps.executeQuery();
					if(rs2.next()){
						set.setAutoid(rs2.getInt(1)+1);
						System.out.println("系统找到z_subjectentryrectify最大值："+rs.getInt(1));
					}
					DbUtil.close(rs2);
					DbUtil.close(ps);
					
					set.setOccurvalue(Math.abs(Double.parseDouble(sqlBalance)));
					
					/**
					 *调整科目分录 
					 */
					set.setSerail(serailID++);
					str = getSCurrency(acc,subjectid);
					if(result.indexOf(str) == -1){
						result += str;
					}
					
					set.setSubjectid(subjectid);
					set.setSummary(SText + "负值重分类:"+subjectid+","+assitemid+","+name);//摘要
					
					if(!"".equals(assitemid.trim())){
						rt.setEntryId(String.valueOf(set.getAutoid()));	
						rt.setAssitemId(assitemid);
						rt.setSubjectId(subjectid);
						AddRectify(rt,"ad");	
					}
					
					FrontProcessAction.printMessage("classificationID","插入调整科目［"+subjectid+"］核算［"+assitemid+"］的调整分录表") ;
					
					AddOrModifySubjectEntry(set,"ad"); //增加调整分录表
					if(bool){
						insertAnalsye(acc,projectID,subjectid,assitemid,subjectid,assitemid,String.valueOf(Math.abs(Double.parseDouble(sqlBalance))),"2",set.getAutoid(),set.getDirction()) ;//调出科目
					}
				}
				
//				insertAnalsye(acc,projectID,subjectid,assitemid,subjectid,assitemid,String.valueOf(Math.abs(Double.parseDouble(sqlBalance))),"2") ;//调出科目 
				
				/**
				 * 对方科目分录 
				 */
				
				set.setOccurvalue(Math.abs(Double.parseDouble(sqlBalance)));
				
				SUId = new DELAutocode().getAutoCode("SUAU","");
				set.setAutoid(Integer.parseInt(SUId));
				
				sql = "select max(autoid) from z_subjectentryrectify";
				ps = conn.prepareStatement(sql);
				ResultSet rs2 = ps.executeQuery();
				if(rs2.next()){
					set.setAutoid(rs2.getInt(1)+1);
					System.out.println("系统找到z_subjectentryrectify最大值："+rs.getInt(1));
				}
				DbUtil.close(rs2);
				DbUtil.close(ps);
				
				set.setSerail(serailID);
				str = getSCurrency(acc,osubjectid);
				if(result.indexOf(str) == -1){
					result += str;
				}
				if("借".equals(direction.trim())){
					set.setDirction(-1);					
				}else{
					set.setDirction(1);
				}
				
				switch(oif){
				case 0 :	
				/**
				 * 1、调整时，同一组内随机取一个 oif = 0,
				 * 2、调整明细之间的自动对冲：名称相同，方向相反	 oif = 0 ,
				 * 3、调整，调整名称与对方核算名称一样的核算 oif = 0
				 */
					set.setSubjectid(osubjectid);
					set.setSummary(SText + "负值重分类:"+osubjectid+","+oassitemid+","+name);//摘要
					if(oassitemid != null && !"".equals(oassitemid.trim())){
						rt.setEntryId(String.valueOf(set.getAutoid()));	
						rt.setAssitemId(oassitemid);
						rt.setSubjectId(osubjectid);
						AddRectify(rt,"ad");	
					}		
					break;
				case 1 :	
				/**
				 * 新增核算　：对方科目是有核算的1级科目　oif=1,
				 * 1、调整科目有核算　：新增与调整科目一样的核算
				 */
					set.setSubjectid(osubjectid);
					set.setSummary(SText + "负值重分类:"+osubjectid+","+oassitemid+","+name);//摘要
					if(oassitemid != null && !"".equals(oassitemid.trim())){
						rt.setEntryId(String.valueOf(set.getAutoid()));	
						rt.setAssitemId(oassitemid);
						rt.setSubjectId(osubjectid);
						AddRectify(rt,"ad");	
					}
					
					insertAssitem( acc,  projectID,  name, osubjectid, oassitemid);  //新增核算
					break;
				case 2 :
				/**
				 * 新增核算　：对方科目是有核算的1级科目　oif=1,
				 * 2、调整科目没有核算：对方科目的核算就先找［科目辅助核算披露设置］，再找往来核算；否则就随机一类核算
				 * 
				 * 新增科目：对方科目一级没有核算，就在一级科目的下级新增科目 新增在一级的下级
				 * 1、对方科目有下级 		osubjects 与　osubjectid　不一样
				 * 2、对方科目没有下级　	osubjects 与　osubjectid　一样
				 */
					if(!"".equals(oassitemid)){	//新增核算
						
						sql = "select AssItemID from c_assitementryacc where AccPackageID = ? and SubMonth = 1 and accid = ? and assitemid like concat(?,'%') and level1 = 2 order by length(AssItemID) desc, AssItemID desc limit 1";
						ps = conn.prepareStatement(sql);
						ps.setString(1, acc);
						ps.setString(2, osubjectid);
						ps.setString(3, oassitems);
						
						rs1 = ps.executeQuery();
						if(rs1.next()){
							oassitemid = rs1.getString(1);
						}
						DbUtil.close(rs1);
						
						oassitemid = UTILString.getNewTaskCode(oassitemid);
						set.setSubjectid(osubjectid);
						
						set.setSummary(SText + "负值重分类:"+osubjectid+","+oassitemid+","+name);//摘要
						if(oassitemid != null && !"".equals(oassitemid.trim())){
							rt.setEntryId(String.valueOf(set.getAutoid()));	
							rt.setAssitemId(oassitemid);
							rt.setSubjectId(osubjectid);
							AddRectify(rt,"ad");	
						}
						
						insertAssitem( acc,  projectID, oassitems, name, osubjectid, oassitemid);  //新增核算
						
					}else{	//新增科目
						
						osubjectid = insertData(acc,projectID,name,osubjects);
						set.setSubjectid(osubjectid);
						set.setSummary(SText + "负值重分类:"+osubjectid+","+oassitemid+","+name);//摘要
						if(oassitemid != null && !"".equals(oassitemid.trim())){
							rt.setEntryId(String.valueOf(set.getAutoid()));	
							rt.setAssitemId(oassitemid);
							rt.setSubjectId(osubjectid);
							AddRectify(rt,"ad");	
						}
						
					}
		
					break;
				}
				
				FrontProcessAction.printMessage("classificationID","插入对方科目［"+osubjectid+"］核算［"+oassitemid+"］的调整分录表") ;
				
				AddOrModifySubjectEntry(set,"ad"); //增加调整分录表
				
				if(bool){
					insertAnalsye(acc,projectID,osubjectid,oassitemid,subjectid,assitemid,String.valueOf(Math.abs(Double.parseDouble(sqlBalance))),"1",set.getAutoid(),set.getDirction()) ;//调入科目 
				}
				
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			if("".equals(result) && opt != 0){
				result = "保存成功！";
			}
			
			if(opt != 0){
				FrontProcessAction.printMessage("classificationID","进行负值重分类的汇总...") ;
				
				createTzhz(acc,projectID);
				createWbTzhz(acc, projectID);
				createAssitem(acc,projectID);//增加核算金额
				if(bool){
					FrontProcessAction.printMessage("classificationID","进行负值重分类的账龄分析...") ;
					spanAnalsye( acc, projectID,vdate);//账龄
				}
			}
			
			
//			FrontProcessAction.printMessage("classificationID","end") ;
			
			return result;
		} catch (Exception e) {
			org.util.Debug.prtOut("负值重分类出错SQL：" + sql );
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		
	}
	
	/**
	 * 判断调整是否存在对应的核算
	 * @param autoid
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	public boolean haveAssitementry(String autoid,String projectId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try {
			sql = "select * from z_assitementryrectify where projectid="+projectId+" and  entryid="+autoid+" ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return false;
	}
	
	
	//＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	
	public void create() throws Exception {
		PreparedStatement ps = null;
		String sql = "";
		try {
			
			if("".equals(getTempTable())){
				setTempTable("tt_" + DELUnid.getNumUnid());
			}
			
			
//			sql = "CREATE TABLE z_AnalsyeRectify (    " +
//			" ProjectID int(14) NOT NULL default '0',        " +        
//			
//			" AutoId varchar(10) NOT NULL default '',     " +	//调整分录的AutoID
//			
//			" SubjectID varchar(50) NOT NULL default '',     " +	//调入科目
//			" AssItemID varchar(50) NOT NULL default '',     " +	//调入核算
//			" SFullName varchar(300) default NULL,           " +  	//调入科目的标准科目
//			" TokenID varchar(300) default NULL,           " +  	//调入科目的TokenID
//			" AssTotalName varchar(300) default NULL,           " +  	//调入核算的AssTotalName
//			" Sid varchar(50) default NULL,           " +  	//调入科目的一级编号
//			" IsSubject varchar(300) default NULL,           " +  	//是否科目
//			" DataName varchar(30) default '0', " +   
//			" SubYearMonth int(10) NOT NULL default '0',         " +  	//年份
//			" SubMonth int(10) NOT NULL default '0',         " +  	//月份
//			
//			" AnalsyeBalance decimal(15,2) default '0.00',   " +  	//账龄 = 调入账龄 + 调出账龄
//			
//			"  KEY ProjectID (ProjectID),   " +   
//			"  KEY AutoId (AutoId),   " +
//			
//			"  KEY SubjectID (SubjectID),   " +
//			"  KEY AssItemID (AssItemID),   " +
//			
//			"  KEY TokenID (TokenID),   " +
//			"  KEY AssTotalName (AssTotalName),   " +
//			
//			"  KEY SubYearMonth (SubYearMonth),   " +
//			"  KEY SubMonth (SubMonth)   " +
//			
//			" ) ENGINE=MyISAM DEFAULT CHARSET=gbk   ";
//			ps = conn.prepareStatement(sql);
//			ps.execute();
//			DbUtil.close(ps);
			
			
			sql = "CREATE TABLE "+getTempTable()+" (    " +
			" ProjectID int(14) NOT NULL default '0',        " +        
			
			" AutoId varchar(10) NOT NULL default '',     " +	//调整分录的AutoID
			" SubYearMonth int(10) NOT NULL default '0',         " +  	//年份
			
			" SubjectID varchar(50) NOT NULL default '',     " +	//调入科目
			" AssItemID varchar(50) NOT NULL default '',     " +	//调入核算
			" SFullName varchar(300) default NULL,           " +  	//调入科目的标准科目
			" TokenID varchar(300) default NULL,           " +  	//调入科目的TokenID
			" AssTotalName varchar(300) default NULL,           " +  	//调入核算的AssTotalName
			
			" Sid varchar(50) default NULL,           " +  	//调入科目的一级编号
			" IsSubject varchar(300) default NULL,           " +  	//是否科目
			
			" oSubjectID varchar(50) NOT NULL default '',    " +	//调出科目
			" oAssItemID varchar(50) NOT NULL default '',    " +  	//调出核算
			" oSFullName varchar(300) default NULL,           " +  	//调出科目的标准科目
			" oTokenID varchar(300) default NULL,           " +  	//调入科目的TokenID
			" oAssTotalName varchar(300) default NULL,           " +  	//调入核算的AssTotalName
			
			" oSid varchar(50) default NULL,           " +  	//调出科目的一级编号
			" oIsSubject varchar(300) default NULL,           " +  	//是否科目

			" opt varchar(10) default NULL,           " +  
			
			" SubMonth int(10) NOT NULL default '0',         " +  	//月份
			
			" Balance decimal(15,2) default '0.00',   " +  	//调入余额
			" oBalance decimal(15,2) default '0.00',   " +  	//调出余额
			
			" Balance1 decimal(15,2) default '0.00',   " +  	//调入余额1
			" oBalance1 decimal(15,2) default '0.00',   " +  	//调出余额1
			
			" ResBalance decimal(15,2) default '0.00',   " +  	//调入账龄
			" oResBalance decimal(15,2) default '0.00',   " +  	//调出账龄
			
			" AnalsyeBalance decimal(15,2) default '0.00',   " +//账龄 = 调入账龄 + 调出账龄
			
			" direction int(2) default NULL,       " +	//调整的方向  	
			
			"  KEY ProjectID (ProjectID),   " +   
			"  KEY SubjectID (SubjectID),   " +
			"  KEY AssItemID (AssItemID),   " +
			"  KEY oSubjectID (oSubjectID),   " +
			"  KEY oAssItemID (oAssItemID),   " +
			
			"  KEY SubjectID1 (SubjectID,AssItemID),   " +
			"  KEY oSubjectID1 (oSubjectID,oAssItemID),   " +
			
			"  KEY TokenID (TokenID),   " +
			"  KEY oTokenID (oTokenID),   " +
			
			"  KEY SubMonth (SubMonth)   " +
			
			" ) ENGINE=MyISAM DEFAULT CHARSET=gbk   ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			
		} catch (Exception e) {
//			表已存在，不用新增
		} finally {
			DbUtil.close(ps);
		}
	}
	
	
	/**
	 *	生成账龄　1年内分12个月　 
	 */
	public void spanAnalsye(String acc,String projectID,String vdate) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try {
			
//			create();//生成z_AnalsyeRectify表
//			1月以内、1-2、2-3,3-4,4-5,5-6,6-7,7-8,8-9,9-10,10-11,11-12,13-24,25-36,37-48,49-60,61以上	
			String [] months = new String[]{"0","1","2","3","4","5","6","7","8","9","10","11","12","24","36","48","60"}; 
			
			updateAnalsye(acc,projectID);
			
			System.out.println("111:"+new ASFuntion().getCurrentTime());
			sql = "select distinct oIsSubject from "+getTempTable()+" where ProjectID = '"+projectID+"' "; 
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();	
			while(rs.next()){
				String isSubject = rs.getString("oIsSubject");
				System.out.println("isSubject:"+isSubject);
				for(int i = 0 ;i<months.length; i++){
					String zlEndMonth = "",zlMonth = "";
					try { //上一区间
						zlMonth = months[i-1];
					} catch (Exception e) {
						zlMonth = "-1";
					}
					try { //下一区间
						zlEndMonth = months[i+1];
					} catch (Exception e) {
						zlEndMonth = "1000";
					}
					spanAnalsye( acc, projectID, isSubject, months[i],zlEndMonth,zlMonth,"o",vdate);
				}
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			System.out.println("222:"+new ASFuntion().getCurrentTime());
			
//			sql = "select distinct IsSubject from "+getTempTable()+" where ProjectID = '"+projectID+"' "; 
//			ps = conn.prepareStatement(sql);
//			rs = ps.executeQuery();	
//			while(rs.next()){
//				String isSubject = rs.getString("IsSubject");
//				for(int i = 0 ;i<months.length; i++){
//					String zlEndMonth = "",zlMonth = "";
//					try { //上一区间
//						zlMonth = months[i-1];
//					} catch (Exception e) {
//						zlMonth = "-1";
//					}
//					try { //下一区间
//						zlEndMonth = months[i+1];
//					} catch (Exception e) {
//						zlEndMonth = "1000";
//					}
//					spanAnalsye( acc, projectID, isSubject, months[i],zlEndMonth,zlMonth,"");
//				}
//			}
			
			//插入账龄表
			String SubYearMonth = "0";
			if("0".equals(vdate)){
				SubYearMonth = "-1";
			}
			sql = " insert into z_AnalsyeRectify (ProjectID ,AutoId,SubjectID,AssItemID,SFullName,TokenID,AssTotalName,SubYearMonth,SubMonth,AnalsyeBalance,Sid,IsSubject,direction,property) " +
			" select ProjectID ,AutoId,SubjectID,AssItemID,SFullName,TokenID,AssTotalName,"+SubYearMonth+",SubMonth,AnalsyeBalance,Sid,IsSubject,direction,'411' " +
			" from "+getTempTable()+" " +
			" where projectID = '"+projectID+"' and AnalsyeBalance <> 0 ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			sql = "drop table if exists " + getTempTable();
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			System.out.println("333:"+new ASFuntion().getCurrentTime());
			
			setTempTable("");
			
		} catch (Exception e) {
			System.out.println("出错的SQL："+sql);
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 
	 * @param acc
	 * @param projectID
	 * @param isSubject		是否科目
	 * @param zlStartMonth	当前区间
	 * @param zlEndMonth	下一区间
	 * @param zlMonth		上一区间
	 * @param type			类型
	 * @throws Exception
	 */
	public void spanAnalsye(String acc,String projectID,String isSubject,String zlStartMonth,String zlEndMonth,String zlMonth,String type,String vdate) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try {
			Project project = new ProjectService(conn).getProjectById(projectID);
			int bYear = Integer.parseInt(project.getAuditTimeBegin().substring(0, 4));
			int bMonth = Integer.parseInt(project.getAuditTimeBegin().substring(5, 7));
			int eYear = Integer.parseInt(project.getAuditTimeEnd().substring(0, 4));
			int eMonth = Integer.parseInt(project.getAuditTimeEnd().substring(5, 7));
			
			int StartYearMonth = bYear * 12 + bMonth;
			int EndYearMonth = 	eYear * 12 + eMonth;
			int EndYearMonth1 = EndYearMonth;
			
			String Balance = "a.Balance";
			if("0".equals(vdate)){
				Balance = "a.DebitRemain + a.CreditRemain";
				EndYearMonth1 = StartYearMonth;
			}
			
			String sql1 = " and opt <> '11' ";
			if(!"".equals(type)){
				sql1 = "";
			}
			
			/**
			 * 更新区间余额
			 */
			if("-1".equals(zlMonth)){ //1月以内
				if("科目".equals(isSubject)){	
					sql = " update "+getTempTable()+" a,(  " +
					
					" 	select distinct b."+type+"subjectid, a.direction2 * ("+Balance+") as "+type+"Balance " +
					" 	from c_account a,"+getTempTable()+" b  " +
					" 	where 1=1 " +
					"	and b.projectID = '"+projectID+"' " +
					" 	and a.subyearmonth*12+a.submonth='"+EndYearMonth1+"' " +
					" 	and a.subjectid = b."+type+"subjectid " +
					" 	and b.submonth = 0 " +
					" 	and b."+type+"issubject = '"+isSubject+"' " +
					
					" ) b  " +
					" set a."+type+"Balance = b."+type+"Balance,a."+type+"Balance1 = b."+type+"Balance " +
					" where 1=1 " +
					" and a.projectID = '"+projectID+"' " +
					" and a."+type+"issubject = '"+isSubject+"' " + sql1 + 
					" and a."+type+"subjectid = b."+type+"subjectid " +
					" and a.submonth = 0";
					
				}else{
					sql = " update "+getTempTable()+" a,(  " +
					" 	select distinct b."+type+"subjectid,b."+type+"assitemid, a.direction2 * ("+Balance+") as "+type+"Balance " +
					" 	from c_assitementryacc a,"+getTempTable()+" b  " +
					" 	where 1=1 " +
					"	and b.projectID = '"+projectID+"' " +
					" 	and a.subyearmonth*12+a.submonth='"+EndYearMonth1+"' " +
					" 	and a.accid = b."+type+"subjectid " +
					" 	and a.assitemid = b."+type+"assitemid " +
					" 	and b.submonth = 0 " +
					" 	and b."+type+"issubject = '"+isSubject+"' " +
					" ) b  " +
					" set a."+type+"Balance = b."+type+"Balance,a."+type+"Balance1 = b."+type+"Balance " +
					" where 1=1 " +
					" and a.projectID = '"+projectID+"' " +
					" and a."+type+"issubject = '"+isSubject+"' " + sql1 + 
					" and a."+type+"subjectid = b."+type+"subjectid " +
					" and a."+type+"assitemid = b."+type+"assitemid " +
					" and a.submonth = 0";
					
				}
				
			}else{
				sql = "update  "+getTempTable()+" a ,( " +
				" 	select distinct "+type+"subjectid, "+type+"assitemid,"+type+"balance,"+type+"ResBalance,b."+type+"balance1 " +
				" 	from "+getTempTable()+" b " +
				" 	where 1=1 " +
				"	and b.projectID = '"+projectID+"' " +
				" 	and b.submonth = '"+zlMonth+"' " +	
				" )  b " +
				" set a."+type+"balance = b."+type+"balance - b."+type+"ResBalance,a."+type+"balance1 = b."+type+"balance1 " +
				" where a.submonth = '"+zlStartMonth+"'  " +
				" and a.projectID = '"+projectID+"' " +
				" and a."+type+"subjectid = b."+type+"subjectid " +
				" and a."+type+"assitemid = b."+type+"assitemid";
				
			}
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			sql = "select 1 from "+getTempTable()+" a where projectID = '"+projectID+"' and submonth = '"+zlStartMonth+"' and a."+type+"balance <> 0 ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(!rs.next()){
				return;		//区间余额为0，不用做账龄
			}
			
			if("0".equals(vdate)){
				EndYearMonth = (eYear - 1) * 12 + eMonth;;
			}
			
			/**
			 * 更新区间账龄
			 */
//			0-1, 1-2, 2-3,3-4,4-5,5-6,6-7,7-8,8-9,9-10,10-11,11-12,13-24,25-36,37-48,49-60,61以上
//			=0<1,=1<2,=2<3
//			0,   1,  2,  3   4   5   6   7   8   9     10    11    12    24    36    48    60   1000
//			String [] months = new String[]{"1","2","3","4","5","6","7","8","9","10","11","12","13","25","37","49","61"}; 
			if("科目".equals(isSubject)){	
				
				sql="update "+getTempTable()+" a left join \n" +
				"( \n" +
				"	select tokenid,occ1,if(inarea=1,occ2,null) occ2 " +
				"	,occ3,if(inarea=1,occ4,null) occ4 " +
				"	from ( \n" +
				"		select a.tokenid, \n" +
				
				"		sum(if( a.subyearmonth*12+a.submonth>('"+EndYearMonth+"'-"+zlStartMonth+") and a.subyearmonth*12+a.submonth<=('"+EndYearMonth+"') ,  if(direction2 * DebitOcc >0,direction2 * DebitOcc,0) + if(direction2 * CreditOcc <0,(-1) * direction2 * CreditOcc,0) , 0 )) Occ1, \n" +
				"		sum(if( a.subyearmonth*12+a.submonth>('"+EndYearMonth+"'-"+zlEndMonth+") and a.subyearmonth*12+a.submonth<=('"+EndYearMonth+"' -"+zlStartMonth+") ,if(direction2 * DebitOcc >0,direction2 * DebitOcc,0) + if(direction2 * CreditOcc <0,(-1) * direction2 * CreditOcc,0), 0 )) Occ2, \n" +
				"		sum(if( a.subyearmonth*12+a.submonth>('"+EndYearMonth+"'-"+zlStartMonth+") and a.subyearmonth*12+a.submonth<=('"+EndYearMonth+"') , if(direction2 * CreditOcc >0,direction2 * CreditOcc,0) + if(direction2 * DebitOcc <0,(-1) * direction2 * DebitOcc,0) , 0 )) Occ3, \n" +
				"		sum(if( a.subyearmonth*12+a.submonth>('"+EndYearMonth+"'-"+zlEndMonth+") and a.subyearmonth*12+a.submonth<=('"+EndYearMonth+"' -"+zlStartMonth+") ,if(direction2 * CreditOcc >0,direction2 * CreditOcc,0) + if(direction2 * DebitOcc <0,(-1) * direction2 * DebitOcc,0) , 0 )) Occ4, \n" +

				"        \n";
				
				if ("1000".equals(zlEndMonth)){
					sql+=" 0 as inarea \n";
				}else{
					sql+="       if (('"+EndYearMonth+"'>=('"+EndYearMonth+"'-"+zlStartMonth+")  && ('"+EndYearMonth+"'-"+zlStartMonth+") >=min(a.subyearmonth)*12+1) \n" +
					"       	||('"+EndYearMonth+"'>= ('"+EndYearMonth+"'-"+zlEndMonth+") && ('"+EndYearMonth+"'-"+zlEndMonth+") >=min(a.subyearmonth)*12+1), \n" +
					"       	1,0) as inarea \n" ;
				}
				
				
				sql+="		from c_account a ,"+getTempTable()+" b \n" + 
				"		where 1=1 \n" + 
				"		and b.projectID = '"+projectID+"' \n" +
				"		and b.submonth = "+zlStartMonth+" \n" +
				
				"		and a.subyearmonth*12+a.submonth<= '"+EndYearMonth1+"' \n" +
				"		and a.subyearmonth*12+a.submonth>('"+EndYearMonth+"'-"+zlEndMonth+") \n" +  
				
				"		and a.tokenid = b.tokenid " +  
//				"		and (subjectfullname2 = "+type+"SFullName or subjectfullname2 like concat("+type+"SFullName,'/%')) \n" +
				"		group by a.tokenid	 \n" +
				"  )a \n" +
				")b \n" +
				" on a."+type+"tokenid=b.tokenid and a.submonth = "+zlStartMonth+" \n"+
				" set "+type+"Resbalance=if(abs("+type+"balance)=0,0,if("+type+"balance1>0,if(("+type+"balance1 -occ1) <0,0,if(occ2 is null,"+type+"balance1-occ1,if( ("+type+"balance1-occ1-occ2)<=0,"+type+"balance1-occ1,occ2))),if(("+type+"balance1 +occ3) >=0,0,if(occ4 is null,"+type+"balance1+occ3,if( ("+type+"balance1+occ3+occ4)>=0,"+type+"balance1+occ3,(-1)*occ4))))) \n" +
				" where a.projectID = '"+projectID+"' and a."+type+"issubject = '"+isSubject+"' and b.tokenid is not null " + sql1;				
				
				
			}else{
				
				sql="update "+getTempTable()+" a left join \n" +
				"( \n" +
				"	select a.accid,a.assitemid,occ1,if(inarea=1,occ2,null) occ2" +
				"	,occ3,if(inarea=1,occ4,null) occ4 " +
				"	from ( \n" +
				"		select b.subjectid as accid,b.assitemid, \n" +	
				
				"		sum(if( a.subyearmonth*12+a.submonth>('"+EndYearMonth+"'-"+zlStartMonth+") and a.subyearmonth*12+a.submonth<=('"+EndYearMonth+"') ,  if(direction2 * DebitOcc >0,direction2 * DebitOcc,0) + if(direction2 * CreditOcc <0,(-1) * direction2 * CreditOcc,0) , 0 )) Occ1, \n" +
				"		sum(if( a.subyearmonth*12+a.submonth>('"+EndYearMonth+"'-"+zlEndMonth+") and a.subyearmonth*12+a.submonth<=('"+EndYearMonth+"' -"+zlStartMonth+") ,if(direction2 * DebitOcc >0,direction2 * DebitOcc,0) + if(direction2 * CreditOcc <0,(-1) * direction2 * CreditOcc,0), 0 )) Occ2, \n" +
				"		sum(if( a.subyearmonth*12+a.submonth>('"+EndYearMonth+"'-"+zlStartMonth+") and a.subyearmonth*12+a.submonth<=('"+EndYearMonth+"') , if(direction2 * CreditOcc >0,direction2 * CreditOcc,0) + if(direction2 * DebitOcc <0,(-1) * direction2 * DebitOcc,0) , 0 )) Occ3, \n" +
				"		sum(if( a.subyearmonth*12+a.submonth>('"+EndYearMonth+"'-"+zlEndMonth+") and a.subyearmonth*12+a.submonth<=('"+EndYearMonth+"' -"+zlStartMonth+") ,if(direction2 * CreditOcc >0,direction2 * CreditOcc,0) + if(direction2 * DebitOcc <0,(-1) * direction2 * DebitOcc,0) , 0 )) Occ4, \n" +
				"        \n" ;
				if ("1000".equals(zlEndMonth)){
					sql+=" 0 as inarea \n";
				}else{
					sql+="       if (('"+EndYearMonth+"'>=('"+EndYearMonth+"'-"+zlStartMonth+")  && ('"+EndYearMonth+"'-"+zlStartMonth+") >=min(a.subyearmonth)*12+1) \n" +
					"       	||('"+EndYearMonth+"'>= ('"+EndYearMonth+"'-"+zlEndMonth+") && ('"+EndYearMonth+"'-"+zlEndMonth+") >=min(a.subyearmonth)*12+1), \n" +
					"       	1,0) as inarea \n";
				}
				
				sql+="		from c_assitementryacc a,"+getTempTable()+" b,( \n" +
				"			select distinct a.AccPackageID,a.subjectid,a.tokenid from c_account  a " +
				"			where 1=1 " +
				"			and a.subyearmonth*12+a.submonth<= '"+EndYearMonth1+"' \n" +
				"			and a.subyearmonth*12+a.submonth>('"+EndYearMonth+"'-"+zlEndMonth+") \n" +  
				"		) c \n" +
				"		where 1=1 \n" +
				"		and b.projectID = '"+projectID+"' \n" +
				"		and b.submonth = "+zlStartMonth+" \n" +
				
				"		and a.subyearmonth*12+a.submonth<= '"+EndYearMonth1+"' \n" +
				"		and a.subyearmonth*12+a.submonth>('"+EndYearMonth+"'-"+zlEndMonth+") \n" +
				"		and a.AccPackageID = c.AccPackageID   \n" +
				"		and a.accid = c.subjectid \n" +
				"		and c.tokenid=b.tokenid    \n" +
				"		and a.AssTotalName1 = b.AssTotalName \n" +
				
				"		group by b.subjectid,b.assitemid	 \n" +
				" ) a \n" +
				")b \n" +
				"on a."+type+"Subjectid=b.accid and a."+type+"assitemid=b.assitemid and a.submonth = "+zlStartMonth+"  \n" +
				"set "+type+"Resbalance =if(abs("+type+"balance)=0,0,if("+type+"balance1>0,if(("+type+"balance1 -occ1) <0,0,if(occ2 is null,"+type+"balance1-occ1,if( ("+type+"balance1-occ1-occ2)<=0,"+type+"balance1-occ1,occ2))),if(("+type+"balance1 +occ3) >=0,0,if(occ4 is null,"+type+"balance1+occ3,if( ("+type+"balance1+occ3+occ4)>=0,"+type+"balance1+occ3,(-1)*occ4))))) \n" +
				"where a.projectID = '"+projectID+"' and a.submonth = "+zlStartMonth+" and a."+type+"issubject = '"+isSubject+"' and b.accid is not null " + sql1;
			
			}
			System.out.println(isSubject + "|"+sql);
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			/**
			 * 更新最后的账龄
			 */
			sql = "update "+getTempTable()+" a set AnalsyeBalance = Resbalance + (-1)*oResbalance where projectID = '"+projectID+"' and submonth = "+zlStartMonth+" ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			
		} catch (Exception e) {
			System.out.println("出错的SQL："+sql);
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	
	
	public void insertAnalsye(String acc,String projectID, String SubjectID,
			String AssItemID, String oSubjectID, String oAssItemID,String Balance,String opt,int AutoId,int direction)
			throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try {
			
			create();//生成z_AnalsyeRectify表
			//1月以内、1-2、2-3,3-4,4-5,5-6,6-7,7-8,8-9,9-10,10-11,11-12,13-24,25-36,37-48,49-60,61以上			
			String [] months = new String[]{"0","1","2","3","4","5","6","7","8","9","10","11","12","24","36","48","60"};  
			
			sql = "select 1 from "+getTempTable()+" " +
				" where ProjectID = ? " +
				" and SubjectID = ? " +
				" and AssItemID = ? " +
				" and oSubjectID = ? " +
				" and oAssItemID = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, projectID);
			ps.setString(2, SubjectID);
			ps.setString(3, AssItemID);
			ps.setString(4, oSubjectID);
			ps.setString(5, oAssItemID);
			rs = ps.executeQuery();
			if(rs.next()){
				return;
			}
			
			/**
			 * 新增一条
			 */
			sql = "insert into "+getTempTable()+" (ProjectID,SubjectID,AssItemID,oSubjectID,oAssItemID,subMonth,opt,AutoId,direction) values (?,?,?,?,?,?,?,?,?) ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, projectID);
			ps.setString(2, SubjectID);
			ps.setString(3, AssItemID);
			ps.setString(4, oSubjectID);
			ps.setString(5, oAssItemID);
			ps.setString(6, "0");
			ps.setString(7, opt);
			ps.setInt(8, AutoId);
			ps.setInt(9, direction);
			
			ps.execute();
			DbUtil.close(ps);
			
			/**
			 * 检查多科目挂账
			 */
			sql = "insert into "+getTempTable()+" (ProjectID,SubjectID,AssItemID,oSubjectID,oAssItemID,subMonth,opt,AutoId,direction) " +
			" 			select '"+projectID+"' as projectID," +
			"			b.subjectid," +
			"			b.assitemid," +
			"			b.orisubjectid,b.oriassitemid,0 as subMonth,"+opt+"1 as  opt,b.AutoId,b.direction " +
			"			from "+getTempTable()+" a " +
			
			"			right join (" + 
			" 						select b.subjectid,b.assitemid, " +
			" 						if(c.oriaccid = '',c.orisubjectid,c.oriaccid) as orisubjectid, " +
			" 						if(c.oriaccid = '','',c.orisubjectid) as oriassitemid,b.AutoId,b.direction " +
			" 						from c_usersubject a ,"+getTempTable()+" b,c_usersubject c " +
			" 						where 1=1 " +
			"						and a.AccPackageID = '"+acc+"'" +
			"						and a.oriDataName = '0' " +
			"						and b.projectID = '"+projectID+"'" +
			"						and b.osubjectid = '"+oSubjectID+"' " +
			"						and b.oassitemid = '"+oAssItemID+"' " +
			"						and c.AccPackageID = '"+acc+"'" +
			"						and c.oriDataName = '0' " +
			" 						and if(a.oriaccid = '',a.orisubjectid,a.oriaccid) = b.osubjectid " + 
			" 						and if(a.oriaccid = '','',a.orisubjectid) =b.oassitemid " +
			"						and a.accid = c.accid " +
			"						and a.SubjectID = c.SubjectID " +
			" 						and a.oriParentSubjectID1 = c.oriParentSubjectID1 " +
			" 			) b  " +
			" 			on a.projectID = '"+projectID+"' " +
			"			and a.subjectid = b.subjectid " +
			" 			and a.assitemid = b.assitemid " +
			" 			and a.osubjectid = b.orisubjectid " +
			" 			and a.oassitemid = b.oriassitemid " +
			" 			where a.projectid is null		 " ;
			System.out.println("多科目挂账 SQL:"+sql);
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			if("1".equals(opt)){
				sql = "update "+getTempTable()+" set SubjectID = oSubjectID,AssItemID = oAssItemID where projectID = '"+projectID+"' and opt = 21";
				ps = conn.prepareStatement(sql);
				ps.execute();
				DbUtil.close(ps);
			}
			
			
//			/**
//			 * 更新　Sid、SFullName、TokenID；oSid、oSFullName、oTokenID
//			 */
//			String sql1 = "";
//			
//			sql1 = "	select a.subjectid,a.AccName,a.SubjectFullName1,a.subjectfullname2,a.tokenid,b.subjectid as sid " +
//			"	from c_account a,( " +
//			"		select * from c_account b   " +
//			"		where 1=1  " +
//			"		and b.AccPackageID = "+acc+" " +
//			"		and b.submonth = 1 " +
//			"		and b.level1 = 1 " +
//			"	) b " +
//			"	where a.AccPackageID = "+acc+" " +
//			"	and a.submonth = 1     " +
//			"	and (a.SubjectFullName1 =b.SubjectFullName1 or a.SubjectFullName1 like concat(b.SubjectFullName1,'/%')) " +
//			"	union " +
//			"	select subjectid,subjectname,SubjectFullName,SubjectFullName,SubjectFullName,tipsubjectid from z_usesubject where projectid = '"+projectID+"' " +
//			
//			"	union " +
//			
//			"	select a.subjectid ,a.subjectname,a.SubjectFullName,a.SubjectFullName2," +
//			"	case when a.level0=1 then a.subjectfullname2 else concat(d.subjectfullname2,'/',a.SubjectName) end as tokenid ," +
//			"	d.subjectid" +
//			"	from (" +
//			"		select  distinct a.*," +
//			"		case when c.level0=1 then concat(c.standkey,substring(a.subjectfullname,locate('/',a.subjectfullname))) when c.level0=2 then concat(c.standkey,'/',a.subjectfullname) else a.subjectfullname end as subjectfullname2," +
//			"		case ifnull(c.property,substring(a.property,2,1)) when 2 then -1 else ifnull(c.property,substring(a.property,2,1)) end as direction2,SubjectName as standname " +
//			"		from c_accpkgsubject a " +
//			"		left join c_account b on a.accpackageid=b.accpackageid and a.SubjectID=b.SubjectID and SubMonth=1 " +
//			"		left join z_keyresult c on c.standkey not like '%/%' and (a.subjectfullname=c.userkey  or a.subjectfullname like concat(c.userkey,'/','%'))  " +
//			"		where a.AccPackageID = "+acc+"  " +
//			"		and b.SubjectID is null  " +
//			"	) a " +
//			"	left join (select * from c_account  d where 1=1 and d.level1=1 and d.SubMonth=1 ) d on a.accpackageid=d.accpackageid " +
//			"	and (a.subjectfullname=d.subjectfullname1 or a.subjectfullname like concat(d.subjectfullname1,'/%'))  " +
//			"";
//			
//			//更新　Sid、SFullName、TokenID；
//			sql = "update  "+getTempTable()+" a,(" +
//				sql1 +
//			"	) b " +
//			"	set a.sid = b.sid,a.SFullName = b.SubjectFullName2,a.TokenID = b.TokenID" +
//			"	where a.projectid = '"+projectID+"' " +
//			"	and a.subjectid = b.subjectid ";
//			ps = conn.prepareStatement(sql);
//			ps.execute();
//			DbUtil.close(ps);
//			
//			//更新　oSid、oSFullName、	；
//			sql = "update  "+getTempTable()+" a,(" +
//				sql1 +
//			"	) b " +
//			"	set a.osid = b.sid,a.oSFullName = b.SubjectFullName2,a.oTokenID = b.TokenID" +
//			"	where a.projectid = '"+projectID+"' " +
//			"	and a.osubjectid = b.subjectid ";
//			ps = conn.prepareStatement(sql);
//			ps.execute();
//			DbUtil.close(ps);
//			
//			//更新AssTotalName
//			sql = "update  "+getTempTable()+" a,c_assitementryacc b " +
//			"	set a.AssTotalName = b.AssTotalName1 " +
//			"	where a.projectid = '"+projectID+"' " +
//			"	and b.AccPackageID = '"+acc+"' " +
//			"	and a.subjectid = b.accid " +
//			"	and a.assitemid = b.assitemid ";
//			ps = conn.prepareStatement(sql);
//			ps.execute();
//			DbUtil.close(ps);
//			
////			更新oAssTotalName
//			sql = "update  "+getTempTable()+" a,c_assitementryacc b " +
//			"	set a.oAssTotalName = b.AssTotalName1 " +
//			"	where a.projectid = '"+projectID+"' " +
//			"	and b.AccPackageID = '"+acc+"' " +
//			"	and a.osubjectid = b.accid " +
//			"	and a.oassitemid = b.assitemid ";
//			ps = conn.prepareStatement(sql);
//			ps.execute();
//			DbUtil.close(ps);	
//			
//			sql = "update  "+getTempTable()+" a set " +
//			" IsSubject = if(ifnull(assitemid,'') = '','科目','核算'), " +
//			" oIsSubject = if(ifnull(oassitemid,'') = '','科目','核算')" +
//			" where projectid = '"+projectID+"' ";
//			ps = conn.prepareStatement(sql);
//			ps.execute();
//			DbUtil.close(ps);
//			
//			/**
//			 * 增加区间
//			 */
//			for(int i = 1;i<months.length;i++){
//				sql = " insert into "+getTempTable()+" (autoid,ProjectID,SubjectID,AssItemID,IsSubject,Sid,SFullName, oSubjectID,oAssItemID,oIsSubject,oSid,oSFullName,subMonth,opt, TokenID,AssTotalName,oTokenID,oAssTotalName,direction) " +
//				" select a.autoid,a.ProjectID,a.SubjectID,a.AssItemID,a.IsSubject,a.Sid,a.SFullName,a.oSubjectID,a.oAssItemID,a.oIsSubject,a.oSid,a.oSFullName,'"+months[i]+"' as subMonth,a.opt,a.TokenID,a.AssTotalName,a.oTokenID,a.oAssTotalName,a.direction  " +
//				" from (" +
//				"	select distinct a.autoid,a.ProjectID,a.SubjectID,a.AssItemID,a.IsSubject,a.Sid,a.SFullName,a.oSubjectID,a.oAssItemID,a.oIsSubject,a.oSid,a.oSFullName,a.opt,a.TokenID,a.AssTotalName,a.oTokenID,a.oAssTotalName,a.direction " +
//				"	from "+getTempTable()+" a where a.projectid = '"+projectID+"' " +
//				" ) a left join ( " +
//				" 	select *  " +
//				" 	from "+getTempTable()+" " +
//				" 	where projectid = '"+projectID+"' " +
//				"	and submonth = " + months[i] + 
//				" ) b  " +
//				" on a.subjectid = b.subjectid " +
//				" and a.assitemid = b.assitemid " +
//				" and a.osubjectid = b.osubjectid " +
//				" and a.oassitemid = b.oassitemid " +
//				" where a.projectid = '"+projectID+"' and b.projectid is null";
//				ps = conn.prepareStatement(sql);
//				ps.execute();
//				DbUtil.close(ps);
//				
//			}
			
		} catch (Exception e) {
			System.out.println("出错的SQL："+sql);
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
	}
	
	
	public void updateAnalsye(String acc,String projectID) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try {
			String [] months = new String[]{"0","1","2","3","4","5","6","7","8","9","10","11","12","24","36","48","60"};
			
			//删除多余的挂账科目
			sql = "	delete a from "+getTempTable()+" a left join ( " +
			"		select a.autoid,if(b.entryid is null,a.subjectid,b.subjectid) as subjectid, " +
			"		if(b.entryid is null,'',b.assitemid) as assitemid " +
			"		from z_subjectentryrectify a  " +
			"		left join z_assitementryrectify b " +
			"		on a.ProjectID = '"+projectID+"' " +
			"		and b.ProjectID = '"+projectID+"' " +
			"		and a.autoid = b.entryid " +
			"	) b  " +
			"	on a.ProjectID = '"+projectID+"' " +
			"	and a.autoid = b.autoid " +
			"	and a.subjectid = b.subjectid " +
			"	and a.assitemid = b.assitemid " +
			"	where b.autoid is null";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			/**
			 * 更新　Sid、SFullName、TokenID；oSid、oSFullName、oTokenID
			 */
			String sql1 = "";
			
			sql1 = "	select a.subjectid,a.AccName,a.SubjectFullName1,a.subjectfullname2,a.tokenid,b.subjectid as sid " +
			"	from c_account a,( " +
			"		select * from c_account b   " +
			"		where 1=1  " +
			"		and b.AccPackageID = "+acc+" " +
			"		and b.submonth = 1 " +
			"		and b.level1 = 1 " +
			"	) b " +
			"	where a.AccPackageID = "+acc+" " +
			"	and a.submonth = 1     " +
			"	and (a.SubjectFullName1 =b.SubjectFullName1 or a.SubjectFullName1 like concat(b.SubjectFullName1,'/%')) " +
			"	union " +
			"	select subjectid,subjectname,SubjectFullName,SubjectFullName,SubjectFullName,tipsubjectid from z_usesubject where projectid = '"+projectID+"' " +
			
			"	union " +
			
			"	select a.subjectid ,a.subjectname,a.SubjectFullName,a.SubjectFullName2," +
			"	case when a.level0=1 then a.subjectfullname2 else concat(d.subjectfullname2,'/',a.SubjectName) end as tokenid ," +
			"	d.subjectid" +
			"	from (" +
			"		select  distinct a.*," +
			"		case when c.level0=1 then concat(c.standkey,substring(a.subjectfullname,locate('/',a.subjectfullname))) when c.level0=2 then concat(c.standkey,'/',a.subjectfullname) else a.subjectfullname end as subjectfullname2," +
			"		case ifnull(c.property,substring(a.property,2,1)) when 2 then -1 else ifnull(c.property,substring(a.property,2,1)) end as direction2,SubjectName as standname " +
			"		from c_accpkgsubject a " +
			"		left join c_account b on a.accpackageid=b.accpackageid and a.SubjectID=b.SubjectID and SubMonth=1 " +
			"		left join z_keyresult c on c.standkey not like '%/%' and (a.subjectfullname=c.userkey  or a.subjectfullname like concat(c.userkey,'/','%'))  " +
			"		where a.AccPackageID = "+acc+"  " +
			"		and b.SubjectID is null  " +
			"	) a " +
			"	left join (select * from c_account  d where 1=1 and d.level1=1 and d.SubMonth=1 ) d on a.accpackageid=d.accpackageid " +
			"	and (a.subjectfullname=d.subjectfullname1 or a.subjectfullname like concat(d.subjectfullname1,'/%'))  " +
			"";
			
			//更新　Sid、SFullName、TokenID；
			sql = "update  "+getTempTable()+" a,(" +
				sql1 +
			"	) b " +
			"	set a.sid = b.sid,a.SFullName = b.SubjectFullName2,a.TokenID = b.TokenID" +
			"	where a.projectid = '"+projectID+"' " +
			"	and a.subjectid = b.subjectid ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			//更新　oSid、oSFullName、	；
			sql = "update  "+getTempTable()+" a,(" +
				sql1 +
			"	) b " +
			"	set a.osid = b.sid,a.oSFullName = b.SubjectFullName2,a.oTokenID = b.TokenID" +
			"	where a.projectid = '"+projectID+"' " +
			"	and a.osubjectid = b.subjectid ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			//更新AssTotalName
			sql = "update  "+getTempTable()+" a,c_assitementryacc b " +
			"	set a.AssTotalName = b.AssTotalName1 " +
			"	where a.projectid = '"+projectID+"' " +
			"	and b.AccPackageID = '"+acc+"' " +
			"	and a.subjectid = b.accid " +
			"	and a.assitemid = b.assitemid ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
//			更新oAssTotalName
			sql = "update  "+getTempTable()+" a,c_assitementryacc b " +
			"	set a.oAssTotalName = b.AssTotalName1 " +
			"	where a.projectid = '"+projectID+"' " +
			"	and b.AccPackageID = '"+acc+"' " +
			"	and a.osubjectid = b.accid " +
			"	and a.oassitemid = b.assitemid ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);	
			
			sql = "update  "+getTempTable()+" a set " +
			" IsSubject = if(ifnull(assitemid,'') = '','科目','核算'), " +
			" oIsSubject = if(ifnull(oassitemid,'') = '','科目','核算')" +
			" where projectid = '"+projectID+"' ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			/**
			 * 增加区间
			 */
			for(int i = 1;i<months.length;i++){
				sql = " insert into "+getTempTable()+" (autoid,ProjectID,SubjectID,AssItemID,IsSubject,Sid,SFullName, oSubjectID,oAssItemID,oIsSubject,oSid,oSFullName,subMonth,opt, TokenID,AssTotalName,oTokenID,oAssTotalName,direction) " +
				" select a.autoid,a.ProjectID,a.SubjectID,a.AssItemID,a.IsSubject,a.Sid,a.SFullName,a.oSubjectID,a.oAssItemID,a.oIsSubject,a.oSid,a.oSFullName,'"+months[i]+"' as subMonth,a.opt,a.TokenID,a.AssTotalName,a.oTokenID,a.oAssTotalName,a.direction  " +
				" from (" +
				"	select distinct a.autoid,a.ProjectID,a.SubjectID,a.AssItemID,a.IsSubject,a.Sid,a.SFullName,a.oSubjectID,a.oAssItemID,a.oIsSubject,a.oSid,a.oSFullName,a.opt,a.TokenID,a.AssTotalName,a.oTokenID,a.oAssTotalName,a.direction " +
				"	from "+getTempTable()+" a where a.projectid = '"+projectID+"' " +
				" ) a left join ( " +
				" 	select *  " +
				" 	from "+getTempTable()+" " +
				" 	where projectid = '"+projectID+"' " +
				"	and submonth = " + months[i] + 
				" ) b  " +
				" on a.subjectid = b.subjectid " +
				" and a.assitemid = b.assitemid " +
				" and a.osubjectid = b.osubjectid " +
				" and a.oassitemid = b.oassitemid " +
				" where a.projectid = '"+projectID+"' and b.projectid is null";
				ps = conn.prepareStatement(sql);
				ps.execute();
				DbUtil.close(ps);
				
			}
		} catch (Exception e) {
			System.out.println("出错的SQL："+sql);
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
	}
	
	
	public void updateAnalsyeRectify(String acc,String projectID) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try{
			
			sql = "update z_analsyerectify a ,z_subjectentryrectify b " +
			"	set a.property = b.property " +
			"	where a.ProjectID = ? and b.ProjectID = ?" +
			"	and ifnull(a.property,'') = '' " +
			"	and a.autoid = b.autoid";
			ps = conn.prepareStatement(sql);
			ps.setString(1,projectID) ;
			ps.setString(2,projectID) ;
			ps.execute() ;
			DbUtil.close(ps);
			
			
			String sql1 = "";
			
			sql1 = "	select a.subjectid,a.AccName,a.SubjectFullName1,a.subjectfullname2,a.tokenid,b.subjectid as sid " +
			"	from c_account a,( " +
			"		select * from c_account b   " +
			"		where 1=1  " +
			"		and b.AccPackageID = "+acc+" " +
			"		and b.submonth = 1 " +
			"		and b.level1 = 1 " +
			"	) b " +
			"	where a.AccPackageID = "+acc+" " +
			"	and a.submonth = 1     " +
			"	and (a.SubjectFullName1 =b.SubjectFullName1 or a.SubjectFullName1 like concat(b.SubjectFullName1,'/%')) " +
			"	union " +
			"	select subjectid,subjectname,SubjectFullName,SubjectFullName,SubjectFullName,tipsubjectid from z_usesubject " +
			"	where projectid = '"+projectID+"' " +
			
			"	union " +
			"	select a.subjectid ,a.subjectname,a.SubjectFullName,a.SubjectFullName2," +
			"	case when a.level0=1 then a.subjectfullname2 else concat(d.subjectfullname2,'/',a.SubjectName) end as tokenid ," +
			"	d.subjectid" +
			"	from (" +
			"		select  distinct a.*," +
			"		case when c.level0=1 then concat(c.standkey,substring(a.subjectfullname,locate('/',a.subjectfullname))) when c.level0=2 then concat(c.standkey,'/',a.subjectfullname) else a.subjectfullname end as subjectfullname2," +
			"		case ifnull(c.property,substring(a.property,2,1)) when 2 then -1 else ifnull(c.property,substring(a.property,2,1)) end as direction2,SubjectName as standname " +
			"		from c_accpkgsubject a " +
			"		left join c_account b on a.accpackageid=b.accpackageid and a.SubjectID=b.SubjectID and SubMonth=1 " +
			"		left join z_keyresult c on c.standkey not like '%/%' and (a.subjectfullname=c.userkey  or a.subjectfullname like concat(c.userkey,'/','%'))  " +
			"		where a.AccPackageID = "+acc+"  " +
			"		and b.SubjectID is null  " +
			"	) a " +
			"	left join (select * from c_account  d where 1=1 and d.level1=1 and d.SubMonth=1 ) d on a.accpackageid=d.accpackageid " +
			"	and (a.subjectfullname=d.subjectfullname1 or a.subjectfullname like concat(d.subjectfullname1,'/%'))  " +
			"";
//			System.out.println(sql1);
			
			//更新　Sid、SFullName、TokenID；
			sql = "update  z_analsyerectify a,(" +
				sql1 +
			"	) b " +
			"	set a.sid = b.sid,a.SFullName = b.SubjectFullName2,a.TokenID = b.TokenID" +
			"	where a.projectid = '"+projectID+"' " +
			"	and a.sid = '' " +
			"	and a.subjectid = b.subjectid ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			
			//更新AssTotalName
			sql = "update  z_analsyerectify a,c_assitementryacc b " +
			"	set a.AssTotalName = b.AssTotalName1 " +
			"	where a.projectid = '"+projectID+"' " +
			"	and b.AccPackageID = '"+acc+"' " +
			"	and a.AssTotalName = '' " +
			"	and a.subjectid = b.accid " +
			"	and a.assitemid = b.assitemid ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			sql = "update  z_analsyerectify a set " +
			" IsSubject = if(ifnull(assitemid,'') = '','科目','核算') " +
			" where projectid = '"+projectID+"' " +
			" and a.IsSubject = '' ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);

			
		} catch (Exception e) {
			System.out.println("出错的SQL："+sql);
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public void addAnalsyeRectify(String acc,AnalsyeRectify ar) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try {
			sql = "insert into z_analsyerectify(ProjectID,AutoId,SubjectID,AssItemID,SFullName,TokenID,AssTotalName,Sid,IsSubject,DataName,SubYearMonth,SubMonth,AnalsyeBalance,direction,property) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			ps = conn.prepareStatement(sql);
			
			ps.setString(1,ar.getProjectID()) ;
			ps.setString(2,ar.getAutoId()) ;
			ps.setString(3,ar.getSubjectID()) ;
			ps.setString(4,ar.getAssItemID()) ;
			ps.setString(5,ar.getSFullName()) ;
			ps.setString(6,ar.getTokenID()) ;
			ps.setString(7,ar.getAssTotalName()) ;
			ps.setString(8,ar.getSid()) ;
			ps.setString(9,ar.getIsSubject()) ;
			ps.setString(10,ar.getDataName()) ;
			ps.setString(11,ar.getSubYearMonth()) ;
			ps.setString(12,ar.getSubMonth()) ;
			ps.setString(13,ar.getAnalsyeBalance()) ;
			ps.setInt(14,ar.getDirection()) ;
			
			ps.setString(15,ar.getProperty()) ;
			
			ps.execute() ;
			DbUtil.close(ps);
			
//			sql = "update z_analsyerectify a ,z_subjectentryrectify b " +
//			"	set a.property = b.property " +
//			"	where a.ProjectID = ? and b.ProjectID = ?" +
//			"	and ifnull(a.property,'') = '' " +
//			"	and a.autoid = b.autoid";
//			ps = conn.prepareStatement(sql);
//			ps.setString(1,ar.getProjectID()) ;
//			ps.setString(2,ar.getProjectID()) ;
//			ps.execute() ;
//			DbUtil.close(ps);
//			
//			
//			String sql1 = "";
//			
//			sql1 = "	select a.subjectid,a.AccName,a.SubjectFullName1,a.subjectfullname2,a.tokenid,b.subjectid as sid " +
//			"	from c_account a,( " +
//			"		select * from c_account b   " +
//			"		where 1=1  " +
//			"		and b.AccPackageID = "+acc+" " +
//			"		and b.submonth = 1 " +
//			"		and b.level1 = 1 " +
//			"	) b " +
//			"	where a.AccPackageID = "+acc+" " +
//			"	and a.submonth = 1     " +
//			"	and (a.SubjectFullName1 =b.SubjectFullName1 or a.SubjectFullName1 like concat(b.SubjectFullName1,'/%')) " +
//			"	union " +
//			"	select subjectid,subjectname,SubjectFullName,SubjectFullName,SubjectFullName,tipsubjectid from z_usesubject " +
//			"	where projectid = '"+ar.getProjectID()+"' " +
//			
//			"	union " +
//			"	select a.subjectid ,a.subjectname,a.SubjectFullName,a.SubjectFullName2," +
//			"	case when a.level0=1 then a.subjectfullname2 else concat(d.subjectfullname2,'/',a.SubjectName) end as tokenid ," +
//			"	d.subjectid" +
//			"	from (" +
//			"		select  distinct a.*," +
//			"		case when c.level0=1 then concat(c.standkey,substring(a.subjectfullname,locate('/',a.subjectfullname))) when c.level0=2 then concat(c.standkey,'/',a.subjectfullname) else a.subjectfullname end as subjectfullname2," +
//			"		case ifnull(c.property,substring(a.property,2,1)) when 2 then -1 else ifnull(c.property,substring(a.property,2,1)) end as direction2,SubjectName as standname " +
//			"		from c_accpkgsubject a " +
//			"		left join c_account b on a.accpackageid=b.accpackageid and a.SubjectID=b.SubjectID and SubMonth=1 " +
//			"		left join z_keyresult c on c.standkey not like '%/%' and (a.subjectfullname=c.userkey  or a.subjectfullname like concat(c.userkey,'/','%'))  " +
//			"		where a.AccPackageID = "+acc+"  " +
//			"		and b.SubjectID is null  " +
//			"	) a " +
//			"	left join (select * from c_account  d where 1=1 and d.level1=1 and d.SubMonth=1 ) d on a.accpackageid=d.accpackageid " +
//			"	and (a.subjectfullname=d.subjectfullname1 or a.subjectfullname like concat(d.subjectfullname1,'/%'))  " +
//			"";
////			System.out.println(sql1);
//			
//			//更新　Sid、SFullName、TokenID；
//			sql = "update  z_analsyerectify a,(" +
//				sql1 +
//			"	) b " +
//			"	set a.sid = b.sid,a.SFullName = b.SubjectFullName2,a.TokenID = b.TokenID" +
//			"	where a.projectid = '"+ar.getProjectID()+"' " +
//			"	and a.sid = '' " +
//			"	and a.subjectid = b.subjectid ";
//			ps = conn.prepareStatement(sql);
//			ps.execute();
//			DbUtil.close(ps);
//			
//			
//			//更新AssTotalName
//			sql = "update  z_analsyerectify a,c_assitementryacc b " +
//			"	set a.AssTotalName = b.AssTotalName1 " +
//			"	where a.projectid = '"+ar.getProjectID()+"' " +
//			"	and b.AccPackageID = '"+acc+"' " +
//			"	and a.AssTotalName = '' " +
//			"	and a.subjectid = b.accid " +
//			"	and a.assitemid = b.assitemid ";
//			ps = conn.prepareStatement(sql);
//			ps.execute();
//			DbUtil.close(ps);
//			
//			sql = "update  z_analsyerectify a set " +
//			" IsSubject = if(ifnull(assitemid,'') = '','科目','核算') " +
//			" where projectid = '"+ar.getProjectID()+"' " +
//			" and a.IsSubject = '' ";
//			ps = conn.prepareStatement(sql);
//			ps.execute();
//			DbUtil.close(ps);
			
		} catch (Exception e) {
			org.util.Debug.prtOut("调整分录帐龄保存出错SQL：" + sql );
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public ArrayList selectAnalsyeRectify(String AutoId,String projectId)throws Exception {
			ArrayList al = new ArrayList();
			DbUtil.checkConn(conn);
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				String sql = "select ProjectID,AutoId,SubjectID,AssItemID,SFullName,TokenID,AssTotalName,Sid,IsSubject,DataName,SubYearMonth,SubMonth,AnalsyeBalance"
					       + " from z_analsyerectify where AutoId=? and ProjectID=? order by SubMonth" ;
				
				ps = conn.prepareStatement(sql) ;
				ps.setString(1,AutoId) ;
				ps.setString(2,projectId) ;
				
				rs = ps.executeQuery() ;
				while(rs.next()) {
					AnalsyeRectify ar = new AnalsyeRectify();
					
					ar.setProjectID(rs.getString("ProjectID")) ;
					ar.setAutoId(rs.getString("AutoId")) ;
					ar.setSubjectID(rs.getString("SubjectID")) ;
					ar.setAssItemID(rs.getString("AssItemID")) ;
					ar.setSFullName(rs.getString("SFullName")) ;
					ar.setTokenID(rs.getString("TokenID")) ;
					ar.setAssTotalName(rs.getString("AssTotalName")) ;
					ar.setSid(rs.getString("Sid")) ;
					ar.setIsSubject(rs.getString("IsSubject")) ;
					ar.setDataName(rs.getString("DataName")) ;
					ar.setSubYearMonth(rs.getString("SubYearMonth")) ;
					ar.setSubMonth(rs.getString("SubMonth")) ;
					ar.setAnalsyeBalance(rs.getString("AnalsyeBalance")) ;
					al.add(ar) ;
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				DbUtil.close(rs);
				DbUtil.close(ps);
			}
			return al;
		}
	
	
	/**
	 *	初始化 
	 */
	public void createAnalsye(String acc,String projectID) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try {
//			try {
//				sql = " CREATE TABLE z_analsyerectify (    " +
//				 "   ProjectID int(14) NOT NULL default '0',     " +    
//				 "      AutoId varchar(10) NOT NULL default '',       " +  
//				 "      SubjectID varchar(50) NOT NULL default '',     " + 
//				 "      AssItemID varchar(50) NOT NULL default '',      " +
//				 "      SFullName varchar(300) default NULL,            " +
//				 "      TokenID varchar(300) default NULL,              " +
//				 "      AssTotalName varchar(300) default NULL,         " +
//				 "      Sid varchar(50) default NULL,                   " +
//				 "      IsSubject varchar(300) default NULL,            " +
//				 "      DataName varchar(30) default '0',               " +
//				 "      SubYearMonth int(10) NOT NULL default '0',      " +
//				 "      SubMonth int(10) NOT NULL default '0',          " +
//				 "      AnalsyeBalance decimal(15,2) default '0.00',    " +
//				 "      direction int(2) default NULL,                  " +
//				 "      KEY ProjectID (ProjectID),                    " +
//				 "      KEY AutoId (AutoId),                          " +
//				 "      KEY SubjectID (SubjectID),                    " +
//				 "      KEY AssItemID (AssItemID),                    " +
//				 "      KEY TokenID (TokenID),                        " +
//				 "      KEY AssTotalName (AssTotalName),              " +
//				 "      KEY SubYearMonth (SubYearMonth),              " +
//				 "      KEY SubMonth (SubMonth)                       " +
//				 "    ) ENGINE=MyISAM DEFAULT CHARSET=gbk   ";
//				ps = conn.prepareStatement(sql) ;
//				ps.execute();
//				DbUtil.close(ps);
//			} catch (Exception e) {
//				//表已存在
//			}
//			
			sql = "select 1 from z_analsyerectify where projectid = ? limit 1";
			ps = conn.prepareStatement(sql) ; 
			ps.setString(1, projectID);
			rs = ps.executeQuery();
			if(rs.next()){
				//表已初始化完成
				return ;
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			sql = "insert into z_analsyerectify (projectid, autoid,subjectid,assitemid,dataname,submonth,direction,AnalsyeBalance,SubYearMonth) " +
			"	select a.projectid, a.autoid,a.subjectid,ifnull(b.assitemid,''),0 as dataname,0 as submonth,dirction,occurvalue," +
			"	(substring(vchdate,1,4) - "+acc.substring(6)+") as SubYearMonth " +
			"	from z_subjectentryrectify a " +
			"	left join z_assitementryrectify b " +
			"	on b.projectid = ?" +
			"	and a.autoid = entryid " +
			"	where a.projectid = ? ";
			ps = conn.prepareStatement(sql) ; 
			ps.setString(1, projectID);
			ps.setString(2, projectID);
			ps.execute();
			DbUtil.close(ps);
			
			String sql1 = "";
			
			sql1 = "	select a.subjectid,a.AccName,a.SubjectFullName1,a.subjectfullname2,a.tokenid,b.subjectid as sid " +
			"	from c_account a,( " +
			"		select * from c_account b   " +
			"		where 1=1  " +
			"		and b.AccPackageID = "+acc+" " +
			"		and b.submonth = 1 " +
			"		and b.level1 = 1 " +
			"	) b " +
			"	where a.AccPackageID = "+acc+" " +
			"	and a.submonth = 1     " +
			"	and (a.SubjectFullName1 =b.SubjectFullName1 or a.SubjectFullName1 like concat(b.SubjectFullName1,'/%')) " +
			"	union " +
			"	select subjectid,subjectname,SubjectFullName,SubjectFullName,SubjectFullName,tipsubjectid from z_usesubject where projectid = '"+projectID+"' ";
			
			//更新　Sid、SFullName、TokenID；
			sql = "update  z_analsyerectify a,(" +
				sql1 +
			"	) b " +
			"	set a.sid = b.sid,a.SFullName = b.SubjectFullName2,a.TokenID = b.TokenID" +
			"	where a.projectid = '"+projectID+"' " +
			"	and a.subjectid = b.subjectid ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			
			//更新AssTotalName
			sql = "update  z_analsyerectify a,c_assitementryacc b " +
			"	set a.AssTotalName = b.AssTotalName1 " +
			"	where a.projectid = '"+projectID+"' " +
			"	and b.AccPackageID = '"+acc+"' " +
			"	and a.subjectid = b.accid " +
			"	and a.assitemid = b.assitemid ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			sql = "update  z_analsyerectify a set " +
			" IsSubject = if(ifnull(assitemid,'') = '','科目','核算') " +
			" where projectid = '"+projectID+"' " ;
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
		} catch (Exception e) {
			System.out.println("出错的SQL："+ sql);
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	//新的调整保存 ，又调整类型
	public void createSubject(String AccPackageID,String projectID) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try {
			ASFuntion CHF = new ASFuntion();
			try {
				//创建表
				sql = "CREATE TABLE z_rectifysubject ( " +
				"	AccPackageID varchar(20) NOT NULL COMMENT '账套编号', " +
				"	ProjectID varchar(20) NOT NULL COMMENT '项目编号', " +
				"	SubjectID varchar(50) NOT NULL COMMENT '科目编号', " +
				"	SubjectName varchar(100) default NULL COMMENT '科目名称', " +
				"	SubjectFullName1 varchar(500) default NULL COMMENT '科目全路径', " +
				"	SubjectFullName2 varchar(500) default NULL COMMENT '标准科目全路径', " +
				"	isleaf int(5) default NULL COMMENT '是否叶子', " +
				"	level0 int(5) default NULL COMMENT '层次', " +
				"	DataName varchar(100) NOT NULL COMMENT '外币名称，本位币=0', " +
				"	itemtype varchar(100) NOT NULL COMMENT '调整类型', " +
				"	yearrectify varchar(10) NOT NULL COMMENT '历年年份，本年=0，历年=-1...', " +
				"	DebitTotalOcc1 decimal(15,2) default '0.00' COMMENT '调整借', " +
				"	CreditTotalOcc1 decimal(15,2) default '0.00' COMMENT '调整贷', " +
				"	DebitTotalOcc2 decimal(15,2) default '0.00' COMMENT '重分类借', " +
				"	CreditTotalOcc2 decimal(15,2) default '0.00' COMMENT '重分类贷', " +
				"	DebitTotalOcc3 decimal(15,2) default '0.00' COMMENT '不符未调借', " +
				"	CreditTotalOcc3 decimal(15,2) default '0.00' COMMENT '不符未调贷', " +
				"	property varchar(100) default NULL, " +
				"	PRIMARY KEY  (AccPackageID,ProjectID,SubjectID,DataName,itemtype,yearrectify), " +
				"	KEY AccPackageID (AccPackageID), " +
				"	KEY ProjectID (ProjectID), " +
				"	KEY SubjectID (SubjectID), " +
				"	KEY DataName (DataName), " +
				"	KEY itemtype (itemtype), " +
				"	KEY yearrectify (yearrectify), " +
				"	KEY isleaf (isleaf), " +
				"	KEY level0 (level0), " +
				"	KEY SubjectFullName1 (SubjectFullName1), " +
				"	KEY SubjectFullName2 (SubjectFullName2) " +
				"	) ENGINE=MyISAM DEFAULT CHARSET=gbk";
				ps = conn.prepareStatement(sql);
				ps.execute();
				DbUtil.close(ps);
			} catch (Exception e) {
				//表已存在，删除本项目调整汇总
				sql = "DELETE from z_rectifysubject where projectID='" + projectID + "' ";
				ps = conn.prepareStatement(sql);
				ps.execute();
				DbUtil.close(ps);
			}
			
			System.out.println("createSubject 0 = |" + CHF.getCurrentTime());
			//汇总z_rectifysubject (本位币)
			sql = "insert into z_rectifysubject (AccPackageID, ProjectID, DataName, itemtype, yearrectify, " +
			"	SubjectID, SubjectName, SubjectFullName1, SubjectFullName2, isleaf, level0, " + 
			"	DebitTotalOcc1, CreditTotalOcc1, DebitTotalOcc2, CreditTotalOcc2, DebitTotalOcc3, CreditTotalOcc3) \n" +
			
			"	select a.accpackageid,a.projectid,'0' as DataName,a.itemtype,a.yearrectify, \n" +
			"	b.subjectid,b.SubjectName,b.SubjectFullName1,b.SubjectFullName2,b.isleaf1,b.level1, \n" +
			"	sum(DebitTotalOcc1) as DebitTotalOcc1,sum(CreditTotalOcc1) as CreditTotalOcc1, \n" +
			"	sum(DebitTotalOcc2) as DebitTotalOcc2,sum(CreditTotalOcc2) as CreditTotalOcc2, \n" +
			"	sum(DebitTotalOcc3) as DebitTotalOcc3,sum(CreditTotalOcc3) as CreditTotalOcc3 \n" +
			"	from ( \n" +
			"		select a.accpackageid,a.projectid,a.subjectid,a.itemtype,year(vchdate) - substring(accpackageid,7) as yearrectify, \n" +
			"		b.SubjectName,b.SubjectFullName1, \n" +
			"		sum(case when (a.property like '3%' or a.property like '83%' or a.property like '6%') and a.dirction=1 then OccurValue else 0 end) as DebitTotalOcc1, \n" +
			"		sum(case when (a.property like '3%' or a.property like '83%' or a.property like '6%') and a.dirction=-1 then OccurValue else 0 end) as CreditTotalOcc1, \n" +
			"		sum(case when (a.property like '4%' or a.property like '84%' or a.property like '7%') and a.dirction=1 then OccurValue else 0 end) as DebitTotalOcc2, \n" +
			"		sum(case when (a.property like '4%' or a.property like '84%' or a.property like '7%') and a.dirction=-1 then OccurValue else 0 end) as CreditTotalOcc2, \n" +
			"		sum(case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=1 then OccurValue else 0 end) as DebitTotalOcc3, \n" +
			"		sum(case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=-1 then OccurValue else 0 end) as CreditTotalOcc3 \n" +
			"		from z_subjectentryrectify a , ( \n" +
			"			select subjectID,accname as SubjectName,SubjectFullName1,SubjectFullName2,level1,isleaf1 from c_account where accpackageid='" + AccPackageID + "' and submonth=1 \n" +
			"			union  \n" +
			"			select subjectID,SubjectName,SubjectFullName,SubjectFullName,level0,isleaf from z_usesubject where accpackageid='" + AccPackageID + "'  and projectid='" + projectID + "' \n" + 
			"		) b \n" +
			"		where a.accpackageid='" + AccPackageID + "' and a.projectid='" + projectID + "' and a.subjectid=b.subjectid \n" + 
			"		group by a.subjectid,a.itemtype,year(a.vchdate)  \n" +
			"	) a ,( \n" +
			"		select subjectID,accname as SubjectName,SubjectFullName1,SubjectFullName2,level1, \n" +
			"		if(isleaf1=1 ,if((select count(*) from z_usesubject where accpackageid='" + AccPackageID + "'  and projectid='" + projectID + "' and subjectfullname like concat(a.subjectfullname1,'/%'))>0,0,isleaf1),isleaf1) as isleaf1 \n" +
			"		from c_account a where accpackageid='" + AccPackageID + "' and submonth=1  \n" +
			"		union  \n" +
			"		select subjectID,SubjectName,SubjectFullName,SubjectFullName,level0,isleaf from z_usesubject where accpackageid='" + AccPackageID + "'  and projectid='" + projectID + "' \n" + 
			"	) b  \n" +
			"	where (a.subjectfullname1=b.subjectfullname1  or a.subjectfullname1 like concat(b.subjectfullname1,'/%') ) \n" +
			"	group by b.subjectid,a.itemtype,a.yearrectify";
			System.out.println("createSubject = |" + sql);
			ps = conn.prepareStatement(sql);
			ps.execute();
			ps.close();
			System.out.println("createSubject 1 = |" + CHF.getCurrentTime());
			
			//汇总z_rectifysubject (外币)
			sql = "insert into z_rectifysubject (AccPackageID, ProjectID, DataName, itemtype, yearrectify, " +
			"	SubjectID, SubjectName, SubjectFullName1, SubjectFullName2, isleaf, level0, " + 
			"	DebitTotalOcc1, CreditTotalOcc1, DebitTotalOcc2, CreditTotalOcc2, DebitTotalOcc3, CreditTotalOcc3) \n" +
			
			"	select a.accpackageid,a.projectid,a.Currency as DataName,a.itemtype,a.yearrectify, \n" +
			"	b.subjectid,b.SubjectName,b.SubjectFullName1,b.SubjectFullName2,b.isleaf1,b.level1, \n" +
			"	sum(DebitTotalOcc1) as DebitTotalOcc1,sum(CreditTotalOcc1) as CreditTotalOcc1, \n" +
			"	sum(DebitTotalOcc2) as DebitTotalOcc2,sum(CreditTotalOcc2) as CreditTotalOcc2, \n" +
			"	sum(DebitTotalOcc3) as DebitTotalOcc3,sum(CreditTotalOcc3) as CreditTotalOcc3 \n" +
			"	from ( \n" +
			"		select a.accpackageid,a.projectid,a.subjectid,a.itemtype,a.Currency,year(vchdate) - substring(accpackageid,7) as yearrectify, \n" +
			"		b.SubjectName,b.SubjectFullName1, \n" +
			"		sum(case when (a.property like '3%' or a.property like '83%' or a.property like '6%') and a.dirction=1 then CurrValue else 0 end) as DebitTotalOcc1, \n" +
			"		sum(case when (a.property like '3%' or a.property like '83%' or a.property like '6%') and a.dirction=-1 then CurrValue else 0 end) as CreditTotalOcc1, \n" +
			"		sum(case when (a.property like '4%' or a.property like '84%' or a.property like '7%') and a.dirction=1 then CurrValue else 0 end) as DebitTotalOcc2, \n" +
			"		sum(case when (a.property like '4%' or a.property like '84%' or a.property like '7%') and a.dirction=-1 then CurrValue else 0 end) as CreditTotalOcc2, \n" +
			"		sum(case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=1 then CurrValue else 0 end) as DebitTotalOcc3, \n" +
			"		sum(case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=-1 then CurrValue else 0 end) as CreditTotalOcc3 \n" +
			"		from z_subjectentryrectify a , ( \n" +
			"			select subjectID,accname as SubjectName,SubjectFullName1,SubjectFullName2,level1,isleaf1 from c_account where accpackageid='" + AccPackageID + "' and submonth=1 \n" +
			"			union  \n" +
			"			select subjectID,SubjectName,SubjectFullName,SubjectFullName,level0,isleaf from z_usesubject where accpackageid='" + AccPackageID + "'  and projectid='" + projectID + "' \n" + 
			"		) b \n" +
			"		where a.accpackageid='" + AccPackageID + "' and a.projectid='" + projectID + "' and a.subjectid=b.subjectid \n" + 
			"		group by a.subjectid,a.itemtype,year(a.vchdate),a.Currency  \n" +
			"	) a ,( \n" +
			"		select subjectID,accname as SubjectName,SubjectFullName1,SubjectFullName2,level1, \n" +
			"		if(isleaf1=1 ,if((select count(*) from z_usesubject where accpackageid='" + AccPackageID + "'  and projectid='" + projectID + "' and subjectfullname like concat(a.subjectfullname1,'/%'))>0,0,isleaf1),isleaf1) as isleaf1 \n" +
			"		from c_account a where accpackageid='" + AccPackageID + "' and submonth=1  \n" +
			"		union  \n" +
			"		select subjectID,SubjectName,SubjectFullName,SubjectFullName,level0,isleaf from z_usesubject where accpackageid='" + AccPackageID + "'  and projectid='" + projectID + "' \n" + 
			"	) b  \n" +
			"	where (a.subjectfullname1=b.subjectfullname1  or a.subjectfullname1 like concat(b.subjectfullname1,'/%') ) \n" +
			"	group by b.subjectid,a.itemtype,a.yearrectify,a.Currency";
			ps = conn.prepareStatement(sql);
			ps.execute();
			ps.close();
			System.out.println("createSubject 2 = |" + CHF.getCurrentTime());
			
		} catch (Exception e) {
			System.out.println("出错的SQL："+ sql);
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
	}

	//新的调整保存 ，又调整类型(核算)
	public void createAssitemNew(String AccPackageID,String projectID) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try {
			ASFuntion CHF = new ASFuntion();
			try {
				//创建表
				sql = "CREATE TABLE z_rectifyassitem ( " +
				"	AccPackageID varchar(20) NOT NULL COMMENT '账套编号', " +
				"	ProjectID varchar(20) NOT NULL COMMENT '项目编号', " +
				"	SubjectID varchar(50) NOT NULL COMMENT '科目编号', " +
				"	AssItemID varchar(100) NOT NULL COMMENT '核算编号', " +
				"	AssItemName varchar(100) default NULL COMMENT '核算名称', " +
				"	SubjectFullName1 varchar(500) default NULL COMMENT '科目全路径(显示)', " +
				"	SubjectFullName2 varchar(500) default NULL COMMENT '标准科目全路径(显示)', " +
				"	isleaf int(5) default NULL COMMENT '是否叶子', " +
				"	level0 int(5) default NULL COMMENT '层次', " +
				"	DataName varchar(100) NOT NULL COMMENT '外币名称，本位币=0', " +
				"	itemtype varchar(100) NOT NULL COMMENT '调整类型', " +
				"	yearrectify varchar(10) NOT NULL COMMENT '历年年份，本年=0，历年=-1...', " +
				"	DebitTotalOcc1 decimal(15,2) default '0.00' COMMENT '调整借', " +
				"	CreditTotalOcc1 decimal(15,2) default '0.00' COMMENT '调整贷', " +
				"	DebitTotalOcc2 decimal(15,2) default '0.00' COMMENT '重分类借', " +
				"	CreditTotalOcc2 decimal(15,2) default '0.00' COMMENT '重分类贷', " +
				"	DebitTotalOcc3 decimal(15,2) default '0.00' COMMENT '不符未调借', " +
				"	CreditTotalOcc3 decimal(15,2) default '0.00' COMMENT '不符未调贷', " +
				"	property varchar(100) default NULL, " +
				"	PRIMARY KEY  (AccPackageID,ProjectID,SubjectID,AssItemID,DataName,itemtype,yearrectify), " +
				"	KEY AccPackageID (AccPackageID), " +
				"	KEY ProjectID (ProjectID), " +
				"	KEY SubjectID (SubjectID), " +
				"	KEY AssItemID (AssItemID), " +
				"	KEY DataName (DataName), " +
				"	KEY itemtype (itemtype), " +
				"	KEY yearrectify (yearrectify), " +
				"	KEY isleaf (isleaf), " +
				"	KEY level0 (level0), " +
				"	KEY SubjectFullName1 (SubjectFullName1), " +
				"	KEY SubjectFullName2 (SubjectFullName2) " +
				"	) ENGINE=MyISAM DEFAULT CHARSET=gbk";
				ps = conn.prepareStatement(sql);
				ps.execute();
				DbUtil.close(ps);
			} catch (Exception e) {
				//表已存在，删除本项目调整汇总
				sql = "DELETE from z_rectifyassitem where projectID='" + projectID + "' ";
				ps = conn.prepareStatement(sql);
				ps.execute();
				DbUtil.close(ps);
			}
			
			System.out.println("createAssitemNew 0 = |" + CHF.getCurrentTime());
			sql = "insert into z_rectifyassitem (AccPackageID, ProjectID, DataName, itemtype, yearrectify, " +
			"	SubjectID, SubjectFullName1, SubjectFullName2, isleaf, level0,  " +
			"	AssItemID, AssItemName,  " +
			"	DebitTotalOcc1, CreditTotalOcc1, DebitTotalOcc2, CreditTotalOcc2, DebitTotalOcc3, CreditTotalOcc3) \n" +
			
			"	select a.accpackageid,a.projectid,'0' as DataName,a.itemtype,year(vchdate) - substring(a.accpackageid,7) as yearrectify, \n" + 
			"	a.subjectid,b.SubjectFullName1, b.SubjectFullName2,b.isleaf1,b.level1, \n" +
			"	d.assitemid,d.assitemname, \n" +
			"	sum(case when (a.property like '3%' or a.property like '83%' or a.property like '6%') and a.dirction=1 then OccurValue else 0 end) as DebitTotalOcc1, \n" + 
			"	sum(case when (a.property like '3%' or a.property like '83%' or a.property like '6%') and a.dirction=-1 then OccurValue else 0 end) as CreditTotalOcc1,  \n" +
			"	sum(case when (a.property like '4%' or a.property like '84%' or a.property like '7%') and a.dirction=1 then OccurValue else 0 end) as DebitTotalOcc2,  \n" +
			"	sum(case when (a.property like '4%' or a.property like '84%' or a.property like '7%') and a.dirction=-1 then OccurValue else 0 end) as CreditTotalOcc2,  \n" +
			"	sum(case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=1 then OccurValue else 0 end) as DebitTotalOcc3, \n" + 
			"	sum(case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=-1 then OccurValue else 0 end) as CreditTotalOcc3  \n" +
			"	from z_subjectentryrectify a, ( \n" + 
			"		select subjectID,accname as SubjectName,SubjectFullName1,SubjectFullName2,level1,isleaf1 from c_account where accpackageid='" + AccPackageID + "' and submonth=1 \n" + 
			"		union   \n" +
			"		select subjectID,SubjectName,SubjectFullName,SubjectFullName,level0,isleaf from z_usesubject where accpackageid='" + AccPackageID + "'  and projectid='" + projectID + "' \n" +  
			"	) b,z_assitementryrectify c ,c_assitementryacc d \n" +
			"	where a.projectid='" + projectID + "' and c.projectid='" + projectID + "' \n" +
			"	and d.accpackageId= '" + AccPackageID + "' and d.submonth=1 \n" +
			"	and a.subjectid=b.subjectid   \n" +
			"	and b.subjectid = c.subjectid \n" +
			"	and b.subjectid =d.accid \n" +
			"	and a.autoid=c.entryId \n" +
			"	and c.subjectid = d.accid  \n" +
			"	and c.assitemid=d.assitemid \n" +
			"	group by a.subjectid,c.assitemid,a.itemtype,year(a.vchdate) ";
			System.out.println("createSubject = |" + sql);
			ps = conn.prepareStatement(sql);
			ps.execute();
			ps.close();
			System.out.println("createAssitemNew 1 = |" + CHF.getCurrentTime());
			
			//汇总z_rectifyassitem (外币)
			sql = "insert into z_rectifyassitem (AccPackageID, ProjectID, DataName, itemtype, yearrectify, " +
			"	SubjectID, SubjectFullName1, SubjectFullName2, isleaf, level0,  " +
			"	AssItemID, AssItemName,  " +
			"	DebitTotalOcc1, CreditTotalOcc1, DebitTotalOcc2, CreditTotalOcc2, DebitTotalOcc3, CreditTotalOcc3) \n" +
			
			"	select a.accpackageid,a.projectid,a.Currency as DataName,a.itemtype,year(vchdate) - substring(a.accpackageid,7) as yearrectify, \n" + 
			"	a.subjectid,b.SubjectFullName1, b.SubjectFullName2,b.isleaf1,b.level1, \n" +
			"	d.assitemid,d.assitemname, \n" +
			"	sum(case when (a.property like '3%' or a.property like '83%' or a.property like '6%') and a.dirction=1 then CurrValue else 0 end) as DebitTotalOcc1, \n" +
			"	sum(case when (a.property like '3%' or a.property like '83%' or a.property like '6%') and a.dirction=-1 then CurrValue else 0 end) as CreditTotalOcc1, \n" +
			"	sum(case when (a.property like '4%' or a.property like '84%' or a.property like '7%') and a.dirction=1 then CurrValue else 0 end) as DebitTotalOcc2, \n" +
			"	sum(case when (a.property like '4%' or a.property like '84%' or a.property like '7%') and a.dirction=-1 then CurrValue else 0 end) as CreditTotalOcc2, \n" +
			"	sum(case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=1 then CurrValue else 0 end) as DebitTotalOcc3, \n" +
			"	sum(case when (a.property like '5%' or a.property like '6%' or a.property like '7%' or a.property like '8%') and a.dirction=-1 then CurrValue else 0 end) as CreditTotalOcc3 \n" +
			"	from z_subjectentryrectify a, ( \n" + 
			"		select subjectID,accname as SubjectName,SubjectFullName1,SubjectFullName2,level1,isleaf1 from c_account where accpackageid='" + AccPackageID + "' and submonth=1 \n" + 
			"		union   \n" +
			"		select subjectID,SubjectName,SubjectFullName,SubjectFullName,level0,isleaf from z_usesubject where accpackageid='" + AccPackageID + "'  and projectid='" + projectID + "' \n" +  
			"	) b,z_assitementryrectify c ,c_assitementryacc d \n" +
			"	where a.projectid='" + projectID + "' and c.projectid='" + projectID + "' \n" +
			"	and d.accpackageId= '" + AccPackageID + "' and d.submonth=1 \n" +
			"	and a.subjectid=b.subjectid   \n" +
			"	and b.subjectid = c.subjectid \n" +
			"	and b.subjectid =d.accid \n" +
			"	and a.autoid=c.entryId \n" +
			"	and c.subjectid = d.accid  \n" +
			"	and c.assitemid=d.assitemid \n" +
			"	group by a.subjectid,c.assitemid,a.itemtype,year(a.vchdate),a.Currency ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			ps.close();
			System.out.println("createAssitemNew 2 = |" + CHF.getCurrentTime());
			
		} catch (Exception e) {
			System.out.println("出错的SQL："+ sql);
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public static void main(String[] args) {
		
		System.out.println("6001".substring(0, 1));
//		int ch;  
//		String str = "B";
//		
//		for(ch='A';ch<='Z';ch++)  {
//			if(ch == 'B'){
//				System.out.println("111="+(char)ch + "|"+String.valueOf((char)ch));
//			}
//			if(String.valueOf((char)ch).equals(str)){
//				System.out.println("222="+(char)ch);
//			}
////			System.out.println("字母%c的ACSII码值是:" + ch);
//		}
//
//		System.out.println("10021401:|" + new	RectifyService(null).getNewID("10021401", "1002" ,""));
//		System.out.println("101507:|" + new	RectifyService(null).getNewID("101507", "1015" ,""));
		
//		System.out.println("5181A:|" + new	RectifyService(null).getNewID("5181A", "5181" ,""));
//		System.out.println("5181Z:|" + new	RectifyService(null).getNewID("5181Z", "5181" ,""));
//		System.out.println("5181ZA:|" + new	RectifyService(null).getNewID("5181ZA", "5181","" ));
		
	}
	

}
