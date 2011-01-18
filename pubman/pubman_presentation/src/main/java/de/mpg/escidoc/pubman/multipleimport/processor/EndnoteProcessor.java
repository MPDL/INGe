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
* Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.pubman.multipleimport.processor;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

import org.apache.axis.encoding.Base64;

/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class EndnoteProcessor extends FormatProcessor
{
    
    private boolean init = false;
    private String[] items = null;
    private int counter = -1;
    private int length = -1;
    private byte[] originalData = null;
    
    /* (non-Javadoc)
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext()
    {
        if (!init)
        {
            initialize();
        }
        return (this.items != null && this.counter < this.items.length);
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    public String next() throws NoSuchElementException
    {
        if (!init)
        {
            initialize();
        }
        if (this.items != null && this.counter < this.items.length)
        {
            this.counter++;
            return items[counter - 1];
        }
        else
        {
            throw new NoSuchElementException("No more entries left");
        }
        
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#remove()
     */
    public void remove()
    {
        throw new RuntimeException("Method not implemented");
    }

    private void initialize()
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
            
            String inputString = new String(this.originalData, this.encoding);

            //replace first empty lines and BOM
            inputString = Pattern.compile("^.*?%", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(inputString).replaceFirst("%");
            
            BufferedReader reader = new BufferedReader(new StringReader(inputString));
            
            String buff;
            boolean firstItem = true;
            int count = 0;
            StringBuffer sb = null;
            List<String> l = new ArrayList<String>();
            
            while ((buff = reader.readLine()) != null)
            {
                
                if ( buff.trim().equals("") )
                {
                    count++; 
                }
                else
                {
                    //first item handling
                    if ( firstItem )
                    {
                        firstItem = false;
                        sb = new StringBuffer();
                    }
                    // new item 
                    else if ( count >= 1 && buff.startsWith("%0") ) 
                    {
                        l.add(sb.toString().trim());
                        count = 0;
                        sb = new StringBuffer();
                    }
                    sb.append(buff).append("\n");                            
                }

            }
            //add last item
            if ( sb != null )
            {
                l.add(sb.toString().trim());
            }

            reader.close();
            
            items = (String[])l.toArray(new String[l.size()]);
            
            this.length = items.length;
            
            counter = 0;
            
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error reading input stream", e);
        }
        
    }

    /* (non-Javadoc)
     * @see de.mpg.escidoc.pubman.multipleimport.processor.FormatProcessor#getLength()
     */
    @Override
    public int getLength()
    {
        return length;
    }

    /* (non-Javadoc)
     * @see de.mpg.escidoc.pubman.multipleimport.processor.FormatProcessor#getDataAsBase64()
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
    
}
