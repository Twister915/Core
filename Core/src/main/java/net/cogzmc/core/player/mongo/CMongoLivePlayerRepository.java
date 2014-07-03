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
    COfflineMongoPlayer getPlayerWithUUIDAndObject(UUID uuid, DBObject object) {
        return new COfflineLiveMongoPlayer(uuid, object, this);
    }
}
