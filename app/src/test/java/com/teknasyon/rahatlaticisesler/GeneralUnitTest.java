package com.teknasyon.rahatlaticisesler;

import com.teknasyon.rahatlaticisesler.Controller.MusicActivity;
import com.teknasyon.rahatlaticisesler.Model.Music;
import com.teknasyon.rahatlaticisesler.Network.RetrofitClient;
import com.teknasyon.rahatlaticisesler.Network.Service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.LinkedList;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.mock.MockRetrofit;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@Config(constants = BuildConfig.class, sdk = 21,
        manifest = "app/src/main/AndroidManifest.xml")


public class GeneralUnitTest {
    private MusicActivity musicActivity;

    @Mock
    private Service mockRetrofitApiImpl;

    private Retrofit retrofit = RetrofitClient.getClient();
    @Captor
    private ArgumentCaptor<Call<ArrayList<Music>>> callbackArgumentCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);


        // Then we need to swap the retrofit api impl. with a mock one
        // I usually store my Retrofit api impl as a static singleton in class RestClient, hence:

        RetrofitClient.getClient();

    }

    @Test
    public void networkBehaviorNullThrows() {
        MockRetrofit.Builder builder = new MockRetrofit.Builder(RetrofitClient.getClient());
        try {
            builder.networkBehavior(null);
            fail();
        } catch (NullPointerException e) {
           assertTrue(e.getMessage().contains("behavior == null"));
        }
    }
    @Test
    public void checkRestClient() throws Exception {
        Service service = RetrofitClient.getClient().create(Service.class);
        Response<ArrayList<Music>> response = service.musics().execute();
        ArrayList<Music> authResponse = response.body();
        assertTrue(response.isSuccessful() && !authResponse.isEmpty());

    }
    @Test public void retrofitPropagated() {
        MockRetrofit mockRetrofit = new MockRetrofit.Builder(retrofit).build();
        assertEquals(mockRetrofit.retrofit(),retrofit);
    }
    @Test public void networkBehaviorDefault() {
        MockRetrofit mockRetrofit = new MockRetrofit.Builder(retrofit).build();
        assertNotNull(mockRetrofit.networkBehavior());
    }
    @Test
    public void addition_isCorrect() {
        LinkedList mockedList = mock(LinkedList.class);

// stubbing appears before the actual execution
        when(mockedList.get(0)).thenReturn("first2");

// the following prints "first"
        System.out.println(mockedList.get(0));

// the following prints "null" because get(999) was not stubbed
        System.out.println(mockedList.get(999));
    }
}