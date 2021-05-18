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

            img#download-icon
            {
                width: 1em;
                margin-bottom: -0.23em;
                margin-left: 0.5em;
                cursor: pointer;
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

            div#table-container
            {
                width: 99%;
                margin-left: 0.5%;
                border-radius: 0.25em;
                overflow: hidden;
                overflow-x: auto;
                border: 1px solid #734272;
                background: #ffffff;
            }

            table
            {
                border-collapse: collapse;
                width: 100%;
            }

            tbody *
            {
                font-family: "Times New Roman", Times, serif;
                font-size: 0.95em;
                color: #7b4182;
            }

            table *
            {
                margin: 0;
                padding: 0;
            }

            tr:nth-child(even)
            {
                background: #efe9ff;
            }

            th
            {
                font-size: 0.8em;
                padding: 0.5em;
                background: #590069;
                color: #ffffff;
            }

            td
            {
                border-bottom: 1px solid #d4a5dcad;
                padding: 0.5em;
            }

            td span
            {
                max-width: 400px;
                display: inline-block;
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;
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

        <h2>
            <c:out value="${descriptor.getTitle()}" />
            <img id="download-icon" src="/image/eos-icons/download.svg" title="Download as XLSX" />
        </h2>

        <div id="table-container">
            <table>
                <thead>
                    <c:forEach var="f" items="${descriptor.getDefaultDisplayFields()}">
                        <th><c:out value="${descriptor.getFields().get(f).getTitle()}" /></th>
                    </c:forEach>
                </thead>

                <tbody>
                    <c:forEach var="doc" items="${repo}">
                        <tr>
                            <c:forEach var="f" items="${descriptor.getDefaultDisplayFields()}">
                                <td><span><c:out value="${doc.getString(f)}" /></span></td>
                            </c:forEach>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </body>
</html>