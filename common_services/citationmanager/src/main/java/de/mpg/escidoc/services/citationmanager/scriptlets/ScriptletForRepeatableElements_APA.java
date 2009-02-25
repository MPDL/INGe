package de.mpg.escidoc.services.citationmanager.scriptlets;
import net.sf.jasperreports.engine.JRDefaultScriptlet;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.util.JRStringUtil;
import de.mpg.escidoc.services.citationmanager.utils.Utils;
import java.util.*;
import java.util.regex.*;

public class ScriptletForRepeatableElements_APA extends JRDefaultScriptlet {
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
   if (Utils.checkLen(str)) {
       str = str.replace("null", "");
		str = Pattern.compile("[\n\r\t]+", Pattern.DOTALL).matcher(str).replaceAll(" ");
       str = str.replaceAll("<style[^>]*?>\\s*<[/]style[^>]*?>","");
       str = str.replaceAll("\\[span.*?\\]\\s*\\[/span\\]","");
       str = str.replaceAll("(\\s*[.]+)+",".");
       str = str.replaceAll("(\\s*[,]+)+",",");
       str = str.replaceAll("(\\s*[:]+)+",":");
       str = str.replaceAll("(\\s*[?]+)+","?");
       str = str.replaceAll("(\\s*[!]+)+","!");
       str = str.replaceAll("(\\s*[;]+)+",";");
       str = str.replaceAll("([?!])\\s*[.]","$1");
       str = str.replaceAll("([({<\\[])\\s+(.*)","$1$2");
       str = str.replaceAll("(.*)\\s+([\\]>})])","$1$2");
       str = str.replaceAll("(([.,;?!])+\\s*<[/]?style[^>]*?>)\\s*\\2+","$1");
       str = str.replaceAll("\\s+(<[/]?style[^>]*?>)?\\s+","$1 ");
       str = str.replaceAll("\\s*(<[/]?style[^>]*?>)\\s*([.,;?!])","$1$2");
       str = str.replaceAll("(([.,;?!])+\\s*\\[[/]?span\\s*\\])\\s*\\2+","$1");
       str = str.replaceAll("\\s+(\\[[/]?span\\s*\\])\\s+","$1 ");
       str = str.replaceAll("\\s*(\\[[/]?span\\s*\\])\\s*([.,;?!])","$1$2");
   }
   return Utils.checkVal(str) ? str: null;
}
public String get_month(String str) {String[] sa = str.split("-");return sa.length >= 2 ? sa[1] : null; }public String join(String[] arr, String delimiter){if ( arr==null || arr.length == 0 ) return null;if ( arr.length == 1 ) return arr[0];StringBuffer sb = new StringBuffer();if (delimiter==null) delimiter="";for (int i=0, n=arr.length; i<n; i++ ){if (arr[i]==null || arr[i].trim().equals(""))continue;sb.append(arr[i]);if (i<n-1) sb.append(delimiter);}String str = sb.toString().replaceAll(Pattern.quote(delimiter)+"$", "");return str;}    public String get_year(String str) {return str != null ? str.split("-")[0] : null;}public String get_month_name(String str) {String m = get_month(str);if(m==null) return null;int mi = Integer.parseInt(m);return mi==1 ? "January":mi==2?"February":mi==3?"March":mi==4 ? "April": mi==5 ? "May": mi==6 ? "June": mi==7 ? "July":mi==8 ? "August": mi==9 ? "September": mi==10 ? "October":mi==11 ? "November":mi==12 ? "December":null; }    public String mostRecentDateStatus(String[] dates) {String max = mostRecentDate(dates);return max.equals(dates[0]) ? "escidoc.published-online":max.equals(dates[1]) ? "escidoc.issued":max.equals(dates[2]) ? "escidoc.dateAccepted" :max.equals(dates[3]) ? "escidoc.dateSubmitted" :max.equals(dates[4]) ? "escidoc.modified":max.equals(dates[5]) ? "escidoc.created":""; }public int toInt(String str) {return Integer.parseInt(str);}public String get_day(String str) {String[] sa = str.split("-");return sa.length >= 3 ? sa[2] : null;}public String get_initials(String str) {StringTokenizer st = new StringTokenizer(str);String res = "";while (st.hasMoreElements( ))res += st.nextElement().toString().charAt(0) + ". ";return res.trim( );}public String mostRecentDate(String[] dates) {List<String> list = Arrays.asList(dates);Collections.replaceAll(list, null, "");Collections.sort(list);return (String)list.get(list.size() - 1);}public String getCS_1_PLE_1() throws Exception {
String result = "";
String str = "";
String last = "";
String delim = "";
JRXmlDataSource ds = ((JRXmlDataSource) this.getParameterValue("REPORT_DATA_SOURCE"));
JRXmlDataSource subDs = ds.subDataSource("md-record/publication/creator[@role='author']");
JRDesignField field_default_0 = new JRDesignField();
field_default_0.setDescription("organization/organization-name");
field_default_0.setValueClass(String.class);
String chunk_default_0 = "";

JRDesignField field_default_1 = new JRDesignField();
field_default_1.setDescription("person/family-name");
field_default_1.setValueClass(String.class);
String chunk_default_1 = "";

JRDesignField field_default_2 = new JRDesignField();
field_default_2.setDescription("person/given-name");
field_default_2.setValueClass(String.class);
String chunk_default_2 = "";

int count = 1;
boolean hasLast = false;
while ( subDs.next() ) {

 count++;
switch (count) {
default:
chunk_default_0 = (String)subDs.getFieldValue(field_default_0);chunk_default_0 = xmlEncode(chunk_default_0);chunk_default_0 = chunk_default_0!=null && chunk_default_0.length()>0 ? chunk_default_0 : "";chunk_default_0 = chunk_default_0.length()>0 ? (chunk_default_0) : ""; chunk_default_0 = (chunk_default_0);chunk_default_0 = chunk_default_0;
chunk_default_1 = (String)subDs.getFieldValue(field_default_1);chunk_default_1 = xmlEncode(chunk_default_1);chunk_default_1 = chunk_default_1!=null && chunk_default_1.length()>0 ? chunk_default_1 : "";chunk_default_1 = chunk_default_1.length()>0 ? (chunk_default_1) : ""; chunk_default_1 = (chunk_default_1);chunk_default_1 = chunk_default_1;
chunk_default_2 = (String)subDs.getFieldValue(field_default_2);chunk_default_2 = xmlEncode(chunk_default_2);chunk_default_2 = chunk_default_2!=null && chunk_default_2.length()>0 ? chunk_default_2 : "";chunk_default_2 = get_initials(chunk_default_2);chunk_default_2 = chunk_default_2.length()>0 ? (chunk_default_2) : ""; chunk_default_2 = get_initials(chunk_default_2);chunk_default_2 = chunk_default_2;
str = chunk_default_0 + insertDelimiter(chunk_default_0, ", ", chunk_default_1) + chunk_default_1 + insertDelimiter(chunk_default_1, ", ", chunk_default_2) + chunk_default_2;elems.add( new String[]{ str, ", " } );
break;
}

}
int es = elems.size();
for(int i=0; i<es; i++) {
String[] elem = (String[])elems.get(i);
result += elem[0].length()>0 ? elem[0] + (es>1&&i<es-1?elem[1]:"") : "";
}
elems.clear();
return result;
}
public String getCS_1_PLE_2() throws Exception {
String result = "";
String str = "";
String last = "";
String delim = "";
JRXmlDataSource ds = ((JRXmlDataSource) this.getParameterValue("REPORT_DATA_SOURCE"));
JRXmlDataSource subDs = ds.subDataSource("md-record/publication/creator[@role='author']");
JRDesignField field_default_0 = new JRDesignField();
field_default_0.setDescription("organization/organization-name");
field_default_0.setValueClass(String.class);
String chunk_default_0 = "";

JRDesignField field_default_1 = new JRDesignField();
field_default_1.setDescription("person/family-name");
field_default_1.setValueClass(String.class);
String chunk_default_1 = "";

JRDesignField field_default_2 = new JRDesignField();
field_default_2.setDescription("person/given-name");
field_default_2.setValueClass(String.class);
String chunk_default_2 = "";

JRDesignField field_last_0 = new JRDesignField();
field_last_0.setDescription("organization/organization-name");
field_last_0.setValueClass(String.class);
String chunk_last_0 = "";

JRDesignField field_last_1 = new JRDesignField();
field_last_1.setDescription("person/family-name");
field_last_1.setValueClass(String.class);
String chunk_last_1 = "";

JRDesignField field_last_2 = new JRDesignField();
field_last_2.setDescription("person/given-name");
field_last_2.setValueClass(String.class);
String chunk_last_2 = "";

int count = 1;
boolean hasLast = false;
while ( subDs.next() ) {

 count++;
switch (count) {
default:
chunk_default_0 = (String)subDs.getFieldValue(field_default_0);chunk_default_0 = xmlEncode(chunk_default_0);chunk_default_0 = chunk_default_0!=null && chunk_default_0.length()>0 ? chunk_default_0 : "";chunk_default_0 = chunk_default_0.length()>0 ? (chunk_default_0) : ""; chunk_default_0 = (chunk_default_0);chunk_default_0 = chunk_default_0;
chunk_default_1 = (String)subDs.getFieldValue(field_default_1);chunk_default_1 = xmlEncode(chunk_default_1);chunk_default_1 = chunk_default_1!=null && chunk_default_1.length()>0 ? chunk_default_1 : "";chunk_default_1 = chunk_default_1.length()>0 ? (chunk_default_1) : ""; chunk_default_1 = (chunk_default_1);chunk_default_1 = chunk_default_1;
chunk_default_2 = (String)subDs.getFieldValue(field_default_2);chunk_default_2 = xmlEncode(chunk_default_2);chunk_default_2 = chunk_default_2!=null && chunk_default_2.length()>0 ? chunk_default_2 : "";chunk_default_2 = get_initials(chunk_default_2);chunk_default_2 = chunk_default_2.length()>0 ? (chunk_default_2) : ""; chunk_default_2 = get_initials(chunk_default_2);chunk_default_2 = chunk_default_2;
str = chunk_default_0 + insertDelimiter(chunk_default_0, ", ", chunk_default_1) + chunk_default_1 + insertDelimiter(chunk_default_1, ", ", chunk_default_2) + chunk_default_2;elems.add( new String[]{ str, ", " } );
break;
}
if (!hasLast) hasLast = true;
if (hasLast) {
chunk_last_0 = (String)subDs.getFieldValue(field_last_0);chunk_last_0 = xmlEncode(chunk_last_0);chunk_last_0 = chunk_last_0!=null && chunk_last_0.length()>0 ? chunk_last_0 : "";chunk_last_0 = chunk_last_0.length()>0 ? (chunk_last_0) : ""; chunk_last_0 = (chunk_last_0);chunk_last_0 = chunk_last_0;
chunk_last_1 = (String)subDs.getFieldValue(field_last_1);chunk_last_1 = xmlEncode(chunk_last_1);chunk_last_1 = chunk_last_1!=null && chunk_last_1.length()>0 ? chunk_last_1 : "";chunk_last_1 = chunk_last_1.length()>0 ? (chunk_last_1) : ""; chunk_last_1 = (chunk_last_1);chunk_last_1 = chunk_last_1;
chunk_last_2 = (String)subDs.getFieldValue(field_last_2);chunk_last_2 = xmlEncode(chunk_last_2);chunk_last_2 = chunk_last_2!=null && chunk_last_2.length()>0 ? chunk_last_2 : "";chunk_last_2 = get_initials(chunk_last_2);chunk_last_2 = chunk_last_2.length()>0 ? (chunk_last_2) : ""; chunk_last_2 = get_initials(chunk_last_2);chunk_last_2 = chunk_last_2;
last = chunk_last_0 + insertDelimiter(chunk_last_0, ", ", chunk_last_1) + chunk_last_1 + insertDelimiter(chunk_last_1, ", ", chunk_last_2) + chunk_last_2;delim = ", &amp; ";}

}
int es = elems.size();
if ( hasLast && count>2 && ( !subDs.next( ) ) ) {
int idx = es - ( 2 ) ;
String[] elem = (String[])elems.get( idx );
elems.set(idx + 1, new String[]{ last, "" });
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
public String getCS_1_PLE_3() throws Exception {
String result = "";
String str = "";
String last = "";
String delim = "";
JRXmlDataSource ds = ((JRXmlDataSource) this.getParameterValue("REPORT_DATA_SOURCE"));
JRXmlDataSource subDs = ds.subDataSource("md-record/publication/creator[@role='author']");
JRDesignField field_default_0 = new JRDesignField();
field_default_0.setDescription("organization/organization-name");
field_default_0.setValueClass(String.class);
String chunk_default_0 = "";

JRDesignField field_default_1 = new JRDesignField();
field_default_1.setDescription("person/family-name");
field_default_1.setValueClass(String.class);
String chunk_default_1 = "";

JRDesignField field_default_2 = new JRDesignField();
field_default_2.setDescription("person/given-name");
field_default_2.setValueClass(String.class);
String chunk_default_2 = "";

int count = 1;
boolean hasLast = false;
boolean maxCount = false;
away: while ( subDs.next() ) {
if ( count >6) {
 maxCount = true;
 break away;
}
 count++;
switch (count) {
default:
chunk_default_0 = (String)subDs.getFieldValue(field_default_0);chunk_default_0 = xmlEncode(chunk_default_0);chunk_default_0 = chunk_default_0!=null && chunk_default_0.length()>0 ? chunk_default_0 : "";chunk_default_0 = chunk_default_0.length()>0 ? (chunk_default_0) : ""; chunk_default_0 = (chunk_default_0);chunk_default_0 = chunk_default_0;
chunk_default_1 = (String)subDs.getFieldValue(field_default_1);chunk_default_1 = xmlEncode(chunk_default_1);chunk_default_1 = chunk_default_1!=null && chunk_default_1.length()>0 ? chunk_default_1 : "";chunk_default_1 = chunk_default_1.length()>0 ? (chunk_default_1) : ""; chunk_default_1 = (chunk_default_1);chunk_default_1 = chunk_default_1;
chunk_default_2 = (String)subDs.getFieldValue(field_default_2);chunk_default_2 = xmlEncode(chunk_default_2);chunk_default_2 = chunk_default_2!=null && chunk_default_2.length()>0 ? chunk_default_2 : "";chunk_default_2 = get_initials(chunk_default_2);chunk_default_2 = chunk_default_2.length()>0 ? (chunk_default_2) : ""; chunk_default_2 = get_initials(chunk_default_2);chunk_default_2 = chunk_default_2;
str = chunk_default_0 + insertDelimiter(chunk_default_0, ", ", chunk_default_1) + chunk_default_1 + insertDelimiter(chunk_default_1, ", ", chunk_default_2) + chunk_default_2;elems.add( new String[]{ str, ", " } );
break;
}

}
int es = elems.size();
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
JRXmlDataSource subDs = ds.subDataSource("md-record/publication/creator[@role='editor']");
JRDesignField field_default_0 = new JRDesignField();
field_default_0.setDescription("organization/organization-name");
field_default_0.setValueClass(String.class);
String chunk_default_0 = "";

JRDesignField field_default_1 = new JRDesignField();
field_default_1.setDescription("person/family-name");
field_default_1.setValueClass(String.class);
String chunk_default_1 = "";

JRDesignField field_default_2 = new JRDesignField();
field_default_2.setDescription("person/given-name");
field_default_2.setValueClass(String.class);
String chunk_default_2 = "";

int count = 1;
boolean hasLast = false;
while ( subDs.next() ) {

 count++;
switch (count) {
default:
chunk_default_0 = (String)subDs.getFieldValue(field_default_0);chunk_default_0 = xmlEncode(chunk_default_0);chunk_default_0 = chunk_default_0!=null && chunk_default_0.length()>0 ? chunk_default_0 : "";chunk_default_0 = chunk_default_0.length()>0 ? (chunk_default_0) : ""; chunk_default_0 = (chunk_default_0);chunk_default_0 = chunk_default_0;
chunk_default_1 = (String)subDs.getFieldValue(field_default_1);chunk_default_1 = xmlEncode(chunk_default_1);chunk_default_1 = chunk_default_1!=null && chunk_default_1.length()>0 ? chunk_default_1 : "";chunk_default_1 = chunk_default_1.length()>0 ? (chunk_default_1) : ""; chunk_default_1 = (chunk_default_1);chunk_default_1 = chunk_default_1;
chunk_default_2 = (String)subDs.getFieldValue(field_default_2);chunk_default_2 = xmlEncode(chunk_default_2);chunk_default_2 = chunk_default_2!=null && chunk_default_2.length()>0 ? chunk_default_2 : "";chunk_default_2 = get_initials(chunk_default_2);chunk_default_2 = chunk_default_2.length()>0 ? (chunk_default_2) : ""; chunk_default_2 = get_initials(chunk_default_2);chunk_default_2 = chunk_default_2;
str = chunk_default_0 + insertDelimiter(chunk_default_0, ", ", chunk_default_1) + chunk_default_1 + insertDelimiter(chunk_default_1, ", ", chunk_default_2) + chunk_default_2;elems.add( new String[]{ str, ", " } );
break;
}

}
int es = elems.size();
for(int i=0; i<es; i++) {
String[] elem = (String[])elems.get(i);
result += elem[0].length()>0 ? elem[0] + (es>1&&i<es-1?elem[1]:"") : "";
}
elems.clear();
return result;
}
public String getCS_1_PLE_6() throws Exception {
String result = "";
String str = "";
String last = "";
String delim = "";
JRXmlDataSource ds = ((JRXmlDataSource) this.getParameterValue("REPORT_DATA_SOURCE"));
JRXmlDataSource subDs = ds.subDataSource("md-record/publication/creator[@role='editor']");
JRDesignField field_default_0 = new JRDesignField();
field_default_0.setDescription("organization/organization-name");
field_default_0.setValueClass(String.class);
String chunk_default_0 = "";

JRDesignField field_default_1 = new JRDesignField();
field_default_1.setDescription("person/family-name");
field_default_1.setValueClass(String.class);
String chunk_default_1 = "";

JRDesignField field_default_2 = new JRDesignField();
field_default_2.setDescription("person/given-name");
field_default_2.setValueClass(String.class);
String chunk_default_2 = "";

JRDesignField field_last_0 = new JRDesignField();
field_last_0.setDescription("organization/organization-name");
field_last_0.setValueClass(String.class);
String chunk_last_0 = "";

JRDesignField field_last_1 = new JRDesignField();
field_last_1.setDescription("person/family-name");
field_last_1.setValueClass(String.class);
String chunk_last_1 = "";

JRDesignField field_last_2 = new JRDesignField();
field_last_2.setDescription("person/given-name");
field_last_2.setValueClass(String.class);
String chunk_last_2 = "";

int count = 1;
boolean hasLast = false;
while ( subDs.next() ) {

 count++;
switch (count) {
default:
chunk_default_0 = (String)subDs.getFieldValue(field_default_0);chunk_default_0 = xmlEncode(chunk_default_0);chunk_default_0 = chunk_default_0!=null && chunk_default_0.length()>0 ? chunk_default_0 : "";chunk_default_0 = chunk_default_0.length()>0 ? (chunk_default_0) : ""; chunk_default_0 = (chunk_default_0);chunk_default_0 = chunk_default_0;
chunk_default_1 = (String)subDs.getFieldValue(field_default_1);chunk_default_1 = xmlEncode(chunk_default_1);chunk_default_1 = chunk_default_1!=null && chunk_default_1.length()>0 ? chunk_default_1 : "";chunk_default_1 = chunk_default_1.length()>0 ? (chunk_default_1) : ""; chunk_default_1 = (chunk_default_1);chunk_default_1 = chunk_default_1;
chunk_default_2 = (String)subDs.getFieldValue(field_default_2);chunk_default_2 = xmlEncode(chunk_default_2);chunk_default_2 = chunk_default_2!=null && chunk_default_2.length()>0 ? chunk_default_2 : "";chunk_default_2 = get_initials(chunk_default_2);chunk_default_2 = chunk_default_2.length()>0 ? (chunk_default_2) : ""; chunk_default_2 = get_initials(chunk_default_2);chunk_default_2 = chunk_default_2;
str = chunk_default_0 + insertDelimiter(chunk_default_0, ", ", chunk_default_1) + chunk_default_1 + insertDelimiter(chunk_default_1, ", ", chunk_default_2) + chunk_default_2;elems.add( new String[]{ str, ", " } );
break;
}
if (!hasLast) hasLast = true;
if (hasLast) {
chunk_last_0 = (String)subDs.getFieldValue(field_last_0);chunk_last_0 = xmlEncode(chunk_last_0);chunk_last_0 = chunk_last_0!=null && chunk_last_0.length()>0 ? chunk_last_0 : "";chunk_last_0 = chunk_last_0.length()>0 ? (chunk_last_0) : ""; chunk_last_0 = (chunk_last_0);chunk_last_0 = chunk_last_0;
chunk_last_1 = (String)subDs.getFieldValue(field_last_1);chunk_last_1 = xmlEncode(chunk_last_1);chunk_last_1 = chunk_last_1!=null && chunk_last_1.length()>0 ? chunk_last_1 : "";chunk_last_1 = chunk_last_1.length()>0 ? (chunk_last_1) : ""; chunk_last_1 = (chunk_last_1);chunk_last_1 = chunk_last_1;
chunk_last_2 = (String)subDs.getFieldValue(field_last_2);chunk_last_2 = xmlEncode(chunk_last_2);chunk_last_2 = chunk_last_2!=null && chunk_last_2.length()>0 ? chunk_last_2 : "";chunk_last_2 = get_initials(chunk_last_2);chunk_last_2 = chunk_last_2.length()>0 ? (chunk_last_2) : ""; chunk_last_2 = get_initials(chunk_last_2);chunk_last_2 = chunk_last_2;
last = chunk_last_0 + insertDelimiter(chunk_last_0, ", ", chunk_last_1) + chunk_last_1 + insertDelimiter(chunk_last_1, ", ", chunk_last_2) + chunk_last_2;delim = ", &amp; ";}

}
int es = elems.size();
if ( hasLast && count>2 && ( !subDs.next( ) ) ) {
int idx = es - ( 2 ) ;
String[] elem = (String[])elems.get( idx );
elems.set(idx + 1, new String[]{ last, "" });
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
public String getCS_1_PLE_7() throws Exception {
String result = "";
String str = "";
String last = "";
String delim = "";
JRXmlDataSource ds = ((JRXmlDataSource) this.getParameterValue("REPORT_DATA_SOURCE"));
JRXmlDataSource subDs = ds.subDataSource("md-record/publication/creator[@role='editor']");
JRDesignField field_default_0 = new JRDesignField();
field_default_0.setDescription("organization/organization-name");
field_default_0.setValueClass(String.class);
String chunk_default_0 = "";

JRDesignField field_default_1 = new JRDesignField();
field_default_1.setDescription("person/family-name");
field_default_1.setValueClass(String.class);
String chunk_default_1 = "";

JRDesignField field_default_2 = new JRDesignField();
field_default_2.setDescription("person/given-name");
field_default_2.setValueClass(String.class);
String chunk_default_2 = "";

int count = 1;
boolean hasLast = false;
boolean maxCount = false;
away: while ( subDs.next() ) {
if ( count >6) {
 maxCount = true;
 break away;
}
 count++;
switch (count) {
default:
chunk_default_0 = (String)subDs.getFieldValue(field_default_0);chunk_default_0 = xmlEncode(chunk_default_0);chunk_default_0 = chunk_default_0!=null && chunk_default_0.length()>0 ? chunk_default_0 : "";chunk_default_0 = chunk_default_0.length()>0 ? (chunk_default_0) : ""; chunk_default_0 = (chunk_default_0);chunk_default_0 = chunk_default_0;
chunk_default_1 = (String)subDs.getFieldValue(field_default_1);chunk_default_1 = xmlEncode(chunk_default_1);chunk_default_1 = chunk_default_1!=null && chunk_default_1.length()>0 ? chunk_default_1 : "";chunk_default_1 = chunk_default_1.length()>0 ? (chunk_default_1) : ""; chunk_default_1 = (chunk_default_1);chunk_default_1 = chunk_default_1;
chunk_default_2 = (String)subDs.getFieldValue(field_default_2);chunk_default_2 = xmlEncode(chunk_default_2);chunk_default_2 = chunk_default_2!=null && chunk_default_2.length()>0 ? chunk_default_2 : "";chunk_default_2 = get_initials(chunk_default_2);chunk_default_2 = chunk_default_2.length()>0 ? (chunk_default_2) : ""; chunk_default_2 = get_initials(chunk_default_2);chunk_default_2 = chunk_default_2;
str = chunk_default_0 + insertDelimiter(chunk_default_0, ", ", chunk_default_1) + chunk_default_1 + insertDelimiter(chunk_default_1, ", ", chunk_default_2) + chunk_default_2;elems.add( new String[]{ str, ", " } );
break;
}

}
int es = elems.size();
for(int i=0; i<es; i++) {
String[] elem = (String[])elems.get(i);
result += elem[0].length()>0 ? elem[0] + (es>1&&i<es-1?elem[1]:"") : "";
}
elems.clear();
return result;
}
public String getCS_1_PLE_11() throws Exception {
String result = "";
String str = "";
String last = "";
String delim = "";
JRXmlDataSource ds = ((JRXmlDataSource) this.getParameterValue("REPORT_DATA_SOURCE"));
JRXmlDataSource subDs = ds.subDataSource("md-record/publication/source[1]/creator[@role='editor']");
JRDesignField field_default_0 = new JRDesignField();
field_default_0.setDescription("organization/organization-name");
field_default_0.setValueClass(String.class);
String chunk_default_0 = "";

JRDesignField field_default_1 = new JRDesignField();
field_default_1.setDescription("person/family-name");
field_default_1.setValueClass(String.class);
String chunk_default_1 = "";

JRDesignField field_default_2 = new JRDesignField();
field_default_2.setDescription("person/given-name");
field_default_2.setValueClass(String.class);
String chunk_default_2 = "";

JRDesignField field_last_0 = new JRDesignField();
field_last_0.setDescription("organization/organization-name");
field_last_0.setValueClass(String.class);
String chunk_last_0 = "";

JRDesignField field_last_1 = new JRDesignField();
field_last_1.setDescription("person/family-name");
field_last_1.setValueClass(String.class);
String chunk_last_1 = "";

JRDesignField field_last_2 = new JRDesignField();
field_last_2.setDescription("person/given-name");
field_last_2.setValueClass(String.class);
String chunk_last_2 = "";

int count = 1;
boolean hasLast = false;
while ( subDs.next() ) {

 count++;
switch (count) {
default:
chunk_default_0 = (String)subDs.getFieldValue(field_default_0);chunk_default_0 = xmlEncode(chunk_default_0);chunk_default_0 = chunk_default_0!=null && chunk_default_0.length()>0 ? chunk_default_0 : "";chunk_default_0 = chunk_default_0.length()>0 ? (chunk_default_0) : ""; chunk_default_0 = (chunk_default_0);chunk_default_0 = chunk_default_0;
chunk_default_1 = (String)subDs.getFieldValue(field_default_1);chunk_default_1 = xmlEncode(chunk_default_1);chunk_default_1 = chunk_default_1!=null && chunk_default_1.length()>0 ? chunk_default_1 : "";chunk_default_1 = chunk_default_1.length()>0 ? (chunk_default_1) : ""; chunk_default_1 = (chunk_default_1);chunk_default_1 = chunk_default_1;
chunk_default_2 = (String)subDs.getFieldValue(field_default_2);chunk_default_2 = xmlEncode(chunk_default_2);chunk_default_2 = chunk_default_2!=null && chunk_default_2.length()>0 ? chunk_default_2 : "";chunk_default_2 = get_initials(chunk_default_2);chunk_default_2 = chunk_default_2.length()>0 ? (chunk_default_2) : ""; chunk_default_2 = get_initials(chunk_default_2);chunk_default_2 = chunk_default_2;
str = chunk_default_0 + insertDelimiter(chunk_default_0, " ", chunk_default_1) + chunk_default_1 + insertDelimiter(chunk_default_1, " ", chunk_default_2) + chunk_default_2;elems.add( new String[]{ str, ", " } );
break;
}
if (!hasLast) hasLast = true;
if (hasLast) {
chunk_last_0 = (String)subDs.getFieldValue(field_last_0);chunk_last_0 = xmlEncode(chunk_last_0);chunk_last_0 = chunk_last_0!=null && chunk_last_0.length()>0 ? chunk_last_0 : "";chunk_last_0 = chunk_last_0.length()>0 ? (chunk_last_0) : ""; chunk_last_0 = (chunk_last_0);chunk_last_0 = chunk_last_0;
chunk_last_1 = (String)subDs.getFieldValue(field_last_1);chunk_last_1 = xmlEncode(chunk_last_1);chunk_last_1 = chunk_last_1!=null && chunk_last_1.length()>0 ? chunk_last_1 : "";chunk_last_1 = chunk_last_1.length()>0 ? (chunk_last_1) : ""; chunk_last_1 = (chunk_last_1);chunk_last_1 = chunk_last_1;
chunk_last_2 = (String)subDs.getFieldValue(field_last_2);chunk_last_2 = xmlEncode(chunk_last_2);chunk_last_2 = chunk_last_2!=null && chunk_last_2.length()>0 ? chunk_last_2 : "";chunk_last_2 = get_initials(chunk_last_2);chunk_last_2 = chunk_last_2.length()>0 ? (chunk_last_2) : ""; chunk_last_2 = get_initials(chunk_last_2);chunk_last_2 = chunk_last_2;
last = chunk_last_0 + insertDelimiter(chunk_last_0, " ", chunk_last_1) + chunk_last_1 + insertDelimiter(chunk_last_1, " ", chunk_last_2) + chunk_last_2;delim = " and ";}

}
int es = elems.size();
if ( hasLast && count>2 && ( !subDs.next( ) ) ) {
int idx = es - ( 2 ) ;
String[] elem = (String[])elems.get( idx );
elems.set(idx + 1, new String[]{ last, "" });
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
public String getCS_1_PLE_10() throws Exception {
String result = "";
String str = "";
String last = "";
String delim = "";
JRXmlDataSource ds = ((JRXmlDataSource) this.getParameterValue("REPORT_DATA_SOURCE"));
JRXmlDataSource subDs = ds.subDataSource("md-record/publication/source[1]/creator[@role='editor']");
JRDesignField field_default_0 = new JRDesignField();
field_default_0.setDescription("organization/organization-name");
field_default_0.setValueClass(String.class);
String chunk_default_0 = "";

JRDesignField field_default_1 = new JRDesignField();
field_default_1.setDescription("person/family-name");
field_default_1.setValueClass(String.class);
String chunk_default_1 = "";

JRDesignField field_default_2 = new JRDesignField();
field_default_2.setDescription("person/given-name");
field_default_2.setValueClass(String.class);
String chunk_default_2 = "";

JRDesignField field_last_0 = new JRDesignField();
field_last_0.setDescription("organization/organization-name");
field_last_0.setValueClass(String.class);
String chunk_last_0 = "";

JRDesignField field_last_1 = new JRDesignField();
field_last_1.setDescription("person/family-name");
field_last_1.setValueClass(String.class);
String chunk_last_1 = "";

JRDesignField field_last_2 = new JRDesignField();
field_last_2.setDescription("person/given-name");
field_last_2.setValueClass(String.class);
String chunk_last_2 = "";

int count = 1;
boolean hasLast = false;
while ( subDs.next() ) {

 count++;
switch (count) {
default:
chunk_default_0 = (String)subDs.getFieldValue(field_default_0);chunk_default_0 = xmlEncode(chunk_default_0);chunk_default_0 = chunk_default_0!=null && chunk_default_0.length()>0 ? chunk_default_0 : "";chunk_default_0 = chunk_default_0.length()>0 ? (chunk_default_0) : ""; chunk_default_0 = (chunk_default_0);chunk_default_0 = chunk_default_0;
chunk_default_1 = (String)subDs.getFieldValue(field_default_1);chunk_default_1 = xmlEncode(chunk_default_1);chunk_default_1 = chunk_default_1!=null && chunk_default_1.length()>0 ? chunk_default_1 : "";chunk_default_1 = chunk_default_1.length()>0 ? (chunk_default_1) : ""; chunk_default_1 = (chunk_default_1);chunk_default_1 = chunk_default_1;
chunk_default_2 = (String)subDs.getFieldValue(field_default_2);chunk_default_2 = xmlEncode(chunk_default_2);chunk_default_2 = chunk_default_2!=null && chunk_default_2.length()>0 ? chunk_default_2 : "";chunk_default_2 = get_initials(chunk_default_2);chunk_default_2 = chunk_default_2.length()>0 ? (chunk_default_2) : ""; chunk_default_2 = get_initials(chunk_default_2);chunk_default_2 = chunk_default_2;
str = chunk_default_0 + insertDelimiter(chunk_default_0, " ", chunk_default_1) + chunk_default_1 + insertDelimiter(chunk_default_1, " ", chunk_default_2) + chunk_default_2;elems.add( new String[]{ str, ", " } );
break;
}
if (!hasLast) hasLast = true;
if (hasLast) {
chunk_last_0 = (String)subDs.getFieldValue(field_last_0);chunk_last_0 = xmlEncode(chunk_last_0);chunk_last_0 = chunk_last_0!=null && chunk_last_0.length()>0 ? chunk_last_0 : "";chunk_last_0 = chunk_last_0.length()>0 ? (chunk_last_0) : ""; chunk_last_0 = (chunk_last_0);chunk_last_0 = chunk_last_0;
chunk_last_1 = (String)subDs.getFieldValue(field_last_1);chunk_last_1 = xmlEncode(chunk_last_1);chunk_last_1 = chunk_last_1!=null && chunk_last_1.length()>0 ? chunk_last_1 : "";chunk_last_1 = chunk_last_1.length()>0 ? (chunk_last_1) : ""; chunk_last_1 = (chunk_last_1);chunk_last_1 = chunk_last_1;
chunk_last_2 = (String)subDs.getFieldValue(field_last_2);chunk_last_2 = xmlEncode(chunk_last_2);chunk_last_2 = chunk_last_2!=null && chunk_last_2.length()>0 ? chunk_last_2 : "";chunk_last_2 = get_initials(chunk_last_2);chunk_last_2 = chunk_last_2.length()>0 ? (chunk_last_2) : ""; chunk_last_2 = get_initials(chunk_last_2);chunk_last_2 = chunk_last_2;
last = chunk_last_0 + insertDelimiter(chunk_last_0, " ", chunk_last_1) + chunk_last_1 + insertDelimiter(chunk_last_1, " ", chunk_last_2) + chunk_last_2;delim = ", and ";}

}
int es = elems.size();
if ( hasLast && count>2 && ( !subDs.next( ) ) ) {
int idx = es - ( 2 ) ;
String[] elem = (String[])elems.get( idx );
elems.set(idx + 1, new String[]{ last, "" });
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