<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page
	import="java.util.List,be.hehehe.geekbot.persistence.model.QuizzPlayer"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Quizz Scoreboard</title>
<link rel="stylesheet" href="css/style.css" type="text/css" />
</head>
<body>
	<div class="center">
		<h1>Quizz Scoreboard</h1>

		<table>
			<tr>
				<td>Rank</td>
				<td>Player Name</td>
				<td>Points</td>
			</tr>
			<%
				for (QuizzPlayer player : (List<QuizzPlayer>) request
						.getAttribute("players")) {
			%>
			<tr>
				<td>${i}</td>
				<td>${player.name}</td>
				<td>${player.points}</td>
			</tr>
			<%
				}
			%>
		</table>
	</div>
</body>
</html>