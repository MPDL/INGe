<!-- Stylesheet to render BMC article.dtd compliant XML files to basic html output -->
<!-- This stylesheet is made available under the Creative Commons Attribution license -->
<!-- http://creativecommons.org/licenses/by/2.0/ -->
<!-- It may be freely reused, adapted and redistributed as long as attribution remains intact -->
<!-- Copyright BioMed Central Limited 2004 --> 
<!-- Version 1.02  23rd March 2005 -->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:bmc="http://www.biomedcentral.com/xml/schemas/oai/2.0/">
	
<xsl:output method="html" encoding="utf-16" />
	
<!-- Paths for associated files-->
<!-- By default, files will be retrieved from BioMed Central server-->
<!-- If you maintain local copies of image files etc, then substitute as appropriate-->
<xsl:variable name="base-url">http://www.biomedcentral.com/</xsl:variable>	
<xsl:variable name="image-url">content/figures/</xsl:variable><!-- for graphics within fig tag -->

<xsl:variable name="inline-url">content/inline/</xsl:variable><!-- for graphics with paragraph tags (inline images) -->
<xsl:variable name="suppl-url">content/supplementary/</xsl:variable><!-- for graphics within fig tag -->

<xsl:template match="/">
	<html>
	<head>
		<style type="text/css"><!-- Basic style sheet to give sans-serif output -->
	 	body, h1, h2, h3, h4, table {font-family: sans-serif;}
		body {font-size: x-small;}
		h1 {font-size: x-large;}
		h2 {font-size: large;}
		h3 {font-size: medium;}
		h4 {font-size: small; font-weight: normal;}
		table {font-size: x-small;}
		</style>
	</head>

	<body>
		<xsl:for-each select="bmc:art">
				<xsl:for-each select="bmc:fm">
					<xsl:call-template name="render-tig" /><!-- this renders the article information -->
					<xsl:call-template name="article-map"/><!-- links to article sections, figures and tables -->
					<xsl:apply-templates select="bmc:abs" /><!-- renders the article abstract -->
				</xsl:for-each>
				<xsl:apply-templates select="bmc:bdy" /><!-- renders the main body -->
				<xsl:for-each select="bmc:bm">

					<xsl:apply-templates select="bmc:ack" /><!-- Acknowledgements -->
					<xsl:apply-templates select="bmc:refgrp"/><!-- References -->
					<xsl:apply-templates select="bmc:sec" /><!-- other matter -->
				</xsl:for-each>
			</xsl:for-each>
	</body>
	</html>
</xsl:template>

<!-- procedures for rendering the article information -->

<xsl:template name="render-tig">
	<a name="top"><xsl:apply-templates select="bmc:dochead" mode="tig"/></a>
	<xsl:call-template name="render-tig-bibl" />
			
	<xsl:apply-templates select="bmc:bibl/bmc:url" mode="tig"/>
	<xsl:apply-templates select="bmc:history" mode="tig"/>
	<xsl:apply-templates select="bmc:cpyrt"   mode="tig"/>
	<hr/>
	<xsl:apply-templates select="bmc:kwdg"     mode="tig"/>
</xsl:template>

<xsl:template name="render-tig-bibl">
	<xsl:for-each select="bmc:bibl">
		<xsl:apply-templates select="bmc:title" mode="tig" />
		<xsl:apply-templates select="bmc:aug"  mode="tig" />
		<xsl:call-template name="render-tig-bibl-details" />
	</xsl:for-each>
</xsl:template>

	
<xsl:template name="render-tig-bibl-details">

	<xsl:apply-templates select="bmc:insg" mode="tig"/>

	<xsl:call-template name="render-tig-emails" />
			
	<xsl:if test="(bmc:aug/bmc:au[@ce = 'yes']) or (bmc:aug/bmc:au[@ca = 'yes'])">
		<xsl:if test="bmc:aug/bmc:au[@ca='yes']">* Corresponding author&#160;&#160;</xsl:if>
		<xsl:if test="bmc:aug/bmc:au[@ce='yes']"><sup>&#8224;</sup> Contributed equally&#160;&#160;</xsl:if>
		<br /><br />
	</xsl:if>
	<xsl:apply-templates select="bmc:source"  mode="tig"/>

	<xsl:apply-templates select="bmc:pubdate" mode="tig"/>
	<xsl:apply-templates select="bmc:inpress" mode="tig"/>
		
	<xsl:if test="bmc:volume[1]">
		<xsl:text> </xsl:text><b><xsl:value-of select="bmc:volume"/></b><xsl:if test="bmc:issue">(<xsl:value-of select="bmc:issue"/>)</xsl:if><b>:</b>
	</xsl:if>
	<xsl:if test="bmc:fpage[1]">
		<xsl:value-of select="bmc:fpage"/><xsl:if test="bmc:lpage[1] and bmc:fpage[1] != bmc:lpage[1]">-<xsl:value-of select="bmc:lpage"/></xsl:if>

	</xsl:if>
	<xsl:call-template name="render-doi"/>
    	<br/>
	<xsl:if test="boolean(bmc:note)">
		<br/><br/><xsl:apply-templates select="bmc:note" mode="tig"/>
	</xsl:if>
		
</xsl:template>
	
<xsl:template name="render-tig-emails">
	<br /><xsl:if test="bmc:aug/bmc:au/bmc:email">Email: <xsl:call-template name="render-tig-emails-authors" /></xsl:if><br />

</xsl:template>
	
<xsl:template name="render-tig-emails-authors">
	<xsl:for-each select="bmc:aug/bmc:au/bmc:email">
		<xsl:if test="../bmc:fnm"><xsl:value-of select="../bmc:fnm"/></xsl:if>
		<xsl:if test="../bmc:mi">&#160;<xsl:value-of select="../bmc:mi"/></xsl:if>
		<xsl:if test="../bmc:mnm">&#160;<xsl:value-of select="../bmc:mnm"/></xsl:if>
		<xsl:if test="../bmc:snm">&#160;<xsl:value-of select="../bmc:snm"/></xsl:if>
		<xsl:if test="../bmc:cnm"><xsl:value-of select="../bmc:cnm"/></xsl:if>
		<xsl:if test="../@ca='yes'">*</xsl:if>

		<xsl:text>&#160;-&#160;</xsl:text><xsl:value-of select="."/>
		<xsl:if test="not(position()=last())">; </xsl:if>
	</xsl:for-each>
