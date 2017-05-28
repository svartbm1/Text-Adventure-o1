package o1.adventure


class MyAdventure {


  val title = "The Psion"
  


  

  private val cellStart      = new Area("Small Bed",  "You just woke up and are lying on a small bed. \nWhen you look around the room you see a desk on the east wall, a window to the north and a door to the south.", "") //room desc to all
  private val cellBed        = new Area("Small Bed",  "This is just a small bed. You can examine it to look closer. \nGo east to get to the desk, north to look through the window and south to go to the door.", "")// private val cellMiddle     = new Area("Cell",       "\nYou are in a small room. At the west wall is a bed, at the north a window, at the east a desk and at the south a door.", "")
  private val cellDoor       = new Area("Cell Door",  "You're at the door of your cell. If the door is unlocked you can exit by going south.\nYou can go west to the bed, east to the desk or north to the window.", "")
  private val cellWindow     = new Area("Window",     "It's dark and you would guess it's nighttime. \nYou can go south to the door. To the east is the desk and to the west is the bed.", "")
  private val cellDesk       = new Area("Desk",       "There are some things on the desk. Examine it to look closer. \nYou can go west to the bed, north to the window or south to get to the door.", "")
  private val hallwayWest3   = new Area("Hallway",    "You are in a hallway on the third floor The hallway continues to the east and there is a stairway to the west.", "Hallway on the third floor to the west.")
  private val hallwayWest2   = new Area("Hallway",    "You are in a hallway outside your room. There are more doors to the east and \na stairway to the west. You can go north to go back to your room.", "Hallway on the second floor to the west.")
  private val hallwayWest1   = new Area("Hallway",    "You are in a hallway on the first floor. The hallway continues to the east and there is a stairway to the west.", "Hallway on the first floor to the west.")
  private val hallwayEast3   = new Area("Hallway",    "You are in a hallway on the third floor. The hallway continues to the west and there is a stairway to the east.", "Hallway on the third floor to the east.")
  private val hallwayEast2   = new Area("Hallway",    "You are in a hallway east of the door to your room. There are more doors here and to the east there is another stairway.", "Hallway on the second floor to the east.") 
  private val hallwayEast1   = new Area("Hallway",    "You are in a hallway on the first floor. The hallway continues to the west and there is a stairway to the east.\nThere is a locked door to the south that reads: 'AUTHORIZED PERSONNEL ONLY'", "Hallway on the first floor to the east.")
  private val stairwayWest3  = new Area("Stairway",   "You are at the west stairway on the third floor. You can go down or east into the hallway.", "West stairway on the third floor.")
  private val stairwayWest2  = new Area("Stairway",   "You are at the west stairway on the second floor. You can go up, down or east into the hallway.", "West stairway on the second floor.")
  private val stairwayWest1  = new Area("Stairway",   "You are at the west stairway on the first floor. You can go up or east into the hallway.", "West stairway on the first floor.")
  private val stairwayEast3  = new Area("Stairway",   "You are at the east stairway on the third floor. You can go down or west into the hallway.", "East stairway on the third floor.")
  private val stairwayEast2  = new Area("Stairway",   "You are at the east stairway on the second floor. You can go up, down or west into the hallway.", "East stairway on the second floor.")
  private val stairwayEast1  = new Area("Stairway",   "You are at the east stairway on the first floor. You can go up or west into the hallway.", "East stairway on the first floor.")
  private val staffBreakroom = new Area("Breakroom",  "You are in the staff breakroom. There is a door to the south that leads outside. \nThe door behind you to the north closed behind you and you cannot open it again.", "Staff Area") 
  private val outdoors       = new Area("Outside",    "You finally make it outside from the facility. Or do you?", "")
  private val cellStart2     = new Area("???",        "You start walking in a random direction away from the facility and soon you see a car pulling up in the driveway. \nThe car stops in front of you and a middle aged woman dressed in rather fancy clothing steps out. \nShe looks at you for a moment then smiles wryly and snaps her fingers. The illusion starts to fade and you feel a little dizzy. \n\n---\nYou wake up and are lying on a small bed. \nWhen you look around the room you see a desk on the east wall, a window to the north and a door to the south. Déjà vu? \nIt appears you will need to burn the facility to the ground to have a chance of actually escaping.", "") //room desc to all
  private val outdoorsWin    = new Area("Outside",    "You finally make it outside from the facility. For real. You blew it up.", "")
  private val outdoorsWin2   = new Area("Outside",    "You finally make it outside from the facility. A car is approaching.", "")
  private val cellStart3     = new Area("???",        "You start walking in a random direction away from the facility and soon you see a car pulling up in the driveway. \nThe car slows down as it passes you and you can see a woman in fancy dress glare at you through the window. \nAs you look back the fire is spreading fast. You keep running and don't stop until you are far, far away.", "")
  private val cellStart4     = new Area("???",        "You start walking in a random direction away from the facility and soon you see a car pulling up in the driveway. \nThe car drives by you without slowing down. You can barely make out that there is a woman sound asleep in the back. \nYou keep running and don't stop until you are far, far away.", "")
  
