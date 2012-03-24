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
        mojo.unpackTo(unpackDir, artifactFile);

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

    @Test
    public void setExecutableTest1() throws Exception {

        File artifactFile = FileUtils.resolveFile(wd, "src/test/resources/unit/testsdk_mac.zip");
        File unpackDir = getUnpackDir("target/unit/sdktest_mac");

        UnpackAdtMojo mojo = new UnpackAdtMojo();
        List<String> files = mojo.unpackTo(unpackDir, artifactFile);
        mojo.setExecutable(unpackDir, files);

        Assert.assertTrue(checkExecutable(unpackDir, "lib/android/bin/adb"),
                "lib/android/bin/adb should be executable");

        Assert.assertTrue(checkExecutable(unpackDir, "lib/aot/bin/ld64/i686-apple-darwin9-ld64"),
                "lib/aot/bin/ld64/i686-apple-darwin9-ld64 be executable");

        Assert.assertFalse(checkExecutable(unpackDir, "lib/aot"),
                "lib/aot cant be executable");
    }

    @Test
    public void setExecutableTest2() throws Exception {

        File artifactFile = FileUtils.resolveFile(wd, "src/test/resources/unit/testsdk_windows.zip");
        File unpackDir = getUnpackDir("target/unit/sdktest_windows");

        UnpackAdtMojo mojo = new UnpackAdtMojo();
        List<String> files = mojo.unpackTo(unpackDir, artifactFile);
        mojo.setExecutable(unpackDir, files);

        Assert.assertTrue(checkExecutable(unpackDir, "lib/android/bin/adb.exe"),
                "lib/android/bin/adb.exe should be executable");

        Assert.assertTrue(checkExecutable(unpackDir, "lib/aot/bin/ld64/i686-apple-darwin9-ld64.exe"),
                "lib/aot/bin/ld64/i686-apple-darwin9-ld64.exe be executable");

        Assert.assertFalse(checkExecutable(unpackDir, "lib/aot"),
                "lib/aot cant be executable");
    }

    private boolean checkExecutable(File parent, String path) {
        // TODO there isn't correct way
        File f = new File(parent, path);
        if (!f.isDirectory())
            return f.canExecute();
        return false;
    }

    private File getUnpackDir(String path) throws IOException {

        File unpackDir = FileUtils.resolveFile(wd, path);

        if (unpackDir.exists())
            FileUtils.cleanDirectory(unpackDir);

        unpackDir.mkdirs();
        return unpackDir;
    }
}
