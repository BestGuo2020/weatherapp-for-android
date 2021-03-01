package top.bestguo.weatherapp;

import android.util.Log;

import com.google.gson.Gson;

import org.junit.Test;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 4);
    }

    @Test
    public void testDemo(){
        System.out.println("jdfijfisodjfidjfi");
    }

    @Test
    public void testOkHttp() throws IOException {
        OkHttpClient httpClient = new OkHttpClient();

        Request request = new Request.Builder().url("http://jisutqybmf.market" +
                ".alicloudapi.com/weather/query?city=%E5%AE%89%E9%A1%BA")
                .addHeader("Authorization", "APPCODE 1303692fd97f4e848b1334924fc7af98")
                .addHeader("Content-Type", "application/json; charset=utf-8").build();

        Call call = httpClient.newCall(request);
        Response response = call.execute();

        Gson gson = new Gson();
        Map map = gson.fromJson(response.body().string(), Map.class);
        System.out.println(map);
    }
}