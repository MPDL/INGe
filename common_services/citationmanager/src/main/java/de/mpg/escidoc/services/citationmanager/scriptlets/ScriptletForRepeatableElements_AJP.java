package de.mpg.escidoc.services.citationmanager.scriptlets;
import net.sf.jasperreports.engine.JRDefaultScriptlet;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.util.JRStringUtil;
import de.mpg.escidoc.services.citationmanager.Utils;
import java.util.*;
import java.util.regex.*;

public class ScriptletForRepeatableElements_AJP extends JRDefaultScriptlet {
private ArrayList<String[]> elems = new ArrayList<String[]>();
private long cTime = 0;
private String insertDelimiter(String left, String delim, String right) {
    String result;
    return (delim!=null && delim.length()>0 &&
       left!=null && left.length()>0 &&
        right!=null && right.length()>0 ) ? delim : "";
}
public String xmlEncode(String str) {
   if (str!=null && str.length()>0) {
       str = JRStringUtil.xmlEncode(str);
   }
   return str;
}
public String cleanCit(String str) {
   if (str!=null && str.length()>0) {
       str = str.replaceAll("\\p{Blank}+", " ");
       str = str.replaceAll("([.]+\\s*[.]+)+",".");
       str = str.replaceAll("([.]+\\s*\\<[/]?style[.]*?\\>)[.]+","$1");
       str = str.replaceAll("(([,.;:?!])[ \t\r]+)+", "$2 ");
       str = str.replace("null", "");
		str = Pattern.compile("\\n+\\s*\\n+", Pattern.DOTALL).matcher(str).replaceAll("");
       str = Pattern.compile("\\<style.*?\\>\\s*\\<[/]style\\>",Pattern.DOTALL).matcher(str).replaceAll("");
   }
   return Utils.checkVal(str) ? str: null;
}
public String mostRecentDateStatus(String[] dates) {String max = mostRecentDate(dates);return max.equals(dates[0]) ? "escidoc.published-online":max.equals(dates[1]) ? "escidoc.issued":max.equals(dates[2]) ? "escidoc.dateAccepted" :max.equals(dates[3]) ? "escidoc.dateSubmitted" :max.equals(dates[4]) ? "escidoc.modified":max.equals(dates[5]) ? "escidoc.created":""; }public String mostRecentDate(String[] dates) {List<String> list = Arrays.asList(dates);Collections.replaceAll(list, null, "");Collections.sort(list);return (String)list.get(list.size() - 1);}public String get_month(String str) {String[] sa = str.split("-");return sa.length >= 2 ? sa[1] : null; }public String get_year(String str) {return str != null ? str.split("-")[0] : null;}public String get_initials(String str) {StringTokenizer st = new StringTokenizer(str);String res = "";while (st.hasMoreElements( ))res += st.nextElement().toString().charAt(0) + ". ";return res.trim( );}public String get_day(String str) {String[] sa = str.split("-");return sa.length >= 3 ? sa[2] : null;}public String getCS_1_PLE_2() throws Exception {
String result = "";
String str = "";
String last = "";
String delim = "";
JRXmlDataSource ds = ((JRXmlDataSource) this.getParameterValue("REPORT_DATA_SOURCE"));
JRXmlDataSource subDs = ds.subDataSource("md-record/publication/creator[@role='author']/person|organization");
JRDesignField field_default_0 = new JRDesignField();
field_default_0.setDescription("given-name");
field_default_0.setValueClass(String.class);
String chunk_default_0 = "";

JRDesignField field_default_1 = new JRDesignField();
field_default_1.setDescription("family-name");
field_default_1.setValueClass(String.class);
String chunk_default_1 = "";

JRDesignField field_last_0 = new JRDesignField();
field_last_0.setDescription("given-name");
field_last_0.setValueClass(String.class);
String chunk_last_0 = "";

JRDesignField field_last_1 = new JRDesignField();
field_last_1.setDescription("family-name");
field_last_1.setValueClass(String.class);
String chunk_last_1 = "";

int count = 1;
boolean hasLast = false;
boolean maxCount = false;
away: while ( subDs.next() ) {
if ( count >3) {
 elems.add(new String[]{ "et al.", "" } );
  maxCount = true;
 break away;
}
 count++;
switch (count) {
default:
chunk_default_0 = (String)subDs.getFieldValue(field_default_0);chunk_default_0 = xmlEncode(chunk_default_0);chunk_default_0 = chunk_default_0!=null && chunk_default_0.length()>0 ? chunk_default_0 : "";chunk_default_0 = chunk_default_0.length()>0 ? (chunk_default_0) : ""; chunk_default_0 = (chunk_default_0);chunk_default_0 = chunk_default_0;
chunk_default_1 = (String)subDs.getFieldValue(field_default_1);chunk_default_1 = xmlEncode(chunk_default_1);chunk_default_1 = chunk_default_1!=null && chunk_default_1.length()>0 ? chunk_default_1 : "";chunk_default_1 = chunk_default_1.length()>0 ? (chunk_default_1) : ""; chunk_default_1 = (chunk_default_1);chunk_default_1 = chunk_default_1;
str = chunk_default_0 + insertDelimiter(chunk_default_0, " ", chunk_default_1) + chunk_default_1;elems.add( new String[]{ str, ", " } );
break;
}
if (!hasLast) hasLast = true;
if (hasLast) {
chunk_last_0 = (String)subDs.getFieldValue(field_last_0);chunk_last_0 = xmlEncode(chunk_last_0);chunk_last_0 = chunk_last_0!=null && chunk_last_0.length()>0 ? chunk_last_0 : "";chunk_last_0 = chunk_last_0.length()>0 ? (chunk_last_0) : ""; chunk_last_0 = (chunk_last_0);chunk_last_0 = chunk_last_0;
chunk_last_1 = (String)subDs.getFieldValue(field_last_1);chunk_last_1 = xmlEncode(chunk_last_1);chunk_last_1 = chunk_last_1!=null && chunk_last_1.length()>0 ? chunk_last_1 : "";chunk_last_1 = chunk_last_1.length()>0 ? (chunk_last_1) : ""; chunk_last_1 = (chunk_last_1);chunk_last_1 = chunk_last_1;
last = chunk_last_0 + insertDelimiter(chunk_last_0, " ", chunk_last_1) + chunk_last_1;delim = " and ";}

}
int es = elems.size();
if ( hasLast && count>2 && ( maxCount || !subDs.next( ) ) ) {
int idx = es - ( maxCount ? 3 : 2 ) ;
String[] elem = (String[])elems.get( idx );
elems.set(idx + 1, new String[]{ last, maxCount ? elem[1] : "" });
elem[1] = delim;
elems.set(idx, elem);

}
for(int i=0; i<es; i++) {
String[] elem = (String[])elems.get(i);
result += elem[0].length()>0 ? elem[0] + (es>1&&i<es-1?elem[1]:"") : "";
}
elems.clear();
return result;
}
public String getCS_1_PLE_1() throws Exception {
String result = "";
String str = "";
String last = "";
String delim = "";
JRXmlDataSource ds = ((JRXmlDataSource) this.getParameterValue("REPORT_DATA_SOURCE"));
JRXmlDataSource subDs = ds.subDataSource("md-record/publication/creator[@role='author']/person|organization");
JRDesignField field_default_0 = new JRDesignField();
field_default_0.setDescription("given-name");
field_default_0.setValueClass(String.class);
String chunk_default_0 = "";

JRDesignField field_default_1 = new JRDesignField();
field_default_1.setDescription("family-name");
field_default_1.setValueClass(String.class);
String chunk_default_1 = "";

JRDesignField field_last_0 = new JRDesignField();
field_last_0.setDescription("given-name");
field_last_0.setValueClass(String.class);
String chunk_last_0 = "";

JRDesignField field_last_1 = new JRDesignField();
field_last_1.setDescription("family-name");
field_last_1.setValueClass(String.class);
String chunk_last_1 = "";

int count = 1;
boolean hasLast = false;
boolean maxCount = false;
away: while ( subDs.next() ) {
if ( count >3) {
 elems.add(new String[]{ "et al.", "" } );
  maxCount = true;
 break away;
}
 count++;
switch (count) {
default:
chunk_default_0 = (String)subDs.getFieldValue(field_default_0);chunk_default_0 = xmlEncode(chunk_default_0);chunk_default_0 = chunk_default_0!=null && chunk_default_0.length()>0 ? chunk_default_0 : "";chunk_default_0 = chunk_default_0.length()>0 ? (chunk_default_0) : ""; chunk_default_0 = (chunk_default_0);chunk_default_0 = chunk_default_0;
chunk_default_1 = (String)subDs.getFieldValue(field_default_1);chunk_default_1 = xmlEncode(chunk_default_1);chunk_default_1 = chunk_default_1!=null && chunk_default_1.length()>0 ? chunk_default_1 : "";chunk_default_1 = chunk_default_1.length()>0 ? (chunk_default_1) : ""; chunk_default_1 = (chunk_default_1);chunk_default_1 = chunk_default_1;
str = chunk_default_0 + insertDelimiter(chunk_default_0, " ", chunk_default_1) + chunk_default_1;elems.add( new String[]{ str, ", " } );
break;
}
if (!hasLast) hasLast = true;
if (hasLast) {
chunk_last_0 = (String)subDs.getFieldValue(field_last_0);chunk_last_0 = xmlEncode(chunk_last_0);chunk_last_0 = chunk_last_0!=null && chunk_last_0.length()>0 ? chunk_last_0 : "";chunk_last_0 = chunk_last_0.length()>0 ? (chunk_last_0) : ""; chunk_last_0 = (chunk_last_0);chunk_last_0 = chunk_last_0;
chunk_last_1 = (String)subDs.getFieldValue(field_last_1);chunk_last_1 = xmlEncode(chunk_last_1);chunk_last_1 = chunk_last_1!=null && chunk_last_1.length()>0 ? chunk_last_1 : "";chunk_last_1 = chunk_last_1.length()>0 ? (chunk_last_1) : ""; chunk_last_1 = (chunk_last_1);chunk_last_1 = chunk_last_1;
last = chunk_last_0 + insertDelimiter(chunk_last_0, " ", chunk_last_1) + chunk_last_1;delim = ", and ";}

}
int es = elems.size();
if ( hasLast && count>2 && ( maxCount || !subDs.next( ) ) ) {
int idx = es - ( maxCount ? 3 : 2 ) ;
String[] elem = (String[])elems.get( idx );
elems.set(idx + 1, new String[]{ last, maxCount ? elem[1] : "" });
elem[1] = delim;
elems.set(idx, elem);

}
for(int i=0; i<es; i++) {
String[] elem = (String[])elems.get(i);
result += elem[0].length()>0 ? elem[0] + (es>1&&i<es-1?elem[1]:"") : "";
}
elems.clear();
return result;
}
public String getCS_1_PLE_5() throws Exception {
String result = "";
String str = "";
String last = "";
String delim = "";
JRXmlDataSource ds = ((JRXmlDataSource) this.getParameterValue("REPORT_DATA_SOURCE"));
JRXmlDataSource subDs = ds.subDataSource("md-record/publication/source/creator[@role='editor']/person|organization");
JRDesignField field_default_0 = new JRDesignField();
field_default_0.setDescription("given-name");
field_default_0.setValueClass(String.class);
String chunk_default_0 = "";

JRDesignField field_default_1 = new JRDesignField();
field_default_1.setDescription("family-name");
field_default_1.setValueClass(String.class);
String chunk_default_1 = "";

JRDesignField field_last_0 = new JRDesignField();
field_last_0.setDescription("given-name");
field_last_0.setValueClass(String.class);
String chunk_last_0 = "";

JRDesignField field_last_1 = new JRDesignField();
field_last_1.setDescription("family-name");
field_last_1.setValueClass(String.class);
String chunk_last_1 = "";

int count = 1;
boolean hasLast = false;
boolean maxCount = false;
away: while ( subDs.next() ) {
if ( count >3) {
 elems.add(new String[]{ "et al.", "" } );
  maxCount = true;
 break away;
}
 count++;
switch (count) {
default:
chunk_default_0 = (String)subDs.getFieldValue(field_default_0);chunk_default_0 = xmlEncode(chunk_default_0);chunk_default_0 = chunk_default_0!=null && chunk_default_0.length()>0 ? chunk_default_0 : "";chunk_default_0 = chunk_default_0.length()>0 ? (chunk_default_0) : ""; chunk_default_0 = (chunk_default_0);chunk_default_0 = chunk_default_0;
chunk_default_1 = (String)subDs.getFieldValue(field_default_1);chunk_default_1 = xmlEncode(chunk_default_1);chunk_default_1 = chunk_default_1!=null && chunk_default_1.length()>0 ? chunk_default_1 : "";chunk_default_1 = chunk_default_1.length()>0 ? (chunk_default_1) : ""; chunk_default_1 = (chunk_default_1);chunk_default_1 = chunk_default_1;
str = chunk_default_0 + insertDelimiter(chunk_default_0, " ", chunk_default_1) + chunk_default_1;elems.add( new String[]{ str, ", " } );
break;
}
if (!hasLast) hasLast = true;
if (hasLast) {
chunk_last_0 = (String)subDs.getFieldValue(field_last_0);chunk_last_0 = xmlEncode(chunk_last_0);chunk_last_0 = chunk_last_0!=null && chunk_last_0.length()>0 ? chunk_last_0 : "";chunk_last_0 = chunk_last_0.length()>0 ? (chunk_last_0) : ""; chunk_last_0 = (chunk_last_0);chunk_last_0 = chunk_last_0;
chunk_last_1 = (String)subDs.getFieldValue(field_last_1);chunk_last_1 = xmlEncode(chunk_last_1);chunk_last_1 = chunk_last_1!=null && chunk_last_1.length()>0 ? chunk_last_1 : "";chunk_last_1 = chunk_last_1.length()>0 ? (chunk_last_1) : ""; chunk_last_1 = (chunk_last_1);chunk_last_1 = chunk_last_1;
last = chunk_last_0 + insertDelimiter(chunk_last_0, " ", chunk_last_1) + chunk_last_1;delim = " and ";}

}
int es = elems.size();
if ( hasLast && count>2 && ( maxCount || !subDs.next( ) ) ) {
int idx = es - ( maxCount ? 3 : 2 ) ;
String[] elem = (String[])elems.get( idx );
elems.set(idx + 1, new String[]{ last, maxCount ? elem[1] : "" });
elem[1] = delim;
elems.set(idx, elem);

}
for(int i=0; i<es; i++) {
String[] elem = (String[])elems.get(i);
result += elem[0].length()>0 ? elem[0] + (es>1&&i<es-1?elem[1]:"") : "";
}
elems.clear();
return result;
}
public String getCS_1_PLE_4() throws Exception {
String result = "";
String str = "";
String last = "";
String delim = "";
JRXmlDataSource ds = ((JRXmlDataSource) this.getParameterValue("REPORT_DATA_SOURCE"));
JRXmlDataSource subDs = ds.subDataSource("md-record/publication/source/creator[@role='editor']/person|organization");
JRDesignField field_default_0 = new JRDesignField();
field_default_0.setDescription("given-name");
field_default_0.setValueClass(String.class);
String chunk_default_0 = "";

JRDesignField field_default_1 = new JRDesignField();
field_default_1.setDescription("family-name");
field_default_1.setValueClass(String.class);
String chunk_default_1 = "";

JRDesignField field_last_0 = new JRDesignField();
field_last_0.setDescription("given-name");
field_last_0.setValueClass(String.class);
String chunk_last_0 = "";

JRDesignField field_last_1 = new JRDesignField();
field_last_1.setDescription("family-name");
field_last_1.setValueClass(String.class);
String chunk_last_1 = "";

int count = 1;
boolean hasLast = false;
boolean maxCount = false;
away: while ( subDs.next() ) {
if ( count >3) {
 elems.add(new String[]{ "et al.", "" } );
  maxCount = true;
 break away;
}
 count++;
switch (count) {
default:
chunk_default_0 = (String)subDs.getFieldValue(field_default_0);chunk_default_0 = xmlEncode(chunk_default_0);chunk_default_0 = chunk_default_0!=null && chunk_default_0.length()>0 ? chunk_default_0 : "";chunk_default_0 = chunk_default_0.length()>0 ? (chunk_default_0) : ""; chunk_default_0 = (chunk_default_0);chunk_default_0 = chunk_default_0;
chunk_default_1 = (String)subDs.getFieldValue(field_default_1);chunk_default_1 = xmlEncode(chunk_default_1);chunk_default_1 = chunk_default_1!=null && chunk_default_1.length()>0 ? chunk_default_1 : "";chunk_default_1 = chunk_default_1.length()>0 ? (chunk_default_1) : ""; chunk_default_1 = (chunk_default_1);chunk_default_1 = chunk_default_1;
str = chunk_default_0 + insertDelimiter(chunk_default_0, " ", chunk_default_1) + chunk_default_1;elems.add( new String[]{ str, ", " } );
break;
}
if (!hasLast) hasLast = true;
if (hasLast) {
chunk_last_0 = (String)subDs.getFieldValue(field_last_0);chunk_last_0 = xmlEncode(chunk_last_0);chunk_last_0 = chunk_last_0!=null && chunk_last_0.length()>0 ? chunk_last_0 : "";chunk_last_0 = chunk_last_0.length()>0 ? (chunk_last_0) : ""; chunk_last_0 = (chunk_last_0);chunk_last_0 = chunk_last_0;
chunk_last_1 = (String)subDs.getFieldValue(field_last_1);chunk_last_1 = xmlEncode(chunk_last_1);chunk_last_1 = chunk_last_1!=null && chunk_last_1.length()>0 ? chunk_last_1 : "";chunk_last_1 = chunk_last_1.length()>0 ? (chunk_last_1) : ""; chunk_last_1 = (chunk_last_1);chunk_last_1 = chunk_last_1;
last = chunk_last_0 + insertDelimiter(chunk_last_0, " ", chunk_last_1) + chunk_last_1;delim = ", and ";}

}
int es = elems.size();
if ( hasLast && count>2 && ( maxCount || !subDs.next( ) ) ) {
int idx = es - ( maxCount ? 3 : 2 ) ;
String[] elem = (String[])elems.get( idx );
elems.set(idx + 1, new String[]{ last, maxCount ? elem[1] : "" });
elem[1] = delim;
elems.set(idx, elem);

}
for(int i=0; i<es; i++) {
String[] elem = (String[])elems.get(i);
result += elem[0].length()>0 ? elem[0] + (es>1&&i<es-1?elem[1]:"") : "";
}
elems.clear();
return result;
}
}