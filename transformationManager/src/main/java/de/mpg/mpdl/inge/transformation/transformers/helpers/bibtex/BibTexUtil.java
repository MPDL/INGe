/*
 *
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution
 * License, Version 1.0 only (the "License"). You may not use this file except in compliance with
 * the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or
 * http://www.escidoc.org/license. See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License
 * file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with
 * the fields enclosed by brackets "[]" replaced with your own identifying information: Portions
 * Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2010 Fachinformationszentrum Karlsruhe Gesellschaft für
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur Förderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */

package de.mpg.mpdl.inge.transformation.transformers.helpers.bibtex;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;

/**
 * Utility class for BibTeX handling.
 *
 * @author franke (initial creation)
 * @author $Author: mfranke $ (last modification)
 * @version $Revision: 4134 $ $LastChangedDate: 2011-09-22 18:21:00 +0200 (Do, 22 Sep 2011) $
 */
public class BibTexUtil {
  public static final String ESCAPE_CHARACTERS = "$&%#"; // underscore was here too, but this
                                                         // doesn't seem to have any sense
  public static final String HOPEFULLY_UNUSED_TOKEN = "<<<!!HOPEFULLY_UNUSED_TOKEN!!>>>";

  private BibTexUtil() {}

  /**
   * Enum that lists all BibTeX genres.
   */
  public enum Genre
  {
    article, book, booklet, conference, inbook, incollection, inproceedings, manual, mastersthesis, misc, phdthesis, proceedings, techreport, unpublished, webpage, collection, talk, poster
  }

  /**
   * Mapping between BibTeX and eSciDoc genres.
   */
  private static Map<BibTexUtil.Genre, MdsPublicationVO.Genre> genreMapping = new HashMap<>();
  static {
    genreMapping.put(BibTexUtil.Genre.article, MdsPublicationVO.Genre.ARTICLE);
    genreMapping.put(BibTexUtil.Genre.book, MdsPublicationVO.Genre.BOOK);
    genreMapping.put(BibTexUtil.Genre.booklet, MdsPublicationVO.Genre.OTHER);
    genreMapping.put(BibTexUtil.Genre.conference, MdsPublicationVO.Genre.CONFERENCE_PAPER);
    genreMapping.put(BibTexUtil.Genre.inbook, MdsPublicationVO.Genre.BOOK_ITEM);
    genreMapping.put(BibTexUtil.Genre.incollection, MdsPublicationVO.Genre.BOOK_ITEM);
    genreMapping.put(BibTexUtil.Genre.inproceedings, MdsPublicationVO.Genre.CONFERENCE_PAPER);
    genreMapping.put(BibTexUtil.Genre.manual, MdsPublicationVO.Genre.MANUAL);
    genreMapping.put(BibTexUtil.Genre.mastersthesis, MdsPublicationVO.Genre.THESIS);
    genreMapping.put(BibTexUtil.Genre.misc, MdsPublicationVO.Genre.OTHER);
    genreMapping.put(BibTexUtil.Genre.phdthesis, MdsPublicationVO.Genre.THESIS);
    genreMapping.put(BibTexUtil.Genre.proceedings, MdsPublicationVO.Genre.PROCEEDINGS);
    genreMapping.put(BibTexUtil.Genre.techreport, MdsPublicationVO.Genre.REPORT);
    genreMapping.put(BibTexUtil.Genre.unpublished, MdsPublicationVO.Genre.OTHER);
    genreMapping.put(BibTexUtil.Genre.webpage, MdsPublicationVO.Genre.OTHER);
    genreMapping.put(BibTexUtil.Genre.collection, MdsPublicationVO.Genre.PROCEEDINGS);
    genreMapping.put(BibTexUtil.Genre.talk, MdsPublicationVO.Genre.TALK_AT_EVENT);
    genreMapping.put(BibTexUtil.Genre.poster, MdsPublicationVO.Genre.POSTER);
  }

