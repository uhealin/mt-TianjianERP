package com.matech.audit.service.education;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.matech.audit.service.education.model.Education;
import com.matech.audit.service.education.model.EducationPO;
import com.matech.audit.service.education.model.Exam;
import com.matech.framework.pub.db.DbUtil;

public class EducationService {
	private Connection conn=null;
	
	public EducationService(Connection conn){
		this.conn=conn;
	}
	
	/**
	 * 增加网络培训班
	 * @param education
	 */  
	public void addNetEdu(Education education){
		
		PreparedStatement ps=null;
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		String nowTime=format.format(new Date());
		//String startTime=education.getRegistrationStartTime();
		//String endTime=education.getRegistrationEndTime();
		DateFormat df=new SimpleDateFormat("yyyy-MM-dd");
		try {
			//Date start=df.parse(startTime);
			//Date end=df.parse(endTime);
			Date now=df.parse(nowTime);
			String sql="insert into k_education (name,teacherId,registrationNum," 
						+"periodTime,cost,state," 
						+"classType,trainObject,address,content,arrangement," 
						+"attachment,link,uuid,courseType)"
						+" values(?,?,?, now(),?,?,?,?, ?,?,?,?,?, ?,?)";
			int i=1;
			ps=this.conn.prepareStatement(sql);
			ps.setString(i++, education.getName());
			ps.setString(i++, education.getTeacherId());
			ps.setString(i++, education.getRegistrationNum());
			
			ps.setString(i++, education.getPeriodTime());
			ps.setString(i++, education.getCost());

		
			
			ps.setString(i++, education.getClassType());
			ps.setString(i++, education.getTrainObject());
			ps.setString(i++, education.getAddress());
			ps.setString(i++, education.getContent());
			ps.setString(i++, education.getArrangement());
			
			ps.setString(i++, education.getAttachment());
			ps.setString(i++, education.getLink());
			ps.setString(i++, education.getUuid());
			ps.setString(i++, education.getCourseType());
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
	}
	
	
	/**
	 * 增加现场下培训班
	 * @param education
	 */
	public void addLocaleEdu(Education education){
		PreparedStatement ps=null;
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		String nowTime=format.format(new Date());
		String startTime=education.getRegistrationStartTime();
		String endTime=education.getRegistrationEndTime();
		DateFormat df=new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date start=df.parse(startTime);
			Date end=df.parse(endTime);
			Date now=df.parse(nowTime);
			String sql="insert into k_education (name,trainStartTime,trainEndTime,teacherId,registrationNum," 
						+"periodTime,cost,registrationStartTime,registrationEndTime,state," 
						+"classType,trainObject,address,content,arrangement," 
						+"attachment,link,uuid,courseType)"
						+" values(?,?,?,?,?, now(),?,?,?,?, ?,?,?,?,?, ?,?,?,?)";
			int i=1;
			ps=this.conn.prepareStatement(sql);
			ps.setString(i++, education.getName());
			ps.setString(i++, education.getTrainStartTime());
			ps.setString(i++, education.getTrainEndTime());
			ps.setString(i++, education.getTeacherId());
			ps.setString(i++, education.getRegistrationNum());
			
			//ps.setString(i++, education.getPeriodTime());
			ps.setString(i++, education.getCost());
			ps.setString(i++, education.getRegistrationStartTime());
			ps.setString(i++, education.getRegistrationEndTime());
			if(now.getTime()<start.getTime()){
				ps.setString(i++, "1");
			}else if(start.getTime()<=now.getTime() && now.getTime()<=end.getTime()){
				ps.setString(i++, "2");
			}else{
				ps.setString(i++, "3");
			}
			
			ps.setString(i++, education.getClassType());
			ps.setString(i++, education.getTrainObject());
			ps.setString(i++, education.getAddress());
			ps.setString(i++, education.getContent());
			ps.setString(i++, education.getArrangement());
			
			ps.setString(i++, education.getAttachment());
			ps.setString(i++, education.getLink());
			ps.setString(i++, education.getUuid());
			ps.setString(i++, education.getCourseType());
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
	}
	
	/*
	 * 根据id查找培训班
	 */
	public Education findById(int id){
		PreparedStatement ps=null;
		ResultSet rs=null;
		Education education=new Education();
		try {
			String sql="select * from k_education where id="+id+"";
			ps=conn.prepareStatement(sql);
			rs=ps.executeQuery();
			if(rs.next()){
				int i=1;
				
				education.setId(Integer.valueOf(rs.getString(i++)));
				education.setName(rs.getString(i++));
				education.setTrainStartTime(rs.getString(i++));
				education.setTrainEndTime(rs.getString(i++));
				education.setTeacherId(rs.getString(i++));
				
				education.setRegistrationNum(rs.getString(i++));
				education.setPeriodTime(rs.getString(i++));
				education.setCost(rs.getString(i++));
				education.setRegistrationStartTime(rs.getString(i++));
				education.setRegistrationEndTime(rs.getString(i++));
				
				education.setState(rs.getString(i++));
				education.setClassType(rs.getString(i++));
				education.setTrainObject(rs.getString(i++));
				education.setAddress(rs.getString(i++));
				education.setContent(rs.getString(i++));
				
				education.setArrangement(rs.getString(i++));
				education.setAttachment(rs.getString(i++));
				education.setLink(rs.getString(i++));
				education.setUuid(rs.getString("uuid"));
				education.setCourseType(rs.getString("courseType"));
				//education.setCourseware(rs.getString("courseware"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		return education;
	}
	
	/*
	 * 更新培训班资料
	 */
	public void update(Education education){
		PreparedStatement ps=null;
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		String nowTime=format.format(new Date());
		String startTime=education.getRegistrationStartTime();
		String endTime=education.getRegistrationEndTime();
		DateFormat df=new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date start=df.parse(startTime);
			Date end=df.parse(endTime);
			Date now=df.parse(nowTime);
			String state=null;
			if(now.getTime()<start.getTime()){
				state="1";
			}else if(start.getTime()<=now.getTime() && now.getTime()<=end.getTime()){
				state="2";
			}else{
				state="3";
			}
			String sql="update k_education set name='"+education.getName()+"',trainStartTime='"+education.getTrainStartTime()
						+"',trainEndTime='"+education.getTrainEndTime()
						+"',teacherId='"+education.getTeacherId()+"',registrationNum='"+education.getRegistrationNum()
						+"',periodTime='"+education.getPeriodTime()
						+"',cost='"+education.getCost()+"',registrationStartTime='"+education.getRegistrationStartTime()
						+"',registrationEndTime='"+education.getRegistrationEndTime()+"',state='"+state
						+"',classType='"+education.getClassType()+"',trainObject='"+education.getTrainObject()
						+"',address='"+education.getAddress()+"',content='"+education.getContent()
						+"',arrangement='"+education.getArrangement()+"',attachment='"+education.getAttachment()
						+"',link='"+education.getLink()
						+"',courseType='"+education.getCourseType()+"' where id="+education.getId();
			ps=this.conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
	}
	
	/*
	 * 删除选中班级数据
	 */
	public void del(int id){
		PreparedStatement ps=null;
		try {
			String sql="delete from k_education where id="+id+"";
			ps=this.conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
	}
	
	/*
	 * 报名培训班
	 */
	public String reg(int educationId,int userId){
		PreparedStatement ps=null;
		ResultSet rs=null;
		String exist="fail";
		try {
			//查看是否重复报名
			String searchSql="select * from k_educationregdetail where userid="+userId+" and educationid="+educationId;
			ps=this.conn.prepareStatement(searchSql);
			rs=ps.executeQuery();
			if(rs.next()){
				return exist;
			}
			//查看是否满人
			Education ec=new Education();
			ec=findById(educationId);
			int maxNum=Integer.valueOf(ec.getRegistrationNum());
			String countSql="SELECT COUNT(*) FROM k_educationregdetail WHERE educationid="+educationId;
			ps=this.conn.prepareStatement(countSql);
			rs=ps.executeQuery();
			int num=0;
			if(rs.next()){
				num=Integer.valueOf(rs.getString(1));
			}
			if(num>=maxNum){
				exist="full";
				return exist;
			}else{
				String sql="insert into k_educationregdetail set educationid ="+educationId+",userid="+userId+",time =now() ";
				ps=this.conn.prepareStatement(sql);
				ps.execute();
				//以下是检查报名后，培训班报名了多少人，如果到了报名人数上限，就更新培训班的状态
				//查找出培训班报名人数的上限是多少
				String fullSql="select registrationNum from k_education where id="+educationId;
				ps=this.conn.prepareStatement(fullSql);
				rs=ps.executeQuery();
				int count=0;
				if(rs.next()){
					count=Integer.valueOf(rs.getString(1));				
				}
				//查看现在报名了多少人
				ps=this.conn.prepareStatement(countSql);
				rs=ps.executeQuery();
				int countNum=0;
				if(rs.next()){
					countNum=Integer.valueOf(rs.getString(1));
					//如果报名人数大于等于上限，就更新状态
					if(countNum>=count){
						String updateSql="update k_education set state='4' where id="+educationId;
						ps=this.conn.prepareStatement(updateSql);
						ps.execute();
					}
				}
				exist="success";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		return exist;
	}
	
	/*
	 * 根据班号，查找评价的平均分
	 */
	public double getAvg(int educationId){
		double score=0;
		PreparedStatement ps=null;
		ResultSet rs=null;
		try {
			String sql="SELECT AVG(votevalue) FROM k_evaluate v INNER JOIN k_education t ON v.educationid = t.id WHERE t.id="+educationId;
			ps=this.conn.prepareStatement(sql);
			rs=ps.executeQuery();
			if(rs.next()){
		        String stringscore = rs.getString(1);
		        if(stringscore!=null){
		        	score=Double.valueOf(rs.getString(1));
		        }		
		     }
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		return score;
	}
	
	/*
	 * 判断重复报名
	 */
	public String isReg(int educationId,int userId){
		PreparedStatement ps=null;
		ResultSet rs=null;
		String exist="fail";
		try {
			String searchSql="select * from k_educationregdetail where userid="+userId+" and educationid="+educationId;
			ps=this.conn.prepareStatement(searchSql);
			rs=ps.executeQuery();
			if(rs.next()){
				return exist;
			}else{
				exist="success";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		return exist;
	}
	
	/*
	 * 查找培训班对应的状态
	 */
	public String findState(String id){
		String state=null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		try {
			String sql="select stateType from k_educationstate where id="+id;
			ps=this.conn.prepareStatement(sql);
			rs=ps.executeQuery();
			if(rs.next()){
				state=rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
		return state;
	}
	/*
	 * 增加考试记录
	 */
	public void addExam(Exam exam,String userId){
		PreparedStatement ps=null;
		try {
			String sql="insert into k_exam (examType,examSubject,registrationStartTime,registrationEndTime,qualifications,examTime,remark,createuser,createtime) "
						+" values(?,?,?,?,?, ?,?,?,now())";
			int i=1;
			ps=this.conn.prepareStatement(sql);
			ps.setString(i++, exam.getExamType());
			ps.setString(i++, exam.getExamSubject());
			ps.setString(i++, exam.getRegistrationStartTime());
			ps.setString(i++, exam.getRegistrationEndTime());
			ps.setString(i++, exam.getQualifications());
			
			ps.setString(i++, exam.getExamTime());
			ps.setString(i++, exam.getRemark());
			ps.setString(i++, userId);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
	}
	/*
	 * 更新考试记录
	 */
	public void updateExam(Exam exam){
		PreparedStatement ps=null;
		try {
			String sql="update k_exam set examType='"+exam.getExamType()
						+"',examSubject='"+exam.getExamSubject()
						+"',registrationStartTime='"+exam.getRegistrationStartTime()
						+"',registrationEndTime='"+exam.getRegistrationEndTime()
						+"',qualifications='"+exam.getQualifications()
						+"',examTime='"+exam.getExamTime()
						+"',remark='"+exam.getRemark()
						+"' where id="+exam.getId();
			ps=this.conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
	}
	/*
	 * 删除考试记录
	 */
	public void delExam(String id){
		PreparedStatement ps=null;
		try {
			String sql="delete from k_exam where id="+id;
			ps=this.conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
	}
	/*
	 * 根据id查找考试记录
	 */
	public Exam getExamById(String id){
		Exam exam=null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		try {
			String sql="select * from k_exam where id="+id;
			ps=this.conn.prepareStatement(sql);
			rs=ps.executeQuery();
			if(rs.next()){
				exam=new Exam();
				exam.setId(rs.getString("id"));
				exam.setExamType(rs.getString("examType"));
				exam.setExamSubject(rs.getString("examSubject"));
				exam.setRegistrationStartTime(rs.getString("registrationStartTime"));
				exam.setRegistrationEndTime(rs.getString("registrationEndTime"));
				exam.setQualifications(rs.getString("qualifications"));
				exam.setExamTime(rs.getString("examTime"));
				exam.setRemark(rs.getString("remark"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return exam;
	}
	/*
	 * 判断考试重复报名
	 */
	public String isRegExam(String examRegId,String userId){
		PreparedStatement ps=null;
		ResultSet rs=null;
		String exist="fail";
		try {
			String searchSql="select * from k_examreg where userid="+userId+" and examid="+examRegId;
			ps=this.conn.prepareStatement(searchSql);
			rs=ps.executeQuery();
			if(rs.next()){
				return exist;
			}else{
				exist="success";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		return exist;
	}
	/*
	 * 考试报名
	 */
	public void examReg(String userId,String examId){
		PreparedStatement ps=null;
		PreparedStatement ps1=null;
		PreparedStatement ps2=null;
		ResultSet rs1=null;
		try {
			String sql="insert into k_examreg (userid,examid,registrationtime,state) "
						+"values(?,?,now(),?)";
			ps=this.conn.prepareStatement(sql);
			int i=1;
			ps.setString(i++, userId);
			ps.setString(i++, examId);
			ps.setString(i++, "已报名未填成绩");
			ps.execute();
			
			int maxReg=0;
			String searchSql="SELECT MAX(countreg) FROM k_exam where id="+examId;
			ps1=this.conn.prepareStatement(searchSql);
			rs1=ps1.executeQuery();
			if(rs1.next()){
				maxReg=Integer.valueOf(rs1.getString(1));
			}
			
			String updateSql="update k_exam set countreg= "+maxReg+1+" where id="+examId;
			ps2=this.conn.prepareStatement(updateSql);
			ps2.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(rs1);
			DbUtil.close(ps);
			DbUtil.close(ps1);
			DbUtil.close(ps2);
		}
	}
	/**
	 * 对培训班中的人进行评价
	 * @param uuids
	 * @param state
	 * @param educationId
	 * @return
	 */
	public EducationPO queryHui(String uuid){
		
		PreparedStatement ps=null;
		
		ResultSet rs=null;
		
		EducationPO education=null;
		
		String sql="SELECT a.userid,u.name,d.departname,e.name as ename,i.name as iname,e.registrationStartTime, "+
					" registrationEndTime,f.applyCount,e.registrationNum,s.stateType,kc.type,e.trainObject FROM  k_educationregdetail a "+
					" LEFT JOIN k_user u ON u.id = a.userid "+
					" LEFT JOIN k_education e ON e.id = a.educationId "+
					" INNER JOIN k_department d ON d.autoid = u.departmentid " +
					" LEFT JOIN k_classtype kc on kc.id=e.classtype"+
					" LEFT JOIN (SELECT COUNT(*) AS applyCount,educationId FROM k_educationregdetail GROUP BY educationId) f ON e.id = f.educationId "+
					" LEFT JOIN k_educationstate s ON s.id = e.state LEFT JOIN k_dic i ON i.autoid = e.courseType where a.id=?";
		try{
			ps=conn.prepareStatement(sql);
			ps.setString(1,uuid);
			rs=ps.executeQuery();
			while(rs.next()){
				education=new EducationPO();
				education.setName(rs.getString("name"));
				education.setClassName(rs.getString("ename"));
				education.setDepartmentName(rs.getString("departname"));
				education.setCourseType(rs.getString("iname"));
				education.setRegisterStartTime(rs.getString("registrationStartTime"));
				education.setRegisterEndTime(rs.getString("registrationEndTime"));
				education.setLimitNum(rs.getString("registrationNum"));
				education.setPeiType(rs.getString("type"));
				education.setState(rs.getString("stateType"));
				education.setTrainObject(rs.getString("trainObject"));
				education.setRegistNum(rs.getString("applyCount"));
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
			DbUtil.close(rs);
		}
		
		return education;
		
	}
}
