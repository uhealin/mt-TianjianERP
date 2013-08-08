package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.matech.audit.service.task.TaskService;


/** 
 * 
 * @author LuckyStar
 *
 * 刷新出5001公式；供合并报表调用 
 *
 * 调用的例子：
 *  =取行公式覆盖(5000, "明细项目", "gs","&ColNo=1_3&AreaName=明细&GroupBy=1&ManuName=明细表A")
 * =取行公式覆盖(5000, "公式", "gs","&ColNo=7&AreaName=明细&ManuName=现金明细表&FilterBy=d1=~人民币~")
 * =取行公式覆盖(5000, "公式", "gs","&ColNo=7&AreaName=明细&ManuName=现金明细表&FilterBy=d1=~人民币~ and d2>20000")
 * =取行公式覆盖(5000, "公式", "gs","&ColNo=7&AreaName=明细&ManuName=现金明细表&GroupBy=1")
 * 
 * 1.on 20080316:新增参数：
 * EliminateColNo：设置抵销分录要出现的列，要在多列出现，请用_分隔；
 * EliminateOcc: 抵销分录模式：借发生,贷发生,发生
 * EliminateFilterBy: 合并报表过滤条件，比如单项重大、不重大等；
 * 举例如下：
  =取行公式覆盖(5000, "公式", "gs","&ColNo=7&AreaName=明细&ManuName=现金明细表&GroupBy=1&EliminateOcc=发生&EliminateColNo=4")
 *  
 * 
 * 2.on 20080319:新增参数
 * EliminateDirection: 设置对应的方向，多列用_分隔，支持借和贷；
 * 
 * 
 * 3.on 20080321:新增参数NodeName,专为明细表引用附注表临时追加参数；
 * NodeName:专为刷合并资产负债表开发；应用举例：
 * 
 * =取行公式覆盖(5000, "公式", "gs","&ColNo=7&AreaName=明细&ManuName=现金明细表&NodeName=附注表")
 */
public class _5000_0 extends AbstractAreaFunction {

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

			// 获取哪一列
			String strColNo = request.getParameter("ColNo");
			if (strColNo == null) {
				throw new Exception("ColNo未指定");
			}
			
			//抵销分录相关的3个参数
			String strEliminateColNo =request.getParameter("EliminateColNo");
			if (strEliminateColNo == null) {
				strEliminateColNo="";
			}
			strEliminateColNo="_"+strEliminateColNo+"_";
			
			String strEliminateOcc =request.getParameter("EliminateOcc");
			if (strEliminateOcc == null) {
				strEliminateOcc ="";
			}
			String[] strEliminateOccs = strEliminateOcc.split("_");
			
			String strEliminateFilterBy =request.getParameter("EliminateFilterBy");
			if (strEliminateFilterBy == null) {
				strEliminateFilterBy="";
			}
			String strEliminateDirection =request.getParameter("EliminateDirection");
			if (strEliminateDirection == null) {
				strEliminateDirection="";
			}
			String[] strEliminateDirections = strEliminateDirection.split("_");
			
			
			//底稿名称
			String strManuName = request.getParameter("ManuName");
			
			//看看是不是支持多列
			String[] strCols = strColNo.split("_");
			
			int iEliminate=-1;

			String AreaName = request.getParameter("AreaName");
			String strGroupBy = request.getParameter("GroupBy");
			String strParam="";
			String strFilterBy = request.getParameter("FilterBy");
			String strOrderBy = request.getParameter("OrderBy");
			String strLeftOrderBy = request.getParameter("LeftOrderBy");
			String NodeName = request.getParameter("NodeName");
			
			
			String strLimit = request.getParameter("Limit");
			
			if (AreaName != null && !"".equals(AreaName)) {
				strParam+="&AreaName="+AreaName;
			}
			if (strGroupBy != null && !"".equals(strGroupBy)) {
				strParam+="&GroupBy="+strGroupBy;
			}
			if (strManuName != null && !"".equals(strManuName)) {
				strParam+="&ManuName="+strManuName;
			}else{
				//只在没有提供ManuName的情况下才有用
				String strTaskCode = request.getParameter("TaskCode");
				if (strTaskCode != null && !"".equals(strTaskCode)) {
					strParam+="&TaskCode="+strTaskCode;
				}
			}
			
