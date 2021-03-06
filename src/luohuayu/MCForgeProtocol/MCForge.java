package luohuayu.MCForgeProtocol;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.spacehq.mc.protocol.packet.ingame.client.ClientPluginMessagePacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerPluginMessagePacket;
import org.spacehq.packetlib.Session;
import org.spacehq.packetlib.event.session.ConnectedEvent;
import org.spacehq.packetlib.event.session.DisconnectedEvent;
import org.spacehq.packetlib.event.session.DisconnectingEvent;
import org.spacehq.packetlib.event.session.PacketReceivedEvent;
import org.spacehq.packetlib.event.session.PacketSentEvent;
import org.spacehq.packetlib.event.session.SessionListener;

import alcoholmod.Mathioks.SyncPlayerPropsMessage;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import luohuayu.ForgeHandshakeDebugger.Utils;
import net.minecraft.nbt.NBTTagCompound;


public class MCForge {
    private MCForgeHandShake handshake;

    public Map<String, String> modList;
    public Session session;

    public MCForge(Session session, Map<String, String> modList) {
        this.modList = modList;
        this.session = session;
        this.handshake = new MCForgeHandShake(this);
    }

    public void init() {
        this.session.addListener(new SessionListener() {
            public void packetReceived(PacketReceivedEvent e) {
                if (e.getPacket() instanceof ServerPluginMessagePacket) {
                    handle(e.getPacket());
                }
            }

            public void packetSent(PacketSentEvent e) {
            }

            public void connected(ConnectedEvent e) {
                modifyHost();
            }

            public void disconnecting(DisconnectingEvent e) {
            }

            public void disconnected(DisconnectedEvent e) {
            }
        });
    }

    public void handle(ServerPluginMessagePacket packet) {
        switch (packet.getChannel()) {
        case "AlcoholMod":
            this.handshake.handle(packet);

            ByteBuf buf = Unpooled.buffer(packet.getData().length);
            buf.writeBytes(packet.getData());


            this.session.send(new ClientPluginMessagePacket("AlcoholMod", packet.getData()));
        case "FML|HS":
            this.handshake.handle(packet);
            break;
        case "REGISTER":
            Utils.log("REGISTER(S->C): " + new String(packet.getData()));
            this.session.send(new ClientPluginMessagePacket("REGISTER", packet.getData()));
            break;
        case "MC|Brand":
            this.session.send(new ClientPluginMessagePacket("MC|Brand", "fml,forge".getBytes()));
            break;
        default:
            Utils.log("CustomPayload<" + packet.getChannel() + "> (S->C): " + new String(packet.getData()));
//            this.session.send(new ClientPluginMessagePacket(packet.getChannel(), packet.getData()));
            break;
        }
    }

    public void modifyHost() {
        try {
            Class<?> cls = this.session.getClass().getSuperclass();

            Field field = cls.getDeclaredField("host");
            field.setAccessible(true);

            field.set(this.session, this.session.getHost() + "\0FML\0");
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public static boolean isVersion1710() {
        try {
            Class<?> cls = Class.forName("org.spacehq.mc.protocol.ProtocolConstants");
            if (cls != null) {
                Field field = cls.getDeclaredField("PROTOCOL_VERSION");
                int protocol = field.getInt(cls.newInstance());
                return (protocol == 5);
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
}
