package com.matech.audit.work.question;

import java.io.PrintWriter;
import java.security.Policy;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.expert.ExpertService;
import com.matech.audit.service.expert.model.Expert;
import com.matech.audit.service.question.QuestionMan;
import com.matech.audit.service.question.QuestionService;
import com.matech.audit.service.question.TreeView;
import com.matech.audit.service.question.model.Answer;
import com.matech.audit.service.question.model.Question;
import com.matech.audit.service.toHTML.ToHTML;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class QuestionAction extends MultiActionController {
	private final static String MAIN_VIEW = "/question/List.jsp";
	private final static String _MAIN_VIEW = "/question/main.jsp";
	private final static String VIEW = "/question/viewSZX.jsp";
	private final static String UPDATE_VIEW = "/Policy/update.jsp";
	
	public ModelAndView getTree(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		Connection conn = null; 
		response.setContentType("text/html;charset=UTF-8") ;
		try{
			ASFuntion CHF = new ASFuntion();
		  	conn = new DBConnect().getConnect("");
			String id = CHF.showNull(request.getParameter("pid"));
			if("".equals(id)){
				id="1";
			}
			String tablename = CHF.showNull(request.getParameter("tablename"));
			TreeView tree = new TreeView(conn);
			String json = "{}";
			List list = null;
			if("".equals(tablename)){
				List list1 = tree.getJsonTree("p_Questiontype", id);
				//List list2 = tree.getJsonTree("p_casestype", id);
				list = new ArrayList();
				for(int i=0;i<list1.size();i++){
					list.add(list1.get(i));
				}
				//for(int i=0;i<list2.size();i++){
				//	list.add(list2.get(i));
				//}
				
				
				Map map1 = new HashMap();
				map1.put("id", "00003");
				map1.put("text", "我提问的问题");
				map1.put("leaf", "1");
				map1.put("cls", "folder");
				map1.put("pid", "00003");
				map1.put("tablename", tablename);
				list.add(map1);
				
				//list.add(e);
			}else{
				list = tree.getJsonTree(tablename,id);	
			}
			
			if(list != null){
				json = JSONArray.fromObject(list).toString();
			}
			String treeStr = JSONArray.fromObject(list).toString() ;
			response.getWriter().write(treeStr); 
		}catch(Exception e){
			throw e;
		}finally{
			 DbUtil.close(conn);
		}
		return null;
	}
	
	
	
	public void del(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		Connection conn =null;
		try{
			conn = new DBConnect().getConnect(""); 
			new QuestionMan(conn).delAQuestion(request.getParameter("chooseValue"));
			
			response.setContentType("text/html;charset=utf-8");
			PrintWriter out = response.getWriter();
			out.write("OK");
			out.close();
		}catch(Exception e){
			throw e;
		}finally{
			 DbUtil.close(conn);
		}
	}
	
	public ModelAndView serchQuestion(HttpServletRequest request, HttpServletResponse response)  throws Exception {
	 
		ModelAndView modelAndView = new ModelAndView(request.getContextPath()+"/questionsearch/Search.jsp");
		ASFuntion asf = new ASFuntion();
		
		String searchArea = asf.showNull(request.getParameter("searchArea"));//要查的内容
		String ifOne = asf.showNull(request.getParameter("ifOne"));  //判断用的参数
		modelAndView.addObject("searchArea",searchArea);
		modelAndView.addObject("ifOne",ifOne);
		return modelAndView;
	}
//2012-4-25移植省注协	
	public ModelAndView getTreeSZX(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		Connection conn = null; 
		response.setContentType("text/html;charset=UTF-8") ;
		ASFuntion asf = new ASFuntion();
		try { 
			conn = new DBConnect().getConnect("");
			
			String id = request.getParameter("id") ;
			String type = request.getParameter("value") ;
			String mine = asf.showNull(request.getParameter("mine")) ;
			if(id == null || "".equals(id)) {
				id = "0" ;
			}
			if(type == null ) {
				type = "" ;
			}
			if(!"".equals(mine)) {
				UserSession userSession = (UserSession)request.getSession().getAttribute("userSession") ;
				mine = ""+userSession.getUserId();
			}
			
			List list = new QuestionService(conn).getTree(id,type,mine) ;
			String treeStr = JSONArray.fromObject(list).toString() ;
			response.getWriter().write(treeStr); 
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			DbUtil.close(conn) ;
		}
		return null;
	}
	
	public ModelAndView mainSZX(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		Connection conn = null; 
		ModelAndView modelAndView = new ModelAndView(_MAIN_VIEW) ;
		ASFuntion asf = new ASFuntion();
		try { 
			conn = new DBConnect().getConnect("");
			String sql = "select a.id,title,question,b.name,createDate,a.state,fullPath,rewardMark from p_questionSZX a left join k_user b " 
					  +"on a.userId = b.id where 1=1 ${typeIds} ${mine} ${ctype} ${searchQuestion}" ;
			
			DataGridProperty pp = new DataGridProperty() {
				public void onSearch(HttpSession session,HttpServletRequest request,
						HttpServletResponse response) throws Exception {
						Connection conn = null ;
					try { 
						conn = new DBConnect().getConnect("");
						UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
						String ctype = getRequestValue("ctype") ;
						String fullPath = getRequestValue("fullPath") ;
						QuestionService qs = new QuestionService(conn) ;
						String typeIds = "";
						if("policy".equals(ctype)) {
							typeIds = qs.getPolicyTypeIds(fullPath) ;
						}else if("case".equals(ctype)) {
							typeIds = qs.getCaseTypeIds(fullPath) ;
						}else if("course".equals(ctype)) {
							typeIds = "" ;
						}else {
							typeIds = "" ;
						}
						
						if(typeIds == null || "".equals(typeIds)) {
							typeIds = "" ;
						}else {
							typeIds = " and typeId in ("+typeIds+")";
						}
						
						if(ctype == null || "".equals(ctype)) {
							ctype = "" ;
						}else {
							ctype = " and a.ctype = '"+ctype+"'";
						}
						this.setOrAddRequestValue("typeIds", typeIds);
						this.setOrAddRequestValue("ctype", ctype);
						
						String mine = getRequestValue("mine") ; //是否只显示我的问题
						if(mine == null || "".equals(mine)) {
							mine = "" ;
						}else {
//							mine = " and userId = '"+userSession.getUserMap().get("loginid")+"'";
							mine = " and userId = '"+userSession.getUserId()+"'";
						} 
						this.setOrAddRequestValue("mine", mine);
						
						String searchQuestion = getRequestValue("searchQuestion") ;
						if(searchQuestion == null || "".equals(searchQuestion)) {
							searchQuestion = "" ;
						}else {
							searchQuestion = " and title like '%"+searchQuestion+"%'";
						} 
						this.setOrAddRequestValue("searchQuestion", searchQuestion);
					
					} catch (Exception e) {
						e.printStackTrace();
					}finally {
						DbUtil.close(conn) ;
					}
				}
			};
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
			String mine = asf.showNull(request.getParameter("mine")) ;
			
			if(!"".equals(mine)) {
//				sql += " and userId = '"+userSession.getUserMap().get("loginid")+"'" ;
				sql += " and userId = '"+userSession.getUserId()+"'" ;
			}
			
			pp.addSqlWhere("typeIds","${typeIds}") ;
			pp.addSqlWhere("mine","${mine}") ;
			pp.addSqlWhere("ctype","${ctype}") ;
			pp.addSqlWhere("fullPath","${fullPath}") ;
			pp.addSqlWhere("searchQuestion","${searchQuestion}") ;
			pp.setTableID("questionList");
			pp.setWhichFieldIsValue(1);
//			pp.addColumn("标题", "title");//.setTdProperty(" onclick='view(\"${id}\");'");
			pp.addColumn("标题","title",null,null,"<a href=# onclick=\"show('${id}');\">${value}</a>") ;
			pp.addColumn("状态", "state");
			pp.addColumn("最后更新日期", "createDate");
			pp.addColumn("提问人", "name");
			pp.addColumn("悬赏分", "rewardMark");
			
			pp.setTrActionProperty(true);
			pp.setTrAction("style=\"cursor:hand;\" onclick='view(\"${id}\");'  ");
			
			pp.setColumnWidth("20,10,20,10,10");
		
			pp.setSQL(sql);
			pp.setOrderBy_CH("createDate");
			pp.setDirection("desc");
			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
			
			modelAndView.addObject("mine", mine) ;
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			DbUtil.close(conn) ;
		}
		return modelAndView;
	}
	
	public ModelAndView view(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		Connection conn = null; 
		ModelAndView modelAndView = new ModelAndView(VIEW) ;
		try { 
			conn = new DBConnect().getConnect("") ;
			
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
//			String loginId = (String)userSession.getUserMap().get("loginid");
			String loginId=userSession.getUserId();
			
			String id = request.getParameter("id") ;
			QuestionService qs = new QuestionService(conn) ;
			Question question = qs.getQuestion(id) ;
			List<Answer> list = qs.getAnswers(id) ;
			Answer bestAnswer = qs.getBestAnswer(id) ;
			boolean isAnswer = qs.isAnswer(id, loginId);
			
			modelAndView.addObject("question", question) ;
			modelAndView.addObject("answers", list) ;
			modelAndView.addObject("bestAnswer", bestAnswer) ;
			modelAndView.addObject("isAnswer", isAnswer) ;
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			DbUtil.close(conn) ;
		}
		return modelAndView;
	}
	
	public ModelAndView addView(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		ModelAndView modelAndView = new ModelAndView("/question/add.jsp") ;
		ASFuntion asf = new ASFuntion();
		String ctype = 	asf.showNull(request.getParameter("ctype")) ;
		String typeId = asf.showNull(request.getParameter("typeId")) ;
		
		Connection conn = null; 
		
		try {	
			conn = new DBConnect().getConnect("") ;
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession") ;
//			String loginId = userSession.getUserMap().get("loginid")+"";
			String loginId =userSession.getUserId();
			ExpertService gradeService = new ExpertService(conn);
			Expert grade = gradeService.getGradeByLoginId(loginId);
			
			modelAndView.addObject("grade", grade) ;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn) ;
		}
		
		modelAndView.addObject("ctype", ctype) ;
		modelAndView.addObject("typeId", typeId) ;
		return modelAndView;
	}
	
	public ModelAndView save(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		Connection conn = null; 
		ASFuntion asf = new ASFuntion();
		try {
			request.setCharacterEncoding("UTF-8");
			conn = new DBConnect().getConnect("") ;
			String questionContent = asf.showNull(request.getParameter("question")).replaceAll("<p>","<br>") ;
			String title = asf.showNull(request.getParameter("title"));
			String rewardMark = asf.showNull(request.getParameter("rewardMark")) ;
			String ctype = asf.showNull(request.getParameter("ctype")) ;
			String typeId = asf.showNull(request.getParameter("typeId")) ;
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession") ;
			
//			String loginId = userSession.getUserMap().get("loginid")+"";
			String loginId=userSession.getUserId();
			
			Question question = new Question();
			question.setCtype(ctype) ;
			question.setUserId(loginId);
			question.setTypeId(typeId) ;
			question.setTitle(title) ;
			question.setQuestion(questionContent) ;
			question.setState("未解决") ;
			question.setRewardMark(rewardMark) ;
			question.setCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date())) ;
			
			//扣掉资源分
			QuestionService qs = new QuestionService(conn) ;
			String questionId = qs.add(question) ;
			
			int intRewardMark = Integer.parseInt(rewardMark);
			ExpertService gradeService = new ExpertService(conn);
			Expert grade = gradeService.getGradeByLoginId(loginId);
			int resource = grade.getResource() - intRewardMark;
			grade.setResource(resource);
			gradeService.update(grade);
			
			ToHTML tohtml = new ToHTML() ;
			if("cases".equals(ctype)) {
				//重新生成案例静态文件
//				String url = "http://"+request.getServerName() + ":"+request.getServerPort()+request.getContextPath()+"/common/case.do?method=view&id="+typeId;
//				String path = request.getSession().getServletContext().getRealPath("/")+"common/caseHtml/" ;
				String url = "http://"+request.getServerName() + ":"+request.getServerPort()+request.getContextPath()+"/case.do?method=view&id="+typeId;
				String path = request.getSession().getServletContext().getRealPath("/")+"caseHtml/" ;
				tohtml.convertToHtml(url,typeId+".html",path) ;
			}else if("policy".equals(ctype)) {
				//重新生成法规静态文件
//				String url = "http://"+request.getServerName() + ":"+request.getServerPort()+request.getContextPath()+"/common/policy.do?method=view&id="+typeId;
//				String path = request.getSession().getServletContext().getRealPath("/")+"common/policyHtml/" ;
				String url = "http://"+request.getServerName() + ":"+request.getServerPort()+request.getContextPath()+"/policy.do?method=view&id="+typeId;
				String path = request.getSession().getServletContext().getRealPath("/")+"policyHtml/" ;
				tohtml.convertToHtml(url,typeId+".html",path) ;
			}
			
			response.sendRedirect(request.getContextPath()+"/question.do?method=view&id="+questionId);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			DbUtil.close(conn) ;
		}
		return null;
	}
	
	
	
	public ModelAndView addAnswer(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		Connection conn = null; 
		ASFuntion asf = new ASFuntion();
		try { 
			conn = new DBConnect().getConnect("") ;
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession") ;
			String answerContent = asf.showNull(request.getParameter("answer"));
			String questionId = request.getParameter("questionId") ;
			Answer answer = new Answer() ;
			answer.setAnswer(answerContent.replaceAll("\r\n", "</br>")) ;
			answer.setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())) ;
			answer.setQuestionId(questionId) ;
			
