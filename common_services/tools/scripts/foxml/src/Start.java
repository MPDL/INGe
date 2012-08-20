import java.io.File;

import de.mpg.escidoc.tools.Oaidc;
import de.mpg.escidoc.tools.Replace;
import de.mpg.escidoc.tools.Validate;

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
/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class Start
{
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception
    {
        if (args.length == 0)
        {
            System.out.println("usage: java Start <action> <params>");
            System.out.println("actions: oaidc, validate");
        }
        else if ("oaidc".equals(args[0]))
        {
            if (args.length != 3)
            {
                System.out.println("usage: java Start oaidc <root-dir> <stylesheet>");
            }
            else
            {
                new Oaidc(new File(args[1]), args[2]);
            }
        }
        else if ("validate".equals(args[0]))
        {
            if (args.length != 2)
            {
                System.out.println("usage: java Start validate <root-dir>");
            }
            else
            {
                new Validate(new File(args[1]));
            }
        }
        else if ("replace".equals(args[0]))
        {
            if (args.length != 2)
            {
                System.out.println("usage: java Start validate <root-dir>");
            }
            else
            {
                new Replace(new File(args[1]));
            }
        }
    }
}
