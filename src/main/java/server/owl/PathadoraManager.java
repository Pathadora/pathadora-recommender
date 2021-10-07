package server.owl;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.util.SimpleIRIMapper;
import server.utils.Parameters;

import java.io.File;

import static server.utils.PathadoraUtils.OntologyConfig.*;

public class PathadoraManager {

    private OWLOntologyManager manager;
    private OWLOntology lomOnto;
    private OWLOntology accOnto;
    private OWLOntology pathadora;

    public PathadoraManager() throws OWLOntologyCreationException {
        initialize();
    }


    private void initialize() throws OWLOntologyCreationException {
        this.manager = OWLManager.createOWLOntologyManager();

        manager.getIRIMappers().add(new SimpleIRIMapper(IRI.create(LOM_RESOURCE), IRI.create(LOM_LOCAL_PATH)));
        manager.getIRIMappers().add(new SimpleIRIMapper(IRI.create(ACC_RESOURCE), IRI.create(ACC_LOCAL_PATH)));

        this.lomOnto = manager.loadOntologyFromOntologyDocument(new File(LOM_LOCAL_PATH));
        this.accOnto = manager.loadOntologyFromOntologyDocument(new File(ACC_LOCAL_PATH));
        this.pathadora = manager.loadOntologyFromOntologyDocument(new File(PATHADORA_LOCAL_PATH));
    }


    public void dataLoader() throws OWLOntologyStorageException {
        OWLDataFactory dataFactory = manager.getOWLDataFactory();
        OWLImportsDeclaration lomDec = dataFactory.getOWLImportsDeclaration(IRI.create(LOM_RESOURCE));
        OWLImportsDeclaration accDec = dataFactory.getOWLImportsDeclaration(IRI.create(ACC_RESOURCE));

        new AddImport(pathadora, lomDec);
        new AddImport(pathadora, accDec);

        manager.saveOntology(pathadora);
    }

    public void addIndividual(Parameters p) throws OWLOntologyCreationException, OWLOntologyStorageException {
        switch (p.searchByParam("type")) {
            case "LEARNER":
                addLearner(p);
                break;
            case "COURSE":
                addCourse(p);
                break;
            case "LESSON":
                addLesson(p);
                break;
            default:
                break;
        }
    }

    private void addLearner(Parameters p) throws OWLOntologyCreationException, OWLOntologyStorageException {
        OWLOntology pathadora = manager.loadOntologyFromOntologyDocument(new File(PATHADORA_LOCAL_PATH));
        OWLDataFactory factory = manager.getOWLDataFactory();

        PrefixManager pm = new DefaultPrefixManager(PATHADORA_RESOURCE);
        OWLClass learnerClass = factory.getOWLClass("#" + "Learner", pm); //'#' is  delimiter

        // OWLAnnotation  hasName = factory.getOWLAnnotation(
        //       new OWLAnnotationPropertyImpl(IRI.create(PATHADORA_RESOURCE+"#"+"hasName")),
        //      factory.getOWLLiteral(p.searchByParam("name")));
 

        OWLIndividual tIndividual = factory.getOWLNamedIndividual("#" + p.searchByParam("name"), pm);
        OWLClassAssertionAxiom learnerAss = factory.getOWLClassAssertionAxiom(learnerClass, tIndividual);
        //OWLAnnotationAssertionAxiom hasNameAss = factory.getOWLAnnotationAssertionAxiom((OWLAnnotationSubject) tIndividual, hasName);

        manager.addAxiom(pathadora, learnerAss);
        //manager.addAxiom(pathadora, hasNameAss);

        manager.saveOntology(pathadora);
    }

    private void addCourse(Parameters p){}

    private void addLesson(Parameters p){}

    public OWLOntologyManager getManager() {
        return manager;
    }


    public OWLOntology getPathadora() {
        return pathadora;
    }
}
