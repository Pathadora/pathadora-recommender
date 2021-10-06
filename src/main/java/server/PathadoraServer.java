package server;

import com.sun.net.httpserver.HttpServer;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import server.owl.OntologyLoader;

import java.io.IOException;
import java.net.InetSocketAddress;

import static server.utils.PathadoraUtils.ServerConfig.*;

/**
 * Pathadora server with be used to handle the request for a path generation.
 * The main purpose will be to keep updating the ontology continuously based on
 * learner input or requests.
 * By the moment the server is listening to port 8080 and serving request for /pathadora.
 * TODO:
 *  - extract update learner data from the request and update knowledge
 */

public class PathadoraServer {

    public static void initializeAndStart() throws IOException, OWLOntologyCreationException {
        final HttpServer server = HttpServer.create(new InetSocketAddress(HOSTNAME, PORT), BACKLOG);
        final OntologyLoader ontLoader = new OntologyLoader();

        /* TODO: other handlers to be added */
        server.createContext("/pathadora", new LearnerHandler());

        server.start();
    }
}