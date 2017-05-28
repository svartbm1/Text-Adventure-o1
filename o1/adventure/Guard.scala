package o1.adventure


class Guard(val name: String, val startingLocation: Area) {
  //true items can be picked up, false items cannot be picked up but can be examined
  
  /** Returns a short textual representation of the item (its name, that is). */
  override def toString = this.name
  
  var health = 100 //is a number between 0 and 100
  var morale = 50 
  
  //assumes its getting a positive number between 0 and 100, otherwise does nothing
  def damageHealth(magnitude: Int) = { // does true damage
    var originalhp = health
    if (0 < magnitude && magnitude <= 100) {
     if (health - magnitude > 0) health -= magnitude else health = 0
    }
    originalhp - health 
  }
  
  def returnToStart = this.startingLocation.addGuard(this)
  
  def damageMorale(magnitude: Int) = { //does morale damage
    var originalMorale = morale
    if (0 < magnitude && magnitude <= 100) {
    if (morale - magnitude > -50) morale -= magnitude else morale= -50
    }
    originalMorale - morale
  }
  
  
  
  def isDead: Boolean = health == 0
  
  def isDemoralized: Boolean = morale < 0
  def isKnockedOut: Boolean = health < 0
  

  
  

}