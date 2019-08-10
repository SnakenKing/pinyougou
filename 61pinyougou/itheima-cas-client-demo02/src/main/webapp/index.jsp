<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="org.jasig.cas.client.util.AssertionHolder" %>
<html>
<body>
<h2>Hello World!</h2>


<h1>欢迎来到欢乐谷22222:<%=request.getRemoteUser()%></h1>
<h1>欢迎来到欢乐谷222222:<%=AssertionHolder.getAssertion().getPrincipal().getName()%></h1>

<a href="http://localhost:9400/cas/logout?service=http://www.itheima.com">退出</a>
</body>
</html>
