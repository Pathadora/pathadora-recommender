package server.utils;

import com.fasterxml.jackson.core.JsonProcessingException;

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

    public void coursesJsonResponse(){}

    public void resourcesJsonResponse(){}

}
