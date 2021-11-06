package server.owl;

import org.semanticweb.owlapi.model.*;
import org.swrlapi.exceptions.SWRLBuiltInException;
import org.swrlapi.parser.SWRLParseException;

import java.util.*;


public class Recommender {
    final private PathadoraManager manager;
    final private RuleBasedModel model;
    final private OntologyEntities entities;

    public Recommender(PathadoraManager m) throws SWRLParseException, OWLOntologyCreationException, SWRLBuiltInException, OWLOntologyStorageException {
        this.manager = m;
        this.entities = new OntologyEntities(manager);
        this.model = new RuleBasedModel(manager);
        initializeOntologyWithRules(model, false);
    }

    public Map<String, List<String>> recommendedFaculties(String individual, String degree) throws OWLOntologyCreationException,
            OWLOntologyStorageException, SWRLParseException, SWRLBuiltInException {

        List<String> departments = entities.extractIndividualsBy(individual, "recommendedDepartment", manager);
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
        return entities.extractIndividualsBy(learner, property, manager);
    }


    private List<String> nonDuplicatedFaculties(String department, List<String> faculties) throws OWLOntologyCreationException {
        List<String> facsOfDep = entities.extractIndividualsBy(department, "departmentHasFaculty", manager);
        List<String> common = new ArrayList<>(faculties);
        common.retainAll(facsOfDep);
        return common;
    }

    private void initializeOntologyWithRules(RuleBasedModel model, boolean apply) throws SWRLParseException, OWLOntologyCreationException, SWRLBuiltInException, OWLOntologyStorageException {
        if(apply){
            System.out.println("Recommender applying rules, waiting ...");
            model.applyRule("School", Rules.schoolRule());
            model.applyRule("Departments", Rules.departmentRule());
           // model.applyRule("Faculties", Rules.recommendedFaculties());
            System.out.println("Recommender completed applying rules");
        }else{
            System.out.println("Recommender will not apply rules");
        }
    }

}

