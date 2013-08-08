package com.matech.audit.work.investManage;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.investManage.InvestManageService;
import com.matech.audit.service.investManage.model.InvestManage;
import com.matech.audit.service.userpopedom.UserPopedomService;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.StringUtil;

public class InvestManageAction extends MultiActionController{

	private static final String LIST = "/investManage/investList.jsp";  
	private static final String FORBIDINVESTLIST = "/investManage/forbidInvestList.jsp";  
	private static final String AddAndEdit = "/investManage/investEdit.jsp";
	private static final String UPDATE = "/investManage/investUpdate.jsp";
	 
	
	/**
	 *  list
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception  {

		UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
		String userId = userSession.getUserId();
		

		String menuid = StringUtil.showNull(request.getParameter("menuid")); //菜单ID
		if("".equals(menuid)) menuid = "10000733";
		
		Connection conn = null;
		try {
			conn= new DBConnect().getConnect("");
			String departments = new UserPopedomService(conn).getUserIdPopedom(userSession.getUserId(), menuid); //部门授权
			
			DataGridProperty pp = new DataGridProperty();
			// 必要设置
			pp.setTableID("investList");

			// 基本设置
			pp.setTrActionProperty(true);
			pp.setTrAction("style=\"cursor:hand;\" autoId='${autoId}'\" ");
			pp.setPageSize_CH(40);
		    pp.setPrintTitle("投资情况管理信息");

			// sql设置 			
//			String sql = " select autoId,investId,a.loginid,loginName,userName,relations,answer,ssStockNum,hsStockNum,gsstockNum,setTime " 
//					   + " from (" 
//					   + " select max(autoId) as autoId,max(loginid) as loginid,max(loginName) as loginName,investId, " 
//					   + " max(userName) as userName,max(relations) as relations,max(answer) as answer,max(ssStockNum) as ssStockNum," 
//					   + " max(hsStockNum) as hsStockNum,max(gsstockNum) as gsstockNum,max(setTime) as setTime "
//					   + " from K_investManage group by investId ) a  "
//					   + " left join k_user u on a.loginid = u.id  "
//					   + " where 1=1 and ( a.loginid = '"+userId+"' or u.departmentId IN ('"+departments+"'))  "
//					   + " ${userName} ${relations} ${ssstockNum} ${hsstockNum} ${gsstockNum} ";
		    
			
			String sql = " select * from ( "
				+ " select autoId,investId,t.loginid,loginName,userName,relations,answer,ssStockNum,ssStockNum2," 
				+ " hsStockNum,hsStockNum2,gsstockNum,setTime,u.departmentId,max(sockCodes) as hgx from ( "
				+ " select im.*, "
				+ " case when c.sockCode is null then '1关联不上' when c.sockCode='' then '1关联不上' "
				+ " else  "
				+ " case when im.stockOutDate is null then '3违规' when im.stockOutDate='' then '3违规' else '2关联得上 但不违规' "
				+ " end  "
				+ " end as sockCodes "
				+ " from K_investManage im " 
				+ " left join k_customer c on im.stockCode = c.sockCode "
				+ " ) t  "
				+ " left join k_user u on t.loginid = u.id  "
				+ " group by  investId ) a "
				+ " where 1=1 and ( a.loginid = '"+userId+"' or a.departmentId IN ("+departments+")) "
				+ " ${userName} ${relations} ${ssstockNum} ${ssstockNum2} ${hsstockNum} ${hsstockNum2} ${gsstockNum} ";
			
			
			pp.setSQL(sql); 
			pp.setOrderBy_CH("setTime,autoId") ;
			pp.setDirection("desc,desc");
			
			pp.setInputType("radio");
			pp.setWhichFieldIsValue(1);
			
			
			pp.addColumn("投资人姓名", "userName");
			pp.addColumn("投资人与本人关系", "relations");
			pp.addColumn("声明是否进行过股票买卖", "answer");
			pp.addColumn("合规性", "hgx",null,"com.matech.audit.work.investManage.InvestManageProcess",null);
			pp.addColumn("深A股票帐号", "ssStockNum");
			pp.addColumn("深B股票帐号", "ssStockNum2");
			pp.addColumn("沪A股票帐号", "hsStockNum");
			pp.addColumn("沪B股票帐号", "hsStockNum2");
			pp.addColumn("港市股票帐号", "gsstockNum");
			pp.addColumn("操作人", "loginName");
			pp.addColumn("最后操作时间", "setTime");
			
			pp.setPrintEnable(true);
		    pp.setPrintVerTical(false);
		    pp.setPrintTitle("投资合规复查列表");
		    pp.setPrintColumnWidth("15,20,20,30,30,30,30,20");
		    pp.setPrintSqlColumn("userName,relations,answer,hgx,ssStockNum,ssStockNum2,hsStockNum,hsStockNum2,gsstockNum,loginName,setTime ");
			pp.setPrintColumn("投资人姓名`投资人与本人关系`声明是否进行过股票买卖`合规性`深A股票帐号`深B股票帐号`沪A股票帐号`沪B股票帐号`港市股票帐号`操作人`最后操作时间");
			
			pp.addSqlWhere("userName", " and userName like '%${userName}%' ");
			pp.addSqlWhere("relations", " and relations like '%${relations}%' ");
			pp.addSqlWhere("ssstockNum", " and ssstockNum like '%${ssstockNum}%' ");
			pp.addSqlWhere("ssstockNum2", " and ssstockNum2 like '%${ssstockNum2}%' ");
			pp.addSqlWhere("hsstockNum", " and hsstockNum like '%${hsstockNum}%' ");
			pp.addSqlWhere("hsstockNum2", " and hsstockNum2 like '%${hsstockNum2}%' ");
			pp.addSqlWhere("gsstockNum", " and gsstockNum like '%${gsstockNum}%' ");
			
			pp.setColumnWidth("10%,10%,12%,8%,10%,10%,10%,10%,10%,10%,10%");
			
			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(conn);
		}		
		return new ModelAndView(LIST);
	}
	
	/**
	 *  禁止投资名单
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView forbidInvestList(HttpServletRequest request, HttpServletResponse response) throws Exception  {

		DataGridProperty pp = new DataGridProperty();
		// 必要设置
		pp.setTableID("forbitInvestList");

		// 基本设置
		pp.setPageSize_CH(200);
	    pp.setPrintTitle("禁止投资名单信息");

		// sql设置 			
		String sql = " select c.departid, \n"
				   + " c.departname as customerName, \n"
				   + " c.sockCode, \n"
				   + " d.departname, \n"
				   + " '委托或被审计客户' as forbidInvestReason, \n"
				   + " d.organname \n"
				   + " from k_customer c \n"
				   + " left join ( \n"
				   + " select d.autoid,e.departname,e.parentid,f.departname as organname \n"
				   + " from k_department d  \n"
				   + " left join k_department e on d.fullpath like concat(e.fullpath,'%') and e.level0=1 \n"
				   + " left join k_organ f on f.departid =e.parentid \n"
				   + " )d on c.departmentid = d.autoid \n" 
				   + " where (c.sockCode>'' or c.sockCode2>'')  ${customerName} ${sockCode} ${organname} "; 
		
		pp.setSQL(sql); 
		pp.setOrderBy_CH("departid") ;
		pp.setDirection("desc");
		
		// 客户名称、客户股票编号、国际证券识别码、客户国别、客户所属机构、禁止投资原因
		
		pp.addColumn("客户名称", "customerName");
		pp.addColumn("客户股票编号", "sockCode");
		// pp.addColumn("客户所属部门", "departname");
		pp.addColumn("客户所属机构", "organname");
		pp.addColumn("禁止投资原因", "forbidInvestReason");
		

		pp.setPrintEnable(true);
	    pp.setPrintVerTical(false);
	    pp.setPrintTitle("禁用投资列表");
	    pp.setPrintColumnWidth("30,20,30,30");
	    pp.setPrintSqlColumn("customerName,sockCode,organname,forbidInvestReason");
		pp.setPrintColumn("客户名称`客户股票编号`客户所属机构`禁止投资原因");
		
		pp.addSqlWhere("customerName", " and (c.departid like '%${customerName}%' ) ");
		pp.addSqlWhere("sockCode", " and (c.sockCode like '%${sockCode}%' ) ");
		pp.addSqlWhere("organname", " and (organname like '%${organname}%' ) ");
		
		pp.setColumnWidth("20%,15%,20%,25%");
		
		request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		
		return new ModelAndView(FORBIDINVESTLIST);
	}
	
	
	
	/** 保存
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView save(HttpServletRequest request, HttpServletResponse response) throws Exception  {
		Connection conn = null;
		

		UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
		
		try {
			String paramSave = request.getParameter("paramSave");
			
			String uuid = UUID.randomUUID().toString();
			String loginName = userSession.getUserName();
			String loginId = userSession.getUserId();
			String setTime = request.getParameter("setTime");
			
			String investId = request.getParameter("investId");
			
			String userName = request.getParameter("userName");
			String relations = request.getParameter("relations");
			String answer = request.getParameter("answer");
			String ssstockNum = request.getParameter("ssstockNum");
			String ssstockNum2 = request.getParameter("ssstockNum2");
			String hsstockNum = request.getParameter("hsstockNum");
			String hsstockNum2 = request.getParameter("hsstockNum2");
			String gsstockNum = request.getParameter("gsstockNum");
			
			String[] stockCode = request.getParameterValues("stockCode");
			String[] stockName = request.getParameterValues("stockName");
			String[] stockCount = request.getParameterValues("stockCount");
			String[] stockInDate = request.getParameterValues("stockInDate");
			String[] stockOutDate = request.getParameterValues("stockOutDate");
			
			conn=new DBConnect().getConnect("");
			
			InvestManageService ims = new InvestManageService(conn);
			
			InvestManage im = new InvestManage();
			
			im.setLoginName(loginName);
			im.setLoginId(loginId);
			im.setSetTime(setTime);
			im.setInvestId(uuid);
			im.setUserName(userName);
			im.setRelations(relations);
			im.setAnswer(answer);
			im.setSsstockNum(ssstockNum);
			im.setSsstockNum2(ssstockNum2);
			im.setHsstockNum(hsstockNum);
			im.setHsstockNum2(hsstockNum2);
			im.setGsstockNum(gsstockNum);
			
			// 修改的时候 只 知道 一条记录的 autoid，不能定位修改,先删除再新增
			if("update".equalsIgnoreCase(paramSave)){
				// 删除
				ims.deleteInvestManageByInvestId(investId);
				// 修改的情况 可以 考虑 investId 的 存法： 可以用 第一次新增的 那个 investId，也可以 现在这种状况 用 UUID
			}
			
			if(stockCode!=null && !"".equals(stockCode)){
				for (int i = 0; i < stockCode.length; i++) {
					if(stockCode !=null){
						im.setStockCode(stockCode[i]);
					}
					if(stockName !=null){
						im.setStockName(stockName[i]);
					}
					if(stockCount !=null ){
						im.setStockCount(stockCount[i]);
					}
					if(stockInDate !=null){
						im.setStockInDate(stockInDate[i]);
					}
					if(stockOutDate !=null){
						im.setStockOutDate(stockOutDate[i]);
					}
					
					// 添加
					ims.addInvestManage(im);
				}
			}else{
				// 添加
				ims.addInvestManage(im);
			}
			
			response.sendRedirect(request.getContextPath()+"/investManage.do?method=list") ;
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		return null;
	}
	
	
	
	/**	跳转
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView go(HttpServletRequest request, HttpServletResponse response) throws Exception  {
		// 默认 list
		ModelAndView model = new ModelAndView(LIST);
		String paramGo = request.getParameter("paramGo");
		String autoId = request.getParameter("autoId");
		ASFuntion af = new ASFuntion();
		Connection conn = null;
		try{
			
			if("list".equalsIgnoreCase(paramGo)){
				model = new ModelAndView(LIST);
			}else if("update".equalsIgnoreCase(paramGo)){
				conn = new DBConnect().getConnect("");
				model = new ModelAndView(UPDATE);
				InvestManageService ims = new InvestManageService(conn);
				// 投资情况  单个 对象
				InvestManage im = ims.getInvestManageByAutoId(autoId);
				
				// 投资情况 list 对象
				List imList = ims.getInvestManageByInvestId(im.getInvestId());
				model.addObject("im", im);
				model.addObject("imList", imList);
				
				String p = request.getParameter("p");
				if("view".equalsIgnoreCase(p)){	// 查看 
					model.addObject("paramOpt", "view");
				} 
			}else{
				model = new ModelAndView(AddAndEdit);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		model.addObject("nowTime", af.getCurrentDate());
		return model;
	}
	
	
	
	/** 删除
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView del(HttpServletRequest request, HttpServletResponse response) throws Exception  {
		String autoId = request.getParameter("autoId");
		ASFuntion af = new ASFuntion();
		Connection conn = null;
		try{
			conn = new DBConnect().getConnect("");
			InvestManageService ims = new InvestManageService(conn);
			ims.deleteInvestManage(autoId);
				
			response.sendRedirect(request.getContextPath()+"/investManage.do?method=list") ;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		return null;
	}
	
	
	/**
	 * 查看 改 股票编号 是否 在 禁止 名单里面
	 * @throws IOException 
	 */
	public ModelAndView viewStockCode(HttpServletRequest request,HttpServletResponse response) throws IOException{
		response.setContentType("text/html;charset=utf-8");  //设置编码
		PrintWriter out = response.getWriter();
		
		String stockCode = request.getParameter("stockCode");
		
		Connection conn=null; 
		try {
			conn=new DBConnect().getConnect("");
			String strSql = " select departId from k_customer where sockCode = '"+stockCode+"' ";
			String result = new DbUtil(conn).queryForString(strSql);

			if("".equals(result) || null==result){ 
				// 当前 股票编号 没 在 禁止 名单中
				out.print("Y");
			}else{
				// 当前 股票编号 在 禁止 名单中			
				out.print("N");
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return null;
	}
	
	
	/**
	 * 是否自己发起的
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView getIsMine(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		response.setContentType("text/html;charset=utf-8");

		response.setHeader("Pragma","No-cache");
		response.setHeader("Cache-Control","no-cache");
		response.setDateHeader("Expires", 0);
		
		UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
		String userId = userSession.getUserId();
		

		Connection conn = null;

		String id = request.getParameter("id");
		
		PrintWriter out = response.getWriter();

		try{
			conn = new DBConnect().getConnect("");
			
			DbUtil db = new DbUtil(conn);
			String loginId = db.queryForString(" select loginid from K_investManage where autoid = ? ",new Object[]{id});

			
			if(userId.equals(loginId)){
				out.write("Y");
			}else{
				out.write("N");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

		return null;
	}
}
