<!-- Stylesheet to render BMC article.dtd compliant XML files to basic html output -->
<!-- This stylesheet is made available under the Creative Commons Attribution license -->
<!-- http://creativecommons.org/licenses/by/2.0/ -->
<!-- It may be freely reused, adapted and redistributed as long as attribution remains intact -->
<!-- Copyright BioMed Central Limited 2004 -->
<!-- Version 1.02  23rd March 2005 -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"><!---->
	<xsl:output method="html" encoding="utf-8"/>
	<!-- Paths for associated files-->
	<!-- By default, files will be retrieved from BioMed Central server-->
	<!-- If you maintain local copies of image files etc, then substitute as appropriate-->
	<xsl:variable name="base-url">http://www.biomedcentral.com/</xsl:variable>
	<xsl:variable name="image-url">content/figures/</xsl:variable>
	<!-- for graphics within fig tag -->
	<xsl:variable name="inline-url">content/inline/</xsl:variable>
	<!-- for graphics with paragraph tags (inline images) -->
	<xsl:variable name="suppl-url">content/supplementary/</xsl:variable>
	<!-- for graphics within fig tag -->
	<xsl:template match="/">
		<html>
			<head>
				<style type="text/css">
					<!-- Basic style sheet to give sans-serif output -->
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
				<xsl:for-each select="art">
					<xsl:for-each select="fm">
						<xsl:call-template name="render-tig"/>
						<!-- this renders the article information -->
						<xsl:call-template name="article-map"/>
						<!-- links to article sections, figures and tables -->
						<xsl:apply-templates select="abs"/>
						<!-- renders the article abstract -->
					</xsl:for-each>
					<xsl:apply-templates select="bdy"/>
					<!-- renders the main body -->
					<xsl:for-each select="bm">
						<xsl:apply-templates select="ack"/>
						<!-- Acknowledgements -->
						<xsl:apply-templates select="refgrp"/>
						<!-- References -->
						<xsl:apply-templates select="sec"/>
						<!-- other matter -->
					</xsl:for-each>
				</xsl:for-each>
			</body>
		</html>
	</xsl:template>
	<!-- procedures for rendering the article information -->
	<xsl:template name="render-tig">
	    <xsl:apply-templates select="dochead"/>
		<xsl:call-template name="render-tig-bibl"/>
		<xsl:apply-templates select="bibl/url" />
		<xsl:apply-templates select="history" />
		<xsl:apply-templates select="cpyrt" />
		<hr/>
		<xsl:apply-templates select="kwdg" />
	</xsl:template>
	<xsl:template name="render-tig-bibl">
		<xsl:for-each select="bibl">
			<xsl:apply-templates select="title" />
			<xsl:apply-templates select="aug" />
			<xsl:call-template name="render-tig-bibl-details"/>
		</xsl:for-each>
	</xsl:template>
	<xsl:template name="render-tig-bibl-details">
		<xsl:apply-templates select="insg" />
		<xsl:call-template name="render-tig-emails"/>
		<xsl:if test="(aug/au[@ce = 'yes']) or (aug/au[@ca = 'yes'])">
			<xsl:if test="aug/au[@ca='yes']">* Corresponding author&#160;&#160;</xsl:if>
			<xsl:if test="aug/au[@ce='yes']">
				<sup>&#8224;</sup> Contributed equally&#160;&#160;</xsl:if>
			<br/>
			<br/>
		</xsl:if>
		<xsl:apply-templates select="source" />
		<xsl:apply-templates select="pubdate" />
		<xsl:apply-templates select="inpress" />
		<xsl:if test="volume[1]">
			<xsl:text> </xsl:text>
			<b>
				<xsl:value-of select="volume"/>
			</b>
			<xsl:if test="issue">(<xsl:value-of select="issue"/>)</xsl:if>
			<b>:</b>
		</xsl:if>
		<xsl:if test="fpage[1]">
			<xsl:value-of select="fpage"/>
			<xsl:if test="lpage[1] and fpage[1] != lpage[1]">-<xsl:value-of select="lpage"/>
			</xsl:if>
		</xsl:if>
		<xsl:call-template name="render-doi"/>
		<br/>
		<xsl:if test="boolean(note)">
			<br/>
			<br/>
			<xsl:apply-templates select="note" />
		</xsl:if>
	</xsl:template>
	<xsl:template name="render-tig-emails">
		<br/>
		<xsl:if test="aug/au/email">Email: <xsl:call-template name="render-tig-emails-authors"/>
		</xsl:if>
		<br/>
	</xsl:template>
	<xsl:template name="render-tig-emails-authors">
		<xsl:for-each select="aug/au/email">
			<xsl:if test="../fnm">
				<xsl:value-of select="../fnm"/>
			</xsl:if>
			<xsl:if test="../mi">&#160;<xsl:value-of select="../mi"/>
			</xsl:if>
			<xsl:if test="../mnm">&#160;<xsl:value-of select="../mnm"/>
			</xsl:if>
			<xsl:if test="../snm">&#160;<xsl:value-of select="../snm"/>
			</xsl:if>
			<xsl:if test="../cnm">
				<xsl:value-of select="../cnm"/>
			</xsl:if>
			<xsl:if test="../@ca='yes'">*</xsl:if>
			<xsl:text>&#160;-&#160;</xsl:text>
			<xsl:value-of select="."/>
			<xsl:if test="not(position()=last())">; </xsl:if>
		</xsl:for-each>
	</xsl:template>
	<xsl:template name="render-doi">
		<xsl:for-each select="xrefbib//pubid[@idtype='doi']">&#160;&#160;&#160;&#160;&#160;doi:<xsl:value-of select="."/>
		</xsl:for-each>
	</xsl:template>
	<xsl:template match="dochead">
		<h4>
			<xsl:value-of select="."/>
		</h4>
	</xsl:template>
	<xsl:template match="mi|mnm|snm|suf" >
		<xsl:text> </xsl:text>
		<xsl:value-of select="."/>
	</xsl:template>
	<xsl:template match="inpress" >, <i>in press</i>
		<xsl:text> </xsl:text>
	</xsl:template>
	<xsl:template match="pubdate" >
		<xsl:value-of select="."/>, </xsl:template>
	<xsl:template match="source" >
		<i>
			<xsl:value-of select="."/>
		</i>
		<xsl:text> </xsl:text>
	</xsl:template>
	<xsl:template match="title" >
		<h1>
			<xsl:apply-templates select="p"/>
		</h1>
		<br/>
	</xsl:template>
	<xsl:template match="note" >
		<br/>
		<xsl:apply-templates select="."/>
		<br/>
	</xsl:template>
	<xsl:template match="url" >
		<br/>
		<xsl:apply-templates select="."/>
		<br/>
	</xsl:template>
	<xsl:template match="aug" >
		<xsl:for-each select="au">
			<xsl:variable name="aupos">
				<xsl:value-of select="position()"/>
			</xsl:variable>
			<xsl:choose>
				<xsl:when test="position()=last() and not(position()=1) and not (@type='on_behalf') and not(../etal)"> and </xsl:when>
				<xsl:when test="not(position()=1)">
					<xsl:choose>
						<xsl:when test="@type = 'on_behalf'"> for </xsl:when>
						<xsl:when test="(../au[$aupos + 1]/@type = 'on_behalf')"> and </xsl:when>
						<xsl:otherwise>, </xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise/>
			</xsl:choose>
			<b>
				<xsl:choose>
					<xsl:when test="cnm">
						<xsl:value-of select="cnm"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="fnm"/>
						<xsl:apply-templates select="mi" />
						<xsl:apply-templates select="mnm" />
						<xsl:apply-templates select="snm" />
						<xsl:apply-templates select="suf" />
					</xsl:otherwise>
				</xsl:choose>
			</b>
			<xsl:if test="@ca='yes'">* </xsl:if>
			<xsl:if test="@ce='yes'">
				<sup>&#8224; </sup>
			</xsl:if>
			<xsl:if test="@pa='yes'">
				<sup>&#8224; </sup>
			</xsl:if>
			<xsl:if test="count(../../insg/ins) > 1">
				<xsl:for-each select="insr">
					<sup>
						<xsl:if test="not(position()=1)">, </xsl:if>
						<xsl:value-of select="substring(@iid, 2)"/>
					</sup>
				</xsl:for-each>
			</xsl:if>
		</xsl:for-each>
		<xsl:for-each select="etal">, <b>
				<i>et al</i>.</b>
		</xsl:for-each>
		<br/>
	</xsl:template>
	<xsl:template match="history" >
		<br/>
		<table>
			<xsl:for-each select="rec">
				<xsl:call-template name="history-row">
					<xsl:with-param name="title">Received</xsl:with-param>
				</xsl:call-template>
			</xsl:for-each>
			<xsl:for-each select="revreq">
				<xsl:call-template name="history-row">
					<xsl:with-param name="title">Revisions requested</xsl:with-param>
				</xsl:call-template>
			</xsl:for-each>
			<xsl:for-each select="revrec">
				<xsl:call-template name="history-row">
					<xsl:with-param name="title">Revisions received</xsl:with-param>
				</xsl:call-template>
			</xsl:for-each>
			<xsl:for-each select="acc">
				<xsl:call-template name="history-row">
					<xsl:with-param name="title">Accepted</xsl:with-param>
				</xsl:call-template>
			</xsl:for-each>
			<xsl:for-each select="pub">
				<xsl:call-template name="history-row">
					<xsl:with-param name="title">Published</xsl:with-param>
				</xsl:call-template>
			</xsl:for-each>
		</table>
	</xsl:template>
	<xsl:template name="history-row">
		<xsl:param name="title"/>
		<tr>
			<td>
				<b>
					<xsl:value-of select="$title"/>
				</b>
			</td>
			<td width="25">&#160;</td>
			<td>
				<xsl:apply-templates mode="full"/>
			</td>
		</tr>
	</xsl:template>
	<xsl:template match="cpyrt" >
		<br/>
		<xsl:if test="year">&#169;<xsl:text> </xsl:text>
			<xsl:value-of select="year"/>
			<xsl:text> </xsl:text>
		</xsl:if>
		<xsl:if test="collab">
			<xsl:apply-templates select="collab"/>
			<xsl:text> </xsl:text>
		</xsl:if>
		<xsl:apply-templates select="note"/>
	</xsl:template>
	<xsl:template match="kwdg" >
		<b>Keywords: </b>
		<xsl:for-each select="kwd">
			<xsl:if test="not(position()=1)">, </xsl:if>
			<xsl:apply-templates select="."/>
		</xsl:for-each>
		<br/>
	</xsl:template>
	<xsl:template match="insg" >
		<xsl:for-each select="./ins">
			<xsl:if test="last() > 1">
				<sup>
					<xsl:value-of select="substring(@id, 2)"/>
				</sup>
			</xsl:if>
			<xsl:apply-templates select="p"/>
		</xsl:for-each>
		<br/>
	</xsl:template>
	<!-- procedures for rendering Abstract and Main Body -->
	<xsl:template match="sec">
		<xsl:call-template name="open-section"/>
		<br/>
		<xsl:call-template name="iterate-section"/>
		<p align="right">
			<a href="#top">Return to top</a>
		</p>
		<br/>
	</xsl:template>
	<xsl:template name="open-section">
		<xsl:param name="heading"/>
		<tr>
			<td bgcolor="#ffffff" valign="top">
				<a name="{generate-id()}">
					<h2>
						<xsl:choose>
							<xsl:when test="$heading">
								<xsl:copy-of select="$heading"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:apply-templates select="st/p"/>
							</xsl:otherwise>
						</xsl:choose>
					</h2>
				</a>
			</td>
		</tr>
	</xsl:template>
	<xsl:template name="iterate-section">
		<!-- runs through subsections -->
		<xsl:param name="nested">no</xsl:param>
		<xsl:for-each select="sec | p | graphic | bibl | fig | tbl | suppl">
			<xsl:choose>
				<xsl:when test="name()='sec'">
					<xsl:call-template name="render-subsection">
						<xsl:with-param name="nested">
							<xsl:value-of select="$nested"/>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="name()='graphic'">
					<xsl:apply-templates select="."/>
				</xsl:when>
				<xsl:when test="name()='bibl'">
					<xsl:apply-templates select="." mode="bdy"/>
				</xsl:when>
				<xsl:when test="name()='fig'">
					<xsl:call-template name="render-fig"/>
				</xsl:when>
				<xsl:when test="name()='tbl'">
					<xsl:apply-templates select="."/>
				</xsl:when>
				<xsl:when test="name()='suppl'">
					<xsl:apply-templates select="."/>
				</xsl:when>
				<xsl:otherwise>
					<p>
						<xsl:apply-templates/>
					</p>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>
	<xsl:template name="render-subsection">
		<xsl:param name="nested">no</xsl:param>
		<xsl:choose>
			<xsl:when test="$nested='no'">
				<p>
					<h3>
						<xsl:apply-templates select="st/p"/>
					</h3>
				</p>
			</xsl:when>
			<xsl:otherwise>
				<p>
					<xsl:choose>
						<xsl:when test="name(../../../..)='sec'">
							<i>
								<xsl:apply-templates select="st/p"/>
							</i>
						</xsl:when>
						<xsl:when test="name(../../..)='sec'">
							<h4>
								<i>
									<xsl:apply-templates select="st/p"/>
								</i>
							</h4>
						</xsl:when>
						<xsl:otherwise>
							<h4>
								<xsl:apply-templates select="st/p"/>
							</h4>
						</xsl:otherwise>
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
		<xsl:for-each select="../bdy/sec | ../bdy//fig | ../bdy//tbl | ../bdy//suppl">
			<xsl:choose>
				<xsl:when test="name()='sec'">
					<a href="#{generate-id()}">
						<xsl:apply-templates select="st/p"/>
					</a>
				</xsl:when>
				<xsl:when test="name()='fig'">
					<a href="#{generate-id()}">
						<xsl:apply-templates select="title/p"/>
					</a>
				</xsl:when>
				<xsl:when test="name()='tbl'">
					<a href="#{generate-id()}">
						<xsl:apply-templates select="title/p"/>
					</a>
				</xsl:when>
				<xsl:when test="name()='suppl'">
					<a href="#{generate-id()}">
						<xsl:apply-templates select="title/p"/>
					</a>
				</xsl:when>
			</xsl:choose>
	&#160;&#160;</xsl:for-each>
		<xsl:for-each select="//refgrp">
			<a href="#{generate-id()}">References</a>
		</xsl:for-each>
	</xsl:template>
	<!-- procedure for rendering references -->
	<xsl:template match="refgrp">
		<xsl:call-template name="open-section">
			<xsl:with-param name="heading">References</xsl:with-param>
		</xsl:call-template>
		<br/>
		<xsl:choose>
			<xsl:when test="bibl[@rating='1']">
				<xsl:call-template name="render-ratings"/>
			</xsl:when>
			<xsl:when test="bibl[@rating='2']">
				<xsl:call-template name="render-ratings"/>
			</xsl:when>
		</xsl:choose>
		<table width="100%" cellpadding="0" cellspacing="0" border="0">
			<xsl:apply-templates mode="ref"/>
		</table>
	</xsl:template>
	<xsl:template name="bibl-numbering">
		<xsl:number value="position()" format="1."/>
	</xsl:template>
	<xsl:template match="bibl" mode="ref">
		<tr>
			<td valign="top" align="right">
				<xsl:call-template name="bibl-numbering"/>
			</td>
			<td width="5">&#160;</td>
			<td valign="top" colspan="2">
				<a name="{@id}"/>
				<xsl:if test="child::note = child::node()[1]">
					<xsl:apply-templates select="note"/>
					<br/>
				</xsl:if>
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
				<xsl:if test="note[1] and not(child::note = child::node()[1])">
					<xsl:apply-templates select="note"/>
					<br/>
				</xsl:if>
				<xsl:for-each select="//abbr[@bid = current()/@id]">
					<xsl:call-template name="references-output-citation-return-link">
						<xsl:with-param name="pos" select="position()"/>
					</xsl:call-template>
				</xsl:for-each>
			</td>
		</tr>
		<tr>
			<td colspan="3">&#160;</td>
		</tr>
	</xsl:template>
	<xsl:template name="bibl-body">
		<xsl:apply-templates select="aug" mode="ref"/>
		<xsl:apply-templates select="insg" mode="ref"/>
		<xsl:if test="not(aug)">
			<xsl:apply-templates select="editor" mode="book"/>
		</xsl:if>
		<xsl:choose>
			<xsl:when test="publisher and not(url) and not(source)">
				<xsl:apply-templates select="title" mode="book"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="title" mode="ref"/>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:if test="source[1] | edition[1] | editor[1] | publisher[1] | pubdate[1] | inpress[1] | volume[1] | issue[1] | fpage[1] | lpage[1] | xrefbib[1] | url[1]">
			<xsl:if test="title">
				<br/>
			</xsl:if>
			<xsl:apply-templates select="url" mode="ref"/>
			<xsl:choose>
				<xsl:when test="publisher and not(url)">
					<!-- if we have a publisher but not a url then we assume reference is to a book -->
					<xsl:choose>
						<xsl:when test="title and not(source = 'PhD thesis')">
							<xsl:apply-templates select="source" mode="book"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:apply-templates select="source" mode="ref"/>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:apply-templates select="volume" mode="book"/>
					<xsl:apply-templates select="edition" mode="ref"/>
					<xsl:if test="aug">
						<xsl:apply-templates select="editor" mode="ref"/>
					</xsl:if>
					<xsl:apply-templates select="publisher" mode="ref"/>
					<xsl:apply-templates select="pubdate" mode="book"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="source" mode="ref"/>
					<xsl:apply-templates select="edition" mode="ref"/>
					<xsl:if test="aug">
						<xsl:apply-templates select="editor" mode="ref"/>
					</xsl:if>
					<xsl:apply-templates select="publisher" mode="ref"/>
					<xsl:apply-templates select="pubdate" mode="ref"/>
					<xsl:if test="(volume[1] | issue[1] | fpage[1] | lpage[1]) and pubdate[1]">,<xsl:text> </xsl:text>
					</xsl:if>
					<xsl:if test="volume[1] | issue[1]">
						<xsl:text> </xsl:text>
						<b>
							<xsl:value-of select="volume"/>
						</b>
						<xsl:if test="issue">(<xsl:value-of select="issue"/>)</xsl:if>
						<b>:</b>
					</xsl:if>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:apply-templates select="inpress" mode="ref"/>
			<xsl:if test="fpage[1] | lpage[1]">
				<xsl:apply-templates select="fpage" mode="ref"/>
				<xsl:apply-templates select="lpage" mode="ref"/>
				<xsl:text>. </xsl:text>
			</xsl:if>
			<xsl:apply-templates select="xrefbib" mode="ref"/>
			<br/>
		</xsl:if>
	</xsl:template>
	<xsl:template match="mi|snm|suf" mode="ref">
		<xsl:text> </xsl:text>
		<xsl:value-of select="."/>
	</xsl:template>
	<xsl:template match="source" mode="ref">
		<i>
			<xsl:value-of select="."/>
		</i>
		<xsl:if test="not(../title) or (.='PhD thesis')">.</xsl:if>
		<xsl:if test="name(following-sibling::node())='publisher'">
			<xsl:text> </xsl:text>
		</xsl:if>
	</xsl:template>
	<xsl:template match="source" mode="book">In <i>
			<xsl:choose>
				<xsl:when test="(substring(.,1,3)='In:') or (substring(.,1,3)='in:')">
					<xsl:value-of select="substring(.,4)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="."/>
				</xsl:otherwise>
			</xsl:choose>
		</i>.<xsl:if test="name(following-sibling::node())='publisher'">
			<xsl:text> </xsl:text>
		</xsl:if>
	</xsl:template>
	<xsl:template match="volume" mode="book">
		<i>
			<xsl:text> Volume </xsl:text>
			<xsl:value-of select="."/>
		</i>.</xsl:template>
	<xsl:template match="edition" mode="ref">
		<xsl:text> </xsl:text>
		<xsl:value-of select="."/>
		<xsl:call-template name="render-number-suffix"/> edition<xsl:text>. </xsl:text>
	</xsl:template>
	<xsl:template match="editor" mode="ref">
		<xsl:text>Edited by: </xsl:text>
		<xsl:value-of select="."/>
		<xsl:text>. </xsl:text>
	</xsl:template>
	<xsl:template match="editor" mode="book">
		<xsl:value-of select="."/>
		<xsl:text> (Ed</xsl:text>
		<xsl:if test="contains(.,',') or contains(.,' and ')">s</xsl:if>
		<xsl:text>): </xsl:text>
	</xsl:template>
	<xsl:template match="publisher" mode="ref">
		<xsl:value-of select="."/>
		<xsl:text>; </xsl:text>
	</xsl:template>
	<xsl:template match="inpress" mode="ref">, in press<xsl:text>. </xsl:text>
	</xsl:template>
	<xsl:template match="pubdate" mode="ref">
		<xsl:text> </xsl:text>
		<xsl:value-of select="."/>
		<xsl:if test="not(../fpage or ../lpage or ../inpress)">.</xsl:if>
	</xsl:template>
	<xsl:template match="pubdate" mode="book">
		<xsl:text> </xsl:text>
		<xsl:value-of select="."/>
		<xsl:choose>
			<xsl:when test="(../fpage or ../lpage or ../inpress)">:</xsl:when>
			<xsl:otherwise>.</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="title" mode="ref">
		<b>
			<xsl:apply-templates select="p"/>
			<xsl:variable name="lastchar" select="substring(normalize-space(.), string-length(normalize-space(.)), 1)"/>
			<xsl:choose>
				<xsl:when test="($lastchar = '.') or ($lastchar = '!') or ($lastchar = '?') or not (../aug)"/>
				<xsl:otherwise>.</xsl:otherwise>
			</xsl:choose>
		</b>
	</xsl:template>
	<xsl:template match="title" mode="book">
		<i>
			<xsl:apply-templates select="p"/>
			<xsl:variable name="lastchar" select="substring(normalize-space(.), string-length(normalize-space(.)), 1)"/>
			<xsl:choose>
				<xsl:when test="($lastchar = '.') or ($lastchar = '!') or ($lastchar = '?') or not (../aug)"/>
				<xsl:otherwise>.</xsl:otherwise>
			</xsl:choose>
		</i>
	</xsl:template>
	<xsl:template match="url" mode="ref"> [<a class="hiddenlink" target="_blank" href="{.}">
			<xsl:value-of select="."/>
		</a>]</xsl:template>
	<xsl:template match="insg" mode="ref">
		<xsl:for-each select="./ins">
			<xsl:if test="last() > 1">
				<sup>
					<xsl:value-of select="substring(@id, 2)"/>
				</sup>
			</xsl:if>
			<xsl:apply-templates select="p"/>
		</xsl:for-each>: 
