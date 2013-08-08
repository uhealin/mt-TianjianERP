package com.matech.framework.servlet.extmenu;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DBConnect;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

/**
 * 返回前台EXT所需异步加载的菜单的后台类
 * @author asus-0331
 *
 */
public class ExtMenuServlet  extends HttpServlet {
	
	/**
	 * 
	 */
	protected void doPost(HttpServletRequest requset, HttpServletResponse response)
    	throws ServletException, IOException {

		 
		String op = requset.getParameter("op");
		String menuid = requset.getParameter("menuid");
		
		response.setContentType("text/xml");
		response.setHeader("Cache-Control", "no-cache");
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = null;
		Connection conn=null;
		try{
			conn = new DBConnect().getConnect();
			out=response.getWriter() ;
			
			//System.out.println("=====================================================op=|"+op+"|menuid="+menuid);
			if ("1".equals(op)){
				out.write(getFirstMenu(requset,conn));
			}else if ("2".equals(op)){
				//out.write(getChildMenus(menuid,requset,conn));
				out.write(getChildMenus(menuid,requset,conn));
				
			}else if ("3".equals(op)){
				//out.write(getAllChildMenus(menuid,requset,conn));
				out.write(getAllChildMenus3(menuid, requset, conn));
			}
			
		}catch(Exception e){
			if (out!=null){
				try{
				out.close();
				}catch(Exception e1){}
			}
		}finally{
			DbUtil.close(conn);
		}
		
	}
	
	protected void doGet(HttpServletRequest requset, HttpServletResponse response)
		throws ServletException, IOException {
		doPost(requset,response);
	}
	
	private String getFirstMenu(HttpServletRequest requset,Connection conn){
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		JSONArray jsonArr = null ;
		ArrayList treeNodeList = new ArrayList() ;
		
		try {
			  DbUtil db = new DbUtil(conn);
			  
              Statement stmt = conn.createStatement();
              String sql = "",sql1 = "";
              
              UserSession userSession=(UserSession)requset.getSession().getAttribute("userSession");
              String userid = userSession.getUserId();
              String ppm = userSession.getUserPopedom();
              String centerId = requset.getParameter("centerId") ;
              String centerName = "" ;
              if(!"".equals(centerId)) {
            	  int cid ;
            	  try {
            		  cid = Integer.parseInt(centerId) ;
            	  }catch(Exception e) {
            		  cid = 1 ;
            	  }
	              switch(cid) {
	              	case 1:
	            	  centerName = "审计作业中心" ;
	            	  break ;
	              	case 2:
		            	  centerName = "项目管理中心" ;
		            	  break ;
	              	case 3:
		            	  centerName = "质量管理中心" ;
		            	  break ;
	              	case 4:
		            	  centerName = "客户管理中心" ;
		            	  break ;
	              	default :
		            	  centerName = "" ;
		            	  break ;
	              
	              }
              }else { 
            	  centerName = "审计作业中心" ;
              }
              
              String sqlWhere = "" ;
              if(!"".equals(centerName)) {
            	  sqlWhere = " where menuversion like '%" + centerName + "%'" ;
              }
              
              if (ppm.equals("all")) {
                  sql = "select distinct a.id,a.menu_id,a.name,act from (select * from s_sysmenu where parentID='00' and (depth<>'0' or name='退出系统')) a"
                	  + " left join k_menuversion b on a.id = b.menuid "+sqlWhere +" order by a.seq_no asc";
                  
                  sql1 ="select distinct 1 from (select b.* from k_userSysmenu a,s_sysmenu b where a.sysmenu = b.id and a.userid ='"+userid+"' ) a"
                          + " left join k_menuversion b on a.id = b.menuid "+sqlWhere +"  order by a.seq_no asc";
              } else {
              	ppm = new ASFuntion().replaceStr(ppm, ".", "','");
              	ppm = ppm.substring(2,ppm.length()-2);
                  sql ="select distinct a.id,a.menu_id,a.name,act from (select * from s_sysmenu where parentID='00' and id in ("+ppm+") and (depth<>'0' or name='退出系统')) a"
                  	  + " left join k_menuversion b on a.id = b.menuid "+sqlWhere +"  order by a.seq_no asc ";
                  
                  sql1 ="select distinct 1 from (select b.* from k_userSysmenu a,s_sysmenu b where a.sysmenu = b.id and a.userid ='"+userid+"' and id in ("+ppm+") ) a"
                      + " left join k_menuversion b on a.id = b.menuid "+sqlWhere +"  order by a.seq_no asc ";
              }
              String flag = new ASFuntion().showNull(db.queryForString(sql1));
              if("1".equals(flag)){
            	  //有快捷菜单
            	  FirstMenu menu = new FirstMenu() ;
            	  menu.setId("999999");
            	  menu.setTitle("快捷菜单");
            	  menu.setAct("");
                  
                  treeNodeList.add(menu) ;
              }
              
              
              rs = stmt.executeQuery(sql);
              while (rs.next()) {
            	  FirstMenu menu = new FirstMenu() ;
            	  
            	  menu.setId(rs.getString("menu_id"));
            	  menu.setTitle(rs.getString("name"));
            	  menu.setAct(rs.getString("act"));
                  
                  treeNodeList.add(menu) ;
              }
	          	
	          jsonArr = JSONArray.fromObject(treeNodeList) ;
	          
	          ////System.out.println(jsonArr);
	          return jsonArr.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "[]";
		} finally {
			DbUtil.close(ps);
			DbUtil.close(rs);
		}
		
	}
	
