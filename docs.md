### PLAYER HEAD SUPPORT

You can use custom player heads for both the join item and the menu items by using the PLAYER_HEAD material. 

1. Automatic Skin Detection (The player's own head)
If you set the material to PLAYER_HEAD and do not include a username line, the plugin will automatically fetch and display the skin of the player who joined or opened the menu.

Example:
material: PLAYER_HEAD

2. Specific Player Skin
If you want to display a fixed skin for everyone (like an Administrator, NPC, or famous creator), just add the username property below the material with the player's exact name.

Example:
material: basehead-eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTM4ZDllODY1ZTkwNWNhODc0NzhhODNhYzA2NjcwYjg0YjEyZTE4NzM4ZGEwMWMzZjg3M2EwYzc1MzUxMzk4MCJ9fX0=
