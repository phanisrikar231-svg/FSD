import java.io.*;
import java.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet("/DashboardServlet")
public class DashboardServlet extends HttpServlet {

    static final String DB_URL  = "jdbc:mysql://localhost:3306/college_db";
    static final String DB_USER = "root";
    static final String DB_PASS = "root123";

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        if (session == null || session.getAttribute("studentId") == null) {
            response.sendRedirect("login.html");
            return;
        }

        int studentId     = (int) session.getAttribute("studentId");
        String fullName   = (String) session.getAttribute("fullName");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

            // Get registered courses
            String regSQL = "SELECT c.course_name, c.course_code, c.credits, r.reg_date, r.reg_type " +
                            "FROM registrations r JOIN courses c ON r.course_id = c.course_id " +
                            "WHERE r.student_id = ?";
            PreparedStatement regStmt = conn.prepareStatement(regSQL);
            regStmt.setInt(1, studentId);
            ResultSet regRs = regStmt.executeQuery();

            // Count electives already registered
            String countSQL = "SELECT COUNT(*) FROM registrations WHERE student_id = ? AND reg_type = 'elective'";
            PreparedStatement countStmt = conn.prepareStatement(countSQL);
            countStmt.setInt(1, studentId);
            ResultSet countRs = countStmt.executeQuery();
            int electiveCount = 0;
            if (countRs.next()) {
                electiveCount = countRs.getInt(1);
            }

            // Get available elective courses (not already registered by this student)
            String electiveSQL = "SELECT c.course_id, c.course_name, c.course_code, c.credits FROM courses c " +
                                 "WHERE c.course_id NOT IN (SELECT course_id FROM registrations WHERE student_id = ?) " +
                                 "AND c.course_code IN ('AI101','WEB101','DS101','CYB101')";
            PreparedStatement electiveStmt = conn.prepareStatement(electiveSQL);
            electiveStmt.setInt(1, studentId);
            ResultSet electiveRs = electiveStmt.executeQuery();

            out.println("<!DOCTYPE html>");
            out.println("<html><head><title>Student Dashboard</title>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; background-color: #f0f2f5; margin: 0; padding: 30px; }");
            out.println(".header { background: #2c3e50; color: white; padding: 20px 30px; border-radius: 10px; display: flex; justify-content: space-between; align-items: center; margin-bottom: 25px; }");
            out.println(".header h2 { margin: 0; }");
            out.println(".logout { color: white; text-decoration: none; background: #e74c3c; padding: 8px 16px; border-radius: 6px; font-size: 14px; }");
            out.println(".section { background: white; padding: 25px; border-radius: 10px; box-shadow: 0 4px 15px rgba(0,0,0,0.1); margin-bottom: 25px; }");
            out.println("h3 { color: #2c3e50; margin-bottom: 15px; border-bottom: 2px solid #3498db; padding-bottom: 8px; }");
            out.println("table { width: 100%; border-collapse: collapse; }");
            out.println("th { background-color: #3498db; color: white; padding: 12px; text-align: left; font-size: 14px; }");
            out.println("td { padding: 11px 12px; font-size: 14px; color: #2c3e50; border-bottom: 1px solid #eee; }");
            out.println("tr:hover { background-color: #f8f9fa; }");
            out.println(".badge { padding: 4px 10px; border-radius: 12px; font-size: 12px; font-weight: bold; }");
            out.println(".regular { background: #d5f5e3; color: #1e8449; }");
            out.println(".elective { background: #d6eaf8; color: #1a5276; }");
            out.println(".btn { display: inline-block; padding: 8px 18px; background-color: #27ae60; color: white; border: none; border-radius: 6px; font-size: 13px; cursor: pointer; text-decoration: none; }");
            out.println(".btn:hover { background-color: #219150; }");
            out.println(".btn-disabled { display: inline-block; padding: 8px 18px; background-color: #bdc3c7; color: white; border: none; border-radius: 6px; font-size: 13px; cursor: not-allowed; }");
            out.println(".info { color: #888; font-size: 14px; margin-bottom: 15px; }");
            out.println("</style></head><body>");

            // Header
            out.println("<div class='header'>");
            out.println("<h2>Welcome, " + fullName + "!</h2>");
            out.println("<a href='LogoutServlet' class='logout'>Logout</a>");
            out.println("</div>");

            // My Registered Courses
            out.println("<div class='section'>");
            out.println("<h3>My Registered Courses</h3>");
            out.println("<table>");
            out.println("<tr><th>Course Name</th><th>Course Code</th><th>Credits</th><th>Reg Date</th><th>Type</th></tr>");
            boolean hasCourses = false;
            while (regRs.next()) {
                hasCourses = true;
                String type = regRs.getString("reg_type");
                String badgeClass = type.equals("elective") ? "elective" : "regular";
                out.println("<tr>");
                out.println("<td>" + regRs.getString("course_name") + "</td>");
                out.println("<td>" + regRs.getString("course_code") + "</td>");
                out.println("<td>" + regRs.getInt("credits") + "</td>");
                out.println("<td>" + regRs.getDate("reg_date") + "</td>");
                out.println("<td><span class='badge " + badgeClass + "'>" + type + "</span></td>");
                out.println("</tr>");
            }
            if (!hasCourses) {
                out.println("<tr><td colspan='5' style='text-align:center; color:#888;'>No courses registered yet.</td></tr>");
            }
            out.println("</table>");
            out.println("</div>");

            // Open Electives Section
            out.println("<div class='section'>");
            out.println("<h3>Open Electives</h3>");
            if (electiveCount >= 2) {
                out.println("<p class='info'>You have already registered for the maximum of 2 open elective courses.</p>");
            } else {
                out.println("<p class='info'>You can register for up to 2 open elective courses. You have registered " + electiveCount + " so far.</p>");
            }
            out.println("<table>");
            out.println("<tr><th>Course Name</th><th>Course Code</th><th>Credits</th><th>Action</th></tr>");
            boolean hasElectives = false;
            while (electiveRs.next()) {
                hasElectives = true;
                out.println("<tr>");
                out.println("<td>" + electiveRs.getString("course_name") + "</td>");
                out.println("<td>" + electiveRs.getString("course_code") + "</td>");
                out.println("<td>" + electiveRs.getInt("credits") + "</td>");
                if (electiveCount >= 2) {
                    out.println("<td><span class='btn-disabled'>Limit Reached</span></td>");
                } else {
                    out.println("<td><a href='ElectiveServlet?courseId=" + electiveRs.getInt("course_id") + "' class='btn'>Register</a></td>");
                }
                out.println("</tr>");
            }
            if (!hasElectives) {
                out.println("<tr><td colspan='4' style='text-align:center; color:#888;'>No more electives available.</td></tr>");
            }
            out.println("</table>");
            out.println("</div>");
            out.println("</body></html>");

            conn.close();

        } catch (Exception e) {
            out.println("<h2 style='color:red;'>Error: " + e.getMessage() + "</h2>");
        }
    }
}