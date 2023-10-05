package io.bonitoo.virtual.device.influx;

import com.influxdb.v3.client.InfluxDBClient;
import com.influxdb.v3.client.query.QueryParameters;
import com.influxdb.v3.client.query.QueryType;
import com.influxdb.v3.client.write.Point;
import com.influxdb.v3.client.write.WriteParameters;
import io.netty.util.internal.ObjectUtil;

import java.util.stream.Stream;

public class WriteIox {

  public static void main(String[] args) throws Exception {
    System.out.println("Testing!");

    String hostUrl = "https://us-east-1-1.aws.cloud2.influxdata.com";

    String database = "CI_TEST";

    // TODO - encrypt below then delete
    char[] authToken = "H3BNo31Ue1LYFvrY9NmMnycdOaAdQ2AT6lpVz9DPtjTYHMCwa87pANw_Zs3vvv_Vhze3CG1Jw7rmzMmku4CSWg==".toCharArray();

    try (InfluxDBClient client = InfluxDBClient.getInstance(hostUrl, authToken, "CI_TEST")) {

      System.out.println("DEBUG client " + client);

      Point[] points = new Point[] {
        Point.measurement("census")
          .addTag("location", "Klamath")
          .addField("bees", 23),
        Point.measurement("census")
          .addTag("location", "Portland")
          .addField("ants", 30),
        Point.measurement("census")
          .addTag("location", "Klamath")
          .addField("bees", 28),
        Point.measurement("census")
          .addTag("location", "Portland")
          .addField("ants", 32),
        Point.measurement("census")
          .addTag("location", "Klamath")
          .addField("bees", 29),
        Point.measurement("census")
          .addTag("location", "Portland")
          .addField("ants", 40)
      };

      for (Point point : points) {
        client.writePoint(point, new WriteParameters(database, null, null));

        Thread.sleep(1000); // separate points by 1 second
      }

      System.out.println("Complete. Return to the InfluxDB UI.");

      String sql = "SELECT * " +
        "FROM 'census' " +
        "WHERE time >= now() - interval '1 hour' " +
        "AND ('bees' IS NOT NULL OR 'ants' IS NOT NULL) order by time asc";

      System.out.printf("| %-5s | %-5s | %-8s | %-30s |%n", "ants", "bees", "location", "time");

      // client.query(sql, new QueryParameters("CI_TEST", QueryType.SQL));
      // client.query(sql);

      try (Stream<Object[]> stream = client.query(sql, new QueryParameters("CI_TEST", QueryType.SQL))) {
        stream.forEach(row -> System.out.printf("| %-5s | %-5s | %-8s | %-30s |%n",  row[0], row[1], row[2], row[3]));
      }


    }

    }

}
