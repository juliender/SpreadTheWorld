package controllers;

import com.restfb.DefaultFacebookClient;
import com.restfb.Parameter;
import com.restfb.types.FacebookType;
import models.IdentityId;
import play.Logger;
import play.data.DynamicForm;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;
import views.html.post;

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
