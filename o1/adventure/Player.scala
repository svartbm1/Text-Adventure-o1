package o1.adventure

import scala.collection.mutable.Map
//import scala.collection.mutable.Buffer
import scala.util.Random

/* TODO: doors items should add a random set of useful items when examined. test multipleitems bug?
 * add a area mindblast or frenzy spell
 * add a way to actually win. spell to win the lady in the end
 * aurasight outside sees lady!!!!! Gwen
 * 
 */

class Player(startingArea: Area) {
  
  //all spells 
  private val aurasight      = new Spell("aurasight",    "Simply type 'aurasight' to cast. \nThis checks all neighboring areas and lets the caster know if there is anyone nearby. Consumes 75 mana.", 75)
  private val demoralize     = new Spell("demoralize",   "Type 'demoralize' and the name of the enemy you want to target. \nYou get into the targets head and make them believe there is nothing they can do and that all hope is lost for them. \nUse aurasight to be able to target guards who aren't following you. This does around 40 morale damage to a guard. \nIf a guards morale is negative, they will not take any action or pursue the player any longer. Consumes 40 mana.", 40)
  // not implemented:  private val mindblast      = new Spell("mindblast",    "You inflict a massive amount of pain on the target. Use aurasight to be able to target guards who aren't following you. \nThis does around 75 damage to a guard. A guard is knocked out at negative health but can recover when you rest. \nIf a guard dies at -100 health, they will not take any action for the rest of the game. 80 mana.", 80)
  private val invisibility   = new Spell("invisibility", "Type 'invisibility' to cast this effect on yourself, cast again to cancel the effect. \nMakes you invisible for all guards in the area. \nInitially consumes 200 mana. Will consume an additional 100 mana for every turn you spend in an area with guards present. ", 200)
  private val mindsight      = new Spell("mindsight", "Type 'mindsight' and the name of an enemy. This makes you read the target's mind. Consumes 10 mana.", 10)
  private val compel         = new Spell("compel", "NOTE: This is very situational and only works in a few cases. Control the guard to do your will. Type 'compel' and the name of a guard. Consumes 150 mana.", 150)            
  //add compel/control
  
  //lists
  private var currentLocation = startingArea        // gatherer: changes in relation to the previous location
  private var quitCommandGiven = false              // one-way flag
  private var playerItems =  Map[String, Item]() //inventory
  private var spellList = Map[String, Spell]((aurasight.name, aurasight), (mindsight.name, mindsight), (compel.name, compel)) //like an inventory for learned spells
  private var guardsFollowing = Map[String, Guard]() //the guards curretlyfollowing
  private var guardsDetected = Map[String, Guard]() //all guards the player has encountered
  private var manaVar = 1000 //current mana, gatherer
  var isInvisible = false //invisibility toggle
  val guardsDemoralized = Map[String, Guard]()
  //val guardsKnockedOut = Map[String, Guard]() FOR MINDBLAST 
  val demoralizedGuardLocations = Map[String, Area]()
 // val knockedOutGuardLocations = Map[String, Area]()  FOR MINDBLAST 
  val roll = new Random() //random generator
  private var castSuccess: Boolean = false //changed if a cast is successful, then changed back immediately. see methods cast, castAurasight, castIvisibility and castDemoralize
  private var guardsSearched = 0
  private var roomsSearched = 0
  
  
 def help() = { //command help
    "Type 'go north', 'go east', 'go south', 'go east', 'go up' or 'go down' (etc) to move around in the game world." +
    "\nType 'get' or 'take' to pick up items. Not all items can be picked up, but larger items can still be examined. " +  //picking up the same item twice may occur and may crash the game
    "\nType 'examine' (or 'search') to view a small item in your inventory or a large item in the area you are in. Examine demoralized guards to search their belongings." +
    "\nType 'inventory' to see the items you have with you. To drop an item use 'drop' and the item name." +
    "\nType 'spells' for a list of all the spells you know. Learn more spells by using the ones you already know." +
    "\nCast a spell by typing the spell name. For some spells a target should be defined as well. Example: 'Mindsight Jeff'." +
    "\nType 'spellbook' followed by the name of a spell if you want to know what a specific spell does and how to cast it." + 
    "\nType 'run' and a direction to attempt to shake off guards. Be careful not to run into more guards..." +
    "\nType 'rest' to regenerate around 500 mana. Be careful as demoralized guards can recover while you're resting. (PS. You can rest while invisible...)" +
    "\nTry commands such as 'pick lock' to interact with objects in the game world. In semi-rare cases the command 'use' and an item can also work." +
    "\nNote that while most things have to be typed in lowercase, guard names should always be written with a capital letter. For example: 'Jeff'." +
    "\n\nIf you are stuck try examining everything you can and in some cases also the command 'use' (followed by an item) will get you forward in the game. \nIf all else fails look in your spells to find something that might work." 
    
  }
  
  
 
