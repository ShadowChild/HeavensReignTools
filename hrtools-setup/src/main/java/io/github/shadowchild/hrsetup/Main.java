package io.github.shadowchild.hrsetup;


import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CountDownLatch;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by Zach Piddock on 15/01/2016.
 */
public class Main {

    static String platform;
    static File baseDir = new File(".");
    static File toolsDir = new File(baseDir, "Tools");
    static File gitFile = new File(baseDir, ".git");
    static File scFolder = new File(baseDir, "Cybernize");
    static File seFolder = new File(baseDir, "SilenceEngine");
    static CountDownLatch latch = new CountDownLatch(2);

    public static void main(String... args) {

        platform = args.length > 0 ? args[0] : "";

        // if it doesnt exist, download the relevant submodules
        if(!gitFile.exists()) {

            try {

                Download scDownload = new Download(
                        new URL("https://github.com/ShadowChild/Cybernize/archive/master.zip"),
                        new DownloadObserver(), new File(toolsDir, "downloads/Cybernize/")
                );
                Download seDownload = new Download(
                        new URL("https://github.com/sriharshachilakapati/SilenceEngine/archive/master.zip"),
                        new DownloadObserver(), new File(toolsDir, "downloads/SE/")
                );

                scDownload.download();
                seDownload.download();

                latch.await();

                File scZip = new File(toolsDir, "downloads/Cybernize/master.zip");
                File seZip = new File(toolsDir, "downloads/SE/master.zip");

                unZipIt(scZip.getAbsolutePath(),
                        new File(toolsDir, "downloads/temp/").getAbsolutePath()
                );
                unZipIt(seZip.getAbsolutePath(),
                        new File(toolsDir, "downloads/temp/").getAbsolutePath()
                );

                FileUtils.copyDirectory(new File(toolsDir, "downloads/temp/Cybernize-master"),
                        scFolder, true
                );
                FileUtils.copyDirectory(new File(toolsDir, "downloads/temp/SilenceEngine-master"),
                        seFolder, true
                );

                FileUtils.deleteDirectory(new File(toolsDir, "download"));

                System.out.println(scFolder.getAbsolutePath());

            } catch(IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {

            doCommon();
            doEngine();
        } catch(IOException | InterruptedException e) {

            e.printStackTrace();
        }
    }

    public static void doEngine() throws IOException, InterruptedException {

        // TODO: Add platform specific
        String gradlew = platform.equals("win") ? "gradlew.bat" : "gradlew";

        File gradleWrapper = new File(seFolder, gradlew);

        // compile the jar
        {
            String[] command = { gradleWrapper.getCanonicalPath(), "clean", "build", "javadoc" };
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.directory(seFolder.getCanonicalFile());

            Process process = builder.start();

            InputStream stream = process.getInputStream();
            InputStreamReader reader = new InputStreamReader(stream);
            BufferedReader br = new BufferedReader(reader);
            String line;
            System.out.printf("Output of running %s is:\n", Arrays.toString(command));
            while((line = br.readLine()) != null) {

                System.out.println(line);
            }

            int exitValue = process.waitFor();
            System.out.println("\n\nExit Value is " + exitValue);

            if(exitValue == 0) {

                FileUtils.copyDirectory(new File(seFolder, "build/libs"),
                        new File(baseDir, "libs/SilenceEngine")
                );
                FileUtils.copyDirectory(new File(seFolder, "libs"),
                        new File(baseDir, "libs/LWJGL")
                );
            }
        }

        // install into local maven repo
        {
            String mvnw = platform.equals("win") ? "mvnw.cmd" : "mvnw";
            File mvnWrapper = new File(scFolder, mvnw);

            String[] command = new String[] {
                    mvnWrapper.getCanonicalPath(),
                    "install:install-file",
                    "-Dfile=./build/libs/SilenceEngine.jar",
                    "-DgroupId=com.goharsha", "-DartifactId=SilenceEngine",
                    "-Dversion=0.4.1b",
                    "-Dpackaging=jar"
            };

            ProcessBuilder builder = new ProcessBuilder(command);
            builder.directory(seFolder.getCanonicalFile());

            Process process = builder.start();

            InputStream stream = process.getInputStream();
            InputStreamReader reader = new InputStreamReader(stream);
            BufferedReader br = new BufferedReader(reader);
            String line;
            System.out.printf("Output of running %s is:\n", Arrays.toString(command));
            while((line = br.readLine()) != null) {

                System.out.println(line);
            }

            int exitValue = process.waitFor();
            System.out.println("\n\nExit Value is " + exitValue);
        }
    }

    public static void doCommon() throws IOException, InterruptedException {

        // TODO: Add platform specific
        String mvnw = platform.equals("win") ? "mvnw.cmd" : "mvnw";
        File mvnWrapper = new File(scFolder, mvnw);

        String[] command = { mvnWrapper.getCanonicalPath(), "clean", "install", "-DskipTests=true" };
        ProcessBuilder builder = new ProcessBuilder(command);
        builder.directory(scFolder.getCanonicalFile());

        Process process = builder.start();

        InputStream stream = process.getInputStream();
        InputStreamReader reader = new InputStreamReader(stream);
        BufferedReader br = new BufferedReader(reader);
        String line;
        System.out.printf("Output of running %s is:\n", Arrays.toString(command));
        while((line = br.readLine()) != null) {

            System.out.println(line);
        }

        int exitValue = process.waitFor();
        System.out.println("\n\nExit Value is " + exitValue);

        if(exitValue == 0) {

            FileUtils.copyDirectory(new File(scFolder, "cybernize-core/build"),
                    new File(baseDir, "libs/Cybernize/core")
            );
            FileUtils.copyDirectory(new File(scFolder, "cybernize-opengl/build"),
                    new File(baseDir, "libs/Cybernize/opengl")
            );
        }
    }

    // TODO: Port to ShadowCommon

    /**
     * Unzip it
     *
     * @param zipFile      input zip file
     * @param outputFolder zip file output folder
     */
    public static void unZipIt(String zipFile, String outputFolder) {

        byte[] buffer = new byte[1024];

        try {

            //create output directory is not exists
            File folder = new File(outputFolder);
            if(!folder.exists()) {
                folder.mkdir();
            }

            //get the zip file content
            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
            //get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();

            while(ze != null) {

                String fileName = ze.getName();
                File newFile = new File(outputFolder + File.separatorChar + fileName);

                System.out.println("file unzip : " + newFile.getAbsoluteFile());

                //create all non exists folders
                //else you will hit FileNotFoundException for compressed folder
                new File(newFile.getParent()).mkdirs();
                if(ze.isDirectory()) { newFile.mkdirs(); } else newFile.createNewFile();

                if(newFile.isFile()) {

                    FileOutputStream fos = new FileOutputStream(newFile);

                    int len;
                    while((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }

                    fos.close();
                }
                ze = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();

            System.out.println("Done");

        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    public static class DownloadObserver implements Observer {

        @Override
        public void update(Observable o, Object arg) {

            Download download = (Download)o;

            switch(download.getStatus()) {

                case Download.DOWNLOADING: {

                    System.out.println("Progress = " + download.getProgress());
                    break;
                }
                case Download.COMPLETE: {

                    System.out.print("File Download Completed");
                    latch.countDown();
                    break;
                }
                case Download.ERROR: {

                    System.out.println("An Error Has Occurred");
                    break;
                }
            }
        }
    }
}
