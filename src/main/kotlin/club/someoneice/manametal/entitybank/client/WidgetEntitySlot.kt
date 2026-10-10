package club.someoneice.manametal.entitybank.client

import alexsocol.asjlib.mc
import club.someoneice.wolftail.api.IMouseEventListener
import club.someoneice.wolftail.api.IStyle
import club.someoneice.wolftail.api.ITooltip
import club.someoneice.wolftail.api.IWidget
import club.someoneice.wolftail.api.style.StyleAdapter
import club.someoneice.wolftail.util.UIPos
import club.someoneice.wolftail.util.UtilGui
import com.google.common.collect.ImmutableList
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.shader.Framebuffer
import net.minecraft.entity.EntityLiving
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.ChatComponentText
import net.minecraft.util.IChatComponent
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import java.awt.image.BufferedImage

class WidgetEntitySlot(index: Int, val entity: EntityLiving, val onClick: (WidgetEntitySlot) -> Unit):
  IWidget, IMouseEventListener, ITooltip {
  private val pos = UIPos((index + 1) % 8 * 20, (index + 1) / 8 * 20, 20, 20)
  private val entityAvatar: BufferedImage = bakeEntityAvatar(entity)

  override fun getStyle(): IStyle = StyleAdapter.INSTANCE
  override fun weightPos(): UIPos = this.pos

  override fun render(pGui: Gui, pMouseX: Int, pMouseY: Int, pGuiX: Int, pGuiY: Int) {
    UtilGui.drawImageWithoutResource(pGuiX + 2, pGuiY + 2, this.entityAvatar)
  }

  override fun onMouseClicked(pGui: Gui, pMouseX: Int, pMouseY: Int, pGuiX: Int, pGuiY: Int, pMouseButton: Int) {
    onClick(this)
  }

  override fun getData(): ImmutableList<IChatComponent> {
    return ImmutableList.of(ChatComponentText(this.entity.commandSenderName))
  }

  companion object {
    fun bakeEntityAvatar(entity: EntityLivingBase, size: Int = 16): BufferedImage {
      val image: BufferedImage

      val fbo = Framebuffer(size, size, true)
      fbo.bindFramebuffer(true)

      GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
      GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)

      GL11.glMatrixMode(GL11.GL_PROJECTION)
      GL11.glPushMatrix()
      run {
        GL11.glLoadIdentity()
        GL11.glOrtho(-size / 2.0, size / 2.0, size / 2.0, -size / 2.0, -1000.0, 1000.0)

        GL11.glMatrixMode(GL11.GL_MODELVIEW)
        GL11.glPushMatrix()
        run {
          GL11.glLoadIdentity()

          GL11.glEnable(GL11.GL_DEPTH_TEST)
          GL11.glEnable(GL11.GL_BLEND)
          GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

          val entityHeight = if (entity.height > 0) entity.height else 1.8f
          val scale = (size * 0.45f) / entityHeight

          GL11.glTranslatef(0.0f, size * 0.2f, 0.0f) // 偏置，使头部居中
          GL11.glScalef(-scale, scale, scale)

          GL11.glRotatef(180.0f, 0.0f, 0.0f, 1.0f)
          GL11.glRotatef(0.0f, 1.0f, 0.0f, 0.0f)
          GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f)

          val tempYO = entity.renderYawOffset
          val tempYR = entity.rotationYawHead

          entity.renderYawOffset = 180.0f
          entity.rotationYawHead = 180.0f

          RenderHelper.enableStandardItemLighting()
          RenderManager.instance.playerViewY = 180.0f
          RenderManager.instance.renderEntityWithPosYaw(
            entity,
            0.0, 0.0, 0.0,
            0.0f, 1.0f
          )
          RenderHelper.disableStandardItemLighting()

          entity.renderYawOffset = tempYO
          entity.rotationYawHead = tempYR

          image = renderImageFromFBO(size)

          GL11.glMatrixMode(GL11.GL_PROJECTION)
        }
        GL11.glPopMatrix()
        GL11.glMatrixMode(GL11.GL_MODELVIEW)
      }
      GL11.glPopMatrix()

      fbo.unbindFramebuffer()
      fbo.deleteFramebuffer()
      mc.framebuffer.bindFramebuffer(true)

      return image
    }

    private fun renderImageFromFBO(size: Int): BufferedImage {
      val length = size * size
      val pixelBuffer = BufferUtils.createIntBuffer(length)
      GL11.glReadPixels(0, 0, size, size, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixelBuffer)

      val pixels = IntArray(length)
      pixelBuffer.get(pixels)

      val image = BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB)
      for (y in 0..<size) {
        for (x in 0..<size) {
          val pixel = pixels[(size - 1 - y) * size + x]
          val a = (pixel shr 24) and 0xFF
          val r = pixel and 0xFF
          val g = (pixel shr 8) and 0xFF
          val b = (pixel shr 16) and 0xFF
          image.setRGB(x, y, (a shl 24) or (r shl 16) or (g shl 8) or b)
        }
      }
      return image
    }
  }
}
