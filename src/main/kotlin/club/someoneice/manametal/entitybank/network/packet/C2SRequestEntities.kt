package club.someoneice.manametal.entitybank.network.packet

import club.someoneice.manametal.entitybank.EntityBank
import club.someoneice.manametal.entitybank.ThreadDataHandler
import club.someoneice.manametal.entitybank.network.IPacket
import club.someoneice.manametal.entitybank.network.NetworkHandler
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.PacketBuffer

object C2SRequestEntities: IPacket {
  override fun pack(buff: PacketBuffer) {
  }

  override fun handle(buff: PacketBuffer, player: EntityPlayerMP?) {
    player ?: return
    ThreadDataHandler.TASKS.add { checkAndSend(player) }
  }

  private fun checkAndSend(player: EntityPlayerMP) {
    val db = EntityBank.createDB()
    if (!db.exists(player.commandSenderName)) {
      NetworkHandler.sendPacket(S2CEntities.INSTANCE, player)
      return
    }

    val collection = db.get(player.commandSenderName, List::class.java).filterIsInstance<NBTTagCompound>()
    db.close()

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
