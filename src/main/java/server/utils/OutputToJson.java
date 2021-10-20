package server.utils;

import java.util.*;

import static server.utils.ParserUtils.listToJson;

public class OutputToJson {

    public static String facDepJsonResponse(String learner, Map<String, List<String>> data) {
        int indexDep = 0;

        StringBuilder response = new StringBuilder("{\n");
        response.append("'learner': ")
                .append("'"+learner+"'")
                .append(",\n")
                .append("'departments': {\n");

        for (Map.Entry v : data.entrySet()) {
            indexDep++;
            response.append("\t'" + indexDep + "'")
                    .append(":{\n\t\t 'department': ")
                    .append("'" + v.getKey() + "',\n\t\t")
                    .append("'faculties' :\n")
                    .append(listToJson((List<String>) v.getValue()))
                    .append("\n");
        }

        return response.toString().concat("}") ;
    }

    public static String coursesJsonResponse(Map<String, String> output){
        StringBuilder response = new StringBuilder("{\n");
        response.append("'learner': 'ToBeControlled',\n")
                .append("'degree': 'ToBeControlled',\n")
                .append("'department': 'Agricultural and Food Sciences',\n")
                .append("'faculty': 'Agricultural Technology'\n")
                .append("'year': 'I'\n")
                .append("'courses': {\n");

        for(int i=0; i< output.size(); i++){
            response.append("\t\t'" + i + "': {\n");
            for (Map.Entry courseInfo : output.entrySet()) {
                response.append("\t\t\t'" + courseInfo.getKey() + "': '"+courseInfo.getValue()+"',\n");
            }
            response.setLength(response.length()-2);
            response.append("\n\t\t},\n");
        }
        response.setLength(response.length()-2);
        return response.toString().concat("\n}");
    }

    public void resourcesJsonResponse(){}

    public static void main(String... args){
        Map<String, String> courseData = new HashMap<>();
        courseData.put("course", "Chemistry");
        courseData.put("period", "I");
        courseData.put("type", "basic learning activities");
        courseData.put("scientific area", "CHIM/03");

        System.out.println("Output: \n"+ coursesJsonResponse(courseData));

    }

}
