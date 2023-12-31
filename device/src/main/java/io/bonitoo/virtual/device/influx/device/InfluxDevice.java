package io.bonitoo.virtual.device.influx.device;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.bonitoo.qa.VirtualDeviceRuntimeException;
import io.bonitoo.qa.conf.data.SampleConfig;
import io.bonitoo.qa.data.GenericSample;
import io.bonitoo.qa.data.Sample;
import io.bonitoo.qa.device.Device;
import io.bonitoo.qa.util.LogHelper;
import io.bonitoo.virtual.device.influx.client.InfluxClient;
import io.bonitoo.virtual.device.influx.client.SampleWriter;
import io.bonitoo.virtual.device.influx.conf.Config;
import io.bonitoo.virtual.device.influx.conf.InfluxDeviceConfig;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

@Getter
@Setter
public class InfluxDevice extends Device {

  static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  InfluxClient client;

  Map<String, List<String>> stringFields = new HashMap<>();

  public InfluxDevice() {
    this.setConfig(null);
    this.setSampleList(new ArrayList<>());
    this.client = null;
  }

  public InfluxDevice(InfluxDeviceConfig config) throws MalformedURLException {
    this();
    this.setConfig(config);
    for(SampleConfig sc : config.getFluxSamples()){
      this.getSampleList().add(GenericSample.of(sc));
    }
    this.client = InfluxClient.of(config.getClient());
  }

  public InfluxDevice readyConfig(InfluxDeviceConfig config){
    this.setConfig(config);
    this.setSampleList(new ArrayList<>());
    for(SampleConfig sc : config.getSamples()){
      this.getSampleList().add(GenericSample.of(sc));
    }
    return this;
  }

  public InfluxDevice readySamples(List<Sample> list){
    this.setSampleList(list);
    return this;
  }

  public InfluxDevice addSample(Sample sample){
    this.getSampleList().add(sample);
    return this;
  }

  /*
     String fields are used to specify whether a String item
     should be serialized as a tag or as a field.  Default is
     tag serialization.  If a string item should be serialized
     as a field, it should be added to the string field list.

     Key is sample name.
   */
  public InfluxDevice setStringFields(HashMap<String, List<String>> fieldsList){
    this.stringFields = fieldsList;
    return this;
  }

  protected void generatePastValues(){

    logger.info("generating past values");
    long grit = (long) Config.getProp(Config.TIME_PERIOD_GRIT_KEY, Config.DEFAULT_PAST_GEN_GRIT);

    if(Config.getTimePeriodConf() == null){
      throw new VirtualDeviceRuntimeException("Expected Config.timePeriodConf to not be null");
    }

    long nowInstant = Config.getTimePeriodConf().getStart().toEpochMilli();
    long endInstant = Config.getTimePeriodConf().getStop().toEpochMilli();

    try {
      while (nowInstant < endInstant) {
        for (Sample sample : this.getSampleList()) {
          String jsonSample = sample.update().toJson();
          sample.setTimestamp(nowInstant);
          logger.debug(String.format("publishing %s \n", jsonSample));
          SampleWriter.writeSample(sample, this.client, stringFields.get(sample.getName()));
        }
        nowInstant += this.getConfig().getInterval();
        // TODO - if this is necessary find better way
        // pause 100ms to handle backend write
        LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(grit));
      }
    } catch(JsonProcessingException jpe){
      throw new RuntimeException(jpe);
    }finally{
      System.out.flush();  // TODO remove after cleanup of any System.out messages
      client.close();
    }

  }

  protected void generateCurrentValues(){

    logger.info("generating current values");

    long ttl = System.currentTimeMillis() + Config.getTTL();

    try {
      while (System.currentTimeMillis() < ttl) {
        logger.debug(String.format("publishing at %d \n", (ttl - System.currentTimeMillis()) ));
        System.out.flush();
        for (Sample sample : this.getSampleList()) {
          String jsonSample = sample.update().toJson();
          logger.debug(String.format("publishing %s \n", jsonSample));
          System.out.flush();
          SampleWriter.writeSample(sample, this.client, stringFields.get(sample.getName()));
        }
        LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(this.getConfig().getInterval()));
      }
      logger.debug(String.format("Published at %d\n", (ttl - System.currentTimeMillis())));
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    } finally {
        client.close();
    }
  }

  @Override
  public void run(){

    // TODO - if Config.getTimePeriodConf is not NULL generate past values instead.  Add Timestamps
    // see SampleWriter.writeSampleWithTimestamp();

    if(Config.generatePast()){
      if(Config.getTimePeriodConf() != null ){
        generatePastValues();
      } else {
        throw new VirtualDeviceRuntimeException(
          "Want to generate past value but no time.period defined in virtdevInflux.props."
        );
      }
    } else {
      generateCurrentValues();
    }
/*
    long ttl = System.currentTimeMillis() + Config.getTTL();

    try {
      while (System.currentTimeMillis() < ttl) {
  //      logger.debug(LogHelper.buildMsg(config.getId(),
  //        "Wait to publish",
  //        Long.toString((ttl - System.currentTimeMillis()))));
  //      LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(config.getJitter()));
        System.out.printf("DEBUG publishing at %d \n", (ttl - System.currentTimeMillis()) );
        System.out.flush();
        for (Sample sample : this.getSampleList()) {
          String jsonSample = sample.update().toJson();
  //        logger.info(LogHelper.buildMsg(sample.getId(), "Publishing", jsonSample));
//          client.getClient().publish(sample.getTopic(), jsonSample);
          System.out.printf("DEBUG publishing %s \n", jsonSample);
          System.out.flush();
          SampleWriter.writeSample(sample, this.client, stringFields.get(sample.getName()));
        }
        LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(this.getConfig().getInterval()));
      }
//      logger.debug(LogHelper.buildMsg(config.getId(),
//        "Published",
//        Long.toString((ttl - System.currentTimeMillis()))));
      System.out.printf("Published at %d\n", (ttl - System.currentTimeMillis()));
      System.out.flush();
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    } finally {
      System.out.println("DEBUG finally " + (ttl - System.currentTimeMillis()));
      System.out.flush();
      try {
        client.getClient().close();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    System.out.println("DEBUG end RUN");
*/
  }

}
