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
    final private RuleBasedModel model;

    public Recommender(PathadoraManager m) throws SWRLParseException,
            OWLOntologyCreationException, SWRLBuiltInException, OWLOntologyStorageException {
        this.manager = m;
        model = new RuleBasedModel(manager);
        model.applyRule("School", Rules.schoolRule());
        model.applyRule("Departments", Rules.departmentRule());
        //model.applyRule("Faculties", Rules.recommendedFaculties());
    }

    public Map<String, List<String>> recommendedFaculties(String individual, String degree) throws OWLOntologyCreationException,
            OWLOntologyStorageException, SWRLParseException, SWRLBuiltInException {

        List<String> departments = extractIndividualsBy(individual, "recommendedDepartment");
        List<String> recommendedFacs =  recommend("Faculties", Rules.recommendedFaculties(individual, degree),"recommendedFaculty", individual);

        Map<String, List<String>> output = new HashMap<>();
        for(String dep : departments){
            output.put(dep, nonDuplicatedFaculties(dep, recommendedFacs));
        }

        return output;
    }


    public Map<String, String> recommendedCourses(String learner, String faculty, String degree, String year) throws SWRLParseException,
            OWLOntologyCreationException, SWRLBuiltInException, OWLOntologyStorageException {
        List<String> recCourses = recommend(
                "Courses", Rules.recommendedCourses(learner, faculty, degree, year), "recommendedCourses", learner);
        System.out.println("Recommended courses: " + recCourses.toString());

        Map<String, String> output = new HashMap<>();
        for(String course : recCourses){
            /* Add other course data here */
            output.put("course", course);
        }
        return output;
    }


    private List<String> recommend(String ruleName, String rule, String property, String learner) throws OWLOntologyCreationException,
            SWRLParseException, SWRLBuiltInException, OWLOntologyStorageException {
        model.applyRule(ruleName, rule);
        return extractIndividualsBy(learner, property);
    }


    private List<String> extractIndividualsBy(String individual, String property) throws OWLOntologyCreationException {
        OntologyEntities entities = new OntologyEntities(manager);
        OWLOntology ontology = manager.pathadoraOnt();
        OWLObjectProperty objProperty = (OWLObjectProperty) entities.ontologyEntitiesBy(OBJECT_PROPERTIES, property);
        OWLNamedIndividual namedInd = (OWLNamedIndividual) entities.ontologyEntitiesBy(INDIVIDUALS, individual);

        OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
        OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
        return individualsToList(reasoner.getObjectPropertyValues(namedInd, objProperty).getFlattened());
    }


    private List<String> nonDuplicatedFaculties(String department, List<String> faculties) throws OWLOntologyCreationException {
        List<String> facsOfDep = extractIndividualsBy(department, "departmentHasFaculty");
        List<String> common = new ArrayList<>(faculties);
        common.retainAll(facsOfDep);
        return common;
    }

}

