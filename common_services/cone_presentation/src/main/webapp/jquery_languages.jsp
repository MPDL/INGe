<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<script src="./js/jquery-1.2.6.min.js" type="text/javascript"></script>
		<script src="./js/jquery.suggest.js" type="text/javascript"></script>
		<script src="./js/jquery.dimensions.js" type="text/javascript"></script>
		<link href="./js/jquery.suggest.css" rel="stylesheet" type="text/css" />
		<title>CONE 4 JQuery</title>
	</head>
	<body id="body" lang="en">
		<h1>CONE 4 JQuery</h1>
		<form name="form1" method="get">
			Enter query string:
			<input class="languageSuggest" id="test1" size="100"/>
			<select id="lang" onchange="document.getElementById('body').lang = this.options[this.selectedIndex].value">
				<option value="en">Default (English)</option>
				<option value="en">English</option>
				<option value="de">Deutsch</option>
				<option value="fr">Français</option>
			</select>
			<script type="text/javascript">
				$(".languageSuggest").suggest("jquery/lang/query",{ onSelect: function() {alert("You selected: " + this.getResultID())}});
			</script>
		</form>
	</body>
</html>