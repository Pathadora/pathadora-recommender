import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.swrlapi.exceptions.SWRLBuiltInException;
import org.swrlapi.parser.SWRLParseException;
import server.PathadoraServer;

import java.io.IOException;

public class PathadoraRecommender {

    public static void main(String... args) throws OWLOntologyCreationException,
            OWLOntologyStorageException, IOException, SWRLParseException, SWRLBuiltInException {
        System.out.println("Pathadora Recommender Engine is listening at http://localhost:8080/pathadora");
        System.out.println("*******************************************************\n");

        PathadoraServer.initializeAndStart();
    }
}


