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

import org.specs2.mutable._

class MapOfWorldSpec extends Specification {
  "simple overlay" should {
    val v1 = toView("""
      |_____
      |_____
      |__M__
      |WW___
      |_W___
    """.stripMargin)

    val v2 = toView("""
      |____M
      |__WW_
      |__SW_
      |W____
      |_W___
    """.stripMargin)

    "overlay bottom left" in {
      compare(v1.combine(XY(-2,2), v2), toView("""
        |?????????
        |?????????
        |??_____??
        |??_____??
        |____M__??
        |__WW___??
        |__SW___??
        |W____????
        |_W___????
      """.stripMargin))
    }

    // this case is actually invalid as you would always want only one M, and it should be at the center
    "overlay top right" in {
      compare(v1.combine(XY(2,-2), v2), toView("""
        |????____M
        |????__WW_
        |??____SW_
        |??__W____
        |??___W___
        |??WW___??
        |??_W___??
        |?????????
        |?????????
      """.stripMargin))
    }
  }

  "larger overlay" in {
    val v1 = toView("""
      |______________________?????????
      |______________________?????????
      |_____________________??????????
      |_____________________WWWW??????
      |________________________???????
      |_______________________????????
      |______________________?????????
      |_____________________??????????
      |_____________________??????????
      |____________________???????????
      |___________________????????????
      |__________________?????????????
      |_________________??????????????
      |_________________W?????????????
      |_________________W?????????????
      |_______________M_WW????????????
      |__________________?????????????
      |________??W____________????????
      |_____?????W_________________???
      |__????????W____________________
      |??????????W____________________
      |??????????W____________________
      |??????????W____________________
      |???????????____________________
      |??????????_________W___________
      |?????????__________W?__________
      |?????????__________W?__________
      |????????W__________W??_________
      |????????W__________W??W________
      |????????W__________W???________
      |????????W__________?????_______
    """.stripMargin)

    val v2 = toView("""
      |____________?????????
      |____________?????????
      |____________?????????
      |___________??????????
      |___________??????????
      |___________??????????
      |___________??????????
      |___________W?????????
      |__________MW???????__
      |___________WW???_____
      |__________S__________
      |????W___p__b__B______
      |????W________________
      |????W________________
      |????W______________P_
      |????W________________
      |????W________________
      |????____________P____
      |???__________W_______
      |??___________W?______
      |?WW__________W?______
    """.stripMargin)

    compare(v1.combine(XY(2,2), v2), toView("""
      |______________________?????????
      |______________________?????????
      |_____________________??????????
      |_____________________WWWW??????
      |________________________???????
      |_______________________????????
      |______________________?????????
      |_____________________??????????
      |_____________________??????????
      |____________________???????????
      |___________________????????????
      |__________________?????????????
      |__________________?????????????
      |__________________?????????????
      |__________________W????????????
      |_________________MW???????__???
      |__________________WW???_____???
      |_________________S__________???
      |_____?????WW___p__b__B______???
      |__????????WW___________________
      |??????????WW___________________
      |??????????WW______________P____
      |??????????WW___________________
      |???????????W___________________
      |??????????_____________P_______
      |?????????___________W__________
      |?????????___________W__________
      |????????WW__________W?_________
      |????????W__________W??W________
      |????????W__________W???________
      |????????W__________?????_______
    """.stripMargin))
  }

  def compare(v1: MapOfWorld, v2: MapOfWorld) = v1.toString === v2.toString
  def toView(s: String) = MapOfWorld(s.replaceAll("\\s*",""))
}
