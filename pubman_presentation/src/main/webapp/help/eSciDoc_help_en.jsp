<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
	xmlns:f="http://xmlns.jcp.org/jsf/core"
	xmlns:h="http://xmlns.jcp.org/jsf/html"
	xmlns:ui="http://xmlns.jcp.org/jsf/facelets"
	xmlns:p="http://primefaces.org/ui"
	xmlns:pt="http://xmlns.jcp.org/jsf/passthrough">
<h:head>
	<title>PubMan Online Help</title>
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
	<meta name="generator" content="TeX4ht (http://www.cse.ohio-state.edu/~gurari/TeX4ht/)" />
	<meta name="originator"	content="TeX4ht (http://www.cse.ohio-state.edu/~gurari/TeX4ht/)" />
	<meta name="src" content="eSciDoc_help_de.tex" />
	<meta name="date" content="2008-10-08 13:10:00" />
	<ui:include src="/header/ui/StandardImports.jspf" />
</h:head>
<body id="helppage" lang="EN-GB" dir="ltr">
	<f:view locale="#{InternationalizationHelper.userLocale}">
		<div class="maketitle wrapper"
			style="padding: 0.74em 0.74em 3em 0.74em; font-size: 129% !important; width: auto;">
			<h1>PubMan Online Help</h1>
			<p class="noindent">
				<span class="cmr-12">January 16, 2009</span>
			</p>
			<h2>Contents</h2>
			<ol>
				<li><a href="#HomePage">About PubMan</a></li>
				<li><a href="#Preface">Preface</a>
					<ul>
						<li><span class="titlemark">2.1.</span><a href="#Log_in">Log
								In</a></li>
						<li><span class="titlemark">2.2.</span><a
							href="#User_Roles_and_Workflows">User Roles and Workflows</a>
							<ul>
								<li><span class="titlemark">2.2.1.</span><a
									href="#Workflows">Workflows</a>
									<ul>
										<li><span class="titlemark">2.2.1.1.</span><a
											href="#Standard_Workflow">Standard Workflow</a></li>
										<li><span class="titlemark">2.2.1.2.</span><a
											href="#Simple_Workflow">Simple Workflow</a></li>
									</ul></li>
								<li><span class="titlemark">2.2.2.</span><a
									href="#PubMan_User_Roles">PubMan User Roles</a></li>
							</ul></li>
					</ul></li>
				<li><a href="#PubMan_Functionalities">PubMan
						Functionalities</a>
					<ul>
						<li><span class="titlemark">3.1.</span><a
							href="#Search_Possibilities">Search Possibilities in PubMan</a>
							<ul>
								<li><span class="titlemark">3.1.1.</span><a
									href="#Quick_Search">Quick Search</a></li>
								<li><span class="titlemark">3.1.2.</span><a
									href="#Advanced_Search">Advanced Search</a>
									<ul>
										<li><span class="titlemark">3.1.2.1.</span><a
											href="#Date_Search">Date Search</a></li>
									</ul></li>
								<li><span class="titlemark">3.1.3.</span><a
									href="#Organization_Search">Organization Search</a></li>
							</ul></li>
						<li><span class="titlemark">3.2.</span><a
							href="#Search_Results">Search Results</a>
							<ul>
								<li><span class="titlemark">3.2.1.</span><a href="#Export">Export</a>
									<ul>
										<li><span class="titlemark">3.2.1.1.</span><a
											href="#Send_Export_via_E-Mail">Send Export via E-Mail</a></li>
									</ul></li>
							</ul></li>
						<li><span class="titlemark">3.3.</span><a href="#Basket">Basket</a></li>
						<li><span class="titlemark">3.4.</span><a href="#View_Item">View
								Item</a>
							<ul>
								<li><span class="titlemark">3.4.1.</span><a
									href="#View_Release_History">View release History</a></li>
								<li><span class="titlemark">3.4.3.</span><a rel="tag"
									href="#View_Item_Statistics">View Item Statistics</a></li>
								<li><span class="titlemark">3.4.4.</span><a
									href="#Item_Log">Item Log (Registered Users only)</a></li>
								<li><span class="titlemark">3.4.5.</span><a
									href="#Local_Tags">Local Tags (Registered Users only)</a></li>
							</ul></li>
					</ul></li>
				<li><a href="#Submission">Submission</a>
					<ul>
						<li><span class="titlemark">4.1.</span><a href="#Create_Item">Create
								Item</a></li>
						<li><span class="titlemark">4.2.</span><a href="#Edit_Item">Create
								/ Edit Item</a>
							<ul>
								<li><span class="titlemark">4.2.1.</span><a
									href="#Submitting_Persons_and_Organizations">Submitting
										Persons and Organizations</a></li>
								<li><span class="titlemark">4.2.2.</span><a
									href="#Submitting_Journal_Names">Submitting Journal Names</a></li>
								<li><span class="titlemark">4.2.3.</span><a
									href="#Entering_the_Publication_Language">Entering the
										Publication Language</a></li>
								<li><span class="titlemark">4.2.4.</span><a
									href="#Entering_Dates">Entering Dates</a></li>
								<li><span class="titlemark">4.2.5.</span><a
									href="#Providing_Rights_Information">Provide Rights
										Information</a></li>
							</ul></li>
						<li><span class="titlemark">4.3.</span><a
							href="#Create_New_Revision">Create New Revision</a></li>
						<li><span class="titlemark">4.4.</span><a
							href="#Validate_Item">Validate Item</a></li>
						<li><span class="titlemark">4.5.</span><a href="#Save_Item">Save
								Item</a></li>
						<li><span class="titlemark">4.6.</span><a
							href="#Delete%20Item">Delete Item</a></li>
						<li><span class="titlemark">4.7.</span><a href="#Submit_Item">Submit
								Item</a>
							<ul>
								<li><span class="titlemark">4.7.1.</span><a
									href="#Submission_Comment">Submission Comment</a></li>
							</ul></li>
						<li><span class="titlemark">4.8.</span><a
							href="#Release_Item">Release Item</a>
							<ul>
								<li><span class="titlemark">4.8.1.</span><a
									href="#Release_Comment">Release Comment</a></li>
							</ul></li>
					</ul></li>
				<li><a href="#Quality%20Assurance%20in%20PubMan">Quality
						Assurance in PubMan</a>
					<ul>
						<li><span class="titlemark">5.1.</span><a
							href="#Send_Back_for_Re-Work">Send Back for Re-work</a></li>
						<li><span class="titlemark">5.2.</span><a href="#Modify_Item">Modify
								Item</a></li>
						<li><span class="titlemark">5.3.</span><a href="#Release_Item">Release
								Item</a>
							<ul>
								<li><span class="titlemark">5.3.1.</span><a
									href="#Release_Comment">Release Comment</a></li>
							</ul></li>
						<li><span class="titlemark">5.4.</span><a
							href="#Withdraw%20Item">Withdraw Item</a></li>
					</ul></li>
				<li><a href="#Tools%20for%20Data%20Management">Tools for
						Data Management</a>
					<ul>
						<li><span class="titlemark">6.1.</span><a href="#My%20Items">My
								Items</a>
							<ul>
								<li><span class="titlemark">6.1.1.</span><a
									href="#Item_State">Item State</a></li>
								<li><span class="titlemark">6.1.2.</span><a href="#Sort_by">Sort
										by</a></li>
							</ul></li>
						<li><span class="titlemark">6.2.</span><a
							href="#Quality_Assurance_Workspace">Quality Assurance
								Workspace</a>
							<ul>
								<li><span class="titlemark">6.2.1.</span><a
									href="#Item_State_QA">Item State</a></li>
								<li><span class="titlemark">6.2.2.</span><a
									href="#Sort_by_QA">Sort by</a></li>
							</ul></li>
						<li><span class="titlemark">6.3.</span><a
							href="#Import%20Workspace">Import Workspace</a></li>
					</ul></li>
			</ol>
			<h2>
				<a>Download PubMan Guide</a>
			</h2>
			<p>
				A detailed PubMan Guide containing explanations of all
				functionalities can be downloaded <a
					href="https://subversion.mpdl.mpg.de/repos/smc/tags/public/PubMan/Wegweiser_durch_PubMan/Wegweiser_durch_PubMan.pdf"
					title="PubMan Guide">here</a>. (Currently only available in German.
				An English version will follow soon.)
			</p>
			<h2>
				<span class="titlemark">1.</span><a>About PubMan</a>
			</h2>
			<p>
				PubMan supports research organizations in the management,
				dissemination and re-use of publications and supplementary material.
				The solution PubMan is a component of the eResearch infrastructure
				of the Max Planck Society and is based on the service-oriented
				architecture of eSciDoc. Further information can be found
				under:&#160;<a
					href="http://colab.mpdl.mpg.de/mediawiki/Portal:PubMan">http://colab.mpdl.mpg.de/mediawiki/Portal:PubMan</a>
			</p>
			<h2>
				<span class="titlemark">2.</span><a>Preface</a>
			</h2>
			<h3>
				<span class="titlemark">2.1.</span><a>Log in</a>
			</h3>
			<p class="noindent">Please enter your username and your password,
				so that you can make use of functionalities only available to
				registered users, such as submitting content to PubMan.</p>
			<p class="noindent">
				If you do not have a PubMan account yet, please contact: <a
					href="mailto::pure-support@listsrv.mpg.de"
					title="contact PubMan support with email">PubMan Support.</a>
			</p>
			<h3>
				<span class="titlemark">2.2.</span><a>User Roles and Workflows</a>
			</h3>
			<p class="noindent">
				At the moment, two workflows are implemented: <a
					href="#Simple_Workflow">Simple Workflow</a>, the more basic form,
				and <a href="#Standard_Workflow">Standard Workflow</a>, where each
				publication needs to be controlled and approved by an authorised
				person, e.g. a librarian, before it becomes publicly visible.
			</p>
			<p class="noindent">In addition to the diverse workflows, there
				are also different user roles with varying privileges. These roles
				principally remain the same, whereas their rights may vary according
				to the workflow. A "depositor", for instance, is always a person
				that submits data (e.g. a scientist). A moderator, on the other
				hand, cannot submit data &#8211; s/he can only modify or complement
				them (e.g. a librarian).</p>
			<p class="noindent">Since roles within the institutes are often
				assigned in quite different ways, PubMan roles can be accordingly
				combined. For example, the same user may be depositor and moderator
				in one.</p>
			<h4>
				<span class="titlemark">2.2.1.</span><a>Workflows</a>
			</h4>
			<h5>
				<span class="titlemark">2.2.1.1.</span><a>Standard Workflow</a>
			</h5>
			<p class="noindent">In the standard workflow, the depositor
				creates items (item state: "pending") and submits them to the
				moderator for quality check (item state: "submitted"). After
				evaluating the item, the moderator can either release the item
				&#8211; making it publicly available (item state: "released")
				&#8211; or send it back to the depositor for rework, in case the
				item does not conform to the quality standards (item state: "in
				rework"). Released items can be modified by the moderator and the
				depositor. After a relesed item has been modified by the depositor,
				this item will again be set to item state "pending".</p>
			<h5>
				<span class="titlemark">2.2.1.2.</span><a>Simple Workflow</a>
			</h5>
			<p class="noindent">In this workflow, the depositor can create
				items (item state: "pending") and then release them for public view
				(item state: "released"). After an item has been released by the
				depositor, it can be modified by the depositor and the moderator.
				After modifying an item, the moderator releases it (item state:
				"released"). The option "send back for rework" is not available in
				the simple workflow.</p>
			<h4>
				<span class="titlemark">2.2.2.</span><a>PubMan User Roles</a>
			</h4>
			<p class="noindent">
				As mentioned before, two user roles are currently implemented in
				PubMan: depositor and moderator. However, this concept is expanded
				at the institutes' request. Under the following link you find an
				overview of user roles and workflows: <a
					href="http://colab.mpdl.mpg.de/mediawiki/PubMan_Workflows"
					title="more information on MPDL colab">http://colab.mpdl.mpg.de/mediawiki/PubMan_Workflows</a>
			</p>
			<h2>
				<span class="titlemark">3.</span><a>PubMan Functionalities</a>
			</h2>
			<p class="noindent">Many PubMan functionalities are not reserved
				for logged-in users, but are also open to not logged-in users.
				Below, there is an overview of all functionalities available to not
				logged-in users.</p>
			<h3>
				<span class="titlemark">3.1.</span><a>Search Possibilities in
					PubMan</a>
			</h3>
			<p class="noindent">Three different search modes are provided:</p>
			<ul>
				<li>Quick Search</li>
				<li>Advanced Search</li>
				<li>Organization Search</li>
			</ul>
			<h4>
				<span class="titlemark">3.1.1.</span><a>Quick Search</a>
			</h4>
			<p class="noindent">Please enter one or more search terms and
				click on the "go" button beside the search field to perform a
				search.</p>
			<p class="indent">The following Boolean operators are supported:
			</p>
			<ul>
				<li>AND</li>
				<li>OR</li>
				<li>NOT</li>
			</ul>
			<p class="indent">If you want to search within the metadata and
				the full text file attached to a record, please check the checkbox
				next to "Include Files". Please note that only the following mime
				types are indexed:</p>
			<ul>
				<li>application/pdf</li>
				<li>application/msword</li>
				<li>text/xml</li>
				<li>application/xml</li>
				<li>text/plain</li>
			</ul>
			<p class="noindent">Additionally truncation symbols can be used
				in all searches. Supported are "?" for one or no characters and "*"
				for cero until unlimited characters. Please note, that truncation at
				the beginning of the word is not allowed.</p>
			<h4 id="AdvancedSearchPage">
				<span class="titlemark">3.1.2.</span><a>Advanced Search</a>
			</h4>
			<p class="noindent">
				You can either search using single search options (Any field,
				Persons, Organizations, etc.) or you can combine the search options
				with "AND", "OR" or "NOT". By default, the operator "AND" is set
				between the fields. An overview of available search fields and their
				indexes can be found under: <a
					href="http://colab.mpdl.mpg.de/mediawiki/PubMan_Indexing"
					title="more information on MPDL colab">http://colab.mpdl.mpg.de/mediawiki/PubMan_Indexing</a>
			</p>
			<h5>
				<span class="titlemark">3.1.2.1.</span><a>Date Search</a>
			</h5>
			<p class="noindent">To search for dates, enter the date in the
				given format YYYY-MM-DD. As in the submission process, you may
				truncate the date and give only YYYY or YYYY-MM.</p>
			<p class="noindent">Some additional hints:</p>
			<p class="noindent">If you search for a specific date, please
				enter it in both fields: for example, startdate "2009-06-15" to
				enddate "2009-06-15".</p>
			<p class="noindent">If you enter a date only in the startdate
				e.g. "2009-06-15", you will get all records valid after the 15th of
				June 2009.</p>
			<p class="noindent">If you search for a time range, e.g.
				startdate "2008" to enddate "2009", it will automatically search for
				2008-01-01, means from the beginning of the year 2008, to
				2009-12-31, means to the end of the year 2009.</p>
			<h4>
				<span class="titlemark">3.1.3.</span><a>Organization Search</a>
			</h4>
			<p class="noindent">Please select the organization (or the
				sub-organization) you would like to be informed about. All PubMan
				items affiliated with this institute will be displayed. Clicking on
				the description button will provide you with more information on
				this organization.</p>
			<h3>
				<span class="titlemark">3.2.</span><a>Search results</a>
			</h3>
			<p class="noindent">If you clicked on "Include Files", there will
				also be records displayed, where the search term was found within
				the attached full text. You can change the display of single items
				or of the whole list, choosing between short and medium view. Please
				note that full text parts with the search term highlighted are only
				visible in medium view. If you click on the title of the
				publication, the full item view will be displayed.</p>
			<h4>
				<span class="titlemark">3.2.1.</span><a>Export</a>
			</h4>
			<p class="noindent">You can either retrieve the selected items in
				a citation style (e.g. APA) or you can export the items to an export
				format (e.g. EndNote Export Format).</p>
			<h5>
				<span class="titlemark">3.2.1.1.</span><a>Send Export via E-Mail</a>
			</h5>
			<p class="noindent">You can send exports per E-Mail.Please
				specify the receiver of the E-Mail, as well as the E-Mail address
				the receiver can reply to.</p>
			<p class="indent">If you want to send the mail to more than one
				E-Mail addresses, please separate them with a comma and a blank.</p>
			<h3 id="CartItemsPage">
				<span class="titlemark">3.3.</span><a>Basket</a>
			</h3>
			<p class="noindent">In PubMan, you can select any number of items
				from a list and compile them to a basket. To do so, please check the
				box next to the desired items and then click on "Add to basket". The
				number of items in the basket is always visible next to the "Basket"
				link. Please note, that the basket is only available for the
				particular session and cannot be saved. It is possible to export all
				items in the basket.</p>
			<h3>
				<span class="titlemark">3.4.</span><a>View Item</a>
			</h3>
			<p>
				On the detailed item view you have the possibility to use different
				<b><span class="nobreak">Social Bookmark Services</span></b>.
				Currently PubMan offers bookmarking options for <b>Delicious</b>, <b>CiteULike</b>
				and <b>Connotea</b>. If an <b>ISI Web of Knowledge Identifier</b> is
				stored with the item, that Identifier serves as a link to an <span
					style="">ISI Search.</span> A query for the same item in ISI offers
				the possibility for the user to&#160;retrieve some additional
				information about the displayed article. In case of a business card
				symbol is placed after the authors name, this icon leads to the <b>Researcher
					Portfolio</b> page. On this page the user gets further information
				about the author. All publications, which are stored in PubMan, are
				listed there. Furthermore the Researcher Portfolio offers the
				possibility to look up the same author in <b>WorldCat</b> and <b>Google
					Scholar</b>. In case a house symbol is placed after the name of the
				organization the author is belonging to, this icon leads to the
				organization description.
			</p>
			<h4>
				<span class="titlemark">3.4.1.</span><a>View Release History</a>
			</h4>
			<p class="noindent">All released versions of an item are
				displayed here, so that you can track all changes on an item.</p>
			<h4>
				<span class="titlemark">3.4.3.</span><a>View item Statistics</a>
			</h4>
			<p class="noindent">Here you can see how often the item was
				retrieved and how often the attached full text file was downloaded.
				Please note that the statistics are updated on a nightly basis.</p>
			<h4>
				<span class="titlemark">3.4.4.</span><a>Item Log (Registered
					Users only)</a>
			</h4>
			<p class="noindent">This is an option reserved to registered
				users, where you can see a full history of actions performed on the
				item, as logged by the system.</p>
			<h4>
				<span class="titlemark">3.4.5.</span><a>Local Tags (Registered
					Users only)</a>
			</h4>
			<p class="noindent">Here you can assign tags to publications in
				order to create own sets for specific purposes, such us "my best
				publications".</p>
			<h2>
				<span class="titlemark">4.</span><a>Submission</a>
			</h2>
			<p class="noindent">In PubMan, you may choose between three
				different ways of submitting content.</p>
			<ul>
				<li><b>Full submission:</b> All metadata fields available for
					the genre you have selected are shown.</li>
				<li><b>Easy Submission:</b> a stepwise manual submission
					wizard.</li>
				<li><b>Import:</b> Here you can upload a Bib<span class="tex">T</span><span
					class="e">e</span><span class="tex">X</span> record or fetch
					metadata and full text(s) from arXiv, SPIRES, BioMed Central or
					PubMed Central.</li>
				<li><b>Multiple Import:</b> Here you have the possibility to do
					a mass import of data from&#160;Web of Science, EndNote, BibTeX or
					RIS. <i>Note for multiple EndNote Imports: All authors of an
						item are affiliated to "Max Planck Society" by default. Desired
						changes/modifications regarding the affiliated Organizational Unit
						of an author have to be done manually for each respective item
						after the import.</i></li>
			</ul>
			<h3>
				<span class="titlemark">4.1.</span><a>Create Item</a>
			</h3>
			<p class="noindent">Before you create a new item, please select a
				submission method and then the collection, in which you would like
				to add the item, by clicking on the name of the collection.</p>
			<h3>
				<span class="titlemark">4.2.</span><a>Create / Edit Item</a>
			</h3>
			<p class="noindent">Please specify the document type (Genre) of
				your item before you start submitting the publication data.
				According to this genre, you are then provided with a submission
				mask. Please note that you can only change the genre until you have
				saved, submitted or released the item once; PubMan will then delete
				all submission fields that are unnecessary for this genre.</p>
			<p class="indent">
				Please fill out all fields marked with a star, as they are the
				minimum data required to submit an item. Should you leave one of
				these fields empty, a validation message is displayed. Also see: <a
					href="#Validate_Item">Validation</a>.
			</p>
			<h4>
				<span class="titlemark">4.2.1.</span><a>Submitting Persons and
					Organizations</a>
			</h4>
			<p class="noindent">
				There are two ways of submitting person names. You may submit them
				separately, using the "persons" mask and clicking on the "plus"
				symbol to add further persons. If the name you start typing is
				recognized by the system, an auto-suggestion list is displayed. You
				can either select a name from the list or press ESC to close it. <br />
				To enter multiple persons at once click on the "add multiple" link:
				a text field appears, where you may type or copy/paste a list of
				persons. This list is then parsed by the system. <br /> Please note
				that at least one person has to be affiliated to an organization,
				otherwise the item cannot be created.
			</p>
			<h4>
				<span class="titlemark">4.2.2.</span><a>Submitting Journal Names</a>
			</h4>
			<p class="noindent">If you would like to give a journal name as a
				source, please choose "Journal" as "Source Genre". When you start
				typing the journal name, PubMan will start suggesting journal names
				accordingly. You may either choose one of the suggestions by
				selecting it and clicking on it or pressing "enter", or you may
				enter a new journal name. If you do not want to select a suggested
				name, please press ESC to close the list.</p>
			<h4>
				<span class="titlemark">4.2.3.</span><a>Entering the Publication
					Language</a>
			</h4>
			<p class="noindent">Auto-suggestion lists are also available in
				the publication language field. You may enter the language in German
				or English; both languages are recognized by the system.</p>
			<h4>
				<span class="titlemark">4.2.4.</span><a>Entering Dates</a>
			</h4>
			<p class="noindent">
				A date should have the following format: YYYY-MM-DD. However, you
				may also enter terms like "yesterday", "last year", or similar:
				PubMan will convert them into the right format. <br /> <i>Note
					for a date entry for publication types "Series" and "Journal": For
					this both genres the given date relates to the start of the
					respective publication.</i>
			</p>
			<h4>
				<span class="titlemark">4.2.5.</span><a>Provide Rights
					Information</a>
			</h4>
			<p class="noindent">
				It's possible to provide several rights information for each
				uploaded file and locator. You can appoint access rights ("public",
				"private", "restricted") and assign them to selected user groups
				respectively. There is an option to provide a "Rights Statement"
				(freetext field) and a "Copyright Date" (date format). Additional
				it's possible to choose a <span class="nobreak">Creative
					Commons License</span> (<a href="http://creativecommons.org/">http://creativecommons.org/</a>)
				for the file. All these data are optional. Please be aware that you
				have checked these rights information before!
			</p>
			<h3>
				<span class="titlemark">4.4.</span><a>Validate Item</a>
			</h3>
			<p class="noindent">You can validate the item in order to check
				if it meets the requirements of the collection.</p>
			<p class="noindent">Please note that you can&#8217;t submit an
				item that doesn&#8217;t meet the requirements of the collection.</p>
			<h3>
				<span class="titlemark">4.5.</span><a>Save Item</a>
			</h3>
			<p class="noindent">After clicking on the "save" button, your
				item obtains the status "pending" and can only be seen by you. This
				might be useful if you want to complete your entry at a later time.
				You can then access and edit your item by clicking on "My Items".</p>
			<h3>
				<span class="titlemark">4.6.</span><a>Delete</a>
			</h3>
			<p class="noindent">Please note that only items in state
				"pending" can be deleted. "Released" items can only be withdrawn,
				since they have been assigned a PID for citation purposes.</p>
			<h3>
				<span class="titlemark">4.7.</span><a>Submit Item</a>
			</h3>
			<p class="noindent">In the Standard Workflow, the depositor can
				only submit his items.</p>
			<p class="noindent">If you go on "submit item", your data will
				first be validated. If your item is valid, you are guided to a mask,
				where you can state a comment to your submission. After you have
				submitted your item, it is sent to the moderator of your collection,
				who then checks the data and either releases it for public view or
				sends it back to you for corrections. Items that have been sent back
				to the depositor are labeled as "in rework" and can be found under
				the corresponding filter under "My items".</p>
			<h4>
				<span class="titlemark">4.7.1.</span><a>Submission Comment</a>
			</h4>
			<p class="noindent">In the comment field you can give a comment
				on your submission; the user following you in the workflow will be
				able to read it.</p>
			<h3>
				<span class="titlemark">4.8.</span><a>Release Item</a>
			</h3>
			<p class="noindent">In the simple workflow, you don&#8217;t have
				to submit items first; they can be directly released and made
				publicly available.</p>
			<h4>
				<span class="titlemark">4.8.1.</span><a>Release Comment</a>
			</h4>
			<p class="noindent">In the comment field you can give a comment
				on your release; the user following you in the workflow will be able
				to read it.</p>
			<h2>
				<span class="titlemark">5.</span><a>Quality Assurance in PubMan</a>
			</h2>
			<p class="noindent">In order to guarantee the high quality of
				data in PubMan, a quality assurance workflow is implemented, which
				ensures that specific quality criteria are met. Responsible for the
				quality assurance is the moderator, who has a wide spectrum of means
				at her/his disposal. S/he can modify the items or send them back to
				the depositor for re-work; if s/he is content with the quality, s/he
				can release and release the items. A further quality assurance
				option, which is only available to the depositor, is the withdrawal
				of an item: an alternative to deleting the item that serves the
				purposes of long-term storage and guarantees further citability of
				the item.</p>
			<h3>
				<span class="titlemark">5.1.</span><a>Send Back for Re-Work</a>
			</h3>
			<p class="noindent">In the standard workflow, a moderator can
				send "submitted" items back to the owner for re-work, in case they
				do not meet with the Quality Assurance standards. Items in state "in
				rework" will be visible by both the moderator and the depositor, but
				can only be edited by the depositor, until they are once more
				submitted to the moderator for quality check.</p>
			<h3>
				<span class="titlemark">5.2.</span><a>Modify Item</a>
			</h3>
			<p class="noindent">"Pending" items can be edited by their owner
				until s/he is satisfied with them and submits them. "Submitted" or
				"released" items (depending on the workflow) can be changed by the
				moderator, "releasesd" items can also be modified by the depositor.
			</p>
			<h3>
				<span class="titlemark">5.3.</span><a>Realease Item</a>
			</h3>
			<p class="noindent">If you want to release the item you have
				modified and make it publicly available again, please click on
				"release".</p>
			<h4>
				<span class="titlemark">5.3.1.</span><a>Release Comment</a>
			</h4>
			<p class="noindent">In the comment field you can give a comment
				on releasing the item, which is then visible in the item's release
				history.</p>
			<h3>
				<span class="titlemark">5.4.</span><a>Withdraw</a>
			</h3>
			<p class="noindent">"Released" PubMan items cannot be deleted
				anymore. They can only be withdrawn, so that the metadata is further
				on available (without any full text) and the items remain quotable.
				"Withdrawn" items are not searchable and they can only be accessed
				through their ID. Items can only be withdrawn by their owner
				(depositor). Please state the reason for withdrawing the item.</p>
			<h2>
				<span class="titlemark">6.</span><a>Tools for Data Management</a>
			</h2>
			<p class="noindent">As a depositor, you can manage your items
				through the depositor workspace (&#8220;My Items"). There you can
				find an overview of your items and your managing options. You can,
				for instance, change the viewing mode, filter the items according to
				their state, sort them based on various criteria, export selected
				items or create a basket.</p>
			<h3>
				<span class="titlemark">6.1.</span><a>My Items</a>
			</h3>
			<p class="noindent">In the "My Items" workspace, the depositor
				can find all the items he has created. He may filter them according
				to their state and perform different actions, e.g. complete items in
				status "pending" or enrich entries that have been sent back for
				rework. Another filter shows imported items sorted by date.</p>
			<h4>
				<span class="titlemark">6.1.1.</span><a>Item State</a>
			</h4>
			<p>If you want items of another state to be displayed, please
				click on "filter" and open the pull-down list. You may choose one of
				the following states:</p>
			<ul>
				<li><b>"pending":</b> These are items that you created and then
					saved, because your submission was not complete yet. They still
					have to be "submitted" or "released", when you have finished
					editing them.</li>
				<li><b>"submitted":</b> This <span class="nobreak">item-state
						exists</span> only in the standard workflow. The data entry for these
					items is complete and they are "submitted" to the moderator of your
					collection for formal check.</li>
				<li><b>"released":</b> Items in state "released" are publicly
					visible. They can be modified by you and the moderator of your
					collection.</li>
				<li><b>"withdrawn":</b> Items in state "withdrawn" are not
					accessible via search or organizations search. They are only
					visible to you as their creator - alternatively, they can be
					accessed via their specific URL.</li>
				<li><b>"in rework":</b> This item-state exists only in the
					standard workflow. These items have been sent back to you by the
					moderator for rework. It is important that you do not forget to
					re-submit these items, when you have finished editing them.</li>
			</ul>
			<h4>
				<span class="titlemark">6.1.2.</span><a>Sort by</a>
			</h4>
			<p class="noindent">Please specify the sorting option for your
				item list by selecting it in the pull-down list. You can switch
				between "ascending" and "descending" sorting order by clicking on
				the link next to the pull down list.</p>
			<h3>
				<span class="titlemark">6.2.</span><a>Quality Assurance
					Workspace</a>
			</h3>
			<p class="noindent">In the Quality Assurance Workspace all items
				relevant for the moderator are listed. Items in state "submitted"
				are items that need to be quality checked. The moderator will judge
				their quality and then either release them or send them back for
				rework to the depositor.</p>
			<p class="noindent">Please note that this only applies to the
				Standard Workflow. In the Simple Workflow there are no "submitted"
				items.</p>
			<h4>
				<span class="titlemark">6.2.1.</span><a>Item State</a>
			</h4>
			<p>If you want items of another state to be displayed, please
				click on "filter" and open the pull-down list. You may choose one of
				the following states:</p>
			<ul>
				<li><b>"submitted":</b> This item-state exists only in the
					standard workflow. These are items that still need to be formally
					checked by you. After that, you can either edit them yourself and
					"release" them for the public view, or send them back to the
					depositor for rework.</li>
				<li><b>"released":</b> Items in state "released" are publicly
					visible and can be modified by you, if needed.</li>
				<li><b>"in rework":</b> This item-state exists only in the
					standard workflow. These are the items that you have sent back to
					the depositor for rework.</li>
			</ul>
			<h4>
				<span class="titlemark">6.2.2.</span><a>Sort by</a>
			</h4>
			<p class="noindent">Please specify the sorting option for your
				item list by selecting it in the pull-down list. You can switch
				between "ascending" and "descending" sorting order by clicking on
				the link next to the pull down list.</p>
			<h3 id="ImportWorkspace">
				<span class="titlemark">6.3.</span><a>Import Workspace</a>
			</h3>
			<p class="noindent">In the import workspace you can check the
				status of your import(s) and do batch operations. If you use the
				remove buttons, the import task will be delete (not the items). If
				you use the delete button, the items will be deleted if they still
				are in state "pending".</p>
		</div>
	</f:view>
	
</body>

</html>
