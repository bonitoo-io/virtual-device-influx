package io.bonitoo.virtual.device.influx.conf;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.bonitoo.qa.conf.data.SampleConfig;
import io.bonitoo.qa.conf.device.DeviceConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

// TODO introduce runner config in separate class with pre-declarations of items and samples

@Getter
@Setter
@NoArgsConstructor
@JsonDeserialize(using = InfluxDeviceConfigDeserializer.class)
public class InfluxDeviceConfig extends DeviceConfig {

  InfluxClientConfig client;

  @JsonProperty("samples")
  List<InfluxSampleConfig> fluxSamples;

  public InfluxDeviceConfig(String id,
                            String name,
                            String description,
                            Long interval,
                            InfluxClientConfig client,
                            List<InfluxSampleConfig> fluxSamples){
    super(id, name, description, new ArrayList<>(), interval, 0L, 1);
    this.client = client;
    this.fluxSamples = fluxSamples;
  }

  public InfluxDeviceConfig(String id,
                            String name,
                            String description,
                            List<SampleConfig> samples,
                            Long interval,
                            Long jitter,
                            int count,
                            InfluxClientConfig client,
                            List<InfluxSampleConfig> fluxSamples) {
    super(id, name, description, samples, interval, jitter, count);
    this.client = client;
    this.fluxSamples = fluxSamples;
  }

  public InfluxDeviceConfig(String id,
                           String name,
                           String description,
                           Long interval,
                           Long jitter,
                           int count,
                           InfluxClientConfig client,
                           List<InfluxSampleConfig> fluxSamples){
    super(id, name, description, new ArrayList<>(), interval, jitter, count);
    this.client = client;
    this.fluxSamples = fluxSamples;
  }

  public InfluxDeviceConfig(InfluxDeviceConfig deviceConfig, int num) {
    super(deviceConfig, num);
    System.out.println("DEBUG config copy constructor id " + this.getId());
    this.client = deviceConfig.getClient();
    this.fluxSamples = new ArrayList<>();
    for(InfluxSampleConfig sample: deviceConfig.getFluxSamples()){
      InfluxSampleConfig isc = new InfluxSampleConfig(sample);
      if(num > 0){
        isc.setId(String.format("%s-%03d", sample.getId(), num));
        isc.setName(String.format("%s-%03d", sample.getName(), num));
      }
      this.fluxSamples.add(isc);
    }
  }

  @Override
  public String toString(){
    StringBuffer samplesBuffer = new StringBuffer("[");
    for(InfluxSampleConfig sample : this.fluxSamples){
      samplesBuffer.append("\n" + sample.toString() + ",");
    }
    samplesBuffer.append("]");
    return String.format("client: %s, samples: %s",
      client.toString(), samplesBuffer);
  }

}
