package server.owl;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.swrlapi.exceptions.SWRLBuiltInException;
import org.swrlapi.parser.SWRLParseException;
import static server.utils.ParserUtils.*;
import static server.utils.PathadoraConfig.OntologyConfig.*;
import java.util.*;


public class Recommender {

    final private PathadoraManager manager;

    public Recommender(PathadoraManager m) {
        this.manager = m;
    }

    public Map<String, List<String>> recommendedFacultiesAndDepartments(String individual) throws OWLOntologyCreationException, OWLOntologyStorageException, SWRLParseException, SWRLBuiltInException {
        RuleBasedModel model = new RuleBasedModel(manager);
        Map<String, List<String>> output = new HashMap<>();

        List<String> faculties = recommend(model,
                "Faculties", Rules.facultyRule(),  "recommendedFaculty", individual);
        for(String fac : faculties){
            List<String> departments = recommendedDepartment(model, individual, fac);
            output.put(fac, departments);
        }

        return output;
    }


    private List<String> recommend(RuleBasedModel model, String ruleName, String rule, String property, String learner) throws OWLOntologyCreationException, SWRLParseException, SWRLBuiltInException, OWLOntologyStorageException {
        model.applyRule(ruleName, rule);
        Set<OWLNamedIndividual> values = extractIndividualsBy(learner, property);
        return individualsToList(values);
    }


    private List<String> recommendedDepartment(RuleBasedModel model, String learner, String faculty) throws SWRLParseException, OWLOntologyCreationException, SWRLBuiltInException, OWLOntologyStorageException {
        List<String> recDeps = recommend(model,
                "Departments", Rules.departmentRule(learner,faculty), "recommendedDepartment", learner);
        recDeps.retainAll(individualsToList(extractIndividualsBy(faculty, "facultyHasDepartment")));
        return recDeps;
    }


    private Set<OWLNamedIndividual> extractIndividualsBy(String individual, String property) throws OWLOntologyCreationException {
        OntologyEntities entities = new OntologyEntities(manager);
        OWLOntology ontology = manager.pathadoraOnt();
        OWLObjectProperty objProperty = (OWLObjectProperty) entities.ontologyEntitiesBy(OBJECT_PROPERTIES, property);
        OWLNamedIndividual namedInd = (OWLNamedIndividual) entities.ontologyEntitiesBy(INDIVIDUALS, individual);

        OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
        OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);

        return reasoner.getObjectPropertyValues(namedInd, objProperty).getFlattened();
    }


    public Map<String, String> recommendCourses(){
        Map<String, String> courseData = new HashMap<>();
        courseData.put("course", "Chemistry");
        courseData.put("period", "I");
        courseData.put("type", "basic learning activities");
        courseData.put("scientific area", "CHIM/03");
        return courseData;
    }

}
