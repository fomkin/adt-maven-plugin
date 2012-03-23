package com.yelbota.plugins.adt;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @goal dependency
 * @phase package
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

            return sdkArtifact;

        } else {

            getLog().info("No plugin dependency defined.");

            if (sdkVersion == null) {
                String message = "sdkVersion or plugin dependency must be defined";
                throw new MojoFailureException(this, message, message);
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
                    throw new MojoFailureException(this, message, message);
                }
            }
            else getLog().warn("Can't resolve air_sdk dependency. repositorySystem unavailable");

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
            String message = fullName + " is not supported";
            throw new MojoFailureException(this, message, message);
        }
    }

}
