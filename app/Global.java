import controllers.routes;
import play.GlobalSettings;
import play.api.mvc.EssentialFilter;
import play.filters.gzip.GzipFilter;
import play.libs.F.Promise;
import play.mvc.Controller;
import play.mvc.Http.RequestHeader;
import play.mvc.SimpleResult;

import java.net.URLDecoder;

import static play.mvc.Controller.ctx;
import static play.mvc.Controller.flash;

public class Global extends GlobalSettings {


	@Override
	public Promise<SimpleResult> onError(final RequestHeader arg0, final Throwable arg1) {

        if(arg0.path().equals("/authenticate/facebook")){
            flash("error", "You should accept the application");
            String app = URLDecoder.decode(ctx().session().get("original-url")).substring(1);
            return Promise.pure(Controller.redirect(routes.Application.appPage(app)));
        }
        return super.onError(arg0, arg1);
	}

    public <T extends EssentialFilter> Class<T>[] filters() {
        return new Class[]{GzipFilter.class};
    }
}
