package com.matech.audit.service.process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.matech.audit.service.process.model.ProcessApply;
import com.matech.audit.service.process.model.ProcessDeploy;
import com.matech.audit.service.process.model.ProcessField;
import com.matech.audit.service.process.model.ProcessForm;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.log.Log;
import com.matech.framework.pub.util.StringUtil;


public class ProcessService {

	private Connection conn = null;
	private static Log log = new Log(ProcessService.class) ;

	public ProcessService(Connection conn) {
		this.conn = conn;
	}

	/**
	 * 
	 * 新增流程方法
	 * @param processDeploy
	 * @throws Exception
	 */
	public void addProcessDeploy(ProcessDeploy processDeploy) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null ;
		try {
			String sql = "insert into mt_jbpm_processdeploy( "
					+ " 	id, pdid, pkey, pname, desccontent, "
					+ " 	property, jbpmxml, flowfile, updateuser, updatetime, "
					+ " 	relateForm,orderByRelateForm,processDes,join_sql,join_head_jarr,hidden_cols "
					+ " ) values( ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?,?)";
			
			conn.setAutoCommit(false) ;
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, processDeploy.getId());
			ps.setString(i++, processDeploy.getPdId());
			ps.setString(i++, processDeploy.getPkey());
			ps.setString(i++, processDeploy.getPname());
			ps.setString(i++, processDeploy.getDesccontent());

			ps.setString(i++, processDeploy.getProperty());
			ps.setString(i++, processDeploy.getJbpmXml());
			ps.setString(i++, processDeploy.getFlowFile());
			ps.setString(i++, processDeploy.getUpdateUser());
			ps.setString(i++, processDeploy.getUpdateTime());
			
			ps.setString(i++, processDeploy.getRelateForm()) ;
			ps.setString(i++, processDeploy.getOrderByRelateForm()) ;
			ps.setString(i++, processDeploy.getProcessDes()) ;
			ps.setString(i++, processDeploy.getJoin_sql()) ;
			ps.setString(i++, processDeploy.getJoin_head_jarr()) ;
			ps.setString(i++, processDeploy.getHidden_cols()) ;
			ps.execute();
			
