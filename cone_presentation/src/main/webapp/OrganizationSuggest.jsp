<%@ page import="de.mpg.mpdl.inge.cone.web.util.beans.OrganizationSuggest"%>
<%@ page import="de.mpg.mpdl.inge.cone.web.util.beans.OrganizationResult"%>
<%@ page import="de.mpg.mpdl.inge.service.pubman.OrganizationService"%>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@ page import="java.util.List"%>
<%
    String query = request.getParameter("q");
    OrganizationService organizationService = (OrganizationService) WebApplicationContextUtils.getRequiredWebApplicationContext(application).getBean("organizationServiceDbImpl");
    OrganizationSuggest suggest = new OrganizationSuggest(query, organizationService);
    List<OrganizationResult> results = suggest.getResults();
%>
[
<% for (int i = 0; i < results.size(); i++) {
    OrganizationResult ou = results.get(i);
%>
    {
        "id" : "<%= ou.getIdentifier() %>",
        "value" : "<%= ou.getName() %>",
        "address" : "<%= ou.getAddress() %>"
    }<%= (i < results.size() - 1) ? "," : "" %>
<% } %>
]
