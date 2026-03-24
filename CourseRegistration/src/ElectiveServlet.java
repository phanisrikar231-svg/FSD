import java.io.*;
import java.sql.*;
import java.time.LocalDate;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet("/ElectiveServlet")
public class ElectiveServlet extends HttpServlet {

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

        int studentId = (int) session.getAttribute("studentId");
        int courseId  = Integer.parseInt(request.getParameter("courseId"));

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

            // Check elective count
            String countSQL = "SELECT COUNT(*) FROM registrations WHERE student_id = ? AND reg_type = 'elective'";
            PreparedStatement countStmt = conn.prepareStatement(countSQL);
            countStmt.setInt(1, studentId);
            ResultSet countRs = countStmt.executeQuery();
            int electiveCount = 0;
            if (countRs.next()) {
                electiveCount = countRs.getInt(1);
            }

            if (electiveCount >= 2) {
                out.println("<html><body style='font-family:Arial; text-align:center; margin-top:100px;'>");
                out.println("<h2 style='color:red;'>Limit Reached!</h2>");
                out.println("<p>You can only register for a maximum of 2 open elective courses.</p>");
                out.println("<a href='DashboardServlet'>Back to Dashboard</a>");
                out.println("</body></html>");
                conn.close();
                return;
            }

            // Register elective
            String regSQL = "INSERT INTO registrations (student_id, course_id, reg_date, reg_type) VALUES (?, ?, ?, 'elective')";
            PreparedStatement regStmt = conn.prepareStatement(regSQL);
            regStmt.setInt(1, studentId);
            regStmt.setInt(2, courseId);
            regStmt.setDate(3, Date.valueOf(LocalDate.now()));
            regStmt.executeUpdate();

            conn.close();
            response.sendRedirect("DashboardServlet");

        } catch (Exception e) {
            out.println("<h2 style='color:red;'>Error: " + e.getMessage() + "</h2>");
        }
    }
}