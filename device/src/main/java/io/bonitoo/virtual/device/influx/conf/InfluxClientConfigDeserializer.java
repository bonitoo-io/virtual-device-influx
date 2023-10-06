package io.bonitoo.virtual.device.influx.conf;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import io.bonitoo.qa.conf.VirDevDeserializer;

import java.io.IOException;

public class InfluxClientConfigDeserializer extends VirDevDeserializer {

  public InfluxClientConfigDeserializer(){
    super(null);
  }
  protected InfluxClientConfigDeserializer(Class vc) {
    super(vc);
  }

  @Override
  public InfluxClientConfig deserialize(JsonParser parser, DeserializationContext context) throws IOException {

    JsonNode node = parser.getCodec().readTree(parser);

    String org = safeGetNode(node, "org").asText();
    String bucket = safeGetNode(node, "bucket").asText();
    String token = safeGetNode(node, "token").asText();
    String hostUrl = safeGetNode(node, "hostUrl").asText();

    return new InfluxClientConfig(org, bucket, token, hostUrl);
  }
}
