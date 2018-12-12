package edu.unh.cs.cs619.bulletzone;

import android.content.Context;
import android.test.suitebuilder.annotation.LargeTest;

import org.androidannotations.api.view.OnViewChangedListener;
import org.junit.Test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
//import androidx.test.runner;



@RunWith(AndroidJUnit4.class)
@LargeTest


public class ClientActivityTest {

    @Rule
    public ActivityTestRule<ClientActivity> mActivityRule
            = new ActivityTestRule<>(ClientActivity.class);


    @Test
    public void onButtonMove() {

    }

    @Test
    public void onButtonEject() {
    }

    @Test
    public void onButtonEjectPower() {
    }

    @Test
    public void onButtonTurn() {
    }

    @Test
    public void onButtonFire() {
    }
}