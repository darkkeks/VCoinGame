package ru.darkkeks.vcoin.game.vk;

import com.vk.api.sdk.client.TransportClient;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Random;

public class VkUtil {

    private static Random random = new Random();

    public static int randomId() {
        return random.nextInt();
    }

    public static String shortenUrl(TransportClient client, String url) throws URISyntaxException, IOException {
        return client.get(new URIBuilder("https://clck.ru/--")
                .addParameters(Collections.singletonList(new BasicNameValuePair("url", url)))
                .build().toString()).getContent();
    }
}
