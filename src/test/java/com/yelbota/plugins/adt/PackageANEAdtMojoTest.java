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
import org.apache.maven.model.Build;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.testing.stubs.ArtifactStub;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.codehaus.plexus.util.FileUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.codehaus.plexus.util.StringUtils;

import java.util.ArrayList;
import java.io.File;

public class PackageANEAdtMojoTest {

	private PackageANEAdtMojo mojo;
	private File workDir;
	private File testDir;
	private Build build;

	@BeforeTest
	public void setUp() throws Exception {

		testDir = new File("target/test-classes/");
		workDir = new File(testDir, "/package-ane-test/");

		if(workDir.exists())
			workDir.delete();

		build = new Build();
		build.setFinalName("artifact-version");
		build.setDirectory(workDir.getAbsolutePath());

		mojo = new PackageANEAdtMojo();
		mojo.project = new MavenProjectStub();
		mojo.project.setBuild(build);
	}

	@Test(expectedExceptions=MojoFailureException.class)
	public void testNoPlatformsDefined() throws Exception {
		File descriptor = FileUtils.resolveFile(testDir, "unit/extension-descriptor.xml");
		File swc = FileUtils.resolveFile(testDir, "unit/stub.swc");
		mojo.extensionDescriptor = descriptor;
		mojo.extensionSwc = swc;
		mojo.platforms = new ArrayList<Platform>();
		mojo.prepareArguments();
	}

	@Test
	public void testArgumentsPreparation() throws Exception {
		File descriptor = FileUtils.resolveFile(testDir, "unit/extension-descriptor.xml");
		File swc = FileUtils.resolveFile(testDir, "unit/stub.swc");
		File platformFolder = FileUtils.resolveFile(new File(build.getDirectory()), "platform-folder/");
		Platform platform1 = new Platform("default", platformFolder);
		platform1.files = new ArrayList<String>();
		platform1.files.add("file1");
		platform1.files.add("file2");
		Platform platform2 = new Platform("Android-ARM", platformFolder);
		mojo.extensionDescriptor = descriptor;
		mojo.extensionSwc = swc;
		mojo.platforms = new ArrayList<Platform>();
		mojo.platforms.add(platform1);
		mojo.platforms.add(platform2);
		mojo.prepareArguments();

		File expectedTarget = new File(workDir, "artifact-version.ane");

		String[] args = new String[]{
			"-package -target ane",
			expectedTarget.getAbsolutePath(),
			descriptor.getAbsolutePath(),
			"-swc " + swc.getAbsolutePath(),
			"-platform default -C " + platformFolder.getAbsolutePath(),
			"file1 file2",
			"-platform Android-ARM -C " + platformFolder.getAbsolutePath(),
			"." // platform with no files -> use "." to mean all folder content
		};

		Assert.assertEquals(mojo.arguments, StringUtils.join(args, " ") );
		Assert.assertTrue(expectedTarget.getParentFile().exists(), "Target folder should exist");
	}

}