package com.matech.audit.service.doc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.matech.audit.service.doc.model.DocRecVO;
import com.matech.audit.service.user.model.User;
import com.matech.framework.pub.util.TestUtil;

public class DocRecTest extends TestUtil {

	DocRecService docRecService=null; 
	DocRecVO docRecVO=new DocRecVO();
	
	public DocRecTest(){
		docRecService=new DocRecService(conn);
		try {
			docRecVO=dbUtil.load(DocRecVO.class,"8df9e1db-7b71-4b8e-ac54-df150135537f" );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void toDo(){
	  User user=new User();
	  user.setId("911");
	  user.setName("你妹");
	  String re=docRecService.doToDo(docRecVO, userSession,user).get(0);
	  Assert.assertEquals("",re );	
	 
	}
	
	@Test
	public void check(){
	   Assert.assertEquals("", docRecService.doCheck(docRecVO, userSession).get(0));
	}
	
	@Test
	public void q(){
		List<Integer> ids=new ArrayList<Integer>();
		String sql="select * from s_autohintselect";
		PreparedStatement ps=null;
		ResultSet rs=null;
		try{
			ps=conn.prepareStatement(sql);
			rs=ps.executeQuery();
			while(rs.next()){
				ids.add(rs.getInt("id"));
			}
			for(int id :ids){
			sql="UPDATE s_autohintselect SET UUID=UUID() WHERE id=?";
		    ps=conn.prepareStatement(sql);
		    ps.setInt(1, id);
		    ps.executeUpdate();
			}
		}catch(Exception ex){
			
		}
	}
}
