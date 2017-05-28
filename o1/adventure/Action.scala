package o1.adventure


/** The class `Action` represents actions that a player may take in a text adventure game.
  * `Action` objects are constructed on the basis of textual commands and are, in effect, 
  * parsers for such commands. An action object is immutable after creation.
  *
  * @param input  a textual in-game command such as "go east" or "rest"
  */
class Action(input: String) {

  private val commandText = input.trim//.toLowerCase
  private val verb        = commandText.takeWhile( _ != ' ' )
  private val modifiers   = commandText.drop(verb.length).trim

  
  /** Causes the given player to take the action represented by this object, assuming 
    * that the command was understood. Returns a description of what happened as a result 
    * of the action (such as "You go west."). The description is returned in an `Option` 
    * wrapper; if the command was not recognized, `None` is returned. */
  def execute(actor: Player) = {                             

    if (this.verb == "go") {
      Some(actor.go(this.modifiers))
    } else if (this.verb == "take" || this.verb == "get") {
      Some(actor.take(this.modifiers))
    } else if (this.verb == "examine" || this.verb == "search") {
      Some(actor.examine(this.modifiers))
    } else if (this.verb == "drop") {
      Some(actor.drop(this.modifiers))
    } else if (this.verb == "use") {
      Some(actor.use(this.modifiers))
    } else if (this.verb == "rest") {
      Some(actor.rest())
    } else if (this.verb == "help") {
      Some(actor.help())
    } else if (this.verb == "run") {
      Some(actor.run(this.modifiers))   
    } else if (this.verb == "mana") {
      Some(actor.mana)
    } else if (this.verb == "inventory") {
      Some(actor.inventory)    
    } else if (this.verb == "spells") {
      Some(actor.spells)
    } else if (this.verb == "spellbook") {
      Some(actor.study(this.modifiers))      
    } else if (this.verb == "aurasight") {    //spell
      Some(actor.castAurasight())
    } else if (this.verb == "mindsight") {    //spell
      Some(actor.castMindsight(this.modifiers))
    } else if (this.verb == "compel") {       //spell
      Some(actor.castCompel(this.modifiers))
    } else if (this.verb == "demoralize") {   //spell
      Some(actor.castDemoralize(this.modifiers))
    } else if (this.verb == "invisibility") { //spell
      Some(actor.castInvisibility)
    } else if (this.verb == "quit") {
      Some(actor.quit())
    } else if (this.verb == "cast") { //error message that explains how to cast (note there are two 'cast' methods in class Player)
      Some(actor.cast(this.modifiers))
    } else if (this.verb == "make") { //for making a bomb
      Some(actor.make(this.modifiers))
    } else if (this.verb == "pick") { //for picking a lock
      Some(actor.pick(this.modifiers))
    } else if (this.verb == "notebook") { //for opening notebook without 'examine'
      Some(actor.examine("notebook"))
    } else {
      None
    }
    
  }


  /** Returns a textual description of the action object, for debugging purposes. */
  override def toString = this.verb + " (modifiers: " + this.modifiers + ")"  

  
}

