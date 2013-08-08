package com.matech.audit.service.form;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.management.Query;

import com.matech.audit.service.form.model.FieldVO;
import com.matech.audit.service.form.model.FormVO;
import com.matech.audit.service.form.model.QueryVO;
import com.matech.framework.pub.db.DbUtil;
import com.mysql.jdbc.Driver;
import com.sun.mail.handlers.message_rfc822;

public class GenFormService {

	private Connection conn;
	private DbUtil dbUtil;
	
	public GenFormService(Connection conn) throws Exception{
		this.conn=conn;
		this.dbUtil=new DbUtil(conn);
	}
	
	
	public static String textField(QueryVO queryVO){
		return MessageFormat.format("ext_id=\"{0}\" ext_name=\"{0}\" ext_field=\"{0}\" id=\"{0}\" name=\"{0}\"", queryVO.getENNAME());
	}
	
	public static String mtTextField(QueryVO queryVO){
		return MessageFormat.format("ext_id={0};ext_name={0};ext_type={1};ext_field={0};", queryVO.getENNAME(),"text");
	}
	
	public String tableHtml(FormVO formVO,int cols,boolean noempty){
		return tableHtml(formVO, cols, noempty,"");
	}
	
	public String tableHtml(FormVO formVO,int cols,boolean noempty,String prendContext){
		StringBuffer context=new StringBuffer("");
		
		List<QueryVO> tempQueryVOs=dbUtil.select(QueryVO.class, 
				"select * from {0} where formid=? and enname != ? order by orderid asc  ",formVO.getUUID(),"uuid");
		
		
		
		List<QueryVO> queryVOs=new ArrayList<QueryVO>();
		Set<String> columns=this.columns(formVO);
		for(QueryVO queryVO :tempQueryVOs){
			if(!columns.contains(queryVO.getENNAME().toLowerCase()))continue;
			if(queryVO.getBSHOW()!=1||(noempty&&(queryVO.getNAME()==null||queryVO.getNAME().isEmpty()))){
				continue;
			}
			queryVOs.add(queryVO);
		}
		if(cols<1){
			if(queryVOs.size()<10){
				cols=1;
			}else if(queryVOs.size()<20){
				cols=2;
			}else if(queryVOs.size()<30){
				cols=3;
			}else{
				cols=4;
			}
		}
		
		context
		.append("<table class='formTable'> \n")
		.append(MessageFormat.format("<thead><tr><th colspan=\"{0}\">{1}</th></tr></thead> \n",cols*2,formVO.getNAME()))
		.append("<tbody> \n")
		;
		
		for(int index=0;index<queryVOs.size();index++){
			StringBuffer fieldContext=new StringBuffer("");
			QueryVO queryVO=queryVOs.get(index);
		
			if(index%cols==0){
				fieldContext.append("<tr> \n");
			}
			
			fieldContext
			.append(MessageFormat.format("<th>{0}</th><td>",queryVO.getNAME()))
			.append(MessageFormat.format("<input matech_ext=\"{0}ext_end\" type=\"text\" />",mtTextField(queryVO),queryVO.getENNAME() ) )
			.append("</td> \n")
			;
			
			if(index%cols==cols-1||index==queryVOs.size()-1){
				fieldContext.append("</tr> \n");
			}
			context.append(fieldContext);
		}
		context.append("</tbody></table>");
		context.append("\n"+prendContext);
		return context.toString();
	}
	
	public List<FieldVO> tableFields(FormVO formVO){
		List<FieldVO> fieldVOs=new LinkedList<FieldVO>();
		List<QueryVO> queryVOs=dbUtil.select(QueryVO.class, 
				"select * from {0} where formid=? order by orderid asc ",formVO.getUUID());
		int i=1;
		for(QueryVO queryVO:queryVOs){
			FieldVO fieldVO=new FieldVO();
			fieldVO.setUUID(UUID.randomUUID().toString());
			fieldVO.setENNAME(queryVO.getENNAME());
			fieldVO.setFORMID(queryVO.getFORMID());
			fieldVO.setMATECHEXT(mtTextField(queryVO));
			fieldVO.setNAME(queryVO.getENNAME());
			fieldVO.setORDERID(queryVO.getORDERID());
			fieldVO.setPARENTFORMID("");
			fieldVOs.add(fieldVO);
		}
		
		return fieldVOs;
	}
	
	private Set<String> columns(FormVO formVO) {
		Set<String> cols=new HashSet<String>();
		String sql=MessageFormat.format("select * from {0} where 1=2", formVO.getTABLENAME());
		PreparedStatement ps;
		try {
			ps = this.conn.prepareStatement(sql);
			ResultSet rs=ps.executeQuery();
			for(int i=0;i<rs.getMetaData().getColumnCount();i++){
				cols.add(rs.getMetaData().getColumnName(i+1).toLowerCase());
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return cols;
	}
	
	public int genTable(FormVO formVO,int cols,boolean noempty) throws Exception{
		
		return genTable(formVO, cols, noempty,"");
	}
	public int genTable(FormVO formVO,int cols,boolean noempty,String pretendContext) throws Exception{
            int re=0;
			String context=this.tableHtml(formVO, cols,noempty,pretendContext);
			formVO.setDEFINESTR(context);
			re+=dbUtil.update(formVO);
			dbUtil.del("mt_com_form_field", "formid",formVO.getUUID() );
			List<FieldVO> formVOs=this.tableFields(formVO);
			re+=dbUtil.insert(formVOs.toArray());
			return re;
	}
	
	public static void main(String[] args){
		String sql="select sex from k_user where id=9802";
		String context="<input matech_ext=\"ext_id=sex;ext_name=sex;ext_default=sql{"+sql+" };ext_field=sex;ext_end\" type=\"text\" value=\""+sql+" }\" /></td>";
		String pa="sql\\{"+sql+" \\}";
		System.err.println(context.replaceAll(pa, "男"));
		
		/*
		Connection conn=null;
		try {
			Class.forName(Driver.class.getName());
			//matech-sd2.eicp.net 172.19.7.121172.19.7.121
		    conn=DriverManager.getConnection("jdbc:mysql://172.19.7.121:5188/asdb?autoReconnect=true&useUnicode=true&characterEncoding=utf-8","xoops_root","654321");
		    GenFormService genEditTableApp=new GenFormService(conn);
            DbUtil dbUtil=new DbUtil(conn);
            FormVO formVO=dbUtil.load(FormVO.class, "89217d57-2027-4f7d-9826-50418c03ede8");
			int re=genEditTableApp.genTable(formVO,2,true);
			System.out.println(re);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        DbUtil.close(conn);
        */
	}
}
