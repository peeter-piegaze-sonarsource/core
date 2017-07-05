package org.meveo.api.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class NotAcceptableException extends WebApplicationException {
     public  NotAcceptableException(String message) {
         super(Response.status(Response.Status.NOT_ACCEPTABLE)
             .entity(message).type(MediaType.APPLICATION_JSON_TYPE).build());
     }
}