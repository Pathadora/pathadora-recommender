package server.stardog;

import com.complexible.stardog.api.Connection;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface GraphDatabase {
    Connection initializeConnection();
    void insertData(String content) throws IOException;
    void importData(String path) throws FileNotFoundException;
    List<Map<String, String>> queryDatabase(String sparqlQuery);
    void removeDatabase();
}
