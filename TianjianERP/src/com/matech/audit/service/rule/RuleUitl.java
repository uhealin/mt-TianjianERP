package com.matech.audit.service.rule;

public class RuleUitl {

	/**
	 * 对字符串按要求进行格式化
	 * @param title
	 * @return
	 * @throws Exception
	 */
public String replaceAlls(String addResult) throws Exception {
	

	addResult=addResult.replaceAll(" 加 ", "#1#").replaceAll(" 减 ", "#2#").replaceAll(" 乘 ", "#3#").replaceAll(" 除 ", "#4#").replaceAll(" ", "");
	
	addResult=addResult.replaceAll("\\+"," + ").replaceAll("\\-"," - ").replaceAll("\\*", " * ").replaceAll("\\/", " / ").replaceAll("＋", " ＋ ").replaceAll("－", " － ").replaceAll("×", " × ").replaceAll("÷", " ÷ ");

	addResult=" "+addResult.replaceAll("#1#", " 加 ").replaceAll("#2#", " 减 ").replaceAll("#3#", " 乘 ").replaceAll("#4#", " 除 ").replaceAll(","," , ")+" ";
	
	
	
	return addResult;
}	


/**
 * 在计算符号前加换行
 * @param title
 * @return
 * @throws Exception
 */
public String replaceAllsEx(String addResult) throws Exception {


addResult=addResult.replaceAll(" 加 ", "<br>加 ").replaceAll(" 减 ", "<br>减 ").replaceAll(" 乘 ", "<br>乘 ").replaceAll(" 除 ", "<br>除 ");

addResult=addResult.replaceAll(" \\+ ","<br>+ ").replaceAll("\\-","<br>- ").replaceAll("\\*", "<br>* ").replaceAll("\\/", "<br>/ ").replaceAll("＋", "<br>＋ ").replaceAll("－", "<br>－ ").replaceAll("×", "<br>× ").replaceAll("÷", "<br>÷ ");



return addResult;
}	

/**
 * 把{}之外的部分的符号全部替换
 * @param title
 * @return
 * @throws Exception
 */
public String replaceAllOutOf(String addResult) throws Exception {

	addResult=addResult.replaceAll("\\{", "#5#").replaceAll("\\}", "#5#");
	String[] addResults = addResult.split("#5#");
	String ccc="";
	for(int i=0;i<addResults.length;i++){//for循环的作用是把{}之外的部分的符号全部替换
		if(addResults[i].trim()!=""){
			if(i%2==0){
				if(i!=addResults.length-1){
					addResults[i]=replaceAllsEx(replaceAlls(addResults[i]));
					ccc=ccc+addResults[i]+"{";
				}else{
					addResults[i]=replaceAllsEx(replaceAlls(addResults[i]));
					ccc=ccc+addResults[i];
				}
				continue;
			}
			ccc=ccc+addResults[i]+"}";
		}
	}
	ccc=ccc.replaceAll("取值 ", "取值");
	return ccc;
	}	


public static void main(String[] args) {
	
	RuleUitl ruleUitl=new RuleUitl();
	try {
		
		String aaa=ruleUitl.replaceAllOutOf("取值{取数方式;项目或帐套编号;\"物资采购\";指标属性;年份;月份;币种} + 取值{取数方式;项目或帐套编号;\"在途物资\";指标属性;年份;月份;币种} + 取值{取数方式;项目或帐套编号;\"原材料\";指标属性;年份;月份;币种} + 取值{取数方式;项目或帐套编号;\"包装物\";指标属性;年份;月份;币种} + 取值{取数方式;项目或帐套编号;\"低值易耗品\";指标属性;年份;月份;币种} + 取值{取数方式;项目或帐套编号;\"库存商品\";指标属性;年份;月份;币种} + 取值{取数方式;项目或帐套编号;\"商品进销差价\";指标属性;年份;月份;币种} + 取值{取数方式;项目或帐套编号;\"产成品成本差异\";指标属性;年份;月份;币种} + 取值{取数方式;项目或帐套编号;\"委托加工物资\";指标属性;年份;月份;币种} + 取值{取数方式;项目或帐套编号;\"委托代销商品\";指标属性;年份;月份;币种} + 取值{取数方式;项目或帐套编号;\"受托代销商品\";指标属性;年份;月份;币种} + 取值{取数方式;项目或帐套编号;\"发出商品\";指标属性;年份;月份;币种} + 取值{取数方式;项目或帐套编号;\"待转库存商品差价\";指标属性;年份;月份;币种} + 取值{取数方式;项目或帐套编号;\"生产成本\";指标属性;年份;月份;币种} + 取值{取数方式;项目或帐套编号;\"材料成本差异\";指标属性;年份;月份;币种}+ 取值{取数方式;项目或帐套编号;\"半成品\";指标属性;年份;月份;币种}");
	System.out.println("yzm:aaa="+aaa);
	} catch (Exception e) {
		// TODO 自动生成 catch 块
		e.printStackTrace();
	}
	
}
}

