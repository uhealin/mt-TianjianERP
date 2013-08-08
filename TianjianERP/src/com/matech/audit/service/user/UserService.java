


package com.matech.audit.service.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.user.model.PartTime;
import com.matech.audit.service.user.model.User;
import com.matech.audit.service.user.model.UserDetailsTree;
import com.matech.audit.service.userState.UserStateService;
import com.matech.audit.service.userdef.UserdefService;
import com.matech.audit.service.userdisplay.UserDisplayService;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.exception.MatechException;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;
import com.matech.framework.pub.util.MD5;
import com.matech.framework.pub.util.StringUtil;
import com.matech.audit.service.user.EnCodeUtils;


/**
 *
 * <p>Title: 用户表操作</p>
 * <p>Description: 用户表的增、删、查、改</p>
 * <p>Copyright: Copyright (c) 2006, 2008 MaTech Corporation.All rights reserved. </p>
 * <p>Company: Matech  广州铭太信息科技有限公司</p>
 *
 * 本程序及其相关的所有资源均为铭太科技公司研发中心审计开发组所有，
 * 版本发行及解释权归研发中心，公司网站为：http://www.matech.cn
 *
 * <p>贡献者团队:
 *     铭太科技 - 研发中心，审计开发组
 *
 * @author 彭勇
 * 2007-6-9
 */
public class UserService {

	/**
	 * 正常的
	 */
	public final static int USER_STATE_ENABLED = 0;

	/**
	 * 无法找到
	 */
	public final static int USER_STATE_NOFOUND = 1;

	/**
	 * 禁用
	 */
	public final static int USER_STATE_DISABLED = 2;

	/**
	 * 没有权
	 */
	public final static int USER_STATE_POWERLESS = 3;

	/**
	 * 密码错误
	 */
	public final static int USER_STATE_PWD_ERROR = 4;

	private Connection conn=null;

	public UserService(Connection conn) {
        this.conn=conn;
    }
	/**
	 * 删除用户
	 * @param id ： 为用户的自动编号
	 * @param opt： 0为设置删除标志［可还原］，1为物理删除用户［不可还原］
	 * @throws MatechException
	 */
	public void removeUser(String id,String opt)throws MatechException {
		DbUtil.checkConn(conn);
		String sql = "";

        PreparedStatement ps = null;
        try {
            if (id != null) {
            	if("0".equals(opt)){
            		sql = "update k_user set state = 1 where id = '"+id+"' ";
            	}else{
            		sql = "delete from k_user where id = '"+id+"' ";
            		new UserStateService(conn).removeUserStateByUserId(id);
            		new UserDisplayService(conn).delByUserId(id);
            	}

            	ps = conn.prepareStatement(sql);
                ps.execute();
                ps.execute("Flush tables");
            }
        }catch(Exception e){
        	Debug.print(Debug.iError,"访问失败",e);
			throw new MatechException("访问失败："+e.getMessage(),e);
        }finally {
            DbUtil.close(ps);
        }
	}
	/**
	 * 批量禁用用户
	 * @param id ： 为用户的自动编号
	 * @param opt： 0为设置删除标志［可还原］，1为物理删除用户［不可还原］
	 * @throws MatechException
	 */
	public void removeUser3(String id,String opt)throws MatechException {
		DbUtil.checkConn(conn);
		String sql = "";

        PreparedStatement ps = null;
        try {
            if (id != null) {
            	if("0".equals(opt)){
            		sql = "update k_user set state = 1,forbiddenDate=now() where id in ("+id+") ";
            	}

            	ps = conn.prepareStatement(sql);
                ps.execute();
                ps.execute("Flush tables");
            }
        }catch(Exception e){
        	Debug.print(Debug.iError,"访问失败",e);
			throw new MatechException("访问失败："+e.getMessage(),e);
        }finally {
            DbUtil.close(ps);
        }
	}
	
	/**
	 * 批量删除人员
	 * @param id ： 为用户的自动编号
	 * @param opt： 0为设置删除标志［可还原］，1为物理删除用户［不可还原］
	 * @throws MatechException
	 */
	public void removeUser4(String id,String opt)throws MatechException {
		DbUtil.checkConn(conn);
		String sql = "";

        PreparedStatement ps = null;
        try {
            if (id != null) {
            	if("1".equals(opt)){
            		sql = "delete from k_user where id in ("+id+") "; 
            	}

            	ps = conn.prepareStatement(sql);
                ps.execute();
                ps.execute("Flush tables");
            }
        }catch(Exception e){
        	Debug.print(Debug.iError,"访问失败",e);
			throw new MatechException("访问失败："+e.getMessage(),e);
        }finally {
            DbUtil.close(ps);
        }
	}
	

	/**
	 * 还原用户
	 * @param User ： 用户对象
	 * @throws MatechException
	 */
	public void revertUser(User user) throws MatechException{
		DbUtil.checkConn(conn);

		String id = new ASFuntion().showNull(user.getId());
		String loginid = new ASFuntion().showNull(user.getLoginid());
		String departmentid = new ASFuntion().showNull(user.getDepartmentid());

		String sql = "";
        PreparedStatement ps = null;
        try {
        	if(!"".equals(id)){
        		if(!"".equals(loginid)){
        			sql = "update k_user set state = 0,loginid = '"+loginid+"'," +
        					"departmentid = ?,forbiddenDate='', clientDogSysUi='"+new DesUtils().encrypt("1")+"'," +
        					"Password='"+MD5.getMD5String("1")+"' where id = ?";
        		}else{
        			sql = "update k_user set state = 0," +
							"departmentid = ?,forbiddenDate='', clientDogSysUi='"+new DesUtils().encrypt("1")+"'," +
							"Password='"+MD5.getMD5String("1")+"' where id = ?";
        		}
        		ps = conn.prepareStatement(sql);
        		ps.setString(1, departmentid);
        		ps.setString(2, id);
        		ps.execute();
        		ps.execute("Flush tables");
        	}
        }catch(Exception e){
        	Debug.print(Debug.iError,"访问失败",e);
			throw new MatechException("访问失败："+e.getMessage(),e);
        }finally {
            DbUtil.close(ps);
        }
	}

