package net.jibini.eb.epicor;

import net.jibini.eb.impl.EasyButtonContextImpl;
import net.jibini.eb.auth.AuthDetails;
import net.jibini.eb.auth.Authenticator;

import org.json.JSONException;
import org.json.JSONObject;

import org.springframework.http.HttpStatus;

import javax.net.ssl.HttpsURLConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This is an implementation class for data sources which require access to the
 * Epicor backend. It allows invocation of Epicor API v1 services and
 * validation of their results. The base server address is determined by the
 * configuration file for the Epicor implementation extension.
 *
 * API calls will be consumed as JSON; any malformed results or error results
 * will be parsed and rethrown as an {@link EpicorException} detailing what
 * went wrong. In most cases, the error will be a malformed request or filter.
 *
 * @author Zach Goethel
 */
@Authenticator
public class EpicorCall
{
    private final String path;

    /**
     * Constructs a new Epicor call for a specific service.
     *
     * @param basePath Base Epicor API v1 server path.
     * @param service Specific API path for this service.
     */
    public EpicorCall(String basePath, String service)
    {
        this.path = String.format("%s/%s", basePath, service);
    }

    /**
     * Constructs a new Epicor call for a specific service. The base server path
     * is loaded from configuration.
     *
     * @param service Specific API path for this service.
     */
    public EpicorCall(String service)
    {
        this(
            Objects.requireNonNull(EasyButtonContextImpl.getBean(Epicor.class))
                .config
                .getBaseServerPath(),
            service
        );
    }

    /**
     * Invokes the API service using the provided parameters and authentication
     * details. Results are parsed and returned as a {@link JSONObject}. For
     * properly formatted successful results, listed entries will be in an array
     * called 'value' in the root of the returned JSON.
     *
     * @param auth Authentication details with which to connect to Epicor.
     * @param args Request parameters for the API call.
     * @return API results interpreted as a {@link JSONObject}. Resulting entries
     *      are stored in an array called 'value' in the JSON result's root.
     * @throws EpicorException If results are malformed or the backend server
     *      returns a status other than 200 (Okay). Any malformed results or error
     *      results will be parsed and rethrown as an {@link EpicorException}
     *      detailing what went wrong. In most cases, the error will be a
     *      malformed request or filter.
     */
    public JSONObject call(AuthDetails auth, Map<String, String> args)
    {
        // Build a request path with provided parameters
        StringBuilder url = new StringBuilder(String.format("%s?", path));

        args.forEach((key, value) -> url
                .append(key)
                .append('=')
                .append(URLEncoder.encode(value, Charset.defaultCharset()))
                .append('&'));

        HttpsURLConnection connection;

        try
        {
            // Open a connection to the backend server
            connection = (HttpsURLConnection)(new URL(url.toString()).openConnection());
        } catch (MalformedURLException ex)
        {
            throw new EpicorException("The Epicor request URL was malformed", ex);
        } catch (IOException ex)
        {
            throw new EpicorException("A communication error occurred while connecting", ex);
        }

        // Set the authorization and format headers
        connection.setRequestProperty("Authorization", auth.getBasicAuth());
        connection.setRequestProperty("Accept", "application/json");

        try
        {
            // Read the server's response
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String content = reader
                    .lines()
                    .collect(Collectors.joining("\n"));

            reader.close();

            try
            {
                return new JSONObject(content);
            } catch (JSONException ex)
            {
                throw new EpicorException("The resulting content was malformed JSON", ex);
            }
        } catch (IOException ex)
        {
            try
            {
                InputStream input = connection.getErrorStream();
                if (input == null)
                    throw new EpicorException("Connection to Epicor backend failed; check address");

                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                String content = reader
                        .lines()
                        .collect(Collectors.joining("\n"));

                // Check that the response was not an error
                int response;
                if ((response = connection.getResponseCode()) != HttpStatus.OK.value())
                    throw new EpicorException(
                            String.format("Epicor responded with non-200 status (%d)", response),
                            constructException(content)
                    );
                else
                    throw new EpicorException("An error occurred while communicating", ex);
            } catch (IOException unRecovered)
            {
                throw new EpicorException("An error occurred while communicating", unRecovered);
            }
        }
    }

    /**
     * @param content Error message or JSON-encoded error information. Some error
     *      responses will be formatted in plain HTML.
     * @return An exception detailing what went wrong according the the provided
     *      server response.
     */
    private EpicorException constructException(String content)
    {
        switch (content.charAt(0))
        {
            case '{':
                try
                {
                    // Parse the content as JSON
                    JSONObject document = new JSONObject(content);

                    // Retrieve field data
                    int httpStatus = document.getInt("HttpStatus");

                    String exception = document.getString("ErrorType");
                    String reason = document.getString("ReasonPhrase");
                    String errorMessage = document.getString("ErrorMessage");

                    // Format into a useful error message
                    return new EpicorException(String.format(
                            "%s (%s, %d) - %s",
                            exception,
                            reason,
                            httpStatus,
                            errorMessage
                    ));
                } catch (JSONException ex)
                {
                    return new EpicorException("Could not construct response exception", ex);
                }

            case '<':
                // Parses the HTML page's title. Originally, this used XML document
                // parsing; due to a native implementation bug, that would hang on a
                // native method 'socketRead0'. This is a simple/naive workaround.
                return new EpicorException(content.split("<[/]*title>", 3)[1]);

            default:
                // The error message is likely plain text
                return new EpicorException(content);
        }
    }
}