</xsl:template>
	
<xsl:template name="render-doi">
	<xsl:for-each select="bmc:xrefbib//bmc:pubid[@idtype='doi']">&#160;&#160;&#160;&#160;&#160;doi:<xsl:value-of select="."/></xsl:for-each>
</xsl:template>
	
<xsl:template match="dochead"   mode="tig">

	<h4><xsl:value-of select="."/></h4>
</xsl:template>

<xsl:template match="bmc:mi|bmc:mnm|bmc:snm|bmc:suf" mode="tig"><xsl:text> </xsl:text><xsl:value-of select="."/></xsl:template>
<xsl:template match="bmc:inpress"    mode="tig">, <i>in press</i><xsl:text> </xsl:text></xsl:template>
<xsl:template match="bmc:pubdate"    mode="tig"><xsl:value-of select="."/>, </xsl:template>

<xsl:template match="bmc:source" mode="tig">
	<i><xsl:value-of select="."/></i><xsl:text> </xsl:text>

</xsl:template>

<xsl:template match="bmc:title" mode="tig">
	<h1><xsl:apply-templates select="bmc:p"/></h1><br/>
</xsl:template>

<xsl:template match="bmc:note" mode="tig">
	<br/>
		<xsl:apply-templates select="."/>
	<br/>
</xsl:template>

<xsl:template match="bmc:url" mode="tig">

	<br/><xsl:apply-templates select="."/><br/>
</xsl:template>

<xsl:template match="bmc:aug" mode="tig">
		
	<xsl:for-each select="bmc:au">
		<xsl:variable name="aupos"><xsl:value-of select="position()"></xsl:value-of></xsl:variable>
		<xsl:choose>
			<xsl:when test="position()=last() and not(position()=1) and not (@type='on_behalf') and not(../bmc:etal)"> and </xsl:when>
			<xsl:when test="not(position()=1)">

				<xsl:choose>
					<xsl:when test="@type = 'on_behalf'"> for </xsl:when>
					<xsl:when test="(../bmc:au[$aupos + 1]/@type = 'on_behalf')"> and </xsl:when>
					<xsl:otherwise>, </xsl:otherwise>
				</xsl:choose>
			</xsl:when>

			<xsl:otherwise></xsl:otherwise>
		</xsl:choose>
		<b>
		<xsl:choose>
			<xsl:when test="bmc:cnm"><xsl:value-of select="bmc:cnm" /></xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="bmc:fnm"/>
				<xsl:apply-templates select="bmc:mi"  mode="tig"/>
				<xsl:apply-templates select="bmc:mnm" mode="tig"/>

				<xsl:apply-templates select="bmc:snm" mode="tig"/>
				<xsl:apply-templates select="bmc:suf" mode="tig"/>
			</xsl:otherwise>
		</xsl:choose>
		</b>
		<xsl:if test="@ca='yes'">* </xsl:if>
		<xsl:if test="@ce='yes'"><sup>&#8224; </sup></xsl:if>
		<xsl:if test="@pa='yes'"><sup>&#8224; </sup></xsl:if>

		<xsl:if test="count(../../bmc:insg/bmc:ins) > 1">
			<xsl:for-each select="bmc:insr">
				<sup><xsl:if test="not(position()=1)">, </xsl:if><xsl:value-of select="substring(@iid, 2)"/></sup>
			</xsl:for-each>
		</xsl:if>
	</xsl:for-each>
			
	<xsl:for-each select="etal">, <b><i>et al</i>.</b></xsl:for-each>

	<br/>
</xsl:template>

<xsl:template match="bmc:history" mode="tig">
	<br/>
	<table>
	<xsl:for-each select="bmc:rec">
		<xsl:call-template name="history-row"><xsl:with-param name="title">Received</xsl:with-param></xsl:call-template>
	</xsl:for-each>
	<xsl:for-each select="revreq">

		<xsl:call-template name="history-row"><xsl:with-param name="title">Revisions requested</xsl:with-param></xsl:call-template>
	</xsl:for-each>
	<xsl:for-each select="revrec">
		<xsl:call-template name="history-row"><xsl:with-param name="title">Revisions received</xsl:with-param></xsl:call-template>
	</xsl:for-each>
	<xsl:for-each select="acc">
		<xsl:call-template name="history-row"><xsl:with-param name="title">Accepted</xsl:with-param></xsl:call-template>

	</xsl:for-each>
	<xsl:for-each select="pub">
		<xsl:call-template name="history-row"><xsl:with-param name="title">Published</xsl:with-param></xsl:call-template>
	</xsl:for-each>
	</table>
</xsl:template>

<xsl:template name="history-row">
	<xsl:param name="title"></xsl:param>
	<tr>

		<td><b><xsl:value-of select="$title"/></b></td>
		<td width="25">&#160;</td>
		<td><xsl:apply-templates mode="full" /></td>
	</tr>
</xsl:template>

<xsl:template match="bmc:cpyrt" mode="tig">
	<br/>
	<xsl:if test="bmc:year">&#169;<xsl:text> </xsl:text><xsl:value-of select="bmc:year"/><xsl:text> </xsl:text></xsl:if>

	<xsl:if test="bmc:collab"><xsl:apply-templates select="bmc:collab"/><xsl:text> </xsl:text></xsl:if>
	<xsl:apply-templates select="bmc:note"/>
</xsl:template>


<xsl:template match="bmc:kwdg" mode="tig">
	<b>Keywords: </b>
	<xsl:for-each select="bmc:kwd">
		<xsl:if test="not(position()=1)">, </xsl:if><xsl:apply-templates select="."/>

	</xsl:for-each><br />
