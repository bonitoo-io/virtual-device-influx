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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
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

  static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public IFluxDevice(){
 //   super();
    this.setSampleList(new ArrayList<>());
  }

  private static void generateCurrent(List<InfluxDevice> devices){
    // TODO - in future (post POC) encapsulate in a runner class -

    // Start Runner

    long startTime = System.currentTimeMillis();
    logger.info("Start Time  " + new Date(startTime));
    logger.info("Config.TTL  " + Config.getTTL());

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
    logger.info("Stop Time " + new Date(stopTime));
    logger.info("Run Time " + (stopTime - startTime) + " ms");
  }

  private static void generatePast(List<InfluxDevice> devices){

    logger.info("generating past values");
    logger.info("     start instant " + Config.getTimePeriodConf().getStart());
    logger.info("     end instant   " + Config.getTimePeriodConf().getStop());
    logger.info("     grit   " + Config.getProp(Config.TIME_PERIOD_GRIT_KEY, Config.DEFAULT_PAST_GEN_GRIT));


    ExecutorService service = Executors.newFixedThreadPool(devices.size());

    devices.forEach(service::execute);

    // Cleanup

    try {
      boolean terminated = service.awaitTermination(Config.getTTL(), TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    service.shutdown();

  }

  public static void main(String[] args) throws JsonProcessingException, MalformedURLException {

    // Read Config

    Config.readProps();

    for(Object key: Config.getProps().keySet()){
      logger.debug("Config property " + key + ":" + Config.getProps().get(key));
    }

    Config.readDeviceConfig();

    ObjectWriter owy = new ObjectMapper(new YAMLFactory()).writer().withDefaultPrettyPrinter();

    List<InfluxDevice> devices = new ArrayList<>();

    for(int i = 0; i < Config.getDeviceConfig().getCount(); i++){
      InfluxDevice influxDev;
      logger.debug("config.id " + Config.getDeviceConfig().getId());
      if(Config.getDeviceConfig().getCount() == 1) {
        influxDev = new InfluxDevice(Config.getDeviceConfig());
      } else {
        // each device gets its own copy of the config
        influxDev = new InfluxDevice(new InfluxDeviceConfig(Config.getDeviceConfig(), i+1));
      }
      devices.add(influxDev);
    }

    logger.info("Target host " + devices.get(0).getClient().getUrl());
    logger.info("Target org  " + devices.get(0).getClient().getOrg());
    logger.info("Target db   " + devices.get(0).getClient().getBucket());

    if(Config.generatePast()) {
      generatePast(devices);
    }else{
      generateCurrent(devices);
    }

    // Setup Runner

  }

}
