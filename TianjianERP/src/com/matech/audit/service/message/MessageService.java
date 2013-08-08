package com.matech.audit.service.message;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.message.model.Message;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.StringUtil;



/**
 *	系统消息服务类 
 * 
 * @author METACH
 * @date 2012-03-06
 * 
 */
public class MessageService {
	/**
	 * 获取数据库链接
	 */
	private Connection conn = null;
	
	public MessageService(Connection conn){
		this.conn=conn;
	}
//	/**
//	 * 保存消息
//	 */
//	public void save(Message msg){
//		PreparedStatement ps = null;
//		try{
//			
//			String sql="insert into MT_COM_MESSAGE_INF(ID,msgtype,message,noticetype,noticearea,createtime,referto) "
//					 +"values(?,?,?,?,?,?,?)";
//			ps=conn.prepareStatement(sql);
//			
//			
//			String MsgId=msg.getID().replaceAll("\\s*|\t|\r|\n","");
//			if("".equals(MsgId)||MsgId==null){
//				MsgId=StringUtil.getUUID();
//			}
//			String createTime=msg.getCreatetime();
//			if(createTime==null||createTime.length()<8){
//				createTime= StringUtil.getCurDateTime();
//			}
//			ps.setString(1, MsgId);
//			ps.setString(2,msg.getMsgtype());
//			ps.setString(3, msg.getMessage());
//			ps.setString(4,msg.getNoticetype());
//			ps.setString(5,msg.getNoticearea());
//			
//			ps.setString(6,createTime);
//			ps.setString(7,msg.getReferto());
//			ps.execute();
//			
//		}catch(Exception e){
//			e.printStackTrace();
//		}finally{
//			DbUtil.close(ps);
//		}
//	}
	
	/**
	 * 更新消息 
	 */
	public void update(Message msg){		
	}
	
	/**
	 * 删除消息
	 */
	public void delete(Message msg){
		
	}
	
//	/**
//	 * 获取消息
//	 * 
//	 * @return MessageInfo
//	 */
//	public Message getMsgById(String Id){
//		Message msg=null;
//		
//		PreparedStatement ps=null;
//		ResultSet rs=null;
//		
//		try{
//			String sql="select m11.id,m11.msgtype,m11.message,m11.noticetype,m11.noticearea,m11.createtime,m11.referto from MT_COM_MESSAGE_INF m11 where m11.id=?";
//			ps=conn.prepareStatement(sql);
//			ps.setString(1, Id);
//			
//			rs=ps.executeQuery();
//			while(rs.next()){
//				msg=new Message();
//				msg.setID(rs.getString("id"));
//				msg.setMsgtype(rs.getString("msgtype"));
//				msg.setMessage(rs.getString("message"));
//				msg.setNoticetype(rs.getString("noticetype"));
//				msg.setNoticearea(rs.getString("noticearea"));
//				msg.setCreatetime(rs.getString("createtime"));
//				msg.setReferto(rs.getString("referto"));
//				break;
//			}
//			
//		}catch(Exception e){
//			e.printStackTrace();
//		}finally{
//			DbUtil.close(rs);
//			DbUtil.close(ps);
//		}
//		return msg;
//	}
	
//	/**
//	 * 获取消息
//	 * 
//	 * @param int  返回行数
//	 * @param String  排序语句
//	 */
//	public List<Message> getMsgs(int nums,UserSession userSession){
//		List<Message> msgs=new ArrayList<Message>();
//
//		String userId=userSession.getUserId();
//		
//		if(userId==null){
//			return null;
//		}
//		
//		PreparedStatement ps=null;
//		ResultSet rs=null;
//		
//		try{
//			String sql="select m11.id,m11.msgtype,m11.message,m11.noticetype,m11.noticearea,m11.createtime,m11.referto \n" +
//					   "from (select * from MT_COM_MESSAGE_INF \n" +
//					   "where  instr(noticearea,"+userId+")>0 or trim(noticearea) is null \n" +
//					   "order by createtime desc) m11 \n" +
//					   "where rownum<=?";
//			
//			System.out.println("从数据库获取数据:"+sql);
//			
//			ps=conn.prepareStatement(sql);
//			ps.setInt(1, nums);
//			
//			rs=ps.executeQuery();
//			while(rs.next()){
//				Message msg=new Message();
//				msg.setID(rs.getString("id"));
//				msg.setMsgtype(rs.getString("msgtype"));
//				msg.setMessage(rs.getString("message"));
//				msg.setNoticetype(rs.getString("noticetype"));
//				msg.setNoticearea(rs.getString("noticearea"));
//				msg.setCreatetime(rs.getString("createtime"));
//				msg.setReferto(rs.getString("referto"));
//				msgs.add(msg);
//			}
//			
//		}catch(Exception e){
//			e.printStackTrace();
//		}finally{
//			DbUtil.close(rs);
//			DbUtil.close(ps);
//		}		
//		return msgs;
//	}
//	/**
//	 * 获取消息
//	 * 
//	 * @param int  返回行数
//	 * @param String  排序语句
//	 */
//	public List<Message> getMsgs(){
//		List<Message> msgs=new ArrayList<Message>();
//
//		PreparedStatement ps=null;
//		ResultSet rs=null;
//		
//		try{
//			String sql="select m11.id,m11.msgtype,m11.message,m11.noticetype,m11.noticearea,m11.createtime,m11.referto \n" +
//					   "from MT_COM_MESSAGE_INF m11\n" +
//					   "order by createtime desc \n";
//			
//			ps=conn.prepareStatement(sql);
//			rs=ps.executeQuery();
//			while(rs.next()){
//				Message msg=new Message();
//				msg.setID(rs.getString("id"));
//				msg.setMsgtype(rs.getString("msgtype"));
//				msg.setMessage(rs.getString("message"));
//				msg.setNoticetype(rs.getString("noticetype"));
//				msg.setNoticearea(rs.getString("noticearea"));
//				msg.setCreatetime(rs.getString("createtime"));
//				msg.setReferto(rs.getString("referto"));
//				msgs.add(msg);
//			}
//			
//		}catch(Exception e){
//			e.printStackTrace();
//		}finally{
//			DbUtil.close(rs);
//			DbUtil.close(ps);
//		}		
//		return msgs;
//	}
	/**
	 * 更新消息已阅状态(点击已阅)
	 * @param message
	 * @param id
	 * @throws Exception
	 */
	public void updateIsRead(String userid) throws Exception {
		PreparedStatement ps = null;
		Connection conn = null;
		try {

			String sql = "update MT_COM_MESSAGE set ISREAD='1',READ_TIME=? where RECEIVE_USERID=? ";
			conn = new DBConnect().getConnect();
			ps = conn.prepareStatement(sql);
			ps.setString(1, StringUtil.getCurDateTime());
			ps.setString(2, userid);
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
			DbUtil.close(conn);
		}
		
	}
	/**
	 * 更新消息已阅状态
	 * @param message
	 * @param id
	 * @throws Exception
	 */
	public void updateIsRead2(String uuid) throws Exception {
		PreparedStatement ps = null;
		Connection conn = null;
		try {

			String sql = "update MT_COM_MESSAGE set ISREAD='1',READ_TIME=? where uuid=? ";
			conn = new DBConnect().getConnect();
			ps = conn.prepareStatement(sql);
			ps.setString(1, StringUtil.getCurDateTime());
			ps.setString(2, uuid);
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
			DbUtil.close(conn);
		}
		
	}
	
	
	
