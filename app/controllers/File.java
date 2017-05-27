package controllers;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Result;

import static play.mvc.Results.ok;

public class File {
    @Transactional
    public Result get(Long id) {
        models.FileMeta fileMeta = JPA.em().find(models.FileMeta.class, id);
        java.io.File file = models.FileManager.getFile(fileMeta);
        return ok(file).as(fileMeta.getMimeType());
    }
}
