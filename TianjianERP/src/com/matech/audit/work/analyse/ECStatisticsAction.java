package com.matech.audit.work.analyse;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.physicalExamination.InformService;
import com.matech.audit.service.physicalExamination.model.InformVO;
import com.matech.framework.pub.datagrid.DataGrid;

public class ECStatisticsAction extends MultiActionController {

	protected enum Jsp {
		ecStatist;
		
		public String getPath(){
			return MessageFormat.format("/analyse/{0}.jsp", this.name());
		}
	}
	
	
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response) throws Exception{
		DataGridProperty dp = new DataGridProperty(){};
		
		dp.setTableID("statisticsList");
		dp.setCustomerId("");
		dp.setPageSize_CH(20);
		
		
		
		ModelAndView modelandview =new ModelAndView(Jsp.ecStatist.getPath());
		
		
		
		String sql = 
				"select * from view_user_goabord"  ;
		
		dp.setSQL(sql);
		dp.setOrderBy_CH("name");
		dp.setDirection("desc");
		
		dp.setColumnWidth("8,6,4,8,4,10,8,12,16");
		
		
		String strHead="姓名,出国考察地点登记{";
		//dp.addColumn("部门", "部门");
		dp.addColumn("姓名", "姓名");
		//dp.addColumn("性别", "sex");
		//dp.addColumn("办公区域", "residence");
		//dp.addColumn("楼层", "floor");
		//dp.addColumn("人员类别", "rank");
		
		
		Connection conn = new DBConnect().getConnect();
		
		Calendar cal=Calendar.getInstance();
		for (int i = 0; i <5 ; i++){
			
			dp.addColumn((cal.get(Calendar.YEAR)-i)+"年", i+"年");
			strHead += i + "年";
		}
		strHead = strHead.substring(0, strHead.length()-1) + "}";
		System.out.println("=================================");
		System.out.println("strHead ="+strHead);
		System.out.println("=================================");
		dp.setTableHead(strHead);
		
		//dp.addColumn("是否已领体检表", "examination_get");
		//dp.addColumn("是否已领体检结果表", "results_get");
		
		dp.setTrActionProperty(false);
		
		dp.setWhichFieldIsValue(1);
		dp.setPrintEnable(true);
		dp.setPrintVerTical(false);
		dp.setPrintTitle("出国考察地点登记");

		request.getSession().setAttribute(DataGrid.sessionPre + dp.tableID, dp);
		
		return modelandview;
	}
}
