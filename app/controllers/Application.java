package controllers;

import play.*;
import play.mvc.*;
import play.db.jpa.*;

import views.html.pages.index;

public class Application extends Controller {
    public Result index() {
        return ok(index.render());
    }

}
