package controllers;

import models.App;
import models.User;
import play.data.DynamicForm;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import securesocial.core.Identity;
import securesocial.core.java.SecureSocial;
import views.html.index;
import views.html.post;

import java.io.File;
import java.util.HashMap;

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

        User user = getLoggedUser();
        if(user==null) {
            ctx().session().put("original-url", "/" + appName);
            ctx().session().put("app-url", "/" + appName);
            return ok(index.render(false));
        }else{

            if (app.owner == null) {
                app.owner = user;
                app.save();
                return redirect(routes.Application.post(appName));
            }

            if (app.owner.equals(user)) {
                return redirect(routes.Application.post(appName));
            }

            if (!app.users.contains(user)) {
                app.users.add(user);
                app.save();
            }
            return ok(index.render(true));
        }
    }

    public static Result post(String appName) {
        User user=getLoggedUser();
        if(user!=null && (user.isAdmin() || App.findByName(appName).owner.equals(user))){
            return ok(post.render(appName));
        }else{
            //TODO belle error page
            return ok("you're not admin of this page!");
        }
    }

    public static Result submitPost(String appName) {

        App app=App.findByName(appName);
        if(app==null){
            //TODO belle error page
            return badRequest("app doesnt exist");
        }

        User user=getLoggedUser();
        if(user==null || !user.isAdmin() || !app.owner.equals(user)){
            //TODO belle error page
            return ok("you're not admin!");
        }

        DynamicForm form = play.data.Form.form().bindFromRequest();
        String text = form.data().get("message");
        String link = form.data().get("link");
        File file = null;

        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart picture = body.getFile("picture");
        if (picture != null) {
            file = picture.getFile();
        }

        if (text != null) {
            HashMap<String, String> states = Post.send(app, text, link, file, form);
            return ok(views.html.result.render(states));
        }
        //TODO belle error page
        return ok("text vide");
    }

    public static Result admin() {
        User user=getLoggedUser();
        if(user!=null && user.isAdmin()){
            return ok(views.html.admin.render());
        }else{
            //TODO belle error page
            return ok("you're not admin!");
        }
    }

    public static Result adminSubmit() {
        User user=getLoggedUser();
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

    public static User getLoggedUser(){
        Identity identity = SecureSocial.currentUser();
        if(identity !=null) {
            return User.findByIdentityId(identity.identityId());
        }
        return null;
    }

}
