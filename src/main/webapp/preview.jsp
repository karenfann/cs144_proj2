<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%><%@ taglib uri="http://java.sun.com/jsp/jstl/core"
prefix="c" %><!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8" />
    <title>Preview Post</title>
  </head>
  <% String title = request.getParameter("title"); %> <% String body =
  request.getParameter("body"); %> <% String postid =
  request.getParameter("postid"); %> <% String username =
  request.getParameter("username"); %> <% String body_html =
  request.getAttribute("body_html").toString(); %>
  <body>
    <div>
      <form action="post">
        <input type="hidden" name="username" value="<%= username%>" />
        <input type="hidden" name="postid" value="<%= postid%>" />
        <input type="hidden" name="title" value="<%= title%>" />
        <input type="hidden" name="body" value="<%= body%>" />
        <button type="submit" name="action" value="open">Close</button>
      </form>
    </div>
    <div><h1><%= title %></h1></div>
    <%= body_html %>
  </body>
</html>
