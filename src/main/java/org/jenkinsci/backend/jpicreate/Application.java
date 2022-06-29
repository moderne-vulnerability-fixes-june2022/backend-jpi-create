package org.jenkinsci.backend.jpicreate;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.TeeOutputStream;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Tar;
import org.apache.tools.ant.taskdefs.Tar.TarCompressionMethod;
import org.apache.tools.ant.taskdefs.Zip;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.HttpResponses;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.framework.adjunct.AdjunctManager;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * @author Kohsuke Kawaguchi
 */
public class Application {
    public final AdjunctManager adjuncts;
    private final File mvn;
    private final ScheduledExecutorService periodUpdateCheck = Executors.newScheduledThreadPool(1);

    public Application(ServletContext context, File mvn) {
        this.adjuncts = new AdjunctManager(context,getClass().getClassLoader(),"adjuncts");
        this.mvn = mvn;

        // periodically perform up-to-date check
        periodUpdateCheck.submit(new Callable<Object>() {
            public Object call() throws Exception {
                HttpResponse r = doGenerate("bogus", Type.ZIP, true);
                if (r instanceof Archive)
                    ((Archive)r).clear();
                periodUpdateCheck.schedule(this,15,TimeUnit.MINUTES);
                return null;
            }
        });
    }

    public HttpResponse doGenerate(@QueryParameter("name") String _name, @QueryParameter Type type, @QueryParameter boolean updateCheck) throws IOException, InterruptedException {
        if (_name.endsWith("-plugin"))  _name = _name.substring(0,_name.length()-7);

        final String name = _name;
        if (!NAME.matcher(name).matches())
            return HttpResponses.plainText("Invalid plugin name: "+name);

        LOGGER.info("Generating "+name);

        File settings = File.createTempFile("settings","xml");
        FileUtils.copyURLToFile(getClass().getClassLoader().getResource("settings.xml"),settings);

        final File tmpDir = Files.createTempDirectory("plugin" + "gen").toFile();
        try {

            List<String> args = new ArrayList<String>();
            args.add(mvn.getAbsolutePath());
            if (updateCheck)    args.add("-U"); // limit up to date check
            args.addAll(Arrays.asList(
                    "-B",
                    "-s", settings.getAbsolutePath(),
                    "org.jenkins-ci.tools:maven-hpi-plugin:LATEST:create",
                    "-DgroupId=org.jenkins-ci.plugins",
                    "-DartifactId=" + name,
                    "-DpackageName=org.jenkinsci.plugins." + name.replace('-', '_')));
            ProcessBuilder pb = new ProcessBuilder(args);

            pb.environment().put("JAVA_HOME",System.getProperty("java.home"));

            Process proc = pb
                .redirectErrorStream(true)
                .directory(tmpDir)
                .start();
            proc.getOutputStream().close();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            InputStream is = proc.getInputStream();
            try {
                IOUtils.copy(is, new TeeOutputStream(System.out,baos));
                if (proc.waitFor()!=0) {
                    // error
                    return HttpResponses.plainText(baos.toString());
                }
            } finally {
                is.close();
            }

            final File archive;
            switch (type) {
            case ZIP:
                archive = File.createTempFile("plugin",".zip");
                archive.delete();
                Zip zip = new Zip();
                zip.setProject(new Project());
                zip.setDestFile(archive);
                zip.setBasedir(tmpDir);
                zip.execute();
                break;
            case TAR:
                archive = File.createTempFile("plugin",".tgz");
                archive.delete();
                Tar tar = new Tar();
                TarCompressionMethod comp = new TarCompressionMethod();
                comp.setValue("gzip");
                tar.setCompression(comp);
                tar.setProject(new Project());
                tar.setDestFile(archive);
                tar.setBasedir(tmpDir);
                tar.execute();
                break;
            default:
                throw new Error();
            }

            return new Archive(name + "-plugin" + type.extension, archive);
        } finally {
            FileUtils.deleteDirectory(tmpDir);
            settings.delete();
        }
    }

    private static class Archive implements HttpResponse {
        private final String fileName;
        private final File archive;

        public Archive(String fileName, File archive) {
            this.fileName = fileName;
            this.archive = archive;
        }

        public void generateResponse(StaplerRequest req, StaplerResponse rsp, Object node) throws IOException, ServletException {
            try {
                rsp.setContentType("application/octet-stream");
                rsp.setHeader("Content-Disposition", "attachment; filename=" + fileName);
                InputStream is = new FileInputStream(archive);
                try {
                    IOUtils.copy(is, rsp.getOutputStream());
                } finally {
                    is.close();
                }
            } finally {
                clear();
            }
        }

        private void clear() {
            archive.delete();
        }
    }

    private static final Pattern NAME = Pattern.compile("[a-zA-Z0-9_-]+");

    private static final Logger LOGGER = Logger.getLogger(Application.class.getName());
}

