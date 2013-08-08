package com.matech.audit.service.doc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import junit.framework.Assert;

import org.apache.tools.ant.taskdefs.rmic.KaffeRmic;
import org.junit.Test;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.department.model.DepartmentVO;
import com.matech.audit.service.department.model.KAreaVO;
import com.matech.audit.service.department.model.KDepartmentVO;
import com.matech.audit.service.doc.model.DocFlowVO;
import com.matech.audit.service.doc.model.DocLogVO;

import com.matech.audit.service.doc.model.AutoCodeUsedVO;
import com.matech.audit.service.doc.model.AutoCodeVO;
import com.matech.audit.service.doc.model.DocPostFileVO;
import com.matech.audit.service.doc.model.DocPostSignVO;
import com.matech.audit.service.doc.model.DocPostVO;
import com.matech.audit.service.doc.model.DocRecVO;
import com.matech.audit.service.placard.PlacardService;
import com.matech.audit.service.placard.model.PlacardTable;
import com.matech.audit.service.user.model.User;
import com.matech.audit.service.user.model.UserVO;
import com.matech.audit.work.form.FormDefineAction;
import com.matech.framework.listener.UserSession;

import com.matech.framework.pub.autocode.DELAutocode;
import com.matech.framework.pub.autocode.model.AutocodeTable;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.StringUtil;
import com.matech.sms.SmsOpt;
import com.matech.sms.SmsService;
import com.mysql.jdbc.Driver;
import com.sun.java_cup.internal.runtime.Scanner;

import com.matech.framework.pub.util.StringUtil;

public class DocPostService {

	 public enum Node{
		 fq("发起"),xm("项目相关签字"),hq("会签"),qf("签发"),hy("核阅"),end("完结");
		 
		 private String name_cn;
		 
		 Node(String name_cn){
			 this.name_cn=name_cn;
		 }

		public String getName_cn() {
			return name_cn;
		}
		 
		public Node next(){
			switch (this) {
			case fq:return hq;
			case hq:return qf;
			case qf:return hy;
			
			default:
				return null;
			}
		}
		 
	 }
	
	private Connection conn;
	private DbUtil dbUtil;
	private PlacardService placardService;
	private DELAutocode delAutocode;
	private SmsService smsService;
	
