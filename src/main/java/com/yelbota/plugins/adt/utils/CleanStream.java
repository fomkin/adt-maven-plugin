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
package com.yelbota.plugins.adt.utils;

import org.apache.maven.plugin.logging.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CleanStream extends Thread {

    public enum CleanStreamType {
        INFO, DEBUG, ERROR     
    }
    
    private InputStream is;
    private CleanStreamType type = null;
    private Log log = null;

    public CleanStream(InputStream is)
    {
        this.is = is;
    }

    public CleanStream(InputStream is, Log log)
    {
        this.is = is;
        this.log = log;
    }

    public CleanStream(InputStream is, Log log, CleanStreamType type)
    {
        this.is = is;
        this.type = type;
        this.log = log;
    }

    public void run()
    {
        try
        {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;

            while ((line = br.readLine()) != null)
            {
                if (log != null)
                {
                    if (type == null) {

                        log.info(line);

                    }
                    else {
                        if (type == CleanStreamType.INFO) {
                            log.info(line);
                        } else if (type == CleanStreamType.DEBUG) {
                            log.debug(line);
                        } else if (type == CleanStreamType.ERROR) {
                            log.error(line);
                        }
                    }
                }
            }
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }
}