		     //提交       
		     conn.commit();      
		     conn.setAutoCommit(true);
		     
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
		}
	}

	public void updateProcessDeploy(ProcessDeploy processDeploy) throws Exception{
		PreparedStatement ps = null;
		try {
			String sql = "update mt_jbpm_processdeploy set "
					+ " pname=?,desccontent=?, property=?, "
					+ " updateuser=?, updatetime=?,relateForm=?,orderByRelateForm=?, "
					+ " processDes=?,join_sql=?,join_head_jarr=?,hidden_cols=? "
					+ " where id=?";
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, processDeploy.getPname());
			ps.setString(i++, processDeploy.getDesccontent());
			ps.setString(i++, processDeploy.getProperty());

			ps.setString(i++, processDeploy.getUpdateUser());
			ps.setString(i++, processDeploy.getUpdateTime());
			ps.setString(i++, processDeploy.getRelateForm());
			ps.setString(i++, processDeploy.getOrderByRelateForm());
			
			ps.setString(i++, processDeploy.getProcessDes()) ;
			ps.setString(i++, processDeploy.getJoin_sql()) ;
			ps.setString(i++, processDeploy.getJoin_head_jarr()) ;
			ps.setString(i++, processDeploy.getHidden_cols()) ;
			
			ps.setString(i++, processDeploy.getId());
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
		}
	}
	
	public void updateDesign(ProcessDeploy processDeploy) throws Exception{
		PreparedStatement ps = null;
		try {
			String sql = "update mt_jbpm_processdeploy set "
					+ " pdid=?,jbpmxml=?, "
					+ " updateuser=?, updatetime=?,notSelectUserNodes=? "
					+ " where id=?";
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, processDeploy.getPdId());
			ps.setString(i++, processDeploy.getJbpmXml());

			ps.setString(i++, processDeploy.getUpdateUser());
			ps.setString(i++, processDeploy.getUpdateTime());
			ps.setString(i++, processDeploy.getNotSelectUserNodes());

			ps.setString(i++, processDeploy.getId());
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
		}
	}

	public ProcessDeploy getProcessDeploy(String key) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		ProcessDeploy pd = null;
		try {
			String sql = " select id, pdid, pkey, pname, desccontent, property,relateForm," +
						 " notSelectUserNodes,orderByRelateForm,processDes,join_sql,join_head_jarr,hidden_cols " +
						 " from mt_jbpm_processdeploy where pkey=? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, key);
			rs = ps.executeQuery();
			if (rs.next()) {
				pd = new ProcessDeploy();
				pd.setId(rs.getString(1));
				pd.setPdId(rs.getString(2));
				pd.setPkey(rs.getString(3));
				pd.setPname(rs.getString(4));
				pd.setDesccontent(rs.getString(5));
				pd.setProperty(rs.getString(6));
				pd.setRelateForm(rs.getString(7));
				pd.setNotSelectUserNodes(rs.getString(8));
				pd.setOrderByRelateForm(rs.getString(9));
				pd.setProcessDes(rs.getString(10));
				pd.setJoin_sql(rs.getString(11));
				pd.setJoin_head_jarr(rs.getString(12));
				pd.setHidden_cols(rs.getString(13));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
		return pd;
	}

	public ProcessDeploy getProcessDeployById(String id) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		ProcessDeploy pd = null;
		try {
			String sql = "select id, pdid, pkey, pname, desccontent, "
					+ " property, jbpmxml, flowfile, updateuser, updatetime, "
					+ " relateForm,notSelectUserNodes,orderByRelateForm,processDes,join_sql,join_head_jarr,hidden_cols "
					+ " from mt_jbpm_processdeploy " + " where id=? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, id);
			rs = ps.executeQuery();
			if (rs.next()) {
				pd = new ProcessDeploy();
				pd.setId(rs.getString(1));
				pd.setPdId(rs.getString(2));
				pd.setPkey(rs.getString(3));
				pd.setPname(rs.getString(4));
				pd.setDesccontent(rs.getString(5));

				pd.setProperty(rs.getString(6));
				pd.setJbpmXml(rs.getString(7));
				pd.setFlowFile(rs.getString(8));
				pd.setUpdateUser(rs.getString(9));
				pd.setUpdateTime(rs.getString(10));
				
				pd.setRelateForm(rs.getString(11)) ;
				pd.setNotSelectUserNodes(rs.getString(12)) ;
				pd.setOrderByRelateForm(rs.getString(13)) ;
				pd.setProcessDes(rs.getString(14)) ;
				pd.setJoin_sql(rs.getString(15));
				pd.setJoin_head_jarr(rs.getString(16));
				pd.setHidden_cols(rs.getString(17));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
		return pd;
	}

	public void deleteProcessDeploy(String id) {
		PreparedStatement ps = null;
		try {
			String sql = "delete from mt_jbpm_processdeploy where id=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, id);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}

	public Map<String, String> getProcessForm(String processInstanseId) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Map<String, String> map = new HashMap<String, String>();
		try {
			String sql = "select id, pid, `key`, `value`, nodename, dealuserid, dealtime, property from mt_jbpm_processform where pid=? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, processInstanseId);
			rs = ps.executeQuery();
			while (rs.next()) {
				map.put(rs.getString(1), rs.getString(2));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return map;
	}

	public boolean isNodeNameExist(String nodeName, String processInstanseId) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select 1 from mt_jbpm_processform where pid=? and nodeName like '%"
					+ nodeName + "%' ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, processInstanseId);
			rs = ps.executeQuery();
			if (rs.next()) {
				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return false;
	}

	public void addProcessForm(ProcessForm pf) {
		PreparedStatement ps = null;
		try {
			String sql = "insert into mt_jbpm_processform(`uuid`, pid, `key`, `value`, nodename, dealuserid, dealtime, property,formid,formEntityId) values(?,?,?,?, ?,?,?,? ,?,?)";
			ps = conn.prepareStatement(sql);
			ps.setString(1, StringUtil.getUUID());
			ps.setString(2, pf.getpId());
			ps.setString(3, pf.getKey());
			ps.setString(4, pf.getValue());
			
			ps.setString(5, pf.getNodeName());
			ps.setString(6, pf.getDealUserId());
			ps.setString(7, pf.getDealTime());
			ps.setString(8, pf.getProperty());
			
			ps.setString(9,pf.getFormId()) ;
			ps.setString(10,pf.getFormEntityId()) ;

			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}

	public List<ProcessForm> getNodeList(String processInstanseId) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		List<ProcessForm> nodeList = new ArrayList<ProcessForm>();
		try {
			String sql = "select DISTINCT nodeName,b.name as user_name,dealTime from mt_jbpm_processform a "
					+ " left join k_user b on a.dealUserId = b.id "
					+ " where pId= ? ORDER BY dealTime ASC,nodeName";
			ps = conn.prepareStatement(sql);
			ps.setString(1, processInstanseId);
			rs = ps.executeQuery();
			while (rs.next()) {

				String nodeName = rs.getString(1);
				String dealTime = rs.getString(3);
				ProcessForm pf = new ProcessForm();
				pf.setNodeName(nodeName);
				pf.setDealUserId(rs.getString(2));
				pf.setDealTime(dealTime);

				List<ProcessForm> formList = new ArrayList<ProcessForm>();
				sql = "select `key`,`value`,property from mt_jbpm_processform where pId= ? and nodeName='"
						+ nodeName + "' and dealTime='" + dealTime + "'";
				ps = conn.prepareStatement(sql);
				ps.setString(1, processInstanseId);
				rs2 = ps.executeQuery();
				while (rs2.next()) {
					ProcessForm cpf = new ProcessForm();
					cpf.setKey(rs2.getString(1));
					cpf.setValue(rs2.getString(2));
					cpf.setProperty(rs2.getString(3));
					formList.add(cpf);
				}
				pf.setFormList(formList);

				nodeList.add(pf);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return nodeList;
	}

	public List<ProcessForm> getNodeListGroupbyNodeName(String processInstanseId) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		List<ProcessForm> nodeList = new ArrayList<ProcessForm>();
		try {
			String sql = "select DISTINCT nodeName,dealUserId,dealTime from mt_jbpm_processform a "
					+ " where pId= ? group by nodeName,dealTime ORDER BY dealTime ASC,nodeName";
			ps = conn.prepareStatement(sql);
			ps.setString(1, processInstanseId);
			rs = ps.executeQuery();
			String tempNodeName = "";
			while (rs.next()) {

				String nodeName = rs.getString(1);
				String dealTime = rs.getString(3);
				ProcessForm pf = new ProcessForm();
				pf.setNodeName(nodeName);
				pf.setDealUserId(rs.getString(2));
				pf.setDealTime(dealTime);

				if (tempNodeName.equals(nodeName)) {
					pf.setNodeName("");
				}

				List<ProcessForm> formList = new ArrayList<ProcessForm>();
				sql = "select `key`,`value`,property,dealTime from mt_jbpm_processform where pId= ? and nodeName='"
						+ nodeName + "' and dealTime='" + dealTime + "'";
				ps = conn.prepareStatement(sql);
				ps.setString(1, processInstanseId);
				rs2 = ps.executeQuery();

				while (rs2.next()) {
					ProcessForm cpf = new ProcessForm();
					cpf.setKey(rs2.getString(1));
					cpf.setValue(rs2.getString(2));
					cpf.setProperty(rs2.getString(3));
					formList.add(cpf);
				}
				pf.setFormList(formList);

				tempNodeName = rs.getString(1);
				nodeList.add(pf);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return nodeList;
	}

	public void addProcessApply(ProcessApply pa) {
		PreparedStatement ps = null;
		try {
			String sql = " insert into mt_jbpm_apply(id, pkey, pid, foreignid, " 
					   + " pname, applytime, applyuserid, property,processDesc) " 
					   + " values(?,?,?,?, ?,?,?,?, ?)";
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, pa.getId());
			ps.setString(i++, pa.getPkey());
			ps.setString(i++, pa.getPid());
			ps.setString(i++, pa.getForeignId());

			ps.setString(i++, pa.getPname());
			ps.setString(i++, pa.getApplyTime());
			ps.setString(i++, pa.getApplyUserId());
			ps.setString(i++, pa.getProperty());
			
			ps.setString(i++, pa.getProcessDes()) ;

			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
	
	

	/**
	 * 
	 * 获取流程节点对应表单的可读和可写属性
	 * @param formid 对应表单id
	 * @param processKey 对应流程key
	 * @param nodeName 对应流程节点名称
	 * @return
	 * @throws Exception
	 */
	public List<ProcessField> getProcessFieldList(String formid,String processKey,String nodeName) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null ;
		List<ProcessField> fieldList = new ArrayList<ProcessField>() ;
		try {
			
			String sql = " select a.uuid,a.name,a.enname,a.fieldType,a.tableName,ifnull(b.isHide,'否') as isHide,ifnull(b.isReadOnly,'否') as isReadOnly, " 
					   + " ifnull(b.isProcessVariable,'否') as isProcessVariable "
					   + " from ("
					   + " 		select 1 as myorderid,a.orderid,a.uuid,a.name,a.enname,'mainFiled' as fieldType,b.tableName from mt_com_form_field a "
					   + " 		left join mt_com_form b on a.formid = b.uuid "
					   + " 		where formid = ? "
					   + " 		union "
					   + " 		select 2 as myorderid,0,a.uuid,'子表【' || a.name || '】',a.enname,'subList',a.tableName from mt_com_form a  "
					   + " 		where a.parentformid=?  "
					   + " 		union "
					   + " 		select 3 as myorderid,a.orderid,a.uuid,a.name,a.enname,'subField',b.tableName from mt_com_form_field a "
					   + " 		left join mt_com_form b on a.formid = b.uuid  "
					   + " 		where b.parentformid = ? "
					   + " 	) a "
					   + " 	left join mt_jbpm_form_config b on a.uuid = b.fieldid and b.processKey=? and b.nodeName=?  "
					   + " 	where 1=1 "
					   + " 	order by a.myorderid,a.orderid " ;
					   
			ps = conn.prepareStatement(sql);
			ps.setString(1, formid);
			ps.setString(2, formid);
			ps.setString(3, formid);
			ps.setString(4, processKey);
			ps.setString(5, nodeName);
			log.debug("formSql:"+sql) ;
			rs = ps.executeQuery() ;
			
			while(rs.next()) {
				ProcessField processField = new ProcessField() ;
				int i = 1 ;
				processField.setUuid(rs.getString(i++)) ;
				processField.setName(rs.getString(i++)) ;
				processField.setEnname(StringUtil.showNull(rs.getString(i++)).toLowerCase()) ;
				processField.setType(rs.getString(i++)) ;
				processField.setTableName(rs.getString(i++)) ;
				processField.setIsHide(rs.getString(i++)) ;
				processField.setIsReadOnly(rs.getString(i++)) ;
				processField.setIsProcessVariable(rs.getString(i++)) ;
				 
				fieldList.add(processField) ;
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e ;
		} finally {
			DbUtil.close(ps);
		}
		return fieldList ;
	}
	
	

	/**
	 * 删除节点对应表单的可读和可写属性
	 * @param processKey 流程key
	 * @param nodeName 节点名称
	 */
	public void delFieldConfig(String processKey,String nodeName) {
		PreparedStatement ps = null;
		try {
			String sql = "delete from mt_jbpm_form_config where processKey=? and nodeName=?" ;
			ps = conn.prepareStatement(sql) ;
			ps.setString(1,processKey) ;
			ps.setString(2,nodeName) ;
			ps.execute() ;
		} catch (Exception e) {
			log.exception("流程配置表单属性删除失败!",e) ;
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
	
	
	/**
	 * 新增节点对应表单的可读和可写属性
	 * @param ff 实体类
	 */
	public void addFieldConfig(ProcessField ff) {
		PreparedStatement ps = null;
		try {
			
			String sql = "insert into mt_jbpm_form_config(`uuid`,fieldid,nodename,ishide, isreadonly,processkey,property,isProcessVariable, formId) " 
					   + " values(?,?,?,?, ?,?,?,?, ?)";
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, StringUtil.getUUID());
			ps.setString(i++, ff.getUuid());
			ps.setString(i++, ff.getNodeName());
			ps.setString(i++, ff.getIsHide());

			ps.setString(i++, ff.getIsReadOnly());
			ps.setString(i++, ff.getProcessKey());
			ps.setString(i++, ff.getProperty());
			ps.setString(i++, ff.getIsProcessVariable()) ;
			
			ps.setString(i++, ff.getFormid()) ;
			ps.execute();
			
		} catch (Exception e1) {
			log.exception("增加流程配置表单属性失败!",e1) ;
			e1.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
	
	
	/**
	 * 获取流程对应表单是否已有对应新增修改记录
	 * @param pId  流程实例id
	 * @param formId 表单id
	 * @return
	 */
	public String getProcessFormEntityId(String pId,String formId) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String formEntityId = "" ;
		try {
			String sql = "select formEntityId from mt_jbpm_processform where pid=? and formid=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, pId);
			ps.setString(2, formId);
			rs = ps.executeQuery();
			if(rs.next()) {
				formEntityId = StringUtil.showNull(rs.getString(1)) ;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return formEntityId;
	}
	
	
	/**
	 * 获得流程已办任务sql
	 * @param pkey  流程定义key
	 * @param userId 登陆人员id
	 * @return
	 */
	public static String getDealtSql(String pkey,String userId) {
		
		String sql = "select distinct a.pid,b.pname,b.pkey,a.applytime,d.name as userName, \n"
					+"c.arrivetime,ifnull(e.ACTIVITYNAME_,'审结') activeName, \n"
					+"z.dealUserName,Z.CREATE_ as create_, \n"
					+"z.DBID_ as taskId,a.foreignid as foreignUuid,a.processDesc \n"
					+"from mt_jbpm_apply a \n"
					+"left join mt_jbpm_processdeploy b on a.pkey = b.pkey \n"
					+"left join mt_jbpm_processform c on a.pid = c.pid \n"
					+"left join k_user d on a.applyuserid = d.id \n"
					+"left join jbpm4_execution e on a.pid = e.ID_  \n"
					+"left join( \n"
					+"    SELECT a.DBID_,a.EXECUTION_ID_,group_concat(distinct d.name) dealUserName,max(a.create_) as create_ FROM jbpm4_task a \n" 
					+"    LEFT JOIN  jbpm4_participation b ON a.DBID_ = b.TASK_ AND b.type_ = 'candidate' \n"
					+"    left join k_user d on b.userid_ = d.id \n"
					+"    GROUP BY a.EXECUTION_ID_,A.DBID_ \n"
					+") z on  z.EXECUTION_ID_= A.PID \n" 
					+"where 1= 1  \n" ;
		
		if(!"".equals(userId)) {
			sql += " and c.dealuserid = '" + userId + "' " ;
		}
		
		if(!"".equals(pkey)) {
			pkey = pkey.replaceAll(",", "','") ;
			sql += " and a.pkey in('" + pkey + "')" ;
		}
		
		return sql ;
		
	}
	
	
	/**
	 * 获取流程待办任务sql
	 * @param pkey 流程定义key
	 * @param userId 登陆人员id
	 * @return
	 */
	public static String getDealingSql(String pkey,String userId) {
		
		String sql = "  SELECT a.taskId,a.auditStatus,d.pKey,d.pName,a.ID_ as eid,d.descContent,a.CREATE_ as create_,  \n" 
					+ "		e.applytime,f.name as applyUserName,Z.dealUserName,e.foreignid as foreignUuid,e.processDesc \n" 
					+ "		FROM ( \n" 
					+ "			SELECT DISTINCT a.DBID_ AS taskId,b.ID_,a.EXECUTION_ID_,b.ACTIVITYNAME_ AS auditStatus, PROCDEFID_,\n"  
					+ "			a.create_ \n" 
					+ "			FROM jbpm4_task a  \n" 
					+ "			INNER JOIN jbpm4_execution b ON a.EXECUTION_ID_ = b.ID_ \n"   
					+ "			LEFT JOIN  jbpm4_participation c ON a.DBID_ = c.TASK_  AND c.type_ = 'candidate' \n"   
					+ "			WHERE 1=1 \n"  ;
		
		if(!"".equals(userId)) {
				sql += "		AND c.userID_ = '" + userId   + "'   \n"  ;
		}
					
				sql += "	) a  \n" 
					+ "		LEFT JOIN jbpm4_deployprop c ON a.PROCDEFID_ = c.STRINGVAL_\n"  
					+ "		LEFT JOIN MT_JBPM_PROCESSDEPLOY d ON c.objname_ = d.pKey \n"  
					+ "		left join mt_jbpm_apply e on a.ID_ = e.pid  \n"  
					+ "		left join k_user f on e.applyuserid = f.id \n"
					+"		left join( \n"
					+"    		SELECT a.EXECUTION_ID_,group_concat(d.name) as dealUserName FROM jbpm4_task a \n" 
					+"    		LEFT JOIN  jbpm4_participation b ON a.DBID_ = b.TASK_ AND b.type_ = 'candidate' \n"
					+"    		left join k_user d on b.userid_ = d.id \n"
					+"    		GROUP BY A.EXECUTION_ID_ \n"
					+"		) z on  z.EXECUTION_ID_=A.ID_ \n" 
					+ "		where 1=1 \n"  ;

		if(!"".equals(pkey)) {
			pkey = pkey.replaceAll(",", "','") ;
			sql += " and d.pkey in('" + pkey + "')" ;
		}
		
		return sql ;
		
	}
	
	/**
	 * 根据uuid和流程Key找到流程实例id
	 * @param pId  流程实例id
	 * @param formId 表单id
	 * @return
	 */
	public String getPIdByPKeyAndForeignId(String pKey,String foreignId) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String pId = "" ;
		try {
			String sql = "select pid from mt_jbpm_apply where pKey=? and foreignid=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, pKey);
			ps.setString(2, foreignId);
			rs = ps.executeQuery();
			if(rs.next()) {
				pId = StringUtil.showNull(rs.getString(1)) ;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return pId;
	}
	
	/**
	 * 根据uuid和流程Key删除流程申请
	 * @param pId  流程实例id
	 * @param foreignId 表单id
	 * @return
	 */
	public void deleteApply(String pKey,String foreignId) {
		PreparedStatement ps = null;
		try {
			String sql = "delete from mt_jbpm_apply where pKey=? and foreignid=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, pKey);
			ps.setString(2, foreignId);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 根据pId删除意见
	 * @param pId  流程实例id
	 * @return
	 */
	public void deleteProcessform(String pId) {
		PreparedStatement ps = null;
		try {
			String sql = "delete from mt_jbpm_processform where PId=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, pId);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
	
	
	/**
	 * 获取节点的处理人
	 * @param pId
	 * @return
	 */
	public List<Map<String,String>> getNodeUserList(String pId,String nodeName) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Map<String,String>> nodeList = new ArrayList<Map<String,String>>();
		try {
			String sql = "select DISTINCT a.dealUserId,b.name as user_name from mt_jbpm_processform a "
					+ " left join k_user b on a.dealUserId = b.id "
					+ " where pId= ? and nodeName = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, pId);
			ps.setString(2, nodeName);
			rs = ps.executeQuery();
			while (rs.next()) {
				Map<String,String> map = new HashMap<String, String>() ;
				
				String userId = rs.getString(1);
				String userName = rs.getString(2);
				
				map.put("userId",userId) ;
				map.put("userName",userName) ;
				
				nodeList.add(map) ;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return nodeList;
	}

	/** 从保存后加入审核意见
	 * @param pf
	 */
	public void addProcessFormFromSave(ProcessForm pf) {
		PreparedStatement ps = null;
		try {
			String sql = 
				"insert into mt_jbpm_processform  \n"+
				" (`uuid`, pid, `key`, `value`, nodename, dealuserid, dealtime, property,formid,formEntityId)  \n"+
				" select distinct ?,a.PID,?,?,?,?,?,?,FORMID,?  \n"+
				" from MT_JBPM_PROCESSFORM a  \n"+
				" where a.FORMENTITYID=? and a.PID is not null and rownum<=1 \n";
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, StringUtil.getUUID());
			ps.setString(2, pf.getKey());
			ps.setString(3, pf.getValue());
			
			ps.setString(4, pf.getNodeName());
			ps.setString(5, pf.getDealUserId());
			ps.setString(6, pf.getDealTime());
			ps.setString(7, pf.getProperty());
			
			ps.setString(8,pf.getFormEntityId()) ;
			ps.setString(9,pf.getFormEntityId()) ;

			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
	
}
