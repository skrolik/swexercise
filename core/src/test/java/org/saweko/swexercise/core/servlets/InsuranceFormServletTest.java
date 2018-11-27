package org.saweko.swexercise.core.servlets;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import io.wcm.testing.mock.aem.junit.AemContext;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.wrappers.SlingHttpServletRequestWrapper;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.servlet.MockRequestDispatcherFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.saweko.swexercise.core.model.Gender;
import org.saweko.swexercise.core.service.InsuranceCalculatorException;
import org.saweko.swexercise.core.service.InsuranceCalculatorService;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.saweko.swexercise.core.servlets.InsuranceFormServlet.A_BACK_URL;
import static org.saweko.swexercise.core.servlets.InsuranceFormServlet.A_RESULT;
import static org.saweko.swexercise.core.servlets.InsuranceFormServlet.P_AGE;
import static org.saweko.swexercise.core.servlets.InsuranceFormServlet.P_GENDER;
import static org.saweko.swexercise.core.servlets.InsuranceFormServlet.P_ZIP_CODE;

@RunWith(MockitoJUnitRunner.class)
public class InsuranceFormServletTest {
    private static final String VALID_RES_PATH = "/valid";
    private static final String INVALID_RES_PATH = "/invalid";
    private static final String RATES_RESOURCE_PATH = "/content/swexercise/en/configuration/rates-configuration";

    @Rule
    public final AemContext context = new AemContext(ResourceResolverType.JCR_MOCK);


    @Mock
    private InsuranceCalculatorService service;

    @Mock
    private PageManager pm;

    @Mock
    private Page page;

    private InsuranceFormServlet toTest;

    @Before
    public void prepare() {
        context.load(true).json("/invalid-servlet-config.json", INVALID_RES_PATH);
        context.load(true).json("/valid-servlet-config.json", VALID_RES_PATH);
        context.registerAdapter(ResourceResolver.class, PageManager.class, pm);
        when(pm.getContainingPage(any(Resource.class))).thenReturn(page);
        when(page.getPath()).thenReturn("/dummy/path");
        context.registerService(InsuranceCalculatorService.class, service);
        toTest = context.registerInjectActivateService(new InsuranceFormServlet());
    }

