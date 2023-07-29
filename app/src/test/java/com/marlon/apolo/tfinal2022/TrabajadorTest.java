package com.marlon.apolo.tfinal2022;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.marlon.apolo.tfinal2022.model.Trabajador;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
//@SmallTest
public class TrabajadorTest {
    private Trabajador trabajador;
    // Set up the environment for testing
    @Before
    public void setUp() {
        trabajador = new Trabajador();
    }

    // test for simple addition
    @Test
    public void testData() {
        trabajador.setIdUsuario("asd35uhjma6u322");
        trabajador.setNombre("Marlon");
        trabajador.setApellido("Apolo");
        trabajador.setEmail("marlonapolo@gmail.com");
        trabajador.setCalificacion(2.0);
        trabajador.setEstadoRrcordP(false);

        assertThat(trabajador.getNombre(), is(equalTo("Marlon")));
        assertThat(trabajador.getApellido(), is(equalTo("Apolo")));
        assertThat(trabajador.getEmail(), is(equalTo("marlonapolo@gmail.com")));
        assertThat(trabajador.getCalificacion(), is(equalTo(2.0)));
        assertThat(trabajador.isEstadoRrcordP(), is(equalTo(false)));
    }
}
