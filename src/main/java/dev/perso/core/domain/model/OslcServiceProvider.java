package dev.perso.core.domain.model;

import java.util.List;
import java.util.Map;

public record OslcServiceProvider(String uri, Map<String, String> properties, List<OslcService> services) {}
