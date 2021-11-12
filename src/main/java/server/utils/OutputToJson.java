package server.utils;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

import static server.utils.PathadoraConfig.OntologyConfig.*;


public class OutputToJson {


    public static String facultiesJsonResponse(String learner, Map<String, List<String>> data) {
        return parseFaculties(learner, data);
    }

    public static String coursesJsonResponse(List<Map<String, String>> data) {
        return parseCourseValues(data, data.get(0));
    }

    public static String resourcesJsonResponse(List<Map<String, String>> data) {
        return parseResources(data);
    }


    private static String parseFaculties(String learner, Map<String, List<String>> data) {
        JSONObject facultiesInfo = new JSONObject();
        facultiesInfo.put(LEARNER, learner);

        JSONArray dataArray = new JSONArray();

        data.forEach((key, value) -> {
            JSONObject facOfDepartment = new JSONObject();
            facOfDepartment.put(DEPARTMENT, key);

            facOfDepartment.put(FACULTIES, value);
            dataArray.put(facOfDepartment);
            facultiesInfo.put(DATA, dataArray);
        });

        return facultiesInfo.toString();
    }


    private static String parseCourseValues(List<Map<String, String>> data, Map<String, String> learnerData) {
        JSONObject courseInfo = new JSONObject();
        learnerData.forEach(courseInfo::put);

        JSONArray courses = new JSONArray();

        data.forEach(course -> {
            JSONObject courseData = new JSONObject();
            course.forEach(courseData::put);
            courses.put(courseData);
        });

        courseInfo.put(COURSES, courses);
        return courseInfo.toString();
    }


    private static String parseResources(List<Map<String, String>> data) {
        JSONObject resourcesRec = new JSONObject();
        JSONArray resources = new JSONArray();

        data.forEach(course -> {
            JSONObject resourceData = new JSONObject();
            course.forEach(resourceData::put);
            resources.put(resourceData);
        });

        resourcesRec.put(RESOURCES, resources);
        return resourcesRec.toString();
    }


    public static void main(String... args) throws JsonProcessingException {
        Map<String, List<String>> result = new HashMap<>();
        result.put("Department_The_Arts", Arrays.asList(
                "Faculty_DAR_Disciplines_of_music_and_theater",
                "Faculty_DAR_Visual_arts", "Faculty_DAR_Information_culture_and_media_organization",
                "Faculty_DAR_Cinema_Television_and_multimedia_production" ,
                "Faculty_DAR_Design_and_management_of_audiovisual_and_performative_production_systems"));

        result.put("Department_Classical_Philology", Arrays.asList(
                "Faculty_FILCOM_Communication_Sciences",
                "Faculty_FILCOM_Semiotics", "Faculty_FICLIT_Letters",
                "Faculty_FILCOM_Philosophical_Sciences" ,
                "Faculty_FICLIT_Philology_literature_and_classical_tradition"));

        System.out.println(facultiesJsonResponse("sokol", result));
    }

}
