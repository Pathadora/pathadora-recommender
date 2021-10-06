package server.owl;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.SimpleIRIMapper;

import java.io.File;

import static server.utils.PathadoraUtils.OntologyConfig.*;

public class OntologyLoader {

    private OWLOntologyManager ontologyManager;
    private OWLOntology lomOnto;
    private OWLOntology accOnto;
    private OWLOntology pathadora;

    public OntologyLoader() throws OWLOntologyCreationException {
        initialize();
    }


    private void initialize() throws OWLOntologyCreationException {
        this.ontologyManager= OWLManager.createOWLOntologyManager();

        ontologyManager.getIRIMappers().add(new SimpleIRIMapper(IRI.create(LOM_RESOURCE), IRI.create(LOM_LOCAL_PATH)));
        ontologyManager.getIRIMappers().add(new SimpleIRIMapper(IRI.create(ACC_RESOURCE), IRI.create(ACC_LOCAL_PATH)));

        this.lomOnto = ontologyManager.loadOntologyFromOntologyDocument(new File(LOM_LOCAL_PATH));
        this.accOnto = ontologyManager.loadOntologyFromOntologyDocument(new File(ACC_LOCAL_PATH));
        this.pathadora = ontologyManager.loadOntologyFromOntologyDocument(new File(PATHADORA_LOCAL_PATH));
    }


    public void dataLoader() throws OWLOntologyStorageException {
        OWLDataFactory dataFactory = ontologyManager.getOWLDataFactory();
        OWLImportsDeclaration lomDec = dataFactory.getOWLImportsDeclaration(IRI.create(LOM_RESOURCE));
        OWLImportsDeclaration accDec = dataFactory.getOWLImportsDeclaration(IRI.create(ACC_RESOURCE));

        new AddImport(pathadora, lomDec);
        new AddImport(pathadora, accDec);

        ontologyManager.saveOntology(pathadora);
    }


    public OWLOntologyManager getOntologyManager() {
        return ontologyManager;
    }


    public OWLOntology getPathadora() {
        return pathadora;
    }
}
