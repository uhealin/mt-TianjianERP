package com.matech.audit.work.system;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;

import org.del.JRockey2Opp;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.exception.ExpressionSyntaxErrorException;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.autoHitSelect.AutoHitSelect;
import com.matech.audit.service.autoHitSelect.SelectOption;
import com.matech.framework.listener.UserCurTask;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.log.Log;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.FileUtil;
import com.matech.framework.pub.util.StringUtil;
import com.matech.framework.service.userDisplay.UserDisplayService;

/**
 * @author Bill
 * 
 */
public class SystemAction extends MultiActionController {

	private static Log log = new Log(SystemAction.class);

	private static final String LOGIN_VIEW = "login.jsp";
	private static final String INDEX_VIEW = "index.jsp";
	private static final String ERROR_VIEW = "system/error.jsp";
	private static final String HOME_VIEW = "home.jsp";
	private static final String OCX_DOWNLOAD_VIEW = "ocx/downloadOcx.jsp";
	private static final String SINGLE_LOGIN_VIEW = "system/singleLogin.jsp";
	private static final String MSSAGE_LIST_VIEW = "system/mssageList.jsp";
	private static final String COMPUTER_FILE_VIEW = "docDownload/form/computerFile.jsp";

	/**
	 * 关闭底稿,清空用户打开的底稿信息
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView fileClose(HttpServletRequest request, HttpServletResponse response) throws Exception {
		//取得用户Session
		HttpSession session = request.getSession();
		UserSession userSession = (UserSession) session.getAttribute("userSession");

		Set userCurTasks = userSession.getUserCurTasks();
		Iterator it = userCurTasks.iterator();
		ASFuntion asf = new ASFuntion();
		String taskId = asf.showNull(request.getParameter("taskId"));

		while(it.hasNext()) {
			UserCurTask userCurTask = (UserCurTask)it.next();

			if (taskId.equals(userCurTask.getCurTaskId())) {
				userCurTasks.remove(userCurTask);
				break;
			}
		}

		System.out.println("关闭底稿了,该用户当前打开底稿数：" + userCurTasks.size());

		return null;
	}
	
	/**
	 * 新下拉使用的方法
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @return ModelAndView
	 */
	public ModelAndView combox(HttpServletRequest request,
			HttpServletResponse response) {

		Connection conn = null;

		StringBuffer stringbuffer = new StringBuffer();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = null;
		String sql="",oralSql="";

		try {
			out = response.getWriter();
			conn = new DBConnect().getConnect();

			String autoid = StringUtil.showNull(request.getParameter("autoid"));

			// 获取前台提交的联动参数
			String refer = StringUtil.showNull(request.getParameter("refer"));
			String refer1 = StringUtil.showNull(request.getParameter("refer1"));
			String refer2 = StringUtil.showNull(request.getParameter("refer2"));
			String multilevel = StringUtil.showNull(request.getParameter("multilevel"));
			String multiselect = StringUtil.showNull(request.getParameter("multiselect"));
			String node = StringUtil.showNull(request.getParameter("node"));
			String grid = StringUtil.showNull(request.getParameter("grid"));
			String start = StringUtil.showNull(request.getParameter("start"));
			String limit = StringUtil.showNull(request.getParameter("limit"));

			String checkmode = StringUtil.showNull(request.getParameter("checkmode"));

			// 获取前台输入或者选中的PK；
			String pk1 = StringUtil.showNull(request.getParameter("pk1"));

			if ("".equals(checkmode) || "".equals(multiselect)) {
				if (pk1 != null && !"".equals(pk1))
					pk1 = pk1.replaceAll("'", "\\\\\\\\'");
			}
			if (autoid.toLowerCase().indexOf("class") > -1) {
				comboxListByClass(request, response);
				return null;
			}

			// 缓存返回的数据
			PreparedStatement ps = null;
			PreparedStatement ps2 = null;
			ResultSet rs = null;
			ResultSet rs2 = null;
			if (autoid != null && !"".equals(autoid)) {

				String strSql = "select strinitsql,strsql,strchecksql from S_AUTOHINTSELECT where id='"
						+ autoid + "'";

				ps = conn.prepareStatement(strSql);
				rs = ps.executeQuery();

				if (rs.next()) {

					String strinitsql = rs.getString(1);
					String strsql = rs.getString(2);
					String strchecksql = rs.getString(3);

					sql = strinitsql;

					if (!"".equals(node)) {
						// 多级展开时，取第二条sql
						if("root".equals(node)) {
							sql = strinitsql;
						}else {
							sql = strsql.replaceAll("\\$1", node);
						}
						
					}else if(!"".equals(pk1)) {
						sql = strsql.replaceAll("\\$1", pk1);
					}


					if (!"".equals(checkmode)) {
						sql = strchecksql.replaceAll("\\$1", pk1);
					}

					UserSession us = (UserSession) request.getSession()
							.getAttribute("userSession");
					// 增加这句话以免用户没有登陆的时候，就不能使用在线提示；
					if (us == null)
						us = new UserSession();

					String strCurUserId = us.getUserId();
					String curDepartmentid = (String) us.getUserAuditDepartmentId() ;

					if (sql != null && !"".equals(sql)) {

						// 使用\\是因为转义
						if (pk1 != null)
							sql = sql.replaceAll("\\$1", pk1);
						if (refer != null)
							sql = sql.replaceAll("\\$2", refer);
						if (refer1 != null)
							sql = sql.replaceAll("\\$3", refer1);
						if (refer2 != null)
							sql = sql.replaceAll("\\$4", refer2);

						if (strCurUserId != null)
							sql = sql.replaceAll("\\$CURUSER", strCurUserId);

						if (curDepartmentid != null)
							sql = sql.replaceAll("\\$CURDEPARTMENTID",
									curDepartmentid);
						
						
						sql = StringUtil.transSessionValue(request.getSession(), sql);
						
						log.debug("下拉ID:" + autoid + ",下拉SQL:\n" + sql);

						oralSql = sql;

						oralSql = " select count(1) from (" + oralSql + ") a ";
						ps2 = conn.prepareStatement(oralSql);
						rs2 = ps2.executeQuery();

						int rowCount = 0;
						if (rs2.next()) {
							rowCount = rs2.getInt(1);
						}

						if (!"".equals(start) && !"".equals(limit)) {

							int startNum = Integer.parseInt(start);
							int limitNum = Integer.parseInt(limit);

							sql = sql + " limit " + startNum + "," + limitNum ;

						}
						
						if ("true".equals(grid)) {

							// 下拉表格

							String head = StringUtil.showNull(request.getParameter("head"));

							ps = conn.prepareStatement(sql);
							rs = ps.executeQuery();

							if (!"".equals(head)) {
								// 请求框架
								List columnList = new ArrayList();
								List fieldList = new ArrayList();
								ResultSetMetaData rsms = ps.getMetaData();

								for (int i = 1; i <= rsms.getColumnCount(); i++) {
									String columnName = rsms.getColumnLabel(i)
											.toLowerCase();
									
									if("COMBOX_ROWNUM".equalsIgnoreCase(columnName)) {
										continue ;
									}
									
									String renderer = "";
									
									switch (rsms.getColumnType(i)) {
										case Types.NUMERIC:
											//金额数字
											renderer = "function(v){ var value = mt_select_formatNumber(v); return value;}";
											break; 
	
										default:
											break;
									}
									

									Map fieldMap = new LinkedHashMap();
									fieldMap.put("name", columnName);
									fieldList.add(fieldMap);

									Map columnMap = new LinkedHashMap();
									columnMap.put("id", columnName);
									columnMap.put("header", columnName);
									columnMap.put("sortable", false);
									columnMap.put("dataIndex", columnName);
									
									
									if(!"".equals(renderer)) {
										columnMap.put("renderer", renderer);
									}
									
									if(columnName.indexOf("hidden_") > -1) {
										columnMap.put("hidden", true) ; 
										columnMap.put("hideable", false) ; 
									}
									
									columnList.add(columnMap);

								}

								String fieldStr = JSONArray.fromObject(
										fieldList).toString();
								String columnStr = JSONArray.fromObject(
										columnList).toString();
								
								Map jsonMap = new LinkedHashMap();
								jsonMap.put("fields", fieldStr);
								jsonMap.put("columns", columnStr);
								String jsonStr = JSONArray.fromObject(jsonMap)
										.toString();
								
								
								stringbuffer.append(jsonStr);

							} else {
								// 请求数据

								String jsonStr = "{totalProperty:" + rowCount
										+ ",data:";

								List resultList = new ArrayList();
								ResultSetMetaData rsms = ps.getMetaData();
								while (rs.next()) {
									Map map = new LinkedHashMap();
									for (int i = 1; i <= rsms.getColumnCount(); i++) {
										String field = rsms.getColumnLabel(i)
												.toLowerCase();
										
										if("COMBOX_ROWNUM".equalsIgnoreCase(field)) {
											continue ;
										}
										
										map.put(field,  DbUtil.getStringValue(rs.getObject(field), rsms.getColumnType(i)));
									}
									resultList.add(map);
								}

								String resultStr = JSONArray.fromObject(
										resultList).toString();
								jsonStr += resultStr + "}";
								stringbuffer.append(jsonStr);
							}

						} else {

							String jsonStr = "{totalProperty:" + rowCount
									+ ",data:";
							List resultList = new ArrayList();
							ps = conn.prepareStatement(sql);
							rs = ps.executeQuery();
							while (rs.next()) {
								String value = rs.getString(1);
								String text = rs.getString(2);

								Map map = new LinkedHashMap();
								map.put("value", value);
								map.put("text", text);
								map.put("id", value);

								if (multilevel != null
										&& !"".equals(multilevel)) {
									// 求出树是否有下级节点
									String sqlTemp = strsql.replaceAll("\\$1",
											value);
									ps = conn.prepareStatement(sqlTemp);
									rs2 = ps.executeQuery();
									if (rs2.next()) {
										map.put("leaf", false);
									} else {
										map.put("leaf", true);
										
									}

									if (!"".equals(multiselect)) {
										// 多级多选，增加一个多选框，默认不选中
										if(((","+pk1+",").indexOf("," + value + ",") > -1)) {
											map.put("checked", true);
										}else {
											map.put("checked", false);
										}
										
									}
								}

								resultList.add(map);
							}
							
							String resultStr = JSONArray.fromObject(resultList)
									.toString();
							jsonStr += resultStr + "}";
							if (!"".equals(multilevel) || !"".equals(checkmode)) {
								stringbuffer.append(resultStr);
							} else {
								stringbuffer.append(jsonStr);
							}
							
							// System.out.println("combox:"+jsonStr);
						}

					}
				} else {

					// 没有找到，就返回出错信息
					stringbuffer.append("ERROR|指定的AUTOID" + autoid + "不存在|");
				}
			}

		} catch (Exception e) {
			log.debug("下拉出错sql="+sql);
			log.debug("出错时的oralSql="+oralSql);
			e.printStackTrace();
			stringbuffer.append("ERROR|出错原因:" + e.getMessage() + "|");
		} finally {
			DbUtil.close(conn);
		}
		out.write(stringbuffer.toString());
		return null;
	}

