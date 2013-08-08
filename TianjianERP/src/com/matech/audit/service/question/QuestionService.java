package com.matech.audit.service.question;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.matech.framework.pub.db.DbUtil;
import com.matech.audit.service.question.model.Answer;
import com.matech.audit.service.question.model.Question;
import com.matech.audit.service.question.model.TreeNode;

public class QuestionService {
	
	public final static String TYPE_POLICY = "policy";
	
	public final static String TYPE_CASES = "cases";
	
	public final static String TYPE_COURSE = "course";
	
	public final static String TYPE_OTHER = "other";
	

	private Connection conn = null;

	public QuestionService(Connection conn) {
		this.conn = conn;
	}

	public List getTree(String id, String type, String mine) {

		PreparedStatement ps = null;
		ResultSet rs = null;
		List treeList = new ArrayList();
		try {
			if (TYPE_POLICY.equals(type)) {
				// 展开法律法规树
				String sql = "select distinct a.* from p_policytypeSZX a,( \n"
						+ " 	select a.*,b.userId from ( \n"
						+ " 		select a.id as policyId,b.*  from p_policySZX a,(  \n"
						+ "  		select b.* from p_policytypeSZX a "
						+ "   		inner join  \n"
						+ "   		p_policytypeSZX b on b.fullPath like a.fullPath+'%' \n"
						+ "			where a.parentid = '"
						+ id
						+ "' "
						+ "	 	) b where a.typeid= b .id  \n"
						+ "	) a,p_questionSZX b where a.policyId = b.typeid  \n"
						+ ") b where b.fullPath like a.fullPath+'%' and a.parentid = '"
						+ id + "'   \n";
				if (!"".equals(mine)) {
					sql += "and userId='" + mine + "'";
				}
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while (rs.next()) {
					TreeNode policyTree = new TreeNode();
					policyTree.setId(rs.getString(1));
					policyTree.setText(rs.getString(2));
					policyTree.setValue("policy");
					policyTree.setShowcheck(false);
					String isLeaf = rs.getString(4);
					policyTree.setFullPath(rs.getString(5));
					if ("0".equals(isLeaf)) {
						policyTree.setHasChildren(true);
					} else {
						policyTree.setHasChildren(false);
					}
					treeList.add(policyTree);
				}
			} else if (TYPE_CASES.equals(type)) {
				// 展开案例库树
				String sql = "select distinct a.* from p_casesTypeSZX a,( \n"
						+ " 	select a.*,b.userId from ( \n"
						+ " 		select a.id as caseId,b.*  from p_cases a,(  \n"
						+ "  		select b.* from p_casesTypeSZX a "
						+ "   		inner join  \n"
						+ "   		p_casesTypeSZX b on b.fullPath like a.fullPath+'%' \n"
						+ "			where a.parentid = '"
						+ id
						+ "' "
						+ "	 	) b where a.typeid= b.id  \n"
						+ "	) a,p_questionSZX b where a.caseId = b.typeid  \n"
						+ ") b where b.fullPath like a.fullPath+'%' and a.parentid = '"
						+ id + "'   \n";
				if (!"".equals(mine)) {
					sql += "and userId='" + mine + "'";
				}
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while (rs.next()) {
					TreeNode caseTree = new TreeNode();
					caseTree.setId(rs.getString(1));
					caseTree.setText(rs.getString(2));
					caseTree.setValue("cases");
					caseTree.setShowcheck(false);
					String isLeaf = rs.getString(4).trim();
					if ("0".equals(isLeaf)) {
						caseTree.setHasChildren(true);
					} else {
						caseTree.setHasChildren(false);
					}
					treeList.add(caseTree);
				}

			} else if (TYPE_COURSE.equals(type)) {
				// 展开课程树
			} else if (TYPE_OTHER.equals(type)) {

			} else {
				// type为空，代表只显示法律法规，案例，课程 3个结点

				TreeNode policyTree = new TreeNode();
				policyTree.setId("00");
				policyTree.setText("法律法规问题");
				policyTree.setValue("policy");
				policyTree.setShowcheck(false);
				policyTree.setHasChildren(true);
				treeList.add(policyTree);

				TreeNode casesTree = new TreeNode();
				casesTree.setId("01");
				casesTree.setText("案例问题");
				casesTree.setValue("cases");
				casesTree.setShowcheck(false);
				casesTree.setHasChildren(true);
				treeList.add(casesTree);

				TreeNode courseTree = new TreeNode();
				courseTree.setId("02");
				courseTree.setText("课程问题");
				courseTree.setValue("course");
				courseTree.setShowcheck(false);
				courseTree.setHasChildren(true);
				treeList.add(courseTree);

				TreeNode otherTree = new TreeNode();
				otherTree.setId("03");
				otherTree.setText("其它问题");
				otherTree.setValue("other");
				otherTree.setShowcheck(false);
				otherTree.setHasChildren(true);
				treeList.add(otherTree);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);

		}
		return treeList;
	}
	
