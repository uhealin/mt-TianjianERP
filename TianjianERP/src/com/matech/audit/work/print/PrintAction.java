package com.matech.audit.work.print;

import java.io.File;
import java.sql.*;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.employment.EmploymentService;
import com.matech.audit.service.fileupload.MyFileUpload;
import com.matech.audit.service.form.model.FormVO;
import com.matech.audit.service.manuscript.ManuFileService;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.autocode.DELUnid;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.excel.SaveAsExcel;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.StringUtil;
import com.matech.framework.pub.util.UTILString;
import com.matech.framework.service.print.PrintSetup;


/**
 * 1、模板设置{list add}
 * 2、打印{选模板，打印}
 */
public class PrintAction extends MultiActionController {
	
	private final String LIST = "print/list.jsp"; //模板list
	private final String EDIT = "print/edit.jsp"; //新增模板
	private final String OPENFILE = "Excel/tempdata/PrintandSave.jsp";
	
	/**
	 * 选择模板打印
	 */
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(LIST) ;
		DataGridProperty pp = new DataGridProperty();
		String tablename = "tt_" + DELUnid.getNumUnid();
		
		pp.setCustomerId("");
		pp.setTableID(tablename);
		pp.setInputType("radio");
		pp.setWhichFieldIsValue(1);	
		
		pp.setPrintEnable(true);
		pp.setPrintVerTical(false);
		pp.setPrintColumnWidth("20,20,20");
		pp.setPrintTitle("报表模板");

		pp.addColumn("报表模板名称", "templatename");
		pp.addColumn("最后修改人", "name");
		pp.addColumn("最后修改时间", "lasttime");
		
		String sql ="select a.*,b.name from mt_print_table a left join k_user b on a.lastname = b.id where 1=1 and (property is null or property = '') ";
		
		pp.setSQL(sql);
		pp.setOrderBy_CH("orderid,uuid");
		pp.setDirection("asc,asc");
		