  //inventory
  
  def take(itemName: String): String = {    //command to pick up an item. previously 'get'. also works with verb "get" as defined in class Action
    if (this.location.itemType(itemName) == Some(false))
      "You can't pick up that item." //add text depending on item
    else if (this.location.itemType(itemName) == Some(true)) {
      this.playerItems += itemName -> location.removeItem(itemName).get
      "You pick up the " + itemName + "."
    } else "There is no " + itemName + " here to pick up."
  }
  
   def drop(itemName: String): String = {    //command drop
    if (this.has(itemName)){
      this.location.addItem(this.playerItems(itemName))
      this.playerItems -= itemName      
    "You drop the " + itemName + "."
    } else "You don't have that!"    
  } 

  def has(itemName: String): Boolean = !(this.playerItems.get(itemName) == None)  //checks if player has the item in their inventory
  
  def inventory: String = { //command inventory, lists the items the player is currently carrying
    val itemList = "You are carrying:\n" + this.playerItems.keys.mkString("\n")
    if (playerItems.isEmpty) "You are empty-handed." else itemList
  }
  
 
  
   
   
   
   
  //spellbook 
  
  def learn(spell: Spell): Unit = this.spellList += spell.name -> spell //adds a spell to the players list of spells
  
  def knows(spellName: String): Boolean = !(this.spellList.get(spellName) == None) //checks if player knows a given spell
  
  def spells: String = { //command to see a list of spells the player knows
    if (this.has("spellbook")){
      val spellList = "You know these spells:\n" + this.spellList.keys.mkString("\n") + "\nTo learn more about a spell and how to use it, type 'spellbook' followed by the name of the spell, \nYou can learn more spells by using the ones you already have."
      if (spellList.isEmpty) "Soemthing went wrong and you don't know any spells." else spellList
    } else "Pick up your spellbook first. Try looking on the desk..."
  }
  
  def study(spellName: String): String = { //command to look up a spell in spellbook   
    if (this.has("spellbook")){
      if (this.knows(spellName)) 
        "You look up the spell " + spellName + " in your spellbook:\n\n" + this.spellList.get(spellName).get.description   
      else "Type 'spells' to see a list of the spells you know."
    } else "Pick up your spellbook first. Try looking on the desk..."
  }
       
 
  
  
 //guards
  
  def isDetected: Boolean = !guardsFollowing.isEmpty || isInvisible // a boolean that is checked to see if the guardReport should be printed. see UI code
  
  def guardReport = { //a thing printed in the UI on every turn if the player has been detected by guards.
     var guardList = ""
    if (guardsFollowing.size == 1) guardList = "\nA guard is following you. You should try to shake him somehow. Guard: " + guardsFollowing.keys.mkString(", ")
    else if (guardsFollowing.size > 1) guardList = "\nThere are " + guardsFollowing.size + " guards after you. You should do something about this quickly. Guards: " + guardsFollowing.keys.mkString(", ")
    else if (isInvisible && !this.location.guardsOfArea.isEmpty) {
      
      if (manaVar >= 100){ // The manacost of spending a turn in an area with guards
         manaVar -= 100
        guardList = "You remain undetected by the guards. You spend 100 mana on staying hidden." + mana        
      } else {         
        guardList = "Your mana is too low to stay properly hidden. The invisibility fades." + mana
        this.isInvisible = false
      }
    } 
    guardList
  }
  
  def setFollowing(name: String, guard: Guard) = {
    guardsFollowing += name -> guard
    guardsDetected += name -> guard
  }
  
  def removeFollowing(guard: Guard) = guardsFollowing -= guard.name
 
  
  
  
  
 //misc
 
  

  def hasQuit = this.quitCommandGiven


