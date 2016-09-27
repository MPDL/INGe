package de.mpg.mpdl.inge.transformation.transformers;

import java.io.ByteArrayOutputStream;

import javax.xml.transform.Result;

import org.marc4j.MarcReader;
import org.marc4j.MarcStreamReader;
import org.marc4j.marc.Record;

import de.mpg.mpdl.inge.transformation.ChainableTransformer;
import de.mpg.mpdl.inge.transformation.SingleTransformer;
import de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT;
import de.mpg.mpdl.inge.transformation.TransformerModule;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.transformation.results.TransformerResult;
import de.mpg.mpdl.inge.transformation.results.TransformerStreamResult;
import de.mpg.mpdl.inge.transformation.sources.TransformerSource;
import de.mpg.mpdl.inge.transformation.sources.TransformerStreamSource;
import de.mpg.mpdl.inge.transformation.transformers.helpers.marc.MarcXmlWriterNSFix;

@TransformerModule(sourceFormat = FORMAT.MARC_21_STRING, targetFormat = FORMAT.MARC_XML)
public class Marc21ToMarcXml extends SingleTransformer implements ChainableTransformer {

	@Override
	public void transform(TransformerSource source, TransformerResult result)
			throws TransformationException {
		try {


			
			      MarcReader reader = new MarcStreamReader(((TransformerStreamSource)source).getInputStream(), "UTF-8");
			      //OutputStream resultOs = new ByteArrayOutputStream();

			      MarcXmlWriterNSFix writer = new MarcXmlWriterNSFix((Result)result);

			      while (reader.hasNext()) {
			        Record record = reader.next();
			        writer.write(record);
			      }

			      writer.close();
			

		} catch (Exception e) {
			throw new TransformationException("Error while transforming Marc21 to Marc XML", e);
		}


	}

	@Override
	public TransformerResult createNewInBetweenResult() {
		return new TransformerStreamResult(new ByteArrayOutputStream());
	}


	
	


}