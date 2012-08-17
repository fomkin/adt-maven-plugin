package com.yelbota.plugins.adt;

import com.yelbota.plugins.adt.utils.CleanStream;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.archiver.zip.ZipUnArchiver;
import org.codehaus.plexus.logging.console.ConsoleLoggerManager;

import java.io.File;
import java.io.IOException;


/**
 * @goal unpack
 * @threadSafe
 */
public class UnpackAdtMojo extends DependencyAdtMojo {

    protected static ConsoleLoggerManager plexusLoggerManager = new ConsoleLoggerManager();

    /**
     * adt-maven-plugin home directory.
     * For example "${user.home}/.adt" allows to keep SDK always unpaked.
     *
     * @parameter expression="${project.build.directory}"
     */
    public File pluginHome;

    protected File sdkDirectory;

    // ${user.home}

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        if (!pluginHome.exists()) {
            if (!pluginHome.mkdirs()) {
                failWith("Can't create adt-maven-plugin home directory: " + pluginHome.getAbsolutePath());
            }
        }

        Artifact artifact = getAirSdkArtifact();
        File artifactFile = artifact.getFile();
        File unpackDir = new File(pluginHome, artifact.getArtifactId() + "-" + artifact.getVersion());
        getLog().debug("unpackDir = " + unpackDir.getAbsolutePath());

        sdkDirectory = unpackDir;
        unpackTo(unpackDir, artifactFile, artifact.getType());
    }

    /**
     * Unpack files to directory.
     * @param unpackDir
     * @param artifactFile
     * @return
     * @throws MojoFailureException
     */
    public void unpackTo(File unpackDir, File artifactFile, String type) throws MojoFailureException {

        if (unpackDir.exists()) {

            if (unpackDir.isDirectory()) {

                getLog().debug("AIR SDK already unpacked");
            }
            else {
                failWith(unpackDir.getAbsolutePath() + ", which must be directory for unpacking, now is file");
            }

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
