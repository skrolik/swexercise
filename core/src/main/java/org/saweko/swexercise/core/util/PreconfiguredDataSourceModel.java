package org.saweko.swexercise.core.util;

import com.adobe.granite.ui.components.Value;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.EmptyDataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.crx.JcrConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;
import static org.apache.sling.query.SlingQuery.$;

/**
 * Will create dynamic datasource model using provided datasource properties and provided path
 * <pre>{@code
 * <select jcr:primaryType="nt:unstructured"
 *      sling:resourceType="granite/ui/components/coral/foundation/form/select"
 *      fieldLabel="Choose field"
 *      name="./field"
 *      required="true">
 *      <datasource
 *          jcr:primaryType="nt:unstructured"
 *          sling:resourceType="swexercise/components/configuration/preconfigured-data-source"
 *          sourcePath="/content/swexercise/en/configuration/age-ranges/jcr:content/generic-list-items"
 *          keyPropertyName="value"
 *          valuePropertyName="label"
 *          />
 *      </select>
 * }</pre>
 * From above example we see that we are creating {@code <datasource} element
 * PreconfiguredDataSourceModel searches for all children under source
 * path that has non empty properties keyPropertyName="value" and valuePropertyName="label"
 * Later Datasource element is created and populated with found elements
 * <p>
 * if some error occur or path doesn't contain any values empty data source is created
 * <p>
 */
@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class PreconfiguredDataSourceModel {
    private static final Logger LOG = LoggerFactory.getLogger(PreconfiguredDataSourceModel.class);

    @Inject
    private SlingHttpServletRequest request;

    @Inject
    private ResourceResolver resolver;

    @ChildResource(name = "datasource")
    private DataSourcePropertiesModel properties;

    /**
     * easier placeholder for {@link DataSourcePropertiesModel#getKeyPropertyName()}
     */
    private String propsKey;

    /**
     * easier placeholder for {@link DataSourcePropertiesModel#getValuePropertyName()}
     */
    private String propsValue;


    /**
     * will initialize component as written in class description
     */
    @PostConstruct
    public void initialize() {
        request.setAttribute(DataSource.class.getName(), EmptyDataSource.instance());
        if (properties == null || !properties.valid()) {
            return;
        }
        propsKey = properties.getKeyPropertyName();
        propsValue = properties.getValuePropertyName();

        final String dataPath = properties.getSourcePath();
        final Resource dataResource = resolver.getResource(dataPath);

        if (dataResource == null) {
            LOG.error("Data Source '{}' doesn't exist!", dataPath);
            return;
        }

        final DataSource ds = loadDataAndCreateDatasource(dataResource);
        request.setAttribute(DataSource.class.getName(), ds);
    }

    /**
     * loads data and create datasource
     *
     * @param dataResource - datasource for data
     *
     * @return datasource object
     */
    private DataSource loadDataAndCreateDatasource(final Resource dataResource) {
        final Map<String, String> extractedData =
                StreamSupport.stream(dataResource.getChildren().spliterator(), false)
                        .filter(this::hasKeyAndValue)
                        .map(Resource::getValueMap)
                        .collect(Collectors.toMap(
                                vm -> vm.get(propsKey, String.class),
                                vm -> vm.get(propsValue, String.class),
                                (older, newer) -> older, LinkedHashMap::new));


        if (extractedData.size() == 0) {
            LOG.error("Datasource {} doesn't contain any valid entries!", dataResource.getPath());
            return null;
        }
        return buildDatasource(extractedData);
    }

    /**
     * checks if resource contains values required by configuration
     *
     * @param resource - to check
     *
     * @return true if contains false otherwise
     */
    private boolean hasKeyAndValue(final Resource resource) {
        return resource.getValueMap().containsKey(propsKey)
                && resource.getValueMap().containsKey(propsValue);
    }

    /**
     * builds datasource from data map
     *
     * @param data - source for data
     *
     * @return - built datasource
     */
    private DataSource buildDatasource(Map<String, String> data) {
        final List<Resource> fakeResourceList = data.entrySet().stream()
                .map(DataSourceUtils::createValueMap)
                .map(valueMap -> new ValueMapResource(resolver, new ResourceMetadata(), JcrConstants.NT_UNSTRUCTURED, valueMap))
                .collect(toList());

        return new SimpleDataSource(fakeResourceList.iterator());
    }

    /**
     * resolves parent using supplied query
     *
     * @param query - query to search parent for
     *
     * @return found parent path or empty string
     */
    private String determinateParent(final String query) {
        final String contextPath = (String) request.getAttribute(Value.CONTENTPATH_ATTRIBUTE);
        final Resource contextPathResource = request.getResourceResolver().getResource(contextPath);

        return $(contextPathResource).parents(query).stream()
                .findFirst()
                .map(Resource::getPath)
                .orElse(StringUtils.EMPTY);

    }
}