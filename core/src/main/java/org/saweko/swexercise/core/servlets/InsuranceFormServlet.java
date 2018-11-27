package org.saweko.swexercise.core.servlets;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.crx.JcrConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.wrappers.SlingHttpServletRequestWrapper;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.saweko.swexercise.core.model.Gender;
import org.saweko.swexercise.core.service.InsuranceCalculatorException;
import org.saweko.swexercise.core.service.InsuranceCalculatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component(service = Servlet.class, property = {
        ServletResolverConstants.SLING_SERVLET_METHODS + "=POST",
        ServletResolverConstants.SLING_SERVLET_EXTENSIONS + "=html",
        ServletResolverConstants.SLING_SERVLET_SELECTORS + "=calculate",
        ServletResolverConstants.SLING_SERVLET_RESOURCE_TYPES + "=swexercise/components/form/insurance-calculate-form"
})
public class InsuranceFormServlet extends SlingAllMethodsServlet {
    private static final Logger LOG = LoggerFactory.getLogger(InsuranceCalculatorService.class);

    /**
     * resource configuration path
     */
    static final String PN_CONFIGURATION_PATH = "configurationPath";

    /**
     * incoming request parameters names
     */
    static final String P_ZIP_CODE = "zipCode";
    static final String P_AGE = "age";
    static final String P_GENDER = "gender";

    /**
     * outgoing attributes names
     */
    static final String A_RESULT = "calculationResult";
    static final String A_BACK_URL = "backUrl";

    @Reference
    private InsuranceCalculatorService insuranceCalculatorService;

    /**
     * validates input, parses parameters,
     * calls {@link InsuranceCalculatorService#calculateRate(ResourceResolver, String, Integer, Integer, Gender)}
     * handles result by forwarding response to same page with success selector
     * @param request incoming request
     * @param response outgoing response
     *
     * @throws ServletException when issues on handling result occur
     * @throws IOException when issues on handling result occur
     */
    @Override
    protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException, IOException {
        // get configuration path
        final String configurationPath = request.getResource().getValueMap().get(PN_CONFIGURATION_PATH, String.class);

        // extract parameters
        final String pZipCode = request.getParameter(P_ZIP_CODE);
        final String pAge = request.getParameter(P_AGE);
        final String pGender = request.getParameter(P_GENDER);

        // validate input:
        if (hasConfigurationErrors(request, response, configurationPath, pZipCode, pAge, pGender)) {
            return;
        }

        final Integer zipCode = Integer.parseInt(pZipCode);
        final Integer age = Integer.parseInt(pAge);
        final Gender gender = Gender.parse(pGender);

        final Double result;
        try {
            result = insuranceCalculatorService.calculateRate(request.getResourceResolver(), configurationPath, age, zipCode, gender);

        } catch (InsuranceCalculatorException e) {
            LOG.error("Calculation error occurred, arguments are confPath: {} age: {}, zipCode: {}, gender: {}",
                    new Object[]{configurationPath, age, zipCode, gender});
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "There were error calculating rate, please contact support");
            return;
        }

        final PageManager pm = request.getResourceResolver().adaptTo(PageManager.class);

        final Page p = pm.getContainingPage(request.getResource());
        final RequestDispatcherOptions rdo = new RequestDispatcherOptions();


        final SlingHttpServletRequestWrapper wrapperRequest = new SlingHttpServletRequestWrapper(request) {
            @Override
            public String getMethod() {
                return "GET";
            }
        };

        if (result == null) {
            rdo.setReplaceSelectors("failure");
        } else {
            rdo.setReplaceSelectors("success");
        }
        wrapperRequest.setAttribute(A_RESULT, result);
        wrapperRequest.setAttribute(A_BACK_URL, p.getPath());

        final RequestDispatcher rd = request.getRequestDispatcher(p.getPath(), rdo);
        rd.include(wrapperRequest, response);
    }

    /**
     * Will check if supplied data is valid, that is:
     * configurationPath is not null and resolves to resource
     * pZipCode and pAge aren't null and are numbers
     * pGender is not null and can be parsed
     *
     * @param request           - used to access resource resolver and log current form path
     * @param response          - used to send error
     * @param configurationPath - config path to check
     * @param pZipCode          - zipCode value to check
     * @param pAge              - age value to check
     * @param pGender           - gender value to check
     *
     * @return true if something is wrong, false otherwise
     *
     * @throws IOException on issues when sendError is called
     */
    private boolean hasConfigurationErrors(final SlingHttpServletRequest request, final SlingHttpServletResponse response, final String configurationPath,
                                           final String pZipCode, final String pAge, final String pGender) throws IOException {
        if (StringUtils.isBlank(configurationPath) || request.getResourceResolver().getResource(configurationPath) == null) {
            LOG.error("Unable to load configuration path from resource " + request.getResource().getPath());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to load configuration, please contact support");
            return true;
        }

        // validate parameters (not null, zip and age numeric, gender can be parsed
        if (StringUtils.isAnyBlank(pZipCode, pAge, pGender) || !StringUtils.isNumeric(pAge) || !StringUtils.isNumeric(pZipCode)
                || Gender.parse(pGender) == null) {
            LOG.error("Missing or incorrect mandatory parameter, required are {}:{}, {}:{}, {}:{}"
                    , new Object[]{P_AGE, pAge, P_ZIP_CODE, pZipCode, P_GENDER, pGender});
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing or incorrect mandatory parameters: "
                    + StringUtils.join(new String[]{P_GENDER, P_ZIP_CODE, P_AGE}, ','));
            return true;
        }
        return false;
    }


}