	public List<Map<String,String>> getTypeList(String ctype) throws Exception {
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		Map<String,String> map = null;
		
		String tableName = "";
		
		if (TYPE_POLICY.equals(ctype)) {
			tableName = "p_policyTypeSZX";
		} else if(TYPE_CASES.equals(ctype)) {
			tableName = "p_casesTypeSZX";
		} 
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			String sql = " select a.id,a.typeName "
						+ " from " + tableName +" a where id in( "
						+ " 	select typeId "
						+ " 	from p_questionSZX "
						+ " 	where ctype='" + ctype + "' "
						+ " ) ";
			
			ps = conn.prepareStatement(sql);
			
			rs = ps.executeQuery();
			
			while(rs.next()) {
				map = new HashMap<String, String>();
				map.put("id", rs.getString(1));
				map.put("typeName", rs.getString(2));
				
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return list;
	}
	
	/**
	 * 判断是否已经回答过问题
	 * @param questionId
	 * @param loginId
	 * @return
	 * @throws Exception
	 */
	public boolean isAnswer(String questionId, String loginId) throws Exception {

		String sql = " select autoid from p_answer where questionId=? and userId=? ";
		Object[] args = new Object[]{
				questionId,
				loginId
		};
			
		return new DbUtil(conn).queryForString(sql, args) != null;
	}

	public Question getQuestion(String id) {

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select a.id,a.ctype,typeId,title,question,a.userId,createDate,a.state,fullPath,rewardMark,b.name,explan,explanDate "
					+ " from p_questionSZX a left join k_user b on a.userId=b.id  where a.id=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, id);
			rs = ps.executeQuery();
			Question question = new Question();
			if (rs.next()) {
				question.setId(rs.getString(1));
				question.setCtype(rs.getString(2)) ;
				question.setTypeId(rs.getString(3)) ;
				question.setTitle(rs.getString(4));
				question.setQuestion(rs.getString(5));
				question.setUserId(rs.getString(6));
				question.setCreateDate(rs.getString(7));
				question.setState(rs.getString(8));
				question.setFullPath(rs.getString(9));
				question.setRewardMark(rs.getString(10));
				question.setUserName(rs.getString(11));
				question.setExplan(rs.getString(12));
				question.setExplanDate(rs.getString(13));

			}
			return question;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);

		}
		return null;
	}

	// 获得所有子结点的id
	public String getPolicyTypeIds(String fullPath) {

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select distinct a.id from p_policySZX a,("
					+ "	select b.* from p_policytypeSZX a " + "	inner join "
					+ "	p_policytypeSZX b on b.fullPath like a.fullPath+'%' "
					+ "	where b.fullPath = '" + fullPath + "'"
					+ ") b where a.typeid= b .id ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			String typeIds = "";
			while (rs.next()) {
				typeIds += rs.getString(1) + ",";
			}
			if (typeIds.length() > 1) {
				typeIds = typeIds.substring(0, typeIds.length() - 1);
			}
			return typeIds;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);

		}
		return "";
	}

	// 获得所有子结点的id
	public String getCaseTypeIds(String fullPath) {

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select distinct a.id from p_cases a,("
					+ "	select b.* from p_casestypeSZX a " + "	inner join "
					+ "	p_casestypeSZX b on b.fullPath like a.fullPath+'%' "
					+ "	where b.fullPath = '" + fullPath + "'"
					+ ") b where a.casestype= b.id ";
			System.out.println("##" + sql);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			String typeIds = "";
			while (rs.next()) {
				typeIds += rs.getString(1) + ",";
			}
			if (typeIds.length() > 1) {
				typeIds = typeIds.substring(0, typeIds.length() - 1);
			}
			return typeIds;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);

		}
		return "";
	}

	public String add(Question question) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String id = UUID.randomUUID().toString();
		try {
//			String sql = "insert into p_question(id,ctype,typeId,title,question,userId,createDate,state,fullPath,rewardMark) values(?,?,?,?, ?,?,?,? ,?,?) select scope_identity()";
			String sql = "insert into p_questionSZX(id,ctype,typeId,title,question,userId,createDate,state,fullPath,rewardMark) values(?,?,?,?, ?,?,?,? ,?,?) ";
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, id);
			ps.setString(i++, question.getCtype());
			ps.setString(i++, question.getTypeId());
			ps.setString(i++, question.getTitle());
			ps.setString(i++, question.getQuestion());
			ps.setString(i++, question.getUserId());
			ps.setString(i++, question.getCreateDate());
			ps.setString(i++, question.getState());
			ps.setString(i++, question.getFullPath());
			ps.setString(i++, question.getRewardMark());
			ps.execute();
