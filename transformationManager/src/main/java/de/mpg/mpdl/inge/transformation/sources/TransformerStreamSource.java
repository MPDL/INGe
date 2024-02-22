package de.mpg.mpdl.inge.transformation.sources;

import java.io.InputStream;
import java.io.Reader;

import javax.xml.transform.stream.StreamSource;

public class TransformerStreamSource extends StreamSource implements TransformerSource {
  public TransformerStreamSource(InputStream inputStream) {
    super(inputStream);
  }

  public TransformerStreamSource(Reader reader) {
    super(reader);
  }
}
