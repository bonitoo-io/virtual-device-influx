package io.bonitoo.virtual.device.influx.conf;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.bonitoo.qa.conf.data.ItemConfig;
import io.bonitoo.qa.conf.data.SampleConfig;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InfluxDeviceConfigTest {

  InfluxClientConfig clientConfig = new InfluxClientConfig("qa",
    "TEST_BUCKET",
    "TEST_TOKEN",
    "http://localhost:8086");


  @Test
  public void deviceConfigConstructor1Test(){

    InfluxDeviceConfig config = new InfluxDeviceConfig(
      "test",
      "testMe",
      "test device",
      30000L,
      clientConfig,
      new ArrayList<>() // flux samples
    );

    assertEquals("qa", config.getClient().getOrg());
    assertEquals("TEST_BUCKET", config.getClient().getBucket());
    assertEquals("TEST_TOKEN", config.getClient().getToken());
    assertEquals("http://localhost:8086", config.getClient().getHostUrl());
    assertEquals("test", config.getId());
    assertEquals("testMe", config.getName());
    assertEquals("test device", config.getDescription());
    assertEquals(30000L, config.getInterval());
    assertNotNull(config.getFluxSamples());
    assertEquals(0, config.getSamples().size()); //MQTT style samples will be ignored

  }

  @Test
  public void deviceConfigConstructor2Test(){

    InfluxDeviceConfig config = new InfluxDeviceConfig(
      "test",
      "testMe",
      "test device",
      null, // MQTT samples not needed
      30000L,
      10L,
      1,
      clientConfig,
      new ArrayList<>()
    );

    assertEquals("qa", config.getClient().getOrg());
    assertEquals("TEST_BUCKET", config.getClient().getBucket());
    assertEquals("TEST_TOKEN", config.getClient().getToken());
    assertEquals("http://localhost:8086", config.getClient().getHostUrl());
    assertEquals("test", config.getId());
    assertEquals("testMe", config.getName());
    assertEquals("test device", config.getDescription());
    assertEquals(30000L, config.getInterval());
    assertEquals(1, config.getCount());
    assertEquals(10L, config.getJitter());
    assertNotNull(config.getFluxSamples());
    assertNull(config.getSamples()); //MQTT style samples will be ignored

  }

  private static String deviceConfYaml = """
---
id: "6b6aaff5-0e2b-4dfa-a0e5-7aa1433cc789"
name: "testDevice"
description: "A Device For Testing"
client:
  org: "qa"
  bucket: "TEST_BUCKET"
  token: "TEST_TOKEN"
  hostUrl: "http://localhost:8086"
samples:
- name: "sConfig1"
  id: "ceced7ec-8427-4000-b6d5-d2902c9003eb"
  topic: "temp/ignore"
  items:
  - name: "icDouble1"
    label: "dbl"
    type: "Double"
    genClassName: "io.bonitoo.qa.data.generator.NumGenerator"
    count: 1
    arType: "Undefined"
    max: 20.0
    min: -10.0
    period: 2.0
    dev: 0.17
  - name: "icDouble2"
    label: "flt"
    type: "Double"
    genClassName: "io.bonitoo.qa.data.generator.NumGenerator"
    count: 1
    arType: "Undefined"
    max: 0.0
    min: -100.0
    period: 1.0
    dev: 0.33
  - name: "icString1"
    label: "text"
    type: "String"
    genClassName: "io.bonitoo.qa.data.generator.SimpleStringGenerator"
    count: 1
    arType: "Undefined"
    values:
    - "Arsenal"
    - "ManCity"
    - "Liverpool"
    - "NewCastle"
    - "ManUnited"
  arType: "Undefined"
  plugin: null
  stringFields:
  - "icString1"
interval: 1000
jitter: 0
count: 3
    """;

  @Test
  public void deserializeTest() throws JsonProcessingException {
    List<String> stringFields = List.of(StandardConfs.getIcString1().getName());

    InfluxSampleConfig sConfig1 = new InfluxSampleConfig("random", "sConfig1", "temp/ignore",
      List.of(StandardConfs.getIcDouble1(),
        StandardConfs.getIcDouble2(),
        StandardConfs.getIcString1()),
        stringFields);

    InfluxDeviceConfig devConf = new InfluxDeviceConfig("random",
      "testDevice",
      "A Device For Testing",
      1000L,
      StandardConfs.getClientConf1(),
      List.of(sConfig1));

    System.out.println("DEBUG devConf " + devConf);

    ObjectMapper omy = new ObjectMapper(new YAMLFactory());
    ObjectWriter owy = omy.writer().withDefaultPrettyPrinter();

    System.out.println("DEBUG devConf Yaml\n" + owy.writeValueAsString(devConf));

    InfluxDeviceConfig devConfParsed = omy.readValue(deviceConfYaml, InfluxDeviceConfig.class);
    // TODO try and parse deviceConfYaml above

    System.out.println("DEBUG devConfParsed " + devConfParsed);

  }

}
