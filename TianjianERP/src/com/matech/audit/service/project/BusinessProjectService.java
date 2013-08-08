package com.matech.audit.service.project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.attach.model.Attach;
import com.matech.audit.service.customer.model.CustomerProject;
import com.matech.audit.service.project.model.BusinessProject;
import com.matech.framework.pub.autocode.DELUnid;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

/**
 * <p>Title: 业务项目服务类</p>
 * <p>Description: 实现业务项目详细信息的管理，如生成审计类型树等</p>
 * <p>Copyright: Copyright (c) 2006, 2008 MaTech Corporation.All rights reserved. </p>
 * <p>Company: Matech  广州铭太信息科技有限公司</p>
 *
 * 本程序及其相关的所有资源均为铭太科技公司研发中心审计开发组所有，
 * 版本发行及解释权归研发中心，公司网站为：http://www.matech.cn
 *
 * 贡献者团队:
 *     铭太科技 - 研发中心，审计开发组
 *
 * @author bill
 * 2010-9-9
 */
public class BusinessProjectService {
	
	private Connection conn = null ;
	
	public BusinessProjectService(Connection conn) {
		this.conn = conn ;
	}
	
	/**
	 * 保存项目的方法
	 * @param project
	 * @return
	 * @throws Exception
	 */
	public void save(BusinessProject bp) throws Exception {
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
		 
			//插入项目表
			StringBuffer sql = new StringBuffer();
			//companyType business contactUser contactPhone businesChannel reportRequire reportDate 
			sql.append(" insert into asdb.z_projectbusiness( ")
				.append(" projectID,projectname,EntrustCustomerId,customerId,payCustomerId, ")
				.append(" auditPara,typeId,isSpecialProject,isNewTakeProject,isReport, ")
				.append(" customerType,managerUserId,departManagerUserId,partnerUserId,ristPartnerUserId, ")
				.append(" seniorCpaUserId,ristLevel,isStore,signedDate,businessCost, ")
				.append(" property,projectPartner1,projectPartner2,qualityPartner,signedCpa1,signedCpa2, ")
				.append(" creator,createTime,departmentid,companyType,business, ")
				.append(" contactUser,contactPhone,businesChannel,reportRequire,reportDate,")
				.append(" isStock,entrustNumber,reportNumber,registerNum,reportfilename, ")
				.append(" reportFileTempName,state,travelAgree,secretFileName,secretFileTempName, ")
				.append(" isNewBusiness,businessResource,continueUser,introduceUser,costPromise, ")
				.append(" businessDesc,travelPromise,finishYear,parentRegisterNum,instalment_AnLian,projectFile_AnLian,planDate_AnLian,expertUserId,qualityUserId,signaturePartnerUserId) ")
				
				.append(" values(?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?,?,?,?,?,?) ");
			
			ps = conn.prepareStatement(sql.toString());
			int i = 1 ;
			ps.setString(i++,bp.getProjectID()) ;
			ps.setString(i++,bp.getProjectName()) ;
			ps.setString(i++,bp.getEntrustCustomerId()) ;
			ps.setString(i++,bp.getCustomerId()) ;
			ps.setString(i++,bp.getPayCustomerId()) ;
			
			ps.setString(i++,bp.getAuditpara()) ;
			ps.setString(i++,bp.getTypeId()) ;
			ps.setString(i++,bp.getIsSpecialProject()) ;
			ps.setString(i++,bp.getIsNewTakeProject()) ;
			ps.setString(i++,bp.getIsReport()) ;
			
			ps.setString(i++,bp.getCustomerType()) ;
			ps.setString(i++,bp.getManagerUserId()) ;
			ps.setString(i++,bp.getDepartManagerUserId()) ;
			ps.setString(i++,bp.getPartnerUserId()) ;
			ps.setString(i++,bp.getRistPartnerUserId()) ;
			
			ps.setString(i++,bp.getSeniorCpaUserId()) ;
			ps.setString(i++,bp.getRistLevel()) ;
			ps.setString(i++,bp.getIsStore()) ;
			ps.setString(i++,bp.getSignedDate()) ;
			ps.setString(i++,bp.getBusinessCost()) ;
			
			ps.setString(i++,bp.getProperty()) ;
			ps.setString(i++,bp.getProjectPartner1()) ;
			ps.setString(i++,bp.getProjectPartner2()) ;
			ps.setString(i++,bp.getQualityPartner()) ;
			ps.setString(i++,bp.getSignedCpa1()) ;
			ps.setString(i++,bp.getSignedCpa2()) ;
			
			ps.setString(i++,bp.getCreator()) ;
			ps.setString(i++,bp.getCreateTime()) ;
			ps.setString(i++,bp.getDepartmentId()) ;
			ps.setString(i++,bp.getCompanyType()) ;
			ps.setString(i++,bp.getBusiness()) ;
			
			ps.setString(i++,bp.getContactUser()) ;
			ps.setString(i++,bp.getContactPhone()) ;
			ps.setString(i++,bp.getBusinesChannel()) ;
			ps.setString(i++,bp.getReportRequire()) ;
			ps.setString(i++,bp.getReportDate()) ;
			
			ps.setString(i++,bp.getIsStock()) ;
			ps.setString(i++,bp.getEntrustNumber()) ;
			ps.setString(i++,bp.getReportNumeber()) ;
			ps.setString(i++,bp.getRegisterNum()) ;
			ps.setString(i++,bp.getReportFileName()) ;
			
			ps.setString(i++,bp.getReportFileTempName()) ;
			ps.setString(i++,bp.getState()) ;
			ps.setString(i++,bp.getTravelAgree()) ;
			ps.setString(i++,bp.getSecretFileName()) ;
			ps.setString(i++,bp.getSecretFileTempName()) ;
			
			ps.setString(i++,bp.getIsNewBusiness()) ;
			ps.setString(i++,bp.getBusinessResource()) ;
			ps.setString(i++,bp.getContinueUser()) ;
			ps.setString(i++,bp.getIntroduceUser()) ;
			ps.setString(i++,bp.getCostPromise()) ;
			
			ps.setString(i++,bp.getBusinessDesc()) ;
			ps.setString(i++,bp.getTravelPromise()) ;
			ps.setString(i++,bp.getFinishYear()) ;
			ps.setString(i++,bp.getParentRegisterNum()) ;
			ps.setString(i++, bp.getInstalment_AnLian());
			ps.setString(i++, bp.getProjectFile_AnLian());
			ps.setString(i++, bp.getPlanDate_AnLian());
			ps.setString(i++, bp.getExpertUserId());
			ps.setString(i++, bp.getQualityUserId());
			
			ps.setString(i++, bp.getSignaturePartnerUserId());
			
			
			ps.execute();
			
		} catch (Exception e) {
			System.out.println("qwh:e:"+e.getMessage());
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}
	
