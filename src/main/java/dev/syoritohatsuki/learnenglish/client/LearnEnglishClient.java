package dev.syoritohatsuki.learnenglish.client;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;

public class LearnEnglishClient implements ClientModInitializer {

    public static final String MOD_ID = "learn-english";
    public static final Logger LOGGER = LogUtils.getLogger();

    public void onInitializeClient() {
        LOGGER.info("{} initialized with mod-id {}", getClass().getSimpleName(), MOD_ID);
    }
}