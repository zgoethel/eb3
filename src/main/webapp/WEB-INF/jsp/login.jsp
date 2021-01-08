<!doctype html>
<html>
    <head>
        <title>Login</title>

        <meta name="viewport" content="width=device-width, height=device-height, initial-scale=1.0, minimum-scale=1.0">

        <link rel="stylesheet" type="text/css" href="/style/general.css"/>
        <link rel="stylesheet" type="text/css" href="/style/fonts.css"/>
    </head>
    
    <body>
        <div class="logo"></div>

        <form action="/login" method="post">
            <input type="text" name="username" />
            <input type="password" name="password" />

            <input type="text" name="successRedirect" value="${redirect}" />

            <input type="submit" />
        </form>
    </body>
</html>