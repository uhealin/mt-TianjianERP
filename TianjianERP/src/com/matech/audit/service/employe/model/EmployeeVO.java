package com.matech.audit.service.employe.model;

import com.matech.framework.pub.db.Table;

@Table(name="hr_employee_register",pk="uuid")
public class EmployeeVO {
	
	 protected String uuid ;
	 protected String employeeId ;
	 protected String departmentId ;
	 protected String name ;
	 protected String sex ;
	 protected String nation ;
	 protected String brithday ;
	 protected String marriage ;
	 protected String idcard ;
	 protected String brith_address ;
	 protected String natives ;
	 protected String parents_address ;
	 protected String wrok_time ;
	 protected String assume_office_time ;
	 protected String contract_date ;
	 protected String grad_formal ;
	 protected String grad_degrees ;
	 protected String t_grad_degrees ;
	 protected String two_grad_degrees ;
	 protected String cet_grade ;
	 protected String two_foreign_language ;
	 protected String academic_title ;
	 protected String academic_title_time ;
	 protected String politics ;
	 protected String politics_time ;
	 protected String h_k_address ;
	 protected String h_k_move ;
	 protected String now_address ;
	 protected String conn_mobile ;
	 protected String conn_phone ;
	 protected String exam_course1 ;
	 protected String exam_course2 ;
	 protected String exam_course3 ;
	 protected String exam_course4 ;
	 protected String exam_course5 ;
	 protected String exam_time1 ;
	 protected String exam_time2 ;
	 protected String exam_time3 ;
	 protected String exam_time4 ;
	 protected String exam_time5 ;
	 protected String acquire_academic_title1 ;
	 protected String acquire_academic_title2 ;
	 protected String acquire_academic_title3 ;
	 protected String acquire_academic_title4 ;
	 protected String acquire_academic_title5 ;
	 protected String register_time1 ;
	 protected String register_time2 ;
	 protected String register_time3 ;
	 protected String register_time4 ;
	 protected String register_time5 ;
	 protected String study_start_time1 ;
	 protected String study_start_time2 ;
	 protected String study_start_time3 ;
	 protected String study_start_time4 ;
	 protected String study_end_time1 ;
	 protected String study_end_time2 ;
	 protected String study_end_time3 ;
	 protected String study_end_time4 ;
	 protected String grad_school1 ;
	 protected String grad_school2 ;
	 protected String grad_school3 ;
	 protected String grad_school4 ;
	 protected String grad_major1 ;
	 protected String grad_major2 ;
	 protected String grad_major3 ;
	 protected String grad_major4 ;
	 protected String work_strart_time1 ;
	 protected String work_strart_time2 ;
	 protected String work_strart_time3 ;
	 protected String work_strart_time4 ;
	 protected String work_end_time1 ;
	 protected String work_end_time2 ;
	 protected String work_end_time3 ;
	 protected String work_end_time4 ;
	 protected String work_unit1 ;
	 protected String work_unit2 ;
	 protected String work_unit3 ;
	 protected String work_unit4 ;
	 protected String work_job1 ;
	 protected String work_job2 ;
	 protected String work_job3 ;
	 protected String work_job4 ;
	 protected String family_name1 ;
	 protected String family_name2 ;
	 protected String family_name3 ;
	 protected String society_name1 ;
	 protected String society_name2 ;
	 protected String relation1 ;
	 protected String relation2 ;
	 protected String relation3 ;
	 protected String relation4 ;
	 protected String relation5 ;
	 protected String f_work_until1 ;
	 protected String f_work_until2 ;
	 protected String f_work_until3 ;
	 protected String f_work_until4 ;
	 protected String f_work_until5 ;
	 protected String f_work_job1 ;
	 protected String f_work_job2 ;
	 protected String f_work_job3 ;
	 protected String f_work_job4 ;
	 protected String f_work_job5 ;
	 protected String f_phone1 ;
	 protected String f_phone2 ;
	 protected String f_phone3 ;
	 protected String f_phone4 ;
	 protected String f_phone5 ;
	 protected String remarks ;
	 protected String write_date ;
	 protected String type ;
	 protected String isneed ;
	 protected String rlb_opinion ;
	 protected String zg_opinion ;
	 protected String profession_from_prac ;


