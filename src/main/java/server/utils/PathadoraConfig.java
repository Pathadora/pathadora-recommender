package server.utils;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Utils class to help on server configuration and handling the requests.
 */
public class PathadoraConfig {

    public static class OntologyConfig {
        public final static String PATHADORA_TEMP_LOCAL_PATH = "./src/main/resources/ont/pathadora-ontology-temp.owl";
        public final static String PATHADORA_LOCAL_PATH = "./src/main/resources/ont/pathadora-ontology.owl";
        public final static String LOM_LOCAL_PATH = "./src/main/resources/ont/lom_ontology_ods.owl";
        public final static String ACC_LOCAL_PATH = "./src/main/resources/ont/AccessibleOCW.owl";
        public final static String ESO_LOCAL_PATH = "./src/main/resources/ont/eso.ttl";

        public final static String LOM_RESOURCE = "http://data.opendiscoveryspace.eu/lom_ontology_ods.owl";
        public final static String ACC_RESOURCE = "http://purl.org/accessible_ocw_ontology";
        public final static String ESO_RESOURCE = "http://purl.org/ontology/edu/eso";
        //public final static String PATHADORA_RESOURCE = "http://www.semanticweb.org/learning-path/pathadora-ontology/1.0";
        public final static String PATHADORA_RESOURCE = "http://www.semanticweb.org/learning-path/pathadora-ontology";
        public final static String PATHADORA_TEMP_RESOURCE = "http://www.semanticweb.org/learning-path/pathadora-ontology-temp";

        /** TODO: to be discussed */
        public static final String ACTION = "action";
        public static final String ADD = "add";
        public static final String FACULTIES_GENERATION = "faculties_generation";
        public static final String COURSE_GENERATION = "course_generation";
        public static final String RESOURCE_GENERATION = "resource_generation";
        public static final String FINISH = "finish";

        public static final String TYPE = "type";
        public static final String LEARNER = "learner";
        public static final String COURSE = "course";
        public static final String LESSON = "lesson";
        public static final String FACULTY = "faculty";
        public static final String ID = "id";
        public static final String DATA = "data";
        public static final String DEGREE = "degree";
        public static final String SCHOOL = "school";
        public static final String DEPARTMENT = "department";
        public static final String FACULTIES = "faculties";
        public static final String COURSES = "courses";
        public static final String YEAR = "year";

        public static final String OBJECT_PROPERTIES = "object_properties";
        public static final String DATA_PROPERTIES = "data_properties";
        public static final String ANNOTATION_PROPERTIES = "annotation_properties";
        public static final String DATA_TYPES = "data_types";
        public static final String INDIVIDUALS = "individuals";
        public static final String CLASSES = "classes";
        public static final String CLASS = "class";

        public static final String DEPARTMENT_PROPERTY = "recommendedDepartment";
        public static final String FACULTY_PROPERTY = "recommendedFaculty";
        public static final String COURSE_PROPERTY = "recommendedCourses";
        public static final String DEPARTMENT_OF_PROPERTY = "departmentHasFaculty";



    }

    public static class ServerConfig {
        public static final String HOSTNAME = "localhost";
        public static final int PORT = 8080;
        public static final int BACKLOG = 1;

        public static final String HEADER_ALLOW = "Allow";
        public static final String HEADER_CONTENT_TYPE = "Content-Type";

        public static final Charset CHARSET = StandardCharsets.UTF_8;

        public static final int STATUS_OK = 200;
        public static final int STATUS_ERROR = 404;
        public static final int STATUS_METHOD_NOT_ALLOWED = 405;

        public static final int NO_RESPONSE_LENGTH = -1;

        public static final String METHOD_GET = "GET";
        public static final String METHOD_POST = "POST";
        public static final String METHOD_OPTIONS = "OPTIONS";
        public static final String ALLOWED_METHODS = METHOD_GET + "," + METHOD_OPTIONS;


        public static Headers setRequestHeaders(HttpExchange exchange){
            Headers headers = exchange.getResponseHeaders();
            headers.set(HEADER_ALLOW, ALLOWED_METHODS);
            headers.add("Access-Control-Allow-Origin", "*");
            headers.add("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization, x-auth-token");
            headers.add("Access-Control-Allow-Credentials", "true");
            headers.add("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS,HEAD");
            return headers;
        }
    }

    public static String classAssertion(String individual, String iClass){
        return new StringBuilder()
                .append("<owl:NamedIndividual rdf:about=\""+individual+"\">\n")
                .append("<rdf:type rdf:resource=\""+iClass+"\"/>\n")
                .toString();
    }

    public static String property(String propName, String val){
        return "<pathadora-ontology:"+ propName + " rdf:resource=\""+val+"\"/>\n";
    }

    public static String annotation(String annName, String val){
        return "<pathadora-ontology:"+ annName + ">"+ val +"</pathadora-ontology:"+annName+">\n";
    }

    public static String endIndividualDeclaration(){
        return " </owl:NamedIndividual>";
    }

}
