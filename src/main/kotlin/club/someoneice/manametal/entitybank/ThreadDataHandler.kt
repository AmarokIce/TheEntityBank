package club.someoneice.manametal.entitybank

import com.google.common.collect.Queues
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.gameevent.TickEvent
import java.util.*

object ThreadDataHandler {
  val TASKS: Queue<Runnable> = Queues.newArrayDeque()

  @SubscribeEvent
  fun onEvent(event: TickEvent.WorldTickEvent) {
    if (event.phase == TickEvent.Phase.END || TASKS.isEmpty()) {
      return
    }

    val task = TASKS.poll()
    if (Objects.isNull(task)) {
      return
    }

    task.run()
  }
}
