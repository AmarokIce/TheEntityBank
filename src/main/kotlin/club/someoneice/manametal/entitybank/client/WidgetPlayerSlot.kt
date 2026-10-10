package club.someoneice.manametal.entitybank.client

import alexsocol.asjlib.mc
import club.someoneice.manametal.entitybank.client.gui.GuiBank
import club.someoneice.manametal.entitybank.network.NetworkHandler
import club.someoneice.manametal.entitybank.network.packet.C2SRemoveEntity
import club.someoneice.manametal.entitybank.network.packet.C2SSaveEntity
import club.someoneice.wolftail.api.IMouseEventListener
import club.someoneice.wolftail.api.IStyle
import club.someoneice.wolftail.api.ITooltip
import club.someoneice.wolftail.api.IWidget
import club.someoneice.wolftail.api.style.StyleAdapter
import club.someoneice.wolftail.util.UIPos
import club.someoneice.wolftail.util.UtilGui
import com.google.common.collect.ImmutableList
import net.minecraft.client.gui.Gui
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ChatComponentText
import net.minecraft.util.IChatComponent
import java.awt.image.BufferedImage

class WidgetPlayerSlot(index: Int, val player: EntityPlayer, val entity: WidgetEntitySlot, val parent: GuiBank):
  IWidget, IMouseEventListener, ITooltip {
  private val pos = UIPos((index + 1) % 4 * 40, (index + 1) / 4 * 40, 40, 40)
  private val entityAvatar: BufferedImage = WidgetEntitySlot.bakeEntityAvatar(player)

  override fun getStyle(): IStyle = StyleAdapter.INSTANCE
  override fun weightPos(): UIPos = this.pos

  override fun render(pGui: Gui, pMouseX: Int, pMouseY: Int, pGuiX: Int, pGuiY: Int) {
    UtilGui.drawImageWithoutResource(pGuiX + 2, pGuiY + 2, this.entityAvatar)
  }

  override fun onMouseClicked(pGui: Gui, pMouseX: Int, pMouseY: Int, pGuiX: Int, pGuiY: Int, pMouseButton: Int) {
    NetworkHandler.sendPacket(C2SRemoveEntity(this.entity.entity.uniqueID))
    val entityNBT = NBTTagCompound().also {
      this.entity.entity.writeEntityToNBT(it)
    }
    NetworkHandler.sendPacket(C2SSaveEntity(entityNBT, this.player.commandSenderName))

    parent.removeChild(this.entity)
    mc.displayGuiScreen(parent)
  }

  override fun getData(): ImmutableList<IChatComponent> {
    return ImmutableList.of(ChatComponentText(this.player.commandSenderName))
  }
}
