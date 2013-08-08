/*     */ package com.matech.audit.service.user;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.Random;
/*     */ 
/*     */ public class EnCodeUtils
/*     */ {
/*     */   public String uCode(String s)
/*     */   {
/*  14 */     int i = s.length();
/*  15 */     String s2 = "";
/*  16 */     if (i == 0)
/*  17 */       return "";
/*  18 */     for (int k = 0; k < i; k++)
/*     */     {
/*  20 */       String s1 = s.substring(k, k + 1);
/*  21 */       int j = s1.hashCode();
/*  22 */       if (j > 255)
/*  23 */         s2 = s2 + String.valueOf((char)(j >> 8)) + String.valueOf((char)(j & 0xFF));
/*     */       else {
/*  25 */         s2 = s2 + String.valueOf('\000') + s1;
/*     */       }
/*     */     }
/*  28 */     return s2;
/*     */   }
/*     */ 
/*     */   public String uDCode(String s)
/*     */   {
/*  33 */     int i = s.length();
/*  34 */     if (i % 2 != 0)
/*  35 */       return "-1";
/*  36 */     String s1 = "";
/*  37 */     for (int j = 0; j < i / 2; j++) {
/*  38 */       s1 = s1 + String.valueOf((char)((s.charAt(j * 2) << '\b') + s.charAt(j * 2 + 1)));
/*     */     }
/*  40 */     return s1;
/*     */   }
/*     */ 
/*     */   public String encodePWS(String s)
/*     */   {
/*  45 */     if (s == null)
/*  46 */       return null;
/*  47 */     int i = s.length();
/*  48 */     if (i == 0)
/*  49 */       return "";
/*  50 */     s = uCode(s);
/*  51 */     i = s.length();
/*  52 */     String s4 = "";
/*  53 */     String s5 = "";
/*  54 */     Random random = new Random();
/*  55 */     boolean flag = false;
/*  56 */     for (int j = 0; j < i; j++)
/*     */     {
/*  58 */       String s1 = Integer.toHexString(s.substring(j, j + 1).hashCode() >> 4);
/*  59 */       String s2 = Integer.toHexString(s.substring(j, j + 1).hashCode() & 0xF);
/*  60 */       String s3 = Integer.toString(random.nextInt());
/*  61 */       s3 = s3.substring(s3.length() - 1, s3.length());
/*  62 */       if (j % 3 == 0) {
/*  63 */         s4 = s4 + s3 + s1 + s2;
/*     */       }
/*  65 */       else if (j % 3 == 1)
/*  66 */         s4 = s4 + s1 + s3 + s2;
/*     */       else {
/*  68 */         s4 = s4 + s1 + s2 + s3;
/*     */       }
/*     */     }
/*  71 */     return s4;
/*     */   }
/*     */ 
/*     */   public String decodePWS(String s)
/*     */   {
/*  76 */     if (s == null)
/*  77 */       return null;
/*  78 */     int i = s.length();
/*  79 */     if (i == 0)
/*  80 */       return "";
/*  81 */     String s1 = "";
/*  82 */     String s2 = "";
/*  83 */     String s3 = "";
/*  84 */     s2 = s;
/*  85 */     if (s2.length() % 3 != 0)
/*  86 */       return "-1";
/*  87 */     for (int j = 0; j < s2.length() / 3; j++) {
/*  88 */       if (j % 3 == 0) {
/*  89 */         s1 = s1 + s2.substring(j * 3 + 1, (j + 1) * 3);
/*     */       }
/*  91 */       else if (j % 3 == 1)
/*  92 */         s1 = s1 + s2.substring(j * 3, j * 3 + 1) + s2.substring(j * 3 + 2, (j + 1) * 3);
/*     */       else
/*  94 */         s1 = s1 + s2.substring(j * 3, j * 3 + 2);
/*     */     }
/*  96 */     for (int k = 0; k < s1.length() / 2; k++) {
/*  97 */       s3 = s3 + String.valueOf((char)((Char2int(s1.charAt(k * 2)) << 4) + Char2int(s1.charAt(k * 2 + 1))));
/*     */     }
/*  99 */     s3 = uDCode(s3);
/* 100 */     return s3;
/*     */   }
/*     */ 
/*     */   public int Char2int(char c)
/*     */   {
/* 105 */     if ((c >= '0') && (c <= '9'))
/* 106 */       return c - '0';
/* 107 */     if ((c >= 'a') && (c <= 'f')) {
/* 108 */       return c - 'a' + 10;
/*     */     }
/* 110 */     return -1;
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/* 115 */     EnCodeUtils encode = new EnCodeUtils();
/* 116 */     String s = encode.encodePWS("7783453534534433534356445646");
/* 117 */     System.out.println(s);
/* 118 */     System.out.println("The encode lenth is " + s.length());
/* 119 */     System.out.println(encode.decodePWS(s));
/* 120 */     System.out.println(encode.decodePWS("900361003632020336900791002177090654500782005774080794"));
/*     */   }
/*     */ }

/* Location:           C:\Users\hhlin\Desktop\LDAP同步2012-12-31-2000\复件 LDAP同步\webRoot\AuditSystem\WEB-INF\classes\
 * Qualified Name:     com.matech.audit.service.user.EnCodeUtils
 * JD-Core Version:    0.6.0
 */