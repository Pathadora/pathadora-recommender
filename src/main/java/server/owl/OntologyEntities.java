package server.owl;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import java.util.*;
import java.util.stream.Collectors;

import static server.utils.ParserUtils.individualsToList;
import static server.utils.PathadoraConfig.*;
import static server.utils.PathadoraConfig.OntologyConfig.*;
import static server.utils.PathadoraConfig.annotation;

public class OntologyEntities {

    private final PathadoraManager pathadoraManager;
    private final OWLOntologyManager owlManager;

    public OntologyEntities(PathadoraManager mng) {
        this.pathadoraManager = mng;
        this.owlManager = pathadoraManager.getManager();
    }


    public String defineClassAssertion(Map<String, String> params, OWLNamedIndividual tIndividual, OWLOntology pathadora) {
        OWLDataFactory df = owlManager.getOWLDataFactory();
        OWLClass learnerClass = (OWLClass) ontologyEntitiesBy(CLASSES, params.get(CLASS));
        owlManager.addAxiom(pathadora, df.getOWLClassAssertionAxiom(learnerClass, tIndividual));

        return classAssertion(valName(tIndividual.toString()), valName(learnerClass.toString()));
    }

    public String defineObjectPropertyAssertions(Map<String, String> obj_prop, OWLNamedIndividual tIndividual, OWLOntology pathadora) {
        StringBuilder outBuild = new StringBuilder();
        for (Map.Entry<String, String> obj : obj_prop.entrySet()) {
            /* check for multiple object property values */
            if(obj.getValue().contains(",")) {
                Arrays.stream(obj.getValue().split(",")).forEach( o -> {
                    outBuild.append(addProperty(obj.getKey(), o, tIndividual,  pathadora));});
            }else {
                outBuild.append(addProperty(obj.getKey(), obj.getValue(), tIndividual, pathadora));
            }
        }

        return outBuild.toString();
    }


    public String defineAnnotationPropertyAssertions(Map<String, String> ann_prop, OWLNamedIndividual tIndividual, OWLOntology pathadora) {
        StringBuilder outBuild = new StringBuilder();
        for (Map.Entry<String, String> obj : ann_prop.entrySet()) {
            /* check for multiple annotation property values */
            if (obj.getValue().contains(",")) {
                Arrays.stream(obj.getValue().split(",")).forEach(o -> {
                    outBuild.append(addAnnotation(obj.getKey(), o, tIndividual, pathadora));});
            } else {
                String output = addAnnotation(obj.getKey(), obj.getValue(), tIndividual,  pathadora);
                outBuild.append(output);
            }
        }

        return outBuild.append(endIndividualDeclaration()).toString();
    }


    private String addProperty(String key, String value, OWLNamedIndividual tIndividual, OWLOntology pathadora){
        OWLDataFactory df = owlManager.getOWLDataFactory();

        OWLNamedIndividual val = (OWLNamedIndividual) this.ontologyEntitiesBy(INDIVIDUALS, value);
        OWLObjectProperty prop = (OWLObjectProperty) this.ontologyEntitiesBy(OBJECT_PROPERTIES, key);
        OWLObjectPropertyAssertionAxiom propertyAssertion = df.getOWLObjectPropertyAssertionAxiom(prop, tIndividual, val);
        owlManager.addAxiom(pathadora, propertyAssertion);

        return property(key, value);
    }


    private String addAnnotation(String key, String value, OWLNamedIndividual tIndividual, OWLOntology pathadora){
        OWLDataFactory df = owlManager.getOWLDataFactory();

        OWLAnnotationProperty annProp = (OWLAnnotationProperty) this.ontologyEntitiesBy(ANNOTATION_PROPERTIES, key);
        OWLLiteral val = df.getOWLLiteral(value);
        OWLAnnotation ann = df.getOWLAnnotation(annProp, val);
        OWLAxiom propertyAssertion = df.getOWLAnnotationAssertionAxiom(tIndividual.getIRI(), ann);
        owlManager.addAxiom(pathadora, propertyAssertion);

        return annotation(key, value);
    }


