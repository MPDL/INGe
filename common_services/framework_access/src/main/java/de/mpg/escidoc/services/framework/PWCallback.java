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
package de.mpg.escidoc.services.framework;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.ws.security.WSPasswordCallback;

/**
 * PWCallback for the Client.<p/> Sets the eSciDoc user handle as the password if the technical username "eSciDocUser"
 * is provided when calling the webservice.<br>
 * This password can be changed by using the <code>setHandle</code> and reset by using the <code>resetHandle</code>
 * methods.
 *
 * @author Peter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * @revised by BrP: 03.09.2007
 */
public class PWCallback implements CallbackHandler
{
    private String handle;

    /**
     * Sets the eSciDoc user handle to the provided value.
     *
     * @param hd The eSciDoc user handle to use.
     */
    public PWCallback(String hd)
    {
        handle = hd;
    }

    /**
     * The handle method of the callback handler.
     *
     * @param callbacks the WSPasswordCallback implementation
     * @throws IOException Exception
     * @throws UnsupportedCallbackException Exception
     * @see javax.security.auth.callback.CallbackHandler#handle (javax.security.auth.callback.Callback[])
     */
    public void handle(final Callback[] callbacks) throws IOException, UnsupportedCallbackException
    {
        for (int i = 0; i < callbacks.length; i++)
        {
            if (callbacks[i] instanceof WSPasswordCallback)
            {
                WSPasswordCallback pc = (WSPasswordCallback) callbacks[i];
                if ("eSciDocUser".equals(pc.getIdentifer()))
                {
                    pc.setPassword(handle);
                }
            }
            else
            {
                throw new UnsupportedCallbackException(callbacks[i], "Unrecognized Callback");
            }
        }
    }
}