       cellStart.setNeighbors(Vector("north" -> cellWindow     ,"east" -> cellDesk        ,"south" -> cellDoor                                                                                        ))   
      cellStart2.setNeighbors(Vector("north" -> cellWindow     ,"east" -> cellDesk        ,"south" -> cellDoor                                                                                        )) 
   
         cellBed.setNeighbors(Vector("north" -> cellWindow     ,"east" -> cellDesk        ,"south" -> cellDoor                                                                                        ))   
        cellDesk.setNeighbors(Vector("north" -> cellWindow                                ,"south" -> cellDoor       ,"west" -> cellBed                                                               ))
      cellWindow.setNeighbors(Vector(                           "east" -> cellDesk        ,"south" -> cellDoor       ,"west" -> cellBed                                                               ))
        cellDoor.setNeighbors(Vector("north" -> cellWindow     ,"east" -> cellDesk                                   ,"west" -> cellBed                                                               )) 
          cellDoor.addSecretNeighbor("south", hallwayWest2)
       
    hallwayWest3.setNeighbors(Vector(                           "east" -> hallwayEast3                               ,"west" -> stairwayWest3                                                         ))
    hallwayWest2.setNeighbors(Vector("north" -> cellDoor       ,"east" -> hallwayEast2                               ,"west" -> stairwayWest2                                                         ))
    hallwayWest1.setNeighbors(Vector(                           "east" -> hallwayEast1                               ,"west" -> stairwayWest1                                                         ))
      
    hallwayEast3.setNeighbors(Vector(                           "east" -> stairwayEast3                              ,"west" -> hallwayWest3                                                          ))
    hallwayEast2.setNeighbors(Vector(                           "east" -> stairwayEast2                              ,"west" -> hallwayWest2                                                          ))
    hallwayEast1.setNeighbors(Vector(                           "east" -> stairwayEast1                              ,"west" -> hallwayWest1                                                          ))       
    hallwayEast1.addSecretNeighbor("south", staffBreakroom)
   stairwayWest3.setNeighbors(Vector(                                                      "east" -> hallwayWest3                                                           ,"down" -> stairwayWest2  ))
   stairwayWest2.setNeighbors(Vector(                                                      "east" -> hallwayWest2                                ,"up" -> stairwayWest3     ,"down" -> stairwayWest1  ))
   stairwayWest1.setNeighbors(Vector(                                                      "east" -> hallwayWest1                                ,"up" -> stairwayWest2                               ))
      
   stairwayEast3.setNeighbors(Vector(                                                                                 "west" -> hallwayEast3                                ,"down" -> stairwayEast2  ))
   stairwayEast2.setNeighbors(Vector(                                                                                 "west" -> hallwayEast2     ,"up" -> stairwayEast3     ,"down" -> stairwayEast1  ))
   stairwayEast1.setNeighbors(Vector(                                                                                 "west" -> hallwayEast1     ,"up" -> stairwayEast2                               ))
    staffBreakroom.addSecretNeighbor("south", outdoors)
    staffBreakroom.addSecretNeighbor("south2", outdoorsWin)
    staffBreakroom.addSecretNeighbor("north", hallwayEast1)
    outdoors.addSecretNeighbor("anywhere else", outdoorsWin2)
    outdoorsWin.addSecretNeighbor("anywhere else", outdoorsWin2)
        outdoors.setNeighbors(Vector("anywhere" -> cellStart2))
     outdoorsWin.setNeighbors(Vector("anywhere" -> cellStart3))
    outdoorsWin2.setNeighbors(Vector("anywhere" -> cellStart4))   
        
        
        //some items are added when other items are examined, see class Player
        cellDesk.addItem(new Item("desk", "It's a simple desk with some notebooks and a couple pens on it. \nYou look in all the drawers without finding anything else really interesting.", false))
        cellDesk.addItem(new Item("spellbook", "It's a book with all the spells you learned. \nTo use the spellbook to find information about a known spell, type 'spellbook' followed by the name of a spell you know. \nTo see a list of spells you know, type spells. To learn new spells use the ones you have.", true))
        
