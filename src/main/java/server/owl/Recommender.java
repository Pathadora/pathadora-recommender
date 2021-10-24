package server.owl;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.swrlapi.exceptions.SWRLBuiltInException;
import org.swrlapi.parser.SWRLParseException;

import java.util.*;

import static server.utils.PathadoraConfig.OntologyConfig.*;

public class Recommender {

    final private PathadoraManager manager;

    public Recommender(PathadoraManager m) {
        this.manager = m;
    }


    public Map<String, List<String>> recommendedFacultiesAndDepartments(String individual) throws OWLOntologyCreationException, OWLOntologyStorageException, SWRLParseException, SWRLBuiltInException {
        RuleBasedModel model = new RuleBasedModel(manager);
        Map<String, List<String>> output = new HashMap<>();

        List<String> faculties = recommendedFaculties(model, individual);

        for(String fac : faculties){
            System.out.println("Faculty: " + fac);
            List<String> departments =  recommendedDepartmentsOfFaculty(model, individual, fac);
            output.put(fac, departments);
        }

        return output;
    }



    private List<String> recommendedFaculties(RuleBasedModel model, String learner) throws OWLOntologyCreationException, SWRLParseException, SWRLBuiltInException, OWLOntologyStorageException {
        model.applyRule("Faculties", Rules.facultyRule());

        Set<OWLNamedIndividual> values = extractIndividualsBy(learner, "recommendedFaculty");
        List<String> output = new ArrayList<>();
        for (OWLNamedIndividual value : values) {
            System.out.println("value " + value.toString());
            output.add(value.toString().split("#")[1].replaceFirst(".$","")); // remove last char >
        }
        return output;
    }


    private List<String> recommendedDepartmentsOfFaculty(RuleBasedModel model, String learner, String faculty) throws SWRLParseException, OWLOntologyCreationException, SWRLBuiltInException, OWLOntologyStorageException {
        model.applyRule("Departments", Rules.departmentRule(learner, faculty));

        Set<OWLNamedIndividual> values = extractIndividualsBy(learner, "recommendedDepartment");
        List<String> output = new ArrayList<>();
        for (OWLNamedIndividual value : values) {
            System.out.println("value " + value.toString());
            output.add(value.toString().split("#")[1].replaceFirst(".$",""));
        }
        return output;
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
