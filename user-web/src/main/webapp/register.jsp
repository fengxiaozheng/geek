<head>
    <jsp:directive.include
            file="/WEB-INF/jsp/prelude/include-head-meta.jspf"/>
    <title>注册</title>

    <style>
        .form-body {
            display: flex;
            align-items: center;
            flex-direction: column;
        }
        .form-div {
            margin: 10px;
        }
    </style>
</head>
<body>
<h3 class="form-body">注册</h3>
<form class="form-body" action="signUp" method="post">
    <div class="form-div">
        用 户 名<input type="text" name="username">
    </div>
    <div class="form-div">
        密 码  <input type="password" name="password">
    </div>
    <div class="form-div">
        邮 箱  <input type="email" name="email">
    </div>
    <div class="form-div">
        手机号码<input type="number" name="phone">
    </div>
    <div class="form-div">
        <input type="submit" value="提交">
    </div>
</form>
</body>