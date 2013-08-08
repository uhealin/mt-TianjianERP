package com.matech.audit.service.usersubject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.framework.pub.db.DbUtil;

public class ComeService {
	
	private Connection conn;

	public ComeService(Connection conn){
		this.conn = conn;
	}
	
	public void save(String acc,String SubjectID) throws Exception{
		PreparedStatement ps = null;
		try {
			String sql = "update c_accpkgsubject a left join c_accpkgsubject b on a.AccPackageID='"+acc+"' and b.AccPackageID='"+acc+"' and b.SubjectID='"+SubjectID+"' and (a.SubjectFullName like concat(b.SubjectFullName,'/%') or a.SubjectFullName =b.SubjectFullName) set a.AssistCode = '1' where  b.SubjectID is not null ";
			ps = conn.prepareStatement(sql);
			ps.execute();			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
		}
	}
	
	public void del(String acc,String SubjectID) throws Exception{
		PreparedStatement ps = null;
		try {
			String sql = "update c_accpkgsubject a left join c_accpkgsubject b on a.AccPackageID='"+acc+"' and b.AccPackageID='"+acc+"' and b.SubjectID='"+SubjectID+"' and (a.SubjectFullName like concat(b.SubjectFullName,'/%') or a.SubjectFullName =b.SubjectFullName) set a.AssistCode = '' where  b.SubjectID is not null ";
			ps = conn.prepareStatement(sql);
			ps.execute();	
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
		}
	}
	
	public void AssistCode(String customerid)throws Exception{
		
		PreparedStatement ps = null;
		try {
			String [] key = new String[]{"应收账款","应收款项","其他应收款","预收账款","应付账款","应付款项","其他应付款","预付账款","预付款项","预收款项"};
			String string = "";
			for (int i = 0; i < key.length; i++) {
				string += "'" + key[i] + "',";
			}
			string = string.substring(0,string.length()-1);
			
			String sql = "update c_accpkgsubject set AssistCode='' where  substring(accpackageid,1,6) =  '"+customerid+"'";
			ps = conn.prepareStatement(sql);
			ps.execute();
			
			sql = "update  c_accpkgsubject a  join (select userkey from z_keyresult a left join (" +
					" select * from asdb.k_standsubject where vocationid = (select vocationid from asdb.k_customer where departid = '"+customerid+"') " +
					" and subjectName in("+string+")) b on a.standkey = b.subjectName where  ifnull(b.subjectName,'') <> '' ) b on a.SubjectFullName like concat(b.userkey,'%') " +
					" set a.AssistCode=1 where substring(accpackageid,1,6) =  '"+customerid+"'";
			ps = conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
		}
		
	}
	
	public void AssistCode(String customerid,String opt)throws Exception{
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			
			
			String [] key = new String[]{"应收账款","其他应收款","预收账款","应付账款","其他应付款","预付账款"};
			String string = "";
			for (int i = 0; i < key.length; i++) {
				string += "'" + key[i] + "',";
			}
			string = string.substring(0,string.length()-1);
			
//			String sql = "update c_accpkgsubject set AssistCode='' where  substring(accpackageid,1,6) =  '"+customerid+"'";
//			ps = conn.prepareStatement(sql);
//			ps.execute();
			
			String sql = "update  c_accpkgsubject a  join (select userkey from z_keyresult a left join (" +
					" select * from asdb.k_standsubject where vocationid = (select vocationid from asdb.k_customer where departid = '"+customerid+"') " +
					" and subjectName in("+string+")) b on a.standkey = b.subjectName where  b.subjectName is not null ) b on a.SubjectFullName like concat(b.userkey,'%') " +
					" set a.AssistCode=1 where substring(accpackageid,1,6) =  '"+customerid+"'";
			ps = conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
	}
	
	public String isAssistCode(String acc )throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String bool = "0";
			String sql = "select 1 from c_accpkgsubject where AccPackageID='"+acc+"' and AssistCode<>'' limit 1";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				bool = "1";
			}
			return bool;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
}
