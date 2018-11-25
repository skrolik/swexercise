package org.saweko.swexercise.core.util;

import com.adobe.cq.commerce.common.ValueMapDecorator;
import org.apache.sling.api.resource.ValueMap;

import java.util.HashMap;
import java.util.Map;

public final class DataSourceUtils {
    public static final String P_DATASOURCE_TEXT = "text";

    public static final String P_DATASOURCE_VALUE = "value";

    public static ValueMap createValueMap(Map.Entry<String, String> e) {
        return createValueMap(e.getValue(), e.getKey());
    }

    public static ValueMap createValueMap(final String text, final String value) {
        final ValueMap vm = new ValueMapDecorator(new HashMap<>());
        vm.put(P_DATASOURCE_TEXT, text);
        vm.put(P_DATASOURCE_VALUE, value);
        return vm;
    }

    private DataSourceUtils() {
    }
}
