package server.stardog;
import com.complexible.stardog.api.*;
import com.complexible.stardog.api.admin.AdminConnection;
import com.complexible.stardog.api.admin.AdminConnectionConfiguration;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

import java.util.concurrent.TimeUnit;

import com.stardog.stark.query.SelectQueryResult;
import com.stardog.stark.query.io.QueryResultWriters;
import com.complexible.common.rdf.query.resultio.TextTableQueryResultWriter;
import com.complexible.stardog.api.Connection;
import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.api.SelectQuery;

import static com.stardog.stark.io.RDFFormats.RDFXML;
import static server.utils.PathadoraConfig.OntologyConfig.PATHADORA_LOCAL_PATH;

public class StardogDatabase {
   /* private final Connection connection;

    public StardogDatabase() throws FileNotFoundException {
        this.connection = initializeConnection();
        this.importData();
    }


    private void importData() throws FileNotFoundException {
        connection.begin();
        connection.add().io()
                .format(RDFXML)
                .stream(new FileInputStream(PATHADORA_LOCAL_PATH));
        connection.commit();
    }


    public void insertData(String content) throws IOException {
        DataToOwl.toOWLFile(content);

        connection.begin();
        connection.add().io()
                .format(RDFXML)
                .stream(new FileInputStream("newIndividuals"));
        connection.commit();

        Files.deleteIfExists(new File("newIndividuals").toPath());
    }

    private static ConnectionPool createConnectionPool(ConnectionConfiguration connectionConfig) {
        ConnectionPoolConfig poolConfig = ConnectionPoolConfig
                .using(connectionConfig)
                .minPool(10)
                .maxPool(100000)
                .expiration(1, TimeUnit.HOURS)
                .blockAtCapacity(1, TimeUnit.MINUTES);
        return poolConfig.create();
    }


    public void queryDatabase(String sparqlQuery) throws IOException {
        SelectQuery query = connection.select(sparqlQuery);
        try(SelectQueryResult aResult = query.execute()) {
            System.out.println("\n Now a particular slice...");
            QueryResultWriters.write(aResult, System.out, TextTableQueryResultWriter.FORMAT);
        }
    }


    private void checkDuplicatedDatabases() {
        try (final AdminConnection aConn = AdminConnectionConfiguration
                .toServer(Provider.url)
                .credentials(Provider.username, Provider.password)
                .connect()) {
            aConn.list().forEach(System.out::println);
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
        return connectionPool.obtain();
    }


    public static void main(String... args) throws IOException, InterruptedException {
        System.out.println("Stardog Database");

        StardogDatabase database = new StardogDatabase();
        database.importData();
        //database.queryDatabase();
        //database.insertData();
        database.queryDatabase(Queries.individualsByClass("accessible_ocw_ontology","Learner"));
        //database.queryDatabase(Queries.individualsByClass("GenericOntology","User"));

    }
*/
}
