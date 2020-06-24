package xyz.ethanh.bwhead.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import xyz.ethanh.bwhead.ThreadUpdateCache;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LoadStats extends CommandBase {

    @Override
    public String getCommandName() {
        return "loadstats";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "Loads stats manually";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("[BWHead] Loading stats..."));
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.schedule(new ThreadUpdateCache(), 0, TimeUnit.SECONDS);
    }
}