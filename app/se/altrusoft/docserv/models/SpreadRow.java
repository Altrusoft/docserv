/*
 * Copyright (c) 2013 Altrusoft AB.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package se.altrusoft.docserv.models;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SpreadRow {

	private List<SpreadCell> cells = new ArrayList<>();

	public List<SpreadCell> getCells() {
		return cells;
	}

	public void setCells(List<SpreadCell> cells) {
		this.cells = cells;
	}

}
