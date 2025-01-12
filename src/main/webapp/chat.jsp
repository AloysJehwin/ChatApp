<%@ page import="java.sql.*" %>
<%@ page session="true" %>
<%
    // Retrieve sender details from the session
    Integer senderId = (Integer) session.getAttribute("userId");
    String senderName = (String) session.getAttribute("name");

    // Safely retrieve and parse receiverId parameter
    int receiverId = 0; // Default to 0 if not provided
    String receiverIdParam = request.getParameter("receiverId");

    if (receiverIdParam != null && !receiverIdParam.isEmpty()) {
        try {
            receiverId = Integer.parseInt(receiverIdParam);
        } catch (NumberFormatException e) {
            out.println("<p>Error: Invalid receiver ID provided.</p>");
        }
    } else {
        out.println("<p>Error: No receiver ID provided.</p>");
    }

    // Check if there's an error message in the URL
    String errorMessage = request.getParameter("error");
    if (errorMessage != null) {
        out.println("<p style='color:red;'>" + errorMessage + "</p>");
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Chat</title>
</head>
<body>
    <% if (receiverId > 0) { %>
        <h1>Chat with User <%= receiverId %></h1>
        <div>
            <%
                // Retrieve and display chat messages
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/chatapp", "root", "Ausnet@1975")) {
                    Class.forName("com.mysql.cj.jdbc.Driver");

                    String query = "SELECT * FROM messages WHERE (sender_id = ? AND receiver_id = ?) " +
                                   "OR (sender_id = ? AND receiver_id = ?) ORDER BY timestamp";
                    try (PreparedStatement stmt = conn.prepareStatement(query)) {
                        stmt.setInt(1, senderId);
                        stmt.setInt(2, receiverId);
                        stmt.setInt(3, receiverId);
                        stmt.setInt(4, senderId);

                        try (ResultSet rs = stmt.executeQuery()) {
                            boolean hasMessages = false;

                            while (rs.next()) {
                                hasMessages = true;
                                String message = rs.getString("message");
                                int sender = rs.getInt("sender_id");
            %>
                                <p><strong><%= (sender == senderId ? "You" : "User " + sender) %>:</strong> <%= message %></p>
            <%
                            }

                            if (!hasMessages) {
            %>
                                <p>No messages found. Start the conversation!</p>
            <%
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    out.println("<p>Error loading messages. Please try again later.</p>");
                }
            %>
        </div>
        <form action="ChatServlet" method="post">
            <input type="hidden" name="receiverId" value="<%= receiverId %>">
            <textarea name="message" placeholder="Type your message..." required></textarea><br>
            <button type="submit">Send</button>
        </form>
    <% } %>
</body>
</html>
