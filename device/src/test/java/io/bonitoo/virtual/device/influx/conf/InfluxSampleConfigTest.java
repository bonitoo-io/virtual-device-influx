package io.bonitoo.virtual.device.influx.conf;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.bonitoo.qa.conf.data.ItemConfig;
import io.bonitoo.qa.conf.data.ItemNumConfig;
import io.bonitoo.qa.conf.data.ItemStringConfig;
import io.bonitoo.qa.data.ItemType;
import org.checkerframework.checker.units.qual.min;
import org.junit.jupiter.api.Test;

import java.util.List;

public class InfluxSampleConfigTest {

  static String influxSampleYaml = """
    ---
    name: "test"
    id: "9d816567-fd63-432e-b0da-18c44e05c554"
    topic: "test/ignore"
    items:
    - name: "Foo23"
      label: "foo"
      type: "Double"
      genClassName: "io.bonitoo.qa.data.generator.NumGenerator"
      count: 1
      arType: "Undefined"
      max: 20.0
      min: 0.0
      period: 1.0
      dev: 0.17
    - name: "Bar23"
      label: "bar"
      type: "Long"
      genClassName: "io.bonitoo.qa.data.generator.NumGenerator"
      count: 1
      arType: "Undefined"
      max: 100.0
      min: 0.0
      period: 0.5
      dev: 0.25
    - name: "Wumpus"
      label: "wump"
      type: "String"
      genClassName: "io.bonitoo.qa.data.generator.SimpleStringGenerator"
      count: 1
      arType: "Undefined"
      values:
      - "Smokey"
      - "Yogi"
      - "Beda"
    arType: "Undefined"
    plugin: null
    stringFields:
    - "Wumpus"
    """;

  @Test
  public void baseTest() throws JsonProcessingException {
    ItemConfig ic1 = new ItemNumConfig("Foo23", "foo", ItemType.Double, 0, 20, 1, 0.17 );
    ItemConfig ic2 = new ItemNumConfig("Bar23", "bar", ItemType.Long, 0, 100, 0.5, 0.25 );
    ItemConfig ic3 = new ItemStringConfig("Wumpus", "wump", ItemType.String, List.of("Smokey", "Yogi", "Beda"));

    InfluxSampleConfig sampConf = new InfluxSampleConfig("random", "test", "test/ignore",
      List.of(ic1, ic2, ic3), List.of("Wumpus"));

    System.out.println("DEBUG sampConf " + sampConf);

    ObjectMapper omy = new ObjectMapper(new YAMLFactory());
    ObjectWriter owy = omy.writer().withDefaultPrettyPrinter();
    System.out.println("DEBUG sampConf.yaml\n" + owy.writeValueAsString(sampConf));

    InfluxSampleConfig sampConfParsed = omy.readValue(influxSampleYaml, InfluxSampleConfig.class);

    System.out.println("DEBUG sampConfParsed \n" + sampConfParsed);


  }

}
