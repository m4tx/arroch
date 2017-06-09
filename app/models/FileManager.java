package models;

import com.typesafe.config.ConfigFactory;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import play.Logger;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.FileImageOutputStream;
import javax.persistence.EntityManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static java.awt.RenderingHints.*;
import static java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR;

public class FileManager {
    public static final float THUMB_JPEG_QUALITY = .85f;

    private static String filePath = ConfigFactory.load().getString("filePath");

    public static FileMeta addFile(File data, String name, String mimeType, EntityManager em) throws IOException {
        FileMeta f = new FileMeta();
        f.setExtension(FilenameUtils.getExtension(name));
        f.setMimeType(mimeType);
        f.setOriginalName(name);
        em.persist(f);
        File dest = getFile(f);
        FileUtils.copyFile(data, dest);
        return f;
    }

    public static FileMeta addFile(File data, EntityManager em) throws IOException {
        return addFile(data, data.getName(), Files.probeContentType(data.toPath()), em);
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
        f.setOriginalName(name);
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
        double outputAspect = 1.0 * width / height;
        double inputAspect = 1.0 * orig.getWidth() / orig.getHeight();
        if (outputAspect < inputAspect) {
            height = (int) (width / inputAspect);
        } else {
            width = (int) (height * inputAspect);
        }
        int type = (orig.getTransparency() == Transparency.OPAQUE) ?
                BufferedImage.TYPE_INT_RGB :
                BufferedImage.TYPE_INT_ARGB;
        BufferedImage img = new BufferedImage(width, height, type);
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
            JPEGImageWriteParam jpegParams = new JPEGImageWriteParam(null);
            jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            jpegParams.setCompressionQuality(THUMB_JPEG_QUALITY);

            BufferedImage orig = ImageIO.read(img);
            BufferedImage thumb = minifyImg(orig, 100, 100);
            ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next();
            jpgWriter.setOutput(new FileImageOutputStream(thumbnail));
            jpgWriter.write(null, new IIOImage(thumb, null, null), jpegParams);
        }

        return thumbnail;
    }

    public static String getSha512Digest(File file) throws IOException {
        return getSha512Digest(FileUtils.readFileToByteArray(file));
    }

    public static String getSha512Digest(byte[] fileData) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] digest = messageDigest.digest(fileData);
        return Hex.encodeHexString(digest);
    }
}
