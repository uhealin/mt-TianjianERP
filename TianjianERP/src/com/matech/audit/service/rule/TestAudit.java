package com.matech.audit.service.rule;

public class TestAudit {
	public static Object test(Integer par1, Integer par2) throws Exception {
		return new Integer(par1.intValue() * par2.intValue());
	}

	public static void main(String[] args) {
		try {
			System.out.println(TestAudit.test(new Integer(5),new Integer(5)));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
