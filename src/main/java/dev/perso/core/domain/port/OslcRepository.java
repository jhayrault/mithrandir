package dev.perso.core.domain.port;

import dev.perso.core.domain.model.OslcRootServices;
import dev.perso.core.domain.model.OslcServiceProvider;
import dev.perso.core.domain.model.OslcService;
import dev.perso.core.domain.model.OslcQueryCapability;
import java.util.List;

public interface OslcRepository {
    void saveRootServices(String key, OslcRootServices oslcRootServices);
    OslcRootServices getRootServices(String key);

    List<OslcServiceProvider> getAllProviders();
    List<OslcService> getAllServices();
    List<OslcQueryCapability> getAllQueries();
}