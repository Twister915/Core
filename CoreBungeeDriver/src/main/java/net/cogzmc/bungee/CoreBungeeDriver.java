package net.cogzmc.bungee;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public final class CoreBungeeDriver extends Plugin {
    private JedisPool jedisPool;
    @Getter private CPlayerRepository playerRepository;
    @Getter private CGroupRepository groupRepository;
    @Getter private static CoreBungeeDriver instance;
    @Getter @Setter private Controller controller;
    @Getter private ServerReaper serverReaper;

    @Override
    public void onEnable() {
        instance = this;
        try {
            if (!getDataFolder().exists() && !getDataFolder().mkdir()) throw new IOException("Could not create the data directory!");
            final File file = new File(getDataFolder(), "database.yml");
            if (!file.exists()) {
                Files.copy(getResourceAsStream("database.yml"), file.toPath());
            }
            ProxyServer.getInstance().getScheduler().runAsync(this, new Runnable() {
                @Override
                @SneakyThrows
                public void run() {
                    Configuration dbConfig = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
                    Configuration redis = dbConfig.getSection("redis");
                    JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
                    jedisPoolConfig.setMinIdle(10);
                    jedisPoolConfig.setMaxTotal(50);
                    jedisPoolConfig.setTestOnBorrow(true);
                    jedisPoolConfig.setBlockWhenExhausted(false);
                    jedisPoolConfig.setTestOnReturn(true);
                    jedisPool = new JedisPool(jedisPoolConfig, redis.getString("host"), redis.getInt("port"), 5);
                    Jedis resource = jedisPool.getResource();
                    resource.connect();
                    if (!resource.isConnected()) throw new IllegalStateException("Jedis is not connected!");
                    resource.close();
                    //player repo
                    if (dbConfig.getKeys().contains("mongo")) {
                        Configuration mongo = dbConfig.getSection("mongo");
                        CMongoDatabase cMongoDatabase = new CMongoDatabase(
                                mongo.getString("host", "127.0.0.1"),
                                mongo.getInt("port", 27017),
                                mongo.getString("database", "core"),
                                mongo.getString("username", null),
                                mongo.getString("password", null),
                                mongo.getString("prefix", null)
                        );
                        cMongoDatabase.connect();
                        CMongoPlayerRepository cMongoPlayerRepository = new CMongoPlayerRepository(cMongoDatabase);
                        playerRepository = cMongoPlayerRepository;
                        CMongoGroupRepository groupRepository1 = new CMongoGroupRepository(cMongoDatabase, cMongoPlayerRepository);
                        cMongoPlayerRepository.setGroupRepository(groupRepository1);
                        groupRepository = groupRepository1;
                        groupRepository1.reloadGroups();

                        ServerLinkingHandler.enable();
                        serverReaper = ServerReaper.enable();
                        DriverListener.enable();
                        Teleporter.enable();
                        PermissionsHandler.enable();
                        PlayerKickManager.enable();
                    }
                }
            });

        } catch (Exception e) {
            throw new IllegalStateException("Could not enable CoreBungeeDriver", e);
        }
    }

    public Jedis getJedisClient() {
        return jedisPool.getResource();
    }

    public void returnJedis(Jedis jedis) {
        jedisPool.returnResource(jedis);
    }
}
