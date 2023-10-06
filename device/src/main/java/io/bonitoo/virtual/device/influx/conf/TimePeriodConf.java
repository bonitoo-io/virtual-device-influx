package io.bonitoo.virtual.device.influx.conf;

import io.bonitoo.qa.conf.VirDevConfigException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class TimePeriodConf {

  private static final long MS = 1;

  private static final long SECOND = 1000 * MS;
  private static final long MINUTE = 60 * SECOND;

  private static final long HOUR = 60 * MINUTE;

  private static final long DAY = 24 * HOUR;

  private static final String timeValPattern = "^[-+]??\\d*([smhd]{1}||ms)";
  private static final String timeUnitPattern = "([smhd]||ms){1}";
  //private static final String timeQuantityPattern = "\\d+";

  Instant start;
  Instant stop;

  /**
   * Example timeVal
   *   "-1h"
   *   "-3d"
   *   "15m"
   *
   * @param timeVal
   * @return
   */

  protected static long parseTimeValToLong(String timeVal){

    String parseMe = timeVal.equalsIgnoreCase("now") ? "0ms"
      : timeVal;


    if(!parseMe.matches(timeValPattern)){
      throw new VirDevConfigException(
        String.format("The pattern \"%s\" does not match the expected time value pattern." +
          "  e.g. -12m, 10h, +2d", timeVal)
      );
    }

    int quantity = Integer.parseInt(parseMe.replaceAll("[\\D]", ""));
    int sign = parseMe.startsWith("-") ? -1 : 1;

    String unit = parseMe.replaceAll("[\\d,\\-+]", "");

    return switch (unit) {
      case "d" -> quantity * DAY * sign;
      case "h" -> quantity * HOUR * sign;
      case "m" -> quantity * MINUTE * sign;
      case "s" -> quantity * SECOND * sign;
      case "ms" -> quantity * MS * sign;
      default -> throw new VirDevConfigException(
        String.format("Unhandled time unit %s", unit)
      );
    };
  }

  protected static Instant timeValToInstant(String timeVal, Instant from){
    return Instant.ofEpochMilli(parseTimeValToLong(timeVal) + from.toEpochMilli());
  }

  protected static Duration parseTimeInterval(String interVal){

    return Duration.ofMillis(parseTimeValToLong(interVal));

  }

  public static TimePeriodConf fromBetween(String start, String end){
    Instant now = ZonedDateTime.now().toInstant();
    Instant iStart;
    Instant iEnd;

    if(start.matches(timeValPattern) || start.equalsIgnoreCase("now")) {
      iStart = timeValToInstant(start, now);
    } else { // expect ISO string
      iStart = ZonedDateTime.parse(start).toInstant();
    }

    if(end.matches(timeValPattern) || end.equalsIgnoreCase("now")){
      iEnd = timeValToInstant(end, now);
    } else {
      iEnd = ZonedDateTime.parse(end).toInstant();
    }

    // if by chance end is lower than start, swap them
    if(iStart.compareTo(iEnd) > 0){
      Instant holder = iStart;
      iStart = iEnd;
      iEnd = holder;
    }

    return new TimePeriodConf(iStart, iEnd);
  }

  public static TimePeriodConf fromSpan(String origin, String span){
    Instant now = ZonedDateTime.now().toInstant();
    Instant iOrigin;

    if(origin.matches(timeValPattern) || origin.equalsIgnoreCase("now")) {
      iOrigin = timeValToInstant(origin, now);
    } else { // expect ISO string
      iOrigin = ZonedDateTime.parse(origin).toInstant();
    }

    Duration dur = parseTimeInterval(span);

    iOrigin.plus(dur);

    if(dur.isNegative()){
      return new TimePeriodConf(iOrigin.plus(dur), iOrigin);
    }else{
      return new TimePeriodConf(iOrigin, iOrigin.plus(dur));
    }
  }

}
