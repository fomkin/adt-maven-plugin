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

import com.yelbota.plugins.nd.DependencyHelper;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@Mojo(name = "dependency")
public class DependencyAdtMojo extends AbstractAdtMojo {

    /**
     * AIR SDK version.
     */
    @Parameter
    protected String sdkVersion;

    public static final String ZIP = "zip";
    public static final String TBZ2 = "tbz2";

    public static final String OS_CLASSIFIER_WINDOWS = "windows";
    public static final String OS_CLASSIFIER_MAC = "mac";

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getAirSdkArtifact();
    }

    public Artifact getAirSdkArtifact() throws MojoFailureException {

        DependencyHelper dependencyHelper = new DependencyHelper() {

            @Override
            protected String getDefaultArtifactId()  throws MojoFailureException  {
                return "air-sdk";
            }

            @Override
            protected String getDefaultGroupId()  throws MojoFailureException  {
                return "com.adobe.air";
            }

            @Override
            protected String getDefaultVersion() throws MojoFailureException  {
                return sdkVersion;
            }

            @Override
            protected String getDefaultPackaging() throws MojoFailureException {
                return getSDKArtifactPackaging(sdkVersion, getDefaultClassifier());
            }
        };

        Artifact artifact = dependencyHelper.resolve(
                pluginArtifacts,
                repositorySystem,
                localRepository,
                remoteRepositories
        );

        getLog().debug("No plugin dependency defined.");

        if (artifact == null && sdkVersion == null) {
            throw failWith("sdkVersion or plugin dependency must be defined");
        }

        return artifact;
    }

    public String getSDKArtifactPackaging(String version, String classifier) {

        if (getVersionNumber(version) < 3.4) {
            return ZIP;
        }
        else {

            if (classifier.equals(OS_CLASSIFIER_WINDOWS)) {
                return ZIP;
            }
            else if (classifier.equals(OS_CLASSIFIER_MAC)) {
                return TBZ2;
            }
        }

        return null;
    }

    protected Float getVersionNumber(String version) {
        String preparedVersion = version.replaceAll("[^\\d.]", "");
        Pattern pattern = Pattern.compile("^(\\d+\\.\\d+).*");
        Matcher matcher = pattern.matcher(preparedVersion);
        if(!matcher.matches()) {
            getLog().debug("Invalid version string " + preparedVersion);
            return null;
        }
        return Float.valueOf(matcher.group(1));
    }
}
