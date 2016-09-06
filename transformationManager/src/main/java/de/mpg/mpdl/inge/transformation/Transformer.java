package de.mpg.mpdl.inge.transformation;

import java.util.Map;

import de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.transformation.results.TransformerResult;
import de.mpg.mpdl.inge.transformation.sources.TransformerSource;

public interface Transformer{


	public void transform(TransformerSource source, TransformerResult result) throws TransformationException;
	
	public Map<String,String> getConfiguration();
	
	public void setConfiguration(Map<String,String> config);
	
	public void setSourceFormat(FORMAT sourceFormat);
	
	public FORMAT getSourceFormat();
	
	public void setTargetFormat(FORMAT targetFormat);
	
	public FORMAT getTargetFormat();
	

	
}
