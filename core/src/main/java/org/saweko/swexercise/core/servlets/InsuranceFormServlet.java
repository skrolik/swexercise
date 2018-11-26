package org.saweko.swexercise.core.servlets;

import com.day.cq.i18n.I18n;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.wrappers.SlingHttpServletRequestWrapper;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.saweko.swexercise.core.service.InsuranceCalculatorService;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component(service = Servlet.class, property = {
        ServletResolverConstants.SLING_SERVLET_METHODS + "=POST",
        ServletResolverConstants.SLING_SERVLET_EXTENSIONS + "=html",
        ServletResolverConstants.SLING_SERVLET_SELECTORS + "=calculate",
        ServletResolverConstants.SLING_SERVLET_RESOURCE_TYPES + "=swexercise/components/form/insurance-calculate-form"
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

        final PageManager pm = request.getResourceResolver().adaptTo(PageManager.class);

        Page p = pm.getContainingPage(request.getResource());
        RequestDispatcherOptions rdo = new RequestDispatcherOptions();
        rdo.setReplaceSelectors("");

        SlingHttpServletRequestWrapper wrapperRequest = new SlingHttpServletRequestWrapper(request) {
            @Override
            public String getMethod() {
                return "GET";
            }
        };

        final RequestDispatcher rd = request.getRequestDispatcher(p.getPath(), rdo);
        try {
            rd.include(wrapperRequest, response);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void forwardResponse()
}
