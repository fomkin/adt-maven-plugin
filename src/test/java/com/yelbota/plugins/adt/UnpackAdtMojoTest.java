/**
 * Copyright (C) 2012 https://github.com/yelbota/adt-maven-plugin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
