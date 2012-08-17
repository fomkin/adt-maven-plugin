package com.yelbota.plugins.adt;

import com.yelbota.plugins.adt.utils.CleanStream;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @goal command
 * @phase package
 * @threadSafe
 */
public class CommandAdtMojo extends UnpackAdtMojo {

    /**
     * Custom adt arguments
     * @parameter
     */
    protected String arguments;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        super.execute();
        prepareArguments();

        ArrayList<String> finalArgs = new ArrayList<String>();
        File adtFile = FileUtils.resolveFile(sdkDirectory, "lib/adt.jar");

        finalArgs.add("java");
        finalArgs.add("-jar");
        finalArgs.add(adtFile.getAbsolutePath());

        for (String s: StringUtils.split(arguments, " "))
            finalArgs.add(s);

        execute(finalArgs.toArray(new String[]{}));
    }

    protected void prepareArguments() throws MojoFailureException
    {
        // Override this method for arguments modification.
    }

    public void execute(String[] args) throws MojoFailureException
    {
        try {

            getLog().debug(StringUtils.join(args, " "));
            Process process = Runtime.getRuntime().exec(args);

            CleanStream cleanError = new CleanStream(process.getErrorStream(),
                    getLog(), CleanStream.CleanStreamType.ERROR);

            CleanStream cleanOutput = new CleanStream(process.getInputStream(),
                    getLog(), CleanStream.CleanStreamType.INFO);

            cleanError.start();
            cleanOutput.start();

            int code = process.waitFor();

            if (code > 0) {

                // Oops.
                throw failWith(
                        "adt fails with return code #" + code +
                                ". Checkout official documentation here http://help.adobe.com/en_US/air/build/air_buildingapps.pdf"
                );
            }

        } catch (IOException e) {

            throw failWith("Cant execute adt", e.getLocalizedMessage());

        } catch (InterruptedException e) {

            throw failWith("adt fails " + e);
        }
    }
}
