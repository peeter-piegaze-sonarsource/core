package org.meveo.apiv2.generic.exception;

import org.jboss.resteasy.api.validation.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final ExceptionSerializer exceptionSerializer = new ExceptionSerializer(Response.Status.NOT_FOUND);

    @Override
    public Response toResponse(NotFoundException exception) {
        log.error("A not found exception occurred ", exception);

        Map mapInfo = new HashMap();

        final String patternFindEntity = "(?i)(?<=entity ).*(?i)(?= with id )";

        Pattern pattern = Pattern.compile(patternFindEntity);
        final Matcher matcherFindEntity = pattern.matcher(exception.getMessage());

        final String patternFindId = "(?i)(?<=with id ).*(?i)(?= not found)";
        pattern = Pattern.compile(patternFindId);
        final Matcher matcherFindId = pattern.matcher(exception.getMessage());

        while ( matcherFindEntity.find() && matcherFindId.find() ) {
            mapInfo.put( "path", "/generic/" + matcherFindEntity.group(0) + "s" + "/" + matcherFindId.group(0) );
        }

        return Response.status(Response.Status.NOT_FOUND).entity(exceptionSerializer.toApiError(exception, mapInfo))
                .type(MediaType.APPLICATION_JSON).header(Validation.VALIDATION_HEADER, "true")
                .build();
    }
}
