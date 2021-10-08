package server.owl;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.util.SimpleIRIMapper;
import server.model.Learner;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static server.utils.ParserUtils.*;
import static server.utils.PathadoraConfig.OntologyConfig.*;

public class PathadoraManager {

    private OWLOntologyManager manager;

    public PathadoraManager() throws OWLOntologyCreationException {
        initialize();
    }

    private void initialize() throws OWLOntologyCreationException {
        this.manager = OWLManager.createOWLOntologyManager();
        manager.getIRIMappers().add(new SimpleIRIMapper(IRI.create(LOM_RESOURCE), IRI.create(LOM_LOCAL_PATH)));
        manager.getIRIMappers().add(new SimpleIRIMapper(IRI.create(ACC_RESOURCE), IRI.create(ACC_LOCAL_PATH)));
        manager.loadOntologyFromOntologyDocument(new File(LOM_LOCAL_PATH));
        manager.loadOntologyFromOntologyDocument(new File(ACC_LOCAL_PATH));
    }

    public void addIndividual(Map<String, String> params) throws OWLOntologyCreationException, OWLOntologyStorageException, IOException {
        switch (params.get(TYPE)) {
            case LEARNER:
                addLearner(params);
                break;
            case COURSE:
                addCourse(params);
                break;
            case LESSON:
                addLesson(params);
                break;
            default:
                break;
        }
    }

    private void addLearner(Map<String, String> params) throws OWLOntologyCreationException, IOException, OWLOntologyStorageException {
        OWLDataFactory df = manager.getOWLDataFactory();
        PrefixManager pmPathadora = new DefaultPrefixManager(PATHADORA_RESOURCE);
        PrefixManager pmLom = new DefaultPrefixManager(LOM_RESOURCE);
        PrefixManager pmAcc = new DefaultPrefixManager(ACC_RESOURCE);

        Learner l = new Learner(params);
        Map<String, String> obj_prop = paramsToMapByKey(OBJECT_PROPERTIES, params);
        Map<String, String> ann_prop = paramsToMapByKey(ANNOTATION_PROPERTIES, params);

        OntologyEntities entities = new OntologyEntities(this);
        OWLOntology pathadora = pathadoraOnt();

        OWLClass learnerClass = entities.getClassBy("Learner"); //'#' is  delimiter
        OWLIndividual tIndividual = df.getOWLNamedIndividual("#" +ann_prop.get("name")+"_"+ann_prop.get("last_name"), pmAcc);
        OWLClassAssertionAxiom learnerAx = df.getOWLClassAssertionAxiom(learnerClass, tIndividual);
        manager.addAxiom(pathadora, learnerAx);

        for(Map.Entry<String, String> obj : obj_prop.entrySet()){
            OWLNamedIndividual val = entities.getIndividualsBy(obj.getValue());
            OWLObjectProperty prop = entities.getObjectPropertiesBy(obj.getKey());
            OWLObjectPropertyAssertionAxiom propertyAssertion = df.getOWLObjectPropertyAssertionAxiom(prop, tIndividual, val);
            manager.addAxiom(pathadora, propertyAssertion);
        }
        System.out.println("Exit");
        manager.saveOntology(pathadora);
    }

    private void addCourse(Map<String, String> p) {
    }

    private void addLesson(Map<String, String> p) {
    }

    public OWLOntologyManager getManager() {
        return manager;
    }


    public OWLOntology pathadoraOnt() throws OWLOntologyCreationException {
        return manager.loadOntologyFromOntologyDocument(new File(PATHADORA_LOCAL_PATH));
    }
}
