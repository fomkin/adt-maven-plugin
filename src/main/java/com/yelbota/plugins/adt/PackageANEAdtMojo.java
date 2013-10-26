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
import com.yelbota.plugins.adt.model.AneModel;
import com.yelbota.plugins.adt.model.ApplicationDescriptorModel;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Build;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.FileHeader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Mojo(name="package-ane", defaultPhase = LifecyclePhase.PACKAGE)
public class PackageANEAdtMojo extends CommandAdtMojo {

    @Parameter(property = "build.adt.extensionDescriptor", required = true)
	public File extensionDescriptor;

    @Parameter(property = "build.adt.extensionSwc", required = true)
	public File extensionSwc;

    @Parameter(required = true)
	public ArrayList<Platform> platforms;

    @Parameter
    public String finalName;

    @Override
    protected void prepareArguments() throws MojoFailureException
    {
        Build build = project.getBuild();

        if(platforms.size() < 1) {
            throw failWith("Expected at least one platform!");
        }

        if(finalName == null)            
            finalName = build.getFinalName() + ".ane";              

        File aneFile = new File(build.getDirectory(), finalName);
        File targetDir = aneFile.getParentFile();
        getLog().debug("ANE file = " + aneFile.getAbsolutePath());
        getLog().debug("Target dir = " + targetDir.getAbsolutePath());
        if(!targetDir.exists()) {
            getLog().debug("Creating target directory");
            targetDir.mkdir();
        }

        ArrayList<String> args = new ArrayList<String>();
        args.add("-package -target ane");
        args.add(aneFile.getAbsolutePath());
        args.add(extensionDescriptor.getAbsolutePath());
        args.add("-swc " + extensionSwc.getAbsolutePath());

        for (Platform platform : platforms) {
            args.add("-platform");
            args.add(platform.name);     

            if(platform.options != null) {
                args.add("-platformoptions");
                args.add(platform.options.getAbsolutePath());
            }

            args.add("-C");
            args.add(platform.directory.getAbsolutePath());            
            args.add(".");

            if(platform.requiresSWFExtraction()) {                
                try {
                    extractLibrarySwf(extensionSwc, platform.directory);
                }
                catch (Exception e) {
                    throw failWith("Unable to extract library", e.getMessage());
                }                                                    
            }
        }

    	arguments = StringUtils.join(args.toArray(new String[]{}), " ");
    }

    private void extractLibrarySwf(File swc, File destinationDir) throws Exception {
        ZipFile zipFile = new ZipFile(extensionSwc.getAbsolutePath());
        List headers = zipFile.getFileHeaders();
        FileHeader header;
        for(int i = 0; i < headers.size(); i++) {
            header = (FileHeader) headers.get(i);            
            if(header.getFileName().equals("library.swf")) {
                getLog().debug("Writing library.swf in " + destinationDir.getAbsolutePath());
                zipFile.extractFile(header, destinationDir.getAbsolutePath());
                return;       
            }
        }
        throw new Exception("Could not find library.swf in " + swc.getAbsolutePath());
    }
}