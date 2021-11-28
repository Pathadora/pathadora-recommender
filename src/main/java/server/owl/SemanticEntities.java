package server.owl;

import org.semanticweb.owlapi.model.OWLLogicalEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.Map;

public interface SemanticEntities {
    String defineClassAssertion(Map<String, String> params, OWLNamedIndividual tIndividual, OWLOntology pathadora);

    String defineObjectPropertyAssertions(Map<String, String> objProp, OWLNamedIndividual tIndividual, OWLOntology pathadora);

    String defineDataPropertyAssertions(Map<String, String> objProp, OWLNamedIndividual tIndividual, OWLOntology pathadora);

    String defineAnnotationPropertyAssertions(Map<String, String> ann_prop, OWLNamedIndividual tIndividual, OWLOntology pathadora);

    OWLLogicalEntity ontologyEntitiesBy(String type, String key);
}
