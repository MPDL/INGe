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


 Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
-->
<jsp:root version="2.1" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html" xmlns:jsp="http://java.sun.com/JSP/Page">

<jsp:output doctype-root-element="html"
        doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
        doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" />

	<jsp:directive.page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" />
	<f:view locale="#{InternationalizationHelper.userLocale}" xmlns:e="http://www.escidoc.de/jsf">
		<f:loadBundle var="lbl" basename="Label"/>
		<f:loadBundle var="msg" basename="Messages"/>
		<f:loadBundle var="tip" basename="Tooltip"/>
		<html xmlns="http://www.w3.org/1999/xhtml">
			<head>
				<title><h:outputText value="#{ApplicationBean.appTitle}"/></title>
				<link rel="sword" type="application/xml" title="Sword Servicedocument Location" href="#{ApplicationBean.pubmanInstanceUrl}/pubman/faces/sword-app/servicedocument"/>
				<meta name="description" content="Title: #{lbl.Pubman_descriptionMetaTag}"></meta>
				<jsp:directive.include file="header/ui/StandardImports.jspf" />
				<jsp:directive.include file="home/HomePageFeedLinks.jspf" />
				
				<STYLE type="text/css">
					body {
						overflow-y: scroll;
						overflow-x: hidden;
					}
					
					#flakes {
						position: absolute;
						top: -200px;
						display: inline-block;
					}
					
					#flakes div {
					    background-image:
					        linear-gradient(180deg, 
					            rgba(255,255,255,0) 40%, 
					            #ffffff 40%, 
					            #ffffff 60%, 
					            rgba(255,255,255,0) 60%),
					        linear-gradient(90deg,  
					            rgba(255,255,255,0) 40%, 
					            #ffffff 40%, 
					            #ffffff 60%, 
					            rgba(255,255,255,0) 60%),
					        linear-gradient(45deg,  
					            rgba(255,255,255,0) 43%, 
					            #ffffff 43%, 
					            #ffffff 57%, 
					            rgba(255,255,255,0) 57%),
					        linear-gradient(135deg,  
					            rgba(255,255,255,0) 43%, 
					            #ffffff 43%, 
					            #ffffff 57%, 
					            rgba(255,255,255,0) 57%);
					    border-radius: 50%;
					    width:32px;
					    height:32px;
					    display: inline-block;
					    margin-left: 75px;
					    position: relative;
					    animation-name:rotate, falling;
					    animation-timing-function:linear;
					    animation-iteration-count:infinite;
					    animation-direction:linear;
					    animation-play-state:running;
					    /* Safari and Chrome: */
					    -webkit-animation-name:rotate, falling;
					    -webkit-animation-timing-function:linear;
					    -webkit-animation-iteration-count:infinite;
					    -webkit-animation-direction:linear;
					    -webkit-animation-play-state:running;
					}
					
					#flakes div:nth-of-type(2n) {
					    animation-delay:3.5s;
					    animation-duration:8s;
					    -webkit-animation-delay:3.5s;
					    -webkit-animation-duration:8s;
					    height: 28px;
					    transform-origin: right -30px 0;
					    width: 28px;
					}
					
					#flakes div:nth-of-type(2n+1) {
						animation-delay:0s;
					    animation-duration:5.5s;
					    -webkit-animation-delay:0s;
					    -webkit-animation-duration:5.5s;
					    height: 24px;
					    transform-origin: left 0px -30px;
					    width: 24px;
					}
					
					#flakes div:nth-of-type(3n) {
					    animation-delay:1.5s;
					    animation-duration:4.5s;
					    -webkit-animation-delay:1.5s;
					    -webkit-animation-duration:4.5s;
					    height: 16px;
					    transform-origin: left 30px -30px;
					    width: 16px;
					}
					
					#flakes div:nth-of-type(4n) {
					    animation-delay:3.5s;
					    animation-duration:3s;
					    -webkit-animation-delay:3.5s;
					    -webkit-animation-duration:3s;
					    height:10px;
					    transform-origin: left 0 30px;
					    width: 10px;
					}
					
					#flakes div:nth-of-type(5n) {
					    animation-delay:2s;
					    animation-duration:4.5s;
					    -webkit-animation-delay:2s;
					    -webkit-animation-duration:4.5s;
					    height:20px;
					    transform-origin: left -30px -30px;
					    width: 20px;
					}
					
					#flakes div:nth-of-type(6n) {
					    animation-delay:0.5s;
					    animation-duration:6s;
					    -webkit-animation-delay:0.5s;
					    -webkit-animation-duration:6s;
					    height: 24px;
					    transform-origin: left -30px 0;
					    width: 24px;
					}
					
					#flakes div:nth-of-type(7n) {
					    animation-delay:2s;
					    animation-duration:5s;
					    -webkit-animation-delay:2s;
					    -webkit-animation-duration:5s;
					    height: 16px;
					    width: 16px;
					}
					
					#flakes div:nth-of-type(8n) {
					    animation-delay:1.5s;
					    animation-duration:5.5s;
					    -webkit-animation-delay:1.5s;
					    -webkit-animation-duration:5.5s;
					    transform-origin: left 30px -30px;
					    height: 20px;
					    width: 20px;
					}
					
					#flakes div:nth-of-type(9n) {
					    animation-delay:1.5s;
					    animation-duration:5.5s;
					    -webkit-animation-delay:1.5s;
					    -webkit-animation-duration:5.5s;
					    transform-origin: left -30px 30px;
					    height: 14px;
					    width: 14px;
					}
					
					#flakes div:nth-of-type(10n) {
					    animation-delay:1s;
					    animation-duration:3s;
					    -webkit-animation-delay:1s;
					    -webkit-animation-duration:3s;
					    height: 10px;
					    transform-origin: left 30px 0;
					    width: 10px;
					}
					
					#flakes div:nth-of-type(11n) {
					    animation-delay:0s;
					    animation-duration:7.5s;
					    -webkit-animation-delay:0s;
					    -webkit-animation-duration:7.5s;
					    height: 12px;
					    transform-origin: left 30px -30px;
					    width: 12px;
					}
					
					#flakes div:nth-of-type(12n) {
					    animation-delay:5s;
					    animation-duration:3.5s;
					    -webkit-animation-delay:5s;
					    -webkit-animation-duration:3.5s;
					    height: 16px;
					    transform-origin: left 0px -30px;
					    width: 16px;
					}
					
					#flakes div:nth-of-type(13n) {
					    animation-delay:3s;
					    animation-duration:4s;
					    -webkit-animation-delay:3s;
					    -webkit-animation-duration:4s;
					    height: 18px;
					    transform-origin: left 0px -30px;
					    width: 18px;
					}
					
					#flakes div:nth-of-type(14n) {
					    animation-delay:0.5s;
					    animation-duration:5.5s;
					    -webkit-animation-delay:0.5s;
					    -webkit-animation-duration:5.5s;
					    height: 24px;
					    width: 24px;
					}
					
					#flakes div:nth-of-type(15n) {
					    animation-delay:1s;
					    animation-duration:7s;
					    -webkit-animation-delay:1s;
					    -webkit-animation-duration:7s;
					    height: 28px;
					    transform-origin: left -30px -30px;
					    width: 28px;
					}
					
					#flakes div:nth-of-type(16n) {
					    animation-delay:2s;
					    animation-duration:5.5s;
					    -webkit-animation-delay:2s;
					    -webkit-animation-duration:5.5s;
					    height: 24px;
					    width: 24px;
					}
					
					#flakes div:nth-of-type(17n) {
					    animation-delay:4.5s;
					    animation-duration:3s;
					    -webkit-animation-delay:4.5s;
					    -webkit-animation-duration:3s;
					    height: 20px;
					    transform-origin: left -30px 0px;
					    width: 20px;
					}
					
					#flakes div:nth-of-type(18n) {
					    animation-delay:5s;
					    animation-duration:2.5s;
					    -webkit-animation-delay:5s;
					    -webkit-animation-duration:2.5s;
					    height: 12px;
					    transform-origin: left 0px -30px;
					    width: 12px;
					}
					
					#flakes div:nth-of-type(19n) {
					    animation-delay:6.5s;
					    animation-duration:3s;
					    -webkit-animation-delay:6.5s;
					    -webkit-animation-duration:3s;
					    height: 10px;
					    transform-origin: left 30px -30px;
					    width: 10px;
					}
					
					#flakes div:nth-of-type(20n) {
					    animation-delay:2.5s;
					    animation-duration:4s;
					    -webkit-animation-delay:2.5s;
					    -webkit-animation-duration:4s;
					    height: 26px;
					    transform-origin: left -30px -30px;
					    width: 26px;
					}
					
					#flakes div:nth-of-type(21n) {
					    animation-delay:2.5s;
					    animation-duration:4s;
					    -webkit-animation-delay:2.5s;
					    -webkit-animation-duration:4s;
					    height: 12px;
					    transform-origin: left -30px 0;
					    width: 12px;
					}
					
					#flakes div:nth-of-type(22n) {
					    animation-delay:4s;
					    animation-duration:4s;
					    -webkit-animation-delay:4s;
					    -webkit-animation-duration:4s;
					    height: 16px;
					    transform-origin: left -30px -30px;
					    width: 16px;
					}
					
					#flakes div:nth-of-type(23n) {
					    animation-delay:1s;
					    animation-duration:4.5s;
					    -webkit-animation-delay:1s;
					    -webkit-animation-duration:4.5s;
					    height: 20px;
					    width: 20px;
					}
					
					#flakes div:nth-of-type(24n) {
					    animation-delay:3.5s;
					    animation-duration:6s;
					    -webkit-animation-delay:3.5s;
					    -webkit-animation-duration:6s;
					    height: 22px;
					    transform-origin: left 30px 0;
					    width: 22px;
					}
					
					#flakes div:nth-of-type(25n) {
					    animation-delay:5s;
					    animation-duration:3.5s;
					    -webkit-animation-delay:5s;
					    -webkit-animation-duration:3.5s;
					    height: 18px;
					    transform-origin: left 30px -30px;
					    width: 18px;
					}
					
					#flakes div:nth-of-type(26n) {
					    animation-delay:5s;
					    animation-duration:3.5s;
					    -webkit-animation-delay:5s;
					    -webkit-animation-duration:3.5s;
					    height: 18px;
					    transform-origin: left 30px -30px;
					    width: 18px;
					}
					
					#flakes div:nth-of-type(27n) {
					    animation-delay:2s;
					    animation-duration:3s;
					    -webkit-animation-delay:2s;
					    -webkit-animation-duration:3s;
					    height: 10px;
					    width: 10px;
					}
					
					#flakes div:nth-of-type(28n) {
					    animation-delay:1s;
					    animation-duration:6s;
					    -webkit-animation-delay:1s;
					    -webkit-animation-duration:6s;
					    height: 22px;
					    width: 22px;
					}
					
					#flakes div:nth-of-type(29n) {
					    animation-delay:2s;
					    animation-duration:7s;
					    -webkit-animation-delay:2s;
					    -webkit-animation-duration:7s;
					    height: 24px;
					    transform-origin: left 30px 0;
					    width: 24px;
					}
					
					#flakes div:nth-of-type(30n) {
					    animation-delay:1s;
					    animation-duration:2.5s;
					    -webkit-animation-delay:1s;
					    -webkit-animation-duration:2.5s;
					    height: 12px;
					    transform-origin: left 0px -30px;
					    width: 12px;
					}
					
					#flakes div:nth-of-type(31n) {
					    animation-delay:4s;
					    animation-duration:5s;
					    -webkit-animation-delay:4s;
					    -webkit-animation-duration:5s;
					    height: 18px;
					    transform-origin: left 30px -30px;
					    width: 18px;
					}
					
					#flakes div:nth-of-type(32n) {
					    animation-delay:2s;
					    animation-duration:7.5s;
					    -webkit-animation-delay:2s;
					    -webkit-animation-duration:7.5s;
					    height: 24px;
					    width: 24px;
					}
					
					#flakes div:nth-of-type(33n) {
					    animation-delay:4s;
					    animation-duration:5s;
					    -webkit-animation-delay:4s;
					    -webkit-animation-duration:5s;
					    height: 20px;
					    width: 20px;
					    transform-origin: left -30px 30px;
					}
					
					#flakes div:nth-of-type(34n) {
					    animation-delay:7s;
					    animation-duration:4s;
					    -webkit-animation-delay:7s;
					    -webkit-animation-duration:4s;
					    height: 16px;
					    width: 16px;
					    transform-origin: left -30px 0px;
					}
					
					#flakes div:nth-of-type(35n) {
					    animation-delay:1s;
					    animation-duration:3.5s;
					    -webkit-animation-delay:1s;
					    -webkit-animation-duration:3.5s;
					    height: 14px;
					    width: 14px;
					    transform-origin: right 0px 30px;
					}
					
					#flakes div:nth-of-type(36n) {
					    animation-delay:1s;
					    animation-duration:3.5s;
					    -webkit-animation-delay:1s;
					    -webkit-animation-duration:3.5s;
					    height: 26px;
					    width: 26px;
					}
					
					#flakes div:nth-of-type(37n) {
					    animation-delay:3s;
					    animation-duration:7s;
					    -webkit-animation-delay:3s;
					    -webkit-animation-duration:7s;
					    height: 20px;
					    transform-origin: left 0px 30px;
					    width: 20px;
					}
					
					#flakes div:nth-of-type(38n) {
					    animation-delay:3s;
					    animation-duration:7s;
					    -webkit-animation-delay:3s;
					    -webkit-animation-duration:7s;
					    height: 20px;
					    transform-origin: left 0px 30px;
					    width: 20px;
					}
					
					#flakes div:nth-of-type(39n) {
					    animation-delay:7s;
					    animation-duration:3s;
					    -webkit-animation-delay:7s;
					    -webkit-animation-duration:3s;
					    height: 10px;
					    transform-origin: right -30px 30px;
					    width:10px;
					}
					
					#flakes div:nth-of-type(40n) {
					    animation-delay:0s;
					    animation-duration:7s;
					    -webkit-animation-delay:0s;
					    -webkit-animation-duration:7s;
					    height: 26px;
					    transform-origin: right 30px 0px;
					    width:26px;
					}
					
					#flakes div:nth-of-type(41n) {
					    animation-delay:1s;
					    animation-duration:6s;
					    -webkit-animation-delay:1s;
					    -webkit-animation-duration:6s;
					    height: 22px;
					    width:22px;
					}
					
					#flakes div:nth-of-type(42n) {
					    animation-delay:0s;
					    animation-duration:7s;
					    -webkit-animation-delay:0s;
					    -webkit-animation-duration:7s;
					    height: 26px;
					    transform-origin: right 30px 0px;
					    width:26px;
					}
					
					#flakes div:nth-of-type(43n) {
					    animation-delay:3s;
					    animation-duration:5.5s;
					    -webkit-animation-delay:3s;
					    -webkit-animation-duration:5.5s;
					    height: 20px;
					    transform-origin: right 0px -30px;
					    width:20px;
					}
					
					#flakes div:nth-of-type(44n) {
					    animation-delay:5s;
					    animation-duration:4s;
					    -webkit-animation-delay:5s;
					    -webkit-animation-duration:4s;
					    height: 16px;
					    width:16px;
					}
					
					#flakes div:nth-of-type(45n) {
					    animation-delay:2s;
					    animation-duration:3.5s;
					    -webkit-animation-delay:2s;
					    -webkit-animation-duration:3.5s;
					    height: 14px;
					    transform-origin: left 0px -30px;
					    width:14px;
					}
					
					#flakes div:nth-of-type(46n) {
					    animation-delay:1s;
					    animation-duration:8.5s;
					    -webkit-animation-delay:1s;
					    -webkit-animation-duration:8.5s;
					    height: 26px;
					    width: 26px;
					}
					
					#flakes div:nth-of-type(47n) {
					    animation-delay:1.5s;
					    animation-duration:3s;
					    -webkit-animation-delay:1.5s;
					    -webkit-animation-duration:3s;
					    height: 12px;
					    transform-origin: left 30px 30px;
					    width: 12px;
					}
					
					#flakes div:nth-of-type(48n) {
					    animation-delay:4.5s;
					    animation-duration:8s;
					    -webkit-animation-delay:4.5s;
					    -webkit-animation-duration:8s;
					    height: 26px;
					    transform-origin: right 30px -30px;
					    width: 26px;
					}
					
					#flakes div:nth-of-type(49n) {
					    animation-delay:5s;
					    animation-duration:4s;
					    -webkit-animation-delay:5s;
					    -webkit-animation-duration:4s;
					    height: 20px;
					    transform-origin: left -30px -30px;
					    width: 20px;
					}
					
					#flakes div:nth-of-type(50n) {
					    animation-delay:6.5s;
					    animation-duration:4s;
					    -webkit-animation-delay:6.5s;
					    -webkit-animation-duration:4s;
					    height: 18px;
					    transform-origin: left 0px -30px;
					    width: 18px;
					}
					
					
					@keyframes rotate
					{
					  0%   {
					    left:25px; 
					    transform: rotate(0deg);
					  }
					  33% 
					  {
					    left:50px;
					    transform: rotate(50deg);
					  }
					  66% 
					  {
					    left:0px;
					    transform: rotate(0deg);
					  }
					  100% 
					  {
					    left:25px; 
					    transform: rotate(-50deg);
					  }
					}
					
					@keyframes falling 
					{
					  0%
					  {
					    top:0px;
					  }
					  100%
					  {
					    top:1200px;
					  }
					}
					
					@-webkit-keyframes rotate
					{
					  0%   {
					    left:25px; 
					    transform: rotate(0deg);
					  }
					  33% 
					  {
					    left:50px;
					    transform: rotate(50deg);
					  }
					  66% 
					  {
					    left:0px;
					    transform: rotate(0deg);
					  }
					  100% 
					  {
					    left:25px; 
					    transform: rotate(-50deg);
					  }
					}
					
					@-webkit-keyframes falling 
					{
					  0%
					  {
					    top:0px;
					  }
					  100%
					  {
					    top:1200px;
					  }
					}
				</STYLE>
			</head>
			<body lang="#{InternationalizationHelper.locale}">
				<h:outputText value="#{HomePage.beanName}" styleClass="noDisplay" />
				<h:form id="form1">
					<div class="full wrapper">
						<h:inputHidden id="offset"></h:inputHidden>
		
						<!-- import header -->
						<jsp:directive.include file="header/Header.jspf" />
						<div id="content" class="full_area0 clear">
						<!-- begin: content section (including elements that visualy belong to the header (breadcrumb, headline, subheader and content menu)) -->
							<div class="clear">
								<div class="headerSection">
									
								<jsp:directive.include file="header/Breadcrumb.jspf" />
						
									<div id="contentSkipLinkAnchor" class="clear headLine">
										<!-- Headline starts here -->
										<h1><h:outputText value="#{lbl.HomePage}" /></h1>
										<!-- Headline ends here -->
									</div>
								</div>
								<div class="small_marginLIncl subHeaderSection">
									<div class="contentMenu">
									<!-- content menu starts here -->
										<div class="free_area0 sub">
										<!-- content menu upper line starts here -->
											&#160;
										<!-- content menu upper line ends here -->
										</div>
									<!-- content menu ends here -->
									</div>
									<div class="subHeader">
										<!-- Subheadline starts here -->
										<h:messages styleClass="singleMessage" errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{HomePage.numberOfMessages == 1}"/>
										<h:panelGroup layout="block" styleClass="half_area2_p6 messageArea errorMessageArea" rendered="#{HomePage.hasErrorMessages and HomePage.numberOfMessages != 1}">
											<h2><h:outputText value="#{lbl.warning_lblMessageHeader}"/></h2>
											<h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{HomePage.hasMessages}"/>
										</h:panelGroup>
										<h:panelGroup layout="block" styleClass="half_area2_p6 messageArea infoMessageArea" rendered="#{HomePage.hasMessages and !HomePage.hasErrorMessages and HomePage.numberOfMessages != 1}">
											<h2><h:outputText value="#{lbl.info_lblMessageHeader}"/></h2>
											<h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{HomePage.hasMessages}"/>
										</h:panelGroup>
										<h:outputText value="&#160;" rendered="#{!HomePage.hasErrorMessages}" />
										<!-- Subheadline ends here -->
									</div>
								</div>
							</div>
							<div class="full_area0">
								<div class="full_area0 infoPage">
									<!-- Main Content -->
									
									<h:panelGroup styleClass="half_area0_p8 mainSection" rendered="#{!PubManSessionBean.loggedIn and InternationalizationHelper.homeContent!=null}">
										<h:outputText value="#{InternationalizationHelper.homeContent}" escape="false"/>
									</h:panelGroup>
									
									<h:panelGroup styleClass="half_area0_p8 mainSection" rendered="#{!PubManSessionBean.loggedIn and InternationalizationHelper.homeContent==null}">
										<jsp:directive.include file="home/StartPageLoggedOut.jspf" />
									</h:panelGroup>
									
									<h:panelGroup styleClass="half_area0_p8 mainSection" rendered="#{PubManSessionBean.loggedIn}">
										<jsp:directive.include file="home/StartPageLoggedIn.jspf" />
									</h:panelGroup>
									
									<!-- Side Panels -->
									<h:panelGroup styleClass="sideSectionArea">
										<h:panelGroup styleClass="free_area0_p8 sideSection">
											<jsp:directive.include file="home/LastReleased.jspf" />
											<h:panelGroup rendered="#{ApplicationBean.pubmanBlogFeedUrl != ''}" >
												<jsp:directive.include file="home/BlogIntegration.jspf"  />
											</h:panelGroup>
											<h:panelGroup>
												<div id="searchCloudDiv">&#160;</div>
											</h:panelGroup>
										</h:panelGroup>
									</h:panelGroup>
									
								</div>	
							</div>
						</div>
						<!-- end: content section -->
						<jsp:directive.include file="footer/Footer.jspf" />
						<script type="text/javascript">
							$pb("input[id$='offset']").submit(function() {
								$pb(this).val($pb(window).scrollTop());
							});
							$pb(document).ready(function () {
								$pb(window).scrollTop($pb("input[id$='offset']").val());
								$pb(window).scroll(function(){$pb("input[id$='offset']").val($pb(window).scrollTop())});
							});
						</script>
					</div> <!-- end: full wrapper -->
				</h:form>
				<div id="flakes">
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				    <div>&#160;</div>
				</div>
			</body>
		</html>
	</f:view>
</jsp:root>
