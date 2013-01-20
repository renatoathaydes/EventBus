package com.smartbear.edp.scala


import java.util.EventObject

import org.scalatest.junit.JUnitSuite
import org.junit.Test


/**
 *
 * User: Renato
 */
class EventManagerTest extends
//FlatSpec {
JUnitSuite {

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

  class SpecialEvent(source: Any) extends EventObject(source: Any)

  @Test def testUsage() {
      val m = new ScalaEventManager

      val sub1 = new ScalaSubscriber {
        val sourceBaseType = classOf[Number]

        def handle(event: EventObject) {
          println("Handling event: " + event.asInstanceOf[SpecialEvent])
        }
      }
      m.subscribeWeak(sub1, classOf[SpecialEvent])

      m.post(new EventObject(1))
      m.post(new SpecialEvent(2))

      println(sub1)
    }

}
