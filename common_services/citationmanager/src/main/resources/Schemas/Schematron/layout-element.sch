<?xml version="1.0" encoding="UTF-8"?>
<!--
 CDDL HEADER START

 The contents of this file are subject to the terms of the
 Common Development and Distribution License, Version 1.0 only
 (the "License"). You may not use this file except in compliance
 with the License.

 You can obtain a copy of the license at license/ESCIDOC.LICENSE
 or http://www.escidoc.de/license.
 See the License for the specific language governing permissions
 and limitations under the License.

 When distributing Covered Code, include this CDDL HEADER in each
 file and include the License file at license/ESCIDOC.LICENSE.
 If applicable, add the following below this CDDL HEADER, with the
 fields enclosed by brackets "[]" replaced with your own identifying
 information: Portions Copyright [yyyy] [name of copyright owner]

 CDDL HEADER END


 Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
-->
<!-- 
	Schematron rules for Citation Style Language 
	Initial creation: bsaquet
	$Author: vdm $
	$Revision: 146 $ $LastChangedDate: 2007-11-12 20:58:08 +0100 (Mon, 12 Nov 2007) $
-->
<sch:schema xmlns:sch="http://www.ascc.net/xml/schematron">
<sch:ns prefix="cs" uri="http://www.escidoc.de/citationstyle"/>
	<sch:pattern name="Check citation style XML structure">

		<!-- Parameters which are not applicable in non-repeatable elements: -->
		<sch:rule context="cs:layout-element[not(@repeatable)]/cs:parameters">
			
				<sch:report test="cs:internal-delimiter">The internal-delimiter 
					parameter cannot be defined for non-repeatable elements.</sch:report>
				<sch:report test="cs:max-count">The max-count 
					parameter cannot be defined for non-repeatable elements.</sch:report>
				<sch:report test="cs:max-count-ends-with">The max-count-ends-with
					parameter cannot be defined for non-repeatable elements.</sch:report>
		</sch:rule>

		<!-- What is not allowed in sub elements of the repeatable element: -->
		<sch:rule context="cs:layout-element[@repeatable = 'yes']/cs:elements/cs:layout-element">
			<sch:report test="@repeatable = 'yes'">Repeatable element 
				is not allowed at this level (layout-element of repeatable element).
			</sch:report>
			<sch:report test="cs:parameters/cs:valid-if">The valid-if
					parameter cannot be defined at this level (layout-element of repeatable element).
			</sch:report>
			<sch:report test="cs:parameters/cs:delimiter">The delimiter
					parameter cannot be defined at this level (layout-element of repeatable element).
			</sch:report>
			 <sch:report test="cs:parameters/cs:internal-delimiter">The internal-delimiter 
					parameter cannot be defined at this level (layout-element of repeatable element).
			</sch:report>
			  <sch:report test="cs:parameters/cs:max-count">The max-count
					parameter cannot be defined at this level (layout-element of repeatable element).
			</sch:report>
			  <sch:report test="cs:parameters/cs:max-count-ends-with">The max-count-ends-with 
					parameter cannot be defined at this level (layout-element of repeatable element).
			</sch:report>
		</sch:rule>
			
 </sch:pattern>
</sch:schema>