      cellWindow.addItem(new Item("window", "It's a rather small window. It's barred and you cannot escape through it. \nIf you look outside you can see that you are at least 2 floors above ground level.", false))
        cellDoor.addItem(new Item("door", "This is the only door to your cell. There is a simple lock on it. \nIf you listen closely you can hear someone on the other side.", false))
         cellBed.addItem(new Item("bed", "This is just a regular bed. You could sleep on it but you don't really feel tired. \nYou find some old hairpins under the pillow.", false))
    hallwayWest3.addItem(new Item("doors", "You need a guard's key to open the doors.", false))
    hallwayEast3.addItem(new Item("doors", "You need a guard's key to open the doors.", false))
    hallwayEast2.addItem(new Item("doors", "You need a guard's key to open the doors.", false))
    hallwayWest1.addItem(new Item("doors", "You need a guard's key to open the doors.", false))
    hallwayWest3.addItem(new Item("thing", "This is a thing. It's really quite useless. There is no reason to take this with you.", true))
    hallwayEast1.addItem(new Item("door", "This isn't like the other doors in the hallways. Maybe this would lead out. It seems it's opened with employee ID cards.", false))
  staffBreakroom.addItem(new Item("exit", "This is a door much like the one you got through to get in here. The guards can certainly open it. If you know the code to the lock, type 'use 0000' replacing the zeros with the code.", false))
      

   
    hallwayWest2.addGuard(new Guard("Jeff", hallwayWest2))
    
    hallwayWest3.addGuard(new Guard("Jim", hallwayWest3))
    hallwayEast3.addGuard(new Guard("Dave", hallwayEast3))
    hallwayEast1.addGuard(new Guard("Stan", hallwayEast1))

   stairwayWest3.addGuard(new Guard("Matt", stairwayWest3))
   stairwayWest2.addGuard(new Guard("Mark", stairwayWest2))
   stairwayWest2.addGuard(new Guard("Tim", stairwayWest2))   
   stairwayEast1.addGuard(new Guard("John", stairwayEast1))
   stairwayEast1.addGuard(new Guard("Steve", stairwayEast1))
   stairwayWest1.addGuard(new Guard("Chad", stairwayWest1))
   
  staffBreakroom.addGuard(new Guard("Ted", staffBreakroom))
  staffBreakroom.addGuard(new Guard("Frank", staffBreakroom))
  staffBreakroom.addGuard(new Guard("Sean", staffBreakroom))
  staffBreakroom.addGuard(new Guard("Greg", staffBreakroom))
  
  
  //note: spells are added in Player class
  

  val player = new Player(cellStart)


  var turnCount = 0

  val timeLimit = 20000 // i didnt end up implementing this for now but leaving the code if i ever come back and play with this

  
  
  def isComplete = (this.player.location == this.cellStart3 || this.player.location == this.cellStart4) && player.has("spellbook")

  def checkIfDetected = {
    if (!this.player.location.guardsOfArea.isEmpty && !this.player.isInvisible) {
      for (guard <- this.player.location.guardsOfArea.values) player.setFollowing(guard.name, guard)
      this.player.location.removeGuards      
    }
  }
  
  
 
  def isOver = this.isComplete || this.player.hasQuit || this.turnCount == this.timeLimit

  
  def welcomeMessage = "You wake up in a small cell. You can't remember what happened or how you got here. \nYou do remember that you are a telepath, and you can use spells to influence other peoples thoughts or actions. \nYou should try to find your spellbook and escape the facility somehow."

    
  
  def goodbyeMessage = {
    if (this.isComplete) {
      "YOU WIN!"
    } else if (this.turnCount == this.timeLimit) {
      "GAME OVER!"
    } else { // game over due to player quitting
      "You have quit the game." 
    }
  }
  

  
  
  def playTurn(command: String) = {
    val action = new Action(command)
    val outcomeReport = action.execute(this.player)
    if (outcomeReport.isDefined) { 
      this.turnCount += 1 
    }
    outcomeReport.getOrElse("Unknown command: \"" + command + "\".")
  }
  
  
}

