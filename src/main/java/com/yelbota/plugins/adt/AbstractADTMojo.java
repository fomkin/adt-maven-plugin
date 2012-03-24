package com.yelbota.plugins.adt;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
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
