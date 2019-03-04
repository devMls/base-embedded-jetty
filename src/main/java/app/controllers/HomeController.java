package app.controllers;

import org.apache.shiro.subject.Subject;
import util.ControllerGenerico;
import org.adrianwalker.multilinestring.Multiline;

public class HomeController extends ControllerGenerico {

    /**
     <p>
     Hello<br/>
     Multiline<br/>
     World<br/>
     </p>
     */
    @Multiline
    private String stringEjemploMultilinea;

    public void index() {

        logInfo(stringEjemploMultilinea);

        Subject session = sessionShiro();

    }
}
