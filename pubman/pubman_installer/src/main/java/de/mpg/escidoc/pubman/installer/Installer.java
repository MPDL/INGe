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
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/
package de.mpg.escidoc.pubman.installer;


import java.net.URL;

import org.apache.log4j.Logger;


/**
 * @author endres
 *
 */
public class Installer
{

    /**
     * @param args
     */
    
    private Logger logger = null;
    
    
    public Installer() {
        logger = Logger.getLogger(Installer.class);
        
    }
    
    public static void main(String[] args)
    {
        Installer installer = new Installer();
        installer.logger.info("logger is working!");
        try
        {
//            InitialDataset dataset = new InitialDataset(new URL("http://framework:8080"));
//            String handle = dataset.loginToCoreservice("sysadmin", "eSciDoc");
//            installer.logger.info( handle );
//            installer.logger.info( dataset.retrieveContentModel("escidoc:persistent4", handle));
              Configuration config = new Configuration("configuration/pubman.properties");      
            config.store("test.properties");
            
        } catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
