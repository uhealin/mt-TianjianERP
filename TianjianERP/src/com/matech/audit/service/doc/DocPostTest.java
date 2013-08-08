package com.matech.audit.service.doc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;
import org.quartz.JobExecutionException;

import com.matech.audit.service.doc.model.AutoCodeVO;
import com.matech.audit.service.doc.model.DocFlowVO;
import com.matech.audit.service.doc.model.DocPostFileVO;
import com.matech.audit.service.doc.model.DocPostVO;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.TestUtil;
import com.matech.sms.SmsOpt;
import com.mysql.jdbc.Driver;

public class DocPostTest extends TestUtil {

	protected DocPostService docPostService;
	protected DocPostVO docPostVO;
	protected AutoCodeVO autoCodeVO;
	
	public DocPostTest(){
		    super();
	        try {
	        	docPostService=new DocPostService(conn);
				docPostVO=dbUtil.load(DocPostVO.class, "e78389c5-9650-49f7-b6e8-98444a278ea6");
				 autoCodeVO=dbUtil.load(AutoCodeVO.class,3);
	        } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		    
	    
	}
	
	//@Test
	public void handle(){
		Assert.assertEquals(1,docPostService.doHandle(docPostVO, this.userSession,"").size() );
	}
	
	//@Test
	public void nextNode(){
		DocFlowVO docFlowVO=new DocFlowVO();
		docFlowVO.setCode("fq");
		docFlowVO.setCtype("发文");
		Assert.assertNotNull(docPostService.getNextNode(docFlowVO));
	}
	
	//@Test
	public void nextSeqNo(){
		Assert.assertEquals(1, docPostService.getNextSeqNo());
	}
	
	//@Test
	public void bookCode(){
		
		//Assert.assertNotNull(docPostService.doBookCode(userSession, autoCodeVO));
		System.out.println(docPostService.genDocNo(docPostVO, userSession));
	}
	
	//@Test
	public void getSeqNo(){
		String sql="insert into k_autocode_seqno (no) values (?)";
		for(int i=0;i<8000;i++){
			try {
				dbUtil.executeUpdate(sql, new Object[]{i+1});
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	//@Test
	public void comparedate(){
		SimpleDateFormat smdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date d=smdf.parse(docPostVO.getSignissue_date());
			Calendar cal=Calendar.getInstance();
			cal.add(Calendar.DAY_OF_YEAR, -2);
			Date d1=cal.getTime();
			System.out.println(d1.compareTo(d));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	//@Test
	public void testSMS(){
		//SmsOpt.sendSm("1363225486466", "收短信","",conn);  //13958081898
	}
	
	//@Test
	public void testTimeout(){
		try {
			new DocPostSchedule().execute(null);
		} catch (JobExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	
	public void fixDocFile(){
		try {
			docPostVO=dbUtil.load(DocPostVO.class, "c274b641-a07d-4c9c-9619-52278d28b244");
			DocPostFileVO docPostFileVO=docPostService.createDocPostFile(docPostVO);
			dbUtil.insert(docPostFileVO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
