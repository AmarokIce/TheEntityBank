package club.someoneice.manametal.entitybank.client

import alexsocol.asjlib.mc
import club.someoneice.wolftail.api.IStyle
import club.someoneice.wolftail.util.UIPos
import club.someoneice.wolftail.util.UtilGui
import net.minecraft.client.gui.Gui
import net.minecraft.util.ResourceLocation
import java.awt.Color
import java.util.*

object StyleBackground: IStyle {
  val colorRgb = Color(0, 0, 0, 128).rgb
  val colorRgbHL = Color(0, 0, 0, 192).rgb

  override fun getTexture(): ResourceLocation? = null
  override fun render(pGui: Gui, rect: UIPos, pGuiX: Int, pGuiY: Int, args: Map<String, Any>) {
    val sX = rect.x + pGuiX
    val sY = rect.y + pGuiY

    val highlight = args["highlight"] as? Boolean == true
    val canBeUse = args["canUse"] as? Boolean == true

    UtilGui.fill(sX, sY, rect.w, rect.h, if (!canBeUse) Color.BLACK.rgb else if (highlight) this.colorRgbHL else colorRgb)

    val title = args["title"] as? String
    if (Objects.nonNull(title)) {
      this.drawString(
        title!!, pGui,
        rect.x + (rect.w / 2 - mc.fontRenderer.getStringWidth(title) / 2),
        rect.y + (rect.h / 2 - mc.fontRenderer.FONT_HEIGHT / 2),
        pGuiX, pGuiY
      )
    }
  }
}
