package xyz.ethanh.bwhead;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.EnumChatFormatting;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

public class BWHeadUtil {
    public static ArrayList<String> statsCache = new ArrayList<String>();
    public static String statsCacheString = "";

    private static String apiKey = BWHead.getAPIKey();

    public static String getPage(String urlAsString) {
        //Instantiating the URL class
        URL url = null;
        try {
            url = new URL(urlAsString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        //Retrieving the contents of the specified page
        Scanner sc = null;
        try {
            sc = new Scanner(url.openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Instantiating the StringBuffer class to hold the result
        StringBuffer sb = new StringBuffer();
        while(sc.hasNext()) {
            sb.append(sc.next());
            //System.out.println(sc.next());
        }
        //Retrieving the String from the String Buffer object
        String result = sb.toString();
        //Removing the HTML tags
        result = result.replaceAll("<[^>]*>", "");
        return result;
    }

    public static String arrayToString(ArrayList<String> arrayList) {
        StringBuffer sb = new StringBuffer();

        for (String s : arrayList) {
            sb.append(s);
            sb.append("|");
        }
        String str = sb.toString();
        return str;
    }

    public static ArrayList<String> getPlayers() {
        Minecraft mc = Minecraft.getMinecraft();

        Collection<NetworkPlayerInfo> playerInfo = mc.getNetHandler().getPlayerInfoMap();
        ArrayList<String> players = new ArrayList<String>();

        for(NetworkPlayerInfo player : playerInfo) {
            String playerName = mc.ingameGUI.getTabList().getPlayerName(player);
            playerName = EnumChatFormatting.getTextWithoutFormattingCodes(playerName);
            if(playerName.contains(" ")) {players.add(playerName.split(" ")[1]);}
            else players.add(playerName);
        }

        return players;
    }

    public static void updateCache() {
        ArrayList<String> playerList = getPlayers();
        statsCache = new ArrayList<String>();
        for(String ign: playerList) {
//            System.out.println(ign);
//            System.out.println(getPage("http://157.245.90.182/?" + ign + "&" + apiKey));
            statsCache.add(ign + ", " + getPage("http://157.245.90.182/?" + ign + "&" + apiKey));
        }
        statsCacheString = arrayToString(statsCache);
        System.out.println("Updated cache: " + statsCache);
    }
}
