package com.example.playeroutline;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.util.hit.HitResult;

public final class PlayerOutlineClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            MinecraftClient client = MinecraftClient.getInstance();

            if (client.world == null || client.player == null) return;

            MatrixStack matrices = context.matrixStack();
            VertexConsumerProvider consumers = context.consumers();

            if (matrices == null || consumers == null) return;

            Vec3d camera = context.camera().getPos();

            for (PlayerEntity target : client.world.getPlayers()) {
                if (target == client.player
                        || target.isRemoved()
                        || target.isInvisible()
                        || !canSee(client, target)) {
                    continue;
                }

                Box box = target.getBoundingBox().offset(
                        -camera.x, -camera.y, -camera.z
                );

                VertexConsumer lines = consumers.getBuffer(
                        RenderLayer.getLines()
                );

                drawBox(matrices, lines, box);
            }
        });
    }

    private static boolean canSee(
            MinecraftClient client, PlayerEntity target) {
        Vec3d start = client.player.getEyePos();
        Vec3d end = target.getBoundingBox().getCenter();

        return client.world.raycast(new RaycastContext(
                start,
                end,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                client.player
        )).getType() == HitResult.Type.MISS;
    }

    private static void drawBox(
            MatrixStack matrices, VertexConsumer vertexConsumer, Box box) {
        WorldRenderer.drawBox(
                matrices,
                vertexConsumer,
                box,
                1.0f, 0.0f, 0.0f, 1.0f
        );
    }
}
