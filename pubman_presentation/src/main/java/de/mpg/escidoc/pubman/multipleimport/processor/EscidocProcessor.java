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

package de.mpg.escidoc.pubman.multipleimport.processor;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;

import org.apache.axis.encoding.Base64;

import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;

/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class EscidocProcessor extends FormatProcessor
{
    private boolean init = false;
    private List<String> items = null;
    private int counter = -1;
    private int length = -1;
    private byte[] originalData = null;
    
    XmlTransforming xmlTransforming;
    
    public EscidocProcessor()
    {
        try
        {
            InitialContext context = new InitialContext();
            xmlTransforming = (XmlTransforming) context.lookup(XmlTransforming.SERVICE_NAME);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error initializing XmlTransforming", e);
        }
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getDataAsBase64()
    {
        if (this.originalData == null)
        {
            return null;
        }
        else
        {
            return Base64.encode(this.originalData);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getLength()
    {
        if (!this.init)
        {
            initialize();
        }
        return this.length;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasNext()
    {
        if (!this.init)
        {
            initialize();
        }
        return (this.counter < this.length);
    }

    /**
     * {@inheritDoc}
     */
    public String next()
    {
        if (!this.init)
        {
            initialize();
        }
        return items.get(counter++);
    }

    private void initialize()
    {
        if (getSource() == null)
        {
            throw new RuntimeException("No input source");
        }
        else
        {
            init = true;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int read;
            byte[] buffer = new byte[2048];
            try
            {
                while ((read = getSource().read(buffer)) != -1)
                {
                    byteArrayOutputStream.write(buffer, 0, read);
                }
                
                this.originalData = byteArrayOutputStream.toByteArray();
                
                List<PubItemVO> itemList;
                String source = new String(this.originalData, "UTF-8");
                if (source.contains("item-list"))
                {
                    itemList= xmlTransforming.transformToPubItemList(source);
                }
                else
                {
                    itemList = new ArrayList<PubItemVO>();
                    PubItemVO itemVO = xmlTransforming.transformToPubItem(source);
                    itemList.add(itemVO);
                }
                this.items = new ArrayList<String>();
                for (ItemVO itemVO : itemList)
                {
                    this.items.add(xmlTransforming.transformToItem(itemVO));
                }
                this.counter = 0;
                this.length = this.items.size();
                
            }
            catch (Exception e) {
                throw new RuntimeException("Error reading input stream", e);
            }
        }
    }

    /**
     * Not implemented.
     */
    @Deprecated
    public void remove()
    {
        throw new RuntimeException("Not implemented");
    }
    
}
