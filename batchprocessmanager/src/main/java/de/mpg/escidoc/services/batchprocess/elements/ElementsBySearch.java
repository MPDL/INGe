/*
 *
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License"). You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE
 * or http://www.escidoc.org/license.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at license/ESCIDOC.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */

package de.mpg.escidoc.services.batchprocess.elements;

import gov.loc.www.zing.srw.SearchRetrieveRequestType;
import gov.loc.www.zing.srw.SearchRetrieveResponseType;

import java.util.ArrayList;
import java.util.List;

import org.apache.axis.types.NonNegativeInteger;
import org.apache.log4j.Logger;

import de.escidoc.www.services.om.ItemHandler;
import de.mpg.escidoc.services.batchprocess.BatchProcessReport.ReportEntryStatusType;
import de.mpg.escidoc.services.batchprocess.helper.CoreServiceHelper;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.LimitFilter;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.Filter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.FrameworkItemTypeFilter;
import de.mpg.escidoc.services.common.valueobjects.ItemVO.State;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;
import de.mpg.escidoc.services.framework.AdminHelper;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Class retrieves Elements via search by ID (every other search query is also
 * possible)
 * 
 * @author walter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public class ElementsBySearch extends Elements<ItemVO> {

	// escidoc_all
	// private static final String SEARCH_QUERY =
	// "escidoc.objid=\"escidoc:815087\" or escidoc.objid=\"escidoc:1301991\"";

	// item_container_admin
	private static final String SEARCH_QUERY = "\"/id\" any escidoc:853164";

	private static final Logger logger = Logger
			.getLogger(ElementsBySearch.class);

	public ElementsBySearch(String[] args) {
		super(args);
	}

	@Override
	public void init(String[] args) {
		try {
			setUserHandle(AdminHelper.loginUser(
					PropertyReader.getProperty("framework.admin.username"),
					PropertyReader.getProperty("framework.admin.password")));
		} catch (Exception e) {
			throw new RuntimeException(
					"Login error. Please make sure the user credentials (framework.admin.username, framework.admin.password) are provided in your settings.xml file."
							+ e);
		}
	}

	@Override
	public void retrieveElements() {
		try {

			/*
			 * SearchRetrieveRequestType searchRetrieveRequest = new
			 * SearchRetrieveRequestType();
			 * searchRetrieveRequest.setVersion("1.1");
			 * searchRetrieveRequest.setQuery(SEARCH_QUERY);
			 * searchRetrieveRequest.setMaximumRecords(new NonNegativeInteger(
			 * maximumNumberOfElements + ""));
			 * searchRetrieveRequest.setRecordPacking("xml");
			 * SearchRetrieveResponseType searchResult = ServiceLocator
			 * .getSearchHandler("item_container_admin",
			 * this.getUserHandle()).searchRetrieveOperation(
			 * searchRetrieveRequest);
			 */

			ItemHandler itemHandler = ServiceLocator
					.getItemHandler(getUserHandle());
			// define the filter criteria
			FilterTaskParamVO filter = new FilterTaskParamVO();
			Filter itemFilter = filter.new FrameworkItemTypeFilter(
					PropertyReader
							.getProperty("escidoc.framework_access.content-model.id.publication"));
			filter.getFilterList().add(itemFilter);
			
			Filter publicStatusFilter = filter.new ItemPublicStatusFilter(State.RELEASED);
			filter.getFilterList().add(publicStatusFilter);

			Filter queryFilter = filter.new CqlFilter(SEARCH_QUERY);
			filter.getFilterList().add(queryFilter);
			
			// nearly infinite number of Records
			Filter maximumRecordsFilter = filter.new LimitFilter("1000000");
			filter.getFilterList().add(maximumRecordsFilter);

			String searchResult = itemHandler.retrieveItems(filter.toMap());
			List<ItemVO> resultElements = new ArrayList<ItemVO>();
			resultElements.addAll(CoreServiceHelper
					.transformSearchResultXmlToListOfItemVO(searchResult));
			int resultElementsSize = resultElements.size();
			logger.info("Retrieved " + resultElementsSize
					+ " Elements as result of the search");

			// fetching each item again is needed, as content of files is null
			// in lists. So no update would be possible on items with internal
			// components
			logger.info("Starting to fetch single items");
			for (ItemVO item : resultElements) {
				XmlTransformingBean xmlTransforming = new XmlTransformingBean();
				elements.add(xmlTransforming.transformToItem(itemHandler
						.retrieve(item.getVersion().getObjectId())));
				logger.info("Retrieved item "
						+ (resultElements.indexOf(item) + 1) + " of "
						+ resultElementsSize + " ["
						+ item.getVersion().getObjectId() + "] ");
			}

			report.addEntry("retrieveElements", "Get Data",
					ReportEntryStatusType.FINE);
			logger.info(elements.size() + " items added for further use");
		} catch (Exception e) {
			throw new RuntimeException(
					"Error initializing ElementsById.java: ", e);
		}
	}

	@Override
	public CoreServiceObjectType getObjectType() {
		return CoreServiceObjectType.ITEM;
	}

}
