package server.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

import static server.utils.PathadoraConfig.OntologyConfig.*;


public class OutputToJson {

    public static String facultiesJsonResponse(String learner, Map<String, List<String>> data) {
        return parseFaculties(learner, data);
    }


    public static String coursesJsonResponse(List<Map<String, String>> data) {
        return parseCourseValues(data);
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


    private static String parseCourseValues(List<Map<String, String>> data) {
        JSONObject courseInfo = new JSONObject();
        data.get(0).forEach(courseInfo::put);

        JSONArray courses = new JSONArray();

        data.subList(1, data.size()).forEach(course -> {
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

}
