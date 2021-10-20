package server.owl;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.search.Searcher;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import java.util.*;
import java.util.stream.Collectors;

import static server.utils.PathadoraConfig.OntologyConfig.*;

public class OntologyEntities {

    private final PathadoraManager manager;

    public OntologyEntities(PathadoraManager mng) {
        this.manager = mng;
    }

    private Set<OWLObjectProperty> objectProperties() throws OWLOntologyCreationException {
        return manager.pathadoraOnt().getObjectPropertiesInSignature();
    }

    private Set<OWLClass> classes() throws OWLOntologyCreationException {
        return manager.pathadoraOnt().getClassesInSignature();
    }

    private Set<OWLDataProperty> dataProperties() throws OWLOntologyCreationException {
        return manager.pathadoraOnt().getDataPropertiesInSignature();
    }

    private Set<OWLNamedIndividual> namedIndividuals() throws OWLOntologyCreationException {
        return manager.pathadoraOnt().getIndividualsInSignature();
    }

    private Set<OWLAnnotationProperty> annotationProperties() throws OWLOntologyCreationException {
        return manager.pathadoraOnt().getAnnotationPropertiesInSignature();
    }


    public boolean checkAssertionAxiom(OWLClass iClass, OWLIndividual individual) throws OWLOntologyCreationException {
        OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
        OWLReasoner reasoner = reasonerFactory.createReasoner(manager.pathadoraOnt());

        NodeSet<OWLNamedIndividual> individualsNodeSet = reasoner.getInstances(iClass, true);
        Set<OWLNamedIndividual> individuals = individualsNodeSet.getFlattened();

        for (OWLNamedIndividual ind : individuals) {
            if (individual.toString().equals(individuals.toString())) {
                return true;
            }
            //String s = ind.toString();
            //System.out.println(s.substring(s.indexOf("#") + 1, s.length() -1));
        }
        return false;
    }

    public OWLLogicalEntity ontologyEntitiesBy(String type, String key) throws OWLOntologyCreationException {
        OWLDataFactory df = manager.getManager().getOWLDataFactory();
        PrefixManager pm = new DefaultPrefixManager(PATHADORA_RESOURCE);
        System.out.println("We are at Ontology Entities");

        if (type.equals(CLASSES)) {
            System.out.println("Classes");
            List<OWLLogicalEntity> classes =
                    classes().stream()
                            .filter(c -> c.toString().contains(key))
                            .sorted(Comparator.comparing(c -> c.toString().length())).collect(Collectors.toList());

            System.out.println(classes.toString());
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

    public void ontologyEntitiesToConsole() throws OWLOntologyCreationException {
        OWLOntology ontology = manager.pathadoraOnt();
        Set<OWLClass> classes = classes();
        Set<OWLObjectProperty> objectProperties = objectProperties();
        Set<OWLDataProperty> dataProperties = dataProperties();
        Set<OWLNamedIndividual> namedIndividuals = namedIndividuals();

        for (OWLClass cls : classes) {
            System.out.println("+: " + cls.getIRI().getShortForm());

            System.out.println(" \tObject Property Domain");
            for (OWLObjectPropertyDomainAxiom op : ontology.getAxioms(AxiomType.OBJECT_PROPERTY_DOMAIN)) {
                if (op.getDomain().equals(cls)) {
                    for (OWLObjectProperty oop : op.getObjectPropertiesInSignature()) {
                        System.out.println("\t\t +: " + oop.getIRI().getShortForm());
                    }
                    System.out.println("\t\t +: " + op.getProperty().getNamedProperty().getIRI().getShortForm());
                }
            }

            System.out.println(" \tData Property Domain");
            for (OWLDataPropertyDomainAxiom dp : ontology.getAxioms(AxiomType.DATA_PROPERTY_DOMAIN)) {
                if (dp.getDomain().equals(cls)) {
                    for (OWLDataProperty odp : dp.getDataPropertiesInSignature()) {
                        System.out.println("\t\t +: " + odp.getIRI().getShortForm());
                    }
                    System.out.println("\t\t +:" + dp.getProperty());
                }
            }
        }
    }

}
