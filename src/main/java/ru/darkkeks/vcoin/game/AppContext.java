package ru.darkkeks.vcoin.game;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.client.actors.UserActor;
import com.zaxxer.hikari.HikariDataSource;
import ru.darkkeks.vcoin.game.api.VCoinApi;
import ru.darkkeks.vcoin.game.vk.FollowerManager;
import ru.darkkeks.vcoin.game.vk.MessageBatcher;

import java.util.concurrent.ScheduledExecutorService;

public class AppContext {

    private VCoinApi vCoinApi;
    private VkApiClient vk;
    private GroupActor actor;
    private UserActor topActor;
    private TransportClient transportClient;
    private ScheduledExecutorService executorService;
    private HikariDataSource dataSource;
    private MessageBatcher messageBatcher;
    private FollowerManager followerManager;

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

    public HikariDataSource getDataSource() {
        return dataSource;
    }

    public MessageBatcher getMessageBatcher() {
        return messageBatcher;
    }

    public FollowerManager getFollowerManager() {
        return followerManager;
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

    public UserActor getTopActor() {
        return topActor;
    }

    public void setTopActor(UserActor topActor) {
        this.topActor = topActor;
    }

    public void setTransportClient(TransportClient transportClient) {
        this.transportClient = transportClient;
    }

    public void setExecutorService(ScheduledExecutorService executorService) {
        this.executorService = executorService;
    }

    public void setDataSource(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setMessageBatcher(MessageBatcher messageBatcher) {
        this.messageBatcher = messageBatcher;
    }

    public void setFollowerManager(FollowerManager followerManager) {
        this.followerManager = followerManager;
    }
}
