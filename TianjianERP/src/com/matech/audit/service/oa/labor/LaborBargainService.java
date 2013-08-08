package com.matech.audit.service.oa.labor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.oa.labor.model.LaborBargain;
import com.matech.audit.service.oa.worknote.model.worknoteTable;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.exception.MatechException;
import com.matech.framework.pub.util.Debug;

public class LaborBargainService {

	private Connection conn = null;

	public LaborBargainService(Connection conn) {

		this.conn = conn;
	}

	/**
	 * 增加
	 * 
	 * @param laborbargain
	 * @return
	 * @throws Exception
	 */
	public boolean addlabor(LaborBargain laborbargain) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;

		try {
			String sql = "insert into oa_workbargain \n"
					+ "(bargainID,bargainperson,endorsedate,emolument,ineffecttime,other,checkinperson,checkindate,userid," 
					+ "uploadFileName, \n" 
					+ "uploadTempName,Bargaintype,trialtime \n)"
					+ "values(?,?,?,?,?,?,?,now(),?,?,?,?,?)";

			ps = conn.prepareStatement(sql);

			ps.setString(1, laborbargain.getBargainID());
			ps.setString(2, laborbargain.getBargainperson());
			ps.setString(3, laborbargain.getEndorsedate());
			ps.setString(4, laborbargain.getEmolument());
			ps.setString(5, laborbargain.getIneffecttime());
			ps.setString(6, laborbargain.getOther());
			ps.setString(7, laborbargain.getCheckinperson());
			ps.setString(8, laborbargain.getUserid());
			ps.setString(9,	laborbargain.getFileNames()) ;
			ps.setString(10,laborbargain.getFileRondomNames()) ;
			ps.setString(11,laborbargain.getBargaintype()) ;
			ps.setString(12,laborbargain.getTrialtime()) ;
			ps.execute();

			return true;

		} catch (Exception e) {
			e.printStackTrace();

		} finally {

			DbUtil.close(ps);
		}

		return false;

	}

	/**
	 * 修改
	 * 
	 * @param laborbargain
	 * @param autoid
	 * @return
	 * @throws Exception
	 */
	public boolean updatelabor(LaborBargain laborbargain, String autoid)
			throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;

		try {

			String sql = "update oa_workbargain set bargainID =?,bargainperson=?,endorsedate=?,emolument=?,ineffecttime=?,other=?,userid=?,uploadFileName=?,uploadTempName=?,bargaintype=?,trialtime=? where autoid=?";

			ps = conn.prepareStatement(sql);

			ps.setString(1, laborbargain.getBargainID());

			ps.setString(2, laborbargain.getBargainperson());

			ps.setString(3, laborbargain.getEndorsedate());

			ps.setString(4, laborbargain.getEmolument());

			ps.setString(5, laborbargain.getIneffecttime());

			ps.setString(6, laborbargain.getOther());

			ps.setString(7, laborbargain.getUserid());
			
			ps.setString(8,laborbargain.getFileNames()) ;
			ps.setString(9,laborbargain.getFileRondomNames()) ;
			
			ps.setString(10,laborbargain.getBargaintype()) ;
			ps.setString(11,laborbargain.getTrialtime()) ;
			
			ps.setString(12, autoid);

			ps.execute();

			return true;

		} catch (Exception e) {
			e.printStackTrace();

		} finally {

			DbUtil.close(ps);
		}

		return false;

	}
	
	
	/**
	 * 根据指定的ID返回整条记录信息
	 * @param id
	 * @return
	 * @throws MatechException
	 */
		public LaborBargain getLaborBargain(String autoid) throws MatechException {
			DbUtil.checkConn(conn);
			String sql = "";
			PreparedStatement ps = null;
			ResultSet rs = null;
			LaborBargain lb = new LaborBargain();
			try {
				conn = new DBConnect().getConnect("");

					sql = "select * from oa_workbargain where autoid='"+ autoid + "'";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();

				if (rs.next()) {
					lb.setBargainID(rs.getString("bargainID"));
					lb.setBargainperson(rs.getString("bargainperson"));
					lb.setEndorsedate(rs.getString("endorsedate"));
					lb.setEmolument(rs.getString("emolument"));
					lb.setIneffecttime(rs.getString("ineffecttime"));
					lb.setOther(rs.getString("other"));
					lb.setCheckinperson(rs.getString("checkinperson"));
					lb.setCheckindate(rs.getString("checkindate"));
					lb.setUserid(rs.getString("userid"));
					lb.setFileNames(rs.getString("uploadFileName")) ;
					lb.setFileRondomNames(rs.getString("uploadTempName")) ;
					lb.setBargaintype(rs.getString("bargaintype")) ;
					lb.setTrialtime(rs.getString("trialtime")) ;
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				DbUtil.close(rs);
				DbUtil.close(ps);
			}
			return lb;
		}

	/**
	 * 删除
	 * 
	 * @param autoid
	 * @return
	 * @throws Exception
	 */
	public boolean removeLabor(String autoid) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;

		try {

			String sql = "delete from oa_workbargain where autoid='" + autoid + "' ";

			ps = conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}

		return false;

	}

}
