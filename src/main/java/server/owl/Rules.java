package server.owl;

public class Rules {

    public static String schoolRule() { return
            "accessible_ocw_ontology:Learner(?learner) ^ " +
                    "pathadora-ontology:passionateOf(?learner, ?passion) ^ " +
                    "pathadora-ontology:passionArea(?passion, ?sArea) ^ " +
                    "pathadora-ontology:schoolArea(?school, ?sArea) -> " +
                    "pathadora-ontology:recommendedSchool(?learner, ?school)";
    }

    public static String departmentRule(String learner, String school){ return
            "pathadora-ontology:recommendedSchool(pathadora-ontology:"+learner+", ?school) ^ " +
                    "pathadora-ontology:schoolHasDepartment(pathadora-ontology:"+school+", ?depart) " +
                    "-> pathadora-ontology:recommendedDepartment(pathadora-ontology:"+learner+", ?depart)";
    }

    public static String departmentRule(){ return
            "accessible_ocw_ontology:Learner(?learner) ^ " +
                    "pathadora-ontology:recommendedSchool(?learner, ?school) ^ " +
                    "pathadora-ontology:schoolHasDepartment(?school, ?depart) -> " +
                    "pathadora-ontology:recommendedDepartment(?learner, ?depart)";
    }


    public static String recommendedFaculties(String learner, String department, String degree){
        String degreeProperty = "";
        if(degree.contains("Bachelor")){ degreeProperty = "isBachelorFaculty";}
        if(degree.contains("Master")){ degreeProperty = "isMasterFaculty";}
        if(degree.contains("Doctoral")){ degreeProperty = "isDoctoralFaculty";}

        return
            "pathadora-ontology:recommendedDepartment(?learner, pathadora-ontology:"+department+") ^ " +
                    "pathadora-ontology:facultyOfDepartment(?fac, ?depart) ^  " +
                    "pathadora-ontology:"+degreeProperty+"(?fac, pathadora-ontology:"+degree+") -> " +
                     "pathadora-ontology:recommendedFaculty(pathadora-ontology:"+learner+", ?fac)";
    }

    public static String recommendedFaculties(){ return
            "accessible_ocw_ontology:Learner(?learner) ^ " +
                    "pathadora-ontology:recommendedDepartment(?learner, ?dep) ^ " +
                    "pathadora-ontology:facultyOfDepartment(?fac, ?depart) -> " +
                    "pathadora-ontology:recommendedFaculty(?learner, ?fac)";
    }

    public static String coursesRule(String learner, String faculty, String degree, String year){ return
        "";
    }

}
