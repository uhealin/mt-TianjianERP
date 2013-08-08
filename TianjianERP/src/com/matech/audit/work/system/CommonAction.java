package com.matech.audit.work.system;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jxl.CellView;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.NumberFormat;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import net.sf.json.JSONArray;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.del.JRockey2Opp;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.attachFileUploadService.AttachService;
import com.matech.audit.service.attachFileUploadService.model.Attach;
import com.matech.audit.service.datamanage.BackupUtil;
import com.matech.audit.service.datamanage.DataZip;
import com.matech.audit.service.fileupload.FileUpload;
import com.matech.audit.service.sysMenuManger.sysMenuMangerService;
import com.matech.audit.service.user.UserService;
import com.matech.framework.listener.OnlineListListener;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.datagrid.Column;
import com.matech.framework.pub.datagrid.ExtGrid;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.StringUtil;
import com.matech.framework.service.print.PrintSetup;
import com.matech.framework.service.userDisplay.UserDisplayService;

public class CommonAction extends MultiActionController {
	
	private static Log log = LogFactory.getLog(CommonAction.class);

	private static final String PRINT_VIEW = "Excel/tempdata/PrintandSave.jsp";

	private static final String VALIDATE_VIEW = "AS_SYSTEM/validate.jsp";

	public static void main(String[] args) {
		try {
			String ss = null;
			new File(ss);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 公共打印
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView print(HttpServletRequest request, HttpServletResponse response) throws Exception{

		ModelAndView modelAndView = new ModelAndView(PRINT_VIEW);

		String printSql = request.getParameter("printSql");
		String printTitle = request.getParameter("printTitle");
		String printCharColumn = request.getParameter("printCharColumn");
		String printColumnWidth = request.getParameter("printColumnWidth");
		String printVerTical = request.getParameter("printVerTical");
		String printDisplayColName = request.getParameter("printDisplayColName");
		String printCustomerId = request.getParameter("printCustomerId");
		String printTitleRows = request.getParameter("printTitleRows");

		String printTableHead = request.getParameter("printTableHead");
		String printAllCount = request.getParameter("printAllCount");

		String printPoms = request.getParameter("printPoms");
		
		System.out.println("printPoms:===" + printPoms);

		ASFuntion asf = new ASFuntion() ;
		String hideColumnsStr = asf.showNull(request.getParameter("hideColumnsStr")) ;
		String printColName = asf.showNull(request.getParameter("printColName")) ;


			//页面上有隐藏列,则对打印参数进行处理
		if(!"".equals(hideColumnsStr)) {


				String[] hideStr = null;
				String[] displayColName = null;
				String[] columnWidth = null;
				//String[] tableHead  = null;
				String[] colName = null ;
				if(!"".equals(hideColumnsStr)) {
					 hideStr = hideColumnsStr.split(",") ;
				}
				if(!"".equals(printDisplayColName)) {
					displayColName = printDisplayColName.split("`") ;
					printDisplayColName = "";
				}
				if(!"".equals(printColumnWidth)) {
					columnWidth = printColumnWidth.split(",") ;
					printColumnWidth = "" ;
				}
				if(!"".equals(printColName)) {
					colName = printColName.split(",") ;
					printColName = "";
				}

				for(int i=0;i<displayColName.length;i++){
					if(!"hidden".equals(hideStr[i])) {
						printDisplayColName += displayColName[i]+"`" ;
						printColName += colName[i]+"," ;

						if(columnWidth != null) {
						printColumnWidth += columnWidth[i] + "," ;
						}
					}

				}
				printColName = printColName.substring(0,printColName.length()-1) ;
				printDisplayColName = printDisplayColName.substring(0,printDisplayColName.length()-1) ;
				if(!"".equals(printColumnWidth)) {
				printColumnWidth = printColumnWidth.substring(0,printColumnWidth.length()-1) ;
				}
				printSql = "select " + printColName + " from ( " +printSql+ " ) bb";




		}
		Connection conn=null;

		try {
			conn= new DBConnect().getConnect("");

			if(printCustomerId != null || !"".equals(printCustomerId)) {
				new DBConnect().changeDataBase(printCustomerId, conn);
			}

			String[] colNames = printDisplayColName.split("`");
			int[] iColumnWidths;
			String[] columnWidths;
			int cols = colNames.length;
			int maxWidth = 0;
			if(!"true".equals(printVerTical)) {
				maxWidth = 180;	//横向打印最大宽度
			} else {
				maxWidth = 130;	//纵向打印最大宽度
			}

			iColumnWidths = new int[cols];

			//如果没有设置列宽度,就自动计算宽度,非精确
			if("null".equals(printColumnWidth)
					|| printColumnWidth == null
					|| "".equals(printColumnWidth) ) {
				int sumWidth = 0;
				for(int i=0; i < cols; i++ ) {
					sumWidth += colNames[i].length();
				}

				for(int i=0; i < cols; i++ ) {
					iColumnWidths[i] = colNames[i].length() * maxWidth / sumWidth;
				}
			} else {
				columnWidths = printColumnWidth.split(",");
				for(int i=0; i < columnWidths.length; i++) {
					iColumnWidths[i] = Integer.parseInt(columnWidths[i]);
				}
			}

			//如果没有设置字符列,就默认把所有列都设置为字符列
			if("null".equals(printCharColumn)
					|| printCharColumn == null
					|| "".equals(printCharColumn)) {
				printCharColumn = "";
				for(int i=1; i <= cols; i++ ) {
					printCharColumn += i + "`";
				}

				printCharColumn = printCharColumn.substring(0, printCharColumn.length() - 1);
			}

			PrintSetup printSetup = new PrintSetup(conn);

			//设置打印副标题
			if(printPoms != null && !"".equals(printPoms.trim())){
				List[] lists = new List[1];
				lists[0] = new ArrayList();
				lists[0].add(0, "1");
				lists[0].add(1, printPoms);
				lists[0].add(2, null);
				lists[0].add(3, null);

				printSetup.setPoms(lists);
			}
			
			if(printTableHead !=null && !"".equals(printTableHead)){
				String[] strfields = printTableHead.split(",");
				String[] outstr1 = new String[strfields.length],outstr2=new String[strfields.length];
				int mode = 0;
				String strToken = "";

				for (int i = 0;i<strfields.length;i++){
					if (strfields[i].indexOf("{")>-1){
						mode = 1;
						strToken = strfields[i].substring(0,strfields[i].indexOf("{"));

					}else if (strfields[i].indexOf("}")>-1){
						mode = 2;
					}
					if (mode == 1){
						outstr1[i] = strToken;
						if (strfields[i].indexOf("{")>-1){
							outstr2[i] = strfields[i].substring(strfields[i].indexOf("{")+1);
						}else
							outstr2[i] = strfields[i];
					}else if (mode == 2){
						outstr1[i] = strToken;
						outstr2[i] = strfields[i].substring(0,strfields[i].indexOf("}"));
						mode=0;
					}else {
						outstr1[i] = strfields[i];
						outstr2[i] = strfields[i];
					}


				}


				String t1 = "",t2 = "";
				for (int i=0;i<strfields.length;i++){
					t1 += outstr1[i] + ",";
					t2 += outstr2[i] + ",";
				}
				if(!"".equals(t1)) t1 = t1.substring(0,t1.length()-1);
				if(!"".equals(t2)) t2 = t2.substring(0,t2.length()-1);


				//页面有列被隐藏了,对打印参数处理
				if(!"".equals(hideColumnsStr)) {
					if(printTableHead !=null && !"".equals(printTableHead)){
					t1 = "" ;
					t2 = "" ;
					String[] hideCol = hideColumnsStr.split(",") ;


					for(int i=0;i<hideCol.length;i++) {

						if(!"hidden".equals(hideCol[i])) {
							t1 += outstr1[i] + "," ;
							t2 += outstr2[i] + "," ;
						}

					}
				if(!"".equals(t1)) t1 = t1.substring(0,t1.length()-1);
				if(!"".equals(t2)) t2 = t2.substring(0,t2.length()-1);
				}
				}


				String [][] CTName = new String[1][2];
				String [][] ColName = new String[1][2];
				CTName[0][1] = new String();
				CTName[0][1] = ",";
				ColName[0][1] = new String();
				ColName[0][1]= ",";
				CTName[0][0] = new String();
				ColName[0][0] = new String();
				CTName[0][0] = t1;
				ColName[0][0] = t2;

				printSetup.setHeaders(CTName, ColName);
			}else{
				printSetup.setStrChineseTitles(new String[]{printDisplayColName});
			}

//			应小彭要求,将&替换成＆
			printSql = printSql.replaceAll("myReplaceBY＆", "&");

			ArrayList filename = new ArrayList();
			String txtSQL = " limit ";
			int iSheet = 10000;

			int TCount = 0;

			if(!"".equals(printAllCount)){

				TCount = Integer.parseInt(printAllCount);
			}

			int TFor = TCount / iSheet;

			for(int t = 0; t<= TFor; t++ ){

				txtSQL += String.valueOf(t * iSheet ) + "," +  String.valueOf((t+1) * iSheet) ;

				String tempSql =printSql + txtSQL;

				printSetup.setStrQuerySqls(new String[]{ tempSql });

				printSetup.setStrTitles(new String[]{printTitle});
				printSetup.setIColumnWidths(iColumnWidths);
				printSetup.setCharColumn(new String[]{printCharColumn});
				printSetup.setStrSheetName("表页-"+(t+1));

				filename.add( printSetup.getExcelFile());

				txtSQL = " limit ";

			}

			modelAndView.addObject("saveasfilename",printTitle);
			modelAndView.addObject("filenameList", filename);
			modelAndView.addObject("strPrintTitleRows", printTitleRows);
			modelAndView.addObject("bVpage", printVerTical);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}

		return modelAndView;
	}

	public ModelAndView printGroup(HttpServletRequest request,HttpServletResponse response) throws Exception {

		ModelAndView modelAndView = new ModelAndView(PRINT_VIEW);
		Connection conn=null;

		try {
			ArrayList filenameList = new ArrayList();

			String [] GroupName = request.getParameterValues("printGroupName");

			ASFuntion asf = new ASFuntion() ;

			String [] printTableIDs = request.getParameterValues("printTableID_" + GroupName[0]);

			conn= new DBConnect().getConnect("");

			String [][] CTName = new String[printTableIDs.length][2];
			String [][] ColName = new String[printTableIDs.length][2];

			String [] DisplayColName = new String[printTableIDs.length];
			String [] Title = new String[printTableIDs.length];
			String [] Sql = new String[printTableIDs.length];
			String [] CharColumn = new String[printTableIDs.length];

			int [] ColumnWidths = null;
			int optHead = 0;

			String title = "";

			for(int iPrint = 0 ; iPrint< printTableIDs.length ; iPrint ++){

				String printSql = request.getParameter("printSql_" + printTableIDs[iPrint]);

				printSql = printSql.replaceAll("myReplaceBY＆", "&");

				String printTitle = request.getParameter("printTitle_" + printTableIDs[iPrint]);
				String printCharColumn = request.getParameter("printCharColumn_" + printTableIDs[iPrint]);
				String printColumnWidth = request.getParameter("printColumnWidth_" + printTableIDs[iPrint]);
				String printVerTical = request.getParameter("printVerTical_" + printTableIDs[iPrint]);
				String printDisplayColName = request.getParameter("printDisplayColName_" + printTableIDs[iPrint]);
				String printCustomerId = request.getParameter("printCustomerId_" + printTableIDs[iPrint]);
				String printTitleRows = request.getParameter("printTitleRows_" + printTableIDs[iPrint]);

				String printTableHead = request.getParameter("printTableHead_" + printTableIDs[iPrint]);
				String printColName = request.getParameter("printColName_" + printTableIDs[iPrint]) ;

				String hideColumnsStr = asf.showNull(request.getParameter("hideColumnsStr" + printTableIDs[iPrint])) ;

				if(iPrint == 0) title = printTitle;

				if(!"".equals(hideColumnsStr)) {


					String[] hideStr = null;
					String[] displayColName = null;
					String[] columnWidth = null;
					String[] tableHead  = null;
					String[] colName = null ;
					if(!"".equals(hideColumnsStr)) {
						 hideStr = hideColumnsStr.split(",") ;
					}
					if(!"".equals(printDisplayColName)) {
						displayColName = printDisplayColName.split("`") ;
						printDisplayColName = "";
					}
					if(!"".equals(printColumnWidth)) {
						columnWidth = printColumnWidth.split(",") ;
						printColumnWidth = "" ;
					}
					if(!"".equals(printColName)) {
						colName = printColName.split(",") ;
						printColName = "";
					}

					for(int i=0;i<displayColName.length;i++){
						if(!"hidden".equals(hideStr[i])) {
							printDisplayColName += displayColName[i]+"`" ;
							printColName += colName[i]+"," ;

							if(columnWidth != null) {
								printColumnWidth += columnWidth[i] + "," ;
							}
						}

					}
					printColName = printColName.substring(0,printColName.length()-1) ;
					printDisplayColName = printDisplayColName.substring(0,printDisplayColName.length()-1) ;
					if(!"".equals(printColumnWidth)) {
						printColumnWidth = printColumnWidth.substring(0,printColumnWidth.length()-1) ;
					}
					printSql = "select " + printColName + " from ( " +printSql+ " ) bb";

				}

	//			页面上有隐藏列,则对打印参数进行处理

					if(printCustomerId != null || !"".equals(printCustomerId)) {
						new DBConnect().changeDataBase(printCustomerId, conn);
					}

					String[] colNames = printDisplayColName.split("`");
					int[] iColumnWidths;
					String[] columnWidths;
					int cols = colNames.length;
					int maxWidth = 0;
					if(!"true".equals(printVerTical)) {
						maxWidth = 180;	//横向打印最大宽度
					} else {
						maxWidth = 130;	//纵向打印最大宽度
					}

					iColumnWidths = new int[cols];

					//如果没有设置列宽度,就自动计算宽度,非精确
					if("null".equals(printColumnWidth)
							|| printColumnWidth == null
							|| "".equals(printColumnWidth) ) {
						int sumWidth = 0;
						for(int i=0; i < cols; i++ ) {
							sumWidth += colNames[i].length();
						}

						for(int i=0; i < cols; i++ ) {
							iColumnWidths[i] = colNames[i].length() * maxWidth / sumWidth;
						}
					} else {
						columnWidths = printColumnWidth.split(",");
						for(int i=0; i < columnWidths.length; i++) {
							iColumnWidths[i] = Integer.parseInt(columnWidths[i]);
						}
					}

					//如果没有设置字符列,就默认把所有列都设置为字符列
					if("null".equals(printCharColumn)
							|| printCharColumn == null
							|| "".equals(printCharColumn)) {
						printCharColumn = "";
						for(int i=1; i <= cols; i++ ) {
							printCharColumn += i + "`";
						}

						printCharColumn = printCharColumn.substring(0, printCharColumn.length() - 1);
					}

//					PrintSetup printSetup = new PrintSetup(conn);

					if(printTableHead !=null && !"".equals(printTableHead)){
						String[] strfields = printTableHead.split(",");
						String[] outstr1 = new String[strfields.length],outstr2=new String[strfields.length];
						int mode = 0;
						String strToken = "";

						for (int i = 0;i<strfields.length;i++){
							if (strfields[i].indexOf("{")>-1){
								mode = 1;
								strToken = strfields[i].substring(0,strfields[i].indexOf("{"));

							}else if (strfields[i].indexOf("}")>-1){
								mode = 2;
							}
							if (mode == 1){
								outstr1[i] = strToken;
								if (strfields[i].indexOf("{")>-1){
									outstr2[i] = strfields[i].substring(strfields[i].indexOf("{")+1);
								}else
									outstr2[i] = strfields[i];
							}else if (mode == 2){
								outstr1[i] = strToken;
								outstr2[i] = strfields[i].substring(0,strfields[i].indexOf("}"));
								mode=0;
							}else {
								outstr1[i] = strfields[i];
								outstr2[i] = strfields[i];
							}


						}


						String t1 = "",t2 = "";
						for (int i=0;i<strfields.length;i++){
							t1 += outstr1[i] + ",";
							t2 += outstr2[i] + ",";
						}
						if(!"".equals(t1)) t1 = t1.substring(0,t1.length()-1);
						if(!"".equals(t2)) t2 = t2.substring(0,t2.length()-1);


						//页面有列被隐藏了,对打印参数处理
						if(!"".equals(hideColumnsStr)) {
							if(printTableHead !=null && !"".equals(printTableHead)){
							t1 = "" ;
							t2 = "" ;
							String[] hideCol = hideColumnsStr.split(",") ;


							for(int i=0;i<hideCol.length;i++) {

								if(!"hidden".equals(hideCol[i])) {
									t1 += outstr1[i] + "," ;
									t2 += outstr2[i] + "," ;
								}

							}
							if(!"".equals(t1)) t1 = t1.substring(0,t1.length()-1);
							if(!"".equals(t2)) t2 = t2.substring(0,t2.length()-1);
							}
						}

						CTName[iPrint][1] = new String();
						CTName[iPrint][1] = ",";
						ColName[iPrint][1] = new String();
						ColName[iPrint][1]= ",";
						CTName[iPrint][0] = new String();
						ColName[iPrint][0] = new String();
						CTName[iPrint][0] = t1;
						ColName[iPrint][0] = t2;

						optHead = 1;

					}else{

						DisplayColName[iPrint] = new String();
						DisplayColName[iPrint] = printDisplayColName;

					}

					Title[iPrint] = new String();
					Sql[iPrint] = new String();
					CharColumn[iPrint] = new String();

					Title[iPrint] = printTitle;
					Sql[iPrint] = printSql;
					CharColumn[iPrint] = printCharColumn;

					ColumnWidths = iColumnWidths;

//					printSetup.setStrTitles(new String[]{printTitle});
//					printSetup.setStrQuerySqls(new String[]{printSql});
//					printSetup.setCharColumn(new String[]{printCharColumn});
//					printSetup.setIColumnWidths(iColumnWidths);

//					printSetup.setStrSheetName(printTitle);


					modelAndView.addObject("bVpage", printVerTical);
			}

			PrintSetup printSetup = new PrintSetup(conn);
			if(optHead == 1){
				printSetup.setHeaders(CTName, ColName);
			}else{
				printSetup.setStrChineseTitles(DisplayColName);
			}
			printSetup.setStrTitles(Title);
			printSetup.setStrQuerySqls(Sql);
			printSetup.setCharColumn(CharColumn);
			printSetup.setIColumnWidths(ColumnWidths);

			printSetup.setStrSheetName(title);

			String filename = printSetup.getExcelFile();

//			filenameList.add(filename);

			modelAndView.addObject("saveasfilename",title);
//			modelAndView.addObject("filenameList", filenameList);
			modelAndView.addObject("filename", filename);
//			modelAndView.addObject("strPrintTitleRows", printTitleRows);


			return modelAndView;

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
	}

	public ModelAndView printList(HttpServletRequest request,HttpServletResponse response) throws Exception {

		ModelAndView modelAndView = new ModelAndView(PRINT_VIEW);
		Connection conn=null;

		try {
			ArrayList filenameList = new ArrayList();

			String GroupName = request.getParameter("printGroupName");

			ASFuntion asf = new ASFuntion() ;

			String [] printTableIDs = request.getParameterValues("printTableID_" + GroupName);

			conn= new DBConnect().getConnect("");

//			String [][] CTName = new String[printTableIDs.length][2];
//			String [][] ColName = new String[printTableIDs.length][2];

			String [] DisplayColName = new String[printTableIDs.length];
			String [] Title = new String[printTableIDs.length];
			String [] Sql = new String[printTableIDs.length];
			String [] CharColumn = new String[printTableIDs.length];

			int [] ColumnWidths = null;
			int optHead = 0;

			String title = "";

			for(int iPrint = 0 ; iPrint< printTableIDs.length ; iPrint ++){

				String printSql = request.getParameter("printSql_" + printTableIDs[iPrint]);

				printSql = printSql.replaceAll("myReplaceBY＆", "&");

				String printTitle = request.getParameter("printTitle_" + printTableIDs[iPrint]);
				String printCharColumn = request.getParameter("printCharColumn_" + printTableIDs[iPrint]);
				String printColumnWidth = request.getParameter("printColumnWidth_" + printTableIDs[iPrint]);
				String printVerTical = request.getParameter("printVerTical_" + printTableIDs[iPrint]);
				String printDisplayColName = request.getParameter("printDisplayColName_" + printTableIDs[iPrint]);
				String printCustomerId = request.getParameter("printCustomerId_" + printTableIDs[iPrint]);
				String printTitleRows = request.getParameter("printTitleRows_" + printTableIDs[iPrint]);

				String printTableHead = request.getParameter("printTableHead_" + printTableIDs[iPrint]);
				String printColName = request.getParameter("printColName_" + printTableIDs[iPrint]) ;

				String hideColumnsStr = asf.showNull(request.getParameter("hideColumnsStr" + printTableIDs[iPrint])) ;

				if(iPrint == 0) title = printTitle;

				if(!"".equals(hideColumnsStr)) {


					String[] hideStr = null;
					String[] displayColName = null;
					String[] columnWidth = null;
					String[] tableHead  = null;
					String[] colName = null ;
					if(!"".equals(hideColumnsStr)) {
						 hideStr = hideColumnsStr.split(",") ;
					}
					if(!"".equals(printDisplayColName)) {
						displayColName = printDisplayColName.split("`") ;
						printDisplayColName = "";
					}
					if(!"".equals(printColumnWidth)) {
						columnWidth = printColumnWidth.split(",") ;
						printColumnWidth = "" ;
					}
					if(!"".equals(printColName)) {
						colName = printColName.split(",") ;
						printColName = "";
					}

					for(int i=0;i<displayColName.length;i++){
						if(!"hidden".equals(hideStr[i])) {
							printDisplayColName += displayColName[i]+"`" ;
							printColName += colName[i]+"," ;

							if(columnWidth != null) {
								printColumnWidth += columnWidth[i] + "," ;
							}
						}

					}
					printColName = printColName.substring(0,printColName.length()-1) ;
					printDisplayColName = printDisplayColName.substring(0,printDisplayColName.length()-1) ;
					if(!"".equals(printColumnWidth)) {
						printColumnWidth = printColumnWidth.substring(0,printColumnWidth.length()-1) ;
					}
					printSql = "select " + printColName + " from ( " +printSql+ " ) bb";

				}

	//			页面上有隐藏列,则对打印参数进行处理

					if(printCustomerId != null || !"".equals(printCustomerId)) {
						new DBConnect().changeDataBase(printCustomerId, conn);
					}

					String[] colNames = printDisplayColName.split("`");
					int[] iColumnWidths;
					String[] columnWidths;
					int cols = colNames.length;
					int maxWidth = 0;
					if(!"true".equals(printVerTical)) {
						maxWidth = 180;	//横向打印最大宽度
					} else {
						maxWidth = 130;	//纵向打印最大宽度
					}

					iColumnWidths = new int[cols];

					//如果没有设置列宽度,就自动计算宽度,非精确
					if("null".equals(printColumnWidth)
							|| printColumnWidth == null
							|| "".equals(printColumnWidth) ) {
						int sumWidth = 0;
						for(int i=0; i < cols; i++ ) {
							sumWidth += colNames[i].length();
						}

						for(int i=0; i < cols; i++ ) {
							iColumnWidths[i] = colNames[i].length() * maxWidth / sumWidth;
						}
					} else {
						columnWidths = printColumnWidth.split(",");
						for(int i=0; i < columnWidths.length; i++) {
							iColumnWidths[i] = Integer.parseInt(columnWidths[i]);
						}
					}

					//如果没有设置字符列,就默认把所有列都设置为字符列
					if("null".equals(printCharColumn)
							|| printCharColumn == null
							|| "".equals(printCharColumn)) {
						printCharColumn = "";
						for(int i=1; i <= cols; i++ ) {
							printCharColumn += i + "`";
						}

						printCharColumn = printCharColumn.substring(0, printCharColumn.length() - 1);
					}

					PrintSetup printSetup = new PrintSetup(conn);

					if(printTableHead !=null && !"".equals(printTableHead)){
						String[] strfields = printTableHead.split(",");
						String[] outstr1 = new String[strfields.length],outstr2=new String[strfields.length];
						int mode = 0;
						String strToken = "";

						for (int i = 0;i<strfields.length;i++){
							if (strfields[i].indexOf("{")>-1){
								mode = 1;
								strToken = strfields[i].substring(0,strfields[i].indexOf("{"));

							}else if (strfields[i].indexOf("}")>-1){
								mode = 2;
							}
							if (mode == 1){
								outstr1[i] = strToken;
								if (strfields[i].indexOf("{")>-1){
									outstr2[i] = strfields[i].substring(strfields[i].indexOf("{")+1);
								}else
									outstr2[i] = strfields[i];
							}else if (mode == 2){
								outstr1[i] = strToken;
								outstr2[i] = strfields[i].substring(0,strfields[i].indexOf("}"));
								mode=0;
							}else {
								outstr1[i] = strfields[i];
								outstr2[i] = strfields[i];
							}


						}


						String t1 = "",t2 = "";
						for (int i=0;i<strfields.length;i++){
							t1 += outstr1[i] + ",";
							t2 += outstr2[i] + ",";
						}
						if(!"".equals(t1)) t1 = t1.substring(0,t1.length()-1);
						if(!"".equals(t2)) t2 = t2.substring(0,t2.length()-1);


						//页面有列被隐藏了,对打印参数处理
						if(!"".equals(hideColumnsStr)) {
							if(printTableHead !=null && !"".equals(printTableHead)){
							t1 = "" ;
							t2 = "" ;
							String[] hideCol = hideColumnsStr.split(",") ;


							for(int i=0;i<hideCol.length;i++) {

								if(!"hidden".equals(hideCol[i])) {
									t1 += outstr1[i] + "," ;
									t2 += outstr2[i] + "," ;
								}

							}
							if(!"".equals(t1)) t1 = t1.substring(0,t1.length()-1);
							if(!"".equals(t2)) t2 = t2.substring(0,t2.length()-1);
							}
						}

						String [][] CTName = new String[1][2];
						String [][] ColName = new String[1][2];
						CTName[0][1] = new String();
						CTName[0][1] = ",";
						ColName[0][1] = new String();
						ColName[0][1]= ",";
						CTName[0][0] = new String();
						ColName[0][0] = new String();
						CTName[0][0] = t1;
						ColName[0][0] = t2;

						optHead = 1;
						printSetup.setHeaders(CTName, ColName);
					}else{

						DisplayColName[iPrint] = new String();
						DisplayColName[iPrint] = printDisplayColName;
						printSetup.setStrChineseTitles(new String[]{printDisplayColName});
					}

					Title[iPrint] = new String();
					Sql[iPrint] = new String();
					CharColumn[iPrint] = new String();

					Title[iPrint] = printTitle;
					Sql[iPrint] = printSql;
					CharColumn[iPrint] = printCharColumn;

					System.out.println(printTitle + "|" + printSql + "|"+printCharColumn + "|" +printDisplayColName);

					ColumnWidths = iColumnWidths;

					printSetup.setStrTitles(new String[]{printTitle});
					printSetup.setStrQuerySqls(new String[]{printSql});
					printSetup.setCharColumn(new String[]{printCharColumn});
					printSetup.setIColumnWidths(iColumnWidths);

					printSetup.setStrSheetName(printTitle);

					String filename = printSetup.getExcelFile();

					filenameList.add(filename);

					modelAndView.addObject("strPrintTitleRows", printTitleRows);
					modelAndView.addObject("bVpage", printVerTical);

			}


			modelAndView.addObject("saveasfilename",title);
			modelAndView.addObject("filenameList", filenameList);
//			modelAndView.addObject("filename", filename);


			return modelAndView;

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
	}

	/**
	 * 下载文件
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView download(HttpServletRequest request, HttpServletResponse response) throws Exception{
		response.setContentType("text/html;charset=utf-8");

		java.io.BufferedInputStream bis = null;
		java.io.BufferedOutputStream bos = null;

		String filePath = request.getParameter("filePath");
		String fileName = request.getParameter("fileName");

		try {
			long fileLength = new File(filePath).length();

			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.setContentType("application/x-msdownload");
			response.setHeader("Content-disposition","attachment; filename=" + fileName);
			response.setHeader("Content-Length", String.valueOf(fileLength));

			bis = new BufferedInputStream(new FileInputStream(filePath));
			bos = new BufferedOutputStream(response.getOutputStream());
			byte[] buff = new byte[2048];
			int bytesRead;
			while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
				bos.write(buff, 0, bytesRead);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bis != null)
				bis.close();
			if (bos != null)
				bos.close();
		}


		return null;
	}
	
	/**
	 * 下载文件
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView download1(HttpServletRequest request, HttpServletResponse response) throws Exception{
		response.setContentType("text/html;charset=utf-8");

		java.io.BufferedInputStream bis = null;
		java.io.BufferedOutputStream bos = null;

		String filePath = URLDecoder.decode(request.getParameter("filePath"),"UTF-8");
		String fileName = URLDecoder.decode(request.getParameter("fileName"),"UTF-8");

		try {
			long fileLength = new File(filePath).length();

			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.setContentType("application/x-msdownload");
			response.setHeader("Content-disposition","attachment; filename=" + fileName);
			response.setHeader("Content-Length", String.valueOf(fileLength));

			bis = new BufferedInputStream(new FileInputStream(filePath));
			bos = new BufferedOutputStream(response.getOutputStream());
			byte[] buff = new byte[2048];
			int bytesRead;
			while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
				bos.write(buff, 0, bytesRead);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bis != null)
				bis.close();
			if (bos != null)
				bos.close();
		}


		return null;
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
		UserSession userSession = (UserSession) session.getAttribute("userSession");

		Connection conn=null;

		try{

			conn = new DBConnect().getConnect("");
			if(userSession != null) {

			    String userId = userSession.getUserId();
			    String screen = (String)session.getAttribute("userScreen");

			    int pageSize =	userSession.getUserPageSize() != null ? Integer.parseInt(userSession.getUserPageSize()) : 50;

			    if(userId != null) {

			    	UserDisplayService userDisplayService = new UserDisplayService(conn);

				    if(userSession.getCurProjectId() != null && !"".equals(userSession.getCurProjectId())) {
				    	//保存最后一次登陆项目
				    	userDisplayService.setLastProject(userId,userSession.getCurProjectId());
				    }

				    if(screen != null && !"".equals(screen)) {
				    	//保存用户分页数
				    	userDisplayService.setUserPageSize(userId,screen,pageSize);
				    }
			    }
			}


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
			response.sendRedirect("login.do");
		}

		return null;
	}

	/**
	 * 验证用户
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView validateUser(HttpServletRequest request, HttpServletResponse response) throws Exception{

		response.setContentType("text/html;charset=utf-8");
		PrintWriter printWriter = response.getWriter();

		String loginId = request.getParameter("loginId");
		String password = request.getParameter("password");
		String validate = request.getParameter("validate");

		String returnString = "";

		Connection conn = null;

		try {
			HttpSession session = request.getSession();
			UserSession userSession = (UserSession) session.getAttribute("userSession");

			String userLogin = new ASFuntion().showNull(userSession.getUserLoginId());

			//如果当前用户和登录用户是同一人
			if(userLogin.equals(loginId)) {
				returnString = "对不起,二次登录用户名不允许跟当前用户名一致!!!";
			} else {

				conn = new DBConnect().getConnect("");

				int state = new UserService(conn).validateUser(loginId, password,validate );

				//返回用户状态
				switch(state) {
					case UserService.USER_STATE_ENABLED:
						returnString = "ok!!";
						break;

					case UserService.USER_STATE_DISABLED:
						returnString = "对不起,该用户名已被禁用!!";
						break;

					case UserService.USER_STATE_NOFOUND:
						returnString = "对不起,该用户名不存在!!";
						break;

					case UserService.USER_STATE_POWERLESS:
						returnString = "对不起,该用户名所在角色没有权限!!";
						break;

					case UserService.USER_STATE_PWD_ERROR:
						returnString = "对不起,密码错误!!";
						break;

					default:
						returnString = "对不起,系统错误!!";
						break;
				}
			}

			printWriter.write(returnString);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
			printWriter.close();
		}

		return null;
	}

	/**
	 * 验证路径是否正确
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView validateServerFilePath(HttpServletRequest request, HttpServletResponse response) throws Exception{

		response.setContentType("text/html;charset=utf-8");
		PrintWriter printWriter = response.getWriter();
		String serverFilePath = request.getParameter("serverFilePath");
		
		if(serverFilePath == null) {
			printWriter.write("参数错误");
		} else if("".equals(serverFilePath)) {
			printWriter.write("服务器文件夹路径不能为空");
		} else if(!new File(serverFilePath).exists()) {
			printWriter.write("服务器文件夹路径不存在：" + serverFilePath);
		} else if(!new File(serverFilePath).isDirectory()) {
			printWriter.write("该路径不是文件夹：" + serverFilePath);
		} else {
			printWriter.write("ok");
		}
		
		return null;
	}
	
	/**
	 * 跳转到验证页面
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView validate(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView modelAndView = new ModelAndView(VALIDATE_VIEW);

		String menuId = request.getParameter("menuid");

		Connection conn = null;

		String act = "error_page.jsp";
		String validate = "-1";

		try {
			conn = new DBConnect().getConnect("");
			sysMenuMangerService menuMangerService = new sysMenuMangerService(conn);
			validate = menuMangerService.getMenuValidate(menuId);
			act = menuMangerService.getMenuAct(menuId);

			act = act.replaceAll("\\.\\.", "/AuditSystem");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

		modelAndView.addObject("act", act);
		modelAndView.addObject("validate", validate);

		return modelAndView;
	}
	
	
	public ModelAndView datagirdPrint(HttpServletRequest request, HttpServletResponse response)throws Exception {
		
		BufferedWriter writer = null;
		PreparedStatement ps = null ;
		ResultSet rs = null ;
		Connection conn = null ;
		try {
			ASFuntion CHF = new ASFuntion();
			String userSessionId = CHF.showNull(request.getParameter("userSessionId")).trim() ;
			String tableId = CHF.showNull(request.getParameter("tableId")); 
			
			//找出用户的session,这里是根据sessionid去匹配的。
			List list = OnlineListListener.getList() ;
			UserSession userSession = null ;
			for(int i=0;i<list.size();i++) { 
				UserSession tempUserSession = (UserSession)list.get(i);
				String sessionId = tempUserSession.getUserSessionId() ;
				if(userSessionId.equals(sessionId)){
					userSession = tempUserSession ;
				}
			}
			

			DataGridProperty pp = (DataGridProperty)userSession.getUserSession().getAttribute(ExtGrid.sessionPre+tableId);
			String customerId = CHF.showNull(userSession.getCurCustomerId());
			
			conn = new DBConnect().getConnect(customerId) ;
			
			//这里开始拼打印的sql..
			String displayColName = "";
			String colName = "";
			String printSql = "";

			if(pp.getPrintSql() == null || pp.getPrintColumnName() == null) {

				//如果设置了不需要打印的列
				if(pp.getNoPrintColName() != null
						&& !"".equals(pp.getNoPrintColName())) {

					for (int i = 0; i < pp.columns.size(); i++) {
						Column column = (Column) pp.columns.get(i);
						String noPrintColName = "," + pp.getNoPrintColName().toLowerCase() + ",";

						//如果该列不在不需要打印的列中,就拼成sql
						if(noPrintColName.indexOf(column.getColName().toLowerCase()) == -1) {
							displayColName += column.getDisplayColName() + ",";
							colName += column.getColName() + ",";
						}
					}   
				} else {
					for (int i = 0; i < pp.columns.size(); i++) {
						Column column = (Column) pp.columns.get(i);

						String tempDisplayColName = column.getDisplayColName();

						//如果是抽疑调等几个列,则默认不打印
						if("抽".equals(tempDisplayColName)
								|| "疑".equals(tempDisplayColName)
								|| "调".equals(tempDisplayColName)
								|| "析".equals(tempDisplayColName)
								|| "撤".equals(tempDisplayColName)) {
							continue;
						}

						displayColName += column.getDisplayColName() + ",";
						colName += column.getColName() + ",";

					}
				}
				
				displayColName = displayColName.substring(0, displayColName.length() - 1);
				colName = colName.substring(0, colName.length() - 1);

				if(pp.getPrintSqlColumn() != null && !"".equals(pp.getPrintSqlColumn())){
					colName = pp.getPrintSqlColumn();
				}
				
				if(pp.getPrintColumn() != null  && !"".equals(pp.getPrintColumn())){
					displayColName = pp.getPrintColumn();
				}

				printSql = "select " + colName + " from ( " + pp.getFinishSQLDeleteLimit() + " ) aa";
			} else {
				printSql = pp.getPrintSql();
				displayColName = pp.getPrintColumnName();
			}
			//复杂表头
			String tableHead = pp.getPrintTableHead();
			if(tableHead == null || "".equals(tableHead)) tableHead = pp.tableHead;
			
			//开始生成文件了,首先创建1个文本文件
			File printFileFoder = new File(BackupUtil.getDATABASE_PATH() + "../print/temp");
			if (!printFileFoder.exists()) {
				printFileFoder.mkdirs();
			}
			//生成一个随机命名的文件
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmssSS");
			String fileName = sdf.format(new Date()) ;
			File printFile = new File(printFileFoder, fileName+ ".txt");
			
			writer = new BufferedWriter(new FileWriter(printFile, true));
			writer.write(displayColName) ;
			
			ps = conn.prepareStatement(printSql) ;
			rs = ps.executeQuery() ;
			while(rs.next()) {
				String lineStr = "" ;
				String[] col = colName.split(",");
				for(int i=0;i<col.length;i++) { 
					lineStr += rs.getString(col[i])+"," ;
				}
				if(!"".equals(lineStr)) {
					lineStr = lineStr.substring(0,lineStr.length()-1) ;
				}
				writer.newLine() ;
				writer.write(lineStr);
			}
			
			writer.close() ;
			//压缩文件
			File zipFile =  new File(BackupUtil.getDATABASE_PATH() + "../print/"+fileName+ ".zip");
			new DataZip().zip(BackupUtil.getDATABASE_PATH() + "../print/temp",new FileOutputStream(zipFile));
			
			//删除临时文件夹printFile
			if(printFile.exists()) {
				System.out.println("delete:"+printFile.delete());
			}
			response.setContentType("application/x-msdownload");
			response.setHeader("Content-disposition","attachment; filename=" + fileName+".zip");
			response.setContentType("text/html;charset=utf-8");
 			
 			OutputStream os = response.getOutputStream();
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(zipFile));

			byte b[] = new byte[512];
			int len;

			while ((len = bis.read(b)) != -1) {
				os.write(b, 0, len);
			}

			os.flush(); 
			bis.close();
			os.close();
			  
			//下载完了..把生成的zip包删除
 			if(zipFile.exists()) {
 				zipFile.delete();
 			}
 		//	out.write("-----------执行完了！！-------------");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(writer !=null) writer.close() ;
			DbUtil.close(rs) ;
			DbUtil.close(ps) ;
			DbUtil.close(conn) ;
		}
		return null;
	}

	/**
	 * 附件上传
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView attachUpload(HttpServletRequest request,
			HttpServletResponse response) {

		response.setContentType("text/html;charset=utf-8");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);

		String mode=request.getParameter("mode");
		
		PrintWriter out = null;
		Connection conn = null;

		String indexTable = StringUtil.showNull(request
				.getParameter("indexTable"));
		String indexId = StringUtil.showNull(request.getParameter("indexId"));

		HttpSession session = request.getSession();
		UserSession userSession = (UserSession) session
				.getAttribute("userSession");

		String userId = "";

		if (userSession != null) {
			userId = userSession.getUserId();
		}

		try {
			// 生成UUID作为文件名
			String attachId = StringUtil.getUUID();
			String attachFilePath = AttachService.ATTACH_FILE_PATH;

			if (attachFilePath.lastIndexOf("/") != attachFilePath.length()) {
				attachFilePath += "/";
			}

			// 如果不指定模块，则放到
			if (!"".equals(indexTable)) {
				attachFilePath += indexTable + "/";
			} else {
				attachFilePath += AttachService.ATTACH_FILE_DEFAULT_FOLDER;
				indexTable = AttachService.ATTACH_FILE_DEFAULT_FOLDER;
			}


			log.info("附件上传路径:" + attachFilePath);

			// 处理上传信息
			FileUpload fileUpload = new FileUpload(request);

			Map map = fileUpload.UploadFile(attachId, attachFilePath);

			String clientFileName = (String) map.get("clientFileName"); // 原文件名
			long fileSize = (Long) map.get("fileSize");

			// 文件信息保存到数据库
			conn = new DBConnect().getConnect();
			AttachService attachService = new AttachService(conn);

			if("single".equals(mode)){
				attachService.remove("",indexId);
			}
			
			Attach attach = new Attach();
			attach.setAttachFile(attachId);
			attach.setAttachFilePath(indexTable + "/" + attachId);
			attach.setAttachId(attachId);
			attach.setAttachName(clientFileName);

			attach.setUpdateTime(StringUtil.getCurDateTime());
			attach.setUpdateUser(userId);

			attach.setIndexTable(indexTable);
			attach.setIndexId(indexId);

			attach.setFileSize(fileSize);

			attachService.save(attach);

			out = response.getWriter();
			out.write("{success:true,msg:'保存上传文件数据成功!'}");
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

		return null;
	}

	/**
	 * 附件上传进度
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView attachUploadProcess(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		response.setContentType("text/html;charset=utf-8");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);

		PrintWriter out = response.getWriter();

		Object percent = request.getSession().getAttribute("uploadPercentage");
		String msg = "";
		double d = 0;
		if (percent == null) {
			d = 0;
		} else {
			d = (Double) percent;
		}

		if (d < 1) {
			// d<1代表正在上传，
			int intPercent = (int) (d * 100);
			msg = intPercent + "%";
			out.write("{success:true, msg: '" + msg + "', percentage:'" + d
					+ "', finished: false}");
		} else if (d >= 1) {
			out.write("{success:true,finished: true}");

		}
		out.flush();

		return null;
	}

	/**
	 * 获取附件列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView getAttachList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);

		PrintWriter out = null;
		Connection conn = null;

		try {
			
			ASFuntion funtion = new ASFuntion();
			
			// 生成UUID作为文件名
			String indexTable = funtion.showNull(request
					.getParameter("indexTable"));
			String indexId = funtion.showNull(request
					.getParameter("indexId"));

			// 文件信息保存到数据库
			conn = new DBConnect().getConnect("");
			AttachService attachService = new AttachService(conn);

			List attachList = attachService.getAttachList(indexTable, indexId);

			out = response.getWriter();
			out.write(JSONArray.fromObject(attachList).toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

		return null;
	}

	/**
	 * 删除附件
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView attachRemove(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);

		PrintWriter out = null;
		Connection conn = null;

		try {
			String attachId = new ASFuntion().showNull(request
					.getParameter("attachId"));

			conn = new DBConnect().getConnect("");
			AttachService attachService = new AttachService(conn);
			Attach attach = attachService.getAttach(attachId);
			attachService.remove(attach);

			out = response.getWriter();
			out.write("success");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

		return null;
	}

	/**
	 * 下载附件
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView attachDownload(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		response.setContentType("text/html;charset=utf-8");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);

		String attachId = request.getParameter("attachId");

		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		Connection conn = null;

		try {
			conn = new DBConnect().getConnect();

			AttachService attachService = new AttachService(conn);
			Attach attach = attachService.getAttach(attachId);

			String attachFilePath = attachService.getAttachFilePath(attach);

			File file = new File(attachFilePath);

			if (file.exists()) {
				long fileLength = file.length();

				String fileName = URLEncoder.encode(attach.getAttachName(),
						"UTF-8");
				fileName = fileName.replaceAll("\\+", "%20"); // 处理空格变成加号的问题
				if("flash".equals(request.getParameter("ftype"))){
					response.setContentType("application/x-shockwave-flash");
				}else{
				response.setContentType("application/x-msdownload");
				
				response.setHeader("Content-disposition",
						"attachment; filename=" + fileName);
				response.setHeader("Content-Length", String.valueOf(fileLength));
				}
				try {
					bis = new BufferedInputStream(new FileInputStream(file));
					bos = new BufferedOutputStream(response.getOutputStream());
					byte[] buff = new byte[2048];
					int bytesRead;
					while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
						bos.write(buff, 0, bytesRead);
					}
				} catch (Exception e) {
					// e.printStackTrace();
				}
			} else {
				throw new Exception("附件不存在：" + attachFilePath);
			}

		} catch (Exception e) {
			// e.printStackTrace();
			throw e;
		} finally {
			if (bis != null)
				bis.close();
			if (bos != null)
				bos.close();

			DbUtil.close(conn);
		}

		return null;
	}
	
	/**
	 * 检测文件是否存在
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView checkFile(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		response.setContentType("text/html;charset=utf-8");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);

		String attachId = request.getParameter("attachId");

		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		Connection conn = null;

		try {
			conn = new DBConnect().getConnect("");

			AttachService attachService = new AttachService(conn);
			Attach attach = attachService.getAttach(attachId);
			File file = attachService.getAttachFile(attach);
			if (file !=null) {
				response.getWriter().write("true");
			}else{
				response.getWriter().write("false");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bis != null)
				bis.close();
			if (bos != null)
				bos.close();

			DbUtil.close(conn);
		}

		return null;
	}
	
	/**
	 * 获取全球唯一ID
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView getUUID(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);

		PrintWriter out = null;

		try {
			out = response.getWriter();
			out.write(StringUtil.getUUID());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * 导出成EXCEL
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView expExcel(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String tableId = request.getParameter("tableId");

		Connection conn = null;

		PreparedStatement ps = null;
		ResultSet rs = null;
		OutputStream os = null;

		try {

			HttpSession session = request.getSession();
			DataGridProperty pp = (DataGridProperty) session
					.getAttribute(ExtGrid.sessionPre + tableId);

			String excelName = new SimpleDateFormat("yyyyMMddHHmmss")
					.format(new Date()) + ".xls";
			String sheetName = "Sheet1";
			String fileName = URLEncoder.encode(excelName, "ISO8859-1");

			if (fileName.length() > 150) {
				fileName = new String(excelName.getBytes("GBK"), "ISO-8859-1");
			}

			response.setContentType("text/html;charset=UTF-8");
			response.setContentType("application/octetstream");
			response.addHeader("Content-Disposition", "attachment;filename="
					+ fileName);

			os = response.getOutputStream();
			// 创建EXCEL
			WritableWorkbook wwb = Workbook.createWorkbook(os);

			// 创建表页
			WritableSheet ws = wwb.createSheet(sheetName, 0);

			// 设置单元格默认格式
			ws.getSettings().setDefaultColumnWidth(10);
			ws.getSettings().setShowGridLines(false);

			// 设置字体
			jxl.write.WritableFont wfont = null;
			wfont = new jxl.write.WritableFont(WritableFont.createFont("宋体"),
					9, WritableFont.NO_BOLD);

			conn = new DBConnect().getConnect("");

			ps = conn.prepareStatement(pp.getFinishSQL());
			rs = ps.executeQuery();
			ResultSetMetaData rsms = ps.getMetaData();

			List list = pp.getColumns();

			// 设置列宽
			String[] strColWidth = pp.getColumnWidth().split(",");
			for (int i = 0; i < list.size(); i++) {
				CellView cv = new CellView(); // 定义一个列显示样式
				try {
					cv.setSize((Integer.valueOf(strColWidth[i]) * 2) * 265);
				} catch (Exception ex) {
					cv.setSize((10 * 2) * 265);
				}
				ws.setColumnView(i, cv);
			}
			// 获得表头标题
			String displayColName[] = new String[list.size()];
			String colName[] = new String[list.size()];
			String format[] = new String[list.size()];

			for (int i = 0; i < list.size(); i++) {
				Column column = (Column) list.get(i);
				displayColName[i] = column.getDisplayColName();
				colName[i] = column.getColName();
				format[i] = column.getFormat() ;
			}

			Label label = null;
			// 设置单元格样式：供表头使用
			WritableCellFormat wcfColor = new WritableCellFormat();
			wcfColor.setFont(wfont);
			wcfColor.setBackground(jxl.format.Colour.ICE_BLUE);
			wcfColor.setBorder(jxl.format.Border.ALL,
					jxl.format.BorderLineStyle.THIN, jxl.format.Colour.GRAY_25);

			// 写入表头
			for (int i = 0; i < displayColName.length; i++) {
				label = new Label(i, 0, displayColName[i], wcfColor);
				ws.addCell(label);

			}

			// 设置单元格样式：供数据使用
			WritableCellFormat wcfDataColor = new WritableCellFormat();
			wcfDataColor.setFont(wfont);
			wcfDataColor.setBorder(jxl.format.Border.ALL,
					jxl.format.BorderLineStyle.THIN, jxl.format.Colour.GRAY_25);
			
			// 设置格式化金额
			NumberFormat nf = new NumberFormat("#,##0.00"); 
			WritableCellFormat wcf = new WritableCellFormat(nf);
			wcf.setFont(wfont);
			wcf.setBorder(jxl.format.Border.ALL,
					jxl.format.BorderLineStyle.THIN, jxl.format.Colour.GRAY_25);


			// 写入数据行
			int row = 1;
			while (rs.next()) {
				for (int i = 0; i < colName.length; i++) {
					
					if("showMoney".equals(format[i])) {
						   jxl.write.Number nb = new jxl.write.Number(i, row, rs.getDouble(colName[i]), wcf);
						   ws.addCell(nb);
					}else {
						label = new Label(i, row, rs.getString(colName[i]),
								wcfDataColor);
						ws.addCell(label);
					}
				}
				row++;
			}

			// 写入EXCEL
			wwb.write();
			// 关闭EXCEL
			wwb.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(conn);

			os.close();
		}

		return null;
	}
}
