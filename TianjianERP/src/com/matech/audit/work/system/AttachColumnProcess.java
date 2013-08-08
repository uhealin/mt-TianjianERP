package com.matech.audit.work.system;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.attachFileUploadService.AttachService;
import com.matech.audit.service.attachFileUploadService.model.Attach;
import com.matech.framework.pub.datagrid.DataGridFieldProcess;
import com.matech.framework.pub.datagrid.DataGridProperty;
import com.matech.framework.pub.db.DbUtil;

public class AttachColumnProcess extends DataGridFieldProcess {


	@Override
	public String fieldProcess(DataGridProperty pp, int rowIndex, int colIndex,
			int length, ResultSet rs, String value) throws Exception {
		
		StringBuffer result = new StringBuffer();
		Connection conn = null;
		
		try {
			
			conn = new DBConnect().getConnect("");
			
			String indexTable = (String)super.getObj() ; 
			
			if(value != null && !"".equals(value)) {
				List list = new AttachService(conn).getAttachList(indexTable, value);
				
				for (int i = 0; i < list.size(); i++) {
					Attach attach = (Attach)list.get(i);
					
					result.append("<a title=\"" + attach.getAttachName() + "\" style=\"text-decoration: underline;\" href=\"common.do?method=attachDownload&attachId=" + attach.getAttachId() + "\" >")
							.append(attach.getAttachName())
							.append("</a><br/>");
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		
		return result.toString();
	}

}