</xsl:template>
	
<xsl:template match="bmc:insg" mode="tig">
	<xsl:for-each select="./bmc:ins">
		<xsl:if test="last() > 1">
			<sup><xsl:value-of select="substring(@id, 2)"/></sup>
		</xsl:if>
		<xsl:apply-templates select="bmc:p"/>
	</xsl:for-each>

	<br/>
</xsl:template>
	
<!-- procedures for rendering Abstract and Main Body -->	
	
<xsl:template match="bmc:sec">
	<xsl:call-template name="open-section" />
	<br />
	<xsl:call-template name="iterate-section" /> 
	<p align="right"><a href="#top">Return to top</a></p>
	<br />
</xsl:template>

<xsl:template name="open-section">
	<xsl:param name="heading"></xsl:param>
	<tr>
		<td bgcolor="#ffffff" valign="top">
			<a name="{generate-id()}"><h2>
			<xsl:choose>
				<xsl:when test="$heading"><xsl:copy-of select="$heading"/></xsl:when>
				<xsl:otherwise><xsl:apply-templates select="bmc:st/bmc:p" /></xsl:otherwise>
			</xsl:choose>

			</h2></a>
		</td>
	</tr>
</xsl:template>

<xsl:template name="iterate-section"><!-- runs through subsections -->
	<xsl:param name="nested">no</xsl:param>
	<xsl:for-each select="bmc:sec | bmc:p | bmc:graphic | bmc:bibl | bmc:fig | bmc:tbl | bmc:suppl">
		<xsl:choose>
			<xsl:when test="local-name()='sec'">

				<xsl:call-template name="render-subsection"><xsl:with-param name="nested"><xsl:value-of select="$nested" /></xsl:with-param></xsl:call-template>
			</xsl:when>
			<xsl:when test="local-name()='graphic'">
				<xsl:apply-templates select="."/>
			</xsl:when>
			<xsl:when test="local-name()='bibl'">
				<xsl:apply-templates select="." mode="bdy"/>
			</xsl:when>
			<xsl:when test="local-name()='fig'">

				<xsl:call-template name="render-fig"/>
			</xsl:when>
			<xsl:when test="local-name()='tbl'">
				<xsl:apply-templates select="."/>
			</xsl:when>
			<xsl:when test="local-name()='suppl'">
				<xsl:apply-templates select="."/>
			</xsl:when>
			<xsl:otherwise>

				<p><xsl:apply-templates /></p>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:for-each>
</xsl:template>

<xsl:template name="render-subsection">
	<xsl:param name="nested">no</xsl:param>
	<xsl:choose>
		<xsl:when test="$nested='no'">

			<p><h3><xsl:apply-templates select="bmc:st/bmc:p"/></h3></p>
		</xsl:when>
		<xsl:otherwise>
			<p>
			<xsl:choose>
				<xsl:when test="local-name(../../../..)='sec'"><i><xsl:apply-templates select="bmc:st/bmc:p"/></i></xsl:when>
				<xsl:when test="local-name(../../..)='sec'"><h4><i><xsl:apply-templates select="bmc:st/bmc:p"/></i></h4></xsl:when>
				<xsl:otherwise><h4><xsl:apply-templates select="bmc:st/bmc:p"/></h4></xsl:otherwise>
			</xsl:choose>

			</p>
		</xsl:otherwise>
	</xsl:choose>
	<xsl:call-template name="iterate-section">
		<xsl:with-param name="nested">yes</xsl:with-param>
	</xsl:call-template>
</xsl:template>


<!-- prodcedure for rendering article map -->

<xsl:template name="article-map">
	<xsl:for-each select="../bmc:bdy/bmc:sec | ../bmc:bdy//bmc:fig | ../bmc:bdy//bmc:tbl | ../bmc:bdy//bmc:suppl">
	<xsl:choose>
		<xsl:when test="local-name()='sec'"><a href="#{generate-id()}"><xsl:apply-templates select="bmc:st/bmc:p"/></a></xsl:when>
		<xsl:when test="local-name()='fig'"><a href="#{generate-id()}"><xsl:apply-templates select="bmc:title/bmc:p"/></a></xsl:when>
		<xsl:when test="local-name()='tbl'"><a href="#{generate-id()}"><xsl:apply-templates select="bmc:title/bmc:p"/></a></xsl:when>
		<xsl:when test="local-name()='suppl'"><a href="#{generate-id()}"><xsl:apply-templates select="bmc:title/bmc:p"/></a></xsl:when>
	</xsl:choose>
	&#160;&#160;</xsl:for-each>

	<xsl:for-each select="//bmc:refgrp"><a href="#{generate-id()}">References</a></xsl:for-each>
</xsl:template>

<!-- procedure for rendering references -->

<xsl:template match="bmc:refgrp">
	<xsl:call-template name="open-section">
		<xsl:with-param name="heading">References</xsl:with-param>
	</xsl:call-template>
		
	<br />

	<xsl:choose>
		<xsl:when test="bmc:bibl[@rating='1']"><xsl:call-template name="render-ratings" /></xsl:when>
		<xsl:when test="bmc:bibl[@rating='2']"><xsl:call-template name="render-ratings" /></xsl:when>
	</xsl:choose>
	<table width="100%" cellpadding="0" cellspacing="0" border="0">	
		<xsl:apply-templates mode="ref"/>
	</table>
		
</xsl:template>

<xsl:template name="bibl-numbering">

	<xsl:number value="position()" format="1."/>
</xsl:template>

