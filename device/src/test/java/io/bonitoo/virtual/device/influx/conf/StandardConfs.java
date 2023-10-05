package io.bonitoo.virtual.device.influx.conf;

import io.bonitoo.qa.conf.data.ItemConfig;
import io.bonitoo.qa.conf.data.ItemNumConfig;
import io.bonitoo.qa.conf.data.ItemStringConfig;
import io.bonitoo.qa.data.ItemType;
import io.bonitoo.qa.util.EncryptPass;

import java.util.List;

public class StandardConfs {

  public static InfluxClientConfig clientConf1 = new InfluxClientConfig("qa",
    "TEST_BUCKET",
    EncryptPass.encryptPass(Config.class.getCanonicalName().toCharArray(), Config.getOssToken().toCharArray()),
    "http://localhost:8086");

  public static ItemConfig icDouble1 = new ItemNumConfig("icDouble1",
    "dbl",
    ItemType.Double,
    -10,
    20,
    2.0,
    0.17);

  public static ItemConfig icDouble2 = new ItemNumConfig("icDouble2",
    "flt",
    ItemType.Double,
    -100,
    0,
    1,
    0.33);

  public static ItemConfig icLong1 = new ItemNumConfig("icLong1",
    "long",
    ItemType.Long,
    0,
    100,
    2,
    0.21);

  public static ItemConfig icString1 = new ItemStringConfig("icString1",
    "text",
    ItemType.String,
    List.of("Arsenal", "ManCity", "Liverpool", "NewCastle", "ManUnited"));

  public static ItemConfig getIcDouble1() {
    return icDouble1;
  }

  public static ItemConfig getIcDouble2() {
    return icDouble2;
  }

  public static ItemConfig getIcLong1() {
    return icLong1;
  }

  public static ItemConfig getIcString1() {
    return icString1;
  }

  public static InfluxClientConfig getClientConf1() {
    return clientConf1;
  }

  public static List<ItemConfig> getAllItemConfigs(){
    return List.of(icDouble1, icDouble2, icLong1, icString1);
  }
}
