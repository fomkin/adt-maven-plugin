package com.yelbota.plugins.adt;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.plugin.testing.stubs.ArtifactStub;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class DependencyAdtMojoTest extends AbstractMojoTestCase {

    @BeforeTest

    protected void setUp() throws Exception {
        super.setUp();
    }

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
        adtMojo.setPluginArtifacts(artifactList);
        assertNotNull(adtMojo.getAirSdkArtifact());
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
        adtMojo.setPluginArtifacts(artifactList);

        try {
            adtMojo.getAirSdkArtifact();
            fail("no dependency or sdk-version defined");
        }
        catch (MojoFailureException e) {
            // Ok. This is correct behaviour
        }
    }

}
