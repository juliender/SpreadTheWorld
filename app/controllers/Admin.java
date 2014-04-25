package controllers;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.App;
import models.Picture;
import models.User;
import play.Logger;
import play.data.DynamicForm;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.post;

import java.io.File;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * Created by julien on 25/04/14.
 */
public class Admin extends Controller{

    public static Result post(String appName) {
        User user=Application.getLoggedUser(ctx());
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

        User user=Application.getLoggedUser(ctx());
        if(user==null || !user.isAdmin() || !app.owner.equals(user)){
            //TODO belle error page
            //    return ok("you're not admin!");
        }

        DynamicForm form = play.data.Form.form().bindFromRequest();
        String text = form.data().get("message");
        String link = form.data().get("link");
        Logger.error(text);

        File file = null;
        Http.MultipartFormData.FilePart picture=null;

        Http.MultipartFormData body = request().body().asMultipartFormData();
        if(body!=null) {
            picture = body.getFile("picture");
            if (picture != null) {
                Logger.error(("pic ok"));
                file = picture.getFile();
            }
        }

        //Check for errors
        ObjectNode json = Json.newObject();
        ArrayNode errors=new ArrayNode(JsonNodeFactory.instance);

        if(picture==null && link==null && text!=null && text.length()>420) {
            errors.add("textOnlyLess420");
        }

        if(text!=null && text.length()>1000) {
            errors.add("textLess1000");
        }

        String regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        if(link!=null && Pattern.matches(regex, link)) {
            errors.add("notALink");
        }

        //TODO big work cant fail here !!!


        if(errors.size()>0){
            return ok(json.put("errors",errors));
        }

        HashMap<String, String> states = Post.send(app, text, link, file, form);
        //return ok(views.html.result.render(states));
        flash("posted",states.values().size()+"");
        return ok(post.render(app));
    }

    public static Result submitModif(String appName){
        Logger.error("okin");
        App app=App.findByName(appName);
        if(app==null){
            //todo
            return ok("app null");
        }

        User user=Application.getLoggedUser(ctx());

//        if(user!=null || !user.equals(app.owner)){
//            //todo
//            //return ok("not admin");
//        }

        DynamicForm form = play.data.Form.form().bindFromRequest();
        String message = form.data().get("message");
        if(message.length()>300){
            flash("error_length_modif_message","");
        }
        if(message!=null){
            app.message=message;
        }

        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart background_picture = body.getFile("background_picture");
        Http.MultipartFormData.FilePart middle_picture = body.getFile("middle_picture");
        if (background_picture != null) {
            File file = background_picture.getFile();
            if(app.backgroundPicture==null){
                app.backgroundPicture=Picture.save(file, background_picture.getContentType());
            }else{
                Picture.updatePicture(app.backgroundPicture.id,file, background_picture.getContentType());
            }
            Logger.error("ok");
        }
        if (middle_picture != null) {
            File file = middle_picture.getFile();
            if(app.backgroundPicture==null){
                app.backgroundPicture=Picture.save(file, middle_picture.getContentType());
            }else{
                Picture.updatePicture(app.backgroundPicture.id,file, middle_picture.getContentType());
            }
        }
        app.save();
        return ok();
    }
}
