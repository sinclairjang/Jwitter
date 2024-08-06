package org.zerobase.jwitter.domain.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PageJsonSerializer extends StdSerializer<Page> {
    public PageJsonSerializer() {
        this(null);
    }
    protected PageJsonSerializer(Class<Page> t) {
        super(t);
    }

    @Override
    public void serialize(Page page,
                          JsonGenerator jsonGenerator
            , SerializerProvider serializers) throws IOException {

        jsonGenerator.writeStartObject();
        jsonGenerator.writeObjectField("content", page.getContent());
        jsonGenerator.writeBooleanField("first", page.isFirst());
        jsonGenerator.writeBooleanField("last", page.isLast());
        jsonGenerator.writeNumberField("totalPages", page.getTotalPages());
        jsonGenerator.writeNumberField("totalElements", page.getTotalElements());
        jsonGenerator.writeNumberField("numberOfElements", page.getNumberOfElements());
        jsonGenerator.writeNumberField("size", page.getSize());
        jsonGenerator.writeNumberField("number", page.getNumber());
        jsonGenerator.writeEndObject();
    }
}
