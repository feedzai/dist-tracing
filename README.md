[![Build Status](https://travis-ci.com/GoncaloGarcia/DistTracing.svg?token=UL4UejHxEkoG4ZY6Wp8v&branch=master)](https://travis-ci.com/GoncaloGarcia/DistTracing) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/64c32ebc68a7406c92ec68292cc5e1ac)](https://www.codacy.com?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=GoncaloGarcia/DistTracing&amp;utm_campaign=Badge_Grade)[![codecov](https://codecov.io/gh/GoncaloGarcia/DistTracing/branch/master/graph/badge.svg?token=zeWoZDlzAU)](https://codecov.io/gh/GoncaloGarcia/DistTracing)



# Description

This project is a simplified API for tracing Java based distributed systems, as well as an implementation based on the OpenTracing instrumentation framework and the Jaeger tracing engine.

In this book you will first find a small explanation of the tracing model, then a high-level description of the APIs and some instruction on when to use each API.

## Installation

This project is avaliable on Maven Central. To use it in your projects add the following to your `pom.xml`

```
<dependency>
    <groupId>com.feedzai.commons.tracing</groupId>
    <artifactId>tracing</artifactId>
    <version>0.1.10</version>
    <type>pom</type>
</dependency>
```

## Usage

For an API description along with usage examples please refer to the [Documentation](site/src/gitbook/DESCRIPTION.md)

## Generating Gitbook documentation

Our docs are also available in Gitbook form, to generate and view them execute the following commands from the project's root.

```
cd site/src/gitbook
gitbook install
gitbook serve
```

The output of `gitbook serve` should provide a link to the generated webpage.



## Build

Run the following command:

```
mvn clean install
```

To build without running tests execute:

```
mvn clean install -DskipTests
```

## Enable Logging

If you're running the API backed by our LoggingTracingEngine you need to add the following to your `logback.xml`

```
 <!-- METRICS SERVER LOGGING TAG
  <appender name="METRICS" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>log/${pulse.logcontext}-metrics.log</file>
    <encoder>
      <pattern>%msg</pattern>
    </encoder>
    <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
      <fileNamePattern>log/${pulse.logcontext}-metrics.%d{yyyy-MM}-week-%d{WW}.log.gz</fileNamePattern>
      <maxHistory>8</maxHistory>
    </rollingPolicy>
  </appender>

  <appender name="METRICS-ASYNC" class="ch.qos.logback.classic.AsyncAppender">
    <appender-ref ref="METRICS" />
    <queueSize>2048</queueSize>
    <discardingThreshold>0</discardingThreshold>
    <includeCallerData>false</includeCallerData>
  </appender>

  <logger name="com.feedzai.commons.tracing.engine.LoggingTracingEngine" level="TRACE" additivity="false">
    <appender-ref ref="METRICS-ASYNC" />
  </logger>
  METRICS SERVER LOGGING TAG -->
  <!-- End of metrics server local logging. -->
  ```


