package luohuayu.ForgeHandshakeDebugger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.spacehq.mc.protocol.MinecraftProtocol;
//import org.spacehq.mc.protocol.data.game.entity.metadata.ItemStack;
import org.spacehq.mc.protocol.packet.ingame.client.ClientChatPacket;
import org.spacehq.mc.protocol.packet.ingame.client.ClientPluginMessagePacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import org.spacehq.packetlib.Client;
import org.spacehq.packetlib.event.session.ConnectedEvent;
import org.spacehq.packetlib.event.session.DisconnectedEvent;
import org.spacehq.packetlib.event.session.DisconnectingEvent;
import org.spacehq.packetlib.event.session.PacketReceivedEvent;
import org.spacehq.packetlib.event.session.PacketSentEvent;
import org.spacehq.packetlib.event.session.SessionListener;
import org.spacehq.packetlib.tcp.TcpSessionFactory;

import luohuayu.MCForgeProtocol.MCForge;
import network.JPPacket;

public class BotClient {
    public Map<String, String> modList = new HashMap<String, String>();
    public Client client;

    public void connect(String ip, int port, String username) {
        client = new Client(ip, port, new MinecraftProtocol(username), new TcpSessionFactory());
        new MCForge(client.getSession(), this.modList).init();
        client.getSession().addListener(new SessionListener() {
            public void packetReceived(PacketReceivedEvent e) {
//            	System.out.println("接收到包" + e.getPacket());
                if (e.getPacket() instanceof ServerJoinGamePacket) {
                    ServerJoinGamePacket packet = e.getPacket();
                    Utils.log("Client", "连接成功 EntityId:" + packet.getEntityId());
                } else if (e.getPacket() instanceof ServerPlayerPositionRotationPacket) {
                    ServerPlayerPositionRotationPacket packet = e.getPacket();
                    Utils.log("Client", "x:" + packet.getX() + ",y:" + packet.getY() + ",z:" + packet.getZ());
                }
            }

            public void packetSent(PacketSentEvent e) {
            }

            public void connected(ConnectedEvent e) {
            }

            public void disconnecting(DisconnectingEvent e) {
            }

            public void disconnected(DisconnectedEvent e) {
                String msg;
                if (e.getCause() != null) {
                    msg = e.getCause().getMessage();
                    e.getCause().printStackTrace();
                } else {
                    msg = e.getReason();
                }
                Utils.log("Client", "断开 - " + msg);
            }
        });
        client.getSession().connect(false);
    }

    public void chat(String msg) {
    	if(msg.contains("~")) {
//    		System.out.println("~~~");
//			try {
//		        FileInputStream fis = new FileInputStream(new File("nbt.dat"));
//		        ByteArrayOutputStream out = new ByteArrayOutputStream();
//		        byte[] buffer = new byte[fis.available()];
//		        fis.read(buffer);
//		        out.write(buffer);
//	            fis.close();
//	        	client.getSession().send(new ClientPluginMessagePacket("AlcoholMod", out.toByteArray()));
//	        	System.out.println("ok");
//			} catch (Exception e) {
//				e.printStackTrace();
//			}  
        	client.getSession().send(new ClientPluginMessagePacket("AlcoholMod", "12345".getBytes()));
    	}
        if (client == null)
            return;
        if (client.getSession().isConnected()) {
            client.getSession().send(new ClientChatPacket(msg));
//            client.getSession().send(new JPPacket(msg));
        } else {
            Utils.log("Client", "服务器未连接.");
        }
    }
}
