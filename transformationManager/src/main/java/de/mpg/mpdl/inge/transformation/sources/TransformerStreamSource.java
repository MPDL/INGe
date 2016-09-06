package de.mpg.mpdl.inge.transformation.sources;

import java.io.InputStream;
import java.io.Reader;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import de.mpg.mpdl.inge.model.valueobjects.ValueObject;

public class TransformerStreamSource extends StreamSource implements TransformerSource, Source {


	//private ValueObject valueObject;
	

	public TransformerStreamSource(InputStream inputStream) {
		super(inputStream);
	}

	public TransformerStreamSource(Reader reader) {
		super(reader);
	}

	/*

	public TransformerStreamSource(ValueObject vo)
	{
		this.valueObject = vo;
	}

	
	public ValueObject getValueObject() {
		return valueObject;
	}

	public void setValueObject(ValueObject valueObject) {
		this.valueObject = valueObject;
	}
	*/



}