	public void updateRoles(String userid, String roles) throws MatechException{
		DbUtil.checkConn(conn);

		String[] role = roles.split(",");
		if(role==null || role.length==0)
			return;

		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			String sql = "insert into k_userrole (userid, rid) values(?, ?)";
			String sql2 = "select * from k_userrole where userid=? and rid=?";
			for(int i=0; i<role.length; i++){
				ps = conn.prepareStatement(sql2);

				ps.setString(1, userid);
				ps.setString(2, role[i]);
				rs = ps.executeQuery();
				if(!rs.next()){
					ps = conn.prepareStatement(sql);
					ps.setString(1, userid);
					ps.setString(2, role[i]);
					ps.execute();
				}
				rs.close();
				ps.close();
			}
		}catch(Exception e){
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败："+e.getMessage(), e);
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	/**
	 * 新增用户
	 * @param uservo　
	 * @throws Exception
	 */
	public void addUser(User uservo)throws MatechException {
		DbUtil.checkConn(conn);
		String sql = "";
		String userid = "";

        PreparedStatement ps = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        int i = 1;
        try {

        	String svalue = "";

			sql = "select svalue from s_config where sname='新增人员是否审批'";

			conn = new DBConnect().getConnect("");
			ps = conn.prepareStatement(sql);

			rs = ps.executeQuery();
			while(rs.next()){
				svalue = rs.getString(1);
			}
        	rs.close();
        	ps.close();
        	if("是".equals(svalue)){
        		uservo.setState("1");
        	}else{
        		uservo.setState("0");
        	}
				
			sql = "INSERT INTO k_User(" +
			 	"Name, loginid, Password, Sex, BornDate, " +
			 	"Educational, Diploma, DepartID, Rank, Post, " +
			 	"Specialty, departmentid,clientDogSysUi, IsTips,state," +
			 	"userPhoto,userPhotoTemp,mobilePhone,phone,email," +
			 	"cpano,floor,house,station,  paperstype," +
			 	"papersnumber,nation,marriage,place,residence," +
			 	"politics,partytime,relationships,profession,compact," +
			 	"workstate,leavetype,english,diplomatime,entrytime," +
			 	"identityCard, ip,resume,emtype,tel_shortno, " +
			 	"phone_shortno, bank_card_no,bank_card_name "+
			 	") VALUES(" +
			 	"?,?,?,?,?," +
			 	"?,?,?,?,?," +
			 	"?,?,?,1,?," +
			 	"?,?,?,?,?," +
			 	"?,?,?,?,?," +
			 	"?,?,?,?,?," +
			 	"?,?,?,?,?," +
			 	"?,?,?,?,?," +
			 	"?,?,?,?,?," +
			 	"?,?,?"+
			 	")";

			ps = conn.prepareStatement(sql);
            ps.setString(i++, uservo.getName());
            ps.setString(i++, uservo.getLoginid());
            ps.setString(i++, MD5.getMD5String(uservo.getPassword()));
            ps.setString(i++, uservo.getSex());
            ps.setString(i++, uservo.getBorndate());
            
            ps.setString(i++, uservo.getEducational());
            ps.setString(i++, uservo.getDiploma());
            ps.setString(i++, uservo.getDepartid());
            ps.setString(i++, uservo.getRank());
            ps.setString(i++, uservo.getPost());
            
            ps.setString(i++, uservo.getSpecialty());
            ps.setString(i++, uservo.getDepartmentid());
            //ps.setString(i++, uservo.getClientDogSysUi());
            ps.setString(i++, new DesUtils().encrypt(uservo.getPassword()));
            
            ps.setString(i++, uservo.getState());
            
            ps.setString(i++, uservo.getUserPhoto());
            ps.setString(i++, uservo.getUserPhotoTemp());
            ps.setString(i++, uservo.getMobilePhone());
            ps.setString(i++, uservo.getPhone());
            ps.setString(i++, uservo.getEmail());
            
            ps.setString(i++, uservo.getCpano());
            ps.setString(i++, uservo.getFloor());
            ps.setString(i++, uservo.getHouse());
            ps.setString(i++, uservo.getStation());
            ps.setString(i++, uservo.getPaperstype());
            
            ps.setString(i++, uservo.getPapersnumber());
            ps.setString(i++, uservo.getNation());
            ps.setString(i++, uservo.getMarriage());
            ps.setString(i++, uservo.getPlace());
            ps.setString(i++, uservo.getResidence());
            
            ps.setString(i++, uservo.getPolitics());
            ps.setString(i++, uservo.getPartytime());
            ps.setString(i++, uservo.getRelationships());
            ps.setString(i++, uservo.getProfession());
            ps.setString(i++, uservo.getCompact());
            
            ps.setString(i++, uservo.getWorkstate());
            ps.setString(i++, uservo.getLeavetype());            
            ps.setString(i++, uservo.getEnglish());
            ps.setString(i++, uservo.getDiplomatime());
            ps.setString(i++, uservo.getEntrytime());
            
            ps.setString(i++, uservo.getIdentityCard());
            ps.setString(i++, uservo.getIp());
            ps.setString(i++, uservo.getResume());
            ps.setString(i++, uservo.getEmtype());
            ps.setString(i++, uservo.getTel_shortno()); 
            
            ps.setString(i++, uservo.getPhone_shortno());
            ps.setString(i++, uservo.getBank_card_no());
            ps.setString(i++, uservo.getBank_card_name());
            ps.execute();
            ps.execute("Flush tables");


            sql = "select id from k_User where loginid = '"+uservo.getLoginid()+"'";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            if(rs.next()){
	            String cid = rs.getString(1);
	            try{
	            	new UserdefService(conn).addOrupdateUserdef(uservo.getUserdefs(), cid, "user");
	            }catch(Exception e){
	            	
	            }
	           // new UserdefService(conn).addOrupdateUserdef(uservo.getUserdefs(), cid, "user");
	        }

            String sql1 = "select id from k_user where loginid = '"+uservo.getLoginid()+"'"+" and state = '"+uservo.getState()+"'";
            ps1 = conn.prepareStatement(sql1);

            rs1 = ps1.executeQuery();

            if(rs1.next()){
            	userid  = rs1.getString(1);
            	String sql3 = "delete from k_userrole where userid = '"+rs1.getString(1)+"'";

            	ps1 = conn.prepareStatement(sql3);
            	ps1.execute();
            }
            if(!uservo.getRoles().equals("")){
	            String []roles = uservo.getRoles().split(",");
	
	            for(int j = 0; j<roles.length;j++){
	
	            	String sql4 = "insert into k_userrole(userid,rid) values(?,?) ";
	                ps2 = conn.prepareStatement(sql4);
	
	                ps2.setString(1,userid);
	                ps2.setString(2,roles[j]);
	
	                ps2.execute();
	
	            }
            }  
        }catch(Exception e){
        	Debug.print(Debug.iError,"访问失败",e);
			throw new MatechException("访问失败："+e.getMessage(),e);
        }finally {
            DbUtil.close(ps);
        }
	}

	public void updateUser(User uservo,String loginid)throws Exception {
		PreparedStatement ps = null;
        int i = 1;
        try{
        	
	        String sql = "update k_User set " +
					"sex=?,borndate=?,educational=?,password=?," +
					"diploma=?,departid=?,rank=?,post=?,specialty=?," +
					"departmentid=?,userPhoto=?,userPhotoTemp=?,mobilePhone=?," +
					"phone=?,email=?,cpano=?,floor=?,house=?," +
					"station=? ,paperstype=?,papersnumber=?,nation=?,marriage=?," +
					"place=?,residence=?,politics=?,partytime=?,relationships=?," +
					"profession=?,compact=?,workstate=?,leavetype=?,english=?," +
					"diplomatime=?,entrytime=?,identityCard=?, " +
					"ip=?,resume=?,emtype=?," +
					"tel_shortno=?,phone_shortno=?,bank_card_no=?,bank_card_name=?  "+
					" where loginid=? and state = 0";
			ps = conn.prepareStatement(sql);
            ps.setString(i++, uservo.getSex());
            ps.setString(i++, uservo.getBorndate());
            ps.setString(i++, uservo.getEducational());
            ps.setString(i++, MD5.getMD5String(uservo.getPassword())); //密码
            
            ps.setString(i++, uservo.getDiploma());
            ps.setString(i++, uservo.getDepartid());
            ps.setString(i++, uservo.getRank());
            ps.setString(i++, uservo.getPost());
            ps.setString(i++, uservo.getSpecialty());
            
            ps.setString(i++, uservo.getDepartmentid());
            //ps.setString(i++, uservo.getClientDogSysUi());
            //ps.setString(i++, new DesUtils().encrypt(uservo.getPassword()));
            
            ps.setString(i++, uservo.getUserPhoto()) ;
            ps.setString(i++, uservo.getUserPhotoTemp()) ;
            ps.setString(i++, uservo.getMobilePhone()) ;
            
            ps.setString(i++, uservo.getPhone()) ;
            ps.setString(i++, uservo.getEmail()) ;
            ps.setString(i++, uservo.getCpano()) ;
            ps.setString(i++, uservo.getFloor());
            ps.setString(i++, uservo.getHouse());
            
            ps.setString(i++, uservo.getStation());
            ps.setString(i++, uservo.getPaperstype());
            ps.setString(i++, uservo.getPapersnumber());
            ps.setString(i++, uservo.getNation());
            ps.setString(i++, uservo.getMarriage());
            
            ps.setString(i++, uservo.getPlace());
            ps.setString(i++, uservo.getResidence());
            ps.setString(i++, uservo.getPolitics());
            ps.setString(i++, uservo.getPartytime());
            ps.setString(i++, uservo.getRelationships());
            
            ps.setString(i++, uservo.getProfession());
            ps.setString(i++, uservo.getCompact());
            ps.setString(i++, uservo.getWorkstate());
            ps.setString(i++, uservo.getLeavetype());
            ps.setString(i++, uservo.getEnglish());
            
            ps.setString(i++, uservo.getDiplomatime());
            ps.setString(i++, uservo.getEntrytime());
            ps.setString(i++, uservo.getIdentityCard());
            
            ps.setString(i++, uservo.getIp());
            ps.setString(i++, uservo.getResume());
            ps.setString(i++, uservo.getEmtype());

            ps.setString(i++, uservo.getTel_shortno()); //tel_shortno=?,phone_shortno=?,bank_card_no=?,bank_card_name=? 
            ps.setString(i++, uservo.getPhone_shortno());
            ps.setString(i++, uservo.getBank_card_no());
            ps.setString(i++, uservo.getBank_card_name());
            
            ps.setString(i++, uservo.getLoginid());
            ps.execute();
            ps.execute("Flush tables");
            System.out.println(sql);
            ps.close();
            
        }catch(Exception e){
        	e.printStackTrace();
			throw e;
        }finally {
            DbUtil.close(ps);
        }	
	}
	/**
	 * 修改用户信息
	 * @param uservo
	 * @throws Exception
	 */
	public void updateUser(User uservo)throws MatechException {
		DbUtil.checkConn(conn);
		String sql = "";

		PreparedStatement ps = null;
        int i = 1;
        try {
        	//保留原来的
        	User olduser=getUser(uservo.getId(),"id");
        	
			sql = "update k_User set " +
				"name=?,sex=?,loginid=?,borndate=?,educational=?," +
				"diploma=?,departid=?,rank=?,post=?,specialty=?," +
				"departmentid=?,userPhoto=?,userPhotoTemp=?,mobilePhone=?," +
				"phone=?,email=?,cpano=?,floor=?,house=?," +
				"station=? ,paperstype=?,papersnumber=?,nation=?,marriage=?," +
				"place=?,residence=?,politics=?,partytime=?,relationships=?," +
				"profession=?,compact=?,workstate=?,leavetype=?,english=?," +
				"diplomatime=?,entrytime=?,identityCard=?, ip=?,resume=?," +
				"emtype=?,tel_shortno=?,phone_shortno=?,bank_card_no=?,bank_card_name=? "+
				"where id=? and state = 0";
			ps = conn.prepareStatement(sql);
			ps.setString(i++, uservo.getName());
            ps.setString(i++, uservo.getSex());
            ps.setString(i++, uservo.getLoginid());
            ps.setString(i++, uservo.getBorndate());
            ps.setString(i++, uservo.getEducational());
            
            ps.setString(i++, uservo.getDiploma());
            ps.setString(i++, uservo.getDepartid());
            ps.setString(i++, uservo.getRank());
            ps.setString(i++, uservo.getPost());
            ps.setString(i++, uservo.getSpecialty());
            
            ps.setString(i++, uservo.getDepartmentid());
            //ps.setString(i++, uservo.getClientDogSysUi());
//            ps.setString(i++, new DesUtils().encrypt(uservo.getPassword()));
            ps.setString(i++, uservo.getUserPhoto()) ;
            ps.setString(i++, uservo.getUserPhotoTemp()) ;
            ps.setString(i++, uservo.getMobilePhone()) ;
            
            ps.setString(i++, uservo.getPhone()) ;
            ps.setString(i++, uservo.getEmail()) ;
            ps.setString(i++, uservo.getCpano()) ;
            ps.setString(i++, uservo.getFloor());
            ps.setString(i++, uservo.getHouse());
            
            ps.setString(i++, uservo.getStation());
            ps.setString(i++, uservo.getPaperstype());
            ps.setString(i++, uservo.getPapersnumber());
            ps.setString(i++, uservo.getNation());
            ps.setString(i++, uservo.getMarriage());
            
            ps.setString(i++, uservo.getPlace());
            ps.setString(i++, uservo.getResidence());
            ps.setString(i++, uservo.getPolitics());
            ps.setString(i++, uservo.getPartytime());
            ps.setString(i++, uservo.getRelationships());
            
            ps.setString(i++, uservo.getProfession());
            ps.setString(i++, uservo.getCompact());
            ps.setString(i++, uservo.getWorkstate());
            ps.setString(i++, uservo.getLeavetype());
            ps.setString(i++, uservo.getEnglish());
            
            ps.setString(i++, uservo.getDiplomatime());
            ps.setString(i++, uservo.getEntrytime());
            ps.setString(i++, uservo.getIdentityCard());  //身份证号
            ps.setString(i++, uservo.getIp());
            ps.setString(i++, uservo.getResume());
            
            ps.setString(i++, uservo.getEmtype());
            ps.setString(i++, uservo.getTel_shortno()); //tel_shortno=?,phone_shortno=?,bank_card_no=?,bank_card_name=? 
            ps.setString(i++, uservo.getPhone_shortno());
            ps.setString(i++, uservo.getBank_card_no());
            ps.setString(i++, uservo.getBank_card_name());
            
            ps.setInt(i++, Integer.parseInt(uservo.getId()));
            ps.execute();
            ps.execute("Flush tables");
            ps.close();
            
            new UserdefService(conn).addOrupdateUserdef(uservo.getUserdefs(), uservo.getId(), "user");

 //   System.out.println("------------------------------------------");
            String sql3 = "";
            if(!"".equals(uservo.getRoles())){
            	 //设置权限
                sql3 = "delete from k_userrole where userid ='"+uservo.getId()+"'";
                ps = conn.prepareStatement(sql3);
                ps.execute();
                ps.close();
                
     	       	String []roles = uservo.getRoles().split(",");

	     	       for(int j = 0; j<roles.length;j++){
	     	    	   
	     	       		String sql4 = "insert into k_userrole(userid,rid) values(?,?) ";
	     	       		ps = conn.prepareStatement(sql4);
					
	     	       		ps.setString(1,uservo.getId());
	     	       		ps.setString(2,roles[j]);
	     	       		ps.execute();
	     	       		ps.close();
	
	     	       }
            }
           

	       //设置排班
//	       丘海彬(丘海彬) 16:27:09
//	       1.小彭改下人员修改个人信息和 人员管理修改某个人登录信息的保存的逻辑，同步修改
//	         update oa_timesreport` 的 userid
//	       oa_timesschedular` 的 userid
//	       oa_allallocate 的 LoginID
//	       oa_ratingvalue 的 LoginID
//	       oa_monthallocate 的 LoginID
//	       丘海彬(丘海彬) 16:27:42
//	       记录，一定要判断
//
//	       确实是改了 k_user的loginid，才执行相关的SQL。
//
//	       我记得userservice的update逻辑里面，已经有相关的代码了。可能不全
           olduser.setLoginid(StringUtil.showNull(olduser.getLoginid())); 
           uservo.setLoginid(StringUtil.showNull(uservo.getLoginid())); 
	       if (!olduser.getLoginid().equals(uservo.getLoginid())){
	    	   try {
	    		   sql3="update oa_timesschedular set userid='"+uservo.getLoginid()+"' where userid='"+olduser.getLoginid()+"'";
		    	   ps = conn.prepareStatement(sql3);
		           ps.execute();
		           ps.close();
		           
		           sql3="update oa_timesreport set userid='"+uservo.getLoginid()+"' where userid='"+olduser.getLoginid()+"'";
		    	   ps = conn.prepareStatement(sql3);
		           ps.execute();
		           ps.close();
		           
		           sql3="update oa_allallocate set LoginID='"+uservo.getLoginid()+"' where LoginID='"+olduser.getLoginid()+"'";
		    	   ps = conn.prepareStatement(sql3);
		           ps.execute();
		           ps.close();
		           
		           sql3="update oa_ratingvalue set LoginID='"+uservo.getLoginid()+"' where LoginID='"+olduser.getLoginid()+"'";
		    	   ps = conn.prepareStatement(sql3);
		           ps.execute();
		           ps.close();
		           
		           sql3="update oa_monthallocate set LoginID='"+uservo.getLoginid()+"' where LoginID='"+olduser.getLoginid()+"'";
		    	   ps = conn.prepareStatement(sql3);
		           ps.execute();
		           ps.close();
				} catch (Exception e) {
					System.out.println("设置排班出错sql:"+sql3);
				}
	       }

		} catch (Exception e) {
			e.printStackTrace();
			Debug.print(Debug.iError,"访问失败",e);
			throw new MatechException("访问失败："+e.getMessage(),e);
		} finally {
			DbUtil.close(ps);
		}
	}

	/**
	 * 修改用户密码
	 * @param id
	 * @param oldpwd
	 * @param newpwd
	 * @return true 成功；false 失败
	 * @throws MatechException
	 */
	public boolean UpdatePassword(int id, String oldpwd,String newpwd)throws MatechException{
		DbUtil.checkConn(conn);

		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";

		try {
			
			sql = "select * from k_User where id=" + id +" and password='" + MD5.getMD5String(oldpwd) + "' and  state = 0";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				String encode=new EnCodeUtils().encodePWS(newpwd);
				System.out.println("1:密码"+newpwd+",加密后"+encode+",还原后："+new EnCodeUtils().decodePWS(encode));
				
				sql = "update k_user set password ='" + MD5.getMD5String(newpwd) +"',clientDogSysUi='"+encode+"' where id = " + id + " and state = 0 ";
				System.out.println("UpdatePassword2:"+sql);
				ps = conn.prepareStatement(sql);
				ps.execute();
				ps.execute("Flush tables");
				return true;
			}else{
				return false;
			}
		}catch (Exception e) {
			Debug.print(Debug.iError,"访问失败",e);
			throw new MatechException("访问失败："+e.getMessage(),e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}

	/**
	 * 修改用户密码
	 * @param id
	 * @param newpwd
	 * @return true 成功；false 失败
	 * @throws MatechException
	 */
	public boolean UpdatePassword(int id, String newpwd)throws MatechException{
		DbUtil.checkConn(conn);

        PreparedStatement ps = null;
        String str = "";
        try {
        	String s = "";
            s = MD5.getMD5String(newpwd.trim());
            
            String encode=new EnCodeUtils().encodePWS(newpwd.trim());
			System.out.println("1:密码"+newpwd+",加密后"+encode+",还原后："+new EnCodeUtils().decodePWS(encode));
            
            str = "update k_user set password ='" + s.toString() +"',clientDogSysUi='"+encode+"' where id = " + id + " and state = 0 ";
            System.out.println("UpdatePassword1:"+str);
            ps = conn.prepareStatement(str);
            if(ps.executeUpdate()>0) {
            	ps.execute("Flush tables");
            	 return true;
            }
            else {
              return false;
          }
        }
        catch (Exception e) {
        	e.printStackTrace();
        	Debug.print(Debug.iError,"访问失败",e);
			throw new MatechException("访问失败："+e.getMessage(),e);
        }
        finally {
			DbUtil.close(ps);
        }

      }

	/**
	 * 修改狗信息
	 * @param usr
	 * @param dog
	 * @throws MatechException
	 */
	public void updateDog(String usr,String dog) throws MatechException{
		DbUtil.checkConn(conn);
        PreparedStatement ps = null;
        try {
          	String sql = "update k_User set clientDogSysUi='"+dog+"' where loginid ='"+usr+"' and state =0";
          	ps = conn.prepareStatement(sql);
          	ps.execute();
          	ps.execute("Flush tables");
        } catch (Exception e) {
        	Debug.print(Debug.iError,"访问失败",e);
			throw new MatechException("访问失败："+e.getMessage(),e);
		} finally {
			DbUtil.close(ps);
		}
    }



	/**
	 * 得到用户信息
	 * @param oid 用户自动编号或用户登录名
	 * @param opt　"id" 为用户自动编号，"loginid"　为用户登录名
	 * @return
	 * @throws MatechException
	 */
	public User getUser(String oid,String opt)throws MatechException{
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		PreparedStatement ps1 = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        String sql = "";
        User uservo = new User();
        try {
        	if("id".equals(opt)){
        		sql = "select * from k_user where id='"+oid+"' ";
        	}else if("loginid".equals(opt)){
        		sql = "select * from k_User where loginid ='"+oid+"' and state =0 ";
        	}
   System.out.println("---------------------------------------------sql:"+sql);
        	String sql1 = "select rid from k_userrole where userid='"+oid+"'";
        	ps1 = conn.prepareStatement(sql1);

        	rs1 = ps1.executeQuery();

        	String roles = "";

        	while(rs1.next()){
        		roles = roles+rs1.getString(1)+",";

        	}
        	if(!"".equals(roles)){
        		roles = roles.substring(0, roles.length()-1);
        	}

 //  System.out.println("---------------------------------------------");
        	ps = conn.prepareStatement(sql);
        	rs = ps.executeQuery();
        	if(rs.next()){
        		uservo.setId(rs.getString("id"));
        		uservo.setName(rs.getString("name"));
        		uservo.setLoginid(rs.getString("Loginid"));
        		uservo.setSex(rs.getString("sex"));
        		uservo.setPassword(rs.getString("password"));
        		uservo.setBorndate(rs.getString("borndate"));
        		uservo.setEducational(rs.getString("educational"));
        		uservo.setDiploma(rs.getString("diploma"));
        		uservo.setDepartmentid(rs.getString("departmentid"));
        		uservo.setDepartid(rs.getString("departid"));
        		uservo.setRank(rs.getString("rank"));
        		uservo.setPost(rs.getString("post"));
        		uservo.setSpecialty(rs.getString("specialty"));
        		uservo.setRoles(roles);
        		uservo.setClientDogSysUi(rs.getString("clientDogSysUi"));
        		uservo.setUserPhoto(rs.getString("userPhoto")) ;
        		uservo.setUserPhotoTemp(rs.getString("userPhotoTemp")) ;
        		uservo.setMobilePhone(rs.getString("mobilePhone")) ;
        		uservo.setPhone(rs.getString("phone")) ;
        		uservo.setEmail(rs.getString("email")) ;
        		uservo.setCpano(rs.getString("cpano")) ;
        		
        		uservo.setFloor(rs.getString("floor")) ;
        		uservo.setHouse(rs.getString("house")) ;
        		uservo.setStation(rs.getString("station")) ;
        		
        		uservo.setPaperstype(rs.getString("paperstype"));
        		uservo.setPapersnumber(rs.getString("papersnumber"));
        		uservo.setNation(rs.getString("nation"));
        		uservo.setMarriage(rs.getString("marriage"));
        		uservo.setPlace(rs.getString("place"));

        		uservo.setResidence(rs.getString("residence"));
        		uservo.setPolitics(rs.getString("politics"));
        		uservo.setPartytime(rs.getString("partytime"));
        		uservo.setRelationships(rs.getString("relationships"));
        		uservo.setProfession(rs.getString("profession"));

        		uservo.setCompact(rs.getString("compact"));
        		uservo.setWorkstate(rs.getString("workstate"));
        		uservo.setLeavetype(rs.getString("leavetype"));
        		uservo.setEnglish(rs.getString("english"));
        		uservo.setDiplomatime(rs.getString("diplomatime"));
        		
        		uservo.setEntrytime(rs.getString("entrytime"));
        		uservo.setIdentityCard(rs.getString("identityCard"));
        		uservo.setIp(rs.getString("ip"));
        		uservo.setResume(rs.getString("resume"));
        		uservo.setEmtype(rs.getString("emtype"));
        		
        		uservo.setTel_shortno(rs.getString("tel_shortno"));
        		uservo.setPhone_shortno(rs.getString("phone_shortno"));
        		uservo.setBank_card_no(rs.getString("bank_card_no"));
        		uservo.setBank_card_name(rs.getString("bank_card_name"));
        	}

        	uservo.setUserdefs(new UserdefService(conn).getUserdef(uservo.getId(), "user"));

        	return uservo;
        }catch (Exception e) {
			Debug.print(Debug.iError,"访问失败",e);
			throw new MatechException("访问失败："+e.getMessage(),e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}



	/**
     * 保证用户与狗是一一对应
     * @param dog
     * @return
     * @throws MatechException
     */
    public boolean getDog(String dog ) throws MatechException{
    	DbUtil.checkConn(conn);
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
       	 String sql = "select * from k_user where clientDogSysUi ='"+dog+"' and state=0 ";
         ps = conn.prepareStatement(sql);
       	 rs = ps.executeQuery();
       	 if(rs.next()){
       		return false;
       	 }
       	 return true;
        } catch (Exception e) {
        	Debug.print(Debug.iError,"访问失败",e);
			throw new MatechException("访问失败："+e.getMessage(),e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
    }

    /**
     * 检查用户登陆名是否唯一，state=0
     * @param loginid
     * @return
     * @throws MatechException
     */
    public boolean SelectUser(String loginid)throws MatechException{
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
          ps = conn.prepareStatement("select * from k_User where loginid='" +loginid + "' and state=0");
          rs = ps.executeQuery();
          if (rs.next())
            return false;
         return true;
        }
        catch (Exception e) {
        	Debug.print(Debug.iError,"访问失败",e);
			throw new MatechException("访问失败："+e.getMessage(),e);
        }
        finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
        }

      }


    /**
     * 获得客户部门和角色编号
     * @param id
     * @return
     * @throws Exception
     */
    public String getUser(int id)throws Exception{
        PreparedStatement ps = null;
        ResultSet rs = null;

        String department = "";
        String roles = "";

        try {

          String sql = "select departmentid from k_User where id='" + id + "'";
          ps = conn.prepareStatement(sql);
          rs = ps.executeQuery();
          while(rs.next()){
        	  department += rs.getString(1);
          }

          rs.close();

          sql = "select rid from k_userrole where userid = '" + id + "'";
          ps = conn.prepareStatement(sql);
          rs = ps.executeQuery();
          while(rs.next()){
        	  roles += rs.getString(1)+",";
          }

          if(roles.length()>1){
        	  roles = roles.substring(0, roles.length()-1);
          }

          return department + "*" + roles;

        }
        catch (Exception e) {
        	Debug.print(Debug.iError,"访问失败",e);
			throw new MatechException("访问失败："+e.getMessage(),e);
        }
        finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
        }

      }


    /**
     * 检查用户登陆名是否唯一 true为唯一
     * @param id　用户ID
     * @return
     * @throws MatechException
     */
    public boolean SelectUser(int id)throws MatechException{
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
          ps = conn.prepareStatement("select * from k_User where id='" +id + "' ");
          rs = ps.executeQuery();
          if (rs.next()){
        	  return SelectUser(rs.getString("loginid"));
          }
          return true;
        }
        catch (Exception e) {
        	Debug.print(Debug.iError,"访问失败",e);
			throw new MatechException("访问失败："+e.getMessage(),e);
        }
        finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
        }

     }

