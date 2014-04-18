package controllers;

import com.restfb.BinaryAttachment;
import com.restfb.DefaultFacebookClient;
import com.restfb.Parameter;
import com.restfb.types.FacebookType;
import models.App;
import models.IdentityId;
import models.User;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
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

import java.io.File;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class Application extends Controller {

    public static Result index(String appName) {
        Logger.debug(appName);
        ctx().session().put("original-url","/"+appName);
        Http.Cookie cookie = ctx().request().cookie(Authenticator.cookieName());

        if(cookie!=null) {
            Either<Error, Option<Authenticator>> authenticator = Authenticator.find(cookie.value());

            if (authenticator.isRight()) {
                Option<Authenticator> auth = authenticator.right().get();

                if (auth.isDefined()) {

                    if (auth.get().identityId().providerId().equals("facebook")) {
                        User user= User.findByIdentityId(auth.get().identityId());
                        App app=App.get(appName);
                        if(!app.users.contains(user)){
                            app.users.add(user);
                            app.save();
                        }
                        return ok(index.render(true));
                    }
                }
            }
        }

        return ok(index.render(false));
    }

    public static Result post(String appName) {
            return ok(post.render(appName));
    }

    public static Result submitPost(String appName) {
        DynamicForm form = play.data.Form.form().bindFromRequest();
        String pass = form.data().get("pass");
        String text = form.data().get("message");
        String link = form.data().get("link");
        File file=null;

        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart picture = body.getFile("picture");
        if (picture != null) {
            String fileName = picture.getFilename();
            String contentType = picture.getContentType();
            file = picture.getFile();
        }



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
                String checkbox = form.data().get(identityId.userId);

                if(identityId.providerId.equals("facebook") && checkbox!=null && checkbox.equals("on")) {
                    try {
                        String appId=play.Play.application().configuration().getString("securesocial.facebook.clientId");
                        String appSecret=play.Play.application().configuration().getString("securesocial.facebook.clientSecret");

                        if(file==null) {
                            if(link!=null && text!=null){
                                new DefaultFacebookClient(appId + "|" + appSecret)
                                        .publish(identityId.userId + "/feed",
                                                FacebookType.class,
                                                Parameter.with("message", text),
                                                Parameter.with("link", link)
                                        );
                            }else if(link==null && text!=null){
                                new DefaultFacebookClient(appId + "|" + appSecret)
                                        .publish(identityId.userId + "/feed",
                                                FacebookType.class,
                                                Parameter.with("message", text)
                                        );
                            }else if(link!=null && text==null){
                                new DefaultFacebookClient(appId + "|" + appSecret)
                                        .publish(identityId.userId + "/feed",
                                                FacebookType.class,
                                                Parameter.with("link", link)
                                        );
                            }

                        }else {
                            if(text!=null){
                                InputStream stream = FileUtils.openInputStream(file);
                                new DefaultFacebookClient(identityId.accessToken).publish("me/photos", FacebookType.class,
                                        BinaryAttachment.with(file.getName(), stream),
                                        Parameter.with("message", text));
                            }else {
                                InputStream stream = FileUtils.openInputStream(file);
                                new DefaultFacebookClient(identityId.accessToken).publish("me/photos", FacebookType.class,
                                        BinaryAttachment.with(file.getName(), stream));
                            }

                        }
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

        public static Result adminPageDisplay() {
            return ok(views.html.adminPage.render());
    }
}
