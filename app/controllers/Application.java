package controllers;

import models.App;
import models.User;
import play.data.DynamicForm;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import scala.Option;
import scala.util.Either;
import securesocial.core.Authenticator;
import views.html.index;

public class Application extends Controller {

    public static Result index() {
        //TODO belle home page
        return ok("home page");
    }

    public static Result appPage(String appName) {
        App app = App.findByName(appName);
        if (app == null) {
            //TODO belle error page
            return ok("not exist");
        }

        User user = getLoggedUser(ctx());
        if(user==null) {
            ctx().session().put("original-url", "/" + appName);
            ctx().session().put("app-url", "/" + appName);
            return ok(index.render(app, user));
        }else{

            if (app.owner == null) {
                app.owner = user;
                app.save();
                return redirect(routes.Admin.post(appName));
            }

            if (!app.users.contains(user)) {
                app.users.add(user);
                app.save();
            }
            return ok(index.render(app, user));
        }
    }


    public static Result admin() {
        User user=getLoggedUser(ctx());
        if(user!=null && user.isAdmin()){
            return ok(views.html.admin.render());
        }else{
            //TODO belle error page
            return ok("you're not admin!");
        }
    }

    public static Result adminSubmit() {
        User user=getLoggedUser(ctx());
        if(user!=null && user.isAdmin()){

            DynamicForm form = play.data.Form.form().bindFromRequest();
            String name = form.data().get("name");

            if (App.create(name)) {
                return ok("créé");

            } else {
                return ok("already existe");
            }
        }else{
            return ok("you're not admin!");
        }
    }

    public static User getLoggedUser(Http.Context ctx){
        Http.Cookie cookie = ctx.request().cookie(Authenticator.cookieName());
        if(cookie!=null) {
            Either<Error, Option<Authenticator>> authenticator = Authenticator.find(cookie.value());
            if (authenticator.isRight()) {
                Option<Authenticator> auth = authenticator.right().get();
                if (auth.isDefined()) {
                    if (auth.get().identityId().providerId().equals("facebook")) {
                        return User.findByIdentityId(auth.get().identityId());
                    }
                }
            }
        }
        return null;
    }
}
