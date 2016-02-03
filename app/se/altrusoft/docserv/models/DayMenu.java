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
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DayMenu  {
	
	public DayMenu(Date entryDate) {
		this.entryDate = entryDate; 
		lunchEntries = new ArrayList<>();
		dinnerEntries = new ArrayList<>();
		otherEntries = new ArrayList<>();
		
	}

	public void addMenuEntry(MenuEntry menuEntry) {
		if (menuEntry.meal.equalsIgnoreCase("Lunch")) {
			this.lunchEntries.add(menuEntry);
		} else if (menuEntry.meal.equalsIgnoreCase("Dinner")) {
			this.dinnerEntries.add(menuEntry);
		} else {
			this.otherEntries.add(menuEntry);
		}
	}
	
	public String getEntryDatePP() {
		return (entryDate != null) ? formatter.format(entryDate) : "-";
	}
	
	public Date entryDate;
	
	@JsonIgnore
	public List<MenuEntry> lunchEntries;
	
	@JsonIgnore
	public List<MenuEntry> dinnerEntries;
	
	@JsonIgnore
	public List<MenuEntry> otherEntries;
	
	private static DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

}
