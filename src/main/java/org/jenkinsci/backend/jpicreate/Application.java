package org.jenkinsci.backend.jpicreate;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.HttpResponses;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.framework.adjunct.AdjunctManager;
import org.kohsuke.stapler.interceptor.RequirePOST;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * @author Kohsuke Kawaguchi
 */
public class Application {
    public final AdjunctManager adjuncts;

    public Application(ServletContext context) {
        this.adjuncts = new AdjunctManager(context,getClass().getClassLoader(),"adjuncts");
    }

    @RequirePOST
    public HttpResponse doGenerate(@QueryParameter("name") String _name) throws IOException, InterruptedException {
        if (_name.endsWith("-plugin"))  _name = _name.substring(0,_name.length()-7);

        final String name = _name;
        if (!NAME.matcher(name).matches())
            return HttpResponses.plainText("Invalid plugin name: "+name);

        File settings = File.createTempFile("settings","xml");
        FileUtils.copyURLToFile(getClass().getClassLoader().getResource("settings.xml"),settings);

        try {
            final File tmpDir = File.createTempFile("plugin","gen");
            tmpDir.delete();
            tmpDir.mkdir();

            Process proc = new ProcessBuilder("mvn",
                    "-B","-U",
                    "-s",settings.getAbsolutePath(),
                    "org.jenkins-ci.tools:maven-hpi-plugin:LATEST:create",
                    "-DgroupId=org.jenkins-ci.plugins",
                    "-DartifactId="+name)
                .redirectErrorStream(true)
                .directory(tmpDir)
                .start();
            proc.getOutputStream().close();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            IOUtils.copy(proc.getInputStream(), baos);
            if (proc.waitFor()!=0) {
                // error
                return HttpResponses.plainText(baos.toString());
            }

            return new HttpResponse() {
                public void generateResponse(StaplerRequest req, StaplerResponse rsp, Object node) throws IOException, ServletException {
                    try {
                        Process proc = new ProcessBuilder("zip","-r","-",".")
                                .directory(tmpDir)
                                .start();
                        proc.getOutputStream().close();
                        rsp.setContentType("application/octet-stream");
                        rsp.setHeader("Content-Disposition","attachment; filename="+name+"-plugin.zip");
                        IOUtils.copy(proc.getInputStream(),rsp.getOutputStream());
                        proc.waitFor();
                        proc.getInputStream().close();
                        proc.getErrorStream().close();
                    } catch (InterruptedException e) {
                        throw (IOException)new IOException().initCause(e);
                    } finally {
                        FileUtils.deleteDirectory(tmpDir);
                    }
                }
            };
        } finally {
            settings.delete();
        }
    }

    private static final Pattern NAME = Pattern.compile("[a-zA-Z0-9_-]+");
}

