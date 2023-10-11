#!/usr/bin/env bash

./gradlew device:encryptJar --no-daemon

java -jar device/build/libs/encryptTool.jar
