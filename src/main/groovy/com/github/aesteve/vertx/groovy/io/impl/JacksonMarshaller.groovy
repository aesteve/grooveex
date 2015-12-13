package com.github.aesteve.vertx.groovy.io.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.aesteve.vertx.groovy.io.Marshaller
import groovy.transform.TypeChecked

@TypeChecked
class JacksonMarshaller implements Marshaller {

    ObjectMapper mapper

    public JacksonMarshaller() {
        mapper = new ObjectMapper()
    }

    @Override
    String marshall(Object obj) {
        mapper.writeValueAsString(obj)
    }

    @Override
    def unmarshall(String json, Class type) {
        mapper.readValue(json, type)
    }
}
