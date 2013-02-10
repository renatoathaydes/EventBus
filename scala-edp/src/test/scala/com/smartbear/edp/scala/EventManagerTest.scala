package com.smartbear.edp.scala


import java.util.EventObject

import org.junit.Test
import com.smartbear.edp.api.EventSubscriber
import java.util.concurrent.{TimeUnit, CountDownLatch}

import org.junit.Assert.assertEquals

/**
 *
 * User: Renato
 */
class EventManagerTest {

  //  extends
  //FlatSpec {
  //JUnitSuite { {

  // Example Test from:
  // http://www.scalatest.org/user_guide/writing_your_first_test
  //  "A Stack" should "pop values in last-in-first-out order" in {
  //    val stack = new Stack[Int]
  //    stack.push(1)
  //    stack.push(2)
  //    assert(stack.pop() === 2)
  //    assert(stack.pop() === 1)
  //  }
  //
  //  it should "throw NoSuchElementException if an empty stack is popped" in {
  //    val emptyStack = new Stack[String]
  //    intercept[NoSuchElementException] {
  //      emptyStack.pop()
  //    }
  //  }

  class SpecialEvent(source: Any) extends EventObject(source: Any) {}

  @Test def testUsage() {
    val latch = new CountDownLatch(1)
    val m = new ScalaEventManager
    var specialEvents = List[SpecialEvent]()

    val sub1 = new EventSubscriber[SpecialEvent] {
      val sourceBaseType = classOf[Number]

      def handle(event: SpecialEvent) {
        println("Handling event: " + event)
        specialEvents = event :: specialEvents
        latch.countDown()
      }

      def getSourceBaseType: Class[_] = sourceBaseType
    }

    m.subscribeWeakly(sub1, classOf[SpecialEvent])

    m.post(new EventObject(1)) // should not get caught because of event's class
    m.post(new SpecialEvent("Hi")) // should not get caught because of the source's class
    m.post(new SpecialEvent(2)) // should get caught

    //assert(latch.await(1, TimeUnit.SECONDS), "Latch did not count down to 0")
    //assertEquals("Wrong number of events caught", 1, specialEvents.size)

    println("Caught " + specialEvents.size + " events")
    println("Done!")
  }

}
