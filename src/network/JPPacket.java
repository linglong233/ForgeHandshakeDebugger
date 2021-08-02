package network;

import java.io.IOException;
import org.spacehq.packetlib.io.NetInput;
import org.spacehq.packetlib.io.NetOutput;
import org.spacehq.packetlib.packet.Packet;

public class JPPacket implements Packet {
	  private int data;
  
  private JPPacket() {}
  
  public JPPacket(int jp) {
    this.data = jp;
  }
  
  public int getjp() {
    return this.data;
  }
  
  public void read(NetInput in) throws IOException {
    this.data = in.readInt();
  }
  
  public void write(NetOutput out) throws IOException {
    out.writeInt(this.data);
  }
  
  public boolean isPriority() {
    return false;
  }
}
