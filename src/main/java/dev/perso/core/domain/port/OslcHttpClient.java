package dev.perso.core.domain.port;

import dev.perso.core.domain.model.OslcRootServices;

public interface OslcHttpClient {
    OslcRootServices fetchRootServices(String rootServicesUrl);
}