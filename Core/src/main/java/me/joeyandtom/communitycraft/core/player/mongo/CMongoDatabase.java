package me.joeyandtom.communitycraft.core.player.mongo;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import me.joeyandtom.communitycraft.core.player.CDatabase;
import me.joeyandtom.communitycraft.core.player.DatabaseConnectException;

import java.net.UnknownHostException;

@Data
public final class CMongoDatabase implements CDatabase {
    @NonNull private final String host;
    @NonNull private final Integer port;
    @NonNull private final String database;
    private final String collectionPrefix;

    @Getter private DB mongoDatabase;
    @Getter private MongoClient client;

    @Override
    public void connect() throws DatabaseConnectException {
        try {
            this.client = new MongoClient(host, port);
        } catch (UnknownHostException e) {
            throw new DatabaseConnectException("Could not resolve mongo hostname!", e, this);
        }
        this.mongoDatabase = this.client.getDB(database);
    }

    @Override
    public void disconnect() {

    }
}
