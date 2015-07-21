package de.mpg.escidoc.services.batchprocess.operations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.escidoc.www.services.om.ItemHandler;
import de.mpg.escidoc.services.batchprocess.BatchProcess;
import de.mpg.escidoc.services.batchprocess.BatchProcessReport.ReportEntry;
import de.mpg.escidoc.services.batchprocess.BatchProcessReport.ReportEntryStatusType;
import de.mpg.escidoc.services.batchprocess.elements.Elements;
import de.mpg.escidoc.services.batchprocess.helper.CommandHelper;
import de.mpg.escidoc.services.batchprocess.transformers.Transformer;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;
import de.mpg.escidoc.services.framework.ServiceLocator;

public class Edit extends Operation {
	private Transformer<?> transformer;
	private static final Logger logger = Logger.getLogger(Edit.class);

	public void execute(String[] args) {
		try {
			transformer = Transformer.getTransformer(CommandHelper.getArgument(
					"-t", args, true));
			transformer.setTransformed(elements.getTransformed());
			this.setTransformed(elements.getTransformed());
			elements.setElements(transformer.transform(elements.getElements()));
			update(elements);
			if (CoreServiceObjectStatus.SUBMITTED.equals(CommandHelper
					.getStatusEnumValue(CommandHelper.getArgument("-s", args,
							false)))
					|| CoreServiceObjectStatus.RELEASED.equals(CommandHelper
							.getStatusEnumValue(CommandHelper.getArgument("-s",
									args, false)))) {
				new Submit().execute(args);
			}
			if (CoreServiceObjectStatus.RELEASED.equals(CommandHelper
					.getStatusEnumValue(CommandHelper.getArgument("-s", args,
							false)))) {
				new Release().execute(args);
			}
		} catch (Exception e) {
			throw new RuntimeException("Error Batch Edit: ", e);
		}
	}

	public void update(Elements list) throws Exception {
		if (CoreServiceObjectType.ITEM.equals(list.getObjectType())) {
			updateItems(list);
		} else if (CoreServiceObjectType.CONTAINER.equals(list.getObjectType())) {
		}
	}

	private void updateItems(Elements list) throws Exception {
		ItemHandler ih = ServiceLocator
				.getItemHandler(elements.getUserHandle());
		String handle = elements.getUserHandle();
		XmlTransformingBean xmlTransforming = new XmlTransformingBean();
		List<ItemVO> updated = new ArrayList<ItemVO>();

		if (list.getElements() != null) {
			List<ItemVO> listElements = list.getElements();
			int listElementsSize = listElements.size();
			List<String> errorList = new ArrayList<String>();
			for (ItemVO ivo : listElements) {
				if (this.getTransformed().contains(
						ivo.getVersion().getObjectId())) {
					String xml = xmlTransforming.transformToItem(ivo);
					try {
						logger.info("Updating item "
								+ (listElements.indexOf(ivo) + 1)  + " of "
								+ listElementsSize + " ["
								+ ivo.getVersion().getObjectIdAndVersion()
								+ "]");
						xml = ih.update(ivo.getVersion().getObjectId(), xml);
						logger.info("done!");
						updated.add(xmlTransforming.transformToItem(xml));
						this.report.addEntry("Update"
								+ ivo.getVersion().getObjectId(), "Edit "
								+ ivo.getVersion().getObjectId(),
								ReportEntryStatusType.FINE);
					} catch (Exception e) {
						logger.error("error", e);
						errorList.add("Could not update ["
								+ ivo.getVersion().getObjectId() + "]: "
								+ e.getLocalizedMessage());
						this.report.addEntry("Update"
								+ ivo.getVersion().getObjectId(), " Edit "
								+ ivo.getVersion().getObjectId(),
								ReportEntryStatusType.ERROR);
					}
				} else {
					updated.add(ivo);
				}
			}
			if (!errorList.isEmpty()) {
				logger.error("Some errors occured:");
				for (String error : errorList) {
					logger.error(error);
				}
			}
		}
		elements.setElements(updated);
	}
}
