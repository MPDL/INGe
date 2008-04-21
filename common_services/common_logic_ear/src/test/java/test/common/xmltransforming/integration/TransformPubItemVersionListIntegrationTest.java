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
 * or http://www.escidoc.de/license.
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
 * Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */

package test.common.xmltransforming.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import test.common.xmltransforming.XmlTransformingTestBase;
import de.fiz.escidoc.common.exceptions.application.invalid.InvalidContentException;
import de.fiz.escidoc.common.exceptions.application.invalid.InvalidXmlException;
import de.fiz.escidoc.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.fiz.escidoc.common.exceptions.application.missing.MissingAttributeValueException;
import de.fiz.escidoc.common.exceptions.application.missing.MissingContentException;
import de.fiz.escidoc.common.exceptions.application.missing.MissingElementValueException;
import de.fiz.escidoc.common.exceptions.application.missing.MissingMethodParameterException;
import de.fiz.escidoc.common.exceptions.application.notfound.ContentTypeNotFoundException;
import de.fiz.escidoc.common.exceptions.application.notfound.ContextNotFoundException;
import de.fiz.escidoc.common.exceptions.application.notfound.FileNotFoundException;
import de.fiz.escidoc.common.exceptions.application.notfound.ReferencedResourceNotFoundException;
import de.fiz.escidoc.common.exceptions.application.notfound.RelationPredicateNotFoundException;
import de.fiz.escidoc.common.exceptions.application.security.AuthenticationException;
import de.fiz.escidoc.common.exceptions.application.security.AuthorizationException;
import de.fiz.escidoc.common.exceptions.application.violated.ReadonlyAttributeViolationException;
import de.fiz.escidoc.common.exceptions.application.violated.ReadonlyElementViolationException;
import de.fiz.escidoc.common.exceptions.system.SystemException;
import de.fiz.escidoc.om.ItemHandlerRemote;
import de.mpg.escidoc.services.common.ItemSorting;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.referenceobjects.ItemRO;
import de.mpg.escidoc.services.common.valueobjects.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.VersionHistoryEntryVO;
import de.mpg.escidoc.services.common.valueobjects.comparator.VersionHistoryEntryVOComparator;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Test of {@link PubManTransforming} methods for transforming and integration
 * with common_logic and the framework.
 * 
 * @author Johannes M&uuml;ller (initial creation)
 * @author $Author: jmueller $ (last change)
 * @version $Revision: 649 $ $LastChangedDate: 2007-11-07 12:04:29 +0100 (Mi, 07
 *          Nov 2007) $
 * @revised by MuJ: 20.09.2007
 */
