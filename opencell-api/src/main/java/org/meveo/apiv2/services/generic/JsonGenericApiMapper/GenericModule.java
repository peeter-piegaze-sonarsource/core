package org.meveo.apiv2.services.generic.JsonGenericApiMapper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.meveo.apiv2.generic.GenericPaginatedResource;
import org.meveo.model.IEntity;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

class GenericModule extends SimpleModule {
    private static final String NAME = "GenericModule";
    private static final VersionUtil VERSION_UTIL = new VersionUtil() {};
    // to passe as constructor param from JsonGenericMapper
    private static final String DATA_ROOT_ELEMENT = "data";
    private final Set<String> nestedEntities;

    GenericModule(Set<String> nestedEntities) {
        super(NAME, VERSION_UTIL.version());
        this.nestedEntities = nestedEntities;
        addSerializer(HibernateProxy.class, new LazyProxySerializer());
        addSerializer(List.class, new ListCustomSerializer());
    }

    private class LazyProxySerializer extends StdSerializer<HibernateProxy> {

         LazyProxySerializer(Class<HibernateProxy> t) {
             super(t);
         }

         LazyProxySerializer() {
             this(null);
         }

         @Override
         public void serialize(HibernateProxy value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
             JsonStreamContext outputContext = gen.getOutputContext();
             if(DATA_ROOT_ELEMENT.equals(outputContext.getParent().getCurrentName()) || nestedEntities.contains(outputContext.getCurrentName())){
                 Hibernate.initialize(value);
                 Object implementation = value.getHibernateLazyInitializer().getImplementation();
                 gen.writeObject(implementation);
             } else {
                 gen.writeObject(value.getHibernateLazyInitializer().getIdentifier());
             }
         }
     }

    private class ListCustomSerializer extends StdSerializer<List> {

        private JsonSerializer<Object> serializer;

        ListCustomSerializer() {
            this(List.class);
        }

        ListCustomSerializer(Class<List> t) {
            super(t);
        }

        @Override
        public boolean isEmpty(SerializerProvider provider, List value) {
            return super.isEmpty(provider, value) || value.isEmpty();
        }

        @Override
        public void serialize(List list, JsonGenerator gen, SerializerProvider provider) throws IOException {
            if(shouldReturnOnlyIds(list, gen.getCurrentValue(), gen.getOutputContext().getCurrentName())){
                List<? extends IEntity> listBaseEntity = (List<? extends IEntity>) list;
                gen.writeStartArray(listBaseEntity.size());
                for (int i=0; i<listBaseEntity.size(); i++){
                    gen.writeNumber((Long) listBaseEntity.get(i).getId());
                }
                gen.writeEndArray();
            }else {
                resolveSerializer(provider).serialize(list, gen, provider);
            }
        }

        private JsonSerializer resolveSerializer(SerializerProvider provider) throws JsonMappingException {
            if(serializer == null){
                serializer = BeanSerializerFactory.instance.createSerializer(provider, TypeFactory.defaultInstance().constructType(List.class));
            }
            return serializer;
        }

        private boolean shouldReturnOnlyIds(List list, Object currentValue, String currentName) {
            return (!list.isEmpty() && list.get(0) instanceof IEntity)
                    && !(currentValue instanceof GenericPaginatedResource)
                    && !(nestedEntities != null && nestedEntities.contains(currentName));
        }
    }

    static class Builder {
        private Set<String> nestedEntities;
        public Builder withEntityToLoad(Set<String> nestedEntities){
            this.nestedEntities = nestedEntities;
            return this;
        }

        public static Builder getBuilder(){
            return new Builder();
        }

        public GenericModule build(){
            if(nestedEntities != null){
                return new GenericModule(nestedEntities);
            }
            return new GenericModule(Collections.emptySet());
        }
    }

}