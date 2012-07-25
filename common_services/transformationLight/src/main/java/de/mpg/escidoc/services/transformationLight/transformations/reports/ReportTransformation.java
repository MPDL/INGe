package de.mpg.escidoc.services.transformationLight.transformations.reports;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.TransformerFactoryImpl;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.common.util.PropertyReader;
import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.transformationLight.exceptions.TransformationNotSupportedException;
import de.mpg.escidoc.services.transformationLight.transformations.LocalUriResolver;
import de.mpg.escidoc.services.transformationLight.valueObjects.Format;
/**
 * The Report Transformation Class.
 * @author gergana (initial creation)
 *
 */
public class ReportTransformation {

	private final Logger logger = Logger.getLogger(ReportTransformation.class);

	private final String REPORT_XSLT_LOCATION = "transformations/reports/xslt";

	public ReportTransformation() {
	}
	
	/**
     * Transform the given file with the Report transformation file. The XSL file expects a parameter
     * institutsId for querying all CoNE persons for the given institute or department.
     * The result is a sorted report, where is a section for each person in CoNE of the given institutsId.
     * The publications of each person are sorted regarding the sort order file.
     * 
     * @param String formatFrom
     * @param String formatTo
     * @param String itemXML
     * @param Map<String, String> Configuration for the XSLT, parameter institutsId for CoNE. institutsId 
     * could be single Id or several Ids separated with whitespace. 
     * @return String The generated String with appended sort order 
     */
	public String reportTransform(String formatFrom, Format formatTo,
			String itemXML, Map<String, String> configuration)
			throws TransformationNotSupportedException {
			
		String xsltUri = formatFrom.toLowerCase() + "2" + formatTo.getName().toLowerCase() + "_" + ("text/html".equals(formatTo.getType()) ? "html" : "indesign") + ".xsl";

		TransformerFactory factory = new TransformerFactoryImpl();
		factory.setURIResolver(new LocalUriResolver(this.REPORT_XSLT_LOCATION));
		StringWriter writer = new StringWriter();

		try {
			ClassLoader cl = this.getClass().getClassLoader();
			InputStream in = cl.getResourceAsStream(this.REPORT_XSLT_LOCATION
					+ "/" + xsltUri);
			Transformer transformer = factory.newTransformer(new StreamSource(
					in));
			for (String key : configuration.keySet()) {
				transformer.setParameter(key, configuration.get(key));
				logger.info("Starting transformation with params " + key + ", "
						+ configuration.get(key));
			}
			transformer.setParameter("indesign-namespace", configuration.get(PropertyReader.getProperty("escidoc.report.indesign.namespace")));
			
			String itemXMLwithSortOrder = appendSortOrderToItemXml(itemXML);
			
			StringReader xmlSource = new StringReader(itemXMLwithSortOrder);
			transformer.transform(new StreamSource(xmlSource),
					new StreamResult(writer));
		} catch (Exception e) {
			this.logger.error(
					"An error occurred during a other format transformation.",
					e);
			throw new RuntimeException();
		}

		return writer.toString();
	}
	
	/**
     * Append sort order to the eSciDoc-XML input file. The sort order is read from the configuration file 
     * and has XML format. The sort order is appended before the first item-element.
     * This is needed for the key lookup table in XSLT. 
     * 
     * @param String The eSciDoc-XML input file, to which the sort order is appended
     * @return String The generated String with appended sort order 
     */
	private String appendSortOrderToItemXml(String itemXML) {
		String inString = null;
		try {
			String reportSortOrderPath = PropertyReader.getProperty("escidoc.transformation.report.sortorder.filename");
			inString = ResourceUtil.getResourceAsString(reportSortOrderPath);
		} catch (Exception e1) {
			logger.info("The report sort order file can not be located");
			e1.printStackTrace();
		}

		boolean firstOccurrence = true;

		String firstItem = "<escidocItem:item";
		String buff;
		StringBuffer sb = new StringBuffer();

		BufferedReader reader = new BufferedReader(new StringReader(itemXML));
		try {
			while ((buff = reader.readLine()) != null) {
				if (buff.contains(firstItem) && firstOccurrence) {
					sb.append(inString).append("\n");
					sb.append(buff).append("\n");
					firstOccurrence = false;
				} else {
					sb.append(buff).append("\n");
				}
			}
			reader.close();
		} catch (IOException e) {
			throw new RuntimeException("error by reading of items string", e);
		}
		return sb.toString();
	}
}
