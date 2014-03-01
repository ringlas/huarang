package controllers;

import play.*;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.Http.*;
import models.*;

/**
 * Created by vik_a_000 on 14-3-1.
 */
public class Secured extends Security.Authenticator {

    @Override
    public String getUsername(Context ctx) {
        return ctx.session().get("username");
    }

    @Override
    public Result onUnauthorized(Context ctx) {
        return redirect(routes.Application.login());
    }
}
