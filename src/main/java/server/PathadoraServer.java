package server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

import static server.Utils.Configuration.*;
import static server.Utils.*;

/**
 * Pathadora server with be used to handle the request for a path generation.
 * The main purpose will be to keep updating the ontology continuously based on
 * learner input or requests.
 * By the moment the server is listening to port 8080 and serving request for /pathadora.
 * TODO:
 *  - extract update learner data from the request and update knowledge
 */

public class PathadoraServer {

    public static void main(final String... args) throws IOException {
        final HttpServer server = HttpServer.create(new InetSocketAddress(HOSTNAME, PORT), BACKLOG);
        server.createContext("/pathadora", handler -> {
            try {
                final Headers headers = handler.getResponseHeaders();
                final String requestMethod = handler.getRequestMethod().toUpperCase();
                switch (requestMethod) {
                    case METHOD_GET:
                        final Map<String, List<String>> requestParameters = getRequestParameters(handler.getRequestURI());
                        String responseBody = computeResponse(requestParameters);
                        headers.set(HEADER_CONTENT_TYPE, String.format("application/json; charset=%s", CHARSET));

                        final byte[] rawResponseBody = responseBody.getBytes(CHARSET);
                        handler.sendResponseHeaders(STATUS_OK, rawResponseBody.length);
                        handler.getResponseBody().write(rawResponseBody);
                        break;
                    case METHOD_OPTIONS:
                        headers.set(HEADER_ALLOW, ALLOWED_METHODS);
                        handler.sendResponseHeaders(STATUS_OK, NO_RESPONSE_LENGTH);
                        break;
                    default:
                        headers.set(HEADER_ALLOW, ALLOWED_METHODS);
                        handler.sendResponseHeaders(STATUS_METHOD_NOT_ALLOWED, NO_RESPONSE_LENGTH);
                        break;
                }
            } finally {
                handler.close();
            }
        });
        server.start();
    }

    private static String computeResponse(Map<String, List<String>> requestParameters) {
        System.out.println("Parameters \n"+ requestParameters.toString());
        return "Parameters received, this is the response";
    }
}