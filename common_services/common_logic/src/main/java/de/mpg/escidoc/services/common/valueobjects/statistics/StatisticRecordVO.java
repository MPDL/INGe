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
* Copyright 2006-2008 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/
package de.mpg.escidoc.services.common.valueobjects.statistics;

import java.io.StringWriter;
import java.util.List;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;

import de.escidoc.www.services.sm.StatisticDataHandler;
import de.mpg.escidoc.services.common.valueobjects.intelligent.IntelligentVO;
import de.mpg.escidoc.services.framework.ServiceLocator;


/**
 * VO class representing a statistic-record
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author: mfranke $ (last modification)
 * @version $Revision: 1951 $ $LastChangedDate: 2009-05-07 10:27:06 +0200 (Do, 07 Mai 2009) $
 *
 */
public class StatisticRecordVO extends IntelligentVO
{
    private String scope;
    
    private List<StatisticReportRecordParamVO> paramList;

    public List<StatisticReportRecordParamVO> getParamList()
    {
        return paramList;
    }

    public void setParamList(List<StatisticReportRecordParamVO> paramList)
    {
        this.paramList = paramList;
    }

    public void setScope(String scope)
    {
        this.scope = scope;
    }

    public String getScope()
    {
        return scope;
    }
    
    /**
     * Creates this statistic record in the coreservice.
     * @param userHandle A user handle for authentication in the coreservice.
     * @throws Exception If an error occurs in coreservice or during marshalling/unmarshalling.
     */
    public void createInCoreservice(String userHandle) throws RuntimeException
    {
        Factory.create(this, userHandle);
    }
    
    
    private static class Factory
    {
        /**
         * Creates the given statistic record
         * @param statisticRecord The statistic record to be created.
         * @param userHandle A user handle for authentication in the coreservice.
         * @return The created User Group.
         * @throws Exception If an error occurs in coreservice or during marshalling/unmarshalling.
         */
        private static void create(StatisticRecordVO statisticRecord, String userHandle) throws RuntimeException
        {
            try
            {
                StatisticDataHandler sdh = ServiceLocator.getStatisticDataHandler(userHandle);
                
                IBindingFactory bindingFactory = BindingDirectory.getFactory("StatisticRecord", StatisticRecordVO.class);
                IMarshallingContext macxt = bindingFactory.createMarshallingContext();
                StringWriter sw = new StringWriter();
                macxt.marshalDocument(statisticRecord, "UTF-8", null, sw);
                sdh.create(sw.toString());
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
    }
    
}
