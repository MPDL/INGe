/*
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
 * Copyright 2006-2010 Fachinformationszentrum Karlsruhe Gesellschaft
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */

package test;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.mpg.escidoc.services.reporting.ReportFHI;

public class ReportingTest {

	private Logger logger = Logger.getLogger(ReportingTest.class);

	static ReportFHI rep;

	
	@BeforeClass
	public static void getReportInstance() throws IOException, URISyntaxException, ServiceException
	{
		rep = new ReportFHI();
	}
	
	@Test
//	 @Ignore
	public final void testReportGeneration() throws Exception 
	{
		ReportFHI rep = new ReportFHI();
		for (String att: rep.generateReport())
		{
			if (new File(att).length() == 0)
			{
				fail("Empty attachment file: " + att);
			}
		}
		
	}
	
	@Test
	@Ignore
	public final void testReportAndSend() throws Exception 
	{
		rep.generateAndSendReport();
	}

}
