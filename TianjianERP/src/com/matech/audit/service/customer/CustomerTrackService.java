package com.matech.audit.service.customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.matech.audit.service.customer.model.CustomerTrack;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class CustomerTrackService {
	private Connection conn = null;
	ASFuntion CHF = new ASFuntion();

	public CustomerTrackService(Connection conn) {
		this.conn = conn;
	}

	/**
	 * 增加客户追踪记录
	 * 
	 * @param customerTrack
	 * @return
	 */
	public boolean addCustomerTrack(CustomerTrack customerTrack,String customerId)
			throws Exception {
		DbUtil.checkConn(conn);

		PreparedStatement ps = null;

		try {
			String sql = "insert into oa_customertrack (customerid,companyName,linkman,projectName,telPhone,linkmanQQ,giveCall,"
					+ "callTopic,fixedQuestion,unfixQuestion,fixedInstance,recoder,recodeTime,userid) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			ps = conn.prepareStatement(sql);

			ps.setString(1, customerId);
			ps.setString(2, customerTrack.getCompanyName());
			ps.setString(3, customerTrack.getLinkman());
			ps.setString(4, customerTrack.getProjectName());
			ps.setString(5, customerTrack.getTelPhone());
			ps.setString(6, customerTrack.getLinkmanQQ());
			ps.setString(7, customerTrack.getGiveCall());
			ps.setString(8, customerTrack.getCallTopic());
			ps.setString(9, customerTrack.getFixedQuestion());
			ps.setString(10, customerTrack.getUnfixQuestion());
			ps.setString(11, customerTrack.getFixedInstance());
			ps.setString(12, customerTrack.getRecoder());
			ps.setString(13, customerTrack.getRecodeTime());
			ps.setString(14, customerTrack.getUserid());

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
	 * 修改客户追踪记录
	 * 
	 * @param customerTrack
	 * @param autoid
	 * @return
	 */
	public boolean updateCustomerTrack(CustomerTrack customerTrack,String autoid)
			throws Exception {
		DbUtil.checkConn(conn);

		PreparedStatement ps = null;

		try {
			String sql = " update oa_customertrack set companyName=?,linkman=?,projectName=?,telPhone=?,linkmanQQ=?,"
					+ " giveCall=?,callTopic=?,fixedQuestion=?,unfixQuestion=?,fixedInstance=?,recoder=?,recodeTime=?"
					+ " where autoid=?";

			ps = conn.prepareStatement(sql);

			ps.setString(1, customerTrack.getCompanyName());
			ps.setString(2, customerTrack.getLinkman());
			ps.setString(3, customerTrack.getProjectName());
			ps.setString(4, customerTrack.getTelPhone());
			ps.setString(5, customerTrack.getLinkmanQQ());
			ps.setString(6, customerTrack.getGiveCall());
			ps.setString(7, customerTrack.getCallTopic());
			ps.setString(8, customerTrack.getFixedQuestion());
			ps.setString(9, customerTrack.getUnfixQuestion());
			ps.setString(10, customerTrack.getFixedInstance());
			ps.setString(11, customerTrack.getRecoder());
			ps.setString(12, customerTrack.getRecodeTime());
			ps.setString(13, customerTrack.getAutoid());

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
	 * 删除客户追踪记录
	 * 
	 * @param autoid
	 * @return
	 */
	public boolean removeCustomerTrack(String autoid) throws Exception {
		DbUtil.checkConn(conn);

		PreparedStatement ps = null;

		try {
			String sql = "delete from oa_customertrack where autoid = ?";

			ps = conn.prepareStatement(sql);
			ps.setString(1, autoid);

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
	 * 获得客户追踪记录
	 * 
	 * @param autoid
	 * @return
	 */
	public CustomerTrack getCustomerTrack(String autoid) throws Exception {
		DbUtil.checkConn(conn);

		PreparedStatement ps = null;
		ResultSet rs = null;

		CustomerTrack customerTrack = new CustomerTrack();

		try {
			String sql = " select companyName,linkman,projectName,telPhone,linkmanQQ,giveCall,"
					   + " callTopic,fixedQuestion,unfixQuestion,fixedInstance,recoder,recodeTime from oa_customertrack "
					   + " where autoid = ?";

			ps = conn.prepareStatement(sql);
			
			ps.setString(1, autoid);
			rs = ps.executeQuery();
			
			while(rs.next()){
				customerTrack.setCompanyName(CHF.showNull(rs.getString(1)));
				customerTrack.setLinkman(CHF.showNull(rs.getString(2)));
				customerTrack.setProjectName(CHF.showNull(rs.getString(3)));
				customerTrack.setTelPhone(CHF.showNull(rs.getString(4)));
				customerTrack.setLinkmanQQ(CHF.showNull(rs.getString(5)));
				customerTrack.setGiveCall(CHF.showNull(rs.getString(6)));
				customerTrack.setCallTopic(CHF.showNull(rs.getString(7)));
				customerTrack.setFixedQuestion(CHF.showNull(rs.getString(8)));
				customerTrack.setUnfixQuestion(CHF.showNull(rs.getString(9)));
				customerTrack.setFixedInstance(CHF.showNull(rs.getString(10)));
				customerTrack.setRecoder(CHF.showNull(rs.getString(11)));
				customerTrack.setRecodeTime(CHF.showNull(rs.getString(12)));			
			}
	
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return customerTrack;
	}
	
	//取消提醒
	public void cancelInfo(String id) throws Exception {
		DbUtil.checkConn(conn);

		PreparedStatement ps = null;

		try {
			String sql = "update oa_customertrack set isRead=1 where autoid = ?";

			ps = conn.prepareStatement(sql);
			ps.setString(1, id);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
}
