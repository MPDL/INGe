package de.mpg.escidoc.pubman.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.myfaces.trinidad.model.UploadedFile;

import de.mpg.escidoc.services.common.valueobjects.PubFileVO;

public class PubFileVOPresentation implements UploadedFile {

	private PubFileVO file;
	
	public PubFileVOPresentation(PubFileVO file)
	{
		this.file = file; 
	}

	public void dispose() {
		
	}

	public String getFilename() {
		return file.getName();
	}

	public InputStream getInputStream() throws IOException {
		return null;
	}

	public long getLength() {
		return file.getSize();
	}

	public Object getOpaqueData() {
		return null;
	}

	public String getContentType()
	{
		return file.getContentTypeString();
	}

}
