package com.yelbota.plugins.adt;

import com.yelbota.plugins.adt.exceptions.AdtConfigurationException;
import junit.framework.Assert;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.testing.stubs.ArtifactStub;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.codehaus.plexus.util.FileUtils;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PackageAdtMojoTest {

    private final File wd = new File(".");

    @Test
    public void validateCertificateTest() {

        PackageAdtMojo mojo = new PackageAdtMojo();
        // Certificate required
        mojo.target = "air";
        commonRequiredCertificateTest(mojo);
        mojo.target = "apk";
        commonRequiredCertificateTest(mojo);
        mojo.target = "ipa";
        mojo.provisioningProfile = FileUtils.resolveFile(wd, "src/test/resources/unit/application.mobileprovision");
        commonRequiredCertificateTest(mojo);

        mojo.target = "airi";
        mojo.storetype = null;
        mojo.keystore = null;
        mojo.storepass = null;

        try {
            mojo.validateCertificate();
        } catch (AdtConfigurationException e) {
            Assert.fail();
        }
    }

    @Test
    public void prepareAneDirTest() throws MojoFailureException {

        Set<Artifact> artifactList = new HashSet<Artifact>();

        ArtifactStub stub = new ArtifactStub();
        stub.setGroupId("com.example");
        stub.setArtifactId("myExt1");
        stub.setVersion("1.0");
        stub.setType("ane");
        stub.setFile(FileUtils.resolveFile(wd, "src/test/resources/unit/myExt1-1.0.ane"));
        artifactList.add(stub);

        stub = new ArtifactStub();
        stub.setGroupId("com.example");
        stub.setArtifactId("myLib");
        stub.setVersion("1.0");
        stub.setType("swc");
        artifactList.add(stub);

        stub = new ArtifactStub();
        stub.setGroupId("com.example");
        stub.setArtifactId("myExt2");
        stub.setVersion("1.1");
        stub.setType("ane");
        stub.setFile(FileUtils.resolveFile(wd, "src/test/resources/unit/myExt2-1.1.ane"));
        artifactList.add(stub);

        PackageAdtMojo mojo = new PackageAdtMojo();
        mojo.outputDirectory = FileUtils.resolveFile(wd, "target/unit/extDirTest");
        mojo.outputDirectory.mkdirs();
        mojo.project = new MavenProjectStub();
        mojo.project.setDependencyArtifacts(artifactList);

        File dir = mojo.prepareAneDir();

        String[] dirList = dir.list();
        String[] pattern = new String[]{"myExt1-1.0.ane", "myExt2-1.1.ane"};

        Assert.assertTrue(dir.exists());
        Assert.assertEquals(dirList.length, pattern.length);

        for (int i = 0; i < pattern.length; i++) {
            Assert.assertEquals(dirList[i], pattern[i]);
        }
    }

    private void commonRequiredCertificateTest(PackageAdtMojo mojo) {
        mojo.storetype = "pkcs12";
        mojo.keystore = FileUtils.resolveFile(wd, "src/test/resources/unit/certificate.p12");
        mojo.storepass = "password";

        try {
            mojo.validateCertificate();
        } catch (AdtConfigurationException e) {
            Assert.fail();
        }

        mojo.storepass = null;

        try {
            mojo.validateCertificate();
            Assert.fail();
        } catch (AdtConfigurationException e) {
            // That's correct behaviour
        }

        mojo.keystore = null;
        mojo.storepass = "password";

        try {
            mojo.validateCertificate();
            Assert.fail();
        } catch (AdtConfigurationException e) {
            // That's correct behaviour
        }

        mojo.keystore = FileUtils.resolveFile(wd, "src/test/resources/unit/unknown.p12");

        try {
            mojo.validateCertificate();
            Assert.fail();
        } catch (AdtConfigurationException e) {
            // That's correct behaviour
        }

        mojo.keystore = FileUtils.resolveFile(wd, "src/test/resources/unit/certificate.p12");
        mojo.storetype = null;

        try {
            mojo.validateCertificate();
            Assert.fail();
        } catch (AdtConfigurationException e) {
            // That's correct behaviour
        }
    }

    // TODO test target null
    // TODO test includes null

/*
    @Test
    public void validateConfigurationTestApk() {

        PackageAdtMojo mojo = new PackageAdtMojo();

        mojo.target = "apk";
    }

    @Test
    public void validateConfigurationTestIpa() {

        PackageAdtMojo mojo = new PackageAdtMojo();

        mojo.target = "airi";
    }
*/

}
