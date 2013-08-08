package com.matech.audit.service.pccpa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import sun.jdbc.odbc.JdbcOdbcTypes;

import junit.framework.Assert;

import com.matech.audit.service.attachFileUploadService.model.MtComAttachVO;
import com.matech.audit.service.department.model.DepartmentVO;
import com.matech.audit.service.department.model.KAreaVO;
import com.matech.audit.service.department.model.KDepartmentVO;
import com.matech.audit.service.doc.DocPostService.Node;
import com.matech.audit.service.doc.model.AutoCodeVO;
import com.matech.audit.service.doc.model.DocLogVO;
import com.matech.audit.service.doc.model.DocPostVO;
import com.matech.audit.service.employment.EmploymentService;
import com.matech.audit.service.form.GenFormService;
import com.matech.audit.service.form.model.FormVO;
import com.matech.audit.service.news.model.News;
import com.matech.audit.service.pccpa.model.A001VO;
import com.matech.audit.service.pccpa.model.PccpaNewVO;
import com.matech.audit.service.pccpa.model.SrDepartmentVO;
import com.matech.audit.service.pccpa.model.TempVO;
import com.matech.audit.service.pccpa.model.WordFjVO;
import com.matech.audit.service.pccpa.model.WordMsVO;
import com.matech.audit.service.pccpa.model.WordVO;
import com.matech.audit.service.user.model.User;
import com.matech.audit.service.user.model.UserVO;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.StringUtil;

public class ImportService {

	protected enum A001{
		a001,a002,a003,a021,a022,a023;
		
		public String table(){return this.getClass().getSimpleName().toLowerCase()+this.name();}
	}
	
	protected static void error(String msg){
		System.err.println(msg);
	}
	
	protected static void error(Exception ex){
		System.err.println(ex.getLocalizedMessage());
	}
	
	protected Connection conn;
	protected DbUtil dbUtil;
	protected DbUtil pccpaDbUtil;
	protected GenFormService genFormService;
	public ImportService(Connection conn){
		this.conn=conn;
		try {
			this.dbUtil=new DbUtil(conn);
			genFormService=new GenFormService(conn);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//error(e);
			error(e.getLocalizedMessage());
		}
	}
	
	public static UserVO toKUser(A001VO a001vo){
		UserVO user=new UserVO();
		
		user.setName(a001vo.getA0101());
		user.setPccpa_id(a001vo.getA0100());
		user.setBornDate(a001vo.getA0111());
		user.setFloor(a001vo.getC0113());
		user.setSex("1".equals(a001vo.getA0107())?"M":"F");
		user.setIdentityCard(a001vo.getC0149());
		//user.setState(a001vo.getC0109());
		//user.setLoginid(a001vo.getC0121());
		user.setStation(a001vo.getC0121());
		user.setDepartmentid(String.valueOf(Integer.parseInt(a001vo.getE0122().trim())));
		return user;
	}
	public List<FormVO> selectSubset(){
		return dbUtil.select(FormVO.class, "select * from {0} where form_type=? and tablename!=''K_USER'' order by tablename asc ", EmploymentService.UUID_FORM_TYPE_SUBSET);

	}
	public int dropSubsets(){
		List<FormVO> formVOs=this.selectSubset();
		String dropPattern="drop table {0}";
	    int eff=0;
	    for(FormVO formVO:formVOs){
	    	try {
				dbUtil.executeUpdate(MessageFormat.format(dropPattern,formVO.getTABLENAME()));
				eff++;
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				error(e);
			}
	    }
	    return eff;
	}
	
	String[] BaseCols={"mainformid","departmentid","userid","uuid",};
	
	public int appendBasicColumnToSubsets(){
    	List<FormVO> formVOs=this.selectSubset();
    	
    	String sqlPattern="alter table {0} add column {1} varchar(100) first";
    	int eff=0;
    	for(FormVO formVO :formVOs){
    		for(String col :BaseCols){
    			String sql=MessageFormat.format(sqlPattern, formVO.getTABLENAME(),col);
    			try {
					dbUtil.executeUpdate(sql);
					eff++;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//error(e);
					error(e);
				}
    		}
    	}
    	return eff;
    }
    
    public int fixUserId(){
    	List<FormVO> formVOs=this.selectSubset();
    	String[] cols={"mainformid","departmentid","userid","uuid",};
    	String sqlPattern="alter table {0} change `userid` `userId` varchar(100) ";
    	int eff=0;
    	for(FormVO formVO :formVOs){
    		
    			String sql=MessageFormat.format(sqlPattern, formVO.getTABLENAME());
    			try {
					dbUtil.executeUpdate(sql);
					eff++;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//error(e);
					error(e);
				}
    		
    	}
    	return eff;
    }
    
