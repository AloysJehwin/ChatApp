<%@ page import="java.sql.*" %>
<%@ page session="true" %>
<%
    String status = (String) request.getAttribute("status");
    String name = (String) session.getAttribute("name");

    if (name == null) {
        response.sendRedirect("login.jsp");
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Dashboard</title>
</head>
<body>
    <h1>Welcome, <%= name %>!</h1>
    <h2>Available Users</h2>
    <ul>
        <%
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/ChatApp", "root", "Ausnet@1975");

                String query = "SELECT * FROM users WHERE username != ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, name);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    int id = rs.getInt("id");
                    String username = rs.getString("username");
        %>
                    <li>
                        <a href="chat.jsp?receiverId=<%= id %>"><%= username %></a>
                    </li>
        <%
                }
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        %>
    </ul>
    <% if (status != null && status.equals("failed")) { %>
        <p style="color:red;">Invalid username or password. Please try again.</p>
    <% } %>
</body>
</html>
