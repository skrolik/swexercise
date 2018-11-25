package org.saweko.swexercise.core.servlets;

import com.day.cq.i18n.I18n;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.saweko.swexercise.core.service.InsuranceCalculatorService;

import javax.servlet.Servlet;

@Component(service = Servlet.class, property = {
        ServletResolverConstants.SLING_SERVLET_METHODS + "=POST",
        ServletResolverConstants.SLING_SERVLET_EXTENSIONS + "=html",
        ServletResolverConstants.SLING_SERVLET_SELECTORS + "=calculate",
        ServletResolverConstants.SLING_SERVLET_RESOURCE_TYPES + "swexercise/components/form/insurance-calculate-form"
})
public class InsuranceFormServlet extends SlingAllMethodsServlet {
    private static final String M_EMPTY_FIELD = "insurance-form-servlet.empty-field";

    private static final String P_ZIP_CODE = "zipCode";
    private static final String P_AGE = "age";
    private static final String P_GENDER = "gender";
    private static final String ERROR_SUFFIX = "_error";

    @Reference
    private InsuranceCalculatorService insuranceCalculatorService;

    @Override
    protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response) {
        final I18n i18n = new I18n(request);

//        response.set


//        if (!fieldsAreValid(request, response, i18n, P_AGE, P_GENDER, P_ZIP_CODE)) {
////            response.
//        }
    }

    private boolean fieldsAreValid(final SlingHttpServletRequest request, final SlingHttpServletResponse response, final I18n i18n,
                                   final String... fieldNames) {
        boolean result = true;
        for (String name : fieldNames) {
            if (StringUtils.isBlank(request.getParameter(name))) {
                result = false;
                request.setAttribute(name + ERROR_SUFFIX, i18n.get(M_EMPTY_FIELD));
            }

        }
        if (!result) {
            for (String name : fieldNames) {
                request.setAttribute(name, request.getParameter(name));
            }
        }
        return result;
    }


}
