package server.owl;

import org.semanticweb.owlapi.model.*;

import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

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

    /** TODO: to be refactored with strategy pattern */
    public OWLClass getClassBy(String classKey) throws OWLOntologyCreationException {
        return classes().stream()
                .filter(c -> c.toString().contains(classKey))
                .sorted(Comparator.comparing(c -> c.toString().length())).collect(Collectors.toList())
                .get(0);
    }

    public OWLObjectProperty getObjectPropertiesBy(String propKey) throws OWLOntologyCreationException {
        return objectProperties().stream()
                .filter(c -> c.toString().contains(propKey))
                .sorted(Comparator.comparing(c -> c.toString().length())).collect(Collectors.toList())
                .get(0);
    }

    public OWLDataProperty getDataPropertyBy(String dataPropKey) throws OWLOntologyCreationException {
        return dataProperties().stream()
                .filter(c -> c.toString().contains(dataPropKey))
                .sorted(Comparator.comparing(c -> c.toString().length())).collect(Collectors.toList())
                .get(0);
    }

    public OWLNamedIndividual getIndividualsBy(String indKey) throws OWLOntologyCreationException {
        return namedIndividuals().stream()
                .filter(c -> c.toString().contains(indKey))
                .sorted(Comparator.comparing(c -> c.toString().length())).collect(Collectors.toList())
                .get(0);
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
