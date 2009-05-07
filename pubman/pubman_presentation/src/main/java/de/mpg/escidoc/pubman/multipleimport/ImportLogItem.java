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

package de.mpg.escidoc.pubman.multipleimport;

import java.io.StringWriter;
import java.util.Date;

import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.framework.PropertyReader;


/**
 * Class describing an import item. The parent is a {@link ImportLog}.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class ImportLogItem extends ImportLog
{
    private ImportLog parent;
    private String itemId;
    private PubItemVO itemVO;
    
    private static String link = null;
    
    /**
     * Constructor setting the parent import.
     * 
     * @param parent The parent import
     */
    public ImportLogItem(ImportLog parent)
    {
        this.setStartDate(new Date());
        this.setStatus(Status.PENDING);
        this.setErrorLevel(ErrorLevel.FINE);
        
        this.parent = parent;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setErrorLevel(ErrorLevel errorLevel)
    {
        super.setErrorLevel(errorLevel);
        if (parent != null)
        {
            parent.setErrorLevel(errorLevel);
        }
    }

    /**
     * @return the parent
     */
    public ImportLog getParent()
    {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(ImportLog parent)
    {
        this.parent = parent;
    }

    /**
     * @return the itemId
     */
    public String getItemId()
    {
        return itemId;
    }

    /**
     * @param itemId the itemId to set
     */
    public void setItemId(String itemId)
    {
        this.itemId = itemId;
    }
    
    /**
     * @return the itemVO
     */
    public PubItemVO getItemVO()
    {
        return itemVO;
    }

    /**
     * @param itemVO the itemVO to set
     */
    public void setItemVO(PubItemVO itemVO)
    {
        this.itemVO = itemVO;
    }

    protected String getRelevantString()
    {
        return getMessage();
    }
    

    /**
     * @return the itemLink
     */
    public String getLink()
    {
        if (link == null)
        {
            try
            {
                link = PropertyReader.getProperty("escidoc.pubman.instance.url")
                    + PropertyReader.getProperty("escidoc.pubman.instance.context.path")
                    + PropertyReader.getProperty("escidoc.pubman.item.pattern");
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
        return link.replaceAll("\\$1", itemId);
    }

    /**
     * @return the itemLink
     */
    public String getDetailsLink()
    {
        return "ImportItemDetails.jsp?id=" + getStoredId();
    }
    
    /**
     * @return An XML representation of this item
     */
    public String toXML()
    {
        StringWriter writer = new StringWriter();
        
        writer.write("<import-item ");
        writer.write("status=\"");
        writer.write(getStatus().toString());
        writer.write("\" error-level=\"");
        writer.write(getErrorLevel().toString());
        writer.write("\">\n");
        
        writer.write("\t<message>");
        writer.write(escape(getMessage()));
        writer.write("</message>\n");

        writer.write("\t<escidoc-id>");
        writer.write(getItemId());
        writer.write("</escidoc-id>\n");

        writer.write("\t<start-date>");
        writer.write(getStartDateFormatted());
        writer.write("</start-date>\n");
        
        if (getEndDate() != null)
        {
            writer.write("\t<end-date>");
            writer.write(getEndDateFormatted());
            writer.write("</end-date>\n");
        }
        
        writer.write("\t<items>\n");
        for (ImportLogItem item : getItems())
        {
            writer.write(item.toXML().replaceAll("(.*\n)", "\t\t$1"));
        }
        writer.write("\t</items>\n");
        
        writer.write("</import-item>\n");
        
        return writer.toString();
    }
}
