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

    public Map<String, List<String>> recommendedDepartments(String individual, String property) throws OWLOntologyCreationException, OWLOntologyStorageException, SWRLParseException, SWRLBuiltInException {
        new RuleBasedModel(manager).applyFacultiesAndDepartmentsRule();

        OntologyEntities entities = new OntologyEntities(manager);
        OWLOntology ontology = manager.pathadoraOnt();
        OWLObjectProperty objProperty = (OWLObjectProperty) entities.ontologyEntitiesBy(OBJECT_PROPERTIES, property);
        OWLNamedIndividual namedInd = (OWLNamedIndividual) entities.ontologyEntitiesBy(INDIVIDUALS, individual);

        //Collection<OWLIndividual> ind = Searcher.values(manager.pathadoraOnt().getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION), recommendedDepartment);

        Map<String, List<String>> output = new HashMap<>();
        OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
        OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
        Set<OWLNamedIndividual> values = reasoner.getObjectPropertyValues(namedInd, objProperty).getFlattened();

        for (OWLNamedIndividual value : values) {
            System.out.println("value " + value.toString());
            output.put(value.toString().split("#")[1], recommendedFaculties());
        }

        return output;
    }

    public Map<String, String> recommendCourses(){
        Map<String, String> courseData = new HashMap<>();
        courseData.put("course", "Chemistry");
        courseData.put("period", "I");
        courseData.put("type", "basic learning activities");
        courseData.put("scientific area", "CHIM/03");

        return courseData;
    }

    private List<String> recommendedFaculties(){
        List<String> faculties = new ArrayList<>();
        faculties.add("Faculty 1 Random");
        faculties.add("Faculty 2 Random");
        faculties.add("Faculty 3 Random");
        faculties.add("Faculty 4 Random");

        return faculties;
    }

}