			if (strFilterBy != null && !"".equals(strFilterBy)) {
				strParam+="&FilterBy="+strFilterBy;
			}
			if (strOrderBy != null && !"".equals(strOrderBy)) {
				strParam+="&OrderBy="+strOrderBy;
			}
			if (strLimit != null && !"".equals(strLimit)) {
				strParam+="&Limit="+strLimit;
			}
			if (strLeftOrderBy != null && !"".equals(strLeftOrderBy)) {
				strParam+="&LeftOrderBy="+strLeftOrderBy;
			}
			if (NodeName != null && !"".equals(NodeName)) {
				strParam+="&NodeName="+NodeName;
			}
			
			
			//抵销分录相关参数的传递
			String strEliminateParam="";
			if (strEliminateOcc != null && !"".equals(strEliminateOcc)) {
				strEliminateParam+="&EliminateOcc=$抵销分录发生参数待替换$";
			}
			if (strEliminateFilterBy != null && !"".equals(strEliminateFilterBy)) {
				strEliminateParam+="&EliminateFilterBy="+strEliminateFilterBy;
			}
			if (strEliminateDirection != null && !"".equals(strEliminateDirection)) {
				strEliminateParam+="&EliminateOcc=$抵销分录方向参数待替换$";
			}
			
			
			
			
			//是普通单体附注或者合并附注
			
			if (strCols.length==1){
				
				//不是多列，只是1列，这种情况下，生成的就是从第1列到指定strColNo列。
				int iColNo = 0;
				try {
					iColNo = Integer.parseInt(strColNo);
				} catch (Exception e) {
				}
				if (iColNo < 1 || iColNo > 30) {
					throw new Exception("ColNo[" + iColNo + "]错误,超过1到30范围");
				}
				
				//组装最后的sql
				int iStart=0;
				if (strLeftOrderBy != null && !"".equals(strLeftOrderBy)) {
					strSql = "select concat( '=取列公式插入(5001, \"\", \"leftorderby\", \"&ColNo=1"+strParam+"\")' ) as gs \n";
					iStart=1;
				}else{
					strSql = "select concat( '=取列公式插入(5001, \"\", \"d1\", \"&ColNo=1"+strParam+"\")' ) as gs \n";
					iStart=2;
				}
						
				for (int i=iStart;i<=iColNo;i++){
					
					if (strEliminateColNo.indexOf("_"+i+"_")>=0){
						iEliminate++;
						
						//是抵销分录指定增加的列
						strSql+="union \n"
							+"select concat( '=取列公式覆盖(5001, \"\", \"sdd"+i+"\", \"&ColNo="+i+strParam+strEliminateParam.replaceAll("\\$抵销分录发生参数待替换\\$", strEliminateOccs[iEliminate]).replaceAll("\\$抵销分录方向参数待替换\\$", strEliminateDirections[iEliminate])+"\")' ) as gs \n";
					}else{
						//不是抵销分录指定增加的列
						strSql+="union \n"
							+"select concat( '=取列公式覆盖(5001, \"\", \"d"+i+"\", \"&ColNo="+i+strParam+"\")' ) as gs \n";
					}
				}
			}else{
				//是多列，生成指定列的
				
				//组装最后的sql
				strSql = "select concat( '=取列公式插入(5001, \"\", \"d"+strCols[0]+"\", \"&ColNo="+strCols[0]+strParam+"\")' ) as gs \n";
						
				for (int i=1;i<strCols.length;i++){
					if (strEliminateColNo.indexOf("_"+i+"_+")>=0){
						//是抵销分录指定增加的列
						strSql+="union \n"
							+"select concat( '=取列公式覆盖(5001, \"\", \"sdd"+strCols[i]+"\", \"&ColNo="+strCols[i]+strParam+strEliminateParam.replaceAll("\\$抵销分录发生参数待替换\\$", strEliminateOccs[iEliminate]).replaceAll("\\$抵销分录方向参数待替换\\$", strEliminateDirections[iEliminate])+"\")' ) as gs \n";
					}else{
						strSql+="union \n"
							+"select concat( '=取列公式覆盖(5001, \"\", \"d"+strCols[i]+"\", \"&ColNo="+strCols[i]+strParam+"\")' ) as gs \n";
					}
				}
			}
			
			// 返回指定列的缓存结果
			System.out.println("\n_5000:qwh:sql=\n"+strSql);
			
			
			ps = conn.prepareStatement(strSql);
			rs = ps.executeQuery();

			return rs;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

}
