package ru.darkkeks.vcoin.game.vk;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.groups.responses.GetMembersResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.darkkeks.vcoin.game.AppContext;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class FollowerManager {

    private static final Logger logger = LoggerFactory.getLogger(FollowerManager.class);

    private static final int FETCH_PERIOD_SECONDS = 30;
    private static final int COUNT = 1000;

    private AppContext context;
    private Set<Integer> followers;

    public FollowerManager(AppContext context) {
        this.context = context;
        followers = new HashSet<>();
    }

    public void start() {
        context.getExecutorService().scheduleAtFixedRate(() -> {
            for(int offset = 0; ; offset += COUNT) {
                try {
                    GetMembersResponse result = context.getVk().groups().getMembers(context.getActor())
                            .count(COUNT)
                            .offset(offset)
                            .groupId(context.getActor().getId().toString())
                            .execute();
                    followers.addAll(result.getItems());

                    if(offset + COUNT > result.getCount()) {
                        break;
                    }
                } catch (ApiException | ClientException e) {
                    logger.error("Error fetching followers", e);
                }
            }
        }, 0, FETCH_PERIOD_SECONDS, TimeUnit.SECONDS);
    }

    public boolean isFollower(int usedId) {
        return followers.contains(usedId);
    }

}
