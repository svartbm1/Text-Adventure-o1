package o1.adventure

class Item(val name: String, val description: String, val itemType: Boolean) {
  //true items can be picked up, and have to be picked up to be examined, false items cannot be picked up but can be examined in the area
  
  /** Returns a short textual representation of the item (its name, that is). */
  override def toString = this.name
  

}


/*
This is a notebook. It's mostly empty but on the last page there is a drawing over the facility you are currently in. 

Stairway - Hallway - Hallway - Stairway
   |                               |   
   |         Cell                  |
Stairway - Hallway - Hallway - Stairway
   |                               |   
   |                               |   
Stairway - Hallway - Hallway - Stairway
                       Out             

*/