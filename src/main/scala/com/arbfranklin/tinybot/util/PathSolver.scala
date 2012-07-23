/*
 * Copyright (c) 2012, Andrew Franklin
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.arbfranklin.tinybot.util

import collection._
import PathSolver._

trait PathSolver {
  def solve(): Set[Move]
}

object PathSolver {
  def apply(view: View, start: XY, goal: XY): PathSolver = new AStarSearch(view, start, goal)

  /**val move set */
  val moves = Move.values - Move.Center

  /**bad tile def */
  def isBad(t: Tile.Tile) = (t == Tile.Wall || t == Tile.Toxifera || t == Tile.MiniBot)

  /** find the neighbouring squares of xy */
  def neighbours(view: View, xy: XY) = {
    moves.map(xy + _).filter(p => {
      !isBad(view.at(p)) && view.isBounded(p)
    })
  }
}

/**
 * View solver using A* search.
 * Based on pseudocode at: http://en.wikipedia.org/wiki/A*_search_algorithm
 */
class AStarSearch(view: View, start: XY, goal: XY) extends PathSolver {
  def solve(): Set[Move] = {
    val closedSet = mutable.Set[XY]()
    val openSet = mutable.Set[XY](start)
    val cameFrom = mutable.OpenHashMap[XY, XY]()

    val gScore = mutable.OpenHashMap[XY, Int](start -> 0)
    val fScore = mutable.OpenHashMap[XY, Int](start -> (gScore(start) + start.distTo(goal)))

    while (!openSet.isEmpty) {
      // the node in openset having the lowest f_score[] value
      val current = openSet.minBy(x => fScore(x))
      if (current == goal) {
        val path = reconstructPath(cameFrom, goal)
        if (path.length==2) {
          return Set(path(1)-path(0))
        } else {
          // permute the options for step 2
          val p1 = path(0)
          val p2 = path(2)
          val opts = (neighbours(view,p1) intersect neighbours(view,p2))
          return opts.map(o => o - p1).toSet
        }
      }

      openSet.remove(current)
      closedSet.add(current)

      neighbours(view, current).foreach(n => {
        if (!closedSet.contains(n)) {
          val tGScore = gScore(current) + 1
          if (!openSet.contains(n) || (tGScore < gScore(n))) {
            openSet.add(n)
            cameFrom += (n -> current)
            gScore += (n -> tGScore)
            fScore += (n -> (gScore(n) + n.distTo(goal)))
          }
        }
      })
    }

    // failure
    Set()
  }

  private def reconstructPath(cameFrom: Map[XY, XY], current: XY): List[XY] = {
    if (cameFrom.contains(current)) {
      reconstructPath(cameFrom, cameFrom(current)) ++ List(current)
    } else {
      List(current)
    }
  }
}

/** alternative path solver that's just too slow */
class BFS(view: View, start: XY, goal: XY) extends PathSolver {
  val moves = Move.values - Move.Center

  def solve(): Set[Move] = {
    if (!view.isBounded(goal)) return Set()

    val matrix = Array.fill(math.pow(view.cols,2).toInt)(Int.MaxValue)

    var q = List((start,0))
    while (!q.isEmpty) {
      // dequeue
      val elem = q.head
      q = q.tail

      val xy = elem._1
      val step = elem._2

      matrix(toInt(xy)) = step

      neighbours(view,xy).foreach(n => {
        val i = toInt(n)
        if (matrix(i)>step) {
          q = (n,step+1) :: q
        }
      })
    }

    // solvable?
    val len = matrix(toInt(goal))
    if (len == Int.MaxValue) {
      Set()
    } else {
      // walk backwards
      var candidates = Set(goal)
      var i = len
      while( i>1 ) {
        i -= 1
        candidates = candidates.foldLeft(Set[XY]()){ (l,c) =>
          l ++ neighbours(view,c).filter(xy => matrix(toInt(xy))==i).toSet
        }
      }

      candidates.map(xy => xy - start)
    }
  }

//  def toString(matrix: Array[Int]) = {
//    val s = new collection.mutable.StringBuilder()
//    for (i <- 0 until matrix.length) {
//      if (i % math.sqrt(matrix.length).toInt == 0) s.append('\n')
//      val n = matrix(i)
//      s.append(if (n==Int.MaxValue) "W" else if (n>=10) "+" else n.toString)
//    }
//    s.drop(1).toString()
//  }

  private def toInt(xy: XY) = xy.y * view.cols + xy.x
}
