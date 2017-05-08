package controllers.people;

import models.PersonInfo;
import models.PropertyType;
import play.data.Form;
import play.data.FormFactory;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Result;
import utils.SimpleQuery;
import views.html.editPersonInfo;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

import static play.mvc.Controller.flash;
import static play.mvc.Results.ok;
import static play.mvc.Results.redirect;

public class EditPersonInfo {
    private FormFactory formFactory;

    @Inject
    public EditPersonInfo(FormFactory formFactory) {
        this.formFactory = formFactory;
    }

    @Transactional
    public Result get(Long id, Long infoId) {
        EntityManager em = JPA.em();
        Form<PersonInfo> form = formFactory.form(PersonInfo.class);
        models.Person person = em.find(models.Person.class, id);
        PersonInfo personInfo = em.find(PersonInfo.class, infoId);
        form = form.fill(personInfo);

        List<PropertyType> options = (new SimpleQuery<>(JPA.em(), PropertyType.class)).getResultList();
        return ok(editPersonInfo.render(person, personInfo, form, options));
    }

    @Transactional
    public Result post(Long id, Long infoId) {
        EntityManager em = JPA.em();
        Form<PersonInfo> form = formFactory.form(PersonInfo.class);
        PersonInfo personInfo = em.find(PersonInfo.class, infoId);

        PersonInfo newPersonInfo = form.bindFromRequest().get();
        personInfo.setType(newPersonInfo.getType());
        personInfo.setValue(newPersonInfo.getValue());
        flash("success", "This person's info has been edited");
        return redirect(controllers.people.routes.Person.get(id));
    }
}
