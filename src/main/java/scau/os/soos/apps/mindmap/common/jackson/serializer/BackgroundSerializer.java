package scau.os.soos.apps.mindmap.common.jackson.serializer;

import scau.os.soos.apps.mindmap.util.StyleUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Background;

import java.io.IOException;

public class BackgroundSerializer extends JsonSerializer<Background> {
    @Override
    public void serialize(Background background, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        String v = StyleUtil.getBackgroundColor(background);
        jsonGenerator.writeString(v);
    }
}
