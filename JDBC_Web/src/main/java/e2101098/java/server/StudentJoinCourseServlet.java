package e2101098.java.server;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/studentJoinCourseServlet")
public class StudentJoinCourseServlet extends HttpServlet {
    private Connection conn;
    private PreparedStatement ps;

    @Override
    public void init() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mariadb://mariadb.vamk.fi/e2101098_java", "e2101098", "cqgYeaFEN6A");
            System.out.println(conn);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String studentIdParam = req.getParameter("studentId");
        String courseIdParam = req.getParameter("courseId");

        try {
            int studentId = Integer.parseInt(studentIdParam);
            int courseId = Integer.parseInt(courseIdParam);
            PreparedStatement ps = conn.prepareStatement("INSERT INTO Student_Course (student_id, course_id) VALUES(?, ?)");
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);

            int rowsInserted = ps.executeUpdate();
            if (rowsInserted > 0) {
                res.setStatus(HttpServletResponse.SC_FOUND);
                res.getWriter().println("Successfully joined the course");
            } else {
                res.setStatus(HttpServletResponse.SC_NOT_FOUND);
                res.getWriter().println("Failed to join the course");
            }
            ps.close();
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.getWriter().println("Error joining the course");
        }
    }

    @Override
    public void destroy() {
        try {
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
