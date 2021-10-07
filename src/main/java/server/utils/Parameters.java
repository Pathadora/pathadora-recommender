package server.utils;

import java.util.HashMap;
import java.util.Map;

public class Parameters {

    private final static String DEFAULT = "default";

    private final Map<String, String> params;

    public Parameters(){
        this.params = new HashMap<String, String>();
    }

    public Parameters(Map<String, String> parameters){
        this.params = parameters;
    }

    public void addParam(String key, String value){
        params.putIfAbsent(key, value);
    }

    public String searchByParam(String param){
        return params.getOrDefault(param, DEFAULT);
    }

    public Map<String, String> get(){
        return this.params;
    }

}
