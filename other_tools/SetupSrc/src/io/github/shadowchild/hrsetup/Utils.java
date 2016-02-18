package io.github.shadowchild.hrsetup;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Observer;

/**
 * Created by Zach Piddock on 07/11/2015.
 */
public class Utils {

    /**
     * The fully customized way to the the downloader
     *
     * @param url          - The URL to download from
     * @param observer     - The observer to handle progress updates
     * @param outputFolder - The folder to save the file to
     * @throws IOException
     */
    public static void downloadFile(String url, Observer observer, File outputFolder) throws
            IOException {

        Download download = new Download(new URL(url), observer, outputFolder);
        download.download();
    }

    /**
     * The most useful way to use the downloader
     * Uses the default observer
     *
     * @param url          - The URL to download from
     * @param outputFolder - The folder to save the file to
     * @throws IOException
     */
    public static void downloadFile(String url, File outputFolder) throws IOException {

        Download download = new Download(new URL(url), outputFolder);
        download.download();
    }

    /**
     * The least useful way to use the downloader
     * The is usually used for testing as it saves to a test directory
     *
     * @param url - The URL to download from
     * @throws IOException
     */
    public static void downloadFile(String url) throws IOException {

        Download download = new Download(new URL(url));
        download.download();
    }

    /**
     * Gets the file name from a supplied URL
     *
     * @param url - The URL to get the file name from
     * @return - The String of the filename from the URL
     */
    public static String getFileName(URL url) {

        String fileName = url.getFile();
        return fileName.substring(fileName.lastIndexOf('/') + 1);
    }

    public static final boolean BUILT = Utils.class.getProtectionDomain().getCodeSource().getLocation().getPath().endsWith(".jar");
}
