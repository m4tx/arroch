package models;

import com.typesafe.config.ConfigFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import play.Logger;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static java.awt.RenderingHints.*;
import static java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR;

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

    public static void createDirectories() {
        new File(filePath + "/min").mkdirs();
    }

    public static void deleteAllFiles() throws IOException {
        Logger.warn("!!!Purging files directory!!!");
        FileUtils.cleanDirectory(new File(filePath));
        createDirectories();
    }

    public static File getFile(FileMeta meta) {
        return new File(getFilePath(meta));
    }

    private static BufferedImage minifyImg(BufferedImage orig, int height, int width) {
        double outputAspect = 1.0*width/height;
        double inputAspect = 1.0*orig.getWidth()/orig.getHeight();
        if (outputAspect < inputAspect) {
            height = (int)(width/inputAspect);
        } else {
            width = (int)(height*inputAspect);
        }
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) img.getGraphics();
        g.setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(orig, 0, 0, width, height, null);
        g.dispose();
        return img;
    }

    public static String getThumbnailPath(FileMeta meta) {
        return filePath + "/min/" + meta.getId() + '.' + meta.getExtension();
    }

    public static File getThumbnail(FileMeta meta) throws IOException {
        if (!meta.getMimeType().equals("image/jpeg") && !meta.getMimeType().equals("image/png")) {
            return new File("public/images/file.svg");
        }

        File img = new File(getFilePath(meta));
        File thumbnail = new File(getThumbnailPath(meta));
        if (!thumbnail.exists() || (thumbnail.lastModified() < img.lastModified())) {
            BufferedImage orig = ImageIO.read(img);
            BufferedImage thumb = minifyImg(orig, 100, 100);
            ImageIO.write(thumb, "jpg", thumbnail);
        }

        return thumbnail;
    }
}
