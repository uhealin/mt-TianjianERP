package com.matech.audit.service.checkInfo;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;

public class NewSubjectBalance {
  String customerID;
  String year;
  String month;

  Statement stmt=null;
  ResultSet rs=null;
  Connection conn=null;

  private String accpackageID;
  private String customerName;
  private String[][] remain=new String[7][4];

  public NewSubjectBalance(String customerID,String year,String month,Connection conn) {
    this.customerID=customerID;
    this.year=year;
    this.month=month;
    try{
     /*
    	DBConnect db=new DBConnect();
    	conn = db.getConnect(customerID);
     */
    	this.conn = conn;
     stmt=conn.createStatement(java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE,java.sql.ResultSet.CONCUR_READ_ONLY);
     this.setAccpackageID();
     this.setCustomerName();
     this.setRemain();
    }catch (Exception e){
      e.printStackTrace();
    }finally{
     this.closeConnection();
    }

  }
  public String getRemain(int type,int opt){   //sql模板,type是一级科目的第一个编号����
	  											//	opt，1是期末余额，2是期初余额，3是借方发生额, 4是贷方发生额�Ǵ����
      return remain[type-1][opt-1];
  }


  private void setRemain(){  //sql模板,type是一级科目的第一个编号����
	  							//opt，1是期末余额，2是期初余额，3是借方发生额, 4是贷方发生额�Ǵ����
     for(int i=1;i<8;i++){//遍历每个一级科目����Ŀ
       String sqlResult = null;
       if(month!=null&&!"".equals(month)){
         sqlResult =
             "select Sum(balance) as Remain, ";
         sqlResult = sqlResult +
             "Sum(a.DebitRemain+a.CreditRemain) as InitRemain,";
         sqlResult = sqlResult + "Sum(a.DebitOcc) as DebitOcc,";
         sqlResult = sqlResult + "Sum(a.CreditOcc) as CreditOcc ";
         sqlResult = sqlResult +
             " from c_account a,c_accpkgsubject b,c_accpackage c ";
         sqlResult = sqlResult + " where a.SubjectID like '" + i +
             "%' ";

         sqlResult = sqlResult + " and a.SubMonth=" + month;

         sqlResult = sqlResult + " and b.level0=1 and c.CustomerID=" +
             customerID;
         sqlResult = sqlResult + " and c.AccpackageYear=" + year;
         sqlResult = sqlResult + " and a.SubjectID=b.SubjectID and a.AccPackageID=b.AccPackageID and b.AccPackageID=c.AccPackageID ";
       }else{
         sqlResult =
             "select sum(a.balance) as Remain, ";
         sqlResult = sqlResult +
             "sum(d.DebitRemain+d.CreditRemain) as InitRemain,";
         sqlResult = sqlResult + "sum(a.DebittotalOcc) as DebitOcc,";
         sqlResult = sqlResult + "sum(a.CredittotalOcc) as CreditOcc ";
         sqlResult = sqlResult +
             " from c_account a,c_accpkgsubject b,c_accpackage c ,c_account d";
         sqlResult = sqlResult + " where a.SubjectID like '" + i +
             "%' ";

         sqlResult = sqlResult + " and a.SubMonth=12";
         sqlResult = sqlResult + " and d.SubMonth=1";
         sqlResult = sqlResult + " and b.level0=1 and c.CustomerID=" +
             customerID;
         sqlResult = sqlResult + " and c.AccpackageYear=" + year;
         sqlResult = sqlResult + " and a.subjectid=d.subjectid and a.accpackageid=d.accpackageid ";
         sqlResult = sqlResult + " and a.SubjectID=b.SubjectID and a.AccPackageID=b.AccPackageID and b.AccPackageID=c.AccPackageID ";
       }
//     zyq
       if(i==7) {
    	   if(month!=null&&!"".equals(month)){
    	         sqlResult =
    	             "select Sum(balance) as Remain, ";
    	         sqlResult = sqlResult +
    	             "Sum(a.DebitRemain+a.CreditRemain) as InitRemain,";
    	         sqlResult = sqlResult + "Sum(a.DebitOcc) as DebitOcc,";
    	         sqlResult = sqlResult + "Sum(a.CreditOcc) as CreditOcc ";
    	         sqlResult = sqlResult +
    	             " from c_account a,c_accpkgsubject b,c_accpackage c ";
    	         sqlResult = sqlResult + " where substring(a.SubjectID,1,1)>6";
    	             

    	         sqlResult = sqlResult + " and a.SubMonth=" + month;

    	         sqlResult = sqlResult + " and b.level0=1 and c.CustomerID=" +
    	             customerID;
    	         sqlResult = sqlResult + " and c.AccpackageYear=" + year;
    	         sqlResult = sqlResult + " and a.SubjectID=b.SubjectID and a.AccPackageID=b.AccPackageID and b.AccPackageID=c.AccPackageID ";
    	       }else{
    	         sqlResult =
    	             "select sum(a.balance) as Remain, ";
    	         sqlResult = sqlResult +
    	             "sum(d.DebitRemain+d.CreditRemain) as InitRemain,";
    	         sqlResult = sqlResult + "sum(a.DebittotalOcc) as DebitOcc,";
    	         sqlResult = sqlResult + "sum(a.CredittotalOcc) as CreditOcc ";
    	         sqlResult = sqlResult +
    	             " from c_account a,c_accpkgsubject b,c_accpackage c ,c_account d";
    	         sqlResult = sqlResult + " where substring(a.SubjectID,1,1)>6";

    	         sqlResult = sqlResult + " and a.SubMonth=12";
    	         sqlResult = sqlResult + " and d.SubMonth=1";
    	         sqlResult = sqlResult + " and b.level0=1 and c.CustomerID=" +
    	             customerID;
    	         sqlResult = sqlResult + " and c.AccpackageYear=" + year;
    	         sqlResult = sqlResult + " and a.subjectid=d.subjectid and a.accpackageid=d.accpackageid ";
    	         sqlResult = sqlResult + " and a.SubjectID=b.SubjectID and a.AccPackageID=b.AccPackageID and b.AccPackageID=c.AccPackageID ";
    	       }
    	   
       }
       org.util.Debug.prtOut(sqlResult);
System.out.println("\n\n\n\n\n"+sqlResult+"\n\n\n\n");
       try {
         rs = stmt.executeQuery(sqlResult);
         if(rs.next()){
           for (int j = 1; j < 5; j++) {  //遍历每个一级科目中: 1是期末余额，2是期初余额，3是借方发生额, 4是贷方发生额�Ǵ����
             if (rs.getString(j) != null)
               remain[i - 1][j - 1] = rs.getString(j);
             else {
               remain[i - 1][j - 1] = "0";
             }
           }
         }else{
           for (int j = 1; j < 5; j++) { //遍历每个一级科目中: 1是期末余额，2是期初余额，3是借方发生额, 4是贷方发生额�Ǵ����
               remain[i - 1][j - 1] = "0";
             }
           }
       }catch (SQLException ex) {
         ex.printStackTrace();
       }
     }
  }




