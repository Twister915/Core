package net.cogzmc.core.player.mongo;

import net.cogzmc.core.player.CDatabase;
import net.cogzmc.core.player.DatabaseConnectException;

import java.net.UnknownHostException;

@Data
public final class CMongoDatabase implements CDatabase {
    private final MongoClientURI uri;

    private final String database;
    private final String collectionPrefix;

    @Getter private DB mongoDatabase;
    @Getter private MongoClient client;

    public CMongoDatabase(String host, Integer port, String database, String username, String password, String collectionPrefix) {
        if (password != null && username != null) {
            uri = new MongoClientURI("mongodb://" + username + ":" + password + "@" + host + ":"  + port + "/" + database);
        }
        else uri = new MongoClientURI("mongodb://" + host + ":" + port + "/" + database);
        this.collectionPrefix = collectionPrefix;
        this.database = database;
    }

    public CMongoDatabase(MongoClientURI uri, String collectionPrefix, String database) {
        this.uri = uri;
        this.collectionPrefix = collectionPrefix;
        this.database = database;
    }

    @Override
    public void connect() throws DatabaseConnectException {
        try {
            this.client = new MongoClient(uri); //Connect using it
        } catch (UnknownHostException e) {
            throw new DatabaseConnectException("Could not resolve mongo hostname!", e, this); //Could not connect!
        } catch (Exception e) {
            throw new DatabaseConnectException(e.getMessage(), e, this);
        }
        this.mongoDatabase = this.client.getDB(database); //Grab the database
    }

    @Override
    public void disconnect() {
        if (this.mongoDatabase != null) this.mongoDatabase = null; //Memory management FTW
        if (this.client != null) {
            this.client.close();
            this.client = null;
        }
    }

    public DBCollection getCollection(String name) {
        return mongoDatabase.getCollection((collectionPrefix == null ? "" : collectionPrefix) + name);
    }
}
