<!DOCTYPE html>

<html xmlns="http://www.w3.org/1999/xhtml"
	xmlns:f="http://xmlns.jcp.org/jsf/core"
	xmlns:h="http://xmlns.jcp.org/jsf/html"
	xmlns:ui="http://xmlns.jcp.org/jsf/facelets"
	xmlns:p="http://primefaces.org/ui"
	xmlns:pt="http://xmlns.jcp.org/jsf/passthrough">

<h:head>
	<title>PubMan Onlinehilfe</title>
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
	<meta name="generator" content="TeX4ht (http://www.cse.ohio-state.edu/~gurari/TeX4ht/)" />
	<meta name="originator"	content="TeX4ht (http://www.cse.ohio-state.edu/~gurari/TeX4ht/)" />
	<meta name="src" content="eSciDoc_help_de.tex" />
	<meta name="date" content="2008-10-08 13:10:00" />
	<ui:include src="/header/ui/StandardImports.jspf" />
</h:head>

<body id="helppage" lang="de-DE" dir="ltr">

	<f:view locale="#{InternationalizationHelper.userLocale}">

		<div class="maketitle wrapper"
			style="padding: 0.74em 0.74em 3em 0.74em; font-size: 129% !important; width: auto;">
			
			<h1>PubMan Onlinehilfe</h1>
			<p class="noindent">
				<span class="cmr-12x-x-120">20. April 2009</span>
			</p>
			
			<h2>Inhaltsverzeichnis</h2>
			<ol>
				<li><a href="#HomePage">&#220;ber PubMan</a></li>

				<li><a href="#Allgemeines">Allgemeines</a>
					<ul>
						<li><span class="titlemark">2.1.</span><a href="#Login">Login</a></li>
						<li><span class="titlemark">2.2.</span><a
							href="#Nutzerrollen_und_Workflows">Nutzerrollen und Workflows</a>
							<ul>
								<li><span class="titlemark">2.2.1.</span><a
									href="#Workflows">Workflows</a>
									<ul>
										<li><span class="titlemark">2.2.1.1.</span><a
											href="#Standard_Workflow">Standard Workflow&#160;</a></li>
										<li><span class="titlemark">2.2.1.2.</span><a
											href="#Simple_Workflow">Simple Workflow</a></li>
									</ul></li>
								<li><span class="titlemark">2.2.2.</span><a
									href="#PubMan_Nutzerrollen">Pubman Nutzerrollen</a></li>
							</ul></li>
					</ul></li>

				<li><a href="#PubMan_Funktionalitaeten">PubMan
						Funktionalit&#228;ten</a>
					<ul>
						<li><span class="titlemark">3.1.</span><a
							href="#Suchmoeglichkeiten_in_PubMan">Suchm&#246;glichkeiten
								in PubMan</a>
							<ul>
								<li><span class="titlemark">3.1.1.</span><a
									href="#Einfache_Suche">Einfache Suche</a></li>
								<li><span class="titlemark">3.1.2.</span><a
									href="#AdvancedSearchPage">Erweiterte Suche</a>
									<ul>
										<li><span class="titlemark">3.1.2.1.</span><a
											href="#Datum_Suche">Suche nach Daten und Zeitspannen</a></li>
									</ul></li>
								<li><span class="titlemark">3.1.3.</span><a
									href="#AffiliationTreePage">Organisationssuche</a></li>
							</ul></li>
						<li><span class="titlemark">3.2.</span><a
							href="#Suchergebnisse">Suchergebnisse</a>
							<ul>
								<li><span class="titlemark">3.2.1.</span><a
									href="#Exportieren">Exportieren</a>
									<ul>
										<li><span class="titlemark">3.2.1.1.</span><a
											href="#Export_per_E-Mail_verschicken">Export per E-Mail
												verschicken</a></li>
									</ul></li>
							</ul></li>
						<li><span class="titlemark">3.3.</span><a href="#Basket">Basket</a>
						</li>
						<li><span class="titlemark">3.4.</span><a
							href="#Datensatz-Vollansicht">Datensatz-Vollansicht</a>
							<ul>
								<li><span class="titlemark">3.4.1.</span><a
									href="#Freigabegeschichte_einsehen">Freigabegeschichte
										einsehen</a></li>
								<li><span class="titlemark">3.4.2.</span><a
									href="#Revisionen_einsehen">Revisionen einsehen</a></li>
								<li><span class="titlemark">3.4.3.</span><a
									href="#Statistik_zum_Datensatz_einsehen">Statistik zum
										Datensatz einsehen</a></li>
								<li><span class="titlemark">3.4.4.</span><a
									href="#Bearbeitungsgeschichte">Bearbeitungsgeschichte (nur
										registrierte Nutzer)</a></li>
								<li><span class="titlemark">3.4.5.</span><a
									href="eSciDoc_help_de.jsp#ViewLocalTagsPage">Lokale Tags
										(nur registrierte Nutzer)</a></li>
							</ul></li>
					</ul></li>

				<li><a href="#SubmissionPage">Dateneingabe</a>
					<ul>
						<li><span class="titlemark">4.1.</span><a
							href="#neuen_Datensatz_anlegen">Einen neuen Datensatz anlegen</a></li>
						<li><span class="titlemark">4.2.</span><a
							href="#Datensatz_bearbeiten">Anlegen und Bearbeiten eines
								Datensatzes</a>
							<ul>
								<li><span class="titlemark">4.2.1.</span><a
									href="#Personen_und_Organisationen">Personen und
										Organisationen eingeben</a></li>
								<li><span class="titlemark">4.2.2.</span><a
									href="#Zeitschriftennamen_eingeben">Zeitschriftennamen
										eingeben</a></li>
								<li><span class="titlemark">4.2.3.</span><a
									href="#Sprache_der_Publikation_eingeben">Sprache der
										Publikation angeben</a></li>
								<li><span class="titlemark">4.2.4.</span><a
									href="#Ein_Datum_eingeben">Ein Datum eingeben</a></li>
								<li><span class="titlemark">4.2.5.</span><a href="#Rechte">Rechte-Informationen
										f&#252;r eine Datei hinterlegen</a></li>
							</ul></li>
						<li><span class="titlemark">4.3.</span><a
							href="#Revision_anlegen">Eine neue Revision anlegen</a></li>
						<li><span class="titlemark">4.4.</span><a href="#Validierung">Validierung</a></li>
						<li><span class="titlemark">4.5.</span><a href="#Speichern">Speichern
								eines Datensatzes</a></li>
						<li><span class="titlemark">4.6.</span><a
							href="#Loeschen_des_Datensatzes">L&#246;schen eines
								Datensatzes</a></li>
						<li><span class="titlemark">4.7.</span><a
							href="#Einstellen_eines_Datensatzes">Einstellen eines
								Datensatzes</a>
							<ul>
								<li><span class="titlemark">4.7.1.</span><a
									href="#Kommentar_Einstellen">Kommentar zum Einstellen</a></li>
							</ul></li>
						<li><span class="titlemark">4.8.</span><a href="#Freigeben">Freigeben
								eines Datensatzes</a>
							<ul>
								<li><span class="titlemark">4.8.1.</span><a
									href="#Kommentar_zum_Freigeben">Kommentar zum Freigeben</a></li>
							</ul></li>
					</ul></li>

				<li><a href="#Qualitaetssicherung_in_PubMan">Qualit&#228;tssicherung
						in PubMan</a>
					<ul>
						<li><span class="titlemark">5.1.</span><a
							href="Ueberarbeitung">Einen Datensatz zur &#220;berarbeitung
								an den Depositor zur&#252;cksenden</a></li>
						<li><span class="titlemark">5.2.</span><a
							href="#Datensatz_modifizieren">Einen Datensatz modifizieren</a></li>
						<li><span class="titlemark">5.3.</span><a href="#Akzeptieren">Akzeptieren</a>
							<ul>
								<li><span class="titlemark">5.3.1.</span><a
									href="#Kommentar_Akzeptieren">Kommentar zum Akzeptieren</a></li>
							</ul></li>
						<li><span class="titlemark">5.4.</span><a
							href="#Datensatz_zurueckziehen">Einen Datensatz
								zur&#252;ckziehen</a></li>
					</ul></li>

				<li><a href="#Hilfsmittel_zur_Datenverwaltung">Hilfsmittel
						zur Datenverwaltung</a>
					<ul>
						<li><span class="titlemark">6.1.</span><a
							href="#Meine_Datensaetze">Meine Datens&#228;tze</a>
							<ul>
								<li><span class="titlemark">6.1.1.</span><a
									href="#Status_eines_Datensatzes">Status eines Datensatzes</a></li>
								<li><span class="titlemark">6.1.2.</span><a
									href="#Datensaetze_Sortieren">Datens&#228;tze sortieren</a></li>
							</ul></li>
						<li><span class="titlemark">6.2.</span><a
							href="#Qualitaetssicherung">Qualit&#228;tssicherungs Bereich</a>
							<ul>
								<li><span class="titlemark">6.2.1.</span><a
									href="#Status_eines_Datensatzes_QA">Status eines
										Datensatzes</a></li>
								<li><span class="titlemark">6.2.2.</span><a
									href="#Datensaetze_sortieren_QA">Datens&#228;tze sortieren</a></li>
							</ul></li>
						<li><span class="titlemark">6.3.</span><a
							href="#Import_Bereich">Import Bereich</a></li>
					</ul></li>
			</ol>

			<h2>Download PubMan Wegweiser</h2>
			
			<p>
				Einen ausf&#252;hrlichen Wegweiser durch PubMan mit Erkl&#228;rung
				der verschiedenen Funktionalit&#228;ten finden Sie <a
					href="https://subversion.mpdl.mpg.de/repos/smc/tags/public/PubMan/Wegweiser_durch_PubMan/Wegweiser_durch_PubMan.pdf"
					title="PubMan Wegweiser">hier</a>.
			</p>

			<h2 id="HomePage">
				<span class="titlemark">1.</span><a>&#220;ber PubMan</a>
			</h2>
			
			<p>
				PubMan unterst&#252;tzt Forschungsorganisationen im Management, der
				Verbreitung und der Nachnutzung von Publikationen und Supplementary
				Material. Die Anwendung PubMan ist eine Komponente der eResearch
				Infrastruktur der Max-Planck-Gesellschaft und basiert auf der
				service-orientierten Architektur von eSciDoc. Weitere Informationen
				finden Sie unter: <span class="nobreak"><a
					href="http://colab.mpdl.mpg.de/mediawiki/Portal:PubMan"
					title="Information zu PubMan im MPDL Colab">http://colab.mpdl.mpg.de/mediawiki/Portal:PubMan</a></span>
			</p>

			<h2 id="Allgemeines">
				<span class="titlemark">2.</span><a>Allgemeines</a>
			</h2>
			
			<h3 id="Login">
				<span class="titlemark">2.1.</span><a>Login</a>
			</h3>
			
			<p class="noindent">Bitte geben Sie Ihren Nutzernamen und Ihr
				Passwort ein, um alle Funktionalit&#228;ten nutzen zu k&#246;nnen,
				die nur registrierten Nutzern zur Verf&#252;gung stehen, z.B. um
				Datens&#228;tze in PubMan einzugeben.</p>
			<p class="noindent">
				Wenn Sie noch &#252;ber kein Login f&#252;r PubMan verf&#252;gen,
				dann wenden Sie sich bitte an: <a
					href="mailto:pubman-support@gwdg.de">PubMan Support.</a>
			</p>

			<h3 id="Nutzerrollen_und_Workflows">
				<span class="titlemark">2.2.</span><a>Nutzerrollen und Workflows</a>
			</h3>
			
			<p class="noindent">
				Derzeit sind in PubMan zwei Workflows implementiert: Zum einen ein
				sehr einfacher Workflow, genannt <a href="#Simple_Workflow">Simple
					Workflow</a>, zum anderen der so genannte <a href="#Standard_Workflow">Standard
					Workflow</a>, bei dem eine Publikation erst nach <span class="nobreak">Kontrolle
					und Zustimmung</span> einer autorisierten Person, z.B. einem/r
				Bibliotekar/in, f&#252;r die &#214;ffentlichkeit sichtbar gemacht
				wird.&#160;
			</p>
			<p class="noindent">Zus&#228;tzlich zu den Workflows gibt es
				Nutzerrollen mit unterschiedlichen Privilegien. Diese sind vom
				Grundprinzip her gleich aufgebaut, die Berechtigungen k&#246;nnen je
				nach Workflow anders sein. Die Rolle des Depositors, beispielsweise
				ist immer eine Person (z.B. ein Wissenschaftler), die Daten eingibt.
				Wohingegen ein Moderator (z.B. ein Bibliothekar) selbst keine Daten
				eingeben, sondern lediglich korrigieren oder erg&#228;nzen
				kann.&#160;</p>
			<p class="noindent">Da die Rollen in den Instituten sehr
				unterschiedlich besetzt sein k&#246;nnen, k&#246;nnen Rollen auch
				kombiniert werden. So kann ein Nutzer beispielsweise auch Depositor
				und Moderator in einem sein.</p>

			<h4 id="Workflows">
				<span class="titlemark">2.2.1.</span><a>Workflows</a>
			</h4>
			
			<h5 id="Standard_Workflow">
				<span class="titlemark">2.2.1.1.</span><a>Standard Workflow</a>
			</h5>
			
			<p class="noindent">Im Standard Workflow werden die
				Datens&#228;tze nach ihrer Eingabe (Status: "pending") vom
				Depositor&#160;eingestellt (Status: "eingestellt"), und dann vom
				Moderator &#252;berpr&#252;ft. Nach der Qualit&#228;tskontrolle kann
				der Moderator den Datensatz entweder akzeptieren (Status:
				"freigegeben") oder, wenn noch weitere Angaben ben&#246;tigt werden,
				dem Depositor zur &#220;berarbeitung zur&#252;ckschicken (Status:
				"in &#220;berarbeitung"). Der Moderator und der Depositor haben die
				M&#246;glichkeit, einen bereits freigegebenen Datensatz nochmals zu
				bearbeiten. Nach einer erneuten Bearbeitung durch den Depositor
				erscheint der Datensatz wieder im Status "pending".</p>

			<h5 id="Simple_Workflow">
				<span class="titlemark">2.2.1.2.</span><a>Simple Workflow</a>
			</h5>
			
			<p class="noindent">Hier kann der Depositor einen Datensatz
				eingeben (Status des Datensatzes: "pending") und f&#252;r die
				&#246;ffentliche Sicht freigeben (Status: "freigegeben"). Der
				Moderator und der Depositor k&#246;nnen dann den bereits
				freigegebenen Datensatz noch modifizieren. Nach der Modifikation
				kann der Moderator den Datensatz akzeptieren (Status:
				"freigegeben"). Die Option "zum Bearbeiten schicken" ist im Simple
				Workflow nicht vorhanden.</p>

			<h4 id="PubMan_Nutzerrollen">
				<span class="titlemark">2.2.2.</span><a>PubMan Nutzerrollen</a>
			</h4>
			
			<p class="noindent">
				Wie bereits oben erw&#228;hnt, sind derzeit in PubMan zwei
				Nutzerrollen implementiert, der Depositor und der Moderator. Dieses
				Konzept wird jedoch auf Wunsch von Instituten ausgebaut. Einen
				Gesamt&#252;berblick &#252;ber die Nutzerrollen und Workflows finden
				Sie unter: <a
					href="http://colab.mpdl.mpg.de/mediawiki/PubMan_Workflows">http://colab.mpdl.mpg.de/mediawiki/PubMan_Workflows</a>
			</p>

			<h2 id="PubMan_Funktionalitaeten">
				<span class="titlemark">3.</span><a>PubMan Funktionalit&#228;ten</a>
			</h2>
			
			<p class="noindent">Eine Vielzahl der Funktionalit&#228;ten in
				PubMan steht nicht nur eingeloggten Nutzern zur Verf&#252;gung,
				sondern sind auch f&#252;r nicht eingeloggte Nutzer vorgesehen. Im
				Folgenden erhalten Sie eine &#220;bersicht der
				Funktionalit&#228;ten, die auch f&#252;r nicht eingeloggte Nutzer
				verf&#252;gbar sind.</p>

			<h3 id="Suchmoeglichkeiten_in_PubMan">
				<span class="titlemark">3.1.</span><a>Suchm&#246;glichkeiten in
					PubMan</a>
			</h3>
			
			<p class="MsoNormal" style="margin-left: 0cm; text-indent: 0cm;">
				PubMan bietet drei unterschiedliche Sucheinstiegspunkte:</p>
				
			<ul>
				<li>Einfache Suche</li>
				<li>Erweiterte Suche</li>
				<li>Organisationssuche</li>
			</ul>

			<h4 id="Einfache_Suche">
				<span class="titlemark">3.1.1.</span><a>Einfache Suche</a>
			</h4>
			
			<p class="noindent">Bitte geben Sie einen oder mehrere
				Suchbegriffe ein und klicken Sie den "Los" Knopf rechts neben dem
				Sucheingabefeld, um die Suche auszuf&#252;hren.</p>
			<p class="indent">Folgende boolschen Operatoren werden in der
				Suche unterst&#252;tzt:</p>
			<ul>
				<li>UND (AND)</li>
				<li>ODER (OR)</li>
				<li>NICHT (NOT)</li>
			</ul>
			<p class="indent">Wenn Sie neben den Metadaten auch die
				angeh&#228;ngten Volltexte durchsuchen m&#246;chten, dann
				w&#228;hlen Sie bitte die Checkbox neben "Volltexte einbinden" an.
				Bitte beachten Sie, dass nur folgende Arten von Volltext
				durchsuchbar sind:</p>
			<ul>
				<li>application/pdf</li>
				<li>application/msword</li>
				<li>text/xml</li>
				<li>application/xml</li>
				<li>text/plain</li>
			</ul>
			<p>Alle Suchanfragen k&#246;nnen auch mit Wildcards versehen
				werden. Unterst&#252;tzt wird "?" f&#252;r ein oder kein Zeichen und
				"*" f&#252;r null bis unendlich viele Zeichen. Bitte beachten Sie,
				dass Trunkierungen am Anfang des Suchbegriffs nicht unterst&#252;tz
				werden.</p>

			<h4 id="AdvancedSearchPage">
				<span class="titlemark">3.1.2.</span><a>Erweiterte Suche</a>
			</h4>
			
			<p class="noindent">
				Sie haben die M&#246;glichkeit, die Suchoptionen (Alle Felder,
				Personen, Organisationen usw.) entweder einzeln oder in Kombination
				zu verwenden. Die Suchoptionen k&#246;nnen mit "AND", "OR" oder
				"NOT" miteinander verkn&#252;pft werden. Voreingestellt ist eine
				Verkn&#252;pfung mit "AND" zwischen allen Feldern. Eine
				&#220;bersicht der verf&#252;gbaren Suchfelder und deren Indexierung
				finden Sie unter: <a
					href="http://colab.mpdl.mpg.de/mediawiki/PubMan_Indexing">http://colab.mpdl.mpg.de/mediawiki/PubMan_Indexing</a>
			</p>

			<h5 id="Datum_Suche">
				<span class="titlemark">3.1.2.1.</span><a>Suche nach Daten und
					Zeitspannen</a>
			</h5>
			
			<p>Um nach einem Datum zu suchen geben Sie dieses bitte im Format
				YYYY-MM-DD im Suchfeld ein. Wie auch bei der Dateneingabe ist es
				ebenso m&#246;glich nur nach YYYY oder YYYY-MM zu suchen.</p>
			<p>Einige weitere Hinweise:</p>
			<p>Wenn Sie nach einem bestimmten Datum suchen, geben Sie jenes
				Datum bitte in beiden Such-Feldern ein: zum Beispiel Startdatum
				"2009-06-15" bis Enddatum "2009-06-15".</p>
			<p>Wenn Sie nur im Startdatum eine Eingabe vornehmen, z.B.
				"2009-06-15", enth&#228;lt das Suchergebnis alle Datens&#228;tze mit
				einem Datum ab dem 15. Juni 2009 bis zum heutigen Tag.</p>
			<p>Wenn Sie nach einer Zeitspanne suchen, z.B.Startdatum&#160;
				"2008" bis Enddatum "2009" wird automatisch nach allen
				Datens&#228;tzen gesucht ab dem 01.01.2008, also dem Beginn des
				Jahres 2008, bis zum 31.12.2009, also dem Ende des Jahres 2009.</p>

			<h3 id="AffiliationTreePage">
				<span class="titlemark">3.3.</span><a>Organisationssuche</a>
			</h3>
			
			<p class="noindent">W&#228;hlen Sie hier die Organisation (oder
				Unterorganisation) aus, &#252;ber die Sie sich informieren
				m&#246;chten. Sie bekommen dann alle in PubMan eingegebenen
				Referenzen des Instituts angezeigt. Wenn Sie auf "Beschreibung"
				klicken, erhalten Sie weitere Informationen zur Organisation.</p>

			<h3 id="Suchergebnisse">
				<span class="titlemark">3.2.</span><a>Suchergebnisse</a>
			</h3>
			
			<p class="noindent">Wenn Sie die Option "Volltexte einbinden"
				gew&#228;hlt haben, bekommen Sie auch Ergebnisse angezeigt, bei
				denen der Suchbegriff innerhalb des angeh&#228;ngten Textes gefunden
				wurde. Sie k&#246;nnen sich die ganze Liste oder einzelne
				Datens&#228;tze in einem kurzen oder in einem mittleren
				Anzeigeformat ausgeben lassen. Wenn Sie auf den Titel der
				Publikation klicken, bekommen Sie die Vollansicht des Datensatzes
				angezeigt.</p>

			<h4 id="Exportieren">
				<span class="titlemark">3.2.1.</span><a>Exportieren</a>
			</h4>
			
			<p class="noindent">Sie k&#246;nnen&#160;Ergebnisse
				im&#160;Zitierstil, z.B. APA, oder in ein bestimmtes Format, z.B.
				EndNote Exportformat, exportieren.</p>

			<h5 id="Export_per_E-Mail_verschicken">
				<span class="titlemark">3.2.1.1</span><a>Export per E-Mail
					verschicken</a>
			</h5>
			
			<p class="noindent">Sie k&#246;nnen Daten auch per E-Mail
				verschicken. Hierf&#252;r geben Sie bitte den Empf&#228;nger der
				E-Mail an und die E-Mail Adresse, an die der Empf&#228;nger
				antworten kann.</p>
			<p class="indent">Wenn Sie die Export E-Mail an mehr als eine
				E-Mail Adresse verschicken m&#246;chten, dann trennen Sie diese
				bitte mit einem Komma und einem Leerzeichen voneinander ab.</p>

			<h3 id="Basket">
				<span class="titlemark">3.3.</span><a>Basket</a>
			</h3>
			
			<p class="noindent">In PubMan haben Sie die M&#246;glichkeit,
				eine beliebige Anzahl an Datens&#228;tzen von einer Liste
				auszuw&#228;hlen und sie dann in einen "Basket"
				zusammenzuf&#252;hren. Hierf&#252;r klicken Sie bitte jeweils die
				Checkbox neben den gew&#252;nschten Datens&#228;tzen an, und dann
				auf "Zum Basket hinzuf&#252;gen". Die Anzahl der Datens&#228;tze im
				Basket ist immer in Klammern neben dem Link zum Basket sichtbar.
				Bitte beachten Sie, dass jeder Basket nur f&#252;r die jeweilige
				Sitzung zur Verf&#252;gung steht und nicht gespeichert werden kann.
				Ein Export aller Datens&#228;tze im Basket ist m&#246;glich.</p>

			<h3 id="Datensatz-Vollansicht">
				<span class="titlemark">3.4.</span><a>Datensatz-Vollansicht</a>
			</h3>
			
			<p>
				In der Datenvollansicht haben Sie die M&#246;glichkeit verschiedene
				<b>Social Bookmarking Services</b> zu nutzen. Derzeit bietet Pubman
				Bookmarking-Optionen f&#252;r <b>Delicious, Citeulike</b> und <b>Connotea</b>
				an. <br /> Ist f&#252;r den Datensatz ein&#160; <b>ISI Web of
					Knowledge Identifier</b> hinterlegt, dient dieser Identifier als Link
				zu ISI und bietet die M&#246;glichkeit nach dem angezeigten
				Datensatz bei ISI Web of Knowledge zu suchen, um dort
				m&#246;glicherweise erg&#228;nzende Informationen zum Artikel zu
				finden. <br /> Ist hinter dem Autor des angezeigten Datensatzes das
				Symbol einer Visitenkarte platziert, dient diese als Link zum <b>Researcher
					Posrtfolio</b> des Autors. Auf dieser Seite erh&#228;lt der Nutzer
				erg&#228;nzende Informationen &#252;ber&#160;den Autor. Alle in
				PubMan gespeicherten Publikationen des Autores sind dort gelistet.
				Au&#223;erdem bietet das Researcher Portfolio&#160;die
				M&#246;gllichkeit nach dem angezeigten Autor in <b>WorldCat</b> und
				<b>Google Scholar</b> zu suchen, um dar&#252;ber m&#246;glicherweise
				weitere Publikationen der gesuchten Person zu erhalten. <br /> Ist
				hinter der Organisation, zu der der Autor des angezeigten
				Datensatzes geh&#246;rt, ein Haus-Symbol platziert, kann dort eine
				Beschreibung der Organisation aufgerufen werden.
			</p>

			<h4 id="Freigabegeschichte_einsehen">
				<span class="titlemark">3.4.1.</span><a>Freigabegeschichte
					einsehen</a>
			</h4>
			
			<p class="noindent">Hier werden alle freigegebenen Versionen
				eines Datensatzes angezeigt, und Sie k&#246;nnen Ver&#228;nderungen
				am Metadatensatz nachvollziehen</p>

			<h4 id="Revisionen_einsehen">
				<span class="titlemark">3.4.2.</span><a>Revisionen einsehen</a>
			</h4>
			
			<p class="noindent">Eine Revision ist eine inhaltlich
				ver&#228;nderte oder neu bearbeitete Version, die mit dem
				urspr&#252;nglichen Datensatz verlinkt ist. Unter "Revisionen
				einsehen" werden Ihnen alle Revisionen eines Datensatzes angezeigt.
			</p>

			<h4 id="Statistik_zum_Datensatz_einsehen">
				<span class="titlemark">3.4.3.</span><a>Statistik zum Datensatz
					einsehen</a>
			</h4>
			
			<p class="noindent">Hier k&#246;nnen Sie einsehen, wie
				h&#228;ufig der Datensatz aufgerufen wurde und wie h&#228;ufig der
				Volltext heruntergeladen wurde. Bitte beachten Sie, dass die
				Statistiken nur einmal pro Nacht erneuert werden.</p>

			<h4 id="Bearbeitungsgeschichte">
				<span class="titlemark">3.4.4.</span><a>Bearbeitungsgeschichte
					(nur registrierte Nutzer)</a>
			</h4>
			
			<p class="noindent">Die Option ist nur f&#252;r registrierte
				Nutzer sichtbar. Es wird die vollst&#228;ndige
				Bearbeitungsgeschichte des Datensatzes angezeigt, also alle
				Aktionen, die vom System aufgezeichnet worden sind.</p>

			<h4 id="eSciDoc_help_de.jsp#ViewLocalTagsPage">
				<span class="titlemark">3.4.5.</span><a>Lokale Tags (nur
					f&#252;r registrierte Nutzer)</a>
			</h4>
			
			<p class="noindent">Hier k&#246;nnen Sie selbstgew&#228;hlte
				"Tags" bestimmen, mittels derer Sie die Datens&#228;tze
				kategorisieren k&#246;nnen und so z.B. f&#252;r bestimmte Zwecke
				Sets erstellen k&#246;nnen, z.B. "my best publications".</p>

			<h2 id="SubmissionPage">
				<span class="titlemark">4.</span><a>Dateneingabe</a>
			</h2>
			
			<p class="MsoNormal" style="margin-left: 0cm; text-indent: 0cm;">
				In PubMan gibt es grunds&#228;tzlich vier verschiedene
				M&#246;glichkeiten, Daten einzugeben.</p>
			<ul>
				<li><b>Detaillierte Eingabe:</b> Bei der detaillierten Eingabe
					steht Ihnen eine dokumentenspezifische Eingabemaske zur
					Verf&#252;gung, die alle relevanten Felder f&#252;r das von Ihnen
					gew&#228;hlte Genre enth&#228;lt.</li>
				<li><b>Einfache Eingabe:</b> Hier k&#246;nnen Sie eine kurze,
					schrittweise aufgebaute manuelle Eingabe vornehmen.</li>
				<li><b>Importieren:</b> Hier haben Sie die M&#246;glichkeit,
					eine BibTeX Referenz hochzuladen oder Metadaten inklusive
					Volltext(e) (wenn vorhanden) von arXiv, PubMed Central, SPIRES oder
					BioMed Central zu importieren. Bitte verwenden Sie hierf&#252;r die
					auf den jeweiligen Webseiten angegebenen IDs. (<b>Ausnahme</b>:
					Bitte beachten Sie, dass bei BioMed Central nur der hintere Teil
					der DOI - nach dem Schr&#228;gstrich - als ID anerkannt wird. <i>Beispiel:
						doi:10.1186<b>1471-2253-8-8</b>
				</i>)</li>
				<li><b>Massenimport:</b> Hier haben Sie die M&#246;glichkeit
					einen Massenimport von Web of Science Daten, EndNote, BibTeX oder
					RIS vorzunehmen. <i>Hinweis zum EndNote-Massenimport: Alle
						Autoren eines Datensatzes bekommen standardm&#228;&#223;ig die
						Organisation "Max Planck Society" zugewiesen. Gew&#252;nschte
						&#196;nderungen/Verfeinerungen in der Angabe der Organisation
						m&#252;ssen h&#228;ndisch nach dem Import im jeweiligen Datensatz
						nachbearbeitet werden.</i></li>
			</ul>

			<h3 id="neuen_Datensatz_anlegen">
				<span class="titlemark">4.1.</span><a>Einen neuen Datensatz
					anlegen</a>
			</h3>
			
			<p class="noindent">Bevor Sie einen neuen Datensatz anlegen
				k&#246;nnen, m&#252;ssen Sie zun&#228;chst die Eingabemethode
				bestimmen und dann die Collection ausw&#228;hlen, in der Sie den
				Eintrag vornehmen m&#246;chten. Diese w&#228;hlen Sie aus, indem Sie
				auf den Namen der Collection klicken.</p>

			<h3 id="Datensatz_bearbeiten">
				<span class="titlemark">4.2.</span><a>Anlegen und Bearbeiten
					eines Datensatzes</a>
			</h3>
			
			<p class="noindent">Bevor Sie Ihre Publikationsdaten eingeben,
				w&#228;hlen Sie bitte den Dokumenttyp (Genre) ihres Datensatzes aus.
				Sie bekommen dann, dem Dokumenttyp entsprechend, eine Eingabemaske
				angezeigt. Bitte beachten Sie, dass Sie den Dokumenttypen nur so
				lange &#228;ndern k&#246;nnen, bis Sie den Datensatz einmal
				speichern oder einstellen, beziehungsweise freigeben, da PubMan dann
				die Felder, die f&#252;r das gew&#228;hlte Genre nicht mehr
				ben&#246;tigt werden, herausl&#246;scht.</p>
			<p class="noindent">
				Bitte f&#252;llen Sie alle Felder aus, die mit einem Stern markiert
				sind, da diese Mindestangaben ben&#246;tigt werden, um einen
				Datensatz in PubMan anzulegen. Sollte eins der Felder nicht
				gef&#252;llt sein, so bekommen Sie eine Validierungsmeldung. Siehe
				auch <a href="#Validierung">Validierung</a>.
			</p>

			<h4 id="Personen_und_Organisationen">
				<span class="titlemark">4.2.1.</span><a>Personen und
					Organisationen eingeben</a>
			</h4>
			
			<p class="noindent">Sie haben zwei M&#246;glichkeiten, Personen
				einzugeben. Entweder alle einzeln, hierf&#252;r verwenden Sie die
				Eingabemaske f&#252;r Personen und klicken auf das Plussymbol, wenn
				Sie weitere Personen hinzuf&#252;gen m&#246;chten. Wenn der Name,
				den Sie eingeben, vom System erkannt wird, bekommen Sie eine
				Vorschlagsliste; Sie k&#246;nnen dann einen Namen ausw&#228;hlen
				oder die Liste mit ESC schlie&#223;en. Um mehrere Personen
				gleichzeitig einzugeben, klicken Sie bitte auf &#34;viele
				hinzuf&#252;gen&#34;. Dadurch &#246;ffnet sich dann ein Textfeld,in
				das Sie eine Liste von Personen entweder eingeben oder
				hineinkopieren k&#246;nnen. Diese Liste wird dann vom System in die
				einzelnen Felder f&#252;r die Eingabe von Personen eingef&#252;gt.
				Bitte beachten Sie, dass mindestens eine Person eine Affiliation
				tragen muss, da ansonsten der Eintrag nicht erstellt werden kann.</p>

			<h4 id="Zeitschriftennamen_eingeben">
				<span class="titlemark">4.2.2.</span><a>Zeitschriftennamen
					eingeben</a>
			</h4>
			
			<p>Wenn Sie als Quelle einen Zeitschriftennamen eingeben
				m&#246;chten, dann stellen Sie bitte zun&#228;chst als Genre der
				Quelle "Zeitschrift" ein. Wenn Sie nun anfangen, den
				Zeitschriftennamen einzutippen, werden Ihnen von PubMan
				Zeitschriftennamen vorgeschlagen. Sie k&#246;nnen entweder einen der
				vorgeschlagenen &#252;bernehmen, indem Sie Ihn ausw&#228;hlen und
				mit der Maus drauf klicken oder "Enter" dr&#252;cken, oder Sie
				k&#246;nnen einen neuen Zeitschriftennamen angeben. Die
				Vorschlagsliste schlie&#223;en Sie mit ESC, wenn Sie keinen der
				vorgeschlagenen Namen &#252;bernehmen m&#246;chten.</p>

			<h4 id="Sprache_der_Publikation_eingeben">
				<span class="titlemark">4.2.3.</span><a>Sprache der Publikation
					angeben</a>
			</h4>
			
			<p>Auch bei der Sprache der Publikation wird mit Vorschlagslisten
				gearbeitet. Sie k&#246;nnen hier sogar die Sprache entweder in
				Deutsch oder in Englisch eingeben. Sie wird in beiden F&#228;llen
				erkannt.</p>

			<h4 id="Ein_Datum_eingeben">
				<span class="titlemark">4.2.4.</span><a>Ein Datum eingeben</a>
			</h4>
			
			<p>
				Das Datum sollte folgendes Format haben: JJJJ-MM-TT. Sie k&#246;nnen
				jedoch auch Begriffe wie "gestern", "letztes Jahr" oder
				&#228;hnliches angeben und PubMan wandelt diese Angabe dann in das
				richtige Format um. <br /> <i>Hinweis zur Angabe eines Datums
					f&#252;r die Publikationstypen "Serie" und "Zeitschrift": Das
					anzugebende Datum entspricht in diesen beiden F&#228;llen dem
					Beginn der jeweiligen Ver&#246;ffentlichung.</i>
			</p>

			<h4 id="Rechte">
				<span class="titlemark">4.2.5.</span><a>Rechte-Informationen
					f&#252;r eine Datei hinterlegen</a>
			</h4>
			
			<p>
				Sie haben die M&#246;glichkeit die dem Datensatz angef&#252;gten
				Dateien und externen Referenzen mit verschiedenen
				Rechte-Informationen zu versehen. Sie k&#246;nnen Zugriffsrechte
				("&#246;ffentlich", "privat", "eingeschr&#228;nkt") festlegen bzw.
				diese f&#252;r definierte Nutzergruppen vergeben. Sie k&#246;nnen
				auch ein "Copyright Statement" (als Freitext) abgeben sowie ein
				"Copyright Date" (im Datums-Format) zur Verf&#252;gung stellen.
				Au&#223;erdem haben Sie die M&#246;glichkeit den gespeicherten
				Datensatz mit einer Creative Commons Lizenz (<a
					href="http://creativecommons.org/">http://creativecommons.org/</a>)
				zu versehen. Alle diese Angaben sind optional. Bitte beachten Sie,
				die Richtigkeit dieser Rechte-Angaben im Vorfeld zu
				&#252;berpr&#252;fen!
			</p>

			<h3 id="Revision_anlegen">
				<span class="titlemark">4.3.</span><a>Eine neue Revision anlegen</a>
			</h3>
			
			<p class="noindent">Eine neue Revision ist eine intellektuell
				&#252;berarbeitete Version eines Werkes (z.B. erst Pre Print, dann
				Post Print). Bitte beachten Sie, dass jede neue Revision ein neuer,
				separater Datensatz ist, der mit dem urspr&#252;nglichen Datensatz
				verkn&#252;pft ist ("ist Revision von").</p>
			<p class="noindent">Bevor Sie die Revision (den neuen Datensatz)
				freigeben, haben Sie die M&#246;glichkeit, einen Kommentar hierzu
				abzugeben.</p>

			<h3 id="Validierung">
				<span class="titlemark">4.4.</span><a>Validierung</a>
			</h3>
			
			<p class="noindent">Sie k&#246;nnen Ihren Datensatz validieren,
				wenn Sie &#252;berpr&#252;fen wollen, ob er den Kriterien der
				Collection entspricht. Die Auswahlkriterien werden pro Institut
				definiert und sollen die Qualit&#228;tssicherung erleichtern.</p>
			<p class="indent">Bitte beachten Sie, dass Sie einen Datensatz
				nur in PubMan einstellen k&#246;nnen, wenn er den Kriterien der
				Collection entspricht.</p>

			<h3 id="Speichern">
				<span class="titlemark">4.5.</span><a>Speichern des Datensatzes</a>
			</h3>
			
			<p class="noindent">Wenn Sie den "speichern" Knopf
				bet&#228;tigen, wird ihr Datensatz in den Status "pending" gesetzt
				und kann nur von Ihnen eingesehen und bearbeitet werden. Dies kann
				z.B. n&#252;tzlich sein, wenn Sie eine Aufnahme erst zu einem
				sp&#228;teren Zeitpunkt fertig stellen m&#246;chten. Sie k&#246;nnen
				Ihren Datensatz dann wieder &#252;ber Ihren Arbeitsplatz aufrufen
				und editieren.</p>

			<h3 id="Loeschen_des_Datensatzes">
				<span class="titlemark">4.6.</span><a>L&#246;schen des
					Datensatzes</a>
			</h3>
			
			<p class="noindent">Bitte beachten Sie, dass Sie nur
				Datens&#228;tze im Status "pending" l&#246;schen k&#246;nnen.
				Bereits freigegebene Datens&#228;tze k&#246;nnen lediglich
				zur&#252;ckgezogen werden, da Sie bereits zitierf&#228;hig mit einer
				PID ausgezeichnet sind.</p>
				
			<h3 id="Einstellen_eines_Datensatzes">
				<span class="titlemark">4.7.</span><a>Einstellen eines
					Datensatzes</a>
			</h3>
			<p class="noindent">Der Depositor kann seine Datens&#228;tze im
				Standard Workflow einstellen.</p>
			<p class="noindent">Wenn Sie den "einstellen" Knopf
				bet&#228;tigen, wird Ihr Datensatz zun&#228;chst validiert. Wenn Ihr
				Datensatz valide ist, werden Sie auf eine Maske navigiert, auf der
				Sie diese Aktion best&#228;tigen und kommentieren k&#246;nnen. Der
				Datensatz wird nun an den Moderator Ihrer Collection weitergeleitet,
				der die Daten &#252;berpr&#252;ft und den Datensatz entweder
				f&#252;r die &#246;ffentliche Sicht freigibt oder ihn an Sie zur
				weiteren Bearbeitung zur&#252;ckschickt. Datens&#228;tze, die an Sie
				zur&#252;ckgeschickt wurden, finden Sie in Ihrem Arbeitsplatz unter
				dem Filter "in &#220;berarbeitung".</p>

			<h4 id="Kommentar_Einstellen">
				<span class="titlemark">4.7.1.</span><a>Kommentar zum Einstellen</a>
			</h4>
			
			<p class="noindent">In diesem Kommentarfeld k&#246;nnen Sie dem
				Nutzer, der den Datensatz nach Ihnen im Workflow bearbeitet, einen
				Kommentar hinterlassen, den er dann einsehen kann.</p>

			<h3 id="Freigeben">
				<span class="titlemark">4.8.</span><a>Freigeben eines
					Datensatzes</a>
			</h3>
			
			<p class="noindent">Im Simple Workflow k&#246;nnen Sie Ihre
				Datens&#228;tze direkt f&#252;r die &#246;ffentliche Sicht freigeben
				(sie m&#252;ssen nicht vorher eingestellt werden).</p>

			<h4 id="Kommentar_zum_Freigeben">
				<span class="titlemark">4.8.1.</span><a>Kommentar zum Freigeben</a>
			</h4>
			
			<p class="noindent">In diesem Kommentarfeld k&#246;nnen Sie dem
				Nutzer, der den Datensatz nach Ihnen im Workflow bearbeitet, einen
				Kommentar hinterlassen, den er dann einsehen kann.</p>

			<h2 id="Qualitaetssicherung_in_PubMan">
				<span class="titlemark">5.</span><a>Qualit&#228;tssicherung in
					PubMan</a>
			</h2>
			
			<p class="noindent">Um eine hohe Qualit&#228;t der in PubMan
				enthaltenen Daten zu garantieren, ist ein
				Qualit&#228;tssicherungsworkflow implementiert worden, so dass alle
				Datens&#228;tze bestimmte Qualit&#228;tskriterien erf&#252;llen.</p>
			<p class="noindent">F&#252;r die Qualit&#228;tskontrolle ist der
				Moderator zust&#228;ndig. Hierf&#252;r stehen ihm verschiedene
				M&#246;glichkeiten zur Verf&#252;gung. Er kann die Datens&#228;tze
				modifizieren oder dem Depositor zur weiteren Bearbeitung
				zur&#252;ckschicken; wenn die Qualit&#228;t stimmt, kann er die
				Datens&#228;tze akzeptieren und f&#252;r die &#246;ffentliche Sicht
				freischalten.</p>
			<p class="noindent">Eine weitere Option der
				Qualit&#228;tssicherung, die ausschlie&#223;lich dem Depositor
				vorbehalten ist, ist das Zur&#252;ckziehen eines Datensatzes: eine
				Alternative zum L&#246;schen, die dem Zweck der Langzeitarchivierung
				dient und die Zitierbarkeit der Datens&#228;tze weiterhin
				garantiert.</p>

			<h3 id="Ueberarbeitung">
				<span class="titlemark">5.1.</span><a>Einen Datensatz zur
					&#220;berarbeitung an den Depositor zur&#252;cksenden</a>
			</h3>
			
			<p class="noindent">Im Standard Workflow kann der Moderator
				Datens&#228;tze, die den Qualit&#228;tsstandards nicht entsprechen,
				dem Depositor zum Bearbeiten zur&#252;ckschicken. Datens&#228;tze im
				Status &#34;In &#220;berarbeitung&#34; sind sowohl f&#252;r den
				Depositor als auch f&#252;r den Moderator sichtbar, k&#246;nnen
				jedoch nur noch vom Depositor bearbeitet werden, bis sie erneut
				eingestellt werden.</p>

			<h3 id="Datensatz_modifizieren">
				<span class="titlemark">5.2.</span><a>Einen Datensatz
					modifizieren</a>
			</h3>
			
			<p class="noindent">Datens&#228;tze im Status "pending"
				k&#246;nnen vom Ersteller des Datensatzes noch so lange bearbeitet
				werden, bis er zufrieden ist und den Datensatz in PubMan einstellt.
				Bereits "eingestellte" bzw. "freigegebene" (je nach Workflow-Typ)
				Datens&#228;tze k&#246;nnen vom Moderator ver&#228;ndert werden,
				"freigegebene" Datens&#228;tze auch vom Ersteller.</p>

			<h3 id="Akzeptieren">
				<span class="titlemark">5.3.</span><a>Akzeptieren</a>
			</h3>
			
			<p class="noindent">Wenn Sie den Datensatz, den Sie
				ver&#228;ndert haben, f&#252;r die &#246;ffentliche Sicht wieder
				freischalten m&#246;chten, dann m&#252;ssen Sie ihn akzeptieren.</p>

			<h4 id="Kommentar_Akzeptieren">
				<span class="titlemark">5.3.1.</span><a>Kommentar zum
					Akzeptieren</a>
			</h4>
			
			<p class="noindent">Sie haben hier die M&#246;glichkeit einen
				Kommentar zu Ihrer &#220;berarbeitung abzugeben, der dann
				&#246;ffentlich sichtbar ist &#252;ber die Freigabegeschichte.</p>

			<h3 id="Datensatz_zurueckziehen">
				<span class="titlemark">5.4.</span><a>Einen Datensatz
					zur&#252;ckziehen</a>
			</h3>
			
			<p class="noindent">Freigegebene Datens&#228;tze k&#246;nnen
				nicht mehr gel&#246;scht, sondern nur noch zur&#252;ckgezogen
				werden; die Metadaten bleiben dabei sichtbar, der Volltext jedoch
				nicht, sodass die Datens&#228;tze weiterhin zitierbar sind.
				Zur&#252;ckgezogene Datens&#228;tze sind nicht mehr suchbar, sondern
				k&#246;nnen nur angezeigt werden, wenn die ID des Datensatzes
				eingegeben wird.</p>
			<p class="noindent">Datens&#228;tze k&#246;nnen nur vom Ersteller
				(Depositor) zur&#252;ckgezogen werden. Bitte geben Sie den Grund an,
				weswegen Sie das Item zur&#252;ckziehen m&#246;chten.</p>

			<h2 id="Hilfsmittel_zur_Datenverwaltung">
				<span class="titlemark">6.</span><a>Hilfsmittel zur
					Datenverwaltung</a>
			</h2>
			
			<p class="noindent">Als Depositor haben Sie &#252;ber "Meine
				Datens&#228;tze" verschiedene M&#246;glichkeiten, ihre
				Datens&#228;tze zu verwalten. Dort erhalten Sie eine &#220;bersicht
				Ihrer Datens&#228;tze und der verf&#252;gbaren
				Verwaltungsm&#246;glichkeiten. Sie k&#246;nnen, z.B. die Ansicht
				&#228;ndern, die Datens&#228;tze nach ihrem Status filtern oder nach
				verschiedenen Kriterien sortieren, ausgew&#228;hlte Datens&#228;tze
				exportieren oder einen Basket erstellen.</p>

			<h3 id="Meine_Datensaetze">
				<span class="titlemark">6.1.</span><a>Meine Datens&#228;tze</a>
			</h3>
			
			<p class="noindent">Unter &#34;Meine Datens&#228;tze&#34; werden
				alle Eintr&#228;ge aufgelistet, die der Depositor angelegt hat. Es
				besteht die M&#246;glichkeit, sie nach Status zu filtern. So kann
				der Depositor z.B. Daten, die noch nicht fertig gestellt sind,
				erneut &#252;berarbeiten oder l&#246;schen oder er kann Daten, die
				vom Moderator zur Bearbeitung zur&#252;ckgeschickt wurden,
				anreichern. Ein weiterer Filter zeigt Ihnen importierte
				Datens&#228;tze nach datum sortiert an.</p>

			<h4 id="Status_eines_Datensatzes">
				<span class="titlemark">6.1.1.</span><a>Status eines Datensatzes</a>
			</h4>
			
			<p>Bitte klicken Sie auf "Filter" und &#246;ffnen&#160;die
				pull-down Liste "Status", wenn Sie Datens&#228;tze in einem anderen
				Status angezeigt bekommen m&#246;chten. Folgende Statusangaben sind
				m&#246;glich:</p>
			<ul>
				<li><b>"pending":</b> Der Datensatz wurde von Ihnen eingegeben
					und bisher nur abgespeichert, da Sie noch nicht mit der Eingabe
					fertig waren. Dieser Datensatz muss noch eingestellt oder
					freigeschaltet werden, sobald die Bearbeitung abgeschlossen ist.</li>
				<li><b>"eingestellt":</b> Dieser Status kommt nur im Standard
					Workflow vor. Dies sind Datens&#228;tze, die Sie fertig eingegeben
					haben und die derzeit beim Moderator Ihrer Collection zur Kontrolle
					vorliegen.</li>
				<li><b>"freigegeben":</b> Datens&#228;tze im Status
					"freigegeben" sind &#246;ffentlich sichtbar. &#196;nderungen
					k&#246;nnen von Ihnen und dem Moderator vorgenommen werden.</li>
				<li><b>"zur&#252;ckgezogen":</b> Datens&#228;tze im Status
					"zur&#252;ckgezogen" sind nicht mehr &#252;ber die Suche oder
					&#252;ber die Organisationen einsehbar. Sie k&#246;nnen lediglich
					von Ihnen als Ersteller eingesehen werden oder wenn die genaue URL
					zu dem Datensatz eingegeben wird.</li>
				<li><b>"in &#220;berarbeitung":</b> dieser Status kommt nur im
					Standard Workflow vor. Dies sind Datens&#228;tze, die der Moderator
					zur &#220;berarbeitung an Sie zur&#252;ckgeschickt hat. Achtung,
					vergessen Sie bitte nicht die Datens&#228;tze wieder einzustellen,
					wenn Sie mit der &#220;berarbeitung fertig sind!</li>
			</ul>

			<h4 id="Datensaetze_Sortieren">
				<span class="titlemark">6.1.2.</span><a>Datens&#228;tze
					Sortieren</a>
			</h4>
			
			<p class="noindent">Hier k&#246;nnen Sie das Sortierkriterium
				Ihrer Liste &#228;ndern. Dar&#252;ber hinaus k&#246;nnen Sie
				angeben, ob die Liste aufsteigend oder absteigend sortiert werden
				soll.</p>

			<h3 id="Qualitaetssicherung">
				<span class="titlemark">6.2.</span><a>Qualit&#228;tssicherungs
					Bereich</a>
			</h3>
			
			<p class="noindent">Im Qualit&#228;tssicherungs Bereich werden
				alle Daten angezeigt, die f&#252;r den Moderator relevant sind.
				Datens&#228;tze in Status &#34;eingestellt&#34; m&#252;ssen noch
				&#252;berpr&#252;ft werden. Der Moderator kann die Datens&#228;tze
				dann entweder akzeptieren (f&#252;r die &#246;ffentliche Sicht
				freischalten) oder an den Depositor zur &#220;berarbeitung
				zur&#252;ckschicken ("zum Bearbeiten schicken").</p>
			<p class="noindent">Bitte beachten Sie, dass das oben Genannte
				nur f&#252;r den Standard Workflow gilt. Im Simple Workflow gibt es
				keine Datens&#228;tze im Status "eingestellt".</p>

			<h4 id="Status_eines_Datensatzes_QA">
				<span class="titlemark">6.2.1.</span><a>Status eines Datensatzes</a>
			</h4>
			
			<p class="noindent">Bitte klicken Sie auf "Filter" und
				&#246;ffnen die pull-down Liste, wenn Sie Datens&#228;tze in einem
				anderen Status angezeigt bekommen m&#246;chten. Folgende
				Statusangaben sind m&#246;glich:</p>
			<ul>
				<li><b>"eingestellt":</b> Dieser Status kommt nur im Standard
					Workflow vor. Dies sind Datens&#228;tze, die von Ihnen noch
					&#252;berpr&#252;ft werden m&#252;ssen. Sie k&#246;nnen dann
					entweder den Datensatz selbst editieren und f&#252;r die
					&#246;ffentliche Sicht freischalten oder an den Depositor zur
					&#220;berarbeitung zur&#252;ckschicken.</li>
				<li><b>"freigegeben":</b> Datens&#228;tze im Status
					"freigegeben" sind &#246;ffentlich sichtbar und k&#246;nnen von
					Ihnen bei Bedarf noch ver&#228;ndert werden.</li>
				<li><b>"in &#220;berarbeitung":</b> Dieser Status kommt nur im
					Standard Workflow vor. Dies sind Datens&#228;tze, die Sie an den
					Depositor zur &#220;berarbeitung zur&#252;ckgeschickt haben.</li>
			</ul>

			<h4 id="Datensaetze_sortieren_QA">
				<span class="titlemark">6.2.2.</span><a>Datens&#228;tze	Sortieren</a>
			</h4>
			
			<p class="noindent">Hier k&#246;nnen Sie das Sortierkriterium
				Ihrer Liste &#228;ndern. Dar&#252;ber hinaus k&#246;nnen Sie
				angeben, ob die Liste aufsteigend oder absteigend sortiert werden
				soll.</p>

			<h3 id="Import_Bereich">
				<span class="titlemark">6.3.</span><a>Import Bereich</a>
			</h3>
			
			<p class="noindent">Im Import Bereich k&#246;nnen Sie sich den
				Status Ihrer Importe nachverfolgen und Stapelverarbeitungen
				vornehmen, wie z.B. alle Datens&#228;tze, die Sie importiert haben
				auf einmal freischalten. Mit dem &#34;Entfernen&#34;-Button
				k&#246;nnen Sie den Import l&#246;schen &#8211; ohne gleichzeitig
				die importierten Datensauml;tze zu l&#246;schen. Mit dem
				&#34;L&#246;schen"-Button werden die Datens&#228;tze gel&#246;scht,
				sofern sie sich noch im Status "pending" befinden.</p>
		</div>
	</f:view>

</body>

</html>
