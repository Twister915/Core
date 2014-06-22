package net.cogzmc;

import net.cogzmc.core.player.COfflinePlayer;
import net.cogzmc.core.player.CPlayerRepository;
import net.cogzmc.core.player.mongo.CMongoPlayerRepository;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.UUID;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("player/{uuid}")
public final class PlayerResource {
    private static CPlayerRepository playerRepository;

    static {
        playerRepository = new CMongoPlayerRepository(null);
    }

    @GET
    @Produces("application/json")
    public Response getPlayerByUUID(@PathParam("uuid") String uuid) {
        if (!uuid.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")) return Response.status(422).entity("/invalid_request").build();
        COfflinePlayer offlinePlayerByUUID = playerRepository.getOfflinePlayerByUUID(UUID.fromString(uuid));
        if (offlinePlayerByUUID == null) return Response.status(404).build();
        return Response.ok(new PlayerBean(offlinePlayerByUUID)).build();
    }
}
