package luohuayu.ForgeHandshakeDebugger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import luohuayu.MCForgeProtocol.MCForgeInject;
import luohuayu.MCForgeProtocol.MCForgeMOTD;

public class Main {
    public static void main(String[] args) {
        ASMInject.inject();
        MCForgeInject.inject();

        BotClient client = new BotClient();
//        client.modList.put("ic2", "2.8.101-ex112");
//        client.connect("172.20.16.197", 31000, "Luohuayu");
//        client.connect("127.0.0.1", 25565, "Luohuayu");
        MCForgeMOTD motd = new MCForgeMOTD();
        client.modList = motd.pingGetModsList("127.0.0.1", 25565, 1710);
        client.connect("127.0.0.1", 25565, "test");

        input(client);
    }

    public static void input(BotClient client) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            String msg = sc.nextLine();
            client.chat(msg);
        }
    }
}
