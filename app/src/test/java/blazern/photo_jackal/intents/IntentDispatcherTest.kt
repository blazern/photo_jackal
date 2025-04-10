package blazern.photo_jackal.intents

import android.content.Intent
import org.junit.Assert
import org.junit.Test

class IntentDispatcherTest {
    private val dispatcher = IntentDispatcher()

    @Test
    fun `intent is dispatched`() {
        var received = false
        val receiver = { intent: Intent ->
            received = true
            true
        }
        dispatcher.addReceiver(receiver)

        Assert.assertFalse(received)
        dispatcher.dispatch(Intent())
        Assert.assertTrue(received)
    }

    @Test
    fun `intent is not dispatched when receiver removed`() {
        var received = false
        val receiver = { _: Intent ->
            received = true
            true
        }

        dispatcher.addReceiver(receiver)
        dispatcher.removeReceiver(receiver)
        dispatcher.dispatch(Intent())
        Assert.assertFalse(received)
    }

    @Test
    fun `intent is not dispatched twice when consumed`() {

        var received = 0
        dispatcher.addReceiver {
            received += 1
            true
        }
        dispatcher.addReceiver {
            received += 1
            true
        }

        Assert.assertEquals(0, received)
        dispatcher.dispatch(Intent())
        Assert.assertEquals(1, received)
    }

    @Test
    fun `intent is dispatched twice when not consumed`() {

        var received = 0
        dispatcher.addReceiver {
            received += 1
            false
        }
        dispatcher.addReceiver {
            received += 1
            true
        }

        Assert.assertEquals(0, received)
        dispatcher.dispatch(Intent())
        Assert.assertEquals(2, received)
    }

    @Test
    fun `intent is dispatched after the fact`() {
        dispatcher.dispatch(Intent())

        var received = false
        dispatcher.addReceiver {
            received = true
            true
        }
        Assert.assertTrue(received)
    }

    @Test
    fun `intent is dispatched after the fact when not consumed by another receiver`() {
        dispatcher.addReceiver { false }

        dispatcher.dispatch(Intent())

        var received = false
        dispatcher.addReceiver {
            received = true
            true
        }
        Assert.assertTrue(received)
    }

    @Test
    fun `intent is not dispatched after the fact when consumed by another receiver`() {
        dispatcher.addReceiver { true }

        dispatcher.dispatch(Intent())

        var received = false
        dispatcher.addReceiver {
            received = true
            true
        }
        Assert.assertFalse(received)
    }

    @Test
    fun `intent is not dispatched after the fact twice`() {
        dispatcher.dispatch(Intent())

        var received = 0
        dispatcher.addReceiver {
            received += 1
            true
        }
        dispatcher.addReceiver {
            received += 1
            true
        }
        Assert.assertEquals(1, received)
    }

    @Test
    fun `intent is dispatched after the fact twice when not consumed by first receiver`() {
        dispatcher.dispatch(Intent())

        var received = 0
        dispatcher.addReceiver {
            received += 1
            false
        }
        dispatcher.addReceiver {
            received += 1
            true
        }
        Assert.assertEquals(2, received)
    }
}