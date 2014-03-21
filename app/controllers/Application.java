package controllers;

import play.*;
import play.mvc.*;

import views.html.*;

import models.*;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.Parameter;
import com.restfb.types.FacebookType;
import play.data.DynamicForm;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render());
    }

    public static Result post() {
        return ok(post.render());
    }

    public static Result submitPost() {
        DynamicForm form = play.data.Form.form().bindFromRequest();
        String text = form.data().get("message");
        String link = form.data().get("link");
        String picture = form.data().get("picture");
        if(text != null) {

            for (IdentityId identityId : IdentityId.findAll()) {
                try {

                    new DefaultFacebookClient("640059432708885|272b96015a4bae0b280756f9b1b60ebc")
                            .publish(identityId.userId + "/feed",
                                    FacebookType.class,
                                    Parameter.with("message", text),
                                    Parameter.with("link", link),
                                    Parameter.with("picture", picture)
                            );
                    Logger.info("postSent");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return ok("posté");
    }
}