<xsl:template match="bmc:bibl" mode="ref">
	<tr>
		<td valign="top" align="right">
			<xsl:call-template name="bibl-numbering" />
		</td>
		<td width="5">&#160;</td>
		
		<td valign="top" colspan="2">

			<a name="{@id}" />
			<xsl:if test="child::bmc:note = child::node()[1]"><xsl:apply-templates select="bmc:note" /><br /></xsl:if>
			<xsl:call-template name="bibl-body"/>
		</td>
	</tr>
	<tr>
		<td valign="top" align="right">
			<xsl:choose>
				<xsl:when test="@rating='1'">&#8226;&#160;</xsl:when>

				<xsl:when test="@rating='2'">&#8226;&#8226;&#160;</xsl:when>
				<xsl:otherwise>&#160;</xsl:otherwise>
			</xsl:choose>
		</td>
		<td width="5">&#160;</td>
		<td valign="top" colspan="2">
			<xsl:if test="bmc:note[1] and not(child::bmc:note = child::node()[1])">
				<xsl:apply-templates select="bmc:note" /><br />
			</xsl:if>

			<xsl:for-each select="//bmc:abbr[@bid = current()/@id]">
				<xsl:call-template name="references-output-citation-return-link">
					<xsl:with-param name="pos" select="position()" />
				</xsl:call-template>
			</xsl:for-each>
		</td>
	</tr>
	<tr><td colspan="3">&#160;</td></tr>
</xsl:template>

<xsl:template name="bibl-body">
	<xsl:apply-templates select="bmc:aug"   mode="ref"/>
	<xsl:apply-templates select="bmc:insg"  mode="ref"/>
	<xsl:if test="not(bmc:aug)"><xsl:apply-templates select="bmc:editor" mode="book"/></xsl:if>
	<xsl:choose>
		<xsl:when test="bmc:publisher and not(bmc:url) and not(bmc:source)"><xsl:apply-templates select="bmc:title" mode="book"/></xsl:when>
		<xsl:otherwise><xsl:apply-templates select="bmc:title" mode="ref"/></xsl:otherwise>
	</xsl:choose>
	<xsl:if test="bmc:source[1] | bmc:edition[1] | bmc:editor[1] | bmc:publisher[1] | bmc:pubdate[1] | bmc:inpress[1] | bmc:volume[1] | bmc:issue[1] | bmc:fpage[1] | bmc:lpage[1] | bmc:xrefbib[1] | bmc:url[1]">

		<xsl:if test="bmc:title"><br /></xsl:if>
		<xsl:apply-templates select="bmc:url"   mode="ref"/>
		<xsl:choose>
			<xsl:when test="bmc:publisher and not(bmc:url)"><!-- if we have a publisher but not a url then we assume reference is to a book -->
				<xsl:choose>
					<xsl:when test="bmc:title and not(bmc:source = 'PhD thesis')"><xsl:apply-templates select="bmc:source"  mode="book"/></xsl:when>
					<xsl:otherwise><xsl:apply-templates select="bmc:source"  mode="ref"/></xsl:otherwise>
				</xsl:choose>
				<xsl:apply-templates select="bmc:volume" mode="book"/>

				<xsl:apply-templates select="bmc:edition" mode="ref"/>
				<xsl:if test="bmc:aug"><xsl:apply-templates select="bmc:editor" mode="ref"/></xsl:if>
				<xsl:apply-templates select="bmc:publisher" mode="ref"/>
				<xsl:apply-templates select="bmc:pubdate" mode="book"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="bmc:source"  mode="ref"/>
				<xsl:apply-templates select="bmc:edition" mode="ref"/>
				<xsl:if test="bmc:aug"><xsl:apply-templates select="bmc:editor" mode="ref"/></xsl:if>

				<xsl:apply-templates select="bmc:publisher" mode="ref"/>
				<xsl:apply-templates select="bmc:pubdate" mode="ref"/>
				<xsl:if test="(bmc:volume[1] | bmc:issue[1] | bmc:fpage[1] | bmc:lpage[1]) and bmc:pubdate[1]">,<xsl:text> </xsl:text></xsl:if>
				<xsl:if test="bmc:volume[1] | bmc:issue[1]">
					<xsl:text> </xsl:text><b><xsl:value-of select="bmc:volume"/></b><xsl:if test="bmc:issue">(<xsl:value-of select="bmc:issue"/>)</xsl:if><b>:</b>
				</xsl:if>

			</xsl:otherwise>
		</xsl:choose>
					
		<xsl:apply-templates select="bmc:inpress" mode="ref"/>
		<xsl:if test="bmc:fpage[1] | bmc:lpage[1]">
			<xsl:apply-templates select="bmc:fpage" mode="ref"/><xsl:apply-templates select="bmc:lpage" mode="ref"/><xsl:text>. </xsl:text>
		</xsl:if>
						
		<xsl:apply-templates select="bmc:xrefbib" mode="ref"/>
		<br />

	</xsl:if>
</xsl:template>

<xsl:template match="bmc:mi|bmc:snm|bmc:suf" mode="ref"><xsl:text> </xsl:text><xsl:value-of select="."/></xsl:template>
<xsl:template match="bmc:source"     mode="ref">
	<i><xsl:value-of select="."/></i>
	<xsl:if test="not(../bmc:title) or (.='PhD thesis')">.</xsl:if>
	<xsl:if test="local-name(following-sibling::node())='publisher'"><xsl:text> </xsl:text></xsl:if>
</xsl:template>
<xsl:template match="bmc:source" mode="book">In <i>

	<xsl:choose>
		<xsl:when test="(substring(.,1,3)='In:') or (substring(.,1,3)='in:')"><xsl:value-of select="substring(.,4)"/></xsl:when>
		<xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
	</xsl:choose></i>.<xsl:if test="local-name(following-sibling::node())='publisher'"><xsl:text> </xsl:text></xsl:if>
</xsl:template>
<xsl:template match="bmc:volume"    mode="book"><i><xsl:text> Volume </xsl:text><xsl:value-of select="."/></i>.</xsl:template>
<xsl:template match="bmc:edition"    mode="ref">
	<xsl:text> </xsl:text><xsl:value-of select="."/><xsl:call-template name="render-number-suffix"/> edition<xsl:text>. </xsl:text>

