package org.meveo.api.rest.tmforum.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.meveo.api.billing.OrderApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.billing.ApplicableDueDateDelayDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.tmforum.OrderRs;
import org.meveo.model.order.Order;
import org.tmf.dsmapi.catalog.resource.order.ProductOrder;

@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class OrderRsImpl extends BaseRs implements OrderRs {

    @Inject
    private OrderApi orderApi;

    @Override
    public Response createProductOrder(ProductOrder productOrder, UriInfo info) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        Response.ResponseBuilder responseBuilder = null;

        try {
            productOrder = orderApi.createProductOrder(productOrder, null);
            responseBuilder = Response.status(Response.Status.CREATED).entity(productOrder);

        } catch (Exception e) {
            processExceptionAndSetBuilder(result, responseBuilder, e);
        }



        return buildResponse(responseBuilder);
    }

    @Override
    public Response getProductOrder(String orderId, UriInfo info) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        Response.ResponseBuilder responseBuilder = null;

        try {

            ProductOrder productOrder = orderApi.getProductOrder(orderId);

            responseBuilder = Response.ok().entity(productOrder);

        } catch (Exception e) {
            processExceptionAndSetBuilder(result, responseBuilder, e);
        }



        return buildResponse(responseBuilder);
    }

    @Override
    public Response findProductOrders(UriInfo info) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        Response.ResponseBuilder responseBuilder = null;

        try {

            Map<String, List<String>> filterCriteria = new HashMap<String, List<String>>();
            List<ProductOrder> orders = orderApi.findProductOrders(filterCriteria);

            responseBuilder = Response.ok().entity(orders);

            // } catch (MeveoApiException e) {
            // responseBuilder = Response.status(Response.Status.BAD_REQUEST);
            // responseBuilder.entity(new ActionStatus(ActionStatusEnum.FAIL, e.getErrorCode(), e.getMessage()));
        } catch (Exception e) {
            processExceptionAndSetBuilder(result, responseBuilder, e);
        }



        return buildResponse(responseBuilder);
    }

    @Override
    public Response updateProductOrder(String orderId, ProductOrder productOrder, UriInfo info) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        Response.ResponseBuilder responseBuilder = null;

        try {

            productOrder = orderApi.updatePartiallyProductOrder(orderId, productOrder);
            responseBuilder = Response.ok().entity(productOrder);

        } catch (Exception e) {
            processException(e, result);
            if (responseBuilder != null) {
                responseBuilder.entity(result);
            }
        }



        return buildResponse(responseBuilder);
    }

    @SuppressWarnings("hiding")
    @Override
    public Response deleteProductOrder(String orderId, UriInfo info) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        Response.ResponseBuilder responseBuilder = null;

        try {
            orderApi.deleteProductOrder(orderId);

            responseBuilder = Response.ok();

        } catch (Exception e) {
            processExceptionAndSetBuilder(result, responseBuilder, e);
        }
        

        return buildResponse(responseBuilder);
    }

	@Override
	public Response applicableDueDateDelay(String orderId, UriInfo info) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		Response.ResponseBuilder responseBuilder = null;

		try {
			responseBuilder = Response.ok().entity(orderApi.applicableDueDateDelay(orderId));

		} catch (Exception e) {
			processExceptionAndSetBuilder(result, responseBuilder, e);
		}

		return buildResponse(responseBuilder);
	}

	@Override
	public Response simpleDueDateDelay(String orderId, ApplicableDueDateDelayDto postData, UriInfo info) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		Response.ResponseBuilder responseBuilder = null;

		try {
			orderApi.simpleDueDateDelay(orderId, postData);

			responseBuilder = Response.ok();

		} catch (Exception e) {
			processExceptionAndSetBuilder(result, responseBuilder, e);
		}

		return buildResponse(responseBuilder);
	}

    @Override
    public Response validateProductOrder(ProductOrder productOrder, UriInfo info) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        Response.ResponseBuilder responseBuilder = null;
        try {
            orderApi.validateProductOrder(productOrder);
            responseBuilder = Response.ok().entity(productOrder);

        } catch (Exception e) {
            processException(e, result);
            if (responseBuilder != null) {
                responseBuilder.entity(result);
            }
        }
        return buildResponse(responseBuilder);
    }

    /**
     * @param result action result
     * @param responseBuilder builder for response
     * @param e exception
     */
    private void processExceptionAndSetBuilder(ActionStatus result, Response.ResponseBuilder responseBuilder, Exception e) {
        processException(e, result);
        if (responseBuilder != null) {
            responseBuilder.entity(result);
        }
        
    }

    /**
     * @param responseBuilder Response builder.
     * @return response.
     */
    private Response buildResponse(Response.ResponseBuilder responseBuilder) {
        Response response = null;
        if (responseBuilder != null) {
            response = responseBuilder.build();
            log.debug("RESPONSE={}", response.getEntity());
        }
        
		
		return response;
    }
}
