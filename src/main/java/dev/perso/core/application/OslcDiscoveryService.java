package dev.perso.core.application;

import dev.perso.core.domain.model.OslcQueryCapability;
import dev.perso.core.domain.model.OslcService;
import dev.perso.core.domain.model.OslcServiceProvider;
import dev.perso.core.domain.port.OslcParser;
import dev.perso.core.domain.port.OslcRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class OslcDiscoveryService {

    @Inject
    OslcParser oslcParser;

    @Inject
    OslcRepository oslcRepository;

    public List<OslcServiceProvider> discover(String rootServicesUrl) {
        return oslcParser.parseServiceProviders(rootServicesUrl);
    }

    public List<OslcServiceProvider> getAllProviders() {
        return oslcRepository.getAllProviders();
    }

    public List<OslcService> getAllServices() {
        return oslcRepository.getAllServices();
    }

    public List<OslcQueryCapability> getAllQueries() {
        return oslcRepository.getAllQueries();
    }
}