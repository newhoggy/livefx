package org.livefx

package object event {
  type Bus[A] = SinkSource[A, A]
}
