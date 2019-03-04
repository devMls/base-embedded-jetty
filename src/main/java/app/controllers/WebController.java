package app.controllers;

import util.ControllerGenerico;

public class WebController extends ControllerGenerico {

    public void index() {

        respondJson(new String[]{"ola accede al home", "ola"});

    }
}
