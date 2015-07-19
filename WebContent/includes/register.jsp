<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>includes/register.jsp</title>
</head>
<body>

<% 
Object message = request.getAttribute("message");
if( message != null )
	out.println(message.toString());
%>

<h1>Register</h1>

<form action="register" method="post">
	E-Mail
	<input type="text" name="email" />
	<br/>
	Password
	<input type="password" name="p" />
	<br/>
	Admin
	<input type="checkbox" name="role_admin" value="1" />
	<br/>
	<input type="submit" />
</form>

</body>
</html>