</xsl:template>
<xsl:template match="bmc:editor"     mode="ref"><xsl:text>Edited by: </xsl:text><xsl:value-of select="."/><xsl:text>. </xsl:text></xsl:template>
<xsl:template match="bmc:editor"     mode="book"><xsl:value-of select="."/><xsl:text> (Ed</xsl:text><xsl:if test="contains(.,',') or contains(.,' and ')">s</xsl:if><xsl:text>): </xsl:text></xsl:template>
<xsl:template match="bmc:publisher"  mode="ref"><xsl:value-of select="."/><xsl:text>; </xsl:text></xsl:template>
<xsl:template match="bmc:inpress"    mode="ref">, in press<xsl:text>. </xsl:text></xsl:template>
<xsl:template match="bmc:pubdate"    mode="ref">
	<xsl:text> </xsl:text><xsl:value-of select="."/><xsl:if test="not(../bmc:fpage or ../bmc:lpage or ../bmc:inpress)">.</xsl:if>

</xsl:template>
<xsl:template match="bmc:pubdate"    mode="book">
	<xsl:text> </xsl:text><xsl:value-of select="."/>
	<xsl:choose>
		<xsl:when test="(../bmc:fpage or ../bmc:lpage or ../bmc:inpress)">:</xsl:when>
		<xsl:otherwise>.</xsl:otherwise>
	</xsl:choose>
</xsl:template>
<xsl:template match="bmc:title"      mode="ref">

	<b><xsl:apply-templates select="bmc:p"/>	
	<xsl:variable name="lastchar" select="substring(normalize-space(.), string-length(normalize-space(.)), 1)"/>
	<xsl:choose><xsl:when test="($lastchar = '.') or ($lastchar = '!') or ($lastchar = '?') or not (../bmc:aug)"></xsl:when>
<xsl:otherwise>.</xsl:otherwise></xsl:choose></b>
</xsl:template>
<xsl:template match="bmc:title"  mode="book">
	<i>
	<xsl:apply-templates select="bmc:p"/>	
	<xsl:variable name="lastchar" select="substring(normalize-space(.), string-length(normalize-space(.)), 1)"/>
	<xsl:choose>
		<xsl:when test="($lastchar = '.') or ($lastchar = '!') or ($lastchar = '?') or not (../bmc:aug)"></xsl:when>

		<xsl:otherwise>.</xsl:otherwise>
	</xsl:choose>
	</i>
</xsl:template>

<xsl:template match="bmc:url" mode="ref"> [<a class="hiddenlink" target="_blank" href="{.}"><xsl:value-of select="."/></a>]</xsl:template>

<xsl:template match="bmc:insg" mode="ref">
	<xsl:for-each select="./bmc:ins">

		<xsl:if test="last() > 1">
			<sup><xsl:value-of select="substring(@id, 2)"/></sup>
		</xsl:if>
		<xsl:apply-templates select="bmc:p"/>
	</xsl:for-each>: 
</xsl:template>

<xsl:template match="bmc:aug" mode="ref">
	<xsl:for-each select="bmc:au">
		<xsl:if test="not(position()=1)">

			<xsl:choose>
				<xsl:when test="@type='on_behalf'"> for </xsl:when>
				<xsl:otherwise>, </xsl:otherwise>
			</xsl:choose>
		</xsl:if>
		<xsl:choose>
			<xsl:when test="count(bmc:fnm)>0">

				<xsl:apply-templates select="bmc:snm" mode="ref"/>
				<xsl:apply-templates select="bmc:suf" mode="ref"/>
				<xsl:text> </xsl:text>
				<xsl:value-of select="translate(bmc:fnm,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','ABCDEFGHIJKLMNOPQRSTUVWXYZ')" />
				<xsl:value-of select="bmc:mi" />
				<xsl:for-each select="bmc:insr">
					<sup><xsl:if test="not(position()=1)">, </xsl:if><xsl:value-of select="substring(@iid, 2)"/></sup>
				</xsl:for-each>

			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="bmc:cnm"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:for-each>
	<xsl:for-each select="bmc:etal">, <i>et al</i>.</xsl:for-each>:

</xsl:template>

<xsl:template match="bmc:xrefbib" mode="ref">
	<xsl:if test="count(.//bmc:pubid[@idtype='doi'])=1">
		<xsl:choose>
		<!-- 
			Check whether this doi is a bmc one
			If it is, still go via dx.doi.org but use different text
		-->
			<xsl:when test="substring(.//bmc:pubid[@idtype='doi'],1,8)='10.1186/'">
				[<a class="hiddenlink" target="_blank" href="http://dx.doi.org/{.//bmc:pubid[@idtype='doi']}">BioMed Central Full Text</a>]
			</xsl:when>
			<xsl:otherwise>

				<!-- 
					if we have a full text link via PubMed then we ignore the DOI
					and use the PubMed id which is output below.
				-->
				<xsl:if test="count(.//bmc:pubid[@link='fulltext'])=0">
					[<a class="hiddenlink" target="_blank" href="http://dx.doi.org/{.//bmc:pubid[@idtype='doi']}">Publisher Full Text</a>]
				</xsl:if>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:if>
		
	<!-- if PubMed has a full text link then display that 
	 	 but only if it is not a BMC article
	-->

	<xsl:if test="count(.//bmc:pubid[substring(..//bmc:pubid[@idtype='doi'],1,8)='10.1186/'])=0">
		<xsl:if test=".//bmc:pubid[@link='fulltext']">
			<xsl:choose>
				<xsl:when test="count(.//bmc:pubid[@idtype='pmpid'])=1">[<a class="hiddenlink" target="_blank" href="http://www.ncbi.nlm.nih.gov/entrez/eutils/elink.fcgi?dbfrom=pubmed&amp;cmd=prlinks&amp;retmode=ref&amp;id={.//bmc:pubid[@idtype='pmpid']}">Publisher Full Text</a>]</xsl:when>
				<xsl:when test="count(.//bmc:pubid[@idtype='pubmed'])=1">[<a class="hiddenlink" target="_blank" href="http://www.ncbi.nlm.nih.gov/entrez/eutils/elink.fcgi?dbfrom=pubmed&amp;cmd=prlinks&amp;retmode=ref&amp;id={.//bmc:pubid[@idtype='pubmed']}">Publisher Full Text</a>]</xsl:when>
				<xsl:when test="count(.//bmc:pubid[not(@idtype)])=1">[<a class="hiddenlink" target="_blank" href="http://www.ncbi.nlm.nih.gov/entrez/eutils/elink.fcgi?dbfrom=pubmed&amp;cmd=prlinks&amp;retmode=ref&amp;id={.//bmc:pubid[not(@idtype)]}">Publisher Full Text</a>]</xsl:when>

				<xsl:otherwise />
			</xsl:choose>
		</xsl:if>
	</xsl:if>
	<!-- 
		and finally a PubMed Central full text link
	-->
	<xsl:if test="count(.//bmc:pubid[@idtype='pmcid'])=1">
		<xsl:choose>
			<xsl:when test="count(.//bmc:pubid[@idtype='pmpid'])=1">[<a class="hiddenlink" target="_blank" href="http://www.pubmedcentral.nih.gov/articlerender.fcgi?tool=pubmed&amp;pubmedid={.//bmc:pubid[@idtype='pmpid']}">PubMed Central Full Text</a>]</xsl:when>

			<xsl:when test="count(.//bmc:pubid[@idtype='pubmed'])=1">[<a class="hiddenlink" target="_blank" href="http://www.pubmedcentral.nih.gov/articlerender.fcgi?tool=pubmed&amp;pubmedid={.//bmc:pubid[@idtype='pubmed']}">PubMed Central Full Text</a>]</xsl:when>
			<xsl:when test="count(.//bmc:pubid[not(@idtype)])=1">[<a class="hiddenlink" target="_blank" href="http://www.pubmedcentral.nih.gov/articlerender.fcgi?tool=pubmed&amp;pubmedid={.//bmc:pubid[not(@idtype)]}">PubMed Central Full Text</a>]</xsl:when>
			<xsl:otherwise />
		</xsl:choose>
	</xsl:if>
		

