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
import static server.stardog.DataToOwl.TMP_FILE;
import static server.stardog.Provider.MAX_POOL;
import static server.stardog.Provider.MIN_POOL;
import static server.utils.PathadoraConfig.OntologyConfig.PATHADORA_LOCAL_PATH;

public class StardogDatabase {
 /*   private final Connection connection;

    public StardogDatabase() {
        this.connection = initializeConnection();
    }


    public void importData() throws FileNotFoundException {
        connection.begin();
        connection.add().io()
                .format(RDFXML)
                .stream(new FileInputStream(PATHADORA_LOCAL_PATH));
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

    private static ConnectionPool createConnectionPool(ConnectionConfiguration connectionConfig) {
        ConnectionPoolConfig poolConfig = ConnectionPoolConfig
                .using(connectionConfig)
                .minPool(MIN_POOL)
                .maxPool(MAX_POOL)
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
        return connectionPool.obtain();
    }

  */

}
