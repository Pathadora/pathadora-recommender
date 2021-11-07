package server.owl;

import org.semanticweb.owlapi.model.*;
import org.swrlapi.exceptions.SWRLBuiltInException;
import org.swrlapi.parser.SWRLParseException;

import java.util.*;

import static server.utils.PathadoraConfig.OntologyConfig.*;

public class Recommender {
    final private PathadoraManager manager;
    final private RuleBasedModel model;

    public Recommender(PathadoraManager m) {
        this.manager = m;
        this.model = new RuleBasedModel(manager);
    }

    public Map<String, List<String>> recommendedFaculties(String learner, String degree)
            throws OWLOntologyStorageException, SWRLParseException, SWRLBuiltInException {

        List<String> departments = updatedEntities().extractIndividualsBy(learner, DEPARTMENT_PROPERTY, manager);
        List<String> recommendedFacs = recommend(FACULTIES, Rules.recommendedFaculties(learner, degree), FACULTY_PROPERTY, learner);

        Map<String, List<String>> output = new HashMap<>();
        for (String dep : departments) {
            output.put(dep, nonDuplicatedFaculties(dep, recommendedFacs));
        }

        manager.savePathadoraOntology(manager.pathadoraOnt(), true);
        return output;

    }


    public void initializeOntologyWithRules(boolean apply)
            throws SWRLParseException, SWRLBuiltInException {
        if (apply) {
            System.out.println("Recommender applying rules, waiting ...");
            model.applyRule(SCHOOLS, Rules.schoolRule());
            model.applyRule(DEPARTMENTS, Rules.departmentRule());
            //model.applyRule(FACULTIES, Rules.recommendedFaculties());
            System.out.println("Recommender completed applying rules");
        } else {
            System.out.println("Recommender will not apply rules");
        }
    }


    public Map<String, String> recommendedCourses(String learner, String faculty, String degree, String year)
            throws SWRLParseException, SWRLBuiltInException {
        List<String> recCourses = recommend(COURSES, Rules.recommendedCourses(learner, faculty, degree, year), COURSE_PROPERTY, learner);
        System.out.println("Recommended courses: " + recCourses.toString());

        Map<String, String> output = new HashMap<>();
        for (String course : recCourses) {
            /* Add other course data here */
            output.put("course", course);
        }
        return output;
    }


    private List<String> nonDuplicatedFaculties(String department, List<String> faculties) {
        List<String> facsOfDep = updatedEntities().extractIndividualsBy(department, DEPARTMENT_OF_PROPERTY, manager);
        List<String> common = new ArrayList<>(faculties);
        common.retainAll(facsOfDep);
        return common;
    }


    private List<String> recommend(String ruleName, String rule, String property, String learner)
            throws SWRLParseException, SWRLBuiltInException {
        model.applyRule(ruleName, rule);
        return updatedEntities().extractIndividualsBy(learner, property, manager);
    }



    private OntologyEntities updatedEntities(){ return new OntologyEntities(manager);}

}