	/**
	 * 新增用户消息
	 * @param message
	 * @param id
	 * @throws Exception
	 */
	public void addMessage(Message message) throws Exception {
		PreparedStatement ps = null;
		Connection conn = null;
		try {

			String sql = "insert into MT_COM_MESSAGE(" +
					"uuid, message, msg_type, send_userid, send_time, " +
					"receive_userid, isread,  batch_id, msg_param, msg_title)" +
					"values(?,?,?,?,?   ,?,?,?,?,? ) ";
			conn = new DBConnect().getConnect();
			ps = conn.prepareStatement(sql);
			ps.setString(1, StringUtil.getUUID());
			ps.setString(2, message.getMessageContent());
			ps.setString(3, message.getMsgType());
			ps.setString(4, message.getSendUserid());
			ps.setString(5, StringUtil.getCurDateTime());
			
			ps.setString(6, message.getReceiveUserid());
			ps.setString(7, message.getIsRead());
			ps.setString(8, message.getBatchId());
			ps.setString(9, message.getMsgParam());
			ps.setString(10, message.getMsgTitle());
			
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
			DbUtil.close(conn);
		}
		
	}
	
	/**
	 * 取得用户未读消息列表
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public ArrayList getNoReadListByUserId(String userId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		PreparedStatement ps2 = null;
		ResultSet rs2 = null;
		Connection conn = null;
		ArrayList al = new ArrayList();
		String sql1 = "";
		String sql2 = "";
		String jsMethod = "";
		try {
			conn = new DBConnect().getConnect();
			sql1 = "select MSG_TYPE from MT_COM_MESSAGE a " 
					+ " left join MT_COM_USER b on a.RECEIVE_USERID=b.USER_ID"
					+ " where a.RECEIVE_USERID= ? "
					+ " and a.isread='0' "
					+ " order by a.SEND_TIME desc";
			
			ps = conn.prepareStatement(sql1);
			
			ps.setString(1, userId);
			rs = ps.executeQuery();
			while (rs.next()) {
				String msgType = rs.getString(1);
				if(null!=msgType&&!"".equals(msgType)){
					if (msgType.equals("0")) {//判断消息类型，普通短信
						jsMethod = "goSetup(''' || a.uuid || ''')";
					} else {//审批提醒短信
						jsMethod = "goSetup2(''' || MSG_PARAM || ''',''' || substr(message,4,INSTR(message, '】', 1, 1) - 4) || ''',''' || a.uuid || ''')";
					}
				}
				sql2 = "select uuid, '<a href=#  onclick=\""+jsMethod+"\">' || a.MESSAGE || '...' || '  【' || b.user_name || '】 </a>' as MESSAGE,MSG_PARAM "
						+ " from MT_COM_MESSAGE a" 
						+ " left join MT_COM_USER b on a.RECEIVE_USERID=b.USER_ID"
						+ " where a.RECEIVE_USERID= ? "
						+ " and a.isread='0' "
						+ " order by a.SEND_TIME desc";
				ps2 = conn.prepareStatement(sql2);
				ps2.setString(1, userId);
				rs2 = ps2.executeQuery();
				while (rs2.next()) {
						al.add(rs2.getString("MESSAGE"));
					}
				DbUtil.close(rs2);
				DbUtil.close(ps2);
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(conn);
		}
		return al;
	}
	public String getJSONTree(String type) {
		String result = null;
		try {
			if (type != null && type.trim().length() > 0) {
				if (type.startsWith("organization`")) { // 机构人员
					String parentId = type.split("`")[1];
					if ("0".equals(parentId)) { // 机构人员所属机构
						result = getOrGanizaTionTree();
					} else {// 科室
						result = getDepartMentTree(parentId);
					}
				}
				// 预算单位人员
				if (type.startsWith("bdgunit`")) {
					String parentId = type.split("`")[1];
					if ("0".equals(parentId)) {// 预算人员所属(区/镇街)
						result = getBdgOrganizationTree();
					} else {// 所属预算单位
						result = getBdgUnitTree(parentId);
					}
				}
				// 机构人员
				if (type.startsWith("departMent`")) {
					result = getUser(type, "false");
				}
				// 镇街预算人员
				if (type.startsWith("unit`")) {
					result = getUser(type, "false");
				}
				// 人员
				if ("0".equals(type)) {
					result = "[" + getOneFolder("organization`0", "机构人员") + ","
							+ getOneFolder("bdgunit`0", "预算单位人员") + "]";
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return result;
	}
	/**
	 * 获得机构类型树
	 * 
	 * @param parentId
	 * @return
	 */
	public String getOrGanizaTionTree() {
		PreparedStatement ps = null;
		ResultSet rs = null;

		StringBuilder sb = new StringBuilder();

		try {
			String sql = " SELECT orgid,orgname " 
					+ " FROM  mt_com_organization " + " "
					+ " order by ordercode desc,orgname desc  ";

			ps = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			rs = ps.executeQuery();

			String folderId;
			String folderName;

			sb.append("[");

			while (rs.next()) {
				folderId = rs.getString(1);
				folderName = rs.getString(2);

				sb.append(" {cls:'folder',checked:false,").append("leaf:false,").append("id:'")
						.append("organization`"+folderId).append("',")
						.append("text:'").append(folderName).append("'} ");
				if (!rs.isLast()) {
					sb.append(",");
				}
			}

			sb.append("]");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(conn);
		}

		return sb.toString();
	}
	/**
	 * 获取档案部门树
	 * 
	 * @param deptOrgid
	 * @return
	 */
	public String getDepartMentTree(String deptOrgid) {

		PreparedStatement ps = null;
		ResultSet rs = null;

		StringBuilder sb = new StringBuilder();

		try {

			String sql = "  select deptid, deptname, initflag, timeflag, ordercode from mt_com_dept where deptOrgid =? order by ordercode desc";

			ps = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			ps.setString(1, deptOrgid);
			rs = ps.executeQuery();

			String folderId;
			String folderName;

			sb.append("[");

			while (rs.next()) {
				folderId = rs.getString(1);
				folderName = rs.getString(2);

				sb.append(" {cls:'folder',checked:false,").append("leaf:false,").append("id:'")
						.append("departMent`"+deptOrgid+"`"+folderId).append("',").append("text:'").append(folderName)
						.append("'} ");
				if (!rs.isLast()) {
					sb.append(",");
				}
			}

			sb.append("]");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(conn);
		}

		return sb.toString();
	}
	/**
	 * 获得预算单位类型树
	 * 
	 * @return
	 */
	public String getBdgOrganizationTree() {
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuilder result = new StringBuilder();

		try {
			String sql = "select distinct areaname,orgid ,ordercode"
					+ " from MT_COM_ORGANIZATION  "
					+ " where areaname is not null "
					+ " order by ordercode desc,areaname desc ";
			ps = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			rs = ps.executeQuery();

			String folderId;
			String folderName;

			result.append("[");

			while (rs.next()) {
				folderName = rs.getString(1);
				folderId = rs.getString(2);

				result.append(" {cls:'folder',checked:false,").append("leaf:false,").append("id:'bdgunit`")
						.append(folderId).append("',")
						.append("text:'").append(folderName).append("'} ");
				if (!rs.isLast()) {
					result.append(",");
				}
			}

			result.append("]");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(conn);
		}

		return result.toString();
	}
	/**
	 * @param 预算单位所属机构id
	 * @return 预算单位人员所属预算单位树
	 */
	private String getBdgUnitTree(String orgid) {
		PreparedStatement ps = null;
		ResultSet rs = null;

		StringBuilder sb = new StringBuilder();

		try {

			String sql = "  select unit_id,unit_name from MT_BDG_UNIT t where org_id =?";

			ps = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			ps.setString(1, orgid);
			rs = ps.executeQuery();

			String folderId;
			String folderName;

			sb.append("[");

			while (rs.next()) {
				folderId = rs.getString(1);
				folderName = rs.getString(2);

				sb.append(" {cls:'folder',checked:false,").append("leaf:false,").append("id:'unit`").append(orgid)
						.append("`").append(folderId).append("',")
						.append("text:'").append(folderName).append("'} ");
				if (!rs.isLast()) {
					sb.append(",");
				}
			}

			sb.append("]");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(conn);
		}
		return sb.toString();
	}
	/**
	 * 获取一个树的节点
	 * 
	 * @param id
	 * @param text
	 * @param childer
	 * @return
	 */
	private String getOneFolder(String id, String text) {
		return new StringBuilder("{cls:'folder',checked:false,leaf:false,id:'").append(id).append("',text:'")
				.append(text).append("'}").toString();
	}
	//人员树
		public String getUser(String type,String checked)throws Exception{
			PreparedStatement ps = null;
			ResultSet rs = null;
			String andSql = "";
			String unitId = "";
			String depart = "";
			String sql = "";
			StringBuilder sb = new StringBuilder();
			String json = "";
			try {
				int ii = 0; //区域树无值，返回"";
				//人员树
				if (!"".equals(type) && type != null) {
					// 科室过滤
					if (type.indexOf("departMent`") > -1) {
						depart = type.split("`")[2];
						andSql = " and DEPARTMENTID is not null and DEPARTMENTID='" + depart + "'";
					}
					
					if (type.indexOf("unit`") > -1) {// 区/镇街预算人员
						unitId = type.split("`")[2];
						andSql = "and UNIT_ID is not null and UNIT_ID='" + unitId + "'";
						}
				} else {
					andSql = "";
				}
				sql = "select user_id,user_name from MT_COM_USER   where 1=1 "+andSql+"  order by login_id";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				String userid,username;
				sb.append("[");
				while(rs.next()){
					userid = rs.getString("user_id");
					username = rs.getString("user_name");
					sb.append(" {cls:'folder',checked:").append(checked).append(",").append("leaf:true,").append("id:'")
					.append(userid).append("',")
					.append("text:'").append(username).append("'} ");
					sb.append(",");
				 }
					json = sb.toString().substring(0, sb.toString().length()-1);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				DbUtil.close(rs);
				DbUtil.close(ps);
				DbUtil.close(conn);
			}
			return json+"]";
		}
		
		/**
		 * 获取信息详细内容
		 * @param uuid
		 * @return
		 */
		public Message getMessage(String uuid){
			PreparedStatement ps = null;
			ResultSet rs = null;
			Message message = new Message();
			String sql = "select uuid, message,msg_type, send_userid, send_time, " +
					"receive_userid, isread, read_time, batch_id, msg_param, " +
					"msg_title,b.user_name " +
					"from mt_com_message a " +
					"left join mt_com_user b on a.send_userid=b.user_id " +
					"where a.uuid='"+uuid+"'";
			try {
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				if (rs.next()) {
					message.setMsgTitle(rs.getString("msg_title"));
					message.setMsgType(rs.getString("msg_type"));
					message.setSendUserid(rs.getString("user_name"));
					message.setSendTime(rs.getString("send_time"));
					message.setMessageContent(rs.getString("message"));
				}
				updateIsRead(uuid);//更新已阅状态
			} catch (Exception e) {
				e.printStackTrace();
			} finally{
				DbUtil.close(rs);
				DbUtil.close(ps);
				DbUtil.close(conn);
			}
			return message;
		}
}