	/**
	 * 根据类加载出下拉数据
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView comboxListByClass(HttpServletRequest request,
			HttpServletResponse response) {

		response.setContentType("text/html;charset=utf-8");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);

		try {
			PrintWriter out = response.getWriter();

			String refer = request.getParameter("refer");
			String refer1 = request.getParameter("refer1");

			if (refer == null) {
				return null;
			}

			AutoHitSelect autoHitSelect = (AutoHitSelect) Class.forName(refer)
					.newInstance();

			List<SelectOption> list = autoHitSelect.getList(refer1);

			int totalProperty = list.size();
			// for (int i = 0; i < list.size(); i++) {
			// SelectOption selectOption = list.get(i);
			// if (selectOption.getText().indexOf(pk1) > -1) {
			// list.remove(i);
			// totalProperty --;
			// }
			// }

			String jsonStr = "{totalProperty:" + totalProperty + ",data:";

			String resultStr = JSONArray.fromObject(list).toString();

			jsonStr += resultStr + "}";
			out.write(jsonStr);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 查找文件
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView listFile(HttpServletRequest request,
			HttpServletResponse response) {

		response.setContentType("text/html;charset=utf-8");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);

		try {
			PrintWriter out = response.getWriter();

			StringBuffer stringbuffer = null;

			String pk1 = StringUtil.showNull(request.getParameter("pk1"));

			String refer1 = request.getParameter("refer1");
			String refer2 = request.getParameter("refer2");

			if (refer1 == null) {
				return null;
			}

			File file = new File(refer1);
			if (file.exists() && file.isDirectory()) {
				stringbuffer = new StringBuffer("OK|");
				stringbuffer.append(pk1 + "|");

				String[] fileNames = FileUtil.getFilesAndDir(refer1, refer2);

				for (int i = 0; i < fileNames.length; i++) {
					if (fileNames[i].indexOf(pk1) > -1) {
						stringbuffer.append(fileNames[i]).append("`").append(
								"文件").append("|");
					}
				}
			}

			out.write(stringbuffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 跳到ocx下载页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView toDownloadOcx(HttpServletRequest request,
			HttpServletResponse response) {

		ModelAndView modelAndView = new ModelAndView(OCX_DOWNLOAD_VIEW);

		return modelAndView;
	}


	/**
	 * 错误页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView error(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String code = request.getParameter("code");
		ModelAndView modelAndView = new ModelAndView(ERROR_VIEW);

		String title = "系统异常";
		String ex = "系统异常";
		String errorMessage = "系统异常";

		if ("404".equals(code)) {
			title = "页面不存在";
			ex = "404错误";
			errorMessage = "404错误";
		} else if ("500".equals(code)) {
			title = "服务器内部错误";
			ex = "500错误";
			errorMessage = "500错误";
		}

		modelAndView.addObject("title", title);
		modelAndView.addObject("ex", ex);
		modelAndView.addObject("errorMessage", errorMessage);
		return modelAndView;
	}

	
	/**
	 * 通用后台验证方法
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @return ModelAndView
	 */
	public ModelAndView validate(HttpServletRequest request,
			HttpServletResponse response) {

		Connection conn = null;
		PreparedStatement ps = null ;
		ResultSet rs = null ;
		ResultSet rs2 = null ;

		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = null;
		String sql="",vSql="";

		try {
			out = response.getWriter();
			conn = new DBConnect().getConnect();

			String validateId = StringUtil.showNull(request.getParameter("validateId"));
			String refer = StringUtil.showNull(request.getParameter("refer"));
			String value = StringUtil.showNull(request.getParameter("value"));
			
			if(!"".equals(validateId)) {
				sql = " select vsql,tips,expr from mt_sys_validation where id=? " ;
				ps = conn.prepareStatement(sql) ;
				ps.setString(1,validateId) ;
				rs = ps.executeQuery() ;
				if(rs.next()) {
					vSql = StringUtil.showNull(rs.getString(1)) ; //验证sql
					String tips = StringUtil.showNull(rs.getString(2)) ;
					String expr = StringUtil.showNull(rs.getString(3)) ;
					
					vSql = vSql.replaceAll("\\$1", value) ;
					expr = expr.replaceAll("\\$1", value) ;
					tips = tips.replaceAll("\\$1", value) ;
					
					vSql = replaceRefer(vSql, refer, "|") ;
					expr = replaceRefer(expr, refer, "|") ;
					tips = replaceRefer(tips, refer, "|") ;
					
					
					if(!"".equals(expr)) {
						//如果表达式存在，就根据表达式返回结果验证
						
						ps = conn.prepareStatement(vSql) ;
						rs2 = ps.executeQuery() ;
						if(rs2.next()) {
							
							String[] varibles = StringUtil.getVaribles(expr) ;
							for(int i=0;i<varibles.length;i++) {
								String varible = varibles[i] ;
								String varibleValue = StringUtil.showNull(rs2.getString(varible)) ;
								
								expr = expr.replaceAll("\\$\\{" + varible + "\\}",
										varibleValue);
							}
							
							String[] tipVaribles = StringUtil.getVaribles(tips) ;
							for(int i=0;i<tipVaribles.length;i++) {
								String varible = tipVaribles[i] ;
								String varibleValue = StringUtil.showNull(rs2.getString(varible)) ;
								
								tips = tips.replaceAll("\\$\\{" + varible + "\\}",
										varibleValue);
							}
						}
						
						try{
							log.debug("验证表达式:"+expr) ;
							Boolean result = (Boolean) AviatorEvaluator.execute(expr);
							if(result) {
								out.write("ok") ;
							}else {
								out.write(tips) ;
							}
						}catch(ExpressionSyntaxErrorException e) {
							out.write("验证表达式出现语法错误,原因："+e.getMessage()) ;
							return null ;
						}
						
						out.close();
					}else {
						//检查sql有没有记录,这里是没有记录则验证通过
						
						ps = conn.prepareStatement(vSql) ;
						log.debug("验证sql:"+vSql) ;
						rs2 = ps.executeQuery() ;
						if(rs2.next()) {
							out.write(tips) ;
						}else {
							out.write("ok") ;
						}
					}
				}else {
					out.write("没有找到validateId=" + validateId + "的记录!") ;
				}
			}
			
		} catch (Exception e) {
			log.debug("验证出错sql="+vSql);
			e.printStackTrace();
			out.write("验证后台发生错误,出错原因:" + e.getMessage()) ;
		} finally {
			DbUtil.close(conn);
			if(out != null) out.close() ;
		}
		return null;
	}
	
