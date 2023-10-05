package io.bonitoo.virtual.device.influx.conf;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize(using = InfluxClientConfigDeserializer.class)
public class InfluxClientConfig {

  String org;
  String bucket;
  String token;
  String hostUrl;

  @Override
  public String toString(){
    return String.format("org: %s, bucket: %s, token: %s, hostUrl: %s",
      org, bucket, token, hostUrl);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }

    if (obj instanceof final InfluxClientConfig conf) {
      return this.org.equals(conf.getOrg())
        && this.bucket.equals(conf.getBucket())
        && this.token.equals(conf.getToken())
        && this.hostUrl.equals(conf.getHostUrl());
    } else {
      return false;
    }
  }

}