</xsl:template>
	<xsl:template match="aug" mode="ref">
		<xsl:for-each select="au">
			<xsl:if test="not(position()=1)">
				<xsl:choose>
					<xsl:when test="@type='on_behalf'"> for </xsl:when>
					<xsl:otherwise>, </xsl:otherwise>
				</xsl:choose>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="count(fnm)>0">
					<xsl:apply-templates select="snm" mode="ref"/>
					<xsl:apply-templates select="suf" mode="ref"/>
					<xsl:text> </xsl:text>
					<xsl:value-of select="translate(fnm,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
					<xsl:value-of select="mi"/>
					<xsl:for-each select="insr">
						<sup>
							<xsl:if test="not(position()=1)">, </xsl:if>
							<xsl:value-of select="substring(@iid, 2)"/>
						</sup>
					</xsl:for-each>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="cnm"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
		<xsl:for-each select="etal">, <i>et al</i>.</xsl:for-each>:

</xsl:template>
	<xsl:template match="xrefbib" mode="ref">
		<xsl:if test="count(.//pubid[@idtype='doi'])=1">
			<xsl:choose>
				<!-- 
			Check whether this doi is a bmc one
			If it is, still go via dx.doi.org but use different text
		-->
				<xsl:when test="substring(.//pubid[@idtype='doi'],1,8)='10.1186/'">
				[<a class="hiddenlink" target="_blank" href="http://dx.doi.org/{.//pubid[@idtype='doi']}">BioMed Central Full Text</a>]
			</xsl:when>
				<xsl:otherwise>
					<!-- 
					if we have a full text link via PubMed then we ignore the DOI
					and use the PubMed id which is output below.
				-->
					<xsl:if test="count(.//pubid[@link='fulltext'])=0">
					[<a class="hiddenlink" target="_blank" href="http://dx.doi.org/{.//pubid[@idtype='doi']}">Publisher Full Text</a>]
				</xsl:if>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
		<!-- if PubMed has a full text link then display that 
	 	 but only if it is not a BMC article
	-->
		<xsl:if test="count(.//pubid[substring(..//pubid[@idtype='doi'],1,8)='10.1186/'])=0">
			<xsl:if test=".//pubid[@link='fulltext']">
				<xsl:choose>
					<xsl:when test="count(.//pubid[@idtype='pmpid'])=1">[<a class="hiddenlink" target="_blank" href="http://www.ncbi.nlm.nih.gov/entrez/eutils/elink.fcgi?dbfrom=pubmed&amp;cmd=prlinks&amp;retmode=ref&amp;id={.//pubid[@idtype='pmpid']}">Publisher Full Text</a>]</xsl:when>
					<xsl:when test="count(.//pubid[@idtype='pubmed'])=1">[<a class="hiddenlink" target="_blank" href="http://www.ncbi.nlm.nih.gov/entrez/eutils/elink.fcgi?dbfrom=pubmed&amp;cmd=prlinks&amp;retmode=ref&amp;id={.//pubid[@idtype='pubmed']}">Publisher Full Text</a>]</xsl:when>
					<xsl:when test="count(.//pubid[not(@idtype)])=1">[<a class="hiddenlink" target="_blank" href="http://www.ncbi.nlm.nih.gov/entrez/eutils/elink.fcgi?dbfrom=pubmed&amp;cmd=prlinks&amp;retmode=ref&amp;id={.//pubid[not(@idtype)]}">Publisher Full Text</a>]</xsl:when>
					<xsl:otherwise/>
				</xsl:choose>
			</xsl:if>
		</xsl:if>
		<!-- 
		and finally a PubMed Central full text link
	-->
		<xsl:if test="count(.//pubid[@idtype='pmcid'])=1">
			<xsl:choose>
				<xsl:when test="count(.//pubid[@idtype='pmpid'])=1">[<a class="hiddenlink" target="_blank" href="http://www.pubmedcentral.nih.gov/articlerender.fcgi?tool=pubmed&amp;pubmedid={.//pubid[@idtype='pmpid']}">PubMed Central Full Text</a>]</xsl:when>
				<xsl:when test="count(.//pubid[@idtype='pubmed'])=1">[<a class="hiddenlink" target="_blank" href="http://www.pubmedcentral.nih.gov/articlerender.fcgi?tool=pubmed&amp;pubmedid={.//pubid[@idtype='pubmed']}">PubMed Central Full Text</a>]</xsl:when>
				<xsl:when test="count(.//pubid[not(@idtype)])=1">[<a class="hiddenlink" target="_blank" href="http://www.pubmedcentral.nih.gov/articlerender.fcgi?tool=pubmed&amp;pubmedid={.//pubid[not(@idtype)]}">PubMed Central Full Text</a>]</xsl:when>
				<xsl:otherwise/>
			</xsl:choose>
		</xsl:if>
	</xsl:template>
	<xsl:template match="fpage" mode="ref">
		<xsl:value-of select="."/>
	</xsl:template>
	<xsl:template match="lpage" mode="ref">-<xsl:value-of select="."/>
	</xsl:template>
	<xsl:template name="render-ratings">
	Papers of particular interest have been highlighted as:<br/>
	&#8226; of special interest<br/>
	&#8226;&#8226; of outstanding interest<br/>
		<br/>
	</xsl:template>
	<xsl:template name="references-output-citation-return-link">
		<xsl:param name="pos"/>
		<xsl:if test="$pos=1">
			<xsl:text>Return to citation in text: </xsl:text>
		</xsl:if>
	 [<a class="hiddenlink" href="#{generate-id()}">
			<xsl:value-of select="$pos"/>
		</a>]
