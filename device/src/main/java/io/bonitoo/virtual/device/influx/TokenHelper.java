package io.bonitoo.virtual.device.influx;

import io.bonitoo.qa.conf.mqtt.broker.TlsConfig;
import io.bonitoo.qa.util.EncryptPass;
import io.bonitoo.virtual.device.influx.conf.Config;

import java.io.Console;

public class TokenHelper {

  public static void main(String[] args){

    Console console = System.console();
    String hashedPass;

    if (console != null) {
      char[] token = System.console().readPassword("Enter token: ");
      hashedPass = Config.encryptToken(token);
    } else {
      hashedPass = Config.encryptToken(args[0].toCharArray());
    }

    System.out.println(hashedPass);

  }

}
