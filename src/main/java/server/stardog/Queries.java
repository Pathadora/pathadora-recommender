package server.stardog;

public class Queries {

    public static final String PREFIXES =
            "PREFIX dc: <http://purl.org/dc/elements/1.1/>                                                          \n"+
            "PREFIX dct: <http://purl.org/dc/terms/>                                                                \n"+
            "PREFIX GenericOntology: <http://160.40.50.89/Accessible_Ontology/Version5.1/OWLs/GenericOntology.owl#> \n"+
            "PREFIX owl: <http://www.w3.org/2002/07/owl#>                                                           \n"+
            "PREFIX pathadora-ontology: <http://www.semanticweb.org/learning-path/pathadora-ontology#>              \n"+
            "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>                                              \n"+
            "PREFIX terms: <http://purl.org/dc/terms#>                                                              \n"+
            "PREFIX xml: <http://www.w3.org/XML/1998/namespace>                                                     \n"+
            "PREFIX accessible_ocw_ontology: <http://purl.org/accessible_ocw_ontology#>                             \n"
            ;


    public static String individualsByClass(String prefix, String indClass){ return PREFIXES +
                    "SELECT ?individual \n" +
                    "WHERE { \n"+
                    "  ?individual rdf:type/rdfs:subClassOf* "+prefix+":"+indClass+" . \n"+
                    "}\n" +
                    "LIMIT 100\n";
    }


}
