[![Build Status](https://secure.travis-ci.org/yelbota/adt-maven-plugin.png?branch=master)](http://travis-ci.org/yelbota/adt-maven-plugin)

Build Adobe AIR applications with your Maven!
================================================

General purpose
---------------

Some time ago, Adobe released AIR for mobile devices. There was a question: how to package AIR-application automatically? Flexmojos allows you to build only \*.air packages, so I have created the plugin which could work with platform dependent AIR SDK and additionally build packages for mobile devices. ***This plugin is not replacement of Flexmojos***. You still need Flexmojos to build SWF.

Current status (1.0.7)
----------------------

* Building AIR, APK, IPA packages
* Native desktop packages (DMG on Mac OSX, EXE on Windows)
* Adobe Native Extensions (ANE) support
* Run custom adt command
* Simple configuration
* No need installing SDK. Plugin downloads it as dependency 
 
Plans
--------------------------------------------

* Install to device mojo
* Linux SDK artifact working over wine (just for fun :)

Quick start
-----------------------------------------------

First, make sure that your project has `swf` packaging.

    <packaging>swf</packaging>

Add repository with plugin and SDK artifacts into your POM

    <pluginRepositories>
        <pluginRepository>
            <id>yelbota-dropbox-repo</id>
            <url>http://dl.dropbox.com/u/36020926/maven/</url>
            <snapshots><enabled>false</enabled></snapshots>
            <releases><enabled>true</enabled></releases>
        </pluginRepository>
    </pluginRepositories>

    <repositories>
        <repository>
            <id>yelbota-dropbox-repo</id>
            <url>http://dl.dropbox.com/u/36020926/maven/</url>
            <snapshots><enabled>false</enabled></snapshots>
            <releases><enabled>true</enabled></releases>
        </repository>
    </repositories>

Add `adt-maven-plugin` into plugins section

    <plugin>
    
        <groupId>com.yelbota.plugins</groupId>
        <artifactId>adt-maven-plugin</artifactId>
        <version>1.0.7</version>
        
        <executions>
          <execution>
            <goals>
                <goal>package</goal>
            </goals>
          </execution>
        </executions>
        
        <configuration>
        
            <sdkVersion>3.5</sdkVersion>
            
            <target>ipa-debug</target>
            <keystore>certificate.p12</keystore>
            <storepass>******</storepass>
            <tsa>none</tsa>

            <!-- Required for ipa* targets -->
            <provisioningProfile>myapp.mobileprovision</provisioningProfile>
            
            <!-- 
                 Optional. Application descriptor. By default is 
                 src/main/resources/application-descriptor.xml
            -->
            <descriptor>src/main/flex/Project-app.xml</descriptor>
            
            <!-- 
                 Optional. Replaces versionNumber in application descriptor. Useful
                 for CI. 0.0.0 by default. 
            -->
            <versionNumber>${build.number}</versionNumber>
            
            <!-- 
                 Optional. Replaces versionLabel in application descriptor. 
                 ${project.version} by default.
             -->
            <versionLabel>${project.version}</versionLabel>
            
            <!-- 
                 By default includes lookedup in target/classes directory. Usualy
                 maven-resources-plugin copy here content of src/main/resources.
                 You can change this behaviour by setting <includesRoot> property. 
            -->
            <includes>
                <include>icons</include>
            </includes>
            
            <!-- 
                 Optional. Plugin home directory. For example "${user.home}/.adt" allows to keep SDK always unpacked for many projects.
                 ${project.build.directory} by default.
             -->
            <pluginHome></pluginHome>

            <!-- (iOS only, AIR 3.4 and higher) Enables the telemetry-based ActionScript sampler in iOS 
                 applications. Using this flag lets you profile the application with Adobe Scout. Although
                 Scout can profile any Flash platform content, enabling detailed telemetry gives you 
                 deep insight into ActionScript function timing, DisplayList, Stage3D rendering and more.
                 Note that using this flag will have a slight performance impact, so do not use it for
                 production applications.
            -->
            <sampler>false</sampler>
        </configuration>
    </plugin>

You can configure signing with `build.adt.keystore`, `build.adt.storepass` and `build.adt.mobileprovision` properties.

    mvn package -Dbuild.adt.keystore=certificate.p12 -Dbuild.adt.storepass=******

If you want to use your own SDK package, place it into plugin dependencies. Be aware, that AIR SDK is platform dependent.

    <plugin>
        <groupId>com.yelbota.plugins</groupId>
        <artifactId>adt-maven-plugin</artifactId>
        <version>1.0.7</version>
        <dependencies>
            <dependency>
                <groupId>com.adobe.air</groupId>
                <artifactId>air-sdk</artifactId>
                <version>3.5</version>
                <type>zip</type>
                <classifier>${os.family}</classifier>
            </dependency>
        </dependencies>
        ...
    </plugin>

AIR Native Extensions support
-----------------------------------------------
    
ANE support designed in true maven style. Just deploy your extension to maven repository and add dependency. You don't need to include `<extensions>` section in application descriptor. It will be done automatically.

    <dependency>
        <groupId>com.adobe.extensions</groupId>
        <artifactId>vibration</artifactId>
        <version>1.0</version>
        <type>ane</type>
    </dependency>

Note that Flexmojos 4.x doesn't support ANE dependencies, so you need to use Flexmojos 6.x.

Run custom command
-----------------------------------------------

You can run custom ADT command using `command` goal. 

    <plugin>
        <groupId>com.yelbota.plugins</groupId>
        <artifactId>adt-maven-plugin</artifactId>
        <version>1.0.7</version>
        <configuration>
            <sdkVersion>3.5</sdkVersion>
        </configuration>
        <executions>
            <execution>
                <goals>
                    <goal>command</goal>
                </goals>
                <configuration>
                    <arguments>-certificate -cn cert 1024-RSA ${project.build.directory}/cert.p12 111</arguments>
                </configuration>
            </execution>
        </executions>
    </plugin>

Examples
-----------------------------------------------

[https://github.com/yelbota/adt-maven-plugin/tree/master/src/it](https://github.com/yelbota/adt-maven-plugin/tree/master/src/it)

Foreign resources
-----------------------------------------------

* [Apache Maven](http://maven.apache.org)
* [Flexmojos](http://flexmojos.net)
* [Building Adobe AIR Applications](http://help.adobe.com/en_US/air/build/air_buildingapps.pdf)
