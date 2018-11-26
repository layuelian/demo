<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.thomas.simpletag.User" %>
<%@ taglib uri="/thomas" prefix="my"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
<%
	
List<User> users=Arrays.asList(new User(1,"小黑","123"),new User(2,"小白","123"));
request.setAttribute("users", users);
%>
	<my:tag1>你好</my:tag1><br>
	<my:tag2></my:tag2>
	dadas<br>
	<my:tag3>小黑<br></my:tag3>
	<my:tag4>小白</my:tag4><br>
	<my:tag5 show="true">显示</my:tag5><br>
	<my:tag5 show="false">不显示</my:tag5><br>
	<my:tag6 date="<%=new Date() %>"></my:tag6>
	<table>
	<my:for items="${users }" var="user">
		<tr>
			<td>${user.id }</td>
			<td>${user.name }</td>
			<td>${user.pwd }</td>
		</tr>
	</my:for>
	</table><br>
	<%
		request.setAttribute("flag", false);
	%>
	<my:choose>
		<my:when test="${flag }">
			执行了when<br>
		</my:when>
		<my:otherwise>
			执行了otherwise
		</my:otherwise>
	</my:choose>
</body>
</html>