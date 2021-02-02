![FWDungeons Logo](https://repository-images.githubusercontent.com/268072687/382d7080-a919-11ea-9cc9-e7d4b3e39074)

[![](https://jitpack.io/v/ForgottenWorld/FWDungeons.svg)](https://jitpack.io/#ForgottenWorld/FWDungeons)

<b>FWDungeons</b> is a PaperMC plugin for creating multi-instance scriptable co-op dungeons.

## COMMANDS

<details>
    <summary>Click to expand</summary>

    /fwdungeonsedit (/fwde)
        dungeon
            create

Creates a new dungeon and returns its ID. You're now editing that dungeon, you may define a box for it, edit its
attributes, define triggers and active areas.

            edit DUNGEON_ID

Allows you to edit an existing dungeon by providing its ID. You currently can't add, remove or edit active areas or
triggers this way, but you can edit parameters such as name, description, starting location, difficulty, number of
players and points.

            name DUNGEON_NAME

Sets the name for the dungeon currently being edited or created.

            description DUNGEON_DESCRIPTION

Sets the description for the dungeon currently being edited or created.

            setstart

Sets the caller's current location as the starting location for the dungeon currently being edited or created.

            difficulty [easy, medium, hard]

Sets the chosen difficulty as the difficulty for the dungeon currently being edited or created.

            players MIN_PLAYERS MAX_PLAYERS

Sets the minimum and maximum number of players for the dungeon currently being edited or created.

            points AMOUNT

Sets the number of points for DungeonCompletedEvent. This event can be used for progression/leaderboard plugins such as
EasyRanking.

            pos1

Sets the block the player is looking at as the first location for defining the currently being created dungeon's box.
Once both positions are selected, the box is set.

            pos2

Sets the block the player is looking at as the first location for defining the currently being created dungeon's box.
Once both positions are selected, the box is set.

            hlframes

Toggles highlight mode, in which the boxes for triggers and active areas in the dungeon currently being created are
highlighted by particles: dripping lava for triggers and dripping water for active areas.

            writeout

Exports a configuration file for the dungeon currently being created and exits edit mode. This configuration file is
ready for trigger scripting.

            save

Updates the configuration file for the dungeon currently being edited and exits edit mode.

            discard

Exits edit mode. Changes to existing dungeons will remain until /fwd reload is called or the server is restared, but
they will not be written to disk. Instances created are an exception.

            instadd

Adds an instance (the box is defined as having origin at the block the player is looking at, the origin is the corner of
the box with the lowest X,Y and Z coordinates, the box will have height, width and depth equal to those of the dungeon)
for the dungeon currently being edited and writes it to DB.

            instremove

Removes an instance (the one the box of which the player is currently inside) for the dungeon currently being edited and
removes it from DB.

        trigger
            pos1

Sets the block the player is looking at as the first location for defining the a trigger in the currently being created
dungeon. Once both positions are selected, the box for the trigger is set.

            pos2

Sets the block the player is looking at as the second location for defining the a trigger in the currently being created
dungeon. Once both positions are selected, the box for the trigger is set.

            label

Sets a label for the last created trigger.

            unmake

Deletes the last created trigger.

        activearea
            pos1

Sets the block the player is looking at as the first location for defining an active area in the currently being created
dungeon. Once both positions are selected, the box for the active area is set.

            pos2

Sets the block the player is looking at as the second location for defining an active area in the currently being
created dungeon. Once both positions are selected, the box for the active area is set.

            label

Sets a label for the last created active area.

            unmake

Deletes the last created active area.

    /fwdungeons (/fwd)
        list
        join
        invite
        leave
        start
        lock
        unlock
        evacuate
        lookup
        enable
        disable
        reload

</details>
