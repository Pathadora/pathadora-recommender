package server.owl;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.SimpleIRIMapper;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static server.utils.PathadoraConfig.OntologyConfig.*;

public class PathadoraManager {

    private OWLOntologyManager manager;
    private OWLOntology pathadora;

    public PathadoraManager() throws OWLOntologyCreationException {
        initialize();
    }

    private void initialize() throws OWLOntologyCreationException {
        this.manager = OWLManager.createOWLOntologyManager();
        manager.getIRIMappers().add(new SimpleIRIMapper(IRI.create(LOM_RESOURCE), IRI.create(LOM_LOCAL_PATH)));
        manager.getIRIMappers().add(new SimpleIRIMapper(IRI.create(ACC_RESOURCE), IRI.create(ACC_LOCAL_PATH)));
        manager.loadOntologyFromOntologyDocument(new File(LOM_LOCAL_PATH));
        manager.loadOntologyFromOntologyDocument(new File(ACC_LOCAL_PATH));
        pathadora = manager.loadOntologyFromOntologyDocument(new File(PATHADORA_LOCAL_PATH));
    }

    public void addIndividual(Map<String, String> params) throws OWLOntologyCreationException, OWLOntologyStorageException, IOException {
        Inserter indInserter = new Inserter(this);
        switch (params.get(TYPE)) {
            case LEARNER:
                indInserter.addLearner(params);
                break;
            case COURSE:
                indInserter.addCourse(params);
                break;
            case LESSON:
                indInserter.addLesson(params);
                break;
            default:
                break;
        }
    }

    public OWLOntology pathadoraOnt() throws OWLOntologyCreationException {
        return this.pathadora;
        // return manager.loadOntologyFromOntologyDocument(new File(PATHADORA_LOCAL_PATH));
    }

    public OWLOntologyManager getManager() {
        return manager;
    }

}
