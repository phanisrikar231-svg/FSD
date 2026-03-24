import java.io.*;
import java.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    static final String DB_URL  = "jdbc:mysql://localhost:3306/college_db";
    static final String DB_USER = "root";
    static final String DB_PASS = "root123";

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email    = request.getParameter("email");
        String password = request.getParameter("password");

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

            String sql = "SELECT student_id, full_name FROM students WHERE email = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int studentId   = rs.getInt("student_id");
                String fullName = rs.getString("full_name");
                conn.close();

                // Login successful - redirect to dashboard
                HttpSession session = request.getSession();
                session.setAttribute("studentId", studentId);
                session.setAttribute("fullName", fullName);
                response.sendRedirect("DashboardServlet");
            } else {
                conn.close();
                out.println("<!DOCTYPE html>");
                out.println("<html><head><title>Login Failed</title>");
                out.println("<style>");
                out.println("body { font-family: Arial, sans-serif; background-color: #f0f2f5; display: flex; justify-content: center; align-items: center; min-height: 100vh; margin: 0; }");
                out.println(".card { background: white; padding: 40px; border-radius: 10px; box-shadow: 0 4px 15px rgba(0,0,0,0.1); width: 500px; text-align: center; }");
                out.println("h2 { color: #e74c3c; margin-bottom: 20px; }");
                out.println("p { color: #555; font-size: 15px; margin-bottom: 25px; }");
                out.println(".btn { display: block; width: 100%; padding: 12px; background-color: #3498db; color: white; border: none; border-radius: 6px; font-size: 16px; text-align: center; text-decoration: none; box-sizing: border-box; }");
                out.println("</style></head><body>");
                out.println("<div class='card'>");
                out.println("<h2>Login Failed!</h2>");
                out.println("<p>Invalid email or password. Please try again.</p>");
                out.println("<a href='login.html' class='btn'>Try Again</a>");
                out.println("</div></body></html>");
            }

        } catch (Exception e) {
            out.println("<h2 style='color:red;'>Error: " + e.getMessage() + "</h2>");
        }
    }
}