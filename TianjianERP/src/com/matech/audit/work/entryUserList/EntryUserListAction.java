package com.matech.audit.work.entryUserList;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.service.cadet.model.CadetVO;
import com.matech.audit.service.employe.model.EmployeeVO;
import com.matech.audit.service.entryUserList.EntryUserListVO;
import com.matech.audit.service.official.model.Official;
import com.matech.audit.service.user.model.User;
import com.matech.audit.service.user.model.UserVO;
import com.matech.framework.pub.db.DBConnect;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.MD5;
import com.matech.framework.pub.util.StringUtil;

public class EntryUserListAction extends MultiActionController {

	private String list="";
	private String add="entryUserList/add.jsp";
	
	public ModelAndView addUserSkip(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelAndView=new ModelAndView(add);
		ASFuntion ASF=new ASFuntion();
		String cardNum="";
		String uuid=ASF.showNull(request.getParameter("uuid"));
		Connection conn=null;
		DbUtil dbUtil=null;
		Official officialVO=null;
		EmployeeVO employeeVO=null;
		try {
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			//officialVO=dbUtil.load(Official.class,uuid);
			//如果转正申请里面没有，则去入职表里面取
			//if(StringUtil.isBlank(officialVO.getCardNum())){
				employeeVO=dbUtil.load(EmployeeVO.class, uuid);
				modelAndView.addObject("employeeVO",employeeVO);
				
				modelAndView.addObject("cardNum",employeeVO.getIdcard());
				modelAndView.addObject("from","employeeVO");
				
			//}else{
				//modelAndView.addObject("officialVO",officialVO);
				//modelAndView.addObject("cardNum",officialVO.getCardNum());
				//modelAndView.addObject("from","officialVO");
			//}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		modelAndView.addObject("uuid",uuid);
		return modelAndView;
	}
	
	public ModelAndView addUser(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelAndView=new ModelAndView(add);
		ASFuntion ASF=new ASFuntion();
		String loginid=ASF.showNull(request.getParameter("loginid"));
		String email=ASF.showNull(request.getParameter("email"));

		String from=ASF.showNull(request.getParameter("from"));
		String cardNum=ASF.showNull(request.getParameter("cardNum"));
		String uuid=ASF.showNull(request.getParameter("uuid"));
		String rolevalue=request.getParameter("roles");
		String areaid="";
		Connection conn=null;
		DbUtil dbUtil=null;
		Official officialVO=null;
		EmployeeVO employeeVO=null;
		UserVO userVO=new UserVO();
		try {
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			if("officialVO".equals(from)){
				officialVO=dbUtil.load(Official.class,uuid);
				cardNum=officialVO.getCardNum();
				try {
					areaid = dbUtil.queryForString("select areaid from k_department where autoid="+officialVO.getDepartmentId());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				userVO.setLoginid(loginid);
				userVO.setEmail(email);
				userVO.setName(officialVO.getName());
				String password=cardNum.substring(cardNum.length()-7, cardNum.length());
				userVO.setPassword(MD5.getMD5String(password));
				userVO.setEmtype("001");
				userVO.setState(0);
				userVO.setDepartID(areaid);
				userVO.setDepartmentid(officialVO.getDepartmentId());
				userVO.setIdentityCard(cardNum);
				userVO.setSex(officialVO.getSex());
				userVO.setParentGroup("自定义");
				userVO.setIsTips(1);
				
				officialVO.setList_state("已审核");
				dbUtil.update(officialVO);
			}

			if("employeeVO".equals(from)){
				employeeVO=dbUtil.load(EmployeeVO.class,uuid);
				cardNum=employeeVO.getIdcard();
//				//从实习生表取部门
//				CadetVO cadetVO=null;
//				if(StringUtil.isBlank(employeeVO.getDepartmentId()))
//				cadetVO=dbUtil.load(CadetVO.class, "idcard",cardNum);
				areaid="";
				userVO.setLoginid(loginid);
				userVO.setEmail(email);
				userVO.setName(employeeVO.getName());
				String password=cardNum.substring(cardNum.length()-7, cardNum.length());
				userVO.setPassword(MD5.getMD5String(password));
				userVO.setEmtype("001");
				userVO.setDepartmentid(employeeVO.getDepartmentId());
				try {
					areaid = dbUtil.queryForString("select areaid from k_department where autoid="+employeeVO.getDepartmentId());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				userVO.setDepartID(areaid);
				userVO.setState(0);
				userVO.setIdentityCard(cardNum);
				userVO.setSex(employeeVO.getSex());
				userVO.setParentGroup("自定义");
				userVO.setIsTips(1);
			}

		    List<UserVO> users=dbUtil.select(UserVO.class, "select * from {0} where identitycard=?  ", cardNum);
		    if(users.size()>0){
		    	userVO.setId(users.get(0).getId());
		    	dbUtil.update(userVO);
		    }else{
			dbUtil.insert(userVO);
		    }
			
			EntryUserListVO entryUserListVO=new EntryUserListVO();
			entryUserListVO.setUuid(StringUtil.getUUID());
			entryUserListVO.setList_idcard(cardNum);
			dbUtil.insert(entryUserListVO);

			
			UserVO usernew=dbUtil.load(UserVO.class, "loginid",loginid);
			if(!StringUtil.isBlank(usernew.getId()+"")&&!StringUtil.isBlank(rolevalue))
			{
				try {
					dbUtil.execute("insert into k_userrole(userid,rid) values(?,?)", new Object[]{usernew.getId(),rolevalue});
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				
			}
			
			response.sendRedirect("formDefine.do?method=formListView&formTypeId=bc9dcfb0-c15e-45f5-92cb-0ac89138b83a&uuid=740f0967-be27-424e-8271-0c5ad9689c43");
//			response.sendRedirect("formDefine.do?method=formListView&formTypeId=bc9dcfb0-c15e-45f5-92cb-0ac89138b83a&uuid=7c0bba1b-2371-4b78-a192-6169ed185c6d");
		} catch (Exception e) {
//			try {
//				conn.rollback();
//			} catch (SQLException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
			e.printStackTrace();
		}
		return null;
	}
}
