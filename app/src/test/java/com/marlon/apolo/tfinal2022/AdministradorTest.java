package com.marlon.apolo.tfinal2022;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import android.app.Activity;

import com.marlon.apolo.tfinal2022.model.Administrador;
import com.marlon.apolo.tfinal2022.model.Empleador;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
//@SmallTest
public class AdministradorTest {
    private Administrador administrador;
    // Set up the environment for testing
    @Before
    public void setUp() {
        administrador = new Administrador();
    }

    // test for simple addition
    @Test
    public void testData() {
        administrador.setIdUsuario("-asd35udoboa08760pl");
        administrador.setNombre("Marcia");
        administrador.setApellido("Quishpe");
        administrador.setEmail("marci4qshp@gmail.com");
        administrador.setPassword("f5488346tng80qhm");

        assertThat(administrador.getIdUsuario(), is(equalTo("-asd35udoboa08760pl")));
        assertThat(administrador.getNombre(), is(equalTo("Marcia")));
        assertThat(administrador.getApellido(), is(equalTo("Quishpe")));
        assertThat(administrador.getEmail(), is(equalTo("marci4qshp@gmail.com")));
        assertThat(administrador.getPassword(), is(equalTo("f5488346tng80qhm")));
    }

    // test for simple addition
    @Test
    public void testRegFirebase() {
        administrador.setIdUsuario("-asd35udoboa08760pl");
        administrador.setNombre("Marcia");
        administrador.setApellido("Quishpe");
        administrador.setEmail("marci4qshp12@gmail.com");
        administrador.setPassword("f5488346tng80qhm");

        assertThat(administrador.getIdUsuario(), is(equalTo("-asd35udoboa08760pl")));
        assertThat(administrador.getNombre(), is(equalTo("Marcia")));
        assertThat(administrador.getApellido(), is(equalTo("Quishpe")));
        assertThat(administrador.getEmail(), is(equalTo("marci4qshp12@gmail.com")));
        assertThat(administrador.getPassword(), is(equalTo("f5488346tng80qhm")));

        administrador.registrarseEnFirebase(new Activity());
    }

}
