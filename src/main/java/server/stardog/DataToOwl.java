package server.stardog;

import java.io.FileWriter;
import java.io.IOException;

public class DataToOwl {

    public static final String TMP_FILE = "newIndividuals";

    public static final String docOpener =
      "<?xml version=\"1.0\"?> \n"+
            "<rdf:RDF xmlns=\"http://www.semanticweb.org/sokol/ontologies/2021/7/untitled-ontology-24#\" \n"+
            "xml:base=\"http://www.semanticweb.org/sokol/ontologies/2021/7/untitled-ontology-24\" \n"+
            "xmlns:dc=\"http://purl.org/dc/elements/1.1/\" \n"+
            "xmlns:ns=\"http://www.w3.org/2006/vcard/ns#\" \n"+
            "xmlns:ns1=\"http://creativecommons.org/ns#\" \n"+
            "xmlns:owl=\"http://www.w3.org/2002/07/owl#\" \n"+
            "xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" \n"+
            "xmlns:xml=\"http://www.w3.org/XML/1998/namespace\" \n"+
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" \n"+
            "xmlns:owl1=\"http://www.semanticweb.org/learning-path/pathadora-ontology#owl:\" \n"+
            "xmlns:prov=\"http://www.w3.org/ns/prov#\" \n"+
            "xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" \n"+
            "xmlns:skos=\"http://www.w3.org/2004/02/skos/core#\" \n"+
            "xmlns:swrl=\"http://www.w3.org/2003/11/swrl#\" \n"+
            "xmlns:swrla=\"http://swrl.stanford.edu/ontologies/3.3/swrla.owl#\" \n"+
            "xmlns:swrlb=\"http://www.w3.org/2003/11/swrlb#\" \n"+
            "xmlns:terms=\"http://purl.org/dc/terms/\" \n"+
            "xmlns:GenericOntology=\"http://160.40.50.89/Accessible_Ontology/Version5.1/OWLs/GenericOntology.owl#\" \n"+
            "xmlns:pathadora-ontology=\"http://www.semanticweb.org/learning-path/pathadora-ontology#\" \n"+
            "xmlns:pathadora-ontology1=\"http://www.semanticweb.org/learning-path/pathadora-ontology/1.0#\" \n"+
            "xmlns:accessible_ocw_ontology=\"http://purl.org/accessible_ocw_ontology#\"> \n"+
        "<owl:Ontology rdf:about=\"http://www.semanticweb.org/learning-path/pathadora-ontology\"> \n"+
            "<owl:versionIRI rdf:resource=\"http://www.semanticweb.org/learning-path/pathadora-ontology/1.0\"/> \n"+
            "<owl:imports rdf:resource=\"http://data.opendiscoveryspace.eu/lom_ontology_ods.owl#\"/> \n"+
            "<owl:imports rdf:resource=\"http://purl.org/accessible_ocw_ontology\"/> \n"+
            "<owl:imports rdf:resource=\"http://purl.org/ontology/edu/eso/0.9\"/> \n"+
        "</owl:Ontology>";

    public static final String docCloser =  "\n</rdf:RDF>";


    public static void toOWLFile(String docContent) throws IOException {
        FileWriter writer = new FileWriter("newIndividuals");
        writer.append(docOpener);
        writer.append(docContent);
        writer.append(docCloser);
        writer.close();
    }
}