	public DocPostService(Connection conn){
		this.conn=conn;
	    try {
			this.dbUtil=new DbUtil(conn);
			this.placardService=new PlacardService(conn);
			this.delAutocode=new DELAutocode();
			this.smsService=new SmsService(conn);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/*
	 * 签发
	 */
	public List<String> doSignIssue(DocPostVO docPostVO,UserSession userSession,String remark){
		List<String> result=new ArrayList<String>();
		   
		   
		   int i=0;
		   DocLogVO log=new DocLogVO();
		try {
		
			
	    	log.setDoc_no(docPostVO.getDoc_no());
	    	log.setHandle_time(StringUtil.getCurDateTime());
	    	log.setHandler_id(userSession.getUserId());
	    	log.setHandler_name(userSession.getUserName());
	    	log.setNode(Node.qf);
	    	log.setUuid(UUID.randomUUID().toString());
	    	log.setRemark(remark);
	    	log.setCtype("docpost");
	    	log.setDoc_id(docPostVO.getUuid());
	    	i+=dbUtil.insert(log);
	        if(i==1){
	        	result.add(MessageFormat.format("文件 {0} 签发成功", docPostVO.getTitle()));
	        }else{
	        	result.add(MessageFormat.format("文件 {0} 签发失败", docPostVO.getTitle()));
	        }
	        String handler_ids=StringUtil.trim(docPostVO.getSignissuer_ids(), ",");
	        String[] arr_countsigner_ids=handler_ids.split(",");
	        int log_count= dbUtil.queryForInt("select count(uuid) from oa_doc_log where ctype=? and doc_id=? and node_code=? and handler_id in ("+handler_ids+")"
	        		,new Object[]{"docpost",docPostVO.getUuid(),Node.qf.name()} );
	        //int log_count=dbUtil.select(DocLogVO.class, "select * from oa_doc_log where ctype=? and doc_id=? and node_code=? and handler_id in ("+handler_ids+")"
	        //		, "docpost",docPostVO.getUuid(),Node.qf.name
	        //		).size();
		    if(log_count==arr_countsigner_ids.length){
		    	if(StringUtil.isBlank(docPostVO.getChecker_ids())){
			    	docPostVO.setNode_code(Node.end.name());
			        docPostVO.setNode_remark(MessageFormat.format("{0} 没有核阅人，发文办结",StringUtil.getCurDateTime() ));
                    docPostVO.setDoc_no(genDocNo(docPostVO, userSession));
                    //if(!StringUtil.isBlank(docPostVO.getDoc_no())){
                    DocPostFileVO docPostFileVO=createDocPostFile(docPostVO);
                    dbUtil.insert(docPostFileVO);
                    //}
			        remaindEnd(docPostVO, userSession,false);
		    	}else{
			    	docPostVO.setNode_code(Node.hy.name());
			        docPostVO.setNode_remark(MessageFormat.format("{0} 完成签发,进入核阅",StringUtil.getCurDateTime() ));
			        remaindCheck(docPostVO, userSession,false);
		    	}

		    }
	    	docPostVO=updateSignInfoContext(docPostVO, Node.qf);
	        dbUtil.update(docPostVO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result.add(MessageFormat.format("操作异常:{0}", e.getLocalizedMessage()));
		}finally{
			
		}
		   return result;
		}

	
	/**
	 * 会签
	 * @param docPostVO
	 * @param userSession
	 * @param remark
	 * @return
	 */
	public List<String> doCounterSign(DocPostVO docPostVO,UserSession userSession,String remark){
		   List<String> result=new ArrayList<String>();
		   
		   
		   int i=0;
		   DocLogVO log=new DocLogVO();
		try {
			if(!"a".equals(docPostVO.getApply_type())&&(!StringUtil.isBlank(docPostVO.getCur_hq_id())&&!userSession.getUserId().equals(docPostVO.getCur_hq_id()))){
				UserVO userVO=dbUtil.load(UserVO.class, Integer.parseInt(docPostVO.getCur_hq_id()));
				result.add(MessageFormat.format("你不属于当前会签人，不能进行会签 . 请联系 {0} 手机号码:{1}",userVO.getName(),userVO.getMobilePhone()));
				return result;
			}
			
	    	log.setDoc_no(docPostVO.getDoc_no());
	    	log.setHandle_time(StringUtil.getCurDateTime());
	    	log.setHandler_id(userSession.getUserId());
	    	log.setHandler_name(userSession.getUserName());
	    	log.setNode(Node.hq);
	    	log.setUuid(UUID.randomUUID().toString());
	    	log.setRemark(remark);
	    	log.setCtype("docpost");
	    	log.setDoc_id(docPostVO.getUuid());
	    	i+=dbUtil.insert(log);
	        if(i==1){
	        	result.add(MessageFormat.format("文件 {0} 会签成功", docPostVO.getTitle()));
		        String handler_ids=StringUtil.trim(docPostVO.getCountersigner_ids(), ",");
		        handler_ids=StringUtil.trim(handler_ids, ",");
		        String[] arr_countsigner_ids=handler_ids.split(",");
		        for(int j=0;j<arr_countsigner_ids.length;j++){
		        	if(userSession.getUserId().equals(arr_countsigner_ids[j])){
		        		if(j==arr_countsigner_ids.length-1){
		        			docPostVO.setCur_hq_id("");
		        		}else{
		        			docPostVO.setCur_hq_id(arr_countsigner_ids[j+1]);
		        		}
		        	}
		        }
		        int log_count= dbUtil.queryForInt("select count(uuid) from oa_doc_log where ctype=? and doc_id=? and node_code=? and handler_id in ("+handler_ids+")"
		        		,new Object[]{"docpost",docPostVO.getUuid(),Node.hq.name()} );
			    if(log_count==arr_countsigner_ids.length){
			    	if(!StringUtil.isBlank(docPostVO.getSignissuer_ids())){
			    	docPostVO.setNode_code(Node.qf.name());
			    	
			        docPostVO.setNode_remark(MessageFormat.format("{0} 完成会签,进入签发",StringUtil.getCurDateTime() ));
			        remaindSignissuer(docPostVO, userSession,false);
			    	}else if(!StringUtil.isBlank(docPostVO.getChecker_ids())){
				    	docPostVO.setNode_code(Node.hy.name());
				        docPostVO.setNode_remark(MessageFormat.format("{0} 完成会签，没有签发人，进入核阅",StringUtil.getCurDateTime() ));
				        remaindCheck(docPostVO, userSession,false);	
			    	}else{
				    	docPostVO.setNode_code(Node.end.name());
				        docPostVO.setNode_remark(MessageFormat.format("{0} 完成会签，没有签发人和核阅人，文件完结",StringUtil.getCurDateTime() ));
				        remaindEnd(docPostVO, userSession,false);	
			    	}
			    	
			    }
			    docPostVO=updateSignInfoContext(docPostVO, Node.hq);
			    dbUtil.update(docPostVO);
			    remaindCounterSigner(docPostVO, userSession, false);
	        }else{
	        	result.add(MessageFormat.format("文号{0} 会签失败", docPostVO.getDoc_no()));
	        }

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result.add(MessageFormat.format("操作异常:{0}", e.getLocalizedMessage()));
		}finally{
			
		}
		   return result;
		}
	
	
	/**
	 * 核阅
	 * @param docPostVO
	 * @param userSession
	 * @param remark
	 * @param ischeck
	 * @return
	 */
	public List<String> doCheck(DocPostVO docPostVO,UserSession userSession,String remark,boolean ischeck){
		List<String> result=new ArrayList<String>();
		   DbUtil dbUtil=null;
		   
		   int i=0;
		   DocLogVO log=new DocLogVO();
		try {
			conn=new DBConnect().getConnect();
			dbUtil = new DbUtil(conn);
			
	    	log.setDoc_no(docPostVO.getDoc_no());
	    	log.setHandle_time(StringUtil.getCurDateTime());
	    	log.setHandler_id(userSession.getUserId());
	    	log.setHandler_name(userSession.getUserName());
	    	log.setNode(Node.hy);
	    	log.setUuid(UUID.randomUUID().toString());
	    	log.setRemark(remark);
	    	log.setCtype("docpost");
	    	log.setDoc_id(docPostVO.getUuid());
	    	i+=dbUtil.insert(log);
	        if(i==1){
	        	if(ischeck){
	        	result.add(MessageFormat.format("文件  {0} 核阅并签字成功", docPostVO.getTitle()));
	        	}else{
	        		result.add(MessageFormat.format("文件  {0} 确认不核阅", docPostVO.getTitle()));
	        	}
	        }else{
	        	result.add(MessageFormat.format("文件  {0} 核阅失败", docPostVO.getTitle()));
	        }
	        String handler_ids=StringUtil.trim(docPostVO.getChecker_ids(), ",");
	        String[] arr_countsigner_ids=handler_ids.split(",");
	        int log_count= dbUtil.queryForInt("select count(uuid) from oa_doc_log where ctype=? and doc_id=? and node_code=? and handler_id in ("+handler_ids+")"
	        		,new Object[]{"docpost",docPostVO.getUuid(),Node.hy.name()} );
		    if(log_count==arr_countsigner_ids.length){
		    	docPostVO.setNode_code(Node.end.name());
		        docPostVO.setNode_remark(MessageFormat.format("{0} 完成{1}",StringUtil.getCurDateTime(),ischeck?"核阅并签字":"确认不核阅" ));
                docPostVO.setDoc_no(genDocNo(docPostVO, userSession));
		        remaindEnd(docPostVO, userSession,false);
		        //if(!StringUtil.isBlank(docPostVO.getDoc_no())){
                  DocPostFileVO docPostFileVO=createDocPostFile(docPostVO);
                  dbUtil.insert(docPostFileVO);
		        //}
		    }
		    if(ischeck){
		        docPostVO=updateSignInfoContext(docPostVO, Node.hy);
		    }else{
		    	docPostVO=updateSignInfoContext(docPostVO, Node.hy,"不核阅","",true);
		    }
		    dbUtil.update(docPostVO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result.add(MessageFormat.format("操作异常:{0}", e.getLocalizedMessage()));
		}finally{
			
		}
		   return result;
		}
	
	public static void main(String[] args){

		Connection conn=null;
		DbUtil dbUtil=null;
		try {
			Class.forName(Driver.class.getName());
		//matech-sd2.eicp.net 
			 //conn=DriverManager.getConnection("jdbc:mysql://192.168.1.100:5188/asdb?autoReconnect=true&amp;useUnicode=true&amp;characterEncoding=utf-8","xoops_root","654321");
			 //dbUtil=new DbUtil(conn);
			//String context=dbUtil.tableRefClassConext("oa_doc_post");
			
			//PreparedStatement ps=conn.prepareStatement("select * from oa_doc_sign");
			//ResultSet rs=ps.executeQuery();
			//DocRecVO rec=dbUtil.load(DocRecVO.class, "451f4b2d-cdd3-43ca-8882-6ba625e1e6bb");
			//rec.setFile_name("鬼爷");
			//int i=dbUtil.update(rec);
			//rec.setUuid(UUID.randomUUID().toString());
			//i+=dbUtil.insert(rec);
			//i+=dbUtil.delete(rec);
			
			//System.out.println(dbUtil.tableRefClassConext("k_waresstock_grant"));
			System.out.println(StringUtil.getCurDateTime("yyyy-MM-dd HH:mm:ss",900));
			//System.out.println(i);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			//dbUtil.close(conn);
		}
	}
	

	
	
	public List<String> doHandle(DocPostVO docPostVO,UserSession userSession,String remark){
		StringBuffer sqlPattern=new StringBuffer();
		String sql="";
	    sqlPattern.append("UPDATE oa_doc_post SET ")
	    .append("handler_ids=REPLACE(handler_ids,?,''), ")
	    .append("handler_names=REPLACE(handler_names,?,'') ")
	    .append(" where  doc_no=? and handler_ids like ?")
	    ;
	    sql=sqlPattern.toString();
	    DbUtil dbUtil=null;
	    PreparedStatement ps;
	    DocFlowVO flow=null,nextFlow=null;
	    DocLogVO log=new DocLogVO();
	    List<String> results=new ArrayList<String>();
	    int i=0;
		try {
			conn.setAutoCommit(false);
			dbUtil= new DbUtil(conn);
			ps = conn.prepareStatement(sql);
			
		    ps.setString(1, userSession.getUserId());
		    ps.setString(2, userSession.getUserName());
		    ps.setString(3, docPostVO.getDoc_no());
		    ps.setString(4, "%"+userSession.getUserId()+"%");
		    i+=ps.executeUpdate();
		    if(i==1){

		    	docPostVO=dbUtil.load(docPostVO,docPostVO.getUuid());
		    	flow=dbUtil.load(DocFlowVO.class, "code",docPostVO.getNode_code());
		    	log.setDoc_no(docPostVO.getDoc_no());
		    	log.setHandle_time(StringUtil.getCurDateTime());
		    	log.setHandler_id(userSession.getUserId());
		    	log.setHandler_name(userSession.getUserName());
		    	log.setNode_code(flow.getCode());
		    	log.setNode_name(flow.getName());
		    	log.setUuid(UUID.randomUUID().toString());
		    	log.setRemark(remark);
		    	log.setCtype("发文");
		    	dbUtil.insert(log);
		    	if(docPostVO.getHandler_ids().replaceAll(",", "").trim().length()==0){
		    		flow=dbUtil.load(DocFlowVO.class, "code",docPostVO.getNode_code());
		    	    nextFlow=getNextNode(flow);
		    	    if(nextFlow==null){
		    	    	 results.add("没有下一个节点");
		    	    	 return results;
		    	    }
		    	    docPostVO.setNode_code(nextFlow.getCode());
		    	    if("hq".equals(nextFlow.getCode())){
		    	    	docPostVO.setHandler_ids(docPostVO.getCountersigner_ids());
		    	        docPostVO.setHandler_names(docPostVO.getCountersigner_names());
		    	    }else if("hy".equals(nextFlow.getCode())){
		    	    	docPostVO.setHandler_ids(docPostVO.getCountersigner_ids());
		    	        docPostVO.setHandler_names(docPostVO.getCountersigner_names());
		    	    }else if("hy".equals(nextFlow.getCode())){
		    	    	docPostVO.setHandler_ids(docPostVO.getCountersigner_ids());
		    	        docPostVO.setHandler_names(docPostVO.getCountersigner_names());
		    	    }
		    	   
		    	    i+=dbUtil.update(docPostVO);
		    	   
		    	    results.add("操作成功");
		    	    conn.commit();
		    	}
		    }else {
	    		 results.add("你并不属于处理人");
	    	}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			results.add("操作异常:"+e.getLocalizedMessage());
		}
        return results;
	}
	
	public List<String>  doFallBack(DocPostVO docPostVO,UserSession userSession,String remark){
		StringBuffer sqlPattern=new StringBuffer();
		String sql="";
	    sqlPattern.append("UPDATE oa_doc_post SET ")
	    .append("handler_ids=REPLACE(handler_ids,?,''), ")
	    .append("handler_names=REPLACE(handler_names,?,'') ")
	    .append(" where  doc_no=? and handler_ids like ?")
	    ;
	    sql=sqlPattern.toString();
	    DbUtil dbUtil=null;
	    PreparedStatement ps;
	    DocFlowVO flow=null,nextFlow=null;
	    DocLogVO log=new DocLogVO();
	    List<String> results=new ArrayList<String>();
	    int i=0;
		try {
			conn.setAutoCommit(false);
			dbUtil= new DbUtil(conn);
			ps = conn.prepareStatement(sql);
			
		    ps.setString(1, userSession.getUserId());
		    ps.setString(2, userSession.getUserName());
		    ps.setString(3, docPostVO.getDoc_no());
		    ps.setString(4, "%"+userSession.getUserId()+"%");
		    i+=ps.executeUpdate();
		    if(i==1){

		    	docPostVO=dbUtil.load(docPostVO,docPostVO.getUuid());
		    	flow=dbUtil.load(DocFlowVO.class, "code",docPostVO.getNode_code());
		    	log.setDoc_no(docPostVO.getDoc_no());
		    	log.setHandle_time(StringUtil.getCurDateTime());
		    	log.setHandler_id(userSession.getUserId());
		    	log.setHandler_name(userSession.getUserName());
		    	log.setNode_code(flow.getCode());
		    	log.setNode_name(flow.getName());
		    	log.setUuid(UUID.randomUUID().toString());
		    	log.setRemark("");
		    	log.setCtype("发文");
		    	dbUtil.insert(log);
		    	if(docPostVO.getHandler_ids().replaceAll(",", "").trim().length()==0){
		    		flow=dbUtil.load(DocFlowVO.class, "code",docPostVO.getNode_code());
		    	    nextFlow=getNextNode(flow);
		    	    if(nextFlow==null){
		    	    	 results.add("没有下一个节点");
		    	    	 return results;
		    	    }
		    	    docPostVO.setNode_code(nextFlow.getCode());
		    	    if("hq".equals(nextFlow.getCode())){
		    	    	docPostVO.setHandler_ids(docPostVO.getCountersigner_ids());
		    	        docPostVO.setHandler_names(docPostVO.getCountersigner_names());
		    	    }else if("hy".equals(nextFlow.getCode())){
		    	    	docPostVO.setHandler_ids(docPostVO.getCountersigner_ids());
		    	        docPostVO.setHandler_names(docPostVO.getCountersigner_names());
		    	    }else if("hy".equals(nextFlow.getCode())){
		    	    	docPostVO.setHandler_ids(docPostVO.getCountersigner_ids());
		    	        docPostVO.setHandler_names(docPostVO.getCountersigner_names());
		    	    }
		    	   
		    	    i+=dbUtil.update(docPostVO);
		    	   
		    	    results.add("操作成功");
		    	    conn.commit();
		    	}
		    }else {
	    		 results.add("你并不属于处理人");
	    	}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			results.add("操作异常:"+e.getLocalizedMessage());
		}
        return results;
	}
	
	
	public DocFlowVO getNextNode(DocFlowVO docFlowVO){
		String sql="SELECT a.* FROM oa_doc_flow a,(SELECT next_node_code FROM oa_doc_flow WHERE CODE=? and ctype=? )b WHERE a.CODE=b.next_node_code";
		//sql="SELECT * FROM oa_doc_flow a WHERE a.code=(SELECT next_node_code FROM oa_doc_flow  WHERE CODE=? AND ctype=?)";
		PreparedStatement ps=null;
		ResultSet rs=null;
		DocFlowVO nextVo=null;
		try{
          ps=conn.prepareStatement(sql);
          ps.setString(1, docFlowVO.getCode());
          ps.setString(2, docFlowVO.getCtype());
          rs=ps.executeQuery();
          while(rs.next()){
        	  nextVo=DbUtil.evalObject(DocFlowVO.class, rs);
          }
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			DbUtil.close(null, ps, rs);
		}
		return nextVo;
	}
	
	public int getNextSeqNo(){
		int seqno=0;
		String sql="select doc_seq from oa_doc_post order by doc_seq asc";
		PreparedStatement preparedStatement=null;
		ResultSet resultSet=null;
		
		try {
			preparedStatement=conn.prepareStatement(sql);
			resultSet = preparedStatement.executeQuery();
			while(resultSet.next()){
			   int i=resultSet.getInt("doc_seq");
			   if(seqno!=i){break;}
			   else{
				   seqno++;
			   }
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.close(null, preparedStatement, resultSet);
		}
		
		return seqno;
	}
	
	public static AutoCodeVO toVO(AutocodeTable autocodeTable){
		if(autocodeTable==null)return null;
		AutoCodeVO autoCodeVO=new AutoCodeVO();
	    autoCodeVO.setAowner(autocodeTable.getAowner());
	    autoCodeVO.setAtype(autocodeTable.getAtype());
	    autoCodeVO.setCurNum1(autocodeTable.getCurnum1());
	    autoCodeVO.setCurNum2(autocodeTable.getCurnum2());
	    autoCodeVO.setCurNum3(autocodeTable.getCurnum3());
	    autoCodeVO.setFormat(autocodeTable.getFormat());
	    autoCodeVO.setId(autocodeTable.getId());
	    autoCodeVO.setShowlen1(autocodeTable.getShowlen1());
	    autoCodeVO.setShowlen2(autocodeTable.getShowlen2());
	    autoCodeVO.setShowlen3(autocodeTable.getShowlen3());
		return autoCodeVO;
	}
	
	public AutoCodeUsedVO doBookCode(UserSession userSession, AutoCodeVO autoCodeVO){
		AutoCodeUsedVO autoCodeUsedVO=null;
		DELAutocode delAutocode=DELAutocode.getInstant();
		try{
		   List<AutoCodeUsedVO> autoCodeUsedVOs=null;
		   
		   autoCodeUsedVOs=dbUtil.select(AutoCodeUsedVO.class, 
			 "select * from {0} where atype=? and year=? and state=? order by number asc",
			 autoCodeVO.getAtype(),
			 StringUtil.getCurYear(),
			 0
			 );
		   if(autoCodeUsedVOs.size()==1){
			   autoCodeUsedVO= autoCodeUsedVOs.get(0);
			   autoCodeUsedVO.setAbandonuser(userSession.getUserId());
			   autoCodeUsedVO.setState(1);
			   autoCodeUsedVO.setAbandondate(StringUtil.getCurDateTime());
			   dbUtil.update(autoCodeUsedVO);
			   return autoCodeUsedVO;
		   }
		   String fullnum =delAutocode.getAutoCode(autoCodeVO.getAtype(), "all");
		   autoCodeUsedVOs=dbUtil.select(AutoCodeUsedVO.class, 
					 "select * from {0} where atype=? and year=? and state=? order by number asc",
					 autoCodeVO.getAtype(),
					 StringUtil.getCurYear(),
					 0
		   );
		   if(autoCodeUsedVOs.size()==1){
			   autoCodeUsedVO= autoCodeUsedVOs.get(0);
			   autoCodeUsedVO.setAbandonuser(userSession.getUserId());
			   autoCodeUsedVO.setState(1);
			   autoCodeUsedVO.setAbandondate(StringUtil.getCurDateTime());
			   dbUtil.update(autoCodeUsedVO);
		   }else{
			   throw new Exception(MessageFormat.format("文号 类型 {0} 无法预定",autoCodeVO.getAtype()));
		   }
		   
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return autoCodeUsedVO;
	}
	
	
	public int doEnd(DocPostVO docPostVO){
		return 0;
	}
	
	
	public List<String> doProjectSign(DocPostVO docPostVO,UserSession userSession,String remark){
		   List<String> result=new ArrayList<String>();
	
		   
		   int i=0;
		   DocLogVO log=new DocLogVO();
		try {
			
			
	    	log.setDoc_no(docPostVO.getDoc_no());
	    	log.setHandle_time(StringUtil.getCurDateTime());
	    	log.setHandler_id(userSession.getUserId());
	    	log.setHandler_name(userSession.getUserName());
	    	log.setNode(Node.xm);
	    	log.setUuid(UUID.randomUUID().toString());
	    	log.setRemark(remark);
	    	log.setCtype("docpost");
	    	log.setDoc_id(docPostVO.getUuid());
	    	i+=dbUtil.insert(log);
	        if(i==1){
	        	result.add(MessageFormat.format("文号{0} 项目人员确认成功", docPostVO.getDoc_no()));
	        }else{
	        	result.add(MessageFormat.format("文号{0} 项目人员确认失败", docPostVO.getDoc_no()));
	        }
	        String handler_ids=StringUtil.trim(docPostVO.getProject_member_ids(), ",");
	        String[] arr_project_ids=handler_ids.split(",");
	        int log_count= dbUtil.queryForInt("select count(uuid) from oa_doc_log where ctype=? and doc_id=? and node_code=? and handler_id in ("+handler_ids+")"
	        		,new Object[]{"docpost",docPostVO.getUuid(),Node.xm.name()} );
		    if(log_count==arr_project_ids.length){
		    	docPostVO.setNode_code(Node.hq.name());
		        docPostVO.setNode_remark(MessageFormat.format("{0} 完成项目相关签字,进入会签",StringUtil.getCurDateTime() ));
		        remaindCounterSigner(docPostVO, userSession,false);
		    }
		    if(StringUtil.isBlank(docPostVO.getCountersigner_ids())){
		    	docPostVO.setNode_code(Node.qf.name());
		        docPostVO.setNode_remark(MessageFormat.format("{0} 无会签人，直接进入签发",StringUtil.getCurDateTime() ));
	            remaindSignissuer(docPostVO, userSession,false);
		    }
		    docPostVO=updateSignInfoContext(docPostVO, Node.xm);
		    dbUtil.update(docPostVO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result.add(MessageFormat.format("操作异常:{0}", e.getLocalizedMessage()));
		}finally{
			
		}
		   return result;
		}
	
	public DocPostVO updateSignInfoContext(DocPostVO docPostVO,Node node){
		return updateSignInfoContext(docPostVO, node, "已签", "未签",false);
	}
	
	   public DocPostVO updateSignInfoContext(DocPostVO docPostVO,Node node,String signName,String unsignName,boolean isForce){
		   int eff=0;
		   String context="";
		   String contextName="";
           String names="",ids="";
           
		   List<DocLogVO> docLogVOs=dbUtil.select(DocLogVO.class,
        		   "select * from {0} where node_code=? and doc_id=?",node.name(),docPostVO.getUuid());
          
		   switch(node){
		   case hq:{
			   names=docPostVO.getCountersigner_names();
			   ids=docPostVO.getCountersigner_ids();
			   
			   break;
		   }
		   case xm:{
			   names=docPostVO.getProject_member_names();
			   ids=docPostVO.getProject_member_ids();
			   break;
		   }
		   case qf:{
			   names=docPostVO.getSignissuer_names();
			   ids=docPostVO.getSignissuer_ids();
			   break;
		   }
		   case hy:{
			   names=docPostVO.getChecker_names();
			   ids=docPostVO.getChecker_ids();
			   break;
		   }
		   default:break;
		   }
          // String[] arr_names=names.split(",");
           String[] arr_ids=ids.split(",");
           if(Node.hq==node&&arr_ids.length>0){
        	   docPostVO.setCur_hq_id(arr_ids[0]);
           }
           boolean isFirstSign=false;
           
           for(int i=0;i<arr_ids.length;i++){
        	   //String name=arr_names[i];
        	   //if(StringUtil.isBlank(name))continue;
        	   String id=arr_ids[i];
        	   if(StringUtil.isBlank(id))continue;
        	   UserVO userVO=null;
        	   try{
        	   userVO= dbUtil.load(UserVO.class, Integer.parseInt(id));
        	   }catch(Exception ex){continue;}
        	   boolean isSigned=false;
        	   for(DocLogVO docLogVO:docLogVOs){
        		      
        		   if(id.equals(docLogVO.getHandler_id())){
        			   isSigned=true;
        			   break;
        		   }
 
        		   
        	   }
        	   if(!isFirstSign&&!isSigned&&node==Node.hq){
        		   docPostVO.setCur_hq_id(id);
        		   isFirstSign=true;
        	   }
        	   if(isForce){
        		   context+=userVO.getName()+"("+signName+"),";
        		   contextName+=userVO.getName()+",";
        	   }else{
        	   context+=userVO.getName()+"("+(isSigned?signName:unsignName)+"),";
        	   contextName+=userVO.getName()+",";
        	   }
        	   
           }
           if(!isFirstSign&&node==Node.hq){
        	   docPostVO.setCur_hq_id("");
           }
		   switch(node){
		   case hq:{
			   docPostVO.setCountersign_info(context);
			   docPostVO.setCountersigner_names(contextName);
			   break;
		   }
		   case xm:{
			   docPostVO.setProject_member_sign_info(context);
			   break;
		   }
		   case qf:{
			   docPostVO.setSignissuer_info(context);
			   break;
		   }
		   case hy:{
			   docPostVO.setCheck_info(context);
			   break;
		   }
		   default:break;
		   }
		   return docPostVO;
	   }
	   
	   public String createDocNo(DocPostVO docPostVO,UserSession userSession){
		   String docno="";
		   KDepartmentVO kDepartmentVO=null;
		   KAreaVO kAreaVO=null;
		   try {
		   kDepartmentVO=dbUtil.load(KDepartmentVO.class,docPostVO.getDepartmentid());
		   kAreaVO=dbUtil.load(KAreaVO.class, kDepartmentVO.getAreaid());
		   if(kAreaVO.getAutoid()==1100){
			   
		   }else{
			   
		   }
		   } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		   return docno;
	   }
	
	   
	   public int remaindStart(DocPostVO docPostVO,UserSession userSession){
		   int eff=0;
		   //eff+=remaindPrjectMember(docPostVO, userSession,false);
		   if(eff==0){
			   eff+=remaindCounterSigner(docPostVO, userSession,false);
		   }
		   if(eff==0){
			   eff+=remaindSignissuer(docPostVO, userSession,false);
		   }
		   if(eff==0){
			   eff+=remaindCheck(docPostVO, userSession, false);
		   }
		   //eff+=remaindCreater(docPostVO, userSession, false);
		   return eff;
	   }
	   
	   public int remaindEnd(DocPostVO docPostVO,UserSession userSession,boolean isTimeout){
		   int eff=0;
		   //eff+=remaindPrjectMember(docPostVO, userSession,false);
		   if(eff==0){
			   if(isTimeout){
			   eff+=remaindCounterSigner(docPostVO, userSession,isTimeout);
			   }
		   }
		   if(eff==0){
			   if(isTimeout){
			   eff+=remaindSignissuer(docPostVO, userSession,isTimeout);
			   }
		   }
		   remaindCreater(docPostVO, userSession, isTimeout);
		   return eff;
	   }
	   
	   /**
	    * 提醒项目组成员
	    * @param docPostVO
	    * @param userSession
	    * @param isTimeout
	    * @return
	    */
	   public int remaindPrjectMember(DocPostVO docPostVO,UserSession userSession,boolean isTimeout){
		   int eff=0;
		   String ids=docPostVO.getProject_member_ids();
		   if(StringUtil.isBlank(ids)){
			   return eff;
		   }
		   
		   if(docPostVO.getDel_ind()==1){
			   //被删除的文件不提醒
			   return eff;
		   }
		   
		   ids=StringUtil.trim(ids, ",");
           List<UserVO> userVOs=dbUtil.select(UserVO.class, 
            "select a.* from {0} a where a.id in ("+ids+") and (select count(uuid) from oa_doc_log where doc_id=? and node_code=? and handler_id=a.id)<1 " 
            ,docPostVO.getUuid(),Node.xm.name()
            );
           String contextPattern="{0} 您好，{1} 于 {2} 发起了 {3}发文 {4}，请相关项目人员及时进行确认";
           if(isTimeout){
      	    	contextPattern="{0} 您好，{1} 于 {2} 发起了 {3}发文 {4}，你没有及时进行确认";
      	       }
           String ctype_name="a".equals(docPostVO.getCtype())?"行政":"业务";
           for(UserVO userVO:userVOs){
    	   String context=MessageFormat.format(contextPattern,
    		          userVO.getName(),  //0
    		   		  docPostVO.getCreater_names(),  //2
    		   		  docPostVO.getCreate_date()  //3
    		   	      ,ctype_name  //4
    		   	      ,docPostVO.getTitle()
    		   			);
    	   try{
       	    PlacardTable placardTable=new PlacardTable();
    		placardTable.setIsReversion(0);
    		placardTable.setAddresserTime(StringUtil.getCurDateTime());
    		placardTable.setCaption(ctype_name+"发文 相关项目人员提醒");
    		placardTable.setMatter(context);
    		placardTable.setIsRead(0);
    		placardTable.setIsNotReversion(0);
    		placardTable.setUuid(UUID.randomUUID().toString());
    		placardTable.setUrl(FormDefineAction.pathFormListExtView("0bad72f7-636f-42cb-bf72-1eda086b3836", "06"));
    		placardTable.setUuidName("uuid");
    		placardTable.setModel("相关项目人员确认");
    		placardTable.setAddresser( docPostVO.getCreater_ids());//发起
    		placardTable.setAddressee(String.valueOf(userVO.getId()));//发起
    		placardService.AddPlacard(placardTable);
    		eff++;
    	   }catch(Exception ex){ex.printStackTrace();}
           }
		   return eff;
	   }
	   
	   /**
	    *  提醒创建人
	    * @param docPostVO
	    * @param userSession
	    * @param isTimeout
	    * @return
	    */
	   public int remaindCreater(DocPostVO docPostVO,UserSession userSession,boolean isTimeout){
           int eff=0;
           
           if(docPostVO.getDel_ind()==1){
			   //被删除的文件不提醒
			   return eff;
		   }
           
           
		   String contextPattern="您于 {0} 提交的《{1}》文件签转流程结束，请尽快查阅。";
		   String subfix="end";
		   String time=StringUtil.getCurDateTime();
           if(isTimeout){
      	    	contextPattern="您于 {0} 提交的《{1}》文件讲于今天到期，但签转流程尚未结束，请处理。";
      	    	subfix="timeout";
      	    	time=StringUtil.getCurDate()+" 08:30:00";
      	   }
           String cdate=StringUtil.showDate(docPostVO.getCreate_date(), "showDate:yyyy年MM月dd日");
           UserVO createrVO=null;
           String context="";
           String applyName="a".equals(docPostVO.getApply_type())?"行政":"业务";
           try {
			createrVO=dbUtil.load(UserVO.class, docPostVO.getCreater_ids());
			context=MessageFormat.format(contextPattern, cdate,docPostVO.getTitle());
       	    PlacardTable placardTable=new PlacardTable();
    		placardTable.setIsReversion(0);
    		placardTable.setAddresserTime(StringUtil.getCurDateTime());
    		placardTable.setCaption(applyName+"文件 "+docPostVO.getTitle());
    		placardTable.setMatter(context);
    		placardTable.setIsRead(0);
    		placardTable.setIsNotReversion(0);
    		placardTable.setUuid(UUID.randomUUID().toString());
    		placardTable.setUrl(FormDefineAction.pathFormListExtView("0bad72f7-636f-42cb-bf72-1eda086b3836", "00")+"&ctype="+docPostVO.getApply_type());
    		placardTable.setUuidName("uuid");
    		placardTable.setModel(isTimeout?"文件过期":"文件结束");
    		placardTable.setAddresser( docPostVO.getCreater_ids());//发起
    		placardTable.setAddressee(docPostVO.getCreater_ids());//发起
    		placardService.AddPlacard(placardTable);
			SmsOpt.sendSm(createrVO.getMobilePhone(),context,docPostVO.getUuid()+":"+subfix,time);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
           return eff;
	   }
	   
	   /**
	    * 提醒会签
	    * @param docPostVO
	    * @param userSession
	    * @param isTimeout
	    * @return
	    */
	   public int remaindCounterSigner(DocPostVO docPostVO,UserSession userSession,boolean isTimeout){
		   int eff=0;
		   String ids=docPostVO.getCountersigner_ids();
		   
		   
		   if(StringUtil.isBlank(ids)){
			   System.out.println("====================qwh:ids is null");
			   return eff;
		   }
		   System.out.println("====================qwh:ids is not null");
		   ids=StringUtil.trim(ids, ",");
           List<UserVO> userVOs=dbUtil.select(UserVO.class, 
            "select a.* from {0} a where a.id in ("+ids+") and (select count(uuid) from oa_doc_log where doc_id=? and node_code=? and handler_id=a.id)<1 " 
            ,docPostVO.getUuid(),Node.hq.name()
            );
           
           String contextPattern="{0}{4} {1} 于{2} 提交的《{3}》文件需要您签字。请尽快处理，谢谢!";
           String subfix="hq";
           String time=StringUtil.getCurDateTime("yyyy-MM-dd HH:mm:ss",900);
           if(isTimeout){
        	   	//过期提醒
      	    	contextPattern="{0}{4} {1} 于{2} 提交的 《{3}》 文件将于今日到期。文件需要您签字，请尽快 办理，谢谢!";
      	    	subfix="hqtimeout";
      	    	
      	    	time=StringUtil.getCurDate()+" 08:30:00";
      	   }
           String ctype_name="a".equals(docPostVO.getCtype())?"行政":"业务";
           UserVO createrVO=null;
           KDepartmentVO departmentVO=null;
           KAreaVO kAreaVO=null;
           String cdate="";
           try {
				createrVO=dbUtil.load(UserVO.class, Integer.parseInt(docPostVO.getCreater_ids()));
				departmentVO=dbUtil.load(KDepartmentVO.class, Integer.parseInt(createrVO.getDepartmentid()));
				kAreaVO=dbUtil.load(KAreaVO.class, Integer.parseInt(departmentVO.getAreaid()));
				cdate=StringUtil.showDate(docPostVO.getCreate_date(), "showDate:yyyy年MM月dd日");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
           for(UserVO userVO:userVOs){
           if(StringUtil.isIn(docPostVO.getApply_type(), new String[]{"b"})&&!StringUtil.isIn(docPostVO.getCur_hq_id(), new String[]{String.valueOf(userVO.getId())}))continue;
    	   String context=MessageFormat.format(contextPattern,
    			      kAreaVO.getName(),
    		          createrVO.getName(), //0
                     cdate
    		   	      ,docPostVO.getTitle()
    		   	      ,departmentVO.getDepartname()
    		   			);
    	   try{
       	    PlacardTable placardTable=new PlacardTable();
    		placardTable.setIsReversion(0);
    		placardTable.setAddresserTime(StringUtil.getCurDateTime());
    		placardTable.setCaption(ctype_name+"发文  会签人员提醒");
    		placardTable.setMatter(context);
    		placardTable.setIsRead(0);
    		placardTable.setIsNotReversion(0);
    		placardTable.setUuid(UUID.randomUUID().toString());
    		placardTable.setUrl(FormDefineAction.pathFormListExtView("0bad72f7-636f-42cb-bf72-1eda086b3836", "01"));
    		placardTable.setUuidName("uuid");
    		placardTable.setModel("发文会签");
    		placardTable.setAddresser( docPostVO.getCreater_ids());//发起
    		placardTable.setAddressee(String.valueOf(userVO.getId()));//发起
    		//placardService.AddPlacard(placardTable);
    		eff++;
    		
    		//等待15分钟
    		System.out.println("==================time="+time);
    		SmsOpt.sendSm(userVO.getMobilePhone(), context,docPostVO.getUuid()+":"+subfix,time);
    		//smsService.createSms(userVO.getMobilePhone(), context, docPostVO.getUuid()+":hq");
    	   }catch(Exception ex){ex.printStackTrace();}
           }
		   return eff;
	   }
	   
	   /**
	    * 提醒签发
	    * @param docPostVO
	    * @param userSession
	    * @param isTimeout
	    * @return
	    */
	   public int remaindSignissuer(DocPostVO docPostVO,UserSession userSession,boolean isTimeout){
		   int eff=0;
		   String ids=docPostVO.getSignissuer_ids();
		   if(StringUtil.isBlank(ids)){
			   return eff;
		   }
		   
		   if(docPostVO.getDel_ind()==1){
			   //被删除的文件不提醒
			   return eff;
		   }
		   
		   ids=StringUtil.trim(ids, ",");
           List<UserVO> userVOs=dbUtil.select(UserVO.class, 
            "select a.* from {0} a where a.id in ("+ids+") and (select count(uuid) from oa_doc_log where doc_id=? and node_code=? and handler_id=a.id)<1 " 
            ,docPostVO.getUuid(),Node.qf.name()
            );
           String contextPattern="{0}{4} {1} 于{2} 提交的《{3}》文件需要您签字。请尽快处理，谢谢!";
           String subfix="qf";
           String time=StringUtil.getCurDateTime();
           if(isTimeout){
        	   contextPattern="{0}{4} {1} 于{2} 提交的 《{3}》 文件将于今日到期。文件需要您签字，请尽快 办理。谢谢!";
      	       subfix="qftimeout";    
      	       
      	       time=StringUtil.getCurDate()+" 09:00:00";
           }
           UserVO createrVO=null;
           KDepartmentVO departmentVO=null;
           KAreaVO kAreaVO=null;
           String cdate="";
           String ctype_name="a".equals(docPostVO.getCtype())?"行政":"业务";
           for(UserVO userVO:userVOs){
 
    	   try{
   			createrVO=dbUtil.load(UserVO.class, Integer.parseInt(docPostVO.getCreater_ids()));
   			departmentVO=dbUtil.load(KDepartmentVO.class, Integer.parseInt(createrVO.getDepartmentid()));
   			kAreaVO=dbUtil.load(KAreaVO.class, Integer.parseInt(departmentVO.getAreaid()));
   			cdate=StringUtil.showDate(docPostVO.getCreate_date(), "showDate:yyyy年MM月dd日");
     	   String context=MessageFormat.format(contextPattern,
 			      kAreaVO.getName(),
 		          createrVO.getName(), //0
                  cdate
 		   	      ,docPostVO.getTitle()
 		   	      ,departmentVO.getDepartname()
 		   			);
       	    PlacardTable placardTable=new PlacardTable();
    		placardTable.setIsReversion(0);
    		placardTable.setAddresserTime(StringUtil.getCurDateTime());
    		placardTable.setCaption(ctype_name+"发文  签发人员提醒");
    		placardTable.setMatter(context);
    		placardTable.setIsRead(0);
    		placardTable.setIsNotReversion(0);
    		placardTable.setUuid(UUID.randomUUID().toString());
    		placardTable.setUrl(FormDefineAction.pathFormListExtView("0bad72f7-636f-42cb-bf72-1eda086b3836", "02"));
    		placardTable.setUuidName("uuid");
    		placardTable.setModel("发文签发");
    		placardTable.setAddresser( docPostVO.getCreater_ids());//发起
    		placardTable.setAddressee(String.valueOf(userVO.getId()));//发起
    		//placardService.AddPlacard(placardTable);
    		eff++;
    		//smsService.createSms(userVO.getMobilePhone(), context, docPostVO.getUuid()+":qf");
    		SmsOpt.sendSm(userVO.getMobilePhone(), context,docPostVO.getUuid()+":"+subfix,time);
    	   }catch(Exception ex){ex.printStackTrace();}
           }
		   return eff;
	   }
	   
	   /**
	    * 提醒核阅
	    * @param docPostVO
	    * @param userSession
	    * @param isTimeout
	    * @return
	    */
	   public int remaindCheck(DocPostVO docPostVO,UserSession userSession,boolean isTimeout){
		   int eff=0;
		   String ids=docPostVO.getChecker_ids();
		   if(StringUtil.isBlank(ids)){
			   return eff;
		   }
		   
		   if(docPostVO.getDel_ind()==1){
			   //被删除的文件不提醒
			   return eff;
		   }
		   
		   ids=StringUtil.trim(ids, ",");
           List<UserVO> userVOs=dbUtil.select(UserVO.class, 
            "select a.* from {0} a where a.id in ("+ids+") and (select count(uuid) from oa_doc_log where doc_id=? and node_code=? and handler_id=a.id)<1 " 
            ,docPostVO.getUuid(),Node.hy.name()
            );
           String contextPattern="{0}{4} {1} 于{2} 提交的《{3}》文件需要您签字。请尽快处理，谢谢!";
           String subfix="hy";
           String time=StringUtil.getCurDateTime();
           if(isTimeout){
      	    	contextPattern="{0}{4} {1} 于{2} 提交的 《{3}》 文件将于今日到期。文件需要您签字，请尽快 办理，谢谢!";
      	       subfix="timeouthy";
      	       time=StringUtil.getCurDate()+" 09:30:00";
           }
           UserVO createrVO=null;
           KDepartmentVO departmentVO=null;
           KAreaVO kAreaVO=null;
           String cdate="";
           String ctype_name="a".equals(docPostVO.getCtype())?"行政":"业务";
           for(UserVO userVO:userVOs){
    
    	   try{
    			createrVO=dbUtil.load(UserVO.class, Integer.parseInt(docPostVO.getCreater_ids()));
       			departmentVO=dbUtil.load(KDepartmentVO.class, Integer.parseInt(createrVO.getDepartmentid()));
       			kAreaVO=dbUtil.load(KAreaVO.class, Integer.parseInt(departmentVO.getAreaid()));
       			cdate=StringUtil.showDate(docPostVO.getCreate_date(), "showDate:yyyy年MM月dd日");
         	   String context=MessageFormat.format(contextPattern,
     			      kAreaVO.getName(),
     		          createrVO.getName(), //0
                      cdate
     		   	      ,docPostVO.getTitle(),
     		   	  departmentVO.getDepartname()
     		   			);
       	    PlacardTable placardTable=new PlacardTable();
    		placardTable.setIsReversion(0);
    		placardTable.setAddresserTime(StringUtil.getCurDateTime());
    		placardTable.setCaption(ctype_name+"发文  核阅人员提醒");
    		placardTable.setMatter(context);
    		placardTable.setIsRead(0);
    		placardTable.setIsNotReversion(0);
    		placardTable.setUuid(UUID.randomUUID().toString());
    		placardTable.setUrl(FormDefineAction.pathFormListExtView("0bad72f7-636f-42cb-bf72-1eda086b3836", "03"));
    		placardTable.setUuidName("uuid");
    		placardTable.setModel("发文核阅");
    		placardTable.setAddresser( docPostVO.getCreater_ids());//发起
    		placardTable.setAddressee(String.valueOf(userVO.getId()));//发起
    		//placardService.AddPlacard(placardTable);
    		eff++;
    		//smsService.createSms(userVO.getMobilePhone(), context, docPostVO.getUuid()+":hy");
    		SmsOpt.sendSm(userVO.getMobilePhone(),context,docPostVO.getUuid()+":"+subfix,time);
    	   }catch(Exception ex){ex.printStackTrace();}
           }
		   return eff;
	   }
	   
	   public String genDocNo(DocPostVO docPostVO,UserSession userSession){
		   String doc_no="";
		   KDepartmentVO kDepartmentVO=null;		  
		   KAreaVO kAreaVO=null;
		   String autoCode_name="";
		   try {
			/*
			kDepartmentVO=dbUtil.load(KDepartmentVO.class, Integer.parseInt(docPostVO.getDepartmentid()));
			kAreaVO=dbUtil.load(KAreaVO.class,Integer.parseInt(kDepartmentVO.getAreaid()));
			if(StringUtil.isIn(docPostVO.getDoc_type(), new String[]{"FWTJ","FWTJS"}) ){
				autoCode_name=docPostVO.getDoc_type().toUpperCase()+"_"+kAreaVO.getDoc_code();
				doc_no=delAutocode.getAutoCode(autoCode_name, "all",new String[]{kAreaVO.getShort_name(),kAreaVO.getDoc_code()},this.conn);
			}
			*/
			doc_no=delAutocode.getAutoCode(docPostVO.getDoc_type(), "all",new String[]{},conn,true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		   return doc_no;
	   }
	   
	   
	   public DocPostFileVO createDocPostFile(DocPostVO docPostVO){
		   DocPostFileVO docPostFileVO=new DocPostFileVO();
		   //docPostFileVO.setCreate_date()
		   docPostFileVO.setModify_date(StringUtil.getCurDate());
		   docPostFileVO.setCreater_id(docPostVO.getUserid());
		   docPostFileVO.setDel_ind(0);
		   docPostFileVO.setDepartmentid(docPostVO.getDepartmentid());
		   docPostFileVO.setDoc_id(docPostVO.getUuid());
		   docPostFileVO.setDoc_no(docPostVO.getDoc_no());
		   docPostFileVO.setDoc_type(docPostVO.getDoc_type());
		   docPostFileVO.setFile_count(docPostVO.getFile_total_count());
		   docPostFileVO.setBeaccount_addr(docPostVO.getBeaccount_addr());
		   if(!StringUtil.isBlank(docPostVO.getCountersigner_ids())){
			   String[] arr_hq_ids=docPostVO.getCountersigner_ids().split(",");
			   if(arr_hq_ids.length>=1){
				   docPostFileVO.setHq_id_1(arr_hq_ids[0]);
			   }
			   if(arr_hq_ids.length>=2){
				   docPostFileVO.setHq_id_2(arr_hq_ids[1]);
			   }
			   if(arr_hq_ids.length>=3){
				   docPostFileVO.setHq_id_3(arr_hq_ids[2]);
			   }
			   if(arr_hq_ids.length>=4){
				   docPostFileVO.setHq_id_4(arr_hq_ids[3]);
			   }
		   }
		   docPostFileVO.setHy_id(docPostVO.getChecker_ids());
		   docPostFileVO.setPost_addr(docPostVO.getPost_addr_names());
		   docPostFileVO.setQf_id(docPostVO.getSignissuer_ids());
		   docPostFileVO.setTitle(docPostVO.getTitle());
		   docPostFileVO.setUserid(docPostVO.getUserid());
		   docPostFileVO.setUuid(UUID.randomUUID().toString());
		   docPostFileVO.setRemark(docPostVO.getNode_remark());
		   try{
			   AutoCodeUsedVO autoCodeUsedVO=dbUtil.select(AutoCodeUsedVO.class,"select * from {0} where atype=? and fullnumber=? ", docPostVO.getDoc_type(),docPostVO.getDoc_no()).get(0);
		       docPostFileVO.setDoc_year(autoCodeUsedVO.getYear().toString());
		       docPostFileVO.setDoc_seqno(autoCodeUsedVO.getNumber());
		   }catch(Exception ex){
			   
		   }
		   return docPostFileVO;
	   }
	   
		public int remaindTimeoutCreater(DocPostVO docPostVO){
			int eff=0;
			SimpleDateFormat smdf = new SimpleDateFormat("yyyy-MM-dd");
	        Date sdate=null;
	        Date tdate=null;
	        Date ndate=null;
	        if(StringUtil.isIn(docPostVO.getNode_code(), new String[]{Node.end.name()})){
	        	return eff;
	        }
			try{
				
				sdate=smdf.parse(docPostVO.getSignissue_date());
				
				tdate=smdf.parse(docPostVO.getTimeout_date());
				ndate=smdf.parse(StringUtil.getCurDate());
			    if(tdate.compareTo(ndate)==0){
			    	//this.remaindCounterSigner(docPostVO, null, true);
			    	//this.remaindSignissuer(docPostVO, null, true);
			    	//this.remaindCheck(docPostVO, null, true);
			    	this.remaindCreater(docPostVO, null, true);
			    }else if(sdate.compareTo(ndate)==0){
			        //this.remaindCounterSigner(docPostVO, null, true);
			        
			    }
				}catch(Exception ex){ex.printStackTrace();
			}finally{}
				
			return eff;
		}
	   
		/**
		 * 超时提醒 会签、签发、核阅
		 * @param docPostVO
		 * @return
		 */
		public int remaindTimeoutSigner(DocPostVO docPostVO){
			int eff=0;
			SimpleDateFormat smdf = new SimpleDateFormat("yyyy-MM-dd");
	        Date sdate=null;
	        Date tdate=null;
	        Date ndate=null;
	        if(StringUtil.isIn(docPostVO.getNode_code(), new String[]{Node.end.name()})){
	        	return eff;
	        }
			try{
				
				sdate=smdf.parse(docPostVO.getSignissue_date());
				
				tdate=smdf.parse(docPostVO.getTimeout_date());
				ndate=smdf.parse(StringUtil.getCurDate());
			    if(tdate.compareTo(ndate)==0){
			    	this.remaindCounterSigner(docPostVO, null, true);
			    	this.remaindSignissuer(docPostVO, null, true);
			    	this.remaindCheck(docPostVO, null, true);
			    	//this.remaindCreater(docPostVO, null, true);
			    }else if(sdate.compareTo(ndate)==0){
			        this.remaindCounterSigner(docPostVO, null, true);
			        
			    }
				}catch(Exception ex){ex.printStackTrace();
			}finally{}
				
			return eff;
		}
}
