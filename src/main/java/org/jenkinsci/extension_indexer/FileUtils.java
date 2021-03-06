package org.jenkinsci.extension_indexer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Simple collection of utility stuff for Files.
 *
 * @author Robert Sandell &lt;robert.sandell@sonyericsson.com&gt;
 */
public final class FileUtils extends org.apache.commons.io.FileUtils {

    /**
     * Unzips a zip/jar archive into the specified directory.
     *
     * @param file        the file to unzip
     * @param toDirectory the directory to extract the files to.
     */
    public static void unzip(File file, File toDirectory) throws IOException {
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(file);

            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();

                if (entry.isDirectory()) {
                    File dir = new File(toDirectory, entry.getName());
                    dir.mkdirs();
                    continue;
                }
                File entryFile = new File(toDirectory, entry.getName());
                entryFile.getParentFile().mkdirs();
                copyInputStream(zipFile.getInputStream(entry),
                        new BufferedOutputStream(new FileOutputStream(entryFile)));
            }
        } finally {
            if (zipFile != null) {
                zipFile.close();
            }
        }
    }

    private static void copyInputStream(InputStream in, OutputStream out)
            throws IOException {
        byte[] buffer = new byte[1024];
        int len;

        while ((len = in.read(buffer)) >= 0)
            out.write(buffer, 0, len);

        in.close();
        out.close();
    }

    public static Iterable<File> getFileIterator(File dir, String extension) {
        Iterator i = iterateFiles(dir, new String[]{extension}, true);
        LinkedList<File> l = new LinkedList<File>();
        while(i.hasNext()) {
            l.add((File) i.next());
        }
        return l;
    }
}
