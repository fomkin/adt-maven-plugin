package com.yelbota.plugins.adt.exceptions;

import org.apache.maven.plugin.MojoFailureException;

public class AdtConfigurationException extends MojoFailureException {

    public AdtConfigurationException(String message) {
        super(message);
    }

}
