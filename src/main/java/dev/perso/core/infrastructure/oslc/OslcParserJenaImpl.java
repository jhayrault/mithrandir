package dev.perso.core.infrastructure.oslc;

import dev.perso.core.domain.model.OslcQueryCapability;
import dev.perso.core.domain.model.OslcService;
import dev.perso.core.domain.model.OslcServiceProvider;
import dev.perso.core.domain.port.OslcParser;
import dev.perso.core.infrastructure.http.OslcRestClient;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.set.SetCommands;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.redis.datasource.value.ValueCommands;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class OslcParserJenaImpl implements OslcParser {
    private static final String OSLC_NS = "http://open-services.net/ns/core#";

    @Inject
    @RestClient
    OslcRestClient oslcRestClient;
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

    private static final String PREFIX_PROVIDER = "oslc:provider:";
    private static final String PREFIX_SERVICE = "oslc:service:";
    private static final String PREFIX_QUERY = "oslc:query:";
    private static final long TTL_SECONDS = 600; // 10 minutes

    private static final String INDEX_PROVIDER = "oslc:index:providers";
    private static final String INDEX_SERVICE = "oslc:index:services";
    private static final String INDEX_QUERY = "oslc:index:queries";

    private void cacheProvider(OslcServiceProvider provider) {
        try {
            String key = PREFIX_PROVIDER + provider.uri();
            String json = objectMapper.writeValueAsString(provider);
            redis().setex(key, TTL_SECONDS, json);
            redisSet().sadd(INDEX_PROVIDER, provider.uri());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private void cacheService(OslcService service) {
        try {
            String key = PREFIX_SERVICE + service.uri();
            String json = objectMapper.writeValueAsString(service);
            redis().setex(key, TTL_SECONDS, json);
            redisSet().sadd(INDEX_SERVICE, service.uri());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private void cacheQuery(OslcQueryCapability query) {
        try {
            String key = PREFIX_QUERY + query.uri();
            String json = objectMapper.writeValueAsString(query);
            redis().setex(key, TTL_SECONDS, json);
            redisSet().sadd(INDEX_QUERY, query.uri());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<OslcServiceProvider> parseServiceProviders(String rootServicesUrl) {
        try {
            InputStream input = oslcRestClient.getRootServices();
            Model model = ModelFactory.createDefaultModel();
            model.read(input, null);
            Resource serviceProviderType = model.createResource(OSLC_NS + "ServiceProvider");
            List<OslcServiceProvider> serviceProviders = new ArrayList<>();
            ResIterator spIter = model.listResourcesWithProperty(RDF.type, serviceProviderType);
            while (spIter.hasNext()) {
                Resource spRes = spIter.nextResource();
                serviceProviders.add(parseServiceProvider(model, spRes));
            }
            input.close();
            return serviceProviders;
        } catch (Exception e) {
            throw new RuntimeException("Erreur parsing OSLC", e);
        }
    }

    private OslcServiceProvider parseServiceProvider(Model model, Resource spRes) {
        Map<String, String> properties = extractProperties(spRes);
        Property serviceProp = model.createProperty(OSLC_NS + "service");
        NodeIterator servicesNodes = model.listObjectsOfProperty(spRes, serviceProp);
        List<OslcService> services = new ArrayList<>();
        while (servicesNodes.hasNext()) {
            RDFNode node = servicesNodes.next();
            if (node.isResource()) {
                services.add(parseService(model, node.asResource()));
            }
        }
        OslcServiceProvider provider = new OslcServiceProvider(spRes.getURI(), properties, services);
        cacheProvider(provider);
        return provider;
    }

    private OslcService parseService(Model model, Resource svcRes) {
        Map<String, String> properties = extractProperties(svcRes);
        Property qcProp = model.createProperty(OSLC_NS + "queryCapability");
        NodeIterator qcNodes = model.listObjectsOfProperty(svcRes, qcProp);
        List<OslcQueryCapability> qcs = new ArrayList<>();
        while (qcNodes.hasNext()) {
            RDFNode node = qcNodes.next();
            if (node.isResource()) {
                qcs.add(parseQueryCapability(model, node.asResource()));
            }
        }
        OslcService service = new OslcService(svcRes.getURI(), properties, qcs);
        cacheService(service);
        return service;
    }

    private OslcQueryCapability parseQueryCapability(Model model, Resource qcRes) {
        Map<String, String> properties = extractProperties(qcRes);
        OslcQueryCapability query = new OslcQueryCapability(qcRes.getURI(), properties);
        cacheQuery(query);
        return query;
    }

    private Map<String, String> extractProperties(Resource res) {
        Map<String, String> props = new HashMap<>();
        StmtIterator stmts = res.listProperties();
        while (stmts.hasNext()) {
            Statement stmt = stmts.nextStatement();
            String pred = stmt.getPredicate().getLocalName();
            RDFNode obj = stmt.getObject();
            if (obj.isLiteral()) {
                props.put(pred, obj.asLiteral().getString());
            } else if (obj.isResource()) {
                props.put(pred, obj.asResource().getURI());
            }
        }
        return props;
    }
}