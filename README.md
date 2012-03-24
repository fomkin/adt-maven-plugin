Build Adobe AIR applications with your Maven!
================================================

General purpose
---------------

Some time ago, Adobe released AIR for mobile devices. The latest versions (such as 3.2) appear in box suitable for industrial use. Was the question: how to package AIR-application automatically? Flexmojos allows you to build only *.air packages, so I decided to write a plugin that could work with platform dependent AIR SDK and build packages for mobile devices too.

Current status (1.0.0)
----------------------

* Building AIR, APK, IPA packages
* Simple configuration
* No need to install SDK. Plugin downloads it as dependency 
 
Plans
--------------------------------------------

* Bilding native desktop packages (exe, dmg)
* Adobe Native Extensions support
* Linux SDK artifact working over wine (just for fun :)

Quick start
-----------------------------------------------

Add repository with plugin and sdk artifacts to your POM

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

Add adt-maven-plugin into plugins section

    
Foreign resources:

* [Apache Maven](http://maven.apache.org)
* [Flexmojos](http://flexmojos.sonatype.org/)

[1]: http://www.sparrow-framework.org
