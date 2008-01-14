import net.sf.jasperreports.engine.JRDefaultScriptlet;
import net.sf.jasperreports.engine.JRScriptletException;
import net.sf.jasperreports.engine.JRAbstractScriptlet;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.util.JRStringUtil;
import org.w3c.dom.Document;
import java.util.ArrayList;

public class ScriptletForRepeatableElements_1156755126347 extends JRDefaultScriptlet {
private ArrayList<String[]> elems = new ArrayList<String[]>();
private String insertDelimiter(String left, String delim, String right) {
    String result;
    return (delim!=null && delim.length()>0 &&
       left!=null && left.length()>0 &&
        right!=null && right.length()>0 ) ? delim : "";
}
public String xmlEncode(String str) {
	if (str!=null && str.length()>0) {
		str = JRStringUtil.xmlEncode(str);
		str = JRStringUtil.xmlEncode(str);
	}
	return str;
}
public String cleanCit(String str) {
	if (str!=null && str.length()>0) {
		str = str.replaceAll("\\s+", " ");
		str = str.replaceAll("[.][.]", ".");
	}
	return str;
}
public String getCS_1_CSLD_1_P_default_E_2() throws Exception {
String result = "";
String str = "";
String last = "";
String delim = "";
JRXmlDataSource ds = ((JRXmlDataSource) this.getParameterValue("REPORT_DATA_SOURCE"));
JRXmlDataSource subDs = ds.subDataSource("/record/creators/person");
String chunk_default_0 = "";

String chunk_default_1 = "";

String chunk_last_0 = "";

String chunk_last_1 = "";

int count = 1;
boolean hasLast = false;
boolean maxCount = false;
away: while ( subDs.next() ) {
if ( count++ >6) {
 str = "et al.";
 String[] elem = new String[2];
 elem[0] = str; elem[1] = "";
 elems.add(elem);
  maxCount = true;
 break away;
}
Document d = subDs.subDocument();
switch (count) {
default:
chunk_default_0 = subDs.getText(d.getElementsByTagName("creatorlastname").item(0));
chunk_default_0 = chunk_default_0!=null && chunk_default_0.length()>0 ? chunk_default_0 : "";
chunk_default_0 = chunk_default_0.length()>0 ? (chunk_default_0) : ""; 
chunk_default_1 = subDs.getText(d.getElementsByTagName("creatorini").item(0));
chunk_default_1 = chunk_default_1!=null && chunk_default_1.length()>0 ? chunk_default_1 : "";
chunk_default_1 = chunk_default_1.length()>0 ? (chunk_default_1) : ""; 
str = chunk_default_0 + insertDelimiter(chunk_default_0, ", ", chunk_default_1) + chunk_default_1;
String[] elem = new String[2];
elem[0] = str; elem[1] = ", ";
elems.add(elem);
break;
}
if (!hasLast) hasLast = true;
if (hasLast) {
chunk_last_0 = subDs.getText(d.getElementsByTagName("creatorlastname").item(0));
chunk_last_0 = chunk_last_0!=null && chunk_last_0.length()>0 ? chunk_last_0 : "";
chunk_last_0 = chunk_last_0.length()>0 ? (chunk_last_0) : ""; 
chunk_last_1 = subDs.getText(d.getElementsByTagName("creatorini").item(0));
chunk_last_1 = chunk_last_1!=null && chunk_last_1.length()>0 ? chunk_last_1 : "";
chunk_last_1 = chunk_last_1.length()>0 ? (chunk_last_1) : ""; 
last = chunk_last_0 + insertDelimiter(chunk_last_0, ", ", chunk_last_1) + chunk_last_1;
delim = ", & ";
}

}
if (hasLast && count>2 && !subDs.next()  ) {
elems.remove(elems.size()-1);
String[] elem = (String[])elems.get(elems.size()-1);
elem[1] = delim;
elems.set(elems.size()-1, elem);
elem = new String[2];
elem[0] = last; elem[1] = "";
elems.add(elem);
}
for(int i=0; i<elems.size(); i++) {
String[] elem = (String[])elems.get(i);
result += elem[0].length()>0 ? elem[0] + (elems.size()>1?elem[1]:"") : "";
}
elems.clear();
result = xmlEncode(result);
return result;
}
public String getCS_1_CSLD_1_P_default_E_3() throws Exception {
String result = "";
String str = "";
String last = "";
String delim = "";
JRXmlDataSource ds = ((JRXmlDataSource) this.getParameterValue("REPORT_DATA_SOURCE"));
JRXmlDataSource subDs = ds.subDataSource("/record/creators/group");
String chunk_default_0 = "";

int count = 1;
boolean hasLast = false;
boolean maxCount = false;
away: while ( subDs.next() ) {
if ( count++ >6) {
 str = "et al.";
 String[] elem = new String[2];
 elem[0] = str; elem[1] = "";
 elems.add(elem);
  maxCount = true;
 break away;
}
Document d = subDs.subDocument();
switch (count) {
default:
chunk_default_0 = subDs.getText(d.getElementsByTagName("corporatebody").item(0));
chunk_default_0 = chunk_default_0!=null && chunk_default_0.length()>0 ? chunk_default_0 : "";
chunk_default_0 = chunk_default_0.length()>0 ? (chunk_default_0) : ""; 
str = chunk_default_0;
String[] elem = new String[2];
elem[0] = str; elem[1] = ", ";
elems.add(elem);
break;
}

}
for(int i=0; i<elems.size(); i++) {
String[] elem = (String[])elems.get(i);
result += elem[0].length()>0 ? elem[0] + (elems.size()>1?elem[1]:"") : "";
}
elems.clear();
result = xmlEncode(result);
return result;
}
public String getCS_1_CSLD_1_P_default_E_6() throws Exception {
String result = "";
String str = "";
String last = "";
String delim = "";
JRXmlDataSource ds = ((JRXmlDataSource) this.getParameterValue("REPORT_DATA_SOURCE"));
JRXmlDataSource subDs = ds.subDataSource("/record/source/sourcecreators/person");
String chunk_default_0 = "";

int count = -1;
while ( subDs.next() ) {
Document d = subDs.subDocument();
switch (count) {
default:
chunk_default_0 = subDs.getText(d.getElementsByTagName("creatorfullname").item(0));
chunk_default_0 = chunk_default_0!=null && chunk_default_0.length()>0 ? chunk_default_0 : "";
chunk_default_0 = chunk_default_0.length()>0 ? (chunk_default_0) : ""; 
str = chunk_default_0;
String[] elem = new String[2];
elem[0] = str; elem[1] = ", ";
elems.add(elem);
break;
}

}
for(int i=0; i<elems.size(); i++) {
String[] elem = (String[])elems.get(i);
result += elem[0].length()>0 ? elem[0] + (elems.size()>1?elem[1]:"") : "";
}
elems.clear();
result = xmlEncode(result);
return result;
}
public String getCS_1_CSLD_2_P_default_E_2() throws Exception {
String result = "";
String str = "";
String last = "";
String delim = "";
JRXmlDataSource ds = ((JRXmlDataSource) this.getParameterValue("REPORT_DATA_SOURCE"));
JRXmlDataSource subDs = ds.subDataSource("/record/creators/person");
String chunk_default_0 = "";

String chunk_default_1 = "";

String chunk_last_0 = "";

String chunk_last_1 = "";

int count = 1;
boolean hasLast = false;
boolean maxCount = false;
away: while ( subDs.next() ) {
if ( count++ >6) {
 str = "et al.";
 String[] elem = new String[2];
 elem[0] = str; elem[1] = "";
 elems.add(elem);
  maxCount = true;
 break away;
}
Document d = subDs.subDocument();
switch (count) {
default:
chunk_default_0 = subDs.getText(d.getElementsByTagName("creatorlastname").item(0));
chunk_default_0 = chunk_default_0!=null && chunk_default_0.length()>0 ? chunk_default_0 : "";
chunk_default_0 = chunk_default_0.length()>0 ? (chunk_default_0) : ""; 
chunk_default_1 = subDs.getText(d.getElementsByTagName("creatorini").item(0));
chunk_default_1 = chunk_default_1!=null && chunk_default_1.length()>0 ? chunk_default_1 : "";
chunk_default_1 = chunk_default_1.length()>0 ? (chunk_default_1) : ""; 
str = chunk_default_0 + insertDelimiter(chunk_default_0, ", ", chunk_default_1) + chunk_default_1;
String[] elem = new String[2];
elem[0] = str; elem[1] = ", ";
elems.add(elem);
break;
}
if (!hasLast) hasLast = true;
if (hasLast) {
chunk_last_0 = subDs.getText(d.getElementsByTagName("creatorlastname").item(0));
chunk_last_0 = chunk_last_0!=null && chunk_last_0.length()>0 ? chunk_last_0 : "";
chunk_last_0 = chunk_last_0.length()>0 ? (chunk_last_0) : ""; 
chunk_last_1 = subDs.getText(d.getElementsByTagName("creatorini").item(0));
chunk_last_1 = chunk_last_1!=null && chunk_last_1.length()>0 ? chunk_last_1 : "";
chunk_last_1 = chunk_last_1.length()>0 ? (chunk_last_1) : ""; 
last = chunk_last_0 + insertDelimiter(chunk_last_0, ", ", chunk_last_1) + chunk_last_1;
delim = ",& ";
}

}
if (hasLast && count>2 && !subDs.next()  ) {
elems.remove(elems.size()-1);
String[] elem = (String[])elems.get(elems.size()-1);
elem[1] = delim;
elems.set(elems.size()-1, elem);
elem = new String[2];
elem[0] = last; elem[1] = "";
elems.add(elem);
}
for(int i=0; i<elems.size(); i++) {
String[] elem = (String[])elems.get(i);
result += elem[0].length()>0 ? elem[0] + (elems.size()>1?elem[1]:"") : "";
}
elems.clear();
result = xmlEncode(result);
return result;
}
public String getCS_1_CSLD_2_P_default_E_3() throws Exception {
String result = "";
String str = "";
String last = "";
String delim = "";
JRXmlDataSource ds = ((JRXmlDataSource) this.getParameterValue("REPORT_DATA_SOURCE"));
JRXmlDataSource subDs = ds.subDataSource("/record/creators/group");
String chunk_default_0 = "";

int count = 1;
boolean hasLast = false;
boolean maxCount = false;
away: while ( subDs.next() ) {
if ( count++ >6) {
 str = "et al.";
 String[] elem = new String[2];
 elem[0] = str; elem[1] = "";
 elems.add(elem);
  maxCount = true;
 break away;
}
Document d = subDs.subDocument();
switch (count) {
default:
chunk_default_0 = subDs.getText(d.getElementsByTagName("corporatebody").item(0));
chunk_default_0 = chunk_default_0!=null && chunk_default_0.length()>0 ? chunk_default_0 : "";
chunk_default_0 = chunk_default_0.length()>0 ? (chunk_default_0) : ""; 
str = chunk_default_0;
String[] elem = new String[2];
elem[0] = str; elem[1] = ", ";
elems.add(elem);
break;
}

}
for(int i=0; i<elems.size(); i++) {
String[] elem = (String[])elems.get(i);
result += elem[0].length()>0 ? elem[0] + (elems.size()>1?elem[1]:"") : "";
}
elems.clear();
result = xmlEncode(result);
return result;
}
public String getCS_1_CSLD_3_P_default_E_2() throws Exception {
String result = "";
String str = "";
String last = "";
String delim = "";
JRXmlDataSource ds = ((JRXmlDataSource) this.getParameterValue("REPORT_DATA_SOURCE"));
JRXmlDataSource subDs = ds.subDataSource("/record/creators/person");
String chunk_default_0 = "";

String chunk_default_1 = "";

String chunk_last_0 = "";

String chunk_last_1 = "";

int count = 1;
boolean hasLast = false;
boolean maxCount = false;
away: while ( subDs.next() ) {
if ( count++ >6) {
 str = "et al.";
 String[] elem = new String[2];
 elem[0] = str; elem[1] = "";
 elems.add(elem);
  maxCount = true;
 break away;
}
Document d = subDs.subDocument();
switch (count) {
default:
chunk_default_0 = subDs.getText(d.getElementsByTagName("creatorlastname").item(0));
chunk_default_0 = chunk_default_0!=null && chunk_default_0.length()>0 ? chunk_default_0 : "";
chunk_default_0 = chunk_default_0.length()>0 ? (chunk_default_0) : ""; 
chunk_default_1 = subDs.getText(d.getElementsByTagName("creatorini").item(0));
chunk_default_1 = chunk_default_1!=null && chunk_default_1.length()>0 ? chunk_default_1 : "";
chunk_default_1 = chunk_default_1.length()>0 ? (chunk_default_1) : ""; 
str = chunk_default_0 + insertDelimiter(chunk_default_0, ", ", chunk_default_1) + chunk_default_1;
String[] elem = new String[2];
elem[0] = str; elem[1] = ", ";
elems.add(elem);
break;
}
if (!hasLast) hasLast = true;
if (hasLast) {
chunk_last_0 = subDs.getText(d.getElementsByTagName("creatorlastname").item(0));
chunk_last_0 = chunk_last_0!=null && chunk_last_0.length()>0 ? chunk_last_0 : "";
chunk_last_0 = chunk_last_0.length()>0 ? (chunk_last_0) : ""; 
chunk_last_1 = subDs.getText(d.getElementsByTagName("creatorini").item(0));
chunk_last_1 = chunk_last_1!=null && chunk_last_1.length()>0 ? chunk_last_1 : "";
chunk_last_1 = chunk_last_1.length()>0 ? (chunk_last_1) : ""; 
last = chunk_last_0 + insertDelimiter(chunk_last_0, ", ", chunk_last_1) + chunk_last_1;
delim = ", & ";
}

}
if (hasLast && count>2 && !subDs.next()  ) {
elems.remove(elems.size()-1);
String[] elem = (String[])elems.get(elems.size()-1);
elem[1] = delim;
elems.set(elems.size()-1, elem);
elem = new String[2];
elem[0] = last; elem[1] = "";
elems.add(elem);
}
for(int i=0; i<elems.size(); i++) {
String[] elem = (String[])elems.get(i);
result += elem[0].length()>0 ? elem[0] + (elems.size()>1?elem[1]:"") : "";
}
elems.clear();
result = xmlEncode(result);
return result;
}
public String getCS_1_CSLD_3_P_default_E_3() throws Exception {
String result = "";
String str = "";
String last = "";
String delim = "";
JRXmlDataSource ds = ((JRXmlDataSource) this.getParameterValue("REPORT_DATA_SOURCE"));
JRXmlDataSource subDs = ds.subDataSource("/record/creators/group");
String chunk_default_0 = "";

int count = 1;
boolean hasLast = false;
boolean maxCount = false;
away: while ( subDs.next() ) {
if ( count++ >6) {
 str = "et al.";
 String[] elem = new String[2];
 elem[0] = str; elem[1] = "";
 elems.add(elem);
  maxCount = true;
 break away;
}
Document d = subDs.subDocument();
switch (count) {
default:
chunk_default_0 = subDs.getText(d.getElementsByTagName("corporatebody").item(0));
chunk_default_0 = chunk_default_0!=null && chunk_default_0.length()>0 ? chunk_default_0 : "";
chunk_default_0 = chunk_default_0.length()>0 ? (chunk_default_0) : ""; 
str = chunk_default_0;
String[] elem = new String[2];
elem[0] = str; elem[1] = ", ";
elems.add(elem);
break;
}

}
for(int i=0; i<elems.size(); i++) {
String[] elem = (String[])elems.get(i);
result += elem[0].length()>0 ? elem[0] + (elems.size()>1?elem[1]:"") : "";
}
elems.clear();
result = xmlEncode(result);
return result;
}
}