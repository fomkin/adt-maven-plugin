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
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.repository.RepositorySystem;

import java.io.File;
import java.util.List;

abstract public class AbstractAdtMojo extends AbstractMojo {

    /**
     * @component
     * @readonly
     */
    protected RepositorySystem repositorySystem;

    /**
     * @parameter expression="${plugin.artifacts}"
     * @readonly
     * @required
     */
    public List<Artifact> pluginArtifacts;

    /**
     * @parameter expression="${localRepository}"
     * @required
     * @readonly
     */

    protected ArtifactRepository localRepository;
    /**
     * @parameter expression="${project.remoteArtifactRepositories}"
     * @required
     * @readonly
     */
    protected List<ArtifactRepository> remoteRepositories;

    /**
     * Location of the file.
     *
     * @parameter expression="${project.build.directory}"
     * @required
     */
    protected File outputDirectory;

    protected MojoFailureException failWith(String msg) {
        return new MojoFailureException(this, msg, msg);
    }

    protected MojoFailureException failWith(String msg, String fullMsg) {
        return new MojoFailureException(this, msg, fullMsg);
    }

}
