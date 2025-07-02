package dev.perso.core.domain.model;

import java.util.List;
import java.util.Map;

public record OslcService(String uri, Map<String, String> properties, List<OslcQueryCapability> queryCapabilities) {}
