/*
 * Copyright (c) 2016 Altrusoft AB. This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain
 * one at http://mozilla.org/MPL/2.0/.
 */
package se.altrusoft.docserv.models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DaySchedule {

	private static DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

	private static DateFormat dayFormater = new SimpleDateFormat("EEEE");

	public Date entryDate;

	@JsonIgnore
	public List<ScheduleEntry> entries;

	public DaySchedule(Date entryDate) {
		this.entryDate = entryDate;
		entries = new ArrayList<>();

	}

	public void addScheduleEntry(ScheduleEntry scheduleEntry) {
		entries.add(scheduleEntry);
	}

	public String getEntryDatePP() {
		return (entryDate != null) ? formatter.format(entryDate) : "-";
	}

	public String getEntryDayPP() {
		return (entryDate != null) ? StringUtils.capitalize(dayFormater.format(entryDate)) : "-";
	}

}
