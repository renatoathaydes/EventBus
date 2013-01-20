package com.smartbear.edp.scala

import ref.WeakReference
import java.util.EventObject
import collection.mutable.ArrayBuffer
import com.google.common.eventbus.{Subscribe, EventBus}


class ScalaEventManager {

  private val bus = new EventBus()
  private val handlers = collection.mutable.Map[
    Class[_ <: EventObject], ArrayBuffer[WeakReference[ScalaSubscriber]]]()
  val empty = ArrayBuffer[WeakReference[ScalaSubscriber]]()

  bus.register(this)

  @Subscribe
  def onEvent(event: EventObject) {
    println("Got event " + event)
    val deadRefs = new ArrayBuffer[WeakReference[ScalaSubscriber]]
    val refs = handlers.getOrElse(event.getClass, empty)

    refs.foreach {
      ref =>
          ref.get match {
            case Some(sub) => if (sub.sourceBaseType isAssignableFrom event.getSource.getClass) sub.handle(event)
            case None => deadRefs += ref
          }
    }
    refs --= deadRefs
  }


  def post(event: EventObject) {
    println("Posting event: " + event)
    bus.post(event)
  }

  def subscribeWeak[B <: EventObject](subscriber: ScalaSubscriber, eventType: Class[B]) {
    handlers.getOrElseUpdate(eventType, new ArrayBuffer) += new WeakReference(subscriber)
    println("New subscriber: " + handlers)
  }

}

trait ScalaSubscriber {
  val sourceBaseType: Class[_]
  def handle(event: EventObject)
}

