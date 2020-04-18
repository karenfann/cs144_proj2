<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %><!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit Post</title>
</head>
<body>
    <%  String title = request.getAttribute("title").toString(); 
        String username = request.getParameter("username"); 
        String postid = request.getParameter("postid"); %>
    <div><h1>Edit Post</h1></div>
    <form action="post" method="post">
        <input type="hidden" name="username" value="<%= username %>">
        <input type="hidden" name="postid" value="<%= postid %>">
        <div>
            <button type="submit" name="action" value="save">Save</button>
            <button type="submit" name="action" value="list">Close</button>
            <button type="submit">Preview</button>
            <button type="submit">Delete</button>
        </div>
        <div>
            <label for="title">Title</label>
            <input type="text" id="title" name="title" value="<%= title %>">
        </div>
        <div>
            <label for="body">Body</label>
            <textarea style="height: 20rem;" id="body" name="body"><%= request.getAttribute("body") %></textarea>
        </div>
    </form>
</body>
</html>
