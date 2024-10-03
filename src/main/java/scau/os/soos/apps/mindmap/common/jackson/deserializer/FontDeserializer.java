package scau.os.soos.apps.mindmap.common.jackson.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import java.io.IOException;

public class FontDeserializer extends JsonDeserializer<Font> {
    @Override
    public Font deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        if (node != null) {
            String family = node.get("family").asText();
            String[] styles = node.get("style").asText().split(" ");
            String fontWeight = styles[0];
            String posture = styles[styles.length - 1];
            double size = node.get("size").asDouble();

            return Font.font(family, FontWeight.findByName(fontWeight), FontPosture.findByName(posture), size);
        }
        return null;
    }
}
