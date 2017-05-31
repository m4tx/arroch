package models;

import modules.preloader.DatabasePreloader;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.File;
import java.io.IOException;

@Entity
@Cacheable
@Table(name = "files")
public class FileMeta {
    @Id
    @GeneratedValue
    @Column(name = "file_id")
    private long id;

    @Column(nullable = false, length = 15)
    private String extension;

    @Column(name = "mime_type", nullable = false, length = 100)
    private String mimeType;

    @Column(name = "original_name", length = 260)
    private String originalName;

    public long getId() {
        return id;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String orignalName) {
        this.originalName = orignalName;
    }

    static {
        FileManager.createDirectories();

        DatabasePreloader.addTest(em -> {
            try {
                FileManager.deleteAllFiles();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, 0);
    }
}