    /**
     * EXCEL导入　
     */

    public void newTable() throws MatechException {
		delTable();
//		String sql = "CREATE TABLE tt_k_user (" +
//				"id int(20) NOT NULL auto_increment," +
//				"Name varchar(20)  default ''," +
//				"loginid varchar(20)  default ''," +
//				"Password varchar(50)  default ''," +
//				"Sex varchar(5) default NULL," +
//				"BornDate varchar(10) default NULL," +
//				"Educational varchar(50) default NULL," +
//				"Diploma varchar(50) default NULL," +
//				"DepartID varchar(10) default NULL," +
//				"Rank varchar(10) default NULL,  " +
//				"Post varchar(20) default NULL," +
//				"Specialty varchar(100) default NULL," +
//				"ParentGroup varchar(20) NOT NULL default '自定义'," +
//				"Popedom mediumtext," +
//				"IsTips int(1) NOT NULL default '1'," +
//				"departmentid varchar(20) default NULL," +
//				"userrole varchar(20) default NULL," +
//				"PRIMARY KEY  (id),KEY loginid (loginid)" +
//				") ENGINE=MyISAM DEFAULT CHARSET=gbk";
		String sql = "CREATE TABLE tt_k_user like k_user";
		PreparedStatement ps = null;
		try {

			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			sql = "ALTER TABLE `tt_k_user`    ADD COLUMN `userrole` text NULL ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			sql = "ALTER TABLE `asdb`.`tt_k_user` CHANGE `departmentid` `departmentid` VARCHAR(50) CHARACTER SET gbk COLLATE gbk_chinese_ci NULL ; ";
			ps = conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			Debug.print(Debug.iError,"访问失败",e);
			throw new MatechException("访问失败："+e.getMessage(),e);
		} finally {
			DbUtil.close(ps);
		}

	}

