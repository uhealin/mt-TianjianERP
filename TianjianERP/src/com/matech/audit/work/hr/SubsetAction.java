package com.matech.audit.work.hr;

import java.io.PrintWriter;
import java.sql.Connection;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.fileupload.MyFileUpload;
import com.matech.audit.service.form.model.FormVO;
import com.matech.audit.service.form.model.QueryVO;
import com.matech.audit.service.user.UserService;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.single.Single;
import com.matech.framework.pub.util.Debug;
import com.matech.framework.pub.util.StringUtil;
import com.matech.framework.service.excelupload.ExcelUploadService;

public class SubsetAction extends MultiActionController {

	
	protected enum Jsp{
		subsetUpload;
		
		public String path(){
			return MessageFormat.format("/hr/{0}.jsp", this.name());
		}
	}
	
	
	
	public ModelAndView subsetUpload(HttpServletRequest request,HttpServletResponse response)throws Exception{
		ModelAndView mv=new ModelAndView(Jsp.subsetUpload.path());
		return mv;
		
	}
	
	/**
	 * 保存批量excel导入项目
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView doUploadSubset(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		PrintWriter out = null;
		Connection conn=null;
		Single sl = new Single();
		UserSession us=(UserSession)request.getSession().getAttribute("userSession");
		String lockmsg = "装载帐套数据";
		DbUtil dbUtil=null;
		String formid=null;
		FormVO formVO=null;
		List<QueryVO> queryVOs=new ArrayList<QueryVO>();
		try {
			
			response.setContentType("text/html;charset=utf-8");  //设置编码
//			response.setHeader("title", "EXCEL导入");
			out = response.getWriter();
			
			Map parameters = null;

			String uploadtemppath = "";

			String strFullFileName = "";
//			String nf = "";
			//获取前台指定的客户ID

		
			MyFileUpload myfileUpload = new MyFileUpload(request);
			uploadtemppath = myfileUpload.UploadFile(null, null);
			parameters = myfileUpload.getMap();
			System.out.println(parameters);
			
			//如果不存在,说明是第一次提交,这个时候从MAP中取值
			uploadtemppath = (String) parameters.get("tempdir");

			strFullFileName = uploadtemppath
					+ (String) parameters.get("filename");
			org.util.Debug.prtOut("strFullFileName=" + strFullFileName);
			uploadtemppath = (String) parameters.get("tempdir");
			formid=(String)parameters.get("formid");
			if (uploadtemppath.equals(""))
				out.print("Error\n帐套数据上传及预处理失败");
			else
				out.println("帐套数据上传并分析成功!<br>正在加载，请等待<br><br><br>");

			int error = 0; //用于标记程序是否出错,出错了后面就不会再继续执行了


			//分析帐套文件,取出帐套年份;

			out.println("预处理分析帐套文件<br/>");
			out.flush();
			

			conn = new DBConnect().getDirectConnect("");
            dbUtil=new DbUtil(conn);
            formVO=dbUtil.load(FormVO.class, formid);
            List<QueryVO> tempQueryVOs =dbUtil.select(QueryVO.class, "select * from {0} where formid=?", formid);
            
            for(QueryVO queryVO:tempQueryVOs){
            	if(StringUtil.isBlank(queryVO.getNAME())||queryVO.getBSHOW()!=1)continue;
            	queryVOs.add(queryVO);
            }
            
//			初始化业务对象
			ExcelUploadService upload = null;
			try {
				upload = new ExcelUploadService(conn,strFullFileName);
			} catch (Exception e) {
				e.printStackTrace();
				out.println("临时路径或者客户编号设置有误,请与系统管理员联系<br>");
				error = 1;
			}
			
//			检查用户指定年份的帐套是否存在;

			//定义单一，避免其他用户干扰；
			
			
			try {
				sl.locked(lockmsg, us.getUserLoginId());
			} catch (Exception e) {
				out.println(e.getMessage() + "<br/>");
				error = 1;
			}

			if (error > 0) {
				out.println("装载活动遇到错误,已经中止!<br>请解决错误后重新装载");
			} else {
				org.util.Debug.prtOut("装载的临时目录为:" + uploadtemppath);
				out.println("继续处理装载<br>");
				out.flush();
				
				UserService ued = new UserService(conn);
				out.println("正在分析EXCEL文件......");
				out.flush();
				upload.init();
				out.println("分析EXCEL文件完毕!<BR>");

				//开始装载科目余额表信息
				//首先清空指定表的指定帐套的数据;
				out.println("正在装载用户内容!......");
				out.flush();

				ued.newTable();

				upload.setExcelNum("");
				
				String es="";
				String[] exexlKmye=new String[queryVOs.size()],tableKmye=new String[queryVOs.size()]
						,exexlPzmxOpt=new String[queryVOs.size()],tablePzmxOpt=new String[queryVOs.size()]
						,exexlKmyeFixFields={},excelKmyeFixFieldValues={};
				for(int i=0;i<queryVOs.size();i++){
					QueryVO queryVO=queryVOs.get(i);
					es+=","+queryVO.getNAME();
					exexlKmye[i]=queryVO.getNAME();
					tableKmye[i]=queryVO.getENNAME();
					
				}
				upload.setExcelString(es.substring(1));
				exexlPzmxOpt=exexlKmye;
				tablePzmxOpt=tableKmye;
				/*
				upload.setExcelString("项目编号,项目名称,委托人,审计对象,审计对象所属行业,审计工作类别,	合同开始时间,	合同结束时间,合同签约时间,服务时间" +
						"完成时间,主要审计内容,服务范围,合同金额,投资规模/项目规模(万元),项目联系人,联系方式,合同编号,机构参与人数(高峰),(平均),合同履行情况,证明文件");
				
				String[] exexlKmye = { "项目编号","项目名称","委托人","审计对象","审计对象所属行业","审计工作类别","合同开始时间","合同结束时间"
						,"合同签约时间","服务时间","合同金额","项目联系人","联系方式","合同编号"};
				String[] tableKmye = { "projectId","projectName", "principalName", "auditTarget","auTaIndustry","auditWorkClass",
						"contractBegin","contractEnd","signTime","serveTime","contractMoney","proUnitPrincipal","contactInfo","contractNu"};
				
				//可以为空的字段
				String[] exexlPzmxOpt = { "完成时间","主要审计内容","服务范围","投资规模/项目规模(万元)","机构参与人数(高峰)","(平均)","合同履行情况","证明文件"};
				String[] tablePzmxOpt = { "finishTime","auditContent","serveRange","investmentScale","peopleBigNu","peopleAvgNu","performCondition","certificate" };
				
				//婚姻状态，籍贯，户口所在地，政治面貌，入党时间，组织关系所在单位，专业，英语能力，CPA号，合同类型,特长
				String[] exexlKmyeFixFields = {"property"};
				String[] excelKmyeFixFieldValues = {"33333"};
*/
				String result = "";

