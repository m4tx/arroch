package models;

import com.typesafe.config.ConfigFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import javax.persistence.EntityManager;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileManager {
    private static String filePath = ConfigFactory.load().getString("filePath");

    public static FileMeta addFile(File data, EntityManager em) throws IOException {
        FileMeta f = new FileMeta();
        f.setExtension(FilenameUtils.getExtension(data.getName()));
        f.setMimeType(Files.probeContentType(data.toPath()));
        f.setOrignalName(data.getName());
        em.persist(f);
        File dest = getFile(f);
        FileUtils.copyFile(data, dest);
        return f;
    }

    public static String getFilePath(FileMeta meta) {
        return filePath + '/' + meta.getId() + '.' + meta.getExtension();
    }

    public static void deleteFile(FileMeta file, EntityManager em) throws IOException {
        if (!new File(getFilePath(file)).delete()) throw new IOException("Cannot delete file");
        em.remove(file);
    }

    public static FileMeta createFile(String name, String mimeType, EntityManager em) {
        FileMeta f = new FileMeta();
        f.setExtension(FilenameUtils.getExtension(name));
        f.setMimeType(mimeType);
        f.setOrignalName(name);
        em.persist(f);
        return f;
    }

    public static File getFile(FileMeta meta) {
        return new File(getFilePath(meta));
    }
}
