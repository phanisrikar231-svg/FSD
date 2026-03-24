import java.io.*;
import java.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {

    static final String DB_URL  = "jdbc:mysql://localhost:3306/college_db";
    static final String DB_USER = "root";
    static final String DB_PASS = "root123";

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String fullName   = request.getParameter("fullName");
        String email      = request.getParameter("email");
        String password   = request.getParameter("password");
        String phone      = request.getParameter("phone");
        String department = request.getParameter("department");
        int    courseId   = Integer.parseInt(request.getParameter("courseId"));
        String regDate    = request.getParameter("regDate");

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

            String checkSQL = "SELECT student_id FROM students WHERE email = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSQL);
            checkStmt.setString(1, email);
            ResultSet checkRs = checkStmt.executeQuery();

            if (checkRs.next()) {
                out.println("<!DOCTYPE html>");
                out.println("<html><head><title>Registration Failed</title>");
                out.println("<style>");
                out.println("body { font-family: Arial, sans-serif; background-color: #f0f2f5; display: flex; justify-content: center; align-items: center; min-height: 100vh; margin: 0; }");
                out.println(".card { background: white; padding: 40px; border-radius: 10px; box-shadow: 0 4px 15px rgba(0,0,0,0.1); width: 500px; text-align: center; }");
                out.println("h2 { color: #e74c3c; margin-bottom: 20px; }");
                out.println("p { color: #555; font-size: 15px; margin-bottom: 25px; }");
                out.println(".btn { display: block; width: 100%; padding: 12px; background-color: #3498db; color: white; border: none; border-radius: 6px; font-size: 16px; cursor: pointer; text-align: center; text-decoration: none; box-sizing: border-box; }");
                out.println("</style></head><body>");
                out.println("<div class='card'>");
                out.println("<h2>Email Already Registered!</h2>");
                out.println("<p>The email <b>" + email + "</b> is already registered. Please use a different email.</p>");
                out.println("<a href='index.html' class='btn'>Go Back and Try Again</a>");
                out.println("</div></body></html>");
                conn.close();
                return;
            }

            String studentSQL = "INSERT INTO students (full_name, email, password, phone, department) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement studentStmt = conn.prepareStatement(studentSQL, Statement.RETURN_GENERATED_KEYS);
            studentStmt.setString(1, fullName);
            studentStmt.setString(2, email);
            studentStmt.setString(3, password);
            studentStmt.setString(4, phone);
            studentStmt.setString(5, department);
            studentStmt.executeUpdate();

            ResultSet rs = studentStmt.getGeneratedKeys();
            int studentId = 0;
            if (rs.next()) {
                studentId = rs.getInt(1);
            }

            String regSQL = "INSERT INTO registrations (student_id, course_id, reg_date, reg_type) VALUES (?, ?, ?, 'regular')";
            PreparedStatement regStmt = conn.prepareStatement(regSQL);
            regStmt.setInt(1, studentId);
            regStmt.setInt(2, courseId);
            regStmt.setDate(3, Date.valueOf(regDate));
            regStmt.executeUpdate();

            String courseSQL = "SELECT course_name, course_code FROM courses WHERE course_id = ?";
            PreparedStatement courseStmt = conn.prepareStatement(courseSQL);
            courseStmt.setInt(1, courseId);
            ResultSet courseRs = courseStmt.executeQuery();
            String courseName = "";
            String courseCode = "";
            if (courseRs.next()) {
                courseName = courseRs.getString("course_name");
                courseCode = courseRs.getString("course_code");
            }

            conn.close();

            out.println("<!DOCTYPE html>");
            out.println("<html><head><title>Registration Successful</title>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; background-color: #f0f2f5; display: flex; justify-content: center; align-items: center; min-height: 100vh; margin: 0; }");
            out.println(".card { background: white; padding: 40px; border-radius: 10px; box-shadow: 0 4px 15px rgba(0,0,0,0.1); width: 500px; }");
            out.println("h2 { color: #27ae60; text-align: center; margin-bottom: 30px; }");
            out.println(".detail { display: flex; justify-content: space-between; padding: 12px 0; border-bottom: 1px solid #eee; font-size: 15px; }");
            out.println(".label { color: #888; font-weight: bold; }");
            out.println(".value { color: #2c3e50; }");
            out.println(".btn { display: block; width: 100%; padding: 12px; background-color: #3498db; color: white; border: none; border-radius: 6px; font-size: 16px; text-align: center; text-decoration: none; margin-top: 10px; box-sizing: border-box; }");
            out.println(".btn-green { display: block; width: 100%; padding: 12px; background-color: #27ae60; color: white; border: none; border-radius: 6px; font-size: 16px; text-align: center; text-decoration: none; margin-top: 10px; box-sizing: border-box; }");
            out.println("</style></head><body>");
            out.println("<div class='card'>");
            out.println("<h2>Registration Successful!</h2>");
            out.println("<div class='detail'><span class='label'>Student ID:</span><span class='value'>" + studentId + "</span></div>");
            out.println("<div class='detail'><span class='label'>Full Name:</span><span class='value'>" + fullName + "</span></div>");
            out.println("<div class='detail'><span class='label'>Email:</span><span class='value'>" + email + "</span></div>");
            out.println("<div class='detail'><span class='label'>Phone:</span><span class='value'>" + phone + "</span></div>");
            out.println("<div class='detail'><span class='label'>Department:</span><span class='value'>" + department + "</span></div>");
            out.println("<div class='detail'><span class='label'>Course:</span><span class='value'>" + courseName + " (" + courseCode + ")</span></div>");
            out.println("<div class='detail'><span class='label'>Registration Date:</span><span class='value'>" + regDate + "</span></div>");
            out.println("<a href='login.html' class='btn-green'>Go to Student Login</a>");
            out.println("<a href='index.html' class='btn'>Register Another Student</a>");
            out.println("</div></body></html>");

        } catch (Exception e) {
            out.println("<html><body style='font-family:Arial; text-align:center; margin-top:100px;'>");
            out.println("<h2 style='color:red;'>Registration Failed!</h2>");
            out.println("<p>" + e.getMessage() + "</p>");
            out.println("<a href='index.html'>Go Back</a>");
            out.println("</body></html>");
        }
    }
}