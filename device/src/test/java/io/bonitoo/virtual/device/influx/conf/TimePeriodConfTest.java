package io.bonitoo.virtual.device.influx.conf;

import io.bonitoo.qa.conf.VirDevConfigException;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class TimePeriodConfTest {

  @Test
  public void parseTimeValTest(){

    String isoDateTime = "2023-10-04T12:23:26.648120920Z";
    ZonedDateTime zdt = ZonedDateTime.parse(isoDateTime);
    System.out.println("DEBUG zdt " + zdt);

    Instant now = ZonedDateTime.now().toInstant();
    System.out.println("DEBUG now " + Instant.now());
    Instant pastResult = TimePeriodConf.timeValToInstant("-12h", now);
    Instant futureResult = TimePeriodConf.timeValToInstant("+30d", pastResult);
    Instant nowResult = TimePeriodConf.timeValToInstant("0ms", now);

    // N.B. now has Nano precision while nowResult has only Milli precision
    assertEquals(now.toEpochMilli(), nowResult.toEpochMilli());

    assertTrue(pastResult.compareTo(now) < 0);
    assertTrue(futureResult.compareTo(pastResult) > 0);
    assertTrue(futureResult.compareTo(now) > 0);

    System.out.println("DEBUG pastResult " + pastResult);
    System.out.println("DEBUG futureResult " + futureResult);

    assertThrows(VirDevConfigException.class, () -> TimePeriodConf.timeValToInstant("fooBar", now));

  }

  @Test
  public void parseDurationValTest(){

    Duration d1 = TimePeriodConf.parseTimeInterval("17ms");

    System.out.println("DEBUG d1 " + d1);

    // todo add asserts

  }

  @Test
  public void createTimePeriodConfigBetweenTimeValsBasic(){

    String startVal = "-3h";
    String stopVal = "now";

    TimePeriodConf confWithNow = TimePeriodConf.fromBetween(startVal, stopVal);

    System.out.println("DEBUG conf.getStart() " + confWithNow.getStart());
    System.out.println("DEBUG conf.getEnd() " + confWithNow.getStop());

    assertTrue(confWithNow.getStart().isBefore(confWithNow.getStop()));

  }

  @Test
  public void creatTimePeriodConfigBetweenTimeValsSwapped(){

    String startVal = "-3h";
    String stopVal = "now";

    TimePeriodConf confWithSwapped = TimePeriodConf.fromBetween(stopVal, startVal);

    System.out.println("DEBUG conf.getStart() " + confWithSwapped.getStart());
    System.out.println("DEBUG conf.getEnd() " + confWithSwapped.getStop());

    assertTrue(confWithSwapped.getStart().isBefore(confWithSwapped.getStop()));
  }

  @Test
  public void createTimePeriodConfigBetweeTimeValsISO(){

    String startVal = "2023-10-04T11:25:48.744Z";
    String stopVal = "2023-10-04T14:25:48.744Z";

    TimePeriodConf confWithISO = TimePeriodConf.fromBetween(stopVal, startVal);

    System.out.println("DEBUG conf.getStart() " + confWithISO.getStart());
    System.out.println("DEBUG conf.getEnd() " + confWithISO.getStop());

    assertTrue(confWithISO.getStart().compareTo(confWithISO.getStop()) < 0);

  }

  @Test
  public void createTimePeriodConfigWithSpan(){

    String origin = "-5d";
    String span = "3d";

    TimePeriodConf confFromSpan = TimePeriodConf.fromSpan(origin, span);

    System.out.println("DEBUG confFromSpan.start() " + confFromSpan.getStart());
    System.out.println("DEBUG confFromSpan.stop() " + confFromSpan.getStop());

  }

  @Test
  public void createTimePeriodConfigWithNegSpan(){
    String origin = "30m";
    String span = "-90m";

    TimePeriodConf confFromSpan = TimePeriodConf.fromSpan(origin, span);

    System.out.println("DEBUG confFromSpan.start() " + confFromSpan.getStart());
    System.out.println("DEBUG confFromSpan.stop() " + confFromSpan.getStop());
  }


}
