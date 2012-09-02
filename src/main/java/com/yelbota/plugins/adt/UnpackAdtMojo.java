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

import com.yelbota.plugins.nd.UnpackHelper;
import com.yelbota.plugins.nd.utils.DefaultUnpackMethods;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ConsoleLoggerManager;

import java.io.File;


/**
 * @goal unpack
 * @threadSafe
 */
public class UnpackAdtMojo extends DependencyAdtMojo {

    /**
     * adt-maven-plugin home directory.
     * For example "${user.home}/.adt" allows to keep SDK always unpaked.
     *
     * @parameter expression="${project.build.directory}"
     */
    public File pluginHome;

    protected File sdkDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        if (!pluginHome.exists()) {
            if (!pluginHome.mkdirs()) {
                failWith("Can't create adt-maven-plugin home directory: " + pluginHome.getAbsolutePath());
            }
        }

        Artifact artifact = getAirSdkArtifact();
        File unpackDir = new File(pluginHome, artifact.getArtifactId() + "-" + artifact.getVersion());
        getLog().debug("unpackDir = " + unpackDir.getAbsolutePath());
        sdkDirectory = unpackDir;

        UnpackHelper unpackHelper = new UnpackHelper() {

            @Override
            protected void logAlreadyUnpacked() {
                getLog().debug("AIR SDK already unpacked");
            }

            @Override
            protected void logUnpacking() {
                getLog().info("Unpacking sdk");
            }
        };

        ConsoleLoggerManager plexusLoggerManager = new ConsoleLoggerManager();
        Logger plexusLogger = plexusLoggerManager.getLoggerForComponent(ROLE);
        DefaultUnpackMethods unpackMethods = new DefaultUnpackMethods(plexusLogger);
        unpackHelper.unpack(unpackDir, artifact, unpackMethods, getLog());
    }
}
