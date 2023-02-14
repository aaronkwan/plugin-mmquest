# plugin-mmquest
A simple back-end moderation tool for the server Monumenta utilizing Paper and CommandAPI.

# about
- **Goal**: I created this plugin as a way to introduce myself to the world of Java programming. The Minecraft server Monumenta has been one of my favorite pieces of content, and what better way to get started than to build something for a cause that I'm passionate about?

- **Challenges**: Most of my time was spent with setup: debugging the editor, server, and dependencies. I first tried writing with the visual studio code java extention pack, but there were a number of issues. With the help of some CommandAPI and Monumenta-plugin developers, I was able to install the required dependencies & shading. After much trial and error, I was finally able to start writing code!

# features
- **Note**: This is still a prototype with only 4 "quests", however it is easily scalable to encompass the entire content.

<p> Quest scores in Monumenta are scoreboard objectives assigned to keep track of a player's progress in a custom content. Quests usually have a custom name (*eg: "A Crown of Topaz"*). In addition, their progress is tracked by a numerical value inside a scoreboard (*eg: a Quest01 score of 10*). When (us) moderators attempt to troubleshoot a player's Quest progress, one of the first things we do is check their Quest scoreboard value.</p>

<p> The only problem is that (unless you have photographic memory), it is impossible to know which scoreboard corresponds to which Quest, nor which scoreboard value corresponds to what progress. Finding this information involves logging into the moderator wiki and authenticating through discord (safety measure), wasting precious minutes</p>

<p> What if there was a way to all of this in-game? </p>

- **Usage**: /mmquest <Player> <QuestName>

<p> /mmquest (or /mmq) allows you to view the progress of a Quest for a specific player. </p>

1. ![image](https://user-images.githubusercontent.com/123356351/218684147-86b6012d-6888-43e2-94a4-20d8ec2e4980.png)

2. ![image](https://user-images.githubusercontent.com/123356351/218684290-053af5b6-286e-4f0b-a7e7-ce4ca975ed9f.png)
3. You can hover and click on any Required Quests to also view their progress. ![image](https://user-images.githubusercontent.com/123356351/218684358-3f1de946-6329-4328-ad03-65e5b7df3571.png)

4. Hovering over any [number] value lists a short description of that value: ![image](https://user-images.githubusercontent.com/123356351/218684613-4deab579-5eaf-43ef-a969-8ea89274f50b.png)

5. Click the [number] value to set the player's score to that value.
6. The number associated with their score is highlighted! ![image](https://user-images.githubusercontent.com/123356351/218684682-63792b26-a482-450b-ae51-32db40802f0f.png)

7. Up to two different quest completion values are supported. For example, "Bandit Troubles" has both a good and a bad ending, which both show a green **Complete** near the top: ![image](https://user-images.githubusercontent.com/123356351/218682643-ca8d53dd-4a4c-41ca-9b44-31d1305a5ed0.png)
8. And: ![image](https://user-images.githubusercontent.com/123356351/218682716-714dda54-0645-47e9-b892-113266cbfd70.png)
9. A incomplete quest has no **Complete** near the top, and shows red if it is a requirement for another quest: ![image](https://user-images.githubusercontent.com/123356351/218683203-be66bc2c-830c-4181-b12f-2cb7ddd34669.png)
10. If there is no scoreboard objective, there will be a message: ![image](https://user-images.githubusercontent.com/123356351/218683469-b1cb0ea0-534e-42b0-bb53-20abeba91812.png)
11. Finally, if you didn't enter a valid Quest Name: ![image](https://user-images.githubusercontent.com/123356351/218683651-95b19c56-b6fe-49a9-af39-1eea64d7588d.png)

