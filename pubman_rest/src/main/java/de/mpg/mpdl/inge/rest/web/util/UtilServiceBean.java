package de.mpg.mpdl.inge.rest.web.util;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.springframework.stereotype.Service;

@Service
public class UtilServiceBean {
	
	public Date string2Date(String dateString) {
		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSxxxx");
		ZonedDateTime zdt = ZonedDateTime.parse(dateString, formatter);
		Date convertedDate = Date.from(zdt.toInstant());
		return convertedDate;
	}

}
