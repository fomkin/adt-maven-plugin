package com.yelbota.plugins.adt;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.archiver.zip.ZipUnArchiver;
import org.codehaus.plexus.logging.console.ConsoleLoggerManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * @goal unpack
 * @threadSafe
 */
public class UnpackAdtMojo extends DependencyAdtMojo {

    protected static ConsoleLoggerManager plexusLoggerManager = new ConsoleLoggerManager();

    protected File sdkDirectory;

    private class CleanStream extends Thread {
        InputStream is;
        String type = null;
        boolean typeSet = false;

        CleanStream(InputStream is)//, String type)
        {
            this.is = is;
            //this.type = type;
        }
        CleanStream(InputStream is, String type)
        {
            this.is = is;
            this.type = type;
            typeSet = true;
        }

        public void run()
        {
            try
            {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line=null;
                while ( (line = br.readLine()) != null)
                {
                    if(typeSet) getLog().debug(type + "> " + line);
                }
            }
            catch (IOException ioe)
            {
                ioe.printStackTrace();
            }
        }
    }

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

                    CleanStream cleanError = new CleanStream(p.getErrorStream(), "ERROR");
                    CleanStream cleanOutput = new CleanStream(p.getInputStream(), "OUTPUT");

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
