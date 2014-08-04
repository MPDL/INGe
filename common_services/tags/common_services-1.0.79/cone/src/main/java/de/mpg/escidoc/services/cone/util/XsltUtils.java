package de.mpg.escidoc.services.cone.util;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class XsltUtils {

	
	public static boolean validateDate(String date)
	{
		SimpleDateFormat yearDf = new SimpleDateFormat("yyyy");
		yearDf.setLenient(false);
		SimpleDateFormat yearMonthDf = new SimpleDateFormat("yyyy-MM");
		yearMonthDf.setLenient(false);
		SimpleDateFormat yearMonthDayDf = new SimpleDateFormat("yyyy-MM-dd");
		yearMonthDayDf.setLenient(false);
		
		
		try {
			if(date!=null && date.trim().length()==4)
			{
				Date result = yearDf.parse(date);
				return true;
			}
			
			else if (date.trim().length()==7)
			{
				Date result = yearMonthDf.parse(date);
				return true;
			}
			
	
			else if (date.trim().length()==10)
			{
				Date result = yearMonthDayDf.parse(date);
				return true;
			}
			else
			{
				return false;
			}
		
		} catch (ParseException e) {
			return false;
		}

	}
	
}
