package club.someoneice.manametal.entitybank.client.gui

import club.someoneice.manametal.entitybank.client.StyleBackground
import club.someoneice.manametal.entitybank.client.WidgetEntitySlot
import club.someoneice.manametal.entitybank.client.WidgetPlayerSlot
import club.someoneice.wolftail.api.style.IStyleUI
import club.someoneice.wolftail.ui.core.WScreen
import club.someoneice.wolftail.ui.widget.WScrollList
import net.minecraft.entity.player.EntityPlayer

class GuiGift(val parent: GuiBank, val entity: WidgetEntitySlot): WScreen(256, 256) {
  private val scroll = WScrollList(80, 30, 160, 210, StyleBackground)
  override fun getStyle(): IStyleUI = IStyleUI.INSTANCE
  override fun initWidgets() {
    this.addWidget(scroll)

    var index = 0
    mc.theWorld.playerEntities
      .filterIsInstance<EntityPlayer>()
      .map { WidgetPlayerSlot(index++, it, entity, parent) }
      .forEach(scroll::addChild)
  }

  override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
  }
}