				result = upload.LoadFromExcel(formVO.getNAME(), formVO.getTABLENAME(),
				exexlKmye, tableKmye,exexlPzmxOpt,tablePzmxOpt, exexlKmyeFixFields, excelKmyeFixFieldValues,true);

				out.println("装载用户内容完毕!<BR>");

				out.flush();
				out.println("开始更新用户列表!......");
				out.flush();
				result = ued.CheckUpData();
				ued.updateData();
				ued.insertData();
				out.println("更新用户列表完毕!<BR>");
				
				if (result != null && result.length() > 0) {
					out.println("<br><br>装载非正常结果报告：<BR><font color='red'>");
					out.println(result);
					out.println("</font><br>");
				}

				out.println("<hr>数据装载成功 <a href=\"formDefine.do?method=formListView&uuid=30eb6764-2e94-4507-b27e-c44cfd96e5b0\">返回查询页面</a>\"</font>");
				
				
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			out.println("<font style=\"color:red\">装载处理出现错误:<br/>"+ e.getMessage());
			out.println("<a href=\"user.do?method=UploadProject\">返回装载页面</a>\"</font>");
			
			Debug.print(Debug.iError, "查询失败！", e);
			e.printStackTrace();
		} finally {
			try {
				sl.unlocked(lockmsg, us.getUserLoginId());
			} catch (Exception e) {
				out.println("撤销并发锁失败：" + e.getMessage() + "<br/>");
			}
			DbUtil.close(conn);
		}

		return null;
	}
	
	
}
