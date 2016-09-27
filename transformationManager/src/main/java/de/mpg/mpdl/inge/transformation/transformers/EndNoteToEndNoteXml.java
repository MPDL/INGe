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
import de.mpg.mpdl.inge.transformation.transformers.helpers.endnote.EndNoteImport;

@TransformerModule(sourceFormat = FORMAT.ENDNOTE_STRING, targetFormat = FORMAT.ENDNOTE_XML)
public class EndNoteToEndNoteXml extends SingleTransformer implements ChainableTransformer {

	@Override
	public void transform(TransformerSource source, TransformerResult result)
			throws TransformationException {
		try {


					
			EndNoteImport endNoteImport = new EndNoteImport();
			String res = endNoteImport.transformEndNote2XML(getStringFromSource(source));
			
			XslTransformer.xmlSourceToXmlResult(new StreamSource(new StringReader(res)), (Result)result);	
			
		} catch (Exception e) {
			throw new TransformationException("Error while transforming EndNote to EndNote XML", e);
		}


	}

	@Override
	public TransformerResult createNewInBetweenResult() {
		return new TransformerStreamResult(new ByteArrayOutputStream());
	}

	


}