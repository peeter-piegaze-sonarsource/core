package org.meveo.apiv2.generic.exception;


import org.meveo.apiv2.generic.GenericResourceImpl;
import org.meveo.apiv2.models.ApiException;
import org.meveo.apiv2.models.Cause;
import org.meveo.apiv2.models.ImmutableApiException;
import org.meveo.apiv2.models.ImmutableCause;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class ExceptionSerializer {

    private final Response.Status status;

    private static final String PATH_SWAGGER_DOC = "pages/docs/";

    ExceptionSerializer(Response.Status status) {
        this.status = status;
    }

    public ApiException toApiError(Exception exception) {
        final List<Cause>  cause = getCause(exception);

        Map<String,Object> mapInfoForError = GenericResourceImpl.getMapInfoForError();

        StringBuilder pathBuilder = new StringBuilder();
        final String patternFindPath = "^.*?(?=(?i)(?>api)\\/(?>rest)\\/(?>v2).*)";
        Pattern pattern = Pattern.compile(patternFindPath);

        StringBuilder messageBuilder = null;

        for ( Map.Entry<String,Object> entry : mapInfoForError.entrySet() ) {
            if ( entry.getKey().equals( GenericResourceImpl.URI_INFO ) ) {
                UriInfo uriInfo = (UriInfo) entry.getValue();
                uriInfo.getBaseUri();
                final Matcher matcherFindPath = pattern.matcher( uriInfo.getBaseUri().toString() );
                while ( matcherFindPath.find() ) {
                    pathBuilder.append( matcherFindPath.group(0) );
                    pathBuilder.append( PATH_SWAGGER_DOC );
                }
            }
            else if ( entry.getKey().equals( GenericResourceImpl.EXCEPTION_MESSAGE ) ) {
                messageBuilder = new StringBuilder();
                messageBuilder.append( entry.getValue() );
            }
        }

        GenericResourceImpl.getMapInfoForError().clear();

        return ImmutableApiException.builder()
                .status(status)
                .infoURL( pathBuilder.toString() )
                .message( messageBuilder != null ? messageBuilder.toString() :
                        ( exception.getMessage() != null ? exception.getMessage() : getStackTrace(exception.getStackTrace()) ) )
                .addAllCauses(cause)
                .build();

//        return ImmutableApiException.builder()
//                .status(status)
//                .infoURL( pathBuilder.toString() )
//                .message(exception.getMessage() != null ? exception.getMessage() : getStackTrace(exception.getStackTrace()))
//                .addAllCauses(cause)
//                .build();
    }

    private String getStackTrace(StackTraceElement[] stackTrace) {
        return Arrays.stream(stackTrace)
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("\n"));
    }

    private List<Cause> getCause(Exception exception) {
        List<Throwable> causes = new ArrayList<>();
        Throwable nextCause = exception;

        if ( nextCause != null ) {
            causes.add(nextCause);
        }

        do{
            causes.add(nextCause.getCause());
            nextCause = nextCause.getCause();
        } while (nextCause != null && causes.contains(nextCause));

        return causes.stream()
                .filter(cause -> cause != null && cause.getMessage() != null)
                .map(cause -> ImmutableCause.builder().causeMessage(String.valueOf(cause)).build())
                .collect(Collectors.toList());
    }
}
