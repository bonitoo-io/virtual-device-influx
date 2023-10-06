package io.bonitoo.virtual.device.influx;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.bonitoo.virtual.device.influx.conf.Config;
import io.bonitoo.virtual.device.influx.conf.InfluxDeviceConfig;
import io.bonitoo.virtual.device.influx.device.InfluxDevice;
import lombok.Getter;
import lombok.Setter;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class IFluxDevice extends io.bonitoo.qa.device.Device {

  public IFluxDevice(){
 //   super();
    this.setSampleList(new ArrayList<>());
  }

  private static void generateCurrent(List<InfluxDevice> devices){
    // TODO - in future (post POC) encapsulate in a runner class -

    // Start Runner

    long startTime = System.currentTimeMillis();
    System.out.println("DEBUG Start Time " + new Date(startTime));
    System.out.println("DEBUG Config.getTTL() " + Config.getTTL());

    ExecutorService service = Executors.newFixedThreadPool(devices.size());

    devices.forEach(service::execute);

    // Cleanup

    try {
      boolean terminated = service.awaitTermination(Config.getTTL(), TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    service.shutdown();
    long stopTime = System.currentTimeMillis();
    System.out.println("DEBUG Stop Time " + new Date(stopTime));
    System.out.println("DEBUG runTime " + (stopTime - startTime));
  }

  public static void main(String[] args) throws JsonProcessingException, MalformedURLException {

    // Read Config

    Config.readProps();

    System.out.println("DEBUG Config.props");
    for(Object key: Config.getProps().keySet()){
      System.out.println("   DEBUG " + key + ":" + Config.getProps().get(key));
    }

    Config.readDeviceConfig();

    ObjectWriter owy = new ObjectMapper(new YAMLFactory()).writer().withDefaultPrettyPrinter();

    System.out.println("DEBUG Config.getDeviceConfig()\n" + owy.writeValueAsString(Config.getDeviceConfig()));

    System.out.println("DEBUG count " + Config.getDeviceConfig().getCount());

    List<InfluxDevice> devices = new ArrayList<>();

    for(int i = 0; i < Config.getDeviceConfig().getCount(); i++){
      InfluxDevice influxDev;
      System.out.println("DEBUG config.id " + Config.getDeviceConfig().getId());
      if(Config.getDeviceConfig().getCount() == 1) {
        influxDev = new InfluxDevice(Config.getDeviceConfig());
      } else {
        // each device gets its own copy of the config
        influxDev = new InfluxDevice(new InfluxDeviceConfig(Config.getDeviceConfig(), i+1));
      }
      devices.add(influxDev);
    }

    generateCurrent(devices);

    // Setup Runner

  }

}
