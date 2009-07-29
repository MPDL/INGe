<%
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
* fÃ¼r wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Forderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/
%>
<%@page import="de.mpg.escidoc.services.framework.PropertyReader"%>
<%
	String urlBase = (request.getProtocol().contains("HTTPS") ? "https" : "http") + "://" + request.getServerName() + (request.getServerPort() != 80 ? ":" + request.getServerPort() : "");
	String searchPath = urlBase + "/search/SearchAndExport";
	String feedPath = urlBase + "/syndication/feed";
	String feedImage = "<img src=\""+ urlBase +"/syndication/resources/Live_bookmarks.png\" />";
%>

<jsp:directive.page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"/>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>eSciDoc SearchAndExport Service</title>
		
		<link href="/pubman/resources/eSciDoc_CSS_v2/main.css" type="text/css" rel="stylesheet"/>
	
		<link href="/pubman/resources/eSciDoc_CSS_v2/themes/skin_highContrast/styles/theme.css" id="highContrastTheme" type="text/css" title="kontrastreich" rel="alternate stylesheet"/>
		<link href="/pubman/resources/eSciDoc_CSS_v2/themes/skin_classic/styles/theme.css" id="classicTheme" type="text/css" title="classic" rel="alternate stylesheet"/>
		<link href="/pubman/resources/eSciDoc_CSS_v2/themes/skin_PubMan/styles/theme.css" id="PubManTheme" type="text/css" title="PubMan" rel="stylesheet"/>
		
		<link rel="SHORTCUT ICON" href="/pubman/resources/favicon.ico"/>
		
		<script src="/pubman/resources/eSciDoc_JavaScript/jquery/jquery.min.js" language="JavaScript" type="text/javascript">;</script>
		<script src="/pubman/resources/eSciDoc_JavaScript/eSciDoc_component_JavaScript/eSciDoc_full_item.js" language="JavaScript" type="text/javascript">;</script>
		<script type="text/javascript">$(document).ready(function(){installFullItem();});</script>

		<link rel="search" href="opensearch_apa_html_all.xml" type="application/opensearchdescription+xml" title="eSciDoc SearchAndExport APA" />
		<link rel="search" href="opensearch_endnote_all.xml" type="application/opensearchdescription+xml" title="eSciDoc SearchAndExport EndNote" />

		<link id="rss_0.9_link" rel="alternate" type="application/rss+xml" />
		<link id="rss_0.91N_link" rel="alternate" type="application/rss+xml" />
		<link id="rss_0.91U_link" rel="alternate" type="application/rss+xml" />
		<link id="rss_0.92_link" rel="alternate" type="application/rss+xml" />
		<link id="rss_0.93_link" rel="alternate" type="application/rss+xml" />
		<link id="rss_0.94_link" rel="alternate" type="application/rss+xml" />
		<link id="rss_1.0_link" rel="alternate" type="application/rss+xml" />
		<link id="rss_2.0_link" rel="alternate" type="application/rss+xml" />
		<link id="atom_0.3_link" rel="alternate" type="application/rss+xml" />
		<link id="atom_1.0_link" rel="alternate" type="application/rss+xml" />
		<script type="text/javascript" id="script">

			// This function is called to send the request.
			function submitItem()
			{
				var queryString =  '?cqlQuery=' + document.form.cqlQuery.value;
				
				queryString += '&exportFormat=' + document.form.exportFormat.options[document.form.exportFormat.selectedIndex].value;
				queryString += '&outputFormat=' + document.form.outputFormat.options[document.form.outputFormat.selectedIndex].value;
				queryString += '&language=' + document.form.language.options[document.form.language.selectedIndex].value;
				queryString += '&sortKeys=' + document.form.sortKeys.value;
				queryString += '&sortOrder=' + document.form.sortOrder.options[document.form.sortOrder.selectedIndex].value;
				queryString += '&startRecord=' + document.form.startRecord.value;
				queryString += '&maximumRecords=' + document.form.maximumRecords.value;
				
				var req = document.form.url.value  + queryString;
				
				document.getElementById('result').innerHTML = req;
				document.getElementById('resultArea').style.display='block';

				document.getElementById('feeds').style.display='block';
				
				setFeedAnchor('rss_0.9');
				setFeedLink('rss_0.9');
				setFeedAnchor('rss_0.91N');
				setFeedLink('rss_0.91N');
				setFeedAnchor('rss_0.91U');
				setFeedLink('rss_0.91U');
				setFeedAnchor('rss_0.92');
				setFeedLink('rss_0.92');
				setFeedAnchor('rss_0.93');
				setFeedLink('rss_0.93');
				setFeedAnchor('rss_0.94');
				setFeedLink('rss_0.94');
				setFeedAnchor('rss_1.0');
				setFeedLink('rss_1.0');
				setFeedAnchor('rss_2.0');
				setFeedLink('rss_2.0');
				setFeedAnchor('atom_0.3');
				setFeedLink('atom_0.3');
				setFeedAnchor('atom_1.0');
				setFeedLink('atom_1.0');
				
				//window.open(req,'','height=100, width=100, toolbar=no, scrollbars=yes, resizable=yes');
				location.href = req;
			}

			function checkOutputFormat()
			{
				var efv = document.form.exportFormat.value;
				document.form.outputFormat.disabled =  ! (efv == "APA" || efv == "AJP" ); 
			}
							
			function setFeedAnchor(type)
			{
				document.getElementById(type).href = '<%= feedPath %>/' + type +'/search?q=' + document.form.cqlQuery.value;
			}
			function setFeedLink(type)
			{
				document.getElementById(type+"_link").href = '<%= feedPath %>/' + type +'/search?q=' + document.form.cqlQuery.value;
				document.getElementById(type+"_link").title = "Current Search | " + type;
			}			
		</script>

	</head>
	<body lang="en">
	<form name="form" method="post" action="rest">
		<div class="full wrapper">
			<!-- start: skip link navigation -->
				<a href="#mainMenuSkipLinkAnchor" title="Skiplink to the main menu." class="skipLink">Skip to the main menu.</a>
				<a href="#contentSkipLinkAnchor" title="Skiplink to the page content." class="skipLink">Skip to the page content.</a>
				<a href="#metaMenuSkipLinkAnchor" title="Skiplink to the meta menu." class="skipLink">Skip to the meta menu.</a>
			<!-- end: skip link navigation -->
			<div class="full_area0 header clear">
				<!-- begin: short header section (including meta menu and main menu)-->
		
					<div id="metaMenuSkipLinkAnchor" class="full_area0 metaMenu">
					<!-- meta Menu starts here -->
					
						<!-- logo alternate area starts here || Please use this area only in short headers without logo -->
						<div class="free_area0 small_marginLExcl logoAlternate">
							<a href="index.jsp" title="Go to the start page.">
								<span>eSciDoc</span>
								<span>SearchAndExport Service</span>
							</a>
						</div>
						<!-- logo alternate area ends here -->
					<!-- meta Menu ends here -->
					</div>
	
					<div id="mainMenuSkipLinkAnchor" class="full_area0 mainMenu">
						<!-- main Menu starts here -->
					
						<a href="SearchAndExport_info.jsp" class="free_area0" title="Go to the Description page.">Description</a>

						<span class="free_area0">REST Example</span>

						<!-- main Menu ends here -->
					</div>
						
				<!-- end: short header section -->
			</div>
			<div id="content" class="full_area0 clear">
				<!-- begin: content section (including elements that visualy belong to the header (breadcrumb, headline, subheader and content menu)) -->
					<div class="headerSection">
