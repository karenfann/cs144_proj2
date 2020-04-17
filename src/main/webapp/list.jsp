<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="models.Post,java.util.*" %><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %><!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Post List</title>
</head>
<body>
    <% String username = request.getParameter("username"); %>
    <div><h1>Post List</h1></div>
    <form>
        <button type="submit">New Post</button>
    </form>
    <br>
    <table>
        <tr>
            <th>Title</th>
            <th>Created</th>
            <th>Modified</th>
        </tr>
        <% for (Post p : (ArrayList<Post>) request.getAttribute("posts")) { %>
            <tr>
                <td><%= p.title %></td>
                <td><%= p.created %></td>
                <td><%= p.modified %></td>
                <td>
                    <form>
                        <input type="hidden" name="username" value="<%= username %>">
                        <input type="hidden" name="postid" value="<%= p.postid %>">
                        <input type="hidden" name="title" value="<%= p.title %>">
                        <input type="hidden" name="body" value="<%= p.body %>"> 
                        <button type="submit" name="action" value="open">Open</button>
                        <button type="submit" name="action" value="preview">Preview</button>
                    </form>
                </td>
            </tr>
        <% } %>
    </table>
</body>
</html>
