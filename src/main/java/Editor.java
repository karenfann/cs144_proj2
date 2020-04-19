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
            throw new ServletException("Cannot load driver");
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
            throw new ServletException("Cannot establish connection");
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
        
        if (action == null) {
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }
        
       switch(action) {
            case "list":
                handleList(request, response);
                break;              
            case "open":
                handleOpen(request, response);
                break;
            case "preview":
                handlePreview(request, response);
                break;
            default:
                request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
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
        String action = request.getParameter("action");
        
        if (action == null) {
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }

        switch(action) {
            case "list":
                handleList(request, response);
                break;
            case "open":
                handleOpen(request, response);
                break;
            case "save":
                handleSave(request, response);
                break;
            case "preview":
                handlePreview(request, response);
                break;
            case "delete":
                handleDelete(request, response);
                break;
            default:
                request.getRequestDispatcher("/error.jsp").forward(request, response);
        }

    }

    /**
     * Handles request of list action type
     **/
    public void handleList(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        String username = request.getParameter("username");
        
        // check for required parameters
        if (username == null) {
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }

        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<Post> posts = new ArrayList<Post>();

        try {
            // query for posts matching username, sorted by postid
            ps = connection.prepareStatement("SELECT * FROM Posts WHERE username = ? ORDER BY postid ASC");
            ps.setString(1, username);
            
            rs = ps.executeQuery();

            // parse results into list of Posts
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
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        } finally {
            try { rs.close(); } catch (Exception e) { /* ignore */ }
            try { ps.close(); } catch (Exception e) { /* ignore */ }
        }

        request.setAttribute("posts", posts);
        request.getRequestDispatcher("/list.jsp").forward(request, response);

    }

    /**
     * Handles request of open action type
    **/
    public void handleOpen(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        String username = request.getParameter("username");
        String postid = request.getParameter("postid");
        String title = request.getParameter("title");
        String body = request.getParameter("body");

        // check for required parameters
        if (username == null || postid == null) {
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }

        if (Integer.parseInt(postid) > 0) {
            // retrieve post from database if title or body is missing
            if (title == null || body == null) {
                PreparedStatement ps = null;
                ResultSet rs = null;

                try {
                    // query for post matching (username, postid)
                    ps = connection.prepareStatement("SELECT title, body FROM Posts WHERE username = ? AND postid = ?");
                    rs = ps.executeQuery();

                    if (rs.next()) {
                        // post exists, set title and body to stored values
                        title = rs.getString("title");
                        body = rs.getString("body");
                    } else {
                        // post does not exist
                        response.sendError(HttpServletResponse.SC_NOT_FOUND);
                        return;
                    }
                } catch (SQLException e) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
                    return;
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
     * Handles request of open action type
    **/
    public void handleSave(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        String username = request.getParameter("username");
        String postid = request.getParameter("postid");
        String title = request.getParameter("title");
        String body = request.getParameter("body");

        // check for required parameters
        if (username == null || postid == null || title == null || body == null) {
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            if (Integer.parseInt(postid) > 0) {
                // update post matching (username, postid)
                ps = connection.prepareStatement("UPDATE Posts SET title = ?, body = ?, modified = ? WHERE username = ? AND postid = ?");
                ps.setString(1, title);
                ps.setString(2, body);
                ps.setTimestamp(3, getCurrentTimestamp());
                ps.setString(4, username);
                ps.setInt(5, Integer.parseInt(postid));

                // note: if there is no post matching (username, postid)
                // this update will not affect the database
                int n = ps.executeUpdate();
            } else {
                int nextid;
                
                // query for next highest postid
                ps = connection.prepareStatement("SELECT * FROM (SELECT max(postid)+1 AS nextid FROM Posts WHERE username = ?) AS tmp WHERE nextid IS NOT NULL");
                ps.setString(1, username);
                
                rs = ps.executeQuery();

                if (rs.next()) {
                    // posts exist for username, set nextid
                    nextid = rs.getInt("nextid");
                } else {
                    // user has no posts, assign nextid
                    nextid = 1;
                }

                System.out.println(nextid);

                // insert new post into database
                ps = connection.prepareStatement("INSERT INTO Posts VALUES(?, ?, ?, ?, ?, ?)");
                ps.setString(1, username);
                ps.setInt(2, nextid);
                ps.setString(3, title);
                ps.setString(4, body);
                ps.setTimestamp(5, getCurrentTimestamp());
                ps.setTimestamp(6, getCurrentTimestamp());

                int n = ps.executeUpdate();
            }
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
        } finally {
            try { rs.close(); } catch (Exception e) { /* ignore */ }
            try { ps.close(); } catch (Exception e) { /* ignore */ }
        }

        response.sendRedirect(request.getContextPath() + "/post?action=list&username=" + username);
    }

    public Timestamp getCurrentTimestamp() {
        Date today = new Date();
        return new Timestamp(today.getTime());
    }

    /**
     * Handles request of preview action type
    **/
    public void handlePreview(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        String username = request.getParameter("username");
        String postid = request.getParameter("postid");
        String title = request.getParameter("title");
        String body = request.getParameter("body");

        if (username == null || postid == null || title == null || body == null) {
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }

        // Parse markdown body to HTML
        Parser parser = Parser.builder().build();
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        String body_html = renderer.render(parser.parse(body));

        request.setAttribute("body_html", body_html);
        request.getRequestDispatcher("/preview.jsp").forward(request, response);
    }

    public void handleDelete(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        String username = request.getParameter("username");
        String postid = request.getParameter("postid");

        if (username == null || postid == null) {
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }

        // delete the post from database
        PreparedStatement ps = null;
        try {
            String query = "DELETE FROM Posts WHERE postid = ?";
            ps = connection.prepareStatement(query);
            ps.setString(1, postid);
            ps.execute();
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
        } finally {
            try { ps.close(); } catch (Exception e) { /* ignore */ }
        }

        response.sendRedirect(request.getContextPath() + "/post?action=list&username=" + username);
    }
}
