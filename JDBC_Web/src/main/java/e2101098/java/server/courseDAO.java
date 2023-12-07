import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {
    private String jdbcURL = "jdbc:mariadb://mariadb.vamk.fi/e2101098_java";
    private String jdbcUserName = "e2101098";
    private String jdbcPassword = "cqgYeaFEN6A";

    // Constructors
    public CourseDAO(String url, String userName, String password) {
        this.jdbcURL = url;
        this.jdbcUserName = userName;
        this.jdbcPassword = password;
    }

    public CourseDAO() {
    }

    private static final String SELECT_ALL_COURSES_QUERY = "SELECT * FROM Courses";
    private static final String SELECT_COURSE_BY_ID = "SELECT * FROM Courses WHERE course_id=?";

    protected Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            conn = DriverManager.getConnection(jdbcURL, jdbcUserName, jdbcPassword);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public List<Course> selectAllCourses() {
        List<Course> courses = new ArrayList<>();

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(SELECT_ALL_COURSES_QUERY)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int course_id = rs.getInt(1);
                String name = rs.getString(2);
                String teacher = rs.getString(3);

                courses.add(new Course(course_id, name, teacher));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return courses;
    }

    public Course selectCourseById(int course_id) {
        Course course = null;

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(SELECT_COURSE_BY_ID)) {
            ps.setInt(1, course_id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String name = rs.getString("Name");
                String teacher = rs.getString("teacher");

                course = new Course(course_id, name, teacher);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return course;
    }

    public void saveCourse(Course course) {
        // Implement the logic to save a course to the database
    }
}
