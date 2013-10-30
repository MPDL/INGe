/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
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
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
import com.izforge.izpack.installer.AutomatedInstallData;
import com.izforge.izpack.installer.DataValidator.Status;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Check if a HTTP URL is reachable and return an error if it does.
 * 
 * @author André Schenk
 */
public class HTTPValidator extends AbstractValidator {

    /*
     * Check if the escidoc-core is running.
     * 
     * @return OK if the URL is offline, ERROR otherwise
     * 
     * @see com.izforge.izpack.installer.DataValidator#validateData (com.izforge.izpack.installer.AutomatedInstallData)
     */
    @Override
    public Status validateData(AutomatedInstallData data) {
        Status status = Status.ERROR;
        final String host = data.getVariable("EscidocHost");
        final String port = data.getVariable("EscidocPort");

        try {
            final URL url = new URL("http", host, Integer.parseInt(port), "/");

            buildErrorMessage(url.toString());

            final URLConnection conn = url.openConnection();

            conn.setConnectTimeout(5000);
            conn.connect();
        }
        catch (MalformedURLException e) {
            System.err.println(e.getMessage());
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
            status = Status.OK;
        }
        return status;
    }

    private void buildErrorMessage(String message) {
        clearErrorMessage();
        errorMessage.append("The URL \"");
        errorMessage.append(message);
        errorMessage.append("\" is online.");
    }
}
