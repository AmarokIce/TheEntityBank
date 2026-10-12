package club.someoneice.manametal.entitybank.client.gui

import club.someoneice.manametal.entitybank.network.NetworkHandler
import club.someoneice.manametal.entitybank.network.packet.C2SRequestEntities
import club.someoneice.manametal.entitybank.network.packet.S2CEntities
import club.someoneice.wolftail.api.style.IStyleUI
import club.someoneice.wolftail.ui.core.WScreen
import club.someoneice.wolftail.ui.widget.WVarString

class GuiWaiting: WScreen(256, 256) {
  override fun getStyle(): IStyleUI = IStyleUI.INSTANCE

  override fun initWidgets() {
    NetworkHandler.sendPacket(C2SRequestEntities)

    this.addWidget(WVarString(text = {
      if (S2CEntities.PACK_RES.isEmpty()) {
        return@WVarString "Waiting..."
      }

      val maxSize = S2CEntities.PACK_RES[0].maxSize
      return@WVarString "Loading... ${S2CEntities.PACK_RES.size}/${maxSize}"
    }, 64, 119))
  }

  override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
    if (S2CEntities.PACK_RES.isEmpty()) {
      return
    }

    val maxSize = S2CEntities.PACK_RES[0].maxSize
    if (maxSize == 0) {
      S2CEntities.PACK_RES.clear()
      mc.displayGuiScreen(null)
      return
    }

    if (S2CEntities.PACK_RES.size == maxSize) {
      mc.displayGuiScreen(GuiBank())
    }
  }

  override fun drawBackground(sign: Int) {
    this.drawWorldBackground(sign)
  }

  override fun doesGuiPauseGame(): Boolean {
    return false
  }
}
