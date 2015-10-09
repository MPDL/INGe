package de.mpg.escidoc.services.transformationLight.transformations.reports;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.transformationLight.Configurable;
import de.mpg.escidoc.services.transformationLight.Transformation;
import de.mpg.escidoc.services.transformationLight.Transformation.TransformationModule;
import de.mpg.escidoc.services.transformationLight.exceptions.TransformationNotSupportedException;
import de.mpg.escidoc.services.transformationLight.valueObjects.Format;
/**
 * The Report Transformation Interface.
 * @author gergana (initial creation)
 *
 */
@TransformationModule
public class ReportTransformationInterface implements Transformation, Configurable {

	private final Logger logger = Logger
			.getLogger(ReportTransformationInterface.class);

	private static final Format JUS_REPORT_SNIPPET_FORMAT = new Format("jus_report_snippet", "application/xml", "UTF-8");
	private static final Format JUS_OUT_FORMAT_INDESIGN = new Format("jus_out", "application/xml", "UTF-8");
	private static final Format JUS_OUT_FORMAT_HTML = new Format("jus_out", "text/html", "UTF-8");

	private ReportTransformation transformer;

	public ReportTransformationInterface() {
		this.transformer = new ReportTransformation();
	}

	/**
	 * {@inheritDoc}
	 */
	public Format[] getSourceFormats() throws RuntimeException {
		 return new Format[]{JUS_REPORT_SNIPPET_FORMAT};
	}

	/**
	 * {@inheritDoc}
	 */
	public Format[] getSourceFormats(Format trg) throws RuntimeException {
		if (trg != null && (trg.matches(JUS_OUT_FORMAT_INDESIGN) || trg.matches(JUS_OUT_FORMAT_HTML)))
        {
			return new Format[]{JUS_REPORT_SNIPPET_FORMAT};
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
		if (src != null && src.matches(JUS_REPORT_SNIPPET_FORMAT))
        {
			return new Format[]{JUS_OUT_FORMAT_INDESIGN, JUS_OUT_FORMAT_HTML};
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

		if (srcFormat.getName().toLowerCase().startsWith("jus")) {
			try {
				if (configuration != null) {
					transformedXml = this.transformer.reportTransform(srcFormatName, trgFormat, new String(src,"UTF-8"), configuration);
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
		logger.info("get config values " + JUS_REPORT_SNIPPET_FORMAT);
		return null;
	}

}
