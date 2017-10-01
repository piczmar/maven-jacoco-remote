# Introduction

This document explains steps to follow to setup Jenkins build with Jacoco code coverage report from tests against the app running on remote Tomcat server.

As an example app I am using here a Spring Boot REST service built with Maven and deployed as a WAR, which you can find in [`app`](app) folder

The pom.xml contains separate profile `staging` which runs JUnit tests using Surefire plugin and gathers Jacoco coverage data using Jacoco plugin.
Here is the fragment:

```
	<profile>
			<id>staging</id>
			<properties>
				<build.profile.id>staging</build.profile.id>
				<skip.integration.tests>true</skip.integration.tests>
				<skip.unit.tests>false</skip.unit.tests>
				<jacoco.skip>false</jacoco.skip>
			</properties>
			<build>
				<plugins>
					<plugin>
						<groupId>org.apache.maven.plugins</groupId>
						<artifactId>maven-surefire-plugin</artifactId>
						<configuration>
							<includes>
								<include>**/*FT.java</include>
							</includes>
							<excludes>
								<exclude>**/Tests*.java</exclude>
							</excludes>
						</configuration>
					</plugin>
					<plugin>
						<groupId>org.jacoco</groupId>
						<artifactId>jacoco-maven-plugin</artifactId>
						<version>0.7.8</version>
						<configuration>
							<address>172.17.0.1</address>
							<destFile>${project.build.directory}/jacoco.exec</destFile>
							<port>10001</port>
							<reset>true</reset>
							<append>true</append>
						</configuration>
						<executions>
							<execution>
								<phase>post-integration-test</phase>
								<goals>
									<goal>dump</goal>
								</goals>
							</execution>
						</executions>
					</plugin>
				</plugins>
			</build>
		</profile>
```

Only test classes ending with `FT` postfix are included in `staging` tests.
These tests query remote REST endpoints using Spring `RestTemplate`.
As an example see `com.consulner.springboot.rest.DemoFT` test.

The Jacoco Maven Plugin contains configuration where a few arguments are worth explanation:

- `address` is an IP or domain name of the server where Jacoco agent is running, in our case on Tomcat
- `destFile` is a path where coverage data will be dumped from remote server to machine where Maven build is running
- `port` is a TCP/IP port configured for remote Jacoco agent
- `reset` if true then coverage data is wiped out after each dump
- `append` if true and the data file already exists, coverage data is appended to the existing file, otherwise the file will be replaced.

# Environment setup

The following are the steps required to setup environment:

## 1. Download Jacoco

You need to download Jacoco from [Jacoco site](http://www.eclemma.org/jacoco/) and extract the content of the archive on disk.
It will contain `jacocoagent.jar` in libs folder. 

## 2. Configure Jacoco agent on Tomcat
Update $TOMCAT_HOME/bin/catalina.sh with Jacoco agent params - set env. variable:

```
JAVA_OPTS="${JAVA_OPTS} -javaagent:<path to jacocoagent.jar>=output=tcpserver,address=*,port=10001"
```

In the above line replace `<path to jacocoagent.jar>` with an absolute path to jacocoagent.jar extracted in step 1.
This will start the agent, which will listen on TCP/IP port number 10001. You can use different free port of course.
Make sure your firewall is open and Jenkins server can connect Tomcat server on port 10001.

## 3. Start Tomcat and Jenkins

## 4. Configure Jenkins build

To display coverage report in Jenkins you need to have Jacoco plugin installed. Login as admin and go to `Manage Jenkins > Manage Plugins` menu.

![Manage Jenkins](/images/Selection_125.png)

![Manage Plugins](/images/Selection_127.png)

Search for `jacoco` in available plugins tab.


![Manage Plugins](/images/Selection_128.png)

Click `Install without restart` button.

When the installation is done you should see screen as below.


![After installing](/images/Selection_133.png)

Now you can proceed with build configuration.

On dashboard click `New Item` button and choose `Freestyle project`.


![Adding Freestyle project](/images/Selection_129.png)

Go to Source Code Management section and choose Git, then paste your git project url.


![Configuring git repo](/images/Selection_130.png)

Then go to Build section and configure Maven build

![Configuring Maven build](/images/Selection_131.png)

Choose available Maven version. (If no version is available in dropdown you need to first setup Maven in `Manage Jenkins > Global Tool Configuration`)
Configure Maven targets which should be executed: `clean install` or `clean verify` - the important is to execute phase which is post-integration-test, because this is when Jacoco dump will run as configured in pom.xml.
Here we will also set profile `staging`.

![Configuring Maven version and targets](/images/Selection_132.png)

Add Post-build actions - Record JaCoCo coverage report 

![Post-build actions](/images/Selection_134.png)

You can leave default settings, because in out pom.xml we have configured Jacoco to save dump data in `target/jacoco.exe` in the project folder.

![Record JaCoCo coverage report default settings ](/images/Selection_135.png)



## 5. Start Jenkins build

Jenkins build should end with console logs like:

```
[JaCoCo plugin] Collecting JaCoCo coverage data...
[JaCoCo plugin] **/**.exec;**/classes;**/src/main/java; locations are configured
[JaCoCo plugin] Number of found exec files for pattern **/**.exec: 1
[JaCoCo plugin] Saving matched execfiles:  /home/jenkins/workspace/demo/target/jacoco.exec
[JaCoCo plugin] Saving matched class directories for class-pattern: **/classes: 
[JaCoCo plugin]  - /home/jenkins/workspace/demo/target/RestDemo-0.0.1-SNAPSHOT/WEB-INF/classes 3 files
[JaCoCo plugin]  - /home/jenkins/workspace/demo/target/classes 3 files
[JaCoCo plugin] Saving matched source directories for source-pattern: **/src/main/java: 
[JaCoCo plugin] - /home/jenkins/workspace/demo/src/main/java 3 files
[JaCoCo plugin] Loading inclusions files..
[JaCoCo plugin] inclusions: []
[JaCoCo plugin] exclusions: []
[JaCoCo plugin] Thresholds: JacocoHealthReportThresholds [minClass=0, maxClass=0, minMethod=0, maxMethod=0, minLine=0, maxLine=0, minBranch=0, maxBranch=0, minInstruction=0, maxInstruction=0, minComplexity=0, maxComplexity=0]
[JaCoCo plugin] Publishing the results..
[JaCoCo plugin] Loading packages..
[JaCoCo plugin] Done.
[JaCoCo plugin] Overall coverage: class: 67, method: 43, line: 50, branch: 100, instruction: 41
Finished: SUCCESS
```

Note as it saves Jacoco data dump in project folder `target/jacoco.exec`.
Because we have configured Jacoco Maven Plugin to tell Jacoco agent on Tomcat to restart data generation after dump was downloaded, then every build will contain actual latest coverage from when it was started.

In the build page you should also see coverage report printed.

![Successful build page](/images/Selection_136.png)

And if you navigate to `Coverage Report` link you will see more details.

![Successful build page](/images/Selection_137.png)

## Additional resources:
https://stackoverflow.com/questions/23737268/jacoco-tcpserver-reset-dump
