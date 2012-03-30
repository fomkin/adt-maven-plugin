package com.yelbota.plugins.adt.model;

import junit.framework.Assert;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.FileUtils;
import org.testng.annotations.Test;

import java.io.File;

public class AneModelTest {

    private final File wd = new File(".");

    @Test
    public void test() throws MojoFailureException {

        File file = FileUtils.resolveFile(wd, "src/test/resources/unit/vibration.ane");
        AneModel model = new AneModel(file);

        Assert.assertEquals(model.getId(), "com.adobe.Vibration");
    }

}
