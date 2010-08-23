<%@page import="java.sql.SQLException"%>
<%@page import="java.sql.ResultSetMetaData"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="javax.sql.DataSource"%>
<%@page import="javax.naming.InitialContext"%>
<%@page import="javax.naming.Context"%>
<%@page import="java.sql.Statement"%>
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
* Copyright 2006-2010 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/
%>
<%
	//response.sendRedirect("services");
%>
<html>
	<head>
		<title>eSciDoc Validation Service</title>
	</head>
	<body bgcolor="white">
		<h1>
			eSciDoc Validation Service
		</h1>
		<p>
			This is a makeshift to administer the validation cache database until a proper frontend is available.
		</p>
		<form method="post" action="db.jsp">
			<textarea rows="10" cols="100" name="sql"><%= (request.getParameter("sql") == null ? "" : request.getParameter("sql")) %></textarea>
			<br/>
			<input type="submit"/>
		</form>
		<%
			if (request.getParameter("sql") != null)
			{ %>
			    Result:
			    <br/>
				<% Context ctx = new InitialContext();
	            DataSource dataSource = (DataSource) ctx.lookup("Validation");
	            Statement pstmt = dataSource.getConnection().createStatement();
	            try
	            {
		            ResultSet rs = pstmt.executeQuery(request.getParameter("sql"));
		            ResultSetMetaData resultSetMetaData = rs.getMetaData();
	            
		            %><table cellspacing="1" bgcolor="#EEEEEE"><thead><%
					for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++)
					{
					    %><th><%= resultSetMetaData.getColumnName(i) %></th><%
					}
		            %></thead><%
		            while (rs.next())
		            {
		                %><tr><%
		                for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++)
						{
						    %><td valign="top"><%= (rs.getObject(i) == null ? "---" : rs.getObject(i).toString().replace("<", "&lt;").replace("\n", "<br/>")) %></td><%
						}
		                %></tr><%
		            }
		            %></table><%
	            }
	            catch (SQLException sqle)
	            {
	                if ("General errorresult set is closed".equals(sqle.getMessage()))
	                {
	                    out.println("The query returned no result.");
	                }
	                else
	                {
	                    out.println("Error: " + sqle.getMessage());
	                }
	            }
	            finally
	            {
	            	dataSource.getConnection().close();
	            }
			}
		%>
	</body>
</html>