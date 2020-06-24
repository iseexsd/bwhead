package xyz.ethanh.bwhead;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class BWHeadRender {

    private static double respawnTimer = 0;

    private static final Pattern HYPIXEL_PATTERN = Pattern.compile("^(?:(?:(?:.+\\.)?hypixel\\.net)|(?:209\\.222\\.115\\.\\d{1,3})|(?:99\\.198\\.123\\.[123]?\\d?))\\.?(?::\\d{1,5}\\.?)?$", 2);

    public static boolean isHypixel() {
        final FMLClientHandler instance = FMLClientHandler.instance();
        if (instance == null) {
            return false;
        }
        final Minecraft client = instance.getClient();
        if (client == null) {
            return false;
        }
        final ServerData currentServerData = client.getCurrentServerData();
        return currentServerData != null && HYPIXEL_PATTERN.matcher(currentServerData.serverIP).find();
    }

    private String getStatsFromName(String ign) {
        String stats = "N/A";
        for(int i = 0; i < BWHeadUtil.statsCache.size(); i++) {
            if(ign.equals(BWHeadUtil.statsCache.get(i).split(", ")[0])) {
                stats = BWHeadUtil.statsCache.get(i).split(", ")[1];
            }
        }
        return stats;
    }

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        if(!isHypixel()) return;
        String unformattedText = EnumChatFormatting.getTextWithoutFormattingCodes(event.message.getUnformattedText());
        if(unformattedText.startsWith("The game starts in 1 second!")) {
            ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
            service.schedule(new ThreadUpdateCache(), 5, TimeUnit.SECONDS);
        }
    }


    @SubscribeEvent
    public void render(RenderPlayerEvent.Pre event) {
        if(!isHypixel()) return;
        String name = event.entityPlayer.getDisplayNameString();
        String[] nameArr = name.split(" ");
        if(name.contains("[")) {
            name = nameArr[1];
        } else if(!name.contains("[") && name.contains("]")) {
            name = nameArr[0];
        }
        if(!event.entityPlayer.isSneaking() && (BWHeadUtil.statsCacheString.contains(name + "|") || BWHeadUtil.statsCacheString.contains("|" + event.entityPlayer.getDisplayNameString()))) {
            double offset = 0.6;
            Scoreboard scoreboard = event.entityPlayer.getWorldScoreboard();
            ScoreObjective scoreObjective = scoreboard.getObjectiveInDisplaySlot(2);

            if(scoreObjective != null) {
                offset *= 2;
            }

            renderName(event.renderer, getStatsFromName(event.entityPlayer.getDisplayNameString()).replace("B", " "), event.entityPlayer, event.x, event.y+offset, event.z);
        }
    }

    public void renderName(RendererLivingEntity renderer, String str, EntityPlayer entityIn, double x, double y, double z) {
        FontRenderer fontrenderer = renderer.getFontRendererFromRenderManager();
        float f = 1.6F;
        float f1 = 0.016666668F * f;
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)x + 0.0F, (float)y + entityIn.height + 0.5F, (float)z);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-renderer.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(renderer.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-f1, -f1, f1);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        int i = 0;

        int j = fontrenderer.getStringWidth(str) / 2;
        GlStateManager.disableTexture2D();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos((double)(-j - 1), (double)(-1 + i), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        worldrenderer.pos((double)(-j - 1), (double)(8 + i), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        worldrenderer.pos((double)(j + 1), (double)(8 + i), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        worldrenderer.pos((double)(j + 1), (double)(-1 + i), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        fontrenderer.drawString(str, -fontrenderer.getStringWidth(str) / 2, i, 553648127);
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        fontrenderer.drawString(str, -fontrenderer.getStringWidth(str) / 2, i, -1);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    @SubscribeEvent
    public void playerSpawn(EntityJoinWorldEvent event) {
        if(event.entity != Minecraft.getMinecraft().thePlayer) return;

        if(System.currentTimeMillis() - respawnTimer > 2000) respawnTimer = 0;

        if(respawnTimer == 0) {
            respawnTimer = System.currentTimeMillis();
            //the player has joined a world
            BWHeadUtil.statsCache = new ArrayList<String>();
        }

    }
}