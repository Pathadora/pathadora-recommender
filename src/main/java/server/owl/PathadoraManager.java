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
        OntologyEntities entities = new OntologyEntities(this);
        OWLOntology pathadora = pathadoraOnt();

        Map<String, String> obj_prop = paramsToMapByKey(OBJECT_PROPERTIES, params);
        Map<String, String> ann_prop = paramsToMapByKey(ANNOTATION_PROPERTIES, params);

        OWLClass learnerClass = (OWLClass) entities.ontologyEntitiesBy(CLASSES, params.get(CLASS));
        OWLNamedIndividual tIndividual = (OWLNamedIndividual) entities.ontologyEntitiesBy(INDIVIDUALS, label(ann_prop) );

        manager.addAxiom(pathadora, df.getOWLClassAssertionAxiom(learnerClass, tIndividual));

        for(Map.Entry<String, String> obj : obj_prop.entrySet()){
            OWLNamedIndividual val = (OWLNamedIndividual) entities.ontologyEntitiesBy(INDIVIDUALS, obj.getValue());
            OWLObjectProperty prop = (OWLObjectProperty) entities.ontologyEntitiesBy(OBJECT_PROPERTIES, obj.getKey());
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
        return this.pathadora;
       // return manager.loadOntologyFromOntologyDocument(new File(PATHADORA_LOCAL_PATH));
    }

    private String label(Map<String, String> properties){
        return properties.get("name")+"_"+properties.get("last_name");
    }

}
