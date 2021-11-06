package server.owl;

import org.semanticweb.owlapi.model.*;
import server.stardog.StardogDatabase;
import server.stardog.StardogRunnable;

import java.io.IOException;
import java.util.Map;

import static server.utils.ParserUtils.*;
import static server.utils.PathadoraConfig.OntologyConfig.*;
import static server.utils.PathadoraConfig.ServerConfig.STATUS_OK;

public class Inserter {

    final private PathadoraManager manager;
    final private OWLOntologyManager ontManager;
    final private StardogDatabase database;


    public Inserter(PathadoraManager m, StardogDatabase db) {
        this.manager = m;
        this.ontManager = m.getManager();
        this.database = db;
    }

    public String addLearner(Map<String, String> params) throws OWLOntologyStorageException, IOException {
        OntologyEntities entities = new OntologyEntities(manager);
        OWLOntology pathadora = manager.pathadoraOnt();

        Map<String, String> obj_prop = paramsToMapByKey(OBJECT_PROPERTIES, params);
        Map<String, String> ann_prop = paramsToMapByKey(ANNOTATION_PROPERTIES, params);

        OWLNamedIndividual tIndividual = (OWLNamedIndividual) entities.ontologyEntitiesBy(INDIVIDUALS, label(ann_prop));

        String caOwl  = entities.defineClassAssertion(params, tIndividual, pathadora);
        String opaOwl = entities.defineObjectPropertyAssertions(obj_prop, tIndividual, pathadora);
        String apaOwl = entities.defineAnnotationPropertyAssertions(ann_prop, tIndividual, pathadora);

        ontManager.saveOntology(pathadora);

       /* StardogRunnable stardog = new  StardogRunnable(database);
        new Thread(() -> {
            try {stardog.database().insertData(caOwl+opaOwl+apaOwl);
            } catch (IOException e) {e.printStackTrace();}
        }).start();
        */

        return String.valueOf(STATUS_OK);
    }


    public String addResource(Map<String, String> p) { return String.valueOf(STATUS_OK); }

    private String label(Map<String, String> properties) { return properties.get(ID); }
}

