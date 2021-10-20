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

    PathadoraManager pathadoraManager;
    public RuleBasedModel(PathadoraManager manager) throws OWLOntologyCreationException, OWLOntologyStorageException {
        this.pathadoraManager = manager;
    }

    public void applyRule() throws OWLOntologyCreationException, SWRLBuiltInException, SWRLParseException, OWLOntologyStorageException {
        SWRLRuleEngine engine = initializeQueryEngine();
        engine.createSWRLRule("query 1",
                "accessible_ocw_ontology:Learner(?learner) ^ " +
                        "pathadora-ontology:hasDegree(?learner, ?degree) ^ " +
                        "pathadora-ontology:passionateOf(?learner, ?passion) ^ " +
                        "pathadora-ontology:departmentArea(?department, ?passion) " +
                        "-> pathadora-ontology:recommendedDepartment(?learner, ?department)");
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


    public static void main(String... args) throws OWLOntologyCreationException, SWRLParseException, SWRLBuiltInException, OWLOntologyStorageException {
        System.out.println("SWRL Rule Model");
        //new RuleBasedModel(this).applyRule();
    }
}
