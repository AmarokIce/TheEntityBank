package club.someoneice.manametal.entitybank.client.gui

import club.someoneice.manametal.entitybank.network.NetworkHandler
import club.someoneice.manametal.entitybank.network.packet.C2SRequestEntities
import club.someoneice.manametal.entitybank.network.packet.S2CEntities
import club.someoneice.wolftail.api.style.IStyleUI
import club.someoneice.wolftail.ui.core.WScreen

class GuiWaiting: WScreen(256, 256) {
  override fun getStyle(): IStyleUI = IStyleUI.INSTANCE

  override fun initWidgets() {
    NetworkHandler.sendPacket(C2SRequestEntities)
  }

  override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
    this.drawWorldBackground(0)

    val x: Int = if (this.w == -1) 0 else (this.width - this.w) / 2
    val y: Int = if (this.h == -1) 0 else (this.height - this.h) / 2

    if (S2CEntities.PACK_RES.isEmpty()) {
      return
    }

    val maxSize = S2CEntities.PACK_RES[0].maxSize
    this.getStyle().drawString("Loading... ${S2CEntities.PACK_RES.size}/${maxSize}",
      this, x / 4, y / 2 - 8, x, y)

    if (maxSize == 0) {
      this.mc.displayGuiScreen(null)
      return
    }

    if (maxSize != S2CEntities.PACK_RES.size) {
      return
    }

    mc.displayGuiScreen(GuiBank())
  }

  override fun drawBackground(sign: Int) {
    this.drawWorldBackground(sign)
  }
}
