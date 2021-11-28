package server.owl;

public class Rules {

    public static String schoolRule() { return
            "accessible_ocw_ontology:Learner(?learner) ^ " +
                    "pathadora-ontology:passionateOf(?learner, ?passion) ^ " +
                    "pathadora-ontology:passionArea(?passion, ?sArea) ^ " +
                    "pathadora-ontology:schoolArea(?school, ?sArea) -> " +
                    "pathadora-ontology:recommendedSchool(?learner, ?school)";
    }


    public static String departmentRule(){ return
            "accessible_ocw_ontology:Learner(?learner) ^ " +
                    "pathadora-ontology:recommendedSchool(?learner, ?school) ^ " +
                    "pathadora-ontology:schoolHasDepartment(?school, ?depart) -> " +
                    "pathadora-ontology:recommendedDepartment(?learner, ?depart)";
    }


    public static String recommendedFaculties(String learner, String degree){ return
            "pathadora-ontology:Departments(?depart) ^"+
                    "pathadora-ontology:recommendedDepartment(pathadora-ontology:"+learner+", ?depart) ^ " +
                        "pathadora-ontology:facultyOfDepartment(?fac, ?depart) ^  " +
                        "pathadora-ontology:"+degreeProperty(degree)+"(?fac, pathadora-ontology:"+degree+") -> " +
                        "pathadora-ontology:recommendedFaculty(pathadora-ontology:"+learner+", ?fac)";
    }


    private static String degreeProperty(String degree){
        if(degree.contains("Bachelor")){ return  "isBachelorFaculty";}
        if(degree.contains("Master")){   return  "isMasterFaculty";}
        if(degree.contains("Doctoral")){ return  "isDoctoralFaculty";}
        return "isBachelorFaculty";
    }
}
