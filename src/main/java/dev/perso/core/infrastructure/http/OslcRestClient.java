package dev.perso.core.infrastructure.http;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.io.InputStream;

@RegisterRestClient
public interface OslcRestClient {
    @GET
    @Path("")
    @Produces("application/rdf+xml")
    InputStream getRootServices();
}

