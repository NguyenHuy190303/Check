package e2101098.java.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import com.google.gson.Gson;

import e2101098.java.server.Course;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/course/*")
public class CourseController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private CourseDAO courseDAO;
    private Gson gson;

    public void init() {
        courseDAO = new CourseDAO(); // Assuming CourseDAO exists
        gson = new Gson();
    }

    private void sendAsJSON(HttpServletResponse response, Object obj) throws ServletException, IOException {
        response.setContentType("application/json");
        String result = gson.toJson(obj);
        PrintWriter out = response.getWriter();
        out.print(result);
        out.flush();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        res.addHeader("Access-Control-Allow-Origin", "http://mariadb.vamk.fi/e2101098_java");
        res.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS, HEAD, PUT, POST, DELETE");

        if (pathInfo == null || pathInfo.equals("/")) {
            List<Course> courses = courseDAO.selectAllCourses();
            sendAsJSON(res, courses);
            return;
        }

        String splits[] = pathInfo.split("/");
        if (splits.length != 2) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        int courseId = Integer.parseInt(splits[1]);
        Course course = courseDAO.selectCourseById(courseId);
        if (course == null) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        } else {
            sendAsJSON(res, course);
            return;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        res.addHeader("Access-Control-Allow-Origin", "http://mariadb.vamk.fi/e2101098_java");
        res.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS, HEAD, PUT, POST, DELETE");

        String pathInfo = req.getPathInfo();
        System.out.println(pathInfo);
        if (pathInfo == null || pathInfo.equals("/")) {
            StringBuilder buffer = new StringBuilder();
            BufferedReader reader = req.getReader();

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            String payload = buffer.toString();
            Course course = gson.fromJson(payload, Course.class); // Assuming Course class exists
            courseDAO.saveCourse(course); // Assuming saveCourse method is available in CourseDAO
            sendAsJSON(res, "Success");
        } else {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
    }
}
