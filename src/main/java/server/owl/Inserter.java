package server.owl;

import org.semanticweb.owlapi.model.*;
import server.stardog.StardogDatabase;

import java.util.Map;

import static server.utils.ParserUtils.*;
import static server.utils.PathadoraConfig.OntologyConfig.*;
import static server.utils.PathadoraConfig.ServerConfig.STATUS_OK;

public class Inserter {

    final private PathadoraManager manager;
    final private OWLOntologyManager ontManager;
    final private StardogDatabase database;


    public Inserter(PathadoraManager m, StardogDatabase db) {
        this.manager = m;
        this.ontManager = m.getManager();
        this.database = db;
    }

    public String addLearner(Map<String, String> params) throws OWLOntologyStorageException {
        OWLDataFactory df = ontManager.getOWLDataFactory();
        OntologyEntities entities = new OntologyEntities(manager);
        OWLOntology pathadora = manager.pathadoraOnt();

        Map<String, String> obj_prop = paramsToMapByKey(OBJECT_PROPERTIES, params);
        Map<String, String> ann_prop = paramsToMapByKey(ANNOTATION_PROPERTIES, params);

        OWLNamedIndividual tIndividual = (OWLNamedIndividual) entities.ontologyEntitiesBy(INDIVIDUALS, label(ann_prop));

        String caOwl  = defineClassAssertion(params, df, tIndividual, entities, pathadora);
        String opaOwl = defineObjectPropertyAssertions(obj_prop, df, tIndividual, entities, pathadora);
        String apaOwl = defineAnnotationPropertyAssertions(ann_prop, df, tIndividual, entities, pathadora);

        ontManager.saveOntology(pathadora);

        /*StardogRunnable stardog = new  StardogRunnable(database);
        new Thread(() -> {
            try {stardog.database().insertData(caOwl+opaOwl+apaOwl);
            } catch (IOException e) {e.printStackTrace();}
        }).start();
        */

        return String.valueOf(STATUS_OK);
    }


    public String addLesson(Map<String, String> p) {
        return String.valueOf(STATUS_OK);
    }

    /**TODO: to be refactored after discussion, (course & lesson label key) */
    private String label(Map<String, String> properties) {
        return properties.get("name") + "_" + properties.get("surname");
    }

    private String defineClassAssertion(Map<String, String> params,
                                      OWLDataFactory df,
                                      OWLNamedIndividual tIndividual,
                                      OntologyEntities entities,
                                      OWLOntology pathadora) {

        OWLClass learnerClass = (OWLClass) entities.ontologyEntitiesBy(CLASSES, params.get(CLASS));
        ontManager.addAxiom(pathadora, df.getOWLClassAssertionAxiom(learnerClass, tIndividual));

        return new StringBuilder()
                .append("<owl:NamedIndividual rdf:about=\""+valName(tIndividual.toString())+"\">\n")
                .append("<rdf:type rdf:resource=\""+valName(learnerClass.toString())+"\"/>\n")
                .toString();
    }

    private String defineObjectPropertyAssertions(Map<String, String> obj_prop,
                                                OWLDataFactory df,
                                                OWLNamedIndividual tIndividual,
                                                OntologyEntities entities,
                                                OWLOntology pathadora) {
        StringBuilder outBuild = new StringBuilder();
        for (Map.Entry<String, String> obj : obj_prop.entrySet()) {
            OWLNamedIndividual val = (OWLNamedIndividual) entities.ontologyEntitiesBy(INDIVIDUALS, obj.getValue());
            OWLObjectProperty prop = (OWLObjectProperty) entities.ontologyEntitiesBy(OBJECT_PROPERTIES, obj.getKey());
            OWLObjectPropertyAssertionAxiom propertyAssertion = df.getOWLObjectPropertyAssertionAxiom(prop, tIndividual, val);

            outBuild.append("<pathadora-ontology:"+ propName(prop.toString()) + " rdf:resource=\""+valName(val.toString())+"\"/>\n");
            ontManager.addAxiom(pathadora, propertyAssertion);
        }
        return outBuild.toString();
    }


    private String defineAnnotationPropertyAssertions(Map<String, String> ann_prop,
                                                    OWLDataFactory df,
                                                    OWLNamedIndividual tIndividual,
                                                    OntologyEntities entities,
                                                    OWLOntology pathadora) {
        StringBuilder outBuild = new StringBuilder();
        for (Map.Entry<String, String> obj : ann_prop.entrySet()) {
            OWLAnnotationProperty annProp = (OWLAnnotationProperty) entities.ontologyEntitiesBy(ANNOTATION_PROPERTIES, obj.getKey());
            OWLLiteral val = df.getOWLLiteral(obj.getValue());
            OWLAnnotation ann = df.getOWLAnnotation(annProp, val);
            OWLAxiom propertyAssertion = df.getOWLAnnotationAssertionAxiom(tIndividual.getIRI(), ann);

            outBuild.append("<pathadora-ontology:"+ annName(ann.toString()) + ">"+ val.getLiteral()+"</pathadora-ontology:"+annName(ann.toString())+">\n");
            ontManager.addAxiom(pathadora, propertyAssertion);
        }
        return outBuild.append(" </owl:NamedIndividual>").toString();
    }

    private String propName(String prop){
        return prop.substring(0, prop.length()-1).split("#")[1];
    }
    private String valName(String val){
           return val.substring(1,val.length()-1);
    }
    private String annName(String prop){ return prop.split("#")[1].split(">")[0];}
}
