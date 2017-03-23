<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:f="http://xmlns.jcp.org/jsf/core" xmlns:h="http://xmlns.jcp.org/jsf/html" xmlns:ui="http://xmlns.jcp.org/jsf/facelets" xmlns:p="http://primefaces.org/ui" xmlns:pt="http://xmlns.jcp.org/jsf/passthrough">
<h:head>
    <title>
        <h:outputText value="#{ApplicationBean.appTitle}" />
    </title>
    <ui:include src="header/ui/StandardImports.jspf" />
    <style>
        .mainMenu a:hover,
        .mainMenu a:focus {
            background-image: url("../images/BG_MainNavi_hover_bold.png");
        }
    </style>
</h:head>

<body lang="${InternationalizationHelper.locale}">
    <f:view locale="#{InternationalizationHelper.userLocale}">
        <f:loadBundle var="lbl" basename="Label" />
        <f:loadBundle var="msg" basename="Messages" />
        <f:loadBundle var="tip" basename="Tooltip" />
        <h:form id="form1">
            <div class="full wrapper">
                <h:inputHidden id="offset"></h:inputHidden>
                <div class="metaMenu">
                    <h:panelGroup styleClass="seperator"></h:panelGroup>
                </div>
                <div class="LogoNSearch">
                    <h:outputLink id="lnkStartPage" title="#{tip.navigation_lblStartpage}" value="#{ApplicationBean.appContext}HomePage.jsp">
                        <span class="tiny_marginLExcl quad_area0 headerLogo"> <h:panelGroup
								styleClass="quad_area0 themePark #{Header.serverLogo}"></h:panelGroup>
						</span>
                    </h:outputLink>
                </div>
                <div id="mainMenuSkipLinkAnchor" class="full_area0 mainMenu">
                    <h:outputLink id="lnkHome" styleClass="free_area0" title="#{tip.mainMenu_lnkHome}" value="#{ApplicationBean.appContext}HomePage.jsp">
                        <h:outputText value="#{lbl.mainMenu_lnkHome}" />
                    </h:outputLink>
                </div>
                <div id="content" class="full_area0 clear" style="margin-top: 4em;">
                    <span style="font-size: 140%; line-height: 140%">
						<p style="margin-left: 4em; margin-right: 4em;">
							This publication has been assigned a persistent identifier (PID),
							but the identifier has not yet been registered in the worldwide
							Handle system. This may take some time. If you think the
							registration is overdue, please refer to the
							<h:outputLink value="mailto:pubman-support@gwdg.de"
								title="pubman-support@gwdg.de">PubMan support team</h:outputLink>
							.
						</p>
						<p
							style="margin-left: 4em; margin-right: 4em; padding-top: 1em; border-top-color: #B0B1B1; border-top-style: solid; border-top-width: 0.09em">
							Dieser Publikation wurde ein persistenter Identifikator (PID)
							zugewiesen, der Identifikator wurde aber noch nicht im weltweiten
							Handle-System registriert. Dies kann einige Zeit dauern. Falls
							Sie der Meinung sind, die Registrierung sei überfällig, wenden
							Sie sich bitte an das
							<h:outputLink value="mailto:pubman-support@gwdg.de"
								title="pubman-support@gwdg.de">PubMan-Support-Team</h:outputLink>
							.
						</p>
					</span>
                </div>
            </div>
            <ui:include src="footer/Footer.jspf" />
        </h:form>
        <script type="text/javascript">
            $("input[id$='offset']").submit(function() {
                $(this).val($(window).scrollTop());
            });
            $(document).ready(function() {
                $(window).scrollTop($("input[id$='offset']").val());
                $(window).scroll(function() {
                    $("input[id$='offset']").val($(window).scrollTop())
                });
            });
        </script>
    </f:view>
</body>

</html>