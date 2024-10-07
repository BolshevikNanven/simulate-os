package scau.os.soos.apps.mindmap.common.jackson.deserializer;

import scau.os.soos.apps.mindmap.util.StyleUtil;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Background;

import java.io.IOException;

public class BackgroundDeserializer extends JsonDeserializer<Background> {
    @Override
    public Background deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        if (node != null) {
            return StyleUtil.newBackground(node.asText());
        }
        throw new JsonParseException("background字段不存在");
    }
}
