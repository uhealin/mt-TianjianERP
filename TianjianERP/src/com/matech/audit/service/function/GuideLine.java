package com.matech.audit.service.function;

import java.util.*;

//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//
//import com.matech.framework.pub.db.DbUtil;

public class GuideLine {

	private String[] Property = null;		//指标的属性
	private String subjectID = "";		//科目编号
	private String assitemID = "";		//核算编号
	private String mygroup="";
	
	public int numYear = 5;	//账龄区间	
	public String [] split = new String[]{"one","two","three","four","five"};	//历年调整字段的分隔符
	
	/*
	public static final Map map = new HashMap(); //指标的属性结合
	static {
		
		GuideLineProperty gp=new GuideLineProperty("remain","1");
		map.put("期初数",gp );
		gp=new GuideLineProperty("DebitRemain","1");
		map.put("借方期初数",gp );
		gp=new GuideLineProperty("CreditRemain","1");
		map.put("贷方期初数",gp );
		gp=new GuideLineProperty("DebitOcc","1");
		map.put("借发生",gp );
		gp=new GuideLineProperty("CreditOcc","1");
		map.put("贷发生",gp );
		gp=new GuideLineProperty("Occ","1");
		map.put("净发生",gp);
		gp=new GuideLineProperty("Balance","1");
		map.put("期末数",gp);
		gp=new GuideLineProperty("DebitBalance","1");
		map.put("借方期末数",gp);
		gp=new GuideLineProperty("CreditBalance","1");
		map.put("贷方期末数",gp);
		gp=new GuideLineProperty("remainF","1");
		map.put("本位币期初数",gp );
		gp=new GuideLineProperty("DebitRemainF","1");
		map.put("本位币借方期初数",gp );
		gp=new GuideLineProperty("CreditRemainF","1");
		map.put("本位币贷方期初数",gp);
		gp=new GuideLineProperty("DebitOccF","1");
		map.put("本位币借发生",gp );
		gp=new GuideLineProperty("CreditOccF","1");
		map.put("本位币贷发生",gp );
		gp=new GuideLineProperty("OccF","1");
		map.put("本位币净发生",gp);
		gp=new GuideLineProperty("BalanceF","1");
		map.put("本位币期末数",gp );
		gp=new GuideLineProperty("DebitBalanceF","1");
		map.put("本位币借方期末数",gp );
		gp=new GuideLineProperty("CreditBalanceF","1");
		map.put("本位币贷方期末数",gp );
		
		gp=new GuideLineProperty("yearremain","2");
		map.put("年初数",gp);
		gp=new GuideLineProperty("yearDebitRemain","2");
		map.put("年借方期初数",gp);
		gp=new GuideLineProperty("yearCreditRemain","2");
		map.put("年贷方期初数",gp );
		gp=new GuideLineProperty("yearDebitOcc","2");
		map.put("年借发生",gp );
		gp=new GuideLineProperty("yearCreditOcc","2");
		map.put("年贷发生",gp );
		gp=new GuideLineProperty("yearOcc","2");
		map.put("年净发生",gp);
		gp=new GuideLineProperty("yearBalance","2");
		map.put("年末数",gp );
		gp=new GuideLineProperty("yearDebitBalance","2");
		map.put("年借方期末数",gp );
		gp=new GuideLineProperty("yearCreditBalance","2");
		map.put("年贷方期末数",gp );
		gp=new GuideLineProperty("yearremainF","2");
		map.put("本位币年初数",gp );
		gp=new GuideLineProperty("yearDebitRemainF","2");
		map.put("本位币年借方期初数",gp );
		gp=new GuideLineProperty("yearCreditRemainF","2");
		map.put("本位币年贷方期初数",gp);
		gp=new GuideLineProperty("yearDebitOccF","2");
		map.put("本位币年借发生",gp );
		gp=new GuideLineProperty("yearCreditOccF","2");
		map.put("本位币年贷发生",gp );
		gp=new GuideLineProperty("yearOccF","2");
		map.put("本位币年净发生",gp);
		gp=new GuideLineProperty("yearBalanceF","2");
		map.put("本位币年末数",gp );
		gp=new GuideLineProperty("yearDebitBalanceF","2");
		map.put("本位币年借方期末数",gp );
		gp=new GuideLineProperty("yearCreditBalanceF","2");
		map.put("本位币年贷方期末数",gp );
		
		gp=new GuideLineProperty("sdremain","3");
		map.put("审定期初",gp );
		gp=new GuideLineProperty("sdbalance","3");
		map.put("审定期末",gp );
		
		//账龄
		gp=new GuideLineProperty("Day0","4");
		map.put("1年以内",gp );
		gp=new GuideLineProperty("Day1","4");
		map.put("1年到2年",gp );
		gp=new GuideLineProperty("Day2","4");
		map.put("2年到3年",gp );
		gp=new GuideLineProperty("Day3","4");
		map.put("3年到4年",gp );
		gp=new GuideLineProperty("Day4","4");
		map.put("4年到5年",gp );
		gp=new GuideLineProperty("Day5","4");
		map.put("5年以上",gp );
		
		gp=new GuideLineProperty("sdDay0","5");
		map.put("审定1年以内",gp );
		gp=new GuideLineProperty("sdDay1","5");
		map.put("审定1年到2年",gp );
		gp=new GuideLineProperty("sdDay2","5");
		map.put("审定2年到3年",gp );
		gp=new GuideLineProperty("sdDay3","5");
		map.put("审定3年到4年",gp );
		gp=new GuideLineProperty("sdDay4","5");
		map.put("审定4年到5年",gp );
		gp=new GuideLineProperty("sdDay5","5");
		map.put("审定5年以上",gp );
		
		//调整
		gp=new GuideLineProperty("DebitTotalOcc1","6");
		map.put("期末调整借",gp );
		gp=new GuideLineProperty("CreditTotalOcc1","6");
		map.put("期末调整贷",gp );
		gp=new GuideLineProperty("DebitTotalOcc2","6");
		map.put("期末重分类借",gp );
		gp=new GuideLineProperty("CreditTotalOcc2","6");
		map.put("期末重分类贷",gp );
		gp=new GuideLineProperty("DebitTotalOcc3","6");
		map.put("期末不符未调借",gp );
		gp=new GuideLineProperty("CreditTotalOcc3","6");
		map.put("期末不符未调贷",gp );
		gp=new GuideLineProperty("DebitTotalOcc4","6");
		map.put("期初调整借",gp );
		gp=new GuideLineProperty("CreditTotalOcc4","6");
		map.put("期初调整贷",gp );
		gp=new GuideLineProperty("DebitTotalOcc5","6");
		map.put("期初重分类借",gp );
		gp=new GuideLineProperty("CreditTotalOcc5","6");
		map.put("期初重分类贷",gp );
		gp=new GuideLineProperty("DebitTotalOcc0","6");
		map.put("期初不符未调借",gp );
		gp=new GuideLineProperty("CreditTotalOcc0","6");
		map.put("期初不符未调贷",gp );
		gp=new GuideLineProperty("DebitTotalOcc6","6");
		map.put("账表不符借" ,gp );
		gp=new GuideLineProperty("CreditTotalOcc6","6");
		map.put("账表不符贷",gp );
		
		//历年调整
		gp=new GuideLineProperty("oneDebitTotalOcc1","7");
		map.put("1年前期末调整借",gp );
		gp=new GuideLineProperty("oneCreditTotalOcc1","7");
		map.put("1年前期末调整贷",gp );
		gp=new GuideLineProperty("oneDebitTotalOcc2","7");
		map.put("1年前期末重分类借",gp );
		gp=new GuideLineProperty("oneCreditTotalOcc2","7");
		map.put("1年前期末重分类贷",gp);
		gp=new GuideLineProperty("oneDebitTotalOcc3","7");
		map.put("1年前期末不符未调借",gp );
		gp=new GuideLineProperty("oneCreditTotalOcc3","7");
		map.put("1年前期末不符未调贷",gp );
		gp=new GuideLineProperty("oneDebitTotalOcc4","7");
		map.put("1年前期初调整借",gp );
		gp=new GuideLineProperty("oneCreditTotalOcc4","7");
		map.put("1年前期初调整贷",gp);
		gp=new GuideLineProperty("oneDebitTotalOcc5","7");
		map.put("1年前期初重分类借",gp );
		gp=new GuideLineProperty("oneCreditTotalOcc5","7");
		map.put("1年前期初重分类贷",gp );
		gp=new GuideLineProperty("oneDebitTotalOcc0","7");
		map.put("1年前期初不符未调借",gp);
		gp=new GuideLineProperty("oneCreditTotalOcc0","7");
		map.put("1年前期初不符未调贷",gp);
		gp=new GuideLineProperty("oneDebitTotalOcc6","7");
		map.put("1年前账表不符借" ,gp );
		gp=new GuideLineProperty("oneCreditTotalOcc6","7");
		map.put("1年前账表不符贷",gp);
		
		gp=new GuideLineProperty("twoDebitTotalOcc1","8");
		map.put("2年前期末调整借",gp );
		gp=new GuideLineProperty("twoCreditTotalOcc1","8");
		map.put("2年前期末调整贷",gp );
		gp=new GuideLineProperty("twoDebitTotalOcc2","8");
		map.put("2年前期末重分类借",gp );
		gp=new GuideLineProperty("twoCreditTotalOcc2","8");
		map.put("2年前期末重分类贷",gp);
		gp=new GuideLineProperty("twoDebitTotalOcc3","8");
		map.put("2年前期末不符未调借",gp );
		gp=new GuideLineProperty("twoCreditTotalOcc3","8");
		map.put("2年前期末不符未调贷",gp );
		gp=new GuideLineProperty("twoDebitTotalOcc4","8");
		map.put("2年前期初调整借",gp );
		gp=new GuideLineProperty("twoCreditTotalOcc4","8");
		map.put("2年前期初调整贷",gp);
		gp=new GuideLineProperty("twoDebitTotalOcc5","8");
		map.put("2年前期初重分类借",gp );
		gp=new GuideLineProperty("twoCreditTotalOcc5","8");
		map.put("2年前期初重分类贷",gp );
		gp=new GuideLineProperty("twoDebitTotalOcc0","8");
		map.put("2年前期初不符未调借",gp);
		gp=new GuideLineProperty("twoCreditTotalOcc0","8");
		map.put("2年前期初不符未调贷",gp);
		gp=new GuideLineProperty("twoDebitTotalOcc6","8");
		map.put("2年前账表不符借" ,gp );
		gp=new GuideLineProperty("twoCreditTotalOcc6","8");
		map.put("2年前账表不符贷",gp);
		
		gp=new GuideLineProperty("threeDebitTotalOcc1","9");
		map.put("3年前期末调整借",gp );
		gp=new GuideLineProperty("threeCreditTotalOcc1","9");
		map.put("3年前期末调整贷",gp );
		gp=new GuideLineProperty("threeDebitTotalOcc2","9");
		map.put("3年前期末重分类借",gp );
		gp=new GuideLineProperty("threeCreditTotalOcc2","9");
		map.put("3年前期末重分类贷",gp);
		gp=new GuideLineProperty("threeDebitTotalOcc3","9");
		map.put("3年前期末不符未调借",gp );
		gp=new GuideLineProperty("threeCreditTotalOcc3","9");
		map.put("3年前期末不符未调贷",gp );
		gp=new GuideLineProperty("threeDebitTotalOcc4","9");
		map.put("3年前期初调整借",gp );
		gp=new GuideLineProperty("threeCreditTotalOcc4","9");
		map.put("3年前期初调整贷",gp);
		gp=new GuideLineProperty("threeDebitTotalOcc5","9");
		map.put("3年前期初重分类借",gp );
		gp=new GuideLineProperty("threeCreditTotalOcc5","9");
		map.put("3年前期初重分类贷",gp);
		gp=new GuideLineProperty("threeDebitTotalOcc0","9");
		map.put("3年前期初不符未调借",gp);
		gp=new GuideLineProperty("threeCreditTotalOcc0","9");
		map.put("3年前期初不符未调贷",gp);
		gp=new GuideLineProperty("threeDebitTotalOcc6","9");
		map.put("3年前账表不符借" ,gp);
		gp=new GuideLineProperty("threeCreditTotalOcc6","9");
		map.put("3年前账表不符贷",gp);
		
		gp=new GuideLineProperty("fourDebitTotalOcc1","a");
		map.put("4年前期末调整借",gp );
		gp=new GuideLineProperty("fourCreditTotalOcc1","a");
		map.put("4年前期末调整贷",gp );
		gp=new GuideLineProperty("fourDebitTotalOcc2","a");
		map.put("4年前期末重分类借",gp );
		gp=new GuideLineProperty("fourCreditTotalOcc2","a");
		map.put("4年前期末重分类贷",gp);
		gp=new GuideLineProperty("fourDebitTotalOcc3","a");
		map.put("4年前期末不符未调借",gp );
		gp=new GuideLineProperty("fourCreditTotalOcc3","a");
		map.put("4年前期末不符未调贷",gp );
		gp=new GuideLineProperty("fourDebitTotalOcc4","a");
		map.put("4年前期初调整借",gp );
		gp=new GuideLineProperty("fourCreditTotalOcc4","a");
		map.put("4年前期初调整贷",gp);
		gp=new GuideLineProperty("fourDebitTotalOcc5","a");
		map.put("4年前期初重分类借",gp );
		gp=new GuideLineProperty("fourCreditTotalOcc5","a");
		map.put("4年前期初重分类贷",gp );
		gp=new GuideLineProperty("fourDebitTotalOcc0","a");
		map.put("4年前期初不符未调借",gp);
		gp=new GuideLineProperty("fourCreditTotalOcc0","a");
		map.put("4年前期初不符未调贷",gp);
		gp=new GuideLineProperty("fourDebitTotalOcc6","a");
		map.put("4年前账表不符借" ,gp );
		gp=new GuideLineProperty("fourCreditTotalOcc6","a");
		map.put("4年前账表不符贷",gp);
		
		gp=new GuideLineProperty("fiveDebitTotalOcc1","b");
		map.put("5年前期末调整借",gp );
		gp=new GuideLineProperty("fiveCreditTotalOcc1","b");
		map.put("5年前期末调整贷",gp );
		gp=new GuideLineProperty("fiveDebitTotalOcc2","b");
		map.put("5年前期末重分类借",gp );
		gp=new GuideLineProperty("fiveCreditTotalOcc2","b");
		map.put("5年前期末重分类贷",gp);
		gp=new GuideLineProperty("fiveDebitTotalOcc3","b");
		map.put("5年前期末不符未调借",gp );
		gp=new GuideLineProperty("fiveCreditTotalOcc3","b");
		map.put("5年前期末不符未调贷",gp );
		gp=new GuideLineProperty("fiveDebitTotalOcc4","b");
		map.put("5年前期初调整借",gp );
		gp=new GuideLineProperty("fiveCreditTotalOcc4","b");
		map.put("5年前期初调整贷",gp);
		gp=new GuideLineProperty("fiveDebitTotalOcc5","b");
		map.put("5年前期初重分类借",gp );
		gp=new GuideLineProperty("fiveCreditTotalOcc5","b");
		map.put("5年前期初重分类贷",gp );
		gp=new GuideLineProperty("fiveDebitTotalOcc0","b");
		map.put("5年前期初不符未调借",gp);
		gp=new GuideLineProperty("fiveCreditTotalOcc0","b");
		map.put("5年前期初不符未调贷",gp);
		gp=new GuideLineProperty("fiveDebitTotalOcc6","b");
		map.put("5年前账表不符借" ,gp );
		gp=new GuideLineProperty("fiveCreditTotalOcc6","b");
		map.put("5年前账表不符贷",gp);
		
		//结转数
		gp=new GuideLineProperty("yearOcc","c");
		map.put("结转数",gp);
		
	}
	*/
	
	
	public String getGroup() {
		return mygroup;
	}

	public void setGroup(String Group) {
		this.mygroup = Group;
	}
	
	
	public String getAssitemID() {
		return assitemID;
	}

	public void setAssitemID(String assitemID) {
		this.assitemID = assitemID;
	}

	public String[] getProperty() {
		return Property;
	}

	public void setProperty(String[] propertys) {
		Property = propertys;
	}

	public String getSubjectID() {
		return subjectID;
	}

	public void setSubjectID(String subjectID) {
		this.subjectID = subjectID;
	}


	
}
