package server.utils;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static server.utils.PathadoraUtils.ServerConfig.CHARSET;

/**
 * Utils class to help on server configuration and handling the requests.
 */

public class PathadoraUtils {
    
    public static class OntologyConfig {
        public final static String PATHADORA_LOCAL_PATH = "./src/main/resources/ont/pathadora-ontology.owl";
        public final static String LOM_LOCAL_PATH = "./src/main/resources/ont/lom_ontology_ods.owl";
        public final static String ACC_LOCAL_PATH = "./src/main/resources/ont/AccessibleOCW.owl";

        public final static String LOM_RESOURCE = "http://data.opendiscoveryspace.eu/lom_ontology_ods.owl";
        public final static String ACC_RESOURCE = "http://purl.org/accessible_ocw_ontology";
    }

    public static class ServerConfig {
        public static final String HOSTNAME = "localhost";
        public static final int PORT = 8080;
        public static final int BACKLOG = 1;

        public static final String HEADER_ALLOW = "Allow";
        public static final String HEADER_CONTENT_TYPE = "Content-Type";

        public static final Charset CHARSET = StandardCharsets.UTF_8;

        public static final int STATUS_OK = 200;
        public static final int STATUS_METHOD_NOT_ALLOWED = 405;

        public static final int NO_RESPONSE_LENGTH = -1;

        public static final String METHOD_GET = "GET";
        public static final String METHOD_OPTIONS = "OPTIONS";
        public static final String ALLOWED_METHODS = METHOD_GET + "," + METHOD_OPTIONS;
    }

    /**
     * Extract the parameters from the URL request and return a map.
     */
    public static Parameters getRequestParameters(final URI requestUri) {
        final Map<String, String> requestParameters = new LinkedHashMap<>();
        final String requestQuery = requestUri.getRawQuery();
        if (requestQuery != null) {
            final String[] rawRequestParameters = requestQuery.split("[&;]", -1);
            for (final String rawRequestParameter : rawRequestParameters) {
                final String[] requestParameter = rawRequestParameter.split("=", 2);
                requestParameters.putIfAbsent(decodeUrlComponent(requestParameter[0]), decodeUrlComponent(requestParameter[1]));
            }
        }
        return new Parameters(requestParameters);
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
