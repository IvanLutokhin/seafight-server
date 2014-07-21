package seafight.communication.network.protocol;

public interface ISendablePacket extends IPacket {
	void writeImpl();
}