package server.owl;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.SimpleIRIMapper;
import org.swrlapi.exceptions.SWRLBuiltInException;
import org.swrlapi.parser.SWRLParseException;
import server.utils.OutputToJson;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static server.utils.PathadoraConfig.OntologyConfig.*;

public class PathadoraManager {

    private OWLOntologyManager manager;
    private OWLOntology pathadora;

    public PathadoraManager() throws OWLOntologyCreationException, OWLOntologyStorageException {
        initialize();
    }

    private void initialize() throws OWLOntologyCreationException, OWLOntologyStorageException {
        this.manager = OWLManager.createOWLOntologyManager();
        manager.getIRIMappers().add(new SimpleIRIMapper(IRI.create(LOM_RESOURCE), IRI.create(LOM_LOCAL_PATH)));
        manager.getIRIMappers().add(new SimpleIRIMapper(IRI.create(ACC_RESOURCE), IRI.create(ACC_LOCAL_PATH)));
        manager.getIRIMappers().add(new SimpleIRIMapper(IRI.create(ESO_RESOURCE), IRI.create(ESO_LOCAL_PATH)));

        manager.loadOntologyFromOntologyDocument(new File(LOM_LOCAL_PATH));
        manager.loadOntologyFromOntologyDocument(new File(ACC_LOCAL_PATH));
        manager.loadOntologyFromOntologyDocument(new File(ESO_LOCAL_PATH));
        pathadora = manager.loadOntologyFromOntologyDocument(new File(PATHADORA_LOCAL_PATH));

        IRI iri = pathadora.getOntologyID().getOntologyIRI().get();

        OWLDataFactory factory = manager.getOWLDataFactory();
        OWLImportsDeclaration importDeclaration1 = factory.getOWLImportsDeclaration(IRI.create(ACC_RESOURCE));
        OWLImportsDeclaration importDeclaration2 = factory.getOWLImportsDeclaration(IRI.create(LOM_RESOURCE));
        OWLImportsDeclaration importDeclaration3 = factory.getOWLImportsDeclaration(IRI.create(ESO_RESOURCE));

        AddImport addImport1 = new AddImport(pathadora, importDeclaration1);
        AddImport addImport2 = new AddImport(pathadora, importDeclaration2);
        AddImport addImport3 = new AddImport(pathadora, importDeclaration3);

        manager.saveOntology(pathadora);

        System.out.println("Pathadora: " + pathadora.getAxioms().size());
    }

    public void addIndividual(Map<String, String> params) throws OWLOntologyCreationException, OWLOntologyStorageException, IOException {
        Inserter indInserter = new Inserter(this);
        switch (params.get(TYPE)) {
            case LEARNER:
                indInserter.addLearner(params);
                break;
            case COURSE:
                indInserter.addCourse(params);
                break;
            case LESSON:
                indInserter.addLesson(params);
                break;
            default:
                break;
        }
    }

    public String recommendFacDep(Map<String, String> params) throws OWLOntologyCreationException, JsonProcessingException, SWRLParseException, SWRLBuiltInException, OWLOntologyStorageException {
        Recommender rec = new Recommender(this);
        String learner = params.get("learner");
        System.out.println("leaner: " + learner);

        if(params.containsValue(FAC_DEP_GENERATION)){
            Map<String, List<String>> output = rec.recommendedDepartments(learner, "recommendedDepartment");
            System.out.println("output : " +output.size());
            return OutputToJson.facDepJsonResponse(learner, output);
        }else{
            System.out.println("Error on action");
            return "Error on action";
        }
    }

    public void recommendCourses(Map<String, String> params){
        Recommender rec = new Recommender(this);
    }

    public void recommendResources(Map<String, String> params){
        Recommender rec = new Recommender(this);
    }

    public OWLOntology pathadoraOnt() throws OWLOntologyCreationException {
        return this.pathadora;
        // return manager.loadOntologyFromOntologyDocument(new File(PATHADORA_LOCAL_PATH));
    }

    public OWLOntologyManager getManager() {
        return manager;
    }

    public static void main(String... args) throws OWLOntologyCreationException, OWLOntologyStorageException {
        System.out.println("Pathadora manager");
        PathadoraManager manager = new PathadoraManager();

    }

}
