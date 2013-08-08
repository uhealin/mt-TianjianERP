package com.matech.audit.service.autoHitSelect.impl;

import java.util.ArrayList;
import java.util.List;

import com.matech.audit.service.autoHitSelect.AutoHitSelect;
import com.matech.audit.service.autoHitSelect.SelectOption;
import com.matech.framework.pub.util.StringUtil;

/**
 * 选择年份
 * @author void
 *
 */
public class SelectYearImpl implements AutoHitSelect{

	public List<SelectOption> getList(String param) {
		List<SelectOption> list = new ArrayList<SelectOption>();

		
		String curYear = StringUtil.getCurYear();
		int year = Integer.parseInt(curYear);
		
		for (int i = year-10; i < year + 10; i++) {
			SelectOption option = new SelectOption();
			option.setId(String.valueOf(i));
			option.setText(String.valueOf(i));
			option.setValue(String.valueOf(i));
			
			list.add(option);
			
		}
		return list;
	}
}
