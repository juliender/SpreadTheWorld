package controllers;

import com.restfb.BinaryAttachment;
import com.restfb.DefaultFacebookClient;
import com.restfb.Parameter;
import com.restfb.types.FacebookType;
import models.App;
import models.IdentityId;
import models.User;
import org.apache.commons.io.FileUtils;
import play.data.DynamicForm;
import play.mvc.Controller;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Created by julien on 18/04/14.
 */
public class Post extends Controller {

    public static HashMap<String,String> send(App app, String text, String link, File file, DynamicForm form) {
        HashMap<String, String> states = new HashMap<String, String>();

        for (User app_user : app.users) {
            for (IdentityId identityId : app_user.identityId) {

                String checkbox = form.data().get(identityId.userId);

                if (identityId.providerId.equals("facebook") && checkbox != null && checkbox.equals("on")) {
                    try {
                        String appId = play.Play.application().configuration().getString("securesocial.facebook.clientId");
                        String appSecret = play.Play.application().configuration().getString("securesocial.facebook.clientSecret");

                        if (file == null) {
                            if (link != null && text != null) {
                                new DefaultFacebookClient(appId + "|" + appSecret)
                                        .publish(identityId.userId + "/feed",
                                                FacebookType.class,
                                                Parameter.with("message", text),
                                                Parameter.with("link", link)
                                        );
                            } else if (link == null && text != null) {
                                new DefaultFacebookClient(appId + "|" + appSecret)
                                        .publish(identityId.userId + "/feed",
                                                FacebookType.class,
                                                Parameter.with("message", text)
                                        );
                            } else if (link != null && text == null) {
                                new DefaultFacebookClient(appId + "|" + appSecret)
                                        .publish(identityId.userId + "/feed",
                                                FacebookType.class,
                                                Parameter.with("link", link)
                                        );
                            }

                        } else {
                            if (text != null) {
                                InputStream stream = FileUtils.openInputStream(file);
                                new DefaultFacebookClient(identityId.accessToken).publish("me/photos", FacebookType.class,
                                        BinaryAttachment.with(file.getName(), stream),
                                        Parameter.with("message", text));
                            } else {
                                InputStream stream = FileUtils.openInputStream(file);
                                new DefaultFacebookClient(identityId.accessToken).publish("me/photos", FacebookType.class,
                                        BinaryAttachment.with(file.getName(), stream));
                            }

                        }
                        states.put(identityId.fullname, "Posté");
                    } catch (Exception e) {
                        states.put(identityId.fullname, e.toString());
                    }
                }
            }
        }
        return states;

    }
}
