package e2101098.java.server;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/RegistServlet")
public class RegistServlet extends HttpServlet {
    private Connection conn;
    private PreparedStatement ps;
    private int resultSet;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext context = config.getServletContext();
        String dbUser = context.getInitParameter("db_user");
        String dbPassword = context.getInitParameter("db_password");
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mariadb://mariadb.vamk.fi/e2101098_java", "e2101098", "cqgYeaFEN6A");
            ps = conn.prepareStatement("INSERT INTO Students (name, email, password) VALUES(?, ?, ?)");
            System.out.println();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        RequestDispatcher reqDis;
        if (!isValidEmail(email)) {
            reqDis = req.getRequestDispatcher("register.html");
            req.setAttribute("message", "Invalid email format");
            reqDis.include(req, res);
            return; // Exit the method, no need to proceed further
        }
        try {
            // Check if the email already exists in the database
            PreparedStatement checkEmailPS = conn.prepareStatement("SELECT * FROM Students WHERE email = ?");
            checkEmailPS.setString(1, email);
            ResultSet emailResult = checkEmailPS.executeQuery();

            if (emailResult.next()) {
                // Email already exists, treat it as unsuccessful
                reqDis = req.getRequestDispatcher("login.html");
                req.setAttribute("message", "Email already exists");
                reqDis.include(req, res);
            } else {
                // Email doesn't exist, proceed with registration
                ps.setString(1, name);
                ps.setString(2, email);
                ps.setString(3, password);
                resultSet = ps.executeUpdate();

                if (resultSet > 0) {
                    // Registration success
                    req.setAttribute("message", "Registration successful");
                    RequestDispatcher reqDis1 = req.getRequestDispatcher("login.html");
                    reqDis1.forward(req, res);
                } else {
                    // Registration unsuccessful
                    reqDis = req.getRequestDispatcher("RegistServlet");
                    req.setAttribute("message", "Registration unsuccessful");
                    reqDis.include(req, res);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        Pattern pattern = Pattern.compile(emailRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Override
    public void destroy() {
        try {
            ps.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
