package com.matech.audit.service.form;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import net.sf.json.JSONArray;

import com.matech.audit.service.form.model.FormDefine;
import com.matech.audit.service.form.model.FormQuery;
import com.matech.audit.service.form.model.FormVO;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.log.Log;
import com.matech.framework.pub.util.StringUtil;

public class FormDefineService {

	public static final String FORM_TABLE_PRE = "mt_form_";
	public static final String SUB_FORM_TABLE_PRE = "mt_form_sub_";
	public static final String FORM_LIST_GRID_ID_PRE = "formDataGrid_";
	
	private Connection conn;
	private String contextPath = "";
	private Log log = new Log(FormDefineService.class) ;

	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	public FormDefineService(Connection conn) {
		this.conn = conn;
	}

	/**
	 * 更新MT_COM_FORM_FIELD
	 * 
	 * @param formid
	 *            : 表单id
	 * @param html
	 *            : 表单html
	 * @throws Exception
	 */
	public void saveFormField(String formid, String html, boolean isSave) throws Exception {
		PreparedStatement formFieldInsertPS = null;
		PreparedStatement formFieldUpdatePS = null;
		PreparedStatement formFieldUpdatePS2 = null;	//针对已存在表
		PreparedStatement formPS = null;
		PreparedStatement formQueryInsertPS = null;

		try {
			String sql = "";
			String html2 = "";
			String html3 = "";

			DbUtil db = new DbUtil(conn);
			int orderId = 1;

			// 先检查是否已存在，存在就更新，否则就新增
			sql = "insert into MT_COM_FORM_FIELD (uuid,formid,name,enname,orderid, matechext,parentformid) values(?,?,?,?,?, ?,?)";
			formFieldInsertPS = conn.prepareStatement(sql);
			
			sql = "insert into mt_com_form_query (uuid,formid,name,enname,orderid,btype,bshow,bhiddenrow) values(?,?,?,?,?,?,?,?)";
			formQueryInsertPS = conn.prepareStatement(sql);

			sql = "update MT_COM_FORM_FIELD set matechext = ? where formid = ? and name = ? ";
			formFieldUpdatePS = conn.prepareStatement(sql);

			sql = "update MT_COM_FORM_FIELD set matechext = ?, enname=? where formid = ? and name = ? ";
			formFieldUpdatePS2 = conn.prepareStatement(sql);

			
			FormDefine formDefine = getFormDefine(formid);

			while (true) {
				if (html.indexOf("matech_ext=\"") > -1) {
					html2 = html.substring(html.indexOf("matech_ext=\"") + 12,
							html.indexOf("ext_end\"")); // 要替换属性
					html3 = html.substring(html.indexOf("ext_end\"") + 8); // 结束

					String[] propertys = html2.split(";"); // 属性数组

					String name = "";
					String field = "";
					String subFormId = "";

					if (html2.indexOf("ext_type=sublist") > -1) {
						String subTableName = "";
						
						//找到子表的名称
						for (int i = 0; i < propertys.length; i++) {
							if (propertys[i].indexOf("ext_tableName=") > -1) {
								subTableName = StringUtil.replaceStr(propertys[i], "ext_tableName=", "");
								break;
							}
						}

						for (int i = 0; i < propertys.length; i++) {
							if (propertys[i].indexOf("ext_listTitle=") > -1) {
								propertys[i] = StringUtil.replaceStr(
										propertys[i], "ext_listTitle=", "");
								
								// 检查是否已存在
								
								if(!"".equals(subTableName)) {
									//手工建表
									sql = "SELECT UUID FROM MT_COM_FORM WHERE PARENTFORMID = ? AND TABLENAME = ? ";
									subFormId = StringUtil.showNull(db.queryForString(sql, new String[] {formid, subTableName}));
								} else {
									//自动建表
									sql = "SELECT UUID FROM MT_COM_FORM WHERE PARENTFORMID = ? AND NAME = ? ";
									subFormId = StringUtil.showNull(db.queryForString(sql, new String[] {formid, propertys[i] }));
								}

								if ("".equals(subFormId)) {
									
									String enName = "";
									String tableName = "";
									
									if(subTableName != null && !"".equals(subTableName)) {
										//手工建子表
										enName = subTableName.toUpperCase();
										tableName = subTableName.toUpperCase();
									} else {
										enName = StringUtil.getCurDateTime2();
										tableName = SUB_FORM_TABLE_PRE + enName;
										
										// 新增子表单对应表
										createTable(tableName);
									}
									
									subFormId = StringUtil.getUUID(); // 子表单的formid

									sql = "INSERT INTO MT_COM_FORM(UUID,NAME,ENNAME,PARENTFORMID,TABLENAME, TABLETYPE) VALUES(?,?,?,?,?, ?)";
									formPS = conn.prepareStatement(sql);
									formPS.setString(1, subFormId);
									formPS.setString(2, propertys[i]);
									formPS.setString(3, enName);
									formPS.setString(4, formid);
									formPS.setString(5, tableName);
									formPS.setString(6, "0");
									formPS.execute();
									DbUtil.close(formPS);
								} else {
									//修改
									
								}
							} else if (propertys[i].indexOf("ext_column=") > -1) {
								// 子表单对应的列
								propertys[i] = StringUtil.replaceStr(propertys[i], "ext_column={", "");
								propertys[i] = StringUtil.replaceStr(propertys[i], "}", "");

								String[] subFormListProperty = propertys[i].split(",");
								
								String fieldName = "";	//子表字段名（中文）
								String subField = "";	//子表数据库字段
								
								for (int j = 0; j < subFormListProperty.length; j++) {
									if (subFormListProperty[j].indexOf("ext_name") > -1) {
										subFormListProperty[j] = StringUtil.replaceStr(subFormListProperty[j], "ext_name=", "");
										fieldName = subFormListProperty[j];

									} else if (subFormListProperty[j].indexOf("ext_field") > -1) {
										//子表手工建表字段
										subFormListProperty[j] = StringUtil.replaceStr(
												subFormListProperty[j], "ext_field=", "");
										subField = subFormListProperty[j];
									}
								}
									
								// 检查是否已存在
								sql = "select count(*) from MT_COM_FORM_FIELD where formid = ? and name = ? ";
								int opt = db.queryForInt(sql,new String[] {subFormId, fieldName });

								if (opt > 0) {
									// 已存在，更新
									if (!"".equals(subTableName)) {
										// 手工建表
										
										formFieldUpdatePS2.setString(1, propertys[i]);
										formFieldUpdatePS2.setString(2, subField);
										formFieldUpdatePS2.setString(3, subFormId);
										formFieldUpdatePS2.setString(4, fieldName);
										formFieldUpdatePS2.executeUpdate();
										
									} else {
										// 字段已存在，更新
										formFieldUpdatePS.setString(1, propertys[i]);
										formFieldUpdatePS.setString(2, subFormId);
										formFieldUpdatePS.setString(3, fieldName);
										formFieldUpdatePS.executeUpdate();
									}
									
								} else {
									int maxOrderId = 0;
									// 不存在，新增：先找出最大orderid,再新增
									sql = "select max(orderid) as orderid from MT_COM_FORM_FIELD where formid = ? ";
									maxOrderId = db.queryForInt(sql,
											new String[] { subFormId });

									if (maxOrderId == -1) {
										maxOrderId = 0;
									}

									orderId = maxOrderId + 1;

									if ("".equals(subTableName)) {
										// 手工建表
										subField = "v" + orderId;
									}
									formFieldInsertPS.setString(1, StringUtil.getUUID());
									formFieldInsertPS.setString(2, subFormId);
									formFieldInsertPS.setString(3, fieldName);
									formFieldInsertPS.setString(4, subField);
									formFieldInsertPS.setString(5, String.valueOf(orderId));
									formFieldInsertPS.setString(6, propertys[i]);
									formFieldInsertPS.setString(7, formid);
									formFieldInsertPS.executeUpdate();
								}
							}
						}
					} else {
						for (int i = 0; i < propertys.length; i++) {
							if (propertys[i].indexOf("ext_name") > -1) {
								propertys[i] = StringUtil.replaceStr(
										propertys[i], "ext_name=", "");
								name = propertys[i];
							} else if (propertys[i].indexOf("ext_field") > -1) {
								propertys[i] = StringUtil.replaceStr(
										propertys[i], "ext_field=", "");
								field = propertys[i];
							}
						}

						// 检查是否已存在
						sql = "select count(*) from MT_COM_FORM_FIELD where formid = ? and name = ? ";
						int opt = db.queryForInt(sql, new String[] { formid,
								name });

						if (opt > 0) {
							
							if ("1".equals(formDefine.getTableType())) {
								// 手工建表
								
								formFieldUpdatePS2.setString(1, html2);
								formFieldUpdatePS2.setString(2, field);
								formFieldUpdatePS2.setString(3, formid);
								formFieldUpdatePS2.setString(4, name);
								formFieldUpdatePS2.executeUpdate();
								
							} else {
								// 字段已存在，更新
								formFieldUpdatePS.setString(1, html2);
								formFieldUpdatePS.setString(2, formid);
								formFieldUpdatePS.setString(3, name);
								formFieldUpdatePS.executeUpdate();
							}
							
						} else {
							String fieldName = "";
							int maxOrderId = 0;
							// 不存在，新增：先找出最大orderid,再新增
							sql = "select max(orderid) as orderid from MT_COM_FORM_FIELD where formid = ? ";
							maxOrderId = db.queryForInt(sql,
									new String[] { formid });

							if (maxOrderId == -1) {
								maxOrderId = 0;
							}

							orderId = maxOrderId + 1;
							if ("1".equals(formDefine.getTableType())) {
								// 手工建表
								fieldName = field;
								
							} else {
								// 自动建表
								fieldName = "v" + orderId;
							}

							formFieldInsertPS.setString(1, StringUtil.getUUID());
							formFieldInsertPS.setString(2, formid);
							formFieldInsertPS.setString(3, name);
							formFieldInsertPS.setString(4, fieldName);
							formFieldInsertPS.setString(5, String.valueOf(orderId));
							formFieldInsertPS.setString(6, html2);
							formFieldInsertPS.setString(7, "");
							formFieldInsertPS.executeUpdate();
							
							if(isSave) {
								//如果是第一次保存，才初始化该字段
								//初始化query表  uuid,formid,name,enname,orderid,btype,bshow,bhiddenrow
								formQueryInsertPS.setString(1, StringUtil.getUUID());
								formQueryInsertPS.setString(2, formid);
								formQueryInsertPS.setString(3, name);
								formQueryInsertPS.setString(4, fieldName);
								formQueryInsertPS.setString(5, String.valueOf(orderId));
								formQueryInsertPS.setString(6, "showLeft");
								formQueryInsertPS.setInt(7, 1);
								formQueryInsertPS.setInt(8, 1);
								
								formQueryInsertPS.executeUpdate();
							}
						}
					}

					html = html3;
				} else {
					break;
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(formFieldUpdatePS2);
			DbUtil.close(formFieldInsertPS);
			DbUtil.close(formFieldUpdatePS);
			DbUtil.close(formQueryInsertPS);
			DbUtil.close(formPS);
		}
	}

	/**
	 * 检查html，看看有没有input的属性值要替换
	 * 
	 * @param html
	 *            : 表单html
	 * @return
	 * @throws Exception
	 */
//	public String input(String formId, String uuid, String html) throws Exception {
//		PreparedStatement ps = null;
//		ResultSet rs = null;
//
//		try {
//
//			DbUtil db = new DbUtil(conn);
//
//			String propertyHtmlStart = "";
//			String propertyHtml = "";
//			String propertyHtmlEnd = "";
//
//			List fieldList = db.getList("MT_COM_FORM_FIELD", "formid", formId);
//			
//			String tableName = getFormDefine(formId).getTableName();
//
//			// 普通文本输入框、密码输入框、单选、多选、隐藏
//			Map dataMap = db.get(tableName, "uuid", uuid);
//			
//			// 检查html，看看有没有input的属性值要替换
//			while (true) {
//				if (html.indexOf("matech_ext=\"") > -1) {
//					// input 有属性值要替换
//					propertyHtmlStart = html.substring(0, html.indexOf("matech_ext=\"")); // 开头
//					propertyHtmlStart = propertyHtmlStart.substring(0, propertyHtmlStart.lastIndexOf("<input")); // 开头
//
//					propertyHtml = html.substring(html.indexOf("matech_ext=\"") + 12, html.indexOf("ext_end\"")); // 要替换属性
//
//					propertyHtmlEnd = html.substring(html.indexOf("ext_end\"") + 8); // 结束
//					propertyHtmlEnd = propertyHtmlEnd.substring(propertyHtmlEnd.indexOf(">") + 1); // 结束
//
//					String[] propertys = propertyHtml.split(";"); // 属性数组
//
//					if (propertyHtml.indexOf("ext_type=textarea") > -1) {
//						
//						String value = "";
//						// 多行文本框
//						propertyHtml = "<textarea ";
//						for (int i = 0; i < propertys.length; i++) {
//						
//							
//							if (propertys[i].indexOf("ext_name=") > -1) {
//								String htmlName = StringUtil.replaceStr(propertys[i], "ext_name=", "");
//								
//								for (int j = 0; j < fieldList.size(); j++) { 
//										
//									Map map = (Map) fieldList.get(j);
//									
//									String name = StringUtil.showNull(String.valueOf(map.get("name")));
//
//									if (name.equals(htmlName)) {
//										
//										String enName = StringUtil.showNull(String.valueOf(map.get("enname"))).toLowerCase();
//										
//										propertyHtml += " name=\"" + enName + "\" ";
//										
//										if(!"".equals(uuid) && dataMap != null) {
//											value = StringUtil.showNull(String.valueOf(dataMap.get(enName)));
//										}
//									}
//								}
//								
//							} else {
//								if (propertys[i].indexOf("ext_type") < 0) {
//									propertyHtml += " " + StringUtil.replaceStr(propertys[i],
//													"ext_", "") + " ";
//								}
//							}
//						}
//						propertyHtml += ">" + value + "</textarea>";
//
//					} else if (propertyHtml.indexOf("ext_type=sublist") > -1) {
//
//						// 列表
//						List subFieldList = new ArrayList();
//						List subFormList = new ArrayList();
//						String subFormTableName = "";
//						
//						int colsLength = 0;
//						String html4 = propertyHtml;
//						propertyHtml = "<table cellspacing='1' cellpadding='3' border='0' class='listTable'>";
//						// 标题
//						if (html4.indexOf("ext_listTitle") > -1) {
//							
//							String ext_listTitle = (html4.substring(html4
//									.indexOf("ext_listTitle="))).substring(
//									(html4.substring(html4
//											.indexOf("ext_listTitle=")))
//											.indexOf("ext_listTitle=") + 14,
//									(html4.substring(html4
//											.indexOf("ext_listTitle=")))
//											.indexOf(";"));
//							
//							propertyHtml += " <caption>" + ext_listTitle
//									+ "</caption> ";
//							// list : 通过子标题，求出对应s_formfield列 ，同时求出对应的数据表
//							// 通过数据表求出数据记录
//							String subFormId = "";
//							String sql = "SELECT uuid,enname,tableName FROM MT_COM_FORM WHERE PARENTFORMID = ? AND NAME = ? ";
//							ps = conn.prepareStatement(sql);
//							ps.setString(1, formId);
//							ps.setString(2, ext_listTitle);
//							rs = ps.executeQuery();
//							if (rs.next()) {
//								subFormId = rs.getString("uuid");
//								subFormTableName = StringUtil.showNull(rs.getString("tableName")).toUpperCase();
//							}
//							
//							DbUtil.close(rs);
//							DbUtil.close(ps);
//
//							if (!"".equals(subFormId)) {
//								subFieldList = db.getList("mt_com_form_field","formid", subFormId); // 对应的s_formfield列
//								subFormList = db.getList(subFormTableName, "mainformid", uuid); // 数据记录，有多条
//							}
//						}
//						
//						String ext_group = "" ;
//						if (html4.indexOf("ext_group") > -1) {
//							ext_group = (html4.substring(html4
//									.indexOf("ext_group="))).substring(
//									(html4.substring(html4
//											.indexOf("ext_group=")))
//											.indexOf("ext_group=") + 10,
//									(html4.substring(html4
//											.indexOf("ext_group=")))
//											.indexOf(";"));
//						}
//						
//						String ext_selectList = "";
//						if(html4.indexOf("ext_selectList") > -1) {
//							String[] selectLists = StringUtil.getVaribles("ext_selectList={", "}", html4);
//							if(selectLists.length > 0) {
//								ext_selectList = selectLists[0];
//							}
//						}
//						
//						//propertyHtml += "<tr><td><table border=1 style='width:100%'>";
//
//						// 表头
//						for (int ii = 0; ii < propertys.length; ii++) {
//							if (propertys[ii].indexOf("ext_columnName") > -1) {
//								// ext_columnName=A|A|A`B|B|E`C|D|E
//								propertys[ii] = StringUtil.replaceStr(
//										propertys[ii], "ext_columnName=", "");
//
//								// 读出列宽
//								String columnWidth = "";
//								try {
//									columnWidth = (html4.substring(html4
//											.indexOf("ext_columnWidth=")))
//											.substring(
//													(html4.substring(html4
//															.indexOf("ext_columnWidth=")))
//															.indexOf("ext_columnWidth=") + 16,
//													(html4.substring(html4
//															.indexOf("ext_columnWidth=")))
//															.indexOf(";"));
//									columnWidth = "`" + columnWidth;
//								} catch (Exception e) {
//									// 列宽为空
//									columnWidth = "";
//								}
//
//								// rowspan="1" colspan="3"
//								// 变为一个两维数组
//								String[] rows = propertys[ii].split("`");
//								String[][] fields = new String[rows.length][(rows[0]
//										.split("\\|")).length + 1];// fields[行][列]
//
//								colsLength = fields[0].length;
//
//								for (int i = 0; i < rows.length; i++) {
//									// 无条件增加一个操作列
//									rows[i] = "+|" + rows[i];
//									String[] cols = rows[i].split("\\|");
//									for (int j = 0; j < cols.length; j++) {
//										fields[i][j] = cols[j];
//										if (i == 0 && "".equals(columnWidth)) {
//											columnWidth += "`10%";
//										}
//									}
//
//								}
//
//								String[] width = ("10px" + columnWidth)
//										.split("`");
//
//								//取得第一列的名称
//								Map firstFieldMap = (Map)subFieldList.get(0);
//								String firstFieldEnName = StringUtil.showNull(String.valueOf(firstFieldMap.get("enname"))).toLowerCase();
//								String gridSelectListValue = "";
//								
//								//拼出多选框的值
//								for (int k = 0; k < subFormList.size(); k++) {
//									Map firstDataMap = (Map) subFormList.get(k);
//									
//									gridSelectListValue += String.valueOf(firstDataMap.get(firstFieldEnName));
//									if(k != subFormList.size()-1) {
//										gridSelectListValue += ",";
//									}
//								}
//								
//								for (int i = 0; i < fields.length; i++) { // fields[行][列]
//									propertyHtml += "<tr>";
//									
//									for (int j = 0; j < fields[i].length; j++) {
//										String w=j<width.length?width[j]:"11%";
//										propertyHtml += mergerLine(fields[i][j], fields, i, j, w, subFormTableName, ext_selectList, gridSelectListValue);
//									}
//									propertyHtml += "</tr>";
//								}
//
//							} else {
//								continue;
//							}
//						}
//
//						// 新增列
//						propertyHtml += "<tbody id = '" + subFormTableName + "' >";
//						List columnFieldList = new ArrayList();
//
//						for (int ii = 0; ii < propertys.length; ii++) {
//							if (propertys[ii].indexOf("ext_column=") > -1) {
//								String columnHtml = StringUtil.replaceStr(
//										propertys[ii], "ext_column={", "");
//								columnHtml = StringUtil.replaceStr(
//										columnHtml, "}", "");
//
//								String[] columnFields = columnHtml.split(",");
//
//								String lhtml = inputProperty(formId, columnFields, uuid,
//										subFieldList, null, subFormTableName + "_", "", "");
//
//								columnFieldList.add(lhtml);
//
//							}
//						}
//						String json = JSONArray.fromObject(columnFieldList).toString(); // 新增行
//						propertyHtml += "<textarea style=\"display:none;\" id=\"mt_slist_" + subFormTableName + "\" name=\"mt_slist_" + subFormTableName + "\">"
//									+ json 
//									+ "</textarea> \n";
//
//						String strValue = "";
//						String enName = "";
//						for (int i = 0; i < subFieldList.size(); i++) {
//							Map map = (Map) subFieldList.get(i);
//							enName = StringUtil.showNull(
//									String.valueOf(map.get("enname"))).toLowerCase();
//							strValue += "`" + enName;
//						}
//						if (!"".equals(strValue))
//							strValue = strValue.substring(1);
//						propertyHtml += "<textarea style=\"display:none;\" id=\"mt_value_" + subFormTableName
//									+ "\" name=\"mt_value_" + subFormTableName + "\" >" + strValue + "</textarea>\n";
//						
//						//取得第一列的名称
//						Map firstFieldMap = (Map)subFieldList.get(0);
//						String firstFieldEnName = StringUtil.showNull(String.valueOf(firstFieldMap.get("enname"))).toLowerCase();
//
//						for (int i = 0; i < subFormList.size(); i++) {
//							Map map = (Map) subFormList.get(i);
//							propertyHtml += "<tr keyValue=\"" + map.get(firstFieldEnName) + "\">\n";
//							
//							String groupValue = StringUtil.showNull(map.get(ext_group.toLowerCase())) ;
//							
//							if(!"".equals(groupValue)) {
//								groupValue = "group=" + groupValue ;
//							}
//							propertyHtml += "<td><img id='"+subFormTableName+"_delImg_"+i+"' "+groupValue+" flag="+subFormTableName+"_del style='cursor:hand;' " 
//										 +"alt='删除本行' onclick='mt_remove(this)' src='" + contextPath + "/img/close.gif' ></td>\n";
//
//							int cellCount = 0;
//							
//							for (int ii = 0; ii < propertys.length; ii++) {
//								if (propertys[ii].indexOf("ext_column=") > -1) {
//									String sf_propertys = StringUtil.replaceStr(propertys[ii], "ext_column={", "");
//									sf_propertys = StringUtil.replaceStr(sf_propertys, "}", "");
//
//									String[] lpropertys = sf_propertys.split(",");
//									
//									String inputHtml = inputProperty(formId, lpropertys,uuid, subFieldList, map, subFormTableName + "_", "_" + cellCount, "_" + i);
//									
//									if(sf_propertys.indexOf("ext_type=hidden") > -1) {
//										propertyHtml += inputHtml ;
//									}else {
//										propertyHtml += "<td>" + inputHtml + "</td>\n";
//										
//									}
//
//									
//									cellCount++;
//								}
//							}
//
//							propertyHtml += "</tr>\n";
//						}
//
//						propertyHtml += "</tbody>";
//
//						// 合计列
//
//						//propertyHtml += "</table></td></tr>";
//						propertyHtml += "</table>";
//
//					} else {
//						
//						propertyHtml = inputProperty(formId, propertys, uuid, fieldList, dataMap, "", "", "");
//					}
//
//					html = propertyHtmlStart + propertyHtml + propertyHtmlEnd;
//				} else {
//					break;
//				}
//			}
//
//			return html;
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw e;
//		}
//	}

	public String input(String formId, String uuid, String html,HttpServletRequest request) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			DbUtil db = new DbUtil(conn);

			String propertyHtmlStart = "";
			String propertyHtml = "";
			String propertyHtmlEnd = "";

			List fieldList = db.getList("MT_COM_FORM_FIELD", "formid", formId);
			
			String tableName = getFormDefine(formId).getTableName();

			// 普通文本输入框、密码输入框、单选、多选、隐藏
			Map dataMap = db.get(tableName, "uuid", uuid);
			
			String view = StringUtil.showNull(request.getParameter("view")) ;
			
			// 检查html，看看有没有input的属性值要替换
			while (true) {
				if (html.indexOf("matech_ext=\"") > -1) {
					// input 有属性值要替换
					propertyHtmlStart = html.substring(0, html.indexOf("matech_ext=\"")); // 开头
					propertyHtmlStart = propertyHtmlStart.substring(0, propertyHtmlStart.lastIndexOf("<input")); // 开头

					propertyHtml = html.substring(html.indexOf("matech_ext=\"") + 12, html.indexOf("ext_end\"")); // 要替换属性

					propertyHtmlEnd = html.substring(html.indexOf("ext_end\"") + 8); // 结束
					propertyHtmlEnd = propertyHtmlEnd.substring(propertyHtmlEnd.indexOf(">") + 1); // 结束

					String[] propertys = propertyHtml.split(";"); // 属性数组

					if (propertyHtml.indexOf("ext_type=textarea") > -1) {
						
						String value = "";
						// 多行文本框
						propertyHtml = "<textarea ";
						for (int i = 0; i < propertys.length; i++) {
						
							
							if (propertys[i].indexOf("ext_name=") > -1) {
								String htmlName = StringUtil.replaceStr(propertys[i], "ext_name=", "");
								
								for (int j = 0; j < fieldList.size(); j++) { 
										
									Map map = (Map) fieldList.get(j);
									
									String name = StringUtil.showNull(String.valueOf(map.get("name")));

									if (name.equals(htmlName)) {
										
										String enName = StringUtil.showNull(String.valueOf(map.get("enname"))).toLowerCase();
										
										propertyHtml += " name=\"" + enName + "\" ";
										
										if(!"".equals(uuid) && dataMap != null) {
											value = StringUtil.showNull(String.valueOf(dataMap.get(enName)));
										}
									}
								}
								
							} else {
								if (propertys[i].indexOf("ext_type") < 0) {
									propertyHtml += " " + StringUtil.replaceStr(propertys[i],
													"ext_", "") + " ";
								}
							}
						}
						propertyHtml += ">" + value + "</textarea>";

					} else if (propertyHtml.indexOf("ext_type=sublist") > -1) {

						// 列表
						List subFieldList = new ArrayList();
						List subFormList = new ArrayList();
						String subFormTableName = "";
						
						int colsLength = 0;
						String html4 = propertyHtml;
						propertyHtml = "<table cellspacing='1' cellpadding='3' border='0' class='listTable'>";
						
						
						
						// 标题
						if (html4.indexOf("ext_listTitle") > -1) {
							
							String ext_listTitle = (html4.substring(html4
									.indexOf("ext_listTitle="))).substring(
									(html4.substring(html4
											.indexOf("ext_listTitle=")))
											.indexOf("ext_listTitle=") + 14,
									(html4.substring(html4
											.indexOf("ext_listTitle=")))
											.indexOf(";"));
							
							propertyHtml += " <caption>" + ext_listTitle
									+ "</caption> ";
							// list : 通过子标题，求出对应s_formfield列 ，同时求出对应的数据表
							// 通过数据表求出数据记录
							String subFormId = "";
							String sql = "SELECT uuid,enname,tableName FROM MT_COM_FORM WHERE PARENTFORMID = ? AND NAME = ? ";
							ps = conn.prepareStatement(sql);
							ps.setString(1, formId);
							ps.setString(2, ext_listTitle);
							rs = ps.executeQuery();
							if (rs.next()) {
								subFormId = rs.getString("uuid");
								subFormTableName = StringUtil.showNull(rs.getString("tableName")).toUpperCase();
							}
							
							DbUtil.close(rs);
							DbUtil.close(ps);

							if (!"".equals(subFormId)) {
								subFieldList = db.getList("mt_com_form_field","formid", subFormId); // 对应的s_formfield列
								String subFieldOrderBy = "" ;
								
								//子列表排序
								String firstOrderBy = db.checkFieldExists(subFormTableName,"orderid") ? "orderid," : "" ;
								
								for(int kk=0;kk<subFieldList.size();kk++){
									Map map = (Map)subFieldList.get(kk) ;
									String tempOrderBy = StringUtil.showNull((String)map.get("orderby")) ;
									String tempFieldName = (String)map.get("enname") ;
									
									if(!"".equals(tempOrderBy)){
										subFieldOrderBy += tempFieldName + " " +  tempOrderBy + "," ;
									}
								}
								if(!"".equals(subFieldOrderBy)){
									subFieldOrderBy = subFieldOrderBy.substring(0,subFieldOrderBy.length() -1) ;
								}else {
									if(!"".equals(firstOrderBy)){
										firstOrderBy = firstOrderBy.substring(0,firstOrderBy.length() -1) ;
									}
								}
								subFieldOrderBy = firstOrderBy + subFieldOrderBy ;
								subFormList = db.getList(subFormTableName, "mainformid", uuid,subFieldOrderBy); // 数据记录，有多条
							}
						}
						
						Map subfieldTypeMap = db.getFieldType(subFormTableName) ;
						
						String ext_group = "" ;
						if (html4.indexOf("ext_group") > -1) {
							ext_group = (html4.substring(html4
									.indexOf("ext_group="))).substring(
									(html4.substring(html4
											.indexOf("ext_group=")))
											.indexOf("ext_group=") + 10,
									(html4.substring(html4
											.indexOf("ext_group=")))
											.indexOf(";"));
						}
						
						String ext_selectList = "";
						if(html4.indexOf("ext_selectList") > -1) {
							String[] selectLists = StringUtil.getVaribles("ext_selectList={", "}", html4);
							if(selectLists.length > 0) {
								ext_selectList = selectLists[0];
							}
						}
						
						String ext_view = "" ;
						if (html4.indexOf("ext_view") > -1) {
							ext_view = (html4.substring(html4
									.indexOf("ext_view="))).substring(
									(html4.substring(html4
											.indexOf("ext_view=")))
											.indexOf("ext_view=") + 9,
									(html4.substring(html4
											.indexOf("ext_view=")))
											.indexOf(";"));
						}
						
						//propertyHtml += "<tr><td><table border=1 style='width:100%'>";

						// 表头
						for (int ii = 0; ii < propertys.length; ii++) {
							if (propertys[ii].indexOf("ext_columnName") > -1) {
								// ext_columnName=A|A|A`B|B|E`C|D|E
								propertys[ii] = StringUtil.replaceStr(
										propertys[ii], "ext_columnName=", "");

								// 读出列宽
								String columnWidth = "";
								try {
									columnWidth = (html4.substring(html4
											.indexOf("ext_columnWidth=")))
											.substring(
													(html4.substring(html4
															.indexOf("ext_columnWidth=")))
															.indexOf("ext_columnWidth=") + 16,
													(html4.substring(html4
															.indexOf("ext_columnWidth=")))
															.indexOf(";"));
									columnWidth = "`" + columnWidth;
								} catch (Exception e) {
									// 列宽为空
									columnWidth = "";
								}

								// rowspan="1" colspan="3"
								// 变为一个两维数组
								String[] rows = propertys[ii].split("`");
								String[][] fields = new String[rows.length][(rows[0]
										.split("\\|")).length + 1];// fields[行][列]

								colsLength = fields[0].length;
								
								int colsCount = 0 ;

								for (int i = 0; i < rows.length; i++) {
									// 无条件增加一个操作列
									rows[i] = "+|" + rows[i];
									String[] cols = rows[i].split("\\|");
									for (int j = 0; j < cols.length; j++) {
										fields[i][j] = cols[j];
										if (i == 0 && "".equals(columnWidth)) {
											columnWidth += "`10%";
										}
									}
								}

								String[] width = ("10px" + columnWidth)
										.split("`");

								//取得第一列的名称
								Map firstFieldMap = (Map)subFieldList.get(0);
								String firstFieldEnName = StringUtil.showNull(String.valueOf(firstFieldMap.get("enname"))).toLowerCase();
								String gridSelectListValue = "";
								
								//拼出多选框的值
								for (int k = 0; k < subFormList.size(); k++) {
									Map firstDataMap = (Map) subFormList.get(k);
									
									gridSelectListValue += String.valueOf(firstDataMap.get(firstFieldEnName));
									if(k != subFormList.size()-1) {
										gridSelectListValue += ",";
									}
								}
								
								for (int i = 0; i < fields.length; i++) { // fields[行][列]
									propertyHtml += "<tr>";
									
									for (int j = 0; j < fields[i].length; j++) {
										propertyHtml += mergerLine(fields[i][j], fields, i, j, width[j], subFormTableName, ext_selectList, gridSelectListValue,ext_view,view);
									}
									propertyHtml += "</tr>";
								}

							} else {
								continue;
							}
						}
						
						String colHtml = "" ;
						for(int tt=0;tt<colsLength;tt++){
							colHtml += "<col id="+subFormTableName+"_"+"col"+(tt+1)+" />" ;
						}

						// 新增列
						propertyHtml += "<tbody id = '" + subFormTableName + "' >";
						List columnFieldList = new ArrayList();

						for (int ii = 0; ii < propertys.length; ii++) {
							if (propertys[ii].indexOf("ext_column=") > -1) {
								String columnHtml = StringUtil.replaceStr(
										propertys[ii], "ext_column={", "");
								columnHtml = StringUtil.replaceStr(
										columnHtml, "}", "");

								String[] columnFields = columnHtml.split(",");

								String lhtml = inputProperty(formId, columnFields, uuid,
										subFieldList, null, subFormTableName + "_", "", "", subfieldTypeMap,null,request);

								columnFieldList.add(lhtml);

							}
						}
						String json = JSONArray.fromObject(columnFieldList).toString(); // 新增行
						propertyHtml += "<textarea style=\"display:none;\" id=\"mt_slist_" + subFormTableName + "\" name=\"mt_slist_" + subFormTableName + "\">"
									+ json 
									+ "</textarea> \n";

						String strValue = "";
						String enName = "";
						for (int i = 0; i < subFieldList.size(); i++) {
							Map map = (Map) subFieldList.get(i);
							enName = StringUtil.showNull(
									String.valueOf(map.get("enname"))).toLowerCase();
							strValue += "`" + enName;
						}
						if (!"".equals(strValue))
							strValue = strValue.substring(1);
						propertyHtml += "<textarea style=\"display:none;\" id=\"mt_value_" + subFormTableName
									+ "\" name=\"mt_value_" + subFormTableName + "\" >" + strValue + "</textarea>\n";
						
						//取得第一列的名称
						Map firstFieldMap = (Map)subFieldList.get(0);
						String firstFieldEnName = StringUtil.showNull(String.valueOf(firstFieldMap.get("enname"))).toLowerCase();
						
						
						for (int i = 0; i < subFormList.size(); i++) {
							Map map = (Map) subFormList.get(i);
							propertyHtml += "<tr keyValue=\"" + map.get(firstFieldEnName) + "\">\n";
							
							String groupValue = StringUtil.showNull(map.get(ext_group.toLowerCase())) ;
							
							if(!"".equals(groupValue)) {
								groupValue = "group=" + groupValue ;
							}
							
							propertyHtml += "<td>" ;
							
							if(!"true".equals(view)) {
								propertyHtml += "<img id='"+subFormTableName+"_delImg_"+i+"' "+groupValue+" flag="+subFormTableName+"_del style='cursor:hand;' " 
										 +"alt='删除本行' onclick='mt_remove(this)' src='" + contextPath + "/img/close.gif' >&nbsp;&nbsp;"
										 + "<input type='hidden' name='" + subFormTableName + "_subformuuid' id='" + subFormTableName + "_subformuuid_" + i + "' "
										 + " value='" + map.get("uuid") + "' />" ;
							
								if(!"".equals(ext_view)) {
									ext_view = ext_view.replaceAll("`","'") ;
									ext_view = ext_view.replaceAll("，",",") ;
									propertyHtml += "<img id='"+subFormTableName+"_viewImg' style='cursor:hand;' alt='点击查看详细信息' onclick=\""+ext_view+"\" src='" + contextPath + "/img/view.png' >" ;
								}
							}
							
							propertyHtml += "</td>\n";

							int cellCount = 0;
							for (int ii = 0; ii < propertys.length; ii++) {
								if (propertys[ii].indexOf("ext_column=") > -1) {
									String sf_propertys = StringUtil.replaceStr(propertys[ii], "ext_column={", "");
									sf_propertys = StringUtil.replaceStr(sf_propertys, "}", "");

									String[] lpropertys = sf_propertys.split(",");
									
									String inputHtml = inputProperty(formId, lpropertys,uuid, subFieldList, map, subFormTableName + "_", "_" + cellCount, "_" + i, subfieldTypeMap,dataMap,request);
									
									if(sf_propertys.indexOf("ext_type=hidden") > -1) {
										propertyHtml += inputHtml ;
									}else {
										propertyHtml += "<td>" + inputHtml + "</td>\n";
										cellCount++;
									}
									
								}
							}
							
							propertyHtml += "</tr>\n";
						}

						propertyHtml += "</tbody>";
						
						propertyHtml += colHtml ;

						// 合计列

						//propertyHtml += "</table></td></tr>";
						propertyHtml += "</table>";

					} else {
						Map fieldTypeMap = db.getFieldType(tableName) ;
						propertyHtml = inputProperty(formId, propertys, uuid, fieldList, dataMap, "", "", "", fieldTypeMap,null,request);
					}

					html = propertyHtmlStart + propertyHtml + propertyHtmlEnd;
				} else {
					break;
				}
			}

			return html;

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	/**
	 * 输入框属性
	 * 
	 * @param propertys 输入框属性
	 * @param uuid 主表单数据表的uuid
	 * @param fieldList s_formfield的字段属性
	 * @param dataMap 数据表的记录
	 * @param prefix 输入框的id前缀
	 * @return input的html的代码
	 * @throws Exception
	 */
//	public String inputProperty(String formId, String[] propertys, String uuid, List fieldList,
//			Map dataMap, String prefix, String cellIndex, String rowIndex) throws Exception {
//
//		String extType = "";
//		try {
//			// 普通文本输入框、密码输入框、单选、多选、隐藏
//			String inputHtml = "<input property=\"" + prefix + "\" ";
//			for (int i = 0; i < propertys.length; i++) {
//				
//				String inputProperty = propertys[i];
//				//不要自己设定宽度
//				//if(inputProperty.contains("ext_width"))continue;
//				inputProperty = inputProperty.trim();
//				inputProperty= inputProperty.replaceAll("\r", "");
//				inputProperty= inputProperty.replaceAll("\n", "");
//				inputProperty= inputProperty.replaceAll("\t", "");
//				
//				inputHtml += " " + inputProperty + " ";
//				
//				//名称
//				if (inputProperty.indexOf("ext_name=") > -1) {
//					// input的name
//					
//					for (int j = 0; j < fieldList.size(); j++) { 
//						String ext = StringUtil.replaceStr(inputProperty, "ext_name=", "");
//						Map map = (Map) fieldList.get(j);
//
//						String name = StringUtil.showNull(String.valueOf(map.get("name")));
//						String enName = StringUtil.showNull(String.valueOf(map.get("enname"))).toLowerCase();
//
//						if (name.equals(ext)) {
//							
//							inputHtml += " name=\"" + prefix + enName + "\" ";
//							
//							if (!"".equals(uuid)) {
//								if (dataMap != null) {
//									String value = StringUtil.showNull(String.valueOf(dataMap.get(enName)));
//									inputHtml += " value = \"" + value + "\" ";
//								}
//							}
//						}
//					}
//				} else if(inputProperty.indexOf("ext_id=") > -1) {
//					
//					String inputId = StringUtil.replaceStr(inputProperty, "ext_id=", "");
//					inputId = prefix + inputId + rowIndex;
//					inputHtml +=  " id=\"" + inputId + "\" ";
//					
//				} else if (inputProperty.indexOf("ext_type=") > -1) { //文本框类型
//					
//					if (inputProperty.indexOf("singleSelect") > -1) {
//						// 类型:单选
//						inputHtml += " type=\"text\" valuemustexist=true ";
//						
//					} else if (inputProperty.indexOf("multiSelect") > -1
//							   && inputProperty.indexOf("multiSelectAndLevel") == -1
//							   && inputProperty.indexOf("multiSelectGrid") == -1) {
//						// 类型:多选
//						inputHtml += " type=\"text\" valuemustexist=true multiselect=true ";
//						
//					} else if (inputProperty.indexOf("hidden") > -1){
//						// 类型:隐藏域
//						inputHtml += " type=\"hidden\" ";
//						
//					} else if (inputProperty.indexOf("password") > -1) {
//						//密码框
//						inputHtml += " type=\"password\" ";
//					} else if (inputProperty.indexOf("multilevel") > -1) {
//						//多级
//						inputHtml += " type=\"text\" multilevel=true ";
//					} else if (inputProperty.indexOf("multiSelectAndLevel") > -1) {
//						//多级多选
//						inputHtml += " type=\"text\" multilevel=true multiselect=true ";
//					} else if (inputProperty.indexOf("grid") > -1) {
//						//下拉表格
//						inputHtml += " type=\"text\" grid=true ";
//					} else if (inputProperty.indexOf("multiSelectGrid") > -1) {
//						//下拉多选表格
//						inputHtml += " type=\"text\" grid=true multiselect=true";
//					} else if(inputProperty.indexOf("attachFile") > -1){
//						//附件
//						inputHtml += " type=\"hidden\" attachFile=true indexTable=\"" + formId + "\" ";
//					} else {
//						//其他：文本框
//						inputHtml += " type=\"text\" ";
//					}
//					
//					extType = inputProperty.replaceAll("ext_type=", "");
//				}else if (inputProperty.indexOf("ext_validate=") > -1) { //验证
//					// 验证
//					inputProperty = StringUtil.replaceStr(inputProperty, "ext_validate=", "");
//					inputProperty = StringUtil.replaceStr(inputProperty, ",", " ");
//					
//					inputHtml += " class=\"" + inputProperty + "\" ";
//				}else if (inputProperty.indexOf("ext_readonly=") > -1) { //只读
//					// 只读
//					inputHtml += " " + StringUtil.replaceStr(inputProperty, "ext_", "") + " ";
//				}else if (inputProperty.indexOf("ext_default=") > -1) {//默认值
//					// 如果是新增，就填充初始值
//					if ("".equals(uuid)) {
//						inputHtml += " value=\"" + StringUtil.replaceStr(inputProperty, "ext_default=", "") + "\" ";
//					}
//				}else if (inputProperty.indexOf("ext_select=") > -1) {//下拉ID
//					// 候选值:ext_select=700|客户
//					inputProperty = StringUtil.replaceStr(inputProperty, "ext_select=", "");
//					String[] selects = inputProperty.split("\\|");
//
//					inputHtml += " autoid=\"" + selects[0] + "\" ";
//					
//					
//					for (int j = 1; j < selects.length; j++) {
//						String refer = selects[j];
//						
//						if(!"".equals(refer) && refer.indexOf("_$rowIndex") > -1) {
//							if(!"".equals(rowIndex)) {
//								refer = refer.replaceAll("_\\$rowIndex", rowIndex);
//							}
//							
//							refer = prefix + refer;
//						}
//						
//						inputHtml += " refer" + (j - 1 == 0 ? "" : j - 1) + "=\"" + refer + "\" ";
//					}
//				}else if (inputProperty.indexOf("ext_autoCalc=") > -1) {
//					// 自动计算:ext_autoCalc=单价*数量|总价
//					inputProperty = StringUtil.replaceStr(inputProperty, "ext_autoCalc=", "");
//					inputProperty = StringUtil.replaceStr(inputProperty, "＋","+");
//					inputProperty = StringUtil.replaceStr(inputProperty, "－","-");
//					inputProperty = StringUtil.replaceStr(inputProperty, "＊","*");
//					inputProperty = StringUtil.replaceStr(inputProperty, "／","/");
//					inputProperty = StringUtil.replaceStr(inputProperty, "（","(");
//					inputProperty = StringUtil.replaceStr(inputProperty, "）",")");
//
//					String[] calc = inputProperty.split("\\|");
//
//					inputHtml += " onfocus=\"goAutoCalc('" + calc[0] + "','" + calc[1] + "');\" ";
//
//					// String calc = propertys[ii].substring(0,
//					// propertys[ii].indexOf("|"));
//					// calc = CHF.replaceStr(calc,"+"," ");
//					// calc = CHF.replaceStr(calc,"-"," ");
//					// calc = CHF.replaceStr(calc,"*"," ");
//					// calc = CHF.replaceStr(calc,"/"," ");
//					// calc = CHF.replaceStr(calc,"("," ");
//					// calc = CHF.replaceStr(calc,")"," ");
//					//
//					// String [] cals = calc.split(" ");
//					// for (int i = 0; i < cals.length; i++) {
//					// if(!"".equals(CHF.showNull(cals[i]))){
//					// html1 =
//					// CHF.replaceStr(html1,"id=\""+cals[i]+"\""," id=\""+cals[i]+"\" onblur=\"goAutoCalc('"+propertys[ii]+"');\" ");
//					// html3 =
//					// CHF.replaceStr(html3,"id=\""+cals[i]+"\""," id=\""+cals[i]+"\" onblur=\"goAutoCalc('"+propertys[ii]+"');\" ");
//					// }
//					// }
//				}else {//其它
//					inputHtml += " " + StringUtil.replaceStr(inputProperty, "ext_", "") + " ";
//				}
//				
//
//			}
//			
//			inputHtml += " />";
////			
////			if("date".equalsIgnoreCase(extType)) {
////				inputHtml += getDateExtJSScript(inputId, dateFormat);
////			}
//
//			return inputHtml;
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw e;
//		}
//	}

	public String inputProperty(String formId, String[] propertys, String uuid, List fieldList,
			Map dataMap, String prefix, String cellIndex, String rowIndex,Map fieldTypeMap,Map formMap,HttpServletRequest request) throws Exception {

		String extType = "";
		String inputValue = "" ;
		String[] selectArr = null ;
		
		String view = StringUtil.showNull(request.getParameter("view")) ;
		try {
			// 普通文本输入框、密码输入框、单选、多选、隐藏
			String inputHtml = "<input property=\"" + prefix + "\" ";
			
			boolean hasClass = false ;
			for (int i = 0; i < propertys.length; i++) {
				
				String inputProperty = propertys[i];
				inputProperty = inputProperty.trim();
				inputProperty= inputProperty.replaceAll("\r", "");
				inputProperty= inputProperty.replaceAll("\n", "");
				inputProperty= inputProperty.replaceAll("\t", "");
				
				inputHtml += " " + inputProperty + " ";
				
				//名称
				if (inputProperty.indexOf("ext_name=") > -1) {
					// input的name
					for (int j = 0; j < fieldList.size(); j++) { 
						String ext = StringUtil.replaceStr(inputProperty, "ext_name=", "");
						Map map = (Map) fieldList.get(j);

						String name = StringUtil.showNull(String.valueOf(map.get("name")));
						String enName = StringUtil.showNull(String.valueOf(map.get("enname"))).toLowerCase();

						if (name.equals(ext)) {
							
							inputHtml += " name=\"" + prefix + enName + "\" ";
							
							if (!"".equals(uuid)) {
								if (dataMap != null) {
									String value = StringUtil.showNull(String.valueOf(dataMap.get(enName)));
									
									int fieldType = -1 ;
									if(fieldTypeMap.get(enName) != null) {
										fieldType = ((Integer)fieldTypeMap.get(enName)).intValue() ;
									}
									if(fieldType == Types.NUMERIC || fieldType == Types.DOUBLE || fieldType == Types.DECIMAL) {
										if(value.indexOf(".") > -1) {
											NumberFormat formater = new DecimalFormat("###,##0.00");
											if("".equals(value)) value = "0" ;
											double num = Double.parseDouble(value);
											value = formater.format(num);
										}  
									}
									inputValue = value ;
									
									
									
								}
							}
						}
					}
				} else if(inputProperty.indexOf("ext_id=") > -1) {
					
					String inputId = StringUtil.replaceStr(inputProperty, "ext_id=", "");
					inputId = prefix + inputId + rowIndex;
					inputHtml +=  " id=\"" + inputId + "\" ";
					
				} else if (inputProperty.indexOf("ext_type=") > -1) { //文本框类型
					
					if (inputProperty.indexOf("singleSelect") > -1) {
						// 类型:单选
						inputHtml += " type=\"text\" valuemustexist=true ";
						
					} else if (inputProperty.indexOf("multiSelect") > -1
							   && inputProperty.indexOf("multiSelectAndLevel") == -1
							   && inputProperty.indexOf("multiSelectGrid") == -1) {
						// 类型:多选
						inputHtml += " type=\"text\" valuemustexist=true multiselect=true ";
						
					} else if (inputProperty.indexOf("hidden") > -1){
						// 类型:隐藏域
						inputHtml += " type=\"hidden\" ";
						
					} else if (inputProperty.indexOf("password") > -1) {
						//密码框
						inputHtml += " type=\"password\" ";
					} else if (inputProperty.indexOf("multilevel") > -1) {
						//多级
						inputHtml += " type=\"text\" multilevel=true ";
					} else if (inputProperty.indexOf("multiSelectAndLevel") > -1) {
						//多级多选
						inputHtml += " type=\"text\" multilevel=true multiselect=true ";
					} else if (inputProperty.indexOf("grid") > -1) {
						//下拉表格
						inputHtml += " type=\"text\" grid=true ";
					} else if (inputProperty.indexOf("multiSelectGrid") > -1) {
						//下拉多选表格
						inputHtml += " type=\"text\" grid=true multiselect=true";
					} else if(inputProperty.indexOf("attachFile") > -1){
						//附件
						inputHtml += " type=\"hidden\" attachFile=true indexTable=\"" + formId + "\" ";
					} else {
						//其他：文本框
						inputHtml += " type=\"text\" ";
					}
					
					extType = inputProperty.replaceAll("ext_type=", "");
				}else if (inputProperty.indexOf("ext_validate=") > -1) { //验证
					// 验证
					inputProperty = StringUtil.replaceStr(inputProperty, "ext_validate=", "");
					inputProperty = StringUtil.replaceStr(inputProperty, ",", " ");
					inputProperty = StringUtil.replaceStr(inputProperty, "`", " ");
					
					
					if("true".equals(view)) {
						inputHtml += " class=\"" + inputProperty +" readonly\" ";
					}else {
						inputHtml += " class=\"" + inputProperty + "\" ";
					}
					hasClass = true ;
				}else if (inputProperty.indexOf("ext_readonly=") > -1) { //只读
					// 只读
					inputHtml += " " + StringUtil.replaceStr(inputProperty, "ext_", "") + " ";
				}else if (inputProperty.indexOf("ext_default=") > -1) {//默认值
					// 如果是新增，就填充初始值
					if ("".equals(uuid)) {
						inputHtml += " value=\"" + StringUtil.replaceStr(inputProperty, "ext_default=", "") + "\" ";
					}
				}else if (inputProperty.indexOf("ext_select=") > -1) {//下拉ID
					// 候选值:ext_select=700|客户
					inputProperty = StringUtil.replaceStr(inputProperty, "ext_select=", "");
					String[] selects = inputProperty.split("\\|");
					
					if(!"true".equals(view)) {
						inputHtml += " autoid=\"" + selects[0] + "\" ";
					}
					
					selectArr = selects ;
					
					for (int j = 1; j < selects.length; j++) {
						String refer = selects[j];
						
						if(!"".equals(refer) && refer.indexOf("_$rowIndex") > -1) {
							if(!"".equals(rowIndex)) {
								refer = refer.replaceAll("_\\$rowIndex", rowIndex);
							}
							
							refer = prefix + refer;
						}
						
						inputHtml += " refer" + (j - 1 == 0 ? "" : j - 1) + "=\"" + refer + "\" ";
					}
				}else if (inputProperty.indexOf("ext_autoCalc=") > -1) {
					// 自动计算:ext_autoCalc=单价*数量|总价
					inputProperty = StringUtil.replaceStr(inputProperty, "ext_autoCalc=", "");
					inputProperty = StringUtil.replaceStr(inputProperty, "＋","+");
					inputProperty = StringUtil.replaceStr(inputProperty, "－","-");
					inputProperty = StringUtil.replaceStr(inputProperty, "＊","*");
					inputProperty = StringUtil.replaceStr(inputProperty, "／","/");
					inputProperty = StringUtil.replaceStr(inputProperty, "（","(");
					inputProperty = StringUtil.replaceStr(inputProperty, "）",")");

					String[] calc = inputProperty.split("\\|");

					inputHtml += " onfocus=\"goAutoCalc('" + calc[0] + "','" + calc[1] + "');\" ";

					// String calc = propertys[ii].substring(0,
					// propertys[ii].indexOf("|"));
					// calc = CHF.replaceStr(calc,"+"," ");
					// calc = CHF.replaceStr(calc,"-"," ");
					// calc = CHF.replaceStr(calc,"*"," ");
					// calc = CHF.replaceStr(calc,"/"," ");
					// calc = CHF.replaceStr(calc,"("," ");
					// calc = CHF.replaceStr(calc,")"," ");
					//
					// String [] cals = calc.split(" ");
					// for (int i = 0; i < cals.length; i++) {
					// if(!"".equals(CHF.showNull(cals[i]))){
					// html1 =
					// CHF.replaceStr(html1,"id=\""+cals[i]+"\""," id=\""+cals[i]+"\" onblur=\"goAutoCalc('"+propertys[ii]+"');\" ");
					// html3 =
					// CHF.replaceStr(html3,"id=\""+cals[i]+"\""," id=\""+cals[i]+"\" onblur=\"goAutoCalc('"+propertys[ii]+"');\" ");
					// }
					// }
				}else {//其它
					inputHtml += " " + StringUtil.replaceStr(inputProperty, "ext_", "") + " ";
				}
				

			}
			
			if(selectArr!=null && !"".equals(selectArr[0]) && !"class".equals(selectArr[0])) {
				String rawValue = getSelectRawValue(selectArr[0],inputValue,dataMap,fieldList,selectArr,
						rowIndex,prefix,formMap,formId,request) ;
				if(!"".equals(rawValue)){
					inputHtml += " rawValue=\"" + rawValue + "\" ";
				}
				
				if("true".equals(view)) {
					inputHtml += " value = \"" + rawValue + "\" ";
				}else {
					inputHtml += " value = \"" + inputValue + "\" ";
				}
			}else {
				inputHtml += " value = \"" + inputValue + "\" ";
			}
			
			
			if("true".equals(view)) {
				inputHtml += " readonly=readonly style=\"background-image: none;\"";
				
				if(!hasClass){
					inputHtml += " class=\"readonly\" ";
				}
			}
			
			inputHtml += " />";
//			
//			if("date".equalsIgnoreCase(extType)) {
//				inputHtml += getDateExtJSScript(inputId, dateFormat);
//			}

			return inputHtml;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	/**
	 * 多选下拉grid
	 * @param ext_selectList
	 * @return
	 */
//	private String getSelectListHTML(String ext_selectList, String subTableName, int colCount, String gridSelectListValue) {
//		String[] propertys = ext_selectList.split(",");
//		String property = "";
//		String value = "";
//		
//		String html = "<input type=\"text\" size=\"12\" name=\"gridSelectList_" + subTableName + "\" id=\"gridSeletList_" + subTableName + "\" property=\"" + subTableName + "`" + colCount + "\" ";
//		for (int i = 0; i < propertys.length; i++) {
//			String[] temp = propertys[i].split("=");
//			property = temp[0];
//			value = temp[1];
//			
//			if(property.indexOf("ext_select") > -1) {
//				String[] selects = value.split("\\|");
//				
//				html += " autoid=\"" + selects[0] + "\" ";
//				
//				for (int j = 1; j < selects.length; j++) {
//					html += " refer" + (j - 1 == 0 ? "" : j - 1) + "=\"" + selects[j] + "\" ";
//				}
//				
//			} else {
//				property = property.replaceAll("ext_","") ;
//				html += " " + property + "=\"" + value + "\" ";
//			}
//		}
//		
//		html += " value=\"" + gridSelectListValue + "\" />";
//		return html;
//	}
//	
	/**
	 * 多选下拉grid
	 * @param ext_selectList
	 * @return
	 */
	private String getSelectListHTML(String ext_selectList, String subTableName, int colCount, String gridSelectListValue,String viewEvent) {
		String[] propertys = ext_selectList.split(",");
		String property = "";
		String value = "";
		
		String html = "<input type=\"text\" size=\"12\" name=\"gridSelectList_" + subTableName + "\" id=\"gridSeletList_" + subTableName + "\" property=\"" + subTableName + "~`" + colCount + "~`"+viewEvent+"\" ";
		for (int i = 0; i < propertys.length; i++) {
			String[] temp = propertys[i].split("=");
			property = temp[0];
			value = temp[1];
			
			if(property.indexOf("ext_select") > -1) {
				String[] selects = value.split("\\|");
				
				html += " autoid=\"" + selects[0] + "\" ";
				
				for (int j = 1; j < selects.length; j++) {
					html += " refer" + (j - 1 == 0 ? "" : j - 1) + "=\"" + selects[j] + "\" ";
				}
				
			} else {
				property = property.replaceAll("ext_","") ;
				html += " " + property + "=\"" + value + "\" ";
			}
		}
		
		html += " value=\"" + gridSelectListValue + "\" />";
		return html;
	}
	/**
	 * 比较行、列是否出现合并列
	 * 
	 * @param col
	 * @param fields
	 * @param iRow
	 * @param iCol
	 * @param width
	 * @param prefix
	 * @return
	 * @throws Exception
	 */
//	public String mergerLine(String col, String[][] fields, int iRow, int iCol,
//			String width, String prefix, String ext_selectList, String gridSelectListValue) throws Exception {
//		try {
//			if ("".equals(col))
//				return ""; // 已经处理过了
//
//			int rowspan = 1, colspan = 0;
//			// 1、先一行中的列比较
//			for (int i = iCol; i < fields[iRow].length; i++) {
//				if (col.equals(fields[iRow][i])) {
//					colspan++;
//					if (i != iCol) {
//						fields[iRow][i] = "";
//					}
//				} else {
//					break;
//				}
//			}
//			if (colspan == 0)
//				colspan = 1;
//
//			// 2、再行比较
//			for (int i = (iRow + 1); i < fields.length; i++) {
//				boolean bool = false;
//				for (int j = iCol; j < iCol + colspan; j++) {
//					if (col.equals(fields[i][j])) {
//						bool = true;
//					} else {
//						bool = false;
//						break;
//					}
//				}
//				if (bool) {
//					rowspan++;
//					for (int j = iCol; j < iCol + colspan; j++) {
//						if (i != iRow) {
//							fields[i][j] = ""; // 已经处理过的表头都赋值为空
//						}
//					}
//				} else {
//					break;
//				}
//
//			}
//			if (rowspan == 0)
//				rowspan = 1;
//
//			// 返回td
//			String td = "";
//			if ("+".equals(col)) {
//				// 操作列
//				td = "<th rowspan="
//						+ rowspan
//						+ " colspan="
//						+ colspan
//						+ " align='center' style='width: "
//						+ width
//						+ "'>";
//						
//				if(ext_selectList != null && !"".equals(ext_selectList)) {
//					td += getSelectListHTML(ext_selectList, prefix, fields[iRow].length, gridSelectListValue);
//				} else {
//					td += "<img style='cursor:hand;' alt='新增一行' onclick=\"mt_add('"
//							+ prefix + "','" + fields[iRow].length + "')\" src='"
//							+ contextPath + "/img/add.gif' flag="+prefix+"_del >";
//				}
//						
//				td += "</th>";
//				
//				
//				
//				
//			} else {
//				// 其它列
//				td = "<th rowspan=" + rowspan + " colspan=" + colspan
//						+ "  align='center' style='width: " + width + "'>"
//						+ col + "</th>";
//			}
//			return td;
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw e;
//		}
//	}

	public String mergerLine(String col, String[][] fields, int iRow, int iCol,
			String width, String prefix, String ext_selectList, String gridSelectListValue,
			String viewEvent,String view) throws Exception {
		try {
			if ("".equals(col))
				return ""; // 已经处理过了

			int rowspan = 1, colspan = 0;
			// 1、先一行中的列比较
			for (int i = iCol; i < fields[iRow].length; i++) {
				if (col.equals(fields[iRow][i])) {
					colspan++;
					if (i != iCol) {
						fields[iRow][i] = "";
					}
				} else {
					break;
				}
			}
			if (colspan == 0)
				colspan = 1;

			// 2、再行比较
			for (int i = (iRow + 1); i < fields.length; i++) {
				boolean bool = false;
				for (int j = iCol; j < iCol + colspan; j++) {
					if (col.equals(fields[i][j])) {
						bool = true;
					} else {
						bool = false;
						break;
					}
				}
				if (bool) {
					rowspan++;
					for (int j = iCol; j < iCol + colspan; j++) {
						if (i != iRow) {
							fields[i][j] = ""; // 已经处理过的表头都赋值为空
						}
					}
				} else {
					break;
				}

			}
			if (rowspan == 0)
				rowspan = 1;

			// 返回td
			String td = "";
			if ("+".equals(col)) {
				// 操作列
				td = "<th rowspan="
						+ rowspan
						+ " colspan="
						+ colspan
						+ " align='center' style='width: "
						+ width
						+ "'>";
						
				if(ext_selectList != null && !"".equals(ext_selectList)) {
					if(!"true".equals(view)) {
						td += getSelectListHTML(ext_selectList, prefix, fields[iRow].length, gridSelectListValue, viewEvent);
					}
				} else {
					
					if(!"true".equals(view)) {
						td += "<img style='cursor:hand;' alt='新增一行' onclick=\"mt_add('"
								+ prefix + "','" + fields[iRow].length + "','" + viewEvent + "')\" src='"
								+ contextPath + "/img/add.gif' flag="+prefix+"_del >";
					}
					
				}
						
				td += "</th>";
				
				
			} else {
				// 其它列
				td = "<th rowspan=" + rowspan + " colspan=" + colspan
						+ "  align='center' style='width: " + width + "'>"
						+ col + "</th>";
			}
			return td;

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	/**
	 * 判断表是否存在。 存在返回true，不存在返回false
	 * 
	 * @param tableName
	 *            表名
	 * @return
	 */
	public boolean checkTable(String tableName) throws Exception {
		boolean flg = true;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			String sql = "select * from  " + tableName + " where 1=2";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
		} catch (Exception e) {
			flg = false;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return flg;
	}

	/**
	 * 创建表的字符串
	 * 
	 * @param tableName
	 *            要创建表的名称
	 * @return 创建表的字符串
	 */
	public boolean createTable(String tableName) throws Exception {
		String sql = " create table " + tableName + "( "
				+ "uuid varchar2(36) primary key not null,"
				+ "mainformid varchar2(50) ," + "subformid varchar2(50) ,"
				+ "udate varchar2(50) ," + "userid varchar2(50) ,"
				+ "departmentid varchar2(50),";
		for (int i = 1; i <= 99; i++) {
			sql += "v" + i + " varchar2(200),";
		}

		sql += "v100 varchar2(200))";
		
		
		return new DbUtil(conn).execute(sql);
	}

	/**
	 * 添加记录
	 * 
	 * @param form
	 * @throws Exception
	 */
	public void saveFormDefine(FormDefine form) throws Exception {

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			String sql = " INSERT INTO MT_COM_FORM( "
						+ " 	uuid, name, enname, tablename, tabletype, "
						+ " 	definestr, extclass, udate, uname, selecttype, "
						+ " 	property,FORM_TYPE,thead) "
						+ " VALUES(?,?,?,?,?, ?,?,?,?,?, ?,?,?) ";

			conn.setAutoCommit(false);
			ps = conn.prepareStatement(sql);
			ps.setString(1, form.getUuid());
			ps.setString(2, form.getName());
			ps.setString(3, form.getEnname());
			ps.setString(4, StringUtil.showNull(form.getTableName()).toUpperCase());
			ps.setString(5, form.getTableType());
			
			ps.setString(6, form.getDefinestr()) ;
			ps.setString(7, form.getExtclass());
			ps.setString(8, form.getUdate());
			ps.setString(9, form.getUname());
			ps.setString(10, form.getSelecttype());
			
			ps.setString(11, form.getProperty());
			ps.setString(12, form.getFormType());
			ps.setString(13, form.getThead());
			ps.executeUpdate();
			DbUtil.close(ps);

			conn.commit();
			conn.setAutoCommit(true);

			//如果是自动建表且表不存在，则创建表
			if("0".equals(form.getTableType()) && !this.checkTable(form.getTableName())) {
				this.createTable(form.getTableName());
			}
			
			// 记录存在的话，更新FormField表
			saveFormField(form.getUuid(), form.getDefinestr(), true);
			
			FormQueryConfigService updateFormService = new FormQueryConfigService(conn);
			
			//插入listSQL
			updateFormService.updateListSql(form.getUuid(),getShowSql(form.getUuid()));
			
			updateFormService.initFormListButton(form.getUuid());

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
		}
	}

	/**
	 * 根据uuid查询一条记录
	 * 
	 * @param uuid
	 * @return
	 * @throws Exception
	 */
	public FormDefine getFormDefine(String uuid) throws Exception {
		FormDefine form = new FormDefine();

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			String sql = " SELECT uuid, name, enname, definestr, extclass, "
					+ " udate, uname, selecttype, property, tablename, "
					+ " tabletype,FORM_TYPE,listSql,listHtml,thead "
					+ " FROM MT_COM_FORM  where uuid=? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, uuid);
			rs = ps.executeQuery();
			
			if(rs.next()) {
				form.setUuid(rs.getString(1));
				form.setName(rs.getString(2));
				form.setEnname(rs.getString(3));
				form.setDefinestr(rs.getString(4));
				form.setExtclass(rs.getString(5));
				form.setUdate(rs.getString(6));
				form.setUname(rs.getString(7));
				form.setSelecttype(rs.getString(8));
				form.setProperty(rs.getString(9));
				form.setTableName(rs.getString(10));
				form.setTableType(rs.getString(11));
				form.setFormType(rs.getString(12));
				form.setListSql(rs.getString(13));
				form.setListHtml(rs.getString(14));
				form.setThead(rs.getString(15));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return form;
	}
	
	/**
	 * 备份
	 * @param formId
	 */
	public void backupFormDefine(String formId) {
		
		PreparedStatement ps = null;

		try {
			//备份
			String sql = " insert into MT_BAK_MT_COM_FORM "
						+ " select * "
						+ " from ( "
						+ " 	select A.*, UUID() as BAK_ID "
						+ " 	from MT_COM_FORM A "
						+ " 	where A.UUID = ? "
						+ " ) a ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, formId);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}

	/**
	 * 保存修改
	 * 
	 * @param uuid
	 * @param oldename
	 * @param formDefine
	 * @throws Exception
	 */
	public void updateFormDefine(String oldename, FormDefine formDefine) throws Exception {
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;

		try {
			
			backupFormDefine(formDefine.getUuid());
			
			String sql = " UPDATE MT_COM_FORM SET "
						+ " NAME=?, ENNAME=?,TABLENAME=?,DEFINESTR=?,EXTCLASS=?, "
						+ " UDATE=?,UNAME=?,SELECTTYPE=?,PROPERTY=?,TABLETYPE=?, "
						+ " FORM_TYPE=?,thead=? "
						+ " WHERE UUID=? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, formDefine.getName());
			ps.setString(2, formDefine.getEnname());
			ps.setString(3, StringUtil.showNull(formDefine.getTableName()).toUpperCase());
			ps.setString(4, formDefine.getDefinestr());
			ps.setString(5, formDefine.getExtclass());
			ps.setString(6, formDefine.getUdate());
			ps.setString(7, formDefine.getUname());
			ps.setString(8, formDefine.getSelecttype());
			ps.setString(9, formDefine.getProperty());
			ps.setString(10, formDefine.getTableType());
			ps.setString(11, formDefine.getFormType());
			ps.setString(12,formDefine.getThead());
			ps.setString(13, formDefine.getUuid());
			ps.executeUpdate();
			
			String newTableName = formDefine.getTableName();
			
			//如果是自动建表，则自动改变名称
			if("0".equals(formDefine.getTableType())) {
				
				// 现有表存在,而新表不存在时调用
				if (!this.checkTable(newTableName) && this.checkTable(oldename)) {
					String sql2 = " RENAME " + oldename + " TO " + newTableName;
					ps2 = conn.prepareStatement(sql2);
					ps2.execute(sql2);
				}
			}
			
			saveFormField(formDefine.getUuid(), formDefine.getDefinestr(), false);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
			DbUtil.close(ps2);
		}
	}

	/**
	 * 根据uuid删除form表单
	 * 
	 * @param uuid
	 * @throws Exception
	 */
	public void removeFormDefine(String uuid) throws Exception {

		PreparedStatement ps = null;

		try {
			
			backupFormDefine(uuid);
			
			String sql = " DELETE FROM MT_COM_FORM WHERE uuid=? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, uuid);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 根据uuid删除form表单的数据,删除与该表单有联系的所有子表单的数据
	 * 
	 * @param uuid
	 * @throws Exception
	 */
	public void removeFormData(String formId, String uuid) throws Exception {

		FormDefine formDefine = new FormDefineService(conn).getFormDefine(formId);
		
		FormExtService formExtService = new FormExtService(formDefine.getExtclass());
		
		formExtService.beforeDelete(conn,formId, uuid, null,null);
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<String> nameList = new ArrayList<String>();
		String mainName ="";
		
		try {
			
			String sql = "select tablename from MT_COM_FORM where parentformid = '"+formId+"'";  //查询当前表单下的所有子表单数据表名
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				nameList.add(rs.getString("tablename"));
			}
			DbUtil.close(ps);
			DbUtil.close(rs);
			
			sql = "select tablename from MT_COM_FORM where uuid = '"+formId+"'";   //查询当前主表单数据表名
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				mainName = rs.getString("tablename");
			}
			DbUtil.close(ps);
			DbUtil.close(rs);
			
			//支持多选
			String uuids = uuid.replaceAll(",", "','");
			
			if (null!=nameList&&nameList.size()>0) {
				for (int i = 0; i < nameList.size(); i++) {
					sql = "delete from "+nameList.get(i)+" where mainformid in('" + uuids + "')";    //删除子表单数据
					ps = conn.prepareStatement(sql);
					ps.execute();
					DbUtil.close(ps);
				}
			}
			if (null!= mainName&&!"".equals(mainName)) {
				sql = " DELETE FROM "+mainName+" WHERE uuid in ('" + uuids + "')";
				ps = conn.prepareStatement(sql);
				ps.executeUpdate();
				DbUtil.close(ps);
			}
			
			formExtService.afterDelete(conn, formId, uuid, null,null);
			
			//增加日志
			if(formDefine != null) {
				String formName = formDefine.getName() ;
				String logInfo = "表单【"+formName+"】的数据【"+uuid+"】被删除了" ;
				log.log(logInfo) ;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
			DbUtil.close(rs);
		}
	}
	
	/**
	 * 根据uuid取得构建的sql语句
	 * 
	 * @param uuid
	 * @return
	 * @throws Exception
	 */
	public String getShowSql(String uuid) throws Exception {
		String result = "";
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			String sql = " SELECT tablename FROM MT_COM_FORM where uuid=? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, uuid);
			rs = ps.executeQuery();
			if(rs.next()) {
				String tableName = rs.getString(1);
				FormQueryConfigService updateFormService = new FormQueryConfigService(conn);
				List<FormQuery> list = updateFormService.getformQuery(uuid);
				String showField = " ";
				for (FormQuery bean : list) {
					showField += bean.getEnname() + ",";
				}
				int count1 = showField.lastIndexOf(",");
				if (count1 > 0) {
					showField = showField.substring(0, count1);
				}
				result += "select uuid," + showField + " from " + tableName;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return result;
	}
	
	/**
	 * 根据uuid取得form表自定义的sql语句
	 * 
	 * @param uuid
	 * @return
	 * @throws Exception
	 */
	public String getListSql(String uuid) throws Exception {
		String result = "";
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			String sql = " SELECT listsql FROM MT_COM_FORM where uuid=? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, uuid);
			rs = ps.executeQuery();
			
			if(rs.next()) {
				result = rs.getString(1);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return result;
	}

	/**
	 * 
	 * 根据uuid取得要显示的逻辑
	 * 
	 * @param formId
	 * @return
	 * @throws Exception
	 */
	public List<FormQuery> getFormField(String formId) throws Exception {
		List<FormQuery> list = new ArrayList<FormQuery>();
		FormQuery bean = null;

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			String sql = "select uuid,name,enname,formid,orderid,property " +
					"from MT_COM_FORM_QUERY  where formid = ? ORDER BY orderid ASC ";

			ps = conn.prepareStatement(sql);
			ps.setString(1, formId);
			rs = ps.executeQuery();

			while (rs.next()) {

				bean = new FormQuery();
				bean.setUuid(rs.getString(1));
				bean.setName(rs.getString(2));
				bean.setEnname(rs.getString(3));
				bean.setFormid(rs.getString(4));
				bean.setOrderid(rs.getInt(5));
				bean.setProperty(rs.getString(7));

				list.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return list;
	}
	
	/**
	 * 获取表单实例的HTML
	 * @param formId 表单模版ID
	 * @param uuid 数据UUID
	 * @return
	 * @throws Exception
	 */
	public String getFormDataHTML(HttpServletRequest request, String formId, String uuid) throws Exception {
		String html = "";
		try {
			DbUtil db = new DbUtil(conn);
			
			// 通过formid，得到表单的html
			Map form = db.get("MT_COM_FORM", "uuid", formId);
			  
			html = StringUtil.showNull(form.get("definestr"));
			html = html.replaceAll("&quot;","\"") ;
			html = StringUtil.transSessionValue(request.getSession(), html);
			html = StringUtil.transRequestValue(request, html);
			html = StringUtil.transSqlValue(request, html);
			if(uuid == null || "".equals(uuid)) {
				//替换系统变量
				html = StringUtil.transSystemValue(html);
				
				//替换session变量
				html = StringUtil.transAutoCodeValue(html);
			}
			String view = StringUtil.showNull(request.getParameter("view")) ;
			html = input(formId, uuid, html,request);
		} catch (Exception e) {
			throw e;
		}
		
		//无条件放2个隐藏域
		html = "<input type=\"hidden\" id=\"mt_formid\" name=\"mt_formid\" value=\"" + formId + "\"> \n"
			 + "<input type=\"hidden\" id=\"uuid\" name=\"uuid\"  value=\"" + uuid + "\"> \n" 
			 + html;
		return html;
	}
	
	/**
	 * 保存表单实例数据
	 * @param request
	 * @param formId 表单模版ID
	 * @param uuid	数据UUID,新增时传空
	 * @return
	 * @throws Exception
	 */
	public String saveFormData(HttpServletRequest request,HttpServletResponse response, String formId, String uuid) throws Exception {

		try {
			
			DbUtil db = new DbUtil(conn);
			
			UserSession userSession = (UserSession) request.getSession()
					.getAttribute("userSession");
			
			conn.setAutoCommit(false) ;
			FormDefine formDefine = new FormDefineService(conn).getFormDefine(formId);
			
			FormExtService formExtService = new FormExtService(formDefine.getExtclass());
			
			
			boolean isAdd = false;
			String result="";
			if(uuid == null || "".equals(uuid)){
				//新增
				result=formExtService.beforeAdd(conn, formId, request,response);
				isAdd = true;
			} else {
				//修改
				result=formExtService.beforeUpdate(conn,formId, uuid, request,response);
			}
			
			if(!(result==null||result.length()==0)){
				return result;
			}
			String tableName = formDefine.getTableName();
			
			//通过formid，求出所有子表单的数据表名
			List flist = db.getList("mt_com_form", "parentformid", formId);
			
			Map parameters = new HashMap();
			Enumeration e = request.getParameterNames();
			while (e.hasMoreElements()) {
				String paramName = String.valueOf(e.nextElement());
				String [] paramValue = request.getParameterValues(paramName);
				boolean bool = true;
				for (int i = 0; i < flist.size(); i++) {
					Map map = (Map)flist.get(i);
					if(paramName.indexOf(String.valueOf(map.get("tablename"))) >-1){
						parameters.put(paramName, paramValue);
						bool = false;
						break;
					}
				}
				if(bool){
					parameters.put(paramName, paramValue[0]);	
				}
			}
	
			parameters.put("mainformid", formId);
			
			if(uuid == null || "".equals(uuid)){
				parameters.put("oper_id", userSession.getUserId());
				parameters.put("oper_time", StringUtil.getCurrentDateTime());
				
			} else {
				parameters.put("update_user_id", userSession.getUserId());
				parameters.put("update_time", StringUtil.getCurrentDateTime());
			}
	
			if (isAdd) {
				
				// 新增
				uuid = UUID.randomUUID().toString();
				parameters.put("uuid", uuid);
	
				db.add(tableName, "", parameters);
				String sql=MessageFormat.format("update {0} set departmentid=? where uuid=?", formDefine.getTableName());
				try{
				db.executeUpdate(sql,new Object[]{userSession.getUserAuditDepartmentId(),uuid});
				sql=MessageFormat.format("update {0} set del_ind=? where uuid=?", formDefine.getTableName());
				db.executeUpdate(sql,new Object[]{0,uuid});
 
				}catch(Exception ex){
				  ex.printStackTrace();
				}
			} else {
				
				// 修改
				parameters.put("uuid", uuid);
				db.update(tableName, "uuid", parameters);
				
			}

			//新增、修改 子表单:删除子表单，重新插入
			for (int ii = 0; ii < flist.size(); ii++) {
				//循环子表单
				Map map = (Map)flist.get(ii);
				String table = StringUtil.showNull(String.valueOf(map.get("tablename"))).toUpperCase(); //子表单对应的数据表
				String subFormId = StringUtil.showNull(String.valueOf(map.get("uuid")));
				
				//删除子表单
				db.del(table, "mainformid", uuid);
				
				//新增子表单
				String mt_value = StringUtil.showNull(((String [])parameters.get("mt_value_" + table))[0]);
				
				String [] fieldNames = mt_value.split("`");
				Map [] maps = null;
				String [] values = null;
				
				//判断数据库是否有多余的字段
				int p = 0;
				while(true){
					values = (String [])parameters.get(table + "_" + fieldNames[p]);
					if(values != null){
						maps = new Map[values.length];
						for(int i = 0; i<values.length; i++){
							maps[i] = new HashMap();
						}
						break;
					}
					
					p++;
				}
				
				for (int jj = 0; jj < fieldNames.length; jj++) {
					values = (String [])parameters.get(table + "_" + fieldNames[jj]);
					if(values != null){
						
						for(int i = 0; i < values.length; i++){
							maps[i].put(fieldNames[jj], values[i]);
						}
					}
					
				}
				if(maps != null){
					for(int i = 0; i < maps.length; i++){
						maps[i].put("uuid", StringUtil.getUUID());
						maps[i].put("mainformid", uuid);
						maps[i].put("udate", StringUtil.getCurDateTime());
						maps[i].put("uid", userSession.getUserId());
						maps[i].put("departmentid", userSession.getUserAuditDepartmentId());
						maps[i].put("subformid", subFormId);
						
						db.add(table, "", maps[i]);
					}
				}
			}
			
			if(isAdd) {
				//新增
				formExtService.afterAdd(conn, formId, uuid, request,response);
				
				//增加日志
				if(formDefine != null) {
					String formName = formDefine.getName() ;
					String logInfo = "表单【"+formName+"】新增数据【"+uuid+"】" ;
					log.log(logInfo) ;
				}
			} else {
				//修改
				formExtService.afterUpdate(conn, formId, uuid, request,response);
				
				//增加日志
				if(formDefine != null) {
					String formName = formDefine.getName() ;
					String logInfo = "表单【"+formName+"】的数据【"+uuid+"】被修改了" ;
					log.log(logInfo) ;
				}
			}
			conn.commit() ;
			conn.setAutoCommit(true) ;
		} catch (Exception e) {
			conn.rollback() ;
			e.printStackTrace();
			throw e;
		}
		
		return uuid;
	}
	
	/**
	 * 获取DATAGRID编号
	 * @param formId
	 * @return
	 */
	public static String getDataGridId(String formId) {
		return FORM_LIST_GRID_ID_PRE + formId.replaceAll("-", "_");
	}
	
	
	public void beforeView(HttpServletRequest request,HttpServletResponse response, String formId, String uuid,ModelAndView modelAndView) throws Exception {

		try {
			
			DbUtil db = new DbUtil(conn);
			
			UserSession userSession = (UserSession) request.getSession()
					.getAttribute("userSession");
			
			conn.setAutoCommit(false) ;
			FormDefine formDefine = new FormDefineService(conn).getFormDefine(formId);
			
			FormExtService formExtService = new FormExtService(formDefine.getExtclass());
			formExtService.beforeView(conn, formId, uuid, request, response, modelAndView);
		}catch(Exception ex){
		   ex.printStackTrace(); 
		
		}
    }
	
	/** 得到子表单 table name
	 * @param parentformid
	 * @return
	 */
	public String getSubTableName(String parentformid) {
		String subTableName="";
		try {
			DbUtil dbUtil=new DbUtil(conn);
			List<Map<String, String>> subTableNameList=
				dbUtil.query("select group_concat(upper(t.enname))  as ennames from MT_COM_FORM t where t.parentformid=?", 
						Arrays.asList(parentformid), Arrays.asList("ennames"));
			if (subTableNameList.size()>0) {
				subTableName=subTableNameList.get(0).get("ennames");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return subTableName;
	}
	
	public String getSelectRawValue(String autoId,String value,
			Map dataMap,List fieldList,String[] selects,String rowIndex,
			String prefix,Map formMap,String formId,HttpServletRequest request){
		
		if("".equals(value)) return "" ; 
		
		String rawValue="";
		PreparedStatement ps = null ; 
		ResultSet rs = null ;
		try {
			DbUtil dbUtil=new DbUtil(conn);
			List mainFieldList = dbUtil.getList("MT_COM_FORM_FIELD", "formid", formId);
			//找第三句sql 
			String sql = "select strcheckSql from s_autohintselect where id = '"+autoId+"'" ;
			String strcheckSql = StringUtil.showNull(dbUtil.queryForString(sql)) ;
			if(!"".equals(strcheckSql)) {
				strcheckSql = strcheckSql.replaceAll("\\$1",value) ;
				
				for (int i = 1; i < selects.length; i++) {
					String refer = selects[i];
					
					if(!"".equals(refer) && refer.indexOf("_$rowIndex") > -1) {
						refer = refer.replaceAll("_\\$rowIndex","");
						//refer = prefix + refer;
					}
					
					String referValue = "" ;
					String enName = "" ;
					for (int j = 0; j < fieldList.size(); j++) { 
						Map map = (Map) fieldList.get(j);

						String name = StringUtil.showNull(String.valueOf(map.get("name")));
						
						if(name.equals(refer)){
							enName = StringUtil.showNull(String.valueOf(map.get("enname"))).toLowerCase();
						}
						
					}
					
					if("".equals(enName)){
						for (int j = 0; j < mainFieldList.size(); j++) { 
							Map map = (Map) mainFieldList.get(j);

							String name = StringUtil.showNull(String.valueOf(map.get("name")));
							
							if(name.equals(refer)){
								enName = StringUtil.showNull(String.valueOf(map.get("enname"))).toLowerCase();
							}
							
						}
					}
					
					if(dataMap != null) {
						referValue = StringUtil.showNull(String.valueOf(dataMap.get(enName)));
					}
					
					if("".equals(referValue)) {
						if(formMap != null) {
							referValue = StringUtil.showNull(String.valueOf(formMap.get(enName)));
						}
					}
					
					if("".equals(referValue)) {
						referValue = refer ;
					}
					
					if(!"".equals(referValue)) {
						strcheckSql = strcheckSql.replaceAll("\\$"+(i+1),referValue) ;
					}
					
				}
				
				strcheckSql = StringUtil.transRequestValue(request,strcheckSql) ;
				strcheckSql = StringUtil.transSessionValue(request.getSession(),strcheckSql) ;
				
				ps = conn.prepareStatement(strcheckSql) ;
				rs = ps.executeQuery() ;
				
				
				if(rs.next()) {
					rawValue = StringUtil.showNull(rs.getString(2)) ;
					
					if("".equals(rawValue)) {
						log.debug("后台替换下拉sql:"+strcheckSql) ;
					}
				}else {
					log.debug("后台替换下拉sql:"+strcheckSql) ;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return rawValue;
	}

}
