<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="models.Post,java.util.*" %><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %><!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit Post</title>
</head>
<body>
    <div><h1>Edit Post</h1></div>
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
            </tr>
        <% } %>
    </table>
</body>
</html>
