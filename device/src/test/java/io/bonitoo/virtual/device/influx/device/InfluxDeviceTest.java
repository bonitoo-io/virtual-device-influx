package io.bonitoo.virtual.device.influx.device;

import io.bonitoo.virtual.device.influx.conf.InfluxDeviceConfig;
import io.bonitoo.virtual.device.influx.conf.InfluxSampleConfig;
import io.bonitoo.virtual.device.influx.conf.StandardConfs;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class InfluxDeviceTest {

  @Test
  public void createDeviceTest() throws MalformedURLException {

    InfluxSampleConfig sampleConf = new InfluxSampleConfig("random", "test", "test/test",
      StandardConfs.getAllItemConfigs(), new ArrayList<>());

    InfluxDeviceConfig deviceConfig = new InfluxDeviceConfig("random", "testDevice", "this is just a test",
      1000L, StandardConfs.getClientConf1(), List.of(sampleConf));

    InfluxDevice device = new InfluxDevice(deviceConfig);

    System.out.println("DEBUG device " + device);

  }

}
