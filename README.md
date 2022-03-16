## Spark & Pdf Extent Report generation for REST Assured API Testing executed with Cucumber-JVM

This [artifact](http://ghchirp.tech/4199/) deals with the creation of Spark and Pdf Extent Report for REST Assured validation with Cucumber by using a Maven Plugin. The artifact uses the REST Assured Filter from the Allure Framework for generating the report data. This works with JUnit and TestNG testing frameworks. This avoids the mixing of Extent Report calls within the test code. All that is required in the code is an addition of a REST Assured filter and two Cucumber plugins. The remaining changes are all POM configuration changes.

For more details refer to this [article](http://ghchirp.tech/4199/). Sample usages for [JUnit](https://github.com/grasshopper7/cucumber-rest-assured-junit-report) and [TestNG](https://github.com/grasshopper7/cucumber-rest-assured-testng-report).

**Sample POM** - POM for [JUnit](https://github.com/grasshopper7/cucumber-rest-assured-junit-report/blob/master/pom.xml) and for [TestNG](https://github.com/grasshopper7/cucumber-rest-assured-testng-report/blob/master/pom.xml).

**Maven Failsafe Plugin Configuration** - Plugin configurations for [JUnit](https://github.com/grasshopper7/cucumber-rest-assured-junit-report/blob/de9a4353481eaf1e0f48098cf782f497eb1b9fe5/pom.xml#L86) and [TestNG](https://github.com/grasshopper7/cucumber-rest-assured-testng-report/blob/de9a4353481eaf1e0f48098cf782f497eb1b9fe5/pom.xml#L74).

**Report Plugin** -
```
<plugin>
  <groupId>tech.grasshopper</groupId>
  <artifactId>cucumber-rest-assured-extent-report-plugin</artifactId>
  <version>1.1.1</version>
  <executions>
    <execution>
      <id>report</id>
      <phase>post-integration-test</phase>
      <goals>
        <goal>extentreport</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```
Default configuration details and tips be modify them can be found in the [article](http://ghchirp.tech/4199/).
