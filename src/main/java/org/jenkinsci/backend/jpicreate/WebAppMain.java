package org.jenkinsci.backend.jpicreate;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.kohsuke.stapler.StaticViewFacet;
import org.kohsuke.stapler.WebApp;
import org.kohsuke.stapler.framework.AbstractWebAppMain;

import javax.servlet.ServletContextEvent;
import javax.sound.midi.SysexMessage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * @author Kohsuke Kawaguchi
 */
public class WebAppMain extends AbstractWebAppMain<Application> {
    public WebAppMain() {
        super(Application.class);
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        super.contextInitialized(event);
        WebApp.get(context).facets.add(new StaticViewFacet(".html"));
    }

    @Override
    protected String getApplicationName() {
        return "APP";
    }

    @Override
    protected Object createApplication() throws Exception {
        File home = extractMaven();
        return new Application(context, new File(home,"bin/mvn"));
    }

    private File extractMaven() throws IOException, InterruptedException {
        File zip = File.createTempFile("maven","zip");
        FileUtils.copyURLToFile(
                getClass().getClassLoader().getResource("maven.zip"),
                zip);
        File bin = Files.createTempDirectory("maven" + "bin").toFile();

        Process unzip = new ProcessBuilder("unzip", zip.getAbsolutePath())
                .directory(bin).redirectErrorStream(true).start();
        unzip.getOutputStream().close();
        IOUtils.copy(unzip.getInputStream(), System.out);
        if (unzip.waitFor()!=0) {
            throw new Error("Unzip Maven failed");
        }
        return bin.listFiles()[0];
    }
}
