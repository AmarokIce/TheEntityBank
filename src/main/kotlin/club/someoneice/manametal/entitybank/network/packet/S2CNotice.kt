package club.someoneice.manametal.entitybank.network.packet

import club.someoneice.manametal.entitybank.network.IPacket
import club.someoneice.wolftail.api.constant.DefaultStyleToast
import club.someoneice.wolftail.ui.core.Toasts
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.init.Items
import net.minecraft.network.PacketBuffer

object S2CNotice: IPacket {
  override fun pack(buff: PacketBuffer) {
  }

  override fun handle(buff: PacketBuffer, player: EntityPlayerMP?) {
    val toast = Toasts.create(
      pTitle = I18n.format("entitybank.newentity.title"),
      pText = I18n.format("entitybank.newentity.text"),
      pStyle = DefaultStyleToast.TOAST_DARK,
      pIconItem = Items.spawn_egg
    )
    Toasts.addToast(toast)
  }

}
