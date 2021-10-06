import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import owl.OntologyLoader;


public class Main {

    public static void main(String... args) throws OWLOntologyCreationException, OWLOntologyStorageException {
        new OntologyLoader().dataLoader();
    }
}