	public String getChildMenus(String menuid,HttpServletRequest requset,Connection conn){
		
		JSONArray jsonArr = null ;
		ArrayList treeNodeList = new ArrayList() ;
		
		Statement stmt = null;
		ResultSet rs = null;
		
		Statement stmt2 = null;
		ResultSet rs2 = null;
		
		try {
			
			  conn.createStatement();
              UserSession userSession=(UserSession)requset.getSession().getAttribute("userSession");
              ASFuntion ASF = new ASFuntion() ;

              
              String ppm = userSession.getUserPopedom();
              String userId = userSession.getUserId();

              String centerId = requset.getParameter("centerId") ;
              
              String centerName = "" ;
              if(!"".equals(centerId)) {
            	  int cid ;
            	  try {
            		  cid = Integer.parseInt(centerId) ;
            	  }catch(Exception e) {
            		  cid = 0 ;
            	  }
            
	              switch(cid) {
	              	case 1:
	            	  centerName = "审计作业中心" ;
	            	  break ;
	              	case 2:
		            	  centerName = "项目管理中心" ;
		            	  break ;
	              	case 3:
		            	  centerName = "质量管理中心" ;
		            	  break ;
	              	case 4:
		            	  centerName = "客户管理中心" ;
		            	  break ;
	              	case 6:
		            	  centerName = "档案管理中心" ;
		            	  break ; 
	              	case 7:
		            	  centerName = "erp中心" ;
		            	  break ;
	              	default :
		            	  centerName = "审计作业中心" ;
		            	  break ;
	              
	              }
              }else { 
            	  centerName = "审计作业中心" ;
              }
              
              String sqlWhere = "" ;
              if(!"".equals(centerName)) {
            	  sqlWhere = " where menuversion like '%" + centerName + "%'" ;
              }
              

              String sql = "";
              if (ppm.equals("all")) {
					sql = "select distinct a.id,a.menu_id,a.name,depth,act,ActiveX_method,group_concat(distinct c.dogid) as dogid from (select * from s_sysmenu where parentID='" + menuid
							+ "' and ctype='01') a "
							+ " left join k_menuversion b on a.id = b.menuid "
							+ " left join k_dogpopedom c on c.menuid like concat('%.',a.id,'.%') "
							+sqlWhere 
							+ " group by a.id order by menu_id ";
              } else {
					ppm = "'" + new ASFuntion().replaceStr(ppm, ".", "','")
							+ "'";
					sql = "select distinct a.id,a.menu_id,a.name,depth,act,ActiveX_method,group_concat(distinct c.dogid) as dogid from ( select * from s_sysmenu where parentID='" + menuid
							+ "' and ctype='01' and id in (" + ppm
							+ ")) a"
							+ " left join k_menuversion b on a.id = b.menuid "
							+ " left join k_dogpopedom c on c.menuid like concat('%.',a.id,'.%') "
							+sqlWhere 
							+ " group by a.id order by menu_id ";
              }
				
			//System.out.println("##"+sql);
              stmt = conn.createStatement();
              stmt2 = conn.createStatement();
              rs = stmt.executeQuery(sql);
              
              
              String act = "error_page.jsp";
              String parentID = "";
              String str_temp = "";
              String curmenuid="";
              
              /*
               * @todo
               * 
              boolean isValidate2 = "允许".equals(UTILSysProperty.SysProperty
						.getProperty("是否允许二次登录验证"));
              String memory = UTILSysProperty.SysProperty
						.getProperty("是否记忆系统菜单");
				*/
              
              while (rs.next()) {
            	  
            	  Menu menu=new Menu();
            	  
            	  curmenuid=rs.getString("id");
            	  menu.setId(curmenuid); 
            	  menu.setText(rs.getString("name"));
            	  menu.setActiveXMethod(rs.getString("ActiveX_method"));
            	  menu.setDogid(rs.getString("dogid"));
            	  
            	  if (rs.getInt("depth") != 1) {
            		  
            		  ////System.out.println("aaa:"+rs.getString("name"));
            		  
            		  //如果是叶子
            		  menu.setLeaf(true);
            		  
            		  str_temp = ASF.showNull(rs.getString("act"));
            		  
            		  if (!str_temp.equals("")) {
	  						act = str_temp;
	  					} else {
	  						act = "error_page.jsp";
	  					}
            		  
            		  if (act.indexOf("?") >= 0) {
							act = act + "&menuid="+curmenuid;
						} else {
							act = act + "?menuid="+curmenuid;
						}
            		  
            		  menu.setHref(act);
            		  
            	  }else{
            		  //如果有下级
            		  
            		  menu.setLeaf(false);
            		  
            		  ArrayList treeNodeList2 = new ArrayList() ;
            		  
            		  parentID=rs.getString("menu_id");
            		  
            		  //有下级
            		  if (ppm.equals("all")) {
							sql = "select distinct a.id,a.menu_id,a.name,depth,act,ActiveX_method,group_concat(distinct c.dogid) as dogid from (select * from s_sysmenu where parentID='"
									+ parentID
									+ "' and ctype='01' ) a"
									+ " left join k_menuversion b on a.id = b.menuid "
									+ " left join k_dogpopedom c on c.menuid like concat('%.',a.id,'.%') "
									+sqlWhere +"  group by a.id  order by menu_id ";
            		  } else {
							sql = "select distinct a.id,a.menu_id,a.name,depth,act,ActiveX_method,group_concat(distinct c.dogid) as dogid from (select * from s_sysmenu where parentID='"
									+ parentID + "' and ctype='01' and id in ("
									+ ppm + ") ) a"
									+ " left join k_menuversion b on a.id = b.menuid "
									+ " left join k_dogpopedom c on c.menuid like concat('%.',a.id,'.%') "
									+sqlWhere +"  group by a.id  order by menu_id ";
            		  }

            		  //System.out.println("##2"+sql);
            		  rs2 = stmt2.executeQuery(sql);
					  while (rs2.next()) {
						  	Menu menu2=new Menu();
						  	
						  	curmenuid=rs2.getString("id");
						  	menu2.setId(curmenuid); 
			            	menu2.setText(rs2.getString("name"));
			            	menu2.setActiveXMethod(rs2.getString("ActiveX_method"));
			            	menu2.setDogid(rs2.getString("dogid"));
			            	
			            	//如果是叶子
		            		menu2.setLeaf(true);
		            		  
		            		str_temp = ASF.showNull(rs2.getString("act"));
		            		  
		            		if (!str_temp.equals("")) {
			  						act = str_temp;
			  				} else {
			  						act = "error_page.jsp";
			  				}
		            		  
		            		 if (act.indexOf("?") >= 0) {
									act = act + "&menuid="+curmenuid;
								} else {
									act = act + "?menuid="+curmenuid;
								}
		            		  
		            		menu2.setHref(act);
		            		
		            		treeNodeList2.add(menu2);
							
					}
					rs2.close();
					
					menu.setChildren(treeNodeList2);
					
					
            	  }
            	  treeNodeList.add(menu);
              }//while
              
	          jsonArr = JSONArray.fromObject(treeNodeList) ;
	          //System.out.println("#####"+jsonArr.toString());
	          return jsonArr.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "[]";
		} finally {
			DbUtil.close(stmt);
			DbUtil.close(stmt2);
			DbUtil.close(rs);
			DbUtil.close(rs2);
		}
		
	}
	
