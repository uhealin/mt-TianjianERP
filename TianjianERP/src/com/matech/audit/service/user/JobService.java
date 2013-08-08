package com.matech.audit.service.user;

import java.io.File;
import java.io.PrintWriter;
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

import com.matech.audit.service.attachFileUploadService.AttachService;
import com.matech.audit.service.attachFileUploadService.model.Attach;
import com.matech.audit.service.manuscript.ManuFileService;
import com.matech.audit.service.user.model.ReportDutFlow;
import com.matech.framework.pub.autocode.DELAutocode;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class JobService {
	
	private Connection conn=null;

	public JobService(Connection conn) {
        this.conn=conn;
    }
	
	//=============================================
	//通用单表新增、修改、删除、查询
	//=============================================
	/**
	 * 新增记录
	 * @param table : 表名 
	 * @param field : 自动主键;新增时去掉自动主键,没有自动主键可以为空
	 * @param map : 新增记录,K=表的字段名(小写),V=表单的值
	 */
	public void add(String table,String field,Map map) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;		
		try {
			ASFuntion CHF = new ASFuntion();
			String sql = "",sql1 = "",sql2 = "";
			sql = "select * from "+table+" where 1=2 " ;
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			ResultSetMetaData RSMD = rs.getMetaData();
			for (int i = 1; i <= RSMD.getColumnCount(); i++) {
				if(!CHF.showNull(field).toLowerCase().equals(RSMD.getColumnLabel(i).toLowerCase()) 
					){
					sql1 += ","+RSMD.getColumnLabel(i).toLowerCase()+"";
					sql2 += ",?";
				}
			}

			sql = "insert into "+table+" ("+sql1.substring(1)+") values ("+sql2.substring(1)+") ";
			ps = conn.prepareStatement(sql);
			int ii = 1;
			for (int i = 1; i <= RSMD.getColumnCount(); i++) {
				if(!CHF.showNull(field).toLowerCase().equals(RSMD.getColumnLabel(i).toLowerCase()) 
				){
					String string = (String)map.get(RSMD.getColumnLabel(i).toLowerCase());
					ps.setString(ii, (string == null) ? "" : string );
					ii++;
				}
			}
			System.out.println(sql);
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 修改记录
	 * @param table : 表名 
	 * @param field : 主键字段名 
	 * @param map : 修改记录,K=表的字段名(小写),V=表单的值
	 */
	public void update(String table,String field,Map map) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;		
		try {
			ASFuntion CHF = new ASFuntion();
			String sql = "",sql1 = "";
			sql = "select * from "+table+" where 1=2 " ;
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			ResultSetMetaData RSMD = rs.getMetaData();
			for (int i = 1; i <= RSMD.getColumnCount(); i++) {
				if(!CHF.showNull(field).toLowerCase().equals(RSMD.getColumnLabel(i).toLowerCase()) 
					){
					sql1 += ","+RSMD.getColumnLabel(i).toLowerCase()+" = ? ";
				}
			}
			
			sql = "update "+table+" set " + sql1.substring(1) + " where "+field+" = ? ";
			ps = conn.prepareStatement(sql);
			int ii = 1;
			for (int i = 1; i <= RSMD.getColumnCount(); i++) {
				if(!CHF.showNull(field).toLowerCase().equals(RSMD.getColumnLabel(i).toLowerCase()) 
				){
					String string = (String)map.get(RSMD.getColumnLabel(i).toLowerCase());
					ps.setString(ii, (string == null) ? "" : string );
					ii++;
				}
			}
			ps.setString(ii, (String)map.get(CHF.showNull(field).toLowerCase()) );
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}	
	}
	
	/**
	 * 删除记录
	 * @param table : 表名 
	 * @param field : 主键字段名 
	 * @param value : 主键的值
	 */
	public void del(String table,String field,String value) throws Exception{
		PreparedStatement ps = null;
		try {
			String sql = "DELETE FROM "+table+" WHERE "+field+" = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, value);
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 查询记录
	 * @param table : 表名 
	 * @param field : 主键字段名 
	 * @param value : 主键的值
	 * @return Map ：K=表的字段名(小写),V=表单的值
	 */
	public Map get(String table,String field,String value) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;		
		try {
			Map map = new HashMap();
			String sql = "select * from "+table+" where "+field+"=? ";
			ps = conn.prepareStatement(sql);
	        ps.setString(1, value);
			rs = ps.executeQuery();
			ResultSetMetaData RSMD = rs.getMetaData();	
			if(rs.next()){
				for (int i = 1; i <= RSMD.getColumnCount(); i++) {
					map.put(RSMD.getColumnLabel(i).toLowerCase() , rs.getObject(RSMD.getColumnLabel(i)));
				}
			}
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
	}
	
	/**
	 * 查询记录
	 * @param table : 表名 
	 * @param field : 主键字段名 
	 * @param value : 主键的值
	 * @param returnField : 要返回的字段值
	 */
	public Object get(String table,String field,String value,String returnField) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;		
		try {
			String sql = "select * from "+table+" where "+field+"=? ";
			ps = conn.prepareStatement(sql);
	        ps.setString(1, value);
			rs = ps.executeQuery();
			if(rs.next()){
				return rs.getObject(returnField);
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
	}
	
	/**
	 * 查询记录 多条记录
	 * @param table : 表名 
	 * @param field : 主键字段名 
	 * @param value : 主键的值
	 * @return Map ：K=表的字段名(小写),V=表单的值
	 */
	public List getList(String table,String field,String value) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;		
		try {
			List list = new ArrayList();
			String sql = "select * from "+table+" where "+field+"=? ";
			ps = conn.prepareStatement(sql);
	        ps.setString(1, value);
			rs = ps.executeQuery();
			ResultSetMetaData RSMD = rs.getMetaData();	
			while(rs.next()){
				Map map = new HashMap();
				for (int i = 1; i <= RSMD.getColumnCount(); i++) {
					map.put(RSMD.getColumnLabel(i).toLowerCase() , rs.getObject(RSMD.getColumnLabel(i)));
				}
				list.add(map);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
	}
	
	//临时表tt_k_resume
	public void newTable(String table) throws Exception {
		PreparedStatement ps = null;
		try {
			dropTable("tt_"+table);
			String sql = "CREATE TABLE tt_"+table+" like " + table;
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			sql = "alter table tt_"+table+" drop primary key";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
		}
	}
	
	//删除表
	public void dropTable(String table) throws Exception {
		PreparedStatement ps = null;
		try {
			String sql = "DROP TABLE IF EXISTS "+table;
			ps = conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
		}

	}
	
	//检查、更新
	public void checkUpData(PrintWriter out,String userId,String uploadtemppath)throws Exception{
		PreparedStatement ps = null;
		PreparedStatement ps1 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		String sql = "";
		try {
			ASFuntion CHF = new ASFuntion();
			String sql1 = "",sql2 = "";
			
			ManuFileService mfs = new ManuFileService();
			AttachService attachService = new AttachService(conn);
			DbUtil db = new DbUtil(conn);
			
			//删除所有姓名为空的记录
			sql = "delete from tt_k_resume where ifnull(name,'')=''   ";
			db.execute(sql);
			out.println("<br>删除所有姓名为空的记录");
			out.flush();
			
			sql = "update tt_k_resume set unid = uuid() ";
			db.execute(sql);
			
			//得到附件的文件列表
			String indexTable = "k_resume";
			String attachFilePath = AttachService.ATTACH_FILE_PATH;
			if (!"".equals(indexTable)) {
				attachFilePath += indexTable + "/";
			} else {
				attachFilePath += AttachService.ATTACH_FILE_DEFAULT_FOLDER;
			}
			
			File oldDir = new File(uploadtemppath);
			File[] files = null;
			if (oldDir.isDirectory()) {
				files = oldDir.listFiles();
			}
			
			out.println("<br>移动简历的附件文件...");
			out.flush();
			
			sql = "select a.*,b.resumeid as bresumeid,b.attachid as battachid " +
			"	from tt_k_resume a" +
			"	left join k_resume b " +
			"	on a.name = b.name " +
			"	and a.paperstype = b.paperstype " +
			"	and a.papersnumber = b.papersnumber " ;
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				String unid = rs.getString("unid");
				String name = rs.getString("name");
				String papersnumber = rs.getString("papersnumber");
				String fileName = name + "("+ papersnumber + ")";
				
				String bresumeid = CHF.showNull(rs.getString("bresumeid"));
				String battachid = CHF.showNull(rs.getString("battachid"));
				
				String resumeid = "",indexId = "";
				if("".equals(bresumeid)){
					resumeid = getAutoCode("1");
				}else{
					resumeid = bresumeid;
				}
				if("".equals(battachid)){
					indexId = UUID.randomUUID().toString();
				}else{
					indexId = battachid;
				}
				
				sql = "update tt_k_resume set resumeid = ?,attachid=? where unid = ? ";
				db.execute(sql,new String[]{resumeid,indexId,unid});
				
				// 生成UUID作为文件名
				String attachId = UUID.randomUUID().toString();

				// 如果不指定模块，则放到
				System.out.println("attachFilePath="+attachFilePath);
				System.out.println("uploadtemppath="+uploadtemppath);
				
				//附件
				for (int i = 0; i < files.length; i++) {
					if(files[i].getName().indexOf(fileName) > -1){
						//删除已有的同名文件
						sql = "select * from k_attachext where indexid = ? and attachname = ? ";
						ps1 = conn.prepareStatement(sql);
						ps1.setString(1, indexId);
						ps1.setString(2, files[i].getName());
						rs1 = ps1.executeQuery();
						while(rs1.next()){
							String attachId1 = rs1.getString("attachId"); 
							Attach attach = attachService.getAttach(attachId1);
							attachService.remove(attach);
						}
						
						//是同一个人的附件
						File file = new File(attachFilePath + "/"+ attachId);
						mfs.copyFiles(files[i], file);
						
						//插入附件表信息
						Attach attach = new Attach();
						attach.setAttachFile(attachId);
						attach.setAttachFilePath(attachFilePath);
						attach.setAttachId(attachId);
						attach.setAttachName(files[i].getName());
						attach.setUpdateTime(CHF.getCurrentDate() + " " + CHF.getCurrentTime());
						attach.setUpdateUser(userId);
						attach.setIndexTable(indexTable);
						attach.setIndexId(indexId);
						attach.setFileSize(file.length());

						attachService.save(attach);
						
						break;
					}
				}
				
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			out.println("移动成功");
			out.flush();
			
			//检查简历是否已存在
			sql = "select * from tt_k_resume where 1=2 ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			ResultSetMetaData RSMD = rs.getMetaData();
			for (int i = 1; i <= RSMD.getColumnCount(); i++) {
				sql1 += ","+RSMD.getColumnLabel(i).toLowerCase()+"";
				if(!CHF.showNull("unid").toLowerCase().equals(RSMD.getColumnLabel(i).toLowerCase())
					&& !CHF.showNull("resumeid").toLowerCase().equals(RSMD.getColumnLabel(i).toLowerCase())
					){
					sql2 += ",a."+RSMD.getColumnLabel(i).toLowerCase()+" = if(ifnull(b."+RSMD.getColumnLabel(i).toLowerCase()+",'')='',a."+RSMD.getColumnLabel(i).toLowerCase()+",ifnull(b."+RSMD.getColumnLabel(i).toLowerCase()+",''))";
				}
			}
			sql1 = "".equals(sql1) ? "" : sql1.substring(1);
			sql2 = "".equals(sql2) ? "" : sql2.substring(1);
			
			//更新已有信息
			out.println("<br>更新所有已经存在的简历信息...");
			out.flush();
			sql = "update k_resume a join ( " +
			"		select a.* ,b.unid as bunid " +
			"		from tt_k_resume a " +
			"		left join k_resume b " +
			"		on a.name = b.name " +
			"		and a.paperstype = b.paperstype " +
			"		and a.papersnumber = b.papersnumber " +
			"		where b.name is not null " +
			"	) b  " +
			"	on a.unid = b.bunid" +
			"	set " + sql2;
			db.execute(sql);
			out.println("更新成功");
			out.flush();
			
			//检查新增
			out.println("<br>插入新增的简历信息...");
			out.flush();
			sql = "insert into k_resume(" + sql1 + ") " +
			"	select " + sql1 + " from (" +
			"		select a.* " +
			"		from tt_k_resume a " +
			"		left join k_resume b " +
			"		on a.name = b.name " +
			"		and a.paperstype = b.paperstype " +
			"		and a.papersnumber = b.papersnumber " +
			"		where b.name is null order by a.resumeid" +
			"	) a ";
			db.execute(sql);
			out.println("插入新增成功");
			out.flush();
			
			//移动附件文件
			
			
			
		} catch (Exception e) {
			System.out.println("error sql="+sql);
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	//生成自动编号 opt用于标志自增时是否要考虑临时表
	public String getAutoCode(String opt)throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;	
		try{
			ASFuntion CHF = new ASFuntion();
			
			DbUtil db = new DbUtil(conn);
			String sql = "";
			sql = "select substring(resumeid,1,8) as resumeid from k_resume where 1=1 order by resumeid desc limit 1"; //求出表中最大值
			
			if(!"".equals(opt)){
				sql = "select * from (" +
				"	select substring(resumeid,1,8) as resumeid from k_resume " +
				"	union " +
				"	select substring(resumeid,1,8) as resumeid from tt_k_resume" +
				") a order by resumeid desc limit 1";
			}
			
			String oldDate = CHF.showNull(db.queryForString(sql)); //最大的天数
			
			String newDate = CHF.replaceStr(CHF.getCurrentDate(), "-", ""); //当天
			if(!newDate.equals(oldDate)){
				//不是同一天k_autocode的curnum1 = 0
				sql = "update k_autocode set curnum1 = 0 where atype = 'JLXX' ";
				db.execute(sql);
			}
			
			return new DELAutocode().getAutoCode("JLXX","",new String[]{newDate});
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	//简历树:得到岗位树
	public List getJobTree()throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;	
		try {
			List list = new ArrayList();
			String sql = "select distinct jobname from k_resume order by jobname";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			int opt = 0;
			while(rs.next()){
				String jobname = rs.getString("jobname");
				Map map = new HashMap();
				map.put("id", "job"+opt);
				map.put("text", jobname);
				map.put("leaf", false);
				map.put("cls", "folder");
				map.put("jobname", jobname);
				
				list.add(map);
				opt++;
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
	}
	
	//简历树:得到人员树
	public List getResumeTree(String jobname)throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;	
		try {
			List list = new ArrayList();
			String sql = "select * from k_resume where jobname = ?  order by name";
			ps = conn.prepareStatement(sql);
			ps.setString(1, jobname);
			rs = ps.executeQuery();
			int opt = 0;
			while(rs.next()){
				String name = rs.getString("name");
				String paperstype = rs.getString("paperstype");
				String papersnumber = rs.getString("papersnumber");
				String mobilephone = rs.getString("mobilephone");
				String email = rs.getString("email");
				
				Map map = new HashMap();
				map.put("id", "resume"+opt);
				map.put("text", name);
				map.put("leaf", true);
				map.put("cls", "folder");
				map.put("checked", false);
				
				map.put("name", name);
				map.put("paperstype", paperstype);
				map.put("papersnumber", papersnumber);
				map.put("mobilephone", mobilephone);
				map.put("email", email);
				
				list.add(map);
				opt++;
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
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
				value += rs.getString(1);
			}
			
		} catch (SQLException e) {
		
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return value;
	}
	
	/**
	 * 添加流程信息
	 * @param leaveFlow
	 * @return
	 */
	public boolean addProcss(ReportDutFlow reportDutFlow) {
		int i = 1;
		boolean result = false;
		PreparedStatement ps = null;
		String sql = "insert j_reportDutProcss (ProcessInstanceId,unid,Applyuser,ApplyDate,State,Property) "
				+ "value (?,?,?,?,?,?)";
		try {
			ps = conn.prepareStatement(sql);

			ps.setString(i++, reportDutFlow.getProcessInstanceId());
			ps.setString(i++, reportDutFlow.getUnid());
			ps.setString(i++, reportDutFlow.getApplyuser());
			ps.setString(i++, reportDutFlow.getApplyDate());
			ps.setString(i++, reportDutFlow.getState());
			ps.setString(i++, reportDutFlow.getProperty());

			result = ps.execute();

			result = true;
		} catch (SQLException e) {

			System.out.println("新增流程信息失败service:" + e.getMessage());
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}

		return result;
	}
	
	/*
	 * 把入职申请表的信息插到k_user
	 */
	public void insertToUser(String autoId,String loginId){
		PreparedStatement ps = null;
		try {
			String sql="INSERT INTO `asdb`.`k_user` "
						+"(`id`,`Name`,`loginid`,`Password`,`Sex`,`BornDate`,`Educational`,`Diploma`,`DepartID`,`Rank`," 
						+"`Post`,`Specialty`,`ParentGroup`,`Popedom`,`IsTips`,`departmentid`,`ProjectPopedom`,`clientDogSysUi`," 
						+"`state`,`userPhoto`,`userPhotoTemp`,`mobilePhone`,`phone`,`email`,`cpano`,`floor`,`house`,`station`," 
						+"`identityCard`,`paperstype`,`papersnumber`,`nation`,`marriage`,`place`,`residence`,`politics`," 
						+"`partytime`,`relationships`,`profession`,`compact`,`workstate`,`leavetype`,`english`,`diplomatime`," 
						+"`entrytime`,`ip`,`forbiddenDate`,`resume`) "
						+" SELECT `id`,`Name`,`"+loginId+"`,`md5('"+loginId+"')`,`Sex`,`BornDate`,`Educational`,`Diploma`,`DepartID`,`Rank`,"
						+"`Post`,`Specialty`,`ParentGroup`,`Popedom`,`IsTips`,`departmentid`,`ProjectPopedom`,`clientDogSysUi`,"
						+"'0',`userPhoto`,`userPhotoTemp`,`mobilePhone`,`phone`,`email`,`cpano`,`floor`,`house`,`station`,"
						+"`identityCard`,`paperstype`,`papersnumber`,`nation`,`marriage`,`place`,`residence`,`politics`,"
						+"`partytime`,`relationships`,`profession`,`compact`,`workstate`,`leavetype`,`english`,`diplomatime`,"
						+"`entrytime`,`ip`,`forbiddenDate`,`resume` "
						+" FROM `k_staffregister`  WHERE AUTOID = "+autoId;
			ps = conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}  finally {
			DbUtil.close(ps);
		}
	}
}
