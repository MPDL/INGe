package de.mpg.escidoc.services.transformation.transformations.reports;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.TransformerFactoryImpl;

import org.apache.log4j.Logger;

import de.mpg.escidoc.metadataprofile.schema.x01.transformation.TransformationType;
import de.mpg.escidoc.metadataprofile.schema.x01.transformation.TransformationsDocument;
import de.mpg.escidoc.metadataprofile.schema.x01.transformation.TransformationsType;
import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.transformation.Configurable;
import de.mpg.escidoc.services.transformation.Transformation;
import de.mpg.escidoc.services.transformation.Util;
import de.mpg.escidoc.services.transformation.Transformation.TransformationModule;
import de.mpg.escidoc.services.transformation.exceptions.TransformationNotSupportedException;
import de.mpg.escidoc.services.transformation.transformations.LocalUriResolver;
import de.mpg.escidoc.services.transformation.transformations.otherFormats.OtherFormatsTransformation;
import de.mpg.escidoc.services.transformation.transformations.otherFormats.escidoc.eSciDocVer1ToeSciDocVer2;
import de.mpg.escidoc.services.transformation.transformations.otherFormats.escidoc.eSciDocVer2ToeSciDocVer1;
import de.mpg.escidoc.services.transformation.valueObjects.Format;
/**
 * The Report Transformation Interface.
 * @author gergana (initial creation)
 *
 */
@TransformationModule
public class ReportTransformationInterface implements Transformation, Configurable {

	private final Logger logger = Logger
			.getLogger(ReportTransformationInterface.class);

	private static final Format JUS_IN_FORMAT = new Format("jus_in", "application/xml", "UTF-8");
	private static final Format JUS_OUT_FORMAT = new Format("jus_out", "application/xml", "UTF-8");

	private ReportTransformation trasformer;

	public ReportTransformationInterface() {
		this.trasformer = new ReportTransformation();
	}

	/**
	 * {@inheritDoc}
	 */
	public Format[] getSourceFormats() throws RuntimeException {
		 return new Format[]{JUS_IN_FORMAT};
	}

	/**
	 * {@inheritDoc}
	 */
	public Format[] getSourceFormats(Format trg) throws RuntimeException {
		if (trg != null && trg.matches(JUS_OUT_FORMAT))
        {
			return new Format[]{JUS_IN_FORMAT};
        }
        else
        {
            return new Format[]{};
        }
	}

	/**
	 * {@inheritDoc}
	 */
	@Deprecated
	public String getSourceFormatsAsXml() throws RuntimeException {
		throw new RuntimeException("Not implemented");
	}

	/**
	 * {@inheritDoc}
	 */
	public Format[] getTargetFormats(Format src) throws RuntimeException {
		if (src != null && src.matches(JUS_IN_FORMAT))
        {
			return new Format[]{JUS_OUT_FORMAT};
        }
        else
        {
            return new Format[]{};
        }
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Deprecated
	public String getTargetFormatsAsXml(String srcFormatName, String srcType,
			String srcEncoding) throws RuntimeException {
		throw new RuntimeException("Not implemented");
	}

	/**
	 * {@inheritDoc}
	 */
	public byte[] transform(byte[] src, String srcFormatName, String srcType,
			String srcEncoding, String trgFormatName, String trgType,
			String trgEncoding, String service)
			throws TransformationNotSupportedException, RuntimeException {
		Format source = new Format(srcFormatName, srcType, srcEncoding);
		Format target = new Format(trgFormatName, trgType, trgEncoding);
		return this.transform(src, source, target, service);
	}

	/**
	 * {@inheritDoc}
	 */
	public byte[] transform(byte[] src, Format srcFormat, Format trgFormat, String service) 
		throws TransformationNotSupportedException {
		this.logger.warn("Transformation without parameter institutsId is not supported: \n"
				+ srcFormat.getName() + ", " + srcFormat.getType() + ", "
				+ srcFormat.getEncoding() + "\n" + trgFormat.getName()
				+ ", " + trgFormat.getType() + ", "
				+ trgFormat.getEncoding());
		throw new TransformationNotSupportedException();
	}
	
	public byte[] transform(byte[] src, Format srcFormat, Format trgFormat, String service, Map<String, String> configuration)
			throws TransformationNotSupportedException, RuntimeException {
		byte[] result = null;
		boolean supported = false;
		String transformedXml = null;

		String srcFormatName = srcFormat.getName();
		String trgFormatName = trgFormat.getName();

		if (srcFormat.getName().toLowerCase().startsWith("jus")) {
			try {
				if (configuration != null) {
					transformedXml = this.trasformer.reportTransform(srcFormatName, trgFormatName, new String(src,"UTF-8"), configuration);
				} else {
					this.logger.warn("Transformation without parameter institutsId is not supported: \n"
							+ srcFormat.getName() + ", " + srcFormat.getType() + ", "
							+ srcFormat.getEncoding() + "\n" + trgFormat.getName()
							+ ", " + trgFormat.getType() + ", "
							+ trgFormat.getEncoding());
					throw new TransformationNotSupportedException();
				}
				result = transformedXml.getBytes("UTF-8");
			} catch (Exception e) {
				this.logger.warn("An error occurred during transformation with jusXslt.",e);
			}
			supported = true;
		}

		if (!supported) {
			this.logger.warn("Transformation not supported: \n"
					+ srcFormat.getName() + ", " + srcFormat.getType() + ", "
					+ srcFormat.getEncoding() + "\n" + trgFormat.getName()
					+ ", " + trgFormat.getType() + ", "
					+ trgFormat.getEncoding());
			throw new TransformationNotSupportedException();
		}
		return result;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Map<String, String> getConfiguration(Format srcFormat,
			Format trgFormat) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public List<String> getConfigurationValues(Format srcFormat,
			Format trgFormat, String key) throws Exception {
		logger.info("get config values " + JUS_IN_FORMAT);
		return null;
	}

}
