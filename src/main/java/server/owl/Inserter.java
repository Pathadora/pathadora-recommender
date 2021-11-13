package server.owl;

import org.semanticweb.owlapi.model.*;

import java.util.Map;

import static server.utils.ParserUtils.*;
import static server.utils.PathadoraConfig.OntologyConfig.*;
import static server.utils.PathadoraConfig.ServerConfig.STATUS_OK;

public class Inserter {
    final private PathadoraManager manager;

    public Inserter(PathadoraManager m) {
        this.manager = m;
    }

    public String addNewIndividual(Map<String, String> params) throws OWLOntologyStorageException {
        OntologyEntities entities = new OntologyEntities(manager);
        OWLOntology pathadora = manager.pathadoraOnt();

        Map<String, String> obj_prop = paramsToMapByKey(OBJECT_PROPERTIES, params);
        Map<String, String> ann_prop = paramsToMapByKey(ANNOTATION_PROPERTIES, params);
        Map<String, String> data_prop = paramsToMapByKey(DATA_PROPERTIES, params);

        OWLNamedIndividual tIndividual = (OWLNamedIndividual) entities.ontologyEntitiesBy(INDIVIDUALS, label(ann_prop));

        String caOwl  = entities.defineClassAssertion(params, tIndividual, pathadora);
        String opaOwl = entities.defineObjectPropertyAssertions(obj_prop, tIndividual, pathadora);
        String apaOwl = entities.defineAnnotationPropertyAssertions(ann_prop, tIndividual, pathadora);
        String dpaPwl = entities.defineDataPropertyAssertions(data_prop, tIndividual, pathadora);

        manager.savePathadoraOntology(pathadora, false);
        return String.valueOf(STATUS_OK);
    }


    private String label(Map<String, String> properties) { return properties.get(ID); }
}

