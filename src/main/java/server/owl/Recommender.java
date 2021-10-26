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

/**TODO: RUN RULES ONLY THE FIRST TIME, NOT EACH TIME AN INTERACTION HAPPENS */


public class Recommender {

    final private PathadoraManager manager;
    final private RuleBasedModel model;

    public Recommender(PathadoraManager m) {
        this.manager = m;
        model = new RuleBasedModel(manager);
    }

    public Map<String, List<String>> recommendedFaculties(String individual, String degree) throws OWLOntologyCreationException, OWLOntologyStorageException, SWRLParseException, SWRLBuiltInException {
        System.out.println("Recommended");

        Map<String, List<String>> output = new HashMap<>();

        List<String> faculties = recommend("Schools", Rules.schoolRule(),  "recommendedSchool", individual);
        for(String fac : faculties){
            System.out.println("Recommended departments");
            List<String> departments = recommendedDepartments(individual, fac);
            for(String dep : departments){
                System.out.println("Recommended faculties");
                List<String> facs = extractRecommendedFaculties(individual, dep, degree);
                output.put(dep, facs);
            }
        }
        return output;
    }


    private List<String> recommendedDepartments(String learner, String school) throws SWRLParseException, OWLOntologyCreationException, SWRLBuiltInException, OWLOntologyStorageException {
        List<String> recDepartments = recommend(
                "Departments", Rules.departmentRule(learner,school), "recommendedDepartment", learner);
        recDepartments.retainAll(individualsToList(extractIndividualsBy(school, "schoolHasDepartment")));
        return recDepartments;
    }


    private List<String> extractRecommendedFaculties(String learner, String department, String degree) throws SWRLParseException, OWLOntologyCreationException, SWRLBuiltInException, OWLOntologyStorageException {
        List<String> recDepartments = recommend(
                "Faculties", Rules.recommendedFaculties(learner, department, degree), "recommendedFaculty", learner);
        recDepartments.retainAll(individualsToList(extractIndividualsBy(department, "departmentHasFaculty")));
        return recDepartments;
    }



    public Map<String, String> recommendCourses(String learner, String faculty, String degree, String year) throws SWRLParseException, OWLOntologyCreationException, SWRLBuiltInException, OWLOntologyStorageException {
        List<String> recCourses = recommend(
                "Courses", Rules.coursesRule(learner, faculty, degree, year), "recommendedCourses", learner);

        return new HashMap<>();
    }




    private List<String> recommend(String ruleName, String rule, String property, String learner) throws OWLOntologyCreationException, SWRLParseException, SWRLBuiltInException, OWLOntologyStorageException {
        model.applyRule(ruleName, rule);
        Set<OWLNamedIndividual> values = extractIndividualsBy(learner, property);
        return individualsToList(values);
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
}
