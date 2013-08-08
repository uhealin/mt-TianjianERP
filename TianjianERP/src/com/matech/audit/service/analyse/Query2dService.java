package com.matech.audit.service.analyse;

import java.sql.Connection;
import java.text.MessageFormat;
import java.text.Normalizer.Form;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.matech.audit.service.analyse.model.ConditionVO;
import com.matech.audit.service.analyse.model.TableColVO;
import com.matech.audit.service.analyse.model.TableRowVO;
import com.matech.audit.service.analyse.model.TableVO;
import com.matech.audit.service.form.model.FormVO;
import com.matech.framework.pub.db.DbUtil;

public class Query2dService {
   
	protected Connection conn;
	protected DbUtil dbUtil;
	public Query2dService(Connection conn){
		this.conn=conn;
		try {
			dbUtil=new DbUtil(conn);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String getSql(JSONArray jarr,String prefix){
		String sql="";
		if(jarr==null)return sql;
		for(int i=0;i<jarr.size();i++){
			JSONObject json=jarr.getJSONObject(i);
            try{
			String logic=json.get("cond_logic").toString();
			String column=json.get("cond_column").toString();
			String oper=json.get("cond_oper").toString();
			String val=json.getString("cond_val");
			if(oper.contains("like")){
				val="'%"+val+"%'";
			}else{
				val="'"+val+"'";
			}
			sql+=MessageFormat.format(" {0} {1}.{2} {3} {4} ", 
				i==0?"and":logic,
				prefix,
				column,
				oper,
				val
			);
            }catch(Exception ex){
            	ex.printStackTrace();
            }
		}
		return sql;
	}
	
	public int[][] cal(TableVO tableVO){
		
		List<TableColVO> tableColVOs=dbUtil.select(TableColVO.class, "select * from {0} where tableid=?",tableVO.getUuid() );
		List<TableRowVO> tableRowVOs=dbUtil.select(TableRowVO.class, "select * from {0} where tableid=?",tableVO.getUuid() );
		int[][] res =new int[tableRowVOs.size()][tableColVOs.size()];
		for(int row=0;row<tableRowVOs.size();row++){
			for(int col=0;col<tableColVOs.size();col++){
				res[row][col]=cal(tableColVOs.get(col), tableRowVOs.get(row));
			}
		}
		return res;
	}
	
	public List<ConditionVO> condRows(TableVO tableVO){
		//List<ConditionVO> condColVOs=dbUtil.select(ConditionVO.class, "select * from {0} where uuid in (select conid from an_tablecols where tableid=?)",tableVO.getUuid() );
		return dbUtil.select(ConditionVO.class, "select * from {0} where uuid in (select conid from an_tablerows where tableid=?)",tableVO.getUuid() );

	}
	
	
	public List<ConditionVO> condCols(TableVO tableVO){
		//List<ConditionVO> condColVOs=dbUtil.select(ConditionVO.class, "select * from {0} where uuid in (select conid from an_tablecols where tableid=?)",tableVO.getUuid() );
		return dbUtil.select(ConditionVO.class, "select * from {0} where uuid in (select conid from an_tablecols where tableid=?)",tableVO.getUuid() );

	}
	
	public int cal(TableColVO tableColVO,TableRowVO tableRowVO){
		int re=0;
		DbUtil dbUtil=null;
		FormVO formCol,formRow;
		ConditionVO condCol,condRow;
		JSONArray jarrCol,jarrRow;
		String sqlPattern="select count(t1.id) from ( select distinct a.id from k_user a inner join {0} r_{0} on a.id=r_{0}.userid where 1=1 {1} ) t1,"
	    + "( select distinct a.id from k_user a inner join {2} c_{2} on a.id=c_{2}.userid where 1=1 {3} ) t2 where t1.id=t2.id"
	    ;
		String sql="";
		try{
			dbUtil=new DbUtil(conn);
			condCol=dbUtil.load(ConditionVO.class, tableColVO.getConid());
			formCol=dbUtil.load(FormVO.class, condCol.getFormid());
			jarrCol=JSONArray.fromObject(condCol.getJsonstr());
			
			condRow=dbUtil.load(ConditionVO.class, tableRowVO.getConid());
			formRow=dbUtil.load(FormVO.class, condRow.getFormid());
			jarrRow=JSONArray.fromObject(condRow.getJsonstr());
			
			sql=MessageFormat.format(sqlPattern,
					formRow.getTABLENAME()
					,getSql(jarrRow, "r_"+formRow.getTABLENAME())
			        ,formCol.getTABLENAME()
			        ,getSql(jarrCol, "c_"+formCol.getTABLENAME())
			);
			System.out.println("二维查询:"+sql);
			re=dbUtil.queryForInt(sql);
		}catch (Exception ex){
			ex.printStackTrace();
		}
		return re;
	}
	
	
}
