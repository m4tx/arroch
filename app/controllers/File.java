package controllers;

import models.FileManager;
import models.FileMeta;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Result;
import views.html.pages.group.fileInfo;

import java.io.IOException;

import static play.mvc.Results.ok;

public class File {
    @Transactional
    public Result get(Long id) {
        models.FileMeta fileMeta = JPA.em().find(models.FileMeta.class, id);
        java.io.File file = models.FileManager.getFile(fileMeta);
        return ok(file, fileMeta.getOriginalName()).as(fileMeta.getMimeType());
    }

    @Transactional
    public Result getPreview(Long id) throws IOException {
        models.FileMeta fileMeta = JPA.em().find(models.FileMeta.class, id);
        java.io.File file = models.FileManager.getThumbnail(fileMeta);
        return ok(file);
    }

    @Transactional
    public Result getInfo(Long id) {
        FileMeta meta = JPA.em().find(models.FileMeta.class, id);
        long length = FileManager.getFile(meta).length();
        return ok(fileInfo.render(meta, length));
    }
}