</xsl:template>

<xsl:template match="bmc:fpage" mode="ref"><xsl:value-of select="." /></xsl:template>
<xsl:template match="bmc:lpage" mode="ref">-<xsl:value-of select="." /></xsl:template>

<xsl:template name="render-ratings">
	Papers of particular interest have been highlighted as:<br/>
	&#8226; of special interest<br/>
	&#8226;&#8226; of outstanding interest<br/><br/>

</xsl:template>
	
	

<xsl:template name="references-output-citation-return-link">
	<xsl:param name="pos" />
	<xsl:if test="$pos=1"><xsl:text>Return to citation in text: </xsl:text></xsl:if>
	 [<a class="hiddenlink" href="#{generate-id()}"><xsl:value-of select="$pos" /></a>]
</xsl:template>

<xsl:template name="render-number-suffix">
	<xsl:choose>
		<xsl:when test="(number(.) &gt;1000) or not(number(.)=.)"></xsl:when>

		<xsl:when test="(substring(., string-length(.) - 1)='11') or (substring(., string-length(.) - 1)='12') or(substring(., string-length(.) - 1)='13')">th</xsl:when>
		<xsl:when test="(substring(., string-length(.))='1')">st</xsl:when>
		<xsl:when test="(substring(., string-length(.))='2')">nd</xsl:when>
		<xsl:when test="(substring(., string-length(.))='3')">rd</xsl:when>
		<xsl:otherwise>th</xsl:otherwise>
	</xsl:choose>

</xsl:template>

<!-- procedure for rendering figures -->

<xsl:template name="render-fig">
	<table>
		<tr>
			<td colspan="2"><hr /></td>
		</tr>
	</table>
	<table>

		<tr>
			<td nowrap="y" align="center">
				<a name="{generate-id()}" >
				<xsl:for-each select="bmc:graphic">
					<img border="0" src="{$base-url}{$image-url}{@file}.jpg" />
				</xsl:for-each>
				</a>
			</td>
		</tr>

		<xsl:for-each select="bmc:text">
		<tr>
			<td>
				<xsl:for-each select="../bmc:title"><b><xsl:apply-templates select="bmc:p" /></b></xsl:for-each>
				<xsl:for-each select="bmc:p">
					<xsl:if test="not(position()=1)"><br/></xsl:if>
					<hr />
					<xsl:apply-templates/>
					<hr />

				</xsl:for-each>
			</td>
		</tr>
		</xsl:for-each>
	</table>
	<p align="right"><a href="#top">Return to top</a></p>
</xsl:template>

<!-- procedure for rendering tables -->

<xsl:template match="bmc:tbl">
	<table width="100%">
		<tr>
			<td>
				<a name="{generate-id()}" ><xsl:for-each select="bmc:title"><b><xsl:apply-templates select="bmc:p" /></b></xsl:for-each></a><br/><hr/>
			</td>
		</tr>
		<tr>
			<td>

				<xsl:for-each select="bmc:caption"><b><xsl:apply-templates select="bmc:p" /></b></xsl:for-each><br/><hr/>
			</td>
		</tr>
		<tr>
			<td>
				<xsl:apply-templates select="bmc:tblbdy" />
				<hr/>
			</td>
		</tr>

		<xsl:for-each select="bmc:tblfn">
			<tr>
				<td>
					<xsl:for-each select="bmc:p"><xsl:apply-templates select="." /><xsl:if test="not(position()=last())"><br/></xsl:if></xsl:for-each>
					<hr/>
				</td>
			</tr>
		</xsl:for-each>
	</table>

	<p align="right"><a href="#top">Return to top</a></p>
</xsl:template>

