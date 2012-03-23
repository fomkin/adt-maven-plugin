package com.yelbota.plugins;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Goal which touches a timestamp file.
 *
 * @goal package
 * @phase package
 */
public class ADTMojo extends AbstractMojo {
    /**
     * @parameter default-value="${plugin.artifacts}"
     */
    private List pluginArtifacts;


    /**
     * @component
     */
    private ArtifactFactory artifactFactory;

    /**
     * @component
     */
    private ArtifactResolver resolver;

    /**
     * Location of the file.
     *
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private File outputDirectory;

    public void execute() throws MojoExecutionException {

//artifact = artifactFactory.createArtifactWithClassifier(groupId, artifactId, version, type, classifier);
        File f = outputDirectory;

        if (!f.exists()) {
            f.mkdirs();
        }

        File touch = new File(f, "touch.txt");

        FileWriter w = null;
        try {
            w = new FileWriter(touch);

            w.write("touch.txt");
        } catch (IOException e) {
            throw new MojoExecutionException("Error creating file " + touch, e);
        } finally {
            if (w != null) {
                try {
                    w.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }
}