	 public String getUuid(){ return this.uuid; }
	 public void setUuid(String uuid){ this.uuid=uuid; }
	 public String getEmployeeId(){ return this.employeeId; }
	 public void setEmployeeId(String employeeId){ this.employeeId=employeeId; }
	 public String getDepartmentId(){ return this.departmentId; }
	 public void setDepartmentId(String departmentId){ this.departmentId=departmentId; }
	 public String getName(){ return this.name; }
	 public void setName(String name){ this.name=name; }
	 public String getSex(){ return this.sex; }
	 public void setSex(String sex){ this.sex=sex; }
	 public String getNation(){ return this.nation; }
	 public void setNation(String nation){ this.nation=nation; }
	 public String getBrithday(){ return this.brithday; }
	 public void setBrithday(String brithday){ this.brithday=brithday; }
	 public String getMarriage(){ return this.marriage; }
	 public void setMarriage(String marriage){ this.marriage=marriage; }
	 public String getIdcard(){ return this.idcard; }
	 public void setIdcard(String idcard){ this.idcard=idcard; }
	 public String getBrith_address(){ return this.brith_address; }
	 public void setBrith_address(String brith_address){ this.brith_address=brith_address; }
	 public String getNatives(){ return this.natives; }
	 public void setNatives(String natives){ this.natives=natives; }
	 public String getParents_address(){ return this.parents_address; }
	 public void setParents_address(String parents_address){ this.parents_address=parents_address; }
	 public String getWrok_time(){ return this.wrok_time; }
	 public void setWrok_time(String wrok_time){ this.wrok_time=wrok_time; }
	 public String getAssume_office_time(){ return this.assume_office_time; }
	 public void setAssume_office_time(String assume_office_time){ this.assume_office_time=assume_office_time; }
	 public String getContract_date(){ return this.contract_date; }
	 public void setContract_date(String contract_date){ this.contract_date=contract_date; }
	 public String getGrad_formal(){ return this.grad_formal; }
	 public void setGrad_formal(String grad_formal){ this.grad_formal=grad_formal; }
	 public String getGrad_degrees(){ return this.grad_degrees; }
	 public void setGrad_degrees(String grad_degrees){ this.grad_degrees=grad_degrees; }
	 public String getT_grad_degrees(){ return this.t_grad_degrees; }
	 public void setT_grad_degrees(String t_grad_degrees){ this.t_grad_degrees=t_grad_degrees; }
	 public String getTwo_grad_degrees(){ return this.two_grad_degrees; }
	 public void setTwo_grad_degrees(String two_grad_degrees){ this.two_grad_degrees=two_grad_degrees; }
	 public String getCet_grade(){ return this.cet_grade; }
	 public void setCet_grade(String cet_grade){ this.cet_grade=cet_grade; }
	 public String getTwo_foreign_language(){ return this.two_foreign_language; }
	 public void setTwo_foreign_language(String two_foreign_language){ this.two_foreign_language=two_foreign_language; }
	 public String getAcademic_title(){ return this.academic_title; }
	 public void setAcademic_title(String academic_title){ this.academic_title=academic_title; }
	 public String getAcademic_title_time(){ return this.academic_title_time; }
	 public void setAcademic_title_time(String academic_title_time){ this.academic_title_time=academic_title_time; }
	 public String getPolitics(){ return this.politics; }
	 public void setPolitics(String politics){ this.politics=politics; }
	 public String getPolitics_time(){ return this.politics_time; }
	 public void setPolitics_time(String politics_time){ this.politics_time=politics_time; }
	 public String getH_k_address(){ return this.h_k_address; }
	 public void setH_k_address(String h_k_address){ this.h_k_address=h_k_address; }
	 public String getH_k_move(){ return this.h_k_move; }
	 public void setH_k_move(String h_k_move){ this.h_k_move=h_k_move; }
	 public String getNow_address(){ return this.now_address; }
	 public void setNow_address(String now_address){ this.now_address=now_address; }
	 public String getConn_mobile(){ return this.conn_mobile; }
	 public void setConn_mobile(String conn_mobile){ this.conn_mobile=conn_mobile; }
	 public String getConn_phone(){ return this.conn_phone; }
	 public void setConn_phone(String conn_phone){ this.conn_phone=conn_phone; }
	 public String getExam_course1(){ return this.exam_course1; }
	 public void setExam_course1(String exam_course1){ this.exam_course1=exam_course1; }
	 public String getExam_course2(){ return this.exam_course2; }
	 public void setExam_course2(String exam_course2){ this.exam_course2=exam_course2; }
	 public String getExam_course3(){ return this.exam_course3; }
	 public void setExam_course3(String exam_course3){ this.exam_course3=exam_course3; }
	 public String getExam_course4(){ return this.exam_course4; }
	 public void setExam_course4(String exam_course4){ this.exam_course4=exam_course4; }
	 public String getExam_course5(){ return this.exam_course5; }
	 public void setExam_course5(String exam_course5){ this.exam_course5=exam_course5; }
	 public String getExam_time1(){ return this.exam_time1; }
	 public void setExam_time1(String exam_time1){ this.exam_time1=exam_time1; }
	 public String getExam_time2(){ return this.exam_time2; }
	 public void setExam_time2(String exam_time2){ this.exam_time2=exam_time2; }
	 public String getExam_time3(){ return this.exam_time3; }
	 public void setExam_time3(String exam_time3){ this.exam_time3=exam_time3; }
	 public String getExam_time4(){ return this.exam_time4; }
	 public void setExam_time4(String exam_time4){ this.exam_time4=exam_time4; }
	 public String getExam_time5(){ return this.exam_time5; }
	 public void setExam_time5(String exam_time5){ this.exam_time5=exam_time5; }
	 public String getAcquire_academic_title1(){ return this.acquire_academic_title1; }
	 public void setAcquire_academic_title1(String acquire_academic_title1){ this.acquire_academic_title1=acquire_academic_title1; }
	 public String getAcquire_academic_title2(){ return this.acquire_academic_title2; }
	 public void setAcquire_academic_title2(String acquire_academic_title2){ this.acquire_academic_title2=acquire_academic_title2; }
	 public String getAcquire_academic_title3(){ return this.acquire_academic_title3; }
	 public void setAcquire_academic_title3(String acquire_academic_title3){ this.acquire_academic_title3=acquire_academic_title3; }
	 public String getAcquire_academic_title4(){ return this.acquire_academic_title4; }
	 public void setAcquire_academic_title4(String acquire_academic_title4){ this.acquire_academic_title4=acquire_academic_title4; }
	 public String getAcquire_academic_title5(){ return this.acquire_academic_title5; }
	 public void setAcquire_academic_title5(String acquire_academic_title5){ this.acquire_academic_title5=acquire_academic_title5; }
	 public String getRegister_time1(){ return this.register_time1; }
	 public void setRegister_time1(String register_time1){ this.register_time1=register_time1; }
	 public String getRegister_time2(){ return this.register_time2; }
	 public void setRegister_time2(String register_time2){ this.register_time2=register_time2; }
	 public String getRegister_time3(){ return this.register_time3; }
	 public void setRegister_time3(String register_time3){ this.register_time3=register_time3; }
	 public String getRegister_time4(){ return this.register_time4; }
	 public void setRegister_time4(String register_time4){ this.register_time4=register_time4; }
	 public String getRegister_time5(){ return this.register_time5; }
	 public void setRegister_time5(String register_time5){ this.register_time5=register_time5; }
	 public String getStudy_start_time1(){ return this.study_start_time1; }
	 public void setStudy_start_time1(String study_start_time1){ this.study_start_time1=study_start_time1; }
	 public String getStudy_start_time2(){ return this.study_start_time2; }
	 public void setStudy_start_time2(String study_start_time2){ this.study_start_time2=study_start_time2; }
	 public String getStudy_start_time3(){ return this.study_start_time3; }
	 public void setStudy_start_time3(String study_start_time3){ this.study_start_time3=study_start_time3; }
	 public String getStudy_start_time4(){ return this.study_start_time4; }
	 public void setStudy_start_time4(String study_start_time4){ this.study_start_time4=study_start_time4; }
	 public String getStudy_end_time1(){ return this.study_end_time1; }
	 public void setStudy_end_time1(String study_end_time1){ this.study_end_time1=study_end_time1; }
	 public String getStudy_end_time2(){ return this.study_end_time2; }
	 public void setStudy_end_time2(String study_end_time2){ this.study_end_time2=study_end_time2; }
	 public String getStudy_end_time3(){ return this.study_end_time3; }
	 public void setStudy_end_time3(String study_end_time3){ this.study_end_time3=study_end_time3; }
	 public String getStudy_end_time4(){ return this.study_end_time4; }
	 public void setStudy_end_time4(String study_end_time4){ this.study_end_time4=study_end_time4; }
	 public String getGrad_school1(){ return this.grad_school1; }
	 public void setGrad_school1(String grad_school1){ this.grad_school1=grad_school1; }
	 public String getGrad_school2(){ return this.grad_school2; }
	 public void setGrad_school2(String grad_school2){ this.grad_school2=grad_school2; }
	 public String getGrad_school3(){ return this.grad_school3; }
	 public void setGrad_school3(String grad_school3){ this.grad_school3=grad_school3; }
	 public String getGrad_school4(){ return this.grad_school4; }
	 public void setGrad_school4(String grad_school4){ this.grad_school4=grad_school4; }
	 public String getGrad_major1(){ return this.grad_major1; }
	 public void setGrad_major1(String grad_major1){ this.grad_major1=grad_major1; }
	 public String getGrad_major2(){ return this.grad_major2; }
	 public void setGrad_major2(String grad_major2){ this.grad_major2=grad_major2; }
	 public String getGrad_major3(){ return this.grad_major3; }
	 public void setGrad_major3(String grad_major3){ this.grad_major3=grad_major3; }
	 public String getGrad_major4(){ return this.grad_major4; }
	 public void setGrad_major4(String grad_major4){ this.grad_major4=grad_major4; }
	 public String getWork_strart_time1(){ return this.work_strart_time1; }
	 public void setWork_strart_time1(String work_strart_time1){ this.work_strart_time1=work_strart_time1; }
	 public String getWork_strart_time2(){ return this.work_strart_time2; }
	 public void setWork_strart_time2(String work_strart_time2){ this.work_strart_time2=work_strart_time2; }
	 public String getWork_strart_time3(){ return this.work_strart_time3; }
	 public void setWork_strart_time3(String work_strart_time3){ this.work_strart_time3=work_strart_time3; }
	 public String getWork_strart_time4(){ return this.work_strart_time4; }
	 public void setWork_strart_time4(String work_strart_time4){ this.work_strart_time4=work_strart_time4; }
	 public String getWork_end_time1(){ return this.work_end_time1; }
	 public void setWork_end_time1(String work_end_time1){ this.work_end_time1=work_end_time1; }
	 public String getWork_end_time2(){ return this.work_end_time2; }
	 public void setWork_end_time2(String work_end_time2){ this.work_end_time2=work_end_time2; }
	 public String getWork_end_time3(){ return this.work_end_time3; }
	 public void setWork_end_time3(String work_end_time3){ this.work_end_time3=work_end_time3; }
	 public String getWork_end_time4(){ return this.work_end_time4; }
	 public void setWork_end_time4(String work_end_time4){ this.work_end_time4=work_end_time4; }
	 public String getWork_unit1(){ return this.work_unit1; }
	 public void setWork_unit1(String work_unit1){ this.work_unit1=work_unit1; }
	 public String getWork_unit2(){ return this.work_unit2; }
	 public void setWork_unit2(String work_unit2){ this.work_unit2=work_unit2; }
	 public String getWork_unit3(){ return this.work_unit3; }
	 public void setWork_unit3(String work_unit3){ this.work_unit3=work_unit3; }
	 public String getWork_unit4(){ return this.work_unit4; }
	 public void setWork_unit4(String work_unit4){ this.work_unit4=work_unit4; }
	 public String getWork_job1(){ return this.work_job1; }
	 public void setWork_job1(String work_job1){ this.work_job1=work_job1; }
	 public String getWork_job2(){ return this.work_job2; }
	 public void setWork_job2(String work_job2){ this.work_job2=work_job2; }
	 public String getWork_job3(){ return this.work_job3; }
	 public void setWork_job3(String work_job3){ this.work_job3=work_job3; }
	 public String getWork_job4(){ return this.work_job4; }
	 public void setWork_job4(String work_job4){ this.work_job4=work_job4; }
	 public String getFamily_name1(){ return this.family_name1; }
	 public void setFamily_name1(String family_name1){ this.family_name1=family_name1; }
	 public String getFamily_name2(){ return this.family_name2; }
	 public void setFamily_name2(String family_name2){ this.family_name2=family_name2; }
	 public String getFamily_name3(){ return this.family_name3; }
	 public void setFamily_name3(String family_name3){ this.family_name3=family_name3; }
	 public String getSociety_name1(){ return this.society_name1; }
	 public void setSociety_name1(String society_name1){ this.society_name1=society_name1; }
	 public String getSociety_name2(){ return this.society_name2; }
	 public void setSociety_name2(String society_name2){ this.society_name2=society_name2; }
	 public String getRelation1(){ return this.relation1; }
	 public void setRelation1(String relation1){ this.relation1=relation1; }
	 public String getRelation2(){ return this.relation2; }
	 public void setRelation2(String relation2){ this.relation2=relation2; }
	 public String getRelation3(){ return this.relation3; }
	 public void setRelation3(String relation3){ this.relation3=relation3; }
	 public String getRelation4(){ return this.relation4; }
	 public void setRelation4(String relation4){ this.relation4=relation4; }
	 public String getRelation5(){ return this.relation5; }
	 public void setRelation5(String relation5){ this.relation5=relation5; }
	 public String getF_work_until1(){ return this.f_work_until1; }
	 public void setF_work_until1(String f_work_until1){ this.f_work_until1=f_work_until1; }
	 public String getF_work_until2(){ return this.f_work_until2; }
	 public void setF_work_until2(String f_work_until2){ this.f_work_until2=f_work_until2; }
	 public String getF_work_until3(){ return this.f_work_until3; }
	 public void setF_work_until3(String f_work_until3){ this.f_work_until3=f_work_until3; }
	 public String getF_work_until4(){ return this.f_work_until4; }
	 public void setF_work_until4(String f_work_until4){ this.f_work_until4=f_work_until4; }
	 public String getF_work_until5(){ return this.f_work_until5; }
	 public void setF_work_until5(String f_work_until5){ this.f_work_until5=f_work_until5; }
	 public String getF_work_job1(){ return this.f_work_job1; }
	 public void setF_work_job1(String f_work_job1){ this.f_work_job1=f_work_job1; }
	 public String getF_work_job2(){ return this.f_work_job2; }
	 public void setF_work_job2(String f_work_job2){ this.f_work_job2=f_work_job2; }
	 public String getF_work_job3(){ return this.f_work_job3; }
	 public void setF_work_job3(String f_work_job3){ this.f_work_job3=f_work_job3; }
	 public String getF_work_job4(){ return this.f_work_job4; }
	 public void setF_work_job4(String f_work_job4){ this.f_work_job4=f_work_job4; }
	 public String getF_work_job5(){ return this.f_work_job5; }
	 public void setF_work_job5(String f_work_job5){ this.f_work_job5=f_work_job5; }
	 public String getF_phone1(){ return this.f_phone1; }
	 public void setF_phone1(String f_phone1){ this.f_phone1=f_phone1; }
	 public String getF_phone2(){ return this.f_phone2; }
	 public void setF_phone2(String f_phone2){ this.f_phone2=f_phone2; }
	 public String getF_phone3(){ return this.f_phone3; }
	 public void setF_phone3(String f_phone3){ this.f_phone3=f_phone3; }
	 public String getF_phone4(){ return this.f_phone4; }
	 public void setF_phone4(String f_phone4){ this.f_phone4=f_phone4; }
	 public String getF_phone5(){ return this.f_phone5; }
	 public void setF_phone5(String f_phone5){ this.f_phone5=f_phone5; }
	 public String getRemarks(){ return this.remarks; }
	 public void setRemarks(String remarks){ this.remarks=remarks; }
	 public String getWrite_date(){ return this.write_date; }
	 public void setWrite_date(String write_date){ this.write_date=write_date; }
	 public String getType(){ return this.type; }
	 public void setType(String type){ this.type=type; }
	 public String getIsneed(){ return this.isneed; }
	 public void setIsneed(String isneed){ this.isneed=isneed; }
	 public String getRlb_opinion(){ return this.rlb_opinion; }
	 public void setRlb_opinion(String rlb_opinion){ this.rlb_opinion=rlb_opinion; }
	 public String getZg_opinion(){ return this.zg_opinion; }
	 public void setZg_opinion(String zg_opinion){ this.zg_opinion=zg_opinion; }
	 public String getProfession_from_prac(){ return this.profession_from_prac; }
	 public void setProfession_from_prac(String profession_from_prac){ this.profession_from_prac=profession_from_prac; }



}
