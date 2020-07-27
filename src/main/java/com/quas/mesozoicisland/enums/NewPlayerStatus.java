package com.quas.mesozoicisland.enums;

import com.quas.mesozoicisland.JDBC;

public enum NewPlayerStatus {
	New, Returning, NewToVersion, Error;
	
	public static NewPlayerStatus of(long pid) {
		return JDBC.addPlayer(pid);
	}
}
