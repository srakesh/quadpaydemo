# QuadPay Search Demo

The QuadPay Search Demo is a Java 8.0 maven web application that implements an Ecommerce REST API.

## Online Documentation

https://docs.google.com/document/d/1s57eOsTW-G1dy371p-48VnqpjfAHefW8zR1PK3uUogc/edit

## Building the project

`mvn clean install`

The above generates a target/quadpaydemo.war. All source java files are under src/main/java/quadpaydemo folder.

## How to run

- Stop jetty (kill -9 on pid of "java -jar ./start.jar")
- Copy quadpaydemo.war to a Jetty 9.4.x install under the webapps/ folder
- Start jetty using "java -jar ./start.jar"

## Development/IDEs

- *Eclipse* - Version: 2019-03 (4.11.0). Build id: 20190314-1200

## Dependencies

- Java 8
- Jetty 9.4.x

## Running Tests

No automated tests provided for the demo