<xsl:template match="bmc:tblbdy">
	<table width="100%">
		<xsl:for-each select="bmc:r">
			<tr valign="{@ra}">
				<xsl:for-each select="bmc:c">
					<td>
						<xsl:if test="@cspan"><xsl:attribute name="colspan"><xsl:value-of select="@cspan" /></xsl:attribute></xsl:if>

						<xsl:if test="@rspan"><xsl:attribute name="rowspan"><xsl:value-of select="@rspan" /></xsl:attribute></xsl:if>
						<xsl:if test="@ca"><xsl:attribute name="align"><xsl:value-of select="@ca" /></xsl:attribute></xsl:if>
						<xsl:if test="@width"><xsl:attribute name="width"><xsl:value-of select="@width" /></xsl:attribute></xsl:if>
						<xsl:choose>
							<xsl:when test="@indent">
								<xsl:for-each select = "bmc:p">
									<table cellpadding="0" cellspacing="0">
										<tr><td><xsl:choose>
											<xsl:when test="../@indent[.='1']">&#160;&#160;&#160;&#160;&#160;</xsl:when>

											<xsl:when test="../@indent[.='2']">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</xsl:when>						
											<xsl:when test="../@indent[.='3']">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</xsl:when>
										</xsl:choose></td><td>
											<xsl:apply-templates select="." />
										</td></tr>
									</table>
								</xsl:for-each>
								<xsl:apply-templates select="bmc:hr" />
									
							</xsl:when>

							<xsl:otherwise>
								<xsl:for-each select= "bmc:p"><xsl:apply-templates select="." /><br /></xsl:for-each>
								<xsl:apply-templates select="bmc:hr" />
							</xsl:otherwise>
						</xsl:choose>
					</td>
				</xsl:for-each>
			</tr>
		</xsl:for-each>

	</table>
</xsl:template>

<!-- procedure for rendering supplementary files -->

<xsl:template match="bmc:suppl">
	<table width="100%">
		<tr>
			<td>
				<hr /><a name="{generate-id()}" ><xsl:for-each select="bmc:title"><b><xsl:apply-templates select="bmc:p" /></b></xsl:for-each></a><br/><hr/>
			</td>

		</tr>
		<tr><td><xsl:apply-templates select="bmc:text/bmc:p"/></td></tr>
		<tr><td><a href="{$base-url}{$suppl-url}{bmc:file/@name}"><xsl:apply-templates select="bmc:file/bmc:p"/></a><hr /></td></tr>
	</table>
</xsl:template>
	
<!-- common functions -->

<xsl:template match="bmc:p"><xsl:apply-templates /></xsl:template>
<xsl:template match="bmc:b"><b><xsl:apply-templates /></b></xsl:template>
<xsl:template match="bmc:it"><i><xsl:apply-templates /></i></xsl:template>
<xsl:template match="bmc:sub"><sub><xsl:apply-templates /></sub></xsl:template>
<xsl:template match="bmc:sup"><sup><xsl:apply-templates /></sup></xsl:template>

<xsl:template match="bmc:monospace"><font class="monospace"><xsl:apply-templates /></font></xsl:template>
<xsl:template match="bmc:a"><a><xsl:attribute name="href"><xsl:value-of select="@href" /></xsl:attribute><xsl:value-of select="." /></a></xsl:template>
<xsl:template match="bmc:url"><a><xsl:attribute name="href"><xsl:value-of select="." /></xsl:attribute><xsl:value-of select="." /></a></xsl:template>
<xsl:template match="bmc:email"><a><xsl:attribute name="href">mailto:<xsl:value-of select="." /></xsl:attribute><xsl:value-of select="." /></a></xsl:template>
<xsl:template match="bmc:ul"><u><xsl:apply-templates /></u></xsl:template>
<xsl:template match="bmc:hr"><hr /></xsl:template>
<xsl:template match="bmc:xrefart">
	<a href="{$base-url}article/id/{@art}"><xsl:value-of select="." /></a>
</xsl:template>
<xsl:template match="bmc:bibl" mode="bdy">
	<p><xsl:call-template name="bibl-body"/></p>
</xsl:template>

<xsl:template match="bmc:graphic">

	<img src="{$base-url}{$inline-url}{@file}" />
</xsl:template>
	
<xsl:template match="text()"><xsl:value-of select="."/></xsl:template>

<xsl:template match="bmc:day|bmc:year" mode="full"><xsl:value-of select="number(.)" /></xsl:template>
<xsl:template match="bmc:day|bmc:year"><xsl:value-of select="number(.)" /></xsl:template>

<xsl:template match="bmc:month" mode="full">
	<xsl:choose>
		<xsl:when test="number(.)=1"><xsl:text>&#160;January&#160;</xsl:text></xsl:when>
		<xsl:when test="number(.)=2"><xsl:text>&#160;February&#160;</xsl:text></xsl:when>

		<xsl:when test="number(.)=3"><xsl:text>&#160;March&#160;</xsl:text></xsl:when>
		<xsl:when test="number(.)=4"><xsl:text>&#160;April&#160;</xsl:text></xsl:when>
		<xsl:when test="number(.)=5"><xsl:text>&#160;May&#160;</xsl:text></xsl:when>
		<xsl:when test="number(.)=6"><xsl:text>&#160;June&#160;</xsl:text></xsl:when>
		<xsl:when test="number(.)=7"><xsl:text>&#160;July&#160;</xsl:text></xsl:when>
		<xsl:when test="number(.)=8"><xsl:text>&#160;August&#160;</xsl:text></xsl:when>

		<xsl:when test="number(.)=9"><xsl:text>&#160;September&#160;</xsl:text></xsl:when>
		<xsl:when test="number(.)=10"><xsl:text>&#160;October&#160;</xsl:text></xsl:when>
		<xsl:when test="number(.)=11"><xsl:text>&#160;November&#160;</xsl:text></xsl:when>
		<xsl:when test="number(.)=12"><xsl:text>&#160;December&#160;</xsl:text></xsl:when>
		<xsl:otherwise><xsl:value-of select="." /></xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template match="bmc:month">
	<xsl:choose>
		<xsl:when test="number(.)=1"><xsl:text>&#160;Jan&#160;</xsl:text></xsl:when>
		<xsl:when test="number(.)=2"><xsl:text>&#160;Feb&#160;</xsl:text></xsl:when>
		<xsl:when test="number(.)=3"><xsl:text>&#160;Mar&#160;</xsl:text></xsl:when>
		<xsl:when test="number(.)=4"><xsl:text>&#160;Apr&#160;</xsl:text></xsl:when>
		<xsl:when test="number(.)=5"><xsl:text>&#160;May&#160;</xsl:text></xsl:when>

		<xsl:when test="number(.)=6"><xsl:text>&#160;Jun&#160;</xsl:text></xsl:when>
		<xsl:when test="number(.)=7"><xsl:text>&#160;Jul&#160;</xsl:text></xsl:when>
		<xsl:when test="number(.)=8"><xsl:text>&#160;Aug&#160;</xsl:text></xsl:when>
		<xsl:when test="number(.)=9"><xsl:text>&#160;Sep&#160;</xsl:text></xsl:when>
		<xsl:when test="number(.)=10"><xsl:text>&#160;Oct&#160;</xsl:text></xsl:when>
		<xsl:when test="number(.)=11"><xsl:text>&#160;Nov&#160;</xsl:text></xsl:when>

		<xsl:when test="number(.)=12"><xsl:text>&#160;Dec&#160;</xsl:text></xsl:when>
		<xsl:otherwise><xsl:value-of select="." /></xsl:otherwise>
	</xsl:choose>
