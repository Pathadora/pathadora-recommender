package server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import server.owl.PathadoraManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.Map;

import static server.utils.ParserUtils.*;
import static server.utils.PathadoraConfig.OntologyConfig.*;
import static server.utils.PathadoraConfig.ServerConfig.*;

/**
 * Pathadora server with be used to handle the request for a path generation.
 * The main purpose will be to keep updating the ontology continuously based on
 * learner input or requests.
 * By the moment the server is listening to port 8080 and serving request for /pathadora.
 */
public class PathadoraServer {

    public static void initializeAndStart() throws IOException, OWLOntologyCreationException, OWLOntologyStorageException {
        final HttpServer server = HttpServer.create(new InetSocketAddress(HOSTNAME, PORT), BACKLOG);
        final PathadoraManager pathadoraManager = new PathadoraManager();

        server.createContext("/pathadora", new PathadoraHandler(pathadoraManager));
        server.start();
    }
}


class PathadoraHandler implements HttpHandler {

    private final PathadoraManager pathadoraManager;

    public PathadoraHandler(PathadoraManager pathadoraManager) {
        this.pathadoraManager = pathadoraManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            final Headers headers = exchange.getResponseHeaders();
            final String requestMethod = exchange.getRequestMethod().toUpperCase();
            switch (requestMethod) {

                case METHOD_GET:
                    /*final Parameters requestParameters = getRequestParameters(exchange.getRequestURI());
                    String responseBody = computeRequest(requestParameters);
                    headers.set(HEADER_CONTENT_TYPE, String.format("application/json; charset=%s", CHARSET));

                    final byte[] rawResponseBody = responseBody.getBytes(CHARSET);
                    exchange.sendResponseHeaders(STATUS_OK, rawResponseBody.length);
                    exchange.getResponseBody().write(rawResponseBody);*/
                    break;

                case METHOD_POST:
                    System.out.println("Entered");
                    String parameters = paramsToString(new BufferedReader(new InputStreamReader(exchange.getRequestBody())));
                    computeRequest(parameters);

                    final byte[] rawResponseBody = "My response".getBytes(CHARSET);
                    exchange.sendResponseHeaders(STATUS_OK, rawResponseBody.length);
                    exchange.getResponseBody().write(rawResponseBody);
                    break;

                case METHOD_OPTIONS:
                    headers.set(HEADER_ALLOW, ALLOWED_METHODS);
                    exchange.sendResponseHeaders(STATUS_OK, NO_RESPONSE_LENGTH);
                    break;

                default:
                    headers.set(HEADER_ALLOW, ALLOWED_METHODS);
                    exchange.sendResponseHeaders(STATUS_METHOD_NOT_ALLOWED, NO_RESPONSE_LENGTH);
                    break;
            }
        } catch (OWLOntologyCreationException | OWLOntologyStorageException e) {
            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }

    private String computeRequest(String paramsString) throws IOException, OWLOntologyCreationException, OWLOntologyStorageException {
        Map<String, String> params = paramsToMap(paramsString);

        if (params.containsKey(ACTION)) {
            switch (params.get(ACTION)) {
                case ADD:
                    System.out.println("Add individual was requested");
                    pathadoraManager.addIndividual(params);
                    break;
                case NEXT_STEP:
                    System.out.println("Next step was requested");
                    break;
                case PATH_GENERATION:
                    System.out.println("Path generation was requested");
                    break;
                default:
                    break;
            }
        }
        return "This is my response";
    }
}