  def location = this.currentLocation
  
 
  def go(direction: String): String = {
     val destination = this.location.neighbor(direction)
     this.currentLocation = destination.getOrElse(this.location) 
     if (direction == "anywhere") {
       guardsFollowing.values.foreach(_.returnToStart)
       guardsFollowing.clear }    
     if (destination.isDefined) "You go " + direction + "." else "You can't go " + direction + "."     
  }
  
 
   def run(direction: String): String = { //moves in a direction and shakes guards
     val destination = this.location.neighbor(direction)
     this.currentLocation = destination.getOrElse(this.location) 
     if (destination.isDefined) {
       if (destination.get.guardsOfArea.isEmpty) {         
         guardsFollowing.values.foreach(_.returnToStart)
         guardsFollowing.clear
         "You run " + direction + " really fast. If any guards were following you they lost you." 
       } else "You ran right into some guards!" //maybe a fail criteria  NOT IMPLEMENTED          
     } else "You can't run " + direction + "."
  }

  
  /** Causes the player to rest for a short while (this has no substantial effect in game terms).
    * Returns a description of what happened. */
  def rest(): String = { //command rest. regenerates mana if successfull
    var result = ""
    if (this.guardsFollowing.isEmpty){
      var originalMana = manaVar
      var mana500 = roll.nextInt(200) + roll.nextInt(200) + roll.nextInt(200) + roll.nextInt(200) + roll.nextInt(200) 
      if (manaVar + mana500 > 1000) manaVar = 1000
      else manaVar += mana500
      result += "You take a break and rest for a while. You regenerated " + (manaVar - originalMana) + " mana. \nYour mana is now: " + manaVar
      if (!guardsDemoralized.isEmpty && roll.nextInt(1) == 0)  {
        val guard = this.guardsDemoralized.keys.last
        result += "\nOh no, you took too long to rest and guard " + guard + " has regained his morale and returned to his patrol location."
        this.guardsDemoralized.values.last.returnToStart //adds the guard to the patrol location
        if (demoralizedGuardLocations.contains(guard)) demoralizedGuardLocations(guard).removeItem(guard) //if guard was demoralized when following player, this deletes the guard-item that was created
        else this.guardsDemoralized(guard).startingLocation.removeItem(guard) //otherwise guard-item is removed from guards patrol location
        this.guardsDemoralized.remove(guard) //guard is finally removed from the map of demoralized guards        
      }
    } else result += "You cannot rest when there are guards following you!"
    result
  }
 
  
  //command quit
  def quit() = {
    this.quitCommandGiven = true
    ""
  }
//used to return a description of the players current mana after spells are cast
  def mana: String = if (manaVar < 250) "\nYour current mana is: " + manaVar.toString + "\nYour mana is low! Rest to regenerate mana." else "\nYour current mana is: " + manaVar.toString
  
  //error message if the player types 'cast x' where x can be anything
  def cast(anything: String) = "Cast spells by typing the spell name as a verb. Some spells use modifiers. Type 'spellbook <spellname>' to recieve info on a specific spell."
  

  //returns a map of the guards in the neighboring area. if the area is empty, returns an empty map
  def guardsAt(direction: String) = if (location.neighbor(direction).isDefined) location.neighbor(direction).get.guardsOfArea else Map[String, Guard]()

  
  //SPELLS
  
  //run for every spell casts. general error messages are returned from here
  def cast(spell: Spell): String = {
    if (!this.knows(spell.name)) {
      this.castSuccess = false
      "You don't know that spell yet."    
    } else if (!this.has("spellbook")){ 
      this.castSuccess = false
      "You should pick up your spellbook before you can cast something."    
    } else if (isInvisible && spell == invisibility){  
      this.castSuccess = true     
      "You cast " + spell.name + " again."
    } else if (manaVar < spell.manaCost) {
      this.castSuccess = false
      "You are out of mana! Try resting."    
    } else if (spell == compel && this.location.name != "Cell Door" && this.location.name != "Breakroom") {
      this.castSuccess = false
      "There is no use for that in this situation."    
    } else {  
      this.castSuccess = true
      this.manaVar -= spell.manaCost
      "You cast " + spell.name + " for " + spell.manaCost + " mana."
    }
     
  }
  
