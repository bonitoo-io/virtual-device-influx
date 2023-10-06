package io.bonitoo.virtual.device.influx.client;


import com.influxdb.v3.client.InfluxDBClient;
import io.bonitoo.qa.util.EncryptPass;
import io.bonitoo.virtual.device.influx.conf.Config;
import io.bonitoo.virtual.device.influx.conf.InfluxClientConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.MalformedURLException;
import java.net.URL;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class InfluxClient {

  String org = "qa";
  String bucket = "bucket";

  URL url;

  InfluxDBClient client;

  public static URL buildLocalUrl() throws MalformedURLException {
    return new URL("http", "localhost", 8086, "");
  }

  public static InfluxClient of() throws MalformedURLException {
    InfluxClient ic = new InfluxClient();
    ic.url = buildLocalUrl();
    ic.client = InfluxDBClient.getInstance(ic.url.toString(),
      Config.getTOKEN().toCharArray(), ic.getBucket());
    return ic;
  }

  public static InfluxClient of(String org, String bucket, String url) throws MalformedURLException {
    InfluxClient ic = new InfluxClient();
    ic.url = new URL(url);
    ic.org = org;
    ic.bucket = bucket;
    ic.client = InfluxDBClient.getInstance(ic.url.toString(),
      Config.getTOKEN().toCharArray(),
      ic.getBucket());
    return ic;
  }

  public static InfluxClient of(InfluxClientConfig config) throws MalformedURLException {
    InfluxClient ic = new InfluxClient();
    ic.url = new URL(config.getHostUrl());
    ic.bucket = config.getBucket();
    ic.org = config.getOrg();
    System.out.println("DEBUG canonicalName: " + Config.class.getCanonicalName());
    System.out.println("DEBUG token " + config.getToken());
    char[] token = EncryptPass.passIsEncoded(config.getToken().toCharArray()) ?
      token = EncryptPass.decryptPass(Config.class.getCanonicalName().toCharArray(), config.getToken())
      : config.getToken().toCharArray();


    ic.client = InfluxDBClient.getInstance(ic.url.toString(),
      token,
      ic.getBucket());

    return ic;
  }

  /*
  public void writeWidget(Widget w){

    WriteApiBlocking writeApi = client.getWriteApiBlocking();
    writeApi.writeMeasurement(bucket, org, WritePrecision.MS, w);

  }

  public void writeDoubleItem(DoubleItem di){
    WriteApiBlocking writeApi = client.getWriteApiBlocking();
    writeApi.writeMeasurement(bucket, org, WritePrecision.MS, di);
  }

   */

  public void close(){
    try {
      client.close();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String toString(){
    return String.format("org: %s, bucket: %s, url: %s, client: %s",
      this.org, this.bucket, this.url, this.client);
  }

}
