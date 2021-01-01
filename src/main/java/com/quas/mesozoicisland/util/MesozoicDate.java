package com.quas.mesozoicisland.util;

public class MesozoicDate implements Comparable<MesozoicDate> {

	private String year, month, day;
	private int iyear, imonth, iday;
	
	public MesozoicDate(long millis) {
		MesozoicCalendar gc = new MesozoicCalendar();
		gc.setTimeInMillis(millis);
		year = String.format("%tY", gc);
		iyear = Integer.parseInt(year);
		month = String.format("%tm", gc);
		imonth = Integer.parseInt(month);
		day = String.format("%td", gc);
		iday = Integer.parseInt(day);
	}
	
	public MesozoicDate(String date) {
		if (date.length() == 8) {
			year = date.substring(0, 4);
			iyear = Integer.parseInt(year);
			month = date.substring(4, 6);
			imonth = Integer.parseInt(month);
			day = date.substring(6, 8);
			iday = Integer.parseInt(day);
		} else {
			year = "0000";
			iyear = 0;
			month = date.substring(0, 2);
			imonth = Integer.parseInt(month);
			day = date.substring(2, 4);
			iday = Integer.parseInt(day);
		}
	}
	
	public String getYear() {
		return year;
	}
	
	public int getYearInt() {
		return iyear;
	}
	
	public String getMonth() {
		return month;
	}
	
	public int getMonthInt() {
		return imonth;
	}
	
	public String getDay() {
		return day;
	}
	
	public int getDayInt() {
		return iday;
	}
	
	@Override
	public String toString() {
		return toString(true);
	}
	
	public String toString(boolean showYear) {
		if (showYear) return year + month + day;
		return month + day;
	}

	@Override
	public int compareTo(MesozoicDate that) {
		if (this.iyear == that.iyear) {
			if (this.imonth == that.imonth) {
				return this.iday - that.iday;
			} else if (this.imonth > that.imonth) {
				int ret = this.iday + (Util.getDaysInMonth(that.imonth, Util.isLeapYear(that.iyear)) - that.iday);
				for (int month = that.imonth + 1; month < this.imonth; month++) ret += Util.getDaysInMonth(month, Util.isLeapYear(this.iyear));
				return ret;
			} else {
				return -that.compareTo(this);
			}
		} else if (this.iyear > that.iyear) {
			int ret = this.iday;
			for (int month = 1; month < this.imonth; month++) ret += Util.getDaysInMonth(month, Util.isLeapYear(this.iyear));
			ret += Util.getDaysInYear(that.iyear);
			for (int month = 1; month <= that.imonth; month++) ret -= Util.getDaysInMonth(month, Util.isLeapYear(that.iyear));
			for (int year = that.iyear + 1; year < this.iyear; year++) ret += Util.getDaysInYear(year);
			return ret;
		} else {
			return -that.compareTo(this);
		}
	}
	
	////////////////////////////////////
	
	public static MesozoicDate getToday() {
		return new MesozoicDate(System.currentTimeMillis());
	}
}
