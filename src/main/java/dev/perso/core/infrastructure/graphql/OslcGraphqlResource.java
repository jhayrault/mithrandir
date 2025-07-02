package dev.perso.core.infrastructure.graphql;

import dev.perso.core.application.OslcDiscoveryService;
import dev.perso.core.domain.model.OslcQueryCapability;
import dev.perso.core.domain.model.OslcService;
import dev.perso.core.domain.model.OslcServiceProvider;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;

import java.util.List;

@GraphQLApi
@ApplicationScoped
public class OslcGraphqlResource {

    @Inject
    OslcDiscoveryService discoveryService;

    @Query
    @Description("Liste tous les providers indexés dans Redis")
    public Uni<List<OslcServiceProvider>> getAllProviders() {
        return Uni.createFrom().item(discoveryService.getAllProviders());
    }

    @Query
    @Description("Liste tous les services indexés dans Redis")
    public Uni<List<OslcService>> getAllServices() {
        return Uni.createFrom().item(discoveryService.getAllServices());
    }

    @Query
    @Description("Liste toutes les query capabilities indexées dans Redis")
    public Uni<List<OslcQueryCapability>> getAllQueries() {
        return Uni.createFrom().item(discoveryService.getAllQueries());
    }

    @Query
    @Description("Découvre dynamiquement les providers à partir d'une URL rootServices")
    public Uni<List<OslcServiceProvider>> discoverProviders(String rootServicesUrl) {
        return Uni.createFrom().item(discoveryService.discover(rootServicesUrl));
    }
}
