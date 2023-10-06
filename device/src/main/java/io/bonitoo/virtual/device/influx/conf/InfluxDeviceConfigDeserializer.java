package io.bonitoo.virtual.device.influx.conf;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import io.bonitoo.qa.conf.VirDevConfigException;
import io.bonitoo.qa.conf.VirDevDeserializer;
import io.bonitoo.qa.conf.data.SampleConfigRegistry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InfluxDeviceConfigDeserializer extends VirDevDeserializer {

  public InfluxDeviceConfigDeserializer() {
    this(null);
  }
  protected InfluxDeviceConfigDeserializer(Class vc) {
    super(vc);
  }

  @Override
  public InfluxDeviceConfig deserialize(JsonParser parser, DeserializationContext context)
    throws IOException {

    // TODO deserialze fields: id, name, etc.

    JsonNode node = parser.getCodec().readTree(parser);

    String id = safeGetNode(node, "id").asText();
    String name = safeGetNode(node, "name").asText();
    String description = safeGetNode(node, "description").asText();
    int count = safeGetNode(node, "count").asInt();
    long jitter = safeGetNode(node, "jitter").asLong();


    JsonNode clientNode = safeGetNode(node, "client");
    //   String clientConfString = context.readValue(clientNode.traverse(parser.getCodec()), String.class);
    // System.out.println("DEBUG clientConfString \n" + clientNode.asText());
    InfluxClientConfig clientConf = context.readValue(clientNode.traverse(parser.getCodec()), InfluxClientConfig.class);

    JsonNode samplesNode = node.get("samples");
    List<InfluxSampleConfig> sampleConfigs = new ArrayList<>();
    for(JsonNode sampleNode : samplesNode){
 //     System.out.println("DEBUG getting sampleNode " + sampleNode);
      if (sampleNode == null) {
        throw new VirDevConfigException("Encountered null sampleNode.  "
          + "Cannot continue deserialization of device ");
      }
      if (sampleNode.isTextual()) {
        sampleConfigs.add((InfluxSampleConfig) SampleConfigRegistry.get(sampleNode.asText()));
      }else {
        sampleConfigs.add(context.readValue(sampleNode.traverse(parser.getCodec()), InfluxSampleConfig.class));
      }
    }
//    InfluxClientConfig clientConf = context.readValue(clientNode.traverse(parser.getCodec()), InfluxClientConfig.class);
//    InfluxClientConfig clientConf = null;


    return new InfluxDeviceConfig(id, name, description, 1000L, jitter, count, clientConf, sampleConfigs);
  }
}
