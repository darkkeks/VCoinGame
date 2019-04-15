package ru.darkkeks.vcoin.game.vk.keyboard;

import com.google.gson.annotations.SerializedName;

public enum ButtonType {
    @SerializedName("positive")
    POSITIVE,

    @SerializedName("negative")
    NEGATIVE,

    @SerializedName("primary")
    PRIMARY,

    @SerializedName("default")
    DEFAULT;
}
