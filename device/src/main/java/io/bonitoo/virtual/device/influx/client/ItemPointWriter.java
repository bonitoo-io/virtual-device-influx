package io.bonitoo.virtual.device.influx.client;

import com.influxdb.v3.client.write.Point;
import com.influxdb.v3.client.write.WriteParameters;
import com.influxdb.v3.client.write.WritePrecision;
import io.bonitoo.qa.data.Item;

import java.util.HashMap;
import java.util.Map;

public class ItemPointWriter {

  public static void writeDouble(Item it, InfluxClient ic, Map<String,String> tags){

    Point p = Point.measurement(it.getConfig().getName())
      .addField(it.getLabel(), it.asDouble());

    if(tags != null){
      p.addTags(tags);
    }

    ic.getClient().writePoint(p, new WriteParameters(ic.getBucket(), ic.getOrg(), WritePrecision.MS));

  }

}
