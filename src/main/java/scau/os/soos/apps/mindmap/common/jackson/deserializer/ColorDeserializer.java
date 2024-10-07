package scau.os.soos.apps.mindmap.common.jackson.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Paint;

import java.io.IOException;

public class ColorDeserializer extends JsonDeserializer<Paint> {
    @Override
    public Paint deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        if (node != null) {
            return Paint.valueOf(node.asText());
        }
        throw new JsonParseException("color字段不存在");
    }
}
