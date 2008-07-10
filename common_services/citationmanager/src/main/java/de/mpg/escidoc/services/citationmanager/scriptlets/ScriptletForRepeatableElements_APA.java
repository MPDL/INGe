package de.mpg.escidoc.services.citationmanager.scriptlets;
import net.sf.jasperreports.engine.JRDefaultScriptlet;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.util.JRStringUtil;
import java.util.StringTokenizer;
import java.util.ArrayList;

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
   if (str!=null && str.length()>0) {
       str = str.replaceAll("\\p{Blank}+", " ");
       str = str.replaceAll("[.]+\\s*[.]+",".");
       str = str.replaceAll("([.]+\\<[/]?style[.]*?\\>)[.]+","$1");
       str = str.replaceAll("(([,.;:?!])[ \t\r]+)+", "$2 ");
       str = str.replace("null", "");
   }
   return str;
}
public String get_month(String str) {String[] sa = str.split("-");return sa.length >= 2 ? sa[1] : null; }public String get_year(String str) {return str != null ? str.split("-")[0] : null;}public String get_initials(String str) {StringTokenizer st = new StringTokenizer(str);String res = "";while (st.hasMoreElements( ))res += st.nextElement().toString().charAt(0) + ". ";return res.trim( );}public String get_day(String str) {String[] sa = str.split("-");return sa.length >= 3 ? sa[2] : null;}public String getCS_1_CSLD_2_P_default_E_1() throws Exception {
String result = "";
String str = "";
String last = "";
String delim = "";
JRXmlDataSource ds = ((JRXmlDataSource) this.getParameterValue("REPORT_DATA_SOURCE"));
JRXmlDataSource subDs = ds.subDataSource("md-record/publication/creator[@role='author']/person");
JRDesignField field_default_0 = new JRDesignField();
field_default_0.setDescription("family-name");
field_default_0.setValueClass(String.class);
String chunk_default_0 = "";

JRDesignField field_default_1 = new JRDesignField();
field_default_1.setDescription("given-name");
field_default_1.setValueClass(String.class);
String chunk_default_1 = "";

JRDesignField field_last_0 = new JRDesignField();
field_last_0.setDescription("family-name");
field_last_0.setValueClass(String.class);
String chunk_last_0 = "";

JRDesignField field_last_1 = new JRDesignField();
field_last_1.setDescription("given-name");
field_last_1.setValueClass(String.class);
String chunk_last_1 = "";

int count = 1;
boolean hasLast = false;
boolean maxCount = false;
away: while ( subDs.next() ) {
if ( count++ >6) {
 elems.add(new String[]{ "et al.", "" } );
  maxCount = true;
 break away;
}
switch (count) {
default:
chunk_default_0 = (String)subDs.getFieldValue(field_default_0);chunk_default_0 = xmlEncode(chunk_default_0);chunk_default_0 = chunk_default_0!=null && chunk_default_0.length()>0 ? chunk_default_0 : "";chunk_default_0 = chunk_default_0.length()>0 ? (chunk_default_0) : ""; chunk_default_0 = (chunk_default_0);chunk_default_0 = chunk_default_0;
chunk_default_1 = (String)subDs.getFieldValue(field_default_1);chunk_default_1 = xmlEncode(chunk_default_1);chunk_default_1 = chunk_default_1!=null && chunk_default_1.length()>0 ? chunk_default_1 : "";chunk_default_1 = get_initials(chunk_default_1);chunk_default_1 = chunk_default_1.length()>0 ? (chunk_default_1) : ""; chunk_default_1 = get_initials(chunk_default_1);chunk_default_1 = chunk_default_1;
str = chunk_default_0 + insertDelimiter(chunk_default_0, ", ", chunk_default_1) + chunk_default_1;elems.add( new String[]{ str, ", " } );
break;
}
if (!hasLast) hasLast = true;
if (hasLast) {
chunk_last_0 = (String)subDs.getFieldValue(field_last_0);chunk_last_0 = xmlEncode(chunk_last_0);chunk_last_0 = chunk_last_0!=null && chunk_last_0.length()>0 ? chunk_last_0 : "";chunk_last_0 = chunk_last_0.length()>0 ? (chunk_last_0) : ""; chunk_last_0 = (chunk_last_0);chunk_last_0 = chunk_last_0;
chunk_last_1 = (String)subDs.getFieldValue(field_last_1);chunk_last_1 = xmlEncode(chunk_last_1);chunk_last_1 = chunk_last_1!=null && chunk_last_1.length()>0 ? chunk_last_1 : "";chunk_last_1 = get_initials(chunk_last_1);chunk_last_1 = chunk_last_1.length()>0 ? (chunk_last_1) : ""; chunk_last_1 = get_initials(chunk_last_1);chunk_last_1 = chunk_last_1;
last = chunk_last_0 + insertDelimiter(chunk_last_0, ", ", chunk_last_1) + chunk_last_1;delim = ", &amp; ";}

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
public String getCS_1_CSLD_2_P_default_E_2() throws Exception {
String result = "";
String str = "";
String last = "";
String delim = "";
JRXmlDataSource ds = ((JRXmlDataSource) this.getParameterValue("REPORT_DATA_SOURCE"));
JRXmlDataSource subDs = ds.subDataSource("md-record/publication/creator[@role='author']/organization");
JRDesignField field_default_0 = new JRDesignField();
field_default_0.setDescription("organization-name");
field_default_0.setValueClass(String.class);
String chunk_default_0 = "";

int count = 1;
boolean hasLast = false;
boolean maxCount = false;
away: while ( subDs.next() ) {
if ( count++ >6) {
 elems.add(new String[]{ "et al.", "" } );
  maxCount = true;
 break away;
}
switch (count) {
default:
chunk_default_0 = (String)subDs.getFieldValue(field_default_0);chunk_default_0 = xmlEncode(chunk_default_0);chunk_default_0 = chunk_default_0!=null && chunk_default_0.length()>0 ? chunk_default_0 : "";chunk_default_0 = chunk_default_0.length()>0 ? (chunk_default_0) : ""; chunk_default_0 = (chunk_default_0);chunk_default_0 = chunk_default_0;
str = chunk_default_0;elems.add( new String[]{ str, ", " } );
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
public String getCS_1_CSLD_2_P_default_E_5() throws Exception {
String result = "";
String str = "";
String last = "";
String delim = "";
JRXmlDataSource ds = ((JRXmlDataSource) this.getParameterValue("REPORT_DATA_SOURCE"));
JRXmlDataSource subDs = ds.subDataSource("md-record/publication/source/creator[@role='editor']/person");
JRDesignField field_default_0 = new JRDesignField();
field_default_0.setDescription("given-name");
field_default_0.setValueClass(String.class);
String chunk_default_0 = "";

JRDesignField field_default_1 = new JRDesignField();
field_default_1.setDescription("family-name");
field_default_1.setValueClass(String.class);
String chunk_default_1 = "";

int count = -1;
while ( subDs.next() ) {
switch (count) {
default:
chunk_default_0 = (String)subDs.getFieldValue(field_default_0);chunk_default_0 = xmlEncode(chunk_default_0);chunk_default_0 = chunk_default_0!=null && chunk_default_0.length()>0 ? chunk_default_0 : "";chunk_default_0 = get_initials(chunk_default_0);chunk_default_0 = chunk_default_0.length()>0 ? (chunk_default_0) : ""; chunk_default_0 = get_initials(chunk_default_0);chunk_default_0 = chunk_default_0;
chunk_default_1 = (String)subDs.getFieldValue(field_default_1);chunk_default_1 = xmlEncode(chunk_default_1);chunk_default_1 = chunk_default_1!=null && chunk_default_1.length()>0 ? chunk_default_1 : "";chunk_default_1 = chunk_default_1.length()>0 ? (chunk_default_1) : ""; chunk_default_1 = (chunk_default_1);chunk_default_1 = chunk_default_1;
str = chunk_default_0 + insertDelimiter(chunk_default_0, " ", chunk_default_1) + chunk_default_1;elems.add( new String[]{ str, ", " } );
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
public String getCS_1_CSLD_3_P_default_E_1() throws Exception {
String result = "";
String str = "";
String last = "";
String delim = "";
JRXmlDataSource ds = ((JRXmlDataSource) this.getParameterValue("REPORT_DATA_SOURCE"));
JRXmlDataSource subDs = ds.subDataSource("md-record/publication/creator[@role='author']/person");
JRDesignField field_default_0 = new JRDesignField();
field_default_0.setDescription("family-name");
field_default_0.setValueClass(String.class);
String chunk_default_0 = "";

JRDesignField field_default_1 = new JRDesignField();
field_default_1.setDescription("given-name");
field_default_1.setValueClass(String.class);
String chunk_default_1 = "";

JRDesignField field_last_0 = new JRDesignField();
field_last_0.setDescription("family-name");
field_last_0.setValueClass(String.class);
String chunk_last_0 = "";

JRDesignField field_last_1 = new JRDesignField();
field_last_1.setDescription("given-name");
field_last_1.setValueClass(String.class);
String chunk_last_1 = "";

int count = 1;
boolean hasLast = false;
boolean maxCount = false;
away: while ( subDs.next() ) {
if ( count++ >6) {
 elems.add(new String[]{ "et al.", "" } );
  maxCount = true;
 break away;
}
switch (count) {
default:
chunk_default_0 = (String)subDs.getFieldValue(field_default_0);chunk_default_0 = xmlEncode(chunk_default_0);chunk_default_0 = chunk_default_0!=null && chunk_default_0.length()>0 ? chunk_default_0 : "";chunk_default_0 = chunk_default_0.length()>0 ? (chunk_default_0) : ""; chunk_default_0 = (chunk_default_0);chunk_default_0 = chunk_default_0;
chunk_default_1 = (String)subDs.getFieldValue(field_default_1);chunk_default_1 = xmlEncode(chunk_default_1);chunk_default_1 = chunk_default_1!=null && chunk_default_1.length()>0 ? chunk_default_1 : "";chunk_default_1 = get_initials(chunk_default_1);chunk_default_1 = chunk_default_1.length()>0 ? (chunk_default_1) : ""; chunk_default_1 = get_initials(chunk_default_1);chunk_default_1 = chunk_default_1;
str = chunk_default_0 + insertDelimiter(chunk_default_0, ", ", chunk_default_1) + chunk_default_1;elems.add( new String[]{ str, ", " } );
break;
}
if (!hasLast) hasLast = true;
if (hasLast) {
chunk_last_0 = (String)subDs.getFieldValue(field_last_0);chunk_last_0 = xmlEncode(chunk_last_0);chunk_last_0 = chunk_last_0!=null && chunk_last_0.length()>0 ? chunk_last_0 : "";chunk_last_0 = chunk_last_0.length()>0 ? (chunk_last_0) : ""; chunk_last_0 = (chunk_last_0);chunk_last_0 = chunk_last_0;
chunk_last_1 = (String)subDs.getFieldValue(field_last_1);chunk_last_1 = xmlEncode(chunk_last_1);chunk_last_1 = chunk_last_1!=null && chunk_last_1.length()>0 ? chunk_last_1 : "";chunk_last_1 = get_initials(chunk_last_1);chunk_last_1 = chunk_last_1.length()>0 ? (chunk_last_1) : ""; chunk_last_1 = get_initials(chunk_last_1);chunk_last_1 = chunk_last_1;
last = chunk_last_0 + insertDelimiter(chunk_last_0, ", ", chunk_last_1) + chunk_last_1;delim = ", &amp; ";}

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
public String getCS_1_CSLD_3_P_default_E_2() throws Exception {
String result = "";
String str = "";
String last = "";
String delim = "";
JRXmlDataSource ds = ((JRXmlDataSource) this.getParameterValue("REPORT_DATA_SOURCE"));
JRXmlDataSource subDs = ds.subDataSource("md-record/publication/creator[@role='author']/organization");
JRDesignField field_default_0 = new JRDesignField();
field_default_0.setDescription("organization-name");
field_default_0.setValueClass(String.class);
String chunk_default_0 = "";

int count = 1;
boolean hasLast = false;
boolean maxCount = false;
away: while ( subDs.next() ) {
if ( count++ >6) {
 elems.add(new String[]{ "et al.", "" } );
  maxCount = true;
 break away;
}
switch (count) {
default:
chunk_default_0 = (String)subDs.getFieldValue(field_default_0);chunk_default_0 = xmlEncode(chunk_default_0);chunk_default_0 = chunk_default_0!=null && chunk_default_0.length()>0 ? chunk_default_0 : "";chunk_default_0 = chunk_default_0.length()>0 ? (chunk_default_0) : ""; chunk_default_0 = (chunk_default_0);chunk_default_0 = chunk_default_0;
str = chunk_default_0;elems.add( new String[]{ str, ", " } );
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
public String getCS_1_CSLD_4_P_default_E_1() throws Exception {
String result = "";
String str = "";
String last = "";
String delim = "";
JRXmlDataSource ds = ((JRXmlDataSource) this.getParameterValue("REPORT_DATA_SOURCE"));
JRXmlDataSource subDs = ds.subDataSource("md-record/publication/creator[@role='author']/person|organization");
JRDesignField field_default_0 = new JRDesignField();
field_default_0.setDescription("family-name|organization-name");
field_default_0.setValueClass(String.class);
String chunk_default_0 = "";

JRDesignField field_default_1 = new JRDesignField();
field_default_1.setDescription("given-name");
field_default_1.setValueClass(String.class);
String chunk_default_1 = "";

JRDesignField field_last_0 = new JRDesignField();
field_last_0.setDescription("family-name");
field_last_0.setValueClass(String.class);
String chunk_last_0 = "";

JRDesignField field_last_1 = new JRDesignField();
field_last_1.setDescription("given-name");
field_last_1.setValueClass(String.class);
String chunk_last_1 = "";

int count = 1;
boolean hasLast = false;
boolean maxCount = false;
away: while ( subDs.next() ) {
if ( count++ >6) {
 elems.add(new String[]{ "et al.", "" } );
  maxCount = true;
 break away;
}
switch (count) {
default:
chunk_default_0 = (String)subDs.getFieldValue(field_default_0);chunk_default_0 = xmlEncode(chunk_default_0);chunk_default_0 = chunk_default_0!=null && chunk_default_0.length()>0 ? chunk_default_0 : "";chunk_default_0 = chunk_default_0.length()>0 ? (chunk_default_0) : ""; chunk_default_0 = (chunk_default_0);chunk_default_0 = chunk_default_0;
chunk_default_1 = (String)subDs.getFieldValue(field_default_1);chunk_default_1 = xmlEncode(chunk_default_1);chunk_default_1 = chunk_default_1!=null && chunk_default_1.length()>0 ? chunk_default_1 : "";chunk_default_1 = get_initials(chunk_default_1);chunk_default_1 = chunk_default_1.length()>0 ? (chunk_default_1) : ""; chunk_default_1 = get_initials(chunk_default_1);chunk_default_1 = chunk_default_1;
str = chunk_default_0 + insertDelimiter(chunk_default_0, ", ", chunk_default_1) + chunk_default_1;elems.add( new String[]{ str, ", " } );
break;
}
if (!hasLast) hasLast = true;
if (hasLast) {
chunk_last_0 = (String)subDs.getFieldValue(field_last_0);chunk_last_0 = xmlEncode(chunk_last_0);chunk_last_0 = chunk_last_0!=null && chunk_last_0.length()>0 ? chunk_last_0 : "";chunk_last_0 = chunk_last_0.length()>0 ? (chunk_last_0) : ""; chunk_last_0 = (chunk_last_0);chunk_last_0 = chunk_last_0;
chunk_last_1 = (String)subDs.getFieldValue(field_last_1);chunk_last_1 = xmlEncode(chunk_last_1);chunk_last_1 = chunk_last_1!=null && chunk_last_1.length()>0 ? chunk_last_1 : "";chunk_last_1 = get_initials(chunk_last_1);chunk_last_1 = chunk_last_1.length()>0 ? (chunk_last_1) : ""; chunk_last_1 = get_initials(chunk_last_1);chunk_last_1 = chunk_last_1;
last = chunk_last_0 + insertDelimiter(chunk_last_0, ", ", chunk_last_1) + chunk_last_1;delim = ", &amp; ";}

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