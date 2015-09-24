package de.mpg.escidoc.services.citation_style_language_manager;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.TransformerFactoryImpl;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import de.undercouch.citeproc.CSL;
import de.undercouch.citeproc.ItemDataProvider;
import de.undercouch.citeproc.output.Bibliography;
import de.mpg.escidoc.services.common.valueobjects.ExportFormatVO;

/**
 * CitationStyleLanguageManagerDefaultImpl is used to generate a citation for an
 * escidoc item or a list of escidoc items
 * 
 * @author walter
 * 
 */
@Stateless
@Remote
public class CitationStyleLanguageManagerDefaultImpl implements
		CitationStyleLanguageManagerInterface {

	private final static Logger logger = Logger
			.getLogger(CitationStyleLanguageManagerDefaultImpl.class);
	
	private final static String TRANSFORMATION_ITEM_LIST_2_SNIPPET = "itemList2snippet.xsl";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.citation_style_language_manager.CitationStyleLanguageManagerInterface
	 * #getOutput(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public byte[] getOutput(ExportFormatVO exportFormat,
			String itemList) {
		List<String> citationList = new ArrayList<String>();
		StringWriter snippet = new StringWriter();
		byte[] result = null;
		try {
			ItemDataProvider itemDataProvider = new MetadataProvider(itemList);
			CSL citeproc = new CSL(
					itemDataProvider,
					CitationStyleLanguageUtils.loadStyleFromJsonUrl(exportFormat.getId()));
			citeproc.registerCitationItems(itemDataProvider.getIds());
			citeproc.setOutputFormat("html");
			Bibliography bibl = citeproc.makeBibliography();
			TransformerFactory factory = new TransformerFactoryImpl();
			Transformer transformer = factory.newTransformer(new StreamSource(this.getClass().getClassLoader().getResourceAsStream(TRANSFORMATION_ITEM_LIST_2_SNIPPET)));
			for (String citation : bibl.getEntries()) {
				citation = citation.trim();
				citation = citation.substring(23, citation.indexOf("</div>"));
				System.out.println(citation);
				citationList.add(citation);
			}
			transformer.setParameter("citations", citationList);
			transformer.transform(new StreamSource(new StringReader(itemList)), new StreamResult(snippet));
			System.out.println(snippet);
			result = snippet.toString().getBytes("UTF-8");

		} catch (IOException e) {
			logger.error("Error creating CSL processor", e);
		} catch (TransformerConfigurationException e) {
			logger.error("Eror preparing transformation itemList to snippet", e);
		} catch (TransformerException e) {
			logger.error("Eror transforming itemList to snippet", e);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.citation_style_language_manager.CitationStyleLanguageManagerInterface
	 * #isCitationStyle(java.lang.String)
	 */
	@Override
	public boolean isCitationStyle(String cs) {

		return false;
	}

	public List<String> getListOfStyles() {
		List<String> styleList = new ArrayList<String>();
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpGet httpget = new HttpGet("http://pubman.mpdl.mpg.de/cone/iso639-3/all?format=jquery");

			System.out.println("Executing request " + httpget.getRequestLine());

			// Create a custom response handler
			ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

				@Override
				public String handleResponse(final HttpResponse response)
						throws ClientProtocolException, IOException {
					int status = response.getStatusLine().getStatusCode();
					if (status >= 200 && status < 300) {
						HttpEntity entity = response.getEntity();
						return entity != null ? EntityUtils.toString(entity)
								: null;
					} else {
						throw new ClientProtocolException(
								"Unexpected response status: " + status);
					}
				}

			};
			String responseBody = httpclient.execute(httpget, responseHandler);
			if (logger.isDebugEnabled()) {
				logger.debug("-------------------------\n" + responseBody
						+ "-------------------------\n");
			}

		} catch (Exception e) {
			logger.error("Error getting list of citation styles available", e);
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				logger.error("error getting ");
			}
		}
		return styleList;
	}

}
