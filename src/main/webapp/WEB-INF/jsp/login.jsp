<!doctype html>
<html>
    <head>
        <title>Login</title>

        <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

        <meta name="viewport" content="width=device-width, height=device-height, initial-scale=1.0, minimum-scale=1.0">

        <link rel="stylesheet" type="text/css" href="/style/general.css?"/>
        <link rel="stylesheet" type="text/css" href="/style/fonts.css?"/>

        <style>
            input
            {
                margin-top: 0.5em;
                margin-left: -125px;

                width: 250px;
            }

            div.login-wrapper
            {
                margin-top: 2em;

                position: absolute;
                left: 50%;

                width: 0;
            }

            div.login-wrapper div.error
            {
                width: 250px;
                margin-left: -125px;
                border-radius: 0.5em;
                padding: 0.5em;

                background: #f77777;
                color: #ffffff;
            }

            h2 img.icon
            {
                width: 30px;
                margin-bottom: -0.23em;
            }
        </style>
    </head>
    
    <body class="centered-ribbon">
        <div class="centered-ribbon">
            <div class="logo"></div>

            <div style="text-align: center">
                <h2><img class="icon" src="/image/eos-icons/account_circle.svg"/>Login</h2>
                <p>Please log into your account.</p>
            </div>

            <div class="login-wrapper">
                <c:if test="${not empty error}">
                    <div class="error">${error}</div>
                </c:if>

                <form action="/login" method="post">
                    <input type="text"
                        name="username"
                        placeholder="Username"
                        autofill="username" />

                    <input type="password"
                        name="password"
                        placeholder="Password"
                        autofill="password" />

                    <input style="display: none"
                        type="text"
                        name="successRedirect"
                        value="${redirect}" />

                    <input type="submit" value="Log in" />
                </form>
            </div>
        </div>
    </body>
</html>