/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.controllers;

import org.apache.shiro.subject.Subject;
import static org.easymock.EasyMock.createNiceMock;
import org.junit.Before;
import org.junit.Test;

/**

 @author mlarr
 */
public class HomeControllerSpec extends ControllerSpecWithShiro {

    @Before
    public void beforeClass() {

        Subject subjectUnderTest = createNiceMock(Subject.class);

        setSubject(subjectUnderTest);

    }

    @Test
    public void shouldSendGetToIndex() {

        request().get("index");

        a("hola").shouldBeEqual("hola");
    }

}
