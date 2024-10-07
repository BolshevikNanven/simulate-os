package scau.os.soos.apps.mindmap.common.jackson.serializer;

import scau.os.soos.apps.mindmap.util.StyleUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import javafx.scene.layout.Border;

import java.io.IOException;

public class BorderSerializer extends JsonSerializer<Border> {
    @Override
    public void serialize(Border border, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        String v = StyleUtil.getBorderColor(border);
        jsonGenerator.writeString(v);
    }
}
