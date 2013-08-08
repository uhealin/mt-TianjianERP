package com.matech.audit.service.analyse;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.matech.audit.service.analyse.model.TableColVO;
import com.matech.audit.service.analyse.model.TableRowVO;
import com.matech.audit.service.analyse.model.TableVO;
import com.matech.framework.pub.util.TestUtil;

public class Query2dTest extends TestUtil {

	Query2dService query2dService;
	TableVO tableVO;
	List<TableColVO> tableColVOs;
	List<TableRowVO> tableRowVOs;
	
     public Query2dTest(){
    	 query2dService=new Query2dService(conn);
    	 try {
    		 String uuid="e0b24323-e411-475b-9284-96f64beb7f9b";
			tableVO=dbUtil.load(TableVO.class, uuid);
			tableColVOs=dbUtil.select(TableColVO.class, "select * from {0} where tableid=?",uuid );
			tableRowVOs=dbUtil.select(TableRowVO.class, "select * from {0} where tableid=?",uuid );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
     }
     
     @Test
     public void cal(){
    	 for(int r=0;r<tableRowVOs.size();r++){
    		 for(int c=0;c<tableColVOs.size();c++){
    			 Assert.assertEquals(0, query2dService.cal(tableColVOs.get(c), tableRowVOs.get(r)));
    		 }
    	 }
     } 
}
