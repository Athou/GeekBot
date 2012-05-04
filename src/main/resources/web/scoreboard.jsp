<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page
	import="java.util.List,be.hehehe.geekbot.persistence.model.QuizzPlayer,be.hehehe.geekbot.persistence.model.QuizzMergeRequest"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Quizz Scoreboard</title>
<link rel="stylesheet" href="css/style.css" type="text/css" />

<script type="text/javascript"
	src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
<script type="text/javascript" src="script/requests.js"></script>
</head>
<body>
	<div class="center">
		<h1 class="title">Quizz Scoreboard</h1>

		<table class="center players">
			<tr>
				<td>Rank</td>
				<td>Player Name</td>
				<td>Points</td>
			</tr>
			<%
				List<QuizzPlayer> players = (List<QuizzPlayer>) request
						.getAttribute("players");
				for (int i = 0; i < players.size(); i++) {
					QuizzPlayer player = players.get(i);
			%>
			<tr>
				<td><%=(i + 1)%></td>
				<td><%=player.getName()%></td>
				<td><%=player.getPoints()%></td>
			</tr>
			<%
				}
			%>
		</table>

		<%
			List<QuizzMergeRequest> requests = (List<QuizzMergeRequest>) request
					.getAttribute("requests");
			if (!requests.isEmpty()) {
		%>
		<h1 class="title">Merge Requests</h1>
		<form action="quizzmerge" method="post">
			<table class="center requests">
				<tr>
					<td>Receiving Player</td>
					<td>Giving Player</td>
					<td><img src="images/accept.png" /></td>
					<td><img src="images/cross.png" /></td>
				</tr>
				<%
					for (int i = 0; i < requests.size(); i++) {
							QuizzMergeRequest req = requests.get(i);
				%>
				<tr>
					<td><%=req.getPlayer1().getName()%></td>
					<td><%=req.getPlayer2().getName()%></td>
					<td><input type="checkbox" name="accept"
						value="<%=req.getId()%>" /></td>
					<td><input type="checkbox" name="deny"
						value="<%=req.getId()%>" /></td>

				</tr>
				<%
					}
				%>
				<tr>
					<td>Password:</td>
					<td><input id="password" type="password" /></td>
				</tr>

			</table>
			<input type="submit" value="Submit" />
		</form>
		<%
			}
		%>
	</div>
</body>
</html>