<%--							
						<div class="clear breadcrumb">
							<!-- Breadcrumb starts here -->
							<ol>
								<li>&#160;</li>
							</ol>
							<!-- Breadcrumb ends here -->
						</div>
--%>			
						<div id="contentSkipLinkAnchor" class="clear headLine">
							<!-- Headline starts here -->
							<h1>eSciDoc SearchAndExport Service REST Example</h1>
							<!-- Headline ends here -->
						</div>
					</div>
<%--				
					<div class="small_marginLIncl subHeaderSection">
						<div class="contentMenu">
							<!-- content menu starts here -->
								<div class="free_area0 sub">
								<!-- content menu upper line starts here -->
									&#160;
								<!-- content menu upper line ends here -->
								</div>
								<div class="free_area0 sub action">
								<!-- content menu lower line starts here -->
									&#160;
								<!-- content menu lower line ends here -->
								</div>
							<!-- content menu ends here -->
							</div>
							
							<div class="subHeader">
								<!-- Subheadline starts here -->
									&#160;
								<!-- Subheadline ends here -->
							</div>
					</div>
--%>
					<div class="full_area0 fullItem">
						<div class="full_area0 itemBlock">
							<h3 class="xLarge_area0_p8 endline blockHeader">
								Search Query Form
							</h3>
							<span class="seperator"></span>
							<div class="free_area0 itemBlockContent endline">
								<div class="free_area0 endline itemLine noTopBorder">
									<b class="xLarge_area0_p8 endline labelLine clear">
										CQL search query<span class="noDisplay">: </span>
									</b>
									<span class="xHuge_area0 xTiny_marginLExcl endline">
										<input class="quad_txtInput" type="text" name="cqlQuery" value="escidoc.metadata=&#34;test&#34; and escidoc.content-model.objid=escidoc:persistent4" />
									</span>
								</div>
								<div class="free_area0 endline itemLine noTopBorder">
									<b class="xLarge_area0_p8 endline labelLine clear">
										Indexes<span class="noDisplay">: </span>
									</b>
									<span class="xHuge_area0 xTiny_marginLExcl endline">
										You can find <a href="<%= PropertyReader.getProperty("escidoc.framework_access.framework.url") %>/srw/search/escidoc_all?operation=explain" target="_blank" >here</a> all indexes are allowed.
									</span>
								</div>
								<div class="free_area0 endline itemLine noTopBorder">
									<b class="xLarge_area0_p8 endline labelLine clear">
										Sorting<span class="noDisplay">: </span>
									</b>
									<span class="xHuge_area0 xTiny_marginLExcl endline">
										<span class="double_area0 xTiny_marginRIncl">
											<label class="double_label" for="sortKeys">Sorting Key</label>
											<input class="double_txtInput" type="text" name="sortKeys" value="" />
										</span>
										<span class="double_area0 xTiny_marginRIncl">
											<label class="double_label" for="sortOrder">Sorting Order</label>
											<select class="double_select" name="sortOrder">
							                    <option value="ascending">ascending</option>
							                    <option value="descending">descending</option>
							                </select>
										</span>
									</span>
								</div>
								<div class="free_area0 endline itemLine noTopBorder">
									<b class="xLarge_area0_p8 endline labelLine clear">
										Record Span<span class="noDisplay">: </span>
									</b>
									<span class="xHuge_area0 xTiny_marginLExcl endline">
										<span class="double_area0 xTiny_marginRIncl">
											<label class="double_label" for="startRecord">Start Record (default is first)</label>
											<input class="double_txtInput" type="text" name="startRecord" value="" />
										</span>
										<span class="double_area0 xTiny_marginRIncl">
											<label class="double_label" for="maximumRecords">Maximum Number of records (default is all)</label>
											<input class="double_txtInput" type="text" name="maximumRecords" value="" />
										</span>
									</span>
								</div>
								<div class="free_area0 endline itemLine noTopBorder">
									<b class="xLarge_area0_p8 endline labelLine clear">
										Database Index<span class="noDisplay">: </span>
									</b>
									<span class="xHuge_area0 xTiny_marginLExcl endline">
									 	<span class="double_area0 xTiny_marginRIncl">
											<label class="double_label" for="language">Output Format</label>
											<select class="double_select" name="language">
							                    <option value="all">all languages</option>
												<option value="en">English</option>
												<option value="de">German</option>
							                </select>
										</span>
									</span>
								</div>
								<div class="free_area0 endline itemLine noTopBorder">
									<b class="xLarge_area0_p8 endline labelLine clear">
										Export Options<span class="noDisplay">: </span>
									</b>
									<span class="xHuge_area0 xTiny_marginLExcl endline">
										<span class="double_area0 xTiny_marginRIncl">
											<label class="double_label" for="exportFormat">Export Format</label>
											<select class="double_select" name="exportFormat" onchange="checkOutputFormat()">
							                    <option value="APA">APA</option>
												<option value="AJP">AJP</option>
												<option value="ENDNOTE">EndNote</option>
												<option value="BIBTEX">BibTeX</option>
												<option value="XML">eSciDoc XML</option>
							                </select>
										</span>
										<span class="double_area0 xTiny_marginRIncl">
											<label class="double_label" for="outputFormat">Output Format</label>
											<select class="double_select" name="outputFormat">
							                    <option value="pdf">PDF</option>
												<option value="html">HTML</option>
												<option value="rtf">RTF</option>
												<option value="odt">ODT</option>
												<option value="snippet">SNIPPET</option>
							                </select>
										</span>
									</span>
								</div>
								<div class="free_area0 endline itemLine noTopBorder">
									<b class="xLarge_area0_p8 endline labelLine clear">
										Target URL<span class="noDisplay">: </span>
									</b>
									<span class="xHuge_area0 xTiny_marginLExcl endline">
										<input class="quad_txtInput" type="text" name="url" value="<%= searchPath %>" />
									</span>
								</div>
								<a class="free_area0 collapse">hide</a>
							</div>
							<a class="free_area0 expand">show</a>								
						</div>
						<div id="resultArea" class="full_area0 itemBlock" style="display: none;">
							<h3 class="xLarge_area0_p8 endline blockHeader">
								Complete Search URI
							</h3>
							<span class="seperator"></span>
							<div class="free_area0 itemBlockContent endline">
								<div class="free_area0 endline itemLine noTopBorder">
									<span id="result" class="free_area0_p2 xTiny_marginLExcl endline">
										
									</span>
								</div>
							</div>							
						</div>
						<div id="feeds" class="full_area0 itemBlock" style="display: none;">
							<h3 class="xLarge_area0_p8 endline blockHeader">
								Feeds for the search.
							</h3>
							<span class="seperator"></span>
							<div class="free_area0 itemBlockContent endline">
								<div class="free_area0 endline itemLine noTopBorder">
									<span class="free_area0_p2 xTiny_marginLExcl endline">
										<p>The following feeds are available for the search:</p>
									</span>
								</div>
								<div class="free_area0 endline itemLine noTopBorder">
									<b class="xLarge_area0_p8 endline labelLine clear">
										RSS Feeds<span class="noDisplay">: </span>
									</b>
									<span class="xHuge_area0 xTiny_marginLExcl endline">
										<a id="rss_0.9" class="xHuge_area0">RSS version 0.9</a>
									</span
								</div>
								<div class="free_area0 endline itemLine noTopBorder">
									<b class="xLarge_area0_p8 endline labelLine clear">
										&#160;<span class="noDisplay">: </span>
									</b>
									<span class="xHuge_area0 xTiny_marginLExcl endline">
										<a id="rss_0.91N" class="xHuge_area0">RSS version 0.91N</a>
									</span
								</div>
								<div class="free_area0 endline itemLine noTopBorder">
									<b class="xLarge_area0_p8 endline labelLine clear">
										&#160;<span class="noDisplay">: </span>
									</b>
									<span class="xHuge_area0 xTiny_marginLExcl endline">
										<a id="rss_0.92" class="xHuge_area0">RSS version 0.92</a>
									</span
								</div>
								<div class="free_area0 endline itemLine noTopBorder">
									<b class="xLarge_area0_p8 endline labelLine clear">
										&#160;<span class="noDisplay">: </span>
									</b>
									<span class="xHuge_area0 xTiny_marginLExcl endline">
										<a id="rss_0.93" class="xHuge_area0">RSS version 0.93</a>
									</span
								</div>
								<div class="free_area0 endline itemLine noTopBorder">
									<b class="xLarge_area0_p8 endline labelLine clear">
										&#160;<span class="noDisplay">: </span>
									</b>
									<span class="xHuge_area0 xTiny_marginLExcl endline">
										<a id="rss_0.94" class="xHuge_area0">RSS version 0.94</a>
									</span
								</div>
								<div class="free_area0 endline itemLine noTopBorder">
									<b class="xLarge_area0_p8 endline labelLine clear">
										&#160;<span class="noDisplay">: </span>
									</b>
									<span class="xHuge_area0 xTiny_marginLExcl endline">
										<a id="rss_1.0" class="xHuge_area0">RSS version 1.0</a>
									</span
								</div>
								<div class="free_area0 endline itemLine noTopBorder">
									<b class="xLarge_area0_p8 endline labelLine clear">
										&#160;<span class="noDisplay">: </span>
									</b>
									<span class="xHuge_area0 xTiny_marginLExcl endline">
										<a id="rss_2.0" class="xHuge_area0">RSS version 2.0</a>
									</span>
								</div>
								<div class="free_area0 endline itemLine noTopBorder">
									<b class="xLarge_area0_p8 endline labelLine clear">
										ATOM Feeds<span class="noDisplay">: </span>
									</b>
									<span class="xHuge_area0 xTiny_marginLExcl endline">
										<a id="atom_0.3" class="xHuge_area0">Atom, version 0.3</a>
									</span
								</div>
								<div class="free_area0 endline itemLine noTopBorder">
									<b class="xLarge_area0_p8 endline labelLine clear">
										&#160;<span class="noDisplay">: </span>
									</b>
									<span class="xHuge_area0 xTiny_marginLExcl endline">
										<a id="atom_1.0" class="xHuge_area0">Atom, version 1.0</a>
									</span>
								</div>
								<a class="free_area0 collapse">hide</a>
							</div>
							<a class="free_area0 expand">show</a>								
						</div>
					</div>
					<div class="full_area0 formButtonArea">
						<a class="free_area1_p8 activeButton" href="#" onclick="submitItem()">Submit</a>
					</div>
				
				<!-- end: content section -->
			</div>	
		</div>
		<div class="footer">
			<div class="full_area0">
			<!-- begin: footer section-->
				&#160;
			<!-- end: footer section -->
			</div>
		</div>
	</form>
	</body>
