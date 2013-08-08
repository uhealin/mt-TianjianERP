package com.matech.audit.service.process.impl.hr;

import java.sql.Connection;
import java.util.List;
import java.util.UUID;

import org.jbpm.api.listener.EventListenerExecution;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.employe.model.EmployeeApplyVO;
import com.matech.audit.service.employe.model.EmployeeVO;
import com.matech.audit.service.employment.subset.SubsetFamilyTempVO;
import com.matech.audit.service.employment.subset.SubsetFamilyVO;

import com.matech.audit.service.hr.model.ResumeVO;
import com.matech.audit.service.process.impl.base.NodeHandler;
import com.matech.audit.service.user.model.UserVO;
import com.matech.framework.pub.db.DbUtil;

public class ResumeApplyCheckNodeHandler extends NodeHandler {

	@Override
	public void nodeStart(EventListenerExecution execution) {
		// TODO Auto-generated method stub

	}

	@Override
	public void nodeEnd(EventListenerExecution execution) {
		// TODO Auto-generated method stub
        String uuid=(String)execution.getVariable("uuid");
        Connection conn=null;
		DbUtil dbUtil=null;
		//WebUtil webUtil=new WebUtil(request, response);
		//UserSession userSession=webUtil.getUserSession();
		int eff=0;
		String re="";
		EmployeeApplyVO employApplyVO=null;
		EmployeeVO employVO=null;
		UserVO userVO=new UserVO();
		try{
			conn=new DBConnect().getConnect();
			conn.setAutoCommit(false);
			dbUtil=new DbUtil(conn);
			employApplyVO=dbUtil.load(EmployeeApplyVO.class, uuid);
			employApplyVO.setState("1");
		    eff+=dbUtil.update(employApplyVO);
		    employVO=dbUtil.load(EmployeeVO.class, uuid);
		    userVO.setName(employVO.getName());
		    userVO.setParentGroup("自定义");
		    userVO.setIsTips(1);
		    userVO.setEmtype("001");
		    userVO.setDepartmentid(employApplyVO.getAssign_departmentid());
		    String pccpa_id=UUID.randomUUID().toString();
		    userVO.setPccpa_id(pccpa_id);
		    eff+=dbUtil.insert(userVO);
		    if(eff<1){
		    	throw new Exception("数据保存失败");
		    }
		    conn.commit();
		    userVO=dbUtil.select(UserVO.class, "select * from {0} where pccpa_id=?", pccpa_id).get(0);
		    List<SubsetFamilyTempVO> subsetFamilyTempVOs=dbUtil.select(SubsetFamilyTempVO.class,"select * from {0} where userid=?", employApplyVO.getTemp_userid());
		    for(SubsetFamilyTempVO subsetFamilyTempVO :subsetFamilyTempVOs ){
		    	SubsetFamilyVO subsetFamilyVO=new SubsetFamilyVO();
		    	subsetFamilyVO.setA0100(subsetFamilyTempVO.getA0100());
		    	subsetFamilyVO.setA7905(subsetFamilyTempVO.getA7905());
		    	subsetFamilyVO.setA7910(subsetFamilyTempVO.getA7910());
		    	subsetFamilyVO.setA7920(subsetFamilyTempVO.getA7920());
		    	subsetFamilyVO.setA7940(subsetFamilyTempVO.getA7940());
		    	subsetFamilyVO.setB0110(subsetFamilyTempVO.getB0110());
		    	subsetFamilyVO.setDepartmentid(subsetFamilyTempVO.getDepartmentid());
		    	subsetFamilyVO.setUserid(String.valueOf(userVO.getId()));
		        subsetFamilyVO.setUuid(UUID.randomUUID().toString());
		        dbUtil.insert(subsetFamilyVO);
		    }
		}catch(Exception ex){
			re=ex.getLocalizedMessage();
		}finally{
			DbUtil.close(conn);
		}
	}

}