public class TransformPubItemVersionListIntegrationTest extends
		XmlTransformingTestBase {
	/**
	 * Logger for this class.
	 */
	private Logger logger = Logger.getLogger(getClass());

	private static XmlTransforming xmlTransforming;

	private static ItemSorting itemSorting;

	private String userHandle;

	/**
	 * Get an {@link XmlTransforming} instance once.
	 * 
	 * @throws Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		xmlTransforming = (XmlTransforming) getService(XmlTransforming.SERVICE_NAME);
		itemSorting = (ItemSorting) getService(ItemSorting.SERVICE_NAME);
	}

	/**
	 * Logs in as depositor and retrieves his grants (before every single test
	 * method).
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		// get user handle for user "test_dep_scientist"
		userHandle = loginScientist();

	}

	/**
	 * Logs out (after every single test method).
	 * 
	 * @throws Exception
	 */
	@After
	public void tearDown() throws Exception {
		logout(userHandle);
	}

	private PubItemVO createItem(String userHandle) throws TechnicalException,
			ServiceException, ReadonlyAttributeViolationException,
			XmlSchemaValidationException, ReadonlyElementViolationException,
			MissingContentException, MissingAttributeValueException,
			ContentTypeNotFoundException, ReferencedResourceNotFoundException,
			InvalidContentException, ContextNotFoundException,
			RelationPredicateNotFoundException, FileNotFoundException,
			MissingMethodParameterException, InvalidXmlException,
			MissingElementValueException, AuthenticationException,
			AuthorizationException, SystemException, RemoteException {
		PubItemVO itemVO = getComplexPubItemWithoutFiles();
		String itemXml = xmlTransforming.transformToItem(itemVO);
		ItemHandlerRemote ihr = ServiceLocator.getItemHandler(userHandle);
		String createdItemXml = ihr.create(itemXml);
		logger.debug("Item created:\n" + createdItemXml);
		PubItemVO createdItemVO = xmlTransforming
				.transformToPubItem(createdItemXml);
		String createdItemId = createdItemVO.getVersion().getObjectId();
		logger.info("PubItem '" + createdItemId + "' created.");
		return createdItemVO;
	}

	private PubItemVO updateItem(PubItemVO itemVO, String userHandle)
			throws TechnicalException, ServiceException,
			ReadonlyAttributeViolationException, XmlSchemaValidationException,
			ReadonlyElementViolationException, MissingContentException,
			MissingAttributeValueException, ContentTypeNotFoundException,
			ReferencedResourceNotFoundException, InvalidContentException,
			ContextNotFoundException, RelationPredicateNotFoundException,
			FileNotFoundException, MissingMethodParameterException,
			InvalidXmlException, MissingElementValueException,
			AuthenticationException, AuthorizationException, SystemException,
			RemoteException {
		String itemXml = xmlTransforming.transformToItem(itemVO);
		String itemId = itemVO.getVersion().getObjectId();
		ItemHandlerRemote ihr = ServiceLocator.getItemHandler(userHandle);
		logger.debug("Trying to update:\n" + itemXml);
		String updatedItemXml = ihr.update(itemId, itemXml);
		PubItemVO updatedItemVO = xmlTransforming
				.transformToPubItem(updatedItemXml);
		String updatedItemId = updatedItemVO.getVersion().getObjectId();
		logger.info("PubItem '" + updatedItemId + "' updated.");
		return updatedItemVO;
	}

	/**
	 * @throws Exception
	 * 
	 * Ignore this test untill update bug with special characters is fixed.
	 */
	@Test
	@Ignore
	public void testTransformPubItemVersionList() throws Exception {
		// create item
		PubItemVO item = createItem(userHandle);

		// update item multiple times
		int updateCount = 3;
		for (int i = 0; i < updateCount; i++) {
			String newTitle = item.getMetadata().getTitle().getValue();
			item.getMetadata().setTitle(new TextVO(newTitle + "*"));
			item = updateItem(item, userHandle);
		}

		// retrieve version history of item
		String itemId = item.getVersion().getObjectId();
		ItemHandlerRemote ihr = ServiceLocator.getItemHandler(userHandle);
		String itemVersionHistoryXml = ihr.retrieveVersionHistory(itemId);
		logger.info("Version history of PubItem '" + itemId + "' retrieved.");
		logger.debug(itemVersionHistoryXml);

		// transform the version history XML to a list of VersionHistoryEntryVOs
		long zeit = -System.currentTimeMillis();
		List<VersionHistoryEntryVO> versionList = xmlTransforming
				.transformToEventVOList(itemVersionHistoryXml);
		zeit += System.currentTimeMillis();
		logger.info("transformToVersionHistoryEntryVOList(" + itemId + ") -> "
				+ zeit + "ms");
		assertEquals(updateCount + 1, versionList.size());

		logger.info("########################Unsorted list:");
		for (int i = 0; i < updateCount + 1; i++) {
			logger.debug("VersionHistoryEntryVO[" + i + "]:");
			VersionHistoryEntryVO pubItemVersion = versionList.get(i);
			ItemRO ref = pubItemVersion.getReference();
			assertNotNull(ref);
			logger.debug(" -reference.objectId: " + ref.getObjectId());
			logger
					.debug(" -reference.versionNumber: "
							+ ref.getVersionNumber());
			Date modDate = pubItemVersion.getModificationDate();
			assertNotNull(modDate);
			logger.debug(" -modificationDate: "
					+ new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
							.format(modDate));
			PubItemVO.State status = pubItemVersion.getState();
			logger.debug(" -state: " + status);
			assertEquals(PubItemVO.State.PENDING, status);
		}

		// sort the version history list
		List<VersionHistoryEntryVO> sortedVersionList = itemSorting
				.sortItemVersionList(versionList,
						VersionHistoryEntryVOComparator.Criteria.DATE,
						VersionHistoryEntryVOComparator.Order.ASCENDING);

		logger.info("########################Sorted list:");
		for (int i = 0; i < updateCount + 1; i++) {
			logger.debug("VersionHistoryEntryVO[" + i + "]:");
			VersionHistoryEntryVO pubItemVersion = sortedVersionList.get(i);
			ItemRO ref = pubItemVersion.getReference();
			assertNotNull(ref);
			logger.debug(" -reference.objectId: " + ref.getObjectId());
			logger
					.debug(" -reference.versionNumber: "
							+ ref.getVersionNumber());
			Date modDate = pubItemVersion.getModificationDate();
			assertNotNull(modDate);
			logger.debug(" -modificationDate: "
					+ new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
							.format(modDate));
			PubItemVO.State status = pubItemVersion.getState();
			logger.debug(" -state: " + status);
			assertEquals(PubItemVO.State.PENDING, status);
		}

		// delete item
		ihr.delete(itemId);
		logger.info("PubItem '" + itemId + "' deleted.");
	}
}
