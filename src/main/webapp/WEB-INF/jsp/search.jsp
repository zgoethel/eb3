<!doctype html>
<html>
    <head>
        <title>Search</title>

        <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

        <meta name="viewport" content="width=device-width, height=device-height, initial-scale=1.0, minimum-scale=1.0">

        <link rel="stylesheet" type="text/css" href="/style/general.css?"/>
        <link rel="stylesheet" type="text/css" href="/style/fonts.css?"/>

        <style>
            div.logo
            {
                margin: 1em;
                width: 17em;
            }

            img#user-icon
            {
                margin-bottom: -0.6em;
                width: 2em;
            }

            span#logout-button
            {
                padding: 0.5em;
                margin-left: 0.5em;
                background: #720679;
                color: #ffffff;
                border-radius: 0.5em;
                cursor: pointer;
            }

            div#right-head-buttons
            {
                position: absolute;
                top: 1.8em;
                right: 1em;
            }

            h2
            {
                margin-left: 1em;
            }
        </style>
    </head>
    
    <body>
        <div class="logo"></div>

        <div id="right-head-buttons">
            <img id="user-icon" src="/image/eos-icons/account_circle.svg" />
            <span><c:out value="${username}" /></span>
            <a href="/logout"><span id="logout-button">Logout</span></a>
        </div>

        <h2><c:out value="${descriptor.getTitle()}" /></h2>
    </body>
</html>