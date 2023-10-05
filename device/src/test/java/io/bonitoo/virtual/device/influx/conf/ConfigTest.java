package io.bonitoo.virtual.device.influx.conf;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.bonitoo.qa.conf.data.ItemConfigRegistry;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigTest {

  @Test
  public void readPropertiesTest(){
    Config.readProps();
//    for(String propName : Config.getProps().stringPropertyNames()){
//      System.out.println("DEBUG prop " + propName + " : " + Config.getProps().getProperty(propName));
//    }

    assertEquals("ossDeviceConfig.yml", Config.getProps().getProperty("device.conf"));
    assertEquals(30000, Integer.valueOf(Config.getProps().getProperty("default.ttl")));
    assertFalse(Config.generatePast());

  }

  @Test
  public void readDeviceConfigTest() throws JsonProcessingException {
    Config.readDeviceConfig();
    ObjectWriter owy = new ObjectMapper(new YAMLFactory()).writer().withDefaultPrettyPrinter();
//    System.out.println("DEBUG deviceConfig:\n" + owy.writeValueAsString(Config.getDeviceConfig()));
    assertEquals(1, Config.getDeviceConfig().getFluxSamples().size());
    assertEquals(0, Config.getDeviceConfig().getSamples().size());
    assertEquals(3, Config.getDeviceConfig().getFluxSamples().get(0).getItems().size());
    assertNotNull(Config.getDeviceConfig().getFluxSamples().get(0).getItems().get(0));
//    System.out.println("DEBUG ItemConfigRegistry " + ItemConfigRegistry.keys().size());
//    for(String key : ItemConfigRegistry.keys()){
//      System.out.println("   DEBUG Item key " + key);
//    }
  }

  @Test
  public void replaceVMVarKeyWithEnvVarKey(){
    String vmVarKey = "default.wumpus.hunter";
//    System.out.println("DEBUG " + vmVarKey + " -> " + Config.VMVarKeyToEnvVarKey(vmVarKey));
    assertEquals(Config.ENVAR_PREFIX + "_DEFAULT_WUMPUS_HUNTER", Config.VMVarKeyToEnvVarKey(vmVarKey));
  }

}
