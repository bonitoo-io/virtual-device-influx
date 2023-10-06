package io.bonitoo.virtual.device.influx.conf;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TreeTraversingParser;
import io.bonitoo.qa.conf.data.SampleConfig;
import io.bonitoo.qa.conf.data.SampleConfigDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InfluxSampleConfigDeserializer extends SampleConfigDeserializer {

  public InfluxSampleConfigDeserializer(){
    super(null);
  }

  @Override
  public InfluxSampleConfig deserialize(JsonParser parser, DeserializationContext context)
    throws IOException {


    JsonNode node = parser.getCodec().readTree(parser);

    // Prepare to call already implemented deserializer
    TreeTraversingParser ttParser = new TreeTraversingParser(node);
    ttParser.setCodec(parser.getCodec());

    // Use already implemented deserializer
    SampleConfig sConfig = super.deserialize(ttParser, context);

    // prep deserialize stringFields
    List<String> stringFields = new ArrayList<>();

    // deserialize stringFields
    JsonNode stringFieldsNode = node.get("stringFields");

    if(stringFieldsNode != null){
      for(JsonNode sfNode: stringFieldsNode){
        if(sfNode.isTextual()){
          stringFields.add(sfNode.asText());
        }
      }
    }

    return new InfluxSampleConfig(sConfig, stringFields);

  }


}
