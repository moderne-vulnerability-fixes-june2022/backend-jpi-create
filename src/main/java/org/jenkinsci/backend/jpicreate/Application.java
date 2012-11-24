package org.jenkinsci.backend.jpicreate;

import org.kohsuke.stapler.framework.adjunct.AdjunctManager;

import javax.servlet.ServletContext;

/**
 * @author Kohsuke Kawaguchi
 */
public class Application {
    public final AdjunctManager adjuncts;

    public Application(ServletContext context) {
        this.adjuncts = new AdjunctManager(context,getClass().getClassLoader(),"adjuncts");
    }
}
