package de.mpg.mpdl.inge.cone.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class XsltUtils {

	private XsltUtils() {

	}

	public static boolean validateDate(String date) {
		SimpleDateFormat yearDf = new SimpleDateFormat("yyyy");
		yearDf.setLenient(false);
		SimpleDateFormat yearMonthDf = new SimpleDateFormat("yyyy-MM");
		yearMonthDf.setLenient(false);
		SimpleDateFormat yearMonthDayDf = new SimpleDateFormat("yyyy-MM-dd");
		yearMonthDayDf.setLenient(false);

		try {
			if (date != null && date.trim().length() == 4) {
				yearDf.parse(date);
				return true;
			} else if (date.trim().length() == 7) {
				yearMonthDf.parse(date);
				return true;
			} else if (date.trim().length() == 10) {
				yearMonthDayDf.parse(date);
				return true;
			} else {
				return false;
			}

		} catch (ParseException e) {
			return false;
		}

	}

}