  public void closeConnection(){
    try{
      if(stmt!=null){
        stmt.close();
      }
      /*
      if(conn!=null){
        conn.close();
      }
      */
     }catch (Exception e){
      e.printStackTrace();
     }
  }
  public String getDirection(double args,int type){
	    //type的取值为1和-1,借方为1,贷方为-1
//    if(type>0)type=1;
//    else type=-1;
//    args=args*type;
    DecimalFormat df=new DecimalFormat("0.00");
    args=Double.parseDouble(df.format(args));
    if(args>0)
        return "借";
     else if(args<0)
       return "贷";
     else
       return "平";
  }
  public String getCustomerName(){
     return this.customerName;
  }

  private void setCustomerName(){
    try {
            rs = stmt.executeQuery("select DepartName from k_customer where DepartID="+customerID);
            rs.last();
            if(rs.getRow()>0&&rs.getString(1)!=null)
              this.customerName= rs.getString(1);
            else{
              this.customerName= "无此客户�޴˿ͻ�";
            }
          }
          catch (SQLException ex) {
            ex.printStackTrace();
            this.customerName= "无此客户�޴˿ͻ�";
          }
  }

  public String getAccpackageID(){
    return this.accpackageID;
  }

  private void setAccpackageID(){
	  this.accpackageID=customerID+year;
  }

}
