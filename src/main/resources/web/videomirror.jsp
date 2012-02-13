<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Mirror of ${url}</title>
<link rel="stylesheet" href="css/style.css" type="text/css" />
</head>
<body>
	<div class="center">
		<h1>
			Mirror for <a href="${url}">${url}</a>
		</h1>
		<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"
			width="470" height="320" id="single1" name="single1">
			<param name="movie" value="jwplayer/player.swf" />
			<param name="allowfullscreen" value="true" />
			<param name="allowscriptaccess" value="always" />
			<param name="wmode" value="transparent" />
			<param name="flashvars"
				value="file=${video}&amp;skin=jwplayer/modieus.swf&amp;autostart=true" />
			<embed type="application/x-shockwave-flash" id="single2"
				name="single2" src="jwplayer/player.swf" width="470" height="320"
				bgcolor="undefined" allowscriptaccess="always"
				allowfullscreen="true" wmode="transparent"
				flashvars="file=${video}&amp;skin=jwplayer/modieus.swf&amp;autostart=true" />
		</object>
	</div>
</body>
</html>