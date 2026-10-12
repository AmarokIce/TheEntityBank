package club.someoneice.manametal.entitybank.network.packet

import club.someoneice.manametal.entitybank.core.BankData
import club.someoneice.manametal.entitybank.network.IPacket
import club.someoneice.manametal.entitybank.network.NetworkHandler
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.network.PacketBuffer

object C2SRequestEntities: IPacket {
  override fun pack(buff: PacketBuffer) {
  }

  override fun handle(buff: PacketBuffer, player: EntityPlayerMP?) {
    player ?: return
    checkAndSend(player)
  }

  private fun checkAndSend(player: EntityPlayerMP) {
    val collection = BankData.getEntities(player.commandSenderName)
    if (collection.isEmpty()) {
      NetworkHandler.sendPacket(S2CEntities.INSTANCE, player)
      return
    }

    val list = collection.chunked(3)
    val size = list.size
    list.forEachIndexed { i, chunk ->
      NetworkHandler.sendPacket(S2CEntities(size, i, chunk), player)
    }
  }
}
