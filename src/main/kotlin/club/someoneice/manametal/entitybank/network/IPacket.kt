package club.someoneice.manametal.entitybank.network

import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.network.PacketBuffer

interface IPacket {
  fun pack(buff: PacketBuffer)
  fun handle(buff: PacketBuffer, player: EntityPlayerMP?)
}
