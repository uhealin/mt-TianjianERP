package com.matech.audit.pub.func;

//下面的类提供了两个静态方法，用于检查&修复HTML标签 ：



public class TagsHtml 
{
   public static void main(String[] args)
   {
       System.out.println("--功能测试--");
       String str1 = "<a>ttss</a>aa<div name=\"\" id='3'>sff";
      // String str2 = "tt<u>ss</u><div id=test name=\"<test>\"><a>fds</a></div>";
       System.out.println("检查文本 " + str1);
       System.out.println("结果：" + TagsChecker.check(str1));
       //System.out.println("检查文本 " + str2);
       //System.out.println("结果：" + TagsChecker.check(str2));
       System.out.println("结果：" + TagsChecker.fix(str1));
       
      /* for (int i = 0; i < 10; i++) {
           str1 += str1;
       }*/
       
       System.out.println();
       System.out.println("--效率测试--");
       System.out.println("文本长度：" + str1.length());
       long t1 = System.currentTimeMillis();
       boolean closed = TagsChecker.check(str1);
       long t2 = System.currentTimeMillis();
       String fixedStr = TagsChecker.fix(str1);
       long t3 = System.currentTimeMillis(); 
       System.out.println("检查用时：" + (t2 - t1) + " 毫秒 结果：" + closed);
       System.out.println("修复用时：" + (t3 - t2) + " 毫秒");
   }

} 
