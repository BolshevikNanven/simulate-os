package scau.os.soos.apps.mindmap.common.jackson.deserializer;

import scau.os.soos.apps.mindmap.util.StyleUtil;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import javafx.scene.layout.Border;

import java.io.IOException;

public class BorderDeserializer extends JsonDeserializer<Border> {
    @Override
    public Border deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        if (node != null) {
            return StyleUtil.newBorder(node.asText());
        }
        throw new JsonParseException("background字段不存在");
    }
}
