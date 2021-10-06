import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import server.PathadoraServer;

import java.io.IOException;

public class PathadoraRecommender {

    public static void main(String... args) throws OWLOntologyCreationException, OWLOntologyStorageException, IOException {
       PathadoraServer.initializeAndStart();
    }

}


