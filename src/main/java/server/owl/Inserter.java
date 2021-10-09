package server.owl;

import org.semanticweb.owlapi.model.*;

import java.io.IOException;
import java.util.Map;

import static server.utils.ParserUtils.*;
import static server.utils.PathadoraConfig.OntologyConfig.*;

public class Inserter {

    final private PathadoraManager manager;
    final private OWLOntologyManager ontManager;

    public Inserter(PathadoraManager m) {
        this.manager = m;
        this.ontManager = m.getManager();
    }

    public void addLearner(Map<String, String> params) throws OWLOntologyCreationException, IOException, OWLOntologyStorageException {
        OWLDataFactory df = ontManager.getOWLDataFactory();
        OntologyEntities entities = new OntologyEntities(manager);
        OWLOntology pathadora = manager.pathadoraOnt();

        Map<String, String> obj_prop = paramsToMapByKey(OBJECT_PROPERTIES, params);
        Map<String, String> ann_prop = paramsToMapByKey(ANNOTATION_PROPERTIES, params);

        OWLNamedIndividual tIndividual = (OWLNamedIndividual) entities.ontologyEntitiesBy(INDIVIDUALS, label(ann_prop));

        defineClassAssertion(params, df, tIndividual, entities, pathadora);
        defineObjectPropertyAssertions(obj_prop, df, tIndividual, entities, pathadora);
        defineAnnotationPropertyAssertions(ann_prop, df, tIndividual, entities, pathadora);

        ontManager.saveOntology(pathadora);
        System.out.println("Adding action done");
    }


    public void addCourse(Map<String, String> p) {
    }

    public void addLesson(Map<String, String> p) {
    }

    /**TODO: to be refactored after discussion, (course & lesson label key) */
    private String label(Map<String, String> properties) {
        return properties.get("name") + "_" + properties.get("last_name");
    }

    private void defineClassAssertion(Map<String, String> params,
                                      OWLDataFactory df,
                                      OWLNamedIndividual tIndividual,
                                      OntologyEntities entities,
                                      OWLOntology pathadora) throws OWLOntologyCreationException {
        OWLClass learnerClass = (OWLClass) entities.ontologyEntitiesBy(CLASSES, params.get(CLASS));
        ontManager.addAxiom(pathadora, df.getOWLClassAssertionAxiom(learnerClass, tIndividual));
    }

    private void defineObjectPropertyAssertions(Map<String, String> obj_prop,
                                                OWLDataFactory df,
                                                OWLNamedIndividual tIndividual,
                                                OntologyEntities entities,
                                                OWLOntology pathadora) throws OWLOntologyCreationException {
        for (Map.Entry<String, String> obj : obj_prop.entrySet()) {
            OWLNamedIndividual val = (OWLNamedIndividual) entities.ontologyEntitiesBy(INDIVIDUALS, obj.getValue());
            OWLObjectProperty prop = (OWLObjectProperty) entities.ontologyEntitiesBy(OBJECT_PROPERTIES, obj.getKey());
            OWLObjectPropertyAssertionAxiom propertyAssertion = df.getOWLObjectPropertyAssertionAxiom(prop, tIndividual, val);
            ontManager.addAxiom(pathadora, propertyAssertion);
        }
    }


    private void defineAnnotationPropertyAssertions(Map<String, String> ann_prop,
                                                    OWLDataFactory df,
                                                    OWLNamedIndividual tIndividual,
                                                    OntologyEntities entities,
                                                    OWLOntology pathadora) throws OWLOntologyCreationException {
        for (Map.Entry<String, String> obj : ann_prop.entrySet()) {
            OWLAnnotationProperty annProp = (OWLAnnotationProperty) entities.ontologyEntitiesBy(ANNOTATION_PROPERTIES, obj.getKey());
            OWLLiteral val = df.getOWLLiteral(obj.getValue());
            OWLAnnotation ann = df.getOWLAnnotation(annProp, val);
            OWLAxiom propertyAssertion = df.getOWLAnnotationAssertionAxiom(tIndividual.getIRI(), ann);
            ontManager.addAxiom(pathadora, propertyAssertion);
        }
    }



}
