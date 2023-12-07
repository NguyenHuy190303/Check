package e2101098.java.server;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/courses/*")
public class CourseServlet extends HttpServlet {
    private Connection conn;

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

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String courseIdParam = request.getParameter("courseId"); // Assuming courseId is passed as a parameter
        System.out.println(courseIdParam);
        try {
            int courseId = Integer.parseInt(courseIdParam); // Convert the parameter to an integer
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Courses WHERE course_id = ?");
            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("course_id");
                String name = rs.getString("name");
                String teacher = rs.getString("teacher");
                RequestDispatcher reqDis = request.getRequestDispatcher("CourseList"); // Display course details in the servlet's response
                request.setAttribute("course_id", courseId);
                request.setAttribute("name", name);
                request.setAttribute("teacher", teacher);
                reqDis.forward(request, response);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().println("Course details not found");
            }
            ps.close();
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Error retrieving course details");
        }
    }

    // Implement doPost and doDelete methods for adding, updating, or deleting courses

    @Override
    public void destroy() {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
