package controllers;

import play.*;
import play.mvc.*;
import play.db.jpa.*;

import views.html.*;

public class Application extends Controller {

    @Transactional //Force load JPA
    public Result index() {
        return ok(index.render("Hello World!"));
    }

}
