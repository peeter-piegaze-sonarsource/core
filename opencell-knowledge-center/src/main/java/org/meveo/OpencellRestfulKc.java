package org.meveo;

import org.meveo.api.rest.knowledgeCenter.impl.CollectionRsImpl;
import org.meveo.api.rest.knowledgeCenter.impl.CommentRsImpl;
import org.meveo.api.rest.knowledgeCenter.impl.PostRsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api/rest/v3")
public class OpencellRestfulKc extends Application {
       private Logger log = LoggerFactory.getLogger(OpencellRestfulKc.class);

        @SuppressWarnings({ "rawtypes", "unchecked" })
        @Override
        public Set<Class<?>> getClasses() {
            log.info("KC loaded 1234");

            Set<Class<?>> resources = new HashSet();
            resources.add(CollectionRsImpl.class);
            resources.add(PostRsImpl.class);
            resources.add(CommentRsImpl.class);
            log.debug("Opencell OpenAPI definition is accessible in /api/rest/kc/openapi.{type:json|yaml}");
            return resources;
        }

}