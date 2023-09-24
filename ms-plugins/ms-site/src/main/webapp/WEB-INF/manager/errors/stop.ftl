<!DOCTYPE html>
<html class="no-js" lang="pt-br">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>${err}</title>
<style type="text/css">
* {
	margin: 0;
	padding: 0;
}

a {
	color: #707070;
	text-decoration: none;
}

body {
	background-color: #E4E7EA
}

#container {
	padding: 93px 0 0 0;
}

.stop-code {
	width: 100%;
	height: 420px;
}

.stop-pic {
	background: url('http://cdn.mingsoft.net/global/images/stop.png') no-repeat center top;
}

.stop-text {
	font-size: 24px;
	color: #656565;
	text-align: center;
	text-transform: uppercase;
	font-family: 'Dosis', sans-serif;
	margin-top: -90px;
}
</style>
</head>

<body>
	<div id="container">
		<p class="ir stop-code stop-pic"></p>
		<div class="stop-text">
			<h3
				style="clear: both; line-height: 40px; font-size: 40px; color: #333;">服务请求错误！</h3>
			<p
				style="clear: both; margin: 30px auto 20px auto; line-height: 35px; font-size: 20px; color: #999;">${err}</p>
		</div>
	</div>
</body>
</html>
