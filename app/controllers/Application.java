package controllers;

import com.restfb.DefaultFacebookClient;
import com.restfb.Parameter;
import com.restfb.types.FacebookType;
import models.IdentityId;
import org.apache.commons.codec.binary.Hex;
import play.Logger;
import play.data.DynamicForm;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import scala.Option;
import scala.util.Either;
import securesocial.core.Authenticator;
import views.html.index;
import views.html.post;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class Application extends Controller {

    public static Result index() {
        Http.Cookie cookie = ctx().request().cookie(Authenticator.cookieName());

        if(cookie!=null) {
            Either<Error, Option<Authenticator>> authenticator = Authenticator.find(cookie.value());

            if (authenticator.isRight()) {
                Option<Authenticator> auth = authenticator.right().get();

                if (auth.isDefined()) {

                    if (auth.get().identityId().providerId().equals("facebook")) {
                        return ok(index.render(true));
                    }
                }
            }
        }

        return ok(index.render(false));
    }

    public static Result post() {
        return ok(post.render());
    }

    public static Result submitPost() {
        DynamicForm form = play.data.Form.form().bindFromRequest();
        String pass = form.data().get("pass");
        String text = form.data().get("message");
        String link = form.data().get("link");
        String picture = form.data().get("picture");

        HashMap<String, String> states=new HashMap<String,String>();

        //generate hash
        String hash="";
        try {
            byte[] bytes = MessageDigest.getInstance("MD5").digest(pass.getBytes());
            hash=new String(Hex.encodeHex(bytes));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return ok("bad paw");
        }

        //verify hash
        if(!hash.equals( "edcfb26d01d48b1a15ec3f380e65dc6c")){
            return ok("bad paw");
        }

        if(text != null) {

            for (IdentityId identityId : IdentityId.findAll()) {

                if(identityId.providerId.equals("facebook") && form.data().get(identityId.userId).equals("on")) {
                    try {
                        new DefaultFacebookClient("663029233732748|e8545358f3f4d25ccca4d536bd82e765")
                                .publish(identityId.userId + "/feed",
                                        FacebookType.class,
                                        Parameter.with("message", text),
                                        Parameter.with("link", link),
                                        Parameter.with("picture", picture)
                                );
                        states.put(identityId.fullname,"Posté");
                    } catch (Exception e) {
                        states.put(identityId.fullname,e.toString());
                        e.printStackTrace();
                    }
                }
            }

            return ok(views.html.result.render(states));

        }

        return ok("text vide");
    }
}