	public String replaceRefer(String str,String refer,String seperator) {
		
		if(refer == null || "".equals(refer)) {
			return str ;
		}
		
		String[] referArr = refer.split("\\"+seperator) ;
		for(int i=0;i<referArr.length;i++) {
			str = str.replaceAll("\\$"+(i+2), referArr[i]) ;
		}
		return str ;	
	}
	
	/**
	 * 退出系统
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView exitSystem(HttpServletRequest request, HttpServletResponse response) throws Exception{
		response.setContentType("text/html;charset=utf-8");
		PrintWriter printWriter = response.getWriter();

		printWriter.print("<script>");
		printWriter.print("	try {");
		printWriter.print("		parent.bottomFrame.statu.value=\"exitSystem\";");
		printWriter.print("	}catch(e){");
		printWriter.print("	}");
		printWriter.print("</script>");

		HttpSession session = request.getSession();

		Connection conn=null;

		try{
			session.invalidate();

		}catch(Exception e){
			e.printStackTrace();

		}finally{
			DbUtil.close(conn);
			//printWriter.close();
			//读取服务器的狗信息
			Map dogInfo = JRockey2Opp.getInfoFromDog();
			String sysVn = null;
			if (dogInfo != null && dogInfo.get("sysVn")!=null ) {
				sysVn = (String) dogInfo.get("sysVn");
				if(sysVn!=null&&!"".equals(sysVn)&&sysVn.indexOf("E-OA")>=0) {
					printWriter.print("<script>");
					printWriter.print("	try {");
					printWriter.print("		parent.window.close();");
					printWriter.print("	}catch(e){");
					printWriter.print("	}");
					printWriter.print("</script>");
					return null;
				}
			} 
			response.sendRedirect("login.jsp");
		}

		return null;
	}
}
