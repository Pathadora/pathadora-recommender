package server.owl;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.SimpleIRIMapper;
import org.swrlapi.exceptions.SWRLBuiltInException;
import org.swrlapi.parser.SWRLParseException;
import server.utils.OutputToJson;

import java.io.File;
import java.util.List;
import java.util.Map;

import static server.utils.PathadoraConfig.OntologyConfig.*;
import static server.utils.PathadoraConfig.ServerConfig.*;

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

        updatePathadoraOntology(pathadora);
    }

    public String addIndividual(Inserter inserter, Map<String, String> params) throws OWLOntologyStorageException  {
        inserter.addNewIndividual(params);
        return String.valueOf(STATUS_OK);
    }

    public String recommendFacAndDep(Recommender recommender, Map<String, String> params)
            throws SWRLParseException, SWRLBuiltInException, OWLOntologyStorageException {
        String learner = params.get("learner");
        String degree = params.get("degree");
        Map<String, List<String>> output = recommender.recommendedFaculties(learner, degree);
        return OutputToJson.facDepJsonResponse(learner, output);
    }

    public String recommendCourses(Recommender recommender, Map<String, String> params)
            throws SWRLParseException, SWRLBuiltInException, OWLOntologyStorageException {
        String learner = params.get("learner");
        String degree = params.get("degree"); // todo USELESS
        String year = params.get("year");
        String faculty = params.get("faculty");
        Map<String, String> output = recommender.recommendedCourses(learner, faculty, degree, year);
        return OutputToJson.coursesJsonResponse(output);
    }

    public void recommendResources(Map<String, String> params)
            throws SWRLParseException, SWRLBuiltInException, OWLOntologyStorageException {
        //Recommender rec = new Recommender(this);
    }

    public void reset(){ }

    public OWLOntology pathadoraOnt() {
        return pathadora;
    }

    public void updatePathadoraOntology(OWLOntology updatedOnt) throws OWLOntologyStorageException {
        manager.saveOntology(updatedOnt);
    }

    public OWLOntologyManager getManager() { return manager;}

}
