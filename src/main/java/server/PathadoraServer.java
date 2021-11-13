package server;

import com.sun.net.httpserver.HttpServer;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import server.owl.PathadoraManager;
import java.io.*;
import java.net.InetSocketAddress;
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


