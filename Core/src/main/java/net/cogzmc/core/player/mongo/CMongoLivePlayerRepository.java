package net.cogzmc.core.player.mongo;

import com.mongodb.DBObject;
import net.cogzmc.core.player.COfflinePlayer;

import java.util.UUID;

import static net.cogzmc.core.player.mongo.MongoUtils.getValueFrom;

class CMongoLivePlayerRepository extends CMongoPlayerRepository {
    public CMongoLivePlayerRepository(CMongoDatabase database) {
        super(database);
    }

    @Override
    COfflinePlayer playerFrom(DBObject dbObject) {
        return new COfflineLiveMongoPlayer(UUID.fromString(getValueFrom(dbObject, MongoKey.UUID_KEY.toString(), String.class)), dbObject, this);
    }

    @Override
    COfflineMongoPlayer getPlayerFor(UUID uuid, DBObject object) {
        return new COfflineLiveMongoPlayer(uuid, object, this);
    }
}
