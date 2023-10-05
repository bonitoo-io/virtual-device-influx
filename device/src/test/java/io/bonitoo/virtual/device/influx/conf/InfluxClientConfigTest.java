package io.bonitoo.virtual.device.influx.conf;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InfluxClientConfigTest {

  @Test
  public void clientConfigTest(){

    InfluxClientConfig clientConfig = new InfluxClientConfig("qa",
      "TEST_BUCKET",
      "TEST_TOKEN",
      "http://localhost:8086");



    assertEquals("qa", clientConfig.getOrg());
    assertEquals("TEST_BUCKET", clientConfig.getBucket());
    assertEquals("TEST_TOKEN", clientConfig.getToken());
    assertEquals("http://localhost:8086", clientConfig.getHostUrl());

  }

  @Test
  public void serializeYaml() throws JsonProcessingException {
    InfluxClientConfig conf = new InfluxClientConfig("qa",
      "Test_Bucket", "TEST_TOKEN", "http://localhost:8086");

    String confYaml = """
      ---
      org: "qa"
      bucket: "Test_Bucket"
      token: "TEST_TOKEN"
      hostUrl: "http://localhost:8086"
      """;

    ObjectMapper omy = new ObjectMapper(new YAMLFactory());

    InfluxClientConfig confParsed = omy.readValue(confYaml, InfluxClientConfig.class);

    assertEquals(conf, confParsed);

  }

}
