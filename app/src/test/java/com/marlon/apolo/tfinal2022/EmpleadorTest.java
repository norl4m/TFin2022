package com.marlon.apolo.tfinal2022;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.Trabajador;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
//@SmallTest
public class EmpleadorTest {
    private Empleador empleador;
    // Set up the environment for testing
    @Before
    public void setUp() {
        empleador = new Empleador();
    }

    // test for simple addition
    @Test
    public void testData() {
        empleador.setIdUsuario("-asd35uhjma6u322");
        empleador.setNombre("Kevin");
        empleador.setApellido("Jaramillo");
        empleador.setEmail("kevinjaramillo@gmail.com");
        empleador.setPassword("f5488346tng80qhm");
        assertThat(empleador.getIdUsuario(), is(equalTo("-asd35uhjma6u322")));
        assertThat(empleador.getNombre(), is(equalTo("Kevin")));
        assertThat(empleador.getApellido(), is(equalTo("Jaramillo")));
        assertThat(empleador.getEmail(), is(equalTo("kevinjaramillo@gmail.com")));
        assertThat(empleador.getPassword(), is(equalTo("f5488346tng80qhm")));
    }

}
