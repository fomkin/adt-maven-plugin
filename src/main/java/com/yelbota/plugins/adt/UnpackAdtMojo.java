package com.yelbota.plugins.adt;

import com.yelbota.plugins.adt.utils.CleanStream;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.archiver.zip.ZipUnArchiver;
import org.codehaus.plexus.logging.console.ConsoleLoggerManager;

import java.io.File;
import java.io.IOException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * @goal unpack
 * @threadSafe
 */
public class UnpackAdtMojo extends DependencyAdtMojo {

    protected static ConsoleLoggerManager plexusLoggerManager = new ConsoleLoggerManager();

    protected File sdkDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        Artifact artifact = getAirSdkArtifact();
        File artifactFile = artifact.getFile();
        File unpackDir = new File(outputDirectory, "air_sdk_" + sdkVersion);

        unpackTo(unpackDir, artifactFile, artifact.getType());
        sdkDirectory = unpackDir;
    }

    /**
     * Unpack files to directory.
     * @param unpackDir
     * @param artifactFile
     * @return
     * @throws MojoFailureException
     */
    public void unpackTo(File unpackDir, File artifactFile, String type) throws MojoFailureException {

        if (unpackDir.exists() && unpackDir.isDirectory()) {

            getLog().debug("AIR SDK already unpacked");

        } else {

            getLog().info("Unpacking sdk");

            if (unpackDir.exists()) {
                unpackDir.delete();
            }

            unpackDir.mkdirs();

            if (type.equals(ZIP)) {
                ZipUnArchiver unarchiver = new ZipUnArchiver(artifactFile);

                unarchiver.enableLogging(plexusLoggerManager.getLoggerForComponent(ROLE));
                unarchiver.setDestDirectory(unpackDir);
                unarchiver.extract();

            } else if (type.equals(TBZ2))  {

                try
                {
                    // Java 6 doesn't support symlinks.
                    ProcessBuilder builder = new ProcessBuilder(
                            "tar",
                            "-jxvf",
                            artifactFile.getAbsolutePath(),
                            "-C",
                            unpackDir.getAbsolutePath()
                    );

                    getLog().debug(builder.command().toString());
                    Process p = builder.start();

                    CleanStream cleanError = new CleanStream(p.getErrorStream(), getLog());
                    CleanStream cleanOutput = new CleanStream(p.getInputStream(), getLog());

                    cleanError.start();
                    cleanOutput.start();

                    p.waitFor();
                }
                catch (IOException e)
                {
                    failWith("TBZ2 archives supported only on mac osx");
                }
                catch (InterruptedException e)
                {
                    failWith(e.getMessage());
                }

            } else {
                failWith("SDK archive type is not supported");
            }

        }
    }
}
