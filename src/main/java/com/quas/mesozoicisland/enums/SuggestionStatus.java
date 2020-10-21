package com.quas.mesozoicisland.enums;

public enum SuggestionStatus {
	Pending(0), Accepted(1), Rejected(2);

	private int status;
	private SuggestionStatus(int status) {
		this.status = status;
	}

	public int getId() {
		return status;
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