   def castAurasight(): String = {
    var guardList = ""
    var spellLearned = ""
    var result = ""
    var additionalGuards = Map[String, Guard]() //map for guards who are not following or in any neighbors but in neighbors that can be added to the room, such as behind locked doors   
    if (guardsFollowing.size == 1) guardList = "\nA guard is in this area with you! \nGuards: " + guardsFollowing.keys.mkString(", ")
    else if (guardsFollowing.size > 1) guardList = "\nThere are " + guardsFollowing.size + " guards in this area with you! \nGuards: " + guardsFollowing.keys.mkString(", ")
    result += this.cast(aurasight)
     if (castSuccess){
      castSuccess = false
      this.guardsDetected ++= this.guardsFollowing //creates a map of all known guards
      result += guardList   //constructs the final string
      if (!this.knows("demoralize")) {
        this.learn(demoralize)
        spellLearned = "\n\nYou learned a new spell! Type 'spellbook demoralize' for information about this spell."
      }
      if (this.location.name == "Outside") result += "\nYou sense the boss of this institution closing in! Her name is Gwen."
      if (isInvisible)  {
       this.guardsDetected ++= this.location.guardsOfArea
       if (this.location.guardsOfArea.size == 1) 
         result += "\nThere is one guard in the area you are in.\nGuard: " + this.location.guardsOfArea.keys.mkString(", ")
       else if (this.location.guardsOfArea.size > 1)
         result += "\nThere are " + this.location.guardsOfArea.size + " guards in the area you are in.\nGuards: " + this.location.guardsOfArea.keys.mkString(", ")
   }
      
      for (direction <- this.location.areaNeighbors) { //checks every direction neighbor for guards. this only checks the real neighbors
        this.guardsDetected ++= this.guardsAt(direction)          
        if (guardsAt(direction).size == 1) 
          result += "\nYou sense the presence of a guard in the direction: " + direction + "\nGuard: " + guardsAt(direction).keys.mkString(", ")
        else if (guardsAt(direction).size > 1) 
          result += "\nYou sense the presence of " + guardsAt(direction).size + " guards in the direction: " + direction + "\nGuards: " + guardsAt(direction).keys.mkString(", ")
        else result += "\nThere are no guards in the direction: " + direction        
      }
      for (hidden <- this.location.secretNeighborHolder.keys) { //checks the hidden neighbors if there are any
        if (hidden != "south2") { //ignores a specific case
          additionalGuards = this.location.secretNeighborHolder(hidden).guardsOfArea
          this.guardsDetected ++= additionalGuards         
          if (additionalGuards.size == 1) 
            result += "\nYou sense the presence of a guard in the direction: " + hidden + "\nGuard: " + additionalGuards.keys.mkString(", ") + "\n(Even though you cannot walk in this direction you can still target this guard with some spells.)"
          else if (additionalGuards.size > 1) //sometimes prints a 0 for some reason (?)
            result += "\nYou sense the presence of " + additionalGuards.size + " guards in the direction: " + hidden + "\nGuards: " + additionalGuards.keys.mkString(", ") + "\n(Even though you cannot walk in this direction you can still target these guards with some spells.)"
          else result += "\nThere are no guards in the direction: " + hidden  
        }
      }
      result += this.mana + spellLearned//finally returns the string when it has all the information that should be returned
    }
    result
      
  }
 
  private val thoughts = Vector[String](" is thinking about his family and wishes he was home.",
                                        " is thinking about the meaning of life.",
                                        " wishes it was friday.",
                                        " is debating whether he shoudld quit his job.",
                                        " thinks he is gettign paid too little.",
                                        " is currently thinking about a particulary attractive young blonde.",
                                        " hopes to get a raise in the near future.",
                                        " needs to go to the bathroom.",
                                        " hopes his shift was over sooner.",
                                        " thinks about how much he loves his job.",
                                        " imagines himself being a superhero.",
                                        " secretly admires Rick Astley.",
                                        " thinks about football.",
                                        " wishes he was home.",
                                        " is scared of you.",
                                        " is dreaming of buying a new car.")
   