//			String loginId = (String)userSession.getUserMap().get("loginid");
			String loginId=userSession.getUserId();
			answer.setUserId(loginId) ;
			
			QuestionService qs = new QuestionService(conn) ;
			qs.add(answer) ;
			
			ExpertService gradeService = new ExpertService(conn);
			
			//要进行回答用户的资源分加分
			Expert grade = gradeService.getGradeByLoginId(loginId);
			int resource = grade.getResource() + 2;
			grade.setResource(resource);
			gradeService.update(grade);
			
			response.sendRedirect(request.getContextPath()+"/question.do?method=view&id="+questionId);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			DbUtil.close(conn) ;
		}
		return null;
	}
	
	/**
	 * 修改回答
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView updateAnswer(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		Connection conn = null; 
		try { 
			conn = new DBConnect().getConnect("") ;
			String updateAnswerId = request.getParameter("updateAnswerId") ;
			String updateAnswer = request.getParameter("updateAnswer") ;
			
			
			QuestionService qs = new QuestionService(conn) ;
			Answer answer = qs.getAnswer(updateAnswerId);
			answer.setAnswer(updateAnswer);
			answer.setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			
			qs.updateAnswer(answer);
		
			String questionId = answer.getQuestionId();
			
			response.sendRedirect(request.getContextPath()+"/question.do?method=view&id="+questionId);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			DbUtil.close(conn) ;
		}
		return null;
	}
	
	/**
	 * 修改回答
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView saveExplan(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		Connection conn = null; 
		try { 
			conn = new DBConnect().getConnect("") ;
			String questionId = request.getParameter("questionId") ;
			String explan = request.getParameter("explan");		
			
			QuestionService qs = new QuestionService(conn) ;
			
			Question question = qs.getQuestion(questionId);
			question.setExplan(explan);
			question.setExplanDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			
			qs.updateQuestion(question);
			
			response.sendRedirect(request.getContextPath()+"/question.do?method=view&id="+questionId);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			DbUtil.close(conn) ;
		}
		return null;
	}
	
	/**
	 * 没有答案
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView noAnswer(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		Connection conn = null; 

		String questionId = request.getParameter("questionId") ;
		
		try { 
			conn = new DBConnect().getConnect("") ;
			
			QuestionService questionService = new QuestionService(conn) ;
			
			//将问题状态修改成已解决
			Question question = questionService.getQuestion(questionId);
			question.setState("没有答案");
			questionService.updateQuestion(question);
			
			response.sendRedirect(request.getContextPath() + "/question.do?method=view&id=" + questionId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn) ;
		}
		
		return null;
	}
	
	/**
	 * 最佳答案
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView bestAnswer(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		Connection conn = null; 
		
		try { 
			conn = new DBConnect().getConnect("") ;
			
			String answerId = request.getParameter("answerId") ;
			String questionId = request.getParameter("questionId") ;
			
			QuestionService questionService = new QuestionService(conn) ;
			
			//更新为最佳答案
			Answer answer = questionService.getAnswer(answerId);
			answer.setIsBest("1");
			questionService.updateAnswer(answer);
			
			//将问题状态修改成已解决
			Question question = questionService.getQuestion(questionId);
			question.setState("已解决");
			questionService.updateQuestion(question);
	
			String answerUserId = answer.getUserId();
			
			ExpertService gradeService = new ExpertService(conn);
			
			//取得回答用户的积分，要进行加分
			Expert grade = gradeService.getGradeByLoginId(answerUserId);
			
			//本次问题的悬赏分
			int rewardMark = Integer.parseInt(question.getRewardMark());
			
			//专家分+2,资源分+ 悬赏分
			int expert = grade.getExpert() + 2;
			int resource = grade.getResource() + rewardMark;
			grade.setExpert(expert);
			grade.setResource(resource);
			gradeService.update(grade);
		
			response.sendRedirect(request.getContextPath() + "/question.do?method=view&id=" + questionId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn) ;
		}
		
		return null;
	}
}
