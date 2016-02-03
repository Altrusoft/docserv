/*
 * Copyright (c) 2016 Altrusoft AB.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package se.altrusoft.docserv.models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Menu extends TemplateModel {

	private static String[] FIELDS_TO_TRANSLATE = new String[] {};

	@Override
	public String getInTemplateDesignation() {
		return "menu";
	}

	@Override
	public String[] fieldsToTranslate() {
		return FIELDS_TO_TRANSLATE;
	}

	@Override
	public void expandModel() {
		
		if (entries != null) {
			Map<Date, DayMenu> dayMenusMap = new HashMap<>();
			
			for (MenuEntry e : entries) {
				fromDate = firstDateOf(fromDate, e.entryDate);
				toDate = lastDateOf(toDate, e.entryDate);				
				ensureDayMenu(dayMenusMap,e.entryDate).addMenuEntry(e);
			}
			dayMenus = new ArrayList<>();
			if (fromDate != null) {
				Calendar current = Calendar.getInstance();
				current.setTime(fromDate);
				Calendar end = Calendar.getInstance();
				end.setTime(toDate);

				while (!current.after(end)) {
					dayMenus.add(ensureDayMenu(dayMenusMap,current.getTime()));
					current.add(Calendar.DATE, 1);
				}
			}
		}
	}



	public String location;
	
	public String mealGroup;
	

	public List<MenuEntry> entries;
	
	public String getFromDatePP() {
		return (fromDate != null) ? formatter.format(fromDate) : "-";
	}
	
	@JsonIgnore
	public Date fromDate;
	
	public String getToDatePP() {
		return (toDate != null) ? formatter.format(toDate) : "-";
	}
	
	@JsonIgnore
	public Date toDate;
	
	/**
	 * A sorted list with one entry for each day from fromDate to toDate of
	 * DayMenu objects where all MenuEntry objects have been grouped.
	 */
	@JsonIgnore
	public List<DayMenu> dayMenus = new ArrayList<>();
	
	/* Implementation below */
	
	private static DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	
	private DayMenu ensureDayMenu(Map<Date, DayMenu> m, Date d) {
		if (m.containsKey(d)) {
			return  m.get(d);
		}
		DayMenu result = new DayMenu(d);
		m.put(d,result);
		return result;		
	}
	
	private Date lastDateOf(Date a, Date b) {
		if(a==null) {
			return b;
		}
		return (a.before(b))?b:a;
	}
	
	private Date firstDateOf(Date a, Date b) {
		if(a==null) {
			return b;
		}
		return (a.after(b))?b:a;
	}

}
