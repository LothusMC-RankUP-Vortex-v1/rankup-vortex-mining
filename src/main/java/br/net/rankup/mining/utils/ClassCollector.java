package br.net.rankup.mining.utils;

import java.util.jar.*;
import java.net.*;
import java.util.*;
import java.io.*;

public class ClassCollector<T>
{
    private final Class<?> parent;
    private final Class<T> type;
    private String packageName;
    private boolean selectInterfaces;
    
    public ClassCollector<T> filterByPackage(final String packageName) {
        this.packageName = packageName + ".";
        return this;
    }
    
    public ClassCollector<T> selectInterfaces(final boolean selectInterfaces) {
        this.selectInterfaces = selectInterfaces;
        return this;
    }
    
    public Collection<Class<T>> collect() throws IOException {
        final ArrayList<Class<T>> list = new ArrayList<Class<T>>();
        File file;
        try {
            final URI uri = this.parent.getProtectionDomain().getCodeSource().getLocation().toURI();
            file = new File(uri);
        }
        catch (URISyntaxException e2) {
            throw new IllegalArgumentException("Could not find specified file");
        }
        if (!file.exists()) {
            throw new IllegalArgumentException("Could not find specified file");
        }
        if (file.isDirectory()) {
            throw new IllegalArgumentException("The specified folder must be a file");
        }
        final JarFile jarFile = new JarFile(file);
        final Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            final JarEntry entry = entries.nextElement();
            String name = entry.getName().replace("/", ".");
            if (this.packageName != null) {
                if (!name.startsWith(this.packageName)) {
                    continue;
                }
                if (!name.endsWith(".class")) {
                    continue;
                }
            }
            name = name.substring(0, name.length() - 6);
            if (name.endsWith(".")) {
                continue;
            }
            try {
                final Class<?> aClass = Class.forName(name);
                if (aClass.isInterface() && !this.selectInterfaces) {
                    continue;
                }
                if (!this.type.isAssignableFrom(aClass)) {
                    continue;
                }
                list.add((Class<T>)aClass);
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
    
    public ClassCollector(final Class<?> parent, final Class<T> type) {
        this.selectInterfaces = false;
        this.parent = parent;
        this.type = type;
    }
}