//			ps.getMoreResults();
//			rs = ps.getResultSet();
//			if (rs.next()) {
//				// 获得自增主键
//				int autoIncreamtId = rs.getInt(1);
//				return autoIncreamtId;
//			}
			return id;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
		return id;
	}

	public int updateQuestion(Question question) {
		PreparedStatement ps = null;
		try {
			String sql = "update p_questionSZX "
						+ " set ctype=?,typeId=?,title=?,question=?,userId=?,createDate=?,state=?,fullPath=?,rewardMark=?,explan=?,explanDate=? "
						+ " where id=? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, question.getCtype());
			ps.setString(2, question.getTypeId());
			ps.setString(3, question.getTitle());
			ps.setString(4, question.getQuestion());
			ps.setString(5, question.getUserId());
			ps.setString(6, question.getCreateDate());
			ps.setString(7, question.getState());
			ps.setString(8, question.getFullPath());
			ps.setString(9, question.getRewardMark());
			ps.setString(10, question.getExplan());
			ps.setString(11, question.getExplanDate());

			ps.setString(12, question.getId());

			ps.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
		return 0;
	}

	public void add(Answer answer) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			//String sql = "insert into p_answer(questionId,answer,userId,answerDate,isBest) values(?,?,?,?, ?) select scope_identity()";
//			String sql = "insert into p_answer(autoid,questionId,answer,userId,answerDate,isBest) values(?,?,?,?,?, ?)";
			String sql = "insert into p_answer(questionId,answer,userId,answerDate,isBest) values(?,?,?,?, ?)";
			ps = conn.prepareStatement(sql);
//			ps.setString(1, UUID.randomUUID().toString());
//			ps.setString(2, answer.getQuestionId());
//			ps.setString(3, answer.getAnswer());
//			ps.setString(4, answer.getUserId());
//			ps.setString(5, answer.getDate());
//			ps.setString(6, answer.getIsBest());
			ps.setString(1, answer.getQuestionId());
			ps.setString(2, answer.getAnswer());
			ps.setString(3, answer.getUserId());
			ps.setString(4, answer.getDate());
			ps.setString(5, answer.getIsBest());
			ps.execute();
//			ps.getMoreResults();
//			rs = ps.getResultSet();
//			if (rs.next()) {
//				// 获得自增主键
//				int autoIncreamtId = rs.getInt(1);
//				return autoIncreamtId;
//			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}

	public List<Question> getQuestions(String type, String typeId) {

		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Question> list = new ArrayList<Question>();
		try {
//			String sql = "select top 5 id,title,question,userId,createDate,state,fullPath from p_questionSZX where ctype=? and typeId=? order by createDate desc,id desc ";
			String sql = "select id,title,question,userId,createDate,state,fullPath from p_questionSZX where ctype=? and typeId=? order by createDate desc,id desc limit 0,5";
			ps = conn.prepareStatement(sql);
			ps.setString(1, type);
			ps.setString(2, typeId);
			rs = ps.executeQuery();

			while (rs.next()) {
				Question question = new Question();
				question.setId(rs.getString(1));
				question.setTitle(rs.getString(2));
				question.setQuestion(rs.getString(3));
				question.setUserId(rs.getString(4));
				question.setCreateDate(rs.getString(5));
				question.setState(rs.getString(6));
				question.setFullPath(rs.getString(7));
				list.add(question);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);

		}
		return null;
	}

	public List<Answer> getAnswers(String questionId) {

		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Answer> list = new ArrayList<Answer>();
		try {
			String sql = "select autoid,questionId,answer,a.userId,answerDate,isBest,b.name from p_answer a"
					+ " left join k_user b on a.userId = b.id "
					+ " where questionId=? and (isBest<>'1' or isBest is null) order by answerDate desc,autoid desc";
			ps = conn.prepareStatement(sql);
			ps.setString(1, questionId);
			rs = ps.executeQuery();

			while (rs.next()) {
				Answer answer = new Answer();
				answer.setId(rs.getString(1));
				answer.setQuestionId(rs.getString(2));
				answer.setAnswer(rs.getString(3));
				answer.setUserId(rs.getString(4));
				answer.setDate(rs.getString(5));
				answer.setIsBest(rs.getString(6));
				answer.setUserName(rs.getString(7));
				list.add(answer);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);

		}
		return null;
	}

	/**
	 * 获取回答
	 * 
	 * @param answerId
	 * @return
	 */
	public Answer getAnswer(String answerId) {

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select autoid,questionId,answer,userId,answerDate,isBest from p_answer where autoid=? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, answerId);
			rs = ps.executeQuery();
			Answer answer = new Answer();
			if (rs.next()) {
				answer.setId(rs.getString(1));
				answer.setQuestionId(rs.getString(2));
				answer.setAnswer(rs.getString(3));
				answer.setUserId(rs.getString(4));
				answer.setDate(rs.getString(5));
				answer.setIsBest(rs.getString(6));
				return answer;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);

		}
		return null;
	}

	public Answer getBestAnswer(String questionId) {

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select a.autoid,questionId,answer,a.userId,answerDate,isBest,b.name from p_answer a"
					+ " left join k_user b on a.userId = b.id "
					+ " where questionId=? and isBest='1' "
					+ " order by answerDate desc,autoid desc";
			ps = conn.prepareStatement(sql);
			ps.setString(1, questionId);
			rs = ps.executeQuery();
			Answer answer = new Answer();
			if (rs.next()) {
				answer.setId(rs.getString(1));
				answer.setQuestionId(rs.getString(2));
				answer.setAnswer(rs.getString(3));
				answer.setUserId(rs.getString(4));
				answer.setDate(rs.getString(5));
				answer.setIsBest(rs.getString(6));
				answer.setUserName(rs.getString(7)) ;
				return answer;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);

		}
		return null;
	}

	/**
	 * 更新回答
	 * 
	 * @param answer
	 * @return
	 */
	public int updateAnswer(Answer answer) {

		PreparedStatement ps = null;

		try {
			String sql = "update p_answer set answer=?,userId=?,answerDate=?,isBest=? where autoid=? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, answer.getAnswer());
			ps.setString(2, answer.getUserId());
			ps.setString(3, answer.getDate());
			ps.setString(4, answer.getIsBest());
			ps.setString(5, answer.getId());

			return ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);

		}
		return 0;
	}

	public void updateFullPath(String questionId, String fullPath) {

		PreparedStatement ps = null;
		try {
			String sql = "update p_questionSZX set fullPath=? where id=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, fullPath);
			ps.setString(2, questionId);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}

}
