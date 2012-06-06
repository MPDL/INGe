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

package de.mpg.escidoc.services.transformation.transformations;

import de.mpg.escidoc.services.transformation.Transformation;
import de.mpg.escidoc.services.transformation.exceptions.TransformationNotSupportedException;
import de.mpg.escidoc.services.transformation.valueObjects.Format;

/**
 * @author franke
 */
public abstract class TransformationTemplate implements Transformation
{

	protected abstract Format getSourceFormat();
	protected abstract Format getTargetFormat();
	
	public Format[] getSourceFormats() throws RuntimeException
	{
		return new Format[] {getSourceFormat()};
	}

	public Format[] getSourceFormats(Format trg) throws RuntimeException
	{
		if (getTargetFormat().equals(trg))
		{
			return new Format[] {getTargetFormat()};
		}
		else
		{
			return new Format[] {};
		}
	}

	public String getSourceFormatsAsXml() throws RuntimeException
	{
		return null;
	}

	public Format[] getTargetFormats(Format src) throws RuntimeException
	{
		if (getSourceFormat().equals(src))
		{
			return new Format[] {getTargetFormat()};
		}
		else
		{
			return new Format[] {};
		}
	}

	public String getTargetFormatsAsXml(String arg0, String arg1, String arg2)
			throws RuntimeException
	{
		return null;
	}

	public byte[] transform(byte[] input, String srcName, String srcType, String srcEncoding,
			String trgName, String trgType, String trgEncoding, String service)
			throws TransformationNotSupportedException, RuntimeException
	{
		return transform(input, new Format(srcName, srcType, srcEncoding), new Format(trgName, trgType, trgEncoding), service);
	}

}
