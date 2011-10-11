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


 Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
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
		<html>
		    <head>
		        <title>PubMan Online Help</title>
		        <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"/>
		        <meta name="generator" content="TeX4ht (http://www.cse.ohio-state.edu/~gurari/TeX4ht/)"/>
		        <meta name="originator" content="TeX4ht (http://www.cse.ohio-state.edu/~gurari/TeX4ht/)"/>
		        <!-- html -->
		        <meta name="src" content="eSciDoc_help_de.tex"/>
		        <meta name="date" content="2008-10-08 13:10:00"/>
		        <jsp:directive.include file="/header/ui/StandardImports.jspf" />
		        <!-- <link rel="stylesheet" type="text/css" href="eSciDoc_help_en.css"> -->
		    </head>
		    <body>
		        <div class="maketitle wrapper" style="padding: 0.74em; font-size: 129% !important; width: auto;">
		            <h2>PubMan Online Help</h2>
		            <p class="MsoNormal">
		                <span class="cmr-12"><span style="" lang="EN-GB">January 16, 2009</span></span>
		                <span style="" lang="EN-GB"></span>
		            </p>
		            <h3>
		            	<span style="" lang="EN-GB">Contents</span>
		            	<span class="sectiontoc"><span style="" lang="EN-GB"></span></span>
		            </h3>
		            <span class="sectiontoc"><span style="" lang="EN-GB">1. <a href="#HomePage">About
		                        PubMan</a></span></span>
		            <span style="" lang="EN-GB"></span>
		            <span style="" lang="EN-GB"><span class="subsectiontoc">
		                    <br/>
		                    <span class="sectiontoc"><span style="" lang="EN-GB">2. <a href="#Preface">Preface</a></span></span><span style="" lang="EN-GB"></span><span style="" lang="EN-GB"><span class="subsectiontoc">
		                            <br/>
		                            &#160;&#160;&#160; 2.1. <a href="#Log_in">Log In</a>
		                            
		                            
		                        </span>
		                        <span lang="EN-GB">
		                            <br/>
		                            &#160;&#160;&#160; 2.2. <a href="#User_Roles_and_Workflows">User Roles and Workflows</a>
		                            
		                            
		                        </span>
		                        <span lang="EN-GB">
		                            <br/>
		                            &#160;&#160;&#160; &#160;&#160;&#160; 2.2.1. <a href="#Workflows">Workflows</a>
		                            
		                            
		                        </span>
		                        <span style="" lang="EN-GB">
		                            <br/>
		                            &#160;&#160;&#160; &#160;&#160;&#160; 
		                            &#160;&#160;&#160; 2.2.1.1. 
		                        </span>
		                        <a href="#Depositor"><span style="" lang="EN-GB"></span></a><a href="#Standard_Workflow">Standard
		                            Workflow</a><span style="" lang="EN-GB">
		                            
		                            
		                        </span><span style="" lang="EN-GB">
		                            <br/>
		                            &#160;&#160;&#160; &#160;&#160;&#160; 
		                            &#160;&#160;&#160; 2.2.1.2. 
		                        </span>
		                        <a href="#Moderator"><span style="" lang="EN-GB"></span></a><a href="#Simple_Workflow">Simple
		                            Workflow</a><span style="" lang="EN-GB">
		                            
		                            
		                        </span><span lang="EN-GB">
		                            <br/>
		                            &#160;&#160;&#160; &#160;&#160;&#160; 2.2.2. <a href="#PubMan_User_Roles">PubMan User Roles</a>
		                            
		                            
		                        </span>
		                        <span style="" lang="EN-GB"></span><span style="" lang="EN-GB">
		                            
		                            
		                        </span><span class="sectiontoc"><span style="" lang="EN-GB">
		                                <br/>
		                                3 <a href="#Functionalities">PubMan
		                                    Functionalities</a>
		                            </span>
		                        </span><a href="#PubMan%20Functionalities"><span style=""></span></a><a href="#PubMan%20Functionalities"><span style="" lang="EN-GB"></span></a><span style="" lang="EN-GB"><span class="subsectiontoc">
		                                <br/>
		                                &#160;&#160;&#160; 3.1. <a href="#Search_Possibilities">Search
		                                    Possibilities in PubMan</a>
		                            </span>
		                        </span><span style="" lang="EN-GB"><span class="subsectiontoc">
		                                <br/>
		                                &#160;&#160;&#160; 3.1.1. <a href="#Quick_Search">Quick
		                                    Search</a>
		                            </span>
		                        </span><span style="" lang="EN-GB"><span class="subsectiontoc">
		                                <br/>
		                                &#160;&#160;&#160; 3.1.2. <a href="#Advanced_Search">Advanced
		                                    Search</a>
		                            </span>
		                            <br/>
		                            &#160;&#160; &#160;&#160; &#160;&#160;&#160; &#160; 3.1.2.1. <a href="#Date_Search">Date Search</a>
		                            <br/>
		                        </span>
		                        <span style="" lang="EN-GB"><span class="subsectiontoc"> &#160;&#160;&#160; 3.1.3. <a href="#Organization_Search">Organization
		                                    Search</a></span></span><a href="PubMan%20Help%20English.html#AffiliationTreePage"><span style=""></span></a><span style="" lang="EN-GB"></span><span style="" lang="EN-GB"><span class="sectiontoc">
		                                <br/>
		                                &#160;&#160;&#160; 3.2. <a href="#Search_Results">Search
		                                    Results</a>
		                            </span>
		                        </span><span style="" lang="EN-GB"><span class="subsectiontoc">
		                                <br/>
		                                &#160;&#160;&#160; 3.2.1. <a href="#Export">Export</a>
		                            </span>
		                        </span><a href="#Export"><span style=""></span></a><span style="" lang="EN-GB"></span><span style="" lang="EN-GB"><span class="subsubsectiontoc">
		                                <br/>
		                                &#160;&#160;&#160; &#160;&#160;&#160; 3.2.1.1. <a href="#Send_Export_via_E-Mail">Send
		                                    Export via E-Mail</a>
		                            </span>
		                        </span><span style="" lang="EN-GB"><span class="sectiontoc">
		                                <br/>
		                                &#160;&#160;&#160; &#160;&#160;&#160; 3.3. <a href="#Basket">Basket</a>
		                            </span>
		                        </span><span style="" lang="EN-GB"><span class="sectiontoc">
		                                <br/>
		                                &#160;&#160;&#160; 3.4. <a href="#View_Item">View
		                                    Item</a>
		                            </span>
		                            <a href="#View%20Item"></a></span><span style="" lang="EN-GB"><span class="subsectiontoc">
		                                <br/>
		                                &#160;&#160;&#160; 3.4.1. <a href="#View_Release_History">View release History</a>
		                            </span>
		                        </span><span style="" lang="EN-GB"><span class="subsectiontoc">
		                                <br/>
		                                &#160;&#160;&#160; 3.4.2.<a href="#View%20Item_Revisions">View
		                                    Revisions</a>
		                            </span>
		                        </span><span style="" lang="EN-GB"><span class="subsectiontoc">
		                                <br/>
		                                &#160;&#160;&#160; 3.4.3. <a rel="tag" href="#View_Item_Statistics">View Item Statistics</a>
		                            </span>
		                            
		                            
		                        </span><span style="" lang="EN-GB">
		                            <br/>
		                            &#160;&#160;&#160; 3.4.4. <a href="#Item_Log">Item
		                                Log (Registered Users only)</a>
		                            <br/>
		                        </span>
		                        <span class="subsectiontoc">&#160;&#160;&#160; 
		                            3.4.5. <a href="#Local_Tags">Local
		                                Tags (Registered Users only)</a></span>
		                        <br/>
		                        <span class="sectiontoc"> 4. <a href="#Submission">Submission</a></span><a href="#SubmissionPage"></a><span class="sectiontoc">
		                            <br/>
		                            &#160;&#160;&#160; 4.1. <a href="#Create_Item">Create
		                                Item</a>
		                            <br/>
		                            &#160;&#160;&#160; 4.2. <a href="#Edit_Item">Create / Edit Item</a>
		                            <br/>
		                            &#160;&#160;&#160; 4.2.1. <a href="#Submitting_Persons_and_Organizations">Submitting
		                                Persons and Organizations</a>
		                        </span>
		                        <br/>
		                        &#160;&#160;&#160; 4.2.2. <a href="#Submitting_Journal_Names">Submitting Journal Names</a>
		                        <br/>
		                        &#160;&#160;&#160; 4.2.3. <a href="#Entering_the_Publication_Language">Entering the
		                            Publication Language</a>
		                        <br/>
		                        &#160;&#160;&#160; 4.2.4. <a href="#Entering_Dates">Entering
		                            Dates</a>
		                        <br/>
		                    </span>
		                    <span style="" lang="EN-GB">&#160;&#160;&#160; 
		                        4.2.5. <a href="#Providing_Rights_Information">Provide Rights Information</a></span>
		                    <br/>
		                    &#160;&#160;&#160; 4.3. <a href="#Create_New_Revision">Create New Revision</a>
		                    <br/>
		                    &#160;&#160;&#160; 4.4. <a href="#Validate_Item">Validate
		                        Item</a>
		                    <br/>
		                    &#160;&#160;&#160; 4.5. <a href="#Save_Item">Save
		                        Item</a>
		                    <br/>
		                    &#160;&#160;&#160; 4.6. <a href="#Delete%20Item">Delete
		                        Item</a>
		                    <br/>
		                    &#160;&#160;&#160; 4.7. <a href="#Submit_Item">Submit
		                        Item</a>
		                    <br/>
		                    &#160;&#160;&#160; 4.7.1. <a href="#Submission_Comment">Submission
		                        Comment</a>
		                </span>
		            </span>
		            <a href="PubMan%20Help%20English.html#Submission_Comment"><span style=""></span></a>
		            <span style="" lang="EN-GB">
		                <br/>
		                &#160;&#160;&#160; 4.8. <a href="#Release_Item">Release
		                    Item</a>
		                
		                
		            </span>
		            <br/>
		            &#160;&#160;&#160; 4.8.1. <a href="#Release_Comment">Release
		                Comment</a>
		            
		            
		            <br/>
		            5. <a href="#Quality%20Assurance%20in%20PubMan">Quality Assurance in
		                PubMan</a>
		            <span style="" lang="EN-GB"><span class="sectiontoc">
		                    <br/>
		                    &#160;&#160;&#160; 5.1. <a href="#Send_Back_for_Re-Work">Send Back for Re-work</a>
		                    
		                    
		                </span>
		                <br/>
		                &#160;&#160;&#160; 5.2. <a href="#Modify_Item">Modify
		                    Item</a>
		                
		                
		            </span>
		            <br/>
		            &#160;&#160;&#160; 5.3. <a href="#Accept_Item">Accept
		                Item</a>
		            
		            
		            <br/>
		            &#160;&#160;&#160; 5.3.1. <a href="#Acceptance_Comment">Acceptance Comment</a>
		            
		            
		            <br/>
		            &#160;&#160;&#160; 5.4.<a href="#Withdraw%20Item">Withdraw
		                Item</a>
		            
		            
		            <br/>
		            6. <a href="#Tools%20for%20Data%20Management">Tools for
		                Data Management</a>
		            <span class="sectiontoc">
		                <br/>
		                &#160;&#160;&#160; 6.1. <a href="#My%20Items">My
		                    Items</a>
		                
		                
		            </span>
		            <br/>
		            &#160;&#160;&#160; 6.1.1. <a href="#Item_State">Item
		                State</a>
		            
		            
		            <br/>
		            &#160;&#160;&#160; 6.1.2. <a href="#Sort_by">Sort
		                by</a>
		            
		            
		            <br/>
		            &#160;&#160;&#160; 6.2. <a href="#Quality_Assurance_Workspace">Quality Assurance
		                Workspace</a>
		            
		            
		            <br/>
		            &#160;&#160;&#160; 6.2.1. <a href="#Item_State_QA">Item
		                State</a>
		            
		            
		            <br/>
		            &#160;&#160;&#160; 6.2.2. <a href="#Sort_by_QA">Sort
		                by</a>
		            
		            
		            <br/>
		            &#160;&#160;&#160; 6.3. <a href="#Import%20Workspace">Import
		                Workspace</a>
		            
		            
		            <br/>
		            <p class="MsoNormal" style="margin-bottom: 12pt;">
		                <span style="" lang="EN-GB">
		                    <br style=""/>
		                    
		                    
		                </span>
		            </p>
		            <h3>1. <a name="HomePage"></a><span style="" lang="EN-GB">About
		                    PubMan</span></h3>
		            PubMan supports research organizations in the management, dissemination
		            and re-use of publications and supplementary material. The solution
		            PubMan is a component of the eResearch infrastructure of the Max Planck
		            Society and is based on the service-oriented architecture of eSciDoc.
		            Further information can be found under:&#160;<a href="http://colab.mpdl.mpg.de/mediawiki/Portal:PubMan">http://colab.mpdl.mpg.de/mediawiki/Portal:PubMan</a>
		            <h3><span class="titlemark"><span style="" lang="EN-GB">2. </span></span><a name="Preface"></a><span style="" lang="EN-GB">Preface
		                    
		                    
		                </span></h3>
		            <h4><span class="titlemark"><span style="" lang="EN-GB">2.1. </span></span><a name="Log_in"></a><span style="" lang="EN-GB">Log
		                    in
		                    
		                    
		                </span></h4>
		            <p class="noindent">
		                <span style="" lang="EN-GB">Please
		                    enter
		                    your username and your password, so that you can make use of
		                    functionalities
		                    only available to registered users, such as submitting content to
		                    PubMan..</span>
		            </p>
		            <p class="noindent">
		                If 
		                you do not have a PubMan account yet, please contact: <a href="mailto:pubman-support@gwdg.de">PubMan Support.</a>
		                <span style="" lang="EN-GB">
		                    
		                    
		                </span>
		            </p>
		            <h4><b style="">2.2.<a name="User_Roles_and_Workflows"></a>User Roles and Workflows</b></h4>
		            <p class="MsoNormal">
		                <span style="" lang="EN-GB"> At the moment, two workflows are implemented: <a href="#Simple_Workflow">Simple
		                        Workflow</a>, the more basic form, and <a href="#Standard_Workflow">Standard
		                        Workflow</a>, where each publication needs to be controlled and
		                    approved by an authorised person, e.g. a librarian, before it becomes
		                    publicly visible.</span>
		            </p>
		            <p class="MsoNormal">
		                <span style="" lang="EN-GB"> In addition to the diverse workflows, there are also different user
		                    roles with varying privileges. These roles principally remain the same,
		                    whereas their rights may vary according to the workflow. A "depositor",
		                    for instance, is always a person that submits data (e.g. a scientist).
		                    A moderator, on the other hand, cannot submit data &#8211; s/he can 
		                    only modify or complement them (e.g. a librarian). </span>
		            </p>
		            <p class="MsoNormal">
		                <span style="" lang="EN-GB"> Since roles within the institutes are often assigned in quite different
		                    ways, PubMan roles can be accordingly combined. For example, the same
		                    user may be depositor and moderator in one.</span>
		            </p>
		            <h4><b style="">2.2.1.<a name="Workflows"></a>Workflows</b></h4>
		            <p class="MsoNormal">
		                <b style="">2.2.1.1.</b>
		                <b style=""><span style="" lang="EN-GB"><a name="Standard_Workflow"></a>Standard Workflow
		                        
		                        
		                    </span></b>
		            </p>
		            <p class="MsoNormal">
		                <span style="" lang="EN-GB">In
		                    the standard workflow, the depositor creates items (item state:
		                    "pending") and submits them to the moderator for quality check (item
		                    state: "submitted"). After evaluating the item, the moderator can
		                    either accept the item &#8211; making it publicly available (item
		                    state: "released") &#8211; or send it back to the depositor for
		                    rework, in case the item does not conform to the quality standards
		                    (item state: "in rework"). Released items can be modified by the
		                    moderator and the depositor. After a relesed item has been modified by
		                    the depositor, this item will again be set to item state "pending".</span>
		            </p>
		            <p class="MsoNormal">
		                <b style="">2.2.1.2.<a name="Simple_Workflow"></a>Simple Workflow
		                    
		                    
		                </b>
		            </p>
		            <p class="MsoNormal">
		                <span style="" lang="EN-GB">In
		                    this workflow, the depositor can create items (item state: "pending")
		                    and then release them for public view (item state: "released"). After
		                    an item has been released by the depositor, it can be modified by the
		                    depositor and the moderator. After modifying an item, the moderator
		                    accepts it (item state: "released"). The option "send back for rework"
		                    is not available in the simple workflow.</span>
		            </p>
		            <p class="MsoNormal">
		                <b style="">2.2.2.<a name="PubMan_User_Roles"></a>PubMan User Roles
		                    
		                    
		                </b>
		            </p>
		            As mentioned before, two user roles are currently implemented in
		            PubMan: depositor and moderator. However, this concept is expanded at
		            the institutes' request. Under the following link you find an overview 
		            of user roles and workflows: <a href="http://colab.mpdl.mpg.de/mediawiki/PubMan_Workflows">http://colab.mpdl.mpg.de/mediawiki/PubMan_Workflows</a>
		            <ul>
		            </ul>
		            <h3><span class="titlemark"><span style="" lang="EN-GB">3. </span></span><a name="Functionalities"></a><span style="" lang="EN-GB">PubMan Functionalities
		                    
		                    
		                </span></h3>
		            <p class="noindent">
		                <span style="" lang="EN-GB">Many
		                    PubMan functionalities are not reserved for logged-in users, but are
		                    also open to not logged-in users. Below, there is an overview of all
		                    functionalities available to not logged-in users.</span>
		            </p>
		            <h4><span class="titlemark">3.1</span><a name="Search_Possibilities"></a>Search
		                Possibilities in PubMan</h4>
		            <p class="noindent">
		                <span style="" lang="EN-GB">Three
		                    different search modes are provided:</span>
		            </p>
		            <ul>
		                <li>
		                    Quick Search
		                </li>
		                <li>
		                    Advanced Search
		                </li>
		                <li>
		                    Organization Search
		                </li>
		            </ul>
		            <h4><span class="titlemark">3.1.1.</span><a name="Quick_Search"></a>Quick Search</h4>
		            <p class="noindent">
		                <span style="" lang="EN-GB">Please
		                    enter
		                    one or more search terms and click on the "go" button beside the search 
		                    field to perform a search. 
		                    
		                    
		                </span>
		            </p>
		            <p class="indent">
		                <span style="" lang="EN-GB">The
		                    following Boolean operators
		                    are supported:</span>
		            </p>
		            <ul>
		                <li>
		                    AND
		                </li>
		                <li>
		                    OR
		                </li>
		                <li>
		                    NOT
		                </li>
		            </ul>
		            <p class="indent">
		                <span style="" lang="EN-GB">If
		                    you want to
		                    search within the metadata and the full text file attached to a record,
		                    please check the checkbox next to "Include Files". Please
		                    note that only the 
		                    following mime types are indexed: 
		                    
		                    
		                </span>
		            </p>
		            <ul>
		                <li>
		                    application/pdf
		                </li>
		                <li>
		                    application/msword
		                </li>
		                <li>
		                    text/xml
		                </li>
		                <li>
		                    application/xml
		                </li>
		                <li>
		                    text/plain
		                </li>
		            </ul>
		            Additionally truncation symbols can be used in all searches. Supported
		            are "?" for one or no characters and "*" for cero until unlimited
		            characters. Please note, that truncation at the beginning of the word
		            is not allowed.
		            <br/>
		            <h4><span class="titlemark">3.1.2.</span><a name="Advanced_Search"></a>Advanced
		                Search</h4>
		            <p class="noindent">
		                <span style="" lang="EN-GB">You
		                    can
		                    either search using single search options (Any field, Persons,
		                    Organizations,
		                    etc.) or you can combine the search options with "AND", "OR" or
		                    "NOT". By default, the
		                    operator "AND" is set between the fields. An overview of available
		                    search fields and their indexes can be found under:<a href="http://colab.mpdl.mpg.de/mediawiki/PubMan_Indexing"> http://colab.mpdl.mpg.de/mediawiki/PubMan_Indexing</a></span>
		            </p>
		            <h4><span class="titlemark"><span style="" lang="EN-GB">3.1.2.1.</span></span><a name="Date_Search"></a><span style="" lang="EN-GB">Date Search
		                    
		                    
		                </span></h4>
		            To search for dates, enter the date in the given format YYYY-MM-DD. As
		            in the submission process, you may truncate the date and give only YYYY
		            or YYYY-MM.
		            <br/>
		            <p>
		                Some additional hints:
		            </p>
		            <p>
		                If you search for a specific date, please enter it in both fields: for
		                example, startdate "2009-06-15" to enddate "2009-06-15".
		            </p>
		            <p>
		                If you enter a date only in the startdate e.g. "2009-06-15", you will get all records valid  after the 15th of June 2009.
		            </p>
		            <p>
		                If you search for a time range, e.g.  startdate "2008" to enddate "2009", it
		                will automatically search for 2008-01-01, means from the beginning of
		                the year 2008, to 2009-12-31, means to the end of the year 2009.
		            </p>
		            <h4><span class="titlemark"><span style="" lang="EN-GB">3.1.3. </span></span><a name="Organization_Search"></a><span style="" lang="EN-GB">Organization
		                    Search
		                    
		                    
		                </span></h4>
		            <p class="noindent">
		                Please
		                select the organization (or the sub-organization) you would like to be
		                informed about. All PubMan items affiliated with this institute will be
		                displayed. Clicking on the description button will provide you with
		                more
		                information on this organization.
		                <span style="" lang="EN-GB">
		                	<h4>
		                		<span class="titlemark">
		                			<span style="" lang="EN-GB">3.2. </span>
		                		</span>
		                		<a name="Search_Results"></a>
		                		<span style="" lang="EN-GB">Search results</span>
		                	</h4>
		                    <p class="noindent">
		                        <span style="" lang="EN-GB">If
		                            you
		                            clicked on "Include Files", there will also be records displayed, where
		                            the
		                            search term was found within the attached full text. You can change the
		                            display
		                            of single items or of the whole list, choosing between short and medium
		                            view. Please note that full text parts with the search term highlighted
		                            are only visible in medium view. If you click on the title of the
		                            publication, the full item view will
		                            be 
		                            displayed. 
		                        </span>
		                    </p>
		                    <h4><span class="titlemark"><span style="" lang="EN-GB">3.2.1. </span></span><a name="Export"></a><span style="" lang="EN-GB">Export
		                            
		                            
		                        </span></h4>
		                    <p class="noindent">
		                        <span style="" lang="EN-GB">You
		                            can
		                            either retrieve the selected items in a citation style (e.g. APA) or
		                            you can export the items to an export format (e.g. EndNote Export 
		                            Format). <span style="color: red;">
		                                
		                                
		                            </span></span>
		                    </p>
		                    <h4><span class="titlemark"><span style="" lang="EN-GB">3.2.1.1.</span></span><a name="Send_Export_via_E-Mail"></a><span style="" lang="EN-GB">Send Export via E-Mail
		                            
		                            
		                        </span></h4>
		                    <p class="noindent">
		                        You
		                        can send exports per E-Mail.<span style="" lang="EN-GB"> Please specify the receiver of the E-Mail, as well as the E-Mail 
		                            address the receiver can reply to. 
		                            
		                            
		                        </span>
		                    </p>
		                    <p class="indent">
		                        <span style="" lang="EN-GB">If
		                            you want to
		                            send the mail to more than one E-Mail addresses, please separate them
		                            with a 
		                            comma and a blank. 
		                            
		                            
		                        </span>
		                    </p>
		                    <h4><span class="titlemark"><span style="" lang="EN-GB">3.3. </span></span><a name="Basket"></a>Basket</h4>
		                    <p class="noindent">
		                        In PubMan, you can select any number
		                        of items from a list and compile them to a basket. To do so, please
		                        check the box next to the desired items and then click on "Add to
		                        basket". The number of items in the basket is always visible next to
		                        the "Basket" link. Please note, that the basket is only available for
		                        the particular session and cannot be saved. It is possible to export
		                        all items in the basket.<span style="" lang="EN-GB">
		                            
		                            
		                        </span>
		                    </p>
		                    <h4><span class="titlemark"><span style="" lang="EN-GB">3.4. </span></span><a name="View_Item"></a>View
		                        Item&#160;</h4> On the detailed item view you have the possibility to use different <span style="font-weight: bold;">Social Bookmark&#160;Services</span>. 
		                    Currently PubMan offers bookmarking options for <span style="font-weight: bold;">Delicious</span>, <span style="font-weight: bold;">CiteULike</span> and <span style="font-weight: bold;">Connotea</span>. If an <span style="font-weight: bold;">ISI Web of Knowledge Identifier</span> is stored with the item, that Identifier serves as a link to an ISI
		                    &#160;Search. A query for the same item in ISI offers the
		                    possibility
		                    for the user to&#160;retrieve some additional information about the
		                    displayed article. In case of a business card symbol is placed after 
		                    the authors name, this icon leads to the <span style="font-weight: bold;">Researcher Portfolio</span> page. On this page the user gets further information about the author.
		                    All publications, which are stored in PubMan, are listed there.
		                    Furthermore the Researcher Portfolio offers the possibility to look up 
		                    the same author in <span style="font-weight: bold;">WorldCat</span> and <span style="font-weight: bold;">Google Scholar</span>.
		                    In case a house symbol is placed after the name of the organization the
		                    author is belonging to, this icon leads to the organization
		                    description.<h4><span class="titlemark"><span style="" lang="EN-GB">3.4.1. </span></span><a name="View_Release_History"></a><span style="" lang="EN-GB">View Release
		                            History
		                            
		                            
		                        </span></h4>
		                    <p class="noindent">
		                        All released versions of an item are
		                        displayed here, so that you can track all changes on an item.<span style="" lang="EN-GB">
		                            
		                            
		                        </span>
		                    </p>
		                    <h4><span class="titlemark"><span style="" lang="EN-GB">3.4.2. </span></span><a name="View Item_Revisions"></a><span style="" lang="EN-GB">View Item
		                            Revisions
		                            
		                            
		                        </span></h4>
		                    <span class ="noindent">
		                        <span style="" lang="EN-GB">A
		                            revision is an intellectually modified or re-processed version, which
		                            is linked to the original item. If you click on "View Item Revisions",
		                            all revisions of the item are displayed.
		                        </span>
		                    </span>
		                </span>
		            </p>
		            <h4>
		            	<span class="titlemark">
		            		<span style="" lang="EN-GB">3.4.3. </span>
		            	</span>
		            	<a name="View_Item_Statistics"></a>
		            	<span style="" lang="EN-GB">View item Statistics</span>
		            </h4>
		            <p class="noindent">
		                <span style="" lang="EN-GB">Here
		                    you can
		                    see how often the item was retrieved and how often the attached full
		                    text file was
		                    downloaded. Please note that the statistics are updated on a nightly 
		                    basis. 
		                </span>
		            </p>
		            <p class="noindent">
		                <b style="">
		                <span style="" lang="EN-GB">3.4.4.<a name="Item_Log"></a>Item Log (Registered Users only)</span>
		                </b>
		            </p>
		            <p class="noindent">
		                <span style="" lang="EN-GB">This
		                    is an
		                    option reserved to registered users, where you can see a full history
		                    of
		                    actions performed on the item, as logged by the system.</span>
		            </p>
		            <p class="noindent">
		                <b style="">
		                <span style="" lang="EN-GB">3.4.5.<a name="Local_Tags"></a> Local Tags
		                        (Registered Users only)
	                    </span></b>
		            </p>
		            <p class="noindent">
		                <span style="" lang="EN-GB">Here
		                    you can assign tags to publications in order to create own sets for
		                    specific purposes, such us "my best publications".</span>
		            </p>
		            <p class="noindent">
		                <span style="" lang="EN-GB"><h3><span class="titlemark"><span style="" lang="EN-GB">4. </span></span><a name="Submission"></a><span style="" lang="EN-GB">Submission&#160;
		                            
		                            
		                        </span></h3>
		                    <p class="noindent">
		                        <span style="" lang="EN-GB">In
		                            PubMan<span style="color: red;"><span style="color: rgb(0, 0, 0);">,</span></span>you
		                            may choose between three different ways of
		                            submitting content.
		                            
		                            
		                        </span>
		                    </p>
		                </span>
		                <span style="" lang="EN-GB"></span>
		            </p>
		            <ul>
		                <li>
		                    <b style=""><span style="" lang="EN-GB">Full
		                            submission:</span></b>
		                    <span style="" lang="EN-GB"> All metadata fields available for the genre you have selected are 
		                        shown. 
		                        
		                        
		                    </span>
		                </li>
		                <li>
		                    <b style=""><span style="" lang="EN-GB">Easy
		                            Submission:</span></b>
		                    <span style="" lang="EN-GB"> a stepwise manual submission wizard.
		                        
		                        
		                    </span>
		                </li>
		                <li>
		                    <b style=""><span style="" lang="EN-GB">Import:</span></b>
		                    <span style="" lang="EN-GB"> Here you can upload a Bib<span class="tex">T</span><span class="e">e</span><span class="tex">X</span> record or fetch metadata and full
		                        text(s) from arXiv, SPIRES, BioMed Central or PubMed Central.</span>
		                </li>
		                <li>
		                    <span style="font-weight: bold;">Multiple
		                        Import:</span>
		                    Here you have the possibility to do a mass import
		                    of data from&#160;<span style="" lang="EN-GB">Web
		                        of 
		                        Science, EndNote, BibTeX or RIS. <span style="font-style: italic;">Note
		                            for multiple EndNote Imports: All authors of an item are affiliated to
		                            "Max Planck Society" by default. Desired changes/modifications
		                            regarding the affiliated Organizational Unit of an author have to be
		                            done manually for each respective item after the import.&#160;</span></span>
		                </li>
		            </ul>
		            <p class="noindent">
		                <span style="" lang="EN-GB"><h4><span class="titlemark"><span style="" lang="EN-GB">4.1. </span></span><a name="Create_Item"></a><span style="" lang="EN-GB">Create
		                            Item&#160;
		                            
		                            
		                        </span></h4>
		                    <p class="noindent">
		                        <span style="" lang="EN-GB">Before
		                            you create a new
		                            item, please select a submission method and then the collection, in
		                            which
		                            you would like to add the item, by clicking on the name of the 
		                            collection. 
		                            
		                            
		                        </span>
		                    </p>
		                    <h4>4.2.<a name="Edit_Item"></a>Create /
		                        Edit Item&#160;<span style="" lang="EN-GB">
		                            
		                            
		                        </span></h4>
		                    <p class="noindent">
		                        <span style="" lang="EN-GB">Please
		                            specify the document type (Genre) of your item before you start
		                            submitting the publication
		                            data. According to this genre, you are then provided with a submission
		                            mask.</span>
		                        Please note that you can only change the genre until you have saved,
		                        submitted or
		                        released the item once; PubMan will then delete all submission fields
		                        that are
		                        unnecessary for this genre.<span style="" lang="EN-GB">
		                            
		                            
		                        </span>
		                    </p>
		                    <p class="indent">
		                        <span style="" lang="EN-GB">Please
		                            fill out all
		                            fields marked with a star, as they are the minimum data required to
		                            submit an item. Should you leave one of these fields empty, a 
		                            validation message is displayed. Also see: <a href="#Validate_Item">Validation</a>.
		                            
		                            
		                        </span>
		                    </p>
		                    <h4><span class="titlemark"><span style="" lang="EN-GB">4.2.1.</span></span><a name="Submitting_Persons_and_Organizations"></a><span style="" lang="EN-GB">Submitting Persons and
		                            Organizations
		                            
		                            
		                        </span></h4>
		                    <p class="noindent">
		                        <span style="" lang="EN-GB">There
		                            are two ways of submitting person names. You may submit them
		                            separately, using the "persons" mask and clicking on the "plus" symbol
		                            to
		                            add further persons.If the name you start typing is recognized by the
		                            system, an auto-suggestion list is displayed. You can either select a
		                            name from the list or press ESC to close it.
		                            <br/>
		                            To enter multiple persons at once click on the "add multiple" link: a
		                            text field appears,
		                            where you may type or copy/paste a list of persons. This list is then
		                            parsed by the system.
		                        </span>
		                        <span style="" lang="EN-GB">Please
		                            note that at least one person has to be
		                            affiliated
		                            to an organization, otherwise the item cannot be created.
		                            <br/>
		                            
		                            
		                        </span>
		                    </p>
		                    <h4>4.2.2. <a name="Submitting_Journal_Names"></a>Submitting
		                        Journal Names</h4>
		                    <p class="indent">
		                        <span>If
		                            you would like to give a journal name as a source, please choose
		                            "Journal" as "Source Genre". When you start typing the journal name,
		                            PubMan will start suggesting journal names accordingly. You may either
		                            choose one of the suggestions by selecting it and clicking on it or
		                            pressing "enter", or you may enter a new journal name. If you do not 
		                            want to select a suggested name, please press ESC to close the list. </span>
		                    </p>
		                    <h4>4.2.3.<a name="Entering_the_Publication_Language"></a> Entering
		                        the Publication Language</h4>
		                    <p class="indent">
		                        Auto-suggestion
		                        lists are
		                        also available in the publication language field. You may enter the
		                        language in German or English; both languages are recognized by the
		                        system.
		                    </p>
		                    <h4><span class="titlemark">4.2.4.</span><a name="Entering_Dates"></a>Entering
		                        Dates</h4><span>A date should have the following format: YYYY-MM-DD.
		                        However, you may
		                        also enter terms like "yesterday", "last year", or similar: PubMan will
		                        convert them into the right format.</span></span>
		                <br/>
		                <span style="font-style: italic;">Note for a date entry for publication
		                    types "Series" and "Journal": For this both genres the given date
		                    relates to the start of the respective publication.</span>
		            </p>
		            <p class="noindent">
		                <span style="" lang="EN-GB"><span></span><span style="" lang="EN-GB">
		                        
		                        
		                    </span></span>
		                <span style="" lang="EN-GB"><h4><span class="titlemark">4.2.5.</span><a name="Providing_Rights_Information"></a>Provide Rights Information</h4><span>It's possible to provide
		                        several rights information for each uploaded file and locator. You can
		                        appoint access rights ("public", "private", "restricted") and assign
		                        them to selected user groups respectively. There is an option to
		                        provide a "Rights Statement" (freetext field) and a "Copyright
		                        Date" (date format). Additional it's possible to choose a Creative
		                        Commons License (</span></span>
		                <a href="http://creativecommons.org/">http://creativecommons.org/</a>)
		                for the file. All these data are optional.&#160;Please be aware
		                that you have checked these rights information before!
		            </p>
		            <h4><span class="titlemark"><span style="" lang="EN-GB">4.3. </span></span><a name="Create_New_Revision"></a><span style="" lang="EN-GB">Create New Revision
		                    
		                    
		                </span></h4>
		            <p class="noindent">
		                A new revision is an intellectually
		                revised version of a publication (eg. first pre print, then post
		                print). Please note that each revision is a new, separate item, which
		                is linked to the original item ("is revision of").
		                Before releasing the new revision, you can give a comment on creating a
		                new revision of the item.<span style="" lang="EN-GB"><span style="background: red none repeat scroll 0% 50%; -moz-background-clip: initial; -moz-background-origin: initial; -moz-background-inline-policy: initial;"></span>
		                    
		                    
		                </span>
		            </p>
		            <h4>4.4.<a name="Validate_Item"></a>Validate
		                Item&#160;<span style="" lang="EN-GB">
		                    
		                    
		                </span></h4>
		            <p class="noindent">
		                <span style="" lang="EN-GB">You
		                    can validate
		                    the item in order to check if it meets the requirements of the 
		                    collection. 
		                    
		                    
		                </span>
		            </p>
		            <p class="indent">
		                <span style="" lang="EN-GB">Please
		                    note
		                    that you can&#8217;t submit an item that doesn&#8217;t meet the
		                    requirements of the 
		                    collection. 
		                    
		                    
		                </span>
		            </p>
		            <h4>4.5.<a name="Save_Item"></a>Save
		                Item&#160;<span style="" lang="EN-GB">
		                    
		                    
		                </span></h4>
		            <p class="noindent">
		                <span style="" lang="EN-GB">After
		                    clicking on the "save" button, your item obtains the status "pending"
		                    and can
		                    only be seen by you. This might be useful if you want to complete your
		                    entry at
		                    a later time. You can then access and edit your item by clicking on "My 
		                    Items". 
		                    
		                    
		                </span>
		            </p>
		            <h4><span class="titlemark"><span style="" lang="EN-GB">4.6. </span></span><a name="Delete Item"></a><span style="" lang="EN-GB">Delete
		                    Item&#160;
		                    
		                    
		                </span></h4>
		            <p class="noindent">
		                <span style="" lang="EN-GB">Please
		                    note that only items in state "pending" can be deleted. "Released"
		                    items can only be withdrawn, since they have been assigned a PID for 
		                    citation purposes. 
		                    
		                    
		                </span>
		            </p>
		            <h4><span class="titlemark"><span style="" lang="EN-GB">4.7. </span></span><a name="Submit_Item"></a><span style="" lang="EN-GB">Submit
		                    Item&#160;
		                    
		                    
		                </span></h4>
		            <p class="noindent">
		                <span style="" lang="EN-GB">In
		                    the 
		                    Standard Workflow, the depositor can only submit his items. 
		                    
		                    
		                </span>
		            </p>
		            <p class="noindent">
		                <span style="" lang="EN-GB">If
		                    you go on "submit item", your data will first be validated. If your
		                    item is valid, you
		                    are guided to a mask, where you can state a comment to your submission.
		                    After
		                    you have submitted your item, it is sent to the moderator of your
		                    collection,
		                    who then checks the data and either releases it for public view or
		                    sends it
		                    back to you for corrections. Items that have been sent back to the
		                    depositor
		                    are labeled as "in rework" and can be found under the corresponding 
		                    filter under "My items". 
		                    
		                    
		                </span>
		            </p>
		            <h4><span class="titlemark"><span style="" lang="EN-GB">4.7.1.</span></span><a name="Submission_Comment"></a><span style="" lang="EN-GB">Submission Comment
		                    
		                    
		                </span></h4>
		            <p class="noindent">
		                <span style="" lang="EN-GB">In
		                    the
		                    comment field you can give a comment on your submission; the user
		                    following you
		                    in the workflow will be able to read it.
		                    
		                    
		                </span>
		            </p>
		            <h4><b style=""><span lang="EN-GB">4.8.<a name="Release_Item"></a>Release Item&#160;
		                        
		                        
		                    </span></b></h4>
		            <p class="noindent">
		                <span style="" lang="EN-GB">In
		                    the
		                    simple workflow, you don&#8217;t have to submit items first; they
		                    can be directly 
		                    released and made publicly available. 
		                    
		                    
		                </span>
		            </p>
		            <h4><span class="titlemark"><span style="" lang="EN-GB">4.8.1.<a name="Release_Comment"></a></span></span><span style="" lang="EN-GB">Release Comment
		                    
		                    
		                </span></h4>
		            <p class="noindent">
		                <span style="" lang="EN-GB">In
		                    the
		                    comment field you can give a comment on your release; the user
		                    following you
		                    in the workflow will be able to read it.
		                    
		                    
		                </span>
		            </p>
		            <h3><span lang="EN-GB">5. <a name="Quality Assurance in PubMan"></a>Quality
		                    Assurance in PubMan
		                    
		                    
		                </span></h3>
		            <p class="noindent">
		                <span style="" lang="EN-GB">In
		                    order to guarantee the high quality of data in PubMan, a quality
		                    assurance workflow is implemented, which ensures that specific quality
		                    criteria are met. Responsible for the quality assurance is the
		                    moderator, who has a wide spectrum of means at her/his disposal. S/he
		                    can modify the items or send them back to the depositor for re-work; if
		                    s/he is content with the quality, s/he can accept and release the
		                    items.
		                    A further quality assurance option, which is only available to the
		                    depositor, is the withdrawal of an item: an alternative to deleting the
		                    item that serves the purposes of long-term storage and guarantees
		                    further citability of the item.
		                    
		                    
		                </span>
		            </p>
		            <h4><b style=""><span lang="EN-GB">5.1.<a name="Send_Back_for_Re-Work"></a>Send Back for
		                        Re-Work&#160;
		                        
		                        
		                    </span></b></h4>
		            <p class="noindent">
		                <span style="" lang="EN-GB">In
		                    the 
		                    standard workflow, a moderator can send "submitted" items back to </span>
		                <span style="" lang="EN-GB"></span>
		                <span style="" lang="EN-GB">the owner</span>
		                <span style="" lang="EN-GB"> for
		                    re-work</span>
		                <span style="" lang="EN-GB">,
		                    in case they do not meet with the Quality Assurance standards. Items
		                    in state "in rework" will be visible by both the moderator and the
		                    depositor,
		                    but can only be edited by the depositor, until they are once more
		                    submitted to
		                    the moderator for quality check.
		                    
		                    
		                </span>
		            </p>
		            <h4><span class="titlemark"><span style="" lang="EN-GB">5.2. </span></span><a name="Modify_Item"></a><span style="" lang="EN-GB">Modify
		                    Item&#160;
		                    
		                    
		                </span></h4>
		            <p class="noindent">
		                <span style="" lang="EN-GB">"Pending"
		                    items can be edited by their owner until s/he is satisfied with them
		                    and submits them. "Submitted" or "released" items (depending on the
		                    workflow) can be changed by the moderator, "releasesd" items can also 
		                    be modified by the depositor. 
		                    
		                    
		                </span>
		            </p>
		            <h4><span class="titlemark"><span style="" lang="EN-GB">5.3. </span></span><a name="Accept_Item"></a><span style="" lang="EN-GB">Accept
		                    Item&#160;
		                    
		                    
		                </span></h4>
		            <p class="noindent">
		                <span style="" lang="EN-GB">If
		                    you want
		                    to release the item you have modified and make it publicly available 
		                    again, please click on "accept". 
		                    
		                    
		                </span>
		            </p>
		            <h4><span class="titlemark"><span style="" lang="EN-GB">5.3.1.</span></span><a name="Acceptance_Comment"></a><span style="" lang="EN-GB">Acceptance Comment
		                    
		                    
		                </span></h4>
		            <p class="noindent">
		                <span style="" lang="EN-GB">In
		                    the
		                    comment field you can give a comment on accepting the item, which is 
		                    then visible in the item's release history. 
		                    
		                    
		                </span>
		            </p>
		            <h4><span class="titlemark"><span style="" lang="EN-GB">5.4. </span></span><a name="Withdraw Item"></a><span style="" lang="EN-GB">Withdraw
		                    Item&#160;
		                    
		                    
		                </span></h4>
		            <p class="noindent">
		                <span style="" lang="EN-GB">"Released"
		                    PubMan items cannot be deleted anymore. They can only be withdrawn, so
		                    that the metadata is further on available (without any full text) and
		                    the items remain quotable. "Withdrawn" items are not searchable and
		                    they can only be accessed through their ID.
		                    Items can only be withdrawn by their owner (depositor). Please state
		                    the reason for withdrawing the item.
		                    
		                    
		                </span>
		            </p>
		            <h3><span class="titlemark"><span style="" lang="EN-GB">6. </span></span><a name="Tools for Data Management"></a><span style="" lang="EN-GB">Tools for Data Management
		                    
		                    
		                </span></h3>
		            <p class="noindent">
		                As a depositor, you can manage your
		                items through the depositor workspace (&#8220;My Items"). There you
		                can find an overview of your items and your managing options. You can,
		                for instance, change the viewing mode, filter the items according to
		                their state, sort them based on various criteria, export selected items
		                or create a basket.<span style="" lang="EN-GB">
		                    
		                    
		                </span>
		            </p>
		            <h4><span class="titlemark"><span style="" lang="EN-GB">6.1. </span></span><a name="My Items"></a><span style="" lang="EN-GB">My Items
		                    
		                    
		                </span></h4>
		            <p class="noindent">
		                <span style="" lang="EN-GB">In
		                    the "My Items" workspace, the depositor can find all the items he has
		                    created. He may filter them according to their state and perform
		                    different actions, e.g. complete items in status "pending" or enrich
		                    entries that have been sent back for rework. Another filter shows
		                    imported items sorted by date.
		                    
		                    
		                </span>
		            </p>
		            <h4><span class="titlemark"><span style="" lang="EN-GB">6.1.1. </span></span><a name="Item_State"></a><span style="" lang="EN-GB">Item State
		                    
		                    
		                </span></h4>
		            <p>
		                If
		                you want items of another state to be displayed, please click on
		                "filter" and open the pull-down list. You may choose one of the
		                following states:
		            </p>
		            <ul>
		                <li>
		                    "pending":&#160;
		                    These are items that you created and then saved, because your
		                    submission was
		                    not complete yet. They still have to be "submitted" or "released", when
		                    you have finished editing them.
		                </li>
		                <li>
		                    "submitted":
		                    This item-state&#160;exists only
		                    in the standard workflow. The data entry for these items is complete
		                    and
		                    they are "submitted" to the moderator of your collection for formal
		                    check.
		                </li>
		                <li>
		                    "released":
		                    Items in state "released" are publicly visible. They can be modified by
		                    you and the moderator of your collection.
		                </li>
		                <li>
		                    "withdrawn":
		                    Items in state "withdrawn" are not accessible via search or
		                    organizations search. They are only visible to you as their creator -
		                    alternatively, they can be accessed via their specific URL.
		                </li>
		                <li>
		                    "in
		                    rework": This item-state exists only in the standard workflow. These
		                    items have been sent back to you by the moderator for rework. It is
		                    important that you do not forget to re-submit these items, when you
		                    have finished editing them.&#160;<span style="" lang="EN-GB">
		                        
		                        
		                    </span>
		                </li>
		            </ul>
		            <h4><span class="titlemark"><span style="" lang="EN-GB">6.1.2.</span></span><a name="Sort_by"></a><span style="" lang="EN-GB">Sort by
		                    
		                    
		                </span></h4>
		            <p class="noindent">
		                <span style="" lang="EN-GB">Please
		                    specify the sorting option for your item list by selecting it in the
		                    pull-down
		                    list. You can switch between "ascending" and "descending" sorting order
		                    by 
		                    clicking on the link next to the pull down list. 
		                    
		                    
		                </span>
		            </p>
		            <h4><span class="titlemark"><span style="" lang="EN-GB">6.2. </span></span><a name="Quality_Assurance_Workspace"></a><span style="" lang="EN-GB">Quality
		                    Assurance Workspace&#160;
		                    
		                    
		                </span></h4>
		            <p class="noindent">
		                <span style="" lang="EN-GB">In
		                    the Quality
		                    Assurance Workspace all items relevant for the moderator are listed.
		                    Items in
		                    state "submitted" are items that need to be quality checked. The
		                    moderator will
		                    judge their quality and then either accept them or send them back for
		                    rework to
		                    the depositor.&#160;</span>
		            </p>
		            <p class="noindent">
		                <span style="" lang="EN-GB">Please
		                    note that this only applies to the Standard Workflow. In the Simple 
		                    Workflow there are no "submitted" items. 
		                    
		                    
		                </span>
		            </p>
		            <h4><span class="titlemark"><span style="" lang="EN-GB">6.2.1.</span></span><a name="Item_State_QA"></a><span style="" lang="EN-GB">Item</span><span style="" lang="EN-GB"> State</span><span style="" lang="EN-GB"></span></h4>
		            <p>
		                If
		                you want items of another state to be
		                displayed, please click on "filter" and open the pull-down list. You
		                may choose one of the following states:
		            </p>
		            <ul>
		                <li>
		                    "submitted":
		                    &#160;This item-state exists only in
		                    the standard workflow. These are items that still need to be formally
		                    checked by you. After that, you can either edit them yourself and
		                    "release" them for the public view, or send them back to the depositor
		                    for
		                    rework.
		                </li>
		                <li>
		                    "released":
		                    Items in state
		                    "released" are publicly visible and can be modified by you, if needed.
		                </li>
		                <li>
		                    "in rework": This item-state exists only in the
		                    standard workflow. These are the items that you have sent back to the
		                    depositor for rework.&#160;<span style="" lang="EN-GB">
		                        
		                        
		                    </span>
		                </li>
		            </ul>
		            <h4><span class="titlemark"><span style="" lang="EN-GB">6.2.2.</span></span><a name="Sort_by_QA"></a><span style="" lang="EN-GB">Sort by
		                    
		                    
		                </span></h4>
		            <p class="noindent">
		                <span style="" lang="EN-GB">Please
		                    specify the sorting option for your item list by selecting it in the
		                    pull-down
		                    list. You can switch between "ascending" and "descending" sorting order
		                    by
		                    clicking on the link next to the pull down list.</span>
		            </p>
		            <h4><span class="titlemark">6.3. </span><a name="Import Workspace"></a>Import Workspace.</h4>
		            <p class="noindent">
		                In the import workspace you can check
		                the status
		                of your import(s) and do batch operations. If you use the remove
		                buttons, the import task will be delete (not the items). If you use the
		                delete button, the items will be deleted if they still are in state
		                "pending".
		            </p>
		        </div>
		    </body>
		</html>
	</f:view>
</jsp:root>