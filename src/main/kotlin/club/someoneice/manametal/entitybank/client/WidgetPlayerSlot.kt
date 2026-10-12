package club.someoneice.manametal.entitybank.client

import alexsocol.asjlib.mc
import club.someoneice.manametal.entitybank.client.gui.GuiBank
import club.someoneice.manametal.entitybank.network.NetworkHandler
import club.someoneice.manametal.entitybank.network.packet.C2SRemoveEntity
import club.someoneice.wolftail.api.IMouseEventListener
import club.someoneice.wolftail.api.IStyle
import club.someoneice.wolftail.api.ITooltip
import club.someoneice.wolftail.api.IWidget
import club.someoneice.wolftail.api.style.StyleAdapter
import club.someoneice.wolftail.util.UIPos
import club.someoneice.wolftail.util.UtilGui
import com.google.common.cache.CacheBuilder
import com.google.common.collect.ImmutableList
import net.minecraft.client.gui.Gui
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ChatComponentText
import net.minecraft.util.IChatComponent
import java.awt.Image
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.net.URL
import javax.imageio.ImageIO

class WidgetPlayerSlot(index: Int, val player: EntityPlayer, val entity: WidgetEntitySlot, val parent: GuiBank):
  IWidget, IMouseEventListener, ITooltip {
  private val pos = UIPos(index % 4 * 40 + 1, index / 4 * 40 + 1, 40, 40)
  private val entityAvatar: BufferedImage = getPlayerHead(player.commandSenderName)

  override fun getStyle(): IStyle = StyleAdapter.INSTANCE
  override fun weightPos(): UIPos = this.pos

  override fun render(pGui: Gui, pMouseX: Int, pMouseY: Int, pGuiX: Int, pGuiY: Int) {
    UtilGui.drawImageWithoutResource(pGuiX + this.pos.x + 4, pGuiY + this.pos.y + 4, this.entityAvatar)
  }

  override fun onMouseClicked(pGui: Gui, pMouseX: Int, pMouseY: Int, pGuiX: Int, pGuiY: Int, pMouseButton: Int) {
    NetworkHandler.sendPacket(C2SRemoveEntity(
        this.entity.entity.uniqueID,
      false,
      this.player.commandSenderName)
    )
    parent.removeChild(this.entity)
    mc.displayGuiScreen(parent)
  }

  override fun getData(): ImmutableList<IChatComponent> {
    return ImmutableList.of(ChatComponentText(this.player.commandSenderName))
  }

  companion object {
    const val MC_API_URI = "https://api.minecraftapi.net/v3/profile/%s/avatar?size=128&overlay=true"
//    const val URI = "https://api.rms.net.cn/head/%s"
    val PLAYER_CACHE = CacheBuilder.newBuilder()!!.maximumSize(20)!!.build<String, BufferedImage>()!!

    fun getPlayerHead(name: String, size: Int = 32): BufferedImage {
      val img = PLAYER_CACHE.getIfPresent(name) ?: (
        ImageIO.read(URL(MC_API_URI.format(name)))
          ?: ImageIO.read(URL(MC_API_URI.format("Steve")))!!
        ).let {
          val png = convertPng(it)
          PLAYER_CACHE.put(name, png)
          return@let png
        }

      return resetSize(img, size)
    }

    fun resetSize(img: BufferedImage, size: Int): BufferedImage {
      val newImg = BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB)
      newImg.graphics.drawImage(
        img.getScaledInstance(size, size, Image.SCALE_REPLICATE), 0, 0, null)
      return newImg
    }

    fun convertPng(oImg: BufferedImage): BufferedImage {
      val pngImg = BufferedImage(oImg.width, oImg.height, BufferedImage.TYPE_INT_ARGB)

      val g2d = pngImg.createGraphics()
      g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR)
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF)
      g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED)
      g2d.drawImage(oImg, 0, 0, oImg.width, oImg.height, null)
      g2d.dispose()

      return pngImg
    }
  }
}
