package ru.darkkeks.vcoin.game;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import ru.darkkeks.vcoin.game.api.VCoinApi;

import java.util.concurrent.ScheduledExecutorService;

public class AppContext {

    private VCoinApi vCoinApi;
    private VkApiClient vk;
    private GroupActor actor;
    private TransportClient transportClient;
    private ScheduledExecutorService executorService;

    public VCoinApi getVCoinApi() {
        return vCoinApi;
    }

    public VkApiClient getVk() {
        return vk;
    }

    public GroupActor getActor() {
        return actor;
    }

    public ScheduledExecutorService getExecutorService() {
        return executorService;
    }

    public TransportClient getTransportClient() {
        return transportClient;
    }

    public void setVCoinApi(VCoinApi vCoinApi) {
        this.vCoinApi = vCoinApi;
    }

    public void setVk(VkApiClient vk) {
        this.vk = vk;
    }

    public void setActor(GroupActor actor) {
        this.actor = actor;
    }

    public void setTransportClient(TransportClient transportClient) {
        this.transportClient = transportClient;
    }

    public void setExecutorService(ScheduledExecutorService executorService) {
        this.executorService = executorService;
    }
}
