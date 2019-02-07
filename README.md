# NetworkProgramming

This repository contains the exercises and the final project from Network Programming.

The final project is in the folder "EchoChamber". It can be easily imported and run in Netbeans. Although it fulfills the different goals, there are some improvements that could be done:
- Front-end:
  - Since the project is focused in WebSockets and not in front-end design, the front-end contains absolutely no CSS, so obviously no one would say it is beautiful.
- Back-end:
  - When a new user joins the room, the chosen nick is not compared against existing ones. Thus, there can be several users with the same username, which can lead to undefined behaviour.