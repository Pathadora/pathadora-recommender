package server.stardog;

import java.util.HashMap;
import java.util.Map;

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
            "PREFIX lom: <http://data.opendiscoveryspace.eu/lom_ontology_ods.owl#>"                                 ;

    public static Map<String,String> prefixesMap(){
        Map<String,String> prefixes = new HashMap<>();
        prefixes.put("dc", "http://purl.org/dc/elements/1.1/");
        prefixes.put("dct", "http://purl.org/dc/terms/");
        prefixes.put("GenericOntology", "http://160.40.50.89/Accessible_Ontology/Version5.1/OWLs/GenericOntology.owl#");
        prefixes.put("owl", "http://www.w3.org/2002/07/owl#");
        prefixes.put("pathadora-ontology", "http://www.semanticweb.org/learning-path/pathadora-ontology#");
        prefixes.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        prefixes.put("terms", "http://purl.org/dc/terms#");
        prefixes.put("xml", "http://www.w3.org/XML/1998/namespace");
        prefixes.put("accessible_ocw_ontology", "http://purl.org/accessible_ocw_ontology#");
        prefixes.put("lom", "http://data.opendiscoveryspace.eu/lom_ontology_ods.owl#");

        return prefixes;
    }



    public static String courses(String learner, String faculty, String year){ return PREFIXES +
            "SELECT ?course  ?cfu ?ssd ?type ?period ?isObligatory ?language                            \n" +
            "WHERE {                                                                                    \n" +
            "   ?learner a accessible_ocw_ontology:Learner .                                            \n" +
            "   ?learner pathadora-ontology:id \""+learner+"\" .                                        \n" +
            "   ?faculty a pathadora-ontology:Faculties .                                               \n" +
          //"   ?faculty pathadora-ontology:isMasterFaculty pathadora-ontology:yes .                     \n" +
            "   FILTER (regex(str(?faculty), \""+faculty+"\")) .                                        \n" +
            "   ?course_classes rdfs:subClassOf pathadora-ontology:"+getFaculty(faculty)+" .            \n" +
            "   ?course a ?course_classes .                                                             \n" +
            "   ?course pathadora-ontology:isCourseObligatory ?isObligatory .                           \n" +
            "   ?course pathadora-ontology:courseYear \""+year+"\"^^xsd:literal .                       \n" +
            "   ?course pathadora-ontology:courseCFU ?cfu .                                             \n" +
            "   OPTIONAL{?course pathadora-ontology:courseSSD ?ssd} .                                   \n" +
            "   OPTIONAL{?course pathadora-ontology:courseType ?type} .                                 \n" +
            "   OPTIONAL{?course pathadora-ontology:coursePeriod ?period } .                            \n" +
            "   OPTIONAL{?course pathadora-ontology:courseLanguage ?language}                           \n" +
            "}"                                                                                          ;
    }


    public static String resources(String learner){ return PREFIXES +
            "SELECT DISTINCT ?resource ?type ?extension ?readingEase ?contrastRatio ?fontSize           \n" +
            "WHERE {                                                                                    \n" +
            "    ?learner a accessible_ocw_ontology:Learner .                                           \n" +
            "    ?learner pathadora-ontology:id \"Learner_123\" .                                       \n" +
            "    ?learner pathadora-ontology:hasDisability ?disability .                                \n" +
            "    ?disability a pathadora-ontology:Sensory .                                             \n" +
            "    ?disability pathadora-ontology:accessModeForDisability  ?disability_access .           \n" +
            "    ?resource a lom:LearningObject .                                                       \n" +
            "    {?resource accessible_ocw_ontology:AccessMode ?disability_access . }                   \n" +
            "    UNION                                                                                  \n" +
            "    {?resource accessible_ocw_ontology:adaptationType ?disability_access .}                \n" +
            "    ?resource pathadora-ontology:resourceType ?type .                                      \n" +
            "    OPTIONAL {?resource pathadora-ontology:resourceExtension ?extension } .                \n" +
            "    OPTIONAL {?resource pathadora-ontology:resourceReadingEase ?readingEase } .            \n" +
            "    OPTIONAL {?resource pathadora-ontology:resourceContrastRatio ?contrastRatio } .        \n" +
            "    OPTIONAL {?resource pathadora-ontology:resourceFontSize ?fontSize } .                  \n" +
            "    FILTER(?contrastRatio > 15 && ?readingEase > 50) .                                     \n" +
            "}"                                                                                          ;

    }


    private static String getFaculty(String fac){
        String str = fac.replace("Faculty_","");
        return str.substring(str.indexOf("_")+1);
    }
}