</html>





<%-- 
<html>
	<head>
		<link rel="stylesheet" type="text/css" href="<%= urlBase %>/syndication/resources/escidoc-css/css/main.css" />
		<title>eSciDoc SearchandExport Service</title>
		<link id="rss_0.9_link" rel="alternate" type="application/rss+xml" />
		<link id="rss_0.91N_link" rel="alternate" type="application/rss+xml" />
		<link id="rss_0.91U_link" rel="alternate" type="application/rss+xml" />
		<link id="rss_0.92_link" rel="alternate" type="application/rss+xml" />
		<link id="rss_0.93_link" rel="alternate" type="application/rss+xml" />
		<link id="rss_0.94_link" rel="alternate" type="application/rss+xml" />
		<link id="rss_1.0_link" rel="alternate" type="application/rss+xml" />
		<link id="rss_2.0_link" rel="alternate" type="application/rss+xml" />
		<link id="atom_0.3_link" rel="alternate" type="application/rss+xml" />
		<link id="atom_1.0_link" rel="alternate" type="application/rss+xml" />
		<script type="text/javascript" id="script">

			// This function is called to send the request.
			function submitItem()
			{
				var queryString =  '?cqlQuery=' + document.form.cqlQuery.value;
				
				queryString += '&exportFormat=' + document.form.exportFormat.options[document.form.exportFormat.selectedIndex].value;
				queryString += '&outputFormat=' + document.form.outputFormat.options[document.form.outputFormat.selectedIndex].value;
				queryString += '&language=' + document.form.language.options[document.form.language.selectedIndex].value;
				queryString += '&sortKeys=' + document.form.sortKeys.value;
				queryString += '&sortOrder=' + document.form.sortOrder.options[document.form.sortOrder.selectedIndex].value;
				queryString += '&startRecord=' + document.form.startRecord.value;
				queryString += '&maximumRecords=' + document.form.maximumRecords.value;
				
				var req = document.form.url.value  + queryString;
				
				document.getElementById('result').innerHTML = req;   

				document.getElementById('feeds').style.visibility='visible';
				
				setFeedAnchor('rss_0.9');
				setFeedLink('rss_0.9');
				setFeedAnchor('rss_0.91N');
				setFeedLink('rss_0.91N');
				setFeedAnchor('rss_0.91U');
				setFeedLink('rss_0.91U');
				setFeedAnchor('rss_0.92');
				setFeedLink('rss_0.92');
				setFeedAnchor('rss_0.93');
				setFeedLink('rss_0.93');
				setFeedAnchor('rss_0.94');
				setFeedLink('rss_0.94');
				setFeedAnchor('rss_1.0');
				setFeedLink('rss_1.0');
				setFeedAnchor('rss_2.0');
				setFeedLink('rss_2.0');
				setFeedAnchor('atom_0.3');
				setFeedLink('atom_0.3');
				setFeedAnchor('atom_1.0');
				setFeedLink('atom_1.0');
				
				//window.open(req,'','height=100, width=100, toolbar=no, scrollbars=yes, resizable=yes');
				location.href = req;
			}

			function checkOutputFormat()
			{
				var efv = document.form.exportFormat.value;
				document.form.outputFormat.disabled =  ! (efv == "APA" || efv == "AJP" ); 
			}
							
			function setFeedAnchor(type)
			{
				document.getElementById(type).href = '<%= feedPath %>/' + type +'/search?q=' + document.form.cqlQuery.value;
			}
			function setFeedLink(type)
			{
				document.getElementById(type+"_link").href = '<%= feedPath %>/' + type +'/search?q=' + document.form.cqlQuery.value;
				document.getElementById(type+"_link").title = "Current Search | " + type;
			}			
		</script>
	</head>
	<body>
		<div id="col3">
			<div class="content">
		<h1 class="topSpace">
			eSciDoc SearchAndExport Service REST interface sample application
		</h1>
		<div class="topSpace">
		<form name="form" method="post" action="rest">
			<div class="editItemSingleCol">
				<label class="colLbl">This is the CQL search query:</label><br/>
				<input type="text" size="100" name="cqlQuery" value="escidoc.metadata=&#34;test&#34; and escidoc.content-model.objid=escidoc:persistent4"></input>
			</div>
			<div class="editItemSingleCol">
                <label class="colLbl">Add a sorting key here:</label><br/>
                <input type="text" size="20" name="sortKeys" value=""></input>
            </div>
            <div class="editItemSingleCol">
                <label class="colLbl">Sorting order</label><br/>
                <select size="1" name="sortOrder" style="width:120px">
                    <option value="ascending">ascending</option>
                    <option value="descending">descending</option>
                </select>
            </div>
            <div class="editItemSingleCol">
                <label class="colLbl">Define the start record (first record as default):</label><br/>
                <input type="text" size="10" name="startRecord" value=""></input>
            </div>
            <div class="editItemSingleCol">
                <label class="colLbl">Define the maximum records (all records as default):</label><br/>
                <input type="text" size="10" name="maximumRecords" value="50"></input>
            </div>
			<div class="editItemSingleCol">
				<label class="colLbl"><strong>Info:</strong> You can find
				 <a href="<%= PropertyReader.getProperty("escidoc.framework_access.framework.url") %>/srw/search/escidoc_all?operation=explain" target="_blank" >here</a> 
				all indexes are allowed.</label><br/>
			</div>
			<div class="editItemSingleCol">
				<label class="colLbl">Choose a Database Index:</label><br/>
				<select size="1" name="language" style="width:120px">
					<option value="all">all languages</option>
					<option value="en">en</option>
					<option value="de">de</option>
				</select>
			</div>
			<div class="editItemSingleCol">
				<label class="colLbl">Choose an Export format:</label><br/>
				<select size="1" name="exportFormat" onchange="checkOutputFormat()" style="width:120px">
					<option value="APA">APA</option>
					<option value="AJP">AJP</option>
					<option value="ENDNOTE">EndNote</option>
					<option value="BIBTEX">BibTeX</option>
					<option value="XML">eSciDoc XML</option>
				</select>
			</div>
			<div class="editItemSingleCol">
				<label class="colLbl">Choose an Output format:</label><br/>
				<select size="1" name="outputFormat" style="width:120px">
					<option value="pdf">PDF</option>
					<option value="html">HTML</option>
					<option value="rtf">RTF</option>
					<option value="odt">ODT</option>
					<option value="snippet">SNIPPET</option>
				</select>
			</div>
			<div class="editItemSingleCol">
				<label class="colLbl">This is the address where to send to:</label><br/>
				<input type="text" name="url" size="100" value="<%= searchPath %>"/>
			</div>
			<div class="editItemSingleCol">
				<label class="colLbl">Then submit it here:</label><br/>
				<input type="button" class="inlineButton" value="Submit" onclick="submitItem()" />
			</div>
			<div class="editItemSingleCol">
				<label class="colLbl">The complete URI:</label><br/>
				<pre id="result" style="background-color: #EEEEEE"></pre>
			</div>
			<div id="feeds" class="editItemSingleCol" style="visibility: hidden;">
				<h2 class="topSpace">
					RSS/ATOM feeds for the search.
				</h2>
				<label class="colLbl">The following feeds are available for the search:</label><br/>
				<ul>
					<li><a id="rss_0.9">RSS, version 0.9 <%= feedImage %></a></li>
					<li><a id="rss_0.91N">RSS, version 0.91N <%= feedImage %></a></li>
					<li><a id="rss_0.91U">RSS, version 0.91U <%= feedImage %></a></li>
					<li><a id="rss_0.92">RSS, version 0.92 <%= feedImage %></a></li>
					<li><a id="rss_0.93">RSS, version 0.93 <%= feedImage %></a></li>
					<li><a id="rss_0.94">RSS, version 0.94 <%= feedImage %></a></li>
					<li><a id="rss_1.0">RSS, version 1.0 <%= feedImage %></a></li>
					<li><a id="rss_2.0">RSS, version 2.0 <%= feedImage %></a></li>
					<li><a id="atom_0.3">Atom, version 0.3 <%= feedImage %></a></li>
					<li><a id="atom_1.0">Atom, version 1.0 <%= feedImage %></a></li>
				</ul>
			</div>			
		</form>
		</div>
			</div>
		</div>
	</body>
</html>

--%>