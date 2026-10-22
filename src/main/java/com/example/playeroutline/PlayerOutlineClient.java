
package com.example.playeroutline;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class PlayerOutlineClient implements ClientModInitializer {
    private static final String TEAM_NAME = "player_outline_red_local";
    private final Set<UUID> highlighted = new HashSet<>();

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(this::tick);
    }

    private void tick(MinecraftClient client) {
        if (client.world == null || client.player == null) {
            clearHighlights(client);
            return;
        }

        Scoreboard scoreboard = client.world.getScoreboard();
        Team redTeam = scoreboard.getTeam(TEAM_NAME);
        if (redTeam == null) {
            redTeam = scoreboard.addTeam(TEAM_NAME);
            redTeam.setColor(Formatting.RED);
        }

        Set<UUID> visibleNow = new HashSet<>();
        Vec3d eye = client.player.getEyePos();

        for (PlayerEntity candidate : client.world.getPlayers()) {
            if (!(candidate instanceof AbstractClientPlayerEntity) || candidate == client.player || candidate.isRemoved()) {
                continue;
            }

            if (isVisible(client, eye, candidate)) {
                visibleNow.add(candidate.getUuid());
                candidate.setGlowing(true);
                scoreboard.addScoreHolderToTeam(candidate.getNameForScoreboard(), redTeam);
            }
        }

        for (PlayerEntity candidate : client.world.getPlayers()) {
            UUID id = candidate.getUuid();
            if (highlighted.contains(id) && !visibleNow.contains(id)) {
                candidate.setGlowing(false);
                scoreboard.removeScoreHolderFromTeam(candidate.getNameForScoreboard(), redTeam);
            }
        }

        highlighted.clear();
        highlighted.addAll(visibleNow);
    }

    private boolean isVisible(MinecraftClient client, Vec3d eye, PlayerEntity target) {
        Vec3d[] points = new Vec3d[] {
                target.getEyePos(),
                target.getPos().add(0.0, target.getHeight() * 0.55, 0.0),
                target.getPos().add(0.0, target.getHeight() * 0.2, 0.0)
        };

        for (Vec3d point : points) {
            HitResult hit = client.world.raycast(new RaycastContext(
                    eye, point,
                    RaycastContext.ShapeType.COLLIDER,
                    RaycastContext.FluidHandling.NONE,
                    client.player
            ));
            if (hit.getType() == HitResult.Type.MISS) {
                return true;
            }
        }
        return false;
    }

    private void clearHighlights(MinecraftClient client) {
        if (client.world == null) {
            highlighted.clear();
            return;
        }
        Scoreboard scoreboard = client.world.getScoreboard();
        Team redTeam = scoreboard.getTeam(TEAM_NAME);
        if (redTeam != null) {
            for (PlayerEntity player : client.world.getPlayers()) {
                if (highlighted.contains(player.getUuid())) {
                    player.setGlowing(false);
                    scoreboard.removeScoreHolderFromTeam(player.getNameForScoreboard(), redTeam);
                }
            }
        }
        highlighted.clear();
    }
}