</xsl:template>
	
<xsl:template match="bmc:abbrgrp">
	<xsl:for-each select="bmc:abbr">
		<a name="{generate-id()}" />
	</xsl:for-each>[<xsl:for-each select="bmc:abbr">

		<xsl:choose>
			<xsl:when test="ancestor::bmc:bdy/../bmc:bm/bmc:refgrp/bmc:bibl[@id=current()/@bid]/@rating > 0">
				<xsl:if test="not(position()=1)">,</xsl:if><xsl:call-template name="render-abbr-link"><xsl:with-param name="text"><xsl:call-template name="render-with-bullets"/></xsl:with-param></xsl:call-template>
			</xsl:when>
			<xsl:when test="ancestor::bmc:bdy/../bmc:bm/bmc:refgrp/bmc:bibl[@id=concat('B', current()-1)]/@rating > 0">
				<xsl:if test="not(position()=1)">,</xsl:if><xsl:call-template name="render-abbr-link"><xsl:with-param name="text"><xsl:call-template name="render-with-bullets"/></xsl:with-param></xsl:call-template>
			</xsl:when>
			<xsl:when test="ancestor::bmc:bdy/../bmc:bm/bmc:refgrp/bmc:bibl[@id=concat('B', current()+1)]/@rating > 0">

				<xsl:choose>
					<xsl:when test="preceding-sibling::bmc:abbr[position()=2]=current()-2">-<xsl:call-template name="render-with-bullets"/><xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text></xsl:when>
					<xsl:otherwise><xsl:if test="not(position()=1)"><xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>,</xsl:if><xsl:call-template name="render-abbr-link"><xsl:with-param name="text"><xsl:call-template name="render-with-bullets"/></xsl:with-param></xsl:call-template></xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="preceding-sibling::bmc:abbr=current()-1">
				<xsl:if test="not(following-sibling::bmc:abbr=current()+1)">

					<xsl:choose>
						<xsl:when test="preceding-sibling::bmc:abbr[position()=2]=current()-2">-<xsl:call-template name="render-with-bullets"/><xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text></xsl:when>
						<xsl:otherwise><xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>,<xsl:call-template name="render-abbr-link"><xsl:with-param name="text"><xsl:call-template name="render-with-bullets"/></xsl:with-param></xsl:call-template></xsl:otherwise>
					</xsl:choose>
				</xsl:if>
			</xsl:when>
			<xsl:when test="following-sibling::bmc:abbr=current()+1">

				<xsl:if test="not(position()=1)">,</xsl:if>
				<xsl:text disable-output-escaping="yes">&lt;a href="#</xsl:text><xsl:value-of select="@bid" /><xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
				<xsl:call-template name="render-with-bullets"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:if test="not(position()=1)">,</xsl:if><xsl:call-template name="render-abbr-link"><xsl:with-param name="text"><xsl:call-template name="render-with-bullets"/></xsl:with-param></xsl:call-template>
			</xsl:otherwise>

		</xsl:choose>
	</xsl:for-each>]<!--
	-->
</xsl:template>

<xsl:template name="render-abbr-link">
	<xsl:param name="text" />
	<a onclick=" " href="#{@bid}"><xsl:value-of select="$text" /></a>
</xsl:template>

<xsl:template name="render-with-bullets">
	<xsl:value-of select="."/>

	<xsl:choose>
		<xsl:when test="ancestor::bmc:bdy/../bmc:bm/bmc:refgrp/bmc:bibl[@id=current()/@bid]/@rating = 1">&#8226;</xsl:when>
		<xsl:when test="ancestor::bmc:bdy/../bmc:bm/bmc:refgrp/bmc:bibl[@id=current()/@bid]/@rating = 2">&#8226;&#8226;</xsl:when>
	</xsl:choose>
</xsl:template>

<xsl:template match="bmc:figr">
	<xsl:variable name="pos"><xsl:value-of select="@fid"/></xsl:variable>
	<a href="#{generate-id(ancestor::bmc:art//bmc:fig[@id = $pos])}"><xsl:value-of select="."/></a>
</xsl:template>

<xsl:template match="bmc:tblr">
	<xsl:variable name="pos"><xsl:value-of select="@tid"/></xsl:variable>
	<a href="#{generate-id(ancestor::bmc:art//bmc:tbl[@id = $pos])}"><xsl:value-of select="."/></a>
</xsl:template>

<xsl:template match="bmc:supplr">
	<xsl:variable name="pos"><xsl:value-of select="@sid"/></xsl:variable>
	<a href="#{generate-id(ancestor::bmc:art//bmc:suppl[@id = $pos])}"><xsl:value-of select="."/></a>
</xsl:template>

</xsl:stylesheet>