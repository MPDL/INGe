<%--

 CDDL HEADER START

 The contents of this file are subject to the terms of the
 Common Development and Distribution License, Version 1.0 only
 (the "License"). You may not use this file except in compliance
 with the License.

 You can obtain a copy of the license at license/ESCIDOC.LICENSE
 or http://www.escidoc.org/license.
 See the License for the specific language governing permissions
 and limitations under the License.

 When distributing Covered Code, include this CDDL HEADER in each
 file and include the License file at license/ESCIDOC.LICENSE.
 If applicable, add the following below this CDDL HEADER, with the
 fields enclosed by brackets "[]" replaced with your own identifying
 information: Portions Copyright [yyyy] [name of copyright owner]

 CDDL HEADER END


 Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@page import="de.mpg.mpdl.inge.aa.web.client.IngeAaClientFinish"%>
<%@page import="de.mpg.mpdl.inge.util.PropertyReader" %>
<%@page import="java.net.URLDecoder"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.Enumeration"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>

<title>PubMan Login</title>
<link href="/pubman/resources/cssFramework/main.css" type="text/css" rel="stylesheet" />
<link href=<%= PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_STYLESHEET_URL) %> type="text/css" rel="stylesheet" />
<style type="text/css">
.fa {
	line-height: inherit;
	margin-right: 0.454545em;
	color: #004465;
}
</style>
<head>
<body lang="de">

<%
String message = null;
if(null != request.getParameter("username")) {

  //TODO login
  String username = request.getParameter("username");
  String password = request.getParameter("password");

  String token = IngeAaClientFinish.loginInInge(username, password);
      if(null != token)
      {
        String target = request.getParameter("target");
     	response.sendRedirect(target + "&token=" + token);
      }
      else
      {
        message = "Incorrect credentials. Please try again";
      }
}
  %>

	<div class="full wrapper">
		<input id="offset" name="offset" type="hidden">

		<div class="full_area0 header clear">
			<!-- begin: header section (including meta menu, logo, searchMenu and main menu)-->
			<!-- import meta menu here -->
			<span id="metaMenuSkipLinkAnchor" class="full_area0 metaMenu"> </span>
			<div class="full_area0 LogoNSearch">
				<a id="lnkStartPage" name="lnkStartPage" href="/cone"
					title="Gehe zu CoNE"><img src="/cone/img/cone_logo_web.png"
					style="border: none;" class="tiny_marginLExcl headerLogo">
				<span class="tiny_marginLExcl xDouble_area0 themePark "></span></a>


				<!-- import search here-->

			</div>

		</div>
		<!-- import main menu here -->
		<div id="mainMenuSkipLinkAnchor" class="full_area0 mainMenu">
			<a id="lnkHome" name="lnkHome" href="/cone"
				title="Gehe zu CoNE." class="free_area0">Back to CoNE</a>
		</div>


			<div id="content" class="full_area0 clear">
				<!-- begin: content section (including elements that visualy belong to the header (breadcrumb, headline, subheader and content menu)) -->
				<div class="clear">
					<div class="headerSection">

						<div id="contentSkipLinkAnchor" class="clear headLine">
							<!-- Headline starts here -->
							<h1>Login</h1>
							<!-- Headline ends here -->
						</div>
					</div>
					<!-- MessageArea starts here -->
					<div class="subHeader">
						<!-- Subheadline starts here -->
						&nbsp;
						<!-- Subheadline ends here -->
					</div>
					<!-- MessageArea ends here -->
				</div>
				<div class="full_area0">
					<div class="full_area0 fullItem">
						<div class="third_area0 tiny_marginRExcl small_marginLExcl tile_category borderDarkTurquoise">
							<!-- Citation title and icon -->
							<div class="third_area0_p6">
								<h5 class="tile_citation_title">
									<img src="/pubman/resources/images/overviewPage/ANY_authors_64.png" class="big_imgBtn" align="right">
									Login
								</h5>
							</div>
							<div class="third_area0_p6">
							<p><% if(null != message) out.println(message); %></p>
							<form method="post">
								<span class="quad_area0_p8 xTiny_marginLExcl endline">
									<span class="double_label">Username</span>
									<input name="username" class="double_txtInput username" type="text">
								</span>
								<!-- Reenter password -->
								<span class="quad_area0_p8 xTiny_marginLExcl endline">
									<span class="double_label">Password</span>
									<input name="password" value="" class="double_txtInput password" type="password">
								</span>
								<!-- Update password -->
								<span class="quad_area0_p8 xTiny_marginLExcl endline">
									<input name="submit" value="Login" class="free_area1_p8 activeButton" type="submit">
								</span>
								<span class="quad_area0_p8 xTiny_marginLExcl endline"></span>
							</form>
							</div>
						</div>
					</div>
				</div>
			</div>
			<!-- end: content section -->
		</form>
	</div>

</body>
</html>