</xsl:template>
	<xsl:template name="render-number-suffix">
		<xsl:choose>
			<xsl:when test="(number(.) &gt;1000) or not(number(.)=.)"/>
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
				<td colspan="2">
					<hr/>
				</td>
			</tr>
		</table>
		<table>
			<tr>
				<td nowrap="y" align="center">
					<a name="{generate-id()}">
						<xsl:for-each select="graphic">
							<img border="0" src="{$base-url}{$image-url}{@file}.jpg"/>
						</xsl:for-each>
					</a>
				</td>
			</tr>
			<xsl:for-each select="text">
				<tr>
					<td>
						<xsl:for-each select="../title">
							<b>
								<xsl:apply-templates select="p"/>
							</b>
						</xsl:for-each>
						<xsl:for-each select="p">
							<xsl:if test="not(position()=1)">
								<br/>
							</xsl:if>
							<hr/>
							<xsl:apply-templates/>
							<hr/>
						</xsl:for-each>
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<p align="right">
			<a href="#top">Return to top</a>
		</p>
	</xsl:template>
	<!-- procedure for rendering tables -->
	<xsl:template match="tbl">
		<table width="100%">
			<tr>
				<td>
					<a name="{generate-id()}">
						<xsl:for-each select="title">
							<b>
								<xsl:apply-templates select="p"/>
							</b>
						</xsl:for-each>
					</a>
					<br/>
					<hr/>
				</td>
			</tr>
			<tr>
				<td>
					<xsl:for-each select="caption">
						<b>
							<xsl:apply-templates select="p"/>
						</b>
					</xsl:for-each>
					<br/>
					<hr/>
				</td>
			</tr>
			<tr>
				<td>
					<xsl:apply-templates select="tblbdy"/>
					<hr/>
				</td>
			</tr>
			<xsl:for-each select="tblfn">
				<tr>
					<td>
						<xsl:for-each select="p">
							<xsl:apply-templates select="."/>
							<xsl:if test="not(position()=last())">
								<br/>
							</xsl:if>
						</xsl:for-each>
						<hr/>
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<p align="right">
			<a href="#top">Return to top</a>
		</p>
	</xsl:template>
	<xsl:template match="tblbdy">
		<table width="100%">
			<xsl:for-each select="r">
				<tr valign="{@ra}">
					<xsl:for-each select="c">
						<td>
							<xsl:if test="@cspan">
								<xsl:attribute name="colspan"><xsl:value-of select="@cspan"/></xsl:attribute>
							</xsl:if>
							<xsl:if test="@rspan">
								<xsl:attribute name="rowspan"><xsl:value-of select="@rspan"/></xsl:attribute>
							</xsl:if>
							<xsl:if test="@ca">
								<xsl:attribute name="align"><xsl:value-of select="@ca"/></xsl:attribute>
							</xsl:if>
							<xsl:if test="@width">
								<xsl:attribute name="width"><xsl:value-of select="@width"/></xsl:attribute>
							</xsl:if>
							<xsl:choose>
								<xsl:when test="@indent">
									<xsl:for-each select="p">
										<table cellpadding="0" cellspacing="0">
											<tr>
												<td>
													<xsl:choose>
														<xsl:when test="../@indent[.='1']">&#160;&#160;&#160;&#160;&#160;</xsl:when>
														<xsl:when test="../@indent[.='2']">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</xsl:when>
														<xsl:when test="../@indent[.='3']">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</xsl:when>
													</xsl:choose>
												</td>
												<td>
													<xsl:apply-templates select="."/>
												</td>
											</tr>
										</table>
									</xsl:for-each>
									<xsl:apply-templates select="hr"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:for-each select="p">
										<xsl:apply-templates select="."/>
										<br/>
									</xsl:for-each>
									<xsl:apply-templates select="hr"/>
								</xsl:otherwise>
							</xsl:choose>
						</td>
					</xsl:for-each>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:template>
	<!-- procedure for rendering supplementary files -->
	<xsl:template match="suppl">
		<table width="100%">
			<tr>
				<td>
					<hr/>
					<a name="{generate-id()}">
						<xsl:for-each select="title">
							<b>
								<xsl:apply-templates select="p"/>
							</b>
						</xsl:for-each>
					</a>
					<br/>
					<hr/>
				</td>
			</tr>
			<tr>
				<td>
					<xsl:apply-templates select="text/p"/>
				</td>
			</tr>
			<tr>
				<td>
					<a href="{$base-url}{$suppl-url}{file/@name}">
						<xsl:apply-templates select="file/p"/>
					</a>
					<hr/>
				</td>
			</tr>
		</table>
	</xsl:template>
	<!-- common functions -->
	<xsl:template match="p">
		<xsl:apply-templates/>
	</xsl:template>
	<xsl:template match="b">
		<b>
			<xsl:apply-templates/>
		</b>
	</xsl:template>
	<xsl:template match="it">
		<i>
			<xsl:apply-templates/>
		</i>
	</xsl:template>
	<xsl:template match="sub">
		<sub>
			<xsl:apply-templates/>
		</sub>
	</xsl:template>
	<xsl:template match="sup">
		<sup>
			<xsl:apply-templates/>
		</sup>
	</xsl:template>
	<xsl:template match="monospace">
		<font class="monospace">
			<xsl:apply-templates/>
		</font>
	</xsl:template>
	<xsl:template match="a">
		<a>
			<xsl:attribute name="href"><xsl:value-of select="@href"/></xsl:attribute>
			<xsl:value-of select="."/>
		</a>
	</xsl:template>
	<xsl:template match="url">
		<a>
			<xsl:attribute name="href"><xsl:value-of select="."/></xsl:attribute>
			<xsl:value-of select="."/>
		</a>
	</xsl:template>
	<xsl:template match="email">
		<a>
			<xsl:attribute name="href">mailto:<xsl:value-of select="."/></xsl:attribute>
			<xsl:value-of select="."/>
		</a>
	</xsl:template>
	<xsl:template match="ul">
		<u>
			<xsl:apply-templates/>
		</u>
	</xsl:template>
	<xsl:template match="hr">
		<hr/>
	</xsl:template>
	<xsl:template match="xrefart">
		<a href="{$base-url}article/id/{@art}">
			<xsl:value-of select="."/>
		</a>
	</xsl:template>
	<xsl:template match="bibl" mode="bdy">
		<p>
			<xsl:call-template name="bibl-body"/>
		</p>
	</xsl:template>
	<xsl:template match="graphic">
		<img src="{$base-url}{$inline-url}{@file}"/>
	</xsl:template>
	<xsl:template match="text()">
		<xsl:value-of select="."/>
	</xsl:template>
	<xsl:template match="day|year" mode="full">
		<xsl:value-of select="number(.)"/>
	</xsl:template>
	<xsl:template match="day|year">
		<xsl:value-of select="number(.)"/>
	</xsl:template>
	<xsl:template match="month" mode="full">
		<xsl:choose>
			<xsl:when test="number(.)=1">
				<xsl:text>&#160;January&#160;</xsl:text>
			</xsl:when>
			<xsl:when test="number(.)=2">
				<xsl:text>&#160;February&#160;</xsl:text>
			</xsl:when>
			<xsl:when test="number(.)=3">
				<xsl:text>&#160;March&#160;</xsl:text>
			</xsl:when>
			<xsl:when test="number(.)=4">
				<xsl:text>&#160;April&#160;</xsl:text>
			</xsl:when>
			<xsl:when test="number(.)=5">
				<xsl:text>&#160;May&#160;</xsl:text>
			</xsl:when>
			<xsl:when test="number(.)=6">
				<xsl:text>&#160;June&#160;</xsl:text>
			</xsl:when>
			<xsl:when test="number(.)=7">
				<xsl:text>&#160;July&#160;</xsl:text>
			</xsl:when>
			<xsl:when test="number(.)=8">
				<xsl:text>&#160;August&#160;</xsl:text>
			</xsl:when>
			<xsl:when test="number(.)=9">
				<xsl:text>&#160;September&#160;</xsl:text>
			</xsl:when>
			<xsl:when test="number(.)=10">
				<xsl:text>&#160;October&#160;</xsl:text>
			</xsl:when>
			<xsl:when test="number(.)=11">
				<xsl:text>&#160;November&#160;</xsl:text>
			</xsl:when>
			<xsl:when test="number(.)=12">
				<xsl:text>&#160;December&#160;</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="."/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="month">
		<xsl:choose>
			<xsl:when test="number(.)=1">
				<xsl:text>&#160;Jan&#160;</xsl:text>
			</xsl:when>
			<xsl:when test="number(.)=2">
				<xsl:text>&#160;Feb&#160;</xsl:text>
			</xsl:when>
			<xsl:when test="number(.)=3">
				<xsl:text>&#160;Mar&#160;</xsl:text>
			</xsl:when>
			<xsl:when test="number(.)=4">
				<xsl:text>&#160;Apr&#160;</xsl:text>
			</xsl:when>
			<xsl:when test="number(.)=5">
				<xsl:text>&#160;May&#160;</xsl:text>
			</xsl:when>
			<xsl:when test="number(.)=6">
				<xsl:text>&#160;Jun&#160;</xsl:text>
			</xsl:when>
			<xsl:when test="number(.)=7">
				<xsl:text>&#160;Jul&#160;</xsl:text>
			</xsl:when>
			<xsl:when test="number(.)=8">
				<xsl:text>&#160;Aug&#160;</xsl:text>
			</xsl:when>
			<xsl:when test="number(.)=9">
				<xsl:text>&#160;Sep&#160;</xsl:text>
			</xsl:when>
			<xsl:when test="number(.)=10">
				<xsl:text>&#160;Oct&#160;</xsl:text>
			</xsl:when>
			<xsl:when test="number(.)=11">
				<xsl:text>&#160;Nov&#160;</xsl:text>
			</xsl:when>
			<xsl:when test="number(.)=12">
				<xsl:text>&#160;Dec&#160;</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="."/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="abbrgrp">
		<xsl:for-each select="abbr">
			<a name="{generate-id()}"/>
		</xsl:for-each>[<xsl:for-each select="abbr">
			<xsl:choose>
				<xsl:when test="ancestor::bdy/../bm/refgrp/bibl[@id=current()/@bid]/@rating > 0">
					<xsl:if test="not(position()=1)">,</xsl:if>
					<xsl:call-template name="render-abbr-link">
						<xsl:with-param name="text">
							<xsl:call-template name="render-with-bullets"/>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="ancestor::bdy/../bm/refgrp/bibl[@id=concat('B', current()-1)]/@rating > 0">
					<xsl:if test="not(position()=1)">,</xsl:if>
					<xsl:call-template name="render-abbr-link">
						<xsl:with-param name="text">
							<xsl:call-template name="render-with-bullets"/>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="ancestor::bdy/../bm/refgrp/bibl[@id=concat('B', current()+1)]/@rating > 0">
					<xsl:choose>
						<xsl:when test="preceding-sibling::abbr[position()=2]=current()-2">-<xsl:call-template name="render-with-bullets"/>
							<xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:if test="not(position()=1)">
								<xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>,</xsl:if>
							<xsl:call-template name="render-abbr-link">
								<xsl:with-param name="text">
									<xsl:call-template name="render-with-bullets"/>
								</xsl:with-param>
							</xsl:call-template>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:when test="preceding-sibling::abbr=current()-1">
					<xsl:if test="not(following-sibling::abbr=current()+1)">
						<xsl:choose>
							<xsl:when test="preceding-sibling::abbr[position()=2]=current()-2">-<xsl:call-template name="render-with-bullets"/>
								<xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>,<xsl:call-template name="render-abbr-link">
									<xsl:with-param name="text">
										<xsl:call-template name="render-with-bullets"/>
									</xsl:with-param>
								</xsl:call-template>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:if>
				</xsl:when>
				<xsl:when test="following-sibling::abbr=current()+1">
					<xsl:if test="not(position()=1)">,</xsl:if>
					<xsl:text disable-output-escaping="yes">&lt;a href="#</xsl:text>
					<xsl:value-of select="@bid"/>
					<xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
					<xsl:call-template name="render-with-bullets"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:if test="not(position()=1)">,</xsl:if>
					<xsl:call-template name="render-abbr-link">
						<xsl:with-param name="text">
							<xsl:call-template name="render-with-bullets"/>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>]<!--
	-->
	</xsl:template>
	<xsl:template name="render-abbr-link">
		<xsl:param name="text"/>
		<a onclick=" " href="#{@bid}">
			<xsl:value-of select="$text"/>
		</a>
	</xsl:template>
	<xsl:template name="render-with-bullets">
		<xsl:value-of select="."/>
		<xsl:choose>
			<xsl:when test="ancestor::bdy/../bm/refgrp/bibl[@id=current()/@bid]/@rating = 1">&#8226;</xsl:when>
			<xsl:when test="ancestor::bdy/../bm/refgrp/bibl[@id=current()/@bid]/@rating = 2">&#8226;&#8226;</xsl:when>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="figr">
		<xsl:variable name="pos">
			<xsl:value-of select="@fid"/>
		</xsl:variable>
		<a href="#{generate-id(ancestor::art//fig[@id = $pos])}">
			<xsl:value-of select="."/>
		</a>
	</xsl:template>
	<xsl:template match="tblr">
		<xsl:variable name="pos">
			<xsl:value-of select="@tid"/>
		</xsl:variable>
		<a href="#{generate-id(ancestor::art//tbl[@id = $pos])}">
			<xsl:value-of select="."/>
		</a>
	</xsl:template>
	<xsl:template match="supplr">
		<xsl:variable name="pos">
			<xsl:value-of select="@sid"/>
		</xsl:variable>
		<a href="#{generate-id(ancestor::art//suppl[@id = $pos])}">
			<xsl:value-of select="."/>
		</a>
	</xsl:template>
</xsl:stylesheet>
