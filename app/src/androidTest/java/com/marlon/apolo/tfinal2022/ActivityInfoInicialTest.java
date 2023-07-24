package com.marlon.apolo.tfinal2022;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

import android.content.Context;
import android.content.Intent;

import androidx.test.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.filters.SdkSuppress;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.Until;

import com.marlon.apolo.tfinal2022.infoInicial.view.InformacionInicialActivity;
import com.marlon.apolo.tfinal2022.puntoEntrada.view.MainActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 18)
@LargeTest
public class ActivityInfoInicialTest {
    private static final int LAUNCH_TIMEOUT = 5000;
    private static final String STRING_TO_BE_TYPED = "UiAutomator";
    private UiDevice mDevice;
    Context appContext;


    private static final String BASIC_SAMPLE_PACKAGE
            = "com.marlon.apolo.tfinal2022";

    @Before
    public void startMainActivityFromHomeScreen() {
        appContext = InstrumentationRegistry.getTargetContext();
        // Initialize UiDevice instance
        mDevice = UiDevice.getInstance(getInstrumentation());
        // Start from the home screen
        mDevice.pressHome();
        // Wait for launcher
        final String launcherPackage = mDevice.getLauncherPackageName();
        assertThat(launcherPackage, notNullValue());
        mDevice.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT);
        // Launch the app
//        Intent intent = new Intent(Intent.ACTION_MAIN);
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setClassName(getInstrumentation().getTargetContext(), InformacionInicialActivity.class.getName());
        intent.setClassName(getInstrumentation().getTargetContext(), MainActivity.class.getName());
        getInstrumentation().startActivitySync(intent);

        // Wait for the app to appear
        mDevice.wait(Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)), LAUNCH_TIMEOUT);
    }

    @Test
    public void activityLaunch() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClassName(getInstrumentation().getTargetContext(), InformacionInicialActivity.class.getName());
        getInstrumentation().startActivitySync(intent);

        onView(withId(R.id.btnChangeSlide)).perform(click());
        onView(withId(R.id.btnChangeSlide)).perform(click());
        onView(withId(R.id.btnChangeSlide)).perform(click());

        onView(withId(R.id.btnChangeSlide1)).perform(click());
        onView(withId(R.id.btnChangeSlide1)).perform(click());
        onView(withId(R.id.btnChangeSlide1)).perform(click());

        onView(withId(R.id.btnChangeSlide)).perform(click());
        onView(withId(R.id.btnChangeSlide)).perform(click());
        onView(withId(R.id.btnChangeSlide)).perform(click());

        onView(withId(R.id.btnLogin)).perform(click());
        // Wait for the app to appear
    }
}
