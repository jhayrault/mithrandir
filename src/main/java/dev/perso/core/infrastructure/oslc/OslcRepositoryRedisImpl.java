package dev.perso.core.infrastructure.oslc;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.perso.core.domain.model.OslcQueryCapability;
import dev.perso.core.domain.model.OslcService;
import dev.perso.core.domain.model.OslcServiceProvider;
import dev.perso.core.domain.model.OslcRootServices;
import dev.perso.core.domain.port.OslcRepository;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.set.SetCommands;
import io.quarkus.redis.datasource.value.ValueCommands;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class OslcRepositoryRedisImpl implements OslcRepository {
    private static final String PREFIX_PROVIDER = "oslc:provider:";
    private static final String PREFIX_SERVICE = "oslc:service:";
    private static final String PREFIX_QUERY = "oslc:query:";
    private static final String INDEX_PROVIDER = "oslc:index:providers";
    private static final String INDEX_SERVICE = "oslc:index:services";
    private static final String INDEX_QUERY = "oslc:index:queries";

    @Inject
    RedisDataSource redisDS;
    @Inject
    ObjectMapper objectMapper;

    private ValueCommands<String, String> redis() {
        return redisDS.value(String.class, String.class);
    }
    private SetCommands<String, String> redisSet() {
        return redisDS.set(String.class);
    }

    @Override
    public List<OslcServiceProvider> getAllProviders() {
        List<OslcServiceProvider> result = new ArrayList<>();
        for (String uri : redisSet().smembers(INDEX_PROVIDER)) {
            String json = redis().get(PREFIX_PROVIDER + uri);
            if (json != null) {
                try {
                    result.add(objectMapper.readValue(json, OslcServiceProvider.class));
                } catch (Exception ignored) {}
            }
        }
        return result;
    }

    @Override
    public List<OslcService> getAllServices() {
        List<OslcService> result = new ArrayList<>();
        for (String uri : redisSet().smembers(INDEX_SERVICE)) {
            String json = redis().get(PREFIX_SERVICE + uri);
            if (json != null) {
                try {
                    result.add(objectMapper.readValue(json, OslcService.class));
                } catch (Exception ignored) {}
            }
        }
        return result;
    }

    @Override
    public List<OslcQueryCapability> getAllQueries() {
        List<OslcQueryCapability> result = new ArrayList<>();
        for (String uri : redisSet().smembers(INDEX_QUERY)) {
            String json = redis().get(PREFIX_QUERY + uri);
            if (json != null) {
                try {
                    result.add(objectMapper.readValue(json, OslcQueryCapability.class));
                } catch (Exception ignored) {}
            }
        }
        return result;
    }

    @Override
    public void saveRootServices(String key, OslcRootServices oslcRootServices) {}
    @Override
    public OslcRootServices getRootServices(String key) { return null; }
}

