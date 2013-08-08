package com.matech.audit.service.circumstance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.audit.service.circumstance.model.Circumstance;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;



public class CircumstanceService {
	
	private Connection conn= null;
	
	public CircumstanceService(Connection conn){
		
		this.conn =conn;
	}
	
	public boolean updatecircumstance(Circumstance circumstance,String autoid) throws Exception{
		DbUtil.checkConn(conn);	
		
		PreparedStatement ps = null;
		
		try {
			
			String sql = "update s_config set sname =?,svalue=?,smemo=?,upuser=?,uptime=? where autoid=?";
			
			ps = conn.prepareStatement(sql);
			
			ps.setString(1,circumstance.getSname());
			ps.setString(2,circumstance.getSvalue());
			ps.setString(3, circumstance.getSmemo());
			ps.setString(4, circumstance.getUpuser());
			ps.setString(5, circumstance.getUptime());
			ps.setString(6, autoid);
			
			ps.execute();
			ps.execute("Flush tables");
			
			return true;
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			
			
		}finally{
			
			DbUtil.close(ps);
		}
	
					
		return false;
		
	}
	
	/**
	 * 菜单控制：跟据环境设置来控制菜单的显示
	 * 环境设置表 ：s_config 增加 config
	 * 值：(是：菜单1，菜单2;否：菜单3)
	 * 例：是`.12.|否`.13.
	 * @param SysMenuPpm
	 * @return
	 * @throws Exception
	 */
	public String getUserMenu(String UserMenuPpm)  throws Exception{
		try {
			String result = "";
			ASFuntion CHF=new ASFuntion();
			UserMenuPpm = UserMenuPpm.substring(1, UserMenuPpm.length()-1);
			UserMenuPpm = "'" + CHF.replaceStr(UserMenuPpm, ".", "','") + "'";
			result=	getSysMenu(UserMenuPpm);
			result = result.substring(1, result.length()-1);
			result = "." + CHF.replaceStr(result, "','", ".") + ".";
			//System.out.println(result);
			return result ;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} 
	}
	
	public String getSysMenu(String SysMenuPpm)  throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			//System.out.println(SysMenuPpm);
			
			ASFuntion CHF=new ASFuntion();
			SysMenuPpm = SysMenuPpm + ",";
			String sql = "select * from asdb.s_config where ifnull(control,'') <>'' ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				String svalue = rs.getString("svalue");
				String control = rs.getString("control");
				
				String yesControl = "";
				String allControl = "";
				
				if(!"".equals(control.trim())){
					String [] controls = control.split("\\|");
					for (int i = 0; i < controls.length; i++) {
						if(controls[i] !=null && !"".equals(controls[i])){
							String [] value = controls[i].split("`");
							if(svalue.equals(value[0])){
								yesControl += value[1];
							}
							allControl += value[1]; 
						}
					}
					
					String [] all = allControl.split("\\.");
					for (int i = 0; i < all.length; i++) {
						if(all[i] != null && !"".equals(all[i])){
							SysMenuPpm = CHF.replaceStr(SysMenuPpm, "'"+all[i]+"',", "");
						}
					}
					
					String [] yes = yesControl.split("\\.");
					for (int i = 0; i < yes.length; i++) {
						if(yes[i] != null && !"".equals(yes[i])){
							SysMenuPpm += "'"+yes[i]+"',";
						}
					}
				}
			}
			//System.out.println(SysMenuPpm);
			SysMenuPpm = SysMenuPpm.substring(0,SysMenuPpm.length()-1);
			return SysMenuPpm;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
}