	private List getTreeMenus(String rootmenuid,List<Menu> menus){
		List treeNodeList = new ArrayList() ;
		for(Menu menu :menus){
			if(rootmenuid.equals(menu.getParentid())){
				if(!menu.isLeaf()){
				  menu.setChildren(getTreeMenus(menu.getMenuid(), menus));
				}
				treeNodeList.add(menu);
			}
		}
		return treeNodeList;
	}
	
	private List getTreeMenus(String menuid,HttpServletRequest requset,Connection conn){
		JSONArray jsonArr = null ;
		ArrayList treeNodeList = new ArrayList() ;
		
		Statement stmt = null;
		ResultSet rs = null;
		
		Statement stmt2 = null;
		ResultSet rs2 = null;
		try {
			
			  conn.createStatement();
            UserSession userSession=(UserSession)requset.getSession().getAttribute("userSession");
            ASFuntion ASF = new ASFuntion() ;

            
            String ppm = userSession.getUserPopedom();
            String userId = userSession.getUserId();

            String centerId = requset.getParameter("centerId") ;
            
            String centerName = "" ;
            if(!"".equals(centerId)) {
          	  int cid ;
          	  try {
          		  cid = Integer.parseInt(centerId) ;
          	  }catch(Exception e) {
          		  cid = 0 ;
          	  }
          
	              switch(cid) {
	              	case 1:
	            	  centerName = "审计作业中心" ;
	            	  break ;
	              	case 2:
		            	  centerName = "项目管理中心" ;
		            	  break ;
	              	case 3:
		            	  centerName = "质量管理中心" ;
		            	  break ;
	              	case 4:
		            	  centerName = "客户管理中心" ;
		            	  break ;
	              	case 6:
		            	  centerName = "档案管理中心" ;
		            	  break ; 
	              	case 7:
		            	  centerName = "erp中心" ;
		            	  break ;
	              	default :
		            	  centerName = "审计作业中心" ;
		            	  break ;
	              
	              }
            }else { 
          	  centerName = "审计作业中心" ;
            }
            
            String sqlWhere = "" ;
            if(!"".equals(centerName)) {
          	  sqlWhere = " where menuversion like '%" + centerName + "%'" ;
            }
            

            String sql = "";
            if (ppm.equals("all")) {
					sql = "select distinct a.id,a.ctype,a.menu_id,a.name,depth,act,ActiveX_method,group_concat(distinct c.dogid) as dogid from (select * from s_sysmenu where parentID='" + menuid
							+ "' and ctype in ('01','03')) a "
							+ " left join k_menuversion b on a.id = b.menuid "
							+ " left join k_dogpopedom c on c.menuid like concat('%.',a.id,'.%') "
							+sqlWhere 
							+ " group by a.id order by menu_id ";
            } else {
					ppm = "'" + new ASFuntion().replaceStr(ppm, ".", "','")
							+ "'";
					sql = "select distinct a.id,a.ctype,a.menu_id,a.name,depth,act,ActiveX_method,group_concat(distinct c.dogid) as dogid from ( select * from s_sysmenu where parentID='" + menuid
							+ "' and ctype in ('01','03') and id in (" + ppm
							+ ")) a"
							+ " left join k_menuversion b on a.id = b.menuid "
							+ " left join k_dogpopedom c on c.menuid like concat('%.',a.id,'.%') "
							+sqlWhere 
							+ " group by a.id order by menu_id ";
            }
				
			//System.out.println("##"+sql);
            stmt = conn.createStatement();
            stmt2 = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            
            String act = "error_page.jsp";
            String parentID = "";
            String str_temp = "";
            String curmenuid="";
            
            /*
             * @todo
             * 
            boolean isValidate2 = "允许".equals(UTILSysProperty.SysProperty
						.getProperty("是否允许二次登录验证"));
            String memory = UTILSysProperty.SysProperty
						.getProperty("是否记忆系统菜单");
				*/
            
            while (rs.next()) {
          	  
          	  Menu menu=new Menu();
          	  String ctype=rs.getString("ctype");
          	  curmenuid=rs.getString("id");
          	  menu.setId(curmenuid); 
          	  menu.setText(rs.getString("name"));
          	  menu.setActiveXMethod(rs.getString("ActiveX_method"));
          	  menu.setDogid(rs.getString("dogid"));
          	
          	  menu.setLeaf(rs.getInt("depth") != 1||"03".equals(ctype));
          	  
          	  if (rs.getInt("depth") != 1||"03".equals(ctype)) {
          		  
          		  ////System.out.println("aaa:"+rs.getString("name"));
          		  
          		  //如果是叶子
          		  //menu.setLeaf(true);
          		  
          		  str_temp = ASF.showNull(rs.getString("act"));
          		  
          		  if (!str_temp.equals("")) {
	  						act = str_temp;
	  					} else {
	  						act = "error_page.jsp";
	  					}
          		  
          		  if (act.indexOf("?") >= 0) {
							act = act + "&menuid="+curmenuid;
						} else {
							act = act + "?menuid="+curmenuid;
						}
          		  
          		  menu.setHref(act);
          		  if("03".equals(ctype)){
          			  menu.setHref("sysMenuManger.do?method=tabView&menuid="+curmenuid);
          		  }
          		  
          	  }else{
          		  //如果有下级
          		  
          		  menu.setLeaf(false);
          		  
          		  ArrayList treeNodeList2 = new ArrayList() ;
          		  
          		  parentID=rs.getString("menu_id");
          		  /*
          		  //有下级
          		  if (ppm.equals("all")) {
							sql = "select distinct a.id,a.menu_id,a.name,depth,act,ActiveX_method,group_concat(distinct c.dogid) as dogid from (select * from s_sysmenu where parentID='"
									+ parentID
									+ "' and ctype='01' ) a"
									+ " left join k_menuversion b on a.id = b.menuid "
									+ " left join k_dogpopedom c on c.menuid like concat('%.',a.id,'.%') "
									+sqlWhere +"  group by a.id  order by menu_id ";
          		  } else {
							sql = "select distinct a.id,a.menu_id,a.name,depth,act,ActiveX_method,group_concat(distinct c.dogid) as dogid from (select * from s_sysmenu where parentID='"
									+ parentID + "' and ctype='01' and id in ("
									+ ppm + ") ) a"
									+ " left join k_menuversion b on a.id = b.menuid "
									+ " left join k_dogpopedom c on c.menuid like concat('%.',a.id,'.%') "
									+sqlWhere +"  group by a.id  order by menu_id ";
          		  }

          		  //System.out.println("##2"+sql);
          		  rs2 = stmt2.executeQuery(sql);
					  while (rs2.next()) {
						  	Menu menu2=new Menu();
						  	
						  	curmenuid=rs2.getString("id");
						  	menu2.setId(curmenuid); 
			            	menu2.setText(rs2.getString("name"));
			            	menu2.setActiveXMethod(rs2.getString("ActiveX_method"));
			            	menu2.setDogid(rs2.getString("dogid"));
			            	
			            	//如果是叶子
		            		menu2.setLeaf(true);
		            		  
		            		str_temp = ASF.showNull(rs2.getString("act"));
		            		  
		            		if (!str_temp.equals("")) {
			  						act = str_temp;
			  				} else {
			  						act = "error_page.jsp";
			  				}
		            		  
		            		 if (act.indexOf("?") >= 0) {
									act = act + "&menuid="+curmenuid;
								} else {
									act = act + "?menuid="+curmenuid;
								}
		            		  
		            		menu2.setHref(act);
		            		
		            		treeNodeList2.add(menu2);
							
					}
					
					rs2.close();
					
					//menu.setChildren(treeNodeList2);
					 * 
					 */
					menu.setChildren(getTreeMenus(parentID, requset, conn));
					
          	  }
          	  treeNodeList.add(menu);
            }//while
            
	        //  jsonArr = JSONArray.fromObject(treeNodeList) ;
	        //  //System.out.println("#####"+jsonArr.toString());
	         // return jsonArr.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList();
		} finally {
			DbUtil.close(stmt);
			//DbUtil.close(stmt2);
			DbUtil.close(rs);
			//DbUtil.close(rs2);
		}
		return treeNodeList;
	}
	
	
private String getAllChildMenus(String menuid,HttpServletRequest requset,Connection conn){
		
		JSONArray jsonArr = null ;
		ArrayList treeNodeList = new ArrayList() ;
		
		Statement stmt = null;
		ResultSet rs = null;
		
		
		
		try {
		
         
	          jsonArr = JSONArray.fromObject(getTreeMenus(menuid, requset, conn)) ;
	          //System.out.println("#####"+jsonArr.toString());
	          return jsonArr.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "[]";
		} finally {
			DbUtil.close(stmt);
			
			DbUtil.close(rs);
			
		}
		
	}


//无限极菜单，龟速
private String getAllChildMenus2(String menuid,HttpServletRequest requset,Connection conn){
	
	JSONArray jsonArr = null ;
	ArrayList treeNodeList = new ArrayList() ;
	
	Statement stmt = null;
	ResultSet rs = null;
	
	Statement stmt2 = null;
	ResultSet rs2 = null;
	
	try {
		
		  conn.createStatement();
          UserSession userSession=(UserSession)requset.getSession().getAttribute("userSession");
          ASFuntion ASF = new ASFuntion() ;

          
          String ppm = userSession.getUserPopedom();
          String userId = userSession.getUserId();

          String centerId = requset.getParameter("centerId") ;
          
          String centerName = "" ;
          if(!"".equals(centerId)) {
        	  int cid ;
        	  try {
        		  cid = Integer.parseInt(centerId) ;
        	  }catch(Exception e) {
        		  cid = 0 ;
        	  }
        
              switch(cid) {
              	case 1:
            	  centerName = "审计作业中心" ;
            	  break ;
              	case 2:
	            	  centerName = "项目管理中心" ;
	            	  break ;
              	case 3:
	            	  centerName = "质量管理中心" ;
	            	  break ;
              	case 4:
	            	  centerName = "客户管理中心" ;
	            	  break ;
              	case 6:
	            	  centerName = "档案管理中心" ;
	            	  break ; 
              	case 7:
	            	  centerName = "erp中心" ;
	            	  break ;
              	default :
	            	  centerName = "审计作业中心" ;
	            	  break ;
              
              }
          }else { 
        	  centerName = "审计作业中心" ;
          }
          
          String sqlWhere = "" ;
          if(!"".equals(centerName)) {
        	  sqlWhere = " where menuversion like '%" + centerName + "%'" ;
          }
          

          String sql = "";
          if (ppm.equals("all")) {
				sql = "select distinct a.id,a.menu_id,a.name,depth,act,ActiveX_method,group_concat(distinct c.dogid) as dogid,a.parentID from (select * from s_sysmenu where "
						+ " ctype='01') a "
						+ " left join k_menuversion b on a.id = b.menuid "
						+ " left join k_dogpopedom c on c.menuid like concat('%.',a.id,'.%') "
						+sqlWhere 
						+ " group by a.id order by menu_id ";
          } else {
				ppm = "'" + new ASFuntion().replaceStr(ppm, ".", "','")
						+ "'";
				sql = "select distinct a.id,a.menu_id,a.name,depth,act,ActiveX_method,group_concat(distinct c.dogid) as dogid,a.parentID from ( select * from s_sysmenu where "
						+ " ctype='01' and id in (" + ppm
						+ ")) a"
						+ " left join k_menuversion b on a.id = b.menuid "
						+ " left join k_dogpopedom c on c.menuid like concat('%.',a.id,'.%') "
						+sqlWhere 
						+ " group by a.id order by menu_id ";
          }
			 
		//System.out.println("##"+sql);
          stmt = conn.createStatement();
          stmt2 = conn.createStatement();
          rs = stmt.executeQuery(sql);
          
          
          String act = "error_page.jsp";
          String parentID = "";
          String str_temp = "";
          String curmenuid="";
	
	      List<Menu> menus=new ArrayList<Menu>();
	      
	      while(rs.next()){
	    	  Menu menu=new Menu();
        	  
        	  curmenuid=rs.getString("id");
        	  menu.setId(curmenuid); 
        	  menu.setText(rs.getString("name"));
        	  menu.setActiveXMethod(rs.getString("ActiveX_method"));
        	  menu.setDogid(rs.getString("dogid"));
        	  menu.setParentid(rs.getString("parentid"));
        	  if (rs.getInt("depth") != 1) {
        		  
        		  ////System.out.println("aaa:"+rs.getString("name"));
        		  
        		  //如果是叶子
        		  menu.setLeaf(true);
        		  
        		  str_temp = ASF.showNull(rs.getString("act"));
        		  
        		  if (!str_temp.equals("")) {
  						act = str_temp;
  					} else {
  						act = "error_page.jsp";
  					}
        		  
        		  if (act.indexOf("?") >= 0) {
						act = act + "&menuid="+curmenuid;
					} else {
						act = act + "?menuid="+curmenuid;
					}
        		  
        		  menu.setHref(act);
        	  }else {
        		  menu.setLeaf(false);
        	  }
        	  menus.add(menu);
	      }
     
          jsonArr = JSONArray.fromObject(getTreeMenus(menuid, menus)) ;
          //System.out.println("#####"+jsonArr.toString());
          return jsonArr.toString();
	} catch (Exception e) {
		e.printStackTrace();
		return "[]";
	} finally {
		DbUtil.close(stmt);
		
		DbUtil.close(rs);
		
	}
	
}
   
//无限级菜单，闪电加载
private String getAllChildMenus3(String menuid,HttpServletRequest requset,Connection conn){
	
	JSONArray jsonArr = null ;
	List treeNodeList = new ArrayList() ;
	
	Statement stmt = null;
	ResultSet rs = null;
	
	List<Menu> menus=new ArrayList<Menu>();
	try {
		
		  conn.createStatement();
          UserSession userSession=(UserSession)requset.getSession().getAttribute("userSession");
          ASFuntion ASF = new ASFuntion() ;

          
          String ppm = userSession.getUserPopedom();
          String userId = userSession.getUserId();

          String centerId = requset.getParameter("centerId") ;
          
          String centerName = "" ;
          if(!"".equals(centerId)) {
        	  int cid ;
        	  try {
        		  cid = Integer.parseInt(centerId) ;
        	  }catch(Exception e) {
        		  cid = 0 ;
        	  }
        
              switch(cid) {
              	case 1:
            	  centerName = "审计作业中心" ;
            	  break ;
              	case 2:
	            	  centerName = "项目管理中心" ;
	            	  break ;
              	case 3:
	            	  centerName = "质量管理中心" ;
	            	  break ;
              	case 4:
	            	  centerName = "客户管理中心" ;
	            	  break ;
              	case 6:
	            	  centerName = "档案管理中心" ;
	            	  break ; 
              	case 7:
	            	  centerName = "erp中心" ;
	            	  break ;
              	default :
	            	  centerName = "审计作业中心" ;
	            	  break ;
              
              }
          }else { 
        	  centerName = "审计作业中心" ;
          }
          
          String sqlWhere = "" ;
          if(!"".equals(centerName)) {
        	  sqlWhere = " where menuversion like '%" + centerName + "%'" ;
          }
          

          String sql = "",sql1 = "";
          if (ppm.equals("all")) {
				sql = "select distinct a.id,a.menu_id,a.name,depth,act,ActiveX_method,group_concat(distinct c.dogid) as dogid,a.parentID,a.ctype from (select * from s_sysmenu where "
						+ " ctype in ('01','03')) a "
						+ " left join k_menuversion b on a.id = b.menuid "
						+ " left join k_dogpopedom c on c.menuid like concat('%.',a.id,'.%') "
						+sqlWhere 
						+ " group by a.id order by menu_id ";
				sql1 = "select distinct a.id,a.menu_id,a.name,depth,act,ActiveX_method,group_concat(distinct c.dogid) as dogid,'999999' as parentID,a.ctype from ( "
						+"select a.*,b.orderid as borderid from s_sysmenu a,k_userSysmenu b  where ctype in ('01','03')  and a.id = b.sysmenu and b.userId='"+userId+"' "
						+") a"
						+ " left join k_menuversion b on a.id = b.menuid "
						+ " left join k_dogpopedom c on c.menuid like concat('%.',a.id,'.%') "
						+sqlWhere 
						+ " group by a.id order by borderid,menu_id ";
          } else {
				ppm = "'" + new ASFuntion().replaceStr(ppm, ".", "','")
						+ "'";
				sql = "select distinct a.id,a.menu_id,a.name,depth,act,ActiveX_method,group_concat(distinct c.dogid) as dogid,a.parentID,a.ctype from ( select * from s_sysmenu where "
						+ " ctype in ('01','03') and id in (" + ppm
						+ ")) a"
						+ " left join k_menuversion b on a.id = b.menuid "
						+ " left join k_dogpopedom c on c.menuid like concat('%.',a.id,'.%') "
						+sqlWhere 
						+ " group by a.id order by menu_id ";
				sql1 = "select distinct a.id,a.menu_id,a.name,depth,act,ActiveX_method,group_concat(distinct c.dogid) as dogid,'999999' as parentID,a.ctype from ( "
						+"select a.*,b.orderid as borderid from s_sysmenu a,k_userSysmenu b  where ctype in ('01','03') and id in (" + ppm+ ") and a.id = b.sysmenu and b.userId='"+userId+"' "
						+") a"
						+ " left join k_menuversion b on a.id = b.menuid "
						+ " left join k_dogpopedom c on c.menuid like concat('%.',a.id,'.%') "
						+sqlWhere 
						+ " group by a.id order by borderid,menu_id ";
          }
			 
		////System.out.println("##"+sql);
          if("999999".equals(menuid)){
        	  sql = sql1;
          }
          stmt = conn.createStatement();
          rs = stmt.executeQuery(sql);
          
          
          String act = "error_page.jsp";
          String str_temp = "";
          String curmenuid="";
	
	      
	      
	      while(rs.next()){
	    	  Menu menu=new Menu();
        	  
        	  curmenuid=rs.getString("id");
        	  String ctype=rs.getString("ctype"); 
        	  menu.setId(curmenuid);
        	  menu.setMenuid(rs.getString("menu_id"));
        	  menu.setText(rs.getString("name"));
        	  menu.setActiveXMethod(rs.getString("ActiveX_method"));
        	  menu.setDogid(rs.getString("dogid"));
        	  menu.setParentid(rs.getString("parentid"));
        	  
        	  menu.setLeaf(rs.getInt("depth") != 1||"03".equals(ctype));
        	  
        	  if (menu.isLeaf()) {
          		  
          		  ////System.out.println("aaa:"+rs.getString("name"));
          		  
          		  //如果是叶子
          		  //menu.setLeaf(true);
          		  
          		  str_temp = ASF.showNull(rs.getString("act"));
          		  
          		  if (!str_temp.equals("")) {
	  						act = str_temp;
	  					} else {
	  						act = "error_page.jsp";
	  					}
          		  
          		  if (act.indexOf("?") >= 0) {
							act = act + "&menuid="+curmenuid;
						} else {
							act = act + "?menuid="+curmenuid;
						}
          		  
          		  menu.setHref(act);
          		  if("03".equals(ctype)){
          			  menu.setHref("sysMenuManger.do?method=tabView&menuid="+curmenuid);
          		  }
          		  
          	  }
        	  menus.add(menu);
	      }
	      treeNodeList=getTreeMenus(menuid, menus);
	}catch(Exception ex){
	}
	return JSONArray.fromObject(treeNodeList).toString();
	}
}
