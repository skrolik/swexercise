package org.saweko.swexercise.core.util;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Simple model exposing request attributes to sightly
 * Copies request.attributes to internal HashMap and implements AbstractMap
 * to ease access from sightly
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class RequestAttributeModel extends AbstractMap<String, Object> {
    private Map<String, Object> attributes;

    public RequestAttributeModel(SlingHttpServletRequest request) {
        attributes = new HashMap<>();
        for (Object key : Collections.list(request.getAttributeNames())) {
            attributes.put((String) key, request.getAttribute((String) key));
        }
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return attributes.entrySet();
    }

    @Override
    public Object getOrDefault(final Object key, final Object defaultValue) {
        return null;
    }
}
