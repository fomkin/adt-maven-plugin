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

import com.yelbota.plugins.adt.exceptions.AdtConfigurationException;
import com.yelbota.plugins.adt.model.AneModel;
import com.yelbota.plugins.adt.model.ApplicationDescriptorModel;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Build;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Mojo(name="package", defaultPhase = LifecyclePhase.PACKAGE)
public class PackageAdtMojo extends CommandAdtMojo {

    /**
     * ADT package target. available:
     *
     *   Desktop AIR runtime:
     *     air,
     *     airi
     *
     *   Android:
     *     apk,
     *     apk-debug,
     *     apk-captive-runtime
     *
     *   IOS:
     *     ipa-ad-hoc,
     *     ipa-app-store,
     *     ipa-debug,
     *     ipa-test,
     *     ipa-debug-interpreter,
     *     ipa-test-interpreter
     *
     *   Native desktop:
     *     native
     *     bundle
     *
     * For more information visit Adobe website
     * http://help.adobe.com/en_US/air/build/WS901d38e593cd1bac1e63e3d128cdca935b-8000.html
     */
    @Parameter
    public String target;

    /**
     * http://help.adobe.com/en_US/air/build/WS5b3ccc516d4fbf351e63e3d118666ade46-7f72.html
     */
    @Parameter(defaultValue = "pkcs12")
    public String storetype;

    /**
     * Certificate file
     */
    @Parameter(property = "build.adt.keystore")
    public File keystore;

    /**
     * Password for certificate file.
     */
    @Parameter(property = "build.adt.storepass")
    public String storepass;

    @Parameter(property = "none")
    public String tsa;


    /**
     * Required for iOS packages (ipa*).
     * Example: /Users/yelbota/myapp.mobileprovision
     */
    @Parameter(property = "build.adt.mobileprovision")
    public File provisioningProfile;

    /**
     * Output filename without extension.
     */
    @Parameter
    public String fileNamePattern;

    /**
     * AIR Application descriptor. For example src/main/flex/MyProject-app.xml
     */
    @Parameter(defaultValue = "src/main/resources/application-descriptor.xml")
    public File descriptor;

    /**
     * File to include in application descriptor
     */
    @Parameter
    public File applicationContent;

    /**
     * Root directory for includes.
     */
    @Parameter
    public File includesRoot;

    /**
     * List of files and directories to include into the package
     * relative to includesRoot directory
     */
    @Parameter
    public List<String> includes;

    /**
     * A string value (such as "v1", "2.5", or "Alpha 1") that represents the version of the application, as it should
     * be shown to users.
     */
    @Parameter(defaultValue = "project.version")
    public String versionLabel;

    /**
     * A string value of the format <0-999>.<0-999>.<0-999> that represents application version which can be used
     * to check for application upgrade.Values can also be 1-part or 2-part. It is not necessary to have a 3-part value.
     * An updated version of application must have a versionNumber value higher than the previous version.
     */
    @Parameter(defaultValue = "0.0.0")
    public String versionNumber;

    /**
    * A boolean value
    *
    * If true, the "-sampler" option is added to the "adt -package" command (iOS only, AIR 3.4)
    */
    @Parameter(defaultValue = "false")
    public boolean sampler;

    /**
    * A boolean value
    *
    * If true, the "-hideAneLibSymbols yes" option is added to the "adt -package" command (iOS only, AIR 3.4)
    */
    @Parameter(defaultValue = "false")
    public boolean hideAneLibSymbols;

    /**
    * A boolean value
    *
    * If false, the "-useLegacyAOT no" option is added to the "adt -package" command (iOS only, AIR 4.0)
    */
    @Parameter(defaultValue = "true")
    public boolean useLegacyAOT;


    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        Build build = project.getBuild();

        if (fileNamePattern == null) {
            fileNamePattern = project.getArtifactId() + "-" + project.getVersion();
        }

        if (applicationContent == null) {
            applicationContent = new File(
                    build.getDirectory(),
                    build.getFinalName() + "." + project.getPackaging()
            );
        }

