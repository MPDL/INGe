package de.mpg.mpdl.inge.transformation;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;

import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.transformation.results.TransformerResult;

public interface ChainableTransformer extends Transformer {

  TransformerResult createNewInBetweenResult();

  void xmlSourceToXmlResult(Source s, Result r) throws TransformationException, TransformerException;
}
