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

package de.mpg.escidoc.pubman.releases;

import de.mpg.escidoc.pubman.util.ValueObjectWrapper;
import de.mpg.escidoc.services.common.valueobjects.EventLogEntryVO;

/**
 * Wrapper for EventLogEntryVOs that provides additional attributes for the presentation layer. 
 * 
 * @author: Tobias Schraut, created 18.10.2007
 * @version: $Revision: 1587 $ $LastChangedDate: 2007-11-20 10:54:36 +0100 (Di, 20 Nov 2007) $
 */
public class PubItemVersionVOWrapper extends ValueObjectWrapper
{
    protected EventLogEntryVO version = null;
    
    /**
     * Public constructor.
     */
    public PubItemVersionVOWrapper()
    {        
        super();
    }
    
    public PubItemVersionVOWrapper(EventLogEntryVO pubItemVersionVO)
    {
        this.version = pubItemVersionVO;
    }
    
    public EventLogEntryVO getValueObject()
    {
        return (EventLogEntryVO)this.valueObject;
    }

    public void setValueObject(EventLogEntryVO pubItemVersionVO)
    {
        this.valueObject = pubItemVersionVO;
    }

    public EventLogEntryVO getVersion()
    {
        return version;
    }

    public void setVersion(EventLogEntryVO version)
    {
        this.version = version;
    }
    
}
