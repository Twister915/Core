package net.cogzmc;

import net.cogzmc.core.player.COfflinePlayer;
import net.cogzmc.core.player.CPlayerRepository;
import net.cogzmc.core.player.DatabaseConnectException;
import net.cogzmc.core.player.mongo.CMongoDatabase;
import net.cogzmc.core.player.mongo.CMongoGroupRepository;
import net.cogzmc.core.player.mongo.CMongoPlayerRepository;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("/player")
public class PlayerResource {
    private static CPlayerRepository playerRepository;

    static {
        CMongoDatabase bashurverse = new CMongoDatabase("play.bashursworld.com", 27017, "bashurverse", null, null, "");
        try {
            bashurverse.connect();
        } catch (DatabaseConnectException e) {
            e.printStackTrace();
        }
        CMongoPlayerRepository cMongoPlayerRepository = new CMongoPlayerRepository(bashurverse);
        CMongoGroupRepository groupRepository = new CMongoGroupRepository(bashurverse, cMongoPlayerRepository);
        playerRepository = cMongoPlayerRepository;
        cMongoPlayerRepository.setGroupRepository(groupRepository);
    }

    @Path("/uuid/{uuid: [0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}}")
    @Produces("application/json")
    @GET
    public Response getPlayerByUUID(@PathParam("uuid") String uuid) {
        if (uuid == null) return Response.status(422).entity("INVALID_REQUEST").build();
        COfflinePlayer offlinePlayerByUUID = playerRepository.getOfflinePlayerByUUID(UUID.fromString(uuid));
        if (offlinePlayerByUUID == null) return Response.status(404).build();
        PlayerBean playerBean = new PlayerBean(offlinePlayerByUUID);
        return Response.ok().entity(playerBean).build();
    }

    @Path("/username/{username: [A-Z|a-z|1-9|_]{0,16}}")
    @Produces("application/json")
    @GET
    public Response getPlayerByUsername(@PathParam("username") String username) {
        if (username == null) return Response.status(422).entity("INVALID_REQUEST").build();
        List<COfflinePlayer> offlinePlayerByName = playerRepository.getOfflinePlayerByName(username);
        PlayerBean[] beans = new PlayerBean[offlinePlayerByName.size()];
        for (int x = 0; x < offlinePlayerByName.size(); x++) {
            beans[x] = new PlayerBean(offlinePlayerByName.get(x));
        }
        return Response.ok(beans).build();
    }

}
