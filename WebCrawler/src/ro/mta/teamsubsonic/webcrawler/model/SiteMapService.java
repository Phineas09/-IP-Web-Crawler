package ro.mta.teamsubsonic.webcrawler.model;

import ro.mta.teamsubsonic.webcrawler.model.exceptions.FileException;
import ro.mta.teamsubsonic.webcrawler.model.exceptions.InputException;
import ro.mta.teamsubsonic.webcrawler.model.exceptions._CrawlerException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that makes a sitemap
 * Implements {@link Crawler}
 * @author VladTeapa
 */
public class SiteMapService implements Crawler {

    /**
     * Members of the class
     */
    private String path;
    private String outFile;

    /**
     * Constructor
     *
     * @param path    this is the path to the root directory
     * @param outFile this is the path to the file that will be written in
     */
    public SiteMapService(String path, String outFile) {
        this.path = path;
        this.outFile = outFile;
    }

    /**
     * Class that creates the sitemap and writes it to outFile
     **/
    @Override
    public void run() {
        try {
            boolean flag = false;

            File file = new File(path);

            List<Integer> depth = new ArrayList<>();
            List<File> fileList = new ArrayList<>();

            depth.add(0);
            fileList.add(file);

            if (!file.exists()) {
                throw new InputException("File doesn't exist!");
            }

            if (!file.isDirectory()) {
                throw new FileException("Target directory is not a directory!");
            }


            for (int i = 0; i < fileList.size(); i++) {
                File[] subDirectoryList = fileList.get(i).listFiles();
                if (subDirectoryList != null) {
                    for (int j = 0; j < subDirectoryList.length; j++) {
                        file = subDirectoryList[j];
                        if (file.isDirectory())
                            depth.add(i+1,-(Math.abs(depth.get(i)) + 1));
                        else
                            depth.add(i+1,Math.abs(depth.get(i)) + 1);
                        fileList.add(i+1,subDirectoryList[j]);
                    }
                }
            }

            try {
                file = new File(outFile);
                file.createNewFile();
            } catch (IOException ex) {
                throw new FileException("Not enough privileges!");
            }

            FileWriter fileWriter = new FileWriter(file);
            file = new File(path);
            fileWriter.write(file.getName()+"\n");
            for (int i = 1; i < fileList.size(); i++) {

                int d = depth.get(i);

                if (d < 0) {
                    flag = true;
                }

                d = Math.abs(d);
                for (int j = 0; j < d; j++) {
                    fileWriter.write("\t");
                }
                fileWriter.write(fileList.get(i).getName());
                if (flag) {
                    fileWriter.write("/");
                    flag = false;
                }
                fileWriter.write("\n");
            }
            fileWriter.close();
        } catch (_CrawlerException ex) {
            ex.getMessage();
        } catch (Exception ex) {
            InputException exception = new InputException("Path is empty!");
            exception.getMessage();
        }
    }
}
