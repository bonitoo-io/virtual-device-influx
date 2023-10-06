package io.bonitoo.virtual.device.influx.conf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.bonitoo.qa.conf.VirDevConfigException;
import io.bonitoo.qa.util.EncryptPass;
import io.bonitoo.qa.util.LogHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.util.Properties;

public class Config {

  static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  // TODO get config values from environment

  // TODO config values for IOX

  static final long DEFAULT_TTL = 60 * 1000;

  static final String envConfigFile = "VIRTDEV_INFLUX_CONFIG";


  static long TTL = DEFAULT_TTL;

  static String TOKEN = "DUMMY_TOKEN";

  public static String OSS_TOKEN = "L6iAyjzfsoSHKBOwl1gpHJvk5oc7PA2KgOP0ug-EGmTQm8l75qKSq2AsAyc98EyXXL4HaD4wcTeR46Y_RT9HZA==";

  static Properties props;

  // TODO - should be replaced by runner for multiple devices - later
  static InfluxDeviceConfig deviceConfig;

  static final String ENVAR_PREFIX = "VIRDEV_INFLUX";

  public static final String MEASUREMENT_FIELD_KEY = "measurement.field";

  static final String configFile = System.getenv(envConfigFile) == null ? "virtdevInflux.props" :
    System.getenv(envConfigFile).trim();

  public static final String GENERATE_PAST_KEY = "generate.past";
  public static final String TIME_PERIOD_START_KEY = "time.period.start";
  public static final String TIME_PERIOD_END_KEY = "time.period.end";

  public static final String TIME_PERIOD_SPAN_KEY = "time.period.span";

  static TimePeriodConf timePeriodConf = null;

  // Converts dot notation variable key to dash separated with caps
  protected static String VMVarKeyToEnvVarKey(String vmVar){
    return ENVAR_PREFIX + "_" + vmVar.toUpperCase().replace(".", "_");
  }

  public static void readProps(){
    props = new Properties();
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    logger.info(LogHelper.buildMsg("config", "Reading base config", configFile));
    try (
      InputStream is = (loader.getResourceAsStream(configFile) == null)
        ? Files.newInputStream(new File(configFile).toPath()) :
        loader.getResourceAsStream(configFile)) {
      props.load(is);
    } catch (IOException e) {
      logger.error(LogHelper.buildMsg("config", "Load failure",
        String.format("Unable to load config file %s", configFile)));
      logger.error(LogHelper.buildMsg("config", "Load exception", e.toString()));
      System.exit(1);
    }


//    System.out.println("DEBUG System.getProperties()");
//    for(Object key : System.getProperties().keySet()){
//      System.out.println("     " + key + " : " + System.getProperty(key.toString()));
//
//    }

    // Overwrite properties set in JVM with system values
    for (String key : props.stringPropertyNames()) {
//      System.out.println("DEBUG asdf Checking key " + key);
      if(System.getenv(VMVarKeyToEnvVarKey(key)) != null){
//        System.out.println("DEBUG setting key " + key + " to " + System.getenv(VMVarKeyToEnvVarKey(key)));
        props.setProperty(key, System.getenv(VMVarKeyToEnvVarKey(key)));
      }else if (System.getProperty(key) != null) {
//        System.out.println("DEBUG Setting key " + key + " to " + System.getProperty(key));
        props.setProperty(key, System.getProperty(key));
      }
    }

    TTL = Long.parseLong(props.getProperty("default.ttl", Long.toString(TTL)));

    if(props.containsKey(TIME_PERIOD_START_KEY)){
      // N.B. TIME_PERIOD_END_KEY has precedence over TIME_PERIOD_SPAN_KEY
      if(props.containsKey(TIME_PERIOD_END_KEY)){
        timePeriodConf = TimePeriodConf.fromBetween(
          props.getProperty(TIME_PERIOD_START_KEY),
          props.getProperty(TIME_PERIOD_END_KEY)
        );
      }else if(props.containsKey(TIME_PERIOD_SPAN_KEY)) {
        timePeriodConf = TimePeriodConf.fromSpan(
          props.getProperty(TIME_PERIOD_START_KEY),
          props.getProperty(TIME_PERIOD_SPAN_KEY)
        );
      }else{
        // ignore but warn
        logger.warn(LogHelper.buildMsg("InfluxDevConfig", "Incomplete Time Period",
          String.format("The property %s was defined without any complementary properties. " +
              "It will be ignored.",
            TIME_PERIOD_START_KEY)));
      }
    } else if (props.containsKey(TIME_PERIOD_END_KEY)){
      if(props.containsKey(TIME_PERIOD_SPAN_KEY)){
       timePeriodConf = TimePeriodConf.fromSpan(
         props.getProperty(TIME_PERIOD_END_KEY),
         props.getProperty(TIME_PERIOD_SPAN_KEY)
       );
      } else {
        logger.warn(LogHelper.buildMsg("InfluxDevConfig", "Incomplete Time Period",
          String.format("The property %s was defined without any complementary properties. " +
              "It will be ignored.",
            TIME_PERIOD_END_KEY)));
      }
    } else if (props.containsKey(TIME_PERIOD_SPAN_KEY)){
      logger.warn(LogHelper.buildMsg("InfluxDevConfig", "Incomplete Time Period",
        String.format("The property %s was defined without any complementary properties. " +
            "It will be ignored.",
          TIME_PERIOD_SPAN_KEY)));
    }
  }