    @Test
    public void doPost_noConfiguration() throws ServletException, IOException {
        context.currentResource(INVALID_RES_PATH);
        toTest.doPost(context.request(), context.response());
        assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, context.response().getStatus());
    }

    @Test
    public void doPost_noConfigurationResource() throws ServletException, IOException {
        context.currentResource(VALID_RES_PATH);
        toTest.doPost(context.request(), context.response());
        assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, context.response().getStatus());
    }

    @Test
    public void doPost_missingArguments_age() throws ServletException, IOException {
        context.load(true).json("/all-rates.json", RATES_RESOURCE_PATH);
        context.currentResource(VALID_RES_PATH);
        toTest.doPost(context.request(), context.response());
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, context.response().getStatus());

        putParameters(null, "12", "MALE");
        toTest.doPost(context.request(), context.response());
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, context.response().getStatus());
    }

    @Test
    public void doPost_missingArguments_zip() throws ServletException, IOException {
        context.load(true).json("/all-rates.json", RATES_RESOURCE_PATH);
        context.currentResource(VALID_RES_PATH);
        toTest.doPost(context.request(), context.response());
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, context.response().getStatus());

        putParameters("12", null, "MALE");
        toTest.doPost(context.request(), context.response());
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, context.response().getStatus());
    }


    @Test
    public void doPost_missingArguments_gender() throws ServletException, IOException {
        context.load(true).json("/all-rates.json", RATES_RESOURCE_PATH);
        context.currentResource(VALID_RES_PATH);
        toTest.doPost(context.request(), context.response());
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, context.response().getStatus());

        putParameters("12", "12", null);
        toTest.doPost(context.request(), context.response());
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, context.response().getStatus());
    }

    @Test
    public void doPost_invalidArguments() throws ServletException, IOException {
        context.load(true).json("/all-rates.json", RATES_RESOURCE_PATH);
        context.currentResource(VALID_RES_PATH);

        putParameters("12", "notNumber", "MALE");
        toTest.doPost(context.request(), context.response());
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, context.response().getStatus());

        putParameters("not-number", "12", "MALE");
        toTest.doPost(context.request(), context.response());
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, context.response().getStatus());

        putParameters("13", "12", "UGHH");
        toTest.doPost(context.request(), context.response());
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, context.response().getStatus());
    }

    @Test
    public void doPost_serviceThrowsException() throws InsuranceCalculatorException, ServletException, IOException {
        context.load(true).json("/all-rates.json", RATES_RESOURCE_PATH);
        context.currentResource(VALID_RES_PATH);
        putParameters("12", "13", "MALE");

        when(service.calculateRate(any(ResourceResolver.class), anyString(), anyInt(), anyInt(), any(Gender.class)))
                .thenThrow(new InsuranceCalculatorException());
        toTest.doPost(context.request(), context.response());

        assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, context.response().getStatus());
    }

    @Test
    public void doPostNoResult() throws InsuranceCalculatorException, ServletException, IOException {
        context.load(true).json("/all-rates.json", RATES_RESOURCE_PATH);
        context.currentResource(VALID_RES_PATH);
        putParameters("12", "13", "MALE");

        when(service.calculateRate(any(ResourceResolver.class), anyString(), anyInt(), anyInt(), any(Gender.class)))
                .thenReturn(null);
        CallsExposingRequestDispatcher dispatcher = new CallsExposingRequestDispatcher();
        context.request().setRequestDispatcherFactory(create(dispatcher));
        toTest.doPost(context.request(), context.response());

        SlingHttpServletRequestWrapper wrapper = (SlingHttpServletRequestWrapper) dispatcher.request;

        Assert.assertEquals("/dummy/path", wrapper.getAttribute(A_BACK_URL));
        Assert.assertNull(wrapper.getAttribute(A_RESULT));
    }

    @Test
    public void doPostWithResult() throws InsuranceCalculatorException, ServletException, IOException {
        context.load(true).json("/all-rates.json", RATES_RESOURCE_PATH);
        context.currentResource(VALID_RES_PATH);
        putParameters("12", "13", "MALE");

        when(service.calculateRate(any(ResourceResolver.class), anyString(), anyInt(), anyInt(), any(Gender.class)))
                .thenReturn(new Double(123.1));
        CallsExposingRequestDispatcher dispatcher = new CallsExposingRequestDispatcher();
        context.request().setRequestDispatcherFactory(create(dispatcher));
        toTest.doPost(context.request(), context.response());

        SlingHttpServletRequestWrapper wrapper = (SlingHttpServletRequestWrapper) dispatcher.request;

        Assert.assertEquals("/dummy/path", wrapper.getAttribute(A_BACK_URL));
        Assert.assertEquals(new Double(123.1), wrapper.getAttribute(A_RESULT));
    }

    private void putParameters(String age, String zipCode, String gender) {
        if (age != null) {
            context.request().getParameterMap().put(P_AGE, new String[]{age});
        }
        if (zipCode != null) {
            context.request().getParameterMap().put(P_ZIP_CODE, new String[]{zipCode});
        }
        if (gender != null) {
            context.request().getParameterMap().put(P_GENDER, new String[]{gender});
        }
    }



    public MockRequestDispatcherFactory create(RequestDispatcher dispatcher) {
        return new MockRequestDispatcherFactory() {
            @Override
            public RequestDispatcher getRequestDispatcher(final String s, final RequestDispatcherOptions requestDispatcherOptions) {
                return dispatcher;
            }

            @Override
            public RequestDispatcher getRequestDispatcher(final Resource resource, final RequestDispatcherOptions requestDispatcherOptions) {
                return dispatcher;
            }
        };
    }

    class CallsExposingRequestDispatcher implements RequestDispatcher {
        ServletRequest request;
        ServletResponse response;
        boolean forward = false;
        boolean include = false;

        @Override
        public void forward(final ServletRequest request, final ServletResponse response) {
            this.request = request;
            this.response = response;
            include = false;
            forward = true;
        }

        @Override
        public void include(final ServletRequest request, final ServletResponse response) {
            this.request = request;
            this.response = response;
            include = true;
            forward = false;

        }
    }

}