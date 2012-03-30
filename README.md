Build Adobe AIR applications with your Maven!
================================================

General purpose
---------------

Some time ago, Adobe released AIR for mobile devices. The latest versions (such as 3.2) become suitable for industrial use. Was the question: how to package AIR-application automatically? Flexmojos allows you to build only \*.air packages, so I have created the plugin which could work with platform dependent AIR SDK and additionally build packages for mobile devices.

Current status (1.0.1)
----------------------

* Building AIR, APK, IPA packages
* Adobe Native Extensions (ANE) support
* Simple configuration
* No need installing SDK. Plugin downloads it as dependency 
 
Plans
--------------------------------------------

=======
* Building native desktop packages (exe, dmg)
* Install to device mojo
* Linux SDK artifact working over wine (just for fun :)

Quick start
-----------------------------------------------

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
        <version>1.0.1</version>
        
        <executions>
          <execution>
            <goals>
                <goal>package</goal>
            </goals>
          </execution>
        </executions>
        
        <configuration>
        
            <sdkVersion>3.2-RC1</sdkVersion>
            <target>ipa-debug</target>
            <keystore>certificate.p12</keystore>
            <storepass>******</storepass>
            
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
            
        </configuration>
    </plugin>

You can configure signing with `adt.build.keystore`, `adt.build.storepass` and `adt.buid.mobileprovision` properties. 

    mvn package -Dadt.build.keystore=certificate.p12 -Dadt.build.storepass=******

If you want to use your own SDK package, place it into plugin dependencies. Be aware, that AIR SDK is platform dependent.

    <plugin>
        <groupId>com.yelbota.plugins</groupId>
        <artifactId>adt-maven-plugin</artifactId>
        <version>1.0.0</version>
        <dependencies>
            <dependency>
                <groupId>com.adobe.air</groupId>
                <artifactId>air-sdk</artifactId>
                <version>3.1</version>
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

Note that Flexmojos doesn't support ANE dependencies at this moment (30.03.2012), so you can deploy your ANE with `ane` and `swc` packagings, and add they to dependencies both. Another way: use my experimental Flexmojos fork version `4.3-beta-y1` (available in my repo). 

Foreign resources:

* [Apache Maven](http://maven.apache.org)
* [Flexmojos](http://flexmojos.sonatype.org/)
* [Building Adobe AIR Applications](http://help.adobe.com/en_US/air/build/air_buildingapps.pdf)

[1]: http://www.sparrow-framework.org
