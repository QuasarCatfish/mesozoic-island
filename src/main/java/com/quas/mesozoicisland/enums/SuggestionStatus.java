package com.quas.mesozoicisland.enums;

import java.awt.Color;

import com.quas.mesozoicisland.util.Constants;

public enum SuggestionStatus {
	Pending(0, Constants.COLOR, true),
	Accepted(1, Color.green, false),
	Rejected(2, Color.red, false),
	Planned(3, Color.yellow, true);

	private int status;
	private Color color;
	private boolean edit;
	private SuggestionStatus(int status, Color color, boolean edit) {
		this.status = status;
		this.color = color;
		this.edit = edit;
	}

	public int getId() {
		return status;
	}

	public Color getColor() {
		return color;
	}

	public boolean isEdit() {
		return edit;
	}

	//////////////////////////////////////

	public static SuggestionStatus of(int status) {
		for (SuggestionStatus ss : values()) {
			if (ss.status == status) {
				return ss;
			}
		}
		return null;
	}
}
