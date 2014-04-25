package controllers;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.App;
import models.User;
import play.data.DynamicForm;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import scala.Option;
import scala.util.Either;
import securesocial.core.Authenticator;
import views.html.index;

import java.io.File;
import java.util.HashMap;
import java.util.regex.Pattern;

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

            if (!app.users.contains(user)) {
                app.users.add(user);
                app.save();
            }
            return ok(index.render(true));
        }
    }

    public static Result post(String appName) {
        User user=getLoggedUser();
        App app = App.findByName(appName);
        //if(user!=null && (user.isAdmin() || app.owner.equals(user))){
            return ok(views.html.post.render(app));
        //}else{
            //TODO belle error page
        //    return ok("you're not admin of this page!");
        //}
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
        //    return ok("you're not admin!");
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

        //Check for errors
        ObjectNode json = Json.newObject();
        ArrayNode errors=new ArrayNode(JsonNodeFactory.instance);

        if(picture==null && link==null && text.length()>420) {
            errors.add("textOnlyLess420");
        }

        if(text.length()>1000) {
            errors.add("textLess1000");
        }

        String regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        if(link!=null && Pattern.matches(regex,link)) {
            errors.add("notALink");
        }

        //TODO big work cant fail here !!!


        if(errors.size()>0){
            return ok(json.put("errors",errors));
        }

        HashMap<String, String> states = Post.send(app, text, link, file, form);
        return ok(views.html.result.render(states));

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
        Http.Cookie cookie = ctx().request().cookie(Authenticator.cookieName());
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
