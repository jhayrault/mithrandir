package dev.perso.core.infrastructure.oslc;

import dev.perso.core.domain.model.OslcRootServices;
import dev.perso.core.domain.port.OslcHttpClient;
import dev.perso.core.infrastructure.http.OslcRestClient;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.jena.rdf.model.*;

import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.io.InputStream;

@ApplicationScoped
public class OslcHttpClientImpl implements OslcHttpClient {

    @Inject
    @RestClient
    OslcRestClient oslcRestClient;

    @Override
    public OslcRootServices fetchRootServices(String rootServicesUrl) {
        try {
            InputStream input = oslcRestClient.getRootServices();

            Model model = ModelFactory.createDefaultModel();
            model.read(input, null);

            Property svcCatalogProp = model.createProperty("http://open-services.net/ns/core#serviceProviderCatalog");
            ResIterator resIterator = model.listResourcesWithProperty(svcCatalogProp);

            String svcCatalogUrl = null;
            if (resIterator.hasNext()) {
                Resource res = resIterator.nextResource();
                svcCatalogUrl = res.getProperty(svcCatalogProp).getObject().toString();
            }

            input.close();

            return new OslcRootServices(svcCatalogUrl);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération OSLC", e);
        }
    }
}