	/**
	 * 更新项目信息
	 * @param project
	 * @throws Exception
	 */
	public void update(BusinessProject bp) throws Exception {
		PreparedStatement ps = null;
		String sql = "";
		try {
			
			//执行更新
			
			sql="update asdb.z_projectbusiness "
				+" set projectname=?,EntrustCustomerId=?, customerId=?, payCustomerId=?, auditPara=?, "
				+" typeId=?, isSpecialProject=?, isNewTakeProject=?, isReport=?, customerType=?, "
				+" managerUserId=?, departManagerUserId=?, partnerUserId=?, ristPartnerUserId=?, seniorCpaUserId=?, "
				+" ristLevel=?, isStore=?, signedDate=?, businessCost=?, property=?, "
				+" projectPartner1=?,projectPartner2=?, qualityPartner=?, signedCpa1=?, signedCpa2=?, creator=?, "
				+" createTime=?, departmentid=?, companyType=?, business=?, contactUser=?, "
				+" contactPhone=?, businesChannel=?, reportRequire=?, reportDate=?, isstock=?, "
				+" entrustNumber = if(ifnull(entrustNumber,'')='' or entrustNumber = 0,?,entrustNumber) ,"
				+" reportNumber  = if(ifnull(reportNumber,'')='' or reportNumber = 0,?,reportNumber) ,"
				+" travelAgree = ?,isNewBusiness=?,businessResource=?,continueUser=?,introduceUser=?, "
				+" costPromise = ?,businessDesc=?,travelPromise=?,finishYear=?,state=?, "
				+" parentRegisterNum=?,reportfilename=?,reportFileTempName=?,secretFileName=?,secretFileTempName=?,instalment_AnLian=?,projectFile_AnLian=?,planDate_AnLian=?,expertUserId=?,qualityUserId=?,signaturePartnerUserId=? "
				+" where ProjectID=? ";
			ps = conn.prepareStatement(sql.toString());
			
			int i = 1 ;
			ps.setString(i++,bp.getProjectName()) ;
			ps.setString(i++,bp.getEntrustCustomerId()) ;
			ps.setString(i++,bp.getCustomerId()) ;
			ps.setString(i++,bp.getPayCustomerId()) ;
			ps.setString(i++,bp.getAuditpara()) ;
			
			ps.setString(i++,bp.getTypeId()) ;
			ps.setString(i++,bp.getIsSpecialProject()) ;
			ps.setString(i++,bp.getIsNewTakeProject()) ;
			ps.setString(i++,bp.getIsReport()) ;
			ps.setString(i++,bp.getCustomerType()) ;
			
			ps.setString(i++,bp.getManagerUserId()) ;
			ps.setString(i++,bp.getDepartManagerUserId()) ;
			ps.setString(i++,bp.getPartnerUserId()) ;
			ps.setString(i++,bp.getRistPartnerUserId()) ;
			ps.setString(i++,bp.getSeniorCpaUserId()) ;
			
			ps.setString(i++,bp.getRistLevel()) ;
			ps.setString(i++,bp.getIsStore()) ;
			ps.setString(i++,bp.getSignedDate()) ;
			ps.setString(i++,bp.getBusinessCost()) ;
			ps.setString(i++,bp.getProperty()) ;
			
			ps.setString(i++,bp.getProjectPartner1()) ;
			ps.setString(i++,bp.getProjectPartner2()) ;
			ps.setString(i++,bp.getQualityPartner()) ;
			ps.setString(i++,bp.getSignedCpa1()) ;
			ps.setString(i++,bp.getSignedCpa2()) ;
			ps.setString(i++,bp.getCreator()) ;
			
			ps.setString(i++,bp.getCreateTime()) ;
			ps.setString(i++,bp.getDepartmentId()) ;
			ps.setString(i++,bp.getCompanyType()) ;
			ps.setString(i++,bp.getBusiness()) ;
			ps.setString(i++,bp.getContactUser()) ;
			
			ps.setString(i++,bp.getContactPhone()) ;
			ps.setString(i++,bp.getBusinesChannel()) ;
			ps.setString(i++,bp.getReportRequire()) ;
			ps.setString(i++,bp.getReportDate()) ;
			ps.setString(i++,bp.getIsStock()) ;
			
			ps.setString(i++,bp.getEntrustNumber()) ;
			ps.setString(i++,bp.getReportNumeber()) ;
			ps.setString(i++,bp.getTravelAgree()) ;
			ps.setString(i++,bp.getIsNewBusiness()) ;
			ps.setString(i++,bp.getBusinessResource()) ;
			
			ps.setString(i++,bp.getContinueUser()) ;
			ps.setString(i++,bp.getIntroduceUser()) ;
			
			ps.setString(i++,bp.getCostPromise()) ;
			ps.setString(i++,bp.getBusinessDesc()) ;
			ps.setString(i++,bp.getTravelPromise()) ;
			ps.setString(i++,bp.getFinishYear()) ;
			ps.setString(i++,bp.getState()) ;
			
			ps.setString(i++,bp.getParentRegisterNum()) ;
			ps.setString(i++,bp.getReportFileName()) ;
			ps.setString(i++,bp.getReportFileTempName()) ;
			ps.setString(i++,bp.getSecretFileName()) ;
			ps.setString(i++,bp.getSecretFileTempName()) ;
			ps.setString(i++, bp.getInstalment_AnLian());
			ps.setString(i++, bp.getProjectFile_AnLian());
			ps.setString(i++, bp.getPlanDate_AnLian());
			ps.setString(i++, bp.getExpertUserId());
			ps.setString(i++, bp.getQualityUserId());
			
			ps.setString(i++, bp.getSignaturePartnerUserId());
			
			ps.setString(i++,bp.getProjectID()) ;
			
			

			ps.executeUpdate();
			ps.close();
			
		} catch (Exception e) {
			System.out.println("sql="+sql);
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
	
	public String getInfoJson(String projectId) throws Exception {
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		ASFuntion CHF = new ASFuntion() ;
		try {

			//插入项目表
			StringBuffer sql = new StringBuffer();
			new DBConnect().changeDataBaseByProjectid(conn, projectId) ;
			sql.append("  SELECT isSpecialProject,c.name,businessCost,b.AuditPara,isNewTakeProject,d.taskName as dName, ")
				.append(" e.taskName as eName,f.taskName as fName ")
				.append(" FROM z_projectbusiness a ")
				.append(" LEFT JOIN z_project b ON a.projectID = b.projectid ")
				.append(" LEFT JOIN k_user c ON a.managerUserId = c.id  ") 
				.append(" left join z_task d on a.projectid = d.projectid and d.property = 'Z1'")
				.append(" left join z_task e on a.projectid = e.projectid and e.property = 'Z2'")
				.append(" left join z_task f on a.projectid = f.projectid and f.property = 'Z3'")
				.append(" where a.projectid=?") ;
				
			ps = conn.prepareStatement(sql.toString());
			ps.setString(1,projectId) ;
			rs = ps.executeQuery() ;
			Map<String,String> map = new HashMap<String,String>();
			
			String isSpecialProject = "" ;
			String managerUser = "" ;
			String businessCost = "" ;
			String auditpara = "" ;
			String isNewTakeProject = "" ;
			String z1="",z2="",z3="" ;
			if(rs.next()) {
				isSpecialProject =  rs.getString(1) ;
				managerUser =  rs.getString(2) ;
				businessCost =  rs.getString(3) ;
				auditpara =  rs.getString(4) ;
				isNewTakeProject =  rs.getString(5) ;
				z1 = CHF.showNull(rs.getString(6));
				z2 = CHF.showNull(rs.getString(7)) ;
				z3 = CHF.showNull(rs.getString(8));
			}
			map.put("isSpecialProject",isSpecialProject) ;
			map.put("managerUser", managerUser) ;
			map.put("businessCost", businessCost) ;
			map.put("auditpara", auditpara) ;
			map.put("isNewTakeProject",isNewTakeProject) ;
			map.put("z1",z1) ;
			map.put("z2",z2) ;
			map.put("z3",z3) ;
			String json = JSONArray.fromObject(map).toString() ;
			return json ;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return "" ;
	}
	//安联修改
	public BusinessProject anLianGet(String autoId)throws Exception{
		BusinessProject bp = null ;
		  PreparedStatement ps = null;
		  ResultSet rs = null;
		try {
			bp = new BusinessProject();
			String sql = "select b.customerId as customerId ,b.customerName as customerName ,b.contractMoney as businessCost from z_projectbusiness a  left join k_customerproject b on b.customerId = a.customerId where autoId = " +autoId;
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				//cp.setAutoId(rs.getString(autoId));
				bp.setCustomerId(rs.getString("customerName"));
				//bp.setCustomerName(rs.getString("customerName"));
				bp.setBusinessCost(rs.getString("businessCost"));
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return bp;
	}
	
	public BusinessProject get(String projectId) throws Exception {
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		BusinessProject bp = null ;
		try {

			StringBuffer sql = new StringBuffer();
			
			sql.append("  select ")
				.append(" projectID,projectname,EntrustCustomerId,customerId,payCustomerId, ")
				.append(" auditPara,typeId,isSpecialProject,isNewTakeProject,isReport, ")
				.append(" customerType,managerUserId,departManagerUserId,partnerUserId,ristPartnerUserId, ")
				.append(" seniorCpaUserId,ristLevel,isStore,signedDate,businessCost, ")
				.append(" property,entrustNumber,reportNumber,projectPartner1,projectPartner2,qualityPartner, ")
				.append(" signedCpa1,signedCpa2,creator,createTime,departmentid, ")
				.append(" companyType,business,contactUser,contactPhone,businesChannel, ")
				.append(" reportRequire,reportDate,outdays,auditpeopleCount,schedulebegin, ")
				.append(" scheduleend,reporttype,sealorSign,remark,filename, ")
				.append(" filetempname,reportCopies,reportPrint,licenseCount,latestReportDate, ")
				.append(" attachDesc,reportFileName,reportFileTempName,printUser,checkMoney, ")
				.append(" bingUser,reportSign,reportUsage,reportUser,isstock, ")
				.append(" registerNum,travelAgree,money,receicemoney,secretFileName, ")
				.append(" secretFileTempName,isNewBusiness,businessResource,continueUser,introduceUser, ")
				.append(" costPromise,businessDesc,travelPromise,finishYear,state, ")
				.append(" isExtactFee,payRate,instalment_AnLian,projectFile_AnLian,planDate_AnLian,expertUserId,qualityUserId,SignaturePartnerUserId ")
				.append(" from asdb.z_projectbusiness ")
				.append(" where projectid=? ");
			
			ps = conn.prepareStatement(sql.toString());
			ps.setString(1,projectId) ;
			rs = ps.executeQuery() ;
			if(rs.next()) {
				int i = 1 ;
				bp = new BusinessProject() ;
				
				bp.setProjectID(rs.getString(i++)) ;
				bp.setProjectName(rs.getString(i++)) ;
				bp.setEntrustCustomerId(rs.getString(i++)) ;
				bp.setCustomerId(rs.getString(i++)) ;
				bp.setPayCustomerId(rs.getString(i++)) ;
				
				bp.setAuditpara(rs.getString(i++)) ;
				bp.setTypeId(rs.getString(i++)) ;
				bp.setIsSpecialProject(rs.getString(i++)) ;
				bp.setIsNewTakeProject(rs.getString(i++)) ;
				bp.setIsReport(rs.getString(i++)) ;
				
				bp.setCustomerType(rs.getString(i++)) ;
				bp.setManagerUserId(rs.getString(i++)) ;
				bp.setDepartManagerUserId(rs.getString(i++)) ;
				bp.setPartnerUserId(rs.getString(i++)) ;
				bp.setRistPartnerUserId(rs.getString(i++)) ;
				
				bp.setSeniorCpaUserId(rs.getString(i++)) ;
				bp.setRistLevel(rs.getString(i++)) ;
				bp.setIsStore(rs.getString(i++)) ;
				bp.setSignedDate(rs.getString(i++)) ;
				bp.setBusinessCost(rs.getString(i++)) ;
				
				bp.setProperty(rs.getString(i++)) ;
				bp.setEntrustNumber(rs.getString(i++)) ;
				bp.setReportNumeber(rs.getString(i++)) ;
				bp.setProjectPartner1(rs.getString(i++)) ;
				bp.setProjectPartner2(rs.getString(i++)) ;
				bp.setQualityPartner(rs.getString(i++)) ;
				
				bp.setSignedCpa1(rs.getString(i++)) ;
				bp.setSignedCpa2(rs.getString(i++)) ;
				bp.setCreator(rs.getString(i++)) ;
				bp.setCreateTime(rs.getString(i++)) ;
				bp.setDepartmentId(rs.getString(i++)) ;
				
				bp.setCompanyType(rs.getString(i++)) ;
				bp.setBusiness(rs.getString(i++)) ;
				bp.setContactUser(rs.getString(i++)) ;
				bp.setContactPhone(rs.getString(i++)) ;
				bp.setBusinesChannel(rs.getString(i++)) ;
				
				bp.setReportRequire(rs.getString(i++)) ;
				bp.setReportDate(rs.getString(i++)) ;
				bp.setOutdays(rs.getString(i++)) ;
				bp.setAuditpeopleCount(rs.getString(i++)) ;
				bp.setScheduleBegin(rs.getString(i++)) ;
				
				bp.setScheduleEnd(rs.getString(i++)) ;
				bp.setReportType(rs.getString(i++)) ;
				bp.setSealOrSign(rs.getString(i++)) ;
				bp.setRemark(rs.getString(i++)) ;
				bp.setFilename(rs.getString(i++)) ;
				
				bp.setFiletempname(rs.getString(i++)) ;
				bp.setReportCopies(rs.getString(i++)) ;
				bp.setReportPrint(rs.getString(i++)) ;
				bp.setLicenseCount(rs.getString(i++)) ;
				bp.setLatestReportDate(rs.getString(i++)) ;
				
				bp.setAttachDesc(rs.getString(i++)) ;
				bp.setReportFileName(rs.getString(i++)) ;
				bp.setReportFileTempName(rs.getString(i++)) ;
				bp.setPrintUser(rs.getString(i++)) ;
				bp.setCheckMoney(rs.getString(i++)) ;
				
				bp.setBingUser(rs.getString(i++)) ;
				bp.setReportSign(rs.getString(i++)) ;
				bp.setReportUsage(rs.getString(i++)) ;
				bp.setReportUser(rs.getString(i++)) ;
				bp.setIsStock(rs.getString(i++)) ;
				
				bp.setRegisterNum(rs.getString(i++)) ;
				bp.setTravelAgree(rs.getString(i++)) ;
				bp.setMoney(rs.getString(i++));
				bp.setReceivemoney(rs.getString(i++));
				bp.setSecretFileName(rs.getString(i++));
				
				bp.setSecretFileTempName(rs.getString(i++));
				bp.setIsNewBusiness(rs.getString(i++));
				bp.setBusinessResource(rs.getString(i++));
				bp.setContinueUser(rs.getString(i++));
				bp.setIntroduceUser(rs.getString(i++));
				
				bp.setCostPromise(rs.getString(i++)) ;
				bp.setBusinessDesc(rs.getString(i++)) ;
				bp.setTravelPromise(rs.getString(i++)) ;
				bp.setFinishYear(rs.getString(i++)) ;
				bp.setState(rs.getString(i++)) ;
				
				bp.setIsExtactFee(rs.getString(i++)) ;
				bp.setPayRate(rs.getString(i++)) ;
				bp.setInstalment_AnLian(rs.getString("instalment_AnLian"));
				bp.setProjectFile_AnLian(rs.getString("projectFile_AnLian"));
				bp.setPlanDate_AnLian(rs.getString("planDate_AnLian"));
				bp.setExpertUserId(rs.getString("expertUserId"));
				bp.setQualityUserId(rs.getString("qualityUserId"));
				bp.setSignaturePartnerUserId(rs.getString("SignaturePartnerUserId"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return bp ;
	}
	
	public BusinessProject getOfName(String projectId) throws Exception {
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		BusinessProject bp = null ;
		try {

			StringBuffer sql = new StringBuffer();
			
			sql.append("  select ")
				.append(" projectID,projectname,b.DepartName,c.DepartName,d.DepartName, ")
				.append(" auditPara,a.typeId,isSpecialProject,isNewTakeProject,isReport, ")
				.append(" customerType,e.name,f.name,g.name,h.name, ")
				.append(" o.name,ristLevel,isStore,signedDate,businessCost, ")
				.append(" a.property,entrustNumber,reportNumber,i.name,j.name,k.name, ")
				.append(" l.name,m.name,creator,createTime,n.departname, ")
				.append(" companyType,business,contactUser,contactPhone,businesChannel, ")
				.append(" reportRequire,reportDate,auditpeopleCount,outdays,schedulebegin, ")
				.append(" scheduleend,reporttype,sealOrSign,a.remark,a.filename, ")
				.append(" a.filetempname,reportCopies,reportPrint,licenseCount,latestReportDate, ")
				.append(" attachDesc,reportFileName,reportFileTempName,printUser,checkMoney, ")
				.append(" bingUser,reportSign,reportUsage,reportUser,u1.name as expertUserId,u2.name qualityUserId,u3.name as SignaturePartnerUserId ")
				
				.append(" from asdb.z_projectbusiness a")
				.append(" left join k_customer b on a.EntrustCustomerId = b.DepartID ")
				.append(" left join k_customer c on a.customerId = c.DepartID ")
				.append(" left join k_customer d on a.payCustomerId = d.DepartID ")
				.append(" left join k_user e on a.managerUserId = e.id ")
				.append(" left join k_user f on a.departManagerUserId = f.id ")
				.append(" left join k_user g on a.partnerUserId = g.id ")
				.append(" left join k_user h on a.ristPartnerUserId = h.id ")
				.append(" left join k_user i on a.projectPartner1 = i.id ")
				.append(" left join k_user j on a.projectPartner2 = j.id ")
				.append(" left join k_user k on a.qualityPartner = k.id ")
				.append(" left join k_user l on a.signedCpa1 = l.id ")
				.append(" left join k_user m on a.signedCpa2 = m.id ")
				.append(" left join k_user o on a.seniorCpaUserId = o.id ")
				.append(" left join k_department n on a.departmentid = n.autoid ")
				.append(" left join k_user u1 on a.expertUserId = u1.id ")
				.append(" left join k_user u2 on a.qualityUserId = u2.id ")
				.append(" left join k_user u3 on a.SignaturePartnerUserId = u3.id ")
				.append(" where projectid=? ");
			
			ps = conn.prepareStatement(sql.toString());
			ps.setString(1,projectId) ;
			rs = ps.executeQuery() ;
			if(rs.next()) {
				int i = 1 ;
				bp = new BusinessProject() ;
				
				bp.setProjectID(rs.getString(i++)) ;
				bp.setProjectName(rs.getString(i++)) ;
				bp.setEntrustCustomerId(rs.getString(i++)) ;
				bp.setCustomerId(rs.getString(i++)) ;
				bp.setPayCustomerId(rs.getString(i++)) ;
				
				bp.setAuditpara(rs.getString(i++)) ;
				bp.setTypeId(rs.getString(i++)) ;
				bp.setIsSpecialProject(rs.getString(i++)) ;
				bp.setIsNewTakeProject(rs.getString(i++)) ;
				bp.setIsReport(rs.getString(i++)) ;
				
				bp.setCustomerType(rs.getString(i++)) ;
				bp.setManagerUserId(rs.getString(i++)) ;
				bp.setDepartManagerUserId(rs.getString(i++)) ;
				bp.setPartnerUserId(rs.getString(i++)) ;
				bp.setRistPartnerUserId(rs.getString(i++)) ;
				
				bp.setSeniorCpaUserId(rs.getString(i++)) ;
				bp.setRistLevel(rs.getString(i++)) ;
				bp.setIsStore(rs.getString(i++)) ;
				bp.setSignedDate(rs.getString(i++)) ;
				bp.setBusinessCost(rs.getString(i++)) ;
				
				bp.setProperty(rs.getString(i++)) ;
				bp.setEntrustNumber(rs.getString(i++)) ;
				bp.setReportNumeber(rs.getString(i++)) ;
				bp.setProjectPartner1(rs.getString(i++)) ;
				bp.setProjectPartner2(rs.getString(i++)) ;
				bp.setQualityPartner(rs.getString(i++)) ;
				
				bp.setSignedCpa1(rs.getString(i++)) ;
				bp.setSignedCpa2(rs.getString(i++)) ;
				bp.setCreator(rs.getString(i++)) ;
				bp.setCreateTime(rs.getString(i++)) ;
				bp.setDepartmentId(rs.getString(i++)) ;
				
				bp.setCompanyType(rs.getString(i++)) ;
				bp.setBusiness(rs.getString(i++)) ;
				bp.setContactUser(rs.getString(i++)) ;
				bp.setContactPhone(rs.getString(i++)) ;
				bp.setBusinesChannel(rs.getString(i++)) ;
				
				bp.setReportRequire(rs.getString(i++)) ;
				bp.setReportDate(rs.getString(i++)) ;
				bp.setAuditpeopleCount(rs.getString(i++)) ;
				bp.setOutdays(rs.getString(i++)) ;
				bp.setScheduleBegin(rs.getString(i++)) ;
				
				bp.setScheduleEnd(rs.getString(i++)) ;
				bp.setReportType(rs.getString(i++)) ;
				bp.setSealOrSign(rs.getString(i++)) ;
				bp.setRemark(rs.getString(i++)) ;
				bp.setFilename(rs.getString(i++)) ;
				
				bp.setFiletempname(rs.getString(i++)) ;
				bp.setReportCopies(rs.getString(i++)) ;
				bp.setReportPrint(rs.getString(i++)) ;
				bp.setLicenseCount(rs.getString(i++)) ;
				bp.setLatestReportDate(rs.getString(i++)) ;
				
				bp.setAttachDesc(rs.getString(i++)) ;
				bp.setReportFileName(rs.getString(i++)) ;
				bp.setReportFileTempName(rs.getString(i++)) ;
				bp.setPrintUser(rs.getString(i++)) ;
				bp.setCheckMoney(rs.getString(i++)) ;
				
				bp.setBingUser(rs.getString(i++)) ;
				bp.setReportSign(rs.getString(i++)) ;
				bp.setReportUsage(rs.getString(i++)) ;
				bp.setReportUser(rs.getString(i++)) ;
				bp.setExpertUserId(rs.getString("expertUserId"));
				bp.setQualityUserId(rs.getString("qualityUserId"));
				bp.setSignaturePartnerUserId("SignaturePartnerUserId");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return bp ;
	}
	
	
	public boolean isSpecialProject(String projectId) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {

			//插入项目表
			StringBuffer sql = new StringBuffer();
			
			sql.append("  select 1")
				.append(" from z_projectbusiness ")
				.append(" where projectid=?")
				.append(" and isSpecialProject = '是' ") ;
			
			ps = conn.prepareStatement(sql.toString());
			ps.setString(1,projectId) ;
			rs = ps.executeQuery() ;
			if(rs.next()) {
				return true ;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return false ;
	}
	
	public void updateNumber(String projectId,String WTBH,String BGWH) {
		PreparedStatement ps = null;
		try {

			StringBuffer sql = new StringBuffer();
			sql.append("  update z_projectbusiness")
				.append(" set entrustNumber=?,reportNumber=? ")
				.append(" where projectid=? and (entrustNumber is null or entrustNumber='') and (reportNumber is null or reportNumber='') ") ;
			
			ps = conn.prepareStatement(sql.toString());
			ps.setString(1, WTBH) ;
			ps.setString(2, BGWH) ;
			ps.setString(3,projectId) ;
			ps.execute() ;
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
	
	
	public void updateReportFile(String projectId,String reportFileName,String reportFileTempName) {
		PreparedStatement ps = null;
		try {

			StringBuffer sql = new StringBuffer();
			sql.append("  update z_projectbusiness")
				.append(" set reportFileName=?,reportFileTempName=? ")
				.append(" where projectid=?") ;
			
			ps = conn.prepareStatement(sql.toString());
			ps.setString(1, reportFileName) ;
			ps.setString(2, reportFileTempName) ;
			ps.setString(3,projectId) ;
			ps.execute() ;
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
	
	
	public void updatePartner(String projectId,String projectPartner1,String projectPartner2,
			String qualityPartner,String signedCpa1,String signedCpa2) {
		PreparedStatement ps = null;
		try {

			StringBuffer sql = new StringBuffer();
			sql.append("  update z_projectbusiness")
				.append(" set projectPartner1=?,projectPartner2=?,qualityPartner=?,signedCpa1=?,signedCpa2=? ")
				.append(" where projectid=?") ;
			
			ps = conn.prepareStatement(sql.toString());
			ps.setString(1, projectPartner1) ;
			ps.setString(2, projectPartner2) ;
			ps.setString(3, qualityPartner) ;
			ps.setString(4, signedCpa1) ;
			ps.setString(5, signedCpa2) ;
			ps.setString(6,projectId) ;
			ps.execute() ;
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
	
	public void updateReport(BusinessProject bp) {
		PreparedStatement ps = null;
		try {

			StringBuffer sql = new StringBuffer();
			sql.append("  update z_projectbusiness")
				.append(" set outdays=?,auditpeopleCount=?,schedulebegin=?,scheduleend=?,reporttype=?, ")
				.append(" sealorSign=?,remark=?,filename=?,filetempname=?,reportCopies=?, ")
				.append(" reportPrint=?,licenseCount=?,latestReportDate=?,attachDesc=?,reportUser=?, ")
				.append(" printUser=?,checkMoney=?,bingUser=?,reportSign=?,reportUsage=?, ")
				.append(" signedCpa1=?,signedCpa2=? ")     
				.append(" where projectid=?") ;
			
			ps = conn.prepareStatement(sql.toString());
			int i = 1 ;
			ps.setString(i++, bp.getOutdays()) ;
			ps.setString(i++, bp.getAuditpeopleCount()) ;
			ps.setString(i++, bp.getScheduleBegin()) ;
			ps.setString(i++, bp.getScheduleEnd()) ;
			ps.setString(i++, bp.getReportType()) ;
			
			ps.setString(i++,bp.getSealOrSign()) ;
			ps.setString(i++,bp.getRemark()) ;
			ps.setString(i++,bp.getFilename()) ;
			ps.setString(i++,bp.getFiletempname()) ;
			ps.setString(i++,bp.getReportCopies()) ;
			
			ps.setString(i++,bp.getReportPrint()) ;
			ps.setString(i++,bp.getLicenseCount()) ;
			ps.setString(i++,bp.getLatestReportDate()) ;
			ps.setString(i++,bp.getAttachDesc()) ;
			ps.setString(i++,bp.getReportUser()) ;
			
			ps.setString(i++,bp.getPrintUser()) ;
			ps.setString(i++,bp.getCheckMoney()) ;
			ps.setString(i++,bp.getBingUser()) ;
			ps.setString(i++,bp.getReportSign()) ;
			ps.setString(i++,bp.getReportUsage()) ;
			
			ps.setString(i++,bp.getSignedCpa1()) ;
			ps.setString(i++,bp.getSignedCpa2()) ;
			
			ps.setString(i++,bp.getProjectID()) ;
			
			ps.execute() ;
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
	
	public List getProjects(String projectIds) {
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		List bpList = new ArrayList() ;
		ASFuntion CHF = new ASFuntion() ;
		try {
			
			
			String[] projectArr = projectIds.split(",") ;
			
			for(int i=0;i<projectArr.length;i++) {
				StringBuffer sql = new StringBuffer();
				new DBConnect().changeDataBaseByProjectid(conn, projectArr[i]) ;
				sql.append("  SELECT isSpecialProject,c.name,businessCost,a.auditpara,isNewTakeProject,a.projectName,d.taskName as dName, ")
				.append(" e.taskName as eName,f.taskName as fName,a.projectid ")
				.append(" FROM z_projectbusiness a ")
				.append(" LEFT JOIN k_user c ON a.managerUserId = c.id  ") 
				.append(" left join z_task d on a.projectid = d.projectid and d.property = 'Z1'")
				.append(" left join z_task e on a.projectid = e.projectid and e.property = 'Z2'")
				.append(" left join z_task f on a.projectid = f.projectid and f.property = 'Z3'")
				.append(" where a.projectid ="+projectArr[i]) ;
				
				ps = conn.prepareStatement(sql.toString());
				rs = ps.executeQuery() ;
				
				String isSpecialProject = "" ;
				String managerUser = "" ;
				String businessCost = "" ;
				String auditpara = "" ;
				String isNewTakeProject = "" ;
				String projectName = "" ;
				String z1="",z2="",z3="" ;
				String projectid = "" ;
				if(rs.next()) {
					Map<String,String> map = new HashMap<String,String>();
					isSpecialProject =  rs.getString(1) ;
					managerUser =  rs.getString(2) ;
					businessCost =  rs.getString(3) ;
					auditpara =  rs.getString(4) ;
					isNewTakeProject =  rs.getString(5) ;
					projectName = rs.getString(6) ;
					z1 = CHF.showNull(rs.getString(7));
					z2 = CHF.showNull(rs.getString(8)) ;
					z3 = CHF.showNull(rs.getString(9));
					projectid = CHF.showNull(rs.getString(10));
					
					map.put("isSpecialProject",isSpecialProject) ;
					map.put("managerUser", managerUser) ;
					map.put("businessCost", businessCost) ;
					map.put("auditpara", auditpara) ;
					map.put("isNewTakeProject",isNewTakeProject) ;
					map.put("projectName", projectName) ;
					map.put("z1",z1) ;
					map.put("z2",z2) ;
					map.put("z3",z3) ;
					map.put("projectid",projectid) ;
					bpList.add(map) ;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return bpList ;
	}
	
	public void del(String projectId) {
		PreparedStatement ps = null;
		try {

			StringBuffer sql = new StringBuffer();
			sql.append("  delete from z_projectbusiness")
				.append(" where projectid=?") ;
			
			ps = conn.prepareStatement(sql.toString());
			ps.setString(1, projectId) ;
			ps.execute() ;
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
	
	

	/**
	 * 根据客户编号得到客户信息
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	
	public String getCustomerInfoJson(String customerId) throws Exception {

		PreparedStatement ps = null;
		ResultSet rs = null;
		ASFuntion CHF = new ASFuntion();
		try {

			String sql = "select linkman,phone,enterprisecode from k_customer  where departId = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, customerId);
			
			rs = ps.executeQuery();
			Map<String, String> map = new HashMap<String, String>();

			//所属部门
			String depart = "";
			//客户联系人
			String linkman = "";
			//联系电话
			String phone = "";
			//企业性质     NO
			String enterprisecode = "";
			
			if (rs.next()) {
				linkman = rs.getString("linkman");
				phone = rs.getString("phone");
				enterprisecode = rs.getString("enterprisecode");
			}
			map.put("linkman", CHF.showNull(linkman));
			map.put("phone", CHF.showNull(phone));
			map.put("enterprisecode", CHF.showNull(enterprisecode));
			String json = JSONArray.fromObject(map).toString();
			return json;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return "";
	}
	
	/**
	 * 修改已开票金额
	 * @param projectid
	 * @param money
	 */
	public void updateMoneyByProjectId(String projectId,String cdate){
		PreparedStatement ps = null;
		try {

			StringBuffer sql = new StringBuffer();
			sql.append("  update z_projectbusiness set money = (select sum(money) from k_invoice where projectid=?),lastkaipiaodate=?  where projectid=?");
			ps = conn.prepareStatement(sql.toString());
		
			ps.setString(1, projectId) ;
			ps.setString(2, cdate) ;
			ps.setString(3, projectId) ;
			ps.execute() ;
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 修改已开票金额
	 * @param projectid
	 * @param money
	 */
	public void updateMoneyByRegisterNum(String projectid,String registernum,String cdate){
		PreparedStatement ps = null;
		try {

			StringBuffer sql = new StringBuffer();
			sql.append("  update z_projectbusiness set money = (select sum(money) from k_invoice where projectid=?),lastkaipiaodate=?  where registernum=?");
			ps = conn.prepareStatement(sql.toString());
		
			ps.setString(1, projectid) ;
			ps.setString(2, cdate) ;
			ps.setString(3, registernum) ;
			ps.execute() ;
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 修改已收款金额
	 * @param projectid
	 * @param money
	 */
	public void updateReceiveMoneyByProjectId(String projectId,String receicedate){
		PreparedStatement ps = null;
		try {

			StringBuffer sql = new StringBuffer();
			sql.append("  update z_projectbusiness set receicemoney = (select sum(receicemoney) from k_getFunds where projectid=?),lastshoukuandate=? where projectid=?");
			ps = conn.prepareStatement(sql.toString());
			ps.setString(1, projectId) ;
			ps.setString(2, receicedate) ;
			ps.setString(3, projectId) ;
			ps.execute() ;
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
	
	public void updateBusinessByRegistNum(String fieldname,String value,String registerNum){
		PreparedStatement ps = null;
		try {

			StringBuffer sql = new StringBuffer();
			sql.append("  update z_projectbusiness set "+fieldname+" = ? where registerNum=?");
			ps = conn.prepareStatement(sql.toString());
			ps.setString(1, value) ;
			ps.setString(2, registerNum) ;
			ps.execute() ;
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 修改已收款金额
	 * @param projectid
	 * @param money
	 */
	public void updateReceiveMoneyByRegistNum(String projectId,String registerNum,String receicedate){
		PreparedStatement ps = null;
		try {

			StringBuffer sql = new StringBuffer();
			sql.append("  update z_projectbusiness set receicemoney = (select sum(receicemoney) from k_getFunds where projectid=?),lastshoukuandate=? where registerNum=?");
			ps = conn.prepareStatement(sql.toString());
			ps.setString(1, projectId) ;
			ps.setString(2, receicedate) ;
			ps.setString(3, registerNum) ;
			ps.execute() ;
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 修改项目状态
	 * @param projectid
	 * @param money
	 */
	public void updatePropertyByProjectId(String projectId,String property){
		PreparedStatement ps = null;
		try {

			StringBuffer sql = new StringBuffer();
			sql.append(" update z_projectbusiness set property = ? where projectId=? ");
			ps = conn.prepareStatement(sql.toString());
			ps.setString(1, property) ;
			ps.setString(2, projectId) ;
			ps.execute() ;
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 修改时间
	 * @param projectid
	 * @param money
	 */
	public void updateFillDateByProjectId(String projectId,String fillDate){
		PreparedStatement ps = null;
		try {

			StringBuffer sql = new StringBuffer();
			sql.append(" update z_projectbusiness set fillDate = ? where projectId=? ");
			ps = conn.prepareStatement(sql.toString());
			ps.setString(1, fillDate) ;
			ps.setString(2, projectId) ;
			ps.execute() ;
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
	

	/**
	 * 修改状态
	 * @param projectid
	 * @param money
	 */
	public void updateState(String projectId,String state,String remark){
		PreparedStatement ps = null;
		try {
			
			String sql = " update z_projectbusiness set state = ?,isUpdated='Y',remark=? where projectId=? ";
			
			ps = conn.prepareStatement(sql.toString());
			ps.setString(1, state) ;
			ps.setString(2, remark) ;
			ps.setString(3, projectId) ;
			
			ps.execute() ;
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
	
	
	/**
	 * 根据客户编号得到客户信息
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	
	public String getCustomerInfo(String departId) throws Exception {

		PreparedStatement ps = null;
		ResultSet rs = null;
		ASFuntion CHF = new ASFuntion();
		try {

			List list = new ArrayList();
			Map<String, String> map = new HashMap<String, String>();
			
			String sql = " select c.departId,c.departName,g.groupid,g.groupname "
					   + " from k_customer c left join k_group g "
					   + " on c.groupname = g.groupid where departId = ? order by departId ";
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, departId);
			
			rs = ps.executeQuery();

			ResultSetMetaData rmd = rs.getMetaData();
			
			while (rs.next()) {
				map = new HashMap<String, String>();
				for (int j = 1; j <= rmd.getColumnCount(); j++) {
					map.put(rmd.getColumnName(j).toLowerCase(),CHF.showNull(rs.getString(rmd.getColumnName(j))));
				}
				
				list.add(map);
			}
			
			String json = JSONArray.fromObject(list).toString();
			
			return json;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return "";
	}
	
	
	// 登记流水号
	public String createRegisterNum() throws Exception {
		
		ASFuntion af = new ASFuntion();
		PreparedStatement ps = null;
		ResultSet rs = null;
		String year = af.getCurrentDate().substring(0,4);
		// 当前年的 第一条
		String registerNum = year+"000001";
		try {

			String sql = " select max(registerNum) as registerNum from z_projectBusiness ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				String result = af.showNull(rs.getString("registerNum"));
				if(result.length()>4){
					if(year.equals(result.substring(0,4))){
						registerNum = (Integer.parseInt(result)+1)+"";
					}
				}
			}
			
			return registerNum ;
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return "" ;
	}
		
	
	/**
	 * 委托号
	 * @param ywlx  业务类型
	 * @param userid  当前人
	 * @return
	 * @throws Exception
	 */
	public String createEntrustnumber(String ywlx,String userid) throws Exception {
		
		ASFuntion af = new ASFuntion();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		String year = af.getCurrentDate().substring(0,4);
		
		String Entrustnumber = "";
		
		// 当前年的 第一条
		if("".equals(ywlx) || null == ywlx){
			Entrustnumber = userid+"-"+year+"000001";	
		}else{
			Entrustnumber = ywlx+"-"+userid+"-"+year+"000001";
		}
		
		try {

			String sql = " select max(entrustnumber) as entrustnumber from z_projectBusiness where entrustnumber like '"+ywlx+"-"+userid+"-%' ";
			if("".equals(ywlx) || null == ywlx){
				sql = " select max(entrustnumber) as entrustnumber from z_projectBusiness where entrustnumber like '"+userid+"-%' ";
			}
			
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				String result = af.showNull(rs.getString("entrustnumber"));
				if(result.length()>4){
					if("".equals(ywlx) || null == ywlx){
						if("".equals(userid) || null == userid){
							Entrustnumber = (Integer.parseInt(result.substring(result.lastIndexOf("-")+1))+1) + "";
						}else{
							Entrustnumber = userid+"-"+(Integer.parseInt(result.substring(result.lastIndexOf("-")+1))+1);
						}
					}else{
						if("".equals(userid) || null == userid){
							Entrustnumber = ywlx+"-"+(Integer.parseInt(result.substring(result.lastIndexOf("-")+1))+1);
						}else{
							Entrustnumber = ywlx+"-"+userid+"-"+(Integer.parseInt(result.substring(result.lastIndexOf("-")+1))+1);
						}
					}
				}
			}
			
			return Entrustnumber ;
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return "" ;
	}
	
	
	
	/**
	 * 得到项目编号 集合
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	
	public List getProjectIdByRegisterNum(String registerNum) throws Exception {

		PreparedStatement ps = null;
		ResultSet rs = null;
		List list = new ArrayList();;
		ASFuntion CHF = new ASFuntion();
		try {

			String sql = " select projectid from z_project where registerNum = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, registerNum);
			
			rs = ps.executeQuery();

			while (rs.next()) {
				Map map = new HashMap();
				map.put("projectid", rs.getString("projectid"));
				list.add(map);
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
	 * 得到附件
	 * @param mime
	 * @return
	 * @throws Exception
	 */
	public List getAttach(String mime) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Attach> list = new ArrayList<Attach>();
		try {
			String sql = "select * from asdb.k_attach where mime='"+mime+"'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){

				Attach attach = new Attach();
				attach.setUnid(rs.getString("unid"));
				attach.setTypeId(rs.getString("typeId"));
				attach.setTitle(rs.getString("title"));
				attach.setContent(rs.getString("content"));
				attach.setUdate(rs.getString("udate"));

				attach.setLastDate(rs.getString("lastDate"));
				attach.setLastPerson(rs.getString("lastPerson"));
				attach.setOrderId(rs.getString("orderId"));
				attach.setViewCount(rs.getInt("viewCount"));
				attach.setFilename(rs.getString("filename"));

				attach.setEdate(rs.getString("edate"));
				attach.setMime(rs.getString("mime"));
				attach.setDepartid(rs.getString("departid"));
				attach.setProperty(rs.getString("property"));
				attach.setProjectid(rs.getString("projectid"));
				attach.setReleasedate(rs.getString("releasedate"));
				
				list.add(attach);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 根据,号分隔的项目编号获得所有项目
	 * @param groupProjectId
	 * @return
	 * @throws Exception
	 */
	public Map<String,String> getProjectByProjectId(String projectId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		Map<String,String> map = new HashMap<String,String>() ;
		ASFuntion CHF = new ASFuntion() ;
		try {
			
			String sql = " SELECT a.projectid,c.groupname,b.departname,a.auditpara,d.departname AS departmentname,\n" 
					   + " o.s21 AS isReport,o.otherproject,o.price,o.travelAgree,CONCAT(a.auditTimeBegin,' - ',a.audittimeend) AS auditarea,\n"
					   + " o.s1 AS special,GROUP_CONCAT(distinct e2.name) AS projectPartner,GROUP_CONCAT(distinct f2.name) AS teamManager,\n"
					   + " GROUP_CONCAT(distinct g2.name) AS projectManager,GROUP_CONCAT(distinct h2.name) AS departManager,\n"
					   + " GROUP_CONCAT(distinct i2.name) AS signedCpa1,GROUP_CONCAT(distinct j2.name) AS signedCpa2,\n"
					   + " GROUP_CONCAT(distinct k2.name) AS signedCpa3,GROUP_CONCAT(distinct l2.name) AS auditPartner,\n"
					   + " GROUP_CONCAT(distinct m2.name) AS consortPartner \n"
					   + " FROM z_project a \n"
					   + " LEFT JOIN k_customer b ON a.customerid = b.departid \n"
					   + " LEFT JOIN k_group c ON b.groupname = c.groupid \n"
					   + " LEFT JOIN k_department d ON a.departmentid = d.autoid \n"
					   + " LEFT JOIN z_projectext o ON a.projectid = o.projectid \n"
					   + " LEFT JOIN z_auditpeople e1 ON a.projectid = e1.projectid AND e1.role = '项目负责合伙人' \n"
					   + " LEFT JOIN k_user e2 ON e1.userid = e2.id \n"
					   + " LEFT JOIN z_auditpeople f1 ON a.projectid = f1.projectid AND f1.role = '小组负责人' \n"
					   + " LEFT JOIN k_user f2 ON f1.userid = f2.id \n"
					   + " LEFT JOIN z_auditpeople g1 ON a.projectid = g1.projectid AND g1.role = '项目负责人' \n"
					   + " LEFT JOIN k_user g2 ON g1.userid = g2.id \n"
					   + " LEFT JOIN z_auditpeople h1 ON a.projectid = h1.projectid AND h1.role = '部门一审' \n"
					   + " LEFT JOIN k_user h2 ON h1.userid = h2.id \n"
					   + " LEFT JOIN z_auditpeople i1 ON a.projectid = i1.projectid AND i1.role = '签字会计师1' \n"
					   + " LEFT JOIN k_user i2 ON i1.userid = i2.id \n"
					   + " LEFT JOIN z_auditpeople j1 ON a.projectid = j1.projectid AND j1.role = '签字会计师2' \n"
					   + " LEFT JOIN k_user j2 ON j1.userid = j2.id  \n"
					   + " LEFT JOIN z_auditpeople k1 ON a.projectid = k1.projectid AND k1.role = '签字会计师3' \n"
					   + " LEFT JOIN k_user k2 ON k1.userid = k2.id \n"
					   + " LEFT JOIN z_auditpeople l1 ON a.projectid = l1.projectid AND l1.role = '复核合伙人' \n"
					   + " LEFT JOIN k_user l2 ON l1.userid = l2.id \n"
					   + " LEFT JOIN z_auditpeople m1 ON a.projectid = m1.projectid AND m1.role = '协调合伙人' \n"
					   + " LEFT JOIN k_user m2 ON m1.userid = m2.id \n"
					   + " WHERE 1=1 and a.projectid = ?\n"
					   + " GROUP BY a.projectid \n" ;
			
			
			ps = conn.prepareStatement(sql);
			ps.setString(1,projectId) ;
			rs = ps.executeQuery();
			
			if(rs.next()) {
				map.put("projectId",projectId) ;
				map.put("groupname",CHF.showNull(rs.getString(2))) ;
				map.put("departname",CHF.showNull(rs.getString(3))) ;
				map.put("auditpara",CHF.showNull(rs.getString(4))) ;
				map.put("departmentname",CHF.showNull(rs.getString(5))) ;
				
				map.put("isReport",CHF.showNull(rs.getString(6))) ;
				map.put("otherProject",CHF.showNull(rs.getString(7))) ;
				map.put("price",CHF.showNull(rs.getString(8))) ;
				map.put("travelAgree",CHF.showNull(rs.getString(9))) ;
				map.put("auditarea",CHF.showNull(rs.getString(10))) ;
				
				map.put("special",CHF.showNull(rs.getString(11))) ;
				map.put("projectPartner",CHF.showNull(rs.getString(12))) ;
				map.put("teamManager",CHF.showNull(rs.getString(13))) ;
				
				map.put("projectManager",CHF.showNull(rs.getString(14))) ;
				map.put("departManager",CHF.showNull(rs.getString(15))) ;
				
				map.put("signedCpa1",CHF.showNull(rs.getString(16))) ;
				map.put("signedCpa2",CHF.showNull(rs.getString(17))) ;
				
				map.put("signedCpa3",CHF.showNull(rs.getString(18))) ;
				map.put("auditPartner",CHF.showNull(rs.getString(19))) ;
				
				map.put("consortPartner",CHF.showNull(rs.getString(20))) ;
			
			}		
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return map;
	}
	
	/**
	 * 根据业务流水号获得所有项目
	 * @param groupProjectId
	 * @return
	 * @throws Exception
	 */
	public List<Map<String,String>> getProjectsByRegisterNum(String registerNum) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;   
		
		ASFuntion CHF = new ASFuntion() ;
		List<Map<String,String>> projectList = new ArrayList<Map<String,String>>() ;
		try {
			
			String sql = " SELECT a.projectid,c.groupname,b.departname,a.auditpara,d.departname AS departmentname,\n" 
					   + " o.s21 AS isReport,o.otherproject,o.price,o.travelAgree,CONCAT(a.auditTimeBegin,' - ',a.audittimeend) AS auditarea,\n"
					   + " o.s1 AS special,GROUP_CONCAT(distinct e2.name) AS projectPartner,GROUP_CONCAT(distinct f2.name) AS teamManager,\n"
					   + " GROUP_CONCAT(distinct g2.name) AS projectManager,GROUP_CONCAT(distinct h2.name) AS departManager,\n"
					   + " GROUP_CONCAT(distinct i2.name) AS signedCpa1,GROUP_CONCAT(distinct j2.name) AS signedCpa2,\n"
					   + " GROUP_CONCAT(distinct k2.name) AS signedCpa3,GROUP_CONCAT(distinct l2.name) AS auditPartner,\n"
					   + " GROUP_CONCAT(distinct m2.name) AS consortPartner \n"
					   + " FROM z_project a \n"
					   + " LEFT JOIN k_customer b ON a.customerid = b.departid \n"
					   + " LEFT JOIN k_group c ON b.groupname = c.groupid \n"
					   + " LEFT JOIN k_department d ON a.departmentid = d.autoid \n"
					   + " LEFT JOIN z_projectext o ON a.projectid = o.projectid \n"
					   + " LEFT JOIN z_auditpeople e1 ON a.projectid = e1.projectid AND e1.role = '项目负责合伙人' \n"
					   + " LEFT JOIN k_user e2 ON e1.userid = e2.id \n"
					   + " LEFT JOIN z_auditpeople f1 ON a.projectid = f1.projectid AND f1.role = '小组负责人' \n"
					   + " LEFT JOIN k_user f2 ON f1.userid = f2.id \n"
					   + " LEFT JOIN z_auditpeople g1 ON a.projectid = g1.projectid AND g1.role = '项目负责人' \n"
					   + " LEFT JOIN k_user g2 ON g1.userid = g2.id \n"
					   + " LEFT JOIN z_auditpeople h1 ON a.projectid = h1.projectid AND h1.role = '部门一审' \n"
					   + " LEFT JOIN k_user h2 ON h1.userid = h2.id \n"
					   + " LEFT JOIN z_auditpeople i1 ON a.projectid = i1.projectid AND i1.role = '签字会计师1' \n"
					   + " LEFT JOIN k_user i2 ON i1.userid = i2.id \n"
					   + " LEFT JOIN z_auditpeople j1 ON a.projectid = j1.projectid AND j1.role = '签字会计师2' \n"
					   + " LEFT JOIN k_user j2 ON j1.userid = j2.id  \n"
					   + " LEFT JOIN z_auditpeople k1 ON a.projectid = k1.projectid AND k1.role = '签字会计师3' \n"
					   + " LEFT JOIN k_user k2 ON k1.userid = k2.id \n"
					   + " LEFT JOIN z_auditpeople l1 ON a.projectid = l1.projectid AND l1.role = '复核合伙人' \n"
					   + " LEFT JOIN k_user l2 ON l1.userid = l2.id \n"
					   + " LEFT JOIN z_auditpeople m1 ON a.projectid = m1.projectid AND m1.role = '协调合伙人' \n"
					   + " LEFT JOIN k_user m2 ON m1.userid = m2.id \n"
					   + " WHERE 1=1 and a.registerNum = ?\n"
					   + " GROUP BY a.projectid \n" ;
			
			
			ps = conn.prepareStatement(sql);
			ps.setString(1,registerNum) ;
			rs = ps.executeQuery();
			
			while(rs.next()) {
				Map<String,String> map = new HashMap<String,String>() ;
				map.put("projectId",CHF.showNull(rs.getString(1))) ;
				map.put("groupname",rs.getString(2)) ;
				map.put("departname",CHF.showNull(rs.getString(3))) ;
				map.put("auditpara",rs.getString(4)) ;
				map.put("departmentname",CHF.showNull(rs.getString(5))) ;
				
				map.put("isReport",CHF.showNull(rs.getString(6))) ;
				map.put("otherProject",CHF.showNull(rs.getString(7))) ;
				map.put("price",CHF.showNull(rs.getString(8))) ;
				map.put("travelAgree",CHF.showNull(rs.getString(9))) ;
				map.put("auditarea",CHF.showNull(rs.getString(10))) ;
				
				map.put("special",CHF.showNull(rs.getString(11))) ;
				map.put("projectPartner",CHF.showNull(rs.getString(12))) ;
				map.put("teamManager",CHF.showNull(rs.getString(13))) ;
				
				map.put("projectManager",CHF.showNull(rs.getString(14))) ;
				map.put("departManager",CHF.showNull(rs.getString(15))) ;
				
				map.put("signedCpa1",CHF.showNull(rs.getString(16))) ;
				map.put("signedCpa2",CHF.showNull(rs.getString(17))) ;
				
				map.put("signedCpa3",CHF.showNull(rs.getString(18))) ;
				map.put("auditPartner",CHF.showNull(rs.getString(19))) ;
				
				map.put("consortPartner",CHF.showNull(rs.getString(20))) ;
				projectList.add(map) ;
			}		
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return projectList;
	}
	
	
	/**
	 * 根据业务流水号获得主合同和副合同信息
	 * @param groupProjectId
	 * @return
	 * @throws Exception
	 */
	public List<Map<String,String>> getBusinessByRegisterNum(String registerNum) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		ASFuntion CHF = new ASFuntion() ;
		List<Map<String,String>> projectList = new ArrayList<Map<String,String>>() ;
		try {
			
			String sql = " SELECT registerNum,entrustNumber,businesscost,\n" 
					   + " c.departname,signedDate,\n"
					   + " IFNULL(money,0) AS money,\n"
					   + " IFNULL((a.businessCost - a.money),0) AS wkmoney,\n"
					   + " IFNULL(receicemoney,0) AS receicemoney,\n"
					   + " IFNULL(SUM(b.disMoney),0) AS disMoney \n"
					   + " FROM z_projectbusiness a  \n"
					   + " LEFT JOIN z_projectbusinessassign b ON a.registerNum = b.entrustNum \n"
					   + " AND b.ctype = '划转收入' \n"
					   + " LEFT JOIN k_customer c ON a.EntrustCustomerId = c.departid \n"
					   + " WHERE (a.registerNum = ? OR a.parentRegisterNum = ?) \n"
					   + " GROUP BY a.registerNum \n" ;
			
			
			ps = conn.prepareStatement(sql);
			ps.setString(1,registerNum) ;
			ps.setString(2,registerNum) ;
			rs = ps.executeQuery();
			
			while(rs.next()) {
				Map<String,String> map = new HashMap<String,String>() ;
				map.put("registerNum",CHF.showNull(rs.getString(1))) ;
				map.put("entrustNumber",rs.getString(2)) ;
				map.put("businesscost",CHF.showNull(rs.getString(3))) ;
				map.put("departname",rs.getString(4)) ;
				map.put("signedDate",CHF.showNull(rs.getString(5))) ;
				
				map.put("money",CHF.showNull(rs.getString(6))) ;
				map.put("wkmoney",CHF.showNull(rs.getString(7))) ;
				map.put("receicemoney",CHF.showNull(rs.getString(8))) ;
				map.put("disMoney",CHF.showNull(rs.getString(9))) ;
			
				projectList.add(map) ;
			}		
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return projectList;
	}
	
	/**
	 * 根据业务流水号获得分配信息
	 * @param groupProjectId
	 * @return
	 * @throws Exception
	 */
	public List<Map<String,String>> getAssignByRegisterNum(String registerNum,String ctype) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		ASFuntion CHF = new ASFuntion() ;
		List<Map<String,String>> AssignList = new ArrayList<Map<String,String>>() ;
		try {
			
			
			String sql = " SELECT ssassignType,ssassignUser,disMoney,disDate \n"
					   + " FROM z_projectbusinessassign \n"
					   + " WHERE entrustNum = ? AND ctype = ? \n"
					   + " ORDER BY orderid \n" ;
			
			ps = conn.prepareStatement(sql);
			ps.setString(1,registerNum) ;
			ps.setString(2,ctype) ;
			rs = ps.executeQuery();
			
			while(rs.next()) {
				Map<String,String> map = new HashMap<String,String>() ;
				map.put("ssassignType",CHF.showNull(rs.getString(1))) ;
				map.put("ssassignUser",rs.getString(2)) ;
				map.put("disMoney",CHF.showNull(rs.getString(3))) ;
				map.put("disDate",rs.getString(4)) ;
			
				AssignList.add(map) ;
			}		
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return AssignList;
	}
	
	
	/**
	 * 清除项目与业务约定书的关联
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	
	public void delRelateByRegisterNum(String registerNum) throws Exception {

		PreparedStatement ps = null;
		try {

			String sql = " update z_project set registerNum = '' where registerNum = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, registerNum);
			ps.execute() ;
			
			sql = " update z_projectext set registerNum = '',s21='',otherProject='',travelAgree='',price=0 where registerNum = ? " ;
			ps = conn.prepareStatement(sql);
			ps.setString(1, registerNum);
			ps.execute() ;
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
	
	
	/**
	 * 得到所有副合同编号
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	
	public String getDeputyProjectIds(String registerNum) throws Exception {

		PreparedStatement ps = null;
		ResultSet rs = null ;
		ASFuntion CHF = new ASFuntion() ;
		String deputyProjectIds = "" ;
		try {

			String sql = " select group_concat(projectid) from z_projectbusiness where parentRegisterNum=? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, registerNum);
			rs = ps.executeQuery() ;
			
			if(rs.next()) {
				deputyProjectIds = CHF.showNull(rs.getString(1)) ;
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
		return deputyProjectIds ;
	}
	
	  
	/**
	 * 根据sql 得到一列值
	 * @param sql
	 * @return
	 */
	public String getValueBySql(String sql){
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		String value = "";
		
		
		try {
		
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				value += rs.getString(1);
			}
			
		} catch (SQLException e) {
		
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return value;
	}
	
	/**
	 * 修改莫列的值
	 * @param sql
	 * @return
	 */
	public boolean UpdateValueBySql(String sql){
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean result = false;
		try {
		
			ps = conn.prepareStatement(sql);
			
			ps.executeUpdate();
			
			result = true;
		} catch (SQLException e) {
		
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return result;
	}
	
	public Map getMapProjectext(String projectId){
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "SELECT`autoid`,`projectid`,`setdefprojectid`,`s0`,`s1`,`s2`,`s3`,`s4`,`s5`,`s6`,`s7`,`s8`," +
				       "`s9`,`s10`,`s11`,`s12`,`s13`,`s14`,`s15`,`s16`,`s17`,`s18`,`s19`,`price`,`s20`," +
				       "`s21`,`s22`,`s23`,`s24`,`s25`,`s26`,`s27`,`s28`,`s29`,`s30` "+
					   "FROM `asdb`.`z_projectext` where projectId='"+projectId+"' ";
		
		Map map = new HashMap();
		
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				
				map.put("autoid", rs.getString("autoid"));
				map.put("projectid", rs.getString("projectid"));
				map.put("setdefprojectid", rs.getString("setdefprojectid"));
				
				for (int i = 0; i <=30; i++) {
					map.put("s"+i, rs.getString("s"+i));
				}
			}
			
		} catch (SQLException e) {
		
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return map;
	}
		
}