  def castMindsight(target: String): String = {
    var spellLearned = "" 
    var result = ""     
      result += this.cast(mindsight)
      if (castSuccess){
        castSuccess = false
        if (roll.nextInt(5) == 0 && !this.knows("invisibility")) {
        this.learn(invisibility)
        spellLearned = "\n\nYou learned a new spell! Type 'spellbook invisibility' for information about this spell."
        }
        if (roll.nextInt(2) == 0 && !this.knows("compel")) {
        this.learn(compel)
        spellLearned = "\n\nYou learned a new spell! Type 'spellbook compel' for information about this spell."
        }        
        if (guardsDetected.contains(target)){
          if (!(this.location.name == "Breakroom")) 
            result += "\nYou cast mindsight on " + target + ".\n" + target + roll.shuffle(thoughts).head
          else 
            result += "\nYou cast mindsight on " + target + ".\n" + target + " knows the code to get outside is 7783."
          result += this.mana + spellLearned   
          result
        } else if (guardsDemoralized.contains(target)) {
          result += "\nYou cast mindsight on " + target + ". This target is demoralized and is dwelling on his failiure."
          result += this.mana + spellLearned
          result
        } else if (target == "Gwen") {
          result += "\nYou cast mindsight on " + target + ". \nGwen is the leader of this facility. She is a telepath just like you, she has just had a bit more training. \nShe uses her powers to control and lock down everyone who is like her in this facility."
          result += this.mana
          result
        } else {
          manaVar += mindsight.manaCost
          result = "You must target a guard by typing their name (with a capital letter!!) after 'mindsight'. To learn guards names use aurasight."
        }
      result
    } else result
  }
   
   
  def castDemoralize(target: String): String = {
    var spellLearned = "" 
    var result = ""     
      result += this.cast(demoralize)
      if (castSuccess){
        castSuccess = false
        if (roll.nextInt(2) == 0 && !this.knows("invisibility")) {
        this.learn(invisibility)
        spellLearned = "\n\nYou learned a new spell! Type 'spellbook invisibility' for information about this spell."
        }
        if (roll.nextInt(1) == 0 && !this.knows("compel")) {
        this.learn(compel)
        spellLearned = "\n\nYou learned a new spell! Type 'spellbook compel' for information about this spell."
        }
        val damage = roll.nextInt(10) + roll.nextInt(10) + roll.nextInt(10) + roll.nextInt(10) + roll.nextInt(10) + roll.nextInt(10) + roll.nextInt(10) + roll.nextInt(10) //rolls a number between 0 and 80 that is likely to be around 40
        if (guardsDetected.contains(target)){
          result += "\nYou do " + this.guardsDetected(target).damageMorale(damage) + " damage to " + target + "'s morale. \n" + target + "'s morale is now " + guardsDetected(target).morale + ". Try to get the guards morale to below zero to knock them out."
          if (guardsDetected(target).isDemoralized) { //checks if morale has gone below zero. if so, removes guard and creates a guard-item in the area.
            if (guardsFollowing.contains(target)) {
              demoralizedGuardLocations += target -> location
              guardsDemoralized += guardsFollowing(target).name -> guardsFollowing(target)
              guardsFollowing -= target
              this.location.addItem(new Item(target, "This guard is clearly demoralized and won't do anything for now.", true))
            } else {
              guardsDemoralized += guardsDetected(target).name -> guardsDetected(target)
              guardsDetected(target).startingLocation.removeGuard(target)
              guardsDetected(target).startingLocation.addItem(new Item(target, "This guard is clearly demoralized and wont do anything for now.", true))
              guardsDetected -= target
            }        
            result += "\n" + target + " is demoralized and won't pursue you any longer."
          } 
          result += this.mana + spellLearned           
        } else if (guardsDemoralized.contains(target)) {
          result = target + " is already demoralized!"
          manaVar += demoralize.manaCost
        }
        
        else if (target == "Gwen" || target == "gwen"){
          manaVar - demoralize.manaCost
          this.currentLocation = location.secretNeighborHolder("anywhere else")
          result = "You cast demoralize on Gwen for " + demoralize.manaCost + " mana. \nYou do 52 damage to Gwen's morale. \nGwen's morale is now -2. \nYou have successfully demoralized Gwen and she won't do anything to you now. You are free to go." + mana
        }
        else {
          result = "You must target a guard by typing their name (with a capital letter!!) after 'demoralize'. To learn guards names use aurasight."
          manaVar += demoralize.manaCost
        }
      result
    } else result
  }
  
  
 
  
  def castInvisibility: String = {
    var result = ""  
    result += this.cast(invisibility)
    if (castSuccess){
      castSuccess = false
      guardsFollowing.values.foreach(_.returnToStart)
      guardsFollowing.clear         
      if (!isInvisible) {
        isInvisible = true 
        result += " You are now invisible to all enemies in the area. \nIf any guards were following you they lost you and returned to their patrol location."
      } else {
        isInvisible = false
        result += " You are visible again."
      }          
      result += this.mana     
    }
    result
  }
  
  
  
  
  def castCompel(target: String): String = {
    var result = ""     
      result += this.cast(compel)
      if (castSuccess){
        castSuccess = false       
        if (guardsDetected.contains(target)){
          if (this.location.name == "Breakroom" && (target == "Ted" || target ==  "Frank" || target ==  "Sean" || target ==  "Greg")) {
            this.location.setNeighbor("south", location.secretNeighborHolder("south"))
            result += "\nYou compel " + target + " to type in the code and open the door for you."     
            result += mana
          } else if (this.location.name == "Cell Door") {
            this.location.setNeighbors(location.secretNeighborHolder.toVector)
            location.secretNeighborHolder.clear
            result += "\nYou compel " + target + " to open the door for you and let you out."  
            result += mana
          } else {
            result = "\n" + target + "cannot do anything for you right now." 
            manaVar += compel.manaCost
          }
        } else {
          result = "You must target a guard by typing their name (with a capital letter!!) after 'compel'. To learn guards names use aurasight."
          manaVar += compel.manaCost
        }
      result 
    } else result 
  }
  
