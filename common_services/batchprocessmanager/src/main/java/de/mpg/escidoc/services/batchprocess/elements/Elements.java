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
* Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.services.batchprocess.elements;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import de.mpg.escidoc.services.batchprocess.BatchProcess;
import de.mpg.escidoc.services.batchprocess.helper.CommandHelper;

public abstract class Elements<T> extends BatchProcess
{
    protected List<T> elements = new ArrayList<T>();
    protected int maximumNumberOfElements = 5000;
    private String userHandle;

    public Elements(String[] args)
    {
        init(args);
        String max = CommandHelper.getArgument("-n", args, false);
        if (max != null)
        {
            this.maximumNumberOfElements = Integer.parseInt(max);
        }
        retrieveElements();
    }

    public static Elements<?> factory(String[] args)
    {
        try
        {
            Constructor c = Class.forName(CommandHelper.getArgument("-e", args, true)).getConstructor(
                    new Class[] { String[].class });
            return (Elements<?>)c.newInstance(new Object[] { args });
        }
        catch (Exception e)
        {
            throw new RuntimeException(CommandHelper.getArgument("-e", args, true) + " is not a valid Element name", e);
        }
    }

    public List<T> getElements()
    {
        return elements;
    }

    public void setElements(List<T> elements)
    {
        this.elements = elements;
    }


	public String getUserHandle()
    {
        return userHandle;
    }

    public void setUserHandle(String userHandle)
    {
        this.userHandle = userHandle;
    }

    public abstract void init(String[] args);

    public abstract void retrieveElements();

    public abstract CoreServiceObjectType getObjectType();
}
