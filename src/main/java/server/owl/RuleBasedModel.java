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


    public void applyRule(String ruleName, String rule) throws SWRLBuiltInException, SWRLParseException {
        System.out.println("\tRecommender applying rule: " + ruleName);
        SWRLRuleEngine engine = initializeRuleEngine();
        engine.createSWRLRule(ruleName, rule);
        engine.infer();
        OWLOntology pathadora = pathadoraManager.pathadoraOnt();

        // pathadoraManager.updatePathadoraOntology(pathadora);
    }


    public void applyQuery() throws SWRLParseException, SQWRLException {
        SQWRLQueryEngine queryEngine = initializeQueryEngine();
        SQWRLResult result = queryEngine.runSQWRLQuery("query 1","");
        if (result.next()) System.out.println("Name: " + result.getLiteral("x").getInteger());
    }


    private SWRLRuleEngine initializeRuleEngine() {
        return SWRLAPIFactory.createSWRLRuleEngine(pathadoraManager.pathadoraOnt());
    }


    private SQWRLQueryEngine initializeQueryEngine() {
        return SWRLAPIFactory.createSQWRLQueryEngine(pathadoraManager.pathadoraOnt());
    }

}