  //ABILITIES
  
    
  def pick(lock: String) = {
    if (this.location.name == "Cell Door" && this.has("hairpin") && lock == "lock"){
      this.location.setNeighbors(location.secretNeighborHolder.toVector)
      location.secretNeighborHolder.clear
      "You use the hairpin to pick the lock in the door."      
    } else if (this.location.name != "Cell Door") "There is no lock here that you can pick."
    else if (this.location.name == "Cell Door" && !this.has("hairpin")) "You have nothing to pick the lock with. Try searching the room."
    else "Unknown command: \"" + "pick " + lock + "\"."
     
  }
  
  
  
  def use(something: String): String = {
    
    if (something == "hairpin") {
      if (this.location.name == "Cell Door" && this.has("hairpin")) {
        this.location.setNeighbors(location.secretNeighborHolder.toVector)
        "You use the hairpin to pick the lock in the door."
      } else if (this.location.name != "Cell Door") "There is no lock here to pick."
      else if (this.location.name == "Cell Door" && !this.has("hairpin")) "Pick up a hairpin at first!"
      else "Unknown command: \"" + "use " + something + "\"."           
    } 
    
    else if (something == "card") {
      if (!guardsFollowing.isEmpty && this.location.noteString == "Hallway on the first floor to the east." && this.has("card")) "You can't do that when guards are following you!"
      else if (this.location.noteString == "Hallway on the first floor to the east." && this.has("card")) {
      var result = "You use employee ID card to unlock the door."
        this.currentLocation = location.secretNeighborHolder("south")
        val manaRegenerated = 1000 - manaVar
        manaVar = 1000
        if (manaRegenerated > 0) result += "\nBefore you go through the door you make sure your mana is recharged. You regenerate " + manaRegenerated + " mana."       
        result += "\n\nYou walk through the door and it closes behind you."        
        result      
      } else if (this.location.name == "Breakroom"){ 
        if (!this.has("card")) "You don't have that!"
        else if (!guardsFollowing.isEmpty) "You can't do that when guards are following you!"
        else "You try to use the card on the door, but it appears you also need a 4 digit code. Type 'use 0000' when you know the code, replacing the zeros with the code."
      } else if (!(this.location.noteString == "Hallway on the first floor to the east.") && this.has("card")) "You can't use that here."
      
      else "Unknown command: \"" + "use " + something + "\"."          
    } 
    
     else if (something == "7783") {
       if (!this.has("card") && this.location.name == "Breakroom") "You type in the code but you have no ID card and you don't get through."
       else if (this.location.name == "Breakroom") {        
        this.location.setNeighbor("south", location.secretNeighborHolder("south"))
        "You type in the code and use the ID card to unlock the door."        
      } else "Unknown command: \"" + "use " + something + "\"."
    }
    
    else if (something == "bed") {
      if (this.location.name == "Small Bed") "You don't feel like sleeping. Type 'rest' to regenerate mana."
      else "Unknown command: \"" + "use " + something + "\"."
    }
    
    else if (something == "key") {
      "You can search the rooms now by typing 'examine doors'."
    }
    
   
     else if (something == "cigarettes") {
      "You don't smoke! Put those away!"
    }
    
    else if (something == "bomb" || (something == "lighter" && this.location.name == "Breakroom")) {
      if ((this.has("sheets") && this.has("bottle") && this.has("fuel") && this.has("lighter")) || this.has("bomb") && this.has("lighter"))
        if (this.location.name == "Breakroom") {
          this.currentLocation = location.secretNeighborHolder("south2")
          "You blow up the staff area and run outdoors."
         
        } else "Don't detonate the bomb until you are in the staff breakroom!"
      else "You don't have the requred materials: sheets, bottle, fuel and a lighter."
    }
    
    else if (something == "pen" && this.has("pen") && !this.has("notebook")){     
     "You don't have a notebook to write in!"
   }
    else if (something == "pen" && this.has("pen") && this.has("notebook")){     
     "You write the names of the guards you have encountered in the notebook. (These are actually added automatically...)"
   }
  
    else if (this.has(something)) "You can't use that."
    
    else "You don't have a " + something + "."
  }
  

  
  def make(bomb: String) = {
    if (bomb == "bomb" && this.has("sheets") && this.has("bottle") && this.has("fuel")) {
      this.playerItems -= "sheets"
      this.playerItems -= "bottle"
      this.playerItems -= "fuel"
      this.playerItems += "bomb" -> new Item("bomb", "You made this bomb, detonate it in the staff breakroom. Make sure you bring a lighter.", true)
      "You create a bomb. You crazy bastard."
    } else "You cant make a " + bomb
  }
  
  
  
