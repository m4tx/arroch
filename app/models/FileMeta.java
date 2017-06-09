package models;

import modules.preloader.DatabasePreloader;

import javax.persistence.*;
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

    // SHA-512 hex-encoded digest
    @Column(name = "digest", length = 128)
    private String digest;

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

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getDigest() {
        if (digest == null) {
            try {
                digest = FileManager.getSha512Digest(FileManager.getFile(this));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
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
