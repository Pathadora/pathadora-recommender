package server.owl;

import org.semanticweb.owlapi.model.*;
import org.swrlapi.core.SWRLRuleEngine;
import org.swrlapi.exceptions.SWRLBuiltInException;
import org.swrlapi.factory.SWRLAPIFactory;
import org.swrlapi.parser.SWRLParseException;
import org.swrlapi.sqwrl.SQWRLQueryEngine;
import org.swrlapi.sqwrl.SQWRLResult;
import org.swrlapi.sqwrl.exceptions.SQWRLException;

public class RuleBasedModel {

    private final PathadoraManager pathadoraManager;

    public RuleBasedModel(PathadoraManager manager) {
        this.pathadoraManager = manager;
    }

    public void applyRule(String ruleName, String rule) throws OWLOntologyCreationException, SWRLBuiltInException, SWRLParseException, OWLOntologyStorageException {
        SWRLRuleEngine engine = initializeQueryEngine();
        engine.createSWRLRule(ruleName, rule);
        engine.infer();
        OWLOntology ontology = pathadoraManager.pathadoraOnt();
        pathadoraManager.getManager().saveOntology(ontology);
    }


    public void applyQuery() throws OWLOntologyCreationException, SWRLParseException, SQWRLException {
        SQWRLQueryEngine queryEngine = initializeQueryEngine();
        SQWRLResult result = queryEngine.runSQWRLQuery("query 1","");
        if (result.next()) System.out.println("Name: " + result.getLiteral("x").getInteger());
    }


    private SWRLRuleEngine initializeRuleEngine() throws OWLOntologyCreationException {
        return SWRLAPIFactory.createSWRLRuleEngine(pathadoraManager.pathadoraOnt());
    }


    private SQWRLQueryEngine initializeQueryEngine() throws OWLOntologyCreationException {
        return SWRLAPIFactory.createSQWRLQueryEngine(pathadoraManager.pathadoraOnt());
    }

}
