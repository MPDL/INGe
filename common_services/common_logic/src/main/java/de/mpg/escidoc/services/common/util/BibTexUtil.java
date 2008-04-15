package de.mpg.escidoc.services.common.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.common.valueobjects.MdsPublicationVO;

public class BibTexUtil {
	
	private static Logger logger = Logger.getLogger(BibTexUtil.class);
	
	private static Map<BibTexUtil.Genre, MdsPublicationVO.Genre> genreMapping = new HashMap<BibTexUtil.Genre, MdsPublicationVO.Genre>();
	static
	{
		genreMapping.put(BibTexUtil.Genre.article, MdsPublicationVO.Genre.ARTICLE);
		genreMapping.put(BibTexUtil.Genre.book, MdsPublicationVO.Genre.BOOK);
		genreMapping.put(BibTexUtil.Genre.booklet, MdsPublicationVO.Genre.OTHER);
		genreMapping.put(BibTexUtil.Genre.conference, MdsPublicationVO.Genre.CONFERENCE_PAPER);
		genreMapping.put(BibTexUtil.Genre.inbook, MdsPublicationVO.Genre.BOOK_ITEM);
		genreMapping.put(BibTexUtil.Genre.incollection, MdsPublicationVO.Genre.BOOK_ITEM);
		genreMapping.put(BibTexUtil.Genre.inproceedings, MdsPublicationVO.Genre.CONFERENCE_PAPER);
		genreMapping.put(BibTexUtil.Genre.manual, MdsPublicationVO.Genre.OTHER);
		genreMapping.put(BibTexUtil.Genre.mastersthesis, MdsPublicationVO.Genre.THESIS);
		genreMapping.put(BibTexUtil.Genre.misc, MdsPublicationVO.Genre.OTHER);
		genreMapping.put(BibTexUtil.Genre.phdthesis, MdsPublicationVO.Genre.THESIS);
		genreMapping.put(BibTexUtil.Genre.proceedings, MdsPublicationVO.Genre.PROCEEDINGS);
		genreMapping.put(BibTexUtil.Genre.techreport, MdsPublicationVO.Genre.REPORT);
		genreMapping.put(BibTexUtil.Genre.unpublished, MdsPublicationVO.Genre.OTHER);
	}
	
	private static Map<String, String> encodingTable = new HashMap<String, String>();
	static
	{		
		encodingTable.put("{\\\"a}", "ä");
		encodingTable.put("{\\\"o}", "ö");
		encodingTable.put("{\\\"u}", "ü");
		encodingTable.put("{\\\"A}", "Ä");
		encodingTable.put("{\\\"O}", "Ö");
		encodingTable.put("{\\\"U}", "Ü");
		encodingTable.put("{\\\"ss}", "ß");
		encodingTable.put("{\\`a}", "à");
		encodingTable.put("{\\`A}", "À");
		encodingTable.put("{\\'a}", "á");
		encodingTable.put("{\\'A}", "Á");
		encodingTable.put("{\\^a}", "â");
		encodingTable.put("{\\^A}", "Â");
		encodingTable.put("{\\c{c}}", "ç");
		encodingTable.put("{\\c{C}}", "Ç");
		encodingTable.put("{\\~n}", "ñ");
		encodingTable.put("{\\~N}", "Ñ");
		encodingTable.put("{\\O}", "Ø");
		encodingTable.put("{\\aa}", "å");
		encodingTable.put("{\\AA}", "Å");
		
		encodingTable.put("\\\"a", "ä");
		encodingTable.put("\\\"o", "ö");
		encodingTable.put("\\\"u", "ü");
		encodingTable.put("\\\"A", "Ä");
		encodingTable.put("\\\"O", "Ö");
		encodingTable.put("\\\"U", "Ü");
		encodingTable.put("\\\"ss", "ß");
		encodingTable.put("\\`a", "à");
		encodingTable.put("\\`A", "À");
		encodingTable.put("\\'a", "á");
		encodingTable.put("\\'A", "Á");
		encodingTable.put("\\^a", "â");
		encodingTable.put("\\^A", "Â");
		encodingTable.put("\\c{c}", "ç");
		encodingTable.put("\\c{C}", "Ç");
		encodingTable.put("\\~n", "ñ");
		encodingTable.put("\\~N", "Ñ");
		encodingTable.put("\\O", "Ø");
		encodingTable.put("\\aa", "å");
		encodingTable.put("\\AA", "Å");
	}
	
	private static Map<String, String> monthTable = new HashMap<String, String>();
	static
	{
		monthTable.put("0", "01");
		monthTable.put("1", "02");
		monthTable.put("2", "03");
		monthTable.put("3", "04");
		monthTable.put("4", "05");
		monthTable.put("5", "06");
		monthTable.put("6", "07");
		monthTable.put("7", "08");
		monthTable.put("8", "09");
		monthTable.put("9", "10");
		monthTable.put("10", "11");
		monthTable.put("11", "12");
		
		monthTable.put("00", "01");
		monthTable.put("01", "02");
		monthTable.put("02", "03");
		monthTable.put("03", "04");
		monthTable.put("04", "05");
		monthTable.put("05", "06");
		monthTable.put("06", "07");
		monthTable.put("07", "08");
		monthTable.put("08", "09");
		monthTable.put("09", "10");
		
		monthTable.put("january", "01");
		monthTable.put("february", "02");
		monthTable.put("march", "03");
		monthTable.put("april", "04");
		monthTable.put("may", "05");
		monthTable.put("june", "06");
		monthTable.put("july", "07");
		monthTable.put("august", "08");
		monthTable.put("september", "09");
		monthTable.put("october", "10");
		monthTable.put("november", "11");
		monthTable.put("december", "12");
		
		monthTable.put("jan", "01");
		monthTable.put("feb", "02");
		monthTable.put("mar", "03");
		monthTable.put("apr", "04");
		monthTable.put("may", "05");
		monthTable.put("jun", "06");
		monthTable.put("jul", "07");
		monthTable.put("aug", "08");
		monthTable.put("sep", "09");
		monthTable.put("oct", "10");
		monthTable.put("nov", "11");
		monthTable.put("dec", "12");
	}
	
	public static String bibtexDecode(String text)
	{
		for (String element : encodingTable.keySet()) {
			text = text.replace(element, encodingTable.get(element));
		}
		return stripBraces(text);
	}
	
	public static String stripBraces(String in)
	{
		if (in.startsWith("{") && in.endsWith("}"))
		{
			in = in.substring(1, in.length() - 1);
		}
		else if (in.startsWith("\"") && in.endsWith("\""))
		{
			in = in.substring(1, in.length() - 1);
		}
		else if (in.startsWith("'") && in.endsWith("'"))
		{
			in = in.substring(1, in.length() - 1);
		}
		return in.trim();
	}
	
	public static String parseMonth(String monthString)
	{
		logger.debug("Parsing " + monthString);
		return monthTable.get(bibtexDecode(monthString.trim().toLowerCase()));
	}
	
	public static Map<BibTexUtil.Genre, MdsPublicationVO.Genre> getGenreMapping() {
		return genreMapping;
	}

	public static void setGenreMapping(Map<BibTexUtil.Genre, MdsPublicationVO.Genre> genreMapping) {
		BibTexUtil.genreMapping = genreMapping;
	}

	public enum Genre
	{
		article, book, booklet, conference, inbook, incollection, inproceedings,
		manual, mastersthesis, misc, phdthesis, proceedings, techreport, unpublished
	}
}
