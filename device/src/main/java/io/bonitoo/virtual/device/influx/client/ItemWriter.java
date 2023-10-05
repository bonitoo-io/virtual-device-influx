package io.bonitoo.virtual.device.influx.client;

import com.influxdb.v3.client.write.WriteParameters;
import com.influxdb.v3.client.write.WritePrecision;
import io.bonitoo.qa.data.Item;

public class ItemWriter {

  public static void writeDouble(Item it, InfluxClient client){
    String data = String.format("%s,label=%s _value=%f",
      it.getConfig().getName(),
      it.getLabel(),
      it.asDouble());

    client.getClient().writeRecord(
      data, new WriteParameters(client.getBucket(), client.getOrg(), WritePrecision.MS)
    );
  }

}
