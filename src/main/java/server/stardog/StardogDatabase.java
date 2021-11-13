package server.stardog;
import com.complexible.stardog.api.*;
import com.complexible.stardog.api.admin.AdminConnection;
import com.complexible.stardog.api.admin.AdminConnectionConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

import java.util.*;
import java.util.concurrent.TimeUnit;

import com.stardog.stark.query.SelectQueryResult;
import com.complexible.stardog.api.Connection;
import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.api.SelectQuery;
import server.utils.OutputToJson;
import static com.stardog.stark.io.RDFFormats.*;

import static server.stardog.DataToOwl.*;
import static server.stardog.Provider.*;
import static server.stardog.Queries.prefixesMap;
import static server.utils.PathadoraConfig.OntologyConfig.*;

public class StardogDatabase {
   private final Connection connection;

    public StardogDatabase() {
        this.connection = initializeConnection();
    }


    public void importData(String path) throws FileNotFoundException {
        connection.begin();
        connection.add().io()
                .format(RDFXML)
                .stream(new FileInputStream(path));
        connection.commit();
        System.out.println("Stardog Database: Ontology loaded\n");
    }


    public void insertData(String content) throws IOException {
        DataToOwl.toOWLFile(content);

        connection.begin();
        connection.add().io()
                .format(RDFXML)
                .stream(new FileInputStream(TMP_FILE));
        connection.commit();

        Files.deleteIfExists(new File(TMP_FILE).toPath());
    }


    public List<Map<String, String>>  queryDatabase(String sparqlQuery) {
        SelectQuery query = connection.select(sparqlQuery);
        try(SelectQueryResult aResult = query.execute()) {
            List<Map<String, String>> output = new ArrayList<>();
            while (aResult.hasNext()) {
                Map<String, String> mapInfo = new HashMap<>();
                aResult.next()
                        .stream()
                        .map(Object::toString)
                        .forEach(e -> mapInfo.put(extractKey(e), extractValue(e)));
                output.add(mapInfo);
            }
            return output;
        }
    }


    public void removeDatabase(){
        try (final AdminConnection aConn = AdminConnectionConfiguration
                .toServer(Provider.url).credentials(Provider.username, Provider.password)
                .connect()) {
            if (aConn.list().contains(Provider.database)) {aConn.drop(Provider.database);}
        }
    }


    private void checkDuplicatedDatabases() {
        try (final AdminConnection aConn = AdminConnectionConfiguration
                .toServer(Provider.url)
                .credentials(Provider.username, Provider.password)
                .connect()) {
            //aConn.list().forEach(System.out::println);
            if (aConn.list().contains(Provider.database)) {aConn.drop(Provider.database);}
            aConn.disk(Provider.database).create();
        }
    }


    private Connection initializeConnection(){
        checkDuplicatedDatabases();
        ConnectionConfiguration connectionConfig = ConnectionConfiguration
                .to(Provider.database)
                .server(Provider.url)
                .reasoning(false)
                .credentials(Provider.username, Provider.password);
        ConnectionPool connectionPool = createConnectionPool(connectionConfig);
        Connection connection = connectionPool.obtain();
        importPrefixes(connection);

        return connection;
    }


    private String extractKey(String data){
        return data.substring(1, data.indexOf("="));
    }


    private String extractValue(String data){
        String value = data.split("=")[1];
        if(value.contains("^")){
            return data.substring(data.indexOf("\"")+1, data.indexOf("^")-1);
        }else{
            return data.split("#")[1];
        }
    }


    private void importPrefixes(Connection connection){
        prefixesMap().forEach((key, value) -> connection.namespaces().add(key, value));
    }


    private static ConnectionPool createConnectionPool(ConnectionConfiguration connectionConfig) {
        ConnectionPoolConfig poolConfig = ConnectionPoolConfig
                .using(connectionConfig)
                .minPool(MIN_POOL)
                .maxPool(MAX_POOL)
                .expiration(1, TimeUnit.HOURS)
                .blockAtCapacity(1, TimeUnit.MINUTES);
        return poolConfig.create();
    }


    public static void main(String... args) throws IOException {
        StardogDatabase db = new StardogDatabase();
        db.importData(PATHADORA_TEMP_LOCAL_PATH);

        List<Map<String, String>> result = db.queryDatabase(Queries.courses("Learner_Eloho", "Degree_Bachelor", "1"));
        System.out.println(OutputToJson.coursesJsonResponse(result));
    }
}
