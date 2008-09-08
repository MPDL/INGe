package de.mpg.escidoc.services.edoc;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;


public class XSLTTransform {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception
	{
		// TODO Auto-generated method stub

		File source = new File(args[0]);
		File stylesheet = new File(args[1]);

		XSLTTransform transform = new XSLTTransform();
		transform.transform(source, stylesheet, System.out);
	}

	public void transform(File source, File stylesheet, OutputStream out) throws Exception
	{
		
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer(new StreamSource(new FileInputStream(stylesheet)));
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.transform(new StreamSource(new InputStreamReader(new FileInputStream(source), "UTF-8")), new StreamResult(out));
	}

}
