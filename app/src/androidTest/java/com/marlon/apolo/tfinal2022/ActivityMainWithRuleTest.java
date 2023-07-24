package com.marlon.apolo.tfinal2022;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import androidx.test.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.Until;

import com.marlon.apolo.tfinal2022.infoInicial.view.InformacionInicialActivity;
import com.marlon.apolo.tfinal2022.puntoEntrada.view.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;

@RunWith(AndroidJUnit4.class)
public class ActivityMainWithRuleTest {

    private static final String BASIC_SAMPLE_PACKAGE
            = "com.marlon.apolo.tfinal2022";
    // Al eliminar la rule se puede iniciar desde la pantalla de inicio del dispositivo móvil
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals(BASIC_SAMPLE_PACKAGE, appContext.getPackageName());
    }

    @Test
    public void activityLaunch() {
        onView(withId(R.id.textView1)).check(matches(isDisplayed()));
        onView(withId(R.id.imageViewLogo)).check(matches(isDisplayed()));
        onView(withId(R.id.textView2)).check(matches(isDisplayed()));
        onView(withId(R.id.textViewVersionCode)).check(matches(isDisplayed()));
    }

    // More tests...


}