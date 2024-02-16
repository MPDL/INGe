package de.mpg.mpdl.inge.transformation.results;

import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.transformation.sources.TransformerSource;

public interface TransformerResult {


  TransformerSource createSourceFromInBetweenResult() throws TransformationException;



}
