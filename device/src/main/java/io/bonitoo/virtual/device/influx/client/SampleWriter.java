package io.bonitoo.virtual.device.influx.client;

import com.influxdb.v3.client.write.Point;
import com.influxdb.v3.client.write.WriteParameters;
import com.influxdb.v3.client.write.WritePrecision;
import io.bonitoo.qa.conf.VirDevConfigException;
import io.bonitoo.qa.conf.data.ItemConfig;
import io.bonitoo.qa.conf.data.ItemStringConfig;
import io.bonitoo.qa.data.Item;
import io.bonitoo.qa.data.ItemType;
import io.bonitoo.qa.data.Sample;
import io.bonitoo.virtual.device.influx.conf.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.List;

public class SampleWriter {

  static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static class Field {
    Double _double = null;
    Long _long = null;
    String _string = null;
    public Field(String string){ this._string = string;}
    public Field(Double d){ this._double = d;}
    public Field(Long l){this._long = l;}

    Double getDouble(){ return this._double;}
    Long getLong(){ return this._long;}
    String getString(){return this._string;}
  }

  private static String getMeasurement(Sample sample){
    if(Config.getProp(Config.MEASUREMENT_FIELD_KEY) == null){
      return sample.getName();
    }

    String mField = Config.getProp(Config.MEASUREMENT_FIELD_KEY).toString().toLowerCase();

    switch (mField.toLowerCase()) {
      case "name" -> {
        return sample.getName();
      }
      case "id" -> {
        return sample.getId();
      }
      case "topic" -> {
        return sample.getTopic().substring(sample.getTopic().lastIndexOf("/") + 1);
      }
      default -> {
        // Find an ItemStringConfig with name matching property from config
        // and return its first value as the measurement.
        // Should work with devices with multiple samples
        //
        // Matching item should be removed from sample, so that it is not serialized as tag or field

        List<ItemConfig> candidates = sample
          .getConfig()
          .getItems()
          .stream()
          .filter(item -> item.getType() == ItemType.String
          && item.getName().equals(mField))
          .toList();

        if (candidates.size() == 0) {
          throw new VirDevConfigException(
            String.format("No field matching %s found.  Cannot create measurement field.", mField)
          );
        }

        // TODO should the matching Item then be dropped from the items list?
        sample.getItems().remove(mField);

        return ((ItemStringConfig) candidates.get(0)).getValues().get(0);
      }
    }
  }

  private static class LPHolder {
    final HashMap<String, String> tags;
    final HashMap<String, Field> fields;

    String name;

    public LPHolder(Sample sample, List<String> stringFields){
      this.tags = new HashMap<>();
      this.fields = new HashMap<>();
      tags.put("id", sample.getId());
      for(String key : sample.getItems().keySet()){
        Item item = sample.getItems().get(key).get(0);
        String label = sample.getItems().get(key).get(0).getLabel();
        // TODO handle item arrays
        switch (item.getType()) {
          case Double -> this.fields.put(label, new Field(item.asDouble()));
          case Long -> this.fields.put(label, new Field(item.asLong()));
          case String -> this.tags.put(label, item.asString());
          default -> throw new RuntimeException(String.format("Unhandled Item Type: %s",
            item.getType()
          ));
        }
      }

     // this.name = this.tags.containsKey("name") ? this.tags.get("name") :
       // this.tags.containsKey("Name") ? this.tags.get("Name") :
        //  sample.getId();
         // sample.getTopic().substring(sample.getTopic().lastIndexOf("/") + 1);

      this.name = getMeasurement(sample);

     // this.tags.remove("name");
     // this.tags.remove("Name");

      if(this.fields.size() == 0 && this.tags.size() < 2){
        throw new RuntimeException("No available fields in Sample");
      }

      if(stringFields != null) {
        for (String fieldKey : stringFields) {
          if(tags.containsKey(fieldKey)){
            this.fields.put(fieldKey, new Field(this.tags.get(fieldKey)));
            this.tags.remove(fieldKey);
          }
        }
      }
    }

    public HashMap<String, String> getTags(){return this.tags;}
    public HashMap<String, Field> getFields(){return this.fields;}

    public String getName(){return this.name;}

  }

  // stringFields is a list of String values to be used as fields instead of tags
  public static void writeSample(Sample sample, InfluxClient ic, List<String> stringFields){

    System.out.println("DEBUG InfluxClient ic:  " + ic);

    LPHolder lpHolder = new LPHolder(sample, stringFields);

    StringBuilder lineProtocol = new StringBuilder(lpHolder.getName());

    for(String key: lpHolder.getTags().keySet()){
      if(!key.equals(lpHolder.getName())) {
        lineProtocol.append(String.format(",%s=%s", key, lpHolder.getTags().get(key)));
      }
    }

    lineProtocol.append(" ");

    int index = 0;
    for(String key: lpHolder.getFields().keySet()){
      Field f = lpHolder.getFields().get(key);
      if(f.getDouble() != null){
        lineProtocol.append(String.format("%s=%f", key, f.getDouble()));
      }else if(f.getLong() != null){
        lineProtocol.append(String.format("%s=%d", key, f.getLong()));
      }else if(f.getString() != null){
        if(!key.equals(lpHolder.getName())) {
          lineProtocol.append(String.format("%s=\"%s\"", key, f.getString()));
        }
      }else{
        throw new RuntimeException(String.format("Field has no value that can be handled. %s", f));
      }
      if(! (index++ == lpHolder.getFields().keySet().size() - 1)){
        lineProtocol.append(",");
      }
    }

    if(sample.getTimestamp() > 0){
      lineProtocol.append(String.format(" %d", sample.getTimestamp()));
    }

    logger.info("Writing lineProtocol " + lineProtocol);

    ic.getClient().writeRecord(lineProtocol.toString(),
      new WriteParameters(ic.getBucket(),
      ic.getOrg(),
      WritePrecision.MS)
    );

  }

  public static void writeSamplePoint(Sample sample, InfluxClient ic, List<String> stringFields){

    LPHolder lpHolder = new LPHolder(sample, stringFields);

    Point p = Point.measurement(lpHolder.getName());

    for(String key : lpHolder.getTags().keySet()){
      p.addTag(key, lpHolder.getTags().get(key));
    }

    for(String key : lpHolder.getFields().keySet()){
      Field f = lpHolder.getFields().get(key);
      if(f.getDouble() != null){
        p.addField(key, f.getDouble());
      }else if(f.getLong() != null){
        p.addField(key, f.getLong());
      }else if(f.getString() != null){
        p.addField(key, f.getString());
      }else{
        throw new RuntimeException(String.format("Field has no value that can be handled. %s", f));
      }
    }

    ic.getClient().writePoint(p, new WriteParameters(ic.getBucket(), ic.getOrg(), WritePrecision.MS));

  }

}
