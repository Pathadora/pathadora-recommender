package server.owl;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.util.SimpleIRIMapper;
import org.swrlapi.exceptions.SWRLBuiltInException;
import org.swrlapi.parser.SWRLParseException;
import server.stardog.GraphDatabase;
import server.stardog.StardogDatabase;
import org.semanticweb.owlapi.model.*;

import server.utils.OutputToJson;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static server.utils.PathadoraConfig.OntologyConfig.*;
import static server.utils.PathadoraConfig.ServerConfig.*;

public class PathadoraManager {
    private OWLOntologyManager manager;
    private OWLOntology pathadora;
    private final GraphDatabase database;


    public PathadoraManager() throws OWLOntologyCreationException, OWLOntologyStorageException {
        this.database = new StardogDatabase();
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

        savePathadoraOntology(pathadora, false);
    }


    public String addIndividual(Inserter inserter, Map<String, String> params) throws OWLOntologyStorageException {
        inserter.addNewIndividual(params);
        return String.valueOf(STATUS_OK);
    }


    public String recommendFacAndDep(Recommender recommender, Map<String, String> params)
            throws SWRLParseException, SWRLBuiltInException, OWLOntologyStorageException {
        String learner = params.get(LEARNER);
        String degree = params.get(DEGREE);
        Map<String, List<String>> output = recommender.recommendedFaculties(learner, degree);
        return OutputToJson.facultiesJsonResponse(learner, output);
    }


    public String recommendCourses(Recommender recommender, Map<String, String> params) throws IOException {
        initializeStardogDatabase(PATHADORA_TEMP_LOCAL_PATH);

        String learner = params.get(LEARNER);
        String degree = params.get(DEGREE);
        String year = params.get(YEAR);
        String faculty =params.get(FACULTY);

        Map<String, String> learnerData = new HashMap<>();
        learnerData.put(LEARNER, learner);
        learnerData.put(DEGREE,degree);
        learnerData.put(YEAR, year);
        learnerData.put(FACULTY, faculty);

        List<Map<String,String>>  result = recommender.recommendedCourses(learner, faculty, year, database);
        result.add(0, learnerData);

        return OutputToJson.coursesJsonResponse(result);
    }


    public String recommendResources(Recommender recommender, Map<String, String> params) {
        Map<String, String> learnerData = new HashMap<>();

        String learner = params.get(LEARNER);
        String course =params.get(FACULTY);

        learnerData.put(LEARNER, learner);
        learnerData.put(COURSE, course);

        List<Map<String,String>>  result = recommender.recommendResources(learner, database);
        result.add(0, learnerData);
        return OutputToJson.resourcesJsonResponse(result);
    }


    public void savePathadoraOntology(OWLOntology updatedOnt, boolean temporary) throws OWLOntologyStorageException {
        if (temporary) {
            OWLDocumentFormat format = manager.getOntologyFormat(updatedOnt);
            assert format != null;
            manager.saveOntology(updatedOnt, format, IRI.create(new File(PATHADORA_TEMP_LOCAL_PATH).toURI()));
        } else {
            manager.saveOntology(updatedOnt);
        }
    }

    private void initializeStardogDatabase(String owlFile) throws IOException {
        database.importData(owlFile);
        Files.deleteIfExists(new File(owlFile).toPath());
    }


    public OWLOntologyManager getManager() { return manager;
    }


    public OWLOntology pathadoraOnt() {
        return pathadora;
    }

}
