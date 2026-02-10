<%@ page import="de.mpg.mpdl.inge.cone.web.util.beans.IdentifierSuggest"%>
<%@ page import="java.util.List"%>
<%
    String query = request.getParameter("q");
    IdentifierSuggest suggest = new IdentifierSuggest(query);
    List<String> results = suggest.getResults();
%>
[
<% for (int i = 0; i < results.size(); i++) { %>
    {
        "value" : "<%= results.get(i) %>"
    }<%= (i < results.size() - 1) ? "," : "" %>
<% } %>
]
