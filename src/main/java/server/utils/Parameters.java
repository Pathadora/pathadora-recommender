package server.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Parameters {

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

    public Optional<String> searchByParam(String param){
        return (params.containsKey(param)) ? Optional.of(params.get(param)) : Optional.empty();
    }

    public Map<String, String> get(){
        return this.params;
    }

}
