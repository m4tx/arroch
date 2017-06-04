package models;

import org.junit.Test;
import play.db.jpa.JPAApi;
import tools.WithTestDatabase;
import utils.RandomUtils;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class FileManagerTest extends WithTestDatabase {

    @Test
    public void testAddFile() throws IOException {
        JPAApi jpaApi = app.injector().instanceOf(JPAApi.class);
        File img = new File("test.jpg");
        RandomUtils.randomImage(100, 100, img);
        jpaApi.withTransaction(() -> {
            try {
                FileMeta m = FileManager.addFile(img, jpaApi.em());
                FileManager.deleteFile(m, jpaApi.em());
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        });
        assertTrue(img.delete());

    }

    @Test
    public void testCreateFile() {
        JPAApi jpaApi = app.injector().instanceOf(JPAApi.class);
        jpaApi.withTransaction(() -> {
            try {
                 FileMeta meta = FileManager.createFile("test.jpg", "image/jpeg", jpaApi.em());
                 File img = FileManager.getFile(meta);
                 RandomUtils.randomImage(100, 100, img);
                 FileManager.deleteFile(meta, jpaApi.em());
                 assertFalse(img.exists());

                 File test = FileManager.getFile(FileManager.createFile("test.jpg", "image/jpeg", jpaApi.em()));
                 for (int i = 0; i < 10; i++) {
                    test = FileManager.getFile(FileManager.createFile("test" + i + ".jpg", "image/jpeg", jpaApi.em()));
                    RandomUtils.randomImage(10, 10, test);
                    assertTrue(test.exists());
                 }
                FileManager.deleteAllFiles();
                assertFalse(test.exists());

            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        });
    }

    @Test
    public void testEquals() {
        JPAApi jpaApi = app.injector().instanceOf(JPAApi.class);
        File file = new File("test.txt");
        jpaApi.withTransaction(() -> {
            try {
                if (file.createNewFile()) {
                    FileMeta meta, meta2;
                    meta = FileManager.addFile(file, jpaApi.em());
                    assertEquals(FileManager.getFile(meta).getName(), file.getName());
                    assertNotEquals(FileManager.getFilePath(meta), FileManager.getFile(meta).getPath());

                    meta = FileManager.addFile(new File("test.txt"), jpaApi.em());
                    meta2 = FileManager.addFile(new File("test.txt"), jpaApi.em());
                    assertEquals(FileManager.getFile(meta).getName(), FileManager.getFile(meta2).getName());
                    assertNotEquals(FileManager.getFilePath(meta), FileManager.getFilePath(meta2));
                    assertNotEquals(FileManager.getFile(meta), FileManager.getFile(meta2));

                    meta = FileManager.addFile(file, jpaApi.em());
                    meta2 = FileManager.addFile(file, jpaApi.em());
                    assertNotEquals(meta, meta2);
                    assertNotEquals(FileManager.getFile(meta), FileManager.getFile(meta2));
                    assertNotEquals(FileManager.getFilePath(meta), FileManager.getFilePath(meta2));

                    FileManager.deleteAllFiles();
                }
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }

    });
    }

    @Test
    public void testThumbnail() {
        JPAApi jpaApi = app.injector().instanceOf(JPAApi.class);
        jpaApi.withTransaction(() -> {
            try {
                File file = new File("test.txt");
                File file2 = new File("test2.txt");
                FileMeta meta, meta2;
                File thumbnail, thumbnail2 = null;

                if (file.createNewFile() && file2.createNewFile()) {
                    meta = FileManager.addFile(file, jpaApi.em());
                    meta2 = FileManager.addFile(file2, jpaApi.em());
                    thumbnail = FileManager.getThumbnail(meta);
                    thumbnail2 = FileManager.getThumbnail(meta2);
                    assertEquals(thumbnail, thumbnail2);
                    assertEquals(FileManager.getThumbnailPath(meta), FileManager.getThumbnailPath(meta2));
                    assertEquals(FileManager.getThumbnailPath(meta), thumbnail.getPath());
                }

                file = new File("test.jpg");
                RandomUtils.randomImage(500, 500, file);
                meta = FileManager.addFile(file, jpaApi.em());
                thumbnail = FileManager.getThumbnail(meta);
                assertNotEquals(thumbnail, thumbnail2);

                FileManager.deleteAllFiles();

            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }

        });
    }




}