<%@page import="javax.xml.transform.stream.StreamResult"%>
<%@page import="javax.xml.transform.Result"%>
<%@page import="de.mpg.mpdl.inge.util.ResourceUtil"%>
<%@page import="javax.xml.transform.stream.StreamSource"%>
<%@page import="javax.xml.transform.TransformerFactory"%>
<%@page import="javax.xml.transform.Transformer"%>
<%

	Transformer transformer = new net.sf.saxon.TransformerFactoryImpl().newTransformer(new StreamSource(ResourceUtil.getResourceAsStream("rule2html.xsl", this.getClass().getClassLoader())));
	Result result = new StreamResult(out);
	transformer.setParameter("rule-text", request.getParameter("rule-text"));
	transformer.transform(new StreamSource(ResourceUtil.getResourceAsStream("ruler.xml", this.getClass().getClassLoader())), result);

%>