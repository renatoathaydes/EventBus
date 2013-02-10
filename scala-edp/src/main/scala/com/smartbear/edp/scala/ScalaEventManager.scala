package com.smartbear.edp.scala

import ref.WeakReference
import java.util.EventObject
import collection.mutable.ArrayBuffer
import com.google.common.eventbus.{Subscribe, EventBus}
import com.smartbear.edp.api.{EventSubscriber, EventManager}


class ScalaEventManager extends EventManager {

  private val bus = new EventBus()
  private val handlers = collection.mutable.Map[
    Class[_ <: EventObject], ArrayBuffer[WeakReference[EventSubscriber[_]]]]()
  val empty = ArrayBuffer[WeakReference[EventSubscriber[_]]]()

  bus.register(this)

  @Subscribe
  def onEvent(event: EventObject) {
    println("Got event " + event)
    val deadRefs = new ArrayBuffer[WeakReference[EventSubscriber[_]]]
    val refs = handlers.getOrElse(event.getClass, empty)

    refs.foreach {
      ref =>
        ref.get match {
          case Some(sub) => if (sub.getSourceBaseType isAssignableFrom event.getSource.getClass) {
            //sub.handle( event ) //FIXME this is a compiling error in Scala! Need to be rewritten
          }
          case None => deadRefs += ref
        }
    }
    refs --= deadRefs
  }


  def post(event: EventObject) {
    println("Posting event: " + event)
    bus.post(event)
  }

  def getName: String = getClass.getName

  def unSubscribe[K <: EventObject](subscriber: EventSubscriber[K]) {}

  def subscribeWeakly[K <: EventObject](subscriber: EventSubscriber[K], eventType: Class[K]) {
    handlers.getOrElseUpdate(eventType, new ArrayBuffer) += new WeakReference(subscriber)
    println("New subscriber: " + handlers)
  }

  def subscribe[K <: EventObject](subscriber: EventSubscriber[K], eventType: Class[K]) {}
}

