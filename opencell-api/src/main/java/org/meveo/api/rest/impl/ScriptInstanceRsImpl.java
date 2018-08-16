package org.meveo.api.rest.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import com.ingenico.connect.gateway.sdk.java.Client;
import com.ingenico.connect.gateway.sdk.java.CommunicatorConfiguration;
import com.ingenico.connect.gateway.sdk.java.Factory;
import com.ingenico.connect.gateway.sdk.java.domain.definitions.Address;
import com.ingenico.connect.gateway.sdk.java.domain.definitions.AmountOfMoney;
import com.ingenico.connect.gateway.sdk.java.domain.hostedcheckout.CreateHostedCheckoutRequest;
import com.ingenico.connect.gateway.sdk.java.domain.hostedcheckout.CreateHostedCheckoutResponse;
import com.ingenico.connect.gateway.sdk.java.domain.hostedcheckout.definitions.HostedCheckoutSpecificInput;
import com.ingenico.connect.gateway.sdk.java.domain.payment.PaymentResponse;
import com.ingenico.connect.gateway.sdk.java.domain.payment.definitions.Customer;
import com.ingenico.connect.gateway.sdk.java.domain.payment.definitions.Order;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.ScriptInstanceApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.ScriptInstanceDto;
import org.meveo.api.dto.response.GetScriptInstanceResponseDto;
import org.meveo.api.dto.response.ScriptInstanceReponseDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.ScriptInstanceRs;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Edward P. Legaspi
 * @author Mounir Bahije
 * @lastModifiedVersion 5.2
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class ScriptInstanceRsImpl extends BaseRs implements ScriptInstanceRs {


    private static final Logger logger = Logger.getLogger(ScriptInstanceRsImpl.class.getName());


    @Inject
    private ScriptInstanceApi scriptInstanceApi;

    @Override
    public ScriptInstanceReponseDto create(ScriptInstanceDto postData) {
        ScriptInstanceReponseDto result = new ScriptInstanceReponseDto();
        try {
            result.setCompilationErrors(scriptInstanceApi.createWithCompile(postData));
            result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ScriptInstanceReponseDto update(ScriptInstanceDto postData) {
        ScriptInstanceReponseDto result = new ScriptInstanceReponseDto();
        try {
            result.setCompilationErrors(scriptInstanceApi.updateWithCompile(postData));
            result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus remove(String scriptInstanceCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            scriptInstanceApi.remove(scriptInstanceCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetScriptInstanceResponseDto find(String scriptInstanceCode) {
        GetScriptInstanceResponseDto result = new GetScriptInstanceResponseDto();
        try {
            result.setScriptInstance(scriptInstanceApi.find(scriptInstanceCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public Response execute(String scriptInstanceCode) {
        Response.ResponseBuilder responseBuilder = null;
        Map<String, Object> result = null;
        Map<String, Object> context = new HashMap<String, Object>();

        Enumeration<String> parameterNames = httpServletRequest.getParameterNames();

        while (parameterNames.hasMoreElements()) {
            String name = (String) parameterNames.nextElement();
            if (!"scriptInstanceCode".equalsIgnoreCase(name)) {
                context.put(name, httpServletRequest.getParameter(name));
            }
        }

        try {
                result = scriptInstanceApi.execute(scriptInstanceCode, context);
                responseBuilder = Response.ok();
                responseBuilder.entity(result);
        } catch (MeveoApiException e) {
            log.error(e.getLocalizedMessage());
            responseBuilder = Response.status(Response.Status.BAD_REQUEST).entity(result);
            responseBuilder.entity(e.getLocalizedMessage());
        } catch (BusinessException e) {
            log.error("Failed to execute a script {}", scriptInstanceCode , e);
        }
        Response response = responseBuilder.build();
        return response;
    }

    @Override
    public Response receivedPOST1(MultivaluedMap<String, String> formParams) {
        logger.log(Level.INFO, "-------------------------");
        logger.log(Level.INFO, "-------------------------");
        logger.log(Level.INFO, "-------------------------");
        logger.log(Level.INFO, "---------Test111--------");
        logger.log(Level.INFO, "-------------------------");
        logger.log(Level.INFO, "-------------------------");
        logger.log(Level.INFO, "-------------------------");
        for (String paramKey : formParams.keySet()) {
            logger.log(Level.INFO, paramKey + " = " + formParams.getFirst(paramKey));
        }
        return null;
    }

    @Override
    public Response receivedPOST2() {




        logger.log(Level.INFO, "-------------------------");
        logger.log(Level.INFO, "-------------------------");
        logger.log(Level.INFO, "-------------------------");
        logger.log(Level.INFO, "---------Test222--------");
        logger.log(Level.INFO, "-------------------------");
        logger.log(Level.INFO, "-------------------------");
        logger.log(Level.INFO, "-------------------------");
        return null;
    }

    @Override
    public Response receivedGET() {


        String state = "";

        Enumeration<String> parameterNames = httpServletRequest.getParameterNames();

        while (parameterNames.hasMoreElements()) {
            String name = (String) parameterNames.nextElement();

            logger.log(Level.INFO, "-------------------------");
            logger.log(Level.INFO, name + " = " + httpServletRequest.getParameter(name));
            logger.log(Level.INFO, "-------------------------");

            if ("state".equals(name)) state = httpServletRequest.getParameter(name);

        }


        try {
            Client client = getClient();
            PaymentResponse response = client.merchant("OpenCellTest").payments().get(state);
            logger.log(Level.INFO, "status = " + response.getStatus());
            logger.log(Level.INFO, "getPaymentMethod = " + response.getPaymentOutput().getPaymentMethod());
            logger.log(Level.INFO, "getAmountPaid = " +  response.getPaymentOutput().getAmountPaid());
            logger.log(Level.INFO, "getCardNumber = " +  response.getPaymentOutput().getCardPaymentMethodSpecificOutput().getCard().getCardNumber());
            logger.log(Level.INFO, "getExpiryDate = " +  response.getPaymentOutput().getCardPaymentMethodSpecificOutput().getCard().getExpiryDate());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }



        logger.log(Level.INFO, "-------------------------");
        logger.log(Level.INFO, "-------------------------");
        logger.log(Level.INFO, "-------------------------");
        logger.log(Level.INFO, "---------Test333--------");
        logger.log(Level.INFO, "-------------------------");
        logger.log(Level.INFO, "-------------------------");
        logger.log(Level.INFO, "-------------------------");
        return null;
    }

    private Client getClient() throws URISyntaxException {
        String apiKeyId = System.getProperty("connect.api.apiKeyId", "fe4b8561e7d6d332");
        String secretApiKey = System.getProperty("connect.api.secretApiKey", "t6XTfmNwAzjqdU0K5d4PScJKifkt5n2MU7k5Wb2u1mw=");

        URL propertiesUrl = getClass().getResource("/example-configuration.properties");
        CommunicatorConfiguration configuration = Factory.createConfiguration(propertiesUrl.toURI(), apiKeyId, secretApiKey);
        return Factory.createClient(configuration);
    }

    @Override
    public ScriptInstanceReponseDto createOrUpdate(ScriptInstanceDto postData) {
        ScriptInstanceReponseDto result = new ScriptInstanceReponseDto();
        try {
            result.setCompilationErrors(scriptInstanceApi.createOrUpdateWithCompile(postData));
            result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus enable(String code) {
        ActionStatus result = new ActionStatus();

        try {
            scriptInstanceApi.enableOrDisable(code, true);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus disable(String code) {
        ActionStatus result = new ActionStatus();

        try {
            scriptInstanceApi.enableOrDisable(code, false);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
}