package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.matech.audit.service.report.ReportService;
import com.matech.audit.service.task.TaskService;



/**
 * 
 * 5003公式，专门供合并报表体系使用,只会刷出按条件过滤后的抵销分录的汇总值，专用单元格取数使用
 * 
 * 公式修改说明
 * 
 * 1.on 20080317:第一版，参数说明：
 * ManuName：底稿名字
 * AreaName：区域名字
 * EliminateOcc: 抵销分录模式：借发生,贷发生,发生,原币借发生,原币贷发生，原币发生，
 *               期初发生、1年前期初发生、2年前期初发生、3年前期初发生、4年前期初发生、5年前期初发生等；
 *               期初借发生、期初贷发生
 * EliminateFilterBy: 合并报表过滤条件，比如单项重大、不重大等；
 * 
 * 调用说明：
 * =取自定义函数(5003,"occ","&AreaName=明细&ManuName=现金明细表&EliminateOcc=发生&EliminateFilterBy=n1=~是否单项重大~ and v1=~是~")
 * 
 * 总体平衡关系说明如下：
 * 
 * I、1年前期初发生 将只汇总调整到【1年前】的金额（借－贷）；2年前期初发生、3年前期初发生、4年前期初发生、5年前期初发生类似；
 * II、期初发生 将汇总所有调整到【1年前、2年前、3年前、4年前、5年前】的金额；
 * III、发生（实际相当于期末发生）将汇总所有的发生，包括本年以及1到5年前的；
 * IV、借发生将只汇总调整到【本年】的借方金额，贷发生类似（不含1年前至5年前的调整数）；
 * 
 * 原币逻辑处理类似，支持的类型说明如下：
 * I、原币期初发生：汇总所有调整到【1年前、2年前、3年前、4年前、5年前】的原币金额
 * II、原币发生（实际相当于原币期末发生）将汇总所有的原币发生数，包括本年以及1到5年前的；
 * III、原币借发生将只汇总调整到【本年】的原币借方金额，原币贷发生类似（不含1年前至5年前的调整数）；
 * 
 * 截至20080317，EliminateOcc总共支持的类型包括：
 * 借发生,贷发生,发生,期初发生、1年前期初发生、2年前期初发生、3年前期初发生、4年前期初发生、5年前期初发生
 * 原币借发生,原币贷发生，原币发生，原币期初发生；
 * 
 * 相互间平衡关系如下：
 * I、期初发生＝1年前期初发生＋2年前期初发生＋3年前期初发生＋4年前期初发生＋5年前期初发生
 * II、期初发生＋借发生＋贷发生＝发生
 * III、原币期初发生＋原币借发生＋原币贷发生＝原币发生
 * IV、期初借发生+期初贷发生＝期初发生
 * 
 *  * 4.on 20080318: 新增参数EliminateDirection
 * 如果提供EliminateDirection，我就按照EliminateDirection指定的方向；
	如果没有提供，我就去取底稿对应科目方向；
	如果底稿对应科目方向也没有提供，我就无条件按借方走。
 * 
 * 
 * @author winnerQ
 *
 */
public class _5003_0 extends AbstractAreaFunction {

