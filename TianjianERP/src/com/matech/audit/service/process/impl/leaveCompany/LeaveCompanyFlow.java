package com.matech.audit.service.process.impl.leaveCompany;

import java.sql.Connection;

import org.jbpm.api.listener.EventListenerExecution;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.process.impl.base.NodeHandler;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.WebUtil;

public class LeaveCompanyFlow extends NodeHandler {

	@Override
	public void nodeStart(EventListenerExecution execution) {
		// TODO Auto-generated method stub

	}

	@Override
	public void nodeEnd(EventListenerExecution execution) {
		// TODO Auto-generated method stub
        String uuid=(String)execution.getVariable("uuid");

        String userid=(String)execution.getVariable("userId");
        String sql="select userId from k_leaveOfficeTJ where uuid='"+uuid+"'";
        String sqlUser="UPDATE k_user SET name=CONCAT(name,'(离)'),loginid=NULL, state=1 WHERE id =(select userId from k_leaveOfficeTJ where uuid='"+uuid+"')";
        
        Connection conn=null;
		DbUtil dbUtil=null;
		try{
			conn=new DBConnect().getConnect();
			conn.setAutoCommit(false);
			dbUtil=new DbUtil(conn);
//			dbUtil.execute(sqlUser);
			int i=dbUtil.executeUpdate(sqlUser);
//			int j=dbUtil.queryForInt(sql);
//	        System.out.println("uuid---------"+uuid+" : "+userid+" : --int+++--"+i+" ： ----intj"+j);
		    conn.commit();
		    
		   
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		
		
//		WebUtil webUtil=new WebUtil(request, response);
//		UserSession userSession=webUtil.getUserSession();
//		int eff=0;
//		String re="";
//		EmployeeApplyVO employApplyVO=null;
//		EmployeeVO employVO=null;
//		UserVO userVO=new UserVO();
//		try{
//			conn=new DBConnect().getConnect();
//			conn.setAutoCommit(false);
//			dbUtil=new DbUtil(conn);
//			employApplyVO=dbUtil.load(EmployeeApplyVO.class, uuid);
//			employApplyVO.setState("1");
//		    eff+=dbUtil.update(employApplyVO);
//		    employVO=dbUtil.load(EmployeeVO.class, uuid);
//		    userVO.setName(employVO.getName());
//		    userVO.setParentGroup("自定义");
//		    userVO.setIsTips(1);
//		    userVO.setEmtype("001");
//		    userVO.setDepartmentid(employApplyVO.getAssign_departmentid());
//		    String pccpa_id=UUID.randomUUID().toString();
//		    userVO.setPccpa_id(pccpa_id);
//		    eff+=dbUtil.insert(userVO);
//		    if(eff<1){
//		    	throw new Exception("数据保存失败");
//		    }
//		    conn.commit();
//		    userVO=dbUtil.select(UserVO.class, "select * from {0} where pccpa_id=?", pccpa_id).get(0);
//		    List<SubsetFamilyTempVO> subsetFamilyTempVOs=dbUtil.select(SubsetFamilyTempVO.class,"select * from {0} where userid=?", employApplyVO.getTemp_userid());
//		    for(SubsetFamilyTempVO subsetFamilyTempVO :subsetFamilyTempVOs ){
//		    	SubsetFamilyVO subsetFamilyVO=new SubsetFamilyVO();
//		    	subsetFamilyVO.setA0100(subsetFamilyTempVO.getA0100());
//		    	subsetFamilyVO.setA7905(subsetFamilyTempVO.getA7905());
//		    	subsetFamilyVO.setA7910(subsetFamilyTempVO.getA7910());
//		    	subsetFamilyVO.setA7920(subsetFamilyTempVO.getA7920());
//		    	subsetFamilyVO.setA7940(subsetFamilyTempVO.getA7940());
//		    	subsetFamilyVO.setB0110(subsetFamilyTempVO.getB0110());
//		    	subsetFamilyVO.setDepartmentid(subsetFamilyTempVO.getDepartmentid());
//		    	subsetFamilyVO.setUserid(String.valueOf(userVO.getId()));
//		        subsetFamilyVO.setUuid(UUID.randomUUID().toString());
//		        dbUtil.insert(subsetFamilyVO);
//		    }
//		}catch(Exception ex){
//			re=ex.getLocalizedMessage();
//		}finally{
//			DbUtil.close(conn);
//		}
	}

}
