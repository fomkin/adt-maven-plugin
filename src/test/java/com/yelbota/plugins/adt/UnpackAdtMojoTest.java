package com.yelbota.plugins.adt;

import org.codehaus.plexus.util.FileUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class UnpackAdtMojoTest {

    private final File wd = new File(".");

    @Test

    /**
     * Tests unpack method
     * @throws Exception if any
     */
    public void unpackToTest() throws Exception {

        File artifactFile = FileUtils.resolveFile(wd, "src/test/resources/unit/testsdk_mac.zip");
        File unpackDir = getUnpackDir("target/unit/sdktest_mac");

        UnpackAdtMojo mojo = new UnpackAdtMojo();
        mojo.unpackTo(unpackDir, artifactFile, UnpackAdtMojo.ZIP);

        Assert.assertTrue(FileUtils.resolveFile(unpackDir, "lib").exists(),
                "lib folder should exists");

        Assert.assertTrue(FileUtils.resolveFile(unpackDir, "lib/android").exists(),
                "lib/android folder should exists");

        Assert.assertTrue(FileUtils.resolveFile(unpackDir, "lib/aot").exists(),
                "lib/aot folder should exists");

        Assert.assertTrue(FileUtils.resolveFile(unpackDir, "lib/nai").exists(),
                "lib/nai folder should exists");

        Assert.assertTrue(FileUtils.resolveFile(unpackDir, "lib/android/bin/adb").exists(),
                "lib/android/bin/adb should exists");
    }

    private File getUnpackDir(String path) throws IOException {
        File f = FileUtils.resolveFile(wd, path);
        if (f.exists()) {
            FileUtils.cleanDirectory(f);
            f.delete();
        }
        return f;
    }
}
