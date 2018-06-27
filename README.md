# Battleship
A game of Battleship written in java that can be played between two players
over a network connection. 

NOTE:
This was not an individual project. This was a final GROUP project for CPSC 329 at Hobart and William Smith Colleges.

# Directions
For a game of battleship to be played between two players over a network, each user must first have downloaded
the all of the files provided.
- First, one user must be the host, and run `ServerMain.java` on their machine.
- Next, both users will run `ClientMain.java`, and select player when prompted to choose what version of game to play.
- For the user who is the host, when prompted to enter an IP address, will use `localhost`
- For the user who is not the host, when prompted to enter an IP address, will use IP address of host machine.
- From here, both users will have their game windows appear. They will be prompted to place their ships, the number 
of which will be indicated in the chat box. After all ships are placed by each player, the chat box will indicate whose 
turn it is. 
- Messages can be sent between players, but will only be sent after the player whose turn it is makes a move.
