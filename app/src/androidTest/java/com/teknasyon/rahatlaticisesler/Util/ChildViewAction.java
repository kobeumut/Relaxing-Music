package com.teknasyon.rahatlaticisesler.Util;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.view.View;

import org.hamcrest.Matcher;

/**
 * Created by Umut ADALI on 25.06.2018.
 */
public class ChildViewAction {

    public static ViewAction clickChildViewWithId(
            final int id) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return null;
            }

            @Override
            public String getDescription() {
                return
                        "Click on a child view  with specified id.";
            }

            @Override
            public void perform(UiController uiController,
                                View view) {
                View v = view.findViewById(id);
                v.performClick();
            }
        };
    }
}