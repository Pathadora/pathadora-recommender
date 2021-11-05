package server.utils;

import java.util.*;


public class OutputToJson {

    public static String facDepJsonResponse(String learner, Map<String, List<String>> data) {
        int indexDep = 0;

        StringBuilder response = new StringBuilder("{\n");
        response.append("\"learner\": ")
                .append("\""+learner+"\"")
                .append(",\n")
                .append("\"data\": {\n");

        for (Map.Entry v : data.entrySet()) {
            indexDep++;
            response.append("\t\"" + indexDep + "\"")
                    .append(":{\n\t\t\"department\": ")
                    .append("\"" + v.getKey() + "\",\n\t\t")
                    .append("\"faculties\" :{\n")
                    .append(listToJson((List<String>) v.getValue()))
                    .append("},\n");
        }
        response.setLength(response.length()-2);
        return response.toString().concat("}\n}") ;
    }

    public static String coursesJsonResponse(Map<String, String> output){
        StringBuilder response = new StringBuilder("{\n");
        String course = output.get("course");

        response.append("\"learner\": \"ToBeControlled\",\n")
                .append("\"degree\": \"ToBeControlled\",\n")
                .append("\"department\": \"Agricultural and Food Sciences\",\n")
                .append("\"faculty\": \""+course+"\"\n")
                .append("\"year\": \"I\"\n")
                .append("\"courses\": {\n");

        for(int i=0; i< output.size(); i++){
            response.append("\t\t\"" + i + "\": {\n");
            for (Map.Entry courseInfo : output.entrySet()) {
                response.append("\t\t\"" + courseInfo.getKey() + "\": \""+courseInfo.getValue()+"\",\n");
            }
            response.setLength(response.length()-2);
            response.append("\n\t\t},\n");
        }
        response.setLength(response.length()-2);
        return response.toString().concat("\n}\n}");
    }

    public void resourcesJsonResponse(){}

    public static String listToJson(List<String> list){
        StringBuilder builder = new StringBuilder("");
        for(int i=0; i<list.size(); i++){
            builder.append("\t\t\t\t\t\""+i+"\": ").append("\""+list.get(i)+"\",\n");
        }
        String res = builder.substring(0, builder.length() - 5).concat("\"\n\t\t}");
        return  res;
    }


    public static void main(String... args){
        Map<String, String> courseData = new HashMap<>();
        courseData.put("course", "Chemistry");
        courseData.put("period", "I");
        courseData.put("type", "basic learning activities");
        courseData.put("scientific area", "CHIM/03");

        System.out.println("Output: \n"+ coursesJsonResponse(courseData));
    }

}
