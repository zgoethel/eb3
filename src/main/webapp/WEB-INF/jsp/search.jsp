<!doctype html>
<html>
    <head>
        <title>Search</title>

        <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

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
                box-shadow: inset 0 0.15em 0.2em grey;
                width: 100%;
                padding: 0.5em;
                padding-top: 1em;
                border-radius: 0.12em;
                overflow: hidden;
                overflow-x: auto;
                //border: 1px solid #734272;
                background: #e0dce6;
            }

            table
            {
                box-shadow: 0 0 4px black;
                border-collapse: collapse;
                //width: 100%;
                background: #ffffff;
            }

            tbody *
            {
                font-family: monospace;
                font-size: 1.05em;
                color: #000000;
            }

            table *
            {
                margin: 0;
                padding: 0;
            }

            tr:nth-child(even)
            {
                background: #fef7ff;
            }

            th
            {
                font-size: 0.8em;
                padding: 0.5em;
                background: #3a003e;
                color: #ffffff;
                filter: drop-shadow(0 0 2px purple);
                max-width: 70px;

                min-height: 52px;
                box-sizing: border-box;
            }

            td
            {
                border-bottom: 1px solid #c3cfe6ad;
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

            div#paging .button
            {
                margin-top: 1em;
                background: purple;
                filter: drop-shadow(0 10px 0.5rem lightgrey);
            }

            div#paging
            {
                position: absolute;
                width: 50%;
                left: 25%;
                margin-top: 2em;
                padding-bottom: 2em;
                text-align: center;
            }

            div#search-container
            {
                float: right;
                margin-bottom: 1em;
                margin-top: -2em;
                margin-right: 2em;
            }

            .search-button
            {
                transition: 0.2s;
                background-size: contain;
                width: 3em;
                margin-left: 0.3em;
                border: 1px solid black;
                border-radius: 0.2em;
                margin-bottom: -1.2em;
            }

            #search-submit
            {
                background: url("/image/eos-icons/search.svg");
            }

            #search-menu
            {
                background: url("/image/eos-icons/menu.svg");
                border: 1px solid #62006a;
                cursor: pointer;
            }

            div#search-stuff
            {
                float: right;
                position: absolute;
                right: 2em;
                background: white;
                padding: 1em;
                margin-top: 0;
                box-shadow: 0 0 2px black;
                border-radius: 2px;
                width: 24em;
                z-index: 1000;
            }

            span#search-stuff-container div#search-stuff
            {
                display: none;
            }

            span#search-stuff-container:hover div#search-stuff
            {
                display: block;
            }

            span#search-stuff-container:hover .search-button
            {
                border-radius: 0.2em 0.2em 0 0;
                border-bottom: none;
            }

            span#search-stuff-container div#search-stuff input
            {
                width: 120px;
            }

            span#search-stuff-container div#search-stuff select
            {
                width: 100px;
                padding: 1em 0;
            }

            @media only screen and (max-width: 680px)
            {
                div#paging
                {
                    width: 100%;
                    left: 0;
                }

                div#paging *:nth-child(2)
                {
                    display: none;
                }
            }

            @media only screen and (max-width: 540px)
            {
                div#search-container
                {
                    float: none;
                    margin-top: 0;
                    margin-left: 1em;
                }

                div#right-head-buttons
                {
                    display: none;
                }

                img#download-icon
                {
                    display: none;
                }
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

        <c:url value="/report/ExportWorkbook" var="reportURL">
            <c:forEach var="entry" items="${args}">
                <c:if test="${entry.key != 'top' && entry.key != 'skip'}">
                    <c:param name="${entry.key}" value="${entry.value[0]}" />
                </c:if>
            </c:forEach>
        </c:url>

        <h2>
            <c:out value="${descriptor.getTitle()}" />
            <a href="${reportURL}" target="_blank">
                <img id="download-icon" src="/image/eos-icons/download.svg" title="Download as XLSX" />
            </a>
        </h2>

        <div id="search-container">
            <form action="/s" method="GET" autocomplete="off">
                <input type="text" style="display: none;" name="document" value="${descriptor.getName()}" />
                <input type="text" style="display: none;" name="top" value="${top}" />
                <input type="text" name="search" value="${search}" placeholder="Quick search" />
                <input type="submit" class="search-button" id="search-submit" value="" />

                <span id="search-stuff-container">
                    <input type="button" class="search-button" id="search-menu">

                    <div id="search-stuff"><span style="text-decoration: underline;">Granular search</span>
                    <br />
                    <i>Supports wildcards via <a href="https://regexr.com/" target="_blank">regex</a> search.</i>
                    <br />
                    <br />

                    <span style="font-family: monospace; text-decoration: bold;">Comp. A&nbsp;</span>
                    <select name="_comp_a_method" id="_comp_a_method">
                        <option value="Contains" selected>Contains</option>
                        <option value="Matches">Matches</option>
                        <option value=">=">&gt;=</option>
                        <option value="<=">&lt;=</option>
                    </select>
                    <script>document.getElementById('_comp_a_method').value = '${args.get("_comp_a_method")[0]}';</script>
                    <input type="text" name="_comp_a_regex" value="${args.get("_comp_a_regex")[0]}" />
                    <br />

                    <span style="font-family: monospace; text-decoration: bold;">Comp. B&nbsp;</span>
                    <select name="_comp_b_method" id="_comp_b_method">
                        <option value="Contains" selected>Contains</option>
                        <option value="Matches">Matches</option>
                        <option value=">=">&gt;=</option>
                        <option value="<=">&lt;=</option>
                    </select>
                    <script>document.getElementById('_comp_b_method').value = '${args.get("_comp_b_method")[0]}';</script>
                    <input type="text" name="_comp_b_regex" value="${args.get("_comp_b_regex")[0]}" />
                    <br />

                    <br />
                    <hr />
                    <input type="submit" value="Search" /></div>
                </span>
            </form>
        </div>

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

                            <c:url value="/report/ExportWorkbook" var="rowReportURL">
                                <c:param name="top" value="1" />
                                <c:param name="document" value="${args.get('document')[0]}" />
                                <c:param name="_file_name_method" value="Matches" />
                                <c:param name="_file_name_regex" value="${doc.getString('file_name')}" />
                            </c:url>

                            <td><span>
                                <a href="${rowReportURL}" target="_blank">
                                    <img id="download-icon" src="/image/eos-icons/download.svg" title="Download row as XLSX" />
                                </a>
                            </span></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>

        <c:url value="/s" var="pagingURL">
            <c:forEach var="entry" items="${args}">
                <c:if test="${entry.key != 'skip'}">
                    <c:param name="${entry.key}" value="${entry.value[0]}" />
                </c:if>
            </c:forEach>
        </c:url>

        <div id="paging">
            <c:choose>
                <c:when test="${skip - top >= 0}">
                    <a href="${pagingURL}&skip=${skip - top}" class="button">Prev.</a>
                </c:when>
                <c:otherwise>
                    <span style="background: grey;" class="button">Prev.</span>
                </c:otherwise>
            </c:choose>

            <span style="padding: 2em;">
                Page <strong>${Integer.valueOf(skip / top) + 1}</strong>
                (${Math.min(size, Integer.valueOf(skip + 1))} - ${Math.min(size, Integer.valueOf(skip + top))}
                of ${size})
            </span>

            <c:choose>
                <c:when test="${skip + top < size}">
                    <a href="${pagingURL}&skip=${skip + top}" class="button">Next</a>
                </c:when>
                <c:otherwise>
                    <span style="background: grey;" class="button">Next</span>
                </c:otherwise>
            </c:choose>
        </div>
    </body>
</html>