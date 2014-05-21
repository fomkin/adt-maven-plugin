/**
 * Copyright (C) 2012 https://github.com/yelbota/adt-maven-plugin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import java.util.Arrays;
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
        mojo.project = new FixMavenProjectStub();
        mojo.project.setArtifacts(artifactList);

        File dir = mojo.prepareAneDir();

        List<String> dirList = Arrays.asList(dir.list());
        String[] pattern = new String[]{"myExt1-1.0.ane", "myExt2-1.1.ane"};

        Assert.assertTrue(dir.exists());

        // Check that count of files in directory equals length of pattern array
        // and all of the pattern elements exists in directory listing.
        Assert.assertEquals(dirList.size(), pattern.length);
        for (int i = 0; i < pattern.length; i++) {
            dirList.contains(pattern[i]);
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

    @Test
    public void testSamplerArgument() {

        PackageAdtMojo mojo = new PackageAdtMojo();
        mojo.target = "ipa-test";

        mojo.sdkVersion = "3.6";
        mojo.sampler = true;
        Assert.assertTrue(mojo.getPackageArguments().contains("-sampler"));

        mojo.sdkVersion = "3.4";
        mojo.sampler = true;
        Assert.assertTrue(mojo.getPackageArguments().contains("-sampler"));

        mojo.sdkVersion = "3.4";
        mojo.sampler = false;
        Assert.assertFalse(mojo.getPackageArguments().contains("-sampler"));

        mojo.sdkVersion = "3.3";
        mojo.sampler = true;
        Assert.assertFalse(mojo.getPackageArguments().contains("-sampler"));

        // Only added for iOS targets
        mojo.target = "ipa-debug";
        mojo.sdkVersion = "3.4";
        mojo.sampler = true;
        Assert.assertTrue(mojo.getPackageArguments().contains("-sampler"));

        mojo.target = "apk-debug";
        mojo.sdkVersion = "3.4";
        mojo.sampler = true;
        Assert.assertFalse(mojo.getPackageArguments().contains("-sampler"));
    }

    @Test
    public void testHideAnyLibSymbolsArgument() {

        PackageAdtMojo mojo = new PackageAdtMojo();
        mojo.target = "ipa-test";

        mojo.sdkVersion = "3.6";
        mojo.hideAneLibSymbols = true;
        Assert.assertTrue(mojo.getPackageArguments().contains("-hideAneLibSymbols yes"));

        mojo.sdkVersion = "3.4";
        mojo.hideAneLibSymbols = true;
        Assert.assertTrue(mojo.getPackageArguments().contains("-hideAneLibSymbols yes"));

        mojo.sdkVersion = "3.4";
        mojo.hideAneLibSymbols = false;
        Assert.assertFalse(mojo.getPackageArguments().contains("-hideAneLibSymbols"));

        mojo.sdkVersion = "3.3";
        mojo.hideAneLibSymbols = true;
        Assert.assertFalse(mojo.getPackageArguments().contains("-hideAneLibSymbols"));

        // Only added for iOS targets
        mojo.target = "ipa-debug";
        mojo.sdkVersion = "3.4";
        mojo.hideAneLibSymbols = true;
        Assert.assertTrue(mojo.getPackageArguments().contains("-hideAneLibSymbols yes"));

        mojo.target = "apk-debug";
        mojo.sdkVersion = "3.4";
        mojo.hideAneLibSymbols = true;
        Assert.assertFalse(mojo.getPackageArguments().contains("-hideAneLibSymbols yes"));
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
class FixMavenProjectStub extends MavenProjectStub {

    private Set artifacts;

    @Override
    public void setArtifacts(Set set) {
        this.artifacts = set;
    }

    @Override
    public Set getArtifacts() {
        return artifacts;
    }
}
