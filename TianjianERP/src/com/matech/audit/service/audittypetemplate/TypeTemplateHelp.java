package com.matech.audit.service.audittypetemplate;

import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.matech.audit.service.manuscript.ManuFileService;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;
import com.matech.framework.pub.util.UTILString;
import com.matech.framework.pub.util.ZipUtil;

/**
 *	模板对应的【作业指导】 
 */
public class TypeTemplateHelp {

	private Connection conn;
	
	private ZipUtil zipUtil;

	private Map parameters = null;

	private ManuFileService manuScriptService;	//底稿管理类 

	//设置允许用户上传文件大小,单位:字节
	//共10M
	private long sizeMax = 1000485760;
	
	public TypeTemplateHelp(Connection conn) throws Exception {
		this.conn = conn;
		this.zipUtil = new ZipUtil();
		this.manuScriptService = new ManuFileService(conn);
	}
	
	//	模板树
	public String getSubTree(String typeId, String parentaskId) throws Exception {
		if (typeId == null) {
			return "";
		}
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			
			String sql = "select a.taskId,a.taskCode,a.taskName,a.isleaf,ifnull(b.id,'-1') as helpid "
						+ " from k_tasktemplate a "
						+ " left join k_tasktemplatehelp b on a.taskid = b.taskid and a.typeid = b.typeid "
						+ " where a.parenttaskid=? "
						+ " and a.typeId=? "
						+ " and a.isleaf = 0 "
						+ " order by orderid";

			ps = conn.prepareStatement(sql);
			ps.setString(1, parentaskId);
			ps.setString(2, typeId);

			rs = ps.executeQuery();
			StringBuffer sb = new StringBuffer("");
			while (rs.next()) {
				String helpid = "";
				if(rs.getInt(5) != -1){
					helpid = "[完成]";
				}
				
				sb.append("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
				if (rs.getInt(4) == 0) {
					sb.append("<tr style=\"cursor: hand;\">");
					sb.append("<td width=\"1\" height=\"1\" align=\"right\">");
					sb.append("<img onclick=\"getSubTree(" + rs.getString(1) + ");\" id=\"ActImg" + rs.getString(1) + "\" src=\"../images/plus.jpg\" width=\"11\" height=\"11\" />");
					sb.append("</td>");
					sb.append("<td align=left valign=\"bottom\" nowrap><span style='color:#0000CC;' onclick=\"doIt(this," + rs.getString(1) + ","+rs.getString(5)+");\"><font size=2>&nbsp;" + rs.getString(2) + "&nbsp;" + rs.getString(3) + "</font><font size=2 color=red>" + helpid + "</font></span></td>");
					sb.append("</tr>");
				} else {
					String picName = "";
					sb.append("<tr style=\"cursor: hand;\">");
					sb.append("<td width=\"27\" height=\"16\" nowrap align=\"left\">");
					String extendName = rs.getString(3);
					if (extendName.lastIndexOf(".") <= -1) {
						continue;
					}

					extendName = extendName.substring(extendName.lastIndexOf("."));

					if (".doc".equalsIgnoreCase(extendName)) {
						picName = "word" + picName + ".gif";
					} else if (".xls".equalsIgnoreCase(extendName)) {
						picName = "excel" + picName + ".gif";
					}

					sb.append("<img id=\"ActImg\" src=\"/AuditSystem/images/office/" + picName + "\" />");
					sb.append("</td>");
					sb.append("<td align=left valign=\"bottom\" nowrap><span style='color:#0000CC;' onclick=\"doOpenIt(this," + rs.getString(1) + ","+rs.getString(5)+");\"><font size=2>&nbsp;"
									+ rs.getString(2) + "&nbsp;" + rs.getString(3) + "</font><font size=2 color=red>" + helpid + "</font></span></td>");
					sb.append("</tr>");
				}
				sb.append("<tr>");
				sb.append("<td id='subImg" + rs.getString(1) + "' style='display:none'></td>");
				sb.append("<td id='subTree" + rs.getString(1) + "' style='display:none'></td>");
				sb.append("</tr>");
				sb.append("</table>");
			}
			return sb.toString();

		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
		}
	}
	
	
	public String upload(String typeId, HttpServletRequest request) {
		// 定义一个HashMap，存放请求参数
		if (this.parameters == null) {
			parameters = new HashMap();
		}
		//上传项目只要足够小，就应该保留在内存里。
		//较大的项目应该被写在硬盘的临时文件上。
		//非常大的上传请求应该避免。
		//限制项目在内存中所占的空间，限制最大的上传请求，并且设定临时文件的位置。
		// Create a factory for disk-based file items
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload fu = new ServletFileUpload(factory);
		ManuFileService manuScriptService = new ManuFileService(conn);
		ASFuntion asfuntion = new ASFuntion();

		//设置允许用户上传文件大小,单位:字节
		//10M
		fu.setSizeMax(sizeMax);

		// 设置缓冲区大小，这里是4kb
		factory.setSizeThreshold(4096);

		Iterator iter = null;

		String id = "";	//k_tasktemplatehelp 表的唯一ID
		
		//读取上传信息
		try {
			List fileItems = fu.parseRequest(request);
			//处理上传项目
			//依次处理每个上传的文件
			iter = fileItems.iterator();
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			return null;
		}

		/**
		 * 获取前台提交的部分值，请注意，这些值有可能通过需要request来取得，
		 * 也有可能需要通过form特殊域处理来取得
		 * 首先是分析request的代码段
		 */

		String filename = "";

		String taskId = asfuntion.showNull(request.getParameter("taskId")); 
		String taskCode = asfuntion.showNull(request.getParameter("taskCode"));
		String userName = asfuntion.showNull(request.getParameter("userName"));
		
		String sql = "";

		byte[] byteData = null;

		PreparedStatement ps = null;
		ResultSet rs = null;

		String fieldFilename = ""; //文件域自己的文件名
		String paramFilename = asfuntion.showNull(request.getParameter("newfilename")); //输入框提供的文件名；
		Debug.print("paramFilename1=" + paramFilename);

		String myfilename = asfuntion.showNull(request.getParameter("myfilename")); //输入框提供的文件名；
		if (myfilename!=null && !myfilename.equals("")){
			paramFilename=myfilename;
		}
		Debug.print("myfilename=" + myfilename);

		/**
		 * 分析FORM特殊域的代码段
		 */
		try {


			//遍历取得所有域，为后面的分别处理做好准备，其中如果有上传文件，会把上传文件对应的item指针保留到attachItem
			FileItem attachItem = null;
			while (iter.hasNext()) {
				FileItem item = (FileItem) iter.next();

				//忽略其他不是文件域的所有表单信息
				if (!item.isFormField()) {
					//上传的是文件信息
					fieldFilename = item.getName();
					if ((fieldFilename == null) || fieldFilename.equals("")
							&& item.getSize() == 0) {
						continue;
					}
					//记住文件ITEM，已被后面处理
					/**
					 * 请注意，这种方法则意味着只支持一次上传一个附件
					 */
					attachItem = item;
				} else {

					try {
						//上传的是普通表单字域，这里读出放到map中备用
						String fieldName = item.getFieldName();
						String value = item.getString("utf-8"); //转换编码

						if ((fieldName == null) || fieldName.equals("")
								&& item.getSize() == 0) {
							continue;
						}

						if (fieldName.equals("newfilename")) {
							//对应新控件WinHTTPPostRequest的保存！
							paramFilename = URLDecoder.decode(value,"gbk");

							paramFilename=paramFilename.replaceAll("\n", "");

							paramFilename=paramFilename.replaceAll("\r", "");

							System.out.println("qwh:paramFilename="+paramFilename);

						}

						if (fieldName.equals("taskId")) {
							taskId = value;
						}

						if (fieldName.equals("taskCode")) {
							taskCode = value;
						}
						
						if (fieldName.equals("userName")) {
							userName = value;
						}
						
						parameters.put(fieldName, value);
						System.out.println("parameters:" + parameters);
					} catch (Exception e) {
						e.printStackTrace();
					}

					//org.util.Debug.prtOut("fieldNames: " + fieldName + " ; " + value);
				}
			} //循环

			/**
			 * 文件名的生成逻辑是，如果前台有提供newfilename参数，则使用newfilename参数；
			 * 否则，直接使用附件名字(要去掉路径)；
			 */
			filename = "";
			if (paramFilename != null && !paramFilename.equals("")) {
				//用户提供了newfilename参数
				filename = paramFilename;
				Debug.print("filename1=" + filename);
			} else {
				//用户没提供，沿用文件名，但是要去掉路径
				filename = this.GetFileName(attachItem.getName()); //获取不带路径的文件名
				Debug.print("filename2=" + filename);
			}


			sql = "select ifnull(max(id),0)+1 from k_tasktemplatehelp where typeid=? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, typeId);
			rs = ps.executeQuery();
			
			if (rs.next()) {
				id = rs.getString(1);
			}
			rs.close();
			ps.close();

			String strSql = "INSERT INTO k_tasktemplatehelp( "
						+ " id, TaskID, TypeID, helpname, ismust, "
						+ " Udate, Username, Property "
						+ " )VALUES( "
						+ " ?,?,?,?,?, "
						+ " ?,?,?)";

			ps = conn.prepareStatement(strSql);
			ps.setString(1, id);
			ps.setString(2, taskId);
			ps.setString(3, typeId);
			ps.setString(4, filename);
			ps.setString(5, "1"); //规程

			ps.setString(6, asfuntion.getCurrentDate() + " " +asfuntion.getCurrentTime()); //所属流程
			ps.setString(7, userName);
			ps.setString(8, "待定");
			
			ps.execute();

			//存储成文件
			try {

				byteData = attachItem.get();
				byteData = new ZipUtil().gzipBytes(byteData);
				manuScriptService.saveFileByHelpId(typeId, taskId, byteData);
				
			} catch (Exception e) {
				Debug.print(Debug.iError, "访问失败", e);
			}

		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			return null;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return taskId;
	}
	
	public String GetFileName(String filepath) {
		String returnstr = filepath;
		int length = filepath.trim().length();

		filepath = filepath.replace('\\', '/');
		if (length > 0) {
			int i = filepath.lastIndexOf("/");
			if (i >= 0) {
				filepath = filepath.substring(i + 1);
				returnstr = filepath;
			}
		}
		return returnstr;
	}
	
	/**
	 * 删除作业指导中的底稿
	 * @param typeId
	 * @param taskId
	 * @throws Exception
	 */
	public void removeHelpTask(String typeId, String taskId) throws Exception {
		String sql;
		ResultSet rs = null;
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;

		try {
			if (taskId != null && !"".equals(taskId)) {

				//找到底稿唯一ID和底稿名称
				sql = "select * from asdb.k_tasktemplatehelp "
					+ " where 1=1"
					+ " and taskId= ? "
					+ " and TypeID= ? ";
				ps = conn.prepareStatement(sql);
				ps.setString(1, taskId);
				ps.setString(2, typeId);
				rs = ps.executeQuery();

				if (rs.next()) {
					//删除底稿文件
					ManuFileService.deleteFileByHelpID(taskId, typeId);

				}

				//从任务表中删除
				sql = "delete from asdb.k_tasktemplatehelp "
					+ " where taskId= ? "
					+ " and typeId = ?";
				ps = conn.prepareStatement(sql);
				ps.setString(1, taskId);
				ps.setString(2, typeId);
				ps.execute();

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(ps2);
		}
	}
}
