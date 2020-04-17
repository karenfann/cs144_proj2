import java.io.IOException;
import java.sql.* ;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import models.Post;

/**
 * Servlet implementation class for Servlet: ConfigurationTest
 *
 */
public class Editor extends HttpServlet {
    Connection connection;
        
    /**
     * The Servlet constructor
     * 
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public Editor() {}

    public void init() throws ServletException
    {
        // load driver
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e);
            return;
        }

        // establish a connection
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/CS144", "cs144", "");
        } catch (SQLException ex) {
            System.out.println("SQLException caught");
            System.out.println("---");
            while ( ex != null ) {
                System.out.println("Message   : " + ex.getMessage());
                System.out.println("SQLState  : " + ex.getSQLState());
                System.out.println("ErrorCode : " + ex.getErrorCode());
                System.out.println("---");
                ex = ex.getNextException();
            }
        }
    }
    
    public void destroy()
    {
        if (connection != null) {
            try { connection.close(); } catch (Exception e) { /* ignore */ }
        }
    }

    /**
     * Handles HTTP GET requests
     * 
     * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request,
     *      HttpServletResponse response)
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException 
    {
	// implement your GET method handling code here
	    String action = request.getParameter("action");
        
        if (action == null || action.isEmpty()) {
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
        
       switch(action) {
            case "list":
                handleList(request, response);
                break;              
            case "open":
                handleOpen(request, response);
                break;
            default:
                request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    public void handleList(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        String username = request.getParameter("username");
        
        // check for valid username parameter
        if (username == null) {
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }

        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<Post> posts = new ArrayList<Post>();

        // retrieve posts from database
        try {
            ps = connection.prepareStatement("SELECT * FROM Posts WHERE username = ? ORDER BY postid ASC");
            ps.setString(1, username);
            rs = ps.executeQuery();

            while (rs.next()) {
                Post post = new Post();
                
                post.postid = rs.getInt("postid");
                post.title = rs.getString("title");
                post.body = rs.getString("body");
                post.modified = rs.getTimestamp("modified");
                post.created = rs.getTimestamp("created");
                                
                posts.add(post);
            }
            
        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
        } finally {
            try { rs.close(); } catch (Exception e) { /* ignore */ }
            try { ps.close(); } catch (Exception e) { /* ignore */ }
        }

        request.setAttribute("posts", posts);
        request.getRequestDispatcher("/list.jsp").forward(request, response);

    }

    public void handleOpen(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        String username = request.getParameter("username");
        String postid = request.getParameter("postid");
        String title = request.getParameter("title");
        String body = request.getParameter("body");

        if (username == null || postid == null) {
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }

        if (Integer.parseInt(postid) > 0) {
            // retrieve post from database if title or body is missing
            if (title == null || body == null) {
                PreparedStatement ps = null;
                ResultSet rs = null;

                try {
                    ps = connection.prepareStatement("SELECT title, body FROM Posts WHERE username = ? AND postid = ?");
                    rs = ps.executeQuery();

                    if (rs.next()) {
                        title = rs.getString("title");
                        body = rs.getString("body");
                    } else {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    }
                } catch (SQLException e) {
                    System.err.println("SQLException: " + e.getMessage()); 
                } finally {
                    try { rs.close(); } catch (Exception e) { /* ignore */ }
                    try { ps.close(); } catch (Exception e) { /* ignore */ }
                }
            }
        } else {
            if (title == null) {
                title = "";
            }

            if (body == null) {
                body = "";
            } 
        } 
   
        request.setAttribute("title", title);
        request.setAttribute("body", body); 
        request.getRequestDispatcher("/edit.jsp").forward(request, response);
    }
    
    /**
     * Handles HTTP POST requests
     * 
     * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request,
     *      HttpServletResponse response)
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException 
    {
	// implement your POST method handling code here
	// currently we simply show the page generated by "edit.jsp"
        request.getRequestDispatcher("/edit.jsp").forward(request, response);
    }
}

