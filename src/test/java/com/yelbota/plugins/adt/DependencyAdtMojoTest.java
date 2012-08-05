package com.yelbota.plugins.adt;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.testing.stubs.ArtifactStub;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class DependencyAdtMojoTest {

    @Test

    /**
     * Tests that adt dependency selected correctly from plugin dependencies.
     * @throws Exception if any
     */
    public void getAirSdkArtifactTest1() throws Exception {

        List<Artifact> artifactList = new ArrayList<Artifact>();
        ArtifactStub stub = new ArtifactStub();
        stub.setGroupId("com.adobe.air");
        stub.setArtifactId("air-sdk");
        artifactList.add(stub);

        DependencyAdtMojo adtMojo = new DependencyAdtMojo();
        adtMojo.pluginArtifacts = artifactList;
        Assert.assertNotNull(adtMojo.getAirSdkArtifact());
    }

    @Test

    /**
     * Tests that mojo fails when no dependency and airVersion defined
     * @throws Exception if any
     */
    public void getAirSdkArtifactTest2() throws Exception {

        List<Artifact> artifactList = new ArrayList<Artifact>();
        ArtifactStub stub = new ArtifactStub();
        stub.setArtifactId("");
        stub.setGroupId("");
        artifactList.add(stub);

        DependencyAdtMojo adtMojo = new DependencyAdtMojo();
        adtMojo.pluginArtifacts = artifactList;

        try {
            adtMojo.getAirSdkArtifact();
            Assert.fail("no dependency or sdk-version defined");
        } catch (MojoFailureException e) {
            // Ok. This is correct behaviour
        }
    }

    @Test
    public void getSDKArtifactPackagingTest() throws Exception {

        DependencyAdtMojo adtMojo = new DependencyAdtMojo();

        String p = adtMojo.getSDKArtifactPackaging("3.1", DependencyAdtMojo.OS_CLASSIFIER_WINDOWS);
        Assert.assertEquals(p, DependencyAdtMojo.ZIP);

        p = adtMojo.getSDKArtifactPackaging("3.1", DependencyAdtMojo.OS_CLASSIFIER_MAC);
        Assert.assertEquals(p, DependencyAdtMojo.ZIP);

        p = adtMojo.getSDKArtifactPackaging("3.2-RC1", DependencyAdtMojo.OS_CLASSIFIER_MAC);
        Assert.assertEquals(p, DependencyAdtMojo.ZIP);

        p = adtMojo.getSDKArtifactPackaging("3.4", DependencyAdtMojo.OS_CLASSIFIER_WINDOWS);
        Assert.assertEquals(p, DependencyAdtMojo.ZIP);

        p = adtMojo.getSDKArtifactPackaging("3.4", DependencyAdtMojo.OS_CLASSIFIER_MAC);
        Assert.assertEquals(p, DependencyAdtMojo.TBZ2);

        p = adtMojo.getSDKArtifactPackaging("3.4-beta-1", DependencyAdtMojo.OS_CLASSIFIER_MAC);
        Assert.assertEquals(p, DependencyAdtMojo.TBZ2);
    }

}
