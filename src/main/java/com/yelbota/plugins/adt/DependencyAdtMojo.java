package com.yelbota.plugins.adt;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @goal dependency
 * @threadSafe
 */
public class DependencyAdtMojo extends AbstractAdtMojo {

    /**
     * AIR SDK version.
     * @parameter
     */
    protected String sdkVersion;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getAirSdkArtifact();
    }

    public Artifact getAirSdkArtifact() throws MojoFailureException {

        Artifact sdkArtifact = null;

        if (pluginArtifacts != null) {

            for (Artifact artifact : pluginArtifacts) {
                if (artifact.getGroupId().equals("com.adobe.air") && artifact.getArtifactId().equals("air-sdk")) {
                    sdkArtifact = artifact;
                    break;
                }
            }
        }

        if (sdkArtifact != null) {

            sdkVersion = sdkArtifact.getVersion();
            return sdkArtifact;

        } else {

            getLog().debug("No plugin dependency defined.");

            if (sdkVersion == null) {
                String message = "sdkVersion or plugin dependency must be defined";
                throw failWith(message);
            }

            if (repositorySystem != null) {

                sdkArtifact = repositorySystem.createArtifactWithClassifier(
                        "com.adobe.air",
                        "air-sdk",
                        sdkVersion,
                        "zip",
                        getOSClassifier()
                );

                ArtifactResolutionRequest request = new ArtifactResolutionRequest();

                request.setArtifact(sdkArtifact);
                request.setLocalRepository(localRepository);
                request.setRemoteRepositories(remoteRepositories);

                ArtifactResolutionResult res = repositorySystem.resolve(request);

                if (!res.isSuccess()) {

                    if (getLog().isDebugEnabled()) {
                        for (Exception e : res.getExceptions())
                            getLog().error(e);
                    }

                    String message = "Failed to resolve artifact " + sdkArtifact;
                    throw failWith(message);
                }
            }
            else getLog().error("Can't resolve air_sdk dependency. repositorySystem unavailable");

            return sdkArtifact;
        }

    }

    private String getOSClassifier() throws MojoFailureException {

        String fullName = System.getProperty("os.name");
        String osName = fullName.toLowerCase();

        if (osName.indexOf("win") > -1)
            return "windows";
        else if (osName.indexOf("mac") > -1)
            return "mac";
        else {
            throw failWith(fullName + " is not supported");
        }
    }

}
