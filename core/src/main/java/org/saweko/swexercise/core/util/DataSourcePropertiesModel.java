package org.saweko.swexercise.core.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * Simple model for exposing Data Source properties set in dialog for java
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class DataSourcePropertiesModel {
    @ValueMapValue
    private String sourcePath;

    @ValueMapValue
    private String keyPropertyName;

    @ValueMapValue
    private String valuePropertyName;

    /**
     * @return {@link #sourcePath}
     */
    public String getSourcePath() {
        return sourcePath;
    }

    /**
     * {@link #keyPropertyName}
     */
    public String getKeyPropertyName() {
        return keyPropertyName;
    }

    /**
     * {@link #valuePropertyName}
     */
    public String getValuePropertyName() {
        return valuePropertyName;
    }

    /**
     * @return true if all properties are injected and non blank, false otherwise
     */
    public boolean valid() {
        return StringUtils.isNoneBlank(sourcePath, keyPropertyName, valuePropertyName);
    }
}