    public int setPk(String columnName){
    	List<FormVO> formVOs=this.selectSubset();
    	String[] cols={"mainformid","departmentid","userid","uuid",};
    	String sqlPattern="alter table {0} drop primary key, add primary key({1})";
    	int eff=0;
    	for(FormVO formVO :formVOs){
    		
    			String sql=MessageFormat.format(sqlPattern, formVO.getTABLENAME(),columnName);
    			try {
					dbUtil.executeUpdate(sql);
					eff++;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//error(e);
					error(e);
				}
    		
    	}
    	return eff;
    }
    
    
    public int dropBasicColumnToSubsets(){
    	List<FormVO> formVOs=this.selectSubset();
    	
    	String sqlPattern="alter table {0} drop column {1} ";
    	int eff=0;
    	for(FormVO formVO :formVOs){
    		for(String col :BaseCols){
    			String sql=MessageFormat.format(sqlPattern, formVO.getTABLENAME(),col);
    			try {
					dbUtil.executeUpdate(sql);
					eff++;
					String[] reftables=formVO.getReftables().replace("，",",").split(",");
					for(String reftable:reftables){
						dbUtil.executeUpdate(MessageFormat.format(sqlPattern, reftable,col));
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//error(e);
					error(e);
				}
    		}
    		
    	}
    	return eff;
    }
    
    public int synSubset(){
    	List<FormVO> formVOs=this.selectSubset();
    	int eff=0;
    	StringBuffer pattern=new StringBuffer("");
    	//List<UserVO> userVOs=dbUtil.select(, sql, params);
    	pattern
    	.append(" update {0} t1 set ")
    	.append(" t1.userid=(select id from k_user where pccpa_id=t1.a0100 limit 0,1) ")
    	;
    	for(FormVO formVO :formVOs){
    		String sql=MessageFormat.format(pattern.toString(), formVO.getTABLENAME());
    		try {
				eff+=dbUtil.executeUpdate(sql);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//error(e);
				error(e);
			}
    	}
    	return eff;
    }
    
    public int synDepartment(){
    	List<FormVO> formVOs=this.selectSubset();
    	int eff=0;
    	StringBuffer pattern=new StringBuffer("");
    	//List<UserVO> userVOs=dbUtil.select(, sql, params);
    	pattern
    	.append(" update {0} t1 set ")
    	.append(" departmentid=(select autoid from k_department where autoid=(select departmentid from k_user where id=t1.userid limit 0,1) ) ")
    	;
    	for(FormVO formVO :formVOs){
    		String sql=MessageFormat.format(pattern.toString(), formVO.getTABLENAME());
    		try {
				eff+=dbUtil.executeUpdate(sql);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//error(e);
				error(e);
			}
    	}
    	return eff;
    }
    
    public int synSubset2(){
    	List<FormVO> formVOs=this.selectSubset();
    	int eff=0;
    	StringBuffer pattern=new StringBuffer("");
    	List<UserVO> userVOs=dbUtil.select(UserVO.class, "select * from {0} where pccpa_id is not null");
    	Map<String, UserVO> userMap=new HashMap<String, UserVO>();
    	for(UserVO userVO:userVOs){
    		userMap.put(userVO.getPccpa_id(), userVO);
    	}
    	pattern
    	.append(" update {0} t1 set ")
    	.append(" t1.userid=''{1}'' where a0100=''{2}'' ")
    	;
    	for(FormVO formVO :formVOs){
    		Set<String> a0100s=new HashSet<String>();
    		PreparedStatement ps=null;
    		ResultSet rs=null;
    		try
    		{
			    ps=conn.prepareStatement(
				MessageFormat.format("select a0100 from {0}", formVO.getTABLENAME()));
			    rs=ps.executeQuery();
			    while(rs.next()){
			    	a0100s.add(rs.getString("a0100"));
			    }
    		} catch (SQLException e1) {
				// TODO Auto-generated catch block
				error(e1);
			}finally{
				DbUtil.close(null, ps, rs);
			}
    		
    		for(String a0100:a0100s){
    			String sql=MessageFormat.format(pattern.toString()
    					, formVO.getTABLENAME()
    					,String.valueOf(userMap.get(a0100).getId())
    					,a0100
    					);
        		try {
    				eff+=dbUtil.executeUpdate(sql);
    			} catch (Exception e) {
    				// TODO Auto-generated catch block
    				//error(e);
    				error(e);
    			}
    		}
    		
    		
    	}
    	return eff;
    }
    
    public int fixColumnCharset(String encode){
    	List<FormVO> formVOs=this.selectSubset();
        int eff=0;
        for(FormVO formVO:formVOs){
        	PreparedStatement ps=null;
        	ResultSet rs=null;
        	List<String> varcharcolumn=new ArrayList<String>();
        	List<String> varcharlenght=new ArrayList<String>(); 
        	try {
				ps=conn.prepareStatement(MessageFormat.format("select * from {0} where 1=2",formVO.getTABLENAME()));
			    rs=ps.executeQuery();
			    for(int i=0;i<rs.getMetaData().getColumnCount();i++){
			    	String temp=rs.getMetaData().getColumnTypeName(i+1);  //715827882
			    	int p=rs.getMetaData().getPrecision(i+1);
			    	if(!"varchar".equalsIgnoreCase(temp))continue;
			    	varcharcolumn.add(rs.getMetaData().getColumnName(i+1));
			    	
			    	if(p  > 107374){//==715827882){
			    		
			    	varcharlenght.add(" longtext ");
			    	}else{
			    	    p=p<20?100:p;	
				    	varcharlenght.add(" varchar("+p+") ");
			    	}
			    }
			    
        	} catch (SQLException e) {
				// TODO Auto-generated catch block
				//error(e);
        		error(e);
			}finally{
				DbUtil.close(null,ps,rs);
			}
        	String sqlPattern="ALTER TABLE {0} CHANGE {1} {1} {2} CHARACTER SET {3}";
        	for(int i=0;i<varcharcolumn.size();i++){
        	String sql=MessageFormat.format(sqlPattern, formVO.getTABLENAME()
        			,varcharcolumn.get(i)
        			,varcharlenght.get(i)
        			,encode
        			);
        	try {
				eff+=dbUtil.executeUpdate(sql);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				error(e);
			}
        	}
        }
        return eff;
    }
    
    public int fixNotNullColumn(){
    	List<FormVO> formVOs=this.selectSubset();
        int eff=0;
        for(FormVO formVO:formVOs){
        	PreparedStatement ps=null;
        	ResultSet rs=null;
        	List<String> varcharcolumn=new ArrayList<String>();
        	List<String> varcharlenght=new ArrayList<String>(); 
        	try {
				ps=conn.prepareStatement(MessageFormat.format("select * from {0} where 1=2",formVO.getTABLENAME()));
			    rs=ps.executeQuery();
			    for(int i=0;i<rs.getMetaData().getColumnCount();i++){
			    	String temp=rs.getMetaData().getColumnTypeName(i+1);  //715827882
			    	int p=rs.getMetaData().getPrecision(i+1);
			    	if(!"varchar".equalsIgnoreCase(temp)&&!"int".equalsIgnoreCase(temp))continue;
			    	varcharcolumn.add(rs.getMetaData().getColumnName(i+1));
			    	if(p  > 107374){//==715827882){
			    		
			    	varcharlenght.add(" longtext ");
			    	}else if("int".equalsIgnoreCase(temp)){
			    		
				    	varcharlenght.add(" int ");
			    	}else{
			    		
				    	varcharlenght.add(" varchar("+p+") ");
			    	}
			    }
			    
        	} catch (SQLException e) {
				// TODO Auto-generated catch block
				error(e);
			}finally{
				DbUtil.close(null,ps,rs);
			}
        	String sqlPattern="ALTER TABLE {0} CHANGE {1} {1} {2} null";
        	for(int i=0;i<varcharcolumn.size();i++){
        	String sql=MessageFormat.format(sqlPattern, formVO.getTABLENAME()
        			,varcharcolumn.get(i)
        			,varcharlenght.get(i)
      
        			);
        	try {
				eff+=dbUtil.executeUpdate(sql);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				error(e);
			}
        	}
        }
        return eff;
    }
    
    
    
    public int fillUuid(){
    	List<FormVO> formVOs=this.selectSubset();
        int eff=0;
        List<TempVO> temps=new ArrayList<TempVO>();
  
        for(FormVO formVO:formVOs){
        	try{
        		temps =dbUtil.select(TempVO.class,"select B0110,A0100,ID from "+formVO.getTABLENAME()+" where uuid is null "); // b0110,a0100,id
        	    for(TempVO temp:temps){
        	       String sql=MessageFormat.format("update {0} set uuid=UUID() where b0110=''{1}''and a0100=''{2}'' and id={3}", 
        	    	formVO.getTABLENAME(),
        	    	temp.getB0110(),
        	    	temp.getA0100(),
        	        String.valueOf(temp.getID())
        	       );
        	       dbUtil.executeUpdate(sql);
        	    }
        	}catch(Exception ex){
        		
        	}
        }
        return eff;
    }
    
    public int genTable(){
    	List<FormVO> formVOs=this.selectSubset();
        int eff=0;
        StringBuffer sbr=new StringBuffer("");
        sbr.append("<div>")
        .append("<input  matech_ext=\"ext_id=userId;ext_name=userId;ext_default=${param.userId};ext_type=hidden;ext_field=userId;ext_end\" name=\"userId\" type=\"text\" value=\"${param.userId}\" />")
        .append("</div>")
        ;
        for(FormVO formVO:formVOs){
          try {
			genFormService.genTable(formVO, 0, true,sbr.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			error(e);
		}	
        }
        return eff;
    }
    
    public int fixSubsetSourceStruct(){
    	List<FormVO> formVOs=this.selectSubset();
    	int eff=0;
	  	for(FormVO formVO :formVOs){
    		try{
        	String[] reftables=formVO.getReftables().replace("，",",").split(",");
        	dbUtil.getFieldType(formVO.getTABLENAME());
        	ResultSetMetaData md= dbUtil.getResultSet("select * from "+formVO.getTABLENAME()+" where 1=2").getMetaData();
        	for(String reftable :reftables){
        		for(int i=1;i<md.getColumnCount()+1;i++){
        			String type=md.getColumnTypeName(i);
        			String sql="";
        			if(type.equalsIgnoreCase("varchar")){
        				sql=MessageFormat.format("alter table {0} add column {1} {2}( {3} )",
        			    reftable,md.getColumnName(i),type,String.valueOf(md.getPrecision(i))	
        			);
        			}else if(type.equalsIgnoreCase("int")){
        				sql=MessageFormat.format("alter table {0} add column {1} {2}",
                			    reftable,md.getColumnName(i),type,md.getPrecision(i)	
        			);
        			}else{
        				sql=MessageFormat.format("alter table {0} add column {1} {2} ( {3} )",
        						reftable,md.getColumnName(i),"varchar","200"	);
        			}
        			try{
        		    dbUtil.execute(sql);
        			}catch(Exception ex){error(ex);}
        			}
        		}
        	}
    		catch(Exception ex){
    		  error(ex);	
    		}
    	}
    	return eff;
    }
    
    
    public int fixSubsetTargetStruct(){
    	List<FormVO> formVOs=this.selectSubset();
    	int eff=0;
	  	for(FormVO formVO :formVOs){
    		try{
        	String[] reftables=formVO.getReftables().replace("，",",").split(",");
        	//dbUtil.getFieldType(formVO.getTABLENAME());
        	
        	for(String reftable :reftables){
        		ResultSetMetaData md= dbUtil.getResultSet("select * from "+reftable+" where 1=2").getMetaData();
        		for(int i=1;i<md.getColumnCount()+1;i++){
        			String type=md.getColumnTypeName(i);
        			String sql="";
        			if(type.equalsIgnoreCase("varchar")){
        				sql=MessageFormat.format("alter table {0} add column {1} {2} ( {3} )",
        			    formVO.getTABLENAME(),md.getColumnName(i),type,String.valueOf(md.getPrecision(i))	);
        			}
        			else if(type.equalsIgnoreCase("int")){
        				sql=MessageFormat.format("alter table {0} add column {1} {2}",
        				formVO.getTABLENAME(),md.getColumnName(i),type,md.getPrecision(i)	
        			);
        			}else{
        				sql=MessageFormat.format("alter table {0} add column {1} {2} ( {3} )",
                			    formVO.getTABLENAME(),md.getColumnName(i),"varchar","200"	);
        			}
        			try{
        				error(md.getColumnName(i)+":"+md.getColumnTypeName(i));
        			error(sql);
        		    dbUtil.executeUpdate(sql);
        			}catch(Exception ex){error(ex);}
        			}
        		}
        	
    		}catch(Exception ex){
    		  error(ex);	
    		}
    	}
    	return eff;
    }
    
    
    
    public int importSubset(){
    	List<FormVO> formVOs=this.selectSubset();
    	int eff=0;
    	String deletePattern="delete from {0} where a0100 is not null";
    	deletePattern="TRUNCATE TABLE {0}";
    	String insertSelectPattern="insert into {0}  select * from {1}";
    	String sql="";
		  	for(FormVO formVO :formVOs){
	            
	    		try{
	    		ResultSetMetaData md=dbUtil.getResultSet("select * from "+formVO.getTABLENAME()+" where 1=2").getMetaData();
	        	String columns="";
	        	for(int i=1;i<md.getColumnCount()+1;i++){
	        		columns+=md.getColumnName(i)+",";
	        	}
	        	columns=StringUtil.trim(columns, ",");
	    		String[] reftables=formVO.getReftables().replace("，",",").split(",");
		    	sql=MessageFormat.format(deletePattern,formVO.getTABLENAME());
	    		dbUtil.executeUpdate(sql);
	    		for(String reftable :reftables){
	    		    try{
	    		    	sql=MessageFormat.format(insertSelectPattern, formVO.getTABLENAME(),reftable,columns);
	    		    	eff+=dbUtil.executeUpdate(sql);
	    		    }catch(Exception ex){
	    		    	error(ex);
	    		    }
	    		}
	    		}catch(Exception ex){
	    			error(ex);
	    		}
	    	}
	    	//this.conn.commit();
		
  
    	return eff;
    }
    
	
	public int importA001(){
		int eff=0;
		try {
			conn.setAutoCommit(false);	
			dbUtil.executeUpdate("delete from k_user where pccpa_id is not null");
			
			for(A001 a001 :A001.values()){
			List<A001VO> a001vos=dbUtil.select(A001VO.class,"select * from "+a001.table()+" order by a0100 asc");	
			for(A001VO a001vo:a001vos){
				UserVO user=toKUser(a001vo);
				user.setPassword("eccbc87e4b5ce2fe28308fd9f2a7baf3");
				user.setIsTips(1);
				user.setEmtype(a001.name().substring(1));
				user.setParentGroup("自定义");
				//user.setDepartmentid("621");
				//user.setLoginid("f"+eff);
				eff+=dbUtil.insert(user);
			}
			}
			
			conn.commit();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			error(e);
		}finally{
			DbUtil.close(conn);
		}
		return eff;
	}
	
	public String synPccpa(boolean updateKuer){
		String re="";
		int eff=0;
		if(updateKuer){
			eff+=this.importA001();
		}
		
		eff+=importSubset();
		eff+=this.appendBasicColumnToSubsets();
		eff+=synSubset();
		return re;
	}
	
	public KDepartmentVO toKDeparement(SrDepartmentVO srDepartmentVO){
		KDepartmentVO departmentVO=new KDepartmentVO();
		departmentVO.setDepartname(srDepartmentVO.getShortname());
		departmentVO.setAutoid(Integer.parseInt(srDepartmentVO.getCode()));
		departmentVO.setParentid(Integer.parseInt("un".equalsIgnoreCase(srDepartmentVO.getPPtr())?"0":srDepartmentVO.getPPtr()));
		departmentVO.setLevel0(srDepartmentVO.getCodelevel());
		departmentVO.setEnname(srDepartmentVO.getSpell());
		departmentVO.setIsleaf(srDepartmentVO.getCodelevel()>1?"1":"0");
		return departmentVO;
	}
	
	public int importDepartment(){
		int eff=0;
		
		List<SrDepartmentVO> srDepartmentVOs=dbUtil.select(SrDepartmentVO.class, "select * from {0}");
		for(SrDepartmentVO srDepartmentVO:srDepartmentVOs){
			KDepartmentVO kDepartmentVO=this.toKDeparement(srDepartmentVO);
			try {
				eff+=dbUtil.insert(kDepartmentVO);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return eff;
	}
	
	public int importNews(){
		int eff=0;
		List<PccpaNewVO> pccpaNewVOs=dbUtil.select(PccpaNewVO.class, "select * from {0}");
		for(PccpaNewVO pccpaNewVO:pccpaNewVOs){
			News news=new News();
			news.setTitle(pccpaNewVO.getTitle());
			news.setContents(pccpaNewVO.getContent());
			news.setUpdateTime(pccpaNewVO.getAddDate());
			news.setDoc_no(pccpaNewVO.getAuthor());
			//news.setSub_titile(pccpaNewVO.getSubTitle());
			news.setDept_type(pccpaNewVO.getClassCName());
			news.setMenuid(pccpaNewVO.getMenuid());
			news.setArea(pccpaNewVO.getArea());
			news.setPublishUserId("55772");
			try {
				eff = dbUtil.insert(news);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return eff;
	}
	
	
	public int fixOrderid(){
		int eff=0;
		List<FormVO> formVOs=this.selectSubset();
		for(FormVO formVO :formVOs){
			String sql=MessageFormat.format("select DISTINCT(userid) as uid from {0} ",formVO.getTABLENAME());
			try {
				List<Map> listUserid= dbUtil.getList(sql);
				for(Map map :listUserid){
					String uid=map.get("uid").toString();
					sql=MessageFormat.format("select * from {0} where userid=''{1}'' order by ID asc", formVO.getTABLENAME(),uid);
					List<Map> listUserData=dbUtil.getList(sql);
					for(int i=0;i<listUserData.size();i++){
						String uuid=listUserData.get(i).get("uuid").toString();
						sql=MessageFormat.format("update {0} set ID={1} where uuid=''{2}''", formVO.getTABLENAME(),i+1,uuid);
						dbUtil.executeUpdate(sql);
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return eff;
	}
	
	
	public DocPostVO toDocpost(WordVO wordVO){
		
		DocPostVO docPostVO=new DocPostVO();
		docPostVO.setApply_type(StringUtil.showNull(wordVO.getApply_type_name()).contains("行政")?"a":"b");
		docPostVO.setBeaccount_addr(wordVO.getBeaccount_addr());
		docPostVO.setChecker_ids(parseUserNameToId(wordVO.getHy_names()));
		docPostVO.setChecker_names(StringUtil.showNull(wordVO.getHy_names()));
		docPostVO.setChecker_phones(wordVO.getHy_phones());
		docPostVO.setCopy_addr_names(wordVO.getCopy_addr());
		if(!StringUtil.isBlank(wordVO.getHq_names())){
			String[] names=wordVO.getHq_names().split(",");
		    docPostVO.setCountersigner_ids(parseUserNameToId(names));	
		}else{
		docPostVO.setCountersigner_ids("");
		}
		docPostVO.setFile_delivery_count(0);
		docPostVO.setFile_hardcover_count(0);
		docPostVO.setFile_saved_count(0);
		docPostVO.setFile_simple_count(0);
		docPostVO.setFile_total_count(0);
	
		if(!StringUtil.isBlank(wordVO.getFile_counts())){
			String[] fcs=wordVO.getFile_counts().split(",");
			if(fcs.length>=1){
				try{
				docPostVO.setFile_total_count(StringUtil.isBlank(fcs[0])?0:Integer.parseInt(fcs[0]));
				}catch(Exception ex){}
			}
			if(fcs.length>=2){
				try{
				docPostVO.setFile_hardcover_count(StringUtil.isBlank(fcs[1])?0:Integer.parseInt(fcs[1]));
				}catch(Exception ex){}
			}
			if(fcs.length>=3){
				try{
				docPostVO.setFile_simple_count(StringUtil.isBlank(fcs[2])?0:Integer.parseInt(fcs[2]));
				}catch(Exception ex){}
			}
			if(fcs.length>=4){
				try{
				docPostVO.setFile_delivery_count(StringUtil.isBlank(fcs[3])?0:Integer.parseInt(fcs[3]));
				}catch(Exception ex){}
			}
			if(fcs.length>=5){
				try{
				docPostVO.setFile_saved_count(StringUtil.isBlank(fcs[4])?0:Integer.parseInt(fcs[4]));
				}catch(Exception ex){}
			}
		}
		docPostVO.setCountersigner_names(StringUtil.showNull(wordVO.getHq_names()));
		docPostVO.setCountersigner_phones(wordVO.getHq_phones());
		docPostVO.setCreate_date(wordVO.getCreate_date());
		docPostVO.setCreater_ids(parseUserNameToId(wordVO.getCreater_name()));
		docPostVO.setCreater_names(wordVO.getCreater_name());
		docPostVO.setCtype(docPostVO.getApply_type());
		docPostVO.setCur_hq_id("");
		docPostVO.setDel_ind(0);
		docPostVO.setDep_id(wordVO.getDep_name());
		docPostVO.setDepartmentid("");
		docPostVO.setDoc_no(wordVO.getDoc_no());
		docPostVO.setDoc_seq(0);
		docPostVO.setPccpa_docid(wordVO.getId());
		AutoCodeVO autoCodeVO=parseDocTypeNameToCode(wordVO.getDoc_type_name(), wordVO.getArea_name());
		docPostVO.setDoc_type(autoCodeVO.getAtype());

		docPostVO.setNode_code("end");
		docPostVO.setNode_remark(StringUtil.getCurDateTime()+ "天健导入");
		docPostVO.setPost_addr_names(wordVO.getPost_addr());
		docPostVO.setSignissue_date(wordVO.getQf_date());
		docPostVO.setSignissuer_ids(parseUserNameToId(wordVO.getQf_names()));
		docPostVO.setSignissuer_names(StringUtil.showNull(wordVO.getQf_names()));
		docPostVO.setSignissuer_phones(wordVO.getQf_phones());
		docPostVO.setTimeout_date(wordVO.getTimeout_date());
		docPostVO.setTitle(wordVO.getTitle());
		docPostVO.setUserid(parseUserNameToId(wordVO.getCreater_name()));
		docPostVO.setCountersign_info(wordVO.getHq_info());
		docPostVO.setSignissuer_info(wordVO.getQf_info());
		docPostVO.setCheck_info(wordVO.getHy_info());
		docPostVO.setUuid(UUID.randomUUID().toString());
		docPostVO.setFile_has_appendix_ind(wordVO.getAttach()==-1?"true":"false");
		
		return docPostVO;
	}
	
	
	public List<DocLogVO> toDocLog(WordVO wordVO){
		List<DocLogVO> docLogVOs=new ArrayList<DocLogVO>();
		String qf=wordVO.getQf_info();
		if(!StringUtil.isBlank(qf)&&qf.contains("已签")){
			DocLogVO docLogVO=new DocLogVO();
			//王越豪:于2012-12-24 14:35:14签发
		    String qf_date=wordVO.getCreate_date();
			String qf_name=qf.substring(0,qf.indexOf("("));
			docLogVO.setHandle_time(qf_date);
			docLogVO.setHandler_name(qf_name);
			docLogVO.setHandler_id(parseUserNameToId(qf_name));
			docLogVO.setNode_code(Node.qf.name());
			docLogVO.setNode_name(Node.qf.getName_cn());
			docLogVO.setUuid(UUID.randomUUID().toString());
			docLogVO.setCtype("docpost");
			docLogVOs.add(docLogVO);
		}
		String hy=wordVO.getHy_info();
		if(!StringUtil.isBlank(hy)&&hy.contains("已签")){
			DocLogVO docLogVO=new DocLogVO();
			//胡少先:于2012-12-26 09:25:40核阅
		    String date=wordVO.getCreate_date();
			String name=hy.substring(0,hy.indexOf("("));;
			docLogVO.setHandle_time(date);
			docLogVO.setHandler_name(name);
			docLogVO.setHandler_id(parseUserNameToId(name));
			docLogVO.setNode_code(Node.hy.name());
			docLogVO.setNode_name(Node.hy.getName_cn());
			docLogVO.setUuid(UUID.randomUUID().toString());
			docLogVO.setCtype("docpost");
			docLogVOs.add(docLogVO);
		}
		//张靖华:于2012-12-15 16:53:04会签；张靖华:于2012-12-18 15:37:05会签；
		String hqs=wordVO.getHq_info();
		if(!StringUtil.isBlank(hqs)){
			//hqs=hqs.replace("复核", "会签");
			String[]  arr_hqs=hqs.split(",");
			
			for(String hq:arr_hqs){
				if(!hq.contains("已签"))continue;
				DocLogVO docLogVO=new DocLogVO();
				//胡少先:于2012-12-26 09:25:40核阅
			    String date=wordVO.getCreate_date();
				String name=hq.substring(0,hq.indexOf("("));;
				docLogVO.setHandle_time(date);
				docLogVO.setHandler_name(name);
				docLogVO.setHandler_id(parseUserNameToId(name));
				docLogVO.setNode_code(Node.hq.name());
				docLogVO.setNode_name(Node.hq.getName_cn());
				docLogVO.setUuid(UUID.randomUUID().toString());
				docLogVO.setCtype("docpost");
				docLogVOs.add(docLogVO);
			}
		}		
		return docLogVOs;
	}
	
	public List<DocLogVO> toDocLog(WordMsVO wordMsVO){
		List<DocLogVO> docLogVOs=new ArrayList<DocLogVO>();
		String qf=wordMsVO.getQF();
		if(!StringUtil.isBlank(qf)){
			DocLogVO docLogVO=new DocLogVO();
			//王越豪:于2012-12-24 14:35:14签发
		    String qf_date=qf.substring(qf.indexOf("于")+1,qf.lastIndexOf("签发"));
			String qf_name=qf.split(":")[0];
			docLogVO.setHandle_time(qf_date);
			docLogVO.setHandler_name(qf_name);
			docLogVO.setHandler_id(parseUserNameToId(qf_name));
			docLogVO.setNode_code(Node.qf.name());
			docLogVO.setNode_name(Node.qf.getName_cn());
			docLogVO.setUuid(UUID.randomUUID().toString());
			docLogVO.setCtype("docpost");
			docLogVOs.add(docLogVO);
		}
		String hy=wordMsVO.getHY();
		if(!StringUtil.isBlank(hy)){
			DocLogVO docLogVO=new DocLogVO();
			//胡少先:于2012-12-26 09:25:40核阅
		    String date=hy.substring(hy.indexOf("于")+1,hy.lastIndexOf("核阅"));
			String name=hy.split(":")[0];
			docLogVO.setHandle_time(date);
			docLogVO.setHandler_name(name);
			docLogVO.setHandler_id(parseUserNameToId(name));
			docLogVO.setNode_code(Node.hy.name());
			docLogVO.setNode_name(Node.hy.getName_cn());
			docLogVO.setUuid(UUID.randomUUID().toString());
			docLogVO.setCtype("docpost");
			docLogVOs.add(docLogVO);
		}
		//张靖华:于2012-12-15 16:53:04会签；张靖华:于2012-12-18 15:37:05会签；
		String hqs=StringUtil.showNull(wordMsVO.getHQ())+StringUtil.showNull(wordMsVO.getBMFZR())+StringUtil.showNull(wordMsVO.getDLHHR())
				+StringUtil.showNull(wordMsVO.getJSB())+StringUtil.showNull(wordMsVO.getXMFZR());
		if(!StringUtil.isBlank(hqs)){
			hqs=hqs.replace("复核", "会签");
			String[]  arr_hqs=hqs.split("；");
			
			for(String hq:arr_hqs){
				DocLogVO docLogVO=new DocLogVO();
				//胡少先:于2012-12-26 09:25:40核阅
			    String date=hq.substring(hq.indexOf("于")+1,hq.lastIndexOf("会签"));
				String name=hq.split(":")[0];
				docLogVO.setHandle_time(date);
				docLogVO.setHandler_name(name);
				docLogVO.setHandler_id(parseUserNameToId(name));
				docLogVO.setNode_code(Node.hq.name());
				docLogVO.setNode_name(Node.hq.getName_cn());
				docLogVO.setUuid(UUID.randomUUID().toString());
				docLogVO.setCtype("docpost");
				docLogVOs.add(docLogVO);
			}
		}
		return docLogVOs;
	}
	/*
	 "ATTACHID","ATTACHNAME","ATTACHFILE","ATTACHFILEPATH","ATTACHTYPE","UPDATEUSER","UPDATETIME","INDEXTABLE","INDEXMETADATA","INDEXID","PROPERTY","RECORDCONTENT","FILEID","FILESIZE","GATHERDATE"
"e4b660bc-9cb9-417e-99ab-97d73dc57b13","readme.et","e4b660bc-9cb9-417e-99ab-97d73dc57b13","eb2a9808-0727-48b4-85ce-2210d532ca07/e4b660bc-9cb9-417e-99ab-97d73dc57b13",\N,"9802","2012-09-27 15:07:42","eb2a9808-0727-48b4-85ce-2210d532ca07",\N,"4fd9dfce-a4c3-4236-ba68-cfc1511aadc8",\N,\N,\N,"7680",\N
"3bdd0597-046f-48df-8170-1e507e5a29c3","自助终端厂家.doc","3bdd0597-046f-48df-8170-1e507e5a29c3","eb2a9808-0727-48b4-85ce-2210d532ca07/3bdd0597-046f-48df-8170-1e507e5a29c3",\N,"9802","2012-09-27 15:07:52","eb2a9808-0727-48b4-85ce-2210d532ca07",\N,"4fd9dfce-a4c3-4236-ba68-cfc1511aadc8",\N,\N,\N,"128512",\N
	 * */
	public List<MtComAttachVO> toDocAtts(WordVO wordVO,DocPostVO docPostVO){
		List<MtComAttachVO> mtComAttachVOs=new ArrayList<MtComAttachVO>();
		List<WordFjVO> wordFjVOs=dbUtil.select(WordFjVO.class, "select * from {0} where docid=?",wordVO.getId());
		for(WordFjVO wordFjVO:wordFjVOs){
		    String iuid=docPostVO.getAttach_id();
			String auid=UUID.randomUUID().toString();
			MtComAttachVO mtComAttachVO=new MtComAttachVO();
			mtComAttachVO.setATTACHFILE(auid);
			mtComAttachVO.setATTACHFILEPATH(iuid+"/"+auid);
			mtComAttachVO.setATTACHID(auid);
			mtComAttachVO.setATTACHNAME(wordFjVO.getFJName());
			mtComAttachVO.setATTACHTYPE("");
			mtComAttachVO.setFILEID("");
			mtComAttachVO.setFILESIZE(1000);
			mtComAttachVO.setGATHERDATE("");
			mtComAttachVO.setINDEXID(iuid);
			mtComAttachVO.setINDEXMETADATA("");
			mtComAttachVO.setPROPERTY("");
			mtComAttachVO.setRECORDCONTENT("");
			mtComAttachVO.setUPDATETIME(StringUtil.getCurDateTime());
			mtComAttachVO.setUPDATEUSER(docPostVO.getCreater_ids());
			mtComAttachVOs.add(mtComAttachVO);
		}
		return mtComAttachVOs;
	}
	
	
	private String parseUserNameToId(String name){
		String id="";
		try {
			id=dbUtil.queryForString("select id from k_user where name=?", new String[]{name});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return id==null?"":id;
	}
	
	public String parseUserNameToId(String[] names){
		String ids="";
		try {
			for(String name :names){
			 String id=dbUtil.queryForString("select id from k_user where name=?", new String[]{name});
			 ids+=id+",";
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ids;
	} 
	
	private AutoCodeVO parseDocTypeNameToCode(String name,String areaName){
		KAreaVO kAreaVO=null;
		AutoCodeVO autoCodeVO=new AutoCodeVO();
		autoCodeVO.setAtype("");
		try {
			kAreaVO = dbUtil.load(KAreaVO.class, "name", areaName);
			autoCodeVO=dbUtil.select(AutoCodeVO.class, "select * from {0} where aname=? and areaid like ?", name,"%"+kAreaVO.getAutoid().toString()+"%").get(0);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		return autoCodeVO;
	}
}