  public static Properties getProps(){
    return props;
  }

  static public void setTTL(long ttl){
    TTL = ttl;
  }

  static public long getTTL(){
    return TTL;
  }

  static public void setTOKEN(String token){
    TOKEN = token;
  }

  static public String getTOKEN(){
    return TOKEN;
  }

  public static String getOssToken() {
    return OSS_TOKEN;
  }

  public static boolean generatePast(){
    return Boolean.parseBoolean(props.getProperty(GENERATE_PAST_KEY));
  }

  public static Object getProp(String key){
    return props.get(key);
  }

  public static TimePeriodConf getTimePeriodConf(){
    return timePeriodConf;
  }



  public static String encryptToken(char[] token){
    return EncryptPass.encryptPass(Config.class.getCanonicalName().toCharArray(), token);
  }

  public static char[] decryptToken(char[] encToken){
    if(EncryptPass.passIsEncoded(encToken)){
      return EncryptPass.decryptPass(Config.class.getCanonicalName().toCharArray(), new String(encToken));
    }
    System.out.println("DEBUG warning passed token was not encrypted");
    return encToken;
  }

  // TODO replace later with more general runner config
  //  for now create only one device
  public static void readDeviceConfig(){

    if(props == null){
      readProps();
    }

    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    logger.info(LogHelper.buildMsg("config", "Reading runner config",
      props.getProperty("runner.conf")));

    InputStream deviceConfStream = loader.getResourceAsStream(props.getProperty("device.conf"));

      // todo read virtualInflux.props with possibility of getting virtualInflux.props substitute from ENVARS


      // todo read config from virtualInflux.props OR from ENVARS

    try {

      if (deviceConfStream == null) {
        deviceConfStream = Files.newInputStream(
          new File(props.getProperty("runner.conf")
          ).toPath());
      }

      ObjectMapper om = new ObjectMapper(new YAMLFactory());
      deviceConfig = om.readValue(deviceConfStream, InfluxDeviceConfig.class);
      if (deviceConfig == null) {
        throw new VirDevConfigException(
          String.format("Failed to parse config file %s", props.getProperty("runner.conf"))
        );
      }
      logger.info(LogHelper.buildMsg("config",
        "Parse runner config success",
        props.getProperty("runner.conf")));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static InfluxDeviceConfig getDeviceConfig(){
    return deviceConfig;
  }
}
