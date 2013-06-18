package com.alvinalexander.akkademos

import javax.swing.JPanel
import java.awt.{Color, Graphics}
import scala.util.Random

class PingPongPanel extends JPanel {

  val panelWidth = 600
  val panelHeight = 400
  val ballDiameter = 40
  val halfPanelWidth = panelWidth / 2
  val halfPanelHeight = panelHeight / 2
  var currentX = 0
  var currentY = 0
  var color = Color.GREEN
  private val r = new Random
  
  def doPing {
    var x1 = r.nextInt(halfPanelWidth)
    if (x1 < ballDiameter) x1 = ballDiameter
    if (x1 > (halfPanelWidth-ballDiameter)) x1 = halfPanelWidth-ballDiameter
    currentX = x1
    currentY = getRandomY
    color = Color.GREEN
    repaint()
  }

  def doPong {
    var x1 = r.nextInt(halfPanelWidth) + halfPanelWidth
    if (x1 > (panelWidth-ballDiameter)) x1 = panelWidth-ballDiameter
    currentX = x1
    currentY = getRandomY
    color = Color.YELLOW
    repaint()
  }
  
  def getRandomY = {
    var y = r.nextInt(panelHeight)
    if (y < ballDiameter) y = ballDiameter / 2
    if (y > (panelHeight-ballDiameter)) y = panelHeight-2*ballDiameter
    y
  }

  override def paintComponent(g: Graphics) {
    g.clearRect(0, 0, panelWidth, panelHeight)
    
    // draw mid-court line
    g.setColor(Color.WHITE)
    g.drawLine(halfPanelWidth, 0, halfPanelWidth, panelHeight)

    // draw circle (ball)
    g.setColor(color)
    g.fillOval(currentX, currentY, ballDiameter, ballDiameter)
  }

}