  /**
   * Mapping for BibTeX special characters.
   */
  private static final Map<String, String> encodingTable = new LinkedHashMap<>();
  static {
    /*
     * should be generated from de.mpg.mpdl.inge.structuredexportmanager.functions.BibTex.java
     *
     * ----------
     *
     * to add all braclet-possibilities use the following regex and replace the results by the ones
     * mentioned below
     *
     * find put\("(.*?)", "($?)\{?(\\\\)(.{1})\{?(.{1})\}?($?)"\);
     *
     * replace put("$1", "$2$3$4$5$6"); put("$1", "$2\{$3$4$5\}$6"); put("$1", "$2$3$4\{$5\}$6");
     * put("$1", "$2\{$3$4\{$5\}}$6");
     *
     * ----------
     *
     * to get the final table you have to turn the result you got above:
     *
     * find put\(\"(.*)\", \"(.*)\"\);
     *
     * replace encodingTable.put("$2", "$1");
     *
     * ----------
     *
     * remove all Math-Environment-Signs ($)
     *
     * find encodingTable.put\(\"\$(.*)\$\", \"(.*)\"\);
     *
     * replace encodingTable.put("$1", "$2");
     *
     * ----------
     *
     * needs to be sorted by lenght (descending) (use textmechanic.conm/Sort-TextLines.html or
     * something similar)
     *
     * ----------
     *
     * remove all following lines
     *
     * encodingTable.put("-", "\u2010"); encodingTable.put("-", "\u2011"); encodingTable.put("\\-",
     * "\u207B"); encodingTable.put("\\+", "\u207A");
     *
     * ----------
     *
     * add all following lines
     */

    encodingTable.put("{\\textdoublevbaraccent{}}", "\u030E");
    encodingTable.put("{\\textdoublemacronbelow}", "\u035F");
    encodingTable.put("{\\textrightuparrowhead}", "\u0356");
    encodingTable.put("{\\textsuperimposetilde}", "\u0334");
    encodingTable.put("{\\textasteriskcentered}", "\u002A");
    encodingTable.put("{\\textcloserevepsilon}", "\u025E");
    encodingTable.put("{\\textrhookrevepsilon}", "\u025D");
    encodingTable.put("{\\textpertenthousand}", "\u2031");
    encodingTable.put("{\\textrightarrowhead}", "\u0350");
    encodingTable.put("{\\textturncommaabove}", "\u0312");
    encodingTable.put("{\\texttretroflexhook}", "\u0288");
    encodingTable.put("{\\textinvsubbridge{}}", "\u033A");
    encodingTable.put("{\\textsublhalfring{}}", "\u031C");
    encodingTable.put("{\\textbarrevglotstop}", "\u02A2");
    encodingTable.put("{\\textTretroflexhook}", "\u01ae");
    encodingTable.put("{\\textreferencemark}", "\u203B");
    encodingTable.put("{\\textquotedblright}", "\u201D");
    encodingTable.put("{\\rightleftharpoons}", "\u21CC");
    encodingTable.put("{\\textcolonmonetary}", "\u20A1");
    encodingTable.put("{\\textrighthalfring}", "\u0357");
    encodingTable.put("{\\textrevcommaabove}", "\u0314");
    encodingTable.put("{\\texthtbardotlessj}", "\u0284");
    encodingTable.put("{\\textlooptoprevesh}", "\u01aa");
    encodingTable.put("{\\textthreequarters}", "\u00BE");
    encodingTable.put("{\\textasciidieresis}", "\u00A8");
    encodingTable.put("\\mbox{$\\bullet$}", "\u2022");
    encodingTable.put("{\\textquotedblleft}", "\u201C");
    encodingTable.put("{\\ntrianglerighteq}", "\u22ED");
    encodingTable.put("{\\textlefthalfring}", "\u0351");
    encodingTable.put("{\\textsubrhalfring}", "\u0339");
    encodingTable.put("{\\textcommabelow{}}", "\u0326");
    encodingTable.put("{\\textpalhookbelow}", "\u0321");
    encodingTable.put("{\\textretracting{}}", "\u0319");
    encodingTable.put("{\\textGammaafrican}", "\u0194");
    encodingTable.put("{\\textdoublemacron}", "\u035E");
    encodingTable.put("{\\textvbaraccent{}}", "\u030D");
    encodingTable.put("{\\textcloseepsilon}", "\u029A");
    encodingTable.put("{\\textturnlonglegr}", "\u027A");
    encodingTable.put("{\\textpalhookbelow}", "\u01ab");
    encodingTable.put("{\\textquestiondown}", "\u00BF");
    encodingTable.put("{\\textordmasculine}", "\u00BA");
    encodingTable.put("{\\textasciicedilla}", "\u00B8");
    encodingTable.put("{\\textperthousand}", "\u2030");
    encodingTable.put("{\\textinterrobang}", "\u203D");
    encodingTable.put("{\\ntrianglelefteq}", "\u22EC");
    encodingTable.put("{\\rightthreetimes}", "\u22CC");
    encodingTable.put("{\\textdoubletilde}", "\u0360");
    encodingTable.put("{\\textlstrikethru}", "\u0338");
    encodingTable.put("{\\textsstrikethru}", "\u0337");
    encodingTable.put("{\\textlstrokethru}", "\u0336");
    encodingTable.put("{\\textsubumlaut{}}", "\u0324");
    encodingTable.put("{\\textadvancing{}}", "\u0318");
    encodingTable.put("{\\textcommaabover}", "\u0315");
    encodingTable.put("{\\textinvglotstop}", "\u0296");
    encodingTable.put("{\\textrevglotstop}", "\u0295");
    encodingTable.put("{\\textturnscripta}", "\u0252");
    encodingTable.put("{\\textIotaafrican}", "\u0196");
    encodingTable.put("{\\textdoublebreve}", "\u035D");
    encodingTable.put("{\\textovercross{}}", "\u033D");
    encodingTable.put("{\\textsubsquare{}}", "\u033B");
    encodingTable.put("{\\textsubbridge{}}", "\u032A");
    encodingTable.put("{\\textlangleabove}", "\u031A");
    encodingTable.put("{\\textbardotlessj}", "\u025F");
    encodingTable.put("{\\textbarglotstop}", "\u02A1");
    encodingTable.put("\\mbox{$^\\circ$}", "\u00b0");
    encodingTable.put("{\\textasciimacron}", "\u00AF");
    encodingTable.put("{\\textordfeminine}", "\u00AA");
    encodingTable.put("{\\textquotesingle}", "\u0027");
    encodingTable.put("{\\textasciicircum}", "\u005E");
    encodingTable.put("{\\sphericalangle}", "\u2222");
    encodingTable.put("{\\leftrightarrow}", "\u2194");
    encodingTable.put("{\\textquoteright}", "\u2019");
    encodingTable.put("{\\guilsinglright}", "\u203A");
    encodingTable.put("{\\quotesinglbase}", "\u201A");
    encodingTable.put("{\\ntriangleright}", "\u22EB");
    encodingTable.put("{\\leftthreetimes}", "\u22CB");
    encodingTable.put("{\\Leftrightarrow}", "\u21D4");
    encodingTable.put("{\\textsubtilde{}}", "\u0330");
    encodingTable.put("{\\textsyllabic{}}", "\u0329");
    encodingTable.put("{\\textsubacute{}}", "\u0317");
    encodingTable.put("{\\textsubgrave{}}", "\u0316");
    encodingTable.put("{\\textcommaabove}", "\u0313");
    encodingTable.put("{\\textdotbreve{}}", "\u0310");
    encodingTable.put("{\\textraisevibyi}", "\u0285");
    encodingTable.put("{\\textcloseomega}", "\u0277");
    encodingTable.put("{\\textsubwedge{}}", "\u032C");
    encodingTable.put("{\\textlowering{}}", "\u031E");
    encodingTable.put("{\\textturnrrtail}", "\u027B");
    encodingTable.put("{\\textrevepsilon}", "\u025C");
    encodingTable.put("{\\textrhookschwa}", "\u025A");
    encodingTable.put("{\\textlengthmark}", "\u02D0");
    encodingTable.put("{\\textprimstress}", "\u02C8");
    encodingTable.put("\\mbox{$\\cdot$}", "\u00b7");
    encodingTable.put("{\\textonequarter}", "\u00BC");
    encodingTable.put("{\\guillemotright}", "\u00BB");
    encodingTable.put("{\\textasciiacute}", "\u00B4");
    encodingTable.put("{\\textregistered}", "\u00AE");
    encodingTable.put("{\\textexclamdown}", "\u00A1");
    encodingTable.put("{\\textasciigrave}", "\u0060");
    encodingTable.put("{\\textasciitilde}", "\u007E");
    encodingTable.put("{\\textbraceright}", "\u007D");
    encodingTable.put("{\\textunderscore}", "\u005F");
    encodingTable.put("{\\textbackslash}", "\\u005C");
    encodingTable.put("{\\fallingdotseq}", "\u2252");
    encodingTable.put("{\\measuredangle}", "\u2221");
    encodingTable.put("{\\texttrademark}", "\u2122");
    encodingTable.put("{\\guilsinglleft}", "\u2039");
    encodingTable.put("{\\textquoteleft}", "\u2018");
    encodingTable.put("{\\ntriangleleft}", "\u22EA");
    encodingTable.put("{\\divideontimes}", "\u22C7");
    encodingTable.put("{\\textoverline}", "\u203E");
    encodingTable.put("{\\texttoptiebar}", "\u0361");
    encodingTable.put("{\\subdoublevert}", "\u0348");
    encodingTable.put("{\\textsubring{}}", "\u0325");
    encodingTable.put("{\\textnhookleft}", "\u0272");
    encodingTable.put("{\\textturnmrleg}", "\u0270");
    encodingTable.put("{\\textramshorns}", "\u0264");
    encodingTable.put("{\\textseagull{}}", "\u033C");
    encodingTable.put("{\\textsubarch{}}", "\u032F");
    encodingTable.put("{\\textsubcircum}", "\u032D");
    encodingTable.put("{\\textsubplus{}}", "\u031F");
    encodingTable.put("{\\textraising{}}", "\u031D");
    encodingTable.put("{\\textrighthorn}", "\u031B");
    encodingTable.put("{\\textfishhookr}", "\u027E");
    encodingTable.put("{\\textNhookleft}", "\u019d");
    encodingTable.put("{\\textEreversed}", "\u018e");
    encodingTable.put("{\\guillemotleft}", "\u00AB");
    encodingTable.put("{\\textbrokenbar}", "\u00A6");
    encodingTable.put("{\\textquotedbl}", "\\u0022");
    encodingTable.put("{\\textbraceleft}", "\u007B");
    encodingTable.put("\\mbox{$^{+}$}", "\u207A");
    encodingTable.put("\\mbox{$^{-}$}", "\u207B");
    encodingTable.put("{\\risingdotseq}", "\u2253");
    encodingTable.put("{\\quotedblbase}", "\u201E");
    encodingTable.put("{\\subdoublebar}", "\u0347");
    encodingTable.put("{\\subdoublebar}", "\u0333");
    encodingTable.put("{\\textsubminus}", "\u0320");
    encodingTable.put("{\\textbullseye}", "\u0298");
    encodingTable.put("{\\textstretchc}", "\u0297");
    encodingTable.put("{\\textglotstop}", "\u0294");
    encodingTable.put("{\\textDafrican}", "\u0189");
    encodingTable.put("{\\textundertie}", "\u032E");
    encodingTable.put("{\\textlonglegr}", "\u027C");
    encodingTable.put("{\\textlyoghlig}", "\u026E");
    encodingTable.put("{\\textcrlambda}", "\u019b");
    encodingTable.put("{\\textdyoghlig}", "\u02A4");
    encodingTable.put("{\\textcurrency}", "\u00A4");
    encodingTable.put("{\\diamondsuit}", "\u2662");
    encodingTable.put("{\\updownarrow}", "\u2195");
    encodingTable.put("{\\circleddash}", "\u229D");
    encodingTable.put("{\\circledcirc}", "\u229A");
    encodingTable.put("{\\succcurlyeq}", "\u227D");
    encodingTable.put("{\\preccurlyeq}", "\u227C");
    encodingTable.put("{\\succcurlyeq}", "\u22E1");
    encodingTable.put("{\\preccurlyeq}", "\u22E0");
    encodingTable.put("{\\curlyeqsucc}", "\u22DF");
    encodingTable.put("{\\curlyeqprec}", "\u22DE");
    encodingTable.put("{\\textscoelig}", "\u0276");
    encodingTable.put("{\\textscriptg}", "\u0261");
    encodingTable.put("{\\textscripta}", "\u0251");
    encodingTable.put("{\\texttstroke}", "\u0167");
    encodingTable.put("{\\textTstroke}", "\u0166");
    encodingTable.put("{\\doubletilde}", "\u034C");
    encodingTable.put("{\\dottedtilde}", "\u034B");
    encodingTable.put("{\\textoverw{}}", "\u034A");
    encodingTable.put("{\\textupsilon}", "\u028A");
    encodingTable.put("{\\textepsilon}", "\u025B");
    encodingTable.put("{\\texttctclig}", "\u02A8");
    encodingTable.put("{\\textdctzlig}", "\u02A5");
    encodingTable.put("{\\texteturned}", "\u01DD");
    encodingTable.put("{\\textonehalf}", "\u00BD");
    encodingTable.put("{\\textpercent}", "\u0025");
    encodingTable.put("{\\textgreater}", "\u003E");
    encodingTable.put("\\mbox{$^+$}", "\u207A");
    encodingTable.put("\\mbox{$^-$}", "\u207B");
    encodingTable.put("{\\textemdash}", "\u2014");
    encodingTable.put("{\\textendash}", "\u2013");
    encodingTable.put("{\\sqsupseteq}", "\u2292");
    encodingTable.put("{\\sqsubseteq}", "\u2291");
    encodingTable.put("{\\complement}", "\u2201");
    encodingTable.put("{\\rightarrow}", "\u2192");
    encodingTable.put("{\\textnumero}", "\u2116");
    encodingTable.put("{\\textbullet}", "\u2022");
    encodingTable.put("{\\circledast}", "\u229B");
    encodingTable.put("{\\sqsupseteq}", "\u22E3");
    encodingTable.put("{\\sqsubseteq}", "\u22E2");
    encodingTable.put("{\\curlywedge}", "\u22CF");
    encodingTable.put("{\\Rightarrow}", "\u21D2");
    encodingTable.put("{\\overbridge}", "\u0346");
    encodingTable.put("{\\textctyogh}", "\u0293");
    encodingTable.put("{\\textrtailz}", "\u0290");
    encodingTable.put("{\\textrtails}", "\u0282");
    encodingTable.put("{\\textinvscr}", "\u0281");
    encodingTable.put("{\\textrtailn}", "\u0273");
    encodingTable.put("{\\textltailm}", "\u0271");
    encodingTable.put("{\\texththeng}", "\u0267");
    encodingTable.put("{\\textrtaild}", "\u0256");
    encodingTable.put("{\\textflorin}", "\u0192");
    encodingTable.put("{\\spreadlips}", "\u034D");
    encodingTable.put("{\\textsubw{}}", "\u032B");
    encodingTable.put("{\\textrtailr}", "\u027D");
    encodingTable.put("{\\textrtaill}", "\u026D");
    encodingTable.put("{\\textltilde}", "\u026B");
    encodingTable.put("{\\varepsilon}", "\u03B5");
    encodingTable.put("\\mbox{$^1$}", "\u00b9");
    encodingTable.put("\\mbox{$^3$}", "\u00b3");
    encodingTable.put("\\mbox{$^2$}", "\u00b2");
    encodingTable.put("{\\textdegree}", "\u00B0");
    encodingTable.put("{\\textdollar}", "\u0024");
    encodingTable.put("{\\textequals}", "\u003D");
    encodingTable.put("{\\heartsuit}", "\u2661");
    encodingTable.put("{\\spadesuit}", "\u2660");
    encodingTable.put("{\\nsupseteq}", "\u2289");
    encodingTable.put("{\\nsubseteq}", "\u2288");
    encodingTable.put("{\\therefore}", "\u2234");
    encodingTable.put("{\\nparallel}", "\u2226");
    encodingTable.put("{\\textminus}", "\u2212");
    encodingTable.put("{\\downarrow}", "\u2193");
    encodingTable.put("{\\leftarrow}", "\u2190");
    encodingTable.put("{\\supsetneq}", "\u228B");
    encodingTable.put("{\\subsetneq}", "\u228A");
    encodingTable.put("{\\triangleq}", "\u225C");
    encodingTable.put("{\\gtreqless}", "\u22DB");
    encodingTable.put("{\\lesseqgtr}", "\u22DA");
    encodingTable.put("{\\pitchfork}", "\u22D4");
    encodingTable.put("{\\backsimeq}", "\u22CD");
    encodingTable.put("{\\textnaira}", "\u20A6");
    encodingTable.put("{\\subcorner}", "\u0349");
    encodingTable.put("{\\textturnt}", "\u0287");
    encodingTable.put("{\\textctesh}", "\u0286");
    encodingTable.put("{\\textturnr}", "\u0279");
    encodingTable.put("{\\textturnh}", "\u0265");
    encodingTable.put("{\\textgamma}", "\u0263");
    encodingTable.put("{\\textschwa}", "\u0259");
    encodingTable.put("{\\textopeno}", "\u0254");
    encodingTable.put("{\\textturna}", "\u0250");
    encodingTable.put("{\\textKhook}", "\u0198");
    encodingTable.put("{\\textFhook}", "\u0191");
    encodingTable.put("{\\textEopen}", "\u0190");
    encodingTable.put("{\\textChook}", "\u0187");
    encodingTable.put("{\\textOopen}", "\u0186");
    encodingTable.put("{\\textBhook}", "\u0181");
    encodingTable.put("{\\textturnk}", "\u029E");
    encodingTable.put("{\\texthtscg}", "\u029B");
    encodingTable.put("{\\textturny}", "\u028E");
    encodingTable.put("{\\textturnw}", "\u028D");
    encodingTable.put("{\\textturnv}", "\u028C");
    encodingTable.put("{\\textvhook}", "\u028B");
    encodingTable.put("{\\textturnm}", "\u026F");
    encodingTable.put("{\\textbeltl}", "\u026C");
    encodingTable.put("{\\textDhook}", "\u018a");
    encodingTable.put("{\\texttslig}", "\u02A6");
    encodingTable.put("{\\textdzlig}", "\u02A3");
    encodingTable.put("{\\textYhook}", "\u01b3");
    encodingTable.put("{\\textVhook}", "\u01b2");
    encodingTable.put("{\\textThook}", "\u01ac");
    encodingTable.put("{\\textPhook}", "\u01a4");
    encodingTable.put("{\\textyhook}", "\u01B4");
    encodingTable.put("{\\copyright}", "\u00a9");
    encodingTable.put("{\\clubsuit}", "\u2663");
    encodingTable.put("{\\sqsupset}", "\u2290");
    encodingTable.put("{\\supseteq}", "\u2287");
    encodingTable.put("{\\subseteq}", "\u2286");
    encodingTable.put("{\\doteqdot}", "\u2251");
    encodingTable.put("{\\parallel}", "\u2225");
    encodingTable.put("{\\setminus}", "\u2216");
    encodingTable.put("{\\boxminus}", "\u229F");
    encodingTable.put("{\\sqsubset}", "\u228F");
    encodingTable.put("{\\approxeq}", "\u224A");
    encodingTable.put("{\\succnsim}", "\u22E9");
    encodingTable.put("{\\precnsim}", "\u22E8");
    encodingTable.put("{\\curlyvee}", "\u22CE");
    encodingTable.put("{\\bigwedge}", "\u22C0");
    encodingTable.put("{\\barwedge}", "\u22BC");
    encodingTable.put("{\\intercal}", "\u22BA");
    encodingTable.put("{\\multimap}", "\u22B8");
    encodingTable.put("{\\boxtimes}", "\u22A0");
    encodingTable.put("{\\textdong}", "\u20AB");
    encodingTable.put("{\\textlira}", "\u20A4");
    encodingTable.put("{\\textyogh}", "\u0292");
    encodingTable.put("{\\textbaru}", "\u0289");
    encodingTable.put("{\\textbaro}", "\u0275");
    encodingTable.put("{\\textiota}", "\u0269");
    encodingTable.put("{\\textbari}", "\u0268");
    encodingTable.put("{\\textreve}", "\u0258");
    encodingTable.put("{\\texthbar}", "\u0127");
    encodingTable.put("{\\textHbar}", "\u0126");
    encodingTable.put("{\\textlhti}", "\u027F");
    encodingTable.put("{\\varsigma}", "\u03C2");
    encodingTable.put("{\\vartheta}", "\u03B8");
    encodingTable.put("{\\texttesh}", "\u02A7");
    encodingTable.put("{\\textcent}", "\u00A2");
    encodingTable.put("{\\textless}", "\u003C");
    encodingTable.put("{\\gtrless}", "\u2277");
    encodingTable.put("{\\lessgtr}", "\u2276");
    encodingTable.put("{\\lesssim}", "\u2274");
    encodingTable.put("{\\lesssim}", "\u2272");
    encodingTable.put("{\\because}", "\u2235");
    encodingTable.put("{\\dotplus}", "\u2214");
    encodingTable.put("{\\nexists}", "\u2204");
    encodingTable.put("{\\partial}", "\u2202");
    encodingTable.put("{\\uparrow}", "\u2191");
    encodingTable.put("{\\natural}", "\u266E");
    encodingTable.put("{\\boxplus}", "\u229E");
    encodingTable.put("{\\succsim}", "\u227F");
    encodingTable.put("{\\precsim}", "\u227E");
    encodingTable.put("{\\between}", "\u226C");
    encodingTable.put("{\\backsim}", "\u223D");
    encodingTable.put("{\\leadsto}", "\u219D");
    encodingTable.put("{\\lessdot}", "\u22D6");
    encodingTable.put("{\\diamond}", "\u22C4");
    encodingTable.put("{\\textwon}", "\u20A9");
    encodingTable.put("{\\Epsilon}", "\u0395");
    encodingTable.put("{\\sliding}", "\u0362");
    encodingTable.put("{\\subrptr}", "\u0355");
    encodingTable.put("{\\sublptr}", "\u0354");
    encodingTable.put("{\\textscb}", "\u0299");
    encodingTable.put("{\\textctz}", "\u0291");
    encodingTable.put("{\\textesh}", "\u0283");
    encodingTable.put("{\\textscr}", "\u0280");
    encodingTable.put("{\\textphi}", "\u0278");
    encodingTable.put("{\\textscn}", "\u0274");
    encodingTable.put("{\\texthth}", "\u0266");
    encodingTable.put("{\\textscg}", "\u0262");
    encodingTable.put("{\\texthtg}", "\u0260");
    encodingTable.put("{\\texthtd}", "\u0257");
    encodingTable.put("{\\textctc}", "\u0255");
    encodingTable.put("{\\texthtb}", "\u0253");
    encodingTable.put("{\\texthtk}", "\u0199");
    encodingTable.put("{\\texthtc}", "\u0188");
    encodingTable.put("{\\textcrb}", "\u0180");
    encodingTable.put("{\\textkra}", "\u0138");
    encodingTable.put("{\\Omicron}", "\u039F");
    encodingTable.put("{\\whistle}", "\u034E");
    encodingTable.put("{\\textscl}", "\u029F");
    encodingTable.put("{\\textctj}", "\u029D");
    encodingTable.put("{\\textsch}", "\u029C");
    encodingTable.put("{\\textscy}", "\u028F");
    encodingTable.put("{\\textsci}", "\u026A");
    encodingTable.put("{\\upsilon}", "\u03C5");
    encodingTable.put("{\\omicron}", "\u03BF");
    encodingTable.put("{\\Upsilon}", "\u03A5");
    encodingTable.put("{\\texthtq}", "\u02A0");
    encodingTable.put("{\\texthtt}", "\u01ad");
    encodingTable.put("{\\texthtp}", "\u01a5");
    encodingTable.put("{\\textEzh}", "\u01B7");
    encodingTable.put("{\\textyen}", "\u00A5");
    encodingTable.put("{\\textbar}", "\u007C");
    encodingTable.put("{\\oslash}", "\u2298");
    encodingTable.put("{\\otimes}", "\u2297");
    encodingTable.put("{\\ominus}", "\u2296");
    encodingTable.put("{\\supset}", "\u2285");
    encodingTable.put("{\\subset}", "\u2284");
    encodingTable.put("{\\supset}", "\u2283");
    encodingTable.put("{\\subset}", "\u2282");
    encodingTable.put("{\\gtrsim}", "\u2275");
    encodingTable.put("{\\gtrsim}", "\u2273");
    encodingTable.put("{\\circeq}", "\u2257");
    encodingTable.put("{\\eqcirc}", "\u2256");
    encodingTable.put("{\\approx}", "\u2249");
    encodingTable.put("{\\approx}", "\u2248");
    encodingTable.put("{\\bullet}", "\u2219");
    encodingTable.put("{\\coprod}", "\u2210");
    encodingTable.put("{\\exists}", "\u2203");
    encodingTable.put("{\\forall}", "\u2200");
    encodingTable.put("{\\S{M}}", "\u2120");
    encodingTable.put("{\\rfloor}", "\u230B");
    encodingTable.put("{\\lfloor}", "\u230A");
    encodingTable.put("{\\bumpeq}", "\u224F");
    encodingTable.put("{\\Bumpeq}", "\u224E");
    encodingTable.put("{\\propto}", "\u221D");
    encodingTable.put("{\\rangle}", "\u27E9");
    encodingTable.put("{\\langle}", "\u27E8");
    encodingTable.put("{\\gtrdot}", "\u22D7");
    encodingTable.put("{\\Supset}", "\u22D1");
    encodingTable.put("{\\Subset}", "\u22D0");
    encodingTable.put("{\\rtimes}", "\u22CA");
    encodingTable.put("{\\ltimes}", "\u22C9");
    encodingTable.put("{\\bowtie}", "\u22C8");
    encodingTable.put("{\\bigcup}", "\u22C3");
    encodingTable.put("{\\bigcap}", "\u22C2");
    encodingTable.put("{\\bigvee}", "\u22C1");
    encodingTable.put("{\\veebar}", "\u22BB");
    encodingTable.put("{\\nVdash}", "\u22AE");
    encodingTable.put("{\\Vvdash}", "\u22AA");
    encodingTable.put("{\\boxdot}", "\u22A1");
    encodingTable.put("{\\h{v}}", "\u0195");
    encodingTable.put("{\\'{Z}}", "\u0179");
    encodingTable.put("{\\^{y}}", "\u0177");
    encodingTable.put("{\\^{Y}}", "\u0176");
    encodingTable.put("{\\^{w}}", "\u0175");
    encodingTable.put("{\\^{W}}", "\u0174");
    encodingTable.put("{\\c{u}}", "\u0173");
    encodingTable.put("{\\c{U}}", "\u0172");
    encodingTable.put("{\\H{u}}", "\u0171");
    encodingTable.put("{\\H{U}}", "\u0170");
    encodingTable.put("{\\~{u}}", "\u0169");
    encodingTable.put("{\\~{U}}", "\u0168");
    encodingTable.put("{\\v{t}}", "\u0165");
    encodingTable.put("{\\v{T}}", "\u0164");
    encodingTable.put("{\\c{t}}", "\u0163");
    encodingTable.put("{\\c{T}}", "\u0162");
    encodingTable.put("{\\v{s}}", "\u0161");
    encodingTable.put("{\\v{S}}", "\u0160");
    encodingTable.put("{\\v{r}}", "\u0159");
    encodingTable.put("{\\v{R}}", "\u0158");
    encodingTable.put("{\\c{r}}", "\u0157");
    encodingTable.put("{\\c{R}}", "\u0156");
    encodingTable.put("{\\'{r}}", "\u0155");
    encodingTable.put("{\\'{R}}", "\u0154");
    encodingTable.put("{\\o{e}}", "\u0153");
    encodingTable.put("{\\O{E}}", "\u0152");
    encodingTable.put("{\\H{o}}", "\u0151");
    encodingTable.put("{\\H{O}}", "\u0150");
    encodingTable.put("{\\v{n}}", "\u0148");
    encodingTable.put("{\\v{N}}", "\u0147");
    encodingTable.put("{\\c{n}}", "\u0146");
    encodingTable.put("{\\c{N}}", "\u0145");
    encodingTable.put("{\\'{n}}", "\u0144");
    encodingTable.put("{\\'{N}}", "\u0143");
    encodingTable.put("{\\'{L}}", "\u0139");
    encodingTable.put("{\\c{k}}", "\u0137");
    encodingTable.put("{\\c{K}}", "\u0136");
    encodingTable.put("{\\^{J}}", "\u0134");
    encodingTable.put("{\\i{j}}", "\u0133");
    encodingTable.put("{\\I{J}}", "\u0132");
    encodingTable.put("{\\.{I}}", "\u0130");
    encodingTable.put("{\\~{I}}", "\u0128");
    encodingTable.put("{\\^{h}}", "\u0125");
    encodingTable.put("{\\^{H}}", "\u0124");
    encodingTable.put("{\\c{g}}", "\u0123");
    encodingTable.put("{\\c{G}}", "\u0122");
    encodingTable.put("{\\.{g}}", "\u0121");
    encodingTable.put("{\\.{G}}", "\u0120");
    encodingTable.put("{\\c{e}}", "\u0119");
    encodingTable.put("{\\c{E}}", "\u0118");
    encodingTable.put("{\\.{e}}", "\u0117");
    encodingTable.put("{\\.{E}}", "\u0116");
    encodingTable.put("{\\u{e}}", "\u0115");
    encodingTable.put("{\\u{E}}", "\u0114");
    encodingTable.put("{\\={e}}", "\u0113");
    encodingTable.put("{\\={E}}", "\u0112");
    encodingTable.put("{\\d{j}}", "\u0111");
    encodingTable.put("{\\D{J}}", "\u0110");
    encodingTable.put("{\\^{c}}", "\u0109");
    encodingTable.put("{\\^{C}}", "\u0108");
    encodingTable.put("{\\'{c}}", "\u0107");
    encodingTable.put("{\\'{C}}", "\u0106");
    encodingTable.put("{\\c{a}}", "\u0105");
    encodingTable.put("{\\c{A}}", "\u0104");
    encodingTable.put("{\\u{a}}", "\u0103");
    encodingTable.put("{\\u{A}}", "\u0102");
    encodingTable.put("{\\={a}}", "\u0101");
    encodingTable.put("{\\={A}}", "\u0100");
    encodingTable.put("{\\Lambda}", "\u039B");
    encodingTable.put("{\\v{z}}", "\u017e");
    encodingTable.put("{\\v{Z}}", "\u017d");
    encodingTable.put("{\\.{Z}}", "\u017c");
    encodingTable.put("{\\.{Z}}", "\u017b");
    encodingTable.put("{\\'{Z}}", "\u017a");
    encodingTable.put("{\\r{u}}", "\u016f");
    encodingTable.put("{\\r{U}}", "\u016e");
    encodingTable.put("{\\u{u}}", "\u016d");
    encodingTable.put("{\\u{U}}", "\u016c");
    encodingTable.put("{\\={u}}", "\u016b");
    encodingTable.put("{\\={U}}", "\u016a");
    encodingTable.put("{\\c{s}}", "\u015f");
    encodingTable.put("{\\c{S}}", "\u015e");
    encodingTable.put("{\\^{s}}", "\u015d");
    encodingTable.put("{\\^{S}}", "\u015c");
    encodingTable.put("{\\'{s}}", "\u015b");
    encodingTable.put("{\\'{S}}", "\u015a");
    encodingTable.put("{\\u{o}}", "\u014f");
    encodingTable.put("{\\u{O}}", "\u014e");
    encodingTable.put("{\\={o}}", "\u014d");
    encodingTable.put("{\\={O}}", "\u014c");
    encodingTable.put("{\\n{g}}", "\u014b");
    encodingTable.put("{\\N{G}}", "\u014a");
    encodingTable.put("{\\v{l}}", "\u013e");
    encodingTable.put("{\\v{L}}", "\u013d");
    encodingTable.put("{\\c{l}}", "\u013c");
    encodingTable.put("{\\c{L}}", "\u013b");
    encodingTable.put("{\\'{l}}", "\u013a");
    encodingTable.put("{\\c{i}}", "\u012f");
    encodingTable.put("{\\c{I}}", "\u012e");
    encodingTable.put("{\\u{I}}", "\u012c");
    encodingTable.put("{\\={I}}", "\u012a");
    encodingTable.put("{\\u{g}}", "\u011f");
    encodingTable.put("{\\u{G}}", "\u011e");
    encodingTable.put("{\\^{g}}", "\u011d");
    encodingTable.put("{\\^{G}}", "\u011c");
    encodingTable.put("{\\v{e}}", "\u011b");
    encodingTable.put("{\\v{E}}", "\u011a");
    encodingTable.put("{\\v{d}}", "\u010f");
    encodingTable.put("{\\v{D}}", "\u010e");
    encodingTable.put("{\\v{c}}", "\u010d");
    encodingTable.put("{\\v{C}}", "\u010c");
    encodingTable.put("{\\.{c}}", "\u010b");
    encodingTable.put("{\\.{C}}", "\u010a");
    encodingTable.put("{\\varphi}", "\u03C6");
    encodingTable.put("{\\varrho}", "\u03C1");
    encodingTable.put("{\\lambda}", "\u03BB");
    encodingTable.put("{\\'{g}}", "\u01f5");
    encodingTable.put("{\\'{G}}", "\u01f4");
    encodingTable.put("{\\c{o}}", "\u01eb");
    encodingTable.put("{\\c{O}}", "\u01ea");
    encodingTable.put("{\\v{k}}", "\u01e9");
    encodingTable.put("{\\v{K}}", "\u01e8");
    encodingTable.put("{\\v{g}}", "\u01e7");
    encodingTable.put("{\\v{G}}", "\u01e6");
    encodingTable.put("{\\v{u}}", "\u01d4");
    encodingTable.put("{\\v{U}}", "\u01d3");
    encodingTable.put("{\\v{o}}", "\u01d2");
    encodingTable.put("{\\v{O}}", "\u01d1");
    encodingTable.put("{\\v{I}}", "\u01cf");
    encodingTable.put("{\\v{a}}", "\u01ce");
    encodingTable.put("{\\v{A}}", "\u01cd");
    encodingTable.put("{\\t{h}}", "\u00fe");
    encodingTable.put("{\\'{y}}", "\u00fd");
    encodingTable.put("{\\^{u}}", "\u00fb");
    encodingTable.put("{\\'{u}}", "\u00fa");
    encodingTable.put("{\\`{u}}", "\u00f9");
    encodingTable.put("{\\~{o}}", "\u00f5");
    encodingTable.put("{\\^{o}}", "\u00f4");
    encodingTable.put("{\\'{o}}", "\u00f3");
    encodingTable.put("{\\`{o}}", "\u00f2");
    encodingTable.put("{\\~{n}}", "\u00f1");
    encodingTable.put("{\\d{h}}", "\u00f0");
    encodingTable.put("{\\^{e}}", "\u00ea");
    encodingTable.put("{\\'{e}}", "\u00e9");
    encodingTable.put("{\\`{e}}", "\u00e8");
    encodingTable.put("{\\c{c}}", "\u00e7");
    encodingTable.put("{\\a{e}}", "\u00e6");
    encodingTable.put("{\\a{a}}", "\u00e5");
    encodingTable.put("{\\~{a}}", "\u00e3");
    encodingTable.put("{\\^{a}}", "\u00e2");
    encodingTable.put("{\\'{a}}", "\u00e1");
    encodingTable.put("{\\`{a}}", "\u00e0");
    encodingTable.put("{\\s{s}}", "\u00df");
    encodingTable.put("{\\T{H}}", "\u00de");
    encodingTable.put("{\\'{Y}}", "\u00dd");
    encodingTable.put("{\\^{U}}", "\u00db");
    encodingTable.put("{\\'{U}}", "\u00da");
    encodingTable.put("{\\`{U}}", "\u00d9");
    encodingTable.put("{\\~{O}}", "\u00d5");
    encodingTable.put("{\\^{O}}", "\u00d4");
    encodingTable.put("{\\'{O}}", "\u00d3");
    encodingTable.put("{\\`{O}}", "\u00d2");
    encodingTable.put("{\\~{N}}", "\u00d1");
    encodingTable.put("{\\D{H}}", "\u00d0");
    encodingTable.put("{\\^{I}}", "\u00ce");
    encodingTable.put("{\\'{I}}", "\u00cd");
    encodingTable.put("{\\`{I}}", "\u00cc");
    encodingTable.put("{\\^{E}}", "\u00ca");
    encodingTable.put("{\\'{E}}", "\u00c9");
    encodingTable.put("{\\`{E}}", "\u00c8");
    encodingTable.put("{\\c{C}}", "\u00c7");
    encodingTable.put("{\\A{E}}", "\u00c6");
    encodingTable.put("{\\A{A}}", "\u00c5");
    encodingTable.put("{\\~{A}}", "\u00c3");
    encodingTable.put("{\\^{A}}", "\u00c2");
    encodingTable.put("{\\'{A}}", "\u00c1");
    encodingTable.put("{\\`{A}}", "\u00c0");
    encodingTable.put("{\\pounds}", "\u00a3");
    encodingTable.put("{\\rceil}", "\u2309");
    encodingTable.put("{\\lceil}", "\u2308");
    encodingTable.put("{\\oplus}", "\u2295");
    encodingTable.put("{\\sqcup}", "\u2294");
    encodingTable.put("{\\sqcap}", "\u2293");
    encodingTable.put("{\\nsucc}", "\u2281");
    encodingTable.put("{\\nprec}", "\u2280");
    encodingTable.put("{\\gneqq}", "\u2269");
    encodingTable.put("{\\lneqq}", "\u2268");
    encodingTable.put("{\\equiv}", "\u2262");
    encodingTable.put("{\\equiv}", "\u2261");
    encodingTable.put("{\\doteq}", "\u2250");
    encodingTable.put("{\\ncong}", "\u2247");
    encodingTable.put("{\\simeq}", "\u2244");
    encodingTable.put("{\\simeq}", "\u2243");
    encodingTable.put("{\\wedge}", "\u2227");
    encodingTable.put("{\\angle}", "\u2220");
    encodingTable.put("{\\notin}", "\u2209");
    encodingTable.put("{\\nabla}", "\u2207");
    encodingTable.put("{\\Delta}", "\u2206");
    encodingTable.put("{\\9{}}", "\u2079");
    encodingTable.put("{\\8{}}", "\u2078");
    encodingTable.put("{\\7{}}", "\u2077");
    encodingTable.put("{\\6{}}", "\u2076");
    encodingTable.put("{\\5{}}", "\u2075");
    encodingTable.put("{\\4{}}", "\u2074");
    encodingTable.put("{\\i{}}", "\u2071");
    encodingTable.put("{\\ldots}", "\u2026");
    encodingTable.put("{\\sharp}", "\u266F");
    encodingTable.put("{\\uplus}", "\u228E");
    encodingTable.put("{\\nless}", "\u226E");
    encodingTable.put("{\\asymp}", "\u226D");
    encodingTable.put("{\\asymp}", "\u224D");
    encodingTable.put("{\\iiint}", "\u222D");
    encodingTable.put("{\\infty}", "\u221E");
    encodingTable.put("{\\){}}", "\u207E");
    encodingTable.put("{\\({}}", "\u207D");
    encodingTable.put("{\\-{}}", "\u207B");
    encodingTable.put("{\\+{}}", "\u207A");
    encodingTable.put("{\\ddots}", "\u22F1");
    encodingTable.put("{\\cdots}", "\u22EF");
    encodingTable.put("{\\vdots}", "\u22EE");
    encodingTable.put("{\\gnsim}", "\u22E7");
    encodingTable.put("{\\lnsim}", "\u22E6");
    encodingTable.put("{\\unrhd}", "\u22B5");
    encodingTable.put("{\\unlhd}", "\u22B4");
    encodingTable.put("{\\Vdash}", "\u22A9");
    encodingTable.put("{\\dashv}", "\u22A3");
    encodingTable.put("{\\vdash}", "\u22A2");
    encodingTable.put("{\\Theta}", "\u0398");
    encodingTable.put("{\\Delta}", "\u0394");
    encodingTable.put("{\\Gamma}", "\u0393");
    encodingTable.put("{\\Alpha}", "\u0391");
    encodingTable.put("{\\B{}}", "\u0335");
    encodingTable.put("{\\b{}}", "\u0331");
    encodingTable.put("{\\k{}}", "\u0328");
    encodingTable.put("{\\c{}}", "\u0327");
    encodingTable.put("{\\d{}}", "\u0323");
    encodingTable.put("{\\M{}}", "\u0322");
    encodingTable.put("{\\t{}}", "\u0311");
    encodingTable.put("{\\r{}}", "\u030A");
    encodingTable.put("{\\.{}}", "\u0307");
    encodingTable.put("{\\u{}}", "\u0306");
    encodingTable.put("{\\={}}", "\u0304");
    encodingTable.put("{\\~{}}", "\u0303");
    encodingTable.put("{\\^{}}", "\u0302");
    encodingTable.put("{\\'{}}", "\u0301");
    encodingTable.put("{\\`{}}", "\u0300");
    encodingTable.put("{\\j{}}", "\u0237");
    encodingTable.put("{\\l{}}", "\u0142");
    encodingTable.put("{\\L{}}", "\u0141");
    encodingTable.put("{\\i{}}", "\u0131");
    encodingTable.put("{\\Kappa}", "\u039A");
    encodingTable.put("{\\G{}}", "\u030F");
    encodingTable.put("{\\v{}}", "\u030C");
    encodingTable.put("{\\H{}}", "\u030B");
    encodingTable.put("{\\omega}", "\u03C9");
    encodingTable.put("{\\sigma}", "\u03C3");
    encodingTable.put("{\\kappa}", "\u03BA");
    encodingTable.put("{\\delta}", "\u03B4");
    encodingTable.put("{\\gamma}", "\u03B3");
    encodingTable.put("{\\alpha}", "\u03B1");
    encodingTable.put("{\\Omega}", "\u03A9");
    encodingTable.put("{\\Sigma}", "\u03A3");
    encodingTable.put("{\\H{}}", "\u02dd");
    encodingTable.put("{\\~{}}", "\u02dc");
    encodingTable.put("{\\c{}}", "\u02db");
    encodingTable.put("{\\r{}}", "\u02da");
    encodingTable.put("{\\.{}}", "\u02d9");
    encodingTable.put("{\\u{}}", "\u02d8");
    encodingTable.put("{\\v{}}", "\u02c7");
    encodingTable.put("{\\^{}}", "\u02c6");
    encodingTable.put("{\\tone1}", "\u02E9");
    encodingTable.put("{\\tone2}", "\u02E8");
    encodingTable.put("{\\tone3}", "\u02E7");
    encodingTable.put("{\\tone4}", "\u02E6");
    encodingTable.put("{\\tone5}", "\u02E5");
    encodingTable.put("{\\hamza}", "\u02BE");
    encodingTable.put("{\\j{}}", "\u02B2");
    encodingTable.put("{\\h{}}", "\u02B0");
    encodingTable.put("{\\'\\ae}", "\u01fd");
    encodingTable.put("{\\'\\AE}", "\u01fc");
    encodingTable.put("{d\\v{z}}", "\u01c6");
    encodingTable.put("{D\\v{z}}", "\u01c5");
    encodingTable.put("{D\\v{Z}}", "\u01c4");
    encodingTable.put("{\\uhorn}", "\u01b0");
    encodingTable.put("{\\UHORN}", "\u01af");
    encodingTable.put("{\\ohorn}", "\u01a1");
    encodingTable.put("{\\OHORN}", "\u01a0");
    encodingTable.put("{\\o{}}", "\u00f8");
    encodingTable.put("{\\\"\\i}", "\u00ef");
    encodingTable.put("{\\O{}}", "\u00d8");
    encodingTable.put("{\\P{}}", "\u00b6");
    encodingTable.put("{\\S{}}", "\u00a7");
    encodingTable.put("{\\times}", "\u00D7");
    encodingTable.put("{\\micro}", "\u00B5");
    encodingTable.put("{\\&{}}", "\u0026");
    encodingTable.put("{\\#{}}", "\u0023");
    encodingTable.put("$^+$", "\u207A");
    encodingTable.put("$^-$", "\u207B");
    encodingTable.put("{\\odot}", "\u2299");
    encodingTable.put("{\\ngeq}", "\u2271");
    encodingTable.put("{\\nleq}", "\u2270");
    encodingTable.put("{\\geqq}", "\u2267");
    encodingTable.put("{\\leqq}", "\u2266");
    encodingTable.put("{\\cong}", "\u2245");
    encodingTable.put("{\\nsim}", "\u2241");
    encodingTable.put("{\\nmid}", "\u2224");
    encodingTable.put("{\\circ}", "\u2218");
    encodingTable.put("{\\S{M}}", "\u2120");
    encodingTable.put("{\\SM}", "\u2120");
    encodingTable.put("{\\ddag}", "\u2021");
    encodingTable.put("{\\flat}", "\u266D");
    encodingTable.put("{\\succ}", "\u227B");
    encodingTable.put("{\\prec}", "\u227A");
    encodingTable.put("{\\ngtr}", "\u226F");
    encodingTable.put("{\\oint}", "\u222E");
    encodingTable.put("{\\iint}", "\u222C");
    encodingTable.put("{\\surd}", "\u221A");
    encodingTable.put("{\\prod}", "\u220F");
    encodingTable.put("{\\star}", "\u22C6");
    encodingTable.put("{\\cdot}", "\u22C5");
    encodingTable.put("{\\euro}", "\u20AC");
    encodingTable.put("{\\Iota}", "\u0399");
    encodingTable.put("{\\Zeta}", "\u0396");
    encodingTable.put("{\\Beta}", "\u0392");
    encodingTable.put("{\\\"{}}", "\u0308");
    encodingTable.put("{\\h{v}}", "\u0195");
    encodingTable.put("{\\hv}", "\u0195");
    encodingTable.put("{\\'{Z}}", "\u0179");
    encodingTable.put("{\\'Z}", "\u0179");
    encodingTable.put("{\\^{y}}", "\u0177");
    encodingTable.put("{\\^y}", "\u0177");
    encodingTable.put("{\\^{Y}}", "\u0176");
    encodingTable.put("{\\^Y}", "\u0176");
    encodingTable.put("{\\^{w}}", "\u0175");
    encodingTable.put("{\\^w}", "\u0175");
    encodingTable.put("{\\^{W}}", "\u0174");
    encodingTable.put("{\\^W}", "\u0174");
    encodingTable.put("{\\c{u}}", "\u0173");
    encodingTable.put("{\\cu}", "\u0173");
    encodingTable.put("{\\c{U}}", "\u0172");
    encodingTable.put("{\\cU}", "\u0172");
    encodingTable.put("{\\H{u}}", "\u0171");
    encodingTable.put("{\\Hu}", "\u0171");
    encodingTable.put("{\\H{U}}", "\u0170");
    encodingTable.put("{\\HU}", "\u0170");
    encodingTable.put("{\\~{u}}", "\u0169");
    encodingTable.put("{\\~u}", "\u0169");
    encodingTable.put("{\\~{U}}", "\u0168");
    encodingTable.put("{\\~U}", "\u0168");
    encodingTable.put("{\\v{t}}", "\u0165");
    encodingTable.put("{\\vt}", "\u0165");
    encodingTable.put("{\\v{T}}", "\u0164");
    encodingTable.put("{\\vT}", "\u0164");
    encodingTable.put("{\\c{t}}", "\u0163");
    encodingTable.put("{\\ct}", "\u0163");
    encodingTable.put("{\\c{T}}", "\u0162");
    encodingTable.put("{\\cT}", "\u0162");
    encodingTable.put("{\\v{s}}", "\u0161");
    encodingTable.put("{\\vs}", "\u0161");
    encodingTable.put("{\\v{S}}", "\u0160");
    encodingTable.put("{\\vS}", "\u0160");
    encodingTable.put("{\\v{r}}", "\u0159");
    encodingTable.put("{\\vr}", "\u0159");
    encodingTable.put("{\\v{R}}", "\u0158");
    encodingTable.put("{\\vR}", "\u0158");
    encodingTable.put("{\\c{r}}", "\u0157");
    encodingTable.put("{\\cr}", "\u0157");
    encodingTable.put("{\\c{R}}", "\u0156");
    encodingTable.put("{\\cR}", "\u0156");
    encodingTable.put("{\\'{r}}", "\u0155");
    encodingTable.put("{\\'r}", "\u0155");
    encodingTable.put("{\\'{R}}", "\u0154");
    encodingTable.put("{\\'R}", "\u0154");
    encodingTable.put("{\\o{e}}", "\u0153");
    encodingTable.put("{\\oe}", "\u0153");
    encodingTable.put("{\\O{E}}", "\u0152");
    encodingTable.put("{\\OE}", "\u0152");
    encodingTable.put("{\\H{o}}", "\u0151");
    encodingTable.put("{\\Ho}", "\u0151");
    encodingTable.put("{\\H{O}}", "\u0150");
    encodingTable.put("{\\HO}", "\u0150");
    encodingTable.put("{\\v{n}}", "\u0148");
    encodingTable.put("{\\vn}", "\u0148");
    encodingTable.put("{\\v{N}}", "\u0147");
    encodingTable.put("{\\vN}", "\u0147");
    encodingTable.put("{\\c{n}}", "\u0146");
    encodingTable.put("{\\cn}", "\u0146");
    encodingTable.put("{\\c{N}}", "\u0145");
    encodingTable.put("{\\cN}", "\u0145");
    encodingTable.put("{\\'{n}}", "\u0144");
    encodingTable.put("{\\'n}", "\u0144");
    encodingTable.put("{\\'{N}}", "\u0143");
    encodingTable.put("{\\'N}", "\u0143");
    encodingTable.put("{\\'{L}}", "\u0139");
    encodingTable.put("{\\'L}", "\u0139");
    encodingTable.put("{\\c{k}}", "\u0137");
    encodingTable.put("{\\ck}", "\u0137");
    encodingTable.put("{\\c{K}}", "\u0136");
    encodingTable.put("{\\cK}", "\u0136");
    encodingTable.put("{\\^\\j}", "\u0135");
    encodingTable.put("{\\^{J}}", "\u0134");
    encodingTable.put("{\\^J}", "\u0134");
    encodingTable.put("{\\i{j}}", "\u0133");
    encodingTable.put("{\\ij}", "\u0133");
    encodingTable.put("{\\I{J}}", "\u0132");
    encodingTable.put("{\\IJ}", "\u0132");
    encodingTable.put("{\\.{I}}", "\u0130");
    encodingTable.put("{\\.I}", "\u0130");
    encodingTable.put("{\\~\\i}", "\u0129");
    encodingTable.put("{\\~{I}}", "\u0128");
    encodingTable.put("{\\~I}", "\u0128");
    encodingTable.put("{\\^{h}}", "\u0125");
    encodingTable.put("{\\^h}", "\u0125");
    encodingTable.put("{\\^{H}}", "\u0124");
    encodingTable.put("{\\^H}", "\u0124");
    encodingTable.put("{\\c{g}}", "\u0123");
    encodingTable.put("{\\cg}", "\u0123");
    encodingTable.put("{\\c{G}}", "\u0122");
    encodingTable.put("{\\cG}", "\u0122");
    encodingTable.put("{\\.{g}}", "\u0121");
    encodingTable.put("{\\.g}", "\u0121");
    encodingTable.put("{\\.{G}}", "\u0120");
    encodingTable.put("{\\.G}", "\u0120");
    encodingTable.put("{\\c{e}}", "\u0119");
    encodingTable.put("{\\ce}", "\u0119");
    encodingTable.put("{\\c{E}}", "\u0118");
    encodingTable.put("{\\cE}", "\u0118");
    encodingTable.put("{\\.{e}}", "\u0117");
    encodingTable.put("{\\.e}", "\u0117");
    encodingTable.put("{\\.{E}}", "\u0116");
    encodingTable.put("{\\.E}", "\u0116");
    encodingTable.put("{\\u{e}}", "\u0115");
    encodingTable.put("{\\ue}", "\u0115");
    encodingTable.put("{\\u{E}}", "\u0114");
    encodingTable.put("{\\uE}", "\u0114");
    encodingTable.put("{\\={e}}", "\u0113");
    encodingTable.put("{\\=e}", "\u0113");
    encodingTable.put("{\\={E}}", "\u0112");
    encodingTable.put("{\\=E}", "\u0112");
    encodingTable.put("{\\d{j}}", "\u0111");
    encodingTable.put("{\\dj}", "\u0111");
    encodingTable.put("{\\D{J}}", "\u0110");
    encodingTable.put("{\\DJ}", "\u0110");
    encodingTable.put("{\\^{c}}", "\u0109");
    encodingTable.put("{\\^c}", "\u0109");
    encodingTable.put("{\\^{C}}", "\u0108");
    encodingTable.put("{\\^C}", "\u0108");
    encodingTable.put("{\\'{c}}", "\u0107");
    encodingTable.put("{\\'c}", "\u0107");
    encodingTable.put("{\\'{C}}", "\u0106");
    encodingTable.put("{\\'C}", "\u0106");
    encodingTable.put("{\\c{a}}", "\u0105");
    encodingTable.put("{\\ca}", "\u0105");
    encodingTable.put("{\\c{A}}", "\u0104");
    encodingTable.put("{\\cA}", "\u0104");
    encodingTable.put("{\\u{a}}", "\u0103");
    encodingTable.put("{\\ua}", "\u0103");
    encodingTable.put("{\\u{A}}", "\u0102");
    encodingTable.put("{\\uA}", "\u0102");
    encodingTable.put("{\\={a}}", "\u0101");
    encodingTable.put("{\\=a}", "\u0101");
    encodingTable.put("{\\={A}}", "\u0100");
    encodingTable.put("{\\=A}", "\u0100");
    encodingTable.put("{\\v{z}}", "\u017e");
    encodingTable.put("{\\vz}", "\u017e");
    encodingTable.put("{\\v{Z}}", "\u017d");
    encodingTable.put("{\\vZ}", "\u017d");
    encodingTable.put("{\\.{Z}}", "\u017c");
    encodingTable.put("{\\.Z}", "\u017c");
    encodingTable.put("{\\.{Z}}", "\u017b");
    encodingTable.put("{\\.Z}", "\u017b");
    encodingTable.put("{\\'{Z}}", "\u017a");
    encodingTable.put("{\\'Z}", "\u017a");
    encodingTable.put("{\\r{u}}", "\u016f");
    encodingTable.put("{\\ru}", "\u016f");
    encodingTable.put("{\\r{U}}", "\u016e");
    encodingTable.put("{\\rU}", "\u016e");
    encodingTable.put("{\\u{u}}", "\u016d");
    encodingTable.put("{\\uu}", "\u016d");
    encodingTable.put("{\\u{U}}", "\u016c");
    encodingTable.put("{\\uU}", "\u016c");
    encodingTable.put("{\\={u}}", "\u016b");
    encodingTable.put("{\\=u}", "\u016b");
    encodingTable.put("{\\={U}}", "\u016a");
    encodingTable.put("{\\=U}", "\u016a");
    encodingTable.put("{\\c{s}}", "\u015f");
    encodingTable.put("{\\cs}", "\u015f");
    encodingTable.put("{\\c{S}}", "\u015e");
    encodingTable.put("{\\cS}", "\u015e");
    encodingTable.put("{\\^{s}}", "\u015d");
    encodingTable.put("{\\^s}", "\u015d");
    encodingTable.put("{\\^{S}}", "\u015c");
    encodingTable.put("{\\^S}", "\u015c");
    encodingTable.put("{\\'{s}}", "\u015b");
    encodingTable.put("{\\'s}", "\u015b");
    encodingTable.put("{\\'{S}}", "\u015a");
    encodingTable.put("{\\'S}", "\u015a");
    encodingTable.put("{\\u{o}}", "\u014f");
    encodingTable.put("{\\uo}", "\u014f");
    encodingTable.put("{\\u{O}}", "\u014e");
    encodingTable.put("{\\uO}", "\u014e");
    encodingTable.put("{\\={o}}", "\u014d");
    encodingTable.put("{\\=o}", "\u014d");
    encodingTable.put("{\\={O}}", "\u014c");
    encodingTable.put("{\\=O}", "\u014c");
    encodingTable.put("{\\n{g}}", "\u014b");
    encodingTable.put("{\\ng}", "\u014b");
    encodingTable.put("{\\N{G}}", "\u014a");
    encodingTable.put("{\\NG}", "\u014a");
    encodingTable.put("{\\v{l}}", "\u013e");
    encodingTable.put("{\\vl}", "\u013e");
    encodingTable.put("{\\v{L}}", "\u013d");
    encodingTable.put("{\\vL}", "\u013d");
    encodingTable.put("{\\c{l}}", "\u013c");
    encodingTable.put("{\\cl}", "\u013c");
    encodingTable.put("{\\c{L}}", "\u013b");
    encodingTable.put("{\\cL}", "\u013b");
    encodingTable.put("{\\'{l}}", "\u013a");
    encodingTable.put("{\\'l}", "\u013a");
    encodingTable.put("{\\c{i}}", "\u012f");
    encodingTable.put("{\\ci}", "\u012f");
    encodingTable.put("{\\c{I}}", "\u012e");
    encodingTable.put("{\\cI}", "\u012e");
    encodingTable.put("{\\u\\i}", "\u012d");
    encodingTable.put("{\\u{I}}", "\u012c");
    encodingTable.put("{\\uI}", "\u012c");
    encodingTable.put("{\\=\\i}", "\u012b");
    encodingTable.put("{\\={I}}", "\u012a");
    encodingTable.put("{\\=I}", "\u012a");
    encodingTable.put("{\\u{g}}", "\u011f");
    encodingTable.put("{\\ug}", "\u011f");
    encodingTable.put("{\\u{G}}", "\u011e");
    encodingTable.put("{\\uG}", "\u011e");
    encodingTable.put("{\\^{g}}", "\u011d");
    encodingTable.put("{\\^g}", "\u011d");
    encodingTable.put("{\\^{G}}", "\u011c");
    encodingTable.put("{\\^G}", "\u011c");
    encodingTable.put("{\\v{e}}", "\u011b");
    encodingTable.put("{\\ve}", "\u011b");
    encodingTable.put("{\\v{E}}", "\u011a");
    encodingTable.put("{\\vE}", "\u011a");
    encodingTable.put("{\\v{d}}", "\u010f");
    encodingTable.put("{\\vd}", "\u010f");
    encodingTable.put("{\\v{D}}", "\u010e");
    encodingTable.put("{\\vD}", "\u010e");
    encodingTable.put("{\\v{c}}", "\u010d");
    encodingTable.put("{\\vc}", "\u010d");
    encodingTable.put("{\\v{C}}", "\u010c");
    encodingTable.put("{\\vC}", "\u010c");
    encodingTable.put("{\\.{c}}", "\u010b");
    encodingTable.put("{\\.c}", "\u010b");
    encodingTable.put("{\\.{C}}", "\u010a");
    encodingTable.put("{\\.C}", "\u010a");
    encodingTable.put("{\\iota}", "\u03B9");
    encodingTable.put("{\\zeta}", "\u03B6");
    encodingTable.put("{\\beta}", "\u03B2");
    encodingTable.put("{\\'\\o}", "\u01ff");
    encodingTable.put("{\\'\\O}", "\u01fe");
    encodingTable.put("{\\'{g}}", "\u01f5");
    encodingTable.put("{\\'g}", "\u01f5");
    encodingTable.put("{\\'{G}}", "\u01f4");
    encodingTable.put("{\\'G}", "\u01f4");
    encodingTable.put("{\\v\\j}", "\u01f0");
    encodingTable.put("{\\c{o}}", "\u01eb");
    encodingTable.put("{\\co}", "\u01eb");
    encodingTable.put("{\\c{O}}", "\u01ea");
    encodingTable.put("{\\cO}", "\u01ea");
    encodingTable.put("{\\v{k}}", "\u01e9");
    encodingTable.put("{\\vk}", "\u01e9");
    encodingTable.put("{\\v{K}}", "\u01e8");
    encodingTable.put("{\\vK}", "\u01e8");
    encodingTable.put("{\\v{g}}", "\u01e7");
    encodingTable.put("{\\vg}", "\u01e7");
    encodingTable.put("{\\v{G}}", "\u01e6");
    encodingTable.put("{\\vG}", "\u01e6");
    encodingTable.put("{\\v{u}}", "\u01d4");
    encodingTable.put("{\\vu}", "\u01d4");
    encodingTable.put("{\\v{U}}", "\u01d3");
    encodingTable.put("{\\vU}", "\u01d3");
    encodingTable.put("{\\v{o}}", "\u01d2");
    encodingTable.put("{\\vo}", "\u01d2");
    encodingTable.put("{\\v{O}}", "\u01d1");
    encodingTable.put("{\\vO}", "\u01d1");
    encodingTable.put("{\\v\\i}", "\u01d0");
    encodingTable.put("{\\v{I}}", "\u01cf");
    encodingTable.put("{\\vI}", "\u01cf");
    encodingTable.put("{\\v{a}}", "\u01ce");
    encodingTable.put("{\\va}", "\u01ce");
    encodingTable.put("{\\v{A}}", "\u01cd");
    encodingTable.put("{\\vA}", "\u01cd");
    encodingTable.put("{\\t{h}}", "\u00fe");
    encodingTable.put("{\\th}", "\u00fe");
    encodingTable.put("{\\'{y}}", "\u00fd");
    encodingTable.put("{\\'y}", "\u00fd");
    encodingTable.put("{\\^{u}}", "\u00fb");
    encodingTable.put("{\\^u}", "\u00fb");
    encodingTable.put("{\\'{u}}", "\u00fa");
    encodingTable.put("{\\'u}", "\u00fa");
    encodingTable.put("{\\`{u}}", "\u00f9");
    encodingTable.put("{\\`u}", "\u00f9");
    encodingTable.put("{\\~{o}}", "\u00f5");
    encodingTable.put("{\\~o}", "\u00f5");
    encodingTable.put("{\\^{o}}", "\u00f4");
    encodingTable.put("{\\^o}", "\u00f4");
    encodingTable.put("{\\'{o}}", "\u00f3");
    encodingTable.put("{\\'o}", "\u00f3");
    encodingTable.put("{\\`{o}}", "\u00f2");
    encodingTable.put("{\\`o}", "\u00f2");
    encodingTable.put("{\\~{n}}", "\u00f1");
    encodingTable.put("{\\~n}", "\u00f1");
    encodingTable.put("{\\d{h}}", "\u00f0");
    encodingTable.put("{\\dh}", "\u00f0");
    encodingTable.put("{\\^\\i}", "\u00ee");
    encodingTable.put("{\\'\\i}", "\u00ed");
    encodingTable.put("{\\`\\i}", "\u00ec");
    encodingTable.put("{\\^{e}}", "\u00ea");
    encodingTable.put("{\\^e}", "\u00ea");
    encodingTable.put("{\\'{e}}", "\u00e9");
    encodingTable.put("{\\'e}", "\u00e9");
    encodingTable.put("{\\`{e}}", "\u00e8");
    encodingTable.put("{\\`e}", "\u00e8");
    encodingTable.put("{\\c{c}}", "\u00e7");
    encodingTable.put("{\\cc}", "\u00e7");
    encodingTable.put("{\\a{e}}", "\u00e6");
    encodingTable.put("{\\ae}", "\u00e6");
    encodingTable.put("{\\a{a}}", "\u00e5");
    encodingTable.put("{\\aa}", "\u00e5");
    encodingTable.put("{\\~{a}}", "\u00e3");
    encodingTable.put("{\\~a}", "\u00e3");
    encodingTable.put("{\\^{a}}", "\u00e2");
    encodingTable.put("{\\^a}", "\u00e2");
    encodingTable.put("{\\'{a}}", "\u00e1");
    encodingTable.put("{\\'a}", "\u00e1");
    encodingTable.put("{\\`{a}}", "\u00e0");
    encodingTable.put("{\\`a}", "\u00e0");
    encodingTable.put("{\\s{s}}", "\u00df");
    encodingTable.put("{\\ss}", "\u00df");
    encodingTable.put("{\\T{H}}", "\u00de");
    encodingTable.put("{\\TH}", "\u00de");
    encodingTable.put("{\\'{Y}}", "\u00dd");
    encodingTable.put("{\\'Y}", "\u00dd");
    encodingTable.put("{\\^{U}}", "\u00db");
    encodingTable.put("{\\^U}", "\u00db");
    encodingTable.put("{\\'{U}}", "\u00da");
    encodingTable.put("{\\'U}", "\u00da");
    encodingTable.put("{\\`{U}}", "\u00d9");
    encodingTable.put("{\\`U}", "\u00d9");
    encodingTable.put("{\\~{O}}", "\u00d5");
    encodingTable.put("{\\~O}", "\u00d5");
    encodingTable.put("{\\^{O}}", "\u00d4");
    encodingTable.put("{\\^O}", "\u00d4");
    encodingTable.put("{\\'{O}}", "\u00d3");
    encodingTable.put("{\\'O}", "\u00d3");
    encodingTable.put("{\\`{O}}", "\u00d2");
    encodingTable.put("{\\`O}", "\u00d2");
    encodingTable.put("{\\~{N}}", "\u00d1");
    encodingTable.put("{\\~N}", "\u00d1");
    encodingTable.put("{\\D{H}}", "\u00d0");
    encodingTable.put("{\\DH}", "\u00d0");
    encodingTable.put("{\\^{I}}", "\u00ce");
    encodingTable.put("{\\^I}", "\u00ce");
    encodingTable.put("{\\'{I}}", "\u00cd");
    encodingTable.put("{\\'I}", "\u00cd");
    encodingTable.put("{\\`{I}}", "\u00cc");
    encodingTable.put("{\\`I}", "\u00cc");
    encodingTable.put("{\\^{E}}", "\u00ca");
    encodingTable.put("{\\^E}", "\u00ca");
    encodingTable.put("{\\'{E}}", "\u00c9");
    encodingTable.put("{\\'E}", "\u00c9");
    encodingTable.put("{\\`{E}}", "\u00c8");
    encodingTable.put("{\\`E}", "\u00c8");
    encodingTable.put("{\\c{C}}", "\u00c7");
    encodingTable.put("{\\cC}", "\u00c7");
    encodingTable.put("{\\A{E}}", "\u00c6");
    encodingTable.put("{\\AE}", "\u00c6");
    encodingTable.put("{\\A{A}}", "\u00c5");
    encodingTable.put("{\\AA}", "\u00c5");
    encodingTable.put("{\\~{A}}", "\u00c3");
    encodingTable.put("{\\~A}", "\u00c3");
    encodingTable.put("{\\^{A}}", "\u00c2");
    encodingTable.put("{\\^A}", "\u00c2");
    encodingTable.put("{\\'{A}}", "\u00c1");
    encodingTable.put("{\\'A}", "\u00c1");
    encodingTable.put("{\\`{A}}", "\u00c0");
    encodingTable.put("{\\`A}", "\u00c0");
    encodingTable.put("{\\geq}", "\u2265");
    encodingTable.put("{\\leq}", "\u2264");
    encodingTable.put("{\\neq}", "\u2260");
    encodingTable.put("{\\cap}", "\u2229");
    encodingTable.put("{\\vee}", "\u2228");
    encodingTable.put("{\\mid}", "\u2223");
    encodingTable.put("{\\ast}", "\u2217");
    encodingTable.put("{\\sum}", "\u2211");
    encodingTable.put("{\\set}", "\u2205");
    encodingTable.put("{\\9{}}", "\u2079");
    encodingTable.put("{\\9}", "\u2079");
    encodingTable.put("{\\8{}}", "\u2078");
    encodingTable.put("{\\8}", "\u2078");
    encodingTable.put("{\\7{}}", "\u2077");
    encodingTable.put("{\\7}", "\u2077");
    encodingTable.put("{\\6{}}", "\u2076");
    encodingTable.put("{\\6}", "\u2076");
    encodingTable.put("{\\5{}}", "\u2075");
    encodingTable.put("{\\5}", "\u2075");
    encodingTable.put("{\\4{}}", "\u2074");
    encodingTable.put("{\\4}", "\u2074");
    encodingTable.put("{\\i{}}", "\u2071");
    encodingTable.put("{\\i}", "\u2071");
    encodingTable.put("{\\dag}", "\u2020");
    encodingTable.put("{\\sim}", "\u223C");
    encodingTable.put("{\\int}", "\u222B");
    encodingTable.put("{\\cup}", "\u222A");
    encodingTable.put("{\\){}}", "\u207E");
    encodingTable.put("{\\)}", "\u207E");
    encodingTable.put("{\\({}}", "\u207D");
    encodingTable.put("{\\(}", "\u207D");
    encodingTable.put("{\\-{}}", "\u207B");
    encodingTable.put("{\\-}", "\u207B");
    encodingTable.put("{\\+{}}", "\u207A");
    encodingTable.put("{\\+}", "\u207A");
    encodingTable.put("{\\Box}", "\u25A1");
    encodingTable.put("{\\ggg}", "\u22D9");
    encodingTable.put("{\\lll}", "\u22D8");
    encodingTable.put("{\\Cup}", "\u22D3");
    encodingTable.put("{\\Cap}", "\u22D2");
    encodingTable.put("{\\rhd}", "\u22B3");
    encodingTable.put("{\\lhd}", "\u22B2");
    encodingTable.put("{\\bot}", "\u22A5");
    encodingTable.put("{\\top}", "\u22A4");
    encodingTable.put("{\\Eta}", "\u0397");
    encodingTable.put("{\\B{}}", "\u0335");
    encodingTable.put("{\\B}", "\u0335");
    encodingTable.put("{\\b{}}", "\u0331");
    encodingTable.put("{\\b}", "\u0331");
    encodingTable.put("{\\k{}}", "\u0328");
    encodingTable.put("{\\k}", "\u0328");
    encodingTable.put("{\\c{}}", "\u0327");
    encodingTable.put("{\\c}", "\u0327");
    encodingTable.put("{\\d{}}", "\u0323");
    encodingTable.put("{\\d}", "\u0323");
    encodingTable.put("{\\M{}}", "\u0322");
    encodingTable.put("{\\M}", "\u0322");
    encodingTable.put("{\\t{}}", "\u0311");
    encodingTable.put("{\\t}", "\u0311");
    encodingTable.put("{\\r{}}", "\u030A");
    encodingTable.put("{\\r}", "\u030A");
    encodingTable.put("{\\.{}}", "\u0307");
    encodingTable.put("{\\.}", "\u0307");
    encodingTable.put("{\\u{}}", "\u0306");
    encodingTable.put("{\\u}", "\u0306");
    encodingTable.put("{\\={}}", "\u0304");
    encodingTable.put("{\\=}", "\u0304");
    encodingTable.put("{\\~{}}", "\u0303");
    encodingTable.put("{\\~}", "\u0303");
    encodingTable.put("{\\^{}}", "\u0302");
    encodingTable.put("{\\^}", "\u0302");
    encodingTable.put("{\\'{}}", "\u0301");
    encodingTable.put("{\\'}", "\u0301");
    encodingTable.put("{\\`{}}", "\u0300");
    encodingTable.put("{\\`}", "\u0300");
    encodingTable.put("{\\j{}}", "\u0237");
    encodingTable.put("{\\j}", "\u0237");
    encodingTable.put("{\\\"Y}", "\u0178");
    encodingTable.put("{\\l{}}", "\u0142");
    encodingTable.put("{\\l}", "\u0142");
    encodingTable.put("{\\L{}}", "\u0141");
    encodingTable.put("{\\L}", "\u0141");
    encodingTable.put("{\\L{}}", "\u0141");
    encodingTable.put("{\\L}", "\u0141");
    encodingTable.put("{\\i{}}", "\u0131");
    encodingTable.put("{\\i}", "\u0131");
    encodingTable.put("{\\G{}}", "\u030F");
    encodingTable.put("{\\G}", "\u030F");
    encodingTable.put("{\\v{}}", "\u030C");
    encodingTable.put("{\\v}", "\u030C");
    encodingTable.put("{\\H{}}", "\u030B");
    encodingTable.put("{\\H}", "\u030B");
    encodingTable.put("{\\psi}", "\u03C8");
    encodingTable.put("{\\chi}", "\u03C7");
    encodingTable.put("{\\tau}", "\u03C4");
    encodingTable.put("{\\eta}", "\u03B7");
    encodingTable.put("{\\Psi}", "\u03A8");
    encodingTable.put("{\\Chi}", "\u03A7");
    encodingTable.put("{\\Phi}", "\u03A6");
    encodingTable.put("{\\Tau}", "\u03A4");
    encodingTable.put("{\\Rho}", "\u03A1");
    encodingTable.put("{\\H{}}", "\u02dd");
    encodingTable.put("{\\H}", "\u02dd");
    encodingTable.put("{\\~{}}", "\u02dc");
    encodingTable.put("{\\~}", "\u02dc");
    encodingTable.put("{\\c{}}", "\u02db");
    encodingTable.put("{\\c}", "\u02db");
    encodingTable.put("{\\r{}}", "\u02da");
    encodingTable.put("{\\r}", "\u02da");
    encodingTable.put("{\\.{}}", "\u02d9");
    encodingTable.put("{\\.}", "\u02d9");
    encodingTable.put("{\\u{}}", "\u02d8");
    encodingTable.put("{\\u}", "\u02d8");
    encodingTable.put("{\\v{}}", "\u02c7");
    encodingTable.put("{\\v}", "\u02c7");
    encodingTable.put("{\\^{}}", "\u02c6");
    encodingTable.put("{\\^}", "\u02c6");
    encodingTable.put("{\\Ayn}", "\u02BF");
    encodingTable.put("{\\j{}}", "\u02B2");
    encodingTable.put("{\\j}", "\u02B2");
    encodingTable.put("{\\h{}}", "\u02B0");
    encodingTable.put("{\\h}", "\u02B0");
    encodingTable.put("{\\ESH}", "\u01a9");
    encodingTable.put("{\\\"y}", "\u00ff");
    encodingTable.put("{\\\"u}", "\u00fc");
    encodingTable.put("{\\o{}}", "\u00f8");
    encodingTable.put("{\\o}", "\u00f8");
    encodingTable.put("{\\\"o}", "\u00f6");
    encodingTable.put("{\\\"e}", "\u00eb");
    encodingTable.put("{\\\"a}", "\u00e4");
    encodingTable.put("{\\\"U}", "\u00dc");
    encodingTable.put("{\\O{}}", "\u00d8");
    encodingTable.put("{\\O}", "\u00d8");
    encodingTable.put("{\\\"O}", "\u00d6");
    encodingTable.put("{\\\"I}", "\u00cf");
    encodingTable.put("{\\\"E}", "\u00cb");
    encodingTable.put("{\\\"A}", "\u00c4");
    encodingTable.put("{\\P{}}", "\u00b6");
    encodingTable.put("{\\P}", "\u00b6");
    encodingTable.put("{\\neg}", "\u00ac");
    encodingTable.put("{\\S{}}", "\u00a7");
    encodingTable.put("{\\S}", "\u00a7");
    encodingTable.put("{\\div}", "\u00F7");
    encodingTable.put("{\\&{}}", "\u0026");
    encodingTable.put("{\\&}", "\u0026");
    encodingTable.put("{\\#{}}", "\u0023");
    encodingTable.put("{\\#}", "\u0023");
    encodingTable.put("$^+$", "\u207A");
    encodingTable.put("$^-$", "\u207B");
    encodingTable.put("{\\wr}", "\u2240");
    encodingTable.put("{\\mp}", "\u2213");
    encodingTable.put("{\\in}", "\u2208");
    encodingTable.put("{\\SM}", "\u2120");
    encodingTable.put("{\\gg}", "\u226B");
    encodingTable.put("{\\ll}", "\u226A");
    encodingTable.put("{\\ni}", "\u220C");
    encodingTable.put("{\\ni}", "\u220B");
    encodingTable.put("{\\hv}", "\u0195");
    encodingTable.put("{\\'Z}", "\u0179");
    encodingTable.put("{\\^y}", "\u0177");
    encodingTable.put("{\\^Y}", "\u0176");
    encodingTable.put("{\\^w}", "\u0175");
    encodingTable.put("{\\^W}", "\u0174");
    encodingTable.put("{\\cu}", "\u0173");
    encodingTable.put("{\\cU}", "\u0172");
    encodingTable.put("{\\Hu}", "\u0171");
    encodingTable.put("{\\HU}", "\u0170");
    encodingTable.put("{\\~u}", "\u0169");
    encodingTable.put("{\\~U}", "\u0168");
    encodingTable.put("{\\vt}", "\u0165");
    encodingTable.put("{\\vT}", "\u0164");
    encodingTable.put("{\\ct}", "\u0163");
    encodingTable.put("{\\cT}", "\u0162");
    encodingTable.put("{\\vs}", "\u0161");
    encodingTable.put("{\\vS}", "\u0160");
    encodingTable.put("{\\vr}", "\u0159");
    encodingTable.put("{\\vR}", "\u0158");
    encodingTable.put("{\\cr}", "\u0157");
    encodingTable.put("{\\cR}", "\u0156");
    encodingTable.put("{\\'r}", "\u0155");
    encodingTable.put("{\\'R}", "\u0154");
    encodingTable.put("{\\oe}", "\u0153");
    encodingTable.put("{\\OE}", "\u0152");
    encodingTable.put("{\\Ho}", "\u0151");
    encodingTable.put("{\\HO}", "\u0150");
    encodingTable.put("{\\vn}", "\u0148");
    encodingTable.put("{\\vN}", "\u0147");
    encodingTable.put("{\\cn}", "\u0146");
    encodingTable.put("{\\cN}", "\u0145");
    encodingTable.put("{\\'n}", "\u0144");
    encodingTable.put("{\\'N}", "\u0143");
    encodingTable.put("{\\'L}", "\u0139");
    encodingTable.put("{\\ck}", "\u0137");
    encodingTable.put("{\\cK}", "\u0136");
    encodingTable.put("{\\^J}", "\u0134");
    encodingTable.put("{\\ij}", "\u0133");
    encodingTable.put("{\\IJ}", "\u0132");
    encodingTable.put("{\\.I}", "\u0130");
    encodingTable.put("{\\~I}", "\u0128");
    encodingTable.put("{\\^h}", "\u0125");
    encodingTable.put("{\\^H}", "\u0124");
    encodingTable.put("{\\cg}", "\u0123");
    encodingTable.put("{\\cG}", "\u0122");
    encodingTable.put("{\\.g}", "\u0121");
    encodingTable.put("{\\.G}", "\u0120");
    encodingTable.put("{\\ce}", "\u0119");
    encodingTable.put("{\\cE}", "\u0118");
    encodingTable.put("{\\.e}", "\u0117");
    encodingTable.put("{\\.E}", "\u0116");
    encodingTable.put("{\\ue}", "\u0115");
    encodingTable.put("{\\uE}", "\u0114");
    encodingTable.put("{\\=e}", "\u0113");
    encodingTable.put("{\\=E}", "\u0112");
    encodingTable.put("{\\dj}", "\u0111");
    encodingTable.put("{\\DJ}", "\u0110");
    encodingTable.put("{\\^c}", "\u0109");
    encodingTable.put("{\\^C}", "\u0108");
    encodingTable.put("{\\'c}", "\u0107");
    encodingTable.put("{\\'C}", "\u0106");
    encodingTable.put("{\\ca}", "\u0105");
    encodingTable.put("{\\cA}", "\u0104");
    encodingTable.put("{\\ua}", "\u0103");
    encodingTable.put("{\\uA}", "\u0102");
    encodingTable.put("{\\=a}", "\u0101");
    encodingTable.put("{\\=A}", "\u0100");
    encodingTable.put("{\\Xi}", "\u039E");
    encodingTable.put("{\\Nu}", "\u039D");
    encodingTable.put("{\\Mu}", "\u039C");
    encodingTable.put("{\\vz}", "\u017e");
    encodingTable.put("{\\vZ}", "\u017d");
    encodingTable.put("{\\.Z}", "\u017c");
    encodingTable.put("{\\.Z}", "\u017b");
    encodingTable.put("{\\'Z}", "\u017a");
    encodingTable.put("{\\ru}", "\u016f");
    encodingTable.put("{\\rU}", "\u016e");
    encodingTable.put("{\\uu}", "\u016d");
    encodingTable.put("{\\uU}", "\u016c");
    encodingTable.put("{\\=u}", "\u016b");
    encodingTable.put("{\\=U}", "\u016a");
    encodingTable.put("{\\cs}", "\u015f");
    encodingTable.put("{\\cS}", "\u015e");
    encodingTable.put("{\\^s}", "\u015d");
    encodingTable.put("{\\^S}", "\u015c");
    encodingTable.put("{\\'s}", "\u015b");
    encodingTable.put("{\\'S}", "\u015a");
    encodingTable.put("{\\uo}", "\u014f");
    encodingTable.put("{\\uO}", "\u014e");
    encodingTable.put("{\\=o}", "\u014d");
    encodingTable.put("{\\=O}", "\u014c");
    encodingTable.put("{\\ng}", "\u014b");
    encodingTable.put("{\\NG}", "\u014a");
    encodingTable.put("{\\vl}", "\u013e");
    encodingTable.put("{\\vL}", "\u013d");
    encodingTable.put("{\\cl}", "\u013c");
    encodingTable.put("{\\cL}", "\u013b");
    encodingTable.put("{\\'l}", "\u013a");
    encodingTable.put("{\\ci}", "\u012f");
    encodingTable.put("{\\cI}", "\u012e");
    encodingTable.put("{\\uI}", "\u012c");
    encodingTable.put("{\\=I}", "\u012a");
    encodingTable.put("{\\ug}", "\u011f");
    encodingTable.put("{\\uG}", "\u011e");
    encodingTable.put("{\\^g}", "\u011d");
    encodingTable.put("{\\^G}", "\u011c");
    encodingTable.put("{\\ve}", "\u011b");
    encodingTable.put("{\\vE}", "\u011a");
    encodingTable.put("{\\vd}", "\u010f");
    encodingTable.put("{\\vD}", "\u010e");
    encodingTable.put("{\\vc}", "\u010d");
    encodingTable.put("{\\vC}", "\u010c");
    encodingTable.put("{\\.c}", "\u010b");
    encodingTable.put("{\\.C}", "\u010a");
    encodingTable.put("{\\xi}", "\u03BE");
    encodingTable.put("{\\nu}", "\u03BD");
    encodingTable.put("{\\mu}", "\u03BC");
    encodingTable.put("{\\pi}", "\u03C0");
    encodingTable.put("{\\Pi}", "\u03A0");
    encodingTable.put("{\\'g}", "\u01f5");
    encodingTable.put("{\\'G}", "\u01f4");
    encodingTable.put("{\\co}", "\u01eb");
    encodingTable.put("{\\cO}", "\u01ea");
    encodingTable.put("{\\vk}", "\u01e9");
    encodingTable.put("{\\vK}", "\u01e8");
    encodingTable.put("{\\vg}", "\u01e7");
    encodingTable.put("{\\vG}", "\u01e6");
    encodingTable.put("{\\vu}", "\u01d4");
    encodingTable.put("{\\vU}", "\u01d3");
    encodingTable.put("{\\vo}", "\u01d2");
    encodingTable.put("{\\vO}", "\u01d1");
    encodingTable.put("{\\vI}", "\u01cf");
    encodingTable.put("{\\va}", "\u01ce");
    encodingTable.put("{\\vA}", "\u01cd");
    encodingTable.put("{\\th}", "\u00fe");
    encodingTable.put("{\\'y}", "\u00fd");
    encodingTable.put("{\\^u}", "\u00fb");
    encodingTable.put("{\\'u}", "\u00fa");
    encodingTable.put("{\\`u}", "\u00f9");
    encodingTable.put("{\\~o}", "\u00f5");
    encodingTable.put("{\\^o}", "\u00f4");
    encodingTable.put("{\\'o}", "\u00f3");
    encodingTable.put("{\\`o}", "\u00f2");
    encodingTable.put("{\\~n}", "\u00f1");
    encodingTable.put("{\\dh}", "\u00f0");
    encodingTable.put("{\\^e}", "\u00ea");
    encodingTable.put("{\\'e}", "\u00e9");
    encodingTable.put("{\\`e}", "\u00e8");
    encodingTable.put("{\\cc}", "\u00e7");
    encodingTable.put("{\\ae}", "\u00e6");
    encodingTable.put("{\\aa}", "\u00e5");
    encodingTable.put("{\\~a}", "\u00e3");
    encodingTable.put("{\\^a}", "\u00e2");
    encodingTable.put("{\\'a}", "\u00e1");
    encodingTable.put("{\\`a}", "\u00e0");
    encodingTable.put("{\\ss}", "\u00df");
    encodingTable.put("{\\TH}", "\u00de");
    encodingTable.put("{\\'Y}", "\u00dd");
    encodingTable.put("{\\^U}", "\u00db");
    encodingTable.put("{\\'U}", "\u00da");
    encodingTable.put("{\\`U}", "\u00d9");
    encodingTable.put("{\\~O}", "\u00d5");
    encodingTable.put("{\\^O}", "\u00d4");
    encodingTable.put("{\\'O}", "\u00d3");
    encodingTable.put("{\\`O}", "\u00d2");
    encodingTable.put("{\\~N}", "\u00d1");
    encodingTable.put("{\\DH}", "\u00d0");
    encodingTable.put("{\\^I}", "\u00ce");
    encodingTable.put("{\\'I}", "\u00cd");
    encodingTable.put("{\\`I}", "\u00cc");
    encodingTable.put("{\\^E}", "\u00ca");
    encodingTable.put("{\\'E}", "\u00c9");
    encodingTable.put("{\\`E}", "\u00c8");
    encodingTable.put("{\\cC}", "\u00c7");
    encodingTable.put("{\\AE}", "\u00c6");
    encodingTable.put("{\\AA}", "\u00c5");
    encodingTable.put("{\\~A}", "\u00c3");
    encodingTable.put("{\\^A}", "\u00c2");
    encodingTable.put("{\\'A}", "\u00c1");
    encodingTable.put("{\\`A}", "\u00c0");
    encodingTable.put("{\\pm}", "\u00B1");
    encodingTable.put("{\\9}", "\u2079");
    encodingTable.put("{\\8}", "\u2078");
    encodingTable.put("{\\7}", "\u2077");
    encodingTable.put("{\\6}", "\u2076");
    encodingTable.put("{\\5}", "\u2075");
    encodingTable.put("{\\4}", "\u2074");
    encodingTable.put("{\\i}", "\u2071");
    encodingTable.put("{\\)}", "\u207E");
    encodingTable.put("{\\(}", "\u207D");
    encodingTable.put("{\\B}", "\u0335");
    encodingTable.put("{\\b}", "\u0331");
    encodingTable.put("{\\k}", "\u0328");
    encodingTable.put("{\\c}", "\u0327");
    encodingTable.put("{\\d}", "\u0323");
    encodingTable.put("{\\M}", "\u0322");
    encodingTable.put("{\\t}", "\u0311");
    encodingTable.put("{\\r}", "\u030A");
    encodingTable.put("{\\.}", "\u0307");
    encodingTable.put("{\\u}", "\u0306");
    encodingTable.put("{\\=}", "\u0304");
    encodingTable.put("{\\~}", "\u0303");
    encodingTable.put("{\\^}", "\u0302");
    encodingTable.put("{\\'}", "\u0301");
    encodingTable.put("{\\`}", "\u0300");
    encodingTable.put("{\\j}", "\u0237");
    encodingTable.put("{\\l}", "\u0142");
    encodingTable.put("{\\L}", "\u0141");
    encodingTable.put("{\\L}", "\u0141");
    encodingTable.put("{\\i}", "\u0131");
    encodingTable.put("{\\G}", "\u030F");
    encodingTable.put("{\\v}", "\u030C");
    encodingTable.put("{\\H}", "\u030B");
    encodingTable.put("{\\H}", "\u02dd");
    encodingTable.put("{\\~}", "\u02dc");
    encodingTable.put("{\\c}", "\u02db");
    encodingTable.put("{\\r}", "\u02da");
    encodingTable.put("{\\.}", "\u02d9");
    encodingTable.put("{\\u}", "\u02d8");
    encodingTable.put("{\\v}", "\u02c7");
    encodingTable.put("{\\^}", "\u02c6");
    encodingTable.put("{\\j}", "\u02B2");
    encodingTable.put("{\\h}", "\u02B0");
    encodingTable.put("{\\o}", "\u00f8");
    encodingTable.put("{\\O}", "\u00d8");
    encodingTable.put("{\\P}", "\u00b6");
    encodingTable.put("{\\S}", "\u00a7");
    encodingTable.put("{\\&}", "\u0026");
    encodingTable.put("{\\#}", "\u0023");
    encodingTable.put("{^0}", "\u2070");
    encodingTable.put("{^=}", "\u207C");
    encodingTable.put("{^y}", "\u02B8");
    encodingTable.put("{^w}", "\u02B7");
    encodingTable.put("{^r}", "\u02B3");

  }

