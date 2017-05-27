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
}
