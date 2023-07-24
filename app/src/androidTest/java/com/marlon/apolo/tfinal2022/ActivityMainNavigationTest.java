package com.marlon.apolo.tfinal2022;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;


import android.content.Context;
import android.content.Intent;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import com.marlon.apolo.tfinal2022.infoInicial.view.InformacionInicialActivity;
import com.marlon.apolo.tfinal2022.puntoEntrada.view.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ActivityMainNavigationTest {
    //    private static final String CALC_PACKAGE = "com.marlon.apolo.tfinal2022";
    private static final String CALC_PACKAGE = "com.marlon.apolo.tfinal2022.infoInicial.view";
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    private static final int LAUNCH_TIMEOUT = 5000;
    private static final String STRING_TO_BE_TYPED = "UiAutomator";
    private UiDevice mDevice;
    private static final String BASIC_SAMPLE_PACKAGE
            = "com.marlon.apolo.tfinal2022.puntoEntrada.view.MainActivity";

    @Before
    public void startMainActivityFromHomeScreen() {
        // Initialize UiDevice instance
        mDevice = UiDevice.getInstance(getInstrumentation());
        // Start from the home screen
        mDevice.pressHome();
        // Wait for launcher
        final String launcherPackage = mDevice.getLauncherPackageName();
        assertThat(launcherPackage, notNullValue());
        mDevice.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT);
        // Launch the app
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClassName(getInstrumentation().getTargetContext(), InformacionInicialActivity.class.getName());
//        intent.setClassName(getInstrumentation().getTargetContext(), MainActivity.class.getName());
        getInstrumentation().startActivitySync(intent);

//        final Intent intent = new Intent(context, InformacionInicialActivity.class);
//        // Clear out any previous instances
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(intent);
        // Wait for the app to appear
        mDevice.wait(Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)), LAUNCH_TIMEOUT);
    }


    //    @Before
//    public void startMainActivityFromHomeScreen() {
//        // Initialize UiDevice instance
//        mDevice = UiDevice
//                .getInstance(InstrumentationRegistry.getInstrumentation());
//        // Start from the home screen
//        mDevice.pressHome();
//        // Wait for launcher
//        final String launcherPackage =
//                mDevice.getLauncherPackageName();
//        assertThat(launcherPackage, notNullValue());
//        mDevice.wait(Until
//                        .hasObject(By.pkg(launcherPackage).depth(0)),
//                LAUNCH_TIMEOUT);
//        // Launch the app
//        Context context = InstrumentationRegistry.getInstrumentation().getContext();
////        final Intent intent = context.getPackageManager().getLaunchIntentForPackage(CALC_PACKAGE);
//        final Intent intent = new Intent(context, InformacionInicialActivity.class);
//        // Clear out any previous instances esota mal ajsja
//
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        context.startActivity(intent);
//        // Wait for the app to appear
//        mDevice.wait(Until
//                        .hasObject(By.pkg(CALC_PACKAGE).depth(0)),
//                LAUNCH_TIMEOUT);
//    }
    @Test
    public void activityLaunch() {
//        onView(withId(R.id.btnChangeSlide1)).perform(click());
        onView(withId(R.id.btnChangeSlide)).perform(click());
        onView(withId(R.id.btnChangeSlide)).perform(click());
        onView(withId(R.id.btnChangeSlide)).perform(click());
        onView(withId(R.id.btnLogin)).perform(click());
        mDevice.wait(Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)), LAUNCH_TIMEOUT);

//        onView(withId(R.id.textView1)).check(matches(isDisplayed()));
//
////        onView(withId(R.id.button_second)).perform(click());
//
//        onView(withId(R.id.imageViewLogo)).check(matches(isDisplayed()));
//        onView(withId(R.id.textView2)).check(matches(isDisplayed()));
//        onView(withId(R.id.textViewVersionCode)).check(matches(isDisplayed()));
    }


//    public void setUp() {
//        // Setup code ...
//        // Launch a simple calculator app.
//        Context context = getInstrumentation().getContext();
//        Intent intent = context.getPackageManager()
//                .getLaunchIntentForPackage(CALC_PACKAGE);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        // Clear out any previous instances.
//        context.startActivity(intent);
//        mDevice.wait(Until.hasObject(By.pkg(CALC_PACKAGE).depth(0)), LAUNCH_TIMEOUT);
//    }
//
//    public void testTwoPlusThreeEqualsFive() {
//        // Enter an equation: 2 + 3 = ?
//        try {
//            mDevice.findObject(new UiSelector()
//                    .packageName(CALC_PACKAGE).resourceId("two")).click();
//            mDevice.findObject(new UiSelector()
//                    .packageName(CALC_PACKAGE).resourceId("plus")).click();
//            mDevice.findObject(new UiSelector()
//                    .packageName(CALC_PACKAGE).resourceId("three")).click();
//            mDevice.findObject(new UiSelector()
//                    .packageName(CALC_PACKAGE).resourceId("equals")).click();
//        } catch (UiObjectNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        // Verify the result = 5
//        UiObject2 result = mDevice.findObject(By.res(CALC_PACKAGE, "result"));
//        assertEquals("5", result.getText());
//    }
}
