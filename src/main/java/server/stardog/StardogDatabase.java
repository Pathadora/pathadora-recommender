package server.stardog;
import com.complexible.stardog.api.*;
import com.complexible.stardog.api.admin.AdminConnection;
import com.complexible.stardog.api.admin.AdminConnectionConfiguration;
import com.stardog.stark.Resource;
import com.stardog.stark.Statement;
import com.stardog.stark.Values;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import com.stardog.stark.query.SelectQueryResult;
import com.stardog.stark.query.io.QueryResultWriters;
import com.complexible.common.rdf.query.resultio.TextTableQueryResultWriter;
import com.complexible.stardog.api.Connection;
import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.api.SelectQuery;

import static com.stardog.stark.io.RDFFormats.RDFXML;

public class StardogDatabase {

    private final String database;
    private final Connection connection;

    public StardogDatabase(String username, String password, String url){
        this.database = "pathadora-temp-1";
        this.connection = initializeConnection(username, password, url);
    }

    public StardogDatabase(String db, String username, String password, String url){
        this.database = db;
        this.connection = initializeConnection(username, password, url);
    }


    public void importData() throws FileNotFoundException {
        connection.begin();
        connection.add().io()
                .format(RDFXML)
                .stream(new FileInputStream("src/main/resources/ont/pathadora-ontology.owl"));
        connection.commit();
    }


    public void insertData(){
        connection.begin();
        Collection<Statement> aGraph = Collections.singleton(
                Values.statement(
                        Values.iri("http://www.semanticweb.org/learning-path/pathadora-ontology#Learner_Rogerto"),
                        Values.iri("http://www.semanticweb.org/learning-path/pathadora-ontology#recommendedFaculty"),
                        Values.iri("http://www.semanticweb.org/learning-path/pathadora-ontology#Faculty_BC_Mediterranean_societies_and_cultures")));

        Resource aContext = Values.iri("urn:test:context");

        connection.add().graph(aGraph, aContext);
        connection.commit();
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
            System.out.println("\nNow a particular slice...");
            QueryResultWriters.write(aResult, System.out, TextTableQueryResultWriter.FORMAT);
        }
    }

    private void checkDuplicatedDatabases(String username, String password, String url) {
        try (final AdminConnection aConn = AdminConnectionConfiguration
                .toServer(url)
                .credentials(username, password)
                .connect()) {
            aConn.list().forEach(System.out::println);
            if (aConn.list().contains(database)) {aConn.drop(database);}
            aConn.disk(database).create();
        }
    }


    private Connection initializeConnection(String username, String password, String url){
        checkDuplicatedDatabases(username, password, url);
        ConnectionConfiguration connectionConfig = ConnectionConfiguration
                .to(database)
                .server(url)
                .reasoning(false)
                .credentials(username, password);
        ConnectionPool connectionPool = createConnectionPool(connectionConfig);
        return connectionPool.obtain();
    }


    public static void main(String... args) throws IOException {
        System.out.println("Stardog Database");

        StardogDatabase database = new StardogDatabase("admin", "admin", "http://localhost:5820");
        database.importData();
        //database.queryDatabase();
        database.insertData();
        database.queryDatabase(Queries.individualsByClass("accessible_ocw_ontology","Learner"));
    }
}
