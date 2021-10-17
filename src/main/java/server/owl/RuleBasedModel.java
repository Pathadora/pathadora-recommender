package server.owl;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.swrlapi.core.SWRLAPIOWLOntology;
import org.swrlapi.core.SWRLAPIRule;
import org.swrlapi.core.SWRLRuleEngine;
import org.swrlapi.exceptions.SWRLBuiltInException;
import org.swrlapi.factory.SWRLAPIFactory;
import org.swrlapi.parser.SWRLParseException;
import org.swrlapi.sqwrl.SQWRLQueryEngine;
import org.swrlapi.sqwrl.SQWRLResult;
import org.swrlapi.sqwrl.exceptions.SQWRLException;

import static org.swrlapi.factory.SWRLAPIInternalFactory.createSWRLAPIOntology;

public class RuleBasedModel {

    PathadoraManager pathadoraManager = new PathadoraManager();

    public RuleBasedModel() throws OWLOntologyCreationException {
    }

    public void applyRule() throws OWLOntologyCreationException, SWRLBuiltInException, SWRLParseException {
        SWRLAPIOWLOntology swrlapiOWLOntology = createSWRLAPIOntology(pathadoraManager.pathadoraOnt());
        SWRLAPIRule rule = swrlapiOWLOntology
                .createSWRLRule("query 1",
                        "accessible_ocw_ontology:Learner(?learner) ^ " +
                                "pathadora-ontology:hasDegree(?learner, ?degree) ^ " +
                                "pathadora-ontology:passionateOf(?learner, ?passion) ^ " +
                                "pathadora-ontology:departmentArea(?department, ?passion) " +
                                "-> pathadora-ontology:recommendedDepartment(?learner, ?department)");
        
        System.out.println("Result: " + rule.getBodyAtoms().size());
    }

    public void applyQuery() throws OWLOntologyCreationException, SWRLParseException, SQWRLException {
        SQWRLQueryEngine queryEngine = initializeQueryEngine();
        SQWRLResult result = queryEngine
                .runSQWRLQuery("query 1","");
        if (result.next())
            System.out.println("Name: " + result.getLiteral("x").getInteger());
    }


    private SWRLRuleEngine initializeRuleEngine() throws OWLOntologyCreationException {
        return SWRLAPIFactory.createSWRLRuleEngine(pathadoraManager.pathadoraOnt());
    }

    private SQWRLQueryEngine initializeQueryEngine() throws OWLOntologyCreationException {
        return SWRLAPIFactory.createSQWRLQueryEngine(pathadoraManager.pathadoraOnt());
    }


    public static void main(String... args) throws OWLOntologyCreationException, SWRLParseException, SWRLBuiltInException {
        System.out.println("SWRL Rule Model");
        new RuleBasedModel().applyRule();
    }
}