		request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		
		modelAndView.addObject("tablename", tablename);
		return modelAndView ;
	}
	
	public ModelAndView edit(HttpServletRequest request,HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(EDIT) ;
		Connection conn=null;
		try {
			ASFuntion CHF=new ASFuntion();
			String uuid = CHF.showNull(request.getParameter("uuid"));
			Map map = new HashMap();
			String iCount = "1";
			if(!"".equals(uuid)){
				conn = new DBConnect().getConnect("");
				DbUtil db = new DbUtil(conn);
				
				map = db.get("mt_print_table", "uuid", uuid);
				iCount = CHF.showNull(db.queryForString("select max(orderid*1) + 1 as iCount from mt_print_field where formid = ?", new String[]{uuid}));
				if("".equals(iCount)) iCount = "1";
			}
			
			modelAndView.addObject("flagOpt", DELUnid.getNumUnid());
			modelAndView.addObject("iCount", iCount);
			modelAndView.addObject("map", map);
		} catch (Exception e) {
			e.printStackTrace();
            throw e;
		} finally {
			DbUtil.close(conn);
		}
		return modelAndView ;
	}
	
	//保存
	public void save(HttpServletRequest request,HttpServletResponse response) throws Exception {
		Connection conn=null;
		try {
			response.setContentType("text/html;charset=utf-8");
			
			ASFuntion CHF=new ASFuntion();
			String menuid = CHF.showNull(request.getParameter("menuid"));
			String uuid = CHF.showNull(request.getParameter("uuid"));
			String templatename = CHF.showNull(request.getParameter("newfilename"));
			//String newfilename = CHF.showNull(request.getParameter("newfilename"));
			String flagOpt = CHF.showNull(request.getParameter("flagOpt")); //批次号
			String userId = CHF.showNull(request.getParameter("userId"));
			String act = "add";
			
			System.out.println(uuid+"|" + templatename);
			conn = new DBConnect().getConnect("");
			DbUtil db = new DbUtil(conn);
			Map map = new HashMap(); //主表
			
			if("".equals(uuid)){
				//新增 [主表]
				uuid = StringUtil.getUUID();
				map.put("uuid", uuid);
				map.put("templatename", templatename);
				map.put("lastname",userId);  
				map.put("lasttime",StringUtil.getCurDateTime());

				db.add("mt_print_table", "", map);
			}else{
				act = "update";
				//修改[主表]
				map.put("uuid", uuid);
				map.put("templatename", templatename);
				map.put("lastname",userId);  
				map.put("lasttime",StringUtil.getCurDateTime());
				db.update("mt_print_table", "uuid", map);
			}
			
			/**
			 * 保存模板EXCEL
			 */
			MyFileUpload fileUpload = new MyFileUpload(request, conn);
			String strDir = SaveAsExcel.getExeclTemplateDir() + "/";
			fileUpload.UploadFile(uuid, strDir);
			
			/**
			 * 通过临时表tt_${flagOpt}
			 * 修改主表 【enname  FSQL】
			 * 修改从表【mt_print_field】：删除所有，然后重新插入
			 */
			//1、清 理mt_print_field表
			db.del("mt_print_field", "formid", uuid);
			
			//2、跟据[临时表tt_${flagOpt}],插入【mt_print_field】
			String sql = "insert into mt_print_field (uuid, formid, excelname, tabletype,tablename, fieldname,  OrderID ) " +
			"	select uuid() as uuid,b.uuid as formid,a.excelname,  " +
			"	SUBSTRING_INDEX(SUBSTRING_INDEX(a.excelname,'.',1),'.',-1) as tabletype, " +
			"	SUBSTRING_INDEX(SUBSTRING_INDEX(a.excelname,'.',2),'.',-1) as tablename, " +
			"	SUBSTRING_INDEX(SUBSTRING_INDEX(a.excelname,'.',3),'.',-1) as fieldname, " +
			"	SUBSTRING_INDEX(SUBSTRING_INDEX(a.excelname,'.',4),'.',-1) as OrderID " +
			"	from tt_"+flagOpt+" a,mt_print_table b  " +
			"	where b.uuid = '"+uuid+"' "; 
			db.execute(sql);
			
			sql = "update mt_print_field a,mt_com_form b,mt_com_form_query c " +
			"	set a.enname = c.enname,a.tablename = b.tablename " +
			"	where 1=1 " +
			"	and a.formid = '"+uuid+"' " +
			"	and b.form_type ='"+EmploymentService.UUID_FORM_TYPE_SUBSET+"'  " +
			"	and a.tablename = b.name " +
			"	and b.uuid = c.FORMID " +
			"	and a.fieldname = c.name ";
			db.execute(sql);
			
			//判断有没有主表，没有就返回失败
			sql = "select tablename from mt_print_field where formid = '"+uuid+"' and tabletype = '主表' limit 1";
			String sqlTable = StringUtil.showNull(db.queryForString(sql)); //主表
			if(!"".equals(sqlTable)){
				//3、修改主表 【enname  FSQL】
				String leftJoin = "",select = "",fsql = "";//读出【副表】写 left join
				sql = "select distinct tablename from mt_print_field where formid = '"+uuid+"' and tabletype = '副表' ";
				List<Map> lsts = db.getList(sql);
				for(Map lst :lsts){
					String table = StringUtil.showNull(lst.get("tablename"));
					leftJoin += "	left join " + table + " as st_" + table + " on st_"+table+".userid = a.id ";
				}
				
				List<Map> fields = db.getList("mt_print_field", "formid", uuid);
				for(Map field :fields){
					if("主表".equals(StringUtil.showNull(field.get("tabletype")))){
						select += "a." + StringUtil.showNull(field.get("enname")) + ",";
					}else{
						select += "st_" + StringUtil.showNull(field.get("tablename")) + "." + StringUtil.showNull(field.get("enname")) + ",";
					}
				}
				
				fsql = "select " + select + " 0 from " + sqlTable + " as a " +  leftJoin + " where 1=1 ";
				
				sql = "update mt_print_table set enname = ? ,fsql = ? where uuid = ? ";
				db.execute(sql, new String[]{sqlTable,fsql,uuid});
				
				/**
				 * 返回List
				 */
				response.getWriter().write("成功");	
			}else{
				if("add".equals(act)){
					//失败，删除所有信息
					db.del("mt_print_table", "uuid", uuid);
					db.del("mt_print_field", "formid", uuid);
					
					//删除文件
					String filePath = SaveAsExcel.getExeclTemplateDir()+"/"+uuid;
					File file = new File(filePath);
					ManuFileService.deleteFile(file);
				}
				
				response.getWriter().write("报表模板不能没有显示主表列，没法生成查询语句。请重新设置模板！\n");	
			}
			
			sql = "DROP TABLE IF EXISTS tt_" + flagOpt;
			db.execute(sql);
			
		} catch (Exception e) {
			e.printStackTrace();
            throw e;
		} finally {
			DbUtil.close(conn);
		}
		
	}
	
	public void del(HttpServletRequest request,HttpServletResponse response) throws Exception {
		Connection conn=null;
		try {
			ASFuntion CHF=new ASFuntion();
			String uuid = CHF.showNull(request.getParameter("uuid"));
			
			conn = new DBConnect().getConnect("");
			DbUtil db = new DbUtil(conn);
			
			db.del("mt_print_table", "uuid", uuid);
			db.del("mt_print_field", "formid", uuid);
			
			//删除文件
			String filePath = SaveAsExcel.getExeclTemplateDir()+"/"+uuid;
			File file = new File(filePath);
			ManuFileService.deleteFile(file);
			
			response.setContentType("text/html;charset=utf-8");
			response.getWriter().write("删除成功！");	
		} catch (Exception e) {
			e.printStackTrace();
            throw e;
		} finally {
			DbUtil.close(conn);
		}
		
	}

	public void tree(HttpServletRequest request,HttpServletResponse response) throws Exception {
		Connection conn=null;
		try {
			ASFuntion CHF=new ASFuntion();
			
			conn = new DBConnect().getConnect("");
			DbUtil db = new DbUtil(conn);
			List list = new ArrayList();
			
			String sql = "";
			String isSubject = CHF.showNull(request.getParameter("isSubject"));	//判断是哪个节目
			String emtype = CHF.showNull(request.getParameter("emtype"));
			String subsetid = CHF.showNull(request.getParameter("subsetid"));
			
			System.out.println(isSubject+"|" +emtype+"|"+subsetid);
			if("".equals(isSubject) || "0".equals(isSubject)){
				//开始节点
				List<FormVO> formVOs=db.select(FormVO.class, "select * from {0} where form_type=? order by tablename,name",EmploymentService.UUID_FORM_TYPE_SUBSET);
				for(FormVO formVO :formVOs){
			    	
			    	Map<String, Object> item=new HashMap<String, Object>();
					item.put("id", formVO.getUUID());
					item.put("text", formVO.getNAME());
					item.put("isSubject", "item");
					item.put("leaf", false);
					item.put("emtype", emtype);
					item.put("subsetid",formVO.getUUID());
					item.put("subname", formVO.getNAME());
					//item.put("url", MessageFormat.format("formDefine.do?method=formListView&uuid={0}", formVO.getUUID()));
					list.add(item);
			    }
				
			}else{
				sql = "select a.*,a.name as aname,concat(case lower(b.tablename) when 'k_user' then '主表.' else '副表.' end, b.name,'.',a.name) as subname " +
				"	from mt_com_form_query a " +
				"	join mt_com_form b on b.uuid=a.formid  " +
				"	where 1=1 " +
				"	and a.formid = '"+subsetid+"' " +
				"	and (a.bshow=1 and a.name >'') " +
				"	and a.enname not in ('username','departname') order by a.orderid";
				List<Map> fields = db.getList(sql);
				for(Map field :fields){
					Map<String, Object> item=new HashMap<String, Object>();
					item.put("id", field.get("uuid"));
					item.put("text", field.get("name"));
					item.put("isSubject", "2");
					item.put("leaf", true);
					item.put("emtype", emtype);
					item.put("subsetid",field.get("uuid"));
					item.put("subname", field.get("subname"));
					//item.put("url", MessageFormat.format("formDefine.do?method=formListView&uuid={0}", formVO.getUUID()));
					list.add(item);
				}
			}
			
			
			response.setContentType("text/html;charset=utf-8");
			String json = "{}";
			if(list != null){
				json = JSONArray.fromObject(list).toString();
			}
			//System.out.println("json="+json);
			response.getWriter().write(json);
		} catch (Exception e) {
			e.printStackTrace();
            throw e;
		} finally {
			DbUtil.close(conn);
		}
		
	}
	/**
	 * 打印
	 */
	public ModelAndView print(HttpServletRequest request,HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(OPENFILE);
		Connection conn=null;
		try {
			ASFuntion CHF=new ASFuntion();
			String menuid = CHF.showNull(request.getParameter("menuid"));
			
			String tablename = CHF.showNull(request.getParameter("tablename")); 
			String templateid = CHF.showNull(request.getParameter("templateid"));
			String emtype = CHF.showNull(request.getParameter("emtype"));
			String departmentid = CHF.showNull(request.getParameter("departmentid"));
			String qryWhere_em = CHF.showNull(request.getParameter("qryWhere_em"));
			String qryJoin_em = CHF.showNull(request.getParameter("qryJoin_em"));
			
			String filename = "";
			
			conn = new DBConnect().getConnect("");
			DbUtil db = new DbUtil(conn);
			PrintSetup printSetup = null;
			SaveAsExcel sae=null;
			
			Map table = db.get("mt_print_table", "uuid", templateid);
			List fieldList = db.getList("mt_print_field", "formid", templateid);
			
			String sheetname = (String)table.get("templatename");
			String fsql = (String)table.get("fsql");
			String sql = fsql + qryWhere_em ;
			if(!"".equals(menuid)){
				sql += " and a.departmentid in (${userPopedom}) ";
				sql = StringUtil.transUserPopedomValue(request, sql);
			}
			
			System.out.println(sql);
			//结果list
			List resultList = db.getList(sql);
			
			for (int i = 0; i < resultList.size(); i++) {
				Map map = (Map)resultList.get(i);
				Map varMap= new HashMap();
				
				for (int j = 0; j < fieldList.size(); j++) {
					Map field = (Map)fieldList.get(j);
					String replaceValue = StringUtil.showNull(field.get("replacevalue"));
					if(!"".equals(replaceValue)){
						String value = StringUtil.showNull(map.get(((String)field.get("enname")).toLowerCase())); //数据库的值
//						String t1[] = UTILString.getVaribles(replaceValue); //替换规则
//						for (int k = 0; k < t1.length; k++) {
//							if(t1[k].indexOf(value) >-1){
//								value = StringUtil.replaceStr(t1[k], value + "=" , "");
//							}
//						}
						value = StringUtil.transSqlValue(null, StringUtil.replaceStr(replaceValue, "${value}", value));
						varMap.put(field.get("excelname"), value);
					}else{
						varMap.put(field.get("excelname"), map.get(((String)field.get("enname")).toLowerCase()));	
					}
				}
				System.out.println(varMap);
				
				printSetup = new PrintSetup(conn);
				
				printSetup.setStrQuerySqls(new String[]{"select 1,1"});
				printSetup.setVarMap(varMap);
				printSetup.setStrExcelTemplateFileName(templateid);
				printSetup.setStrSheetName(sheetname);
				//String filename = printSetup.getExcelFile();
				//创建SAE
			    if (sae==null){
			    	sae = new SaveAsExcel(conn);
			    }
			    
			    //将新的打印设置重新应用到SAE上
			    printSetup.updateSae(sae);
			    
			    //从模版复制一段来解析形成最终的结果
			    sae.createOneAreaByTemplate();
			}
			
			if (sae==null){
				//无记录
				response.setContentType("text/html;charset=utf-8");
				response.getWriter().write("false|查询没有记录，不用打印！");
				return null;
			}
			
			sae.removeTemplateSheet();
			
			filename = sae.getRandomExcelFileName();
			
			sae.saveExcel(filename);
			
			modelAndView.addObject("pagebreaks",sae.getPagebreaks(null));
			modelAndView.addObject("templateid",templateid);
			
		    System.out.println("tempexcel="+filename);
			
			//返回文件列表
			if (!"".equals(filename)){
				modelAndView.addObject("filename",filename) ;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
            throw e;
		} finally {
			DbUtil.close(conn);
		}
		return modelAndView ;
	}
	
	
	//下载EXCEL
	public ModelAndView download(HttpServletRequest request,HttpServletResponse response) throws Exception {
		//ModelAndView modelAndView = null;
		
		Connection conn=null;
		try {
			ASFuntion CHF=new ASFuntion();
			String menuid = CHF.showNull(request.getParameter("menuid"));
			
			String tablename = CHF.showNull(request.getParameter("tablename")); 
			String templateid = CHF.showNull(request.getParameter("templateid"));
			String emtype = CHF.showNull(request.getParameter("emtype"));
			String departmentid = CHF.showNull(request.getParameter("departmentid"));
			String qryWhere_em = CHF.showNull(request.getParameter("qryWhere_em"));
			String qryJoin_em = CHF.showNull(request.getParameter("qryJoin_em"));
			
			String filename = "";
			
			conn = new DBConnect().getConnect("");
			DbUtil db = new DbUtil(conn);
			PrintSetup printSetup = null;
			SaveAsExcel sae=null;
			
			Map table = db.get("mt_print_table", "uuid", templateid);
			List fieldList = db.getList("mt_print_field", "formid", templateid);
			
			String sheetname = (String)table.get("templatename");
			String fsql = (String)table.get("fsql");
			String sql = fsql + qryWhere_em ;
			if(!"".equals(menuid)){
				sql += " and a.departmentid in (${userPopedom}) ";
				sql = StringUtil.transUserPopedomValue(request, sql);
			}
			
			System.out.println(sql);
			//结果list
			List resultList = db.getList(sql);
			
			for (int i = 0; i < resultList.size(); i++) {
				Map map = (Map)resultList.get(i);
				Map varMap= new HashMap();
				
				for (int j = 0; j < fieldList.size(); j++) {
					Map field = (Map)fieldList.get(j);
					String replaceValue = StringUtil.showNull(field.get("replacevalue"));
					if(!"".equals(replaceValue)){
						String value = StringUtil.showNull(map.get(((String)field.get("enname")).toLowerCase())); //数据库的值
//						String t1[] = UTILString.getVaribles(replaceValue); //替换规则
//						for (int k = 0; k < t1.length; k++) {
//							if(t1[k].indexOf(value) >-1){
//								value = StringUtil.replaceStr(t1[k], value + "=" , "");
//							}
//						}
						value = StringUtil.transSqlValue(null, StringUtil.replaceStr(replaceValue, "${value}", value));
						varMap.put(field.get("excelname"), value);
					}else{
						varMap.put(field.get("excelname"), map.get(((String)field.get("enname")).toLowerCase()));	
					}
				}
				System.out.println(varMap);
				
				printSetup = new PrintSetup(conn);
				
				printSetup.setStrQuerySqls(new String[]{"select 1,1"});
				printSetup.setVarMap(varMap);
				printSetup.setStrExcelTemplateFileName(templateid);
				printSetup.setStrSheetName(sheetname);
				//String filename = printSetup.getExcelFile();
				//创建SAE
			    if (sae==null){
			    	sae = new SaveAsExcel(conn);
			    }
			    
			    //将新的打印设置重新应用到SAE上
			    printSetup.updateSae(sae);
			    
			    //从模版复制一段来解析形成最终的结果
			    sae.createOneAreaByTemplate();
			}
			
			if (sae==null){
				//无记录
				response.setContentType("text/html;charset=utf-8");
				response.getWriter().write("false|查询没有记录，不用打印！");
				return null;
			}
			
			sae.removeTemplateSheet();
			
			filename = sae.getRandomExcelFileName();
			
			sae.saveExcel(filename);
			
			
			//modelAndView =new ModelAndView("Excel/tempdata/PrintandSave.jsp?filename="+filename);
			
			//modelAndView.addObject("pagebreaks",sae.getPagebreaks(null));
			//modelAndView.addObject("templateid",templateid);
			
			response.sendRedirect(request.getContextPath() +"/Excel/tempdata/Download.jsp?filename="+filename);
			
		    System.out.println("tempexcel="+filename);
			
			//返回文件列表
			if (!"".equals(filename)){
				//modelAndView.addObject("filename",filename) ;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
            throw e;
		} finally {
			DbUtil.close(conn);
		}
		//return modelAndView ;
		return null;
	}
}
