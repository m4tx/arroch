package models;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "files")
public class File {
    @Id
    @GeneratedValue
    @Column(name = "file_id")
    private long id;

    @Column(nullable = false)
    @Size(max = 15)
    private String extension;

    @Column(name = "mime_type", nullable = false)
    @Size(max = 255)
    private String mimeType;

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
}
