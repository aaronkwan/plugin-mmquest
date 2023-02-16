# plugin-mmquest
A simple back-end moderation tool for the server Monumenta utilizing Paper and CommandAPI.

# about
- **Goal**: I created this plugin as a way to introduce myself to the world of Java programming. The Minecraft server Monumenta has been one of my favorite pieces of content, and what better way to get started than to build something for a cause that I'm passionate about?

- **Challenges**: Most of my time was spent with setup: debugging the editor, server, and dependencies. I first tried writing with the visual studio code java extention pack, but there were a number of issues. With the help of some CommandAPI and Monumenta-plugin developers (and ChatGBT :o), I was able to install the required dependencies & shading. After much trial and error, I was finally able to start writing code!

# features
- **Note**: This is still a prototype with only 4 "quests", however it is easily scalable to encompass the entire content.

<p> Quest scores in Monumenta are scoreboard objectives assigned to keep track of a player's progress in a custom content. Quests usually have a custom name (*eg: "A Crown of Topaz"*). In addition, their progress is tracked by a numerical value inside a scoreboard (*eg: a Quest01 score of 10*). When (us) moderators attempt to troubleshoot a player's Quest progress, one of the first things we do is check their Quest scoreboard value.</p>

<p> The only problem is that (unless you have photographic memory), it is impossible to know which scoreboard corresponds to which Quest, nor which scoreboard value corresponds to what progress. Finding this information involves logging into the moderator wiki and authenticating through discord (safety measure), often wasting precious minutes.</p>

<p> What if there was a way to do all of this in-game? </p>

- **Usage**: /mmquest <Player> <QuestName>

<p> /mmquest (or /mmq) allows you to view or set the progress of a Quest for a specific player. </p>

1. You can directly set the progress of a quest: ![image](https://user-images.githubusercontent.com/123356351/219274555-fb4de1d4-d57f-4b9d-a480-f9f485aa56e4.png)

1. Or, you can simply view the progress of a quest: ![image](https://user-images.githubusercontent.com/123356351/218684147-86b6012d-6888-43e2-94a4-20d8ec2e4980.png)
2. ![image](https://user-images.githubusercontent.com/123356351/219274082-0e9a5c9e-ee89-4173-9342-de1fc684aa7c.png)

3. You can hover and click on any Required Quests to also view their progress: ![image](https://user-images.githubusercontent.com/123356351/219274115-3124149b-10e5-4b5d-a49d-609b429d900b.png)

4. Hovering over any [number] value lists a short description of that value: ![image](https://user-images.githubusercontent.com/123356351/219274156-5d0b2887-eaf5-48ac-b032-88a2545c99e4.png)

5. Click the [number] value to set the player's score to that value.
6. The number associated with their score is highlighted: ![image](https://user-images.githubusercontent.com/123356351/219274250-7eca8618-319c-40b9-b944-d5586c5522b3.png)


7. Several different quest completion values are supported. For example, "Bandit Troubles" has both a good and a bad ending, which both show a green **Complete** near the top: ![image](https://user-images.githubusercontent.com/123356351/218682643-ca8d53dd-4a4c-41ca-9b44-31d1305a5ed0.png)
8. And: ![image](https://user-images.githubusercontent.com/123356351/218682716-714dda54-0645-47e9-b892-113266cbfd70.png)
9. Values that are placeholder or have no usage are not shown: ![image](https://user-images.githubusercontent.com/123356351/219274363-395d8842-5793-4eee-a282-f16f83c6119d.png)

10. If there is no scoreboard objective, there will be a message: ![image](https://user-images.githubusercontent.com/123356351/219274395-ef649d9a-01c4-4042-8637-d12548a9f644.png)

11. Finally, if you didn't enter a valid Quest Name: ![image](https://user-images.githubusercontent.com/123356351/218683651-95b19c56-b6fe-49a9-af39-1eea64d7588d.png)

