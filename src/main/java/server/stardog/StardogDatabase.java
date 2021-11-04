package server.stardog;
import com.complexible.stardog.StardogException;
import com.complexible.stardog.api.*;
import com.complexible.stardog.api.admin.AdminConnection;
import com.complexible.stardog.api.admin.AdminConnectionConfiguration;
import org.openrdf.model.Graph;
import com.stardog.stark.Resource;
import com.stardog.stark.Statement;
import com.stardog.stark.Values;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import com.stardog.stark.query.SelectQueryResult;
import com.stardog.stark.query.io.QueryResultWriters;
import com.complexible.common.rdf.query.resultio.TextTableQueryResultWriter;
import com.complexible.stardog.api.Connection;
import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.api.SelectQuery;
import org.openrdf.model.Model;
import org.openrdf.model.impl.ValueFactoryImpl;

import static com.stardog.stark.io.RDFFormats.RDFXML;
import static java.lang.Thread.sleep;

public class StardogDatabase {

    private final String database;
    private final Connection connection;

    public StardogDatabase(){
        this.database = Provider.database;
        this.connection = initializeConnection();
    }

    public StardogDatabase(String db){
        this.database = db;
        this.connection = initializeConnection();
    }


    public void importData() throws FileNotFoundException {
        connection.begin();
        connection.add().io()
                .format(RDFXML)
                .stream(new FileInputStream("src/main/resources/ont/pathadora-ontology.owl"));
        connection.commit();
    }


    public void insertData() throws IOException {
        String content = "  <owl:NamedIndividual rdf:about=\"http://www.semanticweb.org/learning-path/pathadora-ontology#Learner_Mateo\">\n" +
                "        <rdf:type rdf:resource=\"http://purl.org/accessible_ocw_ontology#Learner\"/>\n" +
                "        <rdf:type rdf:resource=\"http://www.AccessibleOntology.com/GenericOntology.owl#User\"/>\n" +
                "        <pathadora-ontology:futureDegree rdf:resource=\"http://www.semanticweb.org/learning-path/pathadora-ontology#Degree_Master\"/>\n" +
                "        <pathadora-ontology:gender rdf:resource=\"http://www.semanticweb.org/learning-path/pathadora-ontology#Gender_Male\"/>\n" +
                "        <pathadora-ontology:isPathDriven rdf:resource=\"http://www.semanticweb.org/learning-path/pathadora-ontology#Goal_PathDriven\"/>\n"+
        "<pathadora-ontology:hasSurname>Guri</pathadora-ontology:hasSurname>\n"+
        "<rdfs:label>Learner_Albert</rdfs:label> \n"+
    "</owl:NamedIndividual>";

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
            System.out.println("\nNow a particular slice...");
            QueryResultWriters.write(aResult, System.out, TextTableQueryResultWriter.FORMAT);
        }
    }


    private void checkDuplicatedDatabases() {
        try (final AdminConnection aConn = AdminConnectionConfiguration
                .toServer(Provider.url)
                .credentials(Provider.username, Provider.password)
                .connect()) {
            aConn.list().forEach(System.out::println);
            if (aConn.list().contains(database)) {aConn.drop(database);}
            aConn.disk(database).create();
        }
    }


    private Connection initializeConnection(){
        checkDuplicatedDatabases();
        ConnectionConfiguration connectionConfig = ConnectionConfiguration
                .to(database)
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
        database.insertData();

        database.queryDatabase(Queries.individualsByClass("accessible_ocw_ontology","Learner"));
        //database.queryDatabase(Queries.individualsByClass("GenericOntology","User"));

    }
}
