package com.matech.audit.service.rule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.exception.MatechException;
import com.matech.framework.pub.util.Debug;

public class RuleModelService {
 
	private String colsNameCN="";
	  private String fieldNames="";
	  private String colsType="";//0是默认,1是showMoney,2是center
	  private int colsConut;
	  private String colsName;
	  private String sqlWhere;
	  private String sqlTable;
	  private String analyseType;
	  private int realFieldCount=0;
	  private double[] ruleResults=null;
	  private String yenAxes="";
	  private String ycnAxes="";
	private Connection conn = null;

	public RuleModelService(Connection conn) {
		this.conn = conn;
	}
	
	  public String getSql(String starYear,String starMonth,
	          String endYear,String endMonth,String section,
	          String ruleProperty,
	          String ruleid,String whetherTypeID,String bi,
	          String method,String prjectIdOrCustomerId) throws Exception{




	String tablesName=getTablesName(starYear,starMonth,endYear,endMonth,section);

	if(tablesName.length()<=0){
	return "";
	}
	this.setColsName(getColsName(tablesName,method,prjectIdOrCustomerId,ruleid,ruleProperty,bi,whetherTypeID));
//	this.setSqlWhere(getSqlWhere(tablesName,departID,starYear));
	this.setSqlTable(getSqlTable(tablesName,ruleid));
	this.setYcnAxes(getYcnAxes(ruleid));
	this.setAnalyseType(ruleProperty);
	//this.setAnalyseType(dataType,dataDirection);

//	org.util.Debug.prtOut(tablesName);
//	org.util.Debug.prtOut(colsName);
//	org.util.Debug.prtOut(sqlWhere);
//	org.util.Debug.prtOut("::::::::::::::::::::::::::::::::::::::::::::::::::::");
//	String sql="select "+colsName+" from \n"+sqlTable+" \n"+sqlWhere;
//	org.util.Debug.prtOut(sql);
	return "";
	 }
	
	
	/**
	  * 根据Title求AutoId
	  * @param title
	  * @return
	  * @throws Exception
	  */
public String getSqlTable(String tablesName,String ruleid) throws Exception {
	DbUtil.checkConn(conn);
	PreparedStatement ps = null;
	ResultSet rs = null;
	EdRuleService edRuleService=null;
	String[] tn=tablesName.split(",");
	String[] ss=ruleid.split(",");
	String sql="";
	try{
		
		edRuleService=new  EdRuleService(conn);
		
		for(int i=0;i<tn.length;i++){
			if(i==0){
				sql+="select '"+tn[i].substring(tn[i].lastIndexOf("y")+1,tn[i].lastIndexOf("y")+5)+"年"+tn[i].substring(tn[i].lastIndexOf("m")+1)+"月' as submonth,";
			}else{
				sql+="select '"+tn[i].substring(tn[i].lastIndexOf("y")+1,tn[i].lastIndexOf("y")+5)+"年"+tn[i].substring(tn[i].lastIndexOf("m")+1)+"月',";
			}
			//System.out.println("yzm:tn["+i+"]="+tn[i]);
			for(int j=0;j<ss.length;j++){	
				
				
				
				if(i==0){
					
					if(j!=ss.length-1){
						yenAxes+="s"+ss[j].replaceAll("'", "")+",";
			    	}else{
			    		yenAxes+="s"+ss[j].replaceAll("'", "");
			    	}
				if(j==ss.length-1){
						sql+=ruleResults[j*tn.length+i]+" as s"+ss[j].replaceAll("'", "")+" \n";
					}else{
						sql+=ruleResults[j*tn.length+i]+" as s"+ss[j].replaceAll("'", "")+", \n";
					}
				}else{
					if(j==ss.length-1){
						sql+=ruleResults[j*tn.length+i]+" \n";
					}else{
						sql+=ruleResults[j*tn.length+i]+", \n";
					}
				}
			}
			
			if(i!=tn.length-1){
				sql+=" union all\n";
	    	}
			
		}
	//	System.out.println("yzm;sql1="+sql);
		 
		return sql;
		
		
	}catch (Exception e) {
		Debug.print(Debug.iError, "访问失败！", e);
		throw new MatechException("访问失败！" + e.getMessage(), e);
	} finally {
		DbUtil.close(rs);
		DbUtil.close(ps);
	}	
	
}

public String getYcnAxes(String ruleid) throws Exception {
	DbUtil.checkConn(conn);
	PreparedStatement ps = null;
	ResultSet rs = null;
	String title="";
	try{
		
		String sql="select title from k_rule where autoid in("+ruleid+")";
		ps=conn.prepareStatement(sql);
		rs=ps.executeQuery();
		
		while(rs.next()){	
			title+=rs.getString(1)+",";
		}
		title=title.substring(0,title.length()-1);
	//	System.out.println("yzm:title="+title);
		return title;
		
	}catch (Exception e) {
		Debug.print(Debug.iError, "访问失败！", e);
		throw new MatechException("访问失败！" + e.getMessage(), e);
	} finally {
		DbUtil.close(rs);
		DbUtil.close(ps);
	}	
}

private String getColsName(String tablesName,String method,String prjectIdOrCustomerId ,String ruleid,String ruleProperty,String bi,String whetherTypeID)throws Exception{

	EdRuleService edRuleService=null;
    String[] ri=ruleid.split(",");
    String[] rp=ruleProperty.split(",");
    String[] tn=tablesName.split(",");
    
  // System.out.println("yzm:ruleid="+ruleid);
    double ruleResult=0;
    ruleResults=new double[ri.length*tn.length];
    edRuleService=new  EdRuleService(conn);
    String result=" ";

    System.out.println("\n---------------------taozitaozitaozi--------------------\n");
    
    //colsCount
    int cc=0;
    
    
    realFieldCount=-1;
    fieldNames="";
   
    for(int j=0;j<ri.length;j++){
    	result+="select autoid,title, ";
    	
    	for(int i=0;i<tn.length;i++){
    		
    		ruleResult =edRuleService.getRuleResult(ri[j], method, prjectIdOrCustomerId,tn[i].substring(tn[i].lastIndexOf("y")+1,tn[i].lastIndexOf("y")+5),tn[i].substring(tn[i].lastIndexOf("m")+1),rp[0], bi); 
    		
    		result = result + "\n";	
    		result+="'"+ruleResult+"'"+"as field"+(++realFieldCount)+",";
    		fieldNames+="field"+realFieldCount+",";
    		ruleResults[j*tn.length+i]=ruleResult;
    		
            //set中文col名
    		if(j==0){
    			
    			appendColsNameCN(tn[i] , "");
    			appendColsType("1");
    			cc++;
    		}
      	
            //差异率
            if(i!=0){
            	
            	if(whetherTypeID.equals("定基")){
            		result = result +" ifnull(("+ (ruleResults[j*tn.length+i]-ruleResults[j*tn.length])+")/"+ruleResults[0]+",0)*100 as field"+(++realFieldCount)+",";
            		
            		
            		if(j==0){
            			appendColsNameCN("差异率%");
            			fieldNames+="field"+realFieldCount+",";
            			appendColsType("1");
            			cc++	;
            		}
            	}else{
            		result = result +" ifnull(("+ (ruleResults[j*tn.length+i]- ruleResults[j*tn.length+i-1])+")/"+ruleResults[i-1]+",0)*100 as field"+(++realFieldCount)+",";
            		if(j==0){
            			appendColsNameCN("差异率%");
            			fieldNames+="field"+realFieldCount+",";
            			appendColsType("1");
            			cc++;
            		}
            	}
              //审计建议　
              result = result + " '' as "+tn[i]+"advice,";
              //result = result + " '' as field"+(++realFieldCount)+",";
              if(j==0){
            	  fieldNames+=tn[i]+"advice,";
              
            	  appendColsNameCN("审计建议");
            	  appendColsType("0");
            	  cc++;
              }
            }
            
      
    	}
    	result=result+" 1 from k_rule where autoid="+ri[j]+" \n";
    	if(j!=ri.length-1){
    		result=result+" union \n";
    	}
    
    }
    
    setColsConut(cc);
   
    result=result.substring(0,result.length()-1);
   
    fieldNames=fieldNames.substring(0,fieldNames.length()-1);
    System.out.println("result="+result);
	System.out.println("setColsConut="+cc);
	System.out.println("fieldNames="+fieldNames);
  

    
    
    return result;
  }


//返回内联表的个数名称，及where条件,用“;”分隔
private String getTablesName(String starYear,String starMonth,
                      String endYear,String endMonth,String section){

  int sy=Integer.parseInt(starYear.trim());
  int sm=Integer.parseInt(starMonth.trim());
  int ey=Integer.parseInt(endYear.trim());
  int em=Integer.parseInt(endMonth.trim());

  String result="";

  //section的值:1时间点 2时间段 3同期数


    if (section.equals("1")) {
//      if (sm == em) {
//        result = result + "y" + sy + "m" + sm + ",";
//      }
//      else {
        result = result + "y" + sy + "m" + sm + ",";
        result = result + "y" + ey + "m" + em + ",";
//      }
      //删除最后一个","
      result = result.substring(0, result.length() - 1);
    }
    else if (section.equals("2")) {
      for(int i=sy;i<=ey;i++){
        if (i == sy) {
          for (int j = sm; j <= 12; j++) {
            result = result + "y" + i + "m" + j + ",";
          }
          continue;
        }
        if (i == ey) {
          for (int j = 1; j <= em; j++) {
            result = result + "y" + i + "m" + j + ",";
          }
          continue;
        }
        for(int j=1;j<12;j++){
          result = result+"y" + i + "m" + j + ",";
        }
      }
      result = result.substring(0,result.length()-1);
      return result;
    }
    else if (section.equals("3")) {
      if(sm>em){ em=12;}
      for(int i=sy;i<=ey;i++){
        for(int j=sm;j<=em;j++){
          result=(String) result + "y"+i+"m"+j+",";
        }
      }
      //删除最后一个","
      if(result.length()>1){
        result = result.substring(0, result.length() - 1);
      }
      return result;
    }

  
return result;
}

public String getAnalyseType() {
	return analyseType;
}

public void setAnalyseType(String analyseType) {
	this.analyseType = analyseType;
}

public int getColsConut() {
	return colsConut;
}

public void setColsConut(int colsConut) {
	this.colsConut = colsConut;
}

public String getColsName() {
	return colsName;
}

public void setColsName(String colsName) {
	this.colsName = colsName;
}

public String getColsNameCN() {
	return colsNameCN;
}

public void setColsNameCN(String colsNameCN) {
	this.colsNameCN = colsNameCN;
}

public String getColsType() {
	return colsType;
}

public void setColsType(String colsType) {
	this.colsType = colsType;
}

public Connection getConn() {
	return conn;
}

public void setConn(Connection conn) {
	this.conn = conn;
}

public String getFieldNames() {
	return fieldNames;
}

public void setFieldNames(String fieldNames) {
	this.fieldNames = fieldNames;
}

public int getRealFieldCount() {
	return realFieldCount;
}

public void setRealFieldCount(int realFieldCount) {
	this.realFieldCount = realFieldCount;
}

public String getSqlTable() {
	return sqlTable;
}

public void setSqlTable(String sqlTable) {
	this.sqlTable = sqlTable;
}

public String getSqlWhere() {
	return sqlWhere;
}

public void setSqlWhere(String sqlWhere) {
	this.sqlWhere = sqlWhere;
}




  private void appendColsNameCN(String args,String args2) {

    if(args.indexOf("y")>=0){
      args=args.substring(1,5)+"年"+args.substring(6)+"月"+args2;
    }else{
      args=args.substring(1)+"月"+args2;
    }
    this.colsNameCN = this.colsNameCN+args+",";
  }

  private void appendColsNameCN(String args) {
    this.colsNameCN = this.colsNameCN+args+",";
  }

  
  
  private void appendColsType(String colsType) {
    this.colsType=this.colsType+colsType+",";
  }

public double[] getRuleResults() {
	return ruleResults;
}

public void setRuleResults(double[] ruleResults) {
	this.ruleResults = ruleResults;
}

public String getYcnAxes() {
	return ycnAxes;
}

public void setYcnAxes(String ycnAxes) {
	this.ycnAxes = ycnAxes;
}

public String getYenAxes() {
	return yenAxes;
}

public void setYenAxes(String yenAxes) {
	this.yenAxes = yenAxes;
}

 

}
