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

    @Test
    public void getVersionNumberTest() throws Exception {
        DependencyAdtMojo adtMojo = new DependencyAdtMojo();
        Assert.assertEquals(adtMojo.getVersionNumber("3.4"), Float.valueOf("3.4"));
        Assert.assertEquals(adtMojo.getVersionNumber("4.0"), Float.valueOf("4.0"));
        Assert.assertEquals(adtMojo.getVersionNumber("14.0.0.76"), Float.valueOf("14.0"));
    }

}
