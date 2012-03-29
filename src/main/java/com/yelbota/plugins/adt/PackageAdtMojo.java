package com.yelbota.plugins.adt;

import com.yelbota.plugins.adt.exceptions.AdtConfigurationException;
import com.yelbota.plugins.adt.utils.ApplicationDescriptorConfigurator;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @goal package
 * @phase package
 * @threadSafe
 */
public class PackageAdtMojo extends UnpackAdtMojo {

    /**
     * @parameter default-value="${project}
     */
    public MavenProject project;

    /**
     * ADT package target. available:
     * Desktop AIR runtime:
     * air,
     * airi
     * Android:
     * apk,
     * apk-debug,
     * apk-captive-runtime
     * IOS:
     * ipa-ad-hoc,
     * ipa-app-store,
     * ipa-debug,
     * ipa-test,
     * ipa-debug-interpreter,
     * ipa-test-interpreter
     * Native desktop
     * EXE
     * DMG
     * For more information visit Adobe website
     * http://help.adobe.com/en_US/air/build/WS901d38e593cd1bac1e63e3d128cdca935b-8000.html
     *
     * @parameter
     */
    public String target;

    /**
     * http://help.adobe.com/en_US/air/build/WS5b3ccc516d4fbf351e63e3d118666ade46-7f72.html
     *
     * @parameter default-value="pkcs12"
     */
    public String storetype;

    /**
     * Certificate file
     *
     * @parameter expression="${build.adt.keystore}"
     */
    public File keystore;

    /**
     * Password for certificate file.
     *
     * @parameter expression="${build.adt.storepass}"
     */
    public String storepass;

    /**
     * Required for iOS packages (ipa*).
     * Example: /Users/yelbota/myapp.mobileprovision
     *
     * @parameter expression="${build.adt.mobileprovision}"
     */
    public File provisioningProfile;

    /**
     * Output filename without extension.
     *
     * @parameter expression="${project.artifactId}-${project.version}"
     */
    public String fileNamePattern;

    /**
     * AIR Application descriptor. For example src/main/flex/MyProject-app.xml
     *
     * @parameter default-value="src/main/resources/application-descriptor.xml"
     */
    public File descriptor;

    /**
     * File to include in application descriptor
     *
     * @required
     * @parameter expression="${project.build.directory}/${project.build.finalName}.${project.packaging}"
     */
    public File applicationContent;

    /**
     * Root directory for includes.
     *
     * @parameter expression="${project.build.directory}/classes"
     */
    public File includesRoot;

    /**
     * List of files and directories to include into the package
     * relative to includesRoot directory
     *
     * @parameter
     */
    public List<String> includes;

    /**
     * A string value (such as "v1", "2.5", or "Alpha 1") that represents the version of the application, as it should
     * be shown to users.
     *
     * @parameter expression="${project.version}"
     */
    public String versionLabel;

    /**
     * A string value of the format <0-999>.<0-999>.<0-999> that represents application version which can be used
     * to check for application upgrade.Values can also be 1-part or 2-part. It is not necessary to have a 3-part value.
     * An updated version of application must have a versionNumber value higher than the previous version.
     *
     * @parameter default-value="0.0.0"
     */
    public String versionNumber;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        super.execute();

        validateConfiguration();

        File adtFile = FileUtils.resolveFile(sdkDirectory, "lib/adt.jar");
        File finalAppDescriptor = getFinalApplicationDescriptor();
        File aneDir = prepareAneDir();
        List<String> args = new ArrayList<String>();

        args.add("java");
        args.add("-jar");
        args.add(adtFile.getAbsolutePath());
        args.addAll(getPackageArguments());
        args.addAll(getCertificateArguments());
        args.addAll(getMainArgs(finalAppDescriptor));
        args.addAll(getIncludesArgs());

        if (aneDir.list().length > 0)
            args.addAll(getExtDirArguments(aneDir));

        try {

            String[] argsArray = args.toArray(new String[]{});
            getLog().debug(args.toString());
            Process process = Runtime.getRuntime().exec(argsArray);
            int code = process.waitFor();

            if (code > 0) {

                String adtOutput = "";
                String line = null;
                BufferedReader b = new BufferedReader(new InputStreamReader(process.getInputStream()));

                while ((line = b.readLine()) != null)
                    adtOutput += line + "\n";

                // Oops.
                throw failWith(
                        "Adt can't build the package. Error code #" + code +
                                ". Checkout official documentation here http://help.adobe.com/en_US/air/build/air_buildingapps.pdf",
                        adtOutput
                );
            }

        } catch (IOException e) {

            throw failWith("Cant execute adt", e.getLocalizedMessage());
        } catch (InterruptedException e) {
            throw failWith("ADT fails " + e);
        }
    }

    private File getFinalApplicationDescriptor() throws MojoFailureException {

        ApplicationDescriptorConfigurator configurator = new ApplicationDescriptorConfigurator(descriptor);
        File mutatedDescriptor = FileUtils.resolveFile(outputDirectory, "application-descriptor.xml");

        configurator.setContent(applicationContent.getName());
        configurator.setVersionLabel(versionLabel);
        configurator.setVersionNumber(versionNumber);
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

            throw failWith("Can't create ANE directory");
        }

    }

    public List<String> getPackageArguments() {

        List<String> args = new ArrayList<String>();

        args.add("-package");
        args.add("-target");
        args.add(target);

        return args;
    }

    public List<String> getCertificateArguments() {

        List<String> args = new ArrayList<String>();

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
    public List<String> getMainArgs(File finalAppDescriptorFile) {

        List<String> args = new ArrayList<String>();
        String ext = null;

        if (isiOSTarget()) {
            ext = "ipa";
        } else if (isAndroidTarget()) {
            ext = "apk";
        } else if (isUnsignedAirTarget()) {
            ext = "airi";
        } else {
            ext = "air";
        }

        File outputFile = new File(outputDirectory, fileNamePattern + "." + ext);

        args.add(outputFile.getAbsolutePath());
        args.add(finalAppDescriptorFile.getAbsolutePath());

        return args;
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
            if (storetype == null || storepass == null || keystore == null)
                throw new AdtConfigurationException("Signing options (storetype, keystore, storepass) must be defined");
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

}
