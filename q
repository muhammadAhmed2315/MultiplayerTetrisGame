[33mcommit 8c74d81164f98ef92f6df3b09bdca2a855b7f367[m[33m ([m[1;36mHEAD[m[33m -> [m[1;32mmain[m[33m, [m[1;31morigin/main[m[33m, [m[1;31morigin/HEAD[m[33m)[m
Author: muhammadAhmed2315 <muhammad.ahmed2315@gmail.com>
Date:   Wed Apr 3 14:34:29 2024 +0100

    Added another ScoresList in ScoresScene which shows the scores retrieved from the online server

[33mcommit 2a578d9d300eafa0e2bef3fc5fa0c94207412d28[m
Author: muhammadAhmed2315 <muhammad.ahmed2315@gmail.com>
Date:   Wed Apr 3 13:26:23 2024 +0100

    If the user gets a top 10 high score, they are now prompted to enter their name in ScoresScene and their name and score is shown on the high scores list and also saved to the local file. Added a high score component in ChallengeScene that changes when the user beats the high score.

[33mcommit fd308529fe84e91264bec8e5d348b240a7cae105[m
Author: muhammadAhmed2315 <muhammad.ahmed2315@gmail.com>
Date:   Tue Apr 2 04:09:06 2024 +0100

    Created a ScoresScene, which is automatically switched to at the end of a game. Added functionality to read in scores from a text file. Created a ScoresList custom component (barebones version) to display the high scores. ScoresList component changes when local scores are changed.

[33mcommit 8207b6558fe87a66a3d746b2f1e081534e767caf[m
Author: Muhammad Ahmed <muhammad.ahmed2315@gmail.com>
Date:   Tue Apr 2 00:14:04 2024 +0100

    TEMP COMMIT

[33mcommit afac2c7b3857d5156b9c977035a69c87d6853252[m
Merge: bf8f141 acfb2f6
Author: Muhammad Ahmed <muhammad.ahmed2315@gmail.com>
Date:   Mon Apr 1 15:47:56 2024 +0100

    Merge remote-tracking branch 'origin/main'

[33mcommit bf8f141bee967286ab854b1b8ca37c7101dcdc27[m
Author: Muhammad Ahmed <muhammad.ahmed2315@gmail.com>
Date:   Mon Apr 1 15:46:06 2024 +0100

    Added a timer bar in ChallengeScene.java, which decreases in size and changes colour. Game returns to the main menu when lives < 0. Added sound effects for when lives are lost and the game ends. Added a GameLoopListener class to link the game timer with the UI timer bar.

[33mcommit acfb2f64fd1b23edb63e71e7da734176e3e18a1b[m
Author: Muhammad Ahmed <muhammad.ahmed2315@gmail.com>
Date:   Mon Apr 1 15:46:06 2024 +0100

    Added a timer bar in ChallengeScene.java, which decreases in size and changes colour. Game returns to the main menu when lives < 0. Added sound effects for when lives are lost and the game ends.

[33mcommit 93b1a99a66adf07498d3aedf254ddabcc94d5a6f[m
Author: Muhammad Ahmed <muhammad.ahmed2315@gmail.com>
Date:   Mon Apr 1 01:18:14 2024 +0100

    Implemented a game timer in Game.java. Time running out decreases the game level.

[33mcommit 23f236165f34fddce017b0cc32030d78dc77573d[m
Author: Muhammad Ahmed <muhammad.ahmed2315@gmail.com>
Date:   Mon Apr 1 00:32:48 2024 +0100

    Added comments to many classes and to show an indicator on the middle square of the PieceBoard that shows the current piece

[33mcommit 561a5c3e58443f74225346d24be527870e5e2714[m
Merge: d6c5bb5 1ea9dfd
Author: Muhammad Ahmed <muhammad.ahmed2315@gmail.com>
Date:   Sun Mar 31 21:31:01 2024 +0100

    Merge remote-tracking branch 'origin/main'

[33mcommit d6c5bb5246595bb5a96a105cf344a28bd8fe7c17[m
Author: Muhammad Ahmed <muhammad.ahmed2315@gmail.com>
Date:   Sun Mar 31 21:30:05 2024 +0100

    Added a green flashing fading effect for when any lines are cleared

[33mcommit 1ea9dfdbe61e5dd15fda630b17a183b47c7f0854[m
Author: Muhammad Ahmed <muhammad.ahmed2315@gmail.com>
Date:   Sun Mar 31 21:30:05 2024 +0100

    Added a green flashing fading effect for when lines are cleared

[33mcommit 62af3af92e81473289e5a193089a7f432419c018[m
Author: Muhammad Ahmed <muhammad.ahmed2315@gmail.com>
Date:   Sun Mar 31 15:24:34 2024 +0100

    Added hovering effects for the main GameBoard

[33mcommit 98df47fe49dbc10236b725e93a880b472959559c[m
Author: Muhammad Ahmed <muhammad.ahmed2315@gmail.com>
Date:   Sun Mar 31 14:41:18 2024 +0100

    Updated the GameBlock class so that empty grid squares and painted grid squares look much prettier

[33mcommit e9ebe21819ff58b3353069d926df9aee340cec92[m
Author: Muhammad Ahmed <muhammad.ahmed2315@gmail.com>
Date:   Sun Mar 31 14:14:37 2024 +0100

    Added sound effects to the game, and added functions to the MultiMedia class that automatically stop the currently running MediaPlayer and then play a new sound to prevent two sounds playing at once

[33mcommit d5f840989a3522f8cb8fcea450c181ead9fb10f5[m
Author: Muhammad Ahmed <muhammad.ahmed2315@gmail.com>
Date:   Sun Mar 31 02:18:04 2024 +0100

    Added next piece logic, a separate PieceBoard for the next piece, as well as functionality that allows the user to rotate the current piece or swap between the current piece and the next piece

[33mcommit f13d4aa7ec9378debecc58cd878a8e5e1d5fc1ef[m
Author: Muhammad Ahmed <muhammad.ahmed2315@gmail.com>
Date:   Sat Mar 30 18:56:30 2024 +0000

    Added keyboard listeners to InstructionsScene and MenuScene, so that those scenes can be exited from and the user can enter back into the menu

[33mcommit bb92f79934f13b76233b2fdb52fa12a6cf9bf30f[m
Author: Muhammad Ahmed <muhammad.ahmed2315@gmail.com>
Date:   Sat Mar 30 18:41:33 2024 +0000

    Removed a single TODO comment

[33mcommit fdf5e4f1938d22ff5d485949005eaa0be1cadc3e[m
Author: Muhammad Ahmed <muhammad.ahmed2315@gmail.com>
Date:   Sat Mar 30 18:41:07 2024 +0000

    Added 15 dynamically generated pieces to the bottom of InstructionScene

[33mcommit 67e3581a5c815598ff24ecb3e18b45b1112f3961[m
Author: Muhammad Ahmed <muhammad.ahmed2315@gmail.com>
Date:   Sat Mar 30 18:21:34 2024 +0000

    Created an InstructionScene, finished all apart from the 15 pieces at the bottom

[33mcommit 1fba44d40a2c4962968406fa28c25bff0f4369b3[m
Author: Muhammad Ahmed <muhammad.ahmed2315@gmail.com>
Date:   Sat Mar 30 16:10:47 2024 +0000

    Added four basic buttons in the menu screen, and added functionality for two of those buttons (singlePlayerButton and exitButton)

[33mcommit ecca904e754e3975082af8dd5c89a9283c4f299f[m
Author: Muhammad Ahmed <muhammad.ahmed2315@gmail.com>
Date:   Sat Mar 30 15:54:01 2024 +0000

    PieceBoard in ChallengeScene now shows the next piece the user can place

[33mcommit 86aa2cd40f17d9980124716e8cc6c4b69abd70c0[m
Author: Muhammad Ahmed <muhammad.ahmed2315@gmail.com>
Date:   Sat Mar 30 15:16:51 2024 +0000

    Created a PieceBoard component and added an instance of it to the right bar of the ChallengeScene (to show the next piece)

[33mcommit 79ede89ec5ebf6acafa61ca3b059f6e8b2298172[m
Author: Muhammad Ahmed <muhammad.ahmed2315@gmail.com>
Date:   Sat Mar 30 14:54:27 2024 +0000

    Created the Multimedia class, and implemented background music in the MenuScene and ChallengeScene

[33mcommit 1f2d8be6fd2718f08c87385cf1360e7f0a1b35b6[m
Author: Muhammad Ahmed <muhammad.ahmed2315@gmail.com>
Date:   Sat Mar 30 14:04:54 2024 +0000

    Implemented the level logic

[33mcommit 0a63bdb43f5248930097faa3c5e652f39b8ca7d3[m
Author: Muhammad Ahmed <muhammad.ahmed2315@gmail.com>
Date:   Sat Mar 30 14:00:42 2024 +0000

    Implemented the score multiplier logic

[33mcommit 6c70430b37ee3d95047ac533f94cad0620fb6069[m
Author: Muhammad Ahmed <muhammad.ahmed2315@gmail.com>
Date:   Sat Mar 30 02:30:42 2024 +0000

    Updated afterPiece() in Game so that the score is incremented by the newly calculated score, and fixed the score, lives, level, and multiplier components in ChallengeScene so that they're properly bound and actually update to their proper values from Game

[33mcommit a07d1755517eca190fbb584ad0d5aff7de210b61[m
Author: Muhammad Ahmed <muhammad.ahmed2315@gmail.com>
Date:   Sat Mar 30 01:59:26 2024 +0000

    Finished implementing the calculateScore method and added it to the afterPiece class

[33mcommit 2cb20c1a64c6d9b829b84294a042691ec970a85d[m
Author: Muhammad Ahmed <muhammad.ahmed2315@gmail.com>
Date:   Sat Mar 30 01:53:18 2024 +0000

    Added basic UI elements to show score, level, multiplier, and lives in the ChallengeScene, and binded their values to the properties in Game

[33mcommit 68832448c29493aeebc7038f63d0f2321e4a9585[m
Author: Muhammad Ahmed <muhammad.ahmed2315@gmail.com>
Date:   Fri Mar 29 20:57:49 2024 +0000

    Added bindable properties for the score, level, lives, and multiplier to the Game class, with appropriate accessor methods

[33mcommit 87d5c6c7009b786dd022ed3e96ad8cf0461c55e8[m
Author: Muhammad Ahmed <muhammad.ahmed2315@gmail.com>
Date:   Fri Mar 29 20:51:58 2024 +0000

    Completely finished Game Logic section, including the line clearing logic

[33mcommit f10b47c2e36928fff4b74751c38a1a82da97a80d[m
Author: Muhammad Ahmed <muhammad.ahmed2315@gmail.com>
Date:   Fri Mar 29 19:56:25 2024 +0000

    Finished Game Logic section up to (but not including) "Add an afterPiece method, and add logic to handle the clearance of lines"

[33mcommit 258e740ae4b0690a958a2d9971a9aa70e0025ebd[m
Author: Oli <git@games-creation.com>
Date:   Tue Mar 28 16:24:24 2023 +0100

    Lower Java version to 17

[33mcommit 0cccefdb9ccad85f48dada9629888297fcccbdf5[m
Author: Oli <git@games-creation.com>
Date:   Fri Mar 10 10:58:11 2023 +0000

    Updated for 2023
