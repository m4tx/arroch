package controllers.people;

import models.FileManager;
import models.FileMeta;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.uploadFile;

import javax.persistence.EntityManager;
import java.io.File;
import java.io.IOException;

public class UploadFile extends Controller {
    @Transactional
    public Result get(Long id) {
        EntityManager em = JPA.em();
        models.Person person = em.find(models.Person.class, id);
        return ok(uploadFile.render(person));
    }

    @Transactional
    public Result post(Long id) throws IOException {
        EntityManager em = JPA.em();
        models.Person person = em.find(models.Person.class, id);

        Http.MultipartFormData<File> body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart<File> file = body.getFile("file");
        if (file != null) {
            FileMeta fileMeta = FileManager.addFile(
                    file.getFile(), file.getFilename(), file.getContentType(), em);
            person.getFiles().add(fileMeta);
            flash("success", "File was uploaded successfully");
            return redirect(routes.Person.getFiles(id));
        } else {
            throw new RuntimeException("Missing file");
        }
    }
}