	public void delTable() throws MatechException {
		String sql = "DROP TABLE IF EXISTS tt_k_user";
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			Debug.print(Debug.iError,"访问失败",e);
			throw new MatechException("访问失败："+e.getMessage(),e);
		} finally {
			DbUtil.close(ps);
		}

	}
	public String CheckUpData2()throws MatechException{
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "select * from tt_k_user where loginid='admin'";
		String result = "";
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				result = "系统管理员[<font color=blue>admin</font>]不能重新载入，这条记录被忽略！";
			}
			DbUtil.close(rs);
			DbUtil.close(ps);

			sql = "select a.name from tt_k_user a where loginid >= 'zzzzzzzzz' ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				result +="<br>[<font color=blue>"+rs.getString(1)+"</font>]的登录名不合法，这条记录被忽略！";
				sql = "delete from tt_k_user where name='"+rs.getString(1)+"'";
				//System.out.println("yzm:sql="+sql);
				conn.createStatement().executeUpdate(sql);
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
//			sql ="SELECT  a.place,b.name AS placevalue FROM tt_k_user a LEFT JOIN  k_dic b ON a.place = b.name AND b.ctype = '中国城市' and a.place <> '' ";
//			ps = conn.prepareStatement(sql);
//			rs = ps.executeQuery();
//			while(rs.next()){
//				String value = rs.getString("place");
//				String placevalue = rs.getString("placevalue");
//				if(placevalue==null){
//					result +="<br>籍贯:[<font color=blue>"+value+"</font>]不存在！";
//				}
//			}
//			DbUtil.close(rs);
//			DbUtil.close(ps);
//			
//			sql ="SELECT a.politics,b.name AS politicsvalue FROM tt_k_user a LEFT JOIN k_dic b ON a.politics = b.name AND b.ctype = '政治面貌'";
//			ps = conn.prepareStatement(sql);
//			rs = ps.executeQuery();
//			while(rs.next()){
//				String value = rs.getString("politics");
//				String placevalue = rs.getString("politicsvalue");
//				if(placevalue==null){
//					result +="<br>政治面貌：[<font color=blue>"+value+"</font>]不存在！";
//				}
//			}
//			DbUtil.close(rs);
//			DbUtil.close(ps);
//			
//			sql ="SELECT a.educational,b.name AS educationalValue FROM tt_k_user a LEFT JOIN k_dic b ON a.politics = b.name AND b.ctype = '员工学历'";
//			ps  = conn.prepareStatement(sql);
//			rs = ps.executeQuery();
//			while(rs.next()){
//				String value = rs.getString("educational");
//				String placevalue = rs.getString("educationalValue");
//				if(placevalue==null){
//					result +="<br>学历：[<font color=blue>"+value+"</font>]不存在！";
//				}
//			}
//			DbUtil.close(rs);
//			DbUtil.close(ps);
			
			
			//修改密码
			sql = "update tt_k_user set Password = 1 where ifnull(Password,'') = '' ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			sql = "update tt_k_user set Password = MD5(Password) ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			return result;
		} catch (Exception e) {
			Debug.print(Debug.iError,"访问失败",e);
			throw new MatechException("访问失败："+e.getMessage(),e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	public String CheckUpData()throws MatechException{
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "select * from tt_k_user where loginid='admin'";
		String result = "";
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				result = "系统管理员[<font color=blue>admin</font>]不能重新载入，这条记录被忽略！";
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			sql = "select b.name from k_user a , tt_k_user b where a.loginid<>'admin' and a.loginid=b.loginid and a.name=b.name";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				result +="<br>[<font color=blue>"+rs.getString(1)+"</font>]的用户登录名重复，这条记录被忽略！请修改该用户的登录名,重新导入该用户";
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			sql = "select a.name from  tt_k_user a where a.loginid<>'admin' and a.departmentid='' ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				result +="<br>[<font color=blue>"+rs.getString(1)+"</font>]的用户的部门为空，这条记录被忽略！";
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			sql = "select a.name from tt_k_user a where loginid >= 'zzzzzzzzz' ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				result +="<br>[<font color=blue>"+rs.getString(1)+"</font>]的登录名不合法，这条记录被忽略！";
				sql = "delete from tt_k_user where name='"+rs.getString(1)+"'";
				//System.out.println("yzm:sql="+sql);
				conn.createStatement().executeUpdate(sql);

			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			sql ="SELECT  a.place,b.name AS placevalue FROM tt_k_user a LEFT JOIN  k_dic b ON a.place = b.name AND b.ctype = '中国城市' ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				String value = rs.getString("place");
				String placevalue = rs.getString("placevalue");
				if(placevalue==null){
					result +="<br>籍贯:[<font color=blue>"+value+"</font>]不存在！";
				}
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			sql ="SELECT a.politics,b.name AS politicsvalue FROM tt_k_user a LEFT JOIN k_dic b ON a.politics = b.name AND b.ctype = '政治面貌'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				String value = rs.getString("politics");
				String placevalue = rs.getString("politicsvalue");
				if(placevalue==null){
					result +="<br>政治面貌：[<font color=blue>"+value+"</font>]不存在！";
				}
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			sql ="SELECT a.educational,b.name AS educationalValue FROM tt_k_user a LEFT JOIN k_dic b ON a.politics = b.name AND b.ctype = '员工学历'";
			ps  = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				String value = rs.getString("educational");
				String placevalue = rs.getString("educationalValue");
				if(placevalue==null){
					result +="<br>学历：[<font color=blue>"+value+"</font>]不存在！";
				}
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			/**
			 * 人员批量导入的时候，如果角色不存在，就无条件设置这个人的角色是系统管理员群组
               这个条件作废，还是要自动创建角色；
			sql = "update tt_k_user a set userrole = (select rolename from k_role where id = 1) where userrole = '' ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			*/
			
			//修改密码
			sql = "update tt_k_user set Password = 1 where ifnull(Password,'') = '' ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			sql = "update tt_k_user set Password = MD5(Password) ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			return result;
		} catch (Exception e) {
			Debug.print(Debug.iError,"访问失败",e);
			throw new MatechException("访问失败："+e.getMessage(),e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	public void updateData2()throws MatechException{
		PreparedStatement ps = null;
		PreparedStatement ps1 = null;
		ResultSet rs = null;
		try {
			String sql = "insert into k_department (departname,parentid)  select DISTINCT departmentid,'555555' from tt_k_user where departmentid not in ( select departname from k_department) and departmentid<>''";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			sql = "UPDATE tt_k_user a left join k_department b on departname = departmentid set departmentid = autoid ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			sql = "insert into tt_k_user (" +
			"	Name, loginid, Password, Sex, BornDate, " +
			"	Educational, Diploma, DepartID, Rank, Post, " +
			"	Specialty, ParentGroup, Popedom, IsTips, departmentid, " +
			"	ProjectPopedom, clientDogSysUi, state, userPhoto, userPhotoTemp, " +
			"	mobilePhone, phone, email, cpano, floor,identityCard,diplomatime,entrytime, " +
			"	house, station, userrole " +
			"	) " +
			"	select " +
			"	Name, loginid, Password, Sex, BornDate, " +
			"	Educational, Diploma, DepartID, Rank, Post, " +
			"	Specialty, ParentGroup, Popedom, IsTips, departmentid, " +
			"	ProjectPopedom, clientDogSysUi, state, userPhoto, userPhotoTemp, " +
			"	mobilePhone, phone, email, cpano, floor,identityCard,diplomatime,entrytime, " +
			"	house, station, ? " +
			"	from tt_k_user where id = ? ";
			ps1 = conn.prepareStatement(sql);
			
			sql = "select id,replace(ifnull(userrole,''),'，',',') as userrole  from tt_k_user where replace(ifnull(userrole,''),'，',',') like '%,%'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				String id = rs.getString("id");
				String userrole = rs.getString("userrole");
				
				String [] roles = userrole.split(","); 
				
				for (int i=0; i<roles.length;i++){
					if(roles[i] != null && !"".equals(roles[i])){
						ps1.setString(1, roles[i].trim());
						ps1.setString(2, id);
						ps1.addBatch();
					}
				}
				
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			
			ps1.executeBatch();
			DbUtil.close(ps1);
			
			sql = "delete  from tt_k_user where replace(ifnull(userrole,''),'，',',') like '%,%'";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			sql="insert into k_role (rolename,rolevalue,popedom) \n"   
					+"select DISTINCT userrole,'基于批量导入自动创建', \n"
					+"(select popedom from k_role order by id limit 1 \n"
					+") \n"
					+"from tt_k_user a \n"
					+"where ifnull(a.userrole,'') <> '' and a.userrole not in ( select rolename from k_role)";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			sql =" UPDATE tt_k_user a,\n" +
				 " (SELECT LoginID, FROM_DAYS(TO_DAYS('1900-01-01')+BornDate-2) AS BornDate FROM tt_k_user  WHERE INSTR(RIGHT(BornDate,20),'-')=0 AND BornDate <> '') b \n" +
				 " SET a.borndate = b.bornDate WHERE a.loginid = b.loginid ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			sql =" UPDATE tt_k_user a,\n" +
			 " (SELECT LoginID, FROM_DAYS(TO_DAYS('1900-01-01')+diplomatime-2) AS diplomatime FROM tt_k_user  WHERE INSTR(RIGHT(diplomatime,20),'-')=0 AND diplomatime <> '') b \n" +
			 " SET a.diplomatime = b.diplomatime WHERE a.loginid = b.loginid ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			sql =" UPDATE tt_k_user a,\n" +
			 " (SELECT LoginID, FROM_DAYS(TO_DAYS('1900-01-01')+entrytime-2) AS entrytime FROM tt_k_user  WHERE INSTR(RIGHT(entrytime,20),'-')=0 AND entrytime <> '') b \n" +
			 " SET a.entrytime = b.entrytime WHERE a.loginid = b.loginid ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			 
			
			
		}catch (Exception e) {
			Debug.print(Debug.iError,"访问失败",e);
			throw new MatechException("访问失败："+e.getMessage(),e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}


	public void updateData()throws MatechException{
		PreparedStatement ps = null;
		PreparedStatement ps1 = null;
		ResultSet rs = null;
		try {
			String sql = "insert into k_department (departname,parentid)  select DISTINCT departmentid,'555555' from tt_k_user where departmentid not in ( select departname from k_department) and departmentid<>''";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			sql = "UPDATE tt_k_user a left join k_department b on departname = departmentid set departmentid = autoid ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			//处理一个人员多个角色问题
//			方法：
//			这个只能这样，在导入后，你还没处理前，用
//			用SELECT * FROM 临时表 where role like '%;%' 取出来
//			然后
//			for 循环记录集{
//			    split(;)
//			    for 循环(){
//			       逐条插回到临时表 insert().....
//			    }
//			    delete 当前这条带;的记录 
//			}
			
			sql = "insert into tt_k_user (" +
			"	Name, loginid, Password, Sex, BornDate, " +
			"	Educational, Diploma, DepartID, Rank, Post, " +
			"	Specialty, ParentGroup, Popedom, IsTips, departmentid, " +
			"	ProjectPopedom, clientDogSysUi, state, userPhoto, userPhotoTemp, " +
			"	mobilePhone, phone, email, cpano, floor, " +
			"	house, station, userrole " +
			"	) " +
			"	select " +
			"	Name, loginid, Password, Sex, BornDate, " +
			"	Educational, Diploma, DepartID, Rank, Post, " +
			"	Specialty, ParentGroup, Popedom, IsTips, departmentid, " +
			"	ProjectPopedom, clientDogSysUi, state, userPhoto, userPhotoTemp, " +
			"	mobilePhone, phone, email, cpano, floor, " +
			"	house, station, ? " +
			"	from tt_k_user where id = ? ";
			ps1 = conn.prepareStatement(sql);
			
			sql = "select id,replace(ifnull(userrole,''),'；',';') as userrole  from tt_k_user where replace(ifnull(userrole,''),'；',';') like '%;%'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				String id = rs.getString("id");
				String userrole = rs.getString("userrole");
				
				String [] roles = userrole.split(";"); 
				
				for (int i=0; i<roles.length;i++){
					if(roles[i] != null && !"".equals(roles[i])){
						ps1.setString(1, roles[i].trim());
						ps1.setString(2, id);
						ps1.addBatch();
					}
				}
				
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			
			ps1.executeBatch();
			DbUtil.close(ps1);
			
			sql = "delete  from tt_k_user where replace(ifnull(userrole,''),'；',';') like '%;%'";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			sql="insert into k_role (rolename,rolevalue,popedom) \n"   
					+"select DISTINCT userrole,'基于批量导入自动创建', \n"
					+"(select popedom from k_role order by id limit 1 \n"
					+") \n"
					+"from tt_k_user a \n"
					+"where ifnull(a.userrole,'') <> '' and a.userrole not in ( select rolename from k_role)";
			ps = conn.prepareStatement(sql);
			ps.execute();
			
		}catch (Exception e) {
			Debug.print(Debug.iError,"访问失败",e);
			throw new MatechException("访问失败："+e.getMessage(),e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	public void insertData2()throws MatechException{
		PreparedStatement ps = null;
		try {
			//更新已有用户信息
			String sql = "UPDATE " +
			"	k_user a , tt_k_user b " +
			"	SET  " +
			"	  a.NAME = b.NAME, " +   
			"	  a.PASSWORD	  = b.PASSWORD, " +
			"	  a.Sex 	  = b.Sex , " +
			"	  a.BornDate	  = b.BornDate, " +
			"	  a.Educational	  = b.Educational, " +
			"	  a.Diploma 	  = b.Diploma , " +
			"	  a.DepartID	  = b.DepartID, " +
			"	  a.Rank 	  = b.Rank , " +
			"	  a.Post 	  = b.Post , " +
			"	  a.Specialty 	  = b.Specialty , " +
			"	  a.ParentGroup 	  = b.ParentGroup , " +
			"	  a.IsTips 	  = b.IsTips , " +
			"	  a.departmentid 	  = b.departmentid, " + 
			"	  a.state 	  = b.state," +
			"	  a.userPhoto 	  = b.userPhoto," +
			"	  a.userPhotoTemp 	  = b.userPhotoTemp," +
			"	  a.mobilePhone 	  = b.mobilePhone," +
			"	  a.phone 	  = b.phone," +
			"	  a.email 	  = b.email," +
			"	  a.cpano 	  = b.cpano," +
			"     a.phone     = b.phone," +
			"	  a.station   = b.station,"+
			"	  a.nation    = b.nation,"+
			"     a.diplomatime=b.diplomatime,"+
			"	  a.entrytime =b.entrytime,"+
			"     a.marriage  =b.marriage,"+
			"     a.place     =b.place,"+
			"	  a.residence =b.residence,"+
			"     a.politics  =b.politics,"+
			"	  a.partytime =b.partytime,"+
			"	  a.relationships = b.relationships,"+
			"	  a.profession = b.profession,"+
			"	  a.english = b.english,"+
			"	  a.compact =b.compact"+
			  
			"	WHERE a.departmentId = b.departmentId and a.identityCard = b.identityCard "+
			"	AND a.loginid <> 'admin' " +
			"	and a.state = 0 " ;
			System.out.println(sql);
			ps = conn.prepareStatement(sql);
			ps.execute();
			ps.execute("Flush tables");
			DbUtil.close(ps);
			
			//增加新用户	
			 sql= "insert into k_user(" +
				"Name,loginid,Password,Sex,BornDate,Educational," +
				"Diploma,DepartID,Rank,Post,Specialty,ParentGroup," +
				"Popedom,IsTips,departmentid,state," +
				"userPhoto,userPhotoTemp,mobilePhone,phone,email,cpano" +
				",identityCard,station,nation,diplomatime,entrytime,marriage,place,residence,politics,partytime,relationships,profession,english,`compact`) " +
				"select distinct " +
				"Name,loginid,Password,IF(Sex = '男','M',IF(Sex = '女','F',Sex)) as Sex,BornDate,Educational," +
				"Diploma,DepartID,Rank,Post,Specialty,ParentGroup," +
				"Popedom,IsTips,departmentid,0, " +
				"userPhoto,userPhotoTemp,mobilePhone,phone,email,cpano,identityCard,station,nation,diplomatime,entrytime, " +
				"marriage,place,residence,politics,partytime,relationships,profession,english,`compact` "+
				"from tt_k_user a where a.loginid not in(select b.loginid from k_user a,tt_k_user b where a.departmentId = b.departmentId and a.identityCard = b.identityCard " +
				"or (b.departmentid='' or b.departmentid is null)) and loginId <> 'admin'" ;
			ps = conn.prepareStatement(sql);
			ps.execute();
			ps.execute("Flush tables");
			DbUtil.close(ps);
			
			//更新角色表

			sql ="delete from k_userrole where userid in (select a.id from k_user a,tt_k_user b where a.identityCard = b.identityCard ) ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			sql = "insert into k_userrole (userid,rid) " +
			"	select distinct a.* from (" +
			"		select distinct a.id as userid,c.id as rid from k_user a , tt_k_user b,k_role c where a.state=0 and a.loginid=b.loginid and b.userrole = c.rolename" +
			"	) a left join k_userrole b on a.userid = b.userid and a.rid = b.rid" +
			"	where b.userid is null ";
			System.out.println(sql);
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			
			delTable(); 
		}catch (Exception e) {
			Debug.print(Debug.iError,"访问失败",e);
			throw new MatechException("访问失败："+e.getMessage(),e);
		} finally {
			DbUtil.close(ps);
		}
	}
	public void insertData()throws MatechException{
		PreparedStatement ps = null;
		try {
			//更新已有用户信息
//			String sql = "UPDATE " +
//			"	k_user a , tt_k_user b " +
//			"	SET  " +
//			"	  a.NAME = b.NAME, " +   
//			"	  a.PASSWORD	  = b.PASSWORD, " +
//			"	  a.Sex 	  = b.Sex , " +
//			"	  a.BornDate	  = b.BornDate, " +
//			"	  a.Educational	  = b.Educational, " +
//			"	  a.Diploma 	  = b.Diploma , " +
//			"	  a.DepartID	  = b.DepartID, " +
//			"	  a.Rank 	  = b.Rank , " +
//			"	  a.Post 	  = b.Post , " +
//			"	  a.Specialty 	  = b.Specialty , " +
//			"	  a.ParentGroup 	  = b.ParentGroup , " +
//			"	  a.IsTips 	  = b.IsTips , " +
//			"	  a.departmentid 	  = b.departmentid, " + 
//			"	  a.state 	  = b.state," +
//			"	  a.userPhoto 	  = b.userPhoto," +
//			"	  a.userPhotoTemp 	  = b.userPhotoTemp," +
//			"	  a.mobilePhone 	  = b.mobilePhone," +
//			"	  a.phone 	  = b.phone," +
//			"	  a.email 	  = b.email," +
//			"	  a.cpano 	  = b.cpano," +
//			"     a.phone     = b.phone," +
//			"	  a.identityCard=b.identityCard,"+
//			"	  a.station   = b.station,"+
//			"	  a.nation    = b.nation,"+
//			"     a.diplomatime=b.diplomatime,"+
//			"	  a.entrytime =b.entrytime,"+
//			"     a.marriage  =b.marriage,"+
//			"     a.place     =b.place,"+
//			"	  a.residence =b.residence,"+
//			"     a.politics  =b.politics,"+
//			"	  a.partytime =b.partytime,"+
//			"	  a.relationships = b.relationships,"+
//			"	  a.profession = b.profession,"+
//			"	  a.english = b.english,"+
//			"	  a.compact =b.compact"+
//			  
//			"	WHERE a.loginid = b.loginid and a.name = b.name" +
//			"	AND a.loginid <> 'admin' " +
//			"	and a.state = 0 " ;
//			System.out.println(sql);
//			ps = conn.prepareStatement(sql);
//			ps.execute();
//			ps.execute("Flush tables");
//			DbUtil.close(ps);
			
			//增加新用户	
			String sql= "insert into k_user(" +
				"Name,loginid,Password,Sex,BornDate,Educational," +
				"Diploma,DepartID,Rank,Post,Specialty,ParentGroup," +
				"Popedom,IsTips,departmentid,state," +
				"userPhoto,userPhotoTemp,mobilePhone,phone,email,cpano" +
				",identityCard,station,nation,diplomatime,entrytime,marriage,place,residence,politics,partytime,relationships,profession,english,`compact`) " +
				"select distinct " +
				"Name,loginid,Password,IF(Sex = '男','M',IF(Sex = '女','F',Sex)) as Sex,BornDate,Educational," +
				"Diploma,DepartID,Rank,Post,Specialty,ParentGroup," +
				"Popedom,IsTips,departmentid,0, " +
				"userPhoto,userPhotoTemp,mobilePhone,phone,email,cpano,identityCard,station,nation,diplomatime,entrytime, " +
				"marriage,place,residence,politics,partytime,relationships,profession,english,`compact` "+
				"from tt_k_user a where a.loginid not in (select b.loginid from k_user a , tt_k_user b where a.loginid=b.loginid and a.name=b.name OR (b.departmentid='' or b.departmentid is null) )";
			ps = conn.prepareStatement(sql);
			ps.execute();
			ps.execute("Flush tables");
			DbUtil.close(ps);
			
			//更新角色表
			sql = "insert into k_userrole (userid,rid) " +
			"	select distinct a.* from (" +
			"		select distinct a.id as userid,c.id as rid from k_user a , tt_k_user b,k_role c where a.state=0 and a.loginid=b.loginid and b.userrole = c.rolename" +
			"	) a left join k_userrole b on a.userid = b.userid and a.rid = b.rid" +
			"	where b.userid is null ";
			System.out.println(sql);
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			delTable(); 
		}catch (Exception e) {
			Debug.print(Debug.iError,"访问失败",e);
			throw new MatechException("访问失败："+e.getMessage(),e);
		} finally {
			DbUtil.close(ps);
		}
	}

	/**
	 * 验证选中的是否为系统管理员的方法
	 */
	public boolean SelectName(String id) throws MatechException {
		PreparedStatement ps = null;
		ResultSet rs = null;

		String sql = "select name from k_user where id =? ";


		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, id);
			rs = ps.executeQuery();

			rs.next();

			if (rs.getString("name").equals("系统管理员")){
				return true;
			}

		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return false;
	}

	/**
	 * 返回用户状态
	 * @param loginId
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public int validateUser(String loginId, String password, String roles) throws Exception {

		int state = -1;

		DbUtil dbUtil = new DbUtil(conn);
		Object[] object = null;

		String sql = " select count(1) from k_user where loginId=? ";
		object = new Object[] {loginId};

		int temp = dbUtil.queryForInt(sql, object);

		if(temp > 0) {
			sql = " select state from k_user where loginId=? and password=md5(?) ";
			object =  new Object[] {loginId, password};

			String userState = dbUtil.queryForString(sql, object);

			//如果用户名密码正确
			if(userState != null) {

				//如果用户状态正常
				if(userState.equals("0")) {

					//如果有角色权限限制
					if(!"".equals(roles)) {
						sql = " select count(1) from k_user a,k_userrole b "
							+ " where a.id=b.userid "
							+ " and loginId=? "
							+ " and b.rid in (" + roles + ") ";
						object = new Object[] {loginId};

						int userRoles = dbUtil.queryForInt(sql, object);

						//拥有角色权限
						if(userRoles > 0) {
							//状态正常
							state = USER_STATE_ENABLED;
						} else {
							//没有权限
							state = USER_STATE_POWERLESS;
						}

					} else {
						//状态正常
						state = USER_STATE_ENABLED;
					}

				} else {
					//被禁用
					state = USER_STATE_DISABLED;
				}
			} else {
				//密码错误
				state = USER_STATE_PWD_ERROR;
			}

		} else {
			//找不到用户
			state =USER_STATE_NOFOUND;
		}

		return state;
	}

	/**
	 * 验证用户是否在角色内
	 * @param userId
	 * @param role
	 * @return
	 * @throws Exception
	 */
	public boolean validateUserRole(String userId, String role) throws Exception {
		PreparedStatement ps = null;
        ResultSet rs = null;

        String roles = "-1";

        roles += "," + role;

        try {

          String sql = "select rid from k_userrole where userid = ? "
        	  		+ " and rid in (" + roles + ")";
          ps = conn.prepareStatement(sql);
          ps.setString(1, userId);
          rs = ps.executeQuery();

          if(rs.next()){
        	  return true;
          } else {
        	  return false;
          }
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
        }

        return false;
	}




	/**
	 * 添加或修改的方法
	 * @param pathName
	 * @param pathValue
	 * @return
	 */
	public boolean addOrUpdate(int userId,String pathName,String pathValue,String tj){
		PreparedStatement ps=null;
		try {
			if(tj.equalsIgnoreCase("add")){
				String sql="insert into k_userdef (ContrastID,Name,Value,Property) values(?,?,?,?)";
				ps = conn.prepareStatement(sql);
				
				ps.setInt(1, userId);
				ps.setString(2, pathName);
				ps.setString(3, pathValue);
				ps.setString(4, "workpath");
				boolean bl=ps.execute();
				System.out.println("往k_userdef表中添加记录是否成功? --->>  "+bl);
			
				ps.close();
			}else if(tj.equalsIgnoreCase("update")){
				String sql="update k_userdef set Name=?,Value =? where ContrastID=? and Property=?";
				ps = conn.prepareStatement(sql);
				ps.setString(1, pathName);
				ps.setString(2, pathValue);
				ps.setInt(3, userId);
				ps.setString(4, "workpath");
				boolean bl=ps.execute();
				System.out.println("往k_userdef表中修改记录是否成功? --->>  "+bl);
			}
			
			return true;
		} catch (Exception e) {
			// TODO 自动生成 catch 块
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		return false;
	}
	
	/**
	 * 获取部门列表
	 * @return
	 * @throws Exception
	 */
	public List<User> getUserList() throws Exception {
		
		List<User> list = new ArrayList<User>();
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			String sql = " select id,name from k_user order by istips,name ";
			
			ps = conn.prepareStatement(sql);
			
			rs = ps.executeQuery();
			
			User user = null;
			while(rs.next()) {
				user = new User();
				user.setId(rs.getString(1));
				user.setName(rs.getString(2));
				
				list.add(user);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return list;
	}
	
	/**
	 * 更新排序
	 * @param autoIds
	 * @return
	 * @throws Exception
	 */
	public int saveOrderBy(String autoIds) throws Exception {
		PreparedStatement ps = null;
		
		int result = 0;
		
		String[] autoId = autoIds.split(",");
		try {
			
			if(autoId != null) {
				String sql = " update asdb.k_user set istips=? where id=? ";
				
				ps = conn.prepareStatement(sql);
				
				for (int i = 0; i < autoId.length; i++) {
					ps.setString(1, String.valueOf(i));
					ps.setString(2, autoId[i]);
					
					if(ps.executeUpdate() > 0 ) {
						result ++;
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
		
		return result;
	}
	
	/**
	 * k_userdef 新增
	 * @param ContrastID
	 * @param pathName
	 * @param pathValue
	 * @param Property
	 * @return
	 */
	public boolean addUserdef(String ContrastID,String pathName,String pathValue,String Property){
		PreparedStatement ps=null;
		boolean result = false;
		try {
			 
				String sql="insert into k_userdef (ContrastID,Name,Value,Property) values(?,?,?,?)";
				ps = conn.prepareStatement(sql);
				
				ps.setString(1, ContrastID);
				ps.setString(2, pathName);
				ps.setString(3, pathValue);
				ps.setString(4, Property);
				ps.execute();
				
				result = true;
		 
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		return result;
	}
	/*
	 * 根据userid取权限
	 * 
	 */
	public String getPropertyById(String userId){
		PreparedStatement ps=null;
		ResultSet rs=null;
		String property=null;
		try {
			String sql="select max(r.property) from k_user u" 
						+" inner join k_userrole ku on u.id=ku.userid"
						+" inner join k_role r on r.id=ku.rid "
						+" where u.id="+userId+" ";
			ps=conn.prepareStatement(sql);
			rs=ps.executeQuery();
			if(rs.next()){
				property=rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		
		return property;
	}
	/*
	 * 根据id查找部门名称
	 */
	public String getDepartName(String departId){
		PreparedStatement ps=null;
		ResultSet rs=null;
		String name=null;
		try {
			String sql="select departname from k_department where autoid="+departId;
			ps=conn.prepareStatement(sql);
			rs=ps.executeQuery();
			if(rs.next()){
				name=rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		return name;
	}
	/*
	 * 根据人员id查找职级
	 */
	public String getRank(String userId){
		PreparedStatement ps=null;
		ResultSet rs=null;
		String rank=null;
		try {
			String sql="select rank from k_user where id="+userId;
			ps=conn.prepareStatement(sql);
			rs=ps.executeQuery();
			if(rs.next()){
				rank=rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
		return rank;
	}
	/*
	 * 根据职级名称，查找所有人员
	 */
	public List<String> getUserByRank(String rankName){
		List<String> list=null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		try {
			String sql="select id from k_user where rank="+rankName;
			ps=conn.prepareStatement(sql);
			rs=ps.executeQuery();
			while(rs.next()){
				if(list==null){
					list=new ArrayList<String>();
				}
				list.add(rs.getString(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return list;
	}
	/*
	 * 根据id查找外派人员
	 */
	public PartTime getPartTime(String autoId){
		PreparedStatement ps = null;
		PreparedStatement ps1 = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        PartTime uservo = new PartTime();
        try {
        	String sql="select * from k_partTime where id="+autoId;
        	String sql1 = "select rid from k_userrole where userid='"+autoId+"'";
        	ps1 = conn.prepareStatement(sql1);

        	rs1 = ps1.executeQuery();

        	String roles = "";

        	while(rs1.next()){
        		roles = roles+rs1.getString(1)+",";

        	}
        	if(!"".equals(roles)){
        		roles = roles.substring(0, roles.length()-1);
        	}

        	ps = conn.prepareStatement(sql);
        	rs = ps.executeQuery();
        	if(rs.next()){
        		uservo.setId(rs.getString("id"));
        		uservo.setName(rs.getString("name"));
        		uservo.setLoginid(rs.getString("Loginid"));
        		uservo.setSex(rs.getString("sex"));
        		uservo.setPassword(rs.getString("password"));
        		uservo.setBorndate(rs.getString("borndate"));
        		uservo.setEducational(rs.getString("educational"));
        		uservo.setDiploma(rs.getString("diploma"));
        		uservo.setDepartmentid(rs.getString("departmentid"));
        		uservo.setDepartid(rs.getString("departid"));
        		uservo.setRank(rs.getString("rank"));
        		uservo.setPost(rs.getString("post"));
        		uservo.setSpecialty(rs.getString("specialty"));
        		uservo.setRoles(roles);
        		uservo.setClientDogSysUi(rs.getString("clientDogSysUi"));
        		uservo.setUserPhoto(rs.getString("userPhoto")) ;
        		uservo.setUserPhotoTemp(rs.getString("userPhotoTemp")) ;
        		uservo.setMobilePhone(rs.getString("mobilePhone")) ;
        		uservo.setPhone(rs.getString("phone")) ;
        		uservo.setEmail(rs.getString("email")) ;
        		uservo.setCpano(rs.getString("cpano")) ;
        		
        		uservo.setFloor(rs.getString("floor")) ;
        		uservo.setHouse(rs.getString("house")) ;
        		uservo.setStation(rs.getString("station")) ;
        		
        		uservo.setPaperstype(rs.getString("paperstype"));
        		uservo.setPapersnumber(rs.getString("papersnumber"));
        		uservo.setNation(rs.getString("nation"));
        		uservo.setMarriage(rs.getString("marriage"));
        		uservo.setPlace(rs.getString("place"));

        		uservo.setResidence(rs.getString("residence"));
        		uservo.setPolitics(rs.getString("politics"));
        		uservo.setPartytime(rs.getString("partytime"));
        		uservo.setRelationships(rs.getString("relationships"));
        		uservo.setProfession(rs.getString("profession"));

        		uservo.setCompact(rs.getString("compact"));
        		uservo.setWorkstate(rs.getString("workstate"));
        		uservo.setLeavetype(rs.getString("leavetype"));
        		uservo.setEnglish(rs.getString("english"));
        		uservo.setDiplomatime(rs.getString("diplomatime"));
        		
        		uservo.setEntrytime(rs.getString("entrytime"));
        		uservo.setIdentityCard(rs.getString("identityCard"));
        		uservo.setIp(rs.getString("ip"));
        		uservo.setResume(rs.getString("resume"));
        	}

//        	uservo.setUserdefs(new UserdefService(conn).getUserdef(uservo.getId(), "user"));

        }catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(rs1);
			DbUtil.close(ps);
			DbUtil.close(ps1);
		}
		return uservo;
	}
	/*
	 * 保存外聘人员
	 */
	public void savePartTime(PartTime partTime){
		PreparedStatement ps = null;
		try {
			String sql="insert into k_parttime (`name`,loginid,password,sex,borndate," +
					   "educational,diploma,departid,rank,post," +
					   "specialty,ParentGroup,Popedom,IsTips,departmentid," +
					   "resume,clientDogSysUi,state,userPhoto,userPhotoTemp," +
					   "mobilePhone,phone,email,cpano,floor," +
					   "house,station,identityCard,paperstype,papersnumber," +
					   "nation,marriage,place,residence,politics," +
					   "partytime,relationships,profession,compact,workstate," +
					   "leavetype,english,diplomatime,entrytime,ip" +
					   ") values(?,?,?,?,?,  ?,?,?,?,?,  ?,?,?,?,?,  ?,?,?,?,?,  ?,?,?,?,?,  ?,?,?,?,?,  ?,?,?,?,?,  ?,?,?,?,?,  ?,?,?,?,?)";
			ps=conn.prepareStatement(sql);
			int i=1;
			ps.setString(i++, partTime.getName());
			ps.setString(i++, partTime.getLoginid());
			ps.setString(i++, partTime.getPassword());
			ps.setString(i++, partTime.getSex());
			ps.setString(i++, partTime.getBorndate());
			
			ps.setString(i++, partTime.getEducational());
			ps.setString(i++, partTime.getDiploma());
			ps.setString(i++, partTime.getDepartid());
			ps.setString(i++, partTime.getRank());
			ps.setString(i++, partTime.getPost());

			ps.setString(i++, partTime.getSpecialty());
			ps.setString(i++, partTime.getParentgroup());
			ps.setString(i++, partTime.getPopedom());
			ps.setString(i++, partTime.getIstips());
			ps.setString(i++, partTime.getDepartmentid());
			
			ps.setString(i++, partTime.getResume());
			ps.setString(i++, partTime.getClientDogSysUi());
			ps.setString(i++, partTime.getState());
			ps.setString(i++, partTime.getUserPhoto());
			ps.setString(i++, partTime.getUserPhotoTemp());
			
			ps.setString(i++, partTime.getMobilePhone());
			ps.setString(i++, partTime.getPhone());
			ps.setString(i++, partTime.getEmail());
			ps.setString(i++, partTime.getCpano());
			ps.setString(i++, partTime.getFloor());
			
			ps.setString(i++, partTime.getHouse());
			ps.setString(i++, partTime.getStation());
			ps.setString(i++, partTime.getIdentityCard());
			ps.setString(i++, partTime.getPaperstype());
			ps.setString(i++, partTime.getPapersnumber());
			
			ps.setString(i++, partTime.getNation());
			ps.setString(i++, partTime.getMarriage());
			ps.setString(i++, partTime.getPlace());
			ps.setString(i++, partTime.getResidence());
			ps.setString(i++, partTime.getPolitics());

			ps.setString(i++, partTime.getPartytime());
			ps.setString(i++, partTime.getRelationships());
			ps.setString(i++, partTime.getProfession());
			ps.setString(i++, partTime.getCompact());
			ps.setString(i++, partTime.getWorkstate());
			
			ps.setString(i++, partTime.getLeavetype());
			ps.setString(i++, partTime.getEnglish());
			ps.setString(i++, partTime.getDiplomatime());
			ps.setString(i++, partTime.getEntrytime());
			ps.setString(i++, partTime.getIp());
			
			ps.execute();

		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
	}
	/*
	 * 更新外聘人员
	 */
	public void updatePartTime(String id,PartTime partTime){
		PreparedStatement ps = null;
		try {
			String sql="update k_parttime set `name`=?,loginid=?,password=?,sex=?,borndate=?," +
					"Educational=?,Diploma=?,DepartID=?,Rank=?,Post=?," +
					"Specialty=?,ParentGroup=?,Popedom=?,IsTips=?,departmentid=?," +
					"resume=?,clientDogSysUi=?,state=?,userPhoto=?,userPhotoTemp=?," +
					"mobilePhone=?,phone=?,email=?,cpano=?,floor=?," +
					"house=?,station=?,identityCard=?,paperstype=?,papersnumber=?," +
					"nation=?,marriage=?,place=?,residence=?,politics=?," +
					"partytime=?,relationships=?,profession=?,compact=?,workstate=?," +
					"leavetype=?,english=?,diplomatime=?,entrytime=?,ip=?" +
					" where id="+id;
			ps=conn.prepareStatement(sql);
			int i=1;
			ps.setString(i++, partTime.getName());
			ps.setString(i++, partTime.getLoginid());
			ps.setString(i++, partTime.getPassword());
			ps.setString(i++, partTime.getSex());
			ps.setString(i++, partTime.getBorndate());
			
			ps.setString(i++, partTime.getEducational());
			ps.setString(i++, partTime.getDiploma());
			ps.setString(i++, partTime.getDepartid());
			ps.setString(i++, partTime.getRank());
			ps.setString(i++, partTime.getPost());

			ps.setString(i++, partTime.getSpecialty());
			ps.setString(i++, partTime.getParentgroup());
			ps.setString(i++, partTime.getPopedom());
			ps.setString(i++, partTime.getIstips());
			ps.setString(i++, partTime.getDepartmentid());
			
			ps.setString(i++, partTime.getResume());
			ps.setString(i++, partTime.getClientDogSysUi());
			ps.setString(i++, partTime.getState());
			ps.setString(i++, partTime.getUserPhoto());
			ps.setString(i++, partTime.getUserPhotoTemp());
			
			ps.setString(i++, partTime.getMobilePhone());
			ps.setString(i++, partTime.getPhone());
			ps.setString(i++, partTime.getEmail());
			ps.setString(i++, partTime.getCpano());
			ps.setString(i++, partTime.getFloor());
			
			ps.setString(i++, partTime.getHouse());
			ps.setString(i++, partTime.getStation());
			ps.setString(i++, partTime.getIdentityCard());
			ps.setString(i++, partTime.getPaperstype());
			ps.setString(i++, partTime.getPapersnumber());
			
			ps.setString(i++, partTime.getNation());
			ps.setString(i++, partTime.getMarriage());
			ps.setString(i++, partTime.getPlace());
			ps.setString(i++, partTime.getResidence());
			ps.setString(i++, partTime.getPolitics());

			ps.setString(i++, partTime.getPartytime());
			ps.setString(i++, partTime.getRelationships());
			ps.setString(i++, partTime.getProfession());
			ps.setString(i++, partTime.getCompact());
			ps.setString(i++, partTime.getWorkstate());
			
			ps.setString(i++, partTime.getLeavetype());
			ps.setString(i++, partTime.getEnglish());
			ps.setString(i++, partTime.getDiplomatime());
			ps.setString(i++, partTime.getEntrytime());
			ps.setString(i++, partTime.getIp());
			
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
	}
	/*
	 * 删除外聘人员
	 */
	public void delPartTime(String id){
		PreparedStatement ps = null;
		try {
			String sql="delete from k_parttime where id="+id;
			ps=conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
	}
	
	
	/**
	 * 增加人员详细 左边的树
	 * @param detailsTree
	 */
	public void saveDetailsTree(UserDetailsTree detailsTree){
		PreparedStatement ps = null;
		try {
			String sql=" INSERT INTO `asdb`.`k_userdetailstree` (`id`,`text`,`url`,`orderby`,`isShow`,`createDate`,`createUser`,`property`) \n"
						+" VALUES (?,?,?,?,?,?,?,?);";
			ps=conn.prepareStatement(sql);
			int i=1;
			ps.setString(i++, detailsTree.getId());
			ps.setString(i++, detailsTree.getText());
			ps.setString(i++, detailsTree.getUrl());
			ps.setString(i++, detailsTree.getOrderby());
			ps.setString(i++, detailsTree.getIsShow());
			ps.setString(i++, detailsTree.getCreateDate());
			ps.setString(i++, detailsTree.getCreateUser());
			ps.setString(i++, detailsTree.getProperty());
			ps.execute();

		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
	}
 
	/**
	 * 更新人员详细 左边的树
	 * @param detailsTree
	 */
	public void updateDetailsTree(UserDetailsTree detailsTree){
		PreparedStatement ps = null;
		try {
			String sql="update k_userdetailstree set `id`=?,`text`=?,`url`=?,`orderby`=?,`isShow`=? where autoId=? ";
			ps=conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, detailsTree.getId());
			ps.setString(i++, detailsTree.getText());
			ps.setString(i++, detailsTree.getUrl());
			ps.setString(i++, detailsTree.getOrderby());
			ps.setString(i++, detailsTree.getIsShow());
			ps.setString(i++, detailsTree.getAutoId());
			
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
	}
 
	/**
	 *  删除人员详细 左边的树
	 * @param autoId
	 */
	public void delDetailsTree(String autoId){
		PreparedStatement ps = null;
		try {
			String sql="delete from k_userdetailstree where autoId=?";
			ps=conn.prepareStatement(sql);
			ps.setString(1, autoId);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 得到 人员信息树的信息
	 * @param autoId
	 * @return
	 */
	public UserDetailsTree getUserDetailsTree(String autoId){
		 PreparedStatement ps = null;
		 ResultSet rs = null;
		 UserDetailsTree detailsTree = new UserDetailsTree();
		try {
			String sql=" SELECT `id`,`text`,`url`,`orderby`,`isShow`,`createDate`,`createUser`,`property` FROM `asdb`.`k_userdetailstree` where  autoId=? order by orderby ";
			ps=conn.prepareStatement(sql);
			int i=1;
			ps.setString(i++, autoId);
			rs  = ps.executeQuery();
			if(rs.next()){
				detailsTree.setAutoId(autoId);
				detailsTree.setId(rs.getString("id"));
				detailsTree.setText(rs.getString("text"));
				detailsTree.setOrderby(rs.getString("orderby"));
				detailsTree.setUrl(rs.getString("url"));
				detailsTree.setIsShow(rs.getString("isShow"));
				detailsTree.setCreateDate(rs.getString("createDate"));
				detailsTree.setCreateUser(rs.getString("createUser"));
				detailsTree.setProperty(rs.getString("property"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
			DbUtil.close(rs);
		}
		return detailsTree;
	}
	
	/**
	 * 得到全部 人员信息树
	 * @param autoId
	 * @return
	 */
	public List<Map<String, String>> getListMapUserDetailsTree(){
		 PreparedStatement ps = null;
		 ResultSet rs = null;
		 
		 List<Map<String, String>> listMaps = new ArrayList<Map<String,String>>();
		try {
			String sql=" SELECT autoId,`id`,`text`,`url`,`orderby`,`isShow`,`createDate`,`createUser`,`property` FROM `asdb`.`k_userdetailstree` where isShow=1  order by orderby ";
			ps=conn.prepareStatement(sql);
			rs  = ps.executeQuery();
			while(rs.next()){
				Map<String, String> map = new HashMap<String, String>();
				map.put("autoId",rs.getString("autoId"));
				map.put("id",rs.getString("id"));
				map.put("text",rs.getString("text"));
				map.put("orderby",rs.getString("orderby"));
				map.put("url",rs.getString("url"));
				map.put("isShow",rs.getString("isShow"));
				map.put("createDate",rs.getString("createDate"));
				map.put("createUser",rs.getString("createUser"));
				map.put("property",rs.getString("property"));
				
				listMaps.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
			DbUtil.close(rs);
		}
		return listMaps;
	}
	
	/**
	 * list
	 * @return
	 */
	public List<UserDetailsTree> getListUserDetailsTree(){
		 PreparedStatement ps = null;
		 ResultSet rs = null;
		 
		 List<UserDetailsTree> listMaps = new ArrayList<UserDetailsTree>();
		try {
			String sql=" SELECT autoId,`id`,`text`,`url`,`orderby`,`isShow`,`createDate`,`createUser`,`property` FROM `asdb`.`k_userdetailstree` where isShow=1  order by abs(orderby) ";
			ps=conn.prepareStatement(sql);
			rs  = ps.executeQuery();
			while(rs.next()){
				UserDetailsTree detailsTree = new UserDetailsTree();
				detailsTree.setAutoId(rs.getString("autoId"));
				detailsTree.setId(rs.getString("id"));
				detailsTree.setText(rs.getString("text"));
				detailsTree.setOrderby(rs.getString("orderby"));
				detailsTree.setUrl(rs.getString("url"));
				detailsTree.setIsShow(rs.getString("isShow"));
				detailsTree.setCreateDate(rs.getString("createDate"));
				detailsTree.setCreateUser(rs.getString("createUser"));
				detailsTree.setProperty(rs.getString("property"));
				
				listMaps.add(detailsTree);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
			DbUtil.close(rs);
		}
		return listMaps;
	}
	
	
}