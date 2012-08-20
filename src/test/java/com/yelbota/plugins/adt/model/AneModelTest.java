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
