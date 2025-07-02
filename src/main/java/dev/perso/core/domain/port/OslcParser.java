package dev.perso.core.domain.port;

import dev.perso.core.domain.model.OslcServiceProvider;

import java.util.List;

public interface OslcParser {

    List<OslcServiceProvider> parseServiceProviders(String rootServicesUrl);
}
