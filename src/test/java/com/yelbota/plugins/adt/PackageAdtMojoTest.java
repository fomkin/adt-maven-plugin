package com.yelbota.plugins.adt;

import com.yelbota.plugins.adt.exceptions.AdtConfigurationException;
import junit.framework.Assert;
import org.codehaus.plexus.util.FileUtils;
import org.testng.annotations.Test;

import java.io.File;

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

    private void commonRequiredCertificateTest(PackageAdtMojo mojo)
    {
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
