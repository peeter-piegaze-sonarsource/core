package org.meveo.apiv2;

import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;

import org.meveo.api.rest.knowledgeCenter.impl.CollectionRsImpl;
import org.meveo.api.rest.knowledgeCenter.impl.CommentRsImpl;
import org.meveo.api.rest.knowledgeCenter.impl.PostRsImpl;
import org.meveo.apiv2.exception.BadRequestExceptionMapper;
import org.meveo.apiv2.exception.NotFoundExceptionMapper;
import org.meveo.apiv2.exception.UnhandledExceptionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api/rest/kc/v2")
public class OpencellRestfulKc extends Application {
       private Logger log = LoggerFactory.getLogger(OpencellRestfulKc.class);

        @SuppressWarnings({ "rawtypes", "unchecked" })
        @Override
        public Set<Class<?>> getClasses() {
            Set<Class<?>> resources = new HashSet();
            resources.add(CollectionRsImpl.class);
            resources.add(PostRsImpl.class);
            resources.add(CommentRsImpl.class);


            resources.add(NotFoundExceptionMapper.class);
            resources.add(BadRequestExceptionMapper.class);
            resources.add(UnhandledExceptionMapper.class);
            log.debug("Documenting {} rest services...", resources.size());
            resources.add(OpenApiResource.class);
            log.debug("Opencell OpenAPI definition is accessible in /api/rest/kc/openapi.{type:json|yaml}");
            return resources;
        }

}