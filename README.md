# Virtual Device Influx

This project originated as a POC of leveraging the [`virtual-device`](https://github.com/bonitoo-io/virtual-device) project as a library for a third project, in this case writing data points directly to Influxdb using the latest [Influxdb Java Client Library](https://github.com/influxdata/influxdb-client-java).

While currently using only a limited number of the `virtual-device` features, this generator has been tested with both Influxdb OSS and Influxdb Cloud.  By default, it is configured to work with OSS.    

## Quick Start

In order for this project to compile, the `virtual-device` project needs to be cloned locally as well and installed in the local Maven repository - e.g. in `virtual-device` project root, run `mvn install`.

Once that prerequisite is met...

1. Download and start Influxdb OSS docker.  The default configuration file is set up to write data to OSS.  
   1. `$ docker run --name influxdb -p 8086:8086 influxdb:2.7.1`
   2. In a browser open `http://localhost:8086` and complete on-boarding.  The default configuration is set up for user and org `qa` and a bucket named `TEST_BUCKET`.
   3. Generate a _write_ token for this bucket (or an _All Access_ token) and update the token value in the default config `device/src/main/resources/ossDeviceConfig.yml` with its value.
2. Run the generator using gradle. e.g. in the root directory of this project `$ ./gradlew run`.  This will generate datapoints at one second intervals for about 30 seconds. 
3. Verify the generated data in the Influxdb GUI using its Data Explorer feature. 

## Key configuration files

Essential properties for setting up the device are defined in the file `virtdevInflux.props` in the directory `device/src/main/resources`.  The following properties are recognized. 

   * `default.ttl` - a default TTL to be used when generating live data.  This can be overridden in the file specified by `device.conf`
   * `device.conf` - a required property that indicates the name of the YAML config file that configures the device to be run.  Note that this follows the device config structure used in the `virtual-device` project.  One caveat is that, for now, in this project, it is not possible to forward declare items and samples.  These must all be defined within the device definition. 
   * `measurement.name` - when serializing String data to `line protocol` format, which is the approach currently chosen for this project, it is necessary to have a String value representing the measurement part of the `line protocol` string.  Each sample created by the device maps to a single `line protocol` data point.  Values for this property can be:
      * `name` - use the sample name property for the measurement. 
      * `id` - use the sample id property for the measurement. 
      * `topic` - since the original `virtual-device` project was designed to generate MQTT payloads, this value indicates that the last token of the MQTT `topic` field of the sample will be used for the measurement. 
      * Any string - which should match the name of a String Item.  The first String value of that item will be used for the `line protocol` measurement field.
   * `generate.past` - a boolean.  If false the device will generate live data.  If true the device will attempt to generate data for a time period defined using at least two of the next three properties. 
   * `time.period.start` - Instant at which the device should start generating data. A time period string.  e.g. "-3h" - start three hours in the past from now; "-1d" - start from yesterday at the current time. ISO zulu time is also supported.  Relavent units:
      * `ms` - milliseconds
      * `s` - seconds
      * `m` - minutes
      * `h` - hours
      * `d` - days
   * `time.period.end` - Instant at which the device should stop generating data.  Values follow the same formats of `time.period.start`
   * `time.perid.span` - Instead of declaring a fixed in a span of time can also be declared.  Accepted values follow the same syntax of other `time.period` properties, except that the ISO format is not supported.   

Note that all of these properties can be declared as environment variables using the prefix `VIRDEV_INFLUX_` and then the desired property in capital letters with the periods replaced by underscores.  e.g. `generate.past` becomes `VIRDEV_INFLUX_GENERATE_PAST`.
   
A YAML config that defines the device, its samples and their items, will need to be provided.  To better understand its syntax see the documentation for the `virtual-device` project.  Note that for now in this project all samples and items must be defined within the device declaration block.  Forward declaration and then reuse of samples and items is not yet supported.    
