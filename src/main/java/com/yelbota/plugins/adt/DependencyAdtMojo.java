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

    public static final String ZIP = "zip";
    public static final String TBZ2 = "tbz2";

    public static final String OS_CLASSIFIER_WINDOWS = "windows";
    public static final String OS_CLASSIFIER_MAC = "mac";

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

                String osClassifier = getOSClassifier();

                sdkArtifact = repositorySystem.createArtifactWithClassifier(
                        "com.adobe.air",
                        "air-sdk",
                        sdkVersion,
                        getSDKArtifactPackaging(sdkVersion, osClassifier),
                        osClassifier
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

    public String getSDKArtifactPackaging(String sdkVersion, String osClassifier) throws MojoFailureException
    {
        sdkVersion = sdkVersion.replaceAll("[^\\d.]", "");
        float version = Float.valueOf(sdkVersion);

        if (version < 3.4) {
            return ZIP;
        }
        else {
            if (osClassifier.equals(OS_CLASSIFIER_WINDOWS)) {
                return ZIP;
            }
            else if (osClassifier.equals(OS_CLASSIFIER_MAC)) {
                return TBZ2;
            }
            else {
                throw failWith(osClassifier + " is not supported");
            }
        }
    }

    private String getOSClassifier() throws MojoFailureException {

        String fullName = System.getProperty("os.name");
        String osName = fullName.toLowerCase();

        if (osName.indexOf("win") > -1)
            return OS_CLASSIFIER_WINDOWS;
        else if (osName.indexOf("mac") > -1)
            return OS_CLASSIFIER_MAC;
        else {
            throw failWith(fullName + " is not supported");
        }
    }

}
