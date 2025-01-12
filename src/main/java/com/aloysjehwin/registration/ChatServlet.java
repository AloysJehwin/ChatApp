package com.aloysjehwin.registration;

import java.io.IOException;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.mysql.jdbc.Connection;

@WebServlet("/ChatServlet")
public class ChatServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get the session and userId
        HttpSession session = request.getSession(false);  // Don't create a new session if it doesn't exist
        if (session == null || session.getAttribute("userId") == null) {
            // Log for debugging
            System.out.println("Session is null or userId is not set. Redirecting to login.");
            // Redirect to login if the user is not authenticated
            response.sendRedirect("login.jsp");
            return;
        }

        Integer senderId = (Integer) session.getAttribute("userId");
        if (senderId == null) {
            // Log for debugging
            System.out.println("senderId is null. Redirecting to login.");
            response.sendRedirect("login.jsp");
            return;
        }

        // Get the receiverId from the request
        int receiverId = 0;
        try {
            receiverId = Integer.parseInt(request.getParameter("receiverId"));
        } catch (NumberFormatException e) {
            // Log for debugging
            System.out.println("Invalid receiverId format. Redirecting to chat.");
            e.printStackTrace();
            response.sendRedirect("chat.jsp?error=Invalid receiverId");
            return;
        }

        // Get the message from the request
        String message = request.getParameter("message");
        if (message == null || message.trim().isEmpty()) {
            response.sendRedirect("chat.jsp?receiverId=" + receiverId + "&error=Message cannot be empty");
            return;
        }

        // Database connection and message insertion
        try (Connection con = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/chatapp", "root", "Ausnet@1975")) {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String query = "INSERT INTO messages (sender_id, receiver_id, message) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = con.prepareStatement(query)) {
                stmt.setInt(1, senderId);
                stmt.setInt(2, receiverId);
                stmt.setString(3, message);
                stmt.executeUpdate();
            }

            // After successful message insertion, redirect to the chat page with receiverId
            response.sendRedirect("chat.jsp?receiverId=" + receiverId);
        } catch (Exception e) {
            // Log the error
            e.printStackTrace();
            response.sendRedirect("chat.jsp?receiverId=" + receiverId + "&error=Database error occurred");
        }
    }
}
