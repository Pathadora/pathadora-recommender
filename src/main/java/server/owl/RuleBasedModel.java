package server.owl;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.swrlapi.core.SWRLRuleEngine;
import org.swrlapi.factory.SWRLAPIFactory;

public class RuleModel {

    PathadoraManager pathadoraManager = new PathadoraManager();

    public RuleModel() throws OWLOntologyCreationException {
    }

    public void applyRule() throws OWLOntologyCreationException {
        OWLOntology ontology = pathadoraManager.pathadoraOnt();

        SWRLRuleEngine swrlRuleEngine = SWRLAPIFactory.createSWRLRuleEngine(ontology);

        swrlRuleEngine.infer();
    }

    public static void main(String... args) throws OWLOntologyCreationException {
        System.out.println("SWRL Rule Model");
        new RuleModel().applyRule();

    }

}
