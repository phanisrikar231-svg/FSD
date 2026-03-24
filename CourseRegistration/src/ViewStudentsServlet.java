import java.io.*;
import java.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet("/ViewStudents")
public class ViewStudentsServlet extends HttpServlet {

    static final String DB_URL  = "jdbc:mysql://localhost:3306/college_db";
    static final String DB_USER = "root";
    static final String DB_PASS = "root123";

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

            String sql = "SELECT s.student_id, s.full_name, s.email, s.phone, s.department, c.course_name, c.course_code, r.reg_date " +
                         "FROM registrations r " +
                         "JOIN students s ON r.student_id = s.student_id " +
                         "JOIN courses c ON r.course_id = c.course_id " +
                         "ORDER BY r.reg_date DESC";

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            out.println("<!DOCTYPE html>");
            out.println("<html><head><title>All Registered Students</title>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; background-color: #f0f2f5; margin: 0; padding: 30px; }");
            out.println("h2 { text-align: center; color: #2c3e50; margin-bottom: 25px; }");
            out.println("table { width: 100%; border-collapse: collapse; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.1); }");
            out.println("th { background-color: #3498db; color: white; padding: 14px; text-align: left; font-size: 14px; }");
            out.println("td { padding: 12px 14px; font-size: 14px; color: #2c3e50; border-bottom: 1px solid #eee; }");
            out.println("tr:hover { background-color: #f8f9fa; }");
            out.println(".btn { display: inline-block; padding: 10px 25px; background-color: #3498db; color: white; border-radius: 6px; text-decoration: none; margin-top: 20px; font-size: 14px; }");
            out.println(".btn:hover { background-color: #2980b9; }");
            out.println(".center { text-align: center; margin-top: 20px; }");
            out.println("</style></head><body>");
            out.println("<h2>All Registered Students</h2>");
            out.println("<table>");
            out.println("<tr><th>Student ID</th><th>Full Name</th><th>Email</th><th>Phone</th><th>Department</th><th>Course</th><th>Reg Date</th></tr>");

            while (rs.next()) {
                out.println("<tr>");
                out.println("<td>" + rs.getInt("student_id") + "</td>");
                out.println("<td>" + rs.getString("full_name") + "</td>");
                out.println("<td>" + rs.getString("email") + "</td>");
                out.println("<td>" + rs.getString("phone") + "</td>");
                out.println("<td>" + rs.getString("department") + "</td>");
                out.println("<td>" + rs.getString("course_name") + " (" + rs.getString("course_code") + ")</td>");
                out.println("<td>" + rs.getDate("reg_date") + "</td>");
                out.println("</tr>");
            }

            out.println("</table>");
            out.println("<div class='center'><a href='index.html' class='btn'>Register New Student</a></div>");
            out.println("</body></html>");

            conn.close();

        } catch (Exception e) {
            out.println("<h2 style='color:red;'>Error: " + e.getMessage() + "</h2>");
        }
    }
}