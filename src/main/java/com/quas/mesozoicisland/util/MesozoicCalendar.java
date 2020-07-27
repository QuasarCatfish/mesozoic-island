package com.quas.mesozoicisland.util;

import java.util.GregorianCalendar;
import java.util.TimeZone;

public class MesozoicCalendar extends GregorianCalendar {

	private static final long serialVersionUID = -7740750099456348084L;

	public MesozoicCalendar() {
		super.setTimeZone(TimeZone.getTimeZone("UTC"));
	}
}