	public ResultSet process(HttpSession session, HttpServletRequest request,
			HttpServletResponse response, Connection conn, Map args)
			throws Exception {

		String strSql = "";
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			
			// 项目编号
			String projectid = request.getParameter("curProjectid");
			if (projectid == null || projectid.length() == 0) {
				throw new Exception("请提供项目编号");
			}

			// 底稿编号
			String taskid = request.getParameter("taskid");
			if (taskid == null || taskid.length() == 0) {
				String curTaskCode=request.getParameter("curTaskCode");
				if (curTaskCode == null || curTaskCode.length() == 0) {
					throw new Exception("请提供taskid或curTaskCode");
				}else{
					TaskService ts=new TaskService(conn,projectid);
					taskid=ts.getTaskIdByTaskCode(curTaskCode);
					org.util.Debug.prtOut("通过TASKCODE定位TASKID="+taskid);
				}
			}

			String strManuName = request.getParameter("ManuName");
			List result=null;
			
			ReportService reportservice=new ReportService(conn);
			
			String strResult = "";
			String strTaskCode = request.getParameter("TaskCode");
			if (strTaskCode == null || "".equals(strTaskCode)) {
				//如果没有提供taskcode,则是合并报表，则通过taskname动态定位
				if (strManuName == null || strManuName.length() == 0) {
					result = reportservice.getTaskIdList(projectid,
							taskid);
				}else{
					result = reportservice.getTaskIdList(projectid,
							taskid,strManuName+".xls");
				}
				
				
				if (result.size()>0){
					for (int i = 0; i < result.size(); i++) {
						strResult += result.get(i) + ",";
					}
						//System.out.println("qwh:strResult下级=" + strResult);
				}else{
					//无法通过下级定位，说明是同级，也就是同一个单位里面的汇总表刷明细表录入结果
					strResult=reportservice.getTaskId(projectid, taskid, "明细表", strManuName+".xls") +",";
						//System.out.println("qwh:strResult同级=" + strResult);				
				}
			}
			
			//抵销分录条件
			String strEliminateFilterBy= request.getParameter("EliminateFilterBy");
			if (strEliminateFilterBy!=null && !"".equals(strEliminateFilterBy)){
				strEliminateFilterBy=" and " + strEliminateFilterBy.replaceAll("~", "'") +  " ";
			}
			
			//抵销分录发生额汇总方式
			String strEliminateOcc= request.getParameter("EliminateOcc"),strEliminateFilterBy1="";
			if ("借发生".equals(strEliminateOcc)){
				//至汇总本年
				strEliminateOcc="sum(if(direction=1,occurvalue,0)) as occ";
				strEliminateFilterBy1=" and accountyear='本年'";
			}else if ("贷发生".equals(strEliminateOcc)){
				strEliminateOcc="sum(if(direction=-1,occurvalue,0)) as occ";
				strEliminateFilterBy1=" and accountyear='本年'";
			}else if ("发生".equals(strEliminateOcc)){
				strEliminateOcc="sum(direction * occurvalue) as occ";
				strEliminateFilterBy1=" ";
			}else if ("本期发生".equals(strEliminateOcc)){
				strEliminateOcc="sum(direction * occurvalue) as occ";
				strEliminateFilterBy1=" and accountyear = '本年'";
			}else if ("期初发生".equals(strEliminateOcc)){
				strEliminateOcc="sum(direction * occurvalue) as occ";
				strEliminateFilterBy1=" and accountyear <> '本年'";
			}else if ("期初借发生".equals(strEliminateOcc)){
				strEliminateOcc="sum(if(direction=1,occurvalue,0)) as occ";
				strEliminateFilterBy1=" and accountyear <> '本年'";
			}else if ("期初贷发生".equals(strEliminateOcc)){
				strEliminateOcc="sum(if(direction=-1,occurvalue,0)) as occ";
				strEliminateFilterBy1=" and accountyear <> '本年'";
			}else if ("1年前期初发生".equals(strEliminateOcc)){
				strEliminateOcc="sum(direction * occurvalue) as occ";
				strEliminateFilterBy1=" and accountyear = '1年前'";
			}else if ("2年前期初发生".equals(strEliminateOcc)){
				strEliminateOcc="sum(direction * occurvalue) as occ";
				strEliminateFilterBy1=" and accountyear = '2年前'";
			}else if ("3年前期初发生".equals(strEliminateOcc)){
				strEliminateOcc="sum(direction * occurvalue) as occ";
				strEliminateFilterBy1=" and accountyear = '3年前'";
			}else if ("4年前期初发生".equals(strEliminateOcc)){
				strEliminateOcc="sum(direction * occurvalue) as occ";
				strEliminateFilterBy1=" and accountyear = '4年前'";
			}else if ("5年前期初发生".equals(strEliminateOcc)){
				strEliminateOcc="sum(direction * occurvalue) as occ";
				strEliminateFilterBy1=" and accountyear = '5年前'";
			}
			
			//原币
			else if ("原币借发生".equals(strEliminateOcc)){
				strEliminateOcc="sum(if(direction=1,currvalue,0)) as occ";
				strEliminateFilterBy1=" and accountyear='本年'";
			}else if ("原币贷发生".equals(strEliminateOcc)){
				strEliminateOcc="sum(if(direction=-1,currvalue,0)) as occ";
				strEliminateFilterBy1=" and accountyear='本年'";
			}else if ("原币发生".equals(strEliminateOcc)){
				strEliminateOcc="sum(direction * currvalue) as occ";
				strEliminateFilterBy1=" ";
			}else if ("原币期初发生".equals(strEliminateOcc)){
				strEliminateOcc="sum(direction * currvalue) as occ";
				strEliminateFilterBy1=" and accountyear <> '本年'";
			}else{
				strEliminateOcc="";
			}
			
			
//			抵销分录加总方向
			String strEliminateDirection =request.getParameter("EliminateDirection");
			if (strEliminateDirection==null || "".equals(strEliminateDirection)){
				strSql="select subjectname from z_task where projectid="+projectid+" and taskid=" + taskid;
				ps = conn.prepareStatement(strSql);
				rs = ps.executeQuery();
				if (rs.next()){
					strEliminateDirection=rs.getString(1);
					org.util.Debug.prtOut("strEliminateDirection="+strEliminateDirection);
				}
				rs.close();
				ps.close();
				
				org.util.Debug.prtOut("strSql="+strSql);
				
				org.util.Debug.prtOut("strEliminateDirection="+strEliminateDirection);
			}
			if ("贷".equals(strEliminateDirection) || "－1".equals(strEliminateDirection)) {
				strEliminateDirection="-1";
			}else if (!"-1".equals(strEliminateDirection)) {
				//如果不等于-1，无条件置为1
				strEliminateDirection="1";
			}
			
			
			//显示的列和分组的列
			String AreaName = request.getParameter("AreaName");
			
			strSql="select " + strEliminateDirection +" * "+ strEliminateOcc+ "\n"
					+"from z_accountrectifyentry1 \n"
					+"where projectid="+projectid+"  \n";
					//+"and sheetname='"+strManuName+"' \n";
			if (AreaName != null && !"".equals(AreaName)) {
				strSql+="and areaname='"+AreaName+"' \n";
			}
			
			strSql+= strEliminateFilterBy1 +" \n";
			
			if (strTaskCode != null && !"".equals(strTaskCode)) {
				strSql+=" and accounttype='" + strTaskCode + "'\n";
			}else{
				strSql+=" and accounttype in ( \n"
						+"	select taskcode from z_task \n"
						+"	where projectid="+projectid+" and taskid in ("+ strResult + "-1) \n" 
						+" ) \n";
			}
	
			//追加条件
			if (strEliminateFilterBy != null && !"".equals(strEliminateFilterBy)) {
				strSql += strEliminateFilterBy;
			}
			
			org.util.Debug.prtOut("qwh:5003:resultSql="+strSql);
			

			// 返回指定列的缓存结果

			ps = conn.prepareStatement(strSql);
			rs = ps.executeQuery();

			return rs;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

}
