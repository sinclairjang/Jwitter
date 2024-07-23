package org.zerobase.jwitter.domain.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;
import org.zerobase.jwitter.domain.model.Jweet;

import java.io.IOException;

@JsonComponent
public class JweetCommentsSerializer extends JsonSerializer<Jweet> {

    @Override
    public void serialize(Jweet value, JsonGenerator gen,
                          SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeFieldName("authorId");
        gen.writeNumber(value.getAuthorId());
        gen.writeFieldName("text");
        gen.writeNumber(value.getText());
        gen.writeFieldName("likes");
        gen.writeNumber(value.getLikes());
        gen.writeFieldName("createdAt");
        gen.writeNumber(value.getCreatedAt());
        gen.writeFieldName("jweetComments");
        gen.writeString(getUrl(value));
        gen.writeEndObject();
        gen.close();
    }

    private String getUrl(Jweet jweet) {
        return String.format("http://localhost:8080/v1/jweet/%d/comment?page=?&size=?",
                jweet.getId());
    }

}
