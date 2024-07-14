package com.beatcraft.beatmap.data;

import com.beatcraft.beatmap.Difficulty;
import com.google.gson.JsonObject;

public abstract class GameplayObject extends BeatmapObject {
    private float njs = 20;
    private float offset = 0;
    private int x = 0;
    private int y = 0;

    @Override
    public GameplayObject loadV2(JsonObject json, Difficulty difficulty) {
        super.loadV2(json, difficulty);

        x = json.get("_lineIndex").getAsInt();
        y = json.get("_lineLayer").getAsInt();

        if (json.has("_customData")) {
            JsonObject customData = json.get("_customData").getAsJsonObject();

            if (customData.has("_noteJumpStartBeatOffset")) {
                offset = customData.get("_noteJumpStartBeatOffset").getAsFloat();
            }
            else {
                offset = difficulty.getSetDifficulty().getOffset();
            }
            if (customData.has("_noteJumpMovementSpeed")) {
                njs = customData.get("_noteJumpMovementSpeed").getAsFloat();
            }
            else {
                njs = difficulty.getSetDifficulty().getNjs();
            }
        }

        return this;
    }

    @Override
    public GameplayObject loadV3(JsonObject json, Difficulty difficulty) {
        super.loadV3(json, difficulty);

        x = json.get("x").getAsInt();
        y = json.get("y").getAsInt();

        if (json.has("customData")) {
            JsonObject customData = json.get("customData").getAsJsonObject();

            if (customData.has("noteJumpStartBeatOffset")) {
                offset = customData.get("noteJumpStartBeatOffset").getAsFloat();
            }
            else {
                offset = difficulty.getSetDifficulty().getOffset();
            }
            if (customData.has("noteJumpMovementSpeed")) {
                njs = customData.get("noteJumpMovementSpeed").getAsFloat();
            }
            else {
                njs = difficulty.getSetDifficulty().getNjs();
            }
        }

        return this;
    }

    @Override
    public BeatmapObject loadV4(JsonObject objectJson, JsonObject lutJson, Difficulty difficulty) {
        super.loadV4(objectJson, lutJson, difficulty);

        x = lutJson.get("x").getAsInt();
        y = lutJson.get("y").getAsInt();

        if (lutJson.has("customData")) {
            JsonObject customData = lutJson.get("customData").getAsJsonObject();

            if (customData.has("noteJumpStartBeatOffset")) {
                offset = customData.get("noteJumpStartBeatOffset").getAsFloat();
            }
            else {
                offset = difficulty.getSetDifficulty().getOffset();
            }
            if (customData.has("noteJumpMovementSpeed")) {
                njs = customData.get("noteJumpMovementSpeed").getAsFloat();
            }
            else {
                njs = difficulty.getSetDifficulty().getNjs();
            }
        }

        return this;
    }

    public float getNjs() {
        return njs;
    }

    public float getOffset() {
        return offset;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