  /**
   * Mapping for month formats.
   */
  private static final Map<String, String> monthTable = new HashMap<>();
  static {
    monthTable.put("1", "01");
    monthTable.put("2", "02");
    monthTable.put("3", "03");
    monthTable.put("4", "04");
    monthTable.put("5", "05");
    monthTable.put("6", "06");
    monthTable.put("7", "07");
    monthTable.put("8", "08");
    monthTable.put("9", "09");
    monthTable.put("10", "10");
    monthTable.put("11", "11");
    monthTable.put("12", "12");

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

  /**
   * Translates from BibTeX to normalized UTF-8.
   *
   * @param text A BibTeX encoded string.
   * @return A UTF-8 encoded string.
   */
  public static String bibtexDecode(String text) {
    return bibtexDecode(text, true);
  }

  /**
   * Translates from BibTeX to normalized UTF-8.
   *
   * @param text A BibTeX encoded string.
   * @param stripBraces Indicates whether empty braces "{}" should be removed as well.
   * @return A UTF-8 encoded string.
   */
  public static String bibtexDecode(String text, boolean stripBraces) {
    for (Map.Entry<String, String> entry : encodingTable.entrySet()) {
      text = text.replace(entry.getKey(), entry.getValue());
    }

    // normalize
    text = text.replaceAll("[ \\t][ \\t]+", " ").replaceAll("\\n ", "\n");
    text = stripBraces(text, stripBraces);
    return deEscapeCharacters(text);
  }

  /**
   * Replaces all BibTeX encoded special characters with UTF-8.
   *
   * @param text A BibTeX encoded string.
   * @return A UTF-8 encoded string.
   */
  public static String deEscapeCharacters(String text) {

    for (int i = 0; i < ESCAPE_CHARACTERS.length(); i++) {
      text = deEscapeCharacter(text, ESCAPE_CHARACTERS.substring(i, i + 1));
    }

    return text;
  }

  private static String deEscapeCharacter(String text, String character) {
    int position = text.indexOf("\\" + character);
    if (0 <= position) {
      return text.substring(0, position).replace(character, "") + character
          + deEscapeCharacter(text.substring(position + 2), character);
    } else {
      return text.replace(character, "");
    }
  }

  /**
   * Extracts all braces "{}" from BibTeX encoded strings.
   *
   * @param text A BibTeX encoded string.
   * @return A string without braces.
   */
  public static String stripBraces(String text, boolean removeEmptyBraces) {
    if (!removeEmptyBraces) {
      text = text.replace("{}", HOPEFULLY_UNUSED_TOKEN);
    }
    if (text.startsWith("\"") && text.endsWith("\"")) {
      text = text.substring(1, text.length() - 1);
    } else if (text.startsWith("'") && text.endsWith("'")) {
      text = text.substring(1, text.length() - 1);
    }

    // text = text.substring(text.indexOf("{")+1, text.lastIndexOf("}")-1);

    Pattern pattern = Pattern.compile("\\{(.*)\\}", Pattern.DOTALL);
    // while (true)
    // {
    Matcher matcher = pattern.matcher(text);
    if (matcher.find()) {
      text = matcher.replaceAll("$1");
    }
    // else
    // {
    // break;
    // }
    // }
    if (!removeEmptyBraces) {
      text = text.replace(HOPEFULLY_UNUSED_TOKEN, "{}");
    }
    return text.trim();
  }

  /**
   * Parses a given string into a valid month.
   *
   * @param monthString A string containing an encoded month.
   * @return A string containing a month in eSciDoc format.
   */
  public static String parseMonth(String monthString) {
    return monthTable.get(bibtexDecode(monthString.trim().toLowerCase()));
  }

  public static Map<BibTexUtil.Genre, MdsPublicationVO.Genre> getGenreMapping() {
    return genreMapping;
  }

  public static void setGenreMapping(Map<BibTexUtil.Genre, MdsPublicationVO.Genre> genreMapping) {
    BibTexUtil.genreMapping = genreMapping;
  }

  /**
   * Parses a string containing information about start and end page.
   *
   * @param pagesString A BibTeX "pages" string, e.g. "1--20", "3-5".
   * @param sourceVO The {@link SourceVO} where the pages information should be added.
   */
  public static void fillSourcePages(String pagesString, SourceVO sourceVO) {
    String[] pieces = pagesString.split(" *-+ *");
    if (2 == pieces.length) {
      sourceVO.setStartPage(pieces[0]);
      sourceVO.setEndPage(pieces[1]);
    } else {
      sourceVO.setStartPage(pagesString);
    }
  }
}
