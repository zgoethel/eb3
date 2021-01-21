<!doctype html>
<html>
    <head>
        <title>Error 500</title>

        <meta name="viewport" content="width=device-width, height=device-height, initial-scale=1.0, minimum-scale=1.0">

        <link rel="stylesheet" type="text/css" href="/style/general.css"/>
        <link rel="stylesheet" type="text/css" href="/style/fonts.css"/>

        <style>
            div.error-log
            {
                text-align: left;

                height: 600px;
                padding: 1em;
                border-radius: 1em;

                background: #2f2231;
            }

            div.error-log, div.contact-note
            {
                display: inline-block;

                white-space: nowrap;
                overflow-x: auto;

                width: 775px;
            }

            div.error-log *
            {
                color: #ffffff;
            }

            div.error-log *, div.contact-note *
            {
                font-family: monospace;
                text-size: 11px;
            }

            h2 img.icon
            {
                width: 30px;
                margin-bottom: -0.23em;
            }

            @media only screen and (max-width: 1000px)
            {
                div.error-log, div.contact-note
                {
                    width: 95%;
                }
            }
        </style>
    </head>

    <body>
        <div class="logo"></div>

        <div style="text-align: center">
            <h2><img class="icon" src="/image/eos-icons/critical_bug.svg"/>500</h2>
            <p>An internal server error has occurred.</p>
            <br/>

            <div class="contact-note">
                <p>Please contact the administrator and include the following error details:</p>
            </div>

            <div class="error-log">
                <div><strong>Error:</strong> ${error}</div>
                <div><strong>Status:</strong> ${status}</div>
                <div><strong>Timestamp:</strong> ${timestamp}</div>
                <br/>
                <!-- Trace is pre-formatted with tabs and newlines -->
                <div><pre>${trace}</pre></div>
            </div>
        </div>
    </body>
</html>