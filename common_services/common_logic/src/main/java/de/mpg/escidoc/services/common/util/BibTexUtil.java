package de.mpg.escidoc.services.common.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		encodingTable.put("\\acute{e}", "\u00e9");
		encodingTable.put("\\ast", "\u002a");
		encodingTable.put("\\star", "\u002a");
		encodingTable.put("{\\{}", "\u007b");
		encodingTable.put("\\{", "\u007b");
		encodingTable.put("{\\}}", "\u007d");
		encodingTable.put("\\}", "\u007d");
		encodingTable.put("^{\\underline{\\rm a}}", "\u00aa");
		encodingTable.put("^{\\circ}", "\u00b0");
		encodingTable.put("{\\pm}", "\u00b1");
		encodingTable.put("^{2}", "\u00b2");
		encodingTable.put("^{3}", "\u00b3");
		encodingTable.put("^{1}", "\u00b9");
		encodingTable.put("\\frac{1}{2}", "\u00bd");
		encodingTable.put("{\\times}", "\u00d7");
		encodingTable.put("\\times", "\u00d7");
		encodingTable.put("{\\div}", "\u00f7");
		encodingTable.put("\\div", "\u00f7");
		encodingTable.put("\\dot G", "\u0120");
		encodingTable.put("\\Gamma", "\u0393");
		encodingTable.put("\\Delta", "\u0394");
		encodingTable.put("\\Lambda", "\u039b");
		encodingTable.put("\\Sigma", "\u03a3");
		encodingTable.put("\\Omega", "\u03a9");
		encodingTable.put("\\delta", "\u03b4");
		encodingTable.put("\\alpha", "\u03b1");
		encodingTable.put("\\beta", "\u03b2");
		encodingTable.put("\\gamma", "\u03b3");
		encodingTable.put("\\delta", "\u03b4");
		encodingTable.put("\\epsilon", "\u03b5");
		encodingTable.put("\\zeta", "\u03b6");
		encodingTable.put("\\eta", "\u03b7");
		encodingTable.put("\\theta", "\u03b8");
		encodingTable.put("\\kappa", "\u03ba");
		encodingTable.put("\\lambda", "\u03bb");
		encodingTable.put("\\mu", "\u03bc");
		encodingTable.put("\\nu", "\u03bd");
		encodingTable.put("\\xi", "\u03be");
		encodingTable.put("\\pi", "\u03c0");
		encodingTable.put("\\rho", "\u03c1");
		encodingTable.put("\\sigma", "\u03c3");
		encodingTable.put("\\tau", "\u03c4");
		encodingTable.put("\\phi", "\u03c6");
		encodingTable.put("\\chi", "\u03c7");
		encodingTable.put("\\omega", "\u03c9");
		encodingTable.put("\\ell", "\u2113");
		encodingTable.put("\\rightarrow", "\u2192");
		encodingTable.put("\\to", "\u2192");
		encodingTable.put("\\leftrightarrow", "\u2194");
		encodingTable.put("\\nabla", "\u2207");
		encodingTable.put("\\sim", "\u223c");
		encodingTable.put("\\le", "\u2264");
		encodingTable.put("\\ge", "\u2265");
		encodingTable.put("\\lesssim", "\u2272");
		encodingTable.put("\\gtrsim", "\u2273");
		encodingTable.put("\\odot", "\u2299");
		encodingTable.put("\\infty", "\u221e");
		encodingTable.put("\\circ", "\u2218");
		encodingTable.put("\\cdot", "\u22c5");
		encodingTable.put("\\dot{P}", "\u1e56");
		encodingTable.put("{\\vec B}", "\u20d7");

		encodingTable.put("\\hspace{0 cm}", "\u0000");
		encodingTable.put("\\hspace{0 pt}", "\u0000");
		encodingTable.put("{\\#}", "\u0023");
		encodingTable.put("\\#", "\u0023");
		encodingTable.put("{\\$}", "\u0024");
		encodingTable.put("\\$", "\u0024");
		encodingTable.put("{\\%}", "\u0025");
		encodingTable.put("\\%", "\u0025");
		encodingTable.put("{\\&}", "\u0026");
		encodingTable.put("\\&", "\u0026");
		encodingTable.put("\\symbol{94}", "\u005e");
		encodingTable.put("{\\_}", "\u005f");
		encodingTable.put("\\_", "\u005f");
		encodingTable.put("\\symbol{126}", "\u007e");
		encodingTable.put("\\~{}", "\u007e");
		encodingTable.put("$\\sim$", "\u007e");

		encodingTable.put("{\\#}", "\u0023");
		encodingTable.put("\\#", "\u0023");
		encodingTable.put("{\\$}", "\u0024");
		encodingTable.put("\\$", "\u0024");
		encodingTable.put("{\\%}", "\u0025");
		encodingTable.put("\\%", "\u0025");
		encodingTable.put("{\\&}", "\u0026");
		encodingTable.put("\\&", "\u0026");
		encodingTable.put("\\symbol{94}", "\u005e");
		encodingTable.put("{\\_}", "\u005f");
		encodingTable.put("\\_", "\u005f");
		encodingTable.put("{\\{}", "\u007b");
		encodingTable.put("\\{", "\u007b");
		encodingTable.put("{\\}}", "\u007d");
		encodingTable.put("\\}", "\u007d");
		encodingTable.put("\\symbol{126}", "\u007e");
		encodingTable.put("\\~{}", "\u007e");
		encodingTable.put("{!`}", "\u00a1");
		encodingTable.put("{\\copyright}", "\u00a9");
		encodingTable.put("{?`}", "\u00bf");
		encodingTable.put("{\\`{A}}", "\u00c0");
		encodingTable.put("{\\`A}", "\u00c0");
		encodingTable.put("\\`{A}", "\u00c0");
		encodingTable.put("\\`A", "\u00c0");
		encodingTable.put("{\\'{A}}", "\u00c1");
		encodingTable.put("{\\'A}", "\u00c1");
		encodingTable.put("\\'{A}", "\u00c1");
		encodingTable.put("\\'A", "\u00c1");
		encodingTable.put("{\\^{A}}", "\u00c2");
		encodingTable.put("{\\^A}", "\u00c2");
		encodingTable.put("\\^{A}", "\u00c2");
		encodingTable.put("\\^A", "\u00c2");
		encodingTable.put("{\\~{A}}", "\u00c3");
		encodingTable.put("{\\~A}", "\u00c3");
		encodingTable.put("\\~{A}", "\u00c3");
		encodingTable.put("\\~A", "\u00c3");
		encodingTable.put("{\\\"{A}}", "\u00c4");
		encodingTable.put("{\\\"A}", "\u00c4");
		encodingTable.put("\\\"{A}", "\u00c4");
		encodingTable.put("\\\"A", "\u00c4");
		encodingTable.put("{\\AA}", "\u00c5");
		encodingTable.put("{\\AE}", "\u00c6");
		encodingTable.put("{\\c{C}}", "\u00c7");
		encodingTable.put("\\c{C}", "\u00c7");
		encodingTable.put("{\\`{E}}", "\u00c8");
		encodingTable.put("{\\`E}", "\u00c8");
		encodingTable.put("\\`{E}", "\u00c8");
		encodingTable.put("\\`E", "\u00c8");
		encodingTable.put("{\\'{E}}", "\u00c9");
		encodingTable.put("{\\'E}", "\u00c9");
		encodingTable.put("\\'{E}", "\u00c9");
		encodingTable.put("\\'E", "\u00c9");
		encodingTable.put("{\\^{E}}", "\u00ca");
		encodingTable.put("{\\^E}", "\u00ca");
		encodingTable.put("\\^{E}", "\u00ca");
		encodingTable.put("\\^E", "\u00ca");
		encodingTable.put("{\\\"{E}}", "\u00cb");
		encodingTable.put("{\\\"E}", "\u00cb");
		encodingTable.put("\\\"{E}", "\u00cb");
		encodingTable.put("\\\"E", "\u00cb");
		encodingTable.put("{\\`{I}}", "\u00cc");
		encodingTable.put("{\\`I}", "\u00cc");
		encodingTable.put("\\`{I}", "\u00cc");
		encodingTable.put("\\`I", "\u00cc");
		encodingTable.put("{\\'{I}}", "\u00cd");
		encodingTable.put("{\\'I}", "\u00cd");
		encodingTable.put("\\'{I}", "\u00cd");
		encodingTable.put("\\'I", "\u00cd");
		encodingTable.put("{\\^{I}}", "\u00ce");
		encodingTable.put("{\\^I}", "\u00ce");
		encodingTable.put("\\^{I}", "\u00ce");
		encodingTable.put("\\^I", "\u00ce");
		encodingTable.put("{\\\"{I}}", "\u00cf");
		encodingTable.put("{\\\"I}", "\u00cf");
		encodingTable.put("\\\"{I}", "\u00cf");
		encodingTable.put("\\\"I", "\u00cf");
		encodingTable.put("{\\~{N}}", "\u00d1");
		encodingTable.put("\\~{N}", "\u00d1");
		encodingTable.put("{\\~N}", "\u00d1");
		encodingTable.put("\\~N", "\u00d1");
		encodingTable.put("{\\`{O}}", "\u00d2");
		encodingTable.put("{\\`O}", "\u00d2");
		encodingTable.put("\\`{O}", "\u00d2");
		encodingTable.put("\\`O", "\u00d2");
		encodingTable.put("{\\'{O}}", "\u00d3");
		encodingTable.put("{\\'O}", "\u00d3");
		encodingTable.put("\\'{O}", "\u00d3");
		encodingTable.put("\\'O", "\u00d3");
		encodingTable.put("{\\^{O}}", "\u00d4");
		encodingTable.put("{\\^O}", "\u00d4");
		encodingTable.put("\\^{O}", "\u00d4");
		encodingTable.put("\\^O", "\u00d4");
		encodingTable.put("{\\~{O}}", "\u00d5");
		encodingTable.put("{\\~O}", "\u00d5");
		encodingTable.put("\\~{O}", "\u00d5");
		encodingTable.put("\\~O", "\u00d5");
		encodingTable.put("{\\\"{O}}", "\u00d6");
		encodingTable.put("{\\\"O}", "\u00d6");
		encodingTable.put("\\\"{O}", "\u00d6");
		encodingTable.put("\\\"O", "\u00d6");
		encodingTable.put("{\\O}", "\u00d8");
		encodingTable.put("{\\`{U}}", "\u00d9");
		encodingTable.put("{\\`U}", "\u00d9");
		encodingTable.put("\\`{U}", "\u00d9");
		encodingTable.put("\\`U", "\u00d9");
		encodingTable.put("{\\'{U}}", "\u00da");
		encodingTable.put("{\\'U}", "\u00da");
		encodingTable.put("\\'{U}", "\u00da");
		encodingTable.put("\\'U", "\u00da");
		encodingTable.put("{\\^{U}}", "\u00db");
		encodingTable.put("{\\^U}", "\u00db");
		encodingTable.put("\\^{U}", "\u00db");
		encodingTable.put("\\^U", "\u00db");
		encodingTable.put("{\\\"{U}}", "\u00dc");
		encodingTable.put("{\\\"u}", "\u00dc");
		encodingTable.put("\\\"{U}", "\u00dc");
		encodingTable.put("\\\"u", "\u00dc");
		encodingTable.put("{\\'{Y}}", "\u00dd");
		encodingTable.put("{\\'Y}", "\u00dd");
		encodingTable.put("\\'{Y}", "\u00dd");
		encodingTable.put("\\'Y", "\u00dd");
		encodingTable.put("{\\ss}", "\u00df");
		encodingTable.put("{\\`{a}}", "\u00e0");
		encodingTable.put("{\\`a}", "\u00e0");
		encodingTable.put("\\`{a}", "\u00e0");
		encodingTable.put("\\`a", "\u00e0");
		encodingTable.put("{\\'{a}}", "\u00e1");
		encodingTable.put("{\\'a}", "\u00e1");
		encodingTable.put("\\'{a}", "\u00e1");
		encodingTable.put("\\'a", "\u00e1");
		encodingTable.put("{\\^{a}}", "\u00e2");
		encodingTable.put("{\\^a}", "\u00e2");
		encodingTable.put("\\^{a}", "\u00e2");
		encodingTable.put("\\^a", "\u00e2");
		encodingTable.put("{\\~{a}}", "\u00e3");
		encodingTable.put("{\\~a}", "\u00e3");
		encodingTable.put("\\~{a}", "\u00e3");
		encodingTable.put("\\~a", "\u00e3");
		encodingTable.put("{\\\"{a}}", "\u00e4");
		encodingTable.put("{\\\"a}", "\u00e4");
		encodingTable.put("\\\"{a}", "\u00e4");
		encodingTable.put("\\\"a", "\u00e4");
		encodingTable.put("{\\aa}", "\u00e5");
		encodingTable.put("{\\ae}", "\u00e6");
		encodingTable.put("{\\c{c}}", "\u00e7");
		encodingTable.put("\\c{c}", "\u00e7");
		encodingTable.put("\\c c", "\u00e7");
		encodingTable.put("{\\`{e}}", "\u00e8");
		encodingTable.put("{\\`e}", "\u00e8");
		encodingTable.put("{\\` e}", "\u00e8");
		encodingTable.put("\\`{e}", "\u00e8");
		encodingTable.put("\\`e", "\u00e8");
		encodingTable.put("{\\'{e}}", "\u00e9");
		encodingTable.put("{\\'e}", "\u00e9");
		encodingTable.put("{\\' e}", "\u00e9");
		encodingTable.put("\\'{e}", "\u00e9");
		encodingTable.put("\\'e", "\u00e9");
		encodingTable.put("{\\^{e}}", "\u00ea");
		encodingTable.put("{\\^e}", "\u00ea");
		encodingTable.put("\\^{e}", "\u00ea");
		encodingTable.put("\\^e", "\u00ea");
		encodingTable.put("{\\\"{e}}", "\u00eb");
		encodingTable.put("{\\\"e}", "\u00eb");
		encodingTable.put("\\\"{e}", "\u00eb");
		encodingTable.put("\\\"e", "\u00eb");
		encodingTable.put("{\\`{\\i}}", "\u00ec");
		encodingTable.put("{\\`\\i}", "\u00ec");
		encodingTable.put("\\`{\\i}", "\u00ec");
		encodingTable.put("\\`\\i", "\u00ec");
		encodingTable.put("{\\'{\\i}}", "\u00ed");
		encodingTable.put("{\\'\\i}", "\u00ed");
		encodingTable.put("\\'{\\i}", "\u00ed");
		encodingTable.put("\\'\\i", "\u00ed");
		encodingTable.put("{\\'{i}}", "\u00ed");
		encodingTable.put("{\\'i}", "\u00ed");
		encodingTable.put("\\'{i}", "\u00ed");
		encodingTable.put("\\'i", "\u00ed");
		encodingTable.put("{\\^{\\i}}", "\u00ee");
		encodingTable.put("{\\^\\i}", "\u00ee");
		encodingTable.put("\\^{\\i}", "\u00ee");
		encodingTable.put("\\^\\i", "\u00ee");
		encodingTable.put("{\\\"{\\i}}", "\u00ef");
		encodingTable.put("{\\\"\\i}", "\u00ef");
		encodingTable.put("\\\"{\\i}", "\u00ef");
		encodingTable.put("\\\"\\i", "\u00ef");
		encodingTable.put("{\\~{n}}", "\u00f1");
		encodingTable.put("\\~{n}", "\u00f1");
		encodingTable.put("{\\~n}", "\u00f1");
		encodingTable.put("\\~n", "\u00f1");
		encodingTable.put("{\\`{o}}", "\u00f2");
		encodingTable.put("{\\`o}", "\u00f2");
		encodingTable.put("\\`{o}", "\u00f2");
		encodingTable.put("\\`o", "\u00f2");
		encodingTable.put("{\\'{o}}", "\u00f3");
		encodingTable.put("{\\'o}", "\u00f3");
		encodingTable.put("\\'{o}", "\u00f3");
		encodingTable.put("\\'o", "\u00f3");
		encodingTable.put("{\\^{o}}", "\u00f4");
		encodingTable.put("{\\^o}", "\u00f4");
		encodingTable.put("\\^{o}", "\u00f4");
		encodingTable.put("\\^o", "\u00f4");
		encodingTable.put("{\\~{o}}", "\u00f5");
		encodingTable.put("{\\~o}", "\u00f5");
		encodingTable.put("\\~{o}", "\u00f5");
		encodingTable.put("\\~o", "\u00f5");
		encodingTable.put("{\\\"{o}}", "\u00f6");
		encodingTable.put("{\\\"o}", "\u00f6");
		encodingTable.put("{\\\" o}", "\u00f6");
		encodingTable.put("\\\"{o}", "\u00f6");
		encodingTable.put("\\\"o", "\u00f6");
		encodingTable.put("{\\o}", "\u00f8");
		encodingTable.put("{\\o }", "\u00f8");
		encodingTable.put("{\\`{u}}", "\u00f9");
		encodingTable.put("{\\`u}", "\u00f9");
		encodingTable.put("\\`{u}", "\u00f9");
		encodingTable.put("\\`u", "\u00f9");
		encodingTable.put("{\\'{u}}", "\u00fa");
		encodingTable.put("{\\'u}", "\u00fa");
		encodingTable.put("\\'{u}", "\u00fa");
		encodingTable.put("\\'u", "\u00fa");
		encodingTable.put("{\\^{u}}", "\u00fb");
		encodingTable.put("{\\^u}", "\u00fb");
		encodingTable.put("\\^{u}", "\u00fb");
		encodingTable.put("\\^u", "\u00fb");
		encodingTable.put("{\\\"{u}}", "\u00fc");
		encodingTable.put("{\\\"u}", "\u00fc");
		encodingTable.put("{\\\" u}", "\u00fc");
		encodingTable.put("\\\"{u}", "\u00fc");
		encodingTable.put("\\\"u", "\u00fc");
		encodingTable.put("{\\'{y}}", "\u00fd");
		encodingTable.put("{\\'y}", "\u00fd");
		encodingTable.put("\\'{y}", "\u00fd");
		encodingTable.put("\\'y", "\u00fd");
		encodingTable.put("{\\th}", "\u00fe");
		encodingTable.put("{\\\"{y}}", "\u00ff");
		encodingTable.put("{\\\"y}", "\u00ff");
		encodingTable.put("\\\"{y}", "\u00ff");
		encodingTable.put("\\\"y", "\u00ff");
		encodingTable.put("{\\'{C}}", "\u0106");
		encodingTable.put("\\'{C}", "\u0106");
		encodingTable.put("{\\' C}", "\u0106");
		encodingTable.put("{\\'C}", "\u0106");
		encodingTable.put("\\'C", "\u0106");
		encodingTable.put("{\\'{c}}", "\u0107");
		encodingTable.put("\\'{c}", "\u0107");
		encodingTable.put("{\\' c}", "\u0107");
		encodingTable.put("{\\'c}", "\u0107");
		encodingTable.put("\\'c", "\u0107");
		encodingTable.put("\\`c", "\u0107");
		encodingTable.put("{\\v{C}}", "\u010c");
		encodingTable.put("\\v{C}", "\u010c");
		encodingTable.put("{\\v C}", "\u010c");
		encodingTable.put("{\\v{c}}", "\u010d");
		encodingTable.put("\\v{c}", "\u010d");
		encodingTable.put("{\\v c}", "\u010d");
		encodingTable.put("{\\u{g}}", "\u011f");
		encodingTable.put("\\u{g}", "\u011f");
		encodingTable.put("{\\u g}", "\u011f");
		encodingTable.put("{\\u{\\i}}", "\u012d");
		encodingTable.put("{\\u\\i}", "\u012d");
		encodingTable.put("\\u{\\i}", "\u012d");
		encodingTable.put("\\u\\i", "\u012d");
		encodingTable.put("{\\i}", "\u0131");
		encodingTable.put("{\\L}", "\u0141");
		encodingTable.put("{\\l}", "\u0142");
		encodingTable.put("\\l{}", "\u0142");

		encodingTable.put("{\\'{N}}", "\u0143");
		encodingTable.put("\\'{N}", "\u0143");
		encodingTable.put("{\\' N}", "\u0143");
		encodingTable.put("{\\'N}", "\u0143");
		encodingTable.put("\\'N", "\u0143");
		encodingTable.put("{\\'{n}}", "\u0144");
		encodingTable.put("\\'{n}", "\u0144");
		encodingTable.put("{\\' n}", "\u0144");
		encodingTable.put("{\\'n}", "\u0144");
		encodingTable.put("\\'n", "\u0144");
		encodingTable.put("{\\OE}", "\u0152");
		encodingTable.put("{\\oe}", "\u0153");
		encodingTable.put("{\\v{r}}", "\u0159");
		encodingTable.put("\\v{r}", "\u0159");
		encodingTable.put("{\\v r}", "\u0159");
		encodingTable.put("{\\'{S}}", "\u015a");
		encodingTable.put("\\'{S}", "\u015a");
		encodingTable.put("{\\' S}", "\u015a");
		encodingTable.put("{\\'S}", "\u015a");
		encodingTable.put("\\'S", "\u015a");
		encodingTable.put("{\\'{s}}", "\u015b");
		encodingTable.put("\\'{s}", "\u015b");
		encodingTable.put("{\\' s}", "\u015b");
		encodingTable.put("{\\'s}", "\u015b");
		encodingTable.put("\\'s", "\u015b");
		encodingTable.put("\\'s", "\u015b");
		encodingTable.put("{\\c{S}}", "\u015e");
		encodingTable.put("\\c{S}", "\u015e");
		encodingTable.put("{\\c{s}}", "\u015f");
		encodingTable.put("\\c{s}", "\u015f");
		encodingTable.put("{\\v{S}}", "\u0160");
		encodingTable.put("\\v{S}", "\u0160");
		encodingTable.put("{\\v S}", "\u0160");
		encodingTable.put("{\\u{s}}", "\u0161");
		encodingTable.put("\\u{s}", "\u0161");
		encodingTable.put("{\\v{s}}", "\u0161");
		encodingTable.put("\\v{s}", "\u0161");
		encodingTable.put("{\\'{t}}", "\u0165");
		encodingTable.put("\\'{t}", "\u0165");
		encodingTable.put("{\\'t}", "\u0165");
		encodingTable.put("\\'t", "\u0165");
		encodingTable.put("{\\={u}}", "\u016b");
		encodingTable.put("{\\=u}", "\u016b");
		encodingTable.put("\\={u}", "\u016b");
		encodingTable.put("\\=u", "\u016b");
		encodingTable.put("{\\r{u}}", "\u016f");
		encodingTable.put("\\r{u}", "\u016f");
		encodingTable.put("{\\'{z}}", "\u017a");
		encodingTable.put("\\'{z}", "\u017a");
		encodingTable.put("{\\'z}", "\u017a");
		encodingTable.put("\\'z", "\u017a");
		encodingTable.put("\\'z", "\u017a");
		encodingTable.put("{\\.{Z}}", "\u017b");
		encodingTable.put("\\.{Z}", "\u017b");
		encodingTable.put("{\\.Z}", "\u017b");
		encodingTable.put("\\.Z", "\u017b");
		encodingTable.put("{\\.{z}}", "\u017c");
		encodingTable.put("\\.{z}", "\u017c");
		encodingTable.put("{\\.z}", "\u017c");
		encodingTable.put("\\.z", "\u017c");
		encodingTable.put("{\\v{Z}}", "\u017d");
		encodingTable.put("\\v{Z}", "\u017d");
		encodingTable.put("{\\v Z}", "\u017d");
		encodingTable.put("{\\v{z}}", "\u017e");
		encodingTable.put("\\v{z}", "\u017e");
		encodingTable.put("{\\v z}", "\u017e");
		encodingTable.put("{\\c{e}}", "\u0229");
		encodingTable.put("\\c{e}", "\u0229");
		encodingTable.put("{\\v{A}}", "\u01cd");
		encodingTable.put("\\v{A}", "\u01cd");
		encodingTable.put("{\\v A}", "\u01cd");
		encodingTable.put("{\\v{a}}", "\u01ce");
		encodingTable.put("\\v{a}", "\u01ce");
		encodingTable.put("{\\v a}", "\u01ce");

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
		if (in.startsWith("\"") && in.endsWith("\""))
		{
			in = in.substring(1, in.length() - 1);
		}
		else if (in.startsWith("'") && in.endsWith("'"))
		{
			in = in.substring(1, in.length() - 1);
		}
		Pattern pattern = Pattern.compile("\\{([^\\}]*)\\}");
		while (true)
		{
			Matcher matcher = pattern.matcher(in);
			if (matcher.find())
			{
				in = matcher.replaceAll("$1");
			}
			else
			{
				break;
			}
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
