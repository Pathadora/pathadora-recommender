package server.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jsonldjava.shaded.com.google.common.base.Splitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static server.utils.PathadoraConfig.ServerConfig.CHARSET;

public class ParserUtils {

    public static Map<String, String> paramsToMap(String paramsJson) throws IOException {
        return new ObjectMapper().readValue(paramsJson, Map.class);
    }

    public static Map<String, String> paramsToMapByKey(String key, Map<String, String> params) {
        if (params.containsKey(key)) {
            Object jsonS = params.get(key);
            return Splitter
                    .on(",")
                    .withKeyValueSeparator('=')
                    .split(jsonS.toString()
                            .substring(1, jsonS.toString().length() - 1)  // remove { and }
                            .replaceAll("\\s+", "") // clean spaces
                    );
        }
        return new HashMap<>();
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


    /** GET REQUEST
     * Extract the parameters from the URL request and return a map.
     */
    public static Parameters getRequestParameters(final URI requestUri) {
        final Map<String, String> requestParameters = new LinkedHashMap<>();
        final String requestQuery = requestUri.getRawQuery();
        if (requestQuery != null) {
            final String[] rawRequestParameters = requestQuery.split("[&;]", -1);
            for (final String rawRequestParameter : rawRequestParameters) {
                final String[] requestParameter = rawRequestParameter.split("=", 2);
                requestParameters.putIfAbsent(decodeUrlComponent(requestParameter[0]), decodeUrlComponent(requestParameter[1]));
            }
        }
        return new Parameters(requestParameters);
    }

    /**
     * Basic URL decoder
     */
    private static String decodeUrlComponent(final String urlComponent) {
        try {
            return URLDecoder.decode(urlComponent, CHARSET.name());
        } catch (final UnsupportedEncodingException ex) {
            throw new InternalError(ex);
        }
    }

    public static String listToJson(List<String> list){
        StringBuilder builder = new StringBuilder("\t\t{");
        for(int i=0; i<list.size(); i++){
            builder.append("\t\t\t'"+i+"': ").append("'"+list.get(i)+"',\n\t\t\t");
        }
        String res = builder.substring(0, builder.length() - 5).concat("\n\t\t\t}");
        return  res;
    }

}
