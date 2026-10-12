package club.someoneice.manametal.entitybank.network

import club.someoneice.manametal.entitybank.core.EntityBank
import club.someoneice.manametal.entitybank.network.packet.*
import com.google.common.collect.HashBiMap
import com.google.common.collect.Maps
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent
import cpw.mods.fml.common.network.internal.FMLProxyPacket
import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import io.netty.buffer.Unpooled
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.network.NetHandlerPlayServer
import net.minecraft.network.PacketBuffer


object NetworkHandler {
  val PACKET_MAP: HashBiMap<Int, Class<IPacket>> = HashBiMap.create()
  val CLASS_INSTANCE: HashMap<Class<IPacket>, IPacket> = Maps.newHashMap()

  init {
    this.registerPacket(C2SRequestEntities)
    this.registerPacket(C2SSaveEntity.INSTANCE)
    this.registerPacket(C2SRemoveEntity.INSTANCE)

    this.registerPacket(S2CEntities.INSTANCE)
    this.registerPacket(S2CNotice)
  }

  fun registerPacket(packet: IPacket) {
    PACKET_MAP[PACKET_MAP.size] = packet.javaClass
    CLASS_INSTANCE[packet.javaClass] = packet
  }

  fun sendPacket(packet: IPacket, player: EntityPlayerMP? = null) {
    val buffer = PacketBuffer(Unpooled.buffer())
    buffer.writeByte(PACKET_MAP.inverse()[packet.javaClass] ?: return)
    packet.pack(buffer)

    val pack = FMLProxyPacket(buffer, EntityBank.MODID)
    player?.let { EntityBank.NETWORK.sendTo(pack, player) } ?: EntityBank.NETWORK.sendToServer(pack)
  }

  @SubscribeEvent
  fun onServerPacket(event: ServerCustomPacketEvent) {
    val packet = event.packet

    val buf = PacketBuffer(packet.payload())
    val player: EntityPlayerMP = (event.handler as NetHandlerPlayServer).playerEntity

    val packetId = buf.readByte().toInt()
    val packClass = PACKET_MAP[packetId] ?: return
    CLASS_INSTANCE[packClass]!!.handle(buf, player)
  }

  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  fun onClientPacket(event: ClientCustomPacketEvent) {
    val packet = event.packet
    val buf = PacketBuffer(packet.payload())

    val packetId = buf.readByte().toInt()
    val packClass = PACKET_MAP[packetId] ?: return
    CLASS_INSTANCE[packClass]!!.handle(buf, null)
  }
}
