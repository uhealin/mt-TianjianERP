package com.matech.audit.service.doc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.axis.types.Id;


import com.matech.audit.service.department.model.KAreaVO;
import com.matech.audit.service.department.model.KDepartmentVO;
import com.matech.audit.service.doc.model.DocFlowVO;
import com.matech.audit.service.doc.model.DocLogVO;

import com.matech.audit.service.doc.model.DocRecVO;
import com.matech.audit.service.form.model.FormVO;
import com.matech.audit.service.placard.PlacardService;
import com.matech.audit.service.placard.model.PlacardTable;
import com.matech.audit.service.user.model.User;
import com.matech.audit.service.user.model.UserVO;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.StringUtil;
import com.matech.sms.SmsOpt;
import com.matech.sms.SmsService;
import com.mysql.jdbc.Driver;
import com.sun.mail.handlers.message_rfc822;

public class DocRecService {

	private Connection conn;
	private DbUtil dbUtil;
	private PlacardService placardService;
	private SmsService smsService;
	
	public DocRecService(Connection conn){
		this.conn=conn;
	    try {
			this.dbUtil=new DbUtil(conn);
			placardService = new PlacardService(conn);
			smsService=new SmsService(conn);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public List<String> doToDo(DocRecVO docRecVO,UserSession userSession,User assigner){
		List<String> results=new ArrayList<String>();
		
		
		DbUtil dbUtil=null;
		
		StringBuffer sqlPattern=new StringBuffer();
		String sql="";
	    sqlPattern.append("UPDATE oa_doc_rec SET ")
	    .append("handler_ids=REPLACE(handler_ids,?,?), ")
	    .append("handler_names=REPLACE(handler_names,?,?) ")
	    .append(" where  rec_doc_no=? and handler_ids like ?")
	    ;
	    sql=sqlPattern.toString();
	    PreparedStatement ps=null;
	    ResultSet rs=null;
	    int i=0;
	    String result="";
		try{
	    dbUtil=new DbUtil(conn);
		if(assigner!=null){
			
			ps=conn.prepareStatement(sql);
			ps.setString(1, userSession.getUserId());
			ps.setString(2, assigner.getId());
			ps.setString(3,userSession.getUserName());
			ps.setString(4,assigner.getName());
		    ps.setString(5, docRecVO.getRec_doc_no());
		    ps.setString(6, "%"+userSession.getUserId()+"%");
			//i+=ps.executeUpdate();
		    docRecVO.setCreater_id(assigner.getId());
		    docRecVO.setCreater_name(assigner.getName());
		    i+=dbUtil.update(docRecVO);
			if(i==1){
				result=MessageFormat.format("收文 {0} 成功指派给 {1}", docRecVO.getRec_doc_no(),assigner.getName());
			}
		}
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
           DbUtil.close(null, ps, rs);
		}
		result=MessageFormat.format("收文 {0} 待办", docRecVO.getRec_doc_no());
		results.add(result);
		return results;
	}
	
	public List<String> doCheck(DocRecVO docRecVo,UserSession userSession){
		StringBuffer sqlPattern=new StringBuffer();
		String sql="";
	    sqlPattern.append("UPDATE oa_doc_rec SET ")
	    .append("handler_ids=REPLACE(handler_ids,?,''), ")
	    .append("handler_names=REPLACE(handler_names,?,'') ")
	    .append(" where  rec_doc_no=? and handler_ids like ?")
	    ;
	    sql=sqlPattern.toString();
	    DbUtil dbUtil=null;
	    PreparedStatement ps;
	    DocFlowVO flow=null,nextFlow=null;
	    DocLogVO log=new DocLogVO();
	    List<String> results=new ArrayList<String>();
	    String re="";
	    int i=0;
		try {
			conn.setAutoCommit(false);
			dbUtil= new DbUtil(conn);
			ps = conn.prepareStatement(sql);
			
		    ps.setString(1, userSession.getUserId());
		    ps.setString(2, userSession.getUserName());
		    ps.setString(3, docRecVo.getRec_doc_no());
		    ps.setString(4, "%"+userSession.getUserId()+"%");
		    i+=ps.executeUpdate();
		    if(i==1){

		    	docRecVo=dbUtil.load(docRecVo,docRecVo.getUuid());
		    	//flow=dbUtil.load(DocFlowVO.class, "code",docRecVo.ge);
		    	log.setDoc_no(docRecVo.getRec_doc_no());
		    	log.setHandle_time(StringUtil.getCurDateTime());
		    	log.setHandler_id(userSession.getUserId());
		    	log.setHandler_name(userSession.getUserName());
		    	//log.setNode_code(flow.getCode());
		    	//log.setNode_name(flow.getName());
		    	log.setUuid(UUID.randomUUID().toString());
		    	log.setRemark("");
		    	log.setCtype("收文");
		    	i+=dbUtil.insert(log);
		    	if(docRecVo.getHandler_ids().replaceAll(",", "").trim().length()==0){
		    	    docRecVo.setHandler_ids("");
		    	    docRecVo.setHandler_names("");
		    	    i+=dbUtil.update(docRecVo);
		    	    if(i==3){
		    	        re=MessageFormat.format("收文 {0} 已完成所有审核", docRecVo.getRec_doc_no());
		    	    
		    	    }
		    	}else{
		    		re=MessageFormat.format("收文 {0} 成功审核", docRecVo.getRec_doc_no());
		    	}
		    	conn.commit();
		    }else {
	    		 re="你并不属于处理人";
	    	}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			re="操作异常:"+e.getLocalizedMessage();
		}
			
		results.add(re);
	
        return results;
	}
	
	public DocRecVO addRoam_range(DocRecVO docRecVO,String[] aids){
		
		String[] rids=docRecVO.getRoam_range_ids().split(",");
		Set<String> allIds=new HashSet<String>();
		for(String aid:aids){
			if(!aid.isEmpty()){
				allIds.add(aid);
			}
		}
		for(String aid:rids){
			if(!aid.isEmpty()){
				allIds.add(aid);
			}
		}
		String strAllIds=StringUtil.getStringFromArray(allIds.toArray(new String[allIds.size()]),",");
		List<UserVO> userVOs=dbUtil.select(UserVO.class, "select * from {0} where id in ("+StringUtil.trim(strAllIds,",")+")");
		String ids="",names="";
		for(UserVO userVO :userVOs){
			ids+=userVO.getId()+",";
			names+=userVO.getName()+",";
		}
		docRecVO.setRoam_range_ids(ids);
		docRecVO.setRoam_range_names(names);
		return docRecVO;
	}

	public int remindStart(DocRecVO docRecVO,boolean isTimeout){
		return remindStart(docRecVO, new String[]{},isTimeout);
	}
	
	public int remindCreater(DocRecVO docRecVO,boolean isTimeout){
		int eff=0;
		String strPattern="《{0}》收文签转流程结束，请尽快查阅。";
		String subfix="end";
	    if(isTimeout){
	    	strPattern="《{0}》收文将于今天到期，但签转流程尚未结束，请处理，谢谢！";
	    	subfix="timeout";
	    }
		
		String context=MessageFormat.format(strPattern,
		   docRecVO.getFile_name() 
		);
		try {
			UserVO createrVO=dbUtil.load(UserVO.class,Integer.parseInt(docRecVO.getCreater_id()));
			SmsOpt.sendSm(createrVO.getMobilePhone(), context,docRecVO.getUuid()+":"+subfix);
	  		//smsService.createSms(createrVO.getMobilePhone(), context, docRecVO.getUuid()+":hq");
			eff++;
			if(!isTimeout){
			    PlacardTable placardTable=new PlacardTable();
				placardTable.setIsReversion(0);
				placardTable.setAddresserTime(StringUtil.getCurDateTime());
				placardTable.setCaption("收文传阅提醒");
				placardTable.setMatter(context);
				placardTable.setIsRead(0);
				placardTable.setIsNotReversion(0);
				placardTable.setUuid(UUID.randomUUID().toString());
				placardTable.setUrl("interiorEmail.do?method=emailMain&isReadOnly=true&back=true");
				placardTable.setUuidName("uuid");
				placardTable.setModel("内部邮件");
				placardTable.setAddresser( docRecVO.getCreater_id());//发起
				placardTable.setAddressee(docRecVO.getCreater_id());//发起
				placardService.AddPlacard(placardTable);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return eff;
	}
		
	public int remindStart(DocRecVO docRecVO,UserVO userVO,boolean isTimeout){ 
		int eff=0;
		UserVO createVO=null;
		KDepartmentVO kDepartmentVO=null;
		KAreaVO kAreaVO=null;
		String cdate=StringUtil.showDate(docRecVO.getRec_date()+" 00:00:00", "showDate:yyyy年MM月dd日");
		try {
			createVO=dbUtil.load(UserVO.class, docRecVO.getCreater_id());
			kDepartmentVO=dbUtil.load(KDepartmentVO.class, Integer.parseInt(createVO.getDepartmentid()));
			kAreaVO=dbUtil.load(KAreaVO.class, Integer.parseInt(kDepartmentVO.getAreaid()));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String strPattern="《{0}》收文需您签字。请尽快处理，谢谢！";
		strPattern="{0} {1} {2}于  {3} 提交的{4}机构印发的{5}文件，需要您签阅，请你尽快处理。谢谢 ";
	    String subfix="hq";
		if(isTimeout){
	    	strPattern="《{0}》收文将于今天到期，请尽快处理，谢谢！";
	    	strPattern="{0} {1} {2}于  {3} 提交的{4}机构印发的{5}文件，将于今天到期，但您尚未签阅，请尽快处理，谢谢 ";
	    	subfix+="timeout";
	    }
		
		String context=MessageFormat.format(strPattern,
		   kAreaVO.getName(),
		   kDepartmentVO.getDepartname(),
		   createVO.getName(),
		   cdate,
		   docRecVO.getPost_organ(),
		   docRecVO.getFile_name() 
		);
	    PlacardTable placardTable=new PlacardTable();
		placardTable.setIsReversion(0);
		placardTable.setAddresserTime(StringUtil.getCurDateTime());
		placardTable.setCaption("收文传阅提醒");
		placardTable.setMatter(context);
		placardTable.setIsRead(0);
		placardTable.setIsNotReversion(0);
		placardTable.setUuid(UUID.randomUUID().toString());
		placardTable.setUrl("interiorEmail.do?method=emailMain&isReadOnly=true&back=true");
		placardTable.setUuidName("uuid");
		placardTable.setModel("内部邮件");
		placardTable.setAddresser( docRecVO.getCreater_id());//发起
		placardTable.setAddressee(String.valueOf(userVO.getId()));//发起
		
        try {
			//placardService.AddPlacard(placardTable);
        	SmsOpt.sendSm(userVO.getMobilePhone(), context,docRecVO.getUuid()+subfix);
    		//smsService.createSms(userVO.getMobilePhone(), context, docRecVO.getUuid()+":hq");

			eff++;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return eff;
	}
	
	public int remindStart(DocRecVO docRecVO,String[] aids,boolean isTimeout){
		
		List<UserVO> userVOs=dbUtil.select(UserVO.class, 
				"select * from {0} where id in ("+StringUtil.trim(docRecVO.getRoam_range_ids(),",")+") and id not in (select handler_id from oa_doc_log where doc_id=? and ctype=?)"
				,docRecVO.getUuid()
				,"docrec"
				);
		int eff=0;
		for(UserVO userVO:userVOs){
		eff+=remindStart(docRecVO, userVO,isTimeout);
		}
		return eff;
	}
	
public int remindAllCheckEnd(UserSession userSession, DocRecVO docRecVO){
		
	String strPattern="{0} 您好，你 于 {2} 发起的收文 ,收文号 {3} 文件名  {5},截止日期为 {4}, 已经审核完毕，请给予处理意见和办结";
		int eff=0;
	
		String context=MessageFormat.format(strPattern,
		   docRecVO.getCreater_name(),  //0
		   userSession.getUserName(),  //1
		   docRecVO.getRec_date(),  //2
		   docRecVO.getRec_doc_no(),  //3
		   docRecVO.getTimeout_date()  //4
		   ,docRecVO.getFile_name() 
		);
	    PlacardTable placardTable=new PlacardTable();
		placardTable.setIsReversion(0);
		placardTable.setAddresserTime(StringUtil.getCurDateTime());
		placardTable.setCaption("收文办结提醒");
		placardTable.setMatter(context);
		placardTable.setIsRead(0);
		placardTable.setIsNotReversion(0);
		placardTable.setUuid(UUID.randomUUID().toString());
		placardTable.setUrl("interiorEmail.do?method=emailMain&isReadOnly=true&back=true");
		placardTable.setUuidName("uuid");
		placardTable.setModel("内部邮件");
		placardTable.setAddresser(userSession.getUserId());//发起
		placardTable.setAddressee(docRecVO.getCreater_id());//发起
		
        try {
			placardService.AddPlacard(placardTable);
			eff++;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return eff;
	}
	
	public boolean isAllCheckEnd(DocRecVO docRecVO){
		boolean isAll=false;
		List<DocLogVO> docLogVOs=null;
		try{
			docLogVOs=dbUtil.select(DocLogVO.class,
			"select * from {0} where handler_id in ("+StringUtil.trim(docRecVO.getRoam_range_ids(),",")+") and doc_id=? and ctype=?", 
			 docRecVO.getUuid(),"docrec");
			String[] ids=docRecVO.getRoam_range_ids().split(",");
			Set<String> idset=new HashSet<String>();
			for(String id:ids){
				idset.add(id);
			}
			for(DocLogVO docLogVO:docLogVOs){
				if(idset.contains(docLogVO.getHandler_id())){
					idset.remove(docLogVO.getHandler_id());
					if(idset.isEmpty()){
						isAll=true;
						break;
					}
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return isAll;
	}
}
