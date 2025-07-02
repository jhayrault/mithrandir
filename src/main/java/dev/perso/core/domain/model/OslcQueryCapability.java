package dev.perso.core.domain.model;

import java.util.Map;

public record OslcQueryCapability(String uri, Map<String, String> properties) {}
