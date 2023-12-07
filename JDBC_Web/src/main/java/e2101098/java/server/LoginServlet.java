package e2101098.java.server;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/loginServlet")
public class LoginServlet extends HttpServlet {

    private Connection conn;

    @Override
    public void init() throws ServletException {
        // Establish database connection in the init method
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mariadb://mariadb.vamk.fi/e2101098_java", "e2101098", "cqgYeaFEN6A");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            PreparedStatement ps = conn.prepareStatement("SELECT password FROM Students WHERE email = ?");
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String passwordFromDB = rs.getString("password");
                // Use a secure method to compare hashed passwords (e.g., BCrypt)
                // Replace this comparison with your actual password comparison method
                if (password.equals(passwordFromDB)) {
                    // Authentication successful, create a session and redirect
                    request.getSession().setAttribute("email", email);
                    response.sendRedirect(request.getContextPath() + "/studentAdministration.html");
                    return;
                } else {
                    // Incorrect password
                    response.sendRedirect(request.getContextPath() + "/login.html?error=incorrect_password");
                    return;
                }
            } else {
                // Email not found in the database
                response.sendRedirect(request.getContextPath() + "/login.html?error=email_not_found");
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Authentication failed due to an unknown reason
        response.sendRedirect(request.getContextPath() + "/login.html?error=authentication_failed");
    }
}
