package com.matech.audit.service.cadet;

import java.sql.Connection;
import java.sql.PreparedStatement;


import com.matech.audit.service.cadet.model.CadetVO;
import com.matech.framework.pub.db.DbUtil;

public class CadetService {

	private Connection conn = null;
	
	public CadetService(Connection conn) {
		this.conn = conn;
	}
	
	
	
}
