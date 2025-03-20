package de.mpg.mpdl.inge.transformation.results;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.Writer;

import javax.xml.transform.stream.StreamResult;

import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.transformation.sources.TransformerSource;
import de.mpg.mpdl.inge.transformation.sources.TransformerStreamSource;

public class TransformerStreamResult extends StreamResult implements TransformerResult {



  public TransformerStreamResult(OutputStream outputStream) {
    super(outputStream);
  }


  public TransformerStreamResult(Writer writer) {
    super(writer);
  }

  @Override
  public TransformerSource createSourceFromInBetweenResult() throws TransformationException {
    try {
      byte[] buf = ((ByteArrayOutputStream) getOutputStream()).toByteArray();
      TransformerStreamSource ts = new TransformerStreamSource(new ByteArrayInputStream(buf));
      return ts;
    } catch (Exception e) {
      throw new TransformationException("Could not create new Xml Source", e);
    }

  }



}
