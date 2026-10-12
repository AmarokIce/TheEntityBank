package club.someoneice.manametal.entitybank.client.gui

import club.someoneice.manametal.entitybank.client.StyleBackground
import club.someoneice.manametal.entitybank.client.WidgetEntitySlot
import club.someoneice.manametal.entitybank.network.NetworkHandler
import club.someoneice.manametal.entitybank.network.packet.C2SRemoveEntity
import club.someoneice.wolftail.api.ITooltip
import club.someoneice.wolftail.api.style.IStyleUI
import club.someoneice.wolftail.ui.core.WScreen
import club.someoneice.wolftail.ui.widget.WButton
import club.someoneice.wolftail.ui.widget.WString
import club.someoneice.wolftail.util.UIPos
import com.google.common.collect.ImmutableList
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.client.resources.I18n
import net.minecraft.util.ChatComponentTranslation
import net.minecraft.util.IChatComponent

class GuiRemoveEntity(val parent: GuiBank, val entity: WidgetEntitySlot): WScreen(256, 256) {
  val BUTTON_YES: WButton = object: WButton(
    I18n.format("entitybank.remove.yes"),
    UIPos(40, 144, 80, 16),
    StyleBackground,
    {
      NetworkHandler.sendPacket(C2SRemoveEntity(this.entity.entity.uniqueID))
      this.parent.removeChild(this.entity)
      this.mc.displayGuiScreen(this.parent)
    }
  ), ITooltip {
    override fun getData(): ImmutableList<IChatComponent> =
      ImmutableList.of(ChatComponentTranslation("entitybank.remove.tooltip"))
  }

  val BUTTON_NO = WButton(
    I18n.format("entitybank.remove.no"),
    UIPos(150, 144, 80, 16),
    StyleBackground
  ) {
    this.mc.displayGuiScreen(this.parent)
  }

  override fun getStyle(): IStyleUI = IStyleUI.INSTANCE

  override fun initWidgets() {
    this.addWidget(WString(I18n.format("entitybank.remove.info"), 76, 103))
    this.addWidget(BUTTON_YES)
    this.addWidget(BUTTON_NO)
  }

  override fun drawBackground(sign: Int) {
    this.drawWorldBackground(sign)
  }

  override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
    val x: Int = if (this.w == -1) 0 else (this.width - this.w) / 2
    val y: Int = if (this.h == -1) 0 else (this.height - this.h) / 2

    GuiInventory.func_147046_a(
      x + 128, y + 80, 30,
      x + 128 - mouseX.toFloat(), y + 30 - mouseY.toFloat(),
      this.entity.entity)
  }

  override fun onGuiClosed() {
    this.mc.displayGuiScreen(this.parent)
  }
}
