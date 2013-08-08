package com.matech.audit.work.option;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.option.OptionVO;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;

public class OptionAction extends MultiActionController{
	   
	private final String _VIEW = "/option/view.jsp";
	private final String _RESULT = "/option/result.jsp";
	private final String _SAVE = "/option/save.jsp";
	
	
	

	public ModelAndView result(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(_RESULT);
		
		List<String> list = new ArrayList<String>();
		HttpSession session = request.getSession();
		
		OptionVO op = new OptionVO();
		
		String uuid = request.getParameter("uuid");// 获取前台传过来的值 autoId
		String anonymous = request.getParameter("anonymous");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		int op1Count = 0;
		int op2Count = 0;
		int op3Count = 0;
		int op4Count = 0;
		int op5Count = 0;
		int op6Count = 0;
		int op7Count = 0;
		int op8Count = 0;
		int op9Count = 0;
		int op10Count = 0;
		int totalCount = 0;
		
	
		try {
			conn = new DBConnect().getConnect("");
			
			
			/*
			String sql = 
				"SELECT a.*," +
				"SUM(IF(b.op1 >'',1,0))  AS result1," +
				"SUM(IF(b.op2 >'',1,0))  AS result2," +
				"SUM(IF(b.op3 >'',1,0))  AS result3," +
				"SUM(IF(b.op4 >'',1,0))  AS result4," +
				"SUM(IF(b.op5 >'',1,0))  AS result5," +
				"SUM(IF(b.op6 >'',1,0))  AS result6," +
				"SUM(IF(b.op7 >'',1,0))  AS result7," +
				"SUM(IF(b.op8 >'',1,0))  AS result8," +
				"SUM(IF(b.op9 >'',1,0))  AS result9," +
				"SUM(IF(b.op10 >'',1,0))  AS result10" +		//加空格
				" FROM k_option a" +
				" LEFT JOIN k_optionresult b ON a.uuid=b.uuid" +
				" WHERE a.uuid='" + uuid + "'" +
				" GROUP BY a.uuid";
			*/

			String sql = 
				"SELECT a.*," +
				"SUM(IF(b.op1 >'',1,0))  AS result1," +
				"SUM(IF(b.op2 >'',1,0))  AS result2," +
				"SUM(IF(b.op3 >'',1,0))  AS result3," +
				"SUM(IF(b.op4 >'',1,0))  AS result4," +
				"SUM(IF(b.op5 >'',1,0))  AS result5," +
				"SUM(IF(b.op6 >'',1,0))  AS result6," +
				"SUM(IF(b.op7 >'',1,0))  AS result7," +
				"SUM(IF(b.op8 >'',1,0))  AS result8," +
				"SUM(IF(b.op9 >'',1,0))  AS result9," +
				"SUM(IF(b.op10 >'',1,0))  AS result10," +
				"GROUP_CONCAT(DISTINCT  IF(b.op1 >'',b.username,'')  ) AS name1," +
				"GROUP_CONCAT(DISTINCT  IF(b.op2 >'',b.username,'')  ) AS name2," +
				"GROUP_CONCAT(DISTINCT  IF(b.op3 >'',b.username,'')  ) AS name3," +
				"GROUP_CONCAT(DISTINCT  IF(b.op4 >'',b.username,'')  ) AS name4," +
				"GROUP_CONCAT(DISTINCT  IF(b.op5 >'',b.username,'')  ) AS name5," +
				"GROUP_CONCAT(DISTINCT  IF(b.op6 >'',b.username,'')  ) AS name6," +
				"GROUP_CONCAT(DISTINCT  IF(b.op7 >'',b.username,'')  ) AS name7," +
				"GROUP_CONCAT(DISTINCT  IF(b.op8 >'',b.username,'')  ) AS name8," +
				"GROUP_CONCAT(DISTINCT  IF(b.op9 >'',b.username,'')  ) AS name9," +
				"GROUP_CONCAT(DISTINCT  IF(b.op10 >'',b.username,'')  ) AS name10" +
				" FROM k_option a " +
				" LEFT JOIN k_optionresult b ON a.uuid=b.uuid" +
				" WHERE a.uuid='" + uuid + "' GROUP BY a.uuid";
						
			//System.out.println("冬瓜" + sql);
			
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			
			if (rs.next()) {
				
				op.setUuid(rs.getString("uuid"));
				op.setTitle(rs.getString("title"));
				op.setOp1(rs.getString("op1"));
				op.setOp2(rs.getString("op2"));
				op.setOp3(rs.getString("op3"));
				op.setOp4(rs.getString("op4"));
				op.setOp5(rs.getString("op5"));
				op.setOp6(rs.getString("op6"));
				op.setOp7(rs.getString("op7"));
				op.setOp8(rs.getString("op8"));
				op.setOp9(rs.getString("op9"));
				op.setOp10(rs.getString("op10"));
				
				list.add(rs.getString("op1"));
				list.add(rs.getString("op2"));
				list.add(rs.getString("op3"));
				list.add(rs.getString("op4"));
				list.add(rs.getString("op5"));
				list.add(rs.getString("op6"));
				list.add(rs.getString("op7"));
				list.add(rs.getString("op8"));
				list.add(rs.getString("op9"));
				list.add(rs.getString("op10"));
				
				
				op.setResult1(rs.getString("result1"));
				op.setResult2(rs.getString("result2"));
				op.setResult3(rs.getString("result3"));
				op.setResult4(rs.getString("result4"));
				op.setResult5(rs.getString("result5"));
				op.setResult6(rs.getString("result6"));
				op.setResult7(rs.getString("result7"));
				op.setResult8(rs.getString("result8"));
				op.setResult9(rs.getString("result9"));
				op.setResult10(rs.getString("result10"));
				
				
				op.setAnonymous(rs.getString("anonymous"));
				op.setOnlyonece(rs.getString("onlyonece"));
				op.setOpttype(rs.getString("opttype"));
				
				op1Count = Integer.parseInt(rs.getString("result1"));
				op2Count = Integer.parseInt(rs.getString("result2"));
				op3Count = Integer.parseInt(rs.getString("result3"));
				op4Count = Integer.parseInt(rs.getString("result4"));
				op5Count = Integer.parseInt(rs.getString("result5"));
				op6Count = Integer.parseInt(rs.getString("result6"));
				op7Count = Integer.parseInt(rs.getString("result7"));
				op8Count = Integer.parseInt(rs.getString("result8"));
				op9Count = Integer.parseInt(rs.getString("result9"));
				op10Count = Integer.parseInt(rs.getString("result10"));
				totalCount = op1Count + op2Count + op3Count + op4Count + op5Count + op6Count + op7Count + op8Count + op9Count + op10Count;
				
				

				
				String name="";
				for(int i=1;i<=10;i++){
					name = rs.getString("name"+i);
					try{
						
						//System.out.println("dddd:"+name.substring(0,1)+"|"+name.substring(0,name.length()-1));
						name = name.replaceAll(",,", ",");
						if (name!=null && ",".equals(name.substring(0,1))){
							name=name.substring(1);
						}
						
						if (name!=null && ",".equals(name.substring(name.length()-1,name.length()))){
							name=name.substring(0,name.length()-1);
						}
						
					}catch(Exception e){}
					
					session.setAttribute("name"+i, name);
				}
			
				
 			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		
		
		
		session.setAttribute("op1Count", op1Count);
		session.setAttribute("op2Count", op2Count);
		session.setAttribute("op3Count", op3Count);
		session.setAttribute("op4Count", op4Count);
		session.setAttribute("op5Count", op5Count);
		session.setAttribute("op6Count", op6Count);
		session.setAttribute("op7Count", op7Count);
		session.setAttribute("op8Count", op8Count);
		session.setAttribute("op9Count", op9Count);
		session.setAttribute("op10Count", op10Count);
		session.setAttribute("totalCount", totalCount);
		
	
		
		session.setAttribute("list", list);
		
		modelAndView.addObject("uuid", uuid);// 传值
		modelAndView.addObject("op",op);	 // 传值
		
		String msg=(String)request.getAttribute("msg");
		
		//if (msg==null) msg="";
		modelAndView.addObject("msg",msg);
		session.setAttribute("msg", msg);
				
		return modelAndView;

	}
	
	
	
	public ModelAndView save(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String uuid = request.getParameter("uuid");
		String onlyonece = request.getParameter("onlyonece");	//是否只投一次
		String inputtype = request.getParameter("inputtype");
		String anonymous = request.getParameter("anonymous");
		String endtime = request.getParameter("endtime");
		//System.out.println("邓冬瓜" + endtime);
		
		boolean bijiao = false;
		String msg = null;
		String newDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		//System.out.println(newDate);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date start = dateFormat.parse(newDate);
		Date end = dateFormat.parse(endtime);
		if(end.getTime() - start.getTime() >= 0) {
			bijiao = true;
		}
		
		//System.out.println(bijiao);
		if(bijiao) {
		
		Connection conn = null;
		
		//String msg="";
		
		try {
			String opt = request.getParameter("opt");
			int intOpt = Integer.parseInt(opt);
			
			
			UserSession us=(UserSession)request.getSession().getAttribute("userSession");
			conn = new DBConnect().getConnect("");
			
			
			//先取得设置
			boolean bContinue = true;
			
			if ("true".equals(onlyonece)){
				//检查是不是已经投票了
				String sql = "select count(*) from k_optionresult where uuid=? and userid=?";
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1,uuid);
				ps.setString(2,us.getUserId());
				ResultSet rs = ps.executeQuery();
				
				rs.next();
				int count = rs.getInt(1);

				//如果已经投票了就不允许投
				if(count >= 1) {
					msg="您已经投过票。";
					bContinue=false;
					//ModelAndView modelAndView = new ModelAndView(_SAVE);
					//return modelAndView;
				}
			}
			
			
			if(bContinue){
				//保存
				PreparedStatement ps = conn.prepareStatement("insert into k_optionresult values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				ps.setString(1, uuid);
				ps.setString(2, null);
				ps.setString(3, new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
				ps.setString(4, us.getUserId());
				
				/*
				if("false".equals(anonymous)) {
					ps.setString(5,us.getUserName());					
				} else {
					ps.setString(5, "");		//为空还是null
				}
				*/
				ps.setString(5,us.getUserName());

				
				if("radio".equals(inputtype)) {
					PreparedStatement pstmt2 = conn.prepareStatement("select * from k_option where uuid = ?");
					String str = "op" + opt;
					pstmt2.setString(1, uuid);
					ResultSet rs = pstmt2.executeQuery();
					rs.next();
					//System.out.println(rs.getString(str));
					String updateStr = rs.getString(str);
					
					for(int i=1; i<=10; i++) {
						if(i == intOpt) 
							ps.setString(i+5, updateStr);
						else
							ps.setString(i+5, null);	//加空
					}
					
				} else {
					PreparedStatement pstmt2 = conn.prepareStatement("select * from k_option where uuid = ?");
					pstmt2.setString(1, uuid);
					ResultSet rs = pstmt2.executeQuery();
					rs.next();
					
					String[] opts = request.getParameterValues("opt");
					for(int i=1; i<=10; i++) {
						ps.setString(i+5, null);	//加空
						for(String str : opts) {
							if(Integer.parseInt(str) == i) {
								ps.setString(i+5, rs.getString(i+6));
							}
						}
					}
				}
			
				ps.executeUpdate();		
			}
						
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		//时间判断
		} else {
			//msg = "对不起，投票的截止时间已过。此次投票无效！！";
			//ModelAndView modelAndView = new ModelAndView(_SAVE);
			//return modelAndView;
		}
		
		HttpSession session = request.getSession();
		session.setAttribute("anonymous", anonymous);
		
		ModelAndView modelAndView = new ModelAndView("/option.do?method=result");
		modelAndView.addObject("msg",msg);
		modelAndView.addObject("anonymous",anonymous);
 		return modelAndView;
	}
	
	/**
	 * 修改跳转
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView view(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(_VIEW);
		
		List<String> list = new ArrayList<String>();
		List<String> list1 = new ArrayList<String>();
		
		OptionVO op = new OptionVO();

		String uuid = request.getParameter("uuid");// 获取前台传过来的值 autoId
		HttpSession session = request.getSession();

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		String inputtype="radio";
		String anonymous = "否";
		String onlyonece = "否";
		
		String msg = null;
		UserSession us=(UserSession)request.getSession().getAttribute("userSession");
		boolean bContinue = false;
		boolean bijiao = true;

		try {
			conn = new DBConnect().getConnect("");
			
			
			String endTimeSql = "select * from k_option where uuid = '" + uuid + "'";
			PreparedStatement endTimePs = conn.prepareStatement(endTimeSql);
			ResultSet endTimeRs = endTimePs.executeQuery();
			endTimeRs.next();
			String endTimeStr = endTimeRs.getString("endtime");
			String onlyonece2 = endTimeRs.getString("onlyonece");
			//System.out.println("冬瓜：" + onlyonece2);
			
			
			//String msg = null;
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String newDate = dateFormat.format(new Date());
			Date start = dateFormat.parse(newDate);
			Date end = dateFormat.parse(endTimeStr);
			if(end.getTime() - start.getTime() >= 0) {
				bijiao = false;
			}
			
			//if(bijiao) {
			
			System.out.println("冬瓜"+bijiao);	
				
				if ("true".equals(onlyonece2)){
					//检查是不是已经投票了
					String onlyoneceSql = "select count(*) from k_optionresult where uuid=? and userid=?";
					PreparedStatement onlyonecePs = conn.prepareStatement(onlyoneceSql);
					onlyonecePs.setString(1,uuid);
					onlyonecePs.setString(2,us.getUserId());
					ResultSet onlyoneceRs = onlyonecePs.executeQuery();
					
					onlyoneceRs.next();
					int count = onlyoneceRs.getInt(1);

					//如果已经投票了就不允许投
					if(count >= 1) {
						msg="您已经投过票,此次投票无效!!";
						bContinue = true;
					}
				}
			
			
			
			
			

				String sql = "select * from k_option where uuid='"	+ uuid + "'";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				
				String s1=" ",s2=" style='display:none' ";
				if (rs.next()) {
					
					
					op.setUuid(rs.getString("uuid"));
					op.setTitle(rs.getString("title"));
					op.setOp1(rs.getString("op1"));
					op.setOp2(rs.getString("op2"));
					op.setOp3(rs.getString("op3"));
					op.setOp4(rs.getString("op4"));
					op.setOp5(rs.getString("op5"));
					op.setOp6(rs.getString("op6"));
					op.setOp7(rs.getString("op7"));
					op.setOp8(rs.getString("op8"));
					op.setOp9(rs.getString("op9"));
					op.setOp10(rs.getString("op10"));
					
					for (int i=1; i<=10; i++){
						if (rs.getString("op"+i)!=null && !"".equals(rs.getString("op"+i))){
							list1.add(s1);
						} else {
							//为空不显示
							list1.add(s2);
						}
						list.add(rs.getString("op"+i));
					}
					
					
					op.setAnonymous(rs.getString("anonymous"));
					op.setOnlyonece(rs.getString("onlyonece"));
					op.setOpttype(rs.getString("opttype"));
					op.setEndtime(rs.getString("endtime"));
					
					if ("多选".equals(op.getOpttype())){
						inputtype="checkbox";
					}
					if("true".equals(op.getAnonymous())) {
						anonymous = "是";
					}
					if("true".equals(op.getOnlyonece())) {
						onlyonece = "是";
					}
	 			}
				
				
			/*	
			} else {
				modelAndView = new ModelAndView(_SAVE);
				msg = "对不起，投票截止时间已过!!";
				session.setAttribute("msg", msg);
				System.out.println("llove:"+bijiao);
				return modelAndView;
			}
			*/
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		
		//HttpSession session = request.getSession();
		session.setAttribute("list", list);
		session.setAttribute("list1", list1);
		session.setAttribute("bContinue", bContinue);
		session.setAttribute("bijiao", bijiao);
		
		modelAndView.addObject("uuid", uuid);// 传值
		modelAndView.addObject("op",op);// 传值
		modelAndView.addObject("inputtype",inputtype);// 传值
		modelAndView.addObject("anonymous",anonymous);// 传值
		modelAndView.addObject("onlyonece",onlyonece);// 传值
		modelAndView.addObject("endtime",op.getEndtime());// 传值
		
		return modelAndView;

	}
	

}
