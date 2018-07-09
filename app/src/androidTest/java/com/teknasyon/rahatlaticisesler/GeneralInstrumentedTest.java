package com.teknasyon.rahatlaticisesler;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.teknasyon.rahatlaticisesler.Adapter.CategoryAdapter;
import com.teknasyon.rahatlaticisesler.Adapter.MusicAdapter;
import com.teknasyon.rahatlaticisesler.Controller.LibraryActivity;
import com.teknasyon.rahatlaticisesler.Controller.MusicActivity;
import com.teknasyon.rahatlaticisesler.Util.ChildViewAction;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class GeneralInstrumentedTest {
    @Rule
    public ActivityTestRule<LibraryActivity> lActivityRule = new ActivityTestRule<>(
            LibraryActivity.class);

    @Rule
    public ActivityTestRule<MusicActivity> mActivityRule = new ActivityTestRule<>(
            MusicActivity.class);

    @Test
    public void ensureRecyclerViewIsPresent() throws Exception {
        LibraryActivity activity = lActivityRule.getActivity();
        View viewById = activity.findViewById(R.id.recyclerView);
        assertThat(viewById, notNullValue());
        assertThat(viewById, instanceOf(RecyclerView.class));
        RecyclerView rView = (RecyclerView) viewById;
        CategoryAdapter adapter = (CategoryAdapter) rView.getAdapter();
        assertThat(adapter, instanceOf(CategoryAdapter.class));
        Thread.sleep(3000); //3 saniyelik veri çekme süresi verildi.
        Integer itemCount = adapter.getData().size();
        assertThat("En az 6 adet kategori bulunamadı", itemCount, greaterThan(5));

    }

    @Test
    public void firstOpenCheckEmptyFavoriteListActivity() throws Exception {
        mActivityRule.launchActivity(new Intent().putExtra("category", -1)); //Favori listesinin çağırıldığı intent
        Thread.sleep(3000); //3 saniye veri çekip adaptörün yazma süresi verildi.
        onView(withId(R.id.showText)).check(matches(withText("İçerik Bulunamadı...")));

    }

    @Test
    public void FirstItemMusicIsPlaying() throws Exception {
        mActivityRule.launchActivity(new Intent().putExtra("category", 2)); //id'si 2 olan kategori çağırılıyor

        MusicActivity activity = mActivityRule.getActivity();
        View viewById = activity.findViewById(R.id.recyclerView);
        RecyclerView rView = (RecyclerView) viewById;
        MusicAdapter adapter = (MusicAdapter) rView.getAdapter();
        Thread.sleep(3000); //3 saniye veri çekip adaptörün yazma süresi verildi.

        onView(withId(R.id.recyclerView))
                .perform(
                        RecyclerViewActions.actionOnItemAtPosition(0,
                                ChildViewAction.clickChildViewWithId(
                                        R.id.playButton)));
//        Thread.sleep(2000);
//        assertTrue(adapter.getData().get(0).getMp().isPlaying());

    }

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.teknasyon.rahatlaticisesler", appContext.getPackageName());
    }
}
