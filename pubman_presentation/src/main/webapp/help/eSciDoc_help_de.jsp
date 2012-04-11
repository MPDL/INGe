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
		        <title>PubMan Onlinehilfe</title>
		        <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"/>
		        <meta name="generator" content="TeX4ht (http://www.cse.ohio-state.edu/~gurari/TeX4ht/)"/>
		        <meta name="originator" content="TeX4ht (http://www.cse.ohio-state.edu/~gurari/TeX4ht/)"/>
		        <!-- html -->
		        <meta name="src" content="eSciDoc_help_de.tex"/>
		        <meta name="date" content="2008-10-08 13:10:00"/>
		        <jsp:directive.include file="/header/ui/StandardImports.jspf" />
		        
		        <!-- <link rel="stylesheet" type="text/css" href="eSciDoc_help_de.css"> -->
		    </head>
		    <body>
		        <div class="maketitle wrapper" style="padding: 0.74em; font-size: 129% !important; width: auto;">
		            <h2>PubMan Onlinehilfe</h2>
		            <p class="MsoNormal">
		                <span class="cmr-12x-x-120">20.
		                    April 2009</span>
		            </p>
		            <h3>Inhaltsverzeichnis</h3>
		            <h3><span style="" lang="EN-GB">Contents</span></h3>
		            <p class="MsoNormal">
		                <span class="sectiontoc">1.<a href="#HomePage">&#220;ber PubMan</a></span>
		                <span class="subsectiontoc">
		                    <br/>
		                    <span class="sectiontoc">2. <a href="#Allgemeines">Allgemeines</a></span>
		                    <br/>
		                    <span class="subsectiontoc">&#160;&#160;&#160; 
		                        2.1. <a href="#login">Login</a></span><a href="#Login"></a>
		                    <br/>
		                    <span style=""></span>&#160;&#160;&#160; 
		                    2.2. <a href="#Nutzerrollen_und_Workflows">Nutzerrollen und Workflows</a>
		                    <br/>
		                    <span style=""></span><span style="" lang="EN-GB">&#160;&#160;&#160; 
		                        &#160;&#160;&#160;2.2.1 <a href="#Workflows"><span style="">Workflows</span></a>
		                        <br/>
		                        &#160;&#160;&#160; &#160;&#160;&#160;
		                        &#160;&#160;&#160; 2.2.1.1.
		                    </span>
		                    <a href="#Depositor"><span style="" lang="EN-GB"></span></a><a href="#Standard_Workflow">Standard Workflow&#160;</a><span style="" lang="EN-GB">
		                        
		                      
		                        <br/>
		                        &#160;&#160;&#160; &#160;&#160;&#160;
		                        &#160;&#160;&#160; 2.2.1.2.
		                    </span>
		                    <a href="#Moderator"><span style="" lang="EN-GB"></span></a><a href="#Simple_Workflow">Simple Workflow&#160;</a><span style="" lang="EN-GB">
		                        
		                      
		                        <br/>
		                        <span style=""></span>&#160;&#160;&#160; 
		                        &#160;&#160;&#160; 2.2.2. <a href="#PubMan_Nutzerrollen"><span style="">Pubman Nutzerrollen </span></a>
		                        
		                      
		                    </span>
		                    <span style="" lang="EN-GB"></span>
		                    <br/>
		                    <span class="sectiontoc">3 <a href="#PubMan+Funktionalit%E4ten">PubMan Funktionalit&#228;ten</a></span>
		                    <br/>
		                    <span class="subsectiontoc">&#160;&#160;&#160; 
		                        3.1. <a href="#Suchm%F6glichkeiten+in+PubMan">Suchm&#246;glichkeiten in PubMan</a></span>
		                    <br/>
		                    <span class="subsectiontoc">&#160;&#160;&#160; 
		                        3.1.1 <a href="#Einfache_Suche">Einfache Suche</a></span>
		                    <br/>
		                    <span class="subsectiontoc">&#160;&#160;&#160; 
		                        3.1.2. <a href="#AdvancedSearchPage">Erweiterte Suche</a></span>&#160;
		                    <br/>
		                    <span class="subsectiontoc">&#160;&#160;&#160; &#160;&#160; &#160;&#160;&#160; &#160;3.1.2.1 <a href="#Datum-Suche">Suche nach Daten und Zeitspannen</a>
		                        <br/>
		                        &#160;&#160;&#160; 3.1.3. <a href="#AffiliationTreePage">Organisationssuche</a>
		                    </span>
		                    <br/>
		                    <span class="subsectiontoc">&#160;&#160;&#160; 
		                        3.2. <a href="#Suchergebnisse">Suchergebnisse</a></span>
		                    <br/>
		                    <span class="subsectiontoc">&#160;&#160;&#160; 
		                        3.2.1. <a href="#Exportieren">Exportieren</a></span><a href="#Exportieren"></a>
		                    <br/>
		                    <span class="subsubsectiontoc">&#160;&#160;&#160; 
		                        &#160;&#160;&#160; 3.2.1.1. <a href="#Export_per_E-Mail_verschicken">Export
		                            per E-Mail</a></span><a href="#Export_per_E-Mail_verschicken"> verschicken</a>
		                    <br/>
		                    <span class="subsectiontoc">&#160;&#160;&#160; 
		                        3.4. <a href="#Datensatz-Vollansicht">Datensatz-Vollansicht</a></span>
		                    <br/>
		                    <span class="subsectiontoc">&#160;&#160;&#160; 
		                        3.4.1. <a href="#Freigabegeschichte_einsehen">Freigabegeschichte
		                            einsehen</a></span>
		                    <br/>
		                    <span class="subsectiontoc">&#160;&#160;&#160; 
		                        3.4.1. <a href="#Revisionen_einsehen">Revisionen einsehen</a>
		                        <br/>
		                        <span class="subsectiontoc">&#160;&#160;&#160; 
		                            3.4.3. <a href="#Statistik_zum_Datensatz_einsehen">Statistik
		                                zum Datensatz
		                                einsehen</a></span>
		                        <br/>
		                        <span style=""></span>&#160;&#160;&#160; 
		                        3.4.4. <a href="#Bearbeitungsgeschichte">Bearbeitungsgeschichte
		                            (nur registrierte Nutzer)</a>
		                        <br/>
		                        <span style=""></span>&#160;&#160;&#160; 
		                        3.4.5. <a href="eSciDoc_help_de.jsp#ViewLocalTagsPage">Lokale Tags (nur registrierte Nutzer)</a>
		                    </span>
		                    <span class="sectiontoc">
		                        <br/>
		                        4.<a href="#SubmissionPage">Dateneingabe</a>
		                    </span>
		                    <span class="sectiontoc">
		                        <br/>
		                        <span style=""></span>&#160;&#160;&#160; 
		                        4.1. <a href="#neuen_Datensatz_anlegen">Einen neuen Datensatz anlegen</a>
		                        <br/>
		                        <span style=""></span>&#160;&#160;&#160; 
		                        4.2. <a href="#Datensatz_bearbeiten">Anlegen und Bearbeiten eines Datensatzes</a>
		                        <br/>
		                        &#160;&#160;&#160; 
		                        4.2.1. <a href="#Personen_und_Organisationen">Personen und
		                            Organisationen</a>
		                    </span>
		                    <a href="#Personen_und_Organisationen"> eingeben</a>
		                    <br/>
		                    &#160;&#160;&#160; 4.2.2. <a href="#Zeitschriftennamen_eingeben">Zeitschriftennamen
		                        eingeben</a>
		                    <br/>
		                    &#160;&#160;&#160; 4.2.3. <a href="#Sprache_der_Publikation_eingeben">Sprache der
		                        Publikation angeben</a>
		                    <br/>
		                    &#160;&#160;&#160; 4.2.4. <a href="#Ein_Datum_eingeben">Ein
		                        Datum eingeben</a>
		                    <br/>
		                    <span class="sectiontoc">&#160;&#160;&#160;</span> 4.2.5. <a href="#Rechte">Rechte-Informationen f&#252;r eine Datei hinterlegen</a>
		                    <br/>
		                    &#160;&#160;&#160; 4.3.<a href="#Revision_anlegen">Eine neue Revision anlegen</a>
		                    <br/>
		                    &#160;&#160;&#160; 4.4.<a href="#Validierung">Validierung</a>
		                    <br/>
		                    &#160;&#160;&#160; 4.5.<a href="#Speichern">Speichern eines Datensatzes</a>
		                    <br/>
		                    &#160;&#160;&#160; 4.6.<a href="#L%F6schen_des_Datensatzes">L&#246;schen eines Datensatzes</a>
		                    <br/>
		                    &#160;&#160;&#160; 4.7.<a href="#Einstellen_eines_Datensatzes">Einstellen eines Datensatzes</a>
		                    <br/>
		                    &#160;&#160;&#160; 4.7.1.<a href="#Kommentar_Einstellen">Kommentar zum Einstellen</a>
		                    <br/>
		                    &#160;&#160;&#160; 4.8.<a href="#Freigeben">Freigeben
		                        eines Datensatzes</a>
		                    <br/>
		                    &#160;&#160;&#160; 
		                    4.8.1. <a href="#Kommentar_zum_Freigeben">Kommentar zum
		                        Freigeben</a>
		                    
		                  
		                </span>
		                <br/>
		                5. <a href="#Qualit%E4tssicherung+in+PubMan">Qualit&#228;tssicherung in PubMan</a>
		                <span class="sectiontoc">
		                    <br/>
		                    &#160;&#160;&#160; 
		                    5.1. <a href="#%DCberarbeitung">Einen Datensatz zur &#220;berarbeitung an den Depositor zur&#252;cksenden</a>
		                </span>
		                <span class="sectiontoc">
		                    <br/>
		                    &#160;&#160;&#160; 
		                    5.2. <a href="#Einen%20Datensatz%20modifizieren">Einen Datensatz modifizieren</a>
		                </span>
		                <span class="sectiontoc">
		                    <br/>
		                    &#160;&#160;&#160; 
		                    5.3. <a href="#Akzeptieren">Akzeptieren</a>
		                </span>
		                <span class="sectiontoc">
		                    <br/>
		                    &#160;&#160;&#160; 
		                    5.3.1. <a href="#Kommentar_Akzeptieren">Kommentar zum Akzeptieren</a>
		                </span>
		                <span class="sectiontoc">
		                    <br/>
		                    &#160;&#160;&#160; 
		                    5.4. <a href="#Datensatz_zur%FCckziehen">Einen Datensatz zur&#252;ckziehen</a>
		                </span>
		                <span class="sectiontoc">
		                    <br/>
		                    6.&#160;<a href="#Hilfsmittel%20zur%20Datenverwaltung">Hilfsmittel zur Datenverwaltung</a>
		                </span>
		                <span class="subsectiontoc">
		                    <br/>
		                    &#160;&#160;&#160; 
		                    6.1. <a href="#Meine_Datens%E4tze">Meine Datens&#228;tze</a>
		                </span>
		                <span class="subsectiontoc">
		                    <br/>
		                    &#160;&#160;&#160; 
		                    6.1.1. <a href="#Status_eines_Datensatzes">Status eines
		                        Datensatzes</a>
		                </span>
		                <span class="subsectiontoc">
		                    <br/>
		                    &#160;&#160;&#160; 
		                    6.1.2. <a href="#Datens%E4tze_Sortieren">Datens&#228;tze
		                        sortieren</a>
		                </span>
		                <span class="sectiontoc">
		                    <br/>
		                    &#160;&#160;&#160; 
		                    6.2. <a href="#Qualit%E4tssicherung">Qualit&#228;tssicherungs Bereich</a>
		                </span>
		                <span class="subsectiontoc">
		                    <br/>
		                    &#160;&#160;&#160; 
		                    6.2.1. <a href="#Status_eines_Datensatzes_QA">Status eines Datensatzes</a>
		                </span>
		                <span class="sectiontoc">
		                    <br/>
		                    &#160;&#160;&#160; 
		                    6.2.2. <a href="#Datens%E4tze_sortieren_QA">Datens&#228;tze sortieren</a>
		                </span>
		                <span class="sectiontoc">
		                    <br/>
		                    &#160;&#160;&#160; 
		                    6.3. <a href="#Import%20Bereich">Import Bereich</a>
		                </span>
		                <span class="sectiontoc">
		                    <br/>
		                </span>
		            </p>
		            <h3><span class="titlemark">1. </span><a name="HomePage"></a>&#220;ber PubMan</h3>
		            PubMan unterst&#252;tzt Forschungsorganisationen im Management, der
		            Verbreitung und der Nachnutzung von Publikationen und Supplementary
		            Material. Die Anwendung PubMan ist eine Komponente der eResearch
		            Infrastruktur der Max-Planck-Gesellschaft und basiert auf der
		            service-orientierten Architektur von eSciDoc. Weitere Informationen 
		            finden Sie unter: <a href="http://colab.mpdl.mpg.de/mediawiki/Portal:PubMan">http://colab.mpdl.mpg.de/mediawiki/Portal:PubMan</a>
		            <h3><span class="titlemark"></span>2.<a name="Allgemeines"></a> Allgemeines</h3>
		            <h4><span class="titlemark"></span>2.1.<a name="login"></a><span style="" lang="EN-GB"> Login
		                    
		                  
		                </span></h4>
		            <p class="noindent">
		                Bitte geben Sie Ihren Nutzernamen und
		                Ihr Passwort ein, um
		                alle Funktionalit&#228;ten nutzen zu k&#246;nnen, die nur
		                registrierten Nutzern zur
		                Verf&#252;gung stehen, z.B. um Datens&#228;tze in PubMan
		                einzugeben.
		                
		                    <span style="font-weight: bold;">&#160;</span>
		              
		            </p>
		            <p class="noindent">
		                
		                    <span style="font-weight: bold;"></span>
		                    Wenn Sie noch
		                    &#252;ber kein Login f&#252;r PubMan verf&#252;gen, dann 
		                    wenden Sie sich bitte an: <a href="mailto:pubman-support@gwdg.de">PubMan
		                        Support.</a>
		              
		            </p>
		            <h4><b style=""><span style="" lang="EN-GB">2.2.<a name="Nutzerrollen_und_Workflows"></a>Nutzerrollen
		                        und
		                        Workflows</span></b></h4>
		            <p class="MsoNormal">
		                Derzeit sind in PubMan zwei Workflows implementiert: Zum einen ein sehr 
		                einfacher Workflow, genannt <a href="#Simple_Workflow">Simple
		                    Workflow</a>, zum anderen der so genannte<a href="#Standard_Workflow">Standard Workflow</a>,
		                bei dem eine Publikation erst nach Kontrolle
		                und&#160;Zustimmung&#160;
		                einer autorisierten Person, z.B. einem/r Bibliotekar/in,
		                f&#252;r die &#214;ffentlichkeit sichtbar gemacht
		                wird.&#160;
		            </p>
		            <p class="MsoNormal">
		                Zus&#228;tzlich
		                zu den Workflows gibt es
		                Nutzerrollen mit unterschiedlichen Privilegien. Diese sind vom
		                Grundprinzip her gleich aufgebaut, die Berechtigungen k&#246;nnen
		                je
		                nach Workflow anders sein. Die Rolle des Depositors, beispielsweise ist
		                immer eine Person (z.B. ein Wissenschaftler), die Daten eingibt.
		                Wohingegen ein Moderator (z.B. ein Bibliothekar) selbst keine Daten
		                eingeben, sondern lediglich korrigieren oder erg&#228;nzen kann.&#160;
		            </p>
		            <p class="MsoNormal">
		                Da die Rollen
		                in den Instituten sehr
		                unterschiedlich besetzt sein k&#246;nnen, k&#246;nnen Rollen
		                auch
		                kombiniert werden. So kann ein Nutzer beispielsweise auch Depositor und
		                Moderator in einem sein.
		            </p>
		            <h4><b style="">2.2.1.<a name="Workflows"></a>Workflows</b></h4>
		            <p class="MsoNormal">
		                <b style="">2.2.1.1.</b>
		                <b style=""><span style="" lang="EN-GB"><a name="Standard_Workflow"></a>Standard Workflow
		                        
		                      
		                    </span></b>
		            </p>
		            <p class="MsoNormal">
		                Im
		                Standard Workflow werden die Datens&#228;tze nach ihrer Eingabe
		                (Status: "pending")
		                vom Depositor&#160;eingestellt
		                (Status: "eingestellt"), und dann vom Moderator
		                &#252;berpr&#252;ft. Nach der
		                Qualit&#228;tskontrolle kann der Moderator den Datensatz entweder
		                akzeptieren
		                (Status: "freigegeben") oder, wenn noch weitere
		                Angaben ben&#246;tigt werden, dem Depositor
		                zur &#220;berarbeitung zur&#252;ckschicken (Status: "in
		                &#220;berarbeitung"). Der Moderator und der Depositor haben die
		                M&#246;glichkeit, einen bereits freigegebenen Datensatz nochmals zu
		                bearbeiten. Nach einer erneuten Bearbeitung durch den Depositor 
		                erscheint der Datensatz wieder im Status "pending". 
		            </p>
		            <p class="MsoNormal">
		                <b style="">2.2.1.2.<a name="Simple_Workflow"></a>Simple Workflow
		                    
		                  
		                </b>
		            </p>
		            <p class="MsoNormal">
		                Hier
		                kann der Depositor einen Datensatz eingeben (Status des Datensatzes:
		                "pending") und f&#252;r die
		                &#246;ffentliche Sicht freigeben (Status: "freigegeben"). Der
		                Moderator und der Depositor k&#246;nnen dann den
		                bereits
		                freigegebenen Datensatz noch modifizieren. Nach der Modifikation kann
		                der Moderator den Datensatz
		                akzeptieren (Status: "freigegeben"). Die Option "zum Bearbeiten
		                schicken" ist
		                im Simple Workflow nicht vorhanden.
		            </p>
		            <p class="MsoNormal">
		                <b style="">2.2.2<a name="PubMan_Nutzerrollen"></a>PubMan Nutzerrollen
		                    
		                  
		                </b>
		            </p>
		            <p class="MsoNormal">
		                Wie bereits
		                oben erw&#228;hnt, sind
		                derzeit in PubMan zwei Nutzerrollen implementiert, der Depositor und
		                der Moderator. Dieses Konzept wird jedoch auf Wunsch von Instituten
		                ausgebaut. Einen Gesamt&#252;berblick &#252;ber die 
		                Nutzerrollen und Workflows finden Sie unter: <a href="http://colab.mpdl.mpg.de/mediawiki/PubMan_Workflows">http://colab.mpdl.mpg.de/mediawiki/PubMan_Workflows</a>
		            </p>
		            <h3><span class="titlemark">3. </span><a name="PubMan Funktionalit&#228;ten"></a>PubMan Funktionalit&#228;ten</h3>
		            <p class="MsoNormal">
		                Eine Vielzahl der Funktionalit&#228;ten in PubMan
		                steht nicht nur eingeloggten Nutzern zur Verf&#252;gung, sondern sind
		                auch f&#252;r nicht eingeloggte Nutzer vorgesehen. Im Folgenden
		                erhalten Sie eine &#220;bersicht der Funktionalit&#228;ten, die auch
		                f&#252;r nicht eingeloggte Nutzer verf&#252;gbar sind.
		            </p>
		            <h4><span class="titlemark">3.1 </span><a name="Suchm&#246;glichkeiten in PubMan"></a>Suchm&#246;glichkeiten in PubMan</h4>
		            <p class="MsoNormal" style="margin-left: 0cm; text-indent: 0cm;">
		                PubMan bietet
		                drei unterschiedliche
		                Sucheinstiegspunkte:
		                <br/>
		            </p>
		            <ul>
		                <li>
		                    Einfache
		                    Suche
		                </li>
		                <li>
		                    Erweiterte
		                    Suche
		                </li>
		                <li>
		                    Organisationssuche
		                </li>
		            </ul>
		            <h4><span class="titlemark">3.1.1 </span><a name="Einfache_Suche"></a>Einfache
		                Suche</h4>
		            <p class="noindent">
		                Bitte geben Sie einen oder mehrere
		                Suchbegriffe ein und
		                klicken Sie den "Los"
		                Knopf rechts neben dem Sucheingabefeld, um 
		                die Suche auszuf&#252;hren. 
		            </p>
		            <p class="indent">
		                Folgende boolschen Operatoren werden in 
		                der Suche unterst&#252;tzt: 
		            </p>
		            <ul>
		                <li>
		                    UND (AND)
		                </li>
		                <li>
		                    ODER (OR)
		                </li>
		                <li>
		                    NICHT (NOT)
		                </li>
		            </ul>
		            <p class="MsoNormal" style="margin-left: 0cm; text-indent: 0cm;">
		                Wenn Sie neben
		                den Metadaten auch die
		                angeh&#228;ngten Volltexte
		                durchsuchen m&#246;chten, dann w&#228;hlen Sie bitte die
		                Checkbox neben "Volltexte
		                einbinden" an. Bitte beachten Sie, dass nur folgende Arten
		                von Volltext
		                durchsuchbar sind:
		                <br/>
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
		            Alle Suchanfragen k&#246;nnen auch mit Wildcards versehen werden.
		            Unterst&#252;tzt wird "?" f&#252;r ein oder kein Zeichen und
		            "*"
		            f&#252;r null bis unendlich viele Zeichen. Bitte beachten Sie, dass
		            Trunkierungen am Anfang des Suchbegriffs nicht unterst&#252;tz
		            werden.<h4><span class="titlemark">3.1.2 </span><a name="AdvancedSearchPage"></a>Erweiterte
		                Suche</h4>
		            <p class="noindent">
		                Sie haben die M&#246;glichkeit,
		                die Suchoptionen (Alle Felder,
		                Personen, Organisationen usw.) entweder einzeln oder in Kombination zu
		                verwenden. Die Suchoptionen k&#246;nnen mit "AND", "OR"
		                oder "NOT"
		                miteinander
		                verkn&#252;pft werden. Voreingestellt ist eine Verkn&#252;pfung
		                mit "AND" 
		                zwischen allen Feldern. Eine &#220;bersicht der verf&#252;gbaren Suchfelder und deren Indexierung finden Sie unter: <a href="http://colab.mpdl.mpg.de/mediawiki/PubMan_Indexing">http://colab.mpdl.mpg.de/mediawiki/PubMan_Indexing</a>
		            </p>
		            <h4><span class="titlemark"><span style="" lang="EN-GB">3.1.2.1.&#160;</span></span><a name="Datum-Suche"></a> Suche nach Daten und Zeitspannen<span style="" lang="EN-GB">
		                    
		                  
		                </span></h4>
		            Um nach einem Datum zu suchen geben Sie dieses bitte im Format
		            YYYY-MM-DD im Suchfeld ein. Wie auch bei der Dateneingabe ist es ebenso
		            m&#246;glich nur nach YYYY oder YYYY-MM zu suchen.
		            <br/>
		            <p>
		                Einige weitere Hinweise:
		            </p>
		            <p>
		                Wenn Sie nach einem bestimmten Datum suchen, geben Sie jenes Datum
		                bitte in beiden Such-Feldern ein: zum Beispiel Startdatum "2009-06-15"
		                bis Enddatum "2009-06-15".
		            </p>
		            <p>
		                Wenn Sie nur im Startdatum eine Eingabe vornehmen, z.B.
		                "2009-06-15", enth&#228;lt das Suchergebnis alle Datens&#228;tze mit 
		                einem Datum ab dem 15. Juni 2009 bis zum heutigen Tag. 
		            </p>
		            <p>
		                Wenn Sie nach einer Zeitspanne suchen, z.B.Startdatum&#160; "2008"
		                bis Enddatum "2009" wird automatisch nach allen Datens&#228;tzen
		                gesucht ab dem 01.01.2008, also dem Beginn des Jahres 2008, bis zum 
		                31.12.2009, also dem Ende des Jahres 2009. 
		            </p>
		            <h4><span class="titlemark">3.3 </span><a name="AffiliationTreePage"></a>Organisationssuche</h4>
		            <p class="noindent">
		                W&#228;hlen Sie hier die
		                Organisation (oder Unterorganisation) aus, &#252;ber die Sie sich
		                informieren m&#246;chten. Sie bekommen dann alle in PubMan
		                eingegebenen Referenzen
		                des Instituts angezeigt. Wenn Sie auf "Beschreibung" klicken,
		                erhalten
		                Sie weitere Informationen zur Organisation.
		            </p>
		            <h4><span class="titlemark">3.2 </span><a name="Suchergebnisse"></a>Suchergebnisse</h4>
		            <p class="noindent">
		                Wenn Sie die Option "Volltexte
		                einbinden" gew&#228;hlt haben,
		                bekommen Sie auch Ergebnisse angezeigt, bei denen der Suchbegriff
		                innerhalb des
		                angeh&#228;ngten Textes gefunden wurde. Sie k&#246;nnen sich
		                die ganze Liste oder einzelne
		                Datens&#228;tze in einem kurzen oder in einem mittleren
		                Anzeigeformat ausgeben
		                lassen. Wenn Sie auf den Titel der Publikation klicken, bekommen Sie
		                die 
		                Vollansicht des Datensatzes angezeigt. 
		            </p>
		            <h4><span class="titlemark">3.2.1 </span><a name="Exportieren"></a>Exportieren</h4>
		            <p class="noindent">
		                Sie
		                k&#246;nnen&#160;Ergebnisse im&#160;Zitierstil,
		                z.B.
		                APA, oder in ein bestimmtes Format, z.B. EndNote Exportformat,
		                exportieren.
		            </p>
		            <p style="font-weight: bold;">
		                <span class="titlemark">3.2.1.1</span>
		                <a name="Export_per_E-Mail_verschicken"></a>Export
		                per E-Mail verschicken
		            </p>
		            <p class="noindent">
		                &#160;Sie k&#246;nnen Daten
		                auch per E-Mail verschicken. Hierf&#252;r geben
		                Sie bitte den
		                Empf&#228;nger der E-Mail an und die E-Mail Adresse, an die der
		                Empf&#228;nger antworten 
		                kann. 
		            </p>
		            <p class="indent">
		                Wenn Sie die Export E-Mail an mehr als
		                eine E-Mail Adresse
		                verschicken m&#246;chten, dann trennen Sie diese bitte mit einem
		                Komma und einem 
		                Leerzeichen voneinander ab. 
		            </p>
		            <h4><span class="titlemark">3.3 </span><a name="Basket"></a>Basket</h4>
		            <p class="noindent">
		                In PubMan haben Sie die M&#246;glichkeit, eine
		                beliebige Anzahl an Datens&#228;tzen von einer Liste auszuw&#228;hlen
		                und sie dann in einen "Basket" zusammenzuf&#252;hren. Hierf&#252;r
		                klicken Sie bitte jeweils die Checkbox neben den gew&#252;nschten
		                Datens&#228;tzen an, und dann auf "Zum Basket hinzuf&#252;gen". Die
		                Anzahl der Datens&#228;tze im Basket ist immer in Klammern neben dem
		                Link zum Basket sichtbar. Bitte beachten Sie, dass jeder Basket nur
		                f&#252;r die jeweilige Sitzung zur Verf&#252;gung steht und nicht
		                gespeichert werden kann. Ein Export aller Datens&#228;tze im Basket ist 
		                m&#246;glich. 
		            </p>
		            <h4><span class="titlemark">3.4 </span><a name="Datensatz-Vollansicht"></a>Datensatz-Vollansicht</h4>
		            In der Datenvollansicht haben Sie die M&#246;glichkeit verschiedene <span style="font-weight: bold;">Social Bookmarking Services</span>
		            zu nutzen. Derzeit bietet Pubman Bookmarking-Optionen f&#252;r <span style="font-weight: bold;">Delicious, Citeulike</span>
		            und <span style="font-weight: bold;">Connotea</span>
		            an. 
		            <br/>
		            Ist f&#252;r den Datensatz ein&#160; <span style="font-weight: bold;">ISI Web of Knowledge Identifier</span>
		            hinterlegt, dient dieser Identifier als Link zu ISI und bietet die
		            M&#246;glichkeit nach dem angezeigten Datensatz bei ISI Web of
		            Knowledge zu suchen, um dort m&#246;glicherweise erg&#228;nzende
		            Informationen zum Artikel zu finden.
		            <br/>
		            Ist hinter dem Autor des angezeigten Datensatzes das Symbol einer Visitenkarte platziert, dient diese als Link zum <span style="font-weight: bold;">Researcher Posrtfolio</span>
		            des Autors. Auf dieser Seite erh&#228;lt der Nutzer erg&#228;nzende
		            Informationen &#252;ber&#160;den Autor. Alle in PubMan gespeicherten
		            Publikationen des Autores sind dort gelistet. Au&#223;erdem bietet das
		            Researcher Portfolio&#160;die M&#246;gllichkeit nach dem angezeigten 
		            Autor in <span style="font-weight: bold;">WorldCat</span>
		            und <span style="font-weight: bold;">Google Scholar</span>
		            zu suchen, um dar&#252;ber m&#246;glicherweise weitere Publikationen der gesuchten Person zu erhalten.
		            <br/>
		            Ist hinter der Organisation, zu der der Autor des angezeigten
		            Datensatzes geh&#246;rt, ein Haus-Symbol platziert, kann dort eine
		            Beschreibung der Organisation aufgerufen werden.<h4><span class="titlemark">3.4.1 </span><a name="Freigabegeschichte_einsehen"></a>Freigabegeschichte
		                einsehen</h4>
		            <p class="noindent">
		                Hier werden alle freigegebenen Versionen eines
		                Datensatzes angezeigt, und Sie k&#246;nnen Ver&#228;nderungen am
		                Metadatensatz nachvollziehen
		            </p>
		            <h4><span class="titlemark">3.4.2 </span><a name="Revisionen_einsehen"></a>Revisionen einsehen</h4>
		            <p class="noindent">
		                Eine Revision ist eine inhaltlich ver&#228;nderte
		                oder neu bearbeitete Version, die mit dem urspr&#252;nglichen Datensatz
		                verlinkt ist. Unter "Revisionen einsehen" werden Ihnen alle Revisionen 
		                eines Datensatzes angezeigt. 
		            </p>
		            <h4><span class="titlemark">3.4.3 </span><a name="Statistik_zum_Datensatz_einsehen"></a>Statistik
		                zum Datensatz einsehen</h4>
		            <p class="noindent">
		                Hier k&#246;nnen Sie einsehen, wie
		                h&#228;ufig der Datensatz aufgerufen
		                wurde und wie h&#228;ufig der Volltext heruntergeladen wurde. Bitte
		                beachten Sie, 
		                dass die Statistiken nur einmal pro Nacht erneuert werden. 
		            </p>
		            <p class="noindent">
		                <b style="">3.4.4<a name="Bearbeitungsgeschichte"></a>Bearbeitungsgeschichte
		                    (nur registrierte Nutzer)
		                    
		                  
		                </b>
		            </p>
		            <p class="noindent">
		                Die Option ist nur f&#252;r
		                registrierte Nutzer sichtbar. Es wird
		                die vollst&#228;ndige Bearbeitungsgeschichte des Datensatzes
		                angezeigt, also alle
		                Aktionen, die vom System aufgezeichnet worden sind.
		            </p>
		            <p class="noindent">
		                <b style="">3.4.5<a name="ViewLocalTagsPage"></a>Lokale Tags (nur f&#252;r registrierte Nutzer)
		                </b>
		            </p>
		            <p class="noindent">
		                Hier k&#246;nnen Sie selbstgew&#228;hlte "Tags"
		                bestimmen, mittels derer Sie die Datens&#228;tze kategorisieren
		                k&#246;nnen und so z.B. f&#252;r bestimmte Zwecke Sets erstellen
		                k&#246;nnen, z.B. "my best publications".
		            </p>
		            <h3><span class="titlemark">4. </span><a name="SubmissionPage"></a>Dateneingabe</h3>
		            <p class="MsoNormal" style="margin-left: 0cm; text-indent: 0cm;">
		                In PubMan gibt
		                es
		                grunds&#228;tzlich vier verschiedene
		                M&#246;glichkeiten, Daten einzugeben.
		                <br/>
		            </p>
		            <ul>
		                <li>
		                    <b style="">Detaillierte
		                        Eingabe:</b>Bei der detaillierten
		                    Eingabe steht Ihnen eine dokumentenspezifische Eingabemaske zur
		                    Verf&#252;gung, die alle relevanten Felder f&#252;r das von Ihnen
		                    gew&#228;hlte Genre enth&#228;lt.
		                </li>
		                <li>
		                    <b style="">Einfache
		                        Eingabe:</b>
		                    Hier k&#246;nnen Sie eine kurze, schrittweise
		                    aufgebaute manuelle
		                    Eingabe vornehmen.
		                </li>
		                <li>
		                    <b style="">Importieren:</b>
		                    Hier haben Sie die M&#246;glichkeit, eine BibTeX
		                    Referenz hochzuladen oder Metadaten inklusive Volltext(e)
		                    (wenn vorhanden) von arXiv, PubMed Central, SPIRES oder BioMed Central
		                    zu importieren.Bitte verwenden Sie
		                    hierf&#252;r die auf den jeweiligen Webseiten angegebenen IDs. (<span style="font-weight: bold;">Ausnahme</span>:
		                    Bitte
		                    beachten Sie, dass bei BioMed Central nur der hintere Teil der DOI -
		                    nach dem Schr&#228;gstrich - als ID anerkannt wird. Beispiel:
		                    doi:10.1186<span style="font-weight: bold;">1471-2253-8-8</span>)
		                </li>
		                <li>
		                    <b style="">Massenimport:</b>
		                    Hier haben Sie die M&#246;glichkeit
		                    einen Massenimport von Web of 
		                    Science Daten, EndNote, BibTeX oder RIS vorzunehmen. <span style="font-style: italic;">Hinweis
		                        zum EndNote-Massenimport: Alle Autoren eines Datensatzes bekommen
		                        standardm&#228;&#223;ig die Organisation "Max Planck Society"
		                        zugewiesen. Gew&#252;nschte &#196;nderungen/Verfeinerungen in der
		                        Angabe der Organisation m&#252;ssen h&#228;ndisch nach dem Import im
		                        jeweiligen Datensatz nachbearbeitet werden.</span>
		                </li>
		            </ul>
		            <ul>
		            </ul>
		            <h4><span class="titlemark">4.1. </span><a name="neuen_Datensatz_anlegen"></a>Einen
		                neuen Datensatz anlegen</h4>
		            <p class="noindent">
		                Bevor Sie einen neuen Datensatz
		                anlegen k&#246;nnen, m&#252;ssen Sie zun&#228;chst die Eingabemethode bestimmen und dann die
		                Collection ausw&#228;hlen, in der Sie den Eintrag vornehmen
		                m&#246;chten. Diese w&#228;hlen Sie aus, indem Sie auf den 
		                Namen der Collection klicken. 
		            </p>
		            <h4><span class="titlemark">4.2. </span><a name="Datensatz_bearbeiten"></a>Anlegen
		                und
		                Bearbeiten eines Datensatzes</h4>
		            <p class="noindent">
		                Bevor Sie Ihre Publikationsdaten
		                eingeben,
		                w&#228;hlen Sie bitte
		                den Dokumenttyp (Genre) ihres Datensatzes aus. Sie bekommen dann, dem
		                Dokumenttyp entsprechend, eine Eingabemaske angezeigt. Bitte beachten
		                Sie, dass Sie den Dokumenttypen nur so lange &#228;ndern
		                k&#246;nnen,
		                bis Sie den Datensatz einmal speichern oder einstellen, beziehungsweise
		                freigeben, da PubMan dann die Felder, die f&#252;r das
		                gew&#228;hlte
		                Genre nicht mehr ben&#246;tigt werden, herausl&#246;scht.
		            </p>
		            <p class="noindent">
		                Bitte
		                f&#252;llen Sie alle Felder aus, die mit einem Stern
		                markiert
		                sind, da diese Mindestangaben ben&#246;tigt werden, um einen
		                Datensatz in PubMan anzulegen.
		                Sollte eins der Felder nicht
		                gef&#252;llt 
		                sein, so bekommen Sie eine Validierungsmeldung. Siehe auch <a href="#Validierung">Validierung</a>.
		            </p>
		            <h4><span class="titlemark">4.2.1 </span><a name="Personen_und_Organisationen"></a>Personen
		                und Organisationen eingeben</h4>
		            <p class="noindent">
		                Sie haben zwei M&#246;glichkeiten,
		                Personen
		                einzugeben. Entweder alle einzeln, hierf&#252;r verwenden Sie die
		                Eingabemaske f&#252;r Personen und klicken auf das Plussymbol, wenn
		                Sie
		                weitere Personen hinzuf&#252;gen m&#246;chten. Wenn der Name, den Sie
		                eingeben, vom System erkannt wird, bekommen Sie eine Vorschlagsliste;
		                Sie k&#246;nnen dann einen Namen ausw&#228;hlen oder die Liste mit ESC
		                schlie&#223;en. Um mehrere Personen gleichzeitig einzugeben, klicken
		                Sie bitte auf &#34;viele hinzuf&#252;gen&#34;. Dadurch &#246;ffnet
		                sich dann ein Textfeld,in
		                das Sie eine Liste von Personen entweder eingeben oder hineinkopieren
		                k&#246;nnen. Diese Liste wird dann vom System in die einzelnen
		                Felder
		                f&#252;r die Eingabe von Personen eingef&#252;gt. Bitte
		                beachten Sie, dass mindestens eine Person eine Affiliation tragen muss,
		                da ansonsten der Eintrag nicht erstellt werden kann.
		            </p>
		            <span style="font-weight: bold;"> 4.22 <a name="Zeitschriftennamen_eingeben"></a>Zeitschriftennamen
		                eingeben</span>
		            <br/>
		            <br/>
		            Wenn Sie als Quelle einen Zeitschriftennamen eingeben m&#246;chten,
		            dann stellen Sie bitte zun&#228;chst als Genre der Quelle
		            "Zeitschrift"
		            ein. Wenn Sie nun anfangen, den Zeitschriftennamen einzutippen, werden
		            Ihnen von PubMan Zeitschriftennamen vorgeschlagen. Sie k&#246;nnen
		            entweder einen der vorgeschlagenen &#252;bernehmen, indem Sie Ihn
		            ausw&#228;hlen und mit der Maus drauf klicken oder "Enter"
		            dr&#252;cken,
		            oder Sie k&#246;nnen einen neuen Zeitschriftennamen angeben. Die
		            Vorschlagsliste schlie&#223;en Sie mit ESC, wenn Sie keinen der
		            vorgeschlagenen Namen &#252;bernehmen m&#246;chten.
		            <br/>
		            <br/>
		            <span style="font-weight: bold;"> 4.2.3 <a name="Sprache_der_Publikation_eingeben"></a>Sprache
		                der Publikation angeben</span>
		            <br/>
		            <br/>
		            Auch bei der Sprache der Publikation wird mit Vorschlagslisten
		            gearbeitet. Sie k&#246;nnen hier sogar die Sprache entweder in
		            Deutsch
		            oder in Englisch eingeben. Sie wird in beiden F&#228;llen erkannt.
		            <br/>
		            <br/>
		            <span style="font-weight: bold;">4.2.4<a name="Ein_Datum_eingeben"></a>Ein Datum eingeben
		                <br/>
		                <br/>
		            </span>
		            Das Datum sollte folgendes Format haben: JJJJ-MM-TT. Sie
		            k&#246;nnen jedoch auch Begriffe wie "gestern", "letztes Jahr" oder
		            &#228;hnliches angeben und PubMan wandelt diese Angabe dann in
		            das
		            richtige Format um.
		            <br/>
		            <span style="font-style: italic;">Hinweis zur Angabe eines Datums
		                f&#252;r die Publikationstypen "Serie" und "Zeitschrift": Das
		                anzugebende Datum entspricht in diesen beiden F&#228;llen dem Beginn
		                der jeweiligen Ver&#246;ffentlichung.</span>
		            <br/>
		            <br/>
		            <span style="font-weight: bold;">4.2.5<a name="Rechte"></a>Rechte-Informationen f&#252;r eine Datei hinterlegen
		                <br/>
		                <span style="font-weight: bold;"><span style="font-weight: bold;"></span></span>
		                <br/>
		            </span>
		            Sie haben die M&#246;glichkeit die dem Datensatz angef&#252;gten
		            Dateien und externen Referenzen mit verschiedenen Rechte-Informationen
		            zu versehen. Sie k&#246;nnen Zugriffsrechte ("&#246;ffentlich",
		            "privat", "eingeschr&#228;nkt") festlegen bzw. diese f&#252;r
		            definierte Nutzergruppen vergeben. Sie k&#246;nnen auch ein "Copyright
		            Statement" (als Freitext)
		            abgeben sowie ein "Copyright Date" (im Datums-Format) zur
		            Verf&#252;gung stellen. Au&#223;erdem haben Sie die M&#246;glichkeit
		            den gespeicherten Datensatz mit einer Creative Commons Lizenz (<a href="http://creativecommons.org/">http://creativecommons.org/</a>) zu versehen. Alle diese Angaben sind optional. Bitte beachten Sie, die 
		            Richtigkeit dieser Rechte-Angaben im Vorfeld zu &#252;berpr&#252;fen!<span style="font-weight: bold;">
		                <br/>
		            </span>
		            <h4><span class="titlemark">4.3 </span><a name="Revision_anlegen"></a>Eine
		                neue Revision anlegen</h4>
		            <p class="noindent">
		                Eine neue Revision ist eine
		                intellektuell &#252;berarbeitete Version
		                eines Werkes (z.B. erst Pre Print, dann Post Print). Bitte beachten
		                Sie, dass jede neue Revision ein neuer,
		                separater Datensatz ist, der mit dem urspr&#252;nglichen Datensatz
		                verkn&#252;pft ist 
		                ("ist Revision von"). 
		            </p>
		            <p class="noindent">
		                Bevor Sie die Revision (den neuen
		                Datensatz) freigeben, haben
		                Sie die M&#246;glichkeit, einen Kommentar hierzu abzugeben.
		            </p>
		            <h4><span class="titlemark">4.4 </span><a name="Validierung"></a>Validierung</h4>
		            <p class="noindent">
		                Sie k&#246;nnen Ihren Datensatz
		                validieren, wenn
		                Sie &#252;berpr&#252;fen
		                wollen, ob er den Kriterien der Collection entspricht. Die
		                Auswahlkriterien werden pro Institut definiert und sollen die 
		                Qualit&#228;tssicherung erleichtern. 
		            </p>
		            <p class="indent">
		                Bitte beachten Sie, dass Sie einen
		                Datensatz nur in PubMan
		                einstellen k&#246;nnen, wenn er den Kriterien der Collection 
		                entspricht. 
		            </p>
		            <h4><span class="titlemark">4.5 </span><a name="Speichern"></a>Speichern
		                des Datensatzes</h4>
		            <p class="noindent">
		                Wenn Sie den "speichern"
		                Knopf
		                bet&#228;tigen, wird ihr Datensatz
		                in den Status "pending"
		                gesetzt und kann nur von
		                Ihnen
		                eingesehen und
		                bearbeitet werden. Dies kann z.B. n&#252;tzlich sein, wenn Sie eine
		                Aufnahme erst zu
		                einem sp&#228;teren Zeitpunkt fertig stellen m&#246;chten. Sie
		                k&#246;nnen Ihren Datensatz dann wieder &#252;ber Ihren
		                Arbeitsplatz 
		                aufrufen und editieren. 
		            </p>
		            <h4><span class="titlemark">4.6 </span><a name="L&#246;schen_des_Datensatzes"></a>L&#246;schen
		                des Datensatzes</h4>
		            <p class="noindent">
		                Bitte beachten Sie, dass Sie nur
		                Datens&#228;tze im
		                Status "pending"
		                l&#246;schen k&#246;nnen. Bereits freigegebene
		                Datens&#228;tze
		                k&#246;nnen lediglich zur&#252;ckgezogen werden, da Sie bereits 
		                zitierf&#228;hig mit einer PID ausgezeichnet sind. 
		            </p>
		            <h4><span class="titlemark">4.7 </span><a name="Einstellen_eines_Datensatzes"></a>Einstellen
		                eines Datensatzes</h4>
		            <p class="noindent">
		                Der Depositor kann seine
		                Datens&#228;tze im Standard Workflow
		                einstellen.
		            </p>
		            <p class="noindent">
		                Wenn Sie den "einstellen"
		                Knopf bet&#228;tigen, wird Ihr
		                Datensatz
		                zun&#228;chst validiert. Wenn Ihr Datensatz valide ist, werden Sie
		                auf eine Maske
		                navigiert, auf der Sie diese Aktion best&#228;tigen und
		                kommentieren k&#246;nnen. Der
		                Datensatz wird nun an den Moderator Ihrer Collection weitergeleitet,
		                der die
		                Daten &#252;berpr&#252;ft und den Datensatz entweder
		                f&#252;r die &#246;ffentliche Sicht freigibt oder
		                ihn an Sie zur weiteren Bearbeitung zur&#252;ckschickt.
		                Datens&#228;tze, die an Sie
		                zur&#252;ckgeschickt wurden, finden Sie in Ihrem Arbeitsplatz unter
		                dem Filter "in 
		                &#220;berarbeitung". 
		            </p>
		            <h4><span class="titlemark">4.7.1 </span><a name="Kommentar_Einstellen"></a>Kommentar
		                zum Einstellen</h4>
		            <p class="noindent">
		                In diesem Kommentarfeld
		                k&#246;nnen Sie dem Nutzer, der den
		                Datensatz nach Ihnen im Workflow bearbeitet, einen Kommentar
		                hinterlassen, den 
		                er dann einsehen kann. 
		            </p>
		            <h4><span>4.8 <a name="Freigeben"></a>Freigeben
		                    eines
		                    Datensatzes</span></h4>
		            <p class="noindent">
		                Im
		                Simple Workflow
		                k&#246;nnen Sie Ihre Datens&#228;tze direkt f&#252;r die
		                &#246;ffentliche Sicht freigeben (sie 
		                m&#252;ssen nicht vorher eingestellt werden). 
		            </p>
		            <h4><span class="titlemark">4.8.1 </span><a name="Kommentar_zum_Freigeben"></a>Kommentar zum
		                Freigeben</h4>
		            <p class="noindent">
		                In diesem Kommentarfeld
		                k&#246;nnen Sie dem Nutzer, der den
		                Datensatz nach Ihnen im Workflow bearbeitet, einen Kommentar
		                hinterlassen, den 
		                er dann einsehen kann. 
		            </p>
		            <h3><span class="titlemark">5. </span><a name="Qualit&#228;tssicherung in PubMan"></a>Qualit&#228;tssicherung in PubMan</h3>
		            <p class="noindent">
		                Um eine hohe Qualit&#228;t der in PubMan
		                enthaltenen Daten zu garantieren, ist ein
		                Qualit&#228;tssicherungsworkflow implementiert worden, so dass alle 
		                Datens&#228;tze bestimmte Qualit&#228;tskriterien erf&#252;llen. 
		            </p>
		            <p class="noindent">
		                F&#252;r die Qualit&#228;tskontrolle ist der
		                Moderator zust&#228;ndig. Hierf&#252;r stehen ihm verschiedene
		                M&#246;glichkeiten zur Verf&#252;gung. Er kann die Datens&#228;tze
		                modifizieren oder dem Depositor zur weiteren Bearbeitung
		                zur&#252;ckschicken; wenn die Qualit&#228;t stimmt, kann er die
		                Datens&#228;tze akzeptieren und f&#252;r die &#246;ffentliche Sicht 
		                freischalten. 
		            </p>
		            <p class="noindent">
		                Eine weitere Option der Qualit&#228;tssicherung,
		                die ausschlie&#223;lich dem Depositor vorbehalten ist, ist das
		                Zur&#252;ckziehen eines Datensatzes: eine Alternative zum L&#246;schen,
		                die dem Zweck der Langzeitarchivierung dient und die Zitierbarkeit der
		                Datens&#228;tze weiterhin garantiert.
		            </p>
		            <h4 style="font-weight: normal;"><span><span style="font-weight: bold;">5.1 </span><a name="&#220;berarbeitung"></a><span style="font-weight: bold;">Einen Datensatz zur &#220;berarbeitung an den Depositor zur&#252;cksenden</span></span></h4>
		            <p class="noindent">
		                Im Standard Workflow kann der
		                Moderator Datens&#228;tze, die den
		                Qualit&#228;tsstandards nicht entsprechen, dem Depositor zum
		                Bearbeiten
		                zur&#252;ckschicken. Datens&#228;tze im Status &#34;In
		                &#220;berarbeitung&#34; sind sowohl f&#252;r den
		                Depositor als auch f&#252;r den Moderator sichtbar, k&#246;nnen
		                jedoch nur noch vom 
		                Depositor bearbeitet werden, bis sie erneut eingestellt werden. <span style="" lang="EN-GB">
		                </span>
		            </p>
		            <h4><span class="titlemark">5.2 </span><a name="Einen Datensatz modifizieren"></a>Einen Datensatz modifizieren</h4>
		            <p class="noindent">
		                Datens&#228;tze im Status "pending" k&#246;nnen vom
		                Ersteller des Datensatzes noch so lange bearbeitet werden, bis er
		                zufrieden ist und den Datensatz in PubMan einstellt. Bereits
		                "eingestellte" bzw. "freigegebene" (je nach Workflow-Typ)
		                Datens&#228;tze k&#246;nnen vom Moderator ver&#228;ndert werden, 
		                "freigegebene" Datens&#228;tze auch vom Ersteller. 
		            </p>
		            <h4><span class="titlemark">5.3 </span><a name="Akzeptieren"></a>Akzeptieren</h4>
		            <p class="noindent">
		                Wenn Sie den Datensatz, den Sie
		                ver&#228;ndert haben, f&#252;r die
		                &#246;ffentliche Sicht wieder freischalten m&#246;chten, dann
		                m&#252;ssen Sie ihn akzeptieren.<span style="" lang="EN-GB">
		                    
		                  
		                </span>
		            </p>
		            <h4><span class="titlemark">5.3.1 </span><a name="Kommentar_Akzeptieren"></a>Kommentar
		                zum Akzeptieren</h4>
		            <p class="noindent">
		                Sie haben hier die
		                M&#246;glichkeit einen Kommentar
		                zu Ihrer &#220;berarbeitung abzugeben, der dann &#246;ffentlich 
		                sichtbar ist &#252;ber die Freigabegeschichte. 
		            </p>
		            <h4><span class="titlemark">5.4 </span><a name="Datensatz_zur&#252;ckziehen"></a>Einen
		                Datensatz zur&#252;ckziehen</h4>
		            <p class="noindent">
		                Freigegebene Datens&#228;tze
		                k&#246;nnen nicht mehr
		                gel&#246;scht, sondern nur
		                noch zur&#252;ckgezogen werden; die Metadaten bleiben dabei
		                sichtbar,
		                der Volltext jedoch nicht,
		                sodass die Datens&#228;tze weiterhin zitierbar sind.
		                Zur&#252;ckgezogene Datens&#228;tze sind nicht mehr suchbar,
		                sondern
		                k&#246;nnen nur angezeigt werden, wenn die ID des Datensatzes
		                eingegeben wird.
		            </p>
		            <p class="noindent">
		                Datens&#228;tze k&#246;nnen
		                nur vom Ersteller (Depositor) zur&#252;ckgezogen werden. Bitte
		                geben Sie den Grund an, weswegen Sie das Item zur&#252;ckziehen 
		                m&#246;chten. 
		            </p>
		            <h3><span class="titlemark">6. </span><a name="Hilfsmittel zur Datenverwaltung"></a>Hilfsmittel zur Datenverwaltung</h3>
		            <p class="noindent">
		                Als Depositor haben Sie &#252;ber "Meine
		                Datens&#228;tze" verschiedene M&#246;glichkeiten, ihre Datens&#228;tze
		                zu verwalten. Dort erhalten Sie eine &#220;bersicht Ihrer
		                Datens&#228;tze und der verf&#252;gbaren Verwaltungsm&#246;glichkeiten.
		                Sie k&#246;nnen, z.B. die Ansicht &#228;ndern, die Datens&#228;tze nach
		                ihrem Status filtern oder nach verschiedenen Kriterien sortieren,
		                ausgew&#228;hlte Datens&#228;tze exportieren oder einen Basket
		                erstellen.
		            </p>
		            <h4><span class="titlemark">6.1 </span><a name="Meine_Datens&#228;tze"></a>Meine Datens&#228;tze</h4>
		            <p class="noindent">
		                Unter &#34;Meine
		                Datens&#228;tze&#34; werden alle Eintr&#228;ge
		                aufgelistet,
		                die der Depositor angelegt hat. Es besteht die M&#246;glichkeit,
		                sie nach Status zu
		                filtern. So kann der Depositor z.B. Daten, die noch nicht fertig
		                gestellt sind,
		                erneut &#252;berarbeiten oder l&#246;schen oder er kann Daten,
		                die vom Moderator zur
		                Bearbeitung zur&#252;ckgeschickt wurden, anreichern. Ein weiterer
		                Filter zeigt Ihnen importierte Datens&#228;tze nach datum sortiert an.
		            </p>
		            <h4><span class="titlemark">6.1.1 </span><a name="Status_eines_Datensatzes"></a>Status
		                eines Datensatzes</h4>
		            <span>Bitte
		                klicken Sie auf "Filter" und
		                &#246;ffnen&#160;die
		                pull-down Liste "Status", wenn Sie Datens&#228;tze in
		                einem anderen Status angezeigt bekommen m&#246;chten. Folgende
		                Statusangaben sind m&#246;glich:</span>
		            <br style="background-color: rgb(255, 255, 255);"/>
		            <ul>
		                <li>
		                    "pending": Der
		                    Datensatz wurde von Ihnen eingegeben und bisher nur abgespeichert, da
		                    Sie noch nicht mit der Eingabe fertig waren. Dieser Datensatz muss noch eingestellt oder
		                    freigeschaltet werden, sobald die Bearbeitung abgeschlossen ist.
		                </li>
		                <li>
		                    "eingestellt":
		                    Dieser Status kommt nur im Standard Workflow vor. Dies sind
		                    Datens&#228;tze, die Sie fertig eingegeben haben und die derzeit
		                    beim
		                    Moderator Ihrer Collection zur Kontrolle vorliegen.
		                </li>
		                <li>
		                    "freigegeben":
		                    Datens&#228;tze im Status "freigegeben" sind &#246;ffentlich
		                    sichtbar.
		                    &#196;nderungen k&#246;nnen von Ihnen und dem Moderator vorgenommen werden.
		                </li>
		                <li>
		                    "zur&#252;ckgezogen":
		                    Datens&#228;tze
		                    im Status "zur&#252;ckgezogen" sind nicht mehr &#252;ber die
		                    Suche oder
		                    &#252;ber die Organisationen einsehbar. Sie k&#246;nnen
		                    lediglich von
		                    Ihnen als Ersteller eingesehen werden oder wenn die genaue URL zu dem
		                    Datensatz eingegeben wird.
		                </li>
		                <li>
		                    "in &#220;berarbeitung": dieser
		                    Status kommt nur im Standard Workflow vor. Dies sind
		                    Datens&#228;tze,
		                    die der Moderator zur &#220;berarbeitung an Sie
		                    zur&#252;ckgeschickt
		                    hat. Achtung, vergessen Sie bitte nicht die Datens&#228;tze wieder 
		                    einzustellen, wenn Sie mit der &#220;berarbeitung fertig sind! 
		                </li>
		            </ul>
		            <h4><span class="titlemark">6.1.2 </span><a name="Datens&#228;tze_Sortieren"></a>Datens&#228;tze
		                Sortieren</h4>
		            <p class="noindent">
		                Hier k&#246;nnen Sie das
		                Sortierkriterium Ihrer Liste &#228;ndern.
		                Dar&#252;ber hinaus k&#246;nnen Sie angeben, ob die Liste
		                aufsteigend oder absteigend 
		                sortiert werden soll. 
		            </p>
		            <h4><span class="titlemark">6.2 </span><a name="Qualit&#228;tssicherung"></a>Qualit&#228;tssicherungs
		                Bereich</h4>
		            <p class="noindent">
		                Im Qualit&#228;tssicherungs
		                Bereich werden alle Daten
		                angezeigt, die f&#252;r den Moderator relevant sind.
		                Datens&#228;tze in Status
		                &#34;eingestellt&#34; m&#252;ssen noch
		                &#252;berpr&#252;ft werden. Der Moderator kann die
		                Datens&#228;tze
		                dann entweder akzeptieren (f&#252;r die &#246;ffentliche Sicht
		                freischalten) oder an den
		                Depositor zur &#220;berarbeitung zur&#252;ckschicken
		                ("zum
		                Bearbeiten schicken").
		            </p>
		            <p class="noindent">
		                Bitte beachten Sie, dass das oben
		                Genannte nur
		                f&#252;r den Standard Workflow gilt. Im Simple Workflow gibt es
		                keine
		                Datens&#228;tze im Status "eingestellt".
		            </p>
		            <h4><span class="titlemark"><span style="" lang="EN-GB">6.2.1</span></span><a name="Status_eines_Datensatzes_QA"></a>Status
		                eines Datensatzes<span style="" lang="EN-GB">
		                    
		                  
		                </span></h4>
		            <p class="noindent">
		                Bitte klicken Sie auf "Filter"
		                und &#246;ffnen&#160;die
		                pull-down Liste, wenn Sie Datens&#228;tze in
		                einem anderen Status angezeigt bekommen m&#246;chten. Folgende
		                Statusangaben sind m&#246;glich:
		            </p>
		            <ul>
		                <li>
		                    "eingestellt":
		                    Dieser
		                    Status kommt nur im Standard Workflow vor. Dies sind
		                    Datens&#228;tze,
		                    die von Ihnen noch &#252;berpr&#252;ft werden m&#252;ssen.
		                    Sie
		                    k&#246;nnen dann entweder den Datensatz selbst editieren und
		                    f&#252;r
		                    die &#246;ffentliche Sicht freischalten oder an den Depositor zur
		                    &#220;berarbeitung zur&#252;ckschicken.&#160;
		                </li>
		                <li>
		                    "freigegeben":
		                    Datens&#228;tze im
		                    Status "freigegeben" sind
		                    &#246;ffentlich sichtbar und k&#246;nnen
		                    von Ihnen bei Bedarf noch ver&#228;ndert werden.
		                </li>
		                <li>
		                    "in
		                    &#220;berarbeitung": Dieser
		                    Status kommt nur im Standard Workflow vor. Dies sind
		                    Datens&#228;tze,
		                    die Sie an den Depositor zur &#220;berarbeitung
		                    zur&#252;ckgeschickt
		                    haben.
		                </li>
		            </ul>
		            <h4><span class="titlemark">6.2.2 </span><a name="Datens&#228;tze_sortieren_QA"></a>Datens&#228;tze
		                Sortieren</h4>
		            <p class="MsoNormal">
		                Hier k&#246;nnen Sie das
		                Sortierkriterium Ihrer Liste &#228;ndern.
		                Dar&#252;ber hinaus k&#246;nnen Sie angeben, ob die Liste
		                aufsteigend oder absteigend
		                sortiert werden soll.
		            </p>
		            <h4><span class="titlemark">6.3 </span><a name="Import Bereich"></a>Import Bereich</h4>
		            <p class="noindent">
		                Im Import Bereich k&#246;nnen
		                Sie sich den Status Ihrer Importe nachverfolgen und
		                Stapelverarbeitungen vornehmen, wie z.B. alle Datens&#228;tze, die
		                Sie importiert haben auf einmal freischalten. Mit dem
		                &#34;Entfernen&#34;-Button k&#246;nnen Sie den Import l&#246;schen
		                &#8211; ohne gleichzeitig die importierten Datensauml;tze zu
		                l&#246;schen. Mit dem &#34;L&#246;schen"-Button werden die
		                Datens&#228;tze gel&#246;scht, sofern sie sich noch im Status "pending"
		                befinden.
		            </p>
		        </div>
		    </body>
		</html>
	</f:view>
</jsp:root>