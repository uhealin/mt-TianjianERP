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
 * 刷新出5004公式；供合并报表调用 
 *
 * 调用的例子：
 *  =取行公式覆盖(5004, "明细项目", "gs","&ColNo=1_3&AreaName=明细&GroupBy=1&ManuName=明细表A")
 * =取行公式覆盖(5004, "公式", "gs","&ColNo=7&AreaName=明细&ManuName=现金明细表&FilterBy=d1=~人民币~")
 * =取行公式覆盖(5004, "公式", "gs","&ColNo=7&AreaName=明细&ManuName=现金明细表&FilterBy=d1=~人民币~ and d2>20000")
 * =取行公式覆盖(5004, "公式", "gs","&ColNo=7&AreaName=明细&ManuName=现金明细表&GroupBy=1")
 * 
 * 1.on 20080316:新增参数：
 * EliminateColNo：设置抵销分录要出现的列，要在多列出现，请用_分隔；
 * EliminateOcc: 抵销分录模式：借发生,贷发生,发生
 * EliminateFilterBy: 合并报表过滤条件，比如单项重大、不重大等；
 * 举例如下：
  =取行公式覆盖(5004, "公式", "gs","&ColNo=7&AreaName=明细&ManuName=现金明细表&GroupBy=1&EliminateOcc=发生&EliminateColNo=4")
 *  
 * 
 * 2.on 20080319:新增参数
 * EliminateDirection: 设置对应的方向，多列用_分隔，支持借和贷；
 * 
 * 
 * 3.on 20080319:新增参数
 * NodeName:专为刷合并资产负债表开发；应用举例：
 * 
 * =取行公式覆盖(5004, "公式", "gs","&ColNo=2&AreaName=资产列&ManuName=审定报表")
 */
public class _5004_0 extends AbstractAreaFunction {

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
			
			
			//底稿名称
			String strManuName = request.getParameter("ManuName");
			
			String AreaName = request.getParameter("AreaName");
			String strParam="";
			
			if (AreaName != null && !"".equals(AreaName)) {
				strParam+="&AreaName="+AreaName;
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
			
			String NodeName = request.getParameter("NodeName");
			if (NodeName == null || "".equals(NodeName)) {
				NodeName="报表";
			}
			
			//是最终合并资产负债表横刷的单位，则
			strSql="select concat('=取列公式覆盖(5001, \\\"',b.taskname,'\\\", \\\"d"+strColNo+"\\\", \\\"&ColNo="+strColNo+"&AreaName="+AreaName+"&ManuName="+strManuName+"&TaskCode=',d.taskcode,'\\\")') as gs \n"
				+",b.orderid \n" 
				+"from z_task a,z_task b,z_Task c,z_task d \n"
				+"where a.projectid=  "+projectid+"  \n"
				+"and a.taskname='单体数据' \n"
				+"and a.parenttaskid=( \n"
				+"	select c.parenttaskid from z_Task a, z_Task b,z_Task c \n"
				+"	where a.projectid=  "+projectid+"  \n"
				+"	and a.taskid="+taskid+" \n"
				+"	and b.projectid=  "+projectid+"  \n"
				+"	and b.taskid=a.parenttaskid \n"
				+"	and c.projectid=  "+projectid+"  \n"
				+"	and c.taskid=b.parenttaskid \n"
				+") \n"
				+"and b.projectid=  "+projectid+" \n" 
				+"and b.parenttaskid=a.taskid \n"

				+"and c.projectid=  "+projectid+"  \n"
				+"and c.parenttaskid=b.taskid \n"

				+"and d.projectid=  "+projectid+" \n"
				+"and d.taskname  = '"+strManuName+".xls' \n" 
				+"and d.parenttaskid=c.taskid \n"
				+"order by orderid";
				
			
			// 返回指定列的缓存结果
			org.util.Debug.prtOut("\n_5004:qwh:sql=\n"+strSql);
			
			
			ps = conn.prepareStatement(strSql);
			rs = ps.executeQuery();

			return rs;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

}
