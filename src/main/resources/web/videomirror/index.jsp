<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Mirror of ${requestParam.url}</title>
<link rel="stylesheet" href="style.css" type="text/css" />
</head>
<body>
	<div class="center">
		<h1>Mirror for ${requestParam.url}</h1>
		<object classid="clsid:d27cdb6e-ae6d-11cf-96b8-444553540000"
			width="470" height="320"
			codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,40,0">
			<param name="bgcolor" value="undefined" />
			<param name="flashvars"
				value="file=videostream.mp4&amp;skin=modieus.swf&amp;autostart=true" />
			<param name="src" value="player.swf" />
			<param name="allowfullscreen" value="true" />
			<embed type="application/x-shockwave-flash" width="470" height="320"
				src="player.swf" allowfullscreen="true"
				flashvars="file=videostream.mp4&amp;skin=modieus.swf&amp;autostart=true"
				bgcolor="undefined"> </embed>
		</object>
	</div>
</body>
</html>