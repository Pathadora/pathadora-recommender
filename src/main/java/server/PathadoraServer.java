package server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.swrlapi.exceptions.SWRLBuiltInException;
import org.swrlapi.parser.SWRLParseException;
import server.owl.Inserter;
import server.owl.PathadoraManager;
import server.owl.Recommender;
import server.stardog.StardogDatabase;

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

    public static void initializeAndStart() throws IOException,
            OWLOntologyCreationException, OWLOntologyStorageException, SWRLParseException, SWRLBuiltInException {
        final HttpServer server = HttpServer.create(new InetSocketAddress(HOSTNAME, PORT), BACKLOG);
        final PathadoraManager pathadoraManager = new PathadoraManager();
        final StardogDatabase database = new StardogDatabase();

        server.createContext("/pathadora", new PathadoraHandler(pathadoraManager, database));
        server.start();
    }
}


class PathadoraHandler implements HttpHandler {

    private final PathadoraManager pathadoraManager;
    private final Inserter inserter;
    private final Recommender recommender;

    public PathadoraHandler(PathadoraManager pathadoraManager, StardogDatabase db) throws SWRLParseException, OWLOntologyCreationException, SWRLBuiltInException, OWLOntologyStorageException {
        this.pathadoraManager = pathadoraManager;
        this.inserter = new Inserter(pathadoraManager, db);
        this.recommender = new Recommender(pathadoraManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            final Headers headers = setRequestHeaders(exchange);
            final String requestMethod = exchange.getRequestMethod().toUpperCase();
            switch (requestMethod) {

                case METHOD_GET:
                    break;

                case METHOD_POST:
                    String parameters = paramsToString(new BufferedReader(new InputStreamReader(exchange.getRequestBody())));
                    String response = computeRequest(parameters);

                    final byte[] rawResponseBody = response.getBytes(CHARSET);
                    exchange.sendResponseHeaders(STATUS_OK, rawResponseBody.length);
                    exchange.getResponseBody().write(rawResponseBody);
                    break;

                default:
                    headers.set(HEADER_ALLOW, ALLOWED_METHODS);
                    exchange.sendResponseHeaders(STATUS_METHOD_NOT_ALLOWED, NO_RESPONSE_LENGTH);
                    break;
            }
        } catch (OWLOntologyCreationException | OWLOntologyStorageException | SWRLParseException | SWRLBuiltInException e) {
            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }

    private String computeRequest(String paramsString) throws IOException,
            OWLOntologyCreationException, OWLOntologyStorageException, SWRLParseException, SWRLBuiltInException {
        Map<String, String> params = paramsToMap(paramsString);

        if (params.containsKey(ACTION)) {
            switch (params.get(ACTION)) {
                case ADD:
                    System.out.println("Add individual was requested");
                    return pathadoraManager.addIndividual(inserter, params);
                case FAC_DEP_GENERATION:
                    System.out.println("FAC_DEP_GENERATION was requested");
                    return pathadoraManager.recommendFacAndDep(recommender, params);
                case COURSE_GENERATION:
                    System.out.println("COURSE_GENERATION was requested");
                    //return pathadoraManager.recommendCourses(recommender, params);
                    return "To be refactored with Stardog";
                case RESOURCE_GENERATION:
                    System.out.println("RESOURCE_GENERATION was requested");
                    pathadoraManager.recommendResources(params);
                    return "This is my response";
                default:
                    return "This is my response";
            }
        }
        else {
            System.out.println("Error on request");
            return String.valueOf(STATUS_ERROR);
        }
    }
}
