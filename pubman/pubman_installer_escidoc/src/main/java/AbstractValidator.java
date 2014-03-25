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
import com.izforge.izpack.installer.DataValidator;

/**
 * Basic class for all validators.
 * 
 * @author André Schenk
 */
public abstract class AbstractValidator implements DataValidator {

    protected final StringBuilder errorMessage = new StringBuilder();

    protected final StringBuilder warningMessage = new StringBuilder();

    /*
     * (non-Javadoc)
     * 
     * @see com.izforge.izpack.installer.DataValidator#getDefaultAnswer ()
     */
    @Override
    public boolean getDefaultAnswer() {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.izforge.izpack.installer.DataValidator#getErrorMessageId ()
     */
    @Override
    public String getErrorMessageId() {
        return errorMessage.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.izforge.izpack.installer.DataValidator#getWarningMessageId ()
     */
    @Override
    public String getWarningMessageId() {
        return warningMessage.toString();
    }

    protected void clearErrorMessage() {
        if (!errorMessage.toString().isEmpty()) {
            errorMessage.delete(0, errorMessage.length());
        }
    }

    protected void clearWarningMessage() {
        if (!warningMessage.toString().isEmpty()) {
            warningMessage.delete(0, warningMessage.length());
        }
    }
}
