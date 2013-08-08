package com.matech.audit.service.autoHitSelect.impl;

import java.util.ArrayList;
import java.util.List;

import com.matech.audit.service.autoHitSelect.AutoHitSelect;
import com.matech.audit.service.autoHitSelect.SelectOption;

public class SimpleImpl implements AutoHitSelect {

	public List<SelectOption> getList(String param) {
		
		System.out.println("param:" + param);
		List<SelectOption> list = new ArrayList<SelectOption>();
		
		for (int i = 0; i < 10; i++) {
			SelectOption selectOption = new SelectOption();
			selectOption.setId("id_" + i);
			selectOption.setText("名称" + i);
			selectOption.setValue("值" + i);
			
			list.add(selectOption);
		}
		
		return list;
	}

}
