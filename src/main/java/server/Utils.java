package server;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static server.Utils.Configuration.CHARSET;

/**
 * Utils class to help on server configuration and handling the requests.
 */

public class Utils {

    static class Configuration {
        protected static final String HOSTNAME = "localhost";
        protected static final int PORT = 8080;
        protected static final int BACKLOG = 1;

        protected static final String HEADER_ALLOW = "Allow";
        protected static final String HEADER_CONTENT_TYPE = "Content-Type";

        protected static final Charset CHARSET = StandardCharsets.UTF_8;

        protected static final int STATUS_OK = 200;
        protected static final int STATUS_METHOD_NOT_ALLOWED = 405;

        protected static final int NO_RESPONSE_LENGTH = -1;

        protected static final String METHOD_GET = "GET";
        protected static final String METHOD_OPTIONS = "OPTIONS";
        protected static final String ALLOWED_METHODS = METHOD_GET + "," + METHOD_OPTIONS;
    }

    /**
     * Extract the parameters from the URL request and return a map.
     */
    protected static Map<String, List<String>> getRequestParameters(final URI requestUri) {
        final Map<String, List<String>> requestParameters = new LinkedHashMap<>();
        final String requestQuery = requestUri.getRawQuery();
        if (requestQuery != null) {
            final String[] rawRequestParameters = requestQuery.split("[&;]", -1);
            for (final String rawRequestParameter : rawRequestParameters) {
                final String[] requestParameter = rawRequestParameter.split("=", 2);
                final String requestParameterName = decodeUrlComponent(requestParameter[0]);
                requestParameters.putIfAbsent(requestParameterName, new ArrayList<>());
                final String requestParameterValue = requestParameter.length > 1 ? decodeUrlComponent(requestParameter[1]) : null;
                requestParameters.get(requestParameterName).add(requestParameterValue);
            }
        }
        return requestParameters;
    }

    /**
     * Basic URL decoder
     */
    private static String decodeUrlComponent(final String urlComponent) {
        try {
            return URLDecoder.decode(urlComponent, CHARSET.name());
        } catch (final UnsupportedEncodingException ex) {
            throw new InternalError(ex);
        }
    }
}
