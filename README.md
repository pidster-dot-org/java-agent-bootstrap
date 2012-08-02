# Generic Java Agent

This project provides a generic [Java Agent](http://docs.oracle.com/javase/6/docs/api/java/lang/instrument/package-summary.html "Oracle JavaDocs for the Instrumentation package").

The [Attach API](http://docs.oracle.com/javase/6/docs/jdk/api/attach/spec/com/sun/tools/attach/package-summary.html) is used to connect to the running JVM process and launch the agent.

The agent will start a configurable, specific thread instance inside the running JVM.  This is useful for attaching monitoring components to an existing Java process.

The parameters passed to the agent include:

- 'libs', specifies the directory containing JAR files
- 'thread', specifies the thread class name to be injected and started.


## Build Instructions

The build uses [Gradle](http://gradle.org/) which means you don't have to do much, just run:

    ./gradlew clean build

## HowTo

A script is available after a build as an example of how to use the application.

    java-agent-bootstrap-0.1/inject <pid> -javaagent=java-agent-bootstrap-0.1.jar=libs=/full/path/to/libs,thread=org.example.MyThread

