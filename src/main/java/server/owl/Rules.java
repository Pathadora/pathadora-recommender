package server.owl;

public class Rules {

    public static String facultyRule() { return
            "accessible_ocw_ontology:Learner(?learner) ^ " +
                    "pathadora-ontology:passionateOf(?learner, ?passion) ^ " +
                    "pathadora-ontology:facultyArea(?faculty, ?facArea) ^ " +
                    "pathadora-ontology:passionArea(?passion, ?facArea) -> " +
                    "pathadora-ontology:recommendedFaculty(?learner, ?faculty)";
    }

    public static String departmentRule(String learner, String faculty){ return
                    "pathadora-ontology:facultyHasDepartment(pathadora-ontology:"+faculty+", ?depart) " +
                    "-> pathadora-ontology:recommendedDepartment(pathadora-ontology:"+learner+", ?depart)";
    }


    public static String COURSES_RULE = "";

}
