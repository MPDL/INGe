package de.mpg.mpdl.inge.transformation.transformers;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;

import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamSource;

import de.mpg.mpdl.inge.transformation.ChainableTransformer;
import de.mpg.mpdl.inge.transformation.SingleTransformer;
import de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT;
import de.mpg.mpdl.inge.transformation.TransformerModule;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.transformation.results.TransformerResult;
import de.mpg.mpdl.inge.transformation.results.TransformerStreamResult;
import de.mpg.mpdl.inge.transformation.sources.TransformerSource;
import de.mpg.mpdl.inge.transformation.transformers.helpers.mab.MABImport;

@TransformerModule(sourceFormat = FORMAT.MAB_STRING, targetFormat = FORMAT.MAB_XML)
public class MabToMabXml extends SingleTransformer implements ChainableTransformer {

	@Override
	public void transform(TransformerSource source, TransformerResult result)
			throws TransformationException {
		try {

			MABImport mab = new MABImport();
	        String resultXmlString = mab.transformMAB2XML(getStringFromSource(source));
			
			XslTransformer.xmlSourceToXmlResult(new StreamSource(new StringReader(resultXmlString)), (Result)result);	
			
		} catch (Exception e) {
			throw new TransformationException("Error while transforming EndNote to EndNote XML", e);
		}


	}

	@Override
	public TransformerResult createNewInBetweenResult() {
		return new TransformerStreamResult(new ByteArrayOutputStream());
	}

	


}
