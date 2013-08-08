package com.matech.audit.service.user;

import java.util.List;

import org.junit.Test;

import com.matech.audit.service.department.model.DepartmentVO;
import com.matech.audit.service.department.model.KDepartmentVO;
import com.matech.audit.service.user.model.UserVO;
import com.matech.audit.service.userpopedom.model.UserPeopedomVO;
import com.matech.framework.pub.util.StringUtil;
import com.matech.framework.pub.util.TestUtil;

public class UserTest extends TestUtil {

	
	
	
	@Test
	public void copyUserPeopedom(){
		List<UserPeopedomVO> userPeopedomVOs=dbUtil.select(UserPeopedomVO.class ,
				"select * from {0} where userid=?","58078");
		String[] uids={"57549","57510","57730","56702","58078","57705","57250","57394","57880","56915","57882","58077","58076"};
		List<UserVO> users=dbUtil.select(UserVO.class, "select * from {0}");
		//for(String uid:uids){
		for(UserVO userVO:users){
		//UserVO userVO;
			try {
				//userVO = dbUtil.load(UserVO.class, Integer.parseInt(uid));
				List<KDepartmentVO> departmentVOs=dbUtil.select(KDepartmentVO.class,
					    "SELECT * FROM  {0} WHERE areaid IN (SELECT areaid FROM {0}  WHERE autoid=?)" , Integer.parseInt(userVO.getDepartmentid()));
				String dids="";
				for(KDepartmentVO departmentVO :departmentVOs){
					dids+=departmentVO.getAutoid()+",";
				}
				//dbUtil.execute("delect k_userpeopedom where userid", args)
				for(UserPeopedomVO userPeopedomVO:userPeopedomVOs){
					UserPeopedomVO temp=new UserPeopedomVO();
					
					temp.setDepartmentid(dids);
					temp.setMenuid(userPeopedomVO.getMenuid());
					temp.setProperty(userPeopedomVO.getProperty());
					temp.setUserid(String.valueOf(userVO.getId()));
					dbUtil.delete(userPeopedomVO);
					dbUtil.insert(temp);
				}
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
