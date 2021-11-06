package server.utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

public class ParserUtils {

    public static Map<String, String> paramsToMap(String paramsJson) throws IOException {
        return new ObjectMapper().readValue(paramsJson, Map.class);
    }


    public static Map<String, String> paramsToMapByKey(String key, Map<String, String> params) {
        if (params.containsKey(key)) {
            Object json = params.get(key);
            List<String> dataToList = jsonNestedValuesToList(cleanedJsonString(json.toString()));
            return listToMapByIndex(dataToList);
        }
        return new HashMap<>();
    }


    public static List<String> individualsToList(Set<OWLNamedIndividual> indSet) {
        List<String> output = new ArrayList<>();
        for (OWLNamedIndividual value : indSet) {
            output.add(value.toString().split("#")[1].replaceFirst(".$", ""));
        }
        return output;
    }


    public static String paramsToString(BufferedReader br) throws IOException {
        String line;
        StringBuilder body = new StringBuilder();
        while ((line = br.readLine()) != null) {
            body.append(line);
        }
        return body.toString();
    }


    public static void outputToConsole(String where, Map<String, String> data) {
        System.out.println(where);
        for (Map.Entry e : data.entrySet()) {
            System.out.println("\t\t<" + e.getKey() + ", " + e.getValue() + ">");
        }
        System.out.println("");
    }


    public static String listToJson(List<String> list) {
        StringBuilder builder = new StringBuilder("\t\t{");
        for (int i = 0; i < list.size(); i++) {
            builder.append("\t\t\t\"" + i + "\": ").append("\"" + list.get(i) + "\",\n\t\t\t");
        }
        String res = builder.substring(0, builder.length() - 5).concat("\n\t\t\t}");
        return res;
    }


    /* paramsToMap will produce a map where the values are jsonString. For nested camps, it should
    be extracted by removing special characters and returning an ordered list with keys on even indexes
    and values on even indexes.*/
    private static List<String> jsonNestedValuesToList(String jsonList) {
        String[] pairs = jsonList.split("=");

        List<String> dataToList = new ArrayList<>();
        for (String s : pairs) {
            if (s.contains(",")) {
                String one = s.substring(0, s.lastIndexOf(","));
                String two = s.substring(s.lastIndexOf(","));
                dataToList.add(one);
                dataToList.add(two);
            } else {
                dataToList.add(s);
            }
        }
        return validateValuesList(dataToList);
    }


    /* ordered list will be parsed to map, where keys are found on pair indexes and
    values on even indexes */
    private static Map<String, String> listToMapByIndex(List<String> dataToList) {
        Map<String, String> out = new HashMap<>();
        for (int i = 0; i < dataToList.size(); i++) {
            String key1 = validateString(dataToList.get(i));
            i = i + 1;
            String val1 = validateString(dataToList.get(i));
            out.put(key1, val1);
        }
        return out;
    }

    private static List<String> validateValuesList(List<String> data){
        String last = data.get(data.size()-1);
        if(last.charAt(0) == ','){
            List<String> out = data.subList(0, data.size()-2);
            out.add(data.get(data.size()-2) + data.get(data.size()-1));
            return out;
        }
        return data;
    }

    private static String validateString(String value){
        return (!value.contains("{") || !value.contains("}"))
                ? value.replaceAll("[^a-zA-Z0-9/_]", "")
                : value.replaceAll("[{} ]", "");
    }

    /* remove numeric id of nested camps */
    private static String cleanedJsonString(String jsonS){
        return jsonS.substring( 1, jsonS.length() - 1 ).replaceAll("[0-9]=", "");
    }
}