    public OWLLogicalEntity ontologyEntitiesBy(String type, String key) {
        OWLDataFactory df = owlManager.getOWLDataFactory();
        PrefixManager pm = new DefaultPrefixManager(PATHADORA_RESOURCE);

        if (type.equals(CLASSES)) {
            List<OWLLogicalEntity> classes =
                    classes().stream()
                            .filter(c -> c.toString().contains(key))
                            .sorted(Comparator.comparing(c -> c.toString().length())).collect(Collectors.toList());
            return (classes.isEmpty()) ? df.getOWLClass("#" + key, pm) : classes.get(0);
        }

        if (type.equals(OBJECT_PROPERTIES)) {
            List<OWLLogicalEntity> objProps =
                    objectProperties().stream()
                            .filter(c -> c.toString().contains(key))
                            .sorted(Comparator.comparing(c -> c.toString().length())).collect(Collectors.toList());
            return (objProps.isEmpty()) ? df.getOWLObjectProperty("#" + key, pm) : objProps.get(0);
        }

        if (type.equals(INDIVIDUALS)) {
            List<OWLLogicalEntity> individuals =
                    namedIndividuals().stream()
                            .filter(c -> c.toString().contains(key))
                            .sorted(Comparator.comparing(c -> c.toString().length())).collect(Collectors.toList());
            return (individuals.isEmpty()) ? df.getOWLNamedIndividual("#" + key, pm) : individuals.get(0);
        }

        if (type.equals(ANNOTATION_PROPERTIES)) {
            List<OWLLogicalEntity> annProps =
                    annotationProperties().stream()
                            .filter(c -> c.toString().contains(key))
                            .sorted(Comparator.comparing(c -> c.toString().length())).collect(Collectors.toList());
            return (annProps.isEmpty()) ? df.getOWLAnnotationProperty("#" + key, pm) : annProps.get(0);
        }

        /*if(type.equals(DATA_PROPERTIES)){
            return dataProperties().stream()
                    .filter(c -> c.toString().contains(key))
                    .sorted(Comparator.comparing(c -> c.toString().length())).collect(Collectors.toList());
        }
        return new ArrayList<>();*/
        return null;
    }


    public List<String> extractIndividualsBy(String individual, String property, PathadoraManager manager) {
        OWLOntology ontology = manager.pathadoraOnt();
        OWLObjectProperty objProperty = (OWLObjectProperty) ontologyEntitiesBy(OBJECT_PROPERTIES, property);
        OWLNamedIndividual namedInd = (OWLNamedIndividual) ontologyEntitiesBy(INDIVIDUALS, individual);

        OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
        OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
        return individualsToList(reasoner.getObjectPropertyValues(namedInd, objProperty).getFlattened());
    }


    private Set<OWLObjectProperty> objectProperties() {
        return pathadoraManager.pathadoraOnt().getObjectPropertiesInSignature();
    }

    private Set<OWLClass> classes() {
        return pathadoraManager.pathadoraOnt().getClassesInSignature();
    }

    private Set<OWLDataProperty> dataProperties() {
        return pathadoraManager.pathadoraOnt().getDataPropertiesInSignature(); }

    private Set<OWLNamedIndividual> namedIndividuals() {
        return pathadoraManager.pathadoraOnt().getIndividualsInSignature(); }

    private Set<OWLAnnotationProperty> annotationProperties() {
        return pathadoraManager.pathadoraOnt().getAnnotationPropertiesInSignature();
    }

    private String propName(String prop) {
        return prop.substring(0, prop.length() - 1).split("#")[1];
    }


    private String valName(String val){
        return val.substring(1,val.length()-1);
    }


    private String annName(String ann){
        return ann.split("#")[1].split(">")[0];
    }


}