        if (includesRoot == null) {
            includesRoot = new File(build.getDirectory(), "classes");
        }

        super.execute();
    }

    @Override
    protected void prepareArguments() throws MojoFailureException
    {
        super.prepareArguments();

        validateConfiguration();

        File aneDir = prepareAneDir();
        File finalAppDescriptor = getFinalApplicationDescriptor(getExtensionsIds(aneDir));
        List<String> args = new ArrayList<String>();

        args.addAll(getPackageArguments());

        if (!isNativeTarget())
            args.addAll(getCertificateArguments());

        args.addAll(getMainArgs(finalAppDescriptor));
        args.addAll(getIncludesArgs());

        if (aneDir.list().length > 0)
            args.addAll(getExtDirArguments(aneDir));

        String[] argsArray = args.toArray(new String[]{});
        getLog().info("Building package " + getFinalName());

        arguments = StringUtils.join(argsArray, " ");
    }

    private List<String> getExtensionsIds(File aneDir) throws MojoFailureException {

        List<String> result = new ArrayList<String>();

        for (String name: aneDir.list()) {
            AneModel model = new AneModel(new File(aneDir, name));
            result.add(model.getId());
        }

        return result;
    }

    private File getFinalApplicationDescriptor(List<String> extensionsIds) throws MojoFailureException {

        ApplicationDescriptorModel configurator = new ApplicationDescriptorModel(descriptor);
        File mutatedDescriptor = FileUtils.resolveFile(outputDirectory, "application-descriptor.xml");

        configurator.setContent(applicationContent.getName());
        configurator.setVersionLabel(versionLabel);
        configurator.setVersionNumber(versionNumber);
        configurator.setExtensionIds(extensionsIds);
        configurator.printToFile(mutatedDescriptor);

        return mutatedDescriptor;
    }

    public File prepareAneDir() throws MojoFailureException {

        try {

            File dir = new File(outputDirectory, "extDir");

            if (dir.exists())
                FileUtils.cleanDirectory(dir);
            else dir.mkdir();

            for (Artifact artifact : project.getDependencyArtifacts()) {
                if (artifact.getType().equals("ane")) {
                    FileUtils.copyFileToDirectory(artifact.getFile(), dir);
                }
            }

            return dir;

        } catch (IOException e) {

            throw failWith(String.format("Can't create ANE directory (%s)", e.getMessage()));
        }

    }

    public List<String> getPackageArguments() {

        List<String> args = new ArrayList<String>();

        if (isUnsignedAirTarget()) {

            args.add("-prepare");
        }
        else {

            args.add("-package");

            if (isNativeTarget())
                args.addAll(getCertificateArguments());

            if (!isAirTarget()) {
                args.add("-target");
                args.add(target);
            }
        }

        Boolean allowIOSFlags = getVersionNumber(sdkVersion) >= 3.4 && isiOSTarget();

        if(sampler && allowIOSFlags) {
            args.add("-sampler");
        }

        if(hideAneLibSymbols && allowIOSFlags) {
            args.add("-hideAneLibSymbols yes");
        }

        allowIOSFlags = getVersionNumber(sdkVersion) >= 4.0 && isiOSTarget();

        if(!useLegacyAOT && allowIOSFlags) {
            args.add("-useLegacyAOT no");
        }

        return args;
    }

    public List<String> getCertificateArguments() {

        List<String> args = new ArrayList<String>();

        if (!isUnsignedAirTarget()) {

            if (getProvisioningProfileRequired()) {
                args.add("-provisioning-profile");
                args.add(provisioningProfile.getAbsolutePath());
            }

            args.add("-storetype");
            args.add(storetype);

            args.add("-keystore");
            args.add(keystore.getAbsolutePath());

            args.add("-storepass");
            args.add(storepass);

            args.add("-tsa");
            args.add(tsa);
        }

        return args;
    }

    public List<String> getExtDirArguments(File extDir) {
        List<String> result = new ArrayList<String>();
        result.add("-extdir");
        result.add(extDir.getAbsolutePath());
        return result;
    }

    /**
     * @param finalAppDescriptorFile mutated application descriptor
     * @return list of args
     */
    public List<String> getMainArgs(File finalAppDescriptorFile) throws MojoFailureException
    {

        List<String> args = new ArrayList<String>();
        File outputFile = new File(outputDirectory, getFinalName());

        args.add(outputFile.getAbsolutePath());
        args.add(finalAppDescriptorFile.getAbsolutePath());

        return args;
    }

    private String getFinalName() throws MojoFailureException
    {
        String ext;

        if (isiOSTarget()) {
            ext = "ipa";
        } else if (isAndroidTarget()) {
            ext = "apk";
        } else if (isUnsignedAirTarget()) {
            ext = "airi";
        } else if (isNativeTarget()) {
            ext = getNativeDesktopFileExt(isBundleTarget());
        } else {
            ext = "air";
        }

        return fileNamePattern + "." + ext;
    }

    public List<String> getIncludesArgs() throws MojoFailureException {

        List<String> args = new ArrayList<String>();

        // Get SWF file to add in includes
        if (!applicationContent.exists()) {
            throw failWith("Application content (" + applicationContent.getPath() + ") not found");
        } else {

            addIncludeArg(args, applicationContent.getParentFile(), applicationContent.getName());

            if (includes == null)
                return args;

            for (String name : includes) {

                File include = new File(includesRoot, name);

                if (include.exists()) {
                    addIncludeArg(args, includesRoot, name);
                } else {

                    File includeWithRoot = include;
                    include = new File(name);

                    if (include.exists()) {
                        addIncludeArg(args, include.getParentFile(), include.getName());
                    } else {
                        String path = include.getPath() + " (or " + includeWithRoot.getPath() + ")";
                        throw failWith("Include " + path + " doesn't exists");
                    }
                }
            }
        }

        return args;
    }

    public void validateConfiguration() throws AdtConfigurationException {

        if (target == null)
            throw new AdtConfigurationException("target (for example: air, apk, ipa-debug) must be specified");

        validateCertificate();
    }

    public void validateCertificate() throws AdtConfigurationException {

        if (getCertificateRequired()) {
            // Certificate required
            if (storetype == null || storepass == null || keystore == null || tsa == null)
                throw new AdtConfigurationException("Signing options (storetype, keystore, storepass, tsa) must be defined");
            else if (!keystore.exists())
                throw new AdtConfigurationException("Keystore file doesn't exists");
        }

        if (getProvisioningProfileRequired()) {
            // Check mobileprovision
            if (provisioningProfile == null) {
                throw new AdtConfigurationException("provisioningProfile must be defined for ipa* targets");
            } else if (!provisioningProfile.exists()) {
                throw new AdtConfigurationException("provisioningProfile not found");
            }
        }
    }

    private void addIncludeArg(List<String> args, File dir, String name) {
        args.add("-C");
        args.add(dir.getAbsolutePath());
        args.add(name);
    }

    private boolean getCertificateRequired() {
        return !isUnsignedAirTarget();
    }

    private boolean getProvisioningProfileRequired() {
        return isiOSTarget();
    }

    private boolean isNativeTarget() {
        return target.equals("native") || target.equals("bundle");
    }

    private boolean isBundleTarget() {
        return target.equals("bundle");
    }

    private boolean isUnsignedAirTarget() {
        return target.indexOf("airi") > -1;
    }

    private boolean isAirTarget() {
        return target.indexOf("airi") < 0 && target.indexOf("air") > -1;
    }

    private boolean isiOSTarget() {
        return target.indexOf("ipa") > -1;
    }

    private boolean isAndroidTarget() {
        return target.indexOf("apk") > -1;
    }

    private String getNativeDesktopFileExt(Boolean bundle) throws MojoFailureException
    {
        String fullName = System.getProperty("os.name");
        String osName = fullName.toLowerCase();

        if (osName.indexOf("win") > -1)
            return "exe";
        else if (osName.indexOf("mac") > -1)
            return bundle ? "app" : "dmg";
        else {
            throw failWith(fullName + " is not supported");
        }
    }
}
