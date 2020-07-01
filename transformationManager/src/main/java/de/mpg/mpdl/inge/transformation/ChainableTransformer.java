package de.mpg.mpdl.inge.transformation;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;

import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.transformation.results.TransformerResult;

public interface ChainableTransformer extends Transformer {

  public TransformerResult createNewInBetweenResult();

  public void xmlSourceToXmlResult(Source s, Result r) throws TransformationException, TransformerException;
}
