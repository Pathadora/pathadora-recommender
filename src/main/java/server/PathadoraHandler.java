package server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.swrlapi.exceptions.SWRLBuiltInException;
import org.swrlapi.parser.SWRLParseException;
import server.owl.Inserter;
import server.owl.PathadoraManager;
import server.owl.Recommender;

import java.io.*;
import java.util.Map;

import static server.utils.ParserUtils.*;
import static server.utils.PathadoraConfig.OntologyConfig.*;
import static server.utils.PathadoraConfig.ServerConfig.*;

class PathadoraHandler implements HttpHandler {

    private final PathadoraManager pathadoraManager;
    private final Inserter inserter;
    private final Recommender recommender;

    public PathadoraHandler(PathadoraManager pathadoraManager) {
        this.pathadoraManager = pathadoraManager;
        this.inserter = new Inserter(pathadoraManager);
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

                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(STATUS_OK, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
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
                    return  pathadoraManager.addIndividual(inserter, params);

                case FACULTIES_GENERATION:
                    System.out.println("Recommended faculties was requested");
                    recommender.initializeOntologyWithRules(true);
                    return pathadoraManager.recommendFacAndDep(recommender, params);

                case COURSE_GENERATION:
                    System.out.println("Recommended courses was requested");
                    return pathadoraManager.recommendCourses(recommender, params);

                case RESOURCE_GENERATION:
                    System.out.println("Recommended resources was was requested");
                    return pathadoraManager.recommendResources(recommender, params);

                case FINISH:
                    System.out.println("Operation finished");
                    pathadoraManager.reset();
                    return String.valueOf(STATUS_OK);

                default:
                    return "No correct action";
            }
        } else {
            System.out.println("Error on request");
            return String.valueOf(STATUS_ERROR);
        }
    }
}
