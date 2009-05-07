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

package de.mpg.escidoc.services.common.logging;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.apache.log4j.Logger;

/**
 * Interceptor class for debug logging the duration of a method.
 *
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @revised by MuJ: 03.09.2007
 */
public class LogMethodDurationInterceptor
{
    /**
     * The interceptor method. Logs the duration of the surrounded method.
     * 
     * @see javax.interceptor.InvocationContext
     * @param ctx The invocation context
     * @return The result of the intercepted method
     * @throws Exception
     */
    @AroundInvoke
    public Object log(InvocationContext ctx) throws Exception
    {
        String className = ctx.getTarget().getClass().getName();
        String methodName = ctx.getMethod().getName();
        String target = className + "." + methodName + "()";

        Logger logger = Logger.getLogger(className);

        long start = System.currentTimeMillis();
        
        try
        {
            return ctx.proceed();
        }
        catch (Exception e)
        {
            throw e;
        }
        finally
        {
            long time = System.currentTimeMillis() - start;
            logger.debug(MessageCreator.getMessage(CommonLogicMessages.METHOD_DURATION, new Object[] { target
                    ,time}));
        }
    }
}
