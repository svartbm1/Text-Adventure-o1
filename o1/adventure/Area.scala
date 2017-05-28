package o1.adventure

import scala.collection.mutable.Map
//import scala.collection.mutable.Buffer

/** The class `Area` represents locations in a text adventure game world. A game world 
  * consists of areas. In general, an "area" can be pretty much anything: a room, a building, 
  * an acre of forest, or something completely different. What different areas have in 
  * common is that players can be located in them and that they can have exits leading to 
  * other, neighboring areas. An area also has a name and a description. 
  *
  * @param name         the name of the area 
  * @param description  a basic description of the area (typically not including information about items)
  */
class Area(var name: String, var description: String, var noteString: String) {
  
  private val neighbors = Map[String, Area]()
  private val items = Map[String, Item]()
  private val guards = Map[String,Guard]()
  val secretNeighborHolder = Map[String, Area]()
 
  def addItem(item: Item): Unit = this.items += item.name -> item
  
  def addGuard(guard: Guard): Unit = this.guards += guard.name -> guard
  def removeGuard(guardName: String) = this.guards -= guardName
  def removeGuards: Unit = this.guards.clear
 
  def addSecretNeighbor(direction: String, area: Area) = secretNeighborHolder += direction -> area
  
  def areaNeighbors = this.neighbors.keys.toArray
  
  def itemType(itemName: String): Option[Boolean] = {
    if (!this.contains(itemName)) None
    else Some(this.items.get(itemName).get.itemType)
  }
  
  def itemDescription(itemName: String): Option[String] = {
    if (!this.contains(itemName)) None
    else Some(this.items.get(itemName).get.description)
  }
  
  def removeItem(itemName: String): Option[Item] = {   
    var item =  this.items.get(itemName)
    if (this.contains(itemName))this.items -= itemName
    item
    } // should work
  
  def contains(itemName: String): Boolean = {
   if (this.items.get(itemName) == None) false else true
    } // should work
  
  /** Returns the area that can be reached from this area by moving in the given direction. The result 
    * is returned in an `Option`; `None` is returned if there is no exit in the given direction. */
  def neighbor(direction: String) = this.neighbors.get(direction)

  
  /** Adds an exit from this area to the given area. The neighboring area is reached by moving in 
    * the specified direction from this area. */
  def setNeighbor(direction: String, neighbor: Area) = {
    this.neighbors += direction -> neighbor
  }

  
  
  /** Adds exits from this area to the given areas. Calling this method is equivalent to calling 
    * the `setNeighbor` method on each of the given direction--area pairs.
    *
    * @param exits  contains pairs consisting of a direction and the neighboring area in that direction
    * @see [[setNeighbor]]
    */
  def setNeighbors(exits: Vector[(String, Area)]) = {
    this.neighbors ++= exits
  }
  
  def guardsOfArea = this.guards
  
  /** Returns a multi-line description of the area as a player sees it. This includes a basic 
    * description of the area as well as information about exits and items. */
  def fullDescription = {
    val itemList = "\nYou see here: " + this.items.keys.mkString(", ")
    var guardList = ""
    if (this.guards.size == 1) guardList = "\nThere is one guard in this area." 
    else if (guards.size > 1) guardList = "\nThere are " + guards.size + " guards in this area."
    val exitList = "\n\nExits available: " + this.neighbors.keys.mkString(" ")
    if (this.items.isEmpty) this.description + guardList + exitList 
    else  this.description + itemList + guardList + exitList //done
  
  } 
  
  
  /** Returns a single-line description of the area for debugging purposes. */
  override def toString = this.name + ": " + this.description.replaceAll("\n", " ").take(150)

  
  
}
