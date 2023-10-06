package io.bonitoo.virtual.device.influx.conf;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.bonitoo.qa.conf.data.ItemConfig;
import io.bonitoo.qa.conf.data.SampleConfig;
import io.bonitoo.qa.conf.data.SampleConfigRegistry;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonDeserialize(using = InfluxSampleConfigDeserializer.class)
public class InfluxSampleConfig extends SampleConfig {

  // list of names of string items
  // to be serialized as fields instead of tags
  List<String> stringFields;

  public InfluxSampleConfig(String id, String name, String topic, List<ItemConfig> items, List<String> stringFields) {
    super(id, name, topic, items);
    this.stringFields = stringFields;
    SampleConfigRegistry.add(this.getName(), this);
  }

  public InfluxSampleConfig(SampleConfig sampConfig, List<String> stringFields){
    super(sampConfig);
    this.stringFields = stringFields;
    // N.B. copy constructor in super does not add sample to registry
    SampleConfigRegistry.add(this.getName(), this);
  }

  public InfluxSampleConfig(InfluxSampleConfig sample) {
    super(sample);
    this.stringFields = new ArrayList<>();
    this.stringFields.addAll(sample.getStringFields());
  }

  @Override
  public String toString(){
    return String.format("%s, stringFields: %s\n", super.toString(), stringFields.toString());
  }

}
