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
            "PREFIX accessible_ocw_ontology: <http://purl.org/accessible_ocw_ontology#>                             \n"+
            "PREFIX lom: <http://data.opendiscoveryspace.eu/lom_ontology_ods.owl#>"
            ;


    public static String individualsByClass(String prefix, String indClass){ return PREFIXES +
                    "SELECT ?individual \n" +
                    "WHERE { \n"+
                    "  ?individual rdf:type/rdfs:subClassOf* "+prefix+":"+indClass+" . \n"+
                    "}\n" +
                    "LIMIT 100\n";
    }


    public static String courses(){ return  PREFIXES +
            "\n" +
            "         SELECT  ?course \n" +
            "         (SAMPLE(?year) AS ?year)     \n" +
            "         (SAMPLE(?cfu) AS ?cfu)\n" +
            "         (SAMPLE(?ssd) AS ?ssd)\n" +
            "         (SAMPLE(?type) AS ?type)\n" +
            "         (SAMPLE(?period) AS ?period)\n" +
            "         (SAMPLE(?isObligatory) AS ?isObligatory)\n" +
            "         ?language\n" +
            "\n" +
            "         WHERE {\n" +
            "            {\n" +
            "               SELECT ?department ?faculty ?course_degree \n" +
            "               WHERE {\n" +
            "                  ?learner a accessible_ocw_ontology:Learner .\n" +
            "                  ?learner rdfs:label \"Learner_Luca\" .\n" +
            "   \n" +
            "                  ?school a pathadora-ontology:School .\n" +
            "                  ?learner pathadora-ontology:recommendedSchool ?school .\n" +
            "\n" +
            "                  ?department a pathadora-ontology:Departments .\n" +
            "                  ?school pathadora-ontology:schoolHasDepartment ?department .\n" +
            "  \n" +
            "                  ?faculty pathadora-ontology:facultyOfDepartment ?department .\n" +
            "   \n" +
            "                  ?faculty a ?course_degree .\n" +
            "                  FILTER (regex(str(?course_degree), \"Bachelor\")) .\n" +
            "               }\n" +
            "            }\n" +
            "      \n" +
            "         ?course a ?course_degree .\n" +
            "         ?course pathadora-ontology:isCourseObligatory ?isObligatory .\n" +
            "         ?isObligatory a pathadora-ontology:No .\n" +
            "         \n" +
            "         ?course pathadora-ontology:courseYear ?year .\n" +
            "         ?course pathadora-ontology:courseCFU ?cfu .\n" +
            "         ?course pathadora-ontology:courseSSD ?ssd .\n" +
            "         ?course pathadora-ontology:courseType ?type .\n" +
            "         ?course pathadora-ontology:coursePeriod ?period .\n" +
            "         ?course pathadora-ontology:isCourseObligatory ?isObligatory .\n" +
            "         ?course pathadora-ontology:courseLanguage ?language\n" +
            "      }\n" +
            "   \n" +
            "  GROUP BY ?course ?language\n" +
            "\n" +
            "\n" +
            "\n";
    }


    public static String resources(){ return PREFIXES +
            "SELECT DISTINCT *\n" +
            "WHERE {\n" +
            "    ?resource a lom:LearningObject .\n" +
            "    ?resource pathadora-ontology:resourceType ?type .\n" +
            "    OPTIONAL {?resource pathadora-ontology:resourceExtension ?extension } .\n" +
            "    OPTIONAL {?resource pathadora-ontology:resourceReadingEase ?readingEase } .\n" +
            "    OPTIONAL {?resource pathadora-ontology:resourceCheckRatio ?checkRatio } .\n" +
            "    OPTIONAL {?resource pathadora-ontology:resourceFontSize ?fontSize } .\n" +
            "}";

    }

}