  def examine(itemName: String): String = { //command to examine an item. does different things depending on what is examined:
    
    if (this.has(itemName)) { //checks if the item is in players inventory. if this is the case the item is an existing true item and the player is able to examine it
      if (itemName == "notebook" && this.has("pen")) {
        var guardNote = ""
        for (guard <- (guardsDetected ++ guardsDemoralized).values){
          guardNote += "Name: " + guard.name + "            "
          guardNote
          guardNote += "Patrol Location: " + guard.startingLocation.noteString
          
          if (guardsDemoralized.contains(guard.name)) guardNote += "         DEMORALIZED\n" else guardNote += "\n"
        }
        "You look closely at the " + itemName + ".\n" + "This is a notebook. With the pen you write down all the names of the guards you encounter:\n" + guardNote + "\nOn the last page there is a drawing over the facility you are currently in. \n" + 
                                                                                                                                                                                                         "\n3: Stairway - Hallway - Hallway - Stairway" + 
                                                                                                                                                                                                         "\n      |                               |   " +
                                                                                                                                                                                                         "\n      |         Cell                  |   " +
                                                                                                                                                                                                         "\n2: Stairway - Hallway - Hallway - Stairway" +
                                                                                                                                                                                                         "\n      |                               |   " +
                                                                                                                                                                                                         "\n      |                               |   " +
                                                                                                                                                                                                         "\n1: Stairway - Hallway - Hallway - Stairway" +
                                                                                                                                                                                                         "\n                          Out             "
      } else "You look closely at the " + itemName + ".\n" + this.playerItems.get(itemName).get.description   
    } else if (this.location.itemType(itemName) == Some(true)) { // if the item isnt in the players inventory but it is a true item (can be picked up) it needs to be picked up to be examined
      "If you want to examine that, you need to pick it up first."
    } else if (this.location.itemType(itemName) == Some(false)) {   //if the item is a false item and is in the location it can be examined. some of these make the player find additional items, and those are added to the area here   
      if (guardsDemoralized.contains(itemName)) {  //if the player is searching a guard they have a chance of finding these items          
        guardsSearched += 1
        if (guardsSearched == 1 || !this.has("key")) this.location.addItem(new Item("key", "This is a key that works on the cell doors.", true))
        if (guardsSearched == 3) this.location.addItem(new Item("card", "This is an employee ID card. Maybe it can be used to access restricted areas of the facility.", true))
        if (this.location.name == "Breakroom") this.location.addItem(new Item("memo", "This is a piece of paper with a 4 digit code on it: 7783", true))       
        if (roll.nextInt(1) == 0 && !this.has("lighter")) this.location.addItem(new Item("lighter", "This is a lighter. It appears to be out of fuel. (Damn Zippo...)", true))
        if (roll.nextInt(1) == 0 && !this.has("coins")) this.location.addItem(new Item("coins", "What do you wanna know? This is just some money you found in a guards pocket. You could probably do somethign with it if you weren't in prison.", true))
        if (roll.nextInt(5) == 0 && !this.has("picture")) this.location.addItem(new Item("picture", "A family portrait. You don't know these people but the man looks vaguely familiar...", true))
        if (roll.nextInt(3) == 0 && !this.has("gum")) this.location.addItem(new Item("gum", "Chewing gum. Looks like it's been in a sweaty pocket for a while.", true))
        if (roll.nextInt(3) == 0 && !this.has("pencil")) this.location.addItem(new Item("pencil", "A broken pencil. Hardly useful.", true))
        if (roll.nextInt(4) == 0 && !this.has("cigarettes")) this.location.addItem(new Item("cigarettes", "Cigarettes. You don't smoke, you should probably just toss these.", true))
        if (roll.nextInt(1) == 0 && !this.has("card") && guardsSearched > 3) this.location.addItem(new Item("card", "This is an employee ID card. Maybe it can be used to access restricted areas of the facility.", true))
        "You search " + itemName + ".\n" + location.itemDescription(itemName).get 
      } else { 
        if (itemName == "desk") { //this list items that the player finds when examining desk
          
          location.addItem(new Item("notebook", "This is a notebook. It's mostly empty but on the last page there is drawn map over the facility you are currently in.  \n" + 
                                                                                                                             "\n3: Stairway - Hallway - Hallway - Stairway" + 
                                                                                                                             "\n      |                               |   " +
                                                                                                                             "\n      |         Cell                  |   " +
                                                                                                                             "\n2: Stairway - Hallway - Hallway - Stairway" +
                                                                                                                             "\n      |                               |   " +
                                                                                                                             "\n      |                               |   " +
                                                                                                                             "\n1: Stairway - Hallway - Hallway - Stairway" +
                                                                                                                             "\n                          Out             ", true))
          location.addItem(new Item("pen", "This is a pen. You can use this to write down names of guards you encounter if you have a notebook.", true))
        }
        if (itemName == "bed") location.addItem(new Item("hairpin", "This is just a simple hairpin, but maybe it can be used to escape?", true))
        
        if (itemName == "door" && this.location.name == "Cell Door") {
          location.addItem(new Item("lock", "This lock could be picked quite easily, provided that you have something to pick it with.", false))
          if (!this.location.neighbor("south").contains("Jeff")) location.addItem(new Item("guard", "There appears to be a guard stationed outside. Maybe a spell would help you identify him?", false))
          else location.addItem(new Item("guard", "There appears to be a demoralized guard outside. It should be safe to exit now.", false))
        }
        if ((itemName == "doors" || itemName == "rooms") && this.has("key")) {
          roomsSearched += 1
          if (roomsSearched == 2) this.location.addItem(new Item("journal", "You found a journal! You look through it and find instructions for a bomb: \n\nObtain a bottle, bedsheets and some flammable liquid. Use a guards lighter to light the bomb in employee room. \n(The owner of this journal must have been kind of crazy.)", true))
          if (roomsSearched == 4) this.location.addItem(new Item("paper", "This is and old, scrunched up piece of paper. You can hardly make out any works on it but it seems to say: 'Demoralize HER'", true))
          if (this.location.name == "Breakroom") this.location.addItem(new Item("memo", "This is a piece of paper with a 4 digit code on it: 7783", true))       
          if (roll.nextInt(3) == 0 && !this.has("fuel")) this.location.addItem(new Item("fuel", "This is a strange container, on closer inspection it contains some sort of fuel much like lighter fluid.", true))
          if (roll.nextInt(5) == 0 && !this.has("dust")) this.location.addItem(new Item("dust", "Why would you pick up dust and inspect it. Ew.", true))
          if (roll.nextInt(5) == 0 && !this.has("art")) this.location.addItem(new Item("art", "A family portrait. You don't know these people but the man looks vaguely familiar...", true))
          if (roll.nextInt(5) == 0 && !this.has("iPod")) this.location.addItem(new Item("iPod", "Your escape will be significantly better with some ambient music int he background. Unfortunately the batteries are empty and this iPod won't help with that.", true))
          if (roll.nextInt(4) == 0 && !this.has("prisoner")) this.location.addItem(new Item("prisoner", "There was another prisoner in the cell! Unfortunately for you they don't seem to even realize you're there but just stare blankly at the wall. \nThis prisoner won't be of any help.", false))
          if (roll.nextInt(3) == 0 && !this.has("snacks")) this.location.addItem(new Item("snacks", "You shouldn't eat these. They seem stale.", true))
          if (roll.nextInt(3) == 0 && !this.has("sheets")) this.location.addItem(new Item("sheets", "Maybe if you're feeling cold you can use these to wrap yourself in? No, seriously. Come up with something more creative.", true))
          if (roll.nextInt(3) == 0 && !this.has("bottle")) this.location.addItem(new Item("bottle", "This is an empty bottle. Who knows, maybe you could find some use for it.", true))
          if (roll.nextInt(3) == 0 && !this.has("knife")) this.location.addItem(new Item("knife", "You think you found a knife and can now hurt guards. Nope, sorry, this is actually a stiletto comb.", true))
          if (roll.nextInt(3) == 0 && !this.has("pencil")) this.location.addItem(new Item("pencil", "A broken pencil. Hardly useful.", true))
          if (roll.nextInt(4) == 0 && !this.has("cigarettes")) this.location.addItem(new Item("cigarettes", "Cigarettes. You don't smoke, you should probably just toss these.", true))
          if (roll.nextInt(5) == 0 && !this.has("lighter")) this.location.addItem(new Item("lighter", "This is a lighter. It appears to be out of fuel. (Damn Zippo...)", true))
          if (roll.nextInt(2) == 0 && !this.has("diary")) this.location.addItem(new Item("diary", "The previous owner wrote something in the diary: \n'This facility is crazy. I wish there was a way to explain it but there isn't. \nI have made it out more times than I can count, and each time she comes up and in a snap I'm back where I started. \nIf there was ANYTHING I could do just *before* I run into her I would be out of here... Maybe a spell or something.' \nYou get a weird feeling that this person's handwriting is very much like your own. You quickly close the diary and go on about your business.", true)) 
          
          
          "You search the rooms and find some things. Maybe something can be of use."
        }
        else "You look closely at the " + itemName + ".\n" + location.itemDescription(itemName).get
      } 
    } else "That item isn't here."
  
  }
  
  /** Returns a brief description of the player's state, for debugging purposes. */
  override def toString = "Now at: " + this.location.name   


}


