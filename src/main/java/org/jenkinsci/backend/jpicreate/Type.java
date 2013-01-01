package org.jenkinsci.backend.jpicreate;

/**
 * @author Kohsuke Kawaguchi
 */
public enum Type {
    ZIP(".zip"),TAR(".tar.gz");

    public final String extension;

    private Type(String extension) {
        this.extension = extension;
    }
}
