package com.marlon.apolo.tfinal2022;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.test.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SdkSuppress;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.Until;

import com.marlon.apolo.tfinal2022.puntoEntrada.view.MainActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/*Los @SdkSuppress(minSdkVersion = 18) annotation ensures that tests will
only run on devices with Android 4.3 (API level 18)
* or newer, as required by the UI Automator framework.*/
@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 18)
public class ActivityMainTest {

    private static final int LAUNCH_TIMEOUT = 5000;
    private static final String STRING_TO_BE_TYPED = "UiAutomator";
    private UiDevice mDevice;
    Context appContext;


    private static final String BASIC_SAMPLE_PACKAGE
            = "com.marlon.apolo.tfinal2022";
// Al eliminar la rule se puede iniciar desde la pantalla de inicio del dispositivo móvil
    //    @Rule
//    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    // Antes de cada test se corre este método
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
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setClassName(getInstrumentation().getTargetContext(), InformacionInicialActivity.class.getName());
        intent.setClassName(getInstrumentation().getTargetContext(), MainActivity.class.getName());
        getInstrumentation().startActivitySync(intent);

        // Wait for the app to appear
        mDevice.wait(Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)), LAUNCH_TIMEOUT);
    }

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Log.d("TAG", "useAppContext" + " : " + appContext.getPackageName());
        assertEquals(BASIC_SAMPLE_PACKAGE, appContext.getPackageName());
    }


//    @Test
//    public void useAppContext() throws Exception {
//        // Context of the app under test.
//        Context appContext = InstrumentationRegistry.getTargetContext();
//        assertEquals("com.marlon.apolo.tfinal2022" , appContext.getPackageName());
//    }
//@Test
//public void setUp() {
//    Instrumentation mInstrumentation = getInstrumentation();
//// We register our interest in the activity
//    Instrumentation.ActivityMonitor monitor = mInstrumentation.addMonitor(InformacionInicialActivity.class.getName(), null, false);
//// We launch it
//    Intent intent = new Intent(Intent.ACTION_MAIN);
//    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//    intent.setClassName(mInstrumentation.getTargetContext(), InformacionInicialActivity.class.getName());
//    mInstrumentation.startActivitySync(intent);
//
//    Activity currentActivity = getInstrumentation().waitForMonitor(monitor);
//    assertNotNull(currentActivity);
//// We register our interest in the next activity from the sequence in this use case
//    mInstrumentation.removeMonitor(monitor);
//    monitor = mInstrumentation.addMonitor(InformacionInicialActivity.class.getName(), null, false);
//}

    @Test
    public void activityLaunch() {
        Log.d("TAG", "activityLaunch" + " : " + appContext.getPackageName());

        onView(withId(R.id.textView1)).check(matches(isDisplayed()));
        onView(withId(R.id.imageViewLogo)).check(matches(isDisplayed()));
        onView(withId(R.id.textView2)).check(matches(isDisplayed()));
        onView(withId(R.id.textViewVersionCode)).check(matches(isDisplayed()));
        Log.d(ActivityMainTest.class.getSimpleName(), "Test sobre interfaz gráfica exitoso");

    }

    // More